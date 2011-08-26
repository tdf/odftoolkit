package org.odftoolkit.odfdom.dom.test;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.chart.OdfChart;
import org.odftoolkit.odfdom.doc.element.chart.OdfPlotArea;
import org.odftoolkit.odfdom.doc.element.office.OdfBody;
import org.odftoolkit.odfdom.doc.element.office.OdfDocumentContent;
import org.odftoolkit.odfdom.doc.element.office.OdfPresentation;
import org.odftoolkit.odfdom.doc.element.table.OdfTable;
import org.odftoolkit.odfdom.doc.element.text.OdfHeading;
import org.odftoolkit.odfdom.doc.element.text.OdfList;
import org.odftoolkit.odfdom.doc.element.text.OdfListItem;
import org.odftoolkit.odfdom.doc.element.text.OdfParagraph;
import org.odftoolkit.odfdom.doc.element.text.OdfSoftPageBreak;
import org.odftoolkit.odfdom.dom.OdfNamespace;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.odftoolkit.odfdom.dom.element.chart.OdfChartElement;
import org.odftoolkit.odfdom.dom.element.chart.OdfPlotAreaElement;
import org.odftoolkit.odfdom.dom.element.draw.OdfPageElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.OdfTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.OdfTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.OdfTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.OdfHeadingElement;
import org.odftoolkit.odfdom.dom.element.text.OdfListElement;
import org.odftoolkit.odfdom.dom.element.text.OdfListItemElement;
import org.odftoolkit.odfdom.dom.element.text.OdfParagraphElement;
import org.odftoolkit.odfdom.dom.element.text.OdfSoftPageBreakElement;
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
	
}
