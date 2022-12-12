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
package schema2template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.xml.sax.SAXException;
import schema2template.grammar.OdfModel;
import schema2template.grammar.XMLModel;
import schema2template.template.FileCreationListEntry;
import schema2template.template.FileCreationListHandler;
import schema2template.template.GrammarAdditionsFileHandler;
import schema2template.template.SourceCodeModel;

/**
 * Three ODF examples in one: 1) Create an ODF Reference in HTMLl 2) Create Source Code 3) Create
 * simple ODF Python source
 */
public class SchemaToTemplate {

  private static final Logger LOG = Logger.getLogger(SchemaToTemplate.class.getName());
  public static final Boolean DEBUG = Boolean.FALSE;

  /** This optional Velocity Macro file is loaded from the template directory */
  private static final String VELOCITY_MACRO_FILE = "velocity-macros.vm";

  private SchemaToTemplate() {}

  /**
   * Iniitial function to trigger a list of generations like all Java classes with change history
   * from prior versions!
   *
   * @param generations each a list of generations of files based on a transformation from XML
   *     grammar data into Velocity templates!
   */
  public static void run(List<GenerationParameters> generations)
      throws ParserConfigurationException, IOException, SAXException {
    //    System.err.println(
    //        "Schema2template code generations triggered " + generations.size() + " times!");

    // sort the generations based on their version to start from the latest and add newer
    // incremental
    generations.sort(Comparator.comparing(GenerationParameters::getGrammarVersion));
    Map<String, List<XMLModel>> modelHistories = new HashMap<>();

    for (GenerationParameters generation : generations) {

      //      System.out.println("\n");
      String grammarVersion = generation.getGrammarVersion();
      //      System.out.println("GrammarVersion: " + grammarVersion);

      String grammarID = generation.getGrammarID();
      //      System.out.println("GrammarID: " + grammarID);

      String grammarFilePath = generation.getGrammarPath();
      //      System.out.println("GrammarPath: " + grammarFilePath);

      String grammarAdditionsFilePath = generation.getGrammarAdditionsPath();
      //      System.out.println("grammarAddOnFilePath: " + grammarAdditionsFilePath);

      String mainTemplateFilePath = generation.getMainTemplatePath();
      String mainTemplateFileName = Paths.get(mainTemplateFilePath).getFileName().toString();
      String templateBaseDir =
          mainTemplateFilePath.substring(0, mainTemplateFilePath.lastIndexOf(mainTemplateFileName));
      //      System.out.println("Grammar2Templates: " + mainTemplateFilePath);

      String targetDirPath = generation.getTargetDir();
      targetDirPath = targetDirPath + File.separator + grammarID + "-" + grammarVersion;
      new File(targetDirPath).mkdirs();
      //      System.out.println("TargetDirPath: " + targetDirPath);

      XMLModel currentModel = new XMLModel(new File(grammarFilePath), grammarVersion, grammarID);

      currentModel.getAttributes().withoutMultiples();

      List<XMLModel> modelHistory;
      if (modelHistories.containsKey(grammarID)) {
        modelHistory = modelHistories.get(grammarID);
        // sort the modelHistory from the latest to the oldest, to quickly identify the first change
        // from the current version in the past!
        modelHistory =
            modelHistory.stream()
                .sorted(Comparator.comparing(XMLModel::getGrammarVersion).reversed())
                .collect(Collectors.toList());
      } else {
        modelHistory = null;
      }
      startGeneration(
          templateBaseDir,
          mainTemplateFileName,
          targetDirPath,
          "file-creation-list_" + grammarID + "-" + grammarVersion + ".xml",
          initVelocityContext(currentModel, modelHistory, grammarAdditionsFilePath));

      if (modelHistory == null) {
        modelHistory = new ArrayList<>();
      }
      // after current model creation add the modelhistory VersionID
      modelHistory.add(currentModel);
      modelHistories.put(grammarID, modelHistory);
    }
  }

  private static VelocityContext initVelocityContext(
      XMLModel xmlModel, List<XMLModel> xmlModelHistory, String grammarAdditionsFilePath)
      throws ParserConfigurationException, IOException, SAXException {
    LOG.info("Starting initilization of Velocity context..");
    VelocityContext context = new VelocityContext();

    if (grammarAdditionsFilePath != null && !grammarAdditionsFilePath.isBlank()) {
      // Read grammar-additions.xml
      // Manual added Java specific info - Base class for inheritance of shared attributes from
      // element
      Map<String, String> elementToBaseNameMap = new HashMap<>();
      // Manual added Java specific info - extension class for inheritance added base or generated
      // node class
      Map<String, String> elementSuperClassNameMap = new HashMap<>();

      /**
       * Marks the element that start a (possible larger) semantic component (e.g.
       * &lt;table:table&gt; for table
       */
      Map<String, String> componentRootElementNames = new HashMap<>();
      Set<String> repetitionAttributeNames = new HashSet<>();

      // Manual added ODF specific info - style family mapping
      Map<String, List<String>> elementNameToFamilyMap = new HashMap<>();
      // 2DO - still existent? -- Manual added Java specific info - mapping ODF datatype to Java
      // datatype  -> {odfValueType, javaConversionClassName}
      Map<String, String[]> datatypeValueAndConversionMap = new HashMap<>();
      Map<String, Map<String, String>> attributeDefaultMap = new HashMap<>();
      GrammarAdditionsFileHandler.readGrammarAdditionsFile(
          new File(grammarAdditionsFilePath),
          elementToBaseNameMap,
          elementSuperClassNameMap,
          componentRootElementNames,
          repetitionAttributeNames,
          attributeDefaultMap,
          elementNameToFamilyMap,
          datatypeValueAndConversionMap);
      // odfConstants
      OdfModel odfModel =
          new OdfModel(
              elementNameToFamilyMap,
              componentRootElementNames,
              repetitionAttributeNames,
              attributeDefaultMap,
              xmlModel);
      context.put("odfModel", odfModel);

      // Needed for the base classes - common attributes are being moved into the base classes
      SourceCodeModel sourceCodeModel =
          new SourceCodeModel(
              xmlModel,
              elementToBaseNameMap,
              elementSuperClassNameMap,
              datatypeValueAndConversionMap);
      context.put("codeModel", sourceCodeModel);
    }
    context.put("xmlModel", xmlModel);
    context.put("xmlModelHistory", xmlModelHistory);
    LOG.info("Finished initialization..");
    return context;
  }

  private static void startGeneration(
      String templateBaseDir,
      String templateFileName,
      String targetDirPath,
      String targetFileName,
      VelocityContext context)
      throws ParserConfigurationException, IOException, SAXException {

    // initializing template engine instance (ie. Velocity engine)
    LOG.info("Starting code generation:");
    VelocityEngine ve = new VelocityEngine();
    ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
    ve.setProperty(
        RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
        Paths.get(templateBaseDir).normalize().toString());
    // Using backward compatible whitespace gobbling/eating
    // http://velocity.apache.org/engine/2.3/developer-guide.html#backward-compatible-space-gobbling
    ve.setProperty(RuntimeConstants.SPACE_GOBBLING, "bc");
    // https://velocity.apache.org/engine/2.0/user-guide.html#strict-reference-mode (works in 2.3,
    // but is no longer documented)
    ve.setProperty(RuntimeConstants.RUNTIME_REFERENCES_STRICT, "true");
    if (VELOCITY_MACRO_FILE != null
        && !VELOCITY_MACRO_FILE.isEmpty()
        && Paths.get(templateBaseDir + File.separator + VELOCITY_MACRO_FILE).toFile().exists()) {
      ve.setProperty(RuntimeConstants.VM_LIBRARY, VELOCITY_MACRO_FILE);
    }
    ve.init();
    generateFileCreationList(ve, templateFileName, targetDirPath, targetFileName, context);
    LOG.info("file-creation-list.xml has been created!");

    // Process output-files.xml, create output files
    LOG.fine("Processing output files... ");
    processFileCreationList(ve, targetDirPath, targetFileName, context);
    LOG.fine("DONE.\n");
  }

  private static void generateFileCreationList(
      VelocityEngine ve,
      String templateFileName,
      String targetDirPath,
      String targetFileName,
      VelocityContext context)
      throws IOException {
    File fileCreationList = new File(targetDirPath + File.separator + targetFileName);
    ensureParentFolders(fileCreationList);
    try (FileWriter listout = new FileWriter(fileCreationList)) {
      String encoding = "utf-8";
      ve.mergeTemplate(templateFileName, encoding, context, listout);
    }
  }

  private static void processFileCreationList(
      VelocityEngine ve, String targetDirPath, String targetFileName, VelocityContext context)
      throws ParserConfigurationException, IOException, SAXException {
    File outputFileList = new File(targetDirPath + File.separator + targetFileName);
    List<FileCreationListEntry> fl = FileCreationListHandler.readFileListFile(outputFileList);

    for (FileCreationListEntry f : fl) {
      LOG.log(
          Level.INFO,
          "Processing line {0}: \n\tGenerating file:\n\t\t{1}\n\t\t{2}",
          new Object[] {
            f.getLineNumber(),
            targetDirPath + File.separator,
            // receives the path attribute from the Velocity template
            Paths.get(f.getAttribute("path")).normalize()
          });

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
          new File(targetDirPath + File.separator + Paths.get(f.getAttribute("path")).normalize())
              .getCanonicalFile();
      ensureParentFolders(out);
      try (FileWriter fileout = new FileWriter(out)) {
        String encoding = "utf-8";
        ve.mergeTemplate(f.getAttribute("template"), encoding, context, fileout);
      }
    }
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
