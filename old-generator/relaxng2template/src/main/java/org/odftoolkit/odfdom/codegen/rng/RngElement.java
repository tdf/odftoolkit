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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.odftoolkit.odfdom.codegen.Element;
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
    
    // add for children elements
    private Vector< SubElementEntry > SubElements;
    //add for children attributes
    private Vector< SubAttributeEntry > SubAttributes;
    public static int groupCount;
    
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

    public class SubElementEntry
    {
        public RngElement Element;
        public boolean Optional;
        
        public SubElementEntry( RngElement element, boolean optional)
        {
            Element = element;
            Optional = optional;  
        }
    }

    public class SubAttributeEntry
    {
    	public RngAttribute Attribute ;
    	public boolean IsChoice;
    	public int GroupId;
        
        public SubAttributeEntry( RngAttribute attribute, boolean isChoice, int groupId)
        {
        	Attribute = attribute;
        	IsChoice = isChoice;
        	GroupId = groupId;
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
    
    private void getSubElements( Vector< SubElementEntry > subelements, RngNode parent)
    {
        Iterator< RngNode > iter = parent.getChildren().iterator();
        
        while( iter.hasNext() )
        {
            RngNode child = iter.next();
          
            if( child.getLocalName().equals( RngReference.LOCAL_NAME) )
            {
        
            	Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)child).getName() );
                while( defineIter.hasNext() )
                    getSubElements( subelements, defineIter.next());
            }
            else if( child.getLocalName().equals( "attribute") )
            {
            	continue;
            }
            else if( child.getLocalName().equals( RngElement.LOCAL_NAME ) )
            {
            	
            	if ( ((RngElement)child).getName()!= null )
            	SubElements.add( new SubElementEntry((RngElement)child, false));
            }
            else if( child.getLocalName().equals( "optional" ) )
            {
            	Iterator< RngNode > optIter = child.getChildren().iterator();
            	
                getChildReference(subelements, optIter);

            }
            else if( child.getLocalName().equals( "choice" ) )
            {
            	Iterator< RngNode > choIter = child.getChildren().iterator();
            	getChildReference(subelements, choIter);
            }
            else if( child.getLocalName().equals( "group" ) )
            {
                Iterator< RngNode > grpIter = child.getChildren().iterator();
                getChildReference(subelements, grpIter);
            }
            else if( child.getLocalName().equals( "zeroOrMore" ) )
            {
            	Iterator< RngNode > zomIter = child.getChildren().iterator();
            	getChildReference(subelements, zomIter);
            }
            else if( child.getLocalName().equals( "oneOrMore" ) )
            {
            	Iterator< RngNode > oomIter = child.getChildren().iterator();
            	getChildReference(subelements, oomIter);
            }
            else if( child.getLocalName().equals( "interleave" ) )
            {
            	Iterator< RngNode > itlIter = child.getChildren().iterator();
            	getChildReference(subelements, itlIter);
            }
            else
            {
            	continue;
            }                
        }        
    }

	private void getChildReference(Vector<SubElementEntry> subelements,
			Iterator<RngNode> optIter) {
		while( optIter.hasNext() ) {
			RngNode temNode = optIter.next();
			
			if(temNode.getLocalName().equals(RngReference.LOCAL_NAME)){                		
		    	Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)temNode).getName() );
		        while( defineIter.hasNext() )
		            getSubElements( subelements, defineIter.next());
			}
			else{
				getSubElements( subelements, temNode);	
			}
			               	
		}
	}
    
    public Vector< SubElementEntry > getSubElements()
    {
        if( SubElements == null )
        {
        	SubElements = new Vector< SubElementEntry >();
            getSubElements( SubElements, this);
        }
        return SubElements;
    }
    
    public String getName(){
    	return Name;
    }

    private void getSubAttributes( Vector< SubAttributeEntry > subattributes, RngNode parent)
    {
        Iterator< RngNode > iter = parent.getChildren().iterator();
        while( iter.hasNext() )
        {
            RngNode child = iter.next();
          
            if( child.getLocalName().equals( RngReference.LOCAL_NAME) )
            {
		    	Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)child).getName() );
		    	while( defineIter.hasNext()){
		    		getSubAttributes( subattributes, defineIter.next());
		    	}    
            }
            else if( child.getLocalName().equals( "attribute") )
            {
            	subattributes.add(new SubAttributeEntry((RngAttribute)child, false, 0));
            }
            else if( child.getLocalName().equals( RngElement.LOCAL_NAME ) )
            {           	
                continue;
            }
            else if( child.getLocalName().equals( "optional" ) )
            {   
            	continue;
            }
            else if( child.getLocalName().equals( "choice" ) )
            {
            	Iterator< RngNode > choIter = child.getChildren().iterator();
             	while(choIter.hasNext()){
            		RngNode choNode= choIter.next();
            		if(choNode.getLocalName().equals("attribute")){            			
            			subattributes.add(new SubAttributeEntry((RngAttribute)choNode, true, 0 ));
            		}else if (choNode.getLocalName().equals("group")){ 
            			Iterator< RngNode > grpIter = choNode.getChildren().iterator();
            			while(grpIter.hasNext()){
            				RngNode grpNode = grpIter.next();
            				if(grpNode.getLocalName().equals("attribute")){
            					subattributes.add(new SubAttributeEntry((RngAttribute)grpNode, true, groupCount)); 
            				}else if (grpNode.getLocalName().equals("ref")){
            					Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)grpNode).getName() );
            					while(defineIter.hasNext()){
            						  RngNode refNode = defineIter.next();
            						if(refNode.getLocalName().equals("attribute")){
            							subattributes.add(new SubAttributeEntry((RngAttribute)refNode, true, groupCount)); 
            						}else{
            							continue;
            						}
            					}
            				}else {
            					continue;
            				}
            				
            			}
            			groupCount++;
            		}else{
            			continue;
            		}           		
            	}
            }
            else if( child.getLocalName().equals( "group" ) )
            {
                Iterator< RngNode > grpIter = child.getChildren().iterator();
                getAttributeReference(subattributes, grpIter);
            }
            else if( child.getLocalName().equals( "zeroOrMore" ) )
            {
            	Iterator< RngNode > zomIter = child.getChildren().iterator();
            	getAttributeReference(subattributes, zomIter);
            }
            else if( child.getLocalName().equals( "oneOrMore" ) )
            {
            	Iterator< RngNode > oomIter = child.getChildren().iterator();
            	getAttributeReference(subattributes, oomIter);
            }
            else if( child.getLocalName().equals( "interleave" ) )
            {
            	Iterator< RngNode > itlIter = child.getChildren().iterator();
            	getAttributeReference(subattributes, itlIter);
            }
            else
            {
            	continue;
            }                
        }        
    }

	private void getAttributeReference(Vector<SubAttributeEntry> subattributes,
			Iterator<RngNode> optIter) {
		while( optIter.hasNext() ) {
			RngNode temNode = optIter.next();
			
			if(temNode.getLocalName().equals(RngReference.LOCAL_NAME)){                		
		    	Iterator< RngDefine > defineIter = Handler.getDefines( ((RngReference)temNode).getName() );
		        while( defineIter.hasNext() )
		            getSubAttributes( subattributes, defineIter.next());
			}else if(temNode.getLocalName().equals(RngElement.LOCAL_NAME)){
				continue;
			}else if(temNode.getLocalName().equals("optional")){
				continue;
			}
			else{
				getSubAttributes( subattributes, temNode);	
			}
			               	
		}
	}
    
    public Vector< SubAttributeEntry > getSubAttributes()
    {
        if( SubAttributes == null )
        {
        	SubAttributes = new Vector< SubAttributeEntry >();
        	groupCount=1;
            getSubAttributes( SubAttributes, this);
        }
        
        return SubAttributes;
    }
}
