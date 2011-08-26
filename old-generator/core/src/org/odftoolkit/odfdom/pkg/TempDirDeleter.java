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
import java.util.ArrayList;
import java.util.Iterator;
    
class TempDirDeleter extends Thread {
    private static TempDirDeleter deleterThread=null;

    private ArrayList<File> dirList = new ArrayList<File>();

    static TempDirDeleter getInstance() {
        if ( deleterThread==null ) {
            deleterThread = new TempDirDeleter();
            Runtime.getRuntime().addShutdownHook(deleterThread);
        }
        return deleterThread;
    }
    
    synchronized void add(File dir) {
        dirList.add(dir);
    }

    synchronized void remove(File dir) {
        dirList.remove(dir);
    }

    public void run() {
        synchronized (this) {
            Iterator iterator = dirList.iterator();
            while (iterator.hasNext()) {
                File dir = (File)iterator.next();
                deleteDirectoryRecursive(dir);
                iterator.remove();
            }
            dirList.clear();
        }
    }
    
    private void deleteDirectoryRecursive(File dir) {
        if ( dir == null ) return;

        File[] fileArray = dir.listFiles();
        
        if (fileArray != null) {
            for (int i = 0; i < fileArray.length; i++) {
                if (fileArray[i].isDirectory())
                    deleteDirectoryRecursive(fileArray[i]);
                else
                    fileArray[i].delete();
            }
        }
        dir.delete();
        getInstance();
    }

    void deleteDirectory(File dir) {
        deleteDirectoryRecursive(dir);
        dirList.remove(dir);
    }
}
