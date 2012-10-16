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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBackgroundColorAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoWrapOptionAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalBlTrAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalBlTrWidthsAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalTlBrAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalTlBrWidthsAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleVerticalAlignAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.type.Color;

/**
 * This class represents the table cell style settings. It provides methods to
 * access borders styles. More functions will be added latter.
 * 
 * <p>
 * This class is a corresponded high level class for element
 * "style:table-cell-properties". It provides methods to access the attributes
 * and children of "style:table-cell-properties".
 * 
 * @since 0.3
 */
public class TableCellProperties {

	// fo:background-color
	// fo:border
	// fo:border-bottom
	// fo:border-left
	// fo:border-right
	// fo:border-top
	// fo:padding
	// fo:padding-bottom
	// fo:padding-left
	// fo:padding-right
	// fo:padding-top
	// fo:wrap-option
	// style:border-line-width
	// style:border-line-width-bottom
	// style:border-line-width-left
	// style:border-line-width-right
	// style:border-line-width-top
	// style:cell-protect
	// style:decimal-places
	// style:diagonal-bl-tr
	// style:diagonal-bl-tr-widths
	// style:diagonal-tl-br
	// style:diagonal-tl-br-widths
	// style:direction
	// style:glyph-orientation-vertical
	// style:print-content
	// style:repeat-content
	// style:rotation-align
	// style:rotation-angle
	// style:shadow
	// style:shrink-to-fit
	// style:text-align-source
	// style:vertical-align
	// style:writing-mode

	// String backgroundColor;
	String padding;
	String paddingBottom;
	String paddingLeft;
	String paddingRight;
	String paddingTop;
	String cellProtect;
	String decimalPlaces;
	String direction;
	String glyphOrientationVertical;
	String printContent;
	String repeatContent;
	String rotationAlign;
	String rotationAngle;
	String shadow;
	String shrinkToFit;
	String textAlignSource;
	String verticalAlign;
	String writingMode;
	// String wrapOption;

	// String border;
	// String borderBottom;
	// String borderLeft;
	// String borderRight;
	// String borderTop;
	// String diagonalBlTr;
	// String diagonalTlBr;
	// String borderLineWidth;
	// String borderLineWidthBottom;
	// String borderLineWidthLeft;
	// String borderLineWidthRight;
	// String borderLineWidthTop;
	// String diagonalBlTrWidths;
	// String diagonalTlBrWidths;
	
	BorderPropertiesImpl mBorderPropertiesHandler;

	StyleTableCellPropertiesElement mElement;

	/**
	 * Create an instance of TableCellProperties
	 */
	protected TableCellProperties() {
	}

	/**
	 * Create an instance of TableCellProperties from an element
	 * <style:table-cell-properties>
	 * 
	 * @param properties
	 *            - the element of style:table-cell-properties
	 */
	protected TableCellProperties(StyleTableCellPropertiesElement properties) {
		mElement = properties;
		mBorderPropertiesHandler = new BorderPropertiesImpl(mElement);
	}

	/**
	 * Set the border style.
	 * <p>
	 * The first parameter <code>bordersType</code> describes which borders you
	 * want to apply the style to, e.g. up border, bottom border, left border,
	 * right border, diagonal lines or four borders.
	 * <p>
	 * The border style information will be removed if the parameter
	 * <code>bordersType</code> is NONE.
	 * 
	 * @param bordersType
	 *            - the type of the borders
	 * @param border
	 *            - the border style description
	 */
	public void setBorders(StyleTypeDefinitions.CellBordersType bordersType, Border border) {
		switch (bordersType) {
		case BOTTOM:
			setBottomBorder(border);
			break;
		case LEFT:
			setLeftBorder(border);
			break;
		case RIGHT:
			setRightBorder(border);
			break;
		case TOP:
			setTopBorder(border);
			break;
		case DIAGONALBLTR:
			setDiagonalBlTr(border);
			break;
		case DIAGONALTLBR:
			setDiagonalTlBr(border);
			break;
		case ALL_FOUR:
			setBorder(border);
			break;
		case LEFT_RIGHT:
			setLeftBorder(border);
			// border.switchInnerLineOuterLineWidth();
			setRightBorder(border);
			break;
		case TOP_BOTTOM:
			setTopBorder(border);
			// border.switchInnerLineOuterLineWidth();
			setBottomBorder(border);
			break;
		case DIAGONAL_LINES:
			setDiagonalBlTr(border);
			setDiagonalTlBr(border);
			break;
		case NONE:
			mElement.removeAttribute(StyleBorderLineWidthAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(FoBorderAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleBorderLineWidthBottomAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(FoBorderBottomAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleBorderLineWidthTopAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(FoBorderTopAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleBorderLineWidthLeftAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(FoBorderLeftAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleBorderLineWidthRightAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(FoBorderRightAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalBlTrWidthsAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalBlTrAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalTlBrWidthsAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalTlBrAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	/**
	 * Return the border setting for all four borders.
	 * <p>
	 * Null will be returned if there is no border setting for all four borders.
	 * 
	 * @return the border setting
	 */
	public Border getBorder() {
		return mBorderPropertiesHandler.getBorder();
	}

	/**
	 * Return the border setting for the top border.
	 * <p>
	 * Null will be returned if there is no border setting for the top border.
	 * 
	 * @return the border setting
	 */
	public Border getTopBorder() {
		return mBorderPropertiesHandler.getTopBorder();
	}

	/**
	 * Return the border setting for the left border.
	 * <p>
	 * Null will be returned if there is no border setting for the left border.
	 * 
	 * @return the border setting
	 */
	public Border getLeftBorder() {
		return mBorderPropertiesHandler.getLeftBorder();
	}

	/**
	 * Return the border setting for the right border.
	 * <p>
	 * Null will be returned if there is no border setting for the right border.
	 * 
	 * @return the border setting
	 */
	public Border getRightBorder() {
		return mBorderPropertiesHandler.getRightBorder();
	}

	// fo:border-bottom
	// style:border-line-width-bottom
	/**
	 * Return the border setting for the bottom border.
	 * <p>
	 * Null will be returned if there is no border setting for the bottom
	 * border.
	 * 
	 * @return the border setting
	 */
	public Border getBottomBorder() {
		return mBorderPropertiesHandler.getBottomBorder();
	}

	/**
	 * Return the border setting for the diagonal from bottom left to top right.
	 * <p>
	 * Null will be returned if there is no border setting for the diagonal from
	 * bottom left to top right.
	 * 
	 * @return the border setting
	 */
	public Border getDiagonalBlTr() {
		Border border = new Border();
		String borderAttr = mElement.getStyleDiagonalBlTrAttribute();
		String borderWidth = mElement.getStyleDiagonalBlTrWidthsAttribute();
		if (borderWidth == null)
			// some ODF version uses diagonal-bl-tr-width while schema use
			// diagonal-bl-tr-widths
			borderWidth = mElement.getAttribute("style:diagonal-bl-tr-width");

		if (borderAttr == null || borderAttr.length() == 0)
			return null;

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

	/**
	 * Return the border setting for the diagonal from top left to bottom right.
	 * <p>
	 * Null will be returned if there is no border setting for the diagonal from
	 * top left to bottom right.
	 * 
	 * @return the border setting
	 */
	public Border getDiagonalTlBr() {
		Border border = new Border();
		String borderAttr = mElement.getStyleDiagonalTlBrAttribute();
		String borderWidth = mElement.getStyleDiagonalTlBrWidthsAttribute();
		if (borderWidth == null)
			// some ODF version uses diagonal-tl-br-width while schema use
			// diagonal-tl-br-widths
			borderWidth = mElement.getAttribute("style:diagonal-tl-br-width");

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

	// fo:border-bottom
	// style:border-line-width-bottom
	/**
	 * Set the border definition for the bottom border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the bottom border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the bottom border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setBottomBorder(Border border) {
		mBorderPropertiesHandler.setBottomBorder(border);
	}

	// fo:border-top
	// style:border-line-width-top
	/**
	 * Set the border definition for the top border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the top border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the top border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setTopBorder(Border border) {
		mBorderPropertiesHandler.setTopBorder(border);
	}

	// fo:border-left
	// style:border-line-width-left
	/**
	 * Set the border definition for the left border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the left border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the left border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setLeftBorder(Border border) {
		mBorderPropertiesHandler.setLeftBorder(border);
	}

	// fo:border-right
	// style:border-line-width-right
	/**
	 * Set the border definition for the right border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the right border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the right border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setRightBorder(Border border) {
		mBorderPropertiesHandler.setRightBorder(border);
	}

	// style:diagonal-bl-tr
	// style:diagonal-bl-tr-widths
	/**
	 * Set the border definition for the diagonal from bottom left to top right.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the diagonal from bottom left to top right will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the diagonal from bottom left to top right will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setDiagonalBlTr(Border border) {
		if (border == null) {
			mElement.removeAttribute(StyleDiagonalBlTrWidthsAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalBlTrAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			mElement.setStyleDiagonalBlTrWidthsAttribute(border.getDoubleLineWidthDescription());
			mElement.setStyleDiagonalBlTrAttribute(border.getBorderDescription());
			break;
		case SINGLE:
			mElement.setStyleDiagonalBlTrAttribute(border.getBorderDescription());
			break;
		case NONE:
			mElement.removeAttribute(StyleDiagonalBlTrWidthsAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalBlTrAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	// style:diagonal-tl-br
	// style:diagonal-tl-br-widths
	/**
	 * Set the border definition for the diagonal from top left to bottom right.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the diagonal from top left to bottom right will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the diagonal from top left to bottom right will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setDiagonalTlBr(Border border) {
		if (border == null) {
			mElement.removeAttribute(StyleDiagonalTlBrWidthsAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalTlBrAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			mElement.setStyleDiagonalTlBrWidthsAttribute(border.getDoubleLineWidthDescription());
			mElement.setStyleDiagonalTlBrAttribute(border.getBorderDescription());
			break;
		case SINGLE:
			mElement.setStyleDiagonalTlBrAttribute(border.getBorderDescription());
			break;
		case NONE:
			mElement.removeAttribute(StyleDiagonalTlBrWidthsAttribute.ATTRIBUTE_NAME.getQName());
			mElement.removeAttribute(StyleDiagonalTlBrAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	// fo:border
	// style:border-line-width
	/**
	 * Set the border definition for all four borders.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * all four borders will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for all four borders will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setBorder(Border border) {
		mBorderPropertiesHandler.setBorder(border);
	}

	// fo:background-color
	/**
	 * Set the background color.
	 * <p>
	 * If the parameter <code>aColor</code> is null, the background color
	 * definition will be removed.
	 * 
	 * @param aColor
	 *            - the background color
	 */
	public void setBackgroundColor(Color aColor) {
		if (aColor == null) {
                    mElement.removeAttribute(FoBackgroundColorAttribute.ATTRIBUTE_NAME.getQName());
                } else {
                    mElement.setFoBackgroundColorAttribute(aColor.toString());
                }
	}

	// //style:cell-protect
	// public void setProtected(boolean isProtected)
	// {
	//		
	// }
	//	
	// //style:cell-protect
	// public boolean isProctected()
	// {
	// return false;
	// }

	/**
	 * Return the background color.
	 * <p>
	 * Null will be returned if there is no the background color definition or
	 * the background color definition is not valid.
	 * 
	 * @return the background color
	 */
	public Color getBackgroundColor() {
		String property = mElement.getFoBackgroundColorAttribute();
		Color color = null;
		try {
			color = Color.valueOf(property);
		} catch (Exception e) {
			Logger.getLogger(TableCellProperties.class.getName()).log(Level.WARNING, e.getMessage());
		}
		return color;
	}

	// fo:wrap-option
	/**
	 * Set the text is allowed to be wrapped.
	 * <p>
	 * If the parameter <code>isWrapped</code> is true, the text will be allowed
	 * to be wrapped. Or else, the text is not allowed to be wrapped.
	 * 
	 * @param isWrapped
	 *            - the wrapped option
	 */
	public void setWrapped(boolean isWrapped) {
		if (isWrapped) {
			mElement.setFoWrapOptionAttribute(FoWrapOptionAttribute.Value.WRAP.toString());
		} else {
			mElement.setFoWrapOptionAttribute(FoWrapOptionAttribute.Value.NO_WRAP.toString());
		}
	}

	// style:vertical-align
	/**
	 * Set the vertical alignment.
	 * <p>
	 * If the parameter <code>alignType</code> is null or DEFAULT, the vertical
	 * alignment definition will be removed.
	 * 
	 * @param alignType
	 *            - the vertical alignment
	 */
	public void setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType alignType) {
		if (alignType == StyleTypeDefinitions.VerticalAlignmentType.DEFAULT || alignType == null)
			mElement.removeAttribute(StyleVerticalAlignAttribute.ATTRIBUTE_NAME.getQName());
		else
			mElement.setStyleVerticalAlignAttribute(alignType.toString());
	}

	// style:vertical-align
	/**
	 * Return the vertical alignment.
	 * <p>
	 * If there is no vertical alignment definition, null will be returned.
	 * 
	 * @return the vertical alignment
	 */
	public StyleTypeDefinitions.VerticalAlignmentType getVerticalAlignment() {
		String alignType = mElement.getStyleVerticalAlignAttribute();
		if ((alignType == null) || (alignType.length() == 0))
			return null;

		StyleVerticalAlignAttribute.Value value = StyleVerticalAlignAttribute.Value.enumValueOf(alignType);

		switch (value) {
		case AUTO:
			return StyleTypeDefinitions.VerticalAlignmentType.DEFAULT;
		case AUTOMATIC:
			return StyleTypeDefinitions.VerticalAlignmentType.DEFAULT;
		case BASELINE:
			return StyleTypeDefinitions.VerticalAlignmentType.BOTTOM;
		case BOTTOM:
			return StyleTypeDefinitions.VerticalAlignmentType.BOTTOM;
		case MIDDLE:
			return StyleTypeDefinitions.VerticalAlignmentType.MIDDLE;
		case TOP:
			return StyleTypeDefinitions.VerticalAlignmentType.TOP;
		}
		return null;
	}

	// fo:wrap-option
	/**
	 * Return whether the text is allowed to be wrapped.
	 * <p>
	 * Return true if the text is allowed to be wrapped. Or else, return false.
	 * 
	 * @return a boolean value to indicate whether the text is allowed to be
	 *         wrapped
	 */
	public Boolean isWrapped() {
		String attr = mElement.getFoWrapOptionAttribute();
		if ((attr == null) || (attr.length() == 0))
			return false;

		FoWrapOptionAttribute.Value value = FoWrapOptionAttribute.Value.enumValueOf(attr);

		switch (value) {
		case NO_WRAP:
			return false;
		case WRAP:
			return true;
		}
		return null;
	}

	/**
	 * Return an instance of
	 * <code>TableCellProperties</p> to represent the "style:table-cell-properties" in a style element.
	 * <p>If there is no "style:table-cell-properties" defined in the style element, a new "style:table-cell-properties" element will be created.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>TableCellProperties</p>
	 */
	public static TableCellProperties getOrCreateTableCellProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style
				.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableCellProperties);
		return new TableCellProperties((StyleTableCellPropertiesElement) properties);
	}

	/**
	 * Return an instance of
	 * <code>TableCellProperties</p> to represent the "style:table-cell-properties" in a style element.
	 * <p>If there is no "style:table-cell-properties" defined in the style element, null will be returned.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>TableCellProperties</p>;Null if there is no
	 *         "style:table-cell-properties" defined
	 */
	public static TableCellProperties getTableCellProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style.getPropertiesElement(OdfStylePropertiesSet.TableCellProperties);
		if (properties != null)
			return new TableCellProperties((StyleTableCellPropertiesElement) properties);
		else
			return null;
	}

}

class BorderPropertiesImpl {
	
	OdfStylePropertiesBase borderPropertiesElement;
	
	public BorderPropertiesImpl(OdfStylePropertiesBase element)
	{
		borderPropertiesElement = element;
	}

	// fo:border-bottom
	// style:border-line-width-bottom
	/**
	 * Set the border definition for the bottom border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the bottom border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the bottom border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setBottomBorder(Border border) {
		if (border == null) {
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthBottomAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderBottomAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "style:border-line-width-bottom",border.getDoubleLineWidthDescription());
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-bottom",border.getBorderDescription());
			break;
		case SINGLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-bottom",border.getBorderDescription());
			break;
		case NONE:
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthBottomAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderBottomAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	// fo:border-top
	// style:border-line-width-top
	/**
	 * Set the border definition for the top border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the top border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the top border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setTopBorder(Border border) {
		if (border == null) {
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthTopAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderTopAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "style:border-line-width-top",border.getDoubleLineWidthDescription());
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-top",border.getBorderDescription());
			break;
		case SINGLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-top",border.getBorderDescription());
			break;
		case NONE:
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthTopAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderTopAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	// fo:border-left
	// style:border-line-width-left
	/**
	 * Set the border definition for the left border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the left border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the left border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setLeftBorder(Border border) {
		if (border == null) {
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthLeftAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderLeftAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "style:border-line-width-left",border.getDoubleLineWidthDescription());
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-left",border.getBorderDescription());
			break;
		case SINGLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-left",border.getBorderDescription());
			break;
		case NONE:
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthLeftAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderLeftAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	// fo:border-right
	// style:border-line-width-right
	/**
	 * Set the border definition for the right border.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * the right border will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for the right border will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setRightBorder(Border border) {
		if (border == null) {
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthRightAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderRightAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "style:border-line-width-right",border.getDoubleLineWidthDescription());
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-right",border.getBorderDescription());
			break;
		case SINGLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border-right",border.getBorderDescription());
			break;
		case NONE:
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthRightAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderRightAttribute.ATTRIBUTE_NAME.getQName());
		}
	}

	// fo:border
	// style:border-line-width
	/**
	 * Set the border definition for all four borders.
	 * <p>
	 * If the parameter <code>border</code> is null, the border definition for
	 * all four borders will be removed.
	 * <p>
	 * If the line type in the border definition is NONE, the border definition
	 * for all four borders will be removed.
	 * 
	 * @param border
	 *            - the border setting
	 */
	public void setBorder(Border border) {
		if (border == null) {
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderAttribute.ATTRIBUTE_NAME.getQName());
			return;
		}
		switch (border.lineStyle) {
		case DOUBLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "style:border-line-width",border.getDoubleLineWidthDescription());
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border",border.getBorderDescription());
			break;
		case SINGLE:
			borderPropertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(), "fo:border",border.getBorderDescription());
			break;
		case NONE:
			borderPropertiesElement.removeAttribute(StyleBorderLineWidthAttribute.ATTRIBUTE_NAME.getQName());
			borderPropertiesElement.removeAttribute(FoBorderAttribute.ATTRIBUTE_NAME.getQName());
		}
	}
	
	/**
	 * Return the border setting for all four borders.
	 * <p>
	 * Null will be returned if there is no border setting for all four borders.
	 * 
	 * @return the border setting
	 */
	public Border getBorder() {
		Border border = new Border();
		String borderAttr = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "border");
		String borderWidth = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "border-line-width");

		if (borderAttr == null || borderAttr.length() == 0)
			return null;

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

	/**
	 * Return the border setting for the top border.
	 * <p>
	 * Null will be returned if there is no border setting for the top border.
	 * 
	 * @return the border setting
	 */
	public Border getTopBorder() {
		Border border = new Border();
		String borderAttr = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-top");
		String borderWidth = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "border-line-width-top");

		if (borderAttr == null || borderAttr.length() == 0)
			return getBorder();

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

	/**
	 * Return the border setting for the left border.
	 * <p>
	 * Null will be returned if there is no border setting for the left border.
	 * 
	 * @return the border setting
	 */
	public Border getLeftBorder() {
		Border border = new Border();
		String borderAttr = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-left");
		String borderWidth = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "border-line-width-left");

		if (borderAttr == null || borderAttr.length() == 0)
			return getBorder();

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

	/**
	 * Return the border setting for the right border.
	 * <p>
	 * Null will be returned if there is no border setting for the right border.
	 * 
	 * @return the border setting
	 */
	public Border getRightBorder() {
		Border border = new Border();
		String borderAttr = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-right");
		String borderWidth = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "border-line-width-right");

		if (borderAttr == null || borderAttr.length() == 0)
			return getBorder();

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

	// fo:border-bottom
	// style:border-line-width-bottom
	/**
	 * Return the border setting for the bottom border.
	 * <p>
	 * Null will be returned if there is no border setting for the bottom
	 * border.
	 * 
	 * @return the border setting
	 */
	public Border getBottomBorder() {
		Border border = new Border();
		String borderAttr = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-bottom");
		String borderWidth = borderPropertiesElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "border-line-width-bottom");

		if (borderAttr == null || borderAttr.length() == 0) {
			return getBorder();
		}

		border.setBorderByDescription(borderAttr);
		if ((borderWidth != null) && (borderWidth.length() != 0)) {
			border.setDoubleLineWidthByDescription(borderWidth);
		}
		return border;
	}

}

