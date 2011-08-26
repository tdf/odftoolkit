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

import java.util.Iterator;
import java.util.Vector;
import org.xml.sax.Attributes;

/**
 *
 * @author cl93746
 */
public class RngElement extends RngNode
{
    public static final String LOCAL_NAME = "element";
    private String Name;
    private RngHandler Handler;
    private Vector< AttributeEntry > Attributes;
    
    public class AttributeEntry
    {
        public RngAttribute Attribute;
        public boolean Optional;
        
        public AttributeEntry( RngAttribute attribute, boolean optional )
        {
            Attribute = attribute;
            Optional = optional;
        }
    }

    RngElement( RngHandler handler, Attributes attributes)
    {
        super(LOCAL_NAME);

        Handler = handler;
        Name = attributes.getValue("name");
    }

    public Iterator<AttributeEntry> getAttributes()
    {
        if( Attributes == null )
        {
            Attributes = new Vector< AttributeEntry >();
            getAttributes( Attributes, this, false );
        }
        return Attributes.iterator();
    }
    
    private void getAttributes( Vector< AttributeEntry > attributes, RngNode parent, boolean optional )
    {
        Iterator< RngNode > iter = parent.getChildren().iterator();
        while( iter.hasNext() )
        {
            RngNode child = iter.next();
            if( child.getLocalName().equals( RngAttribute.LOCAL_NAME ) )
            {
                attributes.add(new AttributeEntry( (RngAttribute)child, optional ) );
            }
            else if( child.getLocalName().equals( RngReference.LOCAL_NAME) )
            {
                Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)child).getName() );
                while( defineIter.hasNext() )
                    getAttributes( attributes, defineIter.next(), optional );
            }            
            else if( child.getLocalName().equals( RngAttribute.LOCAL_NAME ) )
            {
                continue; // skip nested attributes, should be an error?
            }
            else if( child.getLocalName().equals( RngElement.LOCAL_NAME ) )
            {
                continue; // skip nested elements, should be an error?
            }
            else
            {
                if( !optional )
                    optional = child.getLocalName().equals("optional");
                getAttributes( attributes, child, optional );
            }                
        }        
    }

    public Iterator<String> getNames()
    {
        Vector< String > names = new Vector< String >();
        if( (Name != null) && (Name.length() != 0) )
            names.add( Name );
        else
            getNames( names, this );
        return names.iterator();
    }
    
    private void getNames( Vector< String > names, RngNode parent )
    {
        Iterator< RngNode > iter = parent.getChildren().iterator();
        while( iter.hasNext() )
        {
            RngNode child = iter.next();
            if( child.getLocalName().equals( RngName.LOCAL_NAME ) )
            {
                names.add(((RngName)child).Value);
            }
            else if( child.getLocalName().equals( RngReference.LOCAL_NAME) )
            {
                Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)child).getName() );
                while( defineIter.hasNext() )
                    getNames( names, defineIter.next() );
            }
            else if( child.getLocalName().equals( RngAttribute.LOCAL_NAME ) )
            {
                continue; // skip names inside nested attributes
            }
            else if( child.getLocalName().equals( RngElement.LOCAL_NAME ) )
            {
                continue; // skip names inside nested elements
            }
            else
            {
                getNames( names, child );
            }                
        }
    }
}
