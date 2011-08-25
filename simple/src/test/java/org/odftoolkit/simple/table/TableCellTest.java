/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009, 2010 IBM. All rights reserved.
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

import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class TableCellTest {

	final static String SAMPLE_SPREADSHEET = "TestSpreadsheetTable";
	final static String SAMPLE_STYLE_SPREADSHEET = "TestSpreadsheetStyleTable";
	final static String SAMPLE_TEXT = "TestTextTable";
	SpreadsheetDocument odsdoc, odsstyle;
	TextDocument odtdoc;
	Table odsTable, odtTable;

	@Before
	public void setUp() {
		try {
			odsdoc = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_SPREADSHEET + ".ods"));
			odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(SAMPLE_TEXT
					+ ".odt"));
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void saveods() {
		try {
			odsdoc.save(ResourceUtilities.newTestOutputFile(SAMPLE_SPREADSHEET + "Output.ods"));

		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Test
	public void testGetIndexInRowColumn() {
		int rowindex = 2, columnindex = 1;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell cell = table.getCellByPosition(columnindex, rowindex);

		Assert.assertEquals(rowindex, cell.getRowIndex());
		Assert.assertEquals(columnindex, cell.getColumnIndex());

		odtdoc.getTableByName("Table3");
		Cell cell1 = table.getCellByPosition(0, 1);
		Assert.assertEquals(1, cell1.getRowIndex());
		Assert.assertEquals(0, cell1.getColumnIndex());
	}

	@Test
	public void testGetSetHoriAlignment() {
		int rowindex = 3, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		HorizontalAlignmentType align = fcell.getHorizontalAlignmentType();
		Assert.assertEquals(HorizontalAlignmentType.CENTER, align);

		fcell.setHorizontalAlignment(HorizontalAlignmentType.DEFAULT);
		HorizontalAlignmentType newAlign = fcell.getHorizontalAlignmentType();
		Assert.assertEquals(HorizontalAlignmentType.DEFAULT, newAlign);

		fcell.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
		align = fcell.getHorizontalAlignmentType();
		Assert.assertEquals(HorizontalAlignmentType.LEFT, align);

		// "left" and "right" should be mapped as "start" and "end".
		fcell.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
		// get string
		String aligns = fcell.getHorizontalAlignment();
		Assert.assertEquals("start", aligns);
		// get type
		align = fcell.getHorizontalAlignmentType();
		Assert.assertEquals(HorizontalAlignmentType.LEFT, align);
		fcell.setHorizontalAlignment(HorizontalAlignmentType.RIGHT);
		// get string
		aligns = fcell.getHorizontalAlignment();
		Assert.assertEquals("end", aligns);
		// get type
		align = fcell.getHorizontalAlignmentType();
		Assert.assertEquals(HorizontalAlignmentType.RIGHT, align);
		saveods();

		SpreadsheetDocument ods;
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell = tbl.getCellByPosition(0, 0);
			HorizontalAlignmentType horizonAlignment = cell.getHorizontalAlignmentType();
			Assert.assertEquals(HorizontalAlignmentType.DEFAULT, horizonAlignment);

			table = ods.getTableByName("Sheet2");
			if (table != null) {
				table.remove();
			}
			table = Table.newTable(ods);
			table.setTableName("Sheet2");
			cell = table.getCellByPosition(1, 1);
			cell.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
			horizonAlignment = cell.getHorizontalAlignmentType();
			Assert.assertEquals(HorizontalAlignmentType.CENTER, horizonAlignment);
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetVertAlignment() {
		int rowindex = 3, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		StyleTypeDefinitions.VerticalAlignmentType align = fcell.getVerticalAlignmentType();
		Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.TOP, align);

		fcell.setVerticalAlignment(VerticalAlignmentType.DEFAULT);
		StyleTypeDefinitions.VerticalAlignmentType newAlign = fcell.getVerticalAlignmentType();
		Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.DEFAULT, newAlign);

		fcell.setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType.BOTTOM);
		align = fcell.getVerticalAlignmentType();
		Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.BOTTOM, align);
		saveods();
		SpreadsheetDocument ods;
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell = tbl.getCellByPosition(0, 0);
			StyleTypeDefinitions.VerticalAlignmentType verticalAlignment = cell.getVerticalAlignmentType();
			Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.DEFAULT, verticalAlignment);

			table = ods.getTableByName("Sheet2");
			if (table != null) {
				table.remove();
			}
			table = Table.newTable(ods);
			table.setTableName("Sheet2");
			cell = table.getCellByPosition(1, 1);
			cell.setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType.TOP);
			verticalAlignment = cell.getVerticalAlignmentType();
			Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.TOP, verticalAlignment);
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetValueType() {
		int rowindex = 3, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		boolean illegalArgumentFlag = false;
		try {
			fcell.setValueType(null);
		} catch (IllegalArgumentException ie) {
			if ("type shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);

		fcell.setValueType("date");
		String valueType = fcell.getValueType();
		Assert.assertEquals("date", valueType);
		saveods();

		SpreadsheetDocument ods;
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell = tbl.getCellByPosition(0, 0);
			valueType = cell.getValueType();
			Assert.assertEquals(null, valueType);
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetWrapOption() {
		int rowindex = 5, columnindex = 8;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		boolean wrap = fcell.isTextWrapped();
		Assert.assertEquals(true, wrap);

		fcell.setTextWrapped(false);
		wrap = fcell.isTextWrapped();
		Assert.assertEquals(false, wrap);
		saveods();
	}

	@Test
	public void testGetSetTextValue() {
		int rowindex = 5, columnindex = 8;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

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
		saveods();

		Table table1 = odtdoc.getTableByName("Table1");
		Cell fcell2 = table1.getCellByPosition(0, 1);
		text = fcell2.getDisplayText();
		Assert.assertEquals("Aa\rbb\rcc\rdd\ree", text);
	}

	@Test
	public void testSetGetFormat() {
		int rowindex = 3, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		fcell.setFormatString("#0.0");
		String displayvalue = fcell.getDisplayText();
		Assert.assertEquals("300" + (new DecimalFormatSymbols()).getDecimalSeparator() + "0", displayvalue);
		String format = fcell.getFormatString();
		Assert.assertEquals("#0.0", format);

		Cell dcell = table.getCellByPosition(3, 2);
		format = dcell.getFormatString();
		Assert.assertEquals("MMM d, yy", format);

		dcell.setFormatString("yyyy-MM-dd");
		displayvalue = dcell.getDisplayText();
		Assert.assertEquals("2008-12-23", displayvalue);

		Cell pcell = table.getCellByPosition("B2");
		format = pcell.getFormatString();
		Assert.assertEquals("#0%", format);

		pcell.setFormatString("#0.00%");
		displayvalue = pcell.getDisplayText();
		Assert.assertEquals("200" + (new DecimalFormatSymbols()).getDecimalSeparator() + "00%", displayvalue);
		try {
			Row tablerow = table.getRowByIndex(6);
			Cell cell = tablerow.getCellByIndex(3);
			Calendar currenttime = Calendar.getInstance();
			cell.setDateValue(currenttime);
			cell.setFormatString("yyyy-MM-dd");
			tablerow = table.getRowByIndex(7);
			cell = tablerow.getCellByIndex(3);
			cell.setTimeValue(currenttime);
			cell.setFormatString("HH:mm:ss");
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		saveods();

		// test value type adapt function.
		SpreadsheetDocument ods;
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell;
			for (int i = 1; i <= 10; i++) {
				cell = tbl.getCellByPosition("A" + i);
				cell.setDoubleValue(new Double(i));
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
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell;
			for (int i = 1; i <= 10; i++) {
				cell = tbl.getCellByPosition("A" + i);
				cell.setPercentageValue(0.1);
			}
			cell = tbl.getCellByPosition("A11");
			cell.setFormula("=sum(A1:A10)");
			// contains '%'should be adapted as percentage.
			cell.setFormatString("###.0%");
			Assert.assertEquals("percentage", cell.getValueType());
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell;
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
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell;
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
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	private void loadOutputSpreadsheet() {
		try {
			odsdoc = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_SPREADSHEET + "Output.ods"));
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Test
	public void testGetSetCellBackgroundColor() throws Exception {
		int rowindex = 2, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		fcell.setCellBackgroundColor(new Color("#ffffff"));
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set color as DEFAULT_BACKGROUND_COLOR #FFFFFF
		Assert.assertEquals("#ffffff", fcell.getCellBackgroundColor().toString());

		Color expectedColor = Color.valueOf("#000000");
		fcell.setCellBackgroundColor(expectedColor);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expectedColor.toString(), fcell.getCellBackgroundColor().toString());

		SpreadsheetDocument ods;
		try {
			ods = SpreadsheetDocument.newSpreadsheetDocument();
			Table tbl = ods.getTableByName("Sheet1");
			Cell cell = tbl.getCellByPosition(0, 0);
			Color actualBackColor = cell.getCellBackgroundColor();
			Assert.assertEquals("#ffffff", actualBackColor.toString());
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetColumnSpannedNumber() throws Exception {
		int rowindex = 2, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		fcell.setColumnSpannedNumber(-2);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set column spanned number as DEFAULT_COLUMN_SPANNED_NUMBER 1.
		Assert.assertEquals(1, fcell.getColumnSpannedNumber());

		fcell.setColumnSpannedNumber(0);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set column spanned number as DEFAULT_COLUMN_SPANNED_NUMBER 1.
		Assert.assertEquals(1, fcell.getColumnSpannedNumber());

		fcell.setColumnSpannedNumber(2);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(2, fcell.getColumnSpannedNumber());
	}

	@Test
	public void testGetSetRowSpannedNumber() throws Exception {
		int rowindex = 2, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		fcell.setRowSpannedNumber(-2);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set row spanned number as DEFAULT_ROW_SPANNED_NUMBER 1.
		Assert.assertEquals(1, fcell.getRowSpannedNumber());

		fcell.setRowSpannedNumber(0);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set row spanned number as DEFAULT_ROW_SPANNED_NUMBER 1.
		Assert.assertEquals(1, fcell.getRowSpannedNumber());

		fcell.setRowSpannedNumber(2);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(2, fcell.getRowSpannedNumber());

	}

	@Test
	public void testGetSetColumnsRepeatedNumber() throws Exception {
		int rowindex = 3, columnindex = 1;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		fcell.setColumnsRepeatedNumber(-2);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set columns repeated number as DEFAULT_COLUMNS_REPEATED_NUMBER 1.
		Assert.assertEquals(1, fcell.getColumnsRepeatedNumber());

		fcell.setColumnsRepeatedNumber(0);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		// set columns repeated number as DEFAULT_COLUMNS_REPEATED_NUMBER 1.
		Assert.assertEquals(1, fcell.getColumnsRepeatedNumber());

		fcell.setColumnsRepeatedNumber(2);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(2, fcell.getColumnsRepeatedNumber());
	}

	@Test
	public void testGetSetDateValue() {
		int rowindex = 7, columnindex = 7;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
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
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(0, fcell.getDateValue().compareTo(expectedCalendar));
	}

	@Test
	public void testGetSetStringValue() {
		int rowindex = 6, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		fcell.setStringValue(null);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals("", fcell.getStringValue());

		String expectedString = "hello world";
		fcell.setStringValue(expectedString);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expectedString, fcell.getStringValue());
	}

	@Test
	public void testGetSetBooleanValue() {
		int rowindex = 5, columnindex = 5;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		boolean expected = false;
		fcell.setBooleanValue(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertFalse(fcell.getBooleanValue());
	}

	@Test
	public void testGetSetCurrencyValue() {
		int rowindex = 5, columnindex = 5;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		fcell.setValueType("currency");
		fcell.setCurrencyFormat("$", "#,##0.00");
		Double actualValue = fcell.getCurrencyValue();
		Assert.assertNull(actualValue);

		double expected = 100.00;
		boolean illegalArgumentFlag = false;
		try {
			fcell.setCurrencyValue(expected, null);
		} catch (IllegalArgumentException ie) {
			if ("currency shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);

		fcell.setCurrencyValue(expected, "USD");
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expected, fcell.getCurrencyValue());
	}

	@Test
	public void testGetSetCurrencyDesc() {
		int rowindex = 1, columnindex = 2;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		boolean illegalArgumentFlag = false;
		try {
			fcell.setCurrencyCode(null);
		} catch (IllegalArgumentException ie) {
			if ("Currency code of cell should not be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);

		fcell = table.getCellByPosition(columnindex, rowindex);
		String expected = "USD";
		fcell.setCurrencyCode(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expected, fcell.getCurrencyCode());
	}

	@Test
	public void testGetSetPercentageValue() {
		int rowindex = 5, columnindex = 5;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		double expected = 56.98;
		fcell.setPercentageValue(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expected, fcell.getPercentageValue());
	}

	@Test
	public void testGetSetTimeValue() {
		int rowindex = 0, columnindex = 4;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		boolean illegalArgumentFlag = false;
		try {
			fcell.setTimeValue(null);
		} catch (IllegalArgumentException ie) {
			if ("time shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		Calendar expected = Calendar.getInstance();
		fcell.setTimeValue(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);

		SimpleDateFormat simpleFormat = new SimpleDateFormat("'PT'HH'H'mm'M'ss'S'");
		String expectedString = simpleFormat.format(expected.getTime());
		String targetString = simpleFormat.format(fcell.getTimeValue().getTime());
		Assert.assertEquals(expectedString, targetString);
	}

	@Test
	public void testGetSetFormula() {
		int rowindex = 1, columnindex = 10;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		boolean illegalArgumentFlag = false;
		try {
			fcell.setFormula(null);
		} catch (IllegalArgumentException ie) {
			if ("formula shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);

		String expected = "of:=[.I2]*4";
		fcell.setFormula(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expected, fcell.getFormula());
	}

	/**
	 * This test case is used to check whether the new created cell uses correct
	 * style settings.</br> SIMPLE allows users to set if cell styles are
	 * inherited or not whenever a new cell is added to the table. The default
	 * setting is using inheritance. In this condition, the style of new column
	 * is same with the last column before the inserted position, while the
	 * style of new row is same with the last row before the inserted position.<br/>
	 * This feature setting will influence <code>appendRow()</code>,
	 * <code>appendColumn()</code>, <code>appendRows()</code>,
	 * <code>appendColumns()</code>, <code>insertRowsBefore()</code> and
	 * <code>insertColumnsBefore()</code>. In default setting condition, the
	 * style name of new created cells after these methods called should be
	 * "ce1" which is inherited from preceding cell. <br/>
	 * But after setting cell style inheritance false, these new created cells'
	 * style name should be "Default", which is not inherited from preceding
	 * one.<br/>
	 * For <code>getCellByPosition()</code>,
	 * <code>getCellRangeByPosition()</code>, <code>getCellRangeByName()</code>,
	 * <code>getRowByIndex()</code> and <code>getColumnByIndex()</code>, if need
	 * automatically expand cells, it will return empty cell(s) without any
	 * style settings. Inheritance setting have no effect on them, so for cells
	 * which created after these methods are called, should have "Default" style
	 * name.
	 */
	@Test
	public void testGetStyleName() {
		try {
			odsstyle = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_STYLE_SPREADSHEET + ".ods"));
			int rowindex = 1, columnindex = 0;
			Table table = odsstyle.getTableByName("Sheet1");
			Cell fcell = table.getCellByPosition(columnindex, rowindex);
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
			odsstyle = (SpreadsheetDocument) SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_STYLE_SPREADSHEET + ".ods"));
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
		} catch (Exception e) {
			Assert.fail(e.toString());
		}
	}

	@Test
	public void testGetTableColumn() {
		int rowindex = 2, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		Assert.assertNotNull(fcell.getTableColumn());
		Assert.assertEquals(columnindex, fcell.getTableColumn().getColumnIndex());
	}

	@Test
	public void testGetTableRow() {
		int rowindex = 2, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		Assert.assertNotNull(fcell.getTableRow());
		Assert.assertEquals(rowindex, fcell.getTableRow().getRowIndex());
	}

	@Test
	public void testRemoveContent() {
		int rowindex = 5, columnindex = 8;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		Assert.assertTrue(fcell.mCellElement.getChildNodes().getLength() > 0);
		fcell.removeContent();
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(0, fcell.mCellElement.getChildNodes().getLength());
	}

	@Test
	public void testRemoveTextContent() throws Exception {
		int rowindex = 5, columnindex = 8;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);

		// how to test?
		fcell.removeContent();
		Assert.assertEquals(0, fcell.mCellElement.getChildNodes().getLength());

		fcell.setDisplayText("hello");
		DrawFrameElement drawEle = new DrawFrameElement(odsdoc.getContentDom());
		drawEle.newDrawImageElement();
		fcell.mCellElement.appendChild(drawEle);

		Assert.assertEquals(2, fcell.mCellElement.getChildNodes().getLength());

		fcell.removeTextContent();
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(1, fcell.mCellElement.getChildNodes().getLength());
	}

	@Test
	public void testGetSetDisplayText() {
		int rowindex = 5, columnindex = 5;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		String expected = "display text";
		fcell.setDisplayText(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expected, fcell.getDisplayText());
		// if the value type is "float", cell value need to be updated, too.
		String cellAddress = "A4";
		fcell = table.getCellByPosition(cellAddress);
		expected = "400.0";
		int expectedValue = 400;
		fcell.setDisplayText(expected);
		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(cellAddress);
		Assert.assertEquals(expected, fcell.getDisplayText());
		Assert.assertEquals(expectedValue, fcell.getDoubleValue().intValue());
	}

	@Test
	public void testGetSetFormatString() {
		int rowindex = 3, columnindex = 0;
		Table table = odsdoc.getTableByName("Sheet1");
		Cell fcell = table.getCellByPosition(columnindex, rowindex);
		boolean illegalArgumentFlag = false;
		try {
			fcell.setFormatString(null);
		} catch (IllegalArgumentException ie) {
			if ("formatStr shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);
		// float format string
		String expected = "#0.0";
		fcell.setFormatString(expected);
		// date format string
		// String expected="MMM d, yy";
		// String expected="yyyy-MM-dd";

		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		fcell = table.getCellByPosition(columnindex, rowindex);
		Assert.assertEquals(expected, fcell.getFormatString());
	}

	@Test
	public void testGetCurrencySymbol() {
		Table table = odsdoc.getTableByName("Sheet1");
		Cell cell1 = table.getCellByPosition("C2");
		Assert.assertEquals("$", cell1.getCurrencySymbol());
		Cell cell2 = table.getCellByPosition("C3");
		Assert.assertEquals("CNY", cell2.getCurrencySymbol());
	}

	@Test
	public void testGetSetCurrencyFormat() {
		Table table = odsdoc.getTableByName("Sheet1");
		String[] formats = { "$#,##0.00", "#,##0.00 CNY", "$#,##0.0" };

		Cell cell = table.getCellByPosition("J1");
		boolean illegalArgumentFlag = false;
		try {
			cell.setCurrencyFormat(null, formats[0]);
		} catch (IllegalArgumentException ie) {
			if ("currencySymbol shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);

		try {
			cell.setCurrencyFormat("$", null);
		} catch (IllegalArgumentException ie) {
			if ("format shouldn't be null.".equals(ie.getMessage())) {
				illegalArgumentFlag = true;
			}
		}
		Assert.assertTrue(illegalArgumentFlag);

		cell.setCurrencyValue(32.12, "USD");
		cell.setCurrencyFormat("$", formats[0]);

		cell = table.getCellByPosition("J2");
		cell.setCurrencyValue(new Double(32), "CNY");
		cell.setCurrencyFormat("CNY", formats[1]);

		cell = table.getCellByPosition("J3");
		cell.setCurrencyValue(-32.12, "USD");
		cell.setCurrencyFormat("$", formats[2]);

		saveods();
		// reload
		loadOutputSpreadsheet();
		table = odsdoc.getTableByName("Sheet1");
		for (int i = 1; i <= 3; i++) {
			Cell newcell = table.getCellByPosition("J" + i);
			Assert.assertEquals(formats[i - 1], newcell.getFormatString());
		}
	}

	@Test
	public void testSetDefaultCellStyle() {
		SpreadsheetDocument outputDocument;
		OdfFileDom contentDom; // the document object model for content.xml
		// the office:automatic-styles element in content.xml
		OdfOfficeAutomaticStyles contentAutoStyles;
		OdfStyle style;
		String noaaDateStyleName;
		String noaaTempStyleName;

		try {
			outputDocument = SpreadsheetDocument.newSpreadsheetDocument();
			contentDom = outputDocument.getContentDom();
			contentAutoStyles = contentDom.getOrCreateAutomaticStyles();

			OdfNumberDateStyle dateStyle = new OdfNumberDateStyle(contentDom, "yyyy-MM-dd", "numberDateStyle", null);
			OdfNumberStyle numberStyle = new OdfNumberStyle(contentDom, "#0.00", "numberTemperatureStyle");

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

			Table table = Table.newTable(outputDocument);
			List<Column> columns = table.insertColumnsBefore(0, 3);
			Column column = columns.get(0);
			column.setDefaultCellStyle(contentAutoStyles.getStyle(noaaDateStyleName, OdfStyleFamily.TableCell));
			Cell aCell = column.getCellByIndex(0);
			aCell.setValueType("date");
			String format = aCell.getFormatString();
			Assert.assertEquals("yyyy-MM-dd", format);

			List<Row> rows = table.insertRowsBefore(0, 1);
			Row row = rows.get(0);
			row.setDefaultCellStyle(contentAutoStyles.getStyle(noaaTempStyleName, OdfStyleFamily.TableCell));
			Cell bCell = row.getCellByIndex(0);
			bCell.setValueType("float");
			String bformat = bCell.getFormatString();
			Assert.assertEquals("#0.00", bformat);
			Assert.assertEquals("end", bCell.getHorizontalAlignment());
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testSetGetFont() {
		try {
			SpreadsheetDocument document;
			document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table1 = document.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			cell1.setStringValue("abcdefg");
			Font font1 = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 12, Color.BLACK, StyleTypeDefinitions.TextLinePosition.THROUGH);
			cell1.setFont(font1);
			Font font11 = cell1.getFont();
			System.out.println(font11);
			if (!font11.equals(font1))
				Assert.fail();

			Cell cell2 = table1.getCellByPosition("A2");
			cell2.setStringValue("redstring");
			Font font2 = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 12, Color.RED, StyleTypeDefinitions.TextLinePosition.UNDER);
			cell2.setFont(font2);
			Font font22 = cell2.getFont();
			System.out.println(font22);
			if (!font22.equals(font2))
				Assert.fail();
			document.save(ResourceUtilities.newTestOutputFile("TestSetGetFont.ods"));
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testSetGetBorder() {
		try {
			SpreadsheetDocument document;
			document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table1 = document.getTableByName("Sheet1");
			Cell cell1 = table1.getCellByPosition("A1");
			cell1.setStringValue("four border");
			Border border = new Border(Color.RED, 1, StyleTypeDefinitions.SupportedLinearMeasure.PT);
			cell1.setBorders(CellBordersType.ALL_FOUR, border);
			Border bottomBorder = cell1.getStyleHandler().getBorder(CellBordersType.BOTTOM);
			Assert.assertEquals(border, bottomBorder);

			Cell cell2 = table1.getCellByPosition("C2");
			cell2.setStringValue("top bottom");
			Border border2 = new Border(Color.BLUE, 5, 1, 2, StyleTypeDefinitions.SupportedLinearMeasure.PT);
			cell2.setBorders(CellBordersType.TOP_BOTTOM, border2);
			Border bottomBorder2 = cell2.getStyleHandler().getBorder(CellBordersType.BOTTOM);
			Assert.assertEquals(border2, bottomBorder2);
			Border bottomBorder22 = cell2.getStyleHandler().getBorder(CellBordersType.LEFT);
			Assert.assertEquals(Border.NONE, bottomBorder22);
			document.save(ResourceUtilities.newTestOutputFile("TestSetGetBorder.ods"));
		} catch (Exception e) {
			Logger.getLogger(TableCellTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}
}
