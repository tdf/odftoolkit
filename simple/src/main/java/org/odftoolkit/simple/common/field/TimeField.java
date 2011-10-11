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
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTimeElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;

/**
 * TimeField displays a time, by default this is the current time.
 * 
 * @since 0.5
 */
public class TimeField extends Field {

	/**
	 * The default time format of time field.
	 */
	private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

	/**
	 * The default time value format.
	 */
	private static final String DEFAULT_TIME_VALUE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S";

	private TextTimeElement timeElement;

	// package constructor, only called by Fields.
	TimeField(OdfElement odfElement) {
		TextSpanElement spanElement = ((OdfFileDom) odfElement.getOwnerDocument()).newOdfElement(TextSpanElement.class);
		odfElement.appendChild(spanElement);
		timeElement = spanElement.newTextTimeElement();
		OdfNumberTimeStyle timeStyle = newTimeStyle();
		String timeStyleName = timeStyle.getStyleNameAttribute();
		timeStyle.buildFromFormat(DEFAULT_TIME_FORMAT);
		timeStyle.setStyleNameAttribute(timeStyleName);
		timeElement.setStyleDataStyleNameAttribute(timeStyle.getStyleNameAttribute());
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_TIME_VALUE_FORMAT);
		Date time = calender.getTime();
		String svalue = simpleFormat.format(time);
		timeElement.setTextTimeValueAttribute(svalue);
		SimpleDateFormat contentFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
		timeElement.setTextContent(contentFormat.format(time));
		timeElement.setTextFixedAttribute(true);
		Component.registerComponent(this, getOdfElement());
	}

	/**
	 * Set the format of this time field. Time format pattern is the same as
	 * {@link java.text.SimpleDateFormat SimpleDateFormat}.
	 * 
	 * @param formatString
	 *            the format string of this time.
	 * @see java.text.SimpleDateFormat
	 */
	public void formatTime(String formatString) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_TIME_VALUE_FORMAT);
		String sValue = timeElement.getTextTimeValueAttribute();
		try {
			Date simpleDate = simpleFormat.parse(sValue);
			SimpleDateFormat newFormat = new SimpleDateFormat(formatString);
			timeElement.setTextContent(newFormat.format(simpleDate));
			OdfFileDom dom = (OdfFileDom) timeElement.getOwnerDocument();
			OdfOfficeAutomaticStyles styles = null;
			if (dom instanceof OdfContentDom) {
				styles = ((OdfContentDom) dom).getAutomaticStyles();
			} else if (dom instanceof OdfStylesDom) {
				styles = ((OdfStylesDom) dom).getAutomaticStyles();
			}
			OdfNumberTimeStyle dataStyle = styles.getTimeStyle(timeElement.getStyleDataStyleNameAttribute());
			dataStyle.buildFromFormat(formatString);
		} catch (ParseException e) {
			Logger.getLogger(TimeField.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Set whether the time value of this field is fixed.
	 * 
	 * @param isFixed
	 *            if <code>true</code>, the time value shall be preserved,
	 *            otherwise it may be replaced with a new value in future edits.
	 */
	public void setFixed(boolean isFixed) {
		timeElement.setTextFixedAttribute(isFixed);
	}

	/**
	 * Return an instance of <code>TextTimeElement</code> which represents this
	 * feature.
	 * 
	 * @return an instance of <code>TextTimeElement</code>
	 */
	public TextTimeElement getOdfElement() {
		return timeElement;
	}

	// Create an <code>OdfNumberTimeStyle</code> element
	private OdfNumberTimeStyle newTimeStyle() {
		OdfFileDom dom = (OdfFileDom) timeElement.getOwnerDocument();
		OdfOfficeAutomaticStyles styles = null;
		if (dom instanceof OdfContentDom) {
			styles = ((OdfContentDom) dom).getAutomaticStyles();
		} else if (dom instanceof OdfStylesDom) {
			styles = ((OdfStylesDom) dom).getAutomaticStyles();
		}
		OdfNumberTimeStyle newStyle = dom.newOdfElement(OdfNumberTimeStyle.class);
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
		return FieldType.TIME_FIELD;
	}
}
