/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>*********************************************************************
 */
package schema2template.example;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import schema2template.example.odf.OdfHelper;

public class ExampleGenerationTest {

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  @Ignore
  public void testAllExampleGenerations() {
    try {
      // user.dir ==> generator/schema2template
      String configFile =
          ".."
              + File.separator
              + ".."
              + File.separator
              + "odfdom"
              + File.separator
              + "src"
              + File.separator
              + "codegen"
              + File.separator
              + "resources"
              + File.separator
              + "config.xml";
      // String targetRootPath =  ".." + File.separator + ".." + File.separator + "odfdom" +
      // File.separator + "src" + File.separator + "main" + File.separator + "java" +
      // File.separator;
      String targetRootPath =
          ".."
              + File.separator
              + ".."
              + File.separator
              + "target"
              + File.separator
              + "generated-sources"
              + File.separator
              + "java"
              + File.separator;

      String domResourceRootPath =
          ".."
              + File.separator
              + ".."
              + File.separator
              + "odfdom"
              + File.separator
              + "src"
              + File.separator
              + "codegen"
              + File.separator
              + "resources"
              + File.separator
              + "dom"
              + File.separator
              + "template";
      String odf13SchemaFile =
          "src"
              + File.separator
              + "main"
              + File.separator
              + "resources"
              + File.separator
              + "examples"
              + File.separator
              + "odf"
              + File.separator
              + "odf-schemas"
              + File.separator
              + File.separator
              + "OpenDocument-v1.3-schema.rng";
      String odf12SchemaFile =
          "src"
              + File.separator
              + "main"
              + File.separator
              + "resources"
              + File.separator
              + "examples"
              + File.separator
              + "odf"
              + File.separator
              + "odf-schemas"
              + File.separator
              + File.separator
              + "OpenDocument-v1.2-os-schema.rng";

      String pkgResourceRootPath =
          ".."
              + File.separator
              + ".."
              + File.separator
              + "odfdom"
              + File.separator
              + "src"
              + File.separator
              + "codegen"
              + File.separator
              + "resources"
              + File.separator
              + "pkg"
              + File.separator
              + "template";
      String odf13SignatureSchemaFile =
          "src"
              + File.separator
              + "main"
              + File.separator
              + "resources"
              + File.separator
              + "examples"
              + File.separator
              + "odf"
              + File.separator
              + "odf-schemas"
              + File.separator
              + "OpenDocument-v1.3-dsig-schema.rng";
      String odf12SignatureSchemaFile =
          "src"
              + File.separator
              + "main"
              + File.separator
              + "resources"
              + File.separator
              + "examples"
              + File.separator
              + "odf"
              + File.separator
              + "odf-schemas"
              + File.separator
              + "OpenDocument-v1.2-os-dsig-schema.rng";
      String odf13ManifestSchemaFile =
          "src"
              + File.separator
              + "main"
              + File.separator
              + "resources"
              + File.separator
              + "examples"
              + File.separator
              + "odf"
              + File.separator
              + "odf-schemas"
              + File.separator
              + "OpenDocument-v1.3-manifest-schema.rng";
      String odf12ManifestSchemaFile =
          "src"
              + File.separator
              + "main"
              + File.separator
              + "resources"
              + File.separator
              + "examples"
              + File.separator
              + "odf"
              + File.separator
              + "odf-schemas"
              + File.separator
              + "OpenDocument-v1.2-os-manifest-schema.rng";

      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("Generation Code Files Root Directory " + targetRootPath);
      Logger.getLogger(ExampleGenerationTest.class.getName()).fine("Config File " + configFile);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("Dom Template Files Directory " + domResourceRootPath);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("ODF1.3 Schema File " + odf13SchemaFile);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("ODF1.2 Schema File " + odf12SchemaFile);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("Pkg Template Files Directory " + pkgResourceRootPath);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("ODF1.3 Signature Schema File " + odf13SignatureSchemaFile);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("ODF1.2 Signature Schema File " + odf12SignatureSchemaFile);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("ODF1.3 Manifest Schema File " + odf13ManifestSchemaFile);
      Logger.getLogger(ExampleGenerationTest.class.getName())
          .fine("ODF1.2 Manifest Schema File " + odf12ManifestSchemaFile);
      OdfHelper codeGen =
          new OdfHelper(
              domResourceRootPath,
              odf13SchemaFile,
              odf12SchemaFile,
              pkgResourceRootPath,
              odf13SignatureSchemaFile,
              odf12SignatureSchemaFile,
              odf13ManifestSchemaFile,
              odf12ManifestSchemaFile,
              targetRootPath,
              configFile);
      codeGen.generate();
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }

  private static final Logger LOG = Logger.getLogger(ExampleGenerationTest.class.getName());
}
