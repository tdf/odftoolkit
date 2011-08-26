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
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableRowPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Tests if default styles are parsed correctly into
 * the defaultstyleCollection of the OdfDocument
 */
public class DefaultStylesTest {

    private static String TEST_FILE = "test2.odt";

    public DefaultStylesTest() {
    }

    @Test
    public void testDefaultStyles() {
        try {
            OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

            doc.getDocumentStyles();
            OdfDefaultStyle oDSG = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Graphic);
            Assert.assertEquals(oDSG.getFamilyName(), OdfStyleFamily.Graphic.getName());
            String prop1 = oDSG.getProperty(StyleGraphicPropertiesElement.ShadowOffsetX);
            Assert.assertEquals(prop1, "0.1181in");

            OdfDefaultStyle oDSP = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Paragraph);
            Assert.assertEquals(oDSP.getFamilyName(), OdfStyleFamily.Paragraph.getName());
            String prop2 = oDSP.getProperty(StyleTextPropertiesElement.FontName);
            Assert.assertEquals(prop2, "Thorndale");
            String prop3 = oDSP.getProperty(StyleTextPropertiesElement.LetterKerning);
            Assert.assertEquals(prop3, "true");

            OdfDefaultStyle oDST = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Table);
            Assert.assertEquals(oDST.getFamilyName(), OdfStyleFamily.Table.getName());
            String prop4 = oDST.getProperty(StyleTablePropertiesElement.BorderModel);
            Assert.assertEquals(prop4, "collapsing");


            OdfDefaultStyle oDSTR = doc.getDocumentStyles().getDefaultStyle(OdfStyleFamily.TableRow);
            Assert.assertEquals(oDSTR.getFamilyName(), OdfStyleFamily.TableRow.getName());
            String prop5 = oDSTR.getProperty(StyleTableRowPropertiesElement.KeepTogether);
            Assert.assertEquals(prop5, "auto");


        } catch (Exception e) {
            Logger.getLogger(DefaultStylesTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Assert.fail(e.getMessage());
        }

    }
}
