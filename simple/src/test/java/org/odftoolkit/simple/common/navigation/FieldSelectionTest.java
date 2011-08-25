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
package org.odftoolkit.simple.common.navigation;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.text.TextConditionalTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextDateElement;
import org.odftoolkit.odfdom.dom.element.text.TextReferenceRefElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextUserFieldGetElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field;
import org.odftoolkit.simple.common.field.Fields;
import org.odftoolkit.simple.common.field.ReferenceField;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.common.field.ReferenceField.DisplayType;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

/**
 * Test the method of class
 * org.odftoolkit.simple.common.navigation.FieldSelection
 */
public class FieldSelectionTest {

	public static final String TEXT_FILE = "TextFieldSampleDocument.odt";
	public static final String SAVE_FILE_STYLE = "TextFieldResultStyle.odt";
	public static final String SAVE_FILE_REPLACE = "TextFieldResultReplace.odt";

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
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
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
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testReplaceWithSimpleField() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextDateElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextDateElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.FIXED_DATE_FIELD);
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextDateElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextDateElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testReplaceWithConditionField() {
		try {
			search = new TextNavigation("ReplaceConditionTarget", doc);
			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			OdfTextParagraph varParagraph = doc.newParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph);
			// count the initial date field count. 
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 2 "ReplaceConditionTarget" to ConditionField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithConditionField("test_con_variable == \"true\"", "trueText", "falseText");
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testReplaceWithHiddenTextField() {
		try {
			search = new TextNavigation("ReplaceHiddenTextTarget", doc);
			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			OdfTextParagraph varParagraph = doc.newParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph);

			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 2 "ReplaceHiddenTextTarget" to HiddenTextField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithHiddenTextField("test_con_variable == \"true\"", "hiddenText");
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextConditionalTextElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testReplaceWithReferenceField() {
		try {
			search = new TextNavigation("ReplaceReferenceTarget", doc);
			TextSpanElement newTextSpanElement = doc.newParagraph("Selection Reference Content:").newTextSpanElement();
			newTextSpanElement.setTextContent("This is a test selection reference content.");
			ReferenceField referenceField = Fields.createReferenceField(newTextSpanElement, "selection-test-ref");
			// count the initial reference field count.
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextReferenceRefElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextReferenceRefElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 2 "ReplaceReferenceTarget" to ReferenceField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithReferenceField(referenceField, DisplayType.TEXT);
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextReferenceRefElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextReferenceRefElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testReplaceWithVariableField() {
		try {
			search = new TextNavigation("SelectionUserVariableTarget", doc);
			// declare variable
			VariableField userVariableField = Fields.createUserVariableField(doc, "selection_user_variable", "test");
			// count the initial variable field count.
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextUserFieldGetElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextUserFieldGetElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "SelectionUserVariableTarget" to VariableField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithVariableField(userVariableField);
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextUserFieldGetElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextUserFieldGetElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}