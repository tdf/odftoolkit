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
package org.openoffice.odf.codegen_maven_plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.openoffice.odf.codegen.CodeGen;

/**
 * Generate Java code for ODFDOM.
 * @goal codegen
 * @phase generate-sources
 * @description ODFDOM Code Generator
 * @requiresDependencyResolution compile
*/
public class CodegenMojo extends AbstractMojo {
	
	/**
     * @parameter default-value="${project.build.directory}/generated/src/main/java"
     * @required
     */
    String sourceRoot;
    
    /**
     * @parameter 
   	 * @required
     */
    File schemaFile;
    
    /**
     * @parameter
     * @required
     */
    File configFile;
    
    /**
     * @parameter 
     * @required
     */
    File templateFile;

    /**
     * @parameter expression="${project}"
     * @required
     */
    MavenProject project;

	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		 CodeGen xThis = new CodeGen(sourceRoot);

         if( xThis.parseConfig(configFile.getAbsolutePath()) ) {
             if( xThis.parseSchema(schemaFile.getAbsolutePath())) {
                 if( xThis.parseTemplate(templateFile.getAbsolutePath())) {
                     if(!xThis.executeTemplate(xThis.getTemplate())) {
                    	 throw new MojoFailureException("Codegen failed.");
                     }
                 }
             }
         }
         if (project != null && sourceRoot != null) {
             project.addCompileSourceRoot(sourceRoot);
         }
	}
}
