/************************************************************************
 *
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
 ************************************************************************/

package org.odftoolkit.odfdom.dom.element;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.style.OdfParagraphProperties;
import org.odftoolkit.odfdom.doc.element.style.OdfTextProperties;
import org.odftoolkit.odfdom.dom.OdfName;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.style.OdfChartPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfDrawingPagePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfHeaderFooterPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfPageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfRubyPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfSectionPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTableRowPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.OdfStylePropertySet;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
abstract public class OdfStyleBase extends OdfContainerElementBase implements OdfStylePropertySet
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8271282184913774000L;
	private HashMap< OdfStylePropertiesSet, OdfStylePropertiesBase > mPropertySetElementMap;
    private Vector< OdfStylableElement > mStyleUser;

    static HashMap< OdfName, OdfStylePropertiesSet > mStylePropertiesElementToSetMap;

    static
    {
        mStylePropertiesElementToSetMap = new HashMap< OdfName, OdfStylePropertiesSet >();
        mStylePropertiesElementToSetMap.put( OdfChartPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.ChartProperties);
        mStylePropertiesElementToSetMap.put( OdfDrawingPagePropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.DrawingPageProperties);
        mStylePropertiesElementToSetMap.put( OdfGraphicPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.GraphicProperties);
        mStylePropertiesElementToSetMap.put( OdfHeaderFooterPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.HeaderFooterProperties );
        mStylePropertiesElementToSetMap.put( OdfListLevelPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.ListLevelProperties );
        mStylePropertiesElementToSetMap.put( OdfPageLayoutPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.PageLayoutProperties );
        mStylePropertiesElementToSetMap.put( OdfParagraphPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.ParagraphProperties );
        mStylePropertiesElementToSetMap.put( OdfRubyPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.RubyProperties );
        mStylePropertiesElementToSetMap.put( OdfSectionPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.SectionProperties );
        mStylePropertiesElementToSetMap.put( OdfTableCellPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.TableCellProperties );
        mStylePropertiesElementToSetMap.put( OdfTableColumnPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.TableColumnProperties );
        mStylePropertiesElementToSetMap.put( OdfTablePropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.TableProperties );
        mStylePropertiesElementToSetMap.put( OdfTableRowPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.TableRowProperties );
        mStylePropertiesElementToSetMap.put( OdfTextPropertiesElement.ELEMENT_NAME, OdfStylePropertiesSet.TextProperties );        
    }
    
    /** Creates a new instance of OdfElement */
    public OdfStyleBase(OdfFileDom ownerDocument,
            String namespaceURI,
            String qualifiedName) throws DOMException {
        super(ownerDocument, namespaceURI, qualifiedName);
    }

    /** Creates a new instance of OdfElement */
    public OdfStyleBase(OdfFileDom ownerDocument, 
            OdfName aName) throws DOMException {
        super(ownerDocument, aName.getUri(), aName.getQName());
    }           
    
    public void addStyleUser( OdfStylableElement user )
    {
        if( mStyleUser == null )
            mStyleUser = new Vector< OdfStylableElement >();

        mStyleUser.add(user);
    }

    /**
     * get a map containing all properties of this style and their values.
     * @return map of properties. 
     */
    public Map<OdfStyleProperty, String> getStyleProperties()
    {
        TreeMap<OdfStyleProperty, String> result = new TreeMap<OdfStyleProperty, String>();
        OdfStyleFamily family = getFamily();
        if( family != null )
        {
            for (OdfStyleProperty property : family.getProperties())
            {
                if( hasProperty(property))
                    result.put(property, getProperty(property));
            }
        }
        return result;
    }
    
    /**
     * get a map containing all properties of this style and their values.
     * The map will also include any properties set by parent styles
     * @return  a map of all the properties.
     */
    public Map<OdfStyleProperty, String> getStylePropertiesDeep()
    {
        TreeMap<OdfStyleProperty, String> result = new TreeMap<OdfStyleProperty, String>();
        OdfStyleBase style = this;
        while (style != null)
        {
            OdfStyleFamily family = style.getFamily();            
            if( family != null )
            {
                for (OdfStyleProperty property : family.getProperties())
                {
                    if( !result.containsKey(property) && style.hasProperty(property))
                        result.put(property, style.getProperty(property));
                }
            }

            style = style.getParentStyle();
        }
        return result;
    }
    
    public void removeStyleUser( OdfStylableElement user )
    {
        if( mStyleUser != null )
            mStyleUser.remove(user);
    }
    
    public int getStyleUserCount()
    {
        return mStyleUser == null ? 0 : mStyleUser.size();
    }
    
    public String getFamilyName()
    {
        return getFamily().getName();
    }
    
    abstract public OdfStyleFamily getFamily();

    /**
     * 
     * @param set
     * @return the style:*-properties element for the given set. Returns null if
     *         such element does not exist yet.
     */
    public OdfStylePropertiesBase getPropertiesElement( OdfStylePropertiesSet set )
    {
        if( mPropertySetElementMap != null )
            return mPropertySetElementMap.get(set);
        
        return null;
    }
    
    /**
     * 
     * @param set
     * @return the style:*-properties element for the given set. If such element
     *         does not yet exist, it is created.
     */
    
    public OdfStylePropertiesBase getOrCreatePropertiesElement(OdfStylePropertiesSet set)
    {
        OdfStylePropertiesBase properties = null;

        if( mPropertySetElementMap != null )
            properties = mPropertySetElementMap.get(set);

        if( properties == null )
        {
            for( Entry< OdfName, OdfStylePropertiesSet > entry : mStylePropertiesElementToSetMap.entrySet() )
            {
                if( entry.getValue().equals(set))
                {
                    properties = (OdfStylePropertiesBase)((OdfFileDom)this.ownerDocument).createElementNS( entry.getKey() );
                    if( getFirstChild() == null )
                    {
                        appendChild( properties );
                    }
                    else
                    {
                        // make sure the properties elements are in the correct order
                        Node beforeNode = null;
                        if( set.equals( OdfStylePropertiesSet.GraphicProperties  ) )
                        {
                            beforeNode = OdfElement.findFirstChildNode( OdfParagraphProperties.class, this );
                            if( beforeNode == null )
                                beforeNode = OdfElement.findFirstChildNode( OdfTextProperties.class, this );
                        }
                        else if( set.equals( OdfStylePropertiesSet.ParagraphProperties  ) )
                        {
                            beforeNode = OdfElement.findFirstChildNode( OdfTextProperties.class, this );
                        }
                        else if( !set.equals( OdfStylePropertiesSet.TextProperties) )
                        {
                            beforeNode = getFirstChild();
                        }

                        if( beforeNode == null )
                        {
                            beforeNode = getFirstChild();
                            // find first non properties node
                            while( beforeNode != null )
                            {
                                if( beforeNode.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    if( !(beforeNode instanceof OdfStylePropertiesBase ) )
                                        break;
                                }
                                beforeNode = beforeNode.getNextSibling();
                            }
                        }

                        insertBefore( properties, beforeNode);
                    }
                    break;
                }
            }
        }

        return properties;
    }
    
    /**
     * 
     * @return a property value.
     */
    public String getProperty(OdfStyleProperty prop)
    {
        String value = null;
        
        OdfStylePropertiesBase properties = getPropertiesElement(prop.getPropertySet());
        if( properties != null )
        {
            if( properties.hasAttributeNS(prop.getName().getUri(), prop.getName().getLocalName() ) )
                return properties.getOdfAttribute(prop.getName());
        }
        
        OdfStyleBase parent = getParentStyle();
        if( parent != null )
            return parent.getProperty( prop );
            
        return value;
    }
    
    public boolean hasProperty( OdfStyleProperty prop )
    {
        if( mPropertySetElementMap != null )
        {
            OdfStylePropertiesBase properties = mPropertySetElementMap.get(prop.getPropertySet());
            if( properties != null )
                return properties.hasAttributeNS(prop.getName().getUri(), prop.getName().getLocalName() );
        }
        return false;
    }
    
    @Override
    protected void onOdfNodeInserted( OdfElement node, Node refChild )
    {
        if( node instanceof OdfStylePropertiesBase )
        {
            OdfStylePropertiesSet set = mStylePropertiesElementToSetMap.get(node.getOdfName());
            if( set != null )
            {
                if( mPropertySetElementMap == null )
                    mPropertySetElementMap = new HashMap< OdfStylePropertiesSet, OdfStylePropertiesBase >();
                mPropertySetElementMap.put( set, (OdfStylePropertiesBase)node );
            }
        }
    }

    @Override
    protected void onOdfNodeRemoved( OdfElement node )
    {
        if( mPropertySetElementMap != null )
        {            
            if( node instanceof OdfStylePropertiesBase )
            {
                OdfStylePropertiesSet set = mStylePropertiesElementToSetMap.get(node.getOdfName());
                if( set != null )
                {
                    mPropertySetElementMap.remove(set);
                }
            }
        }
    }

    public Map<OdfStyleProperty, String> getProperties(Set<OdfStyleProperty> properties)
    {
        HashMap< OdfStyleProperty, String > map = new HashMap< OdfStyleProperty, String >();
        for( OdfStyleProperty property : properties ) {
            map.put(property, getProperty(property));
        }
        
        return map;
    }

    public Set<OdfStyleProperty> getStrictProperties()
    {
        return getFamily().getProperties();
    }

    public void removeProperty(OdfStyleProperty property)
    {
        if( mPropertySetElementMap != null )
        {
            OdfStylePropertiesBase properties = mPropertySetElementMap.get(property.getPropertySet());
            if( properties != null )
                properties.removeAttributeNS(property.getName().getUri(), property.getName().getLocalName());
        }
    }

    public void setProperties(Map<OdfStyleProperty, String> properties)
    {
        for( Map.Entry< OdfStyleProperty, String > entry : properties.entrySet() ) {
            setProperty(entry.getKey(), entry.getValue());
        }
    }

    public void setProperty(OdfStyleProperty property, String value)
    {
        OdfStylePropertiesBase properties = getOrCreatePropertiesElement(property.getPropertySet());
        if( properties != null )
            properties.setOdfAttribute(property.getName(), value);
    }

    /** indicates if some other object is equal to this one.
     *  The attribute style:name is ignored during compare.
     * 
     * @param obj - the reference object with which to compare. 
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if( this == obj )
            return true;

        if( (obj == null) || !(obj instanceof OdfStyleBase) )
            return false;

        OdfStyleBase compare = (OdfStyleBase)obj;

        // compare node name
        if( !localName.equals( compare.localName ) )
           return false;

        if( !this.namespaceURI.equals( compare.namespaceURI ) )
            return false;

        // compare node attributes
        if( attributes == compare.attributes )
            return true;

        if( (attributes == null) || (compare.attributes == null) )
            return false;

        int attr_count1 = attributes.getLength();
        int attr_count2 = compare.attributes.getLength();

        Vector< Node > attr1 = new Vector< Node >();
        for( int i = 0; i < attr_count1; i++ )
        {
            Node node = attributes.item(i);
            if( node.getNodeValue().length() == 0 )
                continue;
            attr1.add( node );
        }

        Vector< Node > attr2 = new Vector< Node >();
        for( int i = 0; i < attr_count2; i++ )
        {
            Node node = compare.attributes.item(i);
            if( node.getNodeValue().length() == 0 )
                continue;
            attr2.add( node );
        }

        if( attr1.size() != attr2.size() )
            return false;

        for( int i = 0; i < attr1.size(); i++ )
        {
            Node n1 = attr1.get(i);
            if( n1.getLocalName().equals( "name") && n1.getNamespaceURI().equals( OdfNamespace.STYLE.getUri()) )
                continue; // do not compare style names

            Node n2 = null;
            int j = 0;
            for( j = 0; j < attr2.size(); j++ )
            {
                n2 = attr2.get(j);
                if( n1.getLocalName().equals(n2.getLocalName()) && n1.getNamespaceURI().equals(n2.getNamespaceURI()) )
                    break;
            }
            if( j == attr2.size() )
                return false;

            if( !n1.getTextContent().equals( n2.getTextContent()))
                return false;
        }

        // now compare child elements
        NodeList childs1 = this.getChildNodes();
        NodeList childs2 = compare.getChildNodes();

        int child_count1 = childs1.getLength();
        int child_count2 = childs2.getLength();
        if( (child_count1 == 0) && (child_count2 == 0 ))
            return true;

        Vector< Node > nodes1 = new Vector< Node >();
        for( int i = 0; i < child_count1; i++ )
        {
            Node node = childs1.item(i);
            if( node.getNodeType() == Node.TEXT_NODE )
                if( node.getNodeValue().trim().length() == 0 )
                    continue; // skip whitespace text nodes

            nodes1.add( node );
        }

        Vector< Node > nodes2 = new Vector< Node >();
        for( int i = 0; i < child_count2; i++ )
        {
            Node node = childs2.item(i);
            if( node.getNodeType() == Node.TEXT_NODE )
                if( node.getNodeValue().trim().length() == 0 )
                    continue; // skip whitespace text nodes
            
            nodes2.add( node );
        }

        if( nodes1.size() != nodes2.size() )
            return false;
        
        for( int i = 0; i < nodes1.size(); i++ )
        {
            Node n1 = nodes1.get(i);
            Node n2 = nodes2.get(i);
            n1.toString();
            n2.toString();
            if( !n1.equals(n2) )
                return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        return 59 * 7 + (this.mPropertySetElementMap != null ? this.mPropertySetElementMap.hashCode() : 0);
    }
    
    abstract public OdfStyleBase getParentStyle();
}
