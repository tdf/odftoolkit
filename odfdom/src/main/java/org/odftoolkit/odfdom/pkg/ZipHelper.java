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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

class ZipHelper {

  private ZipFile mZipFile = null;
  private byte[] mZipBuffer = null;
  private OdfPackage mPackage = null;

  public ZipHelper(OdfPackage pkg, ZipFile zipFile) {
    mZipFile = zipFile;
    mZipBuffer = null;
    mPackage = pkg;
  }

  public ZipHelper(OdfPackage pkg, byte[] buffer) {
    mZipBuffer = buffer;
    mZipFile = null;
    mPackage = pkg;
  }

  public static ZipArchiveInputStream createZipInputStream(InputStream is) {
    return new ZipArchiveInputStream(is, StandardCharsets.UTF_8.toString(), true, true);
  }

  String entriesToMap(Map<String, ZipArchiveEntry> zipEntries) throws IOException, SAXException {
    String firstEntryName = null;
    if (mZipFile != null) {
      Enumeration<? extends ZipArchiveEntry> entries = mZipFile.getEntries();
      if (entries.hasMoreElements()) {
        ZipArchiveEntry zipEntry = entries.nextElement();
        if (zipEntry != null) {
          firstEntryName = zipEntry.getName();
          addZipEntry(zipEntry, zipEntries);
          while (entries.hasMoreElements()) {
            zipEntry = entries.nextElement();
            addZipEntry(zipEntry, zipEntries);
          }
        }
      }
    } else {
      ZipArchiveInputStream inputStream =
          createZipInputStream(new ByteArrayInputStream(mZipBuffer));
      ZipArchiveEntry zipEntry = null;
      try {
        zipEntry = inputStream.getNextZipEntry();
      } catch (ZipException e) {
        // Unit tests expect us to return an empty map in this case.
      }
      if (zipEntry != null) {
        firstEntryName = zipEntry.getName();
        addZipEntry(zipEntry, zipEntries);
        while (zipEntry != null) {
          addZipEntry(zipEntry, zipEntries);
          try {
            zipEntry = inputStream.getNextZipEntry();
          } catch (java.util.zip.ZipException e) {
            if (e.getMessage().contains("only DEFLATED entries can have EXT descriptor")) {
              Logger.getLogger(ZipHelper.class.getName())
                  .finer("ZIP seems to contain encoded parts!");
              throw e;
            }
            // JDK 6 -- the try/catch is workaround for a specific JDK 5 only problem
            if (!e.getMessage().contains("missing entry name")
                && !"1.5.0".equals(System.getProperty("Java.version"))) {
              Logger.getLogger(ZipHelper.class.getName()).finer("ZIP ENTRY not found");
              throw e;
            }
            // ToDo: Error: "only DEFLATED entries can have EXT descriptor"
            // ZipInputStream does not expect (and does not know how to handle) an EXT descriptor
            // when the associated data was not DEFLATED (i.e. was stored uncompressed, as-is).
          }
        }
      }
      inputStream.close();
    }
    return firstEntryName;
  }

  private void addZipEntry(ZipArchiveEntry zipEntry, Map<String, ZipArchiveEntry> zipEntries) {
    String filePath = OdfPackage.normalizePath(zipEntry.getName());
    ErrorHandler errorHandler = mPackage.getErrorHandler();
    if (errorHandler != null) {
      try {
        int zipMethod = zipEntry.getMethod();
        if (zipMethod != ZipArchiveEntry.STORED && zipMethod != ZipArchiveEntry.DEFLATED) {
          mPackage
              .getErrorHandler()
              .error(
                  new OdfValidationException(
                      OdfPackageConstraint.PACKAGE_ENTRY_USING_INVALID_COMPRESSION,
                      mPackage.getBaseURI(),
                      filePath));
        }
      } catch (SAXException ex) {
        Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    zipEntries.put(filePath, zipEntry);
  }

  InputStream getInputStream(ZipArchiveEntry entry) throws IOException {
    if (mZipFile != null) {
      return mZipFile.getInputStream(entry);
    } else {
      ZipArchiveInputStream inputStream =
          createZipInputStream(new ByteArrayInputStream(mZipBuffer));
      ZipArchiveEntry zipEntry = inputStream.getNextZipEntry();
      while (zipEntry != null) {
        if (zipEntry.getName().equalsIgnoreCase(entry.getName())) {
          return readAsInputStream(inputStream);
        }
        zipEntry = inputStream.getNextZipEntry();
      }
      return null;
    }
  }

  private InputStream readAsInputStream(ZipArchiveInputStream inputStream) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    if (outputStream != null) {
      byte[] buf = new byte[4096];
      int r = 0;
      while ((r = inputStream.read(buf, 0, 4096)) > -1) {
        outputStream.write(buf, 0, r);
      }
      inputStream.close();
    }
    return new ByteArrayInputStream(outputStream.toByteArray());
  }

  void close() throws IOException {
    if (mZipFile != null) {
      mZipFile.close();
    } else {
      mZipBuffer = null;
    }
  }
}
