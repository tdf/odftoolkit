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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
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

	String entriesToMap(Map<String, ZipEntry> zipEntries) throws IOException, SAXException {
		String firstEntryName = null;
		if (mZipFile != null) {
			Enumeration<? extends ZipEntry> entries = mZipFile.entries();
			if (entries.hasMoreElements()) {
				ZipEntry zipEntry = entries.nextElement();
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
			ZipInputStream inputStream = new ZipInputStream(new ByteArrayInputStream(mZipBuffer));
			if (inputStream.available() == 0) {
				throw new IllegalArgumentException("Could not unzip the given ODF package!");
			} else {
				ZipEntry zipEntry = inputStream.getNextEntry();
				if (zipEntry != null) {
					firstEntryName = zipEntry.getName();
					addZipEntry(zipEntry, zipEntries);
					while (zipEntry != null) {
						addZipEntry(zipEntry, zipEntries);
						try {
							zipEntry = inputStream.getNextEntry();
						} catch (java.util.zip.ZipException e) {
							if (e.getMessage().contains("only DEFLATED entries can have EXT descriptor")) {
								Logger.getLogger(ZipHelper.class.getName()).finer("ZIP seems to contain encoded parts!");
								throw e;
							}
							// JDK 6 -- the try/catch is workaround for a specific JDK 5 only problem
							if (!e.getMessage().contains("missing entry name") && !System.getProperty("Java.version").equals("1.5.0")) {
								Logger.getLogger(ZipHelper.class.getName()).finer("ZIP ENTRY not found");
								throw e;
							}
							// ToDo: Error: "only DEFLATED entries can have EXT descriptor"
							// ZipInputStream does not expect (and does not know how to handle) an EXT descriptor when the associated data was not DEFLATED (i.e. was stored uncompressed, as-is).
						}
					}
				}
			}
			inputStream.close();
		}
		return firstEntryName;
	}

	private void addZipEntry(ZipEntry zipEntry, Map<String, ZipEntry> zipEntries) {
		String filePath = OdfPackage.normalizePath(zipEntry.getName());
		ErrorHandler errorHandler = mPackage.getErrorHandler();
		if (errorHandler != null) {
			try {
				int zipMethod = zipEntry.getMethod();
				if (zipMethod != ZipEntry.STORED && zipMethod != ZipEntry.DEFLATED) {
					mPackage.getErrorHandler().error(new OdfValidationException(OdfPackageConstraint.PACKAGE_ENTRY_USING_INVALID_COMPRESSION, mPackage.getBaseURI(), filePath));
				}
			} catch (SAXException ex) {
				Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		zipEntries.put(filePath, zipEntry);
	}

	InputStream getInputStream(ZipEntry entry) throws IOException {
		if (mZipFile != null) {
			return mZipFile.getInputStream(entry);
		} else {
			ZipInputStream inputStream = new ZipInputStream(
					new ByteArrayInputStream(mZipBuffer));
			ZipEntry zipEntry = inputStream.getNextEntry();
			while (zipEntry != null) {
				if (zipEntry.getName().equalsIgnoreCase(entry.getName())) {
					return readAsInputStream(inputStream);
				}
				zipEntry = inputStream.getNextEntry();
			}
			return null;
		}
	}

	private InputStream readAsInputStream(ZipInputStream inputStream)
			throws IOException {
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
