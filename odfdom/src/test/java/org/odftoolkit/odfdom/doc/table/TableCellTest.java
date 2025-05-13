/**
 * **********************************************************************
 *
 * <p>Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.doc.table;

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class TableCellTest {

  static final String SAMPLE_SPREADSHEET = "TestSpreadsheetTable";
  static final String SAMPLE_STYLE_SPREADSHEET = "TestSpreadsheetStyleTable";
  static final String SAMPLE_TEXT = "TestTextTable";
  OdfSpreadsheetDocument odsstyle;

  private OdfSpreadsheetDocument loadInputOds() throws Exception {
    return OdfSpreadsheetDocument.loadDocument(
      ResourceUtilities.getAbsoluteInputPath(SAMPLE_SPREADSHEET + ".ods")
    );
  }

  private void saveOutputOds(OdfSpreadsheetDocument odsdoc) throws Exception {
    odsdoc.save(ResourceUtilities.getTestOutputFile(SAMPLE_SPREADSHEET + "Output.ods"));
  }

  private OdfSpreadsheetDocument loadOutputOds() throws Exception {
    return OdfSpreadsheetDocument.loadDocument(
      ResourceUtilities.getTestOutputFile(SAMPLE_SPREADSHEET + "Output.ods")
    );
  }

  private OdfTextDocument loadInputOdt() throws Exception {
    return OdfTextDocument.loadDocument(
      ResourceUtilities.getAbsoluteInputPath(SAMPLE_TEXT + ".odt")
    );
  }

  private void saveOutputOdt(OdfTextDocument odsdoc) throws Exception {
    odsdoc.save(ResourceUtilities.getTestOutputFile(SAMPLE_TEXT + "Output.odt"));
  }

  private OdfTextDocument loadOutputOdt() throws Exception {
    return OdfTextDocument.loadDocument(
      ResourceUtilities.getTestOutputFile(SAMPLE_TEXT + "Output.odt")
    );
  }

  @Test
  public void testGetIndexInRowColumn() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 2, columnindex = 1;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell cell = table.getCellByPosition(columnindex, rowindex);

    Assert.assertEquals(rowindex, cell.getRowIndex());
    Assert.assertEquals(columnindex, cell.getColumnIndex());

    OdfTextDocument odtdoc = loadInputOdt();
    table = odtdoc.getTableByName("Table3");
    OdfTableCell cell1 = table.getCellByPosition(0, 1);
    Assert.assertEquals(1, cell1.getRowIndex());
    Assert.assertEquals(0, cell1.getColumnIndex());
  }

  @Test
  public void testGetSetHoriAlignment() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 3, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    String align = fcell.getHorizontalAlignment();
    Assert.assertEquals("center", align);

    fcell.setHorizontalAlignment(null);
    String newAlign = fcell.getHorizontalAlignment();
    Assert.assertEquals(null, newAlign);

    fcell.setHorizontalAlignment("start");
    align = fcell.getHorizontalAlignment();
    Assert.assertEquals("start", align);

    // "left" and "right" should be mapped as "start" and "end".
    fcell.setHorizontalAlignment("left");
    align = fcell.getHorizontalAlignment();
    Assert.assertEquals("start", align);
    fcell.setHorizontalAlignment("right");
    align = fcell.getHorizontalAlignment();
    Assert.assertEquals("end", align);
    saveOutputOds(odsdoc);

    OdfSpreadsheetDocument ods;
    ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable tbl = ods.getTableByName("Sheet1");
    OdfTableCell cell = tbl.getCellByPosition(0, 0);
    String horizonAlignment = cell.getHorizontalAlignment();
    Assert.assertEquals(null, horizonAlignment);
  }

  @Test
  public void testGetSetVertAlignment() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 3, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    String align = fcell.getVerticalAlignment();
    Assert.assertEquals("top", align);

    fcell.setVerticalAlignment(null);
    String newAlign = fcell.getVerticalAlignment();
    Assert.assertEquals(null, newAlign);

    fcell.setVerticalAlignment("bottom");
    align = fcell.getVerticalAlignment();
    Assert.assertEquals("bottom", align);
    saveOutputOds(odsdoc);
    OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable tbl = ods.getTableByName("Sheet1");
    OdfTableCell cell = tbl.getCellByPosition(0, 0);
    String verticalAlignment = cell.getVerticalAlignment();
    Assert.assertEquals(null, verticalAlignment);
  }

  @Test
  public void testGetSetValueType() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 3, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertThrows("type shouldn't be null.", IllegalArgumentException.class, () -> fcell.setValueType(null));

    fcell.setValueType("date");
    String valueType = fcell.getValueType();
    Assert.assertEquals("date", valueType);
    saveOutputOds(odsdoc);

    OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable tbl = ods.getTableByName("Sheet1");
    OdfTableCell cell = tbl.getCellByPosition(0, 0);
    valueType = cell.getValueType();
    Assert.assertEquals(null, valueType);
  }

  @Test
  public void testGetSetWrapOption() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 8;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    boolean wrap = fcell.isTextWrapped();
    Assert.assertEquals(true, wrap);

    fcell.setTextWrapped(false);
    wrap = fcell.isTextWrapped();
    Assert.assertEquals(false, wrap);
    saveOutputOds(odsdoc);
  }

  @Test
  public void testGetSetTextValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 8;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    String text = fcell.getDisplayText();
    Assert.assertEquals("this is a big cell with a big table", text);

    fcell.setDisplayText("changed");
    text = fcell.getDisplayText();
    Assert.assertEquals("changed", text);
    // reproduce bug 150.
    fcell.setDisplayText(null);
    text = fcell.getDisplayText();
    Assert.assertEquals("", text);
    fcell.setDisplayText(null, "automatic7777");
    text = fcell.getDisplayText();
    Assert.assertEquals("", text);
    saveOutputOds(odsdoc);

    OdfTextDocument odtdoc = loadInputOdt();
    OdfTable table1 = odtdoc.getTableByName("Table1");
    OdfTableCell fcell2 = table1.getCellByPosition(0, 1);
    text = fcell2.getDisplayText();
    Assert.assertEquals("Aabbccddee", text);
  }

  @Test @Ignore // FIXME test failure: Expected: #0.0 Actual: 0.0
  public void testSetGetFormat() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 3, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    fcell.setFormatString("#0.0");
    String displayvalue = fcell.getDisplayText();
    Assert.assertEquals(
        "300" + (new DecimalFormatSymbols()).getDecimalSeparator() + "0", displayvalue);
    String format = fcell.getFormatString();
    Assert.assertEquals("#0.0", format);

    OdfTableCell dcell = table.getCellByPosition(3, 2);
    format = dcell.getFormatString();
    Assert.assertEquals("MMM d, yy", format);

    dcell.setFormatString("yyyy-MM-dd");
    displayvalue = dcell.getDisplayText();
    Assert.assertEquals("2008-12-23", displayvalue);

    OdfTableCell pcell = table.getCellByPosition("B2");
    format = pcell.getFormatString();
    Assert.assertEquals("#0%", format);

    pcell.setFormatString("#0.00%");
    displayvalue = pcell.getDisplayText();
    Assert.assertEquals(
        "200" + (new DecimalFormatSymbols()).getDecimalSeparator() + "00%", displayvalue);

    OdfTableRow tablerow = table.getRowByIndex(6);
    OdfTableCell cell = tablerow.getCellByIndex(3);
    Calendar currenttime = Calendar.getInstance();
    cell.setDateValue(currenttime);
    cell.setFormatString("yyyy-MM-dd");
    tablerow = table.getRowByIndex(7);
    cell = tablerow.getCellByIndex(3);
    cell.setTimeValue(currenttime);
    cell.setFormatString("HH:mm:ss");

    saveOutputOds(odsdoc);

    // test value type adapt function.
    OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable tbl = ods.getTableByName("Sheet1");
    for (int i = 1; i <= 10; i++) {
      cell = tbl.getCellByPosition("A" + i);
      cell.setDoubleValue(Double.valueOf(i));
    }
    cell = tbl.getCellByPosition("A11");
    cell.setFormula("=sum(A1:A10)");
    // contains '#' should be adapted as float.
    cell.setFormatString("#,###");
    Assert.assertEquals("float", cell.getValueType());
    cell = tbl.getCellByPosition("A12");
    cell.setFormula("=sum(A1:A10)");
    // contains '0' should be adapted as float.
    cell.setFormatString("0.00");
    Assert.assertEquals("float", cell.getValueType());

    ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    tbl = ods.getTableByName("Sheet1");
    for (int i = 1; i <= 10; i++) {
      cell = tbl.getCellByPosition("A" + i);
      cell.setPercentageValue(0.1);
    }
    cell = tbl.getCellByPosition("A11");
    cell.setFormula("=sum(A1:A10)");
    // contains '%'should be adapted as percentage.
    cell.setFormatString("###.0%");
    Assert.assertEquals("percentage", cell.getValueType());

    ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    tbl = ods.getTableByName("Sheet1");
    for (int i = 1; i <= 10; i++) {
      cell = tbl.getCellByPosition("A" + i);
      cell.setDateValue(Calendar.getInstance());
      cell.setFormatString("yyyy.MM.dd");
    }
    cell = tbl.getCellByPosition("A11");
    cell.setFormula("=max(A1:A10)");
    // contains 'y' 'M' 'd' should be adapted as date.
    cell.setFormatString("yyyy.MM.dd");
    Assert.assertEquals("date", cell.getValueType());

    ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    tbl = ods.getTableByName("Sheet1");
    for (int i = 1; i <= 10; i++) {
      cell = tbl.getCellByPosition("A" + i);
      cell.setTimeValue(Calendar.getInstance());
      cell.setFormatString("yyyy.MM.dd HH:mm:ss");
    }
    cell = tbl.getCellByPosition("A11");
    cell.setFormula("=max(A1:A10)");
    // contains 'H' 'm' 's' should be adapted as time.
    cell.setFormatString("yyyy.MM.dd HH:mm:ss");
    Assert.assertEquals("time", cell.getValueType());
    cell = tbl.getCellByPosition("A12");
    cell.setFormula("=max(A1:A10)");
    // contains 'H' 'm' 's' should be adapted as time.
    cell.setFormatString("HH:mm:ss");
    Assert.assertEquals("time", cell.getValueType());
  }

  @Test
  public void testGetSetCellBackgroundColor() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 2, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    fcell.setCellBackgroundColor("#ffffff");
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set color as DEFAULT_BACKGROUND_COLOR #FFFFFF
    Assert.assertEquals("#ffffff", fcell.getCellBackgroundColorString());

    Color expectedColor = Color.valueOf("#000000");
    fcell.setCellBackgroundColor(expectedColor);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expectedColor.toString(), fcell.getCellBackgroundColor().toString());

    OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable tbl = ods.getTableByName("Sheet1");
    OdfTableCell cell = tbl.getCellByPosition(0, 0);
    Color actualBackColor = cell.getCellBackgroundColor();
    Assert.assertEquals("#ffffff", actualBackColor.toString());
  }

  @Test
  public void testGetSetColumnSpannedNumber() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 2, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    fcell.setColumnSpannedNumber(-2);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set column spanned number as DEFAULT_COLUMN_SPANNED_NUMBER 1.
    Assert.assertEquals(1, fcell.getColumnSpannedNumber());

    fcell.setColumnSpannedNumber(0);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set column spanned number as DEFAULT_COLUMN_SPANNED_NUMBER 1.
    Assert.assertEquals(1, fcell.getColumnSpannedNumber());

    fcell.setColumnSpannedNumber(2);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(2, fcell.getColumnSpannedNumber());
  }

  @Test
  public void testGetSetRowSpannedNumber() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 2, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    fcell.setRowSpannedNumber(-2);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set row spanned number as DEFAULT_ROW_SPANNED_NUMBER 1.
    Assert.assertEquals(1, fcell.getRowSpannedNumber());

    fcell.setRowSpannedNumber(0);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set row spanned number as DEFAULT_ROW_SPANNED_NUMBER 1.
    Assert.assertEquals(1, fcell.getRowSpannedNumber());

    fcell.setRowSpannedNumber(2);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(2, fcell.getRowSpannedNumber());
  }

  @Test
  public void testGetSetColumnsRepeatedNumber() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 3, columnindex = 1;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    fcell.setColumnsRepeatedNumber(-2);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set columns repeated number as DEFAULT_COLUMNS_REPEATED_NUMBER 1.
    Assert.assertEquals(1, fcell.getColumnsRepeatedNumber());

    fcell.setColumnsRepeatedNumber(0);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    // set columns repeated number as DEFAULT_COLUMNS_REPEATED_NUMBER 1.
    Assert.assertEquals(1, fcell.getColumnsRepeatedNumber());

    fcell.setColumnsRepeatedNumber(2);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(2, fcell.getColumnsRepeatedNumber());
  }

  @Test
  public void testGetSetDateValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 7, columnindex = 7;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    boolean illegalArgumentFlag = false;
    try {
      fcell.setDateValue(null);
    } catch (IllegalArgumentException ie) {
      if ("date shouldn't be null.".equals(ie.getMessage())) {
        illegalArgumentFlag = true;
      }
    }
    Assert.assertTrue(illegalArgumentFlag);
    Calendar expectedCalendar = new GregorianCalendar(2010, 1, 30);
    fcell.setDateValue(expectedCalendar);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(0, fcell.getDateValue().compareTo(expectedCalendar));
  }

  @Test
  public void testGetSetStringValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 6, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    fcell.setStringValue(null);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals("", fcell.getStringValue());

    String expectedString = "hello world";
    fcell.setStringValue(expectedString);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expectedString, fcell.getStringValue());
  }

  @Test
  public void testGetSetBooleanValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 5;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    boolean expected = false;
    fcell.setBooleanValue(expected);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertFalse(fcell.getBooleanValue());
  }

  @Test
  public void testGetSetCurrencyValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 5;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    fcell.setValueType("currency");
    fcell.setCurrencyFormat("$", "#,##0.00");
    Double actualValue = fcell.getCurrencyValue();
    Assert.assertNull(actualValue);

    double expected = 100.00;
    OdfTableCell finalFcell = fcell;
    Assert.assertThrows("currency shouldn't be null.", IllegalArgumentException.class, () -> finalFcell.setCurrencyValue(expected, null));

    fcell.setCurrencyValue(expected, "USD");
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expected, fcell.getCurrencyValue(), 0);
  }

  @Test
  public void testGetSetCurrencyDesc() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 1, columnindex = 2;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    boolean illegalArgumentFlag = false;
    OdfTableCell finalFcell = fcell;
    Assert.assertThrows("Currency code of cell should not be null.", IllegalArgumentException.class, () -> finalFcell.setCurrencyCode(null));

    fcell = table.getCellByPosition(columnindex, rowindex);
    String expected = "USD";
    fcell.setCurrencyCode(expected);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expected, fcell.getCurrencyCode());
  }

  @Test
  public void testGetSetPercentageValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 5;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    double expected = 56.98;
    fcell.setPercentageValue(expected);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expected, fcell.getPercentageValue(), 0);
  }

  @Test
  public void testGetSetTimeValue() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 0, columnindex = 4;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    boolean illegalArgumentFlag = false;
    OdfTableCell finalFcell = fcell;
    Assert.assertThrows("time shouldn't be null.", IllegalArgumentException.class, () -> finalFcell.setTimeValue(null));

    Calendar expected = Calendar.getInstance();
    fcell.setTimeValue(expected);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);

    SimpleDateFormat simpleFormat = new SimpleDateFormat("'PT'HH'H'mm'M'ss'S'");
    String expectedString = simpleFormat.format(expected.getTime());
    String targetString = simpleFormat.format(fcell.getTimeValue().getTime());
    Assert.assertEquals(expectedString, targetString);
  }

  @Test
  public void testGetSetFormula() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 1, columnindex = 10;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    OdfTableCell finalFcell = fcell;
    Assert.assertThrows("formula shouldn't be null.", IllegalArgumentException.class, () -> finalFcell.setFormula(null));

    String expected = "of:=[.I2]*4";
    fcell.setFormula(expected);
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expected, fcell.getFormula());
  }

  /**
   * This test case is used to check whether the new created cell uses correct style settings.
   * <p>
   * ODFDOM allows users to set if cell styles are inherited or not whenever a new cell is added to
   * the table. The default setting is using inheritance. In this condition, the style of new column
   * is same with the last column before the inserted position, while the style of new row is same
   * with the last row before the inserted position.
   * <p>
   * This feature setting will influence <code>appendRow()</code>, <code>appendColumn()</code>,
   * <code>appendRows()</code>, <code>appendColumns()</code>, <code>insertRowsBefore()</code> and
   * <code>insertColumnsBefore()</code>. In default setting condition, the style name of new created
   * cells after these methods called should be "ce1" which is inherited from preceding cell.
   * But after setting cell style inheritance false, these new created cells' style name should be
   * "Default", which is not inherited from preceding one.
   * <p>
   * For <code>getCellByPosition()</code>, <code>getCellRangeByPosition()</code>, <code>
   * getCellRangeByName()</code>, <code>getRowByIndex()</code> and <code>getColumnByIndex()</code>,
   * if need automatically expand cells, it will return empty cell(s) without any style settings.
   * Inheritance setting have no effect on them, so for cells which created after these methods are
   * called, should have "Default" style name.
   */
  @Test
  public void testGetStyleName() throws Exception {
    odsstyle =
        (OdfSpreadsheetDocument)
            OdfSpreadsheetDocument.loadDocument(
                ResourceUtilities.getAbsoluteInputPath(SAMPLE_STYLE_SPREADSHEET + ".ods"));
    int rowindex = 1, columnindex = 0;
    OdfTable table = odsstyle.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    String expected = "ce1";
    Assert.assertEquals(expected, fcell.getStyleName());
    // the default setting is inherited, so for new row,
    // the cell style name should be "ce1".
    // test appendColumn
    table.appendColumn();
    int columnCount = table.getColumnCount();
    fcell = table.getCellByPosition(columnCount - 1, rowindex);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test appendRow
    table.appendRow();
    int rowCount = table.getRowCount();
    fcell = table.getCellByPosition(columnindex, rowCount - 1);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test insertRowsBefore
    table.insertRowsBefore(rowindex + 1, 1);
    fcell = table.getCellByPosition(columnindex, rowindex + 1);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test insertColumnsBefore
    table.insertColumnsBefore(columnindex + 1, 1);
    fcell = table.getCellByPosition(columnindex + 1, rowindex);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test appendColumns
    table.appendColumns(2);
    columnCount = table.getColumnCount();
    fcell = table.getCellByPosition(columnCount - 1, rowindex);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test appendRows
    table.appendRows(2);
    rowCount = table.getRowCount();
    fcell = table.getCellByPosition(columnindex, rowCount - 1);
    Assert.assertEquals(expected, fcell.getStyleName());
    // for getCellByPosition the return cell style should be "Default".
    fcell = table.getCellByPosition(table.getColumnCount() + 1, table.getRowCount() + 1);
    Assert.assertEquals("Default", fcell.getStyleName());
    odsstyle.close();

    // change setting is not inherited, so for new row,
    // the cell style name should be "Default".
    odsstyle =
        (OdfSpreadsheetDocument)
            OdfSpreadsheetDocument.loadDocument(
                ResourceUtilities.getAbsoluteInputPath(SAMPLE_STYLE_SPREADSHEET + ".ods"));
    rowindex = 1;
    columnindex = 0;
    table = odsstyle.getTableByName("Sheet1");
    table.setCellStyleInheritance(false);
    expected = "Default";
    table.appendColumn();
    columnCount = table.getColumnCount();
    fcell = table.getCellByPosition(columnCount - 1, rowindex);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test appendRow
    table.appendRow();
    rowCount = table.getRowCount();
    fcell = table.getCellByPosition(columnindex, rowCount - 1);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test insertRowsBefore
    table.insertRowsBefore(rowindex + 1, 1);
    fcell = table.getCellByPosition(columnindex, rowindex + 1);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test insertColumnsBefore
    table.insertColumnsBefore(columnindex + 1, 1);
    fcell = table.getCellByPosition(columnindex + 1, rowindex);
    // Assert.assertEquals(expected, fcell.getStyleName());
    // test appendColumns
    table.appendColumns(2);
    columnCount = table.getColumnCount();
    fcell = table.getCellByPosition(columnCount - 1, rowindex);
    Assert.assertEquals(expected, fcell.getStyleName());
    // test appendRows
    table.appendRows(2);
    rowCount = table.getRowCount();
    fcell = table.getCellByPosition(columnindex, rowCount - 1);
    Assert.assertEquals(expected, fcell.getStyleName());
    // for getCellByPosition the return cell style should be "Default".
    fcell = table.getCellByPosition(table.getColumnCount(), table.getRowCount());
    Assert.assertEquals("Default", fcell.getStyleName());
    odsstyle.close();
  }

  @Test
  public void testGetTableColumn() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 2, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    Assert.assertNotNull(fcell.getTableColumn());
    Assert.assertEquals(columnindex, fcell.getTableColumn().getColumnIndex());
  }

  @Test
  public void testGetTableRow() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 2, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    Assert.assertNotNull(fcell.getTableRow());
    Assert.assertEquals(rowindex, fcell.getTableRow().getRowIndex());
  }

  @Test
  public void testRemoveContent() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 8;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    Assert.assertTrue(fcell.mCellElement.getChildNodes().getLength() > 0);
    fcell.removeContent();
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(0, fcell.mCellElement.getChildNodes().getLength());
  }

  @Test
  public void testRemoveTextContent() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 8;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);

    // how to test?
    fcell.removeContent();
    Assert.assertEquals(0, fcell.mCellElement.getChildNodes().getLength());

    fcell.setDisplayText("hello");
    DrawFrameElement drawEle = new DrawFrameElement(odsdoc.getContentDom());
    DrawImageElement imageEle = drawEle.newDrawImageElement();
    fcell.mCellElement.appendChild(drawEle);

    Assert.assertEquals(2, fcell.mCellElement.getChildNodes().getLength());

    fcell.removeTextContent();
    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(1, fcell.mCellElement.getChildNodes().getLength());
  }

  @Test
  public void testGetSetDisplayText() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 5, columnindex = 5;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    String expected = "display text";

    fcell.setDisplayText(expected);
    Assert.assertEquals(expected, fcell.getDisplayText());

    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expected, fcell.getDisplayText());
  }

  @Test @Ignore // FIXME test failure: Expected: #0.0 Actual: 0.0
  public void testGetSetFormatString() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    int rowindex = 3, columnindex = 0;
    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell fcell = table.getCellByPosition(columnindex, rowindex);
    OdfTableCell finalFcell = fcell;
    Assert.assertThrows("format string shouldn't be null.", IllegalArgumentException.class, () -> finalFcell.setFormatString(null));

    // float format string
    String expected = "#0.0";
    fcell.setFormatString(expected);
    // date format string
    // String expected="MMM d, yy";
    // String expected="yyyy-MM-dd";

    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    fcell = table.getCellByPosition(columnindex, rowindex);
    Assert.assertEquals(expected, fcell.getFormatString());
  }

  @Test
  public void testGetCurrencySymbol() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    OdfTable table = odsdoc.getTableByName("Sheet1");
    OdfTableCell cell1 = table.getCellByPosition("C2");
    Assert.assertEquals("$", cell1.getCurrencySymbol());
    OdfTableCell cell2 = table.getCellByPosition("C3");
    Assert.assertEquals("CNY", cell2.getCurrencySymbol());
  }

  @Test  @Ignore // FIXME test failure: Expected: $#,##0.00 Actual: [$$]#,##0.00
  public void testGetSetCurrencyFormat() throws Exception {
    OdfSpreadsheetDocument odsdoc = loadInputOds();

    OdfTable table = odsdoc.getTableByName("Sheet1");
    String[] formats = {"$#,##0.00", "#,##0.00 CNY", "$#,##0.0"};

    OdfTableCell cell = table.getCellByPosition("J1");
    OdfTableCell finalCell = cell;
    Assert.assertThrows("currency format shouldn't be null.", IllegalArgumentException.class, () -> finalCell.setCurrencyFormat(null, formats[0]));
    Assert.assertThrows("format shouldn't be null.", IllegalArgumentException.class, () -> finalCell.setCurrencyFormat("$", null));

    cell.setCurrencyValue(32.12, "USD");
    cell.setCurrencyFormat("$", formats[0]);

    cell = table.getCellByPosition("J2");
    cell.setCurrencyValue(Double.valueOf(32), "CNY");
    cell.setCurrencyFormat("CNY", formats[1]);

    cell = table.getCellByPosition("J3");
    cell.setCurrencyValue(-32.12, "USD");
    cell.setCurrencyFormat("$", formats[2]);

    saveOutputOds(odsdoc);
    // reload
    odsdoc = loadOutputOds();
    table = odsdoc.getTableByName("Sheet1");
    for (int i = 1; i <= 3; i++) {
      OdfTableCell newcell = table.getCellByPosition("J" + i);
      Assert.assertEquals(formats[i - 1], newcell.getFormatString());
    }
  }

  @Test @Ignore // FIXME test failure: Expected: yyyy-MM-dd Actual: YYYY-MM-DD
  public void testSetDefaultCellStyle() throws Exception {
    OdfSpreadsheetDocument outputDocument;
    OdfContentDom contentDom; // the document object model for content.xml
    OdfStylesDom stylesDom; // the document object model for styles.xml
    // the office:automatic-styles element in content.xml
    OdfOfficeAutomaticStyles contentAutoStyles;
    // the office:styles element in styles.xml
    OdfOfficeStyles stylesOfficeStyles;
    OdfStyle style;
    String noaaDateStyleName;
    String noaaTempStyleName;

    outputDocument = OdfSpreadsheetDocument.newSpreadsheetDocument();
    contentDom = outputDocument.getContentDom();
    contentAutoStyles = contentDom.getOrCreateAutomaticStyles();

    OdfNumberDateStyle dateStyle =
        new OdfNumberDateStyle(contentDom, "yyyy-MM-dd", "numberDateStyle", null);
    OdfNumberStyle numberStyle =
        new OdfNumberStyle(contentDom, "#0.00", "numberTemperatureStyle");

    contentAutoStyles.appendChild(dateStyle);
    contentAutoStyles.appendChild(numberStyle);

    style = contentAutoStyles.newStyle(OdfStyleFamily.TableCell);
    noaaDateStyleName = style.getStyleNameAttribute();
    style.setStyleDataStyleNameAttribute("numberDateStyle");

    // and for time cells
    style = contentAutoStyles.newStyle(OdfStyleFamily.TableCell);
    noaaTempStyleName = style.getStyleNameAttribute();
    style.setStyleDataStyleNameAttribute("numberTemperatureStyle");
    style.setProperty(StyleParagraphPropertiesElement.TextAlign, "end");

    OdfTable table = OdfTable.newTable(outputDocument);
    List<OdfTableColumn> columns = table.insertColumnsBefore(0, 3);
    OdfTableColumn column = columns.get(0);
    column.setDefaultCellStyle(
        contentAutoStyles.getStyle(noaaDateStyleName, OdfStyleFamily.TableCell));
    OdfTableCell aCell = column.getCellByIndex(0);
    aCell.setValueType("date");
    String format = aCell.getFormatString();
    Assert.assertEquals("yyyy-MM-dd", format);

    List<OdfTableRow> rows = table.insertRowsBefore(0, 1);
    OdfTableRow row = rows.get(0);
    row.setDefaultCellStyle(
        contentAutoStyles.getStyle(noaaTempStyleName, OdfStyleFamily.TableCell));
    OdfTableCell bCell = row.getCellByIndex(0);
    bCell.setValueType("float");
    String bformat = bCell.getFormatString();
    Assert.assertEquals("#0.00", bformat);
    Assert.assertEquals("end", bCell.getHorizontalAlignment());
  }

  @Test
  public void testGetFromEmptyDateValue() throws Exception {
    OdfSpreadsheetDocument doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable table = OdfTable.newTable(doc);
    OdfTableCell dateCell = table.appendRow().getCellByIndex(0);
    dateCell.setValueType(OfficeValueTypeAttribute.Value.DATE.toString());
    Assert.assertNull(dateCell.getDateValue());
  }

  @Test @Ignore // FIXME test failure: NPE
  public void testGetFromEmptyTimeValue() throws Exception {
    OdfSpreadsheetDocument doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
    OdfTable table = OdfTable.newTable(doc);
    OdfTableCell timeCell = table.appendRow().getCellByIndex(0);
    timeCell.setValueType(OfficeValueTypeAttribute.Value.TIME.toString());
    Assert.assertNull(timeCell.getTimeValue());
  }
}
