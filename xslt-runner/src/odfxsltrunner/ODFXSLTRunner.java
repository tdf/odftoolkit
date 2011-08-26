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

package odfxsltrunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.openoffice.odf.pkg.OdfPackage;
import org.openoffice.odf.pkg.manifest.OdfFileEntry;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Class for applying style sheets to ODF documents.
 */
public class ODFXSLTRunner {

    /**
     * Input file is a plain XML file.
     */
    public static final int INPUT_MODE_FILE = 0;
    
    /**
     * Input file is an ODF package. The style sheet is applied to the
     * specified sub file.
     */
    public static final int INPUT_MODE_PACKAGE = 1;
    
    /**
     * Output file is a plain XML or text file.
     */
    public static final int OUTPUT_MODE_FILE = 0;
    
    /**
     * Output is stdout.
     */
    public static final int OUTPUT_MODE_STDOUT = 1;
    
    /**
     * The transformation replaces the specified path within
     * the input file.
     */
    public static final int OUTPUT_MODE_REPLACE_INPUT_PACKAGE = 2;
    
    /**
     * The input package is copied and the result of the transformation
     * is stored in the specified path within the copied package.
     */
    public static final int OUTPUT_MODE_COPY_INPUT_PACKAGE = 3;
    
    /** 
     * The result of the transformation is stored in the specified path within
     * the output package.
     */
    public static final int OUTPUT_MODE_TEMPLATE_PACKAGE = 4;
    
    /**
     * Create new instance of ODFXSLTRunner.
     */
    public ODFXSLTRunner()
    {
    }
    
    /**
     * Apply a style sheeet.
     * 
     * @param aStyleSheet Path of the style sheet
     * @param aParams Parameters that are passed to the XSLT processor
     * @param aInputFile Path of the input file
     * @param aInputMode Input mode
     * @param aOutputFile Path of the output file
     * @param aOutputMode Output mode
     * @param aPathInPackage Path within the package. Default is "content.xml"
     * @param aLogger Logger object
     * 
     * @return true if an error occured.
     */
    public boolean runXSLT( String aStyleSheet, List<XSLTParameter> aParams,
                     String aInputFile, int aInputMode,
                     String aOutputFile, int aOutputMode,
                     String aPathInPackage, Logger aLogger )
    {
        return runXSLT( new File( aStyleSheet ), aParams,
                        new File( aInputFile), aInputMode,
                        aOutputFile != null ? new File(aOutputFile) : null, aOutputMode,
                        aPathInPackage, aLogger );
    }
    
    
    /**
     * Apply a style sheeet.
     * 
     * @param aStyleSheet Style sheet
     * @param aParams Parameters that are passed to the XSLT processor
     * @param aInputFile Input file
     * @param aInputMode Input mode
     * @param aOutputFile Output file
     * @param aOutputMode Output mode
     * @param aPathInPackage Path within the package. Default is "content.xml"
     * @param aLogger Logger object
     *
     * @return true if an error occured.
     */
    public boolean runXSLT( File aStyleSheetFile, List<XSLTParameter> aParams,
                     File aInputFile, int aInputMode,
                     File aOutputFile, int aOutputMode,
                     String aPathInPackage, Logger aLogger )
    {
        boolean bError = false;

        Source aInputSource = null;
        OdfPackage aInputPkg = null;
        String aMediaType ="text/xml";
        aLogger.setName( aInputFile.getAbsolutePath() );
        try
        {
            if( INPUT_MODE_FILE == aInputMode )
            {
                aInputSource = new StreamSource( aInputFile );
            }
            else
            {
                aInputPkg = OdfPackage.loadPackage( aInputFile );
                aLogger.setName( aInputFile.getAbsolutePath(), aPathInPackage );
                aInputSource = new StreamSource( aInputPkg.getInputStream(aPathInPackage));
                OdfFileEntry aFileEntry =  aInputPkg.getFileEntry(aPathInPackage);
                if( aFileEntry != null )
                    aMediaType = aFileEntry.getMediaType();
            }
        }
        catch( Exception e )
        {
            aLogger.logFatalError(e.getMessage());
            return true;
        }
        String aInputName = aLogger.getName();

        Result aOutputResult = null;
        OdfPackage aOutputPkg = null;
        OutputStream aOutputStream = null;
        aLogger.setName( aOutputFile != null ? aOutputFile.getAbsolutePath() : "(none)" );
        try
        {
            switch( aOutputMode )
            {
                case OUTPUT_MODE_FILE:
                    aOutputResult = new StreamResult( aOutputFile );
                    break;
                case OUTPUT_MODE_STDOUT:
                    aOutputResult = new StreamResult( System.out );
                    break;
                case OUTPUT_MODE_REPLACE_INPUT_PACKAGE:
                    aOutputPkg = aInputPkg;
                    aOutputFile = aInputFile;
                    break;
                case OUTPUT_MODE_COPY_INPUT_PACKAGE:
                    aOutputPkg = aInputPkg;
                    break;
                case OUTPUT_MODE_TEMPLATE_PACKAGE:
                    aOutputPkg = OdfPackage.loadPackage( aOutputFile );
                    break;
            }
            if( aOutputResult == null )
            {
                aLogger.setName( aOutputFile.getAbsolutePath(), aPathInPackage );                
                aOutputStream = 
                    aOutputPkg.insertOutputStream(aPathInPackage, aMediaType );
                aOutputResult = new StreamResult( aOutputStream );
            }
        }
        catch( Exception e )
        {
            aLogger.logFatalError(e.getMessage());
            return true;
        }
        String aOutputName = aLogger.getName();

        aLogger.setName( aStyleSheetFile.getAbsolutePath() );
        aLogger.logInfo( "Applying stylesheet to '" + aInputName + "'");
        bError = runXSLT( aStyleSheetFile, aParams, aInputSource, aOutputResult, aLogger );
        if( bError )
            return true;
        
        aLogger.setName( aOutputFile != null ? aOutputFile.getAbsolutePath() : "(none)" );
        try
        {
            aLogger.logInfo( "Storing transformation result to '" + aOutputName + "'");
            if( !bError && aOutputStream != null )
                aOutputStream.close();
            if( !bError && aOutputPkg != null )
                aOutputPkg.save(aOutputFile);
        }
        catch( Exception e )
        {
            aLogger.logFatalError(e.getMessage());
            return true;
        }
        
        return false;
    }
    
    
    private boolean runXSLT( File aStyleSheetFile, 
                     List<XSLTParameter> aParams,
                     Source aInputSource, Result aOutputTarget,
                     Logger aLogger )
    {
        InputStream aStyleSheetInputStream = null;
        try
        {
            aStyleSheetInputStream = new FileInputStream(aStyleSheetFile);
        }
        catch( FileNotFoundException e )
        {
            aLogger.logFatalError(e.getMessage());
            return true;
        }

        InputSource aStyleSheetInputSource = new InputSource(aStyleSheetInputStream);
        aStyleSheetInputSource.setSystemId(aStyleSheetFile.getAbsolutePath());
        
        XMLReader aXMLReader = null;
        try
        {
            aXMLReader = XMLReaderFactory.createXMLReader();
        }
        catch( SAXException e )
        {
            aLogger.logFatalError(e.getMessage());
            return true;            
        }
        
        aXMLReader.setErrorHandler(new SAXErrorHandler(aLogger));
        
        Source aSource = new SAXSource( aXMLReader, aStyleSheetInputSource );

        TransformerFactory aFactory = TransformerFactory.newInstance();
        ErrorListener aErrorListener = new TransformerErrorListener( aLogger );
        aFactory.setErrorListener(aErrorListener);
        
        try
        {
            Transformer aTransformer = aFactory.newTransformer(aSource);
            
            if( aParams != null )
            {
                Iterator<XSLTParameter> aIter = aParams.iterator();
                while( aIter.hasNext() )
                {
                    XSLTParameter aParam = aIter.next();
                    aTransformer.setParameter(aParam.getName(), aParam.getValue());
                    aLogger.logInfo("Using parameter: "+aParam.getName()+"="+aParam.getValue());
                }
            }
            aTransformer.setErrorListener(aErrorListener);
            aTransformer.transform(aInputSource, aOutputTarget);
        }
        catch( TransformerException e )
        {
//            aLogger.logFatalError(e);
            return true;
        }

        return false;
    }
}
