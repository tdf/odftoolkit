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
package org.odftoolkit.odfdom.doc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.odftoolkit.odfdom.OdfAttribute;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.doc.office.OdfOfficeBody;
import org.odftoolkit.odfdom.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeVersionAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.meta.OdfOfficeMeta;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/** This abstract class is representing one of the possible ODF documents */
public abstract class OdfDocument extends OdfPackageDocument {
	// Static parts of file references

	private static final String SLASH = "/";
	private OdfMediaType mMediaType;
	private OdfOfficeStyles mDocumentStyles;
	private OdfFileDom mContentDom;
	private OdfFileDom mStylesDom;
	private OdfFileDom mMetaDom;
	private OdfFileDom mSettingsDom;
	private OdfOfficeMeta mOfficeMeta;
	private StringBuilder mCharsForTextNode = new StringBuilder();
	private XPath mXPath;

	// Using static factory instead of constructor
	protected OdfDocument(OdfPackage pkg, String internalPath, OdfMediaType mediaType) {
		super(pkg, internalPath, mediaType.getMediaTypeString());
		mMediaType = mediaType;
	}

	/**
	 * This enum contains all possible standardized XML ODF files of the OpenDocument document.
	 */
	public static enum OdfXMLFile {

		CONTENT("content.xml"),
		META("meta.xml"),
		SETTINGS("settings.xml"),
		STYLES("styles.xml");
		private final String mFileName;

		/**
		 * @return the file name of xml files contained in odf packages.
		 */
		public String getFileName() {
			return mFileName;
		}

		OdfXMLFile(String fileName) {
			this.mFileName = fileName;
		}
	}

	/**
	 * This enum contains all possible media types of OpenDocument documents.
	 */
	public enum OdfMediaType implements MediaType {

		CHART("application/vnd.oasis.opendocument.chart", "odc"),
		CHART_TEMPLATE("application/vnd.oasis.opendocument.chart-template", "otc"),
		//  FORMULA("application/vnd.oasis.opendocument.formula", "odf"),
		//  FORMULA_TEMPLATE("application/vnd.oasis.opendocument.formula-template", "otf"),
		//	DATABASE_FRONT_END("application/vnd.oasis.opendocument.base", "otf"),
		GRAPHICS("application/vnd.oasis.opendocument.graphics", "odg"),
		GRAPHICS_TEMPLATE("application/vnd.oasis.opendocument.graphics-template", "otg"),
		IMAGE("application/vnd.oasis.opendocument.image", "odi"),
		IMAGE_TEMPLATE("application/vnd.oasis.opendocument.image-template", "oti"),
		PRESENTATION("application/vnd.oasis.opendocument.presentation", "odp"),
		PRESENTATION_TEMPLATE("application/vnd.oasis.opendocument.presentation-template", "otp"),
		SPREADSHEET("application/vnd.oasis.opendocument.spreadsheet", "ods"),
		SPREADSHEET_TEMPLATE("application/vnd.oasis.opendocument.spreadsheet-template", "ots"),
		TEXT("application/vnd.oasis.opendocument.text", "odt"),
		TEXT_MASTER("application/vnd.oasis.opendocument.text-master", "odm"),
		TEXT_TEMPLATE("application/vnd.oasis.opendocument.text-template", "ott"),
		TEXT_WEB("application/vnd.oasis.opendocument.text-web", "oth");
		private final String mMediaType;
		private final String mSuffix;

		OdfMediaType(String mediaType, String suffix) {
			this.mMediaType = mediaType;
			this.mSuffix = suffix;
		}

		/**
		 * @return the mediatype String of this document
		 */
		public String getMediaTypeString() {
			return mMediaType;
		}

		/**
		 * @return the ODF filesuffix of this document
		 */
		public String getSuffix() {
			return mSuffix;
		}

		/**
		 *
		 * @param mediaType string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and the suffix
		 */
		public static OdfMediaType getOdfMediaType(String mediaType) {
			OdfMediaType odfMediaType = null;
			if (mediaType != null) {

				String mediaTypeShort = mediaType.substring(mediaType.lastIndexOf(".") + 1, mediaType.length());
				mediaTypeShort = mediaTypeShort.replace('-', '_').toUpperCase();
				try {
					odfMediaType = OdfMediaType.valueOf(mediaTypeShort);

				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("Given mediaType '" + mediaType + "' is either not yet supported or not an ODF mediatype!");
				}
			}
			return odfMediaType;
		}
	}

	/**
	 * Loads an OpenDocument from the given resource
	 * @param res a resource containing a package with a root document
	 * @param odfMediaType the media type of the root document
	 * @return the OpenDocument document
	 *		  or NULL if the media type is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	static protected OdfDocument loadTemplate(Resource res, OdfMediaType odfMediaType) throws Exception {
		InputStream in = res.createInputStream();
		OdfPackage pkg = null;
		try {
			pkg = OdfPackage.loadPackage(in);
		} finally {
			in.close();
		}
		return newDocument(pkg, ROOT_DOCUMENT_PATH, odfMediaType);
	}

	/**
	 * Loads an OdfDocument from the provided path.
	 *
	 * <p>OdfDocument relies on the file being available for read access over
	 * the whole lifecycle of OdfDocument.</p>
	 *
	 * @param documentPath - the path from where the document can be loaded
	 * @return the OpenDocument from the given path
	 *		  or NULL if the media type is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfDocument loadDocument(String documentPath) throws Exception {
		OdfPackage pkg = OdfPackage.loadPackage(documentPath);
		OdfMediaType odfMediaType = OdfMediaType.getOdfMediaType(pkg.getMediaTypeString());
		if (odfMediaType == null) {
			throw new IllegalArgumentException("Document contains incorrect ODF Mediatype '" + pkg.getMediaTypeString() + "'");
		}
		return newDocument(pkg, ROOT_DOCUMENT_PATH, odfMediaType);
	}

	/**
	 * Creates an OdfDocument from the OpenDocument provided by a resource Stream.
	 *
	 * <p>Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by OdfDocument, the InputStream is cached. This usually
	 * takes more time compared to the other createInternalDocument methods.
	 * An advantage of caching is that there are no problems overwriting
	 * an input file.</p>
	 *
	 * @param inStream - the InputStream of the ODF document.
	 * @return the document created from the given InputStream
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfDocument loadDocument(InputStream inStream) throws Exception {
		return loadDocument(OdfPackage.loadPackage(inStream));
	}

	/**
	 * Creates an OdfDocument from the OpenDocument provided by a File.
	 *
	 * <p>OdfDocument relies on the file being available for read access over
	 * the whole lifecycle of OdfDocument.</p>
	 *
	 * @param file - a file representing the ODF document.
	 * @return the document created from the given File
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfDocument loadDocument(File file) throws Exception {
		return loadDocument(OdfPackage.loadPackage(file));
	}

	/**
	 * Creates an OdfDocument from the OpenDocument provided by an ODF package.
	 * @param odfPackage - the ODF package containing the ODF document.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception - if the ODF document could not be created.
	 */
	public static OdfDocument loadDocument(OdfPackage odfPackage) throws Exception {
		OdfMediaType odfMediaType = OdfMediaType.getOdfMediaType(odfPackage.getMediaTypeString());
		if (odfMediaType == null) {
			throw new IllegalArgumentException("Document contains incorrect ODF Mediatype '" + odfPackage.getMediaTypeString() + "'");
		}
		return newDocument(odfPackage, ROOT_DOCUMENT_PATH, odfMediaType);
	}

	//return null if the media type can not be recognized.
	private static OdfDocument loadDocumentFromTemplate(OdfMediaType odfMediaType) throws Exception {

		final Resource documentTemplate;
		switch (odfMediaType) {
			case TEXT:
			case TEXT_TEMPLATE:
			case TEXT_MASTER:
			case TEXT_WEB:
				documentTemplate = OdfTextDocument.EMPTY_TEXT_DOCUMENT_RESOURCE;
				break;

			case SPREADSHEET:
			case SPREADSHEET_TEMPLATE:
				documentTemplate = OdfSpreadsheetDocument.EMPTY_SPREADSHEET_DOCUMENT_RESOURCE;
				break;

			case PRESENTATION:
			case PRESENTATION_TEMPLATE:
				documentTemplate = OdfPresentationDocument.EMPTY_PRESENTATION_DOCUMENT_RESOURCE;
				break;

			case GRAPHICS:
			case GRAPHICS_TEMPLATE:
				documentTemplate = OdfGraphicsDocument.EMPTY_GRAPHICS_DOCUMENT_RESOURCE;
				break;

			case CHART:
			case CHART_TEMPLATE:
				documentTemplate = OdfChartDocument.EMPTY_CHART_DOCUMENT_RESOURCE;
				break;

			case IMAGE:
			case IMAGE_TEMPLATE:
				documentTemplate = OdfImageDocument.EMPTY_IMAGE_DOCUMENT_RESOURCE;
				break;

			default:
				documentTemplate = null;
				break;
		}
		return OdfDocument.loadTemplate(documentTemplate, odfMediaType);
	}

	/**
	 * Creates one of the ODF documents based a given mediatype.
	 *
	 * @param odfMediaType The ODF Mediatype of the ODF document to be created.
	 * @return The ODF document, which mediatype dependends on the parameter or
	 *	NULL if media type were not supported.
	 */
	private static OdfDocument newDocument(OdfPackage pkg, String internalDocumentPath, OdfMediaType odfMediaType) {
		OdfDocument newDoc = null;
		switch (odfMediaType) {
			case TEXT:
				newDoc = new OdfTextDocument(pkg, internalDocumentPath, OdfTextDocument.OdfMediaType.TEXT);
				break;

			case TEXT_TEMPLATE:
				newDoc = new OdfTextDocument(pkg, internalDocumentPath, OdfTextDocument.OdfMediaType.TEXT_TEMPLATE);
				break;

			case TEXT_MASTER:
				newDoc = new OdfTextDocument(pkg, internalDocumentPath, OdfTextDocument.OdfMediaType.TEXT_MASTER);
				break;

			case TEXT_WEB:
				newDoc = new OdfTextDocument(pkg, internalDocumentPath, OdfTextDocument.OdfMediaType.TEXT_WEB);
				break;

			case SPREADSHEET:
				newDoc = new OdfSpreadsheetDocument(pkg, internalDocumentPath, OdfSpreadsheetDocument.OdfMediaType.SPREADSHEET);
				break;

			case SPREADSHEET_TEMPLATE:
				newDoc = new OdfSpreadsheetDocument(pkg, internalDocumentPath, OdfSpreadsheetDocument.OdfMediaType.SPREADSHEET_TEMPLATE);
				break;

			case PRESENTATION:
				newDoc = new OdfPresentationDocument(pkg, internalDocumentPath, OdfPresentationDocument.OdfMediaType.PRESENTATION);
				break;

			case PRESENTATION_TEMPLATE:
				newDoc = new OdfPresentationDocument(pkg, internalDocumentPath, OdfPresentationDocument.OdfMediaType.PRESENTATION_TEMPLATE);
				break;

			case GRAPHICS:
				newDoc = new OdfGraphicsDocument(pkg, internalDocumentPath, OdfGraphicsDocument.OdfMediaType.GRAPHICS);
				break;

			case GRAPHICS_TEMPLATE:
				newDoc = new OdfGraphicsDocument(pkg, internalDocumentPath, OdfGraphicsDocument.OdfMediaType.GRAPHICS_TEMPLATE);
				break;

			case CHART:
				newDoc = new OdfChartDocument(pkg, internalDocumentPath, OdfChartDocument.OdfMediaType.CHART);
				break;

			case CHART_TEMPLATE:
				newDoc = new OdfChartDocument(pkg, internalDocumentPath, OdfChartDocument.OdfMediaType.CHART_TEMPLATE);
				break;

			case IMAGE:
				newDoc = new OdfImageDocument(pkg, internalDocumentPath, OdfImageDocument.OdfMediaType.IMAGE);
				break;

			case IMAGE_TEMPLATE:
				newDoc = new OdfImageDocument(pkg, internalDocumentPath, OdfImageDocument.OdfMediaType.IMAGE_TEMPLATE);
				break;

			default:
				newDoc = null;
				break;
		}
		// returning null if MediaType is not supported
		return newDoc;
	}

	/**
	 * Returns an embedded OdfPackageDocument from the given package path.
	 *
	 * @param internalDocumentPath path to the directory of the embedded ODF document (relative to ODF package root).
	 * @return an embedded OdfPackageDocument
	 */
	public OdfDocument getEmbeddedDocument(String internalDocumentPath) {

		internalDocumentPath = normalizeDocumentPath(internalDocumentPath);
		OdfDocument embeddedDocument = (OdfDocument) mPackage.getOpenPackageDocument(internalDocumentPath);
		// if the document was not already loaded, fine mimetype and create a new instance
		if (embeddedDocument == null) {
			try {
				OdfFileEntry entry = mPackage.getFileEntry(internalDocumentPath);
				// if the document is not in the package, the return is NULL
				if (entry != null) {
					String mediaTypeOfEmbeddedDoc = entry.getMediaTypeString();
					// if there is no mediaType, the return is NULL
					if (mediaTypeOfEmbeddedDoc != null) {
						return embeddedDocument = newDocument(mPackage, internalDocumentPath, OdfMediaType.getOdfMediaType(mediaTypeOfEmbeddedDoc));
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return embeddedDocument;
	}

	/**
	 * Method returns all embedded OdfPackageDocuments, which match a valid OdfMediaType,
	 * of the current OdfPackageDocument.
	 * @return a list with all embedded documents of the current OdfPackageDocument
	 */
	// ToDo: (Issue 219 - PackageRefactoring) - Better return Path of Documents??
	public List<OdfDocument> getEmbeddedDocuments() {
		List<OdfDocument> embeddedObjects = new ArrayList<OdfDocument>();
		// ToDo: (Issue 219 - PackageRefactoring) - Algorithm enhancement:
		// Instead going through all the files for each mimetype, better
		// Check all files, which have a mimetype if it is one of the desired, perhaps start with ODF prefix
		for (OdfMediaType mediaType : OdfMediaType.values()) {
			embeddedObjects.addAll(getEmbeddedDocuments(mediaType));
		}
		return embeddedObjects;
	}

	/**
	 * Method returns all embedded OdfPackageDocuments of the current OdfPackageDocument matching the
	 * according MediaType. This is done by matching the subfolder entries of the
	 * manifest file with the given OdfMediaType.
	 * @param mediaType media type which is used as a filter
	 * @return embedded documents of the current OdfPackageDocument matching the given media type
	 */
	public List<OdfDocument> getEmbeddedDocuments(OdfMediaType mediaType) {
		String mediaTypeString = mediaType.getMediaTypeString();
		Set<String> manifestEntries = mPackage.getFileEntries();
		List<OdfDocument> embeddedObjects = new ArrayList<OdfDocument>();
		// check manifest for current embedded OdfPackageDocuments
		for (String entry : manifestEntries) {
			// with entry greater one the root document is not within
			if (entry.length() > 1 && entry.endsWith(SLASH)) {
				String entryMediaType = mPackage.getFileEntry(entry).getMediaTypeString();
				if (entryMediaType.equals(mediaTypeString)) {
					embeddedObjects.add(getEmbeddedDocument(entry));
				}
			}
		}
		return embeddedObjects;
	}

	/**
	 * Sets the media type of the OdfDocument
	 * @param odfMediaType media type to be set
	 */
	protected void setOdfMediaType(OdfMediaType odfMediaType) {
		mMediaType = odfMediaType;
		super.setMediaTypeString(odfMediaType.getMediaTypeString());
	}

	/**
	 * Gets the media type of the OdfDocument
	 */
	protected OdfMediaType getOdfMediaType() {
		return mMediaType;
	}

	/**
	 * Get the relative path for an embedded ODF document including its file name.
	 * @param file represents one of the standardized XML ODF files.
	 * @return path to embedded ODF XML file relative to ODF package root.
	 */
	protected String getXMLFilePath(OdfXMLFile file) {
		return getDocumentPackagePath() + file.mFileName;
	}

	/**
	 * Gets the ODF content.xml file as stream.
	 * @return - a stream of the ODF content 'content.xml' file
	 * @throws java.lang.Exception - if the stream can not be extracted
	 */
	public InputStream getContentStream() throws Exception {
		String path = getXMLFilePath(OdfXMLFile.CONTENT);
		return mPackage.getInputStream(path);
	}

	/**
	 * Gets the ODF style.xml file as stream.
	 *
	 * @return - a stream of the ODF style 'styles.xml' file
	 * @throws java.lang.Exception - if the stream can not be extracted
	 */
	public InputStream getStylesStream() throws Exception {
		return mPackage.getInputStream(getXMLFilePath(OdfXMLFile.STYLES));
	}

	/**
	 * Gets the ODF settings.xml file as stream.
	 *
	 * @return - a stream of the ODF settings 'setting.xml' file
	 * @throws java.lang.Exception - if the stream can not be extracted
	 */
	public InputStream getSettingsStream() throws Exception {
		return mPackage.getInputStream(getXMLFilePath(OdfXMLFile.SETTINGS));
	}

	/**
	 * Gets the ODF metadata.xml file as stream.
	 *
	 * @return - a stream of the ODF metadata 'meta.xml' file
	 * @throws java.lang.Exception - if the stream can not be extracted
	 */
	public InputStream getMetaStream() throws Exception {
		return mPackage.getInputStream(getXMLFilePath(OdfXMLFile.META));
	}

	/**
	 * Get the URI, where this ODF document is stored.
	 * @return the URI to the ODF document. Returns null if document is not stored yet.
	 */
	public String getBaseURI() {
		return mPackage.getBaseURI();


	}

	/**
	 *
	 * @return the office:styles element from the styles dom or null if there
	 *         is no such element.
	 */
	public OdfOfficeStyles getDocumentStyles() {
		if (mDocumentStyles == null) {
			try {
				OdfFileDom stylesDom = getStylesDom();
				if (stylesDom != null) {
					mDocumentStyles = OdfElement.findFirstChildNode(OdfOfficeStyles.class, stylesDom.getFirstChild());
				} else {
					return null;
				}
			} catch (Exception ex) {
				Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return mDocumentStyles;
	}

	/**
	 *
	 * @return the office:styles element from the styles dom. If there is not
	 *         yet such an element, it is created.
	 */
	public OdfOfficeStyles getOrCreateDocumentStyles() {
		if (mDocumentStyles == null) {
			try {
				OdfFileDom stylesDom = getStylesDom();
				Node parent = stylesDom != null ? stylesDom.getFirstChild() : null;
				if (parent != null) {
					mDocumentStyles = OdfElement.findFirstChildNode(OdfOfficeStyles.class, parent);
					if (mDocumentStyles == null) {
						mDocumentStyles = stylesDom.newOdfElement(OdfOfficeStyles.class);
						parent.insertBefore(mDocumentStyles, parent.getFirstChild());
					}
				}
			} catch (Exception ex) {
				Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return mDocumentStyles;
	}

	/**
	 * Create an XPath instance to select one or more nodes from an ODF document.
	 * Therefore the namespace context is set to the OdfNamespace
	 * @return an XPath instance with namespace context set to include the standard
	 * ODFDOM prefixes.
	 */
	public XPath getXPath() {
		if (mXPath == null) {
			mXPath = XPathFactory.newInstance().newXPath();
			// could take any Namespace..
			mXPath.setNamespaceContext(OdfNamespace.newNamespace(OdfNamespaceNames.OFFICE));
		}
		return mXPath;
	}

	/**
	 * Return the ODF type-based content DOM of the content.xml
	 * @return ODF type-based content DOM
	 * @throws Exception if content DOM could not be initialized
	 */
	public OdfFileDom getContentDom() throws Exception {
		if (mContentDom == null) {
			mContentDom = getFileDom(OdfXMLFile.CONTENT);
		}
		return mContentDom;
	}

	/**
	 * Return the ODF type-based styles DOM of the styles.xml
	 * @return ODF type-based styles DOM
	 * @throws Exception if styles DOM could not be initialized
	 */
	public OdfFileDom getStylesDom() throws Exception {
		if (mStylesDom == null) {
			mStylesDom = getFileDom(OdfXMLFile.STYLES);
		}
		return mStylesDom;
	}

	/**
	 * Return the ODF type-based metadata DOM of the meta.xml
	 * 
	 * @return ODF type-based meta DOM
	 * @throws Exception if meta DOM could not be initialized
	 */
	public OdfFileDom getMetaDom() throws Exception {
		if (mMetaDom == null) {
			mMetaDom = getFileDom(OdfXMLFile.META);
		}
		return mMetaDom;
	}

	/**
	 * Return the ODF type-based settings DOM of the settings.xml
	 *
	 * @return ODF type-based settings DOM
	 * @throws Exception if settings DOM could not be initialized
	 */
	public OdfFileDom getSettingsDom() throws Exception {
		if (mSettingsDom == null) {
			mSettingsDom = getFileDom(OdfXMLFile.SETTINGS);
		}
		return mSettingsDom;
	}

	/**
	 * Get the meta data feature instance of the current document
	 * 
	 * @return the meta data feature instance which represent 
	 * <code>office:meta</code> in the meta.xml
	 * @throws Exception if the file meta DOM could not be created.
	 */
	public OdfOfficeMeta getOfficeMetadata() throws Exception {
		if (mOfficeMeta == null) {
			mOfficeMeta = new OdfOfficeMeta(getMetaDom());
		}
		return mOfficeMeta;
	}

	/**
	 * Save the document to given path.
	 * 
	 * <p>When save the embedded document to a stand alone document,
	 * all the file entries of the embedded document will be copied to a new document package.
	 * If the embedded document is outside of the current document directory, 
	 * you have to embed it to the sub directory and refresh the link of the embedded document.
	 * You should reload it from the given path to get the saved embedded document.
	 * 
	 * @param path - the path to the file
	 * @throws java.lang.Exception  if the document could not be saved
	 */
	public void save(String path) throws Exception {
		File f = new File(path);
		this.save(f);
	}

	/**
	 * Save the document to an OutputStream. Delegate to the root document
	 * and save possible embedded OdfDocuments.
	 *
	 * <p>If the input file has been cached (this is the case when loading from an
	 * InputStream), the input file can be overwritten.</p>
	 *
	 * <p>If not, the OutputStream may not point to the input file! Otherwise
	 * this will result in unwanted behaviour and broken files.</p>
	 *
	 * <p>When save the embedded document to a stand alone document,
	 * all the file entries of the embedded document will be copied to a new document package.
	 * If the embedded document is outside of the current document directory,
	 * you have to embed it to the sub directory and refresh the link of the embedded document.
	 * you should reload it from the stream to get the saved embedded document.
	 *
	 * @param out - the OutputStream to write the file to
	 * @throws java.lang.Exception  if the document could not be saved
	 */
	public void save(OutputStream out) throws Exception {
		//2DO FLUSH AND SAVE IN PACKAGE
		flushDescendantDOMsToPkg(this);
		if (!isRootDocument()) {
			OdfDocument newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.mPackage.save(out);
			// ToDo: (Issue 219 - PackageRefactoring) - Return the document, when not closing!
			// Should we close the sources now? User will never receive the open package!


		} else {
			//2DO MOVE CACHE TO PACKAGE
//			// the root document only have to flush the DOM of all open child documents
//			flushAllDOMs();
			mPackage.save(out);
		}
	}

	/**
	 * Save the document to a given file.
	 *
	 * <p>If the input file has been cached (this is the case when loading from an
	 * InputStream), the input file can be overwritten.</p>
	 *
	 * <p>Otherwise it's allowed to overwrite the input file as long as
	 * the same path name is used that was used for loading (no symbolic link
	 * foo2.odt pointing to the loaded file foo1.odt, no network path X:\foo.odt
	 * pointing to the loaded file D:\foo.odt).</p>
	 * 
	 * <p>When saving the embedded document to a stand alone document,
	 * all files of the embedded document will be copied to a new document package.
	 * If the embedded document is outside of the current document directory, 
	 * you have to embed it to the sub directory and refresh the link of the embedded document.
	 * You should reload it from the given file to get the saved embedded document.
	 *
	 * @param file - the file to save the document
	 * @throws java.lang.Exception  if the document could not be saved
	 */
	public void save(File file) throws Exception {
		//2DO FLUSH AND SAVE IN PACKAGE
		flushDescendantDOMsToPkg(this);
		if (!isRootDocument()) {
			OdfDocument newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.mPackage.save(file);
			// ToDo: (Issue 219 - PackageRefactoring) - Return the document, when not closing!
			// Should we close the sources now? User will never receive the open package!
		} else {
			this.mPackage.save(file);
		}
	}

	/**
	 * Close the OdfPackage and release all temporary created data.
	 * Acter execution of this method, this class is no longer usable.
	 * Do this as the last action to free resources.
	 * Closing an already closed document has no effect.
	 * Note that this will not close any cached documents.
	 */
	public void close() {
		// set all member variables explicit to null
		mMediaType = null;
		mDocumentStyles = null;
		mContentDom = null;
		mStylesDom = null;
		mMetaDom = null;
		mSettingsDom = null;
		mOfficeMeta = null;
		mCharsForTextNode = null;
		mXPath = null;
		mPackage.close();
	}

	//XML DOM from the memory are written to the package ZIP
	private static void flushDOMsToPkg(OdfPackageDocument pkgDoc) {
		OdfDocument doc = (OdfDocument) pkgDoc;
//ToDo: (Issue 219 - PackageRefactoring) - Move flush as much as possible to OdfPackage, avoiding duplication of work
		try {
			if (doc.mContentDom != null) {
				// currently commented because of bug 51:
				// https://odftoolkit.org/bugzilla/show_bug.cgi?id=51
				// doc.optimizeAutomaticStyles();
				doc.mPackage.insert(doc.mContentDom, doc.getXMLFilePath(OdfXMLFile.CONTENT), "text/xml");
			}
			if (doc.mStylesDom != null) {
				// currently commented because of bug 51:
				// https://odftoolkit.org/bugzilla/show_bug.cgi?id=51
				// doc.optimizeAutomaticStyles();
				doc.mPackage.insert(doc.mStylesDom, doc.getXMLFilePath(OdfXMLFile.STYLES), "text/xml");
			}
			if (doc.mMetaDom != null) {
				doc.mPackage.insert(doc.mMetaDom, doc.getXMLFilePath(OdfXMLFile.META), "text/xml");
			}
			if (doc.mSettingsDom != null) {
				doc.mPackage.insert(doc.mSettingsDom, doc.getXMLFilePath(OdfXMLFile.SETTINGS), "text/xml");
			}
		} catch (Exception e) {
			Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	// flush the DOM of all descendant documents (children of the given document)
	private void flushDescendantDOMsToPkg(OdfPackageDocument parentDocument) {
		String baseDir = parentDocument.getDocumentPackagePath();
		OdfPackage pkg = parentDocument.getPackage();
		for (String odfDocPath : pkg.getOpenedPackageDocuments()) {
			if (odfDocPath.startsWith(baseDir)) {
				flushDOMsToPkg(pkg.getOpenPackageDocument(odfDocPath));
			}
		}
	}

	/**
	 * Embed an OdfPackageDocument to the current OdfPackageDocument.
	 * All the file entries of child document will be embedded as well to the current document package.
	 * @param internDestinationPath path to the directory the ODF document should be inserted (relative to ODF package root).
	 * @param sourceDocument the OdfPackageDocument to be embedded.
	 */
	public void insertDocument(OdfPackageDocument sourceDocument, String internDestinationPath) {

		// opened DOM of descendant Documents will be flashed to the previous pkg
		flushDescendantDOMsToPkg(sourceDocument);

		// Gets the OdfDocument's manifest entry info, no matter it is a independent document or an embeddedDocument.
		Map<String, OdfFileEntry> entryMapToCopy;
		if (sourceDocument.isRootDocument()) {
			entryMapToCopy = sourceDocument.getPackage().getManifestEntries();
		} else {
			entryMapToCopy = getEntriesOfChildren(sourceDocument.getPackage(), sourceDocument.getDocumentPackagePath());
		}
		//insert to package and add it to the Manifest
		internDestinationPath = sourceDocument.setDocumentPackagePath(internDestinationPath);
		Set<String> entryNameList = entryMapToCopy.keySet();
		for (String entryName : entryNameList) {
			OdfFileEntry entry = entryMapToCopy.get(entryName);
			if (entry != null) {
				try {
					// if entry is a directory (e.g. an ODF document root)
					if (entryName.endsWith(SLASH)) {
						// insert directory
						if (entryName.equals(SLASH)) {
							mPackage.insert((byte[]) null, internDestinationPath, entry.getMediaTypeString());
						} else {
							mPackage.insert((byte[]) null, internDestinationPath + entry.getPath(), entry.getMediaTypeString());
						}
					} else {
						mPackage.insert(sourceDocument.getPackage().getInputStream(entryName), (internDestinationPath + entry.getPath()), entry.getMediaTypeString());
					}
				} catch (Exception ex) {
					Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		//make sure the media type of embedded Document is right set.
		OdfFileEntry embedDocumentRootEntry = new OdfFileEntry(internDestinationPath, sourceDocument.getMediaTypeString());
		mPackage.getManifestEntries().put(internDestinationPath, embedDocumentRootEntry);
	}

	/**
	 * Optimize the styles of this document: unused styles and doubled styles
	 * are removed.
	 */
	// currently commented because of bug 51:
	// https://odftoolkit.org/bugzilla/show_bug.cgi?id=51
	private void optimizeAutomaticStyles() {
//        try {
//            OdfFileDom dom = this.getStylesDom();
//            if (dom != null) {
//                OdfOfficeAutomaticStyles auto_styles = dom.getAutomaticStyles();
//                if (auto_styles != null) {
//                    auto_styles.optimizeAutomaticStyles();
//                }
//            }
//            dom = this.getContentDom();
//            if (dom != null) {
//                OdfOfficeAutomaticStyles auto_styles = dom.getAutomaticStyles();
//                if (auto_styles != null) {
//                    auto_styles.optimizeAutomaticStyles();
//                }
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
//        }
	}
	private Resolver mResolver;

	/**
	 * get EntityResolver to be used in XML Parsers
	 * which can resolve content inside the OdfPackage
	 */
	EntityResolver getEntityResolver() {
		if (mResolver == null) {
			mResolver = new Resolver();
		}
		return mResolver;
	}

	/**
	 * get URIResolver to be used in XSL Transformations
	 * which can resolve content inside the OdfPackage
	 */
	URIResolver getURIResolver() {
		if (mResolver == null) {
			mResolver = new Resolver();
		}
		return mResolver;
	}

	/**
	 * Get the content root of a document.
	 *
	 * You may prefer to use the getContentRoot methods of subclasses of
	 * OdfDocument. Their return parameters are already casted to
	 * respective subclasses of OdfElement.
	 *
	 * @param the type of the content root, depend on the document type
	 * @return the child element of office:body, e.g. office:text for text docs
	 * @throws Exception if the file DOM could not be created.
	 */
	@SuppressWarnings("unchecked")
	<T extends OdfElement> T getContentRoot(Class<T> clazz) throws Exception {
		OdfElement contentRoot = getContentDom().getRootElement();
		OdfOfficeBody contentBody = OdfElement.findFirstChildNode(
				OdfOfficeBody.class, contentRoot);
		NodeList childs = contentBody.getChildNodes();
		for (int i = 0;
				i < childs.getLength();
				i++) {
			Node cur = childs.item(i);
			if ((cur != null) && clazz.isInstance(cur)) {
				return (T) cur;
			}
		}
		return null;
	}

	/**
	 * Get the content root of a document.
	 *
	 * You may prefer to use the getContentRoot methods of subclasses of
	 * OdfDocument.
	 * 
	 * @return the child element of office:body, e.g. office:text for text docs
	 * @throws Exception if the file DOM could not be created.
	 */
	public OdfElement getContentRoot() throws Exception {
		return getContentRoot(OdfElement.class);
	}

	/**
	 * return the office:master-styles element of this document.
	 * @return the office:master-styles element
	 */
	public OdfOfficeMasterStyles getOfficeMasterStyles() {
		try {
			OdfFileDom fileDom = getStylesDom();
			if (fileDom != null) {
				return OdfElement.findFirstChildNode(OdfOfficeMasterStyles.class, fileDom.getFirstChild());
			}
		} catch (Exception ex) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
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
				throws SAXException, IOException {
			// this deactivates the attempt to loadPackage the Math DTD
			if (publicId != null && publicId.startsWith("-//OpenOffice.org//DTD Modified W3C MathML")) {
				return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
			}
			if (systemId != null) {
				if ((mPackage.getBaseURI() != null) && systemId.startsWith(mPackage.getBaseURI())) {
					if (systemId.equals(mPackage.getBaseURI())) {
						InputStream in = null;
						try {
							in = mPackage.getInputStream();
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
						if (systemId.length() > mPackage.getBaseURI().length() + 1) {
							InputStream in = null;
							try {
								String path = systemId.substring(mPackage.getBaseURI().length() + 1);
								in = mPackage.getInputStream(path);
								InputSource ins = new InputSource(in);
								ins.setSystemId(systemId);
								return ins;
							} catch (Exception ex) {
								Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
							} finally {
								try {
									in.close();
								} catch (IOException ex) {
									Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
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
					} catch (MalformedURLException me) {
						throw new SAXException(me); // Incorrect URL format used

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
				return src;
			} catch (URISyntaxException use) {
				return null;
			}
		}
	}

	private OdfFileDom getFileDom(OdfXMLFile file) throws Exception {
		// create sax parser
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		saxFactory.setNamespaceAware(true);
		saxFactory.setValidating(false);
		SAXParser parser = saxFactory.newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		// More details at http://xerces.apache.org/xerces2-j/features.html#namespaces
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		// More details at http://xerces.apache.org/xerces2-j/features.html#namespace-prefixes
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		// More details at http://xerces.apache.org/xerces2-j/features.html#xmlns-uris
		xmlReader.setFeature("http://xml.org/sax/features/xmlns-uris", true);

		// initialize the input source's content.xml
		OdfFileDom fileDom = new OdfFileDom(this, this.getXMLFilePath(file));

		String path = getXMLFilePath(file);
		InputStream fileStream = mPackage.getInputStream(path);
		if (fileStream != null) {
			Handler handler = new Handler(fileDom);
			xmlReader.setContentHandler(handler);
			InputSource contentSource = new InputSource(fileStream);
			xmlReader.parse(contentSource);
		}
		return fileDom;
	}

	private class Handler extends DefaultHandler {

		private static final String EMPTY_STRING = "";
		// the empty XML file to which nodes will be added
		private OdfFileDom mDocument;
		// the context node
		private Node mNode;        // a stack of sub handlers. handlers will be pushed on the stack whenever
		// they are required and must pop themselves from the stack when done
		private Stack<ContentHandler> mHandlerStack = new Stack<ContentHandler>();

		Handler(Node rootNode) {
			if (rootNode instanceof OdfFileDom) {
				mDocument = (OdfFileDom) rootNode;
			} else {
				mDocument = (OdfFileDom) rootNode.getOwnerDocument();
			}
			mNode = rootNode;
		}

		@Override
		public void startDocument() throws SAXException {
		}

		@Override
		public void endDocument() throws SAXException {
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			flushTextNode();
			// pop to the parent node
			mNode = mNode.getParentNode();
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			flushTextNode();
			// if there is a specilized handler on the stack, dispatch the event
			Element element = null;
			if (uri.equals(EMPTY_STRING) || qName.equals(EMPTY_STRING)) {
				element = mDocument.createElement(localName);
			} else {
				element = mDocument.createElementNS(uri, qName);
			}
			String attrPrefix = null;
			String attrURL = null;
			OdfAttribute attr = null;
			for (int i = 0; i < attributes.getLength(); i++) {
				attrURL = attributes.getURI(i);
				attrPrefix = attributes.getQName(i);
				if (attrURL.equals(EMPTY_STRING) || attrPrefix.equals(EMPTY_STRING)) {
					attr = mDocument.createAttribute(attributes.getLocalName(i));
				} else {
					attr = mDocument.createAttributeNS(attrURL, attrPrefix);
				}
				element.setAttributeNodeNS(attr);
				if (attr instanceof OfficeVersionAttribute) {
					// write out not the original value, but the version of this odf version
					attr.setValue(OfficeVersionAttribute.Value._1_2.toString());
				} else {
					// don't exit because of invalid attribute values
					try {
						// set Value in the attribute to allow validation in the attribute
						attr.setValue(attributes.getValue(i));
					} // if we detect an attribute with invalid value: remove attribute node
					catch (IllegalArgumentException e) {
						element.removeAttributeNode(attr);
					}
				}
			}
			// add the new element as a child of the current context node
			mNode.appendChild(element);
			// push the new element as the context node...
			mNode = element;
		}

		/**
		 * http://xerces.apache.org/xerces2-j/faq-sax.html#faq-2 :
		 * SAX may deliver contiguous text as multiple calls to characters,
		 * for reasons having to do with parser efficiency and input buffering.
		 * It is the programmer's responsibility to deal with that appropriately,
		 * e.g. by accumulating text until the next non-characters event.
		 */
		private void flushTextNode() {
			if (mCharsForTextNode.length() > 0) {
				Text text = mDocument.createTextNode(mCharsForTextNode.toString());
				mNode.appendChild(text);
				mCharsForTextNode.setLength(0);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (!mHandlerStack.empty()) {
				mHandlerStack.peek().characters(ch, start, length);
			} else {
				mCharsForTextNode.append(ch, start, length);
			}
		}

		@Override
		public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
			return super.resolveEntity(publicId, systemId);
		}
	}

	@Override
	public String toString() {
		return "\n" + getMediaTypeString() + " - ID: " + this.hashCode() + " "
				+ getPackage().getBaseURI();
	}
	private XPath xpath;

	/**
	 * Insert an Image from the specified uri to the end of the OdfDocument.
	 * @param imageUri The URI of the image that will be added to the document,
	 * 				   add image stream to the package,
	 *                 in the 'Pictures/' graphic directory with the same image file name as in the URI.
	 *                 If the imageURI is relative first the user.dir is taken to make it absolute.
	 * @return         Returns the internal package path of the image, which was created based on the given URI.
	 * */
	public String newImage(URI imageUri) {
		if (xpath == null) {
			xpath = XPathFactory.newInstance().newXPath();
			xpath.setNamespaceContext(OdfNamespace.newNamespace(OdfNamespaceNames.OFFICE));
		}
		try {
			OdfDrawFrame drawFrame = this.getContentDom().newOdfElement(OdfDrawFrame.class);

			if (this instanceof OdfSpreadsheetDocument) {
				TableTableCellElement lastCell = (TableTableCellElement) xpath.evaluate("//table:table-cell[last()]", this.getContentDom(), XPathConstants.NODE);
				lastCell.appendChild(drawFrame);
				drawFrame.removeAttribute("text:anchor-type");

			} else if (this instanceof OdfTextDocument) {
				TextPElement lastPara = (TextPElement) xpath.evaluate("//text:p[last()]", this.getContentDom(), XPathConstants.NODE);
				if (lastPara == null) {
					lastPara = ((OdfTextDocument) this).newParagraph();
				}
				lastPara.appendChild(drawFrame);
				drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
			} else if (this instanceof OdfPresentationDocument) {
				DrawPageElement lastPage = (DrawPageElement) xpath.evaluate("//draw:page[last()]", this.getContentDom(), XPathConstants.NODE);
				lastPage.appendChild(drawFrame);
			}
			OdfDrawImage image = (OdfDrawImage) drawFrame.newDrawImageElement();
			String imagePath = image.newImage(imageUri);
			return imagePath;
		} catch (Exception ex) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Return an instance of table feature with the specific table name.
	 * @param name of the table beeing searched for.
	 * @return an instance of table feature with the specific table name.
	 */
	public OdfTable getTableByName(String name) {
		try {
			OdfElement root = getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);

			NodeList childList = typedContent.getChildNodes();
			for (int i = 0; i < childList.getLength(); i++) {
				if (childList.item(i) instanceof TableTableElement) {
					TableTableElement table = (TableTableElement) childList.item(i);
					if (table.getOdfAttributeValue(OdfName.newName(OdfNamespaceNames.TABLE, "name")).equals(name)) {
						return OdfTable.getInstance(table);
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Return a list of table features in this document.
	 * @return a list of table features in this document.
	 */
	public List<OdfTable> getTableList() {
		List<OdfTable> tableList = new ArrayList<OdfTable>();


		try {
			OdfElement root = getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(
					OdfElement.class, officeBody);
			NodeList childList = typedContent.getChildNodes();
			for (int i = 0;
					i < childList.getLength();
					i++) {
				if (childList.item(i) instanceof TableTableElement) {
					tableList.add(OdfTable.getInstance((TableTableElement) childList.item(i)));
				}
			}
		} catch (Exception e) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return tableList;

	}
}
