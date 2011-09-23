/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.office.OfficeDrawingElement;
import org.odftoolkit.simple.utils.ResourceUtilities;

public class GraphicsDocumentTest {
	private static final Logger LOG = Logger.getLogger(GraphicsDocumentTest.class.getName());
	private static final String TEST_FILE = "graphicTestTemplate.otg";
	
	@Test
	public void testLoadingAChartTemplate() throws Exception {
		try {
			GraphicsDocument graphiDoc = GraphicsDocument.loadDocument(ResourceUtilities.getAbsolutePath(TEST_FILE));
			Assert.assertNotNull(graphiDoc);
			Document.OdfMediaType graphiType = graphiDoc.getOdfMediaType();
			Assert.assertTrue(graphiType.equals(Document.OdfMediaType.GRAPHICS_TEMPLATE));
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}
	
	@Test
	public void testLoadDocumentPath() throws Exception {
		try {
			String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			GraphicsDocument  gdoc = GraphicsDocument.loadDocument(filePath);
			Assert.assertNotNull(gdoc);
			Assert.assertEquals("application/vnd.oasis.opendocument.graphics-template", gdoc.getMediaTypeString());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testLoadDocumentFile() throws Exception {
		try {
			String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			File otg = new File(filePath);
			GraphicsDocument  gdoc = GraphicsDocument.loadDocument(otg);
			Assert.assertNotNull(gdoc);
			Assert.assertEquals("application/vnd.oasis.opendocument.graphics-template", gdoc.getMediaTypeString());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testLoadDocumentStream() throws Exception {
		try {
			String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			File otg = new File(filePath);
			FileInputStream fStream = new FileInputStream(otg);
			GraphicsDocument  gdoc = GraphicsDocument.loadDocument(fStream);
			Assert.assertNotNull(gdoc);
			Assert.assertEquals("application/vnd.oasis.opendocument.graphics-template", gdoc.getMediaTypeString());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testGetContentRoot() throws Exception {
		try {
			String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
			FileInputStream fStream = new FileInputStream(new File(filePath));
			GraphicsDocument  gdoc = GraphicsDocument.loadDocument(fStream);
			Assert.assertNotNull(gdoc);
			OfficeDrawingElement officeEle = gdoc.getContentRoot();
			Assert.assertNotNull(officeEle);
			Assert.assertEquals("office:drawing", officeEle.getNodeName());
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testGetTableContainerElement() throws Exception {
		String filePath = ResourceUtilities.getAbsolutePath(TEST_FILE);
		File otg = new File(filePath);
		FileInputStream fStream = new FileInputStream(otg);
		GraphicsDocument  gdoc = GraphicsDocument.loadDocument(fStream);
		Assert.assertNotNull(gdoc);
		Assert.assertEquals("application/vnd.oasis.opendocument.graphics-template", gdoc.getMediaTypeString());
		try {
			gdoc.getTableContainerElement();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof UnsupportedOperationException);
			Assert.assertEquals("Graphics document is not supported to hold table now.", e.getMessage());
		}
		
	}

}
