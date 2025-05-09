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

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.draw;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawAlignAttribute;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawIdAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgXAttribute;
import org.odftoolkit.odfdom.dom.attribute.svg.SvgYAttribute;

/**
 * DOM implementation of OpenDocument element  {@odf.element draw:glue-point}.
 *
 */
public class DrawGluePointElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.DRAW, "glue-point");

	/**
	 * Create the instance of <code>DrawGluePointElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public DrawGluePointElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element draw:glue-point}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawAlignAttribute</code> , See {@odf.attribute draw:align}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawAlignAttribute() {
		DrawAlignAttribute attr = (DrawAlignAttribute) getOdfAttribute(OdfDocumentNamespace.DRAW, "align");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawAlignAttribute</code> , See {@odf.attribute draw:align}
	 *
	 * @param drawAlignValue   The type is <code>String</code>
	 */
	public void setDrawAlignAttribute(String drawAlignValue) {
		DrawAlignAttribute attr = new DrawAlignAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(drawAlignValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>DrawIdAttribute</code> , See {@odf.attribute draw:id}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getDrawIdAttribute() {
		DrawIdAttribute attr = (DrawIdAttribute) getOdfAttribute(OdfDocumentNamespace.DRAW, "id");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>DrawIdAttribute</code> , See {@odf.attribute draw:id}
	 *
	 * @param drawIdValue   The type is <code>String</code>
	 */
	public void setDrawIdAttribute(String drawIdValue) {
		DrawIdAttribute attr = new DrawIdAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(drawIdValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgXAttribute</code> , See {@odf.attribute svg:x}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgXAttribute() {
		SvgXAttribute attr = (SvgXAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "x");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgXAttribute</code> , See {@odf.attribute svg:x}
	 *
	 * @param svgXValue   The type is <code>String</code>
	 */
	public void setSvgXAttribute(String svgXValue) {
		SvgXAttribute attr = new SvgXAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgXValue);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation <code>SvgYAttribute</code> , See {@odf.attribute svg:y}
	 *
	 * Attribute is mandatory.
	 *
	 * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public String getSvgYAttribute() {
		SvgYAttribute attr = (SvgYAttribute) getOdfAttribute(OdfDocumentNamespace.SVG, "y");
		if (attr != null) {
			return String.valueOf(attr.getValue());
		}
		return null;
	}

	/**
	 * Sets the value of ODFDOM attribute representation <code>SvgYAttribute</code> , See {@odf.attribute svg:y}
	 *
	 * @param svgYValue   The type is <code>String</code>
	 */
	public void setSvgYAttribute(String svgYValue) {
		SvgYAttribute attr = new SvgYAttribute((OdfFileDom) this.ownerDocument);
		setOdfAttribute(attr);
		attr.setValue(svgYValue);
	}

  /**
   * Accept an visitor instance to allow the visitor to do some operations. Refer to visitor design
   * pattern to get a better understanding.
   *
   * @param visitor an instance of DefaultElementVisitor
   */
	@Override
	public void accept(ElementVisitor visitor) {
		if (visitor instanceof DefaultElementVisitor) {
			DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
			defaultVisitor.visit(this);
		} else {
			visitor.visit(this);
		}
	}
}
