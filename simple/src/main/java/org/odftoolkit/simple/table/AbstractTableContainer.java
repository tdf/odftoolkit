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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.w3c.dom.NodeList;

/**
 * AbstractTableContainer is an abstract implementation of the TableContainer
 * interface, with a default implementation for every method defined in
 * TableContainer , except getTableContainerElement(). Each subclass must
 * implement the abstract method getTableContainerElement().
 * 
 * @since 0.4.5
 */
public abstract class AbstractTableContainer implements TableContainer {

	private final TableBuilder tableBuilder;

	protected AbstractTableContainer() {
		tableBuilder = new Table.TableBuilder(this);
	}
	
	/**
	 * Add a new Table to this container.
	 * 
	 * @return added table.
	 */
	public Table addTable() {
		return Table.newTable(this);
	}
	
	/**
	 * Add a new Table to this container with a specified row number and column
	 * number.
	 * <p>
	 * The table will be inserted at the end of the tableContainer. An unique
	 * table name will be given, you may set a custom table name using the
	 * <code>setTableName</code> method.
	 * 
	 * @param numRows
	 *            the row number
	 * @param numCols
	 *            the column number
	 * @return a new instance of <code>Table</code>
	 */
	public Table addTable(int numRows, int numCols) {
		return Table.newTable(this, numRows, numCols);
	}
	
	/**
	 * Return an instance of table feature with the specific table name.
	 * 
	 * @param name
	 *            of the table beeing searched for.
	 * @return an instance of table feature with the specific table name.
	 */
	public Table getTableByName(String name) {
		try {
			OdfElement containerEle = getTableContainerElement();
			NodeList nodeList = containerEle.getElementsByTagName(TableTableElement.ELEMENT_NAME.getQName());
			for (int i = 0; i < nodeList.getLength(); i++) {
				TableTableElement table = (TableTableElement) nodeList.item(i);
				if (table.getOdfAttributeValue(OdfName.newName(OdfDocumentNamespace.TABLE, "name")).equals(name)) {
					return getTableBuilder().getTableInstance(table);
				}
			}
		} catch (Exception e) {
			Logger.getLogger(AbstractTableContainer.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Return a list of table features in this container.
	 * 
	 * @return a list of table features in this container.
	 */
	public List<Table> getTableList() {
		List<Table> tableList = new ArrayList<Table>();
		try {
			OdfElement containerEle = getTableContainerElement();
			NodeList nodeList = containerEle.getElementsByTagName(TableTableElement.ELEMENT_NAME.getQName());
			for (int i = 0; i < nodeList.getLength(); i++) {
				tableList.add(getTableBuilder().getTableInstance((TableTableElement) nodeList.item(i)));
			}
		} catch (Exception e) {
			Logger.getLogger(AbstractTableContainer.class.getName()).log(Level.SEVERE, null, e);
		}
		return tableList;
	}

	/**
	 * Return the table builder of this container. Every container has a table
	 * builder, which supplies all of the table creation realization, for
	 * example newTable().
	 * 
	 * @return the table builder of this container.
	 * @since 0.3.5
	 */
	public TableBuilder getTableBuilder() {
		return tableBuilder;
	}
}
