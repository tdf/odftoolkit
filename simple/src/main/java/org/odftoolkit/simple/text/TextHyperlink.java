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
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.Iterator;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Component;
import org.w3c.dom.Node;

/**
 * This class represents a text hyperlink in ODF document. It provides
 * convenient methods to get/set/remove the URI of hyperlinks.
 * 
 * @since 0.6.5
 */
public class TextHyperlink extends Component {

	TextAElement hyperLinkElement;

	private TextHyperlink(TextAElement aElement) {
		hyperLinkElement = aElement;
	}

	/**
	 * Gets a TextHyperlink instance by an instance of <code>TextAElement</code>
	 * .
	 * 
	 * @param aElement
	 *            the instance of TextAElement.
	 * @return an instance of Hyperlink.
	 */
	public static TextHyperlink getInstanceof(TextAElement aElement) {
		if (aElement == null)
			return null;

		TextHyperlink link = null;
		link = (TextHyperlink) Component.getComponentByElement(aElement);
		if (link != null)
			return link;

		link = new TextHyperlink(aElement);
		Component.registerComponent(link, aElement);
		return link;
	}

	@Override
	public OdfElement getOdfElement() {
		// TODO Auto-generated method stub
		return hyperLinkElement;
	}

	/**
	 * Return the URI of this hyperlink
	 * 
	 * @return the URI of this hyperlink
	 * @throws URISyntaxException
	 */
	public URI getURI() throws URISyntaxException {
		return new URI(hyperLinkElement.getXlinkHrefAttribute());
	}

	/**
	 * Set the value of URI for this hyperlink
	 * 
	 * @param linkto
	 *            - the URI of this hyperlink
	 */
	public void setURI(URI linkto) {
		hyperLinkElement.setXlinkHrefAttribute(linkto.toString());
	}

	/**
	 * Get the text content of this hyperlink
	 * 
	 * @return the text content of this hyperlink
	 */
	public String getTextContent() {
		return Paragraph.getTextContent(getOdfElement());
	}

	/**
	 * Set the text content of this hyperlink
	 * 
	 * @param text
	 *            - the text content to be set
	 */
	public void setTextContent(String text) {
		Paragraph.removeTextContentImpl(getOdfElement());
		if (text != null && !text.equals(""))
			Paragraph.appendTextElements(getOdfElement(), text, true);
	}

	/**
	 * Remove the text content of this hyperlink.
	 * <p>
	 * The other child elements except text content will not be removed.
	 * 
	 */
	public void removeTextContent() {
		Paragraph.removeTextContentImpl(getOdfElement());
	}

}

class AbstractTextHyperlinkContainer implements TextHyperlinkContainer {
	OdfElement linkContainer;

	public AbstractTextHyperlinkContainer(OdfElement parent) {
		if ((parent instanceof TextPElement) || (parent instanceof TextHElement) || (parent instanceof TextSpanElement))
			linkContainer = parent;
		else
			throw new InvalidParameterException(parent.getClass() + "is not a valid element.");
	}

	public TextHyperlink applyHyperlink(URI linkto) {
		// new a text:a element, move all the child under text:p to text:a
		OdfElement parent = linkContainer;
		removeHyperlinks();
		TextAElement aElement;
		aElement = ((OdfContentDom) (parent.getOwnerDocument())).newOdfElement(TextAElement.class);
		aElement.setXlinkTypeAttribute("simple");
		aElement.setXlinkHrefAttribute(linkto.toString());
		Node node = parent.getFirstChild();
		while (node != null) {
			Node thisNode = node;
			node = node.getNextSibling();
			parent.removeChild(thisNode);
			aElement.appendChild(thisNode);
		}
		parent.appendChild(aElement);
		return TextHyperlink.getInstanceof(aElement);
	}

	public void removeHyperlinks() {
		OdfElement parent = linkContainer;
		TextAElement aElement = OdfElement.findFirstChildNode(TextAElement.class, parent);
		while (aElement != null) {
			Node node = aElement.getFirstChild();
			while (node != null) {
				Node thisNode = node;
				node = node.getNextSibling();
				aElement.removeChild(thisNode);
				parent.insertBefore(thisNode, aElement);
			}
			TextAElement thisElement = aElement;
			aElement = OdfElement.findNextChildNode(TextAElement.class, aElement);
			parent.removeChild(thisElement);
		}
	}

	public SimpleHyperlinkIterator getHyperlinkIterator() {
		return new SimpleHyperlinkIterator(linkContainer);
	}

	public TextHyperlink appendHyperlink(String text, URI linkto) {
		OdfElement parent = linkContainer;
		TextAElement aElement;
		aElement = ((OdfContentDom) (parent.getOwnerDocument())).newOdfElement(TextAElement.class);
		aElement.setXlinkTypeAttribute("simple");
		aElement.setXlinkHrefAttribute(linkto.toString());
		aElement.setTextContent(text);
		parent.appendChild(aElement);
		return TextHyperlink.getInstanceof(aElement);
	}

	private class SimpleHyperlinkIterator implements Iterator<TextHyperlink> {

		private OdfElement containerElement;
		private TextHyperlink nextElement = null;
		private TextHyperlink tempElement = null;

		private SimpleHyperlinkIterator(OdfElement container) {
			containerElement = container;
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public TextHyperlink next() {
			if (tempElement != null) {
				nextElement = tempElement;
				tempElement = null;
			} else {
				nextElement = findNext(nextElement);
			}
			if (nextElement == null) {
				return null;
			} else {
				return nextElement;
			}
		}

		public void remove() {
			if (nextElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			containerElement.removeChild(nextElement.getOdfElement());
		}

		private TextHyperlink findNext(TextHyperlink thisLink) {
			TextAElement nextLink = null;
			if (thisLink == null) {
				nextLink = OdfElement.findFirstChildNode(TextAElement.class, containerElement);
			} else {
				nextLink = OdfElement.findNextChildNode(TextAElement.class, thisLink.getOdfElement());
			}

			if (nextLink != null) {
				return TextHyperlink.getInstanceof(nextLink);
			}
			return null;
		}
	}

}
