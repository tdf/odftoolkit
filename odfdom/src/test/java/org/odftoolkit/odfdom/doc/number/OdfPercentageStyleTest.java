/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/**
 * @author Daisy
 *
 */
public class OdfPercentageStyleTest {
	private static final Logger LOG = Logger.getLogger(OdfPercentageStyleTest.class.getName());
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
        	LOG.log(Level.SEVERE, e.getMessage(), e);
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
    @Ignore //   OdfPercentageStyleTest.testBuildFromFormat:117 expected:<[#]0%> but was:<[]0%>
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

		LOG.info("buildFromFormat");
		OdfNumberPercentageStyle instance = null;

		for (int i = 0; i < formatTest.length; i++)
		{
			LOG.info("Number format: " + formatTest[i]);
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
		StyleMapElement mapNode;

		LOG.info("setMapPositive");
		String mapName = "positiveMap";
		OdfNumberPercentageStyle instance = new OdfNumberPercentageStyle(dom,
				"#0", "fstyle");
		instance.setMapPositive(mapName);
		node = instance.getLastChild();
		Assert.assertNotNull(node);
		Assert.assertTrue(node instanceof StyleMapElement);
		mapNode = (StyleMapElement) node;
		Assert.assertEquals("value()>0", mapNode.getStyleConditionAttribute());
		Assert.assertEquals(mapName, mapNode.getStyleApplyStyleNameAttribute());
	}

	/**
	 * Test of setMapNegative method, of class OdfNumberNumberStyle.
	 */
	@Test
	public void testSetMapNegative() {
		Node node;
		StyleMapElement mapNode;

		LOG.info("setMapNegative");
		String mapName = "negativeMap";
		OdfNumberPercentageStyle instance = new OdfNumberPercentageStyle(dom,
				"#0", "fstyle");
		instance.setMapNegative(mapName);
		node = instance.getLastChild();
		Assert.assertNotNull(node);
		Assert.assertTrue(node instanceof StyleMapElement);
		mapNode = (StyleMapElement) node;
		Assert.assertEquals("value()<0", mapNode.getStyleConditionAttribute());
		Assert.assertEquals(mapName, mapNode.getStyleApplyStyleNameAttribute());
	}

}
