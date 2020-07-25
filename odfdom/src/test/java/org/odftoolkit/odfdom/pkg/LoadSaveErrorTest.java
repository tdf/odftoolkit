/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.odftoolkit.odfdom.pkg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** Tests the error behavior while loading or saving a document. */
public class LoadSaveErrorTest {

  private static final String SOURCE =
      "OdfTextDocument.odt"; // loaded from the class directory (the template)
  private static final Logger LOG = Logger.getLogger(LoadSaveErrorTest.class.getName());

  /**
   * The ZIP in this test claims to be an ODT file but then backs this with nothing. We should be
   * able to cancel the whole operation through a custom ErrorHandler, throwing exception not only
   * for fatal errors.
   *
   * @throws SAXException if an XML-related error occurs
   * @throws IOException if an I/O error occurs
   */
  @Test
  public void testInvalidZipAsODF() throws Exception {
    ByteArrayOutputStream baout = new ByteArrayOutputStream();
    ZipArchiveOutputStream zout = new ZipArchiveOutputStream(baout);
    ZipArchiveEntry entry = new ZipArchiveEntry("mimetype");
    zout.putArchiveEntry(entry);
    zout.write(OdfDocument.OdfMediaType.TEXT.getMediaTypeString().getBytes("US-ASCII"));
    zout.closeArchiveEntry();
    zout.close();

    byte[] zip = baout.toByteArray();

    final AtomicInteger warnings = new AtomicInteger(0);
    final AtomicInteger errors = new AtomicInteger(0);
    final AtomicInteger fatals = new AtomicInteger(0);

    ErrorHandler errorHandler =
        new ErrorHandler() {

          public void warning(SAXParseException exception) throws SAXException {
            warnings.incrementAndGet();
            Logger.getLogger(getClass().getName()).warning(exception.getLocalizedMessage());
          }

          public void error(SAXParseException exception) throws SAXException {
            errors.incrementAndGet();
            Logger.getLogger(getClass().getName()).severe(exception.getLocalizedMessage());
            // Here's the difference to DefaultErrorHandler:
            // we also throw normal errors, not just fatal errors.
            throw exception;
          }

          public void fatalError(SAXParseException exception) throws SAXException {
            fatals.incrementAndGet();
            Logger.getLogger(getClass().getName()).severe(exception.getLocalizedMessage());
            throw exception;
          }
        };

    try {
      OdfPackage odfPackage =
          OdfPackage.loadPackage(new ByteArrayInputStream(zip), null, errorHandler);
      odfPackage.close();
      Assert.fail("Expected an exception for the incomplete ODF file!");
    } catch (SAXException se) {
      // expected
    }
    Assert.assertEquals(0, warnings.get());
    Assert.assertEquals(0, fatals.get());
    Assert.assertEquals(
        "Expected abortion after the first exception to come through the ErrorHandler",
        1,
        errors.get());
  }

  @Test
  public void testLoadWithFailingNetwortConnection() throws Exception {
    String doc = ResourceUtilities.getAbsolutePath(SOURCE);
    File file = new File(doc);
    InputStream in = new java.io.FileInputStream(file);
    try {
      in = new FailingInputStream(in, 8000);
      try {
        OdfPackage.loadPackage(in);
        Assert.fail("Expected IOException for failing stream!");
      } catch (IOException ioe) {
        Assert.assertTrue(ioe.getMessage().contains("Stream failed!"));
      }
    } finally {
      in.close();
    }
  }

  /**
   * Tests what happens if there is an I/O error while saving an ODF file.
   *
   * @throws Exception if an error occurs
   */
  @Test
  public void testDiskFullOnSave() throws Exception {
    OdfPackageDocument odfDocument =
        OdfPackageDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE));
    Assert.assertTrue(odfDocument.getPackage().contains("content.xml"));
    String baseURI = odfDocument.getPackage().getBaseURI();

    LOG.info("SOURCE URI1:" + ResourceUtilities.getURI(SOURCE).toString());
    LOG.info("SOURCE URI2:" + baseURI);
    Assert.assertTrue(
        ResourceUtilities.getURI(SOURCE).toString().compareToIgnoreCase(baseURI) == 0);

    Document odfContent = odfDocument.getFileDom("content.xml");
    NodeList lst =
        odfContent.getElementsByTagNameNS("urn:oasis:names:tc:opendocument:xmlns:text:1.0", "p");
    Node node = lst.item(0);
    String oldText = "Changed!!!";
    node.setTextContent(oldText);

    try {
      odfDocument.getPackage().save(new DiskFullSimulationOutputStream(400));
      Assert.fail("Expected an IOException when disk is full!");
    } catch (IOException ioe) {
      Assert.assertEquals("Disk full!", ioe.getMessage());
    }
  }

  private static class DiskFullSimulationOutputStream extends OutputStream {

    private long remainingBytes;

    public DiskFullSimulationOutputStream(long byteCountUntilDiskFull) {
      this.remainingBytes = byteCountUntilDiskFull;
    }

    @Override
    public void write(int b) throws IOException {
      remainingBytes--;
      if (remainingBytes <= 0) {
        throw new IOException("Disk full!");
      }
    }
  }

  /**
   * InputStream that will stop reading after the amount to failure has reached. By doing so this
   * InputStream emulates a network failure or disc error.
   */
  private static class FailingInputStream extends FilterInputStream {

    private long remainingBytes;

    protected FailingInputStream(InputStream in, long bytesUntilFailure) {
      super(in);
      this.remainingBytes = bytesUntilFailure;
    }

    private void processBytes(int count) throws IOException {
      remainingBytes -= count;
      if (remainingBytes <= 0) {
        throw new IOException("Stream failed!");
      }
    }

    @Override
    public int read() throws IOException {
      processBytes(1);
      return super.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int bytesRead = super.read(b, off, len);
      if (bytesRead > 0) {
        processBytes(bytesRead);
      }
      return bytesRead;
    }

    @Override
    public int read(byte[] b) throws IOException {
      return read(b, 0, b.length);
    }
  }
}
