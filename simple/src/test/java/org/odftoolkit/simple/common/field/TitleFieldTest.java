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

public class TitleFieldTest {

  @Test
  public void testGetFieldType() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      TitleField titleField = Fields.createTitleField(doc.newParagraph("The Title:"));
      Assert.assertNotNull(titleField);
      FieldType fType = titleField.getFieldType();
      Assert.assertEquals(fType, FieldType.TITLE_FIELD);
    } catch (Exception e) {
      Logger.getLogger(TitleFieldTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }
}
