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

import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.presentation.Slide;
import org.odftoolkit.simple.presentation.Slide.SlideLayout;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImageTest {

	@Test
	public void testNewImage() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setName("this image");
			image.setHyperlink(new URI("http://odftoolkit.org"));
			doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
			Iterator<Image> iter = Image.imageIterator(para);
			if (iter.hasNext()) {
				Image aImage = iter.next();
				Assert.assertEquals(image, aImage);
			}

			// new image in presentation
			PresentationDocument pDoc = PresentationDocument.newPresentationDocument();
			Slide slide = pDoc.newSlide(0, "test", SlideLayout.TITLE_OUTLINE);
			Textbox box = slide.getTextboxByUsage(PresentationDocument.PresentationClass.TITLE).get(0);
			box.setImage(ResourceUtilities.getURI("image_list_item.png"));
			pDoc.save(ResourceUtilities.newTestOutputFile("imagep.odp"));

			// new image in a table
			TextDocument sDoc = TextDocument.newTextDocument();
			Table table1 = sDoc.addTable(2, 2);
			Cell cell1 = table1.getCellByPosition(0, 0);
			Image image3 = cell1.setImage(ResourceUtilities.getURI("image_list_item.png"));
			image3.setHorizontalPosition(FrameHorizontalPosition.LEFT);
			image3.setHyperlink(new URI("http://odftoolkit.org"));
			Assert.assertEquals("http://odftoolkit.org", image3.getHyperlink().toString());
			sDoc.save(ResourceUtilities.newTestOutputFile("imges.odt"));

			SpreadsheetDocument sheet = SpreadsheetDocument.newSpreadsheetDocument();
			Table table2 = sheet.getTableList().get(0);
			Cell cell2 = table2.getCellByPosition(1, 1);
			Image image4 = cell2.setImage(ResourceUtilities.getURI("image_list_item.png"));
			sheet.save(ResourceUtilities.newTestOutputFile("imgesheet.ods"));
			Image aImage4 = cell2.getImage();
			Assert.assertEquals(image4, aImage4);

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}

	}
	
	
	@Test
	public void testGetInstanceof() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setName("this image");
			image.setHyperlink(new URI("http://odftoolkit.org"));
			
			DrawImageElement imageElement = image.getOdfElement();
			
			Image imageother = Image.getInstanceof(imageElement);
			Assert.assertEquals(image, imageother);
			
			//when image is null
			Image imagenull = Image.getInstanceof(null);
			Assert.assertEquals(null, imagenull);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}

	}
	
	
	@Test
	public void testGetFrame() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setName("this image");
			image.setHyperlink(new URI("http://odftoolkit.org"));
			
			Frame imageFrame = image.getFrame();
			Assert.assertEquals("this image", imageFrame.getName());
			Assert.assertEquals(new URI("http://odftoolkit.org"), imageFrame.getHyperlink());
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testRemove() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("imagePara");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setName("this test image");
			image.setHyperlink(new URI("http://odftoolkit.org"));
			//image.remove();
			
			//save
			doc.save(ResourceUtilities.newTestOutputFile("imageFretest.odt"));
			
			TextDocument doc1 = TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("imageFretest.odt"));
			Iterator parasIter = doc1.getParagraphIterator();
			
			while(parasIter.hasNext()){
				Paragraph parac = (Paragraph)parasIter.next();
				String text = parac.getTextContent();
				if("imagePara".equals(text)){
					TextParagraphElementBase textParaEleBase = parac.getOdfElement();
					NodeList nodeImages = textParaEleBase.getElementsByTagName("draw:image");
					Node nodeImage = nodeImages.item(0);
					OdfDrawImage im = (OdfDrawImage)nodeImage;
					Image ima = Image.getInstanceof(im);
					ima.getName();
					boolean flag = ima.remove();
					if(!flag)
						Assert.fail("remove() method was executed failed.");
				}
			}
			
			//save
			doc1.save(ResourceUtilities.newTestOutputFile("imageEndtest.odt"));
			
			//validate
			TextDocument doc2 = TextDocument.loadDocument(ResourceUtilities.getAbsolutePath("imageEndtest.odt"));
			Iterator parasIter2 = doc2.getParagraphIterator();
			
			while(parasIter2.hasNext()){
				Paragraph parac = (Paragraph)parasIter2.next();
				String text = parac.getTextContent();
				if("imagePara".equals(text)){
					TextParagraphElementBase textParaEleBase = parac.getOdfElement();
					NodeList nodeImages = textParaEleBase.getElementsByTagName("draw:image");
					Node nodeImage = nodeImages.item(0);
					if(nodeImage == null)
						Assert.assertTrue(true);
					else
						Assert.fail();
				}
			}
			
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testUpdateImage() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			Assert.assertEquals("image/png", image.getMediaTypeString());

			//change the piceture of image.
			image.updateImage(ResourceUtilities.getURI("testA.jpg"));
			Assert.assertEquals("image/jpeg", image.getMediaTypeString());
			
			//Image imm = (Image)nodeEle;
			DrawImageElement  drawImage = image.getOdfElement();
			String imagePath = drawImage.getAttribute("xlink:href");
			
			Assert.assertEquals("Pictures/image_list_item.png", imagePath);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testGetImageInputStream() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			InputStream inImage = image.getImageInputStream();
			int size = inImage.available();
			Assert.assertTrue(size > 0);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}

	
	@Test
	public void testGetInternalPath() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			String internalPath = image.getInternalPath();
			
			Assert.assertEquals("Pictures/image_list_item.png", internalPath);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testGetMediaTypeString() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			String mediaType = image.getMediaTypeString();
			
			Assert.assertEquals("image/png", mediaType);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testSetTitle() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setTitle("ibm_title");
			String title = image.getTitle();
			
			Assert.assertEquals("ibm_title", title);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testSetDescription() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setDescription("description");
			String description = image.getDesciption();
			
			Assert.assertEquals("description", description);
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testSetRectangle() {
		try {
			// new image in text document
			TextDocument doc = TextDocument.newTextDocument();
			Paragraph para = doc.addParagraph("updateImage test");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			
			FrameRectangle rectangle = new FrameRectangle(5.13, 4.21, 3.76, 3.51, StyleTypeDefinitions.SupportedLinearMeasure.CM); 
			image.setRectangle(rectangle);
			
			FrameRectangle rectangle2 = image.getRectangle();
			
			Assert.assertEquals(3.51, rectangle2.getHeight());
			Assert.assertEquals(3.76, rectangle2.getWidth());
			Assert.assertEquals(5.13, rectangle2.getX());
			Assert.assertEquals(4.21, rectangle2.getY());
			Assert.assertEquals(StyleTypeDefinitions.SupportedLinearMeasure.CM, rectangle2.getLinearMeasure());
			
			//save
			//doc.save(ResourceUtilities.newTestOutputFile("imagetest.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testGetHorizontalPosition() {
		try {
			// new image in a table
			TextDocument sDoc = TextDocument.newTextDocument();
			Table table1 = sDoc.addTable(2, 2);
			Cell cell1 = table1.getCellByPosition(0, 0);
			Image image3 = cell1.setImage(ResourceUtilities.getURI("image_list_item.png"));
			image3.setHorizontalPosition(FrameHorizontalPosition.LEFT);
			Assert.assertEquals(FrameHorizontalPosition.LEFT, image3.getHorizontalPosition());
			
			//save
			//sDoc.save(ResourceUtilities.newTestOutputFile("imges.odt"));

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
	
	@Test
	public void testSetVerticalPosition() {
		try {
			// new image in a table
			TextDocument sDoc = TextDocument.newTextDocument();
			Table table1 = sDoc.addTable(2, 2);
			Cell cell1 = table1.getCellByPosition(0, 0);
			Image image3 = cell1.setImage(ResourceUtilities.getURI("image_list_item.png"));
			image3.setVerticalPosition(FrameVerticalPosition.BELOW);
			
			Assert.assertEquals(FrameVerticalPosition.BELOW, image3.getVerticalPosition());
			
			//save
			//sDoc.save(ResourceUtilities.newTestOutputFile("imges.odt"));

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail();
		}
	}
	
}
