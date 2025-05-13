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

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/** */
public class ValidationOOoTaskIdErrorFilter implements SAXParseExceptionFilter {

  private static final String FILTER_ENTRY = "filter-entry";
  private static final String TASK_ID = "task-id";
  private static final String RESOLVED_IN = "resolved-in";

  int m_nBuildId = 0;

  class FilterEntry {
    String m_aTaskId;
    int m_nBuildId;

    FilterEntry(String aTaskId, int nBuildId) {
      m_aTaskId = aTaskId;
      m_nBuildId = nBuildId;
    }
  }

  private HashMap<String, FilterEntry> m_aFilterEntries;
  private HashSet<String> m_aTaskIdsReported;

  class Handler extends DefaultHandler {
    class Entry {
      String m_aMessage = "";
      String m_aTask = "";
      int m_nBuildId = 0;

      Entry(String aTask) {
        m_aTask = aTask;
      }
    }

    HashMap<String, FilterEntry> m_aFilterEntries;
    Entry m_aEntry = null;

    Handler(HashMap<String, FilterEntry> aFilterEntries) {
      m_aFilterEntries = aFilterEntries;
    }

    @Override
    public void characters(char[] aCh, int nStart, int nLength) throws SAXException {
      if (m_aEntry != null) m_aEntry.m_aMessage += new String(aCh, nStart, nLength);
    }

    @Override
    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAttributes)
        throws SAXException {
      if (aQName.equals(FILTER_ENTRY)) {
        String aTaskId = aAttributes.getValue(TASK_ID);
        if (aTaskId != null) {
          m_aEntry = new Entry(aTaskId);
          String aBuildId = aAttributes.getValue(RESOLVED_IN);
          if (aBuildId != null && aBuildId.length() > 0)
            m_aEntry.m_nBuildId = Integer.parseInt(aBuildId);
        }
      }
    }

    @Override
    public void endElement(String aUri, String aLocalName, String aQName) throws SAXException {
      if (aQName.equals(FILTER_ENTRY)) {
        if (m_aEntry != null) {
          m_aFilterEntries.put(
              m_aEntry.m_aMessage, new FilterEntry(m_aEntry.m_aTask, m_aEntry.m_nBuildId));
        }
      }
    }
  }

  /** Creates a new instance of ValidationErrorFilter */
  public ValidationOOoTaskIdErrorFilter(File aFilterFile, PrintStream aOut)
      throws ODFValidatorException {
    m_aFilterEntries = new HashMap<String, FilterEntry>();
    m_aTaskIdsReported = new HashSet<String>();
    SAXParser aParser = null;
    Logger aLogger = new Logger(aFilterFile.getAbsolutePath(), "", aOut, Logger.LogLevel.ERROR);
    try {
      SAXParserFactory aParserFactory = SAXParserFactory.newInstance();
      aParserFactory.setNamespaceAware(false);
      aParser = aParserFactory.newSAXParser();

      aParser.parse(aFilterFile, new Handler(m_aFilterEntries));
    } catch (javax.xml.parsers.ParserConfigurationException e) {
      throw new ODFValidatorException(e);
    } catch (org.xml.sax.SAXParseException e) {
      aLogger.logError(e);
      throw new ODFValidatorException(e);
    } catch (org.xml.sax.SAXException e) {
      aLogger.logError(e.getMessage());
      throw new ODFValidatorException(e);
    } catch (java.io.IOException e) {
      throw new ODFValidatorException(e);
    }
  }

  public SAXParseException filterException(SAXParseException aExc) {
    if (m_nBuildId > 0) {
      FilterEntry aEntry = m_aFilterEntries.get(aExc.getMessage());
      if (aEntry != null && (aEntry.m_nBuildId == 0 || m_nBuildId <= aEntry.m_nBuildId)) {
        String aTaskId = aEntry.m_aTaskId;
        if (!m_aTaskIdsReported.contains(aTaskId)) {
          m_aTaskIdsReported.add(aTaskId);
          return new SAXParseException(
              "Issue " + aTaskId + " found.",
              aExc.getPublicId(),
              aExc.getSystemId(),
              aExc.getLineNumber(),
              aExc.getColumnNumber(),
              aExc);
        } else return null;
      }
    }

    return aExc;
  }

  public void startPackage(String aGenerator) {
    m_nBuildId = aGenerator.length() > 0 ? getBuildId(aGenerator) : 0;
  }

  public void startSubFile() {
    m_aTaskIdsReported = new HashSet<String>();
    // the build id is kept
  }

  public void setGenerator(String aGenerator) {
    m_nBuildId = aGenerator.length() > 0 ? getBuildId(aGenerator) : 0;
  }

  public static int getBuildId(String aGenerator) {
    int nBuildId = 0;
    int nPos = aGenerator.indexOf("OpenOffice.org_project/");
    if (nPos > 0) {
      nPos = aGenerator.indexOf("Build-", nPos);
      if (nPos > 0) {
        int nStart = nPos + 6;
        nPos = aGenerator.indexOf("$", nStart);
        nBuildId =
            Integer.parseInt(
                nPos > 0 ? aGenerator.substring(nStart, nPos) : aGenerator.substring(nStart));
      }
    }

    return nBuildId;
  }
}
