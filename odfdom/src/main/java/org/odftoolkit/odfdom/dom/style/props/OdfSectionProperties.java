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

// !!! GENERATED SOURCE CODE !!!
package org.odftoolkit.odfdom.dom.style.props;

import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;

public interface OdfSectionProperties {
	public final static OdfStyleProperty BackgroundColor =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.FO, "background-color"));
	public final static OdfStyleProperty MarginLeft =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.FO, "margin-left"));
	public final static OdfStyleProperty MarginRight =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.FO, "margin-right"));
	public final static OdfStyleProperty Editable =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.STYLE, "editable"));
	public final static OdfStyleProperty Protect =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.STYLE, "protect"));
	public final static OdfStyleProperty WritingMode =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.STYLE, "writing-mode"));
	public final static OdfStyleProperty DontBalanceTextColumns =
		OdfStyleProperty.get(OdfStylePropertiesSet.SectionProperties, OdfName.newName(OdfDocumentNamespace.TEXT, "dont-balance-text-columns"));
}
