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

package org.odftoolkit.simple.common.field;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field.FieldType;
import org.odftoolkit.simple.common.navigation.FieldSelectionTest;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class VariableFieldTest {
	private static final Logger LOG = Logger.getLogger(VariableFieldTest.class.getName());
	private static String TEST_DOCUMENT = "TextFieldSampleDocument.odt";

	@Test
	public void testVariableTypeToString() {
		try {
			VariableField.VariableType variableType = VariableField.VariableType.valueOf(VariableField.VariableType.class, VariableField.VariableType.SIMPLE.name());
			String variableTypeStr = variableType.toString();
			Assert.assertNotNull(variableType);
			Assert.assertEquals("simple", variableTypeStr);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testVariableTypesisplayField() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_DOCUMENT));
			// declare simple variable
			VariableField simpleVariableField = Fields.createSimpleVariableField(doc, "test_simple_variable");
			Assert.assertNotNull(simpleVariableField);
			TextSpanElement newTextSpanElement = doc.newParagraph("Update Simple Variable Field:").newTextSpanElement();
			simpleVariableField.updateField("simple variable content", newTextSpanElement);
			
			FieldType fieldType = simpleVariableField.getFieldType();
			Assert.assertNotNull(fieldType);
			Assert.assertEquals(FieldType.SIMPLE_VARIABLE_FIELD, fieldType);
			newTextSpanElement = doc.newParagraph("Show Simple Variable Field:").newTextSpanElement();
			simpleVariableField.displayField(newTextSpanElement);
			simpleVariableField.updateField("aaaa", newTextSpanElement);
			try {
				doc.save(ResourceUtilities.newTestOutputFile("TextFieldSampleDocumentVariableField.odt"));
			} catch (Exception e) {
				Logger.getLogger(FieldSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
