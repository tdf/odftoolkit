/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.doc.number;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.style.OdfStyleMap;
import org.w3c.dom.Node;

/**
 * @author Daisy
 *
 */
public class OdfPercentageStyleTest {

	OdfSpreadsheetDocument doc;
	OdfFileDom dom;

    public OdfPercentageStyleTest() {
    }

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

    @Before
    public void setUp() {
        try
        {
            doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
			dom = doc.getContentDom();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
    }

	/**
	 * Test of buildFromFormat method, of class OdfNumberStyle.
	 */
	@Test
	public void testBuildFromFormat() {
		int n;
		String[] formatTest = {
			"##%",
			"#0%",
			"#00%",
			"#,###%",
			"#,##0%",
			"#,##0.00%",
			"before:##0%",
			"##0%:after",
			"before:##0%:after"
		};

		String[] expectedFormat = {
				"#%",
				"#0%",
				"#00%",
				"#,###%",
				"#,##0%",
				"#,##0.00%",
				"before:#0%",
				"#0%:after",
				"before:#0%:after"
		};

		System.out.println("buildFromFormat");
		OdfNumberPercentageStyle instance = null;

		for (int i = 0; i < formatTest.length; i++)
		{
			System.out.println("Number format: " + formatTest[i]);
			instance = new OdfNumberPercentageStyle(dom,
				formatTest[i], "fstyle");
			Assert.assertNotNull(instance);

			Assert.assertEquals(expectedFormat[i], instance.getFormat());
		}
	}

	/**
	 * Test of setMapPositive method, of class OdfNumberNumberStyle.
	 */
	@Test
	public void testSetMapPositive() {
		Node node;
		OdfStyleMap mapNode;

		System.out.println("setMapPositive");
		String mapName = "positiveMap";
		OdfNumberPercentageStyle instance = new OdfNumberPercentageStyle(dom,
				"#0", "fstyle");
		instance.setMapPositive(mapName);
		node = instance.getLastChild();
		Assert.assertNotNull(node);
		Assert.assertTrue(node instanceof OdfStyleMap);
		mapNode = (OdfStyleMap) node;
		Assert.assertEquals("value()>0", mapNode.getStyleConditionAttribute());
		Assert.assertEquals(mapName, mapNode.getStyleApplyStyleNameAttribute());
	}

	/**
	 * Test of setMapNegative method, of class OdfNumberNumberStyle.
	 */
	@Test
	public void testSetMapNegative() {
		Node node;
		OdfStyleMap mapNode;

		System.out.println("setMapNegative");
		String mapName = "negativeMap";
		OdfNumberPercentageStyle instance = new OdfNumberPercentageStyle(dom,
				"#0", "fstyle");
		instance.setMapNegative(mapName);
		node = instance.getLastChild();
		Assert.assertNotNull(node);
		Assert.assertTrue(node instanceof OdfStyleMap);
		mapNode = (OdfStyleMap) node;
		Assert.assertEquals("value()<0", mapNode.getStyleConditionAttribute());
		Assert.assertEquals(mapName, mapNode.getStyleApplyStyleNameAttribute());
	}
	
}
