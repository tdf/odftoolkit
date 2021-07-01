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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Generate Java code for ODFDOM.
 *
 * @phase generate-sources
 * @description ODFDOM Code Generator
 */
@Mojo(
    name = "codegen",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    threadSafe = true,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class CodeGenMojo extends AbstractMojo {

  // @Parameter
  // private GenerationParameters[] generations;

  /* (non-Javadoc)
   * @see org.apache.maven.plugin.Mojo#execute()
   * @goal codegen
   */
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      getLog().info("Schema2template code generation.");

      // new schema2template.example.odf.OdfHelper(generations);
    } catch (Exception ex) {
      getLog().error("Failed to parse template.");
      getLog().error(ex);
      String msg = "Failed to execute ODF schema2template example";
      throw new MojoFailureException(ex, msg, msg);
    }
  }
}
