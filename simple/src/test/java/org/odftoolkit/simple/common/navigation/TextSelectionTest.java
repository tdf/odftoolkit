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
import java.net.URI;
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
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field;
import org.odftoolkit.simple.common.field.Fields;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.draw.Image;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	/**
	 * Test replaceWith method of
	 * org.odftoolkit.simple.common.navigation.TextSelection
	 */
	@Test
	public void testReplacewithTable() throws Exception {
		doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		TextDocument sourcedoc = TextDocument.newTextDocument();
		String tablename = "Table1";
		int rowcount = 7, columncount = 5;
		String[][] data = new String[rowcount][columncount];
		for (int i = 0; i < rowcount; i++) {
			for (int j = 0; j < columncount; j++) {
				data[i][j] = "string" + (i * columncount + j);
			}
		}
		
		String[] rowlabels = new String[rowcount];
		for (int i = 0; i < rowcount; i++) {
			rowlabels[i] = "RowHeader" + i;
		}
		
		String[] columnlabels = new String[columncount];
		for (int i = 0; i < columncount; i++) {
			columnlabels[i] = "ColumnHeader" + i;
		}
		Table table = Table.newTable(sourcedoc, rowlabels, columnlabels, data);
		table.setTableName(tablename);
		
		String tablename2 = "Table2";
		int rowcount2 = 10, columncount2 = 4;
		double[][] data2 = new double[rowcount2][columncount2];
		for (int i = 0; i < rowcount2; i++) {
			for (int j = 0; j < columncount2; j++) {
				data2[i][j] = Math.random();
			}
		}
		
		String[] rowlabels2 = new String[rowcount2];
		for (int i = 0; i < rowcount2; i++) {
			rowlabels2[i] = "RowHeader" + i;
		}
		
		String[] columnlabels2 = new String[columncount2];
		for (int i = 0; i < columncount2; i++) {
			columnlabels2[i] = "ColumnHeader" + i;
		}
		
		Table table2 = Table.newTable(sourcedoc, rowlabels2, columnlabels2, data2);
		table2.setTableName(tablename2);
		String tablename3 = "Table3";
		int rownumber3 = 5;
		int clmnumber3 = 3;
		Table table1 = Table.newTable(sourcedoc, rownumber3, clmnumber3);
		table1.setTableName(tablename3);
		search = null;
		// 6 Simple, at the middle of original Paragraph, split original
		// Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			table = sourcedoc.getTableByName("Table1");
			Cell cell = table.getCellByPosition(0, 0);
			cell.setStringValue("SIMPLE");
			Table newtable = item.replaceWith(table);
			Assert.assertNotNull(newtable);
			Assert.assertEquals(1, newtable.getHeaderColumnCount());
			Assert.assertEquals(1, newtable.getHeaderRowCount());
			Assert.assertEquals(7 + 1, newtable.getRowCount());
			Assert.assertEquals(5 + 1, newtable.getColumnCount());
			cell = newtable.getCellByPosition(1, 1);
			Assert.assertEquals("string", cell.getValueType());
		}
		// 2 Task1, #1 at the start of original Paragraph, #2 replace original
		// Paragraph
		search = new TextNavigation("Task1", doc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			table = sourcedoc.getTableByName("Table2");
			Table newtable = item.replaceWith(table);
			Cell cell = newtable.getCellByPosition(0, 0);
			cell.setStringValue("From Source Table2");
			Assert.assertNotNull(newtable);
			Assert.assertEquals(1, newtable.getHeaderColumnCount());
			Assert.assertEquals(1, newtable.getHeaderRowCount());
			Assert.assertEquals(10 + 1, newtable.getRowCount());
			Assert.assertEquals(4 + 1, newtable.getColumnCount());
			
			cell = newtable.getCellByPosition(1, 1);
			Assert.assertEquals("float", cell.getValueType());
		}
		// 1 RESS%>, #1 at the end of original Paragraph,
		search = new TextNavigation("RESS%>", doc);
		
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			table = sourcedoc.getTableByName("Table3");
			Table newtable = item.replaceWith(table);
			Cell cell = newtable.getCellByPosition(0, 0);
			cell.setStringValue("From Source Table3");
			Assert.assertNotNull(newtable);
			Assert.assertEquals(5, newtable.getRowCount());
			Assert.assertEquals(3, newtable.getColumnCount());
		}
		try {
			doc.save(ResourceUtilities.newTestOutputFile("TextSelectionReplacewithTableResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	/**
	 * Test replaceWith method of
	 * org.odftoolkit.simple.common.navigation.TextSelection
	 */
	@Test
	public void testReplacewithImage() throws Exception {
		doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		TextDocument sourcedoc = TextDocument.newTextDocument();
		sourcedoc = TextDocument.newTextDocument();
		Paragraph para = sourcedoc.addParagraph("helloImage");
		Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
		image.setName("this image 1");
		image.setHyperlink(new URI("http://odftoolkit.org"));
		Paragraph para2 = sourcedoc.addParagraph("helloImage2");
		Image image2 = Image.newImage(para2, ResourceUtilities.getURI("testA.jpg"));
		image2.setName("this image 2");
		image2.setHyperlink(new URI("http://odftoolkit.org"));
		search = null;
		// 6 Simple, at the middle of original Paragraph, split original
		// Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		int i = 0;
		TextSelection item = null;
		while (search.hasNext()) {
			item = (TextSelection) search.nextSelection();
			Paragraph paragraph = sourcedoc.getParagraphByIndex(0, true);
			TextParagraphElementBase textParaEleBase = paragraph.getOdfElement();
			NodeList nodeImages = textParaEleBase.getElementsByTagName("draw:image");
			Node nodeImage = nodeImages.item(0);
			DrawImageElement im = (DrawImageElement) nodeImage;
			Image ima = Image.getInstanceof(im);
			image = item.replaceWith(ima);
			Assert.assertNotNull(image);
			if (image.getName().startsWith("replace")) {
				Assert.assertTrue(true);
			} else {
				Assert.fail();
			}
			String name = "simple" + (i++);
			image.setName(name);
			Assert.assertEquals(name, image.getName());
		}
		try {
			doc.save(ResourceUtilities.newTestOutputFile("TextSelectionReplacewithImageResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
		doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		search = null;
		// 6 Simple, at the middle of original Paragraph, split original
		// Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		i = 0;
		try {
			while (search.hasNext()) {
				item = (TextSelection) search.nextSelection();
				URI imageuri = ResourceUtilities.getURI("image_list_item.png");
				image = item.replaceWith(imageuri);
				Assert.assertNotNull(image);
				if (image.getName().startsWith("replace")) {
					Assert.assertTrue(true);
				} else {
					Assert.fail();
				}
				String name = "simple" + (i++);
				image.setName(name);
				Assert.assertEquals(name, image.getName());
			}
			doc.save(ResourceUtilities.newTestOutputFile("TextSelectionReplacewithImageURIResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	/**
	 * Test replaceWith method of
	 * org.odftoolkit.simple.common.navigation.TextSelection
	 */
	@Test
	public void testReplacewithParagraph() throws Exception {
		doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		TextDocument sourcedoc = TextDocument.newTextDocument();
		sourcedoc.addParagraph("Hello1 from SIMPLE source document!");
		sourcedoc.addParagraph("Hello2 from source document!");
		sourcedoc.addParagraph("Hello3 from source document!");
		
		search = null;
		// 6 Simple, at the middle of original Paragraph, split original
		// Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		int i = 0;
		TextSelection item = null;
		while (search.hasNext()) {
			item = (TextSelection) search.nextSelection();
			Paragraph paragraph = sourcedoc.getParagraphByIndex(0, true);
			item.replaceWith(paragraph);
			i++;
		}
		search = new TextNavigation("Hello1 from SIMPLE source document!", doc);
		int j = 0;
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);
		
		// 2 Task1, #1 at the start of original Paragraph, #2 replace original
		// Paragraph
		search = new TextNavigation("Task1", doc);
		i = 0;
		while (search.hasNext()) {
			item = (TextSelection) search.nextSelection();
			
			Paragraph paragraph = sourcedoc.getParagraphByIndex(1, true);
			item.replaceWith(paragraph);
			i++;
		}
		search = new TextNavigation("Hello2 from source document!", doc);
		j = 0;
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);
		
		// 1 Container, #1 at the end of original Paragraph,
		search = new TextNavigation("Container", doc);
		i = 0;
		while (search.hasNext()) {
			item = (TextSelection) search.nextSelection();
			Paragraph paragraph = sourcedoc.getParagraphByIndex(2, true);
			item.replaceWith(paragraph);
			i++;
		}
		search = new TextNavigation("Hello3 from source document!", doc);
		j = 0;
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);
		try {
			doc.save(ResourceUtilities.newTestOutputFile("TextSelectionReplacewithParagraphResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	/**
	 * Test replaceWith method of
	 * org.odftoolkit.simple.common.navigation.TextSelection
	 */
	@Test
	public void testReplacewithField() throws Exception {
		doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		TextDocument sourcedoc = TextDocument.newTextDocument();
		sourcedoc.addParagraph("Hello1 from SIMPLE source document!");
		sourcedoc.addParagraph("Hello2 from source document!");
		sourcedoc.addParagraph("Hello3 from source document!");
		VariableField variableField = Fields.createUserVariableField(sourcedoc, "test_simple_variable","testReplacewithField");
		Assert.assertNotNull(variableField);
		TextSpanElement newTextSpanElement = sourcedoc.newParagraph("Update Variable Field:").newTextSpanElement();
		variableField.updateField("simple variable content", newTextSpanElement);
		newTextSpanElement = sourcedoc.newParagraph("Show Variable Field:").newTextSpanElement();
		variableField.displayField(newTextSpanElement);
		Field orgField = sourcedoc.getVariableFieldByName("test_simple_variable");
		// 6 Simple, at the middle of original Paragraph, split original
		// Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		while (search.hasNext()) {
			TextSelection item = (TextSelection) search.nextSelection();
			try {
				Field newField = item.replaceWith(orgField);
			} catch (InvalidNavigationException e) {
				e.printStackTrace();
			}
		}
		try {
			doc.save(ResourceUtilities.newTestOutputFile("TextSelectionReplacewithFieldResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	/**
	 * Test replaceWith method of
	 * org.odftoolkit.simple.common.navigation.TextSelection
	 */
	@Test
	public void testReplacewithTextDocument() throws Exception {
		doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		TextDocument sourcedoc = TextDocument.newTextDocument();
		sourcedoc.addParagraph("Hello1 from SIMPLE source document!");
		sourcedoc.addParagraph("Hello2 from source document!");
		sourcedoc.addParagraph("Hello3 from source document!");
		search = null;
		// 6 Simple, at the middle of original Paragraph, split original
		// Paragraph, insert before the second Paragraph.
		// Note: you need cache the nextSelection item because after you replace
		// currtenItem with TextDocument, TextNavigation.nextSelection will
		// search from the inserted Content,
		// it will make you into a loop if the Search keyword also can be found
		// in the inserted Content.
		int i = 0;
		search = new TextNavigation("SIMPLE", doc);
		TextSelection currtenTextSelection, nextTextSelection = null;
		while (search.hasNext()) {
			if (nextTextSelection != null) {
				currtenTextSelection = nextTextSelection;
			} else {
				currtenTextSelection = (TextSelection) search.nextSelection();
			}
			nextTextSelection = (TextSelection) search.nextSelection();
			if (currtenTextSelection != null) {
				
				try {
					currtenTextSelection.replaceWith(sourcedoc);
					i++;
				} catch (Exception e) {
					e.printStackTrace();
					Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
				}
			}
		}
		if (nextTextSelection != null) {
			try {
				nextTextSelection.replaceWith(sourcedoc);
				i++;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
			}
		}
		search = new TextNavigation("Hello1 from SIMPLE source document!", doc);
		int j = 0;
		while (search.hasNext()) {
			search.nextSelection();
			j++;
		}
		Assert.assertTrue(i == j);
		try {
			doc.save(ResourceUtilities.newTestOutputFile("TextSelectionReplacewithTextDocumentResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
