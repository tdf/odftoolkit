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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

/**
 * Test the method of class
 * org.odftoolkit.simple.common.navigation.TextSelection
 */
public class TextSelectionTest {

	private static final String TEXT_FILE = "TestTextSelection.odt";
	private static final String TEXT_COMMENT_FILE = "TestTextSelectionComment.odt";
	private static final String SAVE_FILE_DELETE = "TextSelectionResultDelete.odt";
	private static final String SAVE_FILE_STYLE = "TextSelectionResultStyle.odt";
	private static final String SAVE_FILE_HREF = "TextSelectionResultHref.odt";
	private static final String SAVE_FILE_COMMENT = "TextSelectionResultComment.odt";
	private static final String SAVE_FILE_REPLACE = "TextSelectionResultReplace.odt";
	private static final String SAVE_FILE_COPYTO = "TextSelectionResultCopyTo.odt";
	private static final String SAVE_FILE_COPYTO1 = "TextSelectionResultCopyTo1.odt";
	private static final String SAVE_FILE_DELETE_PATTERN = "TextSelectionResultPatternDelete.odt";
	TextDocument doc;
	OdfFileDom contentDOM;
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
			doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
			contentDOM = doc.getContentDom();
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testHasNext() {
		search = null;
		search = new TextNavigation("range", doc);

		Assert.assertTrue(search.hasNext());
		Assert.assertTrue(search.hasNext());
		Assert.assertTrue(search.hasNext());
		Assert.assertTrue(search.hasNext());
		Assert.assertTrue(search.hasNext());
	}

	
	/**
	 * Test pasteAtFrontOf method of
	 * org.odftoolkit.simple.common.navigation.TextSelection copy the first
	 * 'change' word in the front of all the 'delete' word
	 */
	@Test
	public void testPasteAtFrontOf() {
		search = null;
		search = new TextNavigation("delete", doc);
		TextSelection sel = null;

		TextNavigation search1 = new TextNavigation("change", doc);
		sel = (TextSelection) search1.nextSelection();

		int i = 0;
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			i++;
			try {
				sel.pasteAtFrontOf(item);
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			}
		}

		int j = 0;
		search = new TextNavigation("changedelete", doc);
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);
		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_COPYTO));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

	}

	/**
	 * Test pasteAtEndOf method of
	 * org.odftoolkit.simple.common.navigation.TextSelection copy the first
	 * 'change' word at the end of all the 'delete' word
	 */
	@Test
	public void testPasteAtEndOf() {
		search = null;
		search = new TextNavigation("delete", doc);
		TextSelection sel = null;

		TextNavigation search1 = new TextNavigation("change", doc);
		sel = (TextSelection) search1.nextSelection();

		int i = 0;
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			i++;
			try {
				sel.pasteAtEndOf(item);
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			}
		}
		int j = 0;
		search = new TextNavigation("deletechange", doc);
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_COPYTO1));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

	}

	/**
	 * Test applyStyle method of
	 * org.odftoolkit.simple.common.navigation.TextSelection append "T4" style
	 * for all the 'delete' word, 'T4' in the original document is the 'bold'
	 * style
	 */
	@Test
	public void testApplyStyle() {
		search = null;
		search = new TextNavigation("delete", doc);
		OdfOfficeAutomaticStyles autoStyles = null;
		try {
			autoStyles = doc.getContentDom().getAutomaticStyles();
		} catch (Exception e1) {
			Assert.fail("Failed with " + e1.getClass().getName() + ": '" + e1.getMessage() + "'");
		}
		// T4 is the bold style for text
		OdfStyleBase style = autoStyles.getStyle("T4", OdfStyleFamily.Text);
		Assert.assertNotNull(style);

		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			try {
				item.applyStyle(style);
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			}
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_STYLE));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test replaceWith method of
	 * org.odftoolkit.simple.common.navigation.TextSelection replace all the
	 * 'SIMPLE' with 'Odf Toolkit'
	 */
	@Test
	public void testReplacewith() {
		search = null;
		search = new TextNavigation("SIMPLE", doc);

		TextSelection nextSelect = null;
		TextNavigation nextsearch = new TextNavigation("next", doc);
		nextSelect = (TextSelection) nextsearch.nextSelection();

		// replace all the "SIMPLE" to "Odf Toolkit"
		// except the sentence
		// "Task5.Change the SIMPLE to Odf Toolkit, and bold them."
		OdfStyle style = new OdfStyle(contentDOM);
		style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
		style.setStyleFamilyAttribute("text");
		int i = 0;
		while (search.hasNext()) {
			if (i > 0) {
				TextSelection item = (TextSelection) search.nextSelection();
				try {
					item.replaceWith("Odf Toolkit");
					item.applyStyle(style);
				} catch (InvalidNavigationException e) {
					Assert.fail(e.getMessage());
				}
			}
			i++;
		}

		search = new TextNavigation("Odf Toolkit", doc);
		int j = 0;
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);

		try {
			nextSelect.replaceWith("bbb");
		} catch (InvalidNavigationException e1) {
			Assert.fail(e1.getMessage());
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	/**
	 * Test addComment method of
	 * org.odftoolkit.simple.common.navigation.TextSelection add comment
	 * "simpleODF should be Simple ODF" for all the 'simpleODF' word
	 */
	@Test
	public void testAddComment() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_COMMENT_FILE));
			search = new TextNavigation("simpleODF", textDoc);
			int i=0;
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				item.addComment("simpleODF should be replaced by Simple ODF.", "devin-"+i);
				i++;
			}
			// there are 7 simpleODF in this test document.
			Assert.assertEquals(7, i);
			textDoc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_COMMENT));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	/**
	 * Test addHref method of
	 * org.odftoolkit.simple.common.navigation.TextSelection add href
	 * "http://www.ibm.com" for all the 'delete' word
	 */
	@Test
	public void testAddHref() {
		search = null;
		search = new TextNavigation("^delete", doc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			try {
				item.addHref(new URL("http://www.ibm.com"));
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			} catch (MalformedURLException e) {
				Assert.fail(e.getMessage());
				Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
			}
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_HREF));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}

	}

	/**
	 * Test search pattern of
	 * org.odftoolkit.simple.common.navigation.TextSelection search a snippet of
	 * text match the pattern "<%([^>]*)%>", and extract the content between
	 * "<%" and "%>"
	 */
	@Test
	public void testCutPattern() {
		search = new TextNavigation("<%([^>]*)%>", doc);

		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			try {
				String text = item.getText();
				text = text.substring(2, text.length() - 2);
				item.replaceWith(text);
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			}
		}

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_DELETE_PATTERN));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test TextSelection container element, all of them must be minimum text:p
	 * or text:h. It means container element can't has child element has the
	 * same text content.
	 */
	@Test
	public void testTextSelectionContainerElement() {
		search = new TextNavigation("TextSelectionContainer", doc);
		if (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			OdfElement container = item.getContainerElement();
			Node childNode = container.getFirstChild();
			while (childNode != null) {
				String containerText = childNode.getTextContent();
				Assert.assertFalse(container.getTextContent().equals(containerText));
				childNode = childNode.getNextSibling();
			}
		} else {
			Assert.fail("Navigation search nothing.");
		}
		// test selected table cell content in draw:frame.
		search = new TextNavigation("Task", doc);
		if (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			OdfElement container = item.getContainerElement();
			Node childNode = container.getFirstChild();
			while (childNode != null) {
				String containerText = childNode.getTextContent();
				Assert.assertFalse(container.getTextContent().equals(containerText));
				childNode = childNode.getNextSibling();
			}
		} else {
			Assert.fail("Navigation search nothing.");
		}
		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_DELETE_PATTERN));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testGetContainerElement() throws Exception {
		search = new TextNavigation("TextSelectionContainer", doc);
		if (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			URL url = new URL("http://www.IBM.com");
			item.addHref(url);
			
			//save
			//doc.save(ResourceUtilities.getAbsolutePath(TEXT_FILE));
			
			//validate
			OdfElement parentElement = item.getContainerElement();
			Node node = parentElement.getFirstChild().getFirstChild().getNextSibling();
			TextAElement textAele = (TextAElement)node;
			System.out.println(textAele.getXlinkTypeAttribute());
			System.out.println(textAele.getXlinkHrefAttribute());
			Assert.assertEquals("simple", textAele.getXlinkTypeAttribute());
			Assert.assertEquals("http://www.IBM.com", textAele.getXlinkHrefAttribute());
			
		} else {
			Assert.fail("Navigation search nothing.");
		}
		
	}
}
