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

package org.odftoolkit.simple.text;

import java.util.Locale;

import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.Document.ScriptType;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.ParagraphProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.TextProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.TextLinePosition;

/**
 * This class provides functions to handle the style of a paragraph.
 * 
 * <p>
 * This class provides functions to handle the font settings, text alignment
 * settings and so on.
 * 
 * @since 0.6.5
 */
public class ParagraphStyleHandler extends DefaultStyleHandler {

	Paragraph mParagraph;
	TextParagraphElementBase mParaElement;

	TextProperties mTextProperties;
	TextProperties mWritableTextProperties;

	ParagraphProperties mParagraphProperties;
	ParagraphProperties mWritableParagraphProperties;

	ParagraphStyleHandler(Paragraph aParagraph) {
		super(aParagraph.getOdfElement());
		mParagraph = aParagraph;
		mParaElement = aParagraph.getOdfElement();
	}

	/**
	 * Return the country information for a specific script type
	 * <p>
	 * The country information in its parent style and default style will be
	 * taken into considered.
	 * <p>
	 * Null will be returned if there is no country information for this script
	 * type.
	 * 
	 * @param type
	 *            - script type
	 * @return the country information for a specific script type
	 */
	public String getCountry(ScriptType type) {
		String country = null;
		TextProperties textProperties = getTextPropertiesForRead();
		if (textProperties != null) {
			country = textProperties.getCountry(type);
		}
		if (country != null && country.length() > 0) {
			return country;
		}

		boolean isDefault = isUseDefaultStyle;
		OdfStyleBase parentStyle = null;
		if (!isDefault) {
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		}
		while ((!isDefault) && (parentStyle != null)) {
			TextProperties parentStyleSetting = TextProperties.getTextProperties(parentStyle);
			country = parentStyleSetting.getCountry(type);
			if (country != null && country.length() > 0) {
				return country;
			}
			if (parentStyle instanceof OdfDefaultStyle) {
				isDefault = true;
			} else {
				parentStyle = getParentStyle((OdfStyle) parentStyle);
			}
		}
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getParagraphDefaultStyle();
			TextProperties defaultStyleSetting = TextProperties.getTextProperties(defaultStyle);
			country = defaultStyleSetting.getCountry(type);
		}
		return country;
	}

	/**
	 * Return the font definition for a specific script type.
	 * <p>
	 * The font definition in its parent style and default style will be taken
	 * into considered.
	 * <p>
	 * A default font definition will be returned if there is no font definition
	 * for this script type at all.
	 * 
	 * @param type
	 *            - script type
	 * @return the font definition for a specific script type
	 */
	public Font getFont(ScriptType type) {
		// A font includes font family name, font style, font color, font size
		Font font = null;
		TextProperties textProperties = getTextPropertiesForRead();
		if (textProperties != null) {
			font = textProperties.getFont(type);
		} else {
			font = new Font(null, null, 0, (StyleTypeDefinitions.TextLinePosition) null);
		}

		if (font != null && font.getFamilyName() != null && font.getColor() != null && font.getSize() != 0
				&& font.getFontStyle() != null && font.getTextLinePosition() != null) {
			return font;
		}

		boolean isDefault = isUseDefaultStyle;
		OdfStyleBase parentStyle = null;
		if (!isDefault) {
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		}
		while ((!isDefault) && (parentStyle != null)) {
			TextProperties parentStyleSetting = TextProperties.getTextProperties(parentStyle);
			Font tempFont = parentStyleSetting.getFont(type);
			mergeFont(font, tempFont);
			if (font.getFamilyName() != null && font.getColor() != null && font.getSize() > 0
					&& font.getFontStyle() != null && font.getTextLinePosition() != null) {
				return font;
			}
			// continue to get parent properties
			if (parentStyle instanceof OdfDefaultStyle) {
				isDefault = true;
			} else {
				parentStyle = getParentStyle((OdfStyle) parentStyle);
			}
		}
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getParagraphDefaultStyle();
			if (defaultStyle == null) {
				defaultStyle = getParagraphDefaultStyle();
			}
			if (defaultStyle != null) {
				TextProperties defaultStyleSetting = TextProperties.getTextProperties(defaultStyle);
				Font tempFont = defaultStyleSetting.getFont(type);
				mergeFont(font, tempFont);
			}
		}
		if (font.getColor() == null) {
			font.setColor(Color.BLACK);
		}
		if (font.getFontStyle() == null) {
			font.setFontStyle(FontStyle.REGULAR);
		}
		if (font.getTextLinePosition() == null) {
			font.setTextLinePosition(TextLinePosition.REGULAR);
		}
		return font;
	}

	/**
	 * Return the language information for a specific script type
	 * <p>
	 * The language definition in its parent style and default style will be
	 * taken into considered.
	 * <p>
	 * Null will be returned if there is no language information for this script
	 * type at all.
	 * 
	 * @param type
	 *            - script type
	 * @return the language information for a specific script type
	 */
	public String getLanguage(ScriptType type) {
		String language = null;
		TextProperties textProperties = getTextPropertiesForRead();
		if (textProperties != null) {
			language = textProperties.getLanguage(type);
		}
		if (language != null && language.length() > 0) {
			return language;
		}
		boolean isDefault = isUseDefaultStyle;
		OdfStyleBase parentStyle = null;
		if (!isDefault) {
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		}
		while ((!isDefault) && (parentStyle != null)) {
			TextProperties parentStyleSetting = TextProperties.getTextProperties(parentStyle);
			language = parentStyleSetting.getLanguage(type);
			if (language != null && language.length() > 0) {
				return language;
			}
			if (parentStyle instanceof OdfDefaultStyle) {
				isDefault = true;
			} else {
				parentStyle = getParentStyle((OdfStyle) parentStyle);
			}
		}
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getParagraphDefaultStyle();
			TextProperties defaultStyleSetting = TextProperties.getTextProperties(defaultStyle);
			language = defaultStyleSetting.getLanguage(type);
		}
		return language;
	}

	/**
	 * Set the country information for a specific script type
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
	 * @see TextProperties#setCountry(String, Document.ScriptType)
	 * @see org.odftoolkit.simple.Document.ScriptType
	 */
	public void setCountry(String country, ScriptType type) {
		getTextPropertiesForWrite().setCountry(country, type);
	}

	/**
	 * Set the font definition. The locale information in font definition will
	 * be used to justify the script type.
	 * <p>
	 * If the parameter <code>font</code> is null, nothing will be happened.
	 * 
	 * @param font
	 *            font definition
	 */
	public void setFont(Font font) {
		getTextPropertiesForWrite().setFont(font);
	}

	/**
	 * Set the font definition. The locale information in font definition will
	 * be used to justify the script type.
	 * <p>
	 * If the parameter <code>font</code> is null, nothing will be happened.
	 * 
	 * @param font
	 *            font definition
	 */
	public void setFont(Font font, Locale language) {
		getTextPropertiesForWrite().setFont(font, language);
	}

	/**
	 * Set the language information for a specific script type
	 * <p>
	 * If the parameter <code>language</code> is null, the language information
	 * for this script type will be removed.
	 * 
	 * @param language
	 *            - the language information
	 * @param type
	 *            - script type
	 */
	public void setLanguage(String language, ScriptType type) {
		getTextPropertiesForWrite().setLanguage(language, type);
	}

	/**
	 * Set the horizontal alignment.
	 * <p>
	 * If the parameter <code>alignType</code> is null, the horizontal alignment
	 * setting will be removed.
	 * 
	 * @param alignType
	 *            the horizontal alignment
	 */
	public void setHorizontalAlignment(HorizontalAlignmentType alignType) {
		getParagraphPropertiesForWrite().setHorizontalAlignment(alignType);
	}

	/**
	 * Return the horizontal alignment.
	 * <p>
	 * The horizontal alignment in its parent style and default style will be
	 * taken into considered.
	 * <p>
	 * HorizontalAlignmentType.DEFAULT will be returned if there is no
	 * horizontal alignment setting.
	 * 
	 * @return the horizontal alignment; null if there is no horizontal
	 *         alignment setting.
	 */
	public HorizontalAlignmentType getHorizontalAlignment() {
		HorizontalAlignmentType tempAlign = null;
		ParagraphProperties properties = getParagraphPropertiesForRead();
		if (properties != null) {
			tempAlign = properties.getHorizontalAlignment();
		}
		if (tempAlign != null) {
			return tempAlign;
		}
		boolean isDefault = isUseDefaultStyle;
		// find in parent style definition
		OdfStyleBase parentStyle = null;
		if (!isDefault) {
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		}
		while ((!isDefault) && (parentStyle != null)) {
			ParagraphProperties parentStyleSetting = ParagraphProperties.getParagraphProperties(parentStyle);
			tempAlign = parentStyleSetting.getHorizontalAlignment();
			if (tempAlign != null) {
				return tempAlign;
			}
			if (parentStyle instanceof OdfDefaultStyle) {
				isDefault = true;
			} else {
				parentStyle = getParentStyle((OdfStyle) parentStyle);
			}
		}
		// find in default style definition
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getParagraphDefaultStyle();
			ParagraphProperties defaultStyleSetting = ParagraphProperties.getParagraphProperties(defaultStyle);
			tempAlign = defaultStyleSetting.getHorizontalAlignment();
		}
		// use default
		if (tempAlign == null) {
			return HorizontalAlignmentType.DEFAULT;
		}
		return tempAlign;
	}

	private OdfDefaultStyle getParagraphDefaultStyle() {
		return mDocument.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Paragraph);
	}

	private OdfStyleBase getParentStyle(OdfStyle aStyle) {
		String parentName = aStyle.getStyleParentStyleNameAttribute();
		if (parentName == null || parentName.length() == 0) {
			return null;
		}
		if (parentName.equals("Default")) {
			return getParagraphDefaultStyle();
		} else {
			return getStyleByName(parentName);
		}
	}

	private OdfStyle getStyleByName(String name) {
		OdfStyle styleElement = null;
		OdfOfficeAutomaticStyles styles = mParaElement.getAutomaticStyles();
		styleElement = styles.getStyle(name, OdfStyleFamily.Paragraph);
		if (styleElement == null) {
			styleElement = mDocument.getDocumentStyles().getStyle(name, OdfStyleFamily.Paragraph);
		}
		return styleElement;
	}

	private OdfStyleBase getCurrentUsedStyle() {
		if (mWritableStyleElement != null) {
			return mWritableStyleElement;
		} else {
			return mStyleElement;
		}
	}

	private void mergeFont(Font target, Font source) {
		// merge font
		if (target.getFamilyName() == null && source.getFamilyName() != null) {
			target.setFamilyName(source.getFamilyName());
		}
		if (target.getColor() == null && source.getColor() != null) {
			target.setColor(source.getColor());
		}
		if (target.getSize() == 0 && source.getSize() > 0) {
			target.setSize(source.getSize());
		}
		if (target.getFontStyle() == null && source.getFontStyle() != null) {
			target.setFontStyle(source.getFontStyle());
		}
		if (target.getTextLinePosition() == null && source.getTextLinePosition() != null) {
			target.setTextLinePosition(source.getTextLinePosition());
		}
	}
}
