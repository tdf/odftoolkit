/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.chart;

import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ChartTest {

	private static final String TEST_FILE = "ChartTest.ods";
	private static final String CHART_FILE1 = "TestCreateChartWithLocalData.ods";
	private static final String CHART_FILE2 = "TestCreateChartWithCellRange.ods";
	private static final String CHART_FILE3 = "TestRemoveChart.ods";
	private static final String CHART_FILE_ODT = "TestODTChart.odt";
	private static final String CHART_FILE_ODP = "TestODPChart.odp";

	@Test
	public void testEquals() {
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
			String title = "Main";
			String[] lables = new String[]{"Anna","Daisy","Tony","MingFei"};
			String[] legends = new String[]{"Day1","Day2","Day3"};
			double[][] data = new double[][]{{1,2,3},{2,3,4},{3,4,5},{4,5,6}};
			DataSet dataset = new DataSet(lables,legends,data);
			Rectangle rect = new Rectangle();
			rect.x = 67;
			rect.y = 89;
			rect.width = 379;
			rect.height = 424;
			Chart chart = new Chart(doc.getContentDom().getRootElement().newOfficeBodyElement().newOfficeChartElement().newChartChartElement(null), null);
			chart.setChartTitle(title);
			chart.setChartType(ChartType.AREA);
			chart.setUseLegend(true);
			chart.setChartData(dataset);
			Assert.assertEquals(dataset, chart.getChartData());
			Assert.assertEquals("Main", chart.getChartTitle());
			Assert.assertEquals(ChartType.AREA, chart.getChartType());
			
			System.out.println("chart " + chart);
			
		} catch (Exception e) {
			Logger.getLogger(DataSetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreateChartWithLocalData() {
		SpreadsheetDocument doc;
		try {
			doc = SpreadsheetDocument.newSpreadsheetDocument();
			String title = "LocalData";
			// double[][] data = new double[3][3];
			String[] lables = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends = new String[] { "Day1", "Day2", "Day3" };
			double[][] data = new double[][] { { 1, 2, 3, 4 }, { 2, 3, 4, 5 }, { 3, 4, 5, 6 } };
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 2700;
			rect.width = 10000;
			rect.height = 12000;
			Chart newChart = doc.createChart(title, lables, legends, data, rect);
			Chart chart = doc.getChartById(newChart.getChartID());
			DataSet ds = chart.getChartData();
			Assert.assertEquals(title, chart.getChartTitle());
			Assert.assertEquals(ChartType.BAR, chart.getChartType());
			Assert.assertArrayEquals(lables, ds.getLabels());
			Assert.assertArrayEquals(legends, ds.getLegends());
			Assert.assertTrue(legends.length == ds.getDataSeriesCount());
			Assert.assertNotNull(doc.getChartByTitle("LocalData"));
			Assert.assertTrue(chart.isUseLegend() == false);
			chart.setChartTitle("Chart with local-data");
			chart.setUseLegend(false);
			Assert.assertEquals(chart.getChartTitle(), "Chart with local-data");
			Assert.assertFalse(chart.isUseLegend() == true);
			doc.save(ResourceUtilities.newTestOutputFile(CHART_FILE1));

			Document doc1 = Document.loadDocument(ResourceUtilities.getTestResourceAsStream(CHART_FILE1));
			Assert.assertEquals(doc1.getMediaTypeString(), Document.OdfMediaType.SPREADSHEET.getMediaTypeString());
			doc = (SpreadsheetDocument) doc1;

			Assert.assertNotNull(doc.getChartByTitle("Chart with local-data"));
			Assert.assertNotNull(doc.getChartByTitle("LocalData"));

			newChart = doc.getChartByTitle("Chart with local-data").get(0);
			Assert.assertArrayEquals(newChart.getChartData().getLabels(), ds.getLabels());
			Assert.assertEquals(ChartType.BAR, newChart.getChartType());
			Assert.assertEquals(newChart.getChartData().getLegendByIndex(1), legends[1]);
			Assert.assertTrue(legends.length == newChart.getChartData().getDataSeriesCount());

		} catch (Exception e) {
			Logger.getLogger(DataSetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreateChartWithCellRange() {
		try {
			SpreadsheetDocument doc = (SpreadsheetDocument) Document.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_FILE));
			String barTitle = "Bar Chart with CellRange ";
			String areaTitle = "AREA Chart with CellRange";
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 2700;
			rect.width = 10000;
			rect.height = 12000;
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.A1:A.E21"), true, true, true, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.A1:A.E21"), true, true, false, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.B1:A.E21"), true, false, true, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.B1:A.E21"), true, false, false, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.A2:A.E21"), false, true, true, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.A2:A.E21"), false, true, false, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.B2:A.E21"), false, false, true, rect);
			doc.createChart(barTitle, doc, CellRangeAddressList.valueOf("A.B2:A.E21"), false, false, false, rect);
			Chart chartID8 = doc.createChart(areaTitle, doc, CellRangeAddressList.valueOf("A.B2:A.E21"), false, false,
					false, rect);
			Chart chart8 = doc.getChartById(chartID8.getChartID());
			chart8.setChartType(ChartType.AREA);
			Assert.assertEquals(ChartType.AREA, chart8.getChartType());
			Assert.assertTrue(doc.getChartByTitle(barTitle).size() == 8);
			Assert.assertNotNull(doc.getChartByTitle(areaTitle));
			doc.save(ResourceUtilities.newTestOutputFile(CHART_FILE2));
			
		} catch (Exception e) {
			Logger.getLogger(DataSetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testRemoveChart() {

		try {
			SpreadsheetDocument doc = (SpreadsheetDocument) Document.loadDocument(ResourceUtilities
					.getTestResourceAsStream(CHART_FILE2));
			doc.deleteChartById("Object 1");
			Assert.assertEquals(doc.getChartCount(), 9);
			String barTitle = "Bar Chart with CellRange ";
			String areaTitle = "AREA Chart with CellRange";
			doc.deleteChartByTitle(barTitle);
			Assert.assertNotNull(doc.getChartByTitle(areaTitle));
			Assert.assertEquals(doc.getChartCount(), 1);
			doc.save(ResourceUtilities.newTestOutputFile(CHART_FILE3));

		} catch (Exception e) {
			Logger.getLogger(DataSetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testCreateChartInDocument() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			String title = "LocalData";
			// double[][] data = new double[3][3];
			String[] lables = new String[] { "Anna", "Daisy", "Tony", "MingFei" };
			String[] legends = new String[] { "Day1", "Day2", "Day3" };
			double[][] data = new double[][] { { 1, 2, 3, 4 }, { 2, 3, 4, 5 }, { 3, 4, 5, 6 } };
			Rectangle rect = new Rectangle();
			rect.x = 2000;
			rect.y = 2700;
			rect.width = 10000;
			rect.height = 12000;
			Chart chartID = doc.createChart(title, lables, legends, data, rect);
			Chart chart = doc.getChartById(chartID.getChartID());
			DataSet ds = chart.getChartData();
			Assert.assertEquals(title, chart.getChartTitle());
			Assert.assertEquals(ChartType.BAR, chart.getChartType());
			Assert.assertArrayEquals(lables, ds.getLabels());
			Assert.assertArrayEquals(legends, ds.getLegends());
			Assert.assertTrue(legends.length == ds.getDataSeriesCount());
			Assert.assertNotNull(doc.getChartByTitle("LocalData"));
			Assert.assertFalse(chart.isUseLegend() == true);
			chart.setChartTitle("Chart with local-data");
			chart.setUseLegend(false);
			chart.setChartType(ChartType.RADAR);
			Assert.assertEquals(chart.getChartTitle(), "Chart with local-data");
			Assert.assertTrue(chart.isUseLegend() == false);
			Assert.assertEquals(chart.getChartType(), ChartType.RADAR);
			doc.save(ResourceUtilities.newTestOutputFile(CHART_FILE_ODT));

			PresentationDocument pDoc = PresentationDocument.newPresentationDocument();
			pDoc.createChart(title, lables, legends, data, rect);
			pDoc.save(ResourceUtilities.newTestOutputFile(CHART_FILE_ODP));

		} catch (Exception e) {
			Logger.getLogger(DataSetTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

}
