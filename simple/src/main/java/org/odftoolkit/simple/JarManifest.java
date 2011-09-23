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
package org.odftoolkit.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides meta data about the Simple ODF library as build date, version
 * number. Its main() method is the start method of the library, enabling the
 * access of version methods from command line: "java -jar simple-odf.jar".
 */
public class JarManifest {

	private static final String CURRENT_CLASS_RESOURCE_PATH = "org/odftoolkit/simple/JarManifest.class";
	private static final String MANIFEST_JAR_PATH = "META-INF/MANIFEST.MF";
	private static String SIMPLE_ODF_NAME;
	private static String SIMPLE_ODF_VERSION;
	private static String SIMPLE_ODF_WEBSITE;
	private static String SIMPLE_ODF_BUILD_BY;
	private static String SIMPLE_ODF_BUILD_DATE;
	private static String SIMPLE_ODF_SUPPORTED_ODF_VERSION;

	static {
		try {
			Manifest manifest = new Manifest(getManifestAsStream());
			Attributes attr = manifest.getEntries().get("SIMPLE-ODF");
			SIMPLE_ODF_NAME = attr.getValue("SIMPLE-ODF-Name");
			SIMPLE_ODF_VERSION = attr.getValue("SIMPLE-ODF-Version");
			SIMPLE_ODF_WEBSITE = attr.getValue("SIMPLE-ODF-Website");
			SIMPLE_ODF_BUILD_BY = attr.getValue("SIMPLE-ODF-Built-By");
			SIMPLE_ODF_BUILD_DATE = attr.getValue("SIMPLE-ODF-Built-Date");
			SIMPLE_ODF_SUPPORTED_ODF_VERSION = attr.getValue("SIMPLE-ODF-Supported-ODF-Version");
		} catch (Exception e) {
			Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE,
					null, e);
		}
	}

	private static InputStream getManifestAsStream() {
		String versionRef = JarManifest.class.getClassLoader().getResource(
				CURRENT_CLASS_RESOURCE_PATH).toString();
		String manifestRef = versionRef.substring(0, versionRef
				.lastIndexOf(CURRENT_CLASS_RESOURCE_PATH))
				+ MANIFEST_JAR_PATH;
		URL manifestURL = null;
		InputStream in = null;
		try {
			manifestURL = new URL(manifestRef);
		} catch (MalformedURLException ex) {
			Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		try {
			in = manifestURL.openStream();
		} catch (IOException ex) {
			Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return in;
	}

	private JarManifest() {
	}

	/**
	 * The main method is meant to be called when the JAR is being executed,
	 * e.g. "java -jar simple-odf.jar" and provides version information:
	 * 
	 * simple-odf 0.3 (build 20110201-1729) from http://odftoolkit.org
	 * supporting ODF 1.2
	 * 
	 * Allowing version access from the JAR without the need to unzip the JAR
	 * nor naming the JAR (requiring the change of class path for every version
	 * due to JAR naming change).
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(getSimpleOdfTitle() + " (build "
				+ getSimpleOdfBuildDate() + ')' + "\nfrom "
				+ getSimpleOdfWebsite() + " supporting ODF "
				+ getSimpleOdfSupportedOdfVersion());
	}

	/**
	 * Return the name of Simple ODF;
	 * 
	 * @return the Simple ODF library name
	 */
	public static String getSimpleOdfName() {
		return SIMPLE_ODF_NAME;
	}

	/**
	 * Returns the Simple ODF library title
	 * 
	 * @return A string containing both the name and the version of the Simple
	 *         ODF library.
	 */
	public static String getSimpleOdfTitle() {
		return getSimpleOdfName() + ' ' + getSimpleOdfVersion();
	}

	/**
	 * Return the version of the Simple ODF library (ie. simple-odf.jar)
	 * 
	 * @return the Simple ODF library version
	 */
	public static String getSimpleOdfVersion() {
		return SIMPLE_ODF_VERSION;
	}

	/**
	 * Return the website of the Simple ODF library (ie. simple-odf.jar)
	 * 
	 * @return the Simple ODF library website
	 */
	public static String getSimpleOdfWebsite() {
		return SIMPLE_ODF_WEBSITE;
	}

	/**
	 * Return the name of the one building the Simple ODF library (ie.
	 * simple-odf.jar)
	 * 
	 * @return the name of the Simple ODF library builder
	 */
	public static String getSimpleOdfBuildResponsible() {
		return SIMPLE_ODF_BUILD_BY;
	}

	/**
	 * Return the date when Simple ODF had been build
	 * 
	 * @return the date of the build
	 */
	public static String getSimpleOdfBuildDate() {
		return SIMPLE_ODF_BUILD_DATE;
	}

	/**
	 * Returns the version of the OpenDocument specification covered by the
	 * Simple ODF library (ie. simple-odf.jar)
	 * 
	 * @return the supported ODF version number
	 */
	public static String getSimpleOdfSupportedOdfVersion() {
		return SIMPLE_ODF_SUPPORTED_ODF_VERSION;
	}
}
