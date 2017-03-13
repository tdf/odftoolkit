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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;

public class ElementTest {

    public ElementTest() {
    }

    @Test
    public void testCloneNode() {
        try {
            OdfDocument doc = OdfTextDocument.newTextDocument();
            OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();

            OdfStyle p1 = styles.newStyle("P1", OdfStyleFamily.Paragraph);
            p1.setProperty(StyleTextPropertiesElement.FontSize, "42pt");

            OdfStyle clone = (OdfStyle) p1.cloneNode(true);
            clone.setStyleNameAttribute("p1-clone");
            styles.appendChild(clone);

            OdfStyle p1clone = styles.getStyle("p1-clone", OdfStyleFamily.Paragraph);
            Assert.assertNotNull(p1clone);
            Assert.assertEquals(p1clone.getProperty(StyleTextPropertiesElement.FontSize), p1.getProperty(StyleTextPropertiesElement.FontSize));
        } catch (Exception e) {
            Logger.getLogger(ElementTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }

    @Test
    public void testEquals() {
        try {
            OdfDocument doc = OdfTextDocument.newTextDocument();
            OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();

            OdfStyle p1 = styles.newStyle("P1", OdfStyleFamily.Paragraph);
            p1.setProperty(StyleTextPropertiesElement.FontSize, "42pt");
            OdfStyle p2 = styles.newStyle("P2", OdfStyleFamily.Paragraph);
            p2.setProperty(StyleTextPropertiesElement.FontSize, "42pt");
            OdfStyle p3 = styles.newStyle("P3", OdfStyleFamily.Paragraph);
            p3.setProperty(StyleTextPropertiesElement.FontSize, "13pt");

            OdfTextListStyle l1 = styles.newListStyle("L1");
            Assert.assertTrue(p1.equals(p1));
            Assert.assertTrue(p1.equals(p2));
            Assert.assertTrue(p2.equals(p1));
            Assert.assertFalse(p1.equals(p3));
            Assert.assertFalse(p3.equals(p1));
            Assert.assertFalse(p3.equals(p1));
            Assert.assertFalse(p1.equals(l1));
            Assert.assertFalse(p1.equals(null));

        } catch (Exception e) {
            Logger.getLogger(ElementTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }
}
