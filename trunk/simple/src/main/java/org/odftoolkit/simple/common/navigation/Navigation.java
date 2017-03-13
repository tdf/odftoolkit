/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.common.navigation;

import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.w3c.dom.Node;

/**
 * <code>Navigation</code> is used to navigate the document and find the matched
 * element by user defined conditions.
 */
public abstract class Navigation {
	
	/**
	 * Return true if document still has more matched {@link Selection
	 * Selection} when traversing the document(in other words return true if
	 * getNextMatchElement() would return an element instance rather than return
	 * null)
	 * 
	 * @return true if document still has more matched Selection, and vice versa
	 */
	public abstract boolean hasNext();

	// public abstract void gotoPrevious();
	
	/**
	 * Get next {@link Selection Selection} result.
	 * 
	 * @return the next <code>Selection</code> result
	 */
	public abstract Selection nextSelection();
	
	/**
	 * Check if the element is a qualified one.
	 * <p> Developers can define their own logic here to determine whether an element satisfies the requirements. 
	 * 
	 * @param element
	 *            navigate this element node.
	 * @return true if the element node match the user defined condition; false
	 *         if not match.
	 */
	public abstract boolean match(Node element);

	/**
	 * Get the next matched element in the whole element tree.
	 * 
	 * @param startpoint
	 *            navigate from the start point
	 * @return the next matched element node
	 */
	protected Node getNextMatchElement(Node startpoint) {
		// match the sub tree up to the root node (parent == null)
		return getNextMatchElementInTree(startpoint, null);
	}

	/**
	 * Get the next matched element node in a sub tree
	 * 
	 * @param startpoint
	 *            navigate from the start point
	 * @param root
	 *            the root of the sub tree
	 * @return the next matched element
	 */
	protected Node getNextMatchElementInTree(Node startpoint, Node root) {
		Node matchedNode = null;
		matchedNode = traverseTree(startpoint);
		Node currentpoint = startpoint;
		while ((matchedNode == null) && (currentpoint != root)) {
			Node sibling = currentpoint.getNextSibling();
			if ((sibling != null)
					&& (sibling.getNodeType() == Node.TEXT_NODE || sibling.getNodeType() == Node.ELEMENT_NODE)
					&& (match(sibling))) {
				matchedNode = sibling;
				break;
			}
			while ((sibling != null) && (matchedNode == null)) {
				if ((sibling.getNodeType() == Node.TEXT_NODE || sibling.getNodeType() == Node.ELEMENT_NODE)) {
					matchedNode = traverseTree(sibling);
				}
				if (matchedNode == null) {
					sibling = sibling.getNextSibling();
					if (sibling != null && match(sibling)) {
						matchedNode = sibling;
						break;
					}
				}
			}
			currentpoint = currentpoint.getParentNode();
		}
		return matchedNode;
	}

	private Node traverseTree(Node root) {
		Node matchedNode = null;
		if (root == null) {
			return null;
		}
		// if (match(root)) return root;
		Node node = root.getFirstChild();
		while (node != null) {
			if ((node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.ELEMENT_NODE)
					&& (!(node instanceof OfficeAnnotationElement))) {
				if (match(node) == true) {
					matchedNode = node;
					break;
				} else {
					matchedNode = traverseTree(node);
					if (matchedNode != null) {
						break;
					}
				}
			}
			node = node.getNextSibling();
		}
		return matchedNode;
	}
}
