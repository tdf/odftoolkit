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

import static schema2template.example.odf.ConstantsBuildEnv.GENERATION_TARGET_BASE_DIR;
import static schema2template.example.odf.ConstantsBuildEnv.TEMPLATE_BASE_DIR;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import schema2template.GenerationParameters;
import schema2template.SchemaToTemplate;
import schema2template.example.odf.ConstantsOdf.OdfSpecificationPart;

public class GenerationOdfdomPythonTest {

  private static final Logger LOG = Logger.getLogger(GenerationOdfdomPythonTest.class.getName());
  private static final String ODFDOM_PYTHON_DIRECTORY = "odfdom-python";
  private static final String MAIN_TEMPLATE_PATH =
      TEMPLATE_BASE_DIR + ODFDOM_PYTHON_DIRECTORY + File.separator + "file-creation-list.vm";

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  public void testAllExampleGenerations() {
    ArrayList<GenerationParameters> generations = new ArrayList<>();

    for (OdfSpecificationPart specPart : OdfSpecificationPart.values()) {
      LOG.info(
          "\n\nNew ODF transformation with following parameters:"
              + "\n\tgrammarVersion "
              + specPart.grammarVersion
              + "\n\tgrammarID: "
              + specPart.grammarID
              + "\n\tgrammarPath: "
              + specPart.grammarPath
              + "\n\tmainTemplatePath: "
              + MAIN_TEMPLATE_PATH
              + "\n\ttargetDirPath: "
              + GENERATION_TARGET_BASE_DIR
              + ODFDOM_PYTHON_DIRECTORY);

      generations.add(
          new GenerationParameters(
              specPart.grammarVersion,
              specPart.grammarID,
              specPart.grammarPath,
              null,
              MAIN_TEMPLATE_PATH,
              GENERATION_TARGET_BASE_DIR + ODFDOM_PYTHON_DIRECTORY));
    }

    try {
      SchemaToTemplate.run(generations);
    } catch (Exception e) {
      Assert.fail("Exception during test run: " + e.toString());
      throw new RuntimeException(e);
    }
    // Changing order of multiple puzzlepieces makes file comparison unuseable
    //    compareDirectories(
    //        GENERATION_TARGET_BASE_DIR + ODFDOM_PYTHON_DIRECTORY, GENERATION_REFERENCE_BASE_DIR
    // +
    // ODFDOM_PYTHON_DIRECTORY);
  }
}
