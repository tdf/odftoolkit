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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;
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
import org.odftoolkit.odfdom.incubator.doc.text.OdfWhitespaceProcessor;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/** Test the method of class org.odftoolkit.odfdom.incubator.search.TextNavigation */
public class TextNavigationTest {

  private static final Logger LOG = Logger.getLogger(TextNavigationTest.class.getName());
  public static final String TEXT_FILE = "TestTextSelection.odt";
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

  /** Test getCurrentItem method of org.odftoolkit.odfdom.incubator.search.TextNavigation */
  @Test
  public void testGotoNext() {

    search = null;
    search = new TextNavigation("delete", doc);

    while (search.hasNext()) {
      TextSelection item = search.next();
      LOG.info(item.toString());
    }
  }

  /** Test getNextMatchElement method of org.odftoolkit.odfdom.incubator.search.TextNavigation */
  @Test
  public void testGetNextMatchElement() {

    search = null;
    search = new TextNavigation("delete", doc);
    OdfWhitespaceProcessor textProcessor = new OdfWhitespaceProcessor();

    try {
      // NodeList list = doc.getContentDom().getElementsByTagName("text:p");
      OdfElement firstmatch = (OdfElement) search.getNextMatchElement(doc.getContentRoot());
      Assert.assertNotNull(firstmatch);
      Assert.assertEquals("Task2.delete next paragraph", textProcessor.getText(firstmatch));

      OdfElement secondmatch = (OdfElement) search.getNextMatchElement(firstmatch);
      Assert.assertNotNull(secondmatch);
      Assert.assertEquals("Hello [delete], I will be delete", textProcessor.getText(secondmatch));

      OdfElement thirdmatch = (OdfElement) search.getNextMatchElement(secondmatch);
      Assert.assertNotNull(thirdmatch);
      Assert.assertEquals("indeed   delete", textProcessor.getText(thirdmatch));

      OdfElement match4 = (OdfElement) search.getNextMatchElement(thirdmatch);
      Assert.assertNotNull(match4);
      Assert.assertEquals(
          "different span in one single word delete indeed", textProcessor.getText(match4));

      OdfElement match5 = (OdfElement) search.getNextMatchElement(match4);
      Assert.assertNotNull(match5);
      Assert.assertEquals(
          "Hello delete this word delete true delete  indeed", textProcessor.getText(match5));

    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /** Test methods hasNext and next of org.odftoolkit.odfdom.incubator.search.TextNavigation */
  @Test
  public void testHasNextNext() {
    String phrase = "";
    try {
      phrase = "<%NAME%>";
      search = new TextNavigation(phrase, doc);
      while (search.hasNext()) {
        TextSelection item = search.next();
        LOG.info(item.toString());
        OdfElement element = search.getElement();

        String text = element.getTextContent();
        Logger logger = Logger.getLogger(TextNavigationTest.class.getName());
        logger.log(Level.INFO, " Current Item Text=" + text);
        element.setTextContent("John Doe");
      }

      // test the phrase 'ODFDOM' which should occur in 4 paragraphs
      phrase = "ODFDOM";
      int countParagraphs = 0;
      search = new TextNavigation(phrase, doc);
      while (search.hasNext()) {

        TextSelection item = search.next();
        LOG.info(item.toString());

        OdfElement element = search.getElement();
        String text = element.getTextContent();
        Logger logger = Logger.getLogger(TextNavigationTest.class.getName());
        logger.log(Level.INFO, " Current Item Text=" + text);
        text = text.replace(phrase, "Software Project");
        element.setTextContent(text);
        countParagraphs++;
      }
      Assert.assertEquals(6, countParagraphs);

    } catch (Exception e) {
      LOG.log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /** Test methods hasNext and next based on the iterator interface */
  @Test
  public void testIteratorInterface() {
    String phrase = "";

    // test single existing phrase
    phrase = "<%NAME%>";
    search = new TextNavigation(phrase, doc);
    if (search.hasNext()) {
      TextSelection result = search.next();
      assertNotNull(result);
    }

    // test non existing phrase
    phrase = "<%NOT EXISTING%>";
    search = new TextNavigation(phrase, doc);
    assertFalse(search.hasNext());

    // test non existing phrase without hasNext
    phrase = "<%NOT EXISTING%>";
    search = new TextNavigation(phrase, doc);
    try {
      search.next(); // throws Exception
      fail();
    } catch (NoSuchElementException e) {
      // expected exception
    }

    // test single phrase without hasNext() and calling next() twice
    phrase = "<%NAME%>";
    search = new TextNavigation(phrase, doc);
    try {
      search.next();
      search.next(); // exception expected
      fail();
    } catch (NoSuchElementException e) {
      // expected exception
    }
  }
}
