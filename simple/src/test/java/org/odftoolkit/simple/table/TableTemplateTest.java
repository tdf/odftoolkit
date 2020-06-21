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

package org.odftoolkit.simple.table;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class TableTemplateTest {

  private static final String TEST_TABLE_FILE_NAME = "TestTableTemplate.odt";
  private static final String TEST_TEMPLATE_FILE_NAME = "TableTemplate.odt";

  Document doc;

  @Before
  public void loadTestDocument() {
    try {
      doc =
          TextDocument.loadDocument(
              ResourceUtilities.getTestResourceAsStream(TEST_TABLE_FILE_NAME));

    } catch (Exception e) {
      Logger.getLogger(CellStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testLoadTableTemplates() {
    try {

      TableTemplate template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME),
              "ColumnStyledTable");
      checkLoadTemplateResults(template, "ColumnStyledTable");

      template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME), "RowStyledTable");
      checkLoadTemplateResults(template, "RowStyledTable");

      try {
        template =
            doc.LoadTableTemplateFromForeignTable(
                ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME), "TableR4C3");
        Assert.fail("Fail to load this template.");
      } catch (IllegalStateException e) {
        Assert.assertTrue(true);
      }

    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testApplyColumnStyledTableTemplate() {
    try {
      TableTemplate template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME),
              "ColumnStyledTable");

      Table table = doc.getTableByName("DataTable1");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_4*4");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_4*3");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_4*2");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_4*1");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_1*3");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_1*2");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      table = doc.getTableByName("Table_1*1");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      // apply to empty table
      table = doc.addTable(8, 8);
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, false);

      doc.save(ResourceUtilities.newTestOutputFile("TestApplyColumnStyledTableTemplate.odt"));
    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testApplyRowStyledTableTemplate() {
    try {
      TableTemplate template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME), "RowStyledTable");

      Table table = doc.getTableByName("DataTable1");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      table = doc.getTableByName("Table_3*4");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      table = doc.getTableByName("Table_2*4");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      table = doc.getTableByName("Table_1*4");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      table = doc.getTableByName("Table2_1*1");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      table = doc.getTableByName("Table2_1*2");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      table = doc.getTableByName("Table2_1*3");
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      // apply to empty table
      table = doc.addTable(8, 8);
      table.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table, true);

      doc.save(ResourceUtilities.newTestOutputFile("TestApplyRowStyledTableTemplate.odt"));
    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testReApplyTableTemplateAfterChanges() {
    try {
      TableTemplate template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME), "RowStyledTable");
      Table table1 = doc.getTableByName("DataTable1");
      Table table2 = doc.getTableByName("DataTable2");

      table1.applyStyle(template);
      table2.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table1, true);
      checkApplyRowOrColumnStyledTableTemplate(table2, true);

      template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME),
              "ColumnStyledTable");
      table1.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table1, false);

      table2.removeRowsByIndex(7, 2);
      table2.appendColumns(2);
      table2.applyStyle(template);
      checkApplyRowOrColumnStyledTableTemplate(table2, false);

      doc.save(ResourceUtilities.newTestOutputFile("TestReApplyTableTemplate.odt"));

    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testApplyTemplateWithEmptyCells() {
    try {
      TableTemplate template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME),
              "RowStyledEmptyTable");
      Table table = doc.getTableByName("DataTable1");
      table.applyStyle(template);
      checkApplyTemplateWithEmptyCells(table, true);

      template =
          doc.LoadTableTemplateFromForeignTable(
              ResourceUtilities.getTestResourceAsStream(TEST_TEMPLATE_FILE_NAME),
              "ColumnStyledEmptyTable");
      table = doc.getTableByName("DataTable2");
      table.applyStyle(template);
      checkApplyTemplateWithEmptyCells(table, false);

      doc.save(ResourceUtilities.newTestOutputFile("TestApplyTemplateWithNoParagraphStyle.odt"));
    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testApplyEmptyTemplate() {
    try {
      TableTemplate template =
          new TableTemplate(
              doc.getStylesDom()
                  .getOfficeStyles()
                  .newTableTableTemplateElement("", "", "", "", ""));
      doc.getTableByName("DataTable1").applyStyle(template);
      doc.save(ResourceUtilities.newTestOutputFile("TestApplyEmptyTemplate.odt"));
    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  private void checkFormatResultsByRow(
      Table table,
      Row row,
      String firstTableStyle,
      String oddTableStyle,
      String evenTableStyle,
      String lastTableStyle,
      String firstParaStyle,
      String oddParaStyle,
      String evenParaStyle,
      String lastParaStyle) {
    int cellIndex = 0;
    int mnRepeatedIndex = row.getRowsRepeatedNumber();
    Cell cell;
    String paraStyle;
    for (Node n : new DomNodeList(row.getOdfElement().getChildNodes())) {
      if (n instanceof TableTableCellElementBase) {
        cell = table.getCellInstance((TableTableCellElementBase) n, 0, mnRepeatedIndex);
        int lastIndex = row.getCellCount() - 1;
        if (cell.getColumnsRepeatedNumber() > 1) lastIndex -= cell.getColumnsRepeatedNumber();
        if (cellIndex == 0) {
          Assert.assertEquals(cell.getCellStyleName(), firstTableStyle);
          paraStyle = firstParaStyle;
        } else if (cellIndex == lastIndex) {
          Assert.assertEquals(cell.getCellStyleName(), lastTableStyle);
          paraStyle = lastParaStyle;
        } else if (cellIndex % 2 == 0) {
          Assert.assertEquals(cell.getCellStyleName(), evenTableStyle);
          paraStyle = evenParaStyle;
        } else {
          Assert.assertEquals(cell.getCellStyleName(), oddTableStyle);
          paraStyle = oddParaStyle;
        }
        Iterator<Paragraph> paraIterator = cell.getParagraphIterator();
        while (paraIterator.hasNext()) {
          Paragraph t = paraIterator.next();
          if (paraStyle != null && paraStyle != "") {
            Assert.assertEquals(t.getOdfElement().getStyleName(), paraStyle);
          }
        }
        cellIndex++;
      }
    }
  }

  private void checkApplyRowOrColumnStyledTableTemplate(Table table, boolean isRowStyle) {

    Iterator<Row> rowIterator = table.getRowIterator();
    if (rowIterator.hasNext()) { // first row
      Row currentRow = rowIterator.next();
      if (isRowStyle) {
        checkFormatResultsByRow(
            table,
            currentRow,
            "RowStyledTable.A1",
            "RowStyledTable.B1",
            "RowStyledTable.B1",
            "RowStyledTable.E1",
            "P1",
            "P2",
            "P2",
            "P5");
      } else {
        checkFormatResultsByRow(
            table,
            currentRow,
            "ColumnStyledTable.A1",
            "ColumnStyledTable.B1",
            "ColumnStyledTable.B1",
            "ColumnStyledTable.E1",
            "P1",
            "P2",
            "P2",
            "P5");
      }

      int line = 0;
      while (rowIterator.hasNext()) {
        currentRow = rowIterator.next();
        line++;
        if (!rowIterator.hasNext()) { // last row

          if (isRowStyle) {
            checkFormatResultsByRow(
                table,
                currentRow,
                "RowStyledTable.A5",
                "RowStyledTable.B5",
                "RowStyledTable.B5",
                "RowStyledTable.E5",
                "P3",
                "P2",
                "P2",
                "P4");
          } else {
            checkFormatResultsByRow(
                table,
                currentRow,
                "ColumnStyledTable.A5",
                "ColumnStyledTable.B5",
                "ColumnStyledTable.B5",
                "ColumnStyledTable.E5",
                "P3",
                "P2",
                "P2",
                "P4");
          }

        } else if (isRowStyle) {
          if (line % 2 != 0) { // odd row
            checkFormatResultsByRow(
                table,
                currentRow,
                "RowStyledTable.A2",
                "RowStyledTable.B2",
                "RowStyledTable.B2",
                "RowStyledTable.E2",
                "P2",
                "P2",
                "P2",
                "P2");
          } else { // even row
            checkFormatResultsByRow(
                table,
                currentRow,
                "RowStyledTable.A2",
                "RowStyledTable.B3",
                "RowStyledTable.B3",
                "RowStyledTable.E2",
                "P2",
                "P2",
                "P2",
                "P2");
          }
        } else { // even&odd column
          checkFormatResultsByRow(
              table,
              currentRow,
              "ColumnStyledTable.A2",
              "ColumnStyledTable.B2",
              "ColumnStyledTable.C2",
              "ColumnStyledTable.E2",
              "P2",
              "P2",
              "P2",
              "P2");
        }
      }
    }
  }

  private void checkApplyTemplateWithEmptyCells(Table table, boolean isRowStyle) {

    Iterator<Row> rowIterator = table.getRowIterator();
    if (rowIterator.hasNext()) { // first row
      Row currentRow = rowIterator.next();
      if (isRowStyle) {
        checkFormatResultsByRow(
            table,
            currentRow,
            "RowStyledEmptyTable.A1",
            "RowStyledEmptyTable.B1",
            "RowStyledEmptyTable.B1",
            "RowStyledEmptyTable.E1",
            "P1",
            "P2",
            "P2",
            "P5");
      } else {
        checkFormatResultsByRow(
            table,
            currentRow,
            "ColumnStyledEmptyTable.A1",
            "ColumnStyledEmptyTable.B1",
            "ColumnStyledEmptyTable.B1",
            "ColumnStyledEmptyTable.E1",
            "P1",
            "P2",
            "P2",
            "P5");
      }

      int line = 0;
      while (rowIterator.hasNext()) {
        currentRow = rowIterator.next();
        line++;
        if (!rowIterator.hasNext()) { // last row

          if (isRowStyle) {
            checkFormatResultsByRow(
                table,
                currentRow,
                "RowStyledEmptyTable.A5",
                "RowStyledEmptyTable.B5",
                "RowStyledEmptyTable.B5",
                "RowStyledEmptyTable.E5",
                "P3",
                "P2",
                "P2",
                "P4");
          } else {
            checkFormatResultsByRow(
                table,
                currentRow,
                "ColumnStyledEmptyTable.A5",
                "ColumnStyledEmptyTable.B5",
                "ColumnStyledEmptyTable.B5",
                "ColumnStyledEmptyTable.E5",
                "P3",
                "P2",
                "P2",
                "P4");
          }

        } else if (isRowStyle) {
          if (line % 2 != 0) { // odd row
            checkFormatResultsByRow(
                table,
                currentRow,
                "RowStyledEmptyTable.A2",
                "RowStyledEmptyTable.B2",
                "RowStyledEmptyTable.B2",
                "RowStyledEmptyTable.E2",
                "P2",
                "P2",
                "P2",
                "P2");
          } else { // even row
            checkFormatResultsByRow(
                table,
                currentRow,
                "RowStyledEmptyTable.A2",
                "RowStyledEmptyTable.B3",
                "RowStyledEmptyTable.B3",
                "RowStyledEmptyTable.E2",
                "P2",
                "P2",
                "P2",
                "P2");
          }
        } else { // even&odd column
          checkFormatResultsByRow(
              table,
              currentRow,
              "ColumnStyledEmptyTable.A2",
              "ColumnStyledEmptyTable.B2",
              "ColumnStyledEmptyTable.C2",
              "ColumnStyledEmptyTable.E2",
              "P2",
              "P2",
              "P2",
              "P2");
        }
      }
    }
  }

  private void checkLoadTemplateResults(TableTemplate template, String name) {
    OdfStyle style = null;
    try {
      OdfOfficeAutomaticStyles styles = doc.getContentDom().getAutomaticStyles();
      style = styles.getStyle(name + ".A1", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".B1", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".E1", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".A2", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".B2", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".E2", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      if (name.contains("Column")) {
        style = styles.getStyle(name + ".C2", OdfStyleFamily.TableCell);
        Assert.assertNotNull(style);
      } else {
        style = styles.getStyle(name + ".B3", OdfStyleFamily.TableCell);
        Assert.assertNotNull(style);
      }
      style = styles.getStyle(name + ".A5", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".B5", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle(name + ".E5", OdfStyleFamily.TableCell);
      Assert.assertNotNull(style);

      style = styles.getStyle("P1", OdfStyleFamily.Paragraph);
      Assert.assertNotNull(style);

      style = styles.getStyle("P2", OdfStyleFamily.Paragraph);
      Assert.assertNotNull(style);

      style = styles.getStyle("P4", OdfStyleFamily.Paragraph);
      Assert.assertNotNull(style);

      style = styles.getStyle("P5", OdfStyleFamily.Paragraph);
      Assert.assertNotNull(style);

      style = styles.getStyle("P3", OdfStyleFamily.Paragraph);
      Assert.assertNotNull(style);

    } catch (Exception e) {
      Logger.getLogger(TableTemplateTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }
}
