/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.changes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.odftoolkit.odfdom.changes.ColumnDefaultCellProps;

@SuppressWarnings("serial")
class CachedTable extends CachedComponent {

    private ArrayList<CachedTable> mTableStack = new ArrayList<CachedTable>();
    public HashMap<List<Integer>, CachedTable> mAllTables = new HashMap<List<Integer>, CachedTable>();
    // START *** SPREADSHEET PROPERTIES
    // required to track repeatedColumns rows for spreadsheets..
    public int mPreviousRepeatedRows = 0;
    public int mRepeatedRowsOnly = 0;
    // spreadsheet range, representing a subset of a single row.
    // added cell data (content & format) neglecting starting/ending empty cells and within the row any adjacent empty cells above MAX_REPEATING_EMPTY_CELLS
    public JSONArray mCurrentRange;
    // only cells with content should trigger an operatoin
    public int mFirstContentCellNo = -1;
    // equal cell contents can be given out compressed
    public int mFirstEqualCellNo = -1;
    public int mCurrentColumnNo = 0;
    // after there had been content, three following empty cells are being counted, before a new insert operation is being triggered
    public int mEmptyCellCount = 0;
    public int mFirstEmptyCell = -1;
    // the JSONArray does not allow to delete empty cells, therefore they have to be counted
    public JSONObject mPreviousCell = null;
    private int mCellRepetition = 1;
    // get the sheetNo
    public Integer mSheetNo;
    public Integer mFirstRow = 0;
    public Integer mLastRow = 0;
    public ArrayList<CachedInnerTableOperation> mCachedTableContentOps = null;
    // 1) Mapping the column's default cell style to empty cells for none max size OR
    // adding column's default cell style to column style
    // 2) Mapping the largest range of rows/columns to the table in case of MAX sheet
    public Integer rowCount = null;
    public Integer columnCount = null;

    // We want to compress two identical following row format operations
    public Boolean lastRowIsVisible = Boolean.TRUE;
    public String lastRowStyleName = null;
    public int lastRowRepetition = 0;
    public CachedInnerTableOperation lastRowFormatOperation = null;

    // List of the column properties with default cell styles
    public List<ColumnDefaultCellProps> mColumnDefaultCells = null;
    public ColumnDefaultCellProps mCurrentDefaultCellProps = null;
    public int nextColumnDefaultCellListPos = 0;
    public String mPreviousColumnCellStyleID = null;
    // Boolean object as three states: There is a default style and all columns covered with dfault style (TRUE), there is a default style, but sometimes no style (FALSE) and no "Default" style (NULL)
    public Boolean mHasOnlyDefaultColumnCellStyles = null;

    // Temporary map to count the most used column style, to move this in case of full column count to the table style
    public Map<String, Integer> columnStyleOccurrence = null;
    // the name of the column style being used most in the table
    public String mMostUsedColumnStyle = null;

    // Temporary map to count the most used row style, to move this in case of full row count to the table style
    public Map<String, Integer> rowStyleOccurrence = null;
    // the name of the column style being used most in the table
    public String mMostUsedRowStyle = null;

    // END *** SPREADSHEET PROPERTIES
    public int mColumnCount = 0;
    public int mRowCount = 0;
    public boolean mWhiteSpaceOnly = true;
    public int mCellCount = 0;
    public boolean mIsTooLarge = false;
    public List<Integer> mTableGrid;
    public List<Integer> mStart;
    public String tableName;

    public CachedTable() {
        super();
    }

    public int getSubTableCount() {
        return mTableStack.size();
    }

    public void addSubTable(CachedTable subTable, List<Integer> position) {
        //TODO: Do we need two containers?
        mTableStack.add(subTable);
        mAllTables.put(position, subTable);
    }

    public CachedTable getSubTable(List<Integer> position) {
        return mAllTables.get(position);
    }

    public void removeSubTable() {
        mTableStack.remove(mTableStack.size() - 1);
    }

    public int getCellRepetition() {
        return mCellRepetition;
    }

    public void setCellRepetition(int set) {
        mCellRepetition = set;
    }

}
