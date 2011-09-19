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

package org.odftoolkit.odfvalidator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import junit.framework.Assert;
import org.junit.Test;

/** Test some invalid packages.
 */
public class InvalidPackageTest {
    private String doValidation(String file, OdfVersion version) throws Exception
    {
            ODFValidator validator = new ODFValidator(null, Logger.LogLevel.INFO, version, true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PrintStream pout = new PrintStream(out);
            InputStream is = getClass().getClassLoader().getResourceAsStream(file);
//            validator.validateFile(pout, f, OdfValidatorMode.VALIDATE, null);
            validator.validateStream(pout, is, file, OdfValidatorMode.VALIDATE, null);
//            System.err.println(out.toString());
            return out.toString();
    }

    @Test
    public void validatePackage1() {
        String output = "";
        try {
            String name = "testInvalidPkg1.odt";
            output = doValidation(name, null);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/MIMETYPE:Error:The file 'mimetype' is not the first file in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/MIMETYPE:Error:The file 'mimetype' shall not be compressed"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/MIMETYPE:Error:There shall be no extra field for the 'mimetype' file"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/META-INF/manifest.xml:Error:The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt:Info:validation errors found"));
    }

    @Test
    public void validatePackage1_V1_1() {
        String output = "";
        try {
            String name = "testInvalidPkg1.odt";
            output = doValidation(name, OdfVersion.V1_1);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/MIMETYPE:Warning:The file 'mimetype' is not the first file in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/MIMETYPE:Error:The file 'mimetype' shall not be compressed"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/MIMETYPE:Error:There shall be no extra field for the 'mimetype' file"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt/META-INF/manifest.xml:Error:The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg1.odt:Info:validation errors found"));
    }

    @Test
    public void validatePackage2() {
        String output = "";
        try {
            String name = "testInvalidPkg2.odt";
            output = doValidation(name, null);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/MIMETYPE:Error:The ODF package contains no 'mimetype' file"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/META-INF/manifest.xml:Error:The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/META-INF/manifest.xml:Error:The file 'not_in_manifest' shall be listed in the 'META-INF/manifest.xml' file as it exists in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt:Info:validation errors found"));
    }

    @Test
    public void validatePackage2_V1_1() {
        String output = "";
        try {
            String name = "testInvalidPkg2.odt";
            output = doValidation(name, OdfVersion.V1_1);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/MIMETYPE:Warning:The ODF package contains no 'mimetype' file"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/META-INF/manifest.xml:Error:The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt/META-INF/manifest.xml:Warning:The file 'not_in_manifest' shall be listed in the 'META-INF/manifest.xml' file as it exists in the ODF package"));
        Assert.assertTrue(output.contains("testInvalidPkg2.odt:Info:validation errors found"));
    }

    @Test
    public void validatePackage3() {
        String output = "";
        try {
            String name = "testInvalidPkg3.odt";
            output = doValidation(name, null);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
        Assert.assertTrue(output.contains("Error:The ODF package 'testInvalidPkg3.odt' shall contain the 'META-INF/manifest.xml' file"));
        Assert.assertTrue(output.contains("testInvalidPkg3.odt:Info:validation errors found"));
    }

}
