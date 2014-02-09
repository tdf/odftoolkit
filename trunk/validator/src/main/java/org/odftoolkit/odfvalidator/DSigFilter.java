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

class DSigFilter extends NamespaceFilter {

    private static final String OOO_DSIG_NAMESPACE_URI = "http://openoffice.org/2004/documentsignatures";
    private static final String DSIG_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:digitalsignature:1.0";
        
    private Logger m_aLogger;
    
    /** Creates a new instance of KnownIssueFilter */
    DSigFilter( Logger aLogger ) {
        m_aLogger = aLogger;
    }

    String adaptNamespaceUri( String aUri, String aPrefix)
    {
        String aNewUri = null;
        if( aUri.equals(OOO_DSIG_NAMESPACE_URI) )
            aNewUri = DSIG_NAMESPACE_URI;
        
        return aNewUri;
    }

    void namespaceUriAdapted( String aUri, String aNewUri )
    {
        String aMsg = "Adapting dsig namspace'" + aUri + "'";
        m_aLogger.logInfo( aMsg , false);
    }


}
