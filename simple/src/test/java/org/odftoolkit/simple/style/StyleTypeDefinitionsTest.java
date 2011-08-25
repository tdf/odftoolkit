/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple.style;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class StyleTypeDefinitionsTest {
	
	private static final Logger LOGGER =  Logger.getLogger(StyleTypeDefinitionsTest.class.getName());
	
	@Test
	public void testHorizontalAlignmentType() {
		try {
			
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph1 = doc.addParagraph("paragraph text");

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.DEFAULT);
			HorizontalAlignmentType align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.DEFAULT, align);
			
			HorizontalAlignmentType filled = HorizontalAlignmentType.enumValueOf("filled");
			String start = filled.getAlignmentString();
			Assert.assertEquals("start", start);
			Assert.assertEquals("filled", filled.toString());
			Assert.assertEquals(HorizontalAlignmentType.FILLED, filled);
			
			HorizontalAlignmentType testnull = HorizontalAlignmentType.enumValueOf("");
			Assert.assertEquals(null, testnull);
			
			try {
				HorizontalAlignmentType testa = HorizontalAlignmentType.enumValueOf("aaaa");
				Assert.assertEquals("Unsupported Horizontal Alignment Type!", testa);
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
				Assert.assertEquals("Unsupported Horizontal Alignment Type!", e.getMessage());
			}
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testHorizontalAlignmentType.odt"));
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	

	@Test
	public void testVerticalAlignmentType() throws Exception{
		try {
			SpreadsheetDocument odsdoc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestSpreadsheetTable.ods"));
		
			int columnindex = 0, rowindex = 3;
			Table table = odsdoc.getTableByName("Sheet1");
			Cell fcell = table.getCellByPosition(columnindex, rowindex);
	
			System.out.println(fcell.getStringValue());
			
			StyleTypeDefinitions.VerticalAlignmentType align = fcell.getVerticalAlignmentType();
			Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.TOP, align);
			
			VerticalAlignmentType vertType = VerticalAlignmentType.enumValueOf("top");
			Assert.assertEquals(VerticalAlignmentType.TOP, vertType);
			Assert.assertEquals("top", vertType.toString());
			
			VerticalAlignmentType testnull = VerticalAlignmentType.enumValueOf("");
			Assert.assertEquals(null, testnull);
			
			try {
				VerticalAlignmentType testa = VerticalAlignmentType.enumValueOf("aaaa");
				Assert.assertEquals("Unsupported Vertical Alignment Type!", testa);
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
				Assert.assertEquals("Unsupported Vertical Alignment Type!", e.getMessage());
			}
			
			//save
			//odsdoc.save(ResourceUtilities.newTestOutputFile("TestSpreadsheetTableOutput.ods"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	
}
