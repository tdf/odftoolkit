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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.stream.StreamSource;

import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;

/**
 * OdfPackageStream is a representation of a stream that is part of an ODF file.
 * If a stream is written to through the output stream, this will be
 * reflected when the output stream is closed
 *
 */
class OdfPackageStream extends StreamSource implements OdfPackageResource {

	private OdfPackage pkg;
	private String name;
	private InputStream inputStream;
	private OutputStream outputStream = null;
	private boolean bClosed;

	public OdfPackageStream(OdfPackage pkg, String name) throws Exception {

		super(pkg.getInputStream(name), pkg.getBaseURI() + "/" + name);
		this.pkg = pkg;
		this.name = name;
	}

	public boolean isOutput() {
		// denote that the output stream has been requested
		return outputStream != null;
	}

	public OutputStream getOutputStream() throws Exception {
		if (bClosed) {
			throw new IOException("stream already closed");
		}
		outputStream = pkg.insertOutputStream(name);
		return outputStream;
	}

	public OdfFileEntry geFileEntry() {
		return pkg.getFileEntry(name);
	}

	public String getName() {
		return name;
	}

	public OdfPackage getPackage() {
		return pkg;
	}

	/** If the OdfPackageStream is aligned to an OutputStream as well, this outputstream will be flushed */
	public void flush() throws IOException {
		if (outputStream != null) {
			outputStream.flush();
		}
	}

	public void close() throws IOException {
		bClosed = true;
		if (outputStream != null) {
			outputStream.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}
}
