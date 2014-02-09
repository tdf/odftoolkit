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

public class TextBoxTest {
	private static final FrameRectangle textBoxRtg = new FrameRectangle(0.5,
			0.2846, 2.9432, 0.8567, SupportedLinearMeasure.IN);

	@BeforeClass
	public static void createForm() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Form form = doc.createForm("Test Form");
			// textbox1
			form.createTextBox(doc, textBoxRtg, "TextBox1", "TextBox1", true);

			// textbox2
			Paragraph para = doc.addParagraph("Insert a text box here.");
			FormControl textBox = form.createTextBox(para, textBoxRtg,
					"TextBox2", "TextBox2", false);
			textBox.setAnchorType(AnchorType.TO_CHARACTER);

			// textbox3
			Table table = Table.newTable(doc, 2, 2);
			Cell cell = table.getCellByPosition("B1");
			para = cell.addParagraph("Insert a text box here.");
			form.createTextBox(para, textBoxRtg, "TextBox3", "TextBox3", false);
			
			doc.save(ResourceUtilities
					.newTestOutputFile("TestCreateTextBox.odt"));

		} catch (Exception e) {
			Logger.getLogger(TextBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testCreateCheckBox() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateTextBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = CheckBox.getSimpleIterator(form);
			int count = 0;
			while (iterator.hasNext()) {
				TextBox textBox = (TextBox) iterator.next();
				Assert.assertNotNull(textBox);
				Assert.assertEquals("TextBox" + (++count), textBox.getName());
				Assert.assertEquals("TextBox" + (count), textBox
						.getCurrentValue());
			}
		} catch (Exception e) {
			Logger.getLogger(TextBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}

	}

	@Test
	public void testRemoveCheckBox() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateTextBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = TextBox.getSimpleIterator(form);
			while (iterator.hasNext()) {
				TextBox textBox = (TextBox) iterator.next();
				if (textBox.getName().equals("TextBox2")) {
					iterator.remove();
					break;
				}
			}
			CheckBox find = null;
			while (iterator.hasNext()) {
				CheckBox textBox = (CheckBox) iterator.next();
				if (textBox.getName().equals("TextBox2")) {
					find = textBox;
					break;
				}
			}
			Assert.assertNull(find);
			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestRemoveTextBox.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetTextBoxRectangle() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateTextBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = TextBox.getSimpleIterator(form);
			TextBox find = null;
			while (iterator.hasNext()) {
				TextBox textBox = (TextBox) iterator.next();
				if (textBox.getName().equals("TextBox2")) {
					find = textBox;
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
					.newTestOutputFile("TestSetTextBoxRectangle.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetAnchorType() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateTextBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = TextBox.getSimpleIterator(form);
			TextBox find = null;
			while (iterator.hasNext()) {
				TextBox textBox = (TextBox) iterator.next();
				if (textBox.getName().equals("TextBox3")) {
					find = textBox;
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
					.newTestOutputFile("TestSetTextBoxAnchorType.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	@Test
	public void testSetTextContent() {
		try {
			TextDocument textDoc = TextDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestCreateTextBox.odt"));
			Form form = textDoc.getFormByName("Test Form");
			Iterator<FormControl> iterator = TextBox.getSimpleIterator(form);
			TextBox textbox1 = null;
			while (iterator.hasNext()) {
				TextBox textBox = (TextBox) iterator.next();
				if (textBox.getName().equals("TextBox1")) {
					textbox1 = textBox;
					break;
				}
			}
			TextBox textbox3 = null;
			while (iterator.hasNext()) {
				TextBox textBox = (TextBox) iterator.next();
				if (textBox.getName().equals("TextBox3")) {
					textbox3 = textBox;
					break;
				}
			}
			Assert.assertNotNull(textbox3);
			Assert.assertNotNull(textbox1);
			// set one-line text
			String textOneLine = "Input text content into this text box.";
			textbox3.setCurrentValue(textOneLine);
			Assert.assertEquals(textOneLine, textbox3.getCurrentValue());
			// set multi-line textString
			String textMultiLine = "This text box allow input multi-line content.\n This is the second line.";
			textbox1.setCurrentValue(textMultiLine);
			Assert.assertEquals(textMultiLine, textbox1.getCurrentValue());
			// set null value
			textbox3.setCurrentValue(null);
			Assert.assertEquals("", textbox3.getCurrentValue());

			textDoc.save(ResourceUtilities
					.newTestOutputFile("TestSetTextBoxContent.odt"));
		} catch (Exception e) {
			Logger.getLogger(TextBoxTest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}
}
