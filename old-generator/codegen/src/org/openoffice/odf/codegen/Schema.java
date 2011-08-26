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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.openoffice.odf.codegen.Config.AttributeConfig;
import org.openoffice.odf.codegen.Config.DataTypeConfig;
import org.openoffice.odf.codegen.Config.ElementConfig;
import org.openoffice.odf.codegen.rng.RngAttribute;
import org.openoffice.odf.codegen.rng.RngElement;
import org.openoffice.odf.codegen.rng.RngElement.AttributeEntry;
import org.openoffice.odf.codegen.rng.RngHandler;
import org.openoffice.odf.codegen.rng.RngNode;

/**
 *
 * @author cl93746
 */
public class Schema
{
    private HashMap< String, Element > BaseElements;
    private HashMap< String, Element > Elements;
    private RngHandler Handler;
    private Config Config;

    public Schema( Config config )
    {
        BaseElements = new HashMap< String, Element >();
        Elements = new HashMap< String, Element >();
        Config = config;
    }
    
    public boolean parseSchema( String path )
    {
        try
        {
            Handler = RngHandler.parse(path, Config);                           
            if( populateElements() )
            {
              if( renameAttributes() )
              {
                if( populateBaseElements() )
                {
                    return removeBaseAttributes();
                }
              }
            }
        }
        catch( Exception ex )
        {
            System.err.println("error: could not load " + path + ", " + ex.toString() );
            ex.printStackTrace();
        }
        return false;        
    }

    public Iterator<Element> getElements()
    {
        return Elements.values().iterator();
    }

    public Iterator<String> getDataTypes()
    {
        return Handler.getDataTypes();
    }

    public Element getBaseElement(String qName)
    {
        return BaseElements.get(qName);
    }

    Iterator<Element> getBaseElements()
    {
        return BaseElements.values().iterator();
    }

    Element getElement(String qName, boolean baseElement )
    {
        if( baseElement )
            return BaseElements.get(qName);
        else
            return Elements.get(qName);
    }

    private boolean populateBaseElements()
    {
        Iterator< ElementConfig > iter = Config.getElementConfigurations();
        while( iter.hasNext() )
        {
            ElementConfig config = iter.next();
            
            if( (config.Base == null) || (config.Base.length() == 0) || (config.Base.indexOf(':') == -1) )
                continue;
            
            Element compareElement = Elements.get( config.Name );
            if( compareElement == null )
            {
                System.err.println("error: Element <" + config.Name + "> from configuration not found in schema!");
                return false;
            }
            
            Element baseElement = BaseElements.get(config.Base);
            if( baseElement == null )
            {
                baseElement = new Element( RngAttribute.getLocalName(config.Base), config.Base, config.StyleFamily, compareElement );
                BaseElements.put( baseElement.getQName(), baseElement);            
            }
            else
            {           
                Vector< String > remove_attrs = new Vector<String>();

                // remove non matching attributes
                Iterator< Attribute > attrIter = baseElement.getAttributes();
                while( attrIter.hasNext() )
                {
                    Attribute attr = attrIter.next();
                    Attribute compareAttr = compareElement.getAttribute(attr.getQName());

                    boolean remove = compareAttr == null;

                    if( !remove )
                    {
                        // compare type
                        if( !attr.getValueType().equals( compareAttr.getValueType() ) )
                        {
                            remove = true;
                        }
                        else if( attr.getValueType().equals(RngAttribute.TYPE_ENUM))
                        {
                            // todo: do a sorted compare for better results!
                            // compare enum values
                            Iterator< String > i1 = attr.getValues();
                            Iterator< String > i2 = compareAttr.getValues();
                            while( i1.hasNext() && i2.hasNext() )
                            {
                                if( !i1.next().equals(i2.next()) )
                                {
                                    remove = true;
                                    break;
                                }
                            }

                            remove = remove || (i1.hasNext() || i2.hasNext() );
                        }                                        
                    }

                    if( remove )
                        remove_attrs.add(attr.getQName());                   
                } 

                if( !remove_attrs.isEmpty() )
                {
                    Iterator< String > removeIter = remove_attrs.iterator();
                    while( removeIter.hasNext() )
                        baseElement.removeAttribute(removeIter.next());
                }
            }
        }                
        
        return true;
    }

    private boolean removeBaseAttributes()
    {
        Iterator< ElementConfig > iter = Config.getElementConfigurations();
        while( iter.hasNext() )
        {
            ElementConfig config = iter.next();
            
            if( (config.Base == null) || (config.Base.length() == 0) || (config.Base.indexOf(':') == -1) )
                continue;
            
            Element baseElement = BaseElements.get(config.Base);            
            Element thisElement = Elements.get( config.Name );
            
            if( (baseElement != null) && (thisElement != null) )
            {
                Iterator< Attribute > attrIter = baseElement.getAttributes();
                while( attrIter.hasNext() )
                {
                    thisElement.removeAttribute( attrIter.next().getQName() );
                }
                
            }
        }        
        return true;
    }
    
    private boolean populateElements()
    {
        Iterator< RngElement > iter = Handler.getElements();
        while( iter.hasNext() )
        {
            RngElement rngElement = iter.next();
            if( (rngElement != null) )
            {
                Element element = null;
                
                Iterator< String > nameIter = rngElement.getNames();
                while( nameIter.hasNext() )
                {                    
                    String elementName = nameIter.next();
                    
                    ElementConfig elementConfig = Config.getConfigForElement(elementName);
                                        
                    String tmpName;
                    if( (elementConfig != null) && (elementConfig.ReName != null) && (elementConfig.ReName.length() != 0) )
                        tmpName = elementConfig.ReName;
                    else
                        tmpName = elementName;
                    
                    String styleFamily;
                    if( elementConfig != null )
                        styleFamily = elementConfig.StyleFamily;
                    else
                        styleFamily = "";
                    
                    element = new Element( RngNode.getLocalName( tmpName ), elementName, styleFamily, element );
                    Elements.put( elementName, element );
                    
                    Iterator< AttributeEntry > attributes = rngElement.getAttributes();
                    while( attributes.hasNext() )
                    {
                        AttributeEntry entry = attributes.next();

                        Iterator< String > attributeNames = entry.Attribute.getNames();
                        while( attributeNames.hasNext() )
                        {
                            String QName = attributeNames.next();
                            
                            if( QName != null )
                            {
                                String valueType = entry.Attribute.getType();
                                String conversionType = null;

                                if( valueType.length() == 0 )
                                    valueType = "string";
                                
                                DataTypeConfig config = Config.getDataTypeConfiguration(valueType);

                                if( config != null )
                                {
                                    valueType = config.ValueType;
                                    conversionType = config.ConversionType;
                                }
                                if( valueType == null )
                                    valueType = entry.Attribute.getType();
                                
                                if( conversionType == null)
                                {
                                    if( valueType.equals(RngAttribute.TYPE_ENUM) )
                                    {
                                        conversionType = QName;
                                    }
                                    else
                                    {
                                        conversionType = valueType;
                                    }
                                }
                                
                                Attribute attr = new Attribute( QName, valueType, conversionType, entry.Attribute.getValues(), entry.Optional, entry.Attribute.getDefaultValue() );
                                element.addAttribute(attr);
                            }
                            else
                            {
                                System.err.println("warning, element <" + elementName + "> has an attribute without a name!");
                            }
                        }
                    }
                    
                    // optimize attributes
                    Iterator< Attribute > attrIter = element.getAttributes();
                    while( attrIter.hasNext() )
                    {
                         Attribute attr = attrIter.next();
                         
                         String valueType = attr.getValueType();
                         
                        // optimize enum types with only one value and boolean value types
                        if( valueType.equals(RngAttribute.TYPE_ENUM) )
                        {
                            switch( attr.getValueCount() )
                            {
                            case 0: // todo: give error?
                            case 1: // todo: optimize
                                valueType = "string";
                                break;
                            case 2:
                                if( attr.hasValue("true") && attr.hasValue("false") )
                                    valueType = "boolean";
                                break;
                            default:
                                continue;
                            }
                         
                            DataTypeConfig config = Config.getDataTypeConfiguration(valueType);
                            attr.setValueType( ( config != null && (config.ValueType.length() != 0) ) ? config.ValueType : valueType);

                            if( (config != null) && (config.ConversionType.length() != 0) )
                            {
                                attr.setConversionType( config.ConversionType );
                            }
                        }
                    }
                }
            }
        }
        
        return true;
    }

    private boolean renameAttributes()
    {
        Iterator< Element > elementIter = Elements.values().iterator();
        while( elementIter.hasNext() )
        {
            Element element = elementIter.next();
            HashMap< String, AttributeConfig > confMap = new HashMap< String, AttributeConfig >();
            
            Iterator< AttributeConfig > configIter = Config.getAttributeConfigurations();
            while( configIter.hasNext() )
            {
                AttributeConfig config = configIter.next();
               
                if( config.Element.equals( element.getQName() ) && (element.getAttribute(config.Name) != null) )
                    confMap.put(config.Name, config);
                
                if( (config.Element.length() == 0) && (element.getAttribute(config.Name) != null) && !confMap.containsKey(config.Name))
                    confMap.put(config.Name, config);                    
            }
            
            if( !confMap.isEmpty() )
            {
                configIter = confMap.values().iterator();
                while( configIter.hasNext() )
                {
                    AttributeConfig config = configIter.next();
                    Attribute attr = element.getAttribute(config.Name);
                    if( (config.TypeName.length() != 0) )
                    {                       
                        DataTypeConfig type_config = Config.getDataTypeConfiguration(config.TypeName);
                        if( type_config != null )
                        {
                            attr.setValueType( type_config.ValueType );
                            if( type_config.ConversionType.length() == 0 )
                                attr.setConversionType( type_config.ValueType );
                            else
                                attr.setConversionType( type_config.ConversionType );
                        }
                        else
                        {                           
                            attr.setConversionType( config.TypeName );
                        }
                    }
                    if( (config.Rename.length() != 0) )
                        attr.setName( config.Rename );
                }
            }
        }
        return true;
    }

    public HashMap<String, String> getNamespaces()
    {
        return Handler.getNamespaces();
    }
}
