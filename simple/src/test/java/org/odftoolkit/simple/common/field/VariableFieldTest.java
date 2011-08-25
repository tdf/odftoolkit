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
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field.FieldType;
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
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
