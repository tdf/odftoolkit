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
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleElementBase;
import org.odftoolkit.odfdom.dom.style.props.OdfListLevelProperties;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListLevelStyleBullet;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListLevelStyleNumber;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.w3c.dom.Node;

/**
 *
 * @author J David Eisenberg
 */
public class OdfListStyleTest {
	private static final Logger LOG = Logger.getLogger(OdfListStyleTest.class.getName());
    OdfTextDocument doc;
	OdfFileDom dom;
	OdfOfficeAutomaticStyles documentStyles;

	String[] listSpecTest = {
		"*,>,##",
		"I./1:/a)",
		"1.!\u273f!(a)",
	};

	String[] delim = { ",", "/", "!" };

	boolean[] show = { false, true, false };

	String[][] levelType = { // B=bullet, N = numeric
		{"B","B","B"},
		{"N","N","N"},
		{"N","B","N"}
	};

	String[][] formatChar = { // 1,I,a,A for numeric, char for bullet
		{"*", ">", "#" },
		{"I", "1", "a" },
		{"1", "\u273f", "a"}
	};

	String[][] prefixSuffix = { // separated by slashes
		{"/", "/", "/" },
		{"/.", "/:", "/)"},
		{"/.", "/", "(/)" }
	};

    public OdfListStyleTest() {
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

	    @Test
    public void testBuildFromFormat() {
		int i;
		
        LOG.info("createListStyle");
        OdfTextListStyle instance = null;

        for (i = 0; i < listSpecTest.length; i++)
		{
			LOG.info("List Format: " + listSpecTest[i]);
			instance = new OdfTextListStyle(dom, "list" + i,
				listSpecTest[i], delim[i], "1cm", show[i]);

			Assert.assertNotNull(instance.getFirstChild());

			checkNodes(instance, i);
		}
	}

	/**
	 * Test of addContent method, of class OdfListStyle.
	 */
	@Test
	public void testAddContent() {
		LOG.info("addContent");
		String content = "heading content";
		OdfTextHeading instance = new OdfTextHeading(dom);
		Node node;
		Assert.assertNotNull(instance);
		instance.addContent(content);
		node = instance.getFirstChild();
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
		Assert.assertEquals(content, node.getTextContent());
	}

	private void checkNodes(Node node, int position)
	{
		OdfTextListStyle theStyle;
		OdfTextListLevelStyleBullet bullet;
		OdfTextListLevelStyleNumber number;
		TextListLevelStyleElementBase base;

		int i = 0;
		theStyle = (OdfTextListStyle) node;
		Assert.assertTrue("Style name " +
			theStyle.getStyleNameAttribute() + " incorrect",
			theStyle.getStyleNameAttribute().equals("list" + position));
		node = node.getFirstChild();

		while (node != null)
		{
			Assert.assertTrue("More nodes than specifiers",
				i < levelType[position].length);
			String[] surround;
			if (prefixSuffix[position][i].equals("/"))
			{
				surround = new String[2];
				surround[0] = "";
				surround[1] = "";
			}
			else
			{
				surround = prefixSuffix[position][i].split("/");
			}
			if (levelType[position][i].equals("B"))
			{
				Assert.assertTrue("Class is not bullet",
					node instanceof OdfTextListLevelStyleBullet);
				bullet = (OdfTextListLevelStyleBullet) node;
				base = bullet;
				Assert.assertEquals("Prefix incorrect",
					surround[0], bullet.getStyleNumPrefixAttribute());
				Assert.assertEquals("Suffix incorrect", surround[1],
					bullet.getStyleNumSuffixAttribute());
			}
			else
			{
				Assert.assertTrue("Class is not number",
					node instanceof OdfTextListLevelStyleNumber);
				number = (OdfTextListLevelStyleNumber) node;
				Assert.assertEquals(formatChar[position][i],
					number.getStyleNumFormatAttribute());
				base = number;
				Assert.assertEquals("Prefix incorrect",
					surround[0], number.getStyleNumPrefixAttribute());
				Assert.assertEquals("Suffix incorrect", surround[1],
					number.getStyleNumSuffixAttribute());
			}
			Assert.assertEquals("Level incorrect",
				i+1, base.getTextLevelAttribute().intValue());

			checkNumber(
				"SpaceBefore",
				base.getProperty(OdfListLevelProperties.SpaceBefore),
				i+1);

			checkNumber(
				"MinLabelWidth",
				base.getProperty(OdfListLevelProperties.MinLabelWidth),
				1.0);

			node = node.getNextSibling();
			i++;
		}
    }

	private void checkNumber(String message, String cssLength, double expected)
	{
		String cssAmount = cssLength.replaceFirst("cm","");
		double amount = Double.valueOf(cssAmount);
		Assert.assertEquals(message + " incorrect", expected, amount, 0.01);
	}

}