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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfElementFactory;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.doc.office.OdfOfficeBody;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.doc.style.OdfStyleTableCellProperties;
import org.odftoolkit.odfdom.doc.style.OdfStyleTableColumnProperties;
import org.odftoolkit.odfdom.doc.style.OdfStyleTableProperties;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.attribute.table.TableAlignAttribute;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfTableProperties;
import org.odftoolkit.odfdom.type.PositiveLength;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * OdfTable represents the table feature in an ODF document.
 * <p>
 * OdfTable provides methods to get/add/delete/modify table column/row/cell.
 *
 */
public class OdfTable {

	TableTableElement mTableElement;
	OdfDocument mDocument;
	String mTableName;
	boolean mIsProtected;
	private static final int DEFAULT_ROW_COUNT = 2;
	private static final int DEFAULT_COLUMN_COUNT = 5;
	private static final double DEFAULT_TABLE_WIDTH = 6;
	private static final int DEFAULT_REL_TABLE_WIDTH = 65535;
	private static final String DEFAULT_TABLE_ALIGN = "margins";
	// TODO: should save seperately for different dom tree
	static IdentityHashMap<TableTableElement, OdfTable> mTableRepository =
			new IdentityHashMap<TableTableElement, OdfTable>();
	IdentityHashMap<TableTableCellElementBase, Vector<OdfTableCell>> mCellRepository =
			new IdentityHashMap<TableTableCellElementBase, Vector<OdfTableCell>>();
	IdentityHashMap<TableTableRowElement, Vector<OdfTableRow>> mRowRepository =
			new IdentityHashMap<TableTableRowElement, Vector<OdfTableRow>>();
	IdentityHashMap<TableTableColumnElement, Vector<OdfTableColumn>> mColumnRepository =
			new IdentityHashMap<TableTableColumnElement, Vector<OdfTableColumn>>();

	private OdfTable(TableTableElement table) {
		mTableElement = table;
	}

	/**
	 * Get a table feature instance by an instance of <code>TableTableElement</code>.
	 * 
	 * @param odfElement	an instance of <code>TableTableElement</code>
	 * @return an instance of <code>OdfTable</code> that can represent <code>odfElement</code>
	 */
	public synchronized static OdfTable getInstance(TableTableElement odfElement) {
		if (mTableRepository.containsKey(odfElement)) {
			return mTableRepository.get(odfElement);
		} else {
			OdfTable newTable = new OdfTable(odfElement);
			mTableRepository.put(odfElement, newTable);
			return newTable;
		}
	}

	OdfTableCell getCellInstance(TableTableCellElementBase cell, int repeatedColIndex, int repeatedRowIndex) {
		if (mCellRepository.containsKey(cell)) {
			Vector<OdfTableCell> list = mCellRepository.get(cell);
			OdfTableCell fCell = null;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getOdfElement() == cell
						&& list.get(i).mnRepeatedColIndex == repeatedColIndex
						&& list.get(i).mnRepeatedRowIndex == repeatedRowIndex) {
					fCell = list.get(i);
					break;
				}
			}
			if (fCell == null) {
				fCell = new OdfTableCell(cell, repeatedColIndex, repeatedRowIndex);
				list.add(fCell);
			}
			return fCell;
		} else {
			OdfTableCell newCell = new OdfTableCell(cell, repeatedColIndex, repeatedRowIndex);
			Vector<OdfTableCell> list = new Vector<OdfTableCell>();
			list.add(newCell);
			mCellRepository.put(cell, list);
			return newCell;
		}
	}

	OdfTableRow getRowInstance(TableTableRowElement row, int repeatedRowIndex) {
		if (mRowRepository.containsKey(row)) {
			Vector<OdfTableRow> list = mRowRepository.get(row);
			if (list.size() <= repeatedRowIndex) {
				list.setSize(repeatedRowIndex + 1);
			}
			OdfTableRow fCell = list.get(repeatedRowIndex);
			if (fCell == null) {
				fCell = new OdfTableRow(row, repeatedRowIndex);
				list.set(repeatedRowIndex, fCell);
			}
			return fCell;
		} else {
			OdfTableRow newCell = new OdfTableRow(row, repeatedRowIndex);
			int size = (repeatedRowIndex > 7) ? (repeatedRowIndex + 1) : 8;
			Vector<OdfTableRow> list = new Vector<OdfTableRow>(size);
			list.setSize(repeatedRowIndex + 1);
			list.set(repeatedRowIndex, newCell);
			mRowRepository.put(row, list);
			return newCell;
		}
	}

	OdfTableColumn getColumnInstance(TableTableColumnElement col, int repeatedColIndex) {
		if (mColumnRepository.containsKey(col)) {
			Vector<OdfTableColumn> list = mColumnRepository.get(col);
			if (list.size() <= repeatedColIndex) {
				list.setSize(repeatedColIndex + 1);
			}
			OdfTableColumn fClm = list.get(repeatedColIndex);
			if (fClm == null) {
				fClm = new OdfTableColumn(col, repeatedColIndex);
				list.set(repeatedColIndex, fClm);
			}
			return fClm;
		} else {
			OdfTableColumn newCell = new OdfTableColumn(col, repeatedColIndex);
			int size = (repeatedColIndex > 7) ? (repeatedColIndex + 1) : 8;
			Vector<OdfTableColumn> list = new Vector<OdfTableColumn>(size);
			list.setSize(repeatedColIndex + 1);
			list.set(repeatedColIndex, newCell);
			mColumnRepository.put(col, list);
			return newCell;
		}
	}

	TableTableColumnElement getColumnElementByIndex(int colIndex) {
		int result = 0;
		TableTableColumnElement columnEle = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderColumnsElement) {
				TableTableHeaderColumnsElement headers = (TableTableHeaderColumnsElement) n;
				for (Node m : new DomNodeList(headers.getChildNodes())) {
					if (m instanceof TableTableColumnElement) {
						columnEle = (TableTableColumnElement) m;
						if (columnEle.getTableNumberColumnsRepeatedAttribute() == null) {
							result += 1;
						} else {
							result += columnEle.getTableNumberColumnsRepeatedAttribute();
						}
					}
					if (result > colIndex) {
						break;
					}
				}
			}
			if (n instanceof TableTableColumnElement) {
				columnEle = (TableTableColumnElement) n;
				if (columnEle.getTableNumberColumnsRepeatedAttribute() == null) {
					result += 1;
				} else {
					result += columnEle.getTableNumberColumnsRepeatedAttribute();
				}
			}
			if (result > colIndex) {
				break;
			}
		}
		return columnEle;
	}

	TableTableRowElement getRowElementByIndex(int rowIndex) {
		int result = 0;
		TableTableRowElement rowEle = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderRowsElement) {
				TableTableHeaderRowsElement headers = (TableTableHeaderRowsElement) n;
				for (Node m : new DomNodeList(headers.getChildNodes())) {
					if (m instanceof TableTableRowElement) {
						rowEle = (TableTableRowElement) m;
						result += rowEle.getTableNumberRowsRepeatedAttribute();
					}
					if (result > rowIndex) {
						break;
					}
				}
			}
			if (n instanceof TableTableRowElement) {
				rowEle = (TableTableRowElement) n;
				result += ((TableTableRowElement) n).getTableNumberRowsRepeatedAttribute();
			}
			if (result > rowIndex) {
				break;
			}
		}
		return rowEle;
	}

	/**
	 * Get the width of the table (in 1/100th mm).
	 * <p>
	 * Throw an UnsupportedOperationException if the 
	 * table is part of a spreadsheet document that does not have an attribute of table width,
	 * because that spreadsheet doesn't have an attribute of table width.
	 * 
	 * @return the width of the current table (in 1/100th mm).
	 * <p>
	 * An UnsupportedOperationException will be thrown if the table is in the spreadsheet document.
	 */
	public long getWidth() {
		OdfDocument doc = ((OdfFileDom) mTableElement.getOwnerDocument()).getOdfDocument();
		if (!(doc instanceof OdfSpreadsheetDocument)) {
			String sWidth = mTableElement.getProperty(OdfTableProperties.Width);
			return PositiveLength.parseLong(sWidth, Unit.MILLIMETER);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Set the width of the table (in 1/100th mm).
	 * <p>
	 * Throw an UnsupportedOperationException if the 
	 * table is part of a spreadsheet document that does not allow to change the table size,
	 * because spreadsheet is not allow user to set the table size.
	 * 
	 * @param width	the width that need to set (in 1/100th mm).
	 * <p>
	 * An UnsupportedOperationException will be thrown if the table is in the spreadsheet document.
	 */
	public void setWidth(long width) {
		OdfDocument doc = ((OdfFileDom) mTableElement.getOwnerDocument()).getOdfDocument();
		if (!(doc instanceof OdfSpreadsheetDocument)) {
			String sWidthMM = String.valueOf(width) + Unit.MILLIMETER.abbr();
			String sWidthIN = PositiveLength.mapToUnit(sWidthMM, Unit.INCH);
			mTableElement.setProperty(OdfTableProperties.Width, sWidthIN);
			//if the width is changed, we should also change the table:align properties if it is "margins"
			//otherwise the width seems not changed
			String alineStyle = mTableElement.getProperty(OdfStyleTableProperties.Align);
			if (TableAlignAttribute.Value.MARGINS.toString().equals(alineStyle)) {
				mTableElement.setProperty(OdfStyleTableProperties.Align, TableAlignAttribute.Value.LEFT.toString());
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	static void setLeftTopBorderStyleProperties(OdfStyle style) {
		style.setProperty(OdfStyleTableCellProperties.Padding, "0.0382in");
		style.setProperty(OdfStyleTableCellProperties.BorderLeft, "0.0007in solid #000000");
		style.setProperty(OdfStyleTableCellProperties.BorderRight, "none");
		style.setProperty(OdfStyleTableCellProperties.BorderTop, "0.0007in solid #000000");
		style.setProperty(OdfStyleTableCellProperties.BorderBottom, "0.0007in solid #000000");
	}

	static void setRightTopBorderStyleProperties(OdfStyle style) {
		style.setProperty(OdfStyleTableCellProperties.Padding, "0.0382in");
		style.setProperty(OdfStyleTableCellProperties.Border, "0.0007in solid #000000");
	}

	static void setLeftBottomBorderStylesProperties(OdfStyle style) {
		style.setProperty(OdfStyleTableCellProperties.Padding, "0.0382in");
		style.setProperty(OdfStyleTableCellProperties.BorderLeft, "0.0007in solid #000000");
		style.setProperty(OdfStyleTableCellProperties.BorderRight, "none");
		style.setProperty(OdfStyleTableCellProperties.BorderTop, "none");
		style.setProperty(OdfStyleTableCellProperties.BorderBottom, "0.0007in solid #000000");

	}

	static void setRightBottomBorderStylesProperties(OdfStyle style) {
		style.setProperty(OdfStyleTableCellProperties.Padding, "0.0382in");
		style.setProperty(OdfStyleTableCellProperties.Border, "0.0007in solid #000000");
		style.setProperty(OdfStyleTableCellProperties.BorderTop, "none");
		style.setProperty(OdfStyleTableCellProperties.BorderBottom, "0.0007in solid #000000");
	}

	private static TableTableElement createTable(OdfDocument document, int numRows, int numCols, int headerRowNumber, int headerColumnNumber) throws Exception {
		// check arguments
		if (numRows < 1 || numCols < 1 || headerRowNumber < 0
				|| headerColumnNumber < 0 || headerRowNumber > numRows
				|| headerColumnNumber > numCols) {
			throw new IllegalArgumentException("Can not create table with the given parameters:\n"
					+ "Rows " + numRows + ", Columns " + numCols + ", HeaderRows " + headerRowNumber + ", HeaderColumns " + headerColumnNumber);
		}
		OdfFileDom dom = document.getContentDom();
		OdfOfficeAutomaticStyles styles = dom.getAutomaticStyles();
		//1. create table element
		TableTableElement newTEle = (TableTableElement) OdfElementFactory.newOdfElement(dom,
				OdfName.newName(OdfNamespaceNames.TABLE, "table"));
		String tablename = getUniqueTableName(document);
		newTEle.setTableNameAttribute(tablename);
		//create style
		OdfStyle tableStyle = styles.newStyle(OdfStyleFamily.Table);
		tableStyle.setStyleNameAttribute(tablename);
		tableStyle.setProperty(OdfStyleTableProperties.Width, DEFAULT_TABLE_WIDTH + "in");
		tableStyle.setProperty(OdfStyleTableProperties.Align, DEFAULT_TABLE_ALIGN);
		newTEle.setStyleName(tablename);

		// 2. create column elements
		// 2.0 create column style
		String columnStylename = tablename + ".A";
		OdfStyle columnStyle = styles.newStyle(OdfStyleFamily.TableColumn);
		columnStyle.setStyleNameAttribute(columnStylename);
		columnStyle.setProperty(OdfStyleTableColumnProperties.ColumnWidth,
				new DecimalFormat("000.0000").format(DEFAULT_TABLE_WIDTH / numCols) + "in");
		columnStyle.setProperty(OdfStyleTableColumnProperties.RelColumnWidth, Math.round(DEFAULT_REL_TABLE_WIDTH / numCols) + "*");
		// 2.1 create header column elements
		if (headerColumnNumber > 0) {
			TableTableHeaderColumnsElement headercolumns = (TableTableHeaderColumnsElement) OdfElementFactory.newOdfElement(dom, OdfName.newName(OdfNamespaceNames.TABLE, "table-header-columns"));
			TableTableColumnElement headercolumn = (TableTableColumnElement) OdfElementFactory.newOdfElement(dom, OdfName.newName(OdfNamespaceNames.TABLE, "table-column"));
			headercolumn.setTableNumberColumnsRepeatedAttribute(headerColumnNumber);
			headercolumns.appendChild(headercolumn);
			newTEle.appendChild(headercolumns);
			headercolumn.setStyleName(columnStylename);
		}
		//2.2 create common column elements
		TableTableColumnElement columns = (TableTableColumnElement) OdfElementFactory.newOdfElement(dom,
				OdfName.newName(OdfNamespaceNames.TABLE, "table-column"));
		columns.setTableNumberColumnsRepeatedAttribute(numCols - headerColumnNumber);
		columns.setStyleName(columnStylename);
		newTEle.appendChild(columns);

		//3. create row elements
		//3.0 create 4 kinds of styles
		OdfStyle lefttopStyle = styles.newStyle(OdfStyleFamily.TableCell);
		String lefttopStylename = tablename + ".A1";
		lefttopStyle.setStyleNameAttribute(lefttopStylename);
		setLeftTopBorderStyleProperties(lefttopStyle);

		OdfStyle leftbottomStyle = styles.newStyle(OdfStyleFamily.TableCell);
		String leftbottomStylename = tablename + ".A2";
		leftbottomStyle.setStyleNameAttribute(leftbottomStylename);
		setLeftBottomBorderStylesProperties(leftbottomStyle);

		OdfStyle righttopStyle = styles.newStyle(OdfStyleFamily.TableCell);
		String righttopStylename = tablename + ".B1";
		righttopStyle.setStyleNameAttribute(righttopStylename);
		setRightTopBorderStyleProperties(righttopStyle);

		OdfStyle rightbottomStyle = styles.newStyle(OdfStyleFamily.TableCell);
		String rightbottomStylename = tablename + ".B2";
		rightbottomStyle.setStyleNameAttribute(rightbottomStylename);
		setRightBottomBorderStylesProperties(rightbottomStyle);

		//3.1 create header row elements
		TableTableHeaderRowsElement headerrows = (TableTableHeaderRowsElement) OdfElementFactory.newOdfElement(dom,
				OdfName.newName(OdfNamespaceNames.TABLE, "table-header-rows"));
		for (int i = 0; i < headerRowNumber; i++) {
			TableTableRowElement aRow = (TableTableRowElement) OdfElementFactory.newOdfElement(dom,
					OdfName.newName(OdfNamespaceNames.TABLE, "table-row"));
			for (int j = 0; j < numCols; j++) {
				TableTableCellElement aCell = (TableTableCellElement) OdfElementFactory.newOdfElement(dom,
						OdfName.newName(OdfNamespaceNames.TABLE, "table-cell"));
				TextPElement aParagraph = (TextPElement) OdfElementFactory.newOdfElement(dom,
						OdfName.newName(OdfNamespaceNames.TEXT, "p"));
				aCell.appendChild(aParagraph);
				if ((j + 1 == numCols) && (i == 0)) {
					aCell.setStyleName(righttopStylename);
				} else if (i == 0) {
					aCell.setStyleName(lefttopStylename);
				} else if ((j + 1 == numCols) && (i > 0)) {
					aCell.setStyleName(rightbottomStylename);
				} else {
					aCell.setStyleName(leftbottomStylename);
				}
				aRow.appendChild(aCell);
			}
			headerrows.appendChild(aRow);
		}
		newTEle.appendChild(headerrows);

		//3.2 create common row elements
		for (int i = headerRowNumber; i < numRows; i++) {
			TableTableRowElement aRow = (TableTableRowElement) OdfElementFactory.newOdfElement(dom,
					OdfName.newName(OdfNamespaceNames.TABLE, "table-row"));
			for (int j = 0; j < numCols; j++) {
				TableTableCellElement aCell = (TableTableCellElement) OdfElementFactory.newOdfElement(dom,
						OdfName.newName(OdfNamespaceNames.TABLE, "table-cell"));
				TextPElement aParagraph = (TextPElement) OdfElementFactory.newOdfElement(dom,
						OdfName.newName(OdfNamespaceNames.TEXT, "p"));
				aCell.appendChild(aParagraph);
				if ((j + 1 == numCols) && (i == 0)) {
					aCell.setStyleName(righttopStylename);
				} else if (i == 0) {
					aCell.setStyleName(lefttopStylename);
				} else if ((j + 1 == numCols) && (i > 0)) {
					aCell.setStyleName(rightbottomStylename);
				} else {
					aCell.setStyleName(leftbottomStylename);
				}
				aRow.appendChild(aCell);
			}
			newTEle.appendChild(aRow);
		}

		return newTEle;
	}

	/**
	 * Construct the <code>OdfTable</code> feature.
	 * The default column count is 5.
	 * The default row count is 2.
	 * <p>
	 * The table will be inserted at the end of the document.
	 * 
	 * @param document	the ODF document that contains this feature 
	 * @return the created <code>OdfTable</code> feature instance
	 */
	public static OdfTable newTable(OdfDocument document) {
		try {
			TableTableElement newTEle = createTable(document, DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT, 0, 0);

			//4. append to the end of document
			OdfElement root = document.getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);
			typedContent.appendChild(newTEle);

			return OdfTable.getInstance(newTEle);

		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private static String getUniqueTableName(OdfDocument document) {
		List<OdfTable> tableList = document.getTableList();
		boolean notUnique = true;

		String tablename = "Table" + (tableList.size() + 1);

		while (notUnique) {
			notUnique = false;
			for (int i = 0; i < tableList.size(); i++) {
				if (tableList.get(i).getTableName().equalsIgnoreCase(tablename)) {
					notUnique = true;
					break;
				}
			}
			if (notUnique) {
				tablename = tablename + Math.round(Math.random() * 10);
			}
		}

		return tablename;

	}

	/**
	 * Construct the <code>OdfTable</code> feature
	 * with a specified row number and column number.
	 * <p>
	 * The table will be inserted at the end of the document.
	 * 
	 * @param document	the ODF document that contains this feature 
	 * @param numRows	the row number
	 * @param numCols	the column number
	 * @return a new instance of <code>OdfTable</code>
	 */
	public static OdfTable newTable(OdfDocument document, int numRows, int numCols) {
		try {
			TableTableElement newTEle = createTable(document, numRows, numCols, 0, 0);

			//4. append to the end of document
			OdfElement root = document.getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);
			typedContent.appendChild(newTEle);

			return OdfTable.getInstance(newTEle);

		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the <code>OdfTable</code> feature
	 * with a specified row number, column number, header row number, header column number.
	 * <p>
	 * The table will be inserted at the end of the document.
	 * 
	 * @param document	the ODF document that contains this feature 
	 * @param numRows	the row number
	 * @param numCols	the column number
	 * @param headerRowNumber	the header row number
	 * @param headerColumnNumber	the header column number
	 * @return a new instance of <code>OdfTable</code>
	 * */
	public static OdfTable newTable(OdfDocument document, int numRows, int numCols, int headerRowNumber, int headerColumnNumber) {
		try {
			TableTableElement newTEle = createTable(document, numRows, numCols, headerRowNumber, headerColumnNumber);

			//4. append to the end of document
			OdfElement root = document.getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);
			typedContent.appendChild(newTEle);

			return OdfTable.getInstance(newTEle);

		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the OdfTable feature
	 * with a specified 2 dimension array as the data of this table.
	 * The value type of each cell is float.
	 * <p>
	 * The table will be inserted at the end of the document.
	 * 
	 * @param document	the ODF document that contains this feature
	 * @param rowLabel	set as the header row, it can be null if no header row needed
	 * @param columnLabel	set as the header column, it can be null if no header column needed
	 * @param data	the two dimension array of double as the data of this table
	 * @return a new instance of <code>OdfTable</code>
	 */
	public static OdfTable newTable(OdfDocument document, String[] rowLabel, String[] columnLabel, double[][] data) {
		//fix bug121
		int rowNumber = DEFAULT_ROW_COUNT;
		int columnNumber = DEFAULT_COLUMN_COUNT;
		if (data != null) {
			rowNumber = data.length;
			columnNumber = data[0].length;
		}
		int rowHeaders = 0, columnHeaders = 0;

		if (rowLabel != null) {
			rowHeaders = 1;
		}
		if (columnLabel != null) {
			columnHeaders = 1;
		}

		try {
			TableTableElement newTEle = createTable(document, rowNumber + rowHeaders, columnNumber + columnHeaders, rowHeaders, columnHeaders);

			//4. append to the end of document
			OdfElement root = document.getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);
			typedContent.appendChild(newTEle);

			OdfTable table = OdfTable.getInstance(newTEle);
			List<OdfTableRow> rowList = table.getRowList();
			for (int i = 0; i < rowNumber + rowHeaders; i++) {
				OdfTableRow row = rowList.get(i);
				for (int j = 0; j < columnNumber + columnHeaders; j++) {
					if ((i == 0) && (j == 0)) {
						continue;
					}
					OdfTableCell cell = row.getCellByIndex(j);
					if (i == 0 && columnLabel != null) //first row, should fill column labels
					{
						if (j <= columnLabel.length) {
							cell.setStringValue(columnLabel[j - 1]);
						} else {
							cell.setStringValue("");
						}
					} else if (j == 0 && rowLabel != null) //first column, should fill row labels
					{
						if (i <= rowLabel.length) {
							cell.setStringValue(rowLabel[i - 1]);
						} else {
							cell.setStringValue("");
						}
					} else {//data
						//fix bug121
						if ((data != null) && (i >= rowHeaders) && (j >= columnHeaders)) {
							cell.setDoubleValue(data[i - rowHeaders][j - columnHeaders]);
						}
					}
				}
			}

			return table;

		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the OdfTable feature
	 * with a specified 2 dimension array as the data of this table.
	 * The value type of each cell is string.
	 * <p>
	 * The table will be inserted at the end of the document.
	 * @param document	the ODF document that contains this feature
	 * @param rowLabel	set as the header row, it can be null if no header row needed
	 * @param columnLabel	set as the header column, it can be null if no header column needed
	 * @param data	the two dimension array of string as the data of this table
	 * @return a new instance of <code>OdfTable</code>
	 */
	public static OdfTable newTable(OdfDocument document, String[] rowLabel, String[] columnLabel, String[][] data) {
		//fix bug121 
		int rowNumber = DEFAULT_ROW_COUNT;
		int columnNumber = DEFAULT_COLUMN_COUNT;
		if (data != null) {
			rowNumber = data.length;
			columnNumber = data[0].length;
		}
		int rowHeaders = 0, columnHeaders = 0;

		if (rowLabel != null) {
			rowHeaders = 1;
		}
		if (columnLabel != null) {
			columnHeaders = 1;
		}

		try {
			TableTableElement newTEle = createTable(document, rowNumber + rowHeaders, columnNumber + columnHeaders, rowHeaders, columnHeaders);

			//4. append to the end of document
			OdfElement root = document.getContentDom().getRootElement();
			OdfOfficeBody officeBody = OdfElement.findFirstChildNode(OdfOfficeBody.class, root);
			OdfElement typedContent = OdfElement.findFirstChildNode(OdfElement.class, officeBody);
			typedContent.appendChild(newTEle);

			OdfTable table = OdfTable.getInstance(newTEle);
			List<OdfTableRow> rowList = table.getRowList();
			for (int i = 0; i < rowNumber + rowHeaders; i++) {
				OdfTableRow row = rowList.get(i);
				for (int j = 0; j < columnNumber + columnHeaders; j++) {
					if ((i == 0) && (j == 0)) {
						continue;
					}
					OdfTableCell cell = row.getCellByIndex(j);
					if (i == 0 && columnLabel != null) //first row, should fill column labels
					{
						if (j <= columnLabel.length) {
							cell.setStringValue(columnLabel[j - 1]);
						} else {
							cell.setStringValue("");
						}
					} else if (j == 0 && rowLabel != null) //first column, should fill row labels
					{
						if (i <= rowLabel.length) {
							cell.setStringValue(rowLabel[i - 1]);
						} else {
							cell.setStringValue("");
						}
					} else {
						//fix bug121
						if ((data != null) && (i >= rowHeaders) && (j >= columnHeaders)) {
							cell.setStringValue(data[i - rowHeaders][j - columnHeaders]);
						}
					}
				}
			}

			return table;

		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get the row count of this table.
	 * 
	 * @return total count of rows
	 */
	public int getRowCount() {
		int result = 0;

		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderRowsElement) {
				result += getHeaderRowCount((TableTableHeaderRowsElement) n);
			}
			if (n instanceof TableTableRowElement) {
				result += ((TableTableRowElement) n).getTableNumberRowsRepeatedAttribute();
			}
		}
		return result;
	}

	/**
	 * Get the column count of this table.
	 * 
	 * @return total count of columns
	 */
	public int getColumnCount() {
		int result = 0;

		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderColumnsElement) {
				result += getHeaderColumnCount((TableTableHeaderColumnsElement) n);
			}
			if (n instanceof TableTableColumnElement) {
				result += getColumnInstance((TableTableColumnElement) n, 0).getColumnsRepeatedNumber();
			}
		}
		return result;
	}

	/**
	 * This method is invoked by appendRow.
	 * When a table has no row, the first row is a default row.
	 */
	private TableTableRowElement createDefaultRow(int columnCount) {
		OdfFileDom dom = (OdfFileDom) mTableElement.getOwnerDocument();
		//3. create row elements
		//3.0 create 4 kinds of styles
		OdfStyle lefttopStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
		setLeftTopBorderStyleProperties(lefttopStyle);

		OdfStyle righttopStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
		setRightTopBorderStyleProperties(righttopStyle);

		//3.1 create header row elements
		TableTableRowElement aRow = (TableTableRowElement) OdfElementFactory.newOdfElement(dom,
				OdfName.newName(OdfNamespaceNames.TABLE, "table-row"));
		for (int j = 0; j < columnCount; j++) {
			TableTableCellElement aCell = (TableTableCellElement) OdfElementFactory.newOdfElement(dom,
					OdfName.newName(OdfNamespaceNames.TABLE, "table-cell"));
			TextPElement aParagraph = (TextPElement) OdfElementFactory.newOdfElement(dom,
					OdfName.newName(OdfNamespaceNames.TEXT, "p"));
			aCell.appendChild(aParagraph);
			if (j + 1 == columnCount) {
				aCell.setStyleName(righttopStyle.getStyleNameAttribute());
			} else {
				aCell.setStyleName(lefttopStyle.getStyleNameAttribute());
			}
			aRow.appendChild(aCell);
		}

		return aRow;
	}

	/**
	 * Append a row to the end of the table.
	 * The style of new row is same with the last row in the table.
	 * 
	 * @return a new appended row
	 */
	public OdfTableRow appendRow() {
		int columnCount = getColumnCount();
		List<OdfTableRow> rowList = getRowList();

		if (rowList.size() == 0) //no row, create a default row
		{
			TableTableRowElement newRow = createDefaultRow(columnCount);
			mTableElement.appendChild(newRow);

			return getRowInstance(newRow, 0);
		} else {
			OdfTableRow refRow = rowList.get(rowList.size() - 1);
			OdfTableRow newRow = insertRowBefore(refRow, null);
			return newRow;
		}
	}

	/**
	 * Append a specific number of rows to the end of the table.
	 * The style of new rows are same with the last row in the table.
	 * 
	 * @param rowCount	is the number of rows to be appended.
	 * @return a list of new appended rows
	 */
	public List<OdfTableRow> appendRows(int rowCount) {
		List<OdfTableRow> resultList = new ArrayList<OdfTableRow>();
		if (rowCount <= 0) {
			return resultList;
		}
		OdfTableRow firstRow = appendRow();
		resultList.add(firstRow);
		if (rowCount == 1) {
			return resultList;
		}
		List<OdfTableRow> list = insertRowsBefore((getRowCount() - 1), (rowCount - 1));
		resultList.addAll(list);
		return resultList;
	}

	/**
	 * Append a column to the right of the table.
	 * The style of new column is same with the rightmost column in the table.
	 * 
	 * @return a new appended column
	 */
	public OdfTableColumn appendColumn() {
		List<OdfTableColumn> columnList = getColumnList();
		int columnCount = columnList.size();

		TableTableColumnElement newColumn;
		OdfElement positonElement = getRowElementByIndex(0);
		if (positonElement.getParentNode() instanceof TableTableHeaderRowsElement) {
			positonElement = (OdfElement) positonElement.getParentNode();
		}

		if (columnList.size() == 0) //no column, create a new column
		{
			String columnStylename = getTableName() + ".A";
			OdfStyle columnStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableColumn);
			columnStyle.setStyleNameAttribute(columnStylename);
			columnStyle.setProperty(OdfStyleTableColumnProperties.ColumnWidth, DEFAULT_TABLE_WIDTH + "in");
			columnStyle.setProperty(OdfStyleTableColumnProperties.RelColumnWidth, DEFAULT_REL_TABLE_WIDTH + "*");

			newColumn = (TableTableColumnElement) OdfElementFactory.newOdfElement((OdfFileDom) mTableElement.getOwnerDocument(),
					OdfName.newName(OdfNamespaceNames.TABLE, "table-column"));
			//newColumn.setTableNumberColumnsRepeatedAttribute(1);
			newColumn.setStyleName(columnStylename);
			mTableElement.insertBefore(newColumn, positonElement);
		} else { //has column, append a same column as the last one.
			TableTableColumnElement refColumn = columnList.get(columnList.size() - 1).getOdfElement();
			newColumn = (TableTableColumnElement) refColumn.cloneNode(true);
			newColumn.setTableNumberColumnsRepeatedAttribute(1);//chagne to remove attribute
			//newColumn.re
			mTableElement.insertBefore(newColumn, positonElement);
		}

		//insert cells
		List<OdfTableRow> rowList = getRowList();
		for (int i = 0; i < rowList.size();) {
			OdfTableRow row1 = rowList.get(i);
			row1.insertCellBefore(row1.getCellByIndex(columnCount - 1), null);
			i = i + row1.getRowsRepeatedNumber();
		}
		return getColumnInstance(newColumn, 0);
	}

	/**
	 * Append a specific number of columns to the right of the table.
	 * The style of new columns are same with the rightmost column in the table.
	 * 
	 * @param clmCount	is the number of columns to be appended.
	 * @return a list of new appended columns
	 */
	public List<OdfTableColumn> appendColumns(int clmCount) {
		List<OdfTableColumn> resultList = new ArrayList<OdfTableColumn>();
		if (clmCount <= 0) {
			return resultList;
		}
		OdfTableColumn firstClm = appendColumn();
		resultList.add(firstClm);
		if (clmCount == 1) {
			return resultList;
		}
		List<OdfTableColumn> list = insertColumnsBefore((getColumnCount() - 1), (clmCount - 1));
		resultList.addAll(list);
		return resultList;
	}

	/**
	 * This method is to insert a numbers of row  
	 */
	private List<OdfTableRow> insertMultipleRowBefore(OdfTableRow refRow, OdfTableRow positionRow, int count) {
		List<OdfTableRow> resultList = new ArrayList<OdfTableRow>();
		int j = 1;

		if (count <= 0) {
			return resultList;
		}

		OdfTableRow firstRow = insertRowBefore(refRow, positionRow);
		resultList.add(firstRow);

		if (count == 1) {
			return resultList;
		}

		TableTableRowElement rowEle = firstRow.getOdfElement();
		for (int i = 0; i < getColumnCount();) {
			OdfTableCell refCell = refRow.getCellByIndex(i);
			if (!refCell.isCoveredElement()) {
				int coveredHeigth = refCell.getRowSpannedNumber();
				if (coveredHeigth > 1) {
					refCell.setRowSpannedNumber(coveredHeigth + 1);
				}
			}
			i += refCell.getColumnsRepeatedNumber();
		}
		firstRow.setRowsRepeatedNumber(count);
		while (j < count) {
			resultList.add(getRowInstance(rowEle, j));
			j++;
		}
		return resultList;
	}

	private OdfTableRow insertRowBefore(OdfTableRow refRow, OdfTableRow positionRow) //only insert one Row
	{
		int columnCount = getColumnCount();
		TableTableRowElement aRow = (TableTableRowElement) OdfElementFactory.newOdfElement((OdfFileDom) mTableElement.getOwnerDocument(),
				OdfName.newName(OdfNamespaceNames.TABLE, "table-row"));
		int coveredLength = 0, coveredHeigth = 0;
		for (int i = 0; i < columnCount;) {
			OdfTableCell refCell = refRow.getCellByIndex(i);
			if (!refCell.isCoveredElement()) //not cover element
			{
				TableTableCellElement aCellEle = (TableTableCellElement) refCell.getOdfElement();
				coveredHeigth = aCellEle.getTableNumberRowsSpannedAttribute();
				if (coveredHeigth == 1) {
					TableTableCellElement newCellEle = (TableTableCellElement) aCellEle.cloneNode(true);
					cleanCell(newCellEle);
					aRow.appendChild(newCellEle);
				} else { //cover more rows
					aCellEle.setTableNumberRowsSpannedAttribute(coveredHeigth + 1);
					TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) OdfElementFactory.newOdfElement(
							(OdfFileDom) mTableElement.getOwnerDocument(),
							OdfName.newName(OdfNamespaceNames.TABLE, "covered-table-cell"));
					newCellEle.setTableNumberColumnsRepeatedAttribute(refCell.getColumnsRepeatedNumber());
					aRow.appendChild(newCellEle);
				}

				coveredLength = aCellEle.getTableNumberColumnsSpannedAttribute() - refCell.getColumnsRepeatedNumber();
				i = i + refCell.getColumnsRepeatedNumber();
			} else {
				TableCoveredTableCellElement aCellEle = (TableCoveredTableCellElement) refCell.getOdfElement();
				if (coveredLength >= 1) {
					TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) aCellEle.cloneNode(true);
					//newCellEle.removeAttributeNS(OdfNamespaceNames.TABLE.getNamespaceUri(), "number-columns-repeated");
					aRow.appendChild(newCellEle);
					coveredLength -= newCellEle.getTableNumberColumnsRepeatedAttribute();
				} else {
					TableTableCellElement coveredCell = (TableTableCellElement) refCell.getCoverCell().getOdfElement();
					TableTableCellElement newCellEle = (TableTableCellElement) coveredCell.cloneNode(true);
					cleanCell(newCellEle);
					newCellEle.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "number-rows-spanned");
					aRow.appendChild(newCellEle);

					coveredLength = coveredCell.getTableNumberColumnsSpannedAttribute() - refCell.getColumnsRepeatedNumber();
				}
				i = i + refCell.getColumnsRepeatedNumber();
			}
		}
		if (positionRow == null) {
			mTableElement.appendChild(aRow);
		} else {
			mTableElement.insertBefore(aRow, positionRow.getOdfElement());
		}

		return getRowInstance(aRow, 0);
	}

	void cleanCell(TableTableCellElement newCellEle) {
		newCellEle.removeAttributeNS(OdfNamespaceNames.OFFICE.getUri(), "value");
		newCellEle.removeAttributeNS(OdfNamespaceNames.OFFICE.getUri(), "date-value");
		newCellEle.removeAttributeNS(OdfNamespaceNames.OFFICE.getUri(), "time-value");
		newCellEle.removeAttributeNS(OdfNamespaceNames.OFFICE.getUri(), "boolean-value");
		newCellEle.removeAttributeNS(OdfNamespaceNames.OFFICE.getUri(), "string-value");
		newCellEle.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "formula");
		//newCellEle.removeAttributeNS(OdfNamespaceNames.TABLE.getNamespaceUri(), "number-rows-spanned");
		//newCellEle.removeAttributeNS(OdfNamespaceNames.TABLE.getNamespaceUri(), "number-columns-repeated");
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

	/**
	 * Return an instance of <code>TableTableElement</code> which represents this feature.
	 * 
	 * @return an instance of <code>TableTableElement</code>
	 */
	public TableTableElement getOdfElement() {
		return mTableElement;
	}

	/** 
	 * Insert a specific number of columns before the column whose index is <code>index</code>.
	 * 
	 * @param index	is the index of the column to insert before.
	 * @param clmCount	is the number of columns to insert.
	 * @return a list of new inserted columns
	 */
	public List<OdfTableColumn> insertColumnsBefore(int index, int clmCount) {
		OdfTableColumn refColumn, positionCol;
		ArrayList<OdfTableColumn> list = new ArrayList<OdfTableColumn>();
		int columncount = getColumnCount();

		if (index >= columncount) {
			throw new IndexOutOfBoundsException();
		}

		if (index == 0) {
			int iRowCount = getRowCount();
			for (int i = 0; i < iRowCount; i++) {
				OdfTableRow row = getRowByIndex(i);
				row.insertCellByIndex(index, clmCount);
			}

			refColumn = getColumnByIndex(index);
			positionCol = refColumn;

			TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement().cloneNode(true);
			newColumnEle.setTableNumberColumnsRepeatedAttribute(new Integer(clmCount));
			mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());

			for (int i = 0; i < clmCount; i++) {
				list.add(getColumnInstance(newColumnEle, i));
			}
			return list;
		}

		//1. insert the cell
		int iRowCount = getRowCount();
		for (int i = iRowCount - 1; i >= 0;) {
			OdfTableRow row = getRowByIndex(i);
			//row.insertCellByIndex((int)nIndex, (int) nCount);
			OdfTableCell refCell = row.getCellByIndex(index - 1);
			OdfTableCell positionCell = null;
			positionCell = row.getCellByIndex(index);
//			for(int j=0;j<nCount;j++)
//			{
//				positionCell = row.insertCellBefore(refCell, positionCell);
//			}
			row.insertCellBefore(refCell, positionCell, clmCount);
			i = i - row.getRowsRepeatedNumber();
		}

		refColumn = getColumnByIndex(index - 1);
		positionCol = getColumnByIndex(index);
		//2. insert a <table:table-column>
		if (refColumn.getOdfElement() == positionCol.getOdfElement()) {
			TableTableColumnElement column = refColumn.getOdfElement();
			int repeatedCount = getColumnInstance(column, 0).getColumnsRepeatedNumber();
			getColumnInstance(column, 0).setColumnsRepeatedNumber((repeatedCount + clmCount));
			TableTableColumnElement columnEle = positionCol.getOdfElement();
			OdfTableColumn startCol = getColumnInstance(positionCol.getOdfElement(), 0);
			for (int i = repeatedCount + clmCount - 1; i >= clmCount + (index - startCol.getColumnIndex()); i--) {
				updateColumnRepository(columnEle, i - clmCount, columnEle, i);
			}
			for (int i = 0; i < clmCount; i++) {
				list.add(getColumnInstance(column, refColumn.mnRepeatedIndex + 1 + i));
			}
		} else {
			TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement().cloneNode(true);
			newColumnEle.setTableNumberColumnsRepeatedAttribute(new Integer(clmCount));
			mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());

			for (int i = 0; i < clmCount; i++) {
				list.add(getColumnInstance(newColumnEle, i));
			}
		}

		return list;
	}

	/** 
	 * Remove a specific number of columns, starting from the column at <code>index</code>.
	 * 
	 * @param startindex
	 * 				is the index of the first column to delete.
	 * @param clmCount
	 * 				is the number of columns to delete.
	 */
	public void removeColumnsByIndex(int startindex, int clmCount) {
		//System.out.println(getTableName());
		//0. verify the index
		int columncount = getColumnCount();
		if (startindex >= columncount) {
			throw new IndexOutOfBoundsException("Start index out of bound");
		}
		if (startindex + clmCount >= columncount) {
			clmCount = columncount - startindex;
		}

		//1. remove cell
		//for(int i=getRowCount()-1;i>=0;i--)
		for (int i = 0; i < getRowCount(); i++) {
			OdfTableRow aRow = getRowByIndex(i);
			//aRow.removeCellByIndex(nIndex, nCount);
			aRow.removeCellByIndex(startindex, clmCount);
		}

		//2. remove column
		OdfTableColumn firstColumn;
		for (int i = 0; i < clmCount; i++) {
			firstColumn = getColumnByIndex(startindex);
			int repeatedAttr = firstColumn.getColumnsRepeatedNumber();
			if (repeatedAttr == 1) {
				TableTableColumnElement columnEle = OdfElement.findNextChildNode(TableTableColumnElement.class, firstColumn.getOdfElement());
				mTableElement.removeChild(firstColumn.getOdfElement());
				if (i < (clmCount - 1)) {
					firstColumn = this.getColumnInstance(columnEle, 0);
				}
			} else {
				if (repeatedAttr > firstColumn.mnRepeatedIndex) {
					firstColumn.setColumnsRepeatedNumber(repeatedAttr - 1);
					OdfTableColumn startCol = this.getColumnInstance(firstColumn.getOdfElement(), 0);
					updateColumnRepository(firstColumn.getOdfElement(), startindex - startCol.getColumnIndex(), null, 0);
				}
			}
		}

	}

	private void reviseStyleFromTopRowToMediumRow(OdfTableRow oldTopRow) {
		int length = getColumnCount();
		OdfStyle leftbottomStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
		setLeftBottomBorderStylesProperties(leftbottomStyle);

		OdfStyle rightbottomStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
		setRightBottomBorderStylesProperties(rightbottomStyle);

		for (int i = 0; i < length;) {
			OdfTableCell cell = oldTopRow.getCellByIndex(i);
			if (cell.isCoveredElement()) {
				i = i + cell.getColumnsRepeatedNumber();
				continue;
			}
			if (i < length - 1) {
				cell.getOdfElement().setStyleName(leftbottomStyle.getStyleNameAttribute());
			} else {
				cell.getOdfElement().setStyleName(rightbottomStyle.getStyleNameAttribute());
			}
			i = i + cell.getColumnsRepeatedNumber();
		}
	}

	private void reviseStyleFromMediumRowToTopRow(OdfTableRow newTopRow) {
		int length = getColumnCount();
		OdfStyle lefttopStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
		setLeftTopBorderStyleProperties(lefttopStyle);

		OdfStyle righttopStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
		setRightTopBorderStyleProperties(righttopStyle);

		for (int i = 0; i < length;) {
			OdfTableCell cell = newTopRow.getCellByIndex(i);
			if (cell.isCoveredElement()) {
				i = i + cell.getColumnsRepeatedNumber();
				continue;
			}
			if (i < length - 1) {
				cell.getOdfElement().setStyleName(lefttopStyle.getStyleNameAttribute());
			} else {
				cell.getOdfElement().setStyleName(righttopStyle.getStyleNameAttribute());
			}
			i = i + cell.getColumnsRepeatedNumber();
		}
	}

	/**
	 * Insert a specific number of rows before the row at <code>index</code>.
	 * 
	 * @param index	is the index of the row to insert before.
	 * @param rowCount	is the number of rows to insert.
	 * @return a list of new inserted rows
	 */
	public List<OdfTableRow> insertRowsBefore(int index, int rowCount) {
		if (index >= getRowCount()) {
			throw new IndexOutOfBoundsException();
		}

		ArrayList<OdfTableRow> list = new ArrayList<OdfTableRow>();

		if (index == 0) {
			OdfTableRow refRow = getRowByIndex(index);
			OdfTableRow positionRow = refRow;
			//add first row
			OdfTableRow newFirstRow = insertRowBefore(refRow, positionRow);
			reviseStyleFromTopRowToMediumRow(refRow);
			list.add(newFirstRow);
			List<OdfTableRow> rowList = insertMultipleRowBefore(refRow, refRow, rowCount - 1);
			for (int i = 0; i < rowList.size(); i++) {
				list.add(rowList.get(i));
			}
			return list;
		}

		OdfTableRow refRow = getRowByIndex(index - 1);
		OdfTableRow positionRow = getRowByIndex(index);
		//1. insert a <table:table-column>
		if (refRow.getOdfElement() == positionRow.getOdfElement()) {
			TableTableRowElement row = refRow.getOdfElement();
			int repeatedCount = refRow.getRowsRepeatedNumber();
			refRow.setRowsRepeatedNumber(repeatedCount + rowCount);
			TableTableRowElement rowEle = positionRow.getOdfElement();
			OdfTableRow startRow = getRowInstance(positionRow.getOdfElement(), 0);
			for (int i = repeatedCount + rowCount - 1; i >= rowCount + (index - startRow.getRowIndex()); i--) {
				updateRowRepository(rowEle, i - rowCount, rowEle, i);
			}
			for (int i = 0; i < rowCount; i++) {
				list.add(getRowInstance(row, refRow.mnRepeatedIndex + 1 + i));
			}
		} else {
			List<OdfTableRow> newRowList = insertMultipleRowBefore(refRow, positionRow, rowCount);
			if (index - 1 == 0) {
				//correct styles
				reviseStyleFromTopRowToMediumRow(newRowList.get(0));
			}
			for (int i = 0; i < newRowList.size(); i++) {
				list.add(newRowList.get(i));
			}
		}

		return list;
	}

	/**
	 * Return a list of columns in the current table.
	 * 
	 * @return a list of table columns
	 */
	public List<OdfTableColumn> getColumnList() {
		ArrayList<OdfTableColumn> list = new ArrayList<OdfTableColumn>();
		TableTableColumnElement colEle = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderColumnsElement) {
				TableTableHeaderColumnsElement headers = (TableTableHeaderColumnsElement) n;
				for (Node m : new DomNodeList(headers.getChildNodes())) {
					if (m instanceof TableTableColumnElement) {
						colEle = (TableTableColumnElement) m;
						for (int i = 0; i < getColumnInstance(colEle, 0).getColumnsRepeatedNumber(); i++) {
							list.add(getColumnInstance(colEle, i));
						}
					}
				}
			}
			if (n instanceof TableTableColumnElement) {
				colEle = (TableTableColumnElement) n;
				for (int i = 0; i < getColumnInstance(colEle, 0).getColumnsRepeatedNumber(); i++) {
					list.add(getColumnInstance(colEle, i));
				}
			}
		}
		return list;
	}

	/**
	 * Return a list of table rows in the current table.
	 * 
	 * @return a list of table rows
	 */
	public List<OdfTableRow> getRowList() {
		ArrayList<OdfTableRow> list = new ArrayList<OdfTableRow>();
		TableTableRowElement rowEle = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderRowsElement) {
				TableTableHeaderRowsElement headers = (TableTableHeaderRowsElement) n;
				for (Node m : new DomNodeList(headers.getChildNodes())) {
					if (m instanceof TableTableRowElement) {
						rowEle = (TableTableRowElement) m;
						for (int i = 0; i < rowEle.getTableNumberRowsRepeatedAttribute(); i++) {
							list.add(getRowInstance(rowEle, i));
						}
					}
				}
			}
			if (n instanceof TableTableRowElement) {
				rowEle = (TableTableRowElement) n;
				for (int i = 0; i < rowEle.getTableNumberRowsRepeatedAttribute(); i++) {
					list.add(getRowInstance(rowEle, i));
				}
			}
		}
		return list;
	}

	/**
	 * Get the column at the specified index. The table will be automatically
	 * expanded, when the given index is outside of the original table.
	 * 
	 * @param index
	 *            the zero-based index of the column.
	 * @return the column at the specified index
	 */
	public OdfTableColumn getColumnByIndex(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"index should be nonnegative integer.");
		}
		// expand column as needed.
		int lastIndex = getColumnCount() - 1;
		if (index > lastIndex) {
			appendColumns(index - lastIndex);
		}
		int result = 0;
		OdfTableColumn col = null;
		// TableTableColumnElement colEle=null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderColumnsElement) {
				col = getHeaderColumnByIndex(
						(TableTableHeaderColumnsElement) n, index);
				if (col != null) {
					return col;
				}
				result += getHeaderColumnCount((TableTableHeaderColumnsElement) n);
			}
			if (n instanceof TableTableColumnElement) {
				col = getColumnInstance((TableTableColumnElement) n, 0);
				result += col.getColumnsRepeatedNumber();
			}
			if ((result > index) && (col != null)) {
				return getColumnInstance(col.getOdfElement(), index
						- (result - col.getColumnsRepeatedNumber()));
			}
		}
		return null;
	}

	private OdfTableRow getHeaderRowByIndex(TableTableHeaderRowsElement headers, int nIndex) {
		int result = 0;
		//TableTableRowElement rowEle=null;
		OdfTableRow row = null;
		for (Node n : new DomNodeList(headers.getChildNodes())) {
			if (n instanceof TableTableRowElement) {
				row = getRowInstance((TableTableRowElement) n, 0);
				result += row.getRowsRepeatedNumber();
			}
			if ((result > nIndex) && (row != null)) {
				return getRowInstance(row.getOdfElement(), nIndex - (result - row.getRowsRepeatedNumber()));
			}
		}
		return null;
	}

	private OdfTableColumn getHeaderColumnByIndex(TableTableHeaderColumnsElement headers, int nIndex) {
		int result = 0;
		//TableTableColumnElement colEle=null;
		OdfTableColumn col = null;
		for (Node n : new DomNodeList(headers.getChildNodes())) {
			if (n instanceof TableTableColumnElement) {
				col = getColumnInstance((TableTableColumnElement) n, 0);
				result += col.getColumnsRepeatedNumber();
			}
			if (result > nIndex) {
				return getColumnInstance(col.getOdfElement(), nIndex - (result - col.getColumnsRepeatedNumber()));
			}
		}
		return null;
	}

	/**
	 * Get the row at the specified index. The table will be automatically
	 * expanded, when the given index is outside of the original table.
	 * 
	 * @param index
	 *            the zero-based index of the row.
	 * @return the row at the specified index
	 */
	public OdfTableRow getRowByIndex(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"index should be nonnegative integer.");
		}
		// expand row as needed.
		int lastIndex = getRowCount() - 1;
		if (index > lastIndex) {
			appendRows(index - lastIndex);
		}
		int result = 0;
		OdfTableRow row = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderRowsElement) {
				row = getHeaderRowByIndex((TableTableHeaderRowsElement) n,
						index);
				if (row != null) {
					return row;
				}
				result += getHeaderRowCount((TableTableHeaderRowsElement) n);
			}
			if (n instanceof TableTableRowElement) {
				row = getRowInstance((TableTableRowElement) n, 0);
				result += row.getRowsRepeatedNumber();
			}
			if (result > index) {
				return getRowInstance(row.getOdfElement(), index
						- (result - row.getRowsRepeatedNumber()));
			}
		}
		return null;
	}

	/** 
	 * Remove the specific number of rows, starting from the row at <code>index</code>.
	 * 
	 * @param startindex	is the zero-based index of the first row to delete.
	 * @param rowCount	is the number of rows to delete.
	 */
	public void removeRowsByIndex(int startindex, int rowCount) {
		boolean deleted = false;
		//0. verify the index
		int rowcount = getRowCount();
		if (startindex >= rowcount) {
			throw new IndexOutOfBoundsException("Start index out of bound");
		}
		if (startindex + rowCount >= rowcount) {
			rowCount = rowcount - startindex;
		}

		//1. remove row
		OdfTableRow firstRow = getRowByIndex(startindex);
		for (int i = startindex; i < startindex + rowCount; i++) {
			int repeatedAttr = firstRow.getRowsRepeatedNumber();
			if (repeatedAttr == 1) {
				TableTableRowElement rowEle = OdfElement.findNextChildNode(TableTableRowElement.class, firstRow.getOdfElement());
				//modifySpanNumberOfUpperRow(firstRow);
				//mTableElement.removeChild(firstRow.getOdfElement()); error. the rows parent is not table
				//firstRow.getOdfElement().getParentNode().removeChild(firstRow.getOdfElement());
				firstRow.removeAllCellsRelationship();
				firstRow.getOdfElement().getParentNode().removeChild(firstRow.getOdfElement());
				updateRowRepository(firstRow.getOdfElement(), firstRow.mnRepeatedIndex, null, 0);
				if (i < (startindex + rowCount - 1)) {
					firstRow = this.getRowInstance(rowEle, 0);
				}
				deleted = true;
			} else {
				if (repeatedAttr > firstRow.mnRepeatedIndex) {
					firstRow.setRowsRepeatedNumber(repeatedAttr - 1);
					OdfTableRow startRow = this.getRowInstance(firstRow.getOdfElement(), 0);
					updateRowRepository(firstRow.getOdfElement(), i - startRow.getRowIndex(), null, 0);
				}
			}
		}
		//2. if mediumRow becomes as top row, revise style
		if (deleted && startindex == 0) {
			OdfTableRow aRow = getRowByIndex(0);
			reviseStyleFromMediumRowToTopRow(aRow);
		}
	}

	/**
	 * Remove this table from the document
	 */
	public void remove() {
		mTableElement.getParentNode().removeChild(mTableElement);

	}

	private int getHeaderRowCount(TableTableHeaderRowsElement headers) {
		int result = 0;
		if (headers != null) {
			for (Node n : new DomNodeList(headers.getChildNodes())) {
				if (n instanceof TableTableRowElement) {
					result += ((TableTableRowElement) n).getTableNumberRowsRepeatedAttribute();
				}
			}
		}
		return result;
	}

	/**
	 * Return the number of header rows in this table.
	 * 
	 * @return the number of header rows.
	 */
	public int getHeaderRowCount() {

		TableTableHeaderRowsElement headers = OdfElement.findFirstChildNode(TableTableHeaderRowsElement.class, mTableElement);
		return getHeaderRowCount(headers);
	}

	private int getHeaderColumnCount(TableTableHeaderColumnsElement headers) {
		int result = 0;
		if (headers != null) {
			for (Node n : new DomNodeList(headers.getChildNodes())) {
				if (n instanceof TableTableColumnElement) {
					result += getColumnInstance(((TableTableColumnElement) n),
							0).getColumnsRepeatedNumber();
				}
			}
		}
		return result;
	}

	/**
	 * Return the number of header columns in the table.
	 * 
	 * @return the number of header columns.
	 */
	public int getHeaderColumnCount() {
		TableTableHeaderColumnsElement headers = OdfElement.findFirstChildNode(TableTableHeaderColumnsElement.class, mTableElement);
		return getHeaderColumnCount(headers);
	}

	/**
	 * Return the table name.
	 * 
	 * @return the table name
	 */
	public String getTableName() {
		return mTableElement.getTableNameAttribute();
	}

	/**
	 * Set the table name.
	 * 
	 * @param tableName the table name
	 */
	public void setTableName(String tableName) {
		mTableElement.setTableNameAttribute(tableName);
	}

	/**
	 * Return true if the table is protected.
	 * 
	 * @return true if the table is protected
	 */
	public boolean isProtected() {
		if (mTableElement.getTableProtectedAttribute() != null) {
			return mTableElement.getTableProtectedAttribute().booleanValue();
		} else {
			return false;
		}
	}

	/**
	 * Set if the table is protected.
	 * @param isProtected	the protected attribute of the table to be set
	 */
	public void setProtected(boolean isProtected) {
		mTableElement.setTableProtectedAttribute(new Boolean(isProtected));
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return a range of cells within the specified range. The table will be
	 * automatically expanded as need.
	 * 
	 * @param startCol
	 *            the column index of the first cell inside the range.
	 * @param startRow
	 *            the row index of the first cell inside the range.
	 * @param endCol
	 *            the column index of the last cell inside the range.
	 * @param endRow
	 *            the row index of the last cell inside the range.
	 * @return the specified cell range.
	 */
	public OdfTableCellRange getCellRangeByPosition(int startCol, int startRow,
			int endCol, int endRow) {
		// test whether cell position is out of table range and expand table
		// automatically.
		getCellByPosition(startCol, startRow);
		getCellByPosition(endCol, endRow);
		return new OdfTableCellRange(this, startCol, startRow, endCol, endRow);
	}

	/**
	 * Return a range of cells within the specified range. The range is
	 * specified by the cell address of the first cell and the cell address of
	 * the last cell. The table will be automatically expanded as need.
	 * <p>
	 * The cell address is constructed with a table name, a dot (.), an
	 * alphabetic value representing the column, and a numeric value
	 * representing the row. The table name can be omitted. For example:
	 * "$Sheet1.A1", "Sheet1.A1" and "A1" are all valid cell address.
	 * 
	 * @param startAddress
	 *            the cell address of the first cell inside the range.
	 * @param endAddress
	 *            the cell address of the last cell inside the range.
	 * @return the specified cell range.
	 */
	public OdfTableCellRange getCellRangeByPosition(String startAddress,
			String endAddress) {
		return getCellRangeByPosition(getColIndexFromCellAddress(startAddress),
				getRowIndexFromCellAddress(startAddress),
				getColIndexFromCellAddress(endAddress),
				getRowIndexFromCellAddress(endAddress));
	}

	/**
	 * Return a range of cells by a specified name.
	 * <p>
	 * After you get a cell range with <code>getCellRangeByPosition</code>,  
	 * you can assign a name to this cell range with the method <code>setCellRangeName<code> in class <code>OdfTableCellRange</code>.
	 * Then you will get a <b>named range</b> which can be represented by name.
	 * This method can be used to get a named range.
	 * 
	 * @param name	the name of the specified named range
	 * @return	the specified cell range.
	 */
	public OdfTableCellRange getCellRangeByName(String name) {
		NodeList nameRanges;
		try {
			nameRanges = mTableElement.getOwnerDocument().getElementsByTagNameNS(OdfNamespaceNames.TABLE.getUri(), "named-range");
			for (int i = 0; i < nameRanges.getLength(); i++) {
				TableNamedRangeElement nameRange = (TableNamedRangeElement) nameRanges.item(i);
				if (nameRange.getTableNameAttribute().equals(name)) {
					String cellRange = nameRange.getTableCellRangeAddressAttribute();
					String[] addresses = cellRange.split(":");
					return getCellRangeByPosition(addresses[0], addresses[1]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return a single cell that is positioned at the specified column and row. 
	 * The table will be automatically expanded as need.
	 * 
	 * @param colIndex  the column index of the cell.
	 * @param rowIndex  the row index of the cell.
	 * @return the cell at the specified position
	 */
	public OdfTableCell getCellByPosition(int colIndex, int rowIndex) {
		if (colIndex < 0 || rowIndex < 0) {
			throw new IllegalArgumentException(
					"colIndex and rowIndex should be nonnegative integer.");
		}
		// expand row as needed.
		int lastRowIndex = getRowCount() - 1;
		if (rowIndex > lastRowIndex) {
			System.out.print("// expand row as needed.");
			appendRows(rowIndex - lastRowIndex);
		}
		// expand column as needed.
		int lastColumnIndex = getColumnCount() - 1;
		if (colIndex > lastColumnIndex) {
			System.out.print("// expand column  as needed.");
			appendColumns(colIndex - lastColumnIndex);
		}
		OdfTableRow row = getRowByIndex(rowIndex);
		return row.getCellByIndex(colIndex);
	}

	//return array of string contain 3 member
	//1. sheet table name
	//2. alphabetic represent the column 
	//3. string represent the row number
	String[] splitCellAddress(String cellAddress) {
		String[] returnArray = new String[3];
		//seperate column and row from cell range
		StringTokenizer stDot = new StringTokenizer(cellAddress, ".");
		//get sheet table name and the cell address
		String cell = "";
		if (stDot.countTokens() >= 2) {
			StringTokenizer stDollar = new StringTokenizer(stDot.nextToken(), "$");
			returnArray[0] = stDollar.nextToken();
			cell = stDot.nextToken();
		} else {
			returnArray[0] = getTableName();
			cell = stDot.nextToken();
		}

		//get the column/row number from the cell address
		StringTokenizer stDollar = new StringTokenizer(cell, "$");
		if (stDollar.countTokens() >= 2) {
			returnArray[1] = stDollar.nextToken();
			returnArray[2] = stDollar.nextToken();
		} else {
			cell = stDollar.nextToken();
			for (int i = 0; i < cell.length(); i++) {
				if (!Character.isLetter(cell.charAt(i))) {
					returnArray[1] = cell.substring(0, i);
					returnArray[2] = cell.substring(i);
					break;
				}
			}
		}
		return returnArray;

	}

	/**
	 * Return a single cell that is positioned at the specified cell address.
	 * The table can be automatically expanded as need.
	 * <p>
	 * The cell address is constructed with a table name, a dot (.), 
	 * an alphabetic value representing the column, and a numeric value representing the row. 
	 * The table name can be omitted. For example: "$Sheet1.A1", "Sheet1.A1" and "A1" are all
	 * valid cell address.
	 * 
	 * @param address	the cell address of the cell.
	 * @return the cell at the specified position.
	 */
	public OdfTableCell getCellByPosition(String address) {
		return getCellByPosition(getColIndexFromCellAddress(address),
				getRowIndexFromCellAddress(address));
	}

	//TODO: can put these two method to type.CellAddress
	int getColIndexFromCellAddress(String cellAddress) {
		String[] returnArray = splitCellAddress(cellAddress);
		String colNum = returnArray[1];
		int colIndex = 0;
		for (int i = 0; i < colNum.length(); i++) {
			colIndex = 26 * colIndex;
			colIndex += (colNum.charAt(i) - 'A') + 1;
		}

		return (colIndex - 1);
	}

	int getRowIndexFromCellAddress(String cellAddress) {
		String[] returnArray = splitCellAddress(cellAddress);
		return Integer.parseInt(returnArray[2]) - 1;
	}

	String getAbsoluteCellAddress(int colIndex, int rowIndex) {
		int remainder = 0;
		int multiple = colIndex;
		String cellRange = "";
		while (multiple != 0) {
			multiple = colIndex / 26;
			remainder = colIndex % 26;
			char c;
			if (multiple == 0) {
				c = (char) ('A' + remainder);
			} else {
				c = (char) ('A' + multiple - 1);
			}
			cellRange = cellRange + String.valueOf(c);
			colIndex = remainder;
		}
		cellRange = "$" + cellRange + "$" + (rowIndex + 1);
		return cellRange;

	}
	//the parameter is the column/row index in the ownerTable,rather than in the cell range
	//if the position is a covered cell, then get the owner cell for it

	OdfTableCell getOwnerCellByPosition(List<CellCoverInfo> coverList, int nCol, int nRow) {
		CellCoverInfo info;
		if (!isCoveredCellInOwnerTable(coverList, nCol, nRow)) {
			OdfTableCell cell = getCellByPosition(nCol, nRow);
			return cell;
		} else {
			for (int m = 0; m < coverList.size(); m++) {
				info = coverList.get(m);
				if (((nCol > info.nStartCol) && (nCol <= info.nEndCol)
						&& (nRow == info.nStartRow) && (nRow == info.nEndRow))
						|| ((nCol == info.nStartCol) && (nCol == info.nEndCol)
						&& (nRow > info.nStartRow) && (nRow <= info.nEndRow))
						|| ((nCol > info.nStartCol) && (nCol <= info.nEndCol)
						&& (nRow > info.nStartRow) && (nRow <= info.nEndRow))) {
					OdfTableCell cell = getCellByPosition(info.nStartCol, info.nStartRow);
					return cell;
				}
			}
		}
		return null;
	}

	//the parameter is the column/row index in the ownerTable,rather than in the cell range
	boolean isCoveredCellInOwnerTable(List<CellCoverInfo> coverList, int nCol, int nRow) {

//		FTableCell cell = getCellByPosition(nCol, nRow);
//		if(cell.getOdfElement() instanceof TableCoveredTableCellElement)
//			//covered cell
//			return true;
//		else if(cell.getOdfElement() instanceof TableTableCellElement)
//		{
		CellCoverInfo info;
		for (int m = 0; m < coverList.size(); m++) {
			info = coverList.get(m);
			if (((nCol > info.nStartCol) && (nCol <= info.nEndCol)
					&& (nRow == info.nStartRow) && (nRow == info.nEndRow))
					|| ((nCol == info.nStartCol) && (nCol == info.nEndCol)
					&& (nRow > info.nStartRow) && (nRow <= info.nEndRow))
					|| ((nCol > info.nStartCol) && (nCol <= info.nEndCol)
					&& (nRow > info.nStartRow) && (nRow <= info.nEndRow))) //covered cell
			{
				return true;
			}
		}
//		}
		return false;
	}

	List<CellCoverInfo> getCellCoverInfos(int nStartCol, int nStartRow, int nEndCol, int nEndRow) {
		List<CellCoverInfo> coverList = new ArrayList<CellCoverInfo>();
		int nColSpan, nRowSpan;
		for (int i = nStartCol; i < nEndCol + 1; i++) {
			for (int j = nStartRow; j < nEndRow + 1; j++) {
				OdfTableCell cell = getCellByPosition(i, j);
				if (cell != null) {
					nColSpan = cell.getColumnSpannedNumber();
					nRowSpan = cell.getRowSpannedNumber();
					if ((nColSpan > 1) || (nRowSpan > 1)) {
						coverList.add(new CellCoverInfo(i, j, nColSpan, nRowSpan));
					}
				}
			}
		}
		return coverList;
	}

	//the odfelement of the FTableColumn changed, so we should update the repository here
	void updateColumnRepository(TableTableColumnElement oldElement, int oldRepeatIndex, TableTableColumnElement newElement, int newRepeatIndex) {
		if (mColumnRepository.containsKey(oldElement)) {
			Vector<OdfTableColumn> oldList = mColumnRepository.get(oldElement);
			if (oldRepeatIndex < oldList.size()) {
				if (oldElement != newElement) {
					//the new column replace the old column
					OdfTableColumn oldColumn = oldList.get(oldRepeatIndex);
					if (oldColumn != null) {
						//update the mnRepeateIndex of the column which locate after the removed column
						for (int i = oldRepeatIndex + 1; i < oldList.size(); i++) {
							OdfTableColumn column = oldList.get(i);
							if (column != null) {
								column.mnRepeatedIndex = i - 1;
							}
						}
						oldList.remove(oldColumn);
						//oldList.add(oldRepeatIndex, null);
						if (newElement != null) {
							oldColumn.maColumnElement = newElement;
							oldColumn.mnRepeatedIndex = newRepeatIndex;
							int size = (newRepeatIndex > 7) ? (newRepeatIndex + 1) : 8;
							Vector<OdfTableColumn> list = new Vector<OdfTableColumn>(size);
							list.setSize(newRepeatIndex + 1);
							list.set(newRepeatIndex, oldColumn);
							mColumnRepository.put(newElement, list);
						} else {
							oldColumn.maColumnElement = null;
						}
					}
				} else {
					//the new column element is equal to the old column element, just change the repeatIndex
					OdfTableColumn oldColumn = oldList.get(oldRepeatIndex);
					if (oldColumn != null) {
						oldList.remove(oldColumn);
						oldList.add(oldRepeatIndex, null);
						oldColumn.mnRepeatedIndex = newRepeatIndex;
						if (newRepeatIndex >= oldList.size()) {
							oldList.setSize(newRepeatIndex + 1);
						}
						oldList.set(newRepeatIndex, oldColumn);
					} else {
						getColumnInstance(newElement, newRepeatIndex);
					}
				}
			}
		}
	}

	//the odfelement of the FTableRow changed, so we should update the repository here
	void updateRowRepository(TableTableRowElement oldElement, int oldRepeatIndex, TableTableRowElement newElement, int newRepeatIndex) {
		if (mRowRepository.containsKey(oldElement)) {
			Vector<OdfTableRow> oldList = mRowRepository.get(oldElement);
			if (oldRepeatIndex < oldList.size()) {
				if (oldElement != newElement) {
					//the new row replace the old row
					OdfTableRow oldRow = oldList.get(oldRepeatIndex);
					Vector<OdfTableCell> updateCellList = new Vector<OdfTableCell>();
					if (oldRow != null) {
						//update the mnRepeateIndex of the row which locate after the removed row
						for (int i = oldRepeatIndex + 1; i < oldList.size(); i++) {
							OdfTableRow row = oldList.get(i);
							if (row != null) {
								//update the cell in this row, 
								int colNum = getColumnCount();
								for (int j = 0; j < colNum; j++) {
									OdfTableCell cell = row.getCellByIndex(j);
//									cell.mnRepeatedRowIndex = i - 1;
									updateCellList.add(cell);
								}
								row.mnRepeatedIndex = i - 1;
							}
						}
						oldList.remove(oldRow);
						if (newElement != null) {
							//update the cell in this row
							int colNum = getColumnCount();
							OdfTableCell[] oldCells = new OdfTableCell[colNum];
							for (int j = 0; j < colNum; j++) {
								oldCells[j] = oldRow.getCellByIndex(j);
							}
							///
							oldRow.maRowElement = newElement;
							oldRow.mnRepeatedIndex = newRepeatIndex;
							int size = (newRepeatIndex > 7) ? (newRepeatIndex + 1) : 8;
							Vector<OdfTableRow> list = new Vector<OdfTableRow>(size);
							list.setSize(newRepeatIndex + 1);
							list.set(newRepeatIndex, oldRow);
							mRowRepository.put(newElement, list);
							//update the cell in this row
							OdfTableCell[] newCells = new OdfTableCell[colNum];
							for (int j = 0; j < colNum; j++) {
								newCells[j] = oldRow.getCellByIndex(j);
							}
							for (int j = 0; j < colNum; j++) {
								this.updateCellRepository(oldCells[j].getOdfElement(), oldCells[j].mnRepeatedColIndex, oldCells[j].mnRepeatedRowIndex,
										newCells[j].getOdfElement(), newCells[j].mnRepeatedColIndex, newCells[j].mnRepeatedRowIndex);
							}

							//update the mnRepeatedRowIndex of the cell which locate after the removed row
							for (int j = 0; j < updateCellList.size(); j++) {
								OdfTableCell cell = updateCellList.get(j);
								if (cell.mnRepeatedRowIndex > oldRepeatIndex) {
									cell.mnRepeatedRowIndex--;
								}
							}
							///

						} else {
							oldRow.maRowElement = null;
						}
					}
				} else {
					//the new row element is equal to the old row element, just change the repeatIndex
					OdfTableRow oldRow = oldList.get(oldRepeatIndex);
					if (oldRow != null) {
						oldList.remove(oldRow);
						oldList.add(oldRepeatIndex, null);
						oldRow.mnRepeatedIndex = newRepeatIndex;
						if (newRepeatIndex >= oldList.size()) {
							oldList.setSize(newRepeatIndex + 1);
						}
						oldList.set(newRepeatIndex, oldRow);
					} else {
						getRowInstance(newElement, newRepeatIndex);
					}
				}
			}
		}
	}

	//the odfelement of the FTableCell changed, so we should update the repository here
	void updateCellRepository(TableTableCellElementBase oldElement, int oldRepeatColIndex, int oldRepeatRowIndex,
			TableTableCellElementBase newElement, int newRepeatColIndex, int newRepeatRowIndex) {
		if (mCellRepository.containsKey(oldElement)) {
			OdfTableCell oldCell = null;
			Vector<OdfTableCell> oldList = mCellRepository.get(oldElement);
			for (int i = 0; i < oldList.size(); i++) {
				if (oldList.get(i).getOdfElement() == oldElement
						&& oldList.get(i).mnRepeatedColIndex == oldRepeatColIndex
						&& oldList.get(i).mnRepeatedRowIndex == oldRepeatRowIndex) {
					oldCell = oldList.get(i);
					break;
				}
			}
			if (oldElement != newElement) {
				//the new cell replace the old cell
				if (oldCell != null) {
					//update the mnRepeateRowIndex &  mnRepeateColIndex of the cell which locate after the removed cell
					for (int i = 0; i < oldList.size(); i++) {
						OdfTableCell cell = oldList.get(i);
						if (cell != null && (cell.getOdfElement() == oldElement)) {
							if ((cell.mnRepeatedRowIndex == oldRepeatRowIndex)
									&& (cell.mnRepeatedColIndex > oldRepeatColIndex)) {
								cell.mnRepeatedColIndex--;
							}
//							if( (cell.mnRepeatedColIndex == oldRepeatColIndex) 
//									&& (cell.mnRepeatedRowIndex > oldRepeatRowIndex ) )
//								cell.mnRepeatedRowIndex--;
						}
					}
					oldList.remove(oldCell);
					if (oldList.size() == 0) {
						mCellRepository.remove(oldElement);
					}
					//oldList.add(oldRepeatIndex, null);
					if (newElement != null) {
						oldCell.mCellElement = newElement;
						oldCell.mnRepeatedColIndex = newRepeatColIndex;
						oldCell.mnRepeatedRowIndex = newRepeatRowIndex;
						Vector<OdfTableCell> list;
						if (mCellRepository.containsKey(newElement)) {
							list = mCellRepository.get(newElement);
							boolean bReplaced = false;
							for (int i = 0; i < list.size(); i++) {
								OdfTableCell cell = list.get(i);
								if (cell != null && (cell.getOdfElement() == newElement)) {
									if ((cell.mnRepeatedColIndex == newRepeatColIndex)
											&& (cell.mnRepeatedRowIndex == newRepeatRowIndex)) {
										list.remove(i);
										list.add(i, oldCell);
										bReplaced = true;
										break;
									}
								}
							}
							if (!bReplaced) {
								list.add(oldCell);
								//System.out.println("should not contains");
							}
						} else {
							list = new Vector<OdfTableCell>();
							list.add(oldCell);
							mCellRepository.put(newElement, list);
						}
					} else {
						oldCell.mCellElement = null;
						oldCell.mnRepeatedColIndex = 0;
						oldCell.mnRepeatedRowIndex = 0;
					}
				}
			} else {
				//the new cell element is equal to the old cell element, just change the repeatIndex
				if (oldCell != null) {
					oldCell.mnRepeatedColIndex = newRepeatColIndex;
					oldCell.mnRepeatedRowIndex = newRepeatRowIndex;
				} else {
					getCellInstance(newElement, newRepeatColIndex, newRepeatRowIndex);
				}
			}
		}
	}

	void updateRepositoryWhenCellElementChanged(int startRow, int endRow, int startClm, int endClm, TableTableCellElement newCellEle) {
		for (int i = startRow; i < endRow; i++) {
			for (int j = startClm; j < endClm; j++) {
				OdfTableCell cell = getCellByPosition(j, i);
				updateCellRepository(cell.getOdfElement(), cell.mnRepeatedColIndex, cell.mnRepeatedRowIndex,
						newCellEle, cell.mnRepeatedColIndex, cell.mnRepeatedRowIndex);
			}
		}
	}
}

/**
 * Record the Cell Cover Info in this cell range.
 * <p>
 * Sometimes the covered cell is not tagged as <table:covered-table-cell> element.
 *
 */
class CellCoverInfo {

	int nStartCol;
	int nStartRow;
	int nEndCol;
	int nEndRow;

	CellCoverInfo(int nSC, int nSR, int nColumnSpan, int nRowSpan) {
		nStartCol = nSC;
		nStartRow = nSR;
		nEndCol = nSC + nColumnSpan - 1;
		nEndRow = nSR + nRowSpan - 1;
	}
}
