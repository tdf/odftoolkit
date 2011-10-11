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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.odftoolkit.odfdom.type.CellRangeAddress;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.w3c.dom.NodeList;

/**
 * DataSet is wrapper class for chart data. Generally it is a 2 dimensional data
 * container, a set of chart data series. Each data series has a key, i.e. chart
 * label. The data may be a 2 dimensional double array, or a cell range address
 * of a sheet in Spreadsheet document. DataSet provides some convenient methods
 * for users to manipulate specific data series in the chart data.
 * 
 * @since 0.6
 */

public class DataSet {

	private Vector<Vector<Double>> dataset; // The second vector is a data
											// series.
	private Vector<String> labelset; // label vector,
	private Vector<String> legendset; // legend vector
	// Vector<OdfCellRangeAddressList> seriesCellRange; //cellrange of data
	// series
	// Vector<String> legendCellRange;
	// String cellRangeAddress;
	private CellRangeAddressList cellRangeAddress;
	private boolean bFirstRowAsLabel, bFirstColumnAsLabel, bRowAsDataSeries;
	// Chart chart;
	private boolean isLocalTable;

	/**
	 * Creates a new DataSet without argument.
	 */
	public DataSet() {
		dataset = new Vector<Vector<Double>>();
		labelset = new Vector<String>();
		legendset = new Vector<String>();
		bFirstRowAsLabel = true;
		bFirstColumnAsLabel = true;
		bRowAsDataSeries = false;
		isLocalTable = false;
	}

	/**
	 * Creates a new DataSet.
	 * 
	 * @param labels
	 *            the label strings of this DataSet.
	 * @param legends
	 *            the legend strings of this DataSet.
	 * @param data
	 *            the data of this DataSet, which stores in 2 dimensional double
	 *            array.
	 */
	public DataSet(String[] labels, String[] legends, double[][] data) {
		dataset = new Vector<Vector<Double>>();
		labelset = new Vector<String>();
		legendset = new Vector<String>();
		bFirstRowAsLabel = true;
		bFirstColumnAsLabel = true;
		bRowAsDataSeries = false;
		isLocalTable = true;
		setValues(labels, legends, data);

	}

	/**
	 * Creates a new DataSet.
	 * 
	 * @param cellRangeAddress
	 *            the cell range address, which is used as the data source.
	 * @param spreadsheet
	 *            the data source SpreadsheetDocument.
	 * @param bFirstRowAsLabel
	 *            whether this data set uses first row as chart label.
	 * @param bFirstColumnAsLabel
	 *            whether this data set uses first column as chart label.
	 * @param rowAsDataSeries
	 *            whether this data set uses row as data series.
	 */
	public DataSet(CellRangeAddressList cellRangeAddress, SpreadsheetDocument spreadsheet, boolean bFirstRowAsLabel,
			boolean bFirstColumnAsLabel, boolean rowAsDataSeries) {
		dataset = new Vector<Vector<Double>>();
		labelset = new Vector<String>();
		legendset = new Vector<String>();
		this.bFirstRowAsLabel = bFirstRowAsLabel;
		this.bFirstColumnAsLabel = bFirstColumnAsLabel;
		bRowAsDataSeries = rowAsDataSeries;
		isLocalTable = false;
		setValues(cellRangeAddress, spreadsheet, bFirstRowAsLabel, bFirstColumnAsLabel, rowAsDataSeries);
	}

	// return legend cell ranges
	/**
	 * Gets the local table cell range.
	 * 
	 * @return the local table cell range,
	 */
	public String getLocalTableCellRanges(int seriesCount, int labelLength, Vector<String> seriesCellRange,
			Vector<String> legendCellAddr) {
		String localtable = "local-table";
		String tablecellrange = localtable + "." + "A1:" + (char) ('A' + seriesCount) + (1 + labelLength);
		return getCellRanges(tablecellrange, true, true, false, seriesCellRange, legendCellAddr);
	}

	/**
	 * Gets the cell range address list.
	 * 
	 * @return the cell range address list.
	 */
	public CellRangeAddressList getCellRangeAddress() {
		return cellRangeAddress;
	}

	/**
	 * Adds a data series at the end of current data set.
	 * 
	 * @param legend
	 *            legend for the data series.
	 * @param values
	 *            data series values corresponding to the legend.
	 */
	public void appendDataSeries(String legend, double[] values) {
		legendset.add(legend);

		Vector<Double> valuesList = new Vector<Double>();
		for (int i = 0; i < values.length; i++) {
			valuesList.add(new Double(values[i]));
		}

		dataset.add(valuesList);

	}

	/**
	 * Adds a data series at the index in current data set.
	 * 
	 * @param index
	 *            the index the added data series will be located.
	 * @param legend
	 *            legend for the data series.
	 * @param values
	 *            data series values corresponding to the legend.
	 */
	public void insertDataSeries(int index, String legend, double[] values) {
		legendset.add(index, legend);

		Vector<Double> valuesList = new Vector<Double>();
		for (int i = 0; i < values.length; i++) {
			valuesList.add(new Double(values[i]));
		}
		dataset.add(index, valuesList);

	}

	/**
	 * Updates the data series values according to the index of the data series.
	 * 
	 * @param index
	 *            the index of the data series in the data set.
	 * @param values
	 *            data series values.
	 */
	public void updateDataSeries(int index, double[] values) {
		Vector<Double> valuesList = new Vector<Double>();
		for (int i = 0; i < values.length; i++) {
			valuesList.add(new Double(values[i]));
		}
		dataset.set(index, valuesList);

	}

	/**
	 * Updates the data series values according to the legend of the data
	 * series.
	 * 
	 * @param legend
	 *            the chart legend of the data series.
	 * @param values
	 *            data series values.
	 */
	public void updateDataSeries(String legend, int beginindex, double[] values) {
		Vector<Double> valuesList = new Vector<Double>();
		for (int i = 0; i < values.length; i++) {
			valuesList.add(new Double(values[i]));
		}
		dataset.set(getIndexOfDataSeries(legend, beginindex), valuesList);

	}

	/**
	 * Removes a data series by the index of the data series in the data set.
	 * 
	 * @param index
	 *            index of the data series in the data set.
	 */
	public void removeDataSeries(int index) {
		legendset.remove(index);
		dataset.remove(index);
	}

	/**
	 * Remove a data series by the legend of the data series.
	 * 
	 * @param legend
	 *            the legend of the data series.
	 */
	public void removeDataSeries(String legend, int beginindex) {
		int index = getIndexOfDataSeries(legend, beginindex);
		removeDataSeries(index);
	}

	/**
	 * Gets the index of the data series by its legend.
	 * 
	 * @param legend
	 *            the legend of the data series.
	 * @return index of the data series in the data set.
	 */
	public int getIndexOfDataSeries(String legend, int beginindex) {
		return legendset.indexOf(legend, beginindex);
	}

	/**
	 * Gets the legend of the data series by its index in the data set.
	 * 
	 * @param index
	 *            index of the data series in the data set.
	 * @return the legend of the data series.
	 */
	public String getLegendByIndex(int index) {
		return legendset.get(index);
	}

	/**
	 * Sets or change the legend of the data series by its index in the data
	 * set.
	 * 
	 * @param index
	 *            index of the data series in the data set.
	 * @param legend
	 *            the legend of the data series.
	 */
	public void setLegendByIndex(int index, String legend) {
		legendset.set(index, legend);
	}

	/**
	 * Gets the legend of the data series by its index in the data set.
	 * 
	 * @param index
	 *            index of the data series in the data set.
	 * @return the label of the data series.
	 */
	public String getLabelByIndex(int index) {
		return labelset.get(index);
	}

	/**
	 * Sets or change the legend of the data series by its index in the data
	 * set.
	 * 
	 * @param index
	 *            index of the data series in the data set.
	 * @param label
	 *            the label of the data series.
	 */
	public void setLabelByIndex(int index, String label) {
		labelset.set(index, label);
	}

	/**
	 * Gets the string array of labels.
	 * 
	 * @return the string array of labels.
	 */
	public String[] getLabels() {
		return labelset.toArray(new String[1]);
	}

	/**
	 * Gets the string array of legends.
	 * 
	 * @return the string array of legends.
	 */
	public String[] getLegends() {
		return legendset.toArray(new String[1]);
	}

	/**
	 * Gets whether this data set uses first row as chart label.
	 * 
	 * @return if return <code>true</code>, this data set uses first row as
	 *         chart label.
	 */
	public boolean isFirstRowAsLabel() {
		return bFirstRowAsLabel;
	}

	/**
	 * Gets whether this data set uses first column as chart label.
	 * 
	 * @return if return <code>true</code>, this data set uses first row as
	 *         chart label.
	 */
	public boolean isFirstColumnAsLabel() {
		return bFirstColumnAsLabel;
	}

	/**
	 * Gets whether this data set uses row as data series.
	 * 
	 * @return if return <code>true</code>, this data set uses row as data
	 *         series.
	 */
	public boolean isRowAsDataSeries() {
		return bRowAsDataSeries;
	}

	/**
	 * Gets whether this data set uses local table.
	 * 
	 * @return if return <code>true</code>, this data set uses uses local table.
	 */
	public boolean isLocalTable() {
		return isLocalTable;
	}

	/**
	 * Sets this data set uses local table or not.
	 * 
	 * @param isLocalTable
	 *            if the value is <code>true</code>, this data set uses uses
	 *            local table.
	 */
	public void setLocalTable(boolean isLocalTable) {
		this.isLocalTable = isLocalTable;
	}

	/**
	 * Gets the values of a data series by its index in the data set.
	 * 
	 * @param index
	 *            index of the data series in the data set.
	 * @return the values of the data series.
	 */
	public double[] getDataSeriesByIndex(int index) {
		double[] doubleArray;
		Vector<Double> values = dataset.elementAt(index);
		doubleArray = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			doubleArray[i] = values.get(i).doubleValue();
		return doubleArray;
	}

	/**
	 * Gets the values of a data series by its legend, if multiple data series
	 * have duplicated legends, return the values of the first matched data
	 * series. we do not recommend the duplicated legends, but practically, some
	 * ODF editors allow users to do that.
	 * 
	 * @param legend
	 *            the legend of the data series
	 * @return the values of the data series
	 */
	public double[] getDataSeriesByLegend(String legend, int beginindex) {
		double[] doubleArray;
		Vector<Double> values = dataset.elementAt(getIndexOfDataSeries(legend, beginindex));
		doubleArray = new double[values.size()];
		for (int i = 0; i < values.size(); i++)
			doubleArray[i] = values.get(i).doubleValue();
		return doubleArray;
	}

	/**
	 * Gets the values of a data set as a double dimension array.
	 * 
	 * @return the values of the whole data set.
	 */
	public Double[][] getLocalTableData() {
		// if rowAsDataSeries, the local table data needs to turn 90 degree
		int seriescount, itemcount;
		Double[][] doubleArray;

		seriescount = legendset.size();
		itemcount = labelset.size();
		if (bRowAsDataSeries) {
			doubleArray = new Double[seriescount][itemcount];
		} else {
			doubleArray = new Double[itemcount][seriescount];
		}

		for (int i = 0; i < seriescount; i++) {
			Vector<Double> values = dataset.elementAt(i);
			for (int j = 0; j < itemcount; j++) {
				if (bRowAsDataSeries)
					doubleArray[i][j] = values.get(j);
				else
					doubleArray[j][i] = values.get(j);
			}
		}

		return doubleArray;
	}

	/**
	 * Gets the first row labels of the local table.
	 * 
	 * @return the first row labels of the local table.
	 */
	public String[] getLocalTableFirstRow() {
		if (bRowAsDataSeries)
			return labelset.toArray(new String[1]);
		else
			return legendset.toArray(new String[1]);
	}

	/**
	 * Gets the first column labels of the local table.
	 * 
	 * @return the first column labels of the local table.
	 */
	public String[] getLocalTableFirstColumn() {
		if (bRowAsDataSeries)
			return legendset.toArray(new String[1]);
		else
			return labelset.toArray(new String[1]);
	}

	/**
	 * Gets the data item count of the data series given by the index.
	 * 
	 * @param index
	 *            index of the data series in the data set
	 * @return the data item count of the data series
	 */
	public int getLengthOfDataSeries(int index) {
		Vector<Double> values = dataset.elementAt(index);
		int length = values.size();
		for (int i = values.size() - 1; i >= 0; i--) {
			if (values.elementAt(i) == null)
				length--;
		}
		return length;
	}

	/**
	 * Gets the data item count of the data series given by the label.
	 * 
	 * @param legend
	 *            the legend of the data series
	 * @return the data item count of the data series
	 */
	public int getLengthOfDataSeries(String legend, int beginindex) {
		int index = getIndexOfDataSeries(legend, beginindex);
		return getLengthOfDataSeries(index);
	}

	/**
	 * Gets the maximal data item count
	 * 
	 * @return the maximal data item count
	 */
	public int getMaxLengthOfDataSeries() {
		int max = 0;
		for (int i = 0; i < legendset.size(); i++) {
			int length = getLengthOfDataSeries(i);
			if (max < length)
				max = length;
		}
		return max;
	}

	/**
	 * Gets the count of data series in the data set.
	 * 
	 * @return the count of data series
	 */
	public int getDataSeriesCount() {
		return legendset.size();
	}

	/**
	 * Sets or updates data for the data set with 2 dimensional double array,
	 * the first dimension represents the index of data series, the second
	 * dimension represents the index of data item in each data series.
	 * 
	 * @param labels
	 *            the chart labels, which is corresponding to the first
	 *            dimension of data array.
	 * @param data
	 *            a 2 dimensional double array.
	 */
	public void setValues(String[] labels, String[] legends, double[][] data) {
		this.isLocalTable = true;
		this.bFirstColumnAsLabel = true;
		this.bFirstRowAsLabel = true;
		this.bRowAsDataSeries = false;
		int seriescount = (legends.length <= data.length) ? legends.length : data.length;
		int itemcount = (labels.length <= data[0].length) ? labels.length : data[0].length;
		for (int i = 0; i < seriescount; i++) {
			if (i < legends.length)
				legendset.add(legends[i]);
			else
				legendset.add("" + (i + 1));
			Vector<Double> series = new Vector<Double>();
			for (int j = 0; j < itemcount; j++) {
				if ((i < data.length) && (j < data[0].length))
					series.add(new Double(data[i][j]));
				else
					series.add(null);
			}
			dataset.add(series);
		}
		for (int i = 0; i < itemcount; i++) {
			if (i < labels.length)
				labelset.add(labels[i]);
			else
				labelset.add("");
		}
	}

	/**
	 * Sets data for the data set with cell range address of sheet in
	 * spreadsheet document or internal chart table in other chart containers.
	 * 
	 * @param cellRangeAddress
	 *            cell range address of sheet or table.
	 * @param spreadsheet
	 *            the spreadsheet document instance, the cell address is
	 *            relative to the spreadsheet document.
	 * @param bFirstRowAsLabel
	 *            whether this data set uses first row as chart label.
	 * @param bFirstColumnAsLabel
	 *            whether this data set uses first column as chart label.
	 * @param rowAsDataSeries
	 *            whether this data set uses row as data series.
	 */
	public void setValues(CellRangeAddressList cellRangeAddress, SpreadsheetDocument spreadsheet,
			boolean bFirstRowAsLabel, boolean bFirstColumnAsLabel, boolean rowAsDataSeries) {
		this.isLocalTable = false;
		this.bFirstColumnAsLabel = bFirstColumnAsLabel;
		this.bFirstRowAsLabel = bFirstRowAsLabel;
		this.bRowAsDataSeries = rowAsDataSeries;
		this.cellRangeAddress = cellRangeAddress;

		// analysis cell range
		String cellrange = ((CellRangeAddress) cellRangeAddress.getCellRangesAddressList().get(0)).toString();
		// init variables
		StringTokenizer st = new StringTokenizer(cellrange, ".:$ ");
		if (st.countTokens() < 3)
			return;

		String sheettable = st.nextToken();
		String begincell = st.nextToken();
		String endcell = st.nextToken();
		if (st.hasMoreTokens())
			endcell = st.nextToken();

		char beginColumn = begincell.charAt(0);
		char endColumn = endcell.charAt(0);
		int beginRow = Integer.parseInt(begincell.substring(1));
		int endRow = Integer.parseInt(endcell.substring(1));

		dataset = new Vector<Vector<Double>>();
		Vector<String> rowLabels = new Vector<String>();
		Vector<String> columnLabels = new Vector<String>();

		try {
			int i = 0, rowindex, columnindex;
			char ch;
			TableTableRowElement td;
			TableTableCellElement cell;
			OdfFileDom sheetContent;
			NodeList tableList, rowList, cellList;
			TableTableElement table;
			Vector<Double> series;
			double value;

			// get table
			sheetContent = spreadsheet.getContentDom();
			tableList = sheetContent.getElementsByTagNameNS(OdfNamespace.newNamespace(OdfDocumentNamespace.TABLE)
					.toString(), "table");
			while (!sheettable.equals(((TableTableElement) tableList.item(i)).getTableNameAttribute()))
				i++;
			table = (TableTableElement) tableList.item(i);
			rowList = table.getElementsByTagNameNS(OdfNamespace.newNamespace(OdfDocumentNamespace.TABLE).toString(),
					"table-row");

			// get data, begin for(beginRow,endRow)
			for (i = beginRow; i <= endRow; i++) {
				rowindex = bFirstRowAsLabel ? (i - beginRow - 1) : (i - beginRow);
				td = (TableTableRowElement) rowList.item(i - 1);
				cellList = td.getElementsByTagNameNS(OdfNamespace.newNamespace(OdfDocumentNamespace.TABLE).toString(),
						"table-cell");
				Map<Integer, TableTableCellElement> cellIndexMap = new HashMap<Integer, TableTableCellElement>();
				for (int index = 0, cellNum = 0; (cellNum < cellList.getLength()) && (index <= (endColumn - 'A')); cellNum++) {
					TableTableCellElement item = (TableTableCellElement) cellList.item(cellNum);
					int repeatedCount = item.getTableNumberColumnsRepeatedAttribute()
							* item.getTableNumberColumnsSpannedAttribute();
					int tmpIndex = index + repeatedCount;
					if (tmpIndex >= (beginColumn - 'A')) {
						if ((beginColumn - 'A') > index) {
							index = beginColumn - 'A';
						}
						for (int ii = index; ii < tmpIndex; ii++) {
							cellIndexMap.put(ii, item);
						}
					}
					index = tmpIndex;
				}
				// begin for(beginColumn,endColumn)
				for (ch = beginColumn; ch <= endColumn; ch++) {
					columnindex = bFirstColumnAsLabel ? (ch - beginColumn - 1) : (ch - beginColumn);
					cell = cellIndexMap.get(ch - 'A');
					if (bFirstRowAsLabel && (i == beginRow)) // label row
					{
						if (!bFirstColumnAsLabel || (ch != beginColumn)) {
							if (cell.getFirstChild() != null)
								rowLabels.add(cell.getFirstChild().getTextContent());
							else
								rowLabels.add("Column " + ch);
						}
					} else if (bFirstColumnAsLabel && (ch == beginColumn)) {
						if (!bFirstRowAsLabel || (i != beginRow)) {
							if (cell.getFirstChild() != null)
								columnLabels.add(cell.getFirstChild().getTextContent());
							else
								columnLabels.add("Row " + i);
						}
					} else {
						// set default rowLabel
						if ((i == beginRow) && (!bFirstColumnAsLabel || (ch != beginColumn))) {
							// first row is not label.
							rowLabels.add("Column " + ch);
						}

						// set default column label
						if ((ch == beginColumn) && (!bFirstRowAsLabel || (i != beginRow))) // first
							// column is not label.
							columnLabels.add("Row " + i);
						if (rowAsDataSeries) {
							if (rowindex < dataset.size())
								series = dataset.get(rowindex);
							else {
								series = new Vector<Double>();
								dataset.add(series);
							}
							try {
								value = cell.getOfficeValueAttribute().doubleValue();
								series.add(new Double(value));
							} catch (Exception e) {
								series.add(null);
							}
						} else {
							if (columnindex < dataset.size())
								series = dataset.get(columnindex);
							else {
								series = new Vector<Double>();
								dataset.add(series);
							}
							try {
								value = cell.getOfficeValueAttribute().doubleValue();
								series.add(new Double(value));
							} catch (Exception e) {
								series.add(null);
							}
						}
					}
				}// end begin for(beginColumn,endColumn)
			}// end begin for(beginRow,endRow)
			if (rowAsDataSeries) {
				labelset = rowLabels;
				legendset = columnLabels;
			} else {
				labelset = columnLabels;
				legendset = rowLabels;
			}
		} catch (Exception e) {
			Logger.getLogger(DataSet.class.getName()).log(Level.SEVERE,	e.getMessage(), e);
		}
	}

	// return label cell ranges
	String getCellRanges(String tablecellrange, boolean bFirstRowAsLabel, boolean bFirstColumnAsLabel,
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
		if (st.hasMoreTokens())
			endcell = st.nextToken();

		char beginColumn = begincell.charAt(0);
		char endColumn = endcell.charAt(0);
		int beginRow = Integer.parseInt(begincell.substring(1));
		int endRow = Integer.parseInt(endcell.substring(1));
		// if (bFirstColumnAsLabel) beginColumn=(char)(beginColumn1);
		// if (bFirstRowAsLabel)beginRow=beginRow1;

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
				if (bFirstColumnAsLabel)
					seriesCellRange.add(createCellRange(sheettable, i, (char) (beginColumn + 1), i, endColumn));
				else
					seriesCellRange.add(createCellRange(sheettable, i, beginColumn, i, endColumn));

				if (bFirstColumnAsLabel)
					legendCellAddr.add(sheettable + "." + beginColumn + i);
				else
					legendCellAddr.add(null);
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

	//get cell range string
	private String createCellRange(String table, int beginRow, char beginColumn, int endRow, char endColumn) {
		return table + "." + beginColumn + beginRow + ":" + table + "." + endColumn + endRow;
	}
}