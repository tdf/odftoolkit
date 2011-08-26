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

package org.openoffice.odf.codegen.rng;

import java.util.Iterator;
import java.util.Vector;
import org.openoffice.odf.codegen.Config.DataTypeConfig;
import org.xml.sax.Attributes;

/**
 *
 * @author cl93746
 */
public class RngAttribute extends RngNode
{
    public final static String TYPE_ENUM = "enum";
    public final static String LOCAL_NAME = "attribute";

    private String Name;
    private String DefaultValue;
    private RngHandler Handler;
    private String Type;
    private Vector< String > Values;
    
    RngAttribute( RngHandler handler, Attributes attributes)
    {
        super(LOCAL_NAME);

        Name = forceString(attributes.getValue("name"));
        DefaultValue = forceString(attributes.getValue(XMLNS_A, "defaultValue"));
        Handler = handler;
    }

    public String getDefaultValue()
    {
        return DefaultValue;
    }
    
    public String getType()
    {
        if( Type == null )
        {
            getType( this );
                        
            if( Type == null )
            {
                if( (Values != null) && !Values.isEmpty())
                {
                    Type = TYPE_ENUM;
                }
                else
                {                
                    Type = new String();
                }
            }
        }
        
        return Type;
    }
    
    public Iterator< String > getValues()
    {        
        if( Values == null )
        {
            Vector< String > temp = new Vector< String >();
            return temp.iterator();
        }
        
        return Values.iterator();
    }
    
    private boolean getType( RngNode parent )
    {
        Iterator< RngNode > iter = parent.getChildren().iterator();
        while( iter.hasNext() )
        {
            RngNode child = iter.next();
            if( child.getLocalName().equals( RngValue.LOCAL_NAME) )
            {
                if( Values == null )
                    Values = new Vector< String >();
                
                Values.add(((RngValue)child).Value);
            }
            else if( child.getLocalName().equals( RngReference.LOCAL_NAME) )
            {
                // if this reference name is equal to a configured data-type, stop here
                String defName = ((RngReference)child).getName();
                if( Type == null )
                {
                    DataTypeConfig config = Handler.getConfiguration().getDataTypeConfiguration(defName);
                    if( config != null )
                        Type = defName;
                }

                Iterator< RngDefine > defineIter = Handler.getDefines( defName );
                while( defineIter.hasNext() )
                {
                    if( getType( defineIter.next() ) )
                        return true;
                }
            }            
            else if( child.getLocalName().equals( RngData.LOCAL_NAME ) )
            {
                if( Type == null )
                    Type = ((RngData)child).getType();
                return true;
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
                if( getType( child ) )
                    return true;
            }                
        }        
        return false;
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
