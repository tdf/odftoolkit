/** **********************************************************************
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
 *********************************************************************** */
package org.odftoolkit.odfdom.pkg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Warnings and errors of the ODF input document are being registered here without breaking the load process.
 * In general the end user would like to load the full document to access its information.
 *
 * Default implementation of the SAX <code>ErrorHandler</code> interface.
 * Enabled by System property <code>System.setProperty("org.odftoolkit.odfdom.validation", "true")<code>;
 *
 * Unfulfilled recommendations from the specification (e.g. ODF specifications) are warnings.
 * Unfulfilled mandatory requirements from the specifications are warnings.
 * Those errors, which interrupt the program flow, e.g. loading a graphic instead of XML is a fatal error.
 */
public class DefaultErrorHandler implements ErrorHandler {

    private static final Logger LOG = Logger.getLogger(DefaultErrorHandler.class.getName());

    private static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private ArrayList<SAXParseException> mWarnings = null;
    private ArrayList<SAXParseException> mErrors = null;
    private ArrayList<SAXParseException> mFatalErrors = null;
    private final StringBuilder mValidationMessages;

    public DefaultErrorHandler() {
        this.mValidationMessages = new StringBuilder();
    }

    /**
     * Triggers an warning. In case an optional ODF conformance was not
     * satisfied. Default handling is to write into Java log using warning level
     */
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        if (mWarnings == null) {
            mWarnings = new ArrayList<>(1);
        }
        mWarnings.add(exception);
        mValidationMessages.append("\n\nVALIDATION WARNING:\n").append(getStackTrace(exception)).append("\n");
        LOG.log(Level.WARNING, "\n\nVALIDATION WARNING:\n{0}\n", getStackTrace(exception));
    }

    /**
     * Triggers an error. In case a mandatory ODF conformance was not satisfied.
     * Default handling is to write into Java log using severe level
     */
    @Override
    public void error(SAXParseException exception) throws SAXException {
        if (mErrors == null) {
            mErrors = new ArrayList<>(1);
        }
        mErrors.add(exception);
        mValidationMessages.append("\n\nVALIDATION ERROR:\n").append(getStackTrace(exception)).append("\n");
        LOG.log(Level.SEVERE, "\n\nVALIDATION ERROR:\n{0}\n", getStackTrace(exception));
    }

    /**
     * Triggers a fatalError. The ODF document can not be loaded due to an
     * error.	* Default handling is to write into Java log using severe level
     * and to throw a SAXException
     */
    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        if (mFatalErrors == null) {
            mFatalErrors = new ArrayList<>(1);
        }
        mFatalErrors.add(exception);
        mValidationMessages.append("\n\nVALIDATION FATALERROR:\n").append(getStackTrace(exception)).append("\n");
        LOG.log(Level.SEVERE, "\n\nVALIDATION FATALERROR:\n{0}\n", getStackTrace(exception));
        throw exception;
    }

    /**
     * @return all warning SaxParseExceptions, all ODF recommendations not being
     * fulfilled. Might be NULL.
     */
    public List<SAXParseException> getWarnings() {
        return mWarnings;
    }

    /**
     * @return all error SaxParseExceptions, all mandatory ODF requirements not
     * being fulfilled. Might be NULL.
     */
    public List<SAXParseException> getErrors() {
        return mErrors;
    }

    /**
     * @return all fatal-error SaxParseExceptions, ODF errors, which interrupt
     * the program flow, e.g. loading a PDF as ODF.  Might be NULL.
     */
    public List<SAXParseException> getFatalErrors() {
        return mFatalErrors;
    }

    public String getValidationMessages(){
        return mValidationMessages.toString();
    }
}
