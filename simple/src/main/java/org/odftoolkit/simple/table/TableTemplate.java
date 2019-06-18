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

import java.util.HashMap;

import org.odftoolkit.odfdom.dom.element.table.TableBodyElement;
import org.odftoolkit.odfdom.dom.element.table.TableEvenColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableEvenRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableFirstColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableFirstRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableLastColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableLastRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableOddColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableOddRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableTemplateElement;
import org.w3c.dom.NodeList;

/**
 * TableTemplate represents the table template feature in ODF documents.
 * <p>
 * Besides the seven types of style (first row, first column, last row, last
 * column, even/odd rows,even/odd columns and body) defined by
 * <code>table:table-template</code> in ODF 1.2, TableTemplate provide extension
 * mechnism, which allows user to specify style value to additional cells. For
 * example, in ODF 1.2 the four coner cells can only inherit styles from the
 * colum or row they are existing, but through TableTemplate.ExtendedStyleType,
 * they can reference any style defined in the document.
 * <p>
 * TableTemplate provide method to get/set a set of references to table cell
 * styles that specify the formatting to be used on a table.
 *
 */
public class TableTemplate {

	private TableTableTemplateElement mElement;

	/**
	 * Extended style types supported by table template.
	 *
	 */
	public static enum ExtendedStyleType {
		FIRSTROWSTARTCOLUM, FIRSTROWENDCOLUMN, LASTROWSTARTCOLUMN, LASTROWENDCOLUMN
	}

	HashMap<ExtendedStyleType, String> extendedTableStyleMap;
	HashMap<ExtendedStyleType, String> extendedParagraphStyleMap;

	/**
	 * Create an instance of TableTemplate
	 */
	protected TableTemplate() {
	}

	/**
	 * Create an instance of TableTemplate from an element
	 * <table:table-template>
	 *
	 * @param tableTableTemplate
	 *            - the element of table:table-template
	 */
	public TableTemplate(TableTableTemplateElement tableTableTemplate) {
		mElement = tableTableTemplate;
	}

	/**
	 * Return the name of table template.
	 * <p>
	 * Null will be returned if there is no table template name setting.
	 *
	 * @return the table template name.
	 */
	public String getTableName() {
		return mElement.getTableNameAttribute();
	}

	/**
	 * Set the name of table template.
	 * <p>
	 * If the parameter <code>tableNameValue</code> is null, the table template
	 * name definition will be removed.
	 *
	 * @param tableNameValue
	 *            - the table template name
	 */
	public void setTableName(String tableNameValue) {
		mElement.setTableNameAttribute(tableNameValue);
	}

	/**
	 * get the value of table style specified by type.
	 * <p>
	 * Null will be returned if there is no such extended style type setting.
	 *
	 * @param type
	 *            - style type
	 * @return the style name referenced by this style type
	 */
	public String getExtendedTableStyleByType(ExtendedStyleType type) {
		if (extendedTableStyleMap == null)
			return null;
		return extendedTableStyleMap.get(type);
	}

	/**
	 * get the value of paragraph style specified by type.
	 * <p>
	 * Null will be returned if there is no such extended style type setting.
	 *
	 * @param type
	 *            - extended style type
	 * @return the style name referenced by this style type
	 */
	public String getExtendedParagraphStyleByType(ExtendedStyleType type) {
		if (extendedParagraphStyleMap == null)
			return null;
		return extendedParagraphStyleMap.get(type);
	}

	/**
	 * set the value of table style and paragraph style specified by type.
	 *
	 * @param type
	 *            - extended style type
	 * @param tableStyle
	 *            - table style name
	 * @param paraStyle
	 *            - paragraph style name
	 */
	public void setExtendedStyleByType(ExtendedStyleType type,
			String tableStyle, String paraStyle) {

		if (extendedTableStyleMap == null) {
			extendedTableStyleMap = new HashMap<ExtendedStyleType, String>(5);
		}
		if (extendedParagraphStyleMap == null) {
			extendedParagraphStyleMap = new HashMap<ExtendedStyleType, String>(
					5);
		}
		extendedTableStyleMap.put(type, tableStyle);
		extendedParagraphStyleMap.put(type, paraStyle);
	}

	/**
	 * get the value of table style of body.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of body.
	 */
	public String getTableBodyTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableBodyElement.ELEMENT_NAME.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableBodyElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of body.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of body.
	 */
	public String getTableBodyParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableBodyElement.ELEMENT_NAME.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableBodyElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of even columns.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of even columns.
	 */
	public String getTableEvenColumnsTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableEvenColumnsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableEvenColumnsElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of even columns.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of even columns.
	 */
	public String getTableEvenColumnsParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableEvenColumnsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableEvenColumnsElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of even rows.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of even rows.
	 */
	public String getTableEvenRowsTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableEvenRowsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableEvenRowsElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of even rows.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of even rows.
	 */
	public String getTableEvenRowsParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableEvenRowsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableEvenRowsElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of first colum.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of first column.
	 */
	public String getTableFirstColumnTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableFirstColumnElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableFirstColumnElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of first column.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of first column.
	 */
	public String getTableFirstColumnParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableFirstColumnElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableFirstColumnElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of first row.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of first row.
	 */
	public String getTableFirstRowTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableFirstRowElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableFirstRowElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of first row.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of first row.
	 */
	public String getTableFirstRowParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableFirstRowElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableFirstRowElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of last column.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of last column.
	 */
	public String getTableLastColumnTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableLastColumnElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableLastColumnElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of last column.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of last column.
	 */
	public String getTableLastColumnParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableLastColumnElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableLastColumnElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of last row.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of last row.
	 */
	public String getTableLastRowTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableLastRowElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableLastRowElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of last row.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of last row.
	 */
	public String getTableLastRowParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableLastRowElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableLastRowElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of odd columns.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of odd columns.
	 */
	public String getTableOddColumnsTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableOddColumnsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableOddColumnsElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of odd columns.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of odd columns.
	 */
	public String getTableOddColumnsParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableOddColumnsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableOddColumnsElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of table style of odd rows.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by table style of odd rows.
	 */
	public String getTableOddRowsTableStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableOddRowsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableOddRowsElement) elements.item(0))
					.getTableStyleNameAttribute();
		}
		return null;
	}

	/**
	 * get the value of paragraph style of odd rows.
	 * <p>
	 * Null will be returned if there is no such style setting.
	 *
	 * @return the style name referenced by paragraph style of odd rows.
	 */
	public String getTableOddRowsParagraphStyle() {
		NodeList elements = mElement
				.getElementsByTagName(TableOddRowsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			return ((TableOddRowsElement) elements.item(0))
					.getTableParagraphStyleNameAttribute();
		}
		return null;
	}

	/**
	 * Set the value of table style and paragraph style of body.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in body will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in body will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by body
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by body
	 */
	public void setTableBodyStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableBodyElement.ELEMENT_NAME.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableBodyElement ele = (TableBodyElement) elements.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableBodyElement ele = mElement
					.newTableBodyElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of even columns.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in even columns will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in even columns will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by even columns
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by even columns
	 */
	public void setTableEvenColumnsStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableEvenColumnsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableEvenColumnsElement ele = (TableEvenColumnsElement) elements
					.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableEvenColumnsElement ele = mElement
					.newTableEvenColumnsElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of even rows.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in even rows will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in even rows will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by even rows
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by even rows
	 */
	public void setTableEvenRowsStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableEvenRowsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableEvenRowsElement ele = (TableEvenRowsElement) elements.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableEvenRowsElement ele = mElement
					.newTableEvenRowsElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of first column.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in first column will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in first column will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by first column
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by first column
	 */
	public void setTableFirstColumnStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableFirstColumnElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableFirstColumnElement ele = (TableFirstColumnElement) elements
					.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableFirstColumnElement ele = mElement
					.newTableFirstColumnElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of first row.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in first row will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in first row will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by first row
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by first row
	 */
	public void setTableFirstRowStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableFirstRowElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableFirstRowElement ele = (TableFirstRowElement) elements.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableFirstRowElement ele = mElement
					.newTableFirstRowElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of last column.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in last column will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in last column will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by last column
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by last column
	 */
	public void setTableLastColumnStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableLastColumnElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableLastColumnElement ele = (TableLastColumnElement) elements
					.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableLastColumnElement ele = mElement
					.newTableLastColumnElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of last row.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in last row will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in last row will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by last row
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by last row
	 */
	public void setTableLastRowStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableLastRowElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableLastRowElement ele = (TableLastRowElement) elements.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableLastRowElement ele = mElement
					.newTableLastRowElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of odd columns.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in odd columns will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in odd columns will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by odd columns
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by odd columns
	 */
	public void setTableOddColumnsStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableOddColumnsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableOddColumnsElement ele = (TableOddColumnsElement) elements
					.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableOddColumnsElement ele = mElement
					.newTableOddColumnsElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

	/**
	 * Set the value of table style and paragraph style of odd rows.
	 * <p>
	 * If the parameter <code>tableStyleNameValue</code> is null, the table
	 * style name definition in odd rows will be removed. If the parameter
	 * <code>tableParagraphStyleNameValue</code> is null, the paragraph style
	 * name definition in odd rows will be removed.
	 *
	 * @param tableStyleNameValue
	 *            - table style name referenced by odd rows
	 * @param tableParagraphStyleNameValue
	 *            - paragraph style name referenced by odd rows
	 */
	public void setTableOddRowsStyle(String tableStyleNameValue,
			String tableParagraphStyleNameValue) {
		NodeList elements = mElement
				.getElementsByTagName(TableOddRowsElement.ELEMENT_NAME
						.getQName());
		if (elements != null && elements.getLength() > 0) {
			TableOddRowsElement ele = (TableOddRowsElement) elements.item(0);
			ele.setTableStyleNameAttribute(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		} else {
			TableOddRowsElement ele = mElement
					.newTableOddRowsElement(tableStyleNameValue);
			ele
					.setTableParagraphStyleNameAttribute(tableParagraphStyleNameValue);
		}
	}

}
