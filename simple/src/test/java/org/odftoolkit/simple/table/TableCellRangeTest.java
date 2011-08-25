/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009 IBM. All rights reserved.
 * 
 * Use is subject to license terms.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.simple.table;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.WhitespaceProcessor;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellRange;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

public class TableCellRangeTest {

	final String filename = "TestSpreadsheetTable";
	final String odtfilename = "TestTextTable";
	SpreadsheetDocument odsdoc;
	TextDocument odtdoc;
	Table odsTable, odtTable;

	@Before
	public void setUp() {
		try {
			odsdoc = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(filename + ".ods"));
			odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(odtfilename + ".odt"));
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testTextCellMerge() {
		//get cell range, then merge
		Table table1 = odtdoc.getTableByName("Table1");
		//get the first two cell
		CellRange cellRange = table1.getCellRangeByPosition(0, 0, 1, 0);
		cellRange.merge();
		Cell cell = cellRange.getCellByPosition(0, 0);
		Assert.assertEquals(cell.getDisplayText(), "cell1cell2");
		saveodt("MergeTwoCell");
		try {
			TextDocument saveddoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(odtfilename + "MergeTwoCell.odt"));
			Table savedTable1 = saveddoc.getTableByName("Table1");
			//get the cell range which the first cell is the covered cell.
			//so the cell range will be enlarged
			CellRange savedCellRange = savedTable1.getCellRangeByPosition(1, 0, 2, 0);
			savedCellRange.merge();
			Assert.assertTrue(savedCellRange.getColumnNumber() == 3);
			Cell savedCell = savedCellRange.getCellByPosition(0, 0);
			NodeList paraList = savedCell.getOdfElement().getChildNodes();
			WhitespaceProcessor textProcessor = new WhitespaceProcessor();
			Assert.assertTrue(paraList.item(2) instanceof OdfTextParagraph);
			Assert.assertEquals(textProcessor.getText(paraList.item(2)),"0.00");
			saveddoc.save(ResourceUtilities.newTestOutputFile(odtfilename + "MergeCoveredCell.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

		try {
			TextDocument saveddoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(odtfilename + "MergeTwoCell.odt"));
			Table savedTable1 = saveddoc.getTableByName("Table1");
			//get the cell range which the first cell is the covered cell.
			//so the cell range will be enlarged
			CellRange savedCellRange = savedTable1.getCellRangeByPosition(0, 0, 0, 1);
			savedCellRange.merge();
			Assert.assertTrue(savedCellRange.getColumnNumber() == 2);
			Assert.assertTrue(savedCellRange.getRowNumber() == 2);
			Cell savedCell = savedCellRange.getCellByPosition(0, 1);
			Assert.assertTrue(savedCell.getOdfElement() instanceof TableCoveredTableCellElement);
			saveddoc.save(ResourceUtilities.newTestOutputFile(odtfilename + "MergeCoveredCell2.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/////////////////////////////////////////
	//issue: removeColumnByIndex removeRowByIndex removeCellByIndex
	@Test
	public void testTextTableMerge() {
		Table table1 = odtdoc.getTableByName("Table1");
		//merge whole table
		CellRange cellRange = table1.getCellRangeByPosition(0, 0, table1.getColumnCount() - 1, table1.getRowCount() - 1);
		cellRange.merge();
		Assert.assertEquals(table1.getColumnCount(), 1);
		Assert.assertEquals(table1.getRowCount(), 1);
		saveodt("MergeTable");
	}

	@Test
	public void testTextColumnMerge() {
		//merge first column
		Table table1 = odtdoc.getTableByName("Table1");
		CellRange firstColumn = table1.getCellRangeByPosition(0, 0, 0, table1.getRowCount() - 1);
		firstColumn.merge();
		Cell cell = firstColumn.getCellByPosition(0, 2);
		Cell firstCell = firstColumn.getCellByPosition(0, 0);
		Assert.assertTrue(cell.getOwnerTableCell().equals(firstCell));
		saveodt("MergeFirstColumn");
		try {
			TextDocument saveddoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(odtfilename + "MergeFirstColumn.odt"));
			Table savedTable = saveddoc.getTableByName("Table1");
			CellRange firstTwoColumn = savedTable.getCellRangeByPosition(0, 0, 1, savedTable.getRowCount() - 1);
			firstTwoColumn.merge();
			Cell cell1 = firstTwoColumn.getCellByPosition(0, 2);
			Cell firstCell1 = firstTwoColumn.getCellByPosition(0, 0);
			Assert.assertTrue(cell1.getOwnerTableCell().equals(firstCell1));
			saveddoc.save(ResourceUtilities.newTestOutputFile(odtfilename + "MergeFirstTwoColumn.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testTextRowMerge() {
		//merge first two row
		Table table1 = odtdoc.getTableByName("Table1");
		int rowCount = table1.getRowCount();
		CellRange firstTwoRow = table1.getCellRangeByPosition(0, 0, table1.getColumnCount() - 1, 1);
		firstTwoRow.merge();
		Assert.assertTrue(rowCount == (table1.getRowCount() + 1));
		saveodt("MergeFirstTwoRow");
	}

	@Test
	public void testSpreadSheetMerge() {
		//get cell range, set name
		Table sheet1 = odsdoc.getTableByName("Sheet1");
		CellRange cellRange = sheet1.getCellRangeByPosition(28, 0, 28, 5);
		cellRange.setCellRangeName("test");
		cellRange.merge();
		CellRange cellRange1 = sheet1.getCellRangeByPosition("$E1", "$E6");
		cellRange1.setCellRangeName("TimeCellRange");
		cellRange1.merge();

		saveods("CellRangeName");
		try {
			SpreadsheetDocument saveddos = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(filename + "CellRangeName.ods"));
			Table savedSheet = saveddos.getTableByName("Sheet1");
			CellRange namedCellRange = savedSheet.getCellRangeByName("TimeCellRange");
			Cell cell = namedCellRange.getCellByPosition("A1");
			Assert.assertTrue(cell.getRowSpannedNumber() == 6);
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testMergeExpandCellRange() {
		try {
			SpreadsheetDocument ods = SpreadsheetDocument
					.newSpreadsheetDocument();
			// the doc contain the table which only have one column and one row
			// element
			Table table = ods.getTableByName("Sheet1");
			int nCols = table.getColumnCount();
			int nRows = table.getRowCount();
			Assert.assertTrue(nCols == 1);
			Assert.assertTrue(nRows == 1);
			CellRange cellRange = table.getCellRangeByPosition("A1","E1");
			Cell cell = table.getCellByPosition("A1");
			cell.setStringValue("Merge A1:E1");
			cellRange.merge();
			Table table2 = Table.newTable(ods, 1, 1);
			table2.setTableName("Sheet2");
			CellRange cellRange2 = table2.getCellRangeByPosition("A1","F3");
			Cell cell2 = table2.getCellByPosition("A1");
			cell2.setStringValue("Merge A1:F3");
			cellRange2.merge();
			ods.save(ResourceUtilities.newTestOutputFile(filename + "MergeExpandCell.ods"));
			table = ods.getTableByName("Sheet1");
			Assert.assertTrue(table.getColumnCount() == 5);
			Assert.assertTrue(table.getRowCount() == 1);
			table = ods.getTableByName("Sheet2");
			Assert.assertTrue(table.getColumnCount() == 6);
			Assert.assertTrue(table.getRowCount() == 3);
			TextDocument odt = TextDocument.newTextDocument();
			Table swTable = Table.newTable(odt, 1, 5);
			CellRange swCellRange = swTable.getCellRangeByPosition("A1", "E2");
			Cell swCell = swTable.getCellByPosition("E2");
			swCell.setStringValue("Merge A1:E2");
			swCellRange.merge();
			odt.save(ResourceUtilities.newTestOutputFile(odtfilename + "MergeTextExpandCell.odt"));
			swTable = odt.getTableList().get(0);
			Assert.assertTrue(swTable.getColumnCount() == 1);
			Assert.assertTrue(swTable.getRowCount() == 1);
		} catch (Exception ex) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log( Level.SEVERE, ex.getMessage(), ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}

	}

	private void saveods(String name) {
		try {
			odsdoc.save(ResourceUtilities.newTestOutputFile(filename + name + ".ods"));
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	private void saveodt(String name) {
		try {
			odtdoc.save(ResourceUtilities.newTestOutputFile(odtfilename + name + ".odt"));
		} catch (Exception e) {
			Logger.getLogger(TableCellRangeTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
