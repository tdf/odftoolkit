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

package org.openoffice.odf.codegen;

import java.io.File;
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
    private IFunctionSupplier FunctionSupplier;
    
    public Context( IFunctionSupplier functionSupplier )
    {
        Environment = new HashMap< String, Stack< String > >();
        CurrentAttribute = new Stack< Attribute >();
        CurrentElement = new Stack< Element >();
        CurrentFile = new Stack< PrintWriter >();
        FunctionSupplier = functionSupplier;
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
    }
    
    public void pushElement( Element element )
    {
        CurrentElement.push(element);
        
        pushVariable( "elementname", element.getName() );
        pushVariable( "elementqname", element.getQName() );
        pushVariable( "elementstylefamily", element.getStyleFamily() );
    }
    
    public void popElement()
    {
        popVariable( "elementname" );
        popVariable( "elementqname" );
        popVariable( "elementstylefamily" );
        CurrentElement.pop();
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
