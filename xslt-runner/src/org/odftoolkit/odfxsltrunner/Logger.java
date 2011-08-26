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

package org.odftoolkit.odfxsltrunner;

import javax.xml.transform.TransformerException;
import org.xml.sax.SAXParseException;

/**
 * Base class for logging messages.
 */
public abstract class Logger {
    private static final String ERROR_PREFIX = "Error";
    private static final String FATAL_PREFIX = "Fatal";
    private static final String INFO_PREFIX = "Info";
    private static final String WARNING_PREFIX = "Warning";
    private String m_aEntryName;
    private String m_aFileName = "(none)";
    private boolean m_bError = false;

    /** 
     * Error log level. 
     */
    public static final int ERROR = 0;

    /** 
     * Warning log level. 
     */
    public static final int WARNING = 1;
    
    /** 
     * Information log level. 
     */
    public static final int INFO = 2;

    /**
     * Create a new Logger instance.
     */
    protected Logger() 
    {
    }

    void setName( String aFileName )
    {
        setName( aFileName, null );
    }
    
    void setName( String aFileName, String aEntryName )
    {
        m_aFileName = aFileName;
        m_aEntryName = aEntryName;
    }

    /**
     * Get name of the file for which messagea are logged.
     * 
     * @return file name
     */
    protected String getName() {
        StringBuffer aName = new StringBuffer(m_aFileName);
        if (m_aEntryName != null && m_aEntryName.length() > 0) {
            aName.append('/');
            aName.append(m_aEntryName);
        }

        return aName.toString();
    }

    /** 
     * Was an error or fatal error logged.
     * 
     * @return true if an error was logged.
     */
    public boolean hasError() {
        return m_bError;
    }

    void logError(String aMsg) {
        logMessage(ERROR_PREFIX, aMsg, ERROR );
        m_bError = true;
    }

    void logError(TransformerException e) {
        logMessage(ERROR_PREFIX, e, ERROR );
        m_bError = true;
    }

    void logError(SAXParseException e) {
        logMessage(ERROR_PREFIX, e, ERROR );
        m_bError = true;
    }


    void logFatalError(String aMsg) {
        logMessage(FATAL_PREFIX, aMsg, ERROR );
        m_bError = true;
    }

    void logFatalError(TransformerException e) {
        logMessage(FATAL_PREFIX, e, ERROR);
        m_bError = true;
    }

    void logFatalError(SAXParseException e) {
        logMessage(FATAL_PREFIX, e, ERROR);
        m_bError = true;
    }

    void logInfo(String aMsg) {
        logMessage(INFO_PREFIX, aMsg, INFO );
    }

    void logWarning(String aMsg) {
        logMessage(WARNING_PREFIX, aMsg, WARNING );
    }

    void logWarning(TransformerException e) {
        logMessage(WARNING_PREFIX, e, WARNING );
    }

    void logWarning(SAXParseException e) {
        logMessage(WARNING_PREFIX, e, WARNING );
    }


    private void logMessage( String aPrefix, TransformerException e, int nLevel )
    {
        logMessageWithLocation( aPrefix, e.getMessageAndLocation(), nLevel );
    }

    private void logMessage( String aPrefix, SAXParseException e, int nLevel )
    {
        StringBuffer aLocation = new StringBuffer( e.getSystemId() );
        aLocation.append(':');
        aLocation.append(e.getLineNumber());
        aLocation.append(": ");
        aLocation.append(e.getColumnNumber());
        
        logMessage( aPrefix, e.getMessage(), aLocation.toString(), nLevel );
    }

    private void logMessage( String aPrefix, String aMsg, int nLevel )
    {
        logMessage( aPrefix, aMsg, null, nLevel );
    }
    
    /**
     * Log a message.
     * 
     * @param aPrefix Message prefix
     * @param aMsg Message text
     * @param aLocation file, row and column number as text (optional)
     * @param nLevel the warning level (one of INFO, WARNING or ERROR)
     */
    protected abstract void logMessage( String aPrefix, String aMsg, String aLocation, int nLevel );
    
    /**
     * Log a message.
     * 
     * @param aPrefix Message prefix
     * @param aMsgWithLocation Message text including location information
     * @param nLevel the warning level (one of INFO, WARNING or ERROR)
     */
    protected abstract void logMessageWithLocation( String aPrefix, String aMsgWithLocation, int nLevel );

}
