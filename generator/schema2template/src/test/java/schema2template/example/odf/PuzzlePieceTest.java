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
package schema2template.example.odf;

import static schema2template.example.odf.OdfConstants.REFERENCE_BASE_DIR;
import static schema2template.example.odf.OdfConstants.TARGET_BASE_DIR;
import static schema2template.example.odf.OdfGenerationTest.compareDirectories;
import static schema2template.example.odf.SchemaToTemplate.DEBUG;

import com.sun.msv.grammar.Expression;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import schema2template.example.odf.OdfConstants.OdfSpecificationPart;
import schema2template.model.MSVExpressionIterator;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;
import schema2template.model.XMLModel;

/** Loads each ODF Grammar into the MSV and dumps its model a file. */
public class PuzzlePieceTest {

  private static final Logger LOG = Logger.getLogger(PuzzlePieceTest.class.getName());

  private static final String MSV_DUMP_DIRECTORY_SUFFIX = "msv-dump";

  @Before
  public void intialize() {
    new File(TARGET_BASE_DIR + MSV_DUMP_DIRECTORY_SUFFIX).mkdirs();
  }

  /**
   * Test: Use the MSV
   *
   * <p>This test uses the ODF example, but it's meant to test the general ability to correctly
   * extract PuzzlePieces out of a XML schema
   */
  @Test
  public void testMSVExpressionTree() {
    try {
      for (OdfSpecificationPart specPart : OdfSpecificationPart.values()) {
        LOG.info(
            "\n\nNew ODF grammar runtime serialization (MSV dump) for regression test:"
                + "\n\tgrammarVersion "
                + specPart.grammarVersion
                + "\n\tgrammarID: "
                + specPart.grammarID
                + "\n\tgrammarPath: "
                + specPart.grammarPath
                + "\n\tgrammarAdditionsPath: "
                + specPart.grammarAdditionsPath
                + "\n\tmainTemplatePath: "
                + specPart.mainTemplatePath
                + "\n\ttargetDirPath: "
                + specPart.targetDirPath);
        Expression odfRoot = XMLModel.loadSchema(specPart.grammarPath).getTopLevel();
        String odfDump = MSVExpressionIterator.dumpMSVExpressionTree(odfRoot);

        String grammarLabel = specPart.grammarID + "-" + specPart.grammarVersion;

        String targetMSVDumpFile =
            TARGET_BASE_DIR
                + MSV_DUMP_DIRECTORY_SUFFIX
                + File.separator
                + grammarLabel
                + "-msvtree.txt";
        LOG.log(
            Level.INFO,
            "Writing MSV RelaxNG tree for " + grammarLabel + " + into file: {0}",
            targetMSVDumpFile);
        try (PrintWriter out = new PrintWriter(new FileWriter(targetMSVDumpFile))) {
          out.print(odfDump);
        }
      }
      compareDirectories(
          TARGET_BASE_DIR + MSV_DUMP_DIRECTORY_SUFFIX,
          REFERENCE_BASE_DIR + MSV_DUMP_DIRECTORY_SUFFIX);
    } catch (Exception ex) {
      Logger.getLogger(PuzzlePieceTest.class.getName()).log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }

  /**
   * Test: Create PuzzlePiece elements and attributes with ODF Spec 1.1 (old version, won't be
   * changed, so it's a good base for a test).
   *
   * <p>This test uses the ODF example, but it's meant to test the general ability to correctly
   * extract PuzzlePieces out of a XML schema
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
                + "\n\tgrammarAdditionsPath: "
                + specPart.grammarAdditionsPath
                + "\n\tmainTemplatePath: "
                + specPart.mainTemplatePath
                + "\n\ttargetDirPath: "
                + specPart.targetDirPath);

        PuzzlePieceSet allElements = new PuzzlePieceSet();
        PuzzlePieceSet allAttributes = new PuzzlePieceSet();
        PuzzlePiece.extractPuzzlePieces(
            XMLModel.loadSchema(specPart.grammarPath),
            allElements,
            allAttributes,
            specPart.grammarPath);
        // There is a difference of one wildcard "*" representing anyElement/anyAttribute

        String grammarLabel = "'" + specPart.grammarID + " " + specPart.grammarVersion + "'";

        foundError |=
            checkFoundNumber(
                allElements, specPart.elementDuplicateNo, grammarLabel, "elements with duplicates");
        foundError |=
            checkFoundNumber(
                allElements.withoutMultiples(),
                specPart.elementNo,
                grammarLabel,
                "elements without duplicates");
        foundError |=
            checkFoundNumber(
                allAttributes,
                specPart.attributeDuplicateNo,
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

    } catch (Exception ex) {
      Logger.getLogger(PuzzlePieceTest.class.getName()).log(Level.SEVERE, null, ex);
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
