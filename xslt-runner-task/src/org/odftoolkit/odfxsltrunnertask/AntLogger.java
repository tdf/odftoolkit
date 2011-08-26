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

package org.odftoolkit.odfxsltrunnertask;

import org.odftoolkit.odfxsltrunner.Logger;
import org.apache.tools.ant.Project;

class AntLogger extends Logger 
{
    static final String LINE_PREFIX = ": line ";
    
    private Project m_aProject = null;
    
    /** Creates a new instance of AntLogger */
    AntLogger( Project aProject )     
    {
        m_aProject = aProject;
    }
    
    /**
     * Log a message.
     * 
     * @param aPrefix Message prefix
     * @param aMsg Message text
     * @param aLocation file, row and column number as text (optional)
     * @param nLevel the warning level (one of INFO, WARNING or ERROR)
     */
    protected void logMessage( String aPrefix, String aMsg, String aLocation, int nLevel  )
    {
        int nAntLevel = nLevel == ERROR ? Project.MSG_ERR : (nLevel == WARNING ? Project.MSG_WARN : Project.MSG_INFO);
        
        StringBuffer aOut = new StringBuffer( aLocation != null ? aLocation : getName() );
        aOut.append( ": " );
        aOut.append( aPrefix );
        aOut.append( ':' );
        aOut.append( aMsg );

        m_aProject.log( aOut.toString(), nAntLevel );
    }

    /**
     * Log a message.
     * 
     * @param aPrefix Message prefix
     * @param aMsgWithLocation Message text including location information
     * @param nLevel the warning level (one of INFO, WARNING or ERROR)
     */
    protected void logMessageWithLocation( String aPrefix, String aMsgWithLocation, int nLevel )
    {
        int nAntLevel = nLevel == ERROR ? Project.MSG_ERR : (nLevel == WARNING ? Project.MSG_WARN : Project.MSG_INFO);

        StringBuffer aOut = new StringBuffer( aMsgWithLocation );
        
        // remove a " line" from the line number information
        int nPos = aOut.indexOf( LINE_PREFIX );
        if( nPos != -1 )
        {
            aOut.delete(nPos+1, nPos+LINE_PREFIX.length() );
            nPos = aOut.indexOf( ": ", nPos);
        }
        if( nPos != -1 )
        {
            aOut.insert(nPos+2, ':' );
            aOut.insert(nPos+2, aPrefix );
        }
        else
        {
            aOut.append( " (" );
            aOut.append( aPrefix );
            aOut.append( ')' );
        }

        m_aProject.log( aOut.toString(), nAntLevel );
    }
}
