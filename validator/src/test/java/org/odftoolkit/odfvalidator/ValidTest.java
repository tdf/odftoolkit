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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** Test some invalid packages. */
public class ValidTest extends OdfValidatorTestBase {

  @Test
  public void validate1() {
    String output = "";
    try {
      String name = "empty.odt";
      output = doValidation(name, OdfVersion.V1_0, OdfValidatorMode.VALIDATE_STRICT, true);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occured during validation!");
    }
    Assert.assertTrue(output.contains("<span "));
  }

  @Test
  @Ignore
  public void validate2() {
    String output = "";
    try {
      String name = "testValid1.odt";
      output = doValidation(name, null);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occured during validation!");
    }
    Assert.assertTrue(output.contains("testValid1.odt:Info:no errors, no warnings"));
  }

  @Test
  @Ignore
  public void validate3() {
    String output = "";
    try {
      String name = "empty.odt";
      output = doValidation(name, OdfVersion.V1_0, OdfValidatorMode.VALIDATE_STRICT);
      output = doValidation(name, OdfVersion.V1_1, OdfValidatorMode.VALIDATE);
      output = doValidation(name, OdfVersion.V1_2, null);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occured during validation!");
    }
    // Assert.assertTrue(output.contains("dummy.odt:Info:no errors, no warnings"));
  }

  @Test
  public void validateForeignElementCharacterContentStrict() {
    String output = "";
    try {
      String name = "extnumberstyle.ods";
      output = doValidation(name, OdfVersion.V1_2, OdfValidatorMode.CONFORMANCE, true);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occurred during validation!");
    }
    System.out.println("OUTPUT:" + output);
    Assert.assertTrue(output.contains("Error:"));
    Assert.assertTrue(output.contains("styles.xml[32,19]:"));
  }

  @Test
  public void validateForeignElementCharacterContentExtended() {
    String output = "";
    try {
      String name = "extnumberstyle.ods";
      output = doValidation(name, OdfVersion.V1_2, OdfValidatorMode.EXTENDED_CONFORMANCE, true);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occurred during validation!");
    }
    System.out.println("OUTPUT:" + output);
    Assert.assertFalse(output.contains("unexpected character literal"));
    Assert.assertFalse(output.contains("Error:"));
  }

  @Test
  public void validateForeignElementCharacterContentExtendedProcessContent() {
    String output = "";
    try {
      String name = "extnumberstyle-processcontent.ods";
      output = doValidation(name, OdfVersion.V1_2, OdfValidatorMode.EXTENDED_CONFORMANCE, true);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occurred during validation!");
    }
    System.out.println("OUTPUT:" + output);
    Assert.assertTrue(output.contains("unexpected character literal"));
    Assert.assertTrue(output.contains("styles.xml[34,79]:"));
  }

  @Test
  public void validateForeignElementCharacterContentExtendedInNonCharacterElement() {
    String output = "";
    try {
      String name = "sender-initials.odt";
      output = doValidation(name, OdfVersion.V1_2, OdfValidatorMode.EXTENDED_CONFORMANCE, true);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occurred during validation!");
    }
    System.out.println("OUTPUT:" + output);
    Assert.assertFalse(output.contains("unexpected character literal"));
    Assert.assertFalse(output.contains("Error:"));
  }

  @Test
  public void validateFormTextBox() {
    String output = "";
    try {
      String name = "testFormTextBox.odt";
      output = doValidation(name, OdfVersion.V1_4, OdfValidatorMode.EXTENDED_CONFORMANCE, true);
    } catch (Throwable t) {
      t.printStackTrace();
      Assert.fail(t.toString());
    }
    if (output.contains("Exception")) {
      System.out.println("OUTPUT:" + output);
      Assert.fail("An exception occurred during validation!");
    }
    System.out.println("OUTPUT:" + output);
    Assert.assertFalse(output.contains("unexpected attribute"));
    Assert.assertFalse(output.contains("Error:"));
  }
}
