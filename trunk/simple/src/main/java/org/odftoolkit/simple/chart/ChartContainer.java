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
import java.util.List;

import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.SpreadsheetDocument;

/**
 * ChartContainer is a container which maintains Chart(s) as element(s).
 * Chart(s) can be added, removed and iterated in this container.
 * 
 * @see Chart
 * @see org.odftoolkit.simple.TextDocument
 * @see org.odftoolkit.simple.SpreadsheetDocument
 * @see org.odftoolkit.simple.PresentationDocument
 * @see org.odftoolkit.simple.presentation.Slide
 * 
 * @since 0.6
 */
public interface ChartContainer {

	/**
	 * Creates a new Chart for this container.
	 * 
	 * @param title
	 *            chart title.
	 * @param dataset
	 *            chart data set.
	 * @param rect
	 *            chart rectangle.
	 * @return the created chart.
	 * @since 0.6
	 */
	public Chart createChart(String title, DataSet dataset, Rectangle rect);

	/**
	 * Creates a new Chart for this container.
	 * 
	 * @param title
	 *            chart title.
	 * @param document
	 *            the data source spreadsheet document.
	 * @param cellRangeAddr
	 *            the cell range address list which is used as chart data set.
	 * @param firstRowAsLabel
	 *            whether uses first row as label.
	 * @param firstColumnAsLabel
	 *            whether uses first column as label.
	 * @param rowAsDataSeries
	 *            whether uses data as series.
	 * @param rect
	 *            chart rectangle.
	 * @return the created chart.
	 * @since 0.6
	 */
	public Chart createChart(String title, SpreadsheetDocument document, CellRangeAddressList cellRangeAddr,
			boolean firstRowAsLabel, boolean firstColumnAsLabel, boolean rowAsDataSeries, Rectangle rect);

	/**
	 * Creates a new Chart for this container.
	 * 
	 * @param title
	 *            chart rectangle.
	 * @param labels
	 *            label strings
	 * @param legends
	 *            legend strings
	 * @param data
	 *            chart data set.
	 * @param rect
	 *            chart rectangle.
	 * @return the created chart.
	 * @since 0.6
	 */
	public Chart createChart(String title, String[] labels, String[] legends, double[][] data, Rectangle rect);

	/**
	 * Deletes chart by chart id.
	 * 
	 * @param chartId
	 *            the id of specified chart.
	 * @since 0.6
	 */
	public void deleteChartById(String chartId);

	/**
	 * Deletes chart(s) by chart title.
	 * 
	 * @param title
	 *            the title of specified chart(s).
	 * @since 0.6
	 */
	public void deleteChartByTitle(String title);

	/**
	 * Gets chart with specified id.
	 * 
	 * @param chartId
	 *            the id of this chart.
	 * @return the chart with specified id.
	 * @since 0.6
	 */
	public Chart getChartById(String chartId);

	/**
	 * Gets chart list with specified title.
	 * 
	 * @param title
	 *            the title of specified chart(s).
	 * @return the chart(s) with specified title.
	 * @since 0.6
	 */
	public List<Chart> getChartByTitle(String title);

	/**
	 * Returns the chart count of this container
	 * 
	 * @return the chart count of this container
	 * @since 0.6
	 */
	public int getChartCount();
}
