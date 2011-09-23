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

import java.util.List;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.table.Table.TableBuilder;

/**
 * TableContainer is a container which maintains Table(s) as element(s).
 * Table(s) can be added, removed and iterated in this container.
 * 
 * @see Table
 * @see org.odftoolkit.simple.TextDocument
 * @see org.odftoolkit.simple.SpreadsheetDocument
 * 
 * @since 0.4.5
 */
public interface TableContainer {

	/**
	 * Get the ODF element which can have
	 * <table:table>
	 * as child element directly according to ODF specification. This Element
	 * will help to find the position to insert a new Table. For example,
	 * <table:table>
	 * element is usable with <office:text> element, so TextDocument will return
	 * OfficeTextElement. While Presentation Notes is an indirectly
	 * TableContainer, which holds Table with the help of its grand-child
	 * element <draw:text-box>, so for Notes, DrawTextBoxElement should be
	 * return.
	 * 
	 * @return container element which can hold <text:table>.
	 */
	public OdfElement getTableContainerElement();

	/**
	 * Add a new Table to this container.
	 * 
	 * @return added table.
	 */
	public Table addTable();

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
	public Table addTable(int numRows, int numCols);

	/**
	 * Return an instance of table feature with the specific table name.
	 * 
	 * @param name
	 *            of the table being searched for.
	 * @return an instance of table feature with the specific table name.
	 */
	public Table getTableByName(String name);

	/**
	 * Return a list of table features in this document.
	 * 
	 * @return a list of table features in this document.
	 */
	public List<Table> getTableList();

	/**
	 * Return the table builder of this document. Every document has a table
	 * builder, which supplies all of the table creation realization, for
	 * example newTable().
	 * 
	 * @return the table builder of this document.
	 */
	public TableBuilder getTableBuilder();
}
