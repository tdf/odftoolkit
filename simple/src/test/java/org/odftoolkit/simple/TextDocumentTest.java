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

package org.odftoolkit.simple;

import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFootnoteSepElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.odfdom.type.Percent;
import org.odftoolkit.simple.Document.OdfMediaType;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartType;
import org.odftoolkit.simple.chart.DataSet;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.style.MasterPage;
import org.odftoolkit.simple.style.NumberFormat;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AdjustmentStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.PrintOrientation;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class TextDocumentTest {

  private static final Logger LOG = Logger.getLogger(TextDocumentTest.class.getName());
  private static final String EMPTY_TEXT_DOCUMENT_PATH = "TextFieldSampleDocument.odt";

  @Test
  public void testAddPageBreak() {
    try {
      // test new creation document.
      TextDocument newDoc = TextDocument.newTextDocument();
      Paragraph paragraph = newDoc.addParagraph("before page break");
      newDoc.addPageBreak();
      validPageBreakExist(newDoc, paragraph);
      Paragraph refParagraph = newDoc.addParagraph("after page break");
      newDoc.addParagraph("end page");
      newDoc.addPageBreak(refParagraph);
      validPageBreakExist(newDoc, refParagraph);
      newDoc.save(ResourceUtilities.newTestOutputFile("AddPageBreakOutput.odt"));

      // test exist document.
      TextDocument existDoc =
          TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("test2.odt"));
      paragraph = existDoc.addParagraph("before page break");
      existDoc.addPageBreak();
      validPageBreakExist(existDoc, paragraph);
      refParagraph = existDoc.getParagraphByIndex(0, true);
      existDoc.addPageBreak(refParagraph);
      validPageBreakExist(existDoc, refParagraph);
      existDoc.save(ResourceUtilities.newTestOutputFile("test2Out.odt"));

    } catch (Exception e) {
      Logger.getLogger(TextDocumentTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  @Test
  public void testAddPagebreakWithMasterPage() {
    try {

      TextDocument newDoc = TextDocument.newTextDocument();

      // create a new master page
      Paragraph paragraph = newDoc.addParagraph("before page break - original Landscape");
      MasterPage master1 = MasterPage.getOrCreateMasterPage(newDoc, "Landscape");
      master1.setPageWidth(279.4);
      master1.setPageHeight(215.9);
      master1.setNumberFormat(NumberFormat.HINDU_ARABIC_NUMBER.toString());
      master1.setPrintOrientation(PrintOrientation.LANDSCAPE);
      master1.setFootnoteMaxHeight(0);
      master1.setWritingMode(StyleTypeDefinitions.WritingMode.LRTB);
      master1.setMargins(20, 20, 20, 20);
      master1.setFootnoteSepProperties(
          AdjustmentStyle.LEFT, Color.valueOf("#000000"), 1, 1, null, Percent.valueOf("25%"), 0.18);
      newDoc.addPageBreak(paragraph, master1);
      newDoc.addParagraph("after page break - original Landscape");
      validMasterPageApplied(newDoc, paragraph, master1);
      newDoc.save(ResourceUtilities.newTestOutputFile("AddPageBreakWithMasterPageOutput1.odt"));

      // modify the master page
      paragraph = newDoc.addParagraph("before page break - modified Landscape");
      MasterPage master2 = MasterPage.getOrCreateMasterPage(newDoc, "Landscape");
      master2.setPageWidth(100);
      master2.setPageHeight(300);
      master2.setNumberFormat(null);
      master2.setPrintOrientation(null);
      master2.setMargins(20, 20, 0, 0);
      newDoc.addPageBreak(paragraph, master2);
      newDoc.addParagraph("after page break - modified Landscape");
      validMasterPageApplied(newDoc, paragraph, master2);

      // apply existing master page
      paragraph = newDoc.addParagraph("before page break - Standard");
      MasterPage master3 = MasterPage.getOrCreateMasterPage(newDoc, "Standard");
      newDoc.addPageBreak(paragraph, master3);
      newDoc.addParagraph("after page break - Standard");
      newDoc.addParagraph("end page");
      newDoc.save(ResourceUtilities.newTestOutputFile("AddPageBreakWithMasterPageOutput2.odt"));
    } catch (Exception e) {
      Logger.getLogger(TextDocumentTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  private void validMasterPageApplied(
      TextDocument newDoc, Paragraph paragraph, MasterPage masterPage) throws Exception {
    Node paragraphNode = paragraph.getOdfElement().getNextSibling();
    Assert.assertTrue(paragraphNode instanceof TextPElement);

    // check paragraph style
    OdfOfficeAutomaticStyles styles = newDoc.getContentDom().getAutomaticStyles();
    OdfStyle style =
        styles.getStyle(((TextPElement) paragraphNode).getStyleName(), OdfStyleFamily.Paragraph);
    Assert.assertNotNull(style);

    // check master page style
    String masterName = style.getStyleMasterPageNameAttribute();
    StyleMasterPageElement master = newDoc.getOfficeMasterStyles().getMasterPage(masterName);
    Assert.assertEquals(masterPage.getName(), masterName);
    Assert.assertNotNull(master);

    // check page layout style
    String pageLayoutName = master.getStylePageLayoutNameAttribute();
    OdfStylePageLayout pageLayout = master.getAutomaticStyles().getPageLayout(pageLayoutName);
    Assert.assertNotNull(pageLayout);

    // check page layout properties
    StylePageLayoutPropertiesElement properties =
        (StylePageLayoutPropertiesElement)
            pageLayout.getPropertiesElement(OdfStylePropertiesSet.PageLayoutProperties);
    Assert.assertNotNull(properties);
    // page width
    checkDoubleValue(masterPage.getPageWidth(), properties.getFoPageWidthAttribute());
    // page height
    checkDoubleValue(masterPage.getPageHeight(), properties.getFoPageHeightAttribute());
    // footnote max height
    checkDoubleValue(
        masterPage.getFootnoteMaxHeight(), properties.getStyleFootnoteMaxHeightAttribute());
    // margins
    checkDoubleValue(masterPage.getMarginTop(), properties.getFoMarginTopAttribute());
    checkDoubleValue(masterPage.getMarginBottom(), properties.getFoMarginBottomAttribute());
    checkDoubleValue(masterPage.getMarginLeft(), properties.getFoMarginLeftAttribute());
    checkDoubleValue(masterPage.getMarginRight(), properties.getFoMarginRightAttribute());
    // writing mode
    checkStringValue(masterPage.getWritingMode(), properties.getStyleWritingModeAttribute());
    // number format
    checkStringValue(masterPage.getNumberFormat(), properties.getStyleNumFormatAttribute());
    // print orientation
    checkStringValue(
        masterPage.getPrintOrientation(), properties.getStylePrintOrientationAttribute());

    // check footnote separator line
    StyleFootnoteSepElement footnoteSep =
        (StyleFootnoteSepElement) properties.getElementsByTagName("style:footnote-sep").item(0);
    Assert.assertNotNull(footnoteSep);
    checkStringValue(
        masterPage.getFootnoteSepAdjustment(), footnoteSep.getStyleAdjustmentAttribute());
    checkStringValue(masterPage.getFootnoteSepColor(), footnoteSep.getStyleColorAttribute());
    checkStringValue(
        masterPage.getFootnoteSepLineStyle(), footnoteSep.getStyleLineStyleAttribute());
    checkDoubleValue(
        masterPage.getFootnoteSepDistanceAfterSep(),
        footnoteSep.getStyleDistanceAfterSepAttribute());
    checkDoubleValue(
        masterPage.getFootnoteSepDistanceBeforeSep(),
        footnoteSep.getStyleDistanceBeforeSepAttribute());
    checkDoubleValue(masterPage.getFootnoteSepThickness(), footnoteSep.getStyleWidthAttribute());
    checkDoubleValue(masterPage.getFootnoteSepWidth(), footnoteSep.getStyleRelWidthAttribute());
  }

  private void checkStringValue(String expected, String actual) {
    if (expected == null) {
      Assert.assertNull(actual);
    } else if (expected.equals("page")) {
      Assert.assertTrue(actual == null || actual.equals(expected));
    } else {
      Assert.assertEquals(expected, actual);
    }
  }

  private void checkDoubleValue(double expected, String actual) {
    double value = 0;
    if (actual != null) {
      if (Percent.isValid(actual)) {
        value = Percent.valueOf(actual).doubleValue();
      } else {
        value = Length.parseDouble(actual, Unit.MILLIMETER);
      }
    }
    Assert.assertEquals(expected, value);
  }

  @Test
  public void testAddComment() {
    try {
      // test new creation document.
      TextDocument newDoc = TextDocument.newTextDocument();

      Paragraph paragraph = newDoc.addParagraph("Paragraph1");
      paragraph.addComment("This is a comment for Paragraph1", "Simple ODF");
      Node firstChildNode = paragraph.getOdfElement().getFirstChild();
      Assert.assertTrue(firstChildNode instanceof OfficeAnnotationElement);
      OfficeAnnotationElement comment = (OfficeAnnotationElement) firstChildNode;
      Assert.assertEquals("Simple ODF", comment.getFirstChild().getTextContent());
      Assert.assertEquals(
          "This is a comment for Paragraph1", comment.getLastChild().getTextContent());
      Assert.assertTrue(firstChildNode instanceof OfficeAnnotationElement);

      paragraph = newDoc.addParagraph("Paragraph2");
      paragraph.addComment("This is a comment for Paragraph2", null);
      firstChildNode = paragraph.getOdfElement().getFirstChild();
      Assert.assertTrue(firstChildNode instanceof OfficeAnnotationElement);
      comment = (OfficeAnnotationElement) firstChildNode;
      Assert.assertEquals(
          System.getProperty("user.name"), comment.getFirstChild().getTextContent());
      Assert.assertEquals(
          "This is a comment for Paragraph2", comment.getLastChild().getTextContent());

      newDoc.save(ResourceUtilities.newTestOutputFile("AddCommentOutput.odt"));
    } catch (Exception e) {
      Logger.getLogger(TextDocumentTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }

  private void validPageBreakExist(TextDocument newDoc, Paragraph paragraph) throws Exception {
    Node paragraphNode = paragraph.getOdfElement().getNextSibling();
    Assert.assertTrue(paragraphNode instanceof TextPElement);
    OdfContentDom contentDocument = newDoc.getContentDom();
    OdfOfficeAutomaticStyles styles = contentDocument.getAutomaticStyles();
    OdfStyle style =
        styles.getStyle(((TextPElement) paragraphNode).getStyleName(), OdfStyleFamily.Paragraph);
    Assert.assertNotNull(style);
    Node paragraphPropertiesNode = style.getFirstChild();
    Assert.assertNotNull(paragraphPropertiesNode instanceof StyleParagraphPropertiesElement);
    Assert.assertEquals(
        ((StyleParagraphPropertiesElement) paragraphPropertiesNode).getFoBreakBeforeAttribute(),
        "page");
  }

  @Test
  public void testGetMediaTypeString() throws Exception {
    try {
      TextDocument tdoc =
          TextDocument.loadDocument(ResourceUtilities.getAbsolutePath(EMPTY_TEXT_DOCUMENT_PATH));
      Assert.assertNotNull(tdoc);

      OdfMediaType odfMedia = tdoc.getOdfMediaType();
      String mediaType = odfMedia.getMediaTypeString();
      Assert.assertEquals("application/vnd.oasis.opendocument.text", mediaType);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testNewTextDocument() throws Exception {
    try {
      TextDocument tdoc = TextDocument.newTextDocument(TextDocument.OdfMediaType.TEXT_MASTER);
      OdfMediaType odfMediaA = tdoc.getOdfMediaType();
      String filePath = ResourceUtilities.getAbsolutePath("");
      tdoc.save(filePath + "testNewTextDocument.odt");

      // validate
      TextDocument tdocument = TextDocument.loadDocument(filePath + "testNewTextDocument.odt");
      OdfMediaType odfMediaB = tdocument.getOdfMediaType();
      Assert.assertEquals(odfMediaA, odfMediaB);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testLoadDocumentResource() throws Exception {
    try {
      TextDocument tdocument =
          TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
      Assert.assertNotNull(tdocument);
      OdfMediaType odfMediaB = tdocument.getOdfMediaType();
      Assert.assertEquals(
          "application/vnd.oasis.opendocument.text", odfMediaB.getMediaTypeString());
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testLoadDocumentFile() throws Exception {
    try {
      String filePath = ResourceUtilities.getAbsolutePath("headerFooterHidden.odt");
      File file = new File(filePath);
      TextDocument tdocument = TextDocument.loadDocument(file);
      Assert.assertNotNull(tdocument);
      OdfMediaType odfMediaB = tdocument.getOdfMediaType();
      Assert.assertEquals(
          "application/vnd.oasis.opendocument.text", odfMediaB.getMediaTypeString());
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetTableContainerElement() throws Exception {
    try {
      String filePath = ResourceUtilities.getAbsolutePath("headerFooterHidden.odt");
      File file = new File(filePath);
      TextDocument tdocument = TextDocument.loadDocument(file);
      Assert.assertNotNull(tdocument);

      TableContainer tablecon = tdocument.getTableContainerImpl();
      OdfElement odfeleA = tablecon.getTableContainerElement();
      OdfElement odfeleB = tdocument.getTableContainerElement();
      Assert.assertEquals(odfeleA, odfeleB);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testAddText() throws Exception {
    try {
      String filePath = ResourceUtilities.getAbsolutePath("headerFooterHidden.odt");
      File file = new File(filePath);
      TextDocument tdocument = TextDocument.loadDocument(file);
      Assert.assertNotNull(tdocument);
      // Paragraph textParagraph1 = tdocument.addParagraph("Paragraph1");
      OdfTextParagraph textParagraph = tdocument.addText("text1");

      Assert.assertEquals("text1", textParagraph.getTextContent());
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testRemoveParagraph() throws Exception {
    try {
      String filePath = ResourceUtilities.getAbsolutePath("headerFooterHidden.odt");
      File file = new File(filePath);
      TextDocument tdoc = TextDocument.loadDocument(file);
      Assert.assertNotNull(tdoc);

      Paragraph para = tdoc.addParagraph("paragraph1");
      Assert.assertEquals("paragraph1", para.getTextContent());
      boolean flag = tdoc.removeParagraph(para);
      Document doc = para.getOwnerDocument();
      Assert.assertNotSame(doc, tdoc);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testDeclareVariable() throws Exception {
    try {
      TextDocument tdoc =
          TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
      Assert.assertNotNull(tdoc);

      VariableField vField = tdoc.declareVariable("variable1", VariableField.VariableType.SIMPLE);
      Assert.assertEquals("variable1", vField.getVariableName());
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetVariableFieldByName() throws Exception {

    try {
      TextDocument tdoc =
          TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
      Assert.assertNotNull(tdoc);

      VariableField vFieldA = tdoc.declareVariable("variable1", VariableField.VariableType.SIMPLE);
      vFieldA.getVariableName();
      System.out.println(vFieldA.getVariableName());
      Assert.assertEquals("variable1", vFieldA.getVariableName());
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testCreateChart() throws Exception {
    TextDocument tdoc =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
    String title = "title_name";
    String[] labels = {"hello", "hi", "odf"};
    String[] legends = {"hello1", "hi1", "odf1"};
    double[][] data = {{1.11, 43.23}, {3.22, 4.00, 5.43}, {121.99, 123.1, 423.00}};
    DataSet dataset = new DataSet(labels, legends, data);
    Rectangle rect = new Rectangle();
    Chart chart = tdoc.createChart(title, dataset, rect);
    chart.setChartType(ChartType.AREA);
    Assert.assertEquals(ChartType.AREA, chart.getChartType());
    Assert.assertEquals("title_name", chart.getChartTitle());
    Assert.assertEquals(dataset, chart.getChartData());

    // save
    tdoc.save(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
  }

  @Test
  public void testGetChartCount() throws Exception {
    TextDocument tdoc =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
    String title = "title_name";
    String[] labels = {"hello", "hi", "odf"};
    String[] legends = {"hello1", "hi1", "odf1"};
    double[][] data = {{1.11, 43.23}, {3.22, 4.00, 5.43}, {121.99, 123.1, 423.00}};
    DataSet dataset = new DataSet(labels, legends, data);
    Rectangle rect = new Rectangle();
    Chart chart = tdoc.createChart(title, dataset, rect);
    List chartA = tdoc.getChartByTitle("title_name");
    chart.setChartType(ChartType.AREA);
    Assert.assertEquals(ChartType.AREA, chart.getChartType());
    Assert.assertEquals("title_name", chart.getChartTitle());
    Assert.assertEquals(dataset, chart.getChartData());

    int count = tdoc.getChartCount();
    Assert.assertEquals(chartA.size(), count);

    // save
    // tdoc.save(ResourceUtilities.getAbsolutePath("headerFooterHidden.odt"));
  }

  @Test
  public void testinsertContentFromDocumentBefore() throws Exception {
    TextDocument src1 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    TextDocument src2 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    TextDocument target1 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    TextDocument target2 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    Paragraph p1 = target1.getParagraphByIndex(2, true);
    target1.insertContentFromDocumentBefore(src1, p1, true);
    Iterable<OdfStyle> pstyles1 =
        target1.getStylesDom().getOfficeStyles().getStylesForFamily(OdfStyleFamily.Paragraph);
    Iterator<OdfStyle> ite = pstyles1.iterator();
    int i = 0;
    System.out.println("Custom Styles in TestInsertDocumentWithStyleBefore.odt");
    while (ite.hasNext()) {
      OdfStyle odfStyle = ite.next();
      String sname = odfStyle.getAttribute("style:name");
      if (sname.startsWith("style")) {
        i++;
        System.out.println(sname);
      }
    }
    Assert.assertEquals(6, i);
    target1.save(ResourceUtilities.newTestOutputFile("TestInsertDocumentWithStyleBefore.odt"));

    target2 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    Paragraph p2 = target2.getParagraphByIndex(2, true);
    target2.insertContentFromDocumentBefore(src2, p2, false);

    Iterable<OdfStyle> pstyles2 =
        target2.getStylesDom().getOfficeStyles().getStylesForFamily(OdfStyleFamily.Paragraph);
    Iterator<OdfStyle> ite2 = pstyles2.iterator();
    i = 0;
    System.out.println("Custom Styles in TestInsertDocumentWithoutStyleBefore.odt");
    while (ite2.hasNext()) {
      OdfStyle odfStyle = ite2.next();
      String sname = odfStyle.getAttribute("style:name");
      if (sname.startsWith("style")) {
        i++;
        System.out.println(sname);
      }
    }
    Assert.assertEquals(3, i);
    target2.save(ResourceUtilities.newTestOutputFile("TestInsertDocumentWithoutStyleBefore.odt"));
  }

  @Test
  public void testinsertContentFromDocumentAfter() throws Exception {
    TextDocument src1 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    TextDocument src2 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    TextDocument target1 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    TextDocument target2 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    Paragraph p1 = target1.getParagraphByIndex(2, true);
    target1.insertContentFromDocumentAfter(src1, p1, true);
    Iterable<OdfStyle> pstyles1 =
        target1.getStylesDom().getOfficeStyles().getStylesForFamily(OdfStyleFamily.Paragraph);
    Iterator<OdfStyle> ite = pstyles1.iterator();
    int i = 0;
    System.out.println("Custom Styles in TestInsertDocumentWithStyleAfter.odt:");
    while (ite.hasNext()) {
      OdfStyle odfStyle = ite.next();
      String sname = odfStyle.getAttribute("style:name");
      if (sname.startsWith("style")) {
        i++;
        System.out.println(sname);
      }
    }
    Assert.assertEquals(6, i);
    target1.save(ResourceUtilities.newTestOutputFile("TestInsertDocumentWithStyleAfter.odt"));

    target2 =
        TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestInsertDocument.odt"));
    Paragraph p2 = target2.getParagraphByIndex(2, true);
    target2.insertContentFromDocumentAfter(src2, p2, false);

    Iterable<OdfStyle> pstyles2 =
        target2.getStylesDom().getOfficeStyles().getStylesForFamily(OdfStyleFamily.Paragraph);
    Iterator<OdfStyle> ite2 = pstyles2.iterator();
    i = 0;
    System.out.println("Custom Styles in TestInsertDocumentWithoutStyleAfter.odt:");
    while (ite2.hasNext()) {
      OdfStyle odfStyle = ite2.next();
      String sname = odfStyle.getAttribute("style:name");
      if (sname.startsWith("style")) {
        i++;
        System.out.println(sname);
      }
    }
    Assert.assertEquals(3, i);
    target2.save(ResourceUtilities.newTestOutputFile("TestInsertDocumentWithoutStyleAfter.odt"));
  }
}
