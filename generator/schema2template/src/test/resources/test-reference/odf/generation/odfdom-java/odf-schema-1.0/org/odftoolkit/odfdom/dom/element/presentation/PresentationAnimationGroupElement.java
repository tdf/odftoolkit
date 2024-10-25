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
package org.odftoolkit.odfdom.dom.element.presentation;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;

/**
 * DOM implementation of OpenDocument element  {@odf.element presentation:animation-group}.
 *
 */
public class PresentationAnimationGroupElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.PRESENTATION, "animation-group");

	/**
	 * Create the instance of <code>PresentationAnimationGroupElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public PresentationAnimationGroupElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element presentation:animation-group}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Create child element {@odf.element presentation:dim}.
	 *
	 * @param drawColorValue  the <code>String</code> value of <code>DrawColorAttribute</code>, see {@odf.attribute  draw:color} at specification
	 * @param drawShapeIdValue  the <code>String</code> value of <code>DrawShapeIdAttribute</code>, see {@odf.attribute  draw:shape-id} at specification
	 * @return the element {@odf.element presentation:dim}
	 */
	 public PresentationDimElement newPresentationDimElement(String drawColorValue, String drawShapeIdValue) {
		PresentationDimElement presentationDim = ((OdfFileDom) this.ownerDocument).newOdfElement(PresentationDimElement.class);
		presentationDim.setDrawColorAttribute(drawColorValue);
		presentationDim.setDrawShapeIdAttribute(drawShapeIdValue);
		this.appendChild(presentationDim);
		return presentationDim;
	}

	/**
	 * Create child element {@odf.element presentation:hide-shape}.
	 *
	 * @param drawShapeIdValue  the <code>String</code> value of <code>DrawShapeIdAttribute</code>, see {@odf.attribute  draw:shape-id} at specification
	 * @return the element {@odf.element presentation:hide-shape}
	 */
	 public PresentationHideShapeElement newPresentationHideShapeElement(String drawShapeIdValue) {
		PresentationHideShapeElement presentationHideShape = ((OdfFileDom) this.ownerDocument).newOdfElement(PresentationHideShapeElement.class);
		presentationHideShape.setDrawShapeIdAttribute(drawShapeIdValue);
		this.appendChild(presentationHideShape);
		return presentationHideShape;
	}

	/**
	 * Create child element {@odf.element presentation:hide-text}.
	 *
	 * @param drawShapeIdValue  the <code>String</code> value of <code>DrawShapeIdAttribute</code>, see {@odf.attribute  draw:shape-id} at specification
	 * @return the element {@odf.element presentation:hide-text}
	 */
	 public PresentationHideTextElement newPresentationHideTextElement(String drawShapeIdValue) {
		PresentationHideTextElement presentationHideText = ((OdfFileDom) this.ownerDocument).newOdfElement(PresentationHideTextElement.class);
		presentationHideText.setDrawShapeIdAttribute(drawShapeIdValue);
		this.appendChild(presentationHideText);
		return presentationHideText;
	}

	/**
	 * Create child element {@odf.element presentation:play}.
	 *
	 * @param drawShapeIdValue  the <code>String</code> value of <code>DrawShapeIdAttribute</code>, see {@odf.attribute  draw:shape-id} at specification
	 * @return the element {@odf.element presentation:play}
	 */
	 public PresentationPlayElement newPresentationPlayElement(String drawShapeIdValue) {
		PresentationPlayElement presentationPlay = ((OdfFileDom) this.ownerDocument).newOdfElement(PresentationPlayElement.class);
		presentationPlay.setDrawShapeIdAttribute(drawShapeIdValue);
		this.appendChild(presentationPlay);
		return presentationPlay;
	}

	/**
	 * Create child element {@odf.element presentation:show-shape}.
	 *
	 * @param drawShapeIdValue  the <code>String</code> value of <code>DrawShapeIdAttribute</code>, see {@odf.attribute  draw:shape-id} at specification
	 * @return the element {@odf.element presentation:show-shape}
	 */
	 public PresentationShowShapeElement newPresentationShowShapeElement(String drawShapeIdValue) {
		PresentationShowShapeElement presentationShowShape = ((OdfFileDom) this.ownerDocument).newOdfElement(PresentationShowShapeElement.class);
		presentationShowShape.setDrawShapeIdAttribute(drawShapeIdValue);
		this.appendChild(presentationShowShape);
		return presentationShowShape;
	}

	/**
	 * Create child element {@odf.element presentation:show-text}.
	 *
	 * @param drawShapeIdValue  the <code>String</code> value of <code>DrawShapeIdAttribute</code>, see {@odf.attribute  draw:shape-id} at specification
	 * @return the element {@odf.element presentation:show-text}
	 */
	 public PresentationShowTextElement newPresentationShowTextElement(String drawShapeIdValue) {
		PresentationShowTextElement presentationShowText = ((OdfFileDom) this.ownerDocument).newOdfElement(PresentationShowTextElement.class);
		presentationShowText.setDrawShapeIdAttribute(drawShapeIdValue);
		this.appendChild(presentationShowText);
		return presentationShowText;
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
