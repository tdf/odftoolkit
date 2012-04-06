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
package org.odftoolkit.odfdom.dom;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.pkg.OdfAlienElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathTest {

	private static final Logger LOG = Logger.getLogger(XPathTest.class.getName());
	private static final String SOURCE_FILE_1 = "XPathTest-foreignPrefix.odp";
	private static final String SOURCE_FILE_2 = "XPathTest-foreignPrefix2.odp";
        private static final String SOURCE_FILE_3 = "XPathTest-duplicate-prefix.odt";
	/**
	 * 1) The first test document "XPathTest-foreignPrefix.odp" uses the prefix "daisy" instead of "office" for ODF XML elements.
	   <daisy:document-content xmlns:daisy="urn:oasis:names:tc:opendocument:xmlns:office:1.0" xmlns:style="ur...
			<daisy:scripts/>
			<daisy:automatic-styles>


	 * 2) The test doc also uses multiple identical namespace prefixes with different URIs.
	 * <text:p>Slide
	 *		<draw:element1 xmlns:draw="urn://dummy-namespace-one">
	 *			<draw:element2 xmlns:draw="urn://dummy-namespace-two"
	 *				draw:attribute2="importantValue">dummy</draw:element2></draw:element1>One!!</text:p>
	 * It will be tested, if they have been renamed correctly.

	 * 3) The test doc also uses multiple different namespace prefixes with the similar URIs.
	 *    Testing getPrefixes(String URI) from the NamespaceContext interface.
	 * <text:p>Slide
	 *		<prefixOne:element1 xmlns:prefixOne="urn://some-test-odfdom-namespace">
	 *			<prefixTwo:element2 xmlns:prefixTwo="urn://some-test-odfdom-namespace"
	 *				prefixTwo:attribute2="importantValue">dummy</prefixTwo:element2></prefixOne:element1>Two!!</text:p>
	 *
	 * 4) Having element and attribute without namespace
	 * <!-- element and attribute without namespace with an attribute value with a namespace prefix as well -->
       <text:p xmlns:alien="urn://some-test-attribute-value-namespace"
	 *		foreign="alien:valueOfAlienAttribute">Some<test>good</test> Content!!</text:p>

	 */
	@Test
	public void testXPathwithAlienNodes() throws Exception {
		try {
			OdfPresentationDocument odpWithSlides = OdfPresentationDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE_FILE_1));
			OdfFileDom contentDom = odpWithSlides.getContentDom();

			XPath xpath = contentDom.getXPath();
			// Test scenario 1 - see comment above
			Iterator<String> prefixes = contentDom.getPrefixes("urn:oasis:names:tc:opendocument:xmlns:office:1.0");
			// The first prefix have to be "office"
			String prefix = prefixes.next();
			Assert.assertTrue(prefix.equals("office") || prefix.equals("daisy"));
			if(prefix.equals("office")){
				prefix = prefixes.next();
				Assert.assertTrue(prefix.equals("daisy"));
			}else if(prefix.equals("daisy")){
				prefix = prefixes.next();
				Assert.assertTrue(prefix.equals("office"));
			}else{
				Assert.fail();
			}
			// There should be no further prefix
			Assert.assertFalse(prefixes.hasNext());
					
			Iterator<String> prefixes2 = contentDom.getPrefixes("urn://some-test-odfdom-namespace");
			prefix = prefixes2.next();
			Assert.assertTrue(prefix.equals("prefixOne") || prefix.equals("prefixTwo"));
			if(prefix.equals("prefixOne")){
				prefix = prefixes2.next();
				Assert.assertTrue(prefix.equals("prefixTwo"));				
			}else if(prefix.equals("prefixTwo")){
				prefix = prefixes2.next();
				Assert.assertTrue(prefix.equals("prefixOne"));				
			}else{
				Assert.fail();
			}
			// There should be no further prefix
			Assert.assertFalse(prefixes.hasNext());

			Node rootNode = contentDom.getRootElement();
			odpWithSlides.save(ResourceUtilities.newTestOutputFile("XPathTest-ForeignPrefix-output.odp"));

			if (rootNode instanceof OdfAlienElement) {
				Assert.fail("The none OOO default prefix for office: was not exchanged!");
			}
			NodeList styleNameAttributes = (NodeList) xpath.evaluate(".//*[@style:name]", rootNode, XPathConstants.NODESET);
			LOG.log(Level.INFO, "Amount of style:name is {0}", styleNameAttributes.getLength());

			Assert.assertTrue(styleNameAttributes.getLength() == 11);
			// test if the identical namespace prefixes with different URI have been renamed correctly (earlier draw:element1/draw:element2).
			String attributeWithDuplicatePrefix = (String) xpath.evaluate(".//draw__1:element1/draw__2:element2/@draw__2:attribute2", rootNode, XPathConstants.STRING);
			Assert.assertTrue(attributeWithDuplicatePrefix.equals("importantValue"));
			Assert.assertEquals("urn:oasis:names:tc:opendocument:xmlns:office:1.0", xpath.getNamespaceContext().getNamespaceURI("office"));
			Assert.assertEquals("http://www.w3.org/1999/xlink", xpath.getNamespaceContext().getNamespaceURI("xlink"));

			String alienAttributeValue = (String) xpath.evaluate(".//*/@foreign", rootNode, XPathConstants.STRING);
			LOG.log(Level.INFO, "The value of the alien attribute is {0}, expected is ''alien:valueOfAlienAttribute''!", alienAttributeValue);
			Assert.assertEquals("alien:valueOfAlienAttribute", alienAttributeValue);

			String alienElementValue = (String) xpath.evaluate("//text:p/test", rootNode, XPathConstants.STRING);
			LOG.log(Level.INFO, "The value of the alien element is {0}, expected is ''good''!", alienElementValue);
			Assert.assertEquals("good", alienElementValue);
			LOG.log(Level.INFO, "Amount of @alien:foreignAttribute and @style:name is {0}", ((NodeList) xpath.evaluate(".//*[@alien:foreignAttribute or @style:name]", rootNode, XPathConstants.NODESET)).getLength());
			Assert.assertTrue("Amount of @alien:foreignAttribute and @style:name is not 13!!", ((NodeList) xpath.evaluate(".//*[@alien:foreignAttribute or @style:name]", rootNode, XPathConstants.NODESET)).getLength() == 13);
			
			// Test if an empty iterator is being returned for a none existing URL
			Iterator<String> prefixes3 = contentDom.getPrefixes("urn://this-prefix-does-not-exist-in-the-xml");
			Assert.assertFalse("Not used prefix returned a none-empty iterator!", prefixes3.hasNext());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.toString());
		}
	}

	/**
	 * A typical test, that deals with xlinks in SOURCE_FILE_2
	 */
	@Test
	public void testCopyForeignSlide() {
		try {
			OdfPresentationDocument targetodp = OdfPresentationDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE_FILE_1));
			OdfPresentationDocument sourceodp = OdfPresentationDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE_FILE_2));

			int slidecount = sourceodp.getSlideCount();
			for (int i = 0; i < slidecount; i++) {
				targetodp.copyForeignSlide(i, sourceodp, i);
			}
			targetodp.save(ResourceUtilities.newTestOutputFile("XPathTest-ForeignPrefix2-output.odp"));
			targetodp.close();
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.toString());
		}
	}

	/**
	 * This test SHOULD fail, but it isn't. Its source code is identical to the first test in this unit test
	 * @throws Exception
	 */
	@Test
	public void testXPathIsMissingXLinkButItWillPassBecauseItTheSecondTestInThisUnitTest() throws Exception {
		try {
			OdfPresentationDocument odpWithSlides = OdfPresentationDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE_FILE_1));
			OdfFileDom contentDom = odpWithSlides.getContentDom();
			XPath xpath = contentDom.getXPath();

			Node node = odpWithSlides.getContentDom().getRootElement();
			NodeList linkNodes = (NodeList) xpath.evaluate(".//*[@xlink:href]", node, XPathConstants.NODESET);
			Assert.assertNotNull(linkNodes);
			Assert.assertEquals("urn:oasis:names:tc:opendocument:xmlns:office:1.0", xpath.getNamespaceContext().getNamespaceURI("office"));
			Assert.assertEquals("http://www.w3.org/1999/xlink", xpath.getNamespaceContext().getNamespaceURI("xlink"));

		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.toString());
		}
	}

        /**
         * This test checks if the XPath returned by OdfFileDom is correctly aware of namespacess.
         * The NamespaceContext implementation in OdfFileDom is aware of duplicate prefixes, but getNamespaceURI(prefix)
         * in OdfFileDom does not implement it correctly
         *
         * With the patch applied in OdfFileDom these tests pass correctly
         *
         * sample metadata rdf generate from Openoffice 3.3 looks like this :
         * <?xml version="1.0" encoding="utf-8"?>
         *   <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
         *     <rdf:Description rdf:about="../content.xml#id1366098766">
         *       <ns1:BungeniActionEvent xmlns:ns1="http://editor.bungeni.org/1.0/anx/"></ns1:BungeniActionEvent>
         *       <ns2:BungeniSectionID xmlns:ns2="http://editor.bungeni.org/1.0/anx/">Xda+5VC/SQKKG7Bk83a2JA</ns2:BungeniSectionID>
         *       <ns3:BungeniSectionType xmlns:ns3="http://editor.bungeni.org/1.0/anx/">Conclusion</ns3:BungeniSectionType>
         *       <ns4:hiddenBungeniMetaEditable xmlns:ns4="http://editor.bungeni.org/1.0/anx/">false</ns4:hiddenBungeniMetaEditable>
         *     </rdf:Description>
         *
         * @throws Exception
         */
        @Test
        public void testXPathDuplicatePrefixForForeignNamespace() throws Exception{
           try {

            OdfDocument odfDoc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SOURCE_FILE_3));
            OdfFileDom fileDom = odfDoc.getFileDom("meta/meta.rdf");
            
            // add additional duplicate NS prefixes to the DOM
            fileDom.setNamespace("anx", "http://editor.bungeni.org/1.0/anx/");
            fileDom.setNamespace("myrdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

            //get the XPath from the file dom and set its NS context to the OdfFileDom object
            XPath xpathFileDom = fileDom.getXPath();
            xpathFileDom.setNamespaceContext(fileDom);

            //PASS - this works correctly
            String rdfNsUri = fileDom.getNamespaceURI("rdf");
            LOG.log(Level.INFO, "The value of the nsuri is {0} expected ns-uri is ''http://www.w3.org/1999/02/22-rdf-syntax-ns#''", rdfNsUri);
            Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#", rdfNsUri);

            //FAIL - for duplicate RDF ns prefix
            String myrdfNsUri = fileDom.getNamespaceURI("myrdf");
            LOG.log(Level.INFO, "The value of the nsuri is {0} expected ns-uri is ''http://www.w3.org/1999/02/22-rdf-syntax-ns#''", myrdfNsUri);
            Assert.assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#", myrdfNsUri);

            //PASS - for custom NS prefix in document, only the first one
            String anxNsUri1 = fileDom.getNamespaceURI("ns1");
            LOG.log(Level.INFO, "The value of the nsuri is {0} expected ns-uri is ''http://editor.bungeni.org/1.0/anx/''", anxNsUri1);
            Assert.assertEquals("http://editor.bungeni.org/1.0/anx/", anxNsUri1);

            //FAIL - for custom NS prefix in document ns3, ns4, ns...
            String anxNsUri2 = fileDom.getNamespaceURI("ns2");
            LOG.log(Level.INFO, "The value of the ns-uri is {0} expected ns-uri is ''http://editor.bungeni.org/1.0/anx/''", anxNsUri2);
            Assert.assertEquals("http://editor.bungeni.org/1.0/anx/", anxNsUri2);

            //FAIL - for custom duplicate NS prefix anx
            String anxNsUriCustom = fileDom.getNamespaceURI("anx");
            LOG.log(Level.INFO, "The value of the ns-uri is {0} expected ns-uri is ''http://editor.bungeni.org/1.0/anx/''", anxNsUriCustom);
            Assert.assertEquals("http://editor.bungeni.org/1.0/anx/", anxNsUriCustom);

           } catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.toString());
           }
        }
}
