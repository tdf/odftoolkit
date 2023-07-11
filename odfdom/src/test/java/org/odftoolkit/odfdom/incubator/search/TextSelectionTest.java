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
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.*;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/** Test the method of class org.odftoolkit.odfdom.incubator.search.TextSelection */
public class TextSelectionTest {

  public static final String TEXT_FILE = "TestTextSelection.odt";
  public static final String SAVE_FILE_DELETE = "TextSelectionResult_Delete.odt";
  public static final String SAVE_FILE_STYLE = "TextSelectionResult_Style.odt";
  public static final String SAVE_FILE_HREF = "TextSelectionResult_Href.odt";
  public static final String SAVE_FILE_REPLACE = "TextSelectionResult_Replace.odt";
  public static final String SAVE_FILE_REPLACE_MULTI_SPACE = "TextSelectionResult_ReplaceMultispace.odt";
  public static final String SAVE_FILE__PASTE_AT_FRONT_OF_FIRST = "TextSelectionResult_PasteAtFrontOfFirst.odt";
  public static final String SAVE_FILE__PASTE_AT_FRONT_OF = "TextSelectionResult_PasteAtFrontOf.odt";
  public static final String SAVE_FILE__PASTE_AT_END_OF = "TextSelection_PasteAtEndOf.odt";  
  public static final String SAVE_FILE_DELETE_PATTERN = "TextSelectionResult_PatternDelete.odt";
  OdfTextDocument doc;
  OdfTextDocument doc2;
  OdfFileDom contentDOM;
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
      doc2 =
          (OdfTextDocument)
              OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(TEXT_FILE));
      contentDOM = doc.getContentDom();
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  @After
  public void tearDown() {}

  /**
   * Test cut method of org.odftoolkit.odfdom.incubator.search.TextSelection delete all the 'delete'
   * word
   */
  @Test
  public void testCut() {
    search = null;
    search = new TextNavigation("delete", doc);

    TextSelection nextSelect = null;
    TextNavigation nextsearch = new TextNavigation("next", doc);
    if (nextsearch.hasNext()) {
      nextSelect = nextsearch.next();
    }
    int i = 0;
    while (search.hasNext()) {
      TextSelection item = search.next();
      i++;
      try {
        item.cut();
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
    }
    assertTrue(8 == i);
    // research the "delete"
    search = new TextNavigation("delete", doc);
    Assert.assertFalse(search.hasNext());

    // this document just have one "next"
    try {
      nextSelect.cut();
    } catch (InvalidNavigationException e1) {
      Assert.fail(e1.getMessage());
    }
    Assert.assertFalse(nextsearch.hasNext());

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_DELETE));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test pasteAtFrontOf method of TextSelection copy the first 'change' word in the front of the
   * first match of the 'delete' word
   *
   * <p>This test exists only for testing the core functionality
   */
  @Test
  public void testPasteAtFrontOfFirst() {

    TextSelection copySelection = null;
    TextNavigation searchChangeWord = new TextNavigation("change", doc);
    if (searchChangeWord.hasNext()) {
      // select the first occurrence of the word 'change' to be inserted later
      copySelection = (TextSelection) searchChangeWord.next();
    }

    // now find all occurrences of 'delete' and insert 'change'
    search = new TextNavigation("delete", doc);
    if (search.hasNext()) {
      TextSelection item = search.next();
      try {
        copySelection.pasteAtFrontOf(item);
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
    }

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE__PASTE_AT_FRONT_OF_FIRST));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test pasteAtFrontOf method of TextSelection copy the first 'change' word in the front of all
   * the 'delete' words
   */
  @Test  
  public void testPasteAtFrontOf() {

    TextSelection copySelection = null;
    TextNavigation searchChangeWord = new TextNavigation("change", doc);
    if (searchChangeWord.hasNext()) {
      // select the first occurrence of the word 'change' to be inserted later
      copySelection = (TextSelection) searchChangeWord.next();
    }

    // now find all occurrences of 'delete' and insert 'change'
    int i = 0;
    search = new TextNavigation("delete", doc);
    while (search.hasNext()) {
      TextSelection item = search.next();
      i++;
      try {
        copySelection.pasteAtFrontOf(item);
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
    }

    int j = 0;
    search = new TextNavigation("changedelete", doc);
    while (search.hasNext()) {
      search.next();
      j++;
    }
    // The count of 'changedelete' should be equals as 'delete'
    assertTrue(i == j);

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE__PASTE_AT_FRONT_OF));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test pasteAtEndOf method of org.odftoolkit.odfdom.incubator.search.TextSelection
   *
   * <p>copy the first 'change' word at the end of all the 'delete' words
   */
  @Test
  public void testPasteAtEndOf() {
    search = new TextNavigation("delete", doc);
    TextSelection sel = null;

    TextNavigation searchChangeWord = new TextNavigation("change", doc);
    if (searchChangeWord.hasNext()) {
      // take the first selection..
      sel = (TextSelection) searchChangeWord.next();
    }
    assertNotNull(sel);

    int i = 0;

    while (search.hasNext()) {
      TextSelection item = search.next();
      i++;
      try {
        sel.pasteAtEndOf(item);
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
    }
    int j = 0;
    search = new TextNavigation("deletechange", doc);
    while (search.hasNext()) {
      search.next();
      j++;
    }
    // The count of 'deletechange' should be equals as 'delete'
    assertTrue(i == j);

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE__PASTE_AT_END_OF));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test applyStyle method of org.odftoolkit.odfdom.incubator.search.TextSelection append "T4"
   * style for all the 'delete' word, 'T4' in the original document is the 'bold' style
   */
  @Test
  public void testApplyStyle() {
    search = null;
    search = new TextNavigation("delete", doc);
    OdfOfficeAutomaticStyles autoStyles = null;
    try {
      autoStyles = doc.getContentDom().getAutomaticStyles();
    } catch (Exception e1) {
      Assert.fail("Failed with " + e1.getClass().getName() + ": '" + e1.getMessage() + "'");
    }
    // T4 is the bold style for text
    OdfStyleBase style = autoStyles.getStyle("T4", OdfStyleFamily.Text);
    Assert.assertNotNull(style);

    while (search.hasNext()) {
      TextSelection item = search.next();
      try {
        item.applyStyle(style);
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
    }

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_STYLE));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /** Test replaceWith method of TextSelection. Replace all the 'ODFDOM' with 'Odf Toolkit' */
  @Test
  public void testReplacewith() {

    // replace all the "ODFDOM" to "Odf Toolkit"
    // except the sentence "Task5.Change the ODFDOM to Odf Toolkit, and bold them."
    search = new TextNavigation("ODFDOM", doc);
    int i = 0;
    while (search.hasNext()) {
      TextSelection item = search.next();
      try {
        item.replaceWith("Odf Toolkit");
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
      i++;
    }
    // we expect 6 occurrences
    assertEquals(6, i);

    search = new TextNavigation("Odf Toolkit", doc);
    int j = 0;
    while (search.hasNext()) {
      TextSelection item = search.next();
      j++;
    }
    // we expect 7 occurrences
    assertEquals(7, j);

    // ODFDOM should no longer occur
    search = new TextNavigation("ODFDOM", doc);
    assertFalse(search.hasNext());

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_REPLACE));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test replaceWith method of TextSelection. Replace all the 'ODFDOM' with 'Odf Toolkit' and set
   * style 'bold'
   */
  @Test
  public void testReplacewithAndBold() {
    search = null;
    search = new TextNavigation("ODFDOM", doc);

    // replace all the "ODFDOM" to "Odf Toolkit"
    // except the sentence "Task5.Change the ODFDOM to Odf Toolkit, and bold them."
    OdfStyle style = new OdfStyle(contentDOM);
    style.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
    style.setStyleFamilyAttribute("text");
    int i = 0;
    while (search.hasNext()) {
      // if (i > 0) {
      TextSelection item = search.next();
      try {
        item.replaceWith("Odf Toolkit");
        item.applyStyle(style);
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
      // }
      i++;
    }
    // we expect 6 occurrences
    assertEquals(6, i);

    search = new TextNavigation("Odf Toolkit", doc);
    int j = 0;
    while (search.hasNext()) {
      TextSelection item = search.next();
      j++;
    }
    // we expect 7 occurrences
    assertEquals(7, j);

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_REPLACE));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test replacewith method of org.odftoolkit.odfdom.incubator.search.TextSelection with multiple
   * spaces
   */
  @Test
  public void testReplacewithMultispace() {
    final List<String> toSearch =
        Arrays.asList("multiple   ", "containing ", "some\\s+words", "some\\s+others", "%>  ");
    final List<TextNavigation> navigations =
        toSearch.stream()
            .map(s -> new TextNavigation(Pattern.compile(s), doc2))
            .collect(Collectors.toList());
    navigations.forEach(n -> assertTrue("Navigation " + n + " should have a next", n.hasNext()));
    final List<TextSelection> selections =
        navigations.stream().map(n -> n.next()).collect(Collectors.toList());
    try {
      selections.get(0).replaceWith("Xmultiple___X");
      selections.get(1).replaceWith("Xcontaining_X");
      selections.get(2).replaceWith("Xsome_+wordsX");

      selections.get(3).replaceWith("Xsome_+othersX");
      selections.get(4).replaceWith("X");
    } catch (final Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
    navigations.forEach(
        n -> assertFalse("Navigation " + n + " should not have a next", n.hasNext()));
    try {
      doc2.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_REPLACE_MULTI_SPACE));
    } catch (final Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test addHref method of org.odftoolkit.odfdom.incubator.search.TextSelection add href
   * "http://www.ibm.com" for all the 'delete' word
   */
  @Test
  public void testAddHref() {
    search = null;
    search = new TextNavigation("^delete", doc);
    while (search.hasNext()) {
      TextSelection item = search.next();
      // LOG.info(item);
      try {
        item.addHref(new URL("http://www.ibm.com"));
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      } catch (MalformedURLException e) {
        Assert.fail(e.getMessage());
        Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
      }
    }

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_HREF));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }

  /**
   * Test search pattern of org.odftoolkit.odfdom.incubator.search.TextSelection search a snippet of
   * text match the pattern "<%([^>]*)%>", and extract the content between "<%" and "%>"
   */
  @Test
  public void testCutPattern() {
    search = new TextNavigation("<%([^>]*)%>", doc);

    while (search.hasNext()) {
      TextSelection item = search.next();
      try {
        String text = item.getText();
        text = text.substring(2, text.length() - 2);
        item.replaceWith(text);
      } catch (InvalidNavigationException e) {
        Assert.fail(e.getMessage());
      }
    }

    try {
      doc.save(ResourceUtilities.getTestOutputFile(SAVE_FILE_DELETE_PATTERN));
    } catch (Exception e) {
      Logger.getLogger(TextSelectionTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
      Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
    }
  }
}
