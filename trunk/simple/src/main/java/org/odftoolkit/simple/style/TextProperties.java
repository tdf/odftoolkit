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

package org.odftoolkit.simple.style;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.fo.FoColorAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoCountryAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoFontSizeAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoFontStyleAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoFontWeightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoLanguageAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleCountryAsianAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleCountryComplexAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontNameAsianAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontNameComplexAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontSizeAsianAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontSizeComplexAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontStyleAsianAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontStyleComplexAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontWeightAsianAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleFontWeightComplexAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleLanguageAsianAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleLanguageComplexAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleTextLineThroughColorAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleTextLineThroughStyleAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleTextUnderlineColorAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleTextUnderlineStyleAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleTextUnderlineWidthAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.office.OfficeFontFaceDeclsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.w3c.dom.NodeList;

/**
 * This class represents text style settings.
 * <p>
 * In Open Document Format, there can be different font settings for different
 * script types.
 * <p>
 * This class provides methods to access font style, font size, font name and
 * etc for different script types.
 * <p>
 * Further functions will be provided, such as underline, shadow, background
 * color and etc.
 * 
 * <p>
 * This class is a corresponded high level class for element
 * "style:text-properties". It provides methods to access the attributes and
 * children of "style:text-properties".
 * 
 * @since 0.3
 */
public class TextProperties {

	// The corresponding attributes includes:
	// ---Westen---
	//
	// fo:background-color
	// fo:color
	// fo:country
	// fo:language
	// fo:font-family
	// fo:font-size
	// fo:font-style
	// fo:font-variant
	// fo:font-weight
	// fo:hyphenate
	// fo:hyphenation-push-char-count
	// fo:hyphenation-remain-char-count
	// fo:letter-spacing
	// fo:script
	// fo:text-shadow
	// fo:text-transform
	// style:font-style-name //not used
	//
	// style:text-emphasize
	// style:text-line-through-color
	// style:text-line-through-mode
	// style:text-line-through-style
	// style:text-line-through-text
	// style:text-line-through-text-style
	// style:text-line-through-type
	// style:text-line-through-width
	//
	// style:text-outline
	// style:text-underline-color
	// style:text-underline-mode
	// style:text-underline-style
	// style:text-underline-type
	// style:text-underline-width
	//
	// style:use-window-font-color
	// style:font-relief
	// style:font-name
	// style:font-charset
	// style:font-family-generic
	// style:font-pitch
	// style:script-type
	//
	// ----Asian---
	//
	// style:font-style-name-asian
	// style:font-weight-asian
	// style:language-asian
	// style:script-asian
	// style:font-size-asian
	// style:font-name-asian
	// style:country-asian
	// style:font-charset-asian
	// style:font-family-asian
	// style:rfc-language-tag-asian
	// style:font-pitch-asian
	// style:font-style-asian
	//
	// ----Complex----
	//
	// style:font-style-name-complex
	// style:font-weight-complex
	// style:language-complex
	// style:script-complex
	// style:font-size-complex
	// style:font-name-complex
	// style:country-complex
	// style:font-charset-complex
	// style:font-family-complex
	// style:rfc-language-tag-complex
	// style:font-style-complex
	// style:font-pitch-complex

	StyleTextPropertiesElement mElement;

	/*
	 * String familyName; //svg:font-family String fontName; //style:name @
	 * style:font-face StyleTypeDefinitions.SimpleFontStyle SimpleFontStyle;
	 * //fo:font-style, fo:font-weight int size; //fo:font-size Locale language;
	 * Color fontColor; //fo:color Color backgroundColor; //fo:background-color
	 * String overLining; //style:text-overline-style="solid"
	 * //style:text-overline-type="double" //style:text-overline-width="auto"
	 * //style:text-overline-color="#000000" String
	 * strikeThrough;//style:text-line-through-style="solid"
	 * style:text-line-through-type="double" String underLining;
	 * //style:text-underline-style="solid" style:text-underline-width="auto"
	 * style:text-underline-color="#00ccff" Color overLiningColor; Color
	 * underLiningColor; String emphasisMark;
	 * //style:text-emphasize="circle below",
	 * style:text-emphasize="accent above" String relief;
	 * //style:font-relief="embossed", style:font-relief="engraved" boolean
	 * isOutline; //style:text-outline="true" boolean isShadow;
	 * //fo:text-shadow="1pt 1pt"
	 */

	/**
	 * Create an instance of TextProperties
	 */
	protected TextProperties() {
	}

	/**
	 * Create an instance of TextProperties from an element
	 * <style:text-properties>
	 * 
	 * @param textProperties
	 *            - the element of style:text-properties
	 */
	protected TextProperties(StyleTextPropertiesElement textProperties) {
		mElement = textProperties;
	}

	/**
	 * Return the font style for western characters
	 * <p>
	 * Null will be returned if there is no font style setting for western
	 * characters
	 * 
	 * @return the font style
	 */
	public StyleTypeDefinitions.FontStyle getFontStyle() {

		String fontstyle = mElement.getFoFontStyleAttribute();
		String fontweight = mElement.getFoFontWeightAttribute();
		StyleTypeDefinitions.OdfFontStyle theFontType = StyleTypeDefinitions.OdfFontStyle.enumValueOf(fontstyle);
		StyleTypeDefinitions.OdfFontWeight theFontWeight = StyleTypeDefinitions.OdfFontWeight.enumValueOf(fontweight);

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.NORMAL
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.NORMAL)
			return StyleTypeDefinitions.FontStyle.REGULAR;

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.ITALIC
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.NORMAL)
			return StyleTypeDefinitions.FontStyle.ITALIC;

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.ITALIC
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.BOLD)
			return StyleTypeDefinitions.FontStyle.BOLDITALIC;

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.NORMAL
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.BOLD)
			return StyleTypeDefinitions.FontStyle.BOLD;

		return null;
	}
	
	/**
	 * Return the font text line style
	 * <p>
	 * TextLineStyle.REGULAR will be returned if there is no text line style setting
	 * 
	 * @return the font style
	 */
	public StyleTypeDefinitions.TextLinePosition getTextLineStyle() {
		String throughLine = mElement.getStyleTextLineThroughStyleAttribute();
		String underLine = mElement.getStyleTextUnderlineStyleAttribute();
		if (throughLine ==null && underLine == null)
			return StyleTypeDefinitions.TextLinePosition.REGULAR;
		if (throughLine !=null && underLine == null)
			return StyleTypeDefinitions.TextLinePosition.THROUGH;
		if (throughLine !=null && underLine != null)
			return StyleTypeDefinitions.TextLinePosition.THROUGHUNDER;
		if (throughLine ==null && underLine != null)
			return StyleTypeDefinitions.TextLinePosition.UNDER;
		return null;
	}

	/**
	 * Return the font style for a specific script type
	 * <p>
	 * REGULAR will be returned if there is no font style setting for this
	 * script type.
	 * 
	 * @param type
	 *            - script type
	 * @return the font style for a specific script type
	 */
	public StyleTypeDefinitions.FontStyle getFontStyle(Document.ScriptType type) {
		if (type == null)
			type = Document.ScriptType.WESTERN;
		String fontstyle = null, fontweight = null;
		switch (type) {
		case WESTERN:
			fontstyle = mElement.getFoFontStyleAttribute();
			fontweight = mElement.getFoFontWeightAttribute();
			break;
		case CJK:
			fontstyle = mElement.getStyleFontStyleAsianAttribute();
			fontweight = mElement.getStyleFontWeightAsianAttribute();
			break;
		case CTL:
			fontstyle = mElement.getStyleFontStyleComplexAttribute();
			fontweight = mElement.getStyleFontWeightComplexAttribute();
			break;
		}
		StyleTypeDefinitions.OdfFontStyle theFontType = StyleTypeDefinitions.OdfFontStyle.enumValueOf(fontstyle);
		StyleTypeDefinitions.OdfFontWeight theFontWeight = StyleTypeDefinitions.OdfFontWeight.enumValueOf(fontweight);

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.NORMAL
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.NORMAL)
			return StyleTypeDefinitions.FontStyle.REGULAR;

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.ITALIC
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.NORMAL)
			return StyleTypeDefinitions.FontStyle.ITALIC;

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.ITALIC
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.BOLD)
			return StyleTypeDefinitions.FontStyle.BOLDITALIC;

		if (theFontType == StyleTypeDefinitions.OdfFontStyle.NORMAL
				&& theFontWeight == StyleTypeDefinitions.OdfFontWeight.BOLD)
			return StyleTypeDefinitions.FontStyle.BOLD;

		return null;
	}

	/**
	 * Set the font style for western characters
	 * <p>
	 * If the parameter <code>style</code> is REGULAR, the font style setting
	 * for western characters will be removed.
	 * 
	 * @param style
	 *            - the font style
	 */
	public void setFontStyle(StyleTypeDefinitions.FontStyle style) {
		switch (style) {
		case BOLD:
			mElement.removeAttribute(FoFontStyleAttribute.ATTRIBUTE_NAME.getQName());
			mElement.setFoFontWeightAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
			break;
		case ITALIC:
			mElement.setFoFontStyleAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
			mElement.removeAttribute(FoFontWeightAttribute.ATTRIBUTE_NAME.getQName());
			break;
		case BOLDITALIC:
			mElement.setFoFontStyleAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
			mElement.setFoFontWeightAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
			break;
		case REGULAR:
			mElement.removeAttribute(FoFontStyleAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(FoFontWeightAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	/**
	 * Set the font text line style for characters
	 * <p>
	 * If the parameter <code>style</code> is REGULAR, the font text line style
	 * setting for characters will be removed.
	 * 
	 * @param style
	 *            - the font text line style
	 */
	public void setTextLineStyle(StyleTypeDefinitions.TextLinePosition style) {
		switch (style) {
		case THROUGH:
			mElement.setStyleTextLineThroughStyleAttribute(StyleTypeDefinitions.LineStyle.SOLID.toString());
			mElement.setStyleTextLineThroughColorAttribute("font-color");
			break;
		case UNDER:
			mElement.setStyleTextUnderlineStyleAttribute(StyleTypeDefinitions.LineStyle.SOLID.toString());
			mElement.setStyleTextUnderlineWidthAttribute("auto");
			mElement.setStyleTextUnderlineColorAttribute("font-color");
			break;
		case THROUGHUNDER:
			mElement.setStyleTextLineThroughStyleAttribute(StyleTypeDefinitions.LineStyle.SOLID.toString());
			mElement.setStyleTextLineThroughColorAttribute("font-color");
			mElement.setStyleTextUnderlineStyleAttribute(StyleTypeDefinitions.LineStyle.SOLID.toString());
			mElement.setStyleTextUnderlineWidthAttribute("auto");
			mElement.setStyleTextUnderlineColorAttribute("font-color");
			break;
		case REGULAR:
			mElement.removeAttribute(StyleTextLineThroughStyleAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleTextLineThroughColorAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleTextUnderlineStyleAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleTextUnderlineWidthAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleTextUnderlineColorAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	/**
	 * Set the font style for a specific script type
	 * <p>
	 * If the parameter <code>style</code> is REGULAR, the font style setting
	 * for this script type will be removed.
	 * 
	 * @param style
	 *            - font style
	 * @param type
	 *            - script type
	 */
	public void setFontStyle(StyleTypeDefinitions.FontStyle style, Document.ScriptType type) {
		if (type == null)
			type = Document.ScriptType.WESTERN;
		switch (type) {
		case WESTERN:
			switch (style) {
			case BOLD:
				mElement.removeAttribute(FoFontStyleAttribute.ATTRIBUTE_NAME.getQName());
				mElement.setFoFontWeightAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
				break;
			case ITALIC:
				mElement.setFoFontStyleAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
				mElement.removeAttribute(FoFontWeightAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case BOLDITALIC:
				mElement.setFoFontStyleAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
				mElement.setFoFontWeightAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
				break;
			case REGULAR:
				mElement.removeAttribute(FoFontStyleAttribute.ATTRIBUTE_NAME.getQName());
				mElement.removeAttribute(FoFontWeightAttribute.ATTRIBUTE_NAME.getQName());
			}
			break;
		case CJK:
			switch (style) {
			case BOLD:
				mElement.removeAttribute(StyleFontStyleAsianAttribute.ATTRIBUTE_NAME.getQName());
				mElement.setStyleFontWeightAsianAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
				break;
			case ITALIC:
				mElement.setStyleFontStyleAsianAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
				mElement.removeAttribute(StyleFontWeightAsianAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case BOLDITALIC:
				mElement.setStyleFontStyleAsianAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
				mElement.setStyleFontWeightAsianAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
				break;
			case REGULAR:
				mElement.removeAttribute(StyleFontStyleAsianAttribute.ATTRIBUTE_NAME.getQName());
				mElement.removeAttribute(StyleFontWeightAsianAttribute.ATTRIBUTE_NAME.getQName());
			}
			break;
		case CTL:
			switch (style) {
			case BOLD:
				mElement.removeAttribute(StyleFontStyleComplexAttribute.ATTRIBUTE_NAME.getQName());
				mElement.setStyleFontWeightComplexAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
				break;
			case ITALIC:
				mElement.setStyleFontStyleComplexAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
				mElement.removeAttribute(StyleFontWeightComplexAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case BOLDITALIC:
				mElement.setStyleFontStyleComplexAttribute(StyleTypeDefinitions.OdfFontStyle.ITALIC.toString());
				mElement.setStyleFontWeightComplexAttribute(StyleTypeDefinitions.OdfFontWeight.BOLD.toString());
				break;
			case REGULAR:
				mElement.removeAttribute(StyleFontStyleComplexAttribute.ATTRIBUTE_NAME.getQName());
				mElement.removeAttribute(StyleFontWeightComplexAttribute.ATTRIBUTE_NAME.getQName());
			}
			break;
		}
	}

	/**
	 * Return the font size definition in measurement point(PT) for western
	 * characters.
	 * <p>
	 * Zero will be returned if there is no font size definition for western
	 * characters.
	 * <p>
	 * Zero will be returned if the line measurement is not point(PT).
	 * 
	 * @return the font size in measurement point(PT)
	 */
	public double getFontSizeInPoint() {
		String fontsize = mElement.getFoFontSizeAttribute();
		if (fontsize.endsWith("pt")) {
			fontsize = fontsize.substring(0, fontsize.length() - 2);
			double iSize;
			try {
				iSize = Double.parseDouble(fontsize.trim());
			} catch (Exception e) {
				iSize = 0;
			}
			return iSize;
		}
		return 0;
	}

	/**
	 * Return the font size definition in measurement point(PT) for a specific
	 * script type.
	 * <p>
	 * Zero will be returned if there is no font size definition for this script
	 * type.
	 * <p>
	 * Zero will be returned if the line measurement is not point(PT).
	 * 
	 * @param type
	 *            - script type
	 * @return the font size in measurement point(PT)
	 */
	public double getFontSizeInPoint(Document.ScriptType type) {
		String fontsize = "";
		switch (type) {
		case WESTERN:
			fontsize = mElement.getFoFontSizeAttribute();
			break;
		case CJK:
			fontsize = mElement.getStyleFontSizeAsianAttribute();
			break;
		case CTL:
			fontsize = mElement.getStyleFontSizeComplexAttribute();
			break;
		}
		if (fontsize != null && fontsize.endsWith("pt")) {
			fontsize = fontsize.substring(0, fontsize.length() - 2);
			double iSize;
			try {
				iSize = Double.parseDouble(fontsize.trim());
			} catch (Exception e) {
				iSize = 0;
			}
			return iSize;
		}
		return 0;
	}

	/**
	 * Set the font size in measurement point(PT) for western characters.
	 * <p>
	 * If the font size is less than zero, the font size definition for western
	 * characters will be removed.
	 * 
	 * @param size
	 *            - font size
	 */
	public void setFontSizeInPoint(double size) {
		setFontSizeInPoint(size, Document.ScriptType.WESTERN);
	}

	/**
	 * Set the font size in measurement point(PT) for a specific script type.
	 * <p>
	 * If the font size is less than zero, the font size definition for this
	 * script type will be removed.
	 * 
	 * @param size
	 *            - font size
	 * @param type
	 *            - script type
	 */
	public void setFontSizeInPoint(double size, Document.ScriptType type) {
		if (size < 0) {
			switch (type) {
			case WESTERN:
				mElement.removeAttribute(FoFontSizeAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CJK:
				mElement.removeAttribute(StyleFontSizeAsianAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CTL:
				mElement.removeAttribute(StyleFontSizeComplexAttribute.ATTRIBUTE_NAME.getQName());
				break;
			}
			return;
		}
		switch (type) {
		case WESTERN:
			mElement.setFoFontSizeAttribute(size + "pt");
			break;
		case CJK:
			mElement.setStyleFontSizeAsianAttribute(size + "pt");
			break;
		case CTL:
			mElement.setStyleFontSizeComplexAttribute(size + "pt");
			break;
		}
	}

	/**
	 * Return the language information for western characters.
	 * <p>
	 * Null will be returned if there is no language information for western
	 * characters.
	 * 
	 * @return the language information for western characters.
	 */
	public String getLanguage() {
		return getLanguage(Document.ScriptType.WESTERN);
	}

	/**
	 * Return the language information for a specific script type
	 * <p>
	 * Null will be returned if there is no language information for this script
	 * type.
	 * 
	 * @param type
	 *            - script type
	 * @return the language information for a specific script type
	 */
	public String getLanguage(Document.ScriptType type) {
		switch (type) {
		case WESTERN:
			return mElement.getFoLanguageAttribute();
		case CJK:
			return mElement.getStyleLanguageAsianAttribute();
		case CTL:
			return mElement.getStyleLanguageComplexAttribute();
		}
		return null;
	}

	/**
	 * Set the language information for western characters.
	 * <p>
	 * If the parameter <code>language</code> is null, the language information
	 * for western characters will be removed.
	 * 
	 * @param language
	 *            - the language information
	 */
	public void setLanguage(String language) {
		setLanguage(language, Document.ScriptType.WESTERN);
	}

	/**
	 * Set the language information for a specific script type
	 * <p>
	 * The consistency between country and script type is not verified.
	 * <p>
	 * If the parameter <code>language</code> is null, the language information
	 * for this script type will be removed.
	 * 
	 * @param language
	 *            - the language information
	 * @param type
	 *            - script type
	 */
	public void setLanguage(String language, Document.ScriptType type) {
		if (language == null) {
			switch (type) {
			case WESTERN:
				mElement.removeAttribute(FoLanguageAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CJK:
				mElement.removeAttribute(StyleLanguageAsianAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CTL:
				mElement.removeAttribute(StyleLanguageComplexAttribute.ATTRIBUTE_NAME.getQName());
				break;
			}
			return;
		}
		switch (type) {
		case WESTERN:
			mElement.setFoLanguageAttribute(language);
			break;
		case CJK:
			mElement.setStyleLanguageAsianAttribute(language);
			break;
		case CTL:
			mElement.setStyleLanguageComplexAttribute(language);
			break;
		}
	}

	/**
	 * Return the country information for western characters.
	 * <p>
	 * Null will be returned if there is no country information for western
	 * characters.
	 * 
	 * @return the country information for western characters.
	 */
	public String getCountry() {
		return getCountry(Document.ScriptType.WESTERN);
	}

	/**
	 * Return the country information for a specific script type
	 * <p>
	 * Null will be returned if there is no country information for this script
	 * type.
	 * 
	 * @param type
	 *            - script type
	 * @return the country information for a specific script type
	 */
	public String getCountry(Document.ScriptType type) {
		switch (type) {
		case WESTERN:
			return mElement.getFoCountryAttribute();
		case CJK:
			return mElement.getStyleCountryAsianAttribute();
		case CTL:
			return mElement.getStyleCountryComplexAttribute();
		}
		return null;
	}

	/**
	 * Set the country information for western character.
	 * <p>
	 * If the parameter <code>country</code> is null, the country information
	 * for western character will be removed.
	 * 
	 * @param country
	 *            - the country information
	 */
	public void setCountry(String country) {
		setCountry(country, Document.ScriptType.WESTERN);
	}

	/**
	 * Set the country information for a specific script type.
	 * <p>
	 * The consistency between country and script type is not verified.
	 * <p>
	 * If the parameter <code>country</code> is null, the country information
	 * for this script type will be removed.
	 * 
	 * @param country
	 *            - the country information
	 * @param type
	 *            - script type
	 */
	public void setCountry(String country, Document.ScriptType type) {
		if (country == null) {
			switch (type) {
			case WESTERN:
				mElement.removeAttribute(FoCountryAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CJK:
				mElement.removeAttribute(StyleCountryAsianAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CTL:
				mElement.removeAttribute(StyleCountryComplexAttribute.ATTRIBUTE_NAME.getQName());
				break;
			}
			return;
		}
		switch (type) {
		case WESTERN:
			mElement.setFoCountryAttribute(country);
			break;
		case CJK:
			mElement.setStyleCountryAsianAttribute(country);
			break;
		case CTL:
			mElement.setStyleCountryComplexAttribute(country);
			break;
		}
	}

	/**
	 * Return the font color.
	 * <p>
	 * Null will be returned if there is no font color setting.
	 * 
	 * @return the font color
	 */
	public Color getFontColor() {
		String color = mElement.getFoColorAttribute();
		if (color != null)
			return Color.valueOf(mElement.getFoColorAttribute());
		else
			return null;
	}

	/**
	 * Set the font color.
	 * <p>
	 * If the parameter <code>fontColor</code> is null, the font color
	 * definition will be removed.
	 * 
	 * @param fontColor
	 *            - the font color
	 */
	public void setFontColor(Color fontColor) {
		if (fontColor == null)
			mElement.removeAttribute(FoColorAttribute.ATTRIBUTE_NAME.getQName());
		else
			mElement.setFoColorAttribute(fontColor.toString());
	}

	/**
	 * Return the font name for western characters.
	 * <p>
	 * Null will be returned if there is no font name setting for western
	 * characters.
	 * 
	 * @return the font style for western characters
	 */
	public String getFontName() {
		return mElement.getStyleFontNameAttribute();
	}

	/**
	 * Return the font name for a specific script type
	 * <p>
	 * Null will be returned if there is no font name setting for this script
	 * type.
	 * 
	 * @param type
	 *            - script type
	 * @return the font name for a specific script type
	 */
	public String getFontName(Document.ScriptType type) {
		if (type == null)
			return null;
		switch (type) {
		case WESTERN:
			return mElement.getStyleFontNameAttribute();
		case CJK:
			return mElement.getStyleFontNameAsianAttribute();
		case CTL:
			return mElement.getStyleFontNameComplexAttribute();
		}
		return null;
	}

	/**
	 * Set the font name for western characters.
	 * <p>
	 * If the parameter <code>fontName</code> is null, the font name for western
	 * characters will be removed.
	 * 
	 * @param fontName
	 *            - font name
	 */
	public void setFontName(String fontName) {
		mElement.setStyleFontNameAttribute(fontName);
	}

	/**
	 * Set the font name for a specific script type
	 * <p>
	 * If the parameter <code>fontName</code> is null, the font name for this
	 * script type will be removed.
	 * 
	 * @param fontName
	 *            - font name
	 * @param type
	 *            - script type
	 */
	public void setFontName(String fontName, Document.ScriptType type) {
		if (fontName == null) {
			switch (type) {
			case WESTERN:
				mElement.removeAttribute(StyleFontNameAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CJK:
				mElement.removeAttribute(StyleFontNameAsianAttribute.ATTRIBUTE_NAME.getQName());
				break;
			case CTL:
				mElement.removeAttribute(StyleFontNameComplexAttribute.ATTRIBUTE_NAME.getQName());
				break;
			}
			return;
		}
		switch (type) {
		case WESTERN:
			mElement.setStyleFontNameAttribute(fontName);
			break;
		case CJK:
			mElement.setStyleFontNameAsianAttribute(fontName);
			break;
		case CTL:
			mElement.setStyleFontNameComplexAttribute(fontName);
			break;
		}
	}

	/**
	 * Return the font definition for western characters.
	 * <p>
	 * Null will be returned if there is no font definition for western
	 * characters.
	 * 
	 * @return the font definition for western characters
	 */
	public Font getFont() {
		return getFont(Document.ScriptType.WESTERN);
	}

	/**
	 * Return the font definition for a specific script type
	 * <p>
	 * Null will be returned if there is no font definition for this script
	 * type.
	 * 
	 * @param type
	 *            - script type
	 * @return the font definition for a specific script type
	 */
	public Font getFont(Document.ScriptType type) {
		// get Font family name
		String fontName = getFontName(type);
		String familyName = null;
		if (fontName != null)
			familyName = getFontFamilyNameFromFontName(fontName);
		else
			familyName = getFontFamilyName(type);
		double size = getFontSizeInPoint(type);
		StyleTypeDefinitions.FontStyle fontStyle = getFontStyle(type);
		StyleTypeDefinitions.TextLinePosition lineStyle = getTextLineStyle();
		Font aFont = new Font(familyName, fontStyle, size, lineStyle);

		Color color = getFontColor();
		// String language = getLanguage(type);
		// String country = getCountry(type);

		if (color != null)
			aFont.setColor(color);

		// Commented since 0.3.5 because the font won't contain the language
		// information
		// if (language != null && country != null) {
		// Locale locale = new Locale(language, country);
		// aFont.setLocale(locale);
		// }
		return aFont;

	}

	/**
	 * Set the font definition for western character.
	 * <p>
	 * If the parameter <code>font</code> is null, nothing will be happened.
	 * 
	 * @param font
	 *            - font definition
	 */
	public void setFont(Font font) {
		if (font == null)
			return;

		setFont(font, Document.ScriptType.WESTERN, null);
		if (font.getColor() != null)
			setFontColor(font.getColor());
	}

	/**
	 * Set the font definition.
	 * <p>
	 * This method can be used to set font for different script type, such as
	 * western characters, CJK characters, and CTL characters. The second
	 * parameter will be used to determine the script type.
	 * <p>
	 * If the parameter <code>font</code> is null, nothing will be happened.
	 * 
	 * @param font
	 *            - font definition
	 * @param language
	 *            - the language
	 * @see org.odftoolkit.simple.Document.ScriptType
	 * 
	 */
	public void setFont(Font font, Locale language) {
		if (font == null)
			return;

		if (language == null) {
			setFont(font, Document.ScriptType.WESTERN, null);
		} else
			setFont(font, Document.getScriptType(language), language);
		if (font.getColor() != null)
			setFontColor(font.getColor());
	}

	/**
	 * Return the font family name for a specific script type
	 * <p>
	 * Null will be returned if there is no font family name definition for this
	 * script type.
	 * 
	 * @param type
	 *            - script type
	 * @return the font family name for a specific script type
	 */
	public String getFontFamilyName(Document.ScriptType type) {
		switch (type) {
		case WESTERN:
			return mElement.getFoFontFamilyAttribute();
		case CJK:
			return mElement.getStyleFontFamilyAsianAttribute();
		case CTL:
			return mElement.getStyleFontFamilyComplexAttribute();
		}
		return null;
	}

	private String getFontFamilyNameFromFontName(String aFontName) {
		try {
			// try if the font has been defined.
			Document mDocument = ((Document) ((OdfFileDom) mElement.getOwnerDocument()).getDocument());
			// find <office:font-face-decls> in content dom
			OdfContentDom contentDom = mDocument.getContentDom();
			OfficeFontFaceDeclsElement fontfaceDecls = OdfElement.findFirstChildNode(OfficeFontFaceDeclsElement.class,
					contentDom.getRootElement());
			if (fontfaceDecls == null) {
				// find <office:font-face-decls> in style dom
				OdfStylesDom styleDom = mDocument.getStylesDom();
				fontfaceDecls = OdfElement.findFirstChildNode(OfficeFontFaceDeclsElement.class, styleDom
						.getRootElement());
			}
			if (fontfaceDecls == null)
				return null;

			NodeList list = fontfaceDecls.getElementsByTagName("style:font-face");
			for (int i = 0; i < list.getLength(); i++) {
				StyleFontFaceElement node = (StyleFontFaceElement) list.item(i);
				String familyname = node.getSvgFontFamilyAttribute();
				String fontName = node.getStyleNameAttribute();
				if (aFontName.equals(fontName)) {
					return familyname;
				}
			}
		} catch (Exception e) {
			Logger.getLogger(TextProperties.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	private String getFontNameFromFamilyName(String aFamilyName) {
		String aFontName = aFamilyName;
		boolean duplicated = false;
		try {
			// try if the font has been defined.
			Document mDocument = ((Document) ((OdfFileDom) mElement.getOwnerDocument()).getDocument());
			// find <office:font-face-decls> in content dom
			OdfContentDom contentDom = mDocument.getContentDom();
			OfficeFontFaceDeclsElement fontfaceDecls = OdfElement.findFirstChildNode(OfficeFontFaceDeclsElement.class,
					contentDom.getRootElement());
			if (fontfaceDecls == null) {
				// find <office:font-face-decls> in style dom
				OdfStylesDom styleDom = mDocument.getStylesDom();
				fontfaceDecls = OdfElement.findFirstChildNode(OfficeFontFaceDeclsElement.class, styleDom
						.getRootElement());
				if (fontfaceDecls == null)
					fontfaceDecls = contentDom.getRootElement().newOfficeFontFaceDeclsElement();
			}

			NodeList list = fontfaceDecls.getElementsByTagName("style:font-face");
			for (int i = 0; i < list.getLength(); i++) {
				StyleFontFaceElement node = (StyleFontFaceElement) list.item(i);
				String familyname = node.getSvgFontFamilyAttribute();
				String fontName = node.getStyleNameAttribute();
				if (aFamilyName.equals(familyname)) {
					return fontName;
				}
				if (aFontName.equals(fontName)) {
					duplicated = true;
				}
			}

			// Get a font name
			while (duplicated) {
				duplicated = false;
				aFontName = aFontName + "_1";
				for (int i = 0; i < list.getLength(); i++) {
					StyleFontFaceElement node = (StyleFontFaceElement) list.item(i);
					String fontName = node.getStyleNameAttribute();
					if (aFontName.equals(fontName)) {
						duplicated = true;
						break;
					}
				}
			}

			StyleFontFaceElement newfont = fontfaceDecls.newStyleFontFaceElement(aFontName);
			newfont.setSvgFontFamilyAttribute(aFamilyName);
			return aFontName;
		} catch (Exception e) {
			Logger.getLogger(TextProperties.class.getName()).log(Level.SEVERE,	e.getMessage(), e);
		}
		return null;

	}

	private void setFont(Font font, Document.ScriptType type, Locale locale) {
		// get font name by font family name
		String fontName = getFontNameFromFamilyName(font.getFamilyName());
		if (fontName == null)
			return;
		font.setFontName(fontName);
		switch (type) {
		case WESTERN:
			setFontName(font.getFontName(), Document.ScriptType.WESTERN);
			setFontStyle(font.getFontStyle(), Document.ScriptType.WESTERN);
			setFontSizeInPoint(font.getSize(), Document.ScriptType.WESTERN);
			if (locale != null) {
				setLanguage(locale.getLanguage(), Document.ScriptType.WESTERN);
				setCountry(locale.getCountry(), Document.ScriptType.WESTERN);
			}
			break;
		case CJK:
			setFontName(font.getFontName(), Document.ScriptType.CJK);
			setFontStyle(font.getFontStyle(), Document.ScriptType.CJK);
			setFontSizeInPoint(font.getSize(), Document.ScriptType.CJK);
			if (locale != null) {
				setLanguage(locale.getLanguage(), Document.ScriptType.CJK);
				setCountry(locale.getCountry(), Document.ScriptType.CJK);
			}
			break;
		case CTL:
			setFontName(font.getFontName(), Document.ScriptType.CTL);
			setFontStyle(font.getFontStyle(), Document.ScriptType.CTL);
			setFontSizeInPoint(font.getSize(), Document.ScriptType.CTL);
			if (locale != null) {
				setLanguage(locale.getLanguage(), Document.ScriptType.CTL);
				setCountry(locale.getCountry(), Document.ScriptType.CTL);
			}
			break;
		}
		setTextLineStyle(font.getTextLinePosition());
	}

	/**
	 * Return an instance of
	 * <code>TextProperties</p> to represent the "style:text-properties" in a style element.
	 * <p>If there is no "style:text-properties" defined in the style element, a new "style:text-properties" element will be created.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>TextProperties</p>
	 */
	public static TextProperties getOrCreateTextProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TextProperties);
		return new TextProperties((StyleTextPropertiesElement) properties);
	}

	/**
	 * Return an instance of
	 * <code>TextProperties</p> to represent the "style:text-properties" in a style element.
	 * <p>If there is no "style:text-properties" defined in the style element, null will be returned.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>TextProperties</p>;Null if there is no
	 *         "style:text-properties" defined
	 */
	public static TextProperties getTextProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style.getPropertiesElement(OdfStylePropertiesSet.TextProperties);
		if (properties != null)
			return new TextProperties((StyleTextPropertiesElement) properties);
		else
			return null;
	}

	// public void setBackgroundColor(Color bkColor)
	// {
	//		
	// }
	//	
	// public Color getBackgroundColor()
	// {
	// return null;
	// }
	//	
	// public void setIsShadow(boolean shadow)
	// {
	//		
	// }
	//	
	// public boolean isShadow()
	// {
	// return false;
	// }

}
