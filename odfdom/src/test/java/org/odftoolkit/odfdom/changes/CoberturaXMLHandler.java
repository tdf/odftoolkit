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

  // e.g. within odftoolkit_latest-0.10.1/odfdom/target/test-classes/test-input/feature/coverage
  private static final String COBERTURA_XML_FILENAME = "cobertura_bold__indent.cov";
  static File mStrippedCoberturaFile = null;
  // Reusing second CoberturyXML for neglecting all lines/methods/classes/packages without hits
  // (coverage)
  XMLStreamWriter mXsw = null;

  // keeping the information from the start element SAX event until it is certain the XML should be
  // written
  Deque<ElementInfo> mStartElementStack = new ArrayDeque<ElementInfo>();
  //    String mPackageName;
  //    String mFileName;
  //    String mClassName;
  //    String mMethodName;
  //    String mMethodSignature;
  //    int mLineNumber;
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
      if (qName.equals("line")) {
        String lineNumber = getAttributeValue(attributes, "number");
        //              mLineNumber = Integer.parseInt(lineNumber);
        String hits = getAttributeValue(attributes, "hits");
        int hitCount = Integer.parseInt(hits);
        if (hitCount > 0) {
          String conditionCoverage = attributes.getValue("condition-coverage");
          if (conditionCoverage != null && !conditionCoverage.isBlank()) {
            mIsCoveredCondition = true;
          }
          flushStartElements();
          //                    if (mMethodName != null) {
          //                        // System.out.println(mClassName + "-L" + mLineNumber + " - "+
          // mMethodName + "() - Times:"
          //                        // + mHits);
          //                    } else {
          //                        System.out.print(mClassName + "-L" + mLineNumber);
          //                        if (mHits > 1) {
          //                            System.out.print(" - Times:" + mHits);
          //                        }
          //                        System.out.println();
          //                    }
        }
        //            } else if (qName.equals("method")) {
        //                // reset its information for debug
        //                mMethodName = getAttributeValue(attributes, "name");
        //                mMethodSignature = getAttributeValue(attributes, "signature");
        //
        //            } else if (qName.equals("class")) {
        //                // reset its information for debug
        //                mFileName = getAttributeValue(attributes, "filename");
        //                mClassName = getAttributeValue(attributes, "name");
        //
        //            } else if (qName.equals("package")) {
        //                // add information for debug
        //                mPackageName = getAttributeValue(attributes, "name");
        //
        //            } else if (qName.equals("packages")) {
        //                // add information for debug
        //                mPackageName = getAttributeValue(attributes, "name");

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
    } else {
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
      //            if (qName.equals("method")) {
      //                // reset its information for debug
      //                mMethodName = null;
      //                mMethodSignature = null;
      //            } else if (qName.equals("class")) {
      //                // reset its information for debug
      //                mFileName = null;
      //                mClassName = null;
      //            } else if (qName.equals("package")) {
      //                // reset its information for debug
      //                mPackageName = null;
      //            }
      //
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

    /*

            if (qName.equals("line")) {
                if(mStartElementStack.pop().isStartElementWritten){
                if (mIsCoveredLine) {
                    mIsCoveredLine = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("lines")) {
                mStartElementStack.pop();
                if (mIsCoveredLines) {
                    mIsCoveredLines = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("method")) {
                // reset its information for debug
                mMethodName = null;
                mMethodSignature = null;
                mStartElementStack.pop();
                if (mIsCoveredMethod) {
                    mIsCoveredMethod = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("methods")) {
                mStartElementStack.pop();
                if (mIsCoveredMethods) {
                    mIsCoveredMethods = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("class")) {
                // reset its information for debug
                mFileName = null;
                mClassName = null;
                mStartElementStack.pop();
                if (mIsCoveredClass) {
                    mIsCoveredClass = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("classes")) {
                mStartElementStack.pop();
                if (mIsCoveredClasses) {
                    mIsCoveredClasses = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("package")) {
                mIsCoveredPackage = false;

                // reset its information for debug
                mPackageName = null;
                mStartElementStack.pop();
                if (mIsCoveredPackage) {
                    mIsCoveredPackage = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("packages")) {
                mIsCoveredPackages = false;

                if (mIsCoveredPackages) {
                    mIsCoveredPackages = Boolean.FALSE;
                    writeEndElement();
                }
            } else if (qName.equals("condition")) {
                //mStartElementStack.pop();
                if (mIsCoveredCondition) {
                    writeEndElement();
                }
            } else if (qName.equals("conditions")) {
                //mStartElementStack.pop();
                if (mIsCoveredCondition) {
                    mIsCoveredCondition = false;
                    writeEndElement();
                }
            } else { // any other element will be written out
                // assumed not being in the descendant line of "line"
                writeEndElement();
            }
    */
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
  //
  //    private void writeStartElement(ElementInfo elementInfo) {
  //        writeStartElement(elementInfo.uri, elementInfo.localName, elementInfo.qName,
  // elementInfo.attributes);
  //    }

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
          "PURPOSE:\n"
              + "\tRemoves all lines without coverage from a single Cobertura XML file. Becoming a Feature Coverage XML file!\n\n"
              + "USAGE:\n"
              + "\t1st parameter: \n"
              + "\t   Name of the Cobertura Coverage XML file from directory:\n"
              + "\t   odfdom/target/test-classes/test-input/feature/coverage/\n\n"
              + "OUTPUT:\n"
              + "\tName of the Cobertura Coverage input XML file added with '--feature' in directory:\n"
              + "\t   odfdom/target/test-classes/test-output/feature!\n\n");

      // "t2nd (optional) parameter: Path to Cobertura Coverage XML which will be substracted from
      // the first");
      coberturaFileName = COBERTURA_XML_FILENAME;
    }
    // e.g. odftoolkit_latest-0.10.1/odfdom/target/test-classes/test-reference/features
    File coberturaXMLFile =
        ResourceUtilities.getTestInputFile(
            "feature" + File.separator + "coverage" + File.separator + coberturaFileName);

    String strippedCoberturaFileName = coberturaFileName.replace(".cov", "--feature.xml");
    mStrippedCoberturaFile =
        ResourceUtilities.getTestOutputFile("feature" + File.separator + strippedCoberturaFileName);

    try {
      readFileListFile(coberturaXMLFile, "testRun");
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
    public Map<String, List> mClassCoverages;

    public Coverage(String name) {
      mCoverageName = name;
      mClassCoverages = new HashMap<String, List>();
    }

    // Map of LinkedList
    // key: className value: LinkedList of CoveredLines
  }

  // temporary static and hie
  static class ElementInfo {

    public String uri;
    public String localName;
    public String qName;
    public Attributes attributes;
    public boolean isStartElementWritten =
        false; // if there is another subelement, e.g. methods not all covered will be written out
    // again

    public ElementInfo(String uri, String localName, String qName, Attributes attributes) {
      this.uri = uri;
      this.localName = localName;
      this.qName = qName;
      this.attributes = new Attributes2Impl(attributes);
    }
  }
}
