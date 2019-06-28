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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.Document;

/**
 * This class represents the form control of Field, provides methods to get/set
 * the form properties and the style formatting of this control.
 *
 * @since 0.8
 */
public class Field extends FormControl {

	Field(FormFormattedTextElement element) {
		mElement = element;
		formElement = (FormFormElement) element.getParentNode();
	}

	/**
	 * Get an instance of field by an instance of FormFormattedTextElement,
	 * while searching the document content to make a bind with the DrawControl
	 * which already reference to this field.
	 *
	 * @param element
	 *            - an instance of FormFormattedTextElement
	 * @return an instance of field
	 */
	public static Field getInstanceOf(FormFormattedTextElement element) {
		Field field = null;
		if (element.getFormControlImplementationAttribute().equals(
				OOFormProvider.OO_CONTROL_IMPLEMENTATION_DATEFIELD)) {
			return DateField.getInstanceOf(element);
		} else if (element.getFormControlImplementationAttribute().equals(
				OOFormProvider.OO_CONTROL_IMPLEMENTATION_TIMEFIELD)) {
			return TimeField.getInstanceOf(element);
		} else if (element.getFormControlImplementationAttribute().equals(
				OOFormProvider.OO_CONTROL_IMPLEMENTATION_NUMERICFIELD)) {
			return NumericField.getInstanceOf(element);
		} else if (element.getFormControlImplementationAttribute().equals(
				OOFormProvider.OO_CONTROL_IMPLEMENTATION_PATTERNFIELD)) {
			return PatternField.getInstanceOf(element);
		} else if (element.getFormControlImplementationAttribute().equals(
				OOFormProvider.OO_CONTROL_IMPLEMENTATION_CURRENCYFIELD)) {
			return CurrencyField.getInstanceOf(element);
		} else {
			field = new Field(element);
		}
		if (field == null)
			throw new IllegalStateException(
					"Failed to load and initiate a field.");

		try {
			field.loadDrawControl(((Document) ((OdfFileDom) element
					.getOwnerDocument()).getDocument()).getContentRoot());
		} catch (Exception e) {
			Logger.getLogger(Field.class.getName()).log(Level.WARNING,
					"Cannot load the drawing shape of this field.");
		}
		return field;
	}

	@Override
	public String getId() {
		return ((FormFormattedTextElement) mElement).getFormIdAttribute();
	}

	@Override
	public void setControlImplementation(String controlImpl) {
		((FormFormattedTextElement) mElement)
				.setFormControlImplementationAttribute(controlImpl);
	}

	@Override
	public void setId(String id) {
		((FormFormattedTextElement) mElement).setFormIdAttribute(id);
	}

	FormPropertiesElement getFormPropertiesElementForWrite() {
		if (mFormProperties == null)
			mFormProperties = ((FormFormattedTextElement) mElement)
					.newFormPropertiesElement();
		return mFormProperties;
	}

	@Override
	public String getName() {
		return ((FormFormattedTextElement) mElement).getFormNameAttribute();
	}

	@Override
	public void setName(String name) {
		((FormFormattedTextElement) mElement).setFormNameAttribute(name);
	}

	/**
	 * Set the default value of this control, it will be override by current
	 * value.
	 *
	 * @param value
	 *            - default value
	 */
	public void setValue(String value) {
		((FormFormattedTextElement) mElement).setFormValueAttribute(value);

	}

	/**
	 * Get the default value of this control
	 *
	 * @return default value
	 */
	public String getValue() {
		return ((FormFormattedTextElement) mElement).getFormValueAttribute();

	}

	/**
	 * Set the current value of this control, it override the default value.
	 *
	 * @param currentValue
	 *            - current value
	 */
	public void setCurrentValue(String value) {
		((FormFormattedTextElement) mElement)
				.setFormCurrentValueAttribute(value);
	}

	/**
	 * Get the current value of this control
	 *
	 * @return current value
	 */
	public String getCurrentValue() {
		return ((FormFormattedTextElement) mElement)
				.getFormCurrentValueAttribute();
	}

	/**
	 * Set the visibility of the spin button
	 *
	 * @param isVisible
	 *            - true means the spin button is visible; false means the spin
	 *            button is hidden
	 */
	public void setSpinButonVisible(boolean isVisible) {
		this.setFormProperty(OOFormProvider.FORM_PROPERTY_NAME_SPIN, "boolean",
				null, isVisible, null, null, null, null);
	}

/**
	 * Set the format string of the field.
	 * <p>
	 * This function only works for float, date, time and percentage, otherwise an
	 * {@link java.lang.IllegalArgumentException} will be thrown.
	 * <p>
	 * For value type float and percentage, the <code>formatStr</code> must follow the encoding
	 * rule of {@link java.text.DecimalFormat <code>java.text.DecimalFormat</code>}.
	 * For value type date and time, the <code>formatStr</code> must follow the encoding
	 * rule of {@link java.text.SimpleDateFormat <code>java.text.SimpleDateFormat</code>}.
	 * <p>
	 * <blockquote>
	 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart showing ValueType, Distinguish Symbol
	 * and Distinguish Priority.">
	 *     <tr bgcolor="#ccccff">
	 *          <th align=left>ValueType
	 *          <th align=left>Distinguish Symbol
	 *          <th align=left>Distinguish Priority
	 *     <tr valign=top>
	 *          <td>percentage
	 *          <td>%
	 *          <td>1
	 *     <tr valign=top>
	 *          <td>time
	 *          <td>H, k, m, s, S
	 *          <td>2
	 *     <tr valign=top>
	 *          <td>date
	 *          <td>y, M, w, W, D, d, F, E, K, h
	 *          <td>3
	 *     <tr valign=top>
	 *          <td>float
	 *          <td>#, 0
	 *          <td>4
	 * </table>
	 * </blockquote>
	 * If adaptive failed, an {@link java.lang.UnsupportedOperationException} will be thrown.
	 * <p>
	 * @param formatStr	the cell need be formatted as this specified format string.
	 * @throws IllegalArgumentException if <code>formatStr</code> is null or the cell value type is supported.
	 * @see java.text.SimpleDateFormat
	 * @see java.text.DecimalFormat
	 */
	protected void setFormatString(String formatStr,
			OfficeValueTypeAttribute.Value type, Locale locale) {
		if (locale == null) {
			locale = Locale.US;
		}
		if (formatStr == null)
			throw new IllegalArgumentException("formatStr cannot be null.");

		if (type == OfficeValueTypeAttribute.Value.FLOAT) {
			OdfNumberStyle numberStyle = new OdfNumberStyle(
					(OdfFileDom) mElement.getOwnerDocument(), formatStr,
					getUniqueNumberStyleName());
			numberStyle.setNumberLanguageAttribute(locale.getLanguage());
			numberStyle.setNumberCountryAttribute(locale.getCountry());
			drawingShape.getOdfElement().getAutomaticStyles().appendChild(
					numberStyle);
			setDataDisplayStyleName(numberStyle.getStyleNameAttribute());
		} else if (type == OfficeValueTypeAttribute.Value.DATE) {
			OdfNumberDateStyle dateStyle = new OdfNumberDateStyle(
					(OdfFileDom) mElement.getOwnerDocument(), formatStr,
					getUniqueDateStyleName(), null);
			dateStyle.setNumberLanguageAttribute(locale.getLanguage());
			dateStyle.setNumberCountryAttribute(locale.getCountry());
			drawingShape.getOdfElement().getAutomaticStyles().appendChild(
					dateStyle);
			setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
		} else if (type == OfficeValueTypeAttribute.Value.TIME) {
			OdfNumberTimeStyle timeStyle = new OdfNumberTimeStyle(
					(OdfFileDom) mElement.getOwnerDocument(), formatStr,
					getUniqueDateStyleName());
			timeStyle.setNumberLanguageAttribute(locale.getLanguage());
			timeStyle.setNumberCountryAttribute(locale.getCountry());
			drawingShape.getOdfElement().getAutomaticStyles().appendChild(
					timeStyle);
			setDataDisplayStyleName(timeStyle.getStyleNameAttribute());
		} else if (type == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			OdfNumberPercentageStyle dateStyle = new OdfNumberPercentageStyle(
					(OdfFileDom) mElement.getOwnerDocument(), formatStr,
					getUniquePercentageStyleName());
			dateStyle.setNumberLanguageAttribute(locale.getLanguage());
			dateStyle.setNumberCountryAttribute(locale.getCountry());
			drawingShape.getOdfElement().getAutomaticStyles().appendChild(
					dateStyle);
			setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
		} else {
			throw new IllegalArgumentException("This function doesn't support "
					+ type + " fomat.");
		}
	}

	/**
	 * Get the format string of the field.
	 *
	 * @return the format string of the field
	 */
	public String getFormatString(OfficeValueTypeAttribute.Value typeValue) {
		Document mDocument = ((Document) ((OdfFileDom) mElement
				.getOwnerDocument()).getDocument());

		if (typeValue == OfficeValueTypeAttribute.Value.FLOAT) {
			String name = getDataDisplayStyleName();
			OdfNumberStyle style = drawingShape.getOdfElement()
					.getAutomaticStyles().getNumberStyle(name);
			if (style == null) {
				style = ((OdfSchemaDocument) mDocument).getDocumentStyles()
						.getNumberStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.DATE) {
			String name = getDataDisplayStyleName();
			OdfNumberDateStyle style = drawingShape.getOdfElement()
					.getAutomaticStyles().getDateStyle(name);
			if (style == null) {
				style = ((OdfSchemaDocument) mDocument).getDocumentStyles()
						.getDateStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.TIME) {
			String name = getDataDisplayStyleName();
			OdfNumberDateStyle style = drawingShape.getOdfElement()
					.getAutomaticStyles().getDateStyle(name);
			if (style == null) {
				style = ((OdfSchemaDocument) mDocument).getDocumentStyles()
						.getDateStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			String name = getDataDisplayStyleName();
			OdfNumberPercentageStyle style = drawingShape.getOdfElement()
					.getAutomaticStyles().getPercentageStyle(name);
			if (style == null) {
				style = ((OdfSchemaDocument) mDocument).getDocumentStyles()
						.getPercentageStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		}
		return null;
	}

	private void setDataDisplayStyleName(String name) {
		OdfStyleBase styleElement = drawingShape.getStyleHandler()
				.getStyleElementForWrite();
		if (styleElement != null) {
			styleElement.setOdfAttributeValue(OdfName.newName(
					OdfDocumentNamespace.STYLE, "data-style-name"), name);
		}
	}

	private String getDataDisplayStyleName() {
		String datadisplayStylename = null;
		OdfStyleBase styleElement = drawingShape.getStyleHandler()
				.getStyleElementForRead();
		if (styleElement != null) {
			datadisplayStylename = styleElement.getOdfAttributeValue(OdfName
					.newName(OdfDocumentNamespace.STYLE, "data-style-name"));
		}

		return datadisplayStylename;
	}

	private String getUniqueNumberStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = drawingShape.getOdfElement()
				.getAutomaticStyles();
		do {
			unique_name = String.format("n%06x",
					(int) (Math.random() * 0xffffff));
		} while (styles.getNumberStyle(unique_name) != null);
		return unique_name;
	}

	private String getUniqueDateStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = drawingShape.getOdfElement()
				.getAutomaticStyles();
		do {
			unique_name = String.format("d%06x",
					(int) (Math.random() * 0xffffff));
		} while (styles.getDateStyle(unique_name) != null);
		return unique_name;
	}

	private String getUniquePercentageStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = drawingShape.getOdfElement()
				.getAutomaticStyles();
		do {
			unique_name = String.format("p%06x",
					(int) (Math.random() * 0xffffff));
		} while (styles.getPercentageStyle(unique_name) != null);
		return unique_name;
	}

	/**
	 * Get a simple iterator for Field.
	 *
	 * @param container
	 *            - an instance of form where to traverse the date fields
	 */
	public static Iterator<FormControl> getSimpleIterator(Form container) {
		return new SimpleFieldIterator(container);
	}

	private static class SimpleFieldIterator implements Iterator<FormControl> {

		private FormFormElement containerElement;
		private Field nextElement = null;
		private Field tempElement = null;

		private SimpleFieldIterator(Form container) {
			containerElement = container.getOdfElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public Field next() {
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

		private Field findNext(Field thisField) {
			FormFormattedTextElement nextfield = null;
			if (thisField == null) {
				nextfield = OdfElement.findFirstChildNode(
						FormFormattedTextElement.class, containerElement);
			} else {
				nextfield = OdfElement.findNextChildNode(
						FormFormattedTextElement.class, thisField
								.getOdfElement());
			}
			if (nextfield != null) {
				return Field.getInstanceOf(nextfield);
			}
			return null;
		}
	}

}
