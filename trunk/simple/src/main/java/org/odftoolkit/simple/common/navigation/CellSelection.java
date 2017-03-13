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

package org.odftoolkit.simple.common.navigation;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.CellValueAdapter;

/**
 * Based on <code>TextSelection</code>, <code>CellSelection</code> updates table
 * cell value and value type when the cell text is replaced by other content.
 * This is a more complete realization than TextSelection for cell content
 * replacement, which keeps the synchronization among cell value, value type and
 * display text.
 * 
 * @see TextSelection
 * @see org.odftoolkit.simple.table.CellValueAdapter
 * 
 * @since 0.3
 */
public class CellSelection extends TextSelection {

	private Cell mCell;

	/**
	 * Constructor of CellSelection.
	 * 
	 * @param text
	 *            the text content of this CellSelection
	 * @param containerElement
	 *            the paragraph element that contains this CellSelection
	 * @param index
	 *            the start index of the text content of the container element
	 * @param cell
	 *            the table cell which is selected
	 */
	CellSelection(Navigation search, String text, OdfElement containerElement,
			int index, Cell cell) {
		super(search, text, containerElement, index);
		mCell = cell;
	}


	/**
	 * Replace the text content of selection with a new string. The cell value
	 * and value type will be updated follow by the rules which are designed in
	 * the {@link org.odftoolkit.simple.table.CellValueAdapter
	 * <code>CellValueAdapter</code>}.
	 * 
	 * @param newText
	 *            the replace text String
	 * @param adapter
	 *            the <code>CellValueAdapter</code> used to adapt cell value and
	 *            value type
	 * @throws InvalidNavigationException
	 *            if the selection is unavailable.
	 * 
	 * @see #replaceWith(String)
	 * @see org.odftoolkit.simple.table.CellValueAdapter
	 */
	public void advancedReplaceWith(String newText, CellValueAdapter adapter) throws InvalidNavigationException {
		super.replaceWith(newText);
		if (mCell != null) {
			String text = mCell.getDisplayText();
			// update mCell value and value type.
			mCell.setDisplayText(text, adapter);
		}
	}

	/**
	 * Replace the text content of selection with a new string. The cell value
	 * and value type will be updated follow by the rules which are designed in
	 * the {@link org.odftoolkit.simple.table.DefaultCellValueAdapter
	 * <code>DefaultCellValueAdapter</code>}.
	 * 
	 * @param newText
	 *            the replace text String
	 * @throws InvalidNavigationException
	 *            if the selection is unavailable.
	 * 
	 * @see org.odftoolkit.simple.table.DefaultCellValueAdapter
	 */
	public void advancedReplaceWith(String newText) throws InvalidNavigationException {
		advancedReplaceWith(newText, CellValueAdapter.DEFAULT_VALUE_ADAPTER);
	}

	/**
	 * Get the selected table cell.
	 * 
	 * @return the selected table cell
	 */
	public Cell getCell() {
		return mCell;
	}
}
