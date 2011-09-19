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

import java.io.InputStream;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.xml.sax.ErrorHandler;


/**
 * Validator for Streams
 */
public class ODFStreamValidator extends ODFRootPackageValidator {

    private InputStream m_aInputStream = null;
    private String m_aBaseURI = null;

    ODFStreamValidator(InputStream aInputStream,
                              String aBaseURI,
                              Logger.LogLevel nLogLevel,
                              OdfValidatorMode eMode,
                              OdfVersion aVersion,
                              SAXParseExceptionFilter aFilter,
                              ODFValidatorProvider aValidatorProvider ) 
        
        throws ODFValidatorException {
        super(nLogLevel, eMode, aVersion, aFilter, aValidatorProvider );
        
        m_aInputStream=aInputStream;
        m_aBaseURI=aBaseURI;
    }

    OdfPackage getPackage(ErrorHandler handler) throws Exception
    {
        return OdfPackage.loadPackage(m_aInputStream, m_aBaseURI, handler);
    }
    
    String getLoggerName() {
        return m_aBaseURI;
    }

    @Override
    String getDocumentPath()
    {
        return ""; // this is the root document
    }

};
