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

import org.odftoolkit.odfdom.dom.element.text.TextUserFieldDeclElement;
import org.odftoolkit.odfdom.dom.element.text.TextUserFieldDeclsElement;
import org.odftoolkit.odfdom.dom.element.text.TextVariableDeclElement;
import org.odftoolkit.odfdom.dom.element.text.TextVariableDeclsElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.common.field.VariableField.VariableType;

/**
 * AbstractVariableContainer is an abstract implementation of the
 * VariableContainer interface, with a default implementation for every method
 * defined in VariableContainer , except getVariableContainerElement(). A
 * subclass must implement the abstract method getVariableContainerElement().
 * 
 * @since 0.5
 */
public abstract class AbstractVariableContainer implements VariableContainer {

	public VariableField declareVariable(String name, VariableType type) {
		VariableField variableField = null;
		switch (type) {
		case SIMPLE:
			variableField = Fields.createSimpleVariableField(this, name);
			break;
		case USER:
			variableField = Fields.createUserVariableField(this, name, "0");
			break;
		case SEQUENCE:
			throw new IllegalArgumentException("Simple Java API for ODF doesn't support this type now.");
		}
		return variableField;
	}

	public VariableField getVariableFieldByName(String name) {
		OdfElement containerElement = getVariableContainerElement();
		TextVariableDeclsElement simpleVariableElements = OdfElement.findFirstChildNode(TextVariableDeclsElement.class,
				containerElement);
		if (simpleVariableElements != null) {
			TextVariableDeclElement simpleVariableElement = (TextVariableDeclElement) simpleVariableElements
					.getFirstChild();
			while (simpleVariableElement != null) {
				if (name.equals(simpleVariableElement.getTextNameAttribute())) {
					return Fields.createSimpleVariableField(this, name);
				} else {
					simpleVariableElement = (TextVariableDeclElement) simpleVariableElement.getNextSibling();
				}
			}
		}
		TextUserFieldDeclsElement userVariableElements = OdfElement.findFirstChildNode(TextUserFieldDeclsElement.class,
				containerElement);
		if (userVariableElements != null) {
			TextUserFieldDeclElement userVariableElement = (TextUserFieldDeclElement) userVariableElements
					.getFirstChild();
			while (userVariableElement != null) {
				if (name.equals(userVariableElement.getTextNameAttribute())) {
					String type = userVariableElement.getOfficeValueTypeAttribute();
					if(type.equalsIgnoreCase("string")){
						String stringValue = userVariableElement.getOfficeStringValueAttribute();
						return Fields.createUserVariableField(this, name, stringValue);
					}
					Double DoubleValue = userVariableElement.getOfficeValueAttribute();
					DoubleValue.toString();
					return Fields.createUserVariableField(this, name, DoubleValue.toString());
				} else {
					userVariableElement = (TextUserFieldDeclElement) userVariableElement.getNextSibling();
				}
			}
		}
		return null;
	}
}
