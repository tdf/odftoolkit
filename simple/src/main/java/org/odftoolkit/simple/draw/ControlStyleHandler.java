package org.odftoolkit.simple.draw;

import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.style.GraphicProperties;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalRelative;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalRelative;

/**
 * This class provides functions to handle the style of a form control.
 * 
 * @since 0.8
 */
public class ControlStyleHandler extends DefaultStyleHandler {

	private Control mControl;

	public ControlStyleHandler(Control control) {
		super(control.getOdfElement());
		mControl = control;
	}

	/**
	 * Set how a form control is bound to a text document. Default position
	 * relative and alignment will be set.
	 * 
	 * <p>
	 * If the document is not text document, nothing will happen.
	 * 
	 * @param achorType
	 *            - the point at which a form control is bound to a text
	 *            document
	 */
	public void setAchorType(StyleTypeDefinitions.AnchorType achorType) {
		if (!mDocument.getMediaTypeString().equals(
				Document.OdfMediaType.TEXT.getMediaTypeString())
				&& !mDocument.getMediaTypeString().equals(
						Document.OdfMediaType.TEXT_TEMPLATE
								.getMediaTypeString()))
			return;
		GraphicProperties graphicPropertiesForWrite = getGraphicPropertiesForWrite();

		DrawControlElement controlElement = (DrawControlElement) mOdfElement;
		controlElement.setTextAnchorTypeAttribute(achorType.toString());

		// set default relative
		switch (achorType) {
		case AS_CHARACTER:
			graphicPropertiesForWrite
					.setVerticalRelative(VerticalRelative.BASELINE);
			graphicPropertiesForWrite
					.setVerticalPosition(FrameVerticalPosition.TOP);
			break;
		case TO_CHARACTER:
			graphicPropertiesForWrite
					.setVerticalRelative(VerticalRelative.PARAGRAPH);
			graphicPropertiesForWrite
					.setVerticalPosition(FrameVerticalPosition.TOP);
			graphicPropertiesForWrite
					.setHorizontalRelative(HorizontalRelative.PARAGRAPH);
			graphicPropertiesForWrite
					.setHorizontalPosition(FrameHorizontalPosition.CENTER);
			break;
		case TO_PAGE:
			controlElement.setTextAnchorPageNumberAttribute(Integer.valueOf(1));
			graphicPropertiesForWrite
					.setVerticalRelative(VerticalRelative.PAGE);
			graphicPropertiesForWrite
					.setVerticalPosition(FrameVerticalPosition.FROMTOP);
			graphicPropertiesForWrite
					.setHorizontalRelative(HorizontalRelative.PARAGRAPH);
			graphicPropertiesForWrite
					.setHorizontalPosition(FrameHorizontalPosition.FROMLEFT);
			break;
		case TO_PARAGRAPH:
			graphicPropertiesForWrite
					.setVerticalRelative(VerticalRelative.PARAGRAPH);
			graphicPropertiesForWrite
					.setVerticalPosition(FrameVerticalPosition.TOP);
			graphicPropertiesForWrite
					.setHorizontalRelative(HorizontalRelative.PARAGRAPH);
			graphicPropertiesForWrite
					.setHorizontalPosition(FrameHorizontalPosition.CENTER);
			break;
		case TO_FRAME:
			break;
		}
	}
}
