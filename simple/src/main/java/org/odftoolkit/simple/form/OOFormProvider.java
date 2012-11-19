package org.odftoolkit.simple.form;

import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;

/**
 * This class provide method to create/get the form instance implemented in
 * Apache Open Office way.
 * 
 * @since 0.8
 */
public class OOFormProvider implements FormProvider {

	static final String OO_CONTROL_IMPLEMENTATION_FORM = "ooo:com.sun.star.form.component.Form";
	static final String OO_CONTROL_IMPLEMENTATION_COMMANDBUTTON = "ooo:com.sun.star.form.component.CommandButton";
	static final String OO_CONTROL_IMPLEMENTATION_FIXEDTEXT = "ooo:com.sun.star.form.component.FixedText";
	static final String OO_CONTROL_IMPLEMENTATION_TEXTFEILD = "ooo:com.sun.star.form.component.TextField";
	static final String OO_CONTROL_IMPLEMENTATION_LISTBOX = "ooo:com.sun.star.form.component.ListBox";
	static final String OO_CONTROL_IMPLEMENTATION_GROUPBOX = "ooo:com.sun.star.form.component.GroupBox";
	static final String OO_CONTROL_IMPLEMENTATION_COMBOBOX = "ooo:com.sun.star.form.component.ComboBox";
	static final String OO_CONTROL_IMPLEMENTATION_RADIOBUTTON = "ooo:com.sun.star.form.component.RadioButton";
	static final String OO_CONTROL_IMPLEMENTATION_CHECKBOX = "ooo:com.sun.star.form.component.CheckBox";
	static final String OO_CONTROL_IMPLEMENTATION_DATEFIELD = "ooo:com.sun.star.form.component.DateField";
	static final String OO_CONTROL_IMPLEMENTATION_TIMEFIELD = "ooo:com.sun.star.form.component.TimeField";
	static final String OO_CONTROL_IMPLEMENTATION_NUMERICFIELD = "ooo:com.sun.star.form.component.NumericField";
	static final String OO_CONTROL_IMPLEMENTATION_PATTERNFIELD = "ooo:com.sun.star.form.component.PatternField";
	static final String OO_CONTROL_IMPLEMENTATION_CURRENCYFIELD = "ooo:com.sun.star.form.component.CurrencyField";

	static final String FORM_PROPERTY_VALUE_COMMANDBUTTON = "com.sun.star.form.control.CommandButton";
	static final String FORM_PROPERTY_VALUE_TEXTFEILD = "com.sun.star.form.control.TextField";
	static final String FORM_PROPERTY_VALUE_LISTBOX = "com.sun.star.form.control.ListBox";
	static final String FORM_PROPERTY_VALUE_COMBOBOX = "com.sun.star.form.control.ComboBox";
	static final String FORM_PROPERTY_VALUE_RADIOBUTTON = "com.sun.star.form.control.RadioButton";
	static final String FORM_PROPERTY_VALUE_CHECKBOX = "com.sun.star.form.control.CheckBox";
	static final String FORM_PROPERTY_VALUE_DATEFIELD = "com.sun.star.form.control.DateField";
	static final String FORM_PROPERTY_VALUE_TIMEFIELD = "com.sun.star.form.control.TimeField";
	static final String FORM_PROPERTY_VALUE_NUMERICFIELD = "com.sun.star.form.control.NumericField";
	static final String FORM_PROPERTY_VALUE_PATTERNFIELD = "com.sun.star.form.control.PatternField";
	static final String FORM_PROPERTY_VALUE_CURRENCYFIELD = "com.sun.star.form.control.CurrencyField";

	static final String FORM_PROPERTY_NAME_DEFAULTCONTROL = "DefaultControl";
	static final String FORM_PROPERTY_NAME_OBJIDINMSO = "ObjIDinMSO";
	static final String FORM_PROPERTY_NAME_MULTILINE = "MultiLine";
	static final String FORM_PROPERTY_NAME_COMPLEXSTRINGITEMLIST = "ComplexStringItemList";
	static final String FORM_PROPERTY_NAME_CONTROLTYPEINMSO = "ControlTypeinMSO";
	static final String FORM_PROPERTY_NAME_FOCUSEDITEM = "FocusedItem";
	static final String FORM_PROPERTY_NAME_EXTMULTISELECTION = "ExtMultiSelection";
	static final String FORM_PROPERTY_NAME_SPIN = "Spin";
	static final String FORM_PROPERTY_NAME_DROPDOWN = "Dropdown";
	public static final String FORM_PROPERTY_NAME_DECIMALACCURACY = "DecimalAccuracy";
	public static final String FORM_PROPERTY_NAME_VALUESTEP = "ValueStep";
	public static final String FORM_PROPERTY_NAME_EDITMASK = "EditMask";
	public static final String FORM_PROPERTY_NAME_LITERALMASK = "LiteralMask";
	public static final String FORM_PROPERTY_NAME_CURRENCYSYMBOL = "CurrencySymbol";
	public static final String FORM_PROPERTY_NAME_PREPENDCURRENCYSYMBOL = "PrependCurrencySymbol";

	static enum FieldType {
		DATE_FIELD, TIME_FIELD, NUMERIC_FIELD, PATTERN_FIELD, CURRENCY_FIELD;
	}

	/**
	 * Create a form in Apache Open Office way.
	 * 
	 * @see FormProvider#createForm(String, OfficeFormsElement)
	 */
	public Form createForm(String name, OfficeFormsElement parent) {

		return OOForm.createForm(name, parent);
	}

	/**
	 * Get a form instance implemented in Apache Open Office way.
	 * 
	 * @see FormProvider#getInstanceOf(FormFormElement)
	 */
	public Form getInstanceOf(FormFormElement element) {
		return OOForm.getInstance(element);
	}

}
