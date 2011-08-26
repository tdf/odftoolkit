/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * Use is subject to license terms.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.odfdom.pkg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;

public class PackageTest {

    public PackageTest() {
    }

    @Test
    public void testPackage() {
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("tmp", ".ods");
        } catch (IOException ex) {
            ex.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(tmpFile);
        OdfDocument doc = null;
        try {
            doc = OdfSpreadsheetDocument.newSpreadsheetDocument();
            doc.save(tmpFile);
            doc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }

        long lengthBefore = tmpFile.length();
        try {
            // not allowed to change the document simply by open and save
            OdfPackage loadPackage = OdfPackage.loadPackage(tmpFile);
            loadPackage.save(tmpFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            Assert.fail();
        }
        long lengthAfter = tmpFile.length();

        // clean up afterwards
        tmpFile.delete();
        Assert.assertEquals(lengthBefore, lengthAfter);
    }
}
