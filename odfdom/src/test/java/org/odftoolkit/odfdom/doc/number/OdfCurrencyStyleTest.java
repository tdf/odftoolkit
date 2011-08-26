/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.doc.number;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencySymbolElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberCurrencyStyle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author J David Eisenberg
 */
public class OdfCurrencyStyleTest {
	OdfSpreadsheetDocument doc;
	OdfFileDom dom;
	private static final Logger LOG = Logger.getLogger(OdfCurrencyStyleTest.class.getName());
    public OdfCurrencyStyleTest() {
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
            doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
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
	 * Test of buildFromFormat method, of class OdfCurrencyStyle.
	 */
	@Test
	public void testBuildFromFormat() {
		String[] formatTest = {
			"$US#,##0.00",
			"cr$US##0.00",
			"## $US",
			"##0 \u03b4\u03c1\u03c7", // Greek drachma
			"cr#,##0.00 $US"
		};

		String[] getFormatExpected = {
				"$US#,##0.00",
				"cr$US#0.00",
				"# $US",
				"#0 \u03b4\u03c1\u03c7", // Greek drachma
				"cr#,##0.00 $US"
		};

		String[] currencySymbol = { "$", "$", "$", "\u03b4\u03c1\u03c7", "$" };

		/*
		 * Expected elements.
         * t -- <number:text> with following text
         * n -- <number:number> with minimum digits, decimal places,
         *      and grouping (T or F)
         * c -- <number:currency-symbol> with the symbol text
		 */
        String[][] expected = {
			{"c$","tUS","n12T" }, // $US#,##0.00
			{"tcr", "c$", "tUS", "n12F"}, // cr$US##0.00
			{"n00F", "t ", "c$", "tUS"}, // ## $US
			{"n10F", "t ", "c\u03b4\u03c1\u03c7"}, // ##0 \u03b4\u03c1\u03c7
			{"tcr", "n12T", "t ", "c$", "tUS"} //cr#,##0.00 $US
		};

		LOG.info("buildFromFormat");
		OdfNumberCurrencyStyle instance = null;
		Node node;
		char expectedType;
		String expectedValue;

		for (int i = 0; i < formatTest.length; i++)
		{
			LOG.info("Currency format: " + formatTest[i]);
			instance = new OdfNumberCurrencyStyle(dom,
				currencySymbol[i], formatTest[i], "fstyle");
			Assert.assertNotNull(instance);

			node = instance.getFirstChild();

			for (int j = 0; j < expected[i].length; j++)
			{
				expectedType = expected[i][j].charAt(0);
				expectedValue = expected[i][j].substring(1);
				switch (expectedType)
				{
					case 't':
						checkNumberText("text", expectedValue, node);
						break;
					case 'c':
						checkCurrency(expectedValue, node);
						break;
					case 'n':
						checkNumberFormat(expectedValue, node);
						break;
				}
				node = node.getNextSibling();
			}
			Assert.assertEquals(getFormatExpected[i], instance.getFormat());
		}
	}

	/**
	 * Check that the node is an element with the given name
	 * with the expected text content.
	 * @param elementName expected element name (in number: namespace)
	 * @param expected expected text content
	 * @param node the Node to be examined
	 */
	private void checkNumberText(String elementName, String expected, Node node)
	{
		Node childNode;

		// Check for <number:elementName> with expected content
		Assert.assertNotNull(node);
		Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Assert.assertEquals(OdfNamespaceNames.NUMBER.getUri(),
			node.getNamespaceURI());
		Assert.assertEquals(elementName, node.getLocalName());
		childNode = node.getFirstChild();
		Assert.assertEquals(Node.TEXT_NODE, childNode.getNodeType());
		Assert.assertEquals(expected, childNode.getNodeValue());
	}

	/**
	 * Check to see that the node is <code>&lt;number:number&gt;</code> and
	 * meets the expected specifications.
	 *
	 * The expected specifications is a string of three characters:
	 * min # of digits, # of decimal places, grouped (T/F)
	 * @param expected expected specification for <code>&lt;number:number&gt;</code>
	 * @param node the node to be validated
	 */
	private void checkNumberFormat(String expected, Node node)
	{
		int nDigits = Integer.parseInt(expected.substring(0,1));
		int nDecimals = Integer.parseInt(expected.substring(1,2));
		boolean grouped = (expected.charAt(2) == 'T');
		boolean nodeGrouped;
		NumberNumberElement number;

		Assert.assertTrue("node is NumberNmberElement", node instanceof NumberNumberElement);
		number = (NumberNumberElement) node;

		// check number of digits and decimals
		Assert.assertEquals(nDigits, (long) number.getNumberMinIntegerDigitsAttribute());

		if (nDecimals > 0)
		{
			Assert.assertEquals(nDecimals, (long)number.getNumberDecimalPlacesAttribute());
		}

		// check if grouping is set properly
		nodeGrouped = (number.getNumberGroupingAttribute() == null) ? 
			false :
			number.getNumberGroupingAttribute().booleanValue();
		Assert.assertTrue("Grouping", grouped == nodeGrouped);
	}

	/**
	 * Check that a <code>&lt;number:currency-symbol&gt;</code> element
	 * meets specifications.
	 *
	 * @param expected a string giving the currency symbol
	 * @param node the node to be validated
	 */
	private void checkCurrency(String expected, Node node)
	{
		Assert.assertTrue("node is currency symbol", node instanceof
			NumberCurrencySymbolElement);
		checkNumberText("currency-symbol", expected, node);
	}

	/**
	 * Test of getCurrencySymbolElement method, of class OdfNumberNumberCurrencyStyle.
	 */
	@Test
	public void testGetCurrencySymbolElement() {
		LOG.info("getCurrencySymbolElement");
		OdfNumberCurrencyStyle instance = new OdfNumberCurrencyStyle(dom,
			"$", "$#,##0.00", "cstyle");
		NumberCurrencySymbolElement expResult = new NumberCurrencySymbolElement(dom);
		expResult.setTextContent("$");
		NumberCurrencySymbolElement result = instance.getCurrencySymbolElement();
		Assert.assertEquals(expResult.getTextContent(),
			result.getTextContent());
	}

	/**
	 * Test of setCurrencyLocale method, of class OdfNumberCurrencyStyle.
	 */
	@Test
	public void testSetCurrencyLocale_String_String() {
		LOG.info("setCurrencyLocale");
		String language = "ko";
		String country = "KR";
		OdfNumberCurrencyStyle instance = new OdfNumberCurrencyStyle(dom,
			"\u20a9", "\u20a9#,##0.00", "kstyle"); // korean Won
		NumberCurrencySymbolElement cSymbol;
		instance.setCurrencyLocale(language,
			country);
		NodeList list = instance.getElementsByTagNameNS(
			OdfNamespaceNames.NUMBER.getUri(), "currency-symbol");
		Assert.assertTrue("Has currency symbol", list.getLength() > 0);
		cSymbol = (NumberCurrencySymbolElement) list.item(0);
		Assert.assertEquals(language, cSymbol.getNumberLanguageAttribute());
		Assert.assertEquals(country, cSymbol.getNumberCountryAttribute());
	}

	/**
	 * Test of setCurrencyLocale method, of class OdfNumberCurrencyStyle.
	 */
	@Test
	public void testSetCurrencyLocale_String() {
		LOG.info("setCurrencyLocale");
		String locale = "ko-KR";
		String language = "ko";
		String country = "KR";
		NumberCurrencySymbolElement cSymbol;
		NodeList list;

		// first, test setting with a combined language/country
		OdfNumberCurrencyStyle instance = new OdfNumberCurrencyStyle(dom,
			"\u20a9", "\u20a9#,##0.00", "kstyle");
		instance.setCurrencyLocale(locale);
		list = instance.getElementsByTagNameNS(
			OdfNamespaceNames.NUMBER.getUri(), "currency-symbol");
		Assert.assertTrue("Has currency symbol", list.getLength() > 0);
		cSymbol = (NumberCurrencySymbolElement) list.item(0);
		Assert.assertEquals(language, cSymbol.getNumberLanguageAttribute());
		Assert.assertEquals(country, cSymbol.getNumberCountryAttribute());

		// then, a language only
		instance = new OdfNumberCurrencyStyle(dom,
			"\u20a9", "\u20a9#,##0.00", "kstyle");
		instance.setCurrencyLocale(language);
		list = instance.getElementsByTagNameNS(
			OdfNamespaceNames.NUMBER.getUri(), "currency-symbol");
		Assert.assertTrue("Has currency symbol", list.getLength() > 0);
		cSymbol = (NumberCurrencySymbolElement) list.item(0);
		Assert.assertEquals(language, cSymbol.getNumberLanguageAttribute());

	}

	/**
	 * Test of setMapPositive method, of class OdfNumberCurrencyStyle.
	 */
	@Test
	public void testSetMapPositive() {
		Node node;
		StyleMapElement mapNode;

		LOG.info("setMapPositive");
		String mapName = "positiveMap";
		OdfNumberCurrencyStyle instance = new OdfNumberCurrencyStyle(dom,
				"$", "#0", "fstyle");
		instance.setMapPositive(mapName);
		node = instance.getLastChild();
		Assert.assertNotNull(node);
		Assert.assertTrue(node instanceof StyleMapElement);
		mapNode = (StyleMapElement) node;
		Assert.assertEquals("value()>0", mapNode.getStyleConditionAttribute());
		Assert.assertEquals(mapName, mapNode.getStyleApplyStyleNameAttribute());
	}

	/**
	 * Test of setMapNegative method, of class OdfNumberCurrencyStyle.
	 */
	@Test
	public void testSetMapNegative() {
		Node node;
		StyleMapElement mapNode;

		LOG.info("setMapNegative");
		String mapName = "negativeMap";
		OdfNumberCurrencyStyle instance = new OdfNumberCurrencyStyle(dom,
				"$", "#0", "fstyle");
		instance.setMapNegative(mapName);
		node = instance.getLastChild();
		Assert.assertNotNull(node);
		Assert.assertTrue(node instanceof StyleMapElement);
		mapNode = (StyleMapElement) node;
		Assert.assertEquals("value()<0", mapNode.getStyleConditionAttribute());
		Assert.assertEquals(mapName, mapNode.getStyleApplyStyleNameAttribute());
	}

}