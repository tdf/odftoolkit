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

import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
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
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;

import org.w3c.dom.Node;

/**
 *
 * @author J David Eisenberg
 */
public class OdfTableRowTest {
	OdfTextDocument doc;
	OdfFileDom dom;

    public OdfTableRowTest() {
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
	 * Test of populateStrings method, of class OdfTableRowExtra.
	 */
	@Test
	public void testPopulateStrings() {
		System.out.println("populateStrings");
		List<String> valueList = Arrays.asList("str1", "str2", "str3", "str4");
		List<String> cellStyleList = Arrays.asList("c1", "c2");
		List<String> paraStyleList = Arrays.asList("p1");
		OdfTableRow instance = new OdfTableRow(dom);
		OdfTableCell cell;
		OdfTextParagraph para;
		Node node;
		Assert.assertNotNull(instance);

		instance.populateStrings(valueList,
			cellStyleList,
			paraStyleList);

		for (int i = 0; i < valueList.size(); i++)
		{
			// check for a table cell with the correct style name
			node = instance.getCellAt(i);
			Assert.assertTrue(node instanceof OdfTableCell);
			cell = (OdfTableCell) instance.getCellAt(i);
			Assert.assertEquals(cellStyleList.get(i % cellStyleList.size()),
				cell.getStyleName());

			// containing a paragraph with the correct style name
			node = node.getFirstChild();
			Assert.assertTrue(node instanceof OdfTextParagraph);
			para = (OdfTextParagraph) node;
			Assert.assertEquals(paraStyleList.get(i % paraStyleList.size()),
				para.getStyleName());

			// containing a text node with the correct contents
			node = node.getFirstChild();
			Assert.assertEquals(Node.TEXT_NODE, node.getNodeType());
			Assert.assertEquals(valueList.get(i), node.getNodeValue());
		}

	}

}