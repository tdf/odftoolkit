package org.odftoolkit.simple.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;

public class NumericField extends Field {

	NumericField(FormFormattedTextElement element) {
		super(element);
	}

	/**
	 * Get an instance of numeric field by an instance of
	 * FormFormattedTextElement, while searching the document content to make a
	 * bind with the DrawControl which already reference to this numeric field.
	 * 
	 * @param element
	 *            - an instance of FormFormattedTextElement
	 * @return an instance of numeric field
	 */
	public static TimeField getInstanceOf(FormFormattedTextElement element) {
		TimeField field = new TimeField(element);
		try {
			field.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(DateField.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this field.");
		}
		return field;
	}

	/**
	 * Set the decimal accuracy of the input value
	 * 
	 * @param value
	 *            - specify how many digits will be kept after the decimal point
	 */
	public void setDecimalAccuracy(double value) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_DECIMALACCURACY,
				"float", null, null, null, null, value, null);
	}

	/**
	 * Set the step value for the spin button.
	 * 
	 * @param value
	 *            - the step value for the spin button.
	 */
	public void setStepValue(double value) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_VALUESTEP,
				"float", null, null, null, null, value, null);
	}
}
