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

package org.odftoolkit.simple.common;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * It's a tool class to help process white space.
 */
public class WhitespaceProcessor {
	private int nSpaces;
	private String partial;
	private OdfFileDom owner;
	private Element element;

	/**
	 * Add given text content to an element, handling multiple blanks, tabs, and
	 * newlines properly.
	 * 
	 * @param element
	 *            the element to which content is being added
	 * @param content
	 *            text content including whitespace
	 */
	public void append(Element element, String content) {
		int i = 0;
		char ch;
		this.element = element;
		partial = "";
		nSpaces = 0;
		owner = (OdfFileDom) element.getOwnerDocument();
		for (i = 0; i < content.length(); i++) {
			ch = content.charAt(i);
			if (ch == ' ') {
				if (nSpaces == 0) {
					partial += " ";
				}
				nSpaces++;
			} else if (ch == '\n') {
				emitPartial();
				element.appendChild(new TextLineBreakElement(owner));
			} else if (ch == '\t') {
				emitPartial();
				element.appendChild(new TextTabElement(owner));
			} else if (ch != '\r') // ignore DOS half of CR-LF
			{
				if (nSpaces > 1) {
					emitPartial();
				}
				partial += ch;
				nSpaces = 0;
			}
		}
		emitPartial();
	}

	/**
	 * Retrieve the text content of an element. Recursively retrieves all the
	 * text nodes, expanding whitespace where necessary. Ignores any elements
	 * except <code>&lt;text:s&gt;</code>, <code>&lt;text:line-break&gt;</code>
	 * and <code>&lt;text:tab&gt</code>.
	 * 
	 * @param element
	 *            an element whose text you want to retrieve
	 * @return the element's text content, with whitespace expanded
	 * 
	 * @deprecated As of Simple version 0.3.5, replaced by
	 *             <code>TextExtractor.getText(OdfElement)</code>
	 * 
	 * @see TextExtractor#getText(OdfElement)
	 */
	public String getText(Node element) {
		String result = "";
		int spaceCount;
		Node node = element.getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.TEXT_NODE) {
				result += node.getNodeValue();
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (node.getLocalName().equals("s")) // text:s
				{
					try {
						spaceCount = Integer.parseInt(((Element) node).getAttributeNS(OdfDocumentNamespace.TEXT
								.getUri(), "c"));
					} catch (Exception e) {
						spaceCount = 1;
					}
					for (int i = 0; i < spaceCount; i++) {
						result += " ";
					}
				} else if (node.getLocalName().equals("line-break")) {
					result += "\n";
				} else if (node.getLocalName().equals("tab")) {
					result += "\t";
				} else {
					result = result + getText(node);
				}
			}
			node = node.getNextSibling();
		}
		return result;
	}

	/**
	 * Append text content to a given element, handling whitespace properly.
	 * This is a static method that creates its own WhitespaceProcessor, so that
	 * you don't have to.
	 * 
	 * @param element
	 *            the element to which content is being added
	 * @param content
	 *            text content including whitespace
	 */
	public static void appendText(Element element, String content) {
		WhitespaceProcessor processor = new WhitespaceProcessor();
		processor.append(element, content);
	}

	/*
	 * Send out any information that has been buffered
	 */
	private void emitPartial() {
		/* send out any partial text */
		if (!partial.equals("")) {
			element.appendChild(owner.createTextNode(partial));
		}
		/* and any spaces if necessary */
		if (nSpaces > 1) {
			TextSElement spaceElement = new TextSElement(owner);
			spaceElement.setTextCAttribute(new Integer(nSpaces - 1));
			element.appendChild(spaceElement);
		}
		/* and reset all the counters */
		nSpaces = 0;
		partial = "";
	}
}