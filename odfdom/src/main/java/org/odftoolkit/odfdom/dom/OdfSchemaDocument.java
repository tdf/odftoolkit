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
package org.odftoolkit.odfdom.dom;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * A document in ODF is from the package view a directory with a media type.
 * If the media type represents a document described by the ODF 1.2 Schema,
 * certain files are assumed within:
 * content.xml, styles.xml, metadata.xml and settings.xml.
 * 
 * The class represents such a document, providing easier access to its XML files.
 */
public abstract class OdfSchemaDocument extends OdfPackageDocument {

	protected OdfContentDom mContentDom;
	protected OdfStylesDom mStylesDom;
	protected OdfMetaDom mMetaDom;
	protected OdfSettingsDom mSettingsDom;
	protected OdfOfficeStyles mDocumentStyles;

	/**
	 * Creates a new OdfSchemaDocument.
	 * 
	 * @param pkg - the ODF Package that contains the document. A baseURL is being generated based on its location.
	 * @param internalPath - the directory path within the package from where the document should be loaded.
	 * @param mediaTypeString
	 *      - media type of stream. If unknown null can be used.
	 */
	protected OdfSchemaDocument(OdfPackage pkg, String internalPath, String mediaTypeString) {
		super(pkg, internalPath, mediaTypeString);
		ErrorHandler errorHandler = pkg.getErrorHandler();
		if (errorHandler != null) {
			if (pkg.getFileEntry(internalPath + "content.xml") == null && pkg.getFileEntry(internalPath + "styles.xml") == null) {
				try {
					String baseURI = pkg.getBaseURI();
					if (baseURI == null) {
						baseURI = internalPath;
					} else {
						if (!internalPath.equals(ROOT_DOCUMENT_PATH)) {
							baseURI = "/" + internalPath;
						}
					}
					errorHandler.error(new OdfValidationException(OdfSchemaConstraint.DOCUMENT_WITHOUT_CONTENT_NOR_STYLES_XML, baseURI));
				} catch (SAXException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			InputStream mimetypeStream = pkg.getInputStream(OdfPackage.OdfFile.MEDIA_TYPE.getPath(), true);
			if (internalPath.equals(ROOT_DOCUMENT_PATH) && mimetypeStream == null) {
				try {
					errorHandler.error(new OdfValidationException(OdfSchemaConstraint.PACKAGE_SHALL_CONTAIN_MIMETYPE, pkg.getBaseURI()));
				} catch (SAXException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	/**
	 * This enum contains all possible standardized XML ODF files of the OpenDocument document.
	 */
	public static enum OdfXMLFile {

		/** The XML file containing the content of an ODF document as specified by the ODF 1.2 specification part 1. */
		CONTENT("content.xml"),
		/** The XML file containing a predifined set of metadata related to an ODF document as specified by the ODF 1.2 specification part 1. */
		META("meta.xml"),
		/** The XML file containing the settings of an ODF document as specified by the ODF 1.2 specification part 1. */
		SETTINGS("settings.xml"),
		/** The XML file containing the styles of an ODF document as specified by the ODF 1.2 specification part 1. */
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
	 * Get the relative path for an embedded ODF document including its file name.
	 * @param file represents one of the standardized XML ODF files.
	 * @return path to embedded ODF XML file relative to ODF package root.
	 */
	protected String getXMLFilePath(OdfXMLFile file) {
		return file.mFileName;
	}

	/**
	 * Get the URI, where this ODF document is stored.
	 * @return the URI to the ODF document. Returns null if document is not stored yet.
	 */
	public String getBaseURI() {
		return mPackage.getBaseURI();


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
				Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return mDocumentStyles;
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
			Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
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
				Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return mDocumentStyles;
	}

	/**
	 * Return a list of table features in this document.
	 * @return a list of table features in this document.
	 */
	// ToDo: Instead of a method to receive all possible feature/components on the document, there might be a generic or one each element?
	public List<TableTableElement> getTables() {
		List<TableTableElement> tableList = new ArrayList<TableTableElement>();
		try {
			// find tables from content.xml
			OfficeBodyElement officeBody = OdfElement.findFirstChildNode(OfficeBodyElement.class, getContentDom().getRootElement());
			OdfElement contentRoot = OdfElement.findFirstChildNode(OdfElement.class, officeBody);
			tableList = fillTableList(contentRoot, tableList);
			
			// find tables from styles.xml (header & footer)
			Map<String, StyleMasterPageElement> masterPages = getMasterPages();
			StyleMasterPageElement defaultMasterPage = masterPages.get("Standard");
			if (defaultMasterPage != null) {
				tableList = fillTableList(defaultMasterPage, tableList);
			}
		} catch (Exception ex) {
			Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
		return tableList;
	}

	// Only tables being on root level are being considered
	private List<TableTableElement> fillTableList(Element startElement, List<TableTableElement> tableList) {
		NodeList childList = startElement.getChildNodes();
		for (int i = 0;
				i < childList.getLength();
				i++) {
			Node childNode = childList.item(i);
			if (childNode instanceof Element) {
				if(childNode instanceof TableTableElement){
					tableList.add((TableTableElement) childList.item(i));
				}else{
					fillTableList((Element) childNode, tableList);
				}
			}
		}
		return tableList;
	}

	/**
	 * ToDo: Instead of adding all elements using an index to the document, we
	 * might add a pattern to the code generation to create a HashMap either on
	 * demand (whenever such a structure is required from the user) or by
	 * default
	 * 
	 * @deprecated This method will be moved to the generated sources as soon
	 *              code generation was improved!
	 * 
	 */
	public Map<String, StyleMasterPageElement> getMasterPages() throws Exception {

		// get original values:
		OdfStylesDom stylesDoc = getStylesDom();
		OfficeMasterStylesElement masterStyles = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class, stylesDoc.getRootElement());
		Map<String, StyleMasterPageElement> masterPages = null;
		if (masterStyles != null) {
			NodeList lstMasterPages = stylesDoc.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(), "master-page");
			if (lstMasterPages != null && lstMasterPages.getLength() > 0) {
				masterPages = new HashMap();
				for (int i = 0; i < lstMasterPages.getLength(); i++) {
					StyleMasterPageElement masterPage = (StyleMasterPageElement) lstMasterPages.item(i); //Take the node from the list
					//ToDo: Drop Attribute Suffix for methods returning String values and NOT Attributes
					String styleName = masterPage.getStyleNameAttribute();
					masterPages.put(styleName, masterPage);
				}
			}
		}
		return masterPages;
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
		mContentDom = null;
		mStylesDom = null;
		mMetaDom = null;
		mSettingsDom = null;
		mDocumentStyles = null;
		super.close();
	}

	public OdfFileDom getFileDom(OdfXMLFile file) throws Exception {
		return getFileDom(getXMLFilePath(file));
	}
}
