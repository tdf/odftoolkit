/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.odftoolkit.odfdom.doc.table;

import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableColumn;
import java.util.List;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.OdfFileDom;

/**
 *
 * @author J David Eisenberg
 */
public class OdfTableTest {
	OdfTextDocument doc;
	OdfFileDom dom;

    public OdfTableTest() {
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
            doc = OdfTextDocument.newTextDocument();
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
	 * Test of addTableColumn method, of class OdfTable.
	 */
	@Test
	public void testAddTableColumn() {
		System.out.println("addTableColumn");
		String styleName = "test";
		OdfTable instance = new OdfTable(dom);
		Assert.assertNotNull(instance);
		OdfTableColumn result = instance.addStyledTableColumn(styleName);
		Assert.assertNotNull(result);
		Assert.assertEquals(styleName, result.getStyleName());
	}

	/**
	 * Test of createColumnStyleList method, of class OdfTable.
	 */
	@Test
	public void testCreateColumnStyleList() {
		System.out.println("createColumnStyleList");
		List<String> styleList = Arrays.asList("test1", "test2", "test3");
		OdfTable instance = new OdfTable(dom);

		Assert.assertNotNull(instance);
		
		List<OdfTableColumn> result =
			instance.makeStyledColumnList(styleList);
		Assert.assertNotNull(result);

		instance.setColumnList(result);
		for (int i = 0; i < styleList.size(); i++)
		{
			Assert.assertEquals(styleList.get(i),
				instance.getTableColumn(i).getStyleName());
		}
	}

}