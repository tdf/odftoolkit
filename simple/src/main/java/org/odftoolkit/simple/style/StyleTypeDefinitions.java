/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple.style;

/**
 * This class defines the common used types in style handling methods.
 * 
 * @since 0.3
 */
public class StyleTypeDefinitions {

	private StyleTypeDefinitions() {

	}

	/**
	 * Common used line style from users perspective
	 * 
	 */
	public static enum LineStyle {
		/**
		 * no border
		 */
		NONE("none"),
		/**
		 * solid line
		 */
		SOLID("solid"),
		/**
		 * single line
		 */
		SINGLE("single"),
		/**
		 * double line
		 */
		DOUBLE("double"),
		/**
		 * bold line
		 */
		BOLD("bold"), DOTTED("dotted"), DOTTED_BOLD("dotted (bold)"), DASH("dash"), DASH_BOLD("dash (bold)"), LONG_DASH(
				"long-dash"), LONG_DASH_BOLD("long-dash (bold)"), DOT_DASH("dot-dash"), DOT_DASH_BOLD("dot-dash (bold)"), DOT_DOT_DASH(
				"dot-dot-dash"), DOT_DOT_DASH_BOLD("dot-dot-dash (bold)"), WAVE("wave"), WAVE_BOLD("wave (bold)"), DOUBLE_WAVE(
				"double wave");

		private String lineStyle;

		LineStyle(String value) {
			lineStyle = value;
		}

		@Override
		public String toString() {
			return lineStyle;
		}

	}

	/**
	 * Common used font style from users perspective
	 * 
	 */
	public static enum FontStyle {
		REGULAR("Regular"), ITALIC("Italic"), BOLD("Bold"), BOLDITALIC("Bold_Italic");

		private String fontStyle;

		FontStyle(String style) {
			this.fontStyle = style;
		}

		@Override
		public String toString() {
			return fontStyle;
		}
	}
	
	/**
	 * Common used font text line position from users perspective.
	 * <p>Currently, only support underline and strike through.
	 * 
	 */
	public static enum TextLinePosition {
		REGULAR("Regular"), THROUGH("Through"), UNDER("Under"), THROUGHUNDER("Through_Under");

		private String textLineStyle;

		TextLinePosition(String style) {
			this.textLineStyle = style;
		}

		@Override
		public String toString() {
			return textLineStyle;
		}
	}

	/**
	 * Common used style of line lining through text from users perspective
	 * 
	 */
	public static enum LineThroughStyle {
		NONE("none"), SINGLE("single"), DOUBLE("double"), BOLD("bold"), WITH_X("with X"), WITH_SLASH("with /");

		private String value;

		LineThroughStyle(String style) {
			this.value = style;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Common used horizontal alignment type from users perspective
	 * 
	 */
	public static enum HorizontalAlignmentType {
		DEFAULT("default"), LEFT("left"), RIGHT("right"), CENTER("center"), JUSTIFIED("justified"), FILLED("filled");

		private String value;

		HorizontalAlignmentType(String style) {
			this.value = style;
		}

		public static HorizontalAlignmentType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (HorizontalAlignmentType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Horizontal Alignment Type!");
		}

		@Override
		public String toString() {
			return value;
		}

		public String getAlignmentString() {
			if (value.equals("left"))
				return "start";
			if (value.equals("right"))
				return "end";
			if (value.equals("filled"))
				return "start";
			return value;
		}
	}

	/**
	 * Common used vertical alignment type from users perspective
	 * 
	 */
	public static enum VerticalAlignmentType {
		DEFAULT("default"), TOP("top"), MIDDLE("middle"), BOTTOM("bottom");

		private String value;

		VerticalAlignmentType(String style) {
			this.value = style;
		}

		public static VerticalAlignmentType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (VerticalAlignmentType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Vertical Alignment Type!");
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Emphasis mark type from ODF specification perspective
	 * 
	 */
	public static enum OdfEmphasisMarkType {
		NONE("none"), ACCENT_ABOVE("accent above"), DOT_ABOVE("dot above"), CIRCLE_ABOVE("circle above"), DISC_ABOVE(
				"disc above"), ACCENT_BELOW("accent below"), DOT_BELOW("dot below"), CIRCLE_BELOW("circle below"), DISC_BELOW(
				"disc below");

		private String value;

		OdfEmphasisMarkType(String style) {
			this.value = style;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Line style from ODF specification perspective
	 * 
	 */
	public static enum OdfLineStyle {
		DASH("dash"), DOT_DASH("dot-dash"), DOT_DOT_DASH("dot-dot-dash"), DOTTED("dotted"), LONG_DASH("long-dash"), NONE(
				"none"), SOLID("solid"), WAVE("wave");

		private String lineStyle;

		OdfLineStyle(String value) {
			lineStyle = value;
		}

		@Override
		public String toString() {
			return lineStyle;
		}
	}

	/**
	 * The supported line measurement till now
	 * 
	 */
	public static enum SupportedLinearMeasure {
		PT("pt") {
			public double toINs(double measure) {
				return measure / 72;
			}

			public double toPTs(double measure) {
				return measure;
			}

			public double convert(double measure, SupportedLinearMeasure measureUnit) {
				return measureUnit.toPTs(measure);
			}
		},
		IN("in") {
			public double toINs(double measure) {
				return measure;
			}

			public double toPTs(double measure) {
				return 72 * measure;
			}

			public double convert(double measure, SupportedLinearMeasure measureUnit) {
				return measureUnit.toINs(measure);
			}
		};
		// CM("cm")
		// MM("mm");

		private String value;

		SupportedLinearMeasure(String style) {
			this.value = style;
		}

		/**
		 * Convert the given linear measure in the given unit to this unit.
		 * 
		 * @param measure
		 *            the measure value in the given <code>measureUnit</code>
		 * @param measureUnit
		 *            the unit of the <code>measure</code> argument
		 * @return the converted measure in this unit.
		 */
		public double convert(double measure, SupportedLinearMeasure measureUnit) {
			throw new AbstractMethodError();
		}

		/**
		 * Convert other measure to inch(IN) measure.
		 * 
		 * @param measure
		 *            the measure
		 * @return the converted measure
		 * @see #convert
		 */
		public double toINs(double measure) {
			throw new AbstractMethodError();
		}

		/**
		 * Convert other measure to point(PT) measure.
		 * 
		 * @param measure
		 *            the measure
		 * @return the converted measure
		 * @see #convert
		 */
		public double toPTs(double measure) {
			throw new AbstractMethodError();
		}

		public static SupportedLinearMeasure enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (SupportedLinearMeasure aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Linear Measure!");
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * 
	 * Common used border types from users perspective
	 * 
	 */
	public static enum CellBordersType {
		TOP("top"), BOTTOM("bottom"), LEFT("left"), RIGHT("right"), DIAGONALBLTR("diagonal_bltr"), DIAGONALTLBR(
				"diagonal_tlbr"), NONE("none"), ALL_FOUR("all_four"), LEFT_RIGHT("left_right"), TOP_BOTTOM("top_bottom"), DIAGONAL_LINES(
				"diagonal_lines");

		private String value;

		CellBordersType(String style) {
			this.value = style;
		}

		public static CellBordersType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (CellBordersType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			throw new RuntimeException("Unsupported Cell Borders Type!");
		}

		@Override
		public String toString() {
			return value;
		}

	}

	/**
	 * Line type from ODF specification perspective
	 * 
	 */
	public static enum LineType {
		DOUBLE("double"), NONE("none"), SINGLE("single");

		private String lineType;

		LineType(String type) {
			this.lineType = type;
		}

		public static LineType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NONE;

			for (LineType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return NONE;
		}

		@Override
		public String toString() {
			return lineType;
		}
	}

	/**
	 * Line width from ODF specification perspective
	 * 
	 */
	public static enum OdfLineWidth {
		AUTO("auto"), BOLD("bold"), MEDIUM("medium"), NORMAL("normal"), THICK("thick"), THIN("thin");

		private String lineWidth;

		OdfLineWidth(String type) {
			this.lineWidth = type;
		}

		@Override
		public String toString() {
			return lineWidth;
		}
	}

	/**
	 * Font style from ODF specification perspective
	 * 
	 */
	public static enum OdfFontStyle {
		ITALIC("italic"), NORMAL("normal"), OBLIQUE("oblique");

		private String value;

		OdfFontStyle(String style) {
			this.value = style;
		}

		public static OdfFontStyle enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NORMAL;

			for (OdfFontStyle aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return NORMAL;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	/**
	 * Font weight from ODF specification perspective
	 * 
	 */
	public static enum OdfFontWeight {
		_100("100"), _200("200"), _300("300"), _400("400"), _500("500"), _600("600"), _700("700"), _800("800"), _900(
				"900"), BOLD("bold"), NORMAL("normal");
		private String value;

		OdfFontWeight(String style) {
			this.value = style;
		}

		public static OdfFontWeight enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NORMAL;

			for (OdfFontWeight aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return NORMAL;
		}

		@Override
		public String toString() {
			return value;
		}
	}

}
