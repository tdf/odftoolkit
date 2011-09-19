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

import java.util.HashSet;
import java.util.Vector;
import org.openoffice.odf.dom.OdfNamespace;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

class AlienFilter extends XMLFilterImpl {

    private static final String OFFICE_NAMESPACE_URI = OdfNamespace.OFFICE.toString();
    
    private static final String PROCESS_CONTENT = "process-content";
    private static final String TRUE_STRING = "true";
    
    private static HashSet<String> m_aODFNamespaceSet = null;
    private Vector<Boolean> m_aAlienElements = null;

    private Logger m_aLogger;

    /** Creates a new instance of NamespaceFilter */
    AlienFilter( Logger aLogger ) {
        m_aLogger = aLogger;
        m_aAlienElements = new Vector<Boolean>();
        
        m_aODFNamespaceSet = new HashSet<String>();
        m_aODFNamespaceSet.add( OdfNamespace.OFFICE.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.STYLE.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.TEXT.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.TABLE.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.DRAW.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.FO.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.DC.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.META.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.NUMBER.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.SVG.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.CHART.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.DR3D.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.FORM.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.PRESENTATION.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.SMIL.toString() );
        m_aODFNamespaceSet.add( "urn:oasis:names:tc:opendocument:xmlns:config:1.0" );
        m_aODFNamespaceSet.add( OdfNamespace.SCRIPT.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.XLINK.toString() );
        m_aODFNamespaceSet.add( OdfNamespace.XFORMS.toString() );
    }



    @Override
    public void endElement(String aUri, String aLocalName, String aQName) throws SAXException {
        if( isAlienNamespace(aUri) )
        {
            m_aAlienElements.removeElementAt( m_aAlienElements.size()-1 );
        }
        else
        {
            boolean bProcessContent = m_aAlienElements.isEmpty() ? true : m_aAlienElements.lastElement();
            if( bProcessContent )
                super.endElement(aUri,aLocalName,aQName);
        }
    }


    @Override
    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAtts) throws SAXException {
        Boolean bProcessContent = m_aAlienElements.isEmpty() ? true : m_aAlienElements.lastElement();
        
        if( isAlienNamespace(aUri) )
        {
            if( bProcessContent )
            {
                String aProcessContentValue = aAtts.getValue( OFFICE_NAMESPACE_URI, PROCESS_CONTENT );
                bProcessContent = aProcessContentValue == null || aProcessContentValue.equals(TRUE_STRING);
            }
            m_aAlienElements.addElement( bProcessContent );
            m_aLogger.logInfo( String.format("element <%s> ignored, content is %s.",aQName,bProcessContent?"processed":"not processed"), false);            
        }
        else if( bProcessContent )
        {
            Attributes aOldAtts = aAtts;
            AttributesImpl aNewAtts = null;
            int i = aOldAtts.getLength();
            while( i>0 ) {
                --i;
                if ( isAlienNamespace( aOldAtts.getURI(i) ) ) {
                    if (aNewAtts == null) {
                        aNewAtts = new AttributesImpl(aOldAtts);
                        aAtts = aNewAtts;
                    }
                    m_aLogger.logInfo( String.format("attribute '%s' of element <%s> ignored.",aAtts.getQName(i),aQName), false);            
                    aNewAtts.removeAttribute(i);
                }
            }
            super.startElement(aUri, aLocalName, aQName, aAtts);
        }
    }
    
    

    private boolean isAlienNamespace( String aUri )
    {
        return !m_aODFNamespaceSet.contains(aUri);
    }

    public void characters(char[] aChars, int nStart, int nLength) throws SAXException {
        boolean bProcessContent = m_aAlienElements.isEmpty() ? true : m_aAlienElements.lastElement();
        if( bProcessContent )
            super.characters(aChars, nStart, nLength);
    }
    
    
}
