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
package org.odftoolkit.simple;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for template aspects of presentations.
 * @author <a href="mailto:fhopf@odftoolkit.org">Florian Hopf</a>
 */
public class PresentationTemplateTest {

	private static final String TEST_PRESENTATION = "/presentationTestTemplate.otp";

	@Test
	public void testLoadingAPresentationTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_PRESENTATION));
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
	}

	@Test
	public void testSavingAPresentationTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_PRESENTATION));
		File destination = File.createTempFile("simple-test", ".otp");
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewPresentationTemplate() throws Exception {
		Document document = PresentationDocument.newPresentationTemplateDocument();
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getMediaTypeString());
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());

		File destination = File.createTempFile("simple-test", ".otp");
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof PresentationDocument);
	}

	@Test
	public void testSwitchingOdfPresentationDocument() throws Exception {
		PresentationDocument document = PresentationDocument.newPresentationDocument();
		document.changeMode(PresentationDocument.OdfMediaType.PRESENTATION_TEMPLATE);
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = PresentationDocument.newPresentationTemplateDocument();
		document.changeMode(PresentationDocument.OdfMediaType.PRESENTATION);
		Assert.assertEquals(Document.OdfMediaType.PRESENTATION.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
	}
}
