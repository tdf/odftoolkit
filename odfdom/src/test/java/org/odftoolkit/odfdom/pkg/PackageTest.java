/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.type.AnyURI;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.InputSource;

public class PackageTest {

	private static final Logger LOG = Logger.getLogger(PackageTest.class.getName());
	private static final String mImagePath = "src/main/javadoc/doc-files/";
	private static final String mImageName = "ODFDOM-Layered-Model.png";
	private static final String mImageMediaType = "image/png";
	private static final String XSL_CONCAT = "xslt/concatfiles.xsl";
	private static final String XSL_OUTPUT = "ResolverTest.html";
	//ToDo: Package Structure for test output possbile?
	//private static final String XSL_OUTPUT ="pkg" + File.separator + "ResolverTest.html";
	private static final String SIMPLE_ODT = "test2.odt";
	private static final String ODF_FORMULAR_TEST_FILE = "SimpleFormula.odf";
	private static final String IMAGE_TEST_FILE = "testA.jpg";
	private static final String IMAGE_PRESENTATION = "imageCompressed.odp";
	private static final String TARGET_STEP_1 = "PackageLoadTestStep1.ods";
	private static final String TARGET_STEP_2 = "PackageLoadTestStep2.ods";
	private static final String TARGET_STEP_3 = "PackageLoadTestStep3.ods";

	public PackageTest() {
	}

	@Test
	public void testNotCompressImages() throws Exception {
		//create test presentation
		OdfPresentationDocument odp = OdfPresentationDocument.newPresentationDocument();
		OfficePresentationElement officePresentation = odp.getContentRoot();
		DrawPageElement page = officePresentation.newDrawPageElement(null);
		DrawFrameElement frame = page.newDrawFrameElement();
		OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
		image.newImage(ResourceUtilities.getURI(IMAGE_TEST_FILE));
		odp.save(ResourceUtilities.newTestOutputFile(IMAGE_PRESENTATION));

		//test if the image is not compressed
		ZipInputStream zinput = new ZipInputStream(ResourceUtilities.getTestResourceAsStream(IMAGE_PRESENTATION));
		ZipEntry entry = zinput.getNextEntry();
		while (entry != null) {
			String entryName = entry.getName();
			if (entryName.endsWith(".jpg")) {
				File f = new File(ResourceUtilities.getAbsolutePath(IMAGE_TEST_FILE));
				Assert.assertEquals(ZipEntry.STORED, entry.getMethod());
				Assert.assertEquals(f.length(), entry.getSize());
			}
			entry = zinput.getNextEntry();
		}

	}

	@Test
	public void loadPackage() {
		try {

			// LOAD PACKAGE FORMULA
			LOG.info("Loading an unsupported ODF Formula document as an ODF Package!");
			OdfPackage formulaPackage = OdfPackage.loadPackage(ResourceUtilities.getTestResourceAsStream(ODF_FORMULAR_TEST_FILE));
			Assert.assertNotNull(formulaPackage);

			// LOAD PACKAGE IMAGE
			LOG.info("Loading an unsupported image file as an ODF Package!");
			try {
				// Exception is expected!
				OdfPackage.loadPackage(ResourceUtilities.getTestResourceAsStream(IMAGE_TEST_FILE));
				Assert.fail();
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().contains(" unzip the file")) {
					LOG.log(Level.SEVERE, null, e);
					Assert.fail();
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testPackage() {
		File tmpFile1 = ResourceUtilities.newTestOutputFile(TARGET_STEP_1);
		File tmpFile2 = ResourceUtilities.newTestOutputFile(TARGET_STEP_2);
		File tmpFile3 = ResourceUtilities.newTestOutputFile(TARGET_STEP_3);
		OdfDocument doc = null;
		try {
			doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
			doc.save(tmpFile1);
			doc.close();
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, mImagePath, ex);
			Assert.fail();
		}

		long lengthBefore = tmpFile1.length();
		try {
			// not allowed to change the document simply by open and save
			OdfPackage odfPackage = OdfPackage.loadPackage(tmpFile1);

			URI imageURI = new URI(mImagePath + mImageName);
			// testing encoded none ASCII in URL path
			String pkgRef1 = AnyURI.encodePath("Pictures/a&b.jpg");
			LOG.log(Level.INFO, "Attempt to write graphic to package path: {0}", pkgRef1);
			odfPackage.insert(uri2ByteArray(imageURI), pkgRef1, mImageMediaType);

			// testing allowed none-ASCII in URL path (see rfc1808.txt)
			String pkgRef2 = "Pictures/a&%" + "\u00ea" + "\u00f1" + "\u00fc" + "b.jpg";
			LOG.log(Level.INFO, "Attempt to write graphic to package path: {0}", pkgRef2);
			odfPackage.insert(uri2ByteArray(imageURI), pkgRef2, mImageMediaType);
			odfPackage.save(tmpFile2);
			long lengthAfter2 = tmpFile2.length();
			// the new package with the images have to be bigger
			Assert.assertTrue(lengthBefore < lengthAfter2);
			odfPackage.remove(pkgRef1);
			odfPackage.remove(pkgRef2);
			odfPackage.remove("Pictures/");
			odfPackage.save(tmpFile3);
			long lengthAfter3 = tmpFile3.length();
			odfPackage.close();

			// the package without the images should be as long as before
			Assert.assertTrue("The files \n\t" + tmpFile1.getAbsolutePath() + " and \n\t" + tmpFile3.getAbsolutePath() + " differ!", lengthBefore == lengthAfter3);

		} catch (Exception ex) {
			LOG.log(Level.SEVERE, mImagePath, ex);
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
			LOG.info("Transformed ODF document " + SIMPLE_ODT + " to " + xslOut.getAbsolutePath() + "!");
			File testOutputFile = new File(xslOut.getAbsolutePath());
			if (testOutputFile.length() < 100) {
				String errorMsg = "The file " + xslOut.getAbsolutePath() + " is smaller than it should be. \nIt was not created from multiple package files!";
				LOG.severe(errorMsg);
				Assert.fail(errorMsg);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			Assert.fail();
		}

	}
}
