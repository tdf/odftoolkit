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

import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalBlTrAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalBlTrWidthsAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalTlBrAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDiagonalTlBrWidthsAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleRunThroughAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawFill;
import org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawStroke;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;

/**
 * This class represents the graphic style settings. It provides methods to
 * access borders and background styles. More functions will be added later.
 * <p>
 * This class is a corresponded high level class for element
 * "style:graphic-properties". It provides methods to access the attributes and
 * children of "style:graphic-properties".
 * 
 * @since 0.5
 */
public class GraphicProperties {

	private StyleGraphicPropertiesElement mElement;
	BorderPropertiesImpl mBorderPropertiesHandler;

	/**
	 * Create a paragraph style setting object, which has the association with
	 * an element "style:paragraph-properties".
	 * 
	 * @param properties
	 *            - the element "style:paragraph-properties"
	 */
	protected GraphicProperties(StyleGraphicPropertiesElement properties) {
		mElement = properties;
		mBorderPropertiesHandler = new BorderPropertiesImpl(mElement);
	}

	/**
	 * Return an instance of
	 * <code>GraphicProperties</p> to represent the "style:graphic-properties" in a style element.
	 * <p>If there is no "style:graphic-properties" defined in the style element, a new "style:graphic-properties" element will be created.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>GraphicProperties</p>
	 */
	public static GraphicProperties getOrCreateGraphicProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style.getOrCreatePropertiesElement(OdfStylePropertiesSet.GraphicProperties);
		return new GraphicProperties((StyleGraphicPropertiesElement) properties);
	}

	/**
	 * Return an instance of
	 * <code>GraphicProperties</p> to represent the "style:graphic-properties" in a style element.
	 * <p>If there is no "style:graphic-properties" defined in the style element, null will be returned.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>GraphicProperties</p>;Null if there is no
	 *         "style:graphic-properties" defined
	 */
	public static GraphicProperties getGraphicProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style.getPropertiesElement(OdfStylePropertiesSet.GraphicProperties);
		if (properties != null)
			return new GraphicProperties((StyleGraphicPropertiesElement) properties);
		else
			return null;
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
			throw new RuntimeException("DIAGONALBL is not supported");
		case DIAGONALTLBR:
			throw new RuntimeException("DIAGONALTLBR is not supported");
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
			throw new RuntimeException("DIAGONAL_LINES is not supported");
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

	/**
	 * Set the style of stroke.
	 * <p>
	 * There are three types of stroke: none, solid and dash.
	 * <p>
	 * If the stroke is NONE, there is no stroke around the frame.
	 * <p>
	 * If the stroke is SOLID, there is solid line around the frame. color and
	 * width need to be specified.
	 * <p>
	 * If the stroke is DASH, there is dash line around the frame. color, width
	 * and the style name of dash line need to be specified.
	 * 
	 * @param stroke
	 *            the stroke type
	 * @param color
	 *            the color of the stroke
	 * @param widthDesc
	 *            the width description of the stroke, e.g. "0.01in"
	 * @param dashStyleName
	 *            the dash style name
	 * @see org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawStroke
	 */
	public void setStroke(OdfDrawStroke stroke, Color color, String widthDesc, String dashStyleName) {
		switch (stroke) {
		case NONE:
			mElement.setDrawStrokeAttribute("none");
			break;
		case SOLID:
			mElement.setDrawStrokeAttribute("solid");
			if (color != null)
				mElement.setSvgStrokeColorAttribute(color.toString());
			if (widthDesc != null && widthDesc.length() > 2 && verifyWidthDesc(widthDesc)) {
				mElement.setSvgStrokeWidthAttribute(widthDesc);
			}
			break;
		case DASH:
			mElement.setDrawStrokeAttribute("dash");
			if (color != null)
				mElement.setSvgStrokeColorAttribute(color.toString());
			if (widthDesc != null && widthDesc.length() > 2 && verifyWidthDesc(widthDesc)) {
				mElement.setSvgStrokeWidthAttribute(widthDesc);
			}
			mElement.setDrawStrokeDashNamesAttribute(dashStyleName);
		}
	}

	/**
	 * Set the fill style for a graphic object.
	 * 
	 * <p>
	 * This method supports 2 types of fill: none and solid.
	 * <p>
	 * If the fill type is NONE, the object is no filled at all.
	 * <p>
	 * If the fill type is SOLID, the drawing object is filled with the color
	 * specified by the second parameter.
	 * 
	 * @param fillType
	 *            the fill style type
	 * @param color
	 *            the specified color
	 */
	public void setFill(OdfDrawFill fillType, Color color) {
		switch (fillType) {
		case NONE:
			mElement.setDrawFillAttribute("none");
			break;
		case SOLID:
			mElement.setDrawFillAttribute("solid");
			mElement.setDrawFillColorAttribute(color.toString());
			break;
		case BITMAP:
		case GRADIENT:
		case HATCH:
			throw new RuntimeException(fillType.toString() + " not supported!");
		}
	}

	/**
	 * Set whether the content of a graphic object is displayed in the
	 * background or foreground. If it's displayed in the background, the
	 * content wouldn't be selected or moved.
	 * 
	 * @param isBackgroundObject
	 *            If <code>true</code>, the graphic object is displayed in the
	 *            background.
	 * @since 0.5.5
	 */
	public void setStyleRunThrough(boolean isBackgroundObject) {
		if (isBackgroundObject) {
			mElement.setStyleRunThroughAttribute(StyleRunThroughAttribute.Value.BACKGROUND.toString());
		} else {
			mElement.setStyleRunThroughAttribute(StyleRunThroughAttribute.Value.FOREGROUND.toString());
		}
	}

	/**
	 * Set the horizontal position
	 * 
	 * @param horizontalPos
	 *            the horizontal position
	 * @since 0.5.5
	 */
	public void setHorizontalPosition(FrameHorizontalPosition horizontalPos) {
		mElement.setStyleHorizontalPosAttribute(horizontalPos.toString());
	}

	/**
	 * Set the horizontal relative
	 * 
	 * @param relative
	 *            the horizontal relative
	 * @since 0.5.5
	 */
	public void setHorizontalRelative(HorizontalRelative relative) {
		mElement.setStyleHorizontalRelAttribute(relative.toString());
	}

	/**
	 * Set the vertical relative
	 * 
	 * @param relative
	 *            the vertical relative
	 * @since 0.5.5
	 */
	public void setVerticalRelative(VerticalRelative relative) {
		mElement.setStyleVerticalRelAttribute(relative.toString());
	}

	/**
	 * Set the vertical position
	 * 
	 * @param verticalPos
	 *            the vertical position
	 * @since 0.5.5
	 */
	public void setVerticalPosition(FrameVerticalPosition verticalPos) {
		mElement.setStyleVerticalPosAttribute(verticalPos.toString());
	}

	/**
	 * Return the horizontal position
	 * 
	 * @return the horizontal position
	 * @since 0.5.5
	 */
	public FrameHorizontalPosition getHorizontalPosition() {
		return FrameHorizontalPosition.enumValueOf(mElement.getStyleHorizontalPosAttribute());
	}

	/**
	 * Return the vertical position
	 * 
	 * @return the vertical position
	 * @since 0.5.5
	 */
	public FrameVerticalPosition getVerticalPosition() {
		return FrameVerticalPosition.enumValueOf(mElement.getStyleVerticalPosAttribute());
	}

	/**
	 * Return the vertical relative
	 * 
	 * @return the vertical relative
	 * @since 0.5.5
	 */
	public VerticalRelative getVerticalRelative() {
		return VerticalRelative.enumValueOf(mElement.getStyleVerticalRelAttribute());
	}

	/**
	 * Return the horizontal relative
	 * 
	 * @return the horizontal relative
	 * 
	 * @since 0.5.5
	 */
	public HorizontalRelative getHorizontalRelative() {
		return HorizontalRelative.enumValueOf(mElement.getStyleHorizontalRelAttribute());
	}

	private boolean verifyWidthDesc(String widthDesc) {
		char char1 = widthDesc.charAt(widthDesc.length() - 1);
		char char2 = widthDesc.charAt(widthDesc.length() - 2);

		if (!(char1 >= 'a' && char1 <= 'z') && !(char1 >= 'A' && (char1 <= 'Z')))
			return false;
		if (!(char2 >= 'a' && char2 <= 'z') && !(char2 >= 'A' && (char2 <= 'Z')))
			return false;
		String floatValue = widthDesc.substring(0, widthDesc.length() - 2).trim();
		try {
			Double.parseDouble(floatValue);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
