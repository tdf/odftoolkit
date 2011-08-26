/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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
import org.odftoolkit.odfdom.pkg.OdfAlienElement;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathTest {

	private static final Logger LOG = Logger.getLogger(XPathTest.class.getName());
	private static final String SOURCE_FILE_1 = "XPathTest-foreignPrefix.odp";
	private static final String SOURCE_FILE_2 = "XPathTest-foreignPrefix2.odp";

	/**
	 * 1) The first test document "slideDeckWithTwoSlides.odp" uses the prefix "daisy" instead of "office" for ODF XML elements.
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
			OdfPresentationDocument odpWithSlides = OdfPresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(SOURCE_FILE_1));
			OdfContentDom contentDom = odpWithSlides.getContentDom();

			XPath xpath = contentDom.getXPath();
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

			Node node = contentDom.getRootElement();
			odpWithSlides.save(ResourceUtilities.newTestOutputFile("XPathTest-ForeignPrefix-output.odp"));

			if (node instanceof OdfAlienElement) {
				Assert.fail("The none OOO default prefix for office: was not exchanged!");
			}
			NodeList linkNodes = (NodeList) xpath.evaluate(".//*[@xlink:href]", node, XPathConstants.NODESET);
			Assert.assertNotNull(linkNodes);
			// test if the identical namespace prefixes with different URI have been renamed correctly (earlier draw:element1/draw:element2).
			String attributeWithDuplicatePrefix = (String) xpath.evaluate(".//draw__1:element1/draw__2:element2/@draw__2:attribute2", node, XPathConstants.STRING);
			Assert.assertTrue(attributeWithDuplicatePrefix.equals("importantValue"));
			Assert.assertEquals("urn:oasis:names:tc:opendocument:xmlns:office:1.0", xpath.getNamespaceContext().getNamespaceURI("office"));
			Assert.assertEquals("http://www.w3.org/1999/xlink", xpath.getNamespaceContext().getNamespaceURI("xlink"));

			String alienAttributeValue = (String) xpath.evaluate(".//*/@foreign", node, XPathConstants.STRING);
			LOG.log(Level.INFO, "The value of the alien attribute is {0}, expected is ''alien:valueOfAlienAttribute''!", alienAttributeValue);
			Assert.assertEquals("alien:valueOfAlienAttribute", alienAttributeValue);

			String alienElementValue = (String) xpath.evaluate("//text:p/test", node, XPathConstants.STRING);
			LOG.log(Level.INFO, "The value of the alien element is {0}, expected is ''good''!", alienElementValue);
			Assert.assertEquals("good", alienElementValue);
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
			OdfPresentationDocument targetodp = OdfPresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(SOURCE_FILE_1));
			OdfPresentationDocument sourceodp = OdfPresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(SOURCE_FILE_2));

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
			OdfPresentationDocument odpWithSlides = OdfPresentationDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(SOURCE_FILE_1));
			OdfContentDom contentDom = odpWithSlides.getContentDom();
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
}
