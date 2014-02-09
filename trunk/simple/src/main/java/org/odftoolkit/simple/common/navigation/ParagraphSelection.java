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
import org.odftoolkit.odfdom.dom.attribute.style.StyleMasterPageNameAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.common.navigation.Selection;
import org.odftoolkit.simple.common.navigation.TextNavigation;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.style.ParagraphProperties;
import org.odftoolkit.simple.text.Paragraph;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a decorator class of TextSelection, which help user replace a text content with a Paragraph.
 * 
 */
public class ParagraphSelection extends Selection {

	private TextSelection textSelection;
	private Paragraph paragraphContainer;
	private Paragraph sourceParagraph;
	
	/**
	 * Replace the content with a paragraph, the paragraph can be in the same TextDocument or in a different Document.
	 * 
	 * @param paragraph
	 *            the reference paragraph to replace.
	 */
	public Paragraph replaceWithParagraph(Paragraph paragraph) {
		this.sourceParagraph=paragraph;
		if (search instanceof TextNavigation) {
			TextNavigation textSearch = (TextNavigation) search;
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
			boolean continued = false;
			if (textSearch != null
					&& textSearch.getReplacedItem() != null
					&& textSearch.getReplacedItem().getElement() == this.textSelection
							.getElement()) {
				continued = true;
			} else {
				textSearch.setHandlePageBreak(false);
			}
			prepareParagraphContainer(leftLength, index, continued);
			Selection.SelectionManager.unregisterItem(this.textSelection);
			if (textSearch != null) {
				textSearch.setReplacedItem(this.textSelection);
				OdfElement containerElement = paragraphContainer
						.getOdfElement();
				String content = TextExtractor.getText(containerElement);
				TextSelection selected = TextSelection.newTextSelection(
						textSearch, this.textSelection.getText(),
						containerElement, content.length() - 1);
				textSearch.setSelectedItem(selected);
			}
		}
		return paragraphContainer;
	}

	/**
	 * Construct a ParagraphSelection with TextSelection. Then user can replace text
	 * content with paragraph.
	 * 
	 * @param selection
	 *            the TextSelection to be decorated.
	 */
	public ParagraphSelection(TextSelection selection) {
		textSelection = selection;
		search = textSelection.getTextNavigation();
		paragraphContainer = null;
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

	private ParagraphProperties getParagraphPropertiesForWrite() {
		OdfStyleBase style = paragraphContainer.getStyleHandler()
				.getStyleElementForRead();
		if (style == null || style.getLocalName().equals("default-style")) {
			OdfStyle element = paragraphContainer.getStyleHandler()
					.getStyleElementForWrite();
			NodeList nodes = element.getChildNodes();
			int size = nodes.getLength();
			for (int i = 0; i < size; i++) {
				element.removeChild(nodes.item(0));
			}
		}
		ParagraphProperties properties = paragraphContainer.getStyleHandler()
				.getParagraphPropertiesForWrite();
		return properties;
	}
	private OdfStyleBase getParagraphStyleElementForWrite() {
		OdfStyleBase style = paragraphContainer.getStyleHandler()
				.getStyleElementForRead();
		OdfStyle element = paragraphContainer.getStyleHandler()
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
					getParagraphPropertiesForWrite().setBreak("before",
							breakAttribute);
					handleBreak = true;
				}
			}
			breakAttribute = orgParaPty.getBreakAfter();
			if (breakAttribute != null) {
				if (posInPara.equals("end") || posInPara.equals("whole")) {
					getParagraphPropertiesForWrite().setBreak("after",
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
						paragraphContainer.getStyleHandler()
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
			cleanBreakProperty(origParagraph);
	}
	private void cleanBreakProperty(Paragraph paragraph) {
		TextNavigation search = this.textSelection.getTextNavigation();
		if (search == null)
			throw new IllegalStateException("Navigation is null");
		OdfStyleBase styleElement = paragraph.getStyleHandler()
				.getStyleElementForRead();
		String name = styleElement.getAttribute("style:name");
		String newName = null;
		OdfElement modifiedStyleElement = search
				.getModifiedStyleElement(styleElement);
		if (modifiedStyleElement == null) {
			modifiedStyleElement = (OdfElement) styleElement.cloneNode(true);
			search.addModifiedStyleElement(styleElement, modifiedStyleElement);
			NodeList paragraphProperties = modifiedStyleElement
					.getElementsByTagName("style:paragraph-properties");
			if (paragraphProperties != null
					&& paragraphProperties.getLength() > 0) {
				StyleParagraphPropertiesElement property = (StyleParagraphPropertiesElement) paragraphProperties
						.item(0);
				property.removeAttribute("fo:break-before");
				property.removeAttribute("fo:break-after");
				property.removeAttribute("style:page-number");
			}
			modifiedStyleElement.removeAttribute("style:master-page-name");
			newName = name + "-" + makeUniqueName();
			NamedNodeMap attributes = modifiedStyleElement.getAttributes();
			if (attributes != null) {
				for (int i = 0; i < attributes.getLength(); i++) {
					Node item = attributes.item(i);
					String value = item.getNodeValue();
					if (name.equals(value)) {
						item.setNodeValue(newName);
						break;
					}
				}
			}
			styleElement.getParentNode().appendChild(modifiedStyleElement);
		} else {
			newName = modifiedStyleElement.getAttribute("style:name");
		}
		NamedNodeMap attributes = paragraph.getOdfElement().getAttributes();
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				Node item = attributes.item(i);
				String value = item.getNodeValue();
				if (name.equals(value)) {
					item.setNodeValue(newName);
					break;
				}
			}
		}
		this.textSelection.getTextNavigation().setHandlePageBreak(true);
	}
	String makeUniqueName() {
		return String.format("p%06x", (int) (Math.random() * 0xffffff));
	}
	private void prepareParagraphContainer(int leftLength, int index,
			boolean continued) {
		if (paragraphContainer == null) {
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
					paragraphContainer = document.insertParagraph(orgparagraph,
							sourceParagraph, false);
					NodeList cnl = rightparentElement.getChildNodes();
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
					paragraphContainer = document.insertParagraph(orgparagraph,
							sourceParagraph, true);
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
				paragraphContainer = document.insertParagraph(orgparagraph,
						sourceParagraph, false);
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
				paragraphContainer = document.insertParagraph(orgparagraph,
						sourceParagraph, true);
				if (!continued)
					cleanBreakProperty(orgparagraph);
				
			}
		} else{
			TextDocument document = (TextDocument) paragraphContainer
					.getOwnerDocument();
			Paragraph tmp = document.insertParagraph(paragraphContainer,
					sourceParagraph, true);
			paragraphContainer.remove();
			paragraphContainer=tmp;
		}
	}
}
