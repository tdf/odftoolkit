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
package org.odftoolkit.odfdom.integrationtest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.JarManifest;

public class JarManifestIT {

  private static final Logger LOG = Logger.getLogger(JarManifestIT.class.getName());
  private static final String JAR_NAME_PREFIX = "odfdom-java-";
  private static final String JAR_NAME_SUFFIX_1 = "-jar-with-dependencies.jar";
  private static final String JAR_NAME_SUFFIX_2 = ".jar";

  @Test
  public void testOdfdomJar() {
    testJar(JAR_NAME_SUFFIX_1);
  }

  @Test
  public void testOdfdomWithDependenciesJar() {
    testJar(JAR_NAME_SUFFIX_2);
  }

  private void testJar(String commandSuffix) {
    try {
      // CREATING THE JAR PATH
      String odfdomVersion = System.getProperty("odfdom.version");
      String jarPath =
          "target" + File.separatorChar + JAR_NAME_PREFIX + odfdomVersion + commandSuffix;

      // TRIGGERING COMMAND LINE JAR EXECUTION
      String firstOutputLine = null;
      String secondOutputLine = null;
      try {
        ProcessBuilder builder = new ProcessBuilder("java", "-jar", jarPath);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (true) {
          line = r.readLine();
          if (line == null) {
            break;
          }
          if (line.contains("Exception")) {
            throw new IOException(line);
          }
          if (firstOutputLine == null) {
            firstOutputLine = line;
          } else {
            secondOutputLine = line;
          }
        }
      } catch (IOException t) {
        StringWriter errors = new StringWriter();
        t.printStackTrace(new PrintWriter(errors));
        Assert.fail(t.toString() + "\n" + errors.toString());
      }

      // EVALUATING COMMAND LINE INFORMATION
      LOG.log(
          Level.INFO,
          "The version info from commandline given by {0} is:\n",
          "java -jar" + jarPath);
      LOG.log(Level.INFO, "\"{0}\"", firstOutputLine);
      LOG.log(Level.INFO, "\"{0}\"", secondOutputLine);
      Assert.assertEquals(
          JarManifest.getOdfdomTitle() + " (build " + JarManifest.getOdfdomBuildDate() + ')',
          firstOutputLine);
      Assert.assertEquals(
          "from "
              + JarManifest.getOdfdomWebsite()
              + " supporting ODF "
              + JarManifest.getOdfdomSupportedOdfVersion(),
          secondOutputLine);

      // EVALUATING JAR MANIFEST INFORMATION
      LOG.log(Level.INFO, "\nJarManifest.getName(): {0}", JarManifest.getOdfdomName());
      Assert.assertNotNull(JarManifest.getOdfdomName());

      LOG.log(Level.INFO, "\nJarManifest.getTitle(): {0}", JarManifest.getOdfdomTitle());
      Assert.assertNotNull(JarManifest.getOdfdomTitle());

      LOG.log(Level.INFO, "\nJarManifest.getVersion(): {0}", JarManifest.getOdfdomVersion());
      Assert.assertNotNull(JarManifest.getOdfdomVersion());

      LOG.log(Level.INFO, "\nJarManifest.getBuildDate(): {0}", JarManifest.getOdfdomBuildDate());
      Assert.assertNotNull(JarManifest.getOdfdomBuildDate());

      LOG.log(
          Level.INFO,
          "\nJarManifest.getSupportedOdfVersion(): {0}",
          JarManifest.getOdfdomSupportedOdfVersion());
      Assert.assertNotNull(JarManifest.getOdfdomSupportedOdfVersion());

    } catch (Exception e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }
}
