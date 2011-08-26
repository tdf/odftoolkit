/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009 Benson I. Margulies. All rights reserved.
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
 */
package org.odftoolkit.odfdom.maven_performancetest_plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Generate Java code for ODFDOM.
 * @goal performancetest
 * @phase generate-sources
 * @description ODFDOM Code Generator
 * @requiresDependencyResolution compile
*/
public class PerformanceTestMojo extends AbstractMojo {
	
	/**
     * @parameter
     * @required
     */
    String testFileFolder;
    
    /**
     * @parameter default-value="myTest"
     */
    String testLable;
    
    /**
     * @parameter default-value="timelog.ods"
     */
    String timeLogFileName;
    
    /**
     * @parameter default-value="memorylog.ods"
     */
    String memoryLogFileName;

    /**
     * @parameter default-value="10"
     */
    int count;
    
    /* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		//System.out.println("folder="+testFileFolder);
		PerformanceEvaluation test = new PerformanceEvaluation(testFileFolder, testLable,count, timeLogFileName,memoryLogFileName);

		try {
			// 1. Collect Test result
			test.test();
			// 2. Save to spreadsheet
			test.writeToLog();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MojoFailureException("Performance test failed:"+e.getMessage());
		}

	}
}
