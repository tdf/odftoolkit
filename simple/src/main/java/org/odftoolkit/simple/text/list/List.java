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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListHeaderElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleBulletElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleNumberElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.text.list.ListDecorator.ListType;
import org.w3c.dom.Node;

/**
 * This class represents a list. It can contain list header, 
 * followed by list items.
 * 
 * @since 0.4
 */
public class List {

	private TextListElement listElement;
	private ListDecorator decorator;

	/**
	 * Constructor ListItem, AbstractListContainer and List use only.
	 * 
	 * @param element
	 *            the ODF element 
	 */
	List(TextListElement element) {
		listElement = element;
		// only getListType and addItem --> item.setParagraphDecorator() need
		// decorator. We have fixed the problems when decorator is null in these
		// two conditions. So decorator null is OK for a exist list. But this
		// constructor should not be used on a new created TextListElement, as
		// its style name may have not set.
		decorator = null;
	}

	/**
	 * Constructor with ListContainer only. A bullet list with default style
	 * will be created.
	 * 
	 * @param container
	 *            the container in where this list will be appended.
	 */
	public List(ListContainer container) {
		this(container, null, null);
	}

	/**
	 * Constructor with ListContainer and ListDecorator.
	 * 
	 * @param container
	 *            the container in where this list will be appended.
	 * @param decorator
	 *            the ListDecorator of this list.
	 */
	public List(ListContainer container, ListDecorator decorator) {
		this(container, null, decorator);
	}

	/**
	 * Constructor with ListContainer, ListDecorator and header.
	 * 
	 * @param container
	 *            the container in where this list will be appended.
	 * @param decorator
	 *            the ListDecorator of this list.
	 * @param header
	 *            the header of this list.
	 */
	public List(ListContainer container, String header, ListDecorator decorator) {
		OdfElement containerElement = container.getListContainerElement();
		OdfFileDom ownerDocument = (OdfFileDom) containerElement.getOwnerDocument();
		listElement = ownerDocument.newOdfElement(TextListElement.class);
		listElement.setXmlIdAttribute(getUniqueXMLID());
		containerElement.appendChild(listElement);
		setHeader(header);
		if (decorator == null) {
			Document doc = (Document) ownerDocument.getDocument();
			decorator = new BulletDecorator(doc);
		}
		this.decorator = decorator;
		decorator.decorateList(this);
	}

	/**
	 * Constructor with ListContainer, ListDecorator, header and numbering setting.
	 * 
	 * @param container
	 *            the container in where this list will be appended.
	 * @param decorator
	 *            the ListDecorator of this list.
	 * @param isContinueNumbering
	 *            If <code>isContinueNumbering</code> is true, the numbering of this list is
	 *            continuing, otherwise the numbering of this list starts from the beginning.
	 * @param header
	 *            the header of this list.
	 */
	public List(ListContainer container, String header, boolean isContinueNumbering, ListDecorator decorator) {
		this(container, header, decorator);
		setContinueNumbering(isContinueNumbering);
	}

	/**
	 * Constructor with ListContainer, ListDecorator, header and continued list
	 * 
	 * @param container
	 *            the container in where this list will be appended.
	 * @param decorator
	 *            the ListDecorator of this list.
	 * @param continueList
	 *            the continued list of this list.
	 * @param header
	 *            the header of this list.
	 */
	public List(ListContainer container, String header, List continueList, ListDecorator decorator) {
		this(container, header, decorator);
		setContinueList(continueList);
	}

	/**
	 * Get the type of this list. The list type can be BULLET, NUMBER and IMAGE.
	 * 
	 * @return the list type.
	 */
	public ListType getType() {
		if (decorator != null) {
			return decorator.getListType();
		} else {
			try {
				String textStyleName = listElement.getTextStyleNameAttribute();
				Document doc = (Document) (((OdfFileDom) listElement.getOwnerDocument()).getDocument());
				OdfContentDom contentDocument = doc.getContentDom();
				OdfOfficeAutomaticStyles styles = contentDocument.getAutomaticStyles();
				OdfOfficeStyles documentStyles = doc.getDocumentStyles();
				OdfTextListStyle listStyle = styles.getListStyle(textStyleName);
				if (listStyle == null) {
					listStyle = documentStyles.getListStyle(textStyleName);
				}
				if (listStyle != null) {
					TextListLevelStyleElementBase listLevelStyle = listStyle.getLevel(1);
					if (listLevelStyle instanceof TextListLevelStyleBulletElement) {
						return ListType.BULLET;
					} else if (listLevelStyle instanceof TextListLevelStyleNumberElement) {
						return ListType.NUMBER;
					} else if (listLevelStyle instanceof TextListLevelStyleImageElement) {
						return ListType.IMAGE;
					}
				}
			} catch (Exception e) {
				Logger.getLogger(List.class.getName()).log(Level.SEVERE, null, e);
			}
			return null;
		}
	}

	/**
	 * Get the header of this list.
	 * 
	 * @return the header of this list.
	 */
	public String getHeader() {
		String header = "";
		Node headerNode = listElement.getFirstChild();
		if (headerNode instanceof TextListHeaderElement) {
			Node pNode = headerNode.getFirstChild();
			String splitString = "";
			while (pNode != null) {
				if (pNode instanceof TextPElement) {
					String content = pNode.getTextContent();
					if ((content != null) && (content.length() > 0)) {
						header = header + splitString + content;
						splitString = "\n";
					}
				}
				pNode = pNode.getNextSibling();
			}
		}
		if ("".equals(header)) {
			return null;
		} else {
			return header;
		}
	}

	/**
	 * Set the header of this list. The exist header will be replaced.
	 * 
	 * @param header
	 *            the header to be set.
	 */
	public void setHeader(String header) {
		if (header != null) {
			String[] headerContents = header.split("\n");
			TextListHeaderElement listHeaderElement = null;
			Node firstNode = listElement.getFirstChild();
			if (firstNode instanceof TextListHeaderElement) {
				listHeaderElement = (TextListHeaderElement) firstNode;
				Node pElement = listHeaderElement.getFirstChild();
				while (pElement != null) {
					firstNode.removeChild(pElement);
					pElement = pElement.getNextSibling();
				}
			} else {
				listHeaderElement = ((OdfFileDom) listElement.getOwnerDocument())
						.newOdfElement(TextListHeaderElement.class);
				listElement.insertBefore(listHeaderElement, firstNode);
			}
			for (String headerContent : headerContents) {
				TextPElement pElement = listHeaderElement.newTextPElement();
				pElement.setTextContent(headerContent);
			}
		}
	}

	/**
	 * Set the ListDecorator of this list. The current ListDecorator will be
	 * replaced.
	 * <p>
	 * This is a useful method which can change the list type and style, even
	 * though it has been created.
	 * 
	 * @param decorator
	 *            the ListDecorator to be used.
	 */
	public void setDecorator(ListDecorator decorator) {
		if (decorator != null) {
			this.decorator = decorator;
			decorator.decorateList(this);
		}
	}

	/**
	 * Add a list item by specifying a string value.
	 * 
	 * @param itemContent
	 *            the list item content to be added.
	 * @return the added ListItem.
	 */
	public ListItem addItem(String itemContent) {
		TextListItemElement listItemElement = listElement.newTextListItemElement();
		ListItem item = new ListItem(listItemElement);
		item.setParagraphDecorator(decorator);
		item.setTextContent(itemContent);
		return item;
	}

	/**
	 * Insert the specified ListItem at the specified location.
	 * The ListItem is inserted before the ListItem at the specified
	 * location.
	 * 
	 * @param location
	 *            the index to insert. The start number is 0.
	 * @param itemContent
	 *            the list item content to be added.
	 * @return the added ListItem.
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public ListItem addItem(int location, String itemContent) {
		OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
		TextListItemElement listItemElement = ownerDocument.newOdfElement(TextListItemElement.class);
		Node refNode = getItemByLocation(location);
		listElement.insertBefore(listItemElement, refNode);
		ListItem item = new ListItem(listItemElement);
		item.setParagraphDecorator(decorator);
		item.setTextContent(itemContent);
		return item;
	}

	/**
	 * Add the specified list item in ListItem object.
	 * 
	 * @param item
	 *            the list item to be added.
	 * @return the added ListItem.
	 */
	public ListItem addItem(ListItem item) {
		TextListItemElement itemElement = (TextListItemElement) (item.getOdfElement().cloneNode(true));
		listElement.appendChild(itemElement);
		ListItem newItem = new ListItem(itemElement);
		return newItem;
	}

	/**
	 * Insert a ListItem at the specified location.
	 * The ListItem is inserted before the ListItem at the specified
	 * location.
	 * 
	 * @param location
	 *            the index to insert.
	 * @param item
	 *            the list item to add.
	 * @return the added ListItem.
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public ListItem addItem(int location, ListItem item) {
		TextListItemElement itemElement = (TextListItemElement) (item.getOdfElement().cloneNode(true));
		Node refNode = getItemByLocation(location);
		listElement.insertBefore(itemElement, refNode);
		ListItem newItem = new ListItem(itemElement);
		return newItem;
	}

	/**
	 * Add list items by specifying an array of string values.
	 * 
	 * @param items
	 *            the list items to be added.
	 * @return the added items.
	 */
	public java.util.List<ListItem> addItems(String[] items) {
		java.util.List<ListItem> itemList = new java.util.ArrayList<ListItem>();
		for (String itemString : items) {
			TextListItemElement listItemElement = listElement.newTextListItemElement();
			ListItem item = new ListItem(listItemElement);
			item.setTextContent(itemString);
			itemList.add(item);
		}
		return itemList;
	}

	/**
	 * Insert the list items at the specified location in this List 
	 * by giving an array of string values.
	 * 
	 * @param location
	 *            the index to insert.
	 * @param items
	 *            the collection of list item contents.
	 * @return the added items.
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public java.util.List<ListItem> addItems(int location, String[] items) {
		java.util.List<ListItem> listCollection = new ArrayList<ListItem>();
		OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
		Node refNode = getItemByLocation(location);
		for (int i = items.length - 1; i >= 0; i--) {
			TextListItemElement listItemElement = ownerDocument.newOdfElement(TextListItemElement.class);
			listElement.insertBefore(listItemElement, refNode);
			ListItem item = new ListItem(listItemElement);
			item.setParagraphDecorator(decorator);
			item.setTextContent(items[i]);
			refNode = listItemElement;
			listCollection.add(item);
		}
		return listCollection;
	}

	/**
	 * Add list items by specifying an array of ListItem.
	 * 
	 * @param items
	 *            the list items to be added.
	 */
	public java.util.List<ListItem> addItems(ListItem[] items) {
		java.util.List<ListItem> itemList = new java.util.ArrayList<ListItem>();
		for (ListItem itemClone : items) {
			TextListItemElement itemElement = (TextListItemElement) (itemClone.getOdfElement().cloneNode(true));
			listElement.appendChild(itemElement);
			itemList.add(new ListItem(itemElement));
		}
		return itemList;
	}

	/**
	 * Insert the list items at the certain location
	 * by specifying an array of ListItem.
	 * 
	 * @param location
	 *            the index to insert.
	 * @param items
	 *            the collection of items.
	 * @return the added items
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public java.util.List<ListItem> addItems(int location, ListItem[] items) {
		java.util.List<ListItem> listCollection = new ArrayList<ListItem>();
		Node refNode = getItemByLocation(location);
		for (int i = items.length - 1; i >= 0; i--) {
			TextListItemElement itemElement = (TextListItemElement) (items[i].getOdfElement().cloneNode(true));
			listElement.insertBefore(itemElement, refNode);
			ListItem item = new ListItem(itemElement);
			refNode = itemElement;
			listCollection.add(item);
		}
		return listCollection;
	}

	/**
	 * Return the item at the specified location in this List.
	 * 
	 * @param location
	 *            the index of the element to be returned.
	 * @return the element at the specified location.
	 * 
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public ListItem getItem(int location) {
		return new ListItem(getItemByLocation(location));
	}

	/**
	 * Get all of the list items.
	 * 
	 * @return all of list items.
	 */
	public java.util.List<ListItem> getItems() {
		java.util.List<ListItem> itemList = new java.util.ArrayList<ListItem>();
		Node firstNode = listElement.getFirstChild();
		while (firstNode != null) {
			if (firstNode instanceof TextListItemElement) {
				itemList.add(new ListItem((TextListItemElement) firstNode));
			}
			firstNode = firstNode.getNextSibling();
		}
		return itemList;
	}

	/**
	 * Return the number of direct child items in this List.
	 * 
	 * @return the number of direct child items in this List.
	 */
	public int size() {
		int size = 0;
		Node firstNode = listElement.getFirstChild();
		while (firstNode != null) {
			if (firstNode instanceof TextListItemElement) {
				size++;
			}
			firstNode = firstNode.getNextSibling();
		}
		return size;
	}

	/**
	 * Replace the item at the specified location in this List with the
	 * specified item.
	 * 
	 * @param location
	 *            the index to put the specified item.
	 * @param item
	 *            the new item to be added.
	 * @return the previous element at the index.
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public ListItem set(int location, ListItem item) {
		TextListItemElement itemElement = (TextListItemElement) (item.getOdfElement().cloneNode(true));
		Node oldNode = getItemByLocation(location);
		listElement.replaceChild(itemElement, oldNode);
		ListItem newItem = new ListItem(itemElement);
		newItem.setParagraphDecorator(decorator);
		return newItem;
	}

	/**
	 * Replace the item at the specified location in this List with the
	 * specified item content.
	 * 
	 * @param location
	 *            the index to insert. The start number is 0.
	 * @param itemContent
	 *            the list item content to be added.
	 * @return the previous element at the index.
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public ListItem set(int location, String itemContent) {
		OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
		TextListItemElement listItemElement = ownerDocument.newOdfElement(TextListItemElement.class);
		Node oldNode = getItemByLocation(location);
		listElement.replaceChild(listItemElement, oldNode);
		ListItem item = new ListItem(listItemElement);
		item.setParagraphDecorator(decorator);
		item.setTextContent(itemContent);
		return item;
	}

	/**
	 * Remove the item at the specified location from this List.
	 * 
	 * @param location
	 *            the index of the item to be removed.
	 * @return true if this List is modified, false otherwise.
	 * @exception IndexOutOfBoundsException
	 *                when the <code>location</code> is out of the List range.
	 */
	public boolean removeItem(int location) {
		TextListItemElement itemElement = getItemByLocation(location);
		if (itemElement == null) {
			return false;
		} else {
			OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
			Document doc = (Document) ownerDocument.getDocument();
			doc.removeElementLinkedResource(itemElement);
			listElement.removeChild(itemElement);
			return true;
		}
	}

	/**
	 * Remove the specified item from this List.
	 * 
	 * @param item
	 *            the item to be removed.
	 * @return true if this List is modified, false otherwise.
	 */
	public boolean removeItem(ListItem item) {
		TextListItemElement itemElement = item.getOdfElement();
		OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
		Document doc = (Document) ownerDocument.getDocument();
		doc.removeElementLinkedResource(itemElement);
		Node removedNode = listElement.removeChild(itemElement);
		if (removedNode == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Remove all the items in the collection from this list
	 * 
	 * @param items
	 *            the collection of items to be removed.
	 * @return true if this List is modified, false otherwise.
	 */
	public boolean removeItems(java.util.List<ListItem> items) {
		boolean listChanged = false;
		for (ListItem item : items) {
			TextListItemElement itemElement = item.getOdfElement();
			OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
			Document doc = (Document) ownerDocument.getDocument();
			doc.removeElementLinkedResource(itemElement);
			Node removedNode = listElement.removeChild(itemElement);
			if (removedNode != null) {
				listChanged = true;
			}
		}
		return listChanged;
	}

	/**
	 * Remove all items from this List.
	 */
	public void clear() {
		Node firstChild = listElement.getFirstChild();
		while (firstChild != null) {
			if (firstChild instanceof TextListItemElement) {
				Node removedNode = firstChild;
				firstChild = firstChild.getNextSibling();
				listElement.removeChild(removedNode);
			} else {
				firstChild = firstChild.getNextSibling();
			}
		}
	}

	/**
	 * Remove this list from its container.
	 */
	public void remove() {
		Node parentElement = listElement.getParentNode();
		OdfFileDom ownerDocument = (OdfFileDom) listElement.getOwnerDocument();
		Document doc = (Document) ownerDocument.getDocument();
		doc.removeElementLinkedResource(listElement);
		parentElement.removeChild(listElement);
	}

	/**
	 * Return whether the numbering of this list is continuing,
	 * or whether the numbering of the preceding list is continued or not.
	 * 
	 * @return true if the numbering of this list is continuing,
	 *         false if not.
	 */
	public boolean isContinueNumbering() {
		Boolean isContinueNumbering = listElement.getTextContinueNumberingAttribute();
		if (isContinueNumbering == null) {
			String continueList = listElement.getTextContinueListAttribute();
			if (continueList != null) {
				return true;
			} else {
				return false;
			}
		} else {
			return isContinueNumbering;
		}
	}

	/**
	 * Set whether the numbering of the preceding list is continued or not. This
	 * method will set the attribute "text:continue-numbering" of list element.
	 * As ODF specification describes, this attribute is ignored, if attribute
	 * <text:continue-list> is present, the user can call
	 * <code>setContinueList(List)</code> to set this attribute. This method is
	 * a easy way to set a list continue numbering, while
	 * <code>setContinueList(List)</code> is an advance way to set a list
	 * numbering. For example, there are three lists ListA, ListB and ListC in
	 * order. If the user call set ListC.setContinueNumbering, the first list
	 * item in ListC is the number of the last item in ListB incremented by one.
	 * It easy, no need to get the reference of ListB. While if the user need
	 * the first list item in ListC is the number of the last item in ListA
	 * incremented by one, he must use ListC.setContinueList(ListA).
	 * 
	 * 
	 * @param isContinueNumbering
	 *            If <code>isContinueNumbering</code> is true, and
	 *            text:continue-list attribute is not present and the numbering
	 *            style of the preceding list is the same as the current list,
	 *            the number of the first list item in the current list is the
	 *            number of the last item in the preceding list incremented by
	 *            one.the list is continue numbering, otherwise if the
	 *            text:continue-list attribute is not present, the numbering of
	 *            the preceding list is not continued.
	 * @see #setContinueList(List)
	 */
	public void setContinueNumbering(boolean isContinueNumbering) {
		if (getType() == ListType.NUMBER) {
			listElement.setTextContinueNumberingAttribute(isContinueNumbering);
		}
	}

	/**
	 * Get the preceding list whose numbering is continued by this list. 
	 * <p>Now only support to get the continued list reference in the same ListContainer and the same Level.
	 * 
	 * @return the continued list of this list. If the list has no continued list,
	 *         it will return null.
	 */
	public List getContinueList() {
		List continueList = null;
		if (isContinueNumbering()) {
			TextListElement continueListElement = null;
			String continueListID = listElement.getTextContinueListAttribute();
			if (continueListID != null) {
				Node parentElement = listElement.getParentNode();
				Node firstNode = parentElement.getFirstChild();
				while (firstNode != null) {
					if (firstNode instanceof TextListElement) {
						TextListElement listElement = (TextListElement) firstNode;
						String xmlID = listElement.getXmlIdAttribute();
						if (continueListID.equals(xmlID)) {
							continueListElement = (TextListElement) firstNode;
							break;
						}
					}
					firstNode = firstNode.getNextSibling();
				}
			} else {
				Node preNode = listElement.getPreviousSibling();
				while (preNode != null) {
					if (preNode instanceof TextListElement) {
						continueListElement = (TextListElement) preNode;
						break;
					}
					preNode = preNode.getPreviousSibling();
				}
			}
			continueList = new List(continueListElement);
		}
		return continueList;
	}

	/**
	 * Set the list whose numbering is continued by this list.
	 * This is an advance way to set a list
	 * numbering. For example, there are three lists ListA, ListB and ListC in
	 * order. If the user needs the first list item in ListC is the number of the
	 * last item in ListA incremented by one, he must use
	 * ListC.setContinueList(ListA).
	 * 
	 * @param continueList
	 *            the continued list of this list.
	 * @see #setContinueNumbering(boolean)
	 */
	public void setContinueList(List continueList) {
		if (getType() == ListType.NUMBER) {
			String xmlId = continueList.listElement.getXmlIdAttribute();
			if (xmlId != null) {
				listElement.setTextContinueListAttribute(xmlId);
			} else {
				xmlId = getUniqueXMLID();
				continueList.listElement.setXmlIdAttribute(xmlId);
				listElement.setTextContinueListAttribute(xmlId);
			}
			listElement.setTextContinueNumberingAttribute(true);
		}
	}

	/**
	 * Get the level of this list.
	 * <p>
	 * Every list has a list level. If a list is not contained in another list,
	 * its list level is 1. If a list is contained within another list, the list
	 * level of the contained list is the list level of the list in which it is
	 * contained incremented by one. If a list is contained in a table cell or
	 * text box, its list level returns to 1, even though the table or text box
	 * may be nested in another list. Every list with a list level of 1 defines
	 * a list and the counter domain for its list items and any sub list of that
	 * list. Each sub list starts a counter for its list items and any sub list
	 * it may contain.
	 * 
	 * @return list level.
	 */
	public int getLevel() {
		int level = 1;
		Node parentNode = listElement.getParentNode();
		while (parentNode != null) {
			if (parentNode instanceof TextListElement) {
				level++;
			}
			if (parentNode instanceof TableTableCellElementBase) {
				break;
			}
			parentNode = parentNode.getParentNode();
		}
		return level;
	}

	/**
	 * Get the instance of TextListElement which represents this list.
	 * 
	 * @return the instance of TextListElement.
	 */
	public TextListElement getOdfElement() {
		return listElement;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		int level = getLevel();
		String levelPrefix = "";
		int i = 1;
		while (i < level) {
			levelPrefix += " ";
			i++;
		}
		String splitStr = "";
		String header = getHeader();
		if (header != null) {
			strBuilder.append(levelPrefix);
			strBuilder.append(header);
			splitStr = "\n";
		}
		String itemPrefix = "• ";
		int j = 0;
		if (getType() == ListType.NUMBER) {
			itemPrefix = ". ";
			j = 1;
		}
		for (ListItem item : getItems()) {
			strBuilder.append(splitStr);
			strBuilder.append(levelPrefix);
			if (j > 0) {
				strBuilder.append(j++);
			}
			strBuilder.append(itemPrefix);
			strBuilder.append(item.toString());
			splitStr = "\n";
		}
		return strBuilder.toString();
	}

	// find the insert node.
	private TextListItemElement getItemByLocation(int location) {
		if (location < 0) {
			throw new IndexOutOfBoundsException("the location " + location + " is is out of the List range.");
		}
		Node firstNode = listElement.getFirstChild();
		TextListItemElement positionNode = null;
		int i = 0;
		while (firstNode != null) {
			if (firstNode instanceof TextListItemElement) {
				if (i == location) {
					break;
				}
				i++;
			}
			firstNode = firstNode.getNextSibling();
		}
		if ((i == location) && (firstNode instanceof TextListItemElement)) {
			positionNode = (TextListItemElement) firstNode;
		}
		if ((location != 0) && (i < location)) {
			throw new IndexOutOfBoundsException("the location " + location + " is is out of the List range.");
		}
		return positionNode;
	}

	private String getUniqueXMLID() {
		return "list" + Math.round(Math.random() * 100000000);
	}

	/**
	 * Answers an Iterator on the items of this List. The items are iterated in
	 * the same order that they occur in the List.
	 * 
	 * @return an Iterator on the items of this List
	 * 
	 * @see java.util.Iterator
	 */
	/*
	 * public Iterator<ListItem> iterator() { throw new
	 * UnsupportedOperationException("todo"); }
	 */
}
