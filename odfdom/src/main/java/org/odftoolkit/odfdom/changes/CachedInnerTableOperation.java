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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CachedInnerTableOperation extends CachedOperation {

    // required to track repeatedColumns rows for spreadsheets..
    int mCurrentRowNo = 0;
    int mCurrentRowRepetition = 0;
    //** Cell Op Props
    public String mCellContentString = null;
    int mCellNo;

    public CachedInnerTableOperation(String componentType, List<Integer> start, boolean absolutePosition, Map<String, Object> styleFormatting, Object... componentProperties) {
        super(componentType, start, absolutePosition, styleFormatting, componentProperties);
    }

    @Override
    public CachedOperation clone() {
        return new CachedInnerTableOperation(mComponentType, new ArrayList<Integer>(mStart), mAbsolutePosition, mHardFormattingProperties, mComponentProperties);
    }
}
