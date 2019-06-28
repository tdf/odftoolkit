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

package org.odftoolkit.simple.form;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.ControlStyleHandler;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class LabelTest {
	private static final FrameRectangle labelRtg = new FrameRectangle(0.5, 1.2553,
			1.2, 0.5, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");
			// label1
			form.createLabel(doc, labelRtg, "Label1", "Label1");
			// label2
			Label label = (Label) form.createLabel(doc, labelRtg, "Label2",
					"Label2");
			label.setAnchorType(AnchorType.TO_CHARACTER);
			// label3
			Paragraph para = doc.addParagraph("Insert a Label here.");
			form.createLabel(para, labelRtg, "Label3", "Label3");
			// label4
			Table table1 = Table.newTable(doc, 2, 2);
			Cell cell = table1.getCellByPosition("B1");
			para = cell.addParagraph("Insert a Label here.");
			form.createLabel(para, labelRtg, "Label4", "Label4");
			doc
					.save(ResourceUtilities
							.newTestOutputFile("TestCreateLabel.odt"));

		} catch (Exception e) {
			Logger.getLogger(LabelTest.class.getName()).log(Level.SEVERE, null,
					e);
            Assert.fail();
		}
	}

	@Test
	public void testCreateLabel() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateLabel.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Label.getSimpleIterator(form);
			int count = 0;
			while (iterator.hasNext()) {
				Label label = (Label) iterator.next();
				Assert.assertNotNull(label);
				Assert.assertEquals("Label" + (++count), label.getName());
				Assert.assertEquals("Label" + (count), label.getLabel());
			}
		} catch (Exception e) {
			Logger.getLogger(LabelTest.class.getName()).log(Level.SEVERE, null,
					e);
            Assert.fail();
		}

	}

	@Test
	public void testRemoveLabel() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateLabel.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Label.getSimpleIterator(form);
			while (iterator.hasNext()) {
				Label label = (Label) iterator.next();
				if (label.getName().equals("Label2")) {
					iterator.remove();
					break;
				}
			}
			Label find = null;
			while (iterator.hasNext()) {
				Label label = (Label) iterator.next();
				if (label.getName().equals("Label2")) {
					find = label;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveLabel.odt"));
		} catch (Exception e) {
			Logger.getLogger(LabelTest.class.getName()).log(Level.SEVERE, null,
					e);
            Assert.fail();
		}
	}

	@Test
	public void testSetLabelRectangle() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateLabel.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Label.getSimpleIterator(form);
			Label find = null;
			while (iterator.hasNext()) {
				Label label = (Label) iterator.next();
				if (label.getName().equals("Label2")) {
					find = label;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setRectangle(new FrameRectangle(2.25455, 5, 3, 0.5,
					SupportedLinearMeasure.IN));
			Assert.assertEquals(3.0, find.getRectangle().getWidth());
			Assert.assertEquals(0.5, find.getRectangle().getHeight());
			Assert.assertEquals(5.0, find.getRectangle().getY());
			Assert.assertEquals(2.2546, find.getRectangle().getX());
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetLabelRectangle.odt"));
		} catch (Exception e) {
			Logger.getLogger(LabelTest.class.getName()).log(Level.SEVERE, null,
					e);
            Assert.fail();
		}
	}

	@Test
	public void testSetAnchorType() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateLabel.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Label.getSimpleIterator(form);
			Label find = null;
			while (iterator.hasNext()) {
				Label label = (Label) iterator.next();
				if (label.getName().equals("Label2")) {
					find = label;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setAnchorType(AnchorType.AS_CHARACTER);
			// validate
			ControlStyleHandler frameStyleHandler = find.getDrawControl()
					.getStyleHandler();
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler
					.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.BASELINE,
					graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP,
					graphicPropertiesForWrite.getVerticalPosition());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetLabelAnchorType.odt"));
		} catch (Exception e) {
			Logger.getLogger(LabelTest.class.getName()).log(Level.SEVERE, null,
					e);
		}
	}

	@Test
	public void testSetLabel() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateLabel.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = Label.getSimpleIterator(form);
			Label find = null;
			while (iterator.hasNext()) {
				Label label = (Label) iterator.next();
				if (label.getName().equals("Label3")) {
					find = label;
					break;
				}
			}
			Assert.assertNotNull(find);
			// set new label
			String newLabel = "Change the content of Label 4.";
			find.setLabel(newLabel);
			Assert.assertEquals(newLabel, find.getLabel());
			// set null value
			find.setLabel(null);
			Assert.assertEquals("", find.getLabel());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetLabelLabel.odt"));
		} catch (Exception e) {
			Logger.getLogger(LabelTest.class.getName()).log(Level.SEVERE, null,
					e);
            Assert.fail();
		}
	}

}
