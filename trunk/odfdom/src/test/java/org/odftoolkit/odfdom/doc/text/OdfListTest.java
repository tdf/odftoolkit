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
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextList;
import org.w3c.dom.Node;

/**
 *
 * @author J David Eisenberg
 */
public class OdfListTest {
	private static final Logger LOG = Logger.getLogger(OdfListTest.class.getName());
    OdfTextDocument doc;
	OdfFileDom dom;
	OdfOfficeAutomaticStyles documentStyles;

	/*
	 * This list purposely does not go in strict hierarchial order;
	 * it skips around levels to make sure that the
	 * proper elements are being produced in the correct places
	 * without extraneous empty OdfListItems.
	 */
	String[] listContent = {
		"Level zero A",
		">>>>Level four A",
		">>>>Level four B",
		">>Level two A",
		">>Level two B",
		">>>Level three A",
		">>>Level three B",
		"Level zero B"
	};

	/*
	 * L: expecting an OdfList
	 * I: expecting an OdfItem
	 * P: expecting an OdfParagraph with given content
	 */
	String[] expected = {
		"L", "I", "PLevel zero A",
		"L","I",
		"L","I",
		"L","I",
		"L","I","PLevel four A",
		"I", "PLevel four B",
		"I", "PLevel two A", "I", "PLevel two B",
		"L", "I", "PLevel three A", "I", "PLevel three B",
		"I", "PLevel zero B"
	};

	int position = 0;

    public OdfListTest() {
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
	 * Test of constructor, of class OdfListStyle.
	 */
	@Test
	public void testOdfList() {
		LOG.info("odfList constructor");
		String styleName = "lstyle";

		OdfTextList instance = new OdfTextList(dom,
			listContent, '>', styleName);
		Node node;
		Assert.assertNotNull(instance);
		node = instance;
		checkNode(node);
	}

	private void checkNode(Node node)
	{
		while (node != null)
		{
			Assert.assertTrue("More nodes than expected",
				position < expected.length);
			
			if (expected[position].startsWith("L"))
			{
				Assert.assertTrue("Not a list", node instanceof OdfTextList);
				position++;
				if (node.hasChildNodes())
				{
					checkNode(node.getFirstChild());
				}
			}
			else if (expected[position].startsWith("I"))
			{
				Assert.assertTrue("Not an item", node instanceof TextListItemElement);
				position++;
				if (node.hasChildNodes())
				{
					checkNode(node.getFirstChild());
				}
			}
			else // it's a paragraph
			{
				checkParagraph(node, expected[position].substring(1));
				position++;
			}
			node = node.getNextSibling();
		}
	}

	private void checkParagraph(Node node, String value)
	{
		Node child;
		Assert.assertTrue("Not a paragraph", node instanceof
			TextPElement);
		Assert.assertTrue("Paragraph has no child",
			node.hasChildNodes());
		child = node.getFirstChild();
		Assert.assertEquals(Node.TEXT_NODE, child.getNodeType());
		Assert.assertEquals(value, child.getTextContent());
   }

}