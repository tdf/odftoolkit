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

/**
 * Test class for template aspects of calc documents.
 * 
 * @author <a href="mailto:fhopf@odftoolkit.org">Florian Hopf</a>
 */
public class TextTemplateTest {

	private static final String TEST_TEXT_TEMPLATE = "/textTestTemplate.ott";

	@Test
	public void testLoadingATextTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_TEXT_TEMPLATE));
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(),
				document.getMediaType());
	}

	@Test
	public void testSavingATextTemplate() throws Exception {
		OdfDocument document = OdfDocument.loadDocument(this.getClass().getResourceAsStream(TEST_TEXT_TEMPLATE));
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(),
				loadedDocument.getMediaType());
	}

	@Test
	public void testNewTextTemplate() throws Exception {
		OdfDocument document = OdfTextDocument.newTextTemplateDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(),
				document.getMediaType());
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(),
				document.getPackage().getMediaType());
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(),
				loadedDocument.getMediaType());
		Assert.assertTrue(document instanceof OdfTextDocument);
	}

	@Test
	public void testNewTextMaster() throws Exception {
		OdfDocument document = OdfTextDocument.newTextMasterDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_MASTER.getName(),
				document.getMediaType());
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_MASTER.getName(),
				document.getPackage().getMediaType());
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_MASTER.getName(),
				loadedDocument.getMediaType());
		Assert.assertTrue(document instanceof OdfTextDocument);
	}

	@Test
	public void testNewTextWeb() throws Exception {
		OdfDocument document = OdfTextDocument.newTextWebDocument();
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_WEB.getName(),
				document.getMediaType());
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_WEB.getName(),
				document.getPackage().getMediaType());
		File destination = File.createTempFile("odfdom-test", ".ott");
		document.save(destination);

		// load again
		OdfDocument loadedDocument = OdfDocument.loadDocument(destination);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_WEB.getName(),
				loadedDocument.getMediaType());
		Assert.assertTrue(document instanceof OdfTextDocument);
	}

	@Test
	public void testSwitchingOdfTextDocument() throws Exception {
		OdfTextDocument document = OdfTextDocument.newTextDocument();
		document.changeMode(OdfTextDocument.SupportedType.TEXT_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_WEB);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_WEB.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_MASTER);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_MASTER.getName(), document.getPackage().getMediaType());

		document = OdfTextDocument.newTextTemplateDocument();
		document.changeMode(OdfTextDocument.SupportedType.TEXT);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_MASTER);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_MASTER.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_WEB);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_WEB.getName(), document.getPackage().getMediaType());

		document = OdfTextDocument.newTextMasterDocument();
		document.changeMode(OdfTextDocument.SupportedType.TEXT);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_WEB);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_WEB.getName(), document.getPackage().getMediaType());

		document = OdfTextDocument.newTextWebDocument();
		document.changeMode(OdfTextDocument.SupportedType.TEXT);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_TEMPLATE);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_TEMPLATE.getName(), document.getPackage().getMediaType());
		document.changeMode(OdfTextDocument.SupportedType.TEXT_MASTER);
		Assert.assertEquals(OdfDocument.OdfMediaType.TEXT_MASTER.getName(), document.getPackage().getMediaType());
	}
}
