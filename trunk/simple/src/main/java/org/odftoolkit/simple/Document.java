/************************************************************************
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
 ************************************************************************/
package org.odftoolkit.simple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableTemplateElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSectionElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.Duration;
import org.odftoolkit.simple.meta.Meta;
import org.odftoolkit.simple.table.TableTemplate;
import org.odftoolkit.simple.table.AbstractTableContainer;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.Section;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;

/**
 * This abstract class is representing one of the possible ODF documents
 */
public abstract class Document extends OdfSchemaDocument implements TableContainer {
	// Static parts of file references
	private static final String SLASH = "/";
	private OdfMediaType mMediaType;
	private Meta mOfficeMeta;
	private long documentOpeningTime;
	private TableContainerImpl tableContainerImpl;
	private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("\\p{Cntrl}");
	private static final String EMPTY_STRING = "";

	private IdentityHashMap<OdfElement, Component> mComponentRepository = new IdentityHashMap<OdfElement, Component>();

	// FIXME: This field is only used in method copyResourcesFrom to improve
	// copy performance, should not be used in any other way.
	// methods loadDocument(String documentPath) and loadDocument(File file)
	// will initialize it.
	// This field and its methods should be removed after ODFDOM supplies batch
	// copy.
	private File mFile = null;

	// if the copy foreign slide for several times,
	// the same style might be copied for several times with the different name
	// so use styleRenameMap to keep track the renamed style so we can reuse the
	// style,
	// rather than new several styles which only have the different style names.
	// while if the style elements really have the same style name but with
	// different content
	// such as that these style elements are from different document
	// so the value for each key should be a list
	private HashMap<String, List<String>> styleRenameMap = new HashMap<String, List<String>>();
	// the map is used to record if the renamed style name is appended to the
	// current dom
	private HashMap<String, Boolean> styleAppendMap = new HashMap<String, Boolean>();

	// the object rename map for image.
	// can not easily recognize if the embedded document are the same.
	// private HashMap<String, String> objectRenameMap = new HashMap<String,
	// String>();

	// Using static factory instead of constructor
	protected Document(OdfPackage pkg, String internalPath, OdfMediaType mediaType) {
		super(pkg, internalPath, mediaType.getMediaTypeString());
		mMediaType = mediaType;
		// set document opening time.
		documentOpeningTime = System.currentTimeMillis();
	}

	/**
	 * This enum contains all possible media types of Document documents.
	 */
	public enum OdfMediaType implements MediaType {

		CHART("application/vnd.oasis.opendocument.chart", "odc"), 
		CHART_TEMPLATE("application/vnd.oasis.opendocument.chart-template", "otc"), 
		FORMULA("application/vnd.oasis.opendocument.formula", "odf"), 
		FORMULA_TEMPLATE("application/vnd.oasis.opendocument.formula-template", "otf"), 
		DATABASE_FRONT_END("application/vnd.oasis.opendocument.base", "odb"), 
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
		 * @param mediaType
		 *            string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and
		 *         the suffix
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
	 * Loads an Document from the given resource. NOTE: Initial meta data will
	 * be added in this method.
	 * 
	 * @param res
	 *            a resource containing a package with a root document
	 * @param odfMediaType
	 *            the media type of the root document
	 * @return the Document document or NULL if the media type is not supported
	 *         by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	protected static Document loadTemplate(Resource res, OdfMediaType odfMediaType) throws Exception {
		InputStream in = res.createInputStream();
		OdfPackage pkg = null;
		try {
			pkg = OdfPackage.loadPackage(in);
		} finally {
			in.close();
		}
		Document newDocument = newDocument(pkg, ROOT_DOCUMENT_PATH, odfMediaType);
		// add initial meta data to new document.
		initializeMetaData(newDocument);
		return newDocument;
	}

	/**
	 * Loads a Document from the provided path.
	 * 
	 * <p>
	 * Document relies on the file being available for read access over the
	 * whole life cycle of Document.
	 * </p>
	 * 
	 * @param documentPath
	 *            - the path from where the document can be loaded
	 * @param password
	 *            - file password.
	 * @return the Document from the given path or NULL if the media type is not
	 *         supported by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 * @since 0.8
	 */
	public static Document loadDocument(String documentPath, String password) throws Exception {
		File file = new File(documentPath);
		return loadDocument(file, password);
	}

	/**
	 * Loads a Document from the provided path.
	 * 
	 * <p>
	 * Document relies on the file being available for read access over the
	 * whole life cycle of Document.
	 * </p>
	 * 
	 * @param documentPath
	 *            - the path from where the document can be loaded
	 * @return the Document from the given path or NULL if the media type is not
	 *         supported by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static Document loadDocument(String documentPath) throws Exception {
		File file = new File(documentPath);
		return loadDocument(file);
	}

	/**
	 * Creates a Document from the Document provided by a resource Stream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by Document, the InputStream is cached. This usually
	 * takes more time compared to the other createInternalDocument methods. An
	 * advantage of caching is that there are no problems overwriting an input
	 * file.
	 * </p>
	 * 
	 * @param inStream
	 *            - the InputStream of the ODF document.
	 * @return the document created from the given InputStream
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static Document loadDocument(InputStream inStream) throws Exception {
		return loadDocument(OdfPackage.loadPackage(inStream));
	}

	/**
	 * Creates a Document from the Document provided by a File.
	 * 
	 * <p>
	 * Document relies on the file being available for read access over the
	 * whole lifecycle of Document.
	 * </p>
	 * 
	 * @param file
	 *            - a file representing the ODF document.
	 * @return the document created from the given File
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static Document loadDocument(File file) throws Exception {
		Document doc = loadDocument(OdfPackage.loadPackage(file));
		doc.setFile(file);
		return doc;
	}

	/**
	 * Creates a Document from the Document provided by a File.
	 * 
	 * <p>
	 * Document relies on the file being available for read access over the
	 * whole lifecycle of Document.
	 * </p>
	 * 
	 * @param file
	 *            - a file representing the ODF document.
	 * @param password
	 *            - file password.
	 * @return the document created from the given File
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 * @since 0.7
	 */
	public static Document loadDocument(File file, String password) throws Exception {
		Document doc = loadDocument(OdfPackage.loadPackage(file, password, null));
		doc.setFile(file);
		return doc;
	}

	/**
	 * Creates a Document from the Document provided by an ODF package.
	 * 
	 * @param odfPackage
	 *            - the ODF package containing the ODF document.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception
	 *             - if the ODF document could not be created.
	 */
	public static Document loadDocument(OdfPackage odfPackage) throws Exception {
		return loadDocument(odfPackage, ROOT_DOCUMENT_PATH);
	}

	/**
	 * Creates a Document from the Document provided by an ODF package.
	 * 
	 * @param odfPackage
	 *            - the ODF package containing the ODF document.
	 * @param internalPath
	 *            - the path to the ODF document relative to the package root.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception
	 *             - if the ODF document could not be created.
	 */
	public static Document loadDocument(OdfPackage odfPackage, String internalPath) throws Exception {
		String documentMediaType = odfPackage.getMediaTypeString(internalPath);
		if (documentMediaType == null) {
			throw new IllegalArgumentException("Given internalPath '" + internalPath + "' is an illegal or inappropriate argument.");
		}
		OdfMediaType odfMediaType = OdfMediaType.getOdfMediaType(documentMediaType);
		if (odfMediaType == null) {
			ErrorHandler errorHandler = odfPackage.getErrorHandler();
			Matcher matcherCTRL = CONTROL_CHAR_PATTERN.matcher(documentMediaType);
			if (matcherCTRL.find()) {
				documentMediaType = matcherCTRL.replaceAll(EMPTY_STRING);
			}
			OdfValidationException ve = new OdfValidationException(OdfSchemaConstraint.DOCUMENT_WITHOUT_ODF_MIMETYPE, internalPath, documentMediaType);
			if (errorHandler != null) {
				errorHandler.fatalError(ve);
			}
			throw ve;
		}
		return newDocument(odfPackage, internalPath, odfMediaType);
	}

	/**
	 * Sets password of this document.
	 * 
	 * @param password
	 *            the password of this document.
	 * @since 0.8
	 */
	public void setPassword(String password) {
		getPackage().setPassword(password);
	}

	// return null if the media type can not be recognized.
	private static Document loadDocumentFromTemplate(OdfMediaType odfMediaType) throws Exception {

		switch (odfMediaType) {
		case TEXT:
		case TEXT_TEMPLATE:
		case TEXT_MASTER:
		case TEXT_WEB:
			// documentTemplate = TextDocument.EMPTY_TEXT_DOCUMENT_RESOURCE;
			TextDocument document = TextDocument.newTextDocument();
			document.changeMode(TextDocument.OdfMediaType.TEXT_WEB);
			return document;

		case SPREADSHEET:
			SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
			spreadsheet.changeMode(SpreadsheetDocument.OdfMediaType.SPREADSHEET);
			return spreadsheet;

		case SPREADSHEET_TEMPLATE:
			SpreadsheetDocument spreadsheettemplate = SpreadsheetDocument.newSpreadsheetDocument();
			spreadsheettemplate.changeMode(SpreadsheetDocument.OdfMediaType.SPREADSHEET_TEMPLATE);
			return spreadsheettemplate;

		case PRESENTATION:
			PresentationDocument presentation = PresentationDocument.newPresentationDocument();
			presentation.changeMode(PresentationDocument.OdfMediaType.PRESENTATION);
			return presentation;

		case PRESENTATION_TEMPLATE:
			PresentationDocument presentationtemplate = PresentationDocument.newPresentationDocument();
			presentationtemplate.changeMode(PresentationDocument.OdfMediaType.PRESENTATION_TEMPLATE);
			return presentationtemplate;

		case GRAPHICS:
			GraphicsDocument graphics = GraphicsDocument.newGraphicsDocument();
			graphics.changeMode(GraphicsDocument.OdfMediaType.GRAPHICS);
			return graphics;

		case GRAPHICS_TEMPLATE:
			GraphicsDocument graphicstemplate = GraphicsDocument.newGraphicsDocument();
			graphicstemplate.changeMode(GraphicsDocument.OdfMediaType.GRAPHICS_TEMPLATE);
			return graphicstemplate;

		case CHART:
			ChartDocument chart = ChartDocument.newChartDocument();
			chart.changeMode(ChartDocument.OdfMediaType.CHART);
			return chart;

		case CHART_TEMPLATE:
			ChartDocument charttemplate = ChartDocument.newChartDocument();
			charttemplate.changeMode(ChartDocument.OdfMediaType.CHART_TEMPLATE);
			return charttemplate;

			// case IMAGE:
			// case IMAGE_TEMPLATE:

		default:
			throw new IllegalArgumentException("Given mediaType '" + odfMediaType.toString() + "' is either not yet supported or not an ODF mediatype!");
		}
	}

	/**
	 * Creates one of the ODF documents based a given mediatype.
	 * 
	 * @param odfMediaType
	 *            The ODF Mediatype of the ODF document to be created.
	 * @return The ODF document, which mediatype dependends on the parameter or
	 *         NULL if media type were not supported.
	 */
	private static Document newDocument(OdfPackage pkg, String internalPath, OdfMediaType odfMediaType) {
		Document newDoc = null;
		switch (odfMediaType) {
		case TEXT:
			newDoc = new TextDocument(pkg, internalPath, TextDocument.OdfMediaType.TEXT);
			break;

		case TEXT_TEMPLATE:
			newDoc = new TextDocument(pkg, internalPath, TextDocument.OdfMediaType.TEXT_TEMPLATE);
			break;

		case TEXT_MASTER:
			newDoc = new TextDocument(pkg, internalPath, TextDocument.OdfMediaType.TEXT_MASTER);
			break;

		case TEXT_WEB:
			newDoc = new TextDocument(pkg, internalPath, TextDocument.OdfMediaType.TEXT_WEB);
			break;

		case SPREADSHEET:
			newDoc = new SpreadsheetDocument(pkg, internalPath, SpreadsheetDocument.OdfMediaType.SPREADSHEET);
			break;

		case SPREADSHEET_TEMPLATE:
			newDoc = new SpreadsheetDocument(pkg, internalPath, SpreadsheetDocument.OdfMediaType.SPREADSHEET_TEMPLATE);
			break;

		case PRESENTATION:
			newDoc = new PresentationDocument(pkg, internalPath, PresentationDocument.OdfMediaType.PRESENTATION);
			break;

		case PRESENTATION_TEMPLATE:
			newDoc = new PresentationDocument(pkg, internalPath, PresentationDocument.OdfMediaType.PRESENTATION_TEMPLATE);
			break;

		case GRAPHICS:
			newDoc = new GraphicsDocument(pkg, internalPath, GraphicsDocument.OdfMediaType.GRAPHICS);
			break;

		case GRAPHICS_TEMPLATE:
			newDoc = new GraphicsDocument(pkg, internalPath, GraphicsDocument.OdfMediaType.GRAPHICS_TEMPLATE);
			break;

		case CHART:
			newDoc = new ChartDocument(pkg, internalPath, ChartDocument.OdfMediaType.CHART);
			break;

		case CHART_TEMPLATE:
			newDoc = new ChartDocument(pkg, internalPath, ChartDocument.OdfMediaType.CHART_TEMPLATE);
			break;
		// case IMAGE:
		// case IMAGE_TEMPLATE:

		default:
			newDoc = null;
			throw new IllegalArgumentException("Given mediaType '" + odfMediaType.mMediaType + "' is not yet supported!");
		}
		// returning null if MediaType is not supported
		return newDoc;
	}

	/**
	 * Returns an embedded OdfPackageDocument from the given package path.
	 * 
	 * @param documentPath
	 *            path to the ODF document within the package. The path is
	 *            relative to the current document.
	 * @return an embedded Document
	 */
	public Document getEmbeddedDocument(String documentPath) {
		String internalPath = getDocumentPath() + documentPath;
		internalPath = normalizeDocumentPath(internalPath);
		Document embeddedDocument = (Document) mPackage.getCachedDocument(internalPath);
		// if the document was not already loaded, fine mimetype and create a
		// new instance
		if (embeddedDocument == null) {
			String mediaTypeString = getMediaTypeString();
			OdfMediaType odfMediaType = OdfMediaType.getOdfMediaType(mediaTypeString);
			if (odfMediaType == null) {
				embeddedDocument = newDocument(mPackage, internalPath, odfMediaType);
			} else {
				try {
					String documentMediaType = mPackage.getMediaTypeString(internalPath);
					odfMediaType = OdfMediaType.getOdfMediaType(documentMediaType);
					if (odfMediaType == null) {
						return null;
					}
					embeddedDocument = Document.loadDocument(mPackage, internalPath);
				} catch (Exception ex) {
					Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return embeddedDocument;
	}

	/**
	 * Method returns all embedded OdfPackageDocuments, which match a valid
	 * OdfMediaType, of the root OdfPackageDocument.
	 * 
	 * @return a list with all embedded documents of the root OdfPackageDocument
	 */
	// ToDo: (Issue 219 - PackageRefactoring) - Better return Path of
	// Documents??
	public List<Document> getEmbeddedDocuments() {
		List<Document> embeddedObjects = new ArrayList<Document>();
		// ToDo: (Issue 219 - PackageRefactoring) - Algorithm enhancement:
		// Instead going through all the files for each mimetype, better
		// Check all files, which have a mimetype if it is one of the desired,
		// perhaps start with ODF prefix
		for (OdfMediaType mediaType : OdfMediaType.values()) {
			embeddedObjects.addAll(getEmbeddedDocuments(mediaType));
		}
		return embeddedObjects;
	}

	/**
	 * Method returns all embedded OdfPackageDocuments of the root
	 * OdfPackageDocument matching the according MediaType. This is done by
	 * matching the subfolder entries of the manifest file with the given
	 * OdfMediaType.
	 * 
	 * @param mediaType
	 *            media type which is used as a filter
	 * @return embedded documents of the root OdfPackageDocument matching the
	 *         given media type
	 */
	public List<Document> getEmbeddedDocuments(OdfMediaType mediaType) {
		String wantedMediaString = null;
		if (mediaType != null) {
			wantedMediaString = mediaType.getMediaTypeString();
		}
		List<Document> embeddedObjects = new ArrayList<Document>();
		// check manifest for current embedded OdfPackageDocuments
		Set<String> manifestEntries = mPackage.getFilePaths();
		for (String path : manifestEntries) {
			// any directory that is not the root document "/"
			if (path.length() > 1 && path.endsWith(SLASH)) {
				String entryMediaType = mPackage.getFileEntry(path).getMediaTypeString();
				// if the entry is a document (directory has mediaType)
				if (entryMediaType != null) {
					// if a specific ODF mediatype was requested
					if (wantedMediaString != null) {
						// test if the desired mediatype matches the current
						if (entryMediaType.equals(wantedMediaString)) {
							normalizeDocumentPath(path);
							embeddedObjects.add(getEmbeddedDocument(path));
						}
					} else {
						// test if any ODF mediatype matches the current
						for (OdfMediaType type : OdfMediaType.values()) {
							if (entryMediaType.equals(type.getMediaTypeString())) {
								embeddedObjects.add(getEmbeddedDocument(path));
							}
						}
					}
				}
			}
		}
		return embeddedObjects;
	}

	/**
	 * Embed an OdfPackageDocument to the current OdfPackageDocument. All the
	 * file entries of child document will be embedded as well to the current
	 * document package.
	 * 
	 * @param documentPath
	 *            to the directory the ODF document should be inserted (relative
	 *            to the current document).
	 * @param sourceDocument
	 *            the OdfPackageDocument to be embedded.
	 */
	public void insertDocument(OdfPackageDocument sourceDocument, String documentPath) {
		super.insertDocument(sourceDocument, documentPath);
	}

	/**
	 * Sets the media type of the Document
	 * 
	 * @param odfMediaType
	 *            media type to be set
	 */
	protected void setOdfMediaType(OdfMediaType odfMediaType) {
		mMediaType = odfMediaType;
		super.setMediaTypeString(odfMediaType.getMediaTypeString());
	}

	/**
	 * Gets the media type of the Document
	 */
	protected OdfMediaType getOdfMediaType() {
		return mMediaType;
	}

	/**
	 * Get the meta data feature instance of the current document
	 * 
	 * @return the meta data feature instance which represent
	 *         <code>office:meta</code> in the meta.xml
	 */
	public Meta getOfficeMetadata() {
		if (mOfficeMeta == null) {
			try {
				mOfficeMeta = new Meta(getMetaDom());
			} catch (Exception ex) {
				Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return mOfficeMeta;
	}

	/**
	 * Save the document to an OutputStream. Delegate to the root document and
	 * save possible embedded Documents.
	 * 
	 * <p>
	 * If the input file has been cached (this is the case when loading from an
	 * InputStream), the input file can be overwritten.
	 * </p>
	 * 
	 * <p>
	 * If not, the OutputStream may not point to the input file! Otherwise this
	 * will result in unwanted behaviour and broken files.
	 * </p>
	 * 
	 * <p>
	 * When save the embedded document to a stand alone document, all the file
	 * entries of the embedded document will be copied to a new document
	 * package. If the embedded document is outside of the current document
	 * directory, you have to embed it to the sub directory and refresh the link
	 * of the embedded document. you should reload it from the stream to get the
	 * saved embedded document.
	 * 
	 * @param out
	 *            - the OutputStream to write the file to
	 * @throws java.lang.Exception
	 *             if the document could not be saved
	 */
	public void save(OutputStream out) throws Exception {
		// 2DO FLUSH AND SAVE IN PACKAGE
		flushDoms();
		updateMetaData();
		if (!isRootDocument()) {
			Document newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.updateMetaData();
			newDoc.mPackage.save(out);
			// ToDo: (Issue 219 - PackageRefactoring) - Return the document,
			// when not closing!
			// Should we close the sources now? User will never receive the open
			// package!
		} else {
			// 2DO MOVE CACHE TO PACKAGE
			// // the root document only have to flush the DOM of all open child
			// documents
			// flushAllDOMs();
			mPackage.save(out);
		}
	}

	/**
	 * Save the document to a given file.
	 * 
	 * <p>
	 * If the input file has been cached (this is the case when loading from an
	 * InputStream), the input file can be overwritten.
	 * </p>
	 * 
	 * <p>
	 * Otherwise it's allowed to overwrite the input file as long as the same
	 * path name is used that was used for loading (no symbolic link foo2.odt
	 * pointing to the loaded file foo1.odt, no network path X:\foo.odt pointing
	 * to the loaded file D:\foo.odt).
	 * </p>
	 * 
	 * <p>
	 * When saving the embedded document to a stand alone document, all files of
	 * the embedded document will be copied to a new document package. If the
	 * embedded document is outside of the current document directory, you have
	 * to embed it to the sub directory and refresh the link of the embedded
	 * document. You should reload it from the given file to get the saved
	 * embedded document.
	 * 
	 * @param file
	 *            - the file to save the document
	 * @throws java.lang.Exception
	 *             if the document could not be saved
	 */
	public void save(File file) throws Exception {
		// 2DO FLUSH AND SAVE IN PACKAGE
		flushDoms();
		updateMetaData();
		if (!isRootDocument()) {
			Document newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.updateMetaData();
			newDoc.mPackage.save(file);
			// ToDo: (Issue 219 - PackageRefactoring) - Return the document,
			// when not closing!
			// Should we close the sources now? User will never receive the open
			// package!
		} else {
			this.mPackage.save(file);
		}
	}

	/**
	 * Save the document to a given file with given password.
	 * 
	 * <p>
	 * If the input file has been cached (this is the case when loading from an
	 * InputStream), the input file can be overwritten.
	 * </p>
	 * 
	 * <p>
	 * Otherwise it's allowed to overwrite the input file as long as the same
	 * path name is used that was used for loading (no symbolic link foo2.odt
	 * pointing to the loaded file foo1.odt, no network path X:\foo.odt pointing
	 * to the loaded file D:\foo.odt).
	 * </p>
	 * 
	 * <p>
	 * When saving the embedded document to a stand alone document, all files of
	 * the embedded document will be copied to a new document package. If the
	 * embedded document is outside of the current document directory, you have
	 * to embed it to the sub directory and refresh the link of the embedded
	 * document. You should reload it from the given file to get the saved
	 * embedded document.
	 * 
	 * @param file
	 *            the file to save the document.
	 * @param file
	 *            the password of this document.
	 * 
	 * @throws java.lang.Exception
	 *             if the document could not be saved
	 * @since 0.8
	 */
	public void save(File file, String password) throws Exception {
		// 2DO FLUSH AND SAVE IN PACKAGE
		flushDoms();
		updateMetaData();
		if (!isRootDocument()) {
			Document newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.updateMetaData();
			newDoc.mPackage.setPassword(password);
			newDoc.mPackage.save(file);
			// ToDo: (Issue 219 - PackageRefactoring) - Return the document,
			// when not closing!
			// Should we close the sources now? User will never receive the open
			// package!
		} else {
			mPackage.setPassword(password);
			mPackage.save(file);
		}
	}

	/**
	 * Close the OdfPackage and release all temporary created data. Acter
	 * execution of this method, this class is no longer usable. Do this as the
	 * last action to free resources. Closing an already closed document has no
	 * effect. Note that this will not close any cached documents.
	 */
	@Override
	public void close() {
		// set all member variables explicit to null
		mMediaType = null;
		mOfficeMeta = null;
		mComponentRepository.clear();
		super.close();
	}

	/**
	 * Get the content root of a document.
	 * 
	 * You may prefer to use the getContentRoot methods of subclasses of
	 * Document. Their return parameters are already casted to respective
	 * subclasses of OdfElement.
	 * 
	 * @param clazz
	 *            the type of the content root, depend on the document type
	 * @return the child element of office:body, e.g. office:text for text docs
	 * @throws Exception
	 *             if the file DOM could not be created.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends OdfElement> T getContentRoot(Class<T> clazz) throws Exception {
		OdfElement contentRoot = getContentDom().getRootElement();
		OfficeBodyElement contentBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, contentRoot);
		NodeList childs = contentBody.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
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
	 * Document.
	 * 
	 * @return the child element of office:body, e.g. office:text for text docs
	 * @throws Exception
	 *             if the file DOM could not be created.
	 */
	public OdfElement getContentRoot() throws Exception {
		return getContentRoot(OdfElement.class);
	}

	@Override
	public String toString() {
		return "\n" + getMediaTypeString() + " - ID: " + this.hashCode() + " " + getPackage().getBaseURI();
	}

	/**
	 * Insert an Image from the specified uri to the end of the Document.
	 * 
	 * @param imageUri
	 *            The URI of the image that will be added to the document, add
	 *            image stream to the package, in the 'Pictures/' graphic
	 *            directory with the same image file name as in the URI. If the
	 *            imageURI is relative first the user.dir is taken to make it
	 *            absolute.
	 * @return Returns the internal package path of the image, which was created
	 *         based on the given URI.
	 * */
	public String newImage(URI imageUri) {
		try {
			OdfContentDom contentDom = this.getContentDom();
			OdfDrawFrame drawFrame = contentDom.newOdfElement(OdfDrawFrame.class);
			XPath xpath = contentDom.getXPath();
			if (this instanceof SpreadsheetDocument) {
				TableTableCellElement lastCell = (TableTableCellElement) xpath.evaluate("//table:table-cell[last()]", contentDom, XPathConstants.NODE);
				lastCell.appendChild(drawFrame);
				drawFrame.removeAttribute("text:anchor-type");

			} else if (this instanceof TextDocument) {
				TextPElement lastPara = (TextPElement) xpath.evaluate("//text:p[last()]", contentDom, XPathConstants.NODE);
				if (lastPara == null) {
					lastPara = ((TextDocument) this).newParagraph();
				}
				lastPara.appendChild(drawFrame);
				drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
			} else if (this instanceof PresentationDocument) {
				DrawPageElement lastPage = (DrawPageElement) xpath.evaluate("//draw:page[last()]", contentDom, XPathConstants.NODE);
				lastPage.appendChild(drawFrame);
			}
			OdfDrawImage image = (OdfDrawImage) drawFrame.newDrawImageElement();
			String imagePath = image.newImage(imageUri);
			return imagePath;
		} catch (Exception ex) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Meta data about the document will be initialized. Following metadata data
	 * is being added:
	 * <ul>
	 * <li>The initial creator name will be the Java user.name System property.</li>
	 * <li>The date and time when this document was created using the current
	 * data.</li>
	 * <li>The number of times this document has been edited.</li>
	 * <li>The default language will be the Java user.language System property.</li>
	 * </ul>
	 * 
	 * @param newDoc
	 *            the Document object which need to initialize meta data.
	 * 
	 *            TODO:This method will be moved to OdfMetadata class. see
	 *            http://odftoolkit.org/bugzilla/show_bug.cgi?id=204
	 */
	private static void initializeMetaData(Document newDoc) {
		Meta metaData = newDoc.getOfficeMetadata();
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
	 * <li>The name of the person who last modified this document will be the
	 * Java user.name System property</li>
	 * <li>The date and time when the document was last modified using current
	 * data</li>
	 * <li>The number of times this document has been edited is incremented by 1
	 * </li>
	 * <li>The total time spent editing this document</li>
	 * </ul>
	 * 
	 * TODO:This method will be moved to OdfMetadata class. see
	 * http://odftoolkit.org/bugzilla/show_bug.cgi?id=204
	 * 
	 * @throws Exception
	 */
	private void updateMetaData() throws Exception {
		if (mMetaDom != null) {
			Meta metaData = getOfficeMetadata();
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
				Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "editing duration update fail as DatatypeFactory can not be instanced", e);
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

	public static ScriptType getScriptType(Locale locale) {
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
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "Failed to set locale", e);
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
				return getDefaultLanguageByProperty(OdfTextProperties.Country, OdfTextProperties.Language);
			case CJK:
				return getDefaultLanguageByProperty(OdfTextProperties.CountryAsian, OdfTextProperties.LanguageAsian);
			case CTL:
				return getDefaultLanguageByProperty(OdfTextProperties.CountryComplex, OdfTextProperties.LanguageComplex);
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "Failed to get locale", e);
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
				if (style.getFamily().getProperties().contains(OdfTextProperties.Language)) {
					style.setProperty(OdfTextProperties.Language, locale.getLanguage());
					style.setProperty(OdfTextProperties.Country, locale.getCountry());
				}
			}
		}
	}

	private Locale getDefaultLanguageByProperty(OdfStyleProperty countryProp, OdfStyleProperty languageProp) throws Exception {
		String lang = null, ctry = null;

		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();

		// get language and country setting from default style setting for
		// paragraph
		OdfDefaultStyle defaultStyle = styles.getDefaultStyle(OdfStyleFamily.Paragraph);
		if (defaultStyle != null) {
			if (defaultStyle.hasProperty(countryProp) && defaultStyle.hasProperty(languageProp)) {
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
			if (style.hasProperty(countryProp) && style.hasProperty(languageProp)) {
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
		if (!user_language.equals(Locale.CHINESE.getLanguage()) && !user_language.equals(Locale.TRADITIONAL_CHINESE.getLanguage())
				&& !user_language.equals(Locale.JAPANESE.getLanguage()) && !user_language.equals(Locale.KOREAN.getLanguage()))
			return;

		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(OdfTextProperties.LanguageAsian)) {
					style.setProperty(OdfTextProperties.LanguageAsian, locale.getLanguage());
					style.setProperty(OdfTextProperties.CountryAsian, locale.getCountry());
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
				if (style.getFamily().getProperties().contains(OdfTextProperties.LanguageComplex)) {
					style.setProperty(OdfTextProperties.LanguageComplex, locale.getLanguage());
					style.setProperty(OdfTextProperties.CountryComplex, locale.getCountry());
				}
			}
		}
	}

	/**
	 * This method will search both the document content and header/footer,
	 * return an iterator of section objects.
	 * <p>
	 * The sections defined in embed document won't be covered.
	 * 
	 * @return an iterator of Section objects
	 */
	public Iterator<Section> getSectionIterator() {
		TextSectionElement element;
		ArrayList<Section> list = new ArrayList<Section>();
		try {
			// search in content.xml
			OdfElement root = getContentDom().getRootElement();
			OfficeBodyElement officeBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, root);
			NodeList sectionList = officeBody.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "section");
			for (int i = 0; i < sectionList.getLength(); i++) {
				element = (TextSectionElement) sectionList.item(i);
				list.add(Section.getInstance(element));
			}

			// Search in style.xml
			root = getStylesDom().getRootElement();
			OfficeMasterStylesElement masterStyle = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class, root);
			sectionList = masterStyle.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "section");
			for (int i = 0; i < sectionList.getLength(); i++) {
				element = (TextSectionElement) sectionList.item(i);
				list.add(Section.getInstance(element));
			}

		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "Failed in sectionIterator", e);
		}
		return list.iterator();
	}

	/**
	 * This method will search both the document content and header/footer,
	 * return a section with a specific name.
	 * <p>
	 * This method won't search in the embed document.
	 * <p>
	 * Null will be returned if there is no section found.
	 * 
	 * @param name
	 *            - the name of a section
	 * @return a section object with a specific name
	 */
	public Section getSectionByName(String name) {
		TextSectionElement element;
		try {
			OdfElement root = getContentDom().getRootElement();
			OfficeBodyElement officeBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, root);
			XPath xpath = getContentDom().getXPath();
			String xpathValue = ".//text:section[@text:name=\"" + name + "\"]";
			element = (TextSectionElement) xpath.evaluate(xpathValue, officeBody, XPathConstants.NODE);
			if (element != null) {
				return Section.getInstance(element);
			}

			root = getStylesDom().getRootElement();
			OfficeMasterStylesElement masterStyle = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class, root);
			xpath = getStylesDom().getXPath();
			element = (TextSectionElement) xpath.evaluate(".//text:section[@text:name=\"" + name + "\"]", masterStyle, XPathConstants.NODE);
			if (element != null) {
				return Section.getInstance(element);
			}

		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "Failed in getSectionByName", e);
		}

		return null;

	}

	/**
	 * Remove an ODF element from the document. All the resources that are only
	 * related with this element will be removed at the same time.
	 * 
	 * @param odfElement
	 *            - the odf element that would be moved.
	 */
	public boolean removeElementLinkedResource(OdfElement odfElement) {
		boolean success = deleteLinkedRef(odfElement);
		success &= deleteStyleRef(odfElement);
		return success;
	}

	/**
	 * Return a unique string with a character "a" followed by randomly
	 * generating 6 hex numbers
	 * 
	 * @return a unique string
	 */
	String makeUniqueName() {
		return String.format("a%06x", (int) (Math.random() * 0xffffff));
	}

	private String getNewUniqueString(String oldStr) {
		int lastIndex = oldStr.lastIndexOf("-");
		if (lastIndex == -1) {
			return oldStr + "-" + makeUniqueName();
		}
		String suffix = oldStr.substring(lastIndex + 1);
		if (suffix.matches("a[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]")) {
			return oldStr.substring(0, lastIndex + 1) + makeUniqueName();
		} else
			return oldStr + "-" + makeUniqueName();
	}

	private void updateAttribute(Attr attr) {
		String oldID = attr.getValue();
		String newID = getNewUniqueString(oldID);
		attr.setValue(newID);
	}

	/**
	 * Make a content copy of the specified element, and the returned element
	 * should have the specified ownerDocument.
	 * 
	 * @param element
	 *            The element that need to be copied
	 * @param dom
	 *            The specified DOM tree that the returned element belong to
	 * @param deep
	 *            If true, recursively clone the subtree under the element,
	 *            false, only clone the element itself
	 * @return Returns a duplicated element which is not in the DOM tree with
	 *         the specified element
	 */
	Node cloneForeignElement(Node element, OdfFileDom dom, boolean deep) {
		if (element instanceof OdfElement) {
			OdfElement cloneElement = dom.createElementNS(((OdfElement) element).getOdfName());

			NamedNodeMap attributes = element.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node item = attributes.item(i);
					String qname = null;
					String prefix = item.getPrefix();
					if (prefix == null) {
						qname = item.getLocalName();
					} else {
						qname = prefix + ":" + item.getLocalName();
					}

					cloneElement.setAttributeNS(item.getNamespaceURI(), qname, item.getNodeValue());
				}
			}

			if (deep) {
				Node childNode = element.getFirstChild();
				while (childNode != null) {
					cloneElement.appendChild(cloneForeignElement(childNode, dom, true));
					childNode = childNode.getNextSibling();
				}
			}

			return cloneElement;
		} else {
			return dom.createTextNode(element.getNodeValue());
		}

	}

	/**
	 * This method will update all the attribute "xml:id" to make it unique
	 * within the whole document content
	 * <p>
	 * This method is usually be invoked before inserting a copied ODF element
	 * to document content.
	 * 
	 * @param element
	 *            - the element that need to be inserted.
	 */
	void updateXMLIds(OdfElement element) {
		try {
			XPath xpath = getContentDom().getXPath();
			String xpathValue = "//*[@xml:id]";
			NodeList childList = (NodeList) xpath.evaluate(xpathValue, element, XPathConstants.NODESET);
			if (childList == null)
				return;

			for (int i = 0; i < childList.getLength(); i++) {
				OdfElement ele = (OdfElement) childList.item(i);
				Attr attri = ele.getAttributeNodeNS(OdfDocumentNamespace.XML.getUri(), "id");
				updateAttribute(attri);
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "Failed in updateXMLIds", e);
		}
	}

	/**
	 * This method will update all the attribute
	 * "text:name","table:name","draw:name","chart:name", to make it unique
	 * within the whole document content.
	 * <p>
	 * This method is usually be invoked before inserting a copied ODF element
	 * to document content.
	 * 
	 * @param element
	 *            - the element that need to be inserted.
	 */
	// anim:name, chart:name, config:name, office:name, presentation:name,
	// svg:name,
	void updateNames(OdfElement element) {
		try {
			XPath xpath = getContentDom().getXPath();
			String xpathValue = "descendant-or-self::node()[@text:name|@table:name|@draw:name|@chart:name]";
			NodeList childList = (NodeList) xpath.evaluate(xpathValue, element, XPathConstants.NODESET);
			if (childList == null)
				return;
			for (int i = 0; i < childList.getLength(); i++) {
				OdfElement ele = (OdfElement) childList.item(i);
				Attr attri = ele.getAttributeNodeNS(OdfDocumentNamespace.TEXT.getUri(), "name");
				if (attri != null)
					updateAttribute(attri);
				attri = ele.getAttributeNodeNS(OdfDocumentNamespace.TABLE.getUri(), "name");
				if (attri != null)
					updateAttribute(attri);
				if (ele instanceof DrawFrameElement)// only update draw:frame
				{
					attri = ele.getAttributeNodeNS(OdfDocumentNamespace.DRAW.getUri(), "name");
					if (attri != null)
						updateAttribute(attri);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, "Failed in updateXMLIds", e);
		}
	}

	/**
	 * This method will copy the linked resource of the element which need to be
	 * copied, from the source package to the target package.
	 * <p>
	 * If the target package contains a resource with the same path and name,
	 * the name of the resource will be renamed.
	 * <p>
	 * This method will copy resources all in one batch.
	 * 
	 * @param sourceCloneEle
	 *            - the element that need to be copied
	 * @param srcDocument
	 *            - the source document
	 */
	void copyLinkedRefInBatch(OdfElement sourceCloneEle, Document srcDocument) {
		try {
			OdfFileDom fileDom = (OdfFileDom) sourceCloneEle.getOwnerDocument();
			XPath xpath;
			if (fileDom instanceof OdfContentDom) {
				xpath = ((OdfContentDom) fileDom).getXPath();
			} else {
				xpath = ((OdfStylesDom) fileDom).getXPath();
			}
			// OdfPackageDocument srcDoc = fileDom.getDocument();
			// new a map to put the original name and the rename string, in case
			// that the same name might be referred by the slide several times.
			HashMap<String, String> objectRenameMap = new HashMap<String, String>();
			NodeList linkNodes = (NodeList) xpath.evaluate(".//*[@xlink:href]", sourceCloneEle, XPathConstants.NODESET);
			for (int i = 0; i <= linkNodes.getLength(); i++) {
				OdfElement object = null;
				if (linkNodes.getLength() == i) {
					if (sourceCloneEle.hasAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href")) {
						object = sourceCloneEle;
					} else {
						break;
					}
				} else {
					object = (OdfElement) linkNodes.item(i);
				}
				String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
				if (refObjPath != null && refObjPath.length() > 0) {
					// the path of the object is start with "./"
					boolean hasPrefix = false;
					String prefix = "./";
					String newObjPath;
					if (refObjPath.startsWith(prefix)) {
						refObjPath = refObjPath.substring(2);
						hasPrefix = true;
					}
					// check if this linked resource has been copied
					if (objectRenameMap.containsKey(refObjPath)) {
						// this object has been copied already
						newObjPath = objectRenameMap.get(refObjPath);
						object.setAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "xlink:href", hasPrefix ? (prefix + newObjPath) : newObjPath);
						continue;
					}
					// check if the current document contains the same path
					OdfFileEntry fileEntry = getPackage().getFileEntry(refObjPath);
					// note: if refObjPath is a directory, it must end with '/'
					if (fileEntry == null) {
						fileEntry = getPackage().getFileEntry(refObjPath + "/");
					}
					newObjPath = refObjPath;
					if (fileEntry != null) {
						// rename the object path
						newObjPath = objectRenameMap.get(refObjPath);
						if (newObjPath == null) {
							// if refObjPath still contains ".", it means that
							// it has the suffix
							// then change the name before the suffix string
							int dotIndex = refObjPath.indexOf(".");
							if (dotIndex != -1) {
								newObjPath = refObjPath.substring(0, dotIndex) + "-" + makeUniqueName() + refObjPath.substring(dotIndex);
							} else {
								newObjPath = refObjPath + "-" + makeUniqueName();
							}
							objectRenameMap.put(refObjPath, newObjPath);
						}
						object.setAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "xlink:href", hasPrefix ? (prefix + newObjPath) : newObjPath);
					} else
						objectRenameMap.put(refObjPath, refObjPath);
				}
			}
			// copy resource in batch
			copyResourcesFrom(srcDocument, objectRenameMap);
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/*****************************/
	/*
	 * These codes are moved from OdfPackage, and should be removed till
	 * OdfPackage can provide a mechanism to copy resources in batch.
	 */
	/*****************************/
	private InputStream readAsInputStream(ZipInputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		if (outputStream != null) {
			byte[] buf = new byte[4096];
			int r = 0;
			while ((r = inputStream.read(buf, 0, 4096)) > -1) {
				outputStream.write(buf, 0, r);
			}
		}
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	private String normalizeFilePath(String internalPath) {
		if (internalPath.equals(EMPTY_STRING)) {
			String errMsg = "The internalPath given by parameter is an empty string!";
			Logger.getLogger(OdfPackage.class.getName()).severe(errMsg);
			throw new IllegalArgumentException(errMsg);
		} else {
			return normalizePath(internalPath);
		}
	}

	private static final String DOUBLE_DOT = "..";
	private static final String DOT = ".";
	private static final String COLON = ":";
	private static final Pattern BACK_SLASH_PATTERN = Pattern.compile("\\\\");
	private static final Pattern DOUBLE_SLASH_PATTERN = Pattern.compile("//");

	private String normalizePath(String path) {
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
			}
		}
		return path;
	}

	private boolean mightBeExternalReference(String internalPath) {
		boolean isExternalReference = false;
		// if the fileReference is a external relative documentURL..
		if (internalPath.startsWith(DOUBLE_DOT) || // or absolute documentURL
				// AND not root document
				internalPath.startsWith(SLASH) && !internalPath.equals(SLASH) || // or
				// absolute
				// IRI
				internalPath.contains(COLON)) {
			isExternalReference = true;
		}
		return isExternalReference;
	}

	private String removeChangeDirectories(String path) {
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
			} else {
				// if a path have to be remove, neglect current path
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

	/*****************************/
	// FIXME: These two methods are only used in method copyResourcesFrom to
	// improve copy performance, should not be used in any other way.
	// methods loadDocument(String documentPath) and loadDocument(File file)
	// will initialize mFile.
	// This field and these two methods should be removed after ODFDOM supplies
	// batch copy.
	private void setFile(File thisFile) {
		mFile = thisFile;
	}

	private File getFile() {
		return mFile;
	}

	/**
	 * This method will copy resources from source document to this document.
	 * The second parameter contains a map between all the name of resources in
	 * the source document and the rename string. If the source document is
	 * loaded from a file, a good performance method will be used. If the source
	 * document is loaded from a input stream, package layer methods will be
	 * invoked to copy these resources, with bad performance.
	 * 
	 * In future, the code of this method will move to ODFDOM package layer.
	 * Till then, good performance will be gotten whether the source document is
	 * loaded from file or from input stream.
	 * 
	 */
	void copyResourcesFrom(Document srcDoc, HashMap<String, String> objectRenameMap) throws Exception {
		if (srcDoc.getFile() != null) {
			ArrayList<String> copiedFolder = new ArrayList<String>();
			Set<String> refObjPathSet = objectRenameMap.keySet();
			FileInputStream tempFileStream = new FileInputStream(srcDoc.getFile());
			ZipInputStream zipStream = new ZipInputStream(tempFileStream);
			ZipEntry zipEntry = zipStream.getNextEntry();
			while (zipEntry != null) {
				String refObjPath = zipEntry.getName();
				for (String path : refObjPathSet) {
					if (refObjPath.equals(path)) {
						String newObjPath = objectRenameMap.get(refObjPath);
						refObjPath = normalizeFilePath(refObjPath);
						String mediaType = srcDoc.getPackage().getFileEntry(refObjPath).getMediaTypeString();
						InputStream is = readAsInputStream(zipStream);
						getPackage().insert(is, newObjPath, mediaType);
						break;
					} else if (refObjPath.startsWith(path + "/")) {
						String suffix = refObjPath.substring(path.length());
						String newObjPath = objectRenameMap.get(path) + suffix;
						refObjPath = normalizeFilePath(refObjPath);
						String mediaType = srcDoc.getPackage().getFileEntry(refObjPath).getMediaTypeString();
						InputStream is = readAsInputStream(zipStream);
						getPackage().insert(is, newObjPath, mediaType);
						if (!copiedFolder.contains(path)) {
							mediaType = srcDoc.getPackage().getFileEntry(path + "/").getMediaTypeString();
							getPackage().insert((InputStream) null, objectRenameMap.get(path) + "/", mediaType);
							copiedFolder.add(path);
						}
						break;
					}
				}
				zipEntry = zipStream.getNextEntry();
			}
			zipStream.close();
			tempFileStream.close();
		} else {
			Set<String> refObjPathSet = objectRenameMap.keySet();
			for (String refObjPath : refObjPathSet) {
				String newObjPath = objectRenameMap.get(refObjPath);
				InputStream is = srcDoc.getPackage().getInputStream(refObjPath);
				if (is != null) {
					String mediaType = srcDoc.getPackage().getFileEntry(refObjPath).getMediaTypeString();
					getPackage().insert(is, newObjPath, mediaType);
				} else {
					Document embedDoc = ((Document) srcDoc).getEmbeddedDocument(refObjPath);
					if (embedDoc != null) {
						insertDocument(embedDoc, newObjPath);
					}
				}
			}
		}
	}

	/**
	 * This method will copy the linked resource of the element which need to be
	 * copied, from the source package to the target package.
	 * <p>
	 * If the target package contains a resource with the same path and name,
	 * the name of the resource will be renamed.
	 * 
	 * @param sourceCloneEle
	 *            - the element that need to be copied
	 */
	// clone the source clone element's referred object path to the current
	// package
	// if the current package contains the same name with the referred object
	// path,
	// rename the object path and path reference of this slide element
	// notes: the source clone element is the copied one to avoid changing the
	// content of the source document.
	void copyLinkedRef(OdfElement sourceCloneEle) {
		try {
			OdfFileDom fileDom = (OdfFileDom) sourceCloneEle.getOwnerDocument();
			XPath xpath;
			if (fileDom instanceof OdfContentDom) {
				xpath = ((OdfContentDom) fileDom).getXPath();
			} else {
				xpath = ((OdfStylesDom) fileDom).getXPath();
			}
			OdfPackageDocument srcDoc = fileDom.getDocument();
			// new a map to put the original name and the rename string, in case
			// that the same name might be referred by the slide several times.
			HashMap<String, String> objectRenameMap = new HashMap<String, String>();
			NodeList linkNodes = (NodeList) xpath.evaluate(".//*[@xlink:href]", sourceCloneEle, XPathConstants.NODESET);
			for (int i = 0; i <= linkNodes.getLength(); i++) {
				OdfElement object = null;
				if (linkNodes.getLength() == i) {
					if (sourceCloneEle.hasAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href")) {
						object = sourceCloneEle;
					} else {
						break;
					}
				} else {
					object = (OdfElement) linkNodes.item(i);
				}
				String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
				if (refObjPath != null && refObjPath.length() > 0) {
					// the path of the object is start with "./"
					boolean hasPrefix = false;
					String prefix = "./";
					if (refObjPath.startsWith(prefix)) {
						refObjPath = refObjPath.substring(2);
						hasPrefix = true;
					}
					// check if the current document contains the same path
					OdfFileEntry fileEntry = getPackage().getFileEntry(refObjPath);
					// note: if refObjPath is a directory, it must end with '/'
					if (fileEntry == null) {
						fileEntry = getPackage().getFileEntry(refObjPath + "/");
					}
					String newObjPath = refObjPath;
					if (fileEntry != null) {
						// rename the object path
						newObjPath = objectRenameMap.get(refObjPath);
						if (newObjPath == null) {
							// if refObjPath still contains ".", it means that
							// it has the suffix
							// then change the name before the suffix string
							int dotIndex = refObjPath.indexOf(".");
							if (dotIndex != -1) {
								newObjPath = refObjPath.substring(0, dotIndex) + "-" + makeUniqueName() + refObjPath.substring(dotIndex);
							} else {
								newObjPath = refObjPath + "-" + makeUniqueName();
							}
							objectRenameMap.put(refObjPath, newObjPath);
						}
						object.setAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "xlink:href", hasPrefix ? (prefix + newObjPath) : newObjPath);
					}
					InputStream is = srcDoc.getPackage().getInputStream(refObjPath);
					if (is != null) {
						String mediaType = srcDoc.getPackage().getFileEntry(refObjPath).getMediaTypeString();
						getPackage().insert(is, newObjPath, mediaType);
					} else {
						Document embedDoc = ((Document) srcDoc).getEmbeddedDocument(refObjPath);
						if (embedDoc != null) {
							insertDocument(embedDoc, newObjPath);
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * When a element needs to be copied to a different document, all the style
	 * definitions that are related with this element need to be copied.
	 * 
	 * @param sourceCloneEle
	 *            - the element that need to be copied
	 * @param srcDoc
	 *            - the source document
	 */
	void copyForeignStyleRef(OdfElement sourceCloneEle, Document srcDoc) {
		try {
			ArrayList<String> tempList = new ArrayList<String>();
			OdfFileDom srcContentDom = srcDoc.getContentDom();
			XPath xpath = srcContentDom.getXPath();
			// 1. collect all the referred style element which has "style:name"
			// attribute
			// 1.1. style:name of content.xml
			String styleQName = "style:name";
			NodeList srcStyleDefNodeList = (NodeList) xpath.evaluate("*/office:automatic-styles/*[@" + styleQName + "]", srcContentDom, XPathConstants.NODESET);
			IdentityHashMap<OdfElement, List<OdfElement>> srcContentStyleCloneEleList = new IdentityHashMap<OdfElement, List<OdfElement>>();
			IdentityHashMap<OdfElement, OdfElement> appendContentStyleList = new IdentityHashMap<OdfElement, OdfElement>();
			getCopyStyleList(null, sourceCloneEle, styleQName, srcStyleDefNodeList, srcContentStyleCloneEleList, appendContentStyleList, tempList, true);
			// 1.2. style:name of styles.xml
			srcStyleDefNodeList = (NodeList) xpath.evaluate(".//*[@" + styleQName + "]", srcDoc.getStylesDom(), XPathConstants.NODESET);
			IdentityHashMap<OdfElement, List<OdfElement>> srcStylesStyleCloneEleList = new IdentityHashMap<OdfElement, List<OdfElement>>();
			IdentityHashMap<OdfElement, OdfElement> appendStylesStyleList = new IdentityHashMap<OdfElement, OdfElement>();
			tempList.clear();
			getCopyStyleList(null, sourceCloneEle, styleQName, srcStyleDefNodeList, srcStylesStyleCloneEleList, appendStylesStyleList, tempList, true);
			// 1.3 rename, copy the referred style element to the corresponding
			// position in the dom tree
			insertCollectedStyle(styleQName, srcContentStyleCloneEleList, getContentDom(), appendContentStyleList);
			insertCollectedStyle(styleQName, srcStylesStyleCloneEleList, getStylesDom(), appendStylesStyleList);

			// 2. collect all the referred style element which has "draw:name"
			// attribute
			// 2.1 draw:name of styles.xml
			// the value of draw:name is string or StyleName,
			// only when the value is StyleName type, the style definition
			// should be cloned to the destination document
			// in ODF spec, such attribute type is only exist in <office:styles>
			// element, so only search it in styles.xml dom
			tempList.clear();
			styleQName = "draw:name";
			srcStyleDefNodeList = (NodeList) xpath.evaluate(".//*[@" + styleQName + "]", srcDoc.getStylesDom(), XPathConstants.NODESET);
			IdentityHashMap<OdfElement, List<OdfElement>> srcDrawStyleCloneEleList = new IdentityHashMap<OdfElement, List<OdfElement>>();
			IdentityHashMap<OdfElement, OdfElement> appendDrawStyleList = new IdentityHashMap<OdfElement, OdfElement>();
			Iterator<OdfElement> iter = appendContentStyleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendContentStyleList.get(styleElement);
				getCopyStyleList(styleElement, cloneStyleElement, styleQName, srcStyleDefNodeList, srcDrawStyleCloneEleList, appendDrawStyleList, tempList,
						false);
			}
			iter = appendStylesStyleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendStylesStyleList.get(styleElement);
				getCopyStyleList(styleElement, cloneStyleElement, styleQName, srcStyleDefNodeList, srcDrawStyleCloneEleList, appendDrawStyleList, tempList,
						false);
			}
			// 2.2 rename, copy the referred style element to the corresponding
			// position in the dom tree
			// note: "draw:name" style element only exist in styles.dom
			insertCollectedStyle(styleQName, srcDrawStyleCloneEleList, getStylesDom(), appendDrawStyleList);
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	// 1. modified the style name of the style definition element which has the
	// same name with the source document
	// 2. As to the style definition which match 1) condition, modified the
	// referred style name of the element which reference this style
	// 3. All the style which also contains other style reference, should be
	// copied to the source document.
	private void insertCollectedStyle(String styleQName, IdentityHashMap<OdfElement, List<OdfElement>> srcStyleCloneEleList, OdfFileDom dom,
			IdentityHashMap<OdfElement, OdfElement> appendStyleList) {
		try {
			String stylePrefix = OdfNamespace.getPrefixPart(styleQName);
			String styleLocalName = OdfNamespace.getLocalPart(styleQName);
			String styleURI = OdfDocumentNamespace.STYLE.getUri();
			if (stylePrefix.equals("draw"))
				styleURI = OdfDocumentNamespace.DRAW.getUri();
			// is the DOM always the styles.xml
			XPath xpath = dom.getXPath();
			NodeList destStyleNodeList;
			if (dom instanceof OdfContentDom)
				destStyleNodeList = (NodeList) xpath.evaluate("*/office:automatic-styles/*[@" + styleQName + "]", dom, XPathConstants.NODESET);
			else
				destStyleNodeList = (NodeList) xpath.evaluate(".//*[@" + styleQName + "]", dom, XPathConstants.NODESET);
			Iterator<OdfElement> iter = srcStyleCloneEleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendStyleList.get(styleElement);
				if (cloneStyleElement == null) {
					cloneStyleElement = (OdfElement) styleElement.cloneNode(true);
					appendStyleList.put(styleElement, cloneStyleElement);
				}
				String styleName = styleElement.getAttributeNS(styleURI, styleLocalName);
				List<String> newStyleNameList = styleRenameMap.get(styleName);
				// if the newStyleNameList != null, means that styleName exists
				// in dest document
				// and it has already been renamed
				if ((newStyleNameList != null) || (isStyleNameExist(destStyleNodeList, styleName) != null)) {
					String newStyleName = null;
					if (newStyleNameList == null) {
						newStyleNameList = new ArrayList<String>();
						newStyleName = styleName + "-" + makeUniqueName();
						newStyleNameList.add(newStyleName);
						styleRenameMap.put(styleName, newStyleNameList);
					} else {
						for (int i = 0; i < newStyleNameList.size(); i++) {
							String styleNameIter = newStyleNameList.get(i);
							OdfElement destStyleElementWithNewName = isStyleNameExist(destStyleNodeList, styleNameIter);
							// check if the two style elements have the same
							// content
							// if not, the cloneStyleElement should rename,
							// rather than reuse the new style name
							cloneStyleElement.setAttributeNS(styleURI, styleQName, styleNameIter);
							if ((destStyleElementWithNewName != null) && destStyleElementWithNewName.equals(cloneStyleElement)) {
								newStyleName = styleNameIter;
								break;
							}
						}
						if (newStyleName == null) {
							newStyleName = styleName + "-" + makeUniqueName();
							newStyleNameList.add(newStyleName);
						}
					}
					// System.out.println("renaming:"+styleName+"-"+newStyleName);
					// if newStyleName has been set in the element as the new
					// name
					// which means that the newStyleName is conform to the odf
					// spec
					// then change element style reference name
					if (changeStyleRefName(srcStyleCloneEleList.get(styleElement), styleName, newStyleName)) {
						cloneStyleElement.setAttributeNS(styleURI, styleQName, newStyleName);
						// if display name should also be renamed
						String displayName = cloneStyleElement.getAttributeNS(styleURI, "display-name");
						if ((displayName != null) && (displayName.length() > 0)) {
							cloneStyleElement.setAttributeNS(styleURI, stylePrefix + ":display-name",
									displayName + newStyleName.substring(newStyleName.length() - 8));
						}
					}
				}
			}

			iter = appendStyleList.keySet().iterator();
			while (iter.hasNext()) {
				OdfElement styleElement = iter.next();
				OdfElement cloneStyleElement = appendStyleList.get(styleElement);
				String newStyleName = cloneStyleElement.getAttributeNS(styleURI, styleLocalName);
				Boolean isAppended = styleAppendMap.get(newStyleName);
				// if styleAppendMap contain the newStyleName,
				// means that cloneStyleElement has already been appended
				if ((isAppended != null) && isAppended.booleanValue() == true) {
					continue;
				} else {
					styleAppendMap.put(newStyleName, true);
				}
				OdfElement cloneForeignStyleElement = (OdfElement) cloneForeignElement(cloneStyleElement, dom, true);
				String styleElePath = getElementPath(styleElement);
				appendForeignStyleElement(cloneForeignStyleElement, dom, styleElePath);
				copyLinkedRef(cloneStyleElement);
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
		}

	}

	// get all the copy of referred style element which is directly referred or
	// indirectly referred by cloneEle
	// styleQName is style:name
	// all the style are defined in srcStyleNodeList
	// and these style are all have the styleName defined in styleQName
	// attribute
	// the key of copyStyleEleList is the style definition element
	// the value of the corresponding key is the clone of the element which
	// refer to the key,
	// the cloned element can be the content of slide or the style element.
	// the key of appendStyleList is the style definition element which has the
	// other style reference
	// the value of the corresponding key is the the style definition clone
	// element
	// loop means if recursive call this function
	// if loop == true, get the style definition element reference other style
	// definition element
	private void getCopyStyleList(OdfElement ele, OdfElement cloneEle, String styleQName, NodeList srcStyleNodeList,
			IdentityHashMap<OdfElement, List<OdfElement>> copyStyleEleList, IdentityHashMap<OdfElement, OdfElement> appendStyleList, List<String> attrStrList,
			boolean loop) {
		try {
			String styleLocalName = OdfNamespace.getLocalPart(styleQName);
			String stylePrefix = OdfNamespace.getPrefixPart(styleQName);
			String styleURI = OdfDocumentNamespace.STYLE.getUri();
			if (stylePrefix.equals("draw"))
				styleURI = OdfDocumentNamespace.DRAW.getUri();
			// OdfElement override the "toString" method
			String cloneEleStr = cloneEle.toString();
			for (int i = 0; i < srcStyleNodeList.getLength(); i++) {
				OdfElement styleElement = (OdfElement) srcStyleNodeList.item(i);
				String styleName = styleElement.getAttributeNS(styleURI, styleLocalName);
				if (styleName != null) {
					int index = 0;
					index = cloneEleStr.indexOf("=\"" + styleName + "\"", index);
					while (index >= 0) {
						String subStr = cloneEleStr.substring(0, index);
						int lastSpaceIndex = subStr.lastIndexOf(' ');
						String attrStr = subStr.substring(lastSpaceIndex + 1, index);
						if (attrStr.equals(styleQName) || attrStrList.contains(attrStr + "=" + "\"" + styleName + "\"")) {
							index = cloneEleStr.indexOf("=\"" + styleName + "\"", index + styleName.length());
							continue;
						}
						attrStrList.add(attrStr + "=" + "\"" + styleName + "\"");
						XPath xpath = ((OdfFileDom) cloneEle.getOwnerDocument()).getXPath();
						NodeList styleRefNodes = (NodeList) xpath.evaluate(".//*[@" + attrStr + "='" + styleName + "']", cloneEle, XPathConstants.NODESET);
						boolean isExist = false;
						for (int j = 0; j <= styleRefNodes.getLength(); j++) {
							OdfElement styleRefElement = null;
							if (j == styleRefNodes.getLength()) {
								isExist = isStyleNameRefExist(cloneEle, styleName, false);
								if (isExist) {
									styleRefElement = cloneEle;
								} else {
									continue;
								}
							} else {
								OdfElement tmpElement = (OdfElement) styleRefNodes.item(j);
								if (isStyleNameRefExist(tmpElement, styleName, false)) {
									styleRefElement = tmpElement;
								} else {
									continue;
								}
							}
							boolean hasLoopStyleDef = true;
							if (!(styleElement instanceof StyleFontFaceElement)) {
							if (copyStyleEleList.get(styleElement) == null) {
								List<OdfElement> styleRefEleList = new ArrayList<OdfElement>();
								copyStyleEleList.put(styleElement, styleRefEleList);
								hasLoopStyleDef = false;
							}
							copyStyleEleList.get(styleElement).add(styleRefElement);
							}

							OdfElement cloneStyleElement = appendStyleList.get(styleElement);
							if (cloneStyleElement == null) {
								cloneStyleElement = (OdfElement) styleElement.cloneNode(true);
								appendStyleList.put(styleElement, cloneStyleElement);
							}
							if (loop && !hasLoopStyleDef) {
								getCopyStyleList(styleElement, cloneStyleElement, styleQName, srcStyleNodeList, copyStyleEleList, appendStyleList, attrStrList,
										loop);
							}
						}
						index = cloneEleStr.indexOf("=\"" + styleName + "\"", index + styleName.length());
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	// append the cloneStyleElement to the contentDom which position is defined
	// by styleElePath
	private void appendForeignStyleElement(OdfElement cloneStyleEle, OdfFileDom dom, String styleElePath) {
		StringTokenizer token = new StringTokenizer(styleElePath, "/");
		boolean isExist = true;
		boolean found = false;
		Node iterNode = dom.getFirstChild();
		Node parentNode = dom;
		while (token.hasMoreTokens()) {
			String onePath = token.nextToken();
			found = false;

			while ((iterNode != null) && isExist) {
				String path = iterNode.getNamespaceURI();
				String prefix = iterNode.getPrefix();
				if (prefix == null) {
					path += "@" + iterNode.getLocalName();
				} else {
					path += "@" + prefix + ":" + iterNode.getLocalName();
				}
				if (!path.equals(onePath)) {
					// not found, then get the next sibling to find such path
					// node
					iterNode = iterNode.getNextSibling();
				} else {
					// found, then get the child nodes to find the next path
					// node
					parentNode = iterNode;
					found = true;
					iterNode = iterNode.getFirstChild();
					break;
				}
			}

			if (!found) {
				// should new the element since the current path node
				if (isExist) {
					isExist = false;
				}
				StringTokenizer token2 = new StringTokenizer(onePath, "@");
				OdfElement newElement = dom.createElementNS(OdfName.newName(token2.nextToken(), token2.nextToken()));
				parentNode.appendChild(newElement);
				parentNode = newElement;
			}
		}
		parentNode.appendChild(cloneStyleEle);
	}

	// The returned string is a path from the top of the dom tree to the
	// specified element
	// and the path is split by "/" between each node
	private String getElementPath(OdfElement styleEle) {
		String path = "";
		Node parentNode = styleEle.getParentNode();
		while (!(parentNode instanceof OdfFileDom)) {
			String qname = null;
			String prefix = parentNode.getPrefix();
			if (prefix == null) {
				qname = parentNode.getLocalName();
			} else {
				qname = prefix + ":" + parentNode.getLocalName();
			}
			path = parentNode.getNamespaceURI() + "@" + qname + "/" + path;
			parentNode = parentNode.getParentNode();
		}
		return path;
	}

	// change the element referred oldStyleName to the new name
	// if true then set newStyleName attribute value successfully
	// if false means that the newStyleName value is not conform to the ODF
	// spec, so do not modify the oldStyleName
	private boolean changeStyleRefName(List<OdfElement> list, String oldStyleName, String newStyleName) {
		boolean rtn = false;
		for (int index = 0; index < list.size(); index++) {
			OdfElement element = list.get(index);
			NamedNodeMap attributes = element.getAttributes();

			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node item = attributes.item(i);
					String value = item.getNodeValue();
					if (oldStyleName.equals(value)) {
						try {
							item.setNodeValue(newStyleName);
							rtn = true;
							break;
						} catch (IllegalArgumentException e) {
							return false;
						}
					}
				}
			}
		}
		return rtn;
	}

	// check if the element contains the referred styleName
	private boolean isStyleNameRefExist(Node element, String styleName, boolean deep) {
		NamedNodeMap attributes = element.getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				if (item.getNodeValue().equals(styleName) && !item.getNodeName().equals("style:name")) {
					// this is style definition, not reference.
					return true;
				}
			}
		}
		if (deep) {
			Node childNode = element.getFirstChild();
			while (childNode != null) {
				if (!isStyleNameRefExist(childNode, styleName, true)) {
					childNode = childNode.getNextSibling();
				} else {
					return true;
				}
			}
		}
		return false;
	}

	// check if nodeList contains the node that "style:name" attribute has the
	// same value with styleName
	// Note: nodeList here is all the style definition list
	private OdfElement isStyleNameExist(NodeList nodeList, String styleName) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			OdfElement element = (OdfElement) nodeList.item(i);
			String name = element.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "name");
			if (name.equals(styleName)) {
				// return true;
				return element;
			}
		}
		// return false;
		return null;
	}

	/**
	 * This method will delete all the linked resources that are only related
	 * with this element.
	 * 
	 * @param odfEle
	 *            - the element to be deleted.
	 * @return true if successfully delete, or else, false will be returned
	 */
	// delete all the xlink:href object which is contained in slideElement and
	// does not referred by other slides
	boolean deleteLinkedRef(OdfElement odfEle) {
		boolean success = true;
		try {
			OdfFileDom contentDom = getContentDom();
			XPath xpath = contentDom.getXPath();
			NodeList linkNodes = (NodeList) xpath.evaluate("//*[@xlink:href]", contentDom, XPathConstants.NODESET);
			for (int i = 0; i < linkNodes.getLength(); i++) {
				OdfElement object = (OdfElement) linkNodes.item(i);
				String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
				int relation = odfEle.compareDocumentPosition(object);
				// if slide element contains the returned element which has the
				// xlink:href reference
				if ((relation & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0 && refObjPath != null && refObjPath.length() > 0) {
					// the path of the object is start with "./"
					NodeList pathNodes = (NodeList) xpath.evaluate("//*[@xlink:href='" + refObjPath + "']", getContentDom(), XPathConstants.NODESET);
					int refCount = pathNodes.getLength();
					if (refCount == 1) {
						// delete "./"
						if (refObjPath.startsWith("./")) {
							refObjPath = refObjPath.substring(2);
						}
						// check if the current document contains the same path
						OdfFileEntry fileEntry = getPackage().getFileEntry(refObjPath);
						if (fileEntry != null) {
							// it is a stream, such as image, binary file
							getPackage().remove(refObjPath);
						} else {
							// note: if refObjPath is a directory, it must end
							// with '/'
							fileEntry = getPackage().getFileEntry(refObjPath + "/");
							removeDocument(refObjPath);
						}
					}
				}
			}
		} catch (XPathExpressionException e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
			success = false;
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
			success = false;
		}
		return success;
	}

	/**
	 * This method will delete all the style definitions that are only related
	 * with this element.
	 * 
	 * @param odfEle
	 *            - the element to be deleted.
	 * @return true if successfully delete, or else, false will be returned
	 */
	boolean deleteStyleRef(OdfElement odfEle) {
		boolean success = true;
		try {
			// method 1:
			// 1.1. iterate child element of the content element
			// 1.2. if the child element is an OdfStylableElement, get the
			// style-name ref count
			// //////////////
			// method 2:
			// 2.1. get the list of the style definition
			ArrayList<OdfElement> removeStyles = new ArrayList<OdfElement>();
			OdfOfficeAutomaticStyles autoStyles = getContentDom().getAutomaticStyles();

			NodeList stylesList = autoStyles.getChildNodes();
			OdfFileDom contentDom = getContentDom();
			XPath xpath = contentDom.getXPath();

			// 2.2. get the reference of each style which occurred in the
			// current page
			for (int i = 0; i < stylesList.getLength(); i++) {
				Node item = stylesList.item(i);
				if (item instanceof OdfElement) {
					OdfElement node = (OdfElement) item;
					String styleName = node.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "name");
					if (styleName != null) {
						// search the styleName contained at the current page
						// element
						NodeList styleNodes = (NodeList) xpath.evaluate("//*[@*='" + styleName + "']", contentDom, XPathConstants.NODESET);
						int styleCnt = styleNodes.getLength();
						if (styleCnt > 1) {
							// the first styleName is occurred in the style
							// definition
							// so check if the second styleName and last
							// styleName is occurred in the current page element
							// if yes, then remove it
							OdfElement elementFirst = (OdfElement) styleNodes.item(1);
							OdfElement elementLast = (OdfElement) styleNodes.item(styleCnt - 1);
							boolean isSamePage = false;
							if (elementFirst instanceof DrawPageElement) {
								DrawPageElement tempPage = (DrawPageElement) elementFirst;
								if (tempPage.equals(odfEle)) {
									isSamePage = true;
								}
							}
							int relationFirst = odfEle.compareDocumentPosition(elementFirst);
							int relationLast = odfEle.compareDocumentPosition(elementLast);
							// if slide element contains the child element which
							// has the styleName reference
							if (((relationFirst & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0 && (relationLast & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0)
									|| (isSamePage && (styleCnt == 1))) {
								if (node instanceof OdfStyleBase) {
									removeStyles.add(node);
								}
							}
						} else {
							continue;
						}
					}
				}
			}
			for (int i = 0; i < removeStyles.size(); i++) {
				autoStyles.removeChild(removeStyles.get(i));
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
			success = false;
		}
		return success;
	}

	public Table addTable() {
		return getTableContainerImpl().addTable();
	}

	public Table addTable(int numRows, int numCols) {
		return getTableContainerImpl().addTable(numRows, numCols);
	}

	public Table getTableByName(String name) {
		return getTableContainerImpl().getTableByName(name);
	}

	public java.util.List<Table> getTableList() {
		return getTableContainerImpl().getTableList();
	}

	public TableBuilder getTableBuilder() {
		return getTableContainerImpl().getTableBuilder();
	}

	protected TableContainer getTableContainerImpl() {
		if (tableContainerImpl == null) {
			tableContainerImpl = new TableContainerImpl();
		}
		return tableContainerImpl;
	}

	private class TableContainerImpl extends AbstractTableContainer {

		public OdfElement getTableContainerElement() {
			OdfElement containerElement = null;
			try {
				containerElement = getContentRoot();
			} catch (Exception e) {
				Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
			}
			return containerElement;
		}
	}

	/**
	 * Return the component repository of this document.
	 * 
	 * @return the component repository of this document.
	 */
	protected IdentityHashMap<OdfElement, Component> getComponentMap() {
		return mComponentRepository;
	}

	/**
	 * Construct a
	 * 
	 * 
	 * <code>TableTemplate<code> feature by extracting style template from an pre-defined table in a foreign document. The styles loaded by the template will be copied into the document as well and can be referenced by table directly.
	 * <p>
	 * The imported table need to be at least a 5*5 table (e.g. A1E5).  Each type of style in the template will be set according to the style reference in a specific table cell, as following:
	 * <br>first column - A2
	 * <br>last column - E2
	 * <br>first row - A2
	 * <br>last row - E2
	 * <br>even rows - B3
	 * <br>odd rows - B2
	 * <br>even columns - C2
	 * <br>odd columns - B2
	 * <br>body - B2
	 * <br>first-row-start-column -A1
	 * <br>first-row-end-column -E1
	 * <br>last-row-start-column -A5
	 * <br>last-row-end-column -E5
	 * 
	 * @param templateFileInputStream
	 *            - the InputStream of the ODF document.
	 * @param tableName
	 *            - the table name which will be used to load styles as template
	 * @throws Exception
	 *             - if content DOM could not be initialized
	 */
	public TableTemplate LoadTableTemplateFromForeignTable(
			InputStream templateFileInputStream, String tableName) throws Exception {

		Document doc = Document.loadDocument(templateFileInputStream);

		if (doc == null)
			throw new IllegalStateException(
					"Cannot load specified template file.");

		Table table = doc.getTableByName(tableName);
		if (table == null)
			throw new IllegalStateException(
					"Cannot load table template from specified file.");

		if (table.getRowCount() < 5 || table.getColumnCount() < 5)
			throw new IllegalStateException(
					"The template cannot be loaded. It should be at least a 5*5 table.");

		TableTemplate template = new TableTemplate(getStylesDom()
				.newOdfElement(TableTableTemplateElement.class));

		// first-row-start-column
		Cell cell = table.getCellByPosition(0, 0);
		cell.getParagraphIterator().hasNext();
		cell.getParagraphIterator().next().getStyleName();
		Paragraph para = cell.getParagraphByIndex(0, false);
		String paraStyle = (para != null ? para.getStyleName() : null);
		template.setExtendedStyleByType(
				TableTemplate.ExtendedStyleType.FIRSTROWSTARTCOLUM, cell
						.getStyleName(), paraStyle);
		TableTableCellElementBase oldCellEle = cell.getOdfElement();
		TableTableCellElementBase newCellEle = (TableTableCellElementBase) oldCellEle
				.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// first-row-end-column
		cell = table.getCellByPosition(4, 0);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setExtendedStyleByType(
				TableTemplate.ExtendedStyleType.FIRSTROWENDCOLUMN, cell
						.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// last-row-start-column
		cell = table.getCellByPosition(0, 4);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setExtendedStyleByType(
				TableTemplate.ExtendedStyleType.LASTROWSTARTCOLUMN, cell
						.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// last-row-end-column
		cell = table.getCellByPosition(4, 4);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setExtendedStyleByType(
				TableTemplate.ExtendedStyleType.LASTROWENDCOLUMN, cell
						.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// first column
		cell = table.getCellByPosition(0, 1);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableFirstColumnStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// last column
		cell = table.getCellByPosition(4, 2);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableLastColumnStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// first row
		cell = table.getCellByPosition(1, 0);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableFirstRowStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// last row
		cell = table.getCellByPosition(1, 4);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableLastRowStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// body (=odd row/column)
		cell = table.getCellByPosition(1, 1);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableBodyStyle(cell.getStyleName(), paraStyle);
		template.setTableOddRowsStyle(cell.getStyleName(), paraStyle);
		template.setTableOddColumnsStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// even row
		cell = table.getCellByPosition(1, 2);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableEvenRowsStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		// even row
		cell = table.getCellByPosition(2, 1);
		para = cell.getParagraphByIndex(0, false);
		paraStyle = (para != null ? para.getStyleName() : null);
		template.setTableEvenColumnsStyle(cell.getStyleName(), paraStyle);
		oldCellEle = cell.getOdfElement();
		newCellEle = (TableTableCellElementBase) oldCellEle.cloneNode(true);
		copyForeignStyleRef(newCellEle, cell.getOwnerDocument());

		return template;
	}

}
