
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
package org.odftoolkit.odfdom.dom.attribute.presentation;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.pkg.OdfAttribute;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;

/**
 * DOM implementation of OpenDocument attribute  {@odf.attribute presentation:transition-style}.
 *
 */
public class PresentationTransitionStyleAttribute extends OdfAttribute {

public static final OdfName ATTRIBUTE_NAME = OdfName.newName(OdfDocumentNamespace.PRESENTATION, "transition-style");


	/**
	 * Create the instance of OpenDocument attribute {@odf.attribute presentation:transition-style}.
	 *
	 * @param ownerDocument       The type is <code>OdfFileDom</code>
	 */
	public PresentationTransitionStyleAttribute(OdfFileDom ownerDocument) {
		super(ownerDocument, ATTRIBUTE_NAME);
	}

	/**
	 * Returns the attribute name.
	 *
	 * @return the <code>OdfName</code> for {@odf.attribute presentation:transition-style}.
	 */
	@Override
	public OdfName getOdfName() {
		return ATTRIBUTE_NAME;
	}

	/**
	 * @return Returns the name of this attribute.
	 */
	@Override
	public String getName() {
		return ATTRIBUTE_NAME.getLocalName();
	}

	/**
	 * The value set of {@odf.attribute presentation:transition-style}.
	 */
	public enum Value {
		CLOCKWISE("clockwise"), CLOSE("close"), CLOSE_HORIZONTAL("close-horizontal"), CLOSE_VERTICAL("close-vertical"), COUNTERCLOCKWISE("counterclockwise"), DISSOLVE("dissolve"), FADE_FROM_BOTTOM("fade-from-bottom"), FADE_FROM_CENTER("fade-from-center"), FADE_FROM_LEFT("fade-from-left"), FADE_FROM_LOWERLEFT("fade-from-lowerleft"), FADE_FROM_LOWERRIGHT("fade-from-lowerright"), FADE_FROM_RIGHT("fade-from-right"), FADE_FROM_TOP("fade-from-top"), FADE_FROM_UPPERLEFT("fade-from-upperleft"), FADE_FROM_UPPERRIGHT("fade-from-upperright"), FADE_TO_CENTER("fade-to-center"), FLY_AWAY("fly-away"), HORIZONTAL_CHECKERBOARD("horizontal-checkerboard"), HORIZONTAL_LINES("horizontal-lines"), HORIZONTAL_STRIPES("horizontal-stripes"), INTERLOCKING_HORIZONTAL_LEFT("interlocking-horizontal-left"), INTERLOCKING_HORIZONTAL_RIGHT("interlocking-horizontal-right"), INTERLOCKING_VERTICAL_BOTTOM("interlocking-vertical-bottom"), INTERLOCKING_VERTICAL_TOP("interlocking-vertical-top"), MELT("melt"), MOVE_FROM_BOTTOM("move-from-bottom"), MOVE_FROM_LEFT("move-from-left"), MOVE_FROM_LOWERLEFT("move-from-lowerleft"), MOVE_FROM_LOWERRIGHT("move-from-lowerright"), MOVE_FROM_RIGHT("move-from-right"), MOVE_FROM_TOP("move-from-top"), MOVE_FROM_UPPERLEFT("move-from-upperleft"), MOVE_FROM_UPPERRIGHT("move-from-upperright"), NONE("none"), OPEN("open"), OPEN_HORIZONTAL("open-horizontal"), OPEN_VERTICAL("open-vertical"), RANDOM("random"), ROLL_FROM_BOTTOM("roll-from-bottom"), ROLL_FROM_LEFT("roll-from-left"), ROLL_FROM_RIGHT("roll-from-right"), ROLL_FROM_TOP("roll-from-top"), SPIRALIN_LEFT("spiralin-left"), SPIRALIN_RIGHT("spiralin-right"), SPIRALOUT_LEFT("spiralout-left"), SPIRALOUT_RIGHT("spiralout-right"), STRETCH_FROM_BOTTOM("stretch-from-bottom"), STRETCH_FROM_LEFT("stretch-from-left"), STRETCH_FROM_RIGHT("stretch-from-right"), STRETCH_FROM_TOP("stretch-from-top"), UNCOVER_TO_BOTTOM("uncover-to-bottom"), UNCOVER_TO_LEFT("uncover-to-left"), UNCOVER_TO_LOWERLEFT("uncover-to-lowerleft"), UNCOVER_TO_LOWERRIGHT("uncover-to-lowerright"), UNCOVER_TO_RIGHT("uncover-to-right"), UNCOVER_TO_TOP("uncover-to-top"), UNCOVER_TO_UPPERLEFT("uncover-to-upperleft"), UNCOVER_TO_UPPERRIGHT("uncover-to-upperright"), VERTICAL_CHECKERBOARD("vertical-checkerboard"), VERTICAL_LINES("vertical-lines"), VERTICAL_STRIPES("vertical-stripes"), WAVYLINE_FROM_BOTTOM("wavyline-from-bottom"), WAVYLINE_FROM_LEFT("wavyline-from-left"), WAVYLINE_FROM_RIGHT("wavyline-from-right"), WAVYLINE_FROM_TOP("wavyline-from-top");

		private String mValue;

		Value(String value) {
			mValue = value;
		}

		@Override
		public String toString() {
			return mValue;
		}

		public static Value enumValueOf(String value) {
			for(Value aIter : values()) {
				if (value.equals(aIter.toString())) {
				return aIter;
				}
			}
			return null;
		}
	}

	/**
	 * @param attrValue The <code>Enum</code> value of the attribute.
	 */
	public void setEnumValue(Value attrValue) {
		setValue(attrValue.toString());
	}

	/**
	 * @return Returns the <code>Enum</code> value of the attribute
	 */
	public Value getEnumValue() {
		return Value.enumValueOf(this.getValue());
	}

	/**
	 * Returns the default value of {@odf.attribute presentation:transition-style}.
	 *
	 * @return the default value as <code>String</code> dependent of its element name
	 *         return <code>null</code> if the default value does not exist
	 */
	@Override
	public String getDefault() {
		return null;
	}

	/**
	 * Default value indicator. As the attribute default value is dependent from its element, the attribute has only a default, when a parent element exists.
	 *
	 * @return <code>true</code> if {@odf.attribute presentation:transition-style} has an element parent
	 *         otherwise return <code>false</code> as undefined.
	 */
	@Override
	public boolean hasDefault() {
		return false;
	}

	/**
	 * @return Returns whether this attribute is known to be of type ID (i.e. xml:id ?)
	 */
	@Override
	public boolean isId() {
		return false;
	}
}
