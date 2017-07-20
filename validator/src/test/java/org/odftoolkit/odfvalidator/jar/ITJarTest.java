/** **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
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
 *********************************************************************** */
package org.odftoolkit.odfvalidator.jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;

/**
 * Integration test to validate via JAR testing command line parameters on
 * (an invalid) ODF package.
 */
public class ITJarTest {
    private static final String JAR_NAME_SUFFIX = "-jar-with-dependencies.jar";
    private static final String JAR_NAME_PREFIX = "odfvalidator-";
    private static final String ODT_NAME = "testInvalidPkg2.odt"; // password: hello
    private static final String FATAL_PREFIX = "Fatal: ";

    @Test
    public void validateOdtViaCommandline() {
        String output = "";
        try {
            // Command line call might be:
            // java -jar .m2/repository/org/apache/odftoolkit/odfvalidator/1.2.0-incubating-SNAPSHOT/odfvalidator-1.2.0-incubating-SNAPSHOT-jar-with-dependencies.jar foo.odt
            String odfvalidatorVersion = System.getProperty("odfvalidator.version");
            ProcessBuilder builder = new ProcessBuilder(
                "java", "-jar", "target" + File.separatorChar + JAR_NAME_PREFIX + odfvalidatorVersion + JAR_NAME_SUFFIX,
                "target" + File.separatorChar + "test-classes" + File.separatorChar + ODT_NAME);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                output = output.concat(line + "\n");
                if (output.contains(FATAL_PREFIX) || output.contains("Exception")) {
                    throw new IOException(output);
                }
            }
        } catch (IOException t) {
            StringWriter errors = new StringWriter();
            t.printStackTrace(new PrintWriter(errors));
            Assert.fail(t.toString() + "\n" + errors.toString());
        }
        java.util.logging.Logger.getLogger(getClass().getName()).log(Level.INFO, "Test result:\n{0}", output);
        Assert.assertTrue(output.contains("contains no 'mimetype' file"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/META-INF/manifest.xml:  Error: The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/META-INF/manifest.xml:  Error: The file 'not_in_manifest' shall be listed in the 'META-INF/manifest.xml' file as it exists in the ODF package"));
    }
}
