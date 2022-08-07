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

import static schema2template.example.odf.SchemaToTemplate.*;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import schema2template.model.MSVExpressionIterator;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;
import schema2template.model.XMLModel;

public class PuzzlePieceTest {

  private static final Logger LOG = Logger.getLogger(PuzzlePieceTest.class.getName());
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
  private static final int ODF11_ELEMENT_NUMBER = 526;

  private static final int ODF12_ELEMENT_NUMBER = 599;

  private static final int ODF13_ELEMENT_NUMBER = 606;
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
  private static final int ODF11_ATTRIBUTE_NUMBER = 1163;

  // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie.
  // @office:process-content)
  private static final int ODF12_ATTRIBUTE_NUMBER = 1301;

  // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie.
  // @office:process-content)
  private static final int ODF13_ATTRIBUTE_NUMBER = 1317;
  private static final int ODF13_ELEMENT_DUPLICATES = 7;
  private static final int ODF13_ATTRIBUTE_DUPLICATES =
      519; // 2DO Svante earlier 117 what is correct?

  /**
   * Via Maven pom.xml (surefire test plugin) received System variable of the absolute path of the
   * target build directory
   */
  private static String buildDir = System.getProperty("schema2template.build.dir");

  private static final String ODF_GRAMMAR_ROOT =
      Paths.get(
              buildDir
                  + File.separator
                  + ".."
                  + File.separator
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
                  + "grammar")
          .normalize()
          .toString();

  public static final String ODF10_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-strict-schema-v1.0-os.rng";
  public static final String ODF11_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-strict-schema-v1.1.rng";
  public static final String ODF12_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-v1.2-os-schema.rng";
  public static final String ODF12_MANIFEST_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-v1.2-os-manifest-schema.rng";
  public static final String ODF12_SIGNATURE_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-v1.2-os-dsig-schema.rng";
  public static final String ODF13_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-v1.3-schema.rng";
  public static final String ODF13_MANIFEST_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-v1.3-manifest-schema.rng";
  public static final String ODF13_SIGNATURE_RNG_FILE =
      ODF_GRAMMAR_ROOT + File.separator + "OpenDocument-v1.3-dsig-schema.rng";

  private static final String TARGET_REL_DIR =
      "generated-sources"
          + File.separator
          + "java"
          + File.separator
          + "odf"
          + File.separator
          + "msv-dump"
          + File.separator;

  private static final String TARGET_ROOT =
      Paths.get(System.getProperty("schema2template.build.dir"), TARGET_REL_DIR)
          .normalize()
          .toString();

  private static final String TARGET_DUMP_FILE_ODF10 =
      TARGET_ROOT + File.separator + "odf10-msvtree.dump";
  private static final String TARGET_DUMP_FILE_ODF11 =
      TARGET_ROOT + File.separator + "odf11-msvtree.dump";
  private static final String TARGET_DUMP_FILE_ODF12 =
      TARGET_ROOT + File.separator + "odf12-msvtree.dump";
  private static final String TARGET_DUMP_FILE_ODF13 =
      TARGET_ROOT + File.separator + "odf13-msvtree.dump";

  private static final String TEST_REFERENCE_DIR =
      System.getProperty("schema2template.build.dir")
          + File.separator
          + "test-classes"
          + File.separator
          + "test-reference"
          + File.separator
          + "odf"
          + File.separator
          + "msv-dump";

  private static final String OUTPUT_REF_ODF10 =
      TEST_REFERENCE_DIR + File.separator + "odf10-msvtree.ref";
  private static final String OUTPUT_REF_ODF11 =
      TEST_REFERENCE_DIR + File.separator + "odf11-msvtree.ref";
  private static final String OUTPUT_REF_ODF12 =
      TEST_REFERENCE_DIR + File.separator + "odf12-msvtree.ref";
  private static final String OUTPUT_REF_ODF13 =
      TEST_REFERENCE_DIR + File.separator + "odf13-msvtree.ref";

  @Before
  public void intialize() {
    new File(TARGET_ROOT).mkdirs();
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
                + ODF10_RNG_FILE;
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
                + ODF11_RNG_FILE;
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
                + ODF12_RNG_FILE;
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
                + ODF13_RNG_FILE;
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
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
    char[] buf = new char[1024];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
      String readData = String.valueOf(buf, 0, numRead);
      fileData.append(readData);
      buf = new char[1024];
    }
    reader.close();
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
      //          SchemaToTemplate.ODF12_RNG_FILE);
      //      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      //      checkFoundNumber(allElements_ODF12.withoutMultiples(), ODF12_ELEMENT_NUMBER,
      // "element");
      //      checkFoundNumber(allAttributes_ODF12.withoutMultiples(), ODF12_ATTRIBUTE_NUMBER,
      // "attribute");

      PuzzlePieceSet allElements_ODF13 = new PuzzlePieceSet();
      PuzzlePieceSet allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF13_RNG_FILE);
      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      checkFoundNumber(allElements_ODF13.withoutMultiples(), ODF13_ELEMENT_NUMBER, "element");
      checkFoundNumber(allAttributes_ODF13.withoutMultiples(), ODF13_ATTRIBUTE_NUMBER, "attribute");

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
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF13_RNG_FILE);
      allElements_ODF13 = new PuzzlePieceSet();
      allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF13_RNG_FILE);
      allElements_ODF13 = new PuzzlePieceSet();
      allAttributes_ODF13 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          loadSchemaODF13(), allElements_ODF13, allAttributes_ODF13, ODF13_RNG_FILE);
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
      PuzzlePieceSet puzzlePieceSet, int expectedAmount, String nodeName) {
    if (expectedAmount == puzzlePieceSet.size()) {
      LOG.log(Level.INFO, "The expected amount of {0}s could be found", nodeName);
      if (DEBUG) {
        int i = 0;
        for (PuzzlePiece piece : puzzlePieceSet) {
          LOG.info(piece.getQName() + " was " + nodeName + " #" + ++i);
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
              + "s found";
      LOG.severe(errorMsg);
      int i = 0;
      for (PuzzlePiece piece : puzzlePieceSet) {
        LOG.severe(piece.getQName() + " was " + nodeName + " #" + ++i);
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
    return XMLModel.loadSchema(ODF10_RNG_FILE);
  }

  /**
   * Load and parse the ODF 1.1 Schema.
   *
   * @return MSV Expression Tree of ODF 1.1 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF11() throws Exception {
    return XMLModel.loadSchema(ODF11_RNG_FILE);
  }

  /**
   * Load and parse the ODF 1.2 Schema.
   *
   * @return MSV Expression Tree of ODF 1.2 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF12() throws Exception {
    return XMLModel.loadSchema(ODF12_RNG_FILE);
  }

  /**
   * Load and parse the ODF 1.3 Schema.
   *
   * @return MSV Expression Tree of ODF 1.3 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  static Grammar loadSchemaODF13() throws Exception {
    return XMLModel.loadSchema(ODF13_RNG_FILE);
  }
}
