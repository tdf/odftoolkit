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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfChartDocument;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfGraphicsDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.element.OdfElementFactory;
import org.odftoolkit.odfdom.doc.element.draw.OdfFrame;
import org.odftoolkit.odfdom.doc.element.draw.OdfImage;
import org.odftoolkit.odfdom.doc.element.draw.OdfObject;
import org.odftoolkit.odfdom.doc.element.office.OdfAutomaticStyles;
import org.odftoolkit.odfdom.doc.element.style.OdfParagraphProperties;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfTextProperties;
import org.odftoolkit.odfdom.doc.element.text.OdfSpan;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.text.OdfParagraphElement;
import org.odftoolkit.odfdom.dom.element.text.OdfSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.type.text.OdfAnchorType;
import org.odftoolkit.odfdom.pkg.OdfPackage;

public class DocumentCreationTest {

    private static final String TEST_FILE_FOLDER = "test/resources/";
    private static final String TEST_FILE_EMBEDDED = TEST_FILE_FOLDER + "testEmbeddedDoc.odt";
    private static final String TEST_PIC = TEST_FILE_FOLDER + "test.jpg";
    private static final String TEST_SPAN_TEXT = " Find Nemo!!!";
    private static final String TEST_FILE_ACCESS_EMBEDDED = "build/test/TestAccess_EmbeddedDocument.odt";
    private static final String TEST_FILE_EMBEDDED_EMBEDDED = "build/test/TestAccess_EmbeddedinEmbedded.odt";
    private static final String TEST_FILE_SAVE_EMBEDDED = TEST_FILE_FOLDER + "testSaveEmbeddedDoc.odt";
    private static final String TEST_FILE_SAVE_EMBEDDED_OUT = "build/test/TestSaveEmbeddedDoc.odt";
    private static final String TEST_FILE_SAVE_EMBEDDED_OUT2 = "build/test/TestSaveEmbeddedDoc2.odt";
    private static final String TEST_FILE_SAVE_QUEER_PATH = "build/test/TestSaveQueerEmbeddedPathDoc1.odt";
    private static final String CORRUPTED_MIMETYPE_DOC = TEST_FILE_FOLDER + "CorruptedMimetypeDoc.odt";
    private static final String CORRUPTED_MIMETYPE_DOC_OUT = "build/test/TestSaveCorruptedMimetypeDoc.odt";
    private static final String CORRUPTED_MIMETYPE_CHART = TEST_FILE_FOLDER + "CorruptedMimetypeChart.odc";
    private static final String CORRUPTED_MIMETYPE_CHART_OUT = "build/test/TestSaveCorruptedMimetypeChart.odc";
    private XPath xpath;

    public DocumentCreationTest() {
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new OdfNamespace());
    }

    @Test
    public void createEmptyDocs() {
        try {
            OdfFileDom contentDom = null;

            OdfTextDocument odtDoc1 = OdfTextDocument.createTextDocument();
            OdfTextDocument odtDoc2 = OdfTextDocument.createTextDocument();
            contentDom = odtDoc2.getContentDom();
            odtDoc1.save("build/test/TestEmpty_OdfTextDocument.odt");

            OdfGraphicsDocument odgDoc1 = OdfGraphicsDocument.createGraphicsDocument();
            OdfGraphicsDocument odgDoc2 = OdfGraphicsDocument.createGraphicsDocument();
            contentDom = odgDoc2.getContentDom();
            odgDoc1.save("build/test/TestEmpty_OdfGraphicsDocument.odg");

            OdfSpreadsheetDocument odsDoc1 = OdfSpreadsheetDocument.createSpreadsheetDocument();
            OdfSpreadsheetDocument odsDoc2 = OdfSpreadsheetDocument.createSpreadsheetDocument();
            contentDom = odsDoc2.getContentDom();
            odsDoc1.save("build/test/TestEmpty_OdfSpreadsheetDocument.ods");

            OdfPresentationDocument odpDoc1 = OdfPresentationDocument.createPresentationDocument();
            OdfPresentationDocument odpDoc2 = OdfPresentationDocument.createPresentationDocument();
            contentDom = odpDoc2.getContentDom();
            odpDoc1.save("build/test/TestEmpty_OdfPresentationDocument.odp");

            OdfChartDocument odcDoc1 = OdfChartDocument.createChartDocument();
            OdfChartDocument odcDoc2 = OdfChartDocument.createChartDocument();
            contentDom = odcDoc2.getContentDom();
            odcDoc1.save("build/test/TestEmpty_OdfChartDocument.odc");

            /////////////////////////////////////////
            // ODFDOM PACKAGE LAYER - WIKI EXAMPLE //          
            /////////////////////////////////////////
            
            // loads the ODF document package from the path
            OdfPackage pkg = OdfPackage.loadPackage("build/test/TestEmpty_OdfTextDocument.odt");

            // loads the images from the URLs and inserts the image in the package, adapting the manifest
            pkg.insert(new URI("test/resources/test.jpg"), "Pictures/test.jpg");
            pkg.insert(new URI("http://odftoolkit.org/attachments/wiki_images/odftoolkit/Table_fruits_diagramm.jpg"), "someweiredname/tableandfruits.jpg");
            pkg.save("build/test/odfdom-wiki-package.odt");

            
            /////////////////////////////////////
            // ODFDOM XML LAYER - WIKI EXAMPLE //          
            /////////////////////////////////////
            
            // loads the ODF document from the path
             OdfDocument odfDoc = OdfDocument.loadDocument("build/test/TestEmpty_OdfTextDocument.odt");

            // get the ODF content as DOM tree representation
            OdfFileDom odfContent = odfDoc.getContentDom();

            //// W3C XPath initialization ''(JDK5 functionality)''  - XPath is the path within the XML file
            //// (Find XPath examples here: http://www.w3.org/TR/xpath#path-abbrev)
            XPath xpath2 = XPathFactory.newInstance().newXPath();
            xpath2.setNamespaceContext(new OdfNamespace());

            // receiving the first paragraph "//text:p[1]" ''(JDK5 functionality)''
            OdfParagraphElement para = (OdfParagraphElement) xpath2.evaluate("//text:p[1]", odfContent, XPathConstants.NODE);

            // adding an image - expecting the user to know that 
            // an image consists always of a 'draw:image' and a 'draw:frame' parent

            // FUTURE USAGE: para.createDrawFrame().createDrawImage("/myweb.org/images/myHoliday.png", "/Pictures/myHoliday.png");
            //             Child access methods are still not part of the v0.6.x releases
            // CURRENT USAGE:
            OdfFrame odfFrame =  (OdfFrame) OdfElementFactory.createOdfElement(odfContent, OdfFrame.ELEMENT_NAME);
            para.appendChild(odfFrame);
            OdfImage odfImage = (OdfImage) OdfElementFactory.createOdfElement(odfContent, OdfImage.ELEMENT_NAME);
            odfImage.insertImage(new URI("test/resources/test.jpg"));                        
            odfFrame.appendChild(odfImage);
            
            OdfImage odfImage2 = (OdfImage) OdfElementFactory.createOdfElement(odfContent, OdfImage.ELEMENT_NAME);
            odfImage2.insertImage(new URI("http://odftoolkit.org/attachments/wiki_images/odftoolkit/Table_fruits_diagramm.jpg"));                        
            odfFrame.appendChild(odfImage2);
            odfDoc.save("build/test/odfdom-wiki-dom.odt");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }

    @Test
    public void createEmbeddedDocs() {
        try {
            OdfTextDocument odtDoc1 = OdfTextDocument.createTextDocument();

            odtDoc1.embedDocument("Object1/", OdfTextDocument.createTextDocument());
            odtDoc1.embedDocument("Object2/", OdfTextDocument.createTextDocument());
            odtDoc1.embedDocument("Object3", OdfDocument.loadDocument("build/test/TestEmpty_OdfGraphicsDocument.odg"));
            odtDoc1.embedDocument("Object4", OdfChartDocument.createChartDocument());
            odtDoc1.embedDocument("Object5", OdfGraphicsDocument.createGraphicsDocument());
            odtDoc1.embedDocument("Object6", OdfPresentationDocument.createPresentationDocument());

            List<OdfDocument> embeddedDocs = odtDoc1.getEmbeddedDocuments();
            System.out.println("Embedded Docs Size: " + embeddedDocs.size());
            Assert.assertTrue(embeddedDocs.size() == 6);

            List<OdfDocument> embeddedTextDocs = odtDoc1.getEmbeddedDocuments(OdfDocument.OdfMediaType.TEXT);
            System.out.println("Only Embedded Text Docs Size: " + embeddedTextDocs.size());
            Assert.assertTrue(embeddedTextDocs.size() == 2);

            List<OdfDocument> embeddedChartDocs = odtDoc1.getEmbeddedDocuments(OdfDocument.OdfMediaType.CHART);
            System.out.println("Only Embedded Chart Docs Size: " + embeddedChartDocs.size());
            Assert.assertTrue(embeddedChartDocs.size() == 1);

            OdfDocument embeddedObject1 = odtDoc1.getEmbeddedDocument("Object1/");
            System.out.println("Embedded Object1 path: " + embeddedObject1.getDocumentPackagePath());
            System.out.println("Embedded Object1 media-type: " + embeddedObject1.getMediaType());
            Assert.assertEquals(embeddedObject1.getMediaType(), OdfDocument.OdfMediaType.TEXT.toString());

            OdfDocument embeddedObject3 = odtDoc1.getEmbeddedDocument("Object3");
            System.out.println("Embedded Object3 path: " + embeddedObject3.getDocumentPackagePath());
            System.out.println("Embedded Object3 media-type: " + embeddedObject3.getMediaType());
            Assert.assertEquals(embeddedObject3.getMediaType(), OdfDocument.OdfMediaType.GRAPHICS.toString());

            OdfDocument embeddedObject6 = odtDoc1.getEmbeddedDocument("Object6/");
            System.out.println("Embedded Object6 path: " + embeddedObject6.getDocumentPackagePath());
            System.out.println("Embedded Object6 media-type: " + embeddedObject6.getMediaType());
            Assert.assertEquals(embeddedObject6.getMediaType(), OdfDocument.OdfMediaType.PRESENTATION.toString());

            odtDoc1.save("build/test/TestCreate_EmbeddedDocuments.odt");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }

    }

    @Test
    public void accessEmbeddedDocs() {
        try {
            OdfDocument docWithEmbeddedObjects = OdfDocument.loadDocument(TEST_FILE_EMBEDDED);
            List<OdfDocument> embDocs = docWithEmbeddedObjects.getEmbeddedDocuments();
            String pathToEmbeddedObject = "";
            for (OdfDocument embDoc : embDocs) {
                System.out.println("Embedded file of " + TEST_FILE_EMBEDDED + " internal package path: " + embDoc.getDocumentPackagePath() + " mediaType: " + embDoc.getMediaType());
                pathToEmbeddedObject = embDoc.getDocumentPackagePath();
            }

            OdfDocument embDoc = docWithEmbeddedObjects.getEmbeddedDocument(pathToEmbeddedObject);
            OdfFileDom contentDom = embDoc.getContentDom();

            // Add text element
            OdfParagraphElement para = (OdfParagraphElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
            System.out.println("First para: " + para.getTextContent());
            OdfSpan spanElem = new OdfSpan(contentDom);
            spanElem.setTextContent(TEST_SPAN_TEXT);
            para.appendChild(spanElem);

            // Add frame and image element
            OdfParagraphElement paraLast = (OdfParagraphElement) xpath.evaluate("//text:p[last()]", contentDom, XPathConstants.NODE);
            addImageToDocument(contentDom, paraLast);

            // Access/Update automatic styles
            OdfAutomaticStyles autoStyles = embDoc.getContentDom().getAutomaticStyles();
            OdfStyle autoStyle = autoStyles.getStyle("P1", OdfStyleFamily.Paragraph);
            Assert.assertEquals(autoStyle.getName(), "P1");
            Assert.assertEquals(autoStyle.getFamilyName(), "paragraph");

            // Access/Update styles.xml
            OdfStyle documentStyle = embDoc.getDocumentStyles().getStyle("myStyle", OdfStyleFamily.Paragraph);
            Assert.assertEquals(documentStyle.getName(), "myStyle");
            Assert.assertEquals(documentStyle.getFamilyName(), "paragraph");

            documentStyle.setProperty(OdfTextProperties.FontWeight, "bold");
            documentStyle.setProperty(OdfParagraphProperties.BackgroundColor, "#14EA5D");

            // SAVE / LOAD
            docWithEmbeddedObjects.save(TEST_FILE_ACCESS_EMBEDDED);

            OdfDocument doc2 = OdfDocument.loadDocument(TEST_FILE_ACCESS_EMBEDDED);
            OdfDocument embDoc2 = doc2.getEmbeddedDocument("Object 1/");
            embDoc2.getStylesDom();
            OdfStyle documentStyle2 = embDoc2.getDocumentStyles().getStyle("myStyle", OdfStyleFamily.Paragraph);
            String prop2 = documentStyle2.getProperty(OdfTextProperties.FontWeight);
            Assert.assertEquals(prop2, "bold");

            OdfSpanElement spanTest = (OdfSpanElement) xpath.evaluate("//text:p[1]/text:span[1]", contentDom, XPathConstants.NODE);
            Assert.assertEquals(spanTest.getTextContent(), TEST_SPAN_TEXT);

        } catch (Exception ex) {
            Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void accessEmbeddedinEmbeddedDocs() {

        try {
            OdfDocument docWithEmbeddedObject = OdfDocument.loadDocument(TEST_FILE_EMBEDDED);

            // Test DOM Acces
            docWithEmbeddedObject.getDocumentStyles();
            docWithEmbeddedObject.getContentDom().getAutomaticStyles();
            docWithEmbeddedObject.getStylesDom();
            docWithEmbeddedObject.getContentDom();

            List<OdfDocument> embDocs = docWithEmbeddedObject.getEmbeddedDocuments();
            OdfDocument embDoc = embDocs.get(0);
            String pathToDoc = embDoc.getDocumentPackagePath() + "object in object/";
            embDoc.embedDocument(pathToDoc, OdfTextDocument.createTextDocument());
            Assert.assertNotNull(embDoc.getPackage().getFileEntry(pathToDoc));
            OdfFileDom contentDom = embDoc.getContentDom();

            OdfParagraphElement lastPara = (OdfParagraphElement) xpath.evaluate("//text:p[last()]", contentDom, XPathConstants.NODE);
            addFrameForEmbeddedDoc(contentDom, lastPara, "object in object");
            //embDoc.saveEmbeddedDoc();

            List<OdfDocument> emb_embDocs = embDoc.getEmbeddedDocuments();
            OdfDocument emb_embDoc = emb_embDocs.get(0);
            contentDom = emb_embDoc.getContentDom();

            OdfParagraphElement para = (OdfParagraphElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
            OdfSpan spanElem = new OdfSpan(contentDom);
            spanElem.setTextContent(TEST_SPAN_TEXT);
            para.appendChild(spanElem);

            //emb_embDoc.saveEmbeddedDoc();
            docWithEmbeddedObject.save(TEST_FILE_EMBEDDED_EMBEDDED);

            OdfDocument docWithdoubleEmbeddedDoc = OdfDocument.loadDocument(TEST_FILE_EMBEDDED_EMBEDDED);
            OdfDocument doubleEmbeddedDoc = docWithdoubleEmbeddedDoc.getEmbeddedDocument("Object 1/object in object");

            OdfFileDom dEDcontentDom = doubleEmbeddedDoc.getContentDom();
            OdfSpanElement spanTest = (OdfSpanElement) xpath.evaluate("//text:span[last()]", dEDcontentDom, XPathConstants.NODE);
            Assert.assertEquals(spanTest.getTextContent(), TEST_SPAN_TEXT);

        } catch (Exception ex) {
            Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testCacheDocuments() {
        OdfDocument docWithEmbeddedObjects;
        try {
            docWithEmbeddedObjects = OdfDocument.loadDocument(TEST_FILE_EMBEDDED);
            List<OdfDocument> embDocs = docWithEmbeddedObjects.getEmbeddedDocuments();
            OdfDocument doc1 = embDocs.get(0);
            OdfFileDom contentDom1 = doc1.getContentDom();
            OdfDocument doc2 = doc1.getEmbeddedDocument("Object 1");
            OdfFileDom contentDom2 = doc2.getContentDom();
            Assert.assertEquals(doc2, doc1);
            Assert.assertEquals(contentDom1, contentDom2);
        } catch (Exception ex) {
            Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testSaveEmbeddedDocuments() {
        OdfDocument docWithEmbeddedObjects;
        try {
            docWithEmbeddedObjects = OdfDocument.loadDocument(TEST_FILE_SAVE_EMBEDDED);
            List<OdfDocument> embDocs = docWithEmbeddedObjects.getEmbeddedDocuments(OdfDocument.OdfMediaType.GRAPHICS);
            // Graphics Doc
            OdfDocument doc1 = embDocs.get(0);
            Assert.assertNotNull(doc1);

            OdfFileDom contentDom = doc1.getContentDom();
            OdfParagraphElement para = (OdfParagraphElement) xpath.evaluate("//text:p[1]", contentDom, XPathConstants.NODE);
            OdfSpan spanElem = new OdfSpan(contentDom);
            spanElem.setTextContent(TEST_SPAN_TEXT);
            para.appendChild(spanElem);
            doc1.save(TEST_FILE_SAVE_EMBEDDED_OUT);

            // Load test
            OdfDocument loadedDoc = OdfDocument.loadDocument(TEST_FILE_SAVE_EMBEDDED_OUT);
            List<OdfDocument> embDocs2 = loadedDoc.getEmbeddedDocuments(OdfDocument.OdfMediaType.GRAPHICS);
            // Graphics Doc
            OdfDocument doc2 = embDocs2.get(0);
            OdfFileDom contentDom2 = doc2.getContentDom();
            OdfSpan span = (OdfSpan) xpath.evaluate("//text:span[last()]", contentDom2, XPathConstants.NODE);
            Assert.assertEquals(span.getTextContent(), TEST_SPAN_TEXT);

            List<OdfDocument> embDocs3 = loadedDoc.getEmbeddedDocuments(OdfDocument.OdfMediaType.TEXT);
            // Writer Doc
            OdfDocument doc3 = embDocs3.get(0);
            Assert.assertNotNull(doc3);
            OdfFileDom contentDom3 = doc3.getContentDom();
            OdfParagraphElement para2 = (OdfParagraphElement) xpath.evaluate("//text:p[1]", contentDom3, XPathConstants.NODE);
            addImageToDocument(contentDom3, para2);

            doc3.embedDocument(doc3.getDocumentPackagePath() + "/NewEmbedded/", OdfTextDocument.createTextDocument());
            OdfDocument doc4 = doc3.getEmbeddedDocument("Object 1/NewEmbedded");
            OdfParagraphElement para3 = (OdfParagraphElement) xpath.evaluate("//text:p[last()]", contentDom3, XPathConstants.NODE);
            addFrameForEmbeddedDoc(contentDom3, para3, "NewEmbedded");
            Assert.assertNotNull(doc4);
            doc4.save(TEST_FILE_SAVE_EMBEDDED_OUT2);

            OdfDocument testLoad = OdfDocument.loadDocument(TEST_FILE_SAVE_EMBEDDED_OUT2);
            Assert.assertNotNull(testLoad.getPackage().getFileEntry("Object 1/NewEmbedded/"));

        } catch (Exception ex) {
            Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    /**
     * OdfDocument docA and docB are embedded Odfdocuments, a containing b!
     * But the relative package path hierachiy is opposite, meaning 
     * OdfPackage/docB/docA
     */
    public void testDocumentWithQueerPath() {
        try {
          
            OdfDocument containerDoc = OdfTextDocument.createTextDocument();
            String pathToDocA = "docB/docA/";
            String pathToDocB = "docB/";
            containerDoc.embedDocument(pathToDocA, OdfTextDocument.createTextDocument());
            OdfDocument docA = containerDoc.getEmbeddedDocument(pathToDocA);
            Assert.assertNotNull(docA);
            docA.embedDocument(pathToDocB, OdfTextDocument.createTextDocument());
            OdfDocument docB = containerDoc.getEmbeddedDocument(pathToDocB);
            Assert.assertNotNull(docB);
            
            docB.save(TEST_FILE_SAVE_QUEER_PATH);
            
        } catch (Exception ex) {
            Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
        }   
    }
    
    /**
     * Test if saving OdfDocuments always results in a valid ODF file. This means
     * that the mimetype is written clearly at the first position in the zipped 
     * ODF package.
     */
    @Test
    public void testWritingCorrectMimetype() {
        try {
            OdfDocument docWithCorruptedMimetype = OdfDocument.loadDocument(CORRUPTED_MIMETYPE_DOC);
            docWithCorruptedMimetype.save(CORRUPTED_MIMETYPE_DOC_OUT);
            OdfDocument chartWithCorruptedMimetype = OdfDocument.loadDocument(CORRUPTED_MIMETYPE_CHART);
            chartWithCorruptedMimetype.save(CORRUPTED_MIMETYPE_CHART_OUT);
        } catch (Exception ex) {
            Logger.getLogger(DocumentCreationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addImageToDocument(OdfFileDom dom, OdfParagraphElement para) throws Exception {
        OdfFrame drawFrame = new OdfFrame(dom);
        drawFrame.setName("graphics1");
        drawFrame.setAnchorType(OdfAnchorType.PARAGRAPH);
        drawFrame.setWidth("4.233cm");
        drawFrame.setHeight("4.233cm");
        drawFrame.setZIndex(0);
        para.appendChild(drawFrame);
            
        OdfImage image = new OdfImage(dom);
        image.insertImage(new URI(TEST_PIC));
        drawFrame.appendChild(image);
    }

    private void addFrameForEmbeddedDoc(OdfFileDom dom, OdfParagraphElement para, String path) {
        OdfFrame drawFrame = new OdfFrame(dom);
        drawFrame.setName(path);
        drawFrame.setAnchorType(OdfAnchorType.PARAGRAPH);
        drawFrame.setX("0.834cm");
        drawFrame.setY("2.919cm");
        drawFrame.setWidth("13.257cm");
        drawFrame.setHeight("11.375cm");
        drawFrame.setZIndex(0);

        OdfObject object = new OdfObject(dom);
        object.setHref(path);

        drawFrame.appendChild(object);
        para.appendChild(drawFrame);
    }
}
