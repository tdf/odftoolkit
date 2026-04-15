package org.odftoolkit.odfdom.doc.table;

import static org.junit.Assert.assertEquals;
import static org.odftoolkit.odfdom.utils.ResourceUtilities.getAbsoluteInputPath;

import junit.framework.AssertionFailedError;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;

public class TableCellCountTest {

  // The number of columns that Excel always uses.
  // For example Excel puts <table:table-cell table:number-columns-repeated="16384"/> if no cell values or covered cells
  private static final int EXCEL_COLUMN_COUNT = 16384;

  @Test
  public void verifyCellCountForLibreOfficeGeneratedSpreadsheet() {
    // Spreadsheet created by LibreOffice on Mac version 25.8.4.2 (AARCH64)
    try (OdfSpreadsheetDocument spreadsheet = loadSpreadsheetDocument("TestLibreOfficeSpreadsheetTableCellCount.ods")) {
      OdfTable sheet = spreadsheet.getSpreadsheetTables().get(0);
      assertEquals(3, sheet.getColumnCount());

      // 3 cells merged so 2 covered cells
      assertEquals(3 - 2 /* covered cells */, sheet.getRowByIndex(0).getCellCount());

      // 2 cells merged so 1 covered cell
      assertEquals(3 - 1 /* covered cell */, sheet.getRowByIndex(1).getCellCount());

      // no merged cells
      assertEquals(3, sheet.getRowByIndex(2).getCellCount());

      // 2 cells merged over 2 rows (simplified XML):
      //        <table:table-row>
      //          <table:table-cell table:number-columns-spanned="2" table:number-rows-spanned="2">
      //            <text:p>1</text:p>
      //          </table:table-cell>
      //          <table:covered-table-cell/>
      //          <table:table-cell>
      //            <text:p>2</text:p>
      //          </table:table-cell>
      //        </table:table-row>
      //        <table:table-row>
      //          <table:covered-table-cell table:number-columns-repeated="2"/>
      //          <table:table-cell>
      //            <text:p>2</text:p>
      //          </table:table-cell>
      //        </table:table-row>
      // so 1 covered cell in first row
      assertEquals(3 - 1 /* covered cell */, sheet.getRowByIndex(3).getCellCount());
      // ... and 2 covered cells in second row
      assertEquals(3 - 2 /* covered cells */, sheet.getRowByIndex(4).getCellCount());
    }
  }

  @Test
  public void verifyCellCountForExcelGeneratedSpreadsheet() {
    // Spreadsheet created by Microsoft® Excel for Mac Version 16.106 (26020821)
    try (OdfSpreadsheetDocument spreadsheet = loadSpreadsheetDocument("TestExcelSpreadsheetTableCellCount.ods")) {
      OdfTable sheet = spreadsheet.getSpreadsheetTables().get(0);
      assertEquals(EXCEL_COLUMN_COUNT, sheet.getColumnCount());

      // 3 cells merged so 2 covered cells
      assertEquals(EXCEL_COLUMN_COUNT - 2 /* covered cells */, sheet.getRowByIndex(0).getCellCount());

      // 2 cells merged so 1 covered cell
      assertEquals(EXCEL_COLUMN_COUNT - 1 /* covered cell */, sheet.getRowByIndex(1).getCellCount());

      // no merged cells
      assertEquals(EXCEL_COLUMN_COUNT, sheet.getRowByIndex(2).getCellCount());

      // 2 cells merged over 2 rows so 1 covered cell in first row
      assertEquals(EXCEL_COLUMN_COUNT - 1 /* covered cell */, sheet.getRowByIndex(3).getCellCount());
      // ... and 2 covered cells in second row
      assertEquals(EXCEL_COLUMN_COUNT - 2 /* covered cells */, sheet.getRowByIndex(4).getCellCount());

      // Excel always adds
      //         <table:table-row table:number-rows-repeated="1048573" table:style-name="ro1">
      //          <table:table-cell table:number-columns-repeated="16384"/>
      //        </table:table-row>
      assertEquals(EXCEL_COLUMN_COUNT, sheet.getRowByIndex(5).getCellCount());
    }
  }

  private OdfSpreadsheetDocument loadSpreadsheetDocument(String filename) {
    try {
      return OdfSpreadsheetDocument.loadDocument(
        getAbsoluteInputPath(filename));
    } catch (Exception ex) {
      throw new AssertionFailedError(ex.getMessage());
    }
  }
}
