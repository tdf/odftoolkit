/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.incubator.search;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.text.OdfWhitespaceProcessor;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Test the method of class org.odftoolkit.odfdom.incubator.search.TextNavigation 
 */
public class TextNavigationTest {

	public static final String TEXT_FILE = "TestTextSelection.odt";
	OdfTextDocument doc;
	TextNavigation search;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			doc = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test getCurrentItem method of org.odftoolkit.odfdom.incubator.search.TextNavigation
	 */
	@Test
	public void testGotoNext() {

		search = null;
		search = new TextNavigation("delete", doc);


		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.getCurrentItem();
			System.out.println(item);
		}

	}

	/**
	 * Test getNextMatchElement method of org.odftoolkit.odfdom.incubator.search.TextNavigation
	 */
	@Test
	public void testGetNextMatchElement() {

		search = null;
		search = new TextNavigation("delete", doc);
		OdfWhitespaceProcessor textProcessor = new OdfWhitespaceProcessor();

		try {
			//NodeList list = doc.getContentDom().getElementsByTagName("text:p");
			OdfElement firstmatch = (OdfElement) search.getNextMatchElement(doc.getContentRoot());
			Assert.assertNotNull(firstmatch);
			Assert.assertEquals("Task2.delete next paragraph", textProcessor.getText(firstmatch));

			OdfElement secondmatch = (OdfElement) search.getNextMatchElement(firstmatch);
			Assert.assertNotNull(secondmatch);
			Assert.assertEquals("Hello [delete], I will be delete", textProcessor.getText(secondmatch));

			OdfElement thirdmatch = (OdfElement) search.getNextMatchElement(secondmatch);
			Assert.assertNotNull(thirdmatch);
			Assert.assertEquals("haha   delete", textProcessor.getText(thirdmatch));

			OdfElement match4 = (OdfElement) search.getNextMatchElement(thirdmatch);
			Assert.assertNotNull(match4);
			Assert.assertEquals("different span in one single word delete haha", textProcessor.getText(match4));

			OdfElement match5 = (OdfElement) search.getNextMatchElement(match4);
			Assert.assertNotNull(match5);
			Assert.assertEquals("Hello delete this word delete ha delete  oyeah", textProcessor.getText(match5));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

	}
}
