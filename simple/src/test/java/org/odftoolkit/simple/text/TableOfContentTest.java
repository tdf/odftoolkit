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

package org.odftoolkit.simple.text;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextTableOfContentElement;
import org.odftoolkit.odfdom.dom.element.text.TextTocMarkElement;
import org.odftoolkit.odfdom.dom.element.text.TextTocMarkStartElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.TOCStyle;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class TableOfContentTest {
  private static final Logger LOG = Logger.getLogger(TableOfContentTest.class.getName());

  @Test
  public void testCreateDefaultTOC() {
    try {
      TextDocument doc = buildSample();
      Assert.assertNotNull(doc);

      Paragraph paragraph1 = doc.getParagraphByIndex(0, true);
      TextTableOfContentElement textTableOfContentElement = doc.createDefaultTOC(paragraph1, false);
      Assert.assertNotNull(textTableOfContentElement);

      Node pnode = paragraph1.getOdfElement().getNextSibling();

      if (pnode.equals(textTableOfContentElement)) {
        Assert.assertTrue(true);
      } else {
        Assert.fail();
      }

      try {
        // Should throw error!
        doc.createDefaultTOC(null, true);
        Assert.fail();
      } catch (Exception e) {
        //				LOG.log(Level.SEVERE, null, e);
        Assert.assertTrue(true);
      }
      doc.save(ResourceUtilities.newTestOutputFile("DefaultTOC.odt"));
    } catch (Exception e) {
      LOG.log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testCreateStyleTOC() {
    try {
      TextDocument doc = buildSample();
      Assert.assertNotNull(doc);

      TOCStyle tocstyle = new TOCStyle();
      tocstyle.addStyle("User_20_Index_20_1", 1);
      tocstyle.addStyle("User_20_Index_20_2", 2);
      tocstyle.addStyle("User_20_Index_20_3", 3);
      tocstyle.addStyle("User_20_Index_20_4", 4);
      tocstyle.addStyle("User_20_Index_20_5", 5);
      tocstyle.addStyle("User_20_Index_20_6", 6);
      tocstyle.addStyle("User_20_Index_20_7", 7);
      tocstyle.addStyle("User_20_Index_20_8", 8);
      tocstyle.addStyle("User_20_Index_20_9", 9);
      tocstyle.addStyle("User_20_Index_20_10", 10);
      LOG.info(tocstyle.toString());

      Paragraph paragraph1 = doc.getParagraphByIndex(0, true);
      TextTableOfContentElement textTableOfContentElement =
          doc.createTOCwithStyle(paragraph1, tocstyle, true);
      Assert.assertNotNull(textTableOfContentElement);
      Node pnode = textTableOfContentElement.getNextSibling();
      if (pnode.equals(paragraph1.getOdfElement())) {
        Assert.assertTrue(true);
      } else {
        Assert.fail();
      }
      try {
        // Should throw error!
        doc.createTOCwithStyle(null, tocstyle, true);
        Assert.fail();
      } catch (Exception e) {
        //				LOG.log(Level.SEVERE, null, e);
        Assert.assertTrue(true);
      }
      doc.save(ResourceUtilities.newTestOutputFile("TOC_Styles.odt"));
    } catch (Exception e) {
      LOG.log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  private TextDocument buildSample() throws Exception {
    TextDocument doc = TextDocument.newTextDocument();
    Paragraph p1 = doc.addParagraph("1.This is a test paragraph apply with Heading 1 style!");
    p1.applyHeading();
    Paragraph p11 = doc.addParagraph("1.1This is a test paragraph apply with heading 2 style!");
    p11.applyHeading(true, 2);
    Paragraph p2 = doc.addParagraph("2.This is a test paragraph apply with Heading 1 style!");
    p2.applyHeading();
    Paragraph p21 = doc.addParagraph("2.1This is a test paragraph apply with Heading 2 style!");
    p21.applyHeading(true, 2);
    Paragraph p22 = doc.addParagraph("2.2This is a test paragraph apply with Heading 2 style!");
    p22.applyHeading(true, 2);
    Paragraph p221 = doc.addParagraph("2.2.1This is a test paragraph apply with Heading 3 style!");
    p221.applyHeading(true, 3);
    Paragraph p222 = doc.addParagraph("2.2.2This is a test paragraph apply with Heading 3 style!");
    p222.applyHeading(true, 3);
    Paragraph p23 = doc.addParagraph("2.3This is a test paragraph apply with Heading 2 style!");
    p23.applyHeading(true, 2);
    Paragraph p3 = doc.addParagraph("3.This is a test paragraph apply with ");
    p3.applyHeading();
    TextHElement h3 = (TextHElement) p3.getOdfElement();
    TextTocMarkElement TextTocMark = h3.newTextTocMarkElement("TextTocMarkElement");
    TextTocMark.setTextOutlineLevelAttribute(2);
    h3.newTextNode("Heading ");
    TextTocMarkStartElement TextTocMarkStart = h3.newTextTocMarkStartElement("IMark159230668");
    TextTocMarkStart.setTextOutlineLevelAttribute(3);
    h3.newTextNode("1 style!");
    h3.newTextTocMarkEndElement("IMark159230668");

    Paragraph p4 = doc.addParagraph("4.This is a test paragraph apply with User Index 1 style!");
    p4.getOdfElement().setStyleName("User_20_Index_20_1");

    Paragraph p5 = doc.addParagraph("5.This is a test paragraph apply with User Index 1 style!");
    p5.getOdfElement().setStyleName("User_20_Index_20_1");

    Paragraph p51 = doc.addParagraph("5.1 This is a test paragraph apply with User Index 2 style!");
    p51.getOdfElement().setStyleName("User_20_Index_20_2");

    Paragraph p52 = doc.addParagraph("5.2 This is a test paragraph apply with User Index 2 style!");
    p52.getOdfElement().setStyleName("User_20_Index_20_2");

    Paragraph p6 = doc.addParagraph("6.This is a test paragraph apply with User Index 1 style!");
    p6.getOdfElement().setStyleName("User_20_Index_20_1");

    Paragraph p62 = doc.addParagraph("6.1This is a test paragraph apply with User Index 2 style!");
    p62.getOdfElement().setStyleName("User_20_Index_20_2");
    Paragraph p63 =
        doc.addParagraph("6.1.1This is a test paragraph apply with User Index 3 style!");
    p63.getOdfElement().setStyleName("User_20_Index_20_3");
    Paragraph p64 =
        doc.addParagraph("6.1.1.1This is a test paragraph apply with User Index 4 style!");
    p64.getOdfElement().setStyleName("User_20_Index_20_4");
    Paragraph p65 =
        doc.addParagraph("6.1.1.1.1This is a test paragraph apply with User Index 5 style!");
    p65.getOdfElement().setStyleName("User_20_Index_20_5");
    Paragraph p66 =
        doc.addParagraph("6.1.1.1.1.1This is a test paragraph apply with User Index 6 style!");
    p66.getOdfElement().setStyleName("User_20_Index_20_6");
    Paragraph p67 =
        doc.addParagraph("6.1.1.1.1.1.1.1This is a test paragraph apply with User Index 7 style!");
    p67.getOdfElement().setStyleName("User_20_Index_20_7");
    Paragraph p68 =
        doc.addParagraph(
            "6.1.1.1.1.1.1.1.1This is a test paragraph apply with User Index 8 style!");
    p68.getOdfElement().setStyleName("User_20_Index_20_8");
    Paragraph p69 =
        doc.addParagraph(
            "6.1.1.1.1.1.1.1.1.1This is a test paragraph apply with User Index 9 style!");
    p69.getOdfElement().setStyleName("User_20_Index_20_9");
    Paragraph p60 =
        doc.addParagraph(
            "6.1.1.1.1.1.1.1.1.1.1This is a test paragraph apply with User Index 10 style!");
    p60.getOdfElement().setStyleName("User_20_Index_20_10");
    return doc;
  }
}
