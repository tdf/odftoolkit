/** ***********************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 ************************************************************ */
package org.odftoolkit.odfdom.changes;

import java.util.Map;

class ColumnDefaultCellProps {

    /**
     * The column ranges with a default cell style will be memorized. Within a
     * sheet with maximum numer of rows, the default cell style can be kept as
     * cell style on the column insertion. As within the operational table model
     * the table, row and column styles are only affecting cells that have
     * neither content nor style, all default cell styles have to applied to
     * cells with content without style. Repeated cells might get be split due
     * to changing default cell styles. Instead of altering the XML model the
     * method to collect the cell range operation will be triggered for every
     * change of default cell style.
     */

    /**
     * position starts counting with 0. It becomes necessary as there might be
     * gaps in between columns due to missing default cell styles at the column.
     */
    public int mColumnStartPos = 0;
    /**
     * the default mColumnRepetition is 1
     */
    public int mColumnRepetition = 1;
    public String mDefaultCellStyleId = null;
    public Map<String, Object> mMappedDefaultCellStyles = null;
}
