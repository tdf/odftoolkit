package org.odftoolkit.simple.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;

public class CurrencyField extends Field {

	CurrencyField(FormFormattedTextElement element) {
		super(element);
	}


	/**
	 * Get an instance of currency field by an instance of FormFormattedTextElement,
	 * while searching the document content to make a bind with the DrawControl
	 * which already reference to this currency field.
	 * 
	 * @param element
	 *            - an instance of FormFormattedTextElement
	 * @return an instance of currency field
	 */
	public static CurrencyField getInstanceOf(FormFormattedTextElement element) {
		CurrencyField field = new CurrencyField(element);
		try {
			field.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(CurrencyField.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this field.");
		}
		return field;
	}
	
	public void setCurrencySymbol(String symbol) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_CURRENCYSYMBOL,
				"string", symbol, null, null, null, null, null);
	}

	public void setDecimalAccuracy(double value) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_DECIMALACCURACY,
				"float", null, null, null, null, value, null);
	}

	public void setStepValue(double value) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_VALUESTEP,
				"float", null, null, null, null, value, null);
	}

	public void setCurrencySymbolVisible(boolean visible) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_PREPENDCURRENCYSYMBOL,
				"boolean", null, visible, null, null, null, null);

	}
}
