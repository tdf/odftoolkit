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
package org.odftoolkit.odfdom.doc;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.OdfNamespace;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.element.anim.AnimAnimateElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartChartElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartPlotAreaElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentContentElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDefaultStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextSoftPageBreakElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextHeading;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextList;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.w3c.dom.NodeList;

public class CreateChildrenElementsTest {

	private XPath xpath;

	public CreateChildrenElementsTest() {
		xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(OdfNamespace.newNamespace(OdfNamespaceNames.OFFICE));

	}

	@Test
	public void testCreatChildrenForPresentation() {
		try {
			OdfPresentationDocument odfdoc = (OdfPresentationDocument) OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("presentation.odp"));
			OfficePresentationElement presentation = odfdoc.getContentRoot();
			Assert.assertNotNull(presentation);

			DrawPageElement page = presentation.newDrawPageElement("NewPage");


			DrawPageElement presentationTest = (DrawPageElement) xpath.evaluate("//draw:page[last()]", odfdoc.getContentDom(), XPathConstants.NODE);

			Assert.assertTrue(presentationTest instanceof DrawPageElement);
			Assert.assertEquals(page, presentationTest);
			Assert.assertEquals(presentationTest.getNodeName(), "draw:page");
			Assert.assertEquals(presentationTest.getDrawMasterPageNameAttribute(), "NewPage");

			odfdoc.save(ResourceUtilities.newTestOutputFile("CreatChildrenForPresentationTest.odp"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreatChildrenForChart() {
		try {

			OdfFileDom doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("empty.odt")).getContentDom();

			// find the last paragraph
			NodeList lst = doc.getElementsByTagNameNS(
					OdfTextParagraph.ELEMENT_NAME.getUri(),
					OdfTextParagraph.ELEMENT_NAME.getLocalName());
			Assert.assertTrue(lst.getLength() > 0);
			OdfTextParagraph p0 = (OdfTextParagraph) lst.item(lst.getLength() - 1);

			OfficeDocumentContentElement content = doc.newOdfElement(OfficeDocumentContentElement.class);
			OfficeBodyElement body = doc.newOdfElement(OfficeBodyElement.class);
			content.appendChild(body);
			ChartChartElement chart = doc.newOdfElement(ChartChartElement.class);
			//create children element
			ChartPlotAreaElement plotArea = chart.newChartPlotAreaElement();
			body.appendChild(chart);
			p0.getParentNode().insertBefore(content, p0);


			ChartChartElement chartTest = (ChartChartElement) xpath.evaluate("//chart:chart[last()]", doc, XPathConstants.NODE);

			Assert.assertNotNull(chartTest.getChildNodes());

			Assert.assertTrue(chartTest.getChildNodes().item(0) instanceof ChartPlotAreaElement);
			Assert.assertEquals(plotArea, chartTest.getChildNodes().item(0));
			Assert.assertEquals(chartTest.getChildNodes().item(0).getNodeName(), "chart:plot-area");

			doc.getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForChartTest.odt"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreateChildrenForTable() {
		try {
			OdfFileDom doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("empty.odt")).getContentDom();

			// find the last paragraph
			NodeList lst = doc.getElementsByTagNameNS(
					TextPElement.ELEMENT_NAME.getUri(),
					TextPElement.ELEMENT_NAME.getLocalName());
			Assert.assertTrue(lst.getLength() > 0);
			OdfTextParagraph p0 = (OdfTextParagraph) lst.item(lst.getLength() - 1);

			TableTableElement table = doc.newOdfElement(TableTableElement.class);


			TableTableRowElement tr = table.newTableTableRowElement();

			TableTableCellElement td1 = tr.newTableTableCellElement();

			TextPElement p1 = td1.newTextPElement();
			p1.appendChild(doc.createTextNode("content 1"));

			p0.getParentNode().insertBefore(table, p0);

			table.setProperty(StyleTablePropertiesElement.Width, "12cm");
			table.setProperty(StyleTablePropertiesElement.Align, "left");

			td1.setProperty(StyleTableColumnPropertiesElement.ColumnWidth, "2cm");

			TableTableRowElement tableRowTest = (TableTableRowElement) xpath.evaluate("//table:table-row [last()]", doc, XPathConstants.NODE);
			Assert.assertNotNull(tableRowTest.getChildNodes());

			Assert.assertTrue(tableRowTest.getChildNodes().item(0) instanceof TableTableCellElement);
			Assert.assertEquals(tableRowTest.getChildNodes().item(0).getNodeName(), "table:table-cell");

			doc.getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreateChildrenForTableTest.odt"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreatChildrenForText() {
		try {

			OdfFileDom doc = OdfDocument.loadDocument(ResourceUtilities.getAbsolutePath("empty.odt")).getContentDom();

			// find the last paragraph
			NodeList lst = doc.getElementsByTagNameNS(
					OdfTextParagraph.ELEMENT_NAME.getUri(),
					OdfTextParagraph.ELEMENT_NAME.getLocalName());
			Assert.assertTrue(lst.getLength() > 0);
			OdfTextParagraph p0 = (OdfTextParagraph) lst.item(lst.getLength() - 1);

			TextListItemElement listItem = doc.newOdfElement(TextListItemElement.class);
			//create children elements
			TextHElement heading = listItem.newTextHElement("1");
			TextListElement list = listItem.newTextListElement();
			TextPElement paragraph = listItem.newTextPElement();
			TextSoftPageBreakElement softPageBreak = listItem.newTextSoftPageBreakElement();

			p0.getParentNode().insertBefore(listItem, p0);

			TextListItemElement listItemTest = (TextListItemElement) xpath.evaluate("//text:list-item[last()]", doc, XPathConstants.NODE);
			Assert.assertNotNull(listItemTest.getChildNodes());

			Assert.assertTrue(listItemTest.getChildNodes().item(0) instanceof OdfTextHeading);
			Assert.assertEquals(heading, listItemTest.getChildNodes().item(0));
			Assert.assertEquals(listItemTest.getChildNodes().item(0).getNodeName(), "text:h");

			Assert.assertTrue(listItemTest.getChildNodes().item(1) instanceof OdfTextList);
			Assert.assertEquals(list, listItemTest.getChildNodes().item(1));
			Assert.assertEquals(listItemTest.getChildNodes().item(1).getNodeName(), "text:list");

			Assert.assertTrue(listItemTest.getChildNodes().item(2) instanceof OdfTextParagraph);
			Assert.assertEquals(paragraph, listItemTest.getChildNodes().item(2));
			Assert.assertEquals(listItemTest.getChildNodes().item(2).getNodeName(), "text:p");

			Assert.assertTrue(listItemTest.getChildNodes().item(3) instanceof TextSoftPageBreakElement);
			Assert.assertEquals(softPageBreak, listItemTest.getChildNodes().item(3));
			Assert.assertEquals(listItemTest.getChildNodes().item(3).getNodeName(), "text:soft-page-break");


			doc.getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForTextTable.odt"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreatChildrenForGraphic() {
		try {

			OdfGraphicsDocument odgDoc1 = OdfGraphicsDocument.newGraphicsDocument();
			OdfFileDom doc = odgDoc1.getContentDom();

			NodeList lst = doc.getElementsByTagNameNS(
					DrawPageElement.ELEMENT_NAME.getUri(),
					DrawPageElement.ELEMENT_NAME.getLocalName());
			DrawPageElement page = (DrawPageElement) lst.item(lst.getLength() - 1);
			//page.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.DRAW), "name" ), "page1" );
			//page.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.DRAW), "style-name" ), "dp1" );
			//page.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.DRAW), "master-page-name" ), "Default" );
			page.setDrawNameAttribute("page1");
			page.setDrawStyleNameAttribute("dp1");
			page.setDrawMasterPageNameAttribute("Default");

			DrawLineElement line = page.newDrawLineElement("6cm", "10cm", "15cm", "20cm");
			//line.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.DRAW), "style-name" ), "gr1" );
			//line.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.DRAW), "text-style-name" ), "P1" );
			//line.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.DRAW), "layer" ), "layout" );
			line.setDrawStyleNameAttribute("gr1");
			line.setDrawTextStyleNameAttribute("P1");
			line.setDrawLayerAttribute("layer");

			DrawPageElement graphicTest = (DrawPageElement) xpath.evaluate("//draw:page[last()]", doc, XPathConstants.NODE);
			Assert.assertNotNull(graphicTest.getChildNodes());

			Assert.assertTrue(graphicTest.getChildNodes().item(0) instanceof DrawLineElement);
			Assert.assertEquals(line, graphicTest.getChildNodes().item(0));
			Assert.assertEquals(graphicTest.getChildNodes().item(0).getNodeName(), "draw:line");

			Assert.assertEquals(((DrawLineElement) graphicTest.getChildNodes().item(0)).getSvgX1Attribute().toString(), "6cm");
			Assert.assertEquals(((DrawLineElement) graphicTest.getChildNodes().item(0)).getSvgX2Attribute().toString(), "10cm");
			Assert.assertEquals(((DrawLineElement) graphicTest.getChildNodes().item(0)).getSvgY1Attribute().toString(), "15cm");
			Assert.assertEquals(((DrawLineElement) graphicTest.getChildNodes().item(0)).getSvgY2Attribute().toString(), "20cm");

			doc.getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForGraphic.odg"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreatChildrenForStyles() {
		try {
			OdfDocument doc = OdfTextDocument.newTextDocument();

			OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
			StyleDefaultStyleElement def = styles.newStyleDefaultStyleElement();
			def.setStyleFamilyAttribute(OdfStyleFamily.Paragraph.toString());
			def.setProperty(StyleTextPropertiesElement.TextUnderlineColor, "#00FF00");

			StyleStyleElement parent = styles.newStyleStyleElement("TheParent");
			parent.setStyleFamilyAttribute(OdfStyleFamily.Paragraph.toString());

			parent.setProperty(StyleTextPropertiesElement.FontSize, "17pt");
			parent.setProperty(StyleTextPropertiesElement.Color, "#FF0000");

			StyleStyleElement styleTest = (StyleStyleElement) xpath.evaluate("//style:style[last()]", doc.getStylesDom(), XPathConstants.NODE);
			Assert.assertEquals(styleTest, parent);

			doc.getContentDom().getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForStyles.odt"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreatChildrenForEmbeddedDoc() {
		try {
			OdfDocument document = OdfTextDocument.newTextDocument();
			document.insertDocument(OdfTextDocument.newTextDocument(), "Object1/");
			OdfDocument embeddedObject1 = (OdfDocument) document.getEmbeddedDocument("Object1/");
			OdfFileDom doc = embeddedObject1.getContentDom();
			// find the last paragraph
			NodeList lst = doc.getElementsByTagNameNS(
					OdfTextParagraph.ELEMENT_NAME.getUri(),
					OdfTextParagraph.ELEMENT_NAME.getLocalName());
			Assert.assertTrue(lst.getLength() > 0);
			OdfTextParagraph p0 = (OdfTextParagraph) lst.item(lst.getLength() - 1);

			TextListItemElement listItem = doc.newOdfElement(TextListItemElement.class);
			//create children elements
			TextHElement heading = listItem.newTextHElement("1");
			TextListElement list = listItem.newTextListElement();
			TextPElement paragraph = listItem.newTextPElement();
			TextSoftPageBreakElement softPageBreak = listItem.newTextSoftPageBreakElement();

			p0.getParentNode().insertBefore(listItem, p0);

			TextListItemElement listItemTest = (TextListItemElement) xpath.evaluate("//text:list-item[last()]", doc, XPathConstants.NODE);
			Assert.assertNotNull(listItemTest.getChildNodes());

			Assert.assertTrue(listItemTest.getChildNodes().item(0) instanceof OdfTextHeading);
			Assert.assertEquals(heading, listItemTest.getChildNodes().item(0));
			Assert.assertEquals(listItemTest.getChildNodes().item(0).getNodeName(), "text:h");

			Assert.assertTrue(listItemTest.getChildNodes().item(1) instanceof OdfTextList);
			Assert.assertEquals(list, listItemTest.getChildNodes().item(1));
			Assert.assertEquals(listItemTest.getChildNodes().item(1).getNodeName(), "text:list");

			Assert.assertTrue(listItemTest.getChildNodes().item(2) instanceof OdfTextParagraph);
			Assert.assertEquals(paragraph, listItemTest.getChildNodes().item(2));
			Assert.assertEquals(listItemTest.getChildNodes().item(2).getNodeName(), "text:p");

			Assert.assertTrue(listItemTest.getChildNodes().item(3) instanceof TextSoftPageBreakElement);
			Assert.assertEquals(softPageBreak, listItemTest.getChildNodes().item(3));
			Assert.assertEquals(listItemTest.getChildNodes().item(3).getNodeName(), "text:soft-page-break");


			doc.getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForEmbedded.odt"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testCreatChildrenForSpreadSheet() {
		try {

			OdfSpreadsheetDocument odfSpreadSheet = OdfSpreadsheetDocument.newSpreadsheetDocument();
			OdfFileDom doc = odfSpreadSheet.getContentDom();

			NodeList lst = doc.getElementsByTagNameNS(
					OfficeSpreadsheetElement.ELEMENT_NAME.getUri(),
					OfficeSpreadsheetElement.ELEMENT_NAME.getLocalName());
			OfficeSpreadsheetElement sheet = (OfficeSpreadsheetElement) lst.item(lst.getLength() - 1);
			TableTableElement table = sheet.newTableTableElement();
			//table.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.TABLE), "name" ), "newtable" );
			//table.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.TABLE), "style-name" ), "ta1" );
			table.setTableNameAttribute("newtable");
			table.setTableStyleNameAttribute("ta1");
			TableTableColumnElement column = table.newTableTableColumnElement();
			//column.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.TABLE), "style-name" ), "co1" );
			//column.setOdfAttribute( OdfName.newName( OdfNamespace.newName(OdfNamespaceNames.TABLE), "default-cell-style-name" ), "Default" );
			column.setTableStyleNameAttribute("co1");
			column.setTableDefaultCellStyleNameAttribute("Default");

			TableTableElement spreadsheetTest = (TableTableElement) xpath.evaluate("//table:table[last()]", doc, XPathConstants.NODE);
			Assert.assertNotNull(spreadsheetTest.getChildNodes());

			Assert.assertTrue(spreadsheetTest.getChildNodes().item(0) instanceof TableTableColumnElement);
			Assert.assertEquals(column, spreadsheetTest.getChildNodes().item(0));
			Assert.assertEquals(spreadsheetTest.getChildNodes().item(0).getNodeName(), "table:table-column");

			Assert.assertEquals(((TableTableColumnElement) spreadsheetTest.getChildNodes().item(0)).getAttribute("table:style-name"), "co1");
			Assert.assertEquals(((TableTableColumnElement) spreadsheetTest.getChildNodes().item(0)).getAttribute("table:default-cell-style-name"), "Default");

			doc.getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForSpreadsheet.ods"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreatChildrenForForm() {
		try {
			OdfTextDocument doc = OdfTextDocument.newTextDocument();
			OfficeTextElement text = doc.getContentRoot();
			FormFormElement form = text.newFormFormElement();
			form.setFormNameAttribute("NewFrom");
			FormFormElement formTest = (FormFormElement) xpath.evaluate("//form:form[last()]", doc.getContentDom(), XPathConstants.NODE);
			Assert.assertEquals(formTest, form);
			doc.getContentDom().getOdfDocument().save(ResourceUtilities.newTestOutputFile("CreatChildrenForForm.odt"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
		}
	}

	@Test
	public void testCreatChildrenForAnimation() {
		try {
			OdfPresentationDocument odfdoc = OdfPresentationDocument.newPresentationDocument();
			OfficePresentationElement presentation = odfdoc.getContentRoot();
			Assert.assertNotNull(presentation);

			DrawPageElement page = presentation.newDrawPageElement("NewPage");

			AnimAnimateElement anim = page.newAnimAnimateElement("new");


			AnimAnimateElement animTest = (AnimAnimateElement) xpath.evaluate("//anim:animate[last()]", odfdoc.getContentDom(), XPathConstants.NODE);

			Assert.assertTrue(animTest instanceof AnimAnimateElement);

			Assert.assertEquals(anim, animTest);

			odfdoc.save(ResourceUtilities.newTestOutputFile("CreatChildrenForAnimateTest.odp"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
