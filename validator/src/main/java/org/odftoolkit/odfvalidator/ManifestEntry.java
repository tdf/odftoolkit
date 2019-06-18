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

public class ManifestEntry
{
    private String m_aFullPath;
    private String m_aMediaType;

    ManifestEntry(String aFullPath, String aMediaType)
    {
        m_aFullPath = aFullPath;
        m_aMediaType = aMediaType;
    }

    String getFullPath()
    {
        return m_aFullPath;
    }

    String getMediaType()
    {
        return m_aMediaType;
    }

    boolean isOpenDocumentMediaType()
    {
        return m_aMediaType!=null && isOpenDocumentMediaType(m_aMediaType);
    }

    static boolean isOpenDocumentMediaType( String aMediaType )
    {
        if( aMediaType.length() >14 && aMediaType.substring(12,14).equals("x-") )
        {
            String aNewMediaType = aMediaType.substring(0,12) + aMediaType.substring(14);
            aMediaType = aNewMediaType;
        }

        return aMediaType.equals(ODFMediaTypes.TEXT_MEDIA_TYPE) ||
                aMediaType.equals(ODFMediaTypes.GRAPHICS_MEDIA_TYPE) ||
                aMediaType.equals(ODFMediaTypes.SPREADSHEET_MEDIA_TYPE) ||
                aMediaType.equals(ODFMediaTypes.PRESENTATION_MEDIA_TYPE) ||
                aMediaType.equals(ODFMediaTypes.FORMULA_MEDIA_TYPE) ||
                aMediaType.equals(ODFMediaTypes.CHART_MEDIA_TYPE);
    }


}
