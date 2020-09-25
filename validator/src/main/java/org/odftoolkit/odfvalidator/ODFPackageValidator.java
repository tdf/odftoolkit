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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Validator;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.EncryptionDataElement;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/** Validator for Files */
abstract class ODFPackageValidator {

  static final String DOCUMENT_SETTINGS = "document-settings";
  static final String DOCUMENT_STYLES = "document-styles";
  static final String DOCUMENT_CONTENT = "document-content";
  protected Logger.LogLevel m_nLogLevel;
  protected OdfValidatorMode m_eMode = OdfValidatorMode.CONFORMANCE;
  protected SAXParseExceptionFilter m_aFilter = null;
  protected ODFValidatorProvider m_aValidatorProvider = null;
  protected ODFValidationResult m_aResult = null;
  protected OdfVersion m_aConfigVersion = null;
  private SAXParserFactory m_aSAXParserFactory = null;
  protected OdfVersion mOdfPackageVersion = null;

  protected ODFPackageValidator(
      Logger.LogLevel nLogLevel,
      OdfValidatorMode eMode,
      OdfVersion aVersion,
      SAXParseExceptionFilter aFilter,
      ODFValidatorProvider aValidatorProvider) {
    m_nLogLevel = nLogLevel;
    m_eMode = eMode;
    m_aFilter = aFilter;
    m_aValidatorProvider = aValidatorProvider;
    m_aConfigVersion = aVersion;
    m_aResult = new ODFValidationResult(aVersion, eMode);
  }

  protected abstract String getLoggerName();

  protected abstract String getDocumentPath();

  protected abstract OdfPackage getPackage(Logger aLogger);

  protected abstract String getStreamName(String aEntry);

  protected boolean validate(PrintStream aOut) throws ODFValidatorException {
    Logger aLogger = new Logger(getLoggerName(), getDocumentPath(), aOut, m_nLogLevel);
    return _validate(aLogger);
  }

  protected boolean validate(Logger aParentLogger) throws ODFValidatorException {
    Logger aLogger = new Logger(getDocumentPath(), aParentLogger);
    return _validate(aLogger);
  }

  OdfVersion getOdfPackageVersion() {
    return mOdfPackageVersion;
  }

  private boolean _validate(Logger aLogger) throws ODFValidatorException {
    boolean bHasErrors = false;

    OdfPackage aPkg = getPackage(aLogger);
    if (aPkg == null) {
      return true;
    }

    try {
      String aDocVersion = getVersion(aLogger);
      if (aDocVersion != null) {
        aLogger.logInfo("ODF version of root document: " + aDocVersion, false);
        mOdfPackageVersion = OdfVersion.valueOf(aDocVersion, true);
      }
      OdfVersion aVersion =
          m_aConfigVersion == null ? OdfVersion.valueOf(aDocVersion, true) : m_aConfigVersion;

      bHasErrors |= validatePre(aLogger, aVersion);
      aLogger.logInfo("Media Type: " + m_aResult.getMediaType(), false);

      bHasErrors |=
          validateMeta(
              aLogger, getStreamName(OdfDocument.OdfXMLFile.META.getFileName()), aVersion, true);
      bHasErrors |=
          validateEntry(
              aLogger,
              getStreamName(OdfDocument.OdfXMLFile.SETTINGS.getFileName()),
              DOCUMENT_SETTINGS,
              aVersion);
      bHasErrors |=
          validateEntry(
              aLogger,
              getStreamName(OdfDocument.OdfXMLFile.STYLES.getFileName()),
              DOCUMENT_STYLES,
              aVersion);
      if (m_aResult.getMediaType().equals(ODFMediaTypes.FORMULA_MEDIA_TYPE)) {
        bHasErrors |=
            validateMathML(
                aLogger, getStreamName(OdfDocument.OdfXMLFile.CONTENT.getFileName()), aVersion);
      } else {
        bHasErrors |=
            validateEntry(
                aLogger,
                getStreamName(OdfDocument.OdfXMLFile.CONTENT.getFileName()),
                DOCUMENT_CONTENT,
                aVersion);
      }
      bHasErrors |= validatePost(aLogger, aVersion);
    } catch (ZipException e) {
      aLogger.logFatalError(e.getMessage());
    } catch (IOException e) {
      aLogger.logFatalError(e.getMessage());
    }

    logSummary(bHasErrors, aLogger);

    return bHasErrors || aLogger.hasError();
  }

  protected boolean validatePre(Logger aLogger, OdfVersion aVersion)
      throws ODFValidatorException, IOException {
    return false;
  }

  protected boolean validatePost(Logger aLogger, OdfVersion aVersion)
      throws ODFValidatorException, IOException {
    return false;
  }

  protected void logSummary(boolean bHasErrors, Logger aLogger) {}

  protected boolean validateEntry(
      Logger aParentLogger, String aEntryName, String aLocalElementName, OdfVersion aVersion)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    Logger aLogger = new Logger(aEntryName, aParentLogger);
    XMLFilter aFilter = new ContentFilter(aLogger, aLocalElementName);
    if ((m_eMode == OdfValidatorMode.CONFORMANCE && aVersion.compareTo(OdfVersion.V1_1) <= 0)
        || m_eMode == OdfValidatorMode.EXTENDED_CONFORMANCE) {
      XMLFilter aAlienFilter = new ForeignContentFilter(aLogger, aVersion, m_aResult);
      aAlienFilter.setParent(aFilter);
      aFilter = aAlienFilter;
    }
    Validator aValidator = null;
    if (m_eMode == OdfValidatorMode.VALIDATE_STRICT) {
      aValidator =
          m_aValidatorProvider.getStrictValidator(aParentLogger.getOutputStream(), aVersion);
    } else {
      aValidator = m_aValidatorProvider.getValidator(aParentLogger.getOutputStream(), aVersion);
    }
    //		Validator aValidator = m_eMode == OdfValidatorMode.VALIDATE_STRICT ?
    // m_aValidatorProvider.getStrictValidator(aParentLogger.getOutputStream(), aVersion)
    //				: m_aValidatorProvider.getValidator(aParentLogger.getOutputStream(), aVersion);
    return validateEntry(aFilter, aValidator, aLogger, aEntryName);
  }

  private boolean validateMeta(
      Logger aParentLogger, String aEntryName, OdfVersion aVersion, boolean bIsRoot)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    Logger aLogger = new Logger(aEntryName, aParentLogger);
    XMLFilter aFilter = new MetaFilter(aLogger, m_aResult);
    if ((m_eMode == OdfValidatorMode.CONFORMANCE && aVersion.compareTo(OdfVersion.V1_1) <= 0)
        || m_eMode == OdfValidatorMode.EXTENDED_CONFORMANCE) {
      XMLFilter aAlienFilter = new ForeignContentFilter(aLogger, aVersion, m_aResult);
      aAlienFilter.setParent(aFilter);
      aFilter = aAlienFilter;
    }

    Validator aValidator = null;
    if (m_eMode == OdfValidatorMode.VALIDATE_STRICT) {
      aValidator =
          m_aValidatorProvider.getStrictValidator(aParentLogger.getOutputStream(), aVersion);
    } else {
      aValidator = m_aValidatorProvider.getValidator(aParentLogger.getOutputStream(), aVersion);
    }
    return validateEntry(aFilter, aValidator, aLogger, aEntryName);
  }

  private boolean validateMathML(Logger aParentLogger, String aEntryName, OdfVersion aVersion)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    Logger aLogger = new Logger(aEntryName, aParentLogger);
    String aMathMLDTDSystemId = m_aValidatorProvider.getMathMLDTDSystemId(aVersion);
    boolean haveDoctype = false;

    // auto-detect whether MathML 1 DTD should be used
    try {
      OdfPackage aPkg = getPackage(aLogger);
      InputStream stream = aPkg.getInputStream(aEntryName, true);
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          break;
        }
        if (line.startsWith("<?xml")) {
          continue;
        }
        if (line.contains("<!DOCTYPE")) {
          haveDoctype = true;
          break;
        }
        if (!line.trim().isEmpty()) {
          break; // ignore whitespace lines
        }
      }
    } catch (Exception e) {
      throw new ODFValidatorException(e);
    }

    if (aMathMLDTDSystemId != null && haveDoctype) {
      // validate using DTD
      return parseEntry(
          new MathML101Filter(aMathMLDTDSystemId, aLogger), aLogger, aEntryName, true);
    } else {
      Validator aMathMLValidator =
          m_aValidatorProvider.getMathMLValidator(aParentLogger.getOutputStream(), null);
      if (aMathMLValidator == null) {
        aLogger.logInfo("MathML schema is not available. Validation has been skipped.", false);
        return false;
      }
      return validateEntry(new MathML20Filter(aLogger), aMathMLValidator, aLogger, aEntryName);
    }
  }

  protected boolean validateDSig(Logger aParentLogger, String aEntryName, OdfVersion aVersion)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    Validator aValidator =
        m_aValidatorProvider.getDSigValidator(aParentLogger.getOutputStream(), aVersion);
    Logger aLogger = new Logger(aEntryName, aParentLogger);
    if (aValidator == null) {
      aLogger.logWarning(
          "Signature not validated because there is no Signature Validator configured for the selected Configuration");
      return false;
    }

    return validateEntry(new DSigFilter(aLogger), aValidator, aLogger, aEntryName);
  }

  protected boolean validateEntry(
      XMLFilter aFilter, Validator aValidator, Logger aLogger, String aEntryName)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    OdfPackage aPkg = getPackage(aLogger);

    if (!aEntryName.equals(OdfPackage.OdfFile.MANIFEST.getPath())
        && isEncrypted(aEntryName, aLogger)) {
      return false;
    }

    InputStream aInStream = null;
    try {
      aInStream = aPkg.getInputStream(aEntryName, true);
      aLogger.setInputStream(aPkg.getInputStream(aEntryName, true));
    } catch (Exception e) {
      throw new ODFValidatorException(e);
    }

    if (aValidator == null) {

      aLogger.logWarning("no Validator configured in selected Configuration for this file type");
      return false;
    }

    return aInStream != null ? validate(aInStream, aFilter, aValidator, aLogger) : false;
  }

  private boolean validate(
      InputStream aInStream,
      XMLFilter aFilter,
      javax.xml.validation.Validator aValidator,
      Logger aLogger)
      throws ODFValidatorException {
    SAXParser aParser = getSAXParser(false);
    SchemaErrorHandler aErrorHandler = new SchemaErrorHandler(aLogger, m_aFilter);

    try {
      XMLReader aReader;
      if (aFilter != null) {
        XMLReader aParent = aFilter.getParent();
        if (aParent != null) {
          ((XMLFilter) aParent).setParent(aParser.getXMLReader());
        } else {
          aFilter.setParent(aParser.getXMLReader());
        }
        aReader = aFilter;
      } else {
        aReader = aParser.getXMLReader();
      }

      if (m_aFilter != null) {
        m_aFilter.startSubFile();
      }
      aValidator.setErrorHandler(aErrorHandler);
      try {
        aValidator.validate(new SAXSource(aReader, new InputSource(aInStream)));
      } catch (RuntimeException e) {
        aLogger.logFatalError(e.getMessage());
        m_aValidatorProvider.resetValidatorProvider();
      }
    } catch (org.xml.sax.SAXParseException e) {
      aErrorHandler.fatalErrorNoException(e);
    } catch (org.xml.sax.SAXException e) {
      aLogger.logFatalError(e.getMessage());
    } catch (IOException e) {
      aLogger.logFatalError(e.getMessage());
    }

    aLogger.logSummaryInfo();
    if (m_aResult.hasForeignElements()) {
      Set<String> aForeignElementURISet = m_aResult.getForeignElements().keySet();
      StringBuilder aBuffer = new StringBuilder();
      Iterator<String> aIter = aForeignElementURISet.iterator();
      boolean bFirst = true;
      while (aIter.hasNext()) {
        String aURI = aIter.next();
        aBuffer.setLength(0);
        aBuffer.append(m_aResult.getForeignElements().get(aURI));
        aBuffer.append(" extension elements from the following namespace were found: ");
        aBuffer.append(aURI);
        aLogger.logInfo(aBuffer.toString(), false);
      }
    }
    if (m_aResult.hasForeignAttributes()) {
      Set<String> aForeignAttributeURISet = m_aResult.getForeignAttributes().keySet();
      Iterator<String> aIter = aForeignAttributeURISet.iterator();
      StringBuilder aBuffer = new StringBuilder();
      while (aIter.hasNext()) {
        String aURI = aIter.next();
        aBuffer.setLength(0);
        aBuffer.append(m_aResult.getForeignAttributes().get(aURI));
        aBuffer.append(" extension attributes from the following namespace were found: ");
        aBuffer.append(aURI);
        aLogger.logInfo(aBuffer.toString(), false);
      }
    }
    return aLogger.hasError();
  }

  protected boolean parseEntry(
      XMLFilter aFilter, Logger aLogger, String aEntryName, boolean bValidating)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    OdfPackage aPkg = getPackage(aLogger);

    if (isEncrypted(aEntryName, aLogger)) {
      return false;
    }

    InputStream aInStream = null;
    try {
      aInStream = getPackage(aLogger).getInputStream(aEntryName, true);
    } catch (Exception e) {
      throw new ODFValidatorException(e);
    }

    return aInStream != null ? parse(aInStream, aFilter, bValidating, aLogger) : false;
  }

  private boolean parse(
      InputStream aInStream, XMLFilter aFilter, boolean bValidating, Logger aLogger)
      throws ODFValidatorException {
    SAXParser aParser = getSAXParser(bValidating);
    SchemaErrorHandler aErrorHandler = new SchemaErrorHandler(aLogger, m_aFilter);

    try {
      XMLReader aReader;
      if (aFilter != null) {
        aFilter.setParent(aParser.getXMLReader());
        aReader = aFilter;
      } else {
        aReader = aParser.getXMLReader();
      }
      if (m_aFilter != null) {
        m_aFilter.startSubFile();
      }
      aReader.setErrorHandler(aErrorHandler);
      aReader.parse(new InputSource(aInStream));
    } catch (org.xml.sax.SAXParseException e) {
      aErrorHandler.fatalErrorNoException(e);
    } catch (org.xml.sax.SAXException e) {
      aLogger.logFatalError(e.getMessage());
    } catch (IOException e) {
      aLogger.logFatalError(e.getMessage());
    }

    if (bValidating) {
      aLogger.logSummaryInfo();
    }
    return aLogger.hasError();
  }

  private boolean isEncrypted(String aEntryName, Logger aLogger) {
    OdfFileEntry aFileEntry = getPackage(aLogger).getFileEntry(aEntryName);
    if (aFileEntry != null) {
      EncryptionDataElement aEncData = aFileEntry.getEncryptionData();
      if (aEncData != null) {
        aLogger.logFatalError(
            "stream content is encrypted. Validation of encrypted content is not supported.");
        return true;
      }
    }
    return false;
  }

  private SAXParser getSAXParser(boolean bValidating) throws ODFValidatorException {
    SAXParser aParser = null;
    if (m_aSAXParserFactory == null) {
      m_aSAXParserFactory = SAXParserFactory.newInstance();
      m_aSAXParserFactory.setNamespaceAware(true);
    }

    try {
      m_aSAXParserFactory.setValidating(bValidating);
      aParser = m_aSAXParserFactory.newSAXParser();
    } catch (javax.xml.parsers.ParserConfigurationException e) {
      throw new ODFValidatorException(e);
    } catch (org.xml.sax.SAXException e) {
      throw new ODFValidatorException(e);
    }

    return aParser;
  }

  /** get the generator */
  public String getGenerator() {
    return m_aResult.getGenerator();
  }

  private String getVersion(Logger aLogger) throws ODFValidatorException {
    String aVersion = null;

    InputStream aInStream = null;
    try {
      OdfPackage aPkg = getPackage(aLogger);
      aInStream =
          aPkg.getInputStream(getStreamName(OdfDocument.OdfXMLFile.META.getFileName()), true);
      if (aInStream == null) {
        aInStream =
            aPkg.getInputStream(getStreamName(OdfDocument.OdfXMLFile.SETTINGS.getFileName()), true);
      }
      if (aInStream == null) {
        aInStream =
            aPkg.getInputStream(getStreamName(OdfDocument.OdfXMLFile.CONTENT.getFileName()), true);
      }
      if (aInStream == null) {
        return null;
      }
    } catch (Exception e) {
      aLogger.logFatalError(e.getMessage());
    }

    SAXParser aParser = getSAXParser(false);

    DefaultHandler aHandler = new VersionHandler();

    try {
      aParser.parse(aInStream, aHandler);
    } catch (SAXVersionException e) {
      aVersion = e.getVersion();
    } catch (org.xml.sax.SAXException e) {
      aLogger.logFatalError(e.getMessage());
    } catch (IOException e) {
      aLogger.logFatalError(e.getMessage());
    }

    return aVersion;
  }
}
