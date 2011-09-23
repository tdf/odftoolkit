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

import org.odftoolkit.odfdom.dom.element.text.TextPageCountElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.meta.Meta;
import org.odftoolkit.simple.style.NumberFormat;

/**
 * A <tt>PageCountField</tt> displays the total number of pages in a document.
 * The value of this field is from the owner document meta statistic
 * information.
 * <p>
 * NOTE: Before the document is opened in any editor, the value of this field
 * maybe invalid.
 * 
 * @since 0.5
 */
public class PageCountField extends Field {

	private TextPageCountElement pageCountElement;

	// package constructor, only called by Fields
	PageCountField(OdfElement odfElement) {
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		pageCountElement = spanElement.newTextPageCountElement(null);
		try {
			OdfFileDom dom = (OdfFileDom) odfElement.getOwnerDocument();
			Meta meta = ((Document) dom.getDocument()).getOfficeMetadata();
			int count = meta.getDocumentStatistic().getPageCount();
			pageCountElement.setTextContent(String.valueOf(count));
		} catch (Exception e) {
			// get meta info failed, do not set count value. Let editor update
			// it.
		}
		Component.registerComponent(this, getOdfElement());
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
		pageCountElement.setStyleNumFormatAttribute(format.toString());
	}

	/**
	 * Return an instance of <code>TextPageCountElement</code> which represents
	 * this feature.
	 * 
	 * @return an instance of <code>TextPageCountElement</code>
	 */
	public TextPageCountElement getOdfElement() {
		return pageCountElement;
	}

	@Override
	public FieldType getFieldType() {
		return FieldType.PAGE_COUNT_FIELD;
	}
}
