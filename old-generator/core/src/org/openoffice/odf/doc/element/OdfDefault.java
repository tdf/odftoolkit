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
package org.openoffice.odf.doc.element;

import org.openoffice.odf.dom.element.OdfElement;
import org.openoffice.odf.doc.*;
import org.openoffice.odf.dom.OdfName;
import org.w3c.dom.DOMException;

/**
 * Temporary class until every ODF class is mapped
 */
public class OdfDefault extends OdfElement {

    public OdfDefault(OdfFileDom ownerDocument,
            OdfName name) throws DOMException {
        super(ownerDocument, name.getUri(), name.getQName());
        ELEMENT_NAME = name;
    }
    public final OdfName ELEMENT_NAME;

    public OdfName getOdfName() {
        return ELEMENT_NAME;
    }
}
