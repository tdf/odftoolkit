/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
 ************************************************************************/
package org.openoffice.odf.pkg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.openoffice.odf.doc.OdfDocument;
import org.openoffice.odf.pkg.manifest.Algorithm;
import org.openoffice.odf.pkg.manifest.EncryptionData;
import org.openoffice.odf.pkg.manifest.OdfFileEntry;
import org.openoffice.odf.pkg.manifest.KeyDerivation;
import org.w3c.dom.Document;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * OdfPackage represents the package view to an OpenDocument document.
 * The OdfPackage will be created from an ODF document and represents a copy of 
 * the loaded document, where files can be inserted and deleted.
 * The changes take effect, when the OdfPackage is being made persistend by save().
 */
public class OdfPackage {

    private Logger mLog = Logger.getLogger(OdfPackage.class.getName());

    public enum OdfFile {

        IMAGE_DIRECTORY("Pictures"),
        MANIFEST("META-INF/manifest.xml"),
        MEDIA_TYPE("mimetype");
        private final String filePath;

        OdfFile(String filePath) {
            this.filePath = filePath;
        }

        public String getPath() {
            return filePath;
        }
    }
    // Static parts of file references
    private static final String TWO_DOTS = "..";
    private static final String SLASH = "/";
    private static final String COLON = ":";
    private static final String EMPTY_STRING = "";    

    // temp Dir for this ODFpackage (2DO: temp dir handling will be removed most likely)
    private static File mTempDirParent = null;
    private File mTempDir = null;
    // some well known streams inside ODF packages    
    private String mMediaType;
    private List<String> mPackageEntries;
    private ZipFile mZipFile;
    private HashMap<String, ZipEntry> mZipEntries;
    private HashMap<String, Document> mContentDoms;
    private HashMap<String, byte[]> mContentStreams;
    private HashMap<String, File> mTempFiles;
    private List<String> mManifestList;
    private HashMap<String, OdfFileEntry> mManifestEntries;
    private String mBaseURI;
    private Resolver mResolver;

    /**
     * Creates an OdfPackage from the OpenDocument provided by a filePath.
     *
     * @param filePath - the filePath to the ODF document.
     * @throws java.lang.Exception - if the package could not be created
     */
    private OdfPackage(String filePath) throws Exception {
        initialize(new File(filePath), false);
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a File.    
     * @param file - a file representing the ODF document
     * @throws java.lang.Exception - if the package could not be created
     */
    private OdfPackage(File file) throws Exception {
        initialize(file, false);
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a InputStream.
     *
     * @param packageStream - an inputStream representing the ODF package
     * @throws java.lang.Exception - if the package could not be created
     */
    private OdfPackage(InputStream packageStream) throws Exception {
        initialize();
        File tempFile = TempDir.saveStreamToTempDir(packageStream, getTempDir());
        initialize(tempFile, true);
    }

    /**
     * Loads an OdfPackage from the given filePath.
     *
     * @param filePath - the local filePath to the file
     * @return the OpenDocument document represented as an OdfPackage
     * @throws java.lang.Exception - if the package could not be loaded
     */
    public static OdfPackage loadPackage(String filePath) throws Exception {
        return new OdfPackage(filePath);
    }

    /**
     * Loads an OdfPackage from the OpenDocument provided by a File.
     *
     * @param file - a File to loadPackage content from
     * @return the OpenDocument document represented as an OdfPackage
     * @throws java.lang.Exception - if the package could not be loaded
     */
    public static OdfPackage loadPackage(File file) throws Exception {
        return new OdfPackage(file);
    }

    /**
     * Creates an OdfPackage from the OpenDocument provided by a InputStream.
     *
     * @param packageStream - an inputStream representing the ODF package
     * @return the OpenDocument document represented as an OdfPackage
     * @throws java.lang.Exception - if the package could not be loaded
     */
    public static OdfPackage loadPackage(InputStream packageStream) throws Exception {
        return new OdfPackage(packageStream);
    }

    private void initialize(File f, boolean isInitialized) throws Exception {
        // only temp Files copied from an InputStream should already be initialized
        if (!isInitialized) {
            initialize();
        }
        mBaseURI = getBaseURIFromFile(f);

        mZipFile = new ZipFile(f);
        Enumeration<? extends ZipEntry> zipEntries = mZipFile.entries();
        try {
            ZipEntry zipEntry;
            while (zipEntries.hasMoreElements()) {
                zipEntry = zipEntries.nextElement();
                if (zipEntry.isDirectory()) {
                    insert(zipEntry, (byte[]) null);
                } else {
                    OutputStream os = null;
                    if (zipEntry.getName().endsWith(".xml") || zipEntry.getName().equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
                        os = new StoreContentOutputStream(zipEntry);
                    } else {
                        os = new StoreTempOutputStream(zipEntry);
                    }
                    if (os != null) {
                        byte[] buf = new byte[4096];
                        int r = 0;
                        InputStream is = mZipFile.getInputStream(zipEntry);
                        while ((r = is.read(buf, 0, 4096)) > -1) {
                            os.write(buf, 0, r);
                        }
                        os.close();
                    }
                }
            }
            parseManifest();
//          decryptAll();
        } finally {
            mZipFile.close();
        }
    }

    /**
     * Set the baseURI for this package. NOTE: Should only be set during saving the package.
     */
    void setBaseURI(String baseURI) {
        mBaseURI = baseURI;
    }

    /**
     * Get the URI, where this ODF package is stored. 
     * @return the URI to the ODF package. Returns null if package is not stored yet.
     */
    public String getBaseURI() {
        return mBaseURI;
    }

    /**
     * Removes all content, resets to not password protected
     * <p>This basically transforms the ODFPackage to an empty Package</p>
     */
    void initialize() {
        mMediaType = null;
        mPackageEntries = new LinkedList<String>();
        mZipEntries = new HashMap<String, ZipEntry>();
        mContentDoms = new HashMap<String, Document>();
        mContentStreams = new HashMap<String, byte[]>();
        mTempFiles = new HashMap<String, File>();
        mManifestList = new LinkedList<String>();
        mManifestEntries = null;
        if (mTempDir != null) {
            TempDir.release(mTempDir);
            mTempDir = null;
        }
    }

    /**
     * Get the media type
     * 
     * @return the mediaType string of this package
     */
    public String getMediaType() {
        return mMediaType;
    }

    /**
     * Set the mediaType
     * 
     * @param mediaType string of this package
     */
    public void setMediaType(String mediaType) {
        mMediaType = mediaType;
        mPackageEntries.remove(OdfPackage.OdfFile.MEDIA_TYPE.getPath());
        if (mMediaType != null) {
            mPackageEntries.add(0, OdfPackage.OdfFile.MEDIA_TYPE.getPath());
        }
    }

    /**
     * 
     * Get a OdfFileEntry for filePath
     * NOTE: This method should be better moved to a DOM inherited Manifest class
     * 
     * @param path The relative package filePath within the ODF package
     * @return The manifest file entry will be returned.
     */
    public OdfFileEntry getFileEntry(String path) {
        path = ensureValidPackagePath(path);
        if (mManifestEntries == null) {
            try {
                parseManifest();
            } catch (Exception ex) {
                mLog.log(Level.SEVERE, null, ex);
            }
        }
        return mManifestEntries.get(path);
    }

    /**
     * Get a OdfFileEntries from the manifest file
     * @return The manifest file entries will be returned.
     */
    public Set<String> getFileEntries() {
        if (mManifestEntries == null) {
            try {
                parseManifest();
            } catch (Exception ex) {
                mLog.log(Level.SEVERE, null, ex);
            }
        }
        return mManifestEntries.keySet();
    }

    /**
     * 
     * Check existence of a file in the package. 
     * 
     * @param filePath The relative package filePath within the ODF package
     * @return True if there is an entry and a file for the given filePath
     */
    public boolean contains(String filePath) {
        filePath = ensureValidPackagePath(filePath);
        return (mPackageEntries.contains(filePath) && (mTempFiles.get(filePath) != null) && getFileEntry(filePath) != null);
    }

    /**
     * Save the package to given filePath.
     *
     * @param filePath - the filePath to the file
     * @throws java.lang.Exception - if the package could not be saved
     */
    public void save(String filePath) throws Exception {

        File f = new File(filePath);
        save(f);
    }

    /**
     * Save package to a given File.
     *
     * @param file - a File to save the package to.
     * @throws java.lang.Exception - if the package could not be saved
     */
    public void save(File file) throws Exception {

        FileOutputStream fos = new FileOutputStream(file);
        String baseURI = file.getCanonicalFile().toURI().toString();
        if (File.separatorChar == '\\') {
            baseURI = baseURI.replaceAll("\\\\", SLASH);
        }
        save(fos, baseURI);
    }

    /**
     * Save an ODF document to the OutputStream.
     *
     * @param os - the OutputStream to insert content to
     * @param baseURI - a URI for the package to be stored
     * @throws java.lang.Exception - if the package could not be saved
     */
    public void save(OutputStream os, String baseURI) throws Exception {

        mBaseURI = baseURI;

        if (mManifestEntries == null) {
            try {
                parseManifest();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        OdfFileEntry rootEntry = mManifestEntries.get(SLASH);
        if (rootEntry == null) {
            rootEntry = new OdfFileEntry(SLASH, mMediaType);
            mManifestList.add(0, rootEntry.getPath());

        } else {
            rootEntry.setMediaType(mMediaType);
        }

        ZipOutputStream zos = new ZipOutputStream(os);
        long modTime = (new java.util.Date()).getTime();

        // move manifest to first place to ensure it is written first
        // into the package zip file
        if (mPackageEntries.contains(OdfFile.MEDIA_TYPE.getPath())) {
            mPackageEntries.remove(OdfFile.MEDIA_TYPE.getPath());
            mPackageEntries.add(0, OdfFile.MEDIA_TYPE.getPath());
        }

        Iterator it = mPackageEntries.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            byte[] data = getBytes(key);

            ZipEntry ze = mZipEntries.get(key);
            if (ze == null) {
                ze = new ZipEntry(key);
            }
            ze.setTime(modTime);
            // 2DO Svante: No dependency to layer above!
            if (key.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath()) || key.equals(OdfDocument.OdfXMLFile.META.getFileName())) {
                ze.setMethod(ZipEntry.STORED);
            } else {
                ze.setMethod(ZipEntry.DEFLATED);
            }

            CRC32 crc = new CRC32();
            if (data != null) {
                crc.update(data);
                ze.setSize(data.length);
            } else {
                ze.setMethod(ZipEntry.STORED);
                ze.setSize(0);
            }
            ze.setCrc(crc.getValue());
            ze.setCompressedSize(-1);
            zos.putNextEntry(ze);
            if (data != null) {
                zos.write(data, 0, data.length);
            }
            zos.closeEntry();

            mZipEntries.put(key, ze);
        }
        zos.close();
        os.flush();
    }

    /*
    public DocumentProperties getDocumentProperties() {
    return new DocumentProperties(this);
    }
    //     */
//    /**
//     * get ODFDOM Document for document in this package
//     */
//    OdfDocument getOdfDocument() throws SAXException, Exception {
//        OdfDocumentFactory builder = OdfDocumentFactory.newInstance();
//        OdfDocument doc = builder.parse(this);
//        // the OdfDocument reads content and styles, so those need to be detached
//        // from the package
//        if (doc != null) {
//            mContentDoms.put(STREAMNAME_CONTENT, doc);
//            mContentDoms.put(STREAMNAME_STYLES, doc);
//            mContentStreams.remove(STREAMNAME_CONTENT);
//            mContentStreams.remove(STREAMNAME_STYLES);
//        }
//        return doc;
//    }
//    /**
//     * get Stream for XML Subcontent
//     *
//     * @throws IllegalArgumentException
//     * if filetype of subcontent is not text/xmlo
//     */
//    public InputStream getXMLInputStream(String filePath)
//            throws Exception {
//
//        if (filePath.equals(OdfPackage.OdfXMLFile.MANIFEST.filePath)) {
//            if (mContentStreams.get(filePath) == null) {
//                throw new Exception(mBaseURI + ": " + filePath + " not found in package");
//            }
//        } else {
//            if (mManifestEntries == null) {
//                try {
//                    parseManifest();
//                } catch (Exception ex) {
//                    mLog.log(Level.SEVERE, null, ex);
//                }
//            }
//            OdfFileEntry fileEntry = (OdfFileEntry) mManifestEntries.get(filePath);
//            if (fileEntry == null) {
//                throw new Exception(mBaseURI + ": " + filePath + " not found in package");
//            }
//            if (!"text/xml".equals(fileEntry.getMediaType())) {
//                throw new IllegalArgumentException(mBaseURI + ": " + filePath + " is not of type text/xml");
//            }
//        }
//        InputStream is = getInputStream(filePath);
//        if (is == null) {
//            throw new Exception(mBaseURI + ": " + filePath + " not found in package");
//        }
//        return is;
//    }
    /**
     * Data was updated, update mZipEntry and OdfFileEntry as well
     */
    private void entryUpdate(String path)
            throws Exception, SAXException,
            TransformerConfigurationException, TransformerException,
            ParserConfigurationException {

        byte[] data = getBytes(path);
        int size = 0;
        if (data == null) {
            size = 0;
        } else {
            size = data.length;
        }
        if (mManifestEntries == null) {
            parseManifest();
        }
        OdfFileEntry fileEntry = mManifestEntries.get(path);
        ZipEntry zipEntry = mZipEntries.get(path);
        if (zipEntry == null) {
            return;
        }
        if (fileEntry != null) {
            if ("text/xml".equals(fileEntry.getMediaType())) {
                fileEntry.setSize(-1);
            } else {
                fileEntry.setSize(size);
            }
        }
        zipEntry.setSize(size);
        CRC32 crc = new CRC32();
        if ((data != null) && size > 0) {
            crc.update(data);
        }
        zipEntry.setCrc(crc.getValue());
        zipEntry.setCompressedSize(-1);
        long modTime = (new java.util.Date()).getTime();
        zipEntry.setTime(modTime);

    }

    /**
     * Parse the Manifest file
     */
    void parseManifest() throws Exception {

        InputStream is = getInputStream(OdfPackage.OdfFile.MANIFEST.filePath);
        if (is == null) {
            mManifestList = null;
            mManifestEntries = null;
            return;
        }

        mManifestList = new LinkedList<String>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (Exception ex) {
            mLog.log(Level.SEVERE, null, ex);
        }

        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        // More details at http://xerces.apache.org/xerces2-j/features.html#namespaces
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        // More details at http://xerces.apache.org/xerces2-j/features.html#namespace-prefixes
        xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        // More details at http://xerces.apache.org/xerces2-j/features.html#xmlns-uris
        xmlReader.setFeature("http://xml.org/sax/features/xmlns-uris", true);

        String uri = mBaseURI + OdfPackage.OdfFile.MANIFEST.filePath;
        xmlReader.setEntityResolver(getEntityResolver());
        xmlReader.setContentHandler(new ManifestContentHandler());

        InputSource ins = new InputSource(is);
        ins.setSystemId(uri);

        xmlReader.parse(ins);

        mContentStreams.remove(OdfPackage.OdfFile.MANIFEST.filePath);
        entryUpdate(OdfPackage.OdfFile.MANIFEST.filePath);
    }

    /** Checks if filePath is not null nor empty and not an external reference */
    private String ensureValidPackagePath(String filePath) {
        if (filePath == null) {
            String errMsg = "The path given by parameter is NULL!";
            mLog.severe(errMsg);
            throw new IllegalArgumentException(errMsg);
        } else if (filePath.equals(EMPTY_STRING)) {
            String errMsg = "The path given by parameter is an empty string!";
            mLog.severe(errMsg);
            throw new IllegalArgumentException(errMsg);
        } else {
            if (filePath.indexOf('\\') != -1) {
                filePath = filePath.replace('\\', '/');
            }
            if (isExternalReference(filePath)) {
                String errMsg = "The path given by parameter '" + filePath + "' is not an internal ODF package path!";
                mLog.severe(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
        }
        return filePath;
    }

    /**
     * add a directory to the OdfPackage
     */
    private void addDirectory(String path) throws Exception {
        path = ensureValidPackagePath(path);

        if ((path.length() < 1) || (path.charAt(path.length() - 1) != '/')) {
            path = path + SLASH;
        }
        insert((byte[]) null, path, null);

    }

    /**
     * Insert DOM tree into OdfPackage. An existing file will be replaced.
     * @param dom - dom tree to be inserted as file
     * @param path - relative filePath where the DOM tree should be inserted as XML file
     * @throws java.lang.Exception when the DOM tree could not be inserted
     */
    public void insert(Document dom, String path) throws Exception {
        path = ensureValidPackagePath(path);

        if (mManifestEntries == null) {
            try {
                parseManifest();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String mediaType = "text/xml";
        String d = EMPTY_STRING;
        StringTokenizer tok = new StringTokenizer(path, SLASH);
        {
            while (tok.hasMoreTokens()) {
                String s = tok.nextToken();
                if (EMPTY_STRING.equals(d)) {
                    d = s + SLASH;
                } else {
                    d = d + s + SLASH;
                }
                if (tok.hasMoreTokens()) {
                    if (!mPackageEntries.contains(d)) {
                        addDirectory(d);
                    }
                }
            }
        }

        mContentStreams.remove(path);
        if (dom == null) {
            mContentDoms.remove(path);
        } else {
            mContentDoms.put(path, dom);
        }

        if (!mPackageEntries.contains(path)) {
            mPackageEntries.add(path);
        }

        try {
            if (!OdfPackage.OdfFile.MANIFEST.filePath.equals(path)) {
                if (mManifestEntries.get(path) == null) {
                    OdfFileEntry fileEntry = new OdfFileEntry(path, mediaType);
                    mManifestEntries.put(path, fileEntry);
                    mManifestList.add(path);
                }
            } else {
                parseManifest();
            }

            ZipEntry ze = mZipEntries.get(path);
            if (ze != null) {
                ze = new ZipEntry(path);
                ze.setMethod(ZipEntry.DEFLATED);
                mZipEntries.put(path, ze);
            }
            // 2DO Svante: No dependency to layer above!            
            if (path.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath()) || path.equals(OdfDocument.OdfXMLFile.META.getFileName())) {
                ze.setMethod(ZipEntry.STORED);
            }

            entryUpdate(path);
        } catch (SAXException se) {
            throw new Exception("SAXException:" + se.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new Exception("ParserConfigurationException:" + pce.getMessage());
        } catch (TransformerConfigurationException tce) {
            throw new Exception("TransformerConfigurationException:" + tce.getMessage());
        } catch (TransformerException te) {
            throw new Exception("TransformerException:" + te.getMessage());
        }
    }

    /**
     * returns true if a DOM tree has been requested
     * for given sub-content of OdfPackage
     * @param path - a path inside the OdfPackage eg to a content.xml stream
     * @return - wether the package class internally has a DOM representation for the given path
     */
    public boolean hasDom(String path) {
        return (mContentDoms.get(path) != null);
    }

    /**
     * Gets org.w3c.dom.Document for XML file contained in package.
     * @param path - a path inside the OdfPackage eg to a content.xml stream
     * @return an org.w3c.dom.Document
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws Exception
     * @throws IllegalArgumentException 
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public Document getDom(String path)
            throws SAXException, ParserConfigurationException,
            Exception, IllegalArgumentException,
            TransformerConfigurationException, TransformerException {

        Document doc = mContentDoms.get(path);
        if (doc != null) {
            return doc;
        }

        InputStream is = getInputStream(path);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver(getEntityResolver());

        String uri = getBaseURI() + path;

//        if (mErrorHandler != null) {
//            builder.setErrorHandler(mErrorHandler);
//        }

        InputSource ins = new InputSource(is);
        ins.setSystemId(uri);

        doc = builder.parse(ins);

        if (doc != null) {
            mContentDoms.put(path, doc);
//            mContentStreams.remove(filePath);
        }
        return doc;
    }

    /**
     * Inserts InputStream into an OdfPackage. An existing file will be replaced.
     * @param sourceURI - the source URI to the file to be inserted into the package.
     * @param packagePath - relative filePath where the tree should be inserted as XML file          
     * @throws java.lang.Exception In case the file could not be saved
     */
    public void insert(URI sourceURI, String packagePath) throws Exception {
        insert(sourceURI, packagePath, null);
    }
    
    /**
     * Inserts InputStream into an OdfPackage. An existing file will be replaced.
     * @param sourceURI - the source URI to the file to be inserted into the package.
     * @param mediaType - media type of stream. Set to null if unknown
     * @param packagePath - relative filePath where the tree should be inserted as XML file          
     * @throws java.lang.Exception In case the file could not be saved
     */
    public void insert(URI sourceURI, String packagePath, String mediaType) throws Exception {
        String sourceRef = sourceURI.toString();
        InputStream is = null;
        if (sourceURI.isAbsolute()) {
            // if the URI is absolute it can be converted to URL
            is = sourceURI.toURL().openStream();
        } else if (sourceRef.contains(COLON)) {
            // if the URI string representation has a protocol create URL
            is = new URL(sourceURI.toString()).openStream();
        } else {
            // otherwise create a file class to open the stream
            is = new File(sourceRef).toURL().openStream();
        }
        if (sourceRef.contains(SLASH)) {
            sourceRef = sourceRef.substring(sourceRef.lastIndexOf(SLASH) + 1, sourceRef.length());
        }
        insert(is, packagePath, mediaType);
    }

    /**
     * Inserts InputStream into an OdfPackage. An existing file will be replaced.
     * @param is - the stream of the file to be inserted into the package.
     * @param mediaType - media type of stream. Set to null if unknown
     * @param packagePath - relative filePath where the tree should be inserted as XML file          
     * @throws java.lang.Exception In case the file could not be saved
     */
    public void insert(InputStream is, String packagePath, String mediaType)
            throws Exception {
        packagePath = ensureValidPackagePath(packagePath);

        if (is == null) {
            insert((byte[]) null, packagePath, mediaType);
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buf = new byte[4096];
            int r = 0;
            while ((r = bis.read(buf, 0, 4096)) > -1) {
                baos.write(buf, 0, r);
            }
            byte[] data = baos.toByteArray();
            insert(data, packagePath, mediaType);
            // image should not be stored in memory but on disc
            if ((!packagePath.endsWith(".xml")) && (!packagePath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath()))) {
                // insertOutputStream to filesystem
                File tempFile = new File(getTempDir(), packagePath);
                File parent = tempFile.getParentFile();
                parent.mkdirs();
                OutputStream fos = new BufferedOutputStream(new FileOutputStream(tempFile));
                fos.write(data);
                fos.close();
                mTempFiles.put(packagePath, tempFile);
                mContentStreams.remove(packagePath);
            }

        }
    }

    /**
     * Insert byte array into OdfPackage. An existing file will be replaced.
     * @param fileBytes - data of the file stream to be stored in package
     * @param mediaType - media type of stream. Set to null if unknown
     * @param fileDestPath - relative filePath where the DOM tree should be inserted as XML file     
     * @throws java.lang.Exception when the DOM tree could not be inserted
     */
    public void insert(byte[] fileBytes, String fileDestPath, String mediaType) throws Exception {
        fileDestPath = ensureValidPackagePath(fileDestPath);

        String d = EMPTY_STRING;
        //2DO: Test tokenizer for whitespaces..
        StringTokenizer tok = new StringTokenizer(fileDestPath, SLASH);
        {
            while (tok.hasMoreTokens()) {
                String s = tok.nextToken();
                if (EMPTY_STRING.equals(d)) {
                    d = s + SLASH;
                } else {
                    d = d + s + SLASH;
                }
                if (tok.hasMoreTokens()) {
                    if (!mPackageEntries.contains(d)) {
                        addDirectory(d);
                        // add manifest entry for folder if not already existing
                        // media type for folder has to be set for embedded objects
                        if (!OdfPackage.OdfFile.MANIFEST.filePath.equals(d)) {
                            if (mediaType != null) {
                                if (mManifestEntries.get(d) == null) {
                                    OdfFileEntry fileEntry = new OdfFileEntry(d, mediaType);
                                    mManifestEntries.put(d, fileEntry);
                                    if (!mManifestList.contains(d)) {
                                        mManifestList.add(d);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        try {
            if (OdfPackage.OdfFile.MEDIA_TYPE.getPath().equals(fileDestPath)) {
                try {
                    setMediaType(new String(fileBytes, "UTF-8"));
                } catch (UnsupportedEncodingException useEx) {
                    mLog.log(Level.WARNING, "ODF file could not be created as string!", useEx);
                }
                return;
            }
            if (fileBytes == null) {
                mContentStreams.remove(fileDestPath);
            } else {
                mContentStreams.put(fileDestPath, fileBytes);
            }
            if (!mPackageEntries.contains(fileDestPath)) {
                mPackageEntries.add(fileDestPath);
            }
            if (!OdfPackage.OdfFile.MANIFEST.filePath.equals(fileDestPath)) {
                if (mediaType != null) {
                    if (mManifestEntries.get(fileDestPath) == null) {
                        OdfFileEntry fileEntry = new OdfFileEntry(fileDestPath, mediaType);
                        mManifestEntries.put(fileDestPath, fileEntry);
                        if (!mManifestList.contains(fileDestPath)) {
                            mManifestList.add(fileDestPath);
                        }
                    }
                }
            } else {
                parseManifest();
            }
            ZipEntry ze = mZipEntries.get(fileDestPath);
            if (ze != null) {
                ze = new ZipEntry(fileDestPath);
                ze.setMethod(ZipEntry.DEFLATED);
                mZipEntries.put(fileDestPath, ze);
            }
            // 2DO Svante: No dependency to layer above!
            if (fileDestPath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath()) || fileDestPath.equals(OdfDocument.OdfXMLFile.META.getFileName())) {
                ze.setMethod(ZipEntry.STORED);
            }
            entryUpdate(fileDestPath);
        } catch (SAXException se) {
            throw new Exception("SAXException:" + se.getMessage());
        } catch (ParserConfigurationException pce) {
            throw new Exception("ParserConfigurationException:" + pce.getMessage());
        } catch (TransformerConfigurationException tce) {
            throw new Exception("TransformerConfigurationException:" + tce.getMessage());
        } catch (TransformerException te) {
            throw new Exception("TransformerException:" + te.getMessage());
        }
    }

    private void insert(ZipEntry zipe, byte[] content) {
        if (content != null) {
            if (zipe.getName().equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
                try {
                    mMediaType = new String(content, 0, content.length, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    mLog.log(Level.SEVERE, null, ex);
                }
            } else {
                mContentStreams.put(zipe.getName(), content);
            }
        }
        if (!mPackageEntries.contains(zipe.getName())) {
            mPackageEntries.add(zipe.getName());
        }
        mZipEntries.put(zipe.getName(), zipe);
    }

    private void insert(ZipEntry zipe, File file) {
        if (file != null) {
            mTempFiles.put(zipe.getName(), file);
        }
        if (!mPackageEntries.contains(zipe.getName())) {
            mPackageEntries.add(zipe.getName());
        }
        mZipEntries.put(zipe.getName(), zipe);
    }

    /**
     * Get Manifest as String
     * NOTE: This functionality should better be moved to a DOM based Manifest class
     */
    String getManifestAsString() {
        if (mManifestEntries == null) {
            try {
                parseManifest();
                if (mManifestEntries == null) {
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        StringBuffer buf = new StringBuffer();

        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n");

        Iterator it = mManifestList.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String s = null;
            OdfFileEntry fileEntry = mManifestEntries.get(key);
            if (fileEntry != null) {
                buf.append(" <manifest:file-entry");
                s = fileEntry.getMediaType();
                if (s == null) {
                    s = EMPTY_STRING;
                }
                buf.append(" manifest:media-type=\"");
                buf.append(encodeXMLAttributes(s));
                buf.append("\"");
                s = fileEntry.getPath();

                if (s == null) {
                    s = EMPTY_STRING;
                }
                buf.append(" manifest:full-path=\"");
                buf.append(encodeXMLAttributes(s));
                buf.append("\"");
                int i = fileEntry.getSize();
                if (i > 0) {
                    buf.append(" manifest:size=\"");
                    buf.append(i);
                    buf.append("\"");
                }
                EncryptionData enc = fileEntry.getEncryptionData();

                if (enc != null) {
                    buf.append(">\n");
                    buf.append("  <manifest:encryption-data>\n");
                    Algorithm alg = enc.getAlgorithm();
                    if (alg != null) {
                        buf.append("   <manifest:algorithm");
                        s = alg.getName();
                        if (s == null) {
                            s = EMPTY_STRING;
                        }
                        buf.append(" manifest:algorithm-name=\"");
                        buf.append(encodeXMLAttributes(s));
                        buf.append("\"");
                        s = alg.getInitializationVector();
                        if (s == null) {
                            s = EMPTY_STRING;
                        }
                        buf.append(" manifest:initialization-vector=\"");
                        buf.append(encodeXMLAttributes(s));
                        buf.append("\"/>\n");
                    }
                    KeyDerivation keyDerivation = enc.getKeyDerivation();
                    if (keyDerivation != null) {
                        buf.append("   <manifest:key-derivation");
                        s = keyDerivation.getName();
                        if (s == null) {
                            s = EMPTY_STRING;
                        }
                        buf.append(" manifest:key-derivation-name=\"");
                        buf.append(encodeXMLAttributes(s));
                        buf.append("\"");
                        s = keyDerivation.getSalt();
                        if (s == null) {
                            s = EMPTY_STRING;
                        }
                        buf.append(" manifest:salt=\"");
                        buf.append(encodeXMLAttributes(s));
                        buf.append("\"");

                        buf.append(" manifest:iteration-count=\"");
                        buf.append(keyDerivation.getIterationCount());
                        buf.append("\"/>\n");
                    }
                    buf.append("  </manifest:encryption-data>\n");
                    buf.append(" </<manifest:file-entry>\n");
                } else {
                    buf.append("/>\n");
                }
            }
        }
        buf.append("</manifest:manifest>");

        return buf.toString();
    }

    /**
     * Get package (sub-) content as byte array
     * 
     * @param filePath relative filePath to the package content
     * @return the unzipped package content as byte array
     * @throws java.lang.Exception
     */
    public byte[] getBytes(String filePath)
            throws Exception {
        filePath = ensureValidPackagePath(filePath);
        byte[] data = null;

        if (filePath == null || filePath.equals(EMPTY_STRING)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            save(baos, mBaseURI);
            return baos.toByteArray();
        }
        if (filePath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
            if (mMediaType == null) {
                return null;
            }
            try {
                data = mMediaType.getBytes("UTF-8");
            } catch (UnsupportedEncodingException use) {
                mLog.log(Level.SEVERE, null, use);
                return null;
            }
        } else if (mPackageEntries.contains(filePath) && mContentDoms.get(filePath) != null) {
            {
                Document doc = mContentDoms.get(filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

                DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
                LSSerializer writer = impl.createLSSerializer();

                LSOutput output = impl.createLSOutput();
                output.setByteStream(baos);

                writer.write(doc, output);
                data = baos.toByteArray();
            }
        } else if (mPackageEntries.contains(filePath) && mTempFiles.get(filePath) != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            InputStream is = new BufferedInputStream(new FileInputStream(mTempFiles.get(filePath)));
            byte[] buf = new byte[4096];
            int r = 0;
            while ((r = is.read(buf, 0, 4096)) > 0) {
                os.write(buf, 0, r);
            }
            is.close();
            os.close();
            data = os.toByteArray();
        } else if (mPackageEntries.contains(filePath) && mContentStreams.get(filePath) != null) {
            data = mContentStreams.get(filePath);
        } else if (filePath.equals(OdfPackage.OdfFile.MANIFEST.filePath)) {
            if (mManifestEntries == null) {
                // manifest was not present
                return null;
            }
            String s = getManifestAsString();
            if (s == null) {
                return null;
            } else {
                data = s.getBytes("UTF-8");
            }
        }
        return data;
    }

    /**
     * Get subcontent as InputStream
     * @param filePath of the desired stream.
     * @return Inputstream of the ODF file within the package for the given path.
     * @throws Exception 
     */
    public InputStream getInputStream(String filePath)
            throws Exception {

        filePath = ensureValidPackagePath(filePath);
        if (mPackageEntries.contains(filePath) && mTempFiles.get(filePath) != null) {
            return new BufferedInputStream(new FileInputStream(mTempFiles.get(filePath)));
        }

        byte[] data = getBytes(filePath);
        if (data != null && data.length != 0) {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            return bais;
        }
        return null;
    }

    /**
     * Gets the InputStream containing whole OdfPackage.
     * 
     * @return the ODF package as input stream
     * @throws java.lang.Exception - if the package could not be read
     */
    public InputStream getInputStream() throws Exception {
        final PipedOutputStream os = new PipedOutputStream();
        final PipedInputStream is = new PipedInputStream();

        is.connect(os);

        Thread thread1 = new Thread() {

            @Override
            public void run() {
                try {
                    save(os, mBaseURI);
                } catch (Exception e) {
                }
            }
        };

        Thread thread2 = new Thread() {

            @Override
            public void run() {
                try {
                    BufferedInputStream bis = new BufferedInputStream(is, 4096);
                    BufferedOutputStream bos = new BufferedOutputStream(os, 4096);
                    byte[] buf = new byte[4096];
                    int r = 0;
                    while ((r = bis.read(buf, 0, 4096)) > 0) {
                        bos.write(buf, 0, r);
                    }
                    is.close();
                    os.close();
                } catch (Exception ie) {
                }
            }
        };

        thread1.start();
        thread2.start();

        return is;
    }

    /**
     * Insert the OutputStream for into OdfPackage. An existing file will be replaced.
     * @param filePath - relative filePath where the DOM tree should be inserted as XML file     
     * @return outputstream for the data of the file to be stored in package
     * @throws java.lang.Exception when the DOM tree could not be inserted
     */
    public OutputStream insertOutputStream(String filePath) throws Exception {
        return insertOutputStream(filePath, null);
    }

    /**
     * Insert the OutputStream - to be filled after method - when stream is closed into OdfPackage. 
     * An existing file will be replaced.   
     * @param filePath - relative filePath where the DOM tree should be inserted as XML file     
     * @param mediaType - media type of stream
     * @return outputstream for the data of the file to be stored in package
     * @throws java.lang.Exception when the DOM tree could not be inserted
     */
    public OutputStream insertOutputStream(String filePath, String mediaType) throws Exception {
        filePath = ensureValidPackagePath(filePath);
        final String fPath = filePath;
        final OdfFileEntry fFileEntry = getFileEntry(filePath);
        final String fMediaType = mediaType;

        ByteArrayOutputStream baos = new ByteArrayOutputStream() {

            @Override
            public void close() {
                try {
                    byte[] data = this.toByteArray();
                    if (fMediaType == null || fMediaType.length() == 0) {
                        insert(data, fPath, fFileEntry == null ? null : fFileEntry.getMediaType());
                    } else {
                        insert(data, fPath, fMediaType);
                    }
                    super.close();
                } catch (Exception ex) {
                    mLog.log(Level.SEVERE, null, ex);
                }
            }
        };
        return baos;
    }

//    /**
//     * get an InputStream with a specific filePath from the package.
//     *
//     * @throws IllegalArgumentException if sub-content is not XML
//     */
//    public InputStream getInputStream(String filePath) throws Exception {
//        return mZipFile.getInputStream(mZipFile.getEntry(filePath));
////        OdfPackageStream stream = new OdfPackageStream(this, filePath);
////        return stream;
//    }
    public void remove(String path) {
        if (mManifestList.contains(path)) {
            mManifestList.remove(path);
        }
        if (mManifestEntries.containsKey(path)) {
            mManifestEntries.remove(path);
        }
        if (mZipEntries.containsKey(path)) {
            mZipEntries.remove(path);
        }
        if (mTempFiles.containsKey(path)) {
            File file = mTempFiles.remove(path);
            file.delete();
        }
        if (mPackageEntries.contains(path)) {
            mPackageEntries.remove(path);
        }
    }

    /** Checks if the given reference is a reference, which points outside the ODF package
     * @param ref the file reference to be checked
     * @return true if the reference is an package external reference
     */
    public static boolean isExternalReference(String ref) {
        boolean isExternalReference = false;
        // if the reference is a external relative filePath..
        if (ref.startsWith(TWO_DOTS) ||
                // or absolute filePath 
                ref.startsWith(SLASH) ||
                // or absolute IRI
                ref.contains(COLON)) {
            isExternalReference = true;
        }
        return isExternalReference;
    }

    /**
     * get Temp Directory
     */
    private File getTempDir()
            throws Exception {

        if (mTempDir == null) {
            mTempDir = TempDir.createGeneratedName("ODF", mTempDirParent);
        }
        return mTempDir;
    }

    /**
     * encoded XML Attributes
     */
    private String encodeXMLAttributes(String s) {
        String r = s.replaceAll("\"", "&quot;");
        r = r.replaceAll("'", "&apos;");
        return r;
    }

    private class StoreContentOutputStream extends ByteArrayOutputStream {

        private ZipEntry mZipEntry1;

        public StoreContentOutputStream(ZipEntry zipEntry) {
            super();
            this.mZipEntry1 = zipEntry;
        }

        @Override
        public void close() {

            byte[] content = toByteArray();
            if (mZipEntry1 != null) {
                insert(mZipEntry1, content);
            }
        }
    }

    private class StoreTempOutputStream extends OutputStream {

        private ZipEntry mZipEntry2;
        private File mTempFile2;
        OutputStream mOs;

        public StoreTempOutputStream(ZipEntry zipEntry)
                throws Exception {

            super();
            this.mZipEntry2 = zipEntry;
            String fname = zipEntry.getName();
            if (File.separatorChar == '\\') {
                fname = fname.replaceAll("\\\\", SLASH);
            }
            mTempFile2 = new File(getTempDir(), fname);
            File parent = mTempFile2.getParentFile();
            parent.mkdirs();
            mOs = new BufferedOutputStream(new FileOutputStream(mTempFile2));
        }

        @Override
        public void write(byte[] b) {
            try {

                mOs.write(b);
            } catch (IOException ex) {
                mLog.log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) {
            try {

                mOs.write(b, off, len);
            } catch (IOException ex) {
                mLog.log(Level.SEVERE, null, ex);
            }
        }

        public void write(int b) {
            try {

                mOs.write(b);
            } catch (IOException ex) {
                mLog.log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void close() {
            try {

                mOs.close();
                if (mZipEntry2 != null) {
                    insert(mZipEntry2, mTempFile2);
                }
            } catch (IOException ex) {
                mLog.log(Level.SEVERE, null, ex);
            }
        }
    }

    private class ManifestContentHandler implements ContentHandler {

        private OdfFileEntry _currentFileEntry;
        private EncryptionData _currentEncryptionData;

        /**
         * Receive an object for locating the origin of SAX document events.
         */
        public void setDocumentLocator(Locator locator) {
        }

        /**
         * Receive notification of the beginning of a document.
         */
        public void startDocument() throws SAXException {
            mManifestList = new LinkedList<String>();
            mManifestEntries = new HashMap<String, OdfFileEntry>();
        }

        /**
         * Receive notification of the end of a document.
         */
        public void endDocument() throws SAXException {
        }

        /**
         * Begin the scope of a prefix-URI Namespace mapping.
         */
        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
        }

        /**
         * End the scope of a prefix-URI mapping.
         */
        public void endPrefixMapping(String prefix)
                throws SAXException {
        }

        /**
         * Receive notification of the beginning of an element.
         */
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
                throws SAXException {

            if (localName.equals("file-entry")) {
                _currentFileEntry = new OdfFileEntry();
                _currentFileEntry.setPath(atts.getValue("manifest:full-path"));
                _currentFileEntry.setMediaType(atts.getValue("manifest:media-type"));
                if (atts.getValue("manifest:size") != null) {
                    try {
                        _currentFileEntry.setSize(Integer.parseInt(atts.getValue("manifest:size")));
                    } catch (NumberFormatException nfe) {
                        throw new SAXException("not a number: " + atts.getValue("manifest:size"));
                    }
                }
            } else if (localName.equals("encryption-data")) {
                _currentEncryptionData = new EncryptionData();
                if (_currentFileEntry != null) {
                    _currentEncryptionData.setChecksumType(atts.getValue("manifest:checksum-type"));
                    _currentEncryptionData.setChecksum(atts.getValue("manifest:checksum"));
                    _currentFileEntry.setEncryptionData(_currentEncryptionData);
                }
            } else if (localName.equals("algorithm")) {
                Algorithm algorithm = new Algorithm();
                algorithm.setName(atts.getValue("manifest:algorithm-name"));
                algorithm.setInitializationVector(atts.getValue("manifest:initialization-vector"));
                if (_currentEncryptionData != null) {
                    _currentEncryptionData.setAlgorithm(algorithm);
                }
            } else if (localName.equals("key-derivation")) {
                KeyDerivation keyDerivation = new KeyDerivation();
                keyDerivation.setName(atts.getValue("manifest:key-derivation-name"));
                keyDerivation.setSalt(atts.getValue("manifest:salt"));
                if (atts.getValue("manifest:iteration-count") != null) {
                    try {
                        keyDerivation.setIterationCount(Integer.parseInt(atts.getValue("manifest:iteration-count")));
                    } catch (NumberFormatException nfe) {
                        throw new SAXException("not a number: " + atts.getValue("manifest:iteration-count"));
                    }
                }
                if (_currentEncryptionData != null) {
                    _currentEncryptionData.setKeyDerivation(keyDerivation);
                }
            }

        }

        /**
         * Receive notification of the end of an element.
         */
        public void endElement(String namespaceURI, String localName, String qName)
                throws SAXException {
            if (localName.equals("file-entry")) {
                if (_currentFileEntry.getPath() != null) {
                    mManifestEntries.put(_currentFileEntry.getPath(), _currentFileEntry);
                }
                mManifestList.add(_currentFileEntry.getPath());
                _currentFileEntry = null;
            } else if (localName.equals("encryption-data")) {
                _currentEncryptionData = null;
            }
        }

        /**
         * Receive notification of character data.
         */
        public void characters(char[] ch, int start, int length)
                throws SAXException {
        }

        /**
         * Receive notification of ignorable whitespace in element content.
         */
        public void ignorableWhitespace(char[] ch, int start, int length)
                throws SAXException {
        }

        /**
         * Receive notification of a processing instruction.
         */
        public void processingInstruction(String target, String data)
                throws SAXException {
        }

        /**
         * Receive notification of a skipped entity.
         */
        public void skippedEntity(String name) throws SAXException {
        }
    }

    /**
     * resolve external entities
     */
    private class Resolver implements EntityResolver, URIResolver {

        /**
         * Resolver constructor.
         */
        public Resolver() {
        }

        /**
         * Allow the application to resolve external entities.
         *
         * The Parser will call this method before opening any external entity except
         * the top-level document entity (including the external DTD subset,
         * external entities referenced within the DTD, and external entities referenced
         * within the document element): the application may request that the parser
         * resolve the entity itself, that it use an alternative URI,
         * or that it use an entirely different input source.
         */
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException {

            // This deactivates the attempt to loadPackage the Math DTD
            if (publicId != null && publicId.startsWith("-//OpenOffice.org//DTD Modified W3C MathML")) {
                return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
            }
            if (systemId != null) {
                if ((mBaseURI != null) && systemId.startsWith(mBaseURI)) {
                    if (systemId.equals(mBaseURI)) {
                        InputStream in = null;
                        try {
                            in = getInputStream();
                        } catch (Exception e) {
                            throw new SAXException(e);
                        }
                        InputSource ins;
                        ins = new InputSource(in);

                        if (ins == null) {
                            return null;
                        }
                        ins.setSystemId(systemId);
                        return ins;
                    } else {
                        if (systemId.length() > mBaseURI.length() + 1) {
                            InputStream in = null;
                            try {
                                String path = systemId.substring(mBaseURI.length() + 1);
                                in = getInputStream(path);
                                InputSource ins = new InputSource(in);
                                ins.setSystemId(systemId);
                                return ins;
                            } catch (Exception ex) {
                                mLog.log(Level.SEVERE, null, ex);
                            } finally {
                                try {
                                    in.close();
                                } catch (IOException ex) {
                                    mLog.log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        return null;
                    }
                } else if (systemId.startsWith("resource:/")) {
                    int i = systemId.indexOf('/');
                    if ((i > 0) && systemId.length() > i + 1) {
                        String res = systemId.substring(i + 1);
                        ClassLoader cl = OdfPackage.class.getClassLoader();
                        InputStream in = cl.getResourceAsStream(res);
                        if (in != null) {
                            InputSource ins = new InputSource(in);
                            ins.setSystemId(systemId);
                            return ins;
                        }
                    }
                    return null;
                } else if (systemId.startsWith("jar:")) {
                    try {
                        URL url = new URL(systemId);
                        JarURLConnection jarConn = (JarURLConnection) url.openConnection();
                        InputSource ins = new InputSource(jarConn.getInputStream());
                        ins.setSystemId(systemId);
                        return ins;
                    } catch (IOException ex) {
                        mLog.log(Level.SEVERE, null, ex);
                    }
                }
            }
            return null;
        }

        public Source resolve(String href, String base)
                throws TransformerException {
            try {
                URI uri = null;
                if (base != null) {
                    URI baseuri = new URI(base);
                    uri = baseuri.resolve(href);
                } else {
                    uri = new URI(href);
                }

                InputSource ins = null;
                try {
                    ins = resolveEntity(null, uri.toString());
                } catch (Exception e) {
                    throw new TransformerException(e);
                }
                if (ins == null) {
                    return null;
                }
                InputStream in = ins.getByteStream();
                StreamSource src = new StreamSource(in);
                src.setSystemId(uri.toString());
                return src;
            } catch (URISyntaxException use) {
                return null;
            }
        }
    }

    /**
     * get EntityResolver to be used in XML Parsers
     * which can resolve content inside the OdfPackage
     * @return a SAX EntityResolver
     */
    public EntityResolver getEntityResolver() {
        if (mResolver == null) {
            mResolver = new Resolver();
        }
        return mResolver;
    }

    /**
     * get URIResolver to be used in XSL Transformations
     * which can resolve content inside the OdfPackage
     * @return a TraX Resolver
     */
    public URIResolver getURIResolver() {
        if (mResolver == null) {
            mResolver = new Resolver();
        }
        return mResolver;
    }

    private static String getBaseURIFromFile(File file) throws Exception {
        String baseURI = file.getCanonicalFile().toURI().toString();
        if (File.separatorChar == '\\') {
            baseURI = baseURI.replaceAll("\\\\", SLASH);
        }
        return baseURI;
    }
}

