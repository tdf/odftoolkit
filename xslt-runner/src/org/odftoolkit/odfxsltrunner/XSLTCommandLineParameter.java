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

package org.odftoolkit.odfxsltrunner;

class XSLTCommandLineParameter implements XSLTParameter
{
    private String m_aName;
    private String m_aValue;

    XSLTCommandLineParameter( String aCmdLineParam )
    {
        String aParam = aCmdLineParam;
        if( aParam.startsWith("\"") && aParam.endsWith("\""))
            aParam = aCmdLineParam.substring(1, aCmdLineParam.length()-1);
        int nIndex = aParam.indexOf( '=' );
        if( nIndex != -1 )
        {
            m_aName = aParam.substring(0, nIndex );
            m_aValue = aParam.substring(nIndex+1);
        }
        else
        {
            m_aName = aParam;
            m_aValue = new String();
        }
    }

    public String getName()
    {
        return m_aName;
    }

    public String getValue()
    {
        return m_aValue;
    }
}
