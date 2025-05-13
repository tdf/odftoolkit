/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.incubator.doc.number;

import org.odftoolkit.odfdom.dom.attribute.number.NumberFormatSourceAttribute;
import org.odftoolkit.odfdom.dom.element.number.NumberAmPmElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDateStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDayElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDayOfWeekElement;
import org.odftoolkit.odfdom.dom.element.number.NumberEraElement;
import org.odftoolkit.odfdom.dom.element.number.NumberHoursElement;
import org.odftoolkit.odfdom.dom.element.number.NumberMinutesElement;
import org.odftoolkit.odfdom.dom.element.number.NumberMonthElement;
import org.odftoolkit.odfdom.dom.element.number.NumberQuarterElement;
import org.odftoolkit.odfdom.dom.element.number.NumberSecondsElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.number.NumberWeekOfYearElement;
import org.odftoolkit.odfdom.dom.element.number.NumberYearElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 * <p>This class lets you create a date style from a format string. The format string is given in
 * the same form as Java's SimpleDateFormat class.
 *
 * <p>The characters used are:
 *
 * <pre>
 * G  	Era designator       AD
 * y 	Year                 1996; 96
 * Q	Quarter in Year      2 -- not in Java; in ODF
 * M 	Month in year        July; Jul; 07
 * w 	Week in year         27
 * W 	Week in month        -- not in ODF
 * D 	Day in year          -- not in ODF
 * d 	Day in month         10
 * F 	Day of week in month -- not in ODF
 * E 	Day in week          Tuesday; Tue
 * a 	Am/pm marker         PM
 * H 	Hour in day (0-23)   0
 * k 	Hour in day (1-24)   -- not in ODF
 * K 	Hour in am/pm (0-11) -- not in ODF
 * h 	Hour in am/pm (1-12) -- depends on AM/PM marker
 * m 	Minute in hour       30
 * s 	Second in minute     55
 * S 	Millisecond          -- not in ODF
 * z 	Time zone            -- not in ODF
 * Z 	Time zone RFC822     -- not in ODF
 * </pre>
 *
 * The G, E, and y specifiers are in long form if there are more then 3 in a row. The Q specifier is
 * in long form if there are more than 2 in a row. The d, h, and m specifiers are in long form if
 * there is more than one in a row.
 */
public class OdfNumberDateStyle extends NumberDateStyleElement {

  private String styleName;
  private String calendarName;

  public OdfNumberDateStyle(OdfFileDom ownerDoc) {
    super(ownerDoc);
  }

  /**
   * Creates a new instance of DateStyleFromFormat.
   *
   * @param ownerDoc document that this format belongs to
   * @param format format string for the date/time
   * @param styleName name of this style
   */
  public OdfNumberDateStyle(OdfFileDom ownerDoc, String format, String styleName) {
    this(ownerDoc, format, styleName, null);
  }

  /**
   * Creates a new instance of DateStyleFromFormat.
   *
   * @param ownerDoc document that this format belongs to
   * @param format format string for the date/time
   * @param styleName name of this style
   * @param calendarName name of the calendar this date style belongs to
   */
  public OdfNumberDateStyle(
      OdfFileDom ownerDoc, String format, String styleName, String calendarName) {
    super(ownerDoc);
    this.styleName = styleName;
    this.calendarName = calendarName;
    setFormat(format);
  }

  /**
   * Get the format string that represents this style.
   *
   * @return the format string
   */
  @Override
  public String getFormat(boolean caps) {
    String result = "";
    Node child = this.getFirstChild();
    while (child != null) {
      if (child instanceof OdfElement) {
        if (child instanceof NumberDayElement) {
          NumberDayElement ele = (NumberDayElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += caps ? "DD" : "dd";
          } else {
            result += caps ? "D" : "d";
          }
        } else if (child instanceof NumberMonthElement) {
          NumberMonthElement ele = (NumberMonthElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if (ele.getNumberTextualAttribute()) {
            if ((numberstyle != null) && numberstyle.equals("long")) {
              result += "MMMM";
            } else {
              result += "MMM";
            }
          } else {
            if ((numberstyle != null) && numberstyle.equals("long")) {
              result += "MM";
            } else {
              result += "M";
            }
          }
        } else if (child instanceof NumberYearElement) {
          NumberYearElement ele = (NumberYearElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += caps ? "YYYY" : "yyyy";
          } else {
            result += caps ? "YY" : "yy";
          }
        } else if (child instanceof NumberTextElement) {
          String content = child.getTextContent();
          if ((content == null) || (content.equals(""))) {
            result += " ";
          } else {
            result += content;
          }
        } else if (child instanceof NumberEraElement) {
          NumberEraElement ele = (NumberEraElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += "GG";
          } else {
            result += "G";
          }
        } else if (child instanceof NumberHoursElement) {
          NumberHoursElement ele = (NumberHoursElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += caps ? "HH" : "hh";
          } else {
            result += caps ? "H" : "h";
          }
        } else if (child instanceof NumberMinutesElement) {
          NumberMinutesElement ele = (NumberMinutesElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += "mm";
          } else {
            result += "m";
          }
        } else if (child instanceof NumberSecondsElement) {
          NumberSecondsElement ele = (NumberSecondsElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += caps ? "SS" : "ss";
          } else {
            result += caps ? "S" : "s";
          }
          Integer decimals = ele.getNumberDecimalPlacesAttribute();
          if (decimals != null && decimals > 0) {
            result += '.';
            for (int i = 0; i < decimals; i++) {
              result += '0';
            }
          }
        } else if (child instanceof NumberQuarterElement) {
          NumberQuarterElement ele = (NumberQuarterElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += "QQ";
          } else {
            result += "Q";
          }
        } else if (child instanceof NumberDayOfWeekElement) {
          NumberDayOfWeekElement ele = (NumberDayOfWeekElement) child;
          String numberstyle = ele.getNumberStyleAttribute();
          if ((numberstyle != null) && numberstyle.equals("long")) {
            result += "NNN";
          } else {
            result += "NN";
          }
        } else if (child instanceof NumberAmPmElement) {
          result += "AM/PM";
        } else if (child instanceof NumberWeekOfYearElement) {
          result += "WW";
        }
      }
      child = child.getNextSibling();
    }
    return result;
  }

  /**
   * Creates a &lt;number:date-style&gt; element based upon format.
   *
   * @param format the format string
   */
  @Override
  public void setFormat(String format) {
    String actionChars = "GgYyQqMWwDdNnEeHhmSs";
    int actionCount = 0;

    char ch;
    String textBuffer = "";
    boolean endQuote = false;

    int i = 0;

    this.setStyleNameAttribute(styleName);
    this.setNumberFormatSourceAttribute(NumberFormatSourceAttribute.Value.LANGUAGE.toString());

    while (i < format.length()) {
      ch = format.charAt(i);
      if (actionChars.indexOf(ch) >= 0) {
        emitText(textBuffer);
        textBuffer = "";
        actionCount = 0;
        while (i < format.length() && format.charAt(i) == ch) {
          actionCount++;
          i++;
        }
        int decimalCount = 0;
        if (i < format.length() - 1 && format.charAt(i) == '.' && format.charAt(i + 1) == '0') {
          decimalCount = 1;
          i += 2;
          while (i < format.length() && format.charAt(i) == '0') {
            decimalCount++;
            i++;
          }
        }
        // special case: a single 'w' is not an action char
        if (actionCount > 1 || (ch != 'w' && ch != 'W')) {
          processChar(ch, actionCount, decimalCount);
        } else {
          textBuffer += ch;
        }

      } else if (ch == '\'') {
        endQuote = false;
        i++;
        while (i < format.length() && (!endQuote)) {
          ch = format.charAt(i);
          if (ch == '\'') // check to see if this is really the end
          {
            if (i + 1 < format.length() && format.charAt(i + 1) == '\'') {
              i++;
              textBuffer += "'";
            } else {
              endQuote = true;
            }
          } else {
            textBuffer += ch;
          }
          i++;
        }
      } else {
        // special handling "AM/PM"
        if (ch == 'A' && format.startsWith("AM/PM", i)) {
          emitText(textBuffer);
          textBuffer = "";
          NumberAmPmElement ampm = new NumberAmPmElement((OdfFileDom) this.getOwnerDocument());
          this.appendChild(ampm);
          i += 5;
        } else {
          textBuffer += ch;
          i++;
        }
      }
    }
    emitText(textBuffer);
  }

  /**
   * Process a formatting character. These elements are built "by hand" rather than
   *
   * @param ch the formatting character to process
   * @param count the number of occurrences of this character
   */
  private void processChar(char ch, int count, int decimalCount) {
    OdfFileDom ownerDoc = (OdfFileDom) this.getOwnerDocument();
    switch (ch) {
      case 'G':
        NumberEraElement era = new NumberEraElement(ownerDoc);
        era.setNumberStyleAttribute(isLongIf(count > 3));
        if (calendarName != null) {
          era.setNumberCalendarAttribute(calendarName);
        }
        this.appendChild(era);
        break;
      case 'y':
      case 'Y':
        NumberYearElement year = new NumberYearElement(ownerDoc);
        year.setNumberStyleAttribute(isLongIf(count > 3));
        if (calendarName != null) {
          year.setNumberCalendarAttribute(calendarName);
        }
        this.appendChild(year);
        break;
      case 'Q':
        NumberQuarterElement quarter = new NumberQuarterElement(ownerDoc);
        quarter.setNumberStyleAttribute(isLongIf(count > 2));
        if (calendarName != null) {
          quarter.setNumberCalendarAttribute(calendarName);
        }
        this.appendChild(quarter);
        break;
      case 'M':
        NumberMonthElement month = new NumberMonthElement(ownerDoc);
        month.setNumberTextualAttribute(count > 2);
        month.setNumberStyleAttribute(isLongIf(count % 2 == 0));
        if (calendarName != null) {
          month.setNumberCalendarAttribute(calendarName);
        }
        this.appendChild(month);
        break;
      case 'w':
      case 'W':
        NumberWeekOfYearElement weekOfYear = new NumberWeekOfYearElement(ownerDoc);
        if (calendarName != null) {
          weekOfYear.setNumberCalendarAttribute(calendarName);
        }
        this.appendChild(weekOfYear);
        break;
      case 'd':
      case 'D':
        if (count > 2) {
          NumberDayOfWeekElement day = new NumberDayOfWeekElement(ownerDoc);
          day.setNumberStyleAttribute(isLongIf(count > 3));
          if (calendarName != null) {
            day.setNumberCalendarAttribute(calendarName);
          }
          this.appendChild(day);
        } else {
          NumberDayElement day = new NumberDayElement(ownerDoc);
          day.setNumberStyleAttribute(isLongIf(count > 1));
          if (calendarName != null) {
            day.setNumberCalendarAttribute(calendarName);
          }
          this.appendChild(day);
        }
        break;
      case 'N':
        NumberDayOfWeekElement dayOfWeek = new NumberDayOfWeekElement(ownerDoc);
        dayOfWeek.setNumberStyleAttribute(isLongIf(count > 3));
        if (count > 3) {
          emitText(", "); // NNNN resolves to long day-of-week plus ", "
        }
        if (calendarName != null) {
          dayOfWeek.setNumberCalendarAttribute(calendarName);
        }
        this.appendChild(dayOfWeek);
        break;
      case 'H':
      case 'h':
        NumberHoursElement hours = new NumberHoursElement(ownerDoc);
        hours.setNumberStyleAttribute(isLongIf(count > 1));
        this.appendChild(hours);
        break;
      case 'm':
        NumberMinutesElement minutes = new NumberMinutesElement(ownerDoc);
        minutes.setNumberStyleAttribute(isLongIf(count > 1));
        this.appendChild(minutes);
        break;
      case 's':
      case 'S':
        NumberSecondsElement seconds = new NumberSecondsElement(ownerDoc);
        seconds.setNumberStyleAttribute(isLongIf(count > 1));
        if (decimalCount > 0) {
          seconds.setNumberDecimalPlacesAttribute(decimalCount);
        }
        this.appendChild(seconds);
        break;
    }
  }

  /**
   * Add long or short style to an element.
   *
   * @param isLong true if this is number:style="long"; false if number:style="short"
   * @return the string "long" or "short"
   */
  private String isLongIf(boolean isLong) {
    return ((isLong) ? "long" : "short");
  }
}
