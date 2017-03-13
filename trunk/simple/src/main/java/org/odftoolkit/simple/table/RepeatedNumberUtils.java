/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.	The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.	See the License for the
specific language governing permissions and limitations
under the License.
 */
package org.odftoolkit.simple.table;

import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * Utility functions to evaluate the "repeated-number"-Attributes of rows,
 * columns and cells.
 *
 * @author raimund
 */
public class RepeatedNumberUtils {

    private static final int LIBRE_OFFICE_WORKAROUND_COLCOUNT = 1024;
    private static final int LIBRE_OFFICE_WORKAROUND_ROWCOUNT = 1048576;
    private static final int MS_EXCEL_BINARY_WORKAROUND_ROWCOUNT = 32768;

    /**
     * Remove unused columns and rows from the end of a table.
     * <p>
     * This is a workaround for bug ODFTOOLKIT-388. LibreOffice adds two dummy
     * rows with "number-rows-repeated" attribute to "blow up" the sheet to
     * {@value #LIBRE_OFFICE_WORKAROUND_ROWCOUNT} rows as needed by MS Excel
     * 2010.
     * <p>
     * Files converted from MS Excel binary format (*.xls) contain a similar
     * dummy row to "blow up" the sheet up to
     * {@value #MS_EXCEL_BINARY_WORKAROUND_ROWCOUNT} rows.
     * <p>
     * Same is done for columns - one dummy column is added to get a whole
     * column count of {@value #LIBRE_OFFICE_WORKAROUND_COLCOUNT}.
     *
     * @param table Table to clean.
     */
    public static void removeDummyCellsFromTable(Table table) {
        // First analyze columns and rows; e. g. count them
        int wholeColumnCount = 0;
        int wholeRowCount = 0;
        int maxCellCount = 0;
        TableTableColumnElement lastColumnNode = null;
        TableTableRowElement msExcelDummyRow = null;
        for (Node node : new DomNodeList(table.getOdfElement().getChildNodes())) {
            // TODO: how about <table:table-column-group>
            if (node instanceof TableTableHeaderColumnsElement) {
                wholeColumnCount += _getHeaderColumnCount((TableTableHeaderColumnsElement) node);
                lastColumnNode = null;

            } else if (node instanceof TableTableColumnsElement) {
                wholeColumnCount += _getColumnsCount((TableTableColumnsElement) node);
                lastColumnNode = null;

            } else if (node instanceof TableTableColumnElement) {
                wholeColumnCount += _getNumberColumnsRepeated((TableTableColumnElement) node);
                lastColumnNode = (TableTableColumnElement) node;
            }

            if (node instanceof TableTableHeaderRowsElement) {
                wholeRowCount += _getHeaderRowCount((TableTableHeaderRowsElement) node);
                int n = _getCellCountOfRowWithoutDummies(node);
                maxCellCount = n > maxCellCount ? n : maxCellCount;

            } else if (node instanceof TableTableRowElement) {
                wholeRowCount += _getNumberRowsRepeated((TableTableRowElement) node);
                if (wholeRowCount == MS_EXCEL_BINARY_WORKAROUND_ROWCOUNT) {
                    msExcelDummyRow = (TableTableRowElement) node;
                }
                int n = _getCellCountOfRowWithoutDummies(node);
                maxCellCount = n > maxCellCount ? n : maxCellCount;

            } else if (node instanceof TableTableRowsElement) {
                for (Node nodeChild : new DomNodeList(node.getChildNodes())) {
                    if (nodeChild instanceof TableTableRowElement) {
                        wholeRowCount += _getNumberRowsRepeated((TableTableRowElement) nodeChild);
                        int n = _getCellCountOfRowWithoutDummies(node);
                        maxCellCount = n > maxCellCount ? n : maxCellCount;
                    }
                }
            }
        }

        // Removing the dummy rows at end if needed
        if (wholeRowCount == LIBRE_OFFICE_WORKAROUND_ROWCOUNT) {
            boolean done = false;
            for (int nodeIndex = table.getOdfElement().getChildNodes().getLength() - 1; !done && nodeIndex > 0; nodeIndex--) {
                Node node = table.getOdfElement().getChildNodes().item(nodeIndex);
                if (node instanceof TableTableHeaderRowsElement || node instanceof TableTableRowsElement) {
                    done = true;

                } else if (node instanceof TableTableRowElement) {
                    if (_isEmptyDummyRow((TableTableRowElement) node)) {
                        table.getOdfElement().removeChild(node);
                    } else {
                        done = true;
                    }
                }
            }
        }

        // Workaround for files converted from MS Excel
        if (msExcelDummyRow != null && msExcelDummyRow == table.getOdfElement().getLastChild()) {
            try {
                table.getOdfElement().removeChild(msExcelDummyRow);
            } catch (DOMException e) {
                // Row may be already removed by code above - just ignore that
            }
        }

        // Removing the dummy columns at end if needed.
        // Warning, we must retain at least one column for each cell,
        // so it may be needed to just reduce the number-columns-repeated
        // instead of deleting the cell.
        if (wholeColumnCount == LIBRE_OFFICE_WORKAROUND_COLCOUNT && lastColumnNode != null) {
            int n = _getNumberColumnsRepeated(lastColumnNode);
            if (n > 1) {
                int realColumnCount = wholeColumnCount - n;
                if (realColumnCount < maxCellCount) {
                    lastColumnNode.setTableNumberColumnsRepeatedAttribute(maxCellCount - realColumnCount);
                } else {
                    table.getOdfElement().removeChild(lastColumnNode);
                }
            }
        }
    }

    private static int _getCellCountOfRowWithoutDummies(Node row) {
        int cellCount = 0;
        Node lastCell = row.getLastChild();
        for (Node node : new DomNodeList(row.getChildNodes())) {
            if (node instanceof TableTableCellElement) {
                TableTableCellElement cell = (TableTableCellElement) node;

                Integer repCnt = cell.getTableNumberColumnsRepeatedAttribute();
                if (repCnt == null) {
                    repCnt = 1;
                }
                Integer spanned = cell.getTableNumberColumnsSpannedAttribute();
                if (spanned == null || spanned == 0) {
                    spanned = 1;
                }

                if (node != lastCell || repCnt == 1 || spanned != 1) {
                    cellCount += repCnt * spanned;
                }
            }
        }
        return cellCount;
    }

    private static int _getNumberRowsRepeated(TableTableRowElement rowElement) {
        int numberRowsRepeated = 0;

        if (rowElement != null) {
            Integer repCnt = rowElement.getTableNumberRowsRepeatedAttribute();
            if (repCnt == null) {
                numberRowsRepeated = 1;
            } else {
                numberRowsRepeated = repCnt;
            }
        }

        return numberRowsRepeated;
    }

    static int _getNumberColumnsRepeated(TableTableColumnElement columnElement) {
        int numberColumnsRepeated = 0;

        if (columnElement != null) {
            Integer repCnt = columnElement.getTableNumberColumnsRepeatedAttribute();
            if (repCnt == null) {
                numberColumnsRepeated = 1;
            } else {
                numberColumnsRepeated = repCnt;
            }
        }

        return numberColumnsRepeated;
    }

    private static int _getHeaderColumnCount(TableTableHeaderColumnsElement headers) {
        int result = 0;
        if (headers != null) {
            for (Node n : new DomNodeList(headers.getChildNodes())) {
                result += _getNumberColumnsRepeated((TableTableColumnElement) n);
            }
        }
        return result;
    }

    private static int _getColumnsCount(TableTableColumnsElement columns) {
        int result = 0;
        if (columns != null) {
            for (Node n : new DomNodeList(columns.getChildNodes())) {
                result += _getNumberColumnsRepeated((TableTableColumnElement) n);
            }
        }
        return result;
    }

    private static int _getHeaderRowCount(TableTableHeaderRowsElement headers) {
        int result = 0;
        if (headers != null) {
            for (Node n : new DomNodeList(headers.getChildNodes())) {
                if (n instanceof TableTableRowElement) {
                    result += _getNumberRowsRepeated((TableTableRowElement) n);
                }
            }
        }
        return result;
    }

    private static boolean _isEmptyDummyRow(TableTableRowElement row) {
        boolean dummyRow = true;
        for (Node child = row.getChildNodes().item(0); dummyRow && child != null; child = child.getNextSibling()) {
            if (child instanceof TableTableCellElement) {
                dummyRow = child.getChildNodes().getLength() == 0;
            } else {
                dummyRow = false;
            }
        }

        return dummyRow;
    }
}
