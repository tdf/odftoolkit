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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.StringTokenizer;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;

/**
 * This class represents border style settings, including line style, color,
 * width, inner line width, outer line width and the distance.
 * 
 * @since 0.3
 */
public class Border {

	StyleTypeDefinitions.LineType lineStyle;
	Color color;
	double width;
	double innerLineWidth, distance, outerLineWidth;
	StyleTypeDefinitions.SupportedLinearMeasure linearMeasure;
	private static final String WidthFormatInInch = "#.####";

	/**
	 * A static variable to represent a border without any lines, which means no
	 * border at all.
	 */
	public static Border NONE = new Border(StyleTypeDefinitions.LineType.NONE);

	/**
	 * Constructor to create an empty border
	 */
	protected Border() {

	}

	private Border(StyleTypeDefinitions.LineType lineType) {
		this.lineStyle = lineType;
		width = 0;
	}

	/**
	 * Constructor to create a single line border
	 * 
	 * @param aColor
	 *            - the color of the border
	 * @param width
	 *            - the line width of the border
	 * @param linearMeasure
	 *            - the linear measurement of the border width
	 */
	public Border(Color aColor, double width, StyleTypeDefinitions.SupportedLinearMeasure linearMeasure) {
		color = aColor;
		this.width = width;
		this.linearMeasure = linearMeasure;
		this.lineStyle = StyleTypeDefinitions.LineType.SINGLE;
	}

	// /**
	// * Constructor to create a single line border.
	// *
	// * @param width - a string to represent the width and the linear
	// measurement, such as "12pt", "0.001in".
	// * The supported linear measurement includes "pt" and "in".
	// * @param aColor - the color of the border
	// */
	// protected Border(String width, Color aColor) {
	// color = aColor;
	// this.width = getLineWidth(width);
	// this.linearMeasure = getLineMeasure(width);
	// this.lineStyle = StyleTypeDefinitions.LineType.SINGLE;
	// }

	/**
	 * Constructor to create a double line border
	 * 
	 * @param aColor
	 *            - the color of the border
	 * @param width
	 *            - the line width of the border
	 * @param innerLineWidth
	 *            - the inner line width of the border
	 * @param outerLineWidth
	 *            - the outer line width of the border
	 * @param linearMeasure
	 *            - the linear measurement of the border width
	 * 
	 * @throws IllegalArgumentException
	 *             if the width is not bigger than the sum of inner line width
	 *             and outer line width.
	 */
	public Border(Color aColor, double width, double innerLineWidth, double outerLineWidth,
			StyleTypeDefinitions.SupportedLinearMeasure linearMeasure) {
		color = aColor;
		this.width = width;
		this.linearMeasure = linearMeasure;
		this.innerLineWidth = innerLineWidth;
		this.outerLineWidth = outerLineWidth;
		this.distance = width - innerLineWidth - outerLineWidth;
		this.lineStyle = StyleTypeDefinitions.LineType.DOUBLE;

		if (distance <= 0)
			throw new IllegalArgumentException(
					"The width must bigger than the sum of inner line width and outer line width!");
	}

	// /**
	// * COnstructor to create a double line border
	// *
	// * @param width - a string to represent the width and the linear
	// measurement.
	// * @param aColor - the color of the border
	// * @param innerLineWidth - a string to represent the width and the linear
	// measurement.
	// * @param distance - a string to represent the width and the linear
	// measurement.
	// * @param outerLineWidth - a string to represent the width and the linear
	// measurement.
	// */
	// protected Border(String width, Color aColor, String innerLineWidth,
	// String distance, String outerLineWidth) {
	// color = aColor;
	// this.width = getLineWidth(width);
	// this.linearMeasure = getLineMeasure(width);
	// setDoubleLineWidth(innerLineWidth, distance, outerLineWidth);
	// }

	/**
	 * Return the line style of the border.
	 * <p>
	 * The possible return value are "SINGLE","DOUBLE", and "NONE";
	 * 
	 * @return the line style of the border.
	 */
	public StyleTypeDefinitions.LineType getLineStyle() {
		return lineStyle;
	}

	/**
	 * Set the line style of the border.
	 * <p>
	 * The valid parameter can be "SINGLE","DOUBLE", and "NONE";
	 * 
	 * @param lineStyle
	 *            - the line style of the border.
	 */
	public void setLineStyle(StyleTypeDefinitions.LineType lineStyle) {
		this.lineStyle = lineStyle;
	}

	/**
	 * Set border style by a description string. The description string includes
	 * border width, line style and color. For example:
	 * "0.0362in double #ff3333" is a valid description string.
	 * 
	 * @param borderDesc
	 *            - the description of border style
	 */
	protected void setBorderByDescription(String borderDesc) {
		StringTokenizer st = new StringTokenizer(borderDesc);
		if (st.countTokens() != 3)
			throw new IllegalArgumentException(
					"The border description is invalid. Border description contains width, style and color, such as '0.0154in double #9900ff'");

		String borderWidth = st.nextToken();
		String borderStyle = st.nextToken();
		Color borderColor = new Color(st.nextToken());

		if (borderStyle.equals("solid"))
			borderStyle = StyleTypeDefinitions.LineType.SINGLE.toString();

		StyleTypeDefinitions.LineType borderStyleType = StyleTypeDefinitions.LineType.enumValueOf(borderStyle);

		this.width = getLineWidth(borderWidth);
		this.linearMeasure = getLineMeasure(borderWidth);
		setLineStyle(borderStyleType);
		setColor(borderColor);
	}

	/**
	 * Return the description string of border style. The description string
	 * includes border width, line style and color. For example,
	 * "0.0362in double #ff3333".
	 * 
	 * @return the description string of border style
	 */
	protected String getBorderDescription() {
		switch (lineStyle) {
		case SINGLE:
			return getWidth() + linearMeasure.toString() + " solid " + getColor();
		case DOUBLE:
			return getWidth() + linearMeasure.toString() + " double " + getColor();
		case NONE:
			return null;
		}
		return null;
	}

	/**
	 * Return the color of this border
	 * 
	 * @return the color of this border
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set the color of this border
	 * 
	 * @param color
	 *            - the color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Return the width of this border
	 * 
	 * @return the width of this border
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Set the width of this border
	 * 
	 * @param width
	 *            - the width of this border
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Return a description string for border widths with double lines. The
	 * description string includes inner line width, distance, and outline
	 * width. For example, "0.0008in 0.0346in 0.0346in".
	 * 
	 * @return - the description for border widths with double lines
	 */
	protected String getDoubleLineWidthDescription() {
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return null;
		DecimalFormat formater = new DecimalFormat(WidthFormatInInch, new DecimalFormatSymbols(Locale.US));
		return formater.format(innerLineWidth) + linearMeasure.toString() + " " + formater.format(distance)
				+ linearMeasure + " " + formater.format(outerLineWidth) + linearMeasure;
	}

	/**
	 * Set the border widths with double lines by a description string. The
	 * description string includes inner line width, distance, and outline
	 * width. For example, "0.0008in 0.0346in 0.0346in".
	 * 
	 * @param widthDesc
	 *            - the description string
	 */
	protected void setDoubleLineWidthByDescription(String widthDesc) {
		StringTokenizer st = new StringTokenizer(widthDesc);
		if (st.countTokens() != 3)
			throw new IllegalArgumentException(
					"The width description is invalid. Width description contains inner line width, distance and outer line width, such as '0.0154in 0154in 0154in'");

		String thisInnerWidth = st.nextToken();
		String thisDistance = st.nextToken();
		String thisOuterWidth = st.nextToken();

		setDoubleLineWidth(thisInnerWidth, thisDistance, thisOuterWidth);
	}

	/**
	 * Return the inner line width of border with double lines
	 * 
	 * @return - the inner line width
	 */
	public double getInnerLineWidth() {
		// DecimalFormat formater = new DecimalFormat(WidthFormatInInch);
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return getWidth();
		else
			return innerLineWidth;

	}

	/**
	 * Set the inner line width of border with double lines If the line style is
	 * not double, nothing will happen.
	 * 
	 * @param innerWidth
	 *            - the inner line width
	 */
	public void setInnerLineWidth(double innerWidth) {
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return;
		innerLineWidth = innerWidth;
	}

	/**
	 * Return the distance between inner line and outer line of border with
	 * double lines
	 * 
	 * @return - the distance between inner line and outer line.
	 */
	public double getDistance() {
		// DecimalFormat formater = new DecimalFormat(WidthFormatInInch);
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return getWidth();
		else
			return distance;

	}

	/**
	 * Set the distance between inner line and outer line of border with double
	 * lines. If the line style is not double, nothing will happen.
	 * 
	 * @param distance
	 *            - the distance between inner line and outer line.
	 */
	public void setDistance(double distance) {
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return;
		else
			this.distance = distance;

	}

	/**
	 * Return the outer line width of border with double lines
	 * 
	 * @return - the outer line width.
	 */
	public double getOuterLineWidth() {
		// DecimalFormat formater = new DecimalFormat(WidthFormatInInch);
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return getWidth();
		else
			return outerLineWidth;
	}

	/**
	 * Set the outer line width of border with double lines If the line style is
	 * not double, nothing will happen.
	 * 
	 * @param lineWidth
	 *            - the outer line width
	 */
	public void setOuterLineWidth(double lineWidth) {
		if (lineStyle == StyleTypeDefinitions.LineType.SINGLE)
			return;
		else
			this.outerLineWidth = lineWidth;
	}

	private void setDoubleLineWidth(String innerLineWidth, String distance, String outerLineWidth) {
		// get line measure for inner line
		this.linearMeasure = getLineMeasure(innerLineWidth);
		// get line width for distance
		StyleTypeDefinitions.SupportedLinearMeasure lm = getLineMeasure(distance);
		double lf = getLineWidth(distance);
		this.distance = linearMeasure.convert(lf, lm);
		// get line width for outer line
		lm = getLineMeasure(outerLineWidth);
		lf = getLineWidth(outerLineWidth);
		this.outerLineWidth = linearMeasure.convert(lf, lm);
		// get line width for inner line
		this.innerLineWidth = getLineWidth(innerLineWidth);
		// set line style
		lineStyle = StyleTypeDefinitions.LineType.DOUBLE;
	}

	private StyleTypeDefinitions.SupportedLinearMeasure getLineMeasure(String width) {
		for (SupportedLinearMeasure aIter : SupportedLinearMeasure.values()) {
			if (width.endsWith(aIter.toString())) {
				return aIter;
			}
		}
		return null;
	}

	private double getLineWidth(String width) {
		String floatValue = width.substring(0, width.length() - 2);
		return Double.parseDouble(floatValue);
	}

	/**
	 * Return a border which is same but with a different line measurement
	 * 
	 * @param newLineMeasure
	 *            - the new measurement
	 * @return the new border
	 */
	protected Border changeLineMeasure(StyleTypeDefinitions.SupportedLinearMeasure newLineMeasure) {
		if (newLineMeasure == linearMeasure)
			return this;
		Border newBorder = new Border();
		newBorder.width = newLineMeasure.convert(width, linearMeasure);
		newBorder.distance = newLineMeasure.convert(distance, linearMeasure);
		newBorder.innerLineWidth = newLineMeasure.convert(innerLineWidth, linearMeasure);
		newBorder.outerLineWidth = newLineMeasure.convert(outerLineWidth, linearMeasure);
		newBorder.color = this.color;
		newBorder.lineStyle = this.lineStyle;
		newBorder.linearMeasure = newLineMeasure;
		return newBorder;
	}

	@Override
	public String toString() {
		return "Border:" + getBorderDescription() + ";" + "BorderWidth:" + getDoubleLineWidthDescription();
	}

	private boolean doubleEqual(double d1, double d2) {
		if (this.linearMeasure == SupportedLinearMeasure.IN && Math.abs(d1 - d2) > 0.0002)
			return false;
		if (this.linearMeasure == SupportedLinearMeasure.PT && Math.abs(d1 - d2) > 0.02)
			return false;
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (o == Border.NONE && this == Border.NONE)
			return true;
		if (o instanceof Border) {
			Border aBorder = (Border) o;
			if (aBorder.getLineStyle() != this.getLineStyle())
				return false;
			if (!aBorder.color.toString().equals(this.color.toString()))
				return false;
			if (aBorder.linearMeasure != this.linearMeasure) {
				Border bBorder = aBorder.changeLineMeasure(this.linearMeasure);
				return this.equals(bBorder);
			}
			if (!doubleEqual(aBorder.width, this.width))
				return false;
			if (lineStyle == StyleTypeDefinitions.LineType.DOUBLE) {
				if (!doubleEqual(aBorder.distance, this.distance))
					return false;
				if (!doubleEqual(aBorder.innerLineWidth, this.innerLineWidth))
					return false;
				if (!doubleEqual(aBorder.outerLineWidth, this.outerLineWidth))
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Return the linear measurement
	 * 
	 * @return the the linear measurement
	 */
	public StyleTypeDefinitions.SupportedLinearMeasure getLinearMeasure() {
		return linearMeasure;
	}

	/**
	 * Set linear measurement
	 * 
	 * @param linearMeasure
	 *            the linear measurement
	 */
	public void setLinearMeasure(StyleTypeDefinitions.SupportedLinearMeasure linearMeasure) {
		this.linearMeasure = linearMeasure;
	}
}
