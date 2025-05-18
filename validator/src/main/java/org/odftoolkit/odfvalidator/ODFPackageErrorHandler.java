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

import java.util.ArrayList;
import java.util.List;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.pkg.OdfPackageConstraint;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.ValidationConstraint;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class ODFPackageErrorHandler implements ErrorHandler {

  List<SAXParseException> m_aExceptionList = new ArrayList<>();

  ODFPackageErrorHandler() {}

  public void warning(SAXParseException exception) throws SAXException {
    m_aExceptionList.add(exception);
  }

  public void error(SAXParseException exception) throws SAXException {
    m_aExceptionList.add(exception);
  }

  public void fatalError(SAXParseException exception) throws SAXException {
    m_aExceptionList.add(exception);
    throw exception;
  }

  /// deferred because aVersion is not available when odfdom checks mimetype
  /// @returns true iff there was an error and package is not valid
  boolean processErrors(
      Logger aPkgLogger, Logger aManifestLogger, Logger aMimetypeLogger, OdfVersion aVersion) {
    boolean bRet = false;
    boolean bPackage = false;
    for (SAXParseException e : m_aExceptionList) {
      if (e instanceof OdfValidationException) {
        ValidationConstraint aConstraint = ((OdfValidationException) e).getConstraint();
        if (aConstraint instanceof OdfPackageConstraint) {
          bPackage = true;
          switch (((OdfPackageConstraint) aConstraint)) {
            case MANIFEST_DOES_NOT_LIST_FILE:
              switch (aVersion) {
                case V1_0:
                case V1_1:
                  aManifestLogger.logWarning(e.getMessage());
                  break;
                default:
                  aManifestLogger.logError(e.getMessage());
                  bRet = true;
                  break;
              }
              break;
            case MIMETYPE_NOT_FIRST_IN_PACKAGE:
            case MIMETYPE_NOT_IN_PACKAGE:
              switch (aVersion) {
                case V1_0:
                case V1_1:
                  aMimetypeLogger.logWarning(e.getMessage());
                  break;
                default:
                  aMimetypeLogger.logError(e.getMessage());
                  bRet = true;
                  break;
              }
              break;
            case MANIFEST_NOT_IN_PACKAGE:
            case MANIFEST_LISTS_NONEXISTENT_FILE:
              aManifestLogger.logError(e.getMessage());
              bRet = true;
              break;
            case MIMETYPE_IS_COMPRESSED:
            case MIMETYPE_HAS_EXTRA_FIELD:
            case MIMETYPE_DIFFERS_FROM_PACKAGE:
              aMimetypeLogger.logError(e.getMessage());
              bRet = true;
              break;
            case MANIFEST_LISTS_DIRECTORY:
            case MANIFEST_DOES_NOT_LIST_DIRECTORY:
              aManifestLogger.logWarning(e.getMessage());
              break;
            default:
              aPkgLogger.logError(e); // unknown aConstraint: assume error
              bRet = true;
              break;
          }
        } else if (aConstraint instanceof OdfSchemaConstraint) {
          switch (((OdfSchemaConstraint) aConstraint)) {
            case DOCUMENT_WITHOUT_ODF_MIMETYPE:
            case DOCUMENT_WITHOUT_CONTENT_NOR_STYLES_XML:
              aPkgLogger.logError(e.getMessage());
              bRet = true;
              break;
            default:
              aPkgLogger.logError(e); // unknown aConstraint: assume error
              bRet = true;
              break;
          }
        }
      } else {
        aPkgLogger.logError(e); // unknown aConstraint: assume error
        bRet = true;
      }
    }

    if (bPackage) {
      String msg = "For more information on ODF package conformance see ";
      switch (aVersion) {
        case V1_0:
          // there isn't even an HTML version
          msg += "https://docs.oasis-open.org/office/v1.0/OpenDocument-v1.0-os.pdf";
          break;
        case V1_1:
          // this was actually quite useless before 1.2
          msg += "https://docs.oasis-open.org/office/v1.1/OpenDocument-v1.1.html#Ref_Package";
          break;
        case V1_2:
          msg +=
              "https://docs.oasis-open.org/office/v1.2/OpenDocument-v1.2-part3.html#a2_2Packages";
          break;
        case V1_3:
          msg +=
              "https://docs.oasis-open.org/office/OpenDocument/v1.3/os/part2-packages/OpenDocument-v1.3-os-part2-packages.html#a_2_2_Package_Conformance";
          break;
        case V1_4:
          msg +=
              "https://docs.oasis-open.org/office/OpenDocument/v1.4/OpenDocument-v1.4-part2-packages.html#a_2_2_Package_Conformance";
          break;
      }
      if (bRet) {
        aMimetypeLogger.logError(msg);
      } else {
        aMimetypeLogger.logWarning(msg);
      }
    }

    m_aExceptionList.clear();
    return bRet;
  }
}
