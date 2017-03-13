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
import org.odftoolkit.odfdom.dom.element.text.TextAuthorInitialsElement;
import org.odftoolkit.odfdom.dom.element.text.TextAuthorNameElement;
import org.odftoolkit.odfdom.dom.element.text.TextChapterElement;
import org.odftoolkit.odfdom.dom.element.text.TextConditionalTextElement;
import org.odftoolkit.odfdom.dom.element.text.TextDateElement;
import org.odftoolkit.odfdom.dom.element.text.TextPageCountElement;
import org.odftoolkit.odfdom.dom.element.text.TextPageNumberElement;
import org.odftoolkit.odfdom.dom.element.text.TextReferenceRefElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextSubjectElement;
import org.odftoolkit.odfdom.dom.element.text.TextTimeElement;
import org.odftoolkit.odfdom.dom.element.text.TextTitleElement;
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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
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
	
	
	@Test
	public void testReplaceWithSimpleField2() {
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
				//fieldSelection.replaceWithSimpleField(Field.FieldType.FIXED_DATE_FIELD);
				fieldSelection.replaceWithSimpleField(Field.FieldType.DATE_FIELD);	// 1
				//fieldSelection.replaceWithSimpleField(Field.FieldType.TIME_FIELD);	//3
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
	public void testReplaceWithSimpleField_time_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			
			//change all <code>ext:fixed</code> value be false 
			for(int k =0;k<nodeList.getLength();k++){
				Node nv = nodeList.item(k);
				if(nv.getFirstChild().getNodeValue().matches("^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")){
					NamedNodeMap nameMap = nv.getAttributes();
					Node nfix = nameMap.getNamedItem("text:fixed");
					nfix.setNodeValue("false");
				}
			}
			
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				//fieldSelection.replaceWithSimpleField(Field.FieldType.FIXED_DATE_FIELD);
				fieldSelection.replaceWithSimpleField(Field.FieldType.TIME_FIELD);
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
			//validate  
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			OdfContentDom contentDom1 = doc1.getContentDom();
			
			nodeList = contentDom1.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList.getLength();k++){
				//
				Node nv = nodeList.item(k);
				if(nv.getFirstChild().getNodeValue().matches("^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")){
					NamedNodeMap nameMap = nv.getAttributes();
					Node nfix = nameMap.getNamedItem("text:fixed");
					System.out.println("&&& " + nfix.getNodeValue());
					Assert.assertEquals("false", nfix.getNodeValue());
				}
			}
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_fixed_time_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			NodeList nodeList = contentDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.FIXED_TIME_FIELD);
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextTimeElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	

	@Test
	public void testReplaceWithSimpleField_previous_page_number_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList1 = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList1.getLength();k++){
				//
				Node nv = nodeList1.item(k);
				NamedNodeMap nameMap = nv.getAttributes();
				Node nfix = nameMap.getNamedItem("text:select-page");
				nfix.setNodeValue("previous");
			}
			
			NodeList nodeList = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.PREVIOUS_PAGE_NUMBER_FIELD);
				i++;
			}
			nodeList = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
			//validate
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			OdfContentDom contentDom1 = doc1.getContentDom();
			
			nodeList = contentDom1.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList.getLength();k++){
				Node nv = nodeList.item(k);
				NamedNodeMap nameMap = nv.getAttributes();
				Node nfix = nameMap.getNamedItem("text:select-page");
				Assert.assertEquals("previous", nfix.getNodeValue());
				
			}
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_current_page_number_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList1 = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList1.getLength();k++){
				//
				Node nv = nodeList1.item(k);
				NamedNodeMap nameMap = nv.getAttributes();
				Node nfix = nameMap.getNamedItem("text:select-page");
				nfix.setNodeValue("current");
			}
			
			NodeList nodeList = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.CURRENT_PAGE_NUMBER_FIELD);
				i++;
			}
			
			//value
			nodeList = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
			//validate
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			OdfContentDom contentDom1 = doc1.getContentDom();
			
			nodeList = contentDom1.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList.getLength();k++){
				Node nv = nodeList.item(k);
				NamedNodeMap nameMap = nv.getAttributes();
				Node nfix = nameMap.getNamedItem("text:select-page");
				System.out.println(nfix.getNodeValue());
				Assert.assertEquals("current", nfix.getNodeValue());
			}
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testReplaceWithSimpleField_next_page_number_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList1 = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList1.getLength();k++){
				//
				Node nv = nodeList1.item(k);
				NamedNodeMap nameMap = nv.getAttributes();
				Node nfix = nameMap.getNamedItem("text:select-page");
				nfix.setNodeValue("next");
			}
			
			NodeList nodeList = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.NEXT_PAGE_NUMBER_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
			//validate
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			OdfContentDom contentDom1 = doc1.getContentDom();
			
			nodeList = contentDom1.getElementsByTagName(TextPageNumberElement.ELEMENT_NAME.getQName());
			for(int k =0;k<nodeList.getLength();k++){
				Node nv = nodeList.item(k);
				NamedNodeMap nameMap = nv.getAttributes();
				Node nfix = nameMap.getNamedItem("text:select-page");
				System.out.println(nfix.getNodeValue());
				Assert.assertEquals("next", nfix.getNodeValue());
			}
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_page_count_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList = contentDom.getElementsByTagName(TextPageCountElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextPageCountElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.PAGE_COUNT_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextPageCountElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextPageCountElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testReplaceWithSimpleField_title_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList = contentDom.getElementsByTagName(TextTitleElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextTitleElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.TITLE_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextTitleElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextTitleElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_subject_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList = contentDom.getElementsByTagName(TextSubjectElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextSubjectElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.SUBJECT_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextSubjectElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextSubjectElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testReplaceWithSimpleField_author_name_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList = contentDom.getElementsByTagName(TextAuthorNameElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextAuthorNameElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.AUTHOR_NAME_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextAuthorNameElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextAuthorNameElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testReplaceWithSimpleField_author_initials_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList = contentDom.getElementsByTagName(TextAuthorInitialsElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextAuthorInitialsElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.AUTHOR_INITIALS_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextAuthorInitialsElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextAuthorInitialsElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_chapter_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			// count the initial date field count. should be 1.
			OdfContentDom contentDom = doc.getContentDom();
			
			NodeList nodeList = contentDom.getElementsByTagName(TextChapterElement.ELEMENT_NAME.getQName());
			int i = nodeList.getLength();
			OdfStylesDom styleDom = doc.getStylesDom();
			nodeList = styleDom.getElementsByTagName(TextChapterElement.ELEMENT_NAME.getQName());
			i += nodeList.getLength();
			// replace all the 3 "ReplaceDateTarget" to FixedDateField.
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.CHAPTER_FIELD);
				i++;
			}
			
			//validate
			nodeList = contentDom.getElementsByTagName(TextChapterElement.ELEMENT_NAME.getQName());
			int j = nodeList.getLength();
			nodeList = styleDom.getElementsByTagName(TextChapterElement.ELEMENT_NAME.getQName());
			j += nodeList.getLength();
			Assert.assertEquals(j, i);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_reference_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.REFERENCE_FIELD);
			}
		} catch (Exception e) {
			//Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("this is not a vaild simple field type.", e.getMessage());
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_simple_variable_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.SIMPLE_VARIABLE_FIELD);
			}
		} catch (Exception e) {
			//Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("this is not a vaild simple field type.", e.getMessage());
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_user_variable_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.USER_VARIABLE_FIELD);
			}
		} catch (Exception e) {
			//Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("this is not a vaild simple field type.", e.getMessage());
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_condition_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.CONDITION_FIELD);
			}
		} catch (Exception e) {
			//Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("this is not a vaild simple field type.", e.getMessage());
		}
	}
	
	@Test
	public void testReplaceWithSimpleField_hidden_text_field() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWithSimpleField(Field.FieldType.HIDDEN_TEXT_FIELD);
			}
		} catch (Exception e) {
			//Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("this is not a vaild simple field type.", e.getMessage());
		}
	}
	
	
	@Test
	public void testReplaceWith() {
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			int i =0;
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.replaceWith("hello world.");
				i++;
			}
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));

			//validate  
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			
			search = new TextNavigation("ReplaceDateTarget", doc1);
			if (search.hasNext()) {
				Assert.fail();
			}
			
			search = new TextNavigation("hello world.", doc1);
			int j =0;
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				item.replaceWith("hi world.");
				j++;
			}
			Assert.assertEquals(i, j);
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	
	@Test
	public void testPasteAtEndOf() {
		try {
			
			//count of TextSelection("ReplaceDateTargetchange")
			TextNavigation search3 = new TextNavigation("ReplaceDateTargetchange", doc);
			int j = 0;
			while(search3.hasNext()){
				TextSelection item = (TextSelection) search3.nextSelection();
				item.getText();
				j++;
			}
			
			search = new TextNavigation("ReplaceDateTarget", doc);
			TextNavigation search2 = new TextNavigation("change", doc);
			TextSelection pastesource = null;
			if(search2.hasNext()){
				pastesource = (TextSelection)search2.nextSelection();
			}
			if(pastesource == null)
				Assert.fail("pastesource == null.");
			
			int i =0;
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				pastesource.pasteAtEndOf(item);
				i++;
			}
			
			int count = i+j;
			System.out.println(count);
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));

			//validate  
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			TextNavigation search4 = new TextNavigation("ReplaceDateTargetchange", doc1);
			int resultcount = 0;
			while (search4.hasNext()) {
				TextSelection item = (TextSelection) search4.nextSelection();
				item.getText();
				//System.out.println(item.getText());
				
				resultcount++;
			}
			
			Assert.assertEquals(count, resultcount);
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	@Test
	public void testPasteAtFrontOf() {
		try {
			
			//count of TextSelection("ReplaceDateTargetchange")
			TextNavigation search3 = new TextNavigation("changeReplaceDateTarget", doc);
			int j = 0;
			while(search3.hasNext()){
				TextSelection item = (TextSelection) search3.nextSelection();
				item.getText();
				j++;
			}
			
			search = new TextNavigation("ReplaceDateTarget", doc);
			TextNavigation search2 = new TextNavigation("change", doc);
			TextSelection pastesource = null;
			if(search2.hasNext()){
				pastesource = (TextSelection)search2.nextSelection();
			}
			if(pastesource == null)
				Assert.fail("pastesource == null.");
			
			int i =0;
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				pastesource.pasteAtFrontOf(item);
				i++;
			}
			
			int count = i+j;

			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));

			//validate  
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			TextNavigation search4 = new TextNavigation("changeReplaceDateTarget", doc1);
			int resultcount = 0;
			while (search4.hasNext()) {
				TextSelection item = (TextSelection) search4.nextSelection();
				item.getText();
				//System.out.println(item.getText());
				
				resultcount++;
			}
			System.out.println(count);
			System.out.println(resultcount);
			
			Assert.assertEquals(count, resultcount);
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	
	
	@Test
	public void testCut() throws Exception{
		try {
			search = new TextNavigation("ReplaceDateTarget", doc);
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.nextSelection();
				FieldSelection fieldSelection = new FieldSelection(item);
				fieldSelection.cut();
			}
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE_REPLACE));

			//validate  
			TextDocument doc1 = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(SAVE_FILE_REPLACE));
			
			TextNavigation searchafter = new TextNavigation("ReplaceDateTarget", doc1);
			if (searchafter.hasNext()) {
				Assert.fail();
			}
			
		} catch (Exception e) {
			Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}	
}
