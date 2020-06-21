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
package org.odftoolkit.odfdom.doc.number;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** @author J David Eisenberg */
public class OdfTimeStyleTest {
  private static final Logger LOG = Logger.getLogger(OdfTimeStyleTest.class.getName());
  OdfSpreadsheetDocument doc;
  OdfFileDom dom;

  public OdfTimeStyleTest() {}

  @BeforeClass
  public static void setUpClass() throws Exception {}

  @AfterClass
  public static void tearDownClass() throws Exception {}

  @Before
  public void setUp() {
    try {
      doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
      dom = doc.getContentDom();
    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail(e.getMessage());
    }
  }

  @After
  public void tearDown() {}

  /** Test of buildFromFormat method, of class OdfDateStyle. */
  /*
  a 	Am/pm marker         PM
  H 	Hour in day (0-23)   0
  h 	Hour in am/pm (1-12) -- depends on AM/PM marker
  m 	Minute in hour       30
  s 	Second in minute     55
          */
  @Test
  public void testBuildFromFormat() {
    int i;
    int j;
    String[] formatTest = {"hh:mm:ss a", "HH:mm:ss"};

    /*
     * starts with "T" if a text node (followed by content)
     * starts with "E" if an element, followed by:
     *    S or L for short or long node, anything else if not applicable
     */
    String[][] expected = {
      {"ELhours", "T:", "ELminutes", "T:", "E-seconds", "T ", "E-am-pm"}, // "hh:mm:ss a"
      {"ELhours", "T:", "ELminutes", "T:", "ELseconds"} // "HH:mm:ss"
    };

    LOG.info("buildFromFormat");
    OdfNumberTimeStyle instance = null;

    for (i = 0; i < formatTest.length; i++) {
      LOG.info("Time format: " + formatTest[i]);
      instance = new OdfNumberTimeStyle(dom, formatTest[i], "fstyle");
      Assert.assertNotNull(instance.getFirstChild());

      checkNodes(instance.getFirstChild(), expected[i], 0);
    }
  }

  private void checkNodes(Node node, String[] expected, int position) {
    char expectedType;
    String expectedValue;

    while (node != null) {
      Assert.assertTrue("More nodes than specifiers", position < expected.length);
      expectedType = expected[position].charAt(0);
      expectedValue = expected[position].substring(1);

      switch (expectedType) {
        case 'T':
          checkNumberText("text", expectedValue, node);
          position++;
          break;
        case 'E':
          checkElement(expectedValue, node);
          position++;
          if (node.hasChildNodes()) {
            node = node.getFirstChild();
            checkNodes(node, expected, position);
          }
          break;
      }
      node = node.getNextSibling();
    }
  }

  /**
   * Check that the node is an element with the given name with the expected text content.
   *
   * @param elementName expected element name (in number: namespace)
   * @param expected expected text content
   * @param node the Node to be examined
   */
  private void checkNumberText(String elementName, String expected, Node node) {
    Node childNode;
    Assert.assertNotNull(node);
    Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
    Assert.assertEquals(OdfDocumentNamespace.NUMBER.getUri(), node.getNamespaceURI());
    Assert.assertEquals(elementName, node.getLocalName());
    childNode = node.getFirstChild();
    Assert.assertEquals(Node.TEXT_NODE, childNode.getNodeType());
    Assert.assertEquals(expected, childNode.getNodeValue());
  }

  /**
   * Check to see that the given Node is an element with the expected name and the appropriate
   * number:style of short or long. For the month specifier, the element name ends with N if it's
   * numeric and T if it's textual.
   *
   * @param expectedName name element should have
   * @param expectedLong 'S' for short, 'L' for long, ' ' if not applicable
   * @param node the node to be examined
   */
  private void checkElement(String expectedName, Node node) {
    char expectedLong;
    String longShort;
    String monthTextual = null;

    expectedLong = expectedName.charAt(0);
    expectedName = expectedName.substring(1);
    if (expectedName.equals("monthN")) {
      expectedName = "month";
      monthTextual = "false";
    } else if (expectedName.equals("monthT")) {
      expectedName = "month";
      monthTextual = "true";
    }
    Assert.assertEquals(Node.ELEMENT_NODE, node.getNodeType());
    Assert.assertEquals(expectedName, node.getLocalName());

    if (expectedLong == 'S' || expectedLong == 'L') {
      if (expectedLong == 'S') {
        longShort = "short";
      } else {
        longShort = "long";
      }
      Assert.assertEquals(
          "Element is " + expectedName,
          longShort,
          ((Element) node).getAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "style"));
    }

    if (monthTextual != null) {
      Assert.assertEquals(
          monthTextual,
          ((Element) node).getAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "textual"));
    }
  }
}
