package org.odftoolkit.odfdom.dom.test;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.OdfGraphicsDocument;
import org.odftoolkit.odfdom.doc.OdfPresentationDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.element.chart.OdfChart;
import org.odftoolkit.odfdom.doc.element.chart.OdfPlotArea;
import org.odftoolkit.odfdom.doc.element.draw.OdfPage;
import org.odftoolkit.odfdom.doc.element.office.OdfBody;
import org.odftoolkit.odfdom.doc.element.office.OdfDocumentContent;
import org.odftoolkit.odfdom.doc.element.office.OdfPresentation;
import org.odftoolkit.odfdom.doc.element.office.OdfSpreadsheet;
import org.odftoolkit.odfdom.doc.element.office.OdfStyles;
import org.odftoolkit.odfdom.doc.element.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.table.OdfTable;
import org.odftoolkit.odfdom.doc.element.text.OdfHeading;
import org.odftoolkit.odfdom.doc.element.text.OdfList;
import org.odftoolkit.odfdom.doc.element.text.OdfListItem;
import org.odftoolkit.odfdom.doc.element.text.OdfParagraph;
import org.odftoolkit.odfdom.doc.element.text.OdfSoftPageBreak;
import org.odftoolkit.odfdom.dom.OdfName;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.odftoolkit.odfdom.dom.element.anim.OdfAnimateElement;
import org.odftoolkit.odfdom.dom.element.chart.OdfChartElement;
import org.odftoolkit.odfdom.dom.element.chart.OdfPlotAreaElement;
import org.odftoolkit.odfdom.dom.element.draw.OdfLineElement;
import org.odftoolkit.odfdom.dom.element.draw.OdfPageElement;
import org.odftoolkit.odfdom.dom.element.form.OdfFormElement;
import org.odftoolkit.odfdom.dom.element.office.OdfSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.office.OdfTextElement;
import org.odftoolkit.odfdom.dom.element.style.OdfDefaultStyleElement;
import org.odftoolkit.odfdom.dom.element.style.OdfStyleElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.OdfTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.OdfTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.OdfTableElement;
import org.odftoolkit.odfdom.dom.element.table.OdfTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.OdfHeadingElement;
import org.odftoolkit.odfdom.dom.element.text.OdfListElement;
import org.odftoolkit.odfdom.dom.element.text.OdfListItemElement;
import org.odftoolkit.odfdom.dom.element.text.OdfParagraphElement;
import org.odftoolkit.odfdom.dom.element.text.OdfSoftPageBreakElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.NodeList;

public class CreatChildrenElementsTest {
	
	private XPath xpath;

	public CreatChildrenElementsTest() {
		xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new OdfNamespace());
	}

	@Test
	public void testCreatChildrenForPresentation() {
		try {
			
			OdfDocument odfdoc = OdfDocument.loadDocument("test/resources/presentation.odp");
            
            OdfPresentation presentation = OdfElement.findFirstChildNode( OdfPresentation.class, odfdoc.getOfficeBody() );
            Assert.assertNotNull(presentation);
            
            OdfPageElement page = presentation.createPageElement("NewPage");
            

            OdfPageElement presentationTest = (OdfPageElement) xpath.evaluate("//draw:page[last()]", odfdoc.getContentDom() , XPathConstants.NODE);
            
            Assert.assertTrue(presentationTest instanceof OdfPageElement);
            Assert.assertEquals(page,presentationTest);
            Assert.assertEquals(presentationTest.getNodeName(), "draw:page");
            Assert.assertEquals(presentationTest.getMasterPageName(), "NewPage");
			
            odfdoc.save("build/test/CreatChildrenForPresentationTest.odp");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreatChildrenForChart() {
		try {
			
            OdfFileDom doc = OdfDocument.loadDocument("test/resources/empty.odt").getContentDom();
            
            // find the last paragraph
            NodeList lst = doc.getElementsByTagNameNS(
                    OdfParagraph.ELEMENT_NAME.getUri(),
                    OdfParagraph.ELEMENT_NAME.getLocalName());
            Assert.assertTrue(lst.getLength() > 0);
            OdfParagraph p0 = (OdfParagraph) lst.item(lst.getLength() - 1);

            OdfDocumentContent content= (OdfDocumentContent) doc.createOdfElement(OdfDocumentContent.class);
            OdfBody body = (OdfBody)doc.createOdfElement(OdfBody.class);
            content.appendChild(body);
            OdfChart chart = doc.createOdfElement(OdfChart.class);
            //create children element
            OdfPlotAreaElement plotArea = chart.createPlotAreaElement();
            body.appendChild(chart);         
            p0.getParentNode().insertBefore(content, p0);
            
            
            OdfChartElement chartTest = (OdfChartElement) xpath.evaluate("//chart:chart[last()]", doc , XPathConstants.NODE);
            
            Assert.assertNotNull(chartTest.getChildNodes());

            Assert.assertTrue(chartTest.getChildNodes().item(0) instanceof OdfPlotArea);
            Assert.assertEquals(plotArea,chartTest.getChildNodes().item(0));
            Assert.assertEquals(chartTest.getChildNodes().item(0).getNodeName(), "chart:plot-area");
			
            doc.getOdfDocument().save("build/test/CreatChildrenForChartTest.odt");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
    @Test
    public void testCreateChildrenForTable() {
        try {            
            OdfFileDom doc = OdfDocument.loadDocument("test/resources/empty.odt").getContentDom();
            
            // find the last paragraph
            NodeList lst = doc.getElementsByTagNameNS(
                    OdfParagraphElement.ELEMENT_NAME.getUri(),
                    OdfParagraphElement.ELEMENT_NAME.getLocalName());
            Assert.assertTrue(lst.getLength() > 0);
            OdfParagraph p0 = (OdfParagraph) lst.item(lst.getLength() - 1);

            OdfTable table = doc.createOdfElement(OdfTable.class);
            
            
            OdfTableRowElement tr = table.createTableRowElement();
            
            OdfTableCellElement td1 =tr.createTableCellElement();
            
            OdfParagraphElement p1 = td1.createParagraphElement();
            p1.appendChild(doc.createTextNode("content 1"));
 
            p0.getParentNode().insertBefore(table, p0);

            table.setProperty(OdfTablePropertiesElement.Width, "12cm");
            table.setProperty(OdfTablePropertiesElement.Align, "left");

            td1.setProperty(OdfTableColumnPropertiesElement.ColumnWidth, "2cm");
            
            OdfTableRowElement tableRowTest = (OdfTableRowElement) xpath.evaluate("//table:table-row [last()]", doc , XPathConstants.NODE);
            Assert.assertNotNull(tableRowTest.getChildNodes());
            
            Assert.assertTrue(tableRowTest.getChildNodes().item(0) instanceof OdfTableCellElement);
            Assert.assertEquals(tableRowTest.getChildNodes().item(0).getNodeName(), "table:table-cell");
                                    
            doc.getOdfDocument().save("build/test/CreateChildrenForTableTest.odt");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }
    
	@Test
	public void testCreatChildrenForText() {
		try {
			
            OdfFileDom doc = OdfDocument.loadDocument("test/resources/empty.odt").getContentDom();
            
            // find the last paragraph
            NodeList lst = doc.getElementsByTagNameNS(
                    OdfParagraph.ELEMENT_NAME.getUri(),
                    OdfParagraph.ELEMENT_NAME.getLocalName());
            Assert.assertTrue(lst.getLength() > 0);
            OdfParagraph p0 = (OdfParagraph) lst.item(lst.getLength() - 1);

            OdfListItem listItem = doc.createOdfElement(OdfListItem.class);
            //create children elements
            OdfHeadingElement heading = listItem.createHeadingElement(1);
            OdfListElement list = listItem.createListElement();
            OdfParagraphElement paragraph = listItem.createParagraphElement();
            OdfSoftPageBreakElement softPageBreak = listItem.createSoftPageBreakElement();
                       
            p0.getParentNode().insertBefore(listItem, p0);
            
            OdfListItemElement listItemTest = (OdfListItemElement) xpath.evaluate("//text:list-item[last()]", doc , XPathConstants.NODE);
            Assert.assertNotNull(listItemTest.getChildNodes());
            
            Assert.assertTrue(listItemTest.getChildNodes().item(0) instanceof OdfHeading);
            Assert.assertEquals(heading,listItemTest.getChildNodes().item(0));
            Assert.assertEquals(listItemTest.getChildNodes().item(0).getNodeName(), "text:h");
            
            Assert.assertTrue(listItemTest.getChildNodes().item(1) instanceof OdfList);
            Assert.assertEquals(list,listItemTest.getChildNodes().item(1));
            Assert.assertEquals(listItemTest.getChildNodes().item(1).getNodeName(), "text:list");
            
            Assert.assertTrue(listItemTest.getChildNodes().item(2) instanceof OdfParagraph);
            Assert.assertEquals(paragraph,listItemTest.getChildNodes().item(2));
            Assert.assertEquals(listItemTest.getChildNodes().item(2).getNodeName(), "text:p");
            
            Assert.assertTrue(listItemTest.getChildNodes().item(3) instanceof OdfSoftPageBreak);
            Assert.assertEquals(softPageBreak,listItemTest.getChildNodes().item(3));
            Assert.assertEquals(listItemTest.getChildNodes().item(3).getNodeName(), "text:soft-page-break");
                        
       
            doc.getOdfDocument().save("build/test/CreatChildrenForTextTable.odt");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCreatChildrenForGraphic() {
		try {
			
            OdfGraphicsDocument odgDoc1 = OdfGraphicsDocument.createGraphicsDocument();
            OdfFileDom doc = odgDoc1.getContentDom();

            NodeList lst = doc.getElementsByTagNameNS(
                    OdfPage.ELEMENT_NAME.getUri(),
                    OdfPage.ELEMENT_NAME.getLocalName());
            OdfPage page = (OdfPage) lst.item(lst.getLength() - 1);
            page.setOdfAttribute( OdfName.get( OdfNamespace.DRAW, "name" ), "page1" );
            page.setOdfAttribute( OdfName.get( OdfNamespace.DRAW, "style-name" ), "dp1" );
            page.setOdfAttribute( OdfName.get( OdfNamespace.DRAW, "master-page-name" ), "Default" );
            
            OdfLineElement line = page.createLineElement("6cm", "10cm", "15cm", "20cm");
            line.setOdfAttribute( OdfName.get( OdfNamespace.DRAW, "style-name" ), "gr1" );
            line.setOdfAttribute( OdfName.get( OdfNamespace.DRAW, "text-style-name" ), "P1" );
            line.setOdfAttribute( OdfName.get( OdfNamespace.DRAW, "layer" ), "layout" );

            
            
            OdfPageElement graphicTest = (OdfPageElement) xpath.evaluate("//draw:page[last()]", doc , XPathConstants.NODE);
            Assert.assertNotNull(graphicTest.getChildNodes());
            
            Assert.assertTrue(graphicTest.getChildNodes().item(0) instanceof OdfLineElement);
            Assert.assertEquals(line,graphicTest.getChildNodes().item(0));
            Assert.assertEquals(graphicTest.getChildNodes().item(0).getNodeName(), "draw:line");
            
            Assert.assertEquals(((OdfLineElement) graphicTest.getChildNodes().item(0)).getX1(),"6cm");
            Assert.assertEquals(((OdfLineElement) graphicTest.getChildNodes().item(0)).getY1(),"10cm");
            Assert.assertEquals(((OdfLineElement) graphicTest.getChildNodes().item(0)).getX2(),"15cm");
            Assert.assertEquals(((OdfLineElement) graphicTest.getChildNodes().item(0)).getY2(),"20cm");
       
            doc.getOdfDocument().save("build/test/CreatChildrenForGraphic.odg");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
    @Test
    public void testCreatChildrenForStyles() {
        try {
            OdfDocument doc = OdfTextDocument.createTextDocument();

            OdfStyles styles = doc.getOrCreateDocumentStyles();
            OdfDefaultStyleElement def = styles.createDefaultStyleElement();
            def.setFamily(OdfStyleFamily.Paragraph);
            def.setProperty(OdfTextPropertiesElement.TextUnderlineColor, "#00FF00");
            
            OdfStyleElement parent =  styles.createStyleElement("TheParent");
            parent.setFamily(OdfStyleFamily.Paragraph);
            
            parent.setProperty(OdfTextPropertiesElement.FontSize, "17pt");
            parent.setProperty(OdfTextPropertiesElement.Color, "#FF0000");

            OdfStyleElement styleTest = (OdfStyleElement) xpath.evaluate("//style:style[last()]", doc.getStylesDom() , XPathConstants.NODE);
            Assert.assertEquals(styleTest, parent);
            
            doc.getContentDom().getOdfDocument().save("build/test/CreatChildrenForStyles.odt");
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
        }
    }

    @Test
    public void testCreatChildrenForEmbeddedDoc(){
    	try {
			OdfDocument document = OdfTextDocument.createTextDocument();
			document.embedDocument("Object1/", OdfTextDocument.createTextDocument());
			OdfDocument embeddedObject1 = document.getEmbeddedDocument("Object1/");
			OdfFileDom doc = embeddedObject1.getContentDom();
            // find the last paragraph
            NodeList lst = doc.getElementsByTagNameNS(
                    OdfParagraph.ELEMENT_NAME.getUri(),
                    OdfParagraph.ELEMENT_NAME.getLocalName());
            Assert.assertTrue(lst.getLength() > 0);
            OdfParagraph p0 = (OdfParagraph) lst.item(lst.getLength() - 1);

            OdfListItem listItem = doc.createOdfElement(OdfListItem.class);
            //create children elements
            OdfHeadingElement heading = listItem.createHeadingElement(1);
            OdfListElement list = listItem.createListElement();
            OdfParagraphElement paragraph = listItem.createParagraphElement();
            OdfSoftPageBreakElement softPageBreak = listItem.createSoftPageBreakElement();
                       
            p0.getParentNode().insertBefore(listItem, p0);
            
            OdfListItemElement listItemTest = (OdfListItemElement) xpath.evaluate("//text:list-item[last()]", doc , XPathConstants.NODE);
            Assert.assertNotNull(listItemTest.getChildNodes());
            
            Assert.assertTrue(listItemTest.getChildNodes().item(0) instanceof OdfHeading);
            Assert.assertEquals(heading,listItemTest.getChildNodes().item(0));
            Assert.assertEquals(listItemTest.getChildNodes().item(0).getNodeName(), "text:h");
            
            Assert.assertTrue(listItemTest.getChildNodes().item(1) instanceof OdfList);
            Assert.assertEquals(list,listItemTest.getChildNodes().item(1));
            Assert.assertEquals(listItemTest.getChildNodes().item(1).getNodeName(), "text:list");
            
            Assert.assertTrue(listItemTest.getChildNodes().item(2) instanceof OdfParagraph);
            Assert.assertEquals(paragraph,listItemTest.getChildNodes().item(2));
            Assert.assertEquals(listItemTest.getChildNodes().item(2).getNodeName(), "text:p");
            
            Assert.assertTrue(listItemTest.getChildNodes().item(3) instanceof OdfSoftPageBreak);
            Assert.assertEquals(softPageBreak,listItemTest.getChildNodes().item(3));
            Assert.assertEquals(listItemTest.getChildNodes().item(3).getNodeName(), "text:soft-page-break");
                        
       
            doc.getOdfDocument().save("build/test/CreatChildrenForEmbedded.odt");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
	@Test
	public void testCreatChildrenForSpreadSheet() {
		try {
			
			OdfSpreadsheetDocument odfSpreadSheet = OdfSpreadsheetDocument.createSpreadsheetDocument();
            OdfFileDom doc = odfSpreadSheet.getContentDom();

            NodeList lst = doc.getElementsByTagNameNS(
            		OdfSpreadsheetElement.ELEMENT_NAME.getUri(),
            		OdfSpreadsheetElement.ELEMENT_NAME.getLocalName());
            OdfSpreadsheet sheet = (OdfSpreadsheet) lst.item(lst.getLength() - 1);
            OdfTableElement table = sheet.createTableElement();
            table.setOdfAttribute( OdfName.get( OdfNamespace.TABLE, "name" ), "newtable" );
            table.setOdfAttribute( OdfName.get( OdfNamespace.TABLE, "style-name" ), "ta1" );
            OdfTableColumnElement column = table.createTableColumnElement();
            column.setOdfAttribute( OdfName.get( OdfNamespace.TABLE, "style-name" ), "co1" );
            column.setOdfAttribute( OdfName.get( OdfNamespace.TABLE, "default-cell-style-name" ), "Default" );

            
            
            OdfTableElement spreadsheetTest = (OdfTableElement) xpath.evaluate("//table:table[last()]", doc , XPathConstants.NODE);
            Assert.assertNotNull(spreadsheetTest.getChildNodes());
            
            Assert.assertTrue(spreadsheetTest.getChildNodes().item(0) instanceof OdfTableColumnElement);
            Assert.assertEquals(column, spreadsheetTest.getChildNodes().item(0));
            Assert.assertEquals(spreadsheetTest.getChildNodes().item(0).getNodeName(), "table:table-column");
            
            Assert.assertEquals(((OdfTableColumnElement) spreadsheetTest.getChildNodes().item(0)).getAttribute("table:style-name"),"co1");
            Assert.assertEquals(((OdfTableColumnElement) spreadsheetTest.getChildNodes().item(0)).getAttribute("table:default-cell-style-name"),"Default");
       
            doc.getOdfDocument().save("build/test/CreatChildrenForSpreadsheet.ods");

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	   @Test
	    public void testCreatChildrenForForm() {
	        try {
	            OdfDocument doc = OdfTextDocument.createTextDocument();
	            OdfBody body = doc.getOfficeBody();
	  	        OdfTextElement text =  body.createTextElement();
	            OdfFormElement form = text.createFormElement();
	            form.setName("NewFrom");
	            OdfFormElement formTest = (OdfFormElement) xpath.evaluate("//form:form[last()]", doc.getContentDom() , XPathConstants.NODE);
	            Assert.assertEquals(formTest, form);
	            doc.getContentDom().getOdfDocument().save("build/test/CreatChildrenForForm.odt");
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            Assert.fail("Failed with " + e.getClass().getName() + ": '" + e.getMessage() + "'");
	        }
	    }
	   
		@Test
		public void testCreatChildrenForAnimation() {
			try {
				
				OdfDocument odfdoc = OdfPresentationDocument.createPresentationDocument();
	            
	            OdfPresentation presentation = OdfElement.findFirstChildNode( OdfPresentation.class, odfdoc.getOfficeBody() );
	            Assert.assertNotNull(presentation);
	            
	            OdfPageElement page = presentation.createPageElement("NewPage");
	            
	            OdfAnimateElement anim = page.createAnimateElement("new");
	           

	            OdfAnimateElement animTest = (OdfAnimateElement) xpath.evaluate("//anim:animate[last()]", odfdoc.getContentDom() , XPathConstants.NODE);
	            
	            Assert.assertTrue(animTest instanceof OdfAnimateElement);
	            
	            Assert.assertEquals(anim,animTest);
				
	            odfdoc.save("build/test/CreatChildrenForAnimateTest.odp");

			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
		}
}
