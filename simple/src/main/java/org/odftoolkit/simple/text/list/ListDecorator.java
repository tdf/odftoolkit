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

package org.odftoolkit.simple.text.list;

/**
 * ListDecorator is a decorator which decides how to decorate a List and its
 * ListItems.
 * <p>
 * Every list, including sub lists, may have a list style which is applied to
 * its list items and sub lists. ListDecorator holds this style and decides the
 * appearance of a list. List style is applied by invoking decorateList(List),
 * while list item style is applied by invoking decorateListItem(ListItem).
 * <p>
 * A ListDecorator specified for a sub list overrides the ListDecorator
 * specified for the list in which the sub list is contained.
 * 
 * @since 0.4
 */
public interface ListDecorator {

	/**
	 * The supported list types till now.
	 * 
	 * @since 0.4
	 */
	public static enum ListType {
		/**
		 * BULLET specifies a list type where list items are preceded by
		 * bullets.
		 */
		BULLET,

		/**
		 * NUMBER specifies a list type where list items are preceded by
		 * numbers.
		 */
		NUMBER,

		/**
		 * IMAGE specifies a list type where list items are preceded by images.
		 */
		IMAGE
	}

	/**
	 * Decorate the specifies <code>list</code>, of which style is set.
	 * 
	 * @param list
	 *            the List is decorated.
	 */
	public void decorateList(List list);

	/**
	 * Decorate the specifies <code>item</code> in a List, of which style is
	 * set.
	 * 
	 * @param item
	 *            the ListItem is decorated.
	 */
	public void decorateListItem(ListItem item);

	/**
	 * Get the ListType of this ListDecorator.
	 * 
	 * @return the ListType of this ListDecorator.
	 */
	public ListType getListType();
}
