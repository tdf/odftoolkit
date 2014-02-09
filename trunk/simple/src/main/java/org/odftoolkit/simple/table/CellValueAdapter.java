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

/**
 * This interface supplies a method which can adapt string content to a more
 * proper cell value type as need. This a very useful function in
 * {@link org.odftoolkit.simple.table.Cell#setDisplayText(String, CellValueAdapter)
 * Cell.setDisplayText} and table cell text replacement. For example, if a table
 * cell would be replaced with string "1234" in text navigation, the cell value
 * type can be adapted to "float" automatically with the help of
 * <code>CellValueAdapter</code>. </br>
 * Different adapter realizations have different adaptive rules.
 * 
 * @see org.odftoolkit.simple.table.Cell#setDisplayText
 * @see org.odftoolkit.simple.common.navigation.CellSelection
 * 
 * @since 0.3
 */
public interface CellValueAdapter {
	
	/**
	 * The default cell value adapter.
	 */
	public static final CellValueAdapter DEFAULT_VALUE_ADAPTER = new DefaultCellValueAdapter();
	
	/**
	 * Adapt string content to a more proper cell value type as need.
	 * 
	 * @param cell  the cell need to value adapt.
	 * @param value the value to be adapted.
	 */
	public void adaptValue(Cell cell, String value);
}
