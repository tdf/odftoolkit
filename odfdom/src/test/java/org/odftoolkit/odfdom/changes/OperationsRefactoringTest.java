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
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_VERSION_BRANCH;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_VERSION_TIME;
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

    private static final Logger LOG = Logger.getLogger(OperationsRefactoringTest.class.getName());
    private static final String ODFDOM_GIT_BRANCH = System.getProperty("odfdom.git.branch");
    private static final String ODFDOM_GIT_COMMIT_TIME = System.getProperty("odfdom.git.commit.time");
    private static final String ODFDOM_GIT_COMMIT_DESCRIBE = System.getProperty("odfdom.git.commit.id.describe");
    private static final String ODFDOM_GIT_URL = System.getProperty("odfdom.git.remote.origin.url");
    static private Boolean mIsTestInputOpsCreated = Boolean.FALSE;
    static private Boolean mIsTestRefOpsCreated = Boolean.FALSE;


    /**
     * File folder to import the JSON text files - by default the reference
     * directory
     */
    // odfdom/src/test/resources/test-reference/operations
    private static final String INPUT_FOLDER_OP_REF = ResourceUtilities.getSrcTestReferenceFolder() + File.separator + OPERATION_OUTPUT_DIR + File.separator;
    // odfdom/src/test/resources/test-input/operations
    private static final String INPUT_FOLDER_OP_EDIT = ResourceUtilities.getSrcTestInputFolder() + File.separator + OPERATION_OUTPUT_DIR + File.separator;

    /**
     * Folder name to be created beyond test output directory
     * "odfdom/target/test-classes/"
     */
    private static final String REFACTORED_OPS_OUTPUT_DIR_SUFFIX = "operations_refactored" + File.separator;

    /**
     * For every new refactoring ONLY this method have to be adopted. For
     * example, the position has is being incremented by one.
     *
     * @param op single operation from the operation file to be refactored
     */
    private static JSONObject refactorOperation(JSONObject op) {
        if (op.has("attrs")) {
            JSONObject attrs = op.optJSONObject("attrs");
            if (attrs.has("paragraph")) {
                JSONObject paragraph = attrs.optJSONObject("paragraph");
                if (paragraph.has("listLevel")) {
                    Integer outlineLevel = paragraph.optInt("listLevel", -1);
                    if (!outlineLevel.equals(JSONObject.NULL) && outlineLevel > -1) {
                        paragraph.put("listLevel", ++outlineLevel);
                        attrs.put("paragraph", paragraph);
                        op.put("attrs", attrs);
                    }
                }
            }
        }
        if (op.has("listDefinition")) {
            JSONObject listDefinition = op.optJSONObject("listDefinition");
            boolean isChanged = Boolean.FALSE;
            for (int i = 10; i >= 0; i--) {
                String listLevel = "listLevel" + i;
                if (listDefinition.has(listLevel)) {
                    isChanged = Boolean.TRUE;
                    JSONObject listLevelObj = listDefinition.optJSONObject(listLevel);
                    if (!listLevelObj.equals(JSONObject.NULL)) {
                        listDefinition.put("listLevel" + ++i, listLevelObj);
                        listDefinition.remove(listLevel);
                    }
                }
            }
            if (isChanged) {
                op.put("listDefinition", listDefinition);
            }
        }
        /**
         * {"name":"addListStyle","listDefinition":{"listLevel0":{"indentFirstLine":-635,"indentLeft":1270,"labelFollowedBy":"listtab","levelText":"","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel1":{"indentFirstLine":-635,"indentLeft":2540,"labelFollowedBy":"listtab","levelText":"○","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel2":{"indentFirstLine":-635,"indentLeft":3810,"labelFollowedBy":"listtab","levelText":"■","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel3":{"indentFirstLine":-635,"indentLeft":5080,"labelFollowedBy":"listtab","levelText":"","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel4":{"indentFirstLine":-635,"indentLeft":6350,"labelFollowedBy":"listtab","levelText":"○","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel5":{"indentFirstLine":-635,"indentLeft":7620,"labelFollowedBy":"listtab","levelText":"■","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel6":{"indentFirstLine":-635,"indentLeft":8890,"labelFollowedBy":"listtab","levelText":"","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel7":{"indentFirstLine":-635,"indentLeft":10160,"labelFollowedBy":"listtab","levelText":"○","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"},"listLevel8":{"indentFirstLine":-635,"indentLeft":11430,"labelFollowedBy":"listtab","levelText":"■","listLevelPositionAndSpaceMode":"label-alignment","numberFormat":"bullet","textAlign":"left"}},"listStyleId":"L1"},
         *
         */
        /* 
        if (op.has("attrs")) {
            JSONObject attrs = op.optJSONObject("attrs");
            if (attrs.has("paragraph")) {
                JSONObject paragraph = attrs.optJSONObject("paragraph");
                if (paragraph.has("outlineLevel")) {
                    Integer outlineLevel = paragraph.optInt("outlineLevel", -1);
                    if(!outlineLevel.equals(JSONObject.NULL) && outlineLevel > -1){
                        paragraph.put("outlineLevel", ++outlineLevel);
                        attrs.put("paragraph", paragraph);
                        op.put("attrs", attrs);
                    }
                }
            }
        }        
        
         */

//        // CHANGES TO THE REFERENCE OPERATIONS
//        JSONArray start = decrementPosition(op.optJSONArray("start"));
//        op.putOpt("start", start);
//        JSONArray end = decrementPosition(op.optJSONArray("end"));
//        op.putOpt("end", end);
//        JSONArray to = decrementPosition(op.optJSONArray("to"));
//        op.putOpt("to", to);
        return op;
    }

    static private JSONArray incrementPosition(JSONArray position) {
        if (position != null) {
            for (int i = 0; i < position.length(); i++) {
                position.put(i, ((Integer) position.get(i)) + 1);
            }
        }
        return position;
    }

    static private JSONArray decrementPosition(JSONArray position) {
        if (position != null) {
            for (int i = 0; i < position.length(); i++) {
                position.put(i, ((Integer) position.get(i)) - 1);
            }
        }
        return position;
    }
    //****************************************************************************

    @Test
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
                    .filter(p -> p.toString().endsWith(".json"))
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
    public static void refactorOperationFile(Path opsPath) {
        String sOpsPath = opsPath.toString();
        File opsFile = new File(sOpsPath);
        System.out.println("Reading: " + sOpsPath);

        String refOpsString = "";
        try {
            refOpsString = ResourceUtilities.loadFileAsString(opsFile);
            /**
             * Reading:
             * odfdom\src\test\resources\test-reference\operations\sections.odt--initial_ops.json
             */
            String opsFileOutPath;
            if (sOpsPath.contains("test-input")) {
                if (!mIsTestInputOpsCreated) {
                    new File(ResourceUtilities.getTestInputFolder() + REFACTORED_OPS_OUTPUT_DIR_SUFFIX).mkdirs();
                    mIsTestInputOpsCreated = Boolean.TRUE;
                }
                opsFileOutPath = ResourceUtilities.getTestInputFolder() + REFACTORED_OPS_OUTPUT_DIR_SUFFIX + opsFile.getName();
            } else {
                if (!mIsTestRefOpsCreated) {
                    new File(ResourceUtilities.getTestReferenceFolder() + REFACTORED_OPS_OUTPUT_DIR_SUFFIX).mkdirs();
                    mIsTestRefOpsCreated = Boolean.TRUE;
                }
                opsFileOutPath = ResourceUtilities.getTestReferenceFolder() + REFACTORED_OPS_OUTPUT_DIR_SUFFIX + opsFile.getName();
            }
            JSONObject jOps = null;
            jOps = new JSONObject(refOpsString);
            jOps.put(OPK_EDITOR, ODFDOM_GIT_URL);
            jOps.put(OPK_VERSION, ODFDOM_GIT_COMMIT_DESCRIBE);
            jOps.put(OPK_VERSION_BRANCH, ODFDOM_GIT_BRANCH);
            jOps.put(OPK_VERSION_TIME, ODFDOM_GIT_COMMIT_TIME);
            JSONArray ops = jOps.getJSONArray(OPK_OPERATIONS);
            for (int i = 0; i < ops.length(); i++) {
                JSONObject op = ops.getJSONObject(i);
//                System.out.println("OP:\n" + op.toString());
                ops.put(i, refactorOperation(op));
            }
            jOps.put(OPK_OPERATIONS, ops);
            String newOps = JsonOperationNormalizer.asString(jOps, Boolean.TRUE);
            ResourceUtilities.saveStringToFile(new File(opsFileOutPath), newOps.replace(",{\"name\"", ",\n{\"name\""));
            System.out.println("Writing: " + opsFileOutPath);
        } catch (JSONException ex) {
            System.err.println("Erroneous JSON file:\n" + opsPath);
            Logger.getLogger(RoundtripTestHelper.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        }
    }
}
