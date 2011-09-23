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
package org.odftoolkit.odfdom.pkg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.OdfDocument.OdfMediaType;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawFrame;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

public class EmbeddedDocumentTest {

	private static final Logger LOG = Logger.getLogger(EmbeddedDocumentTest.class.getName());
	private static final String TEST_FILE_FOLDER = ResourceUtilities.getTestOutputFolder();
	private static final String TEST_FILE_EMBEDDED = "performance/Presentation1_INVALID.odp";
	private static final String TEST_FILE_EMBEDDED_SAVE_OUT = "SaveEmbeddedDoc.odt";
	private static final String TEST_FILE_EMBEDDED_SIDEBYSIDE_SAVE_OUT = "SaveEmbeddedDocSideBySide.odt";
	private static final String TEST_FILE_EMBEDDED_INCLUDED_SAVE_OUT = "SaveEmbeddedDocIncluded.odt";
	private static final String TEST_FILE_REMOVE_EMBEDDED_SAVE_OUT = "RemoveEmbeddedDoc.odt";
	private static final String TEST_FILE_MODIFIED_EMBEDDED = "TestModifiedEmbeddedDoc.odt";
	private static final String TEST_FILE_MODIFIED_EMBEDDED_SAVE_STANDALONE = "SaveModifiedEmbeddedDocAlone.odt";
	private static final String TEST_SPAN_TEXT = "Modify Header";
	private static final String TEST_PIC = "testA.jpg";
	private static final String TEST_PIC_ANOTHER = "testB.jpg";
	private static final String SLASH = "/";

	/**
	 * The document A contains the embedded document E1, 
	 * this test case is used to embed E1 to another document B
	 */
	@Test
	public void testEmbedEmbeddedDocument() {
		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_EMBEDDED));
			OdfDocument saveDoc = OdfTextDocument.newTextDocument();
			Map<String, OdfDocument> subDocs = doc.loadSubDocuments();
			List<String> subDocNames = new ArrayList<String>();
			for (String childDocPath : subDocs.keySet()) {
				OdfDocument childDoc = subDocs.get(childDocPath);
				String embeddedDocPath = childDoc.getDocumentPath();
				saveDoc.insertDocument(childDoc, embeddedDocPath);
				subDocNames.add(embeddedDocPath);
			}
			Set<String> paths = saveDoc.getPackage().getDocumentPaths();
			int docCount = paths.size();
			paths = saveDoc.getPackage().getDocumentPaths("application/vnd.oasis.opendocument.presentation");
			int presentationDocCount = paths.size();
			Assert.assertTrue(docCount > presentationDocCount);
			saveDoc.save(TEST_FILE_FOLDER + TEST_FILE_EMBEDDED_SAVE_OUT);
			saveDoc.close();
			saveDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_EMBEDDED_SAVE_OUT));
			Map<String, OdfDocument> reloadedSubDocs = saveDoc.loadSubDocuments();
			Assert.assertTrue(subDocs.size() == reloadedSubDocs.size());
			for (String childDocPath : subDocs.keySet()) {
				Assert.assertEquals(subDocs.get(childDocPath).getMediaTypeString(), reloadedSubDocs.get(childDocPath).getMediaTypeString());
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
			Assert.fail("Failed to embed an embedded Document: '" + ex.getMessage() + "'");
		}
	}

	/**
	 * The document B is embedded to document A 
	 * and the directory path of A and B are absolute from the package
	 * DOCA/ and DOCB/
	 */
	@Test
	public void testembeddedDocumentsLocatedSideBySide() {
		try {
			OdfTextDocument odtRootDoc = OdfTextDocument.newTextDocument();
			odtRootDoc.insertDocument(OdfTextDocument.newTextDocument(), "DOCA/");
			OdfDocument docA = odtRootDoc.loadSubDocument("DOCA");
			docA.newImage(ResourceUtilities.getURI(TEST_PIC));
			docA.insertDocument(OdfSpreadsheetDocument.newSpreadsheetDocument(), "../DOCB/");
			OdfFileDom contentA = docA.getContentDom();
			XPath xpath = contentA.getXPath();
			TextPElement lastPara = (TextPElement) xpath.evaluate("//text:p[last()]", contentA, XPathConstants.NODE);
			addFrameForEmbeddedDoc(contentA, lastPara, "DOCB");
			OdfDocument docB = odtRootDoc.loadSubDocument("DOCB/");
			Assert.assertNotNull(docB);
			Assert.assertNull(odtRootDoc.loadSubDocument("DOCA/DOCB/"));
			docB.newImage(ResourceUtilities.getURI(TEST_PIC_ANOTHER));
			OdfTable table1 = docB.getTableList().get(0);
			table1.setTableName("NewTable");
			updateFrameForEmbeddedDoc(contentA, "./DOCB", "DOCA/DOCB");
			//if user want to save the docA with the side by side embedded document
			//he has to insert it to the sub document of docA and update the xlink:href link
			docA.insertDocument(docB, "DOCB/");
			//save
			docA.save(TEST_FILE_FOLDER + TEST_FILE_EMBEDDED_SIDEBYSIDE_SAVE_OUT);
			OdfDocument testLoad = OdfDocument.loadDocument(TEST_FILE_FOLDER + TEST_FILE_EMBEDDED_SIDEBYSIDE_SAVE_OUT);
			OdfFileEntry imageEntry = testLoad.getPackage().getFileEntry(OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC);
			Assert.assertNotNull(imageEntry);
			Map<String, OdfDocument> embDocs = testLoad.loadSubDocuments(OdfDocument.OdfMediaType.SPREADSHEET);
			for(String embedDocPath : embDocs.keySet()){
				OdfDocument doc1 = embDocs.get(embedDocPath);
				imageEntry = doc1.getPackage().getFileEntry(doc1.getDocumentPath() + OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC_ANOTHER);
				Assert.assertNotNull(doc1.getTableByName("NewTable"));
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	/**
	 * 1) A new sub document text document DOCA/ is inserted into a new text document
	 * 2) A picture is being added to the subdocument (ie. /DOCA/Pictures/testA.jpg)
	 * 3) A new sub document spreadsheet document is inserted into the first (ie. /DOCA/DOCB)
	 * 4) In the last paragraph of /DOCA a frame with a reference to the subdocument DOCB is added
	 * 5) A picture is being added to the second subdocument (ie. /DOCA/DOCB/Pictures/testB.jpg)
	 * 6) The spreadsheetname of DOCB is set to "NewTable"
	 * 7) DOCA/ is saved in a document for its own	 
	 */
	@Test
	public void testembeddedDocumentWithSubPath() {
		try {
			OdfTextDocument odtDoc1 = OdfTextDocument.newTextDocument();
			odtDoc1.insertDocument(OdfTextDocument.newTextDocument(), "DOCA/");
			OdfDocument docA = odtDoc1.loadSubDocument("DOCA");
			docA.newImage(ResourceUtilities.getURI(TEST_PIC));
			docA.insertDocument(OdfSpreadsheetDocument.newSpreadsheetDocument(), "DOCB/");
			OdfFileDom contentA = docA.getContentDom();
			XPath xpath = contentA.getXPath();
			TextPElement lastPara = (TextPElement) xpath.evaluate("//text:p[last()]", contentA, XPathConstants.NODE);
			addFrameForEmbeddedDoc(contentA, lastPara, "./DOCB");
			OdfDocument docB = odtDoc1.loadSubDocument("DOCA/DOCB/");
			docB.newImage(ResourceUtilities.getURI(TEST_PIC_ANOTHER));
			OdfTable table1 = docB.getTableList().get(0);
			table1.setTableName("NewTable");
			Assert.assertNotNull(docB);
			Assert.assertNull(odtDoc1.loadSubDocument("DOCB/"));
			docA.save(TEST_FILE_FOLDER + TEST_FILE_EMBEDDED_INCLUDED_SAVE_OUT);
			OdfDocument testLoad = OdfDocument.loadDocument(TEST_FILE_FOLDER + TEST_FILE_EMBEDDED_INCLUDED_SAVE_OUT);
			OdfFileEntry imageEntry = testLoad.getPackage().getFileEntry(OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC);
			Assert.assertNotNull(imageEntry);
			Map<String, OdfDocument> embDocs = testLoad.loadSubDocuments(OdfDocument.OdfMediaType.SPREADSHEET);
			for (String childDocPath : embDocs.keySet()) {	
				OdfDocument doc1 = embDocs.get(childDocPath);
				imageEntry = doc1.getPackage().getFileEntry(doc1.getDocumentPath() + OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC_ANOTHER);
				Assert.assertNotNull(doc1.getTableByName("NewTable"));
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	/**
	 * The document A contains the embedded document E1,
	 * This test case is used to show how to save the E1 to a stand alone document.
	 */
	@Test
	public void testSaveEmbeddedDocument() {
		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_EMBEDDED));
			Map<String, OdfDocument> embeddedDocs = doc.loadSubDocuments();
			for (String childDocPath : embeddedDocs.keySet()) {
				OdfDocument childDoc = embeddedDocs.get(childDocPath);
				String embedFileName = childDoc.getDocumentPath();
				OdfMediaType embedMediaType = OdfMediaType.getOdfMediaType(childDoc.getMediaTypeString());
				//use '_' replace '/', because '/' is not the valid char in file path
				embedFileName = embedFileName.replaceAll("/", "_") + "." + embedMediaType.getSuffix();
				childDoc.save(TEST_FILE_FOLDER + embedFileName);
				LOG.log(Level.INFO, "Save file : {0}", TEST_FILE_FOLDER + embedFileName);
				OdfDocument embeddedDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(embedFileName));
				Assert.assertEquals(embeddedDoc.getMediaTypeString(), embedMediaType.getMediaTypeString());
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}

	}

	/**
	 * There are two document, one is Presentation1_INVALID.odp
	 * another is a new text document TestModifiedEmbeddedDoc.odt
	 * Presentation1_INVALID.odp contains an embed document named "Object 1/", add one paragraph to Object 1
	 * then embed "Object 1" to the new text document, and save this text document
	 * reload TestModifiedEmbeddedDoc.odt, then get and modify embed document "DocA" and save it to a standalone document
	 * load the saved standalone document, and check the content of it
	 */
	@Test
	public void testEmbedModifiedEmbeddedDocument() {
		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_EMBEDDED));
			OdfDocument saveDoc = OdfTextDocument.newTextDocument();
			OdfDocument embeddedDoc = doc.loadSubDocument("Object 1/");
			//modify content of "Object 1"
			OdfFileDom embedContentDom = embeddedDoc.getContentDom();
			XPath xpath = embedContentDom.getXPath();
			TextHElement header = (TextHElement) xpath.evaluate("//text:h[1]", embedContentDom, XPathConstants.NODE);
			LOG.log(Level.INFO, "First para: {0}", header.getTextContent());
			OdfTextSpan spanElem = new OdfTextSpan(embedContentDom);
			spanElem.setTextContent(TEST_SPAN_TEXT);
			header.appendChild(spanElem);
			//insert image to "Object 1"
			embeddedDoc.newImage(ResourceUtilities.getURI(TEST_PIC));
			//embed "Object 1" to TestModifiedEmbeddedDoc.odt as the path /DocA
			String embedPath = "DocA";
			saveDoc.insertDocument(embeddedDoc, embedPath);
			saveDoc.save(TEST_FILE_FOLDER + TEST_FILE_MODIFIED_EMBEDDED);
			saveDoc.close();
			//reload TestModifiedEmbeddedDoc.odt
			saveDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_MODIFIED_EMBEDDED));
			embeddedDoc = saveDoc.loadSubDocument(embedPath);
			//check the content of "DocA" and modify it again
			embedContentDom = embeddedDoc.getContentDom();
			header = (TextHElement) xpath.evaluate("//text:h[1]", embedContentDom, XPathConstants.NODE);
			Assert.assertTrue(header.getTextContent().contains(TEST_SPAN_TEXT));
			header.setTextContent("");
			String packagePath = embeddedDoc.getDocumentPath() + SLASH + OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC;
			OdfFileEntry imageEntry = embeddedDoc.getPackage().getFileEntry(packagePath);
			Assert.assertNotNull(imageEntry);
			embeddedDoc.newImage(ResourceUtilities.getURI(TEST_PIC_ANOTHER));
			//save the "DocA" as the standalone document
			embeddedDoc.save(TEST_FILE_FOLDER + TEST_FILE_MODIFIED_EMBEDDED_SAVE_STANDALONE);
			//load the standalone document and check the content
			OdfDocument standaloneDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_MODIFIED_EMBEDDED_SAVE_STANDALONE));
			embedContentDom = standaloneDoc.getContentDom();
			header = (TextHElement) xpath.evaluate("//text:h[1]", embedContentDom, XPathConstants.NODE);
			Assert.assertTrue(header.getTextContent().length() == 0);
			imageEntry = standaloneDoc.getPackage().getFileEntry(OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC);
			Assert.assertNotNull(imageEntry);
			OdfFileEntry anotherImageEntry = standaloneDoc.getPackage().getFileEntry(OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + TEST_PIC_ANOTHER);
			Assert.assertNotNull(anotherImageEntry);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	@Test
	public void testRemoveEmbeddedDocument() {
		try {
			OdfDocument doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_EMBEDDED));
			Map<String, OdfDocument> embeddedDocs = doc.loadSubDocuments();
			List<String> subDocNames = new ArrayList<String>();
			for (String childDocPath : embeddedDocs.keySet()) {
				OdfDocument childDoc = embeddedDocs.get(childDocPath);
				Assert.assertNotNull(childDoc);
				String embedFileName = childDoc.getDocumentPath();
				subDocNames.add(embedFileName);
				doc.removeDocument(embedFileName);
			}
			doc.save(TEST_FILE_FOLDER + TEST_FILE_REMOVE_EMBEDDED_SAVE_OUT);
			doc.close();
			//check manifest entry for the embed document 
			//the sub entry of the embed document such as the pictures of the document should also be removed
			doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_REMOVE_EMBEDDED_SAVE_OUT));
			Map<String, OdfDocument> reloadedSubDocs = doc.loadSubDocuments();
			Assert.assertTrue(0 == reloadedSubDocs.size());
			Set<String> entries = doc.getPackage().getFilePaths();
			Iterator<String> entryIter = null;
			for (int i = 0; i < subDocNames.size(); i++) {
				entryIter = entries.iterator();
				String embeddedDocPath = subDocNames.get(i);
				while (entryIter.hasNext()) {
					String entry = entryIter.next();
					Assert.assertFalse(entry.startsWith(embeddedDocPath));
				}
			}
			doc.close();
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, null, ex);
			Assert.fail("Failed with " + ex.getClass().getName() + ": '" + ex.getMessage() + "'");
		}
	}

	private void addFrameForEmbeddedDoc(OdfFileDom dom, TextPElement para, String path) throws Exception {
		OdfDrawFrame drawFrame = new OdfDrawFrame(dom);
		drawFrame.setDrawNameAttribute(path);
		drawFrame.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PARAGRAPH.toString());
		drawFrame.setSvgXAttribute("0.834cm");
		drawFrame.setSvgYAttribute("2.919cm");
		drawFrame.setSvgWidthAttribute("13.257cm");
		drawFrame.setSvgHeightAttribute("11.375cm");
		drawFrame.setDrawZIndexAttribute(0);

		DrawObjectElement object = new DrawObjectElement(dom);

		object.setXlinkHrefAttribute(path);
		object.setXlinkActuateAttribute("onLoad");
		object.setXlinkShowAttribute("embed");
		object.setXlinkTypeAttribute("simple");
		drawFrame.appendChild(object);
		para.appendChild(drawFrame);
	}

	private void updateFrameForEmbeddedDoc(OdfFileDom dom, String originPath, String newPath) throws Exception {
		NodeList objNodes = dom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "object");
		for (int i = 0; i < objNodes.getLength(); i++) {
			OdfElement object = (OdfElement) objNodes.item(i);
			String refObjPath = object.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
			if (refObjPath.equals(originPath)) {
				object.setAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href", newPath);
			}
		}
	}
}
