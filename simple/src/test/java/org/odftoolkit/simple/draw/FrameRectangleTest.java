/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.draw;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FrameRectangleTest {

  @Test
  public void testGetDesc() {
    String content = "This is a text box";
    try {
      TextDocument textDoc = TextDocument.newTextDocument();
      Paragraph p = textDoc.addParagraph("paragraph demo");
      FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.CM);
      Assert.assertEquals("4.21cm", frameR.getXDesc());
      Assert.assertEquals("1.32cm", frameR.getYDesc());
      Assert.assertEquals("4.41cm", frameR.getWidthDesc());
      Assert.assertEquals("3.92cm", frameR.getHeigthDesc());

      Textbox box = p.addTextbox(frameR);
      box.setName("tbox name");
      box.setTextContent(content);
      textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

    } catch (Exception e) {
      Logger.getLogger(FrameRectangleTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testSet() {
    String content = "This is a text box";
    try {
      TextDocument textDoc = TextDocument.newTextDocument();
      Paragraph p = textDoc.addParagraph("paragraph demo");
      FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.CM);
      frameR.setHeight(3.22);
      Assert.assertEquals(3.22, frameR.getHeight());
      frameR.setWidth(4.44);
      Assert.assertEquals(4.44, frameR.getWidth());

      Assert.assertEquals(SupportedLinearMeasure.CM, frameR.getLinearMeasure());
      frameR.setLinearMeasure(SupportedLinearMeasure.IN);
      Assert.assertEquals(SupportedLinearMeasure.IN, frameR.getLinearMeasure());

      Textbox box = p.addTextbox(frameR);
      box.setName("tbox name");
      box.setTextContent(content);
      textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

    } catch (Exception e) {
      Logger.getLogger(FrameRectangleTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }
}
