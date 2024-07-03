/*
 * Copyright 2018 The Apache Software Foundation.
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Loads all document of the input test directory and gathers its operations. Gathered operations
 * will be applied to an empty text document. The changed text document will be saved and reloaded.
 * New gathered operations will be compared with the original ones, expected to be identical.
 *
 * @author svanteschubert
 */
@RunWith(Parameterized.class)
public class RoundtripTest extends RoundtripTestHelper {

  private static final Logger LOG = Logger.getLogger(RoundtripTest.class.getName());
  private File mTestFile = null;

  public RoundtripTest(File testFile) {
    super();
    mTestFile = testFile;
  }

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    RoundtripTestHelper.setUpBeforeClass();
  }

  @Test
  public void testFile() {
    String fileName = mTestFile.getName();
    String fileTruncName = fileName.substring(0, fileName.lastIndexOf("."));
    String fileSuffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
    super.roundtripOnlyToEmptyDocRegressionTest(fileTruncName, fileSuffix);
  }

  @Parameters(name = "Test# {index}: {0}")
  public static Collection<Object[]> data() {
    Collection<Object[]> testSuiteData = new ArrayList<Object[]>();
    addFilesFromFolder(new File(TEST_INPUT_DIR), testSuiteData);
    return testSuiteData;
  }

  private static void addFilesFromFolder(final File folder, Collection<Object[]> testSuiteData) {
    String filePath = null;
    for (final File fileEntry : folder.listFiles()) {
      filePath = fileEntry.getAbsolutePath();
      if (fileEntry.isDirectory()) {
        // ToDo: There is a performance directory that currently ruins the test!
        //				LOG.log(Level.INFO, "*** testDirectory:{0}", filePath);
        //				addFilesFromFolder(fileEntry, testSuiteData);
      } else {
        if ((filePath.endsWith(".odt") || filePath.endsWith(".ott")
            //				 ||  filePath.endsWith(".ods") || filePath.endsWith(".ots")
            // && !filePath.contains("emptyFile.ods") // complete empty file
            // || filePath.endsWith(".odp") )
            )
            /** 2DO for Svante: validation problems should be tested after package merge */
            && !filePath.contains("MultiStylesSimple-MSO2013.odt")
            && !filePath.contains("MultiStylesSimple_SingleLine-MSO2013.odt")
            && !filePath.contains("simple-list_MSO14.odt")
            && !filePath.contains("simple-table.odt")
            && !filePath.contains("spanInheritanceTest.odt")
            && !filePath.contains("tableCoveredContent.odt")
            && !filePath.contains("textTestTemplate.ott")
            && !filePath.contains("indentTest.odt")
            && !filePath.contains("testInvalidPkg")
            && !filePath.contains("duplicate-files.odt")
            && !filePath.contains("unicode-path.odt")
            && !filePath.contains("slash.odt")
            && !filePath.contains("two-zips.odt")
            && !filePath.contains("BigTable.odt") // too slow 4 now
            && !filePath.endsWith("PasswordProtected.odt")) {
          //						!filePath.contains("Text1.odt")
          //						// ToDo: There is a table repeated issue to be fixed soo
          //						&& !filePath.contains("test1") && !filePath.contains("test1") &&
          // !filePath.endsWith("PasswordProtected.odt")
          //						&& !filePath.endsWith("tableOps.odt") && !filePath.endsWith("OOStyledTable.odt"))
          // {// || )
          LOG.log(Level.INFO, "*** testFile: {0}", filePath);
          Object[] testData = new Object[] {fileEntry};
          testSuiteData.add(testData);
        }
      }
    }
  }
}
