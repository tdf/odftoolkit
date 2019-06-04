/*
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
 */
package org.odftoolkit.odfdom.dom.element;

import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.Attr;

/**
 * This class decides upon the shape style attribute, whether the style family is
 * of type 'graphic' or 'presentation'.
 *
 *
 * Why do have ODF shapes two alternatives for their style:family attribute?
 *
 * In ODF a style (ie. style:style) is always identified not alone by its name,
 * but as well by its style:family. Elements are in general predefined to one style:family.
 * For instance, a paragraph (text:p) will have a style from the style:family="paragraph",
 * a span (text:span) would have a style:family="text".
 *
 * Only ODF shapes (e.g. draw:frame) can choose between two different style:family values,
 * ie. 'presentation' and 'graphic'.
 *
 * The idea behind is that 'graphic' family shapes have a style that belong to the document
 * similar as all other styles, but 'presentation' family shapes have a style that belongs
 * to a master page.
 *
 * The difference:
 * Whenever in an application the master page of a page is being changed, all 'graphic'
 * shapes look the same, only the 'presentation' shapes will get a new look-and-feel
 * from the new master page.
 */
abstract public class OdfStyleableShapeElement extends OdfStylableElement {

	private static final long serialVersionUID = 3604813885619852184L;
	private static OdfName PresStyleAttrName = OdfName.newName(OdfDocumentNamespace.PRESENTATION, "style-name");
	private static OdfName DrawStyleAttrName = OdfName.newName(OdfDocumentNamespace.DRAW, "style-name");

	public OdfStyleableShapeElement(OdfFileDom ownerDocument, OdfName name) {
		super(ownerDocument, name, OdfStyleFamily.Graphic, DrawStyleAttrName);
	}

	/**
	 * When the style-name prefix is changed between draw: and presentation:
	 * the style:family is adjusted as well
	 *
	 * @param uri the namespace uri of the attribute to be changed
	 * @param localname of the attribute to be changed
	 */
	protected void adjustStyleNameAttrib(String uri, String localname) {
		if (DrawStyleAttrName.equals(uri, localname)) {
			mStyleNameAttrib = DrawStyleAttrName;
			mFamily = OdfStyleFamily.Graphic;
		} else if (PresStyleAttrName.equals(uri, localname)) {
			mStyleNameAttrib = PresStyleAttrName;
			mFamily = OdfStyleFamily.Presentation;
		}
	}

	@Override
	public void setAttributeNS(String uri, String localname, String value) {
		if ((value != null) && (value.length() != 0)) {
			adjustStyleNameAttrib(uri, localname);
		}

		super.setAttributeNS(uri, localname, value);
	}

	@Override
	public Attr setAttributeNodeNS(Attr newAttr) {
		String uri = newAttr.getNamespaceURI();
		String localname = newAttr.getName();
		adjustStyleNameAttrib(uri, localname);
		return super.setAttributeNodeNS(newAttr);
	}
}

