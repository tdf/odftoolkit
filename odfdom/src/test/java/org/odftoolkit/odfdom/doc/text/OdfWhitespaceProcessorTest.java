/************************************************************************
 *
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
 ************************************************************************/

package org.odftoolkit.odfdom.doc.text;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfWhitespaceProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author J David Eisenberg
 */
public class OdfWhitespaceProcessorTest {
	private static final Logger LOG = Logger.getLogger(OdfWhitespaceProcessorTest.class.getName());
	OdfTextDocument doc;
	OdfFileDom dom;
	String[] plainText = { "nospace", "one space", "two  spaces",
			"three   spaces", "   three leading spaces",
			"three trailing spaces   ", "one\ttab", "two\t\ttabs",
			"\tleading tab", "trailing tab\t", "mixed   \t   spaces and tabs",
			"line\nbreak" };

	String[][] elementResult = { { "nospace" }, { "one space" },
			{ "two ", "*s1", "spaces" }, { "three ", "*s2", "spaces" },
			{ " ", "*s2", "three leading spaces" },
			{ "three trailing spaces ", "*s2" }, { "one", "*t", "tab" },
			{ "two", "*t", "*t", "tabs" }, { "*t", "leading tab" },
			{ "trailing tab", "*t" },
			{ "mixed ", "*s2", "*t", " ", "*s2", "spaces and tabs" },
			{ "line", "*n", "break" } };

	public OdfWhitespaceProcessorTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			doc = OdfTextDocument.newTextDocument();
			dom = doc.getContentDom();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of append method, of class OdfWhitespaceProcessor.
	 */
	@Test
	public void testAppend() {
		LOG.info("append");
		Element element = null;
		OdfWhitespaceProcessor instance = new OdfWhitespaceProcessor();
		int i;
		for (i = 0; i < plainText.length; i++) {
			element = new OdfTextParagraph(dom);
			instance.append(element, plainText[i]);
			compareResults(element, plainText[i], elementResult[i]);
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
					nSpacesInAttribute = Integer.parseInt(((Element) node)
							.getAttribute("text:c"));
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

	/**
	 * Test of getText method, of class OdfWhitespaceProcessor.
	 */
	@Test
	public void testGetText() {
		LOG.info("getText");
		Node element = null;
		OdfWhitespaceProcessor instance = new OdfWhitespaceProcessor();
		int i;
		String expResult = "";
		String result;
		for (i = 0; i < plainText.length; i++) {
			element = new OdfTextParagraph(dom);
			constructElement(element, elementResult[i]);
			result = plainText[i];
			expResult = instance.getText(element);
			Assert.assertEquals(expResult, result);
		}
	}

	private void constructElement(Node element, String[] expected) {
		int i;
		int nSpaces;
		TextSElement spaceElement;

		for (i = 0; i < expected.length; i++) {
			if (expected[i].startsWith("*")) {
				if (expected[i].equals("*t")) {
					element.appendChild(new TextTabElement(dom));
				} else if (expected[i].equals("*n")) {
					element.appendChild(new TextLineBreakElement(dom));
				} else {
					nSpaces = Integer.parseInt(expected[i].substring(2));
					spaceElement = new TextSElement(dom);
					spaceElement.setTextCAttribute(nSpaces);
					element.appendChild(spaceElement);
				}
			} else {
				element.appendChild(dom.createTextNode(expected[i]));
			}
		}
	}

	/**
	 * Test of appendText method, of class OdfWhitespaceProcessor.
	 */
	@Test
	public void testAppendText() {
		LOG.info("appendText");
		Element element = null;
		int i;
		for (i = 0; i < plainText.length; i++) {
			element = new OdfTextParagraph(dom);
			OdfWhitespaceProcessor.appendText(element, plainText[i]);
			compareResults(element, plainText[i], elementResult[i]);
		}
	}

}