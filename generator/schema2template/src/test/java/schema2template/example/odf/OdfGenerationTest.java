/**
 * *********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
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

  /**
   * Via Maven pom.xml (surefire test plugin) received System variable of the absolute path of the
   * base directory
   *
   * @see https://cwiki.apache.org/confluence/display/MAVEN/Maven+Properties+Guide
   */
  private static final String BASE_DIR = System.getProperty("schema2template.base.dir");

  // The Maven default output directory for generated sources: target/generated-sources/
  private static final String TARGET_REL_DIR =
      File.separator
          + "generated-sources"
          + File.separator
          + "java"
          + File.separator
          + "odf"
          + File.separator
          + "odfdom-java"
          + File.separator;

  private static String ODF_TEMPLATE_DIR =
      Paths.get(
              BASE_DIR
                  + File.separator
                  + "src"
                  + File.separator
                  + "test"
                  + File.separator
                  + "resources"
                  + File.separator
                  + "test-input"
                  + File.separator
                  + "odf"
                  + File.separator
                  + "template"
                  + File.separator
                  + "odfdom-java"
                  + File.separator)
          .normalize()
          .toString();

  /** Test: It should be able to generate all examples without a failure. */
  @Test
  public void testAllExampleGenerations() {
    try {
      // user.dir ==> generator/schema2template
      String[] contextInfoDom = new String[1];
      String configDomFile =
          ODF_TEMPLATE_DIR + File.separator + "dom" + File.separator + "config.xml";
      contextInfoDom[0] = configDomFile;
      String[] contextInfoPkg = new String[1];
      String configPkgFile =
          ODF_TEMPLATE_DIR + File.separator + "pkg" + File.separator + "config.xml";
      contextInfoPkg[0] = configPkgFile;

      String templateFilePathPkg =
          ODF_TEMPLATE_DIR + File.separator + "pkg" + File.separator + "template" + File.separator;
      String templateFilePathDom =
          ODF_TEMPLATE_DIR + File.separator + "dom" + File.separator + "template" + File.separator;

      String templateFilePkgManifest = "pkg-manifest-output-files.vm";
      String templateFilePkgSignature = "pkg-dsig-output-files.vm";
      String templateFileDom = "dom-output-files.vm";

      String odf13SchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-v1.3-schema.rng")
              .normalize()
              .toString();
      String odf12SchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-v1.2-os-schema.rng")
              .normalize()
              .toString();
      String odf11SchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-schema-v1.1.rng")
              .normalize()
              .toString();
      String odf10SchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-schema-v1.0-os.rng")
              .normalize()
              .toString();
      String odf13SignatureSchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-v1.3-dsig-schema.rng")
              .normalize()
              .toString();
      String odf12SignatureSchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-v1.2-os-dsig-schema.rng")
              .normalize()
              .toString();
      String odf13ManifestSchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-v1.3-manifest-schema.rng")
              .normalize()
              .toString();
      String odf12ManifestSchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-v1.2-os-manifest-schema.rng")
              .normalize()
              .toString();
      String odf11ManifestSchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-manifest-schema-v1.1.rng")
              .normalize()
              .toString();
      String odf10ManifestSchemaFile =
          Paths.get(
                  BASE_DIR
                      + File.separator
                      + "src"
                      + File.separator
                      + "test"
                      + File.separator
                      + "resources"
                      + File.separator
                      + "test-input"
                      + File.separator
                      + "odf"
                      + File.separator
                      + "grammar"
                      + File.separator
                      + "OpenDocument-manifest-schema-v1.0-os.rng")
              .normalize()
              .toString();
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("Generation Code Files Root Directory is " + TARGET_REL_DIR);
      Logger.getLogger(OdfGenerationTest.class.getName()).info("Config File DOM" + contextInfoDom);
      Logger.getLogger(OdfGenerationTest.class.getName()).info("Config File PKG" + contextInfoPkg);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("xmlModelOdf13Dom Template Files Directory " + templateFilePathDom);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.3 Schema File " + odf13SchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.2 Schema File " + odf12SchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("Pkg Template Files Directory " + templateFilePathPkg);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.3 Signature Schema File " + odf13SignatureSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.2 Signature Schema File " + odf12SignatureSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.3 Manifest Schema File " + odf13ManifestSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.2 Manifest Schema File " + odf12ManifestSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.1 Manifest Schema File " + odf11ManifestSchemaFile);
      Logger.getLogger(OdfGenerationTest.class.getName())
          .info("ODF1.0 Manifest Schema File " + odf10ManifestSchemaFile);

      XMLModel xmlModelOdf13PkgManifest =
          new XMLModel(new File(odf13ManifestSchemaFile), "Odf 1.3");
      XMLModel xmlModelOdf12PkgManifest =
          new XMLModel(new File(odf12ManifestSchemaFile), "Odf 1.2");
      XMLModel xmlModelOdf11PkgManifest =
          new XMLModel(new File(odf11ManifestSchemaFile), "Odf 1.1");
      XMLModel xmlModelOdf10PkgManifest =
          new XMLModel(new File(odf10ManifestSchemaFile), "Odf 1.0");
      XMLModel[] xmlModelPkgManifestHistory13 = {
        xmlModelOdf12PkgManifest, xmlModelOdf11PkgManifest, xmlModelOdf10PkgManifest
      };
      XMLModel[] xmlModelPkgManifestHistory12 = {
        xmlModelOdf11PkgManifest, xmlModelOdf10PkgManifest
      };
      XMLModel[] xmlModelPkgManifestHistory11 = {xmlModelOdf10PkgManifest};

      XMLModel xmlModelOdf13PkgSignature =
          new XMLModel(new File(odf13SignatureSchemaFile), "Odf 1.3");
      XMLModel xmlModelOdf12PkgSignature =
          new XMLModel(new File(odf12SignatureSchemaFile), "Odf 1.2");
      XMLModel[] xmlModelPkgSignatureHistory13 = {xmlModelOdf12PkgSignature};

      XMLModel xmlModelOdf13Dom = new XMLModel(new File(odf13SchemaFile), "Odf 1.3");
      XMLModel xmlModelOdf12Dom = new XMLModel(new File(odf12SchemaFile), "Odf 1.2");
      XMLModel xmlModelOdf11Dom = new XMLModel(new File(odf11SchemaFile), "Odf 1.1");
      XMLModel xmlModelOdf10Dom = new XMLModel(new File(odf10SchemaFile), "Odf 1.0");
      XMLModel[] xmlModelDomHistory13 = {xmlModelOdf12Dom, xmlModelOdf11Dom, xmlModelOdf10Dom};
      XMLModel[] xmlModelDomHistory12 = {xmlModelOdf11Dom, xmlModelOdf10Dom};
      XMLModel[] xmlModelDomHistory11 = {xmlModelOdf10Dom};
      String targetOdf13 = Paths.get(BASE_DIR, TARGET_REL_DIR, "odf1.3").normalize().toString();
      String targetOdf12 = Paths.get(BASE_DIR, TARGET_REL_DIR, "odf1.2").normalize().toString();
      String targetOdf11 = Paths.get(BASE_DIR, TARGET_REL_DIR, "odf1.1").normalize().toString();
      String targetOdf10 = Paths.get(BASE_DIR, TARGET_REL_DIR, "odf1.0").normalize().toString();

      // ******** ODF 1.3 *************
      SchemaToTemplate.run(
          templateFilePathDom,
          templateFileDom,
          xmlModelOdf13Dom,
          xmlModelDomHistory13,
          contextInfoDom,
          targetOdf13,
          "odf13-dom-output-files.xml");

      SchemaToTemplate.run(
          templateFilePathPkg,
          templateFilePkgManifest,
          xmlModelOdf13PkgManifest,
          xmlModelPkgManifestHistory13,
          contextInfoPkg,
          targetOdf13,
          "odf13-pkg-manifest-output-files.xml");

      SchemaToTemplate.run(
          templateFilePathPkg,
          templateFilePkgSignature,
          xmlModelOdf13PkgSignature,
          xmlModelPkgSignatureHistory13,
          contextInfoPkg,
          targetOdf13,
          "odf13-pkg-dsig-output-files.xml");

      // ******** ODF 1.2 *************
      SchemaToTemplate.run(
          templateFilePathDom,
          templateFileDom,
          xmlModelOdf12Dom,
          xmlModelDomHistory12,
          contextInfoDom,
          targetOdf12,
          "odf12-dom-output-files.xml");

      SchemaToTemplate.run(
          templateFilePathPkg,
          templateFilePkgManifest,
          xmlModelOdf12PkgManifest,
          xmlModelPkgManifestHistory12,
          contextInfoPkg,
          targetOdf12,
          "odf12-pkg-manifest-output-files.xml");

      SchemaToTemplate.run(
          templateFilePathPkg,
          templateFilePkgSignature,
          xmlModelOdf12PkgSignature,
          null,
          contextInfoPkg,
          targetOdf12,
          "odf12-pkg-dsig-output-files.xml");

      // ******** ODF 1.1 *************
      SchemaToTemplate.run(
          templateFilePathDom,
          templateFileDom,
          xmlModelOdf11Dom,
          xmlModelDomHistory11,
          contextInfoDom,
          targetOdf11,
          "odf11-dom-output-files.xml");

      // ******** ODF 1.0 *************
      SchemaToTemplate.run(
          templateFilePathDom,
          templateFileDom,
          xmlModelOdf10Dom,
          xmlModelPkgManifestHistory11,
          contextInfoDom,
          targetOdf10,
          "odf11-dom-output-files.xml");

      // ******** Reference Test *************
      // **2DO: Compare text file content, but ignore line breaking. Showing lines with the
      // difference!!
      // generated sources must be equal to the previously generated reference sources
      //            Assert.assertTrue(
      //                    "The new generated sources\n\t"
      //                    + Paths.get(targetODF1.2).toAbsolutePath().toString()
      //                    + "\ndiffer from their reference:\n\t"
      //                    + Paths.get(TARGET_REL_DIR).toAbsolutePath().toString(),
      //                    DirectoryCompare.directoryContentEquals(
      //                            Paths.get(targetODF1.2), Paths.get(TARGET_REL_DIR)));
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
      Assert.fail(ex.toString());
    }
  }
}
