/**
 * **********************************************************************
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
package org.odftoolkit.odfvalidator;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides metadata about the library as build date, version number. Its main() method is the start
 * method of the library, enabling the access of versioning methods from command line: "java -jar
 * odfvalidator-java-<VERSION_INFO>-jar-with-dependencies.jar".
 */
public class JarManifest {

  private static final String CURRENT_CLASS_RESOURCE_PATH =
      "org/odftoolkit/odfvalidator/JarManifest.class";
  private static final String INNER_JAR_MANIFEST_PATH = "META-INF/MANIFEST.MF";
  private static String ODFVALIDATOR_NAME;
  private static String ODFVALIDATOR_VERSION;
  private static String ODFVALIDATOR_WEBSITE;
  private static String ODFVALIDATOR_BUILD_DATE;
  private static String ODFVALIDATOR_SUPPORTED_ODF_VERSION;

  static {
    try {
      Manifest manifest = new Manifest(getManifestAsStream());
      Attributes attr = manifest.getEntries().get("ODFVALIDATOR");
      ODFVALIDATOR_NAME = attr.getValue("ODFVALIDATOR-Name");
      ODFVALIDATOR_VERSION = attr.getValue("ODFVALIDATOR-Version");
      ODFVALIDATOR_WEBSITE = attr.getValue("ODFVALIDATOR-Website");
      ODFVALIDATOR_BUILD_DATE = attr.getValue("ODFVALIDATOR-Built-Date");
      ODFVALIDATOR_SUPPORTED_ODF_VERSION = attr.getValue("ODFVALIDATOR-Supported-Odf-Version");
    } catch (Exception e) {
      Logger.getLogger(JarManifest.class.getName()).log(Level.SEVERE, null, e);
    }
  }

  /**
   * The problem is that in the test environment the class is NOT within the JAR, but in a class
   * diretory where no MANIFEST.MF exists..
   */
  private static InputStream getManifestAsStream() {
    String versionRef =
        JarManifest.class.getClassLoader().getResource(CURRENT_CLASS_RESOURCE_PATH).toString();
    String manifestRef =
        versionRef.substring(0, versionRef.lastIndexOf(CURRENT_CLASS_RESOURCE_PATH))
            + INNER_JAR_MANIFEST_PATH;
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

  private JarManifest() {}

  /**
   * Return the name of ODFVALIDATOR;
   *
   * @return the ODFVALIDATOR library name
   */
  public static String getLibraryName() {
    return ODFVALIDATOR_NAME;
  }

  /**
   * Returns the ODFVALIDATOR library title
   *
   * @return A string containing both the name and the version of the ODFVALIDATOR library.
   */
  public static String getTitle() {
    return getLibraryName() + ' ' + getVersion();
  }

  /**
   * Return the version of the ODFVALIDATOR library (ie. odfdom.jar)
   *
   * @return the ODFVALIDATOR library version
   */
  public static String getVersion() {
    return ODFVALIDATOR_VERSION;
  }

  /**
   * Return the website of the ODFVALIDATOR library (ie. odfdom.jar)
   *
   * @return the ODFVALIDATOR library website
   */
  public static String getWebsite() {
    return ODFVALIDATOR_WEBSITE;
  }

  /**
   * Return the date when ODFVALIDATOR had been build
   *
   * @return the date of the build formated as "yyyy-MM-dd'T'HH:mm:ss".
   */
  public static String getBuildDate() {
    return ODFVALIDATOR_BUILD_DATE;
  }

  /**
   * Returns the version of the OpenDocument specification covered by the ODFVALIDATOR library (ie.
   * odfdom.jar)
   *
   * @return the supported ODF version number
   */
  public static String getSupportedOdfVersion() {
    return ODFVALIDATOR_SUPPORTED_ODF_VERSION;
  }
}
