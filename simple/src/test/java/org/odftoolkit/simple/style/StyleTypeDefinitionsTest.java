/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple.style;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.TextDocument.OdfMediaType;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.FrameStyleHandler;
import org.odftoolkit.simple.draw.Image;
import org.odftoolkit.simple.draw.ImageTest;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;


public class StyleTypeDefinitionsTest {
	
	private static final Logger LOGGER =  Logger.getLogger(StyleTypeDefinitionsTest.class.getName());
	
	@Test
	public void testHorizontalAlignmentType() {
		try {
			
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph paragraph1 = doc.addParagraph("paragraph text");

			paragraph1.setHorizontalAlignment(HorizontalAlignmentType.DEFAULT);
			HorizontalAlignmentType align = paragraph1.getHorizontalAlignment();
			Assert.assertEquals(HorizontalAlignmentType.DEFAULT, align);
			
			HorizontalAlignmentType filled = HorizontalAlignmentType.enumValueOf("filled");
			String start = filled.getAlignmentString();
			Assert.assertEquals("start", start);
			Assert.assertEquals("filled", filled.toString());
			Assert.assertEquals(HorizontalAlignmentType.FILLED, filled);
			
			HorizontalAlignmentType testnull = HorizontalAlignmentType.enumValueOf("");
			Assert.assertEquals(null, testnull);
			
			try {
				HorizontalAlignmentType testa = HorizontalAlignmentType.enumValueOf("aaaa");
				Assert.assertEquals("Unsupported Horizontal Alignment Type!", testa);
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
				Assert.assertEquals("Unsupported Horizontal Alignment Type!", e.getMessage());
			}
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testHorizontalAlignmentType.odt"));
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
	

	@Test
	public void testVerticalAlignmentType() throws Exception{
		try {
			SpreadsheetDocument odsdoc = SpreadsheetDocument.loadDocument(ResourceUtilities
					.getTestResourceAsStream("TestSpreadsheetTable.ods"));
		
			int columnindex = 0, rowindex = 3;
			Table table = odsdoc.getTableByName("Sheet1");
			Cell fcell = table.getCellByPosition(columnindex, rowindex);
	
			System.out.println(fcell.getStringValue());
			
			StyleTypeDefinitions.VerticalAlignmentType align = fcell.getVerticalAlignmentType();
			Assert.assertEquals(StyleTypeDefinitions.VerticalAlignmentType.TOP, align);
			
			VerticalAlignmentType vertType = VerticalAlignmentType.enumValueOf("top");
			Assert.assertEquals(VerticalAlignmentType.TOP, vertType);
			Assert.assertEquals("top", vertType.toString());
			
			VerticalAlignmentType testnull = VerticalAlignmentType.enumValueOf("");
			Assert.assertEquals(null, testnull);
			
			try {
				VerticalAlignmentType testa = VerticalAlignmentType.enumValueOf("aaaa");
				Assert.assertEquals("Unsupported Vertical Alignment Type!", testa);
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
				Assert.assertEquals("Unsupported Vertical Alignment Type!", e.getMessage());
			}
			
			//save
			//odsdoc.save(ResourceUtilities.newTestOutputFile("TestSpreadsheetTableOutput.ods"));
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testSupportedLinearMeasure() throws Exception{
		try {
			Border borderbase = new Border(Color.LIME, 4.0701, 1.0008, 1.0346, SupportedLinearMeasure.CM);
			SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = doc.getTableByName("Sheet1");
			
			Cell cell = table.getCellByPosition(2, 2);
			cell.setBorders(CellBordersType.TOP, borderbase);
			cell.setBorders(CellBordersType.LEFT, borderbase);

			//verification
			Border base = cell.getBorder(CellBordersType.LEFT);
			base = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(borderbase, base);
			
			//
			double ins = SupportedLinearMeasure.PT.toINs(216.00);
			Assert.assertTrue(3.0 == ins);
			double cms = SupportedLinearMeasure.PT.toCMs(566.93);
			Assert.assertTrue(20.0 == cms);
			double incms = SupportedLinearMeasure.IN.toCMs(11.0);
			Assert.assertTrue(27.94 == incms);
			
			double cmins = SupportedLinearMeasure.CM.toINs(25.4);
			Assert.assertTrue(10 == cmins);
			double cmcms = SupportedLinearMeasure.CM.toCMs(11.0);
			Assert.assertTrue(11.0 == cmcms);
			
			SupportedLinearMeasure suppMeasure = SupportedLinearMeasure.enumValueOf("pt");
			Assert.assertEquals("pt", suppMeasure.toString());
			SupportedLinearMeasure suppMeasure1 = SupportedLinearMeasure.enumValueOf("");
			Assert.assertEquals(null, suppMeasure1);
			try {
				SupportedLinearMeasure.enumValueOf("aaa");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
				Assert.assertEquals("Unsupported Linear Measure!", e.getMessage());
			}
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	
	@Test
	public void testCellBordersType() throws Exception{
		try {
			Border borderbase = new Border(Color.LIME, 4.0701, 1.0008, 1.0346, SupportedLinearMeasure.CM);
			SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = doc.getTableByName("Sheet1");
			
			Cell cell = table.getCellByPosition(2, 2);
			cell.setBorders(CellBordersType.TOP, borderbase);
			cell.setBorders(CellBordersType.LEFT, borderbase);

			//verification
			Border base = cell.getBorder(CellBordersType.LEFT);
			base = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(borderbase, base);
			
			//
			CellBordersType cellType = CellBordersType.enumValueOf("bottom");
			Assert.assertEquals("bottom", cellType.toString());
			CellBordersType cellType1 = CellBordersType.enumValueOf("");
			Assert.assertEquals(null, cellType1);
			try {
				CellBordersType.enumValueOf("aaa");
			} catch (Exception e) {
				Assert.assertTrue(e instanceof RuntimeException);
				Assert.assertEquals("Unsupported Cell Borders Type!", e.getMessage());
			}
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	
	@Test
	public void testLineType() throws Exception{
		try {
			Border borderbase = new Border(Color.LIME, 1.0701, 0.0208, 0.0346, SupportedLinearMeasure.CM);
			borderbase.setLineStyle(StyleTypeDefinitions.LineType.SINGLE);
			SpreadsheetDocument doc = SpreadsheetDocument.newSpreadsheetDocument();
			Table table = doc.getTableByName("Sheet1");
			
			Cell cell = table.getCellByPosition(2, 2);
			cell.setBorders(CellBordersType.TOP, borderbase);
			cell.setBorders(CellBordersType.LEFT, borderbase);

			//verification
			Border base = cell.getBorder(CellBordersType.LEFT);
			base = cell.getBorder(CellBordersType.TOP);
			Assert.assertEquals(borderbase, base);
			Assert.assertEquals(StyleTypeDefinitions.LineType.SINGLE, borderbase.getLineStyle());
			//
			StyleTypeDefinitions.LineType lType = StyleTypeDefinitions.LineType.enumValueOf("double");
			Assert.assertEquals("double", lType.toString());
			StyleTypeDefinitions.LineType lType1 = StyleTypeDefinitions.LineType.enumValueOf("");
			Assert.assertEquals("none", lType1.toString());
			StyleTypeDefinitions.LineType lType2 = StyleTypeDefinitions.LineType.enumValueOf("aaaa");
			Assert.assertEquals("none", lType2.toString());
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("testSupportedLinearMeasure.ods"));

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	
	@Test
	public void testAnchorType() throws Exception{
	    String content = "XXXXX This is a text box XXXXX";
		try {
			TextDocument textDoc = TextDocument.newTextDocument();
			Paragraph p = textDoc.addParagraph("paragraph");
			FrameRectangle frameR = new FrameRectangle(4.21, 1.32, 4.41, 3.92, SupportedLinearMeasure.CM);
			
			Textbox box = p.addTextbox(frameR);
			box.setName("tbox name");
			box.setTextContent(content);
			
			FrameStyleHandler frameStyleHandler = new FrameStyleHandler(box);
			frameStyleHandler.setBackgroundColor(Color.BLUE);
			
			textDoc.changeMode(OdfMediaType.TEXT_TEMPLATE);
			frameStyleHandler.setAchorType(StyleTypeDefinitions.AnchorType.TO_PAGE);
			
			//validate
			GraphicProperties graphicPropertiesForWrite = frameStyleHandler.getGraphicPropertiesForWrite();
			Assert.assertEquals(VerticalRelative.PAGE, graphicPropertiesForWrite.getVerticalRelative());
			Assert.assertEquals(FrameVerticalPosition.TOP, graphicPropertiesForWrite.getVerticalPosition());
			Assert.assertEquals(HorizontalRelative.PAGE, graphicPropertiesForWrite.getHorizontalRelative());
			Assert.assertEquals(FrameHorizontalPosition.CENTER, graphicPropertiesForWrite.getHorizontalPosition());

			//
			StyleTypeDefinitions.AnchorType anchorType = StyleTypeDefinitions.AnchorType.enumValueOf("");
			Assert.assertEquals("page", anchorType.toString());
			StyleTypeDefinitions.AnchorType anchorType1 = StyleTypeDefinitions.AnchorType.enumValueOf("char");
			Assert.assertEquals(StyleTypeDefinitions.AnchorType.TO_CHARACTER, anchorType1);
			StyleTypeDefinitions.AnchorType anchorType2 = StyleTypeDefinitions.AnchorType.enumValueOf("aaaa");
			Assert.assertEquals(StyleTypeDefinitions.AnchorType.TO_PAGE, anchorType2);
			
			//save
			textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
			Logger.getLogger(StyleTypeDefinitionsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
		
	}
	
	
	@Test
	public void testVerticalRelative() throws Exception{
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
			
			//
			StyleTypeDefinitions.VerticalRelative vert = StyleTypeDefinitions.VerticalRelative.enumValueOf("");
			Assert.assertEquals("page", vert.toString());
			StyleTypeDefinitions.VerticalRelative vert1 = StyleTypeDefinitions.VerticalRelative.enumValueOf("aaaa");
			Assert.assertEquals("page", vert1.toString());
			StyleTypeDefinitions.VerticalRelative vert2 = StyleTypeDefinitions.VerticalRelative.enumValueOf("frame");
			Assert.assertEquals("frame", vert2.toString());
			
			//save
			//textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
		    Logger.getLogger(StyleTypeDefinitionsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
		
	}
	
	
	@Test
	public void testHorizontalRelative() throws Exception{
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
			
			//
			HorizontalRelative hor = HorizontalRelative.enumValueOf("");
			Assert.assertEquals("page", hor.toString());
			
			HorizontalRelative hor1 = HorizontalRelative.enumValueOf("paragraph");
			Assert.assertEquals("paragraph", hor1.toString());
			
			HorizontalRelative hor2 = HorizontalRelative.enumValueOf("aaa");
			Assert.assertEquals("page", hor2.toString());
			
			//save
			//textDoc.save(ResourceUtilities.newTestOutputFile("textsample.odt"));

		} catch (Exception e) {
		    Logger.getLogger(StyleTypeDefinitionsTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
		
	}
	
	
	@Test
	public void testFrameVerticalPosition() throws Exception{
	    try {
		// new image in a table
		TextDocument sDoc = TextDocument.newTextDocument();
		Table table1 = sDoc.addTable(2, 2);
		Cell cell1 = table1.getCellByPosition(0, 0);
		Image image3 = cell1.setImage(ResourceUtilities.getURI("image_list_item.png"));
		image3.setVerticalPosition(FrameVerticalPosition.BELOW);
		
		//validate
		Assert.assertEquals(FrameVerticalPosition.BELOW, image3.getVerticalPosition());
		
		//
		FrameVerticalPosition ver = FrameVerticalPosition.enumValueOf("");
		Assert.assertEquals("middle", ver.toString());
		
		FrameVerticalPosition ver1 = FrameVerticalPosition.enumValueOf("below");
		Assert.assertEquals("below", ver1.toString());
		
		FrameVerticalPosition ver2 = FrameVerticalPosition.enumValueOf("aaaa");
		Assert.assertEquals("middle", ver2.toString());
		
		//save
		//sDoc.save(ResourceUtilities.newTestOutputFile("imges.odt"));

	    } catch (Exception e) {
		Logger.getLogger(StyleTypeDefinitionsTest.class.getName()).log(Level.SEVERE, null, e);
		Assert.fail();
	    }
		
	}
	
	
	@Test
	public void testFrameHorizontalPosition() {
		try {
			// new image in a table
			TextDocument sDoc = TextDocument.newTextDocument();
			Table table1 = sDoc.addTable(2, 2);
			Cell cell1 = table1.getCellByPosition(0, 0);
			Image image3 = cell1.setImage(ResourceUtilities.getURI("image_list_item.png"));
			image3.setHorizontalPosition(FrameHorizontalPosition.LEFT);
			Assert.assertEquals(FrameHorizontalPosition.LEFT, image3.getHorizontalPosition());
			
			//
			FrameHorizontalPosition hor = FrameHorizontalPosition.enumValueOf("");
			Assert.assertEquals("center", hor.toString());
			FrameHorizontalPosition hor1 = FrameHorizontalPosition.enumValueOf("inside");
			Assert.assertEquals("inside", hor1.toString());
			FrameHorizontalPosition hor2 = FrameHorizontalPosition.enumValueOf("aaaa");
			Assert.assertEquals("center", hor2.toString());
			
			//save
			sDoc.save(ResourceUtilities.newTestOutputFile("imges.odt"));

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
}
