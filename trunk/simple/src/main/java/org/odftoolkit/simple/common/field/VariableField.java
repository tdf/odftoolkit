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

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextUserFieldDeclElement;
import org.odftoolkit.odfdom.dom.element.text.TextUserFieldDeclsElement;
import org.odftoolkit.odfdom.dom.element.text.TextVariableDeclElement;
import org.odftoolkit.odfdom.dom.element.text.TextVariableDeclsElement;
import org.odftoolkit.odfdom.dom.element.text.TextVariableSetElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;

/**
 * ODF document can contain variables, which are processed or displayed as
 * VariableFields. In an ODF file, variable declarations shall precede in
 * document order any use of those variable declarations.
 * 
 * @since 0.5
 */
public class VariableField extends Field {

	private final VariableType type;
	private final String name;

	private TextVariableDeclElement simpleVariableElement;
	private TextVariableDeclsElement simpleVariableElements;

	private TextUserFieldDeclElement userVariableElement;
	private TextUserFieldDeclsElement userVariableElements;

	/**
	 * A <tt>VariableType</tt> represents the type of the variable field. There
	 * are three types of variables:
	 * <ul>
	 * <li>SIMPLE: Simple variables, or variables, can take different values at
	 * different positions throughout a document. Simple variables can be used
	 * to display different text in recurring elements, such as headers or
	 * footers.
	 * <li>USER: User variables have the same value throughout a document. If a
	 * user variable is set anywhere within the document, all fields in the
	 * document that display the user variable have the same value.
	 * <li>SEQUENCE: Sequence variables are used to number items in an ODF text
	 * document. NOTE: Simple Java API for ODF doesn't support this type now.
	 * </ul>
	 * 
	 * @since 0.5
	 */
	public static enum VariableType {

		SIMPLE("simple"), USER("user"), SEQUENCE("sequence");

		private final String variableType;

		VariableType(String type) {
			variableType = type;
		}

		@Override
		public String toString() {
			return variableType;
		}
	}

	// package constructor, only called by Fields
	VariableField(VariableContainer container, String name, VariableType type) {
		this.type = type;
		this.name = name;
		OdfElement containerElement = container.getVariableContainerElement();
		switch (type) {
		case SIMPLE:
			simpleVariableElements = OdfElement.findFirstChildNode(TextVariableDeclsElement.class, containerElement);
			if (simpleVariableElements == null) {
				simpleVariableElements = ((OdfFileDom) containerElement.getOwnerDocument())
						.newOdfElement(TextVariableDeclsElement.class);
				containerElement.insertBefore(simpleVariableElements, containerElement.getFirstChild());
			} else {
				TextVariableDeclElement simpleVariableElementTmp = (TextVariableDeclElement) simpleVariableElements
						.getFirstChild();
				while (simpleVariableElementTmp != null) {
					if (name.equals(simpleVariableElementTmp.getTextNameAttribute())) {
						simpleVariableElement = simpleVariableElementTmp;
						break;
					} else {
						simpleVariableElementTmp = (TextVariableDeclElement) simpleVariableElementTmp.getNextSibling();
					}
				}
			}
			if (simpleVariableElement == null) {
				simpleVariableElement = simpleVariableElements.newTextVariableDeclElement("string", name);
			}
			break;
		case USER:
			userVariableElements = OdfElement.findFirstChildNode(TextUserFieldDeclsElement.class, containerElement);
			if (userVariableElements == null) {
				userVariableElements = ((OdfFileDom) containerElement.getOwnerDocument())
						.newOdfElement(TextUserFieldDeclsElement.class);
				containerElement.insertBefore(userVariableElements, containerElement.getFirstChild());
			} else {
				TextUserFieldDeclElement userVariableElementTmp = (TextUserFieldDeclElement) userVariableElements
						.getFirstChild();
				while (userVariableElementTmp != null) {
					if (name.equals(userVariableElementTmp.getTextNameAttribute())) {
						userVariableElement = userVariableElementTmp;
						break;
					} else {
						userVariableElementTmp = (TextUserFieldDeclElement) userVariableElementTmp.getNextSibling();
					}
				}
			}
			if (userVariableElement == null) {
				userVariableElement = userVariableElements.newTextUserFieldDeclElement(0, "string", name);
			}
			break;
		case SEQUENCE:
			throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
		}
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Reset the value of this variable field after the reference OdfElement.
	 * <p>
	 * Note: For user variable, the value of all fields in the document that
	 * display the user variable will be reset.
	 * 
	 * @param value
	 *            the new value of this variable field.
	 * @param refElement
	 *            the reference OdfElement. The variable field will be appended
	 *            after this element. For user variable, this parameter can be
	 *            null.
	 */
	public void updateField(String value, OdfElement refElement) {
		String officeNS = OdfDocumentNamespace.OFFICE.getUri();
		switch (type) {
		case SIMPLE:
			TextVariableSetElement textVariableSetElement = null;
			if (refElement instanceof TextPElement) {
				textVariableSetElement = ((TextPElement) refElement).newTextVariableSetElement(0, "string", name);
			} else if (refElement instanceof TextSpanElement) {
				textVariableSetElement = ((TextSpanElement) refElement).newTextVariableSetElement(0, "string", name);
			} else {
				TextPElement pElement = ((OdfFileDom) refElement.getOwnerDocument()).newOdfElement(TextPElement.class);
				OdfElement parentEle = (OdfElement) refElement.getParentNode();
				parentEle.insertBefore(pElement, refElement.getNextSibling());
				textVariableSetElement = pElement.newTextVariableSetElement(0, "string", name);
			}
			textVariableSetElement.removeAttributeNS(officeNS, "value");
			textVariableSetElement.setOfficeStringValueAttribute(value);
			textVariableSetElement.setTextContent(value);
			break;
		case USER:
			userVariableElement.setOfficeValueTypeAttribute("string");
			userVariableElement.setOfficeStringValueAttribute(value);
			break;
		case SEQUENCE:
			throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
		}

	}

	/**
	 * Display this variable field after the reference OdfElement.
	 * 
	 * @param refElement
	 *            the reference OdfElement. The variable field will be appended
	 *            after this element.
	 */
	public void displayField(OdfElement refElement) {
		if (refElement instanceof TextSpanElement) {
			TextSpanElement spanEle = (TextSpanElement) refElement;
			switch (type) {
			case SIMPLE:
				spanEle.newTextVariableGetElement(name);
				break;
			case USER:
				spanEle.newTextUserFieldGetElement(name);
				break;
			case SEQUENCE:
				throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
			}
		} else {
			TextPElement textPElement;
			if (refElement instanceof TextPElement) {
				textPElement = (TextPElement) refElement;
			} else {
				textPElement = ((OdfFileDom) refElement.getOwnerDocument()).newOdfElement(TextPElement.class);
				OdfElement parentEle = (OdfElement) refElement.getParentNode();
				parentEle.insertBefore(textPElement, refElement.getNextSibling());
			}
			switch (type) {
			case SIMPLE:
				textPElement.newTextVariableGetElement(name);
				break;
			case USER:
				textPElement.newTextUserFieldGetElement(name);
				break;
			case SEQUENCE:
				throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
			}
		}
	}

	/**
	 * Get the variable field name.
	 * 
	 * @return the variable field name
	 */
	public String getVariableName() {
		return name;
	}

	@Override
	public OdfElement getOdfElement() {
		switch (type) {
		case SIMPLE:
			return simpleVariableElement;
		case USER:
			return userVariableElement;
		case SEQUENCE:
			throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
		}
		return null;
	}

	@Override
	public FieldType getFieldType() {
		switch (type) {
		case SIMPLE:
			return FieldType.SIMPLE_VARIABLE_FIELD;
		case USER:
			return FieldType.USER_VARIABLE_FIELD;
		case SEQUENCE:
			throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
		}
		return null;
	}
}
