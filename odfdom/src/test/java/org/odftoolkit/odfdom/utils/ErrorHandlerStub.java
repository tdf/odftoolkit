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
package org.odftoolkit.odfdom.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.pkg.OdfPackageConstraint;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.ValidationConstraint;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** A stub for a real SAX ErrorHandler embracing the expected amount of exceptions to be caught */
public class ErrorHandlerStub implements ErrorHandler {

  private static final Logger LOG = Logger.getLogger(ErrorHandlerStub.class.getName());
  /** Map which returns the number of defects for each given ValidationConstraint */
  private Map<ValidationConstraint, Integer> mExpectedWarning;
  private Map<ValidationConstraint, Integer> mExpectedError;
  private Map<ValidationConstraint, Integer> mExpectedFatalError;
  private String mTestFilePath = null;

  /**
   * @param expectedW Excpected Warnings - a Map relating a certain ValidationConstraint to the
   *     number of occurances
   */
  /**
   * @param expectedE Excpected Errors - a Map relating a certain ValidationConstraint to the number
   *     of occurances
   */
  /**
   * @param expectedF Excpected Fatal - a Map relating a certain ValidationConstraint to the number
   *     of occurances
   */
  public ErrorHandlerStub(
      Map<ValidationConstraint, Integer> expectedW,
      Map<ValidationConstraint, Integer> expectedE,
      Map<ValidationConstraint, Integer> expectedF) {
    mExpectedWarning = expectedW;
    mExpectedError = expectedE;
    mExpectedFatalError = expectedF;
  }

  public void warning(SAXParseException exception) throws SAXException {
    registerProblem(exception, Objects.requireNonNullElseGet(mExpectedWarning, HashMap::new));
  }

  public void error(SAXParseException exception) throws SAXException {
    registerProblem(exception, Objects.requireNonNullElseGet(mExpectedError, HashMap::new));
  }

  public void fatalError(SAXParseException exception) throws SAXException {
    registerProblem(exception, Objects.requireNonNullElseGet(mExpectedFatalError, HashMap::new));
  }

  private void registerProblem(
      SAXParseException exception, Map<ValidationConstraint, Integer> problemOccurances) {
    ValidationConstraint constraint = ((OdfValidationException) exception).getConstraint();
    problemOccurances.compute(constraint, (key, value) -> {
      LOG.log(Level.INFO, "EXPECTED VALIDATION MESSAGE:\"{0}\"", exception.getMessage());
      return (value == null ? 0 : value) - 1;
    });
  }

  public void validate() {
    if (mExpectedWarning != null) {
      validateProblem(mExpectedWarning, "warning");
    }
    if (mExpectedError != null) {
      validateProblem(mExpectedError, "error");
    }
    if (mExpectedFatalError != null) {
      validateProblem(mExpectedFatalError, "fatalError");
    }
  }

  private void validateProblem(Map<ValidationConstraint, Integer> expectedProblems, String level) {
    Iterator<ValidationConstraint> constraints = expectedProblems.keySet().iterator();
    while (constraints.hasNext()) {
      ValidationConstraint constraint = constraints.next();
      Integer problemOccurance = expectedProblems.get(constraint);
      if (problemOccurance > 0) {
        logMissingConstraint(constraint, level, problemOccurance);
      } else if (problemOccurance < 0) {
        logUnexpectedConstraint(constraint, level, problemOccurance * -1);
      }
    }
  }

  private void logMissingConstraint(
      ValidationConstraint constraint, String errorLevel, int problemOccurance) {
    if (constraint instanceof OdfPackageConstraint) {
      OdfPackageConstraint pkgConstraint = (OdfPackageConstraint) constraint;
      Assert.fail(
          problemOccurance
              + "x time(s) was in "
              + getTestFilePath()
              + " not thrown the ODF 1.2 Package "
              + errorLevel
              + " '"
              + pkgConstraint.name()
              + "'!");
    } else {
      OdfSchemaConstraint schemaConstraint = (OdfSchemaConstraint) constraint;
      Assert.fail(
          problemOccurance
              + "x time(s) was in "
              + getTestFilePath()
              + " not thrown the ODF 1.2 Schema "
              + errorLevel
              + " '"
              + schemaConstraint.name()
              + "'!");
    }
  }

  private void logUnexpectedConstraint(
      ValidationConstraint constraint, String errorLevel, int problemOccurance) {
    if (constraint instanceof OdfPackageConstraint) {
      OdfPackageConstraint pkgConstraint = (OdfPackageConstraint) constraint;
      Assert.fail(
          problemOccurance
              + "x time(s) in "
              + getTestFilePath()
              + " a new ODF 1.2 Package "
              + errorLevel
              + " '"
              + pkgConstraint.name()
              + "' was unexpected thrown!");
    } else {
      OdfSchemaConstraint schemaConstraint = (OdfSchemaConstraint) constraint;
      Assert.fail(
          problemOccurance
              + "x time(s) in "
              + getTestFilePath()
              + " a new ODF 1.2 Schema "
              + errorLevel
              + " '"
              + schemaConstraint.name()
              + "' was unexpected thrown!");
    }
  }

  /** @param testFilePath the path of the test file */
  public void setTestFilePath(String testFilePath) {
    mTestFilePath = testFilePath;
  }

  /** @return the path of the test file */
  public String getTestFilePath() {
    return mTestFilePath;
  }
}
