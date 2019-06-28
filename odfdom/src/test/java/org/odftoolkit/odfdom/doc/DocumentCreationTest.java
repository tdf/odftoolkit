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

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odftoolkit.junit.AlphabeticalOrderedRunner;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageDocument;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

@RunWith(AlphabeticalOrderedRunner.class)
public class DocumentCreationTest {

	private static final Logger LOG = Logger.getLogger(DocumentCreationTest.class.getName());
	private static final String TEST_FILE_FOLDER = ResourceUtilities.getTestOutputFolder();
	private static final String TEST_FILE_EMBEDDED = TEST_FILE_FOLDER + "testEmbeddedDoc.odt";
	private static final String TEST_PIC = "testA.jpg";
	// Changed leading space against character as leading space have to be <text:s/> element in ODF, see http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#White-space_Characters
	private static final String TEST_SPAN_TEXT = "*Find Truth!!!";
	private static final String TEST_FILE_ACCESS_EMBEDDED = TEST_FILE_FOLDER + "TestAccess_EmbeddedDocument.odt";
	private static final String TEST_FILE_EMBEDDED_EMBEDDED = TEST_FILE_FOLDER + "TestAccess_EmbeddedinEmbedded.odt";
	private static final String TEST_FILE_SAVE_EMBEDDED = TEST_FILE_FOLDER + "testSaveEmbeddedDoc.odt";
	private static final String TEST_FILE_SAVE_EMBEDDED_OUT = TEST_FILE_FOLDER + "TestSaveEmbeddedDoc_newName.odt";
	private static final String TEST_FILE_SAVE_EMBEDDED_OUT2 = TEST_FILE_FOLDER + "TestSaveEmbeddedDoc2.odt";
	private static final String TEST_FILE_SAVE_QUEER_PATH = TEST_FILE_FOLDER + "TestSaveQueerEmbeddedPathDoc1.odt";
	private static final String CORRUPTED_MIMETYPE_DOC = TEST_FILE_FOLDER + "CorruptedMimetypeDoc.odt";
	private static final String CORRUPTED_MIMETYPE_DOC_OUT = TEST_FILE_FOLDER + "TestSaveCorruptedMimetypeDoc.odt";
	private static final String CORRUPTED_MIMETYPE_CHART = TEST_FILE_FOLDER + "CorruptedMimetypeChart.odc";
	private static final String CORRUPTED_MIMETYPE_CHART_OUT = TEST_FILE_FOLDER + "TestSaveCorruptedMimetypeChart.odc";

	@Test
	public void _1_createEmptyDocs() {
		try {
			OdfTextDocument odtDoc1 = OdfTextDocument.newTextDocument();
			OdfTextDocument odtDoc2 = OdfTextDocument.newTextDocument();
			odtDoc2.getContentDom();
			odtDoc1.save(ResourceUtilities.newTestOutputFile("TestEmpty_OdfTextDocument.odt"));

			OdfGraphicsDocument odgDoc1 = OdfGraphicsDocument.newGraphicsDocument();
			OdfGraphicsDocument odgDoc2 = OdfGraphicsDocument.newGraphicsDocument();
			odgDoc2.getContentDom();
			odgDoc1.save(ResourceUtilities.newTestOutputFile("TestEmpty_OdfGraphicsDocument.odg"));

			OdfSpreadsheetDocument odsDoc1 = OdfSpreadsheetDocument.newSpreadsheetDocument();
			OdfSpreadsheetDocument odsDoc2 = OdfSpreadsheetDocument.newSpreadsheetDocument();
			odsDoc2.getContentDom();
			odsDoc1.save(ResourceUtilities.newTestOutputFile("TestEmpty_OdfSpreadsheetDocument.ods"));

			OdfPresentationDocument odpDoc1 = OdfPresentationDocument.newPresentationDocument();
			OdfPresentationDocument odpDoc2 = OdfPresentationDocument.newPresentationDocument();
			odpDoc2.getContentDom();
			odpDoc1.save(ResourceUtilities.newTestOutputFile("TestEmpty_OdfPresentationDocument.odp"));

			OdfChartDocument odcDoc1 = OdfChartDocument.newChartDocument();
			OdfChartDocument odcDoc2 = OdfChartDocument.newChartDocument();
			odcDoc2.getContentDom();
			odcDoc1.save(ResourceUtilities.newTestOutputFile("TestEmpty_OdfChartDocument.odc"));

			/////////////////////////////////////////
			// ODFDOM PACKAGE LAYER - WIKI EXAMPLE //
			/////////////////////////////////////////

			// loads the ODF document package from the path
			OdfPackage pkg = OdfPackage.loadPackage(ResourceUtilities.getTestResourceAsStream("TestEmpty_OdfTextDocument.odt"));

			// loads the images from the URLs and inserts the image in the package, adapting the manifest
			pkg.insert(ResourceUtilities.getURI(TEST_PIC), "Pictures/" + TEST_PIC, null);
			//Deactivated as test fail, when test machine is not online (painful for offline work)
			//pkg.insert(new URI("http://odftoolkit.org/attachments/wiki_images/odftoolkit/Table_fruits_diagramm.jpg"), "someweiredname/tableandfruits.jpg", null);
			pkg.save(ResourceUtilities.newTestOutputFile("odfdom-wiki-package.odt"));


			/////////////////////////////////////
			// ODFDOM XML LAYER - WIKI EXAMPLE //
			/////////////////////////////////////

			// loads the ODF document from the path
			OdfDocument odfDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestEmpty_OdfTextDocument.odt"));

			// get the ODF content as DOM tree representation
			OdfFileDom odfContent = odfDoc.getContentDom();

			//// W3C XPath initialization ''(JDK5 functionality)''  - XPath is the path within the XML file
			//// (Find XPath examples here: http://www.w3.org/TR/xpath#path-abbrev)
			XPath xpath2 = odfContent.getXPath();

			// receiving the first paragraph "//text:p[1]" ''(JDK5 functionality)''
			TextPElement para = (TextPElement) xpath2.evaluate("//text:p[1]", odfContent, XPathConstants.NODE);

			// adding an image - expecting the user to know that
			// an image consists always of a 'draw:image' and a 'draw:frame' parent

			// FUTURE USAGE: para.createDrawFrame().createDrawImage("/myweb.org/images/myHoliday.png", "/Pictures/myHoliday.png");
			//             Child access methods are still not part of the v0.6.x releases
			// CURRENT USAGE:
			OdfDrawFrame odfFrame = (OdfDrawFrame) OdfXMLFactory.newOdfElement(odfContent, DrawFrameElement.ELEMENT_NAME);
			para.appendChild(odfFrame);
			OdfDrawImage odfImage = (OdfDrawImage) OdfXMLFactory.newOdfElement(odfContent, OdfDrawImage.ELEMENT_NAME);
			odfFrame.appendChild(odfImage);
			odfImage.newImage(ResourceUtilities.getURI(TEST_PIC));

			OdfDrawImage odfImage2 = (OdfDrawImage) OdfXMLFactory.newOdfElement(odfContent, OdfDrawImage.ELEMENT_NAME);
			odfFrame.appendChild(odfImage2);
			//Deactivated as test fail, when test machine is not online (painful for offline work)
			//odfImage2.newImage(new URI("http://odftoolkit.org/attachments/wiki_images/odftoolkit/Table_fruits_diagramm.jpg"));
			odfDoc.save(ResourceUtilities.newTestOutputFile("odfdom-wiki-dom.odt"));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void _2_createEmbeddedDocs() {
		try {
			OdfTextDocument odtDoc1 = OdfTextDocument.newTextDocument();
			odtDoc1.insertDocument(OdfTextDocument.newTextDocument(), "Object1/");
			odtDoc1.insertDocument(OdfTextDocument.newTextDocument(), "Object2/");
			odtDoc1.insertDocument(OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("TestEmpty_OdfGraphicsDocument.odg")), "Object3");
			odtDoc1.insertDocument(OdfChartDocument.newChartDocument(), "Object4");
			odtDoc1.insertDocument(OdfGraphicsDocument.newGraphicsDocument(), "Object5");
			odtDoc1.insertDocument(OdfPresentationDocument.newPresentationDocument(), "Object6");

			Map<String, OdfDocument> embeddedDocs = odtDoc1.loadSubDocuments();
			LOG.log(Level.INFO, "Embedded Document count: {0}", embeddedDocs.size());
			odtDoc1.save(ResourceUtilities.newTestOutputFile("TestCreate_EmbeddedDocuments.odt"));

			Assert.assertTrue(embeddedDocs.size() == 6);

			Map<String, OdfDocument> embeddedTextDocs = odtDoc1.loadSubDocuments(OdfDocument.OdfMediaType.TEXT);
			LOG.log(Level.INFO, "Only Embedded Text Docs Size: {0}", embeddedTextDocs.size());
			Assert.assertTrue(embeddedTextDocs.size() == 2);

			Map<String, OdfDocument> embeddedChartDocs = odtDoc1.loadSubDocuments(OdfDocument.OdfMediaType.CHART);
			LOG.log(Level.INFO, "Only Embedded Chart Docs Size: {0}", embeddedChartDocs.size());
			Assert.assertTrue(embeddedChartDocs.size() == 1);

			OdfDocument embeddedObject1 = odtDoc1.loadSubDocument("Object1/");
			LOG.log(Level.INFO, "Embedded Object1 path: {0}", embeddedObject1.getDocumentPath());
			LOG.log(Level.INFO, "Embedded Object1 media-type: {0}", embeddedObject1.getMediaTypeString());
			Assert.assertEquals(embeddedObject1.getMediaTypeString(), OdfDocument.OdfMediaType.TEXT.getMediaTypeString());

			OdfDocument embeddedObject3 = odtDoc1.loadSubDocument("Object3");
			LOG.log(Level.INFO, "Embedded Object3 path: {0}", embeddedObject3.getDocumentPath());
			LOG.log(Level.INFO, "Embedded Object3 media-type: {0}", embeddedObject3.getMediaTypeString());
			Assert.assertEquals(embeddedObject3.getMediaTypeString(), OdfDocument.OdfMediaType.GRAPHICS.getMediaTypeString());

			OdfDocument embeddedObject6 = odtDoc1.loadSubDocument("Object6/");
			LOG.log(Level.INFO, "Embedded Object6 path: {0}", embeddedObject6.getDocumentPath());
			LOG.log(Level.INFO, "Embedded Object6 media-type: {0}", embeddedObject6.getMediaTypeString());
			Assert.assertEquals(embeddedObject6.getMediaTypeString(), OdfDocument.OdfMediaType.PRESENTATION.getMediaTypeString());

		} catch (Exception e) {
			LOG.log(Level.SEVERE, null, e);
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void _3_accessEmbeddedDocs() {
		try {
			OdfDocument docWithEmbeddedObjects = OdfDocument.loadDocument(TEST_FILE_EMBEDDED);
			Map<String, OdfDocument> embDocs = docWithEmbeddedObjects.loadSubDocuments();
			String pathToEmbeddedObject = "";
			for (String embDocPath : embDocs.keySet()) {
				OdfPackageDocument embDoc = embDocs.get(embDocPath);
				LOG.log(Level.INFO, "Embedded file of {0} internal package path: {1} mediaType: {2}", new Object[]{TEST_FILE_EMBEDDED, embDoc.getDocumentPath(), embDoc.getMediaTypeString()});
				pathToEmbeddedObject = embDoc.getDocumentPath();
			}

			OdfDocument embDoc = docWithEmbeddedObjects.loadSubDocument(pathToEmbeddedObject);
			OdfContentDom contentDom = embDoc.getContentDom();
			XPath xpath = contentDom.getXPath();
			// Make sure the embedded document is being loaded


			// Add text element
			TextPElement para = (TextPElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
			LOG.log(Level.INFO, "First para: {0}", para.getTextContent());
			OdfTextSpan spanElem = new OdfTextSpan(contentDom);
			spanElem.setTextContent(TEST_SPAN_TEXT);
			para.appendChild(spanElem);

			// Add frame and image element
			TextPElement paraLast = (TextPElement) xpath.evaluate("//text:p[last()]", contentDom, XPathConstants.NODE);
			addImageToDocument(contentDom, paraLast);

			// Access/Update automatic styles
			OdfOfficeAutomaticStyles autoStyles = embDoc.getContentDom().getAutomaticStyles();
			OdfStyle autoStyle = autoStyles.getStyle("P1", OdfStyleFamily.Paragraph);
			Assert.assertEquals(autoStyle.getStyleNameAttribute(), "P1");
			Assert.assertEquals(autoStyle.getFamilyName(), "paragraph");

			// Access/Update styles.xml
			OdfStyle documentStyle = embDoc.getDocumentStyles().getStyle("myStyle", OdfStyleFamily.Paragraph);
			Assert.assertEquals(documentStyle.getStyleNameAttribute(), "myStyle");
			Assert.assertEquals(documentStyle.getFamilyName(), "paragraph");

			documentStyle.setProperty(StyleTextPropertiesElement.FontWeight, "bold");
			documentStyle.setProperty(StyleParagraphPropertiesElement.BackgroundColor, "#14EA5D");

			// SAVE / LOAD
			docWithEmbeddedObjects.save(TEST_FILE_ACCESS_EMBEDDED);

			OdfDocument doc2 = OdfDocument.loadDocument(TEST_FILE_ACCESS_EMBEDDED);
			OdfDocument embDoc2 = doc2.loadSubDocument("Object 1/");
			embDoc2.getStylesDom();
			OdfStyle documentStyle2 = embDoc2.getDocumentStyles().getStyle("myStyle", OdfStyleFamily.Paragraph);
			String prop2 = documentStyle2.getProperty(StyleTextPropertiesElement.FontWeight);
			Assert.assertEquals(prop2, "bold");

			TextSpanElement spanTest = (TextSpanElement) xpath.evaluate("//text:p[1]/text:span[1]", contentDom, XPathConstants.NODE);
			Assert.assertEquals(spanTest.getTextContent(), TEST_SPAN_TEXT);

		} catch (Exception ex) {
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	@Test
	public void _4_accessEmbeddedWithinEmbeddedDocs() {

		try {
			OdfDocument rootDocument = OdfDocument.loadDocument(TEST_FILE_EMBEDDED);
			// Test DOM Access
			Assert.assertNotNull(rootDocument.getDocumentStyles());
			Assert.assertNotNull(rootDocument.getContentDom().getAutomaticStyles());
			Assert.assertNotNull(rootDocument.getStylesDom());
			Assert.assertNotNull(rootDocument.getContentDom());

			Map<String, OdfDocument> embDocs = rootDocument.loadSubDocuments();
			int embDocsNumber = embDocs.size();
			// the document "Object 1/
			OdfDocument embDoc = embDocs.get("Object 1/");
			String pathOfSecondInnerDoc = "Object in Object1/";
			embDoc.insertDocument(OdfTextDocument.newTextDocument(), pathOfSecondInnerDoc);
			OdfFileEntry fileEntry = embDoc.getPackage().getFileEntry(embDoc.getDocumentPath() + pathOfSecondInnerDoc);
			Assert.assertNotNull(fileEntry);

			// get "Object 1/content.xml"
			OdfContentDom contentDom = embDoc.getContentDom();
			XPath xpath = contentDom.getXPath();
			TextPElement lastPara = (TextPElement) xpath.evaluate("//text:p[last()]", contentDom, XPathConstants.NODE);
			addFrameForEmbeddedDoc(contentDom, lastPara, "Object in Object1");
			Map<String, OdfDocument> emb_embDocs = embDoc.loadSubDocuments();
			Assert.assertEquals(embDocsNumber + 1, emb_embDocs.size());

			OdfDocument emb_embDoc = rootDocument.loadSubDocument(embDoc.getDocumentPath()+ pathOfSecondInnerDoc);
			contentDom = emb_embDoc.getContentDom();
			TextPElement para = (TextPElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
			OdfTextSpan spanElem = new OdfTextSpan(contentDom);
			spanElem.setTextContent(TEST_SPAN_TEXT);
			para.appendChild(spanElem);

			// embDoc.save(ResourceUtilities.newTestOutputFile("222debug.odt"));
			rootDocument.save(TEST_FILE_EMBEDDED_EMBEDDED);

			OdfDocument docWithdoubleEmbeddedDoc = OdfDocument.loadDocument(TEST_FILE_EMBEDDED_EMBEDDED);
			OdfDocument doubleEmbeddedDoc =docWithdoubleEmbeddedDoc.loadSubDocument("Object 1/Object in Object1");

			OdfContentDom dEDcontentDom = doubleEmbeddedDoc.getContentDom();
			TextSpanElement spanTest = (TextSpanElement) xpath.evaluate("//text:span[last()]", dEDcontentDom, XPathConstants.NODE);
			Assert.assertEquals(spanTest.getTextContent(), TEST_SPAN_TEXT);

		} catch (Exception ex) {
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	@Test
	public void _5_testCacheDocuments() {
		OdfDocument docWithEmbeddedObjects;
		try {
			docWithEmbeddedObjects = OdfDocument.loadDocument(TEST_FILE_EMBEDDED);
			Map<String, OdfDocument> embDocs = docWithEmbeddedObjects.loadSubDocuments();
			for(String embDocPath : embDocs.keySet()){
				OdfDocument doc1 = embDocs.get(embDocPath);
				doc1.getDocumentPath();
				OdfContentDom contentDom1 = doc1.getContentDom();
				OdfDocument doc2 = doc1.loadSubDocument(".");
				OdfContentDom contentDom2 = doc2.getContentDom();
				Assert.assertEquals(doc2, doc1);
				Assert.assertEquals(contentDom1, contentDom2);
			}
		} catch (Exception ex) {
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	@Test
	public void _6_testSaveEmbeddedDocuments() {
		OdfDocument docWithEmbeddedObjects;
		try {
			docWithEmbeddedObjects = OdfDocument.loadDocument(TEST_FILE_SAVE_EMBEDDED);
			Map<String, OdfDocument> embDocs = docWithEmbeddedObjects.loadSubDocuments(OdfDocument.OdfMediaType.GRAPHICS);
			// Graphics Doc
			for(String eDocPath : embDocs.keySet()){
				OdfDocument doc1 = embDocs.get(eDocPath);
				Assert.assertNotNull(doc1);
				OdfContentDom contentDom = doc1.getContentDom();
				XPath xpath = contentDom.getXPath();
				TextPElement para = (TextPElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
				OdfTextSpan spanElem = new OdfTextSpan(contentDom);
				spanElem.setTextContent(TEST_SPAN_TEXT);
				para.appendChild(spanElem);
				//save the embed document to a stand alone document
				doc1.save(TEST_FILE_SAVE_EMBEDDED_OUT);
				// Load test
				OdfDocument loadedDoc = OdfDocument.loadDocument(TEST_FILE_SAVE_EMBEDDED_OUT);
				OdfContentDom contentDom2 = loadedDoc.getContentDom();
				OdfTextSpan span = (OdfTextSpan) xpath.evaluate("//text:span[last()]", contentDom2, XPathConstants.NODE);
				Assert.assertEquals(span.getTextContent(), TEST_SPAN_TEXT);
				Map<String, OdfDocument> embDocs3 = docWithEmbeddedObjects.loadSubDocuments(OdfDocument.OdfMediaType.TEXT);
				for(String eDocPath3 : embDocs3.keySet()){
					// Writer Doc
					OdfDocument doc3 = embDocs3.get(eDocPath3);
					Assert.assertNotNull(doc3);
					OdfContentDom contentDom3 = doc3.getContentDom();
					TextPElement para2 = (TextPElement) xpath.evaluate("//text:p[1]", contentDom3, XPathConstants.NODE);
					addImageToDocument(contentDom3, para2);
					TextPElement para3 = (TextPElement) xpath.evaluate("//text:p[last()]", contentDom3, XPathConstants.NODE);
					addFrameForEmbeddedDoc(contentDom3, para3, "NewEmbedded");
					doc3.insertDocument(OdfTextDocument.newTextDocument(), "/NewEmbedded/");
					OdfDocument doc4 = doc3.loadSubDocument("NewEmbedded");
					Assert.assertNotNull(doc4);
					OdfContentDom contentDom4 = doc4.getContentDom();
					para = (TextPElement) xpath.evaluate("//text:p[1]",	contentDom4, XPathConstants.NODE);
					spanElem = new OdfTextSpan(contentDom4);
					spanElem.setTextContent(TEST_SPAN_TEXT);
					para.appendChild(spanElem);
					doc3.save(TEST_FILE_SAVE_EMBEDDED_OUT2);

					OdfDocument testLoad = OdfDocument.loadDocument(TEST_FILE_SAVE_EMBEDDED_OUT2);
					NodeList linkNodes = (NodeList) xpath.evaluate("//*[@xlink:href]", testLoad.getContentDom(), XPathConstants.NODE);
					for (int i = 0; i < linkNodes.getLength(); i++) {
						OdfElement object = (OdfElement) linkNodes.item(i);
						String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
						Assert.assertTrue(refObjPath.equals("Pictures/"	+ TEST_PIC)	|| refObjPath.equals("./NewEmbedded"));
					}
					Assert.assertNotNull(testLoad.getPackage().getFileEntry("Pictures/" + TEST_PIC));
					OdfDocument embedDocOftestLoad = testLoad.loadSubDocument("NewEmbedded/");
					contentDom4 = embedDocOftestLoad.getContentDom();
					OdfTextSpan span4 = (OdfTextSpan) xpath.evaluate("//text:span[last()]", contentDom4, XPathConstants.NODE);
					Assert.assertNotNull(span4);
					Assert.assertEquals(span4.getTextContent(), TEST_SPAN_TEXT);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	@Test
	/**
	 * OdfDocument docA and docB are ODF subdocuments.
	 * docA containing docB, like
	 * OdfPackage/dummy/docA/docB
	 */
	public void _7_testDocumentWithQueerPath() {
		try {

			OdfDocument containerDoc = OdfTextDocument.newTextDocument();
			String pathToDocA = "dummy/docA/";
			String pathToDocB = "docB/";
			containerDoc.insertDocument(OdfTextDocument.newTextDocument(), pathToDocA);
			OdfDocument docA = containerDoc.loadSubDocument(pathToDocA);
			Assert.assertNotNull(docA);
			docA.insertDocument(OdfTextDocument.newTextDocument(), pathToDocB);
			OdfDocument docB = containerDoc.loadSubDocument(pathToDocA + pathToDocB);
			Assert.assertNotNull(docB);
			// only the document docB located at dummy/docA/docB will be saved
			docB.save(TEST_FILE_SAVE_QUEER_PATH);

		} catch (Exception ex) {
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	/**
	 * Test if saving OdfDocuments always results in a valid ODF file. This means
	 * that the mimetype is written clearly at the first position in the zipped
	 * ODF package.
	 */
	@Test
	public void _8_testWritingCorrectMimetype() {
		try {
			OdfDocument docWithCorruptedMimetype = OdfDocument.loadDocument(CORRUPTED_MIMETYPE_DOC);
			docWithCorruptedMimetype.save(CORRUPTED_MIMETYPE_DOC_OUT);
			OdfDocument chartWithCorruptedMimetype = OdfDocument.loadDocument(CORRUPTED_MIMETYPE_CHART);
			chartWithCorruptedMimetype.save(CORRUPTED_MIMETYPE_CHART_OUT);
		} catch (Exception ex) {
			Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	private void addImageToDocument(OdfContentDom dom, TextPElement para) throws Exception {
		OdfDrawFrame drawFrame = new OdfDrawFrame(dom);
		drawFrame.setDrawNameAttribute("graphics1");
		drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
		drawFrame.setSvgWidthAttribute("4.233cm");
		drawFrame.setSvgHeightAttribute("4.233cm");
		drawFrame.setDrawZIndexAttribute(0);
		para.appendChild(drawFrame);

		OdfDrawImage image = new OdfDrawImage(dom);
		drawFrame.appendChild(image);
		image.newImage(ResourceUtilities.getURI(TEST_PIC));
	}

	private void addFrameForEmbeddedDoc(OdfContentDom dom, TextPElement para, String path) throws Exception {
		OdfDrawFrame drawFrame = new OdfDrawFrame(dom);
		drawFrame.setDrawNameAttribute(path);
		drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
		drawFrame.setSvgXAttribute("0.834cm");
		drawFrame.setSvgYAttribute("2.919cm");
		drawFrame.setSvgWidthAttribute("13.257cm");
		drawFrame.setSvgHeightAttribute("11.375cm");
		drawFrame.setDrawZIndexAttribute(0);

		DrawObjectElement object = new DrawObjectElement(dom);
		object.setXlinkHrefAttribute("./" + path);
		object.setXlinkActuateAttribute("onLoad");
		object.setXlinkShowAttribute("embed");
		object.setXlinkTypeAttribute("simple");
		drawFrame.appendChild(object);
		para.appendChild(drawFrame);
	}
}
