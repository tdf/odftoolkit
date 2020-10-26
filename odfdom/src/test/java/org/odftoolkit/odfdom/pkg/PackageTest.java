/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.pkg;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.type.AnyURI;
import org.odftoolkit.odfdom.utils.ErrorHandlerStub;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PackageTest {

  private static final Logger LOG = Logger.getLogger(PackageTest.class.getName());
  private static final String mImagePath = "src/main/javadoc/resources/";
  private static final String mImageName = "ODFDOM-Layered-Model.png";
  private static final String mImageMediaType = "image/png";
  private static final String XSL_CONCAT = "xslt/concatfiles.xsl";
  private static final String XSL_OUTPUT = "ResolverTest.html";
  // ToDo: Package Structure for test output possbile?
  // private static final String XSL_OUTPUT ="pkg" + File.separator +
  // "ResolverTest.html";
  private static final String SIMPLE_ODT = "test2.odt";
  private static final String ODF_FORMULAR_TEST_FILE = "SimpleFormula.odf";
  private static final String IMAGE_TEST_FILE = "testA.jpg";
  private static final String IMAGE_PRESENTATION = "imageCompressed.odp";
  private static final String TARGET_STEP_1 = "PackageLoadTestStep1.ods";
  private static final String TARGET_STEP_2 = "PackageLoadTestStep2.ods";
  private static final String TARGET_STEP_3 = "PackageLoadTestStep3.ods";
  private static final String TEST_STYLE_STYLE_ATTRIBUTE_ODT = "TestStyleStyleAttribute.odt";

  public PackageTest() {}

  @Test
  public void testNotCompressImages() throws Exception {
    // create test presentation
    OdfPresentationDocument odp = OdfPresentationDocument.newPresentationDocument();
    OfficePresentationElement officePresentation = odp.getContentRoot();
    DrawPageElement page = officePresentation.newDrawPageElement(null);
    DrawFrameElement frame = page.newDrawFrameElement();
    OdfDrawImage image = (OdfDrawImage) frame.newDrawImageElement();
    image.newImage(ResourceUtilities.getTestInputURI(IMAGE_TEST_FILE));
    odp.save(ResourceUtilities.getTestOutputFile(IMAGE_PRESENTATION));

    // test if the image is not compressed
    ZipArchiveInputStream zinput =
        ZipHelper.createZipInputStream(ResourceUtilities.getTestOutputAsStream(IMAGE_PRESENTATION));
    ZipArchiveEntry entry = zinput.getNextZipEntry();
    while (entry != null) {
      String entryName = entry.getName();
      if (entryName.endsWith(".jpg")) {
        File f = new File(ResourceUtilities.getAbsoluteInputPath(IMAGE_TEST_FILE));
        Assert.assertEquals(ZipArchiveEntry.STORED, entry.getMethod());
        Assert.assertEquals(f.length(), entry.getSize());
      }
      entry = zinput.getNextZipEntry();
    }
  }

  @Test
  public void loadPackage() {
    try {

      // LOAD PACKAGE FORMULA
      LOG.info("Loading an unsupported ODF Formula document as an ODF Package!");
      OdfPackage formulaPackage =
          OdfPackage.loadPackage(ResourceUtilities.getAbsoluteInputPath(ODF_FORMULAR_TEST_FILE));
      Assert.assertNotNull(formulaPackage);

      // LOAD PACKAGE IMAGE
      LOG.info("Loading an unsupported image file as an ODF Package!");
      try {
        // Exception is expected!
        OdfPackage.loadPackage(ResourceUtilities.getAbsoluteInputPath(IMAGE_TEST_FILE));
        Assert.fail();
      } catch (Exception e) {
        String errorMsg = OdfPackageConstraint.PACKAGE_IS_NO_ZIP.getMessage();
        if (!e.getMessage().endsWith(errorMsg.substring(errorMsg.indexOf("%1$s") + 4))) {
          LOG.log(Level.SEVERE, null, e);
          Assert.fail();
        }
      }

      // LOAD PACKAGE IMAGE (WITH ERROR HANDLER)
      LOG.info("Loading an unsupported image file as an ODF Package (with error handler)!");
      try {
        // Exception is expected by error handler!
        OdfPackage.loadPackage(
            new File(ResourceUtilities.getAbsoluteInputPath(IMAGE_TEST_FILE)),
            null,
            new DefaultHandler());
        Assert.fail();
      } catch (SAXException e) {
        String errorMsg = OdfPackageConstraint.PACKAGE_IS_NO_ZIP.getMessage();
        if (!e.getMessage().endsWith(errorMsg.substring(errorMsg.indexOf("%1$s") + 4))) {
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
    File tmpFile1 = ResourceUtilities.getTestOutputFile(TARGET_STEP_1);
    File tmpFile2 = ResourceUtilities.getTestOutputFile(TARGET_STEP_2);
    File tmpFile3 = ResourceUtilities.getTestOutputFile(TARGET_STEP_3);

    try (OdfDocument doc = OdfSpreadsheetDocument.newSpreadsheetDocument()) {
      doc.save(tmpFile1);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, mImagePath, ex);
      Assert.fail();
    }

    long lengthBefore = tmpFile1.length();
    try (OdfPackage odfPackage = OdfPackage.loadPackage(tmpFile1)) {

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

      // the package without the images should be as long as before
      Assert.assertTrue(
          "The files \n\t"
              + tmpFile1.getAbsolutePath()
              + " and \n\t"
              + tmpFile3.getAbsolutePath()
              + " differ!",
          lengthBefore == lengthAfter3);

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
        // otherwise create a file class to open the transformStream
        fileStream = new FileInputStream(uri.toString());
        // TODO: error handling in this case! -> allow method
        // insert(URI, ppath, mtype)?
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      BufferedInputStream bis = new BufferedInputStream(fileStream);
      StreamHelper.transformStream(bis, baos);
      fileBytes = baos.toByteArray();
    } catch (Exception e) {
      Logger.getLogger(PackageTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
      e.getLocalizedMessage();
    }
    return fileBytes;
  }

  /**
   * Testing the XML helper and the OdfPackage to handle two files at the same time (have them open)
   */
  @Test
  public void testResolverWithXSLT() {
    try {
      OdfXMLHelper helper = new OdfXMLHelper();
      OdfTextDocument odt =
          (OdfTextDocument)
              OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(SIMPLE_ODT));
      InputSource inputSource =
          new InputSource(ResourceUtilities.getTestInputURI(XSL_CONCAT).toString());
      Templates multiFileAccessTemplate =
          TransformerFactory.newInstance().newTemplates(new SAXSource(inputSource));
      File xslOut = ResourceUtilities.getTestOutputFile(XSL_OUTPUT);
      helper.transform(
          odt.getPackage(), "content.xml", multiFileAccessTemplate, new StreamResult(xslOut));
      LOG.info("Transformed ODF document " + SIMPLE_ODT + " to " + xslOut.getAbsolutePath() + "!");
      File testOutputFile = new File(xslOut.getAbsolutePath());
      if (testOutputFile.length() < 100) {
        String errorMsg =
            "The file "
                + xslOut.getAbsolutePath()
                + " is smaller than it should be. \nIt was not created from multiple package files!";
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }
    } catch (Throwable t) {
      Logger.getLogger(PackageTest.class.getName()).log(Level.SEVERE, t.getMessage(), t);
      Assert.fail();
    }
  }

  @Test
  public void validationTestDefault() {
    try {
      // default no error handler: warnings and errors are not reported
      OdfPackage.loadPackage(ResourceUtilities.getAbsoluteInputPath("testInvalidPkg1.odt"));
      OdfPackage.loadPackage(ResourceUtilities.getAbsoluteInputPath("testInvalidPkg2.odt"));
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail();
    }

    // default no error handler: fatal errors are reported
    try {
      OdfPackage.loadPackage(ResourceUtilities.getAbsoluteInputPath("testA.jpg"));
      Assert.fail();
    } catch (Exception e) {
      String errorMsg = OdfPackageConstraint.PACKAGE_IS_NO_ZIP.getMessage();
      if (!e.getMessage().endsWith(errorMsg.substring(errorMsg.indexOf("%1$s") + 4))) {
        Assert.fail();
      }
    }
  }

  @Test
  public void loadPackageWithoutManifest() {
    try {
      // regression for ODFTOOLKIT-327: invalid package without
      // errorhandler
      // doesn't throw NPE
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ZipArchiveOutputStream zipped = new ZipArchiveOutputStream(out);
      ZipArchiveEntry entry = new ZipArchiveEntry("someentry");
      zipped.putArchiveEntry(entry);
      zipped.closeArchiveEntry();
      zipped.close();

      byte[] data = out.toByteArray();
      ByteArrayInputStream in = new ByteArrayInputStream(data);
      OdfPackage pkg = OdfPackage.loadPackage(in);
      Assert.assertNotNull(pkg);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail();
    }
  }

  @Test
  public void validationTest() {

    // TESTDOC1: Expected ODF Warnings
    Map expectedWarning1 = new HashMap();
    expectedWarning1.put(OdfPackageConstraint.MANIFEST_LISTS_DIRECTORY, 10);

    // TESTDOC1: Expected ODF Errors
    Map expectedErrors1 = new HashMap();
    expectedErrors1.put(OdfPackageConstraint.MIMETYPE_NOT_FIRST_IN_PACKAGE, 1);
    expectedErrors1.put(OdfPackageConstraint.MIMETYPE_IS_COMPRESSED, 1);
    expectedErrors1.put(OdfPackageConstraint.MIMETYPE_HAS_EXTRA_FIELD, 1);
    expectedErrors1.put(OdfPackageConstraint.MIMETYPE_DIFFERS_FROM_PACKAGE, 1);
    expectedErrors1.put(OdfPackageConstraint.MANIFEST_LISTS_NONEXISTENT_FILE, 1);
    ErrorHandlerStub handler1 = new ErrorHandlerStub(expectedWarning1, expectedErrors1, null);
    handler1.setTestFilePath("testInvalidPkg1.odt");

    // TESTDOC2: Expected ODF Warnings
    Map expectedWarning2 = new HashMap();
    expectedWarning2.put(OdfPackageConstraint.MIMETYPE_NOT_IN_PACKAGE, 1);
    expectedWarning2.put(OdfPackageConstraint.MANIFEST_LISTS_DIRECTORY, 10);

    // TESTDOC2: Expected ODF Errors
    Map expectedErrors2 = new HashMap();
    expectedErrors2.put(OdfPackageConstraint.MANIFEST_DOES_NOT_LIST_FILE, 1);
    expectedErrors2.put(OdfPackageConstraint.MANIFEST_LISTS_NONEXISTENT_FILE, 3);
    ErrorHandlerStub handler2 = new ErrorHandlerStub(expectedWarning2, expectedErrors2, null);
    handler2.setTestFilePath("testInvalidPkg2.odt");

    // TESTDOC3 DESCRIPTION - only mimetype file in package
    // TESTDOC3: Expected ODF Errors
    Map expectedErrors3 = new HashMap();
    expectedErrors3.put(OdfPackageConstraint.MANIFEST_NOT_IN_PACKAGE, 1);
    expectedErrors3.put(OdfPackageConstraint.MIMETYPE_WITHOUT_MANIFEST_MEDIATYPE, 1);
    ErrorHandlerStub handler3 = new ErrorHandlerStub(null, expectedErrors3, null);
    handler3.setTestFilePath("testInvalidPkg3.odt");

    // TESTDOC4: Expected ODF FatalErrors
    Map<ValidationConstraint, Integer> expectedFatalErrors4 =
        new HashMap<ValidationConstraint, Integer>();
    // loading a graphic instead an ODF document
    expectedFatalErrors4.put(OdfPackageConstraint.PACKAGE_IS_NO_ZIP, 1);
    ErrorHandlerStub handler4 = new ErrorHandlerStub(null, null, expectedFatalErrors4);

    try {
      OdfPackage pkg1 =
          OdfPackage.loadPackage(
              new File(ResourceUtilities.getAbsoluteInputPath(handler1.getTestFilePath())),
              null,
              handler1);
      Assert.assertNotNull(pkg1);
      OdfPackage pkg2 =
          OdfPackage.loadPackage(
              new File(ResourceUtilities.getAbsoluteInputPath(handler2.getTestFilePath())),
              null,
              handler2);
      Assert.assertNotNull(pkg2);
      OdfPackage pkg3 =
          OdfPackage.loadPackage(
              new File(ResourceUtilities.getAbsoluteInputPath(handler3.getTestFilePath())),
              null,
              handler3);
      Assert.assertNotNull(pkg3);
      try {
        OdfPackage.loadPackage(
            new File(ResourceUtilities.getAbsoluteInputPath("testA.jpg")), null, handler4);
        Assert.fail();
      } catch (Exception e) {
        String errorMsg = OdfPackageConstraint.PACKAGE_IS_NO_ZIP.getMessage();
        if (!e.getMessage().endsWith(errorMsg.substring(errorMsg.indexOf("%1$s") + 4))) {
          Assert.fail();
        }
      }
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
    handler1.validate();
    handler2.validate();
    handler3.validate();
    handler4.validate();
  }

  @Test
  public void testPackagePassword() {
    File tmpFile = ResourceUtilities.getTestOutputFile("PackagePassword.ods");
    OdfDocument doc = null;
    try {
      doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
      OdfPackage odfPackage = doc.getPackage();
      LOG.info(
          "Unencrypted content.xml"
              + odfPackage.getRootDocument().getFileDom("content.xml").toString());
      odfPackage.setPassword("password");
      doc.save(tmpFile);
      doc.close();
      // using wrong password
      odfPackage = OdfPackage.loadPackage(tmpFile, "passwordx");
      byte[] contentBytes = odfPackage.getBytes("content.xml");
      // some encrypted XML
      Assert.assertNotNull(contentBytes);
      try {
        LOG.info("AS EXPECTED, WE WILL FAIL TO PARSE THE ENCRYPTED FILE");
        odfPackage.getRootDocument().getFileDom("content.xml").toString();
        fail("NullPointerException missing!");
      } catch (Throwable t) {
        // as expected
      }
      LOG.info("^^EXPECTED SAXPARSER EXCEPTION! :-)");

      // using correct password
      OdfPackage odfPackage2 = OdfPackage.loadPackage(tmpFile, "password");
      byte[] contentBytes2 = odfPackage2.getBytes("content.xml");
      // some encrypted XML
      Assert.assertNotNull(contentBytes2);
      // due to XML parse errors null
      LOG.info(
          "Decrypted content.xml"
              + odfPackage2.getRootDocument().getFileDom("content.xml").toString());

    } catch (Exception ex) {
      LOG.log(Level.SEVERE, "password test failed.", ex);
      Assert.fail();
    }
  }

  @Test
  public void testLoadingDocumentWithStyleStyleAttribute() {
    try {
      OdfDocument doc =
          OdfDocument.loadDocument(
              ResourceUtilities.getAbsoluteInputPath(TEST_STYLE_STYLE_ATTRIBUTE_ODT));
      OdfElement contentRoot = doc.getContentRoot();
    } catch (Throwable t) {
      Logger.getLogger(PackageTest.class.getName()).log(Level.SEVERE, t.getMessage(), t);
      Assert.fail();
    }
  }
}
