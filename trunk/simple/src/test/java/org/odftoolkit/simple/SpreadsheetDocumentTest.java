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

import java.awt.Rectangle;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.Document.OdfMediaType;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartType;
import org.odftoolkit.simple.chart.DataSet;
import org.odftoolkit.simple.table.Column;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class SpreadsheetDocumentTest {
	private static final Logger LOG = Logger.getLogger(SpreadsheetDocumentTest.class.getName());
	private static final String TEST_FILE = "spreadsheetTestTemplate.ots";
	
	@Test
	public void testGetMediaTypeString() throws Exception{
		
		try {
			String spreadDocPath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			SpreadsheetDocument spreadDoc = SpreadsheetDocument.loadDocument(spreadDocPath);
			Assert.assertNotNull(spreadDoc);
			OdfMediaType odfMediaType = spreadDoc.getOdfMediaType();
			Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE, odfMediaType);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testGetSuffix() throws Exception{
		try {
			String spreadDocPath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			SpreadsheetDocument spreadDoc = SpreadsheetDocument.loadDocument(spreadDocPath);
			Assert.assertNotNull(spreadDoc);
			OdfMediaType odfMediaType = spreadDoc.getOdfMediaType();
			String suffix = odfMediaType.getSuffix();
			Assert.assertEquals("ots", suffix);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetOdfMediaType() throws Exception{
		
		try {
			String spreadDocPath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			SpreadsheetDocument spreadDoc = SpreadsheetDocument.loadDocument(spreadDocPath);
			Assert.assertNotNull(spreadDoc);
			Document.OdfMediaType odfMediaType = SpreadsheetDocument.OdfMediaType.getOdfMediaType(spreadDoc.getMediaTypeString());
			Assert.assertEquals("ots", odfMediaType.getSuffix());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadDocument() throws Exception{
		try {
			String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			SpreadsheetDocument spreadDoc = SpreadsheetDocument.loadDocument(filePath);
			Assert.assertNotNull(spreadDoc);
			Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE, spreadDoc.getOdfMediaType());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadDocumentFile() throws Exception{
		
		try {
			String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			File fileDoc = new File(filePath);
			SpreadsheetDocument spreadDoc = SpreadsheetDocument.loadDocument(fileDoc);;
			Assert.assertNotNull(spreadDoc);
			Assert.assertEquals(Document.OdfMediaType.SPREADSHEET_TEMPLATE, spreadDoc.getOdfMediaType());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}

	@Test
	public void testLoadDocumentFileWithExcelDummyRows0() {
		testLoadDocumentFileWithExcelDummyRows("ExcelDummyRowProblem_rows0.ods", 0);
	}

	@Test
	public void testLoadDocumentFileWithExcelDummyRows1() {
		testLoadDocumentFileWithExcelDummyRows("ExcelDummyRowProblem_rows1.ods", 1);
	}

	private void testLoadDocumentFileWithExcelDummyRows(String file, int rowCount) {
		try {
			String filePath = ResourceUtilities.getAbsolutePath(file);
			File fileDoc = new File(filePath);
			SpreadsheetDocument spreadDoc = SpreadsheetDocument.loadDocument(fileDoc);
			Assert.assertNotNull(spreadDoc);
			Table table = spreadDoc.getSheetByIndex(0);
			Assert.assertNotNull(table);
			Assert.assertEquals(rowCount, table.getRowCount());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateChart() throws Exception{
		try {
			SpreadsheetDocument spDocument = SpreadsheetDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
			String title = "XXXTitle";
			String[] lables = new String[]{"spring","summer","autumn","autumn"};
			String[] legends = new String[]{"hello1","hello2","hello3"};
			double[][] data = new double[][]{{1.2,2.22,3},{2,3,4},{3,4,5},{4,5,6}};
			DataSet dataset = new DataSet(lables, legends, data);
			Rectangle rect = new Rectangle();
			rect.x = 367;
			rect.y = 389;
			rect.width = 379;
			rect.height = 424;
			Chart spChart = spDocument.createChart(title, dataset, rect);
			Assert.assertNotNull(spChart);
			spChart.setChartType(ChartType.AREA);
			//save
			spDocument.save(ResourceUtilities.getTestOutput("Chart_"+TEST_FILE));
			
			Assert.assertEquals(dataset, spChart.getChartData());
			Assert.assertEquals("XXXTitle", spChart.getChartTitle());
			Assert.assertEquals(ChartType.AREA, spChart.getChartType());
			
			LOG.log(Level.INFO,"spChart--> " + spChart);
			
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetSheetByIndex() throws Exception{
		File file = new File(ResourceUtilities.getAbsolutePath("TestSpreadsheetTable.ods"));
		SpreadsheetDocument spDocument = SpreadsheetDocument.loadDocument(file);
		//index < 0 , Not expected, table ==null
		Table tablenull = spDocument.getSheetByIndex(-1);
		Assert.assertTrue((tablenull == null));
		//index > 0 
		//index = 0
		Table tableSheet0 = spDocument.getSheetByIndex(0);
		Assert.assertTrue((tableSheet0 != null));
		Assert.assertEquals("Sheet1", tableSheet0.getTableName());
		Assert.assertEquals(29, tableSheet0.getColumnCount());
		//index = 1
		Table tableSheet1 = spDocument.getSheetByIndex(1);
		Assert.assertTrue((tableSheet1 != null));
		Assert.assertEquals("Sheet2", tableSheet1.getTableName());
		Assert.assertEquals(1, tableSheet1.getColumnCount());
	}
	
	@Test
	public void testInsertSheet() throws Exception{
		File file = new File(ResourceUtilities.getAbsolutePath(TEST_FILE));
		SpreadsheetDocument spDocument = SpreadsheetDocument.loadDocument(file);
		//index <0 , Not expected
		Table table = spDocument.insertSheet(-1);
		Assert.assertNull(table);
		//index >= sheet count
		Table tableb = spDocument.insertSheet(11);
		Assert.assertNull(tableb);
		
		//index is within the law
		Table tab = spDocument.getSheetByName("tabellDemo2");
		if(tab != null){
			for(int i=0;i<spDocument.getSheetCount();i++){
				if(tab.equals(spDocument.getSheetByIndex(i)))
					spDocument.removeSheet(i);
			}
		}
		Table tablea = spDocument.insertSheet(0);
		Column col = tablea.appendColumn();
		col.setWidth(12.99);
		Column col2 = tablea.appendColumn();
		col.setWidth(12.);
		tablea.setTableName("tabellDemo2");
		Assert.assertEquals("tabellDemo2", tablea.getTableName());
		spDocument.save(ResourceUtilities.getAbsolutePath(TEST_FILE));
		
	}
	
	@Test
	public void testRemoveSheet() throws Exception{
		File file = new File(ResourceUtilities.getAbsolutePath(TEST_FILE));
		SpreadsheetDocument spDocument = SpreadsheetDocument.loadDocument(file);
		//index <0 , Not expected
		Table table = spDocument.insertSheet(-1);
		Assert.assertNull(table);
		//index >= sheet count
		Table tableb = spDocument.insertSheet(11);
		Assert.assertNull(tableb);
		
		//index is within the law
		Table tab = spDocument.getSheetByName("tabellDemo2");
		if(tab != null){
			for(int i=0;i<spDocument.getSheetCount();i++){
				if(tab.equals(spDocument.getSheetByIndex(i)))
					spDocument.removeSheet(i);
			}
		}
		Table tablea = spDocument.insertSheet(0);
		Column col = tablea.appendColumn();
		col.setWidth(12.99);
		Column col2 = tablea.appendColumn();
		col.setWidth(12.);
		tablea.setTableName("tabellDemo2");
		Assert.assertEquals("tabellDemo2", tablea.getTableName());
		spDocument.removeSheet(0);
		Table tablem = spDocument.getSheetByIndex(0);
		Assert.assertNotSame(tablea, tablem);
		
		spDocument.save(ResourceUtilities.getAbsolutePath(TEST_FILE));
	}
	
	@Test
	public void testGetSheetByIndex2() throws Exception{
		File file = new File(ResourceUtilities.getAbsolutePath(TEST_FILE));
		SpreadsheetDocument spDocument = SpreadsheetDocument.loadDocument(file);
		//index <0 , Not expected
		Table table = spDocument.insertSheet(-1);
		Assert.assertNull(table);
		//index >= sheet count
		Table tableb = spDocument.insertSheet(11);
		Assert.assertNull(tableb);
		
		//index is within the law
		Table tab = spDocument.getSheetByName("tabellDemo1");
		if(tab != null)
			tab.remove();
		
		Table taba = spDocument.getSheetByName("Tabelle1");
		Table tablea = spDocument.insertSheet(taba,0);
		tablea.setTableName("tabellDemo1");
		Assert.assertEquals("tabellDemo1", tablea.getTableName());
		Table tablem = spDocument.getSheetByIndex(0);
		Assert.assertEquals(tablea, tablem);
		
		spDocument.save(ResourceUtilities.getAbsolutePath(TEST_FILE));
		
	}
	
}
