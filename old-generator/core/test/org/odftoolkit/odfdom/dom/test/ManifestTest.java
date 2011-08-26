/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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

import java.io.File;
import java.net.URI;
import java.util.Enumeration;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.draw.OdfFrame;
import org.odftoolkit.odfdom.doc.element.draw.OdfImage;
import org.odftoolkit.odfdom.doc.element.text.OdfParagraph;
import org.odftoolkit.odfdom.dom.element.text.OdfParagraphElement;
import org.odftoolkit.odfdom.dom.type.text.OdfAnchorType;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.w3c.dom.NodeList;
import org.odftoolkit.odfdom.pkg.manifest.Algorithm;
import org.odftoolkit.odfdom.pkg.manifest.EncryptionData;
import org.odftoolkit.odfdom.pkg.manifest.FileEntry;
import org.odftoolkit.odfdom.pkg.manifest.KeyDerivation;
import org.odftoolkit.odfdom.pkg.manifest.Manifest;

public class ManifestTest {

	private static final String TEST_FILE_EMBEDDED = "test/resources/testEmbeddedDoc.odt";
	private static final String TEST_PIC = "test/resources/test.jpg";
	//encrypted file with password '123456'
	private static final String TEST_FILE_ENCRYPT = "test/resources/EncryptDoc.odt";
	String SLASH = "/";
    public ManifestTest() {
    }

    @Test
    public void testManifestEntries() {
        try {            
        	OdfPackage pkg = OdfPackage.loadPackage(TEST_FILE_EMBEDDED);
        	//get Manifest Entries
        	Set<String> manifestEntries = pkg.getFileEntries();
        	//get Zip Entries
        	File odfFile = new File(TEST_FILE_EMBEDDED);
        	ZipFile zipFile = new ZipFile(odfFile);
        	Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
        	ZipEntry zipEntry;
        	//each Manifest entry can be viewed as one node of the tree,
        	//and Zip entry is just the leaf node of this tree.
        	while(zipEntries.hasMoreElements())
        	{
        		zipEntry = zipEntries.nextElement();
        		String zipEntryName = zipEntry.getName();
        		if(!zipEntryName.equals(OdfPackage.OdfFile.MANIFEST.getPath()) 
        				&& !zipEntryName.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath()))
        		{
        			//check leaf node
        			Assert.assertTrue(manifestEntries.contains(zipEntryName));
        			
        			//check the path node
        			int start = 0;
        			int end = 0;
        			String entryPath;
        			while((end = (zipEntryName.indexOf(SLASH, start))) != -1)
        			{
        				entryPath = zipEntryName.substring(0, end+1);
        				Assert.assertTrue(manifestEntries.contains(entryPath));
        				start = end + 1;
        			}
        			
        		}
        	}

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }
    
    @Test
    public void testInsertEntry(){
		try {
			String imageRef = TEST_PIC;
			if (imageRef.contains(SLASH)) {
	            imageRef = imageRef.substring(imageRef.lastIndexOf(SLASH) + 1, imageRef.length());
	        }
	        
			String picturePath = OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH;
	        String imagePath = OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + imageRef;
	        //insert file entry by manifest DOM
			OdfDocument doc = OdfDocument.loadDocument("test/resources/empty.odt");
			//get the initial Manifest Entries
			OdfFileDom manifestDom = doc.getPackage().getManifestDom();
			((Manifest)(manifestDom.getFirstChild())).addFileEntry(picturePath,"");
			((Manifest)(manifestDom.getFirstChild())).addFileEntry(imagePath,"image/jpeg");
        	Set<String> oldManifestEntries = doc.getPackage().getFileEntries();
        	
        	//insert image in the document
        	OdfDocument doc1 = OdfDocument.loadDocument("test/resources/empty.odt");
			OdfFileDom contentDOM = doc1.getContentDom();
			// find the last paragraph
            NodeList lst = contentDOM.getElementsByTagNameNS(
                    OdfParagraphElement.ELEMENT_NAME.getUri(),
                    OdfParagraphElement.ELEMENT_NAME.getLocalName());
            Assert.assertTrue(lst.getLength() > 0);
            OdfParagraph p0 = (OdfParagraph) lst.item(lst.getLength() - 1);
            
            OdfFrame drawFrame = new OdfFrame(contentDOM);
            drawFrame.setName("graphics1");
            drawFrame.setAnchorType(OdfAnchorType.PARAGRAPH);
            drawFrame.setWidth("4.233cm");
            drawFrame.setHeight("4.233cm");
            drawFrame.setZIndex(0);
            p0.appendChild(drawFrame);
                
            OdfImage image = new OdfImage(contentDOM);
            image.insertImage(new URI(TEST_PIC));
            drawFrame.appendChild(image);
            
			doc1.save("build/test/ManifestEntrytest.odt");
			
			OdfPackage newPkg = OdfPackage.loadPackage("build/test/ManifestEntrytest.odt");
			Set<String> newManifestEntries = newPkg.getFileEntries();
	        
			for (String entry : newManifestEntries) {
	            Assert.assertTrue(oldManifestEntries.contains(entry));
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
    	
    }
    
    @Test
    public void testEncryptDoc(){
    	try {
			OdfPackage pkg = OdfPackage.loadPackage(TEST_FILE_ENCRYPT);
			FileEntry _contFileEntry = pkg.getFileEntry("content.xml");
			FileEntry _styleFileEntry = pkg.getFileEntry("styles.xml");
			FileEntry _metaFileEntry = pkg.getFileEntry("meta.xml");
			FileEntry _settFileEntry = pkg.getFileEntry("settings.xml");
			
			Assert.assertEquals(_contFileEntry.getSize().intValue(), 2682);
			EncryptionData _contData = _contFileEntry.getEncryptionData();
			Assert.assertEquals(_contData.getChecksumType(), "SHA1/1K");
			Assert.assertEquals(_contData.getChecksum(), "f3QHaEJ+bPS54JwcQ/lmFa3WZRQ=");
			
			_styleFileEntry.removeEncryptionData();
			EncryptionData _metaData = _metaFileEntry.getEncryptionData();
			EncryptionData _settData = _settFileEntry.getEncryptionData();
			Algorithm _metaAlgorithm = _metaData.getAlgorithm();
			KeyDerivation _metaKey = _metaData.getKeyDerivation();
			
			Algorithm _settAlgorithm = _settData.getAlgorithm();
			KeyDerivation _settKey = _settData.getKeyDerivation();
			
			_metaData.setAlgorithm(_settAlgorithm.getAlgorithmName(), _settAlgorithm.getInitialisationVector());
			_metaData.setKeyDerivation(_settKey.getKeyDerivationName(), _settKey.getSalt(), _settKey.getIterationCount());
			
			_settData.setAlgorithm(_metaAlgorithm.getAlgorithmName(), _metaAlgorithm.getInitialisationVector());
			_settData.setKeyDerivation(_metaKey.getKeyDerivationName(), _metaKey.getSalt(), _metaKey.getIterationCount());
			
			pkg.save("build/test/EncryptManifest.odt");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
    	
    	
    }
}
