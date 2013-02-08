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

import org.odftoolkit.odfdom.dom.attribute.fo.FoBreakAfterAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBreakBeforeAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoTextAlignAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoTextIndentAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;

/**
 * This class represents the paragraph style settings. It provides methods to
 * access horizontal alignment. More functions will be added latter.
 * 
 * <p>
 * This class is a corresponded high level class for element
 * "style:paragraph-properties". It provides methods to access the attributes
 * and children of "style:paragraph-properties".
 * 
 * @since 0.3
 */
public class ParagraphProperties {

	// the default size as used for left-, right-margin and indention
	private static final String  DEFAULT_LENGTH = "0in";

	// fo:text-align

	StyleParagraphPropertiesElement mElement;

	/**
	 * Create an empty paragraph style setting object
	 */
	protected ParagraphProperties() {
	}

	/**
	 * Create a paragraph style setting object, which has the association with
	 * an element "style:paragraph-properties".
	 * 
	 * @param properties
	 *            - the element "style:paragraph-properties"
	 */
	protected ParagraphProperties(StyleParagraphPropertiesElement properties) {
		mElement = properties;
	}

	/**
	 * Set the horizontal alignment.
	 * <p>
	 * If the first parameter is null, the horizontal alignment setting will be
	 * removed.
	 * 
	 * @param alignType
	 *            - the horizontal alignment
	 */
	public void setHorizontalAlignment(HorizontalAlignmentType alignType) {
		if (alignType == HorizontalAlignmentType.DEFAULT)
			mElement.removeAttribute(FoTextAlignAttribute.ATTRIBUTE_NAME.getQName());
		else
			mElement.setFoTextAlignAttribute(alignType.getAlignmentString());
	}

	/**
	 * Return the horizontal alignment.
	 * <p>
	 * Null will be returned if there is no horizontal alignment setting.
	 * 
	 * @return - the horizontal alignment; null if there is no horizontal
	 *         alignment setting.
	 */
	public HorizontalAlignmentType getHorizontalAlignment() {
		String alignType = mElement.getFoTextAlignAttribute();
		if ((alignType == null) || (alignType.length() == 0))
			return HorizontalAlignmentType.DEFAULT;

		FoTextAlignAttribute.Value value = FoTextAlignAttribute.Value.enumValueOf(alignType);
		switch (value) {
		case CENTER:
			return HorizontalAlignmentType.CENTER;
		case END:
			return HorizontalAlignmentType.RIGHT;
		case JUSTIFY:
			return HorizontalAlignmentType.JUSTIFY;
		case LEFT:
			return HorizontalAlignmentType.LEFT;
		case RIGHT:
			return HorizontalAlignmentType.RIGHT;
		case START:
			return HorizontalAlignmentType.LEFT;
		}
		return null;
	}

	/**
	 * Set the left margin of this <code>ParagraphProperties</code>
	 * 
	 * @param marginLeft
	 *            the size of the left margin (in Millimeter)
	 * @since 0.7
	 */
	public void setMarginLeft(double marginLeft) {
		if (marginLeft == 0) {
			mElement.removeAttribute(FoMarginLeftAttribute.ATTRIBUTE_NAME
					.getQName());
		} else {
			mElement.setFoMarginLeftAttribute(getInchValue(marginLeft));
		}
	}

	/**
	 * Get the size of the left margin of this <code>ParagraphProperties</code>
	 * 
	 * @return the size of the left margin (in Millimeter)
	 * @since 0.7
	 */
	public double getMarginLeft() {
		// get the value
		String valueString = mElement.getFoMarginLeftAttribute();
		// check if a value was returned
		if (valueString == null) {
			// if not use the default length
			valueString = DEFAULT_LENGTH;
		}
		// return the converted value
		return Length.parseDouble(valueString, Unit.MILLIMETER);
	}

	/**
	 * Set the right margin of this <code>ParagraphProperties</code>
	 * 
	 * @param marginRight
	 *            the size of the right margin (in Millimeter)
	 * @since 0.7
	 */
	public void setMarginRight(double marginRight) {
		if (marginRight == 0) {
			mElement.removeAttribute(FoMarginRightAttribute.ATTRIBUTE_NAME
					.getQName());
		} else {
			mElement.setFoMarginRightAttribute(getInchValue(marginRight));
		}
	}

	/**
	 * Get the size of the right margin of this <code>ParagraphProperties</code>
	 * 
	 * @return the size of the right margin (in Millimeter)
	 * @since 0.7
	 */
	public double getMarginRight() {
		// get the value
		String valueString = mElement.getFoMarginRightAttribute();
		// check if a value was returned
		if (valueString == null) {
			// if not use the default length
			valueString = DEFAULT_LENGTH;
		}
		// return the converted value
		return Length.parseDouble(valueString, Unit.MILLIMETER);
	}

	/**
	 * Set the top margin of this <code>ParagraphProperties</code>
	 * 
	 * @param marginTop
	 *            the size of the right margin (in Millimeter)
	 * @since 0.7
	 */
	public void setMarginTop(double marginTop) {
		if (marginTop == 0) {
			mElement.removeAttribute(FoMarginTopAttribute.ATTRIBUTE_NAME
					.getQName());
		} else {
			mElement.setFoMarginTopAttribute(getInchValue(marginTop));
		}
	}

	/**
	 * Get the size of the top margin of this <code>ParagraphProperties</code>
	 * 
	 * @return the size of the top margin (in Millimeter)
	 * @since 0.7
	 */
	public double getMarginTop() {
		// get the value
		String valueString = mElement.getFoMarginTopAttribute();
		// check if a value was returned
		if (valueString == null) {
			// if not use the default length
			valueString = DEFAULT_LENGTH;
		}
		// return the converted value
		return Length.parseDouble(valueString, Unit.MILLIMETER);
	}

	/**
	 * Set the bottom margin of this <code>ParagraphProperties</code>
	 * 
	 * @param marginBottom
	 *            the size of the bottom margin (in Millimeter)
	 * @since 0.7
	 */
	public void setMarginBottom(double marginBottom) {
		if (marginBottom == 0) {
			mElement.removeAttribute(FoMarginBottomAttribute.ATTRIBUTE_NAME
					.getQName());
		} else {
			mElement.setFoMarginBottomAttribute(getInchValue(marginBottom));
		}
	}

	/**
	 * Get the size of the bottom margin of this
	 * <code>ParagraphProperties</code>
	 * 
	 * @return the size of the bottom margin (in Millimeter)
	 * @since 0.7
	 */
	public double getMarginBottom() {
		// get the value
		String valueString = mElement.getFoMarginBottomAttribute();
		// check if a value was returned
		if (valueString == null) {
			// if not use the default length
			valueString = DEFAULT_LENGTH;
		}
		// return the converted value
		return Length.parseDouble(valueString, Unit.MILLIMETER);
	}

	/**
	 * Set the text indention size of this <code>ParagraphProperties</code>
	 * 
	 * @param textIndent
	 *            the size of the text indention (in Millimeter)
	 * @since 0.7
	 */
	public void setTextIndent(double textIndent) {
		if (textIndent == 0) {
			mElement.removeAttribute(FoTextIndentAttribute.ATTRIBUTE_NAME
					.getQName());
		} else {
			mElement.setFoTextIndentAttribute(getInchValue(textIndent));
		}
	}

	/**
	 * Get the size of the text indention of this
	 * <code>ParagraphProperties</code>
	 * 
	 * @return the size of the text indention (in Millimeter)
	 * @since 0.7
	 */
	public double getTextIndent() {
		// get the value
		String valueString = mElement.getFoTextIndentAttribute();
		// check if a value was returned
		if (valueString == null) {
			// if not use the default length
			valueString = DEFAULT_LENGTH;
		}
		// return the converted value
		return Length.parseDouble(valueString, Unit.MILLIMETER);
	}

	/**
	 * Return an instance of
	 * <code>ParagraphProperties</p> to represent the "style:paragraph-properties" in a style element.
	 * <p>If there is no "style:paragraph-properties" defined in the style element, a new "style:paragraph-properties" element will be created.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>ParagraphProperties</p>
	 */
	public static ParagraphProperties getOrCreateParagraphProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style
				.getOrCreatePropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
		return new ParagraphProperties((StyleParagraphPropertiesElement) properties);
	}

	/**
	 * Return an instance of
	 * <code>ParagraphProperties</p> to represent the "style:paragraph-properties" in a style element.
	 * <p>If there is no "style:paragraph-properties" defined in the style element, null will be returned.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>ParagraphProperties</p>;Null if there is no
	 *         "style:paragraph-properties" defined
	 */
	public static ParagraphProperties getParagraphProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style.getPropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
		if (properties != null)
			return new ParagraphProperties((StyleParagraphPropertiesElement) properties);
		else
			return null;
	}
	
	/**
	 * Returns the provided Millimeter value as Inch value
	 * 
	 * @param value
	 *          the value to set the attribute value to (in Millimeter)
	 */
	private static String getInchValue(double value) {
		// build the string for mm
		final String mmValueString = value + Unit.MILLIMETER.abbr();
		// convert the length to inch
		final String inchValueString = Length.mapToUnit(mmValueString, Unit.INCH);

		// return the value
		return inchValueString;
	}
	public void setBreak(String breakPosition, String breakAttribute) {
		if (breakPosition == null) {
			return;
		}
		if (breakAttribute == null) {
			if (breakPosition.equals("before")) {
				mElement.removeAttribute(FoBreakBeforeAttribute.ATTRIBUTE_NAME
						.getQName());
			} else if (breakPosition.equals("after"))
				mElement.removeAttribute(FoBreakAfterAttribute.ATTRIBUTE_NAME
						.getQName());
		} else if (breakPosition.equals("before")) {
			mElement.setFoBreakBeforeAttribute(breakAttribute);
		} else if (breakPosition.equals("after"))
			mElement.setFoBreakAfterAttribute(breakAttribute);
	}
	public String getBreakBefore() {
		return mElement.getFoBreakBeforeAttribute();
	}
	public String getBreakAfter() {
		return mElement.getFoBreakAfterAttribute();
	}
	public int getPageNumber() {
		return mElement.getStylePageNumberAttribute();
	}
	public void setPageNumber(int pageNumber) {
		if (pageNumber > 0) {
			mElement.setStylePageNumberAttribute(pageNumber);
		} else {
			mElement.removeAttribute("style:page-number");
		}
	}
}
