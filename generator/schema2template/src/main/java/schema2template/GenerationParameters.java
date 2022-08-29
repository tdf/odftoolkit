/**
 * **********************************************************************
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
 * <p>********************************************************************
 */
package schema2template;

import java.nio.file.Paths;

public class GenerationParameters {

  /** @Parameter @required */
  private String grammarVersion;

  /** @Parameter @required */
  private String grammarID;

  /** @Parameter @required */
  private String grammarPath;

  /** @Parameter @optional */
  private String grammarAdditionsPath;

  /** @Parameter @required */
  private String mainTemplatePath;

  /** @Parameter @required */
  private String targetDirPath;

  public GenerationParameters() {
    // System.err.println("GenerationParameters have been created!!");
  }

  /**
   * @param grammarVersion the version number of grammar
   * @param grammarID the ID label of the grammar (without the version)
   * @param grammarPath the path to the grammar file relative to the project base directory (of the
   *     calling pom.xml)
   * @param grammarAdditionsPath the path to the file with additional information to the grammar,
   *     e.g. ODF default values are listed in the ODF specificaiton
   * @param mainTemplatePath the path to the velocity file that maps the grammar to a list of
   *     velocity templates and their condition triggering them
   * @param targetDirPath the output directory of the generation (usually target/generated-sources/)
   */
  public GenerationParameters(
      String grammarVersion,
      String grammarID,
      String grammarPath,
      String grammarAdditionsPath,
      String mainTemplatePath,
      String targetDirPath) {
    this.grammarVersion = grammarVersion;
    this.grammarID = grammarID;
    this.grammarPath = Paths.get(grammarPath).normalize().toAbsolutePath().toString();
    if (grammarAdditionsPath != null) {
      this.grammarAdditionsPath =
          Paths.get(grammarAdditionsPath).normalize().toAbsolutePath().toString();
    }
    this.mainTemplatePath = Paths.get(mainTemplatePath).normalize().toAbsolutePath().toString();
    this.targetDirPath = Paths.get(targetDirPath).normalize().toAbsolutePath().toString();
  }

  /**
   * @return the version of the grammar. Helpful to document changes between versions in
   *     documentation
   */
  public String getGrammarVersion() {
    return grammarVersion;
  }

  /**
   * @return the ID of the grammar file. For instance, in ODF there are different sub-modules like
   *     package manifest, digitial signature for package and the ODF schema.
   */
  public String getGrammarID() {
    return grammarID;
  }

  /** @return the path to the grammar file */
  public String getGrammarPath() {
    return grammarPath;
  }

  /**
   * @return the path to the grammar-add-on file. This file holds erquired information in the
   *     specification but not in the XML grammar, e.g. default values (as they were inserted during
   *     load of the grammar by some XML parser and were blowing up the DOM. Another example for
   *     add-on information not in the grammar but in the specificaiton are the style families.
   */
  public String getGrammarAdditionsPath() {
    return grammarAdditionsPath;
  }

  /**
   * @return the path to the grammar2template velocity file. It maps the grammar to a set of
   *     template files together with the condition and loops that triggering the data
   *     transformation!
   */
  public String getMainTemplatePath() {
    return mainTemplatePath;
  }

  /** @return the path to the grammar2template velocity file.! */
  public String getTargetDir() {
    return targetDirPath;
  }
}
