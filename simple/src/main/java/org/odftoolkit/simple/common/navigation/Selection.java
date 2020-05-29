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

import java.util.Hashtable;
import java.util.Vector;
import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * <code>Selection</code> describes one of the matched results, which is
 * recognized by the container element, the start index of the text content in
 * this element and the text content.
 */
public abstract class Selection {

	private OdfElement mElement;
	private int mIndex;
	protected Navigation search;
	public Navigation getNavigation() {
		return search;
	}

	/**
	 * Get the container element of this <code>Selection</code>.
	 *
	 * @return the container element
	 */
	public OdfElement getElement() {
		return mElement;
	}

	/**
	 * Get the start index of the text content in the container element. This is
	 * only meaningful for {@link TextSelection TextSelection} and its sub
	 * classes, other type of <code>Selection</code> will return 0.
	 *
	 * @return the start index of the container element
	 */
	public int getIndex() {
		return mIndex;
	}

	/**
	 * Cut current <code>Selection</code>.
	 *
	 * @throws InvalidNavigationException
	 */
	public abstract void cut() throws InvalidNavigationException;

	/**
	 * Paste current <code>Selection</code> at front of the specified position
	 * <code>Selection</code>.
	 *
	 * @param positionItem
	 *            the position <code>Selection</code>
	 * @throws InvalidNavigationException
	 */
	public abstract void pasteAtFrontOf(Selection positionItem) throws InvalidNavigationException;

	/**
	 * Paste current <code>Selection</code> at end of the specified position
	 * <code>Selection</code>.
	 *
	 * @param positionItem
	 *            the position <code>Selection</code>
	 * @throws InvalidNavigationException
	 */
	public abstract void pasteAtEndOf(Selection positionItem) throws InvalidNavigationException;

	/**
	 * When a selected item has been deleted, the <code>Selection</code>s after
	 * this deleted <code>Selection</code> should be refreshed, as these
	 * <code>Selection</code>s index have been changed.
	 *
	 * @param deletedItem
	 *            the deleted <code>Selection</code>
	 */
	protected abstract void refreshAfterFrontalDelete(Selection deletedItem);

	/**
	 * When a selected item has been inserted, the <code>Selection</code> after
	 * the inserted item should be refresh, as these <code>Selection</code>s
	 * index have been changed.
	 *
	 * @param insertedItem
	 *            the inserted <code>Selection</code>
	 */
	protected abstract void refreshAfterFrontalInsert(Selection insertedItem);

	/**
	 * A quick method to update the index of this <code>Selection</code>.
	 *
	 * @param offset
	 *            the offset that the index should be added.
	 */
	protected abstract void refresh(int offset);


}
