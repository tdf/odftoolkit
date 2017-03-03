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
package org.odftoolkit.simple.chart;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ChartTest {

	private static final Logger LOG = Logger.getLogger(ChartTest.class.getName());

	private static final String TEST_SPREADSHEET_WITH_CHART = "SpreadsheetWithChart.ods";
	private static final String TEST_FILE = "ChartTest.ods";
	private static final String CHART_FILE1 = "TestCreateChartWithLocalData.ods";
	private static final String CHART_FILE2 = "TestCreateChartWithCellRange.ods";
	private static final String CHART_FILE3 = "TestRemoveChart.ods";
	private static final String CHART_FILE_ODT = "TestODTChart.odt";
	private static final String CHART_FILE_ODP = "TestODPChart.odp";

	// NEW OUTPUT DOCS
	private static final String TEST_SPREADSHEET_WITH_APPENDED_CHART = "SpreadsheetWithAppendedChart.ods";

	@Test
	public void testCopyChart() {
		try {
			SpreadsheetDocument emptySpreadsheetDocument = SpreadsheetDocument.newSpreadsheetDocument();
			SpreadsheetDocument chartSpreadSheetDocument = (SpreadsheetDocument) Document.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_SPREADSHEET_WITH_CHART));
			// Make sure there are no manifest entries duplicates in the beginning..
			checkManifestEntryDuplication(emptySpreadsheetDocument);

			emptySpreadsheetDocument.appendSheet(chartSpreadSheetDocument.getSheetByIndex(0), "test");
			// Make sure no duplicate manifest entries are after the insertion of the sheet with its dependencies..
			checkManifestEntryDuplication(emptySpreadsheetDocument);

			emptySpreadsheetDocument.save(ResourceUtilities.newTestOutputFile(TEST_SPREADSHEET_WITH_APPENDED_CHART));
			SpreadsheetDocument reloadedSpreadsheetWithChart = (SpreadsheetDocument) Document.loadDocument(ResourceUtilities.newTestOutputFile(TEST_SPREADSHEET_WITH_APPENDED_CHART));
			// Make sure no duplicate manifest entries are after reload of the document..
			checkManifestEntryDuplication(reloadedSpreadsheetWithChart);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	// simply tests the given file path for duplicate
	private static void checkManifestEntryDuplication(Document testDocument) throws XPathExpressionException {
		OdfFileDom manifestDom = testDocument.getPackage().getManifestDom();
		XPath xpath = manifestDom.getXPath();
		Node manifestRootElement = manifestDom.getRootElement();
		NodeList manifestEntries = (NodeList) xpath.evaluate("./*/@manifest:full-path", manifestRootElement, XPathConstants.NODESET);
		Assert.assertNotNull(manifestEntries);
		Map<String, Integer> entries = new HashMap<String, Integer>();
		for (int i = 0; i < manifestEntries.getLength(); i++) {
			String entryName = manifestEntries.item(i).toString();
			if (!entries.containsKey(entryName)) {
				entries.put(entryName, 1);
			} else {
				int newCount = entries.get(entryName) + 1;
				entries.put(entryName, newCount);
			}
		}
		boolean duplicated = false;
		StringBuilder sb = null;

		// check if there are multiple occurances
		for (String s : entries.keySet()) {
			if (entries.get(s) > 1) {
				if (sb == null) {
					sb = new StringBuilder();
					sb.append("The following manifest entrie(s) are duplicated:\n");
				}
				sb.append("\tThe entry '").append(s).append("' exists ").append(entries.get(s)).append(" times.\n");
				duplicated = true;
			}
		}
		String errorMsg = "";
		if (sb != null) {
			errorMsg = sb.toString();
		}
		Assert.assertFalse(errorMsg, duplicated);
	}

	@Test
	public void testEquals() {
		try {
			SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
			String title = "Main";
			String[] lables = new String[]{"Anna", "Daisy", "Tony", "MingFei"};
			String[] legends = new String[]{"Day1", "Day2", "Day3"};
			double[][] data = new double[][]{{1, 2, 3}, {2, 3, 4}, {3, 4, 5}, {4, 5, 6}};
			DataSet dataset = new DataSet(lables, legends, data);
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

			LOG.info("chart " + chart);

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
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
			String[] lables = new String[]{"Anna", "Daisy", "Tony", "MingFei"};
			String[] legends = new String[]{"Day1", "Day2", "Day3"};
			double[][] data = new double[][]{{1, 2, 3, 4}, {2, 3, 4, 5}, {3, 4, 5, 6}};
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
			LOG.log(Level.SEVERE, null, e);
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
			//Assert.assertEquals(8, doc.getChartByTitle(barTitle).size());
			Assert.assertNotNull(doc.getChartByTitle(areaTitle));
			doc.save(ResourceUtilities.newTestOutputFile(CHART_FILE2));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testRemoveChart() {

		try {
			SpreadsheetDocument doc = (SpreadsheetDocument) Document.loadDocument(ResourceUtilities
				.getTestResourceAsStream(CHART_FILE2));
			doc.deleteChartById("Object 1");
			//Assert.assertEquals(9, doc.getChartCount());
			String barTitle = "Bar Chart with CellRange ";
			String areaTitle = "AREA Chart with CellRange";
			doc.deleteChartByTitle(barTitle);
			Assert.assertNotNull(doc.getChartByTitle(areaTitle));
			Assert.assertEquals(1, doc.getChartCount());
			doc.save(ResourceUtilities.newTestOutputFile(CHART_FILE3));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreateChartInDocument() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			String title = "LocalData";
			// double[][] data = new double[3][3];
			String[] lables = new String[]{"Anna", "Daisy", "Tony", "MingFei"};
			String[] legends = new String[]{"Day1", "Day2", "Day3"};
			double[][] data = new double[][]{{1, 2, 3, 4}, {2, 3, 4, 5}, {3, 4, 5, 6}};
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
			LOG.log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

}
