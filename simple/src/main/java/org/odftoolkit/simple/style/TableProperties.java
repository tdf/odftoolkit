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
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;

/**
 * This class represents the table style settings. It provides methods to access
 * or modify the formatting properties applied to tables. More functions will be
 * added latter.
 * 
 * <p>
 * This class is a corresponded high level class for element
 * "style:table-properties". It provides methods to access the attributes and
 * children of "style:table-properties".
 * 
 * @since 0.8
 */
public class TableProperties {
	StyleTablePropertiesElement mElement;

	/**
	 * Create an instance of TableProperties
	 */
	protected TableProperties() {
	}

	/**
	 * Create an instance of TableProperties from an element
	 * <style:table-properties>
	 * 
	 * @param properties
	 *            - the element of style:table-properties
	 */
	protected TableProperties(StyleTablePropertiesElement properties) {
		mElement = properties;
	}

	/**
	 * Set the break.
	 * 
	 * @param breakPosition
	 *            the position to insert a break (before or after)
	 * @param breakAttribute
	 *            the attribute name (page or column)
	 */
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

	/**
	 * Return the break property before the reference.
	 * <p>
	 * Null will be returned if there is no break setting before the reference.
	 * 
	 * @return - the break property; null if there is no break setting.
	 */
	public String getBreakBefore() {
		return mElement.getFoBreakBeforeAttribute();
	}

	/**
	 * Return the break property after the reference.
	 * <p>
	 * Null will be returned if there is no break setting after the reference.
	 * 
	 * @return - the break property; null if there is no break setting.
	 */
	public String getBreakAfter() {
		return mElement.getFoBreakAfterAttribute();
	}

	/**
	 * Return the page number that is used for a new page with a master style.
	 * <p>
	 * If there is no valid page number, 0 will be returned;
	 * 
	 * @return the page number
	 */
	public int getPageNumber() {
		try {
			return mElement.getStylePageNumberAttribute();
		} catch (NullPointerException e) {
			return 0;
		}
	}

	/**
	 * Set the page number for a new page with a master style.
	 * 
	 * @param pageNumber
	 *            the page number
	 */
	public void setPageNumber(int pageNumber) {
		if (pageNumber > 0) {
			mElement.setStylePageNumberAttribute(pageNumber);
		} else {
			mElement.removeAttribute("style:page-number");
		}
	}

	/**
	 * Return an instance of
	 * <code>TableProperties</p> to represent the "style:table-properties" in a style element.
	 * <p>If there is no "style:table-properties" defined in the style element, a new "style:table-properties" element will be created.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>TableProperties</p>
	 */
	public static TableProperties getOrCreateTableProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style
				.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableProperties);
		return new TableProperties((StyleTablePropertiesElement) properties);
	}

	/**
	 * Return an instance of
	 * <code>TableProperties</p> to represent the "style:table-properties" in a style element.
	 * <p>If there is no "style:table-properties" defined in the style element, null will be returned.
	 * 
	 * @param style
	 *            - a style element
	 * @return an instance of <code>TableProperties</p>;Null if there is no
	 *         "style:table-properties" defined
	 */
	public static TableProperties getTableProperties(OdfStyleBase style) {
		OdfStylePropertiesBase properties = style
				.getPropertiesElement(OdfStylePropertiesSet.TableProperties);
		if (properties != null)
			return new TableProperties((StyleTablePropertiesElement) properties);
		else
			return null;
	}
}
