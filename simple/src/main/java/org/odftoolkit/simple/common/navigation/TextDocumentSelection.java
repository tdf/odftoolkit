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
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
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
 * This is a decorator class of TextSelection, which help user replace a text
 * content with a TextDocument, all Styles be included.
 */
public class TextDocumentSelection extends Selection {
	
	private TextSelection textSelection;
	private TextDocument sourceDocument;
	
	/**
	 * Replace the Searched Content with a TextDocument with Styles.
	 * <p>
	 * Note: You need cache the TextNavigation.nextSelection item because after
	 * you replace currtenTextSelection with TextDocument,
	 * TextNavigation.nextSelection will search from the inserted Content, it
	 * will make you into a loop if the Search keyword also can be found in the
	 * new inserted Content.
	 * </p>
	 * The right way to use this replaceWithTextDocument(TextDocument
	 * textDocument) method should like this: <Code>
	 * <p>	search = new TextNavigation("SIMPLE", doc);    </p>
	 * <p>	TextSelection currtenTextSelection,nextTextSelection=null;</p>
	 * <p>		while (search.hasNext()) {</p>
	 * <p>			if(nextTextSelection!=null){</p>
	 * <p>				currtenTextSelection=nextTextSelection;</p>
	 * <p>			}else {</p>
	 * <p>			 	currtenTextSelection = (TextSelection) search.nextSelection();</p>
	 * <p>			}</p>
	 * <p>			nextTextSelection = (TextSelection) search.nextSelection();</p>
	 * <p>			if(currtenTextSelection!=null){</p>
	 * <p>				TextDocumentSelection nextParagraphSelection = new TextDocumentSelection(currtenTextSelection);</p>
	 * <p>				try {</p>
	 * <p>					nextParagraphSelection.replaceWithTextDocument(sourcedoc);</p>
	 * <p>				} catch (Exception e) {</p>
	 * <p>					e.printStackTrace();</p>
	 * <p>				}</p>
	 * <p>			}</p>
	 * <p>		}</p>
	 * <p>		if(nextTextSelection!=null){</p>
	 * <p>			TextDocumentSelection nextParagraphSelection = new TextDocumentSelection(nextTextSelection);</p>
	 * <p>			try {</p>
	 * <p>				nextParagraphSelection.replaceWithTextDocument(sourcedoc);</p>
	 * <p>			} catch (Exception e) {</p>
	 * <p>				e.printStackTrace();</p>
	 * <p>			}</p>
	 * <p>		}</p>
	 * </Code>
	 * 
	 * @param textDocument
	 *            the reference TextDocument to replace.
	 * @throws Exception
	 */
	public void replaceWithTextDocument(TextDocument textDocument) throws Exception {
		this.sourceDocument = textDocument;
		int leftLength = textSelection.getText().length();
		int index = textSelection.getIndex();
		prepareParagraphContainer(leftLength, index);
	}
	
	/**
	 * Construct a TextDocumentSelection with TextSelection. Then user can
	 * replace text content with a TextDocument.
	 * 
	 * @param selection
	 *            the TextSelection to be decorated.
	 */
	public TextDocumentSelection(TextSelection selection) {
		textSelection = selection;
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
						nodeLength = Integer.parseInt(((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "c"));
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
	
	private void prepareParagraphContainer(int leftLength, int index) throws Exception {
		OdfElement rightparentElement = textSelection.getContainerElement();
		int nodeLength = TextExtractor.getText(rightparentElement).length();
		Paragraph orgparagraph = Paragraph.getInstanceof((TextParagraphElementBase) rightparentElement);
		
		TextDocument document = (TextDocument) orgparagraph.getOwnerDocument();
		OfficeTextElement sroot = sourceDocument.getContentRoot();
		NodeList clist = sroot.getChildNodes();
		
		try {
			OfficeTextElement documentRoot = document.getContentRoot();
			Node rootNode = rightparentElement.getParentNode();
			if (!rootNode.equals(documentRoot)) {
				throw new RuntimeException(
						"The ParentNode of text content's ContainerElement which will be replaced is not Document ContentRoot, TextDocument only can be insert to the Docuemnt ContentRoot");
			}
		} catch (Exception e) {
			Logger.getLogger(TextDocumentSelection.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		
		if (index == 0) {
			if (leftLength == nodeLength) {
				// Replace whole Paragraph
				OdfElement refElement = orgparagraph.getOdfElement();
				for (int i = 0; i < clist.getLength(); i++) {
					OdfElement node = (OdfElement) clist.item(i);
					refElement = document.insertOdfElement(refElement,
							sourceDocument, node, false);
				}
				rightparentElement.getParentNode().removeChild(
						rightparentElement);
			} else {
				// at the start of original Paragraph, insert before original
				// Paragraph
				delete(index, leftLength, rightparentElement);
				for (int i = 0; i < clist.getLength(); i++) {
					OdfElement node = (OdfElement) clist.item(i);
					document.insertOdfElement(orgparagraph.getOdfElement(), sourceDocument, node, true);
				}
			}
		} else if (nodeLength == (index + leftLength)) {
			// at the end of original Paragraph, insert after original Paragraph
			delete(index, leftLength, rightparentElement);
			for (int i = 0; i < clist.getLength(); i++) {
				OdfElement node = (OdfElement) clist.item(i);
				document.insertOdfElement(orgparagraph.getOdfElement(), sourceDocument, node, false);
			}
		} else {
			// at the middle of original Paragraph, split original Paragraph,
			// insert before the second Paragraph.
			delete(index, leftLength, rightparentElement);
			Node leftparentElement = rightparentElement.cloneNode(true);
			rightparentElement.getParentNode().insertBefore(leftparentElement, rightparentElement);
			nodeLength = TextExtractor.getText((OdfElement) leftparentElement).length();
			delete(index, nodeLength - index, leftparentElement);
			delete(0, index, rightparentElement);
			for (int i = 0; i < clist.getLength(); i++) {
				OdfElement node = (OdfElement) clist.item(i);
				document.insertOdfElement(orgparagraph.getOdfElement(), sourceDocument, node, true);
			}
			int offset = 0 - leftLength - index;
			SelectionManager.refresh(rightparentElement, offset, offset);
		}
	}
}
