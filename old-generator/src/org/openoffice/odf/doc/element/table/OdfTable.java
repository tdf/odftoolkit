/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * Use is subject to license terms.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package org.openoffice.odf.doc.element.table;

import org.openoffice.odf.dom.element.table.OdfTableElement;
import org.openoffice.odf.dom.util.DomNodeList;
import org.openoffice.odf.doc.OdfFileDom;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class OdfTable extends OdfTableElement {

    private int mCurrentNumberOfColumns = 0;

    /** Creates a new instance of OdfTable */
    public OdfTable(OdfFileDom ownerDoc) {
        super(ownerDoc);
    }

    //@Override
    public OdfTableColumn getTableColumn(int c) {
        List<OdfTableColumn> list = getTableColumnList();
        if (list.size() > c) {
            return list.get(c);
        } else {
            return null;
        }
    }

    public List<OdfTableColumn> getTableColumnList() {
        return makeTableColumnList();
    }

    //@Override
    public int getTableColumnCount() {
        return mCurrentNumberOfColumns; // optimization
    //return makeTableColumnList().size();
    }

    //@Override
    public OdfTableColumn addTableColumn() {
        return addTableColumn(1);
    }

    //@Override
    public OdfTableColumn addTableColumn(int repeat)
    {
        // find the last table coulmn element
        Node ref = getFirstChild();
        for (Node n : new DomNodeList(this.getChildNodes())) {
            if (n instanceof OdfTableColumn) {
                ref = n.getNextSibling();
            }
        }
        OdfTableColumn tce =
                (OdfTableColumn) getOwnerDocument().createElementNS(
                OdfTableColumn.ELEMENT_NAME.getUri(),
                OdfTableColumn.ELEMENT_NAME.getQName());
        if (ref != null) {
            tce = (OdfTableColumn) insertBefore(tce, ref);
        } else {
            tce = (OdfTableColumn) appendChild(tce);
        }

        if (repeat > 1) {
            tce.setNumberColumnsRepeated(repeat);
        }

        mCurrentNumberOfColumns += repeat;

        return tce;
    }

    private List<OdfTableColumn> makeTableColumnList() {
        ArrayList<OdfTableColumn> list = new ArrayList<OdfTableColumn>();
        // get the column definitions
        for (Node n : new DomNodeList(this.getChildNodes())) {
            if (n instanceof OdfTableColumn) {
                OdfTableColumn col = (OdfTableColumn) n;
                for (int i = 0; i < col.getNumberColumnsRepeated(); i++) {
                    list.add(col);
                }
            } else if (n instanceof OdfTableRow) {
                break;
            }
        }
        return list;
    }

    public void setColumnList(List<OdfTableColumn> cl) {
        // remove existing column definitions
        // we cannot use the DomNodeList directly since it is a live list and 
        // will change whenever a node is removed
        List<Node> rmList = new ArrayList<Node>();
        for (Node n : new DomNodeList(this.getChildNodes())) {
            if (n instanceof OdfTableColumn) {
                rmList.add(n);
            }
        }
        for (Node n : rmList) {
            removeChild(n);
        }

        Node ref = getFirstChild();
        // create new TableColumn elements
        OdfTableColumn prev = null;

        for (OdfTableColumn tce : cl) {
            if (prev == null || prev != tce) {
                insertBefore(tce, ref);
                prev = tce;
            } else if (tce != null) {
                tce.setNumberColumnsRepeated(tce.getNumberColumnsRepeated() + 1);
            }
        }
    }

    public Node appendRow(Node aNewChild) throws DOMException {
        Node aNode = super.appendChild(aNewChild);
        if (aNode instanceof OdfTableRow) {
            OdfTableRow aRow = (OdfTableRow) aNode;
            aRow.inheritSpannedCells(0);
        } else if (aNode instanceof OdfTableColumn) {
            ++mCurrentNumberOfColumns;
        }

        return aNode;
    }
}
