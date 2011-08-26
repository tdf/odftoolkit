/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
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
 ************************************************************************/
package schema2template.example.odf;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import schema2template.OutputFileListEntry;
import schema2template.OutputFileListHandler;
import schema2template.model.XMLModel;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Three ODF examples in one:
 *  1) Create an ODF Reference in HTMLl
 *  2) Create Source Code
 *  3) Create simple ODF Python source
 *
 */
public class OdfHelper {

	private static final Logger LOG = Logger.getLogger(OdfHelper.class.getName());
	public static final Boolean DEBUG = Boolean.FALSE;

	/** Expresses the amount of elements in ODF 1.1. There are some issues in the schema that have to be fixed before the full number can be returned by MSV:
		Reference table-table-template is never used, therefore several elements are not taking into account::
		"table:body"
		"table:even-columns"
		"table:even-rows"
		"table:first-column"
		"table:first-row"
		"table:last-column"
		"table:last-row"
		"table:odd-columns"
		"table:odd-rows"
		"table:table-template"
	 NOTE: Ignoring the '*' there can be 525 elements parsed, but with fixed schema it should be 535. */
	public static final int ODF11_ELEMENT_NUMBER = 525; //ToDo: 535 - by search/Replace using RNGSchema and tools, prior exchange <name> to element or attribute declaration
	public static final int ODF12_ELEMENT_NUMBER = 598;
	
	/** Expresses the amount of attributes in ODF 1.1. There are some issues in the schema that have to be fixed before the full number can be returned by MSV:
		Following references are never used, therefore its attribute is not taking into account::
			draw-glue-points-attlist	with "draw:escape-direction"
			office-process-content		with "office:process-content" (DEPRECATED in ODF1.2 only on foreign elements)

		Following attributes are member of the not referenced element "table:table-template":
			"text:first-row-end-column"
			"text:first-row-start-column"
			"text:last-row-end-column"
			"text:last-row-start-column"
			"text:paragraph-style-name"

	 NOTE: Ignoring the '*' there can be 1162 elements parsed, but with fixed schema it should be 1169. */
	public static final int ODF11_ATTRIBUTE_NUMBER = 1162; //ToDo: 1169 - by search/Replace using RNGSchema and tools, prior exchange <name> to element or attribute declaration
	public static final int ODF12_ATTRIBUTE_NUMBER = 1300; //in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie. @office:process-content)
	public static String odfResourceDir;
	public static String outputRoot;
	public static final String INPUT_ROOT = "target" + File.separator + "classes" + File.separator
				+ "examples" + File.separator + "odf";
	public static final String TEST_INPUT_ROOT = "target" + File.separator + "test-classes" + File.separator
				+ "examples" + File.separator + "odf";

	public static final String ODF10_RNG_FILE_NAME = "OpenDocument-strict-schema-v1.0-os.rng";
	public static final String ODF11_RNG_FILE_NAME = "OpenDocument-strict-schema-v1.1.rng";
	public static final String ODF12_RNG_FILE_NAME = "OpenDocument-v1.2-csprd03-schema.rng";
	public  static String odf12RngFile;
	public  static String odf11RngFile;
	public  static String odf10RngFile;
	private static String mConfigFile;
	private static final String OUTPUT_FILES_TEMPLATE = "output-files.vm";
	private static final String OUTPUT_FILES = "target" + File.separator + "output-files.xml";
	private static XMLModel mOdf12SchemaModel;
	private static XMLModel mOdf11SchemaModel;
	private static OdfModel mOdfModel;
	private static SourceCodeModel mJavaModel;

	public OdfHelper(String resourceRoot, String targetRoot, String odf12SchemaFile, String odf11SchemaFile, String configFile) {
		odfResourceDir = resourceRoot;
		outputRoot = targetRoot;
		odf12RngFile = odf12SchemaFile;
		odf11RngFile = odf11SchemaFile;
		mConfigFile = configFile;
	}

	static {
		odfResourceDir = INPUT_ROOT;
		odf12RngFile = INPUT_ROOT + File.separator + ODF12_RNG_FILE_NAME;
		odf11RngFile = INPUT_ROOT + File.separator + ODF11_RNG_FILE_NAME;
		odf10RngFile = INPUT_ROOT + File.separator + ODF10_RNG_FILE_NAME;
		mConfigFile = INPUT_ROOT + File.separator + "config.xml";
		outputRoot = "target";
	}

	public static void main(String[] args) throws Exception {

		// calling MSV to parse the ODF 1.2 RelaxNG, returning a tree
		Expression odf12Root = loadSchema(new File(odf12RngFile));
		// calling MSV to parse the ODF 1.1 RelaxNG, returning a tree
		Expression odf11Root = loadSchema(new File(odf11RngFile));

		// Read config.xml 2DO WHAT IS ODFDOM GENERATOR CONFIG FILE
		// Manual added Java specific info - Base class for inheritance
		Map<String, String> elementToBaseNameMap = new HashMap<String, String>();
		// Manual added ODF specific info - style family mapping
		Map<String, List<String>> elementStyleFamiliesMap = new HashMap<String, List<String>>();
		// 2DO - still existent? -- Manual added Java specific info - mapping ODF datatype to Java datatype  -> {odfValueType, javaConversionClassName}
		Map<String, String[]> datatypeValueAndConversionMap = new HashMap<String, String[]>();
		Map<String, OdfModel.AttributeDefaults> attributeDefaultMap = new HashMap<String, OdfModel.AttributeDefaults>();
		OdfConfigFileHandler.readConfigFile(new File(mConfigFile), elementToBaseNameMap, attributeDefaultMap, elementStyleFamiliesMap, datatypeValueAndConversionMap);

		mOdf12SchemaModel = new XMLModel(odf12Root);
		mOdf11SchemaModel = new XMLModel(odf11Root);
		mOdfModel = new OdfModel(elementStyleFamiliesMap, attributeDefaultMap);
		// Needed for the base classes - common attributes are being moved into the base classes
		mJavaModel = new SourceCodeModel(mOdf12SchemaModel, mOdfModel, elementToBaseNameMap, datatypeValueAndConversionMap);

		fillTemplates(odfResourceDir + File.separator + "odf-reference", odf12Root);
//		fillTemplates(odfResourceDir + File.separator+ "odfdom-java", odf12Root);
//		fillTemplates(odfResourceDir + File.separator + "odfdom-python", odf12Root);
	}

	private static void fillTemplates(String sourceDir, Expression root) throws Exception {
		// intialising template engine (ie. Velocity)
		Properties props = new Properties();
		props.setProperty("file.resource.loader.path", sourceDir);
		VelocityEngine ve = new VelocityEngine(props);
		ve.init();

		// Create output-files.xml
		createOutputFileList(ve);
		LOG.info("DONE.\n");

		// Process output-files.xml, create output files
		LOG.fine("Processing output files... ");
		processFileList(ve, root);
		LOG.fine("DONE.\n");
	}

	/**
	 * Load and parse the ODF 1.0 Schema.
	 *
	 * @return MSV Expression Tree of ODF 1.0 RelaxNG schema (more specific: The tree's MSV root expression)
	 * @throws Exception
	 */
	public static Expression loadSchemaODF10() throws Exception {
		return loadSchema(new File(odf10RngFile));
	}

	/**
	 * Load and parse the ODF 1.1 Schema.
	 *
	 * @return MSV Expression Tree of ODF 1.1 RelaxNG schema (more specific: The tree's MSV root expression)
	 * @throws Exception
	 */
	public static Expression loadSchemaODF11() throws Exception {
		return loadSchema(new File(odf11RngFile));
	}

	/**
	 * Load and parse the ODF 1.2 Schema.
	 *
	 * @return MSV Expression Tree of ODF 1.2 RelaxNG schema  (more specific: The tree's MSV root expression)
	 * @throws Exception
	 */
	public static Expression loadSchemaODF12() throws Exception {
		return loadSchema(new File(odf12RngFile));
	}

	/**
	 * Load and parse a Schema from File.
	 *
	 * @param rngFile
	 * @return MSV Expression Tree (more specific: The tree's MSV root expression)
	 * @throws Exception
	 */
	public static Expression loadSchema(File rngFile) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		// Parsing the Schema with MSV
		Expression root = RELAXNGReader.parse(
				rngFile.getAbsolutePath(),
				factory,
				new com.sun.msv.reader.util.IgnoreController()).getTopLevel();


		if (root == null) {
			throw new Exception("Schema could not be parsed.");
		}
		return root;
	}

	private static VelocityContext getContext(String contextStr, String param) {
		VelocityContext context = new VelocityContext();
		context.put("model", mOdf12SchemaModel);
		context.put("oldmodel", mOdf11SchemaModel);
		context.put("odfmodel", mOdfModel);
		context.put("javamodel", mJavaModel);
		context.put("context", contextStr);
		context.put("param", param);
		return context;
	}

	private static void createOutputFileList(VelocityEngine ve) throws Exception {
		VelocityContext context = getContext(null, null);
		File parentPatch = new File(OUTPUT_FILES).getParentFile();
		if (!parentPatch.exists()) {
			parentPatch.mkdirs();
		}
		FileWriter listout = new FileWriter(new File(OUTPUT_FILES));
		String encoding = "utf-8";
		ve.mergeTemplate(OUTPUT_FILES_TEMPLATE, encoding, context, listout);
		listout.close();
	}

	private static String generateFilename(String rawName) {
		File retfile = new File(".");
		StringTokenizer toktok = new StringTokenizer(rawName.replaceAll(":", "_"), "/");
		while (toktok.hasMoreTokens()) {
			retfile = new File(retfile, toktok.nextToken());
		}
		return retfile.getPath();
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

	public static void processFileList(VelocityEngine ve, Expression root) throws Exception {
		File outputFiles = new File(OUTPUT_FILES);
		List<OutputFileListEntry> fl = OutputFileListHandler.readFileListFile(outputFiles);

		for (OutputFileListEntry f : fl) {
			switch (f.getType()) {
				case PATH:
					break;
				case FILE:
					LOG.log(Level.INFO, "Processing line{0}: Generating file {1}\n", new Object[]{f.getLineNumber(), generateFilename(f.getAttribute("path"))});
					String odfContextStr = f.getAttribute("context");
					String param = f.getAttribute("param");
					VelocityContext context = getContext(odfContextStr, param);
					if (context == null) {
						throw new RuntimeException("Error in output-files.xml, line " + f.getLineNumber() + ": no or invalid odf-scope");
					}

					File out = new File(outputRoot + generateFilename(f.getAttribute("path"))).getCanonicalFile();
					ensureParentFolders(out);
					FileWriter fileout = new FileWriter(out);
					String encoding = "utf-8";

					ve.mergeTemplate(f.getAttribute("template"), encoding, context, fileout);
					fileout.close();

					break;
			}
		}
	}
}
