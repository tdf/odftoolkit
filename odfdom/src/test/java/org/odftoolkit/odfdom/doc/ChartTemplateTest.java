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
package org.odftoolkit.odfdom.doc;

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
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_CHART_TEMPLATE));
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getName(),
				document.getMediaType());
	}

	@Test
	public void testSavingAChartTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_CHART_TEMPLATE));
		File destination = File.createTempFile("odfdom-test", ".otc");
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getName(),
				loadedDocument.getMediaType());
	}

	@Test
	public void testNewChartTemplate() throws Exception {
		OdfDocument document = OdfChartDocument.newChartTemplateDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getName(),
				document.getMediaType());

		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getName(),
				document.getPackage().getMediaType());
		File destination = File.createTempFile("odfdom-test", ".otc");
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getName(),
				loadedDocument.getMediaType());
		Assert.assertTrue(document instanceof OdfChartDocument);
	}

	@Test
	public void testSwitchingOdfChartDocument() throws Exception {
		OdfChartDocument document = OdfChartDocument.newChartDocument();
		document.changeMode(OdfChartDocument.OdfMediaType.CHART_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART_TEMPLATE.getName(),
				document.getPackage().getMediaType());

		document = OdfChartDocument.newChartTemplateDocument();
		document.changeMode(OdfChartDocument.OdfMediaType.CHART);
		Assert.assertEquals(OdfDocument.OdfMediaType.CHART.getName(), document.getPackage().getMediaType());
	}

	@Test
	public void testSwitchingOdfImageDocument() throws Exception {
		OdfImageDocument document = OdfImageDocument.newImageDocument();
		document.changeMode(OdfImageDocument.OdfMediaType.IMAGE_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.IMAGE_TEMPLATE.getName(),
				document.getPackage().getMediaType());

		document = OdfImageDocument.newImageTemplateDocument();
		document.changeMode(OdfImageDocument.OdfMediaType.IMAGE);
		Assert.assertEquals(OdfDocument.OdfMediaType.IMAGE.getName(), document.getPackage().getMediaType());
	}
}
