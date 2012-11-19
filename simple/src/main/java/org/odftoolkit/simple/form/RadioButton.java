package org.odftoolkit.simple.form;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.dom.element.form.FormRadioElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.draw.Control;

/**
 * This class represents the form control of radio button, provides methods to
 * get/set the form properties and the style formatting of this control.
 * 
 * @since 0.8
 */
public class RadioButton extends FormControl {

	RadioButton(FormRadioElement element) {
		mElement = element;
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of radio button by an instance of FormRadioElement, while
	 * searching the document content to make a bind with the DrawControl which
	 * already reference to this radio button.
	 * 
	 * @param element
	 *            - an instance of FormRadioElement
	 * @return an instance of radio button
	 */
	public static RadioButton getInstanceOf(FormRadioElement element) {
		RadioButton radio = new RadioButton(element);
		try {
			radio.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(RadioButton.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this radio button.");
		}
		return radio;
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		((FormRadioElement) mElement)
				.setFormControlImplementationAttribute(controlImpl);
	}

	@Override
	public String getId() {
		return ((FormRadioElement) mElement).getFormIdAttribute();
	}

	@Override
	public void setId(String id) {
		((FormRadioElement) mElement).setFormIdAttribute(id);
	}

	@Override
	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null)
			mFormProperties = ((FormRadioElement) mElement)
					.newFormPropertiesElement();
		return mFormProperties;
	}

	/**
	 * Set the label content of this radio button
	 * 
	 * @param label
	 *            - the label content of this radio button
	 */
	public void setLabel(String label) {
		((FormRadioElement) mElement).setFormLabelAttribute(label);
	}

	/**
	 * Get the label content of this radio button
	 * 
	 * @return the label content of this radio button
	 */
	public String getLabel() {
		return ((FormRadioElement) mElement).getFormLabelAttribute();
	}

	@Override
	public String getName() {
		return ((FormRadioElement) mElement).getFormNameAttribute();
	}

	@Override
	public void setName(String name) {
		((FormRadioElement) mElement).setFormNameAttribute(name);
	}

	/**
	 * Get the default value of this control.
	 * 
	 * @param default value
	 */
	public String getValue() {
		return ((FormRadioElement) mElement).getFormValueAttribute();
	}

	/**
	 * Set the default value of this control, it will be override by current
	 * value.
	 * 
	 * @param value
	 *            - default value
	 */
	public void setValue(String value) {
		((FormRadioElement) mElement).setFormValueAttribute(value);
	}

	/**
	 * Set the default selection status of this radio button
	 * 
	 * @param isSelected
	 *            - true means selected; false means unselected
	 */
	public void setDefaultSelected(boolean isSelected) {
		((FormRadioElement) mElement).setFormSelectedAttribute(isSelected);
	}

	/**
	 * Get the default selection status of this radio button
	 * 
	 * @return true means selected; false means unselected
	 */
	public boolean getDefaultSelected() {
		return ((FormRadioElement) mElement).getFormSelectedAttribute();
	}

	/**
	 * Set current selection status of this radio button, it override the
	 * default status
	 * 
	 * @param isSelected
	 *            - true means selected; false means unselected
	 */
	public void setCurrentSelected(boolean isSelected) {
		((FormRadioElement) mElement)
				.setFormCurrentSelectedAttribute(isSelected);
	}

	/**
	 * Get current selection status of this radio button
	 * 
	 * @return true means selected; false means unselected
	 */
	public boolean getCurrentSelected() {
		return ((FormRadioElement) mElement).getFormCurrentSelectedAttribute();
	}

	/**
	 * Get a simple iterator for radio buttons.
	 * 
	 * @param container
	 *            - an instance of form where to traverse the radio buttons
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleRadioButtonIterator(container);
	}

	private static class SimpleRadioButtonIterator implements
			Iterator<FormControl> {

		private FormFormElement containerElement;
		private RadioButton nextElement = null;
		private RadioButton tempElement = null;

		private SimpleRadioButtonIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public RadioButton next() {
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

		private RadioButton findNext(RadioButton thisRadioButton) {
			FormRadioElement nextRadioButton = null;
			if (thisRadioButton == null) {
				nextRadioButton = OdfElement.findFirstChildNode(
						FormRadioElement.class, containerElement);
			} else {
				nextRadioButton = OdfElement
						.findNextChildNode(FormRadioElement.class,
								thisRadioButton.getOdfElement());
			}

			if (nextRadioButton != null) {
				return RadioButton.getInstanceOf(nextRadioButton);
			}
			return null;
		}
	}

}
