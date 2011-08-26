/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author Christian
 */
public class Context
{
    private HashMap< String, Stack< String > > Environment;
    private Stack< Attribute > CurrentAttribute;
    private Stack< Element > CurrentElement;
    private Stack< PrintWriter > CurrentFile;
    private Stack< AttributeSet > CurrentAttrSet;
    private IFunctionSupplier FunctionSupplier;
    private Vector<Attribute> CurrentAttributeGroup;
    private String DifferentName;
    
	public String getDifferentName() {
		return DifferentName;
	}

	public void setDifferentName(String differentName) {
		DifferentName = differentName;
	}

    public Context( IFunctionSupplier functionSupplier )
    {
        Environment = new HashMap< String, Stack< String > >();
        CurrentAttribute = new Stack< Attribute >();
        CurrentAttrSet = new Stack< AttributeSet >();
        CurrentElement = new Stack< Element >();
        CurrentFile = new Stack< PrintWriter >();
        FunctionSupplier = functionSupplier;
        CurrentAttributeGroup = new Vector<Attribute>();
    }
    
    public String getVariable( String name )
    {
        Stack< String > stack = Environment.get(name);
        if( (stack != null) && !stack.isEmpty() )
            return stack.lastElement();
        
        return null;
    }
    
    public Attribute getCurrentAttribute()
    {
        if( !CurrentAttribute.isEmpty() )
            return CurrentAttribute.lastElement();
        else
            return null;
    }

    public AttributeSet getCurrentAttributeSet()
    {
        if( !CurrentAttrSet.isEmpty() )
            return CurrentAttrSet.lastElement();
        else
            return null;
    }
    
    public Element getCurrentElement()
    {
        if( !CurrentElement.isEmpty() )
            return CurrentElement.lastElement();
        else
            return null;
    }
    
    public PrintWriter getCurrentFile()
    {
        if( !CurrentFile.isEmpty() )
            return CurrentFile.lastElement();
        else
            return null;
    }
    public Vector<Attribute> getCurrentAttributeGroup()
    {

            return CurrentAttributeGroup;

    }    
    public void setCurrentAttributeGroup(Vector<Attribute> groupAttr)
    {

            CurrentAttributeGroup = groupAttr;

    }
    
    public void setVariable( String name, String value )
    {
        Stack< String > stack = Environment.get(name);
        if( stack == null )
        {
            stack = new Stack< String >();
            Environment.put( name, stack );
        }
        if( !stack.isEmpty() )
            stack.pop();
        stack.push(value);
    }
    
    public void pushVariable( String name, String value )
    {
        Stack< String > stack = Environment.get(name);
        if( stack == null )
        {
            stack = new Stack< String >();
            Environment.put( name, stack );
        }
        stack.push(value);
    }
    
    public void popVariable( String name )
    {
        Stack< String > stack = Environment.get(name);
        if( stack != null )
        {
            stack.pop();
        }
    }
    
    public void pushAttribute( Attribute attr )
    {
        CurrentAttribute.push(attr);
        pushVariable( "attributename", attr.getName() );
        pushVariable( "attributeqname", attr.getQName() );        
        pushVariable( "valuetype", attr.getValueType() );
        pushVariable( "conversiontype", attr.getConversionType() );
        pushVariable( "defaultvalue", attr.getDefaultValue() );
        if(attr.getOwnerElement() != null){
        	pushVariable( "ownerelementname", attr.getOwnerElement().getName());
        	pushVariable( "ownerelementqname", attr.getOwnerElement().getQName());
        }
        else{
        	pushVariable( "ownerelementname", "");
        	pushVariable( "ownerelementqname", "");
        }
        pushVariable( "hasmultivaluesets", attr.isMultiValueSets()? "true":"false");
    	pushVariable( "hasmultidefaultvalues", attr.isMultiDefaultValues()?"true":"false");
        pushVariable( "optionalattribute", attr.isOptional() ? "true" : "false");        
    }
    
    public void popAttribute()
    {
        CurrentAttribute.pop();
        popVariable( "attributename" );
        popVariable( "attributeqname" );
        popVariable( "valuetype" );
        popVariable( "conversiontype" );
        popVariable( "defaultvalue" );    
        popVariable( "ownerelementname" );
        popVariable( "ownerelementqname" );
        popVariable( "hasmultivaluesets" );
    	popVariable( "hasmultidefaultvalues" );
        popVariable( "optionalattribute" );
    }
    
    public void pushElement( Element element )
    {
        CurrentElement.push(element);
        pushVariable( "diffqname", getDifferentName());
        pushVariable( "elementname", element.getName() );
        pushVariable( "elementqname", element.getQName() );
        pushVariable( "elementstylefamily", element.getStyleFamily() );
    }
    
    public void popElement()
    {
        popVariable( "diffqname" );
        popVariable( "elementname" );
        popVariable( "elementqname" );
        popVariable( "elementstylefamily" );
        CurrentElement.pop();
    }        
    
    public void pushAttributeSet( AttributeSet attrSet)
    {
    	CurrentAttrSet.push(attrSet);
    	pushVariable( "attributename", attrSet.getName());
    	pushVariable( "attributeqname", attrSet.getQName());
    	pushVariable( "valuetype", attrSet.getValueType());
    	pushVariable( "conversiontype", attrSet.getConversionType());
    	pushVariable( "hasmultivaluesets", attrSet.isMultiValueSets()? "true":"false");
    	pushVariable( "hasdefaultvalue", attrSet.isDefaultValue()?"true":"false");
    	pushVariable( "hasmultidefaultvalues", attrSet.isMultiDefaultValues()?"true":"false");
    }
    
    public void popAttributeSet()
    {
    	popVariable( "attributename" );
    	popVariable( "attributeqname" );
    	popVariable( "valuetype" );
    	popVariable( "conversiontype" );
    	popVariable( "hasmultivaluesets" );
    	popVariable( "hasdefaultvalue" );
    	popVariable( "hasmultidefaultvalues" );
    	CurrentAttrSet.pop();
    }

    public void pushFile( PrintWriter file )
    {
        CurrentFile.push(file);
    }
    
    public void popFile()
    {
        CurrentFile.pop();
    }

    String function(String func, Vector<String> params) throws IOException
    {
        return FunctionSupplier.function(func, params);
    }
}
