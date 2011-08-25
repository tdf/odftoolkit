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
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;

/**
 * Test class for template aspects of calc documents.
 * 
 * @author <a href="mailto:fhopf@odftoolkit.org">Florian Hopf</a>
 */
public class TextTemplateTest {

	private static final String TEST_TEXT_TEMPLATE = "/textTestTemplate.ott";

	@Test
	public void testLoadingATextTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_TEXT_TEMPLATE));
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(),
				document.getMediaTypeString());
	}

	@Test
	public void testSavingATextTemplate() throws Exception {
		Document document = Document.loadDocument(this.getClass().getResourceAsStream(TEST_TEXT_TEMPLATE));
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
	}

	@Test
	public void testNewTextTemplate() throws Exception {
		Document document = TextDocument.newTextTemplateDocument();
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(),
				document.getMediaTypeString());
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof TextDocument);
	}

	@Test
	public void testNewTextMaster() throws Exception {
		Document document = TextDocument.newTextMasterDocument();
		Assert.assertEquals(Document.OdfMediaType.TEXT_MASTER.getMediaTypeString(),
				document.getMediaTypeString());
		Assert.assertEquals(Document.OdfMediaType.TEXT_MASTER.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.TEXT_MASTER.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof TextDocument);
	}

	@Test
	public void testNewTextWeb() throws Exception {
		Document document = TextDocument.newTextWebDocument();
		Assert.assertEquals(Document.OdfMediaType.TEXT_WEB.getMediaTypeString(),
				document.getMediaTypeString());
		Assert.assertEquals(Document.OdfMediaType.TEXT_WEB.getMediaTypeString(),
				document.getPackage().getMediaTypeString());
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		Document loadedDocument = Document.loadDocument(destination);
		Assert.assertEquals(Document.OdfMediaType.TEXT_WEB.getMediaTypeString(),
				loadedDocument.getMediaTypeString());
		Assert.assertTrue(document instanceof TextDocument);
	}

	@Test
	public void testSwitchingOdfTextDocument() throws Exception {
		TextDocument document = TextDocument.newTextDocument();
		document.changeMode(TextDocument.OdfMediaType.TEXT_TEMPLATE);
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_WEB);
		Assert.assertEquals(Document.OdfMediaType.TEXT_WEB.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_MASTER);
		Assert.assertEquals(Document.OdfMediaType.TEXT_MASTER.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = TextDocument.newTextTemplateDocument();
		document.changeMode(TextDocument.OdfMediaType.TEXT);
		Assert.assertEquals(Document.OdfMediaType.TEXT.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_MASTER);
		Assert.assertEquals(Document.OdfMediaType.TEXT_MASTER.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_WEB);
		Assert.assertEquals(Document.OdfMediaType.TEXT_WEB.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = TextDocument.newTextMasterDocument();
		document.changeMode(TextDocument.OdfMediaType.TEXT);
		Assert.assertEquals(Document.OdfMediaType.TEXT.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_TEMPLATE);
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_WEB);
		Assert.assertEquals(Document.OdfMediaType.TEXT_WEB.getMediaTypeString(), document.getPackage().getMediaTypeString());

		document = TextDocument.newTextWebDocument();
		document.changeMode(TextDocument.OdfMediaType.TEXT);
		Assert.assertEquals(Document.OdfMediaType.TEXT.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_TEMPLATE);
		Assert.assertEquals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString(), document.getPackage().getMediaTypeString());
		document.changeMode(TextDocument.OdfMediaType.TEXT_MASTER);
		Assert.assertEquals(Document.OdfMediaType.TEXT_MASTER.getMediaTypeString(), document.getPackage().getMediaTypeString());
	}
}
