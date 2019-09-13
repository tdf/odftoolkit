/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.pkg;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfDocument.OdfMediaType;
import static org.odftoolkit.odfdom.pkg.OdfPackageDocument.ROOT_DOCUMENT_PATH;
import org.odftoolkit.odfdom.pkg.manifest.AlgorithmElement;
import org.odftoolkit.odfdom.pkg.manifest.EncryptionDataElement;
import org.odftoolkit.odfdom.pkg.manifest.FileEntryElement;
import org.odftoolkit.odfdom.pkg.manifest.KeyDerivationElement;
import org.odftoolkit.odfdom.pkg.manifest.ManifestElement;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.pkg.manifest.OdfManifestDom;
import org.odftoolkit.odfdom.pkg.manifest.StartKeyGenerationElement;
import org.odftoolkit.odfdom.pkg.rdfa.Util;
import org.odftoolkit.odfdom.type.Base64Binary;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * OdfPackage represents the package view to an OpenDocument document. The
 * OdfPackage will be created from an ODF document and represents a copy of the
 * loaded document, where files can be inserted and deleted. The changes take
 * effect, when the OdfPackage is being made persisted by save().
 */
public class OdfPackage implements Closeable {

    // Static parts of file references
    private static final String DOUBLE_DOT = "..";
    private static final String DOT = ".";
    private static final String SLASH = "/";
    private static final String COLON = ":";
    private static final String ENCODED_APOSTROPHE = "&apos;";
    private static final String ENCODED_QUOTATION = "&quot;";
    private static final String EMPTY_STRING = "";
    private static final String XML_MEDIA_TYPE = "text/xml";
    // Search patterns to be used in RegEx expressions
    private static final Pattern BACK_SLASH_PATTERN = Pattern.compile("\\\\");
    private static final Pattern DOUBLE_SLASH_PATTERN = Pattern.compile("//");
    private static final Pattern QUOTATION_PATTERN = Pattern.compile("\"");
    private static final Pattern APOSTROPHE_PATTERN = Pattern.compile("'");
    private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("\\p{Cntrl}");
    private static final Set<String> COMPRESSED_FILETYPES;
    private static final byte[] HREF_PATTERN = {'x', 'l', 'i', 'n', 'k', ':', 'h', 'r', 'e', 'f', '=', '"'}; // xlink:href="

    // some well known streams inside ODF packages
    private String mMediaType;
    private String mBaseURI;
    private ZipHelper mZipFile;
    private Resolver mResolver;
    private Map<String, ZipEntry> mZipEntries;
    private HashMap<String, ZipEntry> mOriginalZipEntries;
    private Map<String, OdfFileEntry> mManifestEntries;
    // All opened documents from the same package are cached (including the root document)
    private Map<String, OdfPackageDocument> mPkgDocuments;
    // counter for ids that are not allowed to be saved (otherwise it is not guaranteed that this id is unique)
    private int mTransientMarkupId = 0;
    // Three different incarnations of a package file/data
    // save() will check 1) mPkgDoms, 2) if not check mMemoryFileCache
    private HashMap<String, Document> mPkgDoms;
    private HashMap<String, byte[]> mMemoryFileCache;
    private Map<String, Object> mConfiguration = new HashMap<String, Object>();

    private ErrorHandler mErrorHandler;
    private String mManifestVersion;
    private OdfManifestDom mManifestDom;
    private String mOldPwd;
    private String mNewPwd;

    /* Commonly used files within the ODF Package */
    public enum OdfFile {

        /**
         * The image directory is not defined by the OpenDocument standard,
         * nevertheless the most spread ODF application OpenOffice.org is using
         * the directory named "Pictures".
         */
        IMAGE_DIRECTORY("Pictures"),
        /**
         * The "META-INF/manifest.xml" file is defined by the ODF 1.2 part 3
         * Package specification. This manifest is the 'content table' of the
         * ODF package and describes the file entries of the ZIP including
         * directories, but should not contain empty directories.
         */
        MANIFEST("META-INF/manifest.xml"),
        /**
         * The "mime type" file is defined by the ODF 1.2 part 3 Package
         * specification. It contains the media type string of the root document
         * and must be the first file in the ZIP and must not be compressed.
         */
        MEDIA_TYPE("mimetype");
        private final String internalPath;

        OdfFile(String internalPath) {
            this.internalPath = internalPath;
        }

        public String getPath() {
            return internalPath;
        }
    }

    static {
        HashSet<String> compressedFileTypes = new HashSet<String>();
        String[] typelist = new String[]{"jpg", "gif", "png", "zip", "rar", "jpeg", "mpe", "mpg", "mpeg", "mpeg4", "mp4", "7z", "ari", "arj", "jar", "gz", "tar", "war", "mov", "avi"};
        compressedFileTypes.addAll(Arrays.asList(typelist));
        COMPRESSED_FILETYPES = Collections.unmodifiableSet(compressedFileTypes);
    }

    /**
     * Creates the ODFPackage as an empty Package.
     */
    private OdfPackage() {
        mMediaType = null;
        mResolver = null;
        mPkgDocuments = new HashMap<String, OdfPackageDocument>();
        mPkgDoms = new HashMap<String, Document>();
        mTransientMarkupId = 0;
        mMemoryFileCache = new HashMap<String, byte[]>();
        mManifestEntries = new HashMap<String, OdfFileEntry>();
        // specify whether validation should be enabled and what SAX
        // ErrorHandler should be used.
        if (mErrorHandler == null) {
            String errorHandlerProperty = System.getProperty("org.odftoolkit.odfdom.validation");
            if (errorHandlerProperty != null) {
                if (errorHandlerProperty.equalsIgnoreCase("true")) {
                    mErrorHandler = new DefaultErrorHandler();
                    Logger.getLogger(OdfPackage.class.getName()).config("Activated validation with default ErrorHandler!");
                } else if (!errorHandlerProperty.equalsIgnoreCase("false")) {
                    try {
                        Class<?> cl = Class.forName(errorHandlerProperty);
                        Constructor<?> ctor = cl.getDeclaredConstructor(new Class[]{});
                        mErrorHandler = (ErrorHandler) ctor.newInstance();
                        Logger.getLogger(OdfPackage.class.getName()).log(Level.CONFIG, "Activated validation with ErrorHandler:''{0}''!", errorHandlerProperty);
                    } catch (Exception ex) {
                        Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, "Could not initiate validation with the given ErrorHandler: '" + errorHandlerProperty + "'", ex);
                    }
                }
            }
        }
    }

    // is called if a low memory notification was received... then its tried to free as much memory as possible
    public void freeMemory() {
        mZipFile = null;
        mResolver = null;
        mZipEntries = null;
        mOriginalZipEntries = null;
        mManifestEntries = null;
        mPkgDocuments = null;
        mPkgDoms = null;
        mMemoryFileCache = null;
        mConfiguration = null;
        mErrorHandler = null;
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a File.
     *
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param pkgFile - a file representing the ODF document
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     */
    private OdfPackage(File pkgFile) throws SAXException, IOException {
        this(pkgFile, getBaseURLFromFile(pkgFile), null, null);
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a File.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param pkgFile - a file representing the ODF document
     * @param baseURI defining the base URI of ODF package.
     * @param password defining the password of ODF package.
     * @param errorHandler - SAX ErrorHandler used for ODF validation
     * @see #getErrorHandler
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     * @see #getErrorHandler*
     */
    private OdfPackage(File pkgFile, String baseURI, String password, ErrorHandler errorHandler) throws SAXException, IOException {
        this();
        mBaseURI = getBaseURLFromFile(pkgFile);
        mErrorHandler = errorHandler;
        mOldPwd = password;
        mNewPwd = mOldPwd;
        mBaseURI = baseURI;

        InputStream packageStream = new FileInputStream(pkgFile);
        try {
            initializeZip(packageStream);
        } finally {
            close(packageStream);
        }
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a InputStream.
     * <p>
     * Since an InputStream does not provide the arbitrary (non sequential) read
     * access needed by OdfPackage, the InputStream is cached. This usually
     * takes more time compared to the other constructors.
     * </p>
     *
     * @param packageStream - an inputStream representing the ODF package
     * @param baseURI defining the base URI of ODF package.
     * @param password defining the password of ODF package.
     * @param errorHandler - SAX ErrorHandler used for ODF validation
     * @see #getErrorHandler
     * @throws IOException if there's an I/O error while loading the package
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @see #getErrorHandler*
     */
    private OdfPackage(InputStream packageStream, String baseURI, String password, ErrorHandler errorHandler, Map<String, Object> configuration) throws SAXException, IOException {
        this(); // calling private constructor
        mErrorHandler = errorHandler;
        mBaseURI = baseURI;
        mOldPwd = password;
        mNewPwd = mOldPwd;
        mConfiguration = configuration;
        initializeZip(packageStream);
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a InputStream.
     *
     * <p>
     * Since an InputStream does not provide the arbitrary (non sequential) read
     * access needed by OdfPackage, the InputStream is cached. This usually
     * takes more time compared to the other constructors. </p>
     *
     * @param packageStream - an inputStream representing the ODF package
     * @param baseURI defining the base URI of ODF package.
     * @param errorHandler - SAX ErrorHandler used for ODF validation
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     *
     * @see #getErrorHandler
     */
    private OdfPackage(InputStream packageStream, String baseURI, ErrorHandler errorHandler)
            throws SAXException, IOException {
        this(); // calling private constructor
        if (errorHandler != null) {
            mErrorHandler = errorHandler;
        }
        mBaseURI = baseURI;
        initializeZip(packageStream);
    }

    /**
     * Loads an OdfPackage from the given documentURL.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param path - the documentURL to the ODF package
     * @return the OpenDocument document represented as an OdfPackage
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     */
    public static OdfPackage loadPackage(String path) throws SAXException, IOException {
        File pkgFile = new File(path);
        return new OdfPackage(pkgFile, getBaseURLFromFile(pkgFile), null, null);
    }

    /**
     * Loads an OdfPackage from the OpenDocument provided by a File.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param pkgFile - the ODF Package
     * @return the OpenDocument document represented as an OdfPackage
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     */
    public static OdfPackage loadPackage(File pkgFile) throws SAXException, IOException {
        return new OdfPackage(pkgFile, getBaseURLFromFile(pkgFile), null, null);
    }

    /**
     * Creates an OdfPackage from the given InputStream.
     * <p>
     * Since an InputStream does not provide the arbitrary (non sequential) read
     * access needed by OdfPackage, the InputStream is cached. This usually
     * takes more time compared to the other loadPackage methods.
     * </p>
     *
     * @param packageStream - an inputStream representing the ODF package
     * @return the OpenDocument document represented as an OdfPackage
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     */
    public static OdfPackage loadPackage(InputStream packageStream) throws SAXException, IOException {
        return new OdfPackage(packageStream, null, null, null, null);
    }

    /**
     * Creates an OdfPackage from the given InputStream.
     *
     * <p>
     * Since an InputStream does not provide the arbitrary (non sequential) read
     * access needed by OdfPackage, the InputStream is cached. This usually
     * takes more time compared to the other loadPackage methods.
     * </p>
     *
     * @param packageStream - an inputStream representing the ODF package
     * @param configuration - key/value pairs of user given run-time settings
     * (configuration) For instance, the maximum size of tables.
     * @return the OpenDocument document represented as an OdfPackage
     * @throws java.lang.Exception - if the package could not be loaded
     */
    public static OdfPackage loadPackage(InputStream packageStream, Map<String, Object> configuration)
            throws Exception {
        return new OdfPackage(packageStream, null, null, null, configuration);
    }

    /**
     * Creates an OdfPackage from the given InputStream.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param packageStream - an inputStream representing the ODF package
     * @param baseURI allows to explicitly set the base URI from the document,
     * As the URL can not be derived from a stream. In addition it is possible
     * to set the baseURI to any arbitrary URI, e.g. an URN. One usage of the
     * baseURI to describe the source of validation exception thrown by the
     * ErrorHandler.
     * @param errorHandler - SAX ErrorHandler used for ODF validation
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     * @see #getErrorHandler
     */
    public static OdfPackage loadPackage(InputStream packageStream, String baseURI, ErrorHandler errorHandler) throws SAXException, IOException {
        return new OdfPackage(packageStream, baseURI, null, errorHandler, null);
    }

    /**
     * Loads an OdfPackage from the given File.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param pkgFile - the ODF Package. A baseURL is being generated based on
     * its location.
     * @param errorHandler - SAX ErrorHandler used for ODF validation.
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     * @see #getErrorHandler
     */
    public static OdfPackage loadPackage(File pkgFile, ErrorHandler errorHandler) throws SAXException, IOException {
        return new OdfPackage(pkgFile, getBaseURLFromFile(pkgFile), null, errorHandler);
    }

    /**
     * Run-time configuration such as special logging or maximum table size to
     * create operations from are stored in this map.
     *
     * @return key/value pairs of user given run-time settings (configuration)
     */
    public Map<String, Object> getRunTimeConfiguration() {
        return mConfiguration;
    }

    /**
     * Loads an OdfPackage from the given File.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param pkgFile - the ODF Package. A baseURL is being generated based on
     * its location.
     * @param password - the ODF Package password.
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     * @see #getErrorHandler
     */
    public static OdfPackage loadPackage(File pkgFile, String password) throws SAXException, IOException {
        return OdfPackage.loadPackage(pkgFile, password, null);
    }

    /**
     * Loads an OdfPackage from the given File.
     * <p>
     * OdfPackage relies on the file being available for read access over the
     * whole life-cycle of OdfPackage.
     * </p>
     *
     * @param pkgFile - the ODF Package. A baseURL is being generated based on
     * its location.
     * @param password - the ODF Package password.
     * @param errorHandler - SAX ErrorHandler used for ODF validation.
     * @throws SAXException if there's an XML- or validation-related error while
     * loading the package
     * @throws IOException if there's an I/O error while loading the package
     * @see #getErrorHandler
     */
    public static OdfPackage loadPackage(File pkgFile, String password, ErrorHandler errorHandler) throws SAXException, IOException {
        return new OdfPackage(pkgFile, getBaseURLFromFile(pkgFile), password, errorHandler);
    }

    // Initialize using memory
    private void initializeZip(InputStream odfStream) throws SAXException, IOException {
        ByteArrayOutputStream tempBuf = new ByteArrayOutputStream();
        StreamHelper.transformStream(odfStream, tempBuf);
        byte[] mTempByteBuf = tempBuf.toByteArray();
        tempBuf.close();
        if (mTempByteBuf.length < 3) {
            OdfValidationException ve = new OdfValidationException(OdfPackageConstraint.PACKAGE_IS_NO_ZIP, getBaseURI());
            if (mErrorHandler != null) {
                mErrorHandler.fatalError(ve);
            }
            throw new IllegalArgumentException(ve);
        }
        mZipFile = new ZipHelper(this, mTempByteBuf);
        readZip();
    }

    // // Initialize using ZipFile
    // private void initializeZip(File pkgFile) throws Exception {
    // try {
    // mZipFile = new ZipHelper(this, new ZipFile(pkgFile));
    // } catch (ZipException ze) {
    // OdfValidationException ve = new
    // OdfValidationException(OdfPackageConstraint.PACKAGE_IS_NO_ZIP,
    // getBaseURI());
    // if (mErrorHandler != null) {
    // mErrorHandler.fatalError(ve);
    // }
    // throw new IllegalArgumentException(ve);
    // }
    // readZip();
    // }
    private void readZip() throws SAXException, IOException {
        mZipEntries = new HashMap<String, ZipEntry>();
        String firstEntryName = mZipFile.entriesToMap(mZipEntries);
        if (mZipEntries.isEmpty()) {
            OdfValidationException ve = new OdfValidationException(OdfPackageConstraint.PACKAGE_IS_NO_ZIP, getBaseURI());
            if (mErrorHandler != null) {
                mErrorHandler.fatalError(ve);
            }
            throw new IllegalArgumentException(ve);
        } else {
            // initialize the files of the package (fileEnties of Manifest)
            parseManifest();

            // initialize the package media type
            initializeMediaType(firstEntryName);

            // ToDo: Remove all META-INF/* files from the fileEntries of
            // Manifest
            mOriginalZipEntries = new HashMap<String, ZipEntry>();
            mOriginalZipEntries.putAll(mZipEntries);
            mZipEntries.remove(OdfPackage.OdfFile.MEDIA_TYPE.getPath());
            mZipEntries.remove(OdfPackage.OdfFile.MANIFEST.getPath());
            mZipEntries.remove("META-INF/");
            if (mErrorHandler != null) {
                validateManifest();
            }
            Iterator<String> zipPaths = mZipEntries.keySet().iterator();
            while (zipPaths.hasNext()) {
                String internalPath = zipPaths.next();
                // every resource aside the /META-INF/manifest.xml (and
                // META-INF/ directory)
                // and "mimetype" will be added as fileEntry
                if (!internalPath.equals(OdfPackage.OdfFile.MANIFEST.getPath()) && !internalPath.equals("META-INF/") && !internalPath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
                    // aside "mediatype" and "META-INF/manifest"
                    // add manifest entry as to be described by a
                    // <manifest:file-entry>
                    ensureFileEntryExistence(internalPath);
                }
            }
        }
    }

    /**
     * Validates if all file entries exist in the ZIP and vice versa
     *
     * @throws SAXException
     */
    private void validateManifest() throws SAXException {
        Set<String> zipPaths = mZipEntries.keySet();
        Set<String> manifestPaths = mManifestEntries.keySet();
        Set<String> sharedPaths = new HashSet<String>(zipPaths);
        sharedPaths.retainAll(manifestPaths);

        if (sharedPaths.size() < zipPaths.size()) {
            Set<String> zipPathSuperset = new HashSet<String>(mZipEntries.keySet());
            zipPathSuperset.removeAll(sharedPaths);
            Set<String> sortedSet = new TreeSet<String>(zipPathSuperset);
            Iterator<String> iter = sortedSet.iterator();
            String documentURL = getBaseURI();
            String internalPath;
            while (iter.hasNext()) {
                internalPath = (String) iter.next();
                if (!internalPath.endsWith(SLASH)
                        && // not for directories!
                        // The “META-INF/manifest.xml” file need not contain <manifest:file-entry> elements 4.3 whose manifest:full-path attribute 4.8.4 references files whose relative path start with "META-INF/".
                        !internalPath.startsWith("META-INF/")) {
                    logValidationError(OdfPackageConstraint.MANIFEST_DOES_NOT_LIST_FILE, documentURL, internalPath);
                }
            }
        }
        if (sharedPaths.size() < manifestPaths.size()) {
            Set<String> zipPathSubset = new HashSet<String>(mManifestEntries.keySet());
            zipPathSubset.removeAll(sharedPaths);
            // removing root directory
            zipPathSubset.remove(SLASH);

            // No directory are listed in a ZIP removing all directory with
            // content
            Iterator<String> manifestOnlyPaths = zipPathSubset.iterator();
            while (manifestOnlyPaths.hasNext()) {
                String manifestOnlyPath = manifestOnlyPaths.next();
                // assumption: all directories end with slash
                if (manifestOnlyPath.endsWith(SLASH)) {
                    removeDirectory(manifestOnlyPath);
                } else {
                    // if it is a nonexistent file
                    logValidationError(OdfPackageConstraint.MANIFEST_LISTS_NONEXISTENT_FILE, getBaseURI(), manifestOnlyPath);
                    // remove from the manifest Map
                    OdfFileEntry manifestEntry = mManifestEntries.remove(manifestOnlyPath);
                    // remove from the manifest DOM
                    FileEntryElement manifestEle = manifestEntry.getOdfElement();
                    manifestEle.getParentNode().removeChild(manifestEle);
                }
            }
        }
        // remove none document directories
        Iterator<String> sharedPathsIter = sharedPaths.iterator();
        while (sharedPathsIter.hasNext()) {
            String sharedPath = sharedPathsIter.next();
            // assumption: all directories end with slash
            if (sharedPath.endsWith(SLASH)) {
                removeDirectory(sharedPath);
            }
        }
    }

    /**
     * Removes directories without a mimetype (all none documents)
     *
     * @throws SAXException
     */
    private void removeDirectory(String path) throws SAXException {
        if (path.endsWith(SLASH)) {
            // Check if it is a sub-document?
            // Our assumption: it is a document if it has a mimetype...
            String dirMimeType = mManifestEntries.get(path).getMediaTypeString();
            if (dirMimeType == null || EMPTY_STRING.equals(dirMimeType)) {
                logValidationWarning(OdfPackageConstraint.MANIFEST_LISTS_DIRECTORY, getBaseURI(), path);
                // remove from the manifest Map
                OdfFileEntry manifestEntry = mManifestEntries.remove(path);
                // remove from the manifest DOM
                FileEntryElement manifestEle = manifestEntry.getOdfElement();
                manifestEle.getParentNode().removeChild(manifestEle);
            }
        }
    }

    /**
     * Reads the uncompressed "mimetype" file, which contains the package media
     * / mime type
     *
     * @throws SAXException
     */
    private void initializeMediaType(String firstEntryName) throws SAXException, IOException {
        ZipEntry mimetypeEntry = mZipEntries.get(OdfPackage.OdfFile.MEDIA_TYPE.getPath());
        if (mimetypeEntry != null) {
            if (mErrorHandler != null) {
                validateMimeTypeEntry(mimetypeEntry, firstEntryName);
            }
            // get mediatype value of the root document/package from the
            // mediatype file stream
            String entryMediaType = getMediaTypeFromEntry(mimetypeEntry);
            // get mediatype value of the root document/package from the
            // manifest.xml
            String manifestMediaType = getMediaTypeFromManifest();
            // if a valid mediatype was set by the "mimetype" file
            if (entryMediaType != null && !entryMediaType.equals(EMPTY_STRING)) {
                // the root document's mediatype is taken from the "mimetype"
                // file
                mMediaType = entryMediaType;
                if (mErrorHandler != null) {
                    // if the "mediatype" does exist, the
                    // "/META-INF/manifest.xml" have to contain a MIMETYPE for
                    // the root document);
                    if (manifestMediaType != null && !manifestMediaType.equals(EMPTY_STRING)) {
                        // if the two media-types are inconsistent
                        if (!entryMediaType.equals(manifestMediaType)) {
                            logValidationError(OdfPackageConstraint.MIMETYPE_DIFFERS_FROM_PACKAGE, getBaseURI(), CONTROL_CHAR_PATTERN.matcher(mMediaType).replaceAll(EMPTY_STRING), manifestMediaType);
                        }
                    } else { // if "mimetype" file exists, there have to be a
                        // mimetype in the manifest.xml for the root
                        // document (see ODF 1.2 part 3)
                        logValidationError(OdfPackageConstraint.MIMETYPE_WITHOUT_MANIFEST_MEDIATYPE, getBaseURI(), CONTROL_CHAR_PATTERN.matcher(mMediaType).replaceAll(EMPTY_STRING), manifestMediaType);
                    }
                }
            } else // if there is no media-type was set by the "mimetype" file
            // try as fall-back the mediatype of the root document from the
            // manifest.xml
            {
                if (manifestMediaType != null && !manifestMediaType.equals(EMPTY_STRING)) {
                    // and used as fall-back for the mediatype of the package
                    mMediaType = manifestMediaType;
                }
            }
        } else {
            String manifestMediaType = getMediaTypeFromManifest();
            if (manifestMediaType != null && !manifestMediaType.equals(EMPTY_STRING)) {
                // if not mimetype file exists, the root document mediaType from
                // the manifest.xml is taken
                mMediaType = manifestMediaType;
            }
            if (mErrorHandler != null) {
                logValidationWarning(OdfPackageConstraint.MIMETYPE_NOT_IN_PACKAGE, getBaseURI());
            }
        }
    }

    private void validateMimeTypeEntry(ZipEntry mimetypeEntry, String firstEntryName) throws SAXException {

        if (mimetypeEntry.getMethod() != ZipEntry.STORED) {
            logValidationError(OdfPackageConstraint.MIMETYPE_IS_COMPRESSED, getBaseURI());
        }
        if (mimetypeEntry.getExtra() != null) {
            logValidationError(OdfPackageConstraint.MIMETYPE_HAS_EXTRA_FIELD, getBaseURI());
        }
        if (!OdfFile.MEDIA_TYPE.getPath().equals(firstEntryName)) {
            logValidationError(OdfPackageConstraint.MIMETYPE_NOT_FIRST_IN_PACKAGE, getBaseURI());
        }
    }

    /**
     * @returns the media type of the root document from the manifest.xml
     */
    private String getMediaTypeFromManifest() {
        OdfFileEntry rootDocumentEntry = mManifestEntries.get(SLASH);
        if (rootDocumentEntry != null) {
            return rootDocumentEntry.getMediaTypeString();
        } else {
            return null;
        }
    }

    /**
     * @returns the media type of the root document from the manifest.xml
     */
    private String getMediaTypeFromEntry(ZipEntry mimetypeEntry) throws SAXException, IOException {
        String entryMediaType = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            StreamHelper.transformStream(mZipFile.getInputStream(mimetypeEntry), out);
            entryMediaType = new String(out.toByteArray(), 0, out.size(), "UTF-8");
        } catch (IOException ex) {
            Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
            handleIOException(ex, false);
        } finally {
            if (out != null) {
                try {
                    closeStream(out);
                } catch (IOException ex) {
                    Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
                }
                out = null;
            }
        }
        return entryMediaType;
    }

    private void closeStream(Closeable closeable) throws SAXException, IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ioe) {
                //Warning only. This is usually just logged.
                //Allow user to throw an exception all the same
                handleIOException(ioe, true);
            }
        }
    }

    private void handleIOException(IOException ex, boolean warningOnly) throws SAXException, IOException {
        if (mErrorHandler != null) {
            SAXParseException se = new SAXParseException(ex.getMessage(), null, ex);
            try {
                if (warningOnly) {
                    mErrorHandler.warning(se);
                } else {
                    mErrorHandler.error(se);
                }
            } catch (SAXException e1) {
                if (e1 == se) {
                    throw ex;
                    //We re-throw the original exception if the error handler
                    //just threw the SAXException we gave it.
                } else {
                    throw e1; //Throw what the error handler threw.
                }
            }
        }
        throw ex; //No error handler? Just throw the original IOException
    }

    /**
     * Insert an ODF document into the package at the given path. The path has
     * to be a directory and will receive the MIME type of the
     * OdfPackageDocument.
     *
     * @param doc the OdfPackageDocument to be inserted.
     * @param internalPath path relative to the package root, where the document
     * should be inserted.
     */
    void cacheDocument(OdfPackageDocument doc, String internalPath) {
        if (!internalPath.isEmpty()) {
            internalPath = normalizeDirectoryPath(internalPath);
            updateFileEntry(ensureFileEntryExistence(internalPath), doc.getMediaTypeString());
            mPkgDocuments.put(internalPath, doc);
        }
    }

    /**
     * Set the baseURI for this ODF package. NOTE: Should only be set during
     * saving the package.
     *
     * @param baseURI defining the location of the package
     */
    void setBaseURI(String baseURI) {
        mBaseURI = baseURI;
    }

    /**
     * @return The URI to the ODF package, usually the URL, where this ODF
     * package is located. If the package has not URI NULL is returned. This is
     * the case if the package was new created without an URI and not saved
     * before.
     */
    public String getBaseURI() {
        return mBaseURI;
    }

    /**
     * Returns on ODF documents based a given mediatype.
     *
     * @param internalPath path relative to the package root, where the document
     * should be loaded.
     * @return The ODF document, which mediatype depends on the parameter or
     * NULL if media type were not supported.
     */
    public OdfPackageDocument loadDocument(String internalPath) {
        OdfPackageDocument doc = getCachedDocument(internalPath);
        if (doc == null) {
            String mediaTypeString = getMediaTypeString();
            // ToDo: Issue 265 - Remove dependency to higher layer by factory
            OdfMediaType odfMediaType = OdfMediaType.getOdfMediaType(mediaTypeString);
            if (odfMediaType == null) {
                doc = new OdfPackageDocument(this, internalPath, mediaTypeString);
            } else {
                try {
                    String documentMediaType = getMediaTypeString(internalPath);
                    odfMediaType = OdfMediaType.getOdfMediaType(documentMediaType);
                    if (odfMediaType == null) {
                        return null;
                    }
                    // ToDo: Issue 265 - Remove dependency to higher layer by factory
                    doc = OdfDocument.loadDocument(this, internalPath);
                } catch (Exception ex) {
                    // ToDo: catching Exception, logging it and continuing is bad style.
                    //Refactor exception handling in higher layer, too. - ??
                    Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return doc;
    }

    /**
     * @deprecated This method is only added temporary as workaround for the IBM
     * fork using different DOC classes. Until the registering of DOC documents
     * to the PKG layer has been finished.
     * @param internalPath path relative to the package root, where the document
     * should be inserted.
     * @return an already open OdfPackageDocument via its path, otherwise NULL.
     */
    @Deprecated
    public OdfPackageDocument getCachedDocument(String internalPath) {
        internalPath = normalizeDirectoryPath(internalPath);
        return mPkgDocuments.get(internalPath);
    }

    /**
     * @param dom the DOM tree that has been parsed and should be added to the
     * cache.
     * @param internalPath path relative to the package root, where the XML of
     * the DOM is located.
     * @return an already open OdfPackageDocument via its path, otherwise NULL.
     */
    void cacheDom(Document dom, String internalPath) {
        internalPath = normalizeFilePath(internalPath);
        this.insert(dom, internalPath, null);
    }

    /**
     * @param internalPath path relative to the package root, where the document
     * should be inserted.
     * @return an already open W3C XML Document via its path, otherwise NULL.
     */
    Document getCachedDom(String internalPath) {
        internalPath = normalizeFilePath(internalPath);
        return this.mPkgDoms.get(internalPath);
    }

    /**
     * @return a map with all open W3C XML documents with their internal package
     * path as key.
     */
    Map<String, Document> getCachedDoms() {
        return this.mPkgDoms;
    }

    /**
     * Removes a document from the package via its path. Independent if it was
     * already opened or not.
     *
     * @param internalPath path relative to the package root, where the document
     * should be removed.
     */
    public void removeDocument(String internalPath) {
        // Note: the EMPTY String for root path will be exchanged to a SLASH
        internalPath = normalizeDirectoryPath(internalPath);
        // get all files of the package
        Set<String> allPackageFileNames = getFilePaths();

        // If the document is the root document
        // the "/" representing the root document is outside the
        // manifest.xml in the API an empty path
        // still normalizeDirectoryPath() already exchanged the EMPTY_STRING
        // to SLASH
        if (internalPath.equals(SLASH)) {
            for (String entryName : allPackageFileNames) {
                remove(entryName);
            }
            remove(SLASH);
        } else {
            // remove all the stream of the directory, such as pictures
            List<String> directoryEntryNames = new ArrayList<String>();
            for (String entryName : allPackageFileNames) {
                if (entryName.startsWith(internalPath)) {
                    directoryEntryNames.add(entryName);
                }
            }
            for (String entryName : directoryEntryNames) {
                remove(entryName);
            }
            remove(internalPath);
        }
    }

    /**
     * @return all currently opened OdfPackageDocument of this OdfPackage
     */
    Set<String> getCachedPackageDocuments() {
        return mPkgDocuments.keySet();
    }

    public OdfPackageDocument getRootDocument() {
        OdfPackageDocument odfPackageDocument = null;
        odfPackageDocument = mPkgDocuments.get(OdfPackageDocument.ROOT_DOCUMENT_PATH);
        if (odfPackageDocument == null) {
            odfPackageDocument = this.loadDocument(OdfPackageDocument.ROOT_DOCUMENT_PATH);
        }
        return odfPackageDocument;
    }

    public OdfManifestDom getManifestDom() {
        return mManifestDom;
    }

    /**
     * Get the media type of the ODF file or document (ie. a directory). A
     * directory with a mediatype can be loaded as
     * <code>OdfPackageDocument</code>. Note: A directoy is represented by in
     * the package as directory with media type
     *
     * @param internalPath within the package of the file or document.
     * @return the mediaType for the resource of the given path
     */
    public String getMediaTypeString(String internalPath) {
        String mediaType = null;
        if (internalPath != null) {
            if (internalPath.equals(EMPTY_STRING) || internalPath.equals(SLASH)) {
                return mMediaType;
            } else {
                mediaType = getMediaTypeFromEntry(normalizePath(internalPath));
                // if no file was found, look for a normalized directory name
                if (mediaType == null) {
                    mediaType = getMediaTypeFromEntry(normalizeDirectoryPath(internalPath));
                }
            }
        }
        return mediaType;
    }

    private String getMediaTypeFromEntry(String internalPath) {
        OdfFileEntry entry = getFileEntry(internalPath);
        // if the document is not in the package, the return is NULL
        if (entry != null) {
            return entry.getMediaTypeString();
        } else {
            return null;
        }
    }

    /**
     * Get the media type of the ODF package (equal to media type of ODF root
     * document)
     *
     * @return the mediaType string of this ODF package
     */
    public String getMediaTypeString() {
        return mMediaType;
    }

    /**
     * Set the media type of the ODF package (equal to media type of ODF root
     * document)
     *
     * @param mediaType string of this ODF package
     */
    void setMediaTypeString(String mediaType) {
        mMediaType = mediaType;
    }

    /**
     * Get an OdfFileEntry for the internalPath NOTE: This method should be
     * better moved to a DOM inherited Manifest class
     *
     * @param internalPath The relative package path within the ODF package
     * @return The manifest file entry will be returned.
     */
    public OdfFileEntry getFileEntry(String internalPath) {
        internalPath = normalizeFilePath(internalPath);
        return mManifestEntries.get(internalPath);
    }

    /**
     * Get a OdfFileEntries from the manifest file (i.e. /META/manifest.xml")
     *
     * @return The paths of the manifest file entries will be returned.
     */
    public Set<String> getFilePaths() {
        return mManifestEntries.keySet();
    }

    /**
     * Check existence of a file in the package.
     *
     * @param internalPath The relative package documentURL within the ODF
     * package
     * @return True if there is an entry and a file for the given documentURL
     */
    public boolean contains(String internalPath) {
        internalPath = normalizeFilePath(internalPath);
        return mManifestEntries.containsKey(internalPath);
    }

    /**
     * Save the package to given documentURL.
     *
     * @param odfPath - the path to the ODF package destination
     * @throws java.io.IOException - if the package could not be saved
     */
    public void save(String odfPath) throws SAXException, IOException {
        File f = new File(odfPath);
        save(f);
    }

    /**
     * Save package to a given File. After saving it is still necessary to close
     * the package to have again full access about the file.
     *
     * @param pkgFile - the File to save the ODF package to
     * @throws java.io.IOException - if the package could not be saved
     */
    public void save(File pkgFile) throws SAXException, IOException {
        String baseURL = getBaseURLFromFile(pkgFile);
        // if (baseURL.equals(mBaseURI)) {
        // // save to the same file: cache everything first
        // // ToDo: (Issue 219 - PackageRefactoring) --maybe it's better to
        // write to a new file and copy that
        // // to the original one - would be less memory footprint
        // cacheContent();
        // }
        FileOutputStream fos = new FileOutputStream(pkgFile);
        try {
            save(fos, baseURL);
        } finally {
            fos.close();
        }
    }

    /**
     * Saves the package to a given {@link OutputStream}. The given stream is
     * not closed by this method.
     *
     * @param odfStream the output stream
     * @throws IOException if an I/O error occurs while saving the package
     * @throws SAXException
     */
    public void save(OutputStream odfStream) throws SAXException, IOException {
        save(odfStream, null);
    }

    /**
     * Sets the password of this package. if password is not null, package will
     * be encrypted when save.
     *
     * @param password password
     * @since 0.8.9
     */
    public void setPassword(String password) {
        mNewPwd = password;
    }

    /**
     * Save an ODF document to the OutputStream.
     *
     * @param odfStream - the OutputStream to insert content to
     * @param baseURL defining the location of the package
     * @throws java.io.IOException if an I/O error occurs while saving the
     * package
     */
    private void save(OutputStream odfStream, String baseURL) throws IOException, SAXException {
        mBaseURI = baseURL;
        OdfFileEntry rootEntry = mManifestEntries.get(SLASH);
        if (rootEntry == null) {
            rootEntry = new OdfFileEntry(getManifestDom().getRootElement().newFileEntryElement(SLASH, mMediaType));
            mManifestEntries.put(SLASH, rootEntry);
        } else {
            rootEntry.setMediaTypeString(mMediaType);
        }
        ZipOutputStream zos = new ZipOutputStream(odfStream);
        try {
            // remove mediatype path and use it as first
            this.mManifestEntries.remove(OdfFile.MEDIA_TYPE.getPath());
            Set<String> keys = mManifestEntries.keySet();
            boolean isFirstFile = true;
            CRC32 crc = new CRC32();
            long modTime = (new java.util.Date()).getTime();
            byte[] data = null;
            for (String path : keys) {
                // ODF requires the "mimetype" file to be at first in the package
                if (isFirstFile) {
                    isFirstFile = false;
                    // create "mimetype" from current attribute value
                    data = mMediaType.getBytes("UTF-8");
                    createZipEntry(OdfFile.MEDIA_TYPE.getPath(), data, zos, modTime, crc);

                }
                // create an entry, but NOT for "ODF document directory", "MANIFEST" or "mimetype"
                if (!path.endsWith(SLASH) && !path.equals(OdfPackage.OdfFile.MANIFEST.getPath())
                        && !path.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
                    data = getBytes(path);
                    createZipEntry(path, data, zos, modTime, crc);
                }
                data = null;
            }
            // Create "META-INF/" directory
            createZipEntry("META-INF/", null, zos, modTime, crc);
            // Create "META-INF/manifest.xml" file after all entries with potential encryption have been added
            data = getBytes(OdfFile.MANIFEST.getPath());
            createZipEntry(OdfFile.MANIFEST.getPath(), data, zos, modTime, crc);
        } finally {
            zos.flush();
            zos.close();
        }
        odfStream.flush();
    }

    private void createZipEntry(String path, byte[] data, ZipOutputStream zos, long modTime, CRC32 crc) throws IOException {
        ZipEntry ze = null;
        ze = mZipEntries.get(path);
        if (ze == null) {
            ze = new ZipEntry(path);
        }
        ze.setTime(modTime);
        if (fileNeedsCompression(path)) {
            ze.setMethod(ZipEntry.DEFLATED);
        } else {
            ze.setMethod(ZipEntry.STORED);
        }
        crc.reset();
        if (data != null) {
            OdfFileEntry fileEntry = mManifestEntries.get(path);
            // encrypt file
            if (data.length > 0 && fileNeedsEncryption(path)) {
                data = encryptData(data, fileEntry);
                // encrypted file entries shall be flagged as 'STORED'.
                ze.setMethod(ZipEntry.STORED);
                // the size of the encrypted file should replace the real
                // size value.
                ze.setCompressedSize(data.length);
            } else {
                if (fileEntry != null) {
                    fileEntry.setSize(null);
                    FileEntryElement fileEntryEle = fileEntry.getOdfElement();
                    EncryptionDataElement encryptionDataElement = OdfElement.findFirstChildNode(EncryptionDataElement.class, fileEntryEle);
                    while (encryptionDataElement != null) {
                        fileEntryEle.removeChild(encryptionDataElement);
                        encryptionDataElement = OdfElement.findFirstChildNode(EncryptionDataElement.class, fileEntryEle);
                    }
                }
                ze.setCompressedSize(-1);
            }
            ze.setSize(data.length);
            crc.update(data);
            ze.setCrc(crc.getValue());
        } else {
            ze.setSize(0);
            ze.setCrc(0);
            ze.setCompressedSize(-1);
        }
        zos.putNextEntry(ze);
        if (data != null) {
            zos.write(data, 0, data.length);
        }
        zos.closeEntry();
        mZipEntries.put(path, ze);
    }

    /**
     * Determines if a file have to be compressed.
     *
     * @param internalPath the file location
     * @return true if the file needs compression, false, otherwise
     */
    private boolean fileNeedsCompression(String internalPath) {
        boolean result = true;

        // ODF spec does not allow compression of "./mimetype" file
        if (internalPath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
            return false;
        }
        // see if the file was already compressed
        if (internalPath.lastIndexOf(".") > 0) {
            String suffix = internalPath.substring(internalPath.lastIndexOf(".") + 1, internalPath.length());
            if (COMPRESSED_FILETYPES.contains(suffix.toLowerCase())) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Determines if a file have to be encrypted.
     *
     * @param internalPath the file location
     * @return true if the file needs encrypted, false, otherwise
     */
    private boolean fileNeedsEncryption(String internalPath) {
        if (mNewPwd != null) {
            // ODF spec does not allow encrytion of "./mimetype" file
            if (internalPath.endsWith(SLASH) || OdfFile.MANIFEST.getPath().equals(internalPath) || OdfPackage.OdfFile.MEDIA_TYPE.getPath().equals(internalPath)) {
                return false;
            }
            return fileNeedsCompression(internalPath);
        } else {
            return false;
        }
    }

    private void close(Closeable closeable) throws SAXException, IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ioe) {
                // Warning only. This is usually just logged.
                // Allow user to throw an exception all the same
                handleIOException(ioe, true);
            }
        }
    }

    /**
     * Close the OdfPackage after it is no longer needed. Even after saving it
     * is still necessary to close the package to have again full access about
     * the file. Closing the OdfPackage will release all temporary created data.
     * Do this as the last action to free resources. Closing an already closed
     * document has no effect.
     */
    public void close() {
        if (mZipFile != null) {
            try {
                mZipFile.close();
            } catch (IOException ex) {
                // log exception and continue
                Logger.getLogger(OdfPackage.class.getName()).log(Level.INFO, null, ex);
            }
        }
        // release all stuff - this class is impossible to use afterwards
        mZipFile = null;
        mMediaType = null;
        mZipEntries = null;
        mPkgDoms = null;
        mMemoryFileCache = null;
        mManifestEntries = null;
        mBaseURI = null;
        mResolver = null;
    }

    /**
     * Parse the Manifest file
     */
    private void parseManifest() throws SAXException, IOException {
        mManifestDom = (OdfManifestDom) OdfFileDom.newFileDom(this, OdfFile.MANIFEST.getPath());
        ManifestElement manifestEle = mManifestDom.getRootElement();
        if (manifestEle != null) {
            setManifestVersion(manifestEle.getVersionAttribute());
        } else {
            logValidationError(OdfPackageConstraint.MANIFEST_NOT_IN_PACKAGE, getBaseURI());
        }
        Map<String, OdfFileEntry> entries = getManifestEntries();
        FileEntryElement fileEntryEle = OdfElement.findFirstChildNode(FileEntryElement.class, manifestEle);
        while (fileEntryEle != null) {
            String path = fileEntryEle.getFullPathAttribute();
            if (path.equals(EMPTY_STRING)) {
                if (getErrorHandler() != null) {
                    logValidationError(OdfPackageConstraint.MANIFEST_WITH_EMPTY_PATH, getBaseURI());
                }
            } else {
                path = normalizePath(path);
                OdfFileEntry currentFileEntry = entries.get(path);
                if (currentFileEntry == null) {
                    currentFileEntry = new OdfFileEntry(fileEntryEle);
                }
                if (path != null) {
                    entries.put(path, currentFileEntry);
                }
            }
            fileEntryEle = OdfElement.findNextChildNode(FileEntryElement.class, fileEntryEle);
        }
        mMemoryFileCache.remove(OdfFile.MANIFEST.getPath());
        mPkgDoms.put(OdfFile.MANIFEST.getPath(), mManifestDom);
    }

    XMLReader getXMLReader() throws ParserConfigurationException, SAXException {
        // create sax parser
        SAXParserFactory saxFactory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
        saxFactory.setNamespaceAware(true);
        saxFactory.setValidating(false);
        try {
            saxFactory.setXIncludeAware(false);
            saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // removing potential vulnerability: see https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
            saxFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            saxFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            saxFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (Exception ex) {
            Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException();
        }

        SAXParser parser;
        try {
            parser = saxFactory.newSAXParser();
        } catch (ParserConfigurationException pce) {
            //Re-throw as SAXException in order not to introduce too many checked exceptions
            throw new SAXException(pce);
        }
        XMLReader xmlReader = parser.getXMLReader();
        // More details at http://xerces.apache.org/xerces2-j/features.html#namespaces
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        // More details at http://xerces.apache.org/xerces2-j/features.html#namespace-prefixes
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        // More details at http://xerces.apache.org/xerces2-j/features.html#xmlns-uris
        xmlReader.setFeature("http://xml.org/sax/features/xmlns-uris", true);
        // removing potential vulnerability: see https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return xmlReader;

    }

    // Add the given path and all its subdirectories to the internalPath list
    // to be written later to the manifest
    private void createSubEntries(String internalPath) {
        ManifestElement manifestEle = getManifestDom().getRootElement();
        StringTokenizer tok = new StringTokenizer(internalPath, SLASH);
        if (tok.countTokens() > 1) {
            String path = EMPTY_STRING;
            while (tok.hasMoreTokens()) {
                String directory = tok.nextToken();
                // it is a directory, if there are more token
                if (tok.hasMoreTokens()) {
                    path = path + directory + SLASH;
                    OdfFileEntry fileEntry = mManifestEntries.get(path);
                    // ??? no subdirectory without mimetype in the specification allowed
                    if (fileEntry == null) {
                        mManifestEntries.put(path, new OdfFileEntry(manifestEle.newFileEntryElement(path, null)));
                    }
                }
            }
        }
    }

    /**
     * Insert DOM tree into OdfPackage. An existing file will be replaced.
     *
     * @param fileDOM - XML DOM tree to be inserted as file.
     * @param internalPath - relative documentURL where the DOM tree should be
     * inserted as XML file
     * @param mediaType - media type of stream. Set to null if unknown
     * @throws java.lang.Exception when the DOM tree could not be inserted
     */
    public void insert(Document fileDOM, String internalPath, String mediaType) {
        internalPath = normalizeFilePath(internalPath);
        if (mediaType == null) {
            mediaType = XML_MEDIA_TYPE;
        }
        if (fileDOM == null) {
            mPkgDoms.remove(internalPath);
        } else {
            mPkgDoms.put(internalPath, fileDOM);
        }
        if (!internalPath.endsWith(OdfFile.MANIFEST.internalPath)) {
            updateFileEntry(ensureFileEntryExistence(internalPath), mediaType);
        }
        // remove byte array version of new DOM
        mMemoryFileCache.remove(internalPath);
    }

    /**
     * Embed an OdfPackageDocument to the current OdfPackage. All the file
     * entries of child document will be inserted.
     *
     * @param sourceDocument the OdfPackageDocument to be embedded.
     * @param destinationPath path to the directory the ODF document should be
     * inserted (relative to ODF package root).
     */
    public void insertDocument(OdfPackageDocument sourceDocument, String destinationPath) {
        destinationPath = normalizeDirectoryPath(destinationPath);
        // opened DOM of descendant Documents will be flashed to the their pkg
        flushDoms(sourceDocument);
        // Gets the OdfDocument's manifest entry info, no matter it is a
        // independent document or an embeddedDocument.
        Map<String, OdfFileEntry> manifestEntriesToCopy;
        String sourceSubPath = null;
        if (sourceDocument.isRootDocument()) {
            manifestEntriesToCopy = sourceDocument.getPackage().getManifestEntries();
            sourceSubPath = ROOT_DOCUMENT_PATH;
        } else {
            manifestEntriesToCopy = sourceDocument.getPackage().getSubDirectoryEntries(sourceDocument.getDocumentPath());
            sourceSubPath = sourceDocument.getDocumentPath();
        }
        addEntriesToPackageAndManifest(manifestEntriesToCopy, sourceDocument, sourceSubPath, destinationPath);

        if (!mManifestEntries.containsKey(destinationPath)) {
            ManifestElement manifestEle = mManifestDom.getRootElement();
            // make sure the media type of embedded Document is right set.
            OdfFileEntry embedDocumentRootEntry = new OdfFileEntry(manifestEle.newFileEntryElement(destinationPath, sourceDocument.getMediaTypeString()));
            mManifestEntries.put(destinationPath, embedDocumentRootEntry);
        }
        // the new document will be attached to its new package (it has been
        // inserted to)
        sourceDocument.setPackage(this);
        cacheDocument(sourceDocument, destinationPath);
    }

    private void addEntriesToPackageAndManifest(Map<String, OdfFileEntry> entryMapToCopy, OdfPackageDocument sourceDocument, String subDocumentPath, String destinationPath) {
        // insert to package and add it to the Manifest
        destinationPath = sourceDocument.setDocumentPath(destinationPath);

        Set<String> entryNameList = entryMapToCopy.keySet();
        for (String entryName : entryNameList) {
            OdfFileEntry entry = entryMapToCopy.get(entryName);
            if (entry != null) {
                try {
                    if (!subDocumentPath.equals(ROOT_DOCUMENT_PATH)) {
                        entryName = entryName.substring(subDocumentPath.length());
                        if (entryName.length() == 0) {
                            entryName = SLASH;
                        }
                    }
                    // if entry is a directory (e.g. an ODF document root)
                    if (entryName.endsWith(SLASH)) {
                        // insert directory
                        if (entryName.equals(SLASH)) {
                            insert((byte[]) null, destinationPath, sourceDocument.getMediaTypeString());
                        } else {
                            String mediaType = sourceDocument.getMediaTypeString();
                            if (mediaType != null && mediaType.length() != 0) {
                                if (!destinationPath.equals(SLASH)) {
                                    entryName = destinationPath + entryName;
                                }
                                insert((byte[]) null, entryName, entry.getMediaTypeString());
                            }
                        }
                    } else {
                        String documentDirectory = null;
                        if (destinationPath.equals(SLASH)) {
                            documentDirectory = EMPTY_STRING;
                        } else {
                            documentDirectory = destinationPath;
                        }
                        String packagePath = documentDirectory + entryName;
                        insert(sourceDocument.getPackage().getInputStream(entry.getPath()), packagePath, entry.getMediaTypeString());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Insert all open DOMs of XML files beyond parent document to the package.
     * The XML files will be updated in the package after calling save.
     *
     * @param parentDocument the document, which XML files shall be serialized
     */
    void flushDoms(OdfPackageDocument parentDocument) {
        OdfPackage pkg = parentDocument.getPackage();
        if (parentDocument.isRootDocument()) {
            // for every parsed XML file (DOM)
            for (String xmlFilePath : pkg.getCachedDoms().keySet()) {
                // insert it to the package (serializing and caching it till
                // final save)
                pkg.insert(pkg.getCachedDom(xmlFilePath), xmlFilePath, "text/xml");
            }
        } else {
            // if not root document, check ..
            String parentDocumentPath = parentDocument.getDocumentPath();
            // for every parsed XML file (DOM)
            for (String xmlFilePath : pkg.getCachedDoms().keySet()) {
                // if the file is within the given document
                if (xmlFilePath.startsWith(parentDocumentPath)) {
                    // insert it to the package (serializing and caching it till
                    // final save)
                    pkg.insert(pkg.getCachedDom(xmlFilePath), xmlFilePath, "text/xml");
                }
            }
        }
    }

    /**
     * Get all the file entries from a sub directory
     */
    private Map<String, OdfFileEntry> getSubDirectoryEntries(String directory) {
        directory = normalizeDirectoryPath(directory);
        Map<String, OdfFileEntry> subEntries = new HashMap<String, OdfFileEntry>();
        Map<String, OdfFileEntry> allEntries = getManifestEntries();
        Set<String> rootEntryNameSet = getFilePaths();
        for (String entryName : rootEntryNameSet) {
            if (entryName.startsWith(directory)) {
                subEntries.put(entryName, allEntries.get(entryName));
            }
        }
        return subEntries;
    }

    /**
     * Method returns the paths of all document within the package.
     *
     * @return A set of paths of all documents of the package, including the
     * root document.
     */
    public Set<String> getDocumentPaths() {
        return getDocumentPaths(null, null);
    }

    /**
     * Method returns the paths of all document within the package matching the
     * given criteria.
     *
     * @param mediaTypeString limits the desired set of document paths to
     * documents of the given mediaType
     * @return A set of paths of all documents of the package, including the
     * root document, that match the given parameter.
     */
    public Set<String> getDocumentPaths(String mediaTypeString) {
        return getDocumentPaths(mediaTypeString, null);
    }

    /**
     * Method returns the paths of all document within the package matching the
     * given criteria.
     *
     * @param mediaTypeString limits the desired set of document paths to
     * documents of the given mediaType
     * @param subDirectory limits the desired set document paths to those
     * documents below of this subdirectory
     * @return A set of paths of all documents of the package, including the
     * root document, that match the given parameter.
     */
    Set<String> getDocumentPaths(String mediaTypeString, String subDirectory) {
        Set<String> innerDocuments = new HashSet<String>();
        Set<String> packageFilePaths = getFilePaths();
        // check manifest for current embedded OdfPackageDocuments
        for (String filePath : packageFilePaths) {
            // check if a subdirectory was the criteria and if the files are
            // beyond the given subdirectory
            if (subDirectory == null || filePath.startsWith(subDirectory) && !filePath.equals(subDirectory)) {
                // with documentURL is not empty and is a directory (ie. a
                // potential document)
                if (filePath.length() > 1 && filePath.endsWith(SLASH)) {
                    String fileMediaType = getFileEntry(filePath).getMediaTypeString();
                    if (fileMediaType != null && !fileMediaType.equals(EMPTY_STRING)) {
                        // check if a certain mediaType was the critera and was
                        // matched
                        if (mediaTypeString == null || mediaTypeString.equals(fileMediaType)) {
                            // only relative path is allowed as path
                            innerDocuments.add(filePath);
                        }
                    }
                }
            }
        }
        return innerDocuments;
    }

    /**
     * Adding a manifest:file-entry to be saved in manifest.xml. In addition,
     * sub directories will be added as well to the manifest.
     */
    private OdfFileEntry ensureFileEntryExistence(String internalPath) {
        // if it is NOT the resource "/META-INF/manifest.xml"
        OdfFileEntry fileEntry = null;
        if (!OdfFile.MANIFEST.internalPath.equals(internalPath) && !internalPath.equals(EMPTY_STRING)) {
            if (mManifestEntries == null) {
                mManifestEntries = new HashMap<String, OdfFileEntry>();
            }
            fileEntry = mManifestEntries.get(internalPath);
            // for every new file entry
            if (fileEntry == null) {
                ManifestElement manifestEle = getManifestDom().getRootElement();
                if (manifestEle == null) {
                    return null;
                }
                fileEntry = new OdfFileEntry(manifestEle.newFileEntryElement(internalPath, ""));
                mManifestEntries.put(internalPath, fileEntry);
                // creates recursive file entries for all sub directories
                // BUT ONLY SUBDIRECTORYS WITH MIMETYPE (documents) ARE ALLOWED IN THE MANIFEST
                // createSubEntries(internalPath);
            }
        }
        return fileEntry;
    }

    /**
     * update file entry setting.
     */
    private void updateFileEntry(OdfFileEntry fileEntry, String mediaType) {
        // overwrite previous settings
        fileEntry.setMediaTypeString(mediaType);
        // reset encryption data (ODFDOM does not support encryption yet)
//		fileEntry.setEncryptionData(null);
        // reset size to be unset
        fileEntry.setSize(null);
    }

    /**
     * Gets org.w3c.dom.Document for XML file contained in package.
     *
     * @param internalPath to a file within the Odf Package (eg. content.xml)
     * @return an org.w3c.dom.Document
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public Document getDom(String internalPath) throws SAXException, ParserConfigurationException, IllegalArgumentException, TransformerConfigurationException, TransformerException, IOException {

        Document doc = mPkgDoms.get(internalPath);
        if (doc != null) {
            return doc;
        }

        InputStream is = getInputStream(internalPath);

        // We depend on Xerces. So we just go ahead and create a Xerces DBF,
        // without
        // forcing everything else to do so.
        DocumentBuilderFactory factory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
		try {
			factory.setXIncludeAware(false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // removing potential vulnerability: see https://www.owasp.org/index.php/XML_External_Entity_%28XXE%29_Processing
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		} catch (Exception ex) {
			Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException();
		}

        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(getEntityResolver());

        String uri = getBaseURI() + internalPath;

        if (mErrorHandler != null) {
            builder.setErrorHandler(mErrorHandler);
        }

        InputSource ins = new InputSource(is);
        ins.setSystemId(uri);

        doc = builder.parse(ins);

        if (doc != null) {
            mPkgDoms.put(internalPath, doc);
            mMemoryFileCache.remove(internalPath);
        }
        return doc;
    }

    /**
     * Inserts an external file into an OdfPackage. An existing file will be
     * replaced.
     *
     * @param sourceURI - the source URI to the file to be inserted into the
     * package.
     * @param internalPath - relative documentURL where the tree should be
     * inserted as XML file
     * @param mediaType - media type of stream. Set to null if unknown
     * @throws java.lang.Exception In case the file could not be saved
     */
    public void insert(URI sourceURI, String internalPath, String mediaType) throws Exception {
        InputStream is = null;
        if (sourceURI.isAbsolute()) {
            // if the URI is absolute it can be converted to URL
            is = sourceURI.toURL().openStream();
        } else {
            // otherwise create a file class to open the stream
            is = new FileInputStream(sourceURI.toString());
        }
        insert(is, internalPath, mediaType);
    }

    /**
     * Inserts InputStream into an OdfPackage. An existing file will be
     * replaced.
     *
     * @param fileStream - the stream of the file to be inserted into the ODF
     * package.
     * @param internalPath - relative documentURL where the tree should be
     * inserted as XML file
     * @param mediaType - media type of stream. Set to null if unknown
     */
    public void insert(InputStream fileStream, String internalPath, String mediaType) throws IOException  {
        internalPath = normalizeFilePath(internalPath);
        if (fileStream == null) {
            // adding a simple directory without MIMETYPE
            insert((byte[]) null, internalPath, mediaType);
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedInputStream bis = null;
            if (fileStream instanceof BufferedInputStream) {
                bis = (BufferedInputStream) fileStream;
            } else {
                bis = new BufferedInputStream(fileStream);
            }
            StreamHelper.transformStream(bis, baos);
            byte[] data = baos.toByteArray();
            insert(data, internalPath, mediaType);
        }
    }

    /**
     * Inserts a byte array into OdfPackage. An existing file will be replaced.
     * If the byte array is NULL a directory with the given mimetype will be
     * created.
     *
     * @param fileBytes - data of the file stream to be stored in package. If
     * NULL a directory with the given mimetype will be created.
     * @param internalPath - path of the file or directory relative to the
     * package root.
     * @param mediaTypeString - media type of stream. If unknown null can be
     * used.
     */
    public void insert(byte[] fileBytes, String internalPath, String mediaTypeString) {
        internalPath = normalizeFilePath(internalPath);
        // if path is from the mimetype, which should be first in document
        if (OdfPackage.OdfFile.MEDIA_TYPE.getPath().equals(internalPath)) {
            try {
                setMediaTypeString(new String(fileBytes, "UTF-8"));
            } catch (UnsupportedEncodingException useEx) {
                Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, "ODF file could not be created as string!", useEx);
            }
            return;
        }
        if (fileBytes != null) {
            mMemoryFileCache.put(internalPath, fileBytes);
            // as DOM would overwrite data cache, any existing DOM cache will be
            // deleted
            if (mPkgDoms.containsKey(internalPath)) {
                mPkgDoms.remove(internalPath);
            }
        }
        updateFileEntry(ensureFileEntryExistence(internalPath), mediaTypeString);
    }

    // changed to package access as the manifest interiors are an implementation
    // detail
    Map<String, OdfFileEntry> getManifestEntries() {
        return mManifestEntries;
    }

    /**
     * Get package (sub-) content as byte array
     *
     * @param internalPath relative documentURL to the package content
     * @return the unzipped package content as byte array
     * @throws java.lang.Exception
     */
    public byte[] getBytes(String internalPath) {
        // if path is null or empty return null
        if (internalPath == null || internalPath.equals(EMPTY_STRING)) {
            return null;
        }
        internalPath = normalizeFilePath(internalPath);
        byte[] data = null;
        // if the file is "mimetype"
        if (internalPath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
            if (mMediaType == null) {
                return null;
            } else {
                try {
                    data = mMediaType.getBytes("UTF-8");
                } catch (UnsupportedEncodingException use) {
                    Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, use);
                    return null;
                }
            }
        } else if (mPkgDoms.get(internalPath) != null) {
            data = flushDom(mPkgDoms.get(internalPath));
            mMemoryFileCache.put(internalPath, data);
            // if the path's file was cached to memory (second high priority)
        } else if (mManifestEntries.containsKey(internalPath) && mMemoryFileCache.get(internalPath) != null) {
            data = mMemoryFileCache.get(internalPath);

            // if the path's file was cached to disc (lowest priority)
        }
        // if not available, check if file exists in ZIP
        if (data == null) {
            ZipEntry entry = null;
            if ((entry = mZipEntries.get(internalPath)) != null) {
                InputStream inputStream = null;
                try {
                    inputStream = mZipFile.getInputStream(entry);
                    if (inputStream != null) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        StreamHelper.transformStream(inputStream, out);
                        data = out.toByteArray();
                        // decrypt data as needed
                        if (!(internalPath.equals(OdfFile.MEDIA_TYPE.getPath()) || internalPath.equals(OdfFile.MANIFEST.getPath()))) {
                            OdfFileEntry manifestEntry = getManifestEntries().get(internalPath);
                            EncryptionDataElement encryptionDataElement = manifestEntry.getEncryptionData();
                            if (encryptionDataElement != null) {
                                byte[] newData = decryptData(data, manifestEntry, encryptionDataElement);
                                if (newData != null) {
                                    data = newData;
                                } else {
                                    Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, "Wrong password being used for decryption!");
                                }
                            }
                        }
                        // store for further usage; do not care about manifest:
                        // that is handled exclusively
                        mMemoryFileCache.put(internalPath, data);
                    }
                } catch (IOException ex) {
                    //Catching IOException here should be fine: in-memory operations only
                    Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return data;
    }

    // encrypt data and update manifest.
    private byte[] encryptData(byte[] data, OdfFileEntry fileEntry) {
        byte[] encryptedData = null;
        try {
            // 1.The original uncompressed, unencrypted size is
            // contained in the manifest:size.
            fileEntry.setSize(data.length);

            // 2.Compress with the "deflate" algorithm
            Deflater compresser = new Deflater(Deflater.DEFLATED, true);
            compresser.setInput(data);
            compresser.finish();
            byte[] compressedData = new byte[data.length];
            int compressedDataLength = compresser.deflate(compressedData);

            // 3. The start key is generated: the byte sequence
            // representing the password in UTF-8 is used to
            // generate a 20-byte SHA1 digest.
            byte[] passBytes = mNewPwd.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA1");
            passBytes = md.digest(passBytes);
            // 4. Checksum specifies a digest in BASE64 encoding
            // that can be used to detect password correctness. The
            // digest is build from the compressed unencrypted file.
            md.reset();
            md.update(compressedData, 0, (compressedDataLength > 1024 ? 1024 : compressedDataLength));
            byte[] checksumBytes = new byte[20];
            md.digest(checksumBytes, 0, 20);

            // 5. For each file, a 16-byte salt is generated by a random
            // generator.
            // The salt is a BASE64 encoded binary sequence.
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);

            // char passChars[] = new String(passBytes, "UTF-8").toCharArray();
            /*
			 * char passChars[] = new char[20]; for (int i = 0; i <
			 * passBytes.length; i++) { passChars[i] = (char)
			 * ((passBytes[i]+256)%256);
			 * //System.out.println("passChars[i]:"+passChars
			 * [i]+", passBytes[i]"+passBytes[i]); } //char passChars[] =
			 * getChars(passBytes); // 6. The PBKDF2 algorithm based on the
			 * HMAC-SHA-1 function is used for the key derivation.
			 * SecretKeyFactory factory =
			 * SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); // 7. The
			 * salt is used together with the start key to derive a unique
			 * 128-bit key for each file. // The default iteration count for the
			 * algorithm is 1024. KeySpec spec = new PBEKeySpec(passChars, salt,
			 * 1024, 128); SecretKey skey = factory.generateSecret(spec); byte[]
			 * raw = skey.getEncoded(); // algorithm-name="Blowfish CFB"
			 * SecretKeySpec skeySpec = new SecretKeySpec(raw, "Blowfish");
             */
            byte[] dk = derivePBKDF2Key(passBytes, salt, 1024, 16);
            SecretKeySpec key = new SecretKeySpec(dk, "Blowfish");
            // 8.The files are encrypted: The random number
            // generator is used to generate the 8-byte initialization vector
            // for the
            // algorithm. The derived key is used together with the
            // initialization
            // vector to encrypt the file using the Blowfish algorithm in cipher
            // feedback
            // CFB mode.
            Cipher cipher = Cipher.getInstance("Blowfish/CFB/NoPadding");
            // initialisation-vector specifies the byte-sequence used
            // as an initialization vector to a encryption algorithm. The
            // initialization vector is a BASE64 encoded binary sequence.
            byte[] iv = new byte[8];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
            encryptedData = cipher.doFinal(compressedData, 0, compressedDataLength);

            // 9.update file entry encryption data.
            String checksum = new Base64Binary(checksumBytes).toString();
            FileEntryElement fileEntryElement = fileEntry.getOdfElement();
            EncryptionDataElement encryptionDataElement = OdfElement.findFirstChildNode(EncryptionDataElement.class, fileEntryElement);
            if (encryptionDataElement != null) {
                fileEntryElement.removeChild(encryptionDataElement);
            }
            encryptionDataElement = fileEntryElement.newEncryptionDataElement(checksum, "SHA1/1K");
            String initialisationVector = new Base64Binary(iv).toString();
            AlgorithmElement algorithmElement = OdfElement.findFirstChildNode(AlgorithmElement.class, encryptionDataElement);
            if (algorithmElement != null) {
                encryptionDataElement.removeChild(algorithmElement);
            }
            algorithmElement = encryptionDataElement.newAlgorithmElement("Blowfish CFB", initialisationVector);
            String saltStr = new Base64Binary(salt).toString();
            KeyDerivationElement keyDerivationElement = OdfElement.findFirstChildNode(KeyDerivationElement.class, encryptionDataElement);
            if (keyDerivationElement != null) {
                encryptionDataElement.removeChild(keyDerivationElement);
            }
            keyDerivationElement = encryptionDataElement.newKeyDerivationElement(1024, "PBKDF2", saltStr);
            StartKeyGenerationElement startKeyGenerationElement = OdfElement.findFirstChildNode(StartKeyGenerationElement.class, encryptionDataElement);
            if (startKeyGenerationElement != null) {
                encryptionDataElement.removeChild(startKeyGenerationElement);
            }
            encryptionDataElement.newStartKeyGenerationElement("SHA1").setKeySizeAttribute(20);

            // System.out.println("full-path=\""+ path +"\"");
            // System.out.println("size=\""+ data.length +"\"");
            // System.out.println("checksum=\""+ checksum +"\"");
            // System.out.println("compressedData ="+compressedDataLength);
            // System.out.println("MANIFEST: " + fileEntryElement.getParentNode().toString());
        } catch (Exception e) {
            // throws NoSuchAlgorithmException,
            // InvalidKeySpecException, NoSuchPaddingException,
            // InvalidKeyException,
            // InvalidAlgorithmParameterException,
            // IllegalBlockSizeException, BadPaddingException
            Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, e);
        }
        return encryptedData;
    }

    private byte[] decryptData(byte[] data, OdfFileEntry manifestEntry, EncryptionDataElement encryptionDataElement) {
        byte[] decompressData = null;
        try {
            KeyDerivationElement keyDerivationElement = OdfElement.findFirstChildNode(KeyDerivationElement.class, encryptionDataElement);
            AlgorithmElement algorithmElement = OdfElement.findFirstChildNode(AlgorithmElement.class, encryptionDataElement);
            String saltStr = keyDerivationElement.getSaltAttribute();
            String ivStr = algorithmElement.getInitialisationVectorAttribute();
            String checksum = encryptionDataElement.getChecksumAttribute();
            byte[] salt = Base64Binary.valueOf(saltStr).getBytes();
            byte[] iv = Base64Binary.valueOf(ivStr).getBytes();
            byte[] passBytes = mOldPwd.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            passBytes = md.digest(passBytes);
            /*
			 * char passChars[] = new char[passBytes.length]; for(int i = 0;
			 * i<passBytes.length; i++){ passChars[i] =
			 * (char)(passBytes[i]|0xFF); } KeySpec spec = new
			 * PBEKeySpec(passChars, salt, 1024, 128); SecretKeyFactory factory
			 * = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); SecretKey
			 * skey = factory.generateSecret(spec); byte[] raw =
			 * skey.getEncoded(); SecretKeySpec skeySpec = new
			 * SecretKeySpec(raw, "Blowfish");
             */
            byte[] dk = derivePBKDF2Key(passBytes, salt, 1024, 16);
            SecretKeySpec key = new SecretKeySpec(dk, "Blowfish");

            Cipher cipher = Cipher.getInstance("Blowfish/CFB/NoPadding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] decryptedData = cipher.doFinal(data);

            // valid checksum
            md.reset();
            md.update(decryptedData, 0, (decryptedData.length > 1024 ? 1024 : decryptedData.length));
            byte[] checksumBytes = new byte[20];
            md.digest(checksumBytes, 0, 20);
            String newChecksum = new Base64Binary(checksumBytes).toString();
            if (newChecksum.equals(checksum)) {
                // decompress the bytes
                Inflater decompresser = new Inflater(true);
                decompresser.setInput(decryptedData);
                decompressData = new byte[manifestEntry.getSize()];
                decompresser.inflate(decompressData);
                decompresser.end();
            } else {
                throw new OdfDecryptedException("The given password is wrong, please check it.");
            }
        } catch (Exception e) {
            Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, e);
        }
        return decompressData;
    }

    // derive PBKDF2Key (reference http://www.ietf.org/rfc/rfc2898.txt)
    byte[] derivePBKDF2Key(byte[] password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec keyspec = new SecretKeySpec(password, "HmacSHA1");
        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(keyspec);
        // length in octets of HmacSHA1 function output, a positive integer
        int hmacLen = hmac.getMacLength();
        // let l be the number of hLen-octet blocks in the derived key, rounding
        // up,
        // l = CEIL (dkLen / hLen) Here, CEIL (x) is the smallest integer
        // greater than, or equal to, x.
        int l = (keyLength % hmacLen > 0) ? (keyLength / hmacLen + 1) : (keyLength / hmacLen);
        // let r be the number of octets in the last block: r = dkLen - (l - 1)
        // * hLen .
        int r = keyLength - (l - 1) * hmacLen;
        byte T[] = new byte[l * hmacLen];
        int offset = 0;
        // For each block of the derived key apply the function F defined below
        // to the password P, the salt S, the iteration count c, and
        // the block index to compute the block:
        for (int i = 1; i <= l; i++) {
            byte Ur[] = new byte[hmacLen];
            byte Ui[] = new byte[salt.length + 4];
            System.arraycopy(salt, 0, Ui, 0, salt.length);
            // Here, INT (i) is a four-octet encoding of the integer i, most
            // significant octet first.
            Ui[salt.length + 0] = (byte) (i >>> 24);
            Ui[salt.length + 1] = (byte) (i >>> 16);
            Ui[salt.length + 2] = (byte) (i >>> 8);
            Ui[salt.length + 3] = (byte) (i);
            // U_1 \xor U_2 \xor ... \xor U_c
            for (int j = 0; j < iterationCount; j++) {
                Ui = hmac.doFinal(Ui);
                // XOR
                for (int k = 0; k < T.length; k++) {
                    Ur[k] ^= Ui[k];
                }
            }
            System.arraycopy(Ur, 0, T, offset, hmacLen);
            offset += hmacLen;
        }
        if (r < hmacLen) {
            byte DK[] = new byte[keyLength];
            System.arraycopy(T, 0, DK, 0, keyLength);
            return DK;
        }
        return T;
    }

    // Serializes a DOM tree into a byte array.
    // Providing the counterpart of the generic Namespace handling of
    // OdfFileDom.
    private byte[] flushDom(Document dom) {
        // if it is one of our DOM files we may flush all collected namespaces
        // to the root element
        if (dom instanceof OdfFileDom) {
            OdfFileDom odfDom = (OdfFileDom) dom;
            Map<String, String> nsByUri = odfDom.getMapNamespacePrefixByUri();
            OdfElement root = odfDom.getRootElement();
            if (root != null) {
                for (Entry<String, String> entry : nsByUri.entrySet()) {
                    root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + entry.getValue(), entry.getKey());
                }
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DOMXSImplementationSourceImpl dis = new org.apache.xerces.dom.DOMXSImplementationSourceImpl();
        DOMImplementationLS impl = (DOMImplementationLS) dis.getDOMImplementation("LS");
        LSSerializer writer = impl.createLSSerializer();
        LSOutput output = impl.createLSOutput();
        output.setByteStream(baos);
        writer.write(dom, output);
        return baos.toByteArray();
    }

    /**
     * Get the latest version of package content as InputStream, as it would be
     * saved. This might not be the original version once loaded from the
     * package.
     *
     * @param internalPath of the desired stream.
     * @return Inputstream of the ODF file within the package for the given
     * path.
     */
    public InputStream getInputStream(String internalPath) {
        internalPath = normalizeFilePath(internalPath);
        // else we always cache here and return a ByteArrayInputStream because
        // if
        // we would return ZipFile getInputStream(entry) we would not be
        // able to read 2 Entries at the same time. This is a limitation of the
        // ZipFile class.
        // As it would be quite a common thing to read the content.xml and the
        // styles.xml
        // simultanously when using XSLT on OdfPackages we want to circumvent
        // this limitation
        byte[] data = getBytes(internalPath);
        if (data != null && data.length != 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            return bais;
        }
        return null;
    }

    /**
     * Get the latest version of package content as InputStream, as it would be
     * saved. This might not be the original version once loaded from the
     * package.
     *
     * @param internalPath of the desired stream.
     * @param useOriginal true uses the stream as loaded from the ZIP. False
     * will return even modified file content as a stream.
     * @return Inputstream of the ODF file within the package for the given
     * path.
     */
    public InputStream getInputStream(String internalPath, boolean useOriginal) {
        InputStream stream = null;
        if (useOriginal) {
            ZipEntry entry = mOriginalZipEntries.get(internalPath);
            if (entry != null) {
                try {
                    stream = mZipFile.getInputStream(entry);
                } catch (IOException ex) {
                    //Catching IOException here should be fine: in-memory operations only
                    Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            stream = getInputStream(internalPath);
        }
        return stream;
    }

    /**
     * Gets the InputStream containing whole OdfPackage.
     *
     * @return the ODF package as input stream
     * @throws java.io.IOException - if the package could not be read
     */
    public InputStream getInputStream() throws IOException, SAXException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        save(out, mBaseURI);
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Insert the OutputStream for into OdfPackage. An existing file will be
     * replaced.
     *
     * @param internalPath - relative documentURL where the DOM tree should be
     * inserted as XML file
     * @return outputstream for the data of the file to be stored in package
     * @throws java.lang.Exception when the DOM tree could not be inserted
     */
    public OutputStream insertOutputStream(String internalPath) throws Exception {
        return insertOutputStream(internalPath, null);
    }

    /**
     * Insert the OutputStream - to be filled after method - when stream is
     * closed into OdfPackage. An existing file will be replaced.
     *
     * @param internalPath - relative documentURL where the DOM tree should be
     * inserted as XML file
     * @param mediaType - media type of stream
     * @return outputstream for the data of the file to be stored in package
     * @throws java.io.IOException when the DOM tree could not be inserted
     */
    public OutputStream insertOutputStream(String internalPath, String mediaType) throws IOException {
        internalPath = normalizeFilePath(internalPath);
        final String fPath = internalPath;
        final OdfFileEntry fFileEntry = getFileEntry(internalPath);
        final String fMediaType = mediaType;

        ByteArrayOutputStream baos = new ByteArrayOutputStream() {

            @Override
            public void close() throws IOException {
                byte[] data = this.toByteArray();
                if (fMediaType == null || fMediaType.length() == 0) {
                    insert(data, fPath, fFileEntry == null ? null : fFileEntry.getMediaTypeString());
                } else {
                    insert(data, fPath, fMediaType);
                }
                super.close();
            }
        };
        return baos;
    }

    /**
     * Removes a single file from the package.
     *
     * @param internalPath of the file relative to the package root
     */
    public void remove(String internalPath) {
        internalPath = normalizePath(internalPath);
        if (mZipEntries != null && mZipEntries.containsKey(internalPath)) {
            mZipEntries.remove(internalPath);
        }
        if (mManifestEntries != null && mManifestEntries.containsKey(internalPath)) {
            // remove from the manifest Map
            OdfFileEntry manifestEntry = mManifestEntries.remove(internalPath);
            // remove from the manifest DOM
            FileEntryElement manifestEle = manifestEntry.getOdfElement();
            manifestEle.getParentNode().removeChild(manifestEle);
        }
    }

    /**
     * Get the size of an internal file from the package.
     *
     * @param internalPath of the file relative to the package root
     * @return the size of the file in bytes or -1 if the size could not be
     * received.
     */
    public long getSize(String internalPath) {
        long size = -1;
        internalPath = normalizePath(internalPath);
        if (mZipEntries != null && mZipEntries.containsKey(internalPath)) {
            ZipEntry zipEntry = mZipEntries.get(internalPath);
            size = zipEntry.getSize();;
        }
        return size;
    }

    /**
     * Encoded XML Attributes
     */
    private String encodeXMLAttributes(String attributeValue) {
        String encodedValue = QUOTATION_PATTERN.matcher(attributeValue).replaceAll(ENCODED_QUOTATION);
        encodedValue = APOSTROPHE_PATTERN.matcher(encodedValue).replaceAll(ENCODED_APOSTROPHE);
        return encodedValue;
    }

    /**
     * Get EntityResolver to be used in XML Parsers which can resolve content
     * inside the OdfPackage
     *
     * @return a SAX EntityResolver
     */
    public EntityResolver getEntityResolver() {
        if (mResolver == null) {
            mResolver = new Resolver(this);
        }
        return mResolver;
    }

    /**
     * Get URIResolver to be used in XSL Transformations which can resolve
     * content inside the OdfPackage
     *
     * @return a TraX Resolver
     */
    public URIResolver getURIResolver() {
        if (mResolver == null) {
            mResolver = new Resolver(this);
        }
        return mResolver;
    }

    private static String getBaseURLFromFile(File file) throws IOException {
        String baseURL = Util.toExternalForm(file.getCanonicalFile().toURI());
        baseURL = BACK_SLASH_PATTERN.matcher(baseURL).replaceAll(SLASH);
        return baseURL;
    }

    /**
     * Ensures that the given file path is not null nor empty and not an
     * external reference
     * <ol>
     * <li>All backslashes "\" are exchanged by slashes "/"</li>
     * <li>Any substring "/../", "/./" or "//" will be removed</li>
     * <li>A prefix "./" and "../" will be removed</li>
     * </ol>
     *
     * @throws IllegalArgumentException If the path is NULL, empty or an
     * external path (e.g. starting with "../" is given). None relative URLs
     * will NOT throw an exception.
     * @return the normalized path or the URL
     */
    static String normalizeFilePath(String internalPath) {
        if (internalPath.equals(EMPTY_STRING)) {
            return SLASH;
        } else {
            return normalizePath(internalPath);
        }
    }

    /**
     * Ensures the given directory path is not null nor an external reference to
     * resources outside the package. An empty path and slash "/" are both
     * mapped to the root directory/document. NOTE: Although ODF only refer the
     * "/" as root, the empty path aligns more adequate with the file system
     * concept. To ensure the given directory path within the package can be
     * used as a key (is unique for the Package) the path will be normalized.
     *
     * @see #normalizeFilePath(String) In addition to the file path
     * normalization a trailing slash will be used for directories.
     */
    static String normalizeDirectoryPath(String directoryPath) {
        directoryPath = normalizePath(directoryPath);
        // if not the root document - which is from ODF view a '/' and no
        // trailing '/'
        if (!directoryPath.equals(OdfPackageDocument.ROOT_DOCUMENT_PATH)) {
            if (!directoryPath.endsWith(SLASH)) {
                // add a trailing slash
                directoryPath = directoryPath + SLASH;
            }
            if (directoryPath.startsWith(SLASH) && !directoryPath.equals(SLASH)) {
                directoryPath = directoryPath.substring(1);
            }
        }
        return directoryPath;
    }

    /**
     * 1
     * Normalizes both directory and file path
     */
    static String normalizePath(String path) {
        if (path == null) {
            String errMsg = "The internalPath given by parameter is NULL!";
            Logger.getLogger(OdfPackage.class.getName()).severe(errMsg);
            throw new IllegalArgumentException(errMsg);
        } else if (!mightBeExternalReference(path)) {
            if (path.equals(EMPTY_STRING)) {
                path = SLASH;
            } else {
                // exchange all backslash "\" with a slash "/"
                if (path.indexOf('\\') != -1) {
                    path = BACK_SLASH_PATTERN.matcher(path).replaceAll(SLASH);
                }
                // exchange all double slash "//" with a slash "/"
                while (path.indexOf("//") != -1) {
                    path = DOUBLE_SLASH_PATTERN.matcher(path).replaceAll(SLASH);
                }
                // if directory replacements (e.g. ..) exist, resolve and remove
                // them
                if (path.indexOf("/.") != -1 || path.indexOf("./") != -1) {
                    path = removeChangeDirectories(path);
                }
                if (path.startsWith(SLASH) && !path.equals(SLASH)) {
                    path = path.substring(1);
                }
            }
        }
        return path;
    }

    /**
     * Normalizes both directory and file path
     */
    private static boolean mightBeExternalReference(String internalPath) {
        boolean isExternalReference = false;
        // if the fileReference is a external relative documentURL..
        if (internalPath.startsWith(DOUBLE_DOT)
                || // or absolute documentURL
                // AND not root document
                internalPath.startsWith(SLASH) && !internalPath.equals(SLASH)
                || // or
                // absolute
                // IRI
                internalPath.contains(COLON)) {
            isExternalReference = true;
        }
        return isExternalReference;
    }

    /**
     * Resolving the directory replacements (ie. "/../" and "/./") with a slash
     * "/"
     */
    private static String removeChangeDirectories(String path) {
        boolean isDirectory = path.endsWith(SLASH);
        StringTokenizer tokenizer = new StringTokenizer(path, SLASH);
        int tokenCount = tokenizer.countTokens();
        List<String> tokenList = new ArrayList<String>(tokenCount);
        // add all paths to a list
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            tokenList.add(token);
        }
        if (!isDirectory) {
            String lastPath = tokenList.get(tokenCount - 1);
            if (lastPath.equals(DOT) || lastPath.equals(DOUBLE_DOT)) {
                isDirectory = true;
            }
        }
        String currentToken;
        int removeDirLevel = 0;
        StringBuilder out = new StringBuilder();
        // work on the list from back to front
        for (int i = tokenCount - 1; i >= 0; i--) {
            currentToken = tokenList.get(i);
            // every ".." will remove an upcoming path
            if (currentToken.equals(DOUBLE_DOT)) {
                removeDirLevel++;
            } else if (currentToken.equals(DOT)) {
            } else // if a path have to be remove, neglect current path
            {
                if (removeDirLevel > 0) {
                    removeDirLevel--;
                } else {
                    // add the path segment
                    out.insert(0, SLASH);
                    out.insert(0, currentToken);
                }
            }
        }
        if (removeDirLevel > 0) {
            return EMPTY_STRING;
        } else {
            if (!isDirectory) {
                // remove trailing slash /
                out.deleteCharAt(out.length() - 1);
            }
            return out.toString();
        }
    }

    /**
     * Checks if the given reference is a reference, which points outside the
     * ODF package
     *
     * @param internalPath the file reference to be checked
     * @return true if the reference is an package external reference
     */
    public static boolean isExternalReference(String internalPath) {
        if (mightBeExternalReference(internalPath)) {
            return true;
        } else {
            return mightBeExternalReference(normalizePath(internalPath));
        }
    }

    /**
     * Allow an application to register an error event handler.
     * <p>
     * If the application does not register an error handler, all error events
     * reported by the ODFDOM (e.g. the SAX Parser) will be silently ignored;
     * however, normal processing may not continue. It is highly recommended
     * that all ODF applications implement an error handler to avoid unexpected
     * bugs.
     * </p>
     * <p>
     * Applications may register a new or different handler in the middle of a
     * parse, and the ODFDOM will begin using the new handler immediately.
     * </p>
     *
     * @param handler The error handler.
     * @see #getErrorHandler
     */
    public void setErrorHandler(ErrorHandler handler) {
        mErrorHandler = handler;
    }

    /**
     * Return the current error handler used for ODF validation.
     *
     * @return The current error handler, or null if none has been registered
     * and validation is disabled.
     * @see #setErrorHandler
     */
    public ErrorHandler getErrorHandler() {
        return mErrorHandler;
    }

    void logValidationWarning(ValidationConstraint constraint, String baseURI, Object... o) throws SAXException {
        if (mErrorHandler == null) {
            return;
        }
        int varCount = 0;
        if (o != null) {
            varCount = o.length;
        }
        switch (varCount) {
            case 0:
                mErrorHandler.warning(new OdfValidationException(constraint, baseURI, o));
                break;
            case 1:
                mErrorHandler.warning(new OdfValidationException(constraint, baseURI, o[0]));
                break;
            case 2:
                mErrorHandler.warning(new OdfValidationException(constraint, baseURI, o[0], o[1]));
                break;
        }
    }

    void logValidationError(ValidationConstraint constraint, String baseURI, Object... o) throws SAXException {
        if (mErrorHandler == null) {
            return;
        }
        int varCount = 0;
        if (o != null) {
            varCount = o.length;
        }
        switch (varCount) {
            case 0:
                mErrorHandler.error(new OdfValidationException(constraint, baseURI, o));
                break;
            case 1:
                mErrorHandler.error(new OdfValidationException(constraint, baseURI, o[0]));
                break;
            case 2:
                mErrorHandler.error(new OdfValidationException(constraint, baseURI, o[0], o[1]));
                break;
        }
    }

    /**
     * @param odfVersion parsed from the manifest
     */
    void setManifestVersion(String odfVersion) {
        mManifestVersion = odfVersion;
    }

    /**
     * @return the ODF version found in the manifest. Meant to be used to reuse
     * when the manifest is recreated
     */
    String getManifestVersion() {
        return mManifestVersion;
    }

    /**
     * @return counter for ids that are not allowed to be saved (otherwise it is
     * not guaranteed that this id is unique)
     */
    public String getNextMarkupId() {
        return Integer.toString(mTransientMarkupId++);
    }
}
