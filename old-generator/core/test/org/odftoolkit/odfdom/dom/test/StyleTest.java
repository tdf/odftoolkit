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
package org.odftoolkit.odfdom.dom.test;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.element.office.OdfStyles;
import org.odftoolkit.odfdom.doc.element.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfParagraphProperties;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfTextProperties;
import org.odftoolkit.odfdom.doc.element.text.OdfParagraph;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;

public class StyleTest {

    public StyleTest() {
    }

/*        
    @Test
    public void testStyleOrdering() {
        OdfStyle style1 = new OdfStyle("style1", OdfStyleFamily.Text);
        style1.setProperty(OdfTextProperties.FontWeight, "bold");

        OdfTextStyle ts1 = new OdfTextStyle("text1");
        ts1.setProperty(ts1.FontName, "Helvetica");

        OdfStyle style2 = new OdfStyle("style2", OdfStyleFamily.getByName("text"));
        style2.setProperty(OdfStylePropertiesSet.TextProperties, OdfNamespace.FO, "font-weight", "bold");

        OdfStyle style3 = new OdfStyle("style3", OdfStyleFamily.Text);
        style3.setProperty(OdfStylePropertiesSet.TextProperties, OdfNamespace.FO, "font-weight", "bold");
        style3.setProperty(OdfStylePropertiesSet.TextProperties, OdfNamespace.FO, "font-size", "15pt");

        OdfStyle style4 = new OdfStyle("style4", OdfStyleFamily.Text);

        Assert.assertTrue(style1.compareTo(null) > 0);

        Assert.assertTrue(style1.compareTo(style2) == 0);
        Assert.assertTrue(style2.compareTo(style1) == 0);

        Assert.assertTrue(style1.compareTo(style3) < 0);
        Assert.assertTrue(style3.compareTo(style1) > 0);

        Assert.assertTrue(style4.compareTo(style1) < 0);
    }
*/ 
 
    @Test
    public void testPropertyInheritance() {
        try {
            OdfDocument doc = OdfTextDocument.createTextDocument();

            OdfStyles styles = doc.getOrCreateDocumentStyles();
            
            OdfDefaultStyle def = styles.getOrCreateDefaultStyle(OdfStyleFamily.Paragraph);
            def.setProperty(OdfTextProperties.TextUnderlineColor, "#00FF00");
            
            OdfStyle parent = styles.createStyle("TheParent", OdfStyleFamily.Paragraph);          
            parent.setProperty(OdfTextProperties.FontSize, "17pt");
            parent.setProperty(OdfTextProperties.Color, "#FF0000");

            OdfStyle child = styles.createStyle("TheChild", OdfStyleFamily.Paragraph);
            child.setParentStyleName(parent.getName());

            Assert.assertEquals("17pt", child.getProperty(OdfTextProperties.FontSize));
            Assert.assertEquals("#FF0000", child.getProperty(OdfTextProperties.Color));
            Assert.assertEquals("#00FF00", child.getProperty(OdfTextProperties.TextUnderlineColor));
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }
}
