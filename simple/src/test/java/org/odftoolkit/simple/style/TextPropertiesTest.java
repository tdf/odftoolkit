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
package org.odftoolkit.simple.style;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class TextPropertiesTest {
	private static final Logger LOGGER =  Logger.getLogger(TextPropertiesTest.class.getName());
	
	@Test
	public void testGetFontSizeInPoint() {
		try {
			SpreadsheetDocument document = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = document.getTableByName("Sheet1");
			Font font = new Font("Arial", StyleTypeDefinitions.FontStyle.ITALIC, 17.5, Color.GREEN, StyleTypeDefinitions.TextLinePosition.REGULAR, Locale.ENGLISH);
			Cell cell = table.getCellByPosition("A1");
			cell.setFont(font);
			cell.setStringValue("testGetFontStyle.");
			
			TextProperties textProperties = cell.getStyleHandler().getTextPropertiesForWrite();
			textProperties.setFontSizeInPoint(3.32);
			
			//validate
			Double fontInPoint = textProperties.getFontSizeInPoint();
			Assert.assertEquals(3.32, fontInPoint);
			
			//save
			document.save(ResourceUtilities.newTestOutputFile("testFontOutput89.ods"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	
	
}
