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

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.Document;

public class TimeField extends Field {

  TimeField(FormFormattedTextElement element) {
    super(element);
  }

  /**
   * Get an instance of time field by an instance of FormFormattedTextElement, while searching the
   * document content to make a bind with the DrawControl which already reference to this time
   * field.
   *
   * @param element - an instance of FormFormattedTextElement
   * @return an instance of time field
   */
  public static TimeField getInstanceOf(FormFormattedTextElement element) {
    TimeField field = new TimeField(element);
    try {
      field.loadDrawControl(
          ((Document) ((OdfFileDom) element.getOwnerDocument()).getDocument()).getContentRoot());
    } catch (Exception e) {
      Logger.getLogger(TimeField.class.getName())
          .log(Level.WARNING, "Cannot load the drawing shape of this field.");
    }
    return field;
  }

  /**
   * Format the time value according to the appointed format and locale.
   *
   * @param formatStr - format code
   * @param locale - locale information
   */
  public void setTimeFormat(String formatStr, Locale locale) {
    this.setFormatString(formatStr, Value.TIME, locale);
  }

  /**
   * Set the format of this time field. Time format pattern is the same as {@link
   * java.text.SimpleDateFormat SimpleDateFormat}.
   *
   * @param formatStr the format string of this date.
   * @see java.text.SimpleDateFormat
   */
  public void formatTime(String formatStr, Locale locale) {
    if (drawingShape == null)
      throw new IllegalStateException("Please call loadDrawControl() first.");
    DrawControlElement element = drawingShape.getOdfElement();
    String name = getUniqueTimeStyleName();
    OdfNumberTimeStyle timeStyle =
        new OdfNumberTimeStyle((OdfFileDom) element.getOwnerDocument(), formatStr, name);
    timeStyle.setNumberLanguageAttribute(locale.getLanguage());
    timeStyle.setNumberCountryAttribute(locale.getCountry());
    element.getAutomaticStyles().appendChild(timeStyle);
    OdfStyleBase styleElement = drawingShape.getStyleHandler().getStyleElementForWrite();
    if (styleElement != null) {
      styleElement.setOdfAttributeValue(
          OdfName.newName(OdfDocumentNamespace.STYLE, "data-style-name"), name);
    }
  }

  private String getUniqueTimeStyleName() {
    String unique_name;
    OdfOfficeAutomaticStyles styles = getDrawControl().getOdfElement().getAutomaticStyles();
    do {
      unique_name = String.format("t%06x", (int) (Math.random() * 0xffffff));
    } while (styles.getDateStyle(unique_name) != null);
    return unique_name;
  }

  /**
   * Get the format code which is used to format the display value
   *
   * @return the format code
   */
  public String getTimeFormat() {
    return this.getFormatString(Value.TIME);
  }
}
