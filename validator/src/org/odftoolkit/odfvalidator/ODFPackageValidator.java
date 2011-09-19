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
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Validator;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.EncryptionData;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validator for Files
 */
public abstract class ODFPackageValidator implements MetaInformationListener {

    static final int CHECK_CONFORMANCE = 0; // all versions
    static final int VALIDATE = 1;          // ODF 1.0 and 1.1 only
    static final int VALIDATE_STRICT = 2;   // ODF 1.0 and 1.1 only
    static final int CHECK_EXTENDED_CONFORMANCE = 3; // ODF 1.2 only

    static final String ODF_VERSION_10 = "1.0";
    static final String ODF_VERSION_11 = "1.1";
    static final String ODF_VERSION_12 = "1.2";

    static final String DOCUMENT_SETTINGS = "document-settings";
    static final String DOCUMENT_STYLES = "document-styles";
    static final String DOCUMENT_CONTENT = "document-content";

    private int m_nLogLevel;
    private int m_nMode = CHECK_CONFORMANCE;
    private SAXParseExceptionFilter m_aFilter = null;
    private ODFValidatorProvider m_aValidatorProvider = null;
    
    private String m_aMediaType = "";
    private String m_aGenerator = "";
    private String m_aConfigVersion = "";
    private Vector<ManifestEntry> m_aSubDocs;

    private SAXParserFactory m_aSAXParserFactory = null;


    ODFPackageValidator( int nLogLevel, int nMode, String aVersion, String aMediaType,
                             SAXParseExceptionFilter aFilter,ODFValidatorProvider aValidatorProvider) {
        m_nLogLevel = nLogLevel;
        m_nMode = nMode;
        m_aFilter = aFilter;
        m_aValidatorProvider = aValidatorProvider;
        m_aMediaType = aMediaType;
        m_aConfigVersion = aVersion;
    }
    
       

    abstract String getLoggerName();
    
    abstract OdfPackage getPackage( Logger aLogger );
    
    abstract boolean isRootPackage();
    
    abstract String getStreamName( String aEntry );
    
    public boolean validate(PrintStream aOut) throws ODFValidatorException
    {
        Logger aLogger = new Logger( getLoggerName(), "", aOut, m_nLogLevel);

        boolean bHasErrors = false;

        OdfPackage aPkg = getPackage( aLogger );
        if( aPkg == null )
            return true;

        boolean bRoot = isRootPackage();
        if( bRoot ) 
            validateMimetype( aOut );
        
        try
        {
            String aDocVersion = getVersion( aLogger );
            if( aDocVersion != null )
                aLogger.logInfo( "ODF Version: " + aDocVersion, false );
            String aVersion = m_aConfigVersion == null ? aDocVersion : m_aConfigVersion;
            
            bHasErrors |= validateMeta(aOut, getStreamName( OdfDocument.OdfXMLFile.META.getFileName()), aVersion, true );
            if( bRoot )
                bHasErrors |= validateManifest(aOut, aVersion  );
            aLogger.logInfo( "Media Type: " + m_aMediaType , false);
            bHasErrors |= validateEntry(aOut, getStreamName(OdfDocument.OdfXMLFile.SETTINGS.getFileName()), DOCUMENT_SETTINGS, aVersion);
            bHasErrors |= validateEntry(aOut, getStreamName( OdfDocument.OdfXMLFile.STYLES.getFileName()), DOCUMENT_STYLES, aVersion );
            if( m_aMediaType.equals(ODFMediaTypes.FORMULA_MEDIA_TYPE))
                bHasErrors |= validateMathML(aOut, getStreamName( OdfDocument.OdfXMLFile.CONTENT.getFileName()), aVersion );
            else
                bHasErrors |= validateEntry(aOut, getStreamName( OdfDocument.OdfXMLFile.CONTENT.getFileName()), DOCUMENT_CONTENT, aVersion );
            
            if( bRoot )
            {
                if(m_aSubDocs != null )
                {
                    Iterator<ManifestEntry> aIter = m_aSubDocs.iterator();
                    while( aIter.hasNext() )
                    {
                        ManifestEntry aEntry = aIter.next();
                        ODFPackageValidator aPackageValidator = 
                            new ODFSubPackageValidator( aPkg, getLoggerName(), aEntry.m_aFullPath, aEntry.m_aMediaType,
                                                  m_nLogLevel, m_nMode, m_aConfigVersion, m_aFilter, m_aGenerator, m_aValidatorProvider );
                        bHasErrors |= aPackageValidator.validate(aOut);
                    }
                }

                if( aVersion.equals(ODF_VERSION_12))
                {
                    bHasErrors |= validateDSig( aOut, OdfPackageExt.STREAMNAME_DOCUMENT_SIGNATURES, aVersion );
                    bHasErrors |= validateDSig( aOut, OdfPackageExt.STREAMNAME_MACRO_SIGNATURES, aVersion );
                }
            }
        }
        catch( ZipException e )
        {
            aLogger.logFatalError( e.getMessage() );
        }
        catch( IOException e )
        {
            aLogger.logFatalError( e.getMessage() );
        }
        
        if( bRoot )
        {
            if( bHasErrors || aLogger.hasError() )
            {
                aLogger.logInfo( "validation errors found" , true );
                if( m_nLogLevel < Logger.INFO )
                    aLogger.logInfo( "Generator: " + m_aGenerator , true );
            }
            else
                aLogger.logInfo(  "no errors" , false);  
        }
            
        return bHasErrors || aLogger.hasError();
    }
    
    protected boolean validateEntry(PrintStream aOut, String aEntryName, String aLocalElementName, String aVersion ) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        Logger aLogger = new Logger(getLoggerName(),aEntryName,aOut, m_nLogLevel);
        XMLFilter aFilter = new ContentFilter(aLogger, aLocalElementName );
        if( (m_nMode == CHECK_CONFORMANCE && (aVersion.equals(ODF_VERSION_10) || aVersion.equals(ODF_VERSION_11))) ||
            m_nMode == CHECK_EXTENDED_CONFORMANCE )
        {
            XMLFilter aAlienFilter = new AlienFilter(aLogger,aVersion);
            aAlienFilter.setParent(aFilter);
            aFilter = aAlienFilter;
        }
        Validator aValidator = m_nMode == VALIDATE_STRICT ? m_aValidatorProvider.getStrictValidator(aOut, aVersion)
                                                          : m_aValidatorProvider.getValidator(aOut,aVersion);
        return validateEntry(aOut, aFilter, aValidator, aLogger, aEntryName );
    }

    private boolean validateMeta(PrintStream aOut, String aEntryName, String aVersion, boolean bIsRoot) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        Logger aLogger = new Logger(getLoggerName(),aEntryName,aOut, m_nLogLevel);
        XMLFilter aFilter = new MetaFilter(aLogger, this );
        if( (m_nMode == CHECK_CONFORMANCE && (aVersion.equals(ODF_VERSION_10) || aVersion.equals(ODF_VERSION_11))) ||
            m_nMode == CHECK_EXTENDED_CONFORMANCE )
        {
            XMLFilter aAlienFilter = new AlienFilter(aLogger,aVersion);
            aAlienFilter.setParent(aFilter);
            aFilter = aAlienFilter;
        }

        Validator aValidator = m_nMode == VALIDATE_STRICT ? m_aValidatorProvider.getStrictValidator(aOut,aVersion)
                                                          : m_aValidatorProvider.getValidator(aOut,aVersion);
        return validateEntry(aOut, aFilter, aValidator, aLogger, aEntryName );
    }

    private boolean validateMathML(PrintStream aOut, String aEntryName, String aVersion ) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        Logger aLogger = new Logger(getLoggerName(),aEntryName,aOut, m_nLogLevel);
        String aMathMLDTDSystemId = m_aValidatorProvider.getMathMLDTDSystemId(aVersion);
        if( aMathMLDTDSystemId != null )
        {
            // validate using DTD
            return parseEntry(aOut, new MathML101Filter(aMathMLDTDSystemId, aLogger), aLogger, aEntryName, true);
        }
        else
        {
            Validator aMathMLValidator = m_aValidatorProvider.getMathMLValidator(aOut,null);
            if( aMathMLValidator == null )
            {
                aLogger.logInfo( "MathML schema is not available. Validation has been skipped.", false);
                return false;
            }
            return validateEntry( aOut, new MathML20Filter(aLogger), aMathMLValidator, aLogger, aEntryName );
        }
    }
    
    private boolean validateManifest(PrintStream aOut, String aVersion ) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        boolean bRet;
        Logger aLogger = new Logger(getLoggerName(),OdfPackage.OdfFile.MANIFEST.getPath(),aOut, m_nLogLevel);
        ManifestFilter aFilter = new ManifestFilter(aLogger);
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
        
        m_aSubDocs = aFilter.getSubDocuments();
        m_aMediaType = aFilter.getMediaType();
        return bRet;
    }

    protected boolean validateDSig(PrintStream aOut, String aEntryName, String aVersion ) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        Validator aValidator=m_aValidatorProvider.getDSigValidator(aOut,aVersion);
        Logger aLogger = new Logger(getLoggerName(),aEntryName,aOut, m_nLogLevel);
        if ( aValidator == null ) {
            aLogger.logWarning("Signature not validated because there is no Signature Validator configured for the selected Configuration");
            return false;
        }

        return validateEntry(aOut, new DSigFilter(aLogger), aValidator, aLogger, aEntryName );
    }

    private boolean validateEntry(PrintStream aOut, XMLFilter aFilter, 
                           javax.xml.validation.Validator aValidator,
                           Logger aLogger,
                           String aEntryName ) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        OdfPackage aPkg = getPackage(aLogger);
        
        if( !aEntryName.equals(OdfPackage.OdfFile.MANIFEST.getPath()) && isEncrypted(aEntryName,aLogger) )
            return false;
        
        InputStream aInStream = null;
        try
        {
            aInStream = aPkg.getInputStream(aEntryName);
        }
        catch( Exception e )
        {
            throw new ODFValidatorException( e );
        }
                

        if ( aValidator == null ) {
            
            aLogger.logWarning("no Validator configured in selected Configuration for this file type");
            return false;
        }



        return aInStream != null ? validate(aOut, aInStream, aFilter, aValidator, aLogger ) : false;
    }
    
    private boolean validate(PrintStream aOut, InputStream aInStream,
                      XMLFilter aFilter,
                      javax.xml.validation.Validator aValidator,
                      Logger aLogger ) throws ODFValidatorException
    {
        SAXParser aParser = getSAXParser(false);
        SchemaErrorHandler aErrorHandler = new SchemaErrorHandler(aLogger, m_aFilter );

        try
        {
            XMLReader aReader;
            if( aFilter != null )
            {
                XMLReader aParent = aFilter.getParent();
                if( aParent != null )
                    ((XMLFilter)aParent).setParent( aParser.getXMLReader() ) ;
                else
                    aFilter.setParent( aParser.getXMLReader() ) ;
                aReader = aFilter;
            }
            else
            {
                aReader = aParser.getXMLReader();
            }

            if( m_aFilter != null )
            {
                m_aFilter.startSubFile();
            }
            aValidator.setErrorHandler(aErrorHandler);
            try
            {
                aValidator.validate( new SAXSource(aReader,
                                       new InputSource( aInStream ) ));
            }
            catch( RuntimeException e )
            {
                aLogger.logFatalError(e.getMessage());
                m_aValidatorProvider.resetValidatorProvider();
            }
        }
        catch( org.xml.sax.SAXParseException e )
        {
            aErrorHandler.fatalErrorNoException(e);
        }
        catch( org.xml.sax.SAXException e )
        {
            aLogger.logFatalError(e.getMessage());
        }
        catch( IOException e )
        {
            aLogger.logFatalError(e.getMessage());
        }
        
        aLogger.logInfo( aLogger.hasError() ? "validation errors found" : "no errors" , false);            
        return aLogger.hasError();
    }

    private boolean parseEntry(PrintStream aOut, XMLFilter aFilter, 
                           Logger aLogger,
                           String aEntryName , boolean bValidating) throws IOException, ZipException, IllegalStateException, ODFValidatorException
    {
        OdfPackage aPkg = getPackage(aLogger);

        if( isEncrypted(aEntryName,aLogger) )
            return false;
        
        InputStream aInStream = null;
        try
        {
            aInStream = getPackage(aLogger).getInputStream(aEntryName);
        }
        catch( Exception e )
        {
            throw new ODFValidatorException( e );
        }

        return aInStream != null ? parse(aOut, aInStream, aFilter, bValidating, aLogger ) : false;
    }

    private boolean parse(PrintStream aOut, InputStream aInStream, XMLFilter aFilter, boolean bValidating, Logger aLogger ) throws ODFValidatorException
    {
        SAXParser aParser = getSAXParser(bValidating);
        aLogger.setOutputStream(aOut);
        SchemaErrorHandler aErrorHandler = new SchemaErrorHandler( aLogger, m_aFilter );

        try
        {
            XMLReader aReader;
            if( aFilter != null )
            {
                aFilter.setParent( aParser.getXMLReader() );
                aReader = aFilter;
            }
            else
            {
                aReader = aParser.getXMLReader();
            }
            if( m_aFilter != null )
            {
                m_aFilter.startSubFile();
            }
            aReader.setErrorHandler(aErrorHandler);
            aReader.parse(new InputSource(aInStream));
        }
        catch( org.xml.sax.SAXParseException e )
        {
            aErrorHandler.fatalErrorNoException(e);
        }
        catch( org.xml.sax.SAXException e )
        {
            aLogger.logFatalError(e.getMessage());
        }
        catch( IOException e )
        {
            aLogger.logFatalError(e.getMessage());
        }
        
        if( bValidating )
            aLogger.logInfo( aLogger.hasError() ? "validation errors found" : "no errors" , false);            
        return aLogger.hasError();
    }

    private boolean isEncrypted( String aEntryName, Logger aLogger )
    {
        OdfFileEntry aFileEntry = getPackage(aLogger).getFileEntry(aEntryName);
        if ( aFileEntry != null )
        {
            EncryptionData aEncData=aFileEntry.getEncryptionData();
            if ( aEncData != null ) {
                 aLogger.logFatalError( "stream content is encrypted. Validataion of encrypted content is not supported.");                
                 return true;
            }
        }
        return false;
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
        
    private SAXParser getSAXParser(boolean bValidating) throws ODFValidatorException
    {
        SAXParser aParser = null;
        if( m_aSAXParserFactory == null )
        {
            m_aSAXParserFactory = SAXParserFactory.newInstance();
            m_aSAXParserFactory.setNamespaceAware(true);
        }

        try
        {
            m_aSAXParserFactory.setValidating(bValidating);
            aParser = m_aSAXParserFactory.newSAXParser();
        }
        catch( javax.xml.parsers.ParserConfigurationException e )
        {
            throw new ODFValidatorException( e );
        }
        catch( org.xml.sax.SAXException e )
        {
            throw new ODFValidatorException( e );
        }
        
        return aParser;
    }

    /**
     * set the generator
     */ 
    public void setGenerator(String aGenerator) 
    {
        m_aGenerator = aGenerator;
        if( m_aFilter != null )
            m_aFilter.setGenerator(m_aGenerator);
    }

    /**
     * get the generator
     */
    public String getGenerator() {
        return m_aGenerator;
    }

    private String getVersion(Logger aLogger) throws ODFValidatorException
    {
        String aVersion = null;

        InputStream aInStream = null;
        try
        {
            OdfPackage aPkg = getPackage(aLogger);
            aInStream = aPkg.getInputStream(getStreamName(OdfDocument.OdfXMLFile.META.getFileName()));
            if( aInStream == null )
                aInStream = aPkg.getInputStream(getStreamName(OdfDocument.OdfXMLFile.SETTINGS.getFileName()));
            if( aInStream == null )
                aInStream = aPkg.getInputStream(getStreamName(OdfDocument.OdfXMLFile.CONTENT.getFileName()));
        }
        catch( Exception e )
        {
            aLogger.logFatalError(e.getMessage());
        }
        
        SAXParser aParser = getSAXParser(false);
        
        DefaultHandler aHandler = new VersionHandler();
        
        try
        {
            aParser.parse(aInStream, aHandler);
        }
        catch( SAXVersionException e )
        {
            aVersion = e.getVersion();
        }
        catch( org.xml.sax.SAXException e )
        {
            aLogger.logFatalError(e.getMessage());
        }
        catch( IOException e )
        {
            aLogger.logFatalError(e.getMessage());
        }
 
        return aVersion;
    }

}