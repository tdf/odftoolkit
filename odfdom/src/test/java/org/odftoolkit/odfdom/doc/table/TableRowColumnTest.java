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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.style.props.OdfTableColumnProperties;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.search.TextNavigation;
import org.odftoolkit.odfdom.incubator.search.TextSelection;
import org.odftoolkit.odfdom.type.PositiveLength;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class TableRowColumnTest {

	final String filename = "TestSpreadsheetTable";
	final String odtfilename = "TestTextTable";
	OdfSpreadsheetDocument odsdoc;
	OdfTextDocument odtdoc;
	OdfTable odsTable, odtTable;

	@Before
	public void setUp() {
		try {
			odsdoc = (OdfSpreadsheetDocument) OdfSpreadsheetDocument.loadDocument(ResourceUtilities.getAbsolutePath(filename + ".ods"));
			odtdoc = (OdfTextDocument) OdfTextDocument.loadDocument(ResourceUtilities.getAbsolutePath(odtfilename + ".odt"));
		} catch (Exception e) {
			Logger.getLogger(TableRowColumnTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Test
	public void testSetSize() {
		OdfTable table3 = odtdoc.getTableByName("Table3");
		//change the table height to 1/2
		for (int i = 0; i < table3.getRowCount(); i++) {
			OdfTableRow row = table3.getRowByIndex(i);
			long oldHeight = row.getHeight() / 2;
			row.setHeight(oldHeight, false);
			String sHeightMM = String.valueOf(oldHeight) + Unit.MILLIMETER.abbr();
			String sHeightIN = PositiveLength.mapToUnit(sHeightMM, Unit.INCH);
			long expectedHeight = PositiveLength.parseLong(sHeightIN, Unit.MILLIMETER);
			Assert.assertEquals(expectedHeight, row.getHeight());
		}

		OdfTable table1 = odtdoc.getTableByName("Table1");
		//change the table width to 1/2
		table1.setWidth(table1.getWidth() / 2);
		for (int i = 0; i < table1.getColumnCount(); i++) {
			OdfTableColumn column = table1.getColumnByIndex(i);
			long oldWidth = column.getWidth() / 2;
			column.setWidth(oldWidth);
			String sWidthMM = String.valueOf(oldWidth) + Unit.MILLIMETER.abbr();
			String sWidthIN = PositiveLength.mapToUnit(sWidthMM, Unit.INCH);
			long expectedWidth = PositiveLength.parseLong(sWidthIN, Unit.MILLIMETER);
			Assert.assertEquals(expectedWidth, column.getWidth());
		}
		saveodt("ChangeSize");
	}

	/**
	 * When a repeated column without width is split up, no width attribute should be set.
	 */
	@Test
	public void testSplitRepeatedColumns() {
		// test original width is null or null value.
		OdfTable table5 = odtdoc.getTableByName("Table5");
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
		OdfTable table2 = odtdoc.getTableByName("Table2");
		OdfTableRow row2 = table2.getRowByIndex(2);
		OdfTableRow row1 = table2.getRowByIndex(1);
		OdfTableRow row0 = table2.getRowByIndex(0);
		OdfTableRow lastRow = table2.getRowByIndex(table2.getRowCount() - 1);
		OdfTableRow preRow = row2.getPreviousRow();
		Assert.assertTrue(preRow.equals(row1));
		Assert.assertTrue(row0.getPreviousRow() == null);
		Assert.assertEquals(row1.getNextRow(), row2);
		Assert.assertTrue(lastRow.getNextRow() == null);

		OdfTable table1 = odtdoc.getTableByName("Table1");
		OdfTableColumn column2 = table1.getColumnByIndex(2);
		OdfTableColumn column1 = table1.getColumnByIndex(1);
		OdfTableColumn column0 = table1.getColumnByIndex(0);
		OdfTableColumn lastColumn = table1.getColumnByIndex(table1.getColumnCount() - 1);
		OdfTableColumn preColumn = column2.getPreviousColumn();
		Assert.assertTrue(column0.getPreviousColumn() == null);
		Assert.assertTrue(preColumn.equals(column1));
		Assert.assertEquals(column0.getNextColumn(), column1);
		Assert.assertTrue(lastColumn.getNextColumn() == null);
	}

	@Test
	public void testGetIndex() {
		//this is used to test the method for setColumnRepeatedIndex
		//merge the first two column, then the third column's index is 2
		OdfTable table3 = odtdoc.getTableByName("Table3");
		OdfTableColumn column2OfT3 = table3.getColumnByIndex(2);
		OdfTableCellRange range = table3.getCellRangeByPosition(0, 0, 1, table3.getRowCount() - 1);
		range.merge();
		Assert.assertTrue(column2OfT3.getColumnIndex() == 1);
		//insert two columns after the first column, then the next column's index add 2
		table3.insertColumnsBefore(1, 2);
		Assert.assertTrue(column2OfT3.getColumnIndex() == 3);
		//append a row
		OdfTableRow newRow = table3.appendRow();
		int index = newRow.getRowIndex();
		Assert.assertTrue(index == (table3.getRowCount() - 1));
		//remove two row
//		table3.removeRowByIndex(0, 2);
//		Assert.assertTrue(newRow.getRowIndex() == (index - 2));
		//remove two colum
		table3.removeColumnsByIndex(1, 2);
		Assert.assertTrue(column2OfT3.getColumnIndex() == 1);

		//insert two column in the repeated columns
		OdfTable table2 = odtdoc.getTableByName("Table2");
		OdfTableColumn column1OfT2 = table2.getColumnByIndex(1);
		List<OdfTableColumn> columns = table2.insertColumnsBefore(1, 2);
		Assert.assertTrue(column1OfT2.getColumnIndex() == 3);
		Assert.assertTrue(columns.get(0).getColumnIndex() == 1);
		Assert.assertEquals(column1OfT2.getPreviousColumn(), columns.get(1));
		Assert.assertEquals(column1OfT2.getNextColumn(), table2.getColumnByIndex(4));

		//append two row in the repeated rows
		OdfTableRow row4OfT2 = table2.getRowByIndex(4);
		OdfTableRow row3OfT2 = table2.getRowByIndex(3);
		List<OdfTableRow> rows = table2.insertRowsBefore(4, 2);
		OdfTableCell cell1 = rows.get(0).getCellByIndex(0);
		cell1.setStringValue("cell1");
		OdfTableCell cell2 = rows.get(1).getCellByIndex(0);
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
		OdfTable table3 = odtdoc.getTableByName("Table3");
		OdfTableCell cell = table3.getCellByPosition(0, 1);
		Assert.assertTrue(cell.getRowIndex() == 1);
		table3.removeRowsByIndex(0, 2);
	}

	@Test
	public void testGetInstance() {
		//get the text content contains "cell"
		TextNavigation search = new TextNavigation("cell", odtdoc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.getCurrentItem();
			OdfElement containerEle = item.getContainerElement();
			if (containerEle instanceof OdfTextParagraph) {
				Node ele = containerEle.getParentNode();
				if (ele instanceof TableTableCellElement) {
					OdfTableCell cell = OdfTableCell.getInstance((TableTableCellElementBase) ele);
					Assert.assertTrue(cell.getStringValue().contains("cell"));
					Assert.assertTrue(cell.getRowIndex() == 0);
					ele = ele.getParentNode();
					if (ele instanceof TableTableRowElement) {
						OdfTableRow row = OdfTableRow.getInstance((TableTableRowElement) ele);
						Assert.assertTrue(row == cell.getTableRow());
					}
				}
			}
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
}
