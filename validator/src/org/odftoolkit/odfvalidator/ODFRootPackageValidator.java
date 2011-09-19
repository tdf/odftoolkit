/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.odftoolkit.odfvalidator;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipException;
import javax.xml.validation.Validator;
import org.odftoolkit.odfdom.pkg.OdfPackage;

public abstract class ODFRootPackageValidator extends ODFPackageValidator implements ManifestEntryListener {

    private OdfPackage m_aPkg = null;
    private Vector<ManifestEntry> m_aSubDocs = null;


    ODFRootPackageValidator(Logger.LogLevel nLogLevel, OdfValidatorMode eMode, OdfVersion aVersion, SAXParseExceptionFilter aFilter, ODFValidatorProvider aValidatorProvider) {
        super(nLogLevel, eMode, aVersion, aFilter, aValidatorProvider);
    }

    abstract OdfPackage getPackage() throws Exception;
    
    OdfPackage getPackage(Logger aLogger) {
        if (m_aPkg == null) {
            try {
                m_aPkg = getPackage();
            } catch (IOException e) {
                if (e.getMessage().startsWith("only DEFLATED entries can have EXT descriptor")) {
                    aLogger.logFatalError("The document is encrypted. Validation of encrypted documents is not supported.");
                } else {
                    aLogger.logFatalError(e.getMessage());
                }
            } catch (Exception e) {
                aLogger.logFatalError(e.getMessage());
            }
        }

        return m_aPkg;
    }

    String getStreamName( String aEntry )
    {
        return aEntry;
    }

    @Override
    protected boolean validatePre(PrintStream aOut, OdfVersion aVersion ) throws ODFValidatorException, IOException
    {
        boolean bErrorsFound = validateMimetype( aOut );
        bErrorsFound |= validateManifest(aOut, aVersion  );

        return bErrorsFound;
    }

    @Override
    protected boolean validatePost(PrintStream aOut,Logger aLogger,OdfVersion aVersion ) throws ODFValidatorException, IOException
    {
        boolean bHasErrors = false;
        if(m_aSubDocs != null )
        {
            Iterator<ManifestEntry> aIter = m_aSubDocs.iterator();
            while( aIter.hasNext() )
            {
                ManifestEntry aEntry = aIter.next();
                ODFPackageValidator aPackageValidator =
                    new ODFSubPackageValidator( getPackage(aLogger), getLoggerName(), aEntry.getFullPath(), aEntry.getMediaType(),
                                                  m_nLogLevel, m_eMode, m_aConfigVersion, m_aFilter, m_aResult.getGenerator(), m_aValidatorProvider );
                bHasErrors |= aPackageValidator.validate(aOut);
            }
        }

        if( aVersion.compareTo(OdfVersion.V1_2) >= 0 )
        {
            bHasErrors |= validateDSig( aOut, OdfPackageExt.STREAMNAME_DOCUMENT_SIGNATURES, aVersion );
            bHasErrors |= validateDSig( aOut, OdfPackageExt.STREAMNAME_MACRO_SIGNATURES, aVersion );
        }

        return bHasErrors;
    }

    @Override
    protected void logSummary( boolean bHasErrors, Logger aLogger )
    {
        if( bHasErrors || aLogger.hasError() )
        {
            aLogger.logInfo( "validation errors found" , true );
            if( m_nLogLevel.compareTo(Logger.LogLevel.INFO) < 0 )
                aLogger.logInfo( "Generator: " + m_aResult.getGenerator() , true );
        }
        else
            aLogger.logInfo(  "no errors" , false);
    }


    public void foundManifestEntry( ManifestEntry aManifestEntry )
    {
        if( aManifestEntry.isOpenDocumentMediaType() )
        {
            if( m_aSubDocs == null )
                m_aSubDocs = new Vector<ManifestEntry>();
            m_aSubDocs.add( aManifestEntry );
        }
    }

    private boolean validateMimetype( PrintStream aOut )
    {
        Logger aLogger = new Logger(getLoggerName(),"MIMETYPE",aOut, m_nLogLevel);

        String aMimetype=getPackage(aLogger).getMediaType();
        if( (aMimetype == null) || aMimetype.length() == 0 ) {
            aLogger.logFatalError("file is not a zip file, or has no mimetype.");
            return true;
        }

        if( !(aMimetype.equals(ODFMediaTypes.TEXT_MEDIA_TYPE)
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
            || aMimetype.equals(ODFMediaTypes.TEXT_WEB_MEDIA_TYPE) ) ) {
                aLogger.logInfo("mimetype is not an ODFMediaTypes mimetype.",false);
                return true;
        }

        /* TODO: not supported by ODFDOM
        if ( aDocFile .isMimeTypeValid() ) {
            aLogger.logInfo("no errors",false);
        } else {
            aLogger.logError("file is not the first file in the ODF package or is compressed.");
            return true;
        }
        */

        return false;
    }

    private boolean validateManifest(PrintStream aOut, OdfVersion aVersion ) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        boolean bRet;
        Logger aLogger = new Logger(getLoggerName(),OdfPackage.OdfFile.MANIFEST.getPath(),aOut, m_nLogLevel);
        ManifestFilter aFilter = new ManifestFilter(aLogger,m_aResult, this);
        Validator aManifestValidator = m_aValidatorProvider.getManifestValidator(aOut,aVersion);
        if( aManifestValidator != null )
        {
            bRet = validateEntry(aOut, aFilter,
                         aManifestValidator, aLogger, OdfPackage.OdfFile.MANIFEST.getPath() );
        }
        else
        {
            aLogger.logInfo( "Validation of " + OdfPackage.OdfFile.MANIFEST.getPath() + " skipped.", false);
            bRet = parseEntry(aOut, aFilter, aLogger, OdfPackage.OdfFile.MANIFEST.getPath() , false);
        }
        return bRet;
    }

}
