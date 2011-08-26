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
package org.odftoolkit.odfdom.doc.element.office;

import java.util.HashMap;
import java.util.Vector;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.draw.OdfFillImage;
import org.odftoolkit.odfdom.doc.element.draw.OdfGradient;
import org.odftoolkit.odfdom.doc.element.draw.OdfHatch;
import org.odftoolkit.odfdom.doc.element.draw.OdfMarker;
import org.odftoolkit.odfdom.doc.element.number.OdfBooleanStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfCurrencyStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfDateStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfNumberStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfPercentageStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfTextStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfTimeStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.text.OdfListStyle;
import org.odftoolkit.odfdom.doc.element.text.OdfOutlineStyle;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.odftoolkit.odfdom.dom.element.office.OdfStylesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.Node;

/**
 *
 */
public class OdfStyles extends OdfStylesElement {

    /**
     *
     */
    private static final long serialVersionUID = 700763983193326060L;

    // styles that are only in OdfStyles
    private HashMap<OdfStyleFamily, OdfDefaultStyle> mDefaultStyles;
    private HashMap<String, OdfMarker> mMarker;
    private HashMap<String, OdfGradient> mGradients;
    private HashMap<String, OdfHatch> mHatches;
    private HashMap<String, OdfFillImage> mFillImages;
    private OdfOutlineStyle mOutlineStyle;
    // styles that are common for OdfStyles and OdfAutomaticStyles
    private OdfStylesBase mStylesBaseImpl;

    public OdfStyles(OdfFileDom _aOwnerDoc) {
        super(_aOwnerDoc);
        mStylesBaseImpl = new OdfStylesBase();
    }

    public OdfStyle createStyle(String name, OdfStyleFamily family) {
        OdfStyle newStyle = ((OdfFileDom) this.ownerDocument).createOdfElement(OdfStyle.class);
        newStyle.setName(name);
        newStyle.setFamily(family);
        this.appendChild(newStyle);
        return newStyle;
    }

    public OdfDefaultStyle getOrCreateDefaultStyle(OdfStyleFamily family) {
        OdfDefaultStyle style = getDefaultStyle(family);
        if (style == null) {
            style = ((OdfFileDom) this.ownerDocument).createOdfElement(OdfDefaultStyle.class);
            style.setFamily(family);
            this.appendChild(style);
        }
        return style;
    }

    public OdfListStyle createListStyle(String name) {
        OdfListStyle newStyle = ((OdfFileDom) this.ownerDocument).createOdfElement(OdfListStyle.class);
        newStyle.setName(name);
        this.appendChild(newStyle);
        return newStyle;
    }

    public OdfOutlineStyle getOrCreateOutlineStyle() {
        if (mOutlineStyle == null) {
            this.appendChild(((OdfFileDom) this.ownerDocument).createOdfElement(OdfOutlineStyle.class));
        }

        return mOutlineStyle;
    }

    /** Returns the <code>OdfOutlineStyle</code> element.
     *
     * @return a pointer to the outline stye or null if there is no such element
     */
    public OdfOutlineStyle getOutlineStyle() {
        return mOutlineStyle;
    }

    /** Returns the <code>OdfDefaultStyle</code>  element.
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

    /** Returns an iterator for all <code>OdfDefaultStyle</code> elements.
     *
     * @return iterator for all <code>OdfDefaultStyle</code> elements
     */
    public Iterable<OdfDefaultStyle> getDefaultStyles() {
        if (mDefaultStyles != null) {
            return mDefaultStyles.values();
        } else {
            return new Vector<OdfDefaultStyle>();
        }
    }

    /** Returns the <code>OdfMarker</code> element with the given name.
     *
     * @param name is the name of the marker
     * @return the marker or null if there is no such marker
     */
    public OdfMarker getMarker(String name) {
        if (mMarker != null) {
            return mMarker.get(name);
        } else {
            return null;
        }
    }

    /** Returns an iterator for all <code>OdfMarker</code> elements.
     *
     * @return an iterator for all <code>OdfMarker</code> elements
     */
    public Iterable<OdfMarker> getMarker() {
        if (mMarker != null) {
            return mMarker.values();
        } else {
            return new Vector<OdfMarker>();
        }
    }

    /** Returns the <code>OdfGradient</code> element with the given name.
     *
     * @param name is the name of the gradient
     * @return the gradient or null if there is no such gradient
     */
    public OdfGradient getGradient(String name) {
        if (mGradients != null) {
            return mGradients.get(name);
        } else {
            return null;
        }
    }

    /** Returns an iterator for all <code>OdfGradient</code> elements.
     *
     * @return an iterator for all <code>OdfGradient</code> elements
     */
    public Iterable<OdfGradient> getGradients() {
        if (mGradients != null) {
            return mGradients.values();
        } else {
            return new Vector<OdfGradient>();
        }
    }

    /** Returns the <code>OdfHatch</code> element with the given name.
     *
     * @param name is the name of the hatch
     * @return the hatch or null if there is no such hatch
     */
    public OdfHatch getHatch(String name) {
        if (mHatches != null) {
            return mHatches.get(name);
        } else {
            return null;
        }
    }

    /** Returns an iterator for all <code>OdfHatch</code> elements.
     *
     * @return an iterator for all <code>OdfHatch</code> elements
     */
    public Iterable<OdfHatch> getHatches() {
        if (mHatches != null) {
            return mHatches.values();
        } else {
            return new Vector<OdfHatch>();
        }
    }

    /** Returns the <code>OdfFillImage</code> element with the given name.
     *
     * @param name is the name of the fill image
     * @return the fill image or null if there is no such fill image
     */
    public OdfFillImage getFillImage(String name) {
        if (mFillImages != null) {
            return mFillImages.get(name);
        } else {
            return null;
        }
    }

    /** Returns an iterator for all <code>OdfFillImage</code> elements.
     *
     * @return an iterator for all <code>OdfFillImage</code> elements
     */
    public Iterable<OdfFillImage> getFillImages() {
        if (mFillImages != null) {
            return mFillImages.values();
        } else {
            return new Vector<OdfFillImage>();
        }
    }

    /** Returns the <code>OdfStyle</code> element with the given name and family.
     *
     * @param name is the name of the style
     * @param familyType is the family of the style
     * @return the style or null if there is no such style
     */
    public OdfStyle getStyle(String name, OdfStyleFamily familyType) {
        return mStylesBaseImpl.getStyle(name, familyType);
    }

    /** Returns an iterator for all <code>OdfStyle</code> elements for the given family.
     *
     * @param familyType
     * @return an iterator for all <code>OdfStyle</code> elements for the given family
     */
    public Iterable<OdfStyle> getStylesForFamily(OdfStyleFamily familyType) {
        return mStylesBaseImpl.getStylesForFamily(familyType);
    }

    /** Returns the <code>OdfListStyle</code> element with the given name.
     *
     * @param name is the name of the list style
     * @return the list style or null if there is no such list style
     */
    public OdfListStyle getListStyle(String name) {
        return mStylesBaseImpl.getListStyle(name);
    }

    /** Returns an iterator for all <code>OdfListStyle</code> elements.
     *
     * @return an iterator for all <code>OdfListStyle</code> elements
     */
    public Iterable<OdfListStyle> getListStyles() {
        return mStylesBaseImpl.getListStyles();
    }

    /** Returns the <code>OdfNumberStyle</code> element with the given name.
     *
     * @param name is the name of the number style
     * @return the number style or null if there is no such number style
     */
    public OdfNumberStyle getNumberStyle(String name) {
        return mStylesBaseImpl.getNumberStyle(name);
    }

    /** Returns an iterator for all <code>OdfNumberStyle</code> elements.
     *
     * @return an iterator for all <code>OdfNumberStyle</code> elements
     */
    public Iterable<OdfNumberStyle> getNumberStyles() {
        return mStylesBaseImpl.getNumberStyles();
    }

    /** Returns the <code>OdfDateStyle</code> element with the given name.
     *
     * @param name is the name of the date style
     * @return the date style or null if there is no such date style
     */
    public OdfDateStyle getDateStyle(String name) {
        return mStylesBaseImpl.getDateStyle(name);
    }

    /** Returns an iterator for all <code>OdfDateStyle</code> elements.
     *
     * @return an iterator for all <code>OdfDateStyle</code> elements
     */
    public Iterable<OdfDateStyle> getDateStyles() {
        return mStylesBaseImpl.getDateStyles();
    }

    /** Returns the <code>OdfPercentageStyle</code> element with the given name.
     *
     * @param name is the name of the percentage style
     * @return the percentage style null if there is no such percentage style
     */
    public OdfPercentageStyle getPercentageStyle(String name) {
        return mStylesBaseImpl.getPercentageStyle(name);
    }

    /** Returns an iterator for all <code>OdfPercentageStyle</code> elements.
     *
     * @return an iterator for all <code>OdfPercentageStyle</code> elements
     */
    public Iterable<OdfPercentageStyle> getPercentageStyles() {
        return mStylesBaseImpl.getPercentageStyles();
    }

    /** Returns the <code>OdfCurrencyStyle</code> element with the given name.
     *
     * @param name is the name of the currency style
     * @return the currency style null if there is no such currency style
     */
    public OdfCurrencyStyle getCurrencyStyle(String name) {
        return mStylesBaseImpl.getCurrencyStyle(name);
    }

    /** Returns an iterator for all <code>OdfCurrencyStyle</code> elements.
     *
     * @return an iterator for all <code>OdfCurrencyStyle</code> elements
     */
    public Iterable<OdfCurrencyStyle> getCurrencyStyles() {
        return mStylesBaseImpl.getCurrencyStyles();
    }

    /** Returns the <code>OdfTimeStyle</code> element with the given name.
     *
     * @param name is the name of the time style
     * @return the time style null if there is no such time style
     */
    public OdfTimeStyle getTimeStyle(String name) {
        return mStylesBaseImpl.getTimeStyle(name);
    }

    /** Returns an iterator for all <code>OdfTimeStyle</code> elements.
     *
     * @return an iterator for all <code>OdfTimeStyle</code> elements
     */
    public Iterable<OdfTimeStyle> getTimeStyles() {
        return mStylesBaseImpl.getTimeStyles();
    }

    /** Returns the <code>OdfBooleanStyle</code> element with the given name.
     *
     * @param name is the name of the boolean style
     * @return the boolean style null if there is no such boolean style
     */
    public OdfBooleanStyle getBooleanStyle(String name) {
        return mStylesBaseImpl.getBooleanStyle(name);
    }

    /** Returns an iterator for all <code>OdfBooleanStyle</code> elements.
     *
     * @return an iterator for all <code>OdfBooleanStyle</code> elements
     */
    public Iterable<OdfBooleanStyle> getBooleanStyles() {
        return mStylesBaseImpl.getBooleanStyles();
    }

    /** Returns the <code>OdfTextStyle</code> element with the given name.
     *
     * @param name is the name of the text style
     * @return the text style null if there is no such text style
     */
    public OdfTextStyle getTextStyle(String name) {
        return mStylesBaseImpl.getTextStyle(name);
    }

    /** Returns an iterator for all <code>OdfTextStyle</code> elements.
     *
     * @return an iterator for all <code>OdfTextStyle</code> elements
     */
    public Iterable<OdfTextStyle> getTextStyles() {
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
        } else if (node instanceof OdfMarker) {
            OdfMarker marker = (OdfMarker) node;
            if (mMarker == null) {
                mMarker = new HashMap<String, OdfMarker>();
            }

            mMarker.put(marker.getName(), marker);
        } else if (node instanceof OdfGradient) {
            OdfGradient gradient = (OdfGradient) node;
            if (mGradients == null) {
                mGradients = new HashMap<String, OdfGradient>();
            }

            mGradients.put(gradient.getName(), gradient);
        } else if (node instanceof OdfHatch) {
            OdfHatch hatch = (OdfHatch) node;
            if (mHatches == null) {
                mHatches = new HashMap<String, OdfHatch>();
            }

            mHatches.put(hatch.getName(), hatch);
        } else if (node instanceof OdfFillImage) {
            OdfFillImage fillImage = (OdfFillImage) node;

            if (mFillImages == null) {
                mFillImages = new HashMap<String, OdfFillImage>();
            }

            mFillImages.put(fillImage.getName(), fillImage);
        } else if (node instanceof OdfOutlineStyle) {
            mOutlineStyle = (OdfOutlineStyle) node;
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
        } else if (node instanceof OdfMarker) {
            if (mMarker != null) {
                OdfMarker marker = (OdfMarker) node;
                mMarker.remove(marker.getName());
            }
        } else if (node instanceof OdfGradient) {
            if (mGradients != null) {
                OdfGradient gradient = (OdfGradient) node;
                mGradients.remove(gradient.getName());
            }
        } else if (node instanceof OdfHatch) {
            if (mHatches != null) {
                OdfHatch hatch = (OdfHatch) node;
                mHatches.remove(hatch.getName());
            }
        } else if (node instanceof OdfFillImage) {
            if (mFillImages != null) {
                OdfFillImage fillImage = (OdfFillImage) node;
                mFillImages.remove(fillImage.getName());
            }
        } else if (node instanceof OdfOutlineStyle) {
            if (mOutlineStyle == (OdfOutlineStyle) node) {
                mOutlineStyle = null;
            }
        } else {
            mStylesBaseImpl.onOdfNodeRemoved(node);
        }
    }
}
