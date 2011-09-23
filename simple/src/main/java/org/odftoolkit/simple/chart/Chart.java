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

import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartClassAttribute;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartDimensionAttribute;
import org.odftoolkit.odfdom.dom.attribute.chart.ChartLegendPositionAttribute;
import org.odftoolkit.odfdom.dom.attribute.dr3d.Dr3dProjectionAttribute;
import org.odftoolkit.odfdom.dom.attribute.dr3d.Dr3dShadeModeAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleLegendExpansionAttribute;
import org.odftoolkit.odfdom.dom.element.chart.ChartAxisElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartCategoriesElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartChartElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartDataPointElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartFloorElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartGridElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartLegendElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartPlotAreaElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartSeriesElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartTitleElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartWallElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dLightElement;
import org.odftoolkit.odfdom.dom.element.style.StyleChartPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.w3c.dom.NodeList;

/**
 * <code>Chart</code> represents the chart feature of the ODF document.
 * <code>Chart</code> provides methods to get/set chart title, get/set chart
 * data, etc.
 * 
 * @since 0.6
 */
public class Chart {

	private final static String AXIS_SVG_X = "3.104cm";
	private final static String AXIS_SVG_Y = "6.359cm";
	private final static String AXIS_FONTSIZE = "10pt";
	private final static String AXIS_FONTSIZEASIAN = "10pt";
	private final static String AXIS_FONTSIZECOMPLEX = "10pt";
	private final static String CHART_SVG_WIDTH = "7cm";
	private final static String CHART_SVG_HEIGH = "8cm";
	private final static String CHART_CLASS_TYPE = "chart:bar";
	private final static String CHART_PROPERTIES_STROKE = "none";
	private final static String PLOTAREA_SVG_HEIGHT = "7.52cm";
	private final static String PLOTAREA_SVG_WIDTH = "4.73cm";
	private final static String PLOTAREA_SVG_X = "0.16cm";
	private final static String PLOTAREA_SVG_Y = "1.375cm";

	private ChartChartElement chartElement;
	private DataSet dataSet;
	private ChartTitleElement chartTitle;
	private ChartLegendElement legend;
	private ChartPlotAreaElement plotArea;
	private TableTableElement table;
	private TableTableHeaderColumnsElement headerColumns;
	private TableTableColumnsElement columns;
	private TableTableColumnElement column;
	private TableTableHeaderRowsElement headerRows;
	private TableTableRowElement headerRow;
	private TableTableRowsElement rows;
	private String chartID;

	private boolean isApply3DEffect = false;
	private boolean isUseLegend = false;

	Chart(ChartChartElement chartElement, String chartID) {
		this.chartElement = chartElement;
		this.chartID = chartID;
		init();
	}

	private void init() {
		chartElement.setSvgWidthAttribute(CHART_SVG_WIDTH);
		chartElement.setSvgHeightAttribute(CHART_SVG_HEIGH);
		chartElement.setChartClassAttribute(CHART_CLASS_TYPE);
		chartElement.setProperty(StyleGraphicPropertiesElement.Stroke, CHART_PROPERTIES_STROKE);
	}

	private void setPlotArea() {
		// chart:plotarea
		NodeList plotAreas = chartElement.getElementsByTagName(ChartPlotAreaElement.ELEMENT_NAME.getQName());
		if (plotAreas.getLength() > 0) {
			chartElement.removeChild(plotAreas.item(0));
		}
		plotArea = (ChartPlotAreaElement) chartElement.newChartPlotAreaElement();

		plotArea = (ChartPlotAreaElement) plotAreas.item(0);
		plotArea.setProperty(StyleChartPropertiesElement.RightAngledAxes, "true");
		plotArea.setSvgHeightAttribute(PLOTAREA_SVG_HEIGHT);
		plotArea.setSvgWidthAttribute(PLOTAREA_SVG_WIDTH);
		plotArea.setSvgXAttribute(PLOTAREA_SVG_X);
		plotArea.setSvgYAttribute(PLOTAREA_SVG_Y);

		// chart:axis
		ChartAxisElement axisX = plotArea.newChartAxisElement(ChartDimensionAttribute.Value.x.toString());
		if (getTableCellRange() != null) {
			ChartCategoriesElement categories = (ChartCategoriesElement) axisX.newChartCategoriesElement();
			categories.setTableCellRangeAddressAttribute(getTableCellRange());
		}
		axisX.setChartDimensionAttribute(ChartDimensionAttribute.Value.x.toString());
		axisX.setChartNameAttribute("primary-x");
		axisX.setProperty(StyleChartPropertiesElement.DisplayLabel, "true");
		axisX.setProperty(StyleChartPropertiesElement.Logarithmic, "false");
		axisX.setProperty(StyleChartPropertiesElement.ReverseDirection, "false");
		axisX.setProperty(StyleChartPropertiesElement.LineBreak, "false");
		axisX.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
		axisX.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
		axisX.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
		axisX.setProperty(StyleTextPropertiesElement.FontCharsetComplex, "10pt");

		ChartAxisElement axisY = (ChartAxisElement) plotArea.newChartAxisElement(ChartDimensionAttribute.Value.y
				.toString());
		axisY.setChartDimensionAttribute(ChartDimensionAttribute.Value.y.toString());
		axisY.setChartNameAttribute("primary-y");
		axisY.setProperty(StyleChartPropertiesElement.DisplayLabel, "true");
		axisY.setProperty(StyleChartPropertiesElement.Logarithmic, "false");
		axisY.setProperty(StyleChartPropertiesElement.ReverseDirection, "false");
		axisY.setProperty(StyleChartPropertiesElement.LineBreak, "false");
		axisY.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
		axisY.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
		axisY.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
		axisY.setProperty(StyleTextPropertiesElement.FontCharsetComplex, "10pt");

		// chart:grid
		ChartGridElement grid = axisY.newChartGridElement();
		grid.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
		grid.setChartClassAttribute(ChartClassAttribute.Value.MAJOR.toString());

		// chart:series
		int numSeries = dataSet.getDataSeriesCount();
		Object[] valueCellRange = getValueCellRange();
		Object[] labelCellRange = getLabelCellRange();

		for (int i = 0; i < numSeries; i++) {
			ChartSeriesElement series = plotArea.newChartSeriesElement();
			series.setChartClassAttribute(chartElement.getChartClassAttribute());
			series.setProperty(StyleGraphicPropertiesElement.Stroke, "solid");
			series.setProperty(StyleGraphicPropertiesElement.FillColor, getRandColorCode());
			series.setProperty(StyleGraphicPropertiesElement.EdgeRounding, "0%");
			series.setProperty(StyleTextPropertiesElement.FontSize, "6pt");
			series.setProperty(StyleTextPropertiesElement.FontSizeAsian, "6pt");
			series.setProperty(StyleTextPropertiesElement.FontCharsetComplex, "6pt");

			if ((String) valueCellRange[i] != null) {
				series.setChartValuesCellRangeAddressAttribute((String) valueCellRange[i]);
			}

			if ((String) labelCellRange[i] != null) {
				series.setChartLabelCellAddressAttribute((String) labelCellRange[i]);
			}

			ChartDataPointElement point = series.newChartDataPointElement();
			point.setChartRepeatedAttribute(new Integer(numSeries));
		}

		ChartWallElement wall = plotArea.newChartWallElement();
		wall.setProperty(StyleGraphicPropertiesElement.Stroke, "none");
		wall.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
		wall.setProperty(StyleGraphicPropertiesElement.Fill, "none");
		wall.setProperty(StyleGraphicPropertiesElement.FillColor, "#e6e6e6");

		ChartFloorElement floor = plotArea.newChartFloorElement();
		floor.setProperty(StyleGraphicPropertiesElement.Stroke, "none");
		floor.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
		floor.setProperty(StyleGraphicPropertiesElement.Fill, "none");
		floor.setProperty(StyleGraphicPropertiesElement.FillColor, "#e6e6e6");
	}

	/**
	 * 3D effect manipulation, get whether the chart apples 3D effect
	 * 
	 * @return return true if the chart is applied 3D effect
	 */
	public boolean IsApply3DEffect() {
		return isApply3DEffect;
	}

	/**
	 * chart axis manipulation, temporarily only consider the axis title
	 * 
	 * @param dimType
	 *            the chart axis dimension, x, y or z
	 * @return return axis title according to the given dimension,null if the
	 *         specific dimensional axis has no title
	 */
	public String getAxisTitle(String dimType) {
		NodeList chartAxises = (NodeList) plotArea.getElementsByTagName(ChartAxisElement.ELEMENT_NAME.getQName());
		for (int i = 0; i < chartAxises.getLength(); i++) {
			ChartAxisElement axis = (ChartAxisElement) chartAxises.item(i);
			String dimension = axis.getAttributeNS(OdfDocumentNamespace.CHART.getUri(), "dimension");
			if (dimension.equals(dimType)) {
				NodeList titles = axis.getElementsByTagName(ChartTitleElement.ELEMENT_NAME.getQName());
				ChartTitleElement axisTitle = (ChartTitleElement) titles.item(0);
				NodeList paras = (NodeList) axisTitle.getElementsByTagName(TextPElement.ELEMENT_NAME.getQName());
				return paras.item(0).getTextContent();
			}
		}
		return null;
	}

	/**
	 * chart data manipulation, get the chart data
	 * 
	 * @return return the chart data
	 */
	public DataSet getChartData() {
		String[] labels;
		String[] legends;
		double[][] values;

		if (dataSet == null) {
			this.dataSet = new DataSet();
			if (table == null) {
				table = (TableTableElement) chartElement
						.getElementsByTagName(TableTableElement.ELEMENT_NAME.getQName()).item(0);
				headerRows = (TableTableHeaderRowsElement) table.getElementsByTagName(
						TableTableHeaderRowsElement.ELEMENT_NAME.getQName()).item(0);
				headerRow = (TableTableRowElement) table.getElementsByTagName(
						TableTableRowElement.ELEMENT_NAME.getQName()).item(0);
				NodeList headerCells = headerRow.getElementsByTagName(TableTableCellElement.ELEMENT_NAME.getQName());

				// column count
				int legendcount = headerCells.getLength() - 1;
				legends = new String[legendcount];
				for (int i = 1; i < legendcount + 1; i++) {
					TableTableCellElement headerCell = (TableTableCellElement) headerCells.item(i);
					legends[i - 1] = ((OdfTextParagraph) headerCell.getElementsByTagName(
							TextPElement.ELEMENT_NAME.getQName()).item(0)).getTextContent();
				}

				rows = (TableTableRowsElement) table
						.getElementsByTagName(TableTableRowsElement.ELEMENT_NAME.getQName()).item(0);
				NodeList row = rows.getElementsByTagName(TableTableRowElement.ELEMENT_NAME.getQName());

				// row count
				int labelcount = row.getLength();
				values = new double[legendcount][labelcount];
				labels = new String[labelcount];
				for (int i = 0; i < labelcount; i++) {
					NodeList cells = ((TableTableRowElement) row.item(i)).getElementsByTagNameNS(
							OdfDocumentNamespace.TABLE.getUri(), "table-cell");
					labels[i] = ((OdfTextParagraph) ((TableTableCellElement) cells.item(0)).getElementsByTagNameNS(
							OdfDocumentNamespace.TEXT.getUri(), "p").item(0)).getTextContent();
					for (int j = 0; j < legendcount; j++) {
						String aValue = ((TableTableCellElement) cells.item(j + 1)).getOdfAttributeValue(OdfName
								.newName(OdfNamespace.newNamespace(OdfDocumentNamespace.OFFICE), "value"));
						if (aValue == null || aValue.equals("")) {
							values[j][i] = Double.NaN;
						} else {
							values[j][i] = Double.valueOf(aValue).doubleValue();
						}
					}
				}
				dataSet.setValues(labels, legends, values);
			}
		}
		return dataSet;
	}

	/**
	 * chart title manipulation, get the current chart title
	 * 
	 * @return return the chart title
	 */
	public String getChartTitle() {
		if (chartTitle == null) {
			chartTitle = (ChartTitleElement) chartElement.getElementsByTagName(
					ChartTitleElement.ELEMENT_NAME.getQName()).item(0);
		}
		NodeList paras = (NodeList) chartTitle.getElementsByTagName(TextPElement.ELEMENT_NAME.getQName());
		if (paras.getLength() > 0) {
			return paras.item(0).getTextContent();
		} else {
			return "";
		}
	}

	/**
	 * chart type manipulation, get the current chart type
	 * 
	 * @return the chart type
	 */
	public ChartType getChartType() {
		return ChartType.enumValueOf(chartElement.getChartClassAttribute());
	}

	/**
	 * chart id manipulation, get the current chart id
	 * 
	 * @return the chart id
	 */
	public String getChartID() {
		return chartID;
	}

	/**
	 * chart legend manipulation
	 * 
	 *@return returns true if it is using legend, otherwise returns false
	 */
	public boolean isUseLegend() {
		return isUseLegend;
	}

	/**
	 * 3D effect manipulation, set to apply 3D effect
	 * 
	 * @param _3deffect
	 *            a flag specifying whether or not apply a 3D effect
	 */
	public void setApply3DEffect(boolean _3deffect) {
		isApply3DEffect = _3deffect;
		if (_3deffect) {
			legend.setProperty(StyleGraphicPropertiesElement.Stroke, "none");
			legend.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
			legend.setProperty(StyleGraphicPropertiesElement.Fill, "none");
			legend.setProperty(StyleGraphicPropertiesElement.FillColor, "#e6e6e6");
			legend.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
			legend.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
			legend.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");

			plotArea.setDr3dVpnAttribute("(0.416199821709347 0.173649045905254 0.892537795986984)");
			plotArea.setDr3dVrpAttribute("(17634.6218373783 10271.4823817647 24594.8639082739)");
			plotArea.setDr3dVupAttribute("(-0.0733876362771618 0.984807599917971 -0.157379306090273)");
			plotArea.setDr3dProjectionAttribute(Dr3dProjectionAttribute.Value.PARALLEL.toString());
			plotArea.setDr3dDistanceAttribute("4.2cm");
			plotArea.setDr3dFocalLengthAttribute("8cm");
			plotArea.setDr3dShadowSlantAttribute("0");
			plotArea.setDr3dShadeModeAttribute(Dr3dShadeModeAttribute.Value.FLAT.toString());
			plotArea.setDr3dAmbientColorAttribute("#999999");
			plotArea.setDr3dLightingModeAttribute("");

			plotArea.setProperty(StyleChartPropertiesElement.ThreeDimensional, "true");
			plotArea.setProperty(StyleChartPropertiesElement.SortByXValues, "false");
			plotArea.setProperty(StyleChartPropertiesElement.RightAngledAxes, "true");

			NodeList chartAxises = (NodeList) plotArea.getElementsByTagName(ChartAxisElement.ELEMENT_NAME.getQName());
			for (int i = 0; i < chartAxises.getLength(); i++) {
				ChartAxisElement axis = (ChartAxisElement) chartAxises.item(i);
				if (axis.getChartDimensionAttribute().equals("x")) {
					// x axis
					axis.setProperty(StyleChartPropertiesElement.DisplayLabel, "true");
					axis.setProperty(StyleChartPropertiesElement.Logarithmic, "false");
					axis.setProperty(StyleChartPropertiesElement.ReverseDirection, "false");
					axis.setProperty(StyleChartPropertiesElement.LineBreak, "false");
					axis.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
					axis.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");
				}
				if (axis.getChartDimensionAttribute().equals("y")) {
					// y axis
					axis.setProperty(StyleTextPropertiesElement.FontSize, "9pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeAsian, "9pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeComplex, "9pt");
				}
				if (axis.getChartDimensionAttribute().equals("z")) {
					// z axis
					axis.setProperty(StyleChartPropertiesElement.DisplayLabel, "true");
					axis.setProperty(StyleChartPropertiesElement.Logarithmic, "false");
					axis.setProperty(StyleChartPropertiesElement.ReverseDirection, "false");
					axis.setProperty(StyleChartPropertiesElement.LineBreak, "false");
					axis.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
					axis.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");
				}
			}

			Dr3dLightElement light1 = plotArea.newDr3dLightElement("");
			light1.setDr3dDiffuseColorAttribute("#b3b3b3");
			light1.setDr3dDirectionAttribute("(0 0 1)");
			light1.setDr3dEnabledAttribute(new Boolean(false));
			light1.setDr3dSpecularAttribute(new Boolean(true));
			Dr3dLightElement light2 = plotArea.newDr3dLightElement("");
			light2.setDr3dDiffuseColorAttribute("#999999");
			light2.setDr3dDirectionAttribute("(-0.2 0.7 0.6)");
			light2.setDr3dEnabledAttribute(new Boolean(true));
			light2.setDr3dSpecularAttribute(new Boolean(false));
			Dr3dLightElement light3 = plotArea.newDr3dLightElement("");
			light3.setDr3dDiffuseColorAttribute("#b3b3b3");
			light3.setDr3dDirectionAttribute("(0 0 1)");
			light3.setDr3dEnabledAttribute(new Boolean(false));
			light3.setDr3dSpecularAttribute(new Boolean(false));
			Dr3dLightElement light4 = plotArea.newDr3dLightElement("");
			light4.setDr3dDiffuseColorAttribute("#b3b3b3");
			light4.setDr3dDirectionAttribute("(0 0 1)");
			light4.setDr3dEnabledAttribute(new Boolean(false));
			light4.setDr3dSpecularAttribute(new Boolean(false));
			Dr3dLightElement light5 = plotArea.newDr3dLightElement("");
			light5.setDr3dDiffuseColorAttribute("#b3b3b3");
			light5.setDr3dDirectionAttribute("(0 0 1)");
			light5.setDr3dEnabledAttribute(new Boolean(false));
			light5.setDr3dSpecularAttribute(new Boolean(false));
			Dr3dLightElement light6 = plotArea.newDr3dLightElement("");
			light6.setDr3dDiffuseColorAttribute("#b3b3b3");
			light6.setDr3dDirectionAttribute("(0 0 1)");
			light6.setDr3dEnabledAttribute(new Boolean(false));
			light6.setDr3dSpecularAttribute(new Boolean(false));
			Dr3dLightElement light7 = plotArea.newDr3dLightElement("");
			light7.setDr3dDiffuseColorAttribute("#b3b3b3");
			light7.setDr3dDirectionAttribute("(0 0 1)");
			light7.setDr3dEnabledAttribute(new Boolean(false));
			light7.setDr3dSpecularAttribute(new Boolean(false));
			Dr3dLightElement light8 = plotArea.newDr3dLightElement("");
			light8.setDr3dDiffuseColorAttribute("#b3b3b3");
			light8.setDr3dDirectionAttribute("(0 0 1)");
			light8.setDr3dEnabledAttribute(new Boolean(false));
			light8.setDr3dSpecularAttribute(new Boolean(false));
		} else {
			NodeList lights = plotArea.getElementsByTagName(Dr3dLightElement.ELEMENT_NAME.getQName());
			for (int i = 0; i < lights.getLength(); i++) {
				plotArea.removeChild(lights.item(i));
			}
			if (lights.getLength() > 0) {
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "vpn");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "vrn");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "vun");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "projection");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "distance");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "focal-length");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "shadow-slant");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "shade-mode");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "ambient-color");
				plotArea.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "lighting-mode");
				plotArea.removeProperty(StyleChartPropertiesElement.ThreeDimensional);
				plotArea.removeProperty(StyleChartPropertiesElement.SortByXValues);
				plotArea.setProperty(StyleChartPropertiesElement.RightAngledAxes, "true");
			}
			NodeList chartAxises = (NodeList) plotArea.getElementsByTagName(ChartAxisElement.ELEMENT_NAME.getQName());
			for (int i = 0; i < chartAxises.getLength(); i++) {
				ChartAxisElement axis = (ChartAxisElement) chartAxises.item(i);
				if (axis.getChartDimensionAttribute().equals("x")) {
					// x axis
					axis.setProperty(StyleChartPropertiesElement.DisplayLabel, "true");
					axis.setProperty(StyleChartPropertiesElement.Logarithmic, "false");
					axis.setProperty(StyleChartPropertiesElement.ReverseDirection, "false");
					axis.setProperty(StyleChartPropertiesElement.LineBreak, "false");
					axis.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
					axis.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");
				}

				if (axis.getChartDimensionAttribute().equals("y")) {
					// y axis
					axis.setProperty(StyleChartPropertiesElement.DisplayLabel, "true");
					axis.setProperty(StyleChartPropertiesElement.Logarithmic, "false");
					axis.setProperty(StyleChartPropertiesElement.ReverseDirection, "false");
					axis.setProperty(StyleChartPropertiesElement.LineBreak, "false");
					axis.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
					axis.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
					axis.setProperty(StyleTextPropertiesElement.FontCharsetComplex, "10pt");
				}

				if (axis.getChartDimensionAttribute().equals("z")) {
					// z axis
					axis.removeProperty(StyleChartPropertiesElement.DisplayLabel);
					axis.removeProperty(StyleChartPropertiesElement.Logarithmic);
					axis.removeProperty(StyleChartPropertiesElement.ReverseDirection);
					axis.removeProperty(StyleChartPropertiesElement.LineBreak);
					axis.removeProperty(StyleGraphicPropertiesElement.StrokeColor);
					axis.removeProperty(StyleTextPropertiesElement.FontSize);
					axis.removeProperty(StyleTextPropertiesElement.FontSizeAsian);
					axis.removeProperty(StyleTextPropertiesElement.FontSizeComplex);
				}
			}
		}
	}

	/**
	 * chart axis manipulation, set the chart axis title according to the give
	 * dimension
	 * 
	 * @param dimType
	 *            the chart axis dimension, x,y, or z
	 * @param title
	 *            the title of axis
	 */
	public void setAxisTitle(String dimType, String title) {
		NodeList chartAxises = (NodeList) plotArea.getElementsByTagName(ChartAxisElement.ELEMENT_NAME.getQName());
		for (int i = 0; i < chartAxises.getLength(); i++) {
			ChartAxisElement axis = (ChartAxisElement) chartAxises.item(i);
			String dimension = axis.getAttributeNS(OdfDocumentNamespace.CHART.getUri(), "dimension");
			if (dimension.equals(dimType)) {
				NodeList titles = axis.getElementsByTagName(ChartTitleElement.ELEMENT_NAME.getQName());
				ChartTitleElement axisTitle;
				if (titles.getLength() == 0) {
					axisTitle = axis.newChartTitleElement();
					if (dimension.equals(ChartDimensionAttribute.Value.x.toString())) {
						axisTitle.setSvgXAttribute(AXIS_SVG_X);
						axisTitle.setSvgYAttribute(AXIS_SVG_Y);
					} else {
						axisTitle.setSvgXAttribute("0.161cm");
						axisTitle.setSvgYAttribute("4.188cm");
					}

					axisTitle.setProperty(StyleTextPropertiesElement.FontSize, AXIS_FONTSIZE);
					axisTitle.setProperty(StyleTextPropertiesElement.FontSizeAsian, AXIS_FONTSIZEASIAN);
					axisTitle.setProperty(StyleTextPropertiesElement.FontSizeComplex, AXIS_FONTSIZECOMPLEX);
				} else {
					axisTitle = (ChartTitleElement) titles.item(0);
				}
				NodeList paras = axisTitle.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
				if (paras.getLength() == 0) {
					axisTitle.newTextPElement().setTextContent(title);
				} else {
					TextPElement para0 = (TextPElement) paras.item(0);
					para0.setTextContent(title);

				}
			}
		}
	}

	/**
	 * chart data manipulation, set chart data
	 * 
	 * @param dataset
	 *            the data set for the chart, which is a 2 dimensional data
	 *            container
	 */
	public void setChartData(DataSet dataset) {
		this.dataSet = dataset;
		boolean isFirstRowAsLabel = dataSet.isFirstRowAsLabel();
		boolean isFirstColumnAsLabel = dataSet.isFirstColumnAsLabel();
		boolean isRowAsSeries = dataSet.isRowAsDataSeries();

		setPlotArea();
		NodeList tables = chartElement.getElementsByTagNameNS(OdfDocumentNamespace.CHART.getUri(), "table");
		if (tables.getLength() > 0) {
			chartElement.removeChild(tables.item(0));
		}

		// judge the cell range or local data
		/*
		 * if (dataset.getCellRangeAddress() != null) {
		 * plotArea.setTableCellRangeAddressAttribute
		 * (dataset.getCellRangeAddress().toString()); if
		 * (dataset.isFirstColumnAsLabel() && dataset.isFirstRowAsLabel())
		 * plotArea
		 * .setChartDataSourceHasLabelsAttribute(ChartDataSourceHasLabelsAttribute
		 * .Value.BOTH.toString()); else if (dataset.isFirstColumnAsLabel())
		 * plotArea
		 * .setChartDataSourceHasLabelsAttribute(ChartDataSourceHasLabelsAttribute
		 * .Value.COLUMN.toString()); else if (dataset.isFirstRowAsLabel())
		 * plotArea
		 * .setChartDataSourceHasLabelsAttribute(ChartDataSourceHasLabelsAttribute
		 * .Value.ROW.toString()); if (dataset.isRowAsDataSeries())
		 * plotArea.setProperty(StyleChartPropertiesElement.SeriesSource,
		 * "rows"); else
		 * plotArea.setProperty(StyleChartPropertiesElement.SeriesSource,
		 * "columns"); }
		 */

		table = chartElement.newTableTableElement();
		headerColumns = table.newTableTableHeaderColumnsElement();
		headerColumns.newTableTableColumnElement();
		columns = table.newTableTableColumnsElement();
		column = columns.newTableTableColumnElement();
		headerRows = table.newTableTableHeaderRowsElement();
		headerRow = headerRows.newTableTableRowElement();
		rows = table.newTableTableRowsElement();

		// create table lable cells
		int numColumns = dataset.getLocalTableFirstRow().length;
		int numRows = dataset.getLocalTableFirstColumn().length;
		column.setTableNumberColumnsRepeatedAttribute(new Integer(numColumns));
		headerRow.newTableTableCellElement(0.0, "string");
		Object[] labelCellRange = getLabelCellRange();
		Object[] valueCellRange = getValueCellRange();
		for (int i = 0; i < numColumns; i++) {
			TableTableCellElement cell = headerRow.newTableTableCellElement(0.0, "string");
			cell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
			String[] cellContents = dataset.getLocalTableFirstRow();
			OdfTextParagraph paragraph = (OdfTextParagraph) cell.newTextPElement();

			if (isFirstRowAsLabel && !isRowAsSeries) {
				if ((String) labelCellRange[i] != null) {
					paragraph.setTextIdAttribute((String) labelCellRange[i]);
				}
			}
			if (isFirstRowAsLabel && isRowAsSeries && i == 0) {
				if ((String) getTableCellRange() != null) {
					paragraph.setTextIdAttribute(getTableCellRange());
				}
			}
			if (cellContents[i] != null) {
				paragraph.setTextContent(cellContents[i]);
			} else {
				paragraph.setTextContent("");
			}
		}

		// create table rows
		Double[][] cellValues = dataset.getLocalTableData();
		for (int i = 0; i < numRows; i++) {
			TableTableRowElement row = rows.newTableTableRowElement();
			TableTableCellElement nameCell = row.newTableTableCellElement(0.0, "string");
			nameCell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
			OdfTextParagraph paragraph = (OdfTextParagraph) nameCell.newTextPElement();
			String[] cellContents = dataset.getLocalTableFirstColumn();
			if (cellContents[i] != null) {
				if (isFirstColumnAsLabel) {
					if (isRowAsSeries) {
						paragraph.setTextIdAttribute((String) labelCellRange[i]);
					}
					if (!isRowAsSeries && i == 0) {
						paragraph.setTextIdAttribute(getTableCellRange());
					}
				}
				paragraph.setTextContent(cellContents[i]);
			} else {
				paragraph.setTextContent("");
			}
			for (int j = 0; j < numColumns; j++) {
				TableTableCellElement cell = row.newTableTableCellElement(0.0, "string");
				cell.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.FLOAT.toString());
				if (cellValues[i][j] != null) {
					cell.setOfficeValueAttribute(cellValues[i][j]);
					OdfTextParagraph paragraph1 = (OdfTextParagraph) cell.newTextPElement();
					paragraph1.setTextContent(cellValues[i][j].toString());
					if (isRowAsSeries && j == 0) {
						paragraph1.setTextIdAttribute((String) valueCellRange[i]);
					}
					if (!isRowAsSeries && i == 0) {
						paragraph1.setTextIdAttribute((String) valueCellRange[j]);
					}
				} else {
					cell.setOfficeValueAttribute(new Double(Double.NaN));
					OdfTextParagraph paragraph1 = (OdfTextParagraph) cell.newTextPElement();
					paragraph1.setTextContent("1.#NAN");
					if (isRowAsSeries && j == 0) {
						paragraph1.setTextIdAttribute((String) valueCellRange[i]);
					}
					if (!isRowAsSeries && i == 0) {
						paragraph1.setTextIdAttribute((String) valueCellRange[j]);
					}
				}
			}
		}
	}

	/**
	 * chart title manipulation, get the current chart title
	 * 
	 * @return return the chart title
	 */
	public boolean setChartTitle(String title) {

		// chart:title
		NodeList titles = chartElement.getElementsByTagNameNS(OdfDocumentNamespace.CHART.getUri(), "title");
		if (titles.getLength() == 0) {
			chartTitle = chartElement.newChartTitleElement();
			chartTitle.setSvgXAttribute("3.669cm");
			chartTitle.setSvgYAttribute("0.141cm");
			chartTitle.setProperty(StyleTextPropertiesElement.FontSize, "12pt");
			chartTitle.setProperty(StyleTextPropertiesElement.FontSizeAsian, "12pt");
			chartTitle.setProperty(StyleTextPropertiesElement.FontSizeComplex, "12pt");
		} else {
			chartTitle = (ChartTitleElement) titles.item(0);
		}
		NodeList paras = chartTitle.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
		if (paras.getLength() == 0) {
			chartTitle.newTextPElement().setTextContent(title);
		} else {
			OdfTextParagraph para0 = (OdfTextParagraph) paras.item(0);
			para0.setTextContent(title);
		}
		return true;
	}

	/**
	 * chart type manipulation, set a chart type when chart type is changed, all
	 * the corresponding chart behaviors and properties should be changed
	 * accordingly
	 * 
	 * @param type
	 *            the type of chart
	 */
	public void setChartType(ChartType type) {

		// set chart:class for chart:chart element
		chartElement.setChartClassAttribute(type.toString());
		// set chart:class for the chart:series
		NodeList plotAreas = chartElement.getElementsByTagName(ChartPlotAreaElement.ELEMENT_NAME.getQName());

		if (plotAreas.getLength() > 0) {
			ChartPlotAreaElement plotArea = (ChartPlotAreaElement) plotAreas.item(0);
			NodeList series = plotArea.getElementsByTagName(ChartSeriesElement.ELEMENT_NAME.getQName());
			for (int i = 0; i < series.getLength(); i++) {
				ChartSeriesElement serie = (ChartSeriesElement) series.item(i);
				serie.setChartClassAttribute(type.toString());
			}
		}
	}

	/**
	 * chart legend manipulation, get whether the chart use legend
	 * 
	 * @param useLegend
	 *            a flag specifying whether or not use legend
	 */
	public void setUseLegend(boolean useLegend) {
		this.isUseLegend = useLegend;
		NodeList legends = chartElement.getElementsByTagNameNS(OdfDocumentNamespace.CHART.getUri(), "legend");
		if (useLegend) {
			if (legends.getLength() == 0) {
				legend = chartElement.newChartLegendElement(ChartLegendPositionAttribute.Value.TOP_END.toString(),
						StyleLegendExpansionAttribute.Value.BALANCED.toString());
				legend.setSvgXAttribute("6.715cm");
				legend.setSvgYAttribute("3.192cm");
				legend.setProperty(StyleGraphicPropertiesElement.Stroke, "none");
				legend.setProperty(StyleGraphicPropertiesElement.StrokeColor, "#b3b3b3");
				legend.setProperty(StyleGraphicPropertiesElement.Fill, "none");
				legend.setProperty(StyleGraphicPropertiesElement.FillColor, "#e6e6e6");
				legend.setProperty(StyleTextPropertiesElement.FontSize, "10pt");
				legend.setProperty(StyleTextPropertiesElement.FontSizeAsian, "10pt");
				legend.setProperty(StyleTextPropertiesElement.FontSizeComplex, "10pt");
			}
		} else {
			if (legends.getLength() > 0) {
				chartElement.removeChild(legends.item(0));
			}
		}

	}

	/**
	 * Refresh chart view and data setting.
	 */
	public void refreshChart() {
		setPlotArea();
		setChartData(dataSet);
	}

	private String getRandColorCode() {
		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;
		return "#" + r + g + b;
	}

	private Object[] getValueCellRange() {
		Vector<String> seriesCellRange = new Vector<String>();
		Vector<String> legendCellAddr = new Vector<String>();
		if (dataSet.isLocalTable()) {
			dataSet.getLocalTableCellRanges(dataSet.getDataSeriesCount(), dataSet.getLabels().length, seriesCellRange,
					legendCellAddr);
		} else {
			dataSet.getCellRanges(dataSet.getCellRangeAddress().toString(), dataSet.isFirstRowAsLabel(), dataSet
					.isFirstColumnAsLabel(), dataSet.isRowAsDataSeries(), seriesCellRange, legendCellAddr);
		}
		return seriesCellRange.toArray();
	}

	private Object[] getLabelCellRange() {
		Vector<String> seriesCellRange = new Vector<String>();
		Vector<String> legendCellAddr = new Vector<String>();
		if (dataSet.isLocalTable()) {
			getLocalTableCellRanges(dataSet.getDataSeriesCount(), dataSet.getLabels().length, seriesCellRange,
					legendCellAddr);
		} else {
			getCellRanges(dataSet.getCellRangeAddress().toString(), dataSet.isFirstRowAsLabel(), dataSet
					.isFirstColumnAsLabel(), dataSet.isRowAsDataSeries(), seriesCellRange, legendCellAddr);
		}
		return legendCellAddr.toArray();
	}

	private String getTableCellRange() {
		Vector<String> seriesCellRange = new Vector<String>();
		Vector<String> legendCellAddr = new Vector<String>();
		String cellRange;
		if (dataSet.isLocalTable()) {
			cellRange = getLocalTableCellRanges(dataSet.getDataSeriesCount(), dataSet.getLabels().length,
					seriesCellRange, legendCellAddr);
		} else {
			cellRange = getCellRanges(dataSet.getCellRangeAddress().toString(), dataSet.isFirstRowAsLabel(), dataSet
					.isFirstColumnAsLabel(), dataSet.isRowAsDataSeries(), seriesCellRange, legendCellAddr);
		}
		return cellRange;
	}

	// return label cell ranges
	private String getCellRanges(String tablecellrange, boolean bFirstRowAsLabel, boolean bFirstColumnAsLabel,
			boolean rowAsDataSeries, Vector<String> seriesCellRange, Vector<String> legendCellAddr) {
		// prepare variables
		String labelCellRange = null;
		if (seriesCellRange == null)
			seriesCellRange = new Vector<String>();
		else
			seriesCellRange.removeAllElements();
		if (legendCellAddr == null)
			legendCellAddr = new Vector<String>();
		else
			legendCellAddr.removeAllElements();

		// seperate column and row from cell range
		StringTokenizer st = new StringTokenizer(tablecellrange, ".:$ ");
		if (st.countTokens() < 3)
			return null;

		String sheettable = st.nextToken();
		String begincell = st.nextToken();
		String endcell = st.nextToken();
		if (st.hasMoreTokens()) {
			endcell = st.nextToken();
		}
		char beginColumn = begincell.charAt(0);
		char endColumn = endcell.charAt(0);
		int beginRow = Integer.parseInt(begincell.substring(1));
		int endRow = Integer.parseInt(endcell.substring(1));

		if (rowAsDataSeries) {
			int starti = beginRow + 1;
			if (!bFirstRowAsLabel) {
				labelCellRange = null;
				starti = beginRow;
			} else if (bFirstColumnAsLabel)
				labelCellRange = createCellRange(sheettable, beginRow, (char) (beginColumn + 1), beginRow, endColumn);
			else
				labelCellRange = createCellRange(sheettable, beginRow, beginColumn, beginRow, endColumn);
			for (int i = starti; i < endRow + 1; i++) {
				if (bFirstColumnAsLabel) {
					seriesCellRange.add(createCellRange(sheettable, i, (char) (beginColumn + 1), i, endColumn));
				} else {
					seriesCellRange.add(createCellRange(sheettable, i, beginColumn, i, endColumn));
				}
				if (bFirstColumnAsLabel) {
					legendCellAddr.add(sheettable + "." + beginColumn + i);
				} else {
					legendCellAddr.add(null);
				}
			}
		} else {
			char startch = (char) (beginColumn + 1);
			if (!bFirstColumnAsLabel) {
				labelCellRange = null;
				startch = beginColumn;
			} else if (bFirstRowAsLabel)
				labelCellRange = createCellRange(sheettable, beginRow + 1, beginColumn, endRow, beginColumn);
			else
				labelCellRange = createCellRange(sheettable, beginRow, beginColumn, endRow, beginColumn);

			for (char ch = startch; ch <= endColumn; ch++) {
				if (bFirstRowAsLabel)
					seriesCellRange.add(createCellRange(sheettable, beginRow + 1, ch, endRow, ch));
				else
					seriesCellRange.add(createCellRange(sheettable, beginRow, ch, endRow, ch));

				if (bFirstRowAsLabel)
					legendCellAddr.add(sheettable + "." + ch + beginRow);
				else
					legendCellAddr.add(null);
			}
		}
		return labelCellRange;
	}

	// return legend cell ranges
	private String getLocalTableCellRanges(int seriesCount, int labelLength, Vector<String> seriesCellRange,
			Vector<String> legendCellAddr) {
		String localtable = "local-table";
		String tablecellrange = localtable + "." + "A1:" + (char) ('A' + seriesCount) + (1 + labelLength);
		return dataSet.getCellRanges(tablecellrange, true, true, false, seriesCellRange, legendCellAddr);
	}

	private String createCellRange(String table, int beginRow, char beginColumn, int endRow, char endColumn) {
		return table + "." + beginColumn + beginRow + ":" + table + "." + endColumn + endRow;
	}
}