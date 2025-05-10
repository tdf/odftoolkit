/**
 * **********************************************************************
 *
 * <p>Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.incubator.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfWhitespaceProcessor;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** Test the text modification with the help of a the TextNavigation class */
public class TextModifyTest {

  private static final Logger LOG = Logger.getLogger(TextModifyTest.class.getName());
  public static final String TEXT_FILE = "TestTextSelection.odt";
  public static final String SAVE_FILE_DELETE = "TextSelectionResultDelete.odt";

  OdfTextDocument doc;
  TextNavigation search;

  @BeforeClass
  public static void setUpClass() throws Exception {}

  @AfterClass
  public static void tearDownClass() throws Exception {}

  @Before
  public void setUp() {
    try {
      doc =
          (OdfTextDocument)
              OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(TEXT_FILE));
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  @After
  public void tearDown() {}

  /** Test replace a single text part */
  @Test
  public void testDeleteSingleElement() {
    String phrase = "";

    // test single existing phrase
    phrase = "<%NAME%>";
    search = new TextNavigation(phrase, doc);
    while (search.hasNext()) {
      TextSelection result = search.next();
      assertNotNull(result);
      // delete text
      try {
        result.cut();
      } catch (InvalidNavigationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      //  OdfElement parentElement = result.getContainerElement();
      //    delete(result.getIndex(), phrase.length(), parentElement);

      //  replaceNeu(parentElement,phrase,"");
    }

    // test single phrase should no longer exist
    phrase = "<%NAME%>";
    search = new TextNavigation(phrase, doc);
    assertFalse(search.hasNext());

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_DELETE));
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /** Test replace a single text part */
  @Test
  public void testDeleteMultiElement() {
    String phrase = "";
    // test single existing phrase
    phrase = "ODFDOM";
    search = new TextNavigation(phrase, doc);
    int count = 0;
    while (search.hasNext()) {
      TextSelection result = search.next();
      assertNotNull(result);
      // delete text
      try {
        result.cut();
      } catch (InvalidNavigationException e) {
        e.printStackTrace();
        fail();
      }
      count++;
    }
    assertEquals(6, count);

    // test phrase should no longer exist
    phrase = "ODFDOM";
    search = new TextNavigation(phrase, doc);
    assertFalse(search.hasNext());

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_DELETE));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  public void replaceNeu(OdfElement element, String pattern, String replace) {
    String text = element.getTextContent();
    text = text.replace(pattern, replace);
    element.setTextContent(text);
  }

  /*
   * delete the pNode from the fromindex text, and delete leftLength text
   */
  private void delete(int fromindex, int leftLength, Node pNode) {
    if ((fromindex == 0) && (leftLength == 0)) {
      return;
    }
    int nodeLength = 0;
    Node node = pNode.getFirstChild();
    OdfWhitespaceProcessor textProcessor = new OdfWhitespaceProcessor();

    while (node != null) {
      if ((fromindex == 0) && (leftLength == 0)) {
        return;
      }
      if (node.getNodeType() == Node.TEXT_NODE) {
        nodeLength = node.getNodeValue().length();
      } else if (node.getNodeType() == Node.ELEMENT_NODE) {
        if (node.getLocalName().equals("s")) // text:s
        {
          try {
            nodeLength =
                Integer.parseInt(
                    ((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "c"));
          } catch (Exception e) {
            nodeLength = 1;
          }

        } else if (node.getLocalName().equals("line-break")) {
          nodeLength = 1;
        } else if (node.getLocalName().equals("tab")) {
          nodeLength = 1;
        } else {
          nodeLength = textProcessor.getText((OdfElement) node).length();
        }
      }
      if (nodeLength <= fromindex) {
        fromindex -= nodeLength;
      } else {
        // the start index is in this node
        if (node.getNodeType() == Node.TEXT_NODE) {
          String value = node.getNodeValue();
          StringBuffer buffer = new StringBuffer();
          buffer.append(value.substring(0, fromindex));
          int endLength = fromindex + leftLength;
          int nextLength = value.length() - endLength;
          fromindex = 0;
          if (nextLength >= 0) {
            // delete the result
            buffer.append(value.substring(endLength, value.length()));
            leftLength = 0;
          } else {
            leftLength = endLength - value.length();
          }
          node.setNodeValue(buffer.toString());

        } else if (node.getNodeType() == Node.ELEMENT_NODE) {
          // if text:s?????????
          if (node.getLocalName().equals("s")) // text:s
          {
            // delete space
            if (0 < fromindex || leftLength < nodeLength) {
              final int deleted = Math.min(leftLength, nodeLength - fromindex);
              ((TextSElement) node).setTextCAttribute(new Integer(nodeLength - deleted));
              leftLength = leftLength - deleted;
            } else {
              Node nodeMerker = node.getNextSibling();
              pNode.removeChild(node);
              node = nodeMerker;
              leftLength = leftLength - nodeLength;
            }
            fromindex = 0;
            continue;
          } else if (node.getLocalName().equals("line-break")
              || node.getLocalName().equals("tab")) {
            fromindex = 0;
            leftLength--;
          } else {
            delete(fromindex, leftLength, node);
            int length = (fromindex + leftLength) - nodeLength;
            leftLength = length > 0 ? length : 0;
            fromindex = 0;
          }
        }
      }
      node = node.getNextSibling();
    }
  }
}
