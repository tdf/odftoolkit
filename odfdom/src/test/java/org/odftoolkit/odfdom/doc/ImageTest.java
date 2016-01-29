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
package org.odftoolkit.odfdom.doc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import static org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage.deleteImage;
import static org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage.deleteImageByPath;
import static org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage.getImageByPath;
import static org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage.getImageCount;
import static org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage.getImagePathSet;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.utils.NodeAction;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class ImageTest {

	private URI mImageUri_ODFDOM = null;
	private static final String mImagePath = "src/main/javadoc/doc-files/";
	private static final String mImageName_ODFDOM = "ODFDOM-Layered-Model.png";
	private static final String mPackageGraphicsPath = "Pictures/";

	public ImageTest() {
		try {
			mImageUri_ODFDOM = new URI(mImagePath + mImageName_ODFDOM);

		} catch (URISyntaxException ex) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, null,
					ex);
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testAddImageByUri() {
		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("image.odt"));
			final OdfPackage pkg = doc.getPackage();
			NodeAction addImages = new NodeAction() {

				@Override
				protected void apply(Node node, Object arg, int depth) {
					if (node instanceof OdfDrawImage) {
						OdfDrawImage img = (OdfDrawImage) node;
						try {
							String packagePath = img.newImage(mImageUri_ODFDOM);
							if (packagePath == null || !pkg.contains(packagePath)) {
								Assert.fail("The following image could not be embedded:" + mImageUri_ODFDOM.toString());
							} else if (!packagePath.equals(mPackageGraphicsPath + mImageName_ODFDOM)) {
								Assert.fail("Instead of '" + mPackageGraphicsPath + mImageName_ODFDOM + "' the following image path was returned: '" + packagePath + "'");
							}
						} catch (Exception ex) {
							Logger.getLogger(ImageTest.class.getName()).log(
									Level.SEVERE, null, ex);
							Assert.fail(ex.getMessage());
						}
					}
				}
			};
			addImages.performAction(doc.getContentDom().getDocumentElement(),
					null);
			doc.save(ResourceUtilities.getTestOutput("add-images-by-uri.odt"));

		} catch (Exception e) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRemoveImage() throws Exception {
		OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("image.odt"));
		final OdfPackage pkg = doc.getPackage();
		NodeAction<?> removeImages = new NodeAction<Object>() {

			@Override
			protected void apply(Node node, Object arg, int depth) {
				if (node instanceof OdfDrawImage) {
					OdfDrawImage img = (OdfDrawImage) node;
					String ref = img.getAttributeNS(
							OdfDocumentNamespace.XLINK.getUri(), "href");
					pkg.remove(ref);
					img.getParentNode().removeChild(img);
				}
			}
		};
		removeImages.performAction(doc.getContentDom().getDocumentElement(),
				null);
		pkg.save(ResourceUtilities.getTestOutput("remove-images.odt"));

	}

	@Test
	public void testImageInTextDocument() {
		try {
			OdfTextDocument doc = OdfTextDocument.newTextDocument();
			String imagePath1 = doc.newImage(ResourceUtilities.getURI("testA.jpg"));
			Assert.assertTrue(getImageCount(doc) == 1);
			OdfDrawImage image = getImageByPath(doc, imagePath1).get(0);
			Assert.assertTrue(image.getImageUri().toString().equals(imagePath1));
			OdfDrawFrame frame1 = (OdfDrawFrame) image.getParentNode();
			frame1.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
			frame1.setTextAnchorPageNumberAttribute(1);

			//add paragraph
			OfficeTextElement office = doc.getContentRoot();
			OdfTextParagraph para1 = (OdfTextParagraph) office.newTextPElement();
			para1.setTextContent("insert an image here");
			String imagePath2 = doc.newImage(mImageUri_ODFDOM);

			OdfTextParagraph para2 = (OdfTextParagraph) office.newTextPElement();
			para2.setTextContent("another");
			String imagePath3 = doc.newImage(mImageUri_ODFDOM);
			OdfDrawImage image3 = getImageByPath(doc, imagePath3).get(1);
			OdfDrawFrame frame3 = (OdfDrawFrame) image3.getParentNode();
			frame3.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.CHAR.toString());

			doc.save(ResourceUtilities.newTestOutputFile("addimages.odt"));

			//load the file again
			OdfTextDocument doc1 = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("addimages.odt"));

			Assert.assertTrue(getImageCount(doc1) == 3);
			Assert.assertTrue(getImageByPath(doc1, imagePath2).size() == 2);
			deleteImageByPath(doc1, imagePath2);
			Assert.assertTrue(getImageCount(doc1) == 1);
			Assert.assertTrue(getImageByPath(doc1, imagePath3).size() == 0);
			Assert.assertNull(doc1.getPackage().getBytes(imagePath3));
			doc1.save(ResourceUtilities.newTestOutputFile("removeimages.odt"));

		} catch (Exception ex) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}

	}

        @Test
	public void testSVGInTextDocument() {
		try {
			OdfTextDocument doc = OdfTextDocument.newTextDocument();
			String imagePath1 = doc.newImage(ResourceUtilities.getURI("simple.svg"));
			Assert.assertTrue(getImageCount(doc) == 1);
			OdfDrawImage image = getImageByPath(doc, imagePath1).get(0);
			Assert.assertTrue(image.getImageUri().toString().equals(imagePath1));
			OdfDrawFrame frame1 = (OdfDrawFrame) image.getParentNode();
			frame1.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
			frame1.setTextAnchorPageNumberAttribute(1);

			doc.save(ResourceUtilities.newTestOutputFile("svg-image.odt"));

			//load the file again
			OdfTextDocument doc1 = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("svg-image.odt"));

			Assert.assertTrue(getImageCount(doc1) == 1);

		} catch (Exception ex) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}

	}

	@Test
	public void testRemoveAllImage() {
		try {
			// this test is dependent of the output of the following test, which is therefore being called explicitly
            testImageInTextDocument();
			OdfTextDocument doc = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("addimages.odt"));

			Set<String> pathSet = getImagePathSet(doc);
			for (String pathIter : pathSet) {
				List<OdfDrawImage> imageList = getImageByPath(doc, pathIter);
				for (int i = 0; i < 1; i++) {
					deleteImage(doc, imageList.get(i));
				}
			}
			Assert.assertTrue(getImageCount(doc) == 1);
			pathSet = getImagePathSet(doc);
			for (String pathIter : pathSet) {
				deleteImageByPath(doc, pathIter);
			}
			Assert.assertTrue(getImageCount(doc) == 0);
			doc.save(ResourceUtilities.newTestOutputFile("removeAllImages.odt"));

		} catch (Exception ex) {
			Logger.getLogger(ImageTest.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}
}
