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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import org.junit.Ignore;

@Ignore
public class OdfValidatorTestBase {

  public OdfValidatorTestBase() {}

  String doValidation(String aFileName, OdfVersion aVersion) throws Exception {
    ODFValidator aValidator = new ODFValidator(null, Logger.LogLevel.INFO, aVersion);
    ByteArrayOutputStream aOut = new ByteArrayOutputStream();
    PrintStream aPOut = new PrintStream(aOut);
    InputStream aIn = getClass().getClassLoader().getResourceAsStream(aFileName);
    if (aIn == null) {
      java.util.logging.Logger.getLogger(OdfValidatorTestBase.class.getName())
          .log(Level.SEVERE, "The input document '" + aFileName + "' could not be found!");
    }
    aValidator.validateStream(aPOut, aIn, aFileName, OdfValidatorMode.VALIDATE, null);
    return aOut.toString();
  }

  String doValidation(String aFileName, OdfVersion aVersion, OdfValidatorMode odfValidatorMode)
      throws Exception {
    return doValidation(aFileName, aVersion, odfValidatorMode, false);
  }

  String doValidation(
      String aFileName, OdfVersion aVersion, OdfValidatorMode odfValidatorMode, boolean htmlOutput)
      throws Exception {
    ODFValidator aValidator = new ODFValidator(null, Logger.LogLevel.INFO, htmlOutput, aVersion);
    ByteArrayOutputStream aOut = new ByteArrayOutputStream();
    PrintStream aPOut = new PrintStream(aOut);
    InputStream aIn = getClass().getClassLoader().getResourceAsStream(aFileName);
    ValidationMessageCollectorErrorFilter filter = new ValidationMessageCollectorErrorFilter();
    try {
      aValidator.validateStream(aPOut, aIn, aFileName, odfValidatorMode, filter);
    } catch (Exception e) {
      e.printStackTrace(aPOut);
    }
    return aOut.toString();
  }
}
