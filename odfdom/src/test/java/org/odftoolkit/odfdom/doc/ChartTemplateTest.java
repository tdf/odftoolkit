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
package org.odftoolkit.odfdom.doc;

import java.io.File;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Test class for template aspects of chart.
 */
public class ChartTemplateTest {

	private static final String TEST_CHART_TEMPLATE = "chartTestTemplate.otc";

	@Test
	@Ignore
	public void testLoadingAChartTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_CHART_TEMPLATE));
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getMediaTypeString());
	}

	@Test
	public void testSavingAChartTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_CHART_TEMPLATE));
		File destination = File.createTempFile("odfdom-test", ".otc", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewChartTemplate() throws Exception {
		OdfDocument document = OdfChartDocument.newChartTemplateDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getMediaTypeString());

		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("odfdom-test", ".otc", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof OdfChartDocument);
	}

	@Test
	public void testSwitchingOdfChartDocument() throws Exception {
		OdfChartDocument document = OdfChartDocument.newChartDocument();
		document.changeMode(OdfChartDocument.OdfMediaType.CHART_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getMediaTypeString(),
				document.getPackage().getMediaTypeString());

		document = OdfChartDocument.newChartTemplateDocument();
		document.changeMode(OdfChartDocument.OdfMediaType.CHART);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART.getMediaTypeString(), document.getPackage().getMediaTypeString());
	}

	@Test
	public void testSwitchingOdfImageDocument() throws Exception {
		OdfImageDocument document = OdfImageDocument.newImageDocument();
		document.changeMode(OdfImageDocument.OdfMediaType.IMAGE_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.IMAGE_TEMPLATE.getMediaTypeString(),
				document.getPackage().getMediaTypeString());

		document = OdfImageDocument.newImageTemplateDocument();
		document.changeMode(OdfImageDocument.OdfMediaType.IMAGE);
		Assert.assertEquals(OdfDocument.OdfMediaType.IMAGE.getMediaTypeString(), document.getPackage().getMediaTypeString());
	}
}
