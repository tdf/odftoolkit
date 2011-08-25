/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.text;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParagraphTest {

	private static final String[] PLAIN_TEXT = { "nospace", "one space", "two  spaces", "three   spaces",
			"   three leading spaces", "three trailing spaces   ", "one\ttab", "two\t\ttabs", "\tleading tab",
			"trailing tab\t", "mixed   \t   spaces and tabs", "line\r\nbreak" };

	private static final String[][] ELEMENT_RESULT = { { "nospace" }, { "one space" }, { "two ", "*s1", "spaces" },
			{ "three ", "*s2", "spaces" }, { " ", "*s2", "three leading spaces" }, { "three trailing spaces ", "*s2" },
			{ "one", "*t", "tab" }, { "two", "*t", "*t", "tabs" }, { "*t", "leading tab" }, { "trailing tab", "*t" },
			{ "mixed ", "*s2", "*t", " ", "*s2", "spaces and tabs" }, { "line", "*n", "break" } };
	
	private static final Logger LOGGER =  Logger.getLogger(ParagraphTest.class.getName());

	private static final String TEST_FILE = "CommentBreakHeadingDocument.odt";
	
	@Test
	public void testAppend() {
		TextDocument doc;
		try {
			doc = TextDocument.newTextDocument();
			int i;
			for (i = 0; i < PLAIN_TEXT.length; i++) {
				Paragraph para = Paragraph.newParagraph(doc);
				para.appendTextContent(PLAIN_TEXT[i]);
				compareResults(para.getOdfElement(), PLAIN_TEXT[i], ELEMENT_RESULT[i]);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetTextContent() {
		TextDocument doc;
		try {
			doc = TextDocument.newTextDocument();
			int i;
			for (i = 0; i < PLAIN_TEXT.length; i++) {
				Paragraph para = Paragraph.newParagraph(doc);
				para.setTextContent(PLAIN_TEXT[i]);
				compareResults(para.getOdfElement(), PLAIN_TEXT[i], ELEMENT_RESULT[i]);

				String content = para.getTextContent();
				Assert.assertEquals(PLAIN_TEXT[i], content);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveContent() {
		TextDocument doc;
		try {
			doc = TextDocument.newTextDocument();
			int i;
			for (i = 0; i < PLAIN_TEXT.length; i++) {
				Paragraph para = Paragraph.newParagraph(doc);
				para.setTextContentNotCollapsed(PLAIN_TEXT[i]);
				String content = para.getTextContent();
				Assert.assertEquals(PLAIN_TEXT[i], content);
				para.removeTextContent();
				content = para.getTextContent();
				Assert.assertEquals("", content);
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	private void compareResults(Element element, String input, String[] output) {
		int i;
		int nSpaces;
		int nSpacesInAttribute;
		Node node = element.getFirstChild();
		for (i = 0; i < output.length; i++) {
			if (output[i].startsWith("*")) {
				Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
				if (output[i].equals("*t")) {
					Assert.assertEquals("tab", node.getLocalName());
				} else if (output[i].equals("*n")) {
					Assert.assertEquals("line-break", node.getLocalName());
				} else {
					nSpaces = Integer.parseInt(output[i].substring(2));
					Assert.assertEquals(node.getLocalName(), "s");
					nSpacesInAttribute = Integer.parseInt(((Element) node).getAttribute("text:c"));
					Assert.assertEquals(nSpaces, nSpacesInAttribute);
				}
			} else {
				Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
				Assert.assertEquals(output[i], node.getTextContent());
			}
			node = node.getNextSibling();
		}
		Assert.assertEquals(node, null);
	}

	@Test
	public void testGetParagraphByIndex() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph1 = doc.addParagraph("paragraph1");
			Paragraph paragraphE = doc.addParagraph(null);
			Paragraph paragraph2 = doc.addParagraph("p2");

			Paragraph t1 = doc.getParagraphByIndex(1, false);
			Assert.assertEquals(t1, paragraph1);
			t1 = doc.getParagraphByIndex(3, false);
			Assert.assertEquals(t1, paragraph2);
			t1 = doc.getParagraphByIndex(1, true);
			Assert.assertEquals(t1, paragraph2);
			t1 = doc.getParagraphByReverseIndex(0, false);
			Assert.assertEquals(t1, paragraph2);
			t1 = doc.getParagraphByReverseIndex(2, false);
			Assert.assertEquals(t1, paragraph1);
			t1 = doc.getParagraphByReverseIndex(1, true);
			Assert.assertEquals(t1, paragraph1);
			doc.save(ResourceUtilities.newTestOutputFile("testGetParagraphByIndex.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetGetFont() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph1 = doc.addParagraph("paragraph1");
			Font font1 = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 12, Color.BLACK,
					StyleTypeDefinitions.TextLinePosition.THROUGH);
			paragraph1.setFont(font1);
			Font font11 = paragraph1.getFont();
			LOGGER.info(font11.toString());
			if (!font11.equals(font1)){
				Assert.fail();
			}

			Paragraph paragraph2 = doc.addParagraph("paragraph2");
			Font font2 = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 12, Color.RED,
					StyleTypeDefinitions.TextLinePosition.UNDER);
			paragraph2.setFont(font2);
			Font font22 = paragraph2.getFont();
			LOGGER.info(font22.toString());
			if (!font22.equals(font2)){
				Assert.fail();
			}
			
			Paragraph paragraph3 = doc.addParagraph("paragraph3");
			Font font3 = paragraph3.getFont();
			LOGGER.info(font3.toString());
			font3.setColor(Color.GREEN);
			font3.setFontStyle(StyleTypeDefinitions.FontStyle.BOLD);
			paragraph3.setFont(font3);
			LOGGER.info(font3.toString());
			Font font33 = paragraph3.getFont();
			if (!font33.equals(font3)){
				Assert.fail();
			}
			doc.save(ResourceUtilities.newTestOutputFile("TestParagraphSetGetFont.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetSetHoriAlignment() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph1 = doc.addParagraph("paragraph1");

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.DEFAULT);
			HorizontalAlignmentType align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.DEFAULT, align);

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.LEFT);
			align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.LEFT, align);

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.RIGHT);
			align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.RIGHT, align);

			doc.save(ResourceUtilities.newTestOutputFile("TestParagraphSetGetHoriAlignment.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetSetHeading() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
			// test isHeading() and getHeadingLevel();
			Paragraph headingParagraph = doc.getParagraphByIndex(0, true);
			Assert.assertEquals(true, headingParagraph.isHeading());
			Assert.assertEquals(1, headingParagraph.getHeadingLevel());
			Paragraph textParagraph = doc.getParagraphByIndex(1, true);
			Assert.assertEquals(false, textParagraph.isHeading());
			Assert.assertEquals(0, textParagraph.getHeadingLevel());

			// test applyHeading()
			textParagraph.applyHeading();
			Assert.assertEquals(true, textParagraph.isHeading());
			Assert.assertEquals(1, textParagraph.getHeadingLevel());
			textParagraph.applyHeading(true, 3);
			Assert.assertEquals(true, textParagraph.isHeading());
			Assert.assertEquals(3, textParagraph.getHeadingLevel());

			doc.save(ResourceUtilities.newTestOutputFile("TestParagraphSetGetHeading.odt"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail();
		}
	}
}
