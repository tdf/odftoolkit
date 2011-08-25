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

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FooterTest {

	String footerDocumentPath = "FooterTableDocument.odt";

	@Test
	public void testAddTable() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Footer footer = doc.getFooter();
			Assert.assertNotNull(footer);

			Table table = footer.addTable(1, 1);
			table.setTableName("footerTable");
			int rowCount = table.getRowCount();
			int columnCount = table.getColumnCount();
			String expectedCellValue = "footer table cell";
			Cell cellByPosition = table.getCellByPosition(0, 0);
			cellByPosition.setStringValue(expectedCellValue);
			cellByPosition.setHorizontalAlignment(HorizontalAlignmentType.CENTER);
			cellByPosition.setCellBackgroundColor(Color.GREEN);
			
			//first page
			footer = doc.getFooter(true);
			Assert.assertNotNull(footer);
			table = footer.addTable(1, 2);
			table.setTableName("footerFTable");
			doc.save(ResourceUtilities.newTestOutputFile(footerDocumentPath));

			// load the document again.
			doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(footerDocumentPath));
			footer = doc.getFooter();
			table = footer.getTableByName("footerTable");
			Assert.assertEquals(rowCount, table.getRowCount());
			Assert.assertEquals(columnCount, table.getColumnCount());
			Assert.assertEquals(expectedCellValue, cellByPosition.getStringValue());

			footer = doc.getFooter(true);
			table = footer.getTableByName("footerFTable");
			Assert.assertNotNull(table);
		} catch (Exception e) {
			Logger.getLogger(FooterTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
