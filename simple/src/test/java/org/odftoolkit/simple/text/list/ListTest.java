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
package org.odftoolkit.simple.text.list;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.list.ListDecorator.ListType;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class ListTest {

	private final static String SAMPLE_LIST_DOCUMENT = "testList.odt";
	private String[] numberItemContents = { "number item 1", "number item 2", "number item 3" };

	@Test
	public void testCreateRemoveList() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			ListDecorator numberDecorator = new NumberDecorator(doc);

			List list1 = new List(doc);
			boolean removeResult = doc.removeList(list1);
			Assert.assertTrue(removeResult);

			List list2 = new List(doc, numberDecorator);
			Assert.assertEquals(ListDecorator.ListType.NUMBER, list2.getType());
			removeResult = doc.removeList(list2);
			Assert.assertTrue(removeResult);

			List list3 = new List(doc, "List Header1", numberDecorator);
			Assert.assertEquals(ListDecorator.ListType.NUMBER, list3.getType());
			removeResult = doc.removeList(list3);
			Assert.assertTrue(removeResult);

			List list4 = doc.addList();
			removeResult = doc.removeList(list4);
			Assert.assertTrue(removeResult);

			List list5 = doc.addList(numberDecorator);
			Assert.assertEquals(ListDecorator.ListType.NUMBER, list5.getType());
			removeResult = doc.removeList(list5);
			Assert.assertTrue(removeResult);
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testIterateList() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			int i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(2, i);

			// add 2 new lists.
			odtdoc.addList();
			List list = odtdoc.addList();
			listIterator = odtdoc.getListIterator();
			i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(4, i);

			// remove 1 list.
			odtdoc.removeList(list);
			listIterator = odtdoc.getListIterator();
			i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(3, i);

			// remove all of the lists.
			odtdoc.clearList();
			listIterator = odtdoc.getListIterator();
			i = 0;
			while (listIterator.hasNext()) {
				listIterator.next();
				i++;
			}
			Assert.assertEquals(0, i);

		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetHeader() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			List list;
			if (listIterator.hasNext()) {
				list = listIterator.next();
				Assert.assertEquals("Bullet List Header1\nBullet List Header2", list.getHeader());
			} else {
				Assert.fail("list iterate fail.");
			}

			if (listIterator.hasNext()) {
				list = listIterator.next();
				Assert.assertEquals("Number List Header", list.getHeader());
				String newHeader = "New Header.";
				list.setHeader(newHeader);
				Assert.assertEquals(newHeader, list.getHeader());
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetAddReplaceItems() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			List list;
			if (listIterator.hasNext()) {
				list = listIterator.next();
				// get item
				ListItem item = list.getItem(0);
				Assert.assertEquals("bullet item1", item.getTextContent());
				item = list.getItem(2);
				Assert.assertEquals("item3 with a bullet list", item.getTextContent());

				// test size
				java.util.List<ListItem> items = list.getItems();
				int size = list.size();
				Assert.assertEquals(4, size);
				Assert.assertEquals(items.size(), size);

				// add item
				ListItem newItem1 = list.addItem(item);
				Assert.assertEquals("item3 with a bullet list", newItem1.getTextContent());
				String itemContent = "new string item";
				newItem1 = list.addItem(itemContent);
				Assert.assertEquals(itemContent, newItem1.getTextContent());
				ListItem newItem2 = list.addItem(4, newItem1);
				Assert.assertEquals(newItem2.getTextContent(), newItem1.getTextContent());
				ListItem newItem3 = list.addItem(4, "itemContent");
				Assert.assertEquals(newItem3.getTextContent(), list.getItem(4).getTextContent());

				// add items
				ListItem[] itemArray = list.getItems().toArray(new ListItem[] {});
				list.addItems(itemArray);
				Assert.assertEquals(itemArray.length * 2, list.size());
				list.addItems(2, itemArray);
				Assert.assertEquals(itemArray[0].getTextContent(), list.getItem(2).getTextContent());
				list.addItems(numberItemContents);
				Assert.assertEquals(itemArray.length * 3 + numberItemContents.length, list.size());
				list.addItems(5, numberItemContents);
				Assert.assertEquals(numberItemContents[1], list.getItem(6).getTextContent());

				// replace item
				ListItem item0 = list.getItem(0);
				ListItem item1 = list.getItem(1);
				Assert.assertTrue(!item0.getTextContent().equals(item1.getTextContent()));
				list.set(1, item0);
				item1 = list.getItem(1);
				Assert.assertEquals(item0.getTextContent(), item1.getTextContent());
				ListItem item2 = list.getItem(2);
				Assert.assertTrue(!"replace content".equals(item2.getTextContent()));
				list.set(2, "replace content");
				item2 = list.getItem(2);
				Assert.assertEquals("replace content", item2.getTextContent());
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveItems() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			List list;
			if (listIterator.hasNext()) {
				list = listIterator.next();
				// remove item
				ListItem item0 = list.getItem(0);
				ListItem item1 = list.getItem(1);
				Assert.assertFalse(item0.getOdfElement() == item1.getOdfElement());
				Assert.assertTrue(list.removeItem(0));
				item0 = list.getItem(0);
				Assert.assertTrue(item0.getOdfElement() == item1.getOdfElement());

				item1 = list.getItem(1);
				Assert.assertFalse(item0.getOdfElement() == item1.getOdfElement());
				list.removeItem(item0);
				item0 = list.getItem(0);
				Assert.assertTrue(item0.getOdfElement() == item1.getOdfElement());

				// remove items
				java.util.List<ListItem> newItems = new java.util.ArrayList<ListItem>();
				newItems.add(list.addItem("new item1"));
				newItems.add(list.addItem("new item2"));
				list.removeItems(newItems);
				Assert.assertTrue(item0.getOdfElement() == list.getItem(0).getOdfElement());
			} else {
				Assert.fail("list iterate fail.");
			}
			if (listIterator.hasNext()) {
				list = listIterator.next();
				Assert.assertTrue(list.size() > 0);
				// clear items
				list.clear();
				Assert.assertTrue(list.size() == 0);
			} else {
				Assert.fail("list iterate fail.");
			}
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetListLevel() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			List list;
			if (listIterator.hasNext()) {
				list = listIterator.next();
				Assert.assertTrue(list.getLevel() == 1);

				// sub list
				ListItem item2 = list.getItem(2);
				List subList = item2.getListIterator().next();
				Assert.assertTrue(subList.getLevel() == 2);
			} else {
				Assert.fail("list iterate fail.");
			}
			// list in cell
			Table table = Table.newTable(odtdoc);
			Cell cell = table.getCellByPosition(0, 0);
			List cellList = cell.addList();
			Assert.assertTrue(cellList.getLevel() == 1);
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetSetContinueList() {
		try {
			// load test list container, which contains 2 lists.
			TextDocument odtdoc = (TextDocument) TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(SAMPLE_LIST_DOCUMENT));
			Iterator<List> listIterator = odtdoc.getListIterator();
			List list1 = null, list2 = null, list3 = null;
			if (listIterator.hasNext()) {
				list1 = listIterator.next();
				list1.setContinueNumbering(true);
				// bullet list can't be continue numbering. should return false.
				Assert.assertFalse(list1.isContinueNumbering());
			} else {
				Assert.fail("list iterate fail.");
			}
			if (listIterator.hasNext()) {
				list2 = listIterator.next();
			} else {
				Assert.fail("list iterate fail.");
			}
			list2.setContinueNumbering(true);
			Assert.assertTrue(list2.isContinueNumbering());
			list3 = odtdoc.addList(new NumberDecorator(odtdoc));
			list3.setContinueList(list1);
			Assert.assertTrue(list3.isContinueNumbering());
			Assert.assertTrue(list3.getContinueList().getOdfElement() == list1.getOdfElement());
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSetListDecorator() {
		try {
			TextDocument doc = TextDocument.newTextDocument();

			ListDecorator bulletDecorator = new BulletDecorator(doc);
			ListDecorator numberDecorator = new NumberDecorator(doc);
			ListDecorator imageDecorator = new ImageDecorator(doc, ResourceUtilities.getURI("image_list_item.png"));
			ListDecorator outLineDecorator = new OutLineDecorator(doc);
			String[] subItemContents = { "sub list item 1", "sub list item 2", "sub list item 3" };

			doc.newParagraph(" ");
			doc.newParagraph("Bullet List:");
			List bulletList = doc.addList(bulletDecorator);
			bulletList.setHeader("Bullet List Header1\nBullet List Header2");
			bulletList.addItem("bullet item1");
			bulletList.addItem("bullet item2");
			ListItem item = bulletList.addItem("item3 with a bullet list");
			List subList = item.addList();
			subList.addItems(subItemContents);
			bulletList.addItem("bullet item4");
			Assert.assertEquals(ListType.BULLET, bulletList.getType());

			doc.newParagraph(" ");
			doc.newParagraph("Number List:");
			List numberList = doc.addList(numberDecorator);
			numberList.setHeader("Number List Header");
			numberList.addItem("number item1");
			item = numberList.addItem("number item2");
			subList = item.addList(numberDecorator);
			subList.addItems(subItemContents);
			numberList.addItem("number item3");
			Assert.assertEquals(ListType.NUMBER, numberList.getType());
			numberList.setDecorator(bulletDecorator);
			Assert.assertEquals(ListType.BULLET, numberList.getType());

			doc.newParagraph(" ");
			doc.newParagraph("Image List:");
			List imageList = doc.addList(imageDecorator);
			item = imageList.addItem("image item1");
			subList = item.addList(imageDecorator);
			subList.addItems(subItemContents);
			imageList.addItem("image item2");
			imageList.addItem("image item3");
			Assert.assertEquals(ListType.IMAGE, imageList.getType());
			imageList.setDecorator(numberDecorator);
			Assert.assertEquals(ListType.NUMBER, imageList.getType());

			doc.newParagraph(" ");
			doc.newParagraph("Outline List:");
			List outLineList = doc.addList(outLineDecorator);
			addOutLineItem(outLineList, "", 10, outLineDecorator);
			Assert.assertEquals(ListType.NUMBER, outLineList.getType());
			outLineList.setDecorator(imageDecorator);
			Assert.assertEquals(ListType.IMAGE, outLineList.getType());
			doc.save(ResourceUtilities.getTestOutput("ListOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(ListTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	private void addOutLineItem(List list, String itemPrefix, int listlevel, ListDecorator decorator) {
		if (listlevel == 0) {
			return;
		} else {
			for (int i = 0; i < listlevel; i++) {
				String itemPrefix2 = itemPrefix + (i + 1) + ".";
				ListItem item = list.addItem("list item " + itemPrefix2);
				List newList = item.addList(decorator);
				addOutLineItem(newList, itemPrefix2, i, decorator);
			}
		}
	}
}
