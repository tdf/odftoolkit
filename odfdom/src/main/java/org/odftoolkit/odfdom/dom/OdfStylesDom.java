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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.odftoolkit.odfdom.changes.ChangesFileSaxHandler;
import org.odftoolkit.odfdom.dom.element.office.OfficeAutomaticStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeStylesElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.NamespaceName;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * The DOM representation of the ODF styles.xml file of an ODF document.
 */
public class OdfStylesDom extends OdfFileDom {

    private static final long serialVersionUID = 766167617530147886L;
    // there is one default tab stop width
    private Integer mDefaultTabStopWidth;

    /**
     * Creates the DOM representation of an XML file of an Odf document.
     *
     * @param odfDocument the document the XML files belongs to
     * @param packagePath the internal package path to the XML file
     */
	public OdfStylesDom(OdfSchemaDocument odfDocument, String packagePath) {
        super(odfDocument, packagePath);
    }

    /**
     * Might be used to initialize specific XML Namespace prefixes/URIs for this
     * XML file
     */
    @Override
    protected void initialize() throws SAXException, IOException, ParserConfigurationException {
        for (NamespaceName name : OdfDocumentNamespace.values()) {
            mUriByPrefix.put(name.getPrefix(), name.getUri());
            mPrefixByUri.put(name.getUri(), name.getPrefix());
        }
        try {
            super.initialize(new ChangesFileSaxHandler(this), this);
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
            OdfValidationException ve = new OdfValidationException(OdfSchemaConstraint.DOCUMENT_WITH_EXISTENT_BUT_UNREADABLE_CONTENT_OR_STYLES_XML, mPackage.getBaseURI(), ex, OdfSchemaDocument.OdfXMLFile.STYLES.getFileName());
            ErrorHandler eh = mPackage.getErrorHandler();
            if (eh != null) {
                try {
                    eh.error(ve);
                } catch (SAXException ex1) {
                    Logger.getLogger(OdfStylesDom.class.getName()).log(Level.SEVERE, null, ex1);
                    throw ex1;
                }
            }
        }
	}

    /**
     * Retrieves the Odf Document
     *
     * @return The <code>OdfDocument</code>
     */
    @Override
    public OdfSchemaDocument getDocument() {
        return (OdfSchemaDocument) mPackageDocument;
    }

    /**
     * * @return The root element <office:document-styles> of the styles.xml
     * file as <code>OfficeDocumentStylesElement</code>.
     */
    @Override
    public OfficeDocumentStylesElement getRootElement() {
        return (OfficeDocumentStylesElement) getDocumentElement();
    }

    /**
     * Creates an JDK <code>XPath</code> instance. Initialized with ODF
     * namespaces from <code>OdfDocumentNamespace</code>. Updated with all
     * namespace of the XML file.
     *
     * @return an XPath instance with namespace context set to include the
     * standard ODFDOM prefixes.
     */
    @Override
    public XPath getXPath() {
        if (mXPath == null) {
            mXPath = XPathFactory.newInstance().newXPath();
            mXPath.setNamespaceContext(this);
            for (NamespaceName name : OdfDocumentNamespace.values()) {
                mUriByPrefix.put(name.getPrefix(), name.getUri());
                mPrefixByUri.put(name.getUri(), name.getPrefix());
            }
        }
        return mXPath;
    }

	// ToDo bug 72 - STYLE REFACTORING - THE FOLLOWING METHODS WILL BE RE/MOVED
    // As Package layer should not refer to DOM/DOC layer and DOM files should not
    // handle automatic styles the upcoming DOM Document should capsulate this.
    /**
     * @return the style:office-styles element of this dom. May return null if
     * there is not yet such element in this dom.
     *
     * @see #getOrCreateAutomaticStyles()
     *
     */
    public OdfOfficeStyles getOfficeStyles() {
        return OdfElement.findFirstChildNode(OdfOfficeStyles.class, getFirstChild());
    }

    /**
     * Retrieve the ODF OfficeStyles
     *
     * @return the {
     * @odf.element office:styles} element of this dom or creates a new one.
     *
     */
    public OdfOfficeStyles getOrCreateOfficeStyles() {
        OdfOfficeStyles officeStyles = getOfficeStyles();
        if (officeStyles == null) {
            officeStyles = newOdfElement(OfficeStylesElement.class);

            Node parent = getFirstChild();

            /* from the ODF 1.2 schema
                <define name="office-document-styles">
                    <element name="office:document-styles">
                        <ref name="office-document-common-attrs"/>
                        <ref name="office-font-face-decls"/>
                        <ref name="office-styles"/>
                        <ref name="office-automatic-styles"/>
                        <ref name="office-master-styles"/>
                    </element>
                </define>
            */
            OdfElement sibling = OdfElement.findFirstChildNode(OdfOfficeAutomaticStyles.class, parent);
            if (sibling != null) {
                parent.insertBefore(officeStyles, sibling);
            } else {
                sibling = OdfElement.findFirstChildNode(OdfOfficeMasterStyles.class, parent);
                if (sibling != null) {
                    parent.insertBefore(officeStyles, sibling);
                } else {
                    parent.appendChild(officeStyles);
                }
            }
        }
        return officeStyles;
    }

    /**
     * Retrieve the ODF AutomaticStyles
     *
     * @return the {
     * @odf.element office:automatic-styles} element of this dom. May return
     * null if there is not yet such element in this dom.
     *
     * @see #getOrCreateAutomaticStyles()
     *
     */
    public OfficeAutomaticStylesElement getAutomaticStyles() {
        return OdfElement.findFirstChildNode(OfficeAutomaticStylesElement.class, getFirstChild());
    }

    /**
     * Retrieve the ODF MasterStyles
     *
     * @return the {
     * @odf.element office:master-styles} element of this dom. May return null
     * if there is not yet such element in this dom.
     *
     */
    public OfficeMasterStylesElement getMasterStyles() {
        return OdfElement.findFirstChildNode(OfficeMasterStylesElement.class, getFirstChild());
    }

    /**
     * Retrieve the ODF MasterStyles
     *
     * @return the {
     * @odf.element office:master-styles} element of this dom or creates a new one.
     *
     */
    public OfficeMasterStylesElement getOrCreateMasterStyles() {
        OfficeMasterStylesElement masterStyles = getMasterStyles();
        if (masterStyles == null) {
            masterStyles = newOdfElement(OfficeMasterStylesElement.class);
            Node parent = getFirstChild();
            /* from the ODF 1.2 schema
                <define name="office-document-styles">
                    <element name="office:document-styles">
                        <ref name="office-document-common-attrs"/>
                        <ref name="office-font-face-decls"/>
                        <ref name="office-styles"/>
                        <ref name="office-automatic-styles"/>
                        <ref name="office-master-styles"/>
                    </element>
                </define>
            */
            parent.appendChild(masterStyles);
        }
        return masterStyles;
    }

    /**
     * @return the {
     * @odf.element style:automatic-styles} element of this dom. If it does not
     * yet exists, a new one is inserted into the dom and returned.
     *
     */
    public OdfOfficeAutomaticStyles getOrCreateAutomaticStyles() {

        OdfOfficeAutomaticStyles automaticStyles = getAutomaticStyles();
        if (automaticStyles == null) {
            automaticStyles = newOdfElement(OfficeAutomaticStylesElement.class);

            Node parent = getFirstChild();

            /* from the ODF 1.2 schema
                <define name="office-document-styles">
                    <element name="office:document-styles">
                        <ref name="office-document-common-attrs"/>
                        <ref name="office-font-face-decls"/>
                        <ref name="office-styles"/>
                        <ref name="office-automatic-styles"/>
                        <ref name="office-master-styles"/>
                    </element>
                </define>
            */
            OdfElement sibling = OdfElement.findFirstChildNode(OdfOfficeMasterStyles.class, parent);
            if (sibling != null) {
                parent.insertBefore(automaticStyles, sibling);
            } else {
                parent.appendChild(automaticStyles);
            }
        }
        return automaticStyles;
    }
}
