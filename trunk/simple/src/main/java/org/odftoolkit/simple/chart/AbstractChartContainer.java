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
package org.odftoolkit.simple.chart;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPathConstants;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.chart.ChartChartElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.ChartDocument;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.w3c.dom.NodeList;

/**
 * AbstractChartContainer is an abstract implementation of the ChartContainer
 * interface, with a default implementation for every method defined in
 * ChartContainer.
 * 
 * @since 0.6
 */
public abstract class AbstractChartContainer implements ChartContainer {
	private Document mDocument;
	private Map<String, Chart> mCharts;
	private String ROOT_STRING = "./";

	/**
	 * The constructor to create a chart container
	 * 
	 * @param doc
	 *            - the onwer document
	 */
	protected AbstractChartContainer(Document doc) {
		try {
			mDocument = doc;
			// init chart container by searching the embeded document
			mCharts = new HashMap<String, Chart>();
			List<Document> charts = mDocument.getEmbeddedDocuments(Document.OdfMediaType.CHART);
			for (Document chartDoc : charts) {
				OdfContentDom contentDom = chartDoc.getContentDom();
				ChartChartElement chartEle = (ChartChartElement) contentDom.getXPath().evaluate("//chart:chart[1]",
						contentDom, XPathConstants.NODE);
				String documentPath = chartDoc.getDocumentPath();
				mCharts.put(documentPath, new Chart(chartEle, documentPath));
			}
		} catch (Exception ex) {
			Logger.getLogger(AbstractChartContainer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns the <draw:frame> element which can contain a chart image object.
	 * 
	 * @return the draw:frame element
	 */
	protected abstract DrawFrameElement getChartFrame() throws Exception;

	public Chart createChart(String title, DataSet dataset, Rectangle rect) {
		try {
			String sName = mDocument.getDocumentPath() + "/" + title + "_" + System.currentTimeMillis();
			// embed this chart document
			ChartDocument newChartDocument = ChartDocument.newChartDocument();
			mDocument.insertDocument(newChartDocument, sName);
			OdfContentDom contentDom = newChartDocument.getContentDom();
			ChartChartElement chartEle = (ChartChartElement) (contentDom.getXPath().evaluate("//chart:chart[1]",
					contentDom, XPathConstants.NODE));
			Chart chart = new Chart(chartEle, sName);
			chart.setChartTitle(title);
			chart.setChartType(ChartType.BAR);
			chart.setUseLegend(true);
			chart.setChartData(dataset);
			DrawFrameElement drawFrame = getChartFrame();
			drawFrame.setProperty(StyleGraphicPropertiesElement.OleDrawAspect, "1");
			drawFrame.setPresentationUserTransformedAttribute(true);
			drawFrame.removeAttributeNS(OdfDocumentNamespace.PRESENTATION.getUri(), "placeholder");
			if (rect != null) {
				drawFrame.setSvgXAttribute(new Integer(rect.x).toString());
				drawFrame.setSvgYAttribute(new Integer(rect.y).toString());
				drawFrame.setSvgWidthAttribute(new Integer(rect.width).toString());
				drawFrame.setSvgHeightAttribute(new Integer(rect.height).toString());
			}
			DrawObjectElement drawObject = OdfElement.findFirstChildNode(DrawObjectElement.class, drawFrame);
			if(drawObject == null){
				drawObject = drawFrame.newDrawObjectElement();
			}
			if (dataset.getCellRangeAddress() != null){
				drawObject.setDrawNotifyOnUpdateOfRangesAttribute(dataset.getCellRangeAddress().toString());
			}
			drawObject.setXlinkHrefAttribute(ROOT_STRING + sName);
			drawObject.setXlinkTypeAttribute("simple");
			drawObject.setXlinkShowAttribute("embed");
			drawObject.setXlinkActuateAttribute("onLoad");
			mCharts.put(sName, chart);
			return chart;
		} catch (Exception ex) {
			Logger.getLogger(AbstractChartContainer.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public Chart createChart(String title, SpreadsheetDocument document, CellRangeAddressList cellRangeAddr,
			boolean firstRowAsLabel, boolean firstColumnAsLabel, boolean rowAsDataSeries, Rectangle rect) {
		DataSet dataset = new DataSet(cellRangeAddr, document, firstRowAsLabel, firstColumnAsLabel, rowAsDataSeries);
		return createChart(title, dataset, rect);
	}

	public Chart createChart(String title, String[] labels, String[] legends, double[][] data, Rectangle rect) {
		DataSet dataset = new DataSet(labels, legends, data);
		return createChart(title, dataset, rect);
	}

	public void deleteChartById(String chartId) {
		try {
			// 1.remove from the container
			mCharts.remove(chartId);
			// 2.remove the embedded document
			mDocument.removeDocument(chartId);
			// 3.remove the draw:frame element in main document
			NodeList frameObjects;
			frameObjects = mDocument.getContentDom().getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(),
					"object");
			for (int i = 0; i < frameObjects.getLength(); i++) {
				DrawObjectElement object = (DrawObjectElement) frameObjects.item(i);
				if (object.getXlinkHrefAttribute().toString().endsWith(chartId)) {
					DrawFrameElement frame = (DrawFrameElement) object.getParentNode();
					frame.getParentNode().removeChild(frame);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(AbstractChartContainer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void deleteChartByTitle(String title) {
		List<String> delChartIDList = new ArrayList<String>();
		Set<String> chartSet = mCharts.keySet();
		for (String chartIter : chartSet) {
			Chart chart = mCharts.get(chartIter);
			if (chart.getChartTitle().equals(title)) {
				delChartIDList.add(chartIter);
			}
		}
		for (int i = 0; i < delChartIDList.size(); i++) {
			deleteChartById(delChartIDList.get(i));
		}
	}

	public Chart getChartById(String chartId) {
		try {
			Document embedChartDoc = mDocument.getEmbeddedDocument(chartId);
			if (embedChartDoc != null) {
				OdfContentDom contentDom = embedChartDoc.getContentDom();
				ChartChartElement chartEle = (ChartChartElement) contentDom.getXPath().evaluate("//chart:chart[1]",
						contentDom, XPathConstants.NODE);
				Chart chart = new Chart(chartEle, chartId);
				if (mCharts.get(chartId) != null) {
					return chart;
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(AbstractChartContainer.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public List<Chart> getChartByTitle(String title) {
		List<Chart> chartList = new ArrayList<Chart>();
		Set<String> chartSet = mCharts.keySet();
		for (String chartIter : chartSet) {
			Chart chart = mCharts.get(chartIter);
			if (chart.getChartTitle().equals(title)) {
				chartList.add(chart);
			}
		}
		return chartList;
	}

	public int getChartCount() {
		int cnt = mDocument.getEmbeddedDocuments(Document.OdfMediaType.CHART).size();
		return cnt;
	}
}
