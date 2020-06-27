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

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class SchemaErrorHandler implements org.xml.sax.ErrorHandler {

  private Logger m_aLogger;
  private SAXParseExceptionFilter m_aFilter;

  /** Creates a new instance of SchemaErrorHandler */
  SchemaErrorHandler(Logger aLogger, SAXParseExceptionFilter aFilter) {
    m_aLogger = aLogger;
    m_aFilter = aFilter;
  }

  public void warning(SAXParseException e) throws SAXException {
    if (!filter(e)) m_aLogger.logWarning(e);
  }

  public void fatalError(SAXParseException e) throws SAXException {
    fatalErrorNoException(e);
  }

  public void error(SAXParseException e) throws SAXException {
    if (!filter(e)) m_aLogger.logError(e);
  }

  public void fatalErrorNoException(SAXParseException e) {
    if (!filter(e)) m_aLogger.logFatalError(e);
  }

  private boolean filter(SAXParseException e) {
    boolean bFiltered = false;
    if (m_aFilter != null) {
      SAXParseException aNewExc = m_aFilter.filterException(e);
      if (aNewExc == null) {
        bFiltered = true;
      } else if (aNewExc != e) {
        m_aLogger.logWarning(aNewExc);
        bFiltered = true;
      }
    }

    return bFiltered;
  }
}
