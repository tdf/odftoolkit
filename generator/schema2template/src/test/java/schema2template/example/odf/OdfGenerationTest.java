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
package schema2template.example.odf;

import static schema2template.example.odf.OdfConstants.GENERATED_ODFDOM_REFERENCE;
import static schema2template.example.odf.OdfConstants.GRAMMAR_ADDITIONS_DOM_FILE;
import static schema2template.example.odf.OdfConstants.GRAMMAR_ADDITIONS_PKG_FILE;
import static schema2template.example.odf.OdfConstants.MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE;
import static schema2template.example.odf.OdfConstants.MAIN_TEMPLATE_ODF_PACKAGE_SIGNATURE_FILE;
import static schema2template.example.odf.OdfConstants.MAIN_TEMPLATE_ODF_SCHEMA_FILE;
import static schema2template.example.odf.OdfConstants.ODF_1_0_PACKAGE_MANIFEST_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_0_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_1_PACKAGE_MANIFEST_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_1_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_2_PACKAGE_MANIFEST_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_2_PACKAGE_SIGNATURE_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_2_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_3_PACKAGE_MANIFEST_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_3_PACKAGE_SIGNATURE_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_3_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_PACKAGE_MANIFEST_ID;
import static schema2template.example.odf.OdfConstants.ODF_PACKAGE_SIGNATURE_ID;
import static schema2template.example.odf.OdfConstants.ODF_SCHEMA_ID;
import static schema2template.example.odf.OdfConstants.ODF_VERSION_1_0;
import static schema2template.example.odf.OdfConstants.ODF_VERSION_1_1;
import static schema2template.example.odf.OdfConstants.ODF_VERSION_1_2;
import static schema2template.example.odf.OdfConstants.ODF_VERSION_1_3;
import static schema2template.example.odf.OdfConstants.TARGET_BASE_DIR;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import schema2template.GenerationParameters;

public class OdfGenerationTest {

  private static final Logger LOG = Logger.getLogger(OdfGenerationTest.class.getName());

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  @Ignore
  public void testAllExampleGenerations() {
    try {

      ArrayList<GenerationParameters> generations = new ArrayList<>();

      // ******** ODF 1.3 *************
      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_3,
              ODF_SCHEMA_ID,
              ODF_1_3_SCHEMA_GRAMMAR,
              GRAMMAR_ADDITIONS_DOM_FILE,
              MAIN_TEMPLATE_ODF_SCHEMA_FILE,
              TARGET_BASE_DIR));

      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_3,
              ODF_PACKAGE_MANIFEST_ID,
              ODF_1_3_PACKAGE_MANIFEST_GRAMMAR,
              GRAMMAR_ADDITIONS_PKG_FILE,
              MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE,
              TARGET_BASE_DIR));

      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_3,
              ODF_PACKAGE_SIGNATURE_ID,
              ODF_1_3_PACKAGE_SIGNATURE_GRAMMAR,
              GRAMMAR_ADDITIONS_PKG_FILE,
              MAIN_TEMPLATE_ODF_PACKAGE_SIGNATURE_FILE,
              TARGET_BASE_DIR));

      // ******** ODF 1.2 *************
      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_2,
              ODF_SCHEMA_ID,
              ODF_1_2_SCHEMA_GRAMMAR,
              GRAMMAR_ADDITIONS_DOM_FILE,
              MAIN_TEMPLATE_ODF_SCHEMA_FILE,
              TARGET_BASE_DIR));

      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_2,
              ODF_PACKAGE_MANIFEST_ID,
              ODF_1_2_PACKAGE_MANIFEST_GRAMMAR,
              GRAMMAR_ADDITIONS_PKG_FILE,
              MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE,
              TARGET_BASE_DIR));

      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_2,
              ODF_PACKAGE_SIGNATURE_ID,
              ODF_1_2_PACKAGE_SIGNATURE_GRAMMAR,
              GRAMMAR_ADDITIONS_PKG_FILE,
              MAIN_TEMPLATE_ODF_PACKAGE_SIGNATURE_FILE,
              TARGET_BASE_DIR));

      // ******** ODF 1.1 *************
      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_1,
              ODF_SCHEMA_ID,
              ODF_1_1_SCHEMA_GRAMMAR,
              GRAMMAR_ADDITIONS_DOM_FILE,
              MAIN_TEMPLATE_ODF_SCHEMA_FILE,
              TARGET_BASE_DIR));

      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_1,
              ODF_PACKAGE_MANIFEST_ID,
              ODF_1_1_PACKAGE_MANIFEST_GRAMMAR,
              GRAMMAR_ADDITIONS_PKG_FILE,
              MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE,
              TARGET_BASE_DIR));

      // ******** ODF 1.0 *************
      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_0,
              ODF_SCHEMA_ID,
              ODF_1_0_SCHEMA_GRAMMAR,
              GRAMMAR_ADDITIONS_DOM_FILE,
              MAIN_TEMPLATE_ODF_SCHEMA_FILE,
              TARGET_BASE_DIR));
      generations.add(
          new GenerationParameters(
              ODF_VERSION_1_0,
              ODF_PACKAGE_MANIFEST_ID,
              ODF_1_0_PACKAGE_MANIFEST_GRAMMAR,
              GRAMMAR_ADDITIONS_PKG_FILE,
              MAIN_TEMPLATE_ODF_PACKAGE_MANIFEST_FILE,
              TARGET_BASE_DIR));

      SchemaToTemplate.run(generations);

      // ******** Reference Test *************
      // generated sources must be equal to the previously generated reference sources
      String targetPath = Paths.get(TARGET_BASE_DIR).toAbsolutePath().toString();
      String referencePath = Paths.get(GENERATED_ODFDOM_REFERENCE).toAbsolutePath().toString();

      LOG.log(
          Level.INFO,
          "\n\nComparing new generated Files:\n\t{0}\nwith their reference:\n\t{1}\n",
          new Object[] {
            Paths.get(TARGET_BASE_DIR).toAbsolutePath().toString(),
            Paths.get(GENERATED_ODFDOM_REFERENCE).toAbsolutePath().toString()
          });
      Assert.assertTrue(
          "The new generated sources\n\t"
              + targetPath
              + "\ndiffer from their reference:\n\t"
              + referencePath,
          DirectoryCompare.directoryContentEquals(
              Paths.get(TARGET_BASE_DIR), Paths.get(GENERATED_ODFDOM_REFERENCE)));
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }
}
