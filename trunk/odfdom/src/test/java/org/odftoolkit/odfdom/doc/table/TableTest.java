/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.doc.table;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderFooterPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TableTest {

	final static String mOdsTestFileName = "TestSpreadsheetTable";
	final static String mOdtTestFileName = "TestTextTable";
	OdfSpreadsheetDocument mOdsDoc;
	OdfTextDocument mOdtDoc;
	TableTableElement mOdsTable, mOdtTable;

	@Before
	public void setUp() {
		try {
			mOdsDoc = (OdfSpreadsheetDocument) OdfSpreadsheetDocument.loadDocument(ResourceUtilities.getAbsolutePath(mOdsTestFileName + ".ods"));
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private OdfTextDocument loadODTDocument(String name) {
		try {
			OdfTextDocument odtdoc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getAbsolutePath(name));
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
			OdfTextDocument document = OdfTextDocument.newTextDocument();
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
	public void testNewTableWithArrayData() {
		try {
			OdfSpreadsheetDocument spreadsheet = OdfSpreadsheetDocument.newSpreadsheetDocument();

			// reproduce bug 121
			int rowCount = 10, columnCount = 4;
			String[] rowLabels = getTestTableRowLabel(rowCount);
			String[] columnLabels = getTestTableColumnLabel(columnCount);
			double[][] doubleArray = null;
			String[][] stringArray = null;

			OdfTable table1 = OdfTable.newTable(spreadsheet, null, null,
					doubleArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT 2
			Assert.assertEquals(2, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT 5
			Assert.assertEquals(5, table1.getColumnCount());

			table1 = OdfTable.newTable(spreadsheet, rowLabels, columnLabels,
					doubleArray);
			Assert.assertEquals(1, table1.getHeaderColumnCount());
			Assert.assertEquals(1, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT+1 3
			Assert.assertEquals(3, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT+1 6
			Assert.assertEquals(6, table1.getColumnCount());

			table1 = OdfTable.newTable(spreadsheet, null, null, stringArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT 2
			Assert.assertEquals(2, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT 5
			Assert.assertEquals(5, table1.getColumnCount());

			table1 = OdfTable.newTable(spreadsheet, rowLabels, columnLabels,
					stringArray);
			Assert.assertEquals(1, table1.getHeaderColumnCount());
			Assert.assertEquals(1, table1.getHeaderRowCount());
			// row count should be DEFAULT_ROW_COUNT+1 3
			Assert.assertEquals(3, table1.getRowCount());
			// column count should be DEFAULT_COLUMN_COUNT+1 6
			Assert.assertEquals(6, table1.getColumnCount());

			doubleArray = getTestTableDataDouble(rowCount, columnCount);
			table1 = OdfTable.newTable(spreadsheet, null, null, doubleArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			Assert.assertEquals(rowCount, table1.getRowCount());
			Assert.assertEquals(columnCount, table1.getColumnCount());

			table1 = OdfTable.newTable(spreadsheet, rowLabels, columnLabels,
					doubleArray);
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
			table1 = OdfTable.newTable(spreadsheet, null, null, stringArray);
			Assert.assertEquals(0, table1.getHeaderColumnCount());
			Assert.assertEquals(0, table1.getHeaderRowCount());
			Assert.assertEquals(rowCount, table1.getRowCount());
			Assert.assertEquals(columnCount, table1.getColumnCount());

			table1 = OdfTable.newTable(spreadsheet, rowLabels, columnLabels,
					stringArray);
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
			OdfSpreadsheetDocument spreadsheet = OdfSpreadsheetDocument.newSpreadsheetDocument();
			OdfTable sheet = OdfTable.newTable(spreadsheet, 3, 5);
			TableTableHeaderColumnsElement headers = OdfElement.findFirstChildNode(TableTableHeaderColumnsElement.class,
					sheet.mTableElement);
			if (headers != null) {
				for (Node n : new DomNodeList(headers.getChildNodes())) {
					if (n instanceof TableTableColumnElement) {
						if (sheet.getColumnInstance(
								((TableTableColumnElement) n), 0).getColumnsRepeatedNumber() == 0) {
							Assert.fail("table:number-columns-repeated has the invalid value: '0'. It have to be a value matching the 'positiveInteger' type.");
						}
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private OdfTable createEmptyTable(OdfTextDocument document) {
		String tablename = "Table1";
		int rownumber = 5;
		int clmnumber = 3;

		OdfTable table1 = OdfTable.newTable(document, 5, 3);
		table1.setTableName(tablename);

		Assert.assertEquals(tablename, table1.getTableName());
		Assert.assertEquals(rownumber, table1.getRowCount());
		Assert.assertEquals(clmnumber, table1.getColumnCount());

		OdfTable table = document.getTableByName(tablename);
		Assert.assertNotNull(table);
		Assert.assertEquals(table1, table);
		return table1;
	}

	private OdfTable createTableWithData(OdfTextDocument document) {
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

		OdfTable table2 = OdfTable.newTable(document, rowlabels, columnlabels, data);
		table2.setTableName(tablename);

		Assert.assertEquals(1, table2.getHeaderColumnCount());
		Assert.assertEquals(1, table2.getHeaderRowCount());
		Assert.assertEquals(rowcount + 1, table2.getRowCount());
		Assert.assertEquals(columncount + 1, table2.getColumnCount());
		OdfTable table = document.getTableByName(tablename);
		Assert.assertNotNull(table);
		Assert.assertEquals(table2, table);

		OdfTableCell cell = table.getCellByPosition(1, 1);
		Assert.assertEquals("float", cell.getValueType());

		return table2;
	}

	private OdfTable createTableWithString(OdfTextDocument document) {
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

		OdfTable table3 = OdfTable.newTable(document, rowlabels, columnlabels, data);
		table3.setTableName(tablename);

		Assert.assertEquals(1, table3.getHeaderColumnCount());
		Assert.assertEquals(1, table3.getHeaderRowCount());
		Assert.assertEquals(rowcount + 1, table3.getRowCount());
		Assert.assertEquals(columncount + 1, table3.getColumnCount());
		OdfTable table = document.getTableByName(tablename);
		Assert.assertNotNull(table);
		Assert.assertEquals(table3, table);

		OdfTableCell cell = table.getCellByPosition(1, 1);
		Assert.assertEquals("string", cell.getValueType());


		return table3;

	}

	@Test
	public void testDeleteTable() {
		try {
			mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
			List<OdfTable> tableList = mOdtDoc.getTableList();
			int count = tableList.size();

			OdfTable table = mOdtDoc.getTableByName("DeletedTable");
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
			OdfTextDocument document = OdfTextDocument.newTextDocument();
			document.newParagraph("Empty table:");
			OdfTable table = createEmptyTable(document);
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
		List<OdfTable> tableList = mOdtDoc.getTableList();
		for (int i = 0; i < tableList.size(); i++) {
			OdfTable table = tableList.get(i);
			int clmnum = table.getColumnCount();
			table.appendColumn();
			Assert.assertEquals(clmnum + 1, table.getColumnCount());

			OdfTableColumn column = table.getColumnByIndex(clmnum);
			OdfTableColumn columnOld = table.getColumnByIndex(clmnum - 1);
			Assert.assertEquals(column.getCellCount(), columnOld.getCellCount());
		}
		saveodt(mOdtTestFileName + "Output.odt");
	}

	@Test
	public void testGetSetTablename() {
		String tablename = "My Table";
		OdfTextDocument document = null;
		try {
			document = OdfTextDocument.newTextDocument();
			document.newParagraph("Empty table:");
			OdfTable table = createEmptyTable(document);
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
			//new another table with the same name
			//an exception will be thrown
			OdfTable table2 = OdfTable.newTable(document);
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
	public void testInsertColumnBefore() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		OdfTable table1 = mOdtDoc.getTableByName("Table3");
		OdfTableCellRange range = table1.getCellRangeByPosition(0, 1, 1, 2);
		range.merge();

		int clmnum = table1.getColumnCount();
		OdfTableColumn oldClm1 = table1.getColumnByIndex(1);

		List<OdfTableColumn> columns = table1.insertColumnsBefore(1, 2);
		Assert.assertEquals(clmnum + 2, table1.getColumnCount());
		OdfTableColumn clm0 = table1.getColumnByIndex(0);
		OdfTableColumn clm1 = table1.getColumnByIndex(1);
		OdfTableColumn clm2 = table1.getColumnByIndex(2);
		OdfTableColumn clm3 = table1.getColumnByIndex(3);
		Assert.assertEquals(columns.get(0), clm1);
		Assert.assertEquals(columns.get(1), clm2);
		Assert.assertEquals(clm0.getCellCount(), clm1.getCellCount());
		Assert.assertEquals(clm1.getCellCount(), clm2.getCellCount());
		Assert.assertEquals(clm3, oldClm1);

		OdfTable table2 = mOdtDoc.getTableByName("Table2");
		OdfTableColumn oldClm0 = table2.getColumnByIndex(0);
		columns = table2.insertColumnsBefore(0, 2);

		OdfTableColumn newClm0 = table2.getColumnByIndex(0);
		OdfTableColumn newClm1 = table2.getColumnByIndex(1);
		OdfTableColumn newClm2 = table2.getColumnByIndex(2);
		Assert.assertEquals(newClm0.getCellCount(), newClm2.getCellCount());
		Assert.assertEquals(newClm1.getCellCount(), newClm2.getCellCount());
		Assert.assertEquals(newClm2, oldClm0);

		saveodt(mOdtTestFileName + "Out.odt");
	}

	@Test
	public void testRemoveColumnByIndex() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		OdfTable table1 = mOdtDoc.getTableByName("Table3");
		OdfTableCellRange range = table1.getCellRangeByPosition(0, 1, 1, 2);
		range.merge();

		int clmnum = table1.getColumnCount();
		OdfTableColumn oldClm0 = table1.getColumnByIndex(0);
		OdfTableColumn oldClm3 = table1.getColumnByIndex(3);
		table1.removeColumnsByIndex(1, 2);
		OdfTableColumn newClm0 = table1.getColumnByIndex(0);
		OdfTableColumn newClm1 = table1.getColumnByIndex(1);
		Assert.assertEquals(clmnum - 2, table1.getColumnCount());
		Assert.assertEquals(oldClm0, newClm0);
		Assert.assertEquals(oldClm3, newClm1);

		OdfTable table2 = mOdtDoc.getTableByName("Table4");
		clmnum = table2.getColumnCount();
		OdfTableColumn oldClm1 = table2.getColumnByIndex(2);
		table2.removeColumnsByIndex(0, 2);
		table2.removeColumnsByIndex(table2.getColumnCount() - 2, 2);
		OdfTableColumn clm0 = table2.getColumnByIndex(0);
		Assert.assertEquals(oldClm1, clm0);
		Assert.assertEquals(clmnum - 4, table2.getColumnCount());
		saveodt(mOdtTestFileName + "Out.odt");

	}

	@Test
	public void testInsertRowBefore() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		OdfTable table2 = mOdtDoc.getTableByName("Table2");
		OdfTableRow row = table2.getRowByIndex(0);
		int originalRowCount = table2.getRowCount();
		List<OdfTableRow> newRows = table2.insertRowsBefore(0, 2);


		OdfTableRow newRow1 = table2.getRowByIndex(0);
		OdfTableRow newRow2 = table2.getRowByIndex(0);
		Assert.assertEquals(newRow1.getCellCount(), newRows.get(0).getCellCount());
		Assert.assertEquals(newRow2.getCellCount(), newRows.get(1).getCellCount());
		//original row index 0
		Assert.assertEquals(row, table2.getRowByIndex(2));

		saveodt(mOdtTestFileName + "Out.odt");

		mOdtDoc = loadODTDocument(mOdtTestFileName + "Out.odt");
		OdfTable newTable = mOdtDoc.getTableByName("Table2");

		Assert.assertEquals(originalRowCount + 2, newTable.getRowCount());

	}

	@Test
	public void testGetColumnList() {
		String tablename = "MyTable";
		String testFileName = "TestGetColumnList.odt";
		try {
			OdfTextDocument document = OdfTextDocument.newTextDocument();

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

			OdfTable table3 = OdfTable.newTable(document, rowlabels, columnlabels, data);
			table3.setTableName(tablename);

			document.save(ResourceUtilities.newTestOutputFile(testFileName));
			document = loadODTDocument(testFileName);
			OdfTable table = document.getTableByName(tablename);
			OdfTableColumn tmpColumn;
			List<OdfTableColumn> columns = table.getColumnList();

			//the code below prints the column value,it shows that the first columns value is the same with the last column
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
				//each column's first cell is the column header
				Assert.assertEquals(columnlabels[i - 1], tmpColumn.getCellByIndex(0).getStringValue());
			}
			Assert.assertEquals(columncount, columns.size() - 1);

			Assert.assertEquals("", columns.get(0).getCellByIndex(0).getStringValue());

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
			OdfTextDocument document = OdfTextDocument.newTextDocument();

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

			OdfTable table3 = OdfTable.newTable(document, rowlabels, columnlabels, data);
			table3.setTableName(tablename);

			document.save(ResourceUtilities.newTestOutputFile(testFileName));
			document = loadODTDocument(testFileName);
			OdfTable table = document.getTableByName(tablename);
			OdfTableRow tmpRow;
			List<OdfTableRow> rows = table.getRowList();
			for (int i = 1; i < rows.size(); i++) {
				tmpRow = rows.get(i);
				//each row's first cell is the row header
				Assert.assertEquals(rowlabels[i - 1], tmpRow.getCellByIndex(0).getStringValue());
			}
			Assert.assertEquals(rowcount, rows.size() - 1);

			Assert.assertEquals("", rows.get(0).getCellByIndex(0).getStringValue());
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetColumnByIndex() {

		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		OdfTable table = mOdtDoc.getTableByName("Table3");
		Assert.assertNotNull(table);
		//test if index is negative number, which is an illegal argument.
		boolean illegalArgumentFlag = false;
		try {
			table.getColumnByIndex(-1);
		} catch (IllegalArgumentException ie) {
			if ("index should be nonnegative integer.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		OdfTableColumn column = table.getColumnByIndex(2);
		Assert.assertNotNull(column);
		Assert.assertEquals("string6", column.getCellByIndex(2).getStringValue());
		// test column automatically expands.
		// Table3 original size is 7 rows and 5 columns. this test case will
		// test row index 8 and columns index 6 are work well though they are
		// both out bound of the original table.
		column = table.getColumnByIndex(8);
		Assert.assertNotNull(column);
		OdfTableCell cell = column.getCellByIndex(6);
		Assert.assertNotNull(cell);
		cell.setStringValue("string86");
		Assert.assertEquals("string86", cell.getStringValue());
	}

	@Test
	public void testGetRowByIndex() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		OdfTable table = mOdtDoc.getTableByName("Table3");
		Assert.assertNotNull(table);
		//test index is negative number. This is a illegal argument.
		boolean illegalArgumentFlag = false;
		try {
			table.getRowByIndex(-1);
		} catch (IllegalArgumentException ie) {
			if ("index should be nonnegative integer.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		OdfTableRow row = table.getRowByIndex(3);
		Assert.assertNotNull(row);
		Assert.assertEquals("string12", row.getCellByIndex(3).getStringValue());
		// test row automatically expands.
		// Table3 original size is 7 rows and 5 columns. this test case will
		// test row index 8 and columns index 6 are work well though they are
		// both out bound of the original table.
		row = table.getRowByIndex(6);
		Assert.assertNotNull(row);
		OdfTableCell cell = row.getCellByIndex(8);
		Assert.assertNotNull(cell);
		cell.setStringValue("string86");
		Assert.assertEquals("string86", cell.getStringValue());
	}

	@Test
	public void testRemoveRowByIndex() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		OdfTable table2 = mOdtDoc.getTableByName("Table2");
		OdfTableRow row0 = table2.getRowByIndex(0);
		OdfTableRow row3 = table2.getRowByIndex(3);
		int originalRowCount = table2.getRowCount();
		table2.removeRowsByIndex(1, 2);

		//original row index 0
		Assert.assertEquals(row0, table2.getRowByIndex(0));
		Assert.assertEquals(row3, table2.getRowByIndex(1));

		saveodt(mOdtTestFileName + "Out.odt");

		mOdtDoc = loadODTDocument(mOdtTestFileName + "Out.odt");
		OdfTable newTable = mOdtDoc.getTableByName("Table2");

		Assert.assertEquals(originalRowCount - 2, newTable.getRowCount());
	}

	@Test
	public void testGetHeaderRowCount() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		OdfTable table = mOdtDoc.getTableByName("Table3");
		int headerRowCount = table.getHeaderRowCount();
		Assert.assertEquals(1, headerRowCount);
	}

	@Test
	public void testGetHeaderColumnCount() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		OdfTable table = mOdtDoc.getTableByName("Table3");
		int headerColumnCount = table.getHeaderColumnCount();
		Assert.assertEquals(1, headerColumnCount);

	}

	@Test
	public void testIsProtected() throws Exception {
		String tablename = "DeletedTable";
		String outputFilename = "tableProtected.odt";

		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		Assert.assertNotNull(mOdtDoc);
		OdfTable table = mOdtDoc.getTableByName(tablename);
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
		OdfTable table = mOdtDoc.getTableByName(tablename);
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
		OdfTable table = mOdtDoc.getTableByName("Table3");

		OdfTableCell cell = table.getCellByPosition(3, 3);
		Assert.assertNotNull(cell);
		Assert.assertEquals("string12", cell.getStringValue());
		cell = table.getCellByPosition("D4");
		Assert.assertNotNull(cell);
		Assert.assertEquals("string12", cell.getStringValue());
		//test index are negative numbers. They are illegal arguments.
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
		//test row index 8 and column index 6, row index and column index both out of bound, work well. 
		cell = table.getCellByPosition(8, 6);
		Assert.assertNotNull(cell);
		cell.setStringValue("string86");
		Assert.assertEquals("string86", cell.getStringValue());
		//test row index 9 and column index 4, row index out of bound, work well.
		cell = table.getCellByPosition(4, 9);
		Assert.assertNotNull(cell);
		cell.setStringValue("string49");
		Assert.assertEquals("string49", cell.getStringValue());
		//test row index 9 and column index 4, column index out of bound, work well.
		cell = table.getCellByPosition(9, 10);
		Assert.assertNotNull(cell);
		cell.setStringValue("string910");
		Assert.assertEquals("string910", cell.getStringValue());
		//test column index out of bound, work well.
		cell = table.getCellByPosition("I4");
		Assert.assertNotNull(cell);
		cell.setStringValue("stringI4");
		Assert.assertEquals("stringI4", cell.getStringValue());
		//test row index out of bound, work well.
		cell = table.getCellByPosition("D11");
		Assert.assertNotNull(cell);
		cell.setStringValue("stringD11");
		Assert.assertEquals("stringD11", cell.getStringValue());
		//test row index and column index both out of bound, work well. 
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
		SimpleDateFormat simpleFormat = new SimpleDateFormat(
				"'PT'HH'H'mm'M'ss'S'");
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
		OdfSpreadsheetDocument ods;
		try {
			ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
			OdfTable tbl = ods.getTableByName("Sheet1");
			tbl.setTableName("Tests");
			OdfTableCell cell = tbl.getCellByPosition(5, 5);
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
		OdfTable table = mOdtDoc.getTableByName("Table3");

		OdfTableCellRange range = table.getCellRangeByPosition(0, 0, 3, 3);
		Assert.assertNotNull(range);
		range = table.getCellRangeByPosition("A1", "D4");
		Assert.assertNotNull(range);

		// test TextTable automatically expands.
		// Table3 original size is 7 rows and 5 columns;

		//test index is negative number. They are illegal arguments.
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
		OdfTable table1 = mOdtDoc.getTableByName("Table1");
		int rowCount = table1.getRowCount();
		table1.removeRowsByIndex(1, 2);
		Assert.assertEquals(rowCount - 2, table1.getRowCount());

		OdfTable table2 = mOdtDoc.getTableByName("Table2");
		int columnCount = table2.getColumnCount();
		table2.removeColumnsByIndex(2, 1);
		Assert.assertEquals(columnCount - 1, table2.getColumnCount());

		OdfTable table3 = mOdtDoc.getTableByName("Table3");
		rowCount = table3.getRowCount();
		table3.removeRowsByIndex(0, 2);
		Assert.assertEquals(rowCount - 2, table3.getRowCount());

		saveodt(mOdtTestFileName + "Out.odt");

	}

	@Test
	public void testAppendRow() {
		OdfFileDom dom;
		try {
			dom = mOdsDoc.getContentDom();
			NodeList tablelist = dom.getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(), "table");
			mOdsTable = (TableTableElement) tablelist.item(0);
			testAppendRow(mOdsTable);
			saveods();

			mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
			dom = mOdtDoc.getContentDom();
			tablelist = dom.getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(), "table");
			for (int i = 0; i < tablelist.getLength(); i++) {
				mOdtTable = (TableTableElement) tablelist.item(i);
				testAppendRow(mOdtTable);
			}
			saveodt(mOdtTestFileName + "Out.odt");

		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSplitCellAddress() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		OdfTable table1 = mOdtDoc.getTableByName("Table1");
		//reproduce bug 138, test case to proof the fix problem.
		//test address without table name.
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

		//test relative address
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

		//test absolute address.
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

	// Bug 97 - OdfTableRow.getCellAt(int) returns null when the cell is a repeat cell
	@Test
	public void testGetCellAt() {
		try {
			OdfSpreadsheetDocument doc = (OdfSpreadsheetDocument) OdfSpreadsheetDocument.loadDocument(ResourceUtilities.getAbsolutePath("testGetCellAt.ods"));
			OdfTable odfTable = doc.getTableList().get(0);
			OdfTableRow valueRows = odfTable.getRowByIndex(0);
			for (int i = 0; i < 4; i++) {
				OdfTableCell cell = valueRows.getCellByIndex(i);
				Assert.assertNotNull(cell);
				int value = cell.getDoubleValue().intValue();
				Assert.assertEquals(1, value);
			}
		} catch (Exception e) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	private String[] getTestTableRowLabel(int rowCount) {
		String[] rowLabels = new String[rowCount];
		for (int i = 0; i < rowCount; i++) {
			rowLabels[i] = "RowHeader" + i;
		}
		return rowLabels;
	}

	private String[] getTestTableColumnLabel(int columnCount) {
		String[] columnLabels = new String[columnCount];
		for (int i = 0; i < columnCount; i++) {
			columnLabels[i] = "columnHeader" + i;
		}
		return columnLabels;

	}

	private double[][] getTestTableDataDouble(int rowCount, int columnCount) {
		double[][] doubleArray = new double[rowCount][columnCount];
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				doubleArray[i][j] = i * j;
			}
		}
		return doubleArray;
	}

	// Bug 294 Enable Tables to be inserted in Header Footer
	@Test
	public void testTableInHeaderFooter() {
		try {
			OdfDocument odfDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(mOdtTestFileName + ".odt"));
			Map<String, StyleMasterPageElement> masterPages1 = odfDoc.getMasterPages();
			StyleMasterPageElement masterPage1 = masterPages1.get("Standard");
			Assert.assertNotNull(masterPage1);
			int rowCount = 4;
			int columnCount = 5;
			OdfTable.newTable(masterPage1.newStyleHeaderElement(), getTestTableRowLabel(rowCount), getTestTableRowLabel(columnCount), getTestTableDataDouble(rowCount, columnCount));
			OdfTable.newTable(masterPage1.newStyleFooterElement(), getTestTableRowLabel(rowCount), getTestTableRowLabel(columnCount), getTestTableDataDouble(rowCount, columnCount));

			// ToDo: Should be added as test when header/footer styles are supported in ODFDOM
//			HashMap<String, String> pageProps1 = getPageStyleProps(odfDoc, masterPage1);
//			HashMap<String, String> footerProps1 = getFooterStyleProps(odfDoc, masterPage1);
//			HashMap<String, String> headerProps1 = getHeaderStyleProps(odfDoc, masterPage1);

			odfDoc.save(ResourceUtilities.newTestOutputFile("TestHeaderFooter.odt"));
			odfDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestHeaderFooter.odt"));
			Map<String, StyleMasterPageElement> masterPages2 = odfDoc.getMasterPages();
			StyleMasterPageElement masterPage2 = masterPages2.get("Standard");

				// Test if the new footer exists
			StyleHeaderElement headerContentRoot2 = OdfElement.findFirstChildNode(StyleHeaderElement.class, masterPage2);
			Assert.assertNotNull(headerContentRoot2);
			StyleFooterElement footerContentRoot2 = OdfElement.findFirstChildNode(StyleFooterElement.class, masterPage2);
			Assert.assertNotNull(footerContentRoot2);
			
		} catch (Exception ex) {
			Logger.getLogger(TableTest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	private Map<String, StyleMasterPageElement> getMasterPages(OdfDocument doc) throws Exception {
	
		OdfStylesDom stylesDoc = doc.getStylesDom();
		OfficeMasterStylesElement masterStyles = OdfElement.findFirstChildNode(OfficeMasterStylesElement.class, stylesDoc.getRootElement());
		Map<String, StyleMasterPageElement> masterPages = null;
		if (masterStyles != null) {
			NodeList lstMasterPages = stylesDoc.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(), "master-page");
			if (lstMasterPages != null && lstMasterPages.getLength() > 0) {
				masterPages = new HashMap<String, StyleMasterPageElement>();
				for (int i = 0; i < lstMasterPages.getLength(); i++) {
					StyleMasterPageElement masterPage = (StyleMasterPageElement) lstMasterPages.item(i); //Take the node from the list
					// ODFDOM ToDo?: Drop Attribute Suffix for methods returning String values and NOT Attributes
					// ODFDOM ToDo?: Why is a method with Attirbute ending returng the value? BETTER: Drop the suffix?
					String styleName = masterPage.getStyleNameAttribute();
					masterPages.put(styleName, masterPage);
				}
			}
		}
		return masterPages;
	}

	// ODFDOM ToDo: http://odftoolkit.org/bugzilla/show_bug.cgi?id=293
	// 293 - Adding optional Maps to generated ODF sources for indexed ODF elements
	// Method To be moved on StyleMasterPageElement
	private HashMap<String, String> getPageStyleProps(OdfDocument odfDoc, StyleMasterPageElement masterPage) throws Exception {
		StylePageLayoutElement pageLayout = getMasterPageLayout(odfDoc, masterPage);

		// ToDo: Access methods for MasterPage children NOT available!! & drop prefix/suffix
		StylePageLayoutPropertiesElement pagePropsElement = OdfElement.findFirstChildNode(StylePageLayoutPropertiesElement.class, pageLayout);
		Assert.assertNotNull(pagePropsElement);

		// fill map with header attributes name/values
		HashMap<String, String> pageProps = new HashMap<String, String>();
		NamedNodeMap pageAttrs = pagePropsElement.getAttributes();
		for (int i = 0; i < pageAttrs.getLength(); i++) {
			pageProps.put(pageAttrs.item(i).getNamespaceURI() + pageAttrs.item(i).getLocalName(), pageAttrs.item(i).getNodeValue());
		}
		return pageProps;
	}

	// ODFDOM ToDo: http://odftoolkit.org/bugzilla/show_bug.cgi?id=293
	// 293 - Adding optional Maps to generated ODF sources for indexed ODF elements
	// Method To be moved on StyleMasterPageElement
	private HashMap<String, String> getHeaderStyleProps(OdfDocument odfDoc, StyleMasterPageElement masterPage) throws Exception {
		StylePageLayoutElement pageLayout = getMasterPageLayout(odfDoc, masterPage);
		// ToDo: Combine a GETTER for header Properties in one method
		StyleHeaderStyleElement headerStyle = OdfElement.findFirstChildNode(StyleHeaderStyleElement.class, pageLayout);
		Assert.assertNotNull(headerStyle);
		StyleHeaderFooterPropertiesElement headerStyleProps = OdfElement.findFirstChildNode(StyleHeaderFooterPropertiesElement.class, headerStyle);
		Assert.assertNotNull(headerStyleProps);
		// fill map with header attributes name/values
		HashMap<String, String> headerProps = new HashMap<String, String>();
		NamedNodeMap headerAttrs = headerStyleProps.getAttributes();
		for (int i = 0; i < headerAttrs.getLength(); i++) {
			headerProps.put(headerAttrs.item(i).getNamespaceURI() + headerAttrs.item(i).getLocalName(), headerAttrs.item(i).getNodeValue());
		}
		return headerProps;
	}

	// ODFDOM ToDo: http://odftoolkit.org/bugzilla/show_bug.cgi?id=293
	// 293 - Adding optional Maps to generated ODF sources for indexed ODF elements
	// Method To be moved on StyleMasterPageElement
	private HashMap<String, String> getFooterStyleProps(OdfDocument odfDoc, StyleMasterPageElement masterPage) throws Exception {
		StylePageLayoutElement pageLayout = getMasterPageLayout(odfDoc, masterPage);
		// ODFDOM ToDo: Combine a GETTER for footer Properties in one method
		StyleFooterStyleElement footerStyle = OdfElement.findFirstChildNode(StyleFooterStyleElement.class, pageLayout);
		Assert.assertNotNull(footerStyle);
		StyleHeaderFooterPropertiesElement footerStyleProps = OdfElement.findFirstChildNode(StyleHeaderFooterPropertiesElement.class, footerStyle);
		Assert.assertNotNull(footerStyleProps);

		// fill map with header attributes name/values
		HashMap<String, String> footerProps = new HashMap<String, String>();
		NamedNodeMap footerAttrs = footerStyleProps.getAttributes();
		for (int i = 0; i < footerAttrs.getLength(); i++) {
			footerProps.put(footerAttrs.item(i).getNamespaceURI() + footerAttrs.item(i).getLocalName(), footerAttrs.item(i).getNodeValue());
		}
		return footerProps;
	}

	// ODFDOM ToDo: http://odftoolkit.org/bugzilla/show_bug.cgi?id=292
	// 292 - Usability: Generated ODF classes shall provide getter to element children
	// Method should be generated per se
	private StylePageLayoutElement getMasterPageLayout(OdfDocument odfDoc, StyleMasterPageElement masterPage) throws Exception {
		// ODFDOM ToDo: Drop StylePageLayout as convenient and move those functions to convenient DOM part
		// ODFDOM ToDo: Drop "Odf" Prefix, Drop Styles Prefix
		// ODFDOM ToDo: Add methods to dedicated generated classes? (e.g. OfficeAutomaticStylesElement ?) BEST -- fill into a existing Java tempalte?
		String pageLayoutName = masterPage.getStylePageLayoutNameAttribute();
		Assert.assertNotNull(pageLayoutName);
		StylePageLayoutElement pageLayout = odfDoc.getStylesDom().getAutomaticStyles().getPageLayout(pageLayoutName);
		Assert.assertNotNull(pageLayout);
		return pageLayout;
	}

	private void testAppendRow(TableTableElement table) {
		OdfTable fTable = OdfTable.getInstance(table);
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
}
