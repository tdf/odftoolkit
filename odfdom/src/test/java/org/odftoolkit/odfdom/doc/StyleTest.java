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
package org.odftoolkit.odfdom.doc;

import org.odftoolkit.odfdom.OdfFileDom;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.doc.style.OdfStyleBackgroundImage;
import org.odftoolkit.odfdom.doc.style.OdfStyleChartProperties;
import org.odftoolkit.odfdom.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyleParagraphProperties;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyleTabStop;
import org.odftoolkit.odfdom.doc.style.OdfStyleTabStops;
import org.odftoolkit.odfdom.doc.style.OdfStyleTextProperties;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

public class StyleTest {

    public StyleTest() {
    }

    @Test
    public void testStyleOrdering() {
        try {
            OdfDocument doc = OdfTextDocument.newTextDocument();
            OdfFileDom dom = doc.getContentDom();

            // 1. different # of attributes
            OdfStyle style1 = new OdfStyle(dom);
            style1.setStyleFamilyAttribute(OdfStyleFamily.Paragraph.toString());

            OdfStyle style2 = new OdfStyle(dom);
            style2.setStyleFamilyAttribute(OdfStyleFamily.Paragraph.toString());
            style2.setStyleNextStyleNameAttribute("nextStyle");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 2. same # of attributes, different attributes
            style1.setStyleDisplayNameAttribute("displayName");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 3. same # of attributes, same attributes, different values
            style1.setStyleNextStyleNameAttribute("nextStyle");
            style2.setStyleNextStyleNameAttribute("xnextStyle");
            style2.setStyleDisplayNameAttribute("displayName");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 4. same # of attributes, same attributes, same values, different
            // number of children
            style2.setStyleNextStyleNameAttribute("nextStyle");
            style2.setProperty(OdfStyleTextProperties.FontSize, "17pt");
            style2.setProperty(OdfStyleParagraphProperties.TextAlign, "left");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 5. same # of attributes, same attributes, same values, same number
            // of children, different number of properties
            style1.setProperty(OdfStyleChartProperties.DataLabelNumber, "value");
            style1.setProperty(OdfStyleTextProperties.FontSize, "17pt");
            style1.setProperty(OdfStyleParagraphProperties.TextAlign, "left");
            style2.setProperty(OdfStyleParagraphProperties.KeepTogether, "auto");
            style2.setProperty(OdfStyleChartProperties.DataLabelNumber, "value");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 6. same # of attributes, same attributes, same values, same number
            // of children, same number of properties, different properties:
            style1.setProperty(OdfStyleParagraphProperties.AutoTextIndent, "true");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 7. same # of attributes, same attributes, same values, same number
            // of children, same number of properties, same properties, different
            // values:
            style2.setProperty(OdfStyleParagraphProperties.AutoTextIndent, "true");
            style1.setProperty(OdfStyleParagraphProperties.KeepTogether, "always");
            Assert.assertTrue(style2.compareTo(style1) > 0);

            // 8. same # of attributes, same attributes, same values, same number
            // of children, same number of properties, same properties, same
            // values:
            style1.setProperty(OdfStyleParagraphProperties.KeepTogether, "auto");
            Assert.assertTrue(style2.compareTo(style1) == 0);

            // 9. tab stops vs background image
            OdfStyleTabStops tabStops = (OdfStyleTabStops) OdfElementFactory.newOdfElement(dom, OdfStyleTabStops.ELEMENT_NAME);
            OdfStyleTabStop tabStop1 = (OdfStyleTabStop) OdfElementFactory.newOdfElement(dom, OdfStyleTabStop.ELEMENT_NAME);
            OdfStyleTabStop tabStop2 = (OdfStyleTabStop) OdfElementFactory.newOdfElement(dom, OdfStyleTabStop.ELEMENT_NAME);
            OdfStylePropertiesBase propElement = style1.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            propElement.appendChild(tabStops);
            tabStops.appendChild(tabStop1);
            tabStops.appendChild(tabStop2);
            OdfStyleBackgroundImage img = (OdfStyleBackgroundImage) OdfElementFactory.newOdfElement(dom, OdfStyleBackgroundImage.ELEMENT_NAME);
            propElement = style2.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            propElement.appendChild(img);
            Assert.assertTrue(style2.compareTo(style1) < 0);

            // 10. Same tab stops and background image, different position:
            // These two styles are considered distinct!
            tabStops = (OdfStyleTabStops) OdfElementFactory.newOdfElement(dom, OdfStyleTabStops.ELEMENT_NAME);
            tabStop1 = (OdfStyleTabStop) OdfElementFactory.newOdfElement(dom, OdfStyleTabStop.ELEMENT_NAME);
            tabStop2 = (OdfStyleTabStop) OdfElementFactory.newOdfElement(dom, OdfStyleTabStop.ELEMENT_NAME);
            propElement = style2.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            propElement.appendChild(tabStops);
            tabStops.appendChild(tabStop1);
            tabStops.appendChild(tabStop2);
            img = (OdfStyleBackgroundImage) OdfElementFactory.newOdfElement(dom, OdfStyleBackgroundImage.ELEMENT_NAME);
            propElement = style1.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            propElement.appendChild(img);
            Assert.assertTrue(style2.compareTo(style1) < 0);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }

    @Test
    public void testAutomaticStylesOptimize() {
        try {
            OdfDocument doc = OdfTextDocument.newTextDocument();
            OdfFileDom dom = doc.getContentDom();

            OdfTextParagraph para1 = (OdfTextParagraph) OdfElementFactory.newOdfElement(dom, OdfTextParagraph.ELEMENT_NAME);
            para1.setProperty(OdfStyleTextProperties.FontSize, "17pt");
            para1.setProperty(OdfStyleParagraphProperties.TextAlign, "left");
            para1.setProperty(OdfStyleChartProperties.DataLabelNumber, "value");

            OdfTextParagraph para2 = (OdfTextParagraph) OdfElementFactory.newOdfElement(dom, OdfTextParagraph.ELEMENT_NAME);
            para2.setProperty(OdfStyleChartProperties.DataLabelNumber, "value");
            para2.setProperty(OdfStyleParagraphProperties.TextAlign, "left");
            para2.setProperty(OdfStyleTextProperties.FontSize, "17pt");

            StyleStyleElement style1 = para1.getAutomaticStyle();
            StyleStyleElement style2 = para2.getAutomaticStyle();

            String styleName1 = para1.getStyleName();
            String styleName2 = para2.getStyleName();

            Assert.assertFalse(styleName1.equals(styleName2));
            Assert.assertTrue(style1.getStyleUserCount() == 1);
            Assert.assertTrue(style2.getStyleUserCount() == 1);

            OdfOfficeAutomaticStyles autoStyles = para1.getAutomaticStyles();

            Iterator<OdfStyle> iter = autoStyles.getStylesForFamily(OdfStyleFamily.Paragraph).iterator();
            int count = 0;
            while (iter.hasNext()) {
                iter.next();
                ++count;
            }

            Assert.assertTrue(count == 2);

            // optimize should automatically remove one style:
            autoStyles.optimize();

            iter = autoStyles.getStylesForFamily(OdfStyleFamily.Paragraph).iterator();
            count = 0;
            while (iter.hasNext()) {
                iter.next();
                ++count;
            }

            Assert.assertTrue(count == 1);

            style1 = para1.getAutomaticStyle();
            style2 = para2.getAutomaticStyle();

            Assert.assertTrue(style1 == style2);
            Assert.assertTrue(style1.getStyleUserCount() == 2);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }

    @Test
    public void testPropertyInheritance() {
        try {
            OdfDocument doc = OdfTextDocument.newTextDocument();

            OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();

            OdfDefaultStyle def = styles.getOrCreateDefaultStyle(OdfStyleFamily.Paragraph);
            def.setProperty(StyleTextPropertiesElement.TextUnderlineColor, "#00FF00");

            OdfStyle parent = styles.newStyle("TheParent", OdfStyleFamily.Paragraph);
            parent.setProperty(StyleTextPropertiesElement.FontSize, "17pt");
            parent.setProperty(StyleTextPropertiesElement.Color, "#FF0000");

            OdfStyle child = styles.newStyle("TheChild", OdfStyleFamily.Paragraph);
            child.setStyleParentStyleNameAttribute(parent.getStyleNameAttribute());

            Assert.assertEquals("17pt", child.getProperty(StyleTextPropertiesElement.FontSize));
            Assert.assertEquals("#FF0000", child.getProperty(StyleTextPropertiesElement.Color));
            Assert.assertEquals("#00FF00", child.getProperty(StyleTextPropertiesElement.TextUnderlineColor));

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }

    /**
     * Tes included to reproduce bug29
     *
     */
    @Test
    public void testAutomaticStyleRename() {
        try {
            OdfDocument doc = OdfTextDocument.newTextDocument();
            OdfFileDom dom = doc.getContentDom();

            OdfOfficeAutomaticStyles autoStyles = new OdfOfficeAutomaticStyles(dom);
            OdfStyle paraStyle = autoStyles.newStyle(OdfStyleFamily.Paragraph);

            paraStyle.setStyleNameAttribute("newName");
            OdfStyle newStyle = autoStyles.getStyle("newName", OdfStyleFamily.Paragraph);

            Assert.assertNotNull(newStyle);
            Assert.assertSame(paraStyle, newStyle);

            autoStyles.optimize();
            newStyle = autoStyles.getStyle("newName", OdfStyleFamily.Paragraph);
            // unused style removed
            Assert.assertNull(newStyle);

            // new style created
            OdfStyle style = autoStyles.newStyle(OdfStyleFamily.Paragraph);
            style.setStyleNameAttribute("newName1");
            OdfStyle style2 = autoStyles.newStyle(OdfStyleFamily.Paragraph);
            style2.setStyleNameAttribute("newName2");

            autoStyles.removeChild(style2);
            OdfStyle style3 = autoStyles.getStyle("newName1", OdfStyleFamily.Paragraph);
            // assert the correct style was removed
            Assert.assertNotNull(style3);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
        }
    }

    /**
     * Setting style property on an automatic style, which occurs on multiple
     * elements and does not have a style parent results in an error (Bug 124).
     */
    @Test
    public void testAutomaticStyleSharing() {
        try {
            OdfDocument odfDocument = OdfDocument.loadDocument(ResourceUtilities.getTestResource("sharedautostyles.odt"));
            OdfFileDom dom = odfDocument.getContentDom();

            NodeList lst = dom.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getNamespaceUri(), "p");
            Assert.assertTrue(lst.getLength() == 2);

            OdfTextParagraph p = (OdfTextParagraph)lst.item(0);
            p.setProperty(OdfStyleTextProperties.FontSize, "17pt");
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
        }
    }
}
