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

import com.sun.msv.grammar.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * MSVDescendantsToHTMLTest is able to dump for each XML element and XML attribute its content as a
 * string expressing choice, sequence, etc. with regular expression syntax.
 */
public class MSVDescendantsToHTMLTest {

  private static final Logger LOG = Logger.getLogger(MSVDescendantsToHTMLTest.class.getName());

  private static final String CHILD_DESC_DIRECTORY = "childDesc";

  @Before
  public void intialize() {
    new File(ConstantsBuildEnv.TARGET_BASE_DIR + CHILD_DESC_DIRECTORY).mkdirs();
  }

  /**
   * This test iterates over all ODF grammars loads them into the MSV Validator and dumps the
   * run-time model (ExpressionTree) into a file.
   */
  @Test
  public void testMSVExpressionTree() {
    try {
      for (ConstantsOdf.OdfSpecificationPart specPart :
          ConstantsOdf.OdfSpecificationPart.values()) {
        if (specPart.grammarID.equals(ConstantsOdf.GrammarID.ODF_SCHEMA.ID)
            && specPart.grammarVersion.equals(
                ConstantsOdf.OdfSpecificationPart.ODF_1_3_SCHEMA.grammarVersion)) {
          LOG.info(
              "\n\nNew ODF grammar runtime serialization (MSV dump) for regression test:"
                  + "\n\tgrammarVersion "
                  + specPart.grammarVersion
                  + "\n\tgrammarID: "
                  + specPart.grammarID
                  + "\n\tgrammarPath: "
                  + specPart.grammarPath
                  + "\n\ttargetDirPath: "
                  + ConstantsBuildEnv.TARGET_BASE_DIR
                  + CHILD_DESC_DIRECTORY);
          Grammar xmlGrammar = XMLModel.loadSchema(specPart.grammarPath);
          String odfDump = dumpChildRelation(xmlGrammar);

          String grammarLabel = specPart.grammarID + "-" + specPart.grammarVersion;

          String targetChildDescFile =
              ConstantsBuildEnv.TARGET_BASE_DIR
                  + CHILD_DESC_DIRECTORY
                  + File.separator
                  + grammarLabel
                  + "-msvRegEx.txt";
          LOG.log(
              Level.INFO,
              "Writing MSV Child descriptions for " + grammarLabel + " + into file: {0}",
              targetChildDescFile);
          try (PrintWriter out = new PrintWriter(new FileWriter(targetChildDescFile))) {
            out.print(odfDump);
          }
        }
        /*DirectoryCompare.compareDirectories(
        ConstantsBuildEnv.TARGET_BASE_DIR + CHILD_DESC_DIRECTORY,
        ConstantsBuildEnv.REFERENCE_BASE_DIR + CHILD_DESC_DIRECTORY);*/
      }
    } catch (Exception ex) {
      Logger.getLogger(MSVDescendantsToHTMLTest.class.getName()).log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }
  /**
   * Iterates the MSVExpressionTree between parent and children and dumps their relation into a
   * string
   *
   * @return the MSVExpressionTree serialized into a String
   */
  private static String dumpChildRelation(Grammar xmlGrammar) throws Exception {
    MSVExpressionIterator iterator = new MSVExpressionIterator(xmlGrammar.getTopLevel());
    StringBuilder builder = new StringBuilder();
    while (iterator.hasNext()) {
      Expression expr = iterator.next();
      if (expr instanceof NameClassAndExpression) {
        List<String> names =
            (List<String>)
                ((NameClassAndExpression) expr).getNameClass().visit(new MSVNameClassVisitorList());
        // 2DO: ONGOING INITIAL DEBUG WITH ONE ELEMENT!!
        //      if (names.get(0).equals("form:property")) {

        //      see
        // http://docs.oasis-open.org/office/OpenDocument/v1.3/os/schemas/OpenDocument-v1.3-schema-rng.html#table-table
        if (names.get(0).equals("table:table")) {
          expr.visit(
              new MSVExpressionVisitorDescendantsAsHTMLString(
                  builder, XMLModel.getHeadsOfIslands(xmlGrammar)));
        }
      }
    }
    return builder.toString();
  }
}
