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

import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.OdfDrawFill;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;

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
	
	/**
	 * Set how a frame is bound to a text document. Default position relative and alignment will be set.
	 * 
	 * <p>If the document is not text document, nothing will happen.
	 * @param achorType - the point at which a frame is bound to a text document
	 */
	public void setAchorType(StyleTypeDefinitions.AnchorType achorType)
	{
		if (!mDocument.getMediaTypeString().equals(Document.OdfMediaType.TEXT.getMediaTypeString()) &&
				!mDocument.getMediaTypeString().equals(Document.OdfMediaType.TEXT_TEMPLATE.getMediaTypeString()))
			return;
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();

		DrawFrameElement frameElement = (DrawFrameElement)mOdfElement;
		frameElement.setTextAnchorTypeAttribute(achorType.toString());
		
		//set default relative
		switch(achorType)
		{
		case AS_CHARACTER:
			graphicPropertiesForWrite.setVerticalRelative(VerticalRelative.BASELINE);
			graphicPropertiesForWrite.setVerticalPosition(FrameVerticalPosition.TOP);
			break;
		case TO_CHARACTER:
			graphicPropertiesForWrite.setVerticalRelative(VerticalRelative.PARAGRAPH);
			graphicPropertiesForWrite.setVerticalPosition(FrameVerticalPosition.TOP);
			graphicPropertiesForWrite.setHorizontalRelative(HorizontalRelative.PARAGRAPH);
			graphicPropertiesForWrite.setHorizontalPosition(FrameHorizontalPosition.CENTER);
			break;
		case TO_PAGE:
			graphicPropertiesForWrite.setVerticalRelative(VerticalRelative.PAGE);
			graphicPropertiesForWrite.setVerticalPosition(FrameVerticalPosition.TOP);
			graphicPropertiesForWrite.setHorizontalRelative(HorizontalRelative.PAGE);
			graphicPropertiesForWrite.setHorizontalPosition(FrameHorizontalPosition.CENTER);
			break;
		case TO_PARAGRAPH:
			graphicPropertiesForWrite.setVerticalRelative(VerticalRelative.PARAGRAPH);
			graphicPropertiesForWrite.setVerticalPosition(FrameVerticalPosition.TOP);
			graphicPropertiesForWrite.setHorizontalRelative(HorizontalRelative.PARAGRAPH);
			graphicPropertiesForWrite.setHorizontalPosition(FrameHorizontalPosition.CENTER);
			break;
		case TO_FRAME:
			break;
		}
	}
	
	/**
	 * Set the horizontal position
	 * 
	 * @param horizontalPos
	 *            - the horizontal position
	 */
	public void setHorizontalPosition(FrameHorizontalPosition horizontalPos) {
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();
		graphicPropertiesForWrite.setHorizontalPosition(horizontalPos);
	}
	
	/**
	 * Set the horizontal relative
	 * 
	 * @param relative
	 *            - the horizontal relative
	 */
	public void setHorizontalRelative(HorizontalRelative relative)
	{
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();
		graphicPropertiesForWrite.setHorizontalRelative(relative);
	}
	
	/**
	 * Set the vertical relative
	 * 
	 * @param relative
	 *            - the vertical relative
	 */
	public void setVerticalRelative(VerticalRelative relative)
	{
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();
		graphicPropertiesForWrite.setVerticalRelative(relative);
	}

	/**
	 * Set the vertical position
	 * 
	 * @param verticalPos
	 *            - the vertical position
	 */
	public void setVerticalPosition(FrameVerticalPosition verticalPos) {
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();
		graphicPropertiesForWrite.setVerticalPosition(verticalPos);
	}

	/**
	 * Return the horizontal position
	 * 
	 * @return the horizontal position
	 */
	public FrameHorizontalPosition getHorizontalPosition() {
		GraphicProperties graphicPropertiesForRead = getGraphicPropertiesForRead();
		return graphicPropertiesForRead.getHorizontalPosition();
	}

	/**
	 * Return the vertical position
	 * 
	 * @return the vertical position
	 */
	public FrameVerticalPosition getVerticalPosition() {
		GraphicProperties graphicPropertiesForRead = getGraphicPropertiesForRead();
		return graphicPropertiesForRead.getVerticalPosition();
	}
	
	/**
	 * Return the vertical relative
	 * @return the vertical relative
	 */
	public VerticalRelative getVerticalRelative()
	{
		GraphicProperties graphicPropertiesForRead = getGraphicPropertiesForRead();
		return graphicPropertiesForRead.getVerticalRelative();
	}
	
	/**
	 * Return the horizontal relative
	 * @return the horizontal relative
	 */
	public HorizontalRelative getHorizontalRelative()
	{
		GraphicProperties graphicPropertiesForRead = getGraphicPropertiesForRead();
		return graphicPropertiesForRead.getHorizontalRelative();
	}

}
