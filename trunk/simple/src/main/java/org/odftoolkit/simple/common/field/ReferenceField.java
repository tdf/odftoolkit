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

import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextMetaElement;
import org.odftoolkit.odfdom.dom.element.text.TextMetaFieldElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextReferenceMarkEndElement;
import org.odftoolkit.odfdom.dom.element.text.TextReferenceMarkStartElement;
import org.odftoolkit.odfdom.dom.element.text.TextReferenceRefElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.w3c.dom.Node;

/**
 * A ReferenceField refers to a field that appears in another location in a
 * document.
 * <p>
 * The advantage of entering a reference as a field is that you do not have to
 * adjust the references manually every time you change the document. Just
 * update the fields and the references in the document are updated too.
 * <p>
 * NOTE: Before the document is opened in any editor, the value of this field
 * maybe invalid.
 * 
 * @since 0.5
 */
public class ReferenceField extends Field {

	private OdfElement referencedElement;
	private String name;
	private TextReferenceMarkStartElement referenceMarkStartElement;
	private TextReferenceMarkEndElement referenceMarkEndElement;

	/**
	 * A <tt>DisplayType</tt> specifies the information that a reference field
	 * should display.
	 * <ul>
	 * <li>CHAPTER: displays the number of the chapter in which the referenced
	 * item appears.
	 * <li>DIRECTION: displays whether the referenced item is above or below the
	 * reference field.
	 * <li>PAGE: displays the number of the page on which the referenced item
	 * appears.
	 * <li>TEXT: displays the text of the referenced item.
	 * </ul>
	 * 
	 * @since 0.5
	 */
	public static enum DisplayType {

		CHAPTER("chapter"), DIRECTION("direction"), PAGE("page"), TEXT("text");

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
	ReferenceField(OdfElement odfElement, String referenceName) {
		OdfElement parentEle = (OdfElement) odfElement.getParentNode();
		if (parentEle instanceof TextAElement || parentEle instanceof TextHElement
				|| parentEle instanceof TextMetaElement || parentEle instanceof TextMetaFieldElement
				|| parentEle instanceof TextPElement || parentEle instanceof TextSpanElement) {
			referencedElement = odfElement;
			name = referenceName;
			// insert start target element
			referenceMarkStartElement = ((OdfFileDom) odfElement.getOwnerDocument())
					.newOdfElement(TextReferenceMarkStartElement.class);
			referenceMarkStartElement.setTextNameAttribute(referenceName);
			parentEle.insertBefore(referenceMarkStartElement, odfElement);
			// insert end target element
			referenceMarkEndElement = ((OdfFileDom) odfElement.getOwnerDocument())
					.newOdfElement(TextReferenceMarkEndElement.class);
			referenceMarkEndElement.setTextNameAttribute(referenceName);
			OdfElement nextSiblingEle = (OdfElement) odfElement.getNextSibling();
			if (nextSiblingEle == null) {
				parentEle.appendChild(referenceMarkEndElement);
			} else {
				parentEle.insertBefore(referenceMarkEndElement, nextSiblingEle);
			}
			Component.registerComponent(this, getOdfElement());
		} else {
			throw new IllegalArgumentException("The specific odf element can't own a reference field.");
		}
	}

	/**
	 * Set the name of this reference field.
	 * 
	 * @param referenceName
	 *            the name of this content.
	 */
	public void setName(String referenceName) {
		referenceMarkStartElement.setTextNameAttribute(name);
		name = referenceName;
	}

	/**
	 * Get the name of this reference field.
	 * 
	 * @return the name of this reference field.
	 */
	public String getName() {
		return referenceMarkStartElement.getTextNameAttribute();
	}

	/**
	 * Append this reference field after the specifics OdfElement.
	 * 
	 * @param odfEle
	 *            the reference of odf element.
	 *@param type
	 *            the display type.
	 */
	public void appendReferenceTo(OdfElement odfEle, DisplayType type) {
		// create reference ref element.
		TextSpanElement spanElement = ((OdfFileDom) odfEle.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		TextReferenceRefElement referenceRefElement = spanElement.newTextReferenceRefElement();
		referenceRefElement.setTextRefNameAttribute(name);
		referenceRefElement.setTextReferenceFormatAttribute(type.toString());
		// insert
		if (odfEle instanceof TextPElement) {
			odfEle.appendChild(spanElement);
		} else {
			OdfElement parentEle = (OdfElement) odfEle.getParentNode();
			Node nextSiblingEle =  odfEle.getNextSibling();
			TextPElement pElement = ((OdfFileDom) odfEle.getOwnerDocument()).newOdfElement(TextPElement.class);
			pElement.appendChild(spanElement);
			if (nextSiblingEle == null) {
				parentEle.appendChild(pElement);
			} else {
				parentEle.insertBefore(pElement, nextSiblingEle);
			}
		}
	}

	public OdfElement getOdfElement() {
		return referencedElement;
	}

	@Override
	public FieldType getFieldType() {
		return FieldType.REFERENCE_FIELD;
	}
}
