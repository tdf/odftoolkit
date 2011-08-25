/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.text.list;

import java.util.Iterator;

import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

/**
 * AbstractListContainer is an abstract implementation of the ListContainer
 * interface, with a default implementation for every method defined in ListContainer
 * , except getListContainerElement(). A subclass must implement
 * the abstract method getListContainerElement().
 * 
 * @since 0.4
 */
public abstract class AbstractListContainer implements ListContainer {

	@Override
	public List addList() {
		return new List(this);
	}

	@Override
	public List addList(ListDecorator decorator) {
		return new List(this, decorator);
	}

	@Override
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

	@Override
	public Iterator<List> getListIterator() {
		return new SimpleListIterator(this);
	}

	@Override
	public boolean removeList(List list) {
		OdfElement containerElement = getListContainerElement();
		Node child = containerElement.getFirstChild();
		while (child != null) {
			if (child instanceof TextListElement) {
				TextListElement listElement1 = (TextListElement) child;
				String id1 = listElement1.getXmlIdAttribute();
				TextListElement listElement2 = list.getOdfElement();
				String id2 = listElement2.getXmlIdAttribute();
				if ((listElement1 == listElement2) || ((id1 != null) && (id2 != null) && (id1.equals(id2)))) {
					containerElement.removeChild(child);
					return true;
				}
			}
			child = child.getNextSibling();
		}
		return false;
	}

	// default iterator to iterate list item.
	private class SimpleListIterator implements Iterator<List> {

		private OdfElement containerElement;
		private TextListElement nextListElement;
		private TextListElement tempListElement;

		private SimpleListIterator(ListContainer container) {
			containerElement = container.getListContainerElement();
		}

		public boolean hasNext() {
			tempListElement = findNext(nextListElement);
			return (tempListElement != null);
		}

		public List next() {
			if (tempListElement != null) {
				nextListElement = tempListElement;
				tempListElement = null;
			} else {
				nextListElement = findNext(nextListElement);
			}
			if (nextListElement == null) {
				return null;
			} else {
				return new List(nextListElement);
			}
		}

		public void remove() {
			if (nextListElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			containerElement.removeChild(nextListElement);
		}

		private TextListElement findNext(TextListElement nextListElement) {
			Node child = null;
			if (nextListElement == null) {
				child = containerElement.getFirstChild();
			} else {
				child = nextListElement.getNextSibling();
			}

			while (child != null) {
				if (child instanceof TextListElement) {
					return (TextListElement) child;
				}
				child = child.getNextSibling();
			}
			return null;
		}
	}
}
