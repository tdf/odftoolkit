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
package org.odftoolkit.odfdom.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.pkg.OdfPackageConstraint;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.ValidationConstraint;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** A stub for a real SAX ErrorHandler embracing the expected amount of exceptions to be catched */
public class ErrorHandlerStub implements ErrorHandler {

	private static final Logger LOG = Logger.getLogger(ErrorHandlerStub.class.getName());
	/** Map which returns the number of defects for each given ValidationConstraint */
	Map<ValidationConstraint, Integer> mExpectedWarning;
	Map<ValidationConstraint, Integer> mExpectedError;
	Map<ValidationConstraint, Integer> mExpectedFatalError;

	/** @param expectedW Excpected Warnings - a Map relating a certain ValidationConstraint to the number of occurances */
	/** @param expectedE Excpected Errors	- a Map relating a certain ValidationConstraint to the number of occurances */
	/** @param expectedF Excpected Fatal	- a Map relating a certain ValidationConstraint to the number of occurances */
	public ErrorHandlerStub(Map<ValidationConstraint, Integer> expectedW, Map<ValidationConstraint, Integer> expectedE, Map<ValidationConstraint, Integer> expectedF) {
		mExpectedWarning = expectedW;
		mExpectedError = expectedE;
		mExpectedFatalError = expectedF;
	}

	public void warning(SAXParseException exception) throws SAXException {
		if (mExpectedWarning == null) {
			mExpectedWarning = new HashMap<ValidationConstraint, Integer>();
		}
		registerProblem(exception, mExpectedWarning);
	}

	public void error(SAXParseException exception) throws SAXException {
		if (mExpectedError == null) {
			mExpectedError = new HashMap<ValidationConstraint, Integer>();
		}
		registerProblem(exception, mExpectedError);
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		if (mExpectedFatalError == null) {
			mExpectedFatalError = new HashMap<ValidationConstraint, Integer>();
		}
		registerProblem(exception, mExpectedFatalError);
	}

	private void registerProblem(SAXParseException exception, Map<ValidationConstraint, Integer> problemOccurances) {
		ValidationConstraint constraint = ((OdfValidationException) exception).getConstraint();
		Integer problemOccurance = problemOccurances.get(constraint);
		if (problemOccurance == null) {
			problemOccurance = 0;
		}
		LOG.log(Level.INFO, "EXPECTED VALIDATION MESSAGE:\"{0}\"", exception.getMessage());
		problemOccurances.put(constraint, --problemOccurance);
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

	private void logMissingConstraint(ValidationConstraint constraint, String level, int problemOccurance) {
		if (constraint instanceof OdfPackageConstraint) {
			OdfPackageConstraint pkgConstraint = (OdfPackageConstraint) constraint;
			Assert.fail(problemOccurance + "time(s) missing in the document the expected ODF 1.2 Package " + level + " for " + pkgConstraint.name());
		} else {
			OdfSchemaConstraint schemaConstraint = (OdfSchemaConstraint) constraint;
			Assert.fail(problemOccurance + "time(s) missing in the document the expected ODF 1.2 Schema error for " + schemaConstraint.name());
		}
	}

	private void logUnexpectedConstraint(ValidationConstraint constraint, String level, int problemOccurance) {
		if (constraint instanceof OdfPackageConstraint) {
			OdfPackageConstraint pkgConstraint = (OdfPackageConstraint) constraint;
			Assert.fail(problemOccurance + "time(s) unexpected a new ODF 1.2 Package " + level + " for " + pkgConstraint.name());
		} else {
			OdfSchemaConstraint schemaConstraint = (OdfSchemaConstraint) constraint;
			Assert.fail(problemOccurance + "time(s) unexpected a new ODF 1.2 Schema error for " + schemaConstraint.name());
		}
	}
}
