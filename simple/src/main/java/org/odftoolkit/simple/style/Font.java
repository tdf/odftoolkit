/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple.style;

import java.util.Locale;

import org.odftoolkit.odfdom.type.Color;

/**
 * This class represents the font style settings, including font family name,
 * font style, size, front color and locale information.
 * 
 * @since 0.3
 */
public class Font {

	String familyName; // svg:font-family
	String fontName; // style:name @ <style:font-face>
	StyleTypeDefinitions.SimpleFontStyle simpleFontStyle; // fo:font-style,
	// fo:font-weight
	double size; // fo:font-size
	Locale language;
	Color color;

	/**
	 * Create a font with specific family name, style and size.
	 * 
	 * @param familyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.SimpleFontStyle simpleFontStyle, double fontSize) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.size = fontSize;
		language = null;
	}

	/**
	 * Create a font with specific family name, style, size and front color.
	 * 
	 * @param familyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 * @param color
	 *            - the front color
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.SimpleFontStyle simpleFontStyle, double fontSize,
			Color color) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.size = fontSize;
		language = null;
		this.color = color;
	}

	/**
	 * Create a font with specific family name, style, size for a specific
	 * character. For example, a font style setting for English character.
	 * 
	 * @param familyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the font size
	 * @param language
	 *            - the character information
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.SimpleFontStyle simpleFontStyle, double fontSize,
			Locale language) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.size = fontSize;
		this.language = language;
	}

	/**
	 * Create a font with specific family name, style, size and color for a
	 * specific character. For example, a font style setting for English
	 * character.
	 * 
	 * @param familyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 * @param color
	 *            - the front color
	 * @param language
	 *            - the character information
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.SimpleFontStyle simpleFontStyle, double fontSize,
			Color color, Locale language) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.size = fontSize;
		this.language = language;
		this.color = color;
	}

	/**
	 * Return the font family
	 * 
	 * @return the font family
	 */
	public String getFamilyName() {
		return familyName;
	}

	/**
	 * Set the font family.
	 * <p>
	 * Font family name is what you get from the font list in ODF editors.
	 * 
	 * @param familyName
	 *            - the font family name
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	/**
	 * Return the font name
	 * <p>
	 * Note the font name might be different from font family name. A font
	 * family may have different font name definitions.
	 * 
	 * @return the font name
	 */
	protected String getFontName() {
		return fontName;
	}

	/**
	 * Set the font name
	 * <p>
	 * Note the font name might be different from font family name. A font
	 * family may have different font name definitions.
	 * 
	 * @param fontName
	 *            - the font name
	 */
	protected void setFontName(String fontName) {
		this.fontName = fontName;
	}

	/**
	 * Get the font style
	 * 
	 * @return the font style
	 */
	public StyleTypeDefinitions.SimpleFontStyle getSimpleFontStyle() {
		return simpleFontStyle;
	}

	/**
	 * Set the font style
	 * 
	 * @param simpleFontStyle
	 *            - the font style
	 */
	public void setSimpleFontStyle(StyleTypeDefinitions.SimpleFontStyle simpleFontStyle) {
		this.simpleFontStyle = simpleFontStyle;
	}

	/**
	 * Return the font size in measurement point(PT).
	 * 
	 * @return - the font size in measurement point(PT)
	 */
	public double getSize() {
		return size;
	}

	/**
	 * Set the font size. The font size is in measurement point(PT).
	 * 
	 * @param size
	 *            - the font size
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * Return the character information, which the font setting will affect.
	 * 
	 * @return the character information
	 */
	public Locale getLocale() {
		return language;
	}

	/**
	 * Set the character information, which the font setting will affect.
	 * 
	 * @param language
	 *            - the character information
	 */
	public void setLocale(Locale language) {
		this.language = language;
	}

	/**
	 * Return the font color
	 * 
	 * @return the font color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the font color
	 * 
	 * @param color
	 *            - the font color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "FamilyName:" + getFamilyName() + ";" + "Style:" + getSimpleFontStyle() + ";" + "Size:" + getSize();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Font) {
			Font f = (Font) o;
			if (!this.getFamilyName().equals(f.getFamilyName()))
				return false;
			if (!(Math.abs(this.getSize() - f.getSize()) < 0.005))
				return false;
			if (!(this.getSimpleFontStyle() == f.getSimpleFontStyle()))
				return false;
			// if ((this.getLocale() != null && f.getLocale() == null)
			// || (this.getLocale() == null && f.getLocale() != null))
			// return false;
			// if ((this.getLocale() != null && f.getLocale() != null)
			// && (!this.getLocale().equals(f.getLocale())))
			// return false;
			if ((this.getColor() != null && f.getColor() == null)
					|| (this.getColor() == null && f.getColor() != null))
				return false;
			if ((this.getColor() != null && f.getColor() != null)
					&& (!this.getColor().toString().equals(f.getColor().toString())))
				return false;
			return true;
		}
		return false;
	}
}
