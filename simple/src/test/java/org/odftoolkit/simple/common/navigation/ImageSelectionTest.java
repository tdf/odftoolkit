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

package org.odftoolkit.simple.common.navigation;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.Image;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test the method of class
 * org.odftoolkit.simple.common.navigation.ImageSelection
 */
public class ImageSelectionTest {

	private static final String TEXT_FILE = "TestTextSelection.odt";
	TextDocument doc,sourcedoc;
	TextNavigation search;
	private Image image2;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		try {
			doc = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEXT_FILE));
			sourcedoc=TextDocument.newTextDocument();
			Paragraph para = sourcedoc.addParagraph("helloImage");
			Image image = Image.newImage(para, ResourceUtilities.getURI("image_list_item.png"));
			image.setName("this image 1");
			image.setHyperlink(new URI("http://odftoolkit.org"));
			
			Paragraph para2 = sourcedoc.addParagraph("helloImage2");
			image2 = Image.newImage(para2, ResourceUtilities.getURI("testA.jpg"));
			image2.setName("this image 2");
			image2.setHyperlink(new URI("http://odftoolkit.org"));
			
			
		} catch (Exception e) {
			Logger.getLogger(ImageSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@After
	public void tearDown() {
	}
	/**
	 * Test ReplaceWithImage method of
	 * org.odftoolkit.simple.common.navigation.ImageSelection replace "SIMPLE" to a Image from Document
	 */
	@Test
	public void testReplaceWithImage() {
		search = null;
		//6 Simple, at the middle of original Paragraph, split original Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		int i=0;
		try {
			ImageSelection nextImageSelection=null;
			while (search.hasNext()) {
				TextSelection item= (TextSelection) search.nextSelection();
				nextImageSelection=new ImageSelection(item);
				Paragraph paragraph=sourcedoc.getParagraphByIndex(0, true);
				TextParagraphElementBase textParaEleBase = paragraph.getOdfElement();
				NodeList nodeImages = textParaEleBase.getElementsByTagName("draw:image");
				Node nodeImage = nodeImages.item(0);
				DrawImageElement im = (DrawImageElement)nodeImage;
				Image ima = Image.getInstanceof(im);
				Image image = nextImageSelection.replaceWithImage(ima);
				Assert.assertNotNull(image);
				if(image.getName().startsWith("replace")){
					Assert.assertTrue(true);
				}else{
					Assert.fail();
				}
				String name="simple"+(i++);
				image.setName(name);
				Assert.assertEquals(name,image.getName());
		}
			Image image =nextImageSelection.replaceWithImage(image2);
			image.setName("simpletwice");
			Assert.assertEquals("simpletwice",image.getName());
		doc.save(ResourceUtilities.newTestOutputFile("TestImageSelectionImageResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
	/**
	 * Test ReplaceWithImage method of
	 * org.odftoolkit.simple.common.navigation.ImageSelection replace "SIMPLE" to a Image from Image URI
	 */
	@Test
	public void testReplaceWithImageURI() {
		search = null;
		//6 Simple, at the middle of original Paragraph, split original Paragraph, insert before the second Paragraph.
		search = new TextNavigation("SIMPLE", doc);
		int i=0;
		try {
			ImageSelection nextImageSelection=null;
			while (search.hasNext()) {
				TextSelection item= (TextSelection) search.nextSelection();
				nextImageSelection=new ImageSelection(item);
				URI imageuri = ResourceUtilities.getURI("image_list_item.png");
				Image image = nextImageSelection.replaceWithImage(imageuri);
				Assert.assertNotNull(image);
				if(image.getName().startsWith("replace")){
					Assert.assertTrue(true);
				}else{
					Assert.fail();
				}
				String name="simple"+(i++);
				image.setName(name);
				Assert.assertEquals(name,image.getName());
		}
			URI imageuri =  ResourceUtilities.getURI("testA.jpg");
			Image image =nextImageSelection.replaceWithImage(imageuri);
			image.setName("simpletwice");
			Assert.assertEquals("simpletwice",image.getName());
		doc.save(ResourceUtilities.newTestOutputFile("TestImageSelectionURIResult.odt"));
		} catch (Exception e) {
			Logger.getLogger(ImageSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}
}
