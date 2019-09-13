/*
 * Copyright 2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.odfdom.changes;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This container can be used for components including a mix of text and
 * elements, where each character and element are components. Instead of a list
 * of this mixed components only a list of the elements is being held
 *
 * @author svante.schubertATgmail.com
 */
public class TextContainer<T> extends Component {

    public TextContainer(OdfElement componentElement, Component parent) {
        super(componentElement, parent);
    }

    public void appendText(Text text) {
        mRootElement.appendChild(text);
    }

    public void removeText(int start, int end) {
        ((TextContainingElement) mRootElement).delete(start, end);
    }

    public Node appendChild(Node node) {
        Node newNode = mRootElement.appendChild(node);
        return newNode;
    }

    /**
     * Adds the given component to the root element
     */
    @Override
    public void addChild(int index, Component c) {
        mRootElement.insert(c.getRootElement(), index);
    }

    /**
     * @return either a text node of size 1 or an element being the root element
     * of a component
     */
    @Override
    public Node getChildNode(int index) {
        return ((OdfElement) mRootElement).receiveNode(index);
    }

    /**
     * Removes a component from the text element container. Removes either an
     * element representing a component or text node of size 1
     *
     * @returns the node being deleted, either the text node or element
     */
    @Override
    public Node remove(int index) {
        Node node = (Node) this.getChildNode(index);
        return mRootElement.removeChild(node);
    }

    /**
     * All children of the root element will be traversed. If it is a text node
     * the size is added, if it is an element and a component a size of one is
     * added, if it is a marker, for known text marker elements (text:span,
     * text:bookmark) the children are recursive checked
     *
     * @return the number of child components
     */
    @Override
    public int size() {
        return mRootElement.componentSize();
    }

    /**
     * Recursive traverse the text container and count the children
     */
    private int findChild(Node parent, int size, Node targetNode) {
        NodeList children = parent.getChildNodes();
        Node child;
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            if (child != targetNode) {
                if (child instanceof Text) {
                    size += ((Text) child).getLength();
                } else if (child instanceof Element) {
                    if (Component.isTextSelection(child)) {
                        size = findChild(child, size, targetNode);
                    } else if (child instanceof OdfElement && ((OdfElement) child).isComponentRoot()) {
                        size++;
                    }
//					else if (child instanceof TextSElement) {
//						 Integer spaceCount = ((TextSElement) child).getTextCAttribute();
//						if(spaceCount == null){
//							size++;
//						}else{
//							size += spaceCount;
//						}
//					}
                }
            } else {
                break;
            }
        }
        return size;
    }

    @Override
    public int indexOf(Object o) {
        Node targetNode = null;
        if (o instanceof Component) {
            targetNode = ((Component) o).getRootElement();
        } else if (o instanceof Node) {
            targetNode = (Node) o;
        }
        int position = 0;
        if (targetNode != null) {
            position += findChild(mRootElement, position, targetNode);
        }
        return position;
    }
}
