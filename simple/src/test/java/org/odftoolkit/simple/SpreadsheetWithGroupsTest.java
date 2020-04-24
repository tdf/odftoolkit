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

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpreadsheetWithGroupsTest {

	private final static String TEST_FILE_NAME = "SpreadsheetWithGroupsTest.ods";

	/**
	 * Test-Data expected from Test-File.
	 */
	private final List<List<String>> INITIAL_DATA;
	private final List<List<String>> EXPECTED_DATA;

	public SpreadsheetWithGroupsTest() {
		INITIAL_DATA = new ArrayList<List<String>>();
		for (int rowIndex = 0; rowIndex <= 3; rowIndex++) {
			List<String> row = new ArrayList<String>();
			INITIAL_DATA.add(row);
			for (int columnIndex = 0; columnIndex <= 3; columnIndex++) {
				row.add(String.format("Value%d-%d", rowIndex, columnIndex));
			}
		}
		EXPECTED_DATA = new ArrayList<List<String>>();
		for (int rowIndex = 0; rowIndex <= 3; rowIndex++) {
			List<String> row = new ArrayList<String>();
			EXPECTED_DATA.add(row);
			for (int columnIndex = 0; columnIndex <= 3; columnIndex++) {
				row.add(String.format("NEW-Value%d-%d", rowIndex, columnIndex));
			}
		}
	}

	@Test
	public void testColumnCount() {
		try {
			Table sheet = loadTestSheet();
            for (int columnIndex = 0; columnIndex < sheet.getColumnCount(); columnIndex++) {
                for (int rowIndex = 0; rowIndex < sheet.getRowCount(); rowIndex++) {
                    if (rowIndex == 0 && columnIndex == 2)
                        Logger.getLogger(SpreadsheetWithGroupsTest.class.getName()).info(String.format("NEW-Value%d-%d", rowIndex, columnIndex));
                    Cell cell = sheet.getCellByPosition(columnIndex, rowIndex);
                    cell.removeContent();
                    cell.setFormula("");
                    cell.setStringValue(String.format("NEW-Value%d-%d", rowIndex, columnIndex));
                }
            }
			assertEquals("Table should be as expected", EXPECTED_DATA, sheet);
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetWithGroupsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

    /**
     * Tests the usual use case of a row iterator, a call to {@code hasNext()}
     * followed by exactly one call of {@code next()}.
     * <pre><code>
     * for (Iterator<Row> rowIter = sheet.getRowIterator();
     *      rowIter.hasNext(); ) {
     *   Row row = rowIter.next();
     *   ...
     * }
     * </code></pre>
     *
     * </code></pre>
     */
    @Test
    public void testRowIteratorStandard() {
        try {
            Table sheet = loadTestSheet();

            int expectedRowIndex = 0;
            Iterator<List<String>> expectedRowIter = INITIAL_DATA.iterator();
            for (Iterator<Row> rowIter = sheet.getRowIterator(); rowIter.hasNext(); ) {
                List<String> expectedRow = expectedRowIter.next();
                Row row = rowIter.next();

                assertEquals("Row Iterator not in sync: ", expectedRowIndex, expectedRow, row);

                expectedRowIndex++;
            }

        } catch (Exception e) {
            Logger.getLogger(SpreadsheetIteratorTest.class.getName()).log(Level.SEVERE, null, e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests if the iterator works even if multiple calls of {@code hasNext()}
     * per row occur.
     *
     * Possible use case:
     * <pre><code>
     * for (Iterator<Row> rowIter = sheet.getRowIterator();
     *      rowIter.hasNext(); ) {
     *   ...
     *   if (rowIter.hasNext()) {
     *     Row row = rowIter.next();
     *     ...
     *   }
     * }
     * </code></pre>
     */
    @Test
    public void testRowIteratorMultipleHasNext() {
        try {
            Table sheet = loadTestSheet();

            int expectedRowIndex = 0;
            Iterator<List<String>> expectedRowIter = INITIAL_DATA.iterator();
            for (Iterator<Row> rowIter = sheet.getRowIterator(); rowIter.hasNext(); ) {
                List<String> expectedRow = expectedRowIter.next();

                rowIter.hasNext();
                Row row = rowIter.next();

                assertEquals("Row Iterator not in sync: ", expectedRowIndex, expectedRow, row);

                expectedRowIndex++;
            }

        } catch (Exception e) {
            Logger.getLogger(SpreadsheetIteratorTest.class.getName()).log(Level.SEVERE, null, e);
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests if the iterator works even if {@code hasNext()} is never called.
     *
     * Possible use case:
     * <pre><code>
     * for (Iterator<Row> rowIter = sheet.getRowIterator();
     *      rowIter.hasNext(); ) {
     *   ...
     *   if (rowIter.hasNext()) {
     *     Row row = rowIter.next();
     *     ...
     *   }
     * }
     * </code></pre>
     */
    @Test
    public void testRowIteratorWithoutHasNext() {
        try {
            Table sheet = loadTestSheet();

            int expectedRowIndex = 0;
            Iterator<List<String>> expectedRowIter = INITIAL_DATA.iterator();
            for (Iterator<Row> rowIter = sheet.getRowIterator(); expectedRowIter.hasNext(); ) {
                List<String> expectedRow = expectedRowIter.next();

                Row row = rowIter.next();

                assertEquals("Row Iterator not in sync: ", expectedRowIndex, expectedRow, row);

                expectedRowIndex++;
            }

        } catch (Exception e) {
            Logger.getLogger(SpreadsheetIteratorTest.class.getName()).log(Level.SEVERE, null, e);
            Assert.fail(e.getMessage());
        }
    }

    private Table loadTestSheet() {
		Table sheet = null;
		try {
			SpreadsheetDocument document = SpreadsheetDocument.loadDocument(
							ResourceUtilities.getTestResourceAsStream(TEST_FILE_NAME));
			sheet = document.getSheetByIndex(0);

			// Assure that the spreadsheet contains the expected so we don't have to check
			// boundaries in the actual test
			assertEquals("Validate testfile: ", INITIAL_DATA, sheet);

		} catch (Exception e) {
			Logger.getLogger(SpreadsheetWithGroupsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}

		return sheet;
	}

	/**
	 * Assure that the spreadsheet contains the expected
	 * data.
	 * @param message Prefix for assertion messages.
	 * @param expectedData Expected cell values.
	 * @param sheet Sheet to check.
	 */
	private void assertEquals(String message, List<List<String>> expectedData, Table sheet) {
		Assert.assertNotNull(String.format("%sSheet must exist", message), sheet);

		Assert.assertEquals(String.format("%sRow count must be equal", message),
						expectedData.size(), sheet.getRowCount());

		int rowIndex = 0;
		for (List<String> expectedRow : expectedData) {
			Row row = sheet.getRowByIndex(rowIndex);
			assertEquals(message, rowIndex, expectedRow, row);
			rowIndex++;
		}
	}

	private void assertEquals(String message, int rowIndex, List<String> expectedRow, Row row) {
		Assert.assertNotNull(
						String.format("%sRow [%d] must exist", message, rowIndex), row);
		Assert.assertEquals(
						String.format("%sCell count of row %d have to match the expected count", message, rowIndex),
						expectedRow.size(), row.getCellCount());

		int columnIndex = 0;
		for (String expectedCellValue : expectedRow) {
			Cell cell = row.getCellByIndex(columnIndex);
			assertEquals(message, rowIndex, columnIndex, expectedCellValue, cell);
			columnIndex++;
		}
	}

	private void assertEquals(String message, int rowIndex, int columnIndex,
					String expectedCellValue, Cell cell) {
		Assert.assertNotNull(
						String.format("%sCell [%d] of row [%d] must exist", message, columnIndex, rowIndex), cell);

		Assert.assertEquals(
						String.format("%sContent of cell [%d] of row [%d] must match", message, columnIndex, rowIndex),
						expectedCellValue, cell.getDisplayText());
	}

}
