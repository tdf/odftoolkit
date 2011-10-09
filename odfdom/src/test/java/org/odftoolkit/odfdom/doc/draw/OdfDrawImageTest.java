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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.odftoolkit.odfdom.doc.draw;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.net.URI;
import java.util.logging.Logger;

import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 *
 * @author hs234750
 */
public class OdfDrawImageTest {

	private static final Logger LOG = Logger.getLogger(OdfDrawImageTest.class.getName());

	/**
	 * Test of newImage method, of class OdfDrawImage.
	 */
	@Test
	public void testInsertImage_URI() throws Exception {
		LOG.info("insertImage from URI");
		OdfTextDocument odt = OdfTextDocument.newTextDocument();
		OdfTextParagraph para = (OdfTextParagraph) odt.getContentRoot().newTextPElement();
		OdfDrawFrame frame = (OdfDrawFrame) para.newDrawFrameElement();
		OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		String packagePath = image.newImage(ResourceUtilities.getURI("testA.jpg"));
		assertEquals(image.getXlinkTypeAttribute(), "simple");
		LOG.info(frame.getSvgWidthAttribute());
		LOG.info(frame.getSvgHeightAttribute());
		assert (frame.getSvgWidthAttribute().startsWith("19.") && frame.getSvgWidthAttribute().endsWith("cm"));
		assert (frame.getSvgHeightAttribute().startsWith("6.") && frame.getSvgHeightAttribute().endsWith("cm"));
		assertEquals(odt.getPackage().getFileEntry(packagePath).getMediaTypeString(), "image/jpeg");
	}

	/**
	 * Test of newImage method, of class OdfDrawImage.
	 */
	@Test
	public void testInsertImage_InputStream() throws Exception {
		LOG.info("insertImage from InputStream");
		OdfTextDocument odt = OdfTextDocument.newTextDocument();
		OdfTextParagraph para = (OdfTextParagraph) odt.getContentRoot().newTextPElement();
		OdfDrawFrame frame = (OdfDrawFrame) para.newDrawFrameElement();
		OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		String packagePath = "Pictures/myChosenImageName.jpg";
		String mediaType = "image/jpeg";
		image.newImage(new FileInputStream(ResourceUtilities.getAbsolutePath("testA.jpg")), packagePath, mediaType);
		assertEquals(image.getXlinkTypeAttribute(), "simple");
		assert (frame.getSvgWidthAttribute().startsWith("19.") && frame.getSvgWidthAttribute().endsWith("cm"));
		assert (frame.getSvgHeightAttribute().startsWith("6.") && frame.getSvgHeightAttribute().endsWith("cm"));
	}
}
