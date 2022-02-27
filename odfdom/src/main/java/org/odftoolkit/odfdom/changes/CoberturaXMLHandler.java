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
 * <p>**********************************************************
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

  public CoberturaXMLHandler(Coverage coverage) {
    mCov = coverage;
  }

  public CoberturaXMLHandler(Coverage coverageMinuend, Coverage coverageSubtrahend) {
    mCov = coverageMinuend;
    mCovSubtrahend = coverageSubtrahend;
  }

  Coverage mCov = null;
  Coverage mCovSubtrahend = null;

  // e.g. within odftoolkit/odfdom/target/test-classes/test-input/feature/coverage
  private static final String COBERTURA_FILENAME__MINUEND = "cobertura_bold__indent.cov";
  private static final String COBERTURA_FILENAME__SUBTRAHEND = "cobertura_text_italic.cov";

  // mStrippedWriter will be filled twice
  StreamWriter mStrippedWriter = null;
  StreamWriter mStrippedWriter_Diff = null;
  // keeping all information from start elements
  // until it is certain the XML should be written
  Deque<ElementInfo> mStartElementStack = new ArrayDeque<ElementInfo>();
  Deque<ElementInfo> mStartElementStack_Diff = new ArrayDeque<ElementInfo>();
  boolean mIsCoveredCondition = false;
  boolean mIsCoveredCondition_Diff = false;

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

      // stack as the elements are only written, if at least one line has a hit > 1 (otherwise
      // elements are neglected)
      mStartElementStack.push(new ElementInfo(uri, localName, qName, attributes));
      if (mCovSubtrahend != null) {
        mStartElementStack_Diff.push(new ElementInfo(uri, localName, qName, attributes));
      }
      if (qName.equals("class")) {
        String className = getAttributeValue(attributes, "name");
        if (className != null && !className.isBlank()) {
          try {
            // sets all dependant state changes
            mCov.newClassCoverage(className);
            if (mCovSubtrahend != null) {
              mCovSubtrahend.updateClassName(className);
              mCovSubtrahend.updateLineAndHitNo();
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
          boolean hasConditionCoverage = hasConditionCoverage(attributes);
          if (hasConditionCoverage) {
            mIsCoveredCondition = true;
          }
          String number = getAttributeValue(attributes, "number");
          int lineNo = Integer.parseInt(number);
          // adding the new line coverage
          mCov.addLineCoverage(lineNo, hitCount, mLocator);
          flushStartElements(mStartElementStack, mStrippedWriter);
          if (mCovSubtrahend != null) {
            // state is saved in the handler's start-element stack
            if (mCovSubtrahend.mLineNo != 0 && mCovSubtrahend.mLineNo == lineNo) {
              if (hitCount > mCovSubtrahend.mHitCount) {
                // only a feature of the current parsed file mCov
                int index = attributes.getIndex("hits");
                mStartElementStack_Diff.pop(); // remove wrong hit attribute
                Attributes2Impl updatedAttributes = new Attributes2Impl(attributes);
                updatedAttributes.setValue(
                    index, Integer.toString(hitCount - mCovSubtrahend.mHitCount));
                mStartElementStack_Diff.push(
                    new ElementInfo(uri, localName, qName, updatedAttributes));
                mCovSubtrahend.addLineCoverage(lineNo, hitCount, mLocator);
                if (hasConditionCoverage) {
                  mIsCoveredCondition_Diff = true;
                }
                flushStartElements(mStartElementStack_Diff, mStrippedWriter_Diff);
              }
              mCovSubtrahend.updateLineAndHitNo();
            }
          }
        }
      }
    } else {
      /* NOTE: The condition element always follows a line element
      and will be written if the prior line was written (hit > 0) */
      if (qName.equals("condition") || qName.equals("conditions")) {
        if (mIsCoveredCondition) {
          mStrippedWriter.writeStartElement(uri, localName, qName, attributes);
        }
        if (mIsCoveredCondition_Diff) {
          mStrippedWriter_Diff.writeStartElement(uri, localName, qName, attributes);
        }
      } else if (qName.equals("coverage")) {
        mStrippedWriter.writeStartElementWithoutAttribute(
            uri, localName, qName, attributes, "timestamp");
        if (mCovSubtrahend != null) {
          mStrippedWriter_Diff.writeStartElementWithoutAttribute(
              uri, localName, qName, attributes, "timestamp");
        }
      } else { // all other elements (not mentioned earlier) will be writen out
        mStrippedWriter.writeStartElement(uri, localName, qName, attributes);
        if (mCovSubtrahend != null) {
          mStrippedWriter_Diff.writeStartElement(uri, localName, qName, attributes);
        }
      }
    }
  }

  private boolean hasConditionCoverage(Attributes attributes) {
    String conditionCoverage = attributes.getValue("condition-coverage");
    return (conditionCoverage != null && !conditionCoverage.isBlank());
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
      ElementInfo elementInfo = mStartElementStack.pop();
      if (elementInfo.mIsStartElementWritten) {
        mStrippedWriter.writeEndElement();
      }
      if (mCovSubtrahend != null) {
        ElementInfo elementInfo_Diff = mStartElementStack_Diff.pop();
        if (elementInfo_Diff.mIsStartElementWritten) {
          mStrippedWriter_Diff.writeEndElement();
        }
      }
      if (qName.equals("class")) {
        mCov.updateClassName(null);
        if (mCovSubtrahend != null) {
          mCovSubtrahend.updateClassName(null);
        }
      }

    } else if (qName.equals("condition") || qName.equals("conditions")) {
      if (mIsCoveredCondition) {
        mStrippedWriter.writeEndElement();
        if (qName.equals("conditions")) {
          mIsCoveredCondition = false;
        }
      }
      if (mCovSubtrahend != null) {
        if (this.mIsCoveredCondition_Diff) {
          mStrippedWriter_Diff.writeEndElement();
          if (qName.equals("conditions")) {
            mIsCoveredCondition_Diff = false;
          }
        }
      }
    } else { // any other element will be written out
      // assumed not being in the descendant line of "line"
      // System.out.println("WRITING BASIC END:" + qName);
      mStrippedWriter.writeEndElement();
      if (mCovSubtrahend != null) {
        mStrippedWriter_Diff.writeEndElement();
      }
    }
  }

  public void startDocument() {
    mStrippedWriter = new StreamWriter(mCov.mOutputCoberturaXmlFile_stripped);
    if (mCovSubtrahend != null) {
      // initialize the two feature output streams
      mStrippedWriter_Diff = new StreamWriter(mCovSubtrahend.mOutputCoberturaXmlFile_Diff);
    }
  }

  public void endDocument() {
    mStrippedWriter.flushAndCloseWriter();
    if (mCovSubtrahend != null) {
      mStrippedWriter_Diff.flushAndCloseWriter();
    }
  }

  public static void main(String[] params) {
    String coberturaInputFileName_FeatureA = null;
    String coberturaInputFileName_FeatureB = null;
    if (params.length == 0 || params.length > 2) {
      printErrorManual();
      // 2DO Remove the this.. :-)
      coberturaInputFileName_FeatureA = COBERTURA_FILENAME__MINUEND;
      coberturaInputFileName_FeatureB = COBERTURA_FILENAME__SUBTRAHEND;
    } else if (params.length == 1) {
      coberturaInputFileName_FeatureA = params[0];
      if (coberturaInputFileName_FeatureA == null || coberturaInputFileName_FeatureA.isBlank()) {
        printErrorManual();
        coberturaInputFileName_FeatureA = COBERTURA_FILENAME__MINUEND;
      }
    } else if (params.length == 2) {
      coberturaInputFileName_FeatureA = params[0];
      coberturaInputFileName_FeatureB = params[1];
      if (coberturaInputFileName_FeatureA == null
          || coberturaInputFileName_FeatureA.isBlank()
          || coberturaInputFileName_FeatureB == null
          || coberturaInputFileName_FeatureB.isBlank()) {
        printErrorManual();
        coberturaInputFileName_FeatureA = COBERTURA_FILENAME__MINUEND;
        coberturaInputFileName_FeatureB = COBERTURA_FILENAME__SUBTRAHEND;
      }
    }
    try {
      if (coberturaInputFileName_FeatureB == null) {
        readCoberturaFile(coberturaInputFileName_FeatureA);
      } else {
        Coverage coverage_FeatureB = readCoberturaFile(coberturaInputFileName_FeatureB);
        diffCoberturaFiles(coberturaInputFileName_FeatureA, coverage_FeatureB);
      }
    } catch (Exception ex) {
      Logger.getLogger(CoberturaXMLHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
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
      String inputCoberturaFileName, Coverage coverageSubtrahend) throws Exception {
    System.err.println("\n\n ****************** Starting with DIFF!");
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    Coverage coverage = new Coverage(inputCoberturaFileName);
    parser.parse(
        coverage.mInputCoberturaXmlFile, new CoberturaXMLHandler(coverage, coverageSubtrahend));
    return coverage;
  }

  public static Coverage readCoberturaFile(String inputCoberturaFileName) throws Exception {
    SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
    Coverage coverage = new Coverage(inputCoberturaFileName);
    parser.parse(coverage.mInputCoberturaXmlFile, new CoberturaXMLHandler(coverage));
    return coverage;
  }

  /** As soon a line with coverage was founda all ancestor start elements will be written out */
  private void flushStartElements(Deque<ElementInfo> startElementStack, StreamWriter streamWriter) {
    Iterator<ElementInfo> it = startElementStack.descendingIterator();
    while (it.hasNext()) {
      ElementInfo elementInfo = it.next();
      if (!elementInfo.mIsStartElementWritten) {
        streamWriter.writeStartElement(
            elementInfo.uri, elementInfo.localName, elementInfo.qName, elementInfo.attributes);
        // System.out.println("Writing StartElement with qName:" + elementInfo.qName);
        elementInfo.mIsStartElementWritten = true;
      }
    }
  }

  static class StreamWriter {

    XMLStreamWriter mXsw = null;

    public StreamWriter(File outputFile) {

      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      try {
        // make sure the output directories are being created

        outputFile.getParentFile().mkdirs();
        mXsw = xof.createXMLStreamWriter(new FileWriter(outputFile.getAbsolutePath()));
        mXsw.writeStartDocument();

      } catch (Exception e) {
        System.err.println("Unable to write the file: " + e.getMessage());
      }
    }

    public void writeStartElement(
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

    public void writeStartElementWithoutAttribute(
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

    public void writeEndElement() {
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

    public void flushAndCloseWriter() {
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
  }

  static class Coverage {

    public File mInputCoberturaXmlFile;
    public File mOutputCoberturaXmlFile_stripped;
    public File mOutputCoberturaXmlFile_Diff;
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

    public Coverage(String inputCoberturaFileName) {
      initializeInputOutputFiles(inputCoberturaFileName);

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

    private void initializeInputOutputFiles(String inputFileName) {
      // e.g. odftoolkit/odfdom/target/test-classes/test-reference/features
      mInputCoberturaXmlFile = getCoberturaXMLInputFile(inputFileName);
      mOutputCoberturaXmlFile_stripped = getCoberturaXMLOutputFile(inputFileName, "--stripped");
      mOutputCoberturaXmlFile_Diff = getCoberturaXMLOutputFile(inputFileName, "--diff");
    }

    private static File getCoberturaXMLInputFile(String coberturaXMLFileName) {
      return ResourceUtilities.getTestInputFile(
          "feature" + File.separator + "coverage" + File.separator + coberturaXMLFileName);
    }

    private File getCoberturaXMLOutputFile(String coberturaXMLFileName, String newSuffix) {
      String strippedCoberturaFileName = null;
      if (coberturaXMLFileName.contains(".")) {
        String suffix = coberturaXMLFileName.substring(coberturaXMLFileName.lastIndexOf('.'));
        strippedCoberturaFileName = coberturaXMLFileName.replace(suffix, newSuffix + suffix);
      } else {
        strippedCoberturaFileName = coberturaXMLFileName.concat(newSuffix);
      }
      return ResourceUtilities.getTestReferenceFile(
          "feature" + File.separator + "coverage" + File.separator + strippedCoberturaFileName);
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
    public boolean mIsStartElementWritten = false;

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
