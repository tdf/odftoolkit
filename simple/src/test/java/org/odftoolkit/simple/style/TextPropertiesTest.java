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

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class TextPropertiesTest {

  private static final Logger LOGGER = Logger.getLogger(TextPropertiesTest.class.getName());

  @Test
  public void testGetFontSizeInPoint() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFontSizeInPoint(3.32);

      // validate
      Double fontInPoint = textProperties.getFontSizeInPoint();
      Assert.assertEquals(3.32, fontInPoint);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput89.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetFontStyle() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.BOLDITALIC);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle = textProperties.getFontStyle();
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.BOLDITALIC, fontStyle);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.BOLD);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle1 = textProperties.getFontStyle();
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.BOLD, fontStyle1);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.ITALIC);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle2 = textProperties.getFontStyle();
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.ITALIC, fontStyle2);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.REGULAR);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle3 = textProperties.getFontStyle();
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.REGULAR, fontStyle3);

      // save
      // document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetFontStyleParam() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFontStyle(
          StyleTypeDefinitions.FontStyle.BOLDITALIC, Document.ScriptType.WESTERN);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle =
          textProperties.getFontStyle(Document.ScriptType.WESTERN);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.BOLDITALIC, fontStyle);

      textProperties.setFontStyle(
          StyleTypeDefinitions.FontStyle.BOLDITALIC, Document.ScriptType.CJK);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle1 =
          textProperties.getFontStyle(Document.ScriptType.CJK);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.BOLDITALIC, fontStyle1);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.ITALIC, Document.ScriptType.CJK);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle2 =
          textProperties.getFontStyle(Document.ScriptType.CJK);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.ITALIC, fontStyle2);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.REGULAR, Document.ScriptType.CJK);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle3 =
          textProperties.getFontStyle(Document.ScriptType.CJK);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.REGULAR, fontStyle3);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.BOLD, Document.ScriptType.CTL);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle4 =
          textProperties.getFontStyle(Document.ScriptType.CTL);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.BOLD, fontStyle4);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.ITALIC, Document.ScriptType.CTL);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle5 =
          textProperties.getFontStyle(Document.ScriptType.CTL);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.ITALIC, fontStyle5);

      textProperties.setFontStyle(
          StyleTypeDefinitions.FontStyle.BOLDITALIC, Document.ScriptType.CTL);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle6 =
          textProperties.getFontStyle(Document.ScriptType.CTL);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.BOLDITALIC, fontStyle6);

      textProperties.setFontStyle(StyleTypeDefinitions.FontStyle.REGULAR, Document.ScriptType.CTL);
      // validate
      StyleTypeDefinitions.FontStyle fontStyle7 =
          textProperties.getFontStyle(Document.ScriptType.CTL);
      Assert.assertEquals(StyleTypeDefinitions.FontStyle.REGULAR, fontStyle7);

      // save
      // document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetSetLanguage() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setLanguage("Chinese");

      // validate
      String lan = textProperties.getLanguage();
      Assert.assertEquals("Chinese", lan);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetSetLanguageParam() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setLanguage("Chinese", Document.ScriptType.CJK);
      // validate
      String lan = textProperties.getLanguage(Document.ScriptType.CJK);
      Assert.assertEquals("Chinese", lan);

      textProperties.setLanguage("Chinese", Document.ScriptType.CTL);
      // validate
      String lan1 = textProperties.getLanguage(Document.ScriptType.CTL);
      Assert.assertEquals("Chinese", lan1);

      textProperties.setLanguage(null, Document.ScriptType.CTL);
      // validate
      String lan2 = textProperties.getLanguage(Document.ScriptType.CTL);
      Assert.assertEquals(null, lan2);

      textProperties.setLanguage(null, Document.ScriptType.WESTERN);
      // validate
      String lan3 = textProperties.getLanguage(Document.ScriptType.WESTERN);
      Assert.assertEquals(null, lan3);

      textProperties.setLanguage(null, Document.ScriptType.CJK);
      // validate
      String lan4 = textProperties.getLanguage(Document.ScriptType.CJK);
      Assert.assertEquals(null, lan4);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetCountry() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setCountry("china");

      // validate
      String country = textProperties.getCountry();
      Assert.assertEquals("china", country);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetCountryParam() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setCountry("china", Document.ScriptType.WESTERN);

      // validate
      String country = textProperties.getCountry(Document.ScriptType.WESTERN);
      Assert.assertEquals("china", country);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetColor() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFontColor(Color.GREEN);

      // validate
      Color green = textProperties.getFontColor();
      Assert.assertEquals(Color.GREEN.toString(), green.toString());

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetSetName() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFontName("fontname");

      // validate
      String fontName = textProperties.getFontName();
      Assert.assertEquals("fontname", fontName);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetSetNameParam() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFontName("fontname", Document.ScriptType.WESTERN);

      // validate
      String fontName = textProperties.getFontName(Document.ScriptType.WESTERN);
      Assert.assertEquals("fontname", fontName);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSetFont() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      // cell.setFont(font);
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFont(font);

      // validate
      Font font1 = textProperties.getFont();
      Assert.assertEquals(font, font1);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSetFontName() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Font font =
          new Font(
              "Arial",
              StyleTypeDefinitions.FontStyle.ITALIC,
              17.5,
              Color.GREEN,
              StyleTypeDefinitions.TextLinePosition.REGULAR,
              Locale.ENGLISH);
      Cell cell = table.getCellByPosition("A1");
      // cell.setFont(font);
      font.setFamilyName("Chinese");
      cell.setStringValue("testGetFontStyle.");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setFont(font);

      // validate
      String fontname = textProperties.getFontName();
      Assert.assertEquals("Chinese", fontname);

      // save
      document.save(ResourceUtilities.newTestOutputFile("testFontOutput1.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSubscripted() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Cell cell = table.getCellByPosition("A1");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setSubscripted(null);

      // validate
      Integer i = textProperties.getSubscripted();
      Assert.assertEquals(Integer.valueOf(58), i);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSuperscripted() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Cell cell = table.getCellByPosition("A1");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setSuperscripted(Integer.valueOf(50));

      // validate
      Integer i = textProperties.getSuperscripted();
      Assert.assertEquals(Integer.valueOf(50), i);

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testBackgroundColor() {
    try {
      SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = document.getTableByName("Sheet1");
      Cell cell = table.getCellByPosition("A1");

      TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
      textProperties.setBackgroundColorAttribute(new Color(255, 0, 0));

      // validate
      Assert.assertEquals("#ff0000", textProperties.getBackgroundColorAttribute());

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }
}
