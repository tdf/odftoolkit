/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.odftoolkit.odfdom.dom.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.office.OdfAutomaticStyles;
import org.odftoolkit.odfdom.doc.element.office.OdfStyles;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.dom.OdfName;
import org.odftoolkit.odfdom.dom.element.style.OdfStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.OdfStylePropertySet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.w3c.dom.DOMException;

// 2DO: change modifier public to package after refactoring
abstract public class OdfStylableElement extends OdfElement implements OdfStylePropertySet  {

    // 2DO: Overall StyleRefactoring: DOM Layer reaches to upper layer here...
    private OdfStyle mAutomaticStyle;    
    protected OdfStyleFamily mFamily;
    protected OdfName mStyleNameAttrib;
    
    /** Creates a new instance of OdfElementImpl
     * @param ownerDocument
     * @param name
     * @param family
     * @param styleNameAttrib
     * @throws DOMException 
     */
    public OdfStylableElement(OdfFileDom ownerDocument, OdfName name, OdfStyleFamily family, OdfName styleNameAttrib) throws DOMException {
        super(ownerDocument, name.getUri(), name.getQName());
        mFamily = family;
        mStyleNameAttrib = styleNameAttrib;
    }

    /**
     * 
     * @return
     */
    public OdfStyleElement getOrCreateUnqiueAutomaticStyle()
    {
        if( (mAutomaticStyle == null) || ( mAutomaticStyle.getStyleUserCount() > 1 ) )
        {            
            // we need a new automatic style
            OdfAutomaticStyles automatic_styles = getAutomaticStyles();
            if( automatic_styles != null )
            {
                String sParentStyleName;
                if( mAutomaticStyle == null )
                {
                    mAutomaticStyle = automatic_styles.createStyle(getStyleFamily());
                    sParentStyleName = getStyleName();
                }
                else
                {
                    sParentStyleName = mAutomaticStyle.getParentStyleName();
                    mAutomaticStyle.removeStyleUser(this);
                    mAutomaticStyle = automatic_styles.makeStyleUnique(mAutomaticStyle);
                }

                mAutomaticStyle.addStyleUser(this);

                if( (sParentStyleName != null) && (sParentStyleName.length() != 0) )
                    mAutomaticStyle.setParentStyleName(sParentStyleName);

                setStyleName( mAutomaticStyle.getName() );
            }
        }
        return mAutomaticStyle;
    }

    /**
     * 
     * @return the style:automatic-styles element that contains the automatic
     *         style for this element, or null if not available.
     */
    public OdfAutomaticStyles getAutomaticStyles()
    {
        OdfFileDom fileDom = (OdfFileDom)this.ownerDocument;
        if( fileDom != null )
            return fileDom.getAutomaticStyles();
        else
            return null;
    }
    
    /**
     * 
     * @param uri
     * @param name
     * @param value
     */
    @Override
    public void setAttributeNS(String uri, String name, String value)
    {
        super.setAttributeNS(uri, name, value);

        // check if style has changed
        if( mStyleNameAttrib.equals(uri, name) )
        {
            OdfStyle autoStyle = null;
            
            // optimization: check if we already know this automatic style
            if( (mAutomaticStyle != null) && (mAutomaticStyle.getName().equals(value)) )
            {
                // nothing todo
            }
            else
            {
                // register new automatic style
                OdfAutomaticStyles automatic_styles = getAutomaticStyles();
                if( automatic_styles != null ) {
                    autoStyle = automatic_styles.getStyle(value, getStyleFamily());
                }

                if( mAutomaticStyle != null) {
                    mAutomaticStyle.removeStyleUser(this);
                }

                mAutomaticStyle = autoStyle;
                
                if( mAutomaticStyle != null ) {
                    mAutomaticStyle.addStyleUser(this);
                }
            }
        }
    }
    
    /**
     * 
     * @return
     */
    public String getStyleName()
    {
        return getAttributeNS(mStyleNameAttrib.getUri(),
                mStyleNameAttrib.getLocalName());
    }
    
    /**
     * 
     * @param name
     */
    public void setStyleName(String name )
    {
        setAttributeNS(mStyleNameAttrib.getUri(), mStyleNameAttrib.getLocalName(), name);
    }

    /**
     * 
     * @return
     */
    public OdfStyle getAutomaticStyle()
    {
        return mAutomaticStyle;
    }
    
    /**
     * 
     * @return
     */
    public boolean hasAutomaticStyle()
    {
        return mAutomaticStyle != null;
    }
/*    
    public void setLocalStyleProperties(OdfStyle style) {
        mAutomaticStyle = style.getAsLocalStyle();
        setStyleName(style.getName());
    }
*/
    /* Only returns a DocumentStyle if there is no local style */
    public OdfStyle reuseDocumentStyle(String styleName) {
        OdfStyle style = null;
        if (styleName != null) {
            style = mOdfDocument.getDocumentStyles().getStyle(styleName, getStyleFamily() );
            if (style != null) {
                setDocumentStyle(style);
            }
        }
        return style;
    }

    public void setDocumentStyle(OdfStyle style) {
        // when there is a local style, the document style becomes the parent 
        // of the local style
        if (mAutomaticStyle != null) {
            mAutomaticStyle.setParentStyleName(style.getName());
        } else {
            setStyleName(style.getName());
        }
    }

    //    protected static final String LOCAL_STYLE_PREFIX = "#local-style";

/*    
    public OdfStyle newDocumentStyle(String name) {
        OdfStyle newDocStyle = mFamily.newStyle(name, mOdfDocument.getDocumentStyles());
        setDocumentStyle(newDocStyle);
        return newDocStyle;
    }    
*/

    /**
     * 
     * @return
     */
    public OdfStyle getDocumentStyle()
    {
        String styleName;
        if( mAutomaticStyle != null )
            styleName = mAutomaticStyle.getParentStyleName();
        else
            styleName = getStyleName();
        
        return mOdfDocument.getDocumentStyles().getStyle(styleName, getStyleFamily() );
    }
      
    /**
     * 
     * @return
     */
    public boolean hasDocumentStyle()
    {
        return getDocumentStyle() != null;
    }
        
/*
    public OdfStyle getAutomaticStyle() {
        if (mAutomaticStyle == null) {
            mAutomaticStyle = mFamily.newStyle(LOCAL_STYLE_PREFIX, null);
            // if there is already a document style, but no local style
            String styleName = null;
            if ((styleName = getStyleName()) != null) {
                mAutomaticStyle.setParentName(styleName);
            }
        }
        return mAutomaticStyle;
    }
*/

    /**
     * 
     * @return
     */
    public OdfStyleFamily getStyleFamily()
    {
        return mFamily;
    }
/*
    public OdfStyle getMergedStyle() {
        OdfStyle merged = new OdfStyle("#merged-style", getStyleFamily());
        OdfStyle docStyle = getDocumentStyle();
        if (mAutomaticStyle != null) {
            // a document style may be referenced indirectly from the local style...
            if (docStyle == null) {
                docStyle = mOdfDocument.getDocumentStyles().getStyle(mAutomaticStyle.getParentName());
            }
            // copy local style to merged style
            mAutomaticStyle.copyTo(merged, true,false);
        }

        // copy doc style to merged style
        // copyTo only copies properties that are not already set at the
        // target style
        if (docStyle != null) {
            docStyle.copyTo(merged, true,false);
        }

        return merged;
    }
*/
    /**
     * 
     * @param property
     * @return
     */
    public String getProperty( OdfStyleProperty property )
    {
        // first try automatic style
        OdfStyleElement style = mAutomaticStyle;

        if( style == null )
            style = getOfficeStyle();
        
        if( style != null )
            return style.getProperty(property);

        return null;
    }

    /**
     * 
     * @param properties
     * @return
     */
    public Map<OdfStyleProperty, String> getProperties(Set<OdfStyleProperty> properties)
    {
        HashMap< OdfStyleProperty, String > map = new HashMap< OdfStyleProperty, String >();
        for( OdfStyleProperty property : properties )
            map.put( property, getProperty(property) );
        
        return map;
    }

    /**
     * 
     * @return
     */
    public Set<OdfStyleProperty> getStrictProperties()
    {
        return getStyleFamily().getProperties();
    }

    /**
     * 
     * @param property
     * @return
     */
    public boolean hasProperty(OdfStyleProperty property)
    {
        return (mAutomaticStyle != null) && mAutomaticStyle.hasProperty(property);
    }

    /**
     * 
     * @param property
     */
    public void removeProperty(OdfStyleProperty property)
    {
        if( mAutomaticStyle != null )
            mAutomaticStyle.removeProperty(property);
    }

    /**
     * 
     * @param properties
     */
    public void setProperties(Map<OdfStyleProperty, String> properties)
    {
        for( Map.Entry< OdfStyleProperty, String > entry : properties.entrySet() )
            setProperty( entry.getKey(), entry.getValue() );
    }

    /**
     * 
     * @param property
     * @param value
     */
    public void setProperty(OdfStyleProperty property, String value)
    {
        getOrCreateUnqiueAutomaticStyle().setProperty(property, value);
    }

    @Override
    protected void onInsertNode()
    {
        super.onInsertNode();

        String stylename = getStyleName();
        if( stylename.length() != 0 )
        {
            if( mAutomaticStyle != null )
            {
                if( mAutomaticStyle.getName().equals( stylename ) )
                    return;

                mAutomaticStyle.removeStyleUser(this);
                mAutomaticStyle = null;
            }

            OdfAutomaticStyles automatic_styles = getAutomaticStyles();
            if( automatic_styles != null ) {
                mAutomaticStyle = automatic_styles.getStyle(stylename, getStyleFamily());

                if( mAutomaticStyle != null ) {
                    mAutomaticStyle.addStyleUser(this);
                }
            }
        }
    }

    /**
     *
     */
    @Override
    protected void onRemoveNode()
    {
        super.onInsertNode();

        if( this.mAutomaticStyle != null )
        {
            this.mAutomaticStyle.removeStyleUser(this);
            this.mAutomaticStyle = null;
        }
    }

    // todo: rename after get rid of deprecated getDocumentStyle()
    private OdfStyle getOfficeStyle()
    {           
        OdfStyles styles = this.mOdfDocument.getDocumentStyles();
        if( styles != null )
            return styles.getStyle(getStyleName(), getStyleFamily() );
        else
            return null;
    }
}
