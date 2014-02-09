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

package org.odftoolkit.simple.text;

import java.net.URI;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.common.navigation.InvalidNavigationException;
import org.odftoolkit.simple.common.navigation.TextSelection;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class represents the application of a style to the character data of a
 * portion of text.
 * <p>
 * It provides convenient methods to create a span and manipulate attributes of
 * a span.
 * 
 * @since 0.5.5
 */
public class Span extends Component implements TextHyperlinkContainer {

	private TextSpanElement mSpanElement;
	private Document mOwnerDocument;
	private DefaultStyleHandler mStyleHandler;
	private TextHyperlinkContainerImpl mHyperlinkContainerImpl;

	private Span(TextSpanElement element) {
		mSpanElement = element;
		mOwnerDocument = (Document) ((OdfFileDom) element.getOwnerDocument()).getDocument();
		mStyleHandler = new DefaultStyleHandler(element);
	}

	/**
	 * Get a span instance by an instance of <code>TextSpanElement</code>.
	 * 
	 * @param sElement
	 *            - the instance of TextSpanElement
	 * @return an instance of span
	 */
	public static Span getInstanceof(TextSpanElement sElement) {
		if (sElement == null)
			return null;

		Span span = null;
		span = (Span) Component.getComponentByElement(sElement);
		if (span != null)
			return span;

		span = new Span(sElement);
		Component.registerComponent(span, sElement);
		return span;
	}

	/**
	 * Create a span instance with a text selection
	 * 
	 * @param textSelection
	 *            the TextSelection which the span is applied to.
	 * @return an instance of span
	 * @see org.odftoolkit.simple.common.navigation.TextSelection
	 */
	public static Span newSpan(TextSelection textSelection) {
		try {
			TextSpanElement element = textSelection.createSpanElement();
			return Span.getInstanceof(element);
		} catch (InvalidNavigationException e) {
			Logger.getLogger(Span.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Get the style handler of this span.
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
			mStyleHandler = new DefaultStyleHandler(mSpanElement);
			return mStyleHandler;
		}
	}

	/**
	 * Get the owner document of this span
	 * 
	 * @return the document who owns this span
	 */
	public Document getOwnerDocument() {
		return mOwnerDocument;
	}

	/**
	 * Return the instance of "text:span" element
	 * 
	 * @return the instance of "text:span" element
	 */
	@Override
	public TextSpanElement getOdfElement() {
		return mSpanElement;
	}

	/**
	 * Remove the text content of this span.
	 * 
	 */
	public void removeTextContent() {
		Paragraph.removeTextContentImpl(getOdfElement());
		// remove empty hyperlink
		NodeList nodeList = getOdfElement().getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodename = node.getNodeName();
				if (nodename.equals("text:a") && node.hasChildNodes() == false)
					getOdfElement().removeChild(node);
			}
		}
	}

	/**
	 * Set the text content of this span.
	 * <p>
	 * All the existing text content of this paragraph would be removed, and
	 * then new text content would be set.
	 * 
	 * @param content
	 *            - the text content
	 */
	public void setTextContent(String content) {
		Paragraph.removeTextContentImpl(getOdfElement());
		Node lastNode = getOdfElement().getLastChild();
		if (content != null && !content.equals("")){
			if (lastNode != null && lastNode.getNodeName() != null && lastNode.getNodeName().equals("text:a")) {
				Paragraph.appendTextElements((TextAElement) lastNode, content, true);
			} else {
				Paragraph.appendTextElements(getOdfElement(), content, true);
			}
		}
		// remove empty hyperlink
		Paragraph.removeEmptyHyperlink(getOdfElement());
	}

	/**
	 * Return the text content of this span.
	 * <p>
	 * The other child elements except text content will not be returned.
	 * 
	 * @return - the text content of this span
	 */
	public String getTextContent() {
		return Paragraph.getTextContent(getOdfElement());
	}

	/**
	 * Append the text content at the end of this span.
	 * <p>
	 * The appended text would follow the style of the last character.
	 * 
	 * @param content
	 *            - the text content
	 */
	public void appendTextContent(String content) {
		appendTextContent(content, true);
	}

	/**
	 * Append the text content at the end of this span.
	 * <p>
	 * The appended text would follow the style of the last character if the
	 * second parameter is set to true; Or else, the appended text would follow
	 * the default style of this paragraph.
	 * 
	 * @param content
	 *            - the text content
	 * @param isStyleInherited
	 *            - whether the hyperlink style would be inherited by the
	 *            appended text
	 */
	public void appendTextContent(String content, boolean isStyleInherited) {
		boolean canInherited = false;
		Node lastNode = getOdfElement().getLastChild();
		if (lastNode != null && lastNode.getNodeName() != null && lastNode.getNodeName().equals("text:a"))
			canInherited = true;

		if (isStyleInherited && canInherited) {
			if (content != null && !content.equals(""))
				Paragraph.appendTextElements((OdfElement) lastNode, content, true);
		} else {
			if (content != null && !content.equals(""))
				Paragraph.appendTextElements(getOdfElement(), content, true);
		}
	}

	/************ Hyperlink support ************/
	public TextHyperlink applyHyperlink(URI linkto) {
		return getTextHyperlinkContainerImpl().applyHyperlink(linkto);
	}

	public Iterator<TextHyperlink> getHyperlinkIterator() {
		return getTextHyperlinkContainerImpl().getHyperlinkIterator();
	}

	public void removeHyperlinks() {
		getTextHyperlinkContainerImpl().removeHyperlinks();
	}
	
	public TextHyperlink appendHyperlink(String text, URI linkto) {
		return getTextHyperlinkContainerImpl().appendHyperlink(text, linkto);
	}

	private class TextHyperlinkContainerImpl extends AbstractTextHyperlinkContainer {
		public TextHyperlinkContainerImpl(OdfElement parent) {
			super(parent);
		}
	}

	private TextHyperlinkContainerImpl getTextHyperlinkContainerImpl() {
		if (mHyperlinkContainerImpl == null)
			mHyperlinkContainerImpl = new TextHyperlinkContainerImpl(getOdfElement());
		return mHyperlinkContainerImpl;
	}
	/************ End of Hyperlink support ************/

}
