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
	public static enum SimpleLineStyle {
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

		SimpleLineStyle(String value) {
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
	public static enum SimpleFontStyle {
		REGULAR("Regular"), ITALIC("Italic"), BOLD("Bold"), BOLDITALIC("Bold_Italic");

		private String fontStyle;

		SimpleFontStyle(String style) {
			this.fontStyle = style;
		}

		@Override
		public String toString() {
			return fontStyle;
		}
	}

	/**
	 * Common used style of line lining through text from users perspective
	 * 
	 */
	public static enum SimpleLineThroughStyle {
		NONE("none"), SINGLE("single"), DOUBLE("double"), BOLD("bold"), WITH_X("with X"), WITH_SLASH("with /");

		private String value;

		SimpleLineThroughStyle(String style) {
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
	public static enum SimpleHorizontalAlignmentType {
		DEFAULT("default"), LEFT("left"), RIGHT("right"), CENTER("center"), JUSTIFIED("justified"), FILLED("filled");

		private String value;

		SimpleHorizontalAlignmentType(String style) {
			this.value = style;
		}

		public static SimpleHorizontalAlignmentType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (SimpleHorizontalAlignmentType aIter : values()) {
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
	public static enum SimpleVerticalAlignmentType {
		DEFAULT("default"), TOP("top"), MIDDLE("middle"), BOTTOM("bottom");

		private String value;

		SimpleVerticalAlignmentType(String style) {
			this.value = style;
		}

		public static SimpleVerticalAlignmentType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (SimpleVerticalAlignmentType aIter : values()) {
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
	public static enum EmphasisMarkType {
		NONE("none"), ACCENT_ABOVE("accent above"), DOT_ABOVE("dot above"), CIRCLE_ABOVE("circle above"), DISC_ABOVE(
				"disc above"), ACCENT_BELOW("accent below"), DOT_BELOW("dot below"), CIRCLE_BELOW("circle below"), DISC_BELOW(
				"disc below");

		private String value;

		EmphasisMarkType(String style) {
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
	public static enum LineStyle {
		DASH("dash"), DOT_DASH("dot-dash"), DOT_DOT_DASH("dot-dot-dash"), DOTTED("dotted"), LONG_DASH("long-dash"), NONE(
				"none"), SOLID("solid"), WAVE("wave");

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
	public static enum SimpleCellBordersType {
		TOP("top"), BOTTOM("bottom"), LEFT("left"), RIGHT("right"), DIAGONALBLTR("diagonal_bltr"), DIAGONALTLBR(
				"diagonal_tlbr"), NONE("none"), ALL_FOUR("all_four"), LEFT_RIGHT("left_right"), TOP_BOTTOM("top_bottom"), DIAGONAL_LINES(
				"diagonal_lines");

		private String value;

		SimpleCellBordersType(String style) {
			this.value = style;
		}

		public static SimpleCellBordersType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return null;

			for (SimpleCellBordersType aIter : values()) {
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
	public static enum LineWidth {
		AUTO("auto"), BOLD("bold"), MEDIUM("medium"), NORMAL("normal"), THICK("thick"), THIN("thin");

		private String lineWidth;

		LineWidth(String type) {
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
	public static enum FontStyle {
		ITALIC("italic"), NORMAL("normal"), OBLIQUE("oblique");

		private String value;

		FontStyle(String style) {
			this.value = style;
		}

		public static FontStyle enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NORMAL;

			for (FontStyle aIter : values()) {
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
	public static enum FontWeight {
		_100("100"), _200("200"), _300("300"), _400("400"), _500("500"), _600("600"), _700("700"), _800("800"), _900(
				"900"), BOLD("bold"), NORMAL("normal");
		private String value;

		FontWeight(String style) {
			this.value = style;
		}

		public static FontWeight enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NORMAL;

			for (FontWeight aIter : values()) {
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
