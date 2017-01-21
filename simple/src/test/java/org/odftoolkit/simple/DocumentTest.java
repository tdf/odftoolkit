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
package org.odftoolkit.simple;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.utils.NodeAction;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class DocumentTest {

	private static final Logger LOG = Logger.getLogger(DocumentTest.class.getName());
	private static final String TEST_FILE = "test2.odt";
	private static final String TEST_FILE_WITHOUT_OPT = "no_size_opt.odt";
	private static final String ODF_FORMULAR_TEST_FILE = "SimpleFormula.odf";
	private static final String IMAGE_TEST_FILE = "testA.jpg";
	private static final String GENERATED_INVALID_SPREADSHEET = "invalid.ods";
	private static final String ZERO_BYTE_SPREADSHEET = "empty_file.ods";

	public DocumentTest() {
	}

	@Test
	public void loadDocument() {
		try {
			System.setProperty("org.odftoolkit.odfdom.tmpfile.disable", "true");
			// LOAD INVALID GENERATED SPREADSHEET DOCUMENT
			LOG.info("Loading an supported ODF Spreadsheet document as an ODF Document!");
			try {
				// Should work!
				Document ods = Document.loadDocument(ResourceUtilities.getAbsolutePath(GENERATED_INVALID_SPREADSHEET));
				Assert.assertNotNull(ods);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
				Assert.fail();
			}


			// LOAD EMPTY DOCUMENT
			LOG.info("Loading an empty document as an ODF Document!");
			try {
				// Should throw error!
				Document ods = Document.loadDocument(ResourceUtilities.getAbsolutePath(ZERO_BYTE_SPREADSHEET));
				Assert.fail();
			} catch (Exception e) {
				if (!e.getMessage().contains("shall be a ZIP file")) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
					Assert.fail();
				}
			}

			// LOAD FORMULA DOCUMENT
			LOG.info("Loading an unsupported ODF Formula document as an ODF Document!");
			try {
				// Exception is expected!
				Document.loadDocument(ResourceUtilities.getAbsolutePath(ODF_FORMULAR_TEST_FILE));
				Assert.fail();
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().contains("is not yet supported!")) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
					Assert.fail();
				}
			}

			// LOAD DOCUMENT IMAGE
			LOG.info("Loading an unsupported image file as an ODF Document!");
			try {
				// Exception is expected!
				Document.loadDocument(ResourceUtilities.getAbsolutePath(IMAGE_TEST_FILE));
				Assert.fail();
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().contains("shall be a ZIP file")) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
					Assert.fail();
				}
			} catch (OdfValidationException e) {
				if (!e.getMessage().contains("shall be a ZIP file")) {
					LOG.log(Level.SEVERE, e.getMessage(), e);
					Assert.fail();
				}
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testParser() {
		try {
			Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentRoot() {
		try {
			TextDocument odt = (TextDocument) Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_WITHOUT_OPT));
			Assert.assertNotNull(odt.getContentRoot());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	@Ignore
	public void testDumpDom() {
		try {
			Assert.assertTrue(testXSLT("content") & testXSLT("styles"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	private static boolean testXSLT(String odfFileNamePrefix) throws Exception {
		Document odfdoc = Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

		Transformer trans = TransformerFactory.newInstance().newTransformer();
		trans.setOutputProperty("indent", "yes");
		// trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		LOG.log(Level.INFO, "---------- {0}.xml transformed and compared ---------", odfFileNamePrefix);
		// The XML file (e.g. content.xml) is transformed by XSLT into the similar/identical XML file
		ByteArrayOutputStream xmlBytes = new ByteArrayOutputStream();
		OdfFileDom fileDom = null;
		if (odfFileNamePrefix.equals("content")) {
			fileDom = odfdoc.getContentDom();
		} else {
			fileDom = odfdoc.getStylesDom();
		}
		// transforming the XML using identical transformation
		trans.transform(new DOMSource(fileDom), new StreamResult(xmlBytes));
		String xmlString = xmlBytes.toString("UTF-8");
		// Saving test file to disc
		saveString(xmlString, ResourceUtilities.getTestOutputFolder() + odfFileNamePrefix + "-temporary-test.xml");

		// The template XML was once transformed and saved to the resource folder to gurantee the same indentation
		String xmlStringOriginal = inputStreamToString(ResourceUtilities.getTestResourceAsStream("test2-" + odfFileNamePrefix + ".xml"));
		// Saving original file to disc
		saveString(xmlStringOriginal, ResourceUtilities.getTestOutputFolder() + odfFileNamePrefix + "-temporary-original.xml");


		// Loading original file back to string representation
		String testString = inputStreamToString(new FileInputStream(ResourceUtilities.getTestOutputFolder() + odfFileNamePrefix + "-temporary-test.xml"));
		// Loading test file back to string representation
		String originalString = inputStreamToString(new FileInputStream(ResourceUtilities.getTestOutputFolder() + odfFileNamePrefix + "-temporary-original.xml"));

		boolean xmlEqual = originalString.equals(testString);
		if (!xmlEqual) {
			String testFilePath = ResourceUtilities.getTestOutputFolder() + odfFileNamePrefix + "-final-test.xml";
			String originalFilePath = ResourceUtilities.getTestOutputFolder() + odfFileNamePrefix + "-final-original.xml";
			saveString(testString, testFilePath);
			saveString(originalString, originalFilePath);
			LOG.log(Level.SEVERE, "Please compare the XML of two file:\n{0}\n and \n{1}", new Object[]{testFilePath, originalFilePath});
		}
		return xmlEqual;
	}

	@Test
	public void testStylesDom() {
		try {
			Document odfdoc = Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

			OdfStylesDom stylesDom = odfdoc.getStylesDom();
			Assert.assertNotNull(stylesDom);

			// test styles.xml:styles
			OdfOfficeStyles styles = odfdoc.getDocumentStyles();
			Assert.assertNotNull(styles);

			Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Graphic));
			Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Paragraph));
			Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.Table));
			Assert.assertNotNull(styles.getDefaultStyle(OdfStyleFamily.TableRow));

			OdfStyle style = styles.getStyle("Standard", OdfStyleFamily.Paragraph);
			Assert.assertNotNull(style);
			Assert.assertEquals(style.getStyleClassAttribute(), "text");

			style = styles.getStyle("List", OdfStyleFamily.Paragraph);
			Assert.assertNotNull(style);
			Assert.assertEquals(style.getProperty(StyleTextPropertiesElement.FontNameComplex), "Tahoma1");
			Assert.assertTrue(style.hasProperty(StyleTextPropertiesElement.FontNameComplex));
			Assert.assertFalse(style.hasProperty(StyleTextPropertiesElement.FontNameAsian));

			Assert.assertNull(styles.getStyle("foobar", OdfStyleFamily.Chart));

			// test styles.xml:automatic-styles
			OdfOfficeAutomaticStyles autostyles = stylesDom.getAutomaticStyles();
			Assert.assertNotNull(autostyles);

			OdfStylePageLayout pageLayout = autostyles.getPageLayout("pm1");
			Assert.assertNotNull(pageLayout);
			Assert.assertEquals(pageLayout.getProperty(StylePageLayoutPropertiesElement.PageWidth), "8.5in");
			Assert.assertEquals(pageLayout.getProperty(StylePageLayoutPropertiesElement.PageHeight), "11in");

			Assert.assertNull(autostyles.getStyle("foobar", OdfStyleFamily.Chart));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testContentNode() {
		try {
			Document odfdoc = Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

			OdfContentDom contentDom = odfdoc.getContentDom();

			// test content.xml:automatic-styles
			OdfOfficeAutomaticStyles autoStyles = contentDom.getAutomaticStyles();
			Assert.assertNotNull(autoStyles);

			OdfStyle style = autoStyles.getStyle("P1", OdfStyleFamily.Paragraph);
			Assert.assertNotNull(style);
			Assert.assertEquals(style.getStyleNameAttribute(), "P1");
			Assert.assertEquals(style.getStyleParentStyleNameAttribute(), "Text_20_body");
			Assert.assertEquals(style.getStyleListStyleNameAttribute(), "L1");

			style = autoStyles.getStyle("T1", OdfStyleFamily.Text);
			Assert.assertNotNull(style);
			Assert.assertEquals(style.getStyleNameAttribute(), "T1");

			for (OdfStyle testStyle : autoStyles.getStylesForFamily(OdfStyleFamily.Paragraph)) {
				testStyle(testStyle);
			}

			for (OdfStyle testStyle : autoStyles.getStylesForFamily(OdfStyleFamily.Text)) {
				testStyle(testStyle);
			}

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testSaveDocument() {
		try {
			Document odfdoc = Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
			new NodeAction<String>() {

				@Override
				protected void apply(Node cur, String replace, int depth) {
					if (cur.getNodeType() == Node.TEXT_NODE) {
						cur.setNodeValue(cur.getNodeValue().replaceAll("\\w", replace));
					}
				}
			};
//            replaceText.performAction(e, "X");
			odfdoc.save(ResourceUtilities.newTestOutputFile("list-out.odt"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	private void testStyle(OdfStyle testStyle) throws Exception {
		OdfFileDom fileDom = (OdfFileDom) testStyle.getOwnerDocument();
		OdfOfficeAutomaticStyles autoStyles = null;
		if (testStyle.getStyleParentStyleNameAttribute() != null) {
			if (fileDom instanceof OdfContentDom) {
				autoStyles = ((OdfContentDom) fileDom).getAutomaticStyles();
			} else if (fileDom instanceof OdfStylesDom) {
				autoStyles = ((OdfStylesDom) fileDom).getAutomaticStyles();
			}
			OdfStyle parentStyle = autoStyles.getStyle(testStyle.getStyleParentStyleNameAttribute(), testStyle.getFamily());
			if (parentStyle == null) {
				parentStyle = ((Document) fileDom.getDocument()).getDocumentStyles().getStyle(testStyle.getStyleParentStyleNameAttribute(), testStyle.getFamily());
			}

			Assert.assertNotNull(parentStyle);
		}
		if (testStyle.hasOdfAttribute(OdfName.newName(OdfDocumentNamespace.STYLE, "list-style-name"))) {
			if (testStyle.getStyleListStyleNameAttribute() != null) {
				OdfTextListStyle listStyle = autoStyles.getListStyle(testStyle.getStyleListStyleNameAttribute());
				if (listStyle == null) {
					listStyle = ((Document) fileDom.getDocument()).getDocumentStyles().getListStyle(testStyle.getStyleListStyleNameAttribute());
				}

				Assert.assertNotNull(listStyle);
			}
		}
	}

	@Test
	public void testParsingOfInvalidAttribute() {
		try {
			// file with invalid value for enum text-underline-style
			File testfile = ResourceUtilities.newTestOutputFile("InvalidUnderlineAttribute.odt");

			// Test1: Loading shouldn't fail just because of one invalid attribute
			TextDocument odt = (TextDocument) Document.loadDocument(testfile);
			Assert.assertNotNull(odt);

			// Test2: invalid attribute node should have been be removed
//			OdfStyle styleNode = odt.getContentDom().getAutomaticStyles().getStyle("T1", OdfStyleFamily.Text);
//			StyleTextPropertiesElement props = OdfElement.findFirstChildNode(StyleTextPropertiesElement.class, styleNode);
//			Assert.assertFalse(props.hasAttribute("style:text-underline-style"));
//			odt.save(ResourceUtilities.newTestOutputFile("saving-is-possible2.odt"));

			// Test3: New ODF 1.2 attribute node should exist
			OdfStyle styleNode2 = odt.getStylesDom().getOfficeStyles().getStyle("bug77", OdfStyleFamily.Graphic);
			StyleGraphicPropertiesElement propsGrapicElement = OdfElement.findFirstChildNode(StyleGraphicPropertiesElement.class, styleNode2);
			Assert.assertTrue("Could not find the attribute svg:opac-capicity. Workaround bug77 did not succeeded!", propsGrapicElement.hasAttribute("svg:stroke-opacity"));
			odt.save(ResourceUtilities.newTestOutputFile("saving-is-possible.odt"));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testDocumentPassword() {
		File passwordOutputFile = ResourceUtilities.newTestOutputFile("PasswordDocument.odt");
		File noPassOutputFile = ResourceUtilities.newTestOutputFile("NoPasswordDocument.odt");
		try {
			TextDocument doc = TextDocument.newTextDocument();
			doc.addParagraph("blablabla...");
			doc.setPassword("password");
			doc.save(passwordOutputFile);

			Document redoc = Document.loadDocument(passwordOutputFile, "password");
			//test load content.xml
			Assert.assertNotNull(redoc.getContentRoot().toString());
			//test load styles.xml
			((TextDocument) redoc).getHeader().addTable();
			//test load meta.xml
			Assert.assertNotNull(redoc.getOfficeMetadata().getCreator());

			//remove password
			redoc.setPassword(null);
			redoc.save(noPassOutputFile);

			//test inserted document
			doc = TextDocument.newTextDocument();
			doc.addParagraph("embed_document");
			redoc.insertDocument(doc, "/embed");
			redoc.setPassword("password");

			redoc.save(passwordOutputFile);
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "document password test failed.", ex);
			Assert.fail();
		}
	}

	@Test
	public void testSetLocale() {
		String filename = "testDefaultLanguage.odp";
		try {
			PresentationDocument  doc = PresentationDocument .newPresentationDocument();

			Assert.assertNull(doc.getLocale(Document.ScriptType.WESTERN));
			Assert.assertNull(doc.getLocale(Document.ScriptType.CJK));
			Assert.assertNull(doc.getLocale(Document.ScriptType.CTL));

			Locale eng_can = new Locale(Locale.ENGLISH.getLanguage(),
					Locale.CANADA.getCountry());
			Locale chinese_china = new Locale(Locale.CHINESE.getLanguage(),
					Locale.CHINA.getCountry());
			Locale ar_eg = new Locale("ar", "eg");

			doc.setLocale(eng_can);
			doc.setLocale(chinese_china);
			doc.setLocale(ar_eg);

			doc.save(ResourceUtilities.newTestOutputFile(filename));

			PresentationDocument newDoc = PresentationDocument .loadDocument(ResourceUtilities.getTestResourceAsStream(filename));
			Assert.assertEquals(eng_can, newDoc.getLocale(Document.ScriptType.WESTERN));
			Assert.assertEquals(chinese_china, newDoc.getLocale(Document.ScriptType.CJK));
			Assert.assertEquals(ar_eg, newDoc.getLocale(Document.ScriptType.CTL));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLoadDocumentWithIllegalArgument() throws Exception {
		String filepath = ResourceUtilities.getAbsolutePath("presentation.odp");
		OdfPackage odfpackage = OdfPackage.loadPackage(filepath);
		// illegal internal patch
		try {
			Document.loadDocument(odfpackage, "odt");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("Given internalPath 'odt' is an illegal or inappropriate argument.", e.getMessage());
		}
		// illegal media type
		try {
			Document.loadDocument(odfpackage, "meta.xml");
		} catch (Exception e) {
			Assert.assertTrue(e instanceof IllegalArgumentException);
			Assert.assertEquals("Given mediaType 'text/xml' is either not yet supported or not an ODF mediatype!", e.getMessage());
		}
	}

	private static String inputStreamToString(InputStream in) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line).append("\n");
		}
		bufferedReader.close();
		return stringBuilder.toString();
	}

	/** Saves the datastring as UTF8 to the given filePath */
	private static void saveString(String dataString, String filePath) throws UnsupportedEncodingException, IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF8"));
		out.append(dataString);
		out.close();
	}

	@Test
	public void testGetEmbeddedDocuments() throws Exception {
		try {
			Document doc = Document.loadDocument(OdfPackage.loadPackage(this.getClass().getResource("/").getPath() + "presentationWithEmbedDoc.odp"));
			Document.OdfMediaType odfMediaType_Chart = Document.OdfMediaType.valueOf(Document.OdfMediaType.class, "CHART");
			List<Document> doclist = doc.getEmbeddedDocuments(odfMediaType_Chart);
			if(doclist.size() > 0){
				for(Document docitem : doclist){
					Assert.assertEquals("application/vnd.oasis.opendocument.chart", docitem.getMediaTypeString());
				}
			}else{
				Assert.fail();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testGetEmbeddedDocument() throws Exception {
		try {
			Document doc = Document.loadDocument(OdfPackage.loadPackage(this.getClass().getResource("/").getPath() + "Spreadsheet with Embeded Chart.ods"));
			Document innerdoc = doc.getEmbeddedDocument("/");
			Assert.assertNotNull(innerdoc);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentRootDoc() throws Exception {
		try {
			Document odt = Document.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_WITHOUT_OPT));
			OdfContentDom odfcon = odt.getContentDom();
			Assert.assertNotNull(odfcon);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testToString() throws Exception {
		try {
			Document doc = Document.loadDocument(ResourceUtilities.getTestResourceAsStream("test2.odt"));
			String docStr = doc.toString();
			System.out.println(docStr);
			Assert.assertTrue(docStr.indexOf("application/vnd.oasis.opendocument.text")>0);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSave() throws Exception {
		try {
			Document doc = Document.loadDocument(ResourceUtilities.getAbsolutePath("test2.odt"));
			Assert.assertNotNull(doc);
			String docmedia = doc.getOdfMediaType().getMediaTypeString();
			//create a document for test
			Document doctest = Document.loadDocument(ResourceUtilities.getAbsolutePath("test2.odt"));
			doctest.save(ResourceUtilities.newTestOutputFile("testSave.odt"));
			String filePath = ResourceUtilities.getAbsolutePath("testSave.odt");
			File outerFile = new File(filePath);
			if(!outerFile.exists()){
				System.out.println("outerFile is not exist. ");
			}
			Assert.assertNotNull(doc);

			OutputStream out = new FileOutputStream(outerFile);
			doc.save(out);

			//save
			doc.save(ResourceUtilities.newTestOutputFile("testSave.odt"));
			out.close();

			//verify
			Document docnew = Document.loadDocument(ResourceUtilities.getTestResourceAsStream("testSave.odt"));
			Assert.assertNotNull(docnew);
			String docnewMedia = docnew.getOdfMediaType().getMediaTypeString();

			Assert.assertEquals(docmedia, docnewMedia);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
    /**
     * Testing the 'Hello World' example of https://incubator.apache.org/odftoolkit/simple/gettingstartguide.html
     */
	public void testHelloWorldExample() throws Exception {
        TextDocument outputOdt;
        try {
            outputOdt = TextDocument.newTextDocument();

            // add image
            outputOdt.newImage(new URI(ResourceUtilities.getAbsolutePath("testA.jpg")));

            // add paragraph
            outputOdt.addParagraph("Hello World, Hello Simple ODF!");

            // add list
            outputOdt.addParagraph("The following is a list.");
            org.odftoolkit.simple.text.list.List list = outputOdt.addList();
            String[] items = {"item1", "item2", "item3"};
            list.addItems(items);

            // add table
            Table table = outputOdt.addTable(2, 2);
            Cell cell = table.getCellByPosition(0, 0);
            cell.setStringValue("Hello World!");

            outputOdt.save(ResourceUtilities.newTestOutputFile("HelloWorldExample.odt"));
            outputOdt.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
