/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009 IBM. All rights reserved
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
package org.odftoolkit.odfdom.pkg;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class NoTempFileTest {

	private Logger mLog = Logger.getLogger(OdfPackage.class.getName());
	private static final String TEST_FILE_FOLDER = ResourceUtilities.getTestOutputFolder();
	private static final String Test_File = "image.odt";
	private static String IMage = "testA.jpg";
	private static String New_File = "test3.odt";
	private static String Test2File = "test2.odt";

	@Before
	public void setUp() {
		try {
			System.setProperty("org.odftoolkit.odfdom.tmpfile.disable", "true");
			String userPropTempEnable = System.getProperty("org.odftoolkit.odfdom.tmpfile.disable");
			mLog.info("The test property org.odftoolkit.odfdom.tmpfile.disable is set to '" + userPropTempEnable + "'.");
		} catch (Exception e) {
			Logger.getLogger(NoTempFileTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLoadPkgFromInputStream() {
		try {
			FileInputStream docStream = new FileInputStream(TEST_FILE_FOLDER + Test_File);
			OdfPackage pkg = OdfPackage.loadPackage(docStream);
			docStream.close();

			OdfFileEntry imagefile = pkg.getFileEntry("Pictures/10000000000000B400000050FF285AE0.png");
			Assert.assertNotNull(imagefile);
			Assert.assertEquals("image/png", imagefile.getMediaTypeString());

			byte[] bytes = pkg.getBytes("Pictures/10000000000000B400000050FF285AE0.png");
			Assert.assertEquals(5551, bytes.length);

		} catch (Exception e) {
			Logger.getLogger(NoTempFileTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testInsertImageWithoutTemp() {
		try {
			// loads the ODF document package from the path
			FileInputStream docStream = new FileInputStream(TEST_FILE_FOLDER + Test2File);
			OdfPackage pkg = OdfPackage.loadPackage(docStream);
			docStream.close();

			// loads the image from the URL and inserts the image in the package, adapting the manifest
			pkg.insert(new FileInputStream(TEST_FILE_FOLDER + IMage), "Pictures/myHoliday.jpg", "image/jpeg");
			pkg.save(ResourceUtilities.newTestOutputFile(New_File));

			OdfDocument doc = OdfDocument.loadDocument(TEST_FILE_FOLDER + New_File);
			OdfContentDom contentDom = doc.getContentDom();

			XPath xpath = contentDom.getXPath();
			DrawFrameElement frame = contentDom.newOdfElement(DrawFrameElement.class);
			frame.setSvgHeightAttribute("3in");
			frame.setSvgWidthAttribute("7in");
			DrawImageElement image = contentDom.newOdfElement(DrawImageElement.class);
			image.setXlinkHrefAttribute("Pictures/myHoliday.jpg");
			frame.appendChild(image);

			TextPElement para = (TextPElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
			para.appendChild(frame);
			doc.save(TEST_FILE_FOLDER + New_File);
			doc.close();

			//Test if the image has been inserted
			doc = OdfDocument.loadDocument(TEST_FILE_FOLDER + New_File);
			contentDom = doc.getContentDom();
			DrawFrameElement frameobj = (DrawFrameElement) xpath.evaluate("//text:p[1]/draw:frame", contentDom, XPathConstants.NODE);
			Assert.assertEquals("3in", frameobj.getSvgHeightAttribute());
			Assert.assertEquals("7in", frameobj.getSvgWidthAttribute());
			DrawImageElement imageobj = (DrawImageElement) frameobj.getFirstChild();
			Assert.assertEquals("Pictures/myHoliday.jpg", imageobj.getXlinkHrefAttribute());
		} catch (Exception e) {
			Logger.getLogger(NoTempFileTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@After
	public void tearDown() {
		try {
			System.setProperty("org.odftoolkit.odfdom.tmpfile.disable", "false");
			String userPropTempEnable = System.getProperty("org.odftoolkit.odfdom.tmpfile.disable");
			mLog.info("The test property org.odftoolkit.odfdom.tmpfile.disable is set to '" + userPropTempEnable + "'.");
		} catch (Exception e) {
			Logger.getLogger(NoTempFileTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}
}
