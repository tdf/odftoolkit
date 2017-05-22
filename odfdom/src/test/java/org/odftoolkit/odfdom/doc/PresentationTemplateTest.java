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
 * Test class for template aspects of presentations.
 * @author <a href="mailto:fhopf@odftoolkit.org">Florian Hopf</a>
 */
public class PresentationTemplateTest {

	private static final String TEST_PRESENTATION = "/presentationTestTemplate.otp";

	@Test
	public void testLoadingAPresentationTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_PRESENTATION));
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
	}

	@Test
	public void testSavingAPresentationTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_PRESENTATION));
		File destination = File.createTempFile("odfdom-test", ".otp", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewPresentationTemplate() throws Exception {
		OdfDocument document = OdfPresentationDocument.newPresentationTemplateDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());

		File destination = File.createTempFile("odfdom-test", ".otp", ResourceUtilities.getTempTestDirectory());
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof OdfPresentationDocument);
	}

	@Test
	public void testSwitchingOdfPresentationDocument() throws Exception {
		OdfPresentationDocument document = OdfPresentationDocument.newPresentationDocument();
		document.changeMode(OdfPresentationDocument.OdfMediaType.PRESENTATION_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = OdfPresentationDocument.newPresentationTemplateDocument();
		document.changeMode(OdfPresentationDocument.OdfMediaType.PRESENTATION);
		Assert.assertEquals(OdfDocument.OdfMediaType.PRESENTATION.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
	}
}
