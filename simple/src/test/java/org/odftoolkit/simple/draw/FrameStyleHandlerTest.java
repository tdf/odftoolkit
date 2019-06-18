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

package org.odftoolkit.simple.draw;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.TextDocument.OdfMediaType;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class FrameStyleHandlerTest {

	@Test
	public void testSet() {
		String content = "This is a text box";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.CM);

			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);

			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.YELLOW);

			textDoc.changeMode(OdfMediaType.TEXT_TEMPLATE);
			frameStyleHandler.setAchorType(StyleTypeDefinitions.AnchorType.TO_CHARACTER);

			//validate
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.PARAGRAPH, graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP, graphicPropertiesForWrite.getVerticalPosition());
			Assert.assertEquals(HorizontalRelative.PARAGRAPH, graphicPropertiesForWrite.getHorizontalRelative());
			Assert.assertEquals(FrameHorizontalPosition.CENTER, graphicPropertiesForWrite.getHorizontalPosition());

			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(FrameStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}


	@Test
	public void testSetAchorTypeAs_character() {
		String content = "This is a text box";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.CM);

			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);

			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.YELLOW);

			textDoc.changeMode(OdfMediaType.TEXT_TEMPLATE);
			frameStyleHandler.setAchorType(StyleTypeDefinitions.AnchorType.AS_CHARACTER);

			//validate
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.BASELINE, graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP, graphicPropertiesForWrite.getVerticalPosition());

			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(FrameStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}


	@Test
	public void testSetAchorTypeTo_page() {
		String content = "This is a text box";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.IN);

			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);

			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.YELLOW);

			textDoc.changeMode(OdfMediaType.TEXT_TEMPLATE);
			frameStyleHandler.setAchorType(StyleTypeDefinitions.AnchorType.TO_PAGE);

			//validate
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.PAGE, graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP, graphicPropertiesForWrite.getVerticalPosition());
			Assert.assertEquals(HorizontalRelative.PAGE, graphicPropertiesForWrite.getHorizontalRelative());
			Assert.assertEquals(FrameHorizontalPosition.CENTER, graphicPropertiesForWrite.getHorizontalPosition());

			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(FrameStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}


	@Test
	public void testSetHorizontalPosition() {
		String content = "This is a text box";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.IN);

			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);

			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.YELLOW);
			frameStyleHandler.setHorizontalPosition(FrameHorizontalPosition.RIGHT);

			//validate
			Assert.assertEquals(FrameHorizontalPosition.RIGHT, frameStyleHandler.getHorizontalPosition());

			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(FrameStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}


	@Test
	public void testSetVerticalRelative() {
		String content = "This is a text box";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.IN);

			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);

			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.YELLOW);
			frameStyleHandler.setVerticalRelative(VerticalRelative.PARAGRAPH);

			//validate
			Assert.assertEquals(VerticalRelative.PARAGRAPH, frameStyleHandler.getVerticalRelative());

			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(FrameStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}


	@Test
	public void testSetHorizontalRelative() {
		String content = "This is a text box";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.IN);

			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);

			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.YELLOW);
			frameStyleHandler.setHorizontalRelative(HorizontalRelative.PARAGRAPH);

			//validate
			Assert.assertEquals(HorizontalRelative.PARAGRAPH, frameStyleHandler.getHorizontalRelative());

			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(FrameStyleHandlerTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}


}

