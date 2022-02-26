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
 * <p>**************************************************************
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
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Attributes2Impl;
import org.xml.sax.helpers.DefaultHandler;

/**
 * If a template should be used multiple times (e.g. for every element of the schema), For every
 * file we create a line will be created. This class parses this list of the output files to be
 * created.
 */
public class CoberturaXMLHandler extends DefaultHandler {

  public CoberturaXMLHandler(Coverage coverageBuilding) {
    mCov = coverageBuilding;
  }

  public CoberturaXMLHandler(Coverage coverage2Build, Coverage coverage4Compare) {
    mCov = coverage2Build;
    mCov4Compare = coverage4Compare;
  }

  Coverage mCov = null;
  Coverage mCov4Compare = null;

  // e.g. within odftoolkit/odfdom/target/test-classes/test-input/feature/coverage
  private static final String COBERTURA_XML_FILENAME__BASE = "cobertura_bold__indent.cov";
  private static final String COBERTURA_XML_FILENAME__MINUS = "cobertura_text_italic.cov";
  XMLStreamWriter mXsw = null;
  // keeping all information from start elements
  // until it is certain the XML should be written
  Deque<ElementInfo> mStartElementStack = new ArrayDeque<ElementInfo>();
  boolean mIsCoveredCondition = false;

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
      if (qName.equals("class")) {
        String className = getAttributeValue(attributes, "name");
        if (className != null && !className.isBlank()) {
          try {
            // sets all dependant state changes
            mCov.newClassCoverage(className);
            if (mCov4Compare != null) {
              mCov4Compare.updateClassName(className);
              mCov4Compare.updateLineAndHitNo();
            }
          } catch (Exception ex) {
            Logger.getLogger(CoberturaXMLHandler.class.getName())
                .log(
                    Level.SEVERE,
                    "The input cobertura file has unexpected split class coverage!",
                    ex);
          }
        }
      } else if (qName.equals("line")) {
        String hits = getAttributeValue(attributes, "hits");
        int hitCount = Integer.parseInt(hits);
        if (hitCount > 0) {
          String conditionCoverage = attributes.getValue("condition-coverage");
          if (conditionCoverage != null && !conditionCoverage.isBlank()) {
            mIsCoveredCondition = true;
          }
          flushStartElements();
          String number = getAttributeValue(attributes, "number");
          int lineNo = Integer.parseInt(number);
          // adding the new line coverage
          mCov.addLineCoverage(lineNo, hitCount, mLocator);
          if (mCov4Compare != null) {
            // lineNo == 0 states there is not a single hit in this class
            if (mCov4Compare.mLineNo != 0 && mCov4Compare.mLineNo == lineNo) {
              if (hitCount == mCov4Compare.mHitCount) {
                // not a single feature! Nothing is written
              } else if (hitCount > mCov4Compare.mHitCount) {
                // only a feature of the current parsed file mCov
                // 2DO:Svante
              } else {
                // only a feature of the previous parsed file mCov4Compare
                // 2DO:Svante
              }
              mCov4Compare.updateLineAndHitNo();
            }
          }
        }
      }
    } else if (qName.equals("condition") || qName.equals("conditions")) {
      if (mIsCoveredCondition) {
        writeStartElement(uri, localName, qName, attributes);
      }
    } else {
      // System.out.println("WRITING BASIC START:" + qName);
      if (qName.equals("coverage")) {
        writeStartElementWithoutAttribute(uri, localName, qName, attributes, "timestamp");
      } else {
        writeStartElement(uri, localName, qName, attributes);
      }
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
        mCov.updateClassName(null);
        if (mCov4Compare != null) {
          mCov4Compare.updateClassName(null);
        }
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

  private void writeStartElementWithoutAttribute(
      String uri,
      String localName,
      String qName,
      Attributes attributes,
      String qNameOfattributeToOmit) {
    if (qName == null || qName.isBlank()) {
      System.err.println("ERROR: Non existent qname: " + qName);
    }
    try {
      mXsw.writeStartElement(qName);
      for (int i = 0; i < attributes.getLength(); i++) {
        if (!attributes.getQName(i).equals(qNameOfattributeToOmit)) {
          mXsw.writeAttribute(attributes.getQName(i), attributes.getValue(i));
        }
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
      mCov.mOutputCoberturaXmlFile.getParentFile().mkdirs();
      mXsw =
          xof.createXMLStreamWriter(new FileWriter(mCov.mOutputCoberturaXmlFile.getAbsolutePath()));
      if (mCov4Compare != null) {
        // initialize the two feature output streams
        //        // make sure the output directories are being created
        //        mCov4Compare.mOutputCoberturaXmlFile.getParentFile().mkdirs();
        //        mXsw =
        //            xof.createXMLStreamWriter(
        //                new FileWriter(mCov4Compare.mOutputCoberturaXmlFile.getAbsolutePath()));
      }
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
    String coberturaInputFileName_FeatureA = null;
    String coberturaInputFileName_FeatureB = null;
    if (params.length == 0 || params.length > 2) {
      printErrorManual();
      // 2DO Remove the this.. :-)
      coberturaInputFileName_FeatureA = COBERTURA_XML_FILENAME__BASE;
      coberturaInputFileName_FeatureB = COBERTURA_XML_FILENAME__MINUS;
    } else if (params.length == 1) {
      coberturaInputFileName_FeatureA = params[0];
      if (coberturaInputFileName_FeatureA == null || coberturaInputFileName_FeatureA.isBlank()) {
        printErrorManual();
        coberturaInputFileName_FeatureA = COBERTURA_XML_FILENAME__BASE;
      }
    } else if (params.length == 2) {
      coberturaInputFileName_FeatureA = params[0];
      coberturaInputFileName_FeatureB = params[1];
      if (coberturaInputFileName_FeatureA == null
          || coberturaInputFileName_FeatureA.isBlank()
          || coberturaInputFileName_FeatureB == null
          || coberturaInputFileName_FeatureB.isBlank()) {
        printErrorManual();
        coberturaInputFileName_FeatureA = COBERTURA_XML_FILENAME__BASE;
        coberturaInputFileName_FeatureB = COBERTURA_XML_FILENAME__MINUS;
      }
    }
    run(coberturaInputFileName_FeatureA, coberturaInputFileName_FeatureB);
  }

  private static void run(
      String coberturaInputFileName_FeatureA, String coberturaInputFileName_FeatureB) {
    try {
      // e.g. odftoolkit/odfdom/target/test-classes/test-reference/features
      File inputCoberturaXML_FeatureA = getCoberturaXMLInputFile(coberturaInputFileName_FeatureA);
      File outputCoberturaXML_FeatureA = getCoberturaXMLOutputFile(coberturaInputFileName_FeatureA);
      Coverage coverage_FeatureA =
          readCoberturaFile(
              inputCoberturaXML_FeatureA,
              coberturaInputFileName_FeatureA,
              outputCoberturaXML_FeatureA);

      if (coberturaInputFileName_FeatureB != null) {
        File inputCoberturaXML_FeatureB = getCoberturaXMLInputFile(coberturaInputFileName_FeatureB);
        File outputCoberturaXML_FeatureB =
            getCoberturaXMLOutputFile(coberturaInputFileName_FeatureB);
        Coverage coverage_FeatureB =
            diffCoberturaFiles(
                coverage_FeatureA,
                inputCoberturaXML_FeatureB,
                coberturaInputFileName_FeatureB,
                outputCoberturaXML_FeatureB);
      }
    } catch (Exception ex) {
      Logger.getLogger(CoberturaXMLHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static File getCoberturaXMLInputFile(String coberturaXMLFileName) {
    return ResourceUtilities.getTestInputFile(
        "feature" + File.separator + "coverage" + File.separator + coberturaXMLFileName);
  }

  private static File getCoberturaXMLOutputFile(String coberturaXMLFileName) {
    String strippedCoberturaFileName = null;
    if (coberturaXMLFileName.contains(".")) {
      String suffix = coberturaXMLFileName.substring(coberturaXMLFileName.lastIndexOf('.'));
      strippedCoberturaFileName = coberturaXMLFileName.replace(suffix, "--stripped" + suffix);
    } else {
      strippedCoberturaFileName = coberturaXMLFileName.concat("--stripped.xml");
    }
    return ResourceUtilities.getTestReferenceFile(
        "feature" + File.separator + "coverage" + File.separator + strippedCoberturaFileName);
  }

  private static void printErrorManual() {
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
  }

  public static Coverage diffCoberturaFiles(
      Coverage firstCoverage, File inputCoberturaXML, String coverageName, File outputCoberturaXML)
      throws Exception {
    System.err.println("\n\n ****************** Starting with DIFF!");
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    Coverage coverage = new Coverage(inputCoberturaXML, outputCoberturaXML);
    parser.parse(inputCoberturaXML, new CoberturaXMLHandler(coverage, firstCoverage));
    return coverage;
  }

  public static Coverage readCoberturaFile(
      File inputCoberturaXML, String coverageName, File outputCoberturaXML) throws Exception {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    Coverage coverage = new Coverage(inputCoberturaXML, outputCoberturaXML);
    parser.parse(inputCoberturaXML, new CoberturaXMLHandler(coverage));
    return coverage;
  }

  // temporary static and hie
  static class Coverage {

    public File mInputCoberturaXmlFile;
    public File mOutputCoberturaXmlFile;
    public String mCurrentClassName;
    public int mLineNo = 0;
    public int mHitCount = 0;

    private List<Integer> mCurrentClass_CoveredLines;
    private List<Integer> mCurrentClass_LineHits;
    private Map<String, List<Integer>> mClassCoveragesLines;
    /** here the negativ lineNo indicates a hit other than default 1 */
    private Map<String, List<Integer>> mClassLineHits;

    private Map<Integer, Integer> mLineHits;
    private Iterator<Integer> mLineIterator = null;

    public Coverage(File inputCoberturaXML, File outputCoberturaXML) {
      mInputCoberturaXmlFile = inputCoberturaXML;
      mOutputCoberturaXmlFile = outputCoberturaXML;
      mClassCoveragesLines = new HashMap<String, List<Integer>>();
      mClassLineHits = new HashMap<String, List<Integer>>();
      mLineHits = new HashMap<Integer, Integer>();
    }

    /** @return an ordered list of line numbers */
    public List getClassCoverage(String className) {
      return mClassCoveragesLines.get(className);
    }

    /** @return an empty list of line numbers */
    public void newClassCoverage(String className) throws Exception {
      if (getClassCoverage(className) != null) {
        throw new Exception("The input cobertura file has unexpected split class coverage!");
      }
      mCurrentClassName = className;
      // collection of all upcoming lines (with hit > 0)
      mCurrentClass_CoveredLines = new LinkedList<>();
      mClassCoveragesLines.put(className, mCurrentClass_CoveredLines);

      // collection of all hits > 1 <- indicated internally by negative line number
      mCurrentClass_LineHits = new LinkedList<>();
      mClassLineHits.put(className, mCurrentClass_LineHits);
    }

    public void updateClassName(String className) {
      mLineIterator = null;
      mCurrentClassName = className;
      if (className != null) {
        mCurrentClass_CoveredLines = mClassCoveragesLines.get(className);
        mCurrentClass_LineHits = mClassLineHits.get(className);
      }
    }

    // @update mNextLine & mNextHits
    public void updateLineAndHitNo() {
      if (mLineIterator == null) {
        mLineIterator = mCurrentClass_CoveredLines.iterator();
      }
      if (mLineIterator.hasNext()) {
        mLineNo = mLineIterator.next();
        if (mLineNo < 0) {
          mLineNo *= -1;
          mHitCount = mLineHits.get(mLineNo);
        } else {
          mHitCount = 1;
        }
      } else {
        mLineNo = 0;
        mHitCount = 0;
      }
    }

    public void addLineCoverage(int lineNo, int hitCount, Locator locator) {
      if (mCurrentClass_CoveredLines == null) {
        System.err.println(
            "Line"
                + locator.getLineNumber()
                + "Column"
                + locator.getColumnNumber()
                + ": addLineCoverage "
                + mCurrentClassName
                + " is empty or null:'"
                + mCurrentClassName
                + "'!");
      }
      assert (hitCount > 0);
      if (hitCount > 1) {
        mLineHits.put(lineNo, hitCount);
        // most of the lineNo are 1 in the other rare case
        // we save the hitCount separately and indicate it by negativfe lineNo
        lineNo *= -1;
      }
      mCurrentClass_CoveredLines.add(lineNo);
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
