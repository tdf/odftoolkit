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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.OdfName;
import org.odftoolkit.odfdom.doc.number.OdfNumber;
import org.odftoolkit.odfdom.doc.number.OdfNumberCurrencyStyle;
import org.odftoolkit.odfdom.doc.number.OdfNumberCurrencySymbol;
import org.odftoolkit.odfdom.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.doc.number.OdfNumberText;
import org.odftoolkit.odfdom.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.doc.style.OdfStyle;
import org.odftoolkit.odfdom.doc.text.OdfTextParagraph;
import org.odftoolkit.odfdom.doc.text.OdfWhitespaceProcessor;
import org.odftoolkit.odfdom.dom.OdfNamespaceNames;
import org.odftoolkit.odfdom.dom.attribute.fo.FoWrapOptionAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
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
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.type.Color;
import org.w3c.dom.Node;

/**
 * OdfTableCell represents table cell feature in ODF document.
 * <p>
 * OdfTable provides methods to get/set/modify the cell content and cell properties.
 *
 */
public class OdfTableCell {

	TableTableCellElementBase mOdfElement;
	int mnRepeatedColIndex;
	int mnRepeatedRowIndex;
	OdfTable mOwnerTable;
	String msFormatString;
	private static Logger mLog = Logger.getLogger(OdfTableCell.class.getName());
	
	/**
	 * The default date format of table cell.
	 */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	
	/**
	 * The default time format of table cell.
	 */
	private static final String DEFAULT_TIME_FORMAT = "'PT'HH'H'mm'M'ss'S'";
	
	/**
	 * The default cell back color of table cell.
	 */
	private static final String DEFAULT_BACK_COLOR="#FFFFFF"; 
	
	/**
	 * The default horizontal alignment of table cell.
	 */
	private static final String DEFAULT_HORIZONTAL_ALIGN="start"; 
	
	/**
	 * The default vertical alignment of table cell.
	 */
	private static final String DEFAULT_VERTICAL_ALIGN="top"; 
	
	/**
	 * The default column spanned number.
	 */
	private static final int DEFAULT_COLUMN_SPANNED_NUMBER=1;
	
	/**
	 * The default row spanned number.
	 */
	private static final int DEFAULT_ROW_SPANNED_NUMBER=1;
	
	/**
	 * The default columns repeated number.
	 */
	private static final int DEFAULT_COLUMNS_REPEATED_NUMBER=1;
	TableTableCellElementBase mCellElement;

	OdfTableCell(TableTableCellElementBase odfElement, int repeatedColIndex, int repeatedRowIndex) {
		mCellElement = odfElement;
		mnRepeatedColIndex = repeatedColIndex;
		mnRepeatedRowIndex = repeatedRowIndex;
		mOwnerTable = getTable();
	}

	/**
	 * Get the <code>OdfTableCell</code> instance from the <code>TableTableCellElementBase</code> instance.
	 * <p>
	 * Each <code>TableTableCellElementBase</code> instance has a one-to-one relationship to the a <code>OdfTableCell</code> instance.
	 * 
	 * @param cellElement	the cell element that need to get the corresponding <code>OdfTableCell</code> instance
	 * @return the <code>OdfTableCell</code> instance that represents a specified cell element
	 */
	public static OdfTableCell getInstance(TableTableCellElementBase cellElement) {
		TableTableElement tableElement = null;
		Node node = cellElement.getParentNode();
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
			throw new IllegalArgumentException("the cellElement is not in the table dom tree");
		}

		OdfTableCell cell = table.getCellInstance(cellElement, 0, 0);
		int colRepeatedNum = cell.getColumnsRepeatedNumber();
		int rowRepeatedNum = cell.getTableRow().getRowsRepeatedNumber();
		if (colRepeatedNum > 1 && rowRepeatedNum > 1) {
			if (colRepeatedNum > 1) {
				mLog.warning("the cell has the repeated column number, and puzzled about get which repeated column index of the cell,");
			}
			if (rowRepeatedNum > 1) {
				mLog.warning("the row contains the current cell has the repeated row number, and puzzled about get which repeated row index of the cell,");
			}
			mLog.warning("here just return the first cell that the repeated column index is 0 and repeated row index is 0, too.");
		}
		return cell;
	}

	/**
	 * Return the horizontal alignment setting of this cell.
	 * <p>
	 * The returned value can be "center", "end", "justify", "left", "right", or "start".
	 * Note:"left" and "right" are deprecated.
	 * @return the horizontal alignment setting
	 */
	public String getHorizontalJustify() {
		OdfStyleBase styleElement = getCellStyleElement();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.ParagraphProperties,
					OdfName.newName(OdfNamespaceNames.FO, "text-align"));
			return styleElement.getProperty(property);
		}
		return null;
	}

	/**
	 * Set the horizontal alignment setting of this cell.
	 * <p>
	 * The parameter can be "center", "end", "justify", "left", "right", or "start".
	 * Note:"left" and "right" are deprecated.
	 * @param horiJustify	the horizontal alignment setting.
	 * If horiJustify is null, default horizontal align "center" will be set.
	 */
	public void setHorizontalJustify(String horiJustify) {
		if(horiJustify == null)
			horiJustify = DEFAULT_HORIZONTAL_ALIGN;
		doPrepare();
		OdfStyleBase styleElement = getCellStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.ParagraphProperties,
					OdfName.newName(OdfNamespaceNames.FO, "text-align"));
			styleElement.setProperty(property, horiJustify);
		}
	}

	/**
	 * Return the vertical alignment setting of this cell.
	 * <p>
	 * The returned value can be "auto", "automatic", "baseline", "bottom", "middle", or "top".
	 * @return the vertical alignment setting of this cell
	 * 
	 */
	public String getVerticalJustify() {
		OdfStyleBase styleElement = getCellStyleElement();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties,
					OdfName.newName(OdfNamespaceNames.STYLE, "vertical-align"));
			return styleElement.getProperty(property);
		}
		return null;
	}

	/**
	 * Set the vertical alignment setting of this cell.
	 * <p>
	 * The parameter can be "auto", "automatic", "baseline", "bottom", "middle", or "top".
	 * @param vertJustify	the vertical alignment setting.
	 * If vertJustify is null, default vertical align "top" will be set.
	 */
	public void setVerticalJustify(String vertJustify) {
		if(vertJustify == null)
			vertJustify = DEFAULT_VERTICAL_ALIGN;
		doPrepare();
		OdfStyleBase styleElement = getCellStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties,
					OdfName.newName(OdfNamespaceNames.STYLE, "vertical-align"));
			styleElement.setProperty(property, vertJustify);
		}
	}

	/**
	 * Return the wrap option of this cell.
	 * @return true if the cell content can be wrapped; 
	 * <p>
	 * false if the cell content cannot be wrapped.
	 */
	public boolean isTextWrapped() {
		OdfStyleBase styleElement = getCellStyleElement();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties,
					OdfName.newName(OdfNamespaceNames.FO, "wrap-option"));
			String wrapped = styleElement.getProperty(property);
			if ((wrapped != null) && (wrapped.equals(FoWrapOptionAttribute.Value.WRAP.toString()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set the wrap option of this cell.
	 * @param isTextWrapped	whether the cell content can be wrapped or not
	 */
	public void setTextWrapped(boolean isTextWrapped) {
		doPrepare();
		OdfStyleBase styleElement = getCellStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty property = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties,
					OdfName.newName(OdfNamespaceNames.FO, "wrap-option"));
			if (isTextWrapped) {
				styleElement.setProperty(property, FoWrapOptionAttribute.Value.WRAP.toString());
			} else {
				styleElement.setProperty(property, FoWrapOptionAttribute.Value.NO_WRAP.toString());
			}
		}
	}

	private TableTableRowElement findRowInTableHeaderRows(TableTableHeaderRowsElement headers, TableTableRowElement tr, int[] indexs) {
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
				if (returnEle != null) {//find
					indexs[0] = result;
					return returnEle;
				}
			} else if (m instanceof TableTableRowGroupElement) {
				TableTableRowGroupElement aGroup = (TableTableRowGroupElement) m;
				TableTableRowElement returnEle = findRowInTableRowGroup(aGroup, tr, resultIndexs);
				result += resultIndexs[0];
				if (returnEle != null) {//find
					indexs[0] = result;
					return returnEle;
				}
			} else if (m instanceof TableTableRowsElement) {
				TableTableRowsElement rows = (TableTableRowsElement) m;
				TableTableRowElement returnEle = findRowInTableRows(rows, tr, resultIndexs);
				result += resultIndexs[0];
				if (returnEle != null) {//find
					indexs[0] = result;
					return returnEle;
				}
			} else if (m instanceof TableTableRowElement) {
				if (m == tr) { //find
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
	 * @return the table containing this cell
	 */
	public OdfTable getTable() {
		TableTableElement tableElement = getTableElement();
		if (tableElement != null) {
			return OdfTable.getInstance(tableElement);
		}
		return null;
	}

	/**
	 * Get the index of the table column which contains this cell.
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
	 * @return the instance of table column feature which contains the cell.
	 */
	public OdfTableColumn getTableColumn() {
		OdfTable table = getTable();
		int index = getColumnIndex();
		return table.getColumnByIndex(index);
	}

	TableTableColumnElement getTableColumnElement() {
		//return OdfTableCellBaseImpl.getTableColumn((OdfTableCellBase) mCellElement);
		TableTableElement tableElement = getTableElement();
		int columnindex = getColumnIndex();
		OdfTable fTable = OdfTable.getInstance(tableElement);
		return fTable.getColumnElementByIndex(columnindex);
	}

	/**
	 * Get the instance of table row feature which contains this cell.
	 * @return the instance of table row feature which contains the cell.
	 */
	public OdfTableRow getTableRow() {
		OdfTable table = getTable();
		int index = getRowIndex();
		return table.getRowByIndex(index);
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
	 * If the cell is a covered cell, the owner cell will be returned;
	 * if the cell is a real cell , the cell itself will be returned.
	 * @return the cell that covers the current cell
	 */
	public OdfTableCell getOwnerTableCell() {
		OdfTable ownerTable = getTable();
		List<CellCoverInfo> coverList = ownerTable.getCellCoverInfos(0, 0, ownerTable.getColumnCount() - 1, ownerTable.getRowCount() - 1);
		return ownerTable.getOwnerCellByPosition(coverList, getColumnIndex(), getRowIndex());
	}

	/**
	 * Get the instance of <code>TableTableCellElementBase</code> which represents this cell.
	 * @return the instance of <code>TableTableCellElementBase</code> 
	 */
	public TableTableCellElementBase getOdfElement() {
		return mCellElement;
	}

	/**
	 * Return the currency code of this cell, for example, "USD", "EUR", "CNY", and etc. 
	 * <p>
	 * If the value type is not "currency", an IllegalArgumentException will be thrown.
	 * 
	 * @return the currency code
	 * <p>
	 * An IllegalArgumentException will be throw if the value type is not currency
	 */
	public String getCurrencyCode() {
		if (mCellElement.getOfficeValueTypeAttribute().equals(OfficeValueTypeAttribute.Value.CURRENCY.toString())) {
			return mCellElement.getOfficeCurrencyAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the currency code of this cell, for example, "USD", "EUR", "CNY", and etc. 
	 * <p>
	 * If the value type is not "currency", an IllegalArgumentException will be thrown.
	 * 
	 * @param currency	the currency code that need to be set.
	 * If input currency is null, an IllegalArgumentException will be thrown.
	 */
	public void setCurrencyCode(String currency) {
		if ( currency == null ){
			throw new IllegalArgumentException(
					"Currency code of cell should not be null.");
		} 
		doPrepare();
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
	 * Set the value type of this cell.
	 * The parameter can be "boolean", "currency", "date", "float", "percentage", "string", "time", and "void".
	 * <p>
	 * If the parameter <code>type</code> is not a valid cell type, an IllegalArgumentException will be thrown.
	 * 
	 * @param type	the type that need to be set
	 * If input type is null, an IllegalArgumentException will be thrown.
	 */
	public void setValueType(String type) {
		if( type == null)
			throw new IllegalArgumentException("type shouldn't be null.");
		String sType = type.toLowerCase();
		OfficeValueTypeAttribute.Value value = OfficeValueTypeAttribute.Value.enumValueOf(sType);
		if (value == null) {
			throw new IllegalArgumentException("the value type of cell is not valid");
		}

		mCellElement.setOfficeValueTypeAttribute(sType);
	}

	/**
	 * Get the value type of this cell.
	 * The returned value can be "boolean", "currency", "date", "float", "percentage", "string", "time", and "void".
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
	 * Get the value of this cell as double.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not float.
	 * 
	 * @return the double value of this cell
	 * <p>
	 * An IllegalArgumentException will be thrown if the cell type is not float.
	 */
	public Double getDoubleValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.FLOAT) {
			return mCellElement.getOfficeValueAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Get the value of this cell as double.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not currency.
	 * 
	 * @return the currency value of this cell as a double
	 * <p>
	 * An IllegalArgumentException will be thrown if the cell type is not currency.
	 */
	public double getCurrencyValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.CURRENCY) {
			Double officeValueAttribute = mCellElement.getOfficeValueAttribute();
			if (officeValueAttribute != null) {
				return officeValueAttribute.doubleValue();
			} else {
				return 0.0;
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Get the symbol of currency.
	 * @return the currency symbol
	 * <p>
	 * An IllegalArgumentException will be thrown if the value type is not currency.
	 */
	public String getCurrencySymbol() {
		if (getTypeAttr() != OfficeValueTypeAttribute.Value.CURRENCY) {
			throw new IllegalArgumentException();
		}

		OdfStyle style = getCellStyleElement();
		if (style != null) {
			String dataStyleName = style.getOdfAttributeValue(OdfName.newName(OdfNamespaceNames.STYLE, "data-style-name"));
			OdfNumberCurrencyStyle dataStyle = mCellElement.getAutomaticStyles().getCurrencyStyle(dataStyleName);
			if (dataStyle == null) {
				dataStyle = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getCurrencyStyle(dataStyleName);
			}
			if ((dataStyle != null) && (dataStyle.getCurrencySymbolElement() != null)) {
				return dataStyle.getCurrencySymbolElement().getTextContent();
			}
		}

		return null;
	}

	/**
	 * Set the value and currency of the cell,
	 * and set the value type as currency.
	 * 
	 * @param value			the value that will be set 
	 * @param currency	the currency that will be set.
	 * If input currency is null, an IllegalArgumentException will be thrown.
	 */
	public void setCurrencyValue(double value, String currency) {
		if ( currency == null ) {
			throw new IllegalArgumentException("currency shouldn't be null.");
		}
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.CURRENCY);
		mCellElement.setOfficeValueAttribute(new Double(value));
		mCellElement.setOfficeCurrencyAttribute(currency);
	}

	/**
	 * Get the cell value as double.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not percentage.
	 * 
	 * @return the double value of cell
	 * <p>
	 * An IllegalArgumentException will be thrown if the cell type is not percentage.
	 */
	public double getPercentageValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			return mCellElement.getOfficeValueAttribute().doubleValue();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a percentage value
	 * and set the value type as percentage too.
	 * 
	 * @param value	the value that will be set
	 */
	public void setPercentageValue(double value) {
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.PERCENTAGE);
		mCellElement.setOfficeValueAttribute(new Double(value));
	}

	/**
	 * Get the text displayed in this cell.
	 * 
	 * @return the text displayed in this cell
	 */
	public String getDisplayText() {
		//TODO: This function doesn't work well if a cell contains a list.
		//Refer to testGetSetTextValue();
		OdfWhitespaceProcessor textProcessor = new OdfWhitespaceProcessor();
		return textProcessor.getText(mCellElement);
	}

	/**
	 * Set the text displayed in this cell.
	 * <p>
	 * Please note the displayed text in ODF viewer might be different with the value set by this method,
	 * because the displayed text in ODF viewer is calculated and set by editor. 
	 * 
	 * @param content	the displayed text. 
	 * If content is null, it will display the empty string instead. 
	 */
	public void setDisplayText(String content) {
		if (content == null) {
			content = "";
		}
		removeContent();

		OdfWhitespaceProcessor textProcessor = new OdfWhitespaceProcessor();
		OdfTextParagraph para = new OdfTextParagraph((OdfFileDom) mCellElement
				.getOwnerDocument());
		textProcessor.append(para, content);

		mCellElement.appendChild(para);
	}

	/**
	 * Set the text displayed in this cell, with a specified style name.
	 * <p>
	 * Please note the displayed text in ODF viewer might be different with the value set by this method,
	 * because the displayed text in ODF viewer are calculated and set by editor. 
	 * 
	 * @param content    the displayed text. If content is null, it will display the empty string instead. 
	 * @param stylename  the style name. If stylename is null, the content will use the default paragraph style.
	 */
	public void setDisplayText(String content, String stylename) {
		if (content == null) {
			content = "";
		}
		removeContent();

		OdfWhitespaceProcessor textProcessor = new OdfWhitespaceProcessor();
		OdfTextParagraph para = new OdfTextParagraph((OdfFileDom) mCellElement.getOwnerDocument());
		if((stylename != null) && (stylename.length() > 0) )
			para.setStyleName(stylename);
		textProcessor.append(para, content);

		mCellElement.appendChild(para);
	}

	/**
	 * Set the cell value as a double
	 * and set the value type to be float.
	 * 
	 * @param value	the double value that will be set
	 */
	public void setDoubleValue(double value) {
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.FLOAT);
		mCellElement.setOfficeValueAttribute(new Double(value));
		setDisplayText(value + "");
	}

	/**
	 * Get the cell value as boolean.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not boolean.
	 * 
	 * @return the boolean value of cell
	 * <p>
	 * An IllegalArgumentException will be thrown if the cell type is not boolean.
	 */
	public Boolean getBooleanValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.BOOLEAN) {
			return mCellElement.getOfficeBooleanValueAttribute();
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a boolean
	 * and set the value type to be boolean.
	 * 
	 * @param value	the value of boolean type
	 */
	public void setBooleanValue(boolean value) {
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.BOOLEAN);
		mCellElement.setOfficeBooleanValueAttribute(new Boolean(value));
		setDisplayText(value + "");
	}

	/**
	 * Get the cell value as Calendar.
	 * <p>
	 * Throw IllegalArgumentException if the cell type is not date.
	 * 
	 * @return the Calendar value of cell
	 * <p>
	 * An IllegalArgumentException will be thrown if the cell type is not date.
	 */
	public Calendar getDateValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.DATE) {
			String dateStr = mCellElement.getOfficeDateValueAttribute();
			if (dateStr == null) {
				return null;
			}
			Date date = parseString(dateStr, DEFAULT_DATE_FORMAT);
			Calendar calender = Calendar.getInstance();
			calender.setTime(date);
			return calender;
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the cell value as a date,
	 * and set the value type to be date.
	 * 
	 * @param date	the value of calendar type
	 */
	public void setDateValue(Calendar date) {
		if (date == null) {
			throw new IllegalArgumentException("date shouldn't be null.");
		}
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.DATE);
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
		String svalue = simpleFormat.format(date.getTime());
		mCellElement.setOfficeDateValueAttribute(svalue);
		setDisplayText(svalue);
	}

	/**
	 * Set the cell value as a string,
	 * and set the value type to be string.
	 * 
	 * @param str	the value of string type. 
	 * If input string is null, an empty string will be set.
	 */
	public void setStringValue(String str) {
		if (str == null) {
			str = "";
		}
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.STRING);
		mCellElement.setOfficeStringValueAttribute(str);
		setDisplayText(str);
	}

	//Note: if you want to change the cell
	//doPrepare must be called first in order to 
	//1. update parent row if the row is the repeated rows.
	//2. update the cell itself if the cell is the column repeated cells.
	private void doPrepare() {
		splitRepeatedCells();
	}

	void splitRepeatedCells() {
		OdfTable table = getTable();
		//1.if the parent row is the repeated row
		//the repeated row has to be separated
		//after this the cell element and repeated index will be updated
		//according to the new parent row
		OdfTableRow row = getTableRow();
		int colIndex = getColumnIndex();
		if (row.getRowsRepeatedNumber() > 1) {
			row.splitRepeatedRows();
			OdfTableCell cell = row.getCellByIndex(colIndex);
			mCellElement = cell.getOdfElement();
			mnRepeatedColIndex = cell.mnRepeatedColIndex;
			mnRepeatedRowIndex = cell.mnRepeatedRowIndex;
		}

		//2.if the cell is the column repeated cell
		//this repeated cell has to be separated
		int repeateNum = getColumnsRepeatedNumber();
		if (repeateNum > 1) {
			//change this repeated column to several single columns
			TableTableCellElementBase ownerCellElement = null;
			int repeatedColIndex = mnRepeatedColIndex;
			Node refElement = mCellElement;
			for (int i = repeateNum - 1; i >= 0; i--) {
				TableTableCellElementBase newCell = (TableTableCellElementBase) mCellElement.cloneNode(true);
				newCell.removeAttributeNS(OdfNamespaceNames.TABLE.getUri(), "number-columns-repeated");
				row.getOdfElement().insertBefore(newCell, refElement);
				refElement = newCell;
				if (repeatedColIndex == i) {
					ownerCellElement = newCell;
				} else {
					table.updateCellRepository(mCellElement, i, mnRepeatedRowIndex, newCell, 0, mnRepeatedRowIndex);
				}
			}
			//remove this column element
			row.getOdfElement().removeChild(mCellElement);

			if (ownerCellElement != null) {
				table.updateCellRepository(mCellElement, mnRepeatedColIndex, mnRepeatedRowIndex, ownerCellElement, 0, mnRepeatedRowIndex);
				mCellElement = ownerCellElement;
				mnRepeatedColIndex = 0;
			}
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
	 * Get the cell value as Calendar.
	 * <p>
	 * Throw exception if the cell type is not time.
	 * 
	 * @return the Calendar value of cell
	 * <p>
	 * An IllegalArgumentException will be thrown if the cell type is not time.
	 */
	public Calendar getTimeValue() {
		if (getTypeAttr() == OfficeValueTypeAttribute.Value.TIME) {
			String timeStr = mCellElement.getOfficeTimeValueAttribute();
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
	 * Set the cell value as a time and set the value type to be time too.
	 * 
	 * @param time
	 *            the value of calendar type. If input time is null, an
	 *            IllegalArgumentException exception will be thrown.
	 */
	public void setTimeValue(Calendar time) {
		if(time==null){
			throw new IllegalArgumentException("time shouldn't be null.");
		}
		doPrepare();
		setTypeAttr(OfficeValueTypeAttribute.Value.TIME);
		SimpleDateFormat simpleFormat = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
		String svalue = simpleFormat.format(time.getTime());
		mCellElement.setOfficeTimeValueAttribute(svalue);
		setDisplayText(svalue);
	}

	private Date parseString(String value, String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
		Date simpleDate = null;
		try {
			simpleDate = simpleFormat.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return simpleDate;
	}

	/**
	 * Get the background color of this cell.
	 * <p>
	 * If no back ground color is set, null will be returned.
	 * 
	 * @return the background color of this cell 
	 */
	public Color getCellBackColor() {

		Color color = null;
		OdfStyleBase styleElement = getCellStyleElement();
		if (styleElement != null) {
			OdfStyleProperty bkColorProperty = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties,
					OdfName.newName(OdfNamespaceNames.FO, "background-color"));
			String property = styleElement.getProperty(bkColorProperty);

			color = new Color(property);
		}
		return color;
	}

	/**
	 * Set the back ground color of this cell.
	 * 
	 * @param cellBackColor
	 *            the background color that need to set. 
	 *            If cellBackColor is null, default back color #FFFFFF will be set.
	 */
	public void setCellBackColor(Color cellBackColor) {
		if(cellBackColor==null){
			cellBackColor=Color.valueOf(DEFAULT_BACK_COLOR);
		}
		doPrepare();
		OdfStyleBase styleElement = getCellStyleElementForWrite();
		if (styleElement != null) {
			OdfStyleProperty bkColorProperty = OdfStyleProperty.get(OdfStylePropertiesSet.TableCellProperties,
					OdfName.newName(OdfNamespaceNames.FO, "background-color"));
			styleElement.setProperty(bkColorProperty, cellBackColor.toString());
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
	public int getColumnsRepeatedNumber() {
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
	 *            the column spanned number to be set. If spannedNum is less than 1,
	 *            default column spanned number 1 will be
	 *            set.
	 */
	public void setColumnSpannedNumber(int spannedNum) {
		if (spannedNum < 1) {
			spannedNum = DEFAULT_COLUMN_SPANNED_NUMBER;
		}
		doPrepare();
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
	public void setColumnsRepeatedNumber(int repeatedNum) {
		if (repeatedNum < 1) {
			repeatedNum=DEFAULT_COLUMNS_REPEATED_NUMBER;
		}
		mCellElement.setTableNumberColumnsRepeatedAttribute(new Integer(repeatedNum));
	}

	/**
	 * Set the row spanned number.
	 * 
	 * @param spannedNum	row spanned number that need to be set 
	 *            the row spanned number that need to be set. If spannedNum is
	 *            less than 1, default row spanned number 1 will be
	 *            set.
	 */
	public void setRowSpannedNumber(int spannedNum) {
		if (spannedNum < 1) {
			spannedNum = DEFAULT_ROW_SPANNED_NUMBER;
		}
		doPrepare();
		if (mCellElement instanceof TableTableCellElement) {
			((TableTableCellElement) mCellElement).setTableNumberRowsSpannedAttribute(new Integer(spannedNum));
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Judge if the ODF DOM element of this cell is the covered cell element.
	 * 
	 * @return true if the ODFDOM element is TableCoveredTableCellElement
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
		OdfStyle style = getCellStyleElement();
		if (style == null) {
			return "";
		}
		return style.getStyleNameAttribute();
	}

	/** 
	 * Set a formula to the cell.
	 * <p>
	 * Please note, the parameter <code>formula</code> will not be checked and interpreted;
	 * the cell value will not be calculated.
	 * It's just simply set as a formula attribute. See {@odf.attribute table:formula}
	 * 
	 * @param formula	the formula that need to be set.
	 * If formula is null, an IllegalArgumentException will be thrown.
	 */
	public void setFormula(String formula) {
		if (formula == null) {
			throw new IllegalArgumentException("formula shouldn't be null.");
		} 
		doPrepare();
		mCellElement.setTableFormulaAttribute(formula);
	}

	/**
	 * Get the formula string of the cell.
	 * 
	 * @return the formula representation of the cell
	 * <p>
	 * If the cell does not contain a formula, null will be returned.
	 * 
	 */
	public String getFormula() {
		return mCellElement.getTableFormulaAttribute();
	}

//	/**
//	 * get the error value of the cell
//	 * if the formula can not be calculated, an error will be set
//	 * @return
//	 * 			return 0 if the cell has no error
//	 * 			return the error value of the cell if the formula result can not be calculated
//	 * 			such as divided by 0
//	 */
//	public long getError()
//	{
//		return 0;
//	}
	/**
	 * Set the currency symbol and overall format of a currency cell.
	 * <p>
	 * Please note the overall format includes the symbol character, for example: $#,##0.00.
	 * <p>
	 * This function only works for currency.
	 * @param currencySymbol	the currency symbol
	 * @param format			overall format
	 * If input currencySymbol or format is null, an IllegalArgumentException will be thrown.
	 */
	public void setCurrencyFormat(String currencySymbol, String format) {
		if ( currencySymbol == null ){
			throw new IllegalArgumentException(
					"currencySymbol shouldn't be null.");
		} 
		if ( format == null ){
			throw new IllegalArgumentException("format shouldn't be null.");
		} 
		doPrepare();
		String type = mCellElement.getOfficeValueTypeAttribute();
		OfficeValueTypeAttribute.Value typeValue = null;
		msFormatString = format;
		if (type != null) {
			typeValue = OfficeValueTypeAttribute.Value.enumValueOf(type);
		}

		if (typeValue != OfficeValueTypeAttribute.Value.CURRENCY) {
			throw new IllegalArgumentException();
		}

		OdfNumberCurrencyStyle currencyStyle = new OdfNumberCurrencyStyle(
				(OdfFileDom) mCellElement.getOwnerDocument(),
				currencySymbol,
				format,
				getUniqueCurrencyStyleName());
		mCellElement.getAutomaticStyles().appendChild(currencyStyle);
		setDataDisplayStyleName(currencyStyle.getStyleNameAttribute());
		Double value = getCurrencyValue();

		//set display text
		if (value != null) {
			setDisplayText(formatCurrency(currencyStyle, value.doubleValue()));
		}
	}

	//This method doesn't handle style:map element.
	private String formatCurrency(OdfNumberCurrencyStyle currencyStyle, double value) {
		String valuestr = "";
		for (Node m : new DomNodeList(currencyStyle.getChildNodes())) {
			if (m instanceof OdfNumberCurrencySymbol) {
				valuestr += m.getTextContent();
			} else if (m instanceof OdfNumber) {
				String numberformat = currencyStyle.getNumberFormat();
				valuestr += (new DecimalFormat(numberformat)).format(value);
			} else if (m instanceof OdfNumberText) {
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
	 * This function only works for float, date and percentage, otherwise an
	 * {@link java.lang.IllegalArgumentException} will be thrown.

	 * @param formatStr	the cell need be formatted as this specified format string
	 * If input formatStr is null, an IllegalArgumentException will be thrown.
	 */
	public void setFormatString(String formatStr) {
		if (formatStr == null) {
			throw new IllegalArgumentException("formatStr shouldn't be null.");
		}
		doPrepare();
		String type = mCellElement.getOfficeValueTypeAttribute();
		OfficeValueTypeAttribute.Value typeValue = null;
		msFormatString = formatStr;
		if (type != null) {
			typeValue = OfficeValueTypeAttribute.Value.enumValueOf(type);
		}

		if (typeValue == OfficeValueTypeAttribute.Value.FLOAT) {
			OdfNumberStyle numberStyle = new OdfNumberStyle(
					(OdfFileDom) mCellElement.getOwnerDocument(),
					formatStr,
					getUniqueNumberStyleName());
			mCellElement.getAutomaticStyles().appendChild(numberStyle);
			setDataDisplayStyleName(numberStyle.getStyleNameAttribute());
			Double value = getDoubleValue();
			if (value != null) {
				setDisplayText((new DecimalFormat(formatStr)).format(value.doubleValue()));
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.DATE) {
			OdfNumberDateStyle dateStyle = new OdfNumberDateStyle(
					(OdfFileDom) mCellElement.getOwnerDocument(),
					formatStr,
					getUniqueDateStyleName(), null);
			mCellElement.getAutomaticStyles().appendChild(dateStyle);
			setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
			String dateStr = mCellElement.getOfficeDateValueAttribute();
			if (dateStr != null) {
				Calendar date = getDateValue();
				setDisplayText((new SimpleDateFormat(formatStr)).format(date.getTime()));
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.TIME) {
			OdfNumberTimeStyle timeStyle = new OdfNumberTimeStyle(
					(OdfFileDom) mCellElement.getOwnerDocument(), formatStr,
					getUniqueDateStyleName());
			mCellElement.getAutomaticStyles().appendChild(timeStyle);
			setDataDisplayStyleName(timeStyle.getStyleNameAttribute());
			String timeStr = mCellElement.getOfficeTimeValueAttribute();
			if (timeStr != null) {
				Calendar time = getTimeValue();
				setDisplayText((new SimpleDateFormat(formatStr)).format(time
						.getTime()));
			} 
		} else if (typeValue == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			OdfNumberPercentageStyle dateStyle = new OdfNumberPercentageStyle(
					(OdfFileDom) mCellElement.getOwnerDocument(),
					formatStr,
					getUniquePercentageStyleName());
			mCellElement.getAutomaticStyles().appendChild(dateStyle);
			setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
			Double value = getPercentageValue();
			if (value != null) {
				setDisplayText((new DecimalFormat(formatStr)).format(value.doubleValue()));
			}
		} else {
			throw new IllegalArgumentException("This function doesn't support " + typeValue + " cell.");
		}
	}

	private void setDataDisplayStyleName(String name) {
		OdfStyleBase styleElement = getCellStyleElementForWrite();
		if (styleElement != null) {
			styleElement.setOdfAttributeValue(OdfName.newName(OdfNamespaceNames.STYLE, "data-style-name"), name);
		}
	}

	private OdfStyle getCellStyleElement() {
		String styleName = mCellElement.getStyleName();
		if (styleName == null || (styleName.equals(""))) {	//search in row
			OdfTableRow aRow = getTableRow();
			styleName = aRow.getOdfElement().getTableDefaultCellStyleNameAttribute();
		}
		if (styleName == null || (styleName.equals(""))) {	//search in column
			OdfTableColumn aColumn = getTableColumn();
			styleName = aColumn.getOdfElement().getTableDefaultCellStyleNameAttribute();
		}
		if (styleName == null || (styleName.equals(""))) {
			return null;
		}

		OdfStyle styleElement = mCellElement.getAutomaticStyles().getStyle(styleName, mCellElement.getStyleFamily());

		if (styleElement == null) {
			styleElement = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getStyle(styleName,
					OdfStyleFamily.TableCell);
		}
		
		if (styleElement == null) {
			styleElement = mCellElement.getDocumentStyle();
		}

		if (styleElement == null) {
			OdfStyle newStyle = mCellElement.getAutomaticStyles().newStyle(
					OdfStyleFamily.TableCell);
			String newname = newStyle.getStyleNameAttribute();
			mCellElement.setStyleName(newname);
			newStyle.addStyleUser(mCellElement);
			return newStyle;
		}

		return styleElement;
	}

	private OdfStyle getCellStyleElementForWrite() {
		boolean copy = false;
		String styleName = mCellElement.getStyleName();
		if (styleName == null || (styleName.equals(""))) {	//search in row
			OdfTableRow aRow = getTableRow();
			styleName = aRow.getOdfElement().getTableDefaultCellStyleNameAttribute();
			copy = true;
		}
		if (styleName == null || (styleName.equals(""))) {	//search in column
			OdfTableColumn aColumn = getTableColumn();
			styleName = aColumn.getOdfElement().getTableDefaultCellStyleNameAttribute();
			copy = true;
		}
		if (styleName == null || (styleName.equals(""))) {
			return null;
		}

		OdfStyle styleElement = mCellElement.getAutomaticStyles().getStyle(styleName, mCellElement.getStyleFamily());

		if (styleElement == null) {
			styleElement = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().
					getDocumentStyles().getStyle(styleName, OdfStyleFamily.TableCell);
		}

		if (styleElement == null) {
			styleElement = mCellElement.getDocumentStyle();
		}

		if (styleElement.getStyleUserCount() > 1 || copy) //if this style are used by many users,
		//should create a new one.
		{
			OdfStyle newStyle = mCellElement.getAutomaticStyles().newStyle(OdfStyleFamily.TableCell);
			newStyle.setProperties(styleElement.getStylePropertiesDeep());
			String newname = newStyle.getStyleNameAttribute();
			mCellElement.setStyleName(newname);
			return newStyle;
		}
		return styleElement;
	}

	private String getDataDisplayStyleName() {
		String datadisplayStylename = null;
		OdfStyleBase styleElement = getCellStyleElement();
		if (styleElement != null) {
			datadisplayStylename = styleElement.getOdfAttributeValue(OdfName.newName(OdfNamespaceNames.STYLE, "data-style-name"));
		}

		return datadisplayStylename;
	}

	private String getUniqueNumberStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getNumberStyle(unique_name) != null);
		return unique_name;
	}

	private String getUniqueDateStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getDateStyle(unique_name) != null);
		return unique_name;
	}

	private String getUniquePercentageStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
		} while (styles.getPercentageStyle(unique_name) != null);
		return unique_name;
	}

//    private String getUniqueCellStyleName() {
//    	String unique_name;
//		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
//	    do {
//			unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
//		} while (styles.getStyle(unique_name, OdfStyleFamily.TableCell) != null);
//    	return unique_name;
//    }	
	private String getUniqueCurrencyStyleName() {
		String unique_name;
		OdfOfficeAutomaticStyles styles = mCellElement.getAutomaticStyles();
		do {
			unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
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
				style = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getNumberStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.DATE) {
			String name = getDataDisplayStyleName();
			OdfNumberDateStyle style = mCellElement.getAutomaticStyles().getDateStyle(name);
			if (style == null) {
				style = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getDateStyle(name);
			}
			if (style != null) {
				return style.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.CURRENCY) {
			String name = getCurrencyDisplayStyleName();
			OdfNumberCurrencyStyle dataStyle = mCellElement.getAutomaticStyles().getCurrencyStyle(name);
			if (dataStyle == null) {
				dataStyle = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getCurrencyStyle(name);
			}
			if (dataStyle != null) {
				return dataStyle.getFormat();
			}
		} else if (typeValue == OfficeValueTypeAttribute.Value.PERCENTAGE) {
			String name = getDataDisplayStyleName();
			OdfNumberPercentageStyle style = mCellElement.getAutomaticStyles().getPercentageStyle(name);
			if (style == null) {
				style = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getPercentageStyle(name);
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
			dataStyle = ((OdfFileDom) mCellElement.getOwnerDocument()).getOdfDocument().getDocumentStyles().getCurrencyStyle(name);
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
		doPrepare();
		//delete text:p child element
		Node node = mCellElement.getFirstChild();
		while (node != null) {
			Node nextNode = node.getNextSibling();
			if (node instanceof TextPElement
					|| node instanceof TextHElement
					|| node instanceof TextListElement) {
				mCellElement.removeChild(node);
			}
			node = nextNode;
		}
	}

	/**
	 * Remove all the content of the cell.
	 */
	public void removeContent() {
		doPrepare();
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
	 * @param fromCell	another cell whose content will be appended to this cell
	 */
	void appendContentFrom(OdfTableCell fromCell) {
		doPrepare();
		OdfWhitespaceProcessor textProcess = new OdfWhitespaceProcessor();
		TableTableCellElementBase cell = fromCell.getOdfElement();
		Node node = cell.getFirstChild();
		while (node != null) {
			if (node instanceof OdfTextParagraph) {
				if (!textProcess.getText(node).equals("")) {
					mCellElement.appendChild(node.cloneNode(true));
				}
			} else {
				mCellElement.appendChild(node.cloneNode(true));
			}
			node = node.getNextSibling();
		}
	}

	/*****************************************
	 * Moved from OdfTable
	 * 
	 *******************************************/
	/**
	 * This method is invoked by insertCellBefore and insertRowBefore
	 * When it is needed to clone a cell and the cell is a cover cell,
	 * for some instance, we need to find the cell who covers this cell.
	 * So this method is to get the cell who covers this cell
	 */
	OdfTableCell getCoverCell() {
		int startRowi = getRowIndex();
		int startColumni = getColumnIndex();

		for (int i = startRowi; i >= 0; i--) {
			OdfTableRow aRow = mOwnerTable.getRowByIndex(i);
			for (int j = startColumni; j >= 0; j--) {
				if (i == startRowi && j == startColumni) {
					continue;
				}
				OdfTableCell cell = aRow.getCellByIndex(j);
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
	 * This method is invoked by getCoverCell.
	 * It's to get the cell in a same row who covers this cell.
	 * 
	 * @return the cell in a same row who covers this cell
	 * <p>
	 *         Null if there is no cell who covers this cell
	 */
	OdfTableCell getCoverCellInSameRow() {
		int startRowi = getRowIndex();
		int startColumni = getColumnIndex();

		for (int j = startColumni - 1; j >= 0; j--) {
			OdfTableCell cell = mOwnerTable.getCellByPosition(j, startRowi);
			if (cell.getOdfElement() instanceof TableCoveredTableCellElement) {
				continue;
			}

			int oldSpanN = cell.getColumnSpannedNumber();
			if (oldSpanN + j > startColumni) {
				//cell.setColumnSpannedNumber(oldSpanN-1);
				return cell;
			}
			return null;
		}
		return null;
	}

	/**
	 * This method is invoked by getCoverCell
	 */
	OdfTableCell getCoverCellInSameColumn() {
		int startRowi = getRowIndex();
		int startColumni = getColumnIndex();

		for (int i = startRowi - 1; i >= 0; i--) {
			OdfTableCell cell = mOwnerTable.getCellByPosition(startColumni, i);
			if (cell.getOdfElement() instanceof TableCoveredTableCellElement) {
				continue;
			}

			int oldSpanN = cell.getRowSpannedNumber();
			if (oldSpanN + i > startRowi) {
				//cell.setRowSpannedNumber(oldSpanN-1);
				return cell;
			}
			return null;
		}
		return null;
	}
}
