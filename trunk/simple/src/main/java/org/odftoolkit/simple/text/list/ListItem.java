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

import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextNumberElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.text.list.ListDecorator.ListType;
import org.w3c.dom.Node;

/**
 * ListItem represents an item in a list. ListItem can have text content or sub
 * List.
 * 
 * @since 0.4
 */
public class ListItem implements ListContainer {

	// <text:list-item>
	private TextListItemElement listItemElement;
	private ListDecorator paragraphDecorator;
	private ListContainerImpl listContainerImpl = new ListContainerImpl();

	/**
	 * Constructor with list item element only.
	 * 
	 * @param element
	 *            the list item odf element
	 */
	ListItem(TextListItemElement element) {
		this(element, null);
	}

	/**
	 * Constructor with item content set
	 * 
	 * @param element
	 *            the list item odf element
	 * @param content
	 *            item text content
	 */
	ListItem(TextListItemElement element, String content) {
		listItemElement = element;
		setTextContent(content);
	}

	/**
	 * Get the instance of TextListItemElement which represents this list item.
	 * 
	 * @return the instance of TextListItemElement
	 */
	public TextListItemElement getOdfElement() {
		return listItemElement;
	}

	/**
	 * Get item text content. If this item has a List, its content is not
	 * included.
	 * 
	 * @return the text content of this item
	 */
	public String getTextContent() {
		Node child = listItemElement.getFirstChild();
		while (child != null) {
			if (child instanceof TextPElement) {
				return child.getTextContent();
			}
			child = child.getNextSibling();
		}
		return null;
	}

	/**
	 * Set item text content.
	 * 
	 * @param content
	 *            item text content.
	 */
	public void setTextContent(String content) {
		if (content != null) {
			Node child = listItemElement.getFirstChild();
			Node positionNode = null;
			TextPElement pElement = null;
			while (child != null) {
				if (child instanceof TextNumberElement) {
					positionNode = child.getNextSibling();
					child = child.getNextSibling();
				} else if ((child instanceof TextPElement) && (pElement == null)) {
					pElement = (TextPElement) child;
					child = child.getNextSibling();
				} else if ((child instanceof TextListElement) && (positionNode == null)) {
					positionNode = child;
					child = child.getNextSibling();
				} else {
					Node tmp = child;
					child = child.getNextSibling();
					listItemElement.removeChild(tmp);
				}
			}
			if (pElement == null) {
				if (positionNode == null) {
					pElement = listItemElement.newTextPElement();
				} else {
					pElement = ((OdfFileDom) listItemElement.getOwnerDocument()).newOdfElement(TextPElement.class);
					listItemElement.insertBefore(pElement, positionNode);
				}
			}
			pElement.setTextContent(content);
			if (paragraphDecorator != null) {
				paragraphDecorator.decorateListItem(this);
			} else {
				// paragraphDecorator is null when the owner List is constructed
				// by List(TextListElement).
				Node previousSibling = listItemElement.getPreviousSibling();
				String pElementStyleName = null;
				while (previousSibling != null) {
					if (previousSibling instanceof TextListItemElement) {
						Node previousChild = previousSibling.getFirstChild();
						while (previousChild != null) {
							if (previousChild instanceof TextPElement) {
								TextPElement previousPElement = (TextPElement) previousChild;
								pElementStyleName = previousPElement.getTextStyleNameAttribute();
								break;
							}
							previousChild = previousChild.getNextSibling();
						}
						break;
					}
					previousSibling = previousSibling.getPreviousSibling();
				}
				if (pElementStyleName != null) {
					pElement.setTextStyleNameAttribute(pElementStyleName);
				}

			}
		}
	}

	/**
	 * Remove this item from its owner list.
	 */
	public void remove() {
		Node parentElement = listItemElement.getParentNode();
		OdfFileDom ownerDocument = (OdfFileDom) listItemElement.getOwnerDocument();
		Document doc = (Document) ownerDocument.getDocument();
		doc.removeElementLinkedResource(listItemElement);
		parentElement.removeChild(listItemElement);
	}

	/**
	 * Get the start number of this item.
	 * <p>
	 * A value can be specified that restarts numbering of a list at the current
	 * item. This feature can only be applied to items in a list with a
	 * numbering list style.
	 * 
	 * @return the start number of this item. If there is no start number
	 *         setting on this item or the owner list is not a numbering list,
	 *         <code>null</code> will be returned.
	 */
	public Integer getStartNumber() {
		if (getOwnerList().getType() == ListType.NUMBER) {
			return listItemElement.getTextStartValueAttribute();
		}
		return null;
	}

	/**
	 * Set the start number of this item.
	 * <p>
	 * A value can be specified that restarts numbering of a list at the current
	 * item. This feature can only be applied to items in a list with a
	 * numbering list style.
	 * 
	 * @param number
	 *            the start number to be set.
	 * @throws IllegalArgumentException if <code>number < 0</code>.
	 */
	public void setStartNumber(Integer number) {
		if (number < 0) {
			throw new IllegalArgumentException("start number should be a non-negative integer.");
		}
		if (getOwnerList().getType() == ListType.NUMBER) {
			listItemElement.setTextStartValueAttribute(number);
		}
	}

	/**
	 * Get the number format of this item.
	 * <p>
	 * List item can contain the text of a formatted number which is present
	 * when a list style is applied to an element whose corresponding list level
	 * style specifies that the list label is a number. This text may be used by
	 * consumers that do not support the automatic generation of numbering but
	 * should be ignored by consumers that do support it.
	 */
	public String getNumberFormat() {
		String format = null;
		if (getOwnerList().getType() == ListType.NUMBER) {
			Node child = listItemElement.getFirstChild();
			if ((child != null) && (child instanceof TextNumberElement)) {
				format = ((TextNumberElement) child).getTextContent();
			}
		}
		return format;
	}

	/**
	 * Set the number format of this item.
	 * <p>
	 * List item can contain the text of a formatted number which is present
	 * when a list style is applied to an element whose corresponding list level
	 * style specifies that the list label is a number. This text may be used by
	 * consumers that do not support the automatic generation of numbering but
	 * should be ignored by consumers that do support it.
	 * 
	 * @param format
	 *            the number format to be set.
	 */
	public void setNumberFormat(String format) {
		if (getOwnerList().getType() == ListType.NUMBER) {
			TextNumberElement textNumberElement = null;
			Node child = listItemElement.getFirstChild();
			if (child == null) {
				textNumberElement = listItemElement.newTextNumberElement();
			} else {
				if (child instanceof TextNumberElement) {
					textNumberElement = (TextNumberElement) child;
				} else {
					textNumberElement = ((OdfFileDom) listItemElement.getOwnerDocument())
							.newOdfElement(TextNumberElement.class);
					listItemElement.insertBefore(textNumberElement, child);
				}
			}
			textNumberElement.setTextContent(format);
		}
	}

	/**
	 * Answers the index of the item in its owner list.
	 * 
	 * @return index of the item.
	 */
	public int getIndex() {
		Node firstNode = listItemElement.getParentNode().getFirstChild();
		int i = 0;
		while (firstNode != null) {
			if (firstNode instanceof TextListItemElement) {
				if (firstNode == listItemElement) {
					break;
				} else {
					i++;
				}
			}
			firstNode = firstNode.getNextSibling();
		}
		return i;
	}

	/**
	 * Get the List which contains this ListItem.
	 */
	public List getOwnerList() {
		Node parent = listItemElement.getParentNode();
		if (parent instanceof TextListElement) {
			return new List((TextListElement) parent);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		String numberFormat = getNumberFormat();
		String splitStr = "";
		if (numberFormat != null) {
			strBuilder.append(numberFormat);
			strBuilder.append(" ");
			splitStr = "\n";
		}
		String textContent = getTextContent();
		if (textContent != null) {
			strBuilder.append(textContent);
			splitStr = "\n";
		}
		Iterator<List> lists = getListIterator();
		while (lists.hasNext()) {
			strBuilder.append(splitStr);
			strBuilder.append(lists.next().toString());
		}
		return strBuilder.toString();
	}

	public OdfElement getListContainerElement() {
		return listContainerImpl.getListContainerElement();
	}
	
	public List addList() {
		return listContainerImpl.addList();
	}

	public List addList(ListDecorator decorator) {
		return listContainerImpl.addList(decorator);
	}
	
	public void clearList() {
		listContainerImpl.clearList();
	}
	
	public Iterator<List> getListIterator() {
		return listContainerImpl.getListIterator();
	}

	public boolean removeList(List list) {
		return listContainerImpl.removeList(list);
	}

	private class ListContainerImpl extends AbstractListContainer {
		public OdfElement getListContainerElement() {
			return listItemElement;
		}
	}

	// set ListDecorator for decorating item, only used by List.
	void setParagraphDecorator(ListDecorator decorator) {
		paragraphDecorator = decorator;
	}
}
