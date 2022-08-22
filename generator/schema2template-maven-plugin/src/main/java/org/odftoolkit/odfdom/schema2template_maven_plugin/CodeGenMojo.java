/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009 Benson I. Margulies. All rights reserved.
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2022 Svante Schubert. All rights reserved.
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

import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import schema2template.GenerationParameters;

/**
 * Maven Mojo that triggers the generations of files from XML Grammar into Velocity file templates.
 *
 * @phase generate-sources
 * @description As example and its mjor use case see the ODFDOM Code Generator:
 *     https://odftoolkit.org/generator/index.html
 */
@Mojo(
    name = "codegen",
    defaultPhase = LifecyclePhase.GENERATE_SOURCES,
    threadSafe = false,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class CodeGenMojo extends AbstractMojo {

  @Parameter(property = "generations")
  private List<GenerationParameters> generations;

  /* (non-Javadoc)
   * @see org.apache.maven.plugin.Mojo#execute()
   * @goal codegen
   */
  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      //      System.err.println(
      //          "Schema2template code generation started " + generations.size() + " times!");
      //      for (int i = 0; i < generations.size(); i++) {
      //        System.err.println("GrammarVersion: " + generations.get(i).getGrammarVersion());
      //        System.err.println("GrammarID: " + generations.get(i).getGrammarID());
      //        System.err.println("GrammarPath: " + generations.get(i).getGrammarPath());
      //        System.err.println("GrammarAdditionsPath: " +
      // generations.get(i).getGrammarAdditionsPath());
      //        System.err.println("MainTemplatePath: " + generations.get(i).getMainTemplatePath());
      //        System.err.println("++++++++\n");
      //      }
      schema2template.example.odf.SchemaToTemplate.run(generations);
    } catch (Exception ex) {
      getLog().error("Failed to parse template.");
      getLog().error(ex);
      String msg = "Failed to execute CodeGenMojo";
      throw new MojoFailureException(ex, msg, msg);
    }
  }
}
