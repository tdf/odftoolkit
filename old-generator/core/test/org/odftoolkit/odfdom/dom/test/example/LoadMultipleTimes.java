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
package org.odftoolkit.odfdom.dom.test.example;

import org.odftoolkit.odfdom.doc.OdfDocument;


public class LoadMultipleTimes {
    
    final static int num = 50;
    public static void main(String[] args) {
        try {
            long t = 0;
            for (int i=0; i<num; i++) {
                long t1 = System.currentTimeMillis();                
                OdfDocument.loadDocument("test/resources/test1.odt");
                long t2 = System.currentTimeMillis() - t1;
                t = t + t2;
                System.out.println("open in " + t2 + " milliseconds");
                long f1 = Runtime.getRuntime().freeMemory();
                Runtime.getRuntime().gc();
                long f2 = Runtime.getRuntime().freeMemory();
                System.out.println("freemem pre-gc: " + f1 + ", post-gc: " + f2 + ", delta: " + (f1 - f2) + ".");
            }
            System.out.println("opening " + num + " times took " + t + " milliseconds");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
