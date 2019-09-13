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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfTableRowProperties;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.odfdom.type.PositiveLength;
import org.w3c.dom.Node;

/**
 * OdfTableRow represents table row feature in ODF document.
 * <p>
 * OdfTableRow provides methods to get table cells that belong to this table row.
 *
 */
public class OdfTableRow {

	//boolean mbVisible;
	TableTableRowElement maRowElement;
	int mnRepeatedIndex;
	int mRowsRepeatedNumber = -1;
	private static final String DEFAULT_HEIGHT = "0.30in";
	private OdfDocument mDocument;

	/**
	 * Construct the <code>OdfTableRow</code> feature.
	 * @param rowElement	the row element represent this row
	 * @param repeatedIndex	the index in the repeated rows
	 */
	OdfTableRow(TableTableRowElement rowElement, int repeatedIndex) {
		maRowElement = rowElement;
		mnRepeatedIndex = repeatedIndex;
		mDocument = (OdfDocument) ((OdfFileDom) maRowElement.getOwnerDocument()).getDocument();
	}

	/**
	 * Get the <code>OdfTableRow</code> instance from the <code>TableTableRowElement</code> instance.
	 * <p>
	 * Each <code>TableTableRowElement</code> instance has a one-to-one relationship to a <code>OdfTableRow</code> instance.
	 *
	 * @param rowElement	the row element that need to get the corresponding <code>OdfTableRow</code> instance
	 * @return the <code>OdfTableRow</code> instance represent the specified row element
	 */
	public static OdfTableRow getInstance(TableTableRowElement rowElement) {
		TableTableElement tableElement = null;
		Node node = rowElement.getParentNode();
		while (node != null) {
			if (node instanceof TableTableElement) {
				tableElement = (TableTableElement) node;
			}
			node = node.getParentNode();
		}
		OdfTable table = null;
		if (tableElement != null) {
			table = OdfTable.getInstance(tableElement);
		} else {
			throw new IllegalArgumentException("the rowElement is not in the table dom tree");
		}

		OdfTableRow row = table.getRowInstance(rowElement, 0);
		if (row.getRowsRepeatedNumber() > 1) {
			Logger.getLogger(OdfTableRow.class.getName()).log(Level.WARNING, "the row has the repeated row number, and puzzled about get which repeated index of the row,"
					+ "here just return the first row of the repeated rows.");
		}
		return row;
	}

	/**
	 * Get the <code>TableTableElement</code>  who contains this row.
	 *
	 * @return the table element that contains the row.
	 */
	private TableTableElement getTableElement() {
		Node node = maRowElement.getParentNode();
		while (node != null) {
			if (node instanceof TableTableElement) {
				return (TableTableElement) node;
			}
			node = node.getParentNode();
		}
		return null;
	}

	/**
	 * Get owner table of the current row.
	 * @return	the parent table of this row
	 */
	public OdfTable getTable() {
		TableTableElement tableElement = getTableElement();
		if (tableElement != null) {
			return OdfTable.getInstance(tableElement);
		}
		return null;
	}

	/**
	 * Return the height of the row (in Millimeter).
	 * <p>
	 * Return the minimal height, if the row height is not set,
	 * @return the height of the current row (in Millimeter).
	 */
	public long getHeight() {
		String sHeight = maRowElement.getProperty(OdfTableRowProperties.RowHeight);
		if (sHeight == null) {
			sHeight = maRowElement.getProperty(OdfTableRowProperties.MinRowHeight);
		}
		if (sHeight == null) {
			sHeight = DEFAULT_HEIGHT;
		}
		return PositiveLength.parseLong(sHeight, Unit.MILLIMETER);
	}

	/**
	 * Set the height/minimal height of the row (in Millimeter) according to the second parameter.
	 * @param height
	 * 				the height/minimal height that will be set to the row (in Millimeter).
	 * @param isMinHeight
	 * 				if it is true, the row can fit the height to the text, vice versa.
	 */
	public void setHeight(long height, boolean isMinHeight) {
		String sHeightMM = String.valueOf(height) + Unit.MILLIMETER.abbr();
		String sHeightIN = PositiveLength.mapToUnit(sHeightMM, Unit.INCH);
		splitRepeatedRows();
		maRowElement.setProperty(OdfTableRowProperties.RowHeight, sHeightIN);
	}

	//if one of the repeated row want to change something
	//then this repeated row have to split to repeated number rows
	//the maRowElement/mnRepeatedIndex should also be updated according to the original index in the repeated column
	void splitRepeatedRows() {
		int repeateNum = getRowsRepeatedNumber();
		if (repeateNum > 1) {
			OdfTable table = getTable();
			TableTableElement tableEle = table.getOdfElement();
			//change this repeated row to several single rows
			TableTableRowElement ownerRowElement = null;
			int repeatedRowIndex = mnRepeatedIndex;
			Node refElement = maRowElement;
			Node oldRowElement = maRowElement;
			for (int i = repeateNum - 1; i >= 0; i--) {
				TableTableRowElement newRow = (TableTableRowElement) maRowElement.cloneNode(true);
				newRow.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-repeated");
				tableEle.insertBefore(newRow, refElement);
				refElement = newRow;
				if (repeatedRowIndex == i) {
					ownerRowElement = newRow;
				} else {
					table.updateRowRepository(maRowElement, i, newRow, 0);
				}
			}

			if (ownerRowElement != null) {
				table.updateRowRepository(maRowElement, mnRepeatedIndex, ownerRowElement, 0);
			}
			tableEle.removeChild(oldRowElement);
			mRowsRepeatedNumber = -1;
		}
	}

	/**
	 * Return if the row always keeps its optimal height.
	 * @return
	 * 			true if the row always keeps its optimal height;
	 * 			vice versa
	 */
	public boolean isOptimalHeight() {
		return Boolean.parseBoolean(maRowElement.getProperty(OdfTableRowProperties.UseOptimalRowHeight));
	}

	/**
	 * Set if the row always keeps its optimal height.
	 * @param isUseOptimalHeight
	 * 					the flag that indicate row should keep its optimal height or not
	 */
	public void setUseOptimalHeight(boolean isUseOptimalHeight) {
		maRowElement.setProperty(OdfTableRowProperties.UseOptimalRowHeight, String.valueOf(isUseOptimalHeight));
	}

	/**
	 * Return an instance of <code>TableTableRowElement</code> which represents this feature.
	 * @return an instance of <code>TableTableRowElement</code>
	 */
	public TableTableRowElement getOdfElement() {
		return maRowElement;
	}

	/**
	 * Get a cell with a specific index. The table will be automatically
	 * expanded, when the given index is outside of the original table.
	 *
	 * @param index
	 *            the cell index in this row
	 * @return the cell object in the given cell index
	 */
	public OdfTableCell getCellByIndex(int index) {
		OdfTable table = getTable();
		if (index < 0) {
			throw new IllegalArgumentException("index should be nonnegative integer.");
		}
		// expand column as needed.
		int lastColumnIndex = table.getColumnCount() - 1;
		if (index > lastColumnIndex) {
			//need clean cell style.
			table.appendColumns((index - lastColumnIndex), true);
		}
		for (Node n : new DomNodeList(maRowElement.getChildNodes())) {
			if (n instanceof TableTableCellElementBase) {
				if (index == 0) {
					return table.getCellInstance((TableTableCellElementBase) n,
							0, mnRepeatedIndex);
				} else {
					int nextIndex = index
							- ((TableTableCellElementBase) n).getTableNumberColumnsRepeatedAttribute().intValue();
					if (nextIndex < 0) {
						OdfTableCell cell = table.getCellInstance(
								(TableTableCellElementBase) n, index,
								mnRepeatedIndex);
						return cell;
					} else {
						index = nextIndex;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Return the count of real cells in this row.
	 * The cells covered by top cells are not counted.
	 * <p>
	 * Please note it might not equal to the column count of the owner table,
	 * because some of them are the covered cells.
	 * @return 		the cell count
	 */
	public int getCellCount() {
		OdfTable table = getTable();
		Set<OdfTableCell> realCells = new HashSet<OdfTableCell>();
		List<CellCoverInfo> coverList = table.getCellCoverInfos(0, 0, table.getColumnCount() - 1, table.getRowCount() - 1);
		int rowIndex = getRowIndex();
		for (int i = 0; i < table.getColumnCount(); i++) {
			OdfTableCell cell = table.getOwnerCellByPosition(coverList, i, rowIndex);
			realCells.add(cell);
		}
		return realCells.size();
	}

	/**
	 * Return the previous row of the current row.
	 *
	 * @return the previous row before this row in the owner table
	 */
	public OdfTableRow getPreviousRow() {
		OdfTable table = getTable();
		//the row has repeated row number > 1
		if (getRowsRepeatedNumber() > 1) {
			if (mnRepeatedIndex > 0) {
				return table.getRowInstance(maRowElement, mnRepeatedIndex - 1);
			}
		}
		//the row has repeated row number > 1 && the index is 0
		//or the row has repeated row num = 1
		Node aPrevNode = maRowElement.getPreviousSibling();
		Node aCurNode = maRowElement;
		TableTableRowElement lastRow;
		while (true) {
			if (aPrevNode == null) {
				//does not have previous sibling, then get the parent
				//because aCurNode might be the child element of table-header-rows, table-rows, table-row-group
				Node parentNode = aCurNode.getParentNode();
				//if the parent is table, then it means that this row is the first row in this table
				//it has no previous row
				if (parentNode instanceof TableTableElement) {
					return null;
				}
				aPrevNode = parentNode.getPreviousSibling();
			}
			//else the previous node might be table-header-rows, table-rows, table-row-group
			if (aPrevNode != null) {
				try {
					if (aPrevNode instanceof TableTableRowElement) {
						return table.getRowInstance((TableTableRowElement) aPrevNode,
								((TableTableRowElement) aPrevNode).getTableNumberRowsRepeatedAttribute().intValue() - 1);
					} else if (aPrevNode instanceof TableTableRowsElement
							|| aPrevNode instanceof TableTableHeaderRowsElement
							|| aPrevNode instanceof TableTableRowGroupElement) {
						XPath xpath = ((OdfContentDom) aPrevNode.getOwnerDocument()).getXPath();
						synchronized (mDocument) {
							lastRow = (TableTableRowElement) xpath.evaluate(".//table:table-row[last()]", aPrevNode, XPathConstants.NODE);
						}
						if (lastRow != null) {
							return table.getRowInstance(lastRow, lastRow.getTableNumberRowsRepeatedAttribute().intValue() - 1);
						}
					} else {
						aCurNode = aPrevNode;
						aPrevNode = aPrevNode.getPreviousSibling();
					}
				} catch (XPathExpressionException e) {
					Logger.getLogger(OdfTableRow.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Return the next row of the current row.
	 *
	 * @return the next row after this row in the owner table
	 */
	public OdfTableRow getNextRow() {
		OdfTable table = getTable();
		//the row has repeated row number > 1
		if (getRowsRepeatedNumber() > 1) {
			if (mnRepeatedIndex < (getRowsRepeatedNumber() - 1)) {
				return table.getRowInstance(maRowElement, mnRepeatedIndex + 1);
			}
		}

		Node aNextNode = maRowElement.getNextSibling();
		Node aCurNode = maRowElement;
		TableTableRowElement firstRow;
		while (true) {
			if (aNextNode == null) {
				//does not have next sibling, then get the parent
				//because aCurNode might be the child element of table-header-rows, table-rows, table-row-group
				Node parentNode = aCurNode.getParentNode();
				//if the parent is table, then it means that this row is the last row in this table
				//it has no next row
				if (parentNode instanceof TableTableElement) {
					return null;
				}
				aNextNode = parentNode.getNextSibling();
			}
			//else the next node might be table-header-rows, table-rows, table-row-group
			if (aNextNode != null) {
				try {
					if (aNextNode instanceof TableTableRowElement) {
						return table.getRowInstance((TableTableRowElement) aNextNode, 0);
					} else if (aNextNode instanceof TableTableRowsElement
							|| aNextNode instanceof TableTableHeaderRowsElement
							|| aNextNode instanceof TableTableRowGroupElement) {
						XPath xpath = ((OdfContentDom) aNextNode.getOwnerDocument()).getXPath();
						synchronized (mDocument) {
							firstRow = (TableTableRowElement) xpath.evaluate(".//table:table-row[first()]", aNextNode, XPathConstants.NODE);
						}
						if (firstRow != null) {
							return table.getRowInstance(firstRow, 0);
						}
					} else {
						aCurNode = aNextNode;
						aNextNode = aNextNode.getNextSibling();
					}
				} catch (XPathExpressionException e) {
					Logger.getLogger(OdfTableRow.class.getName()).log(Level.SEVERE, e.getMessage(), e);
				}
			}
		}
	}
	/**
	 * Set the default cell style to this row.
	 * <p>
	 * The style should already exist in this document.
	 * @param style
	 * 			the cell style of the document
	 */
	public void setDefaultCellStyle(OdfStyle style) {
		splitRepeatedRows();
		OdfStyle defaultStyle = getDefaultCellStyle();
		if (defaultStyle != null) {
			defaultStyle.removeStyleUser(maRowElement);
		}

		if (style != null) {
			style.addStyleUser(maRowElement);
			maRowElement.setTableDefaultCellStyleNameAttribute(
					style.getStyleNameAttribute());
		}
	}

	/**
	 * Get the default cell style of this row.
	 *
	 * @return the default cell style of this row
	 */
	public OdfStyle getDefaultCellStyle() {
		String styleName = maRowElement.getTableDefaultCellStyleNameAttribute();
		OdfStyle style = maRowElement.getOrCreateAutomaticStyles().getStyle(
				styleName, OdfStyleFamily.TableCell);

		if (style == null) {
			style = mDocument.getDocumentStyles().getStyle(styleName, OdfStyleFamily.TableCell);
		}
		return style;
	}

	/**
	 * Return the index of this row in the owner table.
	 *
	 * @return the index of the row
	 */
	public int getRowIndex() {
		int result = 0;
		TableTableElement mTableElement = getTableElement();
		TableTableRowElement rowEle = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderRowsElement) {
				TableTableHeaderRowsElement headers = (TableTableHeaderRowsElement) n;
				for (Node m : new DomNodeList(headers.getChildNodes())) {
					if (m instanceof TableTableRowElement) {
						rowEle = (TableTableRowElement) m;
						if (rowEle == getOdfElement()) {
							return result + mnRepeatedIndex;
						}
						result += rowEle.getTableNumberRowsRepeatedAttribute();
					}
				}
			}
			if (n instanceof TableTableRowElement) {
				rowEle = (TableTableRowElement) n;
				if (rowEle == getOdfElement()) {
					break;
				}
				result += ((TableTableRowElement) n).getTableNumberRowsRepeatedAttribute();
			}
		}
		return result + mnRepeatedIndex;

	}

	//insert count number of cell from index
	//this is called after insertColumn has been called by OdfTable
	void insertCellByIndex(int index, int count) {
		splitRepeatedRows();
		//all insert the real cell
		OdfTable table = getTable();
		List<CellCoverInfo> coverList = table.getCellCoverInfos(0, 0, table.getColumnCount() - 1, table.getRowCount() - 1);
		int rowIndex = getRowIndex();
		OdfTableCell preCell;
		if (index == 0) {
			preCell = table.getOwnerCellByPosition(coverList, 0, rowIndex);
		} else {
			preCell = table.getOwnerCellByPosition(coverList, index - 1, rowIndex);
		}
		OdfTableCell nextCell = getCellByIndex(index);
		if (nextCell == null) {
			nextCell = getCellByIndex(getCellCount() - 1);
		}
		for (int i = index + count; i > index; i--) {
			TableTableCellElement newCell = (TableTableCellElement) OdfXMLFactory.newOdfElement((OdfFileDom) maRowElement.getOwnerDocument() ,
					OdfName.newName(OdfDocumentNamespace.TABLE, "table-cell"));
			newCell.setTableStyleNameAttribute(preCell.getStyleName());
			maRowElement.insertBefore(newCell, nextCell.getOdfElement());
		}
	}

	//note: we have to use this method to modify the row repeated number
	//in order to update mnRepeatedIndex of the each row
	void setRowsRepeatedNumber(int num) {
		mRowsRepeatedNumber = num;
		//update the mnRepeatedIndex for the ever repeated row
		maRowElement.setTableNumberRowsRepeatedAttribute(Integer.valueOf(num));
	}

	int getRowsRepeatedNumber() {
		if (mRowsRepeatedNumber < 0) {
			Integer count = maRowElement.getTableNumberRowsRepeatedAttribute();
			if (count == null) {
				mRowsRepeatedNumber = 1;
			} else {
				mRowsRepeatedNumber = count.intValue();
			}
		}
		return mRowsRepeatedNumber;
	}

	/****************************
	 * Moved from OdfTable
	 *
	 */
	private void insertCellElementBefore(OdfElement parentEle, TableTableCellElementBase positionEle, TableTableCellElementBase cellEle, int count) {
		if (positionEle == null) {
			parentEle.appendChild(cellEle);
			for (int i = 1; i < count; i++) {
				parentEle.appendChild(cellEle.cloneNode(true));
			}
		} else {
			parentEle.insertBefore(cellEle, positionEle);
			for (int i = 1; i < count; i++) {
				parentEle.insertBefore(cellEle.cloneNode(true), positionEle);
			}
		}
	}

	void insertCellBefore(OdfTableCell refCell, OdfTableCell positionCell, int count) {
		splitRepeatedRows();
		OdfTable ownerTable = getTable();

		if (positionCell == null) {
			if (refCell.isCoveredElement()) {
				TableTableCellElement coverCellEle = (TableTableCellElement) refCell.getCoverCell().getOdfElement();
				TableTableCellElement newCellEle = (TableTableCellElement) coverCellEle.cloneNode(true);
				cleanCell(newCellEle);
				insertCellElementBefore(getOdfElement(), null, newCellEle, count);
			} else {
				TableTableCellElement endCellEle = (TableTableCellElement) refCell.getOdfElement().cloneNode(true);
				cleanCell(endCellEle);
				getOdfElement().appendChild(endCellEle);
				reviseStyleFromLastColumnToMedium(refCell);
				if (count > 1) {
					TableTableCellElement newCellEle = (TableTableCellElement) refCell.getOdfElement().cloneNode(true);
					cleanCell(newCellEle);
					insertCellElementBefore(getOdfElement(), endCellEle, newCellEle, count - 1);
				}
			}
		} else {
			TableTableCellElement coverRefCellEle = null;
			TableTableCellElement coverPosCellEle = null;
			OdfTableCell coverRefCell = null;
			if (refCell.isCoveredElement()) { //get ref cover cell
				coverRefCell = refCell.getCoverCell();
				coverRefCellEle = (TableTableCellElement) coverRefCell.getOdfElement();
			}
			if (positionCell.isCoveredElement()) //get position cover cell
			{
				coverPosCellEle = (TableTableCellElement) positionCell.getCoverCell().getOdfElement();
			}

			if ((coverRefCellEle != null && coverRefCellEle == coverPosCellEle) //is cover cell and have the same cover cell
					|| (coverPosCellEle != null && refCell.getOdfElement() == coverPosCellEle)) //position cell is cover cell and refer cell covers position cell
			{
				if (coverRefCellEle == null) {
					coverRefCellEle = (TableTableCellElement) refCell.getOdfElement();
					coverRefCell = refCell;
				}
				TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
						(OdfFileDom) ownerTable.getOdfElement().getOwnerDocument(),
						OdfName.newName(OdfDocumentNamespace.TABLE, "covered-table-cell"));
				insertCellElementBefore(getOdfElement(), positionCell.getOdfElement(), newCellEle, count);
				if (refCell.getRowIndex() == coverRefCell.getRowIndex()) //the first cover line
				{
					coverRefCell.setColumnSpannedNumber(coverRefCell.getColumnSpannedNumber() + count);
				}
			} else if (coverRefCellEle != null) //is cover cell
			{
				if (refCell.getRowIndex() == coverRefCell.getRowIndex()) { //the first cover line
					TableTableCellElement newCellEle = (TableTableCellElement) coverRefCellEle.cloneNode(true);
					cleanCell(newCellEle);
					insertCellElementBefore(getOdfElement(), positionCell.getOdfElement(), newCellEle, count);
				} else { //the second and other cover line
					TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) refCell.getOdfElement().cloneNode(true);
					newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
					insertCellElementBefore(getOdfElement(), positionCell.getOdfElement(), newCellEle, count);
				}
			} else if ((refCell.getOdfElement() == positionCell.getOdfElement())
					&& (refCell.getColumnsRepeatedNumber() > 1)) //repeated number
			{
				int repeatNum = refCell.getColumnsRepeatedNumber();
				//update the cell that after the ref cell
				for (int i = repeatNum - 1; i > refCell.mnRepeatedColIndex; i--) {
					ownerTable.updateCellRepository(refCell.getOdfElement(), i, refCell.mnRepeatedRowIndex,
							refCell.getOdfElement(), i + count, refCell.mnRepeatedRowIndex);
				}
				refCell.getOdfElement().setTableNumberColumnsRepeatedAttribute(repeatNum + count);
			} else {
				TableTableCellElement newCellEle = (TableTableCellElement) refCell.getOdfElement().cloneNode(true);
				cleanCell(newCellEle);
				insertCellElementBefore(getOdfElement(), positionCell.getOdfElement(), newCellEle, count);
			}
		}
	}

	/**
	 * This method is to insert a cell same as refCell before positionCell.
	 * <p>
	 * This method is invoked by appendColumn and insertColumnBefore.
	 */
	OdfTableCell insertCellBefore(OdfTableCell refCell, OdfTableCell positionCell) {
		splitRepeatedRows();
		OdfTableCell newCell = null;
		OdfTable ownerTable = getTable();

		if (positionCell == null) {
			if (refCell.isCoveredElement()) {
				TableTableCellElement coverCellEle = (TableTableCellElement) refCell.getCoverCell().getOdfElement();
				TableTableCellElement newCellEle = (TableTableCellElement) coverCellEle.cloneNode(true);
				cleanCell(newCellEle);
				getOdfElement().appendChild(newCellEle);
				newCell = ownerTable.getCellInstance(newCellEle, 0, 0);
			} else {
				TableTableCellElement newCellEle = (TableTableCellElement) refCell.getOdfElement().cloneNode(true);
				cleanCell(newCellEle);
				getOdfElement().appendChild(newCellEle);
				newCell = ownerTable.getCellInstance(newCellEle, 0, 0);
				reviseStyleFromLastColumnToMedium(refCell);
			}
		} else {
			TableTableCellElement coverRefCellEle = null;
			TableTableCellElement coverPosCellEle = null;
			OdfTableCell coverRefCell = null;
			if (refCell.isCoveredElement()) { //get ref cover cell
				coverRefCell = refCell.getCoverCell();
				coverRefCellEle = (TableTableCellElement) coverRefCell.getOdfElement();
			}
			if (positionCell.isCoveredElement()) //get position cover cell
			{
				coverPosCellEle = (TableTableCellElement) positionCell.getCoverCell().getOdfElement();
			}

			if ((coverRefCellEle != null && coverRefCellEle == coverPosCellEle) //is cover cell and have the same cover cell
					|| (coverPosCellEle != null && refCell.getOdfElement() == coverPosCellEle)) //position cell is cover cell and refer cell covers position cell
			{
				if (coverRefCellEle == null) {
					coverRefCellEle = (TableTableCellElement) refCell.getOdfElement();
					coverRefCell = refCell;
				}
				TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) OdfXMLFactory.newOdfElement(
						(OdfFileDom) ownerTable.getOdfElement().getOwnerDocument(),
						OdfName.newName(OdfDocumentNamespace.TABLE, "covered-table-cell"));
				getOdfElement().insertBefore(newCellEle, positionCell.getOdfElement());
				if (refCell.getRowIndex() == coverRefCell.getRowIndex()) //the first cover line
				{
					coverRefCell.setColumnSpannedNumber(coverRefCell.getColumnSpannedNumber() + 1);
				}
				newCell = ownerTable.getCellInstance(newCellEle, 0, 0);
			} else if (coverRefCellEle != null) //is cover cell
			{
				if (refCell.getRowIndex() == coverRefCell.getRowIndex()) { //the first cover line
					TableTableCellElement newCellEle = (TableTableCellElement) coverRefCellEle.cloneNode(true);
					cleanCell(newCellEle);
					getOdfElement().insertBefore(newCellEle, positionCell.getOdfElement());
					newCell = ownerTable.getCellInstance(newCellEle, 0, 0);
				} else { //the second and other cover line
					TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) refCell.getOdfElement().cloneNode(true);
					newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
					getOdfElement().insertBefore(newCellEle, positionCell.getOdfElement());
					newCell = ownerTable.getCellInstance(newCellEle, 0, 0);
				}
			} else if ((refCell.getOdfElement() == positionCell.getOdfElement())
					&& (refCell.getColumnsRepeatedNumber() > 1)) //repeated number
			{
				int repeatNum = refCell.getColumnsRepeatedNumber();
				//update the cell that after the ref cell
				for (int i = repeatNum - 1; i > refCell.mnRepeatedColIndex; i--) {
					ownerTable.updateCellRepository(refCell.getOdfElement(), i, refCell.mnRepeatedRowIndex,
							refCell.getOdfElement(), i + 1, refCell.mnRepeatedRowIndex);
				}
				refCell.getOdfElement().setTableNumberColumnsRepeatedAttribute(repeatNum + 1);
				newCell = ownerTable.getCellInstance(refCell.getOdfElement(), refCell.mnRepeatedColIndex + 1, refCell.mnRepeatedRowIndex);
			} else {
				TableTableCellElement newCellEle = (TableTableCellElement) refCell.getOdfElement().cloneNode(true);
				cleanCell(newCellEle);
				getOdfElement().insertBefore(newCellEle, positionCell.getOdfElement());
				newCell = ownerTable.getCellInstance(newCellEle, 0, 0);
			}
		}
		return newCell;
	}

	private void cleanCell(TableTableCellElement newCellEle) {
		newCellEle.removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "value");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "date-value");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "time-value");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "boolean-value");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "string-value");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "formula");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
		newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-spanned");
		if(!getTable().isCellStyleInheritance()){
			newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "style-name");
		}
		Node n = newCellEle.getFirstChild();
		while (n != null) {
			Node m = n.getNextSibling();
			if (n instanceof TextPElement
					|| n instanceof TextHElement
					|| n instanceof TextListElement) {
				newCellEle.removeChild(n);
			}
			n = m;
		}
	}

	private void reviseStyleFromLastColumnToMedium(OdfTableCell oldLastCell) {
		if (getTable().mIsSpreadsheet) return;

		OdfStyle styleEle = oldLastCell.getCellStyleElementForWrite();
		if (styleEle != null) {
			if (oldLastCell.getRowIndex() == 0) {
				OdfTable.setLeftTopBorderStyleProperties(styleEle);
			} else {
				OdfTable.setLeftBottomBorderStylesProperties(styleEle);
			}
		}
	}

	private void reviseStyleFromMediumColumnToLast(OdfTableCell newLastCell) {
		if (getTable().mIsSpreadsheet) return;

		OdfStyle styleEle = newLastCell.getCellStyleElementForWrite();
		if (styleEle != null) {
			if (newLastCell.getRowIndex() == 0) {
				OdfTable.setRightTopBorderStyleProperties(styleEle);
			} else {
				OdfTable.setRightBottomBorderStylesProperties(styleEle);
			}
		}
	}

	/**
	 * This method is invoked by removeColumnByIndex
	 * So we don't need to care about
	 * the covered and spanned cell in a same column
	 */
	void removeCellByIndex(int nStart, int nCount) {
		splitRepeatedRows();
		OdfTableCell startCell = getCellByIndex(nStart);
		OdfTableCell coverCell = null;
		if (startCell.isCoveredElement()) {
			coverCell = startCell.getCoverCellInSameRow();
		}

		int index = nStart;
		for (int i = 0; i < nCount; i++) {
			OdfTableCell cell = getCellByIndex(index);
			if(cell!=null) {
				cell.splitRepeatedCells();
				if (cell.isCoveredElement() && coverCell != null) {
					coverCell.setColumnSpannedNumber(coverCell.getColumnSpannedNumber() - cell.getColumnsRepeatedNumber());
					maRowElement.removeChild(cell.getOdfElement());
					i += cell.getColumnsRepeatedNumber() - 1;
				} else if (cell.isCoveredElement()) {
					maRowElement.removeChild(cell.getOdfElement());
					i += cell.getColumnsRepeatedNumber() - 1;
				} else if (!cell.isCoveredElement()) {
	                int columnSpan = cell.getColumnSpannedNumber();
					if (i + columnSpan <= nCount) {
						maRowElement.removeChild(cell.getOdfElement());
						i += columnSpan - 1;
					} else {
	                    cell.setColumnSpannedNumber(columnSpan - 1);
	                    OdfElement nextCell = OdfElement.getNextSiblingElement(cell.mCellElement);
	                    // Recently some office application do not use <table:covered-table-cell> elements any longer
	                    if(nextCell instanceof TableCoveredTableCellElement){
	                        // only delete the next child if the next IS a <table:covered-table-cell> element
	                        removeCellByIndex(index + 1, nCount - i);
	                    }

					}
				}
			}
		}

		int clmnum = getTable().getColumnCount();
		if (nStart + nCount >= clmnum) {
			OdfTableCell cell = getCellByIndex(nStart - 1);
			reviseStyleFromMediumColumnToLast(cell);
		}
	}

	void removeAllCellsRelationship() {
		OdfTable table = getTable();

		for (int i = 0; i < table.getColumnCount();) {
			OdfTableCell cell = getCellByIndex(i);
			if (cell.isCoveredElement()) //cell is a cover cell
			{
				OdfTableCell coverCell = cell.getCoverCellInSameColumn();
				if (coverCell != null) {
					coverCell.setRowSpannedNumber(coverCell.getRowSpannedNumber() - getRowsRepeatedNumber());
				}
				getOdfElement().removeChild(cell.getOdfElement());
			} else {
				if (cell.getRowSpannedNumber() > 1) //cell is not a cover cell, and it span more rows
				{
					//split the cell under this cell to a single cell
					OdfTableRow nextRow = table.getRowByIndex(getRowIndex() + 1);
					if (nextRow.getRowsRepeatedNumber() > 1) {
						nextRow.splitRepeatedRows();
					}
					OdfTableCell coveredCell = table.getCellByPosition(cell.getColumnIndex(), getRowIndex() + 1);
					if (coveredCell.getColumnsRepeatedNumber() > 1) {
						coveredCell.splitRepeatedCells();
						coveredCell = table.getCellByPosition(cell.getColumnIndex(), getRowIndex() + 1);
					}

					//create a new cell
					TableTableCellElement newCellEle = (TableTableCellElement) cell.getOdfElement().cloneNode(true);
					newCellEle.setTableNumberRowsSpannedAttribute(cell.getRowSpannedNumber() - getRowsRepeatedNumber());
					//update repository
					int startRow = coveredCell.getRowIndex();
					int endRow = coveredCell.getRowIndex() + newCellEle.getTableNumberRowsSpannedAttribute();
					int startClm = coveredCell.getColumnIndex();
					int endClm = coveredCell.getColumnIndex() + newCellEle.getTableNumberColumnsSpannedAttribute() * newCellEle.getTableNumberColumnsRepeatedAttribute();
					coveredCell.getOdfElement().getParentNode().replaceChild(newCellEle, coveredCell.getOdfElement());

					table.updateRepositoryWhenCellElementChanged(startRow, endRow, startClm, endClm, newCellEle);
				}
			}
			i += cell.getColumnSpannedNumber();
		}
	}
}
