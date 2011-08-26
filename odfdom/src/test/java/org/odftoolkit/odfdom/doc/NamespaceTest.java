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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class NamespaceTest {

    private static final String TARGET = "namespacetest.odt";

    public NamespaceTest() {
    }

    @Test
    public void testNewNamespace() {
        try {
            OdfTextDocument doc = OdfTextDocument.newTextDocument();

            OdfFileDom contentDom = doc.getContentDom();
            XPath xpath = contentDom.getXPath();
            
            // Postive test for XPath on ODF attributes
            String resTest1 = xpath.evaluate("//text:p[@text:style-name='Standard']", contentDom);
            Assert.assertTrue(resTest1 != null);

            // Test XPath on none ODF attributes (added explicitly via DOM)
            OdfTextParagraph p = doc.newParagraph();
            p.setAttributeNS("http://myAttributeNamespace", "my:attr", "attrValue");
            String resAttr1 = xpath.evaluate("//*[@my:attr = 'attrValue']", contentDom);
            Assert.assertTrue(resAttr1 != null);

            // Test XPath on none ODF element (added explicitly via DOM)
            p.appendChild(contentDom.createElementNS("http://myElementNamespace", "my:element"));
            String resElement1 = xpath.evaluate("//my:element", contentDom);
            Assert.assertTrue(resElement1 != null);

            // Save documnet
            File targetFile = ResourceUtilities.newTestOutputFile(TARGET);
            doc.save(targetFile);


            
            // Load document with ODF foreign attriute
            OdfTextDocument docReloaded = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TARGET));
            OdfFileDom contentDomReloaded = docReloaded.getContentDom();

            // Postive test for XPath on ODF attributes
            xpath = contentDomReloaded.getXPath();
            String resTest2 = xpath.evaluate("//text:p[@text:style-name='Standard']", contentDomReloaded);
            Assert.assertTrue(resTest2 != null);
            
            // Test XPath on none ODF attributes (added via load)
            String resAttr2 = xpath.evaluate("//*[@my:attr = 'attrValue']", contentDomReloaded);
            Assert.assertTrue(resAttr2 != null);

            // Test XPath on none ODF element (added via load)
            p.appendChild(contentDom.createElementNS("http://myElementNamespace", "my:element"));
            String resElement2 = xpath.evaluate("//my:element", contentDomReloaded);
            Assert.assertTrue(resElement2 != null);

        } catch (Exception ex) {
        	Logger.getLogger(NamespaceTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
        }
    }
}
