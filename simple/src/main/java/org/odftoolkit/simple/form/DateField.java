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
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;

/** This class represents a date field of form. */
public class DateField extends Field {

  DateField(FormFormattedTextElement element) {
    super(element);
  }

  /**
   * Get an instance of date field by an instance of FormFormattedTextElement, while searching the
   * document content to make a bind with the DrawControl which already reference to this date
   * field.
   *
   * @param element - an instance of FormFormattedTextElement
   * @return an instance of date field
   */
  public static DateField getInstanceOf(FormFormattedTextElement element) {
    DateField field = new DateField(element);
    try {
      field.loadDrawControl(
          ((Document) ((OdfFileDom) element.getOwnerDocument()).getDocument()).getContentRoot());
    } catch (Exception e) {
      Logger.getLogger(DateField.class.getName())
          .log(Level.WARNING, "Cannot load the drawing shape of this field.");
    }
    return field;
  }

  /**
   * Set the visibility of the drop-down button.
   *
   * @param isVisible - the visibility of drop-down button.
   */
  public void setDropDownVisible(boolean isVisible) {
    this.setFormProperty(
        OOFormProvider.FORM_PROPERTY_NAME_DROPDOWN,
        "boolean",
        null,
        isVisible,
        null,
        null,
        null,
        null);
  }

  /**
   * Format the date value according to the appointed format and locale.
   *
   * @param formatStr - format code
   * @param locale - locale information
   */
  public void formatDate(String formatStr, Locale locale) {
    this.setFormatString(formatStr, Value.DATE, locale);
  }

  /**
   * Get the format code which is used to format the display value
   *
   * @return the format code
   */
  public String getDateFormat() {
    return this.getFormatString(Value.DATE);
  }
}
