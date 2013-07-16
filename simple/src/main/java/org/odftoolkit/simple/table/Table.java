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

package org.odftoolkit.simple.table;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.table.TableAlignAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfTableProperties;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.PositiveLength;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.style.DefaultStyleHandler;
import org.odftoolkit.simple.text.Paragraph;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Table represents the table feature in ODF spreadsheet and text documents.
 * <p>
 * Table provides methods to get/add/delete/modify table column/row/cell.
 * 
 */
public class Table extends Component {

	private final TableTableElement mTableElement;
	protected Document mDocument;
	protected boolean mIsSpreadsheet;
	protected boolean mIsCellStyleInheritance = true;
	protected boolean mIsDescribedBySingleElement = true;
	private static final int DEFAULT_ROW_COUNT = 2;
	private static final int DEFAULT_COLUMN_COUNT = 5;
	private static final double DEFAULT_TABLE_WIDTH = 6.692; // 6
	private static final int DEFAULT_REL_TABLE_WIDTH = 65535;
	private static final String DEFAULT_TABLE_ALIGN = "margins";
	private static final DecimalFormat IN_FORMAT = new DecimalFormat("##0.0000");
	// TODO: should save seperately for different dom tree
	IdentityHashMap<TableTableCellElementBase, Vector<Cell>> mCellRepository = new IdentityHashMap<TableTableCellElementBase, Vector<Cell>>();
	IdentityHashMap<TableTableRowElement, Vector<Row>> mRowRepository = new IdentityHashMap<TableTableRowElement, Vector<Row>>();
	IdentityHashMap<TableTableColumnElement, Vector<Column>> mColumnRepository = new IdentityHashMap<TableTableColumnElement, Vector<Column>>();
	private DefaultStyleHandler mStyleHandler;
	static {
		IN_FORMAT.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
	}

	/**
	 * This is a tool class which supplies all of the table creation detail.
	 * <p>
	 * The end user isn't allowed to create it directly, otherwise an
	 * <code>IllegalStateException</code> will be thrown.
	 * 
	 *@since 0.3.5
	 */
	public static class TableBuilder {

		private final TableContainer ownerContainer;

		private final IdentityHashMap<TableTableElement, Table> mTableRepository = new IdentityHashMap<TableTableElement, Table>();

		/**
		 * TableBuilder constructor. This constructor should only be use in
		 * owner {@link org.odftoolkit.simple.table.TableContainer
		 * TableContainer} constructor. The end user isn't allowed to call it
		 * directly, otherwise an <code>IllegalStateException</code> will be
		 * thrown.
		 * 
		 * @param container
		 *            the owner <code>TableContainer</code>.
		 * @throws IllegalStateException
		 *             if new TableBuilder out of owner Document constructor,
		 *             this exception will be thrown.
		 */
		public TableBuilder(TableContainer container) {
			if (container.getTableBuilder() == null) {
				ownerContainer = container;
			} else {
				throw new IllegalStateException("TableBuilder only can be created in table containter constructor.");
			}
		}

		/**
		 * Get a table feature instance by an instance of
		 * <code>TableTableElement</code>.
		 * 
		 * @param odfElement
		 *            an instance of <code>TableTableElement</code>
		 * @return an instance of <code>Table</code> that can represent
		 *         <code>odfElement</code>
		 */
		public synchronized Table getTableInstance(TableTableElement odfElement) {
			if (mTableRepository.containsKey(odfElement)) {
				return mTableRepository.get(odfElement);
			} else {
				Table newTable = new Table(ownerContainer, odfElement);
				mTableRepository.put(odfElement, newTable);
				return newTable;
			}
		}

		/**
		 * Construct the <code>Table</code> feature. The default column count is
		 * 5. The default row count is 2.
		 * <p>
		 * The table will be inserted at the end of the table container. An
		 * unique table name will be given, you may set a custom table name
		 * using the <code>setTableName</code> method.
		 * <p>
		 * If the container is a text document, cell borders will be created by
		 * default.
		 * 
		 * @return the created <code>Table</code> feature instance
		 */
		public Table newTable() {
			return newTable(DEFAULT_ROW_COUNT, DEFAULT_COLUMN_COUNT, 0, 0);
		}

		/**
		 * Construct the <code>Table</code> feature with a specified row number,
		 * column number, header row number, header column number.
		 * <p>
		 * The table will be inserted at the end of the container. An unique
		 * table name will be given, you may set a custom table name using the
		 * <code>setTableName</code> method.
		 * <p>
		 * If the container is a text document, cell borders will be created by
		 * default.
		 * 
		 * @param numRows
		 *            the row number
		 * @param numCols
		 *            the column number
		 * @param headerRowNumber
		 *            the header row number
		 * @param headerColumnNumber
		 *            the header column number
		 * @return a new instance of <code>Table</code>
		 * */
		public Table newTable(int numRows, int numCols, int headerRowNumber, int headerColumnNumber) {
			try {
				TableTableElement newTEle = createTable(ownerContainer, numRows, numCols, headerRowNumber,
						headerColumnNumber);
				ownerContainer.getTableContainerElement().appendChild(newTEle);
				return getTableInstance(newTEle);
			} catch (DOMException e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			} catch (Exception e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
		
		/**
		 * Construct the <code>Table</code> feature with a specified row number,
		 * column number, header row number, header column number, left margin
		 * space and right margin space.
		 * <p>
		 * The table will be inserted at the end of the container. An unique
		 * table name will be given, you may set a custom table name using the
		 * <code>setTableName</code> method.
		 * <p>
		 * If the container is a text document, cell borders will be created by
		 * default.
		 * 
		 * @param numRows
		 *            the row number
		 * @param numCols
		 *            the column number
		 * @param headerRowNumber
		 *            the header row number
		 * @param headerColumnNumber
		 *            the header column number
		 * @param marginLeft
		 *            the left table margin in centimeter(cm), between the left
		 *            margin of table container and the table
		 * @param marginRight
		 *            the right table margin in centimeter(cm), between the
		 *            right margin of table container and the table
		 * 
		 * @return a new instance of <code>Table</code>
		 * 
		 * @since 0.5.5
		 * */
		public Table newTable(int numRows, int numCols, int headerRowNumber, int headerColumnNumber, double marginLeft,
				double marginRight) {
			try {
				TableTableElement newTEle = createTable(ownerContainer, numRows, numCols, headerRowNumber,
						headerColumnNumber, marginLeft, marginRight);
				ownerContainer.getTableContainerElement().appendChild(newTEle);
				return getTableInstance(newTEle);
			} catch (Exception e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}

		/**
		 * Construct the <code>Table</code> feature with a specified row number
		 * and column number.
		 * <p>
		 * The table will be inserted at the end of the container. An unique
		 * table name will be given, you may set a custom table name using the
		 * <code>setTableName</code> method.
		 * <p>
		 * If the container is a text document, cell borders will be created by
		 * default.
		 * 
		 * @param numRows
		 *            the row number
		 * @param numCols
		 *            the column number
		 * @return a new instance of <code>Table</code>
		 */
		public Table newTable(int numRows, int numCols) {
			return newTable(numRows, numCols, 0, 0);
		}

		/**
		 * Construct the Table feature with a specified 2 dimension array as the
		 * data of this table. The value type of each cell is float.
		 * <p>
		 * The table will be inserted at the end of the container. An unique
		 * table name will be given, you may set a custom table name using the
		 * <code>setTableName</code> method.
		 * <p>
		 * If the container is a text document, cell borders will be created by
		 * default.
		 * 
		 * @param rowLabel
		 *            set as the header row, it can be null if no header row
		 *            needed
		 * @param columnLabel
		 *            set as the header column, it can be null if no header
		 *            column needed
		 * @param data
		 *            the two dimension array of double as the data of this
		 *            table
		 * @return a new instance of <code>Table</code>
		 */
		public Table newTable(String[] rowLabel, String[] columnLabel, double[][] data) {
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
				TableTableElement newTEle = createTable(ownerContainer, rowNumber + rowHeaders, columnNumber
						+ columnHeaders, rowHeaders, columnHeaders);
				// append to the end of table container
				ownerContainer.getTableContainerElement().appendChild(newTEle);
				Table table = getTableInstance(newTEle);
				List<Row> rowList = table.getRowList();
				for (int i = 0; i < rowNumber + rowHeaders; i++) {
					Row row = rowList.get(i);
					for (int j = 0; j < columnNumber + columnHeaders; j++) {
						if ((i == 0) && (j == 0)) {
							continue;
						}
						Cell cell = row.getCellByIndex(j);
						if (i == 0 && columnLabel != null) // first row, should
						// fill column
						// labels
						{
							if (j <= columnLabel.length) {
								cell.setStringValue(columnLabel[j - 1]);
							} else {
								cell.setStringValue("");
							}
						} else if (j == 0 && rowLabel != null) // first column,
						// should fill
						// row labels
						{
							if (i <= rowLabel.length) {
								cell.setStringValue(rowLabel[i - 1]);
							} else {
								cell.setStringValue("");
							}
						} else {// data
							if ((data != null) && (i >= rowHeaders) && (j >= columnHeaders)) {
								cell.setDoubleValue(data[i - rowHeaders][j - columnHeaders]);
							}
						}
					}
				}
				return table;

			} catch (DOMException e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			} catch (Exception e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}

		/**
		 * Construct the Table feature with a specified 2 dimension array as the
		 * data of this table. The value type of each cell is string.
		 * <p>
		 * The table will be inserted at the end of the container. An unique
		 * table name will be given, you may set a custom table name using the
		 * <code>setTableName</code> method.
		 * <p>
		 * If the container is a text document, cell borders will be created by
		 * default.
		 * 
		 * @param rowLabel
		 *            set as the header row, it can be null if no header row
		 *            needed
		 * @param columnLabel
		 *            set as the header column, it can be null if no header
		 *            column needed
		 * @param data
		 *            the two dimension array of string as the data of this
		 *            table
		 * @return a new instance of <code>Table</code>
		 */
		public Table newTable(String[] rowLabel, String[] columnLabel, String[][] data) {
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
				TableTableElement newTEle = createTable(ownerContainer, rowNumber + rowHeaders, columnNumber
						+ columnHeaders, rowHeaders, columnHeaders);
				// append to the end of table container
				ownerContainer.getTableContainerElement().appendChild(newTEle);

				Table table = getTableInstance(newTEle);
				List<Row> rowList = table.getRowList();
				for (int i = 0; i < rowNumber + rowHeaders; i++) {
					Row row = rowList.get(i);
					for (int j = 0; j < columnNumber + columnHeaders; j++) {
						if ((i == 0) && (j == 0)) {
							continue;
						}
						Cell cell = row.getCellByIndex(j);
						if (i == 0 && columnLabel != null) // first row, should
						// fill column
						// labels
						{
							if (j <= columnLabel.length) {
								cell.setStringValue(columnLabel[j - 1]);
							} else {
								cell.setStringValue("");
							}
						} else if (j == 0 && rowLabel != null) // first column,
						// should fill
						// row labels
						{
							if (i <= rowLabel.length) {
								cell.setStringValue(rowLabel[i - 1]);
							} else {
								cell.setStringValue("");
							}
						} else {
							if ((data != null) && (i >= rowHeaders) && (j >= columnHeaders)) {
								cell.setStringValue(data[i - rowHeaders][j - columnHeaders]);
							}
						}
					}
				}
				return table;

			} catch (DOMException e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			} catch (Exception e) {
				Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			}
			return null;
		}
	}

	private Table(TableContainer container, TableTableElement table) {
		mTableElement = table;
		mDocument = getOwnerDocument(container);
		if (mDocument instanceof SpreadsheetDocument) {
			mIsSpreadsheet = true;
		} else {
			mIsSpreadsheet = false;
		}
	}

	private static Document getOwnerDocument(TableContainer tableContainer) {
		OdfElement containerElement = tableContainer.getTableContainerElement();
		OdfFileDom ownerDocument = (OdfFileDom) containerElement.getOwnerDocument();
		return (Document) ownerDocument.getDocument();
	}

	/**
	 * Get a table feature instance by an instance of
	 * <code>TableTableElement</code>.
	 * 
	 * @param element
	 *            an instance of <code>TableTableElement</code>
	 * @return an instance of <code>Table</code> that can represent
	 *         <code>element</code>
	 */
	public static Table getInstance(TableTableElement element) {
		Document ownerDocument = (Document) ((OdfFileDom) (element.getOwnerDocument())).getDocument();
		return ownerDocument.getTableBuilder().getTableInstance(element);
	}

	/**
	 * Construct the <code>Table</code> feature. The default column count is 5.
	 * The default row count is 2.
	 * <p>
	 * The table will be inserted at the end of the container. An unique table
	 * name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * <p>
	 * If the <code>tableContainer</code> is a text document, cell borders will
	 * be created by default.
	 * 
	 * @param tableContainer
	 *            the table container that contains this table
	 * @return the created <code>Table</code> feature instance
	 */
	public static Table newTable(TableContainer tableContainer) {
		return tableContainer.getTableBuilder().newTable();
	}

	/**
	 * Construct the <code>Table</code> feature with a specified row number and
	 * column number.
	 * <p>
	 * The table will be inserted at the end of the tableContainer. An unique
	 * table name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * <p>
	 * If the <code>tableContainer</code> is a text document, cell borders will
	 * be created by default.
	 * 
	 * @param tableContainer
	 *            the table container that contains this table
	 * @param numRows
	 *            the row number
	 * @param numCols
	 *            the column number
	 * @return a new instance of <code>Table</code>
	 */
	public static Table newTable(TableContainer tableContainer, int numRows, int numCols) {
		return tableContainer.getTableBuilder().newTable(numRows, numCols);
	}
	
	/**
	 * Construct the <code>Table</code> feature with a specified row number and
	 * column number.
	 * <p>
	 * The table will be inserted at the end of the tableContainer. An unique
	 * table name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * <p>
	 * If the <code>tableContainer</code> is a text document, cell borders will
	 * be created by default.
	 * 
	 * @param tableContainer
	 *            the table container that contains this table
	 * @param numRows
	 *            the row number
	 * @param numCols
	 *            the column number
	 * @param marginLeft double
	 * 			  <I>the left table margin in cm (between the left margin of document and the table)</I>
	 * @param marginRight double
	 * 			  <I>the right table margin in cm (between the right margin of document and the table)</I>
	 * @return a new instance of <code>Table</code>
	 */
	public static Table newTable(TableContainer tableContainer, int numRows, int numCols, 
			double marginLeft, double marginRight) {
		return tableContainer.getTableBuilder().newTable(numRows, numCols, 0, 0, marginLeft, marginRight);
	}

	/**
	 * Construct the <code>Table</code> feature with a specified row number,
	 * column number, header row number, header column number.
	 * <p>
	 * The table will be inserted at the end of the tableContainer. An unique
	 * table name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * <p>
	 * If the <code>tableContainer</code> is a text document, cell borders will
	 * be created by default.
	 * 
	 * @param tableContainer
	 *            the ODF document that contains this feature
	 * @param numRows
	 *            the row number
	 * @param numCols
	 *            the column number
	 * @param headerRowNumber
	 *            the header row number
	 * @param headerColumnNumber
	 *            the header column number
	 * @return a new instance of <code>Table</code>
	 * */
	public static Table newTable(TableContainer tableContainer, int numRows, int numCols, int headerRowNumber,
			int headerColumnNumber) {
		return tableContainer.getTableBuilder().newTable(numRows, numCols, headerRowNumber, headerColumnNumber);
	}
	
	

	/**
	 * Construct the Table feature with a specified 2 dimension array as the
	 * data of this table. The value type of each cell is float.
	 * <p>
	 * The table will be inserted at the end of the tableContainer. An unique
	 * table name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * <p>
	 * If the <code>tableContainer</code> is a text document, cell borders will
	 * be created by default.
	 * 
	 * @param tableContainer
	 *            the table container that contains this table
	 * @param rowLabel
	 *            set as the header row, it can be null if no header row needed
	 * @param columnLabel
	 *            set as the header column, it can be null if no header column
	 *            needed
	 * @param data
	 *            the two dimension array of double as the data of this table
	 * @return a new instance of <code>Table</code>
	 */
	public static Table newTable(TableContainer tableContainer, String[] rowLabel, String[] columnLabel, double[][] data) {
		return tableContainer.getTableBuilder().newTable(rowLabel, columnLabel, data);
	}

	/**
	 * Construct the Table feature with a specified 2 dimension array as the
	 * data of this table. The value type of each cell is string.
	 * <p>
	 * The table will be inserted at the end of the tableContainer. An unique
	 * table name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * <p>
	 * If the <code>tableContainer</code> is a text document, cell borders will
	 * be created by default.
	 * 
	 * @param tableContainer
	 *            the table container that contains this table
	 * @param rowLabel
	 *            set as the header row, it can be null if no header row needed
	 * @param columnLabel
	 *            set as the header column, it can be null if no header column
	 *            needed
	 * @param data
	 *            the two dimension array of string as the data of this table
	 * @return a new instance of <code>Table</code>
	 */
	public static Table newTable(TableContainer tableContainer, String[] rowLabel, String[] columnLabel, String[][] data) {
		return tableContainer.getTableBuilder().newTable(rowLabel, columnLabel, data);
	}

	Cell getCellInstance(TableTableCellElementBase cell, int repeatedColIndex, int repeatedRowIndex) {
		if (mCellRepository.containsKey(cell)) {
			Vector<Cell> list = mCellRepository.get(cell);
			Cell fCell = null;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getOdfElement() == cell && list.get(i).mnRepeatedColIndex == repeatedColIndex
						&& list.get(i).mnRepeatedRowIndex == repeatedRowIndex) {
					fCell = list.get(i);
					break;
				}
			}
			if (fCell == null) {
				fCell = new Cell(cell, repeatedColIndex, repeatedRowIndex);
				list.add(fCell);
			}
			return fCell;
		} else {
			Cell newCell = new Cell(cell, repeatedColIndex, repeatedRowIndex);
			Vector<Cell> list = new Vector<Cell>();
			list.add(newCell);
			mCellRepository.put(cell, list);
			return newCell;
		}
	}

	Row getRowInstance(TableTableRowElement row, int repeatedRowIndex) {
		if (mRowRepository.containsKey(row)) {
			Vector<Row> list = mRowRepository.get(row);
			if (list.size() <= repeatedRowIndex) {
				list.setSize(repeatedRowIndex + 1);
			}
			Row fCell = list.get(repeatedRowIndex);
			if (fCell == null) {
				fCell = new Row(row, repeatedRowIndex);
				list.set(repeatedRowIndex, fCell);
			}
			return fCell;
		} else {
			Row newRow = new Row(row, repeatedRowIndex);
			int size = (repeatedRowIndex > 7) ? (repeatedRowIndex + 1) : 8;
			Vector<Row> list = new Vector<Row>(size);
			list.setSize(repeatedRowIndex + 1);
			list.set(repeatedRowIndex, newRow);
			mRowRepository.put(row, list);
			return newRow;
		}
	}

	Column getColumnInstance(TableTableColumnElement col, int repeatedColIndex) {
		if (mColumnRepository.containsKey(col)) {
			Vector<Column> list = mColumnRepository.get(col);
			if (list.size() <= repeatedColIndex) {
				list.setSize(repeatedColIndex + 1);
			}
			Column fClm = list.get(repeatedColIndex);
			if (fClm == null) {
				fClm = new Column(col, repeatedColIndex);
				list.set(repeatedColIndex, fClm);
			}
			return fClm;
		} else {
			Column newColumn = new Column(col, repeatedColIndex);
			int size = (repeatedColIndex > 7) ? (repeatedColIndex + 1) : 8;
			Vector<Column> list = new Vector<Column>(size);
			list.setSize(repeatedColIndex + 1);
			list.set(repeatedColIndex, newColumn);
			mColumnRepository.put(col, list);
			return newColumn;
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
	 * Get the width of the table (in Millimeter).
	 * <p>
	 * Throw an UnsupportedOperationException if the table is one sheet of a
	 * spreadsheet document. because the sheet doesn't have an attribute of
	 * table width.
	 * 
	 * @return the width of the current table (in Millimeter).
	 *         <p>
	 *         An UnsupportedOperationException will be thrown if the table is
	 *         in the spreadsheet document.
	 */
	public double getWidth() {
		if (!mIsSpreadsheet) {
			String sWidth = mTableElement.getProperty(OdfTableProperties.Width);
			if (sWidth == null) {
				int colCount = getColumnCount();
				double tableWidth = 0;
				for (int i = 0; i < colCount; i++) {
					Column col = getColumnByIndex(i);
					tableWidth += col.getWidth();
				}
				return tableWidth;
			} else{
				return PositiveLength.parseDouble(sWidth, Unit.MILLIMETER);
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Set the width of the table (in Millimeter).
	 * <p>
	 * Throw an UnsupportedOperationException if the table is part of a
	 * spreadsheet document that does not allow to change the table size,
	 * because spreadsheet is not allow user to set the table size.
	 * 
	 * @param width
	 *            the width that need to set (in Millimeter).
	 *            <p>
	 *            An UnsupportedOperationException will be thrown if the table
	 *            is in the spreadsheet document.
	 */
	public void setWidth(double width) {
		if (!mIsSpreadsheet) {
			double roundingFactor = 10000.0;
			//TODO:need refactor to PositiveLength.
			double inValue = Math.round(roundingFactor * width / Unit.INCH.unitInMillimiter()) / roundingFactor;
			String sWidthIN = String.valueOf(inValue) + Unit.INCH.abbr();
			mTableElement.setProperty(OdfTableProperties.Width, sWidthIN);
			// if the width is changed, we should also change the table:align
			// properties if it is "margins"
			// otherwise the width seems not changed
			String alineStyle = mTableElement.getProperty(StyleTablePropertiesElement.Align);
			if (TableAlignAttribute.Value.MARGINS.toString().equals(alineStyle)) {
				mTableElement.setProperty(StyleTablePropertiesElement.Align, TableAlignAttribute.Value.LEFT.toString());
			}
		} else {
			throw new UnsupportedOperationException();
		}
	}

	static void setLeftTopBorderStyleProperties(OdfStyle style) {
		style.setProperty(StyleTableCellPropertiesElement.Padding, "0.0382in");
		style.setProperty(StyleTableCellPropertiesElement.BorderLeft, "0.0007in solid #000000");
		style.setProperty(StyleTableCellPropertiesElement.BorderRight, "none");
		style.setProperty(StyleTableCellPropertiesElement.BorderTop, "0.0007in solid #000000");
		style.setProperty(StyleTableCellPropertiesElement.BorderBottom, "0.0007in solid #000000");
	}

	static void setRightTopBorderStyleProperties(OdfStyle style) {
		style.setProperty(StyleTableCellPropertiesElement.Padding, "0.0382in");
		style.setProperty(StyleTableCellPropertiesElement.Border, "0.0007in solid #000000");
	}

	static void setLeftBottomBorderStylesProperties(OdfStyle style) {
		style.setProperty(StyleTableCellPropertiesElement.Padding, "0.0382in");
		style.setProperty(StyleTableCellPropertiesElement.BorderLeft, "0.0007in solid #000000");
		style.setProperty(StyleTableCellPropertiesElement.BorderRight, "none");
		style.setProperty(StyleTableCellPropertiesElement.BorderTop, "none");
		style.setProperty(StyleTableCellPropertiesElement.BorderBottom, "0.0007in solid #000000");

	}

	static void setRightBottomBorderStylesProperties(OdfStyle style) {
		style.setProperty(StyleTableCellPropertiesElement.Padding, "0.0382in");
		style.setProperty(StyleTableCellPropertiesElement.Border, "0.0007in solid #000000");
		style.setProperty(StyleTableCellPropertiesElement.BorderTop, "none");
		style.setProperty(StyleTableCellPropertiesElement.BorderBottom, "0.0007in solid #000000");
	}
	
	private static TableTableElement createTable(TableContainer container, int numRows, int numCols,
			int headerRowNumber, int headerColumnNumber) throws Exception {
		return createTable(container, numRows, numCols, headerRowNumber, headerColumnNumber, 0, 0);
	}

	private static TableTableElement createTable(TableContainer container, int numRows, int numCols,
			int headerRowNumber, int headerColumnNumber, double marginLeft, double marginRight) throws Exception {
		Document document = getOwnerDocument(container);
		OdfElement containerElement = container.getTableContainerElement();
		OdfFileDom dom = (OdfFileDom) containerElement.getOwnerDocument();
		double tableWidth = getTableWidth(container, marginLeft, marginRight);

		boolean isTextDocument = document instanceof TextDocument;

		// check arguments
		if (numRows < 1 || numCols < 1 || headerRowNumber < 0 || headerColumnNumber < 0 || headerRowNumber > numRows
				|| headerColumnNumber > numCols) {
			throw new IllegalArgumentException("Can not create table with the given parameters:\n" + "Rows " + numRows
					+ ", Columns " + numCols + ", HeaderRows " + headerRowNumber + ", HeaderColumns "
					+ headerColumnNumber);
		}
		OdfOfficeAutomaticStyles styles = null;
		if (dom instanceof OdfContentDom) {
			styles = ((OdfContentDom) dom).getAutomaticStyles();
		} else if (dom instanceof OdfStylesDom) {
			styles = ((OdfStylesDom) dom).getAutomaticStyles();
		}
		// 1. create table element
		TableTableElement newTEle = (TableTableElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
				OdfDocumentNamespace.TABLE, "table"));
		String tablename = getUniqueTableName(container);
		newTEle.setTableNameAttribute(tablename);
		// create style
		OdfStyle tableStyle = styles.newStyle(OdfStyleFamily.Table);
		String stylename = tableStyle.getStyleNameAttribute();
		tableStyle.setProperty(StyleTablePropertiesElement.Width, tableWidth + "in");
		tableStyle.setProperty(StyleTablePropertiesElement.Align, DEFAULT_TABLE_ALIGN);
		if (marginLeft != 0) {
			tableStyle.setProperty(StyleTablePropertiesElement.MarginLeft, (new DecimalFormat("#0.##")
					.format(marginLeft) + Unit.CENTIMETER.abbr()).replace(",", "."));
		}
		if (marginRight != 0) {
			tableStyle.setProperty(StyleTablePropertiesElement.MarginRight, (new DecimalFormat("#0.##")
					.format(marginRight) + Unit.CENTIMETER.abbr()).replace(",", "."));
		}
		newTEle.setStyleName(stylename);

		// 2. create column elements
		// 2.0 create column style
		OdfStyle columnStyle = styles.newStyle(OdfStyleFamily.TableColumn);
		String columnStylename = columnStyle.getStyleNameAttribute();
		// for spreadsheet document, no need compute column width.
		if (isTextDocument) {
			columnStyle.setProperty(StyleTableColumnPropertiesElement.ColumnWidth, IN_FORMAT.format(tableWidth
					/ numCols)
					+ "in");
			columnStyle.setProperty(StyleTableColumnPropertiesElement.RelColumnWidth, Math
					.round(DEFAULT_REL_TABLE_WIDTH / numCols)
					+ "*");
		}
		// 2.1 create header column elements
		if (headerColumnNumber > 0) {
			TableTableHeaderColumnsElement headercolumns = (TableTableHeaderColumnsElement) OdfXMLFactory
					.newOdfElement(dom, OdfName.newName(OdfDocumentNamespace.TABLE, "table-header-columns"));
			TableTableColumnElement headercolumn = (TableTableColumnElement) OdfXMLFactory.newOdfElement(dom, OdfName
					.newName(OdfDocumentNamespace.TABLE, "table-column"));
			if (headerColumnNumber > 1) {
				headercolumn.setTableNumberColumnsRepeatedAttribute(headerColumnNumber);
			} else {
				headercolumn.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
			}
			headercolumns.appendChild(headercolumn);
			newTEle.appendChild(headercolumns);
			headercolumn.setStyleName(columnStylename);
		}
		// 2.2 create common column elements
		TableTableColumnElement columns = (TableTableColumnElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
				OdfDocumentNamespace.TABLE, "table-column"));
		int tableNumberColumnsRepeatedValue = numCols - headerColumnNumber;
		if (tableNumberColumnsRepeatedValue > 1) {
			columns.setTableNumberColumnsRepeatedAttribute(tableNumberColumnsRepeatedValue);
		}
		columns.setStyleName(columnStylename);
		newTEle.appendChild(columns);

		// 3. create row elements
		// 3.0 create 4 kinds of styles
		OdfStyle lefttopStyle = null, leftbottomStyle = null, righttopStyle = null, rightbottomStyle = null;

		if (isTextDocument) {
			lefttopStyle = styles.newStyle(OdfStyleFamily.TableCell);
			setLeftTopBorderStyleProperties(lefttopStyle);

			leftbottomStyle = styles.newStyle(OdfStyleFamily.TableCell);
			setLeftBottomBorderStylesProperties(leftbottomStyle);

			righttopStyle = styles.newStyle(OdfStyleFamily.TableCell);
			setRightTopBorderStyleProperties(righttopStyle);

			rightbottomStyle = styles.newStyle(OdfStyleFamily.TableCell);
			setRightBottomBorderStylesProperties(rightbottomStyle);
		}

		// 3.1 create header row elements
		if (headerRowNumber > 0) {
			TableTableHeaderRowsElement headerrows = (TableTableHeaderRowsElement) OdfXMLFactory.newOdfElement(dom,
					OdfName.newName(OdfDocumentNamespace.TABLE, "table-header-rows"));
			for (int i = 0; i < headerRowNumber; i++) {
				TableTableRowElement aRow = (TableTableRowElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
						OdfDocumentNamespace.TABLE, "table-row"));
				for (int j = 0; j < numCols; j++) {
					TableTableCellElement aCell = (TableTableCellElement) OdfXMLFactory.newOdfElement(dom, OdfName
							.newName(OdfDocumentNamespace.TABLE, "table-cell"));
					if (isTextDocument) {
						if ((j + 1 == numCols) && (i == 0)) {
							aCell.setStyleName(righttopStyle.getStyleNameAttribute());
						} else if (i == 0) {
							aCell.setStyleName(lefttopStyle.getStyleNameAttribute());
						} else if ((j + 1 == numCols) && (i > 0)) {
							aCell.setStyleName(rightbottomStyle.getStyleNameAttribute());
						} else {
							aCell.setStyleName(leftbottomStyle.getStyleNameAttribute());
						}
					}
					aRow.appendChild(aCell);
				}
				headerrows.appendChild(aRow);
			}
			newTEle.appendChild(headerrows);
		}

		// 3.2 create common row elements
		for (int i = headerRowNumber; i < numRows; i++) {
			TableTableRowElement aRow = (TableTableRowElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
					OdfDocumentNamespace.TABLE, "table-row"));
			for (int j = 0; j < numCols; j++) {
				TableTableCellElement aCell = (TableTableCellElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
						OdfDocumentNamespace.TABLE, "table-cell"));
				if (isTextDocument) {
					if ((j + 1 == numCols) && (i == 0)) {
						aCell.setStyleName(righttopStyle.getStyleNameAttribute());
					} else if (i == 0) {
						aCell.setStyleName(lefttopStyle.getStyleNameAttribute());
					} else if ((j + 1 == numCols) && (i > 0)) {
						aCell.setStyleName(rightbottomStyle.getStyleNameAttribute());
					} else {
						aCell.setStyleName(leftbottomStyle.getStyleNameAttribute());
					}
				}
				aRow.appendChild(aCell);
			}
			newTEle.appendChild(aRow);
		}

		return newTEle;
	}

	/**
	 * Apply the formatting specified in the template to corresponding table
	 * cells.
	 * <p>
	 * A table can only be formatted as one type of styles: even-odd-rows or
	 * even-odd-columns. The rule is to check the style of odd rows and even
	 * rows in the template, only if they have one different properties, table:
	 * style-name or table:paragraph-style-name, the table template will be
	 * treated as a even-odd-columns styled table.
	 * <p>
	 * If one style in the template is null, the style of corresponding cells
	 * will be removed. An empty template can be used to remove all the styles
	 * in a table.
	 * 
	 * @param template
	 * @throws IllegalArgumentException
	 *             if the given template is null
	 * @throws Exception
	 *             if content DOM could not be initialized
	 */
	public void applyStyle(TableTemplate template) throws Exception {

		if (template == null)
			throw new IllegalArgumentException(
					"The template cannot null to be applied to a table.");

		Document doc = this.getOwnerDocument();
		OdfOfficeAutomaticStyles styles = doc.getContentDom()
				.getAutomaticStyles();

		// decide row style or column style
		boolean isEqualTableStyle = true;
		boolean isEqualParaStyle = true;
		OdfStyle evenRowsTableStyle = styles.getStyle(template
				.getTableEvenRowsTableStyle(), OdfStyleFamily.TableCell);
		OdfStyle oddRowsTableStyle = styles.getStyle(template
				.getTableOddRowsTableStyle(), OdfStyleFamily.TableCell);
		OdfStyle evenRowsParagraphStyle = styles.getStyle(template
				.getTableEvenRowsParagraphStyle(), OdfStyleFamily.Paragraph);
		OdfStyle oddRowsParagraphStyle = styles.getStyle(template
				.getTableOddRowsParagraphStyle(), OdfStyleFamily.Paragraph);
		if (evenRowsTableStyle != null || oddRowsTableStyle != null)
			isEqualTableStyle = evenRowsTableStyle.compareTo(oddRowsTableStyle) == 0;
		if (evenRowsParagraphStyle != null || oddRowsParagraphStyle != null)
			isEqualParaStyle = evenRowsParagraphStyle
					.compareTo(oddRowsParagraphStyle) == 0;

		Iterator<Row> rowIterator = this.getRowIterator();

		if (rowIterator.hasNext()) { // first row
			Row currentRow = rowIterator.next();
			String firstCellTableStyle = template
					.getExtendedTableStyleByType(TableTemplate.ExtendedStyleType.FIRSTROWSTARTCOLUM);
			String firstCellParagraphStyle = template
					.getExtendedParagraphStyleByType(TableTemplate.ExtendedStyleType.FIRSTROWSTARTCOLUM);
			String lastCellTableStyle = template
					.getExtendedTableStyleByType(TableTemplate.ExtendedStyleType.FIRSTROWENDCOLUMN);
			String lastCellParagraphStyle = template
					.getExtendedParagraphStyleByType(TableTemplate.ExtendedStyleType.FIRSTROWENDCOLUMN);
			String evenCellTableStyle = template.getTableFirstRowTableStyle();
			String evenCellParagraphStyle = template
					.getTableFirstRowParagraphStyle();
			String oddCellTableStyle = evenCellTableStyle;
			String oddCellParagraphStyle = evenCellParagraphStyle;

			applyStyleToRow(template, currentRow, firstCellTableStyle,
					oddCellTableStyle, evenCellTableStyle, lastCellTableStyle,
					firstCellParagraphStyle, oddCellParagraphStyle,
					evenCellParagraphStyle, lastCellParagraphStyle);

			int line = 0;
			while (rowIterator.hasNext()) {
				currentRow = rowIterator.next();
				line++;

				if (!rowIterator.hasNext()) { // last row
					firstCellTableStyle = template
							.getExtendedTableStyleByType(TableTemplate.ExtendedStyleType.LASTROWSTARTCOLUMN);
					firstCellParagraphStyle = template
							.getExtendedParagraphStyleByType(TableTemplate.ExtendedStyleType.LASTROWSTARTCOLUMN);
					lastCellTableStyle = template
							.getExtendedTableStyleByType(TableTemplate.ExtendedStyleType.LASTROWENDCOLUMN);
					lastCellParagraphStyle = template
							.getExtendedParagraphStyleByType(TableTemplate.ExtendedStyleType.LASTROWENDCOLUMN);
					oddCellTableStyle = evenCellTableStyle = template
							.getTableLastRowTableStyle();
					oddCellParagraphStyle = evenCellParagraphStyle = template
							.getTableLastRowParagraphStyle();

					applyStyleToRow(template, currentRow, firstCellTableStyle,
							oddCellTableStyle, evenCellTableStyle,
							lastCellTableStyle, firstCellParagraphStyle,
							oddCellParagraphStyle, evenCellParagraphStyle,
							lastCellParagraphStyle);

				} else if (!isEqualTableStyle || !isEqualParaStyle) {
					firstCellTableStyle = template
							.getTableFirstColumnTableStyle();
					firstCellParagraphStyle = template
							.getTableFirstColumnParagraphStyle();
					lastCellTableStyle = template
							.getTableLastColumnTableStyle();
					lastCellParagraphStyle = template
							.getTableLastColumnParagraphStyle();

					if (line % 2 != 0) { // odd row

						oddCellTableStyle = evenCellTableStyle = template
								.getTableOddRowsTableStyle();
						oddCellParagraphStyle = evenCellParagraphStyle = template
								.getTableOddRowsParagraphStyle();
						applyStyleToRow(template, currentRow,
								firstCellTableStyle, oddCellTableStyle,
								evenCellTableStyle, lastCellTableStyle,
								firstCellParagraphStyle, oddCellParagraphStyle,
								evenCellParagraphStyle, lastCellParagraphStyle);
					} else { // even row

						oddCellTableStyle = evenCellTableStyle = template
								.getTableEvenRowsTableStyle();
						oddCellParagraphStyle = evenCellParagraphStyle = template
								.getTableEvenRowsParagraphStyle();

						applyStyleToRow(template, currentRow,
								firstCellTableStyle, oddCellTableStyle,
								evenCellTableStyle, lastCellTableStyle,
								firstCellParagraphStyle, oddCellParagraphStyle,
								evenCellParagraphStyle, lastCellParagraphStyle);
					}

				} else { // even&odd column
					firstCellTableStyle = template
							.getTableFirstColumnTableStyle();
					firstCellParagraphStyle = template
							.getTableFirstColumnParagraphStyle();
					lastCellTableStyle = template
							.getTableLastColumnTableStyle();
					lastCellParagraphStyle = template
							.getTableLastColumnParagraphStyle();
					evenCellTableStyle = template
							.getTableEvenColumnsTableStyle();
					evenCellParagraphStyle = template
							.getTableEvenColumnsParagraphStyle();
					oddCellTableStyle = template.getTableOddColumnsTableStyle();
					oddCellParagraphStyle = template
							.getTableOddColumnsParagraphStyle();
					applyStyleToRow(template, currentRow, firstCellTableStyle,
							oddCellTableStyle, evenCellTableStyle,
							lastCellTableStyle, firstCellParagraphStyle,
							oddCellParagraphStyle, evenCellParagraphStyle,
							lastCellParagraphStyle);
				}
			}

		}

	}

	private void applyStyleToRow(TableTemplate template, Row row,
			String firstCellTableStyle, String oddCellTableStyle,
			String evenCellTableStyle, String lastCellTableStyle,
			String firstCellParagraphStyle, String oddCellParagraphStyle,
			String evenCellParagraphStyle, String lastCellParagraphStyle) {
		int cellIndex = 0;
		int mnRepeatedIndex = row.getRowsRepeatedNumber();
		int lastIndex = row.getCellCount() - 1;
		Cell cell;
		String tableStyle, paraStyle;
		for (Node n : new DomNodeList(row.getOdfElement().getChildNodes())) {
			if (n instanceof TableTableCellElementBase) {
				cell = this.getCellInstance((TableTableCellElementBase) n, 0,
						mnRepeatedIndex);
				if (cell.getColumnsRepeatedNumber() > 1)
					lastIndex -= cell.getColumnsRepeatedNumber() - 1;
				if (cellIndex == 0) {
					tableStyle = firstCellTableStyle;
					paraStyle = firstCellParagraphStyle;
				} else if (cellIndex == lastIndex) {
					tableStyle = lastCellTableStyle;
					paraStyle = lastCellParagraphStyle;
				} else if (cellIndex % 2 == 0) {
					tableStyle = evenCellTableStyle;
					paraStyle = evenCellParagraphStyle;
				} else {
					tableStyle = oddCellTableStyle;
					paraStyle = oddCellParagraphStyle;
				}
				cell.setCellStyleName(tableStyle);
				Iterator<Paragraph> paraIterator = cell.getParagraphIterator();
				while (paraIterator.hasNext()) {
					Paragraph t = paraIterator.next();
					t.getOdfElement().setStyleName(paraStyle);
				}
				cellIndex++;
			}
		}
	}

	private static String getUniqueTableName(TableContainer container) {
		List<Table> tableList = container.getTableList();
		boolean notUnique = true;

		String tablename = "Table" + (tableList.size() + 1);

		while (notUnique) {
			notUnique = false;
			for (int i = 0; i < tableList.size(); i++) {
				if (tableList.get(i).getTableName() != null) {
					if (tableList.get(i).getTableName().equalsIgnoreCase(tablename)) {
						notUnique = true;
						break;
					}
				}
			}
			if (notUnique) {
				tablename = tablename + Math.round(Math.random() * 10);
			}
		}

		return tablename;

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
			if (n instanceof TableTableRowsElement) {
				for (Node nn : new DomNodeList(n.getChildNodes())) {
					if (nn instanceof TableTableRowElement) {
						result += ((TableTableRowElement) nn).getTableNumberRowsRepeatedAttribute();
					}
				}
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
			// TODO: how about <table:table-column-group>
			if (n instanceof TableTableHeaderColumnsElement) {
				result += getHeaderColumnCount((TableTableHeaderColumnsElement) n);
			}

			// <table:table-columns>
			if (n instanceof TableTableColumnsElement) {
				result += getColumnsCount((TableTableColumnsElement) n);
			}

			if (n instanceof TableTableColumnElement) {
				result += ((TableTableColumnElement) n).getTableNumberColumnsRepeatedAttribute();
			}
			// as different type of elements appear in order, so if n is one of
			// the the following elements, computing will stop. It's helpful
			// when the table has lots of rows.
			if (n instanceof TableTableHeaderRowsElement || n instanceof TableTableRowsElement
					|| n instanceof TableTableRowGroupElement || n instanceof TableTableRowElement) {
				break;
			}
		}
		return result;
	}

	/**
	 * This method is invoked by appendRow. When a table has no row, the first
	 * row is a default row.
	 */
	private TableTableRowElement createDefaultRow(int columnCount, boolean createRepeatedCell) {
		OdfFileDom dom = (OdfFileDom) mTableElement.getOwnerDocument();
		TableTableRowElement aRow = (TableTableRowElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
				OdfDocumentNamespace.TABLE, "table-row"));
		if (createRepeatedCell) {
			TableTableCellElement aCell = (TableTableCellElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
					OdfDocumentNamespace.TABLE, "table-cell"));
			if (columnCount > 1) {
				aCell.setTableNumberColumnsRepeatedAttribute(columnCount);
			}
			if (!mIsSpreadsheet) {
				OdfOfficeAutomaticStyles automaticStyles = mTableElement.getAutomaticStyles();
				OdfStyle borderStyle = automaticStyles.newStyle(OdfStyleFamily.TableCell);
				setRightTopBorderStyleProperties(borderStyle);
				aCell.setStyleName(borderStyle.getStyleNameAttribute());
			}
			aRow.appendChild(aCell);
		} else {
			OdfStyle lefttopStyle = null, righttopStyle = null;
			// create 2 kinds of styles
			if (!mIsSpreadsheet) {
				OdfOfficeAutomaticStyles automaticStyles = mTableElement.getAutomaticStyles();
				lefttopStyle = automaticStyles.newStyle(OdfStyleFamily.TableCell);
				setLeftTopBorderStyleProperties(lefttopStyle);
				righttopStyle = automaticStyles.newStyle(OdfStyleFamily.TableCell);
				setRightTopBorderStyleProperties(righttopStyle);
			}
			for (int j = 0; j < columnCount; j++) {
				TableTableCellElement aCell = (TableTableCellElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
						OdfDocumentNamespace.TABLE, "table-cell"));
				if (!mIsSpreadsheet) {
					if (j + 1 == columnCount) {
						aCell.setStyleName(righttopStyle.getStyleNameAttribute());
					} else {
						aCell.setStyleName(lefttopStyle.getStyleNameAttribute());
					}
				}
				aRow.appendChild(aCell);
			}
		}
		return aRow;
	}

	/**
	 * Append a row to the end of the table. The style of new row is same with
	 * the last row in the table.
	 * <p>
	 * Since SIMPLE supports automatic table expansion. Whenever a cell outside
	 * the current table is addressed the table is instantly expanded. Method
	 * <code>getCellByPosition</code> can randomly access any cell, no matter it
	 * in or out of the table original range.
	 * 
	 * @return a new appended row
	 * @see #appendRows(int)
	 * @see #getRowByIndex(int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 */
	public Row appendRow() {
		// find append position
		Node childNode = mTableElement.getLastChild();
		// where is the new row inserted before.
		Node positionNode = null;
		// row style and structure clone from.
		TableTableRowElement refRowElement = null;
		TableTableRowElement newRow = null;
		if (childNode instanceof TableNamedExpressionsElement) {
			childNode = childNode.getPreviousSibling();
			positionNode = childNode;
		}
		if (childNode instanceof TableTableRowElement) {
			refRowElement = (TableTableRowElement) childNode;
		}
		// TODO: what about childNode instanceof TableTableHeaderRowsElement,
		// TableTableRowsElement or TableTableRowGroupElement
		int columnCount = getColumnCount();
		// no row, create a default row
		if (refRowElement == null) {
			newRow = createDefaultRow(columnCount, true);
			mTableElement.appendChild(newRow);
		} else {
			newRow = (TableTableRowElement) OdfXMLFactory.newOdfElement((OdfFileDom) mTableElement.getOwnerDocument(),
					OdfName.newName(OdfDocumentNamespace.TABLE, "table-row"));
			TableTableCellElementBase cellElement = (TableTableCellElementBase) refRowElement.getFirstChild();
			int i = 1;
			while (cellElement != null && i <= columnCount) {
				// covered element
				String tableNameSpace = OdfDocumentNamespace.TABLE.getUri();
				if (cellElement instanceof TableCoveredTableCellElement) {
					TableCoveredTableCellElement coveredCellEle = (TableCoveredTableCellElement) cellElement;
					// find cover cell element
					TableTableRowElement aRowEle = (TableTableRowElement) (coveredCellEle.getParentNode()
							.getPreviousSibling());
					while (aRowEle != null) {
						// the cover cell and the first covered cell must have
						// the same column index.
						TableTableCellElementBase coverCellEle = (TableTableCellElementBase) (aRowEle.getFirstChild());
						int j = coverCellEle.getTableNumberColumnsRepeatedAttribute();
						while (j < i) {
							coverCellEle = (TableTableCellElementBase) (coverCellEle.getNextSibling());
							if (coverCellEle instanceof TableTableCellElement) {
								j += (coverCellEle.getTableNumberColumnsRepeatedAttribute() * (((TableTableCellElement) coverCellEle)
										.getTableNumberColumnsSpannedAttribute()));
							} else {
								j += coverCellEle.getTableNumberColumnsRepeatedAttribute();
							}
						}
						// find the cover cell, now start cell clone.
						if (coverCellEle instanceof TableTableCellElement) {
							TableTableCellElement newCellEle = (TableTableCellElement) (coverCellEle.cloneNode(true));
							cleanCell(newCellEle);
							newCellEle.removeAttributeNS(tableNameSpace, "number-rows-spanned");
							newRow.appendChild(newCellEle);
							// deal with the following covered cell, spread
							// sheet need change these covered cell to cell.
							if (mIsSpreadsheet) {
								// update column repeated number.
								int columnsSpannedNumber = newCellEle.getTableNumberColumnsSpannedAttribute();
								newCellEle.removeAttributeNS(tableNameSpace, "number-columns-spanned");
								int newColumnRepeatedNumber = newCellEle.getTableNumberColumnsRepeatedAttribute()
										* columnsSpannedNumber;
								if (newColumnRepeatedNumber > 1) {
									newCellEle.setTableNumberColumnsRepeatedAttribute(newColumnRepeatedNumber);
								} else {
									newCellEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
								}
								// ignore the following covered cell of
								// reference row.
								// added by Daisy because of a bug in demo4
								// cellElement is a covered cell. coverCellEle
								// is its cover cell.
								// below codes will count
								// newColumnRepeatedNumber covered cell.
								int tempi = newColumnRepeatedNumber;
								while (tempi > 0) {
									int iColumnRepeatedNumber = cellElement.getTableNumberColumnsRepeatedAttribute();
									if (iColumnRepeatedNumber > tempi) {
										// split covered cell
										if (cellElement instanceof TableCoveredTableCellElement) {
											cellElement.setTableNumberColumnsRepeatedAttribute(iColumnRepeatedNumber
													- tempi);
											TableTableCellElementBase newCoveredCellEle = (TableTableCellElementBase) cellElement
													.cloneNode(true);
											cleanCell(newCoveredCellEle);
											if (tempi > 1) {
												newCoveredCellEle.setTableNumberColumnsRepeatedAttribute(tempi);
											} else {
												newCoveredCellEle.removeAttributeNS(
														OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
											}
											refRowElement.insertBefore(newCoveredCellEle, cellElement);
											cellElement = newCoveredCellEle;
										}
									}
									tempi = tempi - cellElement.getTableNumberColumnsRepeatedAttribute();
									i = i + cellElement.getTableNumberColumnsRepeatedAttribute();
									if (!(cellElement instanceof TableCoveredTableCellElement) && (tempi > 0)){
										Logger.getLogger(Table.class.getName()).log(Level.FINE,	"Not covered cell was ignored");
									}
									cellElement = (TableTableCellElementBase) (cellElement.getNextSibling());
									// while ((cellElement != null) &&
									// (cellElement instanceof
									// TableCoveredTableCellElement)) {
									// cellElement = (TableTableCellElementBase)
									// (cellElement.getNextSibling());
									// }
								}
								// i += newColumnRepeatedNumber;
							} else {
								// clone the following covered cell of reference
								// row.
								// added by Daisy because of a bug in demo4
								cellElement = (TableTableCellElementBase) cellElement.getNextSibling();
								i += cellElement.getTableNumberColumnsRepeatedAttribute();
								int newColumnSpanNumber = newCellEle.getTableNumberColumnsSpannedAttribute();
								while ((cellElement != null) && (cellElement instanceof TableCoveredTableCellElement)
										&& (newColumnSpanNumber > 1)) {
									TableCoveredTableCellElement newCoveredCellElement = (TableCoveredTableCellElement) cellElement
											.cloneNode(true);
									cleanCell(newCoveredCellElement);
									newRow.appendChild(newCoveredCellElement);
									i += cellElement.getTableNumberColumnsRepeatedAttribute();
									cellElement = (TableTableCellElementBase) cellElement.getNextSibling();
									newColumnSpanNumber--;
								}
							}
							break;
						}
						// continue find cover cell
						Node preNode = aRowEle.getPreviousSibling();
						if (preNode instanceof TableTableRowElement) {
							aRowEle = (TableTableRowElement) preNode;
						} else {
							// </table:table-header-rows>
							aRowEle = (TableTableRowElement) (preNode.getLastChild());
						}
					}
				} else {
					TableTableCellElement newCellEle = (TableTableCellElement) cellElement.cloneNode(true);
					cleanCell(newCellEle);
					newRow.appendChild(newCellEle);
					Integer tableNumberColumnsRepeated = newCellEle.getTableNumberColumnsRepeatedAttribute();
					Integer tableNumberColumnsSpanned = newCellEle.getTableNumberColumnsSpannedAttribute();
					i += tableNumberColumnsRepeated * tableNumberColumnsSpanned;
					cellElement = (TableTableCellElementBase) cellElement.getNextSibling();
					if (tableNumberColumnsSpanned > 1) {
						int j = 1;
						if (mIsSpreadsheet) {
							newCellEle.removeAttributeNS(tableNameSpace, "number-columns-spanned");
							int newColumnRepeatedNumber = tableNumberColumnsRepeated * tableNumberColumnsSpanned;
							if (newColumnRepeatedNumber > 1) {
								newCellEle.setTableNumberColumnsRepeatedAttribute(newColumnRepeatedNumber);
							} else {
								newCellEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
							}
							// cellElement is not a covered cell.
							// below codes will count
							// (newColumnRepeatedNumber-1) covered cell.
							int tempi = newColumnRepeatedNumber;
							while (tempi > 1) {
								int iColumnRepeatedNumber = cellElement.getTableNumberColumnsRepeatedAttribute();
								if (iColumnRepeatedNumber > tempi + 1) {
									// split covered cell
									if (cellElement instanceof TableCoveredTableCellElement) {
										cellElement.setTableNumberColumnsRepeatedAttribute(iColumnRepeatedNumber
												- tempi + 1);
										TableTableCellElementBase newCoveredCellEle = (TableTableCellElementBase) cellElement
												.cloneNode(true);
										cleanCell(newCoveredCellEle);
										newCoveredCellEle.setTableNumberColumnsRepeatedAttribute(tempi - 1);
										refRowElement.insertBefore(newCoveredCellEle, cellElement);
										cellElement = newCoveredCellEle;
									}
								}
								tempi = tempi - cellElement.getTableNumberColumnsRepeatedAttribute();
								if (!(cellElement instanceof TableCoveredTableCellElement) && (tempi > 1)){
									Logger.getLogger(Table.class.getName()).log(Level.FINE,	"Not covered cell was ignored");
								}
								cellElement = (TableTableCellElementBase) (cellElement.getNextSibling());
							}
						} else {
							while ((j < tableNumberColumnsSpanned) && (cellElement != null)) {
								int iColumnRepeatedNumber = cellElement.getTableNumberColumnsRepeatedAttribute();
								if (iColumnRepeatedNumber > tableNumberColumnsSpanned - j) {
									// split covered cell
									if (cellElement instanceof TableCoveredTableCellElement) {
										cellElement.setTableNumberColumnsRepeatedAttribute(iColumnRepeatedNumber
												- tableNumberColumnsSpanned + j);
										TableTableCellElementBase newCoveredCellEle = (TableTableCellElementBase) cellElement
												.cloneNode(true);
										cleanCell(newCoveredCellEle);
										newCoveredCellEle
												.setTableNumberColumnsRepeatedAttribute(tableNumberColumnsSpanned - j);
										refRowElement.insertBefore(newCoveredCellEle, cellElement);
										cellElement = newCoveredCellEle;
									}
								}
								TableTableCellElementBase newCoveredCellEle = (TableTableCellElementBase) cellElement
										.cloneNode(true);
								cleanCell(newCoveredCellEle);
								newRow.appendChild(newCoveredCellEle);
								j += newCoveredCellEle.getTableNumberColumnsRepeatedAttribute();
								cellElement = (TableTableCellElementBase) cellElement.getNextSibling();
							}
						}
					}
				}
			}
			if (positionNode == null) {
				mTableElement.appendChild(newRow);
			} else {
				mTableElement.insertBefore(newRow, positionNode);
			}
		}
		return getRowInstance(newRow, 0);
	}

	/**
	 * Append a specific number of rows to the end of the table. The style of
	 * new rows are same with the last row in the table.
	 * <p>
	 * Since SIMPLE supports automatic table expansion. Whenever a cell outside
	 * the current table is addressed the table is instantly expanded. Method
	 * <code>getCellByPosition</code> can randomly access any cell, no matter it
	 * in or out of the table original range.
	 * 
	 * @param rowCount
	 *            is the number of rows to be appended.
	 * @return a list of new appended rows
	 * @see #appendRow()
	 * @see #getRowByIndex(int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 */
	public List<Row> appendRows(int rowCount) {
		return appendRows(rowCount, false);
	}

	List<Row> appendRows(int rowCount, boolean isCleanStyle) {
		List<Row> resultList = new ArrayList<Row>();
		if (rowCount <= 0) {
			return resultList;
		}
		if (isUseRepeat()) {
			Row firstRow = appendRow();
			resultList.add(firstRow);
			if (rowCount > 1) {
				firstRow.setRowsRepeatedNumber(rowCount);
				TableTableRowElement firstRowEle = firstRow.getOdfElement();
				for (int i = 1; i < rowCount; i++) {
					Row row = getRowInstance(firstRowEle, i);
					resultList.add(row);
				}
			}
		} else {
			for (int i = 0; i < rowCount; i++) {
				Row firstRow = appendRow();
				resultList.add(firstRow);
			}
		}
		if (isCleanStyle) {
			// clean style name
			String tableNameSpace = OdfDocumentNamespace.TABLE.getUri();
			for (Row row : resultList) {
				Node cellE = row.getOdfElement().getFirstChild();
				while (cellE != null) {
					((TableTableCellElementBase) cellE).removeAttributeNS(tableNameSpace, "style-name");
					cellE = cellE.getNextSibling();
				}
			}
		}
		return resultList;
	}

	/**
	 * Append a column at the end of the table. The style of new column is same
	 * with the last column in the table.
	 * <p>
	 * Since SIMPLE supports automatic table expansion. Whenever a cell outside
	 * the current table is addressed the table is instantly expanded. Method
	 * <code>getCellByPosition</code> can randomly access any cell, no matter it
	 * in or out of the table original range.
	 * 
	 * @return a new appended column
	 * @see #appendColumns(int)
	 * @see #getColumnByIndex(int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 */
	public Column appendColumn() {
		List<Column> columnList = getColumnList();
		int columnCount = columnList.size();

		TableTableColumnElement newColumn;
		OdfElement positonElement = getRowElementByIndex(0);
		if (positonElement.getParentNode() instanceof TableTableHeaderRowsElement) {
			positonElement = (OdfElement) positonElement.getParentNode();
		}

		// Moved before column elements inserted
		// insert cells firstly
		// Or else, wrong column number will be gotten in updateCellRepository,
		// which will cause a NPE.
		// insertCellBefore()->splitRepeatedRows()->updateRowRepository()->updateCellRepository()
		List<Row> rowList = getRowList();
		for (int i = 0; i < rowList.size();) {
			Row row1 = rowList.get(i);
			row1.insertCellBefore(row1.getCellByIndex(columnCount - 1), null);
			i = i + row1.getRowsRepeatedNumber();
		}

		// insert columns secondly
		if (columnList.size() == 0) // no column, create a new column
		{
			OdfStyle columnStyle = mTableElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableColumn);
			String columnStylename = columnStyle.getStyleNameAttribute();
			columnStyle.setProperty(StyleTableColumnPropertiesElement.ColumnWidth, DEFAULT_TABLE_WIDTH + "in");
			columnStyle.setProperty(StyleTableColumnPropertiesElement.RelColumnWidth, DEFAULT_REL_TABLE_WIDTH + "*");

			newColumn = (TableTableColumnElement) OdfXMLFactory.newOdfElement((OdfFileDom) mTableElement
					.getOwnerDocument(), OdfName.newName(OdfDocumentNamespace.TABLE, "table-column"));
			newColumn.setStyleName(columnStylename);
			mTableElement.insertBefore(newColumn, positonElement);
		} else { // has column, append a same column as the last one.
			TableTableColumnElement refColumn = columnList.get(columnList.size() - 1).getOdfElement();
			newColumn = (TableTableColumnElement) refColumn.cloneNode(true);
			String tableNameSpace = OdfDocumentNamespace.TABLE.getUri();
			newColumn.removeAttributeNS(tableNameSpace, "number-columns-repeated");
			mTableElement.insertBefore(newColumn, positonElement);
		}

		return getColumnInstance(newColumn, 0);
	}

	/**
	 * Append a specific number of columns to the right of the table. The style
	 * of new columns are same with the rightmost column in the table.
	 * <p>
	 * Since SIMPLE supports automatic table expansion. Whenever a cell outside
	 * the current table is addressed the table is instantly expanded. Method
	 * <code>getCellByPosition</code> can randomly access any cell, no matter it
	 * in or out of the table original range.
	 * 
	 * @param columnCount
	 *            is the number of columns to be appended.
	 * @return a list of new appended columns
	 * @see #appendColumn()
	 * @see #getColumnByIndex(int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 */
	public List<Column> appendColumns(int columnCount) {
		return appendColumns(columnCount, false);
	}

	List<Column> appendColumns(int columnCount, boolean isCleanStyle) {
		List<Column> resultList = new ArrayList<Column>();
		if (columnCount <= 0) {
			return resultList;
		}
		Column firstClm = appendColumn();
		resultList.add(firstClm);
		if (columnCount > 1) {
			List<Column> list = insertColumnsBefore((getColumnCount() - 1), (columnCount - 1));
			resultList.addAll(list);
		}
		// clean style name
		if (isCleanStyle) {
			for (Column column : resultList) {
				int length = column.getCellCount();
				for (int i = 0; i < length; i++) {
					TableTableCellElementBase cellElement = column.getCellByIndex(i).mCellElement;
					cellElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "style-name");
				}
			}
		}
		return resultList;
	}

	/**
	 * This method is to insert a numbers of row
	 */
	private List<Row> insertMultipleRowsBefore(Row refRow, Row positionRow, int count) {
		List<Row> resultList = new ArrayList<Row>();
		int j = 1;

		if (count <= 0) {
			return resultList;
		}

		Row firstRow = insertRowBefore(refRow, positionRow, getColumnCount());
		resultList.add(firstRow);

		if (count == 1) {
			return resultList;
		}
		TableTableRowElement rowEle = firstRow.getOdfElement();
		for (int i = 0; i < getColumnCount();) {
			Cell refCell = refRow.getCellByIndex(i);
			if (!refCell.isCoveredElement()) {
				int coveredHeigth = refCell.getRowSpannedNumber();
				if (coveredHeigth > 1) {
					refCell.setRowSpannedNumber(coveredHeigth + 1);
				}
			}
			i += refCell.getColumnsRepeatedNumber();
		}
		if (isUseRepeat()) {
			firstRow.setRowsRepeatedNumber(count);
			while (j < count) {
				resultList.add(getRowInstance(rowEle, j));
				j++;
			}
		} else {
			while (j < count) {
				TableTableRowElement newRowEle = (TableTableRowElement) rowEle.cloneNode(true);
				newRowEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-repeated");
				mTableElement.insertBefore(newRowEle, positionRow.getOdfElement());
				resultList.add(getRowInstance(newRowEle, 0));
				j++;
			}
		}
		return resultList;
	}

	// only insert one Row
	private Row insertRowBefore(Row refRow, Row positionRow, int columnCount) {
		TableTableRowElement aRow = (TableTableRowElement) OdfXMLFactory.newOdfElement((OdfFileDom) mTableElement
				.getOwnerDocument(), OdfName.newName(OdfDocumentNamespace.TABLE, "table-row"));
		int coveredLength = 0, coveredHeigth = 0;
		for (int i = 0; i < columnCount;) {
			Cell refCell = refRow.getCellByIndex(i);
			int columnsRepeatedNumber = refCell.getColumnsRepeatedNumber();
			if (!refCell.isCoveredElement()) // not cover element
			{
				TableTableCellElement aCellEle = (TableTableCellElement) refCell.getOdfElement();
				coveredHeigth = aCellEle.getTableNumberRowsSpannedAttribute();
				if (coveredHeigth == 1) {
					TableTableCellElement newCellEle = (TableTableCellElement) aCellEle.cloneNode(true);
					cleanCell(newCellEle);
					aRow.appendChild(newCellEle);
				} else { // cover more rows
					aCellEle.setTableNumberRowsSpannedAttribute(coveredHeigth + 1);
					TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) OdfXMLFactory
							.newOdfElement((OdfFileDom) mTableElement.getOwnerDocument(), OdfName.newName(
									OdfDocumentNamespace.TABLE, "covered-table-cell"));
					if (columnsRepeatedNumber > 1) {
						newCellEle.setTableNumberColumnsRepeatedAttribute(columnsRepeatedNumber);
					} else {
						newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
					}
					aRow.appendChild(newCellEle);
				}

				coveredLength = aCellEle.getTableNumberColumnsSpannedAttribute() - columnsRepeatedNumber;
				i = i + columnsRepeatedNumber;
			} else {
				TableCoveredTableCellElement aCellEle = (TableCoveredTableCellElement) refCell.getOdfElement();
				if (coveredLength >= 1) {
					TableCoveredTableCellElement newCellEle = (TableCoveredTableCellElement) aCellEle.cloneNode(true);
					aRow.appendChild(newCellEle);
					coveredLength -= newCellEle.getTableNumberColumnsRepeatedAttribute();
				} else {
					TableTableCellElement coveredCell = (TableTableCellElement) refCell.getCoverCell().getOdfElement();
					TableTableCellElement newCellEle = (TableTableCellElement) coveredCell.cloneNode(true);
					cleanCell(newCellEle);
					newCellEle.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-spanned");
					aRow.appendChild(newCellEle);
					coveredLength = coveredCell.getTableNumberColumnsSpannedAttribute() - columnsRepeatedNumber;
				}
				i = i + columnsRepeatedNumber;
			}
		}
		if (positionRow == null) {
			mTableElement.appendChild(aRow);
		} else {
			mTableElement.insertBefore(aRow, positionRow.getOdfElement());
		}

		return getRowInstance(aRow, 0);
	}

	void cleanCell(TableTableCellElementBase newCellEle) {
		String officeNameSpaceURI = OdfDocumentNamespace.OFFICE.getUri();
		String tableNameSpaceURI = OdfDocumentNamespace.TABLE.getUri();
		newCellEle.removeAttributeNS(officeNameSpaceURI, "value");
		newCellEle.removeAttributeNS(officeNameSpaceURI, "date-value");
		newCellEle.removeAttributeNS(officeNameSpaceURI, "time-value");
		newCellEle.removeAttributeNS(officeNameSpaceURI, "boolean-value");
		newCellEle.removeAttributeNS(officeNameSpaceURI, "string-value");
		newCellEle.removeAttributeNS(tableNameSpaceURI, "formula");
		newCellEle.removeAttributeNS(officeNameSpaceURI, "value-type");
		if (!isCellStyleInheritance()) {
			newCellEle.removeAttributeNS(tableNameSpaceURI, "style-name");
		}
		Node n = newCellEle.getFirstChild();
		while (n != null) {
			Node m = n.getNextSibling();
			if (n instanceof TextPElement || n instanceof TextHElement || n instanceof TextListElement
					|| n instanceof OfficeAnnotationElement) {
				newCellEle.removeChild(n);
			}
			n = m;
		}
	}

	/**
	 * Return an instance of <code>TableTableElement</code> which represents
	 * this feature.
	 * 
	 * @return an instance of <code>TableTableElement</code>
	 */
	public TableTableElement getOdfElement() {
		return mTableElement;
	}

	/**
	 * Insert a specific number of columns before the column whose index is
	 * <code>index</code>.
	 * 
	 * @param index
	 *            is the index of the column to insert before.
	 * @param columnCount
	 *            is the number of columns to insert.
	 * @return a list of new inserted columns
	 */
	public List<Column> insertColumnsBefore(int index, int columnCount) {
		Column refColumn, positionCol;
		String tableNameSpace = OdfDocumentNamespace.TABLE.getUri();
		ArrayList<Column> list = new ArrayList<Column>();
		int columncount = getColumnCount();

		if (index >= columncount) {
			throw new IndexOutOfBoundsException();
		}

		if (index == 0) {
			int iRowCount = getRowCount();
			for (int i = 0; i < iRowCount; i++) {
				Row row = getRowByIndex(i);
				row.insertCellByIndex(index, columnCount);
			}
			refColumn = getColumnByIndex(index);
			positionCol = refColumn;
			// add a single column element to describe columns.
			if (isUseRepeat()) {
				TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement().cloneNode(
						true);
				if (columnCount > 1) {
					newColumnEle.setTableNumberColumnsRepeatedAttribute(columnCount);
				} else {
					newColumnEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
				}
				mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());
				for (int i = 0; i < columnCount; i++) {
					list.add(getColumnInstance(newColumnEle, i));
				}
			} else {
				for (int i = 0; i < columnCount; i++) {
					TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement()
							.cloneNode(true);
					mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());
					newColumnEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
					list.add(getColumnInstance(newColumnEle, 0));
				}
			}
			return list;
		}

		// 1. insert the cell
		int iRowCount = getRowCount();
		for (int i = iRowCount - 1; i >= 0;) {
			Row row = getRowByIndex(i);
			Cell refCell = row.getCellByIndex(index - 1);
			Cell positionCell = null;
			positionCell = row.getCellByIndex(index);
			row.insertCellBefore(refCell, positionCell, columnCount);
			i = i - row.getRowsRepeatedNumber();
		}

		refColumn = getColumnByIndex(index - 1);
		positionCol = getColumnByIndex(index);
		// 2. insert a <table:table-column>
		if (refColumn.getOdfElement() == positionCol.getOdfElement()) {
			TableTableColumnElement column = refColumn.getOdfElement();
			int repeatedCount = column.getTableNumberColumnsRepeatedAttribute();
			TableTableColumnElement columnEle = positionCol.getOdfElement();
			// add a single column element to describe columns.
			if (isUseRepeat()) {
				column.setTableNumberColumnsRepeatedAttribute(repeatedCount + columnCount);
				Column startCol = getColumnInstance(positionCol.getOdfElement(), 0);
				for (int i = repeatedCount + columnCount - 1; i >= columnCount + (index - startCol.getColumnIndex()); i--) {
					updateColumnRepository(columnEle, i - columnCount, columnEle, i);
				}
				for (int i = 0; i < columnCount; i++) {
					list.add(getColumnInstance(column, refColumn.mnRepeatedIndex + 1 + i));
				}
			} else {
				TableTableColumnElement newBeforeColumnEle = (TableTableColumnElement) refColumn.getOdfElement()
						.cloneNode(true);
				if (index > 1) {
					newBeforeColumnEle.setTableNumberColumnsRepeatedAttribute(index);
				} else {
					newBeforeColumnEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
				}
				mTableElement.insertBefore(newBeforeColumnEle, positionCol.getOdfElement());
				for (int i = 0; i < index; i++) {
					updateColumnRepository(columnEle, i, newBeforeColumnEle, i);
				}
				int newAfterCount = repeatedCount - index;
				if (newAfterCount > 1) {
					positionCol.setColumnsRepeatedNumber(newAfterCount);
				} else {
					positionCol.getOdfElement().removeAttributeNS(tableNameSpace, "number-columns-repeated");
				}
				for (int i = repeatedCount - 1; i >= index; i--) {
					updateColumnRepository(columnEle, i, columnEle, i - index);
				}
				for (int i = 0; i < columnCount; i++) {
					TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement()
							.cloneNode(true);
					newColumnEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
					mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());
					list.add(getColumnInstance(newColumnEle, 0));
				}
			}
		} else {
			// add a single column element to describe columns.
			if (isUseRepeat()) {
				TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement().cloneNode(
						true);
				if (columnCount > 1) {
					newColumnEle.setTableNumberColumnsRepeatedAttribute(columnCount);
				} else {
					newColumnEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
				}
				mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());
				for (int i = 0; i < columnCount; i++) {
					list.add(getColumnInstance(newColumnEle, i));
				}
			} else {
				for (int i = 0; i < columnCount; i++) {
					TableTableColumnElement newColumnEle = (TableTableColumnElement) refColumn.getOdfElement()
							.cloneNode(true);
					newColumnEle.removeAttributeNS(tableNameSpace, "number-columns-repeated");
					mTableElement.insertBefore(newColumnEle, positionCol.getOdfElement());
					list.add(getColumnInstance(newColumnEle, 0));
				}
			}
		}
		return list;
	}

	/**
	 * Remove a specific number of columns, starting from the column at
	 * <code>index</code>.
	 * 
	 * @param startIndex
	 *            is the index of the first column to delete.
	 * @param deleteColCount
	 *            is the number of columns to delete.
	 */
	public void removeColumnsByIndex(int startIndex, int deleteColCount) {
		// 0. verify the index
		if (deleteColCount <= 0) {
			return;
		}
		if (startIndex < 0) {
			throw new IllegalArgumentException("startIndex of the deleted columns should not be negative");
		}
		int colCount = getColumnCount();
		if (startIndex >= colCount) {
			throw new IndexOutOfBoundsException("Start column index is out of bound");
		}
		if (startIndex + deleteColCount >= colCount) {
			deleteColCount = colCount - startIndex;
		}

		// 1. remove cell
		for (int i = 0; i < getRowCount(); i++) {
			Row aRow = getRowByIndex(i);
			aRow.removeCellByIndex(startIndex, deleteColCount);
		}

		// 2. remove column
		Column firstColumn;
		for (int i = 0; i < deleteColCount; i++) {
			firstColumn = getColumnByIndex(startIndex);
			int repeatedAttr = firstColumn.getColumnsRepeatedNumber();
			if (repeatedAttr == 1) {
				TableTableColumnElement columnEle = OdfElement.findNextChildNode(TableTableColumnElement.class,
						firstColumn.getOdfElement());
				mTableElement.removeChild(firstColumn.getOdfElement());
				if (i < (deleteColCount - 1)) {
					firstColumn = this.getColumnInstance(columnEle, 0);
				}
			} else {
				if (repeatedAttr > firstColumn.mnRepeatedIndex) {
					firstColumn.setColumnsRepeatedNumber(repeatedAttr - 1);
					Column startCol = this.getColumnInstance(firstColumn.getOdfElement(), 0);
					updateColumnRepository(firstColumn.getOdfElement(), startIndex - startCol.getColumnIndex(), null, 0);
				}
			}
		}

	}
	
	/**
	 * Calculates the width between the left and right margins of the table
	 * container.
	 * 
	 * @param container
	 *            TableContainer
	 * @param marginLeft
	 *            space between left margin and the table
	 * @param marginRight
	 *            space between right margin and the table
	 * @return width that can be attributed at the table (in)
	 */
	private static double getTableWidth(TableContainer container, double marginLeft, double marginRight) {
		String pageWidthStr = null;
		double pageWidth = 0;
		double tableWidth = DEFAULT_TABLE_WIDTH;
		OdfOfficeAutomaticStyles automaticStyles = null;
		try {
			automaticStyles = getOwnerDocument(container).getStylesDom().getAutomaticStyles();
		} catch (Exception e) {
			Logger.getLogger(Table.class.getName()).log(Level.SEVERE,	e.getMessage(), e);
		}
		OdfStylePageLayout pageLayout = automaticStyles.getPageLayout("pm1");
		if (pageLayout == null) {
			pageLayout = automaticStyles.getPageLayout("Mpm1");
		}
		if (pageLayout != null) {
			pageWidthStr = pageLayout.getProperty(StylePageLayoutPropertiesElement.PageWidth);
			if (pageWidthStr != null) {
				pageWidth = Length.parseDouble(pageWidthStr, Unit.CENTIMETER);
			}
			// margins
			double dLeftPageMargin = 0;
			double dRightPageMargin = 0;
			String leftPageMargin = pageLayout.getProperty(StylePageLayoutPropertiesElement.MarginLeft);
			String rightPageMargin = pageLayout.getProperty(StylePageLayoutPropertiesElement.MarginRight);
			if (leftPageMargin != null && rightPageMargin != null) {
				dLeftPageMargin = Length.parseDouble(leftPageMargin, Unit.CENTIMETER);
				dRightPageMargin = Length.parseDouble(rightPageMargin, Unit.CENTIMETER);
			}
			tableWidth = (pageWidth - (dLeftPageMargin + dRightPageMargin + marginLeft + marginRight)) / 2.5399;
			if (tableWidth <= 0) {
				tableWidth = DEFAULT_TABLE_WIDTH;
			}
		}
		return Double.valueOf(new DecimalFormat("#0.###").format(tableWidth).replace(",", ".")).doubleValue();
	}

	private void reviseStyleFromTopRowToMediumRow(Row oldTopRow) {
		if (mIsSpreadsheet)
			return;
		int length = getColumnCount();

		for (int i = 0; i < length;) {
			Cell cell = oldTopRow.getCellByIndex(i);
			if (cell.isCoveredElement()) {
				i = i + cell.getColumnsRepeatedNumber();
				continue;
			}
			OdfStyle styleEle = cell.getStyleHandler().getStyleElementForWrite();
			if (i < length - 1) {
				setLeftBottomBorderStylesProperties(styleEle);
			} else {
				setRightBottomBorderStylesProperties(styleEle);
			}
			i = i + cell.getColumnsRepeatedNumber();
		}
	}

	private void reviseStyleFromMediumRowToTopRow(Row newTopRow) {
		if (mIsSpreadsheet) {
			return;
		}
		int length = getColumnCount();

		for (int i = 0; i < length;) {
			Cell cell = newTopRow.getCellByIndex(i);
			if (cell.isCoveredElement()) {
				i = i + cell.getColumnsRepeatedNumber();
				continue;
			}
			OdfStyle styleEle = cell.getStyleHandler().getStyleElementForWrite();
			if (i < length - 1) {
				setLeftTopBorderStyleProperties(styleEle);
			} else {
				setRightTopBorderStyleProperties(styleEle);
			}
			i = i + cell.getColumnsRepeatedNumber();
		}
	}

	/**
	 * Insert a specific number of rows before the row at <code>index</code>.
	 * 
	 * @param index
	 *            is the index of the row to insert before.
	 * @param rowCount
	 *            is the number of rows to insert.
	 * @return a list of new inserted rows
	 */
	public List<Row> insertRowsBefore(int index, int rowCount) {
		if (index >= getRowCount()) {
			throw new IndexOutOfBoundsException();
		}
		ArrayList<Row> list = new ArrayList<Row>();
		if (index == 0) {
			Row refRow = getRowByIndex(index);
			Row positionRow = refRow;
			// add first row
			Row newFirstRow = insertRowBefore(refRow, positionRow, getColumnCount());
			reviseStyleFromTopRowToMediumRow(refRow);
			list.add(newFirstRow);
			List<Row> rowList = insertMultipleRowsBefore(refRow, refRow, rowCount - 1);
			for (int i = 0; i < rowList.size(); i++) {
				list.add(rowList.get(i));
			}
			return list;
		}

		Row refRow = getRowByIndex(index - 1);
		Row positionRow = getRowByIndex(index);
		// 1. insert a <table:table-row>
		if (refRow.getOdfElement() == positionRow.getOdfElement()) {
			TableTableRowElement row = refRow.getOdfElement();
			int repeatedCount = refRow.getRowsRepeatedNumber();
			refRow.setRowsRepeatedNumber(repeatedCount + rowCount);
			TableTableRowElement rowEle = positionRow.getOdfElement();
			Row startRow = getRowInstance(positionRow.getOdfElement(), 0);
			for (int i = repeatedCount + rowCount - 1; i >= rowCount + (index - startRow.getRowIndex()); i--) {
				updateRowRepository(rowEle, i - rowCount, rowEle, i);
			}
			for (int i = 0; i < rowCount; i++) {
				list.add(getRowInstance(row, refRow.mnRepeatedIndex + 1 + i));
			}
		} else {
			List<Row> newRowList = insertMultipleRowsBefore(refRow, positionRow, rowCount);
			if (index - 1 == 0) {
				// correct styles
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
	public List<Column> getColumnList() {
		ArrayList<Column> list = new ArrayList<Column>();
		TableTableColumnElement colEle = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderColumnsElement) {
				TableTableHeaderColumnsElement headers = (TableTableHeaderColumnsElement) n;
				for (Node m : new DomNodeList(headers.getChildNodes())) {
					if (m instanceof TableTableColumnElement) {
						colEle = (TableTableColumnElement) m;
						int columnsRepeatedNumber = colEle.getTableNumberColumnsRepeatedAttribute();
						for (int i = 0; i < columnsRepeatedNumber; i++) {
							list.add(getColumnInstance(colEle, i));
						}
					}
				}
			}
			if (n instanceof TableTableColumnElement) {
				colEle = (TableTableColumnElement) n;
				int columnsRepeatedNumber = colEle.getTableNumberColumnsRepeatedAttribute();
				for (int i = 0; i < columnsRepeatedNumber; i++) {
					list.add(getColumnInstance(colEle, i));
				}
			}
		}
		return list;
	}
	
	/**
	 * Return an Iterator of the column in this table.
	 * 
	 * @return an Iterator of the column in this table.
	 * @see java.util.Iterator
	 * 
	 * @since 0.5.5
	 */
	public Iterator<Column> getColumnIterator(){
		return new SimpleColumnIterator(this);
	}
	
	/**
	 * Return a list of table rows in the current table.
	 * 
	 * @return a list of table rows
	 */
	public List<Row> getRowList() {
		ArrayList<Row> list = new ArrayList<Row>();
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
	 * Return an Iterator of the row in this table.
	 * 
	 * @return an Iterator of the row in this table.
	 * @see java.util.Iterator
	 * 
	 * @since 0.5.5
	 */
	public Iterator<Row> getRowIterator(){
		return new SimpleRowIterator(this);
	}
	
	/**
	 * Get the column at the specified index. The table will be automatically
	 * expanded, when the given index is outside of the original table.
	 * 
	 * @param index
	 *            the zero-based index of the column.
	 * @return the column at the specified index
	 */
	public Column getColumnByIndex(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index should be nonnegative integer.");
		}
		// expand column as needed.
		int lastIndex = getColumnCount() - 1;
		if (index > lastIndex) {
			appendColumns(index - lastIndex);
		}
		int result = 0;
		Column col = null;
		// TableTableColumnElement colEle=null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderColumnsElement) {
				col = getHeaderColumnByIndex((TableTableHeaderColumnsElement) n, index);
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
				return getColumnInstance(col.getOdfElement(), index - (result - col.getColumnsRepeatedNumber()));
			}
		}
		return null;
	}

	private Row getHeaderRowByIndex(TableTableHeaderRowsElement headers, int nIndex) {
		int result = 0;
		Row row = null;
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

	private Column getHeaderColumnByIndex(TableTableHeaderColumnsElement headers, int nIndex) {
		int result = 0;
		Column col = null;
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
	public Row getRowByIndex(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("index should be nonnegative integer.");
		}
		// expand row as needed.
		int lastIndex = getRowCount() - 1;
		if (index > lastIndex) {
			appendRows(index - lastIndex);
		}
		int result = 0;
		Row row = null;
		for (Node n : new DomNodeList(mTableElement.getChildNodes())) {
			if (n instanceof TableTableHeaderRowsElement) {
				row = getHeaderRowByIndex((TableTableHeaderRowsElement) n, index);
				if (row != null) {
					return row;
				}
				result += getHeaderRowCount((TableTableHeaderRowsElement) n);
			}
			if (n instanceof TableTableRowElement) {
				row = getRowInstance((TableTableRowElement) n, 0);
				result += row.getRowsRepeatedNumber();
			}
			if (n instanceof TableTableRowsElement) {
				for (Node nn : new DomNodeList(n.getChildNodes())) {
					if (nn instanceof TableTableRowElement) {
						row = getRowInstance((TableTableRowElement) nn, 0);
						result += row.getRowsRepeatedNumber();
						if (result > index) {
							return getRowInstance(row.getOdfElement(), index - (result - row.getRowsRepeatedNumber()));
						}
					}
				}
			}
			if (result > index) {
				return getRowInstance(row.getOdfElement(), index - (result - row.getRowsRepeatedNumber()));
			}
		}
		return null;
	}

	/**
	 * Remove the specific number of rows, starting from the row at
	 * <code>index</code>.
	 * 
	 * @param startIndex
	 *            is the zero-based index of the first row to delete.
	 * @param deleteRowCount
	 *            is the number of rows to delete.
	 */
	public void removeRowsByIndex(int startIndex, int deleteRowCount) {
		boolean deleted = false;
		// 0. verify the index
		if (deleteRowCount <= 0) {
			return;
		}
		if (startIndex < 0) {
			throw new IllegalArgumentException("startIndex of the deleted rows should not be negative");
		}
		int rowCount = getRowCount();
		if (startIndex >= rowCount) {
			throw new IndexOutOfBoundsException("Start index out of bound");
		}
		if (startIndex + deleteRowCount >= rowCount) {
			deleteRowCount = rowCount - startIndex;
		}

		// 1. remove row
		Row firstRow = getRowByIndex(startIndex);
		for (int i = startIndex; i < startIndex + deleteRowCount; i++) {
			int repeatedAttr = firstRow.getRowsRepeatedNumber();
			if (repeatedAttr == 1) {
				TableTableRowElement rowEle = OdfElement.findNextChildNode(TableTableRowElement.class, firstRow
						.getOdfElement());
				firstRow.removeAllCellsRelationship();
				firstRow.getOdfElement().getParentNode().removeChild(firstRow.getOdfElement());
				updateRowRepository(firstRow.getOdfElement(), firstRow.mnRepeatedIndex, null, 0);
				if (i < (startIndex + deleteRowCount - 1)) {
					firstRow = this.getRowInstance(rowEle, 0);
				}
				deleted = true;
			} else {
				if (repeatedAttr > firstRow.mnRepeatedIndex) {
					firstRow.setRowsRepeatedNumber(repeatedAttr - 1);
					Row startRow = this.getRowInstance(firstRow.getOdfElement(), 0);
					updateRowRepository(firstRow.getOdfElement(), i - startRow.getRowIndex(), null, 0);
				}
			}
		}
		// 2. if mediumRow becomes as top row, revise style
		if (deleted && startIndex == 0 && getRowCount() > 0) {
			Row aRow = getRowByIndex(0);
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

		TableTableHeaderRowsElement headers = OdfElement.findFirstChildNode(TableTableHeaderRowsElement.class,
				mTableElement);
		return getHeaderRowCount(headers);
	}

	private int getHeaderColumnCount(TableTableHeaderColumnsElement headers) {
		int result = 0;
		if (headers != null) {
			for (Node n : new DomNodeList(headers.getChildNodes())) {
				result += ((TableTableColumnElement) n).getTableNumberColumnsRepeatedAttribute();
			}
		}
		return result;
	}

	private int getColumnsCount(TableTableColumnsElement columns) {
		int result = 0;
		if (columns != null) {
			for (Node n : new DomNodeList(columns.getChildNodes())) {
				result += ((TableTableColumnElement) n).getTableNumberColumnsRepeatedAttribute();
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
		TableTableHeaderColumnsElement headers = OdfElement.findFirstChildNode(TableTableHeaderColumnsElement.class,
				mTableElement);
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
	 * @param tableName
	 *            the table name
	 * @throws IllegalArgumentException
	 *             if the tableName is duplicate with one of tables in the
	 *             current document
	 */
	public void setTableName(String tableName) {
		// check if the table name is already exist
		List<Table> tableList = mDocument.getTableList();
		for (int i = 0; i < tableList.size(); i++) {
			Table table = tableList.get(i);
			if (tableName.equals(table.getTableName())) {
				if (table != this) {
					throw new IllegalArgumentException(
							"The table name is duplicate with one of tables in the current document.");
				}
			}
		}
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
	 * 
	 * @param isProtected
	 *            the protected attribute of the table to be set
	 */
	public void setProtected(boolean isProtected) {
		mTableElement.setTableProtectedAttribute(isProtected);
	}

	/**
	 * Return true if cell style is inherited when a new cell is added to the
	 * table.
	 * <p>
	 * The default setting is inherited. In this condition, the style of new
	 * column is same with the previous column before the inserted position,
	 * while the style of new row is same with the last row before the inserted
	 * position.
	 * <p>
	 * This feature setting will influence <code>appendRow()</code>,
	 * <code>appendColumn()</code>, <code>appendRows()</code>,
	 * <code>appendColumns()</code>, <code>insertRowsBefore()</code> and
	 * <code>insertColumnsBefore()</code>.
	 * <p>
	 * For <code>getCellByPosition()</code>,
	 * <code>getCellRangeByPosition()</code>, <code>getCellRangeByName()</code>,
	 * <code>getRowByIndex()</code> and <code>getColumnByIndex()</code>, if need
	 * automatically expand cells, it will return empty cell(s) without any
	 * style settings. So inheritance setting have no effect on them.
	 * 
	 * @return true if cell style is inherited when a new cell is added to the
	 *         table.
	 * 
	 * @see #setCellStyleInheritance(boolean)
	 * @see #appendColumn()
	 * @see #appendColumns(int)
	 * @see #appendRow()
	 * @see #appendRows(int)
	 * @see #insertColumnsBefore(int, int)
	 * @see #insertRowsBefore(int, int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 * @see #getCellRangeByPosition(int, int, int, int)
	 * @see #getCellRangeByPosition(String, String)
	 * @see #getCellRangeByName(String)
	 * @see #getColumnByIndex(int)
	 * @see #getRowByIndex(int)
	 * 
	 * @since 0.4.5
	 */
	public boolean isCellStyleInheritance() {
		return mIsCellStyleInheritance;
	}

	/**
	 * This method allows users to set whether cell style is inherited or not
	 * when a new cell is added to the table. Of course, the default setting is
	 * inherited. In this condition, the style of new column is same with the
	 * previous column before the inserted position, while the style of new row
	 * is same with the last row before the inserted position.
	 * <p>
	 * This feature setting will influence <code>appendRow()</code>,
	 * <code>appendColumn()</code>, <code>appendRows()</code>,
	 * <code>appendColumns()</code>, <code>insertRowsBefore()</code> and
	 * <code>insertColumnsBefore()</code>.
	 * <p>
	 * For <code>getCellByPosition()</code>,
	 * <code>getCellRangeByPosition()</code>, <code>getCellRangeByName()</code>,
	 * <code>getRowByIndex()</code> and <code>getColumnByIndex()</code>, if need
	 * automatically expand cells, it will return empty cell(s) without any
	 * style settings. So inheritance setting have no effect on them.
	 * 
	 * @param isEnabled
	 *            if<code>isEnabled</code> is true, cell style will be inherited
	 *            by new cell.
	 * 
	 * @see #isCellStyleInheritance()
	 * @see #appendColumn()
	 * @see #appendColumns(int)
	 * @see #appendRow()
	 * @see #appendRows(int)
	 * @see #insertColumnsBefore(int, int)
	 * @see #insertRowsBefore(int, int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 * @see #getCellRangeByPosition(int, int, int, int)
	 * @see #getCellRangeByPosition(String, String)
	 * @see #getCellRangeByName(String)
	 * @see #getColumnByIndex(int)
	 * @see #getRowByIndex(int)
	 * 
	 * @since 0.4.5
	 */
	public void setCellStyleInheritance(boolean isEnabled) {
		mIsCellStyleInheritance = isEnabled;
	}

	/**
	 * Return true if the new created multiple columns/rows/cells are described
	 * by a single element when it's possible.
	 * <p>
	 * The default setting is <code>true</code>, which helps to decrease the
	 * document size. If setting is <code>false</code>, each column/row/cell
	 * will be described by its owned single element.
	 * <p>
	 * This feature setting will influence <code>appendRows()</code>,
	 * <code>appendColumns()</code>, <code>insertRowsBefore()</code>,
	 * <code>insertColumnsBefore()</code>, <code>getCellByPosition()</code>,
	 * <code>getCellRangeByPosition()</code>, <code>getCellRangeByName()</code>,
	 * <code>getRowByIndex()</code> and <code>getColumnByIndex()</code>.
	 * 
	 * @return true if the new created columns/rows/cells are described by a
	 *         single element when it's possible.
	 * 
	 * @see #setUseRepeat(boolean)
	 * @see #appendColumns(int)
	 * @see #appendRows(int)
	 * @see #insertColumnsBefore(int, int)
	 * @see #insertRowsBefore(int, int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 * @see #getCellRangeByPosition(int, int, int, int)
	 * @see #getCellRangeByPosition(String, String)
	 * @see #getCellRangeByName(String)
	 * @see #getColumnByIndex(int)
	 * @see #getRowByIndex(int)
	 * 
	 * @since 0.4.5
	 */
	public boolean isUseRepeat() {
		return mIsDescribedBySingleElement;
	}

	/**
	 * When two or more columns/rows/cells are added to a table, if they are
	 * adjoining, and have the same content and style, and do not contain
	 * horizontally/vertically merged cells, they may be described by a single
	 * element. The repeated number attribute, for row is
	 * table:number-rows-repeated, while for column and cell are
	 * table:number-columns-repeated, specifies the number of columns/rows/cells
	 * to which a column/row/cell element applies.
	 * <p>
	 * This method allows users to set whether the new created
	 * columns/rows/cells are described by a single element. Of course, the
	 * default setting is <code>true</code>, which helps to decrease the
	 * document size. If setting is <code>false</code>, each column/row/cell
	 * will be described by its owned single element.
	 * <p>
	 * This feature setting will influence <code>appendRows()</code>,
	 * <code>appendColumns()</code>, <code>insertRowsBefore()</code>,
	 * <code>insertColumnsBefore()</code>, <code>getCellByPosition()</code>,
	 * <code>getCellRangeByPosition()</code>, <code>getCellRangeByName()</code>,
	 * <code>getRowByIndex()</code> and <code>getColumnByIndex()</code>.
	 * 
	 * @param isSingle
	 *            if<code>isSingle</code> is true, the new created
	 *            columns/rows/cells are described by a single element, if
	 *            possible.
	 * 
	 * @see #isUseRepeat()
	 * @see #appendColumns(int)
	 * @see #appendRows(int)
	 * @see #insertColumnsBefore(int, int)
	 * @see #insertRowsBefore(int, int)
	 * @see #getCellByPosition(int, int)
	 * @see #getCellByPosition(String)
	 * @see #getCellRangeByPosition(int, int, int, int)
	 * @see #getCellRangeByPosition(String, String)
	 * @see #getCellRangeByName(String)
	 * @see #getColumnByIndex(int)
	 * @see #getRowByIndex(int)
	 * 
	 * @since 0.4.5
	 */
	public void setUseRepeat(boolean isSingle) {
		mIsDescribedBySingleElement = isSingle;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////
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
	public CellRange getCellRangeByPosition(int startCol, int startRow, int endCol, int endRow) {
		// test whether cell position is out of table range and expand table
		// automatically.
		getCellByPosition(startCol, startRow);
		getCellByPosition(endCol, endRow);
		return new CellRange(this, startCol, startRow, endCol, endRow);
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
	public CellRange getCellRangeByPosition(String startAddress, String endAddress) {
		return getCellRangeByPosition(getColIndexFromCellAddress(startAddress),
				getRowIndexFromCellAddress(startAddress), getColIndexFromCellAddress(endAddress),
				getRowIndexFromCellAddress(endAddress));
	}

	/**
	 * Return a range of cells by a specified name.
	 * <p>
	 * After you get a cell range with <code>getCellRangeByPosition</code>, you
	 * can assign a name to this cell range with the method
	 * <code>setCellRangeName<code> in class <code>CellRange</code>. Then you
	 * will get a <b>named range</b> which can be represented by name. This
	 * method can be used to get a named range.
	 * 
	 * @param name
	 *            the name of the specified named range
	 * @return the specified cell range.
	 */
	public CellRange getCellRangeByName(String name) {
		NodeList nameRanges;
		try {
			nameRanges = mTableElement.getOwnerDocument().getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(),
					"named-range");
			for (int i = 0; i < nameRanges.getLength(); i++) {
				TableNamedRangeElement nameRange = (TableNamedRangeElement) nameRanges.item(i);
				if (nameRange.getTableNameAttribute().equals(name)) {
					String cellRange = nameRange.getTableCellRangeAddressAttribute();
					String[] addresses = cellRange.split(":");
					return getCellRangeByPosition(addresses[0], addresses[1]);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(Table.class.getName()).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Return a single cell that is positioned at the specified column and row.
	 * The table will be automatically expanded as need.
	 * 
	 * @param colIndex
	 *            the column index of the cell.
	 * @param rowIndex
	 *            the row index of the cell.
	 * @return the cell at the specified position
	 */
	public Cell getCellByPosition(int colIndex, int rowIndex) {
		if (colIndex < 0 || rowIndex < 0) {
			throw new IllegalArgumentException("colIndex and rowIndex should be nonnegative integer.");
		}
		// expand row as needed.
		int lastRowIndex = getRowCount() - 1;
		if (rowIndex > lastRowIndex) {
			// need clean cell style.
			appendRows((rowIndex - lastRowIndex), true);
		}
		// expand column as needed.
		int lastColumnIndex = getColumnCount() - 1;
		if (colIndex > lastColumnIndex) {
			// need clean cell style.
			appendColumns((colIndex - lastColumnIndex), true);
		}
		Row row = getRowByIndex(rowIndex);
		return row.getCellByIndex(colIndex);
	}

	// return array of string contain 3 member
	// 1. sheet table name
	// 2. alphabetic represent the column
	// 3. string represent the row number
	String[] splitCellAddress(String cellAddress) {
		String[] returnArray = new String[3];
		// seperate column and row from cell range
		StringTokenizer stDot = new StringTokenizer(cellAddress, ".");
		// get sheet table name and the cell address
		String cell = "";
		if (stDot.countTokens() >= 2) {
			StringTokenizer stDollar = new StringTokenizer(stDot.nextToken(), "$");
			returnArray[0] = stDollar.nextToken();
			cell = stDot.nextToken();
		} else {
			returnArray[0] = getTableName();
			cell = stDot.nextToken();
		}

		// get the column/row number from the cell address
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
	 * The cell address is constructed with a table name, a dot (.), an
	 * alphabetic value representing the column, and a numeric value
	 * representing the row. The table name can be omitted. For example:
	 * "$Sheet1.A1", "Sheet1.A1" and "A1" are all valid cell address.
	 * 
	 * @param address
	 *            the cell address of the cell.
	 * @return the cell at the specified position.
	 */
	public Cell getCellByPosition(String address) {
		return getCellByPosition(getColIndexFromCellAddress(address), getRowIndexFromCellAddress(address));
	}
	
	/**
	 * Modifies the margin above and below the table.
	 * 
	 * @param spaceTop
	 *            space above the table in centimeter(cm), ex. 1.25 cm
	 * @param spaceBottom
	 *            spacing below the table in centimeter(cm), ex. 0.7 cm
	 *            
	 * @since 0.5.5
	 */
	public void setVerticalMargin(double spaceTop, double spaceBottom) {
		String tableStyleName = mTableElement.getStyleName();
		OdfOfficeAutomaticStyles automaticStyles = mTableElement.getAutomaticStyles();
		OdfStyleBase tableStyle = automaticStyles.getStyle(tableStyleName, OdfStyleFamily.Table);
		if (tableStyle != null) {
			tableStyle.setProperty(StyleTablePropertiesElement.MarginTop,
					(new DecimalFormat("#0.##").format(spaceTop) + Unit.CENTIMETER.abbr()).replace(",", "."));
			tableStyle.setProperty(StyleTablePropertiesElement.MarginBottom, (new DecimalFormat("#0.##")
					.format(spaceBottom) + Unit.CENTIMETER.abbr()).replace(",", "."));
		}
	}

	// TODO: can put these two method to type.CellAddress
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

	// the parameter is the column/row index in the ownerTable,rather than in
	// the cell range
	// if the position is a covered cell, then get the owner cell for it

	Cell getOwnerCellByPosition(List<CellCoverInfo> coverList, int nCol, int nRow) {
		CellCoverInfo info;
		if (!isCoveredCellInOwnerTable(coverList, nCol, nRow)) {
			Cell cell = getCellByPosition(nCol, nRow);
			return cell;
		} else {
			for (int m = 0; m < coverList.size(); m++) {
				info = coverList.get(m);
				if (((nCol > info.nStartCol) && (nCol <= info.nEndCol) && (nRow == info.nStartRow) && (nRow == info.nEndRow))
						|| ((nCol == info.nStartCol) && (nCol == info.nEndCol) && (nRow > info.nStartRow) && (nRow <= info.nEndRow))
						|| ((nCol > info.nStartCol) && (nCol <= info.nEndCol) && (nRow > info.nStartRow) && (nRow <= info.nEndRow))) {
					Cell cell = getCellByPosition(info.nStartCol, info.nStartRow);
					return cell;
				}
			}
		}
		return null;
	}

	// the parameter is the column/row index in the ownerTable,rather than in
	// the cell range
	boolean isCoveredCellInOwnerTable(List<CellCoverInfo> coverList, int nCol, int nRow) {
		CellCoverInfo info;
		for (int m = 0; m < coverList.size(); m++) {
			info = coverList.get(m);
			if (((nCol > info.nStartCol) && (nCol <= info.nEndCol) && (nRow == info.nStartRow) && (nRow == info.nEndRow))
					|| ((nCol == info.nStartCol) && (nCol == info.nEndCol) && (nRow > info.nStartRow) && (nRow <= info.nEndRow))
					|| ((nCol > info.nStartCol) && (nCol <= info.nEndCol) && (nRow > info.nStartRow) && (nRow <= info.nEndRow))) // covered
			// cell
			{
				return true;
			}
		}
		return false;
	}

	List<CellCoverInfo> getCellCoverInfos(int nStartCol, int nStartRow, int nEndCol, int nEndRow) {
		List<CellCoverInfo> coverList = new ArrayList<CellCoverInfo>();
		int nColSpan, nRowSpan;
		for (int i = nStartCol; i < nEndCol + 1; i++) {
			for (int j = nStartRow; j < nEndRow + 1; j++) {
				Cell cell = getCellByPosition(i, j);
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

	// the odfelement of the FTableColumn changed, so we should update the
	// repository here
	void updateColumnRepository(TableTableColumnElement oldElement, int oldRepeatIndex,
			TableTableColumnElement newElement, int newRepeatIndex) {
		if (mColumnRepository.containsKey(oldElement)) {
			Vector<Column> oldList = mColumnRepository.get(oldElement);
			if (oldRepeatIndex < oldList.size()) {
				if (oldElement != newElement) {
					// the new column replace the old column
					Column oldColumn = oldList.get(oldRepeatIndex);
					if (oldColumn != null) {
						// update the mnRepeateIndex of the column which locate
						// after the removed column
						for (int i = oldRepeatIndex + 1; i < oldList.size(); i++) {
							Column column = oldList.get(i);
							if (column != null) {
								column.mnRepeatedIndex = i - 1;
							}
						}
						oldList.remove(oldColumn);
						// oldList.add(oldRepeatIndex, null);
						if (newElement != null) {
							oldColumn.maColumnElement = newElement;
							oldColumn.mnRepeatedIndex = newRepeatIndex;
							int size = (newRepeatIndex > 7) ? (newRepeatIndex + 1) : 8;
							Vector<Column> list = new Vector<Column>(size);
							list.setSize(newRepeatIndex + 1);
							list.set(newRepeatIndex, oldColumn);
							mColumnRepository.put(newElement, list);
						} else {
							oldColumn.maColumnElement = null;
						}
					}
				} else {
					// the new column element is equal to the old column
					// element, just change the repeatIndex
					Column oldColumn = oldList.get(oldRepeatIndex);
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

	// the odfelement of the FTableRow changed, so we should update the
	// repository here
	void updateRowRepository(TableTableRowElement oldElement, int oldRepeatIndex, TableTableRowElement newElement,
			int newRepeatIndex) {
		if (mRowRepository.containsKey(oldElement)) {
			Vector<Row> oldList = mRowRepository.get(oldElement);
			if (oldRepeatIndex < oldList.size()) {
				if (oldElement != newElement) {
					// the new row replace the old row
					Row oldRow = oldList.get(oldRepeatIndex);
					Vector<Cell> updateCellList = new Vector<Cell>();
					if (oldRow != null) {
						// update the mnRepeateIndex of the row which locate
						// after the removed row
						for (int i = oldRepeatIndex + 1; i < oldList.size(); i++) {
							Row row = oldList.get(i);
							if (row != null) {
								// update the cell in this row,
								int colNum = getColumnCount();
								for (int j = 0; j < colNum; j++) {
									Cell cell = row.getCellByIndex(j);
									updateCellList.add(cell);
								}
								row.mnRepeatedIndex = i - 1;
							}
						}
						oldList.remove(oldRow);
						if (newElement != null) {
							// update the cell in this row
							int colNum = getColumnCount();
							Cell[] oldCells = new Cell[colNum];
							for (int j = 0; j < colNum; j++) {
								oldCells[j] = oldRow.getCellByIndex(j);
							}
							// /
							oldRow.maRowElement = newElement;
							oldRow.mnRepeatedIndex = newRepeatIndex;
							int size = (newRepeatIndex > 7) ? (newRepeatIndex + 1) : 8;
							Vector<Row> list = new Vector<Row>(size);
							list.setSize(newRepeatIndex + 1);
							list.set(newRepeatIndex, oldRow);
							mRowRepository.put(newElement, list);
							// update the cell in this row
							Cell[] newCells = new Cell[colNum];
							for (int j = 0; j < colNum; j++) {
								newCells[j] = oldRow.getCellByIndex(j);
							}
							for (int j = 0; j < colNum; j++) {
								this.updateCellRepository(oldCells[j].getOdfElement(), oldCells[j].mnRepeatedColIndex,
										oldCells[j].mnRepeatedRowIndex, newCells[j].getOdfElement(),
										newCells[j].mnRepeatedColIndex, newCells[j].mnRepeatedRowIndex);
							}

							// update the mnRepeatedRowIndex of the cell which
							// locate after the removed row
							for (int j = 0; j < updateCellList.size(); j++) {
								Cell cell = updateCellList.get(j);
								if (cell.mnRepeatedRowIndex > oldRepeatIndex) {
									cell.mnRepeatedRowIndex--;
								}
							}
						} else {
							oldRow.maRowElement = null;
						}
					}
				} else {
					// the new row element is equal to the old row element, just
					// change the repeatIndex
					Row oldRow = oldList.get(oldRepeatIndex);
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

	// the odfelement of the FTableCell changed, so we should update the
	// repository here
	void updateCellRepository(TableTableCellElementBase oldElement, int oldRepeatColIndex, int oldRepeatRowIndex,
			TableTableCellElementBase newElement, int newRepeatColIndex, int newRepeatRowIndex) {
		if (mCellRepository.containsKey(oldElement)) {
			Cell oldCell = null;
			Vector<Cell> oldList = mCellRepository.get(oldElement);
			for (int i = 0; i < oldList.size(); i++) {
				if (oldList.get(i).getOdfElement() == oldElement
						&& oldList.get(i).mnRepeatedColIndex == oldRepeatColIndex
						&& oldList.get(i).mnRepeatedRowIndex == oldRepeatRowIndex) {
					oldCell = oldList.get(i);
					break;
				}
			}
			if (oldElement != newElement) {
				// the new cell replace the old cell
				if (oldCell != null) {
					// update the mnRepeateRowIndex & mnRepeateColIndex of the
					// cell which locate after the removed cell
					for (int i = 0; i < oldList.size(); i++) {
						Cell cell = oldList.get(i);
						if (cell != null && (cell.getOdfElement() == oldElement)) {
							if ((cell.mnRepeatedRowIndex == oldRepeatRowIndex)
									&& (cell.mnRepeatedColIndex > oldRepeatColIndex)) {
								cell.mnRepeatedColIndex--;
							}
						}
					}
					oldList.remove(oldCell);
					if (oldList.size() == 0) {
						mCellRepository.remove(oldElement);
					}
					if (newElement != null) {
						oldCell.mCellElement = newElement;
						oldCell.mnRepeatedColIndex = newRepeatColIndex;
						oldCell.mnRepeatedRowIndex = newRepeatRowIndex;
						Vector<Cell> list;
						if (mCellRepository.containsKey(newElement)) {
							list = mCellRepository.get(newElement);
							boolean bReplaced = false;
							for (int i = 0; i < list.size(); i++) {
								Cell cell = list.get(i);
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
							}
						} else {
							list = new Vector<Cell>();
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
				// the new cell element is equal to the old cell element, just
				// change the repeatIndex
				if (oldCell != null) {
					oldCell.mnRepeatedColIndex = newRepeatColIndex;
					oldCell.mnRepeatedRowIndex = newRepeatRowIndex;
				} else {
					getCellInstance(newElement, newRepeatColIndex, newRepeatRowIndex);
				}
			}
		}
	}

	void updateRepositoryWhenCellElementChanged(int startRow, int endRow, int startClm, int endClm,
			TableTableCellElement newCellEle) {
		for (int i = startRow; i < endRow; i++) {
			for (int j = startClm; j < endClm; j++) {
				Cell cell = getCellByPosition(j, i);
				updateCellRepository(cell.getOdfElement(), cell.mnRepeatedColIndex, cell.mnRepeatedRowIndex,
						newCellEle, cell.mnRepeatedColIndex, cell.mnRepeatedRowIndex);
			}
		}
	}
	
	public DefaultStyleHandler getStyleHandler() {
		if (mStyleHandler == null)
			mStyleHandler = new DefaultStyleHandler(this.getOdfElement());
		return mStyleHandler;
	}
	// default iterator to iterate column item.
	private class SimpleColumnIterator implements Iterator<Column> {
		private Table ownerTable;
		private TableTableColumnElement nextColumnElement;
		private TableTableColumnElement tempColumnElement;
		private int columnsRepeatedIndex = -1;
		private int columnsRepeatedNumber = 0;

		private SimpleColumnIterator(Table owner) {
			ownerTable = owner;
		}

		public boolean hasNext() {
			tempColumnElement = findNext(nextColumnElement);
			return (tempColumnElement != null);
		}

		public Column next() {
			if (tempColumnElement != null) {
				nextColumnElement = tempColumnElement;
				tempColumnElement = null;
			} else {
				nextColumnElement = findNext(nextColumnElement);
			}
			if (nextColumnElement == null) {
				return null;
			} else {
				return getColumnInstance(nextColumnElement, columnsRepeatedIndex);
			}
		}

		public void remove() {
			if (nextColumnElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			Column column = getColumnInstance(nextColumnElement, columnsRepeatedIndex);
			ownerTable.removeColumnsByIndex(column.getColumnIndex(), 1);
		}

		private TableTableColumnElement findNext(TableTableColumnElement nextColumnElement) {
			tempColumnElement = null;
			columnsRepeatedIndex++;
			if ((columnsRepeatedNumber > 0) && (columnsRepeatedIndex < columnsRepeatedNumber)) {
				tempColumnElement = nextColumnElement;
			} else {
				Node child = null;
				if (nextColumnElement == null) {
					child = ownerTable.getOdfElement().getFirstChild();
					while (child != null) {
						if (child instanceof TableTableHeaderColumnsElement) {
							TableTableHeaderColumnsElement headers = (TableTableHeaderColumnsElement) child;
							Node header = headers.getFirstChild();
							while (header != null) {
								if (header instanceof TableTableColumnElement) {
									tempColumnElement = (TableTableColumnElement) header;
									break;
								}
								header = header.getNextSibling();
							}
							if (tempColumnElement != null) {
								break;
							}
						}
						if (child instanceof TableTableColumnElement) {
							tempColumnElement = (TableTableColumnElement) child;
							break;
						}
						child = child.getNextSibling();
					}
				} else {
					child = nextColumnElement.getNextSibling();
					if (child != null) {
						if (child instanceof TableTableColumnElement) {
							tempColumnElement = (TableTableColumnElement) child;
						}
					} else {
						Node parentNode = nextColumnElement.getParentNode();
						if (parentNode instanceof TableTableHeaderColumnsElement) {
							parentNode = parentNode.getNextSibling();
							if ((parentNode != null) && (parentNode instanceof TableTableColumnElement)) {
								child = parentNode;
								tempColumnElement = (TableTableColumnElement) child;
							}
						}
					}
				}
				if (tempColumnElement != null) {
					columnsRepeatedNumber = tempColumnElement.getTableNumberColumnsRepeatedAttribute();
					columnsRepeatedIndex = 0;
				}
			}
			return tempColumnElement;
		}
	}
	
	// default iterator to iterate row item.
	private class SimpleRowIterator implements Iterator<Row> {
		
		private Table ownerTable;
		private TableTableRowElement nextRowElement;
		private TableTableRowElement tempRowElement;
		private int rowsRepeatedIndex = -1;
		private int rowsRepeatedNumber = 0;

		private SimpleRowIterator(Table owner) {
			ownerTable = owner;
		}

		public boolean hasNext() {
			tempRowElement = findNext(nextRowElement);
			return (tempRowElement != null);
		}

		public Row next() {
			if (tempRowElement != null) {
				nextRowElement = tempRowElement;
				tempRowElement = null;
			} else {
				nextRowElement = findNext(nextRowElement);
			}
			if (nextRowElement == null) {
				return null;
			} else {
				return getRowInstance(nextRowElement, rowsRepeatedIndex);
			}
		}

		public void remove() {
			if (nextRowElement == null) {
				throw new IllegalStateException("please call next() first.");
			}
			Row row = getRowInstance(nextRowElement, rowsRepeatedIndex);
			ownerTable.removeRowsByIndex(row.getRowIndex(), 1);
		}

		private TableTableRowElement findNext(TableTableRowElement nextRowElement) {
			tempRowElement = null;
			rowsRepeatedIndex++;
			if ((rowsRepeatedNumber > 0) && (rowsRepeatedIndex < rowsRepeatedNumber)) {
				tempRowElement = nextRowElement;
			} else {
				Node child = null;
				if (nextRowElement == null) {
					child = ownerTable.getOdfElement().getFirstChild();
					while (child != null) {
						if (child instanceof TableTableHeaderRowsElement) {
							TableTableHeaderRowsElement headers = (TableTableHeaderRowsElement) child;
							Node header = headers.getFirstChild();
							while (header != null) {
								if (header instanceof TableTableRowElement) {
									tempRowElement = (TableTableRowElement) header;
									break;
								}
								header = header.getNextSibling();
							}
							if (tempRowElement != null) {
								break;
							}
						}
						if (child instanceof TableTableRowElement) {
							tempRowElement = (TableTableRowElement) child;
							break;
						}
						child = child.getNextSibling();
					}
				} else {
					child = nextRowElement.getNextSibling();
					if (child != null) {
						if (child instanceof TableTableRowElement) {
							tempRowElement = (TableTableRowElement) child;
						}
					} else {
						Node parentNode = nextRowElement.getParentNode();
						if (parentNode instanceof TableTableHeaderRowsElement) {
							parentNode = parentNode.getNextSibling();
							if ((parentNode != null) && (parentNode instanceof TableTableRowElement)) {
								child = parentNode;
								tempRowElement = (TableTableRowElement) child;
							}
						}
					}
				}
				if (tempRowElement != null) {
					rowsRepeatedNumber = tempRowElement.getTableNumberRowsRepeatedAttribute();
					rowsRepeatedIndex = 0;
				}
			}
			return tempRowElement;
		}
	}
}

/**
 * Record the Cell Cover Info in this cell range.
 * <p>
 * Sometimes the covered cell is not tagged as
 * <table:covered-table-cell>
 * element.
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
