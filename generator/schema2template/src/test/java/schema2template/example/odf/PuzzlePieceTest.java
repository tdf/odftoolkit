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

import static schema2template.example.odf.OdfConstants.BASE_DIR;
import static schema2template.example.odf.OdfConstants.ODF13_ATTRIBUTE_DUPLICATES;
import static schema2template.example.odf.OdfConstants.ODF13_ATTRIBUTE_NUMBER;
import static schema2template.example.odf.OdfConstants.ODF13_ELEMENT_DUPLICATES;
import static schema2template.example.odf.OdfConstants.ODF13_ELEMENT_NUMBER;
import static schema2template.example.odf.OdfConstants.ODF_1_0_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_1_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_2_SCHEMA_GRAMMAR;
import static schema2template.example.odf.OdfConstants.ODF_1_3_SCHEMA_GRAMMAR;
import static schema2template.example.odf.SchemaToTemplate.DEBUG;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import schema2template.model.MSVExpressionIterator;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;
import schema2template.model.XMLModel;

/** Loads each ODF Grammar into the MSV and dumps its model a file. */
public class PuzzlePieceTest {

  private static final Logger LOG = Logger.getLogger(PuzzlePieceTest.class.getName());

  private static final String TARGET_ROOT_MSV_DUMP =
      BASE_DIR
          + File.separator
          + "target"
          + File.separator
          + "generated-sources"
          + File.separator
          + "java"
          + File.separator
          + "odf"
          + File.separator
          + "msv-dump"
          + File.separator;

  // *********** Multi Schema Validator Model Dumps of RunTime
  private static final String TARGET_DUMP_FILE_ODF10 =
      TARGET_ROOT_MSV_DUMP + File.separator + "odf10-msvtree.dump";
  private static final String TARGET_DUMP_FILE_ODF11 =
      TARGET_ROOT_MSV_DUMP + File.separator + "odf11-msvtree.dump";
  private static final String TARGET_DUMP_FILE_ODF12 =
      TARGET_ROOT_MSV_DUMP + File.separator + "odf12-msvtree.dump";
  private static final String TARGET_DUMP_FILE_ODF13 =
      TARGET_ROOT_MSV_DUMP + File.separator + "odf13-msvtree.dump";

  private static final String MSV_DUMP_TEST_REFERENCE_DIR =
      BASE_DIR
          + File.separator
          + "target"
          + File.separator
          + "test-classes"
          + File.separator
          + "test-reference"
          + File.separator
          + "odf"
          + File.separator
          + "msv-dump";

  private static final String OUTPUT_REF_ODF10 =
      MSV_DUMP_TEST_REFERENCE_DIR + File.separator + "odf10-msvtree.ref";
  private static final String OUTPUT_REF_ODF11 =
      MSV_DUMP_TEST_REFERENCE_DIR + File.separator + "odf11-msvtree.ref";
  private static final String OUTPUT_REF_ODF12 =
      MSV_DUMP_TEST_REFERENCE_DIR + File.separator + "odf12-msvtree.ref";
  private static final String OUTPUT_REF_ODF13 =
      MSV_DUMP_TEST_REFERENCE_DIR + File.separator + "odf13-msvtree.ref";

  @Before
  public void intialize() {
    new File(TARGET_ROOT_MSV_DUMP).mkdirs();
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
      Expression odf10Root = loadSchemaODF10().getTopLevel();
      String odf10Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf10Root);
      LOG.log(
          Level.INFO,
          "Writing MSV RelaxNG tree for ODF 1.0 into file: {0}",
          TARGET_DUMP_FILE_ODF10);
      try (PrintWriter out0 = new PrintWriter(new FileWriter(TARGET_DUMP_FILE_ODF10))) {
        out0.print(odf10Dump);
      }

      Expression odf11Root = loadSchemaODF11().getTopLevel();
      String odf11Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf11Root);
      LOG.log(
          Level.INFO,
          "Writing MSV RelaxNG tree for ODF 1.1 into file: {0}",
          TARGET_DUMP_FILE_ODF11);
      PrintWriter out1 = new PrintWriter(new FileWriter(TARGET_DUMP_FILE_ODF11));
      out1.print(odf11Dump);
      out1.close();

      Expression odf12Root = loadSchemaODF12().getTopLevel();
      String odf12Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf12Root);
      LOG.log(
          Level.INFO,
          "Writing MSV RelaxNG tree for ODF 1.2 into file: {0}",
          TARGET_DUMP_FILE_ODF12);
      PrintWriter out2 = new PrintWriter(new FileWriter(TARGET_DUMP_FILE_ODF12));
      out2.print(odf12Dump);
      out2.close();

      Expression odf13Root = loadSchemaODF13().getTopLevel();
      String odf13Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf13Root);
      LOG.log(
          Level.INFO,
          "Writing MSV RelaxNG tree for ODF 1.3 into file: {0}",
          TARGET_DUMP_FILE_ODF13);
      PrintWriter out3 = new PrintWriter(new FileWriter(TARGET_DUMP_FILE_ODF13));
      out3.print(odf13Dump);
      out3.close();

      String odf10Ref = readFileAsString(OUTPUT_REF_ODF10);
      if (!odf10Ref.equals(odf10Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.0 tree.\n"
                + "Please compare the output:\n\t'"
                + TARGET_DUMP_FILE_ODF10
                + "'\nwith the reference\n\t'"
                + OUTPUT_REF_ODF10;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }

      String odf11Ref = readFileAsString(OUTPUT_REF_ODF11);
      if (!odf11Ref.equals(odf11Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.1 tree.\n"
                + "Please compare the output:\n\t'"
                + TARGET_DUMP_FILE_ODF11
                + "'\nwith the reference\n\t'"
                + OUTPUT_REF_ODF11;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }

      String odf12Ref = readFileAsString(OUTPUT_REF_ODF12);
      if (!odf12Ref.equals(odf12Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.2 tree.\n"
                + "Please compare the output:\n\t'"
                + TARGET_DUMP_FILE_ODF12
                + "'\nwith the reference\n\t'"
                + OUTPUT_REF_ODF12;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }
      String odf13Ref = readFileAsString(OUTPUT_REF_ODF13);
      if (!odf13Ref.equals(odf13Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.3 tree.\n"
                + "Please compare the output:\n\t'"
                + TARGET_DUMP_FILE_ODF13
                + "'\nwith the reference\n\t'"
                + OUTPUT_REF_ODF13;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }
    } catch (Exception ex) {
      Logger.getLogger(PuzzlePieceTest.class.getName()).log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }

  /**
   * Reading a file into a string
   *
   * @param filePath path of the file to be opened.
   */
  private String readFileAsString(String filePath) throws java.io.IOException {
    StringBuilder fileData = new StringBuilder(2000);
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))) {
      char[] buf = new char[1024];
      int numRead = 0;
      while ((numRead = reader.read(buf)) != -1) {
        String readData = String.valueOf(buf, 0, numRead);
        fileData.append(readData);
        buf = new char[1024];
      }
    }
    return fileData.toString();
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
      //      PuzzlePieceSet allElements_ODF11 = new PuzzlePieceSet();
      //      PuzzlePieceSet allAttributes_ODF11 = new PuzzlePieceSet();
      //      PuzzlePiece.extractPuzzlePieces(
      //          SchemaToTemplate.loadSchemaODF11(),
      //          allElements_ODF11,
      //          allAttributes_ODF11,
      //          SchemaToTemplate.ODF11_RNG_FILE);
      //      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      //      checkFoundNumber(allElements_ODF11.withoutMultiples(), ODF11_ELEMENT_NUMBER,
      // "element");
      //      checkFoundNumber(allAttributes_ODF11.withoutMultiples(), ODF11_ATTRIBUTE_NUMBER,
      // "attribute");
      //
      //      PuzzlePieceSet allElements_ODF12 = new PuzzlePieceSet();
      //      PuzzlePieceSet allAttributes_ODF12 = new PuzzlePieceSet();
      //      PuzzlePiece.extractPuzzlePieces(
      //          SchemaToTemplate.loadSchemaODF12(),
      //          allElements_ODF12,
      //          allAttributes_ODF12,
      //          SchemaToTemplate.ODF_1_2_SCHEMA_GRAMMAR);
      //      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      //      checkFoundNumber(allElements_ODF12.withoutMultiples(), ODF12_ELEMENT_NUMBER,
      // "element");
      //      checkFoundNumber(allAttributes_ODF12.withoutMultiples(), ODF12_ATTRIBUTE_NUMBER,
      // "attribute");

      PuzzlePieceSet allElements_ODF13 = new PuzzlePieceSet();
      PuzzlePieceSet allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF_1_3_SCHEMA_GRAMMAR);
      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      checkFoundNumber(
          allElements_ODF13.withoutMultiples(), ODF13_ELEMENT_NUMBER, "element", "ODF 1.3");
      checkFoundNumber(
          allAttributes_ODF13.withoutMultiples(), ODF13_ATTRIBUTE_NUMBER, "attribute", "ODF 1.3");

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
  public void testExtractPuzzlePiecesWithDuplicates() {
    int foundElementDuplicates;
    int foundAttributeDuplicates;
    try {
      PuzzlePieceSet allElements_ODF13 = new PuzzlePieceSet();
      PuzzlePieceSet allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF_1_3_SCHEMA_GRAMMAR);
      allElements_ODF13 = new PuzzlePieceSet();
      allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF_1_3_SCHEMA_GRAMMAR);
      allElements_ODF13 = new PuzzlePieceSet();
      allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF_1_3_SCHEMA_GRAMMAR);
      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      foundElementDuplicates = allElements_ODF13.size() - ODF13_ELEMENT_NUMBER;
      foundAttributeDuplicates = allAttributes_ODF13.size() - ODF13_ATTRIBUTE_NUMBER;
      if (ODF13_ELEMENT_DUPLICATES != foundElementDuplicates) {
        String errorMsg =
            "There is a difference between the expected outcome of duplicates for ODF 1.3 elements.\n"
                + "Expected: '"
                + ODF13_ELEMENT_DUPLICATES
                + "'\tfound:'"
                + foundElementDuplicates
                + "'";
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }
      if (ODF13_ATTRIBUTE_DUPLICATES != foundAttributeDuplicates) {
        String errorMsg =
            "There is a difference between the expected outcome of duplicates for ODF 1.3 attributes.\n"
                + "Expected: '"
                + ODF13_ATTRIBUTE_DUPLICATES
                + "'\tfound:'"
                + foundAttributeDuplicates;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }
    } catch (Exception ex) {
      Logger.getLogger(PuzzlePieceTest.class.getName()).log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }

  /**
   * Routine to compare the expected number of either attributes or elements with the found amount
   */
  private void checkFoundNumber(
      PuzzlePieceSet puzzlePieceSet, int expectedAmount, String nodeName, String versionLabel) {
    if (expectedAmount == puzzlePieceSet.size()) {
      LOG.log(
          Level.FINEST,
          "The expected amount of {0}s could be found in {1}!",
          new Object[] {nodeName, versionLabel});
      if (DEBUG) {
        int i = 0;
        for (PuzzlePiece piece : puzzlePieceSet) {
          LOG.log(Level.INFO, "{0} was {1} #{2}", new Object[] {piece.getQName(), nodeName, ++i});
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
              + nodeName
              + "s found in "
              + versionLabel;
      LOG.severe(errorMsg);
      int i = 0;
      for (PuzzlePiece piece : puzzlePieceSet) {
        LOG.log(Level.SEVERE, "{0} was {1} #{2}", new Object[] {piece.getQName(), nodeName, ++i});
      }
      LOG.info("********************");
      Assert.fail(errorMsg);
    }
  }

  /**
   * Load and parse the ODF 1.0 Schema.
   *
   * @return MSV Expression Tree of ODF 1.0 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF10() throws Exception {
    return XMLModel.loadSchema(ODF_1_0_SCHEMA_GRAMMAR);
  }

  /**
   * Load and parse the ODF 1.1 Schema.
   *
   * @return MSV Expression Tree of ODF 1.1 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF11() throws Exception {
    return XMLModel.loadSchema(ODF_1_1_SCHEMA_GRAMMAR);
  }

  /**
   * Load and parse the ODF 1.2 Schema.
   *
   * @return MSV Expression Tree of ODF 1.2 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF12() throws Exception {
    return XMLModel.loadSchema(ODF_1_2_SCHEMA_GRAMMAR);
  }

  /**
   * Load and parse the ODF 1.3 Schema.
   *
   * @return MSV Expression Tree of ODF 1.3 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF13() throws Exception {
    return XMLModel.loadSchema(ODF_1_3_SCHEMA_GRAMMAR);
  }
}
