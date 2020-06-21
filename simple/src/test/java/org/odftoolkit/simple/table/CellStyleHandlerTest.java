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

package org.odftoolkit.simple.table;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class CellStyleHandlerTest {

  private static final String TEST_FILE_NAME = "CellStyleHandlerTest.odp";

  @Test
  public void testGetFont() {
    try {
      Document doc =
          Document.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_FILE_NAME));
      Table table = doc.getTableByName("slideTable");
      CellStyleHandler styleHandler = table.getCellByPosition(1, 1).getStyleHandler();
      // NullPointerException should not be thrown even if no default text
      // properties style exists.
      styleHandler.getFont(Document.ScriptType.WESTERN);
    } catch (Exception e) {
      Logger.getLogger(CellStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }

  @Test
  public void testGetCountry() {
    try {
      Document doc =
          Document.loadDocument(ResourceUtilities.getTestResourceAsStream(TEST_FILE_NAME));
      Table table = doc.getTableByName("slideTable");
      CellStyleHandler styleHandler = table.getCellByPosition(1, 1).getStyleHandler();

      styleHandler.setCountry("English", Document.ScriptType.WESTERN);
      // validate
      String country = styleHandler.getCountry(Document.ScriptType.WESTERN);
      Assert.assertEquals("English", country);

    } catch (Exception e) {
      Logger.getLogger(CellStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }
}
