/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.doc;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FrameTest {

  private static final Logger LOG = Logger.getLogger(FrameTest.class.getName());

  public FrameTest() {}

  @Test
  public void testFrame() {
    try {
      OdfDocument odfdoc =
          OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath("frame.odt"));
      NodeList lst =
          odfdoc
              .getContentDom()
              .getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
      Assert.assertEquals(lst.getLength(), 1);
      Node node = lst.item(0);
      Assert.assertTrue(node instanceof OdfDrawFrame);
      OdfDrawFrame fe = (OdfDrawFrame) lst.item(0);

      Assert.assertEquals(fe.getProperty(StyleGraphicPropertiesElement.VerticalPos), "top");
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }
}
