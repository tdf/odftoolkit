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

import org.odftoolkit.odfdom.dom.element.form.FormButtonElement;
import org.odftoolkit.odfdom.dom.element.form.FormCheckboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormComboboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormFixedTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormFrameElement;
import org.odftoolkit.odfdom.dom.element.form.FormListboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormRadioElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.draw.ControlContainer;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.form.FormTypeDefinition.FormCommandType;
import org.odftoolkit.simple.form.FormTypeDefinition.FormImageLocation;
import org.odftoolkit.simple.form.OOFormProvider.FieldType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;

/**
 * This class implements the interface of Form according to the implementation
 * of OpenOffice.org.
 * 
 * @since 0.8
 * 
 */
public class OOForm extends Component implements Form {

	private FormFormElement mElement;
	private Document mOwnerDocument;
	protected OfficeFormsElement mFormContainerElement;
	private int controlCount;

	private OOForm(FormFormElement element) {
		mElement = element;
		mOwnerDocument = (Document) ((OdfFileDom) mElement.getOwnerDocument())
				.getDocument();
		mFormContainerElement = (OfficeFormsElement) mElement.getParentNode();
	}

	static OOForm createForm(String name, OfficeFormsElement parent) {
		OOForm form = null;
		if (parent != null) {
			FormFormElement element = parent.newFormFormElement();
			form = new OOForm(element);
			form.setFormName(name);
			form
					.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_FORM);
			Component.registerComponent(form, element);
		}
		return form;
	}

	public Document getOwnerDocument() {
		return mOwnerDocument;
	}

	/**
	 * Get a form instance by an instance of <code>FormFormElement</code>.
	 * 
	 * @param element
	 * @return
	 */
	public static Form getInstance(FormFormElement element) {
		OOForm form = null;
		form = (OOForm) Component.getComponentByElement(element);
		if (form == null) {
			form = new OOForm(element);
			Component.registerComponent(form, element);
		}
		return form;
	}


	public Button createButton(ControlContainer parent,
			FrameRectangle rectangle, String name, String label) {
		FormButtonElement btnElement = mElement.newFormButtonElement(
				FormImageLocation.CENTER.toString(), generateFormId());
		// set default control properties
		Button btnForm = new Button(btnElement);
		Component.registerComponent(btnForm, btnElement);
		btnForm.setId(btnElement.getXmlIdAttribute());
		btnForm.setName(name);
		btnForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_COMMANDBUTTON);
		btnForm.setLabel(label);
		btnForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				OOFormProvider.FORM_PROPERTY_VALUE_COMMANDBUTTON, null, null,
				null, null, null);
		// bond to drawing shape
		btnForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			btnForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			btnForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		btnForm.setRectangle(rectangle);
		return btnForm;
	}


	public FormControl createLabel(ControlContainer parent,
			FrameRectangle rectangle, String name, String text) {
		FormFixedTextElement fixedTElement = mElement
				.newFormFixedTextElement(generateFormId());
		// set default control properties
		Label labelForm = new Label(fixedTElement);
		Component.registerComponent(labelForm, fixedTElement);
		labelForm.setId(fixedTElement.getXmlIdAttribute());
		labelForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_FIXEDTEXT);
		labelForm.setName(name);
		labelForm.setLabel(text);
		// bond to drawing shape
		labelForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			labelForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			labelForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		labelForm.setRectangle(rectangle);
		return labelForm;
	}


	public FormControl createTextBox(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultText,
			boolean isMultipleLine) {
		OdfElement textBoxElement;
		String formId = generateFormId();
		if (isMultipleLine) {
			textBoxElement = mElement.newFormTextareaElement(formId);
		} else {
			textBoxElement = mElement.newFormTextElement(formId);
		}
		// set default control properties
		TextBox textBoxForm = new TextBox(textBoxElement);
		Component.registerComponent(textBoxForm, textBoxElement);
		textBoxForm.setId(formId);
		textBoxForm.setName(name);
		textBoxForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_TEXTFEILD);
		textBoxForm.setValue(defaultText);
		textBoxForm.setCurrentValue(defaultText);
		textBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				OOFormProvider.FORM_PROPERTY_VALUE_TEXTFEILD, null, null, null,
				null, null);
		if (isMultipleLine)
			textBoxForm.setFormProperty(
					OOFormProvider.FORM_PROPERTY_NAME_MULTILINE, "boolean",
					null, true, null, null, null, null);
		textBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_OBJIDINMSO, "float", null,
				null, null, null, 0.0, null);

		// bond to drawing shape
		textBoxForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			textBoxForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			textBoxForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		textBoxForm.setRectangle(rectangle);
		return textBoxForm;
	}


	public FormControl createListBox(ControlContainer parent,
			FrameRectangle rectangle, String name, boolean isMultiSelection,
			boolean isDropDown) {
		String formId = generateFormId();
		FormListboxElement listBoxElement = mElement
				.newFormListboxElement(generateFormId());
		// set default control properties
		ListBox listBoxForm = new ListBox(listBoxElement);
		Component.registerComponent(listBoxForm, listBoxElement);
		listBoxForm.setId(formId);
		listBoxForm.setName(name);
		listBoxForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_LISTBOX);
		listBoxForm.setFormMultiSelection(isMultiSelection);
		if (isDropDown) {
			listBoxForm.setFormDropdown(true);
		}
		listBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_COMPLEXSTRINGITEMLIST,
				"float", null, null, null, null, null, null);
		listBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				OOFormProvider.FORM_PROPERTY_VALUE_LISTBOX, null, null, null,
				null, null);
		listBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_CONTROLTYPEINMSO, "float",
				null, null, null, null, 0.0, null);
		listBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_FOCUSEDITEM, "float", null,
				null, null, null, 0.0, null);
		listBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_EXTMULTISELECTION, "float",
				null, null, null, null, 0.0, null);
		listBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_OBJIDINMSO, "float", null,
				null, null, null, 0.0, null);
		// bond to drawing shape
		listBoxForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			listBoxForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			listBoxForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		listBoxForm.setRectangle(rectangle);
		return listBoxForm;
	}


	public FormControl createComboBox(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultText,
			boolean isDropDown) {
		String formId = generateFormId();
		FormComboboxElement comboElement = mElement
				.newFormComboboxElement(formId);
		// set default control properties
		ComboBox comboBoxForm = new ComboBox(comboElement);
		Component.registerComponent(comboBoxForm, comboElement);
		comboBoxForm.setId(formId);
		comboBoxForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_COMBOBOX);
		comboBoxForm.setName(name);
		if (defaultText != null) {
			comboBoxForm.setCurrentValue(defaultText);
			comboBoxForm.setValue(defaultText);
		}
		if (isDropDown) {
			comboBoxForm.setFormDropdown(true);
		}
		comboBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_COMPLEXSTRINGITEMLIST,
				"float", null, null, null, null, null, null);
		comboBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				OOFormProvider.FORM_PROPERTY_VALUE_COMBOBOX, null, null, null,
				null, null);
		comboBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_CONTROLTYPEINMSO, "float",
				null, null, null, null, 0.0, null);
		comboBoxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_OBJIDINMSO, "float", null,
				null, null, null, 0.0, null);
		// bond to drawing shape
		comboBoxForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			comboBoxForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			comboBoxForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		comboBoxForm.setRectangle(rectangle);
		return comboBoxForm;
	}


	public FormControl createRadioButton(ControlContainer parent,
			FrameRectangle rectangle, String name, String label, String value) {
		String formId = generateFormId();
		FormRadioElement radioElement = mElement.newFormRadioElement("center",
				formId);
		// set default control properties
		RadioButton radioBtnForm = new RadioButton(radioElement);
		Component.registerComponent(radioBtnForm, radioElement);
		radioBtnForm.setId(formId);
		radioBtnForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_RADIOBUTTON);
		radioBtnForm.setLabel(label);
		radioBtnForm.setName(name);
		radioBtnForm.setValue(value);
		radioBtnForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_CONTROLTYPEINMSO, "float",
				null, null, null, null, 0.0, null);
		radioBtnForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				OOFormProvider.FORM_PROPERTY_VALUE_RADIOBUTTON, null, null,
				null, null, null);
		// bond to drawing shape
		radioBtnForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			radioBtnForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			radioBtnForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		radioBtnForm.setRectangle(rectangle);
		return radioBtnForm;
	}


	public FormControl createCheckBox(ControlContainer parent,
			FrameRectangle rectangle, String name, String label, String value) {
		String formId = generateFormId();
		FormCheckboxElement checkboxElement = mElement.newFormCheckboxElement(
				"center", formId);
		// set default control properties
		CheckBox checkboxForm = new CheckBox(checkboxElement);
		Component.registerComponent(checkboxForm, checkboxElement);
		checkboxForm.setId(formId);
		checkboxForm.setName(name);
		checkboxForm
				.setControlImplementation(OOFormProvider.OO_CONTROL_IMPLEMENTATION_CHECKBOX);
		checkboxForm.setLabel(label);
		checkboxForm.setValue(value);
		checkboxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_CONTROLTYPEINMSO, "float",
				null, null, null, null, 0.0, null);
		checkboxForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				OOFormProvider.FORM_PROPERTY_VALUE_CHECKBOX, null, null, null,
				null, null);
		// bond to drawing shape
		checkboxForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			checkboxForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			checkboxForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		checkboxForm.setRectangle(rectangle);
		return checkboxForm;
	}

	/**
	 * Create a time field in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultValue
	 *            - the default value of this input field. It's a 6 digits
	 *            number, e.g. 20121015 represents 2012-10-15.
	 * 
	 * @return an instance of time field
	 */
	public FormControl createDateField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue) {
		return createField(FieldType.DATE_FIELD, parent, rectangle, name,
				defaultValue);
	}

	/**
	 * Create a time field in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultValue
	 *            - the default value of this input field. It's a 6 digits
	 *            number, e.g. 15304000 represents 15:30:40.
	 * 
	 * @return an instance of time field
	 */
	public FormControl createTimeField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue) {
		return createField(FieldType.TIME_FIELD, parent, rectangle, name,
				defaultValue);
	}


	public FormControl createNumericField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue) {
		return createField(FieldType.NUMERIC_FIELD, parent, rectangle, name,
				defaultValue);
	}


	public FormControl createPatternField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue) {
		return createField(FieldType.PATTERN_FIELD, parent, rectangle, name,
				defaultValue);
	}


	public FormControl createCurrencyField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue) {
		return createField(FieldType.CURRENCY_FIELD, parent, rectangle, name,
				defaultValue);
	}

	private FormControl createField(FieldType type, ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue) {
		String formId = generateFormId();
		FormFormattedTextElement formattedTextElement = mElement
				.newFormFormattedTextElement(formId);
		// create control according to the field type
		Field fieldForm = null;
		String defaultControl = null;
		String controlImpl = null;
		switch (type) {
		case DATE_FIELD:
			defaultControl = OOFormProvider.FORM_PROPERTY_VALUE_DATEFIELD;
			controlImpl = OOFormProvider.OO_CONTROL_IMPLEMENTATION_DATEFIELD;
			fieldForm = new DateField(formattedTextElement);
			break;
		case TIME_FIELD:
			defaultControl = OOFormProvider.FORM_PROPERTY_VALUE_TIMEFIELD;
			controlImpl = OOFormProvider.OO_CONTROL_IMPLEMENTATION_TIMEFIELD;
			fieldForm = new TimeField(formattedTextElement);
			break;
		case NUMERIC_FIELD:
			defaultControl = OOFormProvider.FORM_PROPERTY_VALUE_NUMERICFIELD;
			controlImpl = OOFormProvider.OO_CONTROL_IMPLEMENTATION_NUMERICFIELD;
			fieldForm = new NumericField(formattedTextElement);
			break;
		case PATTERN_FIELD:
			defaultControl = OOFormProvider.FORM_PROPERTY_VALUE_PATTERNFIELD;
			controlImpl = OOFormProvider.OO_CONTROL_IMPLEMENTATION_PATTERNFIELD;
			fieldForm = new PatternField(formattedTextElement);
			break;
		case CURRENCY_FIELD:
			defaultControl = OOFormProvider.FORM_PROPERTY_VALUE_CURRENCYFIELD;
			controlImpl = OOFormProvider.OO_CONTROL_IMPLEMENTATION_CURRENCYFIELD;
			fieldForm = new CurrencyField(formattedTextElement);
			break;
		}
		if (fieldForm == null)
			throw new RuntimeException("Fail to create a field.");
		Component.registerComponent(fieldForm, formattedTextElement);
		// set control properties
		fieldForm.setName(name);
		fieldForm.setId(formId);
		fieldForm.setControlImplementation(controlImpl);
		fieldForm.setValue(defaultValue);
		fieldForm.setCurrentValue(defaultValue);
		fieldForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_CONTROLTYPEINMSO, "float",
				null, null, null, null, 0.0, null);
		fieldForm.setFormProperty(
				OOFormProvider.FORM_PROPERTY_NAME_DEFAULTCONTROL, "string",
				defaultControl, null, null, null, null, null);
		// bond to drawing shape
		fieldForm.createDrawControl(parent);
		// set default shape properties
		if (parent instanceof TextDocument) {
			fieldForm.setAnchorType(AnchorType.TO_PAGE);
		} else {
			fieldForm.setAnchorType(AnchorType.TO_PARAGRAPH);
		}
		fieldForm.setRectangle(rectangle);
		return fieldForm;

	}

	private String generateFormId() {
		return getFormName() + (++controlCount);
	}


	public void setControlImplementation(String controlImpl) {
		mElement.setFormControlImplementationAttribute(controlImpl);
	}


	public void setFormName(String name) {
		mElement.setFormNameAttribute(name);
	}


	public void setCommand(String command) {
		mElement.setFormCommandAttribute(command);
	}


	public void setCommandType(FormCommandType commandType) {
		mElement.setFormCommandTypeAttribute(commandType.toString());
	}


	public void setDataSource(String dataSource) {
		mElement.setFormDatasourceAttribute(dataSource);
	}


	public FormFormElement getOdfElement() {
		return mElement;
	}


	public String getFormName() {
		return mElement.getFormNameAttribute();
	}


	public String getCommand() {
		return mElement.getFormCommandAttribute();
	}


	public FormCommandType getCommandType() {
		return FormCommandType.enumValueOf(mElement
				.getFormCommandTypeAttribute());
	}


	public String getControlImplementation() {
		return mElement.getFormControlImplementationAttribute();
	}


	public String getDataSource() {
		return mElement.getFormDatasourceAttribute();
	}

}
