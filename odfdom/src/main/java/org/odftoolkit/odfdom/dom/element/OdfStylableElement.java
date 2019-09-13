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
package org.odftoolkit.odfdom.dom.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.OdfStylePropertySet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.type.StyleName;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

// ToDo: change modifier public to package after refactoring
abstract public class OdfStylableElement extends OdfElement implements
        OdfStylePropertySet {

    private static final long serialVersionUID = -7828513537641758879L;
    // ToDo: Overall StyleRefactoring: DOM Layer reaches to upper layer here...
    private OdfStyle mAutomaticStyle;
    protected OdfStyleFamily mFamily;
    protected OdfName mStyleNameAttrib;
    private OdfSchemaDocument mOdfSchemaDocument;
    boolean misAutomaticStyleSet = false;
    ErrorHandler mErrorHandler;

    /**
     * Creates a new instance of OdfElementImpl
     *
     * @param ownerDocument
     * @param name
     * @param family
     * @param styleNameAttrib
     * @throws DOMException
     */
    public OdfStylableElement(OdfFileDom ownerDocument, OdfName name,
            OdfStyleFamily family, OdfName styleNameAttrib) throws DOMException {
        super(ownerDocument, name.getUri(), name.getQName());
        mFamily = family;
        mStyleNameAttrib = styleNameAttrib;
        mOdfSchemaDocument = (OdfSchemaDocument) ownerDocument.getDocument();
        mErrorHandler = ownerDocument.getDocument().getPackage().getErrorHandler();
    }

    /**
     * Retrieve or create unique ODF AutomaticStyle
     *
     * @return The <code>StyleStyleElement</code> element
     */
    private StyleStyleElement createAutomaticStyle(Boolean createStyleName, OdfStyleFamily styleFamily) {

        if (styleFamily == null) {
            styleFamily = getStyleFamily();
        }

        if ((mAutomaticStyle == null)
                || (mAutomaticStyle.getStyleUserCount() > 1)) {
            // we need a new automatic style
            OdfOfficeAutomaticStyles automatic_styles = getOrCreateAutomaticStyles();
            // ToDo: Move into a createAutomaticStyles function
            if (automatic_styles == null) {
                OdfFileDom fileDom = ((OdfFileDom) this.getOwnerDocument());
                OdfOfficeAutomaticStyles newOfficeAutoStyles = fileDom.newOdfElement(OdfOfficeAutomaticStyles.class);
                OdfElement rootElement = fileDom.getRootElement();
                NodeList rootChildren = rootElement.getChildNodes();
                boolean hasInserted = false;
                for (int i = 0; i < rootChildren.getLength(); i++) {
                    Node currentNode = rootChildren.item(i);
                    if (currentNode instanceof Element) {
                        String elementName = ((Element) currentNode).getNodeName();
                        if (elementName.equals("office:body") || elementName.equals("office:master-styles")) {
                            rootElement.insertBefore(newOfficeAutoStyles, currentNode);
                            hasInserted = true;
                            break;
                        }
                    } else {
                        continue;
                    }
                }
                if (!hasInserted) {
                    rootElement.appendChild(newOfficeAutoStyles);
                }
                automatic_styles = newOfficeAutoStyles;
            }
            if (automatic_styles != null) {
                String styleName = getStyleName();
                String parentName = null;
                mAutomaticStyle = automatic_styles.getStyle(styleName, styleFamily);
                if (mAutomaticStyle == null) {
                    mAutomaticStyle = automatic_styles.newStyle(styleFamily);
                    if (!styleName.isEmpty()) {
                        mAutomaticStyle.setStyleParentStyleNameAttribute(styleName);
                    }
                } else {
                    parentName = mAutomaticStyle.getStyleParentStyleNameAttribute();
                    mAutomaticStyle.removeStyleUser(this);
                    mAutomaticStyle = automatic_styles.makeStyleUnique(mAutomaticStyle);
                    if (parentName != null && !parentName.isEmpty()) {
                        mAutomaticStyle.setStyleParentStyleNameAttribute(parentName);
                    }
                }
                mAutomaticStyle.addStyleUser(this);
                if (createStyleName) {
                    setStyleName(mAutomaticStyle.getStyleNameAttribute());
                }
            }
        }
        return mAutomaticStyle;
    }

    /**
     * Retrieve or create unique ODF AutomaticStyle
     *
     * @return The <code>StyleStyleElement</code> element
     */
    public StyleStyleElement getOrCreateUnqiueAutomaticStyle() {
        return createAutomaticStyle(Boolean.TRUE, null);
    }

    /**
     * Retrieve or create unique ODF AutomaticStyle
     *
     * @return The <code>StyleStyleElement</code> element
     */
    public StyleStyleElement getOrCreateUnqiueAutomaticStyle(Boolean createStyleName, OdfStyleFamily styleFamily) {
        return createAutomaticStyle(createStyleName, styleFamily);
    }

    /**
     * Retrieve ODF OfficeAutomaticStyles
     *
     * @return the <code>OdfOfficeAutomaticStyles</code> element that contains
     * the automatic style for this element. A new node will be created if not
     * existent.
     */
    public OdfOfficeAutomaticStyles getOrCreateAutomaticStyles() {
        OdfFileDom fileDom = (OdfFileDom) this.ownerDocument;
        if (fileDom != null) {
            if (fileDom instanceof OdfContentDom) {
                return ((OdfContentDom) fileDom).getOrCreateAutomaticStyles();
            } else if (fileDom instanceof OdfStylesDom) {
                return ((OdfStylesDom) fileDom).getOrCreateAutomaticStyles();
            } else {
                // if not content.xml nor styles.xml
                return null;
            }
        } else {
            // if the element does not belong to a OdfFileDOM
            return null;
        }
    }

    /**
     * Retrieve ODF OfficeAutomaticStyles
     *
     * @return the <code>OdfOfficeAutomaticStyles</code> element that contains
     * the automatic style for this element, or null if not available.
     */
    public OdfOfficeAutomaticStyles getAutomaticStyles() {
        OdfFileDom fileDom = (OdfFileDom) this.ownerDocument;
        if (fileDom != null) {
            if (fileDom instanceof OdfContentDom) {
                return ((OdfContentDom) fileDom).getAutomaticStyles();
            } else if (fileDom instanceof OdfStylesDom) {
                return ((OdfStylesDom) fileDom).getAutomaticStyles();
            } else {
                // if not content.xml nor styles.xml
                return null;
            }
        } else {
            // if the element does not belong to a OdfFileDOM
            return null;
        }
    }

    /**
     * Set style attribute value with uri and name
     *
     * @param uri The namespace uri
     * @param qname The qualified name of the attribute
     * @param value The attribute value
     */
    @Override
    public void setAttributeNS(String uri, String qname, String value) {
        super.setAttributeNS(uri, qname, value);

        // check if style has changed
        if (mStyleNameAttrib.equals(uri, qname)) {
            OdfStyle autoStyle = null;

            // optimization: check if we already know this automatic style
            if ((mAutomaticStyle != null)
                    && (mAutomaticStyle.getStyleNameAttribute().equals(value))) {
                // nothing todo
            } else {
                // register new automatic style
                OdfOfficeAutomaticStyles automatic_styles = getAutomaticStyles();
                if (automatic_styles != null) {
                    autoStyle = automatic_styles.getStyle(value,
                            getStyleFamily());
                }

                if (mAutomaticStyle != null) {
                    mAutomaticStyle.removeStyleUser(this);
                }

                mAutomaticStyle = autoStyle;

                if (mAutomaticStyle != null) {
                    mAutomaticStyle.addStyleUser(this);
                }

                if (mErrorHandler != null) {
                    // Is String from type NCName? == http://www.w3.org/TR/xmlschema-2/#NCName
                    if (!StyleName.isValid(value)) {
                        try {
                            mErrorHandler.error(new OdfValidationException(OdfSchemaConstraint.DOCUMENT_XML_INVALID_ATTRIBUTE_VALUE, value, "qname"));
                        } catch (SAXException ex) {
                            Logger.getLogger(StyleStyleElement.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieve style name
     *
     * @return the style name
     */
    public String getStyleName() {
        return getAttributeNS(mStyleNameAttrib.getUri(), mStyleNameAttrib.getLocalName());
    }

    /**
     * Set style name
     *
     * @param name The style name
     */
    public void setStyleName(String name) {
        setAttributeNS(mStyleNameAttrib.getUri(), mStyleNameAttrib.getQName(),
                name);
        // if the style name is changed, the reference to the style has to be updated as well
        misAutomaticStyleSet = false;
    }

    /**
     * Retrieve ODF AutomaticStyle
     *
     * @return the <code>OdfStyle</code> element
     */
    public OdfStyle getAutomaticStyle() {
        if (!misAutomaticStyleSet) {
            OdfOfficeAutomaticStyles automatic_styles = getAutomaticStyles();
            if (automatic_styles != null) {
                mAutomaticStyle = automatic_styles.getStyle(getStyleName(), getStyleFamily());
            }
            misAutomaticStyleSet = true;
        }
        return mAutomaticStyle;
    }

    /**
     * Judge if there is an automatic style, not necessary including properties
     *
     * @return true if there is an automatic style
     */
    public boolean hasAutomaticStyle() {
        if (!misAutomaticStyleSet) {
            getAutomaticStyle();
        }
        return mAutomaticStyle != null;
    }

    /*
     * public void setLocalStyleProperties(OdfStyle style) { mAutomaticStyle =
     * style.getAsLocalStyle(); setStyleName(style.getName()); }
     */
    /**
     * Returns a DocumentStyle if there is no local style
     *
     * @return The <code>OdfStyle</code> element
     *
     *
     */
    public OdfStyle reuseDocumentStyle(String styleName) {
        OdfStyle style = null;
        if (styleName != null) {
            style = mOdfSchemaDocument.getDocumentStyles().getStyle(styleName,
                    getStyleFamily());
            if (style != null) {
                setDocumentStyle(style);
            }
        }
        return style;
    }

    /**
     * Set ODF DocumentStyle
     *
     * @param style The document style
     */
    public void setDocumentStyle(OdfStyle style) {
        // when there is a local style, the document style becomes the parent
        // of the local style
        if (!misAutomaticStyleSet) {
            getAutomaticStyle();
        }
        if (mAutomaticStyle != null) {
            mAutomaticStyle.setStyleParentStyleNameAttribute(style.getStyleNameAttribute());
        } else {
            setStyleName(style.getStyleNameAttribute());
        }
    }

    // protected static final String LOCAL_STYLE_PREFIX = "#local-style";

    /*
     * public OdfStyle newDocumentStyle(String name) { OdfStyle newDocStyle =
     * mFamily.newStyle(name, mOdfSchemaDocument.getDocumentStyles());
     * setDocumentStyle(newDocStyle); return newDocStyle; }
     */
    /**
     * Retrieve ODF DocumentStyle
     *
     * @return the document style
     */
    public OdfStyle getDocumentStyle() {
        OdfStyle odfStyle = null;
        String styleName = getDocumentStyleName();
        OdfOfficeStyles documentStyles = mOdfSchemaDocument.getDocumentStyles();
        if (documentStyles != null) {
            odfStyle = documentStyles.getStyle(styleName, getStyleFamily());
        }
        return odfStyle;
    }

    public String getDocumentStyleName() {
        String styleName = null;
        if (!misAutomaticStyleSet) {
            getAutomaticStyle();
        }
        if (mAutomaticStyle != null) {
            styleName = mAutomaticStyle.getStyleParentStyleNameAttribute();
        } else {
            String automaticStyleName = getStyleName();
            OdfOfficeStyles officeStyles = mOdfSchemaDocument.getDocumentStyles();
            if (officeStyles != null && officeStyles.getStyle(automaticStyleName, getStyleFamily()) != null) {
                styleName = automaticStyleName;
            }
        }
        return styleName;
    }

    /**
     *
     * @return true if there is a document style.
     */
    public boolean hasDocumentStyle() {
        String documentStyleName = getDocumentStyleName();
        return documentStyleName != null && !documentStyleName.isEmpty() && !documentStyleName.equals("null");
    }

    /*
     * public OdfStyle getAutomaticStyle() { if (mAutomaticStyle == null) {
     * mAutomaticStyle = mFamily.newStyle(LOCAL_STYLE_PREFIX, null); // if there
     * is already a document style, but no local style String styleName = null;
     * if ((styleName = getStyleName()) != null) {
     * mAutomaticStyle.setParentName(styleName); } } return mAutomaticStyle; }
     */
    /**
     * Retrieve ODF style family
     *
     * @return the style family.
     */
    public OdfStyleFamily getStyleFamily() {
        return mFamily;
    }

    /*
     * public OdfStyle getMergedStyle() { OdfStyle merged = new
     * OdfStyle("#merged-style", getStyleFamily()); OdfStyle docStyle =
     * getDocumentStyle(); if (mAutomaticStyle != null) { // a document style
     * may be referenced indirectly from the local style... if (docStyle ==
     * null) { docStyle =
     * mOdfSchemaDocument.getDocumentStyles().getStyle(mAutomaticStyle
     * .getParentName()); } // copy local style to merged style
     * mAutomaticStyle.copyTo(merged, true,false); }
     *
     * // copy doc style to merged style // copyTo only copies properties that
     * are not already set at the // target style if (docStyle != null) {
     * docStyle.copyTo(merged, true,false); }
     *
     * return merged; }
     */
    /**
     * Retrieve ODF style property
     *
     * @param property The style property
     * @return string for a property.
     */
    @Override
    public String getProperty(OdfStyleProperty property) {
        // first try automatic style
        if (!misAutomaticStyleSet) {
            getAutomaticStyle();
        }
        StyleStyleElement style = mAutomaticStyle;

        if (style == null) {
            style = getOfficeStyle();
        }

        if (style != null) {
            return style.getProperty(property);
        }

        return null;
    }

    /**
     * Retrieve the set of ODF style proerties
     *
     * @param properties
     * @return a map of all the properties.
     */
    @Override
    public Map<OdfStyleProperty, String> getProperties(
            Set<OdfStyleProperty> properties) {
        HashMap<OdfStyleProperty, String> map = new HashMap<OdfStyleProperty, String>();
        for (OdfStyleProperty property : properties) {
            map.put(property, getProperty(property));
        }

        return map;
    }

    /**
     * Retrieve the set of strict ODF properties
     *
     * @return a set of all the properties from the style family.
     */
    @Override
    public Set<OdfStyleProperty> getStrictProperties() {
        return getStyleFamily().getProperties();
    }

    /**
     * Judge if there is an automatic style with this property
     *
     * @param property
     * @return true if there is an automatic style with this property.
     */
    @Override
    public boolean hasProperty(OdfStyleProperty property) {
        if (!misAutomaticStyleSet) {
            getAutomaticStyle();
        }
        return (mAutomaticStyle != null)
                && mAutomaticStyle.hasProperty(property);
    }

    /**
     * Remove the ODF property
     *
     * @param property
     */
    @Override
    public void removeProperty(OdfStyleProperty property) {
        if (mAutomaticStyle != null) {
            mAutomaticStyle.removeProperty(property);
        }
    }

    /**
     * Set ODF properties
     *
     * @param properties
     */
    @Override
    public void setProperties(Map<OdfStyleProperty, String> properties) {
        for (Map.Entry<OdfStyleProperty, String> entry : properties.entrySet()) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Set ODF style property with value
     *
     * @param property
     * @param value
     */
    @Override
    public void setProperty(OdfStyleProperty property, String value) {
        getOrCreateUnqiueAutomaticStyle().setProperty(property, value);
    }

    @Override
    protected void onInsertNode() {
        // whenever a OdfStyleableElement is inserted into content.xml or styles.xml,
        super.onInsertNode();

        String stylename = getStyleName();
        // if it has the stylename
        if (stylename.length() != 0) {
//            if (!misAutomaticStyleSet) {
//                getAutomaticStyle();
//            }
//            if (mAutomaticStyle != null) {
//                // and the style belongs to automatic style
//                if (mAutomaticStyle.getStyleNameAttribute().equals(stylename)) {
//                    // add a user to the style
//                    mAutomaticStyle.addStyleUser(this);
//                    return;
//                }
//                mAutomaticStyle.removeStyleUser(this);
//                mAutomaticStyle = null;
//            }

            OdfOfficeAutomaticStyles automatic_styles = this.getAutomaticStyles();
            // sthe style belongs to automatic style
            if (automatic_styles != null) {
                if (!misAutomaticStyleSet) {
                    getAutomaticStyle();
                }
                if (mAutomaticStyle != null) {
                    mAutomaticStyle.addStyleUser(this);
                }
            }
        }
    }

    /**
     *
     */
    @Override
    protected void onRemoveNode() {
        super.onInsertNode();

        if (this.mAutomaticStyle != null) {
            this.mAutomaticStyle.removeStyleUser(this);
            this.mAutomaticStyle = null;
            misAutomaticStyleSet = false;
        }
    }

    // todo: rename after newName rid of deprecated getDocumentStyle()
    private OdfStyle getOfficeStyle() {
        OdfOfficeStyles styles = this.mOdfSchemaDocument.getDocumentStyles();
        if (styles != null) {
            return styles.getStyle(getStyleName(), getStyleFamily());
        } else {
            return null;
        }
    }
}
