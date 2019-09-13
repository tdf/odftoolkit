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

import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.odftoolkit.odfdom.changes.MapHelper;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.number.DataStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencyStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencySymbolElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 */
public class OdfNumberCurrencyStyle extends NumberCurrencyStyleElement {

	public OdfNumberCurrencyStyle(OdfFileDom ownerDoc) {
		super(ownerDoc);
	}

	public OdfNumberCurrencyStyle(OdfFileDom ownerDoc,
			String currencySymbol, String format, String styleName) {
		super(ownerDoc);
		this.setStyleNameAttribute(styleName);
		buildFromFormat(currencySymbol, format);
	}

    /**
     *
     * @param ownerDoc parent file
     * @param format format string
     * @param styleName new style name
     */
	public OdfNumberCurrencyStyle(OdfFileDom ownerDoc, String format, String styleName) {
        super(ownerDoc);
        this.setStyleNameAttribute(styleName);
        setFormat(format);
    }

	/**
	 * Get the format string that represents this style.
	 * @return the format string
	 */
	@Override
    public String getFormat(boolean caps) {
		String result = "";
		String mappedResult = "";
		Node m = getFirstChild();
		while (m != null) {
			if (m instanceof NumberCurrencySymbolElement) {
			    result += "[$";
				result += m.getTextContent();
				String language = ((NumberCurrencySymbolElement) m).getAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "language");
                String country = ((NumberCurrencySymbolElement) m).getAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "country");
                if( !language.isEmpty() ) {
                    result += "-";
                    //convert local to ms language id
                    result += MapHelper.getMSLangCode(language, country);
                }
                result += "]";
			} else if (m instanceof NumberNumberElement) {
				result += getNumberFormat();
			} else if (m instanceof NumberTextElement) {
				String textcontent = m.getTextContent();
				if (textcontent == null || textcontent.length() == 0) {
					textcontent = " ";
				}
				result += textcontent;
            } else if (m instanceof StyleTextPropertiesElement) {
                result += getColorFromElement((StyleTextPropertiesElement)m);
            } else if(m instanceof StyleMapElement) {
                mappedResult += getMapping((StyleMapElement)m);
                mappedResult += ";";
			}
			m = m.getNextSibling();
		}
        if(!mappedResult.isEmpty()){
            result = mappedResult + result;
        }
		return result;
	}

	public String getCurrencyCode() {
		Node m = getFirstChild();
		while (m!=null) {
			if (m instanceof NumberCurrencySymbolElement) {
				final String currencyValue = m.getTextContent();
				if(currencyValue!=null&&currencyValue.equals("DM")) {
					return "DEM";
				}
				final String language =  ((NumberCurrencySymbolElement) m).getAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "language");
				final String country = ((NumberCurrencySymbolElement) m).getAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "country");
				if(language!=null) {
					try {
						final Currency currency = Currency.getInstance(country!=null ? new Locale(language, country) : new Locale(language));
						if(currency!=null) {
							return currency.toString();
						}
					}
					catch(IllegalArgumentException e) {
					}
				}
				break;
			}
			m = m.getNextSibling();
		}
		return getCurrencySymbolElement().getTextContent();
	}

	public String getConditionStyleName(double value) {
		StyleMapElement map = OdfElement.findFirstChildNode(StyleMapElement.class, this);
		while (map != null) {
			String condition = map.getStyleConditionAttribute();
			if (isTrue(condition, value)) {
				return map.getStyleApplyStyleNameAttribute();
			}
			map = OdfElement.findNextChildNode(StyleMapElement.class, map);
		}
		return getStyleNameAttribute();
	}

	private boolean isTrue(String condition, double value) {
		double rightOp = getLastNumber(condition);
		if (condition.indexOf('>') != -1) {
			if (value > rightOp) {
				return true;
			}
		} else if (condition.indexOf('<') != -1) {
			if (value < rightOp) {
				return true;
			}
		}
		if (condition.indexOf('!') != -1) {
			if (value != rightOp) {
				return true;
			}
		} else if (condition.indexOf('=') != -1) {
			if (value == rightOp) {
				return true;
			}
		}
		return false;
	}

	private Double getLastNumber(String condition) {
		String results = "";
		for (int i = condition.length() - 1; i >= 0; i--) {
			if (condition.charAt(i) >= '0' && condition.charAt(i) <= '9') {
				results += condition.charAt(i);
			} else {
				break;
			}
		}
		return Double.parseDouble(results);
	}
	@Override
    public void setFormat(String format) {
        OdfFileDom dom = (OdfFileDom) this.getOwnerDocument();
        List<StringToken> tokens = tokenize(format, DataStyleElement.NumberFormatType.FORMAT_CURRENCY);
        emitTokens(tokens, DataStyleElement.NumberFormatType.FORMAT_CURRENCY);
    }
	/**
	 * Creates a &lt;number:date-style&gt; element based upon format.
	 * @param currencySymbol the string to be placed as the currency symbol
	 * @param format the currency format string
	 */
	public void buildFromFormat(String currencySymbol, String format) {
		String preMatch;
		String numberSpec;
		String postMatch;
		int pos;
		char ch;
		int nDigits;

		Pattern p = Pattern.compile("[#0,.]+");
		Matcher m;
		NumberNumberElement number;

		/*
		 * If there is a numeric specifcation, then split the
		 * string into the part before the specifier, the specifier
		 * itself, and then part after the specifier. The parts
		 * before and after are just text (which may contain the
		 * currency symbol).
		 */
		m = p.matcher(format);
		if (m.find()) {
			preMatch = format.substring(0, m.start());
			numberSpec = format.substring(m.start(), m.end());
			postMatch = format.substring(m.end());

			processText(preMatch, currencySymbol);

			number = new NumberNumberElement((OdfFileDom) this.getOwnerDocument());

			/* Process part before the decimal point (if any) */
			nDigits = 0;
			for (pos = 0; pos < numberSpec.length()
					&& (ch = numberSpec.charAt(pos)) != '.'; pos++) {
				if (ch == ',') {
					number.setNumberGroupingAttribute(new Boolean(true));
				} else if (ch == '0') {
					nDigits++;
				}
			}
			number.setNumberMinIntegerDigitsAttribute(nDigits);

			/* Number of decimal places is the length after the decimal */
			if (pos < numberSpec.length()) {
				number.setNumberDecimalPlacesAttribute(numberSpec.length() - (pos + 1));
			}
			this.appendChild(number);

			processText(postMatch, currencySymbol);
		}
	}

	/**
	 * Process text that may have a currency symbol ($) in it.
	 * @param text string to be processed
	 * @param currencySymbol the currency symbol under consideration
	 */
	private void processText(String text, String currencySymbol) {
		OdfFileDom dom = (OdfFileDom) this.getOwnerDocument();
		int currencyPos = text.indexOf(currencySymbol);
		if (currencyPos >= 0) {
			emitText(text.substring(0, currencyPos));
			NumberCurrencySymbolElement cSymbol = new NumberCurrencySymbolElement(dom);
			cSymbol.appendChild(dom.createTextNode(currencySymbol));
			this.appendChild(cSymbol);
			emitText(text.substring(currencyPos + currencySymbol.length()));
		} else {
			emitText(text);
		}
	}

	/**
	 * Get OdfCurrencySymbol element from this currency style.
	 * Once you have it, you can add language and country.
	 * @return an OdfCurrencySymbol element
	 */
	public NumberCurrencySymbolElement getCurrencySymbolElement() {
		NumberCurrencySymbolElement cSymbol = null;
		NodeList list = this.getElementsByTagNameNS(
				OdfDocumentNamespace.NUMBER.getUri(), "currency-symbol");
		if (list.getLength() > 0) {
			cSymbol = (NumberCurrencySymbolElement) list.item(0);
		}
		return cSymbol;
	}

	/**
	 * Set language and currency for the currency symbol.
	 * @param language the language for the country
	 * @param country the country name
	 */
	public void setCurrencyLocale(String language, String country) {
		NumberCurrencySymbolElement cSymbol = getCurrencySymbolElement();
		cSymbol.setNumberCountryAttribute(country);
		cSymbol.setNumberLanguageAttribute(language);
	}

	/**
	 * Set language and currency for the currency symbol.
	 * Argument could be just a language like "el" or a
	 * language and country like "en-US".
	 * @param locale string in form language-country or language
	 */
	public void setCurrencyLocale(String locale) {
		NumberCurrencySymbolElement cSymbol = getCurrencySymbolElement();
		int pos = locale.indexOf('-');
		if (pos >= 0) {
			cSymbol.setNumberLanguageAttribute(locale.substring(0, pos));
			cSymbol.setNumberCountryAttribute(locale.substring(pos + 1));
		} else {
			cSymbol.setNumberLanguageAttribute(locale);
		}
	}

	/**
	 * Set &lt;style:map&gt; for positive values to the given style .
	 * @param mapName the style  to map to
	 */
	public void setMapPositive(String mapName) {
		StyleMapElement map = new StyleMapElement((OdfFileDom) this.getOwnerDocument());
		map.setStyleApplyStyleNameAttribute(mapName);
		map.setStyleConditionAttribute("value()>0");
		this.appendChild(map);
	}

	/**
	 * Set &lt;style:map&gt; for negative values to the given style .
	 * @param mapName the style  to map to
	 */
	public void setMapNegative(String mapName) {
		StyleMapElement map = new StyleMapElement((OdfFileDom) this.getOwnerDocument());
		map.setStyleApplyStyleNameAttribute(mapName);
		map.setStyleConditionAttribute("value()<0");
		this.appendChild(map);
	}
}
