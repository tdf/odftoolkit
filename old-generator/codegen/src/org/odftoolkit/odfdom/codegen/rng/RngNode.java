/*
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
 */

package org.odftoolkit.odfdom.codegen.rng;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author cl93746
 */
public class RngNode
{
    private String LocalName;
    private List< RngNode  > Children;
    
    public static final String XMLNS_A = "http://relaxng.org/ns/compatibility/annotations/1.0";
   
    public RngNode( String localName )
    {
        LocalName = localName;
        Children = new Vector< RngNode >();
    }
    
    public String getLocalName()
    {
        return LocalName;
    }
   
    public List< RngNode  > getChildren()
    {
        return Children;
    }
    
    public void addChild( RngNode child  )
    {
        Children.add(child);
    }         

    public static String getLocalName( String qName )
    {
        if( qName == null )
            return new String();

        int i = qName.indexOf(':');
        if( i == -1 )
            return qName;
        else
            return qName.substring(i+1);
    }

    public static String getPrefix(String qName)
    {
        int i = qName.indexOf(':');
        if( i < 2 )
            return new String();
        else
            return qName.substring(0,i);
    }
    
    public static String forceString(String str)
    {
        if( str == null)
            return new String();
        return str;
    }
}
