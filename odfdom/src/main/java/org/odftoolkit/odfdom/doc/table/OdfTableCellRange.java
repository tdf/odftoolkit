/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
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
package org.odftoolkit.odfdom.doc.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.OdfXMLFactory;
import org.odftoolkit.odfdom.doc.office.OdfOfficeBody;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.doc.office.OdfOfficeSpreadsheet;
import org.odftoolkit.odfdom.doc.office.OdfOfficeText;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * OdfTableCellRange represent a rang of cells that are adjacent with each other
 * <p>
 * OdfTableCellRange provides methods to get/set/modify the properties of cell range.
 */
public class OdfTableCellRange {

	private int mnStartRow;
	private int mnStartColumn;
	private int mnEndRow;
	private int mnEndColumn;
	private String msCellRangeName;
	private OdfTable maOwnerTable;

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
		//System.out.println(""+mnStartColumn+","+mnStartRow+","+mnEndColumn+","+mnEndRow);
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
		//if the cell range is the whole table, then merge it to a big cell
		if (maOwnerTable.getRowCount() == (mnEndRow - mnStartRow + 1)
				&& maOwnerTable.getColumnCount() == (mnEndColumn - mnStartColumn + 1)) {
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "number-columns-spanned");
				firstCellElement.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "number-rows-spanned");
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
			///////////////////////////////////////////////////////
			maOwnerTable.removeRowsByIndex(1, maOwnerTable.getRowCount() - 1);
			maOwnerTable.removeColumnsByIndex(1, maOwnerTable.getColumnCount() - 1);
			OdfTableColumn firstColumn = maOwnerTable.getColumnByIndex(0);
			firstColumn.setWidth(maOwnerTable.getWidth());
			//TODO: mnEndRow maEndColumn should be updated when remove table Row/Column
			//add listener on column/row?
//			FTableRow row = maOwnerTable.getRowByIndex(0);
//			row.removeCellByIndex(1,maOwnerTable.getColumnCount() - 1);
			mnEndRow = mnStartRow;
			mnEndColumn = mnStartColumn;
			return;
		} //if the cell range covered all the table row, and the merged column > 1
		//the merged column can be removed
		else if (maOwnerTable.getRowCount() == (mnEndRow - mnStartRow + 1)
				&& maOwnerTable.getColumnCount() > (mnEndColumn - mnStartColumn + 1)
				&& (mnEndColumn - mnStartColumn) > 0) {
			//the first cell, set the span attribute
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "number-columns-spanned");
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
							//OdfCoveredTableCell coveredCell = new OdfCoveredTableCell((OdfFileDom)firstColumnCell.getOwnerDocument());
							TableCoveredTableCellElement coveredCell = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
									(OdfFileDom) firstColumnCell.getOwnerDocument(),
									OdfName.newName(OdfNamespaceNames.TABLE, "covered-table-cell"));
							OdfTableRow parentRow = cellBase.getTableRow();
							parentRow.getOdfElement().insertBefore(coveredCell, firstColumnCell);
							parentRow.getOdfElement().removeChild(firstColumnCell);
						}
					}
				}
			}
			/////////////////////////////////////////////////////////////////////////////
			List<Long> widthList = getCellRangeWidthList();
			long nCellRangeWidth = widthList.get(widthList.size() - 1).longValue() - widthList.get(0).longValue();
			maOwnerTable.removeColumnsByIndex(mnStartColumn + 1, mnEndColumn - mnStartColumn);
			OdfTableColumn firstColumn = maOwnerTable.getColumnByIndex(mnStartColumn);
			firstColumn.setWidth(nCellRangeWidth);
			mnEndColumn = mnStartColumn;
			return;
		} //if the cell range covered all the table column, the merged row can be removed
		else if (maOwnerTable.getRowCount() > (mnEndRow - mnStartRow + 1)
				&& maOwnerTable.getColumnCount() == (mnEndColumn - mnStartColumn + 1)
				&& (mnEndRow - mnStartRow) > 0) {
			//the first cell, set the span attribute
			if (firstCell.getOdfElement() instanceof TableTableCellElement) {
				TableTableCellElement firstCellElement = (TableTableCellElement) (firstCell.getOdfElement());
				firstCellElement.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "number-rows-spanned");
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
							//OdfCoveredTableCell coveredCell = new OdfCoveredTableCell((OdfFileDom)firstRowCell.getOwnerDocument());
							TableCoveredTableCellElement coveredCell = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
									(OdfFileDom) firstRowCell.getOwnerDocument(),
									OdfName.newName(OdfNamespaceNames.TABLE, "covered-table-cell"));
							OdfTableRow parentRow = cellBase.getTableRow();
							parentRow.getOdfElement().insertBefore(coveredCell, firstRowCell);
							parentRow.getOdfElement().removeChild(firstRowCell);
						}
					}
				}
			}
			//////////////////////////////////////////////
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
									OdfName.newName(OdfNamespaceNames.TABLE, "covered-table-cell"));

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
//								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

//	/**
//	 * split each cell that inside the current cell range to nCol*nRow cell range.
//	 * @param nCol
//	 * 				the column count that want to split
//	 */
//	public void splitVertically(int nCol){
//		//split according to the column width
//		List<Long> widthList = getCellRangeWidthList();
//		//vector size is the column number, then here we should add column
//		int size = widthList.size();
//		String startPoint = widthList.get(0).toString();
//		String endPoint = widthList.get(size-1).toString();
//		
//		List<Long> splitWidthList = getVeticalSplitCellRangeWidthList(nCol);
//		List<Long> resultWidthList = new ArrayList<Long>();
//		//merge these two vector then we can get the column number + mnStartColumn
//		resultWidthList = splitWidthList;
//		for(int i=0; i< (widthList.size() - 1); i++){
//			if(!resultWidthList.contains(widthList.get(i)))
//				resultWidthList.add(widthList.get(i));
//		}
//		
//		Long[] array = (Long[])resultWidthList.toArray();
//		Arrays.sort(array);
//		List<Long> orderedWidthList = new ArrayList<Long>();
//		for(int i=0; i<array.length;i++)
//			orderedWidthList.add(array[i]);
//		
//		//get the cell cover info
//		List<CellCoverInfo> coverList = maOwnerTable.getCellCoverInfos(0, 0, maOwnerTable.getColumnCount() - 1, maOwnerTable.getRowCount() - 1);
//		//according to the orderedWidthList, we can know each real cell
//		//the cell not only in cell range, but the cell range covered column should be updated
//		//covered how many cells
//		//update each row for the covered cell.
//		for(int j=mnEndColumn; j >= mnStartColumn; j--){
//			//use the reverse order, so that when insert cell will not impact the previous cell
//			int startCol=0;
//			int endCol=0; 
//			//check this cell cover how many new column
//			long nCellWidth = widthList.get(j-mnStartColumn+1).longValue();
//			long nPreCellWidth = widthList.get(j-mnStartColumn).longValue();
//			int newCoveredCol = 0;
//			for(int m=1; m<orderedWidthList.size();m++){
//				long newColWidth = orderedWidthList.get(m).longValue();
//				if( (nCellWidth > newColWidth) && (newColWidth > nPreCellWidth) ){
//					if(newCoveredCol == 0)
//						startCol = m - 1;
//					newCoveredCol++;
//				}
//				else if(nCellWidth <= newColWidth){
//					endCol = m;
//					break;
//				}
//			}
//			for(int i=0;i< maOwnerTable.getRowCount();i++){
//				OdfTableCell cellBase = maOwnerTable.getCellByPosition(j,i);
//				//if(isCoveredCellInOwnerTable(coverList,j,i))
//				if(!isRowInCellRange(i))
//				{
//					//new the covered cell
//					for(int m=endCol;m<startCol;m--)
//					{
////						
////						if(orderedWidthList.get(m).longValue() == splitWidthList.get(splitIndex).longValue())
////						{
////							
////							splitIndex--;
////						}
//						//org.odftoolkit.odfdom.doc.table.OdfCoveredTableCell coveredCell = new org.odftoolkit.odfdom.doc.table.OdfCoveredTableCell((OdfFileDom)maOwnerTable.getOdfElement().getOwnerDocument());
//						TableCoveredTableCellElement coveredCell = (TableCoveredTableCellElement) OdfElementFactory.newOdfElement(
//								(OdfFileDom)maOwnerTable.getOdfElement().getOwnerDocument(),
//								OdfName.get(OdfNamespaceNames.TABLE.getNamespaceUri(),"covered-table-cell"));
//						OdfTableRow parentRow = cellBase.getTableRow();
//						TableTableCellElementBase nextElement = (TableTableCellElementBase)cellBase.getOdfElement().getNextSibling();
//						if(nextElement!=null)
//							parentRow.getOdfElement().insertBefore(coveredCell, nextElement);
//						else
//							parentRow.getOdfElement().appendChild(coveredCell);
//					}
//					//update the owner cell column span info
//					OdfTableCell ownerCell = maOwnerTable.getOwnerCellByPosition(coverList,j,i);
//					//if the start row of owner cell is i, then update column span
//					//if not, the column span might be added several times which depend on the owner cell's row span and repeated row number
//					if(ownerCell.getTableRow().getRowIndex() == i){
//						((TableTableCellElement)ownerCell.getOdfElement()).setTableNumberColumnsSpannedAttribute(ownerCell.getColumnSpannedNumber()+newCoveredCol);
//					}
//				}
//					//add table column 
//					if(i== (maOwnerTable.getRowCount() -1) ){
//						for(int m=startCol;m<endCol;m++)
//						{
//							OdfTableColumn newCol = maOwnerTable.insertColumnBefore(j, 1).get(0);
//							newCol.setWidth(orderedWidthList.get(m+1) - orderedWidthList.get(m));
//						}
//					}
//			}
//		}
//		//this does not need to do, because when table insert/remove column row, mnStart/EndColumn/Row should be updated
//		//TODO: how to update the column/row count in the current cell range
//		//mnEndColumn = mnStartColumn + orderedWidthList.size() - 1;
//	}
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
	 * split each cell that inside the current cell range to nCol*nRow cell range.
	 * @param nRow
	 * 				the row count that want to split
	 */
	/*public void splitHorizontally(int nRow){
	//get the least row span which will decide how much new row need to add
	//get the cell cover info
	List<CellCoverInfo> coverList = getCellCoverInfos(0, 0, maOwnerTable.getColumnCount(), maOwnerTable.getRowCount());
	for(int i=0;i< maOwnerTable.getColumnCount();i++)
	//use the reverse order, so that when insert cell will not impact the previouse cell
	for(int j=mnEndRow; j >= mnStartRow; j--){
	//if covered cell row index > nRow
	//merge (row index - nRow) num covered cell  in the same column to a real cell, and change row span to (row index - nRow)
	//else
	//if <
	//change this covered cell to real cell, and split it (nRow - row index)
	//the owner cell cover how many rows
	//just the covered cell in the last row of the owner cell need to split
	OdfTableCell cellBase = maOwnerTable.getCellByPosition(i,j);
	OdfTableCell ownerCell = getOwnerCellByPosition(coverList,i,j);
	int startRow = ownerCell.getOwnerTableRow().getRowIndex();
	int spanRow = ownerCell.getTableRowSpanNumber();
	{
	if(startCol == i){
	//change covered cell to real cell
	//duo chulai de cell(spanRow-nRow) is still the covered cell, but need to update the new owner cell(update row span)
	if(spanRow >= nRow)
	//new row (spanRow - nRow)
	else
	//change all the covered cell(nRow-spanRow) to a real cell, and update the real cell
	}else{
	//split the covered cell
	}

	}
	}
	}*/
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
		OdfFileDom contentDom = ((OdfFileDom) maOwnerTable.getOdfElement().getOwnerDocument());
		OdfOfficeBody contentBody = contentDom.getOdfDocument().getOfficeBody();
		NodeList childs = contentBody.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {
			Node cur = childs.item(i);
			if ((cur != null)
					&& (cur instanceof OdfOfficeText || cur instanceof OdfOfficeSpreadsheet || cur instanceof OfficePresentationElement)) {
				//create name range element
				//OdfTableNamedExpressions nameExpress = new OdfTableNamedExpressions(contentDom);
				TableNamedExpressionsElement nameExpress = (TableNamedExpressionsElement) OdfXMLFactory.newOdfElement(
						contentDom,
						OdfName.newName(OdfNamespaceNames.TABLE, "named-expressions"));
				String startCellRange = "$" + maOwnerTable.getTableName() + "." + maOwnerTable.getAbsoluteCellAddress(mnStartColumn, mnStartRow);
				String endCellRange = "$" + maOwnerTable.getTableName() + "." + maOwnerTable.getAbsoluteCellAddress(mnEndColumn, mnEndRow);
				TableNamedRangeElement nameRange = (TableNamedRangeElement) nameExpress.newTableNamedRangeElement(startCellRange + ":" + endCellRange, cellRangeName);
				nameRange.setTableBaseCellAddressAttribute(endCellRange);
				cur.appendChild(nameExpress);
				break;
			}
		}
		msCellRangeName = cellRangeName;
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
	 * Check if the given row in is this cell range.
	 * @param rowIndex	
	 * 					the given row index
	 * @return true if the given row index is in the current cell range
	 * 
	 */
//	private boolean isRowInCellRange(int rowIndex){
//		if( rowIndex < mnStartRow || rowIndex > mnEndRow )
//			return false;
//		else
//			return true;
//	}
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
