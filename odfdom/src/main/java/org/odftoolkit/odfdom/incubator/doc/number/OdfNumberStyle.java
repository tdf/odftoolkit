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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.number.NumberFractionElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberScientificNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/**
 * Convenient functionality for the parent ODF OpenDocument element
 *
 */
public class OdfNumberStyle extends NumberNumberStyleElement {

	public OdfNumberStyle(OdfFileDom ownerDoc) {
		super(ownerDoc);
	}

	public OdfNumberStyle(OdfFileDom ownerDoc, String format, String styleName) {
		super(ownerDoc);
		this.setStyleNameAttribute(styleName);
		setFormat(format);
	}
	private String quoteTextContent(String text){
        Pattern p = Pattern.compile("[MDYHMSE#0,.]+");
        Matcher m = p.matcher(text);
        if(m.find()){
            text = "\"" + text +  "\"";

	    }
	    return text;
	}
	/**
	 * Get the format string that represents this style.
	 * @return the format string
	 */
	@Override
    public String getFormat(boolean caps) {
	    String mappedResult = "";
		String result = "";
		Node m = getFirstChild();
		while (m != null) {
			if (m instanceof NumberNumberElement) {
				result += getNumberFormat();
			} else if (m instanceof NumberTextElement) {
				String textcontent = m.getTextContent();
				if (textcontent == null || textcontent.length() == 0) {
					textcontent = " ";
				}
				result += quoteTextContent(textcontent);
            } else if(m instanceof StyleTextPropertiesElement) {
                result += getColorFromElement((StyleTextPropertiesElement) m);
            } else if(m instanceof StyleMapElement) {
                mappedResult += getMapping((StyleMapElement)m);
                mappedResult += ";";
            } else if(m instanceof NumberFractionElement) {
                NumberFractionElement f = (NumberFractionElement)m;
                Integer digitCount = f.getNumberMinIntegerDigitsAttribute();
                if(digitCount != null){
                    if(digitCount == 0 ){
                        result +=  '#'; // show optional integer part of the fraction
                    } else {
                        while(--digitCount >= 0 ) {
                            result +=  '0';
                        }
                    }
                    result += ' '; //space between integer part and fraction
                }
                Integer numeratorCount = f.getNumberMinNumeratorDigitsAttribute();
                if(numeratorCount != null ){
                    while(--numeratorCount >= 0 ) {
                        result +=  '?';
                    }
                } else {
                    result += '?';
                }

                result += '/';
                Integer denominatorCount = f.getNumberMinDenominatorDigitsAttribute();
                if( denominatorCount != null ){
                    while(--denominatorCount >= 0 ) {
                        result += '?';
                    }
                } else {
                    result += '?';
                }
            } else if(m instanceof NumberScientificNumberElement){
                NumberScientificNumberElement s = (NumberScientificNumberElement)m;
                Boolean isGroup = s.getNumberGroupingAttribute();
                Integer digits = s.getNumberMinIntegerDigitsAttribute();
                int digitCount = digits == null ? 0 : digits.intValue();
                for(int digit = 0; digit < digitCount; ++ digit){
                    result += '0';
                }
                Integer places = s.getNumberDecimalPlacesAttribute();
                if(places != null){
                    result += '.';
                    int placeCount = places.intValue();
                    while(--placeCount >= 0){
                        result += '0';
                    }
                }
                result += 'E';
                if(isGroup != null && isGroup.booleanValue()){
                    //fill with #,##...
                    if(digitCount < 4){
                        String fill = "#,###";
                        result = fill.substring(0, 5 - digitCount) + result;
                    } else {
                        result = result.substring(0, digitCount - 3) + ',' + result.substring(digitCount - 3);
                    }
                }
                Integer exp = s.getNumberMinExponentDigitsAttribute();
                if(exp != null) {
                    result += '+';
                    int exponents = exp.intValue();
                    while(--exponents >= 0){
                        result += '0';
                    }
                }
            }
            m = m.getNextSibling();
        }
        if(!mappedResult.isEmpty()){
            result = mappedResult + result;
        }
		return result;
	}


	/**
	 * Creates a &lt;number:number-style&gt; element based upon format.
	 * @param format the number format string
	 */
	/**
	 * Creates a &lt;number:number-style&gt; element based upon format.
	 * @param format the number format string
	 */
	@Override
    public void setFormat(String format) {
		/*
		 * Setting ownerDoc won't be necessary once this is folded into
		 * OdfNumberStyle
		 */

        int openBracket = format.indexOf("[");
        String color = "";
        while(openBracket >= 0){
            int closeBracket = format.indexOf("]", openBracket);
            if(closeBracket > openBracket){
                String innerText = format.substring(openBracket + 1, closeBracket);
                if(innerText.length() > 1) {
                    //detect color - if any
                    color = getColorElement(innerText);
                    if(!color.isEmpty()) {
                        OdfFileDom dom = (OdfFileDom) this.getOwnerDocument();
                        StyleTextPropertiesElement cProperties = new StyleTextPropertiesElement(dom);
                        cProperties.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:color", color);
                        this.appendChild(cProperties);
                    }
                    format = format.substring(0, openBracket) + format.substring(closeBracket + 1);
                }
            }
            openBracket = format.indexOf("[");
        }

		/*
		 * If there is a numeric specification, then split the
		 * string into the part before the specifier, the specifier
		 * itself, and then part after the specifier. The parts
		 * before and after are just text (which may contain the
		 * currency symbol).
		 */
        if (format != null && !format.equals("")) {
            Pattern p = Pattern.compile("[#0,.?/E+\\s]+");
            Matcher m = p.matcher(format);

            int lastEnd = 0;
            while (m.find()) {
                String prefix = "";
                if(m.start() > lastEnd ){
                    prefix = format.substring(lastEnd, m.start());
                }
                lastEnd = m.end();
                String sub = format.substring(m.start(), m.end());
                if(sub.startsWith(" ")){
                    int pos = 1;
                    while(sub.length() > pos && sub.charAt(pos) == ' '){
                        ++pos;
                    }
                    prefix += sub.substring( 0, pos);
                    sub = sub.substring(pos);
                }
                if(!prefix.isEmpty()){
                    emitText(prefix);
                }
                String suffix = "";
                if(sub.endsWith(" ")){
                    int pos = sub.length() - 1;
                    while(sub.charAt(pos) == ' '){
                        --pos;
                    }
                    suffix = sub.substring( pos + 1 );
                    sub = sub.substring(0, pos + 1);
                }
                boolean denominator = false;
                int denominatorCount = 0;
                int nominatorCount = 0;
                boolean isDecimals = false;
                boolean isFraction = false;
                boolean isHash = false;
                boolean isGrouping = false;
                int digitCount = 0;
                int decimalsCount = 0;
                boolean isScientific = false;
                int exponentCount = 0;
                for(int pos = 0; pos < sub.length(); ++pos){
                    char c = sub.charAt(pos);
                    if(c == '?'){
                        isFraction = true;
                        if(denominator){
                            ++denominatorCount;
                        } else {
                            ++nominatorCount;
                        }
                    } else if( c == '/') {
                        denominator = true;
                    } else if (c == ',') {
                        isGrouping = true;
                    } else if (c == '.') {
                        isDecimals = true;
                    } else if (c == '0') {
                        if(isScientific) {
                            ++exponentCount;
                        } else if(isDecimals){
                            ++decimalsCount;
                        } else {
                            ++digitCount;
                        }
                    } else if (c == 'E') {
                        isScientific = true;
                    } else if (c == '#') {
                        isHash = true; // only required in fraction formats
                    }
                }


                if(isFraction){
                    NumberFractionElement number = new NumberFractionElement((OdfFileDom) this.getOwnerDocument());
                    if (isHash || digitCount > 0) {
                        number.setNumberMinIntegerDigitsAttribute(digitCount == 0 && isHash ? 1 : digitCount);
                    }
                    number.setNumberMinNumeratorDigitsAttribute(nominatorCount);
                    number.setNumberMinDenominatorDigitsAttribute(denominatorCount);
                    appendChild(number);
                } else if(isScientific) {
                    NumberScientificNumberElement number = new NumberScientificNumberElement((OdfFileDom) this.getOwnerDocument());
                    if(decimalsCount > 0){
                        number.setNumberDecimalPlacesAttribute(decimalsCount);
                    }
                    if (digitCount > 0) {
                        number.setNumberMinIntegerDigitsAttribute(digitCount);
                    }
                    if(isGrouping){
                        number.setNumberGroupingAttribute(true);
                    }
                    if(exponentCount > 0){
                        number.setNumberMinExponentDigitsAttribute(exponentCount);
                    }
                    appendChild(number);
                } else if(sub.length() > 0){
                    NumberNumberElement number = new NumberNumberElement((OdfFileDom) this.getOwnerDocument());
                    if(decimalsCount > 0){
                        number.setNumberDecimalPlacesAttribute(decimalsCount);
                    }
                    if (digitCount > 0) {
                        number.setNumberMinIntegerDigitsAttribute(digitCount);
                    }
                    if(isGrouping){
                        number.setNumberGroupingAttribute(true);
                    }
                    appendChild(number);
                }
                if(!suffix.isEmpty()){
                    emitText(suffix);
                }

            }
            if(lastEnd < format.length()){
                emitText( format.substring( lastEnd ) );
            }
        }

	}


	/**
	 * Set &lt;style:map&gt; for positive values to the given style name.
	 * @param mapName the style name to map to
	 */
	public void setMapPositive(String mapName) {
		StyleMapElement map = new StyleMapElement((OdfFileDom) this.getOwnerDocument());
		map.setStyleApplyStyleNameAttribute(mapName);
		map.setStyleConditionAttribute("value()>0");
		this.appendChild(map);
	}

	/**
	 * Set &lt;style:map&gt; for negative values to the given style name.
	 * @param mapName the style name to map to
	 */
	public void setMapNegative(String mapName) {
		StyleMapElement map = new StyleMapElement((OdfFileDom) this.getOwnerDocument());
		map.setStyleApplyStyleNameAttribute(mapName);
		map.setStyleConditionAttribute("value()<0");
		this.appendChild(map);
	}
}
