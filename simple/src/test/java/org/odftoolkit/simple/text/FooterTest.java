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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Fields;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.common.field.VariableField.VariableType;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.MasterPage;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class FooterTest {

  String footerDocumentPath = "FooterTableDocument.odt";

  @Test
  public void testAddTable() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      Assert.assertNotNull(footer);

      Table table = footer.addTable(1, 1);
      table.setTableName("footerTable");
      int rowCount = table.getRowCount();
      int columnCount = table.getColumnCount();
      String expectedCellValue = "footer table cell";
      Cell cellByPosition = table.getCellByPosition(0, 0);
      cellByPosition.setStringValue(expectedCellValue);
      cellByPosition.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
      cellByPosition.setCellBackgroundColor(Color.GREEN);

      // first page
      footer = doc.getFooter(true);
      Assert.assertNotNull(footer);
      table = footer.addTable(1, 2);
      table.setTableName("footerFTable");
      doc.save(ResourceUtilities.newTestOutputFile(footerDocumentPath));

      // load the document again.
      doc =
          TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(footerDocumentPath));
      footer = doc.getFooter();
      table = footer.getTableByName("footerTable");
      Assert.assertEquals(rowCount, table.getRowCount());
      Assert.assertEquals(columnCount, table.getColumnCount());
      Assert.assertEquals(expectedCellValue, cellByPosition.getStringValue());

      footer = doc.getFooter(true);
      table = footer.getTableByName("footerFTable");
      Assert.assertNotNull(table);
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testFooterHidden() {
    try {
      TextDocument doc =
          TextDocument.loadDocument(
              ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
      Footer footer = doc.getFooter();
      Assert.assertEquals(true, footer.isVisible());
      footer.setVisible(false);
      Assert.assertEquals(false, footer.isVisible());
      doc.save(ResourceUtilities.newTestOutputFile("footerHiddenOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetOdfElement() {
    try {
      // TextDocument doc =
      // TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      StyleFooterElement footerEle = footer.getOdfElement();
      footerEle.setTextContent("hello world");
      Assert.assertEquals("hello world", footerEle.getTextContent());

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testAddtable() {
    try {
      // TextDocument doc =
      // TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      Table tab = footer.addTable();
      Assert.assertNotNull(tab);
      Assert.assertTrue(2 == tab.getRowCount());
      Assert.assertTrue(5 == tab.getColumnCount());

      TableTableElement tabEle = tab.getOdfElement();
      System.out.println(tabEle);

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetTableList() {
    try {
      // TextDocument doc =
      // TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      Table tab = footer.addTable();

      List<Table> tabList = footer.getTableList();
      Assert.assertEquals(tab, tabList.get(0));

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetTableBuilder() {
    try {
      // TextDocument doc =
      // TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      // Table tab = footer.addTable();

      TableBuilder tb = footer.getTableBuilder();
      Table tab = tb.newTable();

      Assert.assertNotNull(tab);
      Assert.assertTrue(2 == tab.getRowCount());
      Assert.assertTrue(5 == tab.getColumnCount());

      // save
      doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetVariableContainerElement() {
    try {
      // TextDocument doc =
      // TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      OdfElement odfEle = footer.getVariableContainerElement();

      TableBuilder tb = footer.getTableBuilder();
      Table tab = tb.newTable();

      Assert.assertNotNull(tab);
      Assert.assertTrue(2 == tab.getRowCount());
      Assert.assertTrue(5 == tab.getColumnCount());

      Node nod = odfEle.getFirstChild();
      Assert.assertEquals("table:table", nod.getNodeName());

      // save
      doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testDeclareVariable() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      footer.declareVariable("footername", VariableType.USER);

      // validate
      StyleFooterElement styleFoot = footer.getOdfElement();
      Node nod = styleFoot.getFirstChild().getFirstChild();
      NamedNodeMap nameMap = nod.getAttributes();
      Node nodtext = nameMap.getNamedItem("text:name");
      Assert.assertEquals("footername", nodtext.getNodeValue());

      // save
      doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetVariableFieldByName() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      footer.declareVariable("footername", VariableType.USER);
      VariableField vField = footer.getVariableFieldByName("footername");
      String vName = vField.getVariableName();

      // validate
      StyleFooterElement styleFoot = footer.getOdfElement();
      Node nod = styleFoot.getFirstChild().getFirstChild();
      NamedNodeMap nameMap = nod.getAttributes();
      Node nodtext = nameMap.getNamedItem("text:name");
      Assert.assertEquals(vName, nodtext.getNodeValue());

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("footerOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testAppendNewSection() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      Footer footer = doc.getFooter();
      Section sect = footer.appendSection("Section1");
      Assert.assertNotNull(sect);

      StyleFooterElement styleFoot = footer.getOdfElement();
      Node nod = styleFoot.getFirstChild();
      NamedNodeMap nameMap = nod.getAttributes();
      Node nodtext = nameMap.getNamedItem("text:name");
      Assert.assertEquals("Section1", nodtext.getNodeValue());

    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testHeaderFooterFontSize() {
    try {
      final String TEXT_DATA_INPUT_FILE_NAME = "testData4HeaderFooterTest.txt";
      final String ODT_DATA_OUTPUT_FILE_NAME = "testData4HeaderFooterTest.odt";

      // Opens infile
      BufferedReader infile =
          new BufferedReader(
              new FileReader(ResourceUtilities.getAbsolutePath(TEXT_DATA_INPUT_FILE_NAME)));

      // Creates new doc and sets defaults see
      // http://www.oracle.com/technetwork/java/javase/javase7locales-334809.html
      Locale locale_en_US = new Locale("en", "US");
      Locale locale_ar_SA = new Locale("ar", "SA"); // Saudi Arabia
      Locale locale_ko_KR = new Locale("ko", "KR"); // South Korea
      Font font_en_US =
          new Font("Courier New", StyleTypeDefinitions.FontStyle.REGULAR, 10, locale_en_US);
      Font font_ar_SA =
          new Font("Courier New", StyleTypeDefinitions.FontStyle.REGULAR, 10, locale_ar_SA);
      Font font_ko_KR =
          new Font("Courier New", StyleTypeDefinitions.FontStyle.REGULAR, 10, locale_ko_KR);

      TextDocument doc = TextDocument.newTextDocument();
      MasterPage master = MasterPage.getOrCreateMasterPage(doc, "Standard");
      master.setPageHeight(279.4);
      master.setPageWidth(215.9);
      master.setMargins(19.05, 19.05, 19.05, 19.05); // 19.05 mm = 0.75 in
      doc.setPageColumns(2, 0.635); // 0.635 cm = 0.25 in
      doc.setLocale(locale_en_US);

      // Adds header
      Header header = doc.getHeader();
      Section headersection = header.appendSection("Title");
      Paragraph headerparagraph = headersection.addParagraph(null);
      headerparagraph.setFont(font_en_US);
      headerparagraph.setFont(font_ar_SA);
      headerparagraph.setFont(font_ko_KR);
      headerparagraph.appendTextContent(infile.readLine());
      headerparagraph.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);

      // Adds Page X of Y to the footer
      Font fontsmaller_en_US =
          new Font(
              "Courier New", StyleTypeDefinitions.FontStyle.ITALIC, 8, Color.RED, locale_en_US);
      Font fontsmaller_ar_SA =
          new Font(
              "Courier New", StyleTypeDefinitions.FontStyle.ITALIC, 8, Color.RED, locale_ar_SA);
      Font fontsmaller_ko_KR =
          new Font(
              "Courier New", StyleTypeDefinitions.FontStyle.ITALIC, 8, Color.RED, locale_ko_KR);
      Footer footer = doc.getFooter();
      Section footersection = footer.appendSection("Page Numbers");
      Paragraph footerparagraph = footersection.addParagraph("Page\u00A0");
      Fields.createCurrentPageNumberField(footerparagraph.getOdfElement());
      footerparagraph.appendTextContent("\u00A0of\u00A0");
      Fields.createPageCountField(footerparagraph.getOdfElement());
      footerparagraph.setFont(fontsmaller_en_US); // Doesn't work here
      footerparagraph.setFont(fontsmaller_ar_SA); // Doesn't work here
      footerparagraph.setFont(fontsmaller_ko_KR); // Doesn't work here
      footerparagraph.setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType.CENTER);

      // Special handling for first line
      Paragraph p = doc.getParagraphByIndex(0, false);
      p.setFont(font_en_US);
      p.setFont(font_ar_SA);
      p.setFont(font_ko_KR);
      infile.readLine();
      p.appendTextContent(infile.readLine());

      // Loops over all other lines
      String inline = new String();
      while ((inline = infile.readLine()) != null) {
        // p.appendTextContent("\r\n" + inline.replaceAll("\n", "\r\n")); // Works, because it
        // doesn't use addParagraph
        p = doc.addParagraph(inline.replaceAll("\n", "\r\n"));
        p.setFont(font_en_US); // Does not work, because it uses addParagraph
        p.setFont(font_ar_SA); // Does not work, because it uses addParagraph
        p.setFont(font_ko_KR); // Does not work, because it uses addParagraph
      }
      infile.close();

      // Saves outfile
      doc.save(ResourceUtilities.newTestOutputFile(ODT_DATA_OUTPUT_FILE_NAME));

    } catch (Exception e) {
      Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }
}
