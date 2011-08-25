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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.Document.OdfMediaType;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TableTest {

	final String mOdsTestFileName = "TestSpreadsheetTable";
	final String mOdtTestFileName = "TestTextTable";
	SpreadsheetDocument mOdsDoc;
	TextDocument mOdtDoc;
	TableTableElement mOdsTable, mOdtTable;

	@Before
	public void setUp() {
		try {
			mOdsDoc = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(mOdsTestFileName + ".ods"));
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private TextDocument loadODTDocument(String name) {
		try {
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(name));
			return odtdoc;
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
		return null;
	}

	@Test
	public void testNewTable() {
		try {
			TextDocument document = TextDocument.newTextDocument();
			document.newParagraph("Empty table:");
			createEmptyTable(document);

			document.newParagraph();
			document.newParagraph("Table with float values:");
			createTableWithData(document);
			document.newParagraph();
			document.newParagraph("Table with string values:");
			createTableWithString(document);

			document.save(ResourceUtilities.newTestOutputFile("CreateTableCase.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testColumnWidthCompareNewTableWithGetCellByPosition() {
		try {
			SpreadsheetDocument odsDoc = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = Table.newTable(odsDoc, 20, 20);
			table.setTableName("Table1");
			double width1 = table.getColumnByIndex(0).getWidth();
			table = Table.newTable(odsDoc);
			table.setTableName("Table2");
			// set the table size as 20*20.
			table.getCellByPosition(19, 19);
			double width2 = table.getColumnByIndex(0).getWidth();
			Assert.assertEquals(width1, width2);
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testNewTableWithArrayData() {
		try {
			// reproduce bug 121
			int rowCount = 10, columnCount = 4;
			String[] rowLabels = new String[rowCount];
			for (int i = 0; i < rowCount; i++) {
				rowLabels[i] = "RowHeader" + i;
			}
			String[] columnLabels = new String[columnCount];
			for (int i = 0; i < columnCount; i++) {
				columnLabels[i] = "ColumnHeader" + i;
			}
			double[][] doubleArray = null;
			String[][] stringArray = null;
			SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
			Table table1 = Table.newTable(spreadsheet, null, null, doubleArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT 2
			Assert.assertEquals(2, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT 5
			Assert.assertEquals(5, table1.getColumnCount());

			table1 = Table.newTable(spreadsheet, rowLabels, columnLabels, doubleArray);
			Assert.assertEquals(1, table1.getHeaderColumnCount());
			Assert.assertEquals(1, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT+1 3
			Assert.assertEquals(3, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT+1 6
			Assert.assertEquals(6, table1.getColumnCount());

			table1 = Table.newTable(spreadsheet, null, null, stringArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT 2
			Assert.assertEquals(2, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT 5
			Assert.assertEquals(5, table1.getColumnCount());

			table1 = Table.newTable(spreadsheet, rowLabels, columnLabels, stringArray);
			Assert.assertEquals(1, table1.getHeaderColumnCount());
			Assert.assertEquals(1, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT+1 3
			Assert.assertEquals(3, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT+1 6
			Assert.assertEquals(6, table1.getColumnCount());

			doubleArray = new double[rowCount][columnCount];
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					doubleArray[i][j] = Math.random();
				}
			}
			table1 = Table.newTable(spreadsheet, null, null, doubleArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			Assert.assertEquals(rowCount, table1.getRowCount());
			Assert.assertEquals(columnCount, table1.getColumnCount());

			table1 = Table.newTable(spreadsheet, rowLabels, columnLabels, doubleArray);
			Assert.assertEquals(1, table1.getHeaderColumnCount());
			Assert.assertEquals(1, table1.getHeaderRowCount());
			Assert.assertEquals(rowCount + 1, table1.getRowCount());
			Assert.assertEquals(columnCount + 1, table1.getColumnCount());

			stringArray = new String[rowCount][columnCount];
			for (int i = 0; i < rowCount; i++) {
				for (int j = 0; j < columnCount; j++) {
					stringArray[i][j] = "string" + (i * columnCount + j);
				}
			}
			table1 = Table.newTable(spreadsheet, null, null, stringArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			Assert.assertEquals(rowCount, table1.getRowCount());
			Assert.assertEquals(columnCount, table1.getColumnCount());

			table1 = Table.newTable(spreadsheet, rowLabels, columnLabels, stringArray);
			Assert.assertEquals(1, table1.getHeaderColumnCount());
			Assert.assertEquals(1, table1.getHeaderRowCount());
			Assert.assertEquals(rowCount + 1, table1.getRowCount());
			Assert.assertEquals(columnCount + 1, table1.getColumnCount());
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testNewTableWithoutHeaderColumn() {
		try {
			// reproduce bug 145
			SpreadsheetDocument spreadsheet = SpreadsheetDocument.newSpreadsheetDocument();
			Table sheet = Table.newTable(spreadsheet, 3, 5);
			TableTableHeaderColumnsElement headers = OdfElement.findFirstChildNode(
					TableTableHeaderColumnsElement.class, sheet.getOdfElement());
			if (headers != null) {
				for (Node n : new DomNodeList(headers.getChildNodes())) {
					if (n instanceof TableTableColumnElement) {
						if (sheet.getColumnInstance(((TableTableColumnElement) n), 0).getColumnsRepeatedNumber() == 0) {
							Assert
									.fail("table:number-columns-repeated has the invalid value: '0'. It have to be a value matching the 'positiveInteger' type.");
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private Table createEmptyTable(TextDocument document) {
		String tablename = "Table1";
		int rownumber = 5;
		int clmnumber = 3;

		Table table1 = Table.newTable(document, 5, 3);
		table1.setTableName(tablename);

		Assert.assertEquals(tablename, table1.getTableName());
		Assert.assertEquals(rownumber, table1.getRowCount());
		Assert.assertEquals(clmnumber, table1.getColumnCount());

		Table table = document.getTableByName(tablename);
		Assert.assertNotNull(table);
		Assert.assertEquals(table1, table);
		return table1;
	}

	private Table createTableWithData(TextDocument document) {
		String tablename = "Table2";
		int rowcount = 10, columncount = 4;
		double[][] data = new double[rowcount][columncount];
		for (int i = 0; i < rowcount; i++) {
			for (int j = 0; j < columncount; j++) {
				data[i][j] = Math.random();
			}
		}

		String[] rowlabels = new String[rowcount];
		for (int i = 0; i < rowcount; i++) {
			rowlabels[i] = "RowHeader" + i;
		}

		String[] columnlabels = new String[columncount];
		for (int i = 0; i < columncount; i++) {
			columnlabels[i] = "ColumnHeader" + i;
		}

		Table table2 = Table.newTable(document, rowlabels, columnlabels, data);
		table2.setTableName(tablename);

		Assert.assertEquals(1, table2.getHeaderColumnCount());
		Assert.assertEquals(1, table2.getHeaderRowCount());
		Assert.assertEquals(rowcount + 1, table2.getRowCount());
		Assert.assertEquals(columncount + 1, table2.getColumnCount());
		Table table = document.getTableByName(tablename);
		Assert.assertNotNull(table);
		Assert.assertEquals(table2, table);

		Cell cell = table.getCellByPosition(1, 1);
		Assert.assertEquals("float", cell.getValueType());

		return table2;
	}

	private Table createTableWithString(TextDocument document) {
		String tablename = "Table3";
		int rowcount = 7, columncount = 5;
		String[][] data = new String[rowcount][columncount];
		for (int i = 0; i < rowcount; i++) {
			for (int j = 0; j < columncount; j++) {
				data[i][j] = "string" + (i * columncount + j);
			}
		}

		String[] rowlabels = new String[rowcount];
		for (int i = 0; i < rowcount; i++) {
			rowlabels[i] = "RowHeader" + i;
		}

		String[] columnlabels = new String[columncount];
		for (int i = 0; i < columncount; i++) {
			columnlabels[i] = "ColumnHeader" + i;
		}

		Table table3 = Table.newTable(document, rowlabels, columnlabels, data);
		table3.setTableName(tablename);

		Assert.assertEquals(1, table3.getHeaderColumnCount());
		Assert.assertEquals(1, table3.getHeaderRowCount());
		Assert.assertEquals(rowcount + 1, table3.getRowCount());
		Assert.assertEquals(columncount + 1, table3.getColumnCount());
		Table table = document.getTableByName(tablename);
		Assert.assertNotNull(table);
		Assert.assertEquals(table3, table);

		Cell cell = table.getCellByPosition(1, 1);
		Assert.assertEquals("string", cell.getValueType());

		return table3;

	}

	@Test
	public void testDeleteTable() {
		try {
			mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
			List<Table> tableList = mOdtDoc.getTableList();
			int count = tableList.size();

			Table table = mOdtDoc.getTableByName("DeletedTable");
			if (table != null) {
				table.remove();
			}

			saveodt(mOdtTestFileName + "Out.odt");
			mOdtDoc = loadODTDocument(mOdtTestFileName + "Out.odt");
			tableList = mOdtDoc.getTableList();
			Assert.assertEquals(count - 1, tableList.size());
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetGetWidth() {
		long width = 500;
		try {
			TextDocument document = TextDocument.newTextDocument();
			document.newParagraph("Empty table:");
			Table table = createEmptyTable(document);
			table.setWidth(width);
			Assert.assertTrue(Math.abs(width - table.getWidth()) < 3);

			document.save(ResourceUtilities.newTestOutputFile("TestSetGetWidth.odt"));

		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAppendColumn() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		List<Table> tableList = mOdtDoc.getTableList();
		for (int i = 0; i < tableList.size(); i++) {
			Table table = tableList.get(i);
			int clmnum = table.getColumnCount();
			table.appendColumn();
			Assert.assertEquals(clmnum + 1, table.getColumnCount());

			Column column = table.getColumnByIndex(clmnum);
			Column columnOld = table.getColumnByIndex(clmnum - 1);
			Assert.assertEquals(column.getCellCount(), columnOld.getCellCount());
		}
		saveodt(mOdtTestFileName + "Output.odt");
	}

	@Test
	public void testGetSetTablename() {
		String tablename = "My Table";
		TextDocument document = null;
		try {
			document = TextDocument.newTextDocument();
			document.newParagraph("Empty table:");
			Table table = createEmptyTable(document);
			table.setTableName(tablename);
			Assert.assertEquals(tablename, table.getTableName());

			document.save(ResourceUtilities.newTestOutputFile("TestGetSetName.odt"));
			document.close();
			document = loadODTDocument("TestGetSetName.odt");
			table = document.getTableByName(tablename);
			Assert.assertNotNull(table);
			String tablename2 = table.getTableName();
			Assert.assertEquals(tablename, tablename2);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

		try {
			// new another table with the same name
			// an exception will be thrown
			Table table2 = Table.newTable(document);
			table2.setTableName(tablename);
			document.save(ResourceUtilities.newTestOutputFile("TestGetSetName.odt"));
			Assert.fail("should not save the tables with the same table name.");
		} catch (Exception e) {
			if (!e.getMessage().startsWith("The table name is duplicate")) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@Test
	public void testInsertColumnBefore() throws Exception {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Table table1 = mOdtDoc.getTableByName("Table3");
		table1.setUseRepeat(false);
		CellRange range = table1.getCellRangeByPosition(0, 1, 1, 2);
		range.merge();

		int clmnum = table1.getColumnCount();
		Column oldClm1 = table1.getColumnByIndex(1);

		List<Column> columns = table1.insertColumnsBefore(1, 2);
		Assert.assertEquals(clmnum + 2, table1.getColumnCount());
		Column clm0 = table1.getColumnByIndex(0);
		Column clm1 = table1.getColumnByIndex(1);
		Column clm2 = table1.getColumnByIndex(2);
		Column clm3 = table1.getColumnByIndex(3);
		Assert.assertEquals(columns.get(0), clm1);
		Assert.assertEquals(columns.get(1), clm2);
		Assert.assertEquals(clm0.getCellCount(), clm1.getCellCount());
		Assert.assertEquals(clm1.getCellCount(), clm2.getCellCount());
		Assert.assertEquals(clm3, oldClm1);

		Table table2 = mOdtDoc.getTableByName("Table2");
		Column oldClm0 = table2.getColumnByIndex(0);
		columns = table2.insertColumnsBefore(0, 2);

		Column newClm0 = table2.getColumnByIndex(0);
		Column newClm1 = table2.getColumnByIndex(1);
		Column newClm2 = table2.getColumnByIndex(2);
		Assert.assertEquals(newClm0.getCellCount(), newClm2.getCellCount());
		Assert.assertEquals(newClm1.getCellCount(), newClm2.getCellCount());
		Assert.assertEquals(newClm2, oldClm0);

		saveodt(mOdtTestFileName + "Out.odt");
	}

	@Test
	public void testRemoveColumnByIndex() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Table table1 = mOdtDoc.getTableByName("Table3");
		CellRange range = table1.getCellRangeByPosition(0, 1, 1, 2);
		range.merge();

		int clmnum = table1.getColumnCount();
		Column oldClm0 = table1.getColumnByIndex(0);
		Column oldClm3 = table1.getColumnByIndex(3);
		table1.removeColumnsByIndex(1, 2);
		Column newClm0 = table1.getColumnByIndex(0);
		Column newClm1 = table1.getColumnByIndex(1);
		Assert.assertEquals(clmnum - 2, table1.getColumnCount());
		Assert.assertEquals(oldClm0, newClm0);
		Assert.assertEquals(oldClm3, newClm1);

		Table table2 = mOdtDoc.getTableByName("Table4");
		clmnum = table2.getColumnCount();
		Column oldClm1 = table2.getColumnByIndex(2);
		table2.removeColumnsByIndex(0, 2);
		table2.removeColumnsByIndex(table2.getColumnCount() - 2, 2);
		Column clm0 = table2.getColumnByIndex(0);
		Assert.assertEquals(oldClm1, clm0);
		Assert.assertEquals(clmnum - 4, table2.getColumnCount());
		saveodt(mOdtTestFileName + "Out.odt");

	}

	@Test
	public void testInsertRowBefore() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Table table2 = mOdtDoc.getTableByName("Table2");
		Row row = table2.getRowByIndex(0);
		int originalRowCount = table2.getRowCount();
		List<Row> newRows = table2.insertRowsBefore(0, 2);

		Row newRow1 = table2.getRowByIndex(0);
		Row newRow2 = table2.getRowByIndex(0);
		Assert.assertEquals(newRow1.getCellCount(), newRows.get(0).getCellCount());
		Assert.assertEquals(newRow2.getCellCount(), newRows.get(1).getCellCount());
		// original row index 0
		Assert.assertEquals(row, table2.getRowByIndex(2));

		saveodt(mOdtTestFileName + "Out.odt");

		mOdtDoc = loadODTDocument(mOdtTestFileName + "Out.odt");
		Table newTable = mOdtDoc.getTableByName("Table2");

		Assert.assertEquals(originalRowCount + 2, newTable.getRowCount());

	}

	@Test
	public void testGetColumnList() {
		String tablename = "MyTable";
		String testFileName = "TestGetColumnList.odt";
		try {
			TextDocument document = TextDocument.newTextDocument();

			int rowcount = 3, columncount = 3;
			String[][] data = new String[rowcount][columncount];
			for (int i = 0; i < rowcount; i++) {
				for (int j = 0; j < columncount; j++) {
					data[i][j] = "string" + (i * columncount + j);
				}
			}

			String[] rowlabels = new String[rowcount];
			for (int i = 0; i < rowcount; i++) {
				rowlabels[i] = "RowHeader" + i;
			}

			String[] columnlabels = new String[columncount];
			for (int i = 0; i < columncount; i++) {
				columnlabels[i] = "ColumnHeader" + i;
			}

			Table table3 = Table.newTable(document, rowlabels, columnlabels, data);
			table3.setTableName(tablename);

			document.save(ResourceUtilities.newTestOutputFile(testFileName));
			document = loadODTDocument(testFileName);
			Table table = document.getTableByName(tablename);
			Column tmpColumn;
			List<Column> columns = table.getColumnList();

			// the code below prints the column value,it shows that the first
			// columns value is the same with the last column
			for (int i = 0; i < columns.size(); i++) {
				tmpColumn = columns.get(i);
				for (int j = 0; j < tmpColumn.getCellCount(); j++) {
					String text = tmpColumn.getCellByIndex(j).getStringValue();
					if (i == 0 && j == 0) {
						Assert.assertEquals("", text);
					} else if (i == 0 && j > 0) {
						Assert.assertEquals("RowHeader" + (j - 1), text);
					} else if (i > 0 && j == 0) {
						Assert.assertEquals("ColumnHeader" + (i - 1), text);
					} else {
						Assert.assertEquals("string" + ((j - 1) * columncount + i - 1), text);
					}
				}
			}
			
			for (int i = 1; i < columns.size(); i++) {
				tmpColumn = columns.get(i);
				// each column's first cell is the column header
				Assert.assertEquals(columnlabels[i - 1], tmpColumn.getCellByIndex(0).getStringValue());
			}
			Assert.assertEquals(columncount, columns.size() - 1);
			Assert.assertEquals("", columns.get(0).getCellByIndex(0).getStringValue());
			
			//test table column iterator
			Iterator<Column> columnIterator = table.getColumnIterator();
			int columnNumber =0;
			if(columnIterator.hasNext()){
				Column column = columnIterator.next();
				Assert.assertEquals("", column.getCellByIndex(0).getStringValue());
				columnNumber++;
			}
			while(columnIterator.hasNext()){
				Column column = columnIterator.next();
				Assert.assertEquals(columnlabels[columnNumber-1], column.getCellByIndex(0).getStringValue());
				columnNumber++;
			}
			Assert.assertEquals(columnNumber, columns.size());

		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testGetRowList() {
		String tablename = "MyTable";
		String testFileName = "TestGetRowList.odt";
		try {
			TextDocument document = TextDocument.newTextDocument();

			int rowcount = 3, columncount = 3;
			String[][] data = new String[rowcount][columncount];
			for (int i = 0; i < rowcount; i++) {
				for (int j = 0; j < columncount; j++) {
					data[i][j] = "string" + (i * columncount + j);
				}
			}

			String[] rowlabels = new String[rowcount];
			for (int i = 0; i < rowcount; i++) {
				rowlabels[i] = "RowHeader" + i;
			}

			String[] columnlabels = new String[columncount];
			for (int i = 0; i < columncount; i++) {
				columnlabels[i] = "ColumnHeader" + i;
			}

			Table table3 = Table.newTable(document, rowlabels, columnlabels, data);
			table3.setTableName(tablename);

			document.save(ResourceUtilities.newTestOutputFile(testFileName));
			document = loadODTDocument(testFileName);
			Table table = document.getTableByName(tablename);
			Row tmpRow;
			List<Row> rows = table.getRowList();
			for (int i = 1; i < rows.size(); i++) {
				tmpRow = rows.get(i);
				// each row's first cell is the row header
				Assert.assertEquals(rowlabels[i - 1], tmpRow.getCellByIndex(0).getStringValue());
			}
			Assert.assertEquals(rowcount, rows.size() - 1);
			Assert.assertEquals("", rows.get(0).getCellByIndex(0).getStringValue());
			
			//test table row iterator
			Iterator<Row> rowIterator = table.getRowIterator();
			int rowNumber =0;
			if(rowIterator.hasNext()){
				Row row = rowIterator.next();
				Assert.assertEquals("", row.getCellByIndex(0).getStringValue());
				rowNumber++;
			}
			while(rowIterator.hasNext()){
				Row row = rowIterator.next();
				Assert.assertEquals(rowlabels[rowNumber-1], row.getCellByIndex(0).getStringValue());
				rowNumber++;
			}
			Assert.assertEquals(rowNumber, rows.size());
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetColumnByIndex() {

		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		Table table = mOdtDoc.getTableByName("Table3");
		Assert.assertNotNull(table);
		// test if index is negative number, which is an illegal argument.
		boolean illegalArgumentFlag = false;
		try {
			table.getColumnByIndex(-1);
		} catch (IllegalArgumentException ie) {
			if ("index should be nonnegative integer.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		Column column = table.getColumnByIndex(2);
		Assert.assertNotNull(column);
		Assert.assertEquals("string6", column.getCellByIndex(2).getStringValue());
		// test column automatically expands.
		// Table3 original size is 7 rows and 5 columns. this test case will
		// test row index 8 and columns index 6 are work well though they are
		// both out bound of the original table.
		column = table.getColumnByIndex(8);
		Assert.assertNotNull(column);
		Cell cell = column.getCellByIndex(6);
		Assert.assertNotNull(cell);
		cell.setStringValue("string86");
		Assert.assertEquals("string86", cell.getStringValue());
	}

	@Test
	public void testGetRowByIndex() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		Table table = mOdtDoc.getTableByName("Table3");
		Assert.assertNotNull(table);
		// test index is negative number. This is a illegal argument.
		boolean illegalArgumentFlag = false;
		try {
			table.getRowByIndex(-1);
		} catch (IllegalArgumentException ie) {
			if ("index should be nonnegative integer.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		Row row = table.getRowByIndex(3);
		Assert.assertNotNull(row);
		Assert.assertEquals("string12", row.getCellByIndex(3).getStringValue());
		// test row automatically expands.
		// Table3 original size is 7 rows and 5 columns. this test case will
		// test row index 8 and columns index 6 are work well though they are
		// both out bound of the original table.
		row = table.getRowByIndex(6);
		Assert.assertNotNull(row);
		Cell cell = row.getCellByIndex(8);
		Assert.assertNotNull(cell);
		cell.setStringValue("string86");
		Assert.assertEquals("string86", cell.getStringValue());
	}

	@Test
	public void testRemoveRowByIndex() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Table table2 = mOdtDoc.getTableByName("Table2");
		Row row0 = table2.getRowByIndex(0);
		Row row3 = table2.getRowByIndex(3);
		int originalRowCount = table2.getRowCount();
		table2.removeRowsByIndex(1, 2);

		// original row index 0
		Assert.assertEquals(row0, table2.getRowByIndex(0));
		Assert.assertEquals(row3, table2.getRowByIndex(1));

		saveodt(mOdtTestFileName + "Out.odt");

		mOdtDoc = loadODTDocument(mOdtTestFileName + "Out.odt");
		Table newTable = mOdtDoc.getTableByName("Table2");

		Assert.assertEquals(originalRowCount - 2, newTable.getRowCount());
	}

	@Test
	public void testGetHeaderRowCount() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		Table table = mOdtDoc.getTableByName("Table3");
		int headerRowCount = table.getHeaderRowCount();
		Assert.assertEquals(1, headerRowCount);
	}

	@Test
	public void testGetRowCount() {
		try {
			// without table rows
			Document mOdpDoc = Document.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TableCountTestcase.odp"));
			Table table = mOdpDoc.getTableByName("Table1");
			int rowCount = table.getRowCount();
			Assert.assertEquals(5, rowCount);
			// with table rows
			Document mOdcDoc = mOdpDoc.getEmbeddedDocument("Object 2/");
			table = mOdcDoc.getTableByName("local-table");
			rowCount = table.getRowCount();
			Assert.assertEquals(5, rowCount);
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetHeaderColumnCount() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		Table table = mOdtDoc.getTableByName("Table3");
		int headerColumnCount = table.getHeaderColumnCount();
		Assert.assertEquals(1, headerColumnCount);

	}

	@Test
	public void testGetColumnCountWithColumnsInDocument() {
		try {
			SpreadsheetDocument sDoc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("Spreadsheet with Embeded Chart.ods"));
			List<Document> charts = sDoc.getEmbeddedDocuments(OdfMediaType.CHART);
			for (Document chart : charts) {
				// "local-table" is the inner table name of chart document with
				// 2 columns
				Table localTable = chart.getTableByName("local-table");
				int columnCount = localTable.getColumnCount();
				Assert.assertEquals(2, columnCount);
			}
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testIsProtected() throws Exception {
		String tablename = "DeletedTable";
		String outputFilename = "tableProtected.odt";

		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Assert.assertNotNull(mOdtDoc);
		Table table = mOdtDoc.getTableByName(tablename);
		table.setProtected(false);
		mOdtDoc.save(ResourceUtilities.newTestOutputFile(outputFilename));

		mOdtDoc = loadODTDocument(outputFilename);
		table = mOdtDoc.getTableByName(tablename);
		Assert.assertFalse(table.isProtected());

	}

	@Test
	public void testSetIsProtected() throws Exception {
		String tablename = "DeletedTable";
		String outputFilename = "tableProtected.odt";

		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Assert.assertNotNull(mOdtDoc);
		Table table = mOdtDoc.getTableByName(tablename);
		table.setProtected(true);
		mOdtDoc.save(ResourceUtilities.newTestOutputFile(outputFilename));

		mOdtDoc = loadODTDocument(outputFilename);
		table = mOdtDoc.getTableByName(tablename);
		Assert.assertTrue(table.isProtected());
	}

	@Test
	public void testGetCellByPosition() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		Table table = mOdtDoc.getTableByName("Table3");

		Cell cell = table.getCellByPosition(3, 3);
		Assert.assertNotNull(cell);
		Assert.assertEquals("string12", cell.getStringValue());
		cell = table.getCellByPosition("D4");
		Assert.assertNotNull(cell);
		Assert.assertEquals("string12", cell.getStringValue());
		// test index are negative numbers. They are illegal arguments.
		boolean illegalArgumentFlag = false;
		try {
			cell = table.getCellByPosition(-1, 0);
		} catch (IllegalArgumentException ie) {
			if ("colIndex and rowIndex should be nonnegative integer.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		// test TextTable automatically expands.
		// Table3 original size is 7 rows and 5 columns;
		// test row index 8 and column index 6, row index and column index both
		// out of bound, work well.
		cell = table.getCellByPosition(8, 6);
		Assert.assertNotNull(cell);
		cell.setStringValue("string86");
		Assert.assertEquals("string86", cell.getStringValue());
		// test row index 9 and column index 4, row index out of bound, work
		// well.
		cell = table.getCellByPosition(4, 9);
		Assert.assertNotNull(cell);
		cell.setStringValue("string49");
		Assert.assertEquals("string49", cell.getStringValue());
		// test row index 9 and column index 4, column index out of bound, work
		// well.
		cell = table.getCellByPosition(9, 10);
		Assert.assertNotNull(cell);
		cell.setStringValue("string910");
		Assert.assertEquals("string910", cell.getStringValue());
		// test column index out of bound, work well.
		cell = table.getCellByPosition("I4");
		Assert.assertNotNull(cell);
		cell.setStringValue("stringI4");
		Assert.assertEquals("stringI4", cell.getStringValue());
		// test row index out of bound, work well.
		cell = table.getCellByPosition("D11");
		Assert.assertNotNull(cell);
		cell.setStringValue("stringD11");
		Assert.assertEquals("stringD11", cell.getStringValue());
		// test row index and column index both out of bound, work well.
		cell = table.getCellByPosition("K12");
		Assert.assertNotNull(cell);
		cell.setStringValue("stringK12");
		Assert.assertEquals("stringK12", cell.getStringValue());
		// test TestSpreadsheetTable automatically expands.
		// Sheet1 original size is 6 rows and 9 columns;
		table = mOdsDoc.getTableByName("Sheet1");
		cell = table.getCellByPosition("C1");
		Assert.assertNotNull(cell);
		Assert.assertEquals("Currency", cell.getStringValue());
		cell = table.getCellByPosition("K4");
		Assert.assertNotNull(cell);
		cell.setBooleanValue(true);
		Assert.assertEquals(Boolean.TRUE, cell.getBooleanValue());
		cell = table.getCellByPosition("D10");
		Assert.assertNotNull(cell);
		Calendar cal = Calendar.getInstance();
		cell.setTimeValue(cal);
		SimpleDateFormat simpleFormat = new SimpleDateFormat("'PT'HH'H'mm'M'ss'S'");
		String expectedString = simpleFormat.format(cal.getTime());
		String targetString = simpleFormat.format(cell.getTimeValue().getTime());
		Assert.assertEquals(expectedString, targetString);
		cell = table.getCellByPosition("M15");
		Assert.assertNotNull(cell);
		cell.setStringValue("stringM15");
		Assert.assertEquals("stringM15", cell.getStringValue());
	}

	@Test
	public void testGetCellWithAutoExtend() {
		SpreadsheetDocument ods;
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			tbl.setTableName("Tests");
			Cell cell = tbl.getCellByPosition(5, 5);
			Assert.assertNotNull(cell);
			Assert.assertEquals(6, tbl.getRowCount());
			Assert.assertEquals(6, tbl.getColumnCount());
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetCellRangeByPosition() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		Table table = mOdtDoc.getTableByName("Table3");

		CellRange range = table.getCellRangeByPosition(0, 0, 3, 3);
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition("A1", "D4");
		Assert.assertNotNull(range);

		// test TextTable automatically expands.
		// Table3 original size is 7 rows and 5 columns;

		// test index is negative number. They are illegal arguments.
		boolean illegalArgumentFlag = false;
		try {
			range = table.getCellRangeByPosition(-1, 0, 2, -14);
		} catch (IllegalArgumentException ie) {
			if ("colIndex and rowIndex should be nonnegative integer.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		range = table.getCellRangeByPosition(0, 0, 8, 6);
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition(0, 0, 4, 9);
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition(0, 0, 9, 10);
		Assert.assertNotNull(range);
		// get cell range by address.
		range = table.getCellRangeByPosition("A1", "I4");
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition("A1", "D11");
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition("A1", "K12");
		Assert.assertNotNull(range);
		// test TestSpreadsheetTable automatically expands.
		// Sheet1 original size is 6 rows and 9 columns;
		// get cell range by index.
		table = mOdsDoc.getTableByName("Sheet1");
		range = table.getCellRangeByPosition("A1", "C1");
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition("B7", "K12");
		Assert.assertNotNull(range);
	}

	@Test
	public void testRemoveRowColumn() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Table table1 = mOdtDoc.getTableByName("Table1");
		int rowCount = table1.getRowCount();
		table1.removeRowsByIndex(1, 2);
		Assert.assertEquals(rowCount - 2, table1.getRowCount());

		Table table2 = mOdtDoc.getTableByName("Table2");
		int columnCount = table2.getColumnCount();
		table2.removeColumnsByIndex(2, 1);
		Assert.assertEquals(columnCount - 1, table2.getColumnCount());

		Table table3 = mOdtDoc.getTableByName("Table3");
		rowCount = table3.getRowCount();
		table3.removeRowsByIndex(0, 2);
		Assert.assertEquals(rowCount - 2, table3.getRowCount());

		saveodt(mOdtTestFileName + "Out.odt");

	}

	@Test
	public void testAppendRow() {
		OdfFileDom dom;
		try {
			SpreadsheetDocument odsDoc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestODSAppendRow.ods"));
			dom = odsDoc.getContentDom();
			NodeList tablelist = dom.getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(), "table");
			for (int i = 0; i < tablelist.getLength(); i++) {
				mOdsTable = (TableTableElement) tablelist.item(i);
				testAppendRow(mOdsTable);
			}
			odsDoc.save(ResourceUtilities.newTestOutputFile("TestODSAppendRowOutput.ods"));

			TextDocument odtDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestODTAppendRow.odt"));
			dom = odtDoc.getContentDom();
			tablelist = dom.getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(), "table");
			for (int i = 0; i < tablelist.getLength(); i++) {
				mOdtTable = (TableTableElement) tablelist.item(i);
				testAppendRow(mOdtTable);
			}
			odtDoc.save(ResourceUtilities.newTestOutputFile("TestODTAppendRowOutput.odt"));

		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAppendRowsWithCoveredCell() {
		SpreadsheetDocument odsDoc = null;
		Table table = null;
		try {
			odsDoc = SpreadsheetDocument.newSpreadsheetDocument();
			table = Table.newTable(odsDoc);
			mergeCells(table, 1, 1, 3, 2);
			mergeCells(table, 2, 4, 3, 3);
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testAppendRowsWithRowsRepeated() {
		SpreadsheetDocument odsDoc = null;
		Table table = null;
		try {
			odsDoc = SpreadsheetDocument.newSpreadsheetDocument();
			table = Table.newTable(odsDoc, 1, 1);
			table.appendRows(12);
			Row row10 = table.getRowByIndex(10);
			Row row11 = table.getRowByIndex(11);
			// default appended rows described by single element
			Assert.assertSame(row10.getOdfElement(), row11.getOdfElement());

			table.setUseRepeat(false);
			table.appendRows(12);
			Row row20 = table.getRowByIndex(20);
			Row row21 = table.getRowByIndex(21);
			Assert.assertNotSame(row20.getOdfElement(), row21.getOdfElement());

		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAppendColumnsWithColumnsRepeated() {
		SpreadsheetDocument odsDoc = null;
		Table table = null;
		try {
			odsDoc = SpreadsheetDocument.newSpreadsheetDocument();
			table = Table.newTable(odsDoc, 1, 1);
			table.appendColumns(12);
			Column column10 = table.getColumnByIndex(10);
			Column column11 = table.getColumnByIndex(11);

			Cell cell10 = table.getCellByPosition(10, 2);
			Cell cell11 = table.getCellByPosition(11, 2);

			// default appended rows described by single element
			Assert.assertSame(column10.getOdfElement(), column11.getOdfElement());
			Assert.assertSame(cell10.getOdfElement(), cell11.getOdfElement());

			table.setUseRepeat(false);
			table.appendColumns(12);
			Column column20 = table.getColumnByIndex(20);
			Column column21 = table.getColumnByIndex(21);

			Cell cell20 = table.getCellByPosition(20, 2);
			Cell cell21 = table.getCellByPosition(21, 2);

			Assert.assertNotSame(column20.getOdfElement(), column21.getOdfElement());
			Assert.assertNotSame(cell20.getOdfElement(), cell21.getOdfElement());

		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSplitCellAddress() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Table table1 = mOdtDoc.getTableByName("Table1");
		// reproduce bug 138, test case to proof the fix problem.
		// test address without table name.
		String[] address = table1.splitCellAddress("A1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("A", address[1]);
		Assert.assertEquals("1", address[2]);
		address = table1.splitCellAddress("AC1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("1", address[2]);
		address = table1.splitCellAddress("B34");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("B", address[1]);
		Assert.assertEquals("34", address[2]);
		address = table1.splitCellAddress("AC29");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("29", address[2]);

		// test relative address
		address = table1.splitCellAddress("Table1.A1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("A", address[1]);
		Assert.assertEquals("1", address[2]);
		address = table1.splitCellAddress("Table1.AC1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("1", address[2]);
		address = table1.splitCellAddress("Table1.B34");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("B", address[1]);
		Assert.assertEquals("34", address[2]);
		address = table1.splitCellAddress("Table1.AC29");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("29", address[2]);

		// test absolute address.
		address = table1.splitCellAddress("$Table1.$A$1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("A", address[1]);
		Assert.assertEquals("1", address[2]);
		address = table1.splitCellAddress("$Table1.$AC$1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("1", address[2]);
		address = table1.splitCellAddress("$Table1.$B$34");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("B", address[1]);
		Assert.assertEquals("34", address[2]);
		address = table1.splitCellAddress("$Table1.$AC$29");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("29", address[2]);
	}

	// Bug 97 - Row.getCellAt(int) returns null when the cell is a repeat cell
	@Test
	public void testGetCellAt() {
		try {
			SpreadsheetDocument doc = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("testGetCellAt.ods"));
			Table odfTable = doc.getTableList().get(0);
			Row valueRows = odfTable.getRowByIndex(0);
			for (int i = 0; i < 4; i++) {
				Cell cell = valueRows.getCellByIndex(i);
				Assert.assertNotNull(cell);
				int value = cell.getDoubleValue().intValue();
				Assert.assertEquals(1, value);
			}
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private void testAppendRow(TableTableElement table) {
		Table fTable = Table.getInstance(table);
		int count = fTable.getRowCount();
		fTable.appendRow();
		int newcount = fTable.getRowCount();
		Assert.assertEquals(count + 1, newcount);
	}

	private void saveods() {
		try {
			mOdsDoc.save(ResourceUtilities.newTestOutputFile(mOdsTestFileName + "Output.ods"));
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private void saveodt(String filename) {
		try {
			mOdtDoc.save(ResourceUtilities.newTestOutputFile(filename));
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private void mergeCells(Table table, int cellCol, int cellRow, int colSpan, int rowSpan) {
		if (table != null) {
			CellRange range = table.getCellRangeByPosition(cellCol, cellRow, cellCol + colSpan - 1, cellRow + rowSpan
					- 1);
			range.merge();
		}
	}
}
