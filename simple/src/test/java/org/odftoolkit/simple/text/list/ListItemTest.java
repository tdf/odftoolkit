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

package org.odftoolkit.simple.text.list;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ListItemTest {

	private final static String SAMPLE_LIST_DOCUMENT = "testList.odt";
    private static final String SAMPLE_LIST_DOCUMENT_2 = "testList2.odt";
	private String[] subItemContents = { "sub list item 1", "sub list item 2", "sub list item 3" };

	@Test
	public void testGetSetTextContent() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			if (listIterator.hasNext()) {
				List list = listIterator.next();
				ListItem item = list.getItem(0);
				String itemText = item.getTextContent();
				Assert.assertEquals("bullet item1", itemText);
				String content = "new content";
				item.setTextContent(content);
				Assert.assertEquals(content, item.getTextContent());
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetStartNumber() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			if (listIterator.hasNext()) {
				List list = listIterator.next();
				list = listIterator.next();
				ListItem item = list.getItem(0);
				Integer startNumber = item.getStartNumber();
				Assert.assertNull(startNumber);
				item.setStartNumber(4);
				startNumber = item.getStartNumber();
				Assert.assertEquals(4, startNumber.intValue());
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetNumberFormat() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			if (listIterator.hasNext()) {
				List list = listIterator.next();
				list = listIterator.next();
				ListItem item = list.getItem(0);
				String numberFormat = item.getNumberFormat();
				Assert.assertNull(numberFormat);
				item.setNumberFormat("Mon");
				numberFormat = item.getNumberFormat();
				Assert.assertEquals("Mon", numberFormat);
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetOwnerList() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			if (listIterator.hasNext()) {
				List list = listIterator.next();
				ListItem item = list.getItem(0);
				List ownerList = item.getOwnerList();
				Assert.assertEquals(list.getOdfElement(), ownerList.getOdfElement());
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetIndex() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			if (listIterator.hasNext()) {
				List list = listIterator.next();
				java.util.List<ListItem> items = list.getItems();
				int i = 0;
				for (ListItem item : items) {
					Assert.assertEquals(i, item.getIndex());
					i++;
				}
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAddRemoveIterateSubList() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			ListDecorator bulletDecorator = new BulletDecorator(odtdoc);
			ListDecorator numberDecorator = new NumberDecorator(odtdoc);
			ListDecorator imageDecorator = new ImageDecorator(odtdoc, ResourceUtilities.getURI("image_list_item.png"));
			Iterator<List> listIterator = odtdoc.getListIterator();

			if (listIterator.hasNext()) {
				List list = listIterator.next();
				// add list.
				ListContainer container = list.getItem(0);
				org.odftoolkit.simple.text.list.List bulletList = container.addList(bulletDecorator);
				bulletList.addItems(subItemContents);
				org.odftoolkit.simple.text.list.List numberList = container.addList(numberDecorator);
				numberList.addItems(subItemContents);
				org.odftoolkit.simple.text.list.List imageList = container.addList(imageDecorator);
				imageList.addItems(subItemContents);
				// iterate list
				Iterator<org.odftoolkit.simple.text.list.List> lists = container.getListIterator();
				int i = 0;
				while (lists.hasNext()) {
					lists.next();
					i++;
				}
				Assert.assertEquals(3, i);
				// remove list
				container.clearList();
				Assert.assertFalse(container.getListIterator().hasNext());
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
    
    @Test
    public void testListIterator()  {
        try {
            // load test list container, which contains 2 lists.
			TextDocument odtdoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT_2));
			Iterator<List> listIterator = odtdoc.getListIterator();
            
            final List bodyList = listIterator.next();
            Assert.assertEquals("body list should have 2 items", 2, bodyList.size());
            
            Assert.assertTrue("iterator should have 1 more list (in the Section)", listIterator.hasNext());
                        
            final List sectionList = listIterator.next();
            Assert.assertEquals("section list should have 3 items", 3, sectionList.size());
            
            Assert.assertFalse("iterator should have no more lists", listIterator.hasNext());
        } catch (Exception e) {
            Logger.getLogger(ListItemTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
}
    }
}
