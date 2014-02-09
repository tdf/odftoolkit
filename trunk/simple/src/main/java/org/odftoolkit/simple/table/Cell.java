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

import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JTextField;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.fo.FoTextAlignAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.dc.DcCreatorElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDateElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencySymbolElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationsElement;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableHelpMessageElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberCurrencyStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextSpan;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.OdfXMLFactory;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.common.TextExtractor;
import org.odftoolkit.simple.common.WhitespaceProcessor;
import org.odftoolkit.simple.draw.FrameContainer;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.Image;
import org.odftoolkit.simple.style.Border;
import org.odftoolkit.simple.style.Font;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.odftoolkit.simple.style.StyleTypeDefinitions.CellBordersType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.FontStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.HorizontalAlignmentType;
import org.odftoolkit.simple.style.StyleTypeDefinitions.VerticalAlignmentType;
import org.odftoolkit.simple.text.AbstractParagraphContainer;
import org.odftoolkit.simple.text.Paragraph;
import org.odftoolkit.simple.text.ParagraphContainer;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Cell represents table cell feature in ODF document.
 * <p>
 * Table provides methods to get/set/modify the cell content and cell
 * properties.
 */
public class Cell extends Component implements ListContainer, ParagraphContainer, FrameContainer {

	TableTableCellElementBase mCellElement;
	Document mDocument;

	int mnRepeatedColIndex;
	int mnRepeatedRowIndex;
	Table mOwnerTable;
	String msFormatString;
	CellStyleHandler mStyleHandler;

	/**
	 * The default date format of table cell.
	 */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * The default time format of table cell.
	 */
	private static final String DEFAULT_TIME_FORMAT = "'PT'HH'H'mm'M'ss'S'";
	// example format: 2002-05-30T09:30:10
	private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	/**
	 * The default cell back color of table cell.
	 */
	private static final String DEFAULT_BACKGROUND_COLOR = "#FFFFFF";
	/**
	 * The default column spanned number.
	 */
	private static final int DEFAULT_COLUMN_SPANNED_NUMBER = 1;
	/**
	 * The default row spanned number.
	 */
	private static final int DEFAULT_ROW_SPANNED_NUMBER = 1;
	/**
	 * The default columns repeated number.
	 */
	private static final int DEFAULT_COLUMNS_REPEATED_NUMBER = 1;
	
	private ParagraphContainerImpl paragraphContainerImpl;
	private ListContainerImpl listContainerImpl;

	Cell(TableTableCellElementBase odfElement, int repeatedColIndex, int repeatedRowIndex) {
		mCellElement = odfElement;
		mnRepeatedColIndex = repeatedColIndex;
		mnRepeatedRowIndex = repeatedRowIndex;
		mOwnerTable = getTable();
		mDocument = ((Document) ((OdfFileDom) mCellElement.getOwnerDocument()).getDocument());
		mStyleHandler = new CellStyleHandler(this);
	}

	/**
	 * Get the <code>Cell</code> instance from the
	 * <code>TableTableCellElementBase</code> instance.
	 * <p>
	 * Each <code>TableTableCellElementBase</code> instance has a one-to-one
	 * relationship to the a <code>Cell</code> instance.
	 * 
	 * @param cellElement
	 *            the cell element that need to get the corresponding
	 *            <code>Cell</code> instance
	 * @return the <code>Cell</code> instance that represents a specified cell
	 *         element
	 */
	public static Cell getInstance(TableTableCellElementBase cellElement) {
		TableTableElement tableElement = null;
		Node node = cellElement.getParentNode();
		while (node != null) {
			if (node instanceof TableTableElement) {
				tableElement = (TableTableElement) node;
			}
			node = node.getParentNode();
		}
		Table table = null;
		if (tableElement != null) {
			table = Table.getInstance(tableElement);
		} else {
			throw new IllegalArgumentException("the cellElement is not in the table dom tree");
		}

		Cell cell = table.getCellInstance(cellElement, 0, 0);
		int colRepeatedNum = cell.getColumnsRepeatedNumber();
		int rowRepeatedNum = cell.getTableRow().getRowsRepeatedNumber();
		if (colRepeatedNum > 1 && rowRepeatedNum > 1) {
			if (colRepeatedNum > 1) {
				Logger
						.getLogger(Cell.class.getName())
						.log(Level.WARNING,
								"the cell has the repeated column number, and puzzled about get which repeated column index of the cell,");
			}
			if (rowRepeatedNum > 1) {
				Logger
						.getLogger(Cell.class.getName())
						.log(
								Level.WARNING,
								"the row contains the current cell has the repeated row number, and puzzled about get which repeated row index of the cell,");
			}
			Logger
					.getLogger(Cell.class.getName())
					.log(Level.WARNING,
							"here just return the first cell that the repeated column index is 0 and repeated row index is 0, too.");
		}
		return cell;
	}

	/**
	 * Return the horizontal alignment setting of this cell.
	 * <p>
	 * The returned value can be "center", "end", "justify", "left", "right", or
	 * "start". If no horizontal alignment is set, null will be returned.
	 * 
	 * @return the horizontal alignment setting.
	 * 
	 * @see #getHorizontalAlignmentType()
	 * @deprecated As of Simple version 0.3, replaced by
	 *             <code>getHorizontalAlignmentType()</code>
	 */
	@Deprecated
	public String getHorizontalAlignment() {
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForRead();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.ParagraphProperties, OdfName
					.newName(OdfDocumentNamespace.FO, "text-align"));
			return styleElement.getProperty(property);
		}
		return null;
	}

	/**
	 * Return the horizontal alignment setting of this cell.
	 * <p>
	 * Null will returned if there is no explicit style definition for this
	 * cell.
	 * <p>
	 * Default value will be returned if explicit style definition is found but
	 * no horizontal alignment is set.
	 * 
	 * @return the horizontal alignment setting.
	 */
	public HorizontalAlignmentType getHorizontalAlignmentType() {
		return getStyleHandler().getHorizontalAlignment();
	}

	/**
	 * Set the horizontal alignment setting of this cell.
	 * <p>
	 * The parameter can be "center", "end", "justify", "left", "right", or
	 * "start". Actually, "left" will be interpreted as "start", while "right"
	 * will be interpreted as "end". If argument is null, the explicit
	 * horizontal alignment setting is removed.
	 * 
	 * @param horizontalAlignment
	 *            the horizontal alignment setting.
	 * @see #setHorizontalAlignment(StyleTypeDefinitions.HorizontalAlignmentType)
	 * @deprecated As of Simple version 0.3, replaced by
	 *             <code>setHorizontalAlignment(SimpleHorizontalAlignmentType)</code>
	 */
	@Deprecated
	public void setHorizontalAlignment(String horizontalAlignment) {
		if (FoTextAlignAttribute.Value.LEFT.toString().equalsIgnoreCase(horizontalAlignment)) {
			horizontalAlignment = FoTextAlignAttribute.Value.START.toString();
		}
		if (FoTextAlignAttribute.Value.RIGHT.toString().equalsIgnoreCase(horizontalAlignment)) {
			horizontalAlignment = FoTextAlignAttribute.Value.END.toString();
		}
		splitRepeatedCells();
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.ParagraphProperties, OdfName
					.newName(OdfDocumentNamespace.FO, "text-align"));
			if (horizontalAlignment != null) {
				styleElement.setProperty(property, horizontalAlignment);
			} else {
				styleElement.removeProperty(property);
			}
		}
	}

	/**
	 * Set the horizontal alignment setting of this cell. If the alignment is
	 * set as Default, the explicit horizontal alignment setting is removed.
	 * 
	 * @param alignType
	 *            the horizontal alignment setting.
	 */
	public void setHorizontalAlignment(HorizontalAlignmentType alignType) {
		getStyleHandler().setHorizontalAlignment(alignType);
	}

	/**
	 * Return the vertical alignment setting of this cell.
	 * <p>
	 * The returned value can be "auto", "automatic", "baseline", "bottom",
	 * "middle", or "top".
	 * 
	 * @return the vertical alignment setting of this cell.
	 * 
	 * @see #getVerticalAlignmentType()
	 * @deprecated As of Simple version 0.3, replaced by
	 *             <code>getVerticalAlignmentType()</code>
	 */
	@Deprecated
	public String getVerticalAlignment() {
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForRead();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties, OdfName
					.newName(OdfDocumentNamespace.STYLE, "vertical-align"));
			return styleElement.getProperty(property);
		}
		return null;
	}

	/**
	 * Return the vertical alignment setting of this cell.
	 * <p>
	 * Null will returned if there is no explicit style definition for this
	 * cell.
	 * <p>
	 * Default value will be returned if explicit style definition is found but
	 * no vertical alignment is set.
	 * 
	 * @return the vertical alignment setting.
	 */
	public VerticalAlignmentType getVerticalAlignmentType() {
		return getStyleHandler().getVerticalAlignment();
	}

	/**
	 * Set the vertical alignment setting of this cell.
	 * <p>
	 * The parameter can be "auto", "automatic", "baseline", "bottom", "middle",
	 * or "top". If argument is null, the explicit vertical alignment setting is
	 * removed.
	 * 
	 * @param verticalAlignment
	 *            the vertical alignment setting.
	 * @see #setVerticalAlignment(StyleTypeDefinitions.VerticalAlignmentType)
	 * @deprecated As of Simple version 0.3, replaced by
	 *             <code>setVerticalAlignment(SimpleVerticalAlignmentType)</code>
	 */
	@Deprecated
	public void setVerticalAlignment(String verticalAlignment) {
		splitRepeatedCells();
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties, OdfName
					.newName(OdfDocumentNamespace.STYLE, "vertical-align"));
			if (verticalAlignment != null) {
				styleElement.setProperty(property, verticalAlignment);
			} else {
				styleElement.removeProperty(property);
			}
		}
	}

	/**
	 * Set the vertical alignment setting of this cell.
	 * <p>
	 * If the alignment is set as Default or null, the explicit vertical
	 * alignment setting is removed.
	 * 
	 * @param verticalAlignment
	 *            the vertical alignment setting.
	 */
	public void setVerticalAlignment(VerticalAlignmentType verticalAlignment) {
		getStyleHandler().setVerticalAlignment(verticalAlignment);
	}

	/**
	 * Return the wrap option of this cell.
	 * 
	 * @return true if the cell content can be wrapped;
	 *         <p>
	 *         false if the cell content cannot be wrapped.
	 */
	public boolean isTextWrapped() {
		return getStyleHandler().isTextWrapped();
	}

	/**
	 * Set the wrap option of this cell.
	 * 
	 * @param isTextWrapped
	 *            whether the cell content can be wrapped or not
	 */
	public void setTextWrapped(boolean isTextWrapped) {
		getStyleHandler().setTextWrapped(isTextWrapped);
	}

	private TableTableRowElement findRowInTableHeaderRows(TableTableHeaderRowsElement headers, TableTableRowElement tr,
			int[] indexs) {
		int result = 0;
		for (Node m : new DomNodeList(headers.getChildNodes())) {
			if (m == tr) {
				indexs[0] = result;
				return tr;
			}
			if (m instanceof TableTableRowElement) {
				result += ((TableTableRowElement) m).getTableNumberRowsRepeatedAttribute().intValue();
			}
		}
		indexs[0] = result;
		return null;
	}

	private TableTableRowElement findRowInTableRows(TableTableRowsElement rows, TableTableRowElement tr, int[] indexs) {
		int result = 0;
		for (Node m : new DomNodeList(rows.getChildNodes())) {
			if (m == tr) {
				indexs[0] = result;
				return tr;
			}
			if (m instanceof TableTableRowElement) {
				result += ((TableTableRowElement) m).getTableNumberRowsRepeatedAttribute().intValue();
			}
		}
		indexs[0] = result;
		return null;
	}

	private TableTableRowElement findRowInTableRowGroup(OdfElement group, TableTableRowElement tr, int[] indexs) {
		int result = 0;
		int[] resultIndexs = new int[1];

		if (!(group instanceof TableTableRowGroupElement) && !(group instanceof TableTableElement)) {
			indexs[0] = 0;
			return null;
		}

		for (Node m : new DomNodeList(group.getChildNodes())) {
			if (m instanceof TableTableHeaderRowsElement) {
				TableTableHeaderRowsElement headers = (TableTableHeaderRowsElement) m;
				TableTableRowElement returnEle = findRowInTableHeaderRows(headers, tr, resultIndexs);
				result += resultIndexs[0];
				if (returnEle != null) {// find
					indexs[0] = result;
					return returnEle;
				}
			} else if (m instanceof TableTableRowGroupElement) {
				TableTableRowGroupElement aGroup = (TableTableRowGroupElement) m;
				TableTableRowElement returnEle = findRowInTableRowGroup(aGroup, tr, resultIndexs);
				result += resultIndexs[0];
				if (returnEle != null) {// find
					indexs[0] = result;
					return returnEle;
				}
			} else if (m instanceof TableTableRowsElement) {
				TableTableRowsElement rows = (TableTableRowsElement) m;
				TableTableRowElement returnEle = findRowInTableRows(rows, tr, resultIndexs);
				result += resultIndexs[0];
				if (returnEle != null) {// find
					indexs[0] = result;
					return returnEle;
				}
			} else if (m instanceof TableTableRowElement) {
				if (m == tr) { // find
					indexs[0] = result;
					return tr;
				}
				result += ((TableTableRowElement) m).getTableNumberRowsRepeatedAttribute().intValue();
			}
		}
		indexs[0] = result;
		return null;
	}

	/**
	 * Get the index of the table row which contains this cell.
	 * 
	 * @return the index of the row containing this cell
	 */
	public int getRowIndex() {
		TableTableElement table = getTableElement();
		TableTableRowElement tr = getTableRowElement();
		int[] indexs = new int[1];

		TableTableRowElement returnEle = findRowInTableRowGroup(table, tr, indexs);
		if (returnEle != null) {
			return (indexs[0] + mnRepeatedRowIndex);
		} else {
			return -1;
		}
	}

	/**
	 * Get an instance of table feature which contains this cell.
	 * 
	 * @return the table containing this cell
	 */
	public Table getTable() {
		TableTableElement tableElement = getTableElement();
		if (tableElement != null) {
			return Table.getInstance(tableElement);
		}
		return null;
	}

	/**
	 * Get the index of the table column which contains this cell.
	 * 
	 * @return the index of the column containing this cell
	 */
	public int getColumnIndex() {
		TableTableRowElement tr = (TableTableRowElement) mCellElement.getParentNode();
		int result = 0;
		for (Node n : new DomNodeList(tr.getChildNodes())) {
			if (n == mCellElement) {
				return result + mnRepeatedColIndex;
			}
			if (n instanceof TableTableCellElementBase) {
				result += ((TableTableCellElementBase) n).getTableNumberColumnsRepeatedAttribute().intValue();
			}
		}
		return result;
	}

	/**
	 * Get the instance of table column feature which contains this cell.
	 * 
	 * @return the instance of table column feature which contains the cell.
	 */
	public Column getTableColumn() {
		Table table = getTable();
		int index = getColumnIndex();
		return table.getColumnByIndex(index);
	}

	TableTableColumnElement getTableColumnElement() {
		// return OdfTableCellBaseImpl.getTableColumn((OdfTableCellBase)
		// mCellElement);
		TableTableElement tableElement = getTableElement();
		int columnindex = getColumnIndex();
		Table fTable = Table.getInstance(tableElement);
		return fTable.getColumnElementByIndex(columnindex);
	}

	/**
	 * Get the instance of table row feature which contains this cell.
	 * 
	 * @return the instance of table row feature which contains the cell.
	 */
	public Row getTableRow() {
		Table table = getTable();
		return table.getRowInstance(getTableRowElement(), mnRepeatedRowIndex);
	}

	private TableTableRowElement getTableRowElement() {
		Node node = mCellElement.getParentNode();
		if (node instanceof TableTableRowElement) {
			return (TableTableRowElement) node;
		}
		return null;
	}

	/**
	 * Get the table object who contains this cell.
	 * 
	 * @return the table object who contains the cell.
	 */
	private TableTableElement getTableElement() {
		Node node = mCellElement.getParentNode();
		while (node != null) {
			if (node instanceof TableTableElement) {
				return (TableTableElement) node;
			}
			node = node.getParentNode();
		}
		return null;
	}

	/**
	 * Get the cell that covers this cell.
	 * <p>
	 * If the cell is a covered cell, the owner cell will be returned; if the
	 * cell is a real cell , the cell itself will be returned.
	 * 
	 * @return the cell that covers the current cell
	 */
	public Cell getOwnerTableCell() {
		Table ownerTable = getTable();
		List<CellCoverInfo> coverList = ownerTable.getCellCoverInfos(0, 0, ownerTable.getColumnCount() - 1, ownerTable
				.getRowCount() - 1);
		return ownerTable.getOwnerCellByPosition(coverList, getColumnIndex(), getRowIndex());
	}

	/**
	 * Get the instance of <code>TableTableCellElementBase</code> which
	 * represents this cell.
	 * 
	 * @return the instance of <code>TableTableCellElementBase</code>
	 */
	public TableTableCellElementBase getOdfElement() {
		return mCellElement;
	}

	/**
	 * Return the currency code of this cell, for example, "USD", "EUR", "CNY",
	 * and etc.
	 * <p>
	 * If the value type is not "currency", an IllegalArgumentException will be
	 * thrown.
	 * 
	 * @return the currency code
	 *         <p>
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown if the value type
	 *             is not "currency".
	 */
	public String getCurrencyCode() {
		if (mCellElement.getOfficeValueTypeAttribute().equals(OfficeValueTypeAttribute.Value.CURRENCY.toString())) {
			return mCellElement.getOfficeCurrencyAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the currency code of this cell, for example, "USD", "EUR", "CNY", and
	 * etc.
	 * 
	 * @param currency
	 *            the currency code that need to be set.
	 * @throws IllegalArgumentException
	 *             If input <code>currency</code> is null, an
	 *             IllegalArgumentException will be thrown.
	 */
	public void setCurrencyCode(String currency) {
		if (currency == null) {
			throw new IllegalArgumentException("Currency code of cell should not be null.");
		}
		splitRepeatedCells();
		if (mCellElement.getOfficeValueTypeAttribute().equals(OfficeValueTypeAttribute.Value.CURRENCY.toString())) {
			mCellElement.setOfficeCurrencyAttribute(currency);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private void setTypeAttr(OfficeValueTypeAttribute.Value type) {
		mCellElement.setOfficeValueTypeAttribute(type.toString());
	}

	/**
	 * Set the value type of this cell. The parameter can be "boolean",
	 * "currency", "date", "float", "percentage", "string" or "time".
	 * <p>
	 * If the parameter <code>type</code> is not a valid cell type, an
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param type
	 *            the type that need to be set If input type is null, an
	 *            IllegalArgumentException will be thrown.
	 */
	public void setValueType(String type) {
		if (type == null) {
			throw new IllegalArgumentException("type shouldn't be null.");
		}
		String sType = type.toLowerCase();
		OfficeValueTypeAttribute.Value value = OfficeValueTypeAttribute.Value.enumValueOf(sType);
		if (value == null) {
			throw new IllegalArgumentException("the value type of cell is not valid");
		}

		mCellElement.setOfficeValueTypeAttribute(sType);
	}

	/**
	 * Get the value type of this cell. The returned value can be "boolean",
	 * "currency", "date", "float", "percentage", "string" or "time". If no
	 * value type is set, null will be returned.
	 * 
	 * @return the type of the cell
	 */
	public String getValueType() {
		return mCellElement.getOfficeValueTypeAttribute();
	}

	private OfficeValueTypeAttribute.Value getTypeAttr() {
		String type = mCellElement.getOfficeValueTypeAttribute();
		return OfficeValueTypeAttribute.Value.enumValueOf(type);
	}

	/**
	 * Get the double value of this cell as Double object.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not "float".
	 * 
	 * @return the double value of this cell as a Double object. If the cell
	 *         value is empty, null will be returned.
	 *         <p>
	 *         An IllegalArgumentException will be thrown if the cell type is
	 *         not "float".
	 */
	public Double getDoubleValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.FLOAT) {
			return mCellElement.getOfficeValueAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Get the currency value of this cell as Double object.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not "currency".
	 * 
	 * @return the currency value of this cell as a Double object. If the cell
	 *         value is empty, null will be returned.
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown if the cell type
	 *             is not "currency".
	 */
	public Double getCurrencyValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.CURRENCY) {
			return mCellElement.getOfficeValueAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Get the symbol of currency.
	 * 
	 * @return the currency symbol
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown if the value type
	 *             is not "currency".
	 */
	public String getCurrencySymbol() {
		if (getTypeAttr() != OfficeValueTypeAttribute.Value.CURRENCY) {
			throw new IllegalArgumentException();
		}

		OdfStyleBase style = getStyleHandler().getStyleElementForRead();
		if (style != null) {
			String dataStyleName = style.getOdfAttributeValue(OdfName.newName(OdfDocumentNamespace.STYLE,
					"data-style-name"));
			OdfNumberCurrencyStyle dataStyle = mCellElement.getAutomaticStyles().getCurrencyStyle(dataStyleName);
			if (dataStyle == null) {
				dataStyle = mDocument.getDocumentStyles().getCurrencyStyle(dataStyleName);
			}
			if ((dataStyle != null) && (dataStyle.getCurrencySymbolElement() != null)) {
				return dataStyle.getCurrencySymbolElement().getTextContent();
			}
		}
		return null;
	}

	/**
	 * Set the value and currency of the cell, and set the value type as
	 * "currency". If
	 * <code>value</value> is null, the cell value will be removed.
	 * 
	 * @param value
	 *            the value that will be set
	 * @param currency
	 *            the currency that will be set.
	 * @throws IllegalArgumentException
	 *             If input currency is null, an IllegalArgumentException will
	 *             be thrown.
	 */
	public void setCurrencyValue(Double value, String currency) {
		if (currency == null) {
			throw new IllegalArgumentException("currency shouldn't be null.");
		}
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.CURRENCY);
		mCellElement.setOfficeValueAttribute(value);
		mCellElement.setOfficeCurrencyAttribute(currency);
	}

	/**
	 * Get the cell percentage value as Double object.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not "percentage".
	 * 
	 * @return the percentage value of this cell as a Double object. If the cell
	 *         value is empty, null will be returned.
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown if the cell type
	 *             is not "percentage".
	 */
	public Double getPercentageValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			return mCellElement.getOfficeValueAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a percentage value and set the value type as
	 * percentage too. If
	 * <code>value</value> is null, the cell value will be removed.
	 * 
	 * @param value
	 *            the value that will be set
	 */
	public void setPercentageValue(Double value) {
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.PERCENTAGE);
		mCellElement.setOfficeValueAttribute(value);
	}

	/**
	 * Get the text displayed in this cell.
	 * 
	 * @return the text displayed in this cell
	 */
	public String getDisplayText() {
		// TODO: This function doesn't work well if a cell contains a list.
		// Refer to testGetSetTextValue();
		return TextExtractor.getText(mCellElement);
	}

	/**
	 * Set the text displayed in this cell. If content is null, it will display
	 * the empty string instead.
	 * <p>
	 * Please note the displayed text in ODF viewer might be different with the
	 * value set by this method, because the displayed text in ODF viewer is
	 * calculated and set by editor. So an adapter can be assigned to adapt cell
	 * value and value type.
	 * 
	 * @param content
	 *            the displayed text.
	 * @param adapter
	 *            the <code>CellValueAdapter</code> used to adapt cell value and
	 *            value type.
	 * 
	 * @see org.odftoolkit.simple.table.CellValueAdapter
	 * @since 0.3
	 */
	public void setDisplayText(String content, CellValueAdapter adapter) {
		if (content == null) {
			content = "";
		}
		setDisplayTextContent(content, null);
		// adapt value and value type by display text.
		adapter.adaptValue(this, content);
	}

	/**
	 * Set the text displayed in this cell. If content is null, it will display
	 * the empty string instead.
	 * <p>
	 * Please note the displayed text in ODF viewer might be different with the
	 * value set by this method, because the displayed text in ODF viewer is
	 * calculated and set by editor. The cell value and value type will be
	 * updated follow by the rules which are designed in the
	 * {@link org.odftoolkit.simple.table.DefaultCellValueAdapter
	 * <code>DefaultCellValueAdapter</code>}.
	 * 
	 * @param content
	 *            the displayed text.
	 * 
	 * @see org.odftoolkit.simple.table.CellValueAdapter
	 * @see org.odftoolkit.simple.table.DefaultCellValueAdapter
	 */
	public void setDisplayText(String content) {
		setDisplayText(content, CellValueAdapter.DEFAULT_VALUE_ADAPTER);
	}

	/**
	 * Set the text displayed in this cell, with a specified style name.
	 * <p>
	 * Please note the displayed text in ODF viewer might be different with the
	 * value set by this method, because the displayed text in ODF viewer is
	 * calculated and set by editor. So an adapter can be assigned to adapt cell
	 * value and value type.
	 * 
	 * @param content
	 *            the displayed text. If content is null, it will display the
	 *            empty string instead.
	 * @param adapter
	 *            the <code>CellValueAdapter</code> used to adapt cell value and
	 *            value type.
	 * @param stylename
	 *            the style name. If style name is null, the content will use
	 *            the default paragraph style.
	 * 
	 * @see org.odftoolkit.simple.table.CellValueAdapter
	 * @since 0.3
	 */
	public void setDisplayText(String content, CellValueAdapter adapter, String stylename) {
		if (content == null) {
			content = "";
		}
		setDisplayTextContent(content, stylename);
		// adapt value and value type by display text.
		adapter.adaptValue(this, content);
	}

	/**
	 * Set the text displayed in this cell, with a specified style name.
	 * <p>
	 * Please note the displayed text in ODF viewer might be different with the
	 * value set by this method, because the displayed text in ODF viewer is
	 * calculated and set by editor. The cell value and value type will be
	 * updated follow by the rules which are designed in the
	 * {@link org.odftoolkit.simple.table.DefaultCellValueAdapter
	 * <code>DefaultCellValueAdapter</code>}.
	 * 
	 * @param content
	 *            the displayed text. If content is null, it will display the
	 *            empty string instead.
	 * @param stylename
	 *            the style name. If style name is null, the content will use
	 *            the default paragraph style.
	 * 
	 * @see org.odftoolkit.simple.table.CellValueAdapter
	 * @see org.odftoolkit.simple.table.DefaultCellValueAdapter
	 */
	public void setDisplayText(String content, String stylename) {
		setDisplayText(content, CellValueAdapter.DEFAULT_VALUE_ADAPTER, stylename);
	}

	// Set the text content in this cell. If content is null, it will display
	// the empty string instead.
	private void setDisplayTextContent(String content, String stylename) {
		WhitespaceProcessor textProcessor = new WhitespaceProcessor();
		OdfStylableElement element = OdfElement.findFirstChildNode(OdfTextParagraph.class, mCellElement);
		if (element == null) {
			removeContent();
			element = new OdfTextParagraph((OdfFileDom) mCellElement.getOwnerDocument());
			mCellElement.appendChild(element);
		} else {
			String formerContent = element.getTextContent();
			while (formerContent == null || "".equals(formerContent)) {
				OdfTextSpan span = OdfElement.findFirstChildNode(OdfTextSpan.class, element);
				if (span == null) {
					break;
				}
				formerContent = span.getTextContent();
				element = span;
			}
		}
		if ((stylename != null) && (stylename.length() > 0)) {
			element.setStyleName(stylename);
		}
		element.setTextContent(null);
		textProcessor.append(element, content);
		optimizeCellSize(content);
	}

	/**
	 * Set the cell value as a double and set the value type to be "float".
	 * 
	 * @param value
	 *            the double value that will be set. If
	 *            <code>value</value> is null, the cell value will be removed.
	 */
	public void setDoubleValue(Double value) {
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.FLOAT);
		mCellElement.setOfficeValueAttribute(value);
		setDisplayTextContent(value + "", null);
	}

	/**
	 * Get the cell boolean value as Boolean object.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not "boolean".
	 * 
	 * @return the Boolean value of cell. If the cell value is empty, null will
	 *         be returned.
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown if the cell type
	 *             is not "boolean".
	 */
	public Boolean getBooleanValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.BOOLEAN) {
			return mCellElement.getOfficeBooleanValueAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a boolean and set the value type to be boolean. If
	 * <code>value</value> is null, the cell value will be removed.
	 * 
	 * @param value
	 *            the value of boolean type
	 */
	public void setBooleanValue(Boolean value) {
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.BOOLEAN);
		mCellElement.setOfficeBooleanValueAttribute(value);
		setDisplayTextContent(value + "", null);
	}

	/**
	 * Get the cell date value as Calendar.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not "date".
	 * 
	 * @return the Calendar value of cell
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown, if the cell type
	 *             is not "date".
	 */
	public Calendar getDateValue() {
		return getOfficeDateValue(DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Get the cell date time value (date and time) as Calendar.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not "date".
	 * 
	 * @return the Calendar value of cell
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown, if the cell type
	 *             is not "date".
	 */
	public Calendar getDateTimeValue() {
		return getOfficeDateValue(DEFAULT_DATE_TIME_FORMAT);
	}
	
	private Calendar getOfficeDateValue(String pattern) {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.DATE) {
			String dateStr = mCellElement.getOfficeDateValueAttribute();
			if (dateStr == null) {
				return null;
			}
			Date date = parseString(dateStr, pattern);
			Calendar calender = Calendar.getInstance();
			calender.setTime(date);
			return calender;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a date, and set the value type to be "date".
	 * 
	 * @param date
	 *            the value of {@link java.util.Calendar java.util.Calendar}
	 *            type.
	 */
	public void setDateValue(Calendar date) {
		setOfficeDateValue(date, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * Sets the cell value as a date with second precision and the value type to be "date".
	 * @param date 
	 */
	public void setDateTimeValue(Calendar date) {
		setOfficeDateValue(date, DEFAULT_DATE_TIME_FORMAT);
	}

	private void setOfficeDateValue(Calendar date, String pattern) {
		if (date == null) {
			throw new IllegalArgumentException("date shouldn't be null.");
		}
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.DATE);
		SimpleDateFormat simpleFormat = new SimpleDateFormat(pattern);
		String svalue = simpleFormat.format(date.getTime());
		mCellElement.setOfficeDateValueAttribute(svalue);
		setDisplayTextContent(svalue, null);
	}
	
	/**
	 * Set the cell style name. When lots of cells have the same style features,
	 * the user can configuration the first one and set the other's style name
	 * directly. That will improve the performance.
	 * 
	 * @param styleName
	 *            an exit cell style name.
	 * @since 0.4
	 */
	public void setCellStyleName(String styleName) {
		mCellElement.setStyleName(styleName);
	}

	/**
	 * Get the cell style name.
	 * 
	 * @return cell style name.
	 * @since 0.4
	 */
	public String getCellStyleName() {
		return mCellElement.getStyleName();
	}

	/**
	 * Set the cell value as a string, and set the value type to be string.
	 * 
	 * @param str
	 *            the value of string type. If input string is null, an empty
	 *            string will be set.
	 */
	public void setStringValue(String str) {
		if (str == null) {
			str = "";
		}
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.STRING);
		mCellElement.setOfficeStringValueAttribute(str);
		setDisplayTextContent(str, null);
	}

	// Note: if you want to change the cell
	// splitRepeatedCells must be called first in order to
	// 1. update parent row if the row is the repeated rows.
	// 2. update the cell itself if the cell is the column repeated cells.
	void splitRepeatedCells() {
		Table table = getTable();
		TableTableRowElement ownerRowElement = getTableRowElement();
		// 1.if the parent row is the repeated row
		// the repeated row has to be separated
		// after this the cell element and repeated index will be updated
		// according to the new parent row
		Row ownerRow = table.getRowInstance(ownerRowElement, mnRepeatedRowIndex);
		if (ownerRow.getRowsRepeatedNumber() > 1) {
			ownerRow.splitRepeatedRows();
			// update row element, new row element maybe created.
			ownerRowElement = ownerRow.maRowElement;
			mnRepeatedRowIndex = 0;
		}
		// 2.if the cell is the column repeated cell
		// this repeated cell has to be separated
		int repeateNum = getColumnsRepeatedNumber();
		if (repeateNum > 1) {
			// change this repeated cell to three parts: repeated cell before,
			// new single cell and repeated cell after.
			Map<TableTableCellElementBase, Vector<Cell>> cellRepository = table.mCellRepository;
			String tableNamespaceURI = OdfDocumentNamespace.TABLE.getUri();
			Vector<Cell> oldList = null;
			if (cellRepository.containsKey(mCellElement)) {
				oldList = cellRepository.remove(mCellElement);
			}
			int offetAfterCurrentCell = repeateNum - mnRepeatedColIndex - 1;
			TableTableCellElementBase currentCellElement = mCellElement;
			TableTableCellElementBase newBeforeCellElement = null;
			TableTableCellElementBase newAfterCellElement = null;
			if (mnRepeatedColIndex > 0) {
				newBeforeCellElement = (TableTableCellElementBase) mCellElement.cloneNode(true);
				if (mnRepeatedColIndex > 1) {
					newBeforeCellElement.setTableNumberColumnsRepeatedAttribute(mnRepeatedColIndex);
				} else {
					newBeforeCellElement.removeAttributeNS(tableNamespaceURI, "number-columns-repeated");
				}
				// insert new before repeated cell
				ownerRowElement.insertBefore(newBeforeCellElement, currentCellElement);
				// update cell cache
				if (oldList != null) {
					Vector<Cell> newBeforeList = new Vector<Cell>(mnRepeatedColIndex);
					for (int i = 0; i < mnRepeatedColIndex && i < oldList.size(); i++) {
						Cell beforeCell = oldList.get(i);
						if (beforeCell != null) {
							beforeCell.mCellElement = newBeforeCellElement;
							newBeforeList.add(i, beforeCell);
						}
					}
					cellRepository.put(newBeforeCellElement, newBeforeList);
				}
			}
			currentCellElement.removeAttributeNS(tableNamespaceURI, "number-columns-repeated");
			if (offetAfterCurrentCell > 0) {
				newAfterCellElement = (TableTableCellElementBase) currentCellElement.cloneNode(true);
				ownerRowElement.insertBefore(newAfterCellElement, currentCellElement);
				currentCellElement = newAfterCellElement;
				newAfterCellElement = (TableTableCellElementBase) currentCellElement.getNextSibling();
				if (offetAfterCurrentCell > 1) {
					newAfterCellElement.setTableNumberColumnsRepeatedAttribute(offetAfterCurrentCell);
				}
				// update cell cache
				if (oldList != null) {
					Vector<Cell> newAfterList = new Vector<Cell>(offetAfterCurrentCell);
					for (int i = mnRepeatedColIndex + 1; i < repeateNum && i < oldList.size(); i++) {
						Cell afterCell = oldList.get(i);
						if (afterCell != null) {
							afterCell.mCellElement = newAfterCellElement;
							afterCell.mnRepeatedColIndex = i - mnRepeatedColIndex - 1;
							newAfterList.add(afterCell.mnRepeatedColIndex, afterCell);
						}
					}
					cellRepository.put(newAfterCellElement, newAfterList);
				}
			}
			mnRepeatedColIndex = 0;
			mCellElement = currentCellElement;
			// update cell cache
			Vector<Cell> currentList = new Vector<Cell>(1);
			currentList.add(0, this);
			cellRepository.put(currentCellElement, currentList);
		}
	}

	/**
	 * Get the cell value as a string.
	 * <p>
	 * If the cell type is not string, the display text will be returned.
	 * 
	 * @return the string value of this cell, or the display text
	 */
	public String getStringValue() {
		return getDisplayText();
	}

	/**
	 * Get the cell value as {@link java.util.Calendar java.util.Calendar}.
	 * <p>
	 * Throw exception if the cell type is not "time".
	 * 
	 * @return the Calendar value of cell
	 * @throws IllegalArgumentException
	 *             an IllegalArgumentException will be thrown if the cell type
	 *             is not time.
	 */
	public Calendar getTimeValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.TIME) {
			String timeStr = mCellElement.getOfficeTimeValueAttribute();
                        if (timeStr == null) {
                            return null;
                        }
			Date date = parseString(timeStr, DEFAULT_TIME_FORMAT);
			Calendar calender = Calendar.getInstance();
			calender.setTime(date);
			calender.clear(Calendar.YEAR);
			calender.clear(Calendar.MONTH);
			calender.clear(Calendar.DAY_OF_MONTH);
			return calender;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a time and set the value type to be "time" too.
	 * 
	 * @param time
	 *            the value of {@link java.util.Calendar java.util.Calendar}
	 *            type.
	 * @throws IllegalArgumentException
	 *             If input time is null, an IllegalArgumentException exception
	 *             will be thrown.
	 */
	public void setTimeValue(Calendar time) {
		if (time == null) {
			throw new IllegalArgumentException("time shouldn't be null.");
		}
		splitRepeatedCells();
		setTypeAttr(OfficeValueTypeAttribute.Value.TIME);
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
		String svalue = simpleFormat.format(time.getTime());
		mCellElement.setOfficeTimeValueAttribute(svalue);
		setDisplayTextContent(svalue, null);
	}

	private Date parseString(String value, String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
		Date simpleDate = null;
		try {
			simpleDate = simpleFormat.parse(value);
		} catch (ParseException e) {
			Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
		return simpleDate;
	}

	/**
	 * Get the background color of this cell.
	 * <p>
	 * If no background color is set, default background color "#FFFFFF" will be
	 * returned.
	 * 
	 * @return the background color of this cell
	 */
	public Color getCellBackgroundColor() {
		return getStyleHandler().getBackgroundColor();
	}

	/**
	 * Get the background color string of this cell.
	 * <p>
	 * If no background color is set, default background color "#FFFFFF" will be
	 * returned.
	 * 
	 * @return the background color of this cell
	 * 
	 * @see #getCellBackgroundColor()
	 * @deprecated As of Simple version 0.3, replaced by
	 *             <code>getCellBackgroundColor()</code>
	 */
	@Deprecated
	public String getCellBackgroundColorString() {
		String color = DEFAULT_BACKGROUND_COLOR;
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForRead();
		if (styleElement != null) {
			OdfStyleProperty bkColorProperty = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties, OdfName
					.newName(OdfDocumentNamespace.FO, "background-color"));
			String property = styleElement.getProperty(bkColorProperty);
			if (Color.isValid(property)) {
				color = property;
			}
		}
		return color;
	}

	/**
	 * Set the background color of this cell.
	 * 
	 * @param cellBackgroundColor
	 *            the background color that need to set. If
	 *            <code>cellBackgroundColor</code> is null, default background
	 *            color <code>Color.WHITE</code> will be set.
	 */
	public void setCellBackgroundColor(Color cellBackgroundColor) {
		getStyleHandler().setBackgroundColor(cellBackgroundColor);
	}

	/**
	 * Set the background color of this cell using string. The string must be a
	 * valid argument for constructing {@link org.odftoolkit.odfdom.type.Color
	 * <code>org.odftoolkit.odfdom.type.Color</code>}.
	 * 
	 * @param cellBackgroundColor
	 *            the background color that need to set. If cellBackgroundColor
	 *            is null, default background color #FFFFFF will be set.
	 * @see org.odftoolkit.odfdom.type.Color
	 * @see #setCellBackgroundColor(Color)
	 * @deprecated As of Simple version 0.3, replaced by
	 *             <code>setCellBackgroundColor(Color)</code>
	 */
	@Deprecated
	public void setCellBackgroundColor(String cellBackgroundColor) {
		if (!Color.isValid(cellBackgroundColor)) {
			Logger.getLogger(Cell.class.getName()).log(Level.WARNING,
					"Parameter is invalid for datatype Color, default background color #FFFFFF will be set.");
			cellBackgroundColor = DEFAULT_BACKGROUND_COLOR;
		}
		splitRepeatedCells();
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty bkColorProperty = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties, OdfName
					.newName(OdfDocumentNamespace.FO, "background-color"));
			styleElement.setProperty(bkColorProperty, cellBackgroundColor);
		}
	}

	/**
	 * Get the column spanned number of this cell.
	 * 
	 * @return the column spanned number
	 */
	public int getColumnSpannedNumber() {
		if (mCellElement instanceof TableCoveredTableCellElement) {
			return 1;
		}
		Integer value = ((TableTableCellElement) mCellElement).getTableNumberColumnsSpannedAttribute();
		if (value != null) {
			return value.intValue();
		}
		return DEFAULT_COLUMN_SPANNED_NUMBER;
	}

	/**
	 * Get the column repeated number of this cell.
	 * 
	 * @return the column repeated number
	 */
	int getColumnsRepeatedNumber() {
		Integer value = mCellElement.getTableNumberColumnsRepeatedAttribute();
		if (value != null) {
			return value.intValue();
		}
		return DEFAULT_COLUMNS_REPEATED_NUMBER;
	}

	/**
	 * Get the row spanned number of this cell.
	 * 
	 * @return the row spanned number
	 */
	public int getRowSpannedNumber() {
		if (mCellElement instanceof TableCoveredTableCellElement) {
			return 1;
		}
		Integer value = ((TableTableCellElement) mCellElement).getTableNumberRowsSpannedAttribute();
		if (value != null) {
			return value.intValue();
		}
		return DEFAULT_ROW_SPANNED_NUMBER;
	}

	/**
	 * Set the column spanned number.
	 * 
	 * @param spannedNum
	 *            the column spanned number to be set. If spannedNum is less
	 *            than 1, default column spanned number 1 will be set.
	 */
	void setColumnSpannedNumber(int spannedNum) {
		if (spannedNum < 1) {
			spannedNum = DEFAULT_COLUMN_SPANNED_NUMBER;
		}
		splitRepeatedCells();
		if (mCellElement instanceof TableTableCellElement) {
			((TableTableCellElement) mCellElement).setTableNumberColumnsSpannedAttribute(new Integer(spannedNum));
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the column repeated number.
	 * 
	 * @param repeatedNum
	 *            the column repeated number that need to be set. If repeatedNum
	 *            is less than 1, default columns repeated number 1 will be set.
	 */
	void setColumnsRepeatedNumber(int repeatedNum) {
		if (repeatedNum < 1) {
			repeatedNum = DEFAULT_COLUMNS_REPEATED_NUMBER;
		}
		mCellElement.setTableNumberColumnsRepeatedAttribute(new Integer(repeatedNum));
	}

	/**
	 * Set the row spanned number.
	 * 
	 * @param spannedNum
	 *            row spanned number that need to be set the row spanned number
	 *            that need to be set. If spannedNum is less than 1, default row
	 *            spanned number 1 will be set.
	 */
	void setRowSpannedNumber(int spannedNum) {
		if (spannedNum < 1) {
			spannedNum = DEFAULT_ROW_SPANNED_NUMBER;
		}
		splitRepeatedCells();
		if (mCellElement instanceof TableTableCellElement) {
			((TableTableCellElement) mCellElement).setTableNumberRowsSpannedAttribute(new Integer(spannedNum));
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Judge if the ODF DOM element of this cell is the covered cell element.
	 * 
	 * @return true if the Odf element is TableCoveredTableCellElement
	 */
	boolean isCoveredElement() {
		if (mCellElement instanceof TableCoveredTableCellElement) {
			return true;
		}
		return false;
	}

	/**
	 * Get the style name of this cell.
	 * 
	 * @return the name of the style
	 */
	public String getStyleName() {
		OdfStyleBase style = getStyleHandler().getStyleElementForRead();
		if (style == null) {
			return "";
		}
		if (style instanceof OdfStyle)
			return ((OdfStyle) style).getStyleNameAttribute();
		else
			return "";
	}

	/**
	 * Set a formula to the cell.
	 * <p>
	 * Please note, the parameter <code>formula</code> will not be checked and
	 * interpreted; the cell value will not be calculated. It's just simply set
	 * as a formula attribute.
	 * 
	 * @param formula
	 *            the formula that need to be set.
	 * @see org.odftoolkit.odfdom.dom.attribute.table.TableFormulaAttribute
	 * @throws IllegalArgumentException
	 *             if formula is null, an IllegalArgumentException will be
	 *             thrown.
	 */
	public void setFormula(String formula) {
		if (formula == null) {
			throw new IllegalArgumentException("formula shouldn't be null.");
		}
		splitRepeatedCells();
		mCellElement.setTableFormulaAttribute(formula);
	}

	/**
	 * Get the formula string of the cell.
	 * 
	 * @return the formula representation of the cell
	 *         <p>
	 *         If the cell does not contain a formula, null will be returned.
	 * 
	 */
	public String getFormula() {
		return mCellElement.getTableFormulaAttribute();
	}

	// /**
	// * get the error value of the cell
	// * if the formula can not be calculated, an error will be set
	// * @return
	// * return 0 if the cell has no error
	// * return the error value of the cell if the formula result can not be
	// calculated
	// * such as divided by 0
	// */
	// public long getError()
	// {
	// return 0;
	// }
	/**
	 * Set the currency symbol and overall format of a currency cell.
	 * <p>
	 * Please note the overall format includes the symbol character, for
	 * example: $#,##0.00.
	 * <p>
	 * This function only works for currency.
	 * 
	 * @param currencySymbol
	 *            the currency symbol
	 * @param format
	 *            overall format
	 * @throws IllegalArgumentException
	 *             if input currencySymbol or format is null, an
	 *             IllegalArgumentException will be thrown.
	 */
	public void setCurrencyFormat(String currencySymbol, String format) {
		if (currencySymbol == null) {
			throw new IllegalArgumentException("currencySymbol shouldn't be null.");
		}
		if (format == null) {
			throw new IllegalArgumentException("format shouldn't be null.");
		}
		splitRepeatedCells();
		String type = mCellElement.getOfficeValueTypeAttribute();
		OfficeValueTypeAttribute.Value typeValue = null;
		msFormatString = format;
		if (type != null) {
			typeValue = OfficeValueTypeAttribute.Value.enumValueOf(type);
		}

		if (typeValue != OfficeValueTypeAttribute.Value.CURRENCY) {
			throw new IllegalArgumentException();
		}

		OdfNumberCurrencyStyle currencyStyle = new OdfNumberCurrencyStyle((OdfFileDom) mCellElement.getOwnerDocument(),
				currencySymbol, format, getUniqueCurrencyStyleName());
		mCellElement.getAutomaticStyles().appendChild(currencyStyle);
		setDataDisplayStyleName(currencyStyle.getStyleNameAttribute());
		Double value = getCurrencyValue();

		// set display text
		if (value != null) {
			setDisplayTextContent(formatCurrency(currencyStyle, value.doubleValue()), null);
		}
	}

	// This method doesn't handle style:map element.
	private String formatCurrency(OdfNumberCurrencyStyle currencyStyle, double value) {
		String valuestr = "";
		for (Node m : new DomNodeList(currencyStyle.getChildNodes())) {
			if (m instanceof NumberCurrencySymbolElement) {
				valuestr += m.getTextContent();
			} else if (m instanceof NumberNumberElement) {
				String numberformat = currencyStyle.getNumberFormat();
				valuestr += (new DecimalFormat(numberformat)).format(value);
			} else if (m instanceof NumberTextElement) {
				String textcontent = m.getTextContent();
				if (textcontent == null || textcontent.length() == 0) {
					textcontent = " ";
				}
				valuestr += textcontent;
			}
		}
		return valuestr;
	}

/**
	 * Set the format string of the cell.
	 * <p>
	 * This function only works for float, date, time and percentage, otherwise an
	 * {@link java.lang.IllegalArgumentException} will be thrown. 
	 * <p>
	 * For value type float and percentage, the <code>formatStr</code> must follow the encoding 
	 * rule of {@link java.text.DecimalFormat <code>java.text.DecimalFormat</code>}.
	 * For value type date and time, the <code>formatStr</code> must follow the encoding 
	 * rule of {@link java.text.SimpleDateFormat <code>java.text.SimpleDateFormat</code>}.
	 * <p>
	 * Refer to {@link org.odftoolkit.simple.table.Cell#setCurrencyFormat <code>setCurrencyFormat</code>} to set the format of currency.
	 * <p>
	 * If the cell value type is not set, the method will try to give it a value type, according 
	 * to common ordination. The adapt order is: percentage-> time-> date-> float.
	 * <blockquote>
	 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart showing ValueType, Distinguish Symbol
	 * and Distinguish Priority.">
	 *     <tr bgcolor="#ccccff">
	 *          <th align=left>ValueType
	 *          <th align=left>Distinguish Symbol
	 *          <th align=left>Distinguish Priority
	 *     <tr valign=top>
	 *          <td>percentage
	 *          <td>%
	 *          <td>1
	 *     <tr valign=top>
	 *          <td>time
	 *          <td>H, k, m, s, S
	 *          <td>2
	 *     <tr valign=top>
	 *          <td>date
	 *          <td>y, M, w, W, D, d, F, E, K, h
	 *          <td>3
	 *     <tr valign=top>
	 *          <td>float
	 *          <td>#, 0
	 *          <td>4
	 * </table>
	 * </blockquote>
	 * The adapt result may be inaccurate, so you'd better set value type before call this method. 
	 * If adaptive failed, an {@link java.lang.UnsupportedOperationException} will be thrown.
	 * <p>
	 * @param formatStr	the cell need be formatted as this specified format string.
	 * @throws IllegalArgumentException if <code>formatStr</code> is null or the cell value type is supported.
	 * @throws UnsupportedOperationException if the adaptive failed, when cell value type is not set.
	 * @see java.text.SimpleDateFormat
	 * @see java.text.DecimalFormat
	 */
	public void setFormatString(String formatStr) {
		if (formatStr == null) {
			throw new IllegalArgumentException("formatStr shouldn't be null.");
		}
		String type = getValueType();
		if (type == null) {
			if (formatStr.contains("%")) {
				setValueType("percentage");
			} else if (formatStr.contains("H") || formatStr.contains("k") || formatStr.contains("m")
					|| formatStr.contains("s") || formatStr.contains("S")) {
				setValueType("time");
			} else if (formatStr.contains("y") || formatStr.contains("M") || formatStr.contains("w")
					|| formatStr.contains("W") || formatStr.contains("D") || formatStr.contains("d")
					|| formatStr.contains("F") || formatStr.contains("E") || formatStr.contains("K")
					|| formatStr.contains("h")) {
				setValueType("date");
			} else if (formatStr.contains("#") || formatStr.contains("0")) {
				setValueType("float");
			} else {
				throw new UnsupportedOperationException("format string: " + formatStr
						+ " can't be adapted to a possible value type.");
			}
			type = getValueType();
		}
		setCellFormatString(formatStr, type);
	}

	private void setCellFormatString(String formatStr, String type) {
		OfficeValueTypeAttribute.Value typeValue = null;
		msFormatString = formatStr;
		splitRepeatedCells();
		typeValue = OfficeValueTypeAttribute.Value.enumValueOf(type);
		if (typeValue == OfficeValueTypeAttribute.Value.FLOAT) {
			OdfNumberStyle numberStyle = new OdfNumberStyle((OdfFileDom) mCellElement.getOwnerDocument(), formatStr,
					getUniqueNumberStyleName());
			mCellElement.getAutomaticStyles().appendChild(numberStyle);
			setDataDisplayStyleName(numberStyle.getStyleNameAttribute());
			Double value = getDoubleValue();
			if (value != null) {
				setDisplayTextContent((new DecimalFormat(formatStr)).format(value.doubleValue()), null);
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.DATE) {
			OdfNumberDateStyle dateStyle = new OdfNumberDateStyle((OdfFileDom) mCellElement.getOwnerDocument(),
					formatStr, getUniqueDateStyleName(), null);
			mCellElement.getAutomaticStyles().appendChild(dateStyle);
			setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
			String dateStr = mCellElement.getOfficeDateValueAttribute();
			if (dateStr != null) {
				Calendar date = getDateValue();
				setDisplayTextContent((new SimpleDateFormat(formatStr)).format(date.getTime()), null);
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.TIME) {
			OdfNumberTimeStyle timeStyle = new OdfNumberTimeStyle((OdfFileDom) mCellElement.getOwnerDocument(),
					formatStr, getUniqueDateStyleName());
			mCellElement.getAutomaticStyles().appendChild(timeStyle);
			setDataDisplayStyleName(timeStyle.getStyleNameAttribute());
			String timeStr = mCellElement.getOfficeTimeValueAttribute();
			if (timeStr != null) {
				Calendar time = getTimeValue();
				setDisplayTextContent((new SimpleDateFormat(formatStr)).format(time.getTime()), null);
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			OdfNumberPercentageStyle dateStyle = new OdfNumberPercentageStyle((OdfFileDom) mCellElement
					.getOwnerDocument(), formatStr, getUniquePercentageStyleName());
			mCellElement.getAutomaticStyles().appendChild(dateStyle);
			setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
			Double value = getPercentageValue();
			if (value != null) {
				setDisplayTextContent((new DecimalFormat(formatStr)).format(value.doubleValue()), null);
			}
		} else {
			throw new IllegalArgumentException("This function doesn't support " + typeValue + " cell.");
		}
	}

	private void setDataDisplayStyleName(String name) {
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForWrite();
		if (styleElement != null) {
			styleElement.setOdfAttributeValue(OdfName.newName(OdfDocumentNamespace.STYLE, "data-style-name"), name);
		}
	}

	private String getDataDisplayStyleName() {
		String datadisplayStylename = null;
		OdfStyleBase styleElement = getStyleHandler().getStyleElementForRead();
		if (styleElement != null) {
			datadisplayStylename = styleElement.getOdfAttributeValue(OdfName.newName(OdfDocumentNamespace.STYLE,
					"data-style-name"));
		}

		return datadisplayStylename;
	}

	private String getUniqueNumberStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("n%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getNumberStyle(unique_name) != null);
		return unique_name;
	}

	private String getUniqueDateStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("d%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getDateStyle(unique_name) != null);
		return unique_name;
	}

	private String getUniquePercentageStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("p%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getPercentageStyle(unique_name) != null);
		return unique_name;
	}

	// private String getUniqueCellStyleName() {
	// String unique_name;
	// OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
	// do {
	// unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
	// } while (styles.getStyle(unique_name, OdfStyleFamily.TableCell) != null);
	// return unique_name;
	// }
	private String getUniqueCurrencyStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("c%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getCurrencyStyle(unique_name) != null);
		return unique_name;
	}

	/**
	 * Get the format string of the cell.
	 * 
	 * @return the format string of the cell
	 */
	public String getFormatString() {
		String type = mCellElement.getOfficeValueTypeAttribute();
		OfficeValueTypeAttribute.Value typeValue = null;
		if (type != null) {
			typeValue = OfficeValueTypeAttribute.Value.enumValueOf(type);
		}

		if (typeValue == OfficeValueTypeAttribute.Value.FLOAT) {
			String name = getDataDisplayStyleName();
			OdfNumberStyle style = mCellElement.getAutomaticStyles().getNumberStyle(name);
			if (style == null) {
				style = mDocument.getDocumentStyles().getNumberStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.DATE) {
			String name = getDataDisplayStyleName();
			OdfNumberDateStyle style = mCellElement.getAutomaticStyles().getDateStyle(name);
			if (style == null) {
				style = mDocument.getDocumentStyles().getDateStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.TIME) {
			String name = getDataDisplayStyleName();
			OdfNumberDateStyle style = mCellElement.getAutomaticStyles().getDateStyle(name);
			if (style == null) {
				style = mDocument.getDocumentStyles().getDateStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.CURRENCY) {
			String name = getCurrencyDisplayStyleName();
			OdfNumberCurrencyStyle dataStyle = mCellElement.getAutomaticStyles().getCurrencyStyle(name);
			if (dataStyle == null) {
				dataStyle = mDocument.getDocumentStyles().getCurrencyStyle(name);
			}
			if (dataStyle != null) {
				return dataStyle.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			String name = getDataDisplayStyleName();
			OdfNumberPercentageStyle style = mCellElement.getAutomaticStyles().getPercentageStyle(name);
			if (style == null) {
				style = mDocument.getDocumentStyles().getPercentageStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		}
		return null;
	}

	private String getCurrencyDisplayStyleName() {
		String name = getDataDisplayStyleName();
		OdfNumberCurrencyStyle dataStyle = mCellElement.getAutomaticStyles().getCurrencyStyle(name);
		if (dataStyle == null) {
			dataStyle = mDocument.getDocumentStyles().getCurrencyStyle(name);
		}

		if (dataStyle != null) {
			return dataStyle.getConditionStyleName(getCurrencyValue());
		}
		return null;
	}

	/**
	 * Remove all the text content of cell.
	 */
	public void removeTextContent() {
		splitRepeatedCells();
		// delete text:p child element
		Node node = mCellElement.getFirstChild();
		while (node != null) {
			Node nextNode = node.getNextSibling();
			if (node instanceof TextPElement || node instanceof TextHElement || node instanceof TextListElement) {
				mCellElement.removeChild(node);
			}
			node = nextNode;
		}
	}

	/**
	 * Remove all the content of the cell.
	 */
	public void removeContent() {
		splitRepeatedCells();
		Node node = mCellElement.getFirstChild();
		while (node != null) {
			Node nextNode = node.getNextSibling();
			mCellElement.removeChild(node);
			node = nextNode;
		}
	}

	/**
	 * Append the content of another cell.
	 * 
	 * @param fromCell
	 *            another cell whose content will be appended to this cell
	 */
	void appendContentFrom(Cell fromCell) {
		splitRepeatedCells();
		TableTableCellElementBase cell = fromCell.getOdfElement();
		Node node = cell.getFirstChild();
		while (node != null) {
			if (node instanceof OdfTextParagraph) {
				if (!TextExtractor.getText((OdfTextParagraph) node).equals("")) {
					mCellElement.appendChild(node.cloneNode(true));
				}
			} else {
				mCellElement.appendChild(node.cloneNode(true));
			}
			node = node.getNextSibling();
		}
	}

	/*****************************************
	 * Moved from Table
	 * 
	 *******************************************/
	/**
	 * This method is invoked by insertCellBefore and insertRowBefore When it is
	 * needed to clone a cell and the cell is a cover cell, for some instance,
	 * we need to find the cell who covers this cell. So this method is to get
	 * the cell who covers this cell
	 */
	Cell getCoverCell() {
		int startRowi = getRowIndex();
		int startColumni = getColumnIndex();

		for (int i = startRowi; i >= 0; i--) {
			Row aRow = mOwnerTable.getRowByIndex(i);
			for (int j = startColumni; j >= 0; j--) {
				if (i == startRowi && j == startColumni) {
					continue;
				}
				Cell cell = aRow.getCellByIndex(j);
				if (cell.getOdfElement() instanceof TableTableCellElement) {
					TableTableCellElement cellEle = (TableTableCellElement) cell.getOdfElement();
					if ((cellEle.getTableNumberColumnsSpannedAttribute() + j > startColumni)
							&& (cellEle.getTableNumberRowsSpannedAttribute() + i > startRowi)) {
						return mOwnerTable.getCellInstance(cellEle, 0, 0);
					}
				}
			}
		}
		return null;
	}

	/**
	 * This method is invoked by getCoverCell. It's to get the cell in a same
	 * row who covers this cell.
	 * 
	 * @return the cell in a same row who covers this cell
	 *         <p>
	 *         Null if there is no cell who covers this cell
	 */
	Cell getCoverCellInSameRow() {
		int startRowi = getRowIndex();
		int startColumni = getColumnIndex();

		for (int j = startColumni - 1; j >= 0; j--) {
			Cell cell = mOwnerTable.getCellByPosition(j, startRowi);
			if (cell.getOdfElement() instanceof TableCoveredTableCellElement) {
				continue;
			}

			int oldSpanN = cell.getColumnSpannedNumber();
			if (oldSpanN + j > startColumni) {
				// cell.setColumnSpannedNumber(oldSpanN-1);
				return cell;
			}
			return null;
		}
		return null;
	}

	/**
	 * This method is invoked by getCoverCell
	 */
	Cell getCoverCellInSameColumn() {
		int startRowi = getRowIndex();
		int startColumni = getColumnIndex();

		for (int i = startRowi - 1; i >= 0; i--) {
			Cell cell = mOwnerTable.getCellByPosition(startColumni, i);
			if (cell.getOdfElement() instanceof TableCoveredTableCellElement) {
				continue;
			}

			int oldSpanN = cell.getRowSpannedNumber();
			if (oldSpanN + i > startRowi) {
				// cell.setRowSpannedNumber(oldSpanN-1);
				return cell;
			}
			return null;
		}
		return null;
	}

	/**
	 * Return the font definition for this cell.
	 * <p>
	 * Null will be returned if there is no explicit style definition, or even
	 * default style definition, for this cell.
	 * 
	 * @return the font definition null if there is no style definition for this
	 *         cell
	 * 
	 * @since 0.3
	 */
	public Font getFont() {
		return getStyleHandler().getFont(Document.ScriptType.WESTERN);

	}

	/**
	 * Set font style for this cell.
	 * 
	 * @param font
	 *            - the font
	 * 
	 * @since 0.3
	 */
	public void setFont(Font font) {
		getStyleHandler().setFont(font);
	}

	/**
	 * Set the border style definitions for this cell.
	 * <p>
	 * This method will invoke
	 * <code>CellStyleHandler.setBorders(Border border, SimpleCellBordersType bordersType).</code>
	 * 
	 * @param bordersType
	 *            - A predefined border type
	 * @param border
	 *            - border style description
	 * 
	 * @see CellStyleHandler#setBorders(Border border,
	 *      StyleTypeDefinitions.CellBordersType bordersType)
	 * @since 0.3
	 */
	public void setBorders(StyleTypeDefinitions.CellBordersType bordersType, Border border) {
		getStyleHandler().setBorders(border, bordersType);
	}

	/**
	 * Return the border setting for a specific border.
	 * <p>
	 * This method will invoke
	 * <code>CellStyleHandler.getBorder(SimpleCellBordersType type).</code>
	 * 
	 * @param type
	 *            - the border type which describes a single border
	 * @return the border setting
	 * 
	 * @see CellStyleHandler#getBorder(StyleTypeDefinitions.CellBordersType
	 *      type)
	 * @since 0.3.5
	 */
	public Border getBorder(CellBordersType type) {
		return getStyleHandler().getBorder(type);
	}

	/**
	 * Get the note text of this table cell. If there is no note on this cell,
	 * <code>null</code> will be returned.
	 * <p>
	 * The note may contain text list, text paragraph and styles, but this
	 * method extracts only text from them.
	 * 
	 * @return the note text of this cell.
	 */
	public String getNoteText() {
		String noteString = null;
		OfficeAnnotationElement annotation = OdfElement.findFirstChildNode(OfficeAnnotationElement.class, mCellElement);
		if (annotation != null) {
			noteString = "";
			Node n = annotation.getFirstChild();
			while (n != null) {
				Node m = n.getNextSibling();
				if (n instanceof TextPElement || n instanceof TextListElement) {
					noteString += TextExtractor.getText((OdfElement) n);
				}
				n = m;
			}
		}
		return noteString;
	}

	/**
	 * Set note text for this table cell. This method creates a text paragraph
	 * without style as note. The note text is text paragraph content.
	 * <p>
	 * Only simple text is supported to receive in this method, which is a sub
	 * function of office annotation. So overwriting a note with text might
	 * loose structure and styles.
	 * 
	 * @param note
	 *            note content.
	 */
	public void setNoteText(String note) {
		splitRepeatedCells();
		OfficeAnnotationElement annotation = OdfElement.findFirstChildNode(OfficeAnnotationElement.class, mCellElement);
		if (annotation == null) {
			OdfFileDom dom = (OdfFileDom) mCellElement.getOwnerDocument();
			annotation = (OfficeAnnotationElement) OdfXMLFactory.newOdfElement(dom, OdfName.newName(
					OdfDocumentNamespace.OFFICE, "annotation"));
		}
		TextPElement noteElement = OdfElement.findFirstChildNode(TextPElement.class, annotation);
		if (noteElement == null) {
			noteElement = annotation.newTextPElement();
		}
		noteElement.setTextContent(note);
		DcCreatorElement dcCreatorElement = OdfElement.findFirstChildNode(DcCreatorElement.class, annotation);
		if (dcCreatorElement == null) {
			dcCreatorElement = annotation.newDcCreatorElement();
		}
		dcCreatorElement.setTextContent(System.getProperty("user.name"));
		String dcDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
		DcDateElement dcDateElement = OdfElement.findFirstChildNode(DcDateElement.class, annotation);
		if (dcDateElement == null) {
			dcDateElement = annotation.newDcDateElement();
		}
		dcDateElement.setTextContent(dcDate);
		mCellElement.appendChild(annotation);
	}

	/**
	 * Insert an Image from the specified uri to cell. Note: if there is any
	 * other text content in this cell, it will be removed.
	 * 
	 * @param imageUri
	 *            The URI of the image that will be added to the cell, add image
	 *            stream to the package, in the 'Pictures/' graphic directory
	 *            with the same image file name as in the URI. If the imageURI
	 *            is relative first the user.dir is taken to make it absolute.
	 * @since 0.4.5
	 */
	public Image setImage(URI imageUri) {
		if (imageUri == null)
			return null;

		splitRepeatedCells();
		Image newImage;
		try {
			NodeList cellPs = mCellElement.getElementsByTagNameNS(OdfDocumentNamespace.TEXT.getUri(), "p");
			if (cellPs != null && cellPs.getLength() > 0) {
				for (int i = 0; i < cellPs.getLength(); i++) {
					mCellElement.removeChild(cellPs.item(i));
				}
			}

			if (mOwnerTable.mIsSpreadsheet) {
				newImage = Image.newImage(this, imageUri);
			} else {
				OdfFileDom dom = (OdfFileDom) mCellElement.getOwnerDocument();
				TextPElement pElement = dom.newOdfElement(TextPElement.class);
				mCellElement.appendChild(pElement);
				newImage = Image.newImage(Paragraph.getInstanceof(pElement), imageUri);
			}
			if (imageUri != null) {
				FrameRectangle rect = newImage.getRectangle();
				double height = rect.getHeight();
				double width = rect.getWidth();
				long widthInMI = new Double(width / 100).longValue();
				Column column = getTableColumn();
				if (widthInMI > column.getWidth()) {
					column.setWidth(widthInMI);
				}

				long heightInMI = new Double(height / 100).longValue();
				Row row = getTableRow();
				if (heightInMI > row.getHeight()) {
					row.setHeight(heightInMI, false);
				}
				return newImage;
			}
		} catch (Exception ex) {
			Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Get the Image from the specified cell.
	 * 
	 * @return If there is a image exist in this cell, An {@link java.awt.Image
	 *         Image} will be returned.
	 * @since 0.4.5
	 * @deprecated
	 */
	public BufferedImage getBufferedImage() {
		try {
			TextPElement pElement = OdfElement.findFirstChildNode(TextPElement.class, mCellElement);
			if (pElement != null) {
				DrawFrameElement drawFrame = OdfElement.findFirstChildNode(DrawFrameElement.class, pElement);
				if (drawFrame != null) {
					DrawImageElement imageElement = OdfElement.findFirstChildNode(DrawImageElement.class, drawFrame);
					if (imageElement != null) {
						String packagePath = imageElement.getXlinkHrefAttribute();
						OdfFileDom dom = (OdfFileDom) mCellElement.getOwnerDocument();
						OdfPackage mOdfPackage = dom.getDocument().getPackage();
						InputStream is = mOdfPackage.getInputStream(packagePath);
						BufferedImage image = ImageIO.read(is);
						return image;
					}
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Get the image from the specified cell.
	 * 
	 * @return If there is a image exist in this cell, an
	 *         {@link org.odftoolkit.simple.draw.Image Image} will be returned.
	 * @since 0.5.5
	 */
	public Image getImage() {
		try {
			TextPElement pElement = OdfElement.findFirstChildNode(TextPElement.class, mCellElement);
			if (pElement != null) {
				DrawFrameElement drawFrame = OdfElement.findFirstChildNode(DrawFrameElement.class, pElement);
				if (drawFrame != null) {
					DrawImageElement imageElement = OdfElement.findFirstChildNode(DrawImageElement.class, drawFrame);
					return Image.getInstanceof(imageElement);
				}
			} else {
				DrawFrameElement drawFrame = OdfElement.findFirstChildNode(DrawFrameElement.class, mCellElement);
				if (drawFrame != null) {
					DrawImageElement imageElement = OdfElement.findFirstChildNode(DrawImageElement.class, drawFrame);
					return Image.getInstanceof(imageElement);
				}
			}
		} catch (Exception ex) {
			Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	/**
	 * Return style handler for this cell
	 * 
	 * @return the style handler
	 * 
	 * @since 0.3
	 */
	public CellStyleHandler getStyleHandler() {
		if (mStyleHandler == null)
			mStyleHandler = new CellStyleHandler(this);
		return mStyleHandler;
	}

	public OdfElement getListContainerElement() {
		return getListContainerImpl().getListContainerElement();
	}

	public org.odftoolkit.simple.text.list.List addList() {
		Document ownerDocument = getTable().getOwnerDocument();
		if (ownerDocument instanceof SpreadsheetDocument) {
			throw new UnsupportedOperationException(
					"Open Office and Symphony can't show a list in spreadsheet document cell.");
		} else {
			return getListContainerImpl().addList();
		}
	}

	public org.odftoolkit.simple.text.list.List addList(ListDecorator decorator) {
		Document ownerDocument = getTable().getOwnerDocument();
		if (ownerDocument instanceof SpreadsheetDocument) {
			throw new UnsupportedOperationException(
					"Open Office and Symphony can't show a list in spreadsheet document cell.");
		} else {
			return getListContainerImpl().addList(decorator);
		}
	}

	public void clearList() {
		getListContainerImpl().clearList();
	}

	public Iterator<org.odftoolkit.simple.text.list.List> getListIterator() {
		return getListContainerImpl().getListIterator();
	}

	public boolean removeList(org.odftoolkit.simple.text.list.List list) {
		return getListContainerImpl().removeList(list);
	}

	/**
	 * Creates a new paragraph and append text
	 * 
	 * @param text
	 * @return the new paragraph
	 * @throws Exception
	 *             if the file DOM could not be created.
	 * @since 0.5.5
	 */
	public Paragraph addParagraph(String text) {
		Paragraph para = getParagraphContainerImpl().addParagraph(text);
		return para;
	}

	/**
	 * Remove paragraph from this document
	 * 
	 * @param para
	 *            - the instance of paragraph
	 * @return true if the paragraph is removed successfully, false if errors
	 *         happen.
	 *         
	 * @since 0.5.5
	 */
	public boolean removeParagraph(Paragraph para) {
		return getParagraphContainerImpl().removeParagraph(para);
	}
	
	/**
	 * Get the ODF element which can have <text:p> as child element directly.
	 * 
	 * @return - an ODF element which can have paragraph as child.
	 * 
	 * @since 0.5.5
	 */
	public OdfElement getParagraphContainerElement() {
		return getParagraphContainerImpl().getParagraphContainerElement();
	}
	/**
	 * Return a paragraph with a given index.
	 * <p>
	 * An index of zero represents the first paragraph.
	 * <p>
	 * If empty paragraph is skipped, the empty paragraph won't be counted.
	 * 
	 * @param index
	 *            - the index started from 0.
	 * @param isEmptyParagraphSkipped
	 *            - whether the empty paragraph is skipped or not
	 * @return the paragraph with a given index
	 * 
	 * @since 0.5.5
	 */
	public Paragraph getParagraphByIndex(int index, boolean isEmptyParagraphSkipped) {
		return getParagraphContainerImpl().getParagraphByIndex(index, isEmptyParagraphSkipped);
	}
	
	/**
	 * Return a paragraph with a given index. The index is in reverse order.
	 * <p>
	 * An index of zero represents the last paragraph.
	 * <p>
	 * If empty paragraph is skipped, the empty paragraph won't be counted.
	 * 
	 * @param reverseIndex
	 *            - the index started from 0 in reverse order.
	 * @param isEmptyParagraphSkipped
	 *            - whether the empty paragraph is skipped or not
	 * @return the paragraph with a given index
	 * 
	 * @since 0.5.5
	 */
	public Paragraph getParagraphByReverseIndex(int reverseIndex, boolean isEmptyParagraphSkipped) {
		return getParagraphContainerImpl().getParagraphByReverseIndex(reverseIndex, isEmptyParagraphSkipped);
	}
	
	/**
	 * Return an Iterator of the paragraph in this container.
	 * 
	 * @return an Iterator of the paragraph in this container
	 * 
	 * @since 0.5.5
	 */
	public Iterator<Paragraph> getParagraphIterator() {
		return getParagraphContainerImpl().getParagraphIterator();
	}

	public OdfElement getFrameContainerElement() {
		return mCellElement;
	}

	/**
	 * Specifies the allowed values of this cell in a list. Any value out of
	 * this list is invalid.
	 * <p>
	 * NOTE: Now, the validity rule does not take effect when a cell value
	 * is updated by Simple ODF API yet.
	 * 
	 * @param values
	 *            the list of allowed values.
	 * @since 0.6
	 */
	public void setValidityList(List<String> values) {
		try {
			TableContentValidationElement validation = getContentValidationEle();
			validation.setTableAllowEmptyCellAttribute(true);
			String split = "";
			StringBuilder listStr = new StringBuilder("");
			for (String value : values) {
				listStr.append(split);
				listStr.append("\"");
				listStr.append(value);
				listStr.append("\"");
				split = ";";
			}
			validation.setTableConditionAttribute("cell-content-is-in-list(" + listStr + ")");
			String tableName = getTableElement().getTableNameAttribute();
			validation.setTableBaseCellAddressAttribute(tableName + "." + getCellAddress());
			validation.setTableDisplayListAttribute("unsorted");
		} catch (Exception e) {
			Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Sets the title and the text of the tip, which will then be displayed if
	 * the cell is selected.
	 * 
	 * @param title
	 *            the title of the tip.
	 * @param text
	 *            the text of the tip.
	 * @since 0.6
	 */
	public void setInputHelpMessage(String title, String text) {
		try {
			TableContentValidationElement validationElement = getContentValidationEle();
			TableHelpMessageElement helpMessageElement = OdfElement.findFirstChildNode(TableHelpMessageElement.class,
					validationElement);
			if (helpMessageElement != null) {
				validationElement.removeChild(helpMessageElement);
			}
			helpMessageElement = validationElement.newTableHelpMessageElement();
			helpMessageElement.setTableTitleAttribute(title);
			helpMessageElement.setTableDisplayAttribute(true);
			helpMessageElement.newTextPElement().setTextContent(text);
		} catch (Exception e) {
			Logger.getLogger(Cell.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// private methods ///////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private TableContentValidationElement getContentValidationEle() throws Exception {
		Document ownerDocument = getOwnerDocument();
		OdfElement contentRootElement = ownerDocument.getContentRoot();
		TableContentValidationsElement validations = OdfElement.findFirstChildNode(
				TableContentValidationsElement.class, contentRootElement);
		if (validations == null) {
			validations = (TableContentValidationsElement) OdfXMLFactory.newOdfElement(ownerDocument
					.getContentDom(), OdfName.newName(OdfDocumentNamespace.TABLE, "content-validations"));
			contentRootElement.insertBefore(validations, contentRootElement.getFirstChild());
		}
		String validationName = getOdfElement().getTableContentValidationNameAttribute();
		TableContentValidationElement validationElement = null;
		if (validationName != null) {
			Node child = validations.getFirstChild();
			while (child != null) {
				TableContentValidationElement contentValidationElementRef = (TableContentValidationElement) child;
				if (validationName.equals(contentValidationElementRef.getTableNameAttribute())) {
					validationElement = contentValidationElementRef;
					break;
				}
			}
		} else {
			String valName = "val" + String.format("d%06x", (int) (Math.random() * 0xffffff));
			validationElement = validations.newTableContentValidationElement(valName);
			getOdfElement().setTableContentValidationNameAttribute(valName);
		}
		return validationElement;
	}

	private String getCellAddress() {
		char[] digits = { '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
				'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
		int i = getColumnIndex() + 1;
		char[] buf = new char[32];
		int charPos = 32;
		do {
			buf[--charPos] = digits[i % 26];
			i /= 26;
		} while (i != 0);
		String cs = new String(buf, charPos, (32 - charPos));
		return cs + (getRowIndex() + 1);
	}
	
	private ListContainerImpl getListContainerImpl() {
		if (listContainerImpl == null) {
			listContainerImpl = new ListContainerImpl();
		}
		return listContainerImpl;
	}

	private class ListContainerImpl extends AbstractListContainer {

		public OdfElement getListContainerElement() {
			return mCellElement;
		}
	}
	
	private class ParagraphContainerImpl extends AbstractParagraphContainer {
		public OdfElement getParagraphContainerElement() {
			return mCellElement;
		}
	}

	private ParagraphContainerImpl getParagraphContainerImpl() {
		if (paragraphContainerImpl == null)
			paragraphContainerImpl = new ParagraphContainerImpl();
		return paragraphContainerImpl;
	}
	
	//column can fit the width to the text, if column.isOptimalWidth() is true.
	//since 0.5.5
	private void optimizeCellSize(String content){
		JTextField txtField = new JTextField();
		// map font to awt font
		Font font = getFont();
		String fontFamilyName = font.getFamilyName();
		if (fontFamilyName == null) {
			fontFamilyName = "Times New Roman";
		}
		int fontStyleNum = java.awt.Font.PLAIN;
		FontStyle fontStyle = font.getFontStyle();
		if (fontStyle != null) {
			switch (fontStyle) {
			case BOLD:
				fontStyleNum = java.awt.Font.BOLD;
				break;
			case REGULAR:
				fontStyleNum = java.awt.Font.PLAIN;
				break;
			case ITALIC:
				fontStyleNum = java.awt.Font.ITALIC;
				break;
			case BOLDITALIC:
				fontStyleNum = java.awt.Font.BOLD | java.awt.Font.ITALIC;
				break;
			default:
				fontStyleNum = java.awt.Font.PLAIN;
			}
		}
		double fontSize = font.getSize();
		if (fontSize < 0.0001) {
			fontSize = 10;
		}
		txtField.setFont(new java.awt.Font(fontFamilyName, fontStyleNum, (int)fontSize));
		FontMetrics fm = txtField.getFontMetrics(txtField.getFont());
		// content width in pixels.
		int widthPixels = fm.stringWidth(content);
		// content height in pixels.
		// int heightPixels = fm.getHeight();
		OdfStyleBase properties = getStyleHandler().getStyleElementForRead();
		double millimeterPadding = 0.0;
		if(properties!=null){
			String padding = properties.getProperty(StyleTableCellPropertiesElement.Padding);
			if(padding!=null){
				millimeterPadding = Length.parseDouble(padding, Unit.MILLIMETER);
			}
		}
		// convert width pixels to mm
		double columnWidth = widthPixels / 2.83464;
		columnWidth += millimeterPadding * 2;
		Column column = getTableColumn();
		if (column.isOptimalWidth()) {
			double width = column.getWidth();
			if (width < columnWidth) {
				column.setWidth(columnWidth);
			}
		}
	}
}
