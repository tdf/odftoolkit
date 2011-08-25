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

import org.odftoolkit.odfdom.dom.attribute.fo.FoTextAlignAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SimpleHorizontalAlignmentType;

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
	public void setHorizontalAlignment(SimpleHorizontalAlignmentType alignType) {
		if (alignType == SimpleHorizontalAlignmentType.DEFAULT)
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
	public SimpleHorizontalAlignmentType getHorizontalAlignment() {
		String alignType = mElement.getFoTextAlignAttribute();
		if ((alignType == null) || (alignType.length() == 0))
			return SimpleHorizontalAlignmentType.DEFAULT;

		FoTextAlignAttribute.Value value = FoTextAlignAttribute.Value.enumValueOf(alignType);
		switch (value) {
		case CENTER:
			return SimpleHorizontalAlignmentType.CENTER;
		case END:
			return SimpleHorizontalAlignmentType.RIGHT;
		case JUSTIFY:
			return SimpleHorizontalAlignmentType.JUSTIFIED;
		case LEFT:
			return SimpleHorizontalAlignmentType.LEFT;
		case RIGHT:
			return SimpleHorizontalAlignmentType.RIGHT;
		case START:
			return SimpleHorizontalAlignmentType.LEFT;
		}
		return null;
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
}
