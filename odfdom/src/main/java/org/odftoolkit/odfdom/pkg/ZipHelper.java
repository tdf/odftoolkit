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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
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

  public ZipHelper(OdfPackage pkg, byte[] buffer) throws IOException {
    mZipBuffer = buffer;
    SeekableInMemoryByteChannel c = new SeekableInMemoryByteChannel(mZipBuffer);
    mZipFile =
        new ZipFile.Builder().setSeekableByteChannel(c).setUseUnicodeExtraFields(false).get();
    mPackage = pkg;
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
        if (zipEntries.containsKey(filePath)) {
          mPackage
              .getErrorHandler()
              .fatalError(
                  new OdfValidationException(
                      OdfPackageConstraint.PACKAGE_ENTRY_DUPLICATE,
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
      return null;
    }
  }

  void close() throws IOException {
    if (mZipFile != null) {
      mZipFile.close();
      mZipFile = null;
    }
    mZipBuffer = null;
  }
}
