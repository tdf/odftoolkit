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

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LoadSaveTest {

    private static final String SOURCE = "empty.odt";
    private static final String TARGET = "loadsavetest.odt";

    public LoadSaveTest() {
    }

    @Test
    public void testLoadSave() {
        try {
            OdfDocument odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE));
            Assert.assertTrue(odfDocument.getPackage().contains("content.xml"));
            String baseURI = odfDocument.getBaseURI();
            Assert.assertEquals(ResourceUtilities.getURI(SOURCE).toString(), baseURI);

            Document odfContent = odfDocument.getContentDom();
            NodeList lst = odfContent.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getNamespaceUri(), "p");
            Node node = lst.item(0);
            String oldText = "Changed!!!";
            node.setTextContent(oldText);

            odfDocument.save(ResourceUtilities.newTestOutputFile(TARGET));
            odfDocument = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TARGET));

            odfContent = odfDocument.getContentDom();
            lst = odfContent.getElementsByTagNameNS(OdfNamespaceNames.TEXT.getNamespaceUri(), "p");
            node = lst.item(0);
            String newText = node.getTextContent();

            Assert.assertTrue(newText.equals(oldText));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
