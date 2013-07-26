/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.common.navigation;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
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
	

	@Test
	public void testCellReplacementWithParagraph() throws Exception {
		replace(new ReplacementAction() {

			public void replace(TextSelection selection, String value) throws Exception {
				TextParagraphElementBase paragraphElement = new TextPElement(doc.getContentDom());
				Paragraph para = Paragraph.getInstanceof(paragraphElement);
				para.setTextContent(value);
				selection.replaceWith(para);
			}
		});
		
	}
	

	@Test
	public void testCellReplacementWithString() throws Exception {
		replace(new ReplacementAction() {

			public void replace(TextSelection selection, String value) throws Exception {
				selection.replaceWith(value);
			}
		});
		
	}
	

	private void replace(ReplacementAction replacementAction) throws Exception {
		TextDocument docToReplaceIn = TextDocument.newTextDocument();
		Table newTable = docToReplaceIn.getTableBuilder().newTable(2, 2);
		Cell cell = newTable.getCellByPosition(0, 0);
		cell.addParagraph("<<ONE>>");
		cell.addParagraph("<<TWO>>");

		Map<String, String> replacements = new TreeMap<String, String>();
		replacements.put("<<ONE>>", "1");
		replacements.put("<<TWO>>", "2");

		// sanity check
		Assert.assertEquals("<<ONE>>\n<<TWO>>", cell.getDisplayText());
		Assert.assertEquals("<<ONE>>\n<<TWO>>", cell.getStringValue());
		
		for (String toReplace : replacements.keySet()) {
			TextNavigation navigation = new TextNavigation(toReplace, docToReplaceIn);
			while (navigation.hasNext()) {
				TextSelection selection = (TextSelection) navigation.nextSelection();
				Assert.assertEquals(toReplace, selection.getElement().getTextContent());
				replacementAction.replace(selection, replacements.get(toReplace));
			}
		}

		Assert.assertEquals("1\n2", cell.getDisplayText());
		Assert.assertEquals("1\n2", cell.getStringValue());
	}
	
	private static interface ReplacementAction {
		void replace(TextSelection selection, String value) throws Exception;
	}	
}
