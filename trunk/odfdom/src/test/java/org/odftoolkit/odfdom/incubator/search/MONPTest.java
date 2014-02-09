/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.incubator.search;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Test ReplaceWith method for class org.odftoolkit.odfdom.incubator.search.TextSelection 
 */
public class MONPTest {

	public static final String TEXT_FILE = "navigationtest.odt";
	public static final String SAVE_FILE = "testsave1.odt";

	/**
	 * replace all the "mnop" occurance in navigationtest.odt with the word "success"
	 */
	@Test
	public void testReplaceWith() {

		try {
			OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));

			TextNavigation search = new TextNavigation("mnop", doc);

			int i = 0;
			while (search.hasNext()) {
				TextSelection item = (TextSelection) search.getCurrentItem();
				try {
					item.replaceWith("success");
					i++;
					// item.addHref(new URL("http://www.oracle.com"));
				} catch (InvalidNavigationException e) {
					Assert.fail(e.getMessage());
				}
			}
			Assert.assertTrue(18 == i);
			doc.save(ResourceUtilities.newTestOutputFile(SAVE_FILE));
		} catch (Exception e) {
			Logger.getLogger(MONPTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
