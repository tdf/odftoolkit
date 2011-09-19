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

package odfvalidator;

import org.openoffice.odf.dom.OdfNamespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

class MetaFilter extends XMLFilterImpl {

    private boolean m_bInGenerator = false;
    private String m_aGenerator = "";
    
    private static final String META_NAMESPACE_URI = OdfNamespace.META.toString();
    private static final String GENERATOR = "generator";

    private Logger m_aLogger;
    private MetaInformationListener m_aMetaListener;
    
    
    /** Creates a new instance of MetaFilter */
    MetaFilter( Logger aLogger, MetaInformationListener aMetaListener ) {
        m_aLogger = aLogger;
        m_aMetaListener = aMetaListener;
    }
    
    public void characters(char[] aChars, int nStart, int nLength) throws SAXException {
        super.characters(aChars, nStart, nLength);
        
        if( m_bInGenerator )
        {
            m_aGenerator += new String( aChars, nStart, nLength );
        }
    }

    public void endElement(String aUri, String aLocalName, String aQName) throws SAXException {
        super.endElement(aUri, aLocalName, aQName);
        
        if( aUri.equals(META_NAMESPACE_URI) && aLocalName.equals(GENERATOR) )
        {
            m_aGenerator = m_aGenerator.trim();
            m_aLogger.logInfo( "Generator: " + m_aGenerator , false);
            m_bInGenerator = false;
            if( m_aMetaListener!=null ) {
                m_aMetaListener.setGenerator( m_aGenerator );
            }
        }
    }

    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAttributes) throws SAXException {
        super.startElement(aUri, aLocalName, aQName, aAttributes);
                
        if( aUri.equals(META_NAMESPACE_URI) && aLocalName.equals(GENERATOR) )
            m_bInGenerator = true;
    }
}
