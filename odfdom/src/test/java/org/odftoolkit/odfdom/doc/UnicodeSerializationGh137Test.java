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
package org.odftoolkit.odfdom.doc;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Test for Unicode astral character XML serialization bug.
 *
 * @see <a href="https://github.com/tdf/odftoolkit/issues/137">GH-137</a>
 * @author Daniel Gerhardt
 */
public class UnicodeSerializationGh137Test {
  @Test
  public void testSaveAndLoadDocumentWithUnicodeEmojiCharacter() throws Exception {
    OdfSpreadsheetDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument();
    // content contains 🙂 emoji
    String content = "text with an Unicode emoji \uD83D\uDE42";
    OdfTableCell cell = document.getSpreadsheetTables().get(0).getCellByPosition(0, 0);
    cell.setStringValue(content);
    File destination =
        File.createTempFile("gh137-test-emoji", ".ods", ResourceUtilities.getTempTestDirectory());
    document.save(destination);

    OdfSpreadsheetDocument loadedDocument = OdfSpreadsheetDocument.loadDocument(destination);
    OdfTableCell loadedCell = loadedDocument.getSpreadsheetTables().get(0).getCellByPosition(0, 0);
    Assert.assertEquals(content, loadedCell.getStringValue());
  }

  @Test
  public void testSaveAndLoadDocumentWithGreekCharacter() throws Exception {
    OdfSpreadsheetDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument();
    // content contains Greek 𝜈 (Nu) character
    String content = "text with a Greek character \uD835\uDF08";
    OdfTableCell cell = document.getSpreadsheetTables().get(0).getCellByPosition(0, 0);
    cell.setStringValue(content);
    File destination =
        File.createTempFile("gh137-test-greek", ".ods", ResourceUtilities.getTempTestDirectory());
    document.save(destination);

    OdfSpreadsheetDocument loadedDocument = OdfSpreadsheetDocument.loadDocument(destination);
    OdfTableCell loadedCell = loadedDocument.getSpreadsheetTables().get(0).getCellByPosition(0, 0);
    Assert.assertEquals(content, loadedCell.getStringValue());
  }

  @Test
  public void testSaveAndLoadDocumentWithPlainAscii() throws Exception {
    OdfSpreadsheetDocument document = OdfSpreadsheetDocument.newSpreadsheetDocument();
    String content = "simple ASCII text";
    OdfTableCell cell = document.getSpreadsheetTables().get(0).getCellByPosition(0, 0);
    cell.setStringValue(content);
    File destination =
        File.createTempFile("gh137-test-ascii", ".ods", ResourceUtilities.getTempTestDirectory());
    document.save(destination);

    OdfSpreadsheetDocument loadedDocument = OdfSpreadsheetDocument.loadDocument(destination);
    OdfTableCell loadedCell = loadedDocument.getSpreadsheetTables().get(0).getCellByPosition(0, 0);
    Assert.assertEquals(content, loadedCell.getStringValue());
  }
}
