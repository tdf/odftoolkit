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
 * Provides metadata about the SIMPLE library as build date, version number. Its
 * main() method is the start method of the library, enabling the access of
 * versioning methods from command line: "java -jar simple.jar".
 */
public class JarManifest {

    private static final String CURRENT_CLASS_RESOURCE_PATH = "org/odftoolkit/simple/JarManifest.class";
    private static final String MANIFEST_JAR_PATH = "META-INF/MANIFEST.MF";
    private static String SIMPLE_NAME;
    private static String SIMPLE_VERSION;
    private static String SIMPLE_WEBSITE;
    private static String SIMPLE_BUILD_BY;
    private static String SIMPLE_BUILD_DATE;
    private static String SIMPLE_SUPPORTED_ODF_VERSION;
   
    static {
        try {
            Manifest manifest = new Manifest(getManifestAsStream());
            Attributes attr = manifest.getEntries().get("SIMPLE");
            SIMPLE_NAME = attr.getValue("SIMPLE-Name");
            SIMPLE_VERSION = attr.getValue("SIMPLE-Version");
            SIMPLE_WEBSITE = attr.getValue("SIMPLE-Website");
            SIMPLE_BUILD_BY = attr.getValue("SIMPLE-Built-By");
            SIMPLE_BUILD_DATE = attr.getValue("SIMPLE-Built-Date");
            SIMPLE_SUPPORTED_ODF_VERSION = attr.getValue("SIMPLE-Supported-Odf-Version");
        } catch (Exception e) {
            Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static InputStream getManifestAsStream() {
        String versionRef = JarManifest.class.getClassLoader().getResource(CURRENT_CLASS_RESOURCE_PATH).toString();
        String manifestRef = versionRef.substring(0, versionRef.lastIndexOf(CURRENT_CLASS_RESOURCE_PATH)) + MANIFEST_JAR_PATH;
        URL manifestURL = null;
        InputStream in = null;
        try {
            manifestURL = new URL(manifestRef);
        } catch (MalformedURLException ex) {
            Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in = manifestURL.openStream();
        } catch (IOException ex) {
            Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return in;
    }

    private JarManifest() {
    }

	/**
	 * The main method is meant to be called when the JAR is being executed,
	 * e.g. "java -jar simple.jar" and provides versioning information:
	 *
	 *	simple 0.9-SNAPSHOT (build 20100701-1729)
	 *	from http://odftoolkit.org supporting ODF 1.2
	 *
	 * Allowing version access from the JAR without the need to unzip the JAR nor naming the JAR
	 * (requiring the change of classpath for every version due to JAR naming change).
	 */
    public static void main(String[] args) throws IOException {
        System.out.println(getSimpleTitle() + " (build " + getSimpleBuildDate() + ')' + "\nfrom " + getSimpleWebsite() + " supporting ODF " + getSimpleSupportedOdfVersion());
    }

    /**
     * Return the name of SIMPLE;
     * @return the SIMPLE library name
     */
    public static String getSimpleName() {
        return SIMPLE_NAME;
    }

    /**
     * Returns the SIMPLE library title
     * 
     * @return A string containing both the name and the version of the SIMPLE library.
     */
    public static String getSimpleTitle() {
        return getSimpleName() + ' ' + getSimpleVersion();
    }    

    /**
     * Return the version of the SIMPLE library (ie. simple.jar)
     * @return the SIMPLE library version
     */
    public static String getSimpleVersion() {
        return SIMPLE_VERSION;
    }

    /**
     * Return the website of the SIMPLE library (ie. simple.jar)
     * @return the SIMPLE library website
     */
    public static String getSimpleWebsite() {
        return SIMPLE_WEBSITE;
    }

    /**
     * Return the name of the one building the SIMPLE library (ie. simple.jar)
     * @return the name of the SIMPLE library builder
     */
    public static String getSimpleBuildResponsible() {
        return SIMPLE_BUILD_BY;
    }

    /**
     * Return the date when SIMPLE had been build
     * @return the date of the build
     */
    public static String getSimpleBuildDate() {
        return SIMPLE_BUILD_DATE;
    }

    /**
     * Returns the version of the OpenDocument specification covered by the SIMPLE library (ie. simple.jar)
     * @return the supported ODF version number
     */
    public static String getSimpleSupportedOdfVersion() {
        return SIMPLE_SUPPORTED_ODF_VERSION;
    }
}
