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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
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
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfPackageConstraint;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.odftoolkit.odfdom.pkg.ValidationConstraint;
import org.odftoolkit.odfdom.utils.ErrorHandlerStub;
import org.odftoolkit.odfdom.utils.NodeAction;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;

public class DocumentTest {

	private static final Logger LOG = Logger.getLogger(DocumentTest.class.getName());
	private static final String TEST_FILE = "test2.odt";
	private static final String TEST_FILE_WITHOUT_OPT = "no_size_opt.odt";
	private static final String ODF_FORMULAR_TEST_FILE = "SimpleFormula.odf";
	private static final String IMAGE_TEST_FILE = "testA.jpg";
	private static final String GENERATED_INVALID_SPREADSHEET = "invalid.ods";
	private static final String ZERO_BYTE_SPREADSHEET = "empty_file.ods";
	private static final long PRESENTATION1_DOC_COUNT = 11;

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
				OdfDocument ods = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(GENERATED_INVALID_SPREADSHEET));
				Assert.assertNotNull(ods);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, e.getMessage(), e);
				Assert.fail();
			}


			// LOAD EMPTY DOCUMENT
			LOG.info("Loading an empty document as an ODF Document!");
			try {
				// Should throw error!
				OdfDocument ods = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(ZERO_BYTE_SPREADSHEET));
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
				OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(ODF_FORMULAR_TEST_FILE));
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
				OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(IMAGE_TEST_FILE));
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
			OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetContentRoot() {
		try {
			OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE_WITHOUT_OPT));
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
		OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

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
			OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

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
			OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));

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
			OdfDocument odfdoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
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
				parentStyle = ((OdfDocument) fileDom.getDocument()).getDocumentStyles().getStyle(testStyle.getStyleParentStyleNameAttribute(), testStyle.getFamily());
			}

			Assert.assertNotNull(parentStyle);
		}
		if (testStyle.hasOdfAttribute(OdfName.newName(OdfDocumentNamespace.STYLE, "list-style-name"))) {
			if (testStyle.getStyleListStyleNameAttribute() != null) {
				OdfTextListStyle listStyle = autoStyles.getListStyle(testStyle.getStyleListStyleNameAttribute());
				if (listStyle == null) {
					listStyle = ((OdfDocument) fileDom.getDocument()).getDocumentStyles().getListStyle(testStyle.getStyleListStyleNameAttribute());
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
			OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument(testfile);
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
	public void testSetLocale() {
		String filename = "testDefaultLanguage.odp";
		try {
			OdfPresentationDocument doc = OdfPresentationDocument.newPresentationDocument();

			Assert.assertNull(doc.getLocale(OdfDocument.UnicodeGroup.WESTERN));
			Assert.assertNull(doc.getLocale(OdfDocument.UnicodeGroup.CJK));
			Assert.assertNull(doc.getLocale(OdfDocument.UnicodeGroup.CTL));

			Locale eng_can = new Locale(Locale.ENGLISH.getLanguage(),
					Locale.CANADA.getCountry());
			Locale chinese_china = new Locale(Locale.CHINESE.getLanguage(),
					Locale.CHINA.getCountry());
			Locale ar_eg = new Locale("ar", "eg");

			doc.setLocale(eng_can);
			doc.setLocale(chinese_china);
			doc.setLocale(ar_eg);

			doc.save(ResourceUtilities.newTestOutputFile(filename));

			OdfPresentationDocument newDoc = OdfPresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(filename));
			Assert.assertEquals(eng_can, newDoc.getLocale(OdfDocument.UnicodeGroup.WESTERN));
			Assert.assertEquals(chinese_china, newDoc.getLocale(OdfDocument.UnicodeGroup.CJK));
			Assert.assertEquals(ar_eg, newDoc.getLocale(OdfDocument.UnicodeGroup.CTL));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
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

	/** Saves the data string as UTF8 to the given filePath */
	private static void saveString(String dataString, String filePath) throws UnsupportedEncodingException, IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF8"));
		out.append(dataString);
		out.close();
	}

	@Test
	public void validationTest() {
		// TESTDOC2: Expected ODF Warnings
		Map expectedWarning2 = new HashMap();
		expectedWarning2.put(OdfPackageConstraint.MIMETYPE_NOT_IN_PACKAGE, 1);
		expectedWarning2.put(OdfPackageConstraint.MANIFEST_LISTS_DIRECTORY, 10);

		// TESTDOC2: Expected ODF Errors
		Map expectedErrors2 = new HashMap();
		expectedErrors2.put(OdfPackageConstraint.MANIFEST_DOES_NOT_LIST_FILE, 1);
		expectedErrors2.put(OdfPackageConstraint.MANIFEST_LISTS_NONEXISTENT_FILE, 3);
		expectedErrors2.put(OdfSchemaConstraint.DOCUMENT_WITHOUT_CONTENT_NOR_STYLES_XML, 1);
		expectedErrors2.put(OdfSchemaConstraint.PACKAGE_SHALL_CONTAIN_MIMETYPE, 1);
		ErrorHandlerStub handler2 = new ErrorHandlerStub(expectedWarning2, expectedErrors2, null);
		handler2.setTestFilePath("testInvalidPkg2.odt");


		// TESTDOC3: Expected ODF Warnings
		Map expectedWarning3 = new HashMap();
		expectedWarning3.put(OdfPackageConstraint.MANIFEST_LISTS_DIRECTORY, 21);

		// TESTDOC3: Expected ODF Errors
		Map expectedErrors3 = new HashMap();
		expectedErrors3.put(OdfPackageConstraint.MANIFEST_LISTS_NONEXISTENT_FILE, 2);
		expectedErrors3.put(OdfSchemaConstraint.DOCUMENT_WITHOUT_CONTENT_NOR_STYLES_XML, 1);
		expectedErrors3.put(OdfPackageConstraint.MANIFEST_WITH_EMPTY_PATH, 1);
		ErrorHandlerStub handler3 = new ErrorHandlerStub(expectedWarning3, expectedErrors3, null);
		handler3.setTestFilePath("performance/Presentation1_INVALID.odp");


		// TESTDOC1: Expected ODF Warnings
		Map expectedWarning1 = new HashMap();
		expectedWarning1.put(OdfPackageConstraint.MANIFEST_LISTS_DIRECTORY, 10);

		// TESTDOC1: Expected ODF Errors
		Map expectedErrors1 = new HashMap();
		expectedErrors1.put(OdfPackageConstraint.MIMETYPE_NOT_FIRST_IN_PACKAGE, 1);
		expectedErrors1.put(OdfPackageConstraint.MIMETYPE_IS_COMPRESSED, 1);
		expectedErrors1.put(OdfPackageConstraint.MIMETYPE_HAS_EXTRA_FIELD, 1);
		expectedErrors1.put(OdfPackageConstraint.MIMETYPE_DIFFERS_FROM_PACKAGE, 1);
		expectedErrors1.put(OdfPackageConstraint.MANIFEST_LISTS_NONEXISTENT_FILE, 1);

		// TESTDOC1: Expected ODF FatalErrors
		Map<ValidationConstraint, Integer> expectedFatalErrors1 = new HashMap<ValidationConstraint, Integer>();
		expectedFatalErrors1.put(OdfSchemaConstraint.DOCUMENT_WITHOUT_ODF_MIMETYPE, 1);

		ErrorHandlerStub handler1 = new ErrorHandlerStub(expectedWarning1, expectedErrors1, expectedFatalErrors1);
		handler1.setTestFilePath("testInvalidPkg1.odt");
		try {
			// First Test / Handler2
			OdfPackage pkg2 = OdfPackage.loadPackage(new File(ResourceUtilities.getAbsolutePath(handler2.getTestFilePath())), null, handler2);
			OdfDocument doc2 = OdfDocument.loadDocument(pkg2);
			Assert.assertNotNull(doc2);

			// Second Test / Handler3
			OdfPackage pkg3 = OdfPackage.loadPackage(new File(ResourceUtilities.getAbsolutePath(handler3.getTestFilePath())), null, handler3);
			OdfDocument doc3 = OdfDocument.loadDocument(pkg3);
			Assert.assertNotNull(doc3);
			Map subDocs = doc3.loadSubDocuments();
			Assert.assertNotNull(subDocs);
			Assert.assertEquals(PRESENTATION1_DOC_COUNT, subDocs.size());

			// Third Test / Handler1
			OdfPackage pkg1 = OdfPackage.loadPackage(new File(ResourceUtilities.getAbsolutePath(handler1.getTestFilePath())), null, handler1);
			OdfDocument.loadDocument(pkg1);
			Assert.fail();
		} catch (Exception e) {
			if (!e.getMessage().contains("is invalid for the ODF XML Schema document")) {
				Assert.fail();
			}
		}
		handler1.validate();
		handler2.validate();
		handler3.validate();
	}
}
