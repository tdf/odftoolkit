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
package org.odftoolkit.simple.common.navigation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

/**
 * Test the method of class org.odftoolkit.simple.common.navigation.TextNavigation 
 */
public class TextNavigationTest {

	private static final Logger LOG = Logger.getLogger(TextNavigationTest.class.getName());
	private static final String TEXT_FILE = "TestTextSelection.odt";
	private static final String NAVIGATION_ODFELEMENT_FILE = "NavigationInOdfElementTest.odp";
	
	TextDocument doc;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		} catch (Exception e) {
			Logger.getLogger(TextNavigationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test getCurrentItem method of org.odftoolkit.simple.common.navigation.TextNavigation
	 */
	@Test
	public void testGotoNext() {
		TextNavigation search = new TextNavigation("delete", doc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			LOG.info(item.toString());
		}

		try {
			search = new TextNavigation("delete", doc.getContentRoot());
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				LOG.info(item.toString());
			}
		} catch (Exception e) {
			Logger.getLogger(TextNavigationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test getNextMatchElement method of org.odftoolkit.simple.common.navigation.TextNavigation
	 */
	@Test
	public void testGetNextMatchElement() {
		try {
			// match values by specifying a document to TextNavigation
			TextNavigation search = new TextNavigation("delete", doc);
			matchLines(search, doc.getContentRoot());
			
			// match value by specifying a Node to the TextNavigation
			search = new TextNavigation("delete", doc.getContentRoot());
			matchLines(search, doc.getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(TextNavigationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	/**
	 * Matches the lines on the given search and rootNode
	 * @param search
	 * @param rootNode
	 */
	private void matchLines(TextNavigation search, Node rootNode) {
		//NodeList list = doc.getContentDom().getElementsByTagName("text:p");
		OdfElement match = (OdfElement) search.getNextMatchElement(rootNode);
		Assert.assertNotNull(match);
		Assert.assertEquals("Task2.delete next paragraph", TextExtractor.getText(match));

		match = (OdfElement) search.getNextMatchElement(match);
		Assert.assertNotNull(match);
		Assert.assertEquals("Hello [delete], I will be delete", TextExtractor.getText(match));

		match = (OdfElement) search.getNextMatchElement(match);
		Assert.assertNotNull(match);
		Assert.assertEquals("indeed   delete", TextExtractor.getText(match));

		match = (OdfElement) search.getNextMatchElement(match);
		Assert.assertNotNull(match);
		Assert.assertEquals("different span in one single word delete indeed", TextExtractor.getText(match));

		match = (OdfElement) search.getNextMatchElement(match);
		Assert.assertNotNull(match);
		Assert.assertEquals("Hello delete this word delete true delete  indeed", TextExtractor.getText(match));
		
		match = (OdfElement) search.getNextMatchElement(match);
		Assert.assertNotNull(match);
		Assert.assertEquals("something to delete in a frame!", TextExtractor.getText(match));
	}

	@Test
	public void testNavigationInOdfElement() {
		try {
			PresentationDocument document = PresentationDocument.loadDocument(ResourceUtilities
					.getAbsolutePath(NAVIGATION_ODFELEMENT_FILE));
			TextNavigation navigation = new TextNavigation("RANDOM COLORED TEXTBOX", document);
			int count = 0;
			while (navigation.hasNext()) {
				navigation.nextSelection();
				count++;
			}
			Assert.assertEquals(3, count);

			Slide slide = document.getSlideByIndex(0);
			navigation = new TextNavigation("RANDOM COLORED TEXTBOX", slide.getOdfElement());
			count = 0;
			while (navigation.hasNext()) {
				navigation.nextSelection();
				count++;
			}
			Assert.assertEquals(1, count);
			document.close();
		} catch (Exception e) {
			Logger.getLogger(TextNavigationTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
