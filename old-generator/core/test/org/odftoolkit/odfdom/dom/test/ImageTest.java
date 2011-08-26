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
package org.odftoolkit.odfdom.dom.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.element.draw.OdfImage;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.util.NodeAction;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.w3c.dom.Node;

public class ImageTest {

    private URI mImageUri_ODFDOM = null;
    private static final String mImagePath = "src/resources/";
    private static final String mImageName_ODFDOM = "ODFDOM-Layered-Model.png";
    private static final String mPackageGraphicsPath = "Pictures/";

    public ImageTest() {
        try {
            mImageUri_ODFDOM = new URI(mImagePath + mImageName_ODFDOM);

        } catch (URISyntaxException ex) {
            Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddImageByUri() {
        try {
            OdfDocument doc = OdfDocument.loadDocument("test/resources/image.odt");
            final OdfPackage pkg = doc.getPackage();
            NodeAction addImages = new NodeAction() {

                @Override
				protected void apply(Node node, Object arg, int depth) {
                    if (node instanceof OdfImage) {
                        OdfImage img = (OdfImage) node;
                        try {
                            String packagePath = img.insertImage(mImageUri_ODFDOM);
                            if (packagePath == null || !pkg.contains(packagePath)) {
                                Assert.fail("The folloing image could not be embedded:" + mImageUri_ODFDOM.toString());
                            } else if (!packagePath.equals(mPackageGraphicsPath + mImageName_ODFDOM)) {
                                Assert.fail("Instead of '" + mPackageGraphicsPath + mImageName_ODFDOM +
                                        "' the folloing image path was returned: '" + packagePath + "'");
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null, ex);
                            Assert.fail(ex.getMessage());
                        }
                    }
                }
            };
            addImages.performAction(doc.getContentDom().getDocumentElement(), null);
            doc.save("build/test/add-images-by-uri.odt");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRemoveImage() {
        try {
            OdfDocument doc = OdfDocument.loadDocument("test/resources/image.odt");
            final OdfPackage pkg = doc.getPackage();
            NodeAction<?> removeImages = new NodeAction<Object>() {

                @Override
				protected void apply(Node node, Object arg, int depth) {
                    if (node instanceof OdfImage) {
                        OdfImage img = (OdfImage) node;
                        String ref = img.getAttributeNS(OdfNamespace.XLINK.getUri(), "href");
                        pkg.remove(ref);
                        img.getParentNode().removeChild(img);
                    }
                }
            };
            removeImages.performAction(doc.getContentDom().getDocumentElement(), null);
            pkg.save("build/test/remove-images.odt");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
