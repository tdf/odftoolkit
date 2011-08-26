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
package org.openoffice.odf.dom.element;

import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.ParentNode;
import org.openoffice.odf.doc.OdfDocument;
import org.openoffice.odf.dom.OdfName;
import org.openoffice.odf.doc.OdfFileDom;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

//2DO: Refactor public to package viewer, when inheritance is exchanged from OdfElement to specific Odf Element
abstract public class OdfElement extends ElementNSImpl {

    // the OdfDocument containing the element
    protected OdfDocument mOdfDocument;

    /** Creates a new instance of OdfElement */
    public OdfElement(OdfFileDom ownerDocument,
            String namespaceURI,
            String qualifiedName) throws DOMException {
        super(ownerDocument, namespaceURI, qualifiedName);
        mOdfDocument = ownerDocument.getOdfDocument();
    }

    /** Creates a new instance of OdfElement */
    public OdfElement(OdfFileDom ownerDocument, 
            OdfName aName) throws DOMException {
        super(ownerDocument, aName.getUri(), aName.getQName());
        mOdfDocument = ownerDocument.getOdfDocument();
    }
    
    abstract public OdfName getOdfName();
    
    protected <T extends OdfElement> T getParentAs(Class<T> clazz) {
        Node parent = getParentNode();
        if (parent != null && clazz.isInstance(parent)) {
            return clazz.cast(parent);
        } else {
            return null;
        }
    }

    protected <T extends OdfElement> T getAncestorAs(Class<T> clazz) {
        Node node = getParentNode();        
        while ( node != null) {
            if (clazz.isInstance(node)) {
                return clazz.cast(node);
            }
            node = node.getParentNode();
        }
        return null;
    }    
    
    @Override
    public String toString(){
        return mapNode(this, new StringBuilder()).toString();
    }
    
    /** Only Siblings will be traversed by this method as Children */
    static private StringBuilder mapNodeTree(Node node, StringBuilder xml){
        while(node != null){
            // mapping node and this mapping include always all descendants
            xml = mapNode(node, xml);
            // next sibling will be mapped to XML
            node = node.getNextSibling();
        }
        return xml;
    }
        
    private static StringBuilder mapNode(Node node, StringBuilder xml){
        if(node instanceof Element){
            xml = mapElementNode(node, xml);
        }else if(node instanceof Text){
            xml = mapTextNode(node, xml);
        }
        return xml;
    }
    
    private static StringBuilder mapTextNode(Node node, StringBuilder xml){
        if(node != null){
            xml = xml.append(node.getTextContent());
        }
        return xml;
    }
        
    private static StringBuilder mapElementNode(Node node, StringBuilder xml){
        if(node != null){
            xml = xml.append("<");
            xml = xml.append(node.getNodeName());
            xml = mapAttributeNode(node, xml);
            xml = xml.append(">");
            xml = mapNodeTree(node.getFirstChild(), xml);
            xml = xml.append("</");
            xml = xml.append(node.getNodeName());
            xml = xml.append(">");
        }
        return xml;
    }
    
    private static StringBuilder mapAttributeNode(Node node, StringBuilder xml){
        NamedNodeMap attrs = null;
        int length;
        if((attrs = node.getAttributes()) != null && (length = attrs.getLength()) > 0){
            for(int i=0;length > i;i++){
                xml = xml.append(" ");
                xml = xml.append(attrs.item(i).getNodeName());
                xml = xml.append("=\"");
                xml = xml.append(attrs.item(i).getNodeValue());
                xml = xml.append("\"");
            }
        }
        return xml;
    }   

    public void setOdfAttribute(OdfName name, String value) {
        setAttributeNS(name.getUri(), name.getQName(), value);
    }

    public String getOdfAttribute(OdfName name) {
        return getAttributeNS(name.getUri(), name.getLocalName());
    }
    
    /** returns the first child node that implements the given class.
     * 
     * @param <T> The type of the ODF element to be found.
     * @param clazz is a class that extends OdfElement.
     * @param parentNode is the parent O of the children to be found.
     * @return the first child node of the given parentNode that is a clazz or null if none is found.
     */
    static public <T extends OdfElement> T findFirstChildNode( Class<T> clazz, Node parentNode )
    {
        if( parentNode != null && parentNode instanceof ParentNode )
        {
            Node node = ((ParentNode)parentNode).getFirstChild();
            while( (node != null) && !clazz.isInstance(node) ) {
                node = node.getNextSibling();
            }        

            if( node != null ) {
                return (T) node;
            }
        }
        
        return null;
    }
    
    /** returns the first sibling after the given reference node that implements the given class.
     * 
     * @param <T> The type of the ODF element to be found.
     * @param clazz is a class that extends OdfElement.
     * @param refNode the reference node of the siblings to be found.
     * @return the first sibbling of the given reference node that is a clazz or null if none is found.
     */
    static public <T extends OdfElement> T findNextChildNode( Class<T> clazz, Node refNode )
    {
        if( refNode != null )
        {
            Node node = refNode.getNextSibling();
            while( node != null && !clazz.isInstance(node) ) {
                node = node.getNextSibling();
            }        

            if( node != null ) {
                return (T) node;
            }
        }
        
        return null;
    }
    
    /** returns the first previous sibling before the given reference node that implements the given class.
     * 
     * @param clazz is a class that extends OdfElement.
     * @param refNode the reference node which siblings are to be searched.
     * @return the first previous sibbling of the given reference node that is a clazz or null if none is found.
     */
    static public <T extends OdfElement> T findPreviousChildNode( Class<T> clazz, Node refNode )
    {
        if( refNode != null )
        {
            Node node = refNode.getPreviousSibling();
            while( node != null && !clazz.isInstance(node) )
                node = node.getPreviousSibling();

            if( node != null )
                return (T)node;
        }
        
        return null;
    }    
    
    @Override
    public Node cloneNode( boolean deep )
    {
        OdfElement cloneElement = ((OdfFileDom) this.ownerDocument).createElementNS(getOdfName());
                
        if( attributes != null )
        {
            for( int i = 0; i < attributes.getLength(); i++ )
            {
                Node item = attributes.item(i);
                cloneElement.setAttributeNS(item.getNamespaceURI(), item.getLocalName(), item.getNodeValue() );            
            }
        }
        
        if( deep )
        {
            Node childNode = getFirstChild();
            while( childNode != null )
            {
                cloneElement.appendChild( childNode.cloneNode(true) );
                childNode = childNode.getNextSibling();
            }
        }
        
        return cloneElement;
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if( this == obj )
            return true;
        
        if( obj == null )
            return false;
        
        if( !( obj instanceof Node) )
            return false;
        
        return this.isEqualNode( (Node)obj);
    }
}
