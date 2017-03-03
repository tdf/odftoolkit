/************************************************************************
* 
*  Licensed to the Apache Software Foundation (ASF) under one
*  or more contributor license agreements.  See the NOTICE file
*  distributed with this work for additional information
*  regarding copyright ownership.  The ASF licenses this file
*  to you under the Apache License, Version 2.0 (the
*  "License"); you may not use this file except in compliance
*  with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing,
*  software distributed under the License is distributed on an
*  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*  KIND, either express or implied.  See the License for the
*  specific language governing permissions and limitations
*  under the License.
*
************************************************************************/
package org.odftoolkit.odfdom.type;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * This class represents the in OpenDocument format used data type {@odf.datatype color}
 * See <a href="http://www.w3.org/TR/CSS21/syndata.html#value-def-color">W3C CSS specification</a> for further details.
 */
public class Color implements OdfDataType {

	private static final Pattern sixHexRGBPattern = Pattern.compile("^#[0-9a-fA-F]{6}$");
	private static final Pattern threeHexRGBPattern = Pattern.compile("^#[0-9a-fA-F]{3}$");
	private static final Map<String, String> labeledColors = new HashMap<String, String>();
	static {
		labeledColors.put("aqua", "#00ffff");
		labeledColors.put("black", "#000000");
		labeledColors.put("blue", "#0000ff");
		labeledColors.put("fuchsia", "#ff00ff");
		labeledColors.put("gray", "#808080");
		labeledColors.put("green", "#008000");
		labeledColors.put("lime", "#00ff00");
		labeledColors.put("maroon", "#800000");
		labeledColors.put("navy", "#000080");
		labeledColors.put("olive", "#808000");
		labeledColors.put("orange", "#ffA500");
		labeledColors.put("purple", "#800080");
		labeledColors.put("red", "#ff0000");
		labeledColors.put("silver", "#c0c0c0");
		labeledColors.put("teal", "#008080");
		labeledColors.put("white", "#ffffff");
		labeledColors.put("yellow", "#ffff00");
	}
	private static final String COLOR_PREFIX = "#";
	
	private final String mColorAsSixHexRGB;
	
	/**
	* The color aqua in sRGB space.
	*/
	public static final Color AQUA = new Color("#00ffff");


	/**
	* The color black in sRGB space.
	*/
	public static final Color BLACK = new Color("#000000");


	/**
	* The color blue in sRGB space.
	*/
	public static final Color BLUE = new Color("#0000ff");


	/**
	* The color fuchsia in sRGB space.
	*/
	public static final Color FUCHSIA = new Color("#ff00ff");


	/**
	* The color gray in sRGB space.
	*/
	public static final Color GRAY = new Color("#808080");


	/**
	* The color green in sRGB space.
	*/
	public static final Color GREEN = new Color("#008000");


	/**
	* The color lime in sRGB space.
	*/
	public static final Color LIME = new Color("#00ff00");


	/**
	* The color maroon in sRGB space.
	*/
	public static final Color MAROON = new Color("#800000");


	/**
	* The color navy in sRGB space.
	*/
	public static final Color NAVY = new Color("#000080");


	/**
	* The color olive in sRGB space.
	*/
	public static final Color OLIVE = new Color("#808000");


	/**
	* The color orange in sRGB space.
	*/
	public static final Color ORANGE = new Color("#ffA500");


	/**
	* The color purple in sRGB space.
	*/
	public static final Color PURPLE = new Color("#800080");


	/**
	* The color red in sRGB space.
	*/
	public static final Color RED = new Color("#ff0000");


	/**
	* The color silver in sRGB space.
	*/
	public static final Color SILVER = new Color("#c0c0c0");


	/**
	* The color teal in sRGB space.
	*/
	public static final Color TEAL = new Color("#008080");


	/**
	* The color white in sRGB space.
	*/
	public static final Color WHITE = new Color("#ffffff");


	/**
	* The color yellow in sRGB space.
	*/
	public static final Color YELLOW = new Color("#ffff00");

	
	/**
	 * Construct Color by the parsing the given string. The string should be observed sRGB color standard 
	 * which starts with "#" and following with six numbers or three numbers in Hex format.
	 * For example, "#FFFFFF" is a valid argument and white color will be constructed. 
	 * <p>
	 * For further information on sRGB,
	 * see <A href="http://www.w3.org/pub/WWW/Graphics/Color/sRGB.html">
	 * http://www.w3.org/pub/WWW/Graphics/Color/sRGB.html
	 * </A>.
	 * 
	 * @param color  represented using the 3 or 6 HEX sRGB notation.
	 * @throws IllegalArgumentException  if the given argument is not a valid Color in sRGB HEX notation.
	 */
	public Color(String color) {
		if(!isValid(color)){
			throw new IllegalArgumentException("parameter is invalid for datatype Color");
		}
		if(color.length()==4){
			mColorAsSixHexRGB = mapColorFromThreeToSixHex(color);
		}else{
			mColorAsSixHexRGB = color;
		}
	}

	/**
	 * Construct Color using the specified red, green and blue values in the range (0 - 255).
	 * 
	 * @param red  the red component.
	 * @param green  the green component.
	 * @param blue  the blue component.
	 * @throws IllegalArgumentException  if <code>red</code>, <code>green</code> or <code>blue</code> 
	 *         are outside of the range 0 to 255, inclusive.
	 */
	public Color(int red, int green, int blue) {
		this(mapColorIntegerToString(red, green, blue));
	}
	
	/**
	 * Construct Color using the specified red, green, and blue values in the range (0.0 - 1.0).
	 * 
	 * @param red  the red component
	 * @param green  the green component
	 * @param blue  the blue component
	 * @throws IllegalArgumentException  if <code>red</code>, <code>green</code> or <code>blue</code> are
	 *             outside of the range 0.0 to 1.0, inclusive.
	 */
	public Color(float red, float green, float blue) {
		this((int) (red * 255 + 0.5), (int) (green * 255 + 0.5), (int) (blue * 255 + 0.5));
	}

	/**
	 * Construct Color using {@link java.awt.Color <code>java.awt.Color</code>}.
	 * 
	 * @param color the specified {@link java.awt.Color <code>java.awt.Color</code>}.
	 * @throws IllegalArgumentException  if the given argument is not a valid Color.
	 * @see java.awt.Color
	 */
	public Color(java.awt.Color color) {
		this(color.getRed(),color.getGreen(),color.getBlue());
	}

	/**
	 * Returns the Color in six HEX sRGB notation.
	 * format.
	 * 
	 * @return a six number hexadecimal string representation of the Color
	 */
	@Override
	public String toString() {
		return mColorAsSixHexRGB;
	}

	/**
	 * Returns a Color instance representing the specified String value.
	 * 
	 * @param colorValue a six (or three) number hexadecimal string representation of the Color
	 * @return return a Color instance representing <code>stringValue</code>.
	 * @throws IllegalArgumentException  if the given argument is not a valid Color.
	 */
	public static Color valueOf(String colorValue) {
		return new Color(colorValue);
	}

	/**
	 * Check if the specified String is a valid {@odf.datatype color} data type.
	 * 
	 * @param colorValue  a six (or three) number hexadecimal string representation of the Color
	 * @return true if the value of argument is valid for{@odf.datatype color} data type false otherwise.
	 */
	public static boolean isValid(String colorValue) {
		if ((colorValue == null) || !(threeHexRGBPattern.matcher(colorValue).matches() || sixHexRGBPattern.matcher(colorValue).matches())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Convert RGB color formats to six-digit hex RGB format. 
	 * 
	 * The RGB mapping works as follows:	 
	 * rgb(110%, 0%, 0%)----clipped to rgb(100%,0%,0%), return #ff0000  
	 * maroon----one of the seventeen fixed labeled numbers, return #800000  
	 * #ff0000----six-digit notation #rrggbb, returns the input
	 * #f00----three-digit notation #rgb, return #ff0000
	 * rgb(255,0,0)----integer range 0 - 255, return #ff0000
	 * rgb(300,0,0)----clipped to rgb(255,0,0), return #ff0000
	 * rgb(255,-10,0)----clipped to rgb(255,0,0), return #ff0000 
	 * 
	 * @param colorValue  The sRGB color value to be converted.
	 * @return the converted color.
	 */
	public static String toSixDigitHexRGB(String colorValue){
		if (colorValue == null) {
			throw new IllegalArgumentException("parameter should not be null.");
		} else {
			colorValue = colorValue.toLowerCase().trim();
			if (sixHexRGBPattern.matcher(colorValue).matches()) {
				// 6-digit notation #rrggbb - return itself.
				return colorValue;
			} else if (threeHexRGBPattern.matcher(colorValue).matches()) {
				// convert 3-digit notation #rgb.
				return mapColorFromThreeToSixHex(colorValue);
			} else if (colorValue.startsWith("rgb")) {
				colorValue = colorValue.substring(3);
				colorValue = colorValue.substring(colorValue.indexOf("(") + 1,
						colorValue.indexOf(")"));
				String[] rgbValues = colorValue.split(",");
				if (rgbValues.length == 3) {
					int r = mapColorValueToInteger(rgbValues[0].trim());
					int g = mapColorValueToInteger(rgbValues[1].trim());
					int b = mapColorValueToInteger(rgbValues[2].trim());
					String rs = Integer.toHexString(r);
					String gs = Integer.toHexString(g);
					String bs = Integer.toHexString(b);
					String hexColor = COLOR_PREFIX;
					if (r < 16) {
						hexColor += "0";
					}
					hexColor += rs;
					if (g < 16) {
						hexColor += "0";
					}
					hexColor += gs;
					if (b < 16) {
						hexColor += "0";
					}
					hexColor += bs;
					return hexColor;
				}else{
					throw new IllegalArgumentException("parameter: "+ colorValue + " can't be converted six-digit hex RGB.");
				}
			} else {
				// convert the seventeen fixed labeled numbers.
				String hexColor = labeledColors.get(colorValue);
				if (hexColor != null) {
					return hexColor;
				} else {
					throw new IllegalArgumentException("parameter: "+ colorValue + " can't be converted six-digit hex RGB.");
				}
			}
		}
	}

	/**
	 * Return the corresponding {@link java.awt.Color <code>java.awt.Color</code>} instance of the Color data type.
	 * 
	 * @return the converted {@link java.awt.Color <code>java.awt.Color</code>} instance..
	 */
	public java.awt.Color getAWTColor(){
		return mapColorToAWTColor(this);
	}
	
	/**
	 * Map a Color data type to {@link java.awt.Color <code>java.awt.Color</code>}. 
	 * 
	 * @param color  The color data type to be mapped..
	 * @return the converted {@link java.awt.Color <code>java.awt.Color</code>} instance.
	 */
	public static java.awt.Color mapColorToAWTColor(Color color) {
		int rgb = Integer.decode("0x" + color.mColorAsSixHexRGB.substring(1));
		return new java.awt.Color(rgb);
	}
	
	/**
	 * Converts Color expressed by red, green and blue values in the range (0 - 255) to 
	 * a string format which is used in {@odf.datatype color}.
	 * 
	 * @param red  the red component.
	 * @param green  the green component.
	 * @param blue  the blue component.
	 * @return the string format color.
	 */
	private static String mapColorIntegerToString(int red, int green, int blue) {
		String rs = Integer.toHexString(red);
		String gs = Integer.toHexString(green);
		String bs = Integer.toHexString(blue);
		String hexColor = COLOR_PREFIX;
		if (red < 16) {
			hexColor += "0";
		}
		hexColor += rs;
		if (green < 16) {
			hexColor += "0";
		}
		hexColor += gs;
		if (blue < 16) {
			hexColor += "0";
		}
		hexColor += bs;
		return hexColor;
	}
	
	/**
	 * Converts Color from three-digit to six-digit form. The three-digit (#rgb) is converted into six-digit form (#rrggbb) by replicating digits, 
	 * not by adding zeros. For example, #fb0 expands to #ffbb00. 
	 * 	 * 
	 * @param threeDigitcColor  the three-digit color form.
	 * @return the six-digit color form.
	 */
	private static String mapColorFromThreeToSixHex(String threeDigitcColor) {
		char[] colorData = threeDigitcColor.toCharArray();
		char[] sixDigitColor = new char[7];
		for (int i = 0; i < 7; i++) {
			sixDigitColor[i] = colorData[(i + 1) / 2];
		}
		return new String(sixDigitColor);
	}

	/** Convert percent string and integer string to integer.
		value range will be checked and adapted to border (error correction) */
	private static int mapColorValueToInteger(String colorValue) {
		if (colorValue.endsWith("%")) {
			colorValue = colorValue.substring(0, colorValue.indexOf("%"));
			int value = Integer.parseInt(colorValue);
			if (value < 0) {
				value = 0;
			}
			if (value > 100) {
				value = 100;
			}
			return 255 * value / 100;
		} else {
			int value = Integer.parseInt(colorValue);
			if (value < 0) {
				value = 0;
			}
			if (value > 255) {
				value = 255;
			}
			return value;
		}
	}

        
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Color other = (Color) obj;
        if ((this.mColorAsSixHexRGB == null) ? (other.mColorAsSixHexRGB != null) : !this.mColorAsSixHexRGB.equals(other.mColorAsSixHexRGB)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.mColorAsSixHexRGB != null ? this.mColorAsSixHexRGB.hashCode() : 0);
        return hash;
    }
        
}
