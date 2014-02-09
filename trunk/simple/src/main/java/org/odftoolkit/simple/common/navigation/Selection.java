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

	/**
	 * SelectionManager can manage all the <code>Selection</code>s that are
	 * returned to end users. This SelectionManager contains a repository of all
	 * <code>Selection</code>s, and will refresh the status/index of
	 * <code>Selection</code>s after certain operation.
	 */
	static class SelectionManager {

		private static Hashtable<OdfElement, Vector<Selection>> repository = new Hashtable<OdfElement, Vector<Selection>>();

		/**
		 * Register the <code>Selection</code> item.
		 * 
		 * @param item
		 *            the <code>Selection</code> item
		 */
		public static void registerItem(Selection item) {
			OdfElement element = item.getElement();
			if (repository.containsKey(element)) {
				Vector<Selection> selections = repository.get(element);
				int i = 0;
				while (i < selections.size()) {
					if (selections.get(i).getIndex() > item.getIndex()) {
						selections.insertElementAt(item, i);
						break;
					}
					i++;
				}
				if (i == selections.size()) {
					selections.add(item);
				}
			} else {
				Vector<Selection> al = new Vector<Selection>();
				al.add(item);
				repository.put(element, al);
			}
		}

		/**
		 * Refresh the <code>Selection</code>s in repository after a item is
		 * cut.
		 * 
		 * @param cutItem
		 *            the cut item
		 */
		public synchronized static void refreshAfterCut(Selection cutItem) {
			// travase the whole sub tree
			OdfElement element = cutItem.getElement();
			if (repository.containsKey(element)) {
				Vector<Selection> selections = repository.get(element);
				for (int i = 0; i < selections.size(); i++) {
					if (selections.get(i).getIndex() > cutItem.getIndex()) {
						selections.get(i).refreshAfterFrontalDelete(cutItem);
					}
				}
			}
		}

		/**
		 * Refresh the selections in repository after pastedAtFrontOf operation
		 * is called.
		 * 
		 * @param item
		 *            the pasted item
		 * @param positionItem
		 *            the position item
		 */
		public synchronized static void refreshAfterPasteAtFrontOf(Selection item, Selection positionItem) {
			// travase the whole sub tree
			OdfElement element = positionItem.getElement();
			if (repository.containsKey(element)) {
				Vector<Selection> selections = repository.get(element);
				for (int i = 0; i < selections.size(); i++) {
					if (selections.get(i).getIndex() >= positionItem.getIndex()) {
						selections.get(i).refreshAfterFrontalInsert(item);
					}
				}
			}
		}

		/**
		 * Refresh the <code>Selection</code>s in repository after pastedAtEndOf
		 * operation is called.
		 * 
		 * @param item
		 *            the pasted item
		 * @param positionItem
		 *            the position item
		 */
		public synchronized static void refreshAfterPasteAtEndOf(Selection item, Selection positionItem) {
			OdfElement element = positionItem.getElement();
			int positionIndex;
			if (positionItem instanceof TextSelection) {
				positionIndex = positionItem.getIndex() + ((TextSelection) positionItem).getText().length();
			} else {
				positionIndex = positionItem.getIndex();
			}
			if (repository.containsKey(element)) {
				Vector<Selection> selections = repository.get(element);
				for (int i = 0; i < selections.size(); i++) {
					if (selections.get(i).getIndex() >= positionIndex) {
						selections.get(i).refreshAfterFrontalInsert(item);
					}
				}
			}
		}

		/**
		 * Remove the <code>Selection</code> from repository.
		 * 
		 * @param item
		 *            <code>Selection</code> item
		 */
		public static void unregisterItem(Selection item) {
			OdfElement element = item.getElement();
			if (repository.containsKey(element)) {
				Vector<Selection> selections = repository.get(element);
				selections.remove(item);
			}
		}

		/**
		 * A direct method to update all the <code>Selection</code>s contained
		 * in a element after a certain position.
		 * 
		 * @param containerElement
		 *            the container element
		 * @param offset
		 *            the offset
		 * @param positionIndex
		 *            the index of a certain position
		 */
		public synchronized static void refresh(OdfElement containerElement, int offset, int positionIndex) {
			if (repository.containsKey(containerElement)) {
				Vector<Selection> selections = repository.get(containerElement);
				for (int i = 0; i < selections.size(); i++) {
					if (selections.get(i).getIndex() >= positionIndex) {
						selections.get(i).refresh(offset);
					}
				}
			}
		}

		private SelectionManager() {
		}
	}
}
