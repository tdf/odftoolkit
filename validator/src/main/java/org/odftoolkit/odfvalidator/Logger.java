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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.xml.sax.SAXParseException;

public class Logger {

  public enum LogLevel {
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
  private InputStream m_aEntryContent;
  private static final String INFO_PREFIX = "Info:";
  private static final String WARNING_PREFIX = "Warning:";
  private static final String ERROR_PREFIX = "Error:";
  private static final String FATAL_PREFIX = "Fatal:";
  private static boolean m_isHTMLEnabled;

  /** Creates a new instance of Logger */
  Logger(String aFileName, String aEntryName, PrintStream aOut, LogLevel nLevel) {
    m_aFileName = aFileName;
    m_aEntryName = aEntryName;
    m_aOut = aOut;
    m_nLevel = nLevel;
    m_nErrors = 0;
    m_nWarnings = 0;
    m_aParentLogger = null;
  }

  /** Creates a new instance of Logger */
  Logger(String aEntryName, Logger aParentLogger) {
    m_aFileName = aParentLogger.m_aFileName;
    m_aEntryName = aEntryName;
    m_aOut = aParentLogger.m_aOut;
    m_nLevel = aParentLogger.m_nLevel;
    m_nErrors = 0;
    m_nWarnings = 0;
    m_aParentLogger = aParentLogger;
  }

  static void enableHTML(boolean isHTMLEnabled) {
    m_isHTMLEnabled = isHTMLEnabled;
  }

  public PrintStream getOutputStream() {
    return m_aOut;
  }

  public void setOutputStream(PrintStream aOut) {
    m_aOut = aOut;
  }

  public void setInputStream(InputStream aIn) {
    if (aIn != null && aIn.markSupported()) {
      aIn.mark(Integer.MAX_VALUE);
    }
    m_aEntryContent = aIn;
  }

  boolean hasError() {
    return m_nErrors > 0;
  }

  boolean hasWarning() {
    return m_nWarnings > 0;
  }

  int getErrorCount() {
    return m_nErrors;
  }

  int getWarningCount() {
    return m_nWarnings;
  }

  void logWarning(String aMsg) {
    if (m_nLevel.compareTo(LogLevel.WARNING) >= 0) {
      if (m_isHTMLEnabled) {
        m_aOut.print("<span class='warning'>");
      }
      logMessage(WARNING_PREFIX, aMsg);
    }
    incWarnings();
  }

  void logFatalError(String aMsg) {
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='fatalError'>");
    }
    logMessage(FATAL_PREFIX, aMsg);
    incErrors();
  }

  void logError(String aMsg) {
    if (m_nLevel.compareTo(LogLevel.ERROR) >= 0) {
      if (m_isHTMLEnabled) {
        m_aOut.print("<span class='error'>");
      }
      logMessage(ERROR_PREFIX, aMsg);
    }
    incErrors();
  }

  void logInfo(String aMsg, boolean bForceOutput) {
    if (m_nLevel.compareTo(LogLevel.INFO) >= 0 || bForceOutput) {
      if (m_isHTMLEnabled) {
        m_aOut.print("<span class='info'>");
      }
      logMessage(INFO_PREFIX, aMsg);
    }
  }

  void logWarning(SAXParseException e) {
    if (m_nLevel.compareTo(LogLevel.WARNING) >= 0) {
      if (m_isHTMLEnabled) {
        m_aOut.print("<span class='warning'>");
      }
      logMessage(WARNING_PREFIX, e);
    }
    incWarnings();
  }

  void logFatalError(SAXParseException e) {
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='fatalError'>");
    }
    logMessage(FATAL_PREFIX, e);
    incErrors();
  }

  void logError(SAXParseException e) {
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='error'>");
    }
    if (m_nLevel.compareTo(LogLevel.ERROR) >= 0) {
      logMessage(ERROR_PREFIX, e);
    }
    incErrors();
  }

  void logSummaryInfo() {
    logInfo(
        (hasError() ? getErrorCount() : "no")
            + " errors, "
            + (hasWarning() ? getWarningCount() : "no")
            + " warnings",
        false);
  }

  private void printFileEntryPrefix() {
    m_aOut.print(m_aFileName);
    if (m_aEntryName != null && m_aEntryName.length() > 0) {
      m_aOut.print("/");
      m_aOut.print(m_aEntryName);
    }
  }

  private void logMessage(String aPrefix, SAXParseException e) {
    // filepath
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='filePath'>");
    }
    printFileEntryPrefix();
    m_aOut.print("[");
    m_aOut.print(e.getLineNumber());
    m_aOut.print(",");
    m_aOut.print(e.getColumnNumber());
    m_aOut.print("]:  ");
    if (m_isHTMLEnabled) {
      m_aOut.print("</span>");
    }

    // prefix, e.g. warning
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='messageType'>");
    }
    m_aOut.print(aPrefix);
    if (m_isHTMLEnabled) {
      m_aOut.print("</span>");
    }

    m_aOut.print(" " + e.getMessage());
    if (m_isHTMLEnabled) {
      m_aOut.print("</span></br>");
    }
    m_aOut.println();

    if (m_aEntryContent != null) {
      try {
        m_aEntryContent.reset();
        BufferedReader reader = new BufferedReader(new InputStreamReader(m_aEntryContent));
        for (int l = 0; l < e.getLineNumber() - 1; ++l) {
          reader.readLine();
        }

        String errorLine = reader.readLine();
        int len = errorLine.length();
        if (len > 80) {
          m_aOut.println(
              errorLine.substring(
                  Math.max(0, e.getColumnNumber() - 40),
                  Math.min(len - 1, e.getColumnNumber() + 39)));
          m_aOut.println("".format("%1$38s", "----^"));
        } else {
          m_aOut.println(errorLine);
          m_aOut.println("".format("%1$" + Math.max(0, e.getColumnNumber() - 2) + "s", "----^"));
        }
      } catch (IOException x) {
      }
    }
  }

  private void logMessage(String aPrefix, String aMsg) {
    // filepath
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='filePath'>");
    }
    printFileEntryPrefix();
    m_aOut.print(":  ");
    if (m_isHTMLEnabled) {
      m_aOut.print("</span>");
    }

    // prefix, e.g. warning
    if (m_isHTMLEnabled) {
      m_aOut.print("<span class='messageType'>");
    }
    m_aOut.print(aPrefix);
    if (m_isHTMLEnabled) {
      m_aOut.print("</span>");
    }
    m_aOut.print(" " + aMsg);
    if (m_isHTMLEnabled) {
      m_aOut.print("</span></br>");
    }
    m_aOut.println();
  }

  private void incErrors() {
    ++m_nErrors;
    if (m_aParentLogger != null) {
      m_aParentLogger.incErrors();
    }
  }

  private void incWarnings() {
    ++m_nWarnings;
    if (m_aParentLogger != null) {
      m_aParentLogger.incWarnings();
    }
  }
}
