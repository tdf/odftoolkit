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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.odftoolkit.odfdom.codegen.Config.AttributeConfig;
import org.odftoolkit.odfdom.codegen.Config.DataTypeConfig;
import org.odftoolkit.odfdom.codegen.Config.ElementConfig;
import org.odftoolkit.odfdom.codegen.rng.RngAttribute;
import org.odftoolkit.odfdom.codegen.rng.RngElement;
import org.odftoolkit.odfdom.codegen.rng.RngHandler;
import org.odftoolkit.odfdom.codegen.rng.RngNode;
import org.odftoolkit.odfdom.codegen.rng.RngElement.AttributeEntry;
import org.odftoolkit.odfdom.codegen.rng.RngElement.SubAttributeEntry;
import org.odftoolkit.odfdom.codegen.rng.RngElement.SubElementEntry;

/**
 *
 * @author cl93746
 */
public class Schema
{
    private HashMap< String, Element > BaseElements;
    private HashMap< String, Element > Elements;
    private HashMap< String, AttributeSet > AttributeSetMap;
    private RngHandler Handler;
    private Config Config;

    public Schema( Config config )
    {
        BaseElements = new HashMap< String, Element >();
        Elements = new HashMap< String, Element >();
        AttributeSetMap = new HashMap< String, AttributeSet >();
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
                    if( renameBaseAttributes()){
                    	
                    	if( populateAttributeSets() ){
                    		return removeBaseAttributes();
                    	}
                    }
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
    	Object[]  keys = Elements.keySet().toArray();
        Arrays.sort(keys);
        List<Element> values = new ArrayList<Element>();        
        for(int i=0; i< keys.length; i++){
        	values.add(Elements.get(keys[i].toString()));
        }
    	return values.iterator();
    }
    
    public Iterator<AttributeSet> getAttributeSets()
    {
    	Object[] keys = AttributeSetMap.keySet().toArray();
    	Arrays.sort(keys);
    	List<AttributeSet> attrSetList = new ArrayList<AttributeSet>();
    	for(int i=0; i< keys.length; i++){
    		attrSetList.add(AttributeSetMap.get(keys[i].toString()));
    	}
    	return attrSetList.iterator();
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
                                
                                if(valueType.contains(";"))
                                	valueType = "String";
                                Attribute attr = new Attribute( QName, valueType, conversionType, entry.Attribute.getValues(), entry.Optional, entry.Attribute.getDefaultValue() );
                                element.addAttribute(attr);
                                attr.setOwnerElement(element);
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
                            	System.err.println("attribute " + attr.getQName() + "does not have any value" );
                            	break;
                            case 1: // todo: optimize, just viewed as an enum for now
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
        
		// add children elements to Element
		Iterator<RngElement> parentIter = Handler.getElements();
		while (parentIter.hasNext()) {
			RngElement parent = parentIter.next();
			Element element = Elements.get(parent.getName());
			Iterator<SubElementEntry> subIter = parent.getSubElements()
					.iterator();
			String strSubelements ="";			
			while (subIter.hasNext()) {
				boolean isHave = false;
				SubElementEntry child = subIter.next();
				String strSubelement = child.Element.getName();
				if (element.getName() != null && child.Element.getName() != null) {
					StringTokenizer tokens = new StringTokenizer(strSubelements, ";" );
			        while( tokens.hasMoreTokens() ){
			        	if( tokens.nextToken().equals(strSubelement) ){
	                       isHave = true;    
				        }
			        }
			        if(!isHave){
	        		   element.addSubElement(Elements.get(child.Element.getName()));
	        		   strSubelements = strSubelements+";"+strSubelement;
			        }
				}
			}

		}

		
		
		//add sub attributes to Element
		parentIter = Handler.getElements();
		while (parentIter.hasNext()) {
            
			RngElement rngElement = parentIter.next();
			if(rngElement.getName()==null){
				continue;
			}
			
			Element element = Elements.get(rngElement.getName());
			// subattributes
			Iterator<SubAttributeEntry> subattributes = rngElement
					.getSubAttributes().iterator();
			Vector<Vector<RngAttribute>> tmpattributes = new Vector<Vector<RngAttribute>>();

			// find all required attributes
			Vector<RngAttribute> requiredAttribute = new Vector<RngAttribute>();
			while (subattributes.hasNext()) {
				SubAttributeEntry entry = subattributes.next();
				if (!entry.IsChoice) {
					requiredAttribute.add(entry.Attribute);
				}
			}

			// find all single choice attributes
			subattributes = rngElement.getSubAttributes().iterator();
			while (subattributes.hasNext()) {
				SubAttributeEntry entry = subattributes.next();
				if (entry.IsChoice && entry.GroupId == 0) {
					Vector<RngAttribute> choiceAttribute = new Vector<RngAttribute>();
					if(requiredAttribute.size()>0){
						choiceAttribute.addAll(requiredAttribute);
					}
					choiceAttribute.add(entry.Attribute);
					tmpattributes.add(choiceAttribute);
				}
					
			}

			// find max group id
			int i = 0;
			subattributes = rngElement.getSubAttributes().iterator();
			while (subattributes.hasNext()) {
				SubAttributeEntry entry = subattributes.next();
				if (entry.GroupId > i) {
					i = entry.GroupId;
				}
			}
            
     		// assemble choice group attribute with required attribute		
			int j = 1;
			while (j <= i) {
				Vector<RngAttribute> groupAttribute = new Vector<RngAttribute>();
				subattributes = rngElement.getSubAttributes().iterator();
				while (subattributes.hasNext()) {
					SubAttributeEntry entry = subattributes.next();
					if (entry.IsChoice && entry.GroupId == j) {
						groupAttribute.add(entry.Attribute);
					}
				}

				if(requiredAttribute.size()>0){
					groupAttribute.addAll(requiredAttribute);
				}
					            
				tmpattributes.add(groupAttribute);
				j++;
			}
            
			// only required attribute
            if(tmpattributes.size()==0 && requiredAttribute.size() >0){
            	tmpattributes.add(requiredAttribute);
            }
            
            
			Iterator<Vector<RngAttribute>> itOut = tmpattributes.iterator();
			String strTypes ="";
			String strQNames ="";
			String strs ="";
//			System.out.println("schema: attribute analyst");
			while (itOut.hasNext()) {
				Vector<Attribute> combAttributes = new Vector<Attribute>();
				Vector<RngAttribute> outRngAttribute = (Vector<RngAttribute>) itOut.next();
	            //sort attributes
	            Object [] arr = outRngAttribute.toArray();
	            Arrays.sort(arr,new Comparator() {
	                  public int compare(Object arg0, Object arg1) {
	                    Object key1 = ((RngAttribute)arg0).getName();
	                    Object key2 = ((RngAttribute)arg1).getName();
	                    return ((Comparable) key1).compareTo(key2);
	                  }});
	            Vector< RngAttribute > v = new Vector< RngAttribute >();
	            for(int m=0; m<arr.length; m++){
	            	v.add((RngAttribute) arr[m]);
	            }
				
				Iterator<RngAttribute> itIn = v.iterator();
				while (itIn.hasNext()) {
					RngAttribute inAttribute = itIn.next();
					Iterator<String> attributeNames = inAttribute.getNames();
					while (attributeNames.hasNext()) {
						String QName = attributeNames.next();
						if (QName != null) {
							/*String valueType = inAttribute.getType();
							String conversionType = null;

							if (valueType.length() == 0)
								valueType = "string";

							DataTypeConfig config = Config
									.getDataTypeConfiguration(valueType);

							if (config != null) {
								valueType = config.ValueType;
								conversionType = config.ConversionType;
							}
							if (valueType == null)
								valueType = inAttribute.getType();

							if (conversionType == null) {
								if (valueType.equals(RngAttribute.TYPE_ENUM)) {
									conversionType = QName;
								} else {
									conversionType = valueType;
								}
							}
							Attribute attr = new Attribute(QName, valueType,
									conversionType, inAttribute.getValues(),
									false, inAttribute.getDefaultValue());*/
							Attribute attr = element.getAttribute(QName);
							if(attr!=null)
								combAttributes.add(attr);
							
						} else {
							System.err.println("warning, element <"
									+ "> has an attribute without a name!");
						}
					}

				}
				
				/*
                // optimize attributes
                Iterator< Attribute > attrIter = combAttributes.iterator();
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
                        	System.err.println("attribute " + attr.getQName() + "does not have any value" );
                        	break;
                        case 1: // todo: optimize, just viewed as an enum for now
                            break;
                        case 2:
                            if( attr.hasValue("true") && attr.hasValue("false") ){
                            	valueType = "boolean";
                            }
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
				*/
				
				//merge same type parameter

				Iterator<Attribute> strIter = combAttributes.iterator();
				String strQName ="";
				String strType ="";
				String str ="";
				String type;
				boolean isExistName = false;
				boolean isExistType = false;
				while(strIter.hasNext()){
					Attribute strAttr = strIter.next();
					if(strQName.equals("")){
						strQName = strAttr.getQName();
					}else{
						strQName = strQName +"%"+ strAttr.getQName();
					}
					
					if(strAttr.getValueType().equals("Integer")){
						type = "int";
					}else if( strAttr.getValueType().equals("Boolean")){
						type = "boolean";
					}else if( strAttr.getValueType().equals("javax.xml.datatype.XMLGregorianCalendar")){
						type = "String";
					}else if( strAttr.getValueType().equals("java.net.URI")){
						type = "String";
					}else if( strAttr.getValueType().equals("javax.xml.datatype.Duration")){
						type = "String";
					}else if( strAttr.getValueType().equals("Double")){
						type = "double";
					}else if( strAttr.getValueType().equals("enum")){
						type = "String";
					}else{
						type = strAttr.getValueType();
					}
					
					if(strType.equals("")){
						strType = type;
					}else{
						strType = strType +"%"+ type;
					}
					
				}
//				System.out.println("schema: strQName "+ strQName);
//				System.out.println("schema: strType "+ strType);
				
				StringTokenizer nameTokens = new StringTokenizer(strQNames, ";" );
	            while( nameTokens.hasMoreTokens() ){
	            	if( nameTokens.nextToken().equals(strQName) ){
	            		isExistName =true;
	            	}	
	            }
	            
	            
				StringTokenizer typeTokens = new StringTokenizer(strTypes, ";" );
	            while( typeTokens.hasMoreTokens() ){
	            	if( typeTokens.nextToken().equals(strType) )
	            		isExistType = true;
	            }

	            String diff = "";
	            if(!isExistName && isExistType){
	                String[] nameTypes = strs.split(";");
	                String[] attQNames = null;                
	                for(int m= 0; m< nameTypes.length; m++){
//	                	System.out.println("schema: nameTypes "+ nameTypes[m]);
	                	if(nameTypes[m].endsWith("#"+strType)){
//	                		System.out.println("schame: nametypes m "+nameTypes[m].split("#")[0]);
//	                		System.out.println("schame: attqnames m "+nameTypes[m].split("#")[0].split("%")[0]);
	                		attQNames = nameTypes[m].split("#")[0].split("%");
	                	}
	                }
	                
	                String[] curQNames = strQName.split("%");
	                
	                for(int n=0;n<curQNames.length; n++){
//	                	System.out.println("schema: attQNames "+ curQNames[n]);
	                	int diffInt =0;
	                	for(int p=0; p< attQNames.length; p++){
	                		if(curQNames[n].equals(attQNames[p])){
	                			diffInt++;
	                		}
	                	}
	                	if(diffInt==0){
	                		diff = diff+curQNames[n];
//	                		System.out.println("schema: diff "+ diff);
	                	}
	                }
					element.addSubAttribute(combAttributes);
					element.addDifferentName(diff);
		            str = strQName+"#"+strType;
					strQNames = strQNames+";"+strQName;
//					System.out.println("schema: strQNames "+ strQNames);
					strTypes = strTypes +";"+strType;
//					System.out.println("schema: strType "+ strTypes);
					strs = strs + ";" + str;	
//					System.out.println("schema: strs "+ strs);
      
	            }
	            
				if(!isExistName && !isExistType ){
					diff ="";
					element.addSubAttribute(combAttributes);
					element.addDifferentName(diff);
		            str = strQName+"#"+strType;
					strQNames = strQNames+";"+strQName;
					strTypes = strTypes +";"+strType;
					strs = strs + ";" + str;	

				}

				
			}
//			System.out.println("schema:  out strs "+ strs);
			
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
                    
                    if( (config.DefaultValue.length() != 0) )
                        attr.setDefaultValue( config.DefaultValue );
                    
                    //rename subattributes
					Iterator<Vector<Attribute>> subAttrs = element.getSubAttributes().iterator();
					while (subAttrs.hasNext()) {
						Iterator<Attribute> subAttr = subAttrs.next()
								.iterator();
						while (subAttr.hasNext()) {
							Attribute aAttr = subAttr.next();
							if (aAttr.getQName().equals(config.Name)) {
								if ((config.TypeName.length() != 0)) {
									DataTypeConfig type_config = Config
											.getDataTypeConfiguration(config.TypeName);
									if (type_config != null) {
										aAttr
												.setValueType(type_config.ValueType);
										if (type_config.ConversionType.length() == 0)
											aAttr
													.setConversionType(type_config.ValueType);
										else
											aAttr
													.setConversionType(type_config.ConversionType);
									} else {
										aAttr
												.setConversionType(config.TypeName);
									}
								}
								if ((config.Rename.length() != 0))
									aAttr.setName(config.Rename);
							}
						}
					}

                
                }
            }
        }
        return true;
    }

    private boolean renameBaseAttributes()
    {
        Iterator< Element > elementIter = BaseElements.values().iterator();
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
                    
                    if( (config.DefaultValue.length() != 0) )
                        attr.setDefaultValue( config.DefaultValue );
                    
                    //rename subattributes
					Iterator<Vector<Attribute>> subAttrs = element.getSubAttributes().iterator();
					while (subAttrs.hasNext()) {
						Iterator<Attribute> subAttr = subAttrs.next()
								.iterator();
						while (subAttr.hasNext()) {
							Attribute aAttr = subAttr.next();
							if (aAttr.getQName().equals(config.Name)) {
								if ((config.TypeName.length() != 0)) {
									DataTypeConfig type_config = Config
											.getDataTypeConfiguration(config.TypeName);
									if (type_config != null) {
										aAttr.setValueType(type_config.ValueType);
										if (type_config.ConversionType.length() == 0)
											aAttr.setConversionType(type_config.ValueType);
										else
											aAttr.setConversionType(type_config.ConversionType);
									} else {
										aAttr.setConversionType(config.TypeName);
									}
								}
								if ((config.Rename.length() != 0))
									aAttr.setName(config.Rename);
							}
						}
					}

                
                }
            }
        }
        return true;
    }
    
    public boolean populateAttributeSets()
    {
    	//add AttributeSet which is represent the set of attribute with the same attribute name
		Iterator<Element> elementIter = getElements();
		while (elementIter.hasNext()) { 
			Element element = elementIter.next();
			Iterator<Attribute> attrIter = element.getAttributes();
			AttributeSet retAttrSet;
			while (attrIter.hasNext()) {
				Attribute attr = attrIter.next();
				if( (retAttrSet = AttributeSetMap.get(attr.getQName())) != null){
					retAttrSet.addAttribute(attr);
				}else{
					AttributeSet newAttrSet = new AttributeSet(attr.getName(),attr.getQName(),attr.getValueType(),attr.getConversionType());
					newAttrSet.addAttribute(attr);
					AttributeSetMap.put(attr.getQName(), newAttrSet);
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
