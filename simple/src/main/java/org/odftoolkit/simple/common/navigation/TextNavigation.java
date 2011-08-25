/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2009, 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple.common.navigation;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.common.WhitespaceProcessor;
import org.odftoolkit.simple.table.Cell;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A derived Navigation class used for navigate the text content
 * it is used to search the document and find the matched text 
 * and would return TextSelection instance.
 */
public class TextNavigation extends Navigation {

	private String mMatchedElementName = "text:p,text:h";
	private Pattern mPattern;
	private Document mDocument;
	private TextSelection mCurrentSelectedItem;
	private String mCurrentText;
	private int mCurrentIndex;
	private boolean mbFinishFindInHeaderFooter;

	/**
	 * Construct TextNavigation with matched condition and navigation scope
	 * @param pattern	the matched pattern String
	 * @param doc	the navigation scope
	 */
	public TextNavigation(String pattern, Document doc) {
		mPattern = Pattern.compile(pattern);
		mDocument = doc;
		mCurrentSelectedItem = null;
		mbFinishFindInHeaderFooter = false;
	}
	
	/* (non-Javadoc)
	 * @see org.odftoolkit.simple.common.navigation.Navigation#getCurrentItem()
	 */
	@Override
	public Selection getCurrentItem() {
		Selection.SelectionManager.registerItem(mCurrentSelectedItem);
		return mCurrentSelectedItem;
	}

	/* (non-Javadoc)
	 * @see org.odftoolkit.simple.common.navigation.Navigation#hasNext()
	 */
	@Override
	public boolean hasNext() {
		mCurrentSelectedItem = findnext(mCurrentSelectedItem);
		return (mCurrentSelectedItem != null);
	}

	/**
	 * Check if the text content of element match the specified pattern string
	 * @param element	navigate this element
	 * @return true if the text content of this element match this pattern; 
	 * 		   false if not match
	 */
	@Override
	public boolean match(Node element) {
		if (element instanceof OdfElement) {
			WhitespaceProcessor textProcessor = new WhitespaceProcessor();
			String content = textProcessor.getText(element);
			Matcher matcher = mPattern.matcher(content);
			if (matcher.find()) {
				// check whether this container is minimum
				Node childNode = element.getFirstChild();
				while (childNode != null) {
					WhitespaceProcessor childTextProcessor = new WhitespaceProcessor();
					String childContent = childTextProcessor.getText(childNode);
					Matcher childMatcher = mPattern.matcher(childContent);
					if (childMatcher.find()) {
						if (childNode.getNodeType() == Node.TEXT_NODE
								|| "text:span".equalsIgnoreCase(childNode.getNodeName())
								|| "text:a".equalsIgnoreCase(childNode.getNodeName())) {
							break;
						} else {
							return false;
						}
					} else {
						childNode = childNode.getNextSibling();
					}
				}
				if (mMatchedElementName.indexOf(element.getNodeName()) != -1) {
					// here just consider \n\r\t occupy one char
					mCurrentIndex = matcher.start();
					int eIndex = matcher.end();
					mCurrentText = content.substring(mCurrentIndex, eIndex);
					return true;
				}
			}
		}
		return false;
	}

	//the matched text might exist in header/footer
	private TextSelection findInHeaderFooter(TextSelection selected) {
		OdfFileDom styledom = null;
		OdfOfficeMasterStyles masterpage = null;
		OdfElement element = null;

		if (selected != null) {
			OdfElement containerElement = selected.getContainerElement();
			int index = selected.getIndex();
			WhitespaceProcessor textProcessor = new WhitespaceProcessor();
			String content = textProcessor.getText(containerElement);

			int nextIndex = -1;
			Matcher matcher = mPattern.matcher(content);
			//start from the end index of the selected item
			if (matcher.find(index + selected.getText().length())) {
				// here just consider \n\r\t occupy one char
				nextIndex = matcher.start();
				int eIndex = matcher.end();
				mCurrentText = content.substring(nextIndex, eIndex);
			}
			if (nextIndex != -1) {
				return createSelection(selected.getContainerElement(), nextIndex);
			}
		}
		try {
			styledom = mDocument.getStylesDom();
			NodeList list = styledom.getElementsByTagName("office:master-styles");
			if (styledom == null) {
				return null;
			}
			if (list.getLength() > 0) {
				masterpage = (OdfOfficeMasterStyles) list.item(0);
			} else {
				return null;
			}

			if (selected == null) {
				element = (OdfElement) getNextMatchElementInTree(masterpage, masterpage);
			} else {
				element = (OdfElement) getNextMatchElementInTree(selected.getContainerElement(), masterpage);
			}

			if (element != null) {
				return createSelection(element, mCurrentIndex);
			} else {
				return null;
			}

		} catch (Exception ex) {
			Logger.getLogger(TextNavigation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return null;
	}

	//found the next selection start from the 'selected' TextSelection
	private TextSelection findnext(TextSelection selected) {
		if (!mbFinishFindInHeaderFooter) {
			TextSelection styleselected = findInHeaderFooter(selected);
			if (styleselected != null) {
				return styleselected;
			}
			selected = null;
			mbFinishFindInHeaderFooter = true;
		}

		if (selected == null) {
			OdfElement element = null;
			try {
				element = (OdfElement) getNextMatchElement((Node) mDocument.getContentRoot());
			} catch (Exception ex) {
				Logger.getLogger(TextNavigation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);

			}
			if (element != null) {
				return createSelection(element, mCurrentIndex);
			} else {
				return null;
			}
		}

		OdfElement containerElement = selected.getContainerElement();
		int index = selected.getIndex();
		WhitespaceProcessor textProcessor = new WhitespaceProcessor();
		String content = textProcessor.getText(containerElement);

		int nextIndex = -1;
		Matcher matcher = mPattern.matcher(content);
		//start from the end index of the selected item
		if (matcher.find(index + selected.getText().length())) {
			// here just consider \n\r\t occupy one char
			nextIndex = matcher.start();
			int eIndex = matcher.end();
			mCurrentText = content.substring(nextIndex, eIndex);
		}
		if (nextIndex != -1) {
			return createSelection(selected.getContainerElement(), nextIndex);
		} else {
			OdfElement element = (OdfElement) getNextMatchElement((Node) containerElement);
			if (element != null) {
				return createSelection(element, mCurrentIndex);
			} else {
				return null;
			}
		}
	}
	
	// in order to keep the consist between value and display text, spreadsheet and chart document
	// should use CellSelection.
	private TextSelection createSelection(OdfElement containerElement, int nextIndex) {
		TextSelection item = null;
		Node parent = containerElement.getParentNode();
		while (parent != null) {
			if (TableTableCellElementBase.class.isInstance(parent)) {
				TableTableCellElementBase cellElement = (TableTableCellElementBase) parent;
				Cell cell = Cell.getInstance(cellElement);
				item = new CellSelection(mCurrentText, containerElement, nextIndex, cell);
				break;
			} else {
				OdfName odfName=((OdfElement)parent).getOdfName();
				String ns=odfName.getPrefix();
				if("text".equals(ns)){
					parent = parent.getParentNode();
				}else{
					break;
				}
			}
		}
		if (item == null) {
			item = new TextSelection(mCurrentText, containerElement, nextIndex);
		}
		return item;
	}
}
