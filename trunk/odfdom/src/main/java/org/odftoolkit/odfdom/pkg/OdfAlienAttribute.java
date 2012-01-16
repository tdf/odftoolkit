/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.pkg;

import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.DOMException;

/**
 * Any attribute within the ODF package that is listed in the ODF schema.
 * Note: Existence does not imply invalid ODF, as even namespace attributes are not
 * listed and mapped to an OdfAlienAttribute.
 */
public class OdfAlienAttribute extends OdfAttribute {

	private static final long serialVersionUID = 4210521398191729448L;

	/**
	 * Creates an alien attribute on the XML file
	 *
	 * @param ownerDocument the DOM of the XML file within the ODF package
	 * @param name of the XML attribute to be created
	 * @throws DOMException thrown for any problem during attribute creation
	 */

	public OdfAlienAttribute(OdfFileDom ownerDocument,
            OdfName name) throws DOMException {
        super(ownerDocument, name.getUri(), name.getQName());
        ATTRIBUTE_NAME = name;
    }
    public final OdfName ATTRIBUTE_NAME;

    @Override
	public OdfName getOdfName() {
        return ATTRIBUTE_NAME;
    }

	@Override
	public String getDefault() {
		return null;
	}

	@Override
	public boolean hasDefault() {
		return false;
	}
}
