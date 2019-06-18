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

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.utils.ResourceUtilities;


/**
 * Test class for template aspects of chart.
 */
public class ChartTemplateTest {

	private static final Logger LOG = Logger.getLogger(ChartTemplateTest.class.getName());
	private static final String TEST_CHART_TEMPLATE = "/chartTestTemplate.otc";

	@Test
	public void testLoadingAChartTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_CHART_TEMPLATE));
		Assert.assertEquals(Document.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getMediaTypeString());
	}

	@Test
	public void testSavingAChartTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_CHART_TEMPLATE));
		File destination = File.createTempFile("simple-test", ".otc", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewChartTemplate() throws Exception {
		Document document = ChartDocument.newChartTemplateDocument();
		Assert.assertEquals(Document.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getMediaTypeString());

		Assert.assertEquals(Document.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("simple-test", ".otc", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof ChartDocument);
	}

	@Test
	public void testSwitchingOdfChartDocument() throws Exception {
		ChartDocument document = ChartDocument.newChartDocument();
		document.changeMode(ChartDocument.OdfMediaType.CHART_TEMPLATE);
		Assert.assertEquals(Document.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getPackage().getMediaTypeString());

		document = ChartDocument.newChartTemplateDocument();
		document.changeMode(ChartDocument.OdfMediaType.CHART);
		Assert.assertEquals(Document.OdfMediaType.CHART.getMediaTypeString(), document.getPackage().getMediaTypeString());
	}

	@Test
	public void testGetOdfMediaType() throws Exception {

		try {
			Document.OdfMediaType chartType = ChartDocument.OdfMediaType.getOdfMediaType("CHART");
			Assert.assertEquals("CHART", chartType.name());
			Assert.assertEquals("application/vnd.oasis.opendocument.chart", chartType.getMediaTypeString());
			Assert.assertEquals("odc", chartType.getSuffix());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testGetMediaTypeString() throws Exception {
		try {
			ChartDocument.OdfMediaType odfMediaTypeChart = ChartDocument.OdfMediaType.valueOf(ChartDocument.OdfMediaType.class, ChartDocument.OdfMediaType.CHART.name());
			String MediaType = odfMediaTypeChart.getMediaTypeString();
			Assert.assertEquals("application/vnd.oasis.opendocument.chart", MediaType);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testLoadDocumentInputStream() throws Exception {
		try {
			InputStream inStream = this.getClass().getResourceAsStream(TEST_CHART_TEMPLATE);
			ChartDocument chardoc = ChartDocument.loadDocument(inStream);
			Assert.assertNotNull(chardoc);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testLoadDocumentFilePath() throws Exception {
		try {
			String filePath = this.getClass().getResource(TEST_CHART_TEMPLATE).getPath();
			ChartDocument chardoc = ChartDocument.loadDocument(filePath);
			Assert.assertNotNull(chardoc);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testLoadDocumentFile() throws Exception {
		try {
			String filePath = this.getClass().getResource(TEST_CHART_TEMPLATE).getPath();
			File filedoc = new File(filePath);
			Assert.assertNotNull(filedoc);
			ChartDocument chardoc = ChartDocument.loadDocument(filedoc);
			Assert.assertNotNull(chardoc);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}
}
