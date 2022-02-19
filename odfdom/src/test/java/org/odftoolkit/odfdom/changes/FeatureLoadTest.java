/*
 * Copyright 2012 The Apache Software Foundation.
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

import static org.odftoolkit.odfdom.changes.OperationConstants.OPERATION_OUTPUT_DIR;

import java.io.File;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * For developer: Temporary Test document for quick tests! :D Changes not relevant, might be added
 * to .git ignore list of project.
 *
 * <p>Loads a document with tables and gathers its operations. Gathered operations will be applied
 * to an empty text document. The changed text document will be saved and reloaded. New gathered
 * operations will be compared with the original ones, expected to be identical!
 *
 * @author svanteschubert
 */
public class FeatureLoadTest extends RoundtripTestHelper {

  private static final Logger LOG = Logger.getLogger(FeatureLoadTest.class.getName());

  public FeatureLoadTest() {}

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // Creating the output directory for the tests
    RoundtripTestHelper.setUpBeforeClass();
  }

  @Test
  /**
   * Roundtrip a ODF Text featue test document located in src/test/resources/test-input/feature
   *
   * <p>given by command-line, for example: mvn surefire:test -Dtest=FeatureLoadTest
   * -DargLine="-DtextFeatureName=text_bold"
   *
   * <p>The default test document run by default is given in the pom.xml as "text_italic"
   */
  public void roundTripFeatureTest() {
    final String SOURCE_FILE_NAME_TRUNC = System.getProperty("textFeatureName");
    final String SOURCE_SUBDIR_NAME = "feature";
    // create the test directories for the "performance" subdirectory
    new File(ResourceUtilities.getTestInputFolder() + OPERATION_OUTPUT_DIR + SOURCE_SUBDIR_NAME)
        .mkdirs();
    new File(ResourceUtilities.getTestOutputFolder() + OPERATION_OUTPUT_DIR + SOURCE_SUBDIR_NAME)
        .mkdirs();

    super.roundtripRegressionTextTest(
        SOURCE_SUBDIR_NAME + File.separator + SOURCE_FILE_NAME_TRUNC, getTestMethodName(), null);
  }
}
