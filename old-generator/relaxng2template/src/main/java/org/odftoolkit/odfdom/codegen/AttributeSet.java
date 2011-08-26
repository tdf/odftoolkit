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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.odftoolkit.odfdom.codegen.rng.RngAttribute;
/**
 * this <code>AttributeSet</code> is used to construct the union sets
 * of values and default values of the <code>Attribute</code> which have
 * the same attribute name.
 * 
 * @author whw
 *
 */
public class AttributeSet {
	//the name of the attribute
	private String QName;
	private String Name;
	//all the attributes have the same name <code>Attributeqname</code>
	private Vector< Attribute > Attributes;
	//the union set of values
	private HashSet< String > Values;
	//the union set of default values
	private HashSet< String > DefaultValues;
    
    private String ValueType;
    private String ConversionType;
	private boolean hasMultiValueSets;
	private boolean hasMultiDefaultValues;
	private boolean hasDefaultValue;
	
	AttributeSet( String name,String qName, String valueType, String conversionType )
    {
		Name = name;
        QName = qName;
        ValueType = valueType;
        ConversionType = conversionType;
        hasMultiValueSets = false;
        hasMultiDefaultValues = false;
        hasDefaultValue = false;
        Attributes = new Vector<Attribute>();
    }
	
	public void addAttribute( Attribute newAttr )
	{
		if( newAttr.getQName().equals(QName))
        {
            Attributes.add(newAttr);
            //merge value sets if the newAttr is not boolean
            //because 'true' and 'false' should not be viewed as enum
            if(!newAttr.getValueType().equalsIgnoreCase("boolean"))
            	addValueSet(newAttr.getValues().iterator());
            if( !ValueType.equals(newAttr.getValueType()) || !ConversionType.equals(newAttr.getConversionType()) ){
            	//the contained attributes valuetype/conversiontype are different, this may due to the schema
//            	System.err.println("warning: merging AttributeSet " + newAttr.getName() + ", original(" + ValueType + ", " + ConversionType + 
//            			") new(" + newAttr.getValueType() + ", " + newAttr.getConversionType() + "), reverting from" + getValueType() + " to type string" );
            	ValueType = "String";
            	ConversionType = "String";
            	hasMultiValueSets = true;
            	
            }
            //merge default value
            addDefaultValue(newAttr.getDefaultValue());
        }
        else
        {            
        	System.err.println("warning:not expected: the attribute *" + newAttr.getQName() + "* should not be added to this attributeset with " + QName);
        }
		//enable multi value sets flag for each attribute
        if( hasMultiValueSets )
        	enableMultiValueSets(hasMultiValueSets);
        //enable multi default values flag for each attribute
        if( hasMultiDefaultValues)
        	enableMultiDefaultValues(hasMultiDefaultValues);
	}
	
	private Attribute getAttributeByElement( String elementQName )
    {
        for( int i = 0; i < Attributes.size(); i++ )
        {
            if( Attributes.get(i).getOwnerElement().getQName().equals( elementQName ) )
                return Attributes.get(i);
        }
        return null;
    }
	
	public Iterator<Attribute> getAttributes()
    {
        return Attributes.iterator();
    }

    private void addDefaultValue(String defaultValue)
    {
    	if( DefaultValues == null )
            DefaultValues = new HashSet< String >();
        
        if( !defaultValue.equals("") && !DefaultValues.contains(defaultValue) )
        	DefaultValues.add(defaultValue);
        if( DefaultValues.size() > 0 )
        	hasDefaultValue = true;
        if( DefaultValues.size() > 1 )
        	hasMultiDefaultValues = true;
    }
    
    private void addValueSet( Iterator<String> iter )
    {
    	boolean firstAdd = false;
    	if( Values == null )
    	{
            Values = new HashSet< String >();
            firstAdd = true;
    	}
        
        while( iter.hasNext() )
        {
        	String value = iter.next();
        	if( !Values.contains(value) ){
        		Values.add(value);
        		if(!firstAdd)
        			hasMultiValueSets = true;
        	}
        }
    }
    
    public String getValueType()
    {
        return ValueType;
    }
    
    public String getConversionType()
    {
        return ConversionType;
    }
    
    public HashSet< String > getValues()
    {
        if( Values != null )
        {
            return Values;
        }
        else
        {
        	HashSet< String > temp = new HashSet< String >();
            return temp;
        }
    }
    
    public HashSet< String > getDefaultValues()
    {
    	if( DefaultValues != null )
        {
            return DefaultValues;
        }
        else
        {
        	HashSet< String > temp = new HashSet< String >();
            return temp;
        }
    }

    public boolean isMultiValueSets()
    {
    	return hasMultiValueSets;
    }
    
    public boolean isMultiDefaultValues()
    {
    	return hasMultiDefaultValues;
    }
    
    public boolean isDefaultValue()
    {
    	return hasDefaultValue;
    }
    
    
    private void enableMultiValueSets(boolean enable)
    {
    	Iterator< Attribute > attrIter = getAttributes();
    	while(attrIter.hasNext())
    	{
    		Attribute attr = attrIter.next();
    		attr.enableMultiValueSets(enable);
    	}
    }
    
    private void enableMultiDefaultValues(boolean enable)
    {
    	Iterator< Attribute > attrIter = getAttributes();
    	while(attrIter.hasNext())
    	{
    		Attribute attr = attrIter.next();
    		attr.enableMultiDefaultValues(enable);
    	}
    }
  
    public String getQName()
    {
        return QName;
    }
    
    public String getName()
    {
    	return Name;
    }

    public int getValueCount()
    {
        return (Values == null) ? 0 : Values.size();
    }
    
    public int getDefaultValueCount()
    {
    	return (DefaultValues == null) ? 0 : DefaultValues.size();
    }
    
    public boolean hasValue( String value )
    {
        return (Values != null) && Values.contains(value);
    }

}
