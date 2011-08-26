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
package org.odftoolkit.odfdom.incubator.doc.office;

import java.util.ArrayList;
import java.util.HashMap;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawFillImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGradientElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawHatchElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMarkerElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeStylesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberCurrencyStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextOutlineStyle;
import org.w3c.dom.Node;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 */
public class OdfOfficeStyles extends OfficeStylesElement {


    private static final long serialVersionUID = 700763983193326060L;

    // styles that are only in OdfOfficeStyles
    private HashMap<OdfStyleFamily, OdfDefaultStyle> mDefaultStyles;
    private HashMap<String, DrawMarkerElement> mMarker;
    private HashMap<String, DrawGradientElement> mGradients;
    private HashMap<String, DrawHatchElement> mHatches;
    private HashMap<String, DrawFillImageElement> mFillImages;
    private OdfTextOutlineStyle mOutlineStyle;
    // styles that are common for OdfOfficeStyles and OdfOfficeAutomaticStyles
    private OdfStylesBase mStylesBaseImpl;

    public OdfOfficeStyles(OdfFileDom ownerDoc) {
        super(ownerDoc);
        mStylesBaseImpl = new OdfStylesBase();
    }
    
    /**
     * Create an ODF style with style name and family
     * 
     * @param name  The style name
     * @param family The style family
     * @return  The <code>OdfStyle</code> element
     */
    public OdfStyle newStyle(String name, OdfStyleFamily family) {
        OdfStyle newStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(OdfStyle.class);
        newStyle.setStyleNameAttribute(name);
        newStyle.setStyleFamilyAttribute(family.getName());
        this.appendChild(newStyle);
        return newStyle;
    }

    /**
     * Retrieve or create ODF default style
     * 
     * @param family The style family
     * @return The code>OdfDefaultStyle</code> element
     */
    public OdfDefaultStyle getOrCreateDefaultStyle(OdfStyleFamily family) {
        OdfDefaultStyle style = getDefaultStyle(family);
        if (style == null) {
            style = ((OdfFileDom) this.ownerDocument).newOdfElement(OdfDefaultStyle.class);
            style.setStyleFamilyAttribute(family.getName());
            this.appendChild(style);
        }
        return style;
    }
    
    /**
     * Create ODF TextListStyle 
     * 
     * @param name  The style name
     * @return The code>OdfTextListStyle</code> element
     */
    public OdfTextListStyle newListStyle(String name) {
        OdfTextListStyle newStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(OdfTextListStyle.class);
        newStyle.setStyleNameAttribute(name);
        this.appendChild(newStyle);
        return newStyle;
    }

    /**
     * Retrieve or create ODF OutlineStyle
     * 
     * @return The code>OdfTextOutlineStyle</code> element
     */
    public OdfTextOutlineStyle getOrCreateOutlineStyle() {
        if (mOutlineStyle == null) {
            this.appendChild(((OdfFileDom) this.ownerDocument).newOdfElement(OdfTextOutlineStyle.class));
        }

        return mOutlineStyle;
    }

    /** 
     * Returns the <code>OdfTextOutlineStyle</code> element.
     *
     * @return a pointer to the outline stye or null if there is no such element
     */
    public OdfTextOutlineStyle getOutlineStyle() {
        return mOutlineStyle;
    }

    /** 
     * Returns the <code>OdfStyleDefaultStyle</code>  element.
     *
     * @param familyType is the family for the default style
     * @return the default style with the given family or null if there is no such default style
     */
    public OdfDefaultStyle getDefaultStyle(OdfStyleFamily familyType) {
        if (mDefaultStyles != null) {
            return mDefaultStyles.get(familyType);
        } else {
            return null;
        }
    }

    /** 
     * Returns an iterator for all <code>OdfStyleDefaultStyle</code> elements.
     *
     * @return iterator for all <code>OdfStyleDefaultStyle</code> elements
     */
    public Iterable<OdfDefaultStyle> getDefaultStyles() {
        if (mDefaultStyles != null) {
            return mDefaultStyles.values();
        } else {
            return new ArrayList<OdfDefaultStyle>();
        }
    }

    /** 
     * Returns the <code>DrawMarkerElement</code> element with the given name.
     *
     * @param name is the name of the marker
     * @return the marker or null if there is no such marker
     */
    public DrawMarkerElement getMarker(String name) {
        if (mMarker != null) {
            return mMarker.get(name);
        } else {
            return null;
        }
    }

    /** 
     * Returns an iterator for all <code>DrawMarkerElement</code> elements.
     *
     * @return an iterator for all <code>DrawMarkerElement</code> elements
     */
    public Iterable<DrawMarkerElement> getMarker() {
        if (mMarker != null) {
            return mMarker.values();
        } else {
            return new ArrayList<DrawMarkerElement>();
        }
    }

    /** 
     * Returns the <code>DrawGradientElement</code> element with the given name.
     *
     * @param name is the name of the gradient
     * @return the gradient or null if there is no such gradient
     */
    public DrawGradientElement getGradient(String name) {
        if (mGradients != null) {
            return mGradients.get(name);
        } else {
            return null;
        }
    }

    /** 
     * Returns an iterator for all <code>DrawGradientElement</code> elements.
     *
     * @return an iterator for all <code>DrawGradientElement</code> elements
     */
    public Iterable<DrawGradientElement> getGradients() {
        if (mGradients != null) {
            return mGradients.values();
        } else {
            return new ArrayList<DrawGradientElement>();
        }
    }

    /** 
     * Returns the <code>DrawHatchElement</code> element with the given name.
     *
     * @param name is the name of the hatch
     * @return the hatch or null if there is no such hatch
     */
    public DrawHatchElement getHatch(String name) {
        if (mHatches != null) {
            return mHatches.get(name);
        } else {
            return null;
        }
    }

    /** 
     * Returns an iterator for all <code>DrawHatchElement</code> elements.
     *
     * @return an iterator for all <code>DrawHatchElement</code> elements
     */
    public Iterable<DrawHatchElement> getHatches() {
        if (mHatches != null) {
            return mHatches.values();
        } else {
            return new ArrayList<DrawHatchElement>();
        }
    }

    /** 
     * Returns the <code>DrawFillImageElement</code> element with the given name.
     *
     * @param name is the name of the fill image
     * @return the fill image or null if there is no such fill image
     */
    public DrawFillImageElement getFillImage(String name) {
        if (mFillImages != null) {
            return mFillImages.get(name);
        } else {
            return null;
        }
    }

    /** 
     * Returns an iterator for all <code>DrawFillImageElement</code> elements.
     *
     * @return an iterator for all <code>DrawFillImageElement</code> elements
     */
    public Iterable<DrawFillImageElement> getFillImages() {
        if (mFillImages != null) {
            return mFillImages.values();
        } else {
            return new ArrayList<DrawFillImageElement>();
        }
    }

    /** 
     * Returns the <code>OdfStyle</code> element with the given name and family.
     *
     * @param name is the name of the style
     * @param familyType is the family of the style
     * @return the style or null if there is no such style
     */
    public OdfStyle getStyle(String name, OdfStyleFamily familyType) {
        return mStylesBaseImpl.getStyle(name, familyType);
    }

    /** 
     * Returns an iterator for all <code>OdfStyle</code> elements for the given family.
     *
     * @param familyType
     * @return an iterator for all <code>OdfStyle</code> elements for the given family
     */
    public Iterable<OdfStyle> getStylesForFamily(OdfStyleFamily familyType) {
        return mStylesBaseImpl.getStylesForFamily(familyType);
    }

    /** 
     * Returns the <code>OdfTextListStyle</code> element with the given name.
     *
     * @param name is the name of the list style
     * @return the list style or null if there is no such list style
     */
    public OdfTextListStyle getListStyle(String name) {
        return mStylesBaseImpl.getListStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfTextListStyle</code> elements.
     *
     * @return an iterator for all <code>OdfTextListStyle</code> elements
     */
    public Iterable<OdfTextListStyle> getListStyles() {
        return mStylesBaseImpl.getListStyles();
    }

    /** 
     * Returns the <code>OdfNumberNumberStyle</code> element with the given name.
     *
     * @param name is the name of the number style
     * @return the number style or null if there is no such number style
     */
    public OdfNumberStyle getNumberStyle(String name) {
        return mStylesBaseImpl.getNumberStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfNumberNumberStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberNumberStyle</code> elements
     */
    public Iterable<OdfNumberStyle> getNumberStyles() {
        return mStylesBaseImpl.getNumberStyles();
    }

    /** 
     * Returns the <code>OdfNumberDateStyle</code> element with the given name.
     *
     * @param name is the name of the date style
     * @return the date style or null if there is no such date style
     */
    public OdfNumberDateStyle getDateStyle(String name) {
        return mStylesBaseImpl.getDateStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfNumberDateStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberDateStyle</code> elements
     */
    public Iterable<OdfNumberDateStyle> getDateStyles() {
        return mStylesBaseImpl.getDateStyles();
    }

    /** 
     * Returns the <code>OdfNumberPercentageStyle</code> element with the given name.
     *
     * @param name is the name of the percentage style
     * @return the percentage style null if there is no such percentage style
     */
    public OdfNumberPercentageStyle getPercentageStyle(String name) {
        return mStylesBaseImpl.getPercentageStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfNumberPercentageStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberPercentageStyle</code> elements
     */
    public Iterable<OdfNumberPercentageStyle> getPercentageStyles() {
        return mStylesBaseImpl.getPercentageStyles();
    }

    /** 
     * Returns the <code>OdfNumberCurrencyStyle</code> element with the given name.
     *
     * @param name is the name of the currency style
     * @return the currency style null if there is no such currency style
     */
    public OdfNumberCurrencyStyle getCurrencyStyle(String name) {
        return mStylesBaseImpl.getCurrencyStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfNumberCurrencyStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberCurrencyStyle</code> elements
     */
    public Iterable<OdfNumberCurrencyStyle> getCurrencyStyles() {
        return mStylesBaseImpl.getCurrencyStyles();
    }

    /** 
     * Returns the <code>OdfNumberTimeStyle</code> element with the given name.
     *
     * @param name is the name of the time style
     * @return the time style null if there is no such time style
     */
    public OdfNumberTimeStyle getTimeStyle(String name) {
        return mStylesBaseImpl.getTimeStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfNumberTimeStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberTimeStyle</code> elements
     */
    public Iterable<OdfNumberTimeStyle> getTimeStyles() {
        return mStylesBaseImpl.getTimeStyles();
    }

    /** 
     * Returns the <code>NumberBooleanStyleElement</code> element with the given name.
     *
     * @param name is the name of the boolean style
     * @return the boolean style null if there is no such boolean style
     */
    public NumberBooleanStyleElement getBooleanStyle(String name) {
        return mStylesBaseImpl.getBooleanStyle(name);
    }

    /** 
     * Returns an iterator for all <code>NumberBooleanStyleElement</code> elements.
     *
     * @return an iterator for all <code>NumberBooleanStyleElement</code> elements
     */
    public Iterable<NumberBooleanStyleElement> getBooleanStyles() {
        return mStylesBaseImpl.getBooleanStyles();
    }

    /** 
     * Returns the <code>OdfNumberTextStyle</code> element with the given name.
     *
     * @param name is the name of the text style
     * @return the text style null if there is no such text style
     */
    public NumberTextStyleElement getTextStyle(String name) {
        return mStylesBaseImpl.getTextStyle(name);
    }

    /** 
     * Returns an iterator for all <code>OdfNumberTextStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberTextStyle</code> elements
     */
    public Iterable<NumberTextStyleElement> getTextStyles() {
        return mStylesBaseImpl.getTextStyles();
    }

    @Override
    protected void onOdfNodeInserted(OdfElement node, Node refNode) {
        if (node instanceof OdfDefaultStyle) {
            OdfDefaultStyle defaultStyle = (OdfDefaultStyle) node;
            if (mDefaultStyles == null) {
                mDefaultStyles = new HashMap<OdfStyleFamily, OdfDefaultStyle>();
            }

            mDefaultStyles.put(defaultStyle.getFamily(), defaultStyle);
        } else if (node instanceof DrawMarkerElement) {
            DrawMarkerElement marker = (DrawMarkerElement) node;
            if (mMarker == null) {
                mMarker = new HashMap<String, DrawMarkerElement>();
            }

            mMarker.put(marker.getDrawNameAttribute(), marker);
        } else if (node instanceof DrawGradientElement) {
            DrawGradientElement gradient = (DrawGradientElement) node;
            if (mGradients == null) {
                mGradients = new HashMap<String, DrawGradientElement>();
            }

            mGradients.put(gradient.getDrawNameAttribute(), gradient);
        } else if (node instanceof DrawHatchElement) {
            DrawHatchElement hatch = (DrawHatchElement) node;
            if (mHatches == null) {
                mHatches = new HashMap<String, DrawHatchElement>();
            }

            mHatches.put(hatch.getDrawNameAttribute(), hatch);
        } else if (node instanceof DrawFillImageElement) {
            DrawFillImageElement fillImage = (DrawFillImageElement) node;

            if (mFillImages == null) {
                mFillImages = new HashMap<String, DrawFillImageElement>();
            }

            mFillImages.put(fillImage.getDrawNameAttribute(), fillImage);
        } else if (node instanceof OdfTextOutlineStyle) {
            mOutlineStyle = (OdfTextOutlineStyle) node;
        } else {
            mStylesBaseImpl.onOdfNodeInserted(node, refNode);
        }
    }

    @Override
    protected void onOdfNodeRemoved(OdfElement node) {
        if (node instanceof OdfDefaultStyle) {
            if (mDefaultStyles != null) {
                OdfDefaultStyle defaultStyle = (OdfDefaultStyle) node;
                mDefaultStyles.remove(defaultStyle.getFamily());
            }
        } else if (node instanceof DrawMarkerElement) {
            if (mMarker != null) {
                DrawMarkerElement marker = (DrawMarkerElement) node;
                mMarker.remove(marker.getDrawNameAttribute());
            }
        } else if (node instanceof DrawGradientElement) {
            if (mGradients != null) {
                DrawGradientElement gradient = (DrawGradientElement) node;
                mGradients.remove(gradient.getDrawNameAttribute());
            }
        } else if (node instanceof DrawHatchElement) {
            if (mHatches != null) {
                DrawHatchElement hatch = (DrawHatchElement) node;
                mHatches.remove(hatch.getDrawNameAttribute());
            }
        } else if (node instanceof DrawFillImageElement) {
            if (mFillImages != null) {
                DrawFillImageElement fillImage = (DrawFillImageElement) node;
                mFillImages.remove(fillImage.getDrawNameAttribute());
            }
        } else if (node instanceof OdfTextOutlineStyle) {
            if (mOutlineStyle == (OdfTextOutlineStyle) node) {
                mOutlineStyle = null;
            }
        } else {
            mStylesBaseImpl.onOdfNodeRemoved(node);
        }
    }
}
