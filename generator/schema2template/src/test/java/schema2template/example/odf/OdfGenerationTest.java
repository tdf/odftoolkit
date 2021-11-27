/**
 * *********************************************************************
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
 * <p>******************************************************************
 */
package schema2template.example.odf;

import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import schema2template.model.XMLModel;

public class OdfGenerationTest {

  private static final Logger LOG = Logger.getLogger(OdfGenerationTest.class.getName());
  private static final String REF_BASE_DIR =
      "target"
          + File.separator
          + "test-classes"
          + File.separator
          + "references"
          + File.separator
          + "generated-sources"
          + File.separator
          + "java"
          + File.separator;

  private static String TARGET_BASE_DIR =
      "target" + File.separator + "generated-sources" + File.separator + "java" + File.separator;

  private static String CODEGEN_RESOURCE_DIR =
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
          + File.separator;

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  public void testAllExampleGenerations() {
    try {
      // user.dir ==> generator/schema2template
      String[] contextInfoDom = new String[1];
      String configDomFile =
          CODEGEN_RESOURCE_DIR + File.separator + "dom" + File.separator + "config.xml";
      contextInfoDom[0] = configDomFile;
      String[] contextInfoPkg = new String[1];
      String configPkgFile =
          CODEGEN_RESOURCE_DIR + File.separator + "pkg" + File.separator + "config.xml";
      contextInfoPkg[0] = configPkgFile;

      String templateFilePathPkg =
          CODEGEN_RESOURCE_DIR
              + File.separator
              + "pkg"
              + File.separator
              + "template"
              + File.separator;
      String templateFilePathDom =
          CODEGEN_RESOURCE_DIR
              + File.separator
              + "dom"
              + File.separator
              + "template"
              + File.separator;

      templateFilePathDom =
          Paths.get(System.getProperty("user.dir"), templateFilePathDom).normalize().toString();
      String templateFilePkgManifest = "pkg-manifest-output-files.vm";
      String templateFilePkgSignature = "pkg-dsig-output-files.vm";
      String templateFileDom = "dom-output-files.vm";

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
              + "OpenDocument-v1.2-os-schema.rng";
      String odf11SchemaFile =
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
              + "OpenDocument-schema-v1.1.rng";
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
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("Generation Code Files Root Directory is " + TARGET_BASE_DIR);
      Logger.getLogger(OdfGenerationTest.class.getName()).fine("Config File DOM" + contextInfoDom);
      Logger.getLogger(OdfGenerationTest.class.getName()).fine("Config File PKG" + contextInfoPkg);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("xmlModelOdf13Dom Template Files Directory " + templateFilePathDom);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("ODF1.3 Schema File " + odf13SchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("ODF1.2 Schema File " + odf12SchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("Pkg Template Files Directory " + templateFilePathPkg);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("ODF1.3 Signature Schema File " + odf13SignatureSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("ODF1.2 Signature Schema File " + odf12SignatureSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("ODF1.3 Manifest Schema File " + odf13ManifestSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .fine("ODF1.2 Manifest Schema File " + odf12ManifestSchemaFile);

      XMLModel xmlModelOdf13PkgManifest =
          new XMLModel(new File(odf13ManifestSchemaFile), "Odf 1.3");
      XMLModel xmlModelOdf12PkgManifest =
          new XMLModel(new File(odf12ManifestSchemaFile), "Odf 1.2");
      XMLModel[] xmlModelPkgManifestHistory13 = {xmlModelOdf12PkgManifest};

      XMLModel xmlModelOdf13PkgSignature =
          new XMLModel(new File(odf13SignatureSchemaFile), "Odf 1.3");
      XMLModel xmlModelOdf12PkgSignature =
          new XMLModel(new File(odf12SignatureSchemaFile), "Odf 1.2");
      XMLModel[] xmlModelPkgSignatureHistory13 = {xmlModelOdf12PkgSignature};

      XMLModel xmlModelOdf13Dom = new XMLModel(new File(odf13SchemaFile), "Odf 1.3");
      XMLModel xmlModelOdf12Dom = new XMLModel(new File(odf12SchemaFile), "Odf 1.2");
      XMLModel xmlModelOdf11Dom = new XMLModel(new File(odf12SchemaFile), "Odf 1.1");
      XMLModel xmlModelOdf10Dom = new XMLModel(new File(odf12SchemaFile), "Odf 1.0");
      XMLModel[] xmlModelDomHistory13 = {xmlModelOdf12Dom, xmlModelOdf11Dom, xmlModelOdf10Dom};
      XMLModel[] xmlModelDomHistory12 = {xmlModelOdf11Dom, xmlModelOdf10Dom};
      XMLModel[] xmlModelDomHistory11 = {xmlModelOdf10Dom};
      String targetOdf13 =
          Paths.get(System.getProperty("user.dir"), TARGET_BASE_DIR, "odf1.3")
              .normalize()
              .toString();
      String targetOdf12 =
          Paths.get(System.getProperty("user.dir"), TARGET_BASE_DIR, "odf1.2")
              .normalize()
              .toString();
      String targetOdf11 =
          Paths.get(System.getProperty("user.dir"), TARGET_BASE_DIR, "odf1.1")
              .normalize()
              .toString();
      String targetOdf10 =
          Paths.get(System.getProperty("user.dir"), TARGET_BASE_DIR, "odf1.0")
              .normalize()
              .toString();

      // ******** ODF 1.3 *************
      //      SchemaToTemplate.run(
      //          templateFilePathDom,
      //          templateFileDom,
      //          xmlModelOdf13Dom,
      //          xmlModelDomHistory13,
      //          contextInfoDom,
      //          targetOdf13,
      //          "dom-output-files.xml");
      //
      //      SchemaToTemplate.run(
      //          templateFilePathPkg,
      //          templateFilePkgManifest,
      //          xmlModelOdf13PkgManifest,
      //          xmlModelPkgManifestHistory13,
      //          contextInfoPkg,
      //          targetOdf13,
      //          "pkg-manifest-output-files.xml");
      //
      //      SchemaToTemplate.run(
      //          templateFilePathPkg,
      //          templateFilePkgSignature,
      //          xmlModelOdf13PkgSignature,
      //          xmlModelPkgSignatureHistory13,
      //          contextInfoPkg,
      //          targetOdf13,
      //          "pkg-dsig-output-files.xml");

      // ******** ODF 1.2 *************
      SchemaToTemplate.run(
          templateFilePathDom,
          templateFileDom,
          xmlModelOdf12Dom,
          xmlModelDomHistory12,
          contextInfoDom,
          targetOdf12,
          "dom-output-files.xml");

      SchemaToTemplate.run(
          templateFilePathPkg,
          templateFilePkgManifest,
          xmlModelOdf12PkgManifest,
          null,
          contextInfoPkg,
          targetOdf12,
          "pkg-manifest-output-files.xml");

      SchemaToTemplate.run(
          templateFilePathPkg,
          templateFilePkgSignature,
          xmlModelOdf12PkgSignature,
          null,
          contextInfoPkg,
          targetOdf12,
          "pkg-dsig-output-files.xml");

      // ******** ODF 1.1 *************
      //      SchemaToTemplate.run(
      //          templateFilePathDom,
      //          templateFileDom,
      //          xmlModelOdf11Dom,
      //          xmlModelDomHistory11,
      //          contextInfoDom,
      //          targetOdf11,
      //          "dom-output-files.xml");

      // ******** ODF 1.0 *************
      //      SchemaToTemplate.run(
      //          templateFilePathDom,
      //          templateFileDom,
      //          xmlModelOdf10Dom,
      //          null,
      //          contextInfoDom,
      //          targetOdf10,
      //          "dom-output-files.xml");

      // ******** Reference Test *************
      // **2DO: Compare text file content, but ignore line breaking. Showing lines with the
      // difference!!
      // generated sources must be equal to the previously generated reference sources
      //            Assert.assertTrue(
      //                    "The new generated sources\n\t"
      //                    + Paths.get(targetODF1.2).toAbsolutePath().toString()
      //                    + "\ndiffer from their reference:\n\t"
      //                    + Paths.get(REF_BASE_DIR).toAbsolutePath().toString(),
      //                    DirectoryCompare.directoryContentEquals(
      //                            Paths.get(targetODF1.2), Paths.get(REF_BASE_DIR)));
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }
}
