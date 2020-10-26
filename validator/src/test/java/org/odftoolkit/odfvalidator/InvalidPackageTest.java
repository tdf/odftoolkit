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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Test;

/** Test some invalid packages. */
public class InvalidPackageTest extends OdfValidatorTestBase {

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
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: mimetype is not an ODFMediaTypes mimetype"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: The file 'mimetype' is not the first file in the ODF package"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: The file 'mimetype' shall not be compressed"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: There shall be no extra field for the 'mimetype' file"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/META-INF/manifest.xml:  Error: The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
    Assert.assertTrue(output.contains("testInvalidPkg1.odt:  Info: 8 errors, 10 warnings"));
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
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: mimetype is not an ODFMediaTypes mimetype"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Warning: The file 'mimetype' is not the first file in the ODF package"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: The file 'mimetype' shall not be compressed"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/mimetype:  Error: There shall be no extra field for the 'mimetype' file"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg1.odt/META-INF/manifest.xml:  Error: The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
    Assert.assertTrue(output.contains("testInvalidPkg1.odt:  Info: 11 errors, 11 warnings"));
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
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg2.odt/mimetype:  Error: The ODF package 'testInvalidPkg2.odt' contains no 'mimetype' file"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg2.odt/META-INF/manifest.xml:  Error: The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg2.odt/META-INF/manifest.xml:  Error: The file 'not_in_manifest' shall be listed in the 'META-INF/manifest.xml' file as it exists in the ODF package"));
    Assert.assertTrue(output.contains("testInvalidPkg2.odt:  Info: 5 errors, 10 warnings"));
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
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg2.odt/mimetype:  Warning: The ODF package 'testInvalidPkg2.odt' contains no 'mimetype' file"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg2.odt/META-INF/manifest.xml:  Error: The file 'Configurations2/accelerator/current.xml' shall not be listed in the 'META-INF/manifest.xml' file as it does not exist in the ODF package"));
    Assert.assertTrue(
        output.contains(
            "testInvalidPkg2.odt/META-INF/manifest.xml:  Warning: The file 'not_in_manifest' shall be listed in the 'META-INF/manifest.xml' file as it exists in the ODF package"));
    Assert.assertTrue(output.contains("testInvalidPkg2.odt:  Info: 7 errors, 12 warnings"));
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
    Assert.assertTrue(
        "Output of validatePackage3(): " + output,
        output.contains(
            "Error: The ODF package 'testInvalidPkg3.odt' shall contain the 'META-INF/manifest.xml' file"));
    Assert.assertTrue(
        "Output of validatePackage3(): " + output,
        output.contains("testInvalidPkg3.odt:  Info: 3 errors, no warnings"));
  }

  @Test
  public void validateEncryptedODT() {
    String output = "";
    try {
      // password: hello
      String name = "encrypted-with-pwd_hello.odt";
      output = doValidation(name, null);
    } catch (Exception t) {
      StringWriter errors = new StringWriter();
      t.printStackTrace(new PrintWriter(errors));
      Assert.fail(t.toString() + "\n" + errors.toString());
    }
    Assert.assertFalse(
        output.contains(
            "The document is encrypted. Validation of encrypted documents is not supported."));
    java.util.logging.Logger.getLogger(getClass().getName())
        .log(Level.INFO, "Test result:\n{0}", output);
  }

  @Test
  public void validateDocumentSignature() {
    String output = "";
    try {
      // the manifest.xml is valid, but "META-INF/documentsignatures.xml"
      // was erroneously reported as missing
      String name = "good.odt";
      output = doValidation(name, OdfVersion.V1_2, OdfValidatorMode.EXTENDED_CONFORMANCE);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    Assert.assertFalse(
        "Output of validateDocumentSignature(): " + output,
        output.contains(
            "Error: The file 'META-INF/documentsignatures.xml' shall be listed in the 'META-INF/manifest.xml' file as it exists in the ODF package 'good.odt'"));
    Assert.assertTrue(
        "Output of validateDocumentSignature(): " + output,
        output.contains("Error: element \"document-signatures\" is missing \"version\" attribute"));
    Assert.assertTrue(
        "Output of validateDocumentSignature(): " + output,
        output.contains("good.odt:  Info: 1 errors, no warnings"));
  }
}
