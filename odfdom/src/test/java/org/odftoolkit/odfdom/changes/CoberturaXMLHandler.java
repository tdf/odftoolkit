/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
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
 * <p>*********************************************************************
 */
package org.odftoolkit.odfdom.changes;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * If a template should be used multiple times (e.g. for every element of the schema), For every
 * file we create a line will be created. This class parses this list of the output files to be
 * created.</code>
 */
public class CoberturaXMLHandler extends DefaultHandler {

  public CoberturaXMLHandler(Coverage coverage) {
    mCoverage = coverage;
  }

  Coverage mCoverage = null;

  // e.g. within odftoolkit/odfdom/target/test-classes/test-input/feature/coverage
  private static final String COBERTURA_XML_FILENAME_A = "cobertura_bold__indent.cov";
  private static final String COBERTURA_XML_FILENAME_B = "cobertura_text_italic.cov";
  static File mStrippedCoberturaFile = null;
  XMLStreamWriter mXsw = null;
  // keeping all information from start elements
  // until it is certain the XML should be written
  Deque<ElementInfo> mStartElementStack = new ArrayDeque<ElementInfo>();
  boolean mIsCoveredCondition = false;
  String mClassName = null;
  Locator mLocator;

  /** With the DocumentLocator line numbers will be received during errors */
  @Override
  public void setDocumentLocator(Locator locator) {
    mLocator = locator;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    if (qName.equals("line")
        || qName.equals("lines")
        || qName.equals("method")
        || qName.equals("methods")
        || qName.equals("class")
        || qName.equals("classes")
        || qName.equals("package")
        || qName.equals("packages")) {
      mStartElementStack.push(new ElementInfo(uri, localName, qName, attributes));
      if (qName.equals("line")) {
        String hits = getAttributeValue(attributes, "hits");
        int hitCount = Integer.parseInt(hits);
        if (hitCount > 0) {
          String conditionCoverage = attributes.getValue("condition-coverage");
          if (conditionCoverage != null && !conditionCoverage.isBlank()) {
            mIsCoveredCondition = true;
          }
          flushStartElements();
        }
      } else if (qName.equals("class")) {
        mClassName = getAttributeValue(attributes, "name");
      }
    } else if (qName.equals("condition") || qName.equals("conditions")) {
      if (mIsCoveredCondition) {
        writeStartElement(uri, localName, qName, attributes);
      }
    } else {
      // System.out.println("WRITING BASIC START:" + qName);
      writeStartElement(uri, localName, qName, attributes);
    }
  }

  private String getAttributeValue(Attributes attributes, String attrName) {
    String attrValue = attributes.getValue(attrName);
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
      // } else {
      // System.out.println("Line" + mLocator.getLineNumber() + "Column" +
      // mLocator.getColumnNumber() + ": " + attrName + ":'" + attrValue + "'!");
    }
    return attrValue;
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("line")
        || qName.equals("lines")
        || qName.equals("method")
        || qName.equals("methods")
        || qName.equals("class")
        || qName.equals("classes")
        || qName.equals("package")
        || qName.equals("packages")) {
      if (mStartElementStack.pop().isStartElementWritten) {
        writeEndElement();
      }
      if (qName.equals("class")) {
        mClassName = null;
      }
    } else if (qName.equals("condition") || qName.equals("conditions")) {
      if (mIsCoveredCondition) {
        writeEndElement();
        if (qName.equals("conditions")) {
          mIsCoveredCondition = false;
        }
      }
    } else { // any other element will be written out
      // assumed not being in the descendant line of "line"
      // System.out.println("WRITING BASIC END:" + qName);
      writeEndElement();
    }
  }

  /** As soon a line with coverage was founda all ancestor start elements will be written out */
  private void flushStartElements() {
    Iterator<ElementInfo> it = mStartElementStack.descendingIterator();
    while (it.hasNext()) {
      ElementInfo elementInfo = it.next();
      if (!elementInfo.isStartElementWritten) {
        writeStartElement(
            elementInfo.uri, elementInfo.localName, elementInfo.qName, elementInfo.attributes);
        // System.out.println("Writing StartElement with qName:" + elementInfo.qName);
        elementInfo.isStartElementWritten = true;
      }
    }
  }

  private void writeStartElement(
      String uri, String localName, String qName, Attributes attributes) {
    if (qName == null || qName.isBlank()) {
      System.err.println("ERROR: Non existent qname: " + qName);
    }
    try {
      mXsw.writeStartElement(qName);
      for (int i = 0; i < attributes.getLength(); i++) {
        mXsw.writeAttribute(attributes.getQName(i), attributes.getValue(i));
      }
    } catch (XMLStreamException ex) {
      Logger.getLogger(CoberturaXMLHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void writeEndElement() {
    try {
      mXsw.writeEndElement();
    } catch (XMLStreamException ex) {
      Logger.getLogger(CoberturaXMLHandler.class.getName()).log(Level.SEVERE, null, ex);
      try {
        if (mXsw != null) {
          mXsw.close();
          mXsw = null;
        }
      } catch (Exception e) {
        System.err.println("Unable to close the file: " + e.getMessage());
      }
    }
  }

  public void startDocument() {
    XMLOutputFactory xof = XMLOutputFactory.newInstance();
    try {
      // make sure the output directories are being created
      mStrippedCoberturaFile.getParentFile().mkdirs();
      mXsw = xof.createXMLStreamWriter(new FileWriter(mStrippedCoberturaFile.getAbsolutePath()));
      mXsw.writeStartDocument();
    } catch (Exception e) {
      System.err.println("Unable to write the file: " + e.getMessage());
    }
  }

  public void endDocument() {
    try {
      mXsw.writeEndDocument();
      mXsw.flush();
    } catch (Exception e) {
      System.err.println("Unable to write the file: " + e.getMessage());
    } finally {
      try {
        if (mXsw != null) {
          mXsw.close();
          mXsw = null;
        }
      } catch (Exception e) {
        System.err.println("Unable to close the file: " + e.getMessage());
      }
    }
  }

  public static void main(String[] params) {
    String coberturaFileName = null;
    if (params.length == 0) {
      System.err.println(
          "USAGE:\n"
              + "\t1st PARAMETER (mandatory)\n"
              + "\t   Name of the Cobertura Coverage XML file from directory:\n"
              + "\t   odfdom/target/test-classes/test-input/feature/coverage/\n\n"
              + "\t2nd PARAMETER (optional)\n"
              + "\t   Name of a Cobertura Coverage XML file (as above)\n"
              + "\t   the coverage of the second will be substraced from the first.\n\n"
              + "\tOUTPUT:\n"
              + "\t   Output coverage file reduced to hit lines only and in case of second file showing only the coverage of the feature difference.\n"
              + "\t   The output file's trunc name ends with '--feature' and is saved to directory:\n"
              + "\t   odfdom/target/test-classes/test-output/feature!\n\n");

      // "t2nd (optional) parameter: Path to Cobertura Coverage XML which will be substracted from
      // the first");
      coberturaFileName = COBERTURA_XML_FILENAME_A;
    }
    // e.g. odftoolkit/odfdom/target/test-classes/test-reference/features
    File coberturaXMLFile =
        ResourceUtilities.getTestInputFile(
            "feature" + File.separator + "coverage" + File.separator + coberturaFileName);

    String strippedCoberturaFileName = null;
    if (coberturaFileName.contains(".")) {
      String suffix = coberturaFileName.substring(coberturaFileName.lastIndexOf('.'));
      strippedCoberturaFileName = coberturaFileName.replace(suffix, "--feature" + suffix);
    } else {
      strippedCoberturaFileName = coberturaFileName.concat("--feature.xml");
    }

    mStrippedCoberturaFile =
        ResourceUtilities.getTestOutputFile("feature" + File.separator + strippedCoberturaFileName);

    try {
      readFileListFile(coberturaXMLFile, coberturaFileName);
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

    public String mCoverageName;
    public Map<String, List<Integer>> mClassCoverages;

    public Coverage(String name) {
      mCoverageName = name;
      mClassCoverages = new HashMap<String, List<Integer>>();
    }

    /** @return an ordered list of line numbers */
    public List getClassCoverage(String className) {
      return mClassCoverages.get(className);
    }

    /** @return an empty list of line numbers */
    public List<Integer> newClassCoverage(String className) {
      List<Integer> lineList = new LinkedList<>();
      mClassCoverages.put(className, lineList);
      return lineList;
    }
  }

  // all relevent information of an XML element (by startElement SAX event)
  static class ElementInfo {

    public String uri;
    public String localName;
    public String qName;
    public Attributes attributes;
    // if there is another subelement e.g. <method>
    // the already writen prarent <methods>
    // will not written out again
    public boolean isStartElementWritten = false;

    public ElementInfo(String uri, String localName, String qName, Attributes attributes) {
      this.uri = uri;
      this.localName = localName;
      this.qName = qName;
      // Attributes2Impl will create a real copy, otherwise
      // all attributes are the same from the last element
      this.attributes = new Attributes2Impl(attributes);
    }
  }
}
