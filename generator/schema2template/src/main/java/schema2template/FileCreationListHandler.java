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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * If a Velocity template should be used multiple times (e.g. for every element of the schema), For
 * every file to be created we create a line in the file-creation-list. This file-creation-list XML
 * is being created from the main-template. This class parses this list of the output files to be
 * created.
 */
public class FileCreationListHandler extends DefaultHandler {

  List<FileCreationListEntry> mFilelist;
  boolean mOpenFlTag = false;
  boolean mOpenFileTag = false;
  Locator mLocator;

  FileCreationListHandler(List<FileCreationListEntry> fl) {
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
    if (qName.equals("file-creation-list") && !mOpenFlTag) {
      mOpenFlTag = true;
      return;
    }
    if (qName.equals("file") && mOpenFlTag && !mOpenFileTag) {
      mOpenFileTag = true;
      FileCreationListEntry entry =
          new FileCreationListEntry(FileCreationListEntry.EntryType.FILE, mLocator.getLineNumber());
      String mandatoryPath = attributes.getValue("path");
      if (mandatoryPath == null)
        throw new SAXException(
            "Mandatory attribute path is missing for file element in line "
                + mLocator.getLineNumber()
                + ".");
      entry.setAttribute("path", mandatoryPath);
      entry.setAttribute("contextNode", attributes.getValue("contextNode"));
      entry.setAttribute("param", attributes.getValue("param"));
      entry.setAttribute("template", attributes.getValue("template"));
      mFilelist.add(entry);
      return;
    }
    throw new SAXException("Malformed file-creation-list");
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("file-creation-list") && mOpenFlTag && !mOpenFileTag) {
      mOpenFlTag = false;
      return;
    }
    if (qName.equals("file") && mOpenFlTag && mOpenFileTag) {
      mOpenFileTag = false;
      return;
    }
    throw new SAXException("Malformed file-creation-list");
  }

  public static List<FileCreationListEntry> readFileListFile(File flf)
      throws ParserConfigurationException, SAXException, IOException {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    List<FileCreationListEntry> retval = new ArrayList<>();
    parser.parse(flf, new FileCreationListHandler(retval));
    return retval;
  }
}
