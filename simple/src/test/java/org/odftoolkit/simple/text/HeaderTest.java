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
package org.odftoolkit.simple.text;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class HeaderTest {

	String headerDocumentPath = "HeaderTableDocument.odt";

	@Test
	public void testAddTable() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			Assert.assertNotNull(header);
			
			Table table = header.addTable();
			table.setTableName("headerTable");
			int rowCount = table.getRowCount();
			int columnCount = table.getColumnCount();
			String expectedCellValue = "header table cell";
			table.getCellByPosition(1, 1).setStringValue(expectedCellValue);
			doc.save(ResourceUtilities.newTestOutputFile(headerDocumentPath));
			
			//load the document again.
			doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(headerDocumentPath));
			header = doc.getHeader();
			table = header.getTableByName("headerTable");
			Assert.assertEquals(rowCount, table.getRowCount());
			Assert.assertEquals(columnCount, table.getColumnCount());
			Assert.assertEquals(expectedCellValue, table.getCellByPosition(1, 1).getStringValue());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
