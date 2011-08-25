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
package org.odftoolkit.simple.text;

import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class presents paragraph in ODF document. It provides methods to
 * manipulate text content, and other child component under the paragraph.
 * 
 * @since 0.5
 */
public class Paragraph extends Component {

	private TextPElement mParagraphElement;
	private Document mOwnerDocument;
	private ParagraphContainer mContainer;
	private DefaultStyleHandler mStyleHandler;

	private Paragraph(TextPElement pElement) {
		mParagraphElement = pElement;
		mOwnerDocument = (Document) ((OdfFileDom) pElement.getOwnerDocument()).getDocument();
		mStyleHandler = new DefaultStyleHandler(pElement);
		mContainer = null;
	}

	/**
	 * Get a paragraph instance by an instance of <code>TextPElement</code>.
	 * 
	 * @param pElement
	 *            - the instance of TextPElement
	 * @return an instance of paragraph
	 */
	public static Paragraph getInstanceof(TextPElement pElement) {
		Paragraph para = null;
		para = (Paragraph) Component.getComponentByElement(pElement);
		if (para != null)
			return para;

		para = new Paragraph(pElement);
		Component.registerComponent(para, pElement);
		return para;
	}

	/**
	 * Create an instance of paragraph
	 * <p>
	 * The paragrah will be added at the end of this container.
	 * 
	 * @param container
	 *            - the paragraph container that contains this paragraph.
	 */
	public static Paragraph newParagraph(ParagraphContainer container) {
		Paragraph para = null;
		OdfElement parent = container.getParagraphContainerElement();
		OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
		TextPElement pEle = ownerDom.newOdfElement(TextPElement.class);
		parent.appendChild(pEle);
		para = new Paragraph(pEle);
		para.mContainer = container;

		return para;
	}

	/**
	 * Set the text content of this paragraph.
	 * <p>
	 * All the existing text content of this paragraph would be removed, and
	 * then new text content would be set.
	 * <p>
	 * The white space characters in the content would be collapsed by default.
	 * For example, tab character would be replaced with <text:tab>, break line
	 * character would be replaced with <text:line-break>.
	 * 
	 * @param content
	 *            - the text content
	 * @see #setTextContentNotCollapsed(String content)
	 */
	public void setTextContent(String content) {
		removeTextContent();
		if (content != null && !content.equals(""))
			appendTextElements(content, true);
	}

	/**
	 * Remove the text content of this paragraph.
	 * <p>
	 * The other child elements except text content will not be removed.
	 * 
	 */
	public void removeTextContent() {
		NodeList nodeList = mParagraphElement.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				mParagraphElement.removeChild(node);
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodename = node.getNodeName();
				if (nodename.equals("text:s") || nodename.equals("text:tab") || nodename.equals("text:line-break"))
					mParagraphElement.removeChild(node);
			}
		}
	}

	/**
	 * Return the text content of this paragraph.
	 * <p>
	 * The other child elements except text content will not be returned.
	 * 
	 * @return - the text content of this paragraph
	 */
	public String getTextContent() {
		StringBuffer buffer = new StringBuffer();
		NodeList nodeList = mParagraphElement.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				buffer.append(node.getNodeValue());
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getNodeName().equals("text:s")) {
					int count = ((TextSElement) node).getTextCAttribute();
					for (int j = 0; j < count; j++)
						buffer.append(' ');
				} else if (node.getNodeName().equals("text:tab"))
					buffer.append('\t');
				else if (node.getNodeName().equals("text:line-break")) {
					String lineseperator = System.getProperty("line.separator");
					buffer.append(lineseperator);
				}
			}
		}
		return buffer.toString();
	}

	/**
	 * Set the text content of this paragraph.
	 * <p>
	 * All the existing text content of this paragraph would be removed, and
	 * then new text content would be set.
	 * <p>
	 * The white space characters in the content would not be collapsed.
	 * 
	 * @param content
	 *            - the text content
	 * @see #setTextContent(String content)
	 */
	public void setTextContentNotCollapsed(String content) {
		removeTextContent();
		if (content != null && !content.equals(""))
			appendTextElements(content, false);
	}

	private void appendTextElements(String content, boolean isWhitespaceCollapsed) {
		if (isWhitespaceCollapsed) {
			int i = 0, length = content.length();
			String str = "";
			while (i < length) {
				char ch = content.charAt(i);
				if (ch == ' ') {
					int j = 1;
					i++;
					while ((i < length) && (content.charAt(i) == ' ')) {
						j++;
						i++;
					}
					if (j == 1) {
						str += ' ';
					} else {
						str += ' ';
						Text textnode = mParagraphElement.getOwnerDocument().createTextNode(str);
						mParagraphElement.appendChild(textnode);
						str = "";
						TextSElement spaceElement = mParagraphElement.newTextSElement();
						spaceElement.setTextCAttribute(j - 1);
					}
				} else if (ch == '\n') {
					if (str.length() > 0) {
						Text textnode = mParagraphElement.getOwnerDocument().createTextNode(str);
						mParagraphElement.appendChild(textnode);
						str = "";
					}
					mParagraphElement.newTextLineBreakElement();
					i++;
				} else if (ch == '\t') {
					if (str.length() > 0) {
						Text textnode = mParagraphElement.getOwnerDocument().createTextNode(str);
						mParagraphElement.appendChild(textnode);
						str = "";
					}
					mParagraphElement.newTextTabElement();
					i++;
				} else if (ch == '\r') {
					i++;
				} else {
					str += ch;
					i++;
				}
			}
			if (str.length() > 0) {
				Text textnode = mParagraphElement.getOwnerDocument().createTextNode(str);
				mParagraphElement.appendChild(textnode);
			}
		} else {
			Text textnode = mParagraphElement.getOwnerDocument().createTextNode(content);
			mParagraphElement.appendChild(textnode);
		}
	}

	/**
	 * Append the text content at the end of this paragraph.
	 * <p>
	 * The white space characters in the content would be collapsed by default.
	 * For example, tab character would be replaced with <text:tab>, break line
	 * character would be replaced with <text:line-break>.
	 * 
	 * @param content
	 *            - the text content
	 * @see #appendTextContentNotCollapsed(String content)
	 */
	public void appendTextContent(String content) {
		if (content != null && !content.equals(""))
			appendTextElements(content, true);
	}

	/**
	 * Append the text content at the end of this paragraph.
	 * <p>
	 * The white space characters in the content would not be collapsed.
	 * 
	 * @param content
	 *            - the text content
	 * @see #appendTextContent(String content)
	 */
	public void appendTextContentNotCollapsed(String content) {
		if (content != null && !content.equals(""))
			appendTextElements(content, false);
	}

	/**
	 * Set the style name of this paragraph
	 * 
	 * @param styleName
	 *            - the style name
	 */
	public void setStyleName(String styleName) {
		mStyleHandler.getStyleElementForWrite().setStyleNameAttribute(styleName);
	}

	/**
	 * Get the style name of this paragraph
	 * 
	 * @return - the style name
	 */
	public String getStyleName() {
		OdfStyleBase style = getStyleHandler().getStyleElementForRead();
		if (style == null) {
			return "";
		}
		if (style instanceof OdfStyle)
			return ((OdfStyle) style).getStyleNameAttribute();
		else
			return "";
	}

	/**
	 * Get the owner document of this paragraph
	 * 
	 * @return the document who owns this paragraph
	 */
	public Document getOwnerDocument() {
		return mOwnerDocument;
	}

	/**
	 * Get the style handler of this paragraph.
	 * <p>
	 * The style handler is an instance of DefaultStyleHandler
	 * 
	 * @return an instance of DefaultStyleHandler
	 * @see org.odftoolkit.simple.style.DefaultStyleHandler
	 */
	public DefaultStyleHandler getStyleHandler() {
		if (mStyleHandler != null)
			return mStyleHandler;
		else {
			mStyleHandler = new DefaultStyleHandler(mParagraphElement);
			return mStyleHandler;
		}
	}

	/**
	 * Return the instance of "text:p" element
	 * 
	 * @return the instance of "text:p" element
	 */
	@Override
	public TextPElement getOdfElement() {
		return mParagraphElement;
	}

}
