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

import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.test.ModelTestBase;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.text.TextBookmarkStartElement;
import org.odftoolkit.odfdom.dom.rdfa.BookmarkRDFMetadataExtractor;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class RDFMetadataTest extends ModelTestBase {

	private static final Logger LOG = Logger.getLogger(RDFMetadataTest.class
			.getName());
	private static final String SIMPLE_ODT = "test_rdfmeta.odt";

	public RDFMetadataTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testGetRDFMetaFromGRDDLXSLT() throws Exception {
		OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(SIMPLE_ODT));
		Model m1 = odt.getManifestRDFMetadata();
		LOG.info("RDF Model - manifest:\n" + m1.toString());
		long size1 = m1.size();
		TestCase.assertEquals(25, size1);

		Model m2 = odt.getInContentMetadata();
		LOG.info("RDF Model - rood document in-content:\n" + m1.toString());
		long size2 = m2.size();
		TestCase.assertEquals(20, size2);

		Model m = odt.getRDFMetadata();

		// This triples are duplicated in m1 and m2:
		// (test_rdfmeta.odt rdf:type
		// http://docs.oasis-open.org/ns/office/1.2/meta/pkg#Document)
		// (test_rdfmeta.odt/embeded.odt rdf:type
		// http://docs.oasis-open.org/ns/office/1.2/meta/pkg#Document)
//		int duplicated = 2;
		Model m3 = m1.intersection(m2);
		LOG.info("RDF Model - duplicated (manifest & in-content of root doc):\n" + m3.toString());
// Uncertain about the previuos statement, as the duplicated RDF triple do not have an identical subject..
//		TestCase.assertEquals(duplicated, m3.size());
//		TestCase.assertEquals(size1 + size2 - duplicated, m.size());

		// test the embeded document
		OdfDocument subDoc = odt.loadSubDocument("embeded.odt");
		m1 = subDoc.getManifestRDFMetadata();
		size1 = m1.size();
		TestCase.assertEquals(5, size1);

		m2 = subDoc.getInContentMetadata();
		size2 = m2.size();
		LOG.info("RDF Model - embedded document in content:\n" + m2.toString());
		TestCase.assertEquals(6, size2);

		m = subDoc.getRDFMetadata();

		// This triple is duplicated in m1 and m2:
		// (test_rdfmeta.odt/embeded.odt rdf:type
		// http://docs.oasis-open.org/ns/office/1.2/meta/pkg#Document)
//		duplicated = 1;
		m3 = m1.intersection(m2);
		LOG.info("RDF Model - intersection of in-content metadata of root & embedded document:\n" + m2.toString());
		//TestCase.assertEquals(duplicated, m3.size());
		//TestCase.assertEquals(size1 + size2 - duplicated, m.size());

	}

/*
/* Test based on the assumption that <table:table-cell> may contain plain text content, which is not allowed for ODF..
	@Test
    public void testGetInContentMetaFromCache() throws Exception {
		OdfTextDocument odt = (OdfTextDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath(SIMPLE_ODT));
		Model m1 = odt.getInContentMetadataFromCache();

		// We have the following 1 triple in cache:
		// (http://dbpedia.org/page/J._R._R._Tolkien
		// http://www.w3.org/2006/vcard/ns#fn 'John Ronald Reuel Tolkien')
		TestCase.assertEquals(1, m1.size());

		OdfContentDom contentDom = odt.getContentDom();
		XPath xpath = contentDom.getXPath();
		TextMetaElement tm = (TextMetaElement) xpath.evaluate(
				"//text:p/text:meta[last()]", contentDom, XPathConstants.NODE);

		tm.setXhtmlAboutAttribute("http://dbpedia.org/page/J._K._Rowling");
		m1 = odt.getInContentMetadataFromCache();
		PrintUtil.printOut(m1.listStatements());
		TestCase.assertEquals("http://dbpedia.org/page/J._K._Rowling", m1
				.listStatements().nextStatement().getSubject().getURI());

		tm.setTextContent("Joanne Kathleen Rowling");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals("Joanne Kathleen Rowling", m1.listStatements()
				.nextStatement().getObject().toString());

		tm.setXhtmlPropertyAttribute("dc:name");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals("http://purl.org/dc/elements/1.1/name", m1
				.listStatements().nextStatement().getPredicate().getURI());

		Node parent = tm.getParentNode();
		parent.removeChild(tm);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(0, m1.size());
		parent.appendChild(tm);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		parent.removeChild(tm);

		TextPElement pEle = contentDom.newOdfElement(TextPElement.class);
		parent.appendChild(pEle);
		pEle.setXhtmlAboutAttribute("[dbpedia:J._K._Rowling]");
		pEle.setXhtmlPropertyAttribute("dbpprop:birthDate dbpprop:dateOfBirth");
		pEle.setXhtmlDatatypeAttribute("xsd:date");
		pEle.setXhtmlContentAttribute("1965-07-31");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(2, m1.size());
		StmtIterator iter = m1.listStatements();
		while (iter.hasNext()) {
			Statement stmt = iter.nextStatement();
			TestCase.assertEquals("http://dbpedia.org/page/J._K._Rowling", stmt
					.getSubject().getURI());
			TestCase.assertTrue(stmt.getObject().canAs(Literal.class));
			Literal literal = stmt.getObject().as(Literal.class);
			TestCase.assertEquals("http://www.w3.org/2001/XMLSchema#date",
					literal.getDatatypeURI());
			TestCase.assertEquals("1965-07-31", literal.getLexicalForm());
		}
		pEle.setXhtmlPropertyAttribute("dc:date");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		TestCase.assertEquals("http://purl.org/dc/elements/1.1/date", m1
				.listStatements().nextStatement().getPredicate().getURI());

		parent.removeChild(pEle);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(0, m1.size());
		parent.appendChild(pEle);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		parent.removeChild(pEle);

		TextHElement hEle = contentDom.newOdfElement(TextHElement.class);
		parent.appendChild(hEle);
		hEle.setXhtmlAboutAttribute("[dbpedia:J._K._Rowling]");
		hEle.setXhtmlPropertyAttribute("dbpprop:children");
		hEle.setXhtmlDatatypeAttribute("xsd:integer");
		hEle.setTextContent("2");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		TestCase.assertEquals("http://dbpedia.org/page/J._K._Rowling", m1
				.listStatements().nextStatement().getSubject().getURI());
		TestCase.assertEquals("http://dbpedia.org/property/children", m1
				.listStatements().nextStatement().getPredicate().getURI());
		TestCase.assertTrue(m1.listStatements().nextStatement().getObject()
				.canAs(Literal.class));
		TestCase.assertEquals("2^^http://www.w3.org/2001/XMLSchema#integer", m1
				.listStatements().nextStatement().getObject().toString());
		hEle.setTextContent("3");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals("3^^http://www.w3.org/2001/XMLSchema#integer", m1
				.listStatements().nextStatement().getObject().toString());

		parent.removeChild(hEle);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(0, m1.size());
		parent.appendChild(hEle);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		parent.removeChild(hEle);

		TableTableCellElement ttce = contentDom
				.newOdfElement(TableTableCellElement.class);
		parent.appendChild(ttce);
		ttce.setXhtmlAboutAttribute("[dbpedia:J._K._Rowling]");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(0, m1.size());
        ttce.setXhtmlPropertyAttribute("dbpprop:nationality");
        ttce.setTextContent("British"); // 2DO -- this call is triple wrong:
        /* 2DO
        // a) there is no interrupt to update the RDF model on setTextContent(XX)
        // b) there is no direct text content within a cell, only within a paragraph within the cell
        // c) As there is no direct content and multiple nodes involved for one subject this will not work out on node level

        // Perhaps we should overwrite setTextContent() and throw an exception for now?
        // correct would be to remove all content aside of the 1st paragraph (or create one) and place the text content within..
		*//*
        m1 = odt.getInContentMetadataFromCache();
        // System.err.println("Model is: '" + m1 + "'");
		TestCase.assertEquals(1, m1.size());
		ttce.setXhtmlAboutAttribute("http://dbpedia.org/page/J._R._R._Tolkien");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		TestCase.assertEquals("http://dbpedia.org/page/J._R._R._Tolkien", m1.listStatements().nextStatement().getSubject().getURI());

		TableCoveredTableCellElement tctce = contentDom
				.newOdfElement(TableCoveredTableCellElement.class);
		parent.appendChild(tctce);
		tctce.setXhtmlAboutAttribute("[dbpedia:J._R._R._Tolkien]");
		tctce.setXhtmlPropertyAttribute("dbpprop:shortDescription");
		tctce.setXhtmlContentAttribute("British philologist and author");
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(2, m1.size());

		parent.removeChild(ttce);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		parent.appendChild(ttce);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(2, m1.size());
		parent.removeChild(ttce);

		parent.removeChild(tctce);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(0, m1.size());
		parent.appendChild(tctce);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(1, m1.size());
		parent.removeChild(tctce);
		m1 = odt.getInContentMetadataFromCache();
		TestCase.assertEquals(0, m1.size());

	}*/

	@Test
	public void testGetBookmarkRDFMetadata() throws Exception {
		OdfTextDocument odt = (OdfTextDocument) OdfDocument
				.loadDocument(ResourceUtilities.getAbsoluteInputPath(SIMPLE_ODT));
		Model m = odt.getBookmarkRDFMetadata();
		TestCase.assertEquals(2, m.size());

		OdfContentDom contentDom = odt.getContentDom();
		XPath xpath = contentDom.getXPath();
		TextBookmarkStartElement tm = (TextBookmarkStartElement) xpath
				.evaluate("//text:bookmark-start[last()]", contentDom,
						XPathConstants.NODE);
		BookmarkRDFMetadataExtractor extractor = BookmarkRDFMetadataExtractor
				.newBookmarkTextExtractor();
		m = extractor.getBookmarkRDFMetadata(tm);
		TestCase.assertEquals(1, m.size());
	}
}
