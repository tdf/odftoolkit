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
package org.openoffice.odf.doc.element.draw;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.openoffice.odf.dom.element.draw.OdfImageElement;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openoffice.odf.doc.OdfFileDom;
import org.openoffice.odf.dom.util.URITransformer;
import org.openoffice.odf.pkg.OdfPackage;
import org.openoffice.odf.pkg.manifest.OdfFileEntry;

public class OdfImage extends OdfImageElement {

    private URI mImageURI;
    // OdfPackage necessary to adapt the manifest referencing the image
    private OdfPackage mOdfPackage;
    private static final String COLON = ":";
    private static final String SLASH = "/";

    /** Creates a new instance of this class
     * @param ownerDoc The XML DOM containing the draw:image element
     */
    public OdfImage(OdfFileDom ownerDoc) {
        super(ownerDoc);
        mOdfPackage = ownerDoc.getOdfDocument().getPackage();
    }

    public URI getImageUri() {
        try {
            if (mImageURI == null) {
                mImageURI = new URI(URITransformer.encodePath(this.getHref()));
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(OdfImage.class.getName()).log(Level.SEVERE, null, ex);
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
            URI uri = new URI(URITransformer.encodePath(packagePath).toString());
            this.setHref(URITransformer.decodePath(uri.toString()));
            mImageURI = uri;
        } catch (URISyntaxException ex) {
            Logger.getLogger(OdfImage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Inserts the image file from the URI to the ODF package named similar as in the URI.
     * The manifest is adapted using the media type according to the suffix. Existing images are replaced. 
     * 
     * @param imageUri The URI of the image that will be added as stream to the package 
     *                 in the 'Pictures/' graphic directory with the same image file name as in the URI.
     * @return              Returns the package path of the image, which was created based on the given URI.
     * @throws Exception    If the image provided by the URI, could not be added as stream to the ODF package.
     * 
     */
    public String insertImage(URI imageUri) throws Exception {
        String imageRef = imageUri.toString();

        InputStream is = null;
        String packagePath = null;
        try {
            if (imageUri.isAbsolute()) {
                // if the URI is absolute it can be converted to URL
                is = imageUri.toURL().openStream();
            } else if (imageRef.contains(COLON)) {
                // if the URI string representation has a protocol create URL
                is = new URL(imageUri.toString()).openStream();
            } else {
                // otherwise create a file class to open the stream
                is = new File(imageRef).toURL().openStream();
            }
            if (imageRef.contains(SLASH)) {
                imageRef = imageRef.substring(imageRef.lastIndexOf(SLASH) + 1, imageRef.length());
            }
            packagePath = OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + imageRef;
            packagePath = mOdfDocument.getDocumentPackagePath() + packagePath;
            // TODOD URI ENCODING
            //String packagePathDecoded = packagePath.replaceAll(" ", "%20");
            String mediaType = OdfFileEntry.getMediaType(imageRef);
            insertImage(is, packagePath, mediaType);
        } catch (IOException ex) {
            Logger.getLogger(OdfImage.class.getName()).log(Level.SEVERE, "Could not receive image from URL!", ex);
            packagePath = null;
        }
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
            //setImageUri(new URI(URITransformer.encodePath(packagePath).toString()));
            setImagePath(packagePath);
            mOdfPackage.insert(is, packagePath, mediaType);
    }
}
