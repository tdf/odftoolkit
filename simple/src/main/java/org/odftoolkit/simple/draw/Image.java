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

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.AnyURI;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AnchorType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameHorizontalPosition;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FrameVerticalPosition;
import org.odftoolkit.simple.text.AbstractParagraphContainer;
import org.odftoolkit.simple.text.Paragraph;
import org.w3c.dom.DOMException;

/**
 * This class provides method to add/remove/get images.
 * 
 * @since 0.5.5
 */
public class Image extends Component {

	private static final String SLASH = "/";
	DrawImageElement mImageElement;
	Frame mFrame;
	URI mImageURI = null;
	DrawFrameElement mElement;
	Document mOwnerDocument;
	FrameStyleHandler mStyleHandler;

	private Image(DrawImageElement drawElement) {
		mOwnerDocument = (Document) ((OdfFileDom) drawElement.getOwnerDocument()).getDocument();
		mElement = (DrawFrameElement) drawElement.getParentNode();
		if (mElement == null) {
			Logger.getLogger(Image.class.getName()).log(Level.WARNING,
					"The image has no parent frame. A new frame will be created as its parent");
			OdfFileDom ownerDom = (OdfFileDom) drawElement.getOwnerDocument();
			mElement = ownerDom.newOdfElement(DrawFrameElement.class);
			mElement.appendChild(drawElement);
		}
		mFrame = Frame.getInstanceof(mElement);
		mImageElement = drawElement;
	}

	/**
	 * Get a image instance by an instance of <code>DrawImageElement</code>.
	 * 
	 * @param imageElement
	 *            - the instance of DrawImageElement
	 * @return an instance of image
	 */
	public static Image getInstanceof(DrawImageElement imageElement) {
		if (imageElement == null)
			return null;

		Image image = null;
		image = (Image) Component.getComponentByElement(imageElement);
		if (image != null)
			return image;

		Image myImage = new Image(imageElement);
		Component.registerComponent(myImage, imageElement);
		return myImage;

	}
	
	/**
	 * Add a hypertext reference to this image.
	 * 
	 * @param linkto
	 *            the hyperlink
	 * @since 0.6.5
	 * 
	 */
	public void setHyperlink(URI linkto) {
		mFrame.setHyperlink(linkto);
	}	
	
	/**
	 * Return the URI of hypertext reference if exists, or else, return null.
	 * 
	 * @return the URI of hyperlink if exists
	 */
	public URI getHyperlink() {
		return mFrame.getHyperlink();
	}
	

	/**
	 * Get the owner document of this image
	 * 
	 * @return the document who owns this image
	 */
	public Document getOwnerDocument() {
		return mOwnerDocument;
	}

	/**
	 * Return style handler for this frame
	 * 
	 * @return the style handler
	 */
	public FrameStyleHandler getStyleHandler() {
		if (mStyleHandler == null)
			mStyleHandler = new FrameStyleHandler(mFrame);
		return mStyleHandler;
	}

	/**
	 * Return the instance of "draw:image" element
	 * 
	 * @return the instance of "draw:image" element
	 */
	public DrawImageElement getOdfElement() {
		return mImageElement;
	}

	/**
	 * Return the frame who contains this image.
	 * 
	 * @return - the frame who contains this image
	 */
	public Frame getFrame() {
		return mFrame;
	}

	/**
	 * Create an image and append it at the end of a container element.
	 * 
	 * @param container
	 *            - the frame container element
	 * @param uri
	 *            - the URI of the image
	 * @return the instance of Image
	 */
	public static Image newImage(FrameContainer container, URI uri) {
		Image mImage;

		try {
			OdfElement parent = container.getFrameContainerElement();
			OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
			DrawFrameElement fElement = ownerDom.newOdfElement(DrawFrameElement.class);
			parent.appendChild(fElement);
			DrawImageElement imageElement = fElement.newDrawImageElement();
			// set uri and copy resource
			String packagePath = insertImageResourceIntoPackage((OdfSchemaDocument) ownerDom.getDocument(), uri);
			packagePath = packagePath.replaceFirst(ownerDom.getDocument().getDocumentPath(), "");
			URI newURI = configureInsertedImage((OdfSchemaDocument) ownerDom.getDocument(), imageElement, packagePath,
					false);

			// create image object
			mImage = new Image(imageElement);
			mImage.mImageURI = newURI;
			Component.registerComponent(mImage, imageElement);

			// set default alignment
			if (container instanceof Paragraph) {
				mImage.getStyleHandler().setAchorType(AnchorType.TO_PARAGRAPH);
			}

			return mImage;

		} catch (Exception e) {
			Logger.getLogger(Image.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Create an image and add it to a frame.
	 * 
	 * @param frame
	 *            - the frame which contains this image
	 * @param uri
	 *            - the URI of the image
	 * @return the instance of Image
	 */
	public static Image newImage(Frame frame, URI uri) {
		Image mImage;

		try {
			DrawFrameElement fElement = (DrawFrameElement) frame.getDrawFrameElement();
			OdfFileDom ownerDom = (OdfFileDom) fElement.getOwnerDocument();
			DrawImageElement imageElement = fElement.newDrawImageElement();
			// set uri and copy resource
			String packagePath = insertImageResourceIntoPackage((OdfSchemaDocument) ownerDom.getDocument(), uri);
			packagePath = packagePath.replaceFirst(ownerDom.getDocument().getDocumentPath(), "");
			URI newURI = configureInsertedImage((OdfSchemaDocument) ownerDom.getDocument(), imageElement, packagePath,
					true);

			// create image object
			mImage = new Image(imageElement);
			mImage.mImageURI = newURI;
			Component.registerComponent(mImage, imageElement);

			// set default alignment
			FrameContainer container = frame.mFrameContainer;
			if (container instanceof Paragraph) {
				mImage.getStyleHandler().setAchorType(AnchorType.TO_PARAGRAPH);
			}

			return mImage;

		} catch (Exception e) {
			Logger.getLogger(Image.class.getName()).severe(e.getMessage());
		}
		return null;
	}

	private static String insertImageResourceIntoPackage(OdfSchemaDocument mOdfSchemaDoc, URI imageUri)
			throws Exception {
		String imageRef = imageUri.toString();
		String mediaType = OdfFileEntry.getMediaTypeString(imageRef);
		String packagePath = getPackagePath(mOdfSchemaDoc, imageRef);
		mOdfSchemaDoc.getPackage().insert(imageUri, packagePath, mediaType);
		return packagePath;
	}

	/* Helper method */
	public static String getPackagePath(OdfSchemaDocument mOdfSchemaDoc, String imageRef) {
		if (imageRef.contains(SLASH)) {
			imageRef = imageRef.substring(imageRef.lastIndexOf(SLASH) + 1, imageRef.length());
		}
		String packagePath = OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + imageRef;
		return packagePath = mOdfSchemaDoc.getDocumentPath() + packagePath;
	}

	/* Helper method */
	public static URI configureInsertedImage(OdfSchemaDocument mOdfSchemaDoc, DrawImageElement imageElement,
			String packagePath, boolean isResetSize) throws Exception {
		// Set path to image attribute
		URI uri = new URI(AnyURI.encodePath(packagePath).toString());
		imageElement.setXlinkHrefAttribute(AnyURI.decodePath(uri.toString()));
		// Set mandatory attribute xlink:type
		imageElement.setXlinkTypeAttribute("simple");
		// A draw:image is always embedded in a draw:frame
		InputStream is = mOdfSchemaDoc.getPackage().getInputStream(packagePath);
		DrawFrameElement odfFrame = (DrawFrameElement) imageElement.getParentNode();
		Frame aFrame = Frame.getInstanceof(odfFrame);
		FrameRectangle oldRect = aFrame.getRectangle();
		if (oldRect.getLinearMeasure() != StyleTypeDefinitions.SupportedLinearMeasure.CM)
			oldRect.setLinearMeasure(StyleTypeDefinitions.SupportedLinearMeasure.CM);
		if (odfFrame != null) {
			BufferedImage image = ImageIO.read(is);
			int height = image.getHeight(null);
			int width = image.getWidth(null);
			odfFrame.setSvgHeightAttribute(Length.mapToUnit(String.valueOf(height) + "px", Unit.CENTIMETER));
			odfFrame.setSvgWidthAttribute(Length.mapToUnit(String.valueOf(width) + "px", Unit.CENTIMETER));
			if (isResetSize) {
				FrameRectangle newRect = aFrame.getRectangle();
				newRect.setX(oldRect.getX()+(oldRect.getWidth()-newRect.getWidth())/2);
				newRect.setY(oldRect.getY()+(oldRect.getHeight()-newRect.getHeight())/2);
				aFrame.setRectangle(newRect);
			}
		}
		return uri;
	}

	/**
	 * Return an iterator of image.
	 * 
	 * @param container the frame container.
	 * @return the image iterator.
	 */
	public static Iterator<Image> imageIterator(FrameContainer container) {
		return new SimpleImageIterator(container);
	}

	/**
	 * Remove the image from the document.
	 * <p>
	 * The resource is removed if it's only used by this object.
	 * 
	 * @return true if the image object is successfully removed; false if
	 *         otherwise.
	 */
	public boolean remove() {
		OdfElement containerElement = (OdfElement) mElement.getParentNode();
		try {
			mElement.removeChild(getOdfElement());
			if (mElement.hasChildNodes() == false)
				containerElement.removeChild(mElement);
			mOwnerDocument.removeElementLinkedResource(getOdfElement());
		} catch (DOMException exception) {
			Logger.getLogger(AbstractParagraphContainer.class.getName()).log(Level.WARNING, exception.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Update the image file with another resource.
	 * 
	 * @param imageUri
	 *            - the URI of the new image resource
	 */
	public void updateImage(URI imageUri) {
		try {
			// remove the old resource
			String packagePath = mImageURI.toString();
			mOwnerDocument.getPackage().remove(packagePath);
			// insert the new resource
			String imageRef = imageUri.toString();
			String mediaType = OdfFileEntry.getMediaTypeString(imageRef);
			mOwnerDocument.getPackage().insert(imageUri, packagePath, mediaType);
		} catch (Exception e) {
			Logger.getLogger(Image.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Get the image resource as an input stream.
	 * 
	 * @return - the input stream of the image resource
	 */
	public InputStream getImageInputStream() {
		return mOwnerDocument.getPackage().getInputStream(getInternalPath());
	}

	/**
	 * Get the internal path within the package of the image resource as a
	 * string.
	 * 
	 * @return - the internal path of the image resource
	 */
	public String getInternalPath() {
		try {
			if (mImageURI == null)
				mImageURI = new URI(mImageElement.getXlinkHrefAttribute());
			return mImageURI.toString();
		} catch (URISyntaxException e) {
			Logger.getLogger(Image.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Get the media type of the image resource
	 * 
	 * @return - the media type of the image resource
	 */
	public String getMediaTypeString() {
		return mOwnerDocument.getPackage().getMediaTypeString(getInternalPath());
	}

	private static class SimpleImageIterator implements Iterator<Image> {

		private OdfElement containerElement;
		private Image nextElement = null;
		private Image tempElement = null;

		private SimpleImageIterator(FrameContainer container) {
			containerElement = container.getFrameContainerElement();
		}

		public boolean hasNext() {
			tempElement = findNext(nextElement);
			return (tempElement != null);
		}

		public Image next() {
			if (tempElement != null) {
				nextElement = tempElement;
				tempElement = null;
			} else {
				nextElement = findNext(nextElement);
			}
			if (nextElement == null) {
				return null;
			} else {
				return nextElement;
			}
		}

		public void remove() {
			if (nextElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			containerElement.removeChild(nextElement.getOdfElement());
		}

		private Image findNext(Image thisImage) {
			DrawImageElement nextFrame = null;
			if (thisImage == null) {
				nextFrame = OdfElement.findFirstChildNode(DrawImageElement.class, containerElement);
			} else {
				nextFrame = OdfElement.findNextChildNode(DrawImageElement.class, thisImage.getOdfElement());
			}

			if (nextFrame != null) {
				return Image.getInstanceof(nextFrame);
			}
			return null;
		}
	}

	/**
	 * Set the name of this image.
	 * 
	 * @param name
	 *            - the name of the image
	 */
	public void setName(String name) {
		mFrame.setName(name);
	}

	/**
	 * Get the name of this image.
	 * 
	 * @return the name of the image
	 */
	public String getName() {
		return mFrame.getName();
	}

	/**
	 * Set the rectangle used by this image
	 * 
	 * @param rectangle
	 *            - the rectangle used by this image
	 */
	public void setRectangle(FrameRectangle rectangle) {
		mFrame.setRectangle(rectangle);
	}

	/**
	 * Return the rectangle used by this image
	 * 
	 * @return - the rectangle
	 */
	public FrameRectangle getRectangle() {
		return mFrame.getRectangle();
	}

	/**
	 * Set the title of this image
	 * 
	 * @param title
	 *            - the title of this image
	 */
	public void setTitle(String title) {
		mFrame.setTitle(title);
	}

	/**
	 * Get the title of this image
	 * 
	 * @return - the title of this image
	 */
	public String getTitle() {
		return mFrame.getTitle();
	}

	/**
	 * Get the description of this image
	 * 
	 * @return - the description of this image
	 */
	public String getDesciption() {
		return mFrame.getDesciption();
	}

	/**
	 * Set the description of this image.
	 * 
	 * @param description
	 *            - the description of this image
	 */
	public void setDescription(String description) {
		mFrame.setDescription(description);
	}

	/**
	 * Set the horizontal position
	 * 
	 * @param horizontalPos
	 *            - the horizontal position
	 */
	public void setHorizontalPosition(FrameHorizontalPosition horizontalPos) {
		getStyleHandler().setHorizontalPosition(horizontalPos);
	}

	/**
	 * Set the vertical position
	 * 
	 * @param verticalPos
	 *            - the vertical position
	 */
	public void setVerticalPosition(FrameVerticalPosition verticalPos) {
		getStyleHandler().setVerticalPosition(verticalPos);
	}

	/**
	 * Return the horizontal position
	 * 
	 * @return the horizontal position
	 */
	public FrameHorizontalPosition getHorizontalPosition() {
		return getStyleHandler().getHorizontalPosition();
	}

	/**
	 * Return the vertical position
	 * 
	 * @return the vertical position
	 */
	public FrameVerticalPosition getVerticalPosition() {
		return getStyleHandler().getVerticalPosition();
	}

}
