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

import org.odftoolkit.odfdom.OdfXMLFactory;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FactoryManipulationTest {

    public FactoryManipulationTest() {
    }

    @Test
    public void testFactoryManipulation() {
        try {
            // MyOwnPrivateSpanClass_1 is derived from OdfSpan (doc layer)
            OdfXMLFactory.setOdfElementClass(TextSpanElement.ELEMENT_NAME, MyOwnPrivateSpanClass_1.class);
            OdfPackage pkg = OdfPackage.loadPackage(ResourceUtilities.getAbsolutePath("factorymanipulation.odt"));
            OdfFileDom contentDom = OdfDocument.loadDocument(pkg).getContentDom();
            NodeList lst = contentDom.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getUri(), "span");
            Assert.assertTrue(lst.getLength() == 1);
            Node node = lst.item(0);
            Assert.assertTrue(node instanceof MyOwnPrivateSpanClass_1);
            
            // MyOwnPrivateSpanClass_2 is derived from TextSpanElement (dom layer)
            OdfXMLFactory.setOdfElementClass(TextSpanElement.ELEMENT_NAME, MyOwnPrivateSpanClass_2.class);
            contentDom = OdfDocument.loadDocument(pkg).getContentDom();
            lst = contentDom.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getUri(), "span");
            Assert.assertTrue(lst.getLength() == 1);
            node = lst.item(0);
            Assert.assertTrue(node instanceof MyOwnPrivateSpanClass_2);

            // MyOwnPrivateSpanClass_3 is derived from OdfElement to replace TextSpanElement (dom layer)
            OdfXMLFactory.setOdfElementClass(TextSpanElement.ELEMENT_NAME, MyOwnPrivateSpanClass_3.class);
            contentDom = OdfDocument.loadDocument(pkg).getContentDom();
            lst = contentDom.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getUri(), "span");
            Assert.assertTrue(lst.getLength() == 1);
            node = lst.item(0);
            Assert.assertTrue(node instanceof MyOwnPrivateSpanClass_3);

            // MyOwnPrivateOdfElement is derived from OdfElement to handle <text:userdefined>
            OdfXMLFactory.setOdfElementClass(MyOwnPrivateOdfElement.ELEMENT_NAME, MyOwnPrivateOdfElement.class);
            contentDom = OdfDocument.loadDocument(pkg).getContentDom();
            lst = contentDom.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getUri(), "userdefined");
            Assert.assertTrue(lst.getLength() == 1);
            node = lst.item(0);
            Assert.assertTrue(node instanceof MyOwnPrivateOdfElement);
            
            //set TextSpanElement.ELEMENT_NAME back to org.odftoolkit.odfdom.doc.element.text.OdfSpan
            OdfXMLFactory.setOdfElementClass(TextSpanElement.ELEMENT_NAME, org.odftoolkit.odfdom.doc.text.OdfTextSpan.class);
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
