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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 *
 */
class MathML101Filter extends XMLFilterImpl {
//class MathML101Filter implements EntityResolver {
    
    private String m_aMathMLDTD;
    private static final String MATHML_NAMESPACE_URI = "http://www.w3.org/1998/Math/MathML";
    private static final String MATHML_PUBLIC_ID = "-//W3C//DTD MathML 1.01//EN";
    private static final String OOO_MATHML_PUBLIC_ID = "-//OpenOffice.org//DTD Modified W3C MathML 1.01//EN";

    private Logger m_aLogger;
    
    /** Creates a new instance of MathML101Filter */
    MathML101Filter( String aMathMLDTD,  Logger aLogger )  {
        m_aMathMLDTD = aMathMLDTD;
        m_aLogger = aLogger;
    }


    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAtts) throws SAXException {
        super.startElement(aUri, aLocalName, aQName, aAtts);
    }

    public void endElement(String aUri, String aLocalName, String aQName) throws SAXException {
        super.endElement(aUri, aLocalName, aQName);
    }

    public InputSource resolveEntity(String aPublicId, String aSystemId) throws SAXException, IOException {
        InputSource aRet = null;
        String aEntity = null;
        
        if( aPublicId != null &&
            (aPublicId.equals(MATHML_PUBLIC_ID) ||
            aPublicId.equals(OOO_MATHML_PUBLIC_ID)) )
        {
            aEntity = m_aMathMLDTD;
            if( InternalResources.isInternalResourceIdentifer(aEntity) )
            {
                String aPath = InternalResources.getResourcePath(aEntity);
                InputStream aInStream = getClass().getResourceAsStream(aPath);
                if( aInStream == null )
                {
                    m_aLogger.logFatalError("Missing internal schema file: ".concat(aPath));
                }
                else
                {
                    aRet = new InputSource( aInStream );
                    aRet.setPublicId(aPublicId);
                    aRet.setSystemId(aEntity);
                }
            }
        }
        else
        {
            aEntity = aSystemId;
        }

        String aMsg = "Reading doctype definition '" + aEntity + "'";
        m_aLogger.logInfo( aMsg , false);

        if( aRet == null )
            aRet = new InputSource( aEntity  );
        return aRet;
    }
    
}
