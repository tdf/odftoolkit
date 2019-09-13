/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.odfdom.changes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Ignore;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPERATION_OUTPUT_DIR;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_EDITOR;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_OPERATIONS;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_VERSION;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * This is not a test, but a tool. As test for quick iterations in an IDE.
 *
 * The Tool is meant to adopt the JSON reference (or output test) files
 * according to a new pattern to make differences aside of this pattern easier
 * detectable.
 *
 * For instance, in this example all reference files had been adopted by one
 * higher position number. Earlier the counting started with 0.
 */
public class OperationsRefactoringTest {

    static protected Logger LOG = Logger.getLogger(OperationsRefactoringTest.class.getName());
    static private String ODFDOM_ID = "ODFDOM_" + System.getProperty("odfdom.version");
    static private String ODFDOM_TIMESTAMP = System.getProperty("odfdom.timestamp");


    public OperationsRefactoringTest() {
        File adoptedRefOpsDirFile = new File(ResourceUtilities.getSrcTestReferenceFolder() + REFACTORED_OPS_OUTPUT_DIR_SUFFIX);
        adoptedRefOpsDirFile.mkdirs();
        File adoptedEditOpsDirFile = new File(ResourceUtilities.getTestInputFolder() + REFACTORED_OPS_OUTPUT_DIR_SUFFIX);
        adoptedEditOpsDirFile.mkdirs();
    }

    /**
     * File folder to import the JSON text files - by default the reference
     * directory
     */
    // odfdom/src/test/resources/test-reference/operations
    private static final String INPUT_FOLDER_OP_REF = ResourceUtilities.getSrcTestReferenceFolder() + File.separator + OPERATION_OUTPUT_DIR + File.separator;
    // odfdom/target/test-classes/test-input/operations
    private static final String INPUT_FOLDER_OP_EDIT = ResourceUtilities.getTestInputFolder() + File.separator + OPERATION_OUTPUT_DIR + File.separator;

    /**
     * Folder name to be created beyond test output directory
     * "odfdom/target/test-classes/"
     */
    private static final String REFACTORED_OPS_OUTPUT_DIR_SUFFIX = "refactored" + File.separator + OPERATION_OUTPUT_DIR;

    /**
     * For every new refactoring ONLY this method have to be adopted. For
     * example, the position has is being incremented by one.
     *
     * @param op single operation from the operation file to be refactored
     */
    static private JSONObject refactorOperation(JSONObject op) {
//        // CHANGES TO THE REFERENCE OPERATIONS
//        JSONArray start = decrementAll(op.optJSONArray("start"));
//        op.putOpt("start", start);
//        JSONArray end = decrementAll(op.optJSONArray("end"));
//        op.putOpt("end", end);
//        JSONArray to = decrementAll(op.optJSONArray("to"));
//        op.putOpt("to", to);
        return op;
    }

    static private JSONArray incrementAll(JSONArray position) {
        if (position != null) {
            for (int i = 0; i < position.length(); i++) {
                position.put(i, ((Integer) position.get(i)) + 1);
            }
        }
        return position;
    }

    static private JSONArray decrementAll(JSONArray position) {
        if (position != null) {
            for (int i = 0; i < position.length(); i++) {
                position.put(i, ((Integer) position.get(i)) - 1);
            }
        }
        return position;
    }
    //****************************************************************************

    @Test
    @Ignore
    public void refactorOperations() {
            // READING: odfdom/src/test/resources/test-reference/operations
        refactorDirectory(INPUT_FOLDER_OP_REF);
            // READING: odfdom/target/test-classes/test-input/operations
        refactorDirectory(INPUT_FOLDER_OP_EDIT);
    }

    private void refactorDirectory(String operationInputDirectory) {
        try (Stream<Path> paths = Files.walk(new File(operationInputDirectory).toPath())) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    //.forEach(System.out::println); // list all files being found!
                    .forEach(org.odftoolkit.odfdom.changes.OperationsRefactoringTest::refactorOperationFile);
        } catch (IOException ex) {
            Logger.getLogger(RoundtripTestHelper.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
    }

    /**
     * helpful to adjust the reference according to a pattern (e.g. a new
     * feature), otherwise potential problems would be overseen by all the false
     * positives
     */
    public static  void refactorOperationFile(Path opsPath) {
        File opsFile = new File(opsPath.toString());
        System.out.println("Reading: " + opsPath.toString());

        String refOpsString = "";
        try {
            refOpsString = ResourceUtilities.loadFileAsString(opsFile);
            String opsFilePath = opsPath.getParent().getParent().toString() + File.separator + REFACTORED_OPS_OUTPUT_DIR_SUFFIX + opsFile.getName();

            JSONObject jOps = null;
            jOps = new JSONObject(refOpsString);
            JSONArray ops = jOps.getJSONArray(OPK_OPERATIONS);
            for (int i = 0; i < ops.length(); i++) {
                JSONObject op = ops.getJSONObject(i);
//                System.out.println("OP:\n" + op.toString());
                ops.put(i, refactorOperation(op));
            }
            jOps.put(OPK_VERSION, ODFDOM_TIMESTAMP);
            jOps.put(OPK_EDITOR, ODFDOM_ID);
            jOps.put(OPK_OPERATIONS, ops);
            String newOps = JsonOperationNormalizer.asString(jOps);
            ResourceUtilities.saveStringToFile(new File(opsFilePath), newOps.replace(",{\"name\"", ",\n{\"name\""));
            System.out.println("Writing: " + opsFilePath);
        } catch (JSONException ex) {
            System.err.println("Erroneous JSON file:\n" + opsPath);
            Logger.getLogger(RoundtripTestHelper.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
    }
}
