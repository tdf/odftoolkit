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

package org.odftoolkit.simple.common.navigation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

/**
 * Test the method of class
 * org.odftoolkit.simple.common.navigation.TableSelection
 */
public class TableSelectionTest {

	private static final String TEXT_FILE = "TestTextSelection.odt";
	TextDocument doc,sourcedoc;
	TextNavigation search;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
			sourcedoc=TextDocument.newTextDocument();

			String tablename = "Table1";
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
			Table table = Table.newTable(sourcedoc, rowlabels, columnlabels, data);
			table.setTableName(tablename);


			String tablename2 = "Table2";
			int rowcount2 = 10, columncount2 = 4;
			double[][] data2 = new double[rowcount2][columncount2];
			for (int i = 0; i < rowcount2; i++) {
				for (int j = 0; j < columncount2; j++) {
					data2[i][j] = Math.random();
				}
			}

			String[] rowlabels2 = new String[rowcount2];
			for (int i = 0; i < rowcount2; i++) {
				rowlabels2[i] = "RowHeader" + i;
			}

			String[] columnlabels2 = new String[columncount2];
			for (int i = 0; i < columncount2; i++) {
				columnlabels2[i] = "ColumnHeader" + i;
			}

			Table table2 = Table.newTable(sourcedoc, rowlabels2, columnlabels2, data2);
			table2.setTableName(tablename2);

			String tablename3 = "Table3";
			int rownumber3 = 5;
			int clmnumber3 = 3;

			Table table1 = Table.newTable(sourcedoc, rownumber3, clmnumber3);
			table1.setTableName(tablename3);
			sourcedoc.save(ResourceUtilities.newTestOutputFile("TestTableSelectionSource.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@After
	public void tearDown() {
	}
	/**
	 * Test replaceWithTable method of
	 * org.odftoolkit.simple.common.navigation.TableSelection replace replace SIMPLE,Task1,Container to different Table
	 */
	@Test
	public void testReplaceWithTable() {
		search = null;
		//6 Simple, at the middle of original Paragraph, split original Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);

		while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				TableSelection nextTableSelection=new TableSelection(item);
				Table table=sourcedoc.getTableByName("Table1");
				Cell cell = table.getCellByPosition(0, 0);
				cell.setStringValue("SIMPLE");
				Table newtable = nextTableSelection.replaceWithTable(table);
				Assert.assertNotNull(newtable);
				Assert.assertEquals(1, newtable.getHeaderColumnCount());
				Assert.assertEquals(1, newtable.getHeaderRowCount());
				Assert.assertEquals(7 + 1, newtable.getRowCount());
				Assert.assertEquals(5 + 1, newtable.getColumnCount());
				cell = newtable.getCellByPosition(1, 1);
				Assert.assertEquals("string", cell.getValueType());
		}


		//2 Task1, #1 at the start of original Paragraph, #2 replace original Paragraph
				search = new TextNavigation("Task1", doc);

				while (search.hasNext()) {
						TextSelection item = (TextSelection) search.nextSelection();
						TableSelection nextTableSelection=new TableSelection(item);
						Table table=sourcedoc.getTableByName("Table2");
						Table newtable = nextTableSelection.replaceWithTable(table);
						Cell cell = newtable.getCellByPosition(0, 0);
						cell.setStringValue("From Source Table2");
						Assert.assertNotNull(newtable);
						Assert.assertEquals(1, newtable.getHeaderColumnCount());
						Assert.assertEquals(1, newtable.getHeaderRowCount());
						Assert.assertEquals(10 + 1, newtable.getRowCount());
						Assert.assertEquals(4 + 1, newtable.getColumnCount());

						cell = newtable.getCellByPosition(1, 1);
						Assert.assertEquals("float", cell.getValueType());
				}
				//1 RESS%>, #1 at the end of original Paragraph,
				search = new TextNavigation("RESS%>", doc);

				while (search.hasNext()) {
						TextSelection item = (TextSelection) search.nextSelection();
						TableSelection nextTableSelection=new TableSelection(item);
						Table table=sourcedoc.getTableByName("Table3");
						Table newtable = nextTableSelection.replaceWithTable(table);
						Cell cell = newtable.getCellByPosition(0, 0);
						cell.setStringValue("From Source Table3");
						Assert.assertNotNull(newtable);
						Assert.assertEquals(5, newtable.getRowCount());
						Assert.assertEquals(3, newtable.getColumnCount());

				}

		try {
			doc.save(ResourceUtilities.newTestOutputFile("TestTableSelectionResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TableSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
