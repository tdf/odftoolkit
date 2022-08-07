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
 * <p>*******************************************************************
 */
package schema2template.example.odf;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import schema2template.OutputFileListEntry;
import schema2template.OutputFileListHandler;
import schema2template.model.XMLModel;

/**
 * Three ODF examples in one: 1) Create an ODF Reference in HTMLl 2) Create Source Code 3) Create
 * simple ODF Python source
 */
public class SchemaToTemplate {

  private static final Logger LOG = Logger.getLogger(SchemaToTemplate.class.getName());
  public static final Boolean DEBUG = Boolean.FALSE;

  private SchemaToTemplate() {};

  public static void run(
      String templateBaseDir,
      String templatefileName,
      XMLModel xmlModel,
      XMLModel[] xmlModelHistory,
      String[] contextInfo,
      String outputBaseDir,
      String outputFileName) {

    LOG.log(Level.INFO, "Template Base Directory {0}", templateBaseDir);
    LOG.log(Level.INFO, "Template file name{0}", templatefileName);
    LOG.log(Level.INFO, "Output Base Directory {0}", outputBaseDir);
    LOG.log(Level.INFO, "Output File Name  {0}", outputFileName);

    if (contextInfo != null) {
      for (int i = 0; i < contextInfo.length; i++) {
        String s = contextInfo[i];
        LOG.log(Level.INFO, "\tContext Info #{0}: {1}\n", new Object[] {i, s});
      }
    }

    try {
      fillTemplates(
          templateBaseDir,
          templatefileName,
          outputBaseDir,
          outputFileName,
          initContext(xmlModel, xmlModelHistory, contextInfo));
    } catch (Exception ex) {
      Logger.getLogger(SchemaToTemplate.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static VelocityContext initContext(
      XMLModel xmlModel, XMLModel[] xmlModelHistory, String[] contextInfo) throws Exception {
    LOG.info("Starting initilization of Velocity context..");

    VelocityContext context = null;
    if (contextInfo != null && contextInfo.length > 0) {
      // Read config.xml
      // Manual added Java specific info - Base class for inheritance
      Map<String, String> elementToBaseNameMap = new HashMap<>();
      // Manual added ODF specific info - style family mapping
      Map<String, List<String>> elementStyleFamiliesMap = new HashMap<>();
      // 2DO - still existent? -- Manual added Java specific info - mapping ODF datatype to Java
      // datatype  -> {odfValueType, javaConversionClassName}
      Map<String, String[]> datatypeValueAndConversionMap = new HashMap<>();
      Map<String, OdfModel.AttributeDefaults> attributeDefaultMap = new HashMap<>();
      OdfConfigFileHandler.readConfigFile(
          new File(contextInfo[0]),
          elementToBaseNameMap,
          attributeDefaultMap,
          elementStyleFamiliesMap,
          datatypeValueAndConversionMap);
      // odfConstants
      OdfModel odfModel = new OdfModel(elementStyleFamiliesMap, attributeDefaultMap);
      // Needed for the base classes - common attributes are being moved into the base classes
      SourceCodeModel sourceCodeModel =
          new SourceCodeModel(
              xmlModel, odfModel, elementToBaseNameMap, datatypeValueAndConversionMap);
      context = new VelocityContext();
      context.put("xmlModel", xmlModel);
      context.put("odfModel", odfModel);
      context.put("codeModel", sourceCodeModel);
      context.put("xmlModelHistory", xmlModelHistory);
      Map<String, List<String>> styleFamilyPropertiesMap =
          new OdfFamilyPropertiesPatternMatcher(xmlModel.getGrammar()).getFamilyProperties();
      /* Only works for part 3 schema:
        assert styleFamilyPropertiesMap != null && !styleFamilyPropertiesMap.isEmpty()
          : "The @style:family<->'properties elements' map must not be empty!";
      */
      context.put("styleFamilyPropertiesMap", styleFamilyPropertiesMap);
    }
    LOG.info("Finished initialization..");
    return context;
  }

  private static void fillTemplates(
      String templateBaseDir,
      String templatefileName,
      String outputBaseDir,
      String outputFileName,
      VelocityContext context)
      throws Exception {

    // initializing template engine instance (ie. Velocity engine)
    LOG.info("Starting code generation:");
    Properties props = new Properties();
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
    ve.setProperty(
        RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
        Paths.get(templateBaseDir).normalize().toString());
    // Using backward compatible whitespace gobbling/eating
    // http://velocity.apache.org/engine/2.3/developer-guide.html#backward-compatible-space-gobbling
    ve.setProperty(RuntimeConstants.SPACE_GOBBLING, "bc");
    ve.init();
    createOutputFileList(ve, templatefileName, outputBaseDir, outputFileName, context);
    LOG.info("output-files.xml created done.");

    // Process output-files.xml, create output files
    LOG.fine("Processing output files... ");
    processFileList(ve, outputBaseDir, outputFileName, context);
    LOG.fine("DONE.\n");
  }

  private static void createOutputFileList(
      VelocityEngine ve,
      String templatefileName,
      String outputBaseDir,
      String outputFileName,
      VelocityContext context)
      throws Exception {
    File outputFileList = new File(outputBaseDir + File.separator + outputFileName);
    ensureParentFolders(outputFileList);
    try (FileWriter listout = new FileWriter(outputFileList)) {
      String encoding = "utf-8";
      ve.mergeTemplate(templatefileName, encoding, context, listout);
    }
  }

  private static void processFileList(
      VelocityEngine ve, String outputBaseDir, String outputFileName, VelocityContext context)
      throws Exception {
    File outputFileList = new File(outputBaseDir + File.separator + outputFileName);
    List<OutputFileListEntry> fl = OutputFileListHandler.readFileListFile(outputFileList);

    for (OutputFileListEntry f : fl) {
      switch (f.getType()) {
        case PATH:
          break;
        case FILE:
          LOG.log(
              Level.INFO,
              "Processing line{0}: Generating file {1}\n",
              new Object[] {f.getLineNumber(), generateFilename(f.getAttribute("path"))});
          ;
          String contextAttrValue = f.getAttribute("contextNode");
          if (contextAttrValue != null) {
            context.put("contextNode", contextAttrValue);
            LOG.log(Level.INFO, "Added to context: contextNode : {0}", contextAttrValue);
          }
          String param = f.getAttribute("param");
          if (param != null) {
            context.put("param", param);
            LOG.log(Level.INFO, "adding param: {0}", f.getAttribute("param"));
          }

          File out =
              new File(outputBaseDir + File.separator + generateFilename(f.getAttribute("path")))
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
