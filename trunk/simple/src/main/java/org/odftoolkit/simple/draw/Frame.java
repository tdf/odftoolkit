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

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawAElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeImageElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgTitleElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class presents frame object. It provides method to get/set frame
 * properties, content, and and styles. A frame is a container for enhanced
 * content like text boxes, images or objects. A frame may contain multiple
 * renditions of content.
 * 
 * @since 0.5
 */
public class Frame extends Component {

	/**
	 * Attributes:
	 * 
	 * draw:caption-id 19.115, draw:class-names 19.120, draw:copy-of 19.126,
	 * draw:id 19.187.3, draw:layer 19.189, draw:name 19.197.10, draw:style-name
	 * 19.219.13, draw:text-style-name 19.227, draw:transform 19.228,
	 * draw:z-index 19.231, presentation:class 19.389, presentation:class-names
	 * 19.390, presentation:placeholder 19.407, presentation:style-name 19.422,
	 * presentation:user-transformed 19.427, style:rel-height 19.509,
	 * style:rel-width 19.510.2, svg:height 19.539.8, svg:width 19.571.10, svg:x
	 * 19.573.5, svg:y 19.577.5, table:end-cell-address 19.627, table:end-x
	 * 19.632, table:end-y 19.633, table:table-background 19.728,
	 * text:anchor-page-number 19.753, text:anchor-type 19.754 xml:id 19.914.
	 */
	protected DrawFrameElement mElement;
	protected Document mOwnerDocument;
	protected FrameContainer mFrameContainer;
	protected FrameStyleHandler mStyleHandler;

	protected Frame(DrawFrameElement element) {
		mElement = element;
		mOwnerDocument = (Document) ((OdfFileDom) mElement.getOwnerDocument()).getDocument();
		mFrameContainer = null;
	}

	/**
	 * Get a frame instance by an instance of <code>DrawFrameElement</code>.
	 * 
	 * @param element
	 *            - the instance of DrawFrameElement
	 * @return an instance of frame
	 */
	protected static Frame getInstanceof(DrawFrameElement element) {
		Frame frame = null;
		frame = (Frame) Component.getComponentByElement(element);
		if (frame != null)
			return frame;

		frame = new Frame(element);
		// Component.registerComponent(frame, element);
		return frame;
	}

	/**
	 * Create an instance of frame
	 * <p>
	 * The frame will be added at the end of this container.
	 * 
	 * @param container
	 *            - the frame container that contains this frame.
	 */
	protected static Frame newFrame(FrameContainer container) {
		Frame frame = null;
		OdfElement parent = container.getFrameContainerElement();
		OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
		DrawFrameElement fElement = ownerDom.newOdfElement(DrawFrameElement.class);
		parent.appendChild(fElement);
		frame = new Frame(fElement);
		frame.mFrameContainer = container;
		// Component.registerComponent(frame, fElement);

		return frame;
	}

	/**
	 * Set the name of this frame.
	 * 
	 * @param name
	 *            - the name of the frame
	 */
	public void setName(String name) {
		mElement.setDrawNameAttribute(name);
	}

	/**
	 * Get the name of this frame.
	 * 
	 * @return the name of the frame
	 */
	public String getName() {
		return mElement.getDrawNameAttribute();
	}

	// /**
	// * Get the style handler of this frame.
	// *
	// * @return the style handler of this frame
	// */
	// public FrameStyleHandler getFrameStyleHandler() {
	// return null;
	// }
	//
	// /**
	// * Get the paragraph style handler of this frame.
	// *
	// * @return the paragraph style handler of this frame
	// */
	// public DefaultStyleHandler getParagraphStyleHandler() {
	// return null;
	// }

	/**
	 * Get the instance of <code>DrawFrameElement</code> which represents this
	 * frame.
	 * 
	 * @return the instance of <code>DrawFrameElement</code>
	 */
	public OdfElement getOdfElement() {
		return mElement;
	}

	/**
	 * Get the instance of <code>DrawFrameElement</code> which represents this
	 * frame.
	 * 
	 * @return the instance of <code>DrawFrameElement</code>
	 */
	public DrawFrameElement getDrawFrameElement() {
		return mElement;
	}

	/**
	 * Set the rectangle used by this frame
	 * 
	 * @param rectangle
	 *            - the rectangle used by this frame
	 */
	public void setRectangle(FrameRectangle rectangle) {
		String linemeasure = rectangle.getLinearMeasure().toString();
		if (rectangle.getWidth() > 0)
			mElement.setSvgWidthAttribute(rectangle.getWidth() + linemeasure);
		if (rectangle.getHeight() > 0)
			mElement.setSvgHeightAttribute(rectangle.getHeight() + linemeasure);
		if (rectangle.getX() > 0)
			mElement.setSvgXAttribute(rectangle.getX() + linemeasure);
		if (rectangle.getY() > 0)
			mElement.setSvgYAttribute(rectangle.getY() + linemeasure);
	}

	/**
	 * Return the rectangle used by this frame
	 * 
	 * @return - the rectangle
	 */
	public FrameRectangle getRectangle() {
		try {
			FrameRectangle rectange = new FrameRectangle(mElement.getSvgXAttribute(), mElement.getSvgYAttribute(),
					mElement.getSvgWidthAttribute(), mElement.getSvgHeightAttribute());
			return rectange;
		} catch (Exception e) {
			Logger.getLogger(Frame.class.getName()).log(Level.FINE, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Set the title of this text box
	 * 
	 * @param title
	 *            - the title of this text box
	 */
	public void setTitle(String title) {
		SvgTitleElement titleElement = OdfElement.findFirstChildNode(SvgTitleElement.class, mElement);
		if (titleElement == null)
			titleElement = mElement.newSvgTitleElement();
		titleElement.setTextContent(title);
	}

	/**
	 * Get the title of this text box
	 * 
	 * @return - the title of this text box
	 */
	public String getTitle() {
		SvgTitleElement titleElement = OdfElement.findFirstChildNode(SvgTitleElement.class, mElement);
		if (titleElement == null)
			return null;
		else
			return titleElement.getTextContent();
	}

	/**
	 * Get the description of this text box
	 * 
	 * @return - the description of this text box
	 */
	public String getDesciption() {
		SvgDescElement descElement = OdfElement.findFirstChildNode(SvgDescElement.class, mElement);
		if (descElement == null)
			return null;
		else
			return descElement.getTextContent();
	}

	/**
	 * Set the description of this text box.
	 * 
	 * @param description
	 *            - the description of this text box
	 */
	public void setDescription(String description) {
		SvgDescElement descElement = OdfElement.findFirstChildNode(SvgDescElement.class, mElement);
		if (descElement == null)
			descElement = mElement.newSvgDescElement();
		descElement.setTextContent(description);
	}
	
	/**
	 * Add a hypertext reference to this frame.
	 * 
	 * @param linkto
	 *            the hyperlink
	 * @since 0.6.5
	 * 
	 */
	public void setHyperlink(URI linkto) {
		OdfElement thisFrame = getOdfElement();
		OdfElement parent = (OdfElement) thisFrame.getParentNode();
		// if this frame has a hyperlink setting
		if (parent instanceof DrawAElement) {
			((DrawAElement) parent).setXlinkHrefAttribute(linkto.toString());
			return;
		}
		// if this frame has not hyperlink setting
		Node brother = thisFrame.getNextSibling();
		if (parent instanceof OfficeImageElement)
			return;
		try {
			DrawAElement aElement = mOwnerDocument.getContentDom()
					.newOdfElement(DrawAElement.class);
			aElement.setXlinkHrefAttribute(linkto.toString());
			aElement.setXlinkTypeAttribute("simple");
			parent.removeChild(thisFrame);
			aElement.appendChild(thisFrame);
			if (brother == null)
				parent.appendChild(aElement);
			parent.insertBefore(aElement, brother);
		} catch (Exception e) {
			Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Return the URI of hypertext reference if exists, or else, return null.
	 * 
	 * @return the URI of hyperlink if exists
	 */
	public URI getHyperlink() {
		OdfElement thisFrame = getOdfElement();
		OdfElement parent = (OdfElement) thisFrame.getParentNode();
		// if this frame has a hyperlink setting
		if (parent instanceof DrawAElement) {

			return URI.create(((DrawAElement) parent).getXlinkHrefAttribute());
		}
		return null;
	}

	/**
	 * Return style handler for this frame
	 * 
	 * @return the style handler
	 */
	public FrameStyleHandler getStyleHandler() {
		if (mStyleHandler == null)
			mStyleHandler = new FrameStyleHandler(this);
		return mStyleHandler;
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
		getStyleHandler().setBackgroundColor(color);
	}

	private void removeContent() {
		NodeList nodeList = mElement.getChildNodes();
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Node node;
			node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				mElement.removeChild(node);
			else if (node.getNodeType() == Node.ELEMENT_NODE) {
				mElement.removeChild(node);
			}
		}
		// mElement.removeAttributeNS(OdfDocumentNamespace.PRESENTATION.getUri(),
		// "class");
		mElement.removeAttributeNS(OdfDocumentNamespace.PRESENTATION.getUri(), "placeholder");
	}

	/**
	 * Add a image to the frame after all the contents get removed.
	 * 
	 * @since 0.5.5
	 */
	public Image setImage(URI uri) {
		removeContent();
		Image image = Image.newImage(this, uri);
		return image;
	}

	// /**
	// * Set the frame to be transparent, with none border and none fill color.
	// */
	// public void setTransparent() {
	// }
	// /**
	// * Set the
	// */
	// public void setFitWeightToText()
	// {
	//		
	// }
	//	
	// public void setFitHeightToText()
	// {
	//		
	// }
}
