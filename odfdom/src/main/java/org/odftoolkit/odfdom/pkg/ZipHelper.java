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
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

class ZipHelper {

	private ZipFile mZipFile = null;
	private byte[] mZipBuffer = null;

	public ZipHelper(ZipFile zipFile) {
		mZipFile = zipFile;
		mZipBuffer = null;
	}

	public ZipHelper(byte[] buffer) {
		mZipBuffer = buffer;
		mZipFile = null;
	}

	public Map<String, ZipEntry> entries() throws IOException {
		HashMap<String, ZipEntry> zipEntries = new HashMap<String, ZipEntry>();
		ZipEntry zipEntry = null;
		if (mZipFile != null) {
			Enumeration<? extends ZipEntry> entries = mZipFile.entries();
			if (!entries.hasMoreElements()) {
				throw new IllegalArgumentException("Could not unzip the given ODF package!");
			} else {
				zipEntry = entries.nextElement();
				if (zipEntry != null) {
					checkManifestFirst(zipEntry);
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
				zipEntry = inputStream.getNextEntry();
				if (zipEntry != null) {
					checkManifestFirst(zipEntry);
					addZipEntry(zipEntry, zipEntries);
					while (zipEntry != null) {
						addZipEntry(zipEntry, zipEntries);
						zipEntry = inputStream.getNextEntry();
					}
				}
			}
			inputStream.close();
		}
		return zipEntries;
	}

	private boolean checkManifestFirst(ZipEntry zipEntry) {
		boolean isFirst = true;
		String filePath = OdfPackage.normalizePath(zipEntry.getName());
		// first file must be "mimetype", should not be added to zipentries
		if (!filePath.equals(OdfPackage.OdfFile.MEDIA_TYPE.getPath()) && isFirst) {
			//Logger.getLogger(ZipHelper.class.getName()).severe("INVALID ODF: The file 'mimetype' is not the first");
			isFirst = false;
		}
		return isFirst;
	}

	private void addZipEntry(ZipEntry zipEntry, Map<String, ZipEntry> zipEntries) {
		String filePath = OdfPackage.normalizePath(zipEntry.getName());
//		// if resource is not the "mimetype" file
//		if (!filePath.equals(OdfPackage.OdfFile.MANIFEST.getPath())) {
//			// every resource aside the /META-INF/manifest.xml (and META-INF/ directory)
//			if (!filePath.equals(OdfPackage.OdfFile.MANIFEST.getPath())
//					&& !filePath.equals("META-INF/")) {
		zipEntries.put(filePath, zipEntry);
//			}
//		}
	}

	public InputStream getInputStream(ZipEntry entry) throws IOException {
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

	public void close() throws IOException {
		if (mZipFile != null) {
			mZipFile.close();
		} else {
			mZipBuffer = null;
		}
	}
}
