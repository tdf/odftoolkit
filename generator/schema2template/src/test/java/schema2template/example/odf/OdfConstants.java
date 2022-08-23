/**
 * *********************************************************************
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
 * <p>*****************************************************************
 */
package schema2template.example.odf;

import java.io.File;

/** Contains all test relevant constants related to ODF */
class OdfConstants {

  /**
   * Via Maven pom.xml (surefire test plugin) received System variable of the absolute path of the
   * base directory
   *
   * @see https://cwiki.apache.org/confluence/display/MAVEN/Maven+Properties+Guide
   *     <p>The absolute path to the pom directory of this submodule, which is relative to project
   *     root ./generator/schema2template
   */
  static final String BASE_DIR = System.getProperty("schema2template.base.dir") + File.separator;

  static final String ODF_GRAMMAR_PATH =
      BASE_DIR
          + "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test-input"
          + File.separator
          + "odf"
          + File.separator
          + "grammar"
          + File.separator;

  static final String ODF_1_3_SCHEMA_GRAMMAR = ODF_GRAMMAR_PATH + "OpenDocument-v1.3-schema.rng";
  static final String ODF_1_2_SCHEMA_GRAMMAR = ODF_GRAMMAR_PATH + "OpenDocument-v1.2-os-schema.rng";
  static final String ODF_1_1_SCHEMA_GRAMMAR = ODF_GRAMMAR_PATH + "OpenDocument-schema-v1.1.rng";
  static final String ODF_1_0_SCHEMA_GRAMMAR = ODF_GRAMMAR_PATH + "OpenDocument-schema-v1.0-os.rng";

  static final String ODF_1_3_PACKAGE_MANIFEST_GRAMMAR =
      ODF_GRAMMAR_PATH + "OpenDocument-v1.3-manifest-schema.rng";
  static final String ODF_1_2_PACKAGE_MANIFEST_GRAMMAR =
      ODF_GRAMMAR_PATH + "OpenDocument-v1.2-os-manifest-schema.rng";
  static final String ODF_1_1_PACKAGE_MANIFEST_GRAMMAR =
      ODF_GRAMMAR_PATH + "OpenDocument-manifest-schema-v1.1.rng";
  static final String ODF_1_0_PACKAGE_MANIFEST_GRAMMAR =
      ODF_GRAMMAR_PATH + "OpenDocument-manifest-schema-v1.0-os.rng";

  static final String ODF_1_3_PACKAGE_SIGNATURE_GRAMMAR =
      ODF_GRAMMAR_PATH + "OpenDocument-v1.3-dsig-schema.rng";
  static final String ODF_1_2_PACKAGE_SIGNATURE_GRAMMAR =
      ODF_GRAMMAR_PATH + "OpenDocument-v1.2-os-dsig-schema.rng";

  static final String ODF_VERSION_1_3 = "1.3";
  static final String ODF_VERSION_1_2 = "1.2";
  static final String ODF_VERSION_1_1 = "1.1";
  static final String ODF_VERSION_1_0 = "1.0";

  static final String ODF_SCHEMA_ID = "odf-schema";
  static final String ODF_PACKAGE_MANIFEST_ID = "odf-package-manifest";
  static final String ODF_PACKAGE_SIGNATURE_ID = "odf-package-digital-signature";

  /**
   * Expresses the amount of elements in ODF 1.1. There are some issues in the schema that have to
   * be fixed before the full number can be returned by MSV: Reference table-table-template is never
   * used, therefore several elements are not taking into account:: "table:body"
   * "table:even-columns" "table:even-rows" "table:first-column" "table:first-row"
   * "table:last-column" "table:last-row" "table:odd-columns" "table:odd-rows"
   * "table:table-template" NOTE: Ignoring the '*' there can be 525 elements parsed, but with fixed
   * schema it should be 535.
   */
  // ToDo: 535 - by search/Replace using RNGSchema and tools, prior exchange <name> to element or
  // attribute declaration
  static final int ODF11_ELEMENT_NUMBER = 526;

  static final int ODF12_ELEMENT_NUMBER = 599;

  static final int ODF13_ELEMENT_NUMBER = 606;
  /**
   * Expresses the amount of attributes in ODF 1.1. There are some issues in the schema that have to
   * be fixed before the full number can be returned by MSV: Following references are never used,
   * therefore its attribute is not taking into account:: draw-glue-points-attlist with
   * "draw:escape-direction" office-process-content with "office:process-content" (DEPRECATED in
   * ODF1.2 only on foreign elements)
   *
   * <p>Following attributes are member of the not referenced element "table:table-template":
   * "text:first-row-end-column" "text:first-row-start-column" "text:last-row-end-column"
   * "text:last-row-start-column" "text:paragraph-style-name"
   *
   * <p>NOTE: Ignoring the '*' there can be 1162 elements parsed, but with fixed schema it should be
   * 1169.
   */

  // ToDo: 1169 - by search/Replace using RNGSchema and tools, prior exchange <name> to element or
  // attribute declaration
  static final int ODF11_ATTRIBUTE_NUMBER = 1163;

  // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie.
  // @office:process-content)
  static final int ODF12_ATTRIBUTE_NUMBER = 1301;

  // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie.
  // @office:process-content)
  static final int ODF13_ATTRIBUTE_NUMBER = 1317;
  static final int ODF13_ELEMENT_DUPLICATES = 7;
  static final int ODF13_ATTRIBUTE_DUPLICATES = 117;

  // The Maven default output directory for generated sources: target/generated-sources/
  static final String TARGET_BASE_DIR =
      BASE_DIR
          + "target"
          + File.separator
          + "generated-sources"
          + File.separator
          + "java"
          + File.separator
          + "odf"
          + File.separator
          + "odfdom-java"
          + File.separator;

  static final String ODF_TEMPLATE_DIR =
      BASE_DIR
          + "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test-input"
          + File.separator
          + "odf"
          + File.separator
          + "template"
          + File.separator
          + "odfdom-java"
          + File.separator;

  static final String GRAMMAR_ADDITIONS_DOM_FILE =
      ODF_TEMPLATE_DIR + File.separator + "dom" + File.separator + "grammar-additions.xml";
  static final String GRAMMAR_ADDITIONS_PKG_FILE =
      ODF_TEMPLATE_DIR + File.separator + "pkg" + File.separator + "grammar-additions.xml";

  static final String MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE =
      ODF_TEMPLATE_DIR
          + File.separator
          + "pkg"
          + File.separator
          + "template"
          + File.separator
          + "pkg-manifest-main-template.vm";
  static final String MAIN_TEMPLATE_ODF_PACKAGE_SIGNATURE_FILE =
      ODF_TEMPLATE_DIR
          + File.separator
          + "pkg"
          + File.separator
          + "template"
          + File.separator
          + "pkg-dsig-main-template.vm";
  static final String MAIN_TEMPLATE_ODF_SCHEMA_FILE =
      ODF_TEMPLATE_DIR
          + File.separator
          + "dom"
          + File.separator
          + "template"
          + File.separator
          + "java-odfdom-main-template.vm";

  static final String GENERATED_ODFDOM_REFERENCE =
      BASE_DIR
          + "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "test-reference"
          + File.separator
          + "odf"
          + File.separator
          + "odfdom-java"
          + File.separator;
}
