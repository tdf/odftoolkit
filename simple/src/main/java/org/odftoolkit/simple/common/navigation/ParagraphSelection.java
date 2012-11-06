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

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.text.Paragraph;
import org.w3c.dom.Element;
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
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareParagraphContainer(leftLength, index);
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

	private void prepareParagraphContainer(int leftLength, int index) {
		if (paragraphContainer == null) {
			OdfElement rightparentElement = textSelection.getContainerElement();
			int nodeLength = TextExtractor.getText(rightparentElement).length();
			if(index==0){
				if(leftLength==nodeLength){
					//Replace whole Paragraph
					Paragraph orgparagraph = Paragraph.getInstanceof((TextParagraphElementBase)rightparentElement);
					TextDocument document = (TextDocument)orgparagraph.getOwnerDocument();
					paragraphContainer = document.insertParagraph(orgparagraph, sourceParagraph, false);
					NodeList cnl = rightparentElement.getChildNodes();
					for(int i=0;i<cnl.getLength();i++){
						rightparentElement.removeChild(cnl.item(i));
					}
				}else{
					//at the start of original Paragraph, insert before original Paragraph
					delete(index, leftLength, rightparentElement);
					Paragraph orgparagraph = Paragraph.getInstanceof((TextParagraphElementBase)rightparentElement);
					TextDocument document = (TextDocument)orgparagraph.getOwnerDocument();
					paragraphContainer = document.insertParagraph(orgparagraph, sourceParagraph, true);					
				}
			}
			else if(nodeLength==(index+leftLength)){
				//at the end of original Paragraph, insert after original Paragraph
				delete(index, leftLength, rightparentElement);
				Paragraph orgparagraph = Paragraph.getInstanceof((TextParagraphElementBase)rightparentElement);
				TextDocument document = (TextDocument)orgparagraph.getOwnerDocument();
				paragraphContainer = document.insertParagraph(orgparagraph, sourceParagraph, false);
			}else{
				//at the middle of original Paragraph, split original Paragraph, insert before the second Paragraph.
				delete(index, leftLength, rightparentElement);
				Node leftparentElement = rightparentElement.cloneNode(true);
				rightparentElement.getParentNode().insertBefore(leftparentElement,rightparentElement);
				nodeLength = TextExtractor.getText((OdfElement) leftparentElement).length();
				delete(index, nodeLength-index, leftparentElement);
				delete(0, index, rightparentElement);
				Paragraph orgparagraph = Paragraph.getInstanceof((TextParagraphElementBase)rightparentElement);
				TextDocument document = (TextDocument)orgparagraph.getOwnerDocument();
				paragraphContainer = document.insertParagraph(orgparagraph, sourceParagraph, true);
				
			}
		} else{
			TextDocument document = (TextDocument)paragraphContainer.getOwnerDocument();
			Paragraph tmp = document.insertParagraph(paragraphContainer, sourceParagraph, true);
			paragraphContainer.remove();
			paragraphContainer=tmp;
		}
	}
}
