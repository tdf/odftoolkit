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
package org.odftoolkit.simple.style;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.ParagraphStyleHandler;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ParagraphPropertiesTest {

  private static final Logger LOGGER = Logger.getLogger(ParagraphPropertiesTest.class.getName());
  static final String filename = "testGetCellAt.ods";

  @Test
  public void testGetSetBorder() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      Paragraph paragraph1 = doc.addParagraph("paragraph1");

      paragraph1.setHorizontalAlignment(HorizontalAlignmentType.JUSTIFY);
      HorizontalAlignmentType align = paragraph1.getHorizontalAlignment();
      Assert.assertEquals(HorizontalAlignmentType.JUSTIFY, align);

      paragraph1.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
      align = paragraph1.getHorizontalAlignment();
      Assert.assertEquals(HorizontalAlignmentType.LEFT, align);

      paragraph1.setHorizontalAlignment(HorizontalAlignmentType.RIGHT);
      align = paragraph1.getHorizontalAlignment();
      Assert.assertEquals(HorizontalAlignmentType.RIGHT, align);

      doc.save(
          ResourceUtilities.newTestOutputFile("TestParagraphPropertiesSetGetHoriAlignment.odt"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail();
    }
  }

  @Test
  public void testGetSetBackgroundColor() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      Paragraph paragraph1 = doc.addParagraph("paragraph1");

      ParagraphStyleHandler psh = paragraph1.getStyleHandler();
      ParagraphProperties paraProp = psh.getParagraphPropertiesForWrite();
      paraProp.setBackgroundColor(new Color("#FF0000"));

      psh = paragraph1.getStyleHandler();
      paraProp = psh.getParagraphPropertiesForWrite();

      Assert.assertEquals("#FF0000", paraProp.getBackgroundColorAttribute());

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail();
    }
  }
}
