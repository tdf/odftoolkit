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
            String jarPath = "target" + File.separatorChar + JAR_NAME_PREFIX + odfdomVersion + commandSuffix;

            // TRIGGERING COMMAND LINE JAR EXECUTION
            String firstOutputLine = null;
            String secondOutputLine = null;
            try {
                ProcessBuilder builder = new ProcessBuilder("java",  "-jar", jarPath);
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
                    if(firstOutputLine == null){
                        firstOutputLine = line;
                    }else{
                        secondOutputLine = line;
                    }
                }
            } catch (IOException t) {
                StringWriter errors = new StringWriter();
                t.printStackTrace(new PrintWriter(errors));
                Assert.fail(t.toString() + "\n" + errors.toString());
            }


            // EVALUATING COMMAND LINE INFORMATION
            LOG.log(Level.INFO, "The version info from commandline given by {0} is:\n", "java -jar" + jarPath);
            LOG.log(Level.INFO, "\"{0}\"", firstOutputLine);
            LOG.log(Level.INFO, "\"{0}\"", secondOutputLine);
            Assert.assertEquals(JarManifest.getOdfdomTitle() + " (build " + JarManifest.getOdfdomBuildDate() + ')', firstOutputLine);
            Assert.assertEquals("from " + JarManifest.getOdfdomWebsite() + " supporting ODF " + JarManifest.getOdfdomSupportedOdfVersion(), secondOutputLine);



            // EVALUATING JAR MANIFEST INFORMATION
            LOG.log(Level.INFO, "\nJarManifest.getName(): {0}", JarManifest.getOdfdomName());
            Assert.assertNotNull(JarManifest.getOdfdomName());

            LOG.log(Level.INFO, "\nJarManifest.getTitle(): {0}", JarManifest.getOdfdomTitle());
            Assert.assertNotNull(JarManifest.getOdfdomTitle());

            LOG.log(Level.INFO, "\nJarManifest.getVersion(): {0}", JarManifest.getOdfdomVersion());
            Assert.assertNotNull(JarManifest.getOdfdomVersion());

            LOG.log(Level.INFO, "\nJarManifest.getBuildDate(): {0}", JarManifest.getOdfdomBuildDate());
            Assert.assertNotNull(JarManifest.getOdfdomBuildDate());

            LOG.log(Level.INFO, "\nJarManifest.getSupportedOdfVersion(): {0}", JarManifest.getOdfdomSupportedOdfVersion());
            Assert.assertNotNull(JarManifest.getOdfdomSupportedOdfVersion());

        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }
//}
//	private static final Logger LOG = Logger.getLogger(JarManifestIT.class.getName());
//
//    @Test
//	public void testJar() {
//		try {
//
//
//            File jarFile = getFileFromSubstring("target" + File.separator, "-jar-with-dependencies.jar");
//            System.err.println("JarFile: " + jarFile.getAbsolutePath());
//			String command = "java -jar target" + File.separator + "odfdom-java-0.9.0-incubating-SNAPSHOT-jar-with-dependencies.jar";
//			Process process = Runtime.getRuntime().exec(command);
//			process.waitFor();
//			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//			BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line;
//			while ((line = errorReader.readLine()) != null) {
//				LOG.info(line);
//				Assert.assertTrue(line.indexOf("Exception") == -1);
//			}
//			String firstOutputLine = outputReader.readLine();
//			String secondOutputLine = outputReader.readLine();
//			errorReader.close();
//			outputReader.close();
//			process.destroy();
//			LOG.log(Level.INFO, "The version info from commandline given by {0} is:\n", command);
//			LOG.log(Level.INFO, "\"{0}\"", firstOutputLine);
//			LOG.log(Level.INFO, "\"{0}\"", secondOutputLine);
//			Assert.assertEquals(firstOutputLine, JarManifest.getOdfdomTitle() + " (build " + JarManifest.getOdfdomBuildDate() + ')');
//			Assert.assertEquals(secondOutputLine, "from " + JarManifest.getOdfdomWebsite() + " supporting ODF " + JarManifest.getOdfdomSupportedOdfVersion());
//
//			LOG.log(Level.INFO, "\nJarManifest.getName(): {0}", JarManifest.getOdfdomName());
//			Assert.assertNotNull(JarManifest.getOdfdomName());
//
//			LOG.log(Level.INFO, "\nJarManifest.getTitle(): {0}", JarManifest.getOdfdomTitle());
//			Assert.assertNotNull(JarManifest.getOdfdomTitle());
//
//			LOG.log(Level.INFO, "\nJarManifest.getVersion(): {0}", JarManifest.getOdfdomVersion());
//			Assert.assertNotNull(JarManifest.getOdfdomVersion());
//
//			LOG.log(Level.INFO, "\nJarManifest.getBuildDate(): {0}", JarManifest.getOdfdomBuildDate());
//			Assert.assertNotNull(JarManifest.getOdfdomBuildDate());
//
//			LOG.log(Level.INFO, "\nJarManifest.getSupportedOdfVersion(): {0}", JarManifest.getOdfdomSupportedOdfVersion());
//			Assert.assertNotNull(JarManifest.getOdfdomSupportedOdfVersion());
//
//		} catch (Exception e) {
//			LOG.log(Level.SEVERE, null, e);
//		}
//	}
//
//    private File getFileFromSubstring(String relDirectoryPath, String fileSuffix) {
//        File searchFile = null;
//        File dir = new File(relDirectoryPath);
//        System.out.println(dir.getName());
//        System.out.println(dir.getAbsolutePath());
//        File files[] = dir.listFiles();
//        for (File f : files) {
//            System.out.println(f.getName());
//            if (f.getName().contains(fileSuffix)) {
//                System.out.println("Found IT!");
//                searchFile = f;
//            }
//        }
//        return searchFile;
//    }

}
