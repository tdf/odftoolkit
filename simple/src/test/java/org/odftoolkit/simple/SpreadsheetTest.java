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

package org.odftoolkit.simple;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class SpreadsheetTest {

	private final static String TEST_FILE_NAME = "TestSpreadsheetTable.ods";

	@Test
	public void testGetSheetCount() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_FILE_NAME));
			Assert.assertEquals(3, document.getSheetCount());
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSheetByIndexOrName() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_FILE_NAME));
			Table table1 = document.getSheetByName("Sheet2");
			Table table2 = document.getSheetByIndex(1);
			Assert.assertEquals("Sheet2", table1.getTableName());
			Assert.assertEquals("Sheet2", table2.getTableName());
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetActiveSheet() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_FILE_NAME));
			Table table = document.getActiveSheet();
			Assert.assertEquals("Sheet2", table.getTableName());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger(SpreadsheetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testInsertSheet() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_FILE_NAME));
			int oldCount = document.getSheetCount();
			Table table = document.insertSheet(0);
			Assert.assertFalse("Sheet1".equals(table.getTableName()));
			int newCount = document.getSheetCount();
			Assert.assertEquals(1, newCount - oldCount);
			table = document.insertSheet(document.getSheetByName("Sheet1"), 2);
			Assert.assertEquals(table.getTableName(), document.getSheetByIndex(2).getTableName());
			document.save(ResourceUtilities.newTestOutputFile("Output_"+TEST_FILE_NAME));

			//data table from difference document.
			document = SpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_FILE_NAME));
			Table sheet1 = document.getSheetByName("Sheet1");
			SpreadsheetDocument document1 = SpreadsheetDocument.newSpreadsheetDocument();
			table = document1.insertSheet(sheet1, 0);
			Assert.assertEquals(sheet1.getCellByPosition("E3").getDisplayText(), table.getCellByPosition("E3").getDisplayText());
			document1.save(ResourceUtilities.newTestOutputFile("Output2_"+TEST_FILE_NAME));
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAppendAndRemoveSheet() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_FILE_NAME));
			Table table = document.appendSheet("Sheet4");
			Assert.assertEquals("Sheet4", table.getTableName());
			table = document.appendSheet(document.getSheetByName("Sheet1"), "Sheet5");
			Assert.assertEquals("Sheet5", table.getTableName());
			document.save(ResourceUtilities.newTestOutputFile("Output_"+TEST_FILE_NAME));

			//reload
			document = SpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("Output_"+TEST_FILE_NAME));
			document.removeSheet(4);
			Assert.assertNull(document.getSheetByName("Sheet5"));
			document.removeSheet(3);
			Assert.assertNull(document.getSheetByName("Sheet4"));

			//data table from difference document.
			document = SpreadsheetDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_FILE_NAME));
			Table sheet1 = document.getSheetByName("Sheet1");
			SpreadsheetDocument document1 = SpreadsheetDocument.newSpreadsheetDocument();
			table = document1.appendSheet(sheet1, "SheetA");
			Assert.assertEquals(sheet1.getCellByPosition("E3").getDisplayText(), table.getCellByPosition("E3").getDisplayText());
			document1.save(ResourceUtilities.newTestOutputFile("Output2_"+TEST_FILE_NAME));
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
