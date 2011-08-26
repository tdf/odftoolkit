/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.odftoolkit.odfdom.doc.draw;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.AnyURI;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;



/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 */
public class OdfDrawImage extends DrawImageElement
{

    private URI mImageURI;
    // OdfPackage necessary to adapt the manifest referencing the image
    private OdfPackage mOdfPackage;
    private static final String SLASH = "/";

    /** Creates a new instance of this class
     * @param ownerDoc The XML DOM containing the draw:image element
     */
    public OdfDrawImage( OdfFileDom ownerDoc) {
        super(ownerDoc);
        mOdfPackage = ownerDoc.getOdfDocument().getPackage();
    }

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
            packagePath = packagePath.replaceFirst(mOdfDocument.getDocumentPackagePath(), "");
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
        return packagePath = mOdfDocument.getDocumentPackagePath() + packagePath;
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
            int height = image.getHeight(null);
            int width = image.getWidth(null);
            odfFrame.setSvgHeightAttribute(Length.mapToUnit(String.valueOf(height) + "px", Unit.CENTIMETER));
            odfFrame.setSvgWidthAttribute(Length.mapToUnit(String.valueOf(width) + "px", Unit.CENTIMETER));
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
    public String insertImage(URI imageUri) throws Exception {
      URL url = null;
        String imageRef = null;
        if (!imageUri.isAbsolute()) {
            imageRef = System.getProperty("user.dir") + '/' + imageUri.toString();
            url = new URL(new File(imageRef).getCanonicalFile().toURI().toString());
        } else {
            url = imageUri.toURL();
            imageRef = imageUri.toString();
        }
        String mediaType = OdfFileEntry.getMediaType(imageRef);
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
    public void insertImage(InputStream is, String packagePath, String mediaType) throws Exception {
        mOdfPackage.insert(is, packagePath, mediaType);
        configureInsertedImage(packagePath);
    }

}
