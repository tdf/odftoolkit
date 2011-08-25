/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.draw;

import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawFill;

/**
 * This class provides functions to handle the style of a frame.
 * 
 * @since 0.5
 */
public class FrameStyleHandler extends DefaultStyleHandler {

	private Frame mFrame;

	public FrameStyleHandler(Frame frame) {
		super(frame.getDrawFrameElement());
		mFrame = frame;
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
		getGraphicPropertiesForWrite().setBorders(bordersType, border);
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
	 *            - the stroke type
	 * @param color
	 *            - the color of the stroke
	 * @param widthDesc
	 *            - the width description of the stroke, e.g. "0.01in"
	 * @param dashStyleName
	 *            - the dash style name
	 * @see org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawStroke
	 */
	public void setStroke(StyleTypeDefinitions.OdfDrawStroke stroke, Color color, String widthDesc, String dashStyleName) {
		getGraphicPropertiesForWrite().setStroke(stroke, color, widthDesc, dashStyleName);
	}

	/**
	 * Set the background color of this frame.
	 * <p>
	 * If the parameter is null, there will be no background color defined for
	 * this frame. The old setting of background color will be removed.
	 * 
	 * @param color
	 *            - the background color to be set
	 */
	public void setBackgroundColor(Color color) {
		if (color == null)
			getGraphicPropertiesForWrite().setFill(OdfDrawFill.NONE, null);
		else
			getGraphicPropertiesForWrite().setFill(OdfDrawFill.SOLID, color);

	}

	/**
	 * Set whether the content of a frame is displayed in the background or
	 * foreground. If it's displayed in the background, the content wouldn't be
	 * selected or moved.
	 * 
	 * @param isBackgroundFrame
	 *            If <code>true</code>, the frame is displayed in the
	 *            background.
	 * @since 0.5.5
	 */
	public void setBackgroundFrame(boolean isBackgroundFrame) {
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();
		graphicPropertiesForWrite.setStyleRunThrough(isBackgroundFrame);
	}
}
