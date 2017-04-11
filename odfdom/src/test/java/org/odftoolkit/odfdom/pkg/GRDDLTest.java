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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.logging.Logger;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.InputSource;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.test.ModelTestBase;

import java.util.logging.Level;
import org.junit.Assert;

public class GRDDLTest extends ModelTestBase {

	private static final Logger LOG = Logger.getLogger(GRDDLTest.class
			.getName());
	private static final String SIMPLE_ODT = "test_rdfmeta.odt";

	public GRDDLTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testGRDDL()  {
		try {
			OdfXMLHelper helper = new OdfXMLHelper();
			OdfTextDocument odt = (OdfTextDocument) OdfDocument
					.loadDocument(ResourceUtilities.getAbsolutePath(SIMPLE_ODT));
			InputSource inputSource = new InputSource(ResourceUtilities.getURI(
					"grddl/odf2rdf.xsl").toString());
			Templates multiFileAccessTemplate = TransformerFactory.newInstance()
					.newTemplates(new SAXSource(inputSource));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	 
			helper.transform(odt.getPackage(), "content.xml",
					multiFileAccessTemplate, new StreamResult(out));

			Model m1 = createMemModel();
			m1.read(new InputStreamReader(new ByteArrayInputStream(out
					.toByteArray()), "utf-8"), odt.getPackage().getBaseURI());
			LOG.info("RDF Model:\n" + m1.toString());
			TestCase.assertEquals(5, m1.size());
		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
			Logger.getLogger(GRDDLTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
