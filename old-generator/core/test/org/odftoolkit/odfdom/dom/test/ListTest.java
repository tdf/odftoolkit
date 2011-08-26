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

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.text.OdfList;
import org.odftoolkit.odfdom.doc.element.text.OdfListStyle;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ListTest {

    public ListTest() {
    }

    @Test
    public void testList() {
        try {            
            OdfDocument odfdoc = OdfDocument.loadDocument("test/resources/list.odt");
            OdfFileDom odfContent = odfdoc.getContentDom();
            NodeList lst = odfContent.getElementsByTagNameNS(OdfNamespace.TEXT.getUri(), "list");
            for (int i = 0; i < lst.getLength(); i++) {
                Node node = lst.item(i);
                Assert.assertTrue(node instanceof OdfList);
                OdfList le = (OdfList) lst.item(i);

                OdfListStyle ls = le.getListStyle();
                Assert.assertNotNull(ls);
                OdfElement lvl = ls.getLevel(1);
                Assert.assertNotNull(lvl);

                int level = le.getListLevel();
                OdfElement lvl1 = ls.getLevel(level);
                OdfElement lvl2 = le.getListLevelStyle();
                Assert.assertEquals(lvl1, lvl2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
