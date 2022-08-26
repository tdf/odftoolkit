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

import static schema2template.example.odf.OdfConstants.REFERENCE_BASE_DIR;
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
import schema2template.example.odf.OdfConstants.OdfSpecificationPart;

public class OdfGenerationTest {

  private static final Logger LOG = Logger.getLogger(OdfGenerationTest.class.getName());

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  @Ignore
  public void testAllExampleGenerations() {
    ArrayList<GenerationParameters> generations = new ArrayList<>();

    for (OdfSpecificationPart specPart : OdfSpecificationPart.values()) {
      LOG.info(
          "New ODF transformation with following parameters:"
              + "\ngrammarVersion "
              + specPart.grammarVersion
              + "\ngrammarID: "
              + specPart.grammarID
              + "\ngrammarPath: "
              + specPart.grammarPath
              + "\ngrammarAdditionsPath: "
              + specPart.grammarAdditionsPath
              + "\nmainTemplatePath: "
              + specPart.mainTemplatePath
              + "\ntargetDirPath: "
              + specPart.targetDirPath);

      generations.add(
          new GenerationParameters(
              specPart.grammarVersion,
              specPart.grammarID,
              specPart.grammarPath,
              specPart.grammarAdditionsPath,
              specPart.mainTemplatePath,
              specPart.targetDirPath));
    }

    SchemaToTemplate.run(generations);
    compareDirectories(TARGET_BASE_DIR + "odfdom-java", REFERENCE_BASE_DIR + "odfdom-java");
  }

  public static boolean compareDirectories(String newFileDir, String RefFileDir) {
    boolean directoriesEqual = true;
    try {
      // ******** Reference Test *************
      // generated sources must be equal to the previously generated reference sources
      String targetPath = Paths.get(newFileDir).toAbsolutePath().toString();
      String referencePath = Paths.get(RefFileDir).toAbsolutePath().toString();

      LOG.log(
          Level.INFO,
          "\n\nComparing new generated Files:\n\t{0}\nwith their reference:\n\t{1}\n",
          new Object[] {targetPath, referencePath});
      directoriesEqual =
          DirectoryCompare.directoryContentEquals(Paths.get(newFileDir), Paths.get(RefFileDir));
      Assert.assertTrue(
          "The new generated sources\n\t"
              + targetPath
              + "\ndiffer from their reference:\n\t"
              + referencePath,
          directoriesEqual);
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
    return directoriesEqual;
  }
}
