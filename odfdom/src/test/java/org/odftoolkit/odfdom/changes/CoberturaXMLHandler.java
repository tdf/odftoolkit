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
package org.odftoolkit.odfdom.changes;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * If a template should be used multiple times (e.g. for every element of the schema), For every
 * file we create a line will be created. This class parses this list of the output files to be
 * created.</code>
 */
public class CoberturaXMLHandler extends DefaultHandler {

  // e.g. within odftoolkit_latest-0.10.1/odfdom/target/test-classes/test-reference/features
  private static final String COBERTURA_XML_FILENAME = "cobertura_bold.xml";

  Coverage mCoverage;
  String mPackageName;
  String mFileName;
  String mClassName;
  String mMethodName;
  String mMethodSignature;
  int mLineNumber;
  int mHits;
  boolean mOpenFileTag = false;
  boolean mOpenPathTag = false;
  Locator mLocator;

  CoberturaXMLHandler(Coverage cov) {
    mCoverage = cov;
  }

  /** With the DocumentLocator line numbers will be received during errors */
  @Override
  public void setDocumentLocator(Locator locator) {
    mLocator = locator;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equals("package")) {
      mPackageName = getAttributeValue(attributes, "name");
      return;
    }
    if (qName.equals("class")) {
      mFileName = getAttributeValue(attributes, "filename");
      mClassName = getAttributeValue(attributes, "name");
      return;
    }
    if (qName.equals("method")) {
      mMethodName = getAttributeValue(attributes, "name");
      mMethodSignature = getAttributeValue(attributes, "signature");

      return;
    }
    if (qName.equals("line")) {
      String lineNumber = getAttributeValue(attributes, "number");
      mLineNumber = Integer.parseInt(lineNumber);
      String hits = getAttributeValue(attributes, "hits");
      mHits = Integer.parseInt(hits);
      if (mHits > 0) {
        if (mMethodName != null) {
          // System.out.println(mClassName + "-L" + mLineNumber + " - "+ mMethodName + "() - Times:"
          // + mHits);
        } else {
          System.out.print(mClassName + "-L" + mLineNumber);
          if (mHits > 1) {
            System.out.print(" - Times:" + mHits);
          }
          System.out.println();
        }
      }
      return;
    }
    /** <conditions> <condition coverage="0%" number="0" type="jump" /> </conditions> */
    /**
     * if (qName.equals("file") && mOpenFlTag && !mOpenFileTag && !mOpenPathTag) { mOpenFileTag =
     * true; Coverage entry = new Coverage(Coverage.EntryType.FILE, mLocator.getLineNumber());
     * String mandatoryPath = attributes.getValue("path"); if (mandatoryPath == null) throw new
     * SAXException( "Mandatory attribute path is missing for file element in line " +
     * mLocator.getLineNumber() + "."); entry.setAttribute("path", mandatoryPath);
     * entry.setAttribute("context", attributes.getValue("context")); entry.setAttribute("param",
     * attributes.getValue("param")); entry.setAttribute("template",
     * attributes.getValue("template")); mCoverage.add(entry); return; } if (qName.equals("path") &&
     * mOpenFlTag && !mOpenFileTag && !mOpenPathTag) { mOpenPathTag = true; ClassCoverage cov = new
     * Coverage(Coverage.EntryType.PATH, mLocator.getLineNumber()); String mandatoryPath =
     * attributes.getValue("path"); if (mandatoryPath == null) throw new SAXException( "Mandatory
     * attribute path is missing for path element in line " + mLocator.getLineNumber() + ".");
     * cov.setAttribute("path", mandatoryPath); cov.setAttribute("path", mandatoryPath);
     * mCoverage.add(entry); return; } throw new SAXException("Malformed filelist");
     */
  }

  private String getAttributeValue(Attributes attributes, String attrName) {
    String attrValue = attributes.getValue(attrName);
    // valid attrValue?
    if (attrValue == null || attrValue.isBlank()) {
      System.err.println(
          "Line"
              + mLocator.getLineNumber()
              + "Column"
              + mLocator.getColumnNumber()
              + ": CoverageXML "
              + attrName
              + " is empty or null:'"
              + attrValue
              + "'!");
    } else {
      // System.out.println("Line" + mLocator.getLineNumber() + "Column" +
      // mLocator.getColumnNumber() + ": " + attrName + ":'" + attrValue + "'!");
    }
    return attrValue;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("package")) {
      mPackageName = null;
      return;
    }
    if (qName.equals("class")) {
      mFileName = null;
      mClassName = null;
      return;
    }
    if (qName.equals("method")) {
      mMethodName = null;
      mMethodSignature = null;
      return;
    }
    /**
     * if (qName.equals("filelist") && mOpenFlTag && !mOpenFileTag && !mOpenPathTag) { mOpenFlTag =
     * false; return; } if (qName.equals("file") && mOpenFlTag && mOpenFileTag && !mOpenPathTag) {
     * mOpenFileTag = false; return; } if (qName.equals("path") && mOpenFlTag && mOpenPathTag &&
     * !mOpenFileTag) { mOpenPathTag = false; return; } throw new SAXException("Malformed
     * filelist");
     */
  }

  public static void main(String[] params) {
    String coberturaFileName = null;
    if (params.length == 0) {
      System.out.println(
          "USAGE:\n\t1st: parameter has to be the path to the Cobertura XML!\n\t2nd: Coverage name");
      coberturaFileName = COBERTURA_XML_FILENAME;
    }
    // e.g. odftoolkit_latest-0.10.1/odfdom/target/test-classes/test-reference/features
    File coberturaXML =
        ResourceUtilities.getTestReferenceFile("features" + File.separator + coberturaFileName);
    try {
      readFileListFile(coberturaXML, "testRun");
    } catch (Exception ex) {
      Logger.getLogger(CoberturaXMLHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static Coverage readFileListFile(File coberturaXML, String coverageName) throws Exception {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    Coverage coverage = new Coverage(coverageName);
    parser.parse(coberturaXML, new CoberturaXMLHandler(coverage));
    return coverage;
  }

  // temporary static and hie
  static class Coverage {
    String mCoverageName;

    public Coverage(String name) {
      mCoverageName = name;
    }
  }
}
