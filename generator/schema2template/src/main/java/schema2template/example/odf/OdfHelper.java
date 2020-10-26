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
 * <p>**********************************************************************
 */
package schema2template.example.odf;

import com.sun.msv.grammar.Expression;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import schema2template.OutputFileListEntry;
import schema2template.OutputFileListHandler;
import schema2template.model.XMLModel;

/**
 * Three ODF examples in one: 1) Create an ODF Reference in HTMLl 2) Create Source Code 3) Create
 * simple ODF Python source
 */
public class OdfHelper {

  private static final Logger LOG = Logger.getLogger(OdfHelper.class.getName());
  public static final Boolean DEBUG = Boolean.FALSE;
  /**
   * Expresses the amount of elements in ODF 1.1. There are some issues in the schema that have to
   * be fixed before the full number can be returned by MSV: Reference table-table-template is never
   * used, therefore several elements are not taking into account:: "table:body"
   * "table:even-columns" "table:even-rows" "table:first-column" "table:first-row"
   * "table:last-column" "table:last-row" "table:odd-columns" "table:odd-rows"
   * "table:table-template" NOTE: Ignoring the '*' there can be 525 elements parsed, but with fixed
   * schema it should be 535.
   */
  public static final int ODF11_ELEMENT_NUMBER =
      525; // ToDo: 535 - by search/Replace using RNGSchema and tools, prior exchange <name> to
  // element or attribute declaration

  public static final int ODF12_ELEMENT_NUMBER = 598;
  /**
   * Expresses the amount of attributes in ODF 1.1. There are some issues in the schema that have to
   * be fixed before the full number can be returned by MSV: Following references are never used,
   * therefore its attribute is not taking into account:: draw-glue-points-attlist with
   * "draw:escape-direction" office-process-content with "office:process-content" (DEPRECATED in
   * ODF1.2 only on foreign elements)
   *
   * <p>Following attributes are member of the not referenced element "table:table-template":
   * "text:first-row-end-column" "text:first-row-start-column" "text:last-row-end-column"
   * "text:last-row-start-column" "text:paragraph-style-name"
   *
   * <p>NOTE: Ignoring the '*' there can be 1162 elements parsed, but with fixed schema it should be
   * 1169.
   */
  public static final int ODF11_ATTRIBUTE_NUMBER =
      1162; // ToDo: 1169 - by search/Replace using RNGSchema and tools, prior exchange <name> to
  // element or attribute declaration

  public static final int ODF12_ATTRIBUTE_NUMBER =
      1300; // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced
  // (ie. @office:process-content)

  public static final String TEST_INPUT_ROOT_ODF =
      "target" + File.separator + "classes" + File.separator + "examples" + File.separator + "odf";
  public static final String ODF10_RNG_FILE =
      TEST_INPUT_ROOT_ODF
          + File.separator
          + "odf-schemas"
          + File.separator
          + "OpenDocument-strict-schema-v1.0-os.rng";
  public static String odf11RngFile =
      TEST_INPUT_ROOT_ODF
          + File.separator
          + "odf-schemas"
          + File.separator
          + "OpenDocument-strict-schema-v1.1.rng";
  public static String odf12RngFile =
      TEST_INPUT_ROOT_ODF
          + File.separator
          + "odf-schemas"
          + File.separator
          + "OpenDocument-v1.2-os-schema.rng";
  public static String odf12SignatureRngFile =
      TEST_INPUT_ROOT_ODF
          + File.separator
          + "odf-schemas"
          + File.separator
          + "OpenDocument-v1.2-os-dsig-schema.rng";
  public static String odf12ManifestRngFile =
      TEST_INPUT_ROOT_ODF
          + File.separator
          + "odf-schemas"
          + File.separator
          + "OpenDocument-v1.2-os-manifest-schema.rng";

  public static String mConfigFile = TEST_INPUT_ROOT_ODF + File.separator + "config.xml";

  // Home of test reference output of MSV ODF dump: odf10-msvtree.ref, odf11-msvtree.ref,
  // odf12-msvtree.ref
  public static final String TEST_REFERENCE_DIR =
      "target"
          + File.separator
          + "test-classes"
          + File.separator
          + "examples"
          + File.separator
          + "odf";

  private static final String REFERENCE_OUTPUT_FILES_TEMPLATE = "ref-output-file.vm";
  private static final String REFERENCE_OUTPUT_FILES =
      TEST_INPUT_ROOT_ODF
          + File.separator
          + "odf-reference"
          + File.separator
          + "reference-output-files.xml";

  private static String odfDomResourceDir =
      TEST_INPUT_ROOT_ODF + File.separator + "odfdom-java" + File.separator + "dom";
  private static String odfPkgResourceDir =
      TEST_INPUT_ROOT_ODF + File.separator + "odfdom-java" + File.separator + "pkg";
  private static final String ODF_PYTHON_RESOURCE_DIR =
      TEST_INPUT_ROOT_ODF + File.separator + "odfdom-python";

  private static final String DOM_OUTPUT_FILES_TEMPLATE = "dom-output-files.vm";
  private static final String PKG_OUTPUT_FILES_TEMPLATE = "pkg-output-files.vm";
  private static final String PYTHON_OUTPUT_FILES_TEMPLATE = "dom-output-files.vm";

  private static final String DOM_OUTPUT_FILES = "target" + File.separator + "dom-output-files.xml";
  private static final String PKG_OUTPUT_FILES = "target" + File.separator + "pkg-output-files.xml";
  private static final String PYTHON_OUTPUT_FILES =
      "target" + File.separator + "python-output-files.xml";

  private static String outputRoot = "target";
  private static final String ODF_REFERENCE_RESOURCE_DIR =
      TEST_INPUT_ROOT_ODF + File.separator + "odf-reference";

  private static XMLModel mOdf12SignatureSchemaModel;
  private static XMLModel mOdf12ManifestSchemaModel;
  private static XMLModel mOdf12SchemaModel;
  private static XMLModel mOdf11SchemaModel;
  private static OdfModel mOdfModel;
  private static SourceCodeModel mJavaModelOdf;

  public OdfHelper() {}

  public OdfHelper(
      String domResourceRoot,
      String odf12SchemaFile,
      String odf11SchemaFile,
      String pkgResourceRoot,
      String odf12SignatureSchemaFile,
      String odf12ManifestSchemaFile,
      String targetRoot,
      String configFile) {
    odfDomResourceDir = domResourceRoot;
    odfPkgResourceDir = pkgResourceRoot;
    odf11RngFile = odf11SchemaFile;
    odf12RngFile = odf12SchemaFile;
    odf12SignatureRngFile = odf12SignatureSchemaFile;
    odf12ManifestRngFile = odf12ManifestSchemaFile;
    outputRoot = targetRoot;
    mConfigFile = configFile;
  }

  public static void main(String[] args) throws Exception {
    LOG.info("Starting code generation:");
    LOG.info("Starting initilization..");
    // Read config.xml 2DO WHAT IS ODFDOM GENERATOR CONFIG FILE
    // Manual added Java specific info - Base class for inheritance
    Map<String, String> elementToBaseNameMap = new HashMap<String, String>();
    // Manual added ODF specific info - style family mapping
    Map<String, List<String>> elementStyleFamiliesMap = new HashMap<String, List<String>>();
    // 2DO - still existent? -- Manual added Java specific info - mapping ODF datatype to Java
    // datatype  -> {odfValueType, javaConversionClassName}
    Map<String, String[]> datatypeValueAndConversionMap = new HashMap<String, String[]>();
    Map<String, OdfModel.AttributeDefaults> attributeDefaultMap =
        new HashMap<String, OdfModel.AttributeDefaults>();
    OdfConfigFileHandler.readConfigFile(
        new File(mConfigFile),
        elementToBaseNameMap,
        attributeDefaultMap,
        elementStyleFamiliesMap,
        datatypeValueAndConversionMap);

    //        mOdf12SignatureSchemaModel = new XMLModel(new File(odf12SignatureRngFile));
    mOdf12ManifestSchemaModel = new XMLModel(new File(odf12ManifestRngFile));
    mOdf12SchemaModel = new XMLModel(new File(odf12RngFile));
    //        mOdf11SchemaModel = new XMLModel(new File(odf11RngFile));
    //
    mOdfModel = new OdfModel(elementStyleFamiliesMap, attributeDefaultMap);
    //        // Needed for the base classes - common attributes are being moved into the base
    // classes
    mJavaModelOdf =
        new SourceCodeModel(
            mOdf12SchemaModel,
            mOdf12SignatureSchemaModel,
            mOdf12ManifestSchemaModel,
            mOdfModel,
            elementToBaseNameMap,
            datatypeValueAndConversionMap);
    LOG.info("Finished initilization..");
    // HTML Reference for ODF 1.2 (yet without BNF nor images)
    fillTemplates(
        ODF_REFERENCE_RESOURCE_DIR,
        mOdf12SchemaModel.mRootExpression,
        REFERENCE_OUTPUT_FILES_TEMPLATE,
        REFERENCE_OUTPUT_FILES);

    // ODF 1.2 Python (The generated Python source is from a former colleague and might not work any
    // longer..)
    fillTemplates(
        ODF_PYTHON_RESOURCE_DIR,
        mOdf12SchemaModel.mRootExpression,
        PYTHON_OUTPUT_FILES_TEMPLATE,
        PYTHON_OUTPUT_FILES);

    //        // ODF 1.2 Code Generation
    //        fillTemplates(odfDomResourceDir, mOdf12SchemaModel.mRootExpression,
    // DOM_OUTPUT_FILES_TEMPLATE, DOM_OUTPUT_FILES);
    //        fillTemplates(odfPkgResourceDir, mOdf12ManifestSchemaModel.mRootExpression,
    // PKG_OUTPUT_FILES_TEMPLATE, PKG_OUTPUT_FILES);
    //        fillTemplates(odfPkgResourceDir, mOdf12SignatureSchemaModel.mRootExpression,
    // PKG_OUTPUT_FILES_TEMPLATE, PKG_OUTPUT_FILES);

  }

  private static void fillTemplates(
      String sourceDir, Expression root, String outputRuleTemplate, String outputRuleFile)
      throws Exception {
    // intialising template engine (ie. Velocity)
    Properties props = new Properties();
    props.setProperty("file.resource.loader.path", sourceDir);
    VelocityEngine ve = new VelocityEngine(props);
    ve.init();

    // Create output-files.xml
    createOutputFileList(ve, outputRuleTemplate, outputRuleFile);
    LOG.info("output-files.xml created done.");

    // Process output-files.xml, create output files
    LOG.fine("Processing output files... ");
    processFileList(ve, root, outputRuleFile);
    LOG.fine("DONE.\n");
  }

  private static void createOutputFileList(VelocityEngine ve, String template, String output)
      throws Exception {
    VelocityContext context = getOdfContext(null, null);
    File parentPatch = new File(output).getParentFile();
    if (!parentPatch.exists()) {
      parentPatch.mkdirs();
    }
    FileWriter listout = new FileWriter(new File(output));
    String encoding = "utf-8";
    ve.mergeTemplate(template, encoding, context, listout);
    listout.close();
  }

  public static void processFileList(VelocityEngine ve, Expression root, String outputRuleFile)
      throws Exception {
    File outputFiles = new File(outputRuleFile);
    List<OutputFileListEntry> fl = OutputFileListHandler.readFileListFile(outputFiles);

    for (OutputFileListEntry f : fl) {
      switch (f.getType()) {
        case PATH:
          break;
        case FILE:
          LOG.log(
              Level.INFO,
              "Processing line{0}: Generating file {1}\n",
              new Object[] {f.getLineNumber(), generateFilename(f.getAttribute("path"))});
          String odfContextStr = f.getAttribute("context");
          String param = f.getAttribute("param");
          VelocityContext context = getOdfContext(odfContextStr, param);
          if (context == null) {
            throw new RuntimeException(
                "Error in output-files.xml, line "
                    + f.getLineNumber()
                    + ": no or invalid odf-scope");
          }

          File out =
              new File(outputRoot + File.separator + generateFilename(f.getAttribute("path")))
                  .getCanonicalFile();
          ensureParentFolders(out);
          FileWriter fileout = new FileWriter(out);
          String encoding = "utf-8";

          ve.mergeTemplate(f.getAttribute("template"), encoding, context, fileout);
          fileout.close();
          break;
      }
    }
  }

  private static VelocityContext getOdfContext(String contextStr, String param) {
    VelocityContext context = new VelocityContext();
    context.put("signaturemodel", mOdf12SignatureSchemaModel);
    context.put("manifestmodel", mOdf12ManifestSchemaModel);
    context.put("model", mOdf12SchemaModel);
    context.put("oldmodel", mOdf11SchemaModel);
    context.put("odfmodel", mOdfModel);
    context.put("javamodel", mJavaModelOdf);
    context.put("context", contextStr);
    context.put("param", param);
    return context;
  }

  /**
   * Load and parse the ODF 1.0 Schema.
   *
   * @return MSV Expression Tree of ODF 1.0 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  public static Expression loadSchemaODF10() throws Exception {
    return XMLModel.loadSchema(new File(ODF10_RNG_FILE));
  }

  /**
   * Load and parse the ODF 1.1 Schema.
   *
   * @return MSV Expression Tree of ODF 1.1 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  public static Expression loadSchemaODF11() throws Exception {
    return XMLModel.loadSchema(new File(odf11RngFile));
  }

  /**
   * Load and parse the ODF 1.2 Schema.
   *
   * @return MSV Expression Tree of ODF 1.2 RelaxNG schema (more specific: The tree's MSV root
   *     expression)
   * @throws Exception
   */
  public static Expression loadSchemaODF12() throws Exception {
    return XMLModel.loadSchema(new File(odf12RngFile));
  }

  private static String generateFilename(String rawName) {
    String retFilePath = null;
    StringTokenizer toktok = new StringTokenizer(rawName.replaceAll(":", "_"), "/");
    if (toktok.hasMoreTokens()) {
      File retfile = null;
      retfile = new File(toktok.nextToken());
      while (toktok.hasMoreTokens()) {
        retfile = new File(retfile, toktok.nextToken());
      }
      retFilePath = retfile.getPath();
    }
    return retFilePath;
  }

  private static void ensureParentFolders(File newFile) {
    File parent = newFile.getParentFile();
    if (parent != null && !parent.exists()) {
      try {
        parent.mkdirs();
      } catch (Exception e) {
        LOG.log(Level.WARNING, "Could not create parent directory {0}", parent.getAbsolutePath());
      }
    }
  }
}
