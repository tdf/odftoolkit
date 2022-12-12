/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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
 * <p>*********************************************************************
 */
package schema2template.grammar;

import static schema2template.SchemaToTemplate.DEBUG;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import schema2template.grammar.ConstantsOdf.OdfSpecificationPart;

/**
 * Loading each ODF Grammar into the MSV using our PuzzlePiece class and testing the amount of
 * elements and attributes of each ODF grammar part and generating GraphML serialization of the MSV
 * Expression Tree.
 */
public class MSVGraphMLExportTest {

  private static final Logger LOG = Logger.getLogger(MSVGraphMLExportTest.class.getName());

  private static final String GRAPHML_DIRECTORY = "graphml";

  /**
   * This regression test iterates over all ODF grammars loads them into the MSV Validator and is
   * comparing the amount of elements and attributes of each ODF grammar part with earlier results.
   */
  @Test
  // due to issue https://issues.apache.org/jira/browse/ODFTOOLKIT-180
  public void testExtractPuzzlePieces() {
    try {
      boolean foundError = Boolean.FALSE;
      for (OdfSpecificationPart specPart : OdfSpecificationPart.values()) {
        LOG.info(
            "\n\nNew ODF PuzzlePiece extractions with following parameters:"
                + "\n\tgrammarVersion "
                + specPart.grammarVersion
                + "\n\tgrammarID: "
                + specPart.grammarID
                + "\n\tgrammarPath: "
                + specPart.grammarPath
                + "\n\ttargetDirPath: "
                + ConstantsBuildEnv.TARGET_BASE_DIR
                + GRAPHML_DIRECTORY);

        PuzzlePieceSet allElements = new PuzzlePieceSet();
        PuzzlePieceSet allAttributes = new PuzzlePieceSet();
        PuzzlePiece.extractPuzzlePieces(
            XMLModel.loadSchema(specPart.grammarPath),
            allElements,
            allAttributes,
            specPart.grammarPath,
            ConstantsBuildEnv.TARGET_BASE_DIR
                + GRAPHML_DIRECTORY
                + File.separator
                + specPart.grammarID
                + "-"
                + specPart.grammarVersion);
        // There is a difference of one wildcard "*" representing anyElement/anyAttribute

        String grammarLabel = "'" + specPart.grammarID + " " + specPart.grammarVersion + "'";

        foundError |=
            checkFoundNumber(
                allElements,
                specPart.elementNoWithDuplicates,
                grammarLabel,
                "elements with duplicates");
        foundError |=
            checkFoundNumber(
                allElements.withoutMultiples(),
                specPart.elementNo,
                grammarLabel,
                "elements without duplicates");
        foundError |=
            checkFoundNumber(
                allAttributes,
                specPart.attributeNoWithDuplicates,
                grammarLabel,
                "attributes with duplicates");
        foundError |=
            checkFoundNumber(
                allAttributes.withoutMultiples(),
                specPart.attributeNo,
                grammarLabel,
                "attributes without duplicates");
      }
      if (foundError)
        Assert.fail(
            "The amount of XML nodes were calculated differently by MSV. See earlier error messages!");

      // DISABLED due to the ALL_ELEMENTS.graphml file, which differs due to random ordered
      // namespace definitions
      //      compareDirectories(
      //          TARGET_BASE_DIR + GRAPHML_DIRECTORY,
      //          REFERENCE_BASE_DIR + GRAPHML_DIRECTORY);
    } catch (Exception ex) {
      Logger.getLogger(MSVGraphMLExportTest.class.getName()).log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }

  /**
   * Routine to compare the expected number of either attributes or elements with the found amount
   */
  private boolean checkFoundNumber(
      PuzzlePieceSet puzzlePieceSet, int expectedAmount, String versionLabel, String nodeType) {
    boolean foundError = Boolean.FALSE;
    if (expectedAmount == puzzlePieceSet.size()) {
      LOG.log(
          Level.FINEST,
          "The expected amount of {0}s could be found in {1}!",
          new Object[] {nodeType, versionLabel});
      if (DEBUG) {
        int i = 0;
        for (PuzzlePiece piece : puzzlePieceSet) {
          LOG.log(Level.INFO, "{0} was {1} #{2}", new Object[] {piece.getQName(), nodeType, ++i});
        }
        LOG.info("++++++++++++");
      }
    } else {
      String errorMsg =
          "Instead of "
              + expectedAmount
              + " there were "
              + puzzlePieceSet.size()
              + " "
              + nodeType
              + "s found in "
              + versionLabel;
      LOG.severe(errorMsg);
      int i = 0;
      for (PuzzlePiece piece : puzzlePieceSet) {
        LOG.log(Level.SEVERE, "{0} was {1} #{2}", new Object[] {piece.getQName(), nodeType, ++i});
      }
      LOG.info("********************");
      foundError = Boolean.TRUE;
    }
    return foundError;
  }
}
