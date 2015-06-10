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

package org.odftoolkit.simple;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.odfdom.type.Color;

public class SpreadsheetWithoutTableCellPropertiesTest {
	
	private final static String TEST_FILE_NAME = "TestCellWithoutTableCellProperties.ods";

	@Test
	public void testGetCellBackgroundColorOfEmptySpace() throws Exception {
		SpreadsheetDocument document = SpreadsheetDocument.loadDocument(ResourceUtilities
			.getTestResourceAsStream(TEST_FILE_NAME));
		Table table = document.getSheetByIndex(0);
		Cell cell = table.getCellByPosition(0, 0);
		Color color = cell.getCellBackgroundColor();
		Assert.assertEquals(color, org.odftoolkit.odfdom.type.Color.WHITE);
	}
}
