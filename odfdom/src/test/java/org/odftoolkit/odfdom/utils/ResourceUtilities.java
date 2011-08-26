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
package org.odftoolkit.odfdom.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Test utitility class providing resources for the test in- and output */
public final class ResourceUtilities {

	private ResourceUtilities() {
	}

	/** The relative path of the test file will be resolved and the absolute will be returned
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return the absolute path of the test file
	 * @throws FileNotFoundException If the file could not be found
	 */
	public static String getAbsolutePath(String relativeFilePath) throws FileNotFoundException {
		URI uri = null;
		try {
			uri = ResourceUtilities.class.getClassLoader().getResource(relativeFilePath).toURI();
		} catch (URISyntaxException ex) {
			Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (uri == null) {
			throw new FileNotFoundException("Could not find the file '" + relativeFilePath + "'!");
		}
		return uri.getPath();
	}

	/** The relative path of the test file will be resolved and the absolute will be returned
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return the URI created based on the relativeFilePath
	 * @throws URISyntaxException if no URI could be created from the given relative path
	 */
	public static URI getURI(String relativeFilePath) throws URISyntaxException {
		String filePath = "file:" + ResourceUtilities.class.getClassLoader().getResource(relativeFilePath).getPath();
		return new URI(filePath);		
		//return ResourceUtilities.class.getClassLoader().getResource(relativeFilePath).toURI();
	}

	/** The relative path of the test file will be used to determine an absolute
	 *  path to a temporary directory in the output directory.
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return absolute path to a test output
	 * @throws IOException if no absolute Path could be created.
	 */
	public static String getTestOutput(String relativeFilePath) throws IOException {
		return File.createTempFile(relativeFilePath, null).getAbsolutePath();
	}

	/** The Input of the test file will be resolved and the absolute will be returned
	 * @param relativeFilePath Path of the test resource relative to <code>src/test/resource/</code>.
	 * @return the absolute path of the test file
	 */
	public static InputStream getTestResourceAsStream(String relativeFilePath) {
		return ResourceUtilities.class.getClassLoader().getResourceAsStream(relativeFilePath);
	}

	/** Relative to the test output directory a test file will be returned dependent on the relativeFilePath provided.
	 * @param relativeFilePath Path of the test output resource relative to <code>target/test-classes/</code>.
	 * @return the empty <code>File</code> of the test output (to be filled)
	 */
	public static File newTestOutputFile(String relativeFilePath) {
		String filepath = null;
		try {
			filepath = ResourceUtilities.class.getClassLoader().getResource("").toURI().getPath() + relativeFilePath;
		} catch (URISyntaxException ex) {
			Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
		}
		return new File(filepath);
	}

	/** 
	 * @return the absolute path of the test output folder, which is usually <code>target/test-classes/</code>.
	 */
	public static String getTestOutputFolder() {
		String testFolder = null;
		try {
			testFolder = ResourceUtilities.class.getClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException ex) {
			Logger.getLogger(ResourceUtilities.class.getName()).log(Level.SEVERE, null, ex);
		}
		return testFolder;		
	}
}
