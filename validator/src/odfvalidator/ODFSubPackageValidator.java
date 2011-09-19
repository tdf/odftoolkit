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

package odfvalidator;

import org.openoffice.odf.pkg.OdfPackage;

public class ODFSubPackageValidator extends ODFPackageValidator {

    private OdfPackage m_aPkg = null;
    private String m_aBaseURI = null;
    private String m_aSubEntryName = null;

    ODFSubPackageValidator( OdfPackage aPkg, String aBaseURI, String aSubEntryName, String aMediaType,
                             int nLogLevel, int nMode, String aVersion,
                             SAXParseExceptionFilter aFilter, String aParentGenerator,
                             ODFValidatorProvider aValidatorProvider ) throws ODFValidatorException
    {
        super( nLogLevel, nMode, aVersion, aMediaType, aFilter, aValidatorProvider );
        m_aPkg = aPkg;
        m_aBaseURI = aBaseURI + "/" + aSubEntryName;
        m_aSubEntryName = aSubEntryName;
        if( aFilter != null )
            aFilter.startPackage(aParentGenerator);  // take build id from main document as default (embedded objects can nevern be newer)
    }

    @Override
    String getLoggerName() {
        return m_aBaseURI;
    }

    boolean isRootPackage()
    {
        return false;
    }
    
    OdfPackage getPackage( Logger aLogger ) 
    {
        return m_aPkg;
    }

    String getStreamName( String aEntry )
    {
        return m_aSubEntryName + aEntry;
    }
    
};
