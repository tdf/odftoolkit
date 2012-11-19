package org.odftoolkit.simple.form;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;

public class PatternField extends Field {

	PatternField(FormFormattedTextElement element) {
		super(element);
	}


	/**
	 * Get an instance of pattern field by an instance of FormFormattedTextElement,
	 * while searching the document content to make a bind with the DrawControl
	 * which already reference to this pattern field.
	 * 
	 * @param element
	 *            - an instance of FormFormattedTextElement
	 * @return an instance of pattern field
	 */
	public static PatternField getInstanceOf(FormFormattedTextElement element) {
		PatternField field = new PatternField(element);
		try {
			field.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(PatternField.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this field.");
		}
		return field;
	}

	public void setEditMask(String mask) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_EDITMASK,
				"string", mask, null, null, null, null, null);
	}

	public void setLiteralMask(String mask) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_LITERALMASK,
				"string", mask, null, null, null, null, null);
	}
}
