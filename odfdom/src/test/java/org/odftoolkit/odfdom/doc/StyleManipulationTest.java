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
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class StyleManipulationTest {

    private static final String SOURCE = "empty.odt";
    private static final String TARGET = "stylemanipulationtest.odt";
    
    public StyleManipulationTest() {}

    @Test
    public void testLoadSave() {
        try {
            OdfDocument odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE));
            OdfOfficeStyles styles = odfDocument.getDocumentStyles();
            Assert.assertNotNull(styles);
            OdfStyle standardStyle = styles.getStyle("Standard", OdfStyleFamily.Paragraph);
            standardStyle.setProperty(StyleParagraphPropertiesElement.MarginLeft, "4711");
            odfDocument.save(ResourceUtilities.newTestOutputFile(TARGET));
            odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TARGET));
            styles = odfDocument.getDocumentStyles();
            standardStyle = styles.getStyle("Standard", OdfStyleFamily.Paragraph);
            String marginLeft = standardStyle.getProperty(StyleParagraphPropertiesElement.MarginLeft);
            
            Assert.assertTrue(marginLeft != null && marginLeft.equals("4711"));
        } catch (Exception e) {
            Logger.getLogger(StyleManipulationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            Assert.fail(e.getMessage());
        }        
    }
}
