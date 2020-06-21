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
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderRightAttribute;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class TableCellPropertiesTest {

  private static final Logger LOGGER = Logger.getLogger(TableCellPropertiesTest.class.getName());

  @Test
  public void testSetBorders() {
    try {

      Border borderbase = new Border(Color.LIME, 1.0701, 0.0208, 0.0346, SupportedLinearMeasure.CM);
      borderbase.setLineStyle(StyleTypeDefinitions.LineType.SINGLE);
      SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = doc.getTableByName("Sheet1");

      Cell cell = table.getCellByPosition(2, 2);
      cell.setBorders(CellBordersType.BOTTOM, borderbase);
      cell.setBorders(CellBordersType.RIGHT, borderbase);
      cell.setBorders(CellBordersType.DIAGONALTLBR, borderbase);

      // verification
      Border base = cell.getBorder(CellBordersType.BOTTOM);
      base = cell.getBorder(CellBordersType.RIGHT);
      base = cell.getBorder(CellBordersType.DIAGONALTLBR);
      Assert.assertEquals(borderbase, base);
      Assert.assertEquals(StyleTypeDefinitions.LineType.SINGLE, borderbase.getLineStyle());

      // save
      doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSetBorders_NONE() {
    try {

      Border borderbase = new Border(Color.LIME, 3.0701, 0.0208, 0.0346, SupportedLinearMeasure.CM);
      borderbase.setLineStyle(StyleTypeDefinitions.LineType.SINGLE);
      SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = doc.getTableByName("Sheet1");

      Cell cell = table.getCellByPosition(0, 0);
      // cell.setBorders(CellBordersType.DIAGONAL_LINES, borderbase);
      cell.setBorders(CellBordersType.RIGHT, borderbase);

      // validate
      TableCellProperties styleCell = cell.getStyleHandler().getTableCellPropertiesForWrite();
      NamedNodeMap attr = styleCell.mElement.getAttributes();
      Node nod = attr.getNamedItem(FoBorderRightAttribute.ATTRIBUTE_NAME.getQName());
      Assert.assertEquals("3.0701cm solid #00ff00", nod.getNodeValue());

      cell.setBorders(CellBordersType.NONE, borderbase);

      TableCellProperties styleCell1 = cell.getStyleHandler().getTableCellPropertiesForWrite();
      NamedNodeMap attr1 = styleCell1.mElement.getAttributes();
      Node nod1 = attr1.getNamedItem(FoBorderRightAttribute.ATTRIBUTE_NAME.getQName());
      Assert.assertNull(nod1);

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSetBorders_DIAGONAL_LINES() {
    try {

      Border borderbase = new Border(Color.LIME, 3.0701, 0.0208, 0.0346, SupportedLinearMeasure.CM);
      borderbase.setLineStyle(StyleTypeDefinitions.LineType.SINGLE);
      SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = doc.getTableByName("Sheet1");

      Cell cell = table.getCellByPosition(0, 0);
      cell.setBorders(CellBordersType.DIAGONAL_LINES, borderbase);

      // validate
      TableCellProperties styleCell = cell.getStyleHandler().getTableCellPropertiesForWrite();
      NamedNodeMap attr = styleCell.mElement.getAttributes();

      Node nod = attr.getNamedItem("style:diagonal-bl-tr");
      Assert.assertEquals("3.0701cm solid #00ff00", nod.getNodeValue());

      // save
      // doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetBorder() {
    try {
      Border borderbase = new Border(Color.LIME, 3.0701, 0.0208, 0.0346, SupportedLinearMeasure.CM);
      borderbase.setLineStyle(StyleTypeDefinitions.LineType.SINGLE);
      SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = doc.getTableByName("Sheet1");

      Cell cell = table.getCellByPosition(2, 2);
      cell.setBorders(CellBordersType.ALL_FOUR, borderbase);

      // validate
      TableCellProperties styleCell = cell.getStyleHandler().getTableCellPropertiesForWrite();
      Border bor = styleCell.getBorder();
      Assert.assertEquals(borderbase, bor);

      // save
      doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetLeftBorder() {
    try {
      Border borderbase = new Border(Color.LIME, 3.0701, 0.0208, 0.0346, SupportedLinearMeasure.CM);
      borderbase.setLineStyle(StyleTypeDefinitions.LineType.DOUBLE);
      SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
      Table table = doc.getTableByName("Sheet1");

      Cell cell = table.getCellByPosition(2, 2);
      cell.setBorders(CellBordersType.RIGHT, borderbase);

      // validate
      TableCellProperties styleCell = cell.getStyleHandler().getTableCellPropertiesForWrite();
      Border bor = styleCell.getRightBorder();
      Assert.assertEquals(borderbase, bor);

      // save
      doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testSettingNullBackgroundOnProperties() throws Exception {
    SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
    Table table = doc.getTableByName("Sheet1");
    Cell cell = table.getCellByPosition(1, 1);
    cell.setCellBackgroundColor(Color.BLACK);
    // setting null resets the element, see ODFTOOLKIT-326
    cell.getStyleHandler().getTableCellPropertiesForWrite().setBackgroundColor(null);
    Assert.assertNull(cell.getStyleHandler().getTableCellPropertiesForRead().getBackgroundColor());
    // defaulting to white when color is null
    Assert.assertEquals(Color.WHITE, cell.getCellBackgroundColor());
    Cell newCell = table.appendRow().getCellByIndex(1);
    Assert.assertNull(
        newCell.getStyleHandler().getTableCellPropertiesForRead().getBackgroundColor());
    Assert.assertEquals(Color.WHITE, newCell.getCellBackgroundColor());
  }

  @Test
  public void testSettingBlackBackgroundOnProperties() throws Exception {
    SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
    Table table = doc.getTableByName("Sheet1");
    Cell cell = table.getCellByPosition(1, 1);
    cell.setCellBackgroundColor(Color.BLUE);
    cell.getStyleHandler().getTableCellPropertiesForWrite().setBackgroundColor(Color.BLACK);
    Assert.assertEquals(
        Color.BLACK, cell.getStyleHandler().getTableCellPropertiesForRead().getBackgroundColor());
    Assert.assertEquals(Color.BLACK, cell.getCellBackgroundColor());
    Cell newCell = table.appendRow().getCellByIndex(1);
    Assert.assertEquals(Color.BLACK, newCell.getStyleHandler().getBackgroundColor());
  }

  @Test
  public void testSettingNullBackgroundOnCell() throws Exception {
    SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
    Table table = doc.getTableByName("Sheet1");
    Cell cell = table.getCellByPosition(1, 1);
    cell.setCellBackgroundColor((Color) null);
    Assert.assertNull(cell.getStyleHandler().getTableCellPropertiesForRead().getBackgroundColor());
    Assert.assertEquals(Color.WHITE, cell.getCellBackgroundColor());
  }
}
