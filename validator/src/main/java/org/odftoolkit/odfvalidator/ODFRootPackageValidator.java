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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipException;
import javax.xml.validation.Validator;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.xml.sax.ErrorHandler;

abstract class ODFRootPackageValidator extends ODFPackageValidator
    implements ManifestEntryListener {

  private OdfPackage m_aPkg = null;
  private ArrayList<ManifestEntry> m_aSubDocs = null;
  private ODFPackageErrorHandler m_ErrorHandler = null;

  protected ODFRootPackageValidator(
      Logger.LogLevel nLogLevel,
      OdfValidatorMode eMode,
      OdfVersion aVersion,
      SAXParseExceptionFilter aFilter,
      ODFValidatorProvider aValidatorProvider) {
    super(nLogLevel, eMode, aVersion, aFilter, aValidatorProvider);
  }

  protected abstract OdfPackage getPackage(ErrorHandler handler) throws Exception;

  protected OdfPackage getPackage(Logger aLogger) {
    if (m_aPkg == null) {
      try {
        m_ErrorHandler = new ODFPackageErrorHandler();
        m_aPkg = getPackage(m_ErrorHandler);
        // for additional mimetype checking, load root document
        try {
          OdfDocument.loadDocument(m_aPkg, "");
        } catch (Exception e) {
          // ignore -- the interesting stuff is passed to handler
        }
      } catch (IOException e) {
        if (e.getMessage().startsWith("only DEFLATED entries can have EXT descriptor")) {
          aLogger.logFatalError(
              "The document is encrypted. Validation of encrypted documents is not supported.");
        } else {
          aLogger.logFatalError(e.getMessage());
        }
      } catch (Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        aLogger.logFatalError(e.getMessage() + "\n" + errors.toString());
      }
    }
    return m_aPkg;
  }

  protected String getStreamName(String aEntry) {
    return aEntry;
  }

  @Override
  protected boolean validatePre(Logger aLogger, OdfVersion aVersion)
      throws ODFValidatorException, IOException {
    Logger aManifestLogger = new Logger(OdfPackage.OdfFile.MANIFEST.getPath(), aLogger);
    Logger aMimetypeLogger = new Logger("mimetype", aLogger);

    // UGLY: do something that causes ODFDOM to parse the manifest, which
    // may cause m_ErrorHandler to be called
    m_aPkg.getFilePaths();
    // hack: just create logger again, too lazy to create a Pair class
    // and return it from validateMimetype...
    boolean bErrorsFound =
        m_ErrorHandler.processErrors(aLogger, aManifestLogger, aMimetypeLogger, aVersion);

    bErrorsFound |= validateMimetype(aMimetypeLogger, aVersion);
    bErrorsFound |= validateManifest(aManifestLogger, aVersion);
    aMimetypeLogger.logSummaryInfo();

    return bErrorsFound;
  }

  @Override
  protected boolean validatePost(Logger aLogger, OdfVersion aVersion)
      throws ODFValidatorException, IOException {
    boolean bHasErrors = false;
    if (m_aSubDocs != null) {
      Iterator<ManifestEntry> aIter = m_aSubDocs.iterator();
      while (aIter.hasNext()) {
        ManifestEntry aEntry = aIter.next();
        ODFPackageValidator aPackageValidator =
            new ODFSubPackageValidator(
                getPackage(aLogger),
                getLoggerName(),
                aEntry.getFullPath(),
                aEntry.getMediaType(),
                m_nLogLevel,
                m_eMode,
                m_aConfigVersion,
                m_aFilter,
                m_aResult.getGenerator(),
                m_aValidatorProvider);
        bHasErrors |= aPackageValidator.validate(aLogger);
      }
    }

    if (aVersion.compareTo(OdfVersion.V1_2) >= 0) {
      bHasErrors |= validateDSig(aLogger, OdfPackageExt.STREAMNAME_DOCUMENT_SIGNATURES, aVersion);
      bHasErrors |= validateDSig(aLogger, OdfPackageExt.STREAMNAME_MACRO_SIGNATURES, aVersion);
    }

    return bHasErrors;
  }

  @Override
  protected void logSummary(boolean bHasErrors, Logger aLogger) {
    aLogger.logSummaryInfo();
    if ((bHasErrors || aLogger.hasError()) && m_nLogLevel.compareTo(Logger.LogLevel.INFO) < 0) {
      aLogger.logInfo("Generator: " + m_aResult.getGenerator(), true);
    }
  }

  public void foundManifestEntry(ManifestEntry aManifestEntry) {
    if (aManifestEntry.isOpenDocumentMediaType()) {
      if (m_aSubDocs == null) {
        m_aSubDocs = new ArrayList<ManifestEntry>();
      }
      m_aSubDocs.add(aManifestEntry);
    }
  }

  private boolean validateMimetype(Logger aLogger, OdfVersion aVersion) {
    boolean bHasErrors = false;

    String aMimetype = getPackage(aLogger).getMediaTypeString();
    if ((aMimetype == null) || aMimetype.length() == 0) {
      aLogger.logFatalError("file is not a zip file, or has no mimetype.");
      bHasErrors = true;
    } else if (!(aMimetype.equals(ODFMediaTypes.TEXT_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.TEXT_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.GRAPHICS_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.GRAPHICS_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.PRESENTATION_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.PRESENTATION_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.SPREADSHEET_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.SPREADSHEET_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.CHART_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.CHART_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.IMAGE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.IMAGE_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.FORMULA_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.FORMULA_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.DATABASE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.TEXT_MASTER_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.TEXT_MASTER_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.TEXT_WEB_MEDIA_TYPE))) {
      aLogger.logError("mimetype is not an ODFMediaTypes mimetype.");
      bHasErrors = true;
    }

    return bHasErrors;
  }

  private boolean validateManifest(Logger aLogger, OdfVersion aVersion)
      throws IOException, ZipException, IllegalStateException, ODFValidatorException {
    boolean bRet;
    ManifestFilter aFilter = new ManifestFilter(aLogger, m_aResult, this);
    Validator aManifestValidator =
        m_aValidatorProvider.getManifestValidator(aLogger.getOutputStream(), aVersion);
    if (aManifestValidator != null) {
      bRet =
          validateEntry(
              aFilter, aManifestValidator, aLogger, OdfPackage.OdfFile.MANIFEST.getPath());
    } else {
      aLogger.logInfo(
          "Validation of " + OdfPackage.OdfFile.MANIFEST.getPath() + " skipped.", false);
      bRet = parseEntry(aFilter, aLogger, OdfPackage.OdfFile.MANIFEST.getPath(), false);
    }
    return bRet;
  }
}
