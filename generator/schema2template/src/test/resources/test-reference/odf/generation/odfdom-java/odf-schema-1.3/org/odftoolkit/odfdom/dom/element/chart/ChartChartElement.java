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

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.chart;

import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleableShapeElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dSceneElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawAElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCaptionElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCircleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawConnectorElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCustomShapeElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawEllipseElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMeasureElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageThumbnailElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPathElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPolygonElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPolylineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawRectElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawRegularPolygonElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartClassAttribute;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartColumnMappingAttribute;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartRowMappingAttribute;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartStyleNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgHeightAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgWidthAttribute;
import org.odftoolkit.odfdom.dom.attribute.xlink.XlinkHrefAttribute;
import org.odftoolkit.odfdom.dom.attribute.xlink.XlinkTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.xml.XmlIdAttribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element chart:chart}.
 *
 */
public class ChartChartElement extends OdfStylableElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.CHART, "chart");

	/**
	 * Create the instance of <code>ChartChartElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public ChartChartElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME, OdfStyleFamily.Chart, OdfName.newName(OdfDocumentNamespace.CHART, "style-name"));
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element chart:chart}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>ChartClassAttribute</code> , See {@odf.attribute chart:class}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getChartClassAttribute() {
		ChartClassAttribute attr = (ChartClassAttribute) getOdfAttribute(OdfDocumentNamespace.CHART, "class");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>ChartClassAttribute</code> , See {@odf.attribute chart:class}
	 *
	 * @param chartClassValue   The type is <code>String</code>
	 */
	public void setChartClassAttribute(String chartClassValue) {
		ChartClassAttribute attr = new ChartClassAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(chartClassValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>ChartColumnMappingAttribute</code> , See {@odf.attribute chart:column-mapping}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getChartColumnMappingAttribute() {
		ChartColumnMappingAttribute attr = (ChartColumnMappingAttribute) getOdfAttribute(OdfDocumentNamespace.CHART, "column-mapping");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>ChartColumnMappingAttribute</code> , See {@odf.attribute chart:column-mapping}
	 *
	 * @param chartColumnMappingValue   The type is <code>String</code>
	 */
	public void setChartColumnMappingAttribute(String chartColumnMappingValue) {
		ChartColumnMappingAttribute attr = new ChartColumnMappingAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(chartColumnMappingValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>ChartRowMappingAttribute</code> , See {@odf.attribute chart:row-mapping}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getChartRowMappingAttribute() {
		ChartRowMappingAttribute attr = (ChartRowMappingAttribute) getOdfAttribute(OdfDocumentNamespace.CHART, "row-mapping");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>ChartRowMappingAttribute</code> , See {@odf.attribute chart:row-mapping}
	 *
	 * @param chartRowMappingValue   The type is <code>String</code>
	 */
	public void setChartRowMappingAttribute(String chartRowMappingValue) {
		ChartRowMappingAttribute attr = new ChartRowMappingAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(chartRowMappingValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>ChartStyleNameAttribute</code> , See {@odf.attribute chart:style-name}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getChartStyleNameAttribute() {
		ChartStyleNameAttribute attr = (ChartStyleNameAttribute) getOdfAttribute(OdfDocumentNamespace.CHART, "style-name");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>ChartStyleNameAttribute</code> , See {@odf.attribute chart:style-name}
	 *
	 * @param chartStyleNameValue   The type is <code>String</code>
	 */
	public void setChartStyleNameAttribute(String chartStyleNameValue) {
		ChartStyleNameAttribute attr = new ChartStyleNameAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(chartStyleNameValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgHeightAttribute</code> , See {@odf.attribute svg:height}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgHeightAttribute() {
		SvgHeightAttribute attr = (SvgHeightAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "height");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgHeightAttribute</code> , See {@odf.attribute svg:height}
	 *
	 * @param svgHeightValue   The type is <code>String</code>
	 */
	public void setSvgHeightAttribute(String svgHeightValue) {
		SvgHeightAttribute attr = new SvgHeightAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgHeightValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgWidthAttribute</code> , See {@odf.attribute svg:width}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgWidthAttribute() {
		SvgWidthAttribute attr = (SvgWidthAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "width");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgWidthAttribute</code> , See {@odf.attribute svg:width}
	 *
	 * @param svgWidthValue   The type is <code>String</code>
	 */
	public void setSvgWidthAttribute(String svgWidthValue) {
		SvgWidthAttribute attr = new SvgWidthAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgWidthValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>XlinkHrefAttribute</code> , See {@odf.attribute xlink:href}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getXlinkHrefAttribute() {
		XlinkHrefAttribute attr = (XlinkHrefAttribute) getOdfAttribute(OdfDocumentNamespace.XLINK, "href");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>XlinkHrefAttribute</code> , See {@odf.attribute xlink:href}
	 *
	 * @param xlinkHrefValue   The type is <code>String</code>
	 */
	public void setXlinkHrefAttribute(String xlinkHrefValue) {
		XlinkHrefAttribute attr = new XlinkHrefAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(xlinkHrefValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>XlinkTypeAttribute</code> , See {@odf.attribute xlink:type}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getXlinkTypeAttribute() {
		XlinkTypeAttribute attr = (XlinkTypeAttribute) getOdfAttribute(OdfDocumentNamespace.XLINK, "type");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>XlinkTypeAttribute</code> , See {@odf.attribute xlink:type}
	 *
	 * @param xlinkTypeValue   The type is <code>String</code>
	 */
	public void setXlinkTypeAttribute(String xlinkTypeValue) {
		XlinkTypeAttribute attr = new XlinkTypeAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(xlinkTypeValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>XmlIdAttribute</code> , See {@odf.attribute xml:id}
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getXmlIdAttribute() {
		XmlIdAttribute attr = (XmlIdAttribute) getOdfAttribute(OdfDocumentNamespace.XML, "id");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>XmlIdAttribute</code> , See {@odf.attribute xml:id}
	 *
	 * @param xmlIdValue   The type is <code>String</code>
	 */
	public void setXmlIdAttribute(String xmlIdValue) {
		XmlIdAttribute attr = new XmlIdAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(xmlIdValue);
	}

	/**
	 * Create child element {@odf.element chart:footer}.
	 *
	 * @return the element {@odf.element chart:footer}
	 */
	public ChartFooterElement newChartFooterElement() {
		ChartFooterElement chartFooter = ((OdfFileDom) this.ownerDocument).newOdfElement(ChartFooterElement.class);
		this.appendChild(chartFooter);
		return chartFooter;
	}

	/**
	 * Create child element {@odf.element chart:legend}.
	 *
	 * @param chartLegendPositionValue  the <code>String</code> value of <code>ChartLegendPositionAttribute</code>, see {@odf.attribute  chart:legend-position} at specification
	 * @param styleLegendExpansionValue  the <code>String</code> value of <code>StyleLegendExpansionAttribute</code>, see {@odf.attribute  style:legend-expansion} at specification
	 * @return the element {@odf.element chart:legend}
	 */
	 public ChartLegendElement newChartLegendElement(String chartLegendPositionValue, String styleLegendExpansionValue) {
		ChartLegendElement chartLegend = ((OdfFileDom) this.ownerDocument).newOdfElement(ChartLegendElement.class);
		chartLegend.setChartLegendPositionAttribute(chartLegendPositionValue);
		chartLegend.setStyleLegendExpansionAttribute(styleLegendExpansionValue);
		this.appendChild(chartLegend);
		return chartLegend;
	}

	/**
	 * Create child element {@odf.element chart:plot-area}.
	 *
	 * Child element is mandatory.
	 *
	 * @return the element {@odf.element chart:plot-area}
	 */
	public ChartPlotAreaElement newChartPlotAreaElement() {
		ChartPlotAreaElement chartPlotArea = ((OdfFileDom) this.ownerDocument).newOdfElement(ChartPlotAreaElement.class);
		this.appendChild(chartPlotArea);
		return chartPlotArea;
	}

	/**
	 * Create child element {@odf.element chart:subtitle}.
	 *
	 * @return the element {@odf.element chart:subtitle}
	 */
	public ChartSubtitleElement newChartSubtitleElement() {
		ChartSubtitleElement chartSubtitle = ((OdfFileDom) this.ownerDocument).newOdfElement(ChartSubtitleElement.class);
		this.appendChild(chartSubtitle);
		return chartSubtitle;
	}

	/**
	 * Create child element {@odf.element chart:title}.
	 *
	 * @return the element {@odf.element chart:title}
	 */
	public ChartTitleElement newChartTitleElement() {
		ChartTitleElement chartTitle = ((OdfFileDom) this.ownerDocument).newOdfElement(ChartTitleElement.class);
		this.appendChild(chartTitle);
		return chartTitle;
	}

	/**
	 * Create child element {@odf.element dr3d:scene}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element dr3d:scene}
	 */
	public Dr3dSceneElement newDr3dSceneElement() {
		Dr3dSceneElement dr3dScene = ((OdfFileDom) this.ownerDocument).newOdfElement(Dr3dSceneElement.class);
		this.appendChild(dr3dScene);
		return dr3dScene;
	}

	/**
	 * Create child element {@odf.element draw:a}.
	 *
	 * @param xlinkHrefValue  the <code>String</code> value of <code>XlinkHrefAttribute</code>, see {@odf.attribute  xlink:href} at specification
	 * @param xlinkTypeValue  the <code>String</code> value of <code>XlinkTypeAttribute</code>, see {@odf.attribute  xlink:type} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:a}
	 */
	 public DrawAElement newDrawAElement(String xlinkHrefValue, String xlinkTypeValue) {
		DrawAElement drawA = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawAElement.class);
		drawA.setXlinkHrefAttribute(xlinkHrefValue);
		drawA.setXlinkTypeAttribute(xlinkTypeValue);
		this.appendChild(drawA);
		return drawA;
	}

	/**
	 * Create child element {@odf.element draw:caption}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:caption}
	 */
	public DrawCaptionElement newDrawCaptionElement() {
		DrawCaptionElement drawCaption = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawCaptionElement.class);
		this.appendChild(drawCaption);
		return drawCaption;
	}

	/**
	 * Create child element {@odf.element draw:circle}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:circle}
	 */
	public DrawCircleElement newDrawCircleElement() {
		DrawCircleElement drawCircle = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawCircleElement.class);
		this.appendChild(drawCircle);
		return drawCircle;
	}

	/**
	 * Create child element {@odf.element draw:connector}.
	 *
	 * @param svgViewBoxValue  the <code>Integer</code> value of <code>SvgViewBoxAttribute</code>, see {@odf.attribute  svg:viewBox} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:connector}
	 */
	 public DrawConnectorElement newDrawConnectorElement(int svgViewBoxValue) {
		DrawConnectorElement drawConnector = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawConnectorElement.class);
		drawConnector.setSvgViewBoxAttribute(svgViewBoxValue);
		this.appendChild(drawConnector);
		return drawConnector;
	}

	/**
	 * Create child element {@odf.element draw:control}.
	 *
	 * @param drawControlValue  the <code>String</code> value of <code>DrawControlAttribute</code>, see {@odf.attribute  draw:control} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:control}
	 */
	 public DrawControlElement newDrawControlElement(String drawControlValue) {
		DrawControlElement drawControl = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawControlElement.class);
		drawControl.setDrawControlAttribute(drawControlValue);
		this.appendChild(drawControl);
		return drawControl;
	}

	/**
	 * Create child element {@odf.element draw:custom-shape}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:custom-shape}
	 */
	public DrawCustomShapeElement newDrawCustomShapeElement() {
		DrawCustomShapeElement drawCustomShape = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawCustomShapeElement.class);
		this.appendChild(drawCustomShape);
		return drawCustomShape;
	}

	/**
	 * Create child element {@odf.element draw:ellipse}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:ellipse}
	 */
	public DrawEllipseElement newDrawEllipseElement() {
		DrawEllipseElement drawEllipse = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawEllipseElement.class);
		this.appendChild(drawEllipse);
		return drawEllipse;
	}

	/**
	 * Create child element {@odf.element draw:frame}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:frame}
	 */
	public DrawFrameElement newDrawFrameElement() {
		DrawFrameElement drawFrame = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawFrameElement.class);
		this.appendChild(drawFrame);
		return drawFrame;
	}

	/**
	 * Create child element {@odf.element draw:g}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:g}
	 */
	public DrawGElement newDrawGElement() {
		DrawGElement drawG = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawGElement.class);
		this.appendChild(drawG);
		return drawG;
	}

	/**
	 * Create child element {@odf.element draw:line}.
	 *
	 * @param svgX1Value  the <code>String</code> value of <code>SvgX1Attribute</code>, see {@odf.attribute  svg:x1} at specification
	 * @param svgX2Value  the <code>String</code> value of <code>SvgX2Attribute</code>, see {@odf.attribute  svg:x2} at specification
	 * @param svgY1Value  the <code>String</code> value of <code>SvgY1Attribute</code>, see {@odf.attribute  svg:y1} at specification
	 * @param svgY2Value  the <code>String</code> value of <code>SvgY2Attribute</code>, see {@odf.attribute  svg:y2} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:line}
	 */
	 public DrawLineElement newDrawLineElement(String svgX1Value, String svgX2Value, String svgY1Value, String svgY2Value) {
		DrawLineElement drawLine = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawLineElement.class);
		drawLine.setSvgX1Attribute(svgX1Value);
		drawLine.setSvgX2Attribute(svgX2Value);
		drawLine.setSvgY1Attribute(svgY1Value);
		drawLine.setSvgY2Attribute(svgY2Value);
		this.appendChild(drawLine);
		return drawLine;
	}

	/**
	 * Create child element {@odf.element draw:measure}.
	 *
	 * @param svgX1Value  the <code>String</code> value of <code>SvgX1Attribute</code>, see {@odf.attribute  svg:x1} at specification
	 * @param svgX2Value  the <code>String</code> value of <code>SvgX2Attribute</code>, see {@odf.attribute  svg:x2} at specification
	 * @param svgY1Value  the <code>String</code> value of <code>SvgY1Attribute</code>, see {@odf.attribute  svg:y1} at specification
	 * @param svgY2Value  the <code>String</code> value of <code>SvgY2Attribute</code>, see {@odf.attribute  svg:y2} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:measure}
	 */
	 public DrawMeasureElement newDrawMeasureElement(String svgX1Value, String svgX2Value, String svgY1Value, String svgY2Value) {
		DrawMeasureElement drawMeasure = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawMeasureElement.class);
		drawMeasure.setSvgX1Attribute(svgX1Value);
		drawMeasure.setSvgX2Attribute(svgX2Value);
		drawMeasure.setSvgY1Attribute(svgY1Value);
		drawMeasure.setSvgY2Attribute(svgY2Value);
		this.appendChild(drawMeasure);
		return drawMeasure;
	}

	/**
	 * Create child element {@odf.element draw:page-thumbnail}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:page-thumbnail}
	 */
	public DrawPageThumbnailElement newDrawPageThumbnailElement() {
		DrawPageThumbnailElement drawPageThumbnail = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawPageThumbnailElement.class);
		this.appendChild(drawPageThumbnail);
		return drawPageThumbnail;
	}

	/**
	 * Create child element {@odf.element draw:path}.
	 *
	 * @param svgDValue  the <code>String</code> value of <code>SvgDAttribute</code>, see {@odf.attribute  svg:d} at specification
	 * @param svgViewBoxValue  the <code>Integer</code> value of <code>SvgViewBoxAttribute</code>, see {@odf.attribute  svg:viewBox} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:path}
	 */
	 public DrawPathElement newDrawPathElement(String svgDValue, int svgViewBoxValue) {
		DrawPathElement drawPath = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawPathElement.class);
		drawPath.setSvgDAttribute(svgDValue);
		drawPath.setSvgViewBoxAttribute(svgViewBoxValue);
		this.appendChild(drawPath);
		return drawPath;
	}

	/**
	 * Create child element {@odf.element draw:polygon}.
	 *
	 * @param drawPointsValue  the <code>String</code> value of <code>DrawPointsAttribute</code>, see {@odf.attribute  draw:points} at specification
	 * @param svgViewBoxValue  the <code>Integer</code> value of <code>SvgViewBoxAttribute</code>, see {@odf.attribute  svg:viewBox} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:polygon}
	 */
	 public DrawPolygonElement newDrawPolygonElement(String drawPointsValue, int svgViewBoxValue) {
		DrawPolygonElement drawPolygon = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawPolygonElement.class);
		drawPolygon.setDrawPointsAttribute(drawPointsValue);
		drawPolygon.setSvgViewBoxAttribute(svgViewBoxValue);
		this.appendChild(drawPolygon);
		return drawPolygon;
	}

	/**
	 * Create child element {@odf.element draw:polyline}.
	 *
	 * @param drawPointsValue  the <code>String</code> value of <code>DrawPointsAttribute</code>, see {@odf.attribute  draw:points} at specification
	 * @param svgViewBoxValue  the <code>Integer</code> value of <code>SvgViewBoxAttribute</code>, see {@odf.attribute  svg:viewBox} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:polyline}
	 */
	 public DrawPolylineElement newDrawPolylineElement(String drawPointsValue, int svgViewBoxValue) {
		DrawPolylineElement drawPolyline = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawPolylineElement.class);
		drawPolyline.setDrawPointsAttribute(drawPointsValue);
		drawPolyline.setSvgViewBoxAttribute(svgViewBoxValue);
		this.appendChild(drawPolyline);
		return drawPolyline;
	}

	/**
	 * Create child element {@odf.element draw:rect}.
	 *
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:rect}
	 */
	public DrawRectElement newDrawRectElement() {
		DrawRectElement drawRect = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawRectElement.class);
		this.appendChild(drawRect);
		return drawRect;
	}

	/**
	 * Create child element {@odf.element draw:regular-polygon}.
	 *
	 * @param drawConcaveValue  the <code>Boolean</code> value of <code>DrawConcaveAttribute</code>, see {@odf.attribute  draw:concave} at specification
	 * @param drawCornersValue  the <code>Integer</code> value of <code>DrawCornersAttribute</code>, see {@odf.attribute  draw:corners} at specification
	 * Child element was added in ODF 1.3
	 *
	 * @return the element {@odf.element draw:regular-polygon}
	 */
	 public DrawRegularPolygonElement newDrawRegularPolygonElement(boolean drawConcaveValue, int drawCornersValue) {
		DrawRegularPolygonElement drawRegularPolygon = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawRegularPolygonElement.class);
		drawRegularPolygon.setDrawConcaveAttribute(drawConcaveValue);
		drawRegularPolygon.setDrawCornersAttribute(drawCornersValue);
		this.appendChild(drawRegularPolygon);
		return drawRegularPolygon;
	}

	/**
	 * Create child element {@odf.element table:table}.
	 *
	 * @return the element {@odf.element table:table}
	 */
	public TableTableElement newTableTableElement() {
		TableTableElement tableTable = ((OdfFileDom) this.ownerDocument).newOdfElement(TableTableElement.class);
		this.appendChild(tableTable);
		return tableTable;
	}

  /**
   * Accept an visitor instance to allow the visitor to do some operations. Refer to visitor design
   * pattern to get a better understanding.
   *
   * @param visitor an instance of DefaultElementVisitor
   */
	@Override
	public void accept(ElementVisitor visitor) {
		if (visitor instanceof DefaultElementVisitor) {
			DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
			defaultVisitor.visit(this);
		} else {
			visitor.visit(this);
		}
	}
}