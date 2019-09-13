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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.type.Length;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A MultiCoomponent uses a single XML element to represent multiple components.
 * This container can be used for spreadsheet row and cell components using
 * repeated elements via an attribute.
 *
 * @author svante.schubertATgmail.com
 */
public class Table<T> extends Component {

    public Table(OdfElement componentElement, Component parent) {
        super(componentElement, parent);
    }
    private static final Logger LOG = Logger.getLogger(Table.class.getName());
    /**
     * Used to indicate that the end position is not existing
     */
    public static final int ETERNITY = -1;
    /**
     * Used to indicate that the start position is before the table If a column
     * is inserted in the first place, there is no prior column to inherit
     * styles from.
     */
    static final int BEFORE_START = -1;

    /**
     * The maximal number of rows being generated. Unspecified by ODF but
     * commonly used. Counting starts with 0.
     */
    public static final Integer MAX_ROW_NUMBER = 1048576;

    /**
     * The maximal number of columns being generated. Unspecified by ODF but
     * commonly used. Counting starts with 0.
     */
    public static final Integer MAX_COLUMN_NUMBER_CALC = 1024; // 2^10 There is only a flag in Calc that was changed for having Excel Size.
    public static final Integer MAX_COLUMN_NUMBER_EXCEL = 16384; // 2^16

    /**
     * A multiple components can be represented by a single XML element
     *
     * @return the number of components the elements represents
     */
    @Override
    public int repetition() {
        return mRootElement.getRepetition();
    }

    @Override
    // TABLE ONLY -- Svante REMOVE THIS LATER AS THIS IT IS ONLY USED BY TABLES
    public List getChildren() {
        return list(this);
    }

    // Svante ToDo: After all the refactoring this looks like something to change after the release as well.
    private List<Component> list(final Table tableComponent) {
        return new AbstractList<Component>() {
            @Override
            public int size() {
                return tableComponent.size();
            }

            @Override
            public Component get(int index) {
                Component c = null;
                TableTableElement tableRoot = (TableTableElement) tableComponent.getRootElement();
                OdfTable table = OdfTable.getInstance(tableRoot);
                OdfTableRow row = table.getRowByIndex(index);
                if (row != null) {
                    c = row.getOdfElement().getComponent();
                }
                return c;
            }
        };
    }

    /**
     * Adds the given component to the root element
     *
     * @param c the component of the row to be added
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
     * @param index the position of the row being returned
     * @return either a text node of size 1 or an element being the root element
     * of a component
     */
    @Override
    public Node getChildNode(int index) {
        return mRootElement.receiveNode(index);

    }

    /**
     * Removes a component from the text element container. Removes either an
     * element representing a component or text node of size 1
     *
     * @param index row position to be removed
     * @return the row being removed
     */
    @Override
    public Node remove(int index) {
        Node node = this.getChildNode(index);
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
        OdfTable table = OdfTable.getInstance((TableTableElement) mRootElement);
        return table.getRowCount();
    }

    /**
     *
     * @return a property value.
     */
//	private String getPropertyValue(OdfStyleProperty prop) {
//		String value = null;
//
//		OdfStylePropertiesBase properties = getPropertiesElement(prop.getPropertySet());
//		if (properties != null) {
//			if (properties.hasAttributeNS(prop.getName().getUri(), prop.getName().getLocalName())) {
//				return properties.getOdfAttribute(prop.getName()).getValue();
//			}
//		}
//
//		OdfStyleBase parent = getParentStyle();
//		if (parent != null) {
//			return parent.getProperty(prop);
//		}
//
//		return value;
//	}
    public static String getProperty(OdfStyleProperty prop, OdfStylableElement element) {
        String value = null;
        OdfStyle style = element.getAutomaticStyle();
        if (style == null) {
            element.getOrCreateUnqiueAutomaticStyle();
            style = element.getAutomaticStyle();
            if (style == null) {
                style = element.getDocumentStyle();
            }
        }
        if (style != null) {
            value = style.getProperty(prop);
        }
        return value;
    }

    private static Length getPropertyLength(OdfStyleProperty prop, OdfStylableElement element) {
        Length length = null;
        String propValue = getProperty(prop, element);
        if (propValue != null && !propValue.isEmpty()) {
            length = new Length(propValue);
        }
        return length;

    }

    public static List<Integer> collectColumnWidths(TableTableElement tableElement, List<TableTableColumnElement> columns) {
        boolean hasRelColumnWidth = false;
        boolean hasAbsColumnWidth = false;
        boolean hasColumnWithoutWidth = false;
        List<Integer> columnRelWidths = new ArrayList();
        for (TableTableColumnElement column : columns) {
            if (column.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "style-name")) {
                Length tableWidth = getPropertyLength(StyleTablePropertiesElement.Width, tableElement);

                int repeatedColumns = 1;
                if (column.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
                    repeatedColumns = Integer.parseInt(column.getAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated"));
                }

                String columnRelWidth = getProperty(StyleTableColumnPropertiesElement.RelColumnWidth, column);

                // it is being assumed, when the columnRelWidth is once set, it is always set
                if (columnRelWidth != null && !columnRelWidth.isEmpty()) {
                    hasRelColumnWidth = true;
                    if (hasAbsColumnWidth) {
                        LOG.warning("******* BEWARE: Absolute and relative width are not supposed to be mixed!! ***********");
                    }
                    columnRelWidth = columnRelWidth.substring(0, columnRelWidth.indexOf('*'));
                    Integer relWidth = Integer.parseInt(columnRelWidth);
                    for (int i = 0; i < repeatedColumns; i++) {
                        columnRelWidths.add(relWidth);
                    }
                } else { // if there is no relative column width
                    if (hasRelColumnWidth) {
                        LOG.warning("******* BEWARE: Absolute and relative width are not supposed to be mixed!! ***********");
                    }

                    Length columnWidth = getPropertyLength(StyleTableColumnPropertiesElement.ColumnWidth, column);
                    // there can be only table width and ..
                    if (tableWidth != null) {
                        // columnwidth, with a single one missing
                        if (columnWidth != null) {
                            hasAbsColumnWidth = true;
                            int widthFactor = (int) Math.round((columnWidth.getMillimeters() * 100) / tableWidth.getMillimeters());
                            for (int i = 0; i < repeatedColumns; i++) {
                                columnRelWidths.add(widthFactor);
                            }
                        } else {
                            if (hasColumnWithoutWidth) {
                                LOG.warning("******* BEWARE: Two columns without width and no column width are not expected!! ***********");
                            }
                            hasColumnWithoutWidth = true;
                        }
                        // if the table is not set, it will always be unset..
                    } else {
                        if (columnWidth != null) {
                            hasAbsColumnWidth = true;
                            int widthFactor = (int) Math.round((columnWidth.getMicrometer() * 10));
                            for (int i = 0; i < repeatedColumns; i++) {
                                columnRelWidths.add(widthFactor);
                            }
                        } else {
                            LOG.warning("******* BEWARE: Two columns without width and no column width are not expected!! ***********");
                        }
                    }
                }
            }
        }
        return columnRelWidths;
    }

    static void stashColumnWidths(TableTableElement tableElement) {
        List<TableTableColumnElement> existingColumnList = Table.getTableColumnElements(tableElement, new LinkedList<TableTableColumnElement>());
        List<Integer> tableColumWidths = collectColumnWidths(tableElement, existingColumnList);
        tableElement.pushTableGrid(tableColumWidths);
    }

    /**
     * Returns all TableTableColumn descendants that exist within the
     * tableElement, even within groups, columns and header elements
     */
    public static List<TableTableColumnElement> getTableColumnElements(Element parent, List columns) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                if (child instanceof TableTableColumnElement) {
                    columns.add(child);
                } else if (child instanceof TableTableColumnGroupElement
                        || child instanceof TableTableHeaderColumnsElement
                        || child instanceof TableTableColumnsElement) {
                    columns = getTableColumnElements((Element) child, columns);
                } else if (child instanceof TableTableRowGroupElement
                        || child instanceof TableTableHeaderRowsElement
                        || child instanceof TableTableRowElement
                        || child instanceof TableTableRowsElement) {
                    break;
                }
            }
        }
        return columns;
    }
}
