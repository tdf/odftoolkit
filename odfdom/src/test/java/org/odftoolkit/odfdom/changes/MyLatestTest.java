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

import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

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
public class MyLatestTest extends RoundtripTestHelper {

  private static final Logger LOG = Logger.getLogger(MyLatestTest.class.getName());

  public MyLatestTest() {}

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // Creating the output directory for the tests
    RoundtripTestHelper.setUpBeforeClass();
  }

  @Test
  public void myTest() {

    //        System.setProperty("test", "org.odftoolkit.odfdom.component.MyLatestTest");
    //        final String SOURCE_FILE_NAME_TRUNC = "JSONCritical";
    //        //super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, "2",
    // getEditingOperations(INPUT_FOLDER_OP_REF));
    //        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, "3", null);
    //
    //// *****************************************
    //        String editOperations = "{\"changes\":["
    //                + "{\"name\":\"addParagraph\",\"start\":[1]},"
    //                + "{\"name\":\"addText\",\"start\":[1,1],\"text\":\"PATH:
    // C:\\\\path\\\\own.xls und URL: http:\\/\\/www.heise.de\"}"
    //                + "]}";
    //        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, "1", editOperations);
    //// *****************************************
    //		super.roundtripOnlyToEmptyDocRegressionTest(SOURCE_FILE_NAME_TRUNC, ".odt");
    //      super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ".odt", getTestMethodName(),
    // editOperations);

  }
}
