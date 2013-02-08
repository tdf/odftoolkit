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

import java.util.IdentityHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.table.Cell;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A derived <code>Navigation</code> class used to navigate the text content,
 * which can search the document and find the matched text and return
 * <code>TextSelection</code> instance.
 */
public class TextNavigation extends Navigation {

	private String mMatchedElementName = "text:p,text:h";
	private Pattern mPattern;
	private Document mDocument;
	private OdfElement mElement;
	private TextSelection mNextSelectedItem;
	private TextSelection mTempSelectedItem;
	private TextSelection mReplacedItem;
	private boolean handlePageBreak;
	private String mNextText;
	private int mNextIndex;
	private boolean mbFinishFindInHeaderFooter;
	private IdentityHashMap<OdfElement, OdfElement> modifiedStyleList;

	/**
	 * Construct <code>TextNavigation</code> with matched condition and
	 * navigation scope.
	 * 
	 * @param pattern
	 *            the matched pattern String
	 * @param doc
	 *            the navigation scope
	 */
	public TextNavigation(String pattern, Document doc) {
		mPattern = Pattern.compile(pattern);
		mDocument = doc;
		mElement = null;
		mNextSelectedItem = null;
		mTempSelectedItem = null;
		mbFinishFindInHeaderFooter = false;
		setHandlePageBreak(false);
	}

	/**
	 * Construct <code>TextNavigation</code> with matched condition and
	 * navigation scope.
	 * 
	 * @param pattern
	 *            the matched pattern String
	 * @param element
	 *            the ODF element whose content will be navigated.
	 * @since 0.5
	 */
	public TextNavigation(String pattern, OdfElement element) {
		mPattern = Pattern.compile(pattern);
		mDocument = null;
		mElement = element;
		mNextSelectedItem = null;
		mTempSelectedItem = null;
		mbFinishFindInHeaderFooter = false;
	}

	/**
	 * Check if has next <code>TextSelection</code> with satisfied content
	 * pattern.
	 * 
	 * @see org.odftoolkit.simple.common.navigation.Navigation#hasNext()
	 */
	@Override
	public boolean hasNext() {
		mTempSelectedItem = findNext(mNextSelectedItem);
		return (mTempSelectedItem != null);
	}
	void setSelectedItem(TextSelection nextSelectedItem) {
		this.mNextSelectedItem = nextSelectedItem;
	}
	TextSelection getSelectedItem() {
		return this.mNextSelectedItem;
	}

	/**
	 * Get next <code>TextSelection</code>.
	 * 
	 * @see org.odftoolkit.simple.common.navigation.Navigation#nextSelection()
	 */
	@Override
	public Selection nextSelection() {
		if (mTempSelectedItem != null) {
			mNextSelectedItem = mTempSelectedItem;
			mTempSelectedItem = null;
		} else {
			mNextSelectedItem = findNext(mNextSelectedItem);
		}
		if (mNextSelectedItem == null) {
			return null;
		} else {
			Selection.SelectionManager.registerItem(mNextSelectedItem);
			return mNextSelectedItem;
		}
	}

	/**
	 * Check if the text content of element match the specified matched
	 * condition, which is stated when the <code>TextNavigation</code> created.
	 * 
	 * @param element
	 *            navigate this element
	 * @return true if the text content of this element match this pattern;
	 *         false if not match
	 */
	@Override
	public boolean match(Node element) {
		if (element instanceof OdfElement) {
			String content = TextExtractor.getText((OdfElement) element);
			Matcher matcher = mPattern.matcher(content);
			if (matcher.find()) {
				// check whether this container is minimum
				Node childNode = element.getFirstChild();
				while (childNode != null) {
					String childContent = getText(childNode);
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
					mNextIndex = matcher.start();
					int eIndex = matcher.end();
					mNextText = content.substring(mNextIndex, eIndex);
					return true;
				}
			}
		}
		return false;
	}

	private String getText(Node node) {
		if (node.getNodeType() == Node.TEXT_NODE)
			return node.getNodeValue();
		if (node instanceof OdfElement)
			return TextExtractor.getText((OdfElement) node);
		return "";
	}

	/*
	 * Return the matched text might exist in header/footer.
	 */
	private TextSelection findInHeaderFooter(TextSelection selected) {
		OdfFileDom styledom = null;
		OdfOfficeMasterStyles masterpage = null;
		OdfElement element = null;

		if (selected != null) {
			OdfElement containerElement = selected.getContainerElement();
			int index = selected.getIndex();
			String content = TextExtractor.getText(containerElement);

			int nextIndex = -1;
			Matcher matcher = mPattern.matcher(content);
			// start from the end index of the selected item
			if (matcher.find(index + selected.getText().length())) {
				// here just consider \n\r\t occupy one char
				nextIndex = matcher.start();
				int eIndex = matcher.end();
				mNextText = content.substring(nextIndex, eIndex);
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
				return createSelection(element, mNextIndex);
			} else {
				return null;
			}

		} catch (Exception ex) {
			Logger.getLogger(TextNavigation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return null;
	}

	/*
	 * Found the next <code>Selection</code> start from the
	 * <code>selected</code>.
	 */
	private TextSelection findNext(TextSelection selected) {
		if (!mbFinishFindInHeaderFooter) {
			// find in document.
			if (mElement == null) {
				TextSelection styleselected = findInHeaderFooter(selected);
				if (styleselected != null) {
					return styleselected;
				}
			}
			selected = null;
			mbFinishFindInHeaderFooter = true;
		}
		OdfElement rootElement = null;
		try {
			if (mElement != null) {
				rootElement = mElement;
			} else {
				rootElement = mDocument.getContentRoot();
			}
		} catch (Exception ex) {
			Logger.getLogger(TextNavigation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		if (selected == null) {
			OdfElement element = (OdfElement) getNextMatchElementInTree(rootElement, rootElement);
			if (element != null) {
				return createSelection(element, mNextIndex);
			} else {
				return null;
			}
		}
		OdfElement containerElement = selected.getContainerElement();
		int index = selected.getIndex();
		String content = TextExtractor.getText(containerElement);

		int nextIndex = -1;
		Matcher matcher = mPattern.matcher(content);
		// start from the end index of the selected item
		if (!selected.isSelectionReplaced()) {
		if (((content.length() > index + selected.getText().length()))
				&& (matcher.find(index + selected.getText().length()))) {
				nextIndex = matcher.start();
				int eIndex = matcher.end();
				mNextText = content.substring(nextIndex, eIndex);
			}
		} else if (((content.length() >= index + selected.getText().length()))
				&& (matcher.find(index))) {
			// here just consider \n\r\t occupy one char
			nextIndex = matcher.start();
			int eIndex = matcher.end();
			mNextText = content.substring(nextIndex, eIndex);
		}
		if (nextIndex != -1) {
			return createSelection(selected.getContainerElement(), nextIndex);
		} else {
			OdfElement element = (OdfElement) getNextMatchElementInTree(containerElement, rootElement);
			if (element != null) {
				return createSelection(element, mNextIndex);
			} else {
				return null;
			}
		}
	}

	/*
	 * In order to keep the consist between value and display text, spreadsheet
	 * and chart document should use <code>CellSelection</code>.
	 */
	private TextSelection createSelection(OdfElement containerElement, int nextIndex) {
		TextSelection item = null;
		Node parent = containerElement.getParentNode();
		while (parent != null) {
			if (TableTableCellElementBase.class.isInstance(parent)) {
				TableTableCellElementBase cellElement = (TableTableCellElementBase) parent;
				Cell cell = Cell.getInstance(cellElement);
				item = new CellSelection(this, mNextText, containerElement,
						nextIndex, cell);
				break;
			} else {
				OdfName odfName = ((OdfElement) parent).getOdfName();
				String ns = odfName.getPrefix();
				if ("text".equals(ns)) {
					parent = parent.getParentNode();
				} else {
					break;
				}
			}
		}
		if (item == null) {
			item = new TextSelection(this, mNextText, containerElement,
					nextIndex);
		}
		return item;
	}
	OdfElement getModifiedStyleElement(OdfElement styleElement) {
		if (modifiedStyleList == null)
			return null;
		return modifiedStyleList.get(styleElement);
	}
	void addModifiedStyleElement(OdfElement styleElment,
			OdfElement modifiedStyleElement) {
		if (modifiedStyleElement != null) {
			if (modifiedStyleList == null) {
				modifiedStyleList = new IdentityHashMap<OdfElement, OdfElement>();
			}
			modifiedStyleList.put(styleElment, modifiedStyleElement);
		}
	}
	boolean isHandlePageBreak() {
		return handlePageBreak;
	}
	void setHandlePageBreak(boolean handlePageBreak) {
		this.handlePageBreak = handlePageBreak;
	}
	void setReplacedItem(TextSelection replacedItem) {
		this.mReplacedItem = replacedItem;
	}
	TextSelection getReplacedItem() {
		return this.mReplacedItem;
	}
}
