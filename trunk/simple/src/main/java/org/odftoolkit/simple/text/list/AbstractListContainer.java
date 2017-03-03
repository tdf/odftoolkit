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

package org.odftoolkit.simple.text.list;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * AbstractListContainer is an abstract implementation of the ListContainer
 * interface, with a default implementation for every method defined in ListContainer
 * , except getListContainerElement(). A subclass must implement
 * the abstract method getListContainerElement().
 * 
 * @since 0.4
 */
public abstract class AbstractListContainer implements ListContainer {

	public List addList() {
		return new List(this);
	}

	public List addList(ListDecorator decorator) {
		return new List(this, decorator);
	}

	public void clearList() {
		OdfElement containerElement = getListContainerElement();
		Node child = getListContainerElement().getFirstChild();
		while (child != null) {
			if (child instanceof TextListElement) {
				Node tmp = child;
				child = child.getNextSibling();
				containerElement.removeChild(tmp);
			} else {
				child = child.getNextSibling();
			}
		}
	}

	public Iterator<List> getListIterator() {
		return new SimpleListIterator(this);
	}

	public boolean removeList(List list) {
		OdfElement containerElement = getListContainerElement();
		Node child = containerElement.getFirstChild();
		OdfFileDom ownerDocument = (OdfFileDom) containerElement.getOwnerDocument();
		Document doc = (Document) ownerDocument.getDocument();
		while (child != null) {
			if (child instanceof TextListElement) {
				TextListElement listElement1 = (TextListElement) child;
				String id1 = listElement1.getXmlIdAttribute();
				TextListElement listElement2 = list.getOdfElement();
				String id2 = listElement2.getXmlIdAttribute();
				if ((listElement1 == listElement2) || ((id1 != null) && (id2 != null) && (id1.equals(id2)))) {
					doc.removeElementLinkedResource(listElement1);
					containerElement.removeChild(child);
					return true;
				}
			}
			child = child.getNextSibling();
		}
		return false;
	}

	// default iterator to iterate list item.
	private static class SimpleListIterator implements Iterator<List> {
		
		private java.util.List<List> allLists;
		private int index;
		private List currentList;
		
		public SimpleListIterator(ListContainer container) {
			this.allLists = getLists(container.getListContainerElement().getChildNodes());
		}

		public boolean hasNext() {
			return this.index < this.allLists.size();
		}

		public List next() {
			if (hasNext()) {
				this.currentList = allLists.get(index);
				this.index++;
				return this.currentList;
			} else {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			if (this.currentList == null) {
				throw new IllegalStateException();
			} else {
				this.allLists.remove(this.currentList);
				this.currentList = null;
			}
		}
		
		private java.util.List<List> getLists(NodeList nodes) {
			java.util.List<List> lists = new LinkedList<List>();
			int numberOfNodes = nodes.getLength();
			for (int i = 0; i < numberOfNodes; i++) {
				Node node = nodes.item(i);
				lists.addAll(getLists(node));
			}
			return lists;
		}
		
		private java.util.List<List> getLists(Node node) {
			java.util.List<List> lists = new LinkedList<List>();
			if (node instanceof TextListElement) {
				TextListElement textListElement = (TextListElement) node;
				List list = new List(textListElement);
				lists.add(list);
			} else {
				NodeList childNodes = node.getChildNodes();
				lists.addAll(getLists(childNodes));
			}
			return lists;
		}
		
	}
	
}
