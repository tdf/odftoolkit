package org.odftoolkit.simple.form;

import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.simple.draw.ControlContainer;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.form.FormTypeDefinition.FormCommandType;

/**
 * This class represents form object. It provides method to get/set form
 * properties, content, layout and styles. A form is a container to hold
 * controls like buttons, combo boxes, labels, fields, check boxes, radio
 * buttons, text boxes, list boxes and etc.
 * 
 * @since 0.8
 */
public interface Form {

	/**
	 * Create a button control in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param label
	 *            - the text label of the button
	 * 
	 * @return an instance of button
	 */
	public FormControl createButton(ControlContainer parent,
			FrameRectangle rectangle, String name, String label);

	/**
	 * Create a label control in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param text
	 *            -default text of the label
	 * 
	 * @return an instance of label
	 */
	public FormControl createLabel(ControlContainer parent,
			FrameRectangle rectangle, String name, String text);

	/**
	 * Create a textbox in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultText
	 *            -default text of the textbox
	 * @param isMultipleLine
	 *            - if this textbox supports multiple lines input
	 * 
	 * @return an instance of text box
	 */
	public FormControl createTextBox(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultText,
			boolean isMultipleLine);

	/**
	 * Create a list box in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param isMultiSelection
	 *            - support multi-selection or not
	 * @param isDropDown
	 *            - the drop-down list is visible or not
	 * 
	 * @return an instance of list box
	 */
	public FormControl createListBox(ControlContainer parent,
			FrameRectangle rectangle, String name, boolean isMultiSelection,
			boolean isDropDown);

	/**
	 * Create a combo box in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultText
	 *            - the default text of combobox
	 * @param isDropDown
	 *            - the drop-down list is visible or not
	 * 
	 * @return an instance of combo box
	 */
	public FormControl createComboBox(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultText,
			boolean isDropDown);

	/**
	 * Create a radio button in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param label
	 *            - the label of this radio button
	 * @param value
	 *            - the value assign to this option
	 * 
	 * @return an instance of radio button
	 */
	public FormControl createRadioButton(ControlContainer parent,
			FrameRectangle rectangle, String name, String label, String value);

	/**
	 * Create a check box in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param label
	 *            - the label of this check box
	 * @param value
	 *            - the value assign to this option
	 * 
	 * @return an instance of check box
	 */
	public FormControl createCheckBox(ControlContainer parent,
			FrameRectangle rectangle, String name, String label, String value);

	/**
	 * Create a date field in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultValue
	 *            - the default value of this input field
	 * 
	 * @return an instance of date field
	 */
	public FormControl createDateField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue);

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
	 *            - the default value of this input field
	 * 
	 * @return an instance of time field
	 */
	public FormControl createTimeField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue);

	/**
	 * Create a numeric field in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultValue
	 *            - the default value of this input field
	 * 
	 * @return an instance of numeric field
	 */
	public FormControl createNumericField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue);

	/**
	 * Create a pattern field in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultValue
	 *            - the default value of this input field
	 * 
	 * @return an instance of pattern field
	 */
	public FormControl createPatternField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue);

	/**
	 * Create a currency field in this form.
	 * 
	 * @param parent
	 *            - the element that contains this form control
	 * @param rectangle
	 *            - the bounding rectangle used by this button
	 * @param name
	 *            - the name of the control
	 * @param defaultValue
	 *            - the default value of this input field
	 * 
	 * @return an instance of currency field
	 */
	public FormControl createCurrencyField(ControlContainer parent,
			FrameRectangle rectangle, String name, String defaultValue);

	/**
	 * Set the name of this form
	 * 
	 * @param name
	 *            - the form name
	 */
	public void setFormName(String name);

	/**
	 * Get the form name
	 * 
	 * @return the form name
	 */
	public String getFormName();

	/**
	 * Set the implementation of the created control
	 * 
	 * @param controlImpl
	 *            - implementation of control
	 */
	public void setControlImplementation(String controlImpl);

	/**
	 * Get the implementation of the created control
	 * 
	 * @return a control implementation
	 */
	public String getControlImplementation();

	/**
	 * Set the data source to be used by the form
	 * 
	 * @param dataSource
	 *            - name of data source
	 */
	public void setDataSource(String dataSource);

	/**
	 * Get the name of data source
	 * 
	 * @return the name of data source used by the form
	 */
	public String getDataSource();

	/**
	 * Set the type of command to execute on a data source.
	 * 
	 * @param commandType
	 *            the command type
	 */
	public void setCommandType(FormCommandType commandType);

	/**
	 * Get the type of command to execute on a data source
	 * 
	 * @return the command type
	 */
	public FormCommandType getCommandType();

	/**
	 * Set a command to execute on a data source
	 * 
	 * @param command
	 * 
	 */
	public void setCommand(String command);

	/**
	 * Get the command to execute on a data source
	 * 
	 * @return the command
	 */
	public String getCommand();

	/**
	 * Get the instance of <code>FormFormElemnt</code> element.
	 * 
	 * @return the instance of <code>FormFormElemnt</code>
	 */
	public FormFormElement getOdfElement();

}
