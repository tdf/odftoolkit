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

import javax.xml.XMLConstants;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;

public class ODFValidator implements ODFValidatorProvider {

    // Prefix used to distinguish internal resources from external ones
    
    // Loglevel
    private Logger.LogLevel m_nLogLevel;

    // Validator provider
    private SchemaFactory m_aRNGSchemaFactory = null;
    private SchemaFactory m_aXSDSchemaFactory = null;

    // User provided Configuration
    protected Configuration m_aConfig = null;
    
    // User provided ODF version
    protected OdfVersion m_aVersion = null;
    
    // User provided preference for using Math DTD
    protected boolean m_bUseMathDTD = false;

    // Validatore and configuration cache
    private HashMap<String,Validator> m_aValidatorMap = null;
    private HashMap<OdfVersion,Configuration> m_aConfigurationMap = null;

    // Generator from last validateFile or validateStream call
    private String m_aGenerator = "";

    /** Creates a new instance of Validator */
    public ODFValidator( Configuration aConfig,
                         Logger.LogLevel nLogLevel,
                         OdfVersion aVersion,
                         boolean bUseMathDTD ) throws ODFValidatorException {
        
        m_nLogLevel = nLogLevel;
        m_aConfig = aConfig;
        m_aVersion = aVersion;
        m_bUseMathDTD = bUseMathDTD;
    }    
    
    public boolean validate(PrintStream aOut, Configuration aConfig, OdfValidatorMode eMode ) throws ODFValidatorException
    {
        List<String> aFileNames = aConfig.getListPropety(Configuration.PATH);
        String aExcludeRegExp = aConfig.getProperty(Configuration.EXCLUDE);
        String aRecursive = aConfig.getProperty(Configuration.RECURSIVE);
        boolean bRecursive = aRecursive != null ? Boolean.valueOf(aRecursive) : false;
        String aFilterFileName = aConfig.getProperty(Configuration.FILTER);

        return validate(aOut, aFileNames, aExcludeRegExp, eMode, bRecursive, aFilterFileName );
    }


    public boolean validate(PrintStream aOut, List<String> aFileNames, String aExcludeRegExp, 
                            OdfValidatorMode eMode, boolean bRecursive,
                            String aFilterFileName ) throws ODFValidatorException
    {
        boolean bRet = false;

        SAXParseExceptionFilter aFilter = null;
        if( aFilterFileName != null && aFilterFileName.length() != 0 )
            aFilter = new ValidationOOoTaskIdErrorFilter( new File(aFilterFileName), aOut );

        FileFilter aFileFilter = new ODFFileFilter( aExcludeRegExp, bRecursive );
        
        Iterator<String> aIter = aFileNames.iterator();
        while( aIter.hasNext() )
        {
            File aFile = new File( aIter.next() );
            bRet |= aFile.isDirectory() ? validateDir(aOut, aFile, aFileFilter, eMode, aFilter )
                : validateFile(aOut, aFile, eMode, aFilter );
        }
        
        return bRet;
    }
    
    public boolean validate(PrintStream aOut, InputStream aInputStream, String aBaseURI,
                            OdfValidatorMode eMode,
                            SAXParseExceptionFilter aFilter ) throws ODFValidatorException
    {
        return validateStream( aOut, aInputStream, aBaseURI, eMode, aFilter );
    }
    

    private boolean validateDir(PrintStream aOut, final File aDir, FileFilter aFileFilter, OdfValidatorMode eMode, SAXParseExceptionFilter aFilter ) throws ODFValidatorException
    {
        boolean bRet = true;
        File[] aFiles = aDir.listFiles( aFileFilter );

        if( aFiles != null )
        {
            for( int i=0; i<aFiles.length; ++i)
            {
                File aFile = aFiles[i];
                if( aFile.isDirectory() )
                {
                    bRet |= validateDir(aOut, aFile, aFileFilter, eMode, aFilter );
                }
                else
                {
                    bRet |= validateFile(aOut, aFile, eMode, aFilter );
                }
            }
        }
        return bRet;
    }

    /**
     * validate the input File
     *
     * <p>After validation the getGenerator method can be called to get the generator of the validated file</p>
     */
    public boolean validateFile(PrintStream aOut, File aDocFile, OdfValidatorMode eMode, SAXParseExceptionFilter aFilter ) throws ODFValidatorException
    {
        ODFFileValidator aFileValidator = 
            new ODFFileValidator( aDocFile, m_nLogLevel, eMode, m_aVersion, aFilter, this );
        
        boolean result=aFileValidator.validate(aOut);

        m_aGenerator=aFileValidator.getGenerator();
        return result;
    }

    /**
     * validate the input Stream
     *
     * <p>After validation the getGenerator method can be called to get the generator of the validated file</p>
     */
    public boolean validateStream(PrintStream aOut, InputStream aInputStream, String aBaseURI,
                                  OdfValidatorMode eMode,
                                  SAXParseExceptionFilter aFilter ) throws ODFValidatorException
    {
        ODFStreamValidator aStreamValidator = 
            new ODFStreamValidator( aInputStream, aBaseURI, m_nLogLevel, eMode, m_aVersion, aFilter, this );

        boolean result=aStreamValidator.validate(aOut);
        m_aGenerator=aStreamValidator.getGenerator();

        return result;
    }

    public Validator getManifestValidator(PrintStream aOut, OdfVersion aVersion) throws ODFValidatorException
    {
        return getValidatorForSchema( aOut, getSchemaFileName(Configuration.MANIFEST_SCHEMA,aVersion));
    }
    
    public Validator getValidator(PrintStream aOut, OdfVersion aVersion) throws ODFValidatorException
    {
        return getValidatorForSchema( aOut, getSchemaFileName(Configuration.SCHEMA,aVersion));
    }
    
    public Validator getStrictValidator(PrintStream aOut, OdfVersion aVersion) throws ODFValidatorException
    {
        return getValidatorForSchema( aOut, getSchemaFileName(Configuration.STRICT_SCHEMA,aVersion));
    }
    
    public Validator getMathMLValidator(PrintStream aOut, OdfVersion aVersion) throws ODFValidatorException
    {
        return getValidatorForSchema( aOut, getSchemaFileName(Configuration.MATHML2_SCHEMA,aVersion));
    }
    
    public String getMathMLDTDSystemId( OdfVersion aVersion ) throws ODFValidatorException
    {
        String aDTD = null;
        if( m_bUseMathDTD )
        {
            Configuration aConfig = 
                m_aConfig != null ? m_aConfig : getConfiguration( aVersion );

            aDTD = aConfig.getProperty(Configuration.MATHML_SCHEMA);
            if( m_aConfig == null && aDTD != null && aDTD.length() > 0  )
                aDTD = InternalResources.createInternalResourceIdentifier(aDTD);
        }

        return aDTD;
    }
    
    public Validator getDSigValidator(PrintStream aOut, OdfVersion aVersion) throws ODFValidatorException
    {
        return getValidatorForSchema( aOut, getSchemaFileName(Configuration.DSIG_SCHEMA,aVersion));
    }

    public void resetValidatorProvider()
    {
        m_aRNGSchemaFactory = null;
        m_aXSDSchemaFactory = null;
        m_aValidatorMap = null;
    }

    private String getSchemaFileName( String aConfigName, OdfVersion aVersion) throws ODFValidatorException
    {
        Configuration aConfig = 
            m_aConfig != null ? m_aConfig : getConfiguration( aVersion );

        String aFileName = aConfig.getProperty(aConfigName);
        if( m_aConfig == null && aFileName != null && aFileName.length() > 0  )
            aFileName = InternalResources.createInternalResourceIdentifier(aFileName);
        
        return aFileName;
    }

    private Configuration getConfiguration( OdfVersion aVersion ) throws ODFValidatorException
    {
        if( m_aConfigurationMap == null )
            m_aConfigurationMap = new HashMap<OdfVersion,Configuration>();
        
        Configuration aConfig = m_aConfigurationMap.get(aVersion);
        if( aConfig == null)
        {
            String aConfigName = null;
            if( aVersion == null || aVersion==OdfVersion.V1_2 )
                aConfigName = "/config/v12.properties";
            else if( aVersion==OdfVersion.V1_1 )
                aConfigName = "/config/v11.properties";
            else if( aVersion==OdfVersion.V1_0 )
                aConfigName = "/config/v10.properties";
            else
                throw new ODFValidatorException( "unsupported ODF version: ".concat(aVersion.toString()) );
            
            InputStream aInStream = getClass().getResourceAsStream(aConfigName);
            if( aInStream == null )
                throw new ODFValidatorException( "Internal configuration file is missing: ".concat(aConfigName));

            aConfig = new Configuration();
            try
            {
                aConfig.loadFromXML( aInStream );
                aInStream.close();
            }
            catch( IOException e )
            {
                throw new ODFValidatorException( e );
            }
            
            m_aConfigurationMap.put( aVersion, aConfig );
        }

        return aConfig;
    }
    
    private Validator getValidatorForSchema( PrintStream aOut, String aSchemaFileName ) throws ODFValidatorException
    {
        if( m_aValidatorMap == null )
            m_aValidatorMap = new HashMap<String,Validator>();
        
        Validator aValidator = m_aValidatorMap.get(aSchemaFileName);
        if( aValidator == null )
        {
            aValidator = createValidator( aOut, aSchemaFileName );
            m_aValidatorMap.put( aSchemaFileName, aValidator);
        }
        
        return aValidator;
    }
    
    private Validator createValidator(PrintStream aOut, String aSchemaFileName ) throws ODFValidatorException 
    {
        Logger aLogger = new Logger(aSchemaFileName, "", aOut, m_nLogLevel);
        
        if( aSchemaFileName == null || aSchemaFileName.length() == 0 )
            return null;
        
        String aSchemaLanguage = 
                aSchemaFileName.endsWith("xsd") ? XMLConstants.W3C_XML_SCHEMA_NS_URI
                                                : XMLConstants.RELAXNG_NS_URI;
        SchemaFactory aSchemaFactory = getSchemaFactory( aSchemaLanguage );
        SchemaErrorHandler aErrorHandler = new SchemaErrorHandler( aLogger, null );
        aSchemaFactory.setErrorHandler( aErrorHandler );
        aSchemaFactory.setResourceResolver(new SchemaResourceResolver(aLogger,aSchemaFileName));
        Schema aSchema = null;

        StreamSource aSource = null;
        if( InternalResources.isInternalResourceIdentifer(aSchemaFileName) )
        {
            String aPath = InternalResources.getResourcePath(aSchemaFileName);
            InputStream aInStream = getClass().getResourceAsStream(aPath);
            if( aInStream == null )
                throw new ODFValidatorException( "Internal schema file is missing: ".concat(aPath));

            aSource = new StreamSource(aInStream,aSchemaFileName);
        }
        else
        {
            File aFile=new File(aSchemaFileName);
            if ( ! aFile.exists() ) {
                aLogger.logFatalError( aSchemaFileName+": file does not exist" );
                return null;
            }
            aSource = new StreamSource(aFile);
        }

        boolean bError = false; // Workaround: MSV seems not to call error handler
        try
        {
            aSchema = aSchemaFactory.newSchema( aSource );
        }
        catch( org.xml.sax.SAXParseException e )
        {
            aLogger.logFatalError( e );
        }
        catch( org.xml.sax.SAXException e )
        {
            aLogger.logFatalError( e.getMessage() );
        }
        
        if( aLogger.hasError() )
        {
            throw new ODFValidatorException( aSchemaFileName, "", "Schema has validation errors.");
        }

        aLogger.logInfo( "parsed." , false);
        return aSchema.newValidator();
    }

    private SchemaFactory getSchemaFactory( String aSchemaLanguage ) throws ODFValidatorException
    {
        boolean bRNG = aSchemaLanguage.equals(XMLConstants.RELAXNG_NS_URI);
        SchemaFactory aSchemaFactory = bRNG ? m_aRNGSchemaFactory
                                            : m_aXSDSchemaFactory;
        if( aSchemaFactory == null )
        {

            try
            {
                aSchemaFactory = SchemaFactory.newInstance( aSchemaLanguage );
            }
            catch( IllegalArgumentException e ) 
            {
                throw new ODFValidatorException( (bRNG ? "Relax-NG" : "W3C XML Schema") + " support is not installed: " + e.getMessage() );
            }
            if( bRNG )
                m_aRNGSchemaFactory = aSchemaFactory;
            else
                m_aXSDSchemaFactory = aSchemaFactory;
        }
        
        return aSchemaFactory;
    }

    /**
     * get Generator from last validateFile or validateStream call
     */
    public String getGenerator() {
        return m_aGenerator;
    }

  
}
