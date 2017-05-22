/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.odftoolkit.odfdom.doc;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Test class for template aspects of graphics.
 * @author <a href="mailto:fhopf@odftoolkit.org">Florian Hopf</a>
 */
public class GraphicsTemplateTest {

	private static final String TEST_GRAPHICS_TEMPLATE = "/graphicTestTemplate.otg";

	@Test
	public void testLoadingAGraphicsTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_GRAPHICS_TEMPLATE));
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
	}

	@Test
	public void testSavingAGraphicsTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_GRAPHICS_TEMPLATE));
		File destination = File.createTempFile("odfdom-test", ".otg", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE.getMediaTypeString(), loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewGraphicsTemplate() throws Exception {
		OdfDocument document = OdfGraphicsDocument.newGraphicsTemplateDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("odfdom-test", ".otg", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof OdfGraphicsDocument);
	}

	@Test
	public void testSwitchingOdfGraphicsDocument() throws Exception {
		OdfGraphicsDocument document = OdfGraphicsDocument.newGraphicsDocument();
		document.changeMode(OdfGraphicsDocument.OdfMediaType.GRAPHICS_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = OdfGraphicsDocument.newGraphicsTemplateDocument();
		document.changeMode(OdfGraphicsDocument.OdfMediaType.GRAPHICS);
		Assert.assertEquals(OdfDocument.OdfMediaType.GRAPHICS.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
	}
}
