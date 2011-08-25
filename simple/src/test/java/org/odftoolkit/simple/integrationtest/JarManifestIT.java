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
package org.odftoolkit.simple.integrationtest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.simple.JarManifest;

public class JarManifestIT {

	private static final Logger LOG = Logger.getLogger(JarManifestIT.class.getName());

	@Test
	public void testJar() {
		try {
			String command = "java -jar target/simple.jar";
			Process process = Runtime.getRuntime().exec(command);
			process.waitFor();
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = errorReader.readLine()) != null) {
				LOG.info(line);
				Assert.assertTrue(line.indexOf("Exception") == -1);
			}
			String firstOutputLine = outputReader.readLine();
			String secondOutputLine = outputReader.readLine();
			errorReader.close();
			outputReader.close();
			process.destroy();
			LOG.log(Level.INFO, "The version info from commandline given by {0} is:\n", command);
			LOG.log(Level.INFO, "\"{0}\"", firstOutputLine);
			LOG.log(Level.INFO, "\"{0}\"", secondOutputLine);
			Assert.assertEquals(firstOutputLine, JarManifest.getSimpleTitle() + " (build " + JarManifest.getSimpleBuildDate() + ')');
			Assert.assertEquals(secondOutputLine, "from " + JarManifest.getSimpleWebsite() + " supporting ODF " + JarManifest.getSimpleSupportedOdfVersion());

			LOG.log(Level.INFO, "\nJarManifest.getName(): {0}", JarManifest.getSimpleName());
			Assert.assertNotNull(JarManifest.getSimpleName());

			LOG.log(Level.INFO, "\nJarManifest.getTitle(): {0}", JarManifest.getSimpleTitle());
			Assert.assertNotNull(JarManifest.getSimpleTitle());

			LOG.log(Level.INFO, "\nJarManifest.getVersion(): {0}", JarManifest.getSimpleVersion());
			Assert.assertNotNull(JarManifest.getSimpleVersion());

			LOG.log(Level.INFO, "\nJarManifest.getBuildDate(): {0}", JarManifest.getSimpleBuildDate());
			Assert.assertNotNull(JarManifest.getSimpleBuildDate());

			LOG.log(Level.INFO, "\nJarManifest.getBuildResponsible(): {0}", JarManifest.getSimpleBuildResponsible());
			Assert.assertNotNull(JarManifest.getSimpleBuildResponsible());

			LOG.log(Level.INFO, "\nJarManifest.getSupportedOdfVersion(): {0}", JarManifest.getSimpleSupportedOdfVersion());
			Assert.assertNotNull(JarManifest.getSimpleSupportedOdfVersion());

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
		}
	}
}
