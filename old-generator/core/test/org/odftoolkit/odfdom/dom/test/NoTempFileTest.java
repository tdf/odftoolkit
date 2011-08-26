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

package org.odftoolkit.odfdom.dom.test;

import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.draw.OdfFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.OdfImageElement;
import org.odftoolkit.odfdom.dom.element.text.OdfParagraphElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.FileEntry;

public class NoTempFileTest {

    private static final String TEST_FILE_FOLDER = "test/resources/";
    private static final String Test_File = "image.odt";
    private static String IMage = "test/resources/test.jpg";
    private static String New_File = "test/resources/test3.odt";
    private static String Test2File =  "test/resources/test2.odt";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Test
	public void testLoadPkgFromInputStream()
	{
		try {
			FileInputStream docStream = new FileInputStream(TEST_FILE_FOLDER+Test_File);
			OdfPackage pkg = OdfPackage.loadPackage(docStream,false);
			docStream.close();
			
			FileEntry imagefile = pkg.getFileEntry("Pictures/10000000000000B400000050FF285AE0.png");
			Assert.assertNotNull(imagefile);
			Assert.assertEquals("image/png", imagefile.getMediaType());
			
			byte[] bytes = pkg.getBytes("Pictures/10000000000000B400000050FF285AE0.png");
			Assert.assertEquals(5551, bytes.length);

		}
		catch (Exception e)
		{
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	@Test
	public void testInsertImageWithoutTemp()
	{
        try {
            // loads the ODF document package from the path
			FileInputStream docStream = new FileInputStream(Test2File);
            OdfPackage pkg = OdfPackage.loadPackage(docStream,false);
			docStream.close();

            // loads the image from the URL and inserts the image in the package, adapting the manifest
            pkg.insert(new FileInputStream(IMage),"Pictures/myHoliday.jpg");
            pkg.save(New_File);
            
            OdfDocument doc = OdfDocument.loadDocument(New_File,false);
            OdfFileDom docdom = doc.getContentDom();
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new OdfNamespace());

            OdfFrameElement frame = docdom.createOdfElement(OdfFrameElement.class);
            frame.setHeight("3in");
            frame.setWidth("7in");
            OdfImageElement image = docdom.createOdfElement(OdfImageElement.class);
            image.setHref("Pictures/myHoliday.jpg");
            frame.appendChild(image);

            OdfParagraphElement para = (OdfParagraphElement) xpath.evaluate("//text:p[1]", docdom, XPathConstants.NODE);
            para.appendChild(frame);
            doc.save(New_File);
            
            //Test if the image has been inserted
            doc = OdfDocument.loadDocument(New_File,false);
            docdom = doc.getContentDom();
            OdfFrameElement frameobj = (OdfFrameElement) xpath.evaluate("//text:p[1]/draw:frame", docdom, XPathConstants.NODE);
			Assert.assertEquals("3in", frameobj.getHeight());            
			Assert.assertEquals("7in", frameobj.getWidth());
			OdfImageElement imageobj = (OdfImageElement) frameobj.getFirstChild();
			Assert.assertEquals("Pictures/myHoliday.jpg", imageobj.getHref());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		
	}

}
