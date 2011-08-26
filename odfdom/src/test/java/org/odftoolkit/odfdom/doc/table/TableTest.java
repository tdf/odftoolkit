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
package org.odftoolkit.odfdom.doc.table;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

public class TableTest {

	final String mOdsTestFileName = "TestSpreadsheetTable";
	final String mOdtTestFileName = "TestTextTable";
	OdfSpreadsheetDocument mOdsDoc;
	OdfTextDocument mOdtDoc;
	TableTableElement mOdsTable, mOdtTable;

	@Before
	public void setUp() {
		try {
			mOdsDoc = (OdfSpreadsheetDocument) OdfSpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(mOdsTestFileName + ".ods"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private OdfTextDocument loadODTDocument(String name) {
		try {
			OdfTextDocument odtdoc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(name));
			return odtdoc;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String tablename = "MyTable";
		try {
			OdfTextDocument document = OdfTextDocument.newTextDocument();
			document.newParagraph("Empty table:");
			OdfTable table = createEmptyTable(document);
			table.setTableName(tablename);
			Assert.assertEquals(tablename, table.getTableName());

			document.save(ResourceUtilities.newTestOutputFile("TestGetSetName.odt"));
			document = loadODTDocument("TestGetSetName.odt");
			table = document.getTableByName(tablename);
			Assert.assertNotNull(table);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetColumnByIndex() {

		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		OdfTable table = mOdtDoc.getTableByName("Table3");
		Assert.assertNotNull(table);
		OdfTableColumn column = table.getColumnByIndex(2);
		Assert.assertNotNull(column);
		Assert.assertEquals("string6", column.getCellByIndex(2).getStringValue());

	}

	@Test
	public void testGetRowByIndex() {
		testNewTable();
		mOdtDoc = loadODTDocument("CreateTableCase.odt");
		OdfTable table = mOdtDoc.getTableByName("Table3");
		Assert.assertNotNull(table);
		OdfTableRow row = table.getRowByIndex(3);
		Assert.assertNotNull(row);
		Assert.assertEquals("string12", row.getCellByIndex(3).getStringValue());
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

		table = mOdsDoc.getTableByName("Sheet1");
		cell = table.getCellByPosition("C1");
		Assert.assertNotNull(cell);
		Assert.assertEquals("Currency", cell.getStringValue());
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
			NodeList tablelist = dom.getElementsByTagNameNS(OdfNamespaceNames.TABLE.getUri(), "table");
			mOdsTable = (TableTableElement) tablelist.item(0);
			testAppendRow(mOdsTable);
			saveods();

			mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
			dom = mOdtDoc.getContentDom();
			tablelist = dom.getElementsByTagNameNS(OdfNamespaceNames.TABLE.getUri(), "table");
			for (int i = 0; i < tablelist.getLength(); i++) {
				mOdtTable = (TableTableElement) tablelist.item(i);
				testAppendRow(mOdtTable);
			}
			saveodt(mOdtTestFileName + "Out.odt");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSplitCellAddress() {
		mOdtDoc = loadODTDocument(mOdtTestFileName + ".odt");
		OdfTable table1 = mOdtDoc.getTableByName("Table1");
		//FIXME:bug 138, test case to proof the fix problem.
		//test address without table name.
		String[] address=table1.splitCellAddress("A1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("A", address[1]);
		Assert.assertEquals("1", address[2]);
		address=table1.splitCellAddress("AC1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("1", address[2]);
		address=table1.splitCellAddress("B34");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("B", address[1]);
		Assert.assertEquals("34", address[2]);
		address=table1.splitCellAddress("AC29");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("29", address[2]);
		
		//test relative address
		address=table1.splitCellAddress("Table1.A1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("A", address[1]);
		Assert.assertEquals("1", address[2]);
		address=table1.splitCellAddress("Table1.AC1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("1", address[2]);
		address=table1.splitCellAddress("Table1.B34");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("B", address[1]);
		Assert.assertEquals("34", address[2]);
		address=table1.splitCellAddress("Table1.AC29");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("29", address[2]);
		
		//test absolute address.
		address=table1.splitCellAddress("$Table1.$A$1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("A", address[1]);
		Assert.assertEquals("1", address[2]);
		address=table1.splitCellAddress("$Table1.$AC$1");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("1", address[2]);
		address=table1.splitCellAddress("$Table1.$B$34");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("B", address[1]);
		Assert.assertEquals("34", address[2]);
		address=table1.splitCellAddress("$Table1.$AC$29");
		Assert.assertEquals("Table1", address[0]);
		Assert.assertEquals("AC", address[1]);
		Assert.assertEquals("29", address[2]);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void saveodt(String filename) {
		try {
			mOdtDoc.save(ResourceUtilities.newTestOutputFile(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
