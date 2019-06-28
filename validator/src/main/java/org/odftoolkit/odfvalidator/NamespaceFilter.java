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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

abstract class NamespaceFilter extends XMLFilterImpl {

    private boolean m_bFilterNamespaceUri = false;

    /** Creates a new instance of NamespaceFilter */
    NamespaceFilter() {
    }

    abstract String adaptNamespaceUri(String aUri, String aPrefix);
    abstract void namespaceUriAdapted(String aUri, String aNewUri);


    public void endElement(String aUri, String aLocalName, String aQName) throws SAXException {
        if (m_bFilterNamespaceUri)
        {
            String aNewUri = adaptNamespaceUri( aUri , aQName );

            if( aNewUri != null )
                aUri = aNewUri;
        }

        super.endElement(aUri, aLocalName, aQName);
    }


    public void startElement(String aUri, String aLocalName, String aQName, Attributes aAtts) throws SAXException {
        if (m_bFilterNamespaceUri)
        {
            String aNewUri = adaptNamespaceUri( aUri , aQName);

            if( aNewUri != null )
                aUri = aNewUri;

            Attributes aOldAtts = aAtts;
            AttributesImpl aNewAtts = null;
            for (int i = 0; i < aOldAtts.getLength(); ++i)
            {
                aNewUri = adaptNamespaceUri( aOldAtts.getURI(i) , aQName);
                if (aNewUri != null)
                {
                    if (aNewAtts == null)
                    {
                        aNewAtts = new AttributesImpl(aOldAtts);
                        aAtts = aNewAtts;
                    }
                    aNewAtts.setURI(i,aNewUri);
                }
            }
        }
        super.startElement(aUri, aLocalName, aQName, aAtts);
    }



    public void startPrefixMapping(String aPrefix, String aUri) throws SAXException {
        String aNewUri = adaptNamespaceUri( aUri, aPrefix);
        if (aNewUri != null)
        {
            m_bFilterNamespaceUri = true;
            namespaceUriAdapted( aUri, aNewUri );
        }
        super.startPrefixMapping(aPrefix, aUri);
    }

}
