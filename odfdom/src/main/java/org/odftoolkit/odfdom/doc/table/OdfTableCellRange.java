/************************************************************************
*
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.doc.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;

/**
 * OdfTableCellRange represent a rang of cells that are adjacent with each other
 * <p>
 * OdfTableCellRange provides methods to get/set/modify the properties of cell range.
 *
 */
public class OdfTableCellRange {

	private int mnStartRow;
	private int mnStartColumn;
	private int mnEndRow;
	private int mnEndColumn;
	private String msCellRangeName;
	private OdfTable maOwnerTable;
	private boolean mbSpreadsheet;

	/**
	 * Construct the instance of OdfTableCellRange.
	 * @param table
	 * 					is the container table of this cell range.
	 * @param startColumn
	 * 					is the column index of the first cell in this cell range.
	 * @param startRow
	 * 					is the row index of the first cell in this cell range.
	 * @param endColumn
	 * 					is the column index of the last cell in this cell range.
	 * @param endRow
	 * 					is the row index of the last cell in this cell range.
	 */
	OdfTableCellRange(OdfTable table, int startColumn, int startRow, int endColumn, int endRow) {
		maOwnerTable = table;

		OdfDocument doc = (OdfDocument) ((OdfFileDom) maOwnerTable.getOdfElement().getOwnerDocument()).getDocument();
		if (doc instanceof OdfSpreadsheetDocument) {
			mbSpreadsheet = true;
		}

		//the first cell is the covered cell, then the cell range should be enlarged
		//so that it can contains the complete cell
		//get the cell cover info
		mnStartColumn = startColumn;
		mnStartRow = startRow;
		mnEndColumn = endColumn;
		mnEndRow = endRow;
		List<CellCoverInfo> coverList = maOwnerTable.getCellCoverInfos(0, 0, maOwnerTable.getColumnCount() - 1, maOwnerTable.getRowCount() - 1);
		OdfTableCell cell;// = maOwnerTable.getOwnerCellByPosition(coverList, nStartColumn, nStartRow);
		for (int i = startColumn; i <= endColumn; i++) {
			cell = maOwnerTable.getOwnerCellByPosition(coverList, i, startRow);
			int rowIndex = cell.getRowIndex();
			int colIndex = cell.getColumnIndex();
			mnStartColumn = Math.min(mnStartColumn, colIndex);
			mnStartRow = Math.min(mnStartRow, rowIndex);
			mnEndColumn = Math.max(mnEndColumn, colIndex + cell.getColumnSpannedNumber() - 1);
			mnEndRow = Math.max(mnEndRow, rowIndex + cell.getRowSpannedNumber() - 1);
		}

		for (int i = startColumn; i <= endColumn; i++) {
			cell = maOwnerTable.getOwnerCellByPosition(coverList, i, endRow);
			int rowIndex = cell.getRowIndex();
			int colIndex = cell.getColumnIndex();
			mnStartColumn = Math.min(mnStartColumn, colIndex);
			mnStartRow = Math.min(mnStartRow, rowIndex);
			mnEndColumn = Math.max(mnEndColumn, colIndex + cell.getColumnSpannedNumber() - 1);
			mnEndRow = Math.max(mnEndRow, rowIndex + cell.getRowSpannedNumber() - 1);
		}

		for (int i = startRow + 1; i < endRow; i++) {
			cell = maOwnerTable.getOwnerCellByPosition(coverList, startColumn, i);
			int rowIndex = cell.getRowIndex();
			int colIndex = cell.getColumnIndex();
			mnStartColumn = Math.min(mnStartColumn, colIndex);
			mnStartRow = Math.min(mnStartRow, rowIndex);
			mnEndColumn = Math.max(mnEndColumn, colIndex + cell.getColumnSpannedNumber() - 1);
			mnEndRow = Math.max(mnEndRow, rowIndex + cell.getRowSpannedNumber() - 1);
		}

		for (int i = startRow + 1; i < endRow; i++) {
			cell = maOwnerTable.getOwnerCellByPosition(coverList, endColumn, i);
			int rowIndex = cell.getRowIndex();
			int colIndex = cell.getColumnIndex();
			mnStartColumn = Math.min(mnStartColumn, colIndex);
			mnStartRow = Math.min(mnStartRow, rowIndex);
			mnEndColumn = Math.max(mnEndColumn, colIndex + cell.getColumnSpannedNumber() - 1);
			mnEndRow = Math.max(mnEndRow, rowIndex + cell.getRowSpannedNumber() - 1);
		}
	}

	/**
	 * construct the empty cellRange
	 */
	OdfTableCellRange() {
	}

	/**
	 * Merge the current cell range to one cell
	 */
	public void merge() {
		OdfTableCell firstCell = maOwnerTable.getCellByPosition(mnStartColumn, mnStartRow);

		//note: after merge, the cell row/column count might  be changed
		int rowCount = maOwnerTable.getRowCount();
		int colCount = maOwnerTable.getColumnCount();
		//if the cell range is the whole table, then merge it to a big cell
		//as to the spreadsheet document, it should still keep the original cell count,
		//rather than merge to a big cell.
		if (rowCount == (mnEndRow - mnStartRow + 1)
				&& colCount == (mnEndColumn - mnStartColumn + 1)
				&& !mbSpreadsheet) {
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-spanned");
				firstCellElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-spanned");
				firstCellElement.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
			}
			//just copy the text of the other cells to this first cell
			for (int i = mnStartRow; i < mnEndRow + 1; i++) {
				for (int j = mnStartColumn; j < mnEndColumn + 1; j++) {
					OdfTableCell cellBase = maOwnerTable.getCellByPosition(j, i);
					if (j != mnStartColumn || i != mnStartRow) {
						//copy the content of this cell to the first cell
						firstCell.appendContentFrom(cellBase);
					}
				}
			}
			maOwnerTable.removeRowsByIndex(1, maOwnerTable.getRowCount() - 1);
			maOwnerTable.removeColumnsByIndex(1, maOwnerTable.getColumnCount() - 1);
			OdfTableColumn firstColumn = maOwnerTable.getColumnByIndex(0);
			firstColumn.setWidth(maOwnerTable.getWidth());
			mnEndRow = mnStartRow;
			mnEndColumn = mnStartColumn;
			return;
		} //if the cell range covered all the table row, and the merged column > 1
		//the merged column can be removed
		else if (rowCount == (mnEndRow - mnStartRow + 1)
				&& colCount > (mnEndColumn - mnStartColumn + 1)
				&& (mnEndColumn - mnStartColumn) > 0) {
			//the first cell, set the span attribute
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-spanned");
				firstCellElement.setTableNumberRowsSpannedAttribute(Integer.valueOf(mnEndRow - mnStartRow + 1));
				firstCellElement.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
			}
			//the other cell, copy the content to first cell
			//if it is also in the first column of the cell range, set to the covered cell
			//other cell not in the first column will be removed when remove the column
			for (int i = mnStartRow; i < mnEndRow + 1; i++) {
				for (int j = mnStartColumn; j < mnEndColumn + 1; j++) {
					OdfTableCell cellBase = maOwnerTable.getCellByPosition(j, i);
					if (j != mnStartColumn || i != mnStartRow) {
						//append content to first cell
						firstCell.appendContentFrom(cellBase);
						//change the cell in the first column of cell range to covered cell
						if ((j == mnStartColumn) && (cellBase.getOdfElement() instanceof TableTableCellElement)) {
							//change the normal cell to be the covered cell
							TableTableCellElement firstColumnCell = (TableTableCellElement) cellBase.getOdfElement();
							TableCoveredTableCellElement coveredCell = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
									(OdfFileDom) firstColumnCell.getOwnerDocument(),
									OdfName.newName(OdfDocumentNamespace.TABLE, "covered-table-cell"));
							OdfTableRow parentRow = cellBase.getTableRow();
							parentRow.getOdfElement().insertBefore(coveredCell, firstColumnCell);
							parentRow.getOdfElement().removeChild(firstColumnCell);
						}
					}
				}
			}
			List<Long> widthList = getCellRangeWidthList();
			long nCellRangeWidth = widthList.get(widthList.size() - 1).longValue() - widthList.get(0).longValue();
			maOwnerTable.removeColumnsByIndex(mnStartColumn + 1, mnEndColumn - mnStartColumn);
			OdfTableColumn firstColumn = maOwnerTable.getColumnByIndex(mnStartColumn);
			firstColumn.setWidth(nCellRangeWidth);
			mnEndColumn = mnStartColumn;
			return;
		} //if the cell range covered all the table column, the merged row can be removed
		else if (rowCount > (mnEndRow - mnStartRow + 1)
				&& colCount == (mnEndColumn - mnStartColumn + 1)
				&& (mnEndRow - mnStartRow) > 0) {
			//the first cell, set the span attribute
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-spanned");
				firstCellElement.setTableNumberColumnsSpannedAttribute(Integer.valueOf(mnEndColumn - mnStartColumn + 1));
				firstCellElement.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
			}
			//the other cell, copy the content to first cell
			//if it is also in the first row of the cell range, set to the covered cell
			//other cell not in the first row will be removed when remove the row
			for (int i = mnStartRow; i < mnEndRow + 1; i++) {
				for (int j = mnStartColumn; j < mnEndColumn + 1; j++) {
					OdfTableCell cellBase = maOwnerTable.getCellByPosition(j, i);
					if (j != mnStartColumn || i != mnStartRow) {
						//append content to first cell
						firstCell.appendContentFrom(cellBase);
						//change the cell in the first row of cell range to covered cell
						if ((i == mnStartRow) && (cellBase.getOdfElement() instanceof TableTableCellElement)) {
							//change the normal cell to be the covered cell
							TableTableCellElement firstRowCell = (TableTableCellElement) cellBase.getOdfElement();
							TableCoveredTableCellElement coveredCell = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
									(OdfFileDom) firstRowCell.getOwnerDocument(),
									OdfName.newName(OdfDocumentNamespace.TABLE, "covered-table-cell"));
							OdfTableRow parentRow = cellBase.getTableRow();
							parentRow.getOdfElement().insertBefore(coveredCell, firstRowCell);
							parentRow.getOdfElement().removeChild(firstRowCell);
						}
					}
				}
			}
			maOwnerTable.removeRowsByIndex(mnStartRow + 1, mnEndRow - mnStartRow);
			mnEndRow = mnStartRow;
			return;
		} //don't remove any row/column
		else {
			//first keep the column and row count in this cell range
			//the first cell, set the span attribute
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.setTableNumberColumnsSpannedAttribute(Integer.valueOf(mnEndColumn - mnStartColumn + 1));
				firstCellElement.setTableNumberRowsSpannedAttribute(Integer.valueOf(mnEndRow - mnStartRow + 1));
				firstCellElement.setOfficeValueTypeAttribute(OfficeValueTypeAttribute.Value.STRING.toString());
			}
			//the other cell, set to the covered cell
			for (int i = mnStartRow; i < mnEndRow + 1; i++) {
				for (int j = mnStartColumn; j < mnEndColumn + 1; j++) {
					OdfTableCell cellBase = maOwnerTable.getCellByPosition(j, i);
					if (j != mnStartColumn || i != mnStartRow) {
						if (cellBase.getOdfElement() instanceof TableTableCellElement) {
							//change the normal cell to be the covered cell
							TableTableCellElement cell = (TableTableCellElement) cellBase.getOdfElement();
							TableCoveredTableCellElement coveredCell = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
									(OdfFileDom) cell.getOwnerDocument(),
									OdfName.newName(OdfDocumentNamespace.TABLE, "covered-table-cell"));

							OdfTableRow parentRow = cellBase.getTableRow();
							parentRow.getOdfElement().insertBefore(coveredCell, cell);
							//copy the content of this cell to the first cell
							firstCell.appendContentFrom(cellBase);
							cellBase.removeContent();
							//set the table column repeated attribute
							int repeatedNum = cell.getTableNumberColumnsRepeatedAttribute().intValue();
							int num = (mnEndColumn - j + 1) - repeatedNum;
							if (num >= 0) {
								coveredCell.setTableNumberColumnsRepeatedAttribute(Integer.valueOf(repeatedNum));
								parentRow.getOdfElement().removeChild(cell);
							} else {
								coveredCell.setTableNumberColumnsRepeatedAttribute(new Integer(mnEndColumn - j + 1));
								cell.setTableNumberColumnsRepeatedAttribute(Integer.valueOf(-num));
							}

						} else if (cellBase.getOdfElement() instanceof TableCoveredTableCellElement) {
							try {
								//copy the content of this cell to the first cell
								firstCell.appendContentFrom(cellBase);
								cellBase.removeContent();
							} catch (Exception e) {
								Logger.getLogger(OdfTableCellRange.class.getName()).log(Level.SEVERE, e.getMessage(), e);
							}
						}
					}
				}
			}
		}
	}

	//vector store the x coordinate of each column which reference to the left start point of owner table
	//the returned value is all measured with "mm" unit
	private List<Long> getCellRangeWidthList() {
		List<Long> list = new ArrayList<Long>();
		Long length = Long.valueOf(0);
		for (int i = 0; i < maOwnerTable.getColumnCount() - 1; i++) {
			OdfTableColumn col = maOwnerTable.getColumnByIndex(i);
			int repeateNum = col.getColumnsRepeatedNumber();
			if (repeateNum == 1) {
				if (isColumnInCellRange(i)) {
					list.add(length);
				}
				length = Long.valueOf(length.longValue() + col.getWidth());
			} else {
				for (int j = 0; j < repeateNum; j++) {
					if (isColumnInCellRange(i + j)) {
						list.add(length);
						length = Long.valueOf(length.longValue() + col.getWidth());
					}
				}
				i += repeateNum - 1;
			}
		}
		//x coordinate of last column right point
		list.add(length);
		return list;
	}

	//vector store the x coordinate of each will split column start point
	List<Long> getVeticalSplitCellRangeWidthList(int splitNum) {
		//get each cell in the cell range(the cell here means the real cell, not the covered cell)
		List<CellCoverInfo> coverList = maOwnerTable.getCellCoverInfos(mnStartColumn, mnStartRow, mnEndColumn, mnEndRow);
		//then get the real(uncovered) cell x coordinate
		List<Long> tmpList = new ArrayList<Long>();
		List<Long> widthList = getCellRangeWidthList();
		for (int i = mnStartColumn; i < mnEndColumn + 1; i++) {
			for (int j = mnStartRow; j < mnEndRow + 1; j++) {
				if (maOwnerTable.isCoveredCellInOwnerTable(coverList, i, j)) {
					continue;
				} else {
					//the real cell, record the x coordinate of the left point
					Long width = widthList.get(i - mnStartColumn);
					if (!tmpList.contains(width)) {
						tmpList.add(width);
					}
				}
			}
		}

		//last, reorder the tmpVector and split it to splitNum between each item
		Long[] widthArray = (Long[]) tmpList.toArray();
		Arrays.sort(widthArray);
		List<Long> rtnValues = new ArrayList<Long>();
		Long colWidth;
		long unitWidth;
		rtnValues.add(widthArray[0]);
		for (int i = 1; i < widthArray.length; i++) {
			colWidth = Long.valueOf(widthArray[i].longValue() - widthArray[i - 1].longValue());
			unitWidth = colWidth.longValue() / splitNum;
			for (int j = 1; j < splitNum; j++) {
				long eachWidth = unitWidth * j + widthArray[i - 1].longValue();
				rtnValues.add(Long.valueOf(eachWidth));
			}
			rtnValues.add(widthArray[i]);
		}
		return rtnValues;
	}

	/**
	 * Get the name of the named cell range.
	 *
	 * @return the name of the cell range
	 */
	public String getCellRangeName() {
		return msCellRangeName;
	}

	/**
	 * Set the name of the current cell range.
	 *
	 * @param cellRangeName	the name that need to set
	 */
	public void setCellRangeName(String cellRangeName) {
		try {
			OdfElement contentRoot = maOwnerTable.mDocument.getContentRoot();
			//create name range element
			OdfFileDom contentDom = ((OdfFileDom) maOwnerTable.getOdfElement().getOwnerDocument());
			TableNamedExpressionsElement nameExpress = (TableNamedExpressionsElement) OdfXMLFactory.newOdfElement(
					contentDom,
					OdfName.newName(OdfDocumentNamespace.TABLE, "named-expressions"));
			String startCellRange = "$" + maOwnerTable.getTableName() + "." + maOwnerTable.getAbsoluteCellAddress(mnStartColumn, mnStartRow);
			String endCellRange = "$" + maOwnerTable.getTableName() + "." + maOwnerTable.getAbsoluteCellAddress(mnEndColumn, mnEndRow);
			TableNamedRangeElement nameRange = (TableNamedRangeElement) nameExpress.newTableNamedRangeElement(startCellRange + ":" + endCellRange, cellRangeName);
			nameRange.setTableBaseCellAddressAttribute(endCellRange);
			contentRoot.appendChild(nameExpress);
			msCellRangeName = cellRangeName;
		} catch (Exception ex) {
			Logger.getLogger(OdfTableCellRange.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Get the <code>OdfTable</code> instance who contains this cell range.
	 * @return the table that contains the cell range.
	 */
	public OdfTable getTable() {
		return maOwnerTable;
	}

	/**
	 * Get the number of rows in this cell range.
	 * @return rows number in the cell range
	 */
	public int getRowNumber() {
		return (mnEndRow - mnStartRow + 1);
	}

	/**
	 * Get the number of columns in this cell range.
	 * @return columns number in the cell range
	 */
	public int getColumnNumber() {
		return (mnEndColumn - mnStartColumn + 1);
	}

	/**
	 * Returns a single cell that is positioned at specified column and row.
	 * @param clmIndex	the column index of the cell inside the range.
	 * @param rowIndex	the row index of the cell inside the range.
	 * @return
	 * 				the cell at the specified position relative to the start position of the cell range
	 * @throws IndexOutOfBoundsException if the column/row index is bigger than the column/row count
	 */
	public OdfTableCell getCellByPosition(int clmIndex, int rowIndex) throws IndexOutOfBoundsException {
		return maOwnerTable.getCellByPosition(mnStartColumn + clmIndex, mnStartRow + rowIndex);
	}

	/**
	 * Check if the given column in is this cell range.
	 * @param colIndex
	 * 					the given column index
	 * @return true if the given column index is in the current cell range
	 *
	 */
	private boolean isColumnInCellRange(int colIndex) {
		if (colIndex < mnStartColumn || colIndex > mnEndColumn) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns a single cell that is positioned at specified cell address.
	 *
	 * @param address
	 * 				the cell address of the cell inside the range.
	 * @return
	 * 				the cell at the specified cell address relative to the start position of the cell range
	 */
	public OdfTableCell getCellByPosition(String address) {
		//if the address also contain the table name,  but the table is not the maOwnerTable
		//what should do? get the table then getcellByPosition?
		return getCellByPosition(maOwnerTable.getColIndexFromCellAddress(address), maOwnerTable.getRowIndexFromCellAddress(address));
	}
}
