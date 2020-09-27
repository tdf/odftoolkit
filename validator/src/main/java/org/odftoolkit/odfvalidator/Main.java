/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfvalidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This application validates an OpenDocument file. To run this application from the command line
 * without Ant, try: java -jar "[path/]ODFValidator.jar".
 *
 * @author michael
 */
public class Main {

  // Versions of ODFDOM and ODF Validator are equal
  private static final String VERSION = JarManifest.getVersion();

  /** Creates a new instance of Main */
  public Main() {}

  /** @param aArgs the command line arguments */
  public static void main(String[] aArgs) {

    String aConfigFileName = null;
    String aFilterFileName = null;
    String aStrictSchemaFileName = null;
    String aSchemaFileName = null;
    String aManifestSchemaFileName = null;
    String aDSigSchemaFileName = null;
    String aMathMLSchemaFileName = null;
    String aOutputFileName = null;
    String aExcludeRegExp = null;
    boolean bPrintGenerator = false;
    boolean bPrintHelp = false;
    boolean bPrintVersion = false;
    boolean bRecursive = false;
    Logger.LogLevel nLogLevel = Logger.LogLevel.ERROR;
    OdfValidatorMode eMode = OdfValidatorMode.VALIDATE;
    List<String> aFileNames = new Vector<String>();
    OdfVersion aVersion = null;

    boolean bCommandLineValid = true;
    List<String> aArgList = Arrays.asList(aArgs);
    Iterator<String> aArgIter = aArgList.iterator();
    while (aArgIter.hasNext() && bCommandLineValid) {
      String aArg = aArgIter.next();
      if (aArg.equals("-c")) {
        eMode = OdfValidatorMode.CONFORMANCE;
      } else if (aArg.equals("-d")) {
        // ignore: bUseMathDTD = true;
      } else if (aArg.equals("-e")) {
        eMode = OdfValidatorMode.EXTENDED_CONFORMANCE;
      } else if (aArg.equals("-f")) {
        if (aArgIter.hasNext()) aFilterFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-g")) {
        bPrintGenerator = true;
      } else if (aArg.equals("-h")) {
        bPrintHelp = true;
      } else if (aArg.equals("-o")) {
        if (aArgIter.hasNext()) aOutputFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-r")) {
        bRecursive = true;
      } else if (aArg.equals("-s")) {
        eMode = OdfValidatorMode.VALIDATE_STRICT;
      } else if (aArg.equals("-v")) {
        nLogLevel = Logger.LogLevel.INFO;
      } else if (aArg.equals("-w")) {
        nLogLevel = Logger.LogLevel.WARNING;
      } else if (aArg.equals("-x")) {
        if (aArgIter.hasNext()) aExcludeRegExp = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-C")) {
        if (aArgIter.hasNext()) aConfigFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-S")) {
        if (aArgIter.hasNext()) aStrictSchemaFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-O")) {
        if (aArgIter.hasNext()) aSchemaFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-M")) {
        if (aArgIter.hasNext()) aManifestSchemaFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-D")) {
        if (aArgIter.hasNext()) aDSigSchemaFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-m")) {
        if (aArgIter.hasNext()) aMathMLSchemaFileName = aArgIter.next();
        else bCommandLineValid = false;
      } else if (aArg.equals("-V")) {
        bPrintVersion = true;
      } else if (aArg.equals("-1.0")
          || aArg.equals("-1.1")
          || aArg.equals("-1.2")
          || aArg.equals("-1.3")) {
        aVersion = OdfVersion.valueOf(aArg.substring(1), false);
      } else if (aArg.startsWith("-")) {
        System.out.print(aArg);
        System.out.println(": unknown option, use '-h' for help");
        System.exit(1);
      } else if (aArg.length() > 0) {
        aFileNames.add(aArg);
      }
    }

    // check usage
    if (bPrintHelp || bPrintVersion) {
      bCommandLineValid = true;
    } else if (bPrintGenerator) {
      bCommandLineValid = aFileNames.size() > 0;
    } else if (aConfigFileName != null) {
      bCommandLineValid = aConfigFileName.length() > 0;
    } else {
      bCommandLineValid = aFileNames.size() > 0;
    }

    // print help
    if (!bCommandLineValid || bPrintHelp) {
      printUsage();
      System.exit(bCommandLineValid ? 0 : 1);
    }
    if (bPrintVersion) {
      System.out.print("odfvalidator v");
      System.out.println(VERSION);
      System.exit(0);
    }

    try {
      // Print generator (does not require config file)
      if (bPrintGenerator) {
        MetaInformation aMetaInformation = new MetaInformation(System.out);
        Iterator<String> aIter = aFileNames.iterator();
        while (aIter.hasNext()) aMetaInformation.getInformation(aIter.next());
        System.exit(0);
      }

      // Read configuration - by default aConfig is null, so the version
      // will be read from the input file in
      // ODFPackageValidator.getVersion() and config created based on that
      Configuration aConfig = null;
      if (aConfigFileName != null) {
        File aConfigFile = new File(aConfigFileName);
        try {
          aConfig = new Configuration(aConfigFile);
        } catch (FileNotFoundException e) {
          if (aConfigFileName != null) {
            System.out.println(aConfigFile.getAbsolutePath() + ": file not found.");
            System.exit(1);
          }
        } catch (IOException e) {
          System.out.println(
              "error reading " + aConfigFile.getAbsolutePath() + ": " + e.getLocalizedMessage());
          System.exit(1);
        }

        // Permit input file override from command line, even
        // if config file is given
        if (aFileNames.size() > 0) {
          // But just one for the while...
          aConfig.setProperty(Configuration.PATH, aFileNames.get(0));
        }
      }

      // if schema files specified, only use exactly those schema files
      if (aConfig == null
          && (aStrictSchemaFileName != null
              || aSchemaFileName != null
              || aManifestSchemaFileName != null
              || aDSigSchemaFileName != null
              || aMathMLSchemaFileName != null)) {
        aConfig = new Configuration();
      }

      if (aStrictSchemaFileName != null) {
        aConfig.setProperty(Configuration.STRICT_SCHEMA, aStrictSchemaFileName);
      }
      if (aSchemaFileName != null) {
        aConfig.setProperty(Configuration.SCHEMA, aSchemaFileName);
      }
      if (aManifestSchemaFileName != null) {
        aConfig.setProperty(Configuration.MANIFEST_SCHEMA, aManifestSchemaFileName);
      }
      if (aDSigSchemaFileName != null) {
        aConfig.setProperty(Configuration.DSIG_SCHEMA, aDSigSchemaFileName);
      }
      if (aMathMLSchemaFileName != null) {
        aConfig.setProperty(Configuration.MATHML3_SCHEMA, aMathMLSchemaFileName);
      }

      PrintStream aOut = aOutputFileName != null ? new PrintStream(aOutputFileName) : System.out;
      ODFValidator aValidator = new ODFValidator(aConfig, nLogLevel, aVersion);

      if (aConfigFileName != null) {
        aValidator.validate(aOut, aConfig, eMode);
      } else {
        aValidator.validate(aOut, aFileNames, aExcludeRegExp, eMode, bRecursive, aFilterFileName);
      }
    } catch (ODFValidatorException e) {
      System.out.println(e.getMessage());
      System.out.println("Validation aborted.");
      System.exit(1);
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
      System.out.println("Validation aborted.");
      System.exit(1);
    }
  }

  private static void printUsage() {
    System.out.println("usage: odfvalidator -g <odffiles>");
    System.out.println(
        "       odfvalidator [-r] [-c|-e|-s] [-d] [-v|-w] [-f <filterfile>] [-x <regexp>] [-o outputfile] [-1.0|-1.1|-1.2|1.3] <odffiles>");
    System.out.println(
        "       odfvalidator [-r] [-c|-e|-s] [-d] [-v|-w] [-f <filterfile>] [-x <regexp>] [-o outputfile] -S <schemafile> <odffiles>");
    System.out.println(
        "       odfvalidator [-r] [-c|-e|-s] [-v|-w] -O <rngfile> -M <rngfile> -D <rngfile> -m <xsdfile> [-f <filterfile>] [-x <regexp>] [-o outputfile] <odffiles>");
    System.out.println("       odfvalidator [-c|-s] [-v|-w] [-d] [-o outputfile] -C <configfile>");
    System.out.println("       odfvalidator -h");
    System.out.println("       odfvalidator -V");
    System.out.println();
    System.out.println("-C: Validate using configuration file <configfile>");
    System.out.println("-S: Use strict ODF schema <schemafile> for validation");
    System.out.println("-O: Use ODF schema <schemafile> for validation");
    System.out.println("-M: Use ODF manifest schema <schemafile> for validation");
    System.out.println("-D: Use ODF dsig schema <schemafile> for validation");
    System.out.println("-m: Use specific MathML schema <schemafile> for validation");
    System.out.println("-V: Print version");
    System.out.println("-c: Check conformance (default for ODF 1.2 and 1.3 documents)");
    System.out.println("-e: Check extended conformance (ODF 1.2 and 1.3 documents only)");
    System.out.println(
        "-d: deprecated and ignored; Whether to use MathML DTD or MathML2 schema for validation is auto-detected");
    System.out.println("-f: Use filterfile <filterfile>");
    System.out.println("-g: Show <odffiles> generators and exit");
    System.out.println("-h: Print this help and exit");
    System.out.println("-o: Store validation errors in <outputfile>");
    System.out.println("-r: Process directory recursively");
    System.out.println("-s: Validate against strict schema (ODF 1.0/1.1 documents only)");
    System.out.println("-v: Verbose output, including generator and warnings");
    System.out.println("-w: Print warnings");
    System.out.println("-x: Exclude paths that match <regexp>");
    System.out.println();
    System.out.println(
        "If no option is provided, <odffiles> are validated using the schemas matching the detected ODF version of the files");
  }
}
