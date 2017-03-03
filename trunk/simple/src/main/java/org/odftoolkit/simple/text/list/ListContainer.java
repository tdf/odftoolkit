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

import java.util.Iterator;

import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * ListContainer is a container which maintains List(s) as element(s). List(s)
 * can be added, removed and iterated in this container.
 * <p>
 * All of the components which need to hold a List, must implement this
 * interface. For example, <text:list> element is under <office:text>
 * element according to ODF specification. So TextDocument is a type of
 * ListContainer which holds List directly. TextDocument must implement this
 * interface. While Presentation Notes is also a type of ListContainer, although
 * <presentation:notes> is not a element with which the <text:list> element is
 * usable. <presentation:notes> can have child element <draw:frame> and
 * <draw:frame> can have child element <draw:text-box>. <text:list> is usable
 * with the <draw:text-box> element. Notes is an indirectly ListContainer which
 * let user operate List easily.
 * 
 * @see List
 * @see org.odftoolkit.simple.TextDocument
 * @see org.odftoolkit.simple.presentation.Notes
 * 
 * @since 0.4
 */
public interface ListContainer {

	/**
	 * Get the ODF element which can have <text:list> as child
	 * element directly according to ODF specification. This Element will help to find the
	 * position to insert a new List. For example, <text:list> element is
	 * usable with <office:text> element, so TextDocument will return
	 * OfficeTextElement. While Presentation Notes is an indirectly
	 * ListContainer, which holds List with the help of its grand-child element
	 * <draw:text-box>, so for Notes, DrawTextBoxElement should be return.
	 * 
	 * @return container element which can hold <text:list>.
	 */
	public OdfElement getListContainerElement();

	/**
	 * Add a new List to this container.
	 * 
	 * @return added list.
	 */
	public List addList();

	/**
	 * Add a List with specified ListDecorator to this container.
	 * 
	 * @param decorator
	 *            the specified ListDecorator
	 * @return added list.
	 */
	public List addList(ListDecorator decorator);

	/**
	 * Remove the existing List from this container.
	 * 
	 * @return true, if the container contains this List.
	 */
	public boolean removeList(List list);

	/**
	 * Remove all Lists from this container.
	 */
	public void clearList();

	/**
	 * Return an Iterator of the Lists in this ListContainer. The Lists are
	 * iterated in the same order that they occur in the ListContainer.
	 * 
	 * @return an Iterator of the Lists in this ListContainer
	 * 
	 * @see java.util.Iterator
	 */
	public Iterator<List> getListIterator();
}
