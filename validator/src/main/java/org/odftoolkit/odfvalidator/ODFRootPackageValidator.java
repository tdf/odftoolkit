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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Validator;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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

  protected abstract byte[] getBytes() throws IOException;

  // naive impl
  private Integer lastIndexOf(byte[] haystack, byte[] needle) {
    assert (needle != null);
    assert (haystack != null);
    assert (needle.length > 0);
    assert (haystack.length >= needle.length);
    outer:
    for (int i = haystack.length - needle.length; 0 <= i; --i) {
      if (haystack[i] == needle[0]) {
        for (int j = 1; j < needle.length; ++j) {
          if (haystack[i + j] != needle[j]) {
            continue outer;
          }
        }
        return i; // found it
      }
    }
    return null;
  }

  private Integer findEndOfCD(byte[] file) throws IOException {
    byte[] endSig = {0x50, 0x4b, 0x05, 0x06};
    Integer sigPos = lastIndexOf(file, endSig);
    return sigPos;
  }

  class ZipEntry {
    String name;
    int offset;
    int size;
    int crc;
    boolean compressed;
  }

  class WishJavaHadTuples {
    ZipEntry entry;
    int pos;

    WishJavaHadTuples(ZipEntry a, int b) {
      entry = a;
      pos = b;
    }
  }

  private WishJavaHadTuples getFileHeader(ByteBuffer buf, int pos) throws IOException {
    if (buf.getInt(pos) != 0x02014b50) {
      return null;
    }
    ZipEntry ret = new ZipEntry();
    ret.compressed = (buf.getShort(pos + 10) & 8) != 0;
    ret.crc = buf.getInt(pos + 16);
    ret.size = buf.getInt(pos + 20);
    short fileNameLength = buf.getShort(pos + 28);
    short extraFieldLength = buf.getShort(pos + 30);
    short fileCommentLength = buf.getShort(pos + 32);
    ret.offset = buf.getInt(pos + 42);
    ret.name = new String(buf.array(), pos + 46, fileNameLength, StandardCharsets.UTF_8);
    return new WishJavaHadTuples(
        ret, pos + 46 + fileNameLength + extraFieldLength + fileCommentLength);
  }

  private List<ZipEntry> readEntries(byte[] file) throws IOException {
    Integer i = findEndOfCD(file);
    if (i == null || file.length < i + 20) {
      return null;
    }
    ByteBuffer bb = ByteBuffer.wrap(file).order(ByteOrder.LITTLE_ENDIAN);
    int cdSize = bb.getInt(i + 12);
    int cdOffset = bb.getInt(i + 16);
    bb = ByteBuffer.wrap(file).order(ByteOrder.LITTLE_ENDIAN);
    List<ZipEntry> results = new ArrayList();
    try {
      WishJavaHadTuples result = getFileHeader(bb, cdOffset);
      while (result != null) {
        results.add(result.entry);
        if (result.pos >= cdOffset + cdSize) {
          break;
        }
        result = getFileHeader(bb, result.pos);
      }
    } catch (java.nio.BufferUnderflowException e) {
      // EOF
    } catch (IndexOutOfBoundsException e) {
      // let's report EOF 2 different ways, why not
    }

    return results;
  }

  private InputStream readEntry(byte[] file, ZipEntry entry) throws IOException {
    short fileNameLength =
        (short)
            (Byte.toUnsignedInt(file[entry.offset + 27]) << 8
                | Byte.toUnsignedInt(file[entry.offset + 26]));
    short extraFieldLength =
        (short)
            (Byte.toUnsignedInt(file[entry.offset + 29]) << 8
                | Byte.toUnsignedInt(file[entry.offset + 28]));
    ByteArrayInputStream stream =
        new ByteArrayInputStream(
            file, entry.offset + 30 + fileNameLength + extraFieldLength, entry.size);
    if (entry.compressed) {
      return new InflaterInputStream(stream, new Inflater(true));
    } else {
      return stream;
    }
  }

  class ManifestVersionHandler extends DefaultHandler {

    private static final String NAMESPACE_URI =
        "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";
    private static final String ROOT = "manifest";
    private static final String VERSION = "version";

    @Override
    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAttributes)
        throws SAXException {
      super.startElement(aUri, aLocalName, aQName, aAttributes);

      if (aUri.equals(NAMESPACE_URI) && aLocalName.equalsIgnoreCase(ROOT)) {
        String aVersion = aAttributes.getValue(NAMESPACE_URI, VERSION);
        throw new SAXVersionException(aVersion);
      }
    }
  }

  protected String getVersionFromManifest(Logger logger, InputStream stream)
      throws ODFValidatorException {
    String version = null;
    SAXParser parser = getSAXParser(false);
    DefaultHandler handler = new ManifestVersionHandler();

    try {
      parser.parse(stream, handler);
    } catch (SAXVersionException e) {
      version = e.getVersion();
    } catch (org.xml.sax.SAXException e) {
      logger.logFatalError(e.getMessage());
    } catch (IOException e) {
      logger.logFatalError(e.getMessage());
    }

    return version;
  }

  protected void fallbackValidateManifest(Logger logger) {
    try {
      byte[] file = getBytes();
      List<ZipEntry> entries = readEntries(file);
      for (ZipEntry entry : entries) {
        if (entry.name.equals(OdfPackage.OdfFile.MANIFEST.getPath())) {
          InputStream stream = readEntry(file, entry);
          String versionString = getVersionFromManifest(logger, stream);
          logger.logInfo("ODF version of manifest: \"" + versionString + "\"", false);
          OdfVersion version = OdfVersion.valueOf(versionString, true);
          stream = readEntry(file, entry);
          ManifestFilter filter = new ManifestFilter(logger, m_aResult, this);
          Validator manifestValidator =
              m_aValidatorProvider.getManifestValidator(logger.getOutputStream(), version);
          validate(stream, filter, manifestValidator, logger);
          return;
        }
      }
    } catch (Exception e) {
      logger.logWarning("fallbackValidateManifest Exception:\n" + e);
    }
  }

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
        if (e.getMessage().endsWith("only DEFLATED entries can have EXT descriptor")) {
          aLogger.logFatalError(e.getMessage());
          aLogger.logFatalError(
              "The document is encrypted. Validation of encrypted documents is not supported.");
          // HACK: emergency validation of manifest.xml only
          Logger aManifestLogger = new Logger(OdfPackage.OdfFile.MANIFEST.getPath(), aLogger);
          fallbackValidateManifest(aManifestLogger);
          aLogger.logSummaryInfo(); // summary for root package?
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
        || aMimetype.equals(ODFMediaTypes.SPREADSHEET_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.SPREADSHEET_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.CHART_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.CHART_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.IMAGE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.IMAGE_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.FORMULA_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.FORMULA_TEMPLATE_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.TEXT_MASTER_MEDIA_TYPE)
        || aMimetype.equals(ODFMediaTypes.TEXT_WEB_MEDIA_TYPE))) {
      aLogger.logInfo("mimetype is not an ODFMediaTypes mimetype.", false);
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
