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

package org.odftoolkit.odfdom.incubator.meta;

import org.odftoolkit.odfdom.dom.element.meta.MetaAutoReloadElement;

/**
 * <code>OdfMetaAutoReload</code> feature specifies whether the document is
 * reloaded or replaced by another document after a certain period of time has
 * elapsed.
 * 
 */
// TODO: the functions are not completed.
public class OdfMetaAutoReload {

	private MetaAutoReloadElement mMetaAutoReloadElement;

	/**
	 * Constructor of <code>OdfMetaAutoReload</code> feature.
	 * 
	 * @param element
	 *            an instance of ODF Element <code>MetaAutoReloadElement</code>
	 */
	public OdfMetaAutoReload(MetaAutoReloadElement element) {
		mMetaAutoReloadElement = element;
	}

	/**
	 * Return the instance of ODF Element <codeMetaAutoReloadElement</code>
	 * which represents this feature.
	 * 
	 * @return the instance of ODF Element <code>MetaAutoReloadElement</code>
	 */
	public MetaAutoReloadElement getMetaAutoReloadElement() {
		return mMetaAutoReloadElement;
	}
}
