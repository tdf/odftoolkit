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

package org.odftoolkit.simple.text;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.Test;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.common.field.VariableField;
import org.odftoolkit.simple.common.field.VariableField.VariableType;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.odftoolkit.simple.utils.ResourceUtilities;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class HeaderTest {

	String headerDocumentPath = "HeaderTableDocument.odt";

	@Test
	public void testAddTable() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			Assert.assertNotNull(header);

			Table table = header.addTable();
			table.setTableName("headerTable");
			int rowCount = table.getRowCount();
			int columnCount = table.getColumnCount();
			String expectedCellValue = "header table cell";
			table.getCellByPosition(1, 1).setStringValue(expectedCellValue);
			Cell cell = table.getCellByPosition(4, 0);
			cell.setImage(ResourceUtilities.getURI("image_list_item.png"));
			// first page
			header = doc.getHeader(true);
			Assert.assertNotNull(header);

			table = header.addTable();
			table.setTableName("headerHTable");
			doc.save(ResourceUtilities.newTestOutputFile(headerDocumentPath));
			
			// load the document again.
			doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream(headerDocumentPath));
			header = doc.getHeader();
			table = header.getTableByName("headerTable");
			Assert.assertEquals(rowCount, table.getRowCount());
			Assert.assertEquals(columnCount, table.getColumnCount());
			Assert.assertEquals(expectedCellValue, table.getCellByPosition(1, 1).getStringValue());
			cell = table.getCellByPosition(4, 0);
			Assert.assertEquals(34, cell.getBufferedImage().getHeight(null));
			table.getColumnByIndex(4).setWidth(15);
			
			header = doc.getHeader(true);
			table = header.getTableByName("headerHTable");
			Assert.assertNotNull(table);
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testHeaderHidden() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
			Header header = doc.getHeader();
			Assert.assertEquals(true, header.isVisible());
			header.setVisible(false);
			Assert.assertEquals(false, header.isVisible());
			doc.save(ResourceUtilities.newTestOutputFile("headerHiddenOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetOdfElement() {
		try {
			TextDocument doc = TextDocument.loadDocument(ResourceUtilities.getTestResourceAsStream("headerFooterHidden.odt"));
			Header header = doc.getHeader();
			StyleHeaderElement styleheader = header.getOdfElement();
			Assert.assertNotNull(styleheader);
			Assert.assertEquals("header", styleheader.getLocalName());

			//save
			doc.save(ResourceUtilities.newTestOutputFile("headerHiddenOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testGetTableList() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			Table tab = header.addTable();
			
			//validate
			List<Table> listTab = header.getTableList();
			Table tab1 = listTab.get(0);
			Assert.assertNotNull(tab1);
			Assert.assertEquals(tab, tab1);

			//save
			doc.save(ResourceUtilities.newTestOutputFile("headerTableOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testGetTableBuilder() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			
			TableBuilder tabBuilder = header.getTableBuilder();
			Table tab = tabBuilder.newTable();
			Assert.assertNotNull(tab);
			Assert.assertTrue(2 == tab.getRowCount());
			Assert.assertTrue(5 == tab.getColumnCount());
			
			//validate
			List<Table> listTab = header.getTableList();
			Table tab1 = listTab.get(0);
			Assert.assertNotNull(tab1);
			Assert.assertEquals(tab, tab1);

			//save
			doc.save(ResourceUtilities.newTestOutputFile("headerTableOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testGetVariableContainerElement() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			
			OdfElement odfEle = header.getVariableContainerElement();
			
			TableBuilder tb = header.getTableBuilder();
			Table tab = tb.newTable();
			
			Assert.assertNotNull(tab);
			Assert.assertTrue(2 == tab.getRowCount());
			Assert.assertTrue(5 == tab.getColumnCount());
			
			Node nod = odfEle.getFirstChild();
			Assert.assertEquals("table:table", nod.getNodeName());

			//save
			doc.save(ResourceUtilities.newTestOutputFile("headerTableOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testDeclareVariable() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			
			header.declareVariable("headername", VariableType.USER);
			
			//validate
			StyleHeaderElement styleHeader = header.getOdfElement();
			Node nod = styleHeader.getFirstChild().getFirstChild();
			NamedNodeMap nameMap = nod.getAttributes();
			Node nodtext = nameMap.getNamedItem("text:name");
			Assert.assertEquals("headername", nodtext.getNodeValue());

			//save
			doc.save(ResourceUtilities.newTestOutputFile("headerTableOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testGetVariableFieldByName() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			
			header.declareVariable("headername", VariableType.USER);
			VariableField vField = header.getVariableFieldByName("headername");
			String vName = vField.getVariableName();
			
			//validate
			StyleHeaderElement styleHead = header.getOdfElement();
			Node nod = styleHead.getFirstChild().getFirstChild();
			NamedNodeMap nameMap = nod.getAttributes();
			Node nodtext = nameMap.getNamedItem("text:name");
			Assert.assertEquals(vName, nodtext.getNodeValue());

			//save
			doc.save(ResourceUtilities.newTestOutputFile("headerTableOutput.odt"));
		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE, null, e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testAppendNewSection() {
		try {
			TextDocument doc = TextDocument.newTextDocument();
			Header header = doc.getHeader();
			Section sect = header.appendSection("Section1");
			Assert.assertNotNull(sect);

			StyleHeaderElement styleHead = header.getOdfElement();
			Node nod = styleHead.getFirstChild();
			NamedNodeMap nameMap = nod.getAttributes();
			Node nodtext = nameMap.getNamedItem("text:name");
			Assert.assertEquals("Section1", nodtext.getNodeValue());

		} catch (Exception e) {
			Logger.getLogger(HeaderTest.class.getName()).log(Level.SEVERE,
					null, e);
			Assert.fail(e.getMessage());
		}

	}
}
