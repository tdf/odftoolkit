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
import org.openoffice.odf.dom.OdfNamespace;
import org.openoffice.odf.doc.OdfDocument;
import org.openoffice.odf.doc.element.draw.OdfFrame;
import org.openoffice.odf.doc.element.style.OdfGraphicProperties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FrameTest {

    public FrameTest() {
    }

    @Test
    public void testFrame() {
        try {            
            OdfDocument odfdoc = OdfDocument.loadDocument("test/resources/frame.odt");
            NodeList lst = odfdoc.getContentDom().getElementsByTagNameNS(OdfNamespace.DRAW.getUri(), "frame");
            Assert.assertEquals( lst.getLength(), (int)1 );
            Node node = lst.item(0);
            Assert.assertTrue(node instanceof OdfFrame);
            OdfFrame fe = (OdfFrame) lst.item(0);

            Assert.assertEquals( fe.getProperty(OdfGraphicProperties.VerticalPos), "top");            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
