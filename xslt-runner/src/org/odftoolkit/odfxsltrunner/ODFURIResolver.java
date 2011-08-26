/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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

package org.odftoolkit.odfxsltrunner;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import org.odftoolkit.odfdom.pkg.OdfPackage;

class ODFURIResolver implements URIResolver
{
    private Logger m_aLogger;
    private String m_aPackageBase;
    private String m_aFileEntryPath;
    private OdfPackage m_aPackage;


    /** Creates a new instance of ODFURIResolver
     *
     * @param aPackage the package in which the to be resolved URIs are included
     * @param aPackageBase the package base URI
     * @param aFileEntryPath the path of the file within the package in which the to be resolved URIs are included
     * @param aLogger the logger for error messages
     */
    public ODFURIResolver( OdfPackage aPackage, String aPackageBase, String aFileEntryPath, Logger aLogger )
    {
        m_aPackage = aPackage;
        m_aPackageBase = aPackageBase;
        m_aFileEntryPath = aFileEntryPath;
        m_aLogger = aLogger;
    }

    /** resolve an URI
     *
     * @param aHRef the URI to be resolved
     * @param aBase the base URI that is provied by the XSLT transformation
     */
    public Source resolve(String aHRef, String aBase) throws TransformerException
    {
        if( aBase.startsWith(m_aPackageBase) &&
            !aHRef.contains(":") && !aHRef.startsWith("/") )
        {
            if( aHRef.isEmpty())
            {
                try
                {
                    return new StreamSource(m_aPackage.getInputStream(m_aFileEntryPath), m_aPackageBase + '/' + m_aFileEntryPath );
                }
                catch (Exception ex)
                {
                    m_aLogger.logError(ex.getMessage());
                    return null;
                }
            }

            StringBuffer aHRefBuffer = new StringBuffer( aHRef );
            StringBuffer aFileEntryBuffer = new StringBuffer();
            if( m_aFileEntryPath.contains("/") )
                aFileEntryBuffer.append(m_aFileEntryPath.substring(0, m_aFileEntryPath.lastIndexOf('/')+1));

            do
            {
                if( aHRefBuffer.substring(0, 2).equals("./") )
                {
                    aHRefBuffer.delete(0, 2);
                }
                else if( aHRefBuffer.toString().equals(".") || aHRefBuffer.toString().equals("..")  )
                {
                    m_aLogger.logError("URIs resolving to directories cannot be resolved: " + aHRef );
                    return null;
                }
                else if( aHRefBuffer.substring(0, 3).equals("../") )
                {
                    aFileEntryBuffer.delete(0, 3);
                    if( aFileEntryBuffer.length()==0 )
                    {
                        aHRefBuffer.insert(0, "./");
                        try
                        {
                            URI aURI;
                            aURI = new URI(aBase);
                            aURI.resolve(aHRefBuffer.toString());
                            m_aLogger.logInfo( "Resolving " +  aHRef + " to " + aURI.toString() );
                            return new StreamSource( aURI.toString() );
                        }
                        catch (URISyntaxException ex)
                        {
                            m_aLogger.logError(ex.getMessage());
                            return null;
                        }
                    }
                    else
                    {
                        aFileEntryBuffer.delete(0, aFileEntryBuffer.indexOf("/"+1));
                    }
                }
                else
                {
                    aHRefBuffer.insert(0, aFileEntryBuffer.toString());
                    try
                    {
                        String aFileEntryPath = aHRefBuffer.toString();
                        m_aLogger.logInfo( "Resolving " +  aHRef + " to package file entry " + aFileEntryPath );
                        return new StreamSource(m_aPackage.getInputStream(aFileEntryPath), m_aPackageBase + '/' + aFileEntryPath);
                    }
                    catch (Exception ex)
                    {
                        m_aLogger.logError(ex.getMessage());
                        return null;
                    }
                }
            }
            while( true );
        }

        return null;
    }

}
