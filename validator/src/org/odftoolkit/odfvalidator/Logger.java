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

import java.io.PrintStream;

import org.xml.sax.SAXParseException;

public class Logger {

    public enum LogLevel
    {
        ERROR,
        WARNING,
        INFO
    };
    
    private String m_aFileName;
    private String m_aEntryName;
    private PrintStream m_aOut;
    private boolean m_bError;
    private LogLevel m_nLevel;
    
    private static final String INFO_PREFIX = "Info:";
    private static final String WARNING_PREFIX = "Warning:";
    private static final String ERROR_PREFIX = "Error:";
    private static final String FATAL_PREFIX = "Fatal:";
    
    /** Creates a new instance of SchemaErrorHandler */
    Logger( String aFileName, String aEntryName, PrintStream aOut , LogLevel nLevel) {
        m_aFileName = aFileName;
        m_aEntryName = aEntryName;
        m_aOut = aOut;
        m_nLevel = nLevel;
        m_bError = false;
    }

    public void setOutputStream(PrintStream aOut) {
        m_aOut = aOut;
    }

    boolean hasError()
    {
        return m_bError;
    }

    void logWarning(String aMsg) {
        if( m_nLevel.compareTo(LogLevel.WARNING) >= 0 )
            logMessage( WARNING_PREFIX, aMsg );
    }

    void logFatalError(String aMsg) {
        logMessage( FATAL_PREFIX, aMsg );
        m_bError = true;
    }

    void logError(String aMsg) {
        if( m_nLevel.compareTo(LogLevel.ERROR) >= 0 )
            logMessage( ERROR_PREFIX, aMsg );
        m_bError = true;
    }

    void logInfo(String aMsg, boolean bForceOutput) {
        if( m_nLevel.compareTo(LogLevel.INFO) >= 0 || bForceOutput )
            logMessage( INFO_PREFIX, aMsg );
    }
    
    void logWarning(SAXParseException e) {
        if( m_nLevel.compareTo(LogLevel.WARNING) >= 0 )
            logMessage( WARNING_PREFIX, e );
    }

    void logFatalError(SAXParseException e) {
        logMessage( FATAL_PREFIX, e );
        m_bError = true;
    }

    void logError(SAXParseException e) {
        if( m_nLevel.compareTo(LogLevel.ERROR) >= 0 )
            logMessage( ERROR_PREFIX, e );
        m_bError = true;
    }
        
    private void printFileEntryPrefix()
    {
        m_aOut.print( m_aFileName );
        if( m_aEntryName != null && m_aEntryName.length() > 0 )
        {
            m_aOut.print( "/" );
            m_aOut.print( m_aEntryName );
        }
    }

    private void logMessage( String aPrefix, SAXParseException e )
    {
        printFileEntryPrefix();
        m_aOut.print( "[" );
        m_aOut.print( e.getLineNumber() );
        m_aOut.print( "," );
        m_aOut.print( e.getColumnNumber() );
        m_aOut.print( "]:" );
        m_aOut.print( aPrefix );
        m_aOut.println( e.getMessage() );
    }
    
    private void logMessage( String aPrefix, String aMsg )
    {
        printFileEntryPrefix();
        m_aOut.print( ":" );
        m_aOut.print( aPrefix );
        m_aOut.println( aMsg);
    }
}
