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

public enum OdfVersion {

    V1_0( "1.0" ), V1_1( "1.1" ), V1_2( "1.2" );

    private String m_aValue;

    OdfVersion( String _aValue )
    {
        m_aValue = _aValue;
    }

    @Override
    public String toString()
    {
        return m_aValue;
    }

    public static OdfVersion valueOf( String _aString, boolean _bAttrValue )
    {
        if( _aString == null )
            return _bAttrValue ? V1_1 : null;

        for( OdfVersion aIter : values() )
        {
            if( _aString.equals( aIter.toString() ) )
            {
                return aIter;
            }
        }
        return _bAttrValue ? V1_1 : null;
    }

}
