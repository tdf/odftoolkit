/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.type.AnyURI;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.InputSource;

public class PackageTest {

	private Logger mLog = Logger.getLogger(PackageTest.class.getName());
	private static final String mImagePath = "src/main/javadoc/doc-files/";
	private static final String mImageName = "ODFDOM-Layered-Model.png";
	private static final String mImageMediaType = "image/png";

	private static final String XSL_CONCAT = "xslt/concatfiles.xsl";
	private static final String XSL_OUTPUT = "ResolverTest.html";
	private static final String SIMPLE_ODT = "test2.odt";

	//ToDo: Package Structure for test output possbile?
	//private static final String XSL_OUTPUT ="pkg" + File.separator + "ResolverTest.html";

	public PackageTest() {
	}

	@Test
	public void testPackage() {
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("tmp", ".ods");
		} catch (IOException ex) {
			ex.printStackTrace();
			Assert.fail();
		}
		Assert.assertNotNull(tmpFile);
		OdfDocument doc = null;
		try {
			doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
			doc.save(tmpFile);
			doc.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}

		long lengthBefore = tmpFile.length();
		try {
			// not allowed to change the document simply by open and save
			OdfPackage odfPackage = OdfPackage.loadPackage(tmpFile);

			URI imageURI = new URI(mImagePath + mImageName);
			// testing encoded none ASCII in URL path
			String pkgRef1 = AnyURI.encodePath("a&b.jpg");
			mLog.info("Attempt to write file to package path: " + pkgRef1);
			odfPackage.insert(uri2ByteArray(imageURI), pkgRef1, mImageMediaType);

			// testing allowed none-ASCII in URL path (see rfc1808.txt)
			String pkgRef2 = "a&%" + "\u00ea" + "\u00f1" + "\u00fc" + "b.jpg";
			mLog.info("Attempt to write file to package path: " + pkgRef2);
			odfPackage.insert(uri2ByteArray(imageURI), pkgRef2, mImageMediaType);
			odfPackage.save(tmpFile);
			long lengthAfter = tmpFile.length();
			// the new package with the images have to be bigger
			Assert.assertTrue(lengthBefore < lengthAfter);
			odfPackage.remove(pkgRef1);
			odfPackage.remove(pkgRef2);
			odfPackage.save(tmpFile);
			lengthAfter = tmpFile.length();
			// the package without the images should be as long as before
			Assert.assertTrue(lengthBefore == lengthAfter);
			odfPackage.close();
			// clean up afterwards
			Assert.assertTrue("Test OdfPackage could not be delete after ussage!", tmpFile.delete());
			Assert.assertFalse("Test OdfPackage still exist after attempt deletion!", tmpFile.exists());
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}

	private static byte[] uri2ByteArray(URI uri) {
		byte[] fileBytes = null;
		try {
			InputStream fileStream = null;
			if (uri.isAbsolute()) {
				// if the URI is absolute it can be converted to URL
				fileStream = uri.toURL().openStream();
			} else {
				// otherwise create a file class to open the stream
				fileStream = new FileInputStream(uri.toString());
				// TODO: error handling in this case! -> allow method insert(URI, ppath, mtype)?
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedInputStream bis = new BufferedInputStream(fileStream);
			StreamHelper.stream(bis, baos);
			fileBytes = baos.toByteArray();
		} catch (Exception e) {
			Logger.getLogger(PackageTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
		return fileBytes;
	}

	/** Testing the XML helper and the OdfPackage to handle two files at the same time (have them open) */
	@Test
	public void testResolverWithXSLT() {
		try {
			OdfXMLHelper helper = new OdfXMLHelper();
			OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SIMPLE_ODT));
			InputSource inputSource = new InputSource(ResourceUtilities.getURI(XSL_CONCAT).toString());
			Templates multiFileAccessTemplate = TransformerFactory.newInstance().newTemplates(new SAXSource(inputSource));
			File xslOut = ResourceUtilities.newTestOutputFile(XSL_OUTPUT);
			helper.transform(odt.getPackage(), "content.xml", multiFileAccessTemplate, new StreamResult(xslOut));
			mLog.info("Transformed ODF document " + SIMPLE_ODT + " to " + xslOut.getAbsolutePath() + "!");
			File testOutputFile = new File(xslOut.getAbsolutePath());
			if (testOutputFile.length() < 100) {
				String errorMsg = "The file " + xslOut.getAbsolutePath() + " is smaller than it should be. \nIt was not created from multiple package files!";
				mLog.severe(errorMsg);
				Assert.fail(errorMsg);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}

	}
}
