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

package org.odftoolkit.simple.draw;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.SupportedLinearMeasure;

/**
 * This class represents a rectangle used by a frame object.
 * <p>
 * A Rectangle specifies an area in a coordinate space that is enclosed by the
 * Rectangle object's top-left point (x, y) in the coordinate space, its width,
 * and its height.
 * 
 * @since 0.5
 * 
 */
public class FrameRectangle {

	/**
	 * the x-axis coordinate
	 */
	private double x;
	/**
	 * the y-axis coordinate
	 */
	private double y;
	/**
	 * The width
	 */
	private double width;
	/**
	 * The height
	 */
	private double height;
	private StyleTypeDefinitions.SupportedLinearMeasure linearMeasure;

	private static final String WidthFormat = "#.####";
	private DecimalFormat formater = new DecimalFormat(WidthFormat, new DecimalFormatSymbols(Locale.US));

	/**
	 * @return the x-axis coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x
	 *            - the x-axis coordinate to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y-axis coordinate
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y
	 *            - the y-axis coordinate to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            - the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            - the height to set
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @return the line measurement
	 */
	public StyleTypeDefinitions.SupportedLinearMeasure getLinearMeasure() {
		return linearMeasure;
	}

	/**
	 * @param newLinearMeasure
	 *            the line measurement to set
	 */
	public void setLinearMeasure(StyleTypeDefinitions.SupportedLinearMeasure newLinearMeasure) {
		if (this.linearMeasure != linearMeasure) {
			x = newLinearMeasure.convert(x, linearMeasure);
			y = newLinearMeasure.convert(y, linearMeasure);
			width = newLinearMeasure.convert(width, linearMeasure);
			height = newLinearMeasure.convert(height, linearMeasure);
		}
		this.linearMeasure = newLinearMeasure;
	}

	/**
	 * Create an instance of FrameRectangle with the top-left point (x, y),
	 * width, height and the measurement
	 * 
	 * @param x
	 *            - the x-axis coordinate
	 * @param y
	 *            - the y-axis coordinate
	 * @param width
	 *            - the width
	 * @param height
	 *            - the height
	 * @param linearMeasure
	 *            - the measurement
	 */
	public FrameRectangle(double x, double y, double width, double height,
			StyleTypeDefinitions.SupportedLinearMeasure linearMeasure) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.linearMeasure = linearMeasure;
	}

	/**
	 * Create an instance of FrameRectangle with the descriptions of top-left
	 * point (x, y), width and height.
	 * 
	 * @param xDesc
	 *            - the x-axis coordinate with measurement
	 * @param yDesc
	 *            - the y-axis coordinate with measurement
	 * @param widthDesc
	 *            - the width with measurement
	 * @param heightDesc
	 *            - the height with measurement
	 */
	public FrameRectangle(String xDesc, String yDesc, String widthDesc, String heightDesc) {
		StyleTypeDefinitions.SupportedLinearMeasure tempXMeasure, tempYMeasure, tempWMeasure, tempHMeasure;

		// get the basic information of width and measurement
		if (xDesc == null || xDesc.length() == 0) {
			x = 0;
			tempXMeasure = StyleTypeDefinitions.SupportedLinearMeasure.CM;
		} else {
			x = getLineWidth(xDesc);
			tempXMeasure = getLineMeasure(xDesc);
		}

		if (yDesc == null || yDesc.length() == 0) {
			y = 0;
			tempYMeasure = StyleTypeDefinitions.SupportedLinearMeasure.CM;
		} else {
			y = getLineWidth(yDesc);
			tempYMeasure = getLineMeasure(yDesc);
		}

		if (widthDesc == null || widthDesc.length() == 0) {
			width = 0;
			tempWMeasure = StyleTypeDefinitions.SupportedLinearMeasure.CM;
		} else {
			width = getLineWidth(widthDesc);
			tempWMeasure = getLineMeasure(widthDesc);
		}

		if (heightDesc == null || heightDesc.length() == 0) {
			height = 0;
			tempHMeasure = StyleTypeDefinitions.SupportedLinearMeasure.CM;
		} else {
			height = getLineWidth(heightDesc);
			tempHMeasure = getLineMeasure(heightDesc);
		}

		// if all the measurement are empty, an exception will be thrown.
		if (tempXMeasure == null && tempYMeasure == null && tempWMeasure == null && tempHMeasure == null)
			throw new RuntimeException(xDesc + "," + yDesc + "," + widthDesc + "," + heightDesc + ","
					+ " are not valid line description!");

		// get the first unempty measurement definition
		if (tempXMeasure != null)
			linearMeasure = tempXMeasure;
		else if (tempYMeasure != null)
			linearMeasure = tempYMeasure;
		else if (tempWMeasure != null)
			linearMeasure = tempWMeasure;
		else if (tempHMeasure != null)
			linearMeasure = tempHMeasure;

		// verify if the measurement is same, or else, change the value
		if (tempXMeasure != null && tempXMeasure != linearMeasure)
			x = linearMeasure.convert(x, tempXMeasure);
		if (tempYMeasure != null && tempYMeasure != linearMeasure)
			y = linearMeasure.convert(y, tempYMeasure);
		if (tempWMeasure != null && tempWMeasure != linearMeasure)
			width = linearMeasure.convert(width, tempWMeasure);
		if (tempHMeasure != null && tempHMeasure != linearMeasure)
			height = linearMeasure.convert(height, tempHMeasure);
	}

	/**
	 * Return the x-axis coordinate with measurement
	 * 
	 * @return the x-axis coordinate with measurement
	 */
	public String getXDesc() {
		return formater.format(x) + linearMeasure.toString();
	}

	/**
	 * Return the y-axis coordinate with measurement
	 * 
	 * @return the y-axis coordinate with measurement
	 */
	public String getYDesc() {
		return formater.format(y) + linearMeasure.toString();
	}

	/**
	 * Return the width with measurement
	 * 
	 * @return the width with measurement
	 */
	public String getWidthDesc() {
		return formater.format(width) + linearMeasure.toString();
	}

	/**
	 * Return the height with measurement
	 * 
	 * @return the height with measurement
	 */
	public String getHeigthDesc() {
		return formater.format(height) + linearMeasure.toString();
	}

	private double getLineWidth(String width) {
		String floatValue = width.substring(0, width.length() - 2);
		return Double.parseDouble(floatValue);
	}

	private StyleTypeDefinitions.SupportedLinearMeasure getLineMeasure(String width) {
		for (SupportedLinearMeasure aIter : SupportedLinearMeasure.values()) {
			if (width.endsWith(aIter.toString())) {
				return aIter;
			}
		}
		return null;
	}
}
