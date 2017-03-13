/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

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
	 * Common used font adjustment style from users perspective
	 */
	public static enum AdjustmentStyle {
		CENTER("center"), LEFT("left"), RIGHT("right");

		private String adjustment = "left";

		AdjustmentStyle(String value) {
			this.adjustment = value;
		}

		public String toString() {
			return adjustment;
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
		DEFAULT("default"), LEFT("left"), RIGHT("right"), CENTER("center"), JUSTIFY("justify"), FILLED("filled");

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
		//1in = 2.54cm = 25.4 mm = 72pt = 6pc
		PT("pt") {
			public double toINs(double measure) {
				return measure / 72;
			}

			public double toPTs(double measure) {
				return measure;
			}
			
			public double toCMs(double measure) {
				return measure / 28.3465;
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

			public double toCMs(double measure) {
				return 2.54 * measure;
			}
			
			public double convert(double measure, SupportedLinearMeasure measureUnit) {
				return measureUnit.toINs(measure);
			}
		},
		CM("cm") {
			public double toINs(double measure) {
				return measure / 2.54;
			}

			public double toPTs(double measure) {
				return measure * 28.3465;
			}
			
			public double toCMs(double measure) {
				return measure;
			}
			
			public double convert(double measure, SupportedLinearMeasure measureUnit) {
				return measureUnit.toPTs(measure);
			}
		};
//		MM("mm") {
//			
//		}

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
		
		/**
		 * Convert other measure to centimeter(CM) measure.
		 * 
		 * @param measure
		 *            the measure
		 * @return the converted measure
		 * @see #convert
		 */
		public double toCMs(double measure) {
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
	 * Anchor type from users perspective.
	 * 
	 * @since 0.5.5
	 */
	public static enum AnchorType {
		TO_PAGE("page"), 
		TO_PARAGRAPH("paragraph"), 
		TO_CHARACTER("char"),
		AS_CHARACTER("as-char"),
		TO_FRAME("frame");

		private String anchorType;

		AnchorType(String type) {
			this.anchorType = type;
		}

		public static AnchorType enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return TO_PAGE;

			for (AnchorType aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return TO_PAGE;
		}

		@Override
		public String toString() {
			return anchorType;
		}
	}

	/**
	 * Position vertical relative from ODF specification perspective
	 * 
	 * @since 0.5.5
	 */
	public static enum VerticalRelative
	{
		PAGE("page"), 
		PAGE_CONTENT("page-content"),
		FRAME("frame"),
		FRAME_CONTENT("frame-content"),
		PARAGRAPH("paragraph"),
		PARAGRAPH_CONTENT("paragraph-content"),
		CHAR("char"),
		LINE("line"),
		BASELINE("baseline"),
		TEXT("text");

		private String relativeType;

		VerticalRelative(String type) {
			this.relativeType = type;
		}

		public static VerticalRelative enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return PAGE;

			for (VerticalRelative aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return PAGE;
		}

		@Override
		public String toString() {
			return relativeType;
		}
	}
	
	/**
	 * Position horizontal relative from ODF specification perspective
	 * @since 0.5.5
	 */
	public static enum HorizontalRelative
	{
		PAGE("page"), 
		PAGE_CONTENT("page-content"),
		FRAME("frame"),
		FRAME_CONTENT("frame-content"),
		PARAGRAPH("paragraph"),
		PARAGRAPH_CONTENT("paragraph-content"),
		CHAR("char"),
		PAGE_START_MARGIN("page-start-margin"),
		PAGE_END_MARGIN("page-end-margin"),
		FRAME_START_MARGIN("frame-start-margin"),
		FRAME_END_MARGIN("frame-end-margin"),
		PARAGRAPH_START_MARGIN("paragraph-start-margin"),
		PARAGRAPH_END_MARGIN("paragraph-end-margin");

		private String relativeType;

		HorizontalRelative(String type) {
			this.relativeType = type;
		}

		public static HorizontalRelative enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return PAGE;

			for (HorizontalRelative aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return PAGE;
		}

		@Override
		public String toString() {
			return relativeType;
		}
	}
	
	/**
	 * specifies the vertical alignment of a frame relative to a specific area.
	 * 
	 * @since 0.5.5
	 */
	public static enum FrameVerticalPosition
	{
		TOP("top"),
		MIDDLE("middle"),
		BOTTOM("bottom"),
		FROMTOP("from-top"),
		BELOW("below");

		private String verticalPos;

		FrameVerticalPosition(String type) {
			this.verticalPos = type;
		}

		public static FrameVerticalPosition enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return MIDDLE;

			for (FrameVerticalPosition aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return MIDDLE;
		}

		@Override
		public String toString() {
			return verticalPos;
		}
	}
	
	/**
	 * specifies the horizontal alignment of a frame relative to a specific area.
	 * 
	 * @since 0.5.5
	 */
	public static enum FrameHorizontalPosition
	{
		LEFT("left"),
		CENTER("center"),
		RIGHT("right"),
		FROMLEFT("from-left"),
		INSIDE("inside"),
		OUTSIDE("outside"),
		FROMINSIDE("from-inside");

		private String horizontalPos;

		FrameHorizontalPosition(String type) {
			this.horizontalPos = type;
		}

		public static FrameHorizontalPosition enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return CENTER;

			for (FrameHorizontalPosition aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return CENTER;
		}

		@Override
		public String toString() {
			return horizontalPos;
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
	
	/**
	 * 
	 * the fill style for a graphic object. 
	 *
	 */
	public static enum OdfDrawFill {
		/**
		 * the drawing object is filled with the bitmap specified by the draw:fill-image-name attribute.
		 */
		BITMAP("bitmap"),
		/**
		 * the drawing object is filled with the gradient specified by the draw:fill-gradient-name attribute.
		 */
		GRADIENT("gradient"),
		/**
		 * the drawing object is filled with the hatch specified by the draw:fill-hatch-name attribute. 
		 */
		HATCH("hatch"),
		/**
		 * the drawing object is not filled.
		 */
		NONE("none"),
		/**
		 * the drawing object is filled with the color specified by the draw:fill-color attribute.
		 */
		SOLID("solid");  		

		private String value;

		OdfDrawFill(String style) {
			this.value = style;
		}

		public static OdfDrawFill enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NONE;

			for (OdfDrawFill aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return NONE;
		}

		@Override
		public String toString() {
			return value;
		}
	}
	
	/**
	 * 
	 * The style of the stroke from ODF perspective
	 *
	 */
	public static enum OdfDrawStroke {
		/**
		 * stroke referenced by a draw:stroke-dash attribute of a style on the object is drawn
		 */
		DASH("dash"),
		/**
		 * no stroke is drawn.
		 */
		NONE("none"),
		/**
		 * solid stroke is drawn.
		 */
		SOLID("solid");
		
		private String value;

		OdfDrawStroke(String style) {
			this.value = style;
		}

		public static OdfDrawStroke enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return NONE;

			for (OdfDrawStroke aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return NONE;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static enum PrintOrientation {
		LANDSCAPE("landscape"), PORTRAIT("portrait");

		private String printOrientaiton;

		PrintOrientation(String orientation) {
			this.printOrientaiton = orientation;
		}

		public static PrintOrientation enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return PORTRAIT;

			for (PrintOrientation aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return PORTRAIT;
		}

		@Override
		public String toString() {
			return printOrientaiton;
		}
	}

	public static enum WritingMode {
		LRTB("lr-tb"), RLTB("rl-tb"), TBRL("tb-rl"), TBLR("tb-lr"), LR("lr"), RL(
				"rl"), TB("tb"), PAGE("page");

		private String mode;

		WritingMode(String mode) {
			this.mode = mode;
		}

		public static WritingMode enumValueOf(String aValue) {
			if ((aValue == null) || (aValue.length() == 0))
				return PAGE;

			for (WritingMode aIter : values()) {
				if (aValue.equals(aIter.toString())) {
					return aIter;
				}
			}
			return PAGE;
		}

		@Override
		public String toString() {
			return mode;
		}
	}

}
