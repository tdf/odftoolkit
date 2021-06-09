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
 * <p>**********************************************************************
 */
package schema2template.example.odf;

import static schema2template.example.odf.OdfHelper.*;

import com.sun.msv.grammar.Expression;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import schema2template.model.MSVExpressionIterator;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;

public class PuzzlePieceTest {

  private static final Logger LOG = Logger.getLogger(PuzzlePieceTest.class.getName());
  private static final String OUTPUT_DUMP_ODF10 = "target" + File.separator + "odf10-msvtree.dump";
  private static final String OUTPUT_DUMP_ODF11 = "target" + File.separator + "odf11-msvtree.dump";
  private static final String OUTPUT_DUMP_ODF12 = "target" + File.separator + "odf12-msvtree.dump";
  private static final String OUTPUT_REF_ODF10 =
      TEST_REFERENCE_DIR + File.separator + "odf10-msvtree.ref";
  private static final String OUTPUT_REF_ODF11 =
      TEST_REFERENCE_DIR + File.separator + "odf11-msvtree.ref";
  private static final String OUTPUT_REF_ODF12 =
      TEST_REFERENCE_DIR + File.separator + "odf12-msvtree.ref";
  private static final int ODF12_ELEMENT_DUPLICATES = 7;
  private static final int ODF12_ATTRIBUTE_DUPLICATES = 116;

  /**
   * Test: Use the MSV
   *
   * <p>This test uses the ODF example, but it's meant to test the general ability to correctly
   * extract PuzzlePieces out of a XML schema
   */
  @Test
  public void testMSVExpressionTree() {
    try {
      Expression odf10Root = OdfHelper.loadSchemaODF10();
      String odf10Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf10Root);
      LOG.info("Writing MSV RelaxNG tree into file: " + OUTPUT_DUMP_ODF10);
      PrintWriter out0 = new PrintWriter(new FileWriter(OUTPUT_DUMP_ODF10));
      out0.print(odf10Dump);
      out0.close();

      Expression odf11Root = OdfHelper.loadSchemaODF11();
      String odf11Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf11Root);
      LOG.info("Writing MSV RelaxNG tree into file: " + OUTPUT_DUMP_ODF11);
      PrintWriter out1 = new PrintWriter(new FileWriter(OUTPUT_DUMP_ODF11));
      out1.print(odf11Dump);
      out1.close();

      Expression odf12Root = OdfHelper.loadSchemaODF12();
      String odf12Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf12Root);
      LOG.info("Writing MSV RelaxNG tree into file: " + OUTPUT_DUMP_ODF12);
      PrintWriter out2 = new PrintWriter(new FileWriter(OUTPUT_DUMP_ODF12));
      out2.print(odf12Dump);
      out2.close();

      String odf10Ref = readFileAsString(OUTPUT_REF_ODF10);
      if (!odf10Ref.equals(odf10Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.0 tree.\n"
                + "Please compare the output:\n\t'"
                + OUTPUT_DUMP_ODF10
                + "'\nwith the reference\n\t'"
                + odf10RngFile;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }

      String odf11Ref = readFileAsString(OUTPUT_REF_ODF11);
      if (!odf11Ref.equals(odf11Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.1 tree.\n"
                + "Please compare the output:\n\t'"
                + OUTPUT_DUMP_ODF11
                + "'\nwith the reference\n\t'"
                + odf11RngFile;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }

      String odf12Ref = readFileAsString(OUTPUT_REF_ODF12);
      if (!odf12Ref.equals(odf12Dump)) {
        String errorMsg =
            "There is a difference between the expected outcome of the parsed ODF 1.2 tree.\n"
                + "Please compare the output:\n\t'"
                + OUTPUT_DUMP_ODF12
                + "'\nwith the reference\n\t'"
                + odf12RngFile;
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
  public void testExtractPuzzlePieces() {
    try {
      PuzzlePieceSet allElements_ODF11 = new PuzzlePieceSet();
      PuzzlePieceSet allAttributes_ODF11 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          OdfHelper.loadSchemaODF11(), allElements_ODF11, allAttributes_ODF11);
      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      checkFoundNumber(allElements_ODF11.withoutMultiples(), ODF11_ELEMENT_NUMBER + 1, "element");
      checkFoundNumber(
          allAttributes_ODF11.withoutMultiples(), ODF11_ATTRIBUTE_NUMBER + 1, "attribute");

      PuzzlePieceSet allElements_ODF12 = new PuzzlePieceSet();
      PuzzlePieceSet allAttributes_ODF12 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          OdfHelper.loadSchemaODF12(), allElements_ODF12, allAttributes_ODF12);
      // There is a difference of one wildcard "*" representing anyElement/anyAttribute
      checkFoundNumber(allElements_ODF12.withoutMultiples(), ODF12_ELEMENT_NUMBER + 1, "element");
      checkFoundNumber(
          allAttributes_ODF12.withoutMultiples(), ODF12_ATTRIBUTE_NUMBER + 1, "attribute");
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
    try {
      PuzzlePieceSet allElements_ODF12 = new PuzzlePieceSet();
      PuzzlePieceSet allAttributes_ODF12 = new PuzzlePieceSet();
      PuzzlePiece.extractPuzzlePieces(
          OdfHelper.loadSchemaODF12(), allElements_ODF12, allAttributes_ODF12);
      // There is a difference of one wildcard "*" representing anyElement/anyAttribute

      int foundElementDuplicates = allElements_ODF12.size() - (ODF12_ELEMENT_NUMBER + 1);
      int foundAttributeDuplicates = allAttributes_ODF12.size() - (ODF12_ATTRIBUTE_NUMBER + 1);

      if (ODF12_ELEMENT_DUPLICATES != foundElementDuplicates) {
        String errorMsg =
            "There is a difference between the expected outcome of duplicates for ODF 1.2 elements.\n"
                + "Expected: '"
                + ODF12_ELEMENT_DUPLICATES
                + "'\tfound:'"
                + foundElementDuplicates;
        LOG.severe(errorMsg);
        Assert.fail(errorMsg);
      }
      if (ODF12_ATTRIBUTE_DUPLICATES != foundAttributeDuplicates) {
        String errorMsg =
            "There is a difference between the expected outcome of duplicates for ODF 1.2 elements.\n"
                + "Expected: '"
                + ODF12_ATTRIBUTE_DUPLICATES
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
}
