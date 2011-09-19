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

import java.util.ArrayList;
import java.util.List;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.ValidationConstraint;
import org.odftoolkit.odfdom.pkg.OdfPackageConstraint;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class ODFPackageErrorHandler implements ErrorHandler {

    List<SAXParseException> m_Saved = new ArrayList<SAXParseException>();

    public void warning(SAXParseException exception) throws SAXException {
        m_Saved.add(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        m_Saved.add(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
    }

    /// deferred because version is not available when odfdom checks mimetype
    /// @returns true iff there was an error and package is not valid
    boolean validate(Logger manifestLogger, Logger mimeTypeLogger,
                     OdfVersion version)
    {
        boolean bRet = false;
        LOOP:
        for (SAXParseException e : m_Saved) {
            if (e instanceof OdfValidationException) {
                ValidationConstraint constraint =
                    ((OdfValidationException) e).getConstraint();
                if (constraint instanceof OdfPackageConstraint) {
                    switch (((OdfPackageConstraint) constraint)) {
                        case MANIFEST_DOES_NOT_LIST_FILE:
                            switch (version) {
                                case V1_0:
                                case V1_1:
                                    manifestLogger.logWarning(e.getMessage());
                                break;
                                default:
                                    manifestLogger.logError(e.getMessage());
                                    bRet = true;
                                break;
                            }
                            continue LOOP;
                        case MIMETYPE_NOT_FIRST_IN_PACKAGE:
                        case MIMETYPE_NOT_IN_PACKAGE:
                            switch (version) {
                                case V1_0:
                                case V1_1:
                                    mimeTypeLogger.logWarning(e.getMessage());
                                break;
                                default:
                                    mimeTypeLogger.logError(e.getMessage());
                                    bRet = true;
                                break;
                            }
                            continue LOOP;
                        case MANIFEST_NOT_IN_PACKAGE:
                        case MANIFEST_LISTS_NONEXISTENT_FILE:
                            manifestLogger.logError(e.getMessage());
                            bRet = true;
                            continue LOOP;
                        case MIMETYPE_IS_COMPRESSED:
                        case MIMETYPE_HAS_EXTRA_FIELD:
                        case MIMETYPE_DIFFERS_FROM_PACKAGE:
                            mimeTypeLogger.logError(e.getMessage());
                            bRet = true;
                            continue LOOP;
                        case MANIFEST_LISTS_DIRECTORY:
                        case MANIFEST_DOES_NOT_LIST_DIRECTORY:
                            manifestLogger.logWarning(e.getMessage());
                            continue LOOP;
                    }
                } else if (constraint instanceof OdfSchemaConstraint) {
                    switch (((OdfSchemaConstraint) constraint)) {
                        case DOCUMENT_WITHOUT_ODF_MIMETYPE:
                        case PACKAGE_SHALL_CONTAIN_CONTENT_OR_STYLES_XML:
                            manifestLogger.logError(e.getMessage());
                            bRet = true;
                            continue LOOP;
                    }
                }
            }
            manifestLogger.logError(e); // unknown constraint: assume error
        }
        m_Saved.clear();
        return bRet;
    }
}
