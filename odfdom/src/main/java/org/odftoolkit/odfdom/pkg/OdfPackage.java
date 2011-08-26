/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.pkg;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
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

import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.odftoolkit.odfdom.pkg.manifest.Algorithm;
import org.odftoolkit.odfdom.pkg.manifest.EncryptionData;
import org.odftoolkit.odfdom.pkg.manifest.KeyDerivation;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
 * OdfPackage represents the package view to an OpenDocument document. The
 * OdfPackage will be created from an ODF document and represents a copy of the
 * loaded document, where files can be inserted and deleted. The changes take
 * effect, when the OdfPackage is being made persistend by save().
 */
public class OdfPackage {

	/**
	 * This class solely exists to clean up after a package object has been
	 * removed by garbage collector. Finalizable classes are said to have slow
	 * garbage collection, so we don't make the whole OdfPackage finalizable.
	 */
	static private class OdfFinalizablePackage {

		File mTempDirForDeletion;

		OdfFinalizablePackage(File tempDir) {
			mTempDirForDeletion = tempDir;
		}

		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			if (mTempDirForDeletion != null) {
				TempDir.deleteTempOdfDirectory(mTempDirForDeletion);
			}
		}
	}
	private static final Pattern BACK_SLASH = Pattern.compile("\\\\");

	public enum OdfFile {

		IMAGE_DIRECTORY("Pictures"),
		MANIFEST("META-INF/manifest.xml"),
		MEDIA_TYPE("mimetype");
		private final String packagePath;

		OdfFile(String packagePath) {
			this.packagePath = packagePath;
		}

		public String getPath() {
			return packagePath;
		}
	}
	private static HashSet<String> mCompressedFileTypes;

	static {
		mCompressedFileTypes = new HashSet<String>();
		String[] typelist = new String[]{"jpg", "gif", "png", "zip", "rar",
			"jpeg", "mpe", "mpg", "mpeg", "mpeg4", "mp4", "7z", "ari",
			"arj", "jar", "gz", "tar", "war", "mov", "avi"};
		mCompressedFileTypes.addAll(Arrays.asList(typelist));
	}
	// Static parts of file references
	private static final String TWO_DOTS = "..";
	private static final String SLASH = "/";
	private static final String COLON = ":";
	private static final String EMPTY_STRING = "";
	private static final String XML_MEDIA_TYPE = "text/xml";
	// temp Dir for this ODFpackage (ToDo: (Issue 219 - PackageRefactoring) --temp dir handling will be removed most
	// likely)
	private File mTempDirParent;
	private File mTempDir;
	// only used indirectly for its finalizer (garbage collection)
	private OdfFinalizablePackage mFinalize;
	// some well known streams inside ODF packages
	private String mMediaType;
	private ZipHelper mZipFile;
	private HashMap<String, ZipEntry> mZipEntries;
	private boolean mUseTempFile;
	// ToDo: (Issue 219 - PackageRefactoring) --Why do we hvae two LISTS for the package
	private Set<String> mPackagePathSet;
	private List<String> mManifestPathList;
	private HashMap<String, OdfFileEntry> mManifestEntries;
	private String mBaseURI;
	private Resolver mResolver;
	// All opened documents from the same package are cached (including the root document)
	private HashMap<String, OdfPackageDocument> mPkgDocuments;
	// Three different incarnations of a package file/data
	// save() will check 1) mPkgDoms, 2) if not check mPkgData, 3) if not check mPkgTempFiles
	private HashMap<String, Document> mPkgDoms;
	private HashMap<String, byte[]> mPkgData;
	private HashMap<String, File> mPkgTempFiles;

	/**
	 * Creates the ODFPackage as an empty Package. For setting a specific temp
	 * directory, set the System variable org.odftoolkit.odfdom.tmpdir:<br>
	 * <code>System.setProperty("org.odftoolkit.odfdom.tmpdir");</code>
	 */
	private OdfPackage() {
		mMediaType = null;
		mResolver = null;
		mTempDir = null;
		mTempDirParent = null;
		mZipEntries = new HashMap<String, ZipEntry>();
		mPkgDocuments = new HashMap<String, OdfPackageDocument>();
		mPkgDoms = new HashMap<String, Document>();
		mPkgData = new HashMap<String, byte[]>();
		mPkgTempFiles = new HashMap<String, File>();
		mPackagePathSet = new HashSet<String>();
		mManifestPathList = new LinkedList<String>();

		// get a temp directory for everything
		String userPropDir = System.getProperty("org.odftoolkit.odfdom.tmpdir");
		if (userPropDir != null) {
			mTempDirParent = new File(userPropDir);
		}

		// specify whether temporary files are able to used.
		String userPropTempEnable = System.getProperty("org.odftoolkit.odfdom.tmpfile.disable");
		if ((userPropTempEnable != null)
				&& (userPropTempEnable.equalsIgnoreCase("true"))) {
			mUseTempFile = false;
		} else {
			mUseTempFile = true;
		}
	}

	/**
	 * Insert an Odf document into the package at the given path.
	 * The path has to be a directory and will receive the MIME type of the OdfPackageDocument.
	 *
	 * @param doc the OdfPackageDocument to be inserted.
	 * @param internalDocumentPath
	 *		path relative to the package root, where the document should be inserted.
	 */
	public void insertPackageDocument(OdfPackageDocument doc, String internalDocumentPath) {
		try {
			// the "/" representing the root document is outside the manifest.xml in the API an empty path
			if (internalDocumentPath.equals(OdfPackageDocument.ROOT_DOCUMENT_PATH)) {
				// add to Manifest entries
				insertDirectories(SLASH, doc.getMediaTypeString());
			} else {
				// add to Manifest entries
				insertDirectories(internalDocumentPath, doc.getMediaTypeString());
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, e);
		}
		mPkgDocuments.put(internalDocumentPath, doc);
	}

	/**
	 * @param internalDocumentPath
	 *		path relative to the package root, where the document should be inserted.
	 * @return an already open OdfPackageDocument via its path, otherwise NULL.
	 */
	public OdfPackageDocument getOpenPackageDocument(String internalDocumentPath) {
		return mPkgDocuments.get(internalDocumentPath);
	}

	/**
	 * Method returns all open OdfPackageDocuments of the OdfPackage matching the
	 * according MediaType.
	 * 
	 * @param mediaType media type which is used as a filter
	 * @return already open documents of the current package matching the given media type
	 */
	public List<OdfPackageDocument> getOpenPackageDocuments(MediaType mediaType) {
		String mediaTypeString = mediaType.getMediaTypeString();
		Set<String> manifestEntries = getFileEntries();
		List<OdfPackageDocument> embeddedObjects = new ArrayList<OdfPackageDocument>();
		// check manifest for current embedded OdfPackageDocuments
		for (String entry : manifestEntries) {
			// with entry greater one the root document is not within
			if (entry.length() > 1 && entry.endsWith(SLASH)) {
				String entryMediaType = getFileEntry(entry).getMediaTypeString();
				if (entryMediaType.equals(mediaTypeString)) {
					embeddedObjects.add(getOpenPackageDocument(entry));
				}
			}
		}
		return embeddedObjects;
	}

	/** Removes a document from the package via its path. Independent if it was already opened or not.
	 * @param internalDocumentPath
	 *		path relative to the package root, where the document should be removed.
	 */
	public void removePackageDocument(String internalDocumentPath) {
		try {
			// get all files of the package
			Set<String> allPackageFileNames = getFileEntries();

			// If the document is the root document
			// the "/" representing the root document is outside the manifest.xml in the API an empty path
			if (internalDocumentPath.equals(OdfPackageDocument.ROOT_DOCUMENT_PATH)) {
				for (String entryName : allPackageFileNames) {
					remove(entryName);
				}
				remove(SLASH);
			} else {
				//remove all the stream of the directory, such as pictures
				List<String> directoryEntryNames = new ArrayList<String>();
				for (String entryName : allPackageFileNames) {
					if (entryName.startsWith(internalDocumentPath)) {
						directoryEntryNames.add(entryName);
					}
				}
				for (String entryName : directoryEntryNames) {
					remove(entryName);
				}
				remove(internalDocumentPath);
			}
		} catch (Exception ex) {
			Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/** @return all currently opened OdfPackageDocument of this OdfPackage */
	public Set<String> getOpenedPackageDocuments() {
		return mPkgDocuments.keySet();
	}

	public OdfPackageDocument getRootDocument() {
		return mPkgDocuments.get(OdfPackageDocument.ROOT_DOCUMENT_PATH);
	}

	/**
	 * Creates an OdfPackage from the OpenDocument provided by a File.
	 * 
	 * <p>
	 * OdfPackage relies on the file being available for read access over the
	 * whole lifecycle of OdfPackage.
	 * </p>
	 * 
	 * @param odfFile
	 *            - a file representing the ODF document
	 * @throws java.lang.Exception
	 *             - if the package could not be created
	 */
	private OdfPackage(File odfFile) throws Exception {
		this();
		mBaseURI = getBaseURIFromFile(odfFile);
		initialize(new FileInputStream(odfFile));
	}

	/**
	 * Creates an OdfPackage from the OpenDocument provided by a InputStream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by OdfPackage, the InputStream is cached. This usually
	 * takes more time compared to the other constructors.
	 * </p>
	 * 
	 * @param odfStream
	 *            - an inputStream representing the ODF package
	 * @throws java.lang.Exception
	 *             - if the package could not be created
	 */
	private OdfPackage(InputStream odfStream) throws Exception {
		this();
		if (mUseTempFile) {
			File tempFile = newTempSourceFile(odfStream);
			initialize(tempFile);
		} else {
			initialize(odfStream);
		}
	}

	/**
	 * Loads an OdfPackage from the given filePath.
	 * 
	 * <p>
	 * OdfPackage relies on the file being available for read access over the
	 * whole lifecycle of OdfPackage.
	 * </p>
	 * 
	 * @param odfPath
	 *            - the filePath to the ODF package
	 * @return the OpenDocument document represented as an OdfPackage
	 * @throws java.lang.Exception
	 *             - if the package could not be loaded
	 */
	public static OdfPackage loadPackage(String odfPath) throws Exception {
		return new OdfPackage(new File(odfPath));
	}

	/**
	 * Loads an OdfPackage from the OpenDocument provided by a File.
	 * 
	 * <p>
	 * OdfPackage relies on the file being available for read access over the
	 * whole lifecycle of OdfPackage.
	 * </p>
	 * 
	 * @param odfFile
	 *            - a File to loadPackage content from
	 * @return the OpenDocument document represented as an OdfPackage
	 * @throws java.lang.Exception
	 *             - if the package could not be loaded
	 */
	public static OdfPackage loadPackage(File odfFile) throws Exception {
		return new OdfPackage(odfFile);
	}

	/**
	 * Creates an OdfPackage from the OpenDocument provided by a InputStream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by OdfPackage, the InputStream is cached. This usually
	 * takes more time compared to the other loadPackage methods.
	 * </p>
	 * 
	 * @param odfStream
	 *            - an inputStream representing the ODF package
	 * @return the OpenDocument document represented as an OdfPackage
	 * @throws java.lang.Exception
	 *             - if the package could not be loaded
	 */
	public static OdfPackage loadPackage(InputStream odfStream)
			throws Exception {
		return new OdfPackage(odfStream);
	}

	// Initialize using memory instead temporary disc
	private void initialize(InputStream odfStream) throws Exception {
		ByteArrayOutputStream tempBuf = new ByteArrayOutputStream();
		StreamHelper.stream(odfStream, tempBuf);
		byte[] mTempByteBuf = tempBuf.toByteArray();
		tempBuf.close();

		if (mTempByteBuf.length < 3) {
			throw new IllegalArgumentException(
					"An empty file was tried to be opened as ODF package!");
		}

		mZipFile = new ZipHelper(mTempByteBuf);
		Enumeration<? extends ZipEntry> entries = mZipFile.entries();
		if (!entries.hasMoreElements()) {
			throw new IllegalArgumentException(
					"It was not possible to unzip the file!");
		} else {
			do {
				ZipEntry zipEntry = entries.nextElement();
				mZipEntries.put(zipEntry.getName(), zipEntry);
				// ToDo: (Issue 219 - PackageRefactoring) --think about if the additional list mPackagePathSet is
				// necessary -
				// shouldn't everything be part of one of the other lists?
				// mabe keep this as "master", rename it?
				mPackagePathSet.add(zipEntry.getName());
				if (zipEntry.getName().equals(
						OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					StreamHelper.stream(mZipFile.getInputStream(zipEntry), out);
					try {
						mMediaType = new String(out.toByteArray(), 0, out.size(), "UTF-8");
					} catch (UnsupportedEncodingException ex) {
						Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			} while (entries.hasMoreElements());
		}
	}

	// Initialize using temporary directory on hard disc
	private void initialize(File odfFile) throws Exception {
		mBaseURI = getBaseURIFromFile(odfFile);

		if (mTempDirParent == null) {
			// getParentFile() returns already java.io.tmpdir when package is an
			// odfStream
			mTempDirParent = odfFile.getAbsoluteFile().getParentFile();
			if (!mTempDirParent.canWrite()) {
				mTempDirParent = null; // java.io.tmpdir will be used implicitly
			}
		}

		try {
			mZipFile = new ZipHelper(new ZipFile(odfFile));
		} catch (Exception e) {
			if (odfFile.length() < 3) {
				throw new IllegalArgumentException("The empty file '"
						+ odfFile.getPath() + "' is no ODF package!", e);
			} else {
				throw new IllegalArgumentException("Could not unzip the file "
						+ odfFile.getPath(), e);
			}
		}
		Enumeration<? extends ZipEntry> entries = mZipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = entries.nextElement();
			mZipEntries.put(zipEntry.getName(), zipEntry);
			// ToDo: (Issue 219 - PackageRefactoring) --think about if the additional list mPackagePathSet is
			// necessary -
			// shouldn't everything be part of one of the other lists?
			// mabe keep this as "master", rename it?
			mPackagePathSet.add(zipEntry.getName());
			if (zipEntry.getName().equals(
					OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				StreamHelper.stream(mZipFile.getInputStream(zipEntry), out);
				try {
					mMediaType = new String(out.toByteArray(), 0, out.size(),
							"UTF-8");
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	private File newTempSourceFile(InputStream odfStream) throws Exception {
		//	the type of file is uncertain therefore we use .tmp
		File odfFile = new File(getTempDir(), "theFile.tmp");
		//	 copy stream to temp file
		FileOutputStream os = new FileOutputStream(odfFile);
		StreamHelper.stream(odfStream, os);
		os.close();
		return odfFile;
	}

	/**
	 * Set the baseURI for this ODF package. NOTE: Should only be set during
	 * saving the package.
	 */
	void setBaseURI(String baseURI) {
		mBaseURI = baseURI;
	}

	/**
	 * Get the URI, where this ODF package is stored.
	 * 
	 * @return the URI to the ODF package. Returns null if package is not stored
	 *         yet.
	 */
	public String getBaseURI() {
		return mBaseURI;
	}

	/**
	 * Get the media type of the ODF package (equal to media type of ODF root
	 * document)
	 * 
	 * @return the mediaType string of this ODF package
	 */
	public String getMediaTypeString() {
		// ToDo: (Issue 219 - PackageRefactoring) --PackageRefactoring - Have to be the same mimetype as the root document
		return mMediaType;
	}

	/**
	 * Set the media type of the ODF package (equal to media type of ODF root
	 * document)
	 *
	 * @param mediaType
	 *            string of this ODF package
	 */
	void setMediaTypeString(String mediaType) {
		mMediaType = mediaType;
		mPackagePathSet.remove(OdfPackage.OdfFile.MEDIA_TYPE.getPath());
		mPackagePathSet.add(OdfPackage.OdfFile.MEDIA_TYPE.getPath());

	}

	/**
	 * 
	 * Get an OdfFileEntry for the packagePath NOTE: This method should be
	 * better moved to a DOM inherited Manifest class
	 * 
	 * @param packagePath
	 *            The relative package path within the ODF package
	 * @return The manifest file entry will be returned.
	 */
	public OdfFileEntry getFileEntry(String packagePath) {
		packagePath = normalizeFilePath(packagePath);
		return getManifestEntries().get(packagePath);
	}

	/**
	 * Get a OdfFileEntries from the manifest file (i.e. /META/manifest.xml")
	 * 
	 * @return The manifest file entries will be returned.
	 */
	public Set<String> getFileEntries() {
		return getManifestEntries().keySet();
	}

	/**
	 * 
	 * Check existence of a file in the package.
	 * 
	 * @param packagePath
	 *            The relative package filePath within the ODF package
	 * @return True if there is an entry and a file for the given filePath
	 */
	public boolean contains(String packagePath) {
		packagePath = normalizeFilePath(packagePath);
		return mPackagePathSet.contains(packagePath);
		// ToDo: (Issue 219 - PackageRefactoring) --return true for later added stuff
		// return (mPackagePathSet.contains(packagePath) &&
		// (mPkgTempFiles.get(packagePath) != null ||
		// mPkgData.get(packagePath)!=null) && getFileEntry(packagePath)
		// != null);
	}

	/**
	 * Save the package to given filePath.
	 * 
	 * @param odfPath
	 *            - the path to the ODF package destination
	 * @throws java.lang.Exception
	 *             - if the package could not be saved
	 */
	public void save(String odfPath) throws Exception {
		File f = new File(odfPath);
		save(f);
	}

	/**
	 * Save package to a given File. After saving it is still necessary to close
	 * the package to have again full access about the file.
	 * 
	 * @param odfFile
	 *            - the File to save the ODF package to
	 * @throws java.lang.Exception
	 *             - if the package could not be saved
	 */
	public void save(File odfFile) throws Exception {

		String baseURI = odfFile.getCanonicalFile().toURI().toString();
		if (File.separatorChar == '\\') {
			baseURI = baseURI.replaceAll("\\\\", SLASH);
		}
		if (baseURI.equals(mBaseURI)) {
			// save to the same file: cache everything first
			// ToDo: (Issue 219 - PackageRefactoring) --maybe it's better to write to a new file and copy that
			// to the original one - would be less memory footprint
			cacheContent();
		}
		FileOutputStream fos = new FileOutputStream(odfFile);
		save(fos, baseURI);
	}

	public void save(OutputStream odfStream) throws Exception {
		save(odfStream, null);
	}

	/**
	 * Save an ODF document to the OutputStream.
	 * 
	 * @param odfStream
	 *            - the OutputStream to insert content to
	 * @param baseURI
	 *            - a URI for the package to be stored
	 * @throws java.lang.Exception
	 *             - if the package could not be saved
	 */
	private void save(OutputStream odfStream, String baseURI) {
		try {
			mBaseURI = baseURI;
			OdfFileEntry rootEntry = getManifestEntries().get(SLASH);
			if (rootEntry == null) {
				rootEntry = new OdfFileEntry(SLASH, mMediaType);
				mManifestPathList.add(0, rootEntry.getPath());
			} else {
				rootEntry.setMediaTypeString(mMediaType);
			}
			ZipOutputStream zos = new ZipOutputStream(odfStream);
			long modTime = (new java.util.Date()).getTime();
			// remove mediatype path and use it as first
			mPackagePathSet.remove(OdfFile.MEDIA_TYPE.getPath());
			Iterator<String> it = mPackagePathSet.iterator();
			String key = null;
			boolean isFirstFile = true;
			// ODF requires the "./mimetype" file to be at first in the package
			while (it.hasNext() || isFirstFile) {
				try {
					if (isFirstFile) {
						key = OdfFile.MEDIA_TYPE.getPath();
						isFirstFile = false;
					} else {
						key = it.next();
					}
					byte[] data = getBytes(key);
					ZipEntry ze = mZipEntries.get(key);
					if (ze == null) {
						ze = new ZipEntry(key);
					}
					ze.setTime(modTime);
					if (fileNeedsCompression(key)) {
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
				} catch (IOException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			zos.close();
			odfStream.flush();
		} catch (IOException ex) {
			Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Determines if a file have to be compressed.
	 * @param filePath the file name
	 * @return true if the file needs compression, false, otherwise
	 */
	private boolean fileNeedsCompression(String filePath) {
		boolean result = false;

		if (filePath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
			return true;
		}
		if (filePath.lastIndexOf(".") > 0) {
			String endWith = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
			if (mCompressedFileTypes.contains(endWith.toLowerCase())) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * If this file is saved to itself, we have to cache it. It is not possible
	 * to read and write from the same zip file at the same time, so the content
	 * must be read and stored in memory.
	 */
	private void cacheContent() throws Exception {
		// read all entries
		getManifestEntries();
		Iterator<String> entries = mZipEntries.keySet().iterator();
		while (entries.hasNext()) {
			// open all entries once so the data is cached
			ZipEntry nextElement = mZipEntries.get(entries.next());
			String entryPath = nextElement.getName();
			getBytes(entryPath);
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
		if (mTempDir != null) {
			TempDir.deleteTempOdfDirectory(mTempDir);
		}
		if (mZipFile != null) {
			try {
				mZipFile.close();
			} catch (IOException ex) {
				// log exception and continue
				Logger.getLogger(OdfPackage.class.getName()).log(Level.INFO,
						null, ex);
			}
		}
		// release all stuff - this class is impossible to use afterwards
		mZipFile = null;
////		mTempDirParent = null;
////		mTempDir = null;
		mMediaType = null;
		mPackagePathSet = null;
		mZipEntries = null;
		mPkgDoms = null;
		mPkgData = null;
		mPkgTempFiles = null;
		mManifestPathList = null;
		mManifestEntries = null;
		mBaseURI = null;
		mResolver = null;
	}

	/**
	 * Data was updated, update mZipEntry and OdfFileEntry as well
	 */
	private void entryUpdate(String packagePath) {

		byte[] data = getBytes(packagePath);
		int size = 0;
		if (data == null) {
			size = 0;
		} else {
			size = data.length;
		}
		OdfFileEntry fileEntry = getManifestEntries().get(packagePath);
		ZipEntry zipEntry = mZipEntries.get(packagePath);
		if (zipEntry == null) {
			return;
		}
		if (fileEntry != null) {
			if (XML_MEDIA_TYPE.equals(fileEntry.getMediaTypeString())) {
				fileEntry.setSize(-1);
			} else {
				fileEntry.setSize(size);
			}
		}
		zipEntry.setSize(size);
		// create checksum from binaries
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

		InputStream is = getInputStream(OdfPackage.OdfFile.MANIFEST.packagePath);
		if (is == null) {
			mManifestPathList = null;
			mManifestEntries = null;
			return;
		}

		mManifestPathList = new LinkedList<String>();

		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		try {
			factory.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
		} catch (Exception ex) {
			Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
		}

		SAXParser parser = factory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		// More details at
		// http://xerces.apache.org/xerces2-j/features.html#namespaces
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		// More details at
		// http://xerces.apache.org/xerces2-j/features.html#namespace-prefixes
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes",
				true);
		// More details at
		// http://xerces.apache.org/xerces2-j/features.html#xmlns-uris
		xmlReader.setFeature("http://xml.org/sax/features/xmlns-uris", true);

		String uri = mBaseURI + "/" + OdfPackage.OdfFile.MANIFEST.packagePath;
		xmlReader.setEntityResolver(getEntityResolver());
		xmlReader.setContentHandler(new ManifestContentHandler());

		InputSource ins = new InputSource(is);
		ins.setSystemId(uri);

		xmlReader.parse(ins);

		mPkgData.remove(OdfPackage.OdfFile.MANIFEST.packagePath);
		entryUpdate(OdfPackage.OdfFile.MANIFEST.packagePath);
	}

	private void insertDirectories(String packagePath, String mediaType) {
		StringTokenizer tok = new StringTokenizer(packagePath, SLASH);
		String path = EMPTY_STRING;
		{
			// As directory paths are always ending with SLASH
			// it is a directory, if there are more token
			while (tok.hasMoreTokens()) {
				String directory = tok.nextToken();
				if (tok.hasMoreTokens()) {
					path = path + directory + SLASH;
					if (!mPackagePathSet.contains(path)) {
						// it is only an intermediate directory
						mPackagePathSet.add(path);
						insertDirectory(path, null);
					}
				} else {
					if (mediaType != null || !mPackagePathSet.contains(packagePath)) {
						mPackagePathSet.add(packagePath);
						insert((byte[]) null, packagePath, mediaType);
					}
				}
			}
		}
	}

	/**
	 * add a directory to the OdfPackage
	 */
	private void insertDirectory(String packagePath, String mediaType) {
		packagePath = normalizeFilePath(packagePath);

		// If empty string OR not trailing SLASH
		if ((packagePath.length() < 1)
				|| (packagePath.charAt(packagePath.length() - 1) != '/')) {
			packagePath = packagePath + SLASH;
		}
		insert((byte[]) null, packagePath, mediaType);

	}

	/**
	 * Insert DOM tree into OdfPackage. An existing file will be replaced.
	 *
	 * @param fileDOM
	 *            - XML DOM tree to be inserted as file. Null would remove the file from the package, not the directory it contains even if it was the last file.
	 * @param packagePath
	 *            - relative filePath where the DOM tree should be inserted as
	 *            XML file
	 * @param mediaType
	 *            - media type of stream. Set to null if unknown
	 * @throws java.lang.Exception
	 *             when the DOM tree could not be inserted
	 */
	public void insert(Document fileDOM, String packagePath, String mediaType) {
		packagePath = normalizeFilePath(packagePath);
		if (mediaType == null) {
			mediaType = XML_MEDIA_TYPE;
		}
		if (fileDOM == null) {
			mPkgDoms.remove(packagePath);
		} else {
			mPkgDoms.put(packagePath, fileDOM);
		}
		addPathToZip(packagePath, mediaType);
		// remove byte array version of new DOM
		mPkgData.remove(packagePath);
		// remove temp file version of new DOM
		mPkgTempFiles.remove(packagePath);
	}

	private void addPathToZip(String packagePath, String mediaType) {
		if (!OdfPackage.OdfFile.MANIFEST.packagePath.equals(packagePath)) {
			if (mManifestEntries == null) {
				try {
					parseManifest();
				} catch (Exception e) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE,
							"The package manifest.xml could not be parsed!", e);
				}
			}
			if (mManifestEntries.get(packagePath) == null) {
				OdfFileEntry fileEntry = new OdfFileEntry(packagePath,
						mediaType);
				mManifestEntries.put(packagePath, fileEntry);
				mManifestPathList.add(packagePath);
			} else if (mediaType != null) {
				OdfFileEntry fileEntry = mManifestEntries.get(packagePath);
				fileEntry.setMediaTypeString(mediaType);
			}
		} else {
			// we take information from given META-INF/manifest.xml
			try {
				parseManifest();
			} catch (Exception e) {
				Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE,
						"The package manifest.xml could not be parsed!", e);
			}
		}
		// creates sub directories in the package for the given DOM
		if (!mPackagePathSet.contains(packagePath)) {
			insertDirectories(packagePath, mediaType);
		}
		// try to get the ZipEntry from our cache
		ZipEntry ze = mZipEntries.get(packagePath);
		if (ze == null) {
			ze = new ZipEntry(packagePath);
			ze.setMethod(ZipEntry.DEFLATED);
			mZipEntries.put(packagePath, ze);
		}
		if (fileNeedsCompression(packagePath)) {
			ze.setMethod(ZipEntry.STORED);
		}
		entryUpdate(packagePath);
	}

	/**
	 * returns true if a DOM tree has been requested for given sub-content of
	 * OdfPackage
	 * 
	 * @param packagePath
	 *            - a path inside the OdfPackage eg to a content.xml stream
	 * @return - wether the package class internally has a DOM representation
	 *         for the given path
	 */
	boolean isDomCached(String packagePath) {
		return (mPkgDoms.get(packagePath) != null);
	}

	/**
	 * Gets org.w3c.dom.Document for XML file contained in package.
	 * 
	 * @param packagePath
	 *            - a path inside the OdfPackage eg to a content.xml stream
	 * @return an org.w3c.dom.Document
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws Exception
	 * @throws IllegalArgumentException
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public Document getDom(String packagePath) throws SAXException,
			ParserConfigurationException, Exception, IllegalArgumentException,
			TransformerConfigurationException, TransformerException {

		Document doc = mPkgDoms.get(packagePath);
		if (doc != null) {
			return doc;
		}

		InputStream is = getInputStream(packagePath);

		// We depend on Xerces. So we just go ahead and create a Xerces DBF,
		// without
		// forcing everything else to do so.
		DocumentBuilderFactory factory = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);

		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setEntityResolver(getEntityResolver());

		String uri = getBaseURI() + packagePath;

		// if (mErrorHandler != null) {
		// builder.setErrorHandler(mErrorHandler);
		// }

		InputSource ins = new InputSource(is);
		ins.setSystemId(uri);

		doc = builder.parse(ins);

		if (doc != null) {
			mPkgDoms.put(packagePath, doc);
			// mPkgData.remove(packagePath);
		}
		return doc;
	}

	/**
	 * Inserts InputStream into an OdfPackage. An existing file will be
	 * replaced.
	 * 
	 * @param sourceURI
	 *            - the source URI to the file to be inserted into the package.
	 * @param mediaType
	 *            - media type of stream. Set to null if unknown
	 * @param packagePath
	 *            - relative filePath where the tree should be inserted as XML
	 *            file
	 * @throws java.lang.Exception
	 *             In case the file could not be saved
	 */
	public void insert(URI sourceURI, String packagePath, String mediaType)
			throws Exception {
		InputStream is = null;
		if (sourceURI.isAbsolute()) {
			// if the URI is absolute it can be converted to URL
			is = sourceURI.toURL().openStream();
		} else {
			// otherwise create a file class to open the stream
			is = new FileInputStream(sourceURI.toString());
			// ToDo: (Issue 219 - PackageRefactoring) --error handling in this case! -> allow method insert(URI,
			// ppath, mtype)?
		}
		insert(is, packagePath, mediaType);
	}

	/**
	 * Inserts InputStream into an OdfPackage. An existing file will be
	 * replaced.
	 * 
	 * @param fileStream
	 *            - the stream of the file to be inserted into the ODF package.
	 * @param mediaType
	 *            - media type of stream. Set to null if unknown
	 * @param packagePath
	 *            - relative filePath where the tree should be inserted as XML
	 *            file
	 * @throws java.lang.Exception
	 *             In case the file could not be saved
	 */
	public void insert(InputStream fileStream, String packagePath,
			String mediaType) throws Exception {
		packagePath = normalizeFilePath(packagePath);

		if (fileStream == null) {
			//adding a simple directory without MIMETYPE
			insert((byte[]) null, packagePath, mediaType);
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedInputStream bis = null;
			if (fileStream instanceof BufferedInputStream) {
				bis = (BufferedInputStream) fileStream;
			} else {
				bis = new BufferedInputStream(fileStream);
			}
			StreamHelper.stream(bis, baos);
			byte[] data = baos.toByteArray();
			insert(data, packagePath, mediaType);
			// image should not be stored in memory but on disc
			if ((!packagePath.endsWith(".xml"))
					&& (!packagePath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) && mUseTempFile) {
				// insertOutputStream to filesystem
				File tempFile = new File(getTempDir(), packagePath);
				File parent = tempFile.getParentFile();
				parent.mkdirs();
				OutputStream fos = new BufferedOutputStream(
						new FileOutputStream(tempFile));
				fos.write(data);
				fos.close();
				mPkgTempFiles.put(packagePath, tempFile);
				mPkgData.remove(packagePath);
			}

		}
	}

	/**
	 * Insert byte array into OdfPackage. An existing file will be replaced.
	 * 
	 * @param fileBytes
	 *            - data of the file stream to be stored in package
	 * @param mediaType
	 *            - media type of stream. Set to null if unknown
	 * @param packagePath
	 *            - relative filePath where the DOM tree should be inserted as
	 *            XML file
	 * @throws java.lang.Exception
	 *             when the DOM tree could not be inserted
	 */
	public void insert(byte[] fileBytes, String packagePath, String mediaType) {
		packagePath = normalizeFilePath(packagePath);

		if (OdfPackage.OdfFile.MEDIA_TYPE.getPath().equals(packagePath)) {
			try {
				setMediaTypeString(new String(fileBytes, "UTF-8"));
			} catch (UnsupportedEncodingException useEx) {
				Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE,
						"ODF file could not be created as string!", useEx);
			}
			return;
		}
		if (fileBytes != null) {
			mPkgData.put(packagePath, fileBytes);
		}
		addPathToZip(packagePath, mediaType);
	}

	private void insert(ZipEntry zipe, byte[] content) {
		if (content != null) {
			if (zipe.getName().equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
				try {
					mMediaType = new String(content, 0, content.length, "UTF-8");
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			} else {
				mPkgData.put(zipe.getName(), content);
			}
		}
		if (!mPackagePathSet.contains(zipe.getName())) {
			mPackagePathSet.add(zipe.getName());
		}
		mZipEntries.put(zipe.getName(), zipe);
	}

	private void insert(ZipEntry zipe, File file) {
		if (file != null) {
			mPkgTempFiles.put(zipe.getName(), file);
		}
		if (!mPackagePathSet.contains(zipe.getName())) {
			mPackagePathSet.add(zipe.getName());
		}
		mZipEntries.put(zipe.getName(), zipe);
	}

	public Map<String, OdfFileEntry> getManifestEntries() {
		if (mManifestEntries == null) {
			try {
				parseManifest();
				if (mManifestEntries == null) {
					return null;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return mManifestEntries;
		} else {
			return mManifestEntries;
		}
	}

	/**
	 * Get Manifest as String NOTE: This functionality should better be moved to
	 * a DOM based Manifest class
	 *
	 * @return the /META-INF/manifest.xml as a String
	 */
	public String getManifestAsString() {
		StringBuilder buf = new StringBuilder();

		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		buf.append("<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n");
		if (mManifestEntries == null) {
			try {
				parseManifest();
			} catch (Exception ex) {
				Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		Iterator<String> it = mManifestPathList.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String s = null;
			OdfFileEntry fileEntry = mManifestEntries.get(key);
			if (fileEntry != null) {
				buf.append(" <manifest:file-entry");
				s = fileEntry.getMediaTypeString();
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
	 * @param packagePath
	 *            relative filePath to the package content
	 * @return the unzipped package content as byte array
	 * @throws java.lang.Exception
	 */
	public byte[] getBytes(String packagePath) {
		packagePath = normalizeFilePath(packagePath);
		byte[] data = null;

		if (packagePath == null || packagePath.equals(EMPTY_STRING)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			save(baos, mBaseURI);
			return baos.toByteArray();
		}
		if (packagePath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath())) {
			if (mMediaType == null) {
				return null;
			}
			try {
				data = mMediaType.getBytes("UTF-8");
			} catch (UnsupportedEncodingException use) {
				Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, use);
				return null;
			}
		} else if (mPackagePathSet.contains(packagePath)
				&& mPkgDoms.get(packagePath) != null) {
			{
				Document dom = mPkgDoms.get(packagePath);
				if (dom instanceof OdfFileDom) {
					OdfFileDom odfDom = (OdfFileDom) dom;
					Map<String, String> nsByUri = odfDom.getNamespacesByURI();
					OdfElement root = odfDom.getRootElement();
					for (Entry<String, String> entry : nsByUri.entrySet()) {
						root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + entry.getValue(), entry.getKey());
					}
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				DOMXSImplementationSourceImpl dis = new org.apache.xerces.dom.DOMXSImplementationSourceImpl();
				DOMImplementationLS impl = (DOMImplementationLS) dis.getDOMImplementation("LS");
				LSSerializer writer = impl.createLSSerializer();

				LSOutput output = impl.createLSOutput();
				output.setByteStream(baos);

				writer.write(dom, output);
				data = baos.toByteArray();
			}
		} else if (mPackagePathSet.contains(packagePath)
				&& mPkgTempFiles.get(packagePath) != null) {
			InputStream is = null;
			ByteArrayOutputStream os = null;
			try {
				os = new ByteArrayOutputStream();
				is = new BufferedInputStream(new FileInputStream(mPkgTempFiles.get(packagePath)));
				StreamHelper.stream(is, os);
				is.close();
				os.close();
				data = os.toByteArray();
			} catch (IOException ex) {
				Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (os != null) {
						os.close();
					}
				} catch (IOException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} else if (mPackagePathSet.contains(packagePath)
				&& mPkgData.get(packagePath) != null) {
			data = mPkgData.get(packagePath);
		} else if (packagePath.equals(OdfPackage.OdfFile.MANIFEST.packagePath)) {
			if (mManifestEntries == null) {
				// manifest was not present
				return null;
			}
			String s = getManifestAsString();
			if (s == null) {
				return null;
			} else {
				try {
					data = s.getBytes("UTF-8");
				} catch (UnsupportedEncodingException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		if (data == null) { // not yet stored data; retrieve it.
			ZipEntry entry = null;
			if ((entry = mZipEntries.get(packagePath)) != null) {
				InputStream inputStream = null;
				try {
					inputStream = mZipFile.getInputStream(entry);
					if (inputStream != null) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						StreamHelper.stream(inputStream, out);
						data = out.toByteArray();
						// store for further usage; do not care about manifest: that
						// is handled exclusively
						mPkgData.put(packagePath, data);
						if (!mPackagePathSet.contains(packagePath)) {
							mPackagePathSet.add(packagePath);
						}
					}
				} catch (IOException ex) {
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

	private void addNamespaces(OdfFileDom dom) {
		dom.getXPath().getNamespaceContext();
		Element e = dom.getDocumentElement();
	}

	/**
	 * Get subcontent as InputStream
	 * 
	 * @param packagePath
	 *            of the desired stream.
	 * @return Inputstream of the ODF file within the package for the given
	 *         path.
	 * @throws Exception
	 */
	public InputStream getInputStream(String packagePath) throws Exception {

		packagePath = normalizeFilePath(packagePath);

		if (packagePath.equals(OdfPackage.OdfFile.MANIFEST.packagePath)
				&& (mManifestEntries == null)) {
			ZipEntry entry = null;
			if ((entry = mZipEntries.get(packagePath)) != null) {
				return mZipFile.getInputStream(entry);
			}
		}

		if (mPackagePathSet.contains(packagePath)
				&& mPkgTempFiles.get(packagePath) != null) {
			return new BufferedInputStream(new FileInputStream(mPkgTempFiles.get(packagePath)));
		}

		// else we always cache here and return a ByteArrayInputStream because
		// if
		// we would return ZipFile getInputStream(entry) we would not be
		// able to read 2 Entries at the same time. This is a limitation of the
		// ZipFile class.
		// As it would be quite a common thing to read the content.xml and the
		// styles.xml
		// simultanously when using XSLT on OdfPackages we want to circumvent
		// this limitation

		byte[] data = getBytes(packagePath);
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
	 * @throws java.lang.Exception
	 *             - if the package could not be read
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
					BufferedInputStream bis = new BufferedInputStream(is,
							StreamHelper.PAGE_SIZE);
					BufferedOutputStream bos = new BufferedOutputStream(os,
							StreamHelper.PAGE_SIZE);
					StreamHelper.stream(bis, bos);
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
	 * Insert the OutputStream for into OdfPackage. An existing file will be
	 * replaced.
	 * 
	 * @param packagePath
	 *            - relative filePath where the DOM tree should be inserted as
	 *            XML file
	 * @return outputstream for the data of the file to be stored in package
	 * @throws java.lang.Exception
	 *             when the DOM tree could not be inserted
	 */
	public OutputStream insertOutputStream(String packagePath) throws Exception {
		return insertOutputStream(packagePath, null);
	}

	/**
	 * Insert the OutputStream - to be filled after method - when stream is
	 * closed into OdfPackage. An existing file will be replaced.
	 * 
	 * @param packagePath
	 *            - relative filePath where the DOM tree should be inserted as
	 *            XML file
	 * @param mediaType
	 *            - media type of stream
	 * @return outputstream for the data of the file to be stored in package
	 * @throws java.lang.Exception
	 *             when the DOM tree could not be inserted
	 */
	public OutputStream insertOutputStream(String packagePath, String mediaType)
			throws Exception {
		packagePath = normalizeFilePath(packagePath);
		final String fPath = packagePath;
		final OdfFileEntry fFileEntry = getFileEntry(packagePath);
		final String fMediaType = mediaType;

		ByteArrayOutputStream baos = new ByteArrayOutputStream() {

			@Override
			public void close() {
				try {
					byte[] data = this.toByteArray();
					if (fMediaType == null || fMediaType.length() == 0) {
						insert(data, fPath, fFileEntry == null ? null
								: fFileEntry.getMediaTypeString());
					} else {
						insert(data, fPath, fMediaType);
					}
					super.close();
				} catch (Exception ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		};
		return baos;
	}

	/** Removes the singel given file
	ToDo: (Issue 219 - PackageRefactoring) --Also directory structures should be able to be removed (or copied & moved)
	 */
	public void remove(String packagePath) {
		Map<String, OdfFileEntry> manifestEntries = getManifestEntries();
		if (mManifestPathList != null && mManifestPathList.contains(packagePath)) {
			mManifestPathList.remove(packagePath);
		}
		if (manifestEntries != null && manifestEntries.containsKey(packagePath)) {
			manifestEntries.remove(packagePath);
		}
		if (mZipEntries != null && mZipEntries.containsKey(packagePath)) {
			mZipEntries.remove(packagePath);
		}
		if (mPkgTempFiles != null && mPkgTempFiles.containsKey(packagePath)) {
			File file = mPkgTempFiles.remove(packagePath);
			file.delete();
		}
		if (mPackagePathSet != null && mPackagePathSet.contains(packagePath)) {
			mPackagePathSet.remove(packagePath);
		}
	}

	/**
	 * Get Temp Directory. Create new temp directory on demand and register it
	 * for removal by garbage collector
	 */
	private File getTempDir() throws Exception {
		if (mTempDir == null) {
			mTempDir = TempDir.newTempOdfDirectory("ODF", mTempDirParent);
			mFinalize = new OdfFinalizablePackage(mTempDir);
		}
		return mTempDir;
	}

	/**
	 * Encoded XML Attributes
	 */
	private String encodeXMLAttributes(String s) {
		String r = s.replaceAll("\"", "&quot;");
		r = r.replaceAll("'", "&apos;");
		return r;
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
			mManifestPathList = new LinkedList<String>();
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
		public void endPrefixMapping(String prefix) throws SAXException {
		}

		/**
		 * Receive notification of the beginning of an element.
		 */
		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {

			if (localName.equals("file-entry")) {
				_currentFileEntry = new OdfFileEntry();
				_currentFileEntry.setPath(atts.getValue("manifest:full-path"));
				_currentFileEntry.setMediaTypeString(atts.getValue("manifest:media-type"));
				if (atts.getValue("manifest:size") != null) {
					try {
						_currentFileEntry.setSize(Integer.parseInt(atts.getValue("manifest:size")));
					} catch (NumberFormatException nfe) {
						throw new SAXException("not a number: "
								+ atts.getValue("manifest:size"));
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
						throw new SAXException("not a number: "
								+ atts.getValue("manifest:iteration-count"));
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
		public void endElement(String namespaceURI, String localName,
				String qName) throws SAXException {
			if (localName.equals("file-entry")) {
				if (_currentFileEntry.getPath() != null) {
					getManifestEntries().put(_currentFileEntry.getPath(),
							_currentFileEntry);
				}
				mManifestPathList.add(_currentFileEntry.getPath());
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
		 * The Parser will call this method before opening any external entity
		 * except the top-level document entity (including the external DTD
		 * subset, external entities referenced within the DTD, and external
		 * entities referenced within the document element): the application may
		 * request that the parser resolve the entity itself, that it use an
		 * alternative URI, or that it use an entirely different input source.
		 */
		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException {

			// This deactivates the attempt to loadPackage the Math DTD
			if (publicId != null
					&& publicId.startsWith("-//OpenOffice.org//DTD Modified W3C MathML")) {
				return new InputSource(new ByteArrayInputStream(
						"<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
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
								Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
							} finally {
								try {
									if (in != null) {
										in.close();
									}
								} catch (IOException ex) {
									Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
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
						Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
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
	 * Get EntityResolver to be used in XML Parsers which can resolve content
	 * inside the OdfPackage
	 * 
	 * @return a SAX EntityResolver
	 */
	public EntityResolver getEntityResolver() {
		if (mResolver == null) {
			mResolver = new Resolver();
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
			mResolver = new Resolver();
		}
		return mResolver;
	}

	private static String getBaseURIFromFile(File file) throws Exception {
		String baseURI = file.getCanonicalFile().toURI().toString();
		baseURI = BACK_SLASH.matcher(baseURI).replaceAll("/");
		return baseURI;
	}

	private class ZipHelper {

		private ZipFile mZipFile = null;
		private byte[] mZipBuffer = null;

		public ZipHelper(ZipFile zipFile) {
			mZipFile = zipFile;
			mZipBuffer = null;
		}

		public ZipHelper(byte[] buffer) {
			mZipBuffer = buffer;
			mZipFile = null;
		}

		public Enumeration<? extends ZipEntry> entries() throws IOException {
			if (mZipFile != null) {
				return mZipFile.entries();
			} else {
				Vector<ZipEntry> list = new Vector<ZipEntry>();
				ZipInputStream inputStream = new ZipInputStream(
						new ByteArrayInputStream(mZipBuffer));
				ZipEntry zipEntry = inputStream.getNextEntry();
				while (zipEntry != null) {
					list.add(zipEntry);
					zipEntry = inputStream.getNextEntry();
				}
				inputStream.close();
				return list.elements();
			}
		}

		public InputStream getInputStream(ZipEntry entry) throws IOException {
			if (mZipFile != null) {
				return mZipFile.getInputStream(entry);
			} else {
				ZipInputStream inputStream = new ZipInputStream(
						new ByteArrayInputStream(mZipBuffer));
				ZipEntry zipEntry = inputStream.getNextEntry();
				while (zipEntry != null) {
					if (zipEntry.getName().equalsIgnoreCase(entry.getName())) {
						return readAsInputStream(inputStream);
					}
					zipEntry = inputStream.getNextEntry();
				}
				return null;
			}
		}

		private InputStream readAsInputStream(ZipInputStream inputStream)
				throws IOException {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			if (outputStream != null) {
				byte[] buf = new byte[4096];
				int r = 0;
				while ((r = inputStream.read(buf, 0, 4096)) > -1) {
					outputStream.write(buf, 0, r);
				}
				inputStream.close();
			}
			return new ByteArrayInputStream(outputStream.toByteArray());

		}

		public void close() throws IOException {
			if (mZipFile != null) {
				mZipFile.close();
			} else {
				mZipBuffer = null;
			}
		}
	}

	/**
	 * Ensures that the given file path is not null nor empty and not an external reference
	 *	<ol>
	 *	<li>All backslashes "\" are exchanged by slashes "/"</li>
	 *  <li>Any substring "/../", "/./" or "//" will be removed</li>
	 *	<li>A prefix "./" and "../" will be removed</li>
	 *  </ol>
	 *
	 * @throws IllegalArgumentException If the path is NULL, empty or an external path (e.g. starting with "../" is given).
	 *  None relative URLs will NOT throw an exception.
	 * @return the normalized path or the URL
	 */
	protected static String normalizeFilePath(String filePath) {
		if (filePath.equals(EMPTY_STRING)) {
			String errMsg = "The packagePath given by parameter is an empty string!";
			Logger.getLogger(OdfPackage.class.getName()).severe(errMsg);
			throw new IllegalArgumentException(errMsg);
		} else {
			return normalizePath(filePath);
		}
	}

	/**
	 * Ensures the given directory path is not null nor an external reference.
	 * An empty path and "/" are both mapped to the root directory/document.
	 *
	 * NOTE: Although ODF only refer the "/" as root,
	 * the empty path aligns more adequate with the file system concept.
	 *
	 * To ensure the given directory path within the package can be used as a key (is unique for the Package) the path will be normalized.
	 * @see #normalizeFilePath(String)
	 * In addition to the file path normalization a trailing slash will be used for directories.
	 */
	protected static String normalizeDirectoryPath(String directoryPath) {
		directoryPath = normalizePath(directoryPath);
		// if not the root document - which is from ODF view a '/' and no trailing '/'
		if (!directoryPath.equals(OdfPackageDocument.ROOT_DOCUMENT_PATH) && !directoryPath.endsWith("/")) {
			// add a trailing slash
			directoryPath = directoryPath + "/";
		}
		return directoryPath;
	}

	/** Normalizes both directory and file path */
	private static String normalizePath(String path) {
		if (path == null) {
			String errMsg = "The packagePath given by parameter is NULL!";
			Logger.getLogger(OdfPackage.class.getName()).severe(errMsg);
			throw new IllegalArgumentException(errMsg);
		} else if (!isExternalReference(path)) {
			if (path.indexOf('\\') != -1) {
				path = path.replace('\\', '/');
			}
			// exchange all "/../" substrings with "/"
			if (path.indexOf("/../") != -1) {
				path = path.replace("/../", "/");
			}
			// exchange all "/./" substrings with "/"
			if (path.indexOf("/./") != -1) {
				path = path.replace("//", "/");
			}
			// exchange all "//" substrings with "/"
			while (path.indexOf("//") != -1) {
				path = path.replace("//", "/");
			}
			// remove starting "./" unless it stands alone.
			if (path.length() != 2 && path.startsWith("./")) {
				path = path.substring(2);
			}			
		}
		return path;
	}

	/**
	 * Checks if the given reference is a reference, which points outside the
	 * ODF package
	 *
	 * @param fileRef
	 *            the file reference to be checked
	 * @return true if the reference is an package external reference
	 */
	private static boolean isExternalReference(String fileRef) {
		boolean isExternalReference = false;
		// if the fileReference is a external relative filePath..
		if (fileRef.startsWith(TWO_DOTS)
				|| // or absolute filePath AND not root document
				fileRef.startsWith(SLASH) && !fileRef.equals(SLASH)
				|| // or absolute IRI
				fileRef.contains(COLON)) {
			isExternalReference = true;
		}
		return isExternalReference;
	}
}
