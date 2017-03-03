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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.common.navigation.Navigation;
import org.odftoolkit.simple.common.navigation.Selection;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.ParagraphProperties;
import org.odftoolkit.simple.style.TableProperties;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.text.Paragraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a decorator class of TextSelection, which help user replace a text content with a Table.
 * 
 */
public class TableSelection extends Selection {

	private TextSelection textSelection;
	private Table tableContainer;
	private Table sourceTable;
	
	/**
	 * Replace the content with a Table , the table can be in the same TextDocument or in a different Document.
	 * 
	 * @param paragraph the reference table to replace.
	 * 
	 * @return the new Table in the TextDocument
	 */
	public Table replaceWithTable(Table table) {
		this.sourceTable=table;
		if (search instanceof TextNavigation) {
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
			boolean continued = false;
			TextNavigation textSearch = (TextNavigation) search;
			if (textSearch != null
					&& textSearch.getReplacedItem() != null
					&& textSearch.getReplacedItem().getElement() == this.textSelection
							.getElement()) {
				continued = true;
			} else {
				textSearch.setHandlePageBreak(false);
			}
			preparetableContainer(leftLength, index, continued);
			Selection.SelectionManager.unregisterItem(this.textSelection);
			if (textSearch != null) {
				textSearch.setReplacedItem(this.textSelection);
				Paragraph lastParagraph = getLastParagraphInTable(tableContainer);
				OdfElement newStartPoint;
				if (lastParagraph != null) {
					newStartPoint = lastParagraph.getOdfElement();
				} else {
					newStartPoint = tableContainer.getOdfElement();
				}
				String content = TextExtractor.getText(newStartPoint);
				TextSelection selected = newTextSelection(textSearch,
						this.textSelection.getText(), newStartPoint,
						content.length() - 1);
				textSearch.setSelectedItem(selected);
			}
		}
		return tableContainer;
	}

	/**
	 * Construct a TableSelection with TextSelection. Then user can replace text
	 * content with {@link org.odftoolkit.simple.table.Table table}.
	 * 
	 * @param selection
	 *            the TextSelection to be decorated.
	 */
	public TableSelection(TextSelection selection) {
		textSelection = selection;
		search = textSelection.getTextNavigation();
		tableContainer = null;
	}

	/**
	 * Delete the selection from the document the other matched selection in the
	 * same container element will be updated automatically because the start
	 * index of the following selections will be changed when the previous
	 * selection has been deleted.
	 * 
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	@Override
	public void cut() throws InvalidNavigationException {
		textSelection.cut();
	}

	/**
	 * Paste this selection just after a specific selection.
	 * 
	 * @param positionItem
	 *            a selection that is used to point out the position
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	@Override
	public void pasteAtEndOf(Selection positionItem) throws InvalidNavigationException {
		textSelection.pasteAtEndOf(positionItem);
	}
	private void delete(int fromIndex, int leftLength, Node pNode) {
		if ((fromIndex == 0) && (leftLength == 0)) {
			return;
		}
		int nodeLength = 0;
		Node node = pNode.getFirstChild();
		while (node != null) {
			if ((fromIndex == 0) && (leftLength == 0)) {
				return;
			}
			if (node.getNodeType() == Node.TEXT_NODE) {
				nodeLength = node.getNodeValue().length();
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				// text:s
				if (node.getLocalName().equals("s")) {
					try {
						nodeLength = Integer.parseInt(((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT
								.getUri(), "c"));
					} catch (Exception e) {
						nodeLength = 1;
					}
				} else if (node.getLocalName().equals("line-break")) {
					nodeLength = 1;
				} else if (node.getLocalName().equals("tab")) {
					nodeLength = 1;
				} else {
					nodeLength = TextExtractor.getText((OdfElement) node).length();
				}
			}
			if (nodeLength <= fromIndex) {
				fromIndex -= nodeLength;
			} else {
				// the start index is in this node
				if (node.getNodeType() == Node.TEXT_NODE) {
					String value = node.getNodeValue();
					StringBuffer buffer = new StringBuffer();
					buffer.append(value.substring(0, fromIndex));
					int endLength = fromIndex + leftLength;
					int nextLength = value.length() - endLength;
					fromIndex = 0;
					if (nextLength >= 0) {
						// delete the result
						buffer.append(value.substring(endLength, value.length()));
						leftLength = 0;
					} else {
						leftLength = endLength - value.length();
					}
					node.setNodeValue(buffer.toString());

				} else if (node.getNodeType() == Node.ELEMENT_NODE) {
					// if text:s?????????
					// text:s
					if (node.getLocalName().equals("s")) {
						// delete space
						((TextSElement) node).setTextCAttribute(new Integer(nodeLength - fromIndex));
						leftLength = leftLength - (nodeLength - fromIndex);
						fromIndex = 0;
					} else if (node.getLocalName().equals("line-break") || node.getLocalName().equals("tab")) {
						fromIndex = 0;
						leftLength--;
					} else {
						delete(fromIndex, leftLength, node);
						int length = (fromIndex + leftLength) - nodeLength;
						leftLength = length > 0 ? length : 0;
						fromIndex = 0;
					}
				}
			}
			node = node.getNextSibling();
		}
	}

	/**
	 * Paste this selection just before a specific selection.
	 * 
	 * @param positionItem
	 *            a selection that is used to point out the position
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	@Override
	public void pasteAtFrontOf(Selection positionItem) throws InvalidNavigationException {
		textSelection.pasteAtFrontOf(positionItem);
	}

	protected void refresh(int offset) {
		textSelection.refresh(offset);
	}

	protected void refreshAfterFrontalDelete(Selection deletedItem) {
		textSelection.refreshAfterFrontalDelete(deletedItem);
	}

	protected void refreshAfterFrontalInsert(Selection insertedItem) {
		textSelection.refreshAfterFrontalInsert(insertedItem);
	}

	private void preparetableContainer(int leftLength, int index,
			boolean continued) {
		if (tableContainer == null) {
			String pos = "middle";
			OdfElement rightparentElement = textSelection.getContainerElement();
			int nodeLength = TextExtractor.getText(rightparentElement).length();
			if(index==0){
				
				if(leftLength==nodeLength){
					//Replace whole Paragraph
					Paragraph orgparagraph = Paragraph
							.getInstanceof((TextParagraphElementBase) rightparentElement);
					TextDocument document = (TextDocument) orgparagraph
							.getOwnerDocument();
					tableContainer = document.insertTable(orgparagraph,
							sourceTable, false);
					pos = "whole";
					handlePageBreak(orgparagraph, pos, continued);

					rightparentElement.getParentNode().removeChild(
							rightparentElement);
				}else{
					//at the start of original Paragraph, insert before original Paragraph
					delete(index, leftLength, rightparentElement);
					Paragraph orgparagraph = Paragraph
							.getInstanceof((TextParagraphElementBase) rightparentElement);
					TextDocument document = (TextDocument) orgparagraph
							.getOwnerDocument();
					tableContainer = document.insertTable(orgparagraph,
							sourceTable, true);
					pos = "head";
					handlePageBreak(orgparagraph, pos, continued);
			}
			} else if (nodeLength == (index + leftLength)) {
				//at the end of original Paragraph, insert after original Paragraph
				delete(index, leftLength, rightparentElement);
				Paragraph orgparagraph = Paragraph
						.getInstanceof((TextParagraphElementBase) rightparentElement);
				TextDocument document = (TextDocument) orgparagraph
						.getOwnerDocument();
				tableContainer = document.insertTable(orgparagraph,
						sourceTable, false);
				handlePageBreak(orgparagraph, pos, continued);
			}else{
				//at the middle of original Paragraph, split original Paragraph, insert before the second Paragraph.
				delete(index, leftLength, rightparentElement);
				Node leftparentElement = rightparentElement.cloneNode(true);
				rightparentElement.getParentNode().insertBefore(
						leftparentElement, rightparentElement);
				nodeLength = TextExtractor.getText(
						(OdfElement) leftparentElement).length();
				delete(index, nodeLength-index, leftparentElement);
				delete(0, index, rightparentElement);
				Paragraph orgparagraph = Paragraph
						.getInstanceof((TextParagraphElementBase) rightparentElement);
				TextDocument document = (TextDocument) orgparagraph
						.getOwnerDocument();
				tableContainer = document.insertTable(orgparagraph,
						sourceTable, true);
				if (!continued)
					textSelection.cleanBreakProperty(orgparagraph);
			}
		} else{
			TextDocument document = (TextDocument) tableContainer
					.getOwnerDocument();
			TableTableElement newTEle = (TableTableElement) document
					.insertOdfElement(tableContainer.getOdfElement(),
							tableContainer.getOwnerDocument(),
							sourceTable.getOdfElement(), true);
			tableContainer.getOdfElement().getParentNode()
					.removeChild(tableContainer.getOdfElement());
			Table table = Table.getInstance(newTEle);
			tableContainer=table;
		}
	}
	private Paragraph getLastParagraphInTable(Table table) {
		Paragraph paragraph = null;
		int rowCount = table.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			Row row = table.getRowByIndex(i);
			int cellCount = row.getCellCount();
			for (int j = cellCount - 1; j >= 0; j--) {
				Cell cell = row.getCellByIndex(j);
				paragraph = cell.getParagraphByReverseIndex(0, false);
				if (paragraph != null)
					return paragraph;
			}
		}
		return paragraph;
	}
	private TableProperties getTablePropertiesForWrite() {
		OdfStyleBase style = tableContainer.getStyleHandler()
				.getStyleElementForRead();
		if (style == null || style.getLocalName().equals("default-style")) {
			OdfStyle element = tableContainer.getStyleHandler()
					.getStyleElementForWrite();
			NodeList nodes = element.getChildNodes();
			int size = nodes.getLength();
			for (int i = 0; i < size; i++) {
				element.removeChild(nodes.item(0));
			}
		}
		TableProperties properties = tableContainer.getStyleHandler()
				.getTablePropertiesForWrite();
		return properties;
	}
	private OdfStyleBase getParagraphStyleElementForWrite() {
		OdfStyleBase style = tableContainer.getStyleHandler()
				.getStyleElementForRead();
		OdfStyle element = tableContainer.getStyleHandler()
				.getStyleElementForWrite();
		if (style == null || style.getLocalName().equals("default-style")) {
			NodeList nodes = element.getChildNodes();
			int size = nodes.getLength();
			for (int i = 0; i < size; i++) {
				element.removeChild(nodes.item(0));
			}
		}
		return element;
	}
	private void handlePageBreak(Paragraph origParagraph, String pos,
			boolean continued) {
		if (continued
				&& this.textSelection.getTextNavigation().isHandlePageBreak())
			return;
		ParagraphProperties orgParaPty = origParagraph.getStyleHandler()
				.getParagraphPropertiesForRead();
		boolean handleBreak = false;
		String posInPara = "middle";
		if (continued && pos.equals("whole")) {
			posInPara = "end";
		} else if (continued && pos.endsWith("head")) {
			posInPara = "middle";
		} else if (continued && pos.endsWith("end")) {
			posInPara = "end";
		} else if (!continued && pos.endsWith("whole")) {
			posInPara = "whole";
		} else if (!continued && pos.endsWith("head")) {
			posInPara = "head";
		} else if (!continued && pos.endsWith("end")) {
			posInPara = "end";
		}
		if (orgParaPty != null) {
			String breakAttribute = orgParaPty.getBreakBefore();
			if (breakAttribute != null) {
				if (posInPara.equals("head") || posInPara.equals("whole")) {
					getTablePropertiesForWrite().setBreak("before",
							breakAttribute);
					handleBreak = true;
				}
			}
			breakAttribute = orgParaPty.getBreakAfter();
			if (breakAttribute != null) {
				if (posInPara.equals("end") || posInPara.equals("whole")) {
					getTablePropertiesForWrite().setBreak("after",
							breakAttribute);
					handleBreak = true;
				}
			}
		}
		String masterStyle = origParagraph
				.getStyleHandler()
				.getStyleElementForRead()
				.getOdfAttributeValue(
						OdfName.newName(OdfDocumentNamespace.STYLE,
								"master-page-name"));
		if (masterStyle != null && !masterStyle.isEmpty()) {
			if (posInPara.equals("head") || posInPara.equals("whole")) {
				getParagraphStyleElementForWrite().setOdfAttributeValue(
						OdfName.newName(OdfDocumentNamespace.STYLE,
								"master-page-name"), masterStyle);
				handleBreak = true;
				try {
					int pageNumber = orgParaPty.getPageNumber();
					if (pos.equals("head")) {
						tableContainer.getStyleHandler()
								.getParagraphPropertiesForWrite()
								.setPageNumber(pageNumber);
					}
				} catch (NumberFormatException e) {
					Logger.getLogger(ParagraphSelection.class.getName()).log(
							Level.SEVERE, e.getMessage(), "NumberFormatException");
				}
			}
		}
		if (handleBreak && !posInPara.equals("whole"))
			textSelection.cleanBreakProperty(origParagraph);
	}
	private class TextSelectionForTableReplacement extends TextSelection {
		private OdfElement mContainer;
		TextSelectionForTableReplacement(Navigation search, String text,
				OdfElement containerElement, int index) {
			super(search, text, containerElement, index);
			if (containerElement instanceof TableTableElement)
				mContainer = containerElement;
		}
		@Override
		public OdfElement getContainerElement() {
			OdfElement element = super.getContainerElement();
			if (element == null) {
				element = mContainer;
			}
			return element;
		}
	}
	TextSelection newTextSelection(Navigation search, String text,
			OdfElement containerElement, int index) {
		TextSelection selection = new TextSelectionForTableReplacement(search,
				text, containerElement, index);
		Selection.SelectionManager.registerItem(selection);
		return selection;
	}
}
