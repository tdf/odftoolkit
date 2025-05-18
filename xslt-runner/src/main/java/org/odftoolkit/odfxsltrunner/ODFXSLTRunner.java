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
package org.odftoolkit.odfxsltrunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** Class for applying style sheets to ODF documents. */
public class ODFXSLTRunner {

  public static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(ODFXSLTRunner.class.getName());
  /** Input file is a plain XML file. */
  public static final int INPUT_MODE_FILE = 0;

  /** Input file is an ODF package. The style sheet is applied to the specified sub file. */
  public static final int INPUT_MODE_PACKAGE = 1;

  /** Output file is a plain XML or text file. */
  public static final int OUTPUT_MODE_FILE = 0;

  /** Output is stdout. */
  public static final int OUTPUT_MODE_STDOUT = 1;

  /** The transformation replaces the specified path within the input file. */
  public static final int OUTPUT_MODE_REPLACE_INPUT_PACKAGE = 2;

  /**
   * The input package is copied and the result of the transformation is stored in the specified
   * path within the copied package.
   */
  public static final int OUTPUT_MODE_COPY_INPUT_PACKAGE = 3;

  /** The result of the transformation is stored in the specified path within the output package. */
  public static final int OUTPUT_MODE_TEMPLATE_PACKAGE = 4;

  private static final int FILE_COPY_BUFFER_SIZE = 4096;

  /** Create new instance of ODFXSLTRunner. */
  public ODFXSLTRunner() {}

  /**
   * Apply a style sheeet.
   *
   * @param aStyleSheet Path of the style sheet
   * @param aParams Parameters that are passed to the XSLT processor
   * @param aInputFile Path of the input file
   * @param aInputMode Input mode
   * @param aOutputFile Path of the output file
   * @param aOutputMode Output mode
   * @param aTransformerFactoryClassName XSLT transformer factory to use
   * @param aExtractFileNames A list of files or directory that shell be extracted from the package
   * @param aPathInPackage Path within the package. Default is "content.xml"
   * @param aLogger Logger object
   * @return true if an error occured.
   */
  public boolean runXSLT(
      String aStyleSheet,
      List<XSLTParameter> aParams,
      String aInputFile,
      int aInputMode,
      String aOutputFile,
      int aOutputMode,
      String aPathInPackage,
      String aTransformerFactoryClassName,
      List<String> aExtractFileNames,
      Logger aLogger) {
    return runXSLT(
        new File(aStyleSheet),
        aParams,
        new File(aInputFile),
        aInputMode,
        aOutputFile != null ? new File(aOutputFile) : null,
        aOutputMode,
        aPathInPackage,
        aTransformerFactoryClassName,
        aExtractFileNames,
        aLogger);
  }

  /**
   * Apply a style sheeet.
   *
   * @param aStyleSheetFile Style sheet
   * @param aParams Parameters that are passed to the XSLT processor
   * @param aInputFile Input file
   * @param aInputMode Input mode
   * @param aOutputFile Output file
   * @param aOutputMode Output mode
   * @param aPathInPackage Path within the package. Default is "content.xml"
   * @param aTransformerFactoryClassName XSLT transformer factory to use
   * @param aExtractFileNames A list of files or directory that shell be extracted from the package
   * @param aLogger Logger object
   * @return true if an error occured.
   */
  public boolean runXSLT(
      File aStyleSheetFile,
      List<XSLTParameter> aParams,
      File aInputFile,
      int aInputMode,
      File aOutputFile,
      int aOutputMode,
      String aPathInPackage,
      String aTransformerFactoryClassName,
      List<String> aExtractFileNames,
      Logger aLogger) {
    boolean bError = false;
    URIResolver aURIResolver = null;

    InputSource aInputSource = null;
    OdfPackage aInputPkg = null;
    String aMediaType = "text/xml";
    aLogger.setName(aInputFile.getAbsolutePath());
    try {
      if (INPUT_MODE_FILE == aInputMode) {
        aInputSource = new InputSource(new FileInputStream(aInputFile));
      } else {
        aInputPkg = OdfPackage.loadPackage(aInputFile);
        aLogger.setName(aInputFile.getAbsolutePath(), aPathInPackage);
        aInputSource = new InputSource(aInputPkg.getInputStream(aPathInPackage));
        aInputSource.setSystemId(aInputFile.toURI().toString() + '/' + aPathInPackage);
        OdfFileEntry aFileEntry = aInputPkg.getFileEntry(aPathInPackage);
        if (aFileEntry != null) aMediaType = aFileEntry.getMediaTypeString();
        aURIResolver =
            new ODFURIResolver(aInputPkg, aInputFile.toURI().toString(), aPathInPackage, aLogger);
      }
    } catch (Exception e) {
      aLogger.logFatalError(e.getMessage());
      return true;
    }
    String aInputName = aLogger.getName();

    Result aOutputResult = null;
    OdfPackage aOutputPkg = null;
    OutputStream aOutputStream = null;
    aLogger.setName(aOutputFile != null ? aOutputFile.getAbsolutePath() : "(none)");
    boolean bMkOutputDirs = false;
    try {
      switch (aOutputMode) {
        case OUTPUT_MODE_FILE:
          bMkOutputDirs = true;
          aOutputResult = new StreamResult(aOutputFile);
          break;
        case OUTPUT_MODE_STDOUT:
          aOutputResult = new StreamResult(System.out);
          break;
        case OUTPUT_MODE_REPLACE_INPUT_PACKAGE:
          aOutputPkg = aInputPkg;
          aOutputFile = aInputFile;
          break;
        case OUTPUT_MODE_COPY_INPUT_PACKAGE:
          bMkOutputDirs = true;
          aOutputPkg = aInputPkg;
          break;
        case OUTPUT_MODE_TEMPLATE_PACKAGE:
          aOutputPkg = OdfPackage.loadPackage(aOutputFile);
          break;
      }
      if (aOutputResult == null) {
        aLogger.setName(aOutputFile.getAbsolutePath(), aPathInPackage);
        aOutputStream = aOutputPkg.insertOutputStream(aPathInPackage, aMediaType);
        aOutputResult = new StreamResult(aOutputStream);
      }
    } catch (Exception e) {
      aLogger.logFatalError(e.getMessage());
      return true;
    }

    if (bMkOutputDirs) {
      File aOutputDir = aOutputFile.getParentFile();
      if (aOutputDir != null) aOutputDir.mkdirs();
    }

    String aOutputName = aLogger.getName();

    aLogger.setName(aStyleSheetFile.getAbsolutePath());
    aLogger.logInfo("Applying stylesheet to '" + aInputName + "'");
    try {
      bError =
          runXSLT(
              aStyleSheetFile,
              aParams,
              aInputSource,
              aOutputResult,
              aTransformerFactoryClassName,
              aURIResolver,
              aLogger);
    } catch (ParserConfigurationException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    if (bError) return true;

    aLogger.setName(aOutputFile != null ? aOutputFile.getAbsolutePath() : "(none)");
    try {
      aLogger.logInfo("Storing transformation result to '" + aOutputName + "'");
      if (!bError && aOutputStream != null) aOutputStream.close();
      if (!bError && aOutputPkg != null) aOutputPkg.save(aOutputFile);
      if (aOutputMode == OUTPUT_MODE_FILE && aExtractFileNames != null && aInputPkg != null) {
        File aTargetDir = aOutputFile.getParentFile();
        extractFiles(aInputPkg, aTargetDir, aExtractFileNames, aLogger);
      }
    } catch (IOException | SAXException e) {
      aLogger.logFatalError(e.getMessage());
      return true;
    }

    return false;
  }

  private boolean runXSLT(
      File aStyleSheetFile,
      List<XSLTParameter> aParams,
      InputSource aInputInputSource,
      Result aOutputTarget,
      String aTransformerFactoryClassName,
      URIResolver aURIResolver,
      Logger aLogger)
      throws ParserConfigurationException {
    InputStream aStyleSheetInputStream = null;
    try {
      aStyleSheetInputStream = new FileInputStream(aStyleSheetFile);
    } catch (FileNotFoundException e) {
      aLogger.logFatalError(e.getMessage());
      return true;
    }

    InputSource aStyleSheetInputSource = new InputSource(aStyleSheetInputStream);
    aStyleSheetInputSource.setSystemId(aStyleSheetFile.getAbsolutePath());

    XMLReader aStyleSheetXMLReader;
    XMLReader aInputXMLReader;
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    try {
      aStyleSheetXMLReader = factory.newSAXParser().getXMLReader();
      aInputXMLReader = factory.newSAXParser().getXMLReader();
    } catch (SAXException e) {
      aLogger.logFatalError(e.getMessage());
      return true;
    }

    aStyleSheetXMLReader.setErrorHandler(new SAXErrorHandler(aLogger));
    aInputXMLReader.setErrorHandler(new SAXErrorHandler(aLogger));
    aInputXMLReader.setEntityResolver(new ODFEntityResolver(aLogger));

    Source aStyleSheetSource = new SAXSource(aStyleSheetXMLReader, aStyleSheetInputSource);
    Source aInputSource = new SAXSource(aInputXMLReader, aInputInputSource);

    if (aTransformerFactoryClassName != null)
      aLogger.logInfo("Requesting transformer factory class: " + aTransformerFactoryClassName);
    TransformerFactory aFactory = null;
    if (aTransformerFactoryClassName == null) {
      aFactory = TransformerFactory.newInstance();
    } else {
      try {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = ClassLoader.getSystemClassLoader();
        Class<?> classInstance = cl.loadClass(aTransformerFactoryClassName);
        aFactory = (TransformerFactory) classInstance.getDeclaredConstructor().newInstance();
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ce) {
        aLogger.logFatalError(ce.getMessage());
        return true;
      } catch (NoSuchMethodException
          | SecurityException
          | IllegalArgumentException
          | InvocationTargetException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    ErrorListener aErrorListener = new TransformerErrorListener(aLogger);
    aLogger.logInfo("Using transformer factory class: " + aFactory.getClass().getName());
    aFactory.setErrorListener(aErrorListener);

    try {
      Transformer aTransformer = aFactory.newTransformer(aStyleSheetSource);

      if (aParams != null) {
        Iterator<XSLTParameter> aIter = aParams.iterator();
        while (aIter.hasNext()) {
          XSLTParameter aParam = aIter.next();
          aTransformer.setParameter(aParam.getName(), aParam.getValue());
          aLogger.logInfo("Using parameter: " + aParam.getName() + "=" + aParam.getValue());
        }
      }
      aTransformer.setErrorListener(aErrorListener);
      if (aURIResolver != null) aTransformer.setURIResolver(aURIResolver);
      aTransformer.transform(aInputSource, aOutputTarget);
    } catch (TransformerException e) {
      aLogger.logFatalError(e);
      return true;
    }

    return false;
  }

  private boolean extractFiles(
      OdfPackage aInputPkg, File aTargetDir, List<String> aExtractFileNames, Logger aLogger) {
    Set<String> aInputPkgEntries = aInputPkg.getFilePaths();

    Iterator<String> aInputPkgEntryIter = aInputPkgEntries.iterator();
    while (aInputPkgEntryIter.hasNext()) {
      String aInputFileName = aInputPkgEntryIter.next();

      Iterator<String> aExtractFileNameIter = aExtractFileNames.iterator();
      while (aExtractFileNameIter.hasNext()) {
        String aExtractFileName = aExtractFileNameIter.next();
        if (!aInputFileName.endsWith("/")
            && (aInputFileName.equals(aExtractFileName)
                || (aExtractFileName.endsWith("/")
                    ? aInputFileName.startsWith(aExtractFileName)
                    : aInputFileName.startsWith(aExtractFileName + "/")))) {
          try {
            File aTargetFile = new File(aTargetDir, aInputFileName);
            File aTargetFileDir = aTargetFile.getParentFile();
            if (aTargetFileDir != null) aTargetFileDir.mkdirs();

            aLogger.logInfo(
                "Extracting file " + aInputFileName + " to " + aTargetFile.getAbsolutePath());
            InputStream aInputStream = aInputPkg.getInputStream(aInputFileName);
            OutputStream aTargetStream = new FileOutputStream(aTargetFile);
            byte[] aBuffer = new byte[FILE_COPY_BUFFER_SIZE];
            int n = 0;
            while ((n = aInputStream.read(aBuffer, 0, FILE_COPY_BUFFER_SIZE)) > -1) {
              aTargetStream.write(aBuffer, 0, n);
            }
            aTargetStream.close();
            aInputStream.close();
          } catch (java.lang.Exception e) {
            aLogger.logError(e.getMessage());
          }
          break;
        }
      }
    }
    return false;
  }
}
