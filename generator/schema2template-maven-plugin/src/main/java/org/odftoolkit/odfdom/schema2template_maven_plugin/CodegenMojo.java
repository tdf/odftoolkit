/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009 Benson I. Margulies. All rights reserved.
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved. 
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
package org.odftoolkit.odfdom.schema2template_maven_plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import schema2template.example.odf.OdfHelper;

/**
 * Generate Java code for ODFDOM.
 * @goal codegen
 * @phase generate-sources
 * @description ODFDOM Code Generator
 * @requiresDependencyResolution compile
 */
public class CodegenMojo extends AbstractMojo {

	/**
	 * @parameter
	 * @required
	 */
	File domResourceRoot;
	
	/**
	 * @parameter
	 * @required
	 */
	String odf12SchemaFile;
	
	/**
	 * @parameter
	 * @required
	 */
	String odf11SchemaFile;
	
	/**
	 * @parameter
	 * @required
	 */
	File pkgResourceRoot;
	
	/**
	 * @parameter
	 * @required
	 */
	String signatureSchemaFile;
	
	/**
	 * @parameter
	 * @required
	 */
	String manifestSchemaFile;
	
	/**
	 * @parameter
	 * @required
	 */
	File targetRoot;
	
	/**
	 * @parameter
	 * @required
	 */
	String configFile;
	
	/**
	 * @parameter expression="${project}"
	 * @required
	 */
	MavenProject project;

	/* (non-Javadoc)
	 * @see org.apache.maven.plugin.Mojo#execute()
	 * @goal codegen
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			getLog().info("Schema2template code generation.");
			if (configFile == null) {
				getLog().error("Please set configure file patch.");
				throw new MojoFailureException("Please set configure file patch.");
			}
			if (odf12SchemaFile == null) {
				getLog().error("Please set odf1.2 schema file patch.");
				throw new MojoFailureException("Please set schema file patch.");
			}
			if (odf11SchemaFile == null) {
				getLog().error("Please set odf1.1 schema file patch.");
				throw new MojoFailureException("Please set schema file patch.");
			}
			if (signatureSchemaFile == null) {
				getLog().error("Please set odf1.2 document signature schema file patch.");
				throw new MojoFailureException("Please set schema file patch.");
			}
			if (manifestSchemaFile == null) {
				getLog().error("Please set odf1.2 manifest schema file patch.");
				throw new MojoFailureException("Please set schema file patch.");
			}
			String targetRootPath = targetRoot.getAbsolutePath();
			if (targetRootPath == null) {
				getLog().error("Please set generation code root patch.");
				throw new MojoFailureException("Please set generation code root patch.");
			}
			String domResourceRootPath = domResourceRoot.getAbsolutePath();
			if (domResourceRootPath == null) {
				getLog().error("Please set dom templates root patch.");
				throw new MojoFailureException("Please set templates root patch.");
			}
			String pkgResourceRootPath = pkgResourceRoot.getAbsolutePath();
			if (pkgResourceRootPath == null) {
				getLog().error("Please set pkg templates root patch.");
				throw new MojoFailureException("Please set templates root patch.");
			}
			getLog().debug("Generation Code Files Root Directory " + targetRootPath);
			getLog().debug("Config File " + configFile);
			getLog().debug("Dom Template Files Directory " + domResourceRootPath);
			getLog().debug("ODF1.2 Schema File " + odf12SchemaFile);
			getLog().debug("ODF1.1 Schema File " + odf11SchemaFile);
			getLog().debug("Pkg Template Files Directory " + pkgResourceRootPath);
			getLog().debug("ODF1.2 Signature Schema File " + signatureSchemaFile);
			getLog().debug("ODF1.2 Manifest Schema File " + manifestSchemaFile);
			OdfHelper codeGen = new OdfHelper(domResourceRootPath, odf12SchemaFile, odf11SchemaFile, pkgResourceRootPath, signatureSchemaFile, manifestSchemaFile, targetRootPath, configFile);
			codeGen.start();
		} catch (Exception ex) {
			getLog().error("Failed to parse template.");
			getLog().error(ex);
			String msg = "Failed to execute ODF schema2template example";
			throw new MojoFailureException(ex, msg, msg);
		}

	}
}
