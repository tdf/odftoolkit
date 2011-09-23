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
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field.FieldType;
import org.odftoolkit.simple.common.field.PageNumberField.DisplayType;

public class PageNumberFieldTest {

	@Test
	public void testGetFieldType() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			PageNumberField numberField = Fields.createCurrentPageNumberField(doc.newParagraph("Current Page Number:"));
			Assert.assertNotNull(numberField);
			FieldType fieldType = numberField.getFieldType();
			Assert.assertNotNull(fieldType);
			Assert.assertEquals(fieldType, FieldType.CURRENT_PAGE_NUMBER_FIELD);
			numberField.setDisplayPage(DisplayType.PREVIOUS_PAGE);
			FieldType fieldType1 = numberField.getFieldType();
			Assert.assertEquals(fieldType1, FieldType.PREVIOUS_PAGE_NUMBER_FIELD);
			numberField.setDisplayPage(DisplayType.NEXT_PAGE);
			FieldType fieldType2 = numberField.getFieldType();
			Assert.assertEquals(fieldType2, FieldType.NEXT_PAGE_NUMBER_FIELD);
			
		} catch (Exception e) {
			Logger.getLogger(PageNumberFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
