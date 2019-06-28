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

package org.odftoolkit.simple.style;

import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class TablePropertiesTest {

	private static final Logger LOGGER = Logger
			.getLogger(TablePropertiesTest.class.getName());

	@Test
	public void testGetSetPageBreak() {
		try {

			TextDocument doc = TextDocument.newTextDocument();
			doc.addParagraph("This is the first paragraph.");
			Table table = doc.addTable();
			TableProperties writeProperties = table.getStyleHandler()
					.getTablePropertiesForWrite();
			TableProperties readProperties = table.getStyleHandler()
					.getTablePropertiesForRead();

			writeProperties.setBreak("before", "page");
			Assert.assertEquals("page", readProperties.getBreakBefore());

			// save
			doc.save(ResourceUtilities
					.newTestOutputFile("AAAA1.ods"));

			writeProperties.setBreak("before", null);
			Assert.assertEquals(null, readProperties.getBreakBefore());

			writeProperties.setBreak(null, "page");
			Assert.assertEquals(null, readProperties.getBreakBefore());

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetPageNumber() {
		try {

			TextDocument doc = TextDocument.newTextDocument();
			doc.addParagraph("This is the first paragraph.");
			Table table = doc.addTable();
			TableProperties writeProperties = table.getStyleHandler()
					.getTablePropertiesForWrite();
			TableProperties readProperties = table.getStyleHandler()
					.getTablePropertiesForRead();

			writeProperties.setBreak("before", "page");
			Assert.assertEquals("page", readProperties.getBreakBefore());
			writeProperties.setPageNumber(3);
			Assert.assertEquals(3, readProperties.getPageNumber());

			// save
			doc.save(ResourceUtilities
					.newTestOutputFile("AAAA2.ods"));

			writeProperties.setPageNumber(0);
			Assert.assertEquals(0, readProperties.getPageNumber());
			writeProperties.setPageNumber(-2);
			Assert.assertEquals(0, readProperties.getPageNumber());

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
