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
package org.odftoolkit.simple.common.field;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.text.TextConditionalTextElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field.FieldType;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ConditionFieldTest {

	private static String TEST_DOCUMENT = "TextFieldSampleDocument.odt";
	
	@Test
	public void testUpdateCondition() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));

			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			Paragraph varParagraph = doc.addParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph.getOdfElement());

			// test condition field
			Paragraph newParagraph = doc.addParagraph("Condition Field Test:");
			ConditionField conditionField = Fields.createConditionField(newParagraph.getOdfElement(), "test_con_variable == \"true\"",
					"trueText", "falseText");
			Assert.assertNotNull(conditionField);
			conditionField.updateCondition("test_con_variable == \"false\"");
			TextConditionalTextElement textCondEle = (TextConditionalTextElement) conditionField.getOdfElement();
			Assert.assertEquals("ooow:test_con_variable == \"false\"", textCondEle.getTextConditionAttribute());
			//save
			//doc.save(ResourceUtilities.getAbsolutePath(TEST_DOCUMENT));
		} catch (Exception e) {
			Logger.getLogger(ConditionFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUpdateTrueText() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));

			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			Paragraph varParagraph = doc.addParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph.getOdfElement());

			// test condition field
			Paragraph newParagraph = doc.addParagraph("Condition Field Test:");
			ConditionField conditionField = Fields.createConditionField(newParagraph.getOdfElement(), "test_con_variable == \"true\"",
					"trueText", "falseText");
			Assert.assertNotNull(conditionField);
			conditionField.updateTrueText("trueTextUpdate");
			
			//validate
			TextConditionalTextElement conditionalTextElement = (TextConditionalTextElement) conditionField.getOdfElement();
			conditionalTextElement.getTextStringValueIfTrueAttribute();
			Assert.assertEquals("trueTextUpdate", conditionalTextElement.getTextStringValueIfTrueAttribute());
			
			//save
			//doc.save(ResourceUtilities.getAbsolutePath(TEST_DOCUMENT));
		} catch (Exception e) {
			Logger.getLogger(ConditionFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testUpdateFalseText() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));

			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			Paragraph varParagraph = doc.addParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph.getOdfElement());

			// test condition field
			Paragraph newParagraph = doc.addParagraph("Condition Field Test:");
			ConditionField conditionField = Fields.createConditionField(newParagraph.getOdfElement(), "test_con_variable == \"true\"",
					"trueText", "falseText");
			Assert.assertNotNull(conditionField);
			
			conditionField.updateCondition("test_con_variable == \"false\"");
			conditionField.updateFalseText("falseTextUpdate");

			//validate
			TextConditionalTextElement conditionalTextElement = (TextConditionalTextElement) conditionField.getOdfElement();
			conditionalTextElement.getTextStringValueIfFalseAttribute();
			Assert.assertEquals("falseTextUpdate", conditionalTextElement.getTextStringValueIfFalseAttribute());
		} catch (Exception e) {
			Logger.getLogger(ConditionFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetOdfElement() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));

			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			Paragraph varParagraph = doc.addParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph.getOdfElement());

			// test condition field
			Paragraph newParagraph = doc.addParagraph("Condition Field Test:");
			ConditionField conditionField = Fields.createConditionField(newParagraph.getOdfElement(), "test_con_variable == \"true\"",
					"trueText", "falseText");
			Assert.assertNotNull(conditionField);

			// validate
			TextConditionalTextElement conditionalTextElement = (TextConditionalTextElement) conditionField.getOdfElement();
			conditionalTextElement.getTextStringValueIfFalseAttribute();
			Assert.assertEquals("falseText", conditionalTextElement.getTextStringValueIfFalseAttribute());
			
		} catch (Exception e) {
			Logger.getLogger(ConditionFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testFieldType() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));

			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_con_variable");
			Paragraph varParagraph = doc.addParagraph("test_con_variable:");
			simpleVariableField.updateField("true", varParagraph.getOdfElement());

			// test condition field
			Paragraph newParagraph = doc.addParagraph("Condition Field Test:");
			ConditionField conditionField = Fields.createConditionField(newParagraph.getOdfElement(), "test_con_variable == \"true\"",
					"trueText", "falseText");
			Assert.assertNotNull(conditionField);
			
			FieldType fieldType = conditionField.getFieldType();
			Assert.assertEquals(FieldType.CONDITION_FIELD, fieldType);

			// test hide field
			newParagraph = doc.addParagraph("Hide Text Field Test:");
			conditionField = Fields.createHiddenTextField(newParagraph.getOdfElement(), "test_con_variable == \"true\"", "hiddenText");
			FieldType fieldType1 = conditionField.getFieldType();
			Assert.assertEquals(FieldType.HIDDEN_TEXT_FIELD, fieldType1);
		} catch (Exception e) {
			Logger.getLogger(ConditionFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
