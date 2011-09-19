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

package org.odftoolkit.odfvalidator;

import java.io.IOException;
import java.io.StringReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


class ManifestFilter extends NamespaceFilter {
    
    private static final String OOO_MANIFEST_PUBLIC_ID = "-//OpenOffice.org//DTD Manifest 1.0//EN";
    private static final String OOO_MANIFEST_NAMESPACE_URI = "http://openoffice.org/2001/manifest";
    private static final String MANIFEST_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";
    
    private static final String FILE_ENTRY = "file-entry";
    private static final String FULL_PATH = "full-path";
    private static final String MEDIA_TYPE = "media-type";
    
    private Logger m_aLogger;
    
    private ManifestListener m_aManifestListener = null;
    private ManifestEntryListener m_aManifestEntryListener = null;
    
    /** Creates a new instance of KnownIssueFilter */
    ManifestFilter( Logger aLogger, ManifestListener aManifestListener, ManifestEntryListener aManifestEntryListener ) {
        m_aLogger = aLogger;
        m_aManifestListener = aManifestListener;
        m_aManifestEntryListener = aManifestEntryListener;
    }

    @Override
    public InputSource resolveEntity(String aPublicId, String aSystemId) throws SAXException, IOException {
        // Ignore the external OOo Manifest DTD which was errornously included
        // in early OpenDocument files.
        
        if( aPublicId.equals(OOO_MANIFEST_PUBLIC_ID) )
        {
            String aMsg = "Ignoring doctype definition '" + OOO_MANIFEST_PUBLIC_ID + "' (has been stored by old OOo versions)";
            m_aLogger.logInfo( aMsg , false);
            return new InputSource(new StringReader(""));
        }
        else
            return super.resolveEntity( aPublicId, aSystemId );
    }

    String adaptNamespaceUri( String aUri, String aPrefix)
    {
        String aNewUri = null;
        if( aUri.equals(OOO_MANIFEST_NAMESPACE_URI) )
            aNewUri = MANIFEST_NAMESPACE_URI;
        
        return aNewUri;
    }

    void namespaceUriAdapted( String aUri, String aNewUri )
    {
        String aMsg = "Adapting OpenOffice.org namspace'" + aUri + "' (has been stored by old OOo versions)";
        m_aLogger.logInfo( aMsg , false);
    }

    @Override
    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAtts) throws SAXException {
        super.startElement(aUri, aLocalName, aQName, aAtts);
        if( (aUri.equals(MANIFEST_NAMESPACE_URI) || aUri.equals(OOO_MANIFEST_NAMESPACE_URI)) && aLocalName.equals(FILE_ENTRY))
        {
            String aFullPath = aAtts.getValue(aUri,FULL_PATH);
            String aMediaType = aAtts.getValue(aUri,MEDIA_TYPE);
            if( aFullPath != null )
            {
                if( aFullPath.equals("/") )
                {
                    if( m_aManifestListener != null )
                        m_aManifestListener.setMediaType( aMediaType );
                }
                else
                {
                    if( m_aManifestEntryListener != null )
                        m_aManifestEntryListener.foundManifestEntry( new ManifestEntry(aFullPath,aMediaType) );
                }
            }
        }
    }
}
