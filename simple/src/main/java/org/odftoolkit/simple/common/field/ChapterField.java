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

package org.odftoolkit.simple.common.field;

import org.odftoolkit.odfdom.dom.element.text.TextChapterElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;

/**
 * ChapterField is placed inside a header or footer, it displays the current
 * chapter name or number on every page.
 * <p>
 * NOTE: Before the document is opened in any editor, the value of this field
 * maybe invalid.
 * 
 * @since 0.5
 */
public class ChapterField extends Field {

	private TextChapterElement chapterElement;

	/**
	 * A <tt>DisplayType</tt> specifies the information that a chapter field
	 * should display.
	 * 
	 * @since 0.5
	 */
	public static enum DisplayType {

		NAME("name"), 
		NUMBER("number"), 
		NUMBER_AND_NAME("number-and-name"), 
		PLAIN_NUMBER("plain-number"), 
		PLAIN_NUMBER_AND_NAME("plain-number-and-name");

		private final String displayType;

		DisplayType(String type) {
			displayType = type;
		}

		@Override
		public String toString() {
			return displayType;
		}
	}

	// package constructor, only called by Fields
	ChapterField(OdfElement odfElement) {
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		chapterElement = spanElement.newTextChapterElement(null, 1);
		setDisplayPage(DisplayType.NUMBER_AND_NAME);
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Specifies the information that a chapter field should display.
	 * 
	 * @param type
	 *            the display type which is predefined in
	 *            {@link ChapterField.DisplayType DisplayType}.
	 */
	public void setDisplayPage(DisplayType type) {
		chapterElement.setTextDisplayAttribute(type.toString());
	}

	/**
	 * Specifies the outline level to be displayed.
	 * 
	 * @param level
	 *            the outline level to be displayed.
	 */
	public void setOutlineLevel(int level) {
		chapterElement.setTextOutlineLevelAttribute(level);
	}

	/**
	 * Return an instance of <code>TextChapterElement</code> which represents
	 * this feature.
	 * 
	 * @return an instance of <code>TextChapterElement</code>
	 */
	public TextChapterElement getOdfElement() {
		return chapterElement;
	}

	@Override
	public FieldType getFieldType() {
		return FieldType.CHAPTER_FIELD;
	}
}
