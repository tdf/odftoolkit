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
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
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
package org.odftoolkit.odfdom.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.ResourceUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.odftoolkit.odfdom.changes.Component;
import org.odftoolkit.odfdom.changes.JsonOperationProducer;
import org.odftoolkit.odfdom.changes.CollabTextDocument;
import org.odftoolkit.odfdom.changes.PageArea;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_DEFAULT;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_EVEN;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_FIRST;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_DEFAULT;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_EVEN;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_FIRST;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterLeftElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderLeftElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfStylesBase;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.odfdom.pkg.rdfa.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
/**
 * A document in ODF is from the package view a directory with a media type. If
 * the media type represents a document described by the ODF 1.2 Schema, certain
 * files are assumed within: content.xml, styles.xml, metadata.xml and
 * settings.xml.
 *
 * The class represents such a document, providing easier access to its XML
 * files.
 */
public abstract class OdfSchemaDocument extends OdfPackageDocument {

    /* OdfFileSaxHandler needs to deal with at least two XML files at the same time.
       They are here cached to not dispatch a parsing, whenever the other is received.
       Package DOM caching is used to map to a inputstream and the inital root before parsing would be used by this mechanism */
    protected OdfContentDom mContentDom;
    protected OdfStylesDom mStylesDom;
    protected OdfMetaDom mMetaDom;
    protected OdfSettingsDom mSettingsDom;
    protected JsonOperationProducer mJsonOperationQueue;

    /**
     * Creates a new OdfSchemaDocument.
     *
     * @param pkg - the ODF Package that contains the document. A baseURL is
     * being generated based on its location.
     * @param internalPath - the directory path within the package from where
     * the document should be loaded.
     * @param mediaTypeString - media type of stream. If unknown null can be
     * used.
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
     * This enum contains all possible standardized XML ODF files of the
     * OpenDocument document.
     */
    public static enum OdfXMLFile {

        /**
         * The XML file containing the content of an ODF document as specified
         * by the ODF 1.2 specification part 1.
         */
        CONTENT("content.xml"),
        /**
         * The XML file containing a predifined set of metadata related to an
         * ODF document as specified by the ODF 1.2 specification part 1.
         */
        META("meta.xml"),
        /**
         * The XML file containing the settings of an ODF document as specified
         * by the ODF 1.2 specification part 1.
         */
        SETTINGS("settings.xml"),
        /**
         * The XML file containing the styles of an ODF document as specified by
         * the ODF 1.2 specification part 1.
         */
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
     * @return JSONObject of operations
     */
    // ToDo - JSONObject is to be considered..
    public JsonOperationProducer getJsonOperationQueue() {
        return mJsonOperationQueue;
    }

    public void setJsonOperationQueue(JsonOperationProducer queue) {
        mJsonOperationQueue = queue;
    }

    public JSONObject getOperations(CollabTextDocument operationDoc)
    	throws SAXException, JSONException, IOException {

    	JSONObject ops = null;
        JsonOperationProducer queue = getJsonOperationQueue();
        if (queue == null) {
            try {
                this.getStylesDom();
                this.getContentDom();
                queue = getJsonOperationQueue();
            } catch (SAXException ex) {
                Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            }
        }
        if (queue != null) {
            ops = queue.getDocumentOperations();
        }
        return ops;
    }

    /**
     * The component tree is a high level abstraction of components (table,
     * paragraph, character, etc.) from the XML implementation details of the
     * document.
     */
    private Component mRootComponent;

    /**
     * Returns the component tree of the document. The component tree is a high
     * level abstraction of components (table, paragraph, character, etc.) from
     * the XML implementation details of the document.
     *
     * The DOM of the content.xml will be created if not done before.
     */
    public Component getRootComponent() {
        if (mRootComponent == null) {
            try {
                // Access the DOM of the content.xml so the XML is parsed once!!
                this.getContentDom();
            } catch (Exception ex) {
                Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return mRootComponent;
    }

    /**
     * Returns the component tree of the document. The component tree is a high
     * level abstraction of components (table, paragraph, character, etc.) from
     * the XML implementation details of the document.
     *
     * The DOM of the content.xml will be created if not done before.
     *
     * @param masterStyleName the name of the master style
     * @param localName the local name of the header or footer XML element
     * @return the header or footer element belonging to the given master page
     * style
     */
    public OdfElement getRootComponentElement(String masterStyleName, PageArea pageArea, boolean createIfNotExisting) {
        OdfElement targetElement = null;
        try {
            OdfStylesDom stylesDom = getStylesDom();
            if (stylesDom != null) {
                OdfOfficeMasterStyles masterStyles = stylesDom.getMasterStyles();
                if (masterStyles == null) {
                    masterStyles = stylesDom.getOrCreateMasterStyles();
                }
                StyleMasterPageElement masterPage = masterStyles.getOrCreateMasterPage(masterStyleName);
                if (pageArea.getPageAreaName().contains("header")) {
                    String localName = null;
                    if (pageArea.equals(HEADER_FIRST)) {
                        // header-first
                        localName = HEADER_FIRST.getLocalName();
                        // targetElement =  OdfXMLFactory.newOdfElement(stylesDom, STYLE_FIRST_PAGE);
                    } else if (pageArea.equals(HEADER_EVEN)) {
                        localName = HEADER_EVEN.getLocalName();
                    } else {
                        localName = HEADER_DEFAULT.getLocalName();
                    }
                    targetElement = (OdfElement) masterPage.getChildElement(StyleHeaderElement.ELEMENT_NAME.getUri(), localName, 0);

                    OdfOfficeAutomaticStyles autoStyles = stylesDom.getOrCreateAutomaticStyles();
                    String pageLayoutName = masterPage.getStylePageLayoutNameAttribute();
                    if (targetElement == null && createIfNotExisting) { // create a new page area
                        if (pageArea.equals(HEADER_FIRST)) {
                            // header-first
                            targetElement = OdfXMLFactory.newOdfElement(stylesDom, STYLE_HEADER_FIRST);
                        } else if (pageArea.equals(HEADER_EVEN)) {
                            targetElement = new StyleHeaderLeftElement(stylesDom);
                            OdfStylePageLayout pageLayout = null;
                            if(pageLayoutName != null && !pageLayoutName.isEmpty()){
                                pageLayout = autoStyles.getOrCreatePageLayout(pageLayoutName);
                            }else{
                                pageLayout = autoStyles.newPageLayout();
                            }
                            pageLayout.setStylePageUsageAttribute("right");

                        } else {
                            targetElement = new StyleHeaderElement(stylesDom);
                        }
                        masterPage.appendChild(targetElement);
                    }
                } else {
                    String localName = null;
                    if (pageArea.equals(FOOTER_FIRST)) {
                        // header-first
                        localName = FOOTER_FIRST.getLocalName();
                        // targetElement =  OdfXMLFactory.newOdfElement(stylesDom, STYLE_FIRST_PAGE);
                    } else if (pageArea.equals(FOOTER_EVEN)) {
                        localName = FOOTER_EVEN.getLocalName();
                    } else {
                        localName = FOOTER_DEFAULT.getLocalName();
                    }
                    targetElement = (OdfElement) masterPage.getChildElement(StyleFooterElement.ELEMENT_NAME.getUri(), localName, 0);

                    OdfOfficeAutomaticStyles autoStyles = stylesDom.getOrCreateAutomaticStyles();
                    String pageLayoutName = masterPage.getStylePageLayoutNameAttribute();
                    if (targetElement == null && createIfNotExisting) { // create a new page area
                        if (pageArea.equals(FOOTER_FIRST)) {
                            // header-first
                            targetElement = OdfXMLFactory.newOdfElement(stylesDom, STYLE_FOOTER_FIRST);
                        } else if (pageArea.equals(FOOTER_EVEN)) {
                            targetElement = new StyleFooterLeftElement(stylesDom);
                            OdfStylePageLayout pageLayout = autoStyles.getOrCreatePageLayout(pageLayoutName);
                            pageLayout.setStylePageUsageAttribute("right");

                        } else {
                            targetElement = new StyleFooterElement(stylesDom);
                        }
                        masterPage.appendChild(targetElement);
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return targetElement;
    }
    private static final OdfName STYLE_HEADER_FIRST = OdfName.newName(OdfDocumentNamespace.STYLE, "header-first");
    private static final OdfName STYLE_FOOTER_FIRST = OdfName.newName(OdfDocumentNamespace.STYLE, "footer-first");

    /**
     * For instance, header and footer have their own component trees aside the main document.
     * Therefore in a text document may exist three root components.
     */
    public void setRootComponent(Component rootComponent) {
        mRootComponent = rootComponent;
    }

    /**
     * Gets the ODF content.xml file as stream.
     *
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
     * Get the relative path for an embedded ODF document including its file
     * name.
     *
     * @param file represents one of the standardized XML ODF files.
     * @return path to embedded ODF XML file relative to ODF package root.
     */
    protected String getXMLFilePath(OdfXMLFile file) {
        return file.mFileName;
    }

    /**
     * Get the URI, where this ODF document is stored.
     *
     * @return the URI to the ODF document. Returns null if document is not
     * stored yet.
     */
    public String getBaseURI() {
        return mPackage.getBaseURI();
    }

    /**
     * Return the ODF type-based content DOM of the content.xml
     *
     * @return ODF type-based content DOM or null if no content.xml exists.
     * @throws Exception if content DOM could not be initialized
     */
    public OdfContentDom getContentDom() throws SAXException, IOException {
        if(mContentDom == null){
            mContentDom = (OdfContentDom) getCachedDom(getAbsoluteFilePath(OdfXMLFile.CONTENT.getFileName()));
            if(mContentDom == null){
                mContentDom =  new OdfContentDom(this, getAbsoluteFilePath(OdfXMLFile.CONTENT.getFileName()));
            }
        }
        return mContentDom;
    }

    /**
     * Return the ODF type-based styles DOM of the styles.xml
     *
     * @return ODF type-based styles DOM or null if no styles.xml exists.
     * @throws Exception if styles DOM could not be initialized
     */
    public OdfStylesDom getStylesDom() throws SAXException, IOException {
        if(mStylesDom == null){
            mStylesDom = (OdfStylesDom) getCachedDom(getAbsoluteFilePath(OdfXMLFile.STYLES.getFileName()));;
            if(mStylesDom == null){
                mStylesDom =  new OdfStylesDom(this, getAbsoluteFilePath(OdfXMLFile.STYLES.getFileName()));
            }
        }
        return mStylesDom;
    }

    /**
     * Return the ODF type-based metadata DOM of the meta.xml
     *
     * @return ODF type-based meta DOM or null if no meta.xml exists.
     * @throws Exception if meta DOM could not be initialized
     */
    public OdfMetaDom getMetaDom() throws SAXException, IOException {
        if(mMetaDom == null){
            mMetaDom = (OdfMetaDom) getCachedDom(getAbsoluteFilePath(OdfXMLFile.META.getFileName()));;
            if(mMetaDom == null){
                mMetaDom =  new OdfMetaDom(this, getAbsoluteFilePath(OdfXMLFile.META.getFileName()));
            }
        }
        return mMetaDom;
    }

    /**
     * Return the ODF type-based settings DOM of the settings.xml
     *
     * @return ODF type-based settings DOM or null if no settings.xml exists.
     * @throws Exception if settings DOM could not be initialized
     */
    public OdfSettingsDom getSettingsDom() throws SAXException, IOException {
        if(mSettingsDom == null){
            mSettingsDom = (OdfSettingsDom) getCachedDom(getAbsoluteFilePath(OdfXMLFile.SETTINGS.getFileName()));;
            if(mSettingsDom == null){
                mSettingsDom =  new OdfSettingsDom(this, getAbsoluteFilePath(OdfXMLFile.SETTINGS.getFileName()));
            }
        }
        return mSettingsDom;
    }

    /**
     * Sets the ODF type-based content DOM of the content.xml
     *
     * @param contentDom ODF type-based content DOM or null if no content.xml
     * exists.
     */
    public void setContentDom(OdfContentDom contentDom) {
        mContentDom = contentDom;
    }

    /**
     * Sets the ODF type-based styles DOM of the styles.xml
     *
     * @param stylesDom ODF type-based styles DOM or null if no styles.xml
     * exists.
     */
    public void setStylesDom(OdfStylesDom stylesDom) {
        mStylesDom = stylesDom;
    }

    /**
     * Sets the ODF type-based meta DOM of the meta.xml
     *
     * @param metaDom ODF type-based meta DOM or null if no meta.xml exists.
     */
    public void setMetaDom(OdfMetaDom metaDom) {
        mMetaDom = metaDom;
    }

    /**
     * Sets the ODF type-based settings DOM of the settings.xml
     *
     * @param settingsDom ODF type-based settings DOM or null if no settings.xml
     * exists.
     */
    public void setSettingsDom(OdfSettingsDom settingsDom) {
        mSettingsDom = settingsDom;
    }


    /**
     *
     * @return the office:styles element from the styles dom
     */
    public OdfOfficeStyles getDocumentStyles() {
        OdfOfficeStyles documentStyles = null;
        try {
            OdfStylesDom stylesDom = getStylesDom();
            if (stylesDom != null) {
                documentStyles = stylesDom.getOfficeStyles();
            }
        } catch (Exception ex) {
            Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentStyles;
    }

    /**
     * return the office:master-styles element of this document.
     *
     * @return the office:master-styles element
     */
    public OdfOfficeMasterStyles getOfficeMasterStyles() {
        OdfOfficeMasterStyles officeMasterStyles = null;
        try {
            OdfStylesDom stylesDom = getStylesDom();
            if (stylesDom != null) {
                officeMasterStyles = stylesDom.getOrCreateMasterStyles();
            }
        } catch (Exception ex) {
            Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return officeMasterStyles;
    }

    /**
     *
     * @return the office:styles element from the styles dom. If there is not
     * yet such an element, it is created.
     */
    public OdfOfficeStyles getOrCreateDocumentStyles() {
        OdfOfficeStyles documentStyles = null;
        try {
            OdfStylesDom stylesDom = getStylesDom();
            if (stylesDom != null) {
                documentStyles = stylesDom.getOfficeStyles();
            }
            if(documentStyles == null){
                stylesDom.getOrCreateOfficeStyles();
            }
        } catch (Exception ex) {
            Logger.getLogger(OdfSchemaDocument.class.getName()).log(Level.SEVERE, null, ex);
        }
        return documentStyles;
    }

    public OdfStyle getStyleByName(OdfStyleFamily styleFamily, String styleName)
    	throws SAXException, IOException {

    	OdfStyle odfStyle = getStyleByName(getStylesDom(), styleFamily, styleName);
    	if(odfStyle==null) {
        	final OdfContentDom odfContentDom = getContentDom();
        	if(odfContentDom!=null) {
        		odfStyle = getStyleByName(odfContentDom.getAutomaticStyles(), styleFamily, styleName);
        	}
    	}
    	return odfStyle;
    }

    private OdfStyle getStyleByName(OdfStylesDom odfStylesDom, OdfStyleFamily styleFamily, String styleName) {
    	OdfStyle odfStyle = null;
    	if(odfStylesDom!=null) {
    		odfStyle = getStyleByName(odfStylesDom.getOfficeStyles(), styleFamily, styleName);
    		if(odfStyle==null) {
    			odfStyle = getStyleByName(odfStylesDom.getAutomaticStyles(), styleFamily, styleName);
    		}
    	}
    	return odfStyle;
    }

    private OdfStyle getStyleByName(OdfStylesBase odfStylesBase, OdfStyleFamily styleFamily, String styleName) {
    	OdfStyle odfStyle = null;
		if(odfStylesBase!=null) {
			odfStyle = odfStylesBase.getStyle(styleName, styleFamily);
		}
    	return odfStyle;
    }

    public OdfStyle getStyleByDisplayName(OdfStyleFamily styleFamily, String styleDisplayName)
    	throws SAXException, IOException {

    	OdfStyle odfStyle = getStyleByDisplayName(getStylesDom(), styleFamily, styleDisplayName);
    	if(odfStyle==null) {
    		final OdfContentDom odfContentDom = getContentDom();
    		if(odfContentDom!=null) {
    			odfStyle = getStyleByDisplayName(odfContentDom.getAutomaticStyles(), styleFamily, styleDisplayName);
    		}
    	}
    	return odfStyle;
    }

    private OdfStyle getStyleByDisplayName(OdfStylesDom odfStylesDom, OdfStyleFamily styleFamily, String styleName) {
    	OdfStyle odfStyle = null;
    	if(odfStylesDom!=null) {
    		odfStyle = getStyleByDisplayName(odfStylesDom.getOfficeStyles(), styleFamily, styleName);
    		if(odfStyle==null) {
    			odfStyle = getStyleByDisplayName(odfStylesDom.getAutomaticStyles(), styleFamily, styleName);
    		}
    	}
    	return odfStyle;
    }

    private OdfStyle getStyleByDisplayName(OdfStylesBase odfStylesBase, OdfStyleFamily styleFamily, String styleName) {
		if(odfStylesBase!=null) {
			for(OdfStyle odfStyle:odfStylesBase.getStylesForFamily(styleFamily)) {
				final String displayName = odfStyle.getStyleDisplayNameAttribute();
				if(displayName!=null&&displayName.equals(styleName)) {
					return odfStyle;
				}
			}
		}
    	return null;
    }

    /**
     * Return a list of table features in this document.
     *
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
            if(masterPages != null){
                StyleMasterPageElement defaultMasterPage = masterPages.get("Standard");
                if (defaultMasterPage != null) {
                    tableList = fillTableList(defaultMasterPage, tableList);
                }
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
                if (childNode instanceof TableTableElement) {
                    tableList.add((TableTableElement) childList.item(i));
                } else {
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
     * code generation was improved!
     *
     */
    @Deprecated
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
	 * After execution of this method, this class is no longer usable.
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
        super.close();
    }

    public OdfFileDom getFileDom(OdfXMLFile file) throws SAXException, IOException {
        return getFileDom(getXMLFilePath(file));
    }

	/**
	 * Get all two types of RDF Metadata through GRDDL XSLT:
	 * http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#__RefHeading__1415068_253892949
	 */
	public Model getRDFMetadata() throws Exception {
		Model m = getInContentMetadata().union(this.getManifestRDFMetadata());
		return m;
}

	/**
	 * Get In Content RDF Metadata through GRDDL XSLT
	 * http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#__RefHeading__1415070_253892949
	 */
	public Model getInContentMetadata() throws Exception {
		Model documentRDFModel = ModelFactory.createDefaultModel();
		Model fileRDFModel = null;
		for (String internalPath : this.getPackage().getFilePaths()) {
			for (OdfXMLFile file : OdfXMLFile.values()) {
				if (Util.isSubPathOf(internalPath, this.getDocumentPath())
						&& internalPath.endsWith(file.getFileName())) {
					fileRDFModel = getXMLFileMetadata(internalPath);
					if (fileRDFModel.size() > 0) {
						documentRDFModel = documentRDFModel.union(fileRDFModel);
					}
					break;
				}
			}
		}
		if (fileRDFModel.size() > 0) {
			documentRDFModel = documentRDFModel.union(fileRDFModel);
		}
		return documentRDFModel;
	}

	/**
	 * Get in-content metadata cache model
	 *
	 * @return The in-content metadata cache model
	 * @throws Exception
	 */
	public Model getInContentMetadataFromCache() throws Exception {
		Model m = ModelFactory.createDefaultModel();
		// find and merge the RDF triples cache from the OdfXMLFile files
		for (OdfXMLFile file : OdfXMLFile.values()) {
			for (Model m1 : this.getFileDom(file).getInContentMetadataCache().values()) {
				m = m.union(m1);
			}
		}
		return m;
	}

	/**
	 * Get RDF metadata from manifest.rdf and those rdf files registered in the
	 * manifest.xml as "application/rdf+xml" through GRDDL XSLT
	 * http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#__RefHeading__1415072_253892949
	 */
	public Model getManifestRDFMetadata() throws Exception {
		Model m = ModelFactory.createDefaultModel();
		for (String internalPath : this.getPackage().getFilePaths()) {
			if (Util.isSubPathOf(internalPath, this.getDocumentPath()) && this.getPackage().getMediaTypeString(internalPath).endsWith("application/rdf+xml")) {
				Model m1 = ModelFactory.createDefaultModel();
				String RDFBaseUri = Util.getRDFBaseUri(this.getPackage().getBaseURI(), internalPath);
				m1.read(new InputStreamReader(this.getPackage().getInputStream(internalPath), "utf-8"), RDFBaseUri);
				// remove the last SLASH at the end of the RDFBaseUri:
				// test_rdfmeta.odt/ --> test_rdfmeta.odt
				ResourceUtils.renameResource(m1.getResource(RDFBaseUri), RDFBaseUri.substring(0, RDFBaseUri.length() - 1));
				if (m1.size() > 0) {
					m = m.union(m1);
				}
			}
		}
		return m;
	}

	/**
	 * Get in-content metadata model of bookmarks
	 *
	 * @return The in-content metadata model of bookmarks
	 * @throws Exception
	 */
	public Model getBookmarkRDFMetadata() throws Exception {
		Model m = ModelFactory.createDefaultModel();
		for (OdfXMLFile file : OdfXMLFile.values()) {
			OdfFileDom dom = getFileDom(file);
			m = m.union(dom.getBookmarkRDFMetadata());
		}
		return m;
	}
}
