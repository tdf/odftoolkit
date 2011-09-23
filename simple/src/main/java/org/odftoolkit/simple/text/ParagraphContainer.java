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

import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * ParagraphContainer is a container which maintains paragraph(s) as element(s).
 * Paragraph(s) can be added and removed in this container.
 * 
 * @see Paragraph
 * @see org.odftoolkit.simple.TextDocument
 * 
 * @since 0.5
 */

public interface ParagraphContainer {
	/**
	 * Add paragraph at the end of the container with specified text content.
	 * 
	 * @param textContent
	 *            - the text content
	 * @return an instance of paragraph
	 */
	public Paragraph addParagraph(String textContent);

	/**
	 * Remove paragraph from the container
	 * 
	 * @param para
	 *            - the instance of paragraph
	 * @return true if the paragraph is removed successfully, false if errors
	 *         happen.
	 */
	public boolean removeParagraph(Paragraph para);

	/**
	 * Get the ODF element which can have <text:p> as child element directly.
	 * 
	 * @return - an ODF element which can have paragraph as child
	 */
	public OdfElement getParagraphContainerElement();

	/**
	 * Return an Iterator of the paragraph in this container.
	 * 
	 * @return an Iterator of the paragraph in this container
	 */
	public Iterator<Paragraph> getParagraphIterator();

	/**
	 * Return a paragraph with a given index.
	 * <p>
	 * An index of zero represents the first paragraph.
	 * <p>
	 * If empty paragraph is skipped, the empty paragraph won't be counted.
	 * 
	 * @param index
	 *            - the index started from 0.
	 * @param isEmptyParagraphSkipped
	 *            - whether the empty paragraph is skipped or not
	 * @return the paragraph with a given index
	 */
	public Paragraph getParagraphByIndex(int index, boolean isEmptyParagraphSkipped);

	/**
	 * Return a paragraph with a given index. The index is in reverse order.
	 * <p>
	 * An index of zero represents the last paragraph.
	 * <p>
	 * If empty paragraph is skipped, the empty paragraph won't be counted.
	 * 
	 * @param reverseIndex
	 *            - the index started from 0 in reverse order.
	 * @param isEmptyParagraphSkipped
	 *            - whether the empty paragraph is skipped or not
	 * @return the paragraph with a given index
	 */
	public Paragraph getParagraphByReverseIndex(int reverseIndex, boolean isEmptyParagraphSkipped);
}
