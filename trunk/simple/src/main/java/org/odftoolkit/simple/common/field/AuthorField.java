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

import org.odftoolkit.odfdom.dom.element.text.TextAuthorInitialsElement;
import org.odftoolkit.odfdom.dom.element.text.TextAuthorNameElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;

/**
 * An <tt>AuthorField</tt> represents the initials of the author or the full
 * name of the author of a document.
 * <p>
 * NOTE: Before the document is opened in any editor, the value of this field is
 * invalid.
 * 
 * @since 0.5
 */
public class AuthorField extends Field {

	private final boolean isInitials;

	private TextAuthorNameElement authorNameElement;
	private TextAuthorInitialsElement authorInitialsElement;

	// package constructor, only called by Fields
	AuthorField(OdfElement odfElement, boolean isAuthorInitials) {
		isInitials = isAuthorInitials;
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		if (isAuthorInitials) {
			authorInitialsElement = spanElement.newTextAuthorInitialsElement();
			authorInitialsElement.setTextFixedAttribute(false);
		} else {
			authorNameElement = spanElement.newTextAuthorNameElement();
			authorNameElement.setTextFixedAttribute(false);
		}
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Return an instance of <code>OdfElement</code> which represents this
	 * feature. If this is an initial author field, an instance of
	 * <code>TextAuthorInitialsElement</code> is returned, otherwise an instance
	 * of <code>TextAuthorNameElement</code> is returned.
	 * 
	 * @return an instance of <code>OdfElement</code>
	 */
	public OdfElement getOdfElement() {
		if (isInitials) {
			return authorInitialsElement;
		} else {
			return authorNameElement;
		}
	}

	@Override
	public FieldType getFieldType() {
		return isInitials ? FieldType.AUTHOR_INITIALS_FIELD : FieldType.AUTHOR_NAME_FIELD;
	}
}
