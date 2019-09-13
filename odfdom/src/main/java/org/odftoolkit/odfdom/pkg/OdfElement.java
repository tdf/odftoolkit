/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.pkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.ParentNode;
import org.json.JSONObject;
import org.odftoolkit.odfdom.changes.Component;
import org.odftoolkit.odfdom.changes.JsonOperationConsumer;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHandoutMasterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationsElement;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableShapesElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextNoteElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTrackedChangesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

abstract public class OdfElement extends ElementNSImpl {

    private static final long serialVersionUID = -4939293285696678939L;
    private boolean isComponentRoot = false;
    private boolean mIsIgnoredComponent = false;
    private Component mComponent = null;
    // ToDo: Only on component roots
    private int mComponentSize = 0;

    /**
     * Creates a new instance of OdfElement
     */
    public OdfElement(OdfFileDom ownerDocument, String namespaceURI,
        String qualifiedName) throws DOMException {
        super(ownerDocument, namespaceURI, qualifiedName);
    }

    /**
     * Creates a new instance of OdfElement
     */
    public OdfElement(OdfFileDom ownerDocument, OdfName aName)
        throws DOMException {
        super(ownerDocument, aName.getUri(), aName.getQName());
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
        while (node != null) {
            if (clazz.isInstance(node)) {
                return clazz.cast(node);
            }
            node = node.getParentNode();
        }
        return null;
    }

    /**
     * @returns true if the given potentialParent is an ancestor of this element
     */
    public boolean hasAncestor(Node potentialParent) {
        Node parentNode = this.getParentNode();
        boolean isParent = false;
        do {
            if (parentNode == null) {
                break;
            } else if (parentNode.equals(potentialParent)) {
                isParent = true;
                break;
            }
        } while ((parentNode = parentNode.getParentNode()) != null);
        return isParent;
    }

    @Override
    public String toString() {
        return mapNode(this, new StringBuilder()).toString();
    }

    /**
     * Only Siblings will be traversed by this method as Children
     */
    static private StringBuilder mapNodeTree(Node node, StringBuilder xml) {
        while (node != null) {
            // mapping node and this mapping include always all descendants
            xml = mapNode(node, xml);
            // next sibling will be mapped to XML
            node = node.getNextSibling();
        }
        return xml;
    }

    private static StringBuilder mapNode(Node node, StringBuilder xml) {
        if (node instanceof Element) {
            xml = mapElementNode(node, xml);
        } else if (node instanceof Text) {
            xml = mapTextNode(node, xml);
        }
        return xml;
    }

    private static StringBuilder mapTextNode(Node node, StringBuilder xml) {
        if (node != null) {
            xml = xml.append(node.getTextContent());
        }
        return xml;
    }

    private static StringBuilder mapElementNode(Node node, StringBuilder xml) {
        if (node != null) {
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

    private static StringBuilder mapAttributeNode(Node node, StringBuilder xml) {
        NamedNodeMap attrs = null;
        int length;
        if ((attrs = node.getAttributes()) != null
            && (length = attrs.getLength()) > 0) {
            for (int i = 0; length > i; i++) {
                xml = xml.append(" ");
                xml = xml.append(attrs.item(i).getNodeName());
                xml = xml.append("=\"");
                xml = xml.append(attrs.item(i).getNodeValue());
                xml = xml.append("\"");
            }
        }
        return xml;
    }

    /**
     * Set the value of an ODF attribute by <code>OdfName</code>.
     *
     * @param name The qualified name of the ODF attribute.
     * @param value The value to be set in <code>String</code> form
     */
    public void setOdfAttributeValue(OdfName name, String value) {
        setAttributeNS(name.getUri(), name.getQName(), value);
    }

    /**
     * Set an ODF attribute to this element
     *
     * @param attribute the attribute to be set
     */
    public void setOdfAttribute(OdfAttribute attribute) {
        setAttributeNodeNS(attribute);
    }

    /**
     * Retrieves a value of an ODF attribute by <code>OdfName</code>.
     *
     * @param name The qualified name of the ODF attribute.
     * @return The value of the attribute as <code>String</code> or
     * <code>null</code> if the attribute does not exist.
     */
    public String getOdfAttributeValue(OdfName name) {
        return getAttributeNS(name.getUri(), name.getLocalName());
    }

    /**
     * Retrieves an ODF attribute by <code>OdfName</code>.
     *
     * @param name The qualified name of the ODF attribute.
     * @return The <code>OdfAttribute</code> or <code>null</code> if the
     * attribute does not exist.
     */
    public OdfAttribute getOdfAttribute(OdfName name) {
        return (OdfAttribute) getAttributeNodeNS(name.getUri(), name.getLocalName());
    }

    /**
     * Retrieves an ODF attribute by <code>NamespaceName</code>, and local name.
     *
     * @param namespace The namespace of the ODF attribute.
     * @param localname The local name of the ODF attribute.
     * @return The <code>OdfAttribute</code> or <code>null</code> if the
     * attribute does not exist.
     */
    public OdfAttribute getOdfAttribute(NamespaceName namespace, String localname) {
        return (OdfAttribute) getAttributeNodeNS(namespace.getUri(),
            localname);
    }

    /**
     * Determines if an ODF attribute exists.
     *
     * @param name The qualified name of the ODF attribute.
     * @return True if the attribute exists.
     */
    public boolean hasOdfAttribute(OdfName name) {
        return hasAttributeNS(name.getUri(), name.getLocalName());
    }

    /**
     * returns the first child node that implements the given class.
     *
     * @param <T> The type of the ODF element to be found.
     * @param clazz is a class that extends OdfElement.
     * @param parentNode is the parent O of the children to be found.
     * @return the first child node of the given parentNode that is a clazz or
     * null if none is found.
     */
    @SuppressWarnings("unchecked")
    static public <T extends OdfElement> T findFirstChildNode(Class<T> clazz,
        Node parentNode) {
        if (parentNode != null && parentNode instanceof ParentNode) {
            Node node = ((ParentNode) parentNode).getFirstChild();
            while ((node != null) && !clazz.isInstance(node)) {
                node = node.getNextSibling();
            }

            if (node != null) {
                return (T) node;
            }
        }

        return null;
    }

    /**
     * returns the first sibling after the given reference node that implements
     * the given class.
     *
     * @param <T> The type of the ODF element to be found.
     * @param clazz is a class that extends OdfElement.
     * @param refNode the reference node of the siblings to be found.
     * @return the first sibling of the given reference node that is a class or
     * null if none is found.
     */
    @SuppressWarnings("unchecked")
    static public <T extends OdfElement> T findNextChildNode(Class<T> clazz,
        Node refNode) {
        if (refNode != null) {
            Node node = refNode.getNextSibling();
            while (node != null && !clazz.isInstance(node)) {
                node = node.getNextSibling();
            }

            if (node != null) {
                return (T) node;
            }
        }

        return null;
    }

    /**
     * returns the first previous sibling before the given reference node that
     * implements the given class.
     *
     * @param clazz is a class that extends OdfElement.
     * @param refNode the reference node which siblings are to be searched.
     * @return the first previous sibling of the given reference node that is a
     * class or null if none is found.
     */
    @SuppressWarnings("unchecked")
    static public <T extends OdfElement> T findPreviousChildNode(
        Class<T> clazz, Node refNode) {
        if (refNode != null) {
            Node node = refNode.getPreviousSibling();
            while (node != null && !clazz.isInstance(node)) {
                node = node.getPreviousSibling();
            }

            if (node != null) {
                return (T) node;
            }
        }

        return null;
    }

    /**
     * Clones this complete element with all descendants.
     *
     * @return the cloned element
     */
    public OdfElement cloneElement() {
        OdfElement cloneElement = ((OdfFileDom) this.ownerDocument).newOdfElement(this.getClass());
        // if it is an unknown ODF element
        if (cloneElement == null) {
            cloneElement = new OdfAlienElement((OdfFileDom) this.getOwnerDocument(), OdfName.newName(OdfNamespace.getNamespace(this.getNamespaceURI()), this.getTagName()));
        }

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String qname = null;
                String prefix = item.getPrefix();
                if (prefix == null) {
                    qname = item.getLocalName();
                    cloneElement.setAttribute(qname, item.getNodeValue());
                } else {
                    qname = prefix + ":" + item.getLocalName();
                    cloneElement.setAttributeNS(item.getNamespaceURI(), qname, item.getNodeValue());
                }
            }
        }

        // aside of the XML the flag of being a component root have to be copied
        if (this.isComponentRoot) {
            cloneElement.markAsComponentRoot(true);
            cloneElement.mComponentSize = this.mComponentSize;
            cloneElement.mIsIgnoredComponent = this.mIsIgnoredComponent;
            if (!this.mIsIgnoredComponent) {
                Component.createComponent(this.getComponent().getParent(), cloneElement);
            }
        }

        Node childNode = getFirstChild();
        while (childNode != null) {
            cloneElement.appendChild(childNode.cloneNode(true));
            childNode = childNode.getNextSibling();
        }
        // ToDo: There should be an easier - more obvious - way than this...
        if (this.selfAndDescendantTextIgnoredAsComponent()) {
            cloneElement.ignoredComponent(true);
        }
        cloneElement.mComponentSize = this.mComponentSize;
        cloneElement.mIsIgnoredComponent = this.mIsIgnoredComponent;

        return cloneElement;
    }

    /** Overwritten by AlienElement class, which represents XML elements of various names */
    protected OdfElement cloneOdfElement() {
		return ((OdfFileDom) this.ownerDocument).newOdfElement(this.getClass());
	}


    @Override
    /**
     * Clones this element but without cloning xml:id (and office:value)
     * attributes
     *
     * @param deep if a deep copy should happen. If False, only the given
     * element with attributes and no content is copied
     */
    public Node cloneNode(boolean deep) {
		OdfElement cloneElement = this.cloneOdfElement();

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String qname = null;
                String prefix = item.getPrefix();
                if (prefix == null) {
                    qname = item.getLocalName();
                    cloneElement.setAttribute(qname, item.getNodeValue());
                } else {
                    qname = prefix + ":" + item.getLocalName();
                    if (!qname.equals("xml:id") && !qname.equals("office:value") && !qname.equals("calcext:value-type") && !qname.equals("office:value-type")) {
                        cloneElement.setAttributeNS(item.getNamespaceURI(), qname, item.getNodeValue());
                    }
                }
            }
        }

        // aside of the XML the flag of being a component root have to be copied
        if (this.isComponentRoot) {
            cloneElement.markAsComponentRoot(true);
            cloneElement.mComponentSize = this.mComponentSize;
            cloneElement.mIsIgnoredComponent = this.mIsIgnoredComponent;
            if (!this.mIsIgnoredComponent) {
                Component.createComponent(this.getComponent().getParent(), cloneElement);
            }
        }

        if (deep) {
            Node childNode = getFirstChild();
            while (childNode != null) {
                cloneElement.appendChild(childNode.cloneNode(true));
                childNode = childNode.getNextSibling();
            }
        }
        // ToDo: There should be an easier - more obvious - way than this...
        if (this.selfAndDescendantTextIgnoredAsComponent()) {
            cloneElement.ignoredComponent(true);
        }
        cloneElement.mComponentSize = this.mComponentSize;
        cloneElement.mIsIgnoredComponent = this.mIsIgnoredComponent;

        return cloneElement;
    }

    /**
     * @param depth how many levels of children should be considered
     * @return the cloned node (element)
     * @depth level of children to be cloned All attributes except xml:id and
     * office:value attributes will not be cloned.
     */
    public Node cloneNode(int depth) {
        OdfElement cloneElement = ((OdfFileDom) this.ownerDocument).newOdfElement(this.getClass());
        // if it is an unknown ODF element
        if (cloneElement == null) {
            cloneElement = new OdfAlienElement((OdfFileDom) this.getOwnerDocument(), OdfName.newName(OdfNamespace.getNamespace(this.getNamespaceURI()), this.getTagName()));
        }

        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String qname = null;
                String prefix = item.getPrefix();
                if (prefix == null) {
                    qname = item.getLocalName();
                    cloneElement.setAttribute(qname, item.getNodeValue());
                } else {
                    qname = prefix + ":" + item.getLocalName();
                    // cell value & type handling might as well overwritten in cell class
                    if (!qname.equals("xml:id") && !qname.equals("office:value") && !qname.equals("calcext:value-type") && !qname.equals("office:value-type")) {
                        cloneElement.setAttributeNS(item.getNamespaceURI(), qname, item.getNodeValue());
                    }
                }
            }
        }

        // aside of the XML the flag of being a component root have to be copied
        if (this.isComponentRoot) {
            cloneElement.markAsComponentRoot(true);
            cloneElement.mComponentSize = this.mComponentSize;
            cloneElement.mIsIgnoredComponent = this.mIsIgnoredComponent;
            if (!this.mIsIgnoredComponent) {
                Component.createComponent(this.getComponent().getParent(), cloneElement);
            }
        }

        if (depth > 0) {
            Node childNode = getFirstChild();
            while (childNode != null) {
                if (childNode instanceof OdfElement) {
                    cloneElement.appendChild(((OdfElement) childNode).cloneNode(depth - 1));
                }
                childNode = childNode.getNextSibling();
            }
        }
        // ToDo: There should be an easier - more obvious - way than this...
        if (this.selfAndDescendantTextIgnoredAsComponent()) {
            cloneElement.ignoredComponent(true);
        }
        cloneElement.mComponentSize = this.mComponentSize;
        return cloneElement;
    }

    /**
     * Clones the content of the source element including attributes even xml:id
     * to the target element. Helpful when changing a <text:h> to a <text:p> and
     * vice versa, when outline attribute changes.
     *
     * @param source the element to copy the content & attributes from.
     * @param target the element to copy the content & attributes into.
     * @param deep if a deep copy should happen. If false only the source
     * element attributes will be copied, otherwise all descendants.
     * @return the target element with all new nodes
     */
    // ToDo: Test if a parameter by reference isn't working here!
    public static OdfElement cloneNode(OdfElement source, OdfElement target, boolean deep) {
        NamedNodeMap attributes = source.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String qname;
                String prefix = item.getPrefix();
                if (prefix == null) {
                    qname = item.getLocalName();
                    target.setAttribute(qname, item.getNodeValue());
                } else {
                    qname = prefix + ":" + item.getLocalName();
                    target.setAttributeNS(item.getNamespaceURI(), qname, item.getNodeValue());
                }
            }
        }

        // aside of the XML the flag of being a component root have to be copied
        if (source.isComponentRoot) {
            target.markAsComponentRoot(true);
            target.mComponentSize = source.mComponentSize;
            target.mComponent = source.mComponent;
        }

        if (deep) {
            Node childNode = source.getFirstChild();
            Node newNode;
            while (childNode != null) {
                if (childNode instanceof OdfElement) {
                    newNode = ((OdfElement) childNode).cloneNode(true);
                } else {
                    newNode = childNode.cloneNode(true);
                }
                target.appendChild(newNode);

                childNode = childNode.getNextSibling();
            }
        }
        // ToDo: There should be an easier - more obvious - way than this...
        if (source.selfAndDescendantTextIgnoredAsComponent()) {
            target.ignoredComponent(true);
        }
        target.mComponentSize = source.mComponentSize;
        return target;
    }

    @Override
    public Node appendChild(Node node) {
        // No Counting necessary as appendChild() will call insertBefore()
        if(node instanceof OdfElement){
            OdfElement e = (OdfElement) node;
        }
        return super.appendChild(node);
    }

    /**
     * Recursive traverse the potential text container and count its content
     * size
     */
    private static int descendantsCount(Node parent, int size) {
        if (!isIgnoredElement((Element) parent)) {
            NodeList children = parent.getChildNodes();
            Node child;
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (child instanceof Text) {
                    size += ((Text) child).getLength();
                } else if (child instanceof OdfElement) {
                    OdfElement element = (OdfElement) child;
                    if (Component.isTextSelection(element)) {
                        size += descendantsCount(element, size);
                    } else if (element.isComponentRoot()) {
                        size += element.componentSize();
                    }
                }
            }
        }
        return size;
    }

    /**
     * Recursive traverse the text container and count the size of the content
     */
    public int componentSize() {
//////SVANTE CLEAN ME
//		if(mComponentSize == null){
//			if(isComponentRoot()){
//				mComponentSize = 1;
//			}else{
//				mComponentSize = 0;
//			}
//		}
        return mComponentSize;
    }

    /**
     * indicates if some other object is equal to this one.
     *
     * @param obj - the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false
     * otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || !(obj instanceof OdfElement)) {
            return false;
        }

        OdfElement compare = (OdfElement) obj;

        // compare node name
        if (!localName.equals(compare.localName)) {
            return false;
        }

        if (!this.namespaceURI.equals(compare.namespaceURI)) {
            return false;
        }

        // compare node attributes
        if (attributes == compare.attributes) {
            return true;
        }

        if ((attributes == null) || (compare.attributes == null)) {
            return false;
        }

        int attr_count1 = attributes.getLength();
        int attr_count2 = compare.attributes.getLength();

        List<Node> attr1 = new ArrayList<Node>();
        for (int i = 0; i < attr_count1; i++) {
            Node node = attributes.item(i);
            if (node.getNodeValue().length() == 0) {
                continue;
            }
            attr1.add(node);
        }

        List<Node> attr2 = new ArrayList<Node>();
        for (int i = 0; i < attr_count2; i++) {
            Node node = compare.attributes.item(i);
            if (node.getNodeValue().length() == 0) {
                continue;
            }
            attr2.add(node);
        }

        if (attr1.size() != attr2.size()) {
            return false;
        }

        for (int i = 0; i < attr1.size(); i++) {
            Node n1 = attr1.get(i);
            if (n1.getLocalName().equals("name")
                && n1.getNamespaceURI().equals(
                    OdfDocumentNamespace.STYLE.getUri())) {
                continue; // do not compare style names
            }
            Node n2 = null;
            int j = 0;
            for (j = 0; j < attr2.size(); j++) {
                n2 = attr2.get(j);
                if (n1.getLocalName().equals(n2.getLocalName())) {
                    String ns1 = n1.getNamespaceURI();
                    String ns2 = n2.getNamespaceURI();
                    if (ns1 != null && ns2 != null && ns1.equals(ns2)) {
                        break;
                    }
                }
            }
            if (j == attr2.size()) {
                return false;
            }

            if (!n1.getTextContent().equals(n2.getTextContent())) {
                return false;
            }
        }

        // now compare child elements
        NodeList childs1 = this.getChildNodes();
        NodeList childs2 = compare.getChildNodes();

        int child_count1 = childs1.getLength();
        int child_count2 = childs2.getLength();
        if ((child_count1 == 0) && (child_count2 == 0)) {
            return true;
        }

        List<Node> nodes1 = new ArrayList<Node>();
        for (int i = 0; i < child_count1; i++) {
            Node node = childs1.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                if (node.getNodeValue().trim().length() == 0) {
                    continue; // skip whitespace text nodes
                }
            }
            nodes1.add(node);
        }

        List<Node> nodes2 = new ArrayList<Node>();
        for (int i = 0; i < child_count2; i++) {
            Node node = childs2.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                if (node.getNodeValue().trim().length() == 0) {
                    continue; // skip whitespace text nodes
                }
            }
            nodes2.add(node);
        }

        if (nodes1.size() != nodes2.size()) {
            return false;
        }

        for (int i = 0; i < nodes1.size(); i++) {
            Node n1 = nodes1.get(i);
            Node n2 = nodes2.get(i);
            if (!n1.equals(n2)) {
                return false;
            }
        }
        return true;
    }

    protected void onRemoveNode(Node node) {
        if (node != null) {
            Node child = node.getFirstChild();
            while (child != null) {
                this.onRemoveNode(child);
                child = child.getNextSibling();
            }

            if (OdfElement.class.isInstance(node)) {
                ((OdfElement) node).onRemoveNode();
            }
        }
    }

    protected void onInsertNode(Node node) {
        Node child = node.getFirstChild();
        while (child != null) {
            this.onInsertNode(child);
            child = child.getNextSibling();
        }

        if (OdfElement.class.isInstance(node)) {
            ((OdfElement) node).onInsertNode();
        }
    }

    protected void onRemoveNode() {
    }

    protected void onInsertNode() {
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        Node n = null;
        onInsertNode(newChild);
        n = super.insertBefore(newChild, refChild);
        raiseComponentSize(newChild);
        return n;
    }


    /* Removes the element from the DOM tree, but keeping its ancestors by moving its children in its place */
    public static Element removeSingleElement(Element oldElement) throws DOMException {
        Element parent = (Element) oldElement.getParentNode();
        if (parent != null) {
            NodeList children = oldElement.getChildNodes();
            int childCount = children.getLength();
            Node lastChild = children.item(childCount - 1);
            parent.replaceChild(lastChild, oldElement);
            Node newChild;
            for (int i = childCount - 2; i >= 0; i--) {
                newChild = children.item(i);
                parent.insertBefore(newChild, lastChild);
                lastChild = newChild;
            }
        }
        return parent;
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        onRemoveNode(oldChild);
        reduceComponentSize(oldChild);
        return super.removeChild(oldChild);
    }

    /**
     * Component size is being reduced by the size of the child
     */
    private void reduceComponentSize(Node child) {
        if (child instanceof Text) {
            changeSize(-1 * ((Text) child).getLength());
        } else if (child instanceof Element) {
            if (Component.isTextComponentRoot(child)) {
                changeSize(-1);
            } else {
                changeSize(-1 * descendantsCount(child, 0));
            }
        }
    }

    /**
     * Returns if the text should be returned or is under a nested paragraph or
     * ignored text element (e.g. text:note-citation).
     */
    private boolean isIgnoredText(OdfElement parent) {
        boolean isIgnored = true;
        while (parent != null) {
            if (parent.isComponentRoot() || parent.selfAndDescendantTextIgnoredAsComponent()) {
                if (parent.selfAndDescendantTextIgnoredAsComponent()) {
                    isIgnored = true;
                } else {
                    isIgnored = false;
                }
                break;
            }
            parent = parent.getParentAs(OdfElement.class);
        }
        return isIgnored;
    }

    /**
     * @return true if the element does represent multiple instances. (only
     * applicable for some elements as cell or row).
     */
    public boolean isRepeatable() {
        return Boolean.FALSE;
    }

    /**
     * @return the repetition the element represents, by default it is 1
     */
    public int getRepetition() {
        return 1;
    }

    /**
     * Component size is being reduced by the size of the child
     */
    private void raiseComponentSize(Node child) {
        if (child instanceof Text) {
            if (!isIgnoredText((OdfElement) child.getParentNode())) {
                changeSize(((Text) child).getLength());
            }
        } else if (child instanceof Element) {
            if (child instanceof OdfElement && ((OdfElement) child).isComponentRoot()) {
                if (!((OdfElement) child).selfAndDescendantTextIgnoredAsComponent()) {
					// in theory 1 is the default and repeated factors and space count factors should be applied
                    // it is something different to size (which returns the content size, instead it is like a width?)

                    // Elements with repetition are: text:s, table:table-cell and table:table-row
                    int repetition = ((OdfElement) child).getRepetition();
                    if (repetition != 1) {
                        changeSize(repetition);
                    } else {
                        changeSize(1);
                    }
                }
//				// SPECIAL HANDLING FOR FRAME/IMAGE COMPONENT
//			} else if (child instanceof DrawImageElement) {
//				Node precedingImageSibling = null;
//				precedingImageSibling = child;
//				boolean frameAlreadyCount = false;
//				while ((precedingImageSibling = precedingImageSibling.getPreviousSibling()) != null) {
//					if (precedingImageSibling instanceof DrawImageElement) {
//						frameAlreadyCount = true;
//					}
//				}
//				if (!frameAlreadyCount) {
//					Node parent = child.getParentNode();
//					if (parent != null) {
//						Node grandParent = parent.getParentNode();
//						if (grandParent != null) {
//							((OdfElement) grandParent).changeSize(1);
//						}
//					}
//				}
            } else {
                // ToDo: Improvement: we may limit to elements that may be text delimiter (span) or inbetween a text container and text delimiter (for ongoing recursion)
                changeSize(descendantsCount(child, 0));
            }
        }
    }

    /**
     * A change of the element will be raised to the top till a component is
     * found
     */
    // ToBeMoved to TEXT CONTINER CHILDREN only!
    private int changeSize(int sizeDifference) {
        int size = 0;
        if (sizeDifference != 0) {
            OdfElement element = this;
            if (!element.isComponentRoot() && !isIgnoredElement(element)) {
                do {
                    element = element.getParentAs(OdfElement.class);
                } while (element != null && !element.isComponentRoot() && !isIgnoredElement(element));
            }
            if (element.isComponentRoot()) {
                element.mComponentSize = element.componentSize() + sizeDifference;
                size = element.mComponentSize;
//				element.mComponentSize += sizeDifference;
//				size = element.mComponentSize;

            }
        }
        return size;
    }

    /**
     * Removes all the content from the element
     */
    public void removeContent() {
        // ToDo: Remove Component List Structure -- The first loop is only temporary
        for (int i = 0; i < mComponent.size(); i++) {
            // remove component
            mComponent.remove(i);
        }
        mComponentSize = 0;
        NodeList children = this.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child != null) {
                this.removeChild(child);
            }
        }
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        raiseComponentSize(newChild);
        onRemoveNode(oldChild);
        onInsertNode(newChild);
        //Currently the replace only reduces
        //reduceComponentSize(oldChild);
        return super.replaceChild(newChild, oldChild);
    }

    /**
     * Accept an visitor instance to allow the visitor to do some operations.
     * Refer to visitor design pattern to get a better understanding.
     *
     * @param visitor	an instance of DefaultElementVisitor
     */
    public void accept(ElementVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the component reference
     */
    public OdfElement getComponentRoot() {
        OdfElement element = this;
        if (!element.isComponentRoot()) {
            do {
                element = element.getParentAs(OdfElement.class);
            } while (element != null && !element.isComponentRoot());
        }
        return element;
    }

    public void markAsComponentRoot(boolean isRoot) {
        isComponentRoot = true;
        OdfElement parent = (OdfElement) getParentNode();
		// if the component was already added, addChild the size now
//		if(parent != null && this instanceof DrawFrameElement){
//			parent.raiseComponentSize(this);
//		}
    }

    public Component getComponent() {
        return mComponent;
    }

    public void setComponent(Component component) {
        mComponent = component;
    }

    public boolean isComponentRoot() {
        return isComponentRoot;
    }

    /**
     * @return true if the text should not count as for component path nor the
     * element root itself. This might occur for nested paragraphs or ignored
     * text element (e.g. text:note-citation).
     */
    public boolean selfAndDescendantTextIgnoredAsComponent() {
        return mIsIgnoredComponent;
    }

    /**
     * @param true if the text should not count as for component path nor the
     * element root itself. This might occur for nested paragraphs or ignored
     * text element (e.g. text:note-citation). For instance called by a SAX
     * Component parser, * * * * * * * see
     * <code>org.odftoolkit.odfdom.component.OdfFileSaxHandler</code>
     */
    public void ignoredComponent(boolean isIngoredComponent) {
        mIsIgnoredComponent = isIngoredComponent;
    }

    /**
     * If the string is inserted into a text:p/text:h element and it will be
     * inserted in the start/end all spaces are replaced by <text:s/>
     * element(s). tabulator and linefeeds are being removed.
     *
     * If both the previous text node ends with a space and newString starts
     * with a space, we would need to encode the single leading space as an
     * element, otherwise it would be stripped. Same occurs for the next text
     * node and an ending space. For Example:
     * <span> text </span><text:s c="7"/><span> text2 </span> <== SAVE when
     * starting ending a span as well with space element independent of
     * preceding
     */
    static protected void appendUsingWhitespaceHandling(Node precedingNode, OdfElement parent, Node followingNode, String newString) {
//		addTextNode(precedingNode, parent, followingNode, newString);
        // only addChild text, if parent exists
        if (parent != null) {
//            // if there is only one span in a paragraph/heading
//            if(parent instanceof TextParagraphElementBase){
//                // add the new content to this span
//                NodeList children = parent.getChildNodes();
//                if(children.getLength() == 1){
//                    Node child = children.item(0);
//                    if(child instanceof TextSpanElement){
//                        parent = (OdfElement) child;
//                    }
//                }
//            }
            int spaceCount = 0;
            // Note: The delta between startPosition and endPosition marks the text to be written out
            // startPosition will only be raised to endposition, when characters have to be skipped!
            int startPos = 0;
            int endPos = 0;
            // check if first character is a white space
            for (int i = 0; i < newString.length(); i++) {
                char c = newString.charAt(i);
                if (c == '\u0020' // space
                    || c == '\t' // \t (tabulator = 0x09)
                    // \r (carriage return = 0x0D)
                    || c == '\r'
                    // \n (line feed =' 0x0A)
                    || c == '\n') {
                    spaceCount++;

                    if (spaceCount > 1) {
                        // if there are more than one space a space element have to be inserted, write out the previous spaces
                        if (endPos - startPos > 0) {
                            precedingNode = addTextNode(precedingNode, parent, followingNode, newString.substring(startPos, endPos));
                            // for the single whitespace not written
                        }
                        // NOT including the additional whitespace character
                        startPos = endPos;
                    }
                } else { // else if there was no whitespace character found or at the beginning
                    if (spaceCount > 1 || i == 1 && spaceCount == 1) {
                        TextSElement s = new TextSElement((OdfFileDom) parent.getOwnerDocument());
                        // if there were multiple preceing whitespace, write out a space Element
                        if (spaceCount > 1) {
                            s.setTextCAttribute(spaceCount);
                        }

                        // write out space element
                        precedingNode = addElementNode(precedingNode, parent, followingNode, s);
                        endPos += spaceCount;
                        startPos = endPos;
                        spaceCount = 0;

                        // reset space count to zero as now a character was found
                    } else if (spaceCount == 1) {
                        endPos++;
                        spaceCount = 0;
                    }

                    // write out character
                    endPos++;// including this character
                }
            }
            // reset space count to zero as now a character was found
            if (spaceCount > 1) {
                TextSElement s = new TextSElement((OdfFileDom) parent.getOwnerDocument());
                // if there were multiple preceing whitespace, write out a space Element
                if (spaceCount > 1) {
                    s.setTextCAttribute(spaceCount);
                }
                // write out space element
                precedingNode = addElementNode(precedingNode, parent, followingNode, s);
                endPos += spaceCount;
                startPos = endPos;
                spaceCount = 0;
            }
            if (endPos - startPos > 0) {
                precedingNode = addTextNode(precedingNode, parent, followingNode, newString.substring(startPos, endPos));
            }
            if (spaceCount == 1) {
                TextSElement s = new TextSElement((OdfFileDom) parent.getOwnerDocument());
                addElementNode(precedingNode, parent, followingNode, s);
            }
        } else {
            Logger.getLogger(OdfElement.class.getName()).log(Level.SEVERE, "Node parent should not be NULL!");
        }
    }

    private static Node addElementNode(Node precedingNode, OdfElement parent, Node followingNode, Element newElement) {
        Node newNode = null;
        // APPEND: if there is no text node to expand and no element that follows
        if (followingNode == null) {
            newNode = parent.appendChild(newElement);
        } else if (followingNode != null) {
            // insert before the given element
            newNode = parent.insertBefore(newElement, followingNode);
        }
        return newNode;

    }

    private static Node addTextNode(Node precedingNode, OdfElement parent, Node followingNode, String newString) {
        Node newNode = null;
        // APPEND: if there is no text node to expand and no element that follows
        if (precedingNode == null && followingNode == null || precedingNode != null && precedingNode instanceof Element && followingNode == null) {
            newNode = parent.appendChild(parent.getOwnerDocument().createTextNode(newString));
        } else {
            // INSERT:
            if (precedingNode != null && precedingNode instanceof Text) {
                // insert at the end of the given text
                ((Text) precedingNode).appendData(newString);
                newNode = precedingNode;
            } else if (followingNode != null) {
                // insert before the given element
                newNode = parent.insertBefore(parent.getOwnerDocument().createTextNode(newString), followingNode);
            }
        }
        return newNode;
    }

    private static Node addElementNode(Node precedingNode, OdfElement parent, Node followingNode, OdfElement newElement) {
        Node newNode = null;
        // APPEND: if there is no text node to expand and no element that follows
        if (followingNode == null) {
            newNode = parent.appendChild(newElement);
        } else {
            // INSERT:
            // insert before the given following-node
            newNode = parent.insertBefore(newElement, followingNode);
        }
        return newNode;
    }

    /**
     * Splitting the element at the given position into two halves
     *
     * @param posStart The logical position of the first character (or other
     * paragraph child component) that will be moved to the beginning of the new
     * paragraph. Counting starts with 0.
     * @return the new created second text container
     */
    public OdfElement split(int posStart) {
        OdfElement newSecondElement = this;
        // split with 0 is allowed. For instance to create new paragraphs!
        if (posStart > -1) {
            newSecondElement = (OdfElement) this.cloneNode(true);
            int size = OdfElement.getContentSize(this);

            // This will become the first paragraph
            // Only delete if the start node is within the component length
            // Do NOT do a a
            // if there is only one character the size is 1 and the textPosStart would be 0
//TODO FIXME: Why was the parent DELETEd, when next line was after the condition??!?
            Element parent = (Element) this.getParentNode();
            if (size > posStart) {
                this.delete(posStart, size);
            }
            Node _nextSibling = this.getNextSibling();
            if (_nextSibling != null) {
                parent.insertBefore(newSecondElement, _nextSibling);
            } else {
                parent.appendChild(newSecondElement);
            }

            // only delete if the start position is not before the first component
            if (posStart != 0) {
                // minus one, as the textPosStart was already in the first Element
                newSecondElement.delete(0, posStart - 1);
            }
        }
        // FIXME: There is a better way than cloning and deleting two halves, but it works for POC
        //splitNodes(this.getFirstChild(), textPosStart, 0);
        return newSecondElement;

    }

    /**
     * ********************************************************
     */
    /**
     * Receives node from this text container element.
     *
     * @param textPosStart The start delimiter for the child
     * @return the child node might be text or element
     */
    public Node receiveNode(int textPosStart) {
        if (textPosStart < 0) {
            Logger.getLogger(OdfElement.class.getName()).warning("A negative index " + textPosStart + " was given to insert text into the paragraph!");
        }
        // start recrusion
        ArrayList<Node> nodeContainer = new ArrayList<Node>(1);
        boolean withinTextContainer = this instanceof TextPElement || this instanceof TextHElement;
        TextContentTraverser.traverseSiblings(this.getFirstChild(), 0, textPosStart, textPosStart + 1, TextContentTraverser.Algorithm.RECEIVE, nodeContainer, withinTextContainer);

        Node receivedNode = null;
        if (nodeContainer.size() == 1) {
            receivedNode = nodeContainer.get(0);
        }
        return receivedNode;
    }

    /**
     * @param textPosStart the first text level component to be marked, start
     * counting with 0
     * @param textPosEnd the last text level component to be marked, start
     * counting with 0
     * @param newSelection the element that should embrace the text defined by
     * the positions provided
     */
    public void markText(int textPosStart, int textPosEnd, JSONObject formatChanges) {
        if (formatChanges != null) {
            if (textPosStart < 0) {
                Logger.getLogger(OdfElement.class.getName()).warning("A negative index " + textPosStart + " was given to insert text into the paragraph!");
            }
            if (textPosEnd < textPosStart) {
                // might be caused by invalid span around whitespace that is being eleminated by ODF whitespacehandling
                Logger.getLogger(OdfElement.class.getName()).warning("The start index " + textPosStart + " shall not be higher than the end index " + textPosEnd + "!");
            }
            // incrementing textPosEnd to get in sync with string counting
            TextContentTraverser.traverseSiblings(this.getFirstChild(), 0, textPosStart, textPosEnd + 1, TextContentTraverser.Algorithm.MARK, formatChanges, new HashMap<OdfName, OdfElement>());
        }
    }

    /**
     * Counts the number of descendant components
     */
    public int countDescendantComponents() {
        return TextContentTraverser.traverseSiblings(this.getFirstChild(), 0, 0, Integer.MAX_VALUE, TextContentTraverser.Algorithm.COUNT, Integer.MAX_VALUE, Boolean.TRUE);
    }

    /**
     * Counts the number of child components
     */
    public int countChildComponents(Boolean hasTextComponents) {
        return TextContentTraverser.traverseSiblings(this.getFirstChild(), 0, 0, Integer.MAX_VALUE, TextContentTraverser.Algorithm.COUNT, Integer.MAX_VALUE, hasTextComponents);
    }

// Below recursion works, the new one not yet..
//	public void moveChildrenTo(Element newParent) {
//		// incrementing textPosEnd to get in sync with string counting
//		TextContentTraverser.traverseSiblings(this.getFirstChild(), 0, 0, Integer.MAX_VALUE, TextContentTraverser.Algorithm.MOVE, newParent);
//	}
    public void moveChildrenTo(Element newParent) {
        moveNodes(this.getFirstChild(), newParent);
    }

    private void moveNodes(Node node, Element newParent) {
        while (node != null) {
            // IMPORTANT: Get next sibling first, otherwise references get lost when appending to new parent
            Node _nextSibling = node.getNextSibling();
            if (node instanceof Element) {
                newParent.appendChild(node);
            } else if (node instanceof Text) {
                moveTextNode((Text) node, newParent);
            }
            node = _nextSibling;
        }
    }

    private void moveTextNode(Text node, Element newParent) {
        if (node != null) {
            newParent.appendChild(node);
        }
    }

    /**
     * Insert text to a certain position. The text will be appended to the
     * previous position text, so the span of the previous character will be
     * expanded
     *
     * @param newString string to be inserted
     * @param position text index of the new string
     */
    public void insert(String newString, int textPosStart) {
        if (newString != null && !newString.isEmpty()) {
            insertContent(newString, textPosStart);
        }
    }

    public void insert(Node newNode, int textPosStart) {
        if (newNode != null) {
            insertContent(newNode, textPosStart);
        }
    }

    /**
     * Insert text to a certain position. The text will be appended to the
     * previous position text, so the span of the previous character will be
     * expanded
     *
     * @param newString string to be inserted
     * @param position text index of the new string
     */
    private void insertContent(Object content, int textPosStart) { // parameter order?
        if (textPosStart < 0) {
            Logger.getLogger(OdfElement.class.getName()).warning("A negative index " + textPosStart + " was given to insert text into the paragraph!");
        }
        Node firstChild = this.getFirstChild();
        if (firstChild == null) {
            // if there is no new node, simply exchange multiple whitespaces with <text:s>
            if (content instanceof String) {
                appendUsingWhitespaceHandling(null, this, null, (String) content);
            } else if (content instanceof Element) {
                this.appendChild((Element) content);
            }
        } else {
            List<Object> newData = new ArrayList<Object>(2);
            newData.add(content);
            int currentPos = TextContentTraverser.traverseSiblings(firstChild, 0, textPosStart, textPosStart, TextContentTraverser.Algorithm.INSERT, newData);
            if (newData.size() == 1) {
                // if there is were element(s), but no components within this element
                if (content instanceof String) {
                    appendUsingWhitespaceHandling(null, this, null, (String) content);
                } else if (content instanceof Element) {
                    this.appendChild((Element) content);
                }
            }
//				if (currentPos > textPosStart) {
//					Logger.getLogger(OdfElement.class.getName()).warning("The index " + textPosStart + " is outside the existing text of the paragraph!");
//				}
        }
    }

    /**
     * Deletes text from this paragraph element.
     *
     * @param textPosStart Counting starts with 0, which is the first character
     * of the paragraph.
     * @param textPosEnd The end delimiter for the deletion. To delete text to
     * the end of the paragraph, as represent for the end of the paragraph
     * Integer.MAX_VALUE can be used.
     */
    public void delete(int textPosStart, int textPosEnd) {
        if (textPosStart < 0) {
            Logger.getLogger(OdfElement.class.getName()).warning("A negative index " + textPosStart + " was given to insert text into the paragraph!");
        }
        if (textPosEnd < textPosStart) {
            Logger.getLogger(OdfElement.class.getName()).warning("The start index " + textPosStart + " have to be higher than the end index " + textPosEnd + "!");
        }

        /**
         * Deletion implementation: 1) The text position of the first deletion
         * will be searched. 2) Afterwards all following-sibling, text and nodes
         * will be deleted, until the end text position is found 3) Split the
         * text take the returned and remove it from parent
         */
        List deleteStatus = new ArrayList(1);

        // start recrusion
        TextContentTraverser.traverseSiblings(this.getFirstChild(), 0, textPosStart, textPosEnd + 1, TextContentTraverser.Algorithm.DELETE, deleteStatus);
    }

    private static class TextContentTraverser {

        /**
         *
         * @param node the element node will be checked if it is a text or an
         * element. If an element it will be dispatched to check component than
         * executed
         * @param currentPos the current component position
         * @param posStart the text position where the span starts
         * @param posEnd the text position, where the span ends (one higher as
         * the last component number to be included)
         * @param algorithm dependent on this variable a different subroutine is
         * being used after traversing the sub-tree, e.g. insert, delete, mark,
         * count..
         * @param data differs from the type of algorithm, e.g. for insert it
         * contains the data to be inserted
         * @return the current position after the node was processed
         */
        static private int traverseSiblings(Node node, int currentPos, int posStart, int posEnd, Algorithm algorithm, Object... data) {
            // loop to take over components into the span & split them if required, until final position was reached!
            if (algorithm.equals(Algorithm.DELETE)) {
                // position equal as after the last found even unknown components will be deleted
                while (node != null && currentPos <= posEnd) {
                    // IMPORTANT: get next sibling first, otherwise references get lost by Xerces during splitting
                    Node _nextSibling = node.getNextSibling();
                    if (node instanceof Element) {
// ** THE COMMENTED CODE BELOW REMOVED A COMPONENT, WHICH BECAME EMPTY AFTER DELETING SOME CONTENT.
// ** THIS IS NOW CONSIDERED HARMFUL, AS PARAGRAPH/HEADING/TABLE ALWAYS REMAIN
//						int countBefore = ((List) data).size();
                        currentPos = checkElementNode((Element) node, currentPos, posStart, posEnd, algorithm, data[0]);
                        // OPTIMIZATION: the data is being used as flag to realize, when the element being checked does not contain any component
//						List deleteStatus = (List) data;
//						int countAfter = deleteStatus.size();
//						if (countBefore < countAfter && (Boolean) deleteStatus.get(0) && ((OdfElement) node).countDescendantComponents() == 0) {
//							Element parent = (Element) node.getParentNode();
//							if (parent != null) {
//								// delete the empty boilerplate
//								parent.removeChild(node);
//								// reset the status
//								deleteStatus.clear();
//							}
//						}
                    } else if (node instanceof Text) {
                        currentPos = algorithm.execute(node, currentPos, posStart, posEnd, data[0]);
                    }
                    // next sibling will be checked
                    node = _nextSibling;
                }
            } else if (algorithm.equals(Algorithm.INSERT)) {

                // position equal as it could be inserted before on 0th (first) place
                while (node != null && (currentPos < posEnd || (posEnd == 0 && currentPos == 0))) {
                    // IMPORTANT: get next sibling first, otherwise references get lost by Xerces during splitting
                    Node _nextSibling = node.getNextSibling();
                    if (node instanceof Element) {
                        currentPos = checkElementNode((Element) node, currentPos, posStart, posEnd, algorithm, data[0]);
                    } else if (node instanceof Text) {
                        currentPos = algorithm.execute(node, currentPos, posStart, posEnd, data[0]);
                    }
                    // next sibling will be checked
                    node = _nextSibling;
                }
            } else if (algorithm.equals(Algorithm.COUNT)) {
                while (node != null && (currentPos < posEnd || (posEnd == 0 && currentPos == 0))) {
                    // IMPORTANT: get next sibling first, otherwise references get lost by Xerces during splitting
                    Node _nextSibling = node.getNextSibling();
                    if (node instanceof Element) {
                        currentPos = checkElementNode((Element) node, currentPos, posStart, posEnd, algorithm, data[0], data[1]);
                    } else if (node instanceof Text && (Boolean) data[1]) {
                        currentPos = algorithm.execute(node, currentPos, posStart, posEnd, data[0], data[1]);
                    }
                    // next sibling will be checked
                    node = _nextSibling;
                }
            } else if (algorithm.equals(Algorithm.MARK)) {
                while (node != null && currentPos < posEnd) {
                    // IMPORTANT: get next sibling first, otherwise references get lost by Xerces during splitting
                    Node _nextSibling = node.getNextSibling();
                    currentPos = algorithm.execute(node, currentPos, posStart, posEnd, data[0], data[1]);
                    node = _nextSibling;
                }
            } else {
                boolean withinTextContainer = (Boolean) data[1];
                while (node != null && currentPos < posEnd) {
                    // IMPORTANT: get next sibling first, otherwise references get lost by Xerces during splitting
                    Node _nextSibling = node.getNextSibling();
                    // ToDo: || algorithm.equals(Algorithm.MOVE)
                    if (node instanceof Element) {
                        // if not yet marked as text container (text traversering enabled), check if passing a container
                        if (withinTextContainer == false) {
                            if (node instanceof TextPElement || node instanceof TextHElement) {
                                data[1] = Boolean.TRUE;
                            }
                        }
                        currentPos = checkElementNode((Element) node, currentPos, posStart, posEnd, algorithm, data);
                    } else if (node instanceof Text && withinTextContainer) {
                        currentPos = algorithm.execute(node, currentPos, posStart, posEnd, data);
                    }
                    // next sibling will be checked
                    node = _nextSibling;
                }
            }
            return currentPos;
        }

        /**
         *
         * @param currentNode the element currentNode will be checked if it is a
         * component. If a component it will be executed fully or partly moved
         * into the span
         * @param currentPos the current component position
         * @param posStart the text position where the span starts
         * @param posEnd the text position, where the span ends (one higher as
         * the last component number to be included)
         * @param newSpan the span collecting the marked components
         * @return
         */
        static private int checkElementNode(Element currentNode, int currentPos, int posStart, int posEnd, Algorithm algorithm, Object... data) {
            if (currentNode != null && !OdfElement.isIgnoredElement(currentNode)) {
                if (currentNode instanceof OdfElement && ((OdfElement) currentNode).isComponentRoot() && !((OdfElement) currentNode).mIsIgnoredComponent) {
                    currentPos = algorithm.execute(currentNode, currentPos, posStart, posEnd, data);
                } else {
                    // if element is no component, neglect the element (e.g. another <text:span>, but analyze its content
                    Node firstChild = currentNode.getFirstChild();
                    if (firstChild != null) {
                        currentPos = traverseSiblings(firstChild, currentPos, posStart, posEnd, algorithm, data);
                    }
                }
            }
            return currentPos;
        }

        /**
         * @param content the content to be formatted. Will be moved from its
         * former parent into the span/anchor.
         * @param _nextSibling the span will be added in front of the sibling or
         * appended to the parent if the sibling is NULL
         * @param formatChanges the format changes to be applied to the content
         * will be moved to
         */
        static private void formatContent(Node content, Node _nextSibling, JSONObject formatChanges, Map<OdfName, OdfElement> formatElementHolder) {
            OdfFileDom xmlDoc = (OdfFileDom) content.getOwnerDocument();
            // 2DO: What if I have an anchor AND an span? we have to add it one by one first anchor
            JSONObject charFormatChanges = formatChanges.optJSONObject("character");
            // if a reference should be added for the given content
            TextAElement newAElement = null;
            // add an anchor with hyperlink if necessary
            if (charFormatChanges != null && charFormatChanges.has("url") && !charFormatChanges.get("url").equals(JSONObject.NULL)) {
                newAElement = getAnchorElement((Node) content, xmlDoc, charFormatChanges.optString("url"), formatElementHolder);
                // if an anchor was reused, do not add it
                if (!newAElement.equals(content)) {
                    addNewParent(content, _nextSibling, newAElement);
                }
                _nextSibling = null;
            }

            if (newAElement != null
                    // NOTE: Calc Issue workaround: No span within anchors possible in OpenOffice CALC (no text will be shown)
            		&& (xmlDoc.getDocument() instanceof OdfSpreadsheetDocument
    	            // NOTE: Annotation workaround: No span within anchors possible in Annotations (no text will be shown)
            		|| isOfficeAnnotationChild(newAElement))) {
                NodeList innerSpans = newAElement.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "span");
                int spanCount = innerSpans.getLength();
                for (int i = spanCount - 1; i >= 0; i--) {
                    TextSpanElement innerSpan = (TextSpanElement) innerSpans.item(i);
                    OdfElement.removeSingleElement(innerSpan);
                }
            } else {
                // the changes might be only deletion OR only addition OR a mix of both
                TextSpanElement newSpanElement = getSpanElement(xmlDoc, formatChanges, formatElementHolder);
                // if a new span exist
                if (newSpanElement != null) {
                    if (!newSpanElement.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name")) {
                        newSpanElement = null;
                    } else {
                        addNewParent(content, _nextSibling, newSpanElement);
                    }

                }
                formatDescendants(content, newSpanElement, charFormatChanges != null && charFormatChanges.has("url"), xmlDoc, formatChanges, formatElementHolder);
            }
        }

        // Annotation workaround: No span within anchors possible in Annotations (no text will be shown)
        private static boolean isOfficeAnnotationChild(NodeImpl element) {
        	return isOfficeAnnotationChild(element, 4);
        }

        private static boolean isOfficeAnnotationChild(NodeImpl element, int rec) {
        	if (null != element) {
        		if (element instanceof OfficeAnnotationElement) {
        			return true;
        		} else {
        			if (rec > 0 && element instanceof OdfElement) {
        				return isOfficeAnnotationChild(((OdfElement)element).ownerNode, rec - 1);
        			} else {
        				return false;
        			}
        		}
        	} else {
        		return false;
        	}
        }

        private static void formatDescendants(Node content, TextSpanElement newSpan, boolean removeAnchors, OdfFileDom ownerDoc, JSONObject formatChanges, Map<OdfName, OdfElement> formatElementHolder) {
            if (content instanceof TextSpanElement) {
                mergeSpans((TextSpanElement) content, newSpan, ownerDoc, formatChanges, formatElementHolder);
            }
            while (content instanceof TextAElement && removeAnchors) {
                content = removeSingleNode(content, content.getNextSibling());
                if (content instanceof TextSpanElement) {
                    mergeSpans((TextSpanElement) content, newSpan, ownerDoc, formatChanges, formatElementHolder);
                }
            }

            if (content.hasChildNodes()) {
                NodeList children = content.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child instanceof Element) {
                        formatDescendants(child, newSpan, removeAnchors, ownerDoc, formatChanges, formatElementHolder);
                    }
                }
            }
        }

        private static TextSpanElement mergeSpans(TextSpanElement content, TextSpanElement newSpan, OdfFileDom ownerDoc, JSONObject formatChanges, Map<OdfName, OdfElement> formatElementHolder) {
            if (newSpan != null && content != newSpan) {
                String originalStyleName = newSpan.getStyleName();
                content = (TextSpanElement) OdfStyle.mergeSelectionWithSameRange(newSpan, content);
                String mergedStyleName = newSpan.getStyleName();
                // if the style name was altered from the new span being added
                if (!originalStyleName.equals(mergedStyleName)) {
                    // create a new one
                    TextSpanElement originSpan = new TextSpanElement(ownerDoc);
                    // add the original style-name
                    originSpan.setAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "text:style-name", originalStyleName);
                    // add restore it back to the container, for the following containers, so they won't be influenced by the merge
                    formatElementHolder.put(TextSpanElement.ELEMENT_NAME, originSpan);

                }
            }
            JsonOperationConsumer.addStyle(formatChanges, content, ownerDoc);
            return content;
        }

        private static void addNewParent(Node existingChild, Node _nextSibling, OdfElement newParent) {
            Node parent = existingChild.getParentNode();
            parent.removeChild(existingChild);
            newParent.appendChild(existingChild);
            if (_nextSibling != null) {
                parent.insertBefore(newParent, _nextSibling);
            } else {
                parent.appendChild(newParent);
            }
        }

        /**
         * Removes a single node and moves its children in its position
         */
        private static Node removeSingleNode(Node oldNode, Node _nextSibling) {
            Node parent = oldNode.getParentNode();
            Node _firstChild = null;
            if (oldNode.hasChildNodes()) {
                NodeList children = oldNode.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (i == 0) {
                        _firstChild = child;
                    }
                    if (_nextSibling != null) {
                        parent.insertBefore(child, _nextSibling);
                    } else {
                        parent.appendChild(child);
                    }
                }
            }
            parent.removeChild(oldNode);
            return _firstChild;
        }

        /*  */
        static enum Algorithm {

            /**
             */
            INSERT(1),
            /**
             */
            DELETE(2),
            /**
             */
            MARK(3),
            /**
             */
            RECEIVE(4),
            /**
             */
            MOVE(5),
            /**
             */
            COUNT(6);
            private int mId;

            Algorithm(int id) {
                mId = id;
            }

            int execute(Node currentNode, int currentPos, int posStart, int posEnd, Object... content) {
                switch (mId) {
                    case 1:
                        currentPos = insert(currentNode, currentPos, posStart, posEnd, (List) content[0]);
                        break;
                    case 2:
                        currentPos = delete(currentNode, currentPos, posStart, posEnd, (List) content[0]);
                        break;
                    case 3:
                        currentPos = mark(currentNode, currentPos, posStart, posEnd, (JSONObject) content[0], (Map<OdfName, OdfElement>) content[1]);
                        break;
                    case 4:
                        currentPos = receive(currentNode, currentPos, posStart, posEnd, (ArrayList) content[0]);
                        break;
                    case 5:
                        moveChildrenTo(currentNode, currentPos, posStart, posEnd, (Element) content[0]);
                        break;
                    case 6:
                        currentPos = count(currentNode, currentPos, (Boolean) content[1]);
                        break;

                }
                return currentPos;
            }

            /**
             * This function takes a node and moves it fully or partly into the
             * given
             * <text:span/>, if the node components (e.g. character) are within
             * the given posStart and posEnd.
             *
             * @param currentNode	The current child node to
             * @param currentPos the current component position to be checked.
             * starting with 0
             * @param posStart the start position of the span.
             * @param posEnd the end position of the span as string position.
             * NOTE: One higher than the originally given component position.
             * Therefore always one higher than posStart.
             * @param newNode
             * @return the position of the next component to be checked
             */
            int mark(Node currentNode, int currentPos, int posStart, int posEnd, JSONObject formatChanges, Map<OdfName, OdfElement> formatElementHolder) {
                if (currentNode != null) {
                    Integer nextSplitPos;
                    // if the start is equal to the first position avoid the first split
                    if (currentPos >= posStart) {
                        // we are about to gather content in our span
                        nextSplitPos = posEnd;
                    } else {
                        // we have not reached the area to mark
                        nextSplitPos = posStart;
                    }

                    // ** GET NODE SIZE
                    Integer contentLength = getNodeWidth(currentNode);

                    // addChild the full currentNode ==> if the end of the span is equal to the end of the currentNode
                    boolean isTotalSelection = (currentPos + contentLength == nextSplitPos) && nextSplitPos == posEnd;
                    // addChild the middle part of the currentNode ==> if the currentNode already starts within the span, but the end is not within the span
                    boolean isFirstPart = (currentPos >= posStart && currentPos + contentLength < posEnd);
                    // split the currentNode ==> if the next split position is within the currentNode
                    boolean needsSplit = currentPos + contentLength > nextSplitPos;

                    // MOVE PARTS INTO THE SELECTION ELEMENT
                    if (isTotalSelection || isFirstPart) {
                        Node _nextSibling = currentNode.getNextSibling();
                        formatContent(currentNode, _nextSibling, formatChanges, formatElementHolder);
                        currentPos += contentLength;

                        // SPLIT THE NODE
                    } else if (needsSplit) {
                        int secondPartLength = 1;
                        Node secondPart = null;
                        if (currentNode instanceof Text) {
                            // splitCursor is the first character of second part (counting starts with 0)
                            secondPart = ((Text) currentNode).splitText(nextSplitPos - currentPos);
                            secondPartLength = ((Text) secondPart).getLength();
                        } else {
                            //handle component split...
                            secondPart = ((OdfElement) currentNode).split(nextSplitPos - currentPos);
                            secondPartLength = OdfElement.getNodeWidth(secondPart);
                        }
                        boolean reachedStartPosition = nextSplitPos != posEnd;
                        if (reachedStartPosition) {
                            // if the second split is still in the same currentNode..
                            // if the end of the cut is equal to the end of the span

                            if (currentPos + (contentLength - secondPartLength) == posEnd) {
                                // after the split the secondPart is the full fomrat
                                formatContent(secondPart, secondPart.getNextSibling(), formatChanges, formatElementHolder);
                                currentPos += contentLength;
                            } else {
                                // position changed based on the first cut part
                                currentPos = currentPos + (contentLength - secondPartLength);
                                currentPos = mark(secondPart, currentPos, posStart, posEnd, formatChanges, formatElementHolder);
                            }
                        } else {
                            formatContent(currentNode, secondPart, formatChanges, formatElementHolder);
                            currentPos += contentLength - secondPartLength;
                        }
                        // SKIP THE NODE AS SELECTION NOT STARTED
                    } else {
                        currentPos += contentLength;
                    }
                }
                return currentPos;
            }

            int insert(Node currentNode, int currentPos, int posStart, int posEnd, List content) {
                if (currentNode != null) {
                    // // ** GET NODE SIZE
                    // Integer contentLength = getNodeWidth(currentNode);
                    // ** GET NODE SIZE
                    Integer contentLength = 1; // component default is 1
                    if (currentNode instanceof Text) {
                        contentLength = ((Text) currentNode).getLength();
                    } else {
                        // get size from component
                        contentLength = ((OdfElement) currentNode).getRepetition();
                    }
                    // ** There is content to be inserted..
                    if (currentPos == posStart) { // if we are already at the right place, insert content before the currentNode
                        Node parent = currentNode.getParentNode();
                        Object newData = content.get(0);
                        if (newData instanceof String) {
                            OdfElement.appendUsingWhitespaceHandling(null, (OdfElement) parent, currentNode, (String) newData);
                        } else if (content.get(0) instanceof Element) {
                            OdfElement.addElementNode(null, (OdfElement) parent, currentNode, (Element) content.get(0));
                        }
                        // Mark that the content has been added
                        content.add(Boolean.TRUE);
                        currentPos += contentLength;
                    } else if (currentPos + contentLength >= posStart) {
                        // if the complete text node is selected, append behind..
                        if (currentPos + contentLength == posStart) {
                            Object newData = content.get(0);
                            if (newData instanceof String) {
                                OdfElement.appendUsingWhitespaceHandling(currentNode, (OdfElement) currentNode.getParentNode(), currentNode.getNextSibling(), (String) newData);
                            } else if (newData instanceof Element) {
                                OdfElement.addElementNode(currentNode, (OdfElement) currentNode.getParentNode(), currentNode.getNextSibling(), (Element) newData);
                            }
                            // Mark that the content has been added
                            content.add(Boolean.TRUE);
                            currentPos = posStart;
                        } else { // else if only a part of the text node is selected
                            Node secondPart = null;
                            if (currentNode instanceof Text) {
                                // splitCursor is the first character of second part (counting starts with 0)
                                secondPart = ((Text) currentNode).splitText(posStart - currentPos);
                                Object newData = content.get(0);
                                if (newData instanceof String) {
                                    OdfElement.appendUsingWhitespaceHandling(currentNode, (OdfElement) currentNode.getParentNode(), secondPart, (String) newData);
                                } else if (content instanceof Element) {
                                    OdfElement.addElementNode(currentNode, (OdfElement) currentNode.getParentNode(), secondPart, (Element) content);
                                }
                                // Mark that the content has been added
                                content.add(Boolean.TRUE);
                            } else {
                                //handle component split...
                                secondPart = ((OdfElement) currentNode).split(posStart - currentPos);
                                Node parent = currentNode.getParentNode();
                                Object newData = content.get(0);
                                if (newData instanceof String) {
                                    OdfElement.appendUsingWhitespaceHandling(currentNode, (OdfElement) parent, secondPart, (String) newData);
                                } else if (content instanceof Element) {
                                    OdfElement.addElementNode(currentNode, (OdfElement) parent, secondPart, (Element) content);
                                }
                                // Mark that the content has been added
                                content.add(Boolean.TRUE);
                            }
                            currentPos += contentLength;
                        }
                        // ** SKIP THE NODE AS SELECTION NOT STARTED
                    } else {
                        currentPos += contentLength;
                    }
                }
                return currentPos;
            }

            // Within the text node, either the first or the last part have to be deleted (best selectable)
            int delete(Node currentNode, int currentPos, int posStart, int posEnd, List deleteStatus) {

                if (currentNode != null) {
                    Integer nextSplitPos;
                    // if the start is equal to the first position avoid the first split
                    if (currentPos >= posStart) {
                        // we are about to gather content in our span
                        nextSplitPos = posEnd;
                    } else {
                        // we have not reached the area to mark
                        nextSplitPos = posStart;
                    }
                    // ** GET NODE SIZE
                    Integer contentLength = getNodeWidth(currentNode);

                    // addChild the middle part of the currentNode ==> if the currentNode already starts within the span, but the end is not within the span
                    boolean inExecutionMode = currentPos >= posStart && contentLength + currentPos > posStart && currentPos + contentLength <= posEnd;
                    // split the currentNode ==> if the next split position is within the currentNode
                    // We need to search to the next found component, as all unknown components will be deleted after the known
                    boolean needsSplit = currentPos + contentLength > nextSplitPos && nextSplitPos - currentPos != 0;

                    // APPLY ACTION TO PARTS (here delete them)
                    if (inExecutionMode) {
                        OdfElement parent = (OdfElement) currentNode.getParentNode();
                        parent.removeChild(currentNode);
                        deleteStatus.add(0, Boolean.TRUE);

                        int childCount = parent.countDescendantComponents();
                        // remove empty boilerplate
                        while (!parent.isComponentRoot() && childCount == 0) {
                            OdfElement grandParent = (OdfElement) parent.getParentNode();
                            if (grandParent instanceof OfficeBodyElement) {
                                break;
                            }
                            String styleName = parent.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name");
                            // if we are about to remove the last text span of a paragraph..
                            if ((parent instanceof TextSpanElement && (grandParent instanceof TextParagraphElementBase) && childCount == 0 && grandParent.getChildNodes().getLength() == 1 && styleName != null)) {
                                copyTextProperties(styleName, grandParent);
                            }
                            grandParent.removeChild(parent);
                            parent = grandParent;
                            childCount = parent.countDescendantComponents();
                        }
                        currentPos += contentLength;

                        // SPLIT THE NODE
                    } else if (needsSplit) {
                        int secondPartLength = 1;
                        Node secondPart = null;
                        if (currentNode instanceof Text) {
                            // splitCursor is the first character of second part (counting starts with 0)
                            secondPart = ((Text) currentNode).splitText(nextSplitPos - currentPos);
                            secondPartLength = ((Text) secondPart).getLength();
                        } else {
                            //handle span split...
                            secondPart = ((OdfElement) currentNode).split(nextSplitPos - currentPos);
                            secondPartLength = OdfElement.getNodeWidth(secondPart);
                        }
                        boolean reachedStartPosition = nextSplitPos != posEnd;
                        if (reachedStartPosition) {
                            // if the second split is still in the same currentNode..
                            // if the end of the cut is equal to the end of the span
                            if (currentPos + (contentLength - secondPartLength) == posEnd) {
                                OdfElement parent = (OdfElement) currentNode.getParentNode();
                                parent.removeChild(secondPart);
                                deleteStatus.add(0, Boolean.TRUE);
                                // remove empty boilderplate
                                while (!parent.isComponentRoot() && parent.countDescendantComponents() == 0) {
                                    OdfElement grandParent = (OdfElement) parent.getParentNode();
                                    if (grandParent instanceof OfficeBodyElement) {
                                        break;
                                    }
                                    grandParent.removeChild(parent);
                                    parent = grandParent;
                                }
                                currentPos += contentLength;
                            } else {
                                // position changed based on the first cut part
                                currentPos = currentPos + (contentLength - secondPartLength);
                                currentPos = delete(secondPart, currentPos, posStart, posEnd, deleteStatus);
                            }
                        } else {
                            OdfElement parent = (OdfElement) currentNode.getParentNode();
                            parent.removeChild(currentNode);
                            deleteStatus.add(0, Boolean.TRUE);
                            // remove empty boilderplate
                            while (!parent.isComponentRoot() && parent.countDescendantComponents() == 0) {
                                OdfElement grandParent = (OdfElement) parent.getParentNode();
                                if (grandParent instanceof OfficeBodyElement) {
                                    break;
                                }
                                grandParent.removeChild(parent);
                                parent = grandParent;
                            }
                            currentPos += contentLength - secondPartLength;
                        }
                        // SKIP THE NODE AS SELECTION NOT STARTED
                    } else {
                        currentPos += contentLength;
                    }
                }
                return currentPos;

//				Integer contentLength = node.getLength();
//				// see if targetposition (either start or end) is within this text node
//				if (currentPos + contentLength >= currentTargetPos) {
//					// If the delete pos is this complete text node and it is already past startPos, delete node
//					if (currentPos + contentLength == currentTargetPos && textPosEnd == currentTargetPos) {
//						node.getParentNode().removeChild(node);
//					} else { // if delete position is within the text node
//						Integer splitPosition = null;
//						splitPosition = currentTargetPos - currentPos;
//						Text secondPart = node.splitText(splitPosition);
//						//if position within text was the startNode, only delete second part
//						if (currentTargetPos == textPosStart) {
//							// if the end position is in the the same text node as the start position
//							if (currentPos + contentLength >= textPosEnd) {
//								// split the string once more
//								secondPart.splitText(textPosEnd - splitPosition - currentPos);
//							}
//							node.getParentNode().removeChild(secondPart);
//						} else {//if we had been in the delete mode, only delete first part
//							// node has become the firstNode after split
//							node.getParentNode().removeChild(node);
//						}
//					}
//				} else {
//					// if we are in the deletion mode
//					if (currentTargetPos == textPosEnd) {
//						// delete the complete node
//						node.getParentNode().removeChild(node);
//					}
//				}
//				return currentPos += contentLength;
            }

            /**
             * Copies the text properties from the given style name to the style
             * of the target element
             */
            private void copyTextProperties(String sourceStyleName, OdfElement targetElement) {
                // we move the text style properties from the span to the paragraph--
                OdfOfficeAutomaticStyles autoStyles = null;
                // the automatic styles are in a spreadsheet always in the content.xml, only when a table is in a header/footer it would be in the styles.xml (latter we do not support)
                if (targetElement.ownerDocument instanceof OdfContentDom) {
                    autoStyles = ((OdfContentDom) targetElement.ownerDocument).getAutomaticStyles();
                } else { // if the span is in a header/footer the element is in the styles.xml (part of the master page style)
                    autoStyles = ((OdfStylesDom) targetElement.ownerDocument).getAutomaticStyles();
                }
                OdfStyle spanStyle = autoStyles.getStyle(sourceStyleName, OdfStyleFamily.Text);
                if (spanStyle != null) {
                    OdfStylePropertiesBase textProps = spanStyle.getPropertiesElement(OdfStylePropertiesSet.TextProperties);
                    if (textProps != null && textProps.attributes != null && textProps.attributes.getLength() > 0) {
                        StyleStyleElement paraStyle = ((TextParagraphElementBase) targetElement).getOrCreateUnqiueAutomaticStyle();
                        OdfStylePropertiesBase paraTextProps = paraStyle.getPropertiesElement(OdfStylePropertiesSet.TextProperties);
                        if (paraTextProps == null) {
                            paraTextProps = ((OdfFileDom) targetElement.ownerDocument).newOdfElement(StyleTextPropertiesElement.class);
                            paraStyle.appendChild(paraTextProps);
                        }
                        for (int i = 0; i < textProps.attributes.getLength(); i++) {
                            Attr attr = (Attr) textProps.attributes.item(i);
                            String ns = attr.getNamespaceURI();
                            String prefix = attr.getPrefix();
                            String localName = attr.getLocalName();
                            paraTextProps.setAttributeNS(ns, prefix + ':' + localName, attr.getValue());
                        }
                    }
                }
            }

            private int receive(Node currentNode, int currentPos, int posStart, int posEnd, ArrayList newNodeContainer) {
                if (currentNode != null) {

                    // ** GET NODE SIZE
                    Integer contentLength = 1; // component default is 1
                    if (currentNode instanceof Text) {
                        contentLength = ((Text) currentNode).getLength();
                    } else {
                        // get size from component
                        contentLength = ((OdfElement) currentNode).getRepetition();
                    }

                    // if the current node is selected
                    if (currentPos == posStart && (contentLength == 1 || currentNode instanceof Text)) {
                        newNodeContainer.add(currentNode);
                        currentPos = posEnd;
                    } else if (currentPos + contentLength > posStart) { // else if only a part of the text node is selected
                        Node secondPart = null;
                        if (currentNode instanceof Text) {
                            // splitCursor is the first character of second part (counting starts with 0)
                            secondPart = ((Text) currentNode).splitText(posStart - currentPos);
                            newNodeContainer.add(secondPart);
                        } else {
                            //handle component split...
                            Node thirdPart = null;
                            Node parent = ((OdfElement) currentNode).getParentNode();
                            secondPart = ((OdfElement) currentNode).split(posStart - currentPos);
                            if (((OdfElement) secondPart).getRepetition() > 1) {
                                thirdPart = ((OdfElement) secondPart).split(1);
                            }
                            newNodeContainer.add(secondPart);
                        }
                        currentPos = posEnd;
                        // ** SKIP THE NODE AS SELECTION NOT STARTED
                    } else {
                        currentPos += contentLength;
                    }
                }
                return currentPos;
            }

            private void moveChildrenTo(Node currentNode, int currentPos, int posStart, int posEnd, Element newParent) {
                if (currentNode != null) {
                    newParent.appendChild(currentNode);
                }
            }

            /**
             * @return in opposite of all other algorithms the currentPos is
             * being used to count the number of components
             */
            private int count(Node currentNode, int currentPos, Boolean isTextCounting) {
                if (currentNode != null) {

                    // ** GET NODE SIZE
                    Integer contentLength = 1; // component default is 1
                    if (!(currentNode instanceof Text)) {
                        // get size from component
                        contentLength = ((OdfElement) currentNode).getRepetition();
                    } else if (isTextCounting == null || isTextCounting) {
                        if (((Text) currentNode).toString().trim().length() > 0) {
                            contentLength = ((Text) currentNode).getLength();
                        }
                    }
                    currentPos += contentLength;
                }
                return currentPos;
            }
        }
    }

    /**
     * Returns if the text should be returned or is under a nested paragraph or
     * ignored text element (e.g. text:note-citation).
     */
    private static boolean isIgnoredText(Text text) {
        boolean isIgnored = true;
        Node parentNode = text.getParentNode();
        if (parentNode instanceof OdfElement) {
            isIgnored = isIgnoredElement((OdfElement) parentNode);
        }
        return isIgnored;
    }

    private static boolean isIgnoredElement(OdfElement element) {
        boolean isIgnored = true;
        if (!element.mIsIgnoredComponent) {
            while (element != null) {
                if (element.isComponentRoot() && !element.selfAndDescendantTextIgnoredAsComponent()) {
                    isIgnored = false;
                    break;
                } else {
                    if (element.selfAndDescendantTextIgnoredAsComponent()) {
                        isIgnored = true;
                        break;
                    } else {
                        Node parent = element.getParentAs(OdfElement.class);
                        if (parent instanceof OdfElement) {
                            isIgnored = isIgnoredElement((OdfElement) parent);
                        }
                        break;
                    }
                }
            }
        }
        return isIgnored;
    }

    private static int getContentSize(Node currentNode) {
        int contentLength = 0; // by default there is no length

        Node nextChild = currentNode.getFirstChild();
        while (nextChild != null) {
            contentLength += getNodeWidth(nextChild);
            nextChild = nextChild.getNextSibling();
        }
        return contentLength;
    }

//	public void addReference(String url, int startPos, int endPos){
//		if (url != null && !url.isEmpty() && !url.equals("null")) {
//			OdfFileDom xmlDoc = (OdfFileDom) this.getOwnerDocument();
//
//			// 2DO: can I reuse an anchor?
//			TextAElement anchor = new TextAElement(xmlDoc);
//			anchor.setXlinkHrefAttribute(url);
//			anchor.setXlinkTypeAttribute("simple");
//
//			// 2DO: ONLY remove all text:a descendants from the selection, there is a function parameter missing?!
//			NodeList anchors = this.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "a");
//			int anchorCount = anchors.getLength();
//			for (int i = anchorCount - 1; i >= 0; i--) {
//				TextAElement a = (TextAElement) anchors.item(i);
//				OdfElement.removeSingleElement(a);
//			}
//			Element parent = (Element) this.getParentNode();
//			if (parent != null) {
//				this = (TextSpanElement) parent.replaceChild(anchor, this);
//				anchor.appendChild(this);
//			} else {
//				((OdfElement) this.getParentNode()).markText(startPos, endPos, anchor, attrs);
//			}
//
//			// NOTE: Calc Issue workaround: No span within anchors possible in OpenOffice CALC (no text will be shown)
//			if (xmlDoc.getDocument() instanceof OdfSpreadsheetDocument) {
//				NodeList innerSpans = anchor.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "span");
//				int spanCount = innerSpans.getLength();
//				for (int i = spanCount - 1; i >= 0; i--) {
//					TextSpanElement innerSpan = (TextSpanElement) innerSpans.item(i);
//					OdfElement.removeSingleElement(innerSpan);
//				}
//			}
//		}
//		// if any hyperlink should be removed!
//		if (url == null) {
//			// remove all text:a descendants
//			NodeList anchors = this.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "a");
//			int anchorCount = anchors.getLength();
//			for (int i = anchorCount - 1; i >= 0; i--) {
//				TextAElement a = (TextAElement) anchors.item(i);
//				OdfElement.removeSingleElement(a);
//			}
//		}
//	}
    /**
     * @returns the <text:a> element to apply the new format. If created it will
     * be stored within the spanContainer
     */
    private static TextAElement getAnchorElement(Node content, OdfFileDom ownerDoc, String url, Map<OdfName, OdfElement> formatElementHolder) {
        TextAElement newAnchor = null;
        if (formatElementHolder != null) {
            if (formatElementHolder.containsKey(TextAElement.ELEMENT_NAME)) {
                newAnchor = (TextAElement) formatElementHolder.get(TextAElement.ELEMENT_NAME);
            } else {
                if (content instanceof TextAElement) {
                    newAnchor = (TextAElement) content;
                    newAnchor.setXlinkHrefAttribute(url);
                } else {
                    newAnchor = createAnchorElement(ownerDoc, url);
                }

                // we keep the anchor for following siblings
                formatElementHolder.put(TextAElement.ELEMENT_NAME, newAnchor);
            }
        }
        return newAnchor;
    }

    /**
     * @returns a new Anchor element with the changes as new format
     */
    // ToDo: Should be moved as static creation function to the span element class!!
    private static TextAElement createAnchorElement(OdfFileDom ownerDoc, String url) {
        TextAElement containerElement = new TextAElement(ownerDoc);
        containerElement.setXlinkHrefAttribute(url);
        return containerElement;
    }

    /**
     * @returns the span element to apply the new format. If created it will be
     * stored within the spanContainer
     */
    private static TextSpanElement getSpanElement(OdfFileDom ownerDoc, JSONObject formatChanges, Map<OdfName, OdfElement> formatElementHolder) {
        TextSpanElement newNode = null;
        if (formatElementHolder != null) {
            if (formatElementHolder.containsKey(TextSpanElement.ELEMENT_NAME)) {
                newNode = (TextSpanElement) formatElementHolder.get(TextSpanElement.ELEMENT_NAME);
            } else {
                newNode = createSpanElement(ownerDoc, formatChanges);
                // we keep the span for following siblings
                formatElementHolder.put(TextSpanElement.ELEMENT_NAME, newNode);
            }
        }
        return newNode;
    }

    /**
     * @returns a new span element with the changes as new format
     */
    // ToDo: Should be moved as static creation function to the span element class!!
    private static TextSpanElement createSpanElement(OdfFileDom ownerDoc, JSONObject formatChanges) {
        TextSpanElement spanElement = null;
        spanElement = new TextSpanElement(ownerDoc);
        JsonOperationConsumer.addStyle(formatChanges, spanElement, ownerDoc);
        return spanElement;
    }

    private static int getNodeWidth(Node currentNode) {
        // ** GET NODE SIZE
        int contentLength = 0; // by default there is no length
        // if a text
        if (currentNode instanceof Text) {
            if (!isIgnoredText((Text) currentNode)) {
                contentLength = ((Text) currentNode).getLength();
            }
            // if a component element and NOT ignored
        } else if (currentNode instanceof OdfElement) {
            if (Component.isComponentRoot((OdfElement) currentNode) || currentNode instanceof TextSElement) {
                if (isIgnoredElement((OdfElement) currentNode)) {
                    contentLength = 0;
                } else {
                    // get size from component
                    contentLength = ((OdfElement) currentNode).getRepetition();
                }
            } else {
                Node nextChild = ((OdfElement) currentNode).firstChild;
                while (nextChild != null) {
                    contentLength += getNodeWidth(nextChild);
                    nextChild = nextChild.getNextSibling();
                }
            }
        }
        return contentLength;
    }

    /**
     * Copy attributes from one element to another, existing attributes will be
     * overwritten
     */
    public static void copyAttributes(OdfElement from, OdfElement to) {
        NamedNodeMap attributes = from.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Attr node = (Attr) attributes.item(i);
            to.setAttributeNS(node.getNamespaceURI(), node.getNodeName(), node.getValue());
        }
    }

    /**
     * @return the first child element of a given parent
     */
    public Element getChildElement(String uri, String localName) {
        return getChildElement(uri, localName, 0);
    }

    /**
     * @return the child element of a given parent from a given position (starting with 0)
     */
    public Element getChildElement(String uri, String localName, int position) {
        NodeList childList = this.getElementsByTagNameNS(uri, localName);
        return (Element) childList.item(position);
    }

    public static boolean isIgnoredElement(Element element) {
        return isIgnoredElement(element.getNamespaceURI(), element.getLocalName());
    }

    /**
     * ToDo: Move away to parser, as it is application logic not ODF relevant!
     * Elements that blocks the creation of operations due to implementation
     * issues
     */
    public static boolean isIgnoredElement(String uri, String localName) {
        boolean isIgnored = false;
        if (uri != null && uri.equals(TextNoteElement.ELEMENT_NAME.getUri())) {
            // text:note
            if (localName.equals(TextNoteElement.ELEMENT_NAME.getLocalName())) {
                isIgnored = true;
            }
        }
        if (uri != null && uri.equals(TextTrackedChangesElement.ELEMENT_NAME.getUri())) {
            //text:tracked-changes
            if (localName.equals(TextTrackedChangesElement.ELEMENT_NAME.getLocalName())) {
                isIgnored = true;
            }
        }
        if (uri != null && uri.equals(TableShapesElement.ELEMENT_NAME.getUri())) {
            // table:shapes
            if (localName.equals(TableShapesElement.ELEMENT_NAME.getLocalName())) {
                isIgnored = true;
            }
            // table:covered-table-cell
            if (localName.equals(TableCoveredTableCellElement.ELEMENT_NAME.getLocalName())) {
                isIgnored = true;
            }
        }
        if (uri != null && uri.equals(TableContentValidationsElement.ELEMENT_NAME.getUri())) {
            // table:content-validations
            if (localName.equals(TableContentValidationsElement.ELEMENT_NAME.getLocalName())) {
                isIgnored = true;
            }
        }
        if (uri != null && uri.equals(StyleHandoutMasterElement.ELEMENT_NAME.getUri())) {
            // style:handout-master
            if (localName.equals(StyleHandoutMasterElement.ELEMENT_NAME.getLocalName())) {
                isIgnored = true;
            }
        }
        return isIgnored;
    }

    /**
     * @returns the next element sibling of the given node or null if none
     * exists
     */
    public static OdfElement getNextSiblingElement(Node node) {
        OdfElement nextElement = null;
        Node _nextSibling = node.getNextSibling();
        if (_nextSibling instanceof OdfElement) {
            nextElement = (OdfElement) _nextSibling;
        } else if (_nextSibling instanceof Text) {
            nextElement = getNextSiblingElement(_nextSibling);
        }
        return nextElement;
    }

    /**
     * @returns the next element sibling of the given node or null if none
     * exists
     */
    public static OdfElement getPreviousSiblingElement(Node node) {
        OdfElement previousElement = null;
        Node _previousElement = node.getPreviousSibling();
        if (_previousElement instanceof OdfElement) {
            previousElement = (OdfElement) _previousElement;
        } else if (_previousElement instanceof Text) {
            previousElement = getPreviousSiblingElement(_previousElement);
        }
        return previousElement;
    }

    /**
     * @returns the first element child of the this or null if none exists
     */
    public OdfElement getFirstChildElement() {
        OdfElement firstElementChild = null;
        NodeList nodeList = this.getChildNodes();
        Node node = nodeList.item(0);
        if (node != null) {
            if (node instanceof OdfElement) {
                firstElementChild = (OdfElement) node;
            } else {
                firstElementChild = getNextSiblingElement(node);
            }
        }
        return firstElementChild;
    }

    /**
     * @returns the last element child of the this or null if none exists
     */
    public OdfElement getLastChildElement() {
        OdfElement lastElementChild = null;
        NodeList nodeList = this.getChildNodes();
        Node node = nodeList.item(0);
        for (int i = nodeList.getLength(); i >= 0; i--) {
            if (node instanceof OdfElement) {
                lastElementChild = (OdfElement) node;
                break;
            }
        }
        return lastElementChild;
    }

    public int countPrecedingSiblingElements() {
        int i = 0;
        Node node = this.getPreviousSibling();
        while (node != null) {
            node = node.getPreviousSibling();
            if (node instanceof Element) {
                i++;
            }
        }
        return i;
    }

    // ToDo: Move this to a intermediate class, e.g. ComponentRootElement
    /**
     * @return the component size of a heading, which is always 1
     */
    public void setRepetition(int repetition) {
        // does not work for all classes
    }


    /** @return the concatenated text contained by itself and all descendants (subtree).
        Does not take into account indented XML, as ODF whitespace handling require to neglect preceeding text content */
	public String getTextContent() {
		StringBuilder buffer = new StringBuilder();
		NodeList nodeList = this.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE){
				buffer.append(node.getNodeValue());
            } else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node instanceof TextSpanElement) {
					buffer.append(((TextSpanElement) node).getTextContent());
				}
				else if (node.getNodeName().equals("text:s")) {
					Integer count = ((TextSElement) node).getTextCAttribute();
					for (int j = 0; j < (count != null ? count : 1); j++)
						buffer.append(' ');
				} else if (node.getNodeName().equals("text:tab"))
					buffer.append('\t');
				else if (node.getNodeName().equals("text:line-break")) {
					String lineseperator = System.getProperty("line.separator");
					buffer.append(lineseperator);
				} else if (node.getNodeName().equals("text:a"))
					buffer.append(((TextAElement) node).getTextContent());
			}
		}
		return buffer.toString();
	}
}
