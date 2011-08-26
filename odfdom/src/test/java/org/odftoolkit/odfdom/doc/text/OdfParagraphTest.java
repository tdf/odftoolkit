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
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author instructor
 */
public class OdfParagraphTest {
	private static final Logger LOG = Logger.getLogger(OdfParagraphTest.class.getName());
    OdfTextDocument doc;
	OdfFileDom dom;

    public OdfParagraphTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
        try
        {
            doc = OdfTextDocument.newTextDocument();
			dom = doc.getContentDom();
        }
        catch (Exception e)
        {
        	LOG.log(Level.SEVERE, e.getMessage(), e);
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
    }

	/**
	 * Test of addContent method, of class OdfParagraph.
	 */
	@Test
	public void testAddContent() {
		LOG.info("addContent");
		String content = "paragraph content";
		OdfTextParagraph instance = new OdfTextParagraph(dom);
		Node node;
		Assert.assertNotNull(instance);
		instance.addContent(content);
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(content, node.getTextContent());
	}

	/**
	 * Test of addContentWhitespace method, of class OdfTextP.
	 */
	@Test
	public void testAddContentWhitespace() {
		LOG.info("text:p addContentWhitespace");
		String content = "a\tb";
		String part1 = "a";
		String part2 = "b";
		OdfTextParagraph instance = new OdfTextParagraph(dom);
		Node node;
		Assert.assertNotNull(instance);
		instance.addContentWhitespace(content);
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(part1, node.getTextContent());
		node = node.getNextSibling();
		Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Assert.assertEquals(OdfDocumentNamespace.TEXT.getUri(),
			node.getNamespaceURI());
		Assert.assertEquals("tab", node.getLocalName());
		node = node.getNextSibling();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(part2, node.getTextContent());
	}

    /**
	 * Test of addStyledContent method, of class OdfParagraph.
	 */
	@Test
	public void testAddStyledContent() {
		LOG.info("addStyleContent");
		String content = "paragraph content";
		String styleName = "testStyle";
		OdfTextParagraph instance = new OdfTextParagraph(dom);
		Node node;
		Assert.assertNotNull(instance);
		instance.addStyledContent(styleName, content);
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(node.getTextContent(), content);
		Assert.assertEquals(instance.getStyleName(), styleName);
	}

	/**
	 * Test of addStyledContentWhitespace method, of class OdfTextP.
	 */
	@Test
	public void testAddStyledContentWhitespace() {
		LOG.info("text:p addStyledContentWhitespace");
		String content = "a\nb";
		String part1 = "a";
		String part2 = "b";
		String styleName = "testStyle";
		OdfTextParagraph instance = new OdfTextParagraph(dom);
		Node node;
		Assert.assertNotNull(instance);
		instance.addStyledContentWhitespace(styleName, content);
		Assert.assertEquals(styleName, instance.getStyleName());
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(part1, node.getTextContent());
		node = node.getNextSibling();
		Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Assert.assertEquals(OdfDocumentNamespace.TEXT.getUri(),
			node.getNamespaceURI());
		Assert.assertEquals("line-break", node.getLocalName());
		node = node.getNextSibling();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(part2, node.getTextContent());
	}

	/**
	 * Test of addStyledSpan method, of class OdfParagraph.
	 */
	@Test
	public void testAddStyledSpan() {
		LOG.info("addStyleSpan");
		String content = "heading content";
		String spanContent = "span content";
		String styleName = "testStyle";
		String spanStyleName = "spanStyle";
		OdfTextParagraph instance = new OdfTextParagraph(dom);
		OdfTextSpan subElement;
		Node node;
		Assert.assertNotNull(instance);
		instance.addStyledContent(styleName, content).addStyledSpan(
			spanStyleName, spanContent);

		// first item should be text
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(node.getTextContent(), content);
		Assert.assertEquals(instance.getStyleName(), styleName);

		// followed by a span
		node = node.getNextSibling();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Assert.assertTrue(node instanceof OdfTextSpan);
		subElement = (OdfTextSpan) node;

		// with correct style and content
		Assert.assertEquals(subElement.getStyleName(), spanStyleName);
		node = node.getFirstChild();
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(node.getTextContent(), spanContent);
	}

	/**
	 * Test of addStyledSpan method, of class OdfTextP.
	 */
	@Test
	public void testAddStyledSpanWhitespace() {
		LOG.info("text:p addStyledSpanWhitespace");
		String content = "heading content";
		String spanContent = "span    content";  // four blanks
		String part1 = "span ";
		String part2 = "content";
		String styleName = "testStyle";
		String spanStyleName = "spanStyle";
		OdfTextParagraph instance = new OdfTextParagraph(dom);
		OdfTextSpan subElement;
		Element element;
		Node node;
		Assert.assertNotNull(instance);
		instance.addStyledContent(styleName, content).addStyledSpanWhitespace(
			spanStyleName, spanContent);

		// first item should be text
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(content, node.getTextContent());
		Assert.assertEquals(styleName, instance.getStyleName());

		// followed by a span
		node = node.getNextSibling();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Assert.assertTrue(node instanceof OdfTextSpan);
		subElement = (OdfTextSpan) node;

		// with correct style and content
		Assert.assertEquals(subElement.getStyleName(), spanStyleName);
		node = node.getFirstChild();
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(part1, node.getTextContent());

		node = node.getNextSibling();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Assert.assertEquals("s", node.getLocalName());
		element = (Element) node;
		Assert.assertEquals("3", element.getAttributeNS(
			OdfDocumentNamespace.TEXT.getUri(), "c"));

		node = node.getNextSibling();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(part2, node.getTextContent());
	}

}