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
package schema2template.grammar;

import static org.junit.Assert.fail;

import com.sun.msv.grammar.Grammar;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import schema2template.grammar.ConstantsOdf.OdfSpecificationPart;

/**
 * @author Svante Schubert Tests the finding the ODF family pattern of the ODF grammar within the
 *     MSV expression tree, see <code>OdfFamilyPropertiesPatternMatcher</code> for more details!
 */
public class MSVPatternMatcherTest {

  /** String was taken previously from OdfFamilyPropertiesPatternMatcher.toString() */
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

  public MSVPatternMatcherTest() {}

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
  public void testGetProperties() {
    Grammar g = XMLModel.loadSchema(OdfSpecificationPart.ODF_1_2_SCHEMA.grammarPath);
    OdfFamilyPropertiesPatternMatcher instance = new OdfFamilyPropertiesPatternMatcher(g);
    String result = instance.toString();
    System.out.println("StyleFamily <=> Properties:\n" + result);
    if (!EXPECTED_ODF13_RESULT.equals(result))
      fail(
          "The reference and test result differ:\nExpected:\n"
              + EXPECTED_ODF13_RESULT
              + "\nTest:\n"
              + result);
  }
}
