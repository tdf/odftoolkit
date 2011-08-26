/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
package org.odftoolkit.odfdom.integrationtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.JarManifest;

public class JarManifestIT {

	private static final Logger LOG = Logger.getLogger(JarManifestIT.class.getName());

	@Test
	public void testJar() {
		try {
			String line;
			String command = "java -jar target/odfdom.jar";
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			LOG.log(Level.INFO, "The version info from commandline given by {0} is:\n", command);
			while ((line = bufferedReader.readLine()) != null) {
				LOG.info(line);
				Assert.assertTrue(line.indexOf("Exception") == -1);
			}
			LOG.log(Level.INFO, "\nJarManifest.getName(): {0}", JarManifest.getOdfdomName());
			Assert.assertNotNull(JarManifest.getOdfdomName());

			LOG.log(Level.INFO, "\nJarManifest.getTitle(): {0}", JarManifest.getOdfdomTitle());
			Assert.assertNotNull(JarManifest.getOdfdomTitle());

			LOG.log(Level.INFO, "\nJarManifest.getVersion(): {0}", JarManifest.getOdfdomVersion());
			Assert.assertNotNull(JarManifest.getOdfdomVersion());

			LOG.log(Level.INFO, "\nJarManifest.getBuildDate(): {0}", JarManifest.getOdfdomBuildDate());
			Assert.assertNotNull(JarManifest.getOdfdomBuildDate());

			LOG.log(Level.INFO, "\nJarManifest.getBuildResponsible(): {0}", JarManifest.getOdfdomBuildResponsible());
			Assert.assertNotNull(JarManifest.getOdfdomBuildResponsible());

			LOG.log(Level.INFO, "\nJarManifest.getSupportedOdfVersion(): {0}", JarManifest.getOdfdomSupportedOdfVersion());
			Assert.assertNotNull(JarManifest.getOdfdomSupportedOdfVersion());

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
		}
	}
}
