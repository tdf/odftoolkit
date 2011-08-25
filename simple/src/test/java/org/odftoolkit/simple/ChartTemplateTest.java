/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for template aspects of chart.
 */
public class ChartTemplateTest {

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
		File destination = File.createTempFile("odfdom-test", ".otc");
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
		File destination = File.createTempFile("odfdom-test", ".otc");
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
}
