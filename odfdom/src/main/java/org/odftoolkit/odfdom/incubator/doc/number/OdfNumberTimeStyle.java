/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.odftoolkit.odfdom.incubator.doc.number;

import org.odftoolkit.odfdom.dom.attribute.number.NumberFormatSourceAttribute;
import org.odftoolkit.odfdom.dom.element.number.NumberAmPmElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDayElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDayOfWeekElement;
import org.odftoolkit.odfdom.dom.element.number.NumberEraElement;
import org.odftoolkit.odfdom.dom.element.number.NumberHoursElement;
import org.odftoolkit.odfdom.dom.element.number.NumberMinutesElement;
import org.odftoolkit.odfdom.dom.element.number.NumberMonthElement;
import org.odftoolkit.odfdom.dom.element.number.NumberQuarterElement;
import org.odftoolkit.odfdom.dom.element.number.NumberSecondsElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTimeStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberYearElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 * This class lets you create a date style from a format string.
 * The format string is given in the same form as Java's
 * SimpleDateFormat class.
 *
 * The characters used are:
 * <pre>
a 	Am/pm marker         PM
H 	Hour in day (0-23)   0
k 	Hour in day (1-24)   -- not in ODF
K 	Hour in am/pm (0-11) -- not in ODF
h 	Hour in am/pm (1-12) -- depends on AM/PM marker
m 	Minute in hour       30
s 	Second in minute     55
S 	Millisecond          -- not in ODF
z 	Time zone            -- not in ODF
Z 	Time zone RFC822     -- not in ODF
 * </pre>
 */
public class OdfNumberTimeStyle extends NumberTimeStyleElement {

    public OdfNumberTimeStyle(OdfFileDom ownerDoc) {
        super(ownerDoc);
    }
    private String styleName;
    private String formatCode;


    /** Creates a new instance of OdfTimeStyle.
     * @param ownerDoc document that this format belongs to
     * @param format format string for the date/time
     * @param styleName name of this style
     */
    public OdfNumberTimeStyle(OdfFileDom ownerDoc, String format, String styleName) {
        super(ownerDoc);
        this.styleName = styleName;
        this.formatCode = format;
        setFormat(format);
    }

    /**
     * Get the format string that represents this style.
     * @return the format string
     */
    @Override
    public String getFormat( boolean capsDateFormat ) {
        if(formatCode == null)
        {
            formatCode = "";
            String truncate = getAttribute("number:truncate-on-overflow");
            boolean setBrackets = false; // set brackets around first time element
            if(truncate != null && truncate.equals("false"))
                setBrackets = true;
            Node child = this.getFirstChild();
            while (child != null) {
                if (child instanceof OdfElement) {
                    if (child instanceof NumberDayElement) {
                        NumberDayElement ele = (NumberDayElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += capsDateFormat ? "DD" : "dd";
                        } else {
                            formatCode += capsDateFormat ? "D" : "d";
                        }
                    } else if (child instanceof NumberMonthElement) {
                        NumberMonthElement ele = (NumberMonthElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if (ele.getNumberTextualAttribute().booleanValue()) {
                            if ((numberstyle != null) && numberstyle.equals("long")) {
                                formatCode += "MMMM";
                            } else {
                                formatCode += "MMM";
                            }
                        } else {
                            if ((numberstyle != null) && numberstyle.equals("long")) {
                                formatCode += "MM";
                            } else {
                                formatCode += "M";
                            }
                        }
                    } else if (child instanceof NumberYearElement) {
                        NumberYearElement ele = (NumberYearElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += capsDateFormat ? "YYYY" : "yyyy";
                        } else {
                            formatCode += capsDateFormat ? "YY" : "yy";
                        }
                    } else if (child instanceof NumberTextElement) {
                        String content = child.getTextContent();
                        if ((content == null) || (content.equals(""))) {
                            formatCode += " ";
                        } else {
                            formatCode += content;
                        }
                    } else if (child instanceof NumberEraElement) {
                        NumberEraElement ele = (NumberEraElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += "GGGG";
                        } else {
                            formatCode += "GG";
                        }
                    } else if (child instanceof NumberHoursElement) {
                        NumberHoursElement ele = (NumberHoursElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if( setBrackets)
                            formatCode += "[";
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += capsDateFormat ? "HH": "hh";
                        } else {
                            formatCode += capsDateFormat ? "H": "h";
                        }
                        if( setBrackets)
                        {
                            formatCode += "]";
                            setBrackets = false;
                        }
                    } else if (child instanceof NumberMinutesElement) {
                        if( setBrackets)
                            formatCode += "[";
                        NumberMinutesElement ele = (NumberMinutesElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += "mm";
                        } else {
                            formatCode += "m";
                        }
                        if( setBrackets)
                        {
                            formatCode += "]";
                            setBrackets = false;
                        }
                    } else if (child instanceof NumberSecondsElement) {
                        if( setBrackets)
                            formatCode += "[";
                        NumberSecondsElement ele = (NumberSecondsElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += capsDateFormat ? "SS": "ss";
                        } else {
                            formatCode += capsDateFormat ? "SS": "s";
                        }
                        Integer decimals = ele.getNumberDecimalPlacesAttribute();
                        if(decimals != null && decimals.intValue() > 0){
                            formatCode += '.';
                            for( int i = 0; i < decimals.intValue(); i++){
                                formatCode += '0';
                            }
                        }
                        if( setBrackets)
                        {
                            formatCode += "]";
                            setBrackets = false;
                        }
                    } else if (child instanceof NumberQuarterElement) {
                        NumberQuarterElement ele = (NumberQuarterElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += "QQQ";
                        } else {
                            formatCode += "Q";
                        }
                    } else if (child instanceof NumberDayOfWeekElement) {
                        NumberDayOfWeekElement ele = (NumberDayOfWeekElement) child;
                        String numberstyle = ele.getNumberStyleAttribute();
                        if ((numberstyle != null) && numberstyle.equals("long")) {
                            formatCode += "EEEE";
                        } else {
                            formatCode += "EEE";
                        }
                    } else if (child instanceof NumberAmPmElement) {
                        formatCode += "AM/PM";
                    }
                }
                child = child.getNextSibling();
            }
        }
        return formatCode;
    }

    /**
     * Creates a <code>&lt;number:time-style&gt;</code> element based upon format.
     * @param format the format for the time
     */
    @Override
    public void setFormat(String format) {
        String actionChars = "GyQMwdEHhms";
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
                appendText(textBuffer);
                textBuffer = "";
                actionCount = 0;
                while (i < format.length() && format.charAt(i) == ch) {
                    actionCount++;
                    i++;
                }
                processChar(ch, actionCount);
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
                //special handling "AM/PM"
                if(ch=='A' && format.startsWith("AM/PM", i)) {
                    appendText(textBuffer);
                    textBuffer = "";
                    NumberAmPmElement ampm = new NumberAmPmElement((OdfFileDom) this.getOwnerDocument());
                    this.appendChild(ampm);
                    i+=5;
                }
                else {
	                textBuffer += ch;
	                i++;
                }
            }
        }
        appendText(textBuffer);
    }

    /**
     *	Place pending text into a &lt;number:text&gt; element.
     * @param textBuffer pending text
     */
    private void appendText(String textBuffer) {
        NumberTextElement textElement = null;
        if (!textBuffer.equals("")) {
            textElement = new NumberTextElement((OdfFileDom) this.getOwnerDocument());
            textElement.setTextContent(textBuffer);
            this.appendChild(textElement);
        }
    }

    /**
     * Process a formatting character.
     * @param ch the formatting character to process
     * @param count the number of occurrences of this character
     */
    private void processChar(char ch, int count) {
        OdfFileDom ownerDoc = (OdfFileDom) this.getOwnerDocument();
        switch (ch) {
            case 'H':
            case 'h':
            	NumberHoursElement hours = new NumberHoursElement(ownerDoc);
                hours.setNumberStyleAttribute(isLongIf(count > 1));
                this.appendChild(hours);
                break;
            case 'M':
            case 'm':
                NumberMinutesElement minutes = new NumberMinutesElement(ownerDoc);
                minutes.setNumberStyleAttribute(isLongIf(count > 1));
                this.appendChild(minutes);
                break;
            case 'S':
            case 's':
                NumberSecondsElement seconds = new NumberSecondsElement(ownerDoc);
                seconds.setNumberStyleAttribute(isLongIf(count > 1));
                this.appendChild(seconds);
                break;
        }
    }

     /**
     * Add long or short style to an element.
     * @param isLong true if this is number:style="long"; false if number:style="short"
     * @return the string "long" or "short"
     */
    private String isLongIf(boolean isLong) {
        return ((isLong) ? "long" : "short");
    }
}
