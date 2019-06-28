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

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.style.props.OdfTableColumnProperties;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.type.PositiveLength;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class TableRowColumnTest {

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
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Test
	public void testSetSize() {
		Table table3 = odtdoc.getTableByName("Table3");
		//change the table height to 1/2
		for (int i = 0; i < table3.getRowCount(); i++) {
			Row row = table3.getRowByIndex(i);
			double oldHeight = row.getHeight() / 2;
			row.setHeight(oldHeight, true);
			double roundingFactor = 10000.0;
			double inValue = Math.round(roundingFactor * oldHeight / Unit.INCH.unitInMillimiter()) / roundingFactor;
			String sHeightIN = String.valueOf(inValue) + Unit.INCH.abbr();
			double expectedHeight = PositiveLength.parseDouble(sHeightIN, Unit.MILLIMETER);

			row.setHeight(oldHeight, false);
			Assert.assertEquals(expectedHeight, row.getHeight());
		}

		Table table1 = odtdoc.getTableByName("Table1");
		//change the table width to 1/2
		table1.setWidth(table1.getWidth() / 2);
		for (int i = 0; i < table1.getColumnCount(); i++) {
			Column column = table1.getColumnByIndex(i);
			double oldWidth = column.getWidth() / 2;
			column.setWidth(oldWidth);
			double roundingFactor = 10000.0;
			double inValue = Math.round(roundingFactor * oldWidth / Unit.INCH.unitInMillimiter()) / roundingFactor;
			String sWidthIN = String.valueOf(inValue) + Unit.INCH.abbr();
			double expectedWidth = PositiveLength.parseDouble(sWidthIN, Unit.MILLIMETER);
			Assert.assertEquals(expectedWidth, column.getWidth());
		}
		saveodt("ChangeSize");
	}

	/**
	 * The DecimalFormat will create the depicted "000,8571" with the comma as
	 * separator if used in a German Locale.
	 *
	 * Within org.odftoolkit.simple.table.Column.setWith(long) there is a
	 * getWith() call which ultimately calls
	 * org.odftoolkit.odfdom.type.Length.parseDouble(), where
	 * Double.valueOf(String s) is used to transform the String into a Double.
	 * This method uses "." as separator. Hence the Exception.
	 */
	@Test
	public void testGetLocaleColumnWidth() {
		Locale oldLocale = Locale.getDefault();
		try {
			Locale.setDefault(Locale.GERMAN);
			Table table = Table.newTable(odtdoc, 30, 7);
			table.setTableName("Table21");
			table.getColumnByIndex(2).setWidth(50); // NumberFormatException
			saveodt("LocalColumnWidth");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			Locale.setDefault(oldLocale);
		}
	}

	/**
	 * When a repeated column without width is split up, no width attribute should be set.
	 */
	@Test
	public void testSplitRepeatedColumns() {
		// test original width is null or null value.
		Table table5 = odtdoc.getTableByName("Table5");
		//columns 0-2 original width are null.
		table5.getColumnByIndex(0).splitRepeatedColumns();
		String columnWidth = table5.getColumnByIndex(1).maColumnElement.getProperty(OdfTableColumnProperties.ColumnWidth);
		Assert.assertNull(columnWidth);
		//columns 3-5 original width value are null.
		table5.getColumnByIndex(3).splitRepeatedColumns();
		columnWidth = table5.getColumnByIndex(4).maColumnElement.getProperty(OdfTableColumnProperties.ColumnWidth);
		Assert.assertNull(columnWidth);
		saveodt("SplitRepeatedColumns");
	}

	@Test
	public void testGetPreviousNext() {
		Table table2 = odtdoc.getTableByName("Table2");
		Row row2 = table2.getRowByIndex(2);
		Row row1 = table2.getRowByIndex(1);
		Row row0 = table2.getRowByIndex(0);
		Row lastRow = table2.getRowByIndex(table2.getRowCount() - 1);
		Row preRow = row2.getPreviousRow();
		Assert.assertTrue(preRow.equals(row1));
		Assert.assertTrue(row0.getPreviousRow() == null);
		Assert.assertEquals(row1.getNextRow(), row2);
		Assert.assertTrue(lastRow.getNextRow() == null);

		Table table1 = odtdoc.getTableByName("Table1");
		Column column2 = table1.getColumnByIndex(2);
		Column column1 = table1.getColumnByIndex(1);
		Column column0 = table1.getColumnByIndex(0);
		Column lastColumn = table1.getColumnByIndex(table1.getColumnCount() - 1);
		Column preColumn = column2.getPreviousColumn();
		Assert.assertTrue(column0.getPreviousColumn() == null);
		Assert.assertTrue(preColumn.equals(column1));
		Assert.assertEquals(column0.getNextColumn(), column1);
		Assert.assertTrue(lastColumn.getNextColumn() == null);
	}

	@Test
	public void testGetIndex() {
		//this is used to test the method for setColumnRepeatedIndex
		//merge the first two column, then the third column's index is 2
		Table table3 = odtdoc.getTableByName("Table3");
		Column column2OfT3 = table3.getColumnByIndex(2);
		CellRange range = table3.getCellRangeByPosition(0, 0, 1, table3.getRowCount() - 1);
		range.merge();
		Assert.assertTrue(column2OfT3.getColumnIndex() == 1);
		//insert two columns after the first column, then the next column's index add 2
		table3.insertColumnsBefore(1, 2);
		Assert.assertTrue(column2OfT3.getColumnIndex() == 3);
		//append a row
		Row newRow = table3.appendRow();
		int index = newRow.getRowIndex();
		Assert.assertTrue(index == (table3.getRowCount() - 1));
		//remove two row
//		table3.removeRowByIndex(0, 2);
//		Assert.assertTrue(newRow.getRowIndex() == (index - 2));
		//remove two colum
		table3.removeColumnsByIndex(1, 2);
		Assert.assertTrue(column2OfT3.getColumnIndex() == 1);

		//insert two column in the repeated columns
		Table table2 = odtdoc.getTableByName("Table2");
		Column column1OfT2 = table2.getColumnByIndex(1);
		List<Column> columns = table2.insertColumnsBefore(1, 2);
		Assert.assertTrue(column1OfT2.getColumnIndex() == 3);
		Assert.assertTrue(columns.get(0).getColumnIndex() == 1);
		Assert.assertEquals(column1OfT2.getPreviousColumn(), columns.get(1));
		Assert.assertEquals(column1OfT2.getNextColumn(), table2.getColumnByIndex(4));

		//append two row in the repeated rows
		Row row4OfT2 = table2.getRowByIndex(4);
		Row row3OfT2 = table2.getRowByIndex(3);
		List<Row> rows = table2.insertRowsBefore(4, 2);
		Cell cell1 = rows.get(0).getCellByIndex(0);
		cell1.setStringValue("cell1");
		Cell cell2 = rows.get(1).getCellByIndex(0);
		cell2.setStringValue("cell2");
		Assert.assertTrue(row4OfT2.getRowIndex() == 6);
		Assert.assertEquals(rows.get(0).getPreviousRow(), row3OfT2);
		//remove a row, then the next row will decrease the index
		table2.removeRowsByIndex(4, 1);
		Assert.assertNull(rows.get(0).getOdfElement());
		Assert.assertEquals(rows.get(1).getPreviousRow(), row3OfT2);
		saveodt("ChangeIndex");
	}

	@Test
	public void testCellGetRowIndex() {
		Table table3 = odtdoc.getTableByName("Table3");
		Cell cell = table3.getCellByPosition(0, 1);
		Assert.assertTrue(cell.getRowIndex() == 1);
		table3.removeRowsByIndex(0, 2);
	}

	@Test
	public void testGetInstance() {
		//get the text content contains "cell"
		TextNavigation search = new TextNavigation("cell", odtdoc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			OdfElement containerEle = item.getContainerElement();
			if (containerEle instanceof OdfTextParagraph) {
				Node ele = containerEle.getParentNode();
				if (ele instanceof TableTableCellElement) {
					Cell cell = Cell.getInstance((TableTableCellElementBase) ele);
					Assert.assertTrue(cell.getStringValue().contains("cell"));
					Assert.assertTrue(cell.getRowIndex() == 0);
					ele = ele.getParentNode();
					if (ele instanceof TableTableRowElement) {
						Row row = Row.getInstance((TableTableRowElement) ele);
						Assert.assertTrue(row == cell.getTableRow());
					}
				}
			}
		}
	}

	@Test
	public void testTableColumnWidth() {
		TextDocument textDocument = null;
		try {
			textDocument = TextDocument.newTextDocument();

			textDocument.getParagraphByIndex(0, false).setTextContent(
					"Above, table in header Standard (bug with getWidth, but the widths are good)");
			Table table = textDocument.getHeader().addTable(1, 8);
			long[] columnsWidth = new long[] { 5, 19, 11, 14, 27, 12, 75, 4 };
			setColumnsWidth(table, columnsWidth);
			textDocument.addParagraph(null);
			textDocument.addParagraph(null);

			textDocument.addParagraph("First table (columns width : demande / result)");
			table = Table.newTable(textDocument, 1, 6);
			columnsWidth = new long[] { 10, 15, 20, 25, 30, -1 };
			setColumnsWidth(table, columnsWidth);

			textDocument.addParagraph("Second table");
			table = Table.newTable(textDocument, 1, 8);
			columnsWidth = new long[] { 5, 19, 11, 14, 27, 12, 75, 4 };
			setColumnsWidth(table, columnsWidth);

			textDocument.addParagraph("Third table");
			table = Table.newTable(textDocument, 1, 12);
			columnsWidth = new long[] { 5, 8, 10, 11, 7, 7, 26, 12, 19, 19, 41, 3 };
			setColumnsWidth(table, columnsWidth);

			textDocument.addParagraph("fourth table (left space of 15mm and 10mm space right)");
			table = Table.newTable(textDocument, 1, 12, 1.5, 1.0);
			columnsWidth = new long[] { 5, 8, 10, 11, 7, 7, 11, 12, 9, 19, -1, 3 };
			setColumnsWidth(table, columnsWidth);

			textDocument.addParagraph("fifth table (space of -1 cm to the left and right space of -1.5cm)");
			table = Table.newTable(textDocument, 1, 10, -1, -1.5);
			columnsWidth = new long[] { 32, 8, 10, 11, 7, 7, 16, 12, -1, 18 };
			setColumnsWidth(table, columnsWidth);

			textDocument.addParagraph("sixth table (merging of columns 3-4, row 0, and other merging)");
			table = Table.newTable(textDocument, 2, 8);
			columnsWidth = new long[] { 5, 19, 11, 14, 27, 12, 75, 4 };
			setColumnsWidth(table, columnsWidth);
			CellRange vCellRange = table.getCellRangeByPosition(3, 0, 4, 0);
			vCellRange.merge();
			table.appendRows(2);
			vCellRange = table.getCellRangeByPosition(4, 1, 5, 2);
			vCellRange.merge();

			textDocument.addParagraph("as above, but no merging");
			table = Table.newTable(textDocument, 1, 8);
			columnsWidth = new long[] { 5, 19, 11, 14, 27, 12, 75, 4 };
			setColumnsWidth(table, columnsWidth);

			textDocument.addParagraph("table with three columns width at 0");
			table = Table.newTable(textDocument, 1, 12);
			columnsWidth = new long[] { 0, 13, 10, 11, 0, 14, 26, 12, 19, 19, 47, 0 };
			setColumnsWidth(table, columnsWidth);

			table = Table.newTable(textDocument, 1, 3);
			columnsWidth = new long[] { 100 };
			setColumnsWidth(table, columnsWidth);
			table.setVerticalMargin(0.25, 0.5);
			textDocument.addParagraph("Above, other table with top space of 0.25cm and bottom spacin of 0.5cm");

			textDocument.addParagraph(null);
			textDocument.addParagraph(null);
			textDocument.addParagraph("below, table in footer Standard (bug with getWidth, but the widths are good)");
			table = textDocument.getFooter().addTable(1, 8);
			columnsWidth = new long[] { 5, 19, 11, 14, 27, 12, 75, 4 };
			setColumnsWidth(table, columnsWidth);

			textDocument.save(ResourceUtilities.newTestOutputFile("testColumsWidth.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	private void setColumnsWidth(Table table, long[] columnsWidth){
		List<Column> vListColumns = table.getColumnList();
		for (int i = 0; i < columnsWidth.length; i++) {
			if(columnsWidth[i] >= 0){
				table.getColumnByIndex(i).setWidth(columnsWidth[i]);
			}
			table.getCellByPosition(i, 0).setStringValue(columnsWidth[i] + "/" + vListColumns.get(i).getWidth());
		}
	}

	private void saveods(String name) {
		try {
			odsdoc.save(ResourceUtilities.newTestOutputFile(filename + name + ".ods"));
		} catch (Exception e) {
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void saveodt(String name) {
		try {
			odtdoc.save(ResourceUtilities.newTestOutputFile(odtfilename + name + ".odt"));
		} catch (Exception e) {
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Test
	public void testGetInstanceColumn() {
		try {
			Table table2 = odtdoc.getTableByName("Table1");
			Column col = Column.getInstance(table2.getColumnByIndex(0).getOdfElement());
			Column col1 = table2.getColumnByIndex(0);

			Assert.assertEquals(col, col1);
		} catch (Exception e) {
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}


	@Test
	public void testGetColumnElementByIndex() {
		try {
			Table table2 = odtdoc.getTableByName("Table1");
			Row row = table2.getRowByIndex(1);
			//Cell cell = row.getCellByIndex(0);
			row.setHeight(4.3, true);
			row.setUseOptimalHeight(true);

			boolean flag = row.isOptimalHeight();
			Assert.assertTrue(flag);

		} catch (Exception e) {
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}
}
