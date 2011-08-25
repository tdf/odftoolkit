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
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.Field.FieldType;

public class SubjectFieldTest {

	@Test
	public void testGetFieldType() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			SubjectField subjectField = Fields.createSubjectField(doc.newParagraph("The Subject:"));
			
			FieldType fieldType = subjectField.getFieldType();
			Assert.assertNotNull(subjectField);
			Assert.assertEquals(fieldType, FieldType.SUBJECT_FIELD);
		} catch (Exception e) {
			Logger.getLogger(SubjectFieldTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
