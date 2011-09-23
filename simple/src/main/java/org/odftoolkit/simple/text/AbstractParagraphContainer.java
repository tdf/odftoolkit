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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

/**
 * AbstractParagraphContainer is an abstract class to present paragraph
 * container. It contains the default implementation to add/get/remove
 * paragraph. Each subclass needs to implement getParagraphContainerElement().
 * 
 * @since 0.5
 */
public abstract class AbstractParagraphContainer implements ParagraphContainer {

	abstract public OdfElement getParagraphContainerElement();

	/**
	 * Add paragraph at the end of the container with specified text content.
	 * 
	 * @param textContent
	 *            the text content
	 * @return an instance of paragraph
	 */
	public Paragraph addParagraph(String textContent) {
		Paragraph para = Paragraph.newParagraph(this);
		para.setTextContent(textContent);
		return para;
	}

	/**
	 * Remove paragraph from the container
	 * 
	 * @param para
	 *            the instance of paragraph
	 * @return true if the paragraph is removed successfully, false if errors
	 *         happen.
	 */
	public boolean removeParagraph(Paragraph para) {
		try {
			para.remove();
		} catch (Exception exception) {
			Logger.getLogger(AbstractParagraphContainer.class.getName()).log(Level.WARNING, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Return an Iterator of the paragraph in this container.
	 * 
	 * @return an Iterator of the paragraph in this container
	 */
	public Iterator<Paragraph> getParagraphIterator() {
		return new SimpleParagraphIterator(this);
	}

	/**
	 * Return a paragraph with a given index.
	 * <p>
	 * An index of zero represents the first paragraph.
	 * <p>
	 * If empty paragraph is skipped, the empty paragraph won't be counted.
	 * 
	 * @param index
	 *            the index started from 0.
	 * @param isEmptyParagraphSkipped
	 *            whether the empty paragraph is skipped or not.
	 * @return the paragraph with a given index
	 */
	public Paragraph getParagraphByIndex(int index, boolean isEmptyParagraphSkipped) {
		Iterator<Paragraph> iterator = getParagraphIterator();
		while (iterator.hasNext() && (index >= 0)) {
			Paragraph current = iterator.next();
			if (isEmptyParagraphSkipped) {
				String content = current.getTextContent();
				if ((content == null) || (content.length() == 0)) {
					continue;
				}
			}
			if (index == 0) {
				return current;
			}
			index--;
		}
		return null;
	}

	/**
	 * Return a paragraph with a given index. The index is in reverse order.
	 * <p>
	 * An index of zero represents the last paragraph.
	 * <p>
	 * If empty paragraph is skipped, the empty paragraph won't be counted.
	 * 
	 * @param reverseIndex
	 *            the index started from 0 in reverse order.
	 * @param isEmptyParagraphSkipped
	 *            whether the empty paragraph is skipped or not.
	 * @return the paragraph with a given index.
	 */
	public Paragraph getParagraphByReverseIndex(int reverseIndex, boolean isEmptyParagraphSkipped) {
		OdfElement containerElement = getParagraphContainerElement();
		Node node = containerElement.getLastChild();
		while (node != null && (reverseIndex >= 0)) {
			if (node.getNodeType() != Node.ELEMENT_NODE) {
				node = node.getPreviousSibling();
				continue;
			}
			if (node instanceof TextParagraphElementBase) {
				if (isEmptyParagraphSkipped) {
					if (node.hasChildNodes() == false || node.getTextContent() == null
							|| node.getTextContent().length() == 0) {
						node = node.getPreviousSibling();
						continue;
					}
				}
				if (reverseIndex == 0) {
					return Paragraph.getInstanceof((TextParagraphElementBase) node);
				}
				reverseIndex--;
			}
			node = node.getPreviousSibling();
		}
		return null;
	}

	private class SimpleParagraphIterator implements Iterator<Paragraph> {

		private OdfElement containerElement;
		private Paragraph nextElement = null;
		private Paragraph tempElement = null;

		private SimpleParagraphIterator(ParagraphContainer container) {
			containerElement = container.getParagraphContainerElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public Paragraph next() {
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
			nextElement.remove();
		}

		private Paragraph findNext(Paragraph thisBox) {
			TextParagraphElementBase nextParagraph = null;
			if (thisBox == null) {
				nextParagraph = OdfElement.findFirstChildNode(TextParagraphElementBase.class, containerElement);
			} else {
				nextParagraph = OdfElement.findNextChildNode(TextParagraphElementBase.class, thisBox.getOdfElement());
			}

			if (nextParagraph != null) {
				return Paragraph.getInstanceof(nextParagraph);
			}
			return null;
		}
	}
}
