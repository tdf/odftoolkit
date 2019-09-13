/*
 * Copyright 2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.odfdom.changes;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

/**
 * A MultiCoomponent uses a single XML element to represent multiple components.
 * This container can be used for spreadsheet row and cell components using
 * repeated elements via an attribute.
 *
 * @author svante.schubertATgmail.com
 */
public class Row<T> extends Component {

    public Row(OdfElement componentElement, Component parent) {
        super(componentElement, parent);
    }

    /**
     * A multiple components can be represented by a single XML element
     *
     * @return the number of components the elements represents
     */
    @Override
    public int repetition() {
        return mRootElement.getRepetition();
    }

// CELL ONLY
//	Map<String, Object> mInnerCellStyle = null;
//
//	/** The inner style of a cell will be temporary saved at the cell.
//	 Whenever the cell content is deleted, the style is being merged/applied to the cell style */
//	public Map<String, Object> getInternalCellStyle(){
//		return mInnerCellStyle;
//	}
//
//
//	/** The inner style of a cell will be temporary saved at the cell.
//	 Whenever the cell content is deleted, the style is being merged/applied to the cell style */
//	public void setInternalCellStyle(Map<String, Object> newStyles){
//		mInnerCellStyle = newStyles;
//	}
//
    /**
     * Adds the given component to the root element
     */
    @Override
    public void addChild(int index, Component c) {
        mRootElement.insert(c.getRootElement(), index);
// 2DO: Svante: ARE THE ABOVE AND THE BELOW EQUIVALENT?
//		OdfElement rootElement = c.getRootElement();
//		if (index >= 0) {
//			mRootElement.insertBefore(rootElement, ((OdfElement) mRootElement).receiveNode(index));
//		} else {
//			mRootElement.appendChild(rootElement);
//		}
    }

    /**
     * @return either a text node of size 1 or an element being the root element
     * of a component
     */
    @Override
    public Node getChildNode(int index) {
        return ((OdfElement) mRootElement).receiveNode(index);

    }

    /**
     * Removes a component from the text element container. Removes either an
     * element representing a component or text node of size 1
     */
    @Override
    public Node remove(int index) {
        Node node = (Node) this.getChildNode(index);
        return mRootElement.removeChild(node);
    }

    /**
     * All children of the root element will be traversed. If it is a text node
     * the size is added, if it is an element and a component a size of one is
     * added, if it is a marker, for known text marker elements (text:span,
     * text:bookmark) the children are recursive checked
     *
     * @return the number of child components
     */
    @Override
    public int size() {
        return mRootElement.countDescendantComponents();
    }
//	DELETE ME!!
//	/**
//	 * Inserts a component at the given position as child
//	 *
//	 * @param position of the component, a -1 is going to append the element
//	 */
//	public TableCellComponent createChildComponent(int position) {
//		OdfTableRow tableRow = OdfTableRow.getInstance((TableTableRowElement) mRootElement);
//		OdfElement cellElement = tableRow.getCellByIndex(position).getOdfElement();
//		if(position > -1 && cellElement == null){
//			// create the missing element and all its precessors
//			System.out.println("yeah!");
//		}
//		TableCellComponent c = (TableCellComponent) cellElement.getComponent();
//		if(c == null){
//			(TableCellComponent) Component.createChildComponent(position, this, cellElement);
//		}
//		return c;
//	}
}
