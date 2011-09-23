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

import org.odftoolkit.odfdom.type.Color;

/**
 * This class represents the font style settings, including font family name,
 * font style, size, front color.
 * 
 * @since 0.3
 */
public class Font {

	String familyName; // svg:font-family
	String fontName; // style:name @ <style:font-face>
	StyleTypeDefinitions.FontStyle simpleFontStyle; // fo:font-style,
	StyleTypeDefinitions.TextLinePosition textLinePosition; //style:text-line-through-style and style:text-underline-style
	// fo:font-weight
	double size; // fo:font-size
	// Locale language;
	Color color;

	/**
	 * Create a font with specific family name, font style, and size.
	 * 
	 * @param fontFamilyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = StyleTypeDefinitions.TextLinePosition.REGULAR;
		this.size = fontSize;
	}

	/**
	 * Create a font with specific family name, font style, size and line position.
	 * 
	 * @param fontFamilyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 * @param textLinePosition
	 * 			  - the line position
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize, StyleTypeDefinitions.TextLinePosition textLinePosition) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = textLinePosition;
		this.size = fontSize;
	}

	/**
	 * Create a font with specific family name, style, size, and front color.
	 * 
	 * @param fontFamilyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 * @param color
	 *            - the front color
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize, Color color) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = StyleTypeDefinitions.TextLinePosition.REGULAR;
		this.size = fontSize;
		this.color = color;
	}

	/**
	 * Create a font with specific family name, style, size, front color and line position
	 * 
	 * @param fontFamilyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 * @param color
	 *            - the front color
	 * @param textLinePosition
	 * 			  - the line position
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize, Color color, StyleTypeDefinitions.TextLinePosition textLinePosition) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = textLinePosition;
		this.size = fontSize;
		this.color = color;
	}

	/**
	 * Create a font with specific family name, style, size for a specific
	 * character. For example, a font style setting for English character.
	 * 
	 * @param fontFamilyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the font size
	 * @param language
	 *            - the character information
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize, Locale language) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = StyleTypeDefinitions.TextLinePosition.REGULAR;
		this.size = fontSize;
	}

	/**
	 * Create a font with specific family name, style, size, and color for a
	 * specific character. For example, a font style setting for English
	 * character.
	 * 
	 * @param fontFamilyName
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
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize, Color color, Locale language) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = StyleTypeDefinitions.TextLinePosition.REGULAR;
		this.size = fontSize;
		this.color = color;
	}
	
	/**
	 * Create a font with specific family name, style, size, color, and line position for a
	 * specific character. For example, a font style setting for English
	 * character.
	 * 
	 * @param fontFamilyName
	 *            - the family name
	 * @param simpleFontStyle
	 *            - the font style
	 * @param fontSize
	 *            - the size
	 * @param color
	 *            - the front color
	 * @param textLinePosition
	 * 			  - the line position
	 * @param language
	 *            - the character information
	 */
	public Font(String fontFamilyName, StyleTypeDefinitions.FontStyle simpleFontStyle, double fontSize, Color color, StyleTypeDefinitions.TextLinePosition textLinePosition,
			Locale language) {
		this.familyName = fontFamilyName;
		this.simpleFontStyle = simpleFontStyle;
		this.textLinePosition = textLinePosition;
		this.size = fontSize;
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
	public StyleTypeDefinitions.FontStyle getFontStyle() {
		return simpleFontStyle;
	}

	/**
	 * Set the font style
	 * 
	 * @param simpleFontStyle
	 *            - the font style
	 */
	public void setFontStyle(StyleTypeDefinitions.FontStyle simpleFontStyle) {
		this.simpleFontStyle = simpleFontStyle;
	}

	/**
	 * Get the font text line position
	 * 
	 * @return the font text line position
	 */
	public StyleTypeDefinitions.TextLinePosition getTextLinePosition() {
		return textLinePosition;
	}

	/**
	 * Set the font text line position
	 * 
	 * @param textLinePosition
	 *            - the font text line position
	 */
	public void setTextLinePosition(StyleTypeDefinitions.TextLinePosition textLinePosition) {
		this.textLinePosition = textLinePosition;
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

	// /**
	// * Return the character information, which the font setting will affect.
	// *
	// * @return the character information
	// * @deprecated
	// */
	// public Locale getLocale() {
	// return language;
	// }
	//
	// /**
	// * Set the character information, which the font setting will affect.
	// *
	// * @param language
	// * - the character information
	// * @deprecated
	// */
	// public void setLocale(Locale language) {
	// this.language = language;
	// }

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
		return "FamilyName:" + getFamilyName() + "; Style:" + getFontStyle() + "; Size:" + getSize() + "; Color:"
				+ getColor() + "; TextLinePosition:" + getTextLinePosition();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Font) {
			Font f = (Font) o;
			if (!this.getFamilyName().equals(f.getFamilyName()))
				return false;
			if (!(Math.abs(this.getSize() - f.getSize()) < 0.005))
				return false;
			if (!(this.getFontStyle() == f.getFontStyle()))
				return false;
			// if ((this.getLocale() != null && f.getLocale() == null)
			// || (this.getLocale() == null && f.getLocale() != null))
			// return false;
			// if ((this.getLocale() != null && f.getLocale() != null)
			// && (!this.getLocale().equals(f.getLocale())))
			// return false;
			if ((this.getColor() != null && f.getColor() == null) || (this.getColor() == null && f.getColor() != null))
				return false;
			if ((this.getColor() != null && f.getColor() != null)
					&& (!this.getColor().toString().equals(f.getColor().toString())))
				return false;
			return true;
		}
		return false;
	}
}
