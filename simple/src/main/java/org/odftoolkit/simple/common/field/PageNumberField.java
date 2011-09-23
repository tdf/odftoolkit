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

import org.odftoolkit.odfdom.dom.element.text.TextPageNumberElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.style.NumberFormat;

/**
 * A <tt>PageNumberField</tt> displays the current page number of pages in a
 * document.
 * <p>
 * NOTE: Before the document is opened in any editor, the value of this field is
 * invalid.
 * 
 * @since 0.5
 */
public class PageNumberField extends Field {

	private TextPageNumberElement pageNumberElement;
	private DisplayType type;

	/**
	 * A <tt>DisplayType</tt> represents the selected page attribute of the
	 * field. The page immediately preceding the current page, current page and
	 * the page immediately following the current page are supported.
	 * 
	 * @since 0.5
	 */
	public static enum DisplayType {

		PREVIOUS_PAGE("previous"), CURRENT_PAGE("current"), NEXT_PAGE("next");

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
	PageNumberField(OdfElement odfElement) {
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		pageNumberElement = spanElement.newTextPageNumberElement(null);
		setDisplayPage(DisplayType.CURRENT_PAGE);
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Specifies whether to display or not the number of a previous or following
	 * page rather than the number of the current page.
	 * 
	 * @param type
	 *            the display type which is predefined.
	 */
	public void setDisplayPage(DisplayType type) {
		pageNumberElement.setTextSelectPageAttribute(type.toString());
		this.type = type;
	}

	/**
	 * Specifies the number format of this field.
	 * 
	 * @param format
	 *            the format which is predefined in
	 *            {@link org.odftoolkit.simple.style.NumberFormat NumberFormat}.
	 * @see org.odftoolkit.simple.style.NumberFormat
	 */
	public void setNumberFormat(NumberFormat format) {
		pageNumberElement.setStyleNumFormatAttribute(format.toString());
	}

	/**
	 * Return an instance of <code>TextPageNumberElement</code> which represents
	 * this feature.
	 * 
	 * @return an instance of <code>TextPageNumberElement</code>
	 */
	public TextPageNumberElement getOdfElement() {
		return pageNumberElement;
	}

	@Override
	public FieldType getFieldType() {
		switch (type) {
		case PREVIOUS_PAGE:
			return FieldType.PREVIOUS_PAGE_NUMBER_FIELD;
		case CURRENT_PAGE:
			return FieldType.CURRENT_PAGE_NUMBER_FIELD;
		case NEXT_PAGE:
			return FieldType.NEXT_PAGE_NUMBER_FIELD;
		default:
			return null;
		}
	}
}
