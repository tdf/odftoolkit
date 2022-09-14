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
package schema2template.grammar;

import java.io.File;

/** Contains all test relevant constants related to ODF */
class ConstantsBuildEnv {

  /**
   * Via Maven pom.xml (surefire test plugin) received System variable of the absolute path of the
   * base directory
   *
   * <p>{@ref https://cwiki.apache.org/confluence/display/MAVEN/Maven+Properties+Guide}
   *
   * <p>The absolute path to the pom directory of this submodule, which is relative to project root
   * ./generator/schema2template
   */
  static final String BASE_DIR = System.getProperty("schema2template.base.dir") + File.separator;

  static final String TEMPLATE_BASE_DIR =
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
          + "generation"
          + File.separator;

  /** base directory for all ODF specifications grammars in the ODF Toolkit */
  static final String ODF_GRAMMAR_BASE_DIR =
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

  // The Maven default output directory for generated sources is target/generated-sources/
  // But the generated Java files still need context not being generated and want compile t
  // Therefore, target/odf
  static final String TARGET_BASE_DIR =
      BASE_DIR + "target" + File.separator + "odf" + File.separator;

  // BTW the Maven default output directory for generated sources is target/generated-sources/
  static final String GENERATION_TARGET_BASE_DIR = TARGET_BASE_DIR + "generation" + File.separator;

  /** The base dir of all references to be compared with the new created test artefacts */
  static final String REFERENCE_BASE_DIR =
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
          + File.separator;

  static final String GENERATION_REFERENCE_BASE_DIR =
      REFERENCE_BASE_DIR + "generation" + File.separator;
}
