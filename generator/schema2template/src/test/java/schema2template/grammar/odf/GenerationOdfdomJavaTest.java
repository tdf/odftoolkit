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
 * <p>******************************************************************
 */
package schema2template.grammar.odf;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import schema2template.GenerationParameters;
import schema2template.SchemaToTemplate;

public class GenerationOdfdomJavaTest {

  private static final Logger LOG = Logger.getLogger(GenerationOdfdomJavaTest.class.getName());
  private static final String ODFDOM_JAVA_DIRECTORY = "odfdom-java";

  // ***********************************
  // ***** MAIN TEMPLATES
  // ***********************************
  /** The absolute path to inital template that will afterwards a list of all to be created files */
  private static final String MAIN_TEMPLATE_ODF_SCHEMA_FILE =
      ConstantsBuildEnv.TEMPLATE_BASE_DIR
          + ODFDOM_JAVA_DIRECTORY
          + File.separator
          + "dom"
          + File.separator
          + "template"
          + File.separator
          + "file-creation-list_odf-schema.vm";

  private static final String MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE =
      ConstantsBuildEnv.TEMPLATE_BASE_DIR
          + ODFDOM_JAVA_DIRECTORY
          + File.separator
          + "pkg"
          + File.separator
          + "template"
          + File.separator
          + "file-creation-list_odf-package-manifest.vm";
  /**
   * Each ODF part has its own grammar and an own template to create typed Java DOM files from, this
   * is the ODF digital signature
   */
  private static final String MAIN_TEMPLATE_ODF_PACKAGE_SIGNATURE_FILE =
      ConstantsBuildEnv.TEMPLATE_BASE_DIR
          + ODFDOM_JAVA_DIRECTORY
          + File.separator
          + "pkg"
          + File.separator
          + "template"
          + File.separator
          + "file-creation-list_odf-package-digital-signature.vm";

  // ***********************************
  // ***** GRAMMAR ADDITIONS
  // ***********************************
  /**
   * the absolute path to the file containing additional information for the generation aside the
   * grammar
   */
  private static final String GRAMMAR_ADDITIONS_FILE__SCHEMA =
      ConstantsBuildEnv.TEMPLATE_BASE_DIR
          + ODFDOM_JAVA_DIRECTORY
          + File.separator
          + "dom"
          + File.separator
          + "grammar-additions.xml";

  private static final String GRAMMAR_ADDITIONS_FILE__PACKAGE =
      ConstantsBuildEnv.TEMPLATE_BASE_DIR
          + ODFDOM_JAVA_DIRECTORY
          + File.separator
          + "pkg"
          + File.separator
          + "grammar-additions.xml";

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  public void testAllExampleGenerations() {
    ArrayList<GenerationParameters> generations = new ArrayList<>();

    String grammarAdditionsPath = null;
    String mainTemplatePath = null;
    for (ConstantsOdf.OdfSpecificationPart specPart : ConstantsOdf.OdfSpecificationPart.values()) {
      if (specPart.grammarID.equals(ConstantsOdf.GrammarID.ODF_MANIFEST.ID)) {
        //  ODF manifest grammar
        grammarAdditionsPath = GRAMMAR_ADDITIONS_FILE__PACKAGE;
        mainTemplatePath = MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE;

      } else if (specPart.grammarID.equals(ConstantsOdf.GrammarID.ODF_SIGNATURE.ID)) {
        // ODF signature grammar
        grammarAdditionsPath = GRAMMAR_ADDITIONS_FILE__PACKAGE;
        mainTemplatePath = MAIN_TEMPLATE_ODF_PACKAGE_SIGNATURE_FILE;

      } else if (specPart.grammarID.equals(ConstantsOdf.GrammarID.ODF_SCHEMA.ID)) {
        // ODF schema grammar
        grammarAdditionsPath = GRAMMAR_ADDITIONS_FILE__SCHEMA;
        mainTemplatePath = MAIN_TEMPLATE_ODF_SCHEMA_FILE;
      }
      // if (specPart.grammarID.equals(ConstantsOdf.GrammarID.ODF_SCHEMA.ID)) {
      LOG.info(
          "\n\nNew ODF transformation with following parameters:"
              + "\n\tgrammarVersion "
              + specPart.grammarVersion
              + "\n\tgrammarID: "
              + specPart.grammarID
              + "\n\tgrammarPath: "
              + specPart.grammarPath
              + "\n\tgrammarAdditionsPath: "
              + grammarAdditionsPath
              + "\n\tmainTemplatePath: "
              + mainTemplatePath
              + "\n\ttargetDirPath: "
              + ConstantsBuildEnv.GENERATION_TARGET_BASE_DIR
              + ODFDOM_JAVA_DIRECTORY);

      generations.add(
          new GenerationParameters(
              specPart.grammarVersion,
              specPart.grammarID,
              specPart.grammarPath,
              grammarAdditionsPath,
              mainTemplatePath,
              ConstantsBuildEnv.GENERATION_TARGET_BASE_DIR + ODFDOM_JAVA_DIRECTORY));
    }
    // }

    try {
      SchemaToTemplate.run(generations);
    } catch (Exception e) {
      Assert.fail("Exception during test run: " + e.toString());
      throw new RuntimeException(e);
    }
    DirectoryCompare.compareDirectories(
        ConstantsBuildEnv.GENERATION_TARGET_BASE_DIR + ODFDOM_JAVA_DIRECTORY,
        ConstantsBuildEnv.GENERATION_REFERENCE_BASE_DIR + ODFDOM_JAVA_DIRECTORY);
  }
}
