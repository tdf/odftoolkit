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
package org.odftoolkit.simple.integrationtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.simple.JarManifest;

public class JarManifestIT {

  private static final Logger LOG = Logger.getLogger(JarManifestIT.class.getName());

  @Test
  public void testJar() {
    try {
      String command = "java -jar target/simple.jar";
      Process process = Runtime.getRuntime().exec(command);
      process.waitFor();
      BufferedReader errorReader =
          new BufferedReader(new InputStreamReader(process.getErrorStream()));
      BufferedReader outputReader =
          new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = errorReader.readLine()) != null) {
        LOG.info(line);
        Assert.assertTrue(line.indexOf("Exception") == -1);
      }
      String firstOutputLine = outputReader.readLine();
      String secondOutputLine = outputReader.readLine();
      errorReader.close();
      outputReader.close();
      process.destroy();
      LOG.log(Level.INFO, "The version info from commandline given by {0} is:\n", command);
      LOG.log(Level.INFO, "\"{0}\"", firstOutputLine);
      LOG.log(Level.INFO, "\"{0}\"", secondOutputLine);
      Assert.assertEquals(
          firstOutputLine,
          JarManifest.getSimpleOdfTitle() + " (build " + JarManifest.getSimpleOdfBuildDate() + ')');
      Assert.assertEquals(
          secondOutputLine,
          "from "
              + JarManifest.getSimpleOdfWebsite()
              + " supporting ODF "
              + JarManifest.getSimpleOdfSupportedOdfVersion());

      LOG.log(Level.INFO, "\nJarManifest.getName(): {0}", JarManifest.getSimpleOdfName());
      Assert.assertNotNull(JarManifest.getSimpleOdfName());

      LOG.log(Level.INFO, "\nJarManifest.getTitle(): {0}", JarManifest.getSimpleOdfTitle());
      Assert.assertNotNull(JarManifest.getSimpleOdfTitle());

      LOG.log(Level.INFO, "\nJarManifest.getVersion(): {0}", JarManifest.getSimpleOdfVersion());
      Assert.assertNotNull(JarManifest.getSimpleOdfVersion());

      LOG.log(Level.INFO, "\nJarManifest.getBuildDate(): {0}", JarManifest.getSimpleOdfBuildDate());
      Assert.assertNotNull(JarManifest.getSimpleOdfBuildDate());

      LOG.log(
          Level.INFO,
          "\nJarManifest.getBuildResponsible(): {0}",
          JarManifest.getSimpleOdfBuildResponsible());
      Assert.assertNotNull(JarManifest.getSimpleOdfBuildResponsible());

      LOG.log(
          Level.INFO,
          "\nJarManifest.getSupportedOdfVersion(): {0}",
          JarManifest.getSimpleOdfSupportedOdfVersion());
      Assert.assertNotNull(JarManifest.getSimpleOdfSupportedOdfVersion());

    } catch (Exception e) {
      LOG.log(Level.SEVERE, null, e);
      Assert.fail();
    }
  }
}
