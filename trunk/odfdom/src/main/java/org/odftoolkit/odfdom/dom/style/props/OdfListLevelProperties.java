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

public interface OdfListLevelProperties {
	public final static OdfStyleProperty Height =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.FO, "height"));
	public final static OdfStyleProperty TextAlign =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.FO, "text-align"));
	public final static OdfStyleProperty Width =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.FO, "width"));
	public final static OdfStyleProperty FontName =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.STYLE, "font-name"));
	public final static OdfStyleProperty VerticalPos =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.STYLE, "vertical-pos"));
	public final static OdfStyleProperty VerticalRel =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.STYLE, "vertical-rel"));
	public final static OdfStyleProperty Y =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.SVG, "y"));
	public final static OdfStyleProperty ListLevelPositionAndSpaceMode =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.TEXT, "list-level-position-and-space-mode"));
	public final static OdfStyleProperty MinLabelDistance =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.TEXT, "min-label-distance"));
	public final static OdfStyleProperty MinLabelWidth =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.TEXT, "min-label-width"));
	public final static OdfStyleProperty SpaceBefore =
		OdfStyleProperty.get(OdfStylePropertiesSet.ListLevelProperties, OdfName.newName(OdfDocumentNamespace.TEXT, "space-before"));
}
