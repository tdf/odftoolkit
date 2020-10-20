/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.pkg;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class LoadSaveTest {

  private static final String SOURCE = "not-only-odf.odt";
  private static final String TARGET = "inputOutputTest.odt";

  public LoadSaveTest() {}

  private static String OS = null;

  public static boolean isWindows() {
    if (OS == null) {
      OS = System.getProperty("os.name");
    }
    return OS.startsWith("Windows");
  }

  @Test
  public void testOutputToInputStream() {
    try {
      File testFileBefore = ResourceUtilities.getTestInputFile(SOURCE);
      long lengthBefore = testFileBefore.length();
      OdfPackageDocument odfDocument =
          OdfPackageDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(SOURCE));
      OdfPackage pkgBefore = odfDocument.getPackage();
      Assert.assertTrue(pkgBefore.contains("content.xml"));
      OdfPackage pkgAfter = OdfPackage.loadPackage(pkgBefore.getInputStream());
      File testFileAfter = ResourceUtilities.getTestOutputFile(TARGET);
      pkgAfter.save(testFileAfter);
      long lengthAfter = testFileAfter.length();
      // the XML declaration is in the new ODF XML files in a new line, adding for three files three
      // bytes in total
      if (isWindows()) {
        Assert.assertTrue(
            "Before the package has a size of '"
                + lengthBefore
                + "' and afterwards a size of '"
                + lengthAfter
                + "'.",
            lengthBefore == lengthAfter);
      } else {
        // XML parser under Linux does insert line break after the XML declaration adding a byte for
        // every ODF XML file..
        Assert.assertTrue(
            "Before the package has a size of '"
                + lengthBefore
                + "' and afterwards a size of '"
                + lengthAfter
                + "'.",
            lengthBefore == (lengthAfter + 3) || (lengthBefore == lengthAfter));
      }

    } catch (Exception ex) {
      Logger.getLogger(LoadSaveTest.class.getName()).log(Level.SEVERE, null, ex);
      Assert.fail();
    }
  }
}
