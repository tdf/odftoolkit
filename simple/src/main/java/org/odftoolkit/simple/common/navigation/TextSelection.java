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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.dc.DcCreatorElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDateElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.common.TextExtractor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <code>TextSelection</code> describes a sub element in a paragraph element or
 * a heading element. It is recognized by the container element, which type
 * should be {@link org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph
 * OdfTextParagraph} or
 * {@link org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading
 * OdfTextHeading}, the start index of text content in container element and the
 * text content of this <code>Selection</code>.
 */
public class TextSelection extends Selection {

	String mMatchedText;
	private OdfTextParagraph mParagraph;
	private OdfTextHeading mHeading;
	private int mIndexInContainer;
	private boolean mIsInserted;

	/**
	 * Constructor of <code>TextSelection</code>.
	 * 
	 * @param text
	 *            the text content of this <code>TextSelection</code>
	 * @param containerElement
	 *            the paragraph element or heading element that contains this
	 *            <code>TextSelection</code>
	 * @param index
	 *            the start index of the text content in container element
	 * 
	 */
	TextSelection(String text, OdfElement containerElement, int index) {
		mMatchedText = text;
		if (containerElement instanceof OdfTextParagraph) {
			mParagraph = (OdfTextParagraph) containerElement;
		} else if (containerElement instanceof OdfTextHeading) {
			mHeading = (OdfTextHeading) containerElement;
		}
		mIndexInContainer = index;
	}

	/**
	 * Create a new <code>TextSelection</code>.
	 * 
	 * @param text
	 *            the text content of this <code>TextSelection</code>
	 * @param containerElement
	 *            the paragraph element or heading element that contains this
	 *            <code>TextSelection</code>
	 * @param index
	 *            the start index of the text content in container element
	 * 
	 * @since 0.5.5
	 */
	public static TextSelection newTextSelection(String text, OdfElement containerElement, int index) {
		TextSelection selection = new TextSelection(text, containerElement, index);
		Selection.SelectionManager.registerItem(selection);
		return selection;
	}

	/**
	 * Get the paragraph element or heading element that contains this
	 * <code>TextSelection</code>.
	 * 
	 * @return OdfElement the container element
	 */
	@Override
	public OdfElement getElement() {
		return getContainerElement();
	}

	/**
	 * Get the paragraph element or heading element that contains this text.
	 * 
	 * @return OdfElement
	 */
	public OdfElement getContainerElement() {
		if (mParagraph != null) {
			return mParagraph;
		} else {
			return mHeading;
		}
	}

	/**
	 * Get the start index of the text content of its container element.
	 * 
	 * @return index the start index of the text content of its container
	 *         element
	 */
	@Override
	public int getIndex() {
		return mIndexInContainer;
	}

	/**
	 * Get the text content of this <code>TextSelection</code>.
	 * 
	 * @return text the text content
	 */
	public String getText() {
		return mMatchedText;
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
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		OdfElement container = getContainerElement();
		delete(mIndexInContainer, mMatchedText.length(), container);
		SelectionManager.refreshAfterCut(this);
		mMatchedText = "";
	}

	/**
	 * Apply a style to the selection so that the text style of this selection
	 * will append the specified style.
	 * 
	 * @param style
	 *            the style can be from the current document or user defined
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	public void applyStyle(OdfStyleBase style) throws InvalidNavigationException {
		// append the specified style to the selection
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		OdfElement parentElement = getContainerElement();

		int leftLength = getText().length();
		int index = mIndexInContainer;

		appendStyle(index, leftLength, parentElement, style);

	}

	/**
	 * Replace the text content of selection with a new string.
	 * 
	 * @param newText
	 *            the replace text String
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	public void replaceWith(String newText) throws InvalidNavigationException {
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		OdfElement parentElement = getContainerElement();
		int leftLength = getText().length();
		int index = mIndexInContainer;
		delete(index, leftLength, parentElement);
		OdfTextSpan textSpan = new OdfTextSpan((OdfFileDom) parentElement.getOwnerDocument());
		textSpan.addContentWhitespace(newText);
		mIsInserted = false;
		insertOdfElement(textSpan, index, parentElement);
		// optimize the parent element
		optimize(parentElement);
		int offset = newText.length() - leftLength;
		SelectionManager.refresh(getContainerElement(), offset, index + getText().length());
		mMatchedText = newText;
	}

	/**
	 * Create a span element for this text selection.
	 * 
	 * @return the created text span element for this selection
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 * @since 0.5.5
	 */
	public TextSpanElement createSpanElement() throws InvalidNavigationException {
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		OdfElement parentElement = getContainerElement();
		int leftLength = getText().length();
		int index = mIndexInContainer;
		delete(index, leftLength, parentElement);
		OdfTextSpan textSpan = new OdfTextSpan((OdfFileDom) parentElement.getOwnerDocument());
		textSpan.addContentWhitespace(getText());
		mIsInserted = false;
		insertOdfElement(textSpan, index, parentElement);
		// optimize the parent element
		optimize(parentElement);

		return textSpan;
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
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		int indexOfNew = 0;
		OdfElement newElement = positionItem.getElement();
		if (positionItem instanceof TextSelection) {
			indexOfNew = ((TextSelection) positionItem).getIndex();
			newElement = ((TextSelection) positionItem).getContainerElement();
		}

		OdfTextSpan textSpan = getSpan((OdfFileDom) positionItem.getElement().getOwnerDocument());
		mIsInserted = false;
		insertOdfElement(textSpan, indexOfNew, newElement);
		adjustStyle(newElement, textSpan, null);
		SelectionManager.refreshAfterPasteAtFrontOf(this, positionItem);
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
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		// TODO: think about and test if search item is a element selection
		int indexOfNew = 0;
		OdfElement newElement = positionItem.getElement();
		if (positionItem instanceof TextSelection) {
			indexOfNew = ((TextSelection) positionItem).getIndex() + ((TextSelection) positionItem).getText().length();
			newElement = ((TextSelection) positionItem).getContainerElement();
		}
		OdfTextSpan textSpan = getSpan((OdfFileDom) positionItem.getElement().getOwnerDocument());
		mIsInserted = false;
		insertOdfElement(textSpan, indexOfNew, newElement);
		adjustStyle(newElement, textSpan, null);
		SelectionManager.refreshAfterPasteAtEndOf(this, positionItem);
	}

	/**
	 * Add a hypertext reference to the selection.
	 * 
	 * @param url
	 *            the URL of this hypertext reference
	 * @throws InvalidNavigationException
	 *             if the selection is unavailable.
	 */
	public void addHref(URL url) throws InvalidNavigationException {
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		OdfElement parentElement = getContainerElement();
		int leftLength = getText().length();
		int index = mIndexInContainer;
		addHref(index, leftLength, parentElement, url.toString());
	}
	
	/**
	 * Add a comment to the selection.
	 * 
	 * @param content
	 *            the content of this comment.
	 * @param creator
	 *            the creator of this comment, if <code>creator</code> is null,
	 *            the value of <code>System.getProperty("user.name")</code> will
	 *            be used.
	 * @throws InvalidNavigationException
	 *            if the selection is unavailable.
	 * @since 0.6.5
	 */
	public void addComment(String content, String creator) throws InvalidNavigationException {
		if (validate() == false) {
			throw new InvalidNavigationException("No matched string at this position");
		}
		// create annotation element
		OdfElement parentElement = getContainerElement();
		OdfFileDom dom = (OdfFileDom) parentElement.getOwnerDocument();
		OfficeAnnotationElement annotationElement = dom.newOdfElement(OfficeAnnotationElement.class);
		// set creator
		DcCreatorElement dcCreatorElement = annotationElement.newDcCreatorElement();
		if (creator == null) {
			creator = System.getProperty("user.name");
		}
		dcCreatorElement.setTextContent(creator);
		// set date
		String dcDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		DcDateElement dcDateElement = annotationElement.newDcDateElement();
		dcDateElement.setTextContent(dcDate);
		TextPElement notePElement = annotationElement.newTextPElement();
		TextSpanElement noteSpanElement = notePElement.newTextSpanElement();
		// set comment style
		OdfOfficeAutomaticStyles styles = dom.getAutomaticStyles();
		OdfStyle textStyle = styles.newStyle(OdfStyleFamily.Text);
		StyleTextPropertiesElement styleTextPropertiesElement = textStyle.newStyleTextPropertiesElement(null);
		styleTextPropertiesElement.setStyleFontNameAttribute("Tahoma");
		styleTextPropertiesElement.setFoFontSizeAttribute("10pt");
		styleTextPropertiesElement.setStyleFontNameAsianAttribute("Lucida Sans Unicode");
		styleTextPropertiesElement.setStyleFontSizeAsianAttribute("12pt");
		noteSpanElement.setStyleName(textStyle.getStyleNameAttribute());
		// set comment content
		noteSpanElement.setTextContent(content);
		// insert comment to its position
		insertOdfElement(annotationElement, mIndexInContainer, parentElement);
		// three text length plus two '\r'
		int offset = content.length() + 1 + dcDate.length() + 1 + creator.length();
		SelectionManager.refresh(getContainerElement(), offset, getIndex());
	}
	
	/**
	 * return a String Object representing this selection value the text content
	 * of the selection, start index in the container element and the text
	 * content of the container element will be provided.
	 * 
	 * @return a String representation of the value of this
	 *         <code>TextSelection</code>
	 */
	@Override
	public String toString() {
		return "[" + mMatchedText + "] started from " + mIndexInContainer + " in paragraph:"
				+ TextExtractor.getText(getContainerElement());
	}

	@Override
	protected void refreshAfterFrontalDelete(Selection deleteItem) {
		if (deleteItem instanceof TextSelection) {
			mIndexInContainer -= ((TextSelection) deleteItem).getText().length();
		}
	}

	@Override
	protected void refreshAfterFrontalInsert(Selection pasteItem) {
		if (pasteItem instanceof TextSelection) {
			mIndexInContainer += ((TextSelection) pasteItem).getText().length();
		}
	}

	@Override
	protected void refresh(int offset) {
		mIndexInContainer += offset;
		if (mIndexInContainer < 0) {
			mIndexInContainer = 0;
		}
	}

	/*
	 * Return a new span that cover this selection and keep the original style
	 * of this <code>Selection</code>.
	 */
	private OdfTextSpan getSpan(OdfFileDom ownerDoc) {

		OdfElement parentElement = getContainerElement();
		if (parentElement != null) {
			OdfElement copyParentNode = (OdfElement) parentElement.cloneNode(true);
			if (ownerDoc != parentElement.getOwnerDocument()) {
				copyParentNode = (OdfElement) ownerDoc.adoptNode(copyParentNode);
			}
			OdfTextSpan textSpan = new OdfTextSpan(ownerDoc);
			int sIndex = mIndexInContainer;
			int eIndex = sIndex + mMatchedText.length();
			// delete the content except the selection string
			// delete from the end to start, so that the postion will not be
			// impact by delete action
			delete(eIndex, TextExtractor.getText(copyParentNode).length() - eIndex, copyParentNode);
			delete(0, sIndex, copyParentNode);
			optimize(copyParentNode);
			Node childNode = copyParentNode.getFirstChild();
			while (childNode != null) {
				textSpan.appendChild(childNode.cloneNode(true));
				childNode = childNode.getNextSibling();
			}
			// apply text style for the textSpan
			if (copyParentNode instanceof OdfStylableElement) {
				applyTextStyleProperties(getTextStylePropertiesDeep((OdfStylableElement) copyParentNode), textSpan);
			}
			return textSpan;
		}
		return null;
	}

	/*
	 * Optimize the text element by deleting the empty text node.
	 */
	private void optimize(Node pNode) {
		// check if the text:a can be optimized
		Node node = pNode.getFirstChild();
		while (node != null) {
			Node nextNode = node.getNextSibling();
			// if ((node.getNodeType() == Node.ELEMENT_NODE) &&
			// (node.getPrefix().equals("text"))) {
			if (node instanceof OdfTextSpan) {
				if (TextExtractor.getText((OdfTextSpan) node).length() == 0) {
					node.getParentNode().removeChild(node);
				} else {
					optimize(node);
				}
			}
			node = nextNode;
		}
	}

	/*
	 * Apply the <code>styleMap</code> to the <code>toElement</code> reserve the
	 * style property of toElement, if it is also exist in <code>styleMap</code>
	 */
	private void applyTextStyleProperties(Map<OdfStyleProperty, String> styleMap, OdfStylableElement toElement) {
		if (styleMap != null) {
			// preserve the style property of toElement if it is also exist in
			// styleMap
			OdfStyle resultStyleElement = toElement.getAutomaticStyles().newStyle(OdfStyleFamily.Text);
			for (Map.Entry<OdfStyleProperty, String> entry : styleMap.entrySet()) {
				if (toElement.hasProperty(entry.getKey())) {
					resultStyleElement.setProperty(entry.getKey(), toElement.getProperty(entry.getKey()));
				} else {
					resultStyleElement.setProperty(entry.getKey(), entry.getValue());
				}
			}
			toElement.setStyleName(resultStyleElement.getStyleNameAttribute());
		}
	}

	/*
	 * Insert <code>odfElement</code>, span or annotation, into the from index of <code>pNode<code>.
	 */
	private void insertOdfElement(OdfElement odfElement, int fromIndex, Node pNode) {
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		if (fromIndex == 0 && mIsInserted) {
			return;
		}
		int nodeLength = 0;
		Node node = pNode.getFirstChild();
		while (node != null) {
			if (fromIndex <= 0 && mIsInserted) {
				return;
			}
			if (node.getNodeType() == Node.TEXT_NODE) {
				nodeLength = node.getNodeValue().length();
				if ((fromIndex != 0) && (nodeLength < fromIndex)) {
					fromIndex -= nodeLength;
				} else {
					// insert result after node, and insert an new text node
					// after the result node
					String value = node.getNodeValue();
					StringBuffer buffer = new StringBuffer();
					buffer.append(value.substring(0, fromIndex));
					// insert the text span in appropriate position
					node.setNodeValue(buffer.toString());
					Node nextNode = node.getNextSibling();
					Node parNode = node.getParentNode();
					Node newNode = node.cloneNode(true);
					newNode.setNodeValue(value.substring(fromIndex, value.length()));
					if (nextNode != null) {
						parNode.insertBefore(odfElement, nextNode);
						parNode.insertBefore(newNode, nextNode);
					} else {
						parNode.appendChild(odfElement);
						parNode.appendChild(newNode);
					}
					mIsInserted = true;
					return;
				}
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				// text:s
				if (node.getLocalName().equals("s")) {
					try {
						nodeLength = Integer.parseInt(((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT
								.getUri(), "c"));
					} catch (Exception e) {
						nodeLength = 1;
					}
					fromIndex -= nodeLength;
				} else if (node.getLocalName().equals("line-break")) {
					nodeLength = 1;
					fromIndex--;
				} else if (node.getLocalName().equals("tab")) {
					nodeLength = 1;
					fromIndex--;
				} else {
					nodeLength = TextExtractor.getText((OdfElement) node).length();
					insertOdfElement(odfElement, fromIndex, node);
					fromIndex -= nodeLength;
				}
			}
			node = node.getNextSibling();
		}
	}

	/*
	 * The <code>textSpan</code> must be the child element of
	 * <code>parentNode</code> this method is used to keep the style of text
	 * span when it has been insert into the <code>parentNode</code> if we don't
	 * deal with the style, the inserted span will also have the style of
	 * <code>parentNode</code>.
	 */
	private void adjustStyle(Node parentNode, OdfTextSpan textSpan, Map<OdfStyleProperty, String> styleMap) {
		if (parentNode instanceof OdfStylableElement) {
			OdfStylableElement pStyleNode = (OdfStylableElement) parentNode;
			if (styleMap == null) {
				styleMap = getTextStylePropertiesDeep(pStyleNode);
			}
			Node node = parentNode.getFirstChild();
			while (node != null) {
				if (node.getNodeType() == Node.TEXT_NODE) {
					if (node.getTextContent().length() > 0) {
						Node nextNode = node.getNextSibling();
						OdfTextSpan span = new OdfTextSpan((OdfFileDom) node.getOwnerDocument());
						span.appendChild(node);
						if (nextNode != null) {
							parentNode.insertBefore(span, nextNode);
						} else {
							parentNode.appendChild(span);
						}
						node = span;
						applyTextStyleProperties(styleMap, (OdfStylableElement) node);
					}
				} else if ((node instanceof OdfStylableElement)) {
					if (!node.equals(textSpan)) {
						Map<OdfStyleProperty, String> styles = getTextStylePropertiesDeep(pStyleNode);
						Map<OdfStyleProperty, String> styles1 = getTextStylePropertiesDeep((OdfStylableElement) node);
						if (styles == null) {
							styles = styles1;
						} else if (styles1 != null) {
							styles.putAll(styles1);
						}
						int comp = node.compareDocumentPosition(textSpan);
						// if node contains textSpan, then recurse the node
						if ((comp & Node.DOCUMENT_POSITION_CONTAINED_BY) > 0) {
							adjustStyle(node, textSpan, styles);
						} else {
							applyTextStyleProperties(styles, (OdfStylableElement) node);
						}
					}
				}
				node = node.getNextSibling();
			}
			// change the parentNode to default style
			// here we don't know the default style name, so here just
			// remove the text:style-name attribute
			pStyleNode.removeAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name");
		}
	}

	/*
	 * Delete the <code>pNode<code> from the <code>fromIndex</code> text, and
	 * delete <code>leftLength</code> text.
	 */
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

	/*
	 * Add href for a range text of <code>pNode<code> from the
	 * <code>fromIndex</code> text, and the href will cover
	 * <code>leftLength</code> text.
	 */
	private void addHref(int fromIndex, int leftLength, Node pNode, String href) {
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
					node.setNodeValue(value.substring(0, fromIndex));
					int endLength = fromIndex + leftLength;
					int nextLength = value.length() - endLength;

					Node nextNode = node.getNextSibling();
					Node parNode = node.getParentNode();
					// init text:a
					TextAElement textLink = new TextAElement((OdfFileDom) node.getOwnerDocument());
					Node newNode = null;
					if (nextLength >= 0) {
						textLink.setTextContent(value.substring(fromIndex, endLength));
						newNode = node.cloneNode(true);
						newNode.setNodeValue(value.substring(endLength, value.length()));
						leftLength = 0;
					} else {
						textLink.setTextContent(value.substring(fromIndex, value.length()));
						leftLength = endLength - value.length();
					}
					textLink.setXlinkTypeAttribute("simple");
					textLink.setXlinkHrefAttribute(href);

					if (nextNode != null) {
						parNode.insertBefore(textLink, nextNode);
						if (newNode != null) {
							parNode.insertBefore(newNode, nextNode);
						}
					} else {
						parNode.appendChild(textLink);
						if (newNode != null) {
							parNode.appendChild(newNode);
						}
					}
					fromIndex = 0;
					if (nextNode != null) {
						node = nextNode;
					} else {
						node = textLink;
					}

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
						addHref(fromIndex, leftLength, node, href);
						int length = (fromIndex + leftLength) - nodeLength;
						leftLength = length > 0 ? length : 0;
						fromIndex = 0;
					}
				}
			}
			node = node.getNextSibling();
		}
	}

	/*
	 * Get a map containing text properties of the specified styleable
	 * <code>element</code>.
	 * 
	 * @return a map of text properties.
	 */
	private Map<OdfStyleProperty, String> getTextStyleProperties(OdfStylableElement element) {
		String styleName = element.getStyleName();
		OdfStyleBase styleElement = element.getAutomaticStyles().getStyle(styleName, element.getStyleFamily());

		if (styleElement == null) {
			styleElement = element.getDocumentStyle();
		}
		if (styleElement != null) {
			// check if it is the style:defaut-style
			if ((styleElement.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties) == null)
					&& (styleElement.getPropertiesElement(OdfStylePropertiesSet.TextProperties) == null)) {
				styleElement = ((Document) ((OdfFileDom) styleElement.getOwnerDocument()).getDocument())
						.getDocumentStyles().getDefaultStyle(styleElement.getFamily());
			}
			TreeMap<OdfStyleProperty, String> result = new TreeMap<OdfStyleProperty, String>();
			OdfStyleFamily family = OdfStyleFamily.Text;
			if (family != null) {
				for (OdfStyleProperty property : family.getProperties()) {
					if (styleElement.hasProperty(property)) {
						result.put(property, styleElement.getProperty(property));
					}
				}
			}
			return result;
		}
		return null;
	}

	/*
	 * Get a map containing text properties of the specified styleable
	 * <code>element</code>. The map will also include any properties set by
	 * parent styles.
	 * 
	 * @return a map of text properties.
	 */
	private Map<OdfStyleProperty, String> getTextStylePropertiesDeep(OdfStylableElement element) {
		String styleName = element.getStyleName();
		OdfStyleBase styleElement = element.getAutomaticStyles().getStyle(styleName, element.getStyleFamily());
		if (styleElement == null) {
			styleElement = element.getDocumentStyle();
		}
		TreeMap<OdfStyleProperty, String> result = new TreeMap<OdfStyleProperty, String>();
		while (styleElement != null) {
			// check if it is the style:defaut-style
			if ((styleElement.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties) == null)
					&& (styleElement.getPropertiesElement(OdfStylePropertiesSet.TextProperties) == null)) {
				styleElement = ((Document) ((OdfFileDom) styleElement.getOwnerDocument()).getDocument())
						.getDocumentStyles().getDefaultStyle(styleElement.getFamily());
			}
			OdfStyleFamily family = OdfStyleFamily.Text;
			if (family != null) {
				for (OdfStyleProperty property : family.getProperties()) {
					if (styleElement.hasProperty(property)) {
						result.put(property, styleElement.getProperty(property));
					}
				}
			}
			styleElement = styleElement.getParentStyle();
		}
		return result;
	}

	/*
	 * Validate if the <code>Selection</code> is still available.
	 * 
	 * @return true if the selection is available; false if the
	 * <code>Selection</code> is not available.
	 */
	private boolean validate() {
		if (getContainerElement() == null) {
			return false;
		}
		OdfElement container = getContainerElement();
		if (container == null) {
			return false;
		}
		String content = TextExtractor.getText(container);
		if (content.indexOf(mMatchedText, mIndexInContainer) == mIndexInContainer) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Append specified style for a range text of <code>pNode<code> from
	 * <code>fromIndex</code> and cover <code>leftLength</code>
	 */
	private void appendStyle(int fromIndex, int leftLength, Node pNode, OdfStyleBase style) {
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
					node.setNodeValue(value.substring(0, fromIndex));
					int endLength = fromIndex + leftLength;
					int nextLength = value.length() - endLength;

					Node nextNode = node.getNextSibling();
					Node parNode = node.getParentNode();
					// init text:a
					OdfTextSpan textSpan = new OdfTextSpan((OdfFileDom) node.getOwnerDocument());
					Node newNode = null;
					if (nextLength >= 0) {
						textSpan.setTextContent(value.substring(fromIndex, endLength));
						newNode = node.cloneNode(true);
						newNode.setNodeValue(value.substring(endLength, value.length()));
						leftLength = 0;
					} else {
						textSpan.setTextContent(value.substring(fromIndex, value.length()));
						leftLength = endLength - value.length();
					}
					textSpan.setProperties(style.getStyleProperties());

					if (nextNode != null) {
						parNode.insertBefore(textSpan, nextNode);
						if (newNode != null) {
							parNode.insertBefore(newNode, nextNode);
						}
					} else {
						parNode.appendChild(textSpan);
						if (newNode != null) {
							parNode.appendChild(newNode);
						}
					}
					fromIndex = 0;
					if (nextNode != null) {
						node = nextNode;
					} else {
						node = textSpan;
					}

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
						appendStyle(fromIndex, leftLength, node, style);
						int length = (fromIndex + leftLength) - nodeLength;
						leftLength = length > 0 ? length : 0;
						fromIndex = 0;
					}
				}
			}
			node = node.getNextSibling();
		}
	}
}
