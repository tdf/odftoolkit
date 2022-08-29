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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;
import schema2template.model.XMLModel;

/**
 * ODF example class to print the MSV expressions in between a PuzzlePiece parent element and a
 * direct PuzzlePiece child element.
 *
 * <p>Example of a direct child: table:table -&gt; table:table-row<br>
 * Example of a non-direct child: table:table -&gt; table:table-cell
 *
 * <p>Directly change the string constants EXAMPLE_PARENT and EXAMPLE_CHILD in the source code to
 * set parent and child element.
 */
public class MSVPathPrinterTest {

  private static final Logger LOG = Logger.getLogger(PathPrinter.class.getName());
  private static final String EXAMPLE_PARENT = "table:table";
  private static final String EXAMPLE_CHILD = "table:table-row";

  private static final String[] TEST_RESULTS = new String[3];

  static {
    TEST_RESULTS[0] =
        "ELEMENT table:table -> SEQUENCE -> REF table-rows-and-groups -> ONEOREMORE -> CHOICE -> REF table-rows-no-group -> CHOICE -> SEQUENCE -> REF table-rows -> CHOICE -> ONEOREMORE -> SEQUENCE -> REF table-table-row -> ELEMENT table:table-row";
    TEST_RESULTS[1] =
        "ELEMENT table:table -> SEQUENCE -> REF table-rows-and-groups -> ONEOREMORE -> CHOICE -> REF table-rows-no-group -> CHOICE -> SEQUENCE -> CHOICE -> SEQUENCE -> CHOICE -> REF table-rows -> CHOICE -> ONEOREMORE -> SEQUENCE -> REF table-table-row -> ELEMENT table:table-row";
    TEST_RESULTS[2] =
        "ELEMENT table:table -> SEQUENCE -> REF table-rows-and-groups -> ONEOREMORE -> CHOICE -> REF table-rows-no-group -> CHOICE -> SEQUENCE -> CHOICE -> REF table-rows -> CHOICE -> ONEOREMORE -> SEQUENCE -> REF table-table-row -> ELEMENT table:table-row";
  }

  @Test
  /**
   * Prints the MSV grammar path between a parent and child element, e.g. &lt;table:table&gt; and
   * &lt;table:table:row&gt;
   */
  public void printMSVParentChildPath() throws Exception {
    PathPrinter pp =
        new PathPrinter(
            new XMLModel(
                new File(ConstantsOdf.OdfSpecificationPart.ODF_1_3_SCHEMA.grammarPath),
                ConstantsOdf.OdfSpecificationPart.ODF_1_3_SCHEMA.grammarID,
                ConstantsOdf.OdfSpecificationPart.ODF_1_3_SCHEMA.grammarVersion));
    LOG.info(
        "Print all paths from parent element ("
            + EXAMPLE_PARENT
            + ") to direct child element ("
            + EXAMPLE_CHILD
            + ")");
    List<String> paths = pp.printChildPaths(EXAMPLE_PARENT, EXAMPLE_CHILD);
    if (paths == null) {
      LOG.info("No Path found.");
    } else {
      for (int i = 0; i < paths.size(); i++) {
        String s = paths.get(i);
        LOG.info(s);
        assertTrue(s.equals(TEST_RESULTS[i]));
      }
    }
  }
}
