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

import org.odftoolkit.odfdom.dom.element.form.FormFixedTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;

/**
 * This class represents the form control of Label, provides methods to get/set
 * the form properties and the style formatting of this control.
 * 
 * @since 0.8
 */
public class Label extends FormControl {

	Label(FormFixedTextElement element) {
		mElement = element;
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of label by an instance of FormFixedTextElement, while
	 * searching the document content to make a bind with the DrawControl which
	 * already reference to this label.
	 * 
	 * @param element
	 *            - an instance of FormFixedTextElement
	 * @return an instance of label
	 */
	public static Label getInstanceOf(FormFixedTextElement element) {
		Label label = new Label(element);
		try {
			label.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(Label.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this label.");
		}
		return label;
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		((FormFixedTextElement) mElement)
				.setFormControlImplementationAttribute(controlImpl);
	}

	@Override
	public String getId() {
		return ((FormFixedTextElement) mElement).getFormIdAttribute();
	}

	@Override
	public void setId(String id) {
		((FormFixedTextElement) mElement).setFormIdAttribute(id);
	}

	@Override
	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null)
			mFormProperties = ((FormFixedTextElement) mElement)
					.newFormPropertiesElement();
		return mFormProperties;
	}

	@Override
	public String getName() {
		return ((FormFixedTextElement) mElement).getFormNameAttribute();
	}

	@Override
	public void setName(String name) {
		((FormFixedTextElement) mElement).setFormNameAttribute(name);
	}

	/**
	 * Set the text content of this label
	 * 
	 * @param label
	 *            - the text content of this label
	 */
	public void setLabel(String label) {
		((FormFixedTextElement) mElement).setFormLabelAttribute(label);
	}

	/**
	 * Get the text content of this label
	 * 
	 * @return the text content of this label
	 */
	public String getLabel() {
		return ((FormFixedTextElement) mElement).getFormLabelAttribute();
	}

	/**
	 * Get a simple iterator for labels.
	 * 
	 * @param container
	 *            - an instance of form where to traverse the labels
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleLabelIterator(container);
	}

	private static class SimpleLabelIterator implements Iterator<FormControl> {

		private FormFormElement containerElement;
		private Label nextElement = null;
		private Label tempElement = null;

		private SimpleLabelIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public Label next() {
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

		private Label findNext(Label thisLabel) {
			FormFixedTextElement nextLabel = null;
			if (thisLabel == null) {
				nextLabel = OdfElement.findFirstChildNode(
						FormFixedTextElement.class, containerElement);
			} else {
				nextLabel = OdfElement.findNextChildNode(
						FormFixedTextElement.class, thisLabel.getOdfElement());
			}

			if (nextLabel != null) {
				return Label.getInstanceOf(nextLabel);
			}
			return null;
		}
	}

}
