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

package org.odftoolkit.simple.common.field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.text.TextDateElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;

/**
 * DateField displays a date, by default this is the current date.
 * 
 * @since 0.5
 */
public class DateField extends Field {

	/**
	 * The default date format of date field.
	 */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	/**
	 * The default date value format.
	 */
	private static final String DEFAULT_DATE_VALUE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S";

	private TextDateElement dateElement;

	// package constructor, only called by Fields.
	DateField(OdfElement odfElement) {
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		dateElement = spanElement.newTextDateElement();
		OdfNumberDateStyle dateStyle = newDateStyle();
		String dateStyleName = dateStyle.getStyleNameAttribute();
		dateStyle.buildFromFormat(DEFAULT_DATE_FORMAT);
		dateStyle.setStyleNameAttribute(dateStyleName);
		dateElement.setStyleDataStyleNameAttribute(dateStyle.getStyleNameAttribute());
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_DATE_VALUE_FORMAT);
		Date date = calender.getTime();
		String svalue = simpleFormat.format(date);
		dateElement.setTextDateValueAttribute(svalue);
		SimpleDateFormat contentFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		dateElement.setTextContent(contentFormat.format(date));
		dateElement.setTextFixedAttribute(true);
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Set the format of this date field. Date format pattern is the same as
	 * {@link java.text.SimpleDateFormat SimpleDateFormat}.
	 * 
	 * @param formatString
	 *            the format string of this date.
	 * @see java.text.SimpleDateFormat
	 */
	public void formatDate(String formatString) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_DATE_VALUE_FORMAT);
		String sValue = dateElement.getTextDateValueAttribute();
		try {
			Date simpleDate = simpleFormat.parse(sValue);
			SimpleDateFormat newFormat = new SimpleDateFormat(formatString);
			dateElement.setTextContent(newFormat.format(simpleDate));
			OdfFileDom dom = (OdfFileDom) dateElement.getOwnerDocument();
			OdfOfficeAutomaticStyles styles = null;
			if (dom instanceof OdfContentDom) {
				styles = ((OdfContentDom) dom).getAutomaticStyles();
			} else if (dom instanceof OdfStylesDom) {
				styles = ((OdfStylesDom) dom).getAutomaticStyles();
			}
			OdfNumberDateStyle dataStyle = styles.getDateStyle(dateElement.getStyleDataStyleNameAttribute());
			dataStyle.buildFromFormat(formatString);
		} catch (ParseException e) {
			Logger.getLogger(DateField.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Set whether the date value of this field is fixed.
	 * 
	 * @param isFixed
	 *            if <code>true</code>, the date value shall be preserved,
	 *            otherwise it may be replaced with a new value in future edits.
	 */
	public void setFixed(boolean isFixed) {
		dateElement.setTextFixedAttribute(isFixed);
	}

	/**
	 * Return an instance of <code>TextDateElement</code> which represents this
	 * feature.
	 * 
	 * @return an instance of <code>TextDateElement</code>
	 */
	public TextDateElement getOdfElement() {
		return dateElement;
	}

	// Create an <code>OdfNumberDateStyle</code> element
	private OdfNumberDateStyle newDateStyle() {
		OdfFileDom dom = (OdfFileDom) dateElement.getOwnerDocument();
		OdfOfficeAutomaticStyles styles = null;
		if (dom instanceof OdfContentDom) {
			styles = ((OdfContentDom) dom).getAutomaticStyles();
		} else if (dom instanceof OdfStylesDom) {
			styles = ((OdfStylesDom) dom).getAutomaticStyles();
		}
		OdfNumberDateStyle newStyle = dom.newOdfElement(OdfNumberDateStyle.class);
		newStyle.setStyleNameAttribute(newUniqueStyleName(styles));
		styles.appendChild(newStyle);
		return newStyle;
	}

	private String newUniqueStyleName(OdfOfficeAutomaticStyles styles) {
		String unique_name;
		do {
			unique_name = String.format("N%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getTimeStyle(unique_name) != null);
		return unique_name;
	}

	@Override
	public FieldType getFieldType() {
		return dateElement.getTextFixedAttribute() ? FieldType.FIXED_DATE_FIELD : FieldType.DATE_FIELD;
	}
}
