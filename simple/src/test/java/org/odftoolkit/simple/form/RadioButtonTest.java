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
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class RadioButtonTest {
	private final static FrameRectangle radioRtg = new FrameRectangle(0.7972,
			1.2862, 2.4441, 0.2669, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");

			// radiobox 1
			RadioButton radiobutton = (RadioButton) form.createRadioButton(doc,
					radioRtg, "RadioGroup1", "RadioButton 1", "1");

			// radiobutton 2
			Paragraph para = doc
					.addParagraph("RadioButton2 anchor to heading.");
			para.applyHeading();
			form.createRadioButton(para, radioRtg, "RadioGroup1",
					"RadioButton 2", "2");

			// radiobutton 3
			para = doc.addParagraph("Insert radiobutton3 here, as_char.");
			radiobutton = (RadioButton) form.createRadioButton(para, radioRtg,
					"RadioGroup1", "RadioButton 3", "3");
			radiobutton.setCurrentSelected(true);
			radiobutton.setAnchorType(AnchorType.AS_CHARACTER);

			Table table1 = Table.newTable(doc, 2, 2);
			Cell cell = table1.getCellByPosition("B1");
			para = cell.addParagraph("Insert a check box here.");
			form.createRadioButton(para, radioRtg, "RadioGroup1",
					"RadioButton 4", "4");
			doc.save(ResourceUtilities
					.newTestOutputFile("TestCreateRadioButton.odt"));

		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testCreateRadioButton() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			int count = 0;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				Assert.assertNotNull(radio);
				Assert.assertEquals("RadioGroup1", radio.getName());
				Assert.assertEquals("RadioButton " + (++count), radio
						.getLabel());
			}
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}

	}

	@Test
	public void testRemoveRadioButton() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 2")) {
					iterator.remove();
					break;
				}
			}
			RadioButton find = null;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 2")) {
					find = radio;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveRadioButton.odt"));
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetRadioButtonRectangle() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			RadioButton find = null;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 2")) {
					find = radio;
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
					.newTestOutputFile("TestSetRadioButtonRectangle.odt"));
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetAnchorType() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			RadioButton find = null;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 3")) {
					find = radio;
					break;
				}
			}
			Assert.assertNotNull(find);
			// change the bounding box
			find.setAnchorType(AnchorType.TO_CHARACTER);
			// validate
			ControlStyleHandler frameStyleHandler = find.getDrawControl()
					.getStyleHandler();
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler
					.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.PARAGRAPH,
					graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP,
					graphicPropertiesForWrite.getVerticalPosition());
			Assert.assertEquals(HorizontalRelative.PARAGRAPH,
					graphicPropertiesForWrite.getHorizontalRelative());
			Assert.assertEquals(FrameHorizontalPosition.CENTER,
					graphicPropertiesForWrite.getHorizontalPosition());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetRadioButtonAnchorType.odt"));
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetLabel() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			RadioButton find = null;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 4")) {
					find = radio;
					break;
				}
			}
			Assert.assertNotNull(find);
			// set new label
			String newLabel = "Change the content of RadioButton 4.";
			find.setLabel(newLabel);
			Assert.assertEquals(newLabel, find.getLabel());
			// set null value
			find.setLabel(null);
			Assert.assertEquals("", find.getLabel());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetRadioButtonLabel.odt"));
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testCheckedState() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			RadioButton find = null;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 3")) {
					find = radio;
					break;
				}
			}
			Assert.assertNotNull(find);
			// validate
			Assert.assertEquals(true, find.getCurrentSelected());

			find.setCurrentSelected(false);
			Assert.assertEquals(false, find.getCurrentSelected());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetRadioButtonState.odt"));
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetValue() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateRadioButton.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = RadioButton
					.getSimpleIterator(form);
			RadioButton find = null;
			while (iterator.hasNext()) {
				RadioButton radio = (RadioButton) iterator.next();
				if (radio.getLabel().equals("RadioButton 1")) {
					find = radio;
					break;
				}
			}
			Assert.assertNotNull(find);
			// validate
			Assert.assertEquals("1", find.getValue());

			find.setValue("15");
			Assert.assertEquals("15", find.getValue());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetRadioButtonValue.odt"));
		} catch (Exception e) {
			Logger.getLogger(RadioButtonTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}
}
