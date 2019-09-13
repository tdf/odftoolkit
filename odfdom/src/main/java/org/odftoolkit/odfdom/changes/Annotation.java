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

import org.json.JSONArray;
import org.odftoolkit.odfdom.pkg.OdfElement;

class Annotation extends Component {

    Annotation(OdfElement componentElement, Component parent) {
        super(componentElement, parent);
    }

    /**
     * Get parent component of the given position
     */
    @Override
    public Component getParentOf(JSONArray position) {
        Component c = null;
        if (position.length() == 1) {
            c = this;
        } else {
            c = get(position, true, false, 0);
        }
        return c;
    }

}
