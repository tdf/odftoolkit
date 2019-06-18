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

package org.odftoolkit.simple.presentation;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.text.list.BulletDecorator;
import org.odftoolkit.simple.text.list.ImageDecorator;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.odftoolkit.simple.text.list.NumberDecorator;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class NotesTest {
	PresentationDocument doc;
	final String TEST_PRESENTATION_FILE_MAIN = "Presentation1.odp";

	@Test
	public void testAddRemoveIterateSubList() {
		try {
			doc = PresentationDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream(TEST_PRESENTATION_FILE_MAIN));
			ListDecorator bulletDecorator = new BulletDecorator(doc);
			ListDecorator numberDecorator = new NumberDecorator(doc);
			ListDecorator imageDecorator = new ImageDecorator(doc, ResourceUtilities.getURI("image_list_item.png"));
			String[] numberItemContents = { "number item 1", "number item 2", "number item 3" };

			// add list.
			Slide slide = doc.newSlide(0, "test0", Slide.SlideLayout.TITLE_OUTLINE);
			ListContainer container = slide.getNotesPage();
			org.odftoolkit.simple.text.list.List bulletList = container.addList(bulletDecorator);
			bulletList.addItems(numberItemContents);
			org.odftoolkit.simple.text.list.List numberList = container.addList(numberDecorator);
			numberList.addItems(numberItemContents);
			org.odftoolkit.simple.text.list.List imageList = container.addList(imageDecorator);
			imageList.addItems(numberItemContents);
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
		} catch (Exception e) {
			Logger.getLogger(NotesTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
}
