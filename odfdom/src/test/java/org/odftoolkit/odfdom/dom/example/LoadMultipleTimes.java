/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.dom.example;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;

public class LoadMultipleTimes {

	final static int num = 50;
	private static final Logger LOG = Logger.getLogger(LoadMultipleTimes.class.getName());

	@Test
	@Ignore
	/** A testdocument will be loaded and closed repeatedly and the memory will be logged.
	Loading is tested with and without disc memory usage */
	public void testRepeatedLoading() {
		System.setProperty("org.odftoolkit.odfdom.tmpfile.disable", "true");
		repeatedLoading();
		System.setProperty("org.odftoolkit.odfdom.tmpfile.disable", "false");
		repeatedLoading();
	}

	// ToDO: Load instead the specification as extreme example (not neccessarily 50 times)
	private void repeatedLoading() {
		long t = 0;
		for (int i = 0; i < num; i++) {
			try {
				long t1 = System.currentTimeMillis();
				OdfDocument doc = OdfDocument.loadDocument("src/test/resources/test1.odt");
				OdfContentDom dom1 = doc.getContentDom();
				OdfStylesDom dom2 = doc.getStylesDom();
				long t2 = System.currentTimeMillis() - t1;
				t = t + t2;
				LOG.info("Open in " + t2 + " milliseconds");
				long f1 = Runtime.getRuntime().freeMemory();
				doc.close();
				Runtime.getRuntime().gc();
				long f2 = Runtime.getRuntime().freeMemory();
				LOG.info("Freemem pre-gc: " + f1 + ", post-gc: " + f2 + ", delta: " + (f1 - f2) + ".");
			} catch (Exception ex) {
				Logger.getLogger(LoadMultipleTimes.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		LOG.info("Opening " + num + " times took " + t + " milliseconds");
	}
}
