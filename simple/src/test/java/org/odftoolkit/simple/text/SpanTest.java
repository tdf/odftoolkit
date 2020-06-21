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

import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.TextLinePosition;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class SpanTest {

  @Test
  public void testSpan() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      doc.addParagraph("This is a test paragraph!");
      TextNavigation navigation = new TextNavigation("test", doc);
      TextSelection sel = (TextSelection) navigation.nextSelection();
      Span span = Span.newSpan(sel);
      TextHyperlink link = span.applyHyperlink(new URI("http://www.ibm.com"));
      DefaultStyleHandler handler = span.getStyleHandler();
      Font font1Base =
          new Font("Arial", FontStyle.ITALIC, 10, Color.BLACK, TextLinePosition.THROUGH);
      handler.getTextPropertiesForWrite().setFont(font1Base);
      doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));

      String content = span.getTextContent();
      Assert.assertEquals("test", content);
      span.setTextContent("new test");
      Assert.assertEquals("new test", span.getTextContent());
      doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));

    } catch (Exception e) {
      Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testRemoveTextContent() {
    try {
      TextDocument doc = TextDocument.newTextDocument();

      Iterator<Paragraph> paraA = doc.getParagraphIterator();
      while (paraA.hasNext()) {
        Paragraph pp = paraA.next();
        doc.removeParagraph(pp);
      }

      doc.addParagraph("This is a test paragraph!");

      TextNavigation navigation = new TextNavigation("test", doc);
      TextSelection sel = (TextSelection) navigation.nextSelection();
      Span span = Span.newSpan(sel);

      span.removeTextContent();
      boolean flag = false;
      Iterator<Paragraph> parai = doc.getParagraphIterator();
      while (parai.hasNext()) {
        Paragraph pp = parai.next();
        if ("This is a  paragraph!".equals(pp.getTextContent())) flag = true;
      }
      Assert.assertTrue(flag);

      // save
      doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));

    } catch (Exception e) {
      Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testAppendTextContent() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      doc.addParagraph("This is a test paragraph!");

      TextNavigation navigation = new TextNavigation("test", doc);
      TextSelection sel = (TextSelection) navigation.nextSelection();
      Span span = Span.newSpan(sel);

      span.appendTextContent("hello world.");
      Assert.assertEquals("testhello world.", span.getTextContent());

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));
    } catch (Exception e) {
      Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testAppendTextContentPara() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      doc.addParagraph("This is a test paragraph!");

      TextNavigation navigation = new TextNavigation("test", doc);
      TextSelection sel = (TextSelection) navigation.nextSelection();
      Span span = Span.newSpan(sel);

      span.appendTextContent("hello world.", true);
      Assert.assertEquals("testhello world.", span.getTextContent());

      span.appendTextContent("hello world.", false);
      Assert.assertEquals("testhello world.hello world.", span.getTextContent());

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("spantest.odt"));
    } catch (Exception e) {
      Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testRemoveTextContentLb() {
    try {

      TextDocument doc = TextDocument.newTextDocument();

      Iterator<Paragraph> paraA = doc.getParagraphIterator();
      while (paraA.hasNext()) {
        Paragraph pp = paraA.next();
        doc.removeParagraph(pp);
      }

      Paragraph para = doc.addParagraph("This is a beforelb\nafterlb paragraph!");

      System.out.println(para.getTextContent());

      TextNavigation navigation = new TextNavigation("beforelb\nafterlb", doc);
      TextSelection sel = (TextSelection) navigation.nextSelection();
      Span span = Span.newSpan(sel);

      span.removeTextContent();
      boolean flag = false;
      Iterator<Paragraph> parai = doc.getParagraphIterator();
      while (parai.hasNext()) {
        Paragraph pp = parai.next();
        System.out.println(pp.getTextContent());
        if ("This is a  paragraph!".equals(pp.getTextContent())) {
          flag = true;
        }
      }
      Assert.assertTrue("Linebreak is not removed from paragraph!", flag);

    } catch (Exception e) {
      Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testRemoveTextContentTab() {
    try {

      TextDocument doc = TextDocument.newTextDocument();

      Iterator<Paragraph> paraA = doc.getParagraphIterator();
      while (paraA.hasNext()) {
        Paragraph pp = paraA.next();
        doc.removeParagraph(pp);
      }

      Paragraph para = doc.addParagraph("This is a beforetab\taftertab paragraph!");

      System.out.println(para.getTextContent());

      TextNavigation navigation = new TextNavigation("beforetab\taftertab", doc);
      TextSelection sel = (TextSelection) navigation.nextSelection();
      Span span = Span.newSpan(sel);

      span.removeTextContent();
      boolean flag = false;
      Iterator<Paragraph> parai = doc.getParagraphIterator();
      while (parai.hasNext()) {
        Paragraph pp = parai.next();
        System.out.println(pp.getTextContent());
        if ("This is a  paragraph!".equals(pp.getTextContent())) {
          flag = true;
        }
      }
      Assert.assertTrue("Tabulator is not removed from paragraph!", flag);

    } catch (Exception e) {
      Logger.getLogger(SpanTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }
}
