/*
 * Copyright 2021 The Document Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schema2template.example.odf;

import static org.junit.Assert.fail;

import com.sun.msv.grammar.Grammar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** @author Niemand */
public class OdfFamilyPropertiesPatternMatcherTest {

  private static final String EXPECTED_ODF13_RESULT =
      "@style:family = 'chart' = style:chart-properties style:graphic-properties style:paragraph-properties style:text-properties\n"
          + "@style:family = 'drawing-page' = style:drawing-page-properties\n"
          + "@style:family = 'graphic' = style:graphic-properties style:paragraph-properties style:text-properties\n"
          + "@style:family = 'paragraph' = style:paragraph-properties style:text-properties\n"
          + "@style:family = 'presentation' = style:graphic-properties style:paragraph-properties style:text-properties\n"
          + "@style:family = 'ruby' = style:ruby-properties\n"
          + "@style:family = 'section' = style:section-properties\n"
          + "@style:family = 'table' = style:table-properties\n"
          + "@style:family = 'table-cell' = style:table-cell-properties style:paragraph-properties style:text-properties\n"
          + "@style:family = 'table-column' = style:table-column-properties\n"
          + "@style:family = 'table-row' = style:table-row-properties\n"
          + "@style:family = 'text' = style:text-properties\n";

  public OdfFamilyPropertiesPatternMatcherTest() {}

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  /** Test of getProperties method, of class OdfFamilyPropertiesPatternMatcher. */
  @Test
  public void testGetProperties() throws Exception {
    Grammar g = PuzzlePieceTest.loadSchemaODF12();
    OdfFamilyPropertiesPatternMatcher instance = new OdfFamilyPropertiesPatternMatcher(g);

    String result = OdfFamilyPropertiesPatternMatcher.asString(instance.getFamilyProperties());
    System.out.println("StyleFamily <=> Properties:\n" + result);
    if (!EXPECTED_ODF13_RESULT.equals(result))
      fail(
          "The reference and test result differ:\nExpected:\n"
              + EXPECTED_ODF13_RESULT
              + "\nTest:\n"
              + result);
  }
}
