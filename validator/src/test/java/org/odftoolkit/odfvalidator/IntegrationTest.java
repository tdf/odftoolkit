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
package org.odftoolkit.odfvalidator;

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

public class IntegrationTest {

  private static final Logger LOG = Logger.getLogger(IntegrationTest.class.getName());
  private static final String JAR_NAME_PREFIX = "odfvalidator-";
  private static final String JAR_NAME_SUFFIX_1 = "-jar-with-dependencies.jar";
  private static final String JAR_NAME_SUFFIX_2 = ".war";

  @Test
  public void testValidatorJar() {
    testJar(JAR_NAME_SUFFIX_1);
  }

  // cannot start this via -jar
  // @Test
  public void testValidatorWar() {
    testJar(JAR_NAME_SUFFIX_2);
  }

  private void testJar(String commandSuffix) {
    try {
      // creating the jar path
      String validatorVersion = System.getProperty("odfvalidator.version");
      String jarPath =
          "target" + File.separatorChar + JAR_NAME_PREFIX + validatorVersion + commandSuffix;

      // triggering command line jar execution
      String output = "";
      try {
        String javaHome = System.getenv("JAVA_HOME");
        ProcessBuilder builder;
        String javaPath;
        if (javaHome == null || javaHome.isEmpty()) {
          LOG.info("JAVA_HOME not set, therefore calling default java!");
          javaPath = "java";
        } else {
          LOG.log(Level.INFO, "Calling java defined by JAVA_HOME: {0}/bin/java", javaHome);
          javaPath = System.getenv("JAVA_HOME") + "/bin/java";
        }
        String testFolder =
            IntegrationTest.class.getClassLoader().getResource("").toURI().getPath();
        String file = testFolder + "math_OOo311.odt";
        builder = new ProcessBuilder(javaPath, "-jar", jarPath, "-1.1", /*"-d",*/ file);
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
          output = output + line;
        }
      } catch (IOException t) {
        StringWriter errors = new StringWriter();
        t.printStackTrace(new PrintWriter(errors));
        Assert.fail(t.toString() + "\n" + errors.toString());
      }

      // that would be validating with 1.2
      Assert.assertFalse(
          output.contains(
              "math_OOo311.odt/META-INF/manifest.xml[2,88]:  Error: element \"manifest:manifest\" is missing \"version\" attribute"));

      // 1.1:
      Assert.assertTrue(
          output.contains(
              "math_OOo311.odt/META-INF/manifest.xml[3,132]:  Error: unexpected attribute \"manifest:version\""));
      Assert.assertTrue(
          output.contains(
              "math_OOo311.odt/META-INF/manifest.xml[19,143]:  Error: unexpected attribute \"manifest:version\""));
      // customized schema...
      Assert.assertFalse(
          output.contains(
              "math_OOo311.odt/Object 1/content.xml[3,60]:  Error: Attribute \"xmlns:math\" must be declared for element type \"math:math\""));

    } catch (Exception e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }
}
