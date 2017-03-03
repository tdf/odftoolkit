/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.incubator.search;

import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Test the method of class org.odftoolkit.odfdom.incubator.search.TextStyleNavigation 
 */
public class TextStyleNavigationTest {

	private static final Logger LOG = Logger.getLogger(TextStyleNavigationTest.class.getName());
	public static final String TEXT_FILE = "TestStyleSelection.odt";
	public static final String SAVE_FILE_PAST_FRONT = "TextStyleSelectionResultInsertFront.odt";
	public static final String SAVE_FILE_PAST_END = "TextStyleSelectionResultInsertEnd.odt";
	public static final String SAVE_FILE_DELETE = "TextStyleSelectionResultDelete.odt";
	public static final String SAVE_FILE_STYLE = "TextStyleSelectionResultStyle.odt";
	public static final String SAVE_FILE_REPLACE = "TextStyleSelectionResultReplace.odt";
	public static final String SAVE_FILE_COPYTO = "TextStyleSelectionResultCopyTo.odt";
	public static final String SAVE_FILE_COPYTO1 = "TextStyleSelectionResultCopyTo1.odt";
	public static final String SAVE_FILE_CUT_FOOTERHEADER = "TextStyleSelectionResultCutFooterHeader.odt";
	public static final String SAVE_FILE_APPLY_FOOTERHEADER = "TextStyleSelectionResultApplyFooterHeader.odt";
	OdfTextDocument doc;
	TextStyleNavigation search1;
	TextNavigation search2;
	TextNavigation search3;
	TextStyleNavigation search4;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			doc = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test pasteAtFrontOf method of org.odftoolkit.odfdom.incubator.search.TextStyleNavigation
	 */
	@Test
	public void testPasteAtFrontOf() {

		//search the text of specified style, then insert it before specified text (delete)
		search1 = null;
		TreeMap<OdfStyleProperty, String> searchProps = new TreeMap<OdfStyleProperty, String>();
		searchProps.put(StyleTextPropertiesElement.FontName, "Times New Roman1");
		searchProps.put(StyleTextPropertiesElement.FontSize, "16pt");
		search1 = new TextStyleNavigation(searchProps, doc);
		search2 = new TextNavigation("delete", doc);
		search3 = new TextNavigation("Roman16 Romanl16delete", doc);

		TextSelection itemstyle = null;
		if (search1.hasNext()) {
			itemstyle = (TextSelection) search1.getCurrentItem();
			LOG.info(itemstyle.toString());
		}
		int i = 0;
		if (itemstyle != null) {
			while (search2.hasNext()) {
				i++;
				TextSelection itemtext = (TextSelection) search2.getCurrentItem();
				try {
					itemstyle.pasteAtFrontOf(itemtext);
				} catch (InvalidNavigationException e) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
					Assert.fail(e.getMessage());
				}
				LOG.info(itemtext.toString());
			}
		}

		int j = 0;
		while (search3.hasNext()) {
			j++;
		}
		Assert.assertTrue(i == j);

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_PAST_FRONT));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test pasteAtEndOf method of org.odftoolkit.odfdom.incubator.search.TextStyleNavigation
	 */
	@Test
	public void testPasteAtEndOf() {

		//search the text of specified style, then insert it after specified text (delete)
		TreeMap<OdfStyleProperty, String> searchProps = new TreeMap<OdfStyleProperty, String>();
		searchProps.put(StyleTextPropertiesElement.FontName, "Times New Roman1");
		searchProps.put(StyleTextPropertiesElement.FontSize, "16pt");
		search1 = new TextStyleNavigation(searchProps, doc);
		search2 = new TextNavigation("delete", doc);
		search3 = new TextNavigation("deleteRoman16 Romanl16", doc);
		TextSelection itemstyle = null;
		if (search1.hasNext()) {
			itemstyle = (TextSelection) search1.getCurrentItem();
			LOG.info(itemstyle.toString());
		}
		int i = 0;
		if (itemstyle != null) {
			while (search2.hasNext()) {
				i++;
				TextSelection itemtext = (TextSelection) search2.getCurrentItem();
				try {
					itemstyle.pasteAtEndOf(itemtext);
				} catch (InvalidNavigationException e) {
					Assert.fail(e.getMessage());
				}
				LOG.info(itemtext.toString());
			}
		}

		int j = 0;
		while (search3.hasNext()) {
			j++;
		}

		Assert.assertTrue(i == j);

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_PAST_END));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test cut method of org.odftoolkit.odfdom.incubator.search.TextStyleNavigation
	 */
	@Test
	public void testCut() {

		//delete all text with specified style
		TreeMap<OdfStyleProperty, String> searchProps = new TreeMap<OdfStyleProperty, String>();
		searchProps.put(StyleTextPropertiesElement.FontName, "Century1");
		searchProps.put(StyleTextPropertiesElement.FontSize, "22pt");
		search1 = new TextStyleNavigation(searchProps, doc);
		search2 = new TextNavigation("Century22", doc);

		while (search1.hasNext()) {
			TextSelection item = (TextSelection) search1.getCurrentItem();
			try {
				item.cut();
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			}
		}

		Assert.assertFalse(search2.hasNext());

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_DELETE));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test applyStyle method of org.odftoolkit.odfdom.incubator.search.TextStyleNavigation
	 */
	@Test
	public void testApplyStyle() {
		//select the text specified style and apply the text with new style.
		TreeMap<OdfStyleProperty, String> searchProps = new TreeMap<OdfStyleProperty, String>();
		searchProps.put(StyleTextPropertiesElement.FontName, "Arial");
		searchProps.put(StyleTextPropertiesElement.FontSize, "12pt");
		search1 = new TextStyleNavigation(searchProps, doc);

		OdfStyle style = null;
		try {
			style = new OdfStyle(doc.getContentDom());
			style.setProperty(StyleTextPropertiesElement.FontSize, "23pt");
			style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
			style.setStyleFamilyAttribute("text");
		} catch (Exception e1) {
			LOG.log(Level.SEVERE, e1.getMessage(), e1);
			Assert.fail("Failed with " + e1.getClass().getName() + ": '" + e1.getMessage() + "'");
		}

		int i = 0;
		while (search1.hasNext()) {
			i++;
			TextSelection item = (TextSelection) search1.getCurrentItem();
			// LOG.info(item);
			try {
				item.applyStyle(style);
			} catch (InvalidNavigationException e) {
				Assert.fail(e.getMessage());
			}
		}

		TreeMap<OdfStyleProperty, String> chgProps = new TreeMap<OdfStyleProperty, String>();
		chgProps.put(StyleTextPropertiesElement.FontSize, "23pt");
		chgProps.put(StyleTextPropertiesElement.FontWeight, "bold");
		search4 = new TextStyleNavigation(chgProps, doc);
		int j = 0;
		while (search4.hasNext()) {
			j++;
		}
		Assert.assertTrue(i == j);

		try {
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_STYLE));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
