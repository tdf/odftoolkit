/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

import java.io.IOException;
import org.odftoolkit.odfdom.pkg.OdfPackage;

public abstract class ODFRootPackageValidator extends ODFPackageValidator {

    private OdfPackage m_aPkg = null;

    ODFRootPackageValidator(int nLogLevel, int nMode, String aVersion, SAXParseExceptionFilter aFilter, ODFValidatorProvider aValidatorProvider) {
        super(nLogLevel, nMode, aVersion, null, aFilter, aValidatorProvider);
    }

    abstract OdfPackage getPackage() throws Exception;
    
    OdfPackage getPackage(Logger aLogger) {
        if (m_aPkg == null) {
            try {
                m_aPkg = getPackage();
            } catch (IOException e) {
                if (e.getMessage().startsWith("only DEFLATED entries can have EXT descriptor")) {
                    aLogger.logFatalError("The document is encrypted. Validation of encrypted documents is not supported.");
                } else {
                    aLogger.logFatalError(e.getMessage());
                }
            } catch (Exception e) {
                aLogger.logFatalError(e.getMessage());
            }
        }

        return m_aPkg;
    }

    boolean isRootPackage() {
        return true;
    }

    String getStreamName( String aEntry )
    {
        return aEntry;
    }

}
