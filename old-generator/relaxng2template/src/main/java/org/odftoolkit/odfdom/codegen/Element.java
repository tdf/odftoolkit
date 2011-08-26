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

package org.odftoolkit.odfdom.codegen;

import java.util.Iterator;
import java.util.Vector;

import org.odftoolkit.odfdom.codegen.rng.RngAttribute;

/**
 *
 * @author cl93746
 */
public class Element
{
    private String Name;
    private String QName;
    private String StyleFamily;
   
    private Vector< Attribute > Attributes;
    
    //add for children element
    private Vector< Element > SubElements;    
    private Vector< Vector<Attribute> > SubAttributes;
    
    private Vector< String > DifferentName;
    
    public Element( String name, String qName, String styleElement, Element refElement )
    {
        Name = name;
        QName = qName;
        StyleFamily = styleElement;
        Attributes = new Vector< Attribute >();
        
        //add for children element
        SubElements =  new Vector< Element >();
		SubAttributes = new Vector<Vector<Attribute>>();
        DifferentName = new Vector <String> ();
        if( refElement != null )
        {
            Iterator< Attribute > iter = refElement.Attributes.iterator();
            while( iter.hasNext() )
                Attributes.add(iter.next());
        }
    }

    public String getStyleFamily()
    {
        return StyleFamily;
    }

    public String getQName()
    {
        return QName;
    }
    
    public String getName()
    {
        return Name;
    }

    public void addAttribute( Attribute newAttr )
    {
        Attribute mergeAttr = getAttribute(newAttr.getQName());
        if( mergeAttr == null )
        {
            Attributes.add(newAttr);
        }
        else
        {            
        	Iterator< String > iter = newAttr.getValues();
            while( iter.hasNext() )
            {
            	mergeAttr.addValue(iter.next());
            }
            if( !mergeAttr.getValueType().equals(newAttr.getValueType()) || !mergeAttr.getConversionType().equals(newAttr.getConversionType()) )
            {
                mergeAttr.setValueType( "String" );
                mergeAttr.setConversionType( "String" );
                System.err.println("warning: merging attribute " + newAttr.getName() + ", reverting to type string" );
            }
        }
    }
    
    public Attribute getAttribute( String name )
    {
        for( int i = 0; i < Attributes.size(); i++ )
        {
            if( Attributes.get(i).getQName().equals( name ) )
                return Attributes.get(i);
        }
        return null;
    }
    
    public void removeAttribute( String name )
    {
        for( int i = 0; i < Attributes.size(); i++ )
        {
            if( Attributes.get(i).getQName().equals( name ) )
            {
                Attributes.remove(i);
                break;
            }
        }
    }

    public Iterator<Attribute> getAttributes()
    {
        return Attributes.iterator();
    }
    
    //add for children element
    public void addSubElement( Element newSub )
    {
 
    	SubElements.add(newSub);
    }
    
    public Element getSubElement( String name )
    {
        
    	for( int i = 0; i < SubElements.size(); i++ )
        {
    		if( SubElements.get(i).getQName().equals( name ) )
                return SubElements.get(i);
        }
    	return null;
    }
    
    public void removeSubElement( String name )
    {
        for( int i = 0; i < SubElements.size(); i++ )
        {
            if( SubElements.get(i).getQName().equals( name ) )
            {
            	SubElements.remove(i);
                break;
            }
        }
    }

    public Vector< Element > getSubElements()
    { 
      	return SubElements;
    }
    //
    
    public Vector< Vector<Attribute>> getSubAttributes()
    { 
      	return SubAttributes;
    }
    
    public void addSubAttribute( Vector<Attribute> newAttrs )
    {
                   
        SubAttributes.add(newAttrs);
           
    }

    public Vector< String> getDifferentName()
    { 
      	return DifferentName;
    }
    
    public void addDifferentName( String  diff )
    {
                   
        DifferentName.add(diff);
           
    }
}
