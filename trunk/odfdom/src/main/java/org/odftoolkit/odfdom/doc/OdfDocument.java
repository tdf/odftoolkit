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
package org.odftoolkit.odfdom.doc;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfMetaDom;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
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
import org.odftoolkit.odfdom.incubator.meta.OdfOfficeMeta;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.type.Duration;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/** 
 * This abstract class is representing one of the possible ODF documents.
 * 
 */
public abstract class OdfDocument extends OdfSchemaDocument {
	// Static parts of file references

	private static final String SLASH = "/";
	private OdfMediaType mMediaType;
	private OdfOfficeMeta mOfficeMeta;
	private long documentOpeningTime;
	private static final Pattern CONTROL_CHAR_PATTERN = Pattern.compile("\\p{Cntrl}");
	private static final String EMPTY_STRING = "";
	private Calendar mCreationDate;

	// Using static factory instead of constructor
	protected OdfDocument(OdfPackage pkg, String internalPath, OdfMediaType mediaType) throws SAXException {
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
					throw new IllegalArgumentException("Given mediaType '" + mediaType + "' is not an ODF mediatype!");
				}
			}
			return odfMediaType;
		}
	}

	/**
	 * Loads the ODF root document from the given Resource.
	 * 
	 * NOTE: Initial meta data (like the document creation time) will be added in this method.
	 * 
	 * @param res a resource containing a package with a root document
	 * @param odfMediaType the media type of the root document
	 * @return the OpenDocument document
	 *		  or NULL if the media type is not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	protected static OdfDocument loadTemplate(Resource res, OdfMediaType odfMediaType) throws Exception {
		InputStream in = res.createInputStream();
		OdfPackage pkg = null;
		try {
			pkg = OdfPackage.loadPackage(in);
		} finally {
			in.close();
		}
		OdfDocument newDocument = newDocument(pkg, ROOT_DOCUMENT_PATH, odfMediaType);
		//add creation time, the metadata have to be explicitly set
		newDocument.mCreationDate = Calendar.getInstance();
		return newDocument;
	}

	/**
	 * Loads the ODF root document from the ODF package provided by its path.
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
		return loadDocument(OdfPackage.loadPackage(documentPath));
	}

	/**
	 * Loads the ODF root document from the ODF package provided by a Stream.
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
	 * Loads the ODF root document from the ODF package provided as a File.
	 *
	 * @param file - a file representing the ODF document.
	 * @return the document created from the given File
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfDocument loadDocument(File file) throws Exception {
		return loadDocument(OdfPackage.loadPackage(file));
	}

	/**
	 * Loads the ODF root document from the ODF package.
	 *
	 * @param odfPackage - the ODF package containing the ODF document.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception - if the ODF document could not be created.
	 */
	public static OdfDocument loadDocument(OdfPackage odfPackage) throws Exception {
		return loadDocument(odfPackage, ROOT_DOCUMENT_PATH);
	}

	/**
	 * Creates an OdfDocument from the OpenDocument provided by an ODF package.
	 * @param odfPackage - the ODF package containing the ODF document.
	 * @param internalPath - the path to the ODF document relative to the package root, or an empty String for the root document.
	 * @return the root document of the given OdfPackage
	 * @throws java.lang.Exception - if the ODF document could not be created.
	 */
	public static OdfDocument loadDocument(OdfPackage odfPackage, String internalPath) throws Exception {
		String documentMediaType = odfPackage.getMediaTypeString(internalPath);
		OdfMediaType odfMediaType = null;
		try {
			odfMediaType = OdfMediaType.getOdfMediaType(documentMediaType);
		} catch (IllegalArgumentException e) {
			// the returned NULL will be taking care of afterwards
		}
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
				throw new IllegalArgumentException("Given mediaType '" + odfMediaType.mMediaType + "' is not yet supported!");
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
	private static OdfDocument newDocument(OdfPackage pkg, String internalPath, OdfMediaType odfMediaType) throws SAXException {
		OdfDocument newDoc = null;
		switch (odfMediaType) {
			case TEXT:
				newDoc = new OdfTextDocument(pkg, internalPath, OdfTextDocument.OdfMediaType.TEXT);
				break;

			case TEXT_TEMPLATE:
				newDoc = new OdfTextDocument(pkg, internalPath, OdfTextDocument.OdfMediaType.TEXT_TEMPLATE);
				break;

			case TEXT_MASTER:
				newDoc = new OdfTextDocument(pkg, internalPath, OdfTextDocument.OdfMediaType.TEXT_MASTER);
				break;

			case TEXT_WEB:
				newDoc = new OdfTextDocument(pkg, internalPath, OdfTextDocument.OdfMediaType.TEXT_WEB);
				break;

			case SPREADSHEET:
				newDoc = new OdfSpreadsheetDocument(pkg, internalPath, OdfSpreadsheetDocument.OdfMediaType.SPREADSHEET);
				break;

			case SPREADSHEET_TEMPLATE:
				newDoc = new OdfSpreadsheetDocument(pkg, internalPath, OdfSpreadsheetDocument.OdfMediaType.SPREADSHEET_TEMPLATE);
				break;

			case PRESENTATION:
				newDoc = new OdfPresentationDocument(pkg, internalPath, OdfPresentationDocument.OdfMediaType.PRESENTATION);
				break;

			case PRESENTATION_TEMPLATE:
				newDoc = new OdfPresentationDocument(pkg, internalPath, OdfPresentationDocument.OdfMediaType.PRESENTATION_TEMPLATE);
				break;

			case GRAPHICS:
				newDoc = new OdfGraphicsDocument(pkg, internalPath, OdfGraphicsDocument.OdfMediaType.GRAPHICS);
				break;

			case GRAPHICS_TEMPLATE:
				newDoc = new OdfGraphicsDocument(pkg, internalPath, OdfGraphicsDocument.OdfMediaType.GRAPHICS_TEMPLATE);
				break;

			case CHART:
				newDoc = new OdfChartDocument(pkg, internalPath, OdfChartDocument.OdfMediaType.CHART);
				break;

			case CHART_TEMPLATE:
				newDoc = new OdfChartDocument(pkg, internalPath, OdfChartDocument.OdfMediaType.CHART_TEMPLATE);
				break;

			case IMAGE:
				newDoc = new OdfImageDocument(pkg, internalPath, OdfImageDocument.OdfMediaType.IMAGE);
				break;

			case IMAGE_TEMPLATE:
				newDoc = new OdfImageDocument(pkg, internalPath, OdfImageDocument.OdfMediaType.IMAGE_TEMPLATE);
				break;

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
	 * @param documentPath to the ODF document within the package. The path is relative the current ODF document path.
	 * @return an embedded OdfPackageDocument
	 */
	@Override
	public OdfDocument loadSubDocument(String documentPath) {
		return (OdfDocument) super.loadSubDocument(documentPath);
	}

	/**
	 * Method returns all embedded OdfPackageDocuments, which match a valid OdfMediaType,
	 * of the current OdfPackageDocument. Note: The root document is not part of the returned collection.
	 * @return a map with all embedded documents and their paths of the current OdfPackageDocument
	 */
	public Map<String, OdfDocument> loadSubDocuments() {
		return loadSubDocuments(null);
	}

	/**
	 * Method returns all embedded OdfPackageDocuments of sthe current OdfPackageDocument matching the
	 * according MediaType. This is done by matching the subfolder entries of the
	 * manifest file with the given OdfMediaType.
	 * @param desiredMediaType media type of the documents to be returned (used as a filter).
	 * @return embedded documents of the current OdfPackageDocument matching the given media type
	 */
	public Map<String, OdfDocument> loadSubDocuments(OdfMediaType desiredMediaType) {
		String wantedMediaString = null;
		if (desiredMediaType != null) {
			wantedMediaString = desiredMediaType.getMediaTypeString();
		}
		Map<String, OdfDocument> embeddedObjectsMap = new HashMap<String, OdfDocument>();
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
							path = normalizeDocumentPath(path);
							embeddedObjectsMap.put(path, (OdfDocument) mPackage.loadDocument(path));
						}
					} else {
						// test if any ODF mediatype matches the current
						for (OdfMediaType mediaType : OdfMediaType.values()) {
							if (entryMediaType.equals(mediaType.getMediaTypeString())) {
								embeddedObjectsMap.put(path, (OdfDocument) mPackage.loadDocument(path));
							}
						}
					}
				}
			}
		}
		return embeddedObjectsMap;
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
	public OdfOfficeMeta getOfficeMetadata() {
		if (mOfficeMeta == null) {
			try {
				OdfMetaDom metaDom = getMetaDom();
				if (metaDom == null) {
					metaDom = new OdfMetaDom(this, OdfSchemaDocument.OdfXMLFile.META.getFileName());
				}
				mOfficeMeta = new OdfOfficeMeta(metaDom);
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
		updateMetaData();
		if (!isRootDocument()) {
			OdfDocument newDoc = loadDocumentFromTemplate(getOdfMediaType());
			newDoc.insertDocument(this, ROOT_DOCUMENT_PATH);
			newDoc.updateMetaData();
			newDoc.mPackage.save(out);
			// ToDo: (Issue 219 - PackageRefactoring) - Return the document, when not closing!
			// Should we close the sources now? User will never receive the open package!
		} else {
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
	@Override
	public void save(File file) throws Exception {
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

	@Override
	public String toString() {
		return "\n" + getMediaTypeString() + " - ID: " + this.hashCode() + " " + getPackage().getBaseURI();
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
		List<OdfTable> tableList = null;
		try {
			List<TableTableElement> tableElementList = getTables();
			tableList = new ArrayList<OdfTable>(tableElementList.size());
			for (int i = 0;
					i < tableElementList.size();
					i++) {
				tableList.add(OdfTable.getInstance(tableElementList.get(i)));
			}
		} catch (Exception e) {
			Logger.getLogger(OdfDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return tableList;
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
	 * @throws IllegalArgumentException 
	 */
	private void updateMetaData() throws IllegalArgumentException, Exception {
		if (getOfficeMetadata().hasAutomaticUpdate()) {
			OdfOfficeMeta metaData = getOfficeMetadata();

			// set creation date´
			if (mCreationDate != null) {
				getOfficeMetadata().setCreationDate(mCreationDate);
			}

			// update late modfied date
			Calendar calendar = Calendar.getInstance();
			metaData.setDate(calendar);

			// update editing-cycles
			Integer cycle = metaData.getEditingCycles();
			if (cycle != null) {
				metaData.setEditingCycles(++cycle);
			} else {
				metaData.setEditingCycles(1);
			}
			// update editing-duration
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
	 * three different groups.
	 * 
	 * <p>
	 * 1) There is CJK: the Chinese, Japanese and Korean script (also old
	 * Vietnamese belong to this group). See
	 * http://en.wikipedia.org/wiki/CJK_characters
	 * 
	 * <p>
	 * 2) There is CTL: Complex Text Layout, which uses BIDI algorithms and/or
	 * glyph modules for instance Arabic, Hebrew, Indic and Thai. See http://en.wikipedia.org/wiki/Complex_Text_Layout
	 * 
	 * <p>
	 * 3) And there is all the rest, which was once called by MS Western.
	 */
	public enum UnicodeGroup {

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
		setLocale(locale, getUnicodeGroup(locale));
	}

	/**
	 * This method will return Locale, which presents the default
	 * language and country information settings in this document.
	 *
	 * @return an instance of Locale that the default language and country is
	 *         set to.
	 */
	/**
	 * Similar to OpenOffice.org, ODFDOM assumes that every Locale is related
	 * to one of the three Unicodes Groups, either CJK, CTL or Western.
	 * @param locale the UnicodeGroup is requested for
	 * @return the related UnicodeGroup
	 */
	public static UnicodeGroup getUnicodeGroup(Locale locale) {
		String language = locale.getLanguage();
		if (CJKLanguage.contains(language)) {
			return UnicodeGroup.CJK;
		}
		if (CTLLanguage.contains(language)) {
			return UnicodeGroup.CTL;
		}
		return UnicodeGroup.WESTERN;

	}

	/**
	 * <p>
	 * Set a locale of a specific script type.
	 * <p>
	 * If the locale does not belong to the script type, it will not be set.
	 *
	 * @param locale
	 *            - Locale information
	 * @param unicodeGroup
	 *            - The script type
	 */
	private void setLocale(Locale locale, UnicodeGroup unicodeGroup) {
		try {
			switch (unicodeGroup) {
				case WESTERN:
					setDefaultWesternLanguage(locale);
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
	 * This method will return Locale, which presents the default
	 * language and country information settings in this document
	 * <p>
	 * ODF allows to set a Locale for each of the three UnicodeGroups.
	 * Therefore there might be three different Locale for the document.
	 * 
	 * @param unicodeGroup
	 *            - One of the three (CJK, CTL or Western).
	 * @return the Locale for the given UnicodeGroup
	 */
	public Locale getLocale(UnicodeGroup unicodeGroup) {
		try {
			switch (unicodeGroup) {
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

	/** Returns the current Locale for the OdfStyleProperty of the corresponding UnicodeGroup */
	private Locale getDefaultLanguageByProperty(OdfStyleProperty countryProp,
			OdfStyleProperty languageProp) throws Exception {
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
	 * This method will set the default language and country information of the
	 * document, based on the parameter of the Locale information.
	 *
	 * @param locale
	 *            - an instance of Locale that the default language and country
	 *            will be set to.
	 * @throws Exception
	 */
	private void setDefaultWesternLanguage(Locale locale) throws Exception {
		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(
						OdfTextProperties.Language)) {
					style.setProperty(OdfTextProperties.Language, locale.getLanguage());
					style.setProperty(OdfTextProperties.Country, locale.getCountry());
				}
			}
		}
	}

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
		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(
						OdfTextProperties.LanguageAsian)) {
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
		OdfOfficeStyles styles = getStylesDom().getOfficeStyles();
		Iterable<OdfDefaultStyle> defaultStyles = styles.getDefaultStyles();
		if (defaultStyles != null) {
			Iterator<OdfDefaultStyle> itera = defaultStyles.iterator();
			while (itera.hasNext()) {
				OdfDefaultStyle style = itera.next();
				if (style.getFamily().getProperties().contains(
						OdfTextProperties.LanguageComplex)) {
					style.setProperty(OdfTextProperties.LanguageComplex, locale.getLanguage());
					style.setProperty(OdfTextProperties.CountryComplex, locale.getCountry());
				}
			}
		}
	}
}
