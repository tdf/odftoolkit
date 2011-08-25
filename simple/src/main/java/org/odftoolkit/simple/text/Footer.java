/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.text;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.table.AbstractTableContainer;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.w3c.dom.NodeList;

/**
 * This class represents footer definition in text document. It provides methods
 * to manipulate footer in text document, such as, set text, add table.
 * 
 * @since 0.4.5
 */
public class Footer implements TableContainer {
	private StyleFooterElement footerEle;
	private TableContainerImpl tableContainerImpl;

	/**
	 * Create a footer instance by an object of <code>StyleFooterElement</code>.
	 * 
	 * @param element
	 *            - an object of <code>StyleFooterElement</code>
	 */
	public Footer(StyleFooterElement element) {
		footerEle = element;
	}
	
	/**
	 * Return an instance of <code>StyleFooterElement</code> which represents
	 * this feature.
	 * 
	 * @return an instance of <code>StyleFooterElement</code>
	 */
	public StyleFooterElement getOdfElement() {
		return footerEle;
	}
	
	public Table addTable() {
		Table table = getTableContainerImpl().addTable();
		updateTableToNone(table);
		return table;
	}
	
	public Table addTable(int numRows, int numCols) {
		Table table = getTableContainerImpl().addTable(numRows, numCols);
		updateTableToNone(table);
		return table;
	}
	
	public Table getTableByName(String name) {
		return getTableContainerImpl().getTableByName(name);
	}

	public java.util.List<Table> getTableList() {
		return getTableContainerImpl().getTableList();
	}

	public TableBuilder getTableBuilder() {
		return getTableContainerImpl().getTableBuilder();
	}

	public OdfElement getTableContainerElement() {
		return getTableContainerImpl().getTableContainerElement();
	}

	private TableContainer getTableContainerImpl() {
		if (tableContainerImpl == null) {
			tableContainerImpl = new TableContainerImpl();
		}
		return tableContainerImpl;
	}
	
	private void updateTableToNone(Table table) {
		OdfFileDom dom = (OdfFileDom) getTableContainerElement().getOwnerDocument();
		TableTableElement tableEle = table.getOdfElement();
		String stylename = tableEle.getStyleName();
		OdfOfficeAutomaticStyles styles = dom.getAutomaticStyles();
		OdfStyle tableStyle = styles.getStyle(stylename, OdfStyleFamily.Table);
		tableStyle.setProperty(StyleTablePropertiesElement.Shadow, "none");
		NodeList cells = tableEle.getElementsByTagNameNS(OdfDocumentNamespace.TABLE.getUri(), "table-cell");
		if (cells != null && cells.getLength() > 0) {
			OdfStyle cellStyleWithoutBorder = styles.newStyle(OdfStyleFamily.TableCell);
			cellStyleWithoutBorder.setProperty(StyleTableCellPropertiesElement.Border, "none");
			cellStyleWithoutBorder.setProperty(StyleTableCellPropertiesElement.Padding, "0.0382in");
			String cellStyleName = cellStyleWithoutBorder.getStyleNameAttribute();
			for (int i = 0; i < cells.getLength(); i++) {
				TableTableCellElement cell = (TableTableCellElement) cells.item(i);
				cell.setStyleName(cellStyleName);
			}
		}
	}
	
	private class TableContainerImpl extends AbstractTableContainer {

		public OdfElement getTableContainerElement() {
			return footerEle;
		}
	}
}
