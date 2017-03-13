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

package org.odftoolkit.simple.common.navigation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

/**
 * Test the method of class org.odftoolkit.simple.common.navigation.CellSelection
 */
public class CellSelectionTest {

	public static final String TEST_FILE         = "TestCellSelection.ods";
	public static final String SAVE_FILE_REPLACE = "CellSelectionResultReplace.ods";
	SpreadsheetDocument        doc;

	@Before
	public void setUp() {
		try {
			doc = (SpreadsheetDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
		} catch (Exception e) {
			Logger.getLogger(CellSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	/**
	 * Test replaceWith method of org.odftoolkit.simple.common.navigation.TextSelection replace all the 'SIMPLE' with 'Odf
	 * Toolkit'
	 */
	@Test
	public void testAdvancedReplacewith() {
		Map<String, String> valueProperties = new HashMap<String, String>();
		valueProperties.put("Amount", "3,000,000");
		valueProperties.put("Task", "Develop");
		valueProperties.put("Date", "11/09/2010");
		valueProperties.put("Time", "14:30");
		valueProperties.put("Status", "true");
		Map<String, String> typeProperties = new HashMap<String, String>();
		typeProperties.put("Amount", "float");
		typeProperties.put("Task", "string");
		typeProperties.put("Date", "date");
		typeProperties.put("Time", "time");
		typeProperties.put("Status", "boolean");
		
		// simple text replace
		for (String key : valueProperties.keySet()) {
			String value = valueProperties.get(key);
			TextNavigation navigate = new TextNavigation(key, doc);
			while (navigate.hasNext()) {
				CellSelection selection = (CellSelection) navigate.nextSelection();
				try {
					selection.advancedReplaceWith(value);
					String valueType = selection.getCell().getValueType();
					Assert.assertEquals(typeProperties.get(key), valueType);
				} catch (InvalidNavigationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * this method tests a simple replace with. Especially Line breaks
	 */
	@Test
	public void testReplaceWith() {
		Map<String, String> valueProperties = new HashMap<String, String>();
		valueProperties.put("Amount", "3,000,000");
		valueProperties.put("Task", "Develop\nWith a second line");
		valueProperties.put("Date", "11/09/2010");
		valueProperties.put("Time", "14:30");
		valueProperties.put("Status", "true");

		// simple text replace
		for (String key : valueProperties.keySet()) {
			String value = valueProperties.get(key);
			TextNavigation navigate = new TextNavigation(key, doc);
			while (navigate.hasNext()) {
				CellSelection selection = (CellSelection) navigate.nextSelection();
				try {
					// use a simple replace to replace the values
					selection.replaceWith(value);

					// get the cell's string value (which is different from the textvalue)
					String newValue = selection.getCell().getStringValue();

					// make sure that the cell's value is the same as the intended value
					Assert.assertEquals(valueProperties.get(key), newValue);
				} catch (InvalidNavigationException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
