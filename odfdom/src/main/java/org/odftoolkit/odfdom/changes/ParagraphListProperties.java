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

public class ParagraphListProperties {

    /**
     * The status of the list style
     */
    // on each list
    String mStyleName = null;
    // on each list item
    String mStyleOverride = null;
    boolean mShowListLabel = false;
    boolean mIsListStart = false;
    String mListId;
    String mListXmlId;
    String mListItemXmlId;

    public String getListId() {
        return mListId;
    }

    public void setListId(String listId) {
        mListId = listId;
    }

    public String getListXmlId() {
        return mListXmlId;
    }

    public void setListXmlId(String listXmlId) {
        mListXmlId = listXmlId;
    }

    public String getListItemXmlId() {
        return mListItemXmlId;
    }

    public void setListItemXmlId(String listItemXmlId) {
        mListItemXmlId = listItemXmlId;
    }

    public void setListStart(boolean isListStart) {
        mIsListStart = isListStart;
    }

    public ParagraphListProperties() {
    }

    public void showListLabel(boolean showListLabel) {
        mShowListLabel = showListLabel;
    }

    public boolean hasListLabel() {
        return mShowListLabel;
    }

    public boolean isListStart() {
        return mIsListStart;
    }

    /**
     * Overrides the list style given by a <text:list> element.
     *
     * @param styleName the new list style, or null to unset a previous override
     */
    public void overrideListStyle(String styleName) {
        mStyleOverride = styleName;
    }

    public void setListStyleName(String styleName) {
        mStyleName = styleName;
    }

    String getListStyleName() {
        String styleName = null;
        if (mStyleOverride != null && !mStyleOverride.isEmpty()) {
            styleName = mStyleOverride;
        } else if (mStyleName != null && !mStyleName.isEmpty()) {
            styleName = mStyleName;
        }
        return styleName;
    }
}
