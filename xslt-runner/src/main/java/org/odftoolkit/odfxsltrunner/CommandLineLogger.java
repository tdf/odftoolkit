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

package org.odftoolkit.odfxsltrunner;

import java.io.PrintStream;

/**
 * Logger for command line interface
 */
public class CommandLineLogger extends Logger
{
    private PrintStream m_aOut;
    private int m_nLevel;

    /** Creates a new instance of CommandLineLogger
     *
     * @param aOut PrintStream for messages
     */
    public CommandLineLogger( PrintStream aOut, int nLevel )
    {
        m_nLevel = nLevel;
        m_aOut = aOut;
    }

    /**
     * Log a message.
     *
     * @param aPrefix Message prefix
     * @param aMsg Message text
     * @param aLocation file, row and column number as text (optional)
     * @param nLevel the warning level (one of INFO, WARNING or ERROR)
     */
    protected void logMessage( String aPrefix, String aMsg, String aLocation, int nLevel )
    {
        if( nLevel <= m_nLevel )
        {
            StringBuffer aOut = new StringBuffer( aLocation != null ? aLocation : getName() );
            aOut.append( ": " );
            aOut.append( aPrefix );
            aOut.append( ':' );
            aOut.append( aMsg );
            m_aOut.println( aOut.toString() );
        }
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
        StringBuffer aOut = new StringBuffer( aMsgWithLocation );
        aOut.append( " (" );
        aOut.append( aPrefix );
        aOut.append( ')' );
        m_aOut.println( aOut.toString() );
    }

}
