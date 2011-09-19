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

public class ODFValidatorException extends Exception {
    
    private String m_aFile;
    private String m_aEntry;
    
    /** Creates a new instance of ODFValidatorException */
    ODFValidatorException( Exception e) {
        super( e );
    }
    
    ODFValidatorException( String aMsg ) {
        super( aMsg );
    }

    ODFValidatorException( String aFile, String aEntry, String aMsg ) {
        super( aMsg );
        m_aFile = aFile;
        m_aEntry = aEntry;
    }
 
    ODFValidatorException( String aFile, String aEntry, Exception e ) {
        super( e );
        m_aFile = aFile;
        m_aEntry = aEntry;
    }

    @Override
    public String getMessage() {
        String retValue = "";
        
        if( m_aFile != null && m_aFile.length() > 0 )
        {
            retValue += m_aFile;
            retValue += ":";
        }
        if( m_aEntry != null && m_aEntry.length() > 0 )
        {
            retValue += m_aEntry;
            retValue += ":";
        }
        retValue += super.getMessage();
        return retValue;
    }
    
    
}
