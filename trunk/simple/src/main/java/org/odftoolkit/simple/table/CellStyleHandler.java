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

package org.odftoolkit.simple.table;

import java.util.Locale;

import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.Document.ScriptType;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.ParagraphProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.TableCellProperties;
import org.odftoolkit.simple.style.TextProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.TextLinePosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;

/**
 * This class provides functions to handle the style of a cell.
 * 
 * <p>
 * This class provides functions to handle the border settings, font settings,
 * text alignment settings and so on.
 * 
 * @since 0.3
 */
public class CellStyleHandler extends DefaultStyleHandler {

	Cell mCell;
	TableTableCellElementBase mCellElement;

	TextProperties mTextProperties;
	TextProperties mWritableTextProperties;
	TableCellProperties mTableCellProperties;
	TableCellProperties mWritableTableCellProperties;
	ParagraphProperties mParagraphProperties;
	ParagraphProperties mWritableParagraphProperties;

	CellStyleHandler(Cell aCell) {
		super(aCell.getOdfElement());
		mCell = aCell;
		mCellElement = (TableTableCellElementBase) mCell.getOdfElement();
	}

	private boolean isShared = false;

	/**
	 * Return the used style name of this cell.
	 * <p>
	 * If there is no style name defined for cell, the attribute
	 * "table:default-cell-style-name" in table row and table column would be
	 * returned.
	 */
	@Override
	public String getUsedStyleName() {
		String styleName = mCellElement.getStyleName();
		if (styleName == null || (styleName.equals(""))) { // search in row
			Row aRow = mCell.getTableRow();
			styleName = aRow.getOdfElement().getTableDefaultCellStyleNameAttribute();
			isShared = true;
		}
		if (styleName == null || (styleName.equals(""))) { // search in column
			Column aColumn = mCell.getTableColumn();
			styleName = aColumn.getOdfElement().getTableDefaultCellStyleNameAttribute();
			isShared = true;
		}
		return styleName;
	}

	/**
	 * Return the style element for this cell, for read and write functions.
	 * <p>
	 * An empty style definition will be created if there is no style
	 * definition.
	 * 
	 * @return the style element
	 */
	@Override
	public OdfStyle getStyleElementForWrite() {
		if (mWritableStyleElement != null)
			return mWritableStyleElement;

		mCell.splitRepeatedCells();
		mCellElement = (TableTableCellElementBase) mCell.getOdfElement();
		mOdfElement = mCellElement; // note here: mOdfElement in
									// DefaultStyleHandler must be updated at
									// the same time

		String styleName = getUsedStyleName();
		mWritableStyleElement = getWritableStyleElementByName(styleName, isShared);
		return mWritableStyleElement;
	}

	private OdfDefaultStyle getCellDefaultStyle() {
		return mDocument.getDocumentStyles().getDefaultStyle(OdfStyleFamily.TableCell);
	}

	private OdfDefaultStyle getParagraphDefaultStyle() {
		return mDocument.getDocumentStyles().getDefaultStyle(OdfStyleFamily.Paragraph);
	}

	private OdfStyleBase getParentStyle(OdfStyle aStyle) {
		String parentName = aStyle.getStyleParentStyleNameAttribute();
		if (parentName == null || parentName.length() == 0)
			return null;
		if (parentName.equals("Default"))
			return getCellDefaultStyle();
		else
			return getStyleByName(parentName);
	}

	private OdfStyle getStyleByName(String name) {
		OdfStyle styleElement = null;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		styleElement = styles.getStyle(name, OdfStyleFamily.TableCell);

		if (styleElement == null) {
			styleElement = mDocument.getDocumentStyles().getStyle(name, OdfStyleFamily.TableCell);
		}

		return styleElement;
	}

	private OdfStyleBase getCurrentUsedStyle() {
		if (mWritableStyleElement != null)
			return mWritableStyleElement;
		else
			return mStyleElement;
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
		if (textProperties != null)
			country = textProperties.getCountry(type);
		if (country != null && country.length() > 0)
			return country;

		boolean isDefault = isUseDefaultStyle;
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TextProperties parentStyleSetting = TextProperties.getTextProperties(parentStyle);
			country = parentStyleSetting.getCountry(type);
			if (country != null && country.length() > 0)
				return country;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			TextProperties defaultStyleSetting = TextProperties.getTextProperties(defaultStyle);
			country = defaultStyleSetting.getCountry(type);
		}
		return country;
	}

	private void mergeFont(Font target, Font source) {
		// merge font
		if (target.getFamilyName() == null && source.getFamilyName() != null)
			target.setFamilyName(source.getFamilyName());
		if (target.getColor() == null && source.getColor() != null)
			target.setColor(source.getColor());
		if (target.getSize() == 0 && source.getSize() > 0)
			target.setSize(source.getSize());
		if (target.getFontStyle() == null && source.getFontStyle() != null)
			target.setFontStyle(source.getFontStyle());
		if (target.getTextLinePosition() == null && source.getTextLinePosition() != null)
			target.setTextLinePosition(source.getTextLinePosition());
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
		if (textProperties != null)
			font = textProperties.getFont(type);
		else
			font = new Font(null, null, 0, (StyleTypeDefinitions.TextLinePosition) null);

		if (font != null && font.getFamilyName() != null && font.getColor() != null && font.getSize() != 0
				&& font.getFontStyle() != null && font.getTextLinePosition() != null)
			return font;

		boolean isDefault = isUseDefaultStyle;
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TextProperties parentStyleSetting = TextProperties.getTextProperties(parentStyle);
			Font tempFont = parentStyleSetting.getFont(type);
			mergeFont(font, tempFont);
			if (font.getFamilyName() != null && font.getColor() != null && font.getSize() > 0
					&& font.getFontStyle() != null && font.getTextLinePosition() != null) {
				return font;
			}
			// continue to get parent properties
			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			// For text document, there is no default table cell style.
			// So use default font style in default paragraph style.
			if (defaultStyle == null) {
				defaultStyle = getParagraphDefaultStyle();
			}
			if (defaultStyle != null) {
				TextProperties defaultStyleSetting = TextProperties.getTextProperties(defaultStyle);
				Font tempFont = defaultStyleSetting.getFont(type);
				mergeFont(font, tempFont);
			}
		}

		if (font.getColor() == null)
			font.setColor(Color.BLACK);
		if (font.getFontStyle() == null)
			font.setFontStyle(FontStyle.REGULAR);
		if (font.getTextLinePosition() == null)
			font.setTextLinePosition(TextLinePosition.REGULAR);

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
		if (textProperties != null)
			language = textProperties.getLanguage(type);
		if (language != null && language.length() > 0)
			return language;

		boolean isDefault = isUseDefaultStyle;
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TextProperties parentStyleSetting = TextProperties.getTextProperties(parentStyle);
			language = parentStyleSetting.getLanguage(type);
			if (language != null && language.length() > 0)
				return language;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
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
	 *            - font definition
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
	 *            - font definition
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
	 * Return the background color.
	 * <p>
	 * The background color in its parent style and default style will be taken
	 * into considered.
	 * <p>
	 * Color WHITE will be returned if there is no the background color
	 * definition or the background color definition is not valid.
	 * 
	 * @return the background color
	 */
	public Color getBackgroundColor() {
		Color tempColor = null;
		TableCellProperties properties = getTableCellPropertiesForRead();
		if (properties != null)
			tempColor = properties.getBackgroundColor();
		if (tempColor != null)
			return tempColor;

		boolean isDefault = isUseDefaultStyle;
		// find in parent style definition
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TableCellProperties parentStyleSetting = TableCellProperties.getTableCellProperties(parentStyle);
			tempColor = parentStyleSetting.getBackgroundColor();
			if (tempColor != null)
				return tempColor;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		// find in default style definition
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			TableCellProperties defaultStyleSetting = TableCellProperties.getTableCellProperties(defaultStyle);
			tempColor = defaultStyleSetting.getBackgroundColor();
		}
		// use default
		if (tempColor == null)
			return Color.WHITE;
		return tempColor;
	}

	private Border getNullableBorder(TableCellProperties properties, CellBordersType type) {
		switch (type) {
		case LEFT:
			return properties.getLeftBorder();
		case RIGHT:
			return properties.getRightBorder();
		case TOP:
			return properties.getTopBorder();
		case BOTTOM:
			return properties.getBottomBorder();
		case DIAGONALBLTR:
			return properties.getDiagonalBlTr();
		case DIAGONALTLBR:
			return properties.getDiagonalTlBr();
		default:
			throw new IllegalArgumentException("Only border type with a single border is accepted.");
		}
	}

	/**
	 * Return the border setting for a specific border.
	 * <p>
	 * The accepted parameter can be TOP,BOTTOM,LEFT,RIGHT,DIAGONALBLTR and
	 * DIAGONALTLBR.
	 * <p>
	 * The border type with a collection of borders, e.g. ALL_FOUR, LEFT_RIGHT,
	 * are not legal arguments
	 * <p>
	 * The border definition in its parent style and default style will be taken
	 * into considered.
	 * <p>
	 * <code>Border.NONE</code> will be returned if there is no the border
	 * definition for a specific border.
	 * 
	 * @param type
	 *            - the border type which describes a single border
	 * @return the border setting
	 */
	public Border getBorder(CellBordersType type) {

		Border tempBorder = null;
		TableCellProperties properties = getTableCellPropertiesForRead();
		if (properties != null) {
			tempBorder = getNullableBorder(properties, type);
		}
		if (tempBorder != null)
			return tempBorder;

		boolean isDefault = isUseDefaultStyle;
		// find in parent style definition
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TableCellProperties parentStyleSetting = TableCellProperties.getTableCellProperties(parentStyle);
			tempBorder = getNullableBorder(parentStyleSetting, type);
			;
			if (tempBorder != null)
				return tempBorder;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		// find in default style definition
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			TableCellProperties defaultStyleSetting = TableCellProperties.getTableCellProperties(defaultStyle);
			tempBorder = getNullableBorder(defaultStyleSetting, type);
		}
		// use default
		if (tempBorder == null) {
			return Border.NONE;
		}
		return tempBorder;
	}

	/**
	 * Return the vertical alignment.
	 * <p>
	 * If there is no vertical alignment definition, DEFAULT will be returned.
	 * <p>
	 * The vertical alignment definition in its parent style and default style
	 * will be taken into considered.
	 * 
	 * @return the vertical alignment
	 */
	public VerticalAlignmentType getVerticalAlignment() {
		VerticalAlignmentType tempAlign = null;
		TableCellProperties properties = getTableCellPropertiesForRead();
		if (properties != null) {
			tempAlign = properties.getVerticalAlignment();
		}
		if (tempAlign != null)
			return tempAlign;

		boolean isDefault = isUseDefaultStyle;
		// find in parent style definition
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TableCellProperties parentStyleSetting = TableCellProperties.getTableCellProperties(parentStyle);
			tempAlign = parentStyleSetting.getVerticalAlignment();
			if (tempAlign != null)
				return tempAlign;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		// find in default style definition
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			TableCellProperties defaultStyleSetting = TableCellProperties.getTableCellProperties(defaultStyle);
			tempAlign = defaultStyleSetting.getVerticalAlignment();
		}
		// use default
		if (tempAlign == null) {
			return VerticalAlignmentType.DEFAULT;
		}
		return tempAlign;
	}

	/**
	 * Return the wrap option of this cell.
	 * <p>
	 * The wrap option definition in its parent style and default style will be
	 * taken into considered.
	 * 
	 * @return <code>true</code> if the cell content can be wrapped;
	 *         <p>
	 *         <code>false</code> if the cell content cannot be wrapped.
	 */
	public boolean isTextWrapped() {
		Boolean tempBool = null;
		TableCellProperties properties = getTableCellPropertiesForRead();
		if (properties != null)
			tempBool = properties.isWrapped();
		if (tempBool != null)
			return tempBool.booleanValue();

		boolean isDefault = isUseDefaultStyle;
		// find in parent style definition
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			TableCellProperties parentStyleSetting = TableCellProperties.getTableCellProperties(parentStyle);
			tempBool = parentStyleSetting.isWrapped();
			if (tempBool != null)
				return tempBool;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		// find in default style definition
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			TableCellProperties defaultStyleSetting = TableCellProperties.getTableCellProperties(defaultStyle);
			tempBool = defaultStyleSetting.isWrapped();
		}
		// use default
		if (tempBool == null)
			return false;
		return tempBool.booleanValue();
	}

	/**
	 * Set the background color of this cell.
	 * 
	 * @param color
	 *            - the background color that need to set. If <code>color</code>
	 *            is null, background color setting will be removed.
	 */
	public void setBackgroundColor(Color color) {
		getTableCellPropertiesForWrite().setBackgroundColor(color);
	}

	/**
	 * Set the border style of this cell. You can set the border style for a
	 * single border or a border collection.
	 * <p>
	 * The second parameter <code>bordersType</code> describes which borders you
	 * want to apply the style to, e.g. up border, bottom border, left border,
	 * right border, diagonal lines or four borders.
	 * 
	 * @param border
	 *            - the border style description
	 * @param bordersType
	 *            - the type of the borders
	 */
	public void setBorders(Border border, CellBordersType bordersType) {
		getTableCellPropertiesForWrite().setBorders(bordersType, border);
	}

	/**
	 * Set the vertical alignment setting of this cell.
	 * <p>
	 * If the alignment is set as Default or null, the explicit vertical
	 * alignment setting is removed.
	 * 
	 * @param alignType
	 *            - the vertical alignment setting.
	 */
	public void setVerticalAlignment(VerticalAlignmentType alignType) {
		getTableCellPropertiesForWrite().setVerticalAlignment(alignType);
	}

	/**
	 * Set the wrap option of this cell.
	 * 
	 * @param isWrapped
	 *            - whether the cell content can be wrapped or not
	 */
	public void setTextWrapped(boolean isWrapped) {
		getTableCellPropertiesForWrite().setWrapped(isWrapped);
	}

	/**
	 * Set the horizontal alignment.
	 * <p>
	 * If the parameter <code>alignType</code> is null, the horizontal alignment
	 * setting will be removed.
	 * 
	 * @param alignType
	 *            - the horizontal alignment
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
	 * DEFAULT will be returned if there is no horizontal alignment setting.
	 * 
	 * @return - the horizontal alignment; null if there is no horizontal
	 *         alignment setting.
	 */
	public HorizontalAlignmentType getHorizontalAlignment() {
		HorizontalAlignmentType tempAlign = null;
		ParagraphProperties properties = getParagraphPropertiesForRead();
		if (properties != null)
			tempAlign = properties.getHorizontalAlignment();
		if (tempAlign != null)
			return tempAlign;

		boolean isDefault = isUseDefaultStyle;
		// find in parent style definition
		OdfStyleBase parentStyle = null;
		if (!isDefault)
			parentStyle = getParentStyle((OdfStyle) getCurrentUsedStyle());
		while ((!isDefault) && (parentStyle != null)) {
			ParagraphProperties parentStyleSetting = ParagraphProperties.getParagraphProperties(parentStyle);
			tempAlign = parentStyleSetting.getHorizontalAlignment();
			if (tempAlign != null)
				return tempAlign;

			if (parentStyle instanceof OdfDefaultStyle)
				isDefault = true;
			else
				parentStyle = getParentStyle((OdfStyle) parentStyle);
		}
		// find in default style definition
		if (!isDefault) {
			OdfDefaultStyle defaultStyle = getCellDefaultStyle();
			ParagraphProperties defaultStyleSetting = ParagraphProperties.getParagraphProperties(defaultStyle);
			tempAlign = defaultStyleSetting.getHorizontalAlignment();
		}
		// use default
		if (tempAlign == null)
			return HorizontalAlignmentType.DEFAULT;
		return tempAlign;
	}
}
