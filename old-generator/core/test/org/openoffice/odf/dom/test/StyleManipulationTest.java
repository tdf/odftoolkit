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
package org.openoffice.odf.dom.test;



import org.junit.Assert;
import org.junit.Test;
import org.openoffice.odf.doc.OdfDocument;
import org.openoffice.odf.doc.element.office.OdfStyles;
import org.openoffice.odf.doc.element.style.OdfStyle;
import org.openoffice.odf.doc.element.style.OdfParagraphProperties;
import org.openoffice.odf.dom.style.OdfStyleFamily;

public class StyleManipulationTest {

    private static final String SOURCE = "test/resources/empty.odt";
    private static final String TARGET = "build/test/stylemanipulationtest.odt";
    
    public StyleManipulationTest() {}

    @Test
    public void testLoadSave() {
        try {
            OdfDocument odfDocument = OdfDocument.loadDocument(SOURCE);
            OdfStyles styles = odfDocument.getDocumentStyles();
            Assert.assertNotNull(styles);
            OdfStyle standardStyle = styles.getStyle("Standard", OdfStyleFamily.Paragraph);
            standardStyle.setProperty(OdfParagraphProperties.MarginLeft, "4711");
            odfDocument.save(TARGET);
            odfDocument = OdfDocument.loadDocument(TARGET);
            styles = odfDocument.getDocumentStyles();
            standardStyle = styles.getStyle("Standard", OdfStyleFamily.Paragraph);
            String marginLeft = standardStyle.getProperty(OdfParagraphProperties.MarginLeft);
            
            Assert.assertTrue(marginLeft != null && marginLeft.equals("4711"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }        
    }
}
