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
package org.odftoolkit.simple;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfDomDocument;
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
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.Duration;
import org.odftoolkit.simple.meta.Meta;
import org.odftoolkit.simple.table.Table;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This abstract class is representing one of the possible ODF documents */
public class Document extends OdfDomDocument {
	// Static parts of file references
	private static final String SLASH = "/";
	private OdfMediaType mMediaType;
	private Meta mOfficeMeta;
	private long documentOpeningTime;

	// Using static factory instead of constructor
	protected Document(OdfPackage pkg, String internalPath, OdfMediaType mediaType) {
		super(pkg, internalPath, mediaType.getMediaTypeString());
		mMediaType = mediaType;
		//set document opening time.
		documentOpeningTime = System.currentTimeMillis();
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
	protected static Document loadTemplate(Resource res, OdfMediaType odfMediaType) throws Exception {
		InputStream in = res.createInputStream();
		OdfPackage pkg = null;
		try {
			pkg = OdfPackage.loadPackage(in);
		} finally {
			in.close();
		}
		Document newDocument = newDocument(pkg, ROOT_DOCUMENT_PATH, odfMediaType);
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
	public static Document loadDocument(String documentPath) throws Exception {
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
	public static Document loadDocument(InputStream inStream) throws Exception {
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
	public static Document loadDocument(File file) throws Exception {
		return loadDocument(OdfPackage.loadPackage(file));
	}

	/**
	 * Creates an OdfDocument from the OpenDocument provided by an ODF package.
	 * @param odfPackage - the ODF package containing the ODF document.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception - if the ODF document could not be created.
	 */
	public static Document loadDocument(OdfPackage odfPackage) throws Exception {
		return loadDocument(odfPackage, ROOT_DOCUMENT_PATH);
	}
	
	/**
	 * Creates an OdfDocument from the OpenDocument provided by an ODF package.
	 * @param odfPackage - the ODF package containing the ODF document.
	 * @param internalPath - the path to the ODF document relative to the package root.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception - if the ODF document could not be created.
	 */
	public static Document loadDocument(OdfPackage odfPackage, String internalPath) throws Exception {
		String documentMediaType = odfPackage.getMediaTypeString(internalPath);
		OdfMediaType odfMediaType = OdfMediaType.getOdfMediaType(documentMediaType);
		if (odfMediaType == null) {
			throw new IllegalArgumentException("Document contains incorrect ODF Mediatype '" + odfPackage.getMediaTypeString() + "'");
		}
		return newDocument(odfPackage, internalPath, odfMediaType);
	}
	//return null if the media type can not be recognized.
	private static Document loadDocumentFromTemplate(OdfMediaType odfMediaType) throws Exception {

		switch (odfMediaType) {
			case TEXT:
			case TEXT_TEMPLATE:
			case TEXT_MASTER:
			case TEXT_WEB:
				//documentTemplate = TextDocument.EMPTY_TEXT_DOCUMENT_RESOURCE;
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
				
//			case IMAGE:
//			case IMAGE_TEMPLATE:

			default:
				throw new IllegalArgumentException("Given mediaType '" + odfMediaType.toString() + "' is either not yet supported or not an ODF mediatype!");
		}
	}

	/**
	 * Creates one of the ODF documents based a given mediatype.
	 *
	 * @param odfMediaType The ODF Mediatype of the ODF document to be created.
	 * @return The ODF document, which mediatype dependends on the parameter or
	 *	NULL if media type were not supported.
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
//				case IMAGE:
//				case IMAGE_TEMPLATE:
				
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
	public Document getEmbeddedDocument(String internalDocumentPath) {

		internalDocumentPath = normalizeDocumentPath(internalDocumentPath);
		Document embeddedDocument = (Document) mPackage.getCachedPackageDocument(internalDocumentPath);
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
	public List<Document> getEmbeddedDocuments() {
		List<Document> embeddedObjects = new ArrayList<Document>();
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
	public List<Document> getEmbeddedDocuments(OdfMediaType mediaType) {
		String wantedMediaString = null;
		if (mediaType != null) {
			wantedMediaString = mediaType.getMediaTypeString();
		}
		List<Document> embeddedObjects = new ArrayList<Document>();
		// check manifest for current embedded OdfPackageDocuments
		Set<String> manifestEntries = mPackage.getFileEntries();
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
	 * Embed an OdfPackageDocument to the current OdfPackageDocument.
	 * All the file entries of child document will be embedded as well to the current document package.
	 * @param documentPath to the directory the ODF document should be inserted (relative to the root of this document).
	 * @param sourceDocument the OdfPackageDocument to be embedded.
	 */
	public void insertDocument(OdfPackageDocument sourceDocument, String documentPath) {
		mPackage.insertDocument(sourceDocument, documentPath);
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
	 * Get the meta data feature instance of the current document
	 * 
	 * @return the meta data feature instance which represent 
	 * <code>office:meta</code> in the meta.xml	 
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
		flushDoms();
		updateMetaData();
		if (!isRootDocument()) {
			Document newDoc = loadDocumentFromTemplate(getOdfMediaType());
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
		flushDoms();
		updateMetaData();
		if (!isRootDocument()) {
			Document newDoc = loadDocumentFromTemplate(getOdfMediaType());
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
	@Override
	public void close() {
		// set all member variables explicit to null
		mMediaType = null;
		mOfficeMeta = null;
		super.close();
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
	protected <T extends OdfElement> T getContentRoot(Class<T> clazz) throws Exception {
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
	 * Return an instance of table feature with the specific table name.
	 * @param name of the table beeing searched for.
	 * @return an instance of table feature with the specific table name.
	 */
	public Table getTableByName(String name) {
		try {
			OdfElement root = getContentDom().getRootElement();
			OfficeBodyElement officeBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);

			NodeList childList = typedContent.getChildNodes();
			for (int i = 0; i < childList.getLength(); i++) {
				if (childList.item(i) instanceof TableTableElement) {
					TableTableElement table = (TableTableElement) childList.item(i);
					if (table.getOdfAttributeValue(OdfName.newName(OdfDocumentNamespace.TABLE, "name")).equals(name)) {
						return Table.getInstance(table);
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Return a list of table features in this document.
	 * @return a list of table features in this document.
	 */
	public List<Table> getTableList() {
		List<Table> tableList = new ArrayList<Table>();
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
					tableList.add(Table.getInstance((TableTableElement) childList.item(i)));
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE, null, e);
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
	 * <li>The name of the person who last modified this document will be the Java user.name System property</li>
	 * <li>The date and time when the document was last modified using current data</li>
	 * <li>The number of times this document has been edited is incremented by 1</li>
	 * <li>The total time spent editing this document</li>
	 * </ul>
	 * 
	 * TODO:This method will be moved to OdfMetadata class. 
	 *      see http://odftoolkit.org/bugzilla/show_bug.cgi?id=204
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
				Logger.getLogger(Document.class.getName()).log(Level.SEVERE,
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
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE,
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
			Logger.getLogger(Document.class.getName()).log(Level.SEVERE,
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
