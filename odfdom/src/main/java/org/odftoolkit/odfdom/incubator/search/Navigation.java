/**
 * **********************************************************************
 *
 * <p>Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.incubator.search;

import org.w3c.dom.Node;

import java.util.Set;

/**
 * Abstract class Navigation used to navigate the document and find the matched element by the user
 * defined conditions
 */
public abstract class Navigation {

  /**
   * Return true if document still has more matched Selection when traversing the document(In other
   * words return true if getNextMatchElement() would return an element instance rather than return
   * null)
   *
   * @return true if document still has more matched Selection, and vice versa
   */
  public abstract boolean hasNext();
  // abstract public void gotoPrevious();

  /**
   * get the current Selection result
   *
   * @return the current Selection result
   */
  public abstract Selection getCurrentItem();

  /**
   * check if the element match the user defined condition
   *
   * @param element navigate this element
   * @return true if the element match the user defined condition; false if not match
   */
  public abstract boolean match(Node element);

  /**
   * Checks if the given node can be matched by the navigation
   * @param node the node
   * @return true if the node is of interest for the navigation
   */
  public abstract boolean isMatchingNode(Node node);

  /**
   * get the next matched element in a whole dom tree
   *
   * @param startpoint navigate from the startpoint
   * @return the next matched element
   */
  protected Node getNextMatchElement(Node startpoint) {
    Node matchedNode = null;
    matchedNode = traverseTree(startpoint);

    Node currentpoint = startpoint;
    while ((matchedNode == null) && (currentpoint != null)) {
      if (isMatchingNode(currentpoint) && currentpoint != startpoint){
          //TODO do something here
      }
      Node sibling = currentpoint.getNextSibling();
      if ((sibling != null)
          && (sibling.getNodeType() == Node.TEXT_NODE || sibling.getNodeType() == Node.ELEMENT_NODE)
          && (match(sibling))) {
        matchedNode = sibling;
      }
      while ((sibling != null) && (matchedNode == null)) {
        if ((sibling.getNodeType() == Node.TEXT_NODE
            || sibling.getNodeType() == Node.ELEMENT_NODE)) {
          matchedNode = traverseTree(sibling);
        }
        sibling = sibling.getNextSibling();
        if (sibling != null && match(sibling)) {
          matchedNode = sibling;
        }
      }
      currentpoint = currentpoint.getParentNode();
    }

    return matchedNode;
  }

  /**
   * get the next matched element in a sub tree
   *
   * @param startpoint navigate from the startpoint
   * @param root the root of the sub tree
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
      }
      while ((sibling != null) && (matchedNode == null)) {
        if ((sibling.getNodeType() == Node.TEXT_NODE
            || sibling.getNodeType() == Node.ELEMENT_NODE)) {
          matchedNode = traverseTree(sibling);
        }
        sibling = sibling.getNextSibling();
        if (sibling != null && match(sibling)) {
          matchedNode = sibling;
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
      if ((node.getNodeType() == Node.TEXT_NODE || node.getNodeType() == Node.ELEMENT_NODE)) {
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
