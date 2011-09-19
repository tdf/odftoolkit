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
    private int m_nErrors;
    private int m_nWarnings;
    private LogLevel m_nLevel;
    private Logger m_aParentLogger;
    
    private static final String INFO_PREFIX = "Info:";
    private static final String WARNING_PREFIX = "Warning:";
    private static final String ERROR_PREFIX = "Error:";
    private static final String FATAL_PREFIX = "Fatal:";
    
    /** Creates a new instance of Logger */
    Logger( String aFileName, String aEntryName, PrintStream aOut , LogLevel nLevel) {
        m_aFileName = aFileName;
        m_aEntryName = aEntryName;
        m_aOut = aOut;
        m_nLevel = nLevel;
        m_nErrors = 0;
        m_nWarnings = 0;
        m_aParentLogger = null;
    }

    /** Creates a new instance of Logger */
    Logger(  String aEntryName, Logger aParentLogger ) {
        m_aFileName = aParentLogger.m_aFileName;
        m_aEntryName = aEntryName;
        m_aOut = aParentLogger.m_aOut;
        m_nLevel = aParentLogger.m_nLevel;
        m_nErrors = 0;
        m_nWarnings = 0;
        m_aParentLogger = aParentLogger;
    }

    public PrintStream getOutputStream()
    {
        return m_aOut;
    }

    public void setOutputStream(PrintStream aOut) {
        m_aOut = aOut;
    }

    boolean hasError()
    {
        return m_nErrors>0;
    }

    boolean hasWarning()
    {
        return m_nWarnings>0;
    }

    int getErrorCount()
    {
        return m_nErrors;
    }

    int getWarningCount()
    {
        return m_nWarnings;
    }

    void logWarning(String aMsg) {
        if( m_nLevel.compareTo(LogLevel.WARNING) >= 0 )
            logMessage( WARNING_PREFIX, aMsg );
        incWarnings();
    }

    void logFatalError(String aMsg) {
        logMessage( FATAL_PREFIX, aMsg );
        incErrors();
    }

    void logError(String aMsg) {
        if( m_nLevel.compareTo(LogLevel.ERROR) >= 0 )
            logMessage( ERROR_PREFIX, aMsg );
        incErrors();
    }

    void logInfo(String aMsg, boolean bForceOutput) {
        if( m_nLevel.compareTo(LogLevel.INFO) >= 0 || bForceOutput )
            logMessage( INFO_PREFIX, aMsg );
    }
    
    void logWarning(SAXParseException e) {
        if( m_nLevel.compareTo(LogLevel.WARNING) >= 0 )
            logMessage( WARNING_PREFIX, e );
        incWarnings();
    }

    void logFatalError(SAXParseException e) {
        logMessage( FATAL_PREFIX, e );
        incErrors();
    }

    void logError(SAXParseException e) {
        if( m_nLevel.compareTo(LogLevel.ERROR) >= 0 )
            logMessage( ERROR_PREFIX, e );
        incErrors();
    }

    void logSummaryInfo()
    {
        logInfo( (hasError() ? getErrorCount() : "no") + " errors, " + (hasWarning() ? getWarningCount() : "no") + " warnings", false );
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

    private void incErrors()
    {
        ++m_nErrors;
        if( m_aParentLogger != null )
            m_aParentLogger.incErrors();
    }

    private void incWarnings()
    {
        ++m_nWarnings;
        if( m_aParentLogger != null )
            m_aParentLogger.incWarnings();
    }

}
