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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.changes.CollabTextDocument;
import org.odftoolkit.odfdom.changes.JsonOperationNormalizer;

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
  private static String ODFVALIDATOR_SCM;
  private static String ODFVALIDATOR_WEBSITE;
  private static String ODFVALIDATOR_BUILD_DATE;
  private static String ODFVALIDATOR_SUPPORTED_ODF_VERSION;

  static {
    try {
      Manifest manifest = new Manifest(getManifestAsStream());
      Attributes attr = manifest.getEntries().get("ODFVALIDATOR");
      ODFVALIDATOR_NAME = attr.getValue("ODFVALIDATOR-Name");
      ODFVALIDATOR_VERSION = attr.getValue("ODFVALIDATOR-Version");
      ODFVALIDATOR_SCM = attr.getValue("ODFVALIDATOR-SCM");
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
    String rootRef = versionRef.substring(0, versionRef.lastIndexOf(CURRENT_CLASS_RESOURCE_PATH));
    // 1. in war file, manifest is in top-level META-INF but classes in subdir
    // 2. somehow the URL is wrong, it's a file url to a directory named after
    //    the .war file but without .war extension - fix it to look like the
    //    one from .jar file
    if (rootRef.endsWith("/WEB-INF/classes/")) {
      rootRef = "jar:" + rootRef.substring(0, rootRef.lastIndexOf("/WEB-INF/classes/")) + ".war!/";
    }
    String manifestRef = rootRef + INNER_JAR_MANIFEST_PATH;
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
   * The main method is meant to be called when the JAR is being executed, e.g."java -jar
   * odfdom-java-1.0.0-SNAPSHOT-jar-with-dependencies.jar" and provides versioning information:
   *
   * <p>odfdom 1.0.0-SNAPSHOT (build 2019-07-11T12:38:27) from https://odftoolkit.org/odfdom/
   * supporting ODF 1.2
   *
   * <p>Allowing version access from the JAR without the need to unzip the JAR nor naming the JAR
   * (requiring the change of classpath for every version due to JAR naming change).
   *
   * @param args a relative path to the ODT that should be transformed to JSON changes
   */
  public static void main(String[] args) throws IOException, Exception {
    if (args == null || args.length == 0) {
      System.out.println(
          getTitle()
              + " (build "
              + getBuildDate()
              + ')'
              + "\nfrom "
              + getWebsite()
              + " supporting ODF "
              + getSupportedOdfVersion());
    } else {
      if (args[0].endsWith(".odt")) {
        // support of absolute file paths
        try (CollabTextDocument doc1 =
            new CollabTextDocument(Files.newInputStream(Paths.get(args[0])))) {
          System.out.println(
              JsonOperationNormalizer.asString(doc1.getDocumentAsChanges())
                  .replace(",{\"name\"", ",\n{\"name\""));
        } catch (FileNotFoundException e) {
          // support of relative file paths adding the current user directory ahead
          try (CollabTextDocument doc2 =
              new CollabTextDocument(
                  Files.newInputStream(
                      Paths.get(System.getProperty("user.dir") + File.separator + args[0])))) {
            System.out.println(
                JsonOperationNormalizer.asString(doc2.getDocumentAsChanges())
                    .replace(",{\"name\"", ",\n{\"name\""));
          }
        }
      } else {
        System.out.println(
            "NOTE:\n"
                + "The first argument have to be the relative path to an OpenDocument Text document and therefore have to end with '.odt'!\n"
                + "The ODT will be transformed to equivalent user changes (in JSON) to standard output");
      }
    }
  }

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

  /** @return the git commit id */
  public static String getSCMRevision() {
    return ODFVALIDATOR_SCM;
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
