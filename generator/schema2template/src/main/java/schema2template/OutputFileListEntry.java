/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
package schema2template;

import java.util.HashMap;
import java.util.Map;

/**
 * Belongs to FileListHandler. Represents one single entry in ouput filelist.
 *
 * @author Hans-Peter Schaal
 */
public class OutputFileListEntry {

    /**
     * Type of filelist entry (file or path)
     */
    public enum EntryType {
        FILE, PATH;
    }

    private EntryType mType;
    private Map<String, String> mAttributes;
    private int mLinenumber;

    /**
     * @param type Use FilelistEntry.EntryType.[FILE|PATH]
     */
    public OutputFileListEntry(EntryType type) {
        this(type, -1);
    }

    /**
     * @param type Use FilelistEntry.EntryType.[FILE|PATH]
     * @param lineNumber number in filelist.xml for logging / error output.
     */
    public OutputFileListEntry(EntryType type, int lineNumber) {
        mType = type;
        mAttributes = new HashMap<String, String>();
        mLinenumber = lineNumber;
    }

    /**
     * @return the entry type
     */
    public EntryType getType() {
        return mType;
    }

    /**
     * @return line number in filelist.xml for logging / error output.
     */
    public int getLineNumber() {
        return mLinenumber;
    }

    /**
     * @param key Attribute Key
     * @return Attribute Value
     */
    public String getAttribute(String key) {
        return mAttributes.get(key);
    }

    /**
     * @return Attributes as map
     */
    public Map<String, String> getAttributes() {
        return mAttributes;
    }

    /**
     * @param key Attribute Key
     * @param value Attribute Value. If null, delete the key.
     */
    public void setAttribute(String key, String value) {
        if (key == null) {
            return;
        }
        if (value == null) {
            mAttributes.remove(key);
        }
        else {
            mAttributes.put(key, value);
        }
    }

}
