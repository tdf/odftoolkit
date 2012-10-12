/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.incubator.doc.draw;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.AnyURI;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.w3c.dom.NodeList;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 * 
 * @deprecated As of release 0.8.8, replaced by {@link org.odftoolkit.simple.draw.Image} in Simple API.
 */
public class OdfDrawImage extends DrawImageElement {

	private static final long serialVersionUID = 8409319888919451149L;
	private URI mImageURI;
	// OdfPackage necessary to adapt the manifest referencing the image
	private OdfPackage mOdfPackage;
	private OdfSchemaDocument mOdfSchemaDocument;
	private static final String SLASH = "/";

	/** Creates a new instance of this class
	 * @param ownerDoc The XML DOM containing the draw:image element
	 */
	public OdfDrawImage(OdfFileDom ownerDoc) {
		super(ownerDoc);
		mOdfSchemaDocument = (OdfSchemaDocument) ownerDoc.getDocument();
		mOdfPackage = mOdfSchemaDocument.getPackage();
	}

	/**
	 * Return the URI for this image
	 * @return   the URI of image
	 */
	public URI getImageUri() {
		try {
			if (mImageURI == null) {
				mImageURI = new URI(AnyURI.encodePath(this.getXlinkHrefAttribute().toString()));
			}
		} catch (URISyntaxException ex) {
			Logger.getLogger(OdfDrawImage.class.getName()).log(Level.SEVERE, null, ex);
		}
		return mImageURI;
	}

	/**
	 * The image path will be stored as URI of the href attribute
	 *
	 * @param packagePath The relative path from the package root to the image
	 */
	public void setImagePath(String packagePath) {
		try {			
			packagePath = packagePath.replaceFirst(mOdfSchemaDocument.getDocumentPath(), "");
			URI uri = new URI(AnyURI.encodePath(packagePath).toString());
			this.setXlinkHrefAttribute(AnyURI.decodePath(uri.toString()));
			mImageURI = uri;
		} catch (URISyntaxException ex) {
			Logger.getLogger(OdfDrawImage.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/* Helper method */
	private String getPackagePath(String imageRef) {
		if (imageRef.contains(SLASH)) {
			imageRef = imageRef.substring(imageRef.lastIndexOf(SLASH) + 1, imageRef.length());
		}
		String packagePath = OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + imageRef;
		return packagePath = mOdfSchemaDocument.getDocumentPath() + packagePath;
	}

	/* Helper method */
	private void configureInsertedImage(String packagePath) throws Exception {
		// Set path to image attribute
		setImagePath(packagePath);
		// Set mandatory attribute xlink:type
		setXlinkTypeAttribute("simple");
		// A draw:image is always embedded in a draw:frame
		InputStream is = mOdfPackage.getInputStream(packagePath);
		OdfDrawFrame odfFrame = (OdfDrawFrame) this.getParentNode();
		if (odfFrame != null) {
			BufferedImage image = ImageIO.read(is);
			// some image formats like SVG might not be understood by ImageIO			
			if (image != null)
			{
				int height = image.getHeight(null);
				int width = image.getWidth(null);
				odfFrame.setSvgHeightAttribute(Length.mapToUnit(String.valueOf(height) + "px", Unit.CENTIMETER));
				odfFrame.setSvgWidthAttribute(Length.mapToUnit(String.valueOf(width) + "px", Unit.CENTIMETER));
			}
		}
	}

	/**
	 * Inserts the image file from the URI to the ODF package named similar as in the URI.
	 * The manifest is adapted using the media type according to the suffix. Existing images are replaced.
	 * Note: Default image seize will only be set, if the draw:image had been added to its draw:frame prior.
	 *
	 * @param imageUri The URI of the image that will be added as stream to the package
	 *                 in the 'Pictures/' graphic directory with the same image file name as in the URI.
	 *                 If the imageURI is relativ first the user.dir is taken to make it absolute.
	 * @return              Returns the package path of the image, which was created based on the given URI.
	 * @throws Exception    If the image provided by the URI, could not be added as stream to the ODF package.
	 *
	 */
	public String newImage(URI imageUri) throws Exception {
		String imageRef = imageUri.toString();
		String mediaType = OdfFileEntry.getMediaTypeString(imageRef);
		String packagePath = getPackagePath(imageRef);
		mOdfPackage.insert(imageUri, packagePath, mediaType);
		configureInsertedImage(packagePath);
		return packagePath;
	}

	/**
	 *
	 * Inserts the image file from the stream to the ODF package named similar as in the provided path..
	 * The manifest is adapted using given media type. Existing images are replaced.
	 * @param is            InputStream to be added to the ODF package
	 * @param packagePath   Internal path of the image in the package
	 * @param mediaType     The mediaType of the image.
	 *                      Can be obtained by the OdfFileEntry class findMediaType(String fileRef).
	 * @throws Exception    If the given stream could not be added to the ODF package at the packagePatch
	 */
	public void newImage(InputStream is, String packagePath, String mediaType) throws Exception {
		mOdfPackage.insert(is, packagePath, mediaType);
		configureInsertedImage(packagePath);
	}

	/**
	 * The method returns the specific one or more images by image path since the image may be inserted to the document several times.
	 * @param doc the document the image belongs to
	 * @param imagePath	the internal package path of the image.
	 * @return			an Image list that match the given image path
	 *                  if no images is found under the given path, return an empty list.
	 */
	public static List<OdfDrawImage> getImageByPath(OdfSchemaDocument doc, String imagePath) {
		ArrayList<OdfDrawImage> imageList = new ArrayList<OdfDrawImage>();

		try {
			NodeList imageNodes = doc.getContentDom().getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "image");

			for (int i = 0; i < imageNodes.getLength(); i++) {
				OdfDrawImage image = (OdfDrawImage) imageNodes.item(i);
				if (image.getXlinkHrefAttribute().equals(imagePath)) {
					imageList.add(image);
				}
			}
			if (imageList.size() > 0) {
				return imageList;
			}
		} catch (Exception ex) {
			Logger.getLogger(OdfDrawImage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return imageList;
	}

	/**
	 * The method deletes one or more images from image container by image path.
	 * @param doc the document the image should be deleted from
	 * @param imagePath	the internal package path of the image.
	 */
	public static void deleteImageByPath(OdfSchemaDocument doc, String imagePath) {
		List<OdfDrawImage> imageList = getImageByPath(doc, imagePath);
		if (imageList != null) {
			Iterator<OdfDrawImage> it = imageList.iterator();
			while (it.hasNext()) {
				OdfDrawImage image = it.next();
				//remove the inserted picture
				String ref = image.getXlinkHrefAttribute().toString();
				doc.getPackage().remove(ref);

				//remove the draw:frame element in main document
				if (image.getParentNode() instanceof OdfDrawFrame) {
					OdfDrawFrame frame = (OdfDrawFrame) image.getParentNode();
					if (frame.getChildNodes().getLength() == 1) {
						frame.getParentNode().removeChild(frame);
					} else {
						image.getParentNode().removeChild(image);
					}
				} else {
					image.getParentNode().removeChild(image);
				}

				//remove Pictures/ directory if container does not have images
				if (getImageCount(doc) == 0) {
					doc.getPackage().remove(OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH);
				}
			}
		}

	}

	/**
	 * The method deletes the specified image from image container.
	 * @param doc the document the image should be deleted from
	 * @param image  the image which need to be deleted
	 */
	public static void deleteImage(OdfSchemaDocument doc, OdfDrawImage image) {


		// remove the inserted picture if it does not have reference any more
		String ref = image.getXlinkHrefAttribute().toString();
		List<OdfDrawImage> imageList = getImageByPath(doc, ref);
		if (imageList.size() == 1) {
			doc.getPackage().remove(ref);
		}

		// remove the draw:frame element in main document
		if (image.getParentNode() instanceof OdfDrawFrame) {
			OdfDrawFrame frame = (OdfDrawFrame) image.getParentNode();
			if (frame.getChildNodes().getLength() == 1) {
				frame.getParentNode().removeChild(frame);
			} else {
				image.getParentNode().removeChild(image);
			}
		} else {
			image.getParentNode().removeChild(image);
		}

		// remove Pictures/ directory if container does not have images
		if (getImageCount(doc) == 0) {
			doc.getPackage().remove(
					OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH);
		}

	}

	/**
	 * Get the count of image objects in the image container.
	 * @param doc the document the image should be counted from
	 * @return			the number of image in this document
	 *                  if no image is found, return zero
	 */
	public static int getImageCount(OdfSchemaDocument doc) {
		try {
			NodeList imageNodes = doc.getContentDom().getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "image");
			return imageNodes.getLength();
		} catch (Exception ex) {
			Logger.getLogger(OdfDrawImage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return 0;
	}

	/**
	 * The method return the image list in the image container.
	 * @param doc  the document the list of images should be returned from
	 * @return			an image list in this document
	 *                  if no images is found, return an empty list.
	 */
	public static List<OdfDrawImage> getImages(OdfSchemaDocument doc) {
		ArrayList<OdfDrawImage> imageList = new ArrayList<OdfDrawImage>();
		try {
			NodeList imageNodes = doc.getContentDom().getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "image");
			for (int i = 0; i < imageNodes.getLength(); i++) {
				OdfDrawImage image = (OdfDrawImage) imageNodes.item(i);
				imageList.add(image);
			}
		} catch (Exception ex) {
			Logger.getLogger(OdfDrawImage.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return imageList;
	}

	/**
	 * The method return the set of all the image paths.
	 * @param doc the document the image path set should be obtained from
	 * @return			an image path set in this document
	 */
	public static Set<String> getImagePathSet(OdfSchemaDocument doc) {
		Set<String> paths = new HashSet<String>();
		List<OdfDrawImage> imageList = getImages(doc);
		Iterator<OdfDrawImage> it = imageList.iterator();
		while (it.hasNext()) {
			OdfDrawImage image = it.next();
			paths.add(image.getXlinkHrefAttribute().toString());
		}
		return paths;
	}
}
