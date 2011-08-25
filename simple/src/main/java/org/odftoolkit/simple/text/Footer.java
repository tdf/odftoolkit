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

import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.table.AbstractTableContainer;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.table.Table.TableBuilder;

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

	public Table addTable() {
		return getTableContainerImpl().addTable();
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

	protected TableContainer getTableContainerImpl() {
		if (tableContainerImpl == null) {
			tableContainerImpl = new TableContainerImpl();
		}
		return tableContainerImpl;
	}

	private class TableContainerImpl extends AbstractTableContainer {

		public OdfElement getTableContainerElement() {
			return footerEle;
		}
	}
}
