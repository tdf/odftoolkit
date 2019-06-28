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

import org.odftoolkit.odfdom.dom.element.form.FormButtonElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;

/**
 * This class represents the form control of Button, provides methods to get/set
 * the form properties and the style formatting of this control.
 *
 * @since 0.8
 */
public class Button extends FormControl {

	Button(FormButtonElement element) {
		mElement = element;
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of button by an instance of FormButtonElement, while
	 * searching the document content to make a bind with the DrawControl which
	 * already reference to this button.
	 *
	 * @param element
	 *            - an instance of FormButtonElement
	 * @return an instance of button
	 */
	public static Button getInstanceOf(FormButtonElement element) {
		Button btn = new Button(element);
		try {
			btn.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(Button.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this button.");
		}
		return btn;
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		((FormButtonElement) mElement)
				.setFormControlImplementationAttribute(controlImpl);
	}

	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null)
			mFormProperties = ((FormButtonElement) mElement)
					.newFormPropertiesElement();
		return mFormProperties;
	}

	@Override
	public void setId(String id) {
		((FormButtonElement) mElement).setFormIdAttribute(id);

	}

	@Override
	public String getId() {
		return ((FormButtonElement) mElement).getFormIdAttribute();
	}

	@Override
	public String getName() {
		return ((FormButtonElement) mElement).getFormNameAttribute();
	}

	@Override
	public void setName(String name) {
		((FormButtonElement) mElement).setFormNameAttribute(name);
	}

	/**
	 * Set the label content of this button
	 *
	 * @param label
	 *            - the label
	 */
	public void setLabel(String label) {
		((FormButtonElement) mElement).setFormLabelAttribute(label);
	}

	/**
	 * Get the label content of this button
	 *
	 * @return the label
	 */
	public String getLabel() {
		return ((FormButtonElement) mElement).getFormLabelAttribute();
	}

	/**
	 * Set the default value of this control, it will be override by current
	 * value.
	 *
	 * @param value
	 *            - the default value
	 */
	public void setValue(String value) {
		((FormButtonElement) mElement).setFormValueAttribute(value);

	}

	/**
	 * Get a simple iterator for buttons.
	 *
	 * @param container
	 *            - an instance of form where to traverse the buttons
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleButtonIterator(container);
	}

	private static class SimpleButtonIterator implements Iterator<FormControl> {

		private FormFormElement containerElement;
		private Button nextElement = null;
		private Button tempElement = null;

		private SimpleButtonIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public Button next() {
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

		private Button findNext(Button thisButton) {
			FormButtonElement nextForm = null;
			if (thisButton == null) {
				nextForm = OdfElement.findFirstChildNode(
						FormButtonElement.class, containerElement);
			} else {
				nextForm = OdfElement.findNextChildNode(
						FormButtonElement.class, thisButton.getOdfElement());
			}

			if (nextForm != null) {
				return Button.getInstanceOf(nextForm);
			}
			return null;
		}
	}
}
