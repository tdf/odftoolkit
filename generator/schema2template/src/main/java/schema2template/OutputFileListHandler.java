/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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
package schema2template;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * If a template should be used multiple times (e.g. for every element of the schema), For every
 * file we create a line will be created. This class parses this list of the output files to be
 * created.</code>
 */
public class OutputFileListHandler extends DefaultHandler {

  List<OutputFileListEntry> mFilelist;
  boolean mOpenFlTag = false;
  boolean mOpenFileTag = false;
  boolean mOpenPathTag = false;
  Locator mLocator;

  OutputFileListHandler(List<OutputFileListEntry> fl) {
    mFilelist = fl;
  }

  /** With the DocumentLocator line numbers will be received during errors */
  @Override
  public void setDocumentLocator(Locator locator) {
    mLocator = locator;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equals("filelist") && !mOpenFlTag) {
      mOpenFlTag = true;
      return;
    }
    if (qName.equals("file") && mOpenFlTag && !mOpenFileTag && !mOpenPathTag) {
      mOpenFileTag = true;
      OutputFileListEntry entry =
          new OutputFileListEntry(OutputFileListEntry.EntryType.FILE, mLocator.getLineNumber());
      String mandatoryPath = attributes.getValue("path");
      if (mandatoryPath == null)
        throw new SAXException(
            "Mandatory attribute path is missing for file element in line "
                + mLocator.getLineNumber()
                + ".");
      entry.setAttribute("path", mandatoryPath);
      entry.setAttribute("context", attributes.getValue("context"));
      entry.setAttribute("param", attributes.getValue("param"));
      entry.setAttribute("template", attributes.getValue("template"));
      mFilelist.add(entry);
      return;
    }
    if (qName.equals("path") && mOpenFlTag && !mOpenFileTag && !mOpenPathTag) {
      mOpenPathTag = true;
      OutputFileListEntry entry =
          new OutputFileListEntry(OutputFileListEntry.EntryType.PATH, mLocator.getLineNumber());
      String mandatoryPath = attributes.getValue("path");
      if (mandatoryPath == null)
        throw new SAXException(
            "Mandatory attribute path is missing for path element in line "
                + mLocator.getLineNumber()
                + ".");
      entry.setAttribute("path", mandatoryPath);
      entry.setAttribute("path", mandatoryPath);
      mFilelist.add(entry);
      return;
    }
    throw new SAXException("Malformed filelist");
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("filelist") && mOpenFlTag && !mOpenFileTag && !mOpenPathTag) {
      mOpenFlTag = false;
      return;
    }
    if (qName.equals("file") && mOpenFlTag && mOpenFileTag && !mOpenPathTag) {
      mOpenFileTag = false;
      return;
    }
    if (qName.equals("path") && mOpenFlTag && mOpenPathTag && !mOpenFileTag) {
      mOpenPathTag = false;
      return;
    }
    throw new SAXException("Malformed filelist");
  }

  public static List<OutputFileListEntry> readFileListFile(File flf) throws Exception {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    List<OutputFileListEntry> retval = new ArrayList<OutputFileListEntry>();
    parser.parse(flf, new OutputFileListHandler(retval));
    return retval;
  }
}
