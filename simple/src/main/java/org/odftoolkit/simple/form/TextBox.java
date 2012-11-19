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

package org.odftoolkit.simple.form;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.xerces.dom.ParentNode;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.dom.element.form.FormTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormTextareaElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.draw.Control;
import org.w3c.dom.Node;

/**
 * This class represents the form control of Text Box, provides methods to
 * get/set the form properties and the style formatting of this control.
 * 
 * @since 0.8
 */
public class TextBox extends FormControl {
	private boolean isMultipleLine = false;

	TextBox(OdfElement element) {
		if (element instanceof FormTextareaElement) {
			isMultipleLine = true;
			mElement = (FormTextareaElement) element;
		} else {
			mElement = (FormTextElement) element;
		}
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of text box by an instance of OdfElement, while searching
	 * the document content to make a bind with the DrawControl which already
	 * reference to this text box.
	 * 
	 * @param element
	 *            - an instance of OdfElement
	 * @return an instance of text box
	 */
	public static TextBox getInstanceOf(OdfElement element) {
		TextBox textbox = new TextBox(element);
		try {
			textbox.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(TextBox.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this textbox.");
		}
		return textbox;
	}

	@Override
	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null) {
			if (isMultipleLine) {
				mFormProperties = ((FormTextareaElement) mElement)
						.newFormPropertiesElement();
			} else {
				mFormProperties = ((FormTextElement) mElement)
						.newFormPropertiesElement();
			}
		}
		return mFormProperties;
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		if (isMultipleLine) {
			((FormTextareaElement) mElement)
					.setFormControlImplementationAttribute(controlImpl);
		} else {
			((FormTextElement) mElement)
					.setFormControlImplementationAttribute(controlImpl);
		}

	}

	@Override
	public String getId() {
		String formId;
		if (isMultipleLine) {
			formId = ((FormTextareaElement) mElement).getFormIdAttribute();
		} else {
			formId = ((FormTextElement) mElement).getFormIdAttribute();
		}
		return formId;
	}

	@Override
	public void setId(String id) {
		if (isMultipleLine) {
			((FormTextareaElement) mElement).setFormIdAttribute(id);
		} else {
			((FormTextElement) mElement).setFormIdAttribute(id);
		}
	}

	@Override
	public String getName() {
		String name;
		if (isMultipleLine) {
			name = ((FormTextareaElement) mElement).getFormNameAttribute();
		} else {
			name = ((FormTextElement) mElement).getFormNameAttribute();
		}
		return name;
	}

	@Override
	public void setName(String name) {
		if (isMultipleLine) {
			((FormTextareaElement) mElement).setFormNameAttribute(name);
		} else {
			((FormTextElement) mElement).setFormNameAttribute(name);
		}
	}

	/**
	 * Set the default value of this control, it will be override by current
	 * value.
	 * 
	 * @param value
	 *            - default value
	 */
	public void setValue(String defaultValue) {
		if (isMultipleLine) {
			((FormTextareaElement) mElement)
					.setFormValueAttribute(defaultValue);
		} else {
			((FormTextElement) mElement).setFormValueAttribute(defaultValue);
		}
	}

	/**
	 * Get the default value of this control
	 * 
	 * @return default value
	 */
	public String getValue() {
		if (isMultipleLine) {
			return ((FormTextareaElement) mElement).getFormValueAttribute();
		} else {
			return ((FormTextElement) mElement).getFormValueAttribute();
		}
	}

	/**
	 * Set the current value of this control, it override the default value.
	 * 
	 * @param currentValue
	 *            - current value
	 */
	public void setCurrentValue(String currentValue) {
		if (isMultipleLine) {
			((FormTextareaElement) mElement)
					.setFormCurrentValueAttribute(currentValue);
		} else {
			((FormTextElement) mElement)
					.setFormCurrentValueAttribute(currentValue);
		}
	}

	/**
	 * Get the current value of this control
	 * 
	 * @return current value
	 */
	public String getCurrentValue() {
		if (isMultipleLine) {
			return ((FormTextareaElement) mElement)
					.getFormCurrentValueAttribute();
		} else {
			return ((FormTextElement) mElement).getFormCurrentValueAttribute();
		}
	}

	/**
	 * Get a simple iterator for text boxes.
	 * 
	 * @param container
	 *            - an instance of form where to traverse the text boxes
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleTextBoxIterator(container);
	}

	private static class SimpleTextBoxIterator implements Iterator<FormControl> {

		private FormFormElement containerElement;
		private TextBox nextElement = null;
		private TextBox tempElement = null;

		private SimpleTextBoxIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public TextBox next() {
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

		private TextBox findNext(TextBox thisTextBox) {
			OdfElement nextTextBox = null;
			if (thisTextBox == null) {
				if (containerElement != null
						&& containerElement instanceof ParentNode) {
					Node node = ((ParentNode) containerElement).getFirstChild();
					while ((node != null)
							&& !FormTextareaElement.class.isInstance(node)
							&& !FormTextElement.class.isInstance(node)) {
						node = node.getNextSibling();
					}
					if (node != null) {
						nextTextBox = (OdfElement) node;
					}
				}
			} else {
				Node refNode = thisTextBox.getOdfElement();
				if (refNode != null) {
					Node node = refNode.getNextSibling();
					while (node != null
							&& !FormTextareaElement.class.isInstance(node)
							&& !FormTextElement.class.isInstance(node)) {
						node = node.getNextSibling();
					}

					if (node != null) {
						nextTextBox = (OdfElement) node;
					}
				}
			}
			if (nextTextBox != null) {
				return TextBox.getInstanceOf(nextTextBox);
			}
			return null;
		}
	}

}
