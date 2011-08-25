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

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ParagraphTest {

	String[] plainText = { "nospace", "one space", "two  spaces", "three   spaces", "   three leading spaces",
			"three trailing spaces   ", "one\ttab", "two\t\ttabs", "\tleading tab", "trailing tab\t",
			"mixed   \t   spaces and tabs", "line\r\nbreak" };

	String[][] elementResult = { { "nospace" }, { "one space" }, { "two ", "*s1", "spaces" },
			{ "three ", "*s2", "spaces" }, { " ", "*s2", "three leading spaces" }, { "three trailing spaces ", "*s2" },
			{ "one", "*t", "tab" }, { "two", "*t", "*t", "tabs" }, { "*t", "leading tab" }, { "trailing tab", "*t" },
			{ "mixed ", "*s2", "*t", " ", "*s2", "spaces and tabs" }, { "line", "*n", "break" } };

	@Test
	public void testAppend() {
		TextDocument doc;
		try {
			doc = TextDocument.newTextDocument();
			int i;
			for (i = 0; i < plainText.length; i++) {
				Paragraph para = Paragraph.newParagraph(doc);
				para.appendTextContent(plainText[i]);
				compareResults(para.getOdfElement(), plainText[i], elementResult[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSetTextContent() {
		TextDocument doc;
		try {
			doc = TextDocument.newTextDocument();
			int i;
			for (i = 0; i < plainText.length; i++) {
				Paragraph para = Paragraph.newParagraph(doc);
				para.setTextContent(plainText[i]);
				compareResults(para.getOdfElement(), plainText[i], elementResult[i]);

				String content = para.getTextContent();
				Assert.assertEquals(plainText[i], content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testRemoveContent() {
		TextDocument doc;
		try {
			doc = TextDocument.newTextDocument();
			int i;
			for (i = 0; i < plainText.length; i++) {
				Paragraph para = Paragraph.newParagraph(doc);
				para.setTextContentNotCollapsed(plainText[i]);
				String content = para.getTextContent();
				Assert.assertEquals(plainText[i], content);
				para.removeTextContent();
				content = para.getTextContent();
				Assert.assertEquals("", content);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

}
