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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.odftoolkit.odfdom.pkg.OdfAttribute;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.doc.OdfDocument.ScriptType;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfMetaDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSettingsDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.meta.OdfOfficeMeta;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.Duration;
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
	private OdfContentDom mContentDom;
	private OdfStylesDom mStylesDom;
	private OdfMetaDom mMetaDom;
	private OdfSettingsDom mSettingsDom;
	private OdfOfficeMeta mOfficeMeta;
	private StringBuilder mCharsForTextNode = new StringBuilder();
	private XPath mXPath;
	private long documentOpeningTime;

	// Using static factory instead of constructor
	protected OdfDocument(OdfPackage pkg, String internalPath, OdfMediaType mediaType) {
		super(pkg, internalPath, mediaType.getMediaTypeString());
		mMediaType = mediaType;
		//set document opening time.
		documentOpeningTime = System.currentTimeMillis();
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
	 * Loads an OpenDocument from the given resource. NOTE: Initial meta data will be added in this method.
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
		OdfDocument newDocument = newDocument(pkg, ROOT_DOCUMENT_PATH, odfMediaType);
		//add initial meta data to new document.
		initializeMetaData(newDocument);
		return newDocument;
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
		return loadTemplate(documentTemplate, odfMediaType);
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
	 * Return the ODF type-based content DOM of the content.xml
	 * @return ODF type-based content DOM or null if no content.xml exists.
	 * @throws Exception if content DOM could not be initialized
	 */
	public OdfContentDom getContentDom() throws Exception {
		if (mContentDom == null) {
			mContentDom = (OdfContentDom) getFileDom(OdfXMLFile.CONTENT);
		}
		return mContentDom;
	}

	/**
	 * Return the ODF type-based styles DOM of the styles.xml
	 * @return ODF type-based styles DOM or null if no styles.xml exists.
	 * @throws Exception if styles DOM could not be initialized
	 */
	public OdfStylesDom getStylesDom() throws Exception {
		if (mStylesDom == null) {
			mStylesDom = (OdfStylesDom) getFileDom(OdfXMLFile.STYLES);
		}
		return mStylesDom;
	}

	/**
	 * Return the ODF type-based metadata DOM of the meta.xml
	 * 
	 * @return ODF type-based meta DOM or null if no meta.xml exists.
	 * @throws Exception if meta DOM could not be initialized
	 */
	public OdfMetaDom getMetaDom() throws Exception {
		if (mMetaDom == null) {
			mMetaDom = (OdfMetaDom) getFileDom(OdfXMLFile.META);
		}
		return mMetaDom;
	}

	/**
	 * Return the ODF type-based settings DOM of the settings.xml
	 *
	 * @return ODF type-based settings DOM or null if no settings.xml exists.
	 * @throws Exception if settings DOM could not be initialized
	 */
	public OdfSettingsDom getSettingsDom() throws Exception {
		if (mSettingsDom == null) {
			mSettingsDom = (OdfSettingsDom) getFileDom(OdfXMLFile.SETTINGS);
		}
		return mSettingsDom;
	}

	/**
	 * Get the meta data feature instance of the current document
	 * 
	 * @return the meta data feature instance which represent 
	 * <code>office:meta</code> in the meta.xml	 
	 */
	public OdfOfficeMeta getOfficeMetadata() {
		if (mOfficeMeta == null) {
			try {
				mOfficeMeta = new OdfOfficeMeta(getMetaDom());
			} catch (Exception ex) {
				Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return mOfficeMeta;
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
		updateMetaData();
		if (!isRootDocument()) {
			OdfDocument newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.updateMetaData();
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
		updateMetaData();
		if (!isRootDocument()) {
			OdfDocument newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.updateMetaData();
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
	//ToDo: (Issue 219 - PackageRefactoring) - Move flush as much as possible to OdfPackage, avoiding duplication of work
	private static void flushDOMsToPkg(OdfPackageDocument pkgDoc) {
		OdfDocument doc = (OdfDocument) pkgDoc;
// ToDo: Will be used for issue 60: Load & Save of previous ODF versions (ie. ODF 1.0, ODF 1.1)
//		// if one of the DOM was loaded
//		if (doc.mContentDom != null || doc.mStylesDom != null || doc.mSettingsDom != null || doc.mMetaDom != null) {
//			String odf12 = OfficeVersionAttribute.Value._1_2.toString();
//			boolean adaptVersion = false;
//			String versionAttr = null;
//			// check if the latest ODF version is being used
//			if (doc.mContentDom != null) {
//				OfficeDocumentContentElement contentElement = doc.mContentDom.getRootElement();
//				if (contentElement != null && (versionAttr = contentElement.getOfficeVersionAttribute()) == null || !versionAttr.equals(odf12)) {
//					adaptVersion = true;
//				}
//			} else if (doc.mStylesDom != null) {
//				versionAttr = null;
//				OfficeDocumentStylesElement stylesElement = doc.mStylesDom.getRootElement();
//				if (stylesElement != null && (versionAttr = stylesElement.getOfficeVersionAttribute()) == null || !versionAttr.equals(odf12)) {
//					adaptVersion = true;
//				}
//			} else if (doc.mMetaDom != null) {
//				versionAttr = null;
//				OfficeDocumentMetaElement metaElement = doc.mMetaDom.getRootElement();
//				if (metaElement != null && (versionAttr = metaElement.getOfficeVersionAttribute()) == null || !versionAttr.equals(odf12)) {
//					adaptVersion = true;
//				}
//			} else if (doc.mSettingsDom != null) {
//				versionAttr = null;
//				OfficeDocumentSettingsElement settingsElement = doc.mSettingsDom.getRootElement();
//				if (settingsElement != null && (versionAttr = settingsElement.getOfficeVersionAttribute()) == null || !versionAttr.equals(odf12)) {
//					adaptVersion = true;
//				}
//			}
		try {
// ToDo: Will be used for issue 60: Load & Save of previous ODF versions (ie. ODF 1.0, ODF 1.1)
//				if (adaptVersion) {
//					// change ODF version of content.xml and flush the DOM
//					OdfContentDom contentDom = doc.getContentDom();
//					if (contentDom != null) {
//						OfficeDocumentContentElement contentElement = contentDom.getRootElement();
//						if (contentElement != null) {
//							contentElement.setOfficeVersionAttribute(odf12);
//						}
//					}
//
//					// change ODF version of styles.xml and flush the DOM
//					OdfStylesDom stylesDom = doc.getStylesDom();
//					if (stylesDom != null) {
//						OfficeDocumentStylesElement stylesElement = stylesDom.getRootElement();
//						if (stylesElement != null) {
//							stylesElement.setOfficeVersionAttribute(odf12);
//						}
//					}
//
//					// change ODF version of meta.xml and flush the DOM
//					OdfMetaDom metaDom = doc.getMetaDom();
//					if (metaDom != null) {
//						OfficeDocumentMetaElement metaElement = metaDom.getRootElement();
//						if (metaElement != null) {
//							metaElement.setOfficeVersionAttribute(odf12);
//						}
//					}
//
//					// change ODF version of settings.xml and flush the DOM
//					OdfSettingsDom settingsDom = doc.getSettingsDom();
//					if (settingsDom != null) {
//						OfficeDocumentSettingsElement settingElement = settingsDom.getRootElement();
//						if (settingElement != null) {
//							settingElement.setOfficeVersionAttribute(odf12);
//						}
//					}
//				}
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
//		}
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
		OfficeBodyElement contentBody = OdfElement.findFirstChildNode(
				OfficeBodyElement.class, contentRoot);
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
		String path = getXMLFilePath(file);
		InputStream fileStream = mPackage.getInputStream(path);
		OdfFileDom fileDom = null;
		if (fileStream != null) {
			// create sax parser
			SAXParserFactory saxFactory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
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

			// initialize the file DOM
			switch (file) {
				case CONTENT:
					fileDom = new OdfContentDom(this, this.getXMLFilePath(file));
					break;
				case STYLES:
					fileDom = new OdfStylesDom(this, this.getXMLFilePath(file));
					break;
				case META:
					fileDom = new OdfMetaDom(this, this.getXMLFilePath(file));
					break;
				case SETTINGS:
					fileDom = new OdfSettingsDom(this, this.getXMLFilePath(file));
					break;
				default:
					fileDom = new OdfFileDom(this, this.getXMLFilePath(file));
					break;
			}
			Handler handler = new Handler(fileDom);
			xmlReader.setContentHandler(handler);
			InputSource xmlSource = new InputSource(fileStream);
			xmlReader.parse(xmlSource);
		}
		return fileDom;
	}

	private class Handler extends DefaultHandler {

		private static final String EMPTY_STRING = "";
		// the empty XML file to which nodes will be added
		private OdfFileDom mOdfDom;
		// the context node
		private Node mNode;        // a stack of sub handlers. handlers will be pushed on the stack whenever
		// they are required and must pop themselves from the stack when done
		private Stack<ContentHandler> mHandlerStack = new Stack<ContentHandler>();

		Handler(Node rootNode) {
			if (rootNode instanceof OdfFileDom) {
				mOdfDom = (OdfFileDom) rootNode;
			} else {
				mOdfDom = (OdfFileDom) rootNode.getOwnerDocument();
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
				element = mOdfDom.createElement(localName);
			} else {
				element = mOdfDom.createElementNS(uri, qName);
			}
			String attrQname = null;
			String attrURL = null;
			OdfAttribute attr = null;
			for (int i = 0; i < attributes.getLength(); i++) {
				attrURL = attributes.getURI(i);
				attrQname = attributes.getQName(i);
				if (attrURL.equals(EMPTY_STRING) || attrQname.equals(EMPTY_STRING)) {
					attr = mOdfDom.createAttribute(attributes.getLocalName(i));
				} else {
					if (attrQname.startsWith("xmlns:")) {
						// in case of xmlns prefix we have to create a new OdfNamespace
						mOdfDom.setNamespace(attributes.getLocalName(i), attributes.getValue(i));
					}
					// create all attributes, even namespace attributes
					attr = mOdfDom.createAttributeNS(attrURL, attrQname);
				}

				// namespace attributes will not be created and return null
				if (attr != null) {
					element.setAttributeNodeNS(attr);
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
				Text text = mOdfDom.createTextNode(mCharsForTextNode.toString());
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

	/**
	 * Insert an Image from the specified uri to the end of the OdfDocument.
	 * @param imageUri The URI of the image that will be added to the document,
	 * 				   add image stream to the package,
	 *                 in the 'Pictures/' graphic directory with the same image file name as in the URI.
	 *                 If the imageURI is relative first the user.dir is taken to make it absolute.
	 * @return         Returns the internal package path of the image, which was created based on the given URI.
	 * */
	public String newImage(URI imageUri) {
		try {
			OdfContentDom contentDom = this.getContentDom();
			OdfDrawFrame drawFrame = contentDom.newOdfElement(OdfDrawFrame.class);
			XPath xpath = contentDom.getXPath();
			if (this instanceof OdfSpreadsheetDocument) {
				TableTableCellElement lastCell = (TableTableCellElement) xpath.evaluate("//table:table-cell[last()]", contentDom, XPathConstants.NODE);
				lastCell.appendChild(drawFrame);
				drawFrame.removeAttribute("text:anchor-type");

			} else if (this instanceof OdfTextDocument) {
				TextPElement lastPara = (TextPElement) xpath.evaluate("//text:p[last()]", contentDom, XPathConstants.NODE);
				if (lastPara == null) {
					lastPara = ((OdfTextDocument) this).newParagraph();
				}
				lastPara.appendChild(drawFrame);
				drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
			} else if (this instanceof OdfPresentationDocument) {
				DrawPageElement lastPage = (DrawPageElement) xpath.evaluate("//draw:page[last()]", contentDom, XPathConstants.NODE);
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
			OfficeBodyElement officeBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);

			NodeList childList = typedContent.getChildNodes();
			for (int i = 0; i < childList.getLength(); i++) {
				if (childList.item(i) instanceof TableTableElement) {
					TableTableElement table = (TableTableElement) childList.item(i);
					if (table.getOdfAttributeValue(OdfName.newName(OdfDocumentNamespace.TABLE, "name")).equals(name)) {
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
			OfficeBodyElement officeBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, root);
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

	/**
	 * Meta data about the document will be initialized.
	 * Following metadata data is being added:
	 * <ul>
	 * <li>The initial creator name will be the Java user.name System property.</li>
	 * <li>The date and time when this document was created using the current data.</li>
	 * <li>The number of times this document has been edited.</li>
	 * <li>The default language will be the Java user.language System property.</li>
	 * </ul>
	 * @param newDoc  the OdfDocument object which need to initialize meta data.
	 * 
	 * TODO:This method will be moved to OdfMetadata class. 
	 *      see http://odftoolkit.org/bugzilla/show_bug.cgi?id=204
	 */
	private static void initializeMetaData(OdfDocument newDoc) {
		OdfOfficeMeta metaData = newDoc.getOfficeMetadata();
		// add initial-creator info.
		String creator = System.getProperty("user.name");
		metaData.setInitialCreator(creator);
		// add creation-date info.
		Calendar calendar = Calendar.getInstance();
		metaData.setCreationDate(calendar);
		// add editing-cycles info.
		metaData.setEditingCycles(0);
		// add language info.
		String language = System.getProperty("user.language");
		if (language != null) {
			metaData.setLanguage(language);
		}
	}

	/**
	 * Update document meta data in the ODF document. Following metadata data is
	 * being updated:
	 * <ul>
	 * <li>The name of the person who last modified this document will be the Java user.name System property</li>
	 * <li>The date and time when the document was last modified using current data</li>
	 * <li>The number of times this document has been edited is incremented by 1</li>
	 * <li>The total time spent editing this document</li>
	 * </ul>
	 * 
	 * TODO:This method will be moved to OdfMetadata class. 
	 *      see http://odftoolkit.org/bugzilla/show_bug.cgi?id=204
	 */
	private void updateMetaData() {
		if (mMetaDom != null) {
			OdfOfficeMeta metaData = getOfficeMetadata();
			String creator = System.getProperty("user.name");
			// update creator info.
			metaData.setCreator(creator);
			// update date info.
			Calendar calendar = Calendar.getInstance();
			metaData.setDcdate(calendar);
			// update editing-cycles info.
			Integer cycle = metaData.getEditingCycles();
			if (cycle != null) {
				metaData.setEditingCycles(++cycle);
			} else {
				metaData.setEditingCycles(1);
			}
			// update editing-duration info.
			long editingDuration = calendar.getTimeInMillis() - documentOpeningTime;
			editingDuration = (editingDuration < 1) ? 1 : editingDuration;
			try {
				DatatypeFactory aFactory = DatatypeFactory.newInstance();
				metaData.setEditingDuration(new Duration(aFactory.newDurationDayTime(editingDuration)));
			} catch (DatatypeConfigurationException e) {
				Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE,
						"editing duration update fail as DatatypeFactory can not be instanced", e);
			}
		}
	}

	// /////////////////
	// Following is the implementation of locale settings
	// ////////////////

	/**
	 * <p>
	 * Unicode characters are in general divided by office applications into
	 * three different types:
	 * 
	 * <p>
	 * 1) There is CJK: the Chinese, Japanese and Korean script (also old
	 * Vietnamese belong to this group). See
	 * http://en.wikipedia.org/wiki/CJK_characters
	 * 
	 * <p>
	 * 2) There is CTL: Complex Text Layout, which uses BIDI algorithms and/or
	 * glyph modules. See http://en.wikipedia.org/wiki/Complex_Text_Layout
	 * 
	 * <p>
	 * 3) And there is all the rest, which was once called by MS Western.
	 */
	public enum ScriptType {
		/**
		 * Western language
		 */
		WESTERN,
		/**
		 * Chinese, Japanese and Korean
		 */
		CJK,
		/**
		 * Complex Text Layout language
		 */
		CTL;

	}

	private final static HashSet<String> CJKLanguage = new HashSet<String>();
	private final static HashSet<String> CTLLanguage = new HashSet<String>();
	{
		CJKLanguage.add("zh"); // LANGUAGE_CHINES
		CJKLanguage.add("ja"); // LANGUAGE_JAPANESE
		CJKLanguage.add("ko"); // LANGUAGE_KOREANE

		CTLLanguage.add("am"); // LANGUAGE_AMHARIC_ETHIOPIA
		CTLLanguage.add("ar"); // LANGUAGE_ARABIC_SAUDI_ARABIA
		CTLLanguage.add("as"); // LANGUAGE_ASSAMESE
		CTLLanguage.add("bn"); // LANGUAGE_BENGALI
		CTLLanguage.add("bo"); // LANGUAGE_TIBETAN
		CTLLanguage.add("brx");// LANGUAGE_USER_BODO_INDIA
		CTLLanguage.add("dgo");// LANGUAGE_USER_DOGRI_INDIA
		CTLLanguage.add("dv"); // LANGUAGE_DHIVEHI
		CTLLanguage.add("dz"); // LANGUAGE_DZONGKHA
		CTLLanguage.add("fa"); // LANGUAGE_FARSI
		CTLLanguage.add("gu"); // LANGUAGE_GUJARATI
		CTLLanguage.add("he"); // LANGUAGE_HEBREW
		CTLLanguage.add("hi"); // LANGUAGE_HINDI
		CTLLanguage.add("km"); // LANGUAGE_KHMER
		CTLLanguage.add("kn"); // LANGUAGE_KANNADA
		CTLLanguage.add("ks"); // LANGUAGE_KASHMIRI
		CTLLanguage.add("ku"); // LANGUAGE_USER_KURDISH_IRAQ
		CTLLanguage.add("lo"); // LANGUAGE_LAO
		CTLLanguage.add("mai");// LANGUAGE_USER_MAITHILI_INDIA
		CTLLanguage.add("ml"); // LANGUAGE_MALAYALAM
		CTLLanguage.add("mn"); // LANGUAGE_MONGOLIAN_MONGOLIAN
		CTLLanguage.add("mni");// LANGUAGE_MANIPURI
		CTLLanguage.add("mr"); // LANGUAGE_MARATHI
		CTLLanguage.add("my"); // LANGUAGE_BURMESE
		CTLLanguage.add("ne"); // LANGUAGE_NEPALI
		CTLLanguage.add("or"); // LANGUAGE_ORIYA
		CTLLanguage.add("pa"); // LANGUAGE_PUNJABI
		CTLLanguage.add("sa"); // LANGUAGE_SANSKRIT
		CTLLanguage.add("sd"); // LANGUAGE_SINDHI
		CTLLanguage.add("si"); // LANGUAGE_SINHALESE_SRI_LANKA
		CTLLanguage.add("syr");// LANGUAGE_SYRIAC
		CTLLanguage.add("ta"); // LANGUAGE_TAMIL
		CTLLanguage.add("te"); // LANGUAGE_TELUGU
		CTLLanguage.add("th"); // LANGUAGE_THAI
		CTLLanguage.add("ug"); // LANGUAGE_UIGHUR_CHINA
		CTLLanguage.add("ur"); // LANGUAGE_URDU
		CTLLanguage.add("yi"); // LANGUAGE_YIDDISH
	}

	/**
	 * <p>
	 * Set a locale information.
	 * <p>
	 * The locale information will affect the language and country setting of
	 * the document. Thus the font settings, the spell checkings and etc will be
	 * affected.
	 * 
	 * @param locale
	 *            - an instance of Locale
	 */
	public void setLocale(Locale locale) {
		setLocale(locale, getScriptType(locale));
	}

	private ScriptType getScriptType(Locale locale) {
		String language = locale.getLanguage();
		if (CJKLanguage.contains(language))
			return ScriptType.CJK;
		if (CTLLanguage.contains(language))
			return ScriptType.CTL;
		return ScriptType.WESTERN;

	}

	/**
	 * <p>
	 * Set a locale of a specific script type.
	 * <p>
	 * If the locale is not belone to the script type, nothing will happen.
	 * 
	 * @param locale
	 *            - Locale information
	 * @param scriptType
	 *            - The script type
	 */
	public void setLocale(Locale locale, ScriptType scriptType) {
		try {
			switch (scriptType) {
			case WESTERN:
				setWesternLanguage(locale);
				break;
			case CJK:
				setDefaultAsianLanguage(locale);
				break;
			case CTL:
				setDefaultComplexLanguage(locale);
				break;
			}
		} catch (Exception e) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE,
					"Failed to set locale", e);
		}
	}

	/**
	 * <p>
	 * Get a locale information of a specific script type.
	 * 
	 * @param scriptType
	 *            - The script type
	 * @return the Locale information
	 */
	public Locale getLocale(ScriptType scriptType) {
		try {
			switch (scriptType) {
			case WESTERN:
				return getDefaultLanguageByProperty(OdfTextProperties.Country,
						OdfTextProperties.Language);
			case CJK:
				return getDefaultLanguageByProperty(
						OdfTextProperties.CountryAsian,
						OdfTextProperties.LanguageAsian);
			case CTL:
				return getDefaultLanguageByProperty(
						OdfTextProperties.CountryComplex,
						OdfTextProperties.LanguageComplex);
			}
		} catch (Exception e) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE,
					"Failed to get locale", e);
		}
		return null;
	}

	/**
	 * This method will set the default language and country information of the
	 * document, based on the parameter of the Locale information.
	 * 
	 * @param locale
	 *            - an instance of Locale that the default language and country
	 *            will be set to.
	 * @throws Exception
	 */
	private void setWesternLanguage(Locale locale) throws Exception {
		if (getScriptType(locale) != ScriptType.WESTERN)
			return;

		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(
						OdfTextProperties.Language)) {
					style.setProperty(OdfTextProperties.Language, locale
							.getLanguage());
					style.setProperty(OdfTextProperties.Country, locale
							.getCountry());
				}
			}
		}
	}

	private Locale getDefaultLanguageByProperty(OdfStyleProperty countryProp,
			OdfStyleProperty languageProp) throws Exception {
		String lang = null, ctry = null;

		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();

		// get language and country setting from default style setting for
		// paragraph
		OdfDefaultStyle defaultStyle = styles
				.getDefaultStyle(OdfStyleFamily.Paragraph);
		if (defaultStyle != null) {
			if (defaultStyle.hasProperty(countryProp)
					&& defaultStyle.hasProperty(languageProp)) {
				ctry = defaultStyle.getProperty(countryProp);
				lang = defaultStyle.getProperty(languageProp);
				return new Locale(lang, ctry);
			}
		}
		// if no default style setting for paragraph
		// get language and country setting from other default style settings
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
		while (itera.hasNext()) {
			OdfDefaultStyle style = itera.next();
			if (style.hasProperty(countryProp)
					&& style.hasProperty(languageProp)) {
				ctry = style.getProperty(countryProp);
				lang = style.getProperty(languageProp);
				return new Locale(lang, ctry);
			}
		}
		return null;
	}

	/**
	 * This method will return an instance of Locale, which presents the default
	 * language and country information settings in this document.
	 * 
	 * @return an instance of Locale that the default language and country is
	 *         set to.
	 */

	/**
	 * This method will set the default Asian language and country information
	 * of the document, based on the parameter of the Locale information. If the
	 * Locale instance is not set a Asian language (Chinese, Traditional
	 * Chinese, Japanese and Korean, nothing will take effect.
	 * 
	 * @param locale
	 *            - an instance of Locale that the default Asian language and
	 *            country will be set to.
	 * @throws Exception
	 */
	private void setDefaultAsianLanguage(Locale locale) throws Exception {
		if (getScriptType(locale) != ScriptType.CJK)
			return;
		String user_language = locale.getLanguage();
		if (!user_language.equals(Locale.CHINESE.getLanguage())
				&& !user_language.equals(Locale.TRADITIONAL_CHINESE
						.getLanguage())
				&& !user_language.equals(Locale.JAPANESE.getLanguage())
				&& !user_language.equals(Locale.KOREAN.getLanguage()))
			return;

		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(
						OdfTextProperties.LanguageAsian)) {
					style.setProperty(OdfTextProperties.LanguageAsian, locale
							.getLanguage());
					style.setProperty(OdfTextProperties.CountryAsian, locale
							.getCountry());
				}
			}
		}
	}

	/**
	 * This method will set the default complex language and country information
	 * of the document, based on the parameter of the Locale information.
	 * 
	 * @param locale
	 *            - an instance of Locale that the default complex language and
	 *            country will be set to.
	 * @throws Exception
	 */
	private void setDefaultComplexLanguage(Locale locale) throws Exception {
		if (getScriptType(locale) != ScriptType.CTL)
			return;
		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(
						OdfTextProperties.LanguageComplex)) {
					style.setProperty(OdfTextProperties.LanguageComplex, locale
							.getLanguage());
					style.setProperty(OdfTextProperties.CountryComplex, locale
							.getCountry());
				}
			}
		}
	}

}
