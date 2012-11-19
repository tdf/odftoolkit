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

import org.odftoolkit.odfdom.dom.element.form.FormCheckboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.form.FormTypeDefinition.FormCheckboxState;

/**
 * This class represents the form control of Check Box, provides methods to
 * get/set the form properties and the style formatting of this control.
 * 
 * @since 0.8
 */
public class CheckBox extends FormControl {

	CheckBox(FormCheckboxElement element) {
		mElement = element;
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of check box by an instance of FormCheckboxElement, while
	 * searching the document content to make a bind with the DrawControl which
	 * already reference to this check box.
	 * 
	 * @param element
	 *            - an instance of FormCheckboxElement
	 * @return an instance of check box
	 */
	public static CheckBox getInstanceOf(FormCheckboxElement element) {
		CheckBox checkbox = new CheckBox(element);
		try {
			checkbox.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(CheckBox.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this check box.");
		}
		return checkbox;
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		((FormCheckboxElement) mElement)
				.setFormControlImplementationAttribute(controlImpl);
	}

	@Override
	public String getId() {
		return ((FormCheckboxElement) mElement).getFormIdAttribute();
	}

	@Override
	public void setId(String id) {
		((FormCheckboxElement) mElement).setFormIdAttribute(id);
	}

	@Override
	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null)
			mFormProperties = ((FormCheckboxElement) mElement)
					.newFormPropertiesElement();
		return mFormProperties;
	}

	/**
	 * Set the label content of this check box
	 * 
	 * @param label
	 *            - the label of this check box
	 */
	public void setLabel(String label) {
		((FormCheckboxElement) mElement).setFormLabelAttribute(label);
	}

	/**
	 * Get the label content of this check box
	 * 
	 * @param label
	 *            - the label of this check box
	 */
	public String getLabel() {
		return ((FormCheckboxElement) mElement).getFormLabelAttribute();
	}

	@Override
	public String getName() {
		return ((FormCheckboxElement) mElement).getFormNameAttribute();
	}

	@Override
	public void setName(String name) {
		((FormCheckboxElement) mElement).setFormNameAttribute(name);
	}

	/**
	 * Set the default value of this control, it will be override by current
	 * value.
	 * 
	 * @param value
	 *            - the default value
	 */
	public void setValue(String value) {
		((FormCheckboxElement) mElement).setFormValueAttribute(value);

	}

	/**
	 * Get the default value of this control, it will be override by current
	 * value.
	 * 
	 * @return the default value
	 */
	public String getValue() {
		return ((FormCheckboxElement) mElement).getFormValueAttribute();

	}

	/**
	 * Set the default state of this check box
	 * 
	 * @param state
	 *            - default state of this check box
	 */
	public void setDefaultState(FormCheckboxState state) {
		((FormCheckboxElement) mElement)
				.setFormStateAttribute(state.toString());
	}

	/**
	 * Get the default state of this check box
	 * 
	 * @return default state of this check box
	 */
	public FormCheckboxState getDefaultState() {
		String value = ((FormCheckboxElement) mElement).getFormStateAttribute();
		return FormCheckboxState.enumValueOf(value);
	}

	/**
	 * Set current state of this check box
	 * 
	 * @param state
	 *            - current state of this check box
	 */
	public void setCurrentState(FormCheckboxState state) {
		((FormCheckboxElement) mElement).setFormCurrentStateAttribute(state
				.toString());
	}

	/**
	 * Get current state of this check box
	 * 
	 * @return current state of this check box
	 */
	public FormCheckboxState getCurrentState() {
		String value = ((FormCheckboxElement) mElement)
				.getFormCurrentStateAttribute();
		return FormCheckboxState.enumValueOf(value);
	}

	/**
	 * Get a simple iterator for check boxes.
	 * 
	 * @param container
	 *            - an instance of form where to traverse the check boxes
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleCheckBoxIterator(container);
	}

	private static class SimpleCheckBoxIterator implements
			Iterator<FormControl> {

		private FormFormElement containerElement;
		private CheckBox nextElement = null;
		private CheckBox tempElement = null;

		private SimpleCheckBoxIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public CheckBox next() {
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

		private CheckBox findNext(CheckBox thisCheckBox) {
			FormCheckboxElement nextCheckBox = null;
			if (thisCheckBox == null) {
				nextCheckBox = OdfElement.findFirstChildNode(
						FormCheckboxElement.class, containerElement);
			} else {
				nextCheckBox = OdfElement
						.findNextChildNode(FormCheckboxElement.class,
								thisCheckBox.getOdfElement());
			}

			if (nextCheckBox != null) {
				return CheckBox.getInstanceOf(nextCheckBox);
			}
			return null;
		}
	}

}
