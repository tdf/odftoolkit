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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

class TempDir {

    static Hashtable<File,Integer> _refCounts = new Hashtable<File,Integer>();

    /**
     * Creates a temp directory with a generated name (given a certain prefix) 
     * in a given directory.
     * The directory (and all its content) will be destroyed on exit.
     */
    static File createGeneratedName(String prefix, File directory)
            throws IOException {

        File tempFile = File.createTempFile(prefix, "", directory);
        if (!tempFile.delete()) {
            throw new IOException();
        }
        if (!tempFile.mkdir()) {
            throw new IOException();
        }
        TempDirDeleter.getInstance().add(tempFile);
        _refCounts.put(tempFile, new Integer(1));
        return tempFile;
    }

    /**
     * Creates a temp directory with a generated name (given a certain prefix)
     * in default temporary-file directory.
     * Invoking this method is equivalent to invoking createGeneratedName(prefix, null).
     * The directory (and all its content) will be destroyed on exit.
     */
    static File createGeneratedName(String prefix)
            throws IOException {
        return createGeneratedName(prefix, null);
    }

    /**
     * Creates a temp directory with a given name in a given directory.
     * The directory (and all its content) will be destroyed on exit.
     */
    static File createNamed(String name, File directory)
            throws IOException {

        File tempFile = new File(directory, name);
        if (!tempFile.mkdir()) {
            throw new IOException();
        }
        TempDirDeleter.getInstance().add(tempFile);
        _refCounts.put(tempFile, new Integer(1));
        return tempFile;
    }

    /**
     * increase refCount
     */
    static int ref(File dir) {
        int refCount = (_refCounts.get(dir)).intValue();
        ++refCount;
        _refCounts.put(dir, new Integer(refCount));
        return refCount;
    }

    /**
     * release reference, when refcount gets 0 Directory is deleted
     */
    static void release(File dir) {
        int refCount = (_refCounts.get(dir)).intValue();
        if (--refCount == 0) {
            TempDirDeleter.getInstance().deleteDirectory(dir);
            _refCounts.remove(dir);
        } else {
            _refCounts.put(dir, new Integer(refCount));
        }
    }

    /**
     * Copies an input stream to a temporary file. Currently needed to receive 
     * empty odf files. 
     */
    static File saveStreamToTempDir(InputStream inStream, File tempDir) {
        try {
            FileOutputStream foutStream = null;
            File targetFile = new File(tempDir, "tempOdfFile");

            foutStream = new FileOutputStream(targetFile);

            byte[] buf = new byte[4096];
            int r = 0;
            while ((r = inStream.read(buf, 0, 4096)) > -1) {
                foutStream.write(buf, 0, r);
            }
            foutStream.close();
            return targetFile;

        } catch (Exception ex) {
            Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
