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

import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.number.OdfBooleanStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfCurrencyStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfDateStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfNumberStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfPercentageStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfTextStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfTimeStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfPageLayout;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.text.OdfListStyle;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.odftoolkit.odfdom.dom.element.office.OdfAutomaticStylesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;

import org.w3c.dom.Node;

/**
 *
 */
public class OdfAutomaticStyles extends OdfAutomaticStylesElement {

    /**
     *
     */
    private static final long serialVersionUID = -2925910664631016175L;

    // styles that are only in OdfAutomaticStyles
    private HashMap<String, OdfPageLayout> mPageLayouts;
    // styles that are common for OdfStyles and OdfAutomaticStyles
    private OdfStylesBase mStylesBaseImpl;

    public OdfAutomaticStyles(OdfFileDom _aOwnerDoc) {
        super(_aOwnerDoc);
        mStylesBaseImpl = new OdfStylesBase();
    }

    public OdfStyle createStyle(OdfStyleFamily styleFamily) {
        OdfFileDom dom = (OdfFileDom) this.ownerDocument;
        OdfStyle newStyle = dom.createOdfElement(OdfStyle.class);
        newStyle.setFamily(styleFamily);

        newStyle.setName(createUniqueStyleName(styleFamily));

        this.appendChild(newStyle);

        return newStyle;
    }

    public OdfListStyle createListStyle() {
        OdfFileDom dom = (OdfFileDom) this.ownerDocument;
        OdfListStyle newStyle = dom.createOdfElement(OdfListStyle.class);

        newStyle.setName(createUniqueStyleName(OdfStyleFamily.List));

        this.appendChild(newStyle);

        return newStyle;
    }

    /** Returns the <code>OdfPageLayout</code> element with the given name.
     *
     * @param name is the name of the page layout
     * @return the page layout or null if there is no such page layout
     */
    public OdfPageLayout getPageLayout(String name) {
        if (mPageLayouts != null) {
            return mPageLayouts.get(name);
        } else {
            return null;
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
        if (node instanceof OdfPageLayout) {
            OdfPageLayout pageLayout = (OdfPageLayout) node;
            if (mPageLayouts == null) {
                mPageLayouts = new HashMap<String, OdfPageLayout>();
            }

            mPageLayouts.put(pageLayout.getName(), pageLayout);
        } else {
            mStylesBaseImpl.onOdfNodeInserted(node, refNode);
        }
    }

    @Override
    protected void onOdfNodeRemoved(OdfElement node) {
        if (node instanceof OdfPageLayout) {
            if (mPageLayouts != null) {
                OdfPageLayout pageLayout = (OdfPageLayout) node;
                mPageLayouts.remove(pageLayout.getName());
            }
        } else {
            mStylesBaseImpl.onOdfNodeRemoved(node);
        }
    }

    /** this methods removes all automatic styles that are currently not used by any styleable element.
     *  Todo: In the future this could also merge automatic styles with identical content.
     */
    public void optimize() {
        OdfStyle style = OdfElement.findFirstChildNode(OdfStyle.class, this);
        while (style != null) {
            OdfStyle nextStyle = OdfElement.findNextChildNode(OdfStyle.class, style);
            if (style.getStyleUserCount() < 1) {
                this.removeChild(style);
            }

            style = nextStyle;
        }
    }

    public OdfStyle makeStyleUnique(OdfStyle referenceStyle) {
        OdfStyle newStyle = null;

        if (referenceStyle.getOwnerDocument() != this.getOwnerDocument()) {
            // import style from a different dom
            newStyle = (OdfStyle) this.getOwnerDocument().importNode(referenceStyle, true);
        } else {
            // just clone
            newStyle = (OdfStyle) referenceStyle.cloneNode(true);
        }

        newStyle.setName(createUniqueStyleName(newStyle.getFamily()));
        appendChild(newStyle);

        return newStyle;
    }

    private String createUniqueStyleName(OdfStyleFamily styleFamily) {
        String unique_name;

        if (styleFamily.equals(OdfStyleFamily.List)) {
            do {
                unique_name = String.format("l%06x", (int) (Math.random() * 0xffffff));
            } while (getListStyle(unique_name) != null);
        } else {
            do {
                unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
            } while (getStyle(unique_name, styleFamily) != null);
        }
        return unique_name;
    }
}
