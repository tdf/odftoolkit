/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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


package org.openoffice.odf.dom.util;

import java.util.Locale;

/**
 *
 */
public class OdfUnitConverter
{
    
    public static final double DPI = 127; // as 0.02 cm minimum border is taken as one pixel
    public static final double CENTIMETER_IN_MM = 10;
    public static final double INCH_IN_MM = 25.4;
    public static final double DIDOT_POINT_IN_MM = 0.376065;
    public static final double PICA_IN_MM = 4.2333333;
    public static final double POINT_IN_MM = 0.3527778;
    public static final double TWIP_IN_MM = 0.017636684;
    public static final double PIXEL_IN_MM = INCH_IN_MM / DPI;

    // 2DO: Take mapUnitToCm as template and move functions into measurement class
    /**
     * 
     * @param length    The value to be mapped
     * @return          The converted value
     */
    public static String mapUnitToCm(String length) {
        // used to archieve decimal places
        double roundingFactor = 10000.0;
        double unit = 0;
        String cmValue = null;
        try {
            if (length.contains("mm")) {
                Double mm = Double.valueOf(length.substring(0, length.indexOf("mm")));
                unit = Math.round(roundingFactor * mm / CENTIMETER_IN_MM) / roundingFactor;

            } else if (length.contains("cm")) {
                unit = Double.valueOf(length.substring(0, length.indexOf("cm")));

            } else if (length.contains("inch")) {
                Double inch = Double.valueOf(length.substring(0, length.indexOf("inch")));
                unit = Math.round(roundingFactor * inch / CENTIMETER_IN_MM * INCH_IN_MM) / roundingFactor;

            } else if (length.contains("in")) {
                Double in = Double.valueOf(length.substring(0, length.indexOf("in")));
                unit = Math.round(roundingFactor * in / CENTIMETER_IN_MM * INCH_IN_MM) / roundingFactor;

            } else if (length.contains("pt")) {
                Double pt = Double.valueOf(length.substring(0, length.indexOf("pt")));
                unit = Math.round(roundingFactor * pt / CENTIMETER_IN_MM * POINT_IN_MM) / roundingFactor;

            } else if (length.contains("dpt")) {
                Double dpt = Double.valueOf(length.substring(0, length.indexOf("dpt")));
                unit = Math.round(roundingFactor * dpt / CENTIMETER_IN_MM * DIDOT_POINT_IN_MM) / roundingFactor;

            } else if (length.contains("pica")) {
                Double pica = Double.valueOf(length.substring(0, length.indexOf("pica")));
                unit = Math.round(roundingFactor * pica / CENTIMETER_IN_MM * PICA_IN_MM) / roundingFactor;

            } else if (length.contains("twip")) {
                Double twip = Double.valueOf(length.substring(0, length.indexOf("twip")));
                unit = Math.round(roundingFactor * twip / CENTIMETER_IN_MM * TWIP_IN_MM) / roundingFactor;

            } else if (length.contains("px")) {
                Double pixel = Double.valueOf(length.substring(0, length.indexOf("px")));
                unit = Math.round(roundingFactor * pixel / CENTIMETER_IN_MM * PIXEL_IN_MM) / roundingFactor;

            } else {
                // assume no unit measurement and asume "px"
                unit = Math.round(roundingFactor * (Double.valueOf(length) / CENTIMETER_IN_MM * PIXEL_IN_MM)) / roundingFactor;
            }
            cmValue = String.valueOf(unit) + "cm";
        } catch (NumberFormatException e) {
            // 2DO: Let only the superclass have one LOGGER and use that
            System.err.println("Exception" + e);
            cmValue = length;
        }
        return cmValue;
    }

    /** Value have to be trimmed (no spaces around) and lower caser
     * @param colorValue The color to be converted
     * @return the converted color
     */
    public static String mapColorFromRgbToHex(String colorValue) {
        if (colorValue.startsWith("rgb")) {
            colorValue = colorValue.replace("rgb", "");
            colorValue = colorValue.replace("(", "");
            colorValue = colorValue.replace(")", "");
            String[] rgbValues = colorValue.split(",");
            if (rgbValues.length == 3) {
                int r = Integer.parseInt(rgbValues[0].trim());
                int g = Integer.parseInt(rgbValues[1].trim());
                int b = Integer.parseInt(rgbValues[2].trim());
                String rs = Integer.toHexString(r);
                String gs = Integer.toHexString(g);
                String bs = Integer.toHexString(b);
                String hexColor = "#";

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
            }
        }
        return colorValue;
    }    

    private static boolean isMeasureNumChar( char c )
    {
        return ( c >= '0' && c <= '9' ) || c == '-' || c == '.';
    }
    
    /** Get a 1/100th millimeter value from the measure string
     */    
    public final static int getMeasureValue( String measure )
    {
        if (measure == null)
            return 0;

        // number
        int valEnd = 0;
        while ( valEnd < measure.length() &&
                isMeasureNumChar(measure.charAt(valEnd)) )
            ++valEnd;
        String valStr = measure.substring(0, valEnd);
        double value = Double.parseDouble(valStr);

        // optional spaces
        int spaceEnd = valEnd;
        while ( spaceEnd < measure.length() &&
                measure.charAt(spaceEnd) == ' ' )
            ++spaceEnd;
        
        // unit
        String unitStr = measure.substring(spaceEnd).toLowerCase(Locale.ENGLISH);
        double factor = 0.0;
        if ( unitStr.equals("cm") )
            factor = 1000.0;                // cm -> 1/100mm
        else if ( unitStr.equals("mm") )
            factor = 100.0;                 // mm -> 1/100mm
        else if ( unitStr.equals("in") )
            factor = 2540.0;                // in -> 1/100mm
        else if ( unitStr.equals("pt") )
            factor = 2540.0 / 72.0;         // pt -> 1/100mm
                
        return (int)(value * factor + 0.5);        
    }
    
    /** returns the xml value string for the given measure (position,distance,length)
     * 
     * @param measure in 1/100th mm
     * @return odf convorm measure string using cm as unit
     */
    public final static String getMeasureString( int measure )
    {        
        return new Double((double)measure / (double)1000.0 ).toString() + "cm";
    }
}
