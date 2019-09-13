/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.changes;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.number.DataStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopsElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfStylesBase;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.odftoolkit.odfdom.type.Length;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MapHelper {

    public static final String AUTO = "auto";
    public static final String NORMAL = "normal";
    public static final String BOLD = "bold";
    public static final String THIN = "thin";
    public static final String MEDIUM = "medium";
    public static final String THICK = "thick";
    public static final String HASH = "#";
    public static final String TRANSPARENT = "transparent";
    private static final String PERCENT = "%";
    private static final Logger LOG = Logger.getLogger(JsonOperationProducer.class.getName());
    private static Map<Integer, String> languageToLocaleMap = null;
    private static Map<String, Integer> localeToLanguageMap = null;
    // a color, which type is set to auto - adapting color to environment
    private static final Map<String, String> COLOR_MAP_AUTO = MapHelper.createColorMap(MapHelper.AUTO);

    /**
     * map odf border strings to JSON border object see Changes API Border
     */
    public static JSONObject createBorderMap(String borderValue) {
        JSONObject border = new JSONObject();
        try {
            if (borderValue.equals("none")) {
                border.put("style", "none");
            } else {
                String[] tokens = borderValue.split("\\s+");
                boolean checkedColor = false;
                boolean checkedStyle = false;
                boolean checkedWidth = false;
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    if (!token.isEmpty()) {
                        boolean isTokenTaken = false;
                        if (!checkedColor) {
                            if (isColor(token)) {
                                checkedColor = mapColor(border, token);
                                isTokenTaken = checkedColor;
                            }
                        }
                        if (!isTokenTaken && !checkedStyle) {
                            checkedStyle = mapStyle(border, token);
                            isTokenTaken = checkedStyle;
                        }
                        if (!isTokenTaken && !checkedWidth) {
                            checkedWidth = mapWidth(border, token, tokens);

                        }
                    }
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(JsonOperationProducer.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return border;
    }

    public static boolean isColor(String color) {
        return color.startsWith(HASH) || color.equals(TRANSPARENT);
    }

    public static boolean mapColor(JSONObject border, String width) {
        boolean isColor = false;
        try {
            // try if the first token is a width
            border.put("color", createColorMap(width));

        } catch (JSONException ex) {
            Logger.getLogger(JsonOperationProducer.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        isColor = true;

        return isColor;
    }

    public static boolean mapStyle(JSONObject border, String style) throws JSONException {
        boolean isStyle = false;
        //solid The border is a solid line
        //groove    The border looks like it were carved into the canvas
        //ridge The border "comes out" of the canvas
        if (style.equals("solid") || style.equals("groove") || style.equals("ridge")) {
            style = "single";
            border.put("style", style);
            isStyle = true;

            // hidden   A hidden border
        } else if (style.equals("hidden")) {
            style = "none";
            border.put("style", style);
            isStyle = true;

        } else if (style.equals("double") || style.equals("dotted") || style.equals("dashed") || style.equals("outset") || style.equals("inset")) {
            border.put("style", style);
            isStyle = true;
        } else if (style.contains("-dash")) {
            border.put("style", "dashed");
            isStyle = true;
        } else if (style.contains("-dot")) {
            border.put("style", "dotted");
            isStyle = true;
        } else if (style.contains("double")) {
            border.put("style", "double");
            isStyle = true;
        }
        return isStyle;
    }

    /**
     * see Changes API Color
     */
    public static Map<String, String> createColorMap(String rgbValue) {
        Map color = new HashMap<String, String>();
        if (rgbValue.contains(HASH)) {
            color.put("type", "rgb");
            rgbValue = rgbValue.subSequence(rgbValue.indexOf('#') + 1, rgbValue.length()).toString();
            color.put("value", rgbValue);
        } else if (rgbValue.equals(TRANSPARENT) || rgbValue.equals(AUTO)) {
            color.put("type", AUTO);
        }
        return color;
    }

    /**
     * <define name="lineWidth">
     * <choice>
     * <value>auto</value>
     * <value>normal</value>
     * <value>bold</value>
     * <value>thin</value>
     * <value>medium</value>
     * <value>thick</value>
     * <ref name="positiveInteger"/>
     * <ref name="percent"/>
     * <ref name="positiveLength"/>
     * </choice>
     * </define>
     */
    public static boolean mapWidth(JSONObject border, String widthString, String[] tokens) {
        boolean isWidth = false;
        int width;
        try {
            if (widthString.equals(AUTO)) {
                width = 26;// my guess
            } else if (widthString.equals(NORMAL)) {
                width = 27;// my guess
            } else if (widthString.equals(BOLD)) {
                width = 54;// my guess
            } else if (widthString.equals(THIN)) {
                width = 26;
            } else if (widthString.equals(MEDIUM)) {
                width = 53;
            } else if (widthString.equals(THICK)) {
                width = 79;
            } else {
                width = normalizeLength(widthString);
            }
            // try if the first token is a width
            border.put("width", width);
            isWidth = true;
        } catch (Throwable t) {
            // there have to a a width with three tokens
            if (tokens.length == 3) {
                throw new RuntimeException(t);
            }
        }
        return isWidth;
    }

    /**
     * Currently the normalized length is 100th millimeter (or 10 micrometer)
     */
    public static int normalizeLength(String value) {
        Length length = new Length(value);
        return (int) Math.round(length.getMicrometer() / 10.0);
    }

    public static JSONObject mapProperties(String styleFamilyGroup, Map<String, String> odfProps) {
        JSONObject newProps = null;
        JSONObject shapeProps = null;
        JSONObject imageProps = null;
        JSONObject lineProps = null;
        JSONObject fillProps = null;
        if (odfProps != null) {

            // ToDo: Recycle the maps... (??)
            newProps = new JSONObject();
            String propValue;

            // *** TEXT STYLES ***
            if (styleFamilyGroup.equals("character")) {
                boolean borderToBeDone = true;
                boolean paddingToBeDone = true;
                boolean marginToBeDone = true;
                for (String propName : odfProps.keySet()) {
                    try {
                        if (propName.contains("margin")) {
                            if (marginToBeDone) {
                                mapMargin(newProps, odfProps);
                                marginToBeDone = false;
                            }
                        } else if (propName.contains("padding")) {
                            if (paddingToBeDone) {
                                mapPadding(newProps, odfProps);
                                paddingToBeDone = false;
                            }
                        } else if (propName.contains("border")) {
                            if (borderToBeDone) {
                                mapBorder(newProps, odfProps);
                                borderToBeDone = false;
                            }
                        } else if (propName.contains("letter-spacing")) {
                            propValue = odfProps.get("fo:letter-spacing");
                            if (propValue.equals("normal")) {
                                newProps.put("letterSpacing", propValue);
                            } else {
                                Integer spacing = MapHelper.normalizeLength(propValue);
                                newProps.put("letterSpacing", spacing);
                            }
                        } else if (propName.equals("fo:font-size")) {
                            propValue = odfProps.get("fo:font-size");
                            if (propValue.contains("%")) {
                                LOG.fine("fo:font-size does have a percentage value, which we do not support!");
                            } else {
                                Length length = new Length(propValue);
                                Double fontSize = length.getPoint();
                                newProps.put("fontSize", fontSize);
                            }
                        } else if (propName.equals("style:font-size-asian")) {
                            propValue = odfProps.get("style:font-size-asian");
                            if (propValue.contains("%")) {
                                LOG.fine("style:font-size-asia does have a percentage value!");
                            } else {
                                Length length = new Length(propValue);
                                Double fontSize = length.getPoint();
                                newProps.put("fontSizeAsian", fontSize);
                            }
                        } else if (propName.equals("style:font-size-complex")) {
                            propValue = odfProps.get("style:font-size-complex");
                            if (propValue.contains("%")) {
                                LOG.fine("style:font-size-complex does have a percentage value!");
                            } else {
                                Length length = new Length(propValue);
                                Double fontSize = length.getPoint();
                                newProps.put("fontSizeAsian", fontSize);
                            }
                        } else if (propName.equals("style:font-name")) {
                            propValue = odfProps.get("style:font-name");
                            newProps.put("fontName", propValue);
                        } else if (propName.equals("style:font-name-asian")) {
                            propValue = odfProps.get("style:font-name-asian");
                            newProps.put("fontNameAsian", propValue);
                        } else if (propName.equals("style:font-name-complex")) {
                            propValue = odfProps.get("style:font-name-complex");
                            newProps.put("fontNameComplex", propValue);
                        } else if (propName.equals("style:text-position")) {
                            propValue = odfProps.get("style:text-position");
                            if (propValue.contains("sub")) {
                                propValue = "sub";
                                newProps.put("vertAlign", propValue);
                            } else if (propValue.contains("super")) {
                                propValue = "super";
                                newProps.put("vertAlign", propValue);
                            } else if (propValue.equals("0%") || propValue.equals("0% 100%")) {
                                propValue = "baseline";
                                newProps.put("vertAlign", propValue);
                            }
                        } else if (propName.equals("fo:language")) {
                            propValue = odfProps.get("fo:language");
                            String country = odfProps.get("fo:country");
                            if (propValue != null) {
                                if (!propValue.equals("none")) {
                                    if (country != null && !country.isEmpty() && !country.equals("none")) {
                                        propValue = propValue + '-' + country;
                                    }
                                    newProps.put("language", propValue);
                                } else {
                                    newProps.put("noProof", true);
                                    newProps.put("language", "none");
                                }
                            }
                        }
                        // TODO -- !!!!!!!!!!!!!!!!ROUNDTRIP WITH THESE VALUES!!!!!!!!!!!
                        //  <define name="fontWeight">
                        //      <choice>
                        //          <value>normal</value>
                        //          <value>bold</value>
                        //          <value>100</value>
                        //          <value>200</value>
                        //          <value>300</value>
                        //          <value>400</value>
                        //          <value>500</value>
                        //          <value>600</value>
                        //          <value>700</value>
                        //          <value>800</value>
                        //          <value>900</value>
                        //      </choice>
                        //  </define>
                        if (propName.equals("fo:font-weight")) {
                            propValue = odfProps.get("fo:font-weight");
                            if (propValue.equals("normal")) {
                                newProps.put("bold", Boolean.FALSE);
                            } else {
                                newProps.put("bold", Boolean.TRUE);
                            }
                        } else if (propName.equals("style:font-weight-asian")) {
                            propValue = odfProps.get("style:font-weight-asian");
                            if (propValue.equals("normal")) {
                                newProps.put("boldAsian", Boolean.FALSE);
                            } else {
                                newProps.put("boldAsian", Boolean.TRUE);
                            }
                        } else if (propName.equals("style:font-weight-complex")) {
                            propValue = odfProps.get("style:font-weight-complex");
                            if (propValue.equals("normal")) {
                                newProps.put("boldComplex", Boolean.FALSE);
                            } else {
                                newProps.put("boldComplex", Boolean.TRUE);
                            }
                        } //    <define name="lineStyle">
                        //      <choice>
                        //          <value>none</value>
                        //          <value>solid</value>
                        //          <value>dotted</value>
                        //          <value>dash</value>
                        //          <value>long-dash</value>
                        //          <value>dot-dash</value>
                        //          <value>dot-dot-dash</value>
                        //          <value>wave</value>
                        //      </choice>
                        //  </define>
                        else if (propName.equals("style:text-underline-style")) {
                            propValue = odfProps.get("style:text-underline-style");
                            if (propValue.equals("none")) {
                                newProps.put("underline", Boolean.FALSE);
                            } else {
                                newProps.put("underline", Boolean.TRUE);
                            }

                            //  <define name="fontStyle">
                            //      <choice>
                            //          <value>normal</value>
                            //          <value>italic</value>
                            //          <value>oblique</value>
                            //      </choice>
                            //  </define>
                        } else if (propName.equals("fo:font-style")) {
                            propValue = odfProps.get("fo:font-style");
                            if (propValue.equals("normal")) {
                                newProps.put("italic", Boolean.FALSE);
                            } else {
                                newProps.put("italic", Boolean.TRUE);
                            }
                        } else if (propName.equals("style:font-style-asian")) {
                            propValue = odfProps.get("style:font-style-asian");
                            if (propValue.equals("normal")) {
                                newProps.put("italicAsian", Boolean.FALSE);
                            } else {
                                newProps.put("italicAsian", Boolean.TRUE);
                            }
                        } else if (propName.equals("style:font-style-complex")) {
                            propValue = odfProps.get("style:font-style-complex");
                            if (propValue.equals("normal")) {
                                newProps.put("italicComplex", Boolean.FALSE);
                            } else {
                                newProps.put("italicComplex", Boolean.TRUE);
                            }
                        } // fo:color - text props only
                        else if (propName.equals("fo:color")) {
                            // "auto" color type wins..
                            if (!newProps.has("color")) {
                                propValue = odfProps.get("fo:color");
                                Map<String, String> color = MapHelper.createColorMap(propValue);
                                newProps.put("color", color);
                            }
                        } else if (propName.equals("style:use-window-font-color")) {
                            propValue = odfProps.get("style:use-window-font-color");
                            if (propValue.equals("true")) {
                                newProps.put("color", COLOR_MAP_AUTO);
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);
                        } else if (propName.equals("style:width")) {
                            propValue = odfProps.get("style:width");
                            if (!propValue.contains(PERCENT)) {
                                newProps.put("width", MapHelper.normalizeLength(propValue));
                            }
                        } else if (propName.equals("style:writing-mode")) {
                            propValue = odfProps.get("style:writing-mode");
                            newProps.put("writingMode", propValue);

                        } else if (propName.equals("style:text-line-through-style")) {
                            /* the style might be 'none'*/
                            propValue = odfProps.get("style:text-line-through-style");
                            if (propValue.equals("none")) {
                                newProps.put("strike", "none");
                            } else {
                                if (odfProps.containsKey("style:text-line-through-type")) {
                                    propValue = odfProps.get("style:text-line-through-type");
                                    /* The type is either "double" or different, than we are providing a "single" style */
                                    if (propValue.equals("double")) {
                                        newProps.put("strike", "double");
                                    } else {
                                        newProps.put("strike", "single");
                                    }
                                } else {
                                    newProps.put("strike", "single");

                                }
                            }
                        }
                        //fo:letter-spacing="0.0104in" fo:hyphenate="false
//                      "vertAlign":"super"}
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (styleFamilyGroup.equals("paragraph")) {
                boolean borderToBeDone = true;
                boolean paddingToBeDone = true;
                boolean marginToBeDone = true;
                JSONArray tabs = null;
                for (String propName : odfProps.keySet()) {
                    try {
                        if (propName.contains("margin")) {
                            if (marginToBeDone) {
                                mapMargin(newProps, odfProps);
                                marginToBeDone = false;
                            }
                        } else if (propName.contains("padding")) {
                            if (paddingToBeDone) {
                                mapPadding(newProps, odfProps);
                                paddingToBeDone = false;
                            }
                        } else if (propName.contains("border")) {
                            if (borderToBeDone) {
                                mapBorder(newProps, odfProps);
                                borderToBeDone = false;
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);
                            //              <attribute name="fo:line-height">
                            //                  <choice>
                            //                      <value>normal</value>
                            //                      <ref name="nonNegativeLength"/>
                            //                      <ref name="percent"/>
                            //                  </choice>
                            //              </attribute>
                            // { type: 'percent', value: 100 }
                        } else if (propName.equals("fo:line-height")) {
                            String lineHeightValue = odfProps.get("fo:line-height");
                            if (lineHeightValue != null && !lineHeightValue.isEmpty()) {
                                JSONObject lineHeight = createLineHeightMap(lineHeightValue);
                                newProps.put("lineHeight", lineHeight);
                            }
                        } else if (propName.equals("style:line-spacing")) {
                            String lineLeadingValue = odfProps.get("style:line-spacing");
                            if (lineLeadingValue != null && !lineLeadingValue.isEmpty()) {
                                JSONObject lineHeightLeadingMap = new JSONObject();
                                lineHeightLeadingMap.put("type", "leading");
                                lineHeightLeadingMap.put("value", MapHelper.normalizeLength(lineLeadingValue));
                                newProps.put("lineHeight", lineHeightLeadingMap);
                            }
                        } else if (propName.equals("style:line-height-at-least")) {
                            String lineHeightAtLeastValue = odfProps.get("style:line-height-at-least");
                            if (lineHeightAtLeastValue != null && !lineHeightAtLeastValue.isEmpty()) {
                                JSONObject lineHeightAtLeastMap = new JSONObject();
                                lineHeightAtLeastMap.put("type", "atLeast");
                                lineHeightAtLeastMap.put("value", MapHelper.normalizeLength(lineHeightAtLeastValue));
                                newProps.put("lineHeight", lineHeightAtLeastMap);
                            }
                        } // see Changes API Paragraph_Formatting_Attributes
                        // One of 'left', 'center', 'right', or 'justify'.
                        // start, end, left, right, center or justify.
                        else if (propName.equals("fo:text-align")) {
                            propValue = odfProps.get("fo:text-align");
                            newProps.put("alignment", mapFoTextAlign(propValue));
                        } else if (propName.equals("fo:text-indent")) {
                            propValue = odfProps.get("fo:text-indent");
                            if (propValue.contains("%")) {
                                LOG.fine("WARNING: Found a 'fo:text-indent' with percentage we are not yet supporting in our API: " + propValue);
                            } else {
                                newProps.put("indentFirstLine", MapHelper.normalizeLength(propValue));
                            }
                        } else if (propName.startsWith("tab_")) {
                            int i = 0;
                            boolean hasTabChar = false;
                            boolean hasTabPos = false;
                            boolean hasTabType = false;
                            JSONObject tab = null;
                            // addChild tab property only once
                            if (!newProps.has("tabStops")) {
                                while (true) {
                                    if (odfProps.containsKey("tab_LeaderText" + i)) {
                                        propValue = odfProps.get("tab_LeaderText" + i);
                                        if (tab == null) {
                                            tab = new JSONObject();
                                        }
                                        tab.put("fillChar", propValue);
                                        hasTabChar = true;
                                    } else {
                                        hasTabChar = false;
                                    }
                                    if (odfProps.containsKey("tab_Pos" + i)) {
                                        propValue = odfProps.get("tab_Pos" + i);
                                        if (tab == null) {
                                            tab = new JSONObject();
                                        }
                                        tab.put("pos", Integer.parseInt(propValue));
                                        hasTabPos = true;
                                    } else {
                                        hasTabPos = false;
                                    }
                                    if (odfProps.containsKey("tab_Type" + i)) {
                                        propValue = odfProps.get("tab_Type" + i);
                                        if (tab == null) {
                                            tab = new JSONObject();
                                        }
                                        if (propValue.equals("left")) {
                                            propValue = null;
                                            //as default
                                        }
                                        if (propValue != null) {
                                            tab.put("value", (String) propValue);
                                            hasTabType = true;
                                        }
                                    } else {
                                        hasTabType = false;
                                    }
                                    if (!hasTabChar && !hasTabType && !hasTabPos) {
                                        newProps.put("tabStops", tabs);
                                        break;
                                    } else {
                                        if (tabs == null) {
                                            tabs = new JSONArray();
                                        }
                                        tabs.put(tab);
                                        hasTabType = false;
                                        hasTabPos = false;
                                        hasTabChar = false;
                                        tab = null;
                                    }
                                    i++;
                                }
                            }
                        } else if (propName.equals("style:tab-stop-distance")) {
                            propValue = odfProps.get("style:tab-stop-distance");
                            if (propValue != null && !propValue.isEmpty()) {
                                JSONObject documentProps;
                                if (newProps.has("document")) {
                                    documentProps = newProps.getJSONObject("document");
                                } else {
                                    documentProps = new JSONObject();
                                }
                                documentProps.put("defaultTabStop", MapHelper.normalizeLength(propValue));
                                newProps.put("document", documentProps);
                            }
                        } else if (propName.equals("fo:break-before")) {
                            propValue = odfProps.get("fo:break-before");
                            if (propValue.equals("page")) {
                                newProps.put("pageBreakBefore", Boolean.TRUE);
                            }
                        } else if (propName.equals("fo:break-after")) {
                            propValue = odfProps.get("fo:break-after");
                            if (propValue.equals("page")) {
                                newProps.put("pageBreakAfter", Boolean.TRUE);
                            }
                        }

                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (styleFamilyGroup.equals("cell")) {
                boolean borderToBeDone = true;
                boolean paddingToBeDone = true;
                for (String propName : odfProps.keySet()) {
                    try {
                        // No margin
                        if (propName.contains("padding")) {
                            if (paddingToBeDone) {
                                mapPadding(newProps, odfProps);
                                paddingToBeDone = false;
                            }
                        } else if (propName.contains("border")) {
                            if (borderToBeDone) {
                                mapBorder(newProps, odfProps);
                                borderToBeDone = false;
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);
                        } else if (propName.equals("style:vertical-align")) {
                            propValue = odfProps.get("style:vertical-align");
                            newProps.put("alignVert", propValue);
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (styleFamilyGroup.equals("column")) {
                for (String propName : odfProps.keySet()) {
                    try {
                        // No margin
                        if (propName.equals("style:column-width")) {
                            propValue = odfProps.get("style:column-width");
                            newProps.put("width", MapHelper.normalizeLength(propValue));
                        } else if (propName.contains("style:use-optimal-column-width")) {
                            propValue = odfProps.get("style:use-optimal-column-width");
                            newProps.put("customWidth", !Boolean.parseBoolean(propValue));
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (styleFamilyGroup.equals("row")) {
                try {
                    for (String propName : odfProps.keySet()) {
                        if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);
                        } else if (propName.equals("style:min-row-height")) {
                            propValue = odfProps.get("style:min-row-height");
                            newProps.put("height", MapHelper.normalizeLength(propValue));
                        } else if (propName.equals("style:row-height")) {
                            propValue = odfProps.get("style:row-height");
                            newProps.put("height", MapHelper.normalizeLength(propValue));
                        } else if (propName.contains("style:use-optimal-row-height")) {
                            propValue = odfProps.get("style:use-optimal-row-height");
                            newProps.put("customHeight", !Boolean.parseBoolean(propValue));
                        }
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(JsonOperationProducer.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else if (styleFamilyGroup.equals("list")) {
                try {
                    for (String propName : odfProps.keySet()) {
                        if (propName.equals("style:font-name")) {
                            propValue = odfProps.get("style:font-name");
                            newProps.put("fontName", propValue);
                        } else if (propName.equals("style:font-name-asian")) {
                            propValue = odfProps.get("style:font-name-asian");
                            newProps.put("fontNameAsian", propValue);
                        } else if (propName.equals("style:font-name-complex")) {
                            propValue = odfProps.get("style:font-name-complex");
                            newProps.put("fontNameComplex", propValue);
                        } else if (propName.equals("fo:text-align")) {
                            propValue = odfProps.get("fo:text-align");

                            if (propValue.equals("start") || propValue.equals("left")) {
                                //ToDo: I8N requires a correspondonce to the writing direction
                                propValue = "left";
                            } else if (propValue.equals("end") || propValue.equals("right")) {
                                //ToDo: I8N requires a correspondonce to the writing direction
                                propValue = "right";
                            }
                            newProps.put("alignment", propValue);
                        }
                    }
                } catch (JSONException ex) {
                    Logger.getLogger(JsonOperationProducer.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else if (styleFamilyGroup.equals("table")) {
                boolean marginToBeDone = true;
                for (String propName : odfProps.keySet()) {
                    try {
                        // no padding, no border
                        if (propName.contains("margin")) {
                            if (marginToBeDone) {
                                mapMargin(newProps, odfProps);
                                marginToBeDone = false;
                            }
                        } else if (propName.equals("style:width")) {
                            propValue = odfProps.get("style:width");
                            if (!propValue.contains(PERCENT)) {
                                newProps.put("width", MapHelper.normalizeLength(propValue));
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);
                        } else if (propName.equals("table:display")) {
                            propValue = odfProps.get("table:display");
                            newProps.put("visible", Boolean.parseBoolean(propValue));
                        } else if (propName.equals("fo:break-before")) {
                            propValue = odfProps.get("fo:break-before");
                            newProps.put("pageBreakBefore", propValue.equals("page"));
                        } else if (propName.equals("fo:break-after")) {
                            propValue = odfProps.get("fo:break-after");
                            newProps.put("pageBreakAfter", propValue.equals("page"));
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (styleFamilyGroup.equals("page")) {
                boolean borderToBeDone = true;
                boolean paddingToBeDone = true;
                boolean marginToBeDone = true;
                for (String propName : odfProps.keySet()) {
                    try {
                        if (propName.contains("margin")) {
                            if (marginToBeDone) {
                                mapMargin(newProps, odfProps);
                                marginToBeDone = false;
                            }
                        } else if (propName.contains("padding")) {
                            if (paddingToBeDone) {
                                mapPadding(newProps, odfProps);
                                paddingToBeDone = false;
                            }
                        } else if (propName.contains("border")) {
                            if (borderToBeDone) {
                                mapBorder(newProps, odfProps);
                                borderToBeDone = false;
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);
                        } else if (propName.equals("fo:page-width")) {
                            propValue = odfProps.get("fo:page-width");
                            newProps.put("width", MapHelper.normalizeLength(propValue));
                        } else if (propName.equals("fo:page-height")) {
                            propValue = odfProps.get("fo:page-height");
                            newProps.put("height", MapHelper.normalizeLength(propValue));
                        } else if (propName.equals("style:print-orientation")) {
                            propValue = odfProps.get("style:print-orientation");
                            newProps.put("printOrientation", propValue);
                        } else if (propName.equals("style:num-format")) {
                            propValue = odfProps.get("style:num-format");
                            newProps.put("numberFormat", propValue);
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (styleFamilyGroup.equals("drawing")) {
                boolean borderToBeDone = true;
                boolean paddingToBeDone = true;
                boolean marginToBeDone = true;
                shapeProps = new JSONObject();
                imageProps = new JSONObject();
                lineProps = new JSONObject();
                fillProps = new JSONObject();
                for (String propName : odfProps.keySet()) {
                    try {
                        if (propName.contains("margin")) {
                            if (marginToBeDone) {
                                mapMargin(newProps, odfProps);
                                marginToBeDone = false;
                            }
                        } else if (propName.contains("padding")) {
                            if (paddingToBeDone) {
                                mapPadding(shapeProps, odfProps);
                                paddingToBeDone = false;
                            }
                        } else if (propName.contains("border")) {
                            if (borderToBeDone) {
                                mapBorder(newProps, odfProps);
                                borderToBeDone = false;
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            fillProps.put("type", "solid");
                            fillProps.put("color", color);
                            // http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#property-style_mirror
                        } else if (propName.equals("draw:fill-color")) {
                            propValue = odfProps.get("draw:fill-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            fillProps.put("type", "solid");
                            fillProps.put("color", color);
                        } else if (propName.equals("style:mirror")) {
                            String mirror = odfProps.get("style:mirror");
                            if (mirror.contains("horizontal") && !mirror.contains("-on-")) {
                                newProps.put("flipH", Boolean.TRUE);
                            }
                            if (mirror.contains("vertical")) {
                                newProps.put("flipV", Boolean.TRUE);
                            }
                            if (mirror.equals("none")) {
                                newProps.put("flipV", Boolean.FALSE);
                                newProps.put("flipH", Boolean.FALSE);
                            }
                            /*
                             @style:horizontal-rel:
                             The defined values for the style:horizontal-rel attribute are:
                             * char: horizontal position of a frame is positioned relative to a character.
                             * page: horizontal position of a frame is positioned relative to a page.
                             * page-content: horizontal position of a frame is positioned relative to page-content.
                             * page-start-margin: horizontal position of a frame is positioned relative to a page start margin.
                             * page-end-margin: horizontal position of a frame is positioned relative to a page end margin.
                             * frame: horizontal position of a frame is positioned relative to another frame.
                             * frame-content: horizontal position of a frame is positioned relative to frame content.
                             * frame-end-margin: horizontal position of a frame is positioned relative to a frame end margin.
                             * frame-start-margin: horizontal position of a frame is positioned relative to a frame start margin
                             * paragraph: horizontal position of a frame is positioned relative to a paragraph.
                             * paragraph-content: horizontal position of a frame is positioned relative to paragraph content.
                             * paragraph-end-margin: horizontal position of a frame is positioned relative to a paragraph end margin.
                             * paragraph-start-margin:horizontal position of a frame is positioned relative to a paragraph start margin.

                             @style:horizontal-pos:
                             The defined values for the style:horizontal-pos attribute are:
                             * center: horizontal alignment of a frame should be centered relative to the specified area.
                             * anchorHorAlign=center
                             * from-inside: on pages with an odd page number the left edge of the specific area is taken as the horizontal alignment of a frame. On pages with an even page number the right edge of the specified area is taken. Attribute svg:x associated with the frame element specifies the horizontal position of the frame from the edge which is taken.
                             * UNSUPPORTED
                             * from-left: the svg:x attribute associated with the frame element specifies the horizontal position of the frame from the left edge of the specified area.
                             * anchorHorAlign=offset
                             * inside: on pages with an odd page number the horizontal alignment of a frame is the same as for the attribute value left. On pages with an even page number the horizontal alignment of a frame is the same as for the attribute value right.
                             * anchorHorAlign=inside
                             * left: horizontal alignment of a frame should be left aligned relative to the specified area.
                             * anchorHorAlign=left
                             * outside: on pages with an odd page number the horizontal alignment of a frame is the same as for the attribute value right. On pages with an even page number the horizontal alignment of a frame is the same as for the attribute value left.
                             * anchorHorAlign=outside
                             * right: horizontal alignment of a frame should be right aligned relative to the specified area.
                             * anchorHorAlign=right
                             If the attribute value is not from-left and not from-inside, the svg:x attribute associated with the frame element is ignored for text documents.                       */
                            // Changes API:
                            // anchorHorAlign: Horizontal anchor position:  One of 'left', 'right', 'center', 'inside', 'outside', or 'offset'.
                            // anchorHorOffset: Horizontal position offset (only used if anchorHorAlign is set to 'offset')
                        } else if (propName.contains("horizontal-pos")) {
                            String horizontalPos = odfProps.get("style:horizontal-pos");
                            if (horizontalPos.equals("center")) {
                                newProps.put("anchorHorAlign", "center");
                            } else if (horizontalPos.equals("from-left")) {
                                newProps.put("anchorHorAlign", "offset");
                            } else if (horizontalPos.equals("left")) {
                                newProps.put("anchorHorAlign", "left");
                            } else if (horizontalPos.equals("right")) {
                                newProps.put("anchorHorAlign", "right");
                            } else if (horizontalPos.equals("inside")) {
                                newProps.put("anchorHorAlign", "inside");
                            } else if (horizontalPos.equals("outside")) {
                                newProps.put("anchorHorAlign", "outside");
                            }
                            // anchorVertAlign   Vertical anchor position. One of 'top', 'bottom', 'center', 'inside', 'outside', 'offset'.
                        } else if (propName.contains("vertical-pos")) {
                            String verticalPos = odfProps.get("style:vertical-pos");
                            if (verticalPos.equals("center")) {
                                newProps.put("anchorVertAlign", "center");
                            } else if (verticalPos.equals("from-top")) {
                                newProps.put("anchorVertAlign", "offset");
                            } else if (verticalPos.equals("top")) {
                                newProps.put("anchorVertAlign", "top");
                            } else if (verticalPos.equals("bottom")) {
                                newProps.put("anchorVertAlign", "bottom");
                            } else if (verticalPos.equals("inside")) {
                                newProps.put("anchorVertAlign", "inside");
                            } else if (verticalPos.equals("outside")) {
                                newProps.put("anchorVertAlign", "outside");
                            }
                        } else if (propName.contains("horizontal-rel")) {
                            String horiRel = odfProps.get("style:horizontal-rel");

                            if (horiRel.equals("char")) {
                                newProps.put("anchorHorBase", "character");
                            } else if (horiRel.equals("page-content")) {
                                newProps.put("anchorHorBase", "margin");
                            } else if (horiRel.equals("page-start-margin")) {
                                newProps.put("anchorHorBase", "leftMargin");
                            } else if (horiRel.equals("page-end-margin")) {
                                newProps.put("anchorHorBase", "rightMargin");
                            } else if (horiRel.equals("frame")) {
                                newProps.put("anchorHorBase", "column");
                            } else if (horiRel.equals("frame-content") || horiRel.equals("frame-end-margin") || horiRel.equals("frame-start-margin")) {
                                newProps.put("anchorHorBase", "column");
                            } else if (horiRel.equals("paragraph") || horiRel.equals("paragraph-content")) {
                                newProps.put("anchorHorBase", "column");
                            } else if (horiRel.equals("paragraph-end-margin")) {
                                newProps.put("anchorHorBase", "rightMargin");
                            } else if (horiRel.equals("paragraph-start-margin")) {
                                newProps.put("anchorHorBase", "leftMargin");
                            } else if (horiRel.equals("page")) {
                                newProps.put("anchorHorBase", "page");
                            }
                        } else if (propName.contains("vertical-rel")) {
                            // char,
                            // frame, frame-content
                            // line
                            // page, page-content,
                            //paragraph-content, paragraph,
                            //text
                            String verticalRel = odfProps.get("style:vertical-rel");
                            if (verticalRel.equals("char")) {
                                newProps.put("anchorVertBase", "line");
                            } else if (verticalRel.equals("frame")) {
                                newProps.put("anchorVertBase", "paragraph");
                            } else if (verticalRel.equals("frame-content")) {
                                newProps.put("anchorVertBase", "paragraph");
                            } else if (verticalRel.equals("line")) {
                                newProps.put("anchorVertBase", "line");
                            } else if (verticalRel.equals("page")) {
                                newProps.put("anchorVertBase", "page");
                            } else if (verticalRel.equals("page-content")) {
                                newProps.put("anchorVertBase", "margin");
                            } else if (verticalRel.equals("paragraph")) {
                                newProps.put("anchorVertBase", "paragraph");
                            } else if (verticalRel.equals("paragraph-content")) {
                                newProps.put("anchorVertBase", "paragraph");
                            } else if (verticalRel.equals("text")) {
                                newProps.put("anchorVertBase", "line");
                            }
                        } else if (propName.equals("svg:x")) {
                            int x = MapHelper.normalizeLength(odfProps.get("svg:x"));
                            if (x != 0) {
                                newProps.put("anchorHorOffset", x);
                                newProps.put("left", x);
                            }
                        } else if (propName.equals("svg:y")) {
                            int y = MapHelper.normalizeLength(odfProps.get("svg:y"));
                            if (y != 0) {
                                newProps.put("anchorVertOffset", y);
                                newProps.put("top", y);
                            }

                            /*
                             Changes API:
                             cropLeft   Left cropping in percent
                             cropRight  Right cropping in percent
                             cropTop        Top cropping in percent
                             cropBottom Bottom cropping in percent
                             http://www.w3.org/TR/CSS2/visufx.html#propdef-clip */
                            // <top>, <right>, <bottom>, <left>
                            // Value length or "auto"
                            // e.g. @fo:clip="rect(0in, 0.07874in, 0in, 0.07874in)"
                            // PROBLEM: The width & height is not accessible
                        } else if (propName.equals("fo:clip")) {
                            String clipping = odfProps.get("fo:clip");
                            int start = clipping.indexOf("rect(");
                            int end = clipping.indexOf(")");
                            if (start > -1 && end > -1) {
                                clipping = clipping.substring(start + 5, end);
                            }
                            String clips[] = clipping.split(", ");
                            if (clips.length != 4) {
                                // fallback as some documents to not behave as the standard explains
                                clips = clipping.split(" ");
                            }
                            int clipTop = MapHelper.normalizeLength(clips[0]);
                            imageProps.put("cropTop", clipTop);
                            int clipRight = MapHelper.normalizeLength(clips[1]);
                            imageProps.put("cropRight", clipRight);
                            int clipBottom = MapHelper.normalizeLength(clips[2]);
                            imageProps.put("cropBottom", clipBottom);
                            int clipLeft = MapHelper.normalizeLength(clips[3]);
                            imageProps.put("cropLeft", clipLeft);

                            /* http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#property-style_wrap
                             The style:wrap attribute specifies how text is displayed around a frame or graphic object.
                             The defined values for the style:wrap attribute are:
                             * biggest: text may wrap around the shape where the difference to the left or right page or column border is largest.
                             * Mode=square Side=largest
                             * dynamic: text may wrap around both sides of the shape. The space for wrapping is set by the style:wrap-dynamic-threshold attribute. 20.393
                             * UNSUPPORTED
                             * left: text wraps around the left side of the shape.
                             * Mode=square  Side=left
                             * none: text does not wrap around the shape.
                             * Mode=topAndBottom
                             * parallel: text wraps around both sides of the shape.
                             * Mode=square  Side=both
                             * right: text wraps around the right side of the shape.
                             * Mode=square  Side=right
                             * run-through: text runs through the shape.
                             * Mode=through Side=both   */
 /*
                             Changes API: textWrapMode   One of 'none', 'square', 'tight', 'through', or 'topAndBottom'. (IMHO 'none' == 'topAndBottom', but the latter is implemented)
                             Changes API: textWrapSide   Sides where text wraps around the image (only used if textWrapMode is set to 'square', 'tight', or 'through') (1). One of ['both', 'left', 'right', 'largest'   */
                        } else if (propName.equals("style:wrap")) {
                            String wrap = odfProps.get("style:wrap");

                            if (wrap.equals("biggest")) {
                                newProps.put("textWrapMode", "square");
                                newProps.put("textWrapSide", "largest");
                            } else if (wrap.equals("left")) {
                                newProps.put("textWrapMode", "square");
                                newProps.put("textWrapSide", "left");
                            } else if (wrap.equals("none")) {
                                newProps.put("textWrapMode", "topAndBottom");
                            } else if (wrap.equals("parallel")) {
                                newProps.put("textWrapMode", "square");
                                newProps.put("textWrapSide", "both");
                            } else if (wrap.equals("right")) {
                                newProps.put("textWrapMode", "square");
                                newProps.put("textWrapSide", "right");
                            } else if (wrap.equals("run-through")) {
                                newProps.put("textWrapMode", "through");
                                newProps.put("textWrapSide", "both");

                            }
                        } else if (propName.equals("draw:stroke")) {
                            String stroke = odfProps.get("draw:stroke");
                            if (stroke.equals("none")) {
                                lineProps.put("style", "none");
                            } else {
                                lineProps.put("style", stroke.equals("solid") ? "solid" : "dashed");
                                //TODO: map draw:stroke-dash into the line type
                                if (!lineProps.has("width")) {
                                    lineProps.put("width", 1);
                                }
                                if (!lineProps.has("type")) {
                                    lineProps.put("type", "solid");
                                }
                            }
                        } else if (propName.equals("svg:stroke-color")) {
                            String color = odfProps.get("svg:stroke-color");
                            lineProps.put("color", MapHelper.createColorMap(color));
                        } else if (propName.equals("svg:stroke-width")) {
                            lineProps.put("width", MapHelper.normalizeLength(odfProps.get("svg:stroke-width")));
                        } else if (propName.equals("style:run-through")) {
                            String runThrough = odfProps.get(propName);
                            if ("background".equals(runThrough)) {
                                newProps.put("anchorBehindDoc", true);
                            }
                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
                //border handling: text frames have a border attribute, shapes _can_ have stroke
                try {
                    if (newProps.has("border") || newProps.has("borderTop")) {
                        // convert to "line"
                        JSONObject currentBorder = newProps.has("border") ? newProps.getJSONObject("border") : newProps.getJSONObject("borderTop");
                        if (currentBorder.has("style")) {
                            String style = currentBorder.getString("style");
                            if (style.equals("none")) {
                                lineProps.put("type", "none");
                            } else {
                                if (style.equals("dashed")) {
                                    lineProps.put("style", "dashed");
                                } else if (style.equals("dotted")) {
                                    lineProps.put("style", "dotted");
                                } else {
                                    lineProps.put("style", "solid");
                                }
                                lineProps.put("type", "solid");
                            }
                        }
                        if (currentBorder.has("width")) {
                            lineProps.put("width", currentBorder.get("width"));
                        }
                        if (currentBorder.has("color")) {
                            lineProps.put("color", currentBorder.get("color"));
                        }

                    }
                    if (newProps.has("borderTop")) {
                        newProps.remove("borderTop");
                    }
                    if (newProps.has("borderBottom")) {
                        newProps.remove("borderBottom");
                    }
                    if (newProps.has("borderLeft")) {
                        newProps.remove("borderLeft");
                    }
                    if (newProps.has("borderRight")) {
                        newProps.remove("borderRight");
                    }
                    if (newProps.has("border")) {
                        newProps.remove("border");
                    }
                } catch (JSONException e) {
                    // no handline required
                }
            } // NOTE: HEADER FOOTER ARE SOMEHOW (ASYMETRIC) NESTED AMONG PAGE PROPERTIES.. (ToDo: Unused yet!)
            else if (styleFamilyGroup.equals("headerFooter")) {
                boolean borderToBeDone = true;
                boolean paddingToBeDone = true;
                boolean marginToBeDone = true;
                for (String propName : odfProps.keySet()) {
                    try {
                        if (propName.contains("margin")) {
                            if (marginToBeDone) {
                                mapMargin(newProps, odfProps);
                                marginToBeDone = false;
                            }
                        } else if (propName.contains("padding")) {
                            if (paddingToBeDone) {
                                mapPadding(newProps, odfProps);
                                paddingToBeDone = false;
                            }
                        } else if (propName.contains("border")) {
                            if (borderToBeDone) {
                                mapBorder(newProps, odfProps);
                                borderToBeDone = false;
                            }
                        } else if (propName.equals("fo:background-color")) {
                            propValue = odfProps.get("fo:background-color");
                            Map<String, String> color = MapHelper.createColorMap(propValue);
                            newProps.put("fillColor", color);

                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(JsonOperationProducer.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        JSONObject retObject = new JSONObject();
        try {
            retObject.put(styleFamilyGroup, newProps);
            if (shapeProps != null && shapeProps.length() > 0) {
                retObject.put("shape", shapeProps);
            }
            if (imageProps != null && imageProps.length() > 0) {
                retObject.put("image", imageProps);
            }
            if (lineProps != null) {
                retObject.put("line", lineProps);
            }
            if (fillProps != null && fillProps.length() > 0) {
                retObject.put("fill", fillProps);
            }
        } catch (JSONException e) {
            //no handling required
        }
        return retObject;
    }

    /**
     * see Changes API LineHeight
     */
    private static JSONObject createLineHeightMap(String lineHeightValue) {
        JSONObject lineHeight = new JSONObject();
        try {
            /**
             * Usually normal is given by the font and 110% to 120% of the
             * font-size (see
             * http://www.w3.org/TR/CSS2/visudet.html#line-height), browser even
             * show 1.1 to 1.3, but in office applications normal is ALWAYS
             * 100%, therefore it will be mapped see as well
             * http://www.w3.org/TR/xsl/#line-height
             */
            if (lineHeightValue.equals("normal")) {
                lineHeight.put("type", "percent");
                lineHeight.put("value", "100");
            } else if (lineHeightValue.contains("%")) {
                lineHeight.put("type", "percent");
                lineHeight.put("value", Integer.parseInt(lineHeightValue.subSequence(0, lineHeightValue.indexOf('%')).toString()));
            } else {
                lineHeight.put("type", "fixed");
                lineHeight.put("value", MapHelper.normalizeLength(lineHeightValue));

            }
        } catch (JSONException ex) {
            Logger.getLogger(JsonOperationProducer.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return lineHeight;
    }

    private static void mapBorder(JSONObject newProps, Map<String, String> odfProps) throws JSONException {
        String propValue;
        JSONObject defaultBorder = null;
        Integer defaultSpace = null;
        // fo:border="0.5pt solid #000000"
        if (odfProps.containsKey("fo:border")) {
            propValue = odfProps.get("fo:border");
            defaultBorder = MapHelper.createBorderMap(propValue);
            if (odfProps.containsKey("fo:padding")) {
                propValue = odfProps.get("fo:padding");
                if (!propValue.contains(PERCENT)) {
                    defaultSpace = MapHelper.normalizeLength(propValue);
                }
                defaultBorder.put("space", defaultSpace);
            }
        }
        if (odfProps.containsKey("fo:border-left")) {
            propValue = odfProps.get("fo:border-left");
            JSONObject border = MapHelper.createBorderMap(propValue);
            if (odfProps.containsKey("fo:padding-left")) {
                propValue = odfProps.get("fo:padding-left");
                if (!propValue.contains(PERCENT)) {
                    border.put("space", MapHelper.normalizeLength(propValue));
                }
            } else if (defaultSpace != null) {
                border.put("space", defaultSpace);
            }
            newProps.put("borderLeft", border);
        } else {
            newProps.put("borderLeft", defaultBorder);
        }
        if (odfProps.containsKey("fo:border-top")) {
            propValue = odfProps.get("fo:border-top");
            JSONObject border = MapHelper.createBorderMap(propValue);
            if (odfProps.containsKey("fo:padding-top")) {
                propValue = odfProps.get("fo:padding-top");
                if (!propValue.contains(PERCENT)) {
                    border.put("space", MapHelper.normalizeLength(propValue));
                }
            } else if (defaultSpace != null) {
                border.put("space", defaultSpace);
            }
            newProps.put("borderTop", border);
        } else {
            newProps.put("borderTop", defaultBorder);
        }
        if (odfProps.containsKey("fo:border-right")) {
            propValue = odfProps.get("fo:border-right");
            JSONObject border = MapHelper.createBorderMap(propValue);
            if (odfProps.containsKey("fo:padding-right")) {
                propValue = odfProps.get("fo:padding-right");
                if (!propValue.contains(PERCENT)) {
                    border.put("space", MapHelper.normalizeLength(propValue));
                }
            } else if (defaultSpace != null) {
                border.put("space", defaultSpace);
            }
            newProps.put("borderRight", border);
        } else {
            newProps.put("borderRight", defaultBorder);
        }
        if (odfProps.containsKey("fo:border-bottom")) {
            propValue = odfProps.get("fo:border-bottom");
            JSONObject border = MapHelper.createBorderMap(propValue);
            if (odfProps.containsKey("fo:padding-bottom")) {
                propValue = odfProps.get("fo:padding-bottom");
                if (!propValue.contains(PERCENT)) {
                    border.put("space", MapHelper.normalizeLength(propValue));
                }
            } else if (defaultSpace != null) {
                border.put("space", defaultSpace);
            }
            newProps.put("borderBottom", border);
        } else {
            newProps.put("borderBottom", defaultBorder);
        }
    }

    public static String mapFoTextAlign(String propValue) {
        if (propValue.equals("start") || propValue.equals("left")) {
            //ToDo: I8N requires a correspondonce to the writing direction
            propValue = "left";
        } else if (propValue.equals("end") || propValue.equals("right")) {
            //ToDo: I8N requires a correspondonce to the writing direction
            propValue = "right";
        }
        return propValue;
    }

    /**
     * To do, we should add the String directly for mapping instead adding the
     * Map
     */
    private static void mapMargin(JSONObject newProps, Map<String, String> odfProps) throws JSONException {
        String propValue;
        Integer defaultLength = null;
        if (odfProps.containsKey("fo:margin")) {
            propValue = odfProps.get("fo:margin");
            if (!propValue.contains(PERCENT)) {
                defaultLength = MapHelper.normalizeLength(propValue);
            }
        }
        if (odfProps.containsKey("fo:margin-left")) {
            propValue = odfProps.get("fo:margin-left");
            if (!propValue.contains(PERCENT)) {
                newProps.put("marginLeft", MapHelper.normalizeLength(propValue));
                newProps.put("indentLeft", MapHelper.normalizeLength(propValue)); //FIX API
            }
        } else {
            if (defaultLength != null) {
                newProps.put("marginLeft", defaultLength);
                newProps.put("indentLeft", defaultLength); //FIX API
            }
        }
        if (odfProps.containsKey("fo:margin-top")) {
            propValue = odfProps.get("fo:margin-top");
            if (!propValue.contains(PERCENT)) {
                newProps.put("marginTop", MapHelper.normalizeLength(propValue));
            }
        } else {
            if (defaultLength != null) {
                newProps.put("marginTop", defaultLength);
            }
        }
        if (odfProps.containsKey("fo:margin-right")) {
            propValue = odfProps.get("fo:margin-right");

            if (!propValue.contains(PERCENT)) {
                newProps.put("marginRight", MapHelper.normalizeLength(propValue));
                newProps.put("indentRight", MapHelper.normalizeLength(propValue)); //FIX API
            }
        } else {
            if (defaultLength != null) {
                newProps.put("marginRight", defaultLength);
                newProps.put("indentRight", defaultLength); //FIX API
            }
        }
        if (odfProps.containsKey("fo:margin-bottom")) {
            propValue = odfProps.get("fo:margin-bottom");
            if (!propValue.contains(PERCENT)) {
                newProps.put("marginBottom", MapHelper.normalizeLength(propValue));
            }
        } else {
            if (defaultLength != null) {
                newProps.put("marginBottom", defaultLength);
            }
        }
    }

    private static void mapPadding(JSONObject newProps, Map<String, String> odfProps) throws JSONException {
        String propValue;
        Integer defaultLength = null;
        if (odfProps.containsKey("fo:padding")) {
            propValue = odfProps.get("fo:padding");
            if (!propValue.contains(PERCENT)) {
                defaultLength = MapHelper.normalizeLength(propValue);
            }
        }
        if (odfProps.containsKey("fo:padding-left")) {
            propValue = odfProps.get("fo:padding-left");
            if (!propValue.contains(PERCENT)) {
                newProps.put("paddingLeft", MapHelper.normalizeLength(propValue));
            }
        } else {
            if (defaultLength != null) {
                newProps.put("paddingLeft", defaultLength);
            }
        }
        if (odfProps.containsKey("fo:padding-top")) {
            propValue = odfProps.get("fo:padding-top");
            if (!propValue.contains(PERCENT)) {
                newProps.put("paddingTop", MapHelper.normalizeLength(propValue));
            }
        } else {
            if (defaultLength != null) {
                newProps.put("paddingTop", defaultLength);
            }
        }
        if (odfProps.containsKey("fo:padding-right")) {
            propValue = odfProps.get("fo:padding-right");

            if (!propValue.contains(PERCENT)) {
                newProps.put("paddingRight", MapHelper.normalizeLength(propValue));
            }
        } else {
            if (defaultLength != null) {
                newProps.put("paddingRight", defaultLength);
            }
        }
        if (odfProps.containsKey("fo:padding-bottom")) {
            propValue = odfProps.get("fo:padding-bottom");
            if (!propValue.contains(PERCENT)) {
                newProps.put("paddingBottom", MapHelper.normalizeLength(propValue));
            }
        } else {
            if (defaultLength != null) {
                newProps.put("paddingBottom", defaultLength);
            }
        }
    }

    static public Map<String, Object> mapStyleProperties(OdfStylableElement styleElement, Map<String, Map<String, String>> allOdfProps) {
        // returns the set of allowed properties, e.g. cell, paragraph, text for a cell with properties
        Map<String, OdfStylePropertiesSet> familyPropertyGroups = Component.getAllStyleGroupingIdProperties(styleElement);
        return mapStyleProperties(familyPropertyGroups, allOdfProps);
    }

    static public Map<String, Object> mapStyleProperties(Map<String, OdfStylePropertiesSet> familyPropertyGroups, Map<String, Map<String, String>> allOdfProps) {

        Map<String, Object> allProps = new HashMap<String, Object>();
        for (String styleFamilyKey : familyPropertyGroups.keySet()) {
            //NOTE: Perhaps we should first inherit everything from the parents and map afterwards
            // the ODF properties of one family group
            JSONObject mappedProps = MapHelper.mapProperties(styleFamilyKey, allOdfProps.get(styleFamilyKey));
            try {
                if (mappedProps != null) {
                    if (mappedProps.has(styleFamilyKey) && mappedProps.getJSONObject(styleFamilyKey).length() != 0) {
                        allProps.put(styleFamilyKey, mappedProps.getJSONObject(styleFamilyKey));
                    }
                    if (styleFamilyKey.equals("drawing")) {
                        if (mappedProps.has("shape") && mappedProps.getJSONObject("shape").length() != 0) {
                            allProps.put("shape", mappedProps.getJSONObject("shape"));
                        }
                        if (mappedProps.has("image") && mappedProps.getJSONObject("image").length() != 0) {
                            allProps.put("image", mappedProps.getJSONObject("image"));
                        }
                        if (mappedProps.has("line") && mappedProps.getJSONObject("line").length() != 0) {
                            allProps.put("line", mappedProps.getJSONObject("line"));
                        }
                        if (mappedProps.has("fill") && mappedProps.getJSONObject("fill").length() != 0) {
                            allProps.put("fill", mappedProps.getJSONObject("fill"));
                        }
                    }
                }

            } catch (JSONException e) {
                // no handling required
            }
        }
        return allProps;
    }

    static public Map<String, Object> getMappedStyleProperties(OdfStyle style) {
        // Mapped properties
        Map<String, Object> mappedFormatting = null;
        if (style != null) {
            // Intermediate ODF properties
            Map<String, Map<String, String>> allOdfProps = new HashMap<String, Map<String, String>>();
            // The property groups for this component, e.g. cell, paragraph, text for a cell with properties
            Map<String, OdfStylePropertiesSet> familyPropertyGroups = Component.getAllStyleGroupingIdProperties(style.getFamily());
            // get all ODF properties from this style
            getStyleProperties(style, familyPropertyGroups, allOdfProps);
            // mapping the ODF attribute style props to our component properties
            mappedFormatting = mapStyleProperties(familyPropertyGroups, allOdfProps);
        }
        return mappedFormatting;
    }

    static public void getStyleProperties(OdfStyleBase style, OdfStylableElement styleElement, Map<String, Map<String, String>> allOdfProps) {
        // returns the set of allowed properties, e.g. cell, paragraph, text for a cell with properties
        Map<String, OdfStylePropertiesSet> familyPropertyGroups = Component.getAllStyleGroupingIdProperties(styleElement);
        getStyleProperties(style, familyPropertyGroups, allOdfProps);
    }

    static public void getStyleProperties(OdfStyleBase style, Map<String, OdfStylePropertiesSet> familyPropertyGroups, Map<String, Map<String, String>> allOdfProps) {
        if (style != null) {
            for (String styleFamilyKey : familyPropertyGroups.keySet()) {
                // the ODF properties of one family group
                Map<String, String> odfProps = new HashMap<String, String>();
                OdfStylePropertiesSet key = familyPropertyGroups.get(styleFamilyKey);
                OdfStylePropertiesBase propsElement = style.getPropertiesElement(key);
                if (propsElement != null) {
                    NamedNodeMap attrs = propsElement.getAttributes();
                    String name = null;
                    for (int i = 0; i < attrs.getLength(); i++) {
                        name = null;
                        Attr prop = (Attr) attrs.item(i);

                        // normalize XML prefix of ODF attributes to the prefixes used in the specification
                        name = OdfNamespace.getNamespace(prop.getNamespaceURI()).getPrefix();
                        if (name == null) {
                            name = prop.getPrefix();
                        }
                        if (name != null) {
                            name = name + ":" + prop.getName();
                        } else {
                            name = prop.getName();
                        }
                        odfProps.put(name, prop.getValue());
                    }
                    if (propsElement instanceof StyleParagraphPropertiesElement) {
                        StyleParagraphPropertiesElement paraPropsElement = (StyleParagraphPropertiesElement) propsElement;
                        NodeList tabStops = paraPropsElement.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(), "tab-stops");
                        if (tabStops.getLength() > 0) {
                            StyleTabStopsElement tabStopsElement = (StyleTabStopsElement) tabStops.item(0);
                            NodeList tabStopList = tabStopsElement.getElementsByTagNameNS(OdfDocumentNamespace.STYLE.getUri(), "tab-stop");
                            int size = tabStopList.getLength();
                            int tabNumber = -1;
                            for (int i = 0; i < size; i++) {
                                Node child = tabStopList.item(i);
                                if (!(child instanceof Element)) {
                                    // avoid line breaks, when XML is indented
                                    continue;
                                } else {
                                    tabNumber++;
                                    extractTabulatorLeaderText((StyleTabStopElement) child, odfProps, tabNumber);
                                    extractTabulatorPosition((StyleTabStopElement) child, odfProps, tabNumber);
                                    extractTabulatorType((StyleTabStopElement) child, odfProps, tabNumber);
                                }
                            }
                        }
                    }
                }
                if (!odfProps.isEmpty()) {
                    allOdfProps.put(styleFamilyKey, odfProps);
                }
            }
        }
    }

    /**
     * style:type fillChar String Type of fill character. 'dot' Tab will be
     * filled with dot chars: . . . . . . . . . . . 'hyphen' Tab will be filled
     * with hyphen chars: - - - - - - - - - 'underscore' Tab will be filled with
     * underscore chars _ _ _ _ _ _ _ none Tab is just empty space.
     */
    private static void extractTabulatorType(StyleTabStopElement tabStopElement, Map<String, String> odfProps, int tabNumber) {
        String tabType = tabStopElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "type");

        if (tabType != null && !tabType.isEmpty()) {
            if (tabType.equals("char")) {
                tabType = "decimal";
            }
            odfProps.put("tab_Type" + tabNumber, tabType);
        }
    }

    /**
     * style:char fillChar String Type of fill character. 'dot' Tab will be
     * filled with dot chars: . . . . . . . . . . . 'hyphen' Tab will be filled
     * with hyphen chars: - - - - - - - - - 'underscore' Tab will be filled with
     * underscore chars _ _ _ _ _ _ _ none Tab is just empty space.
     */
    private static void extractTabulatorLeaderText(StyleTabStopElement tabStopElement, Map<String, String> odfProps, int tabNumber) {
        String tabLeaderText = tabStopElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "leader-text");

        if (tabLeaderText != null & !tabLeaderText.isEmpty()) {
            if (tabLeaderText.equals(Constants.DOT_CHAR)) {
                odfProps.put("tab_LeaderText" + tabNumber, Constants.DOT);
            } else if (tabLeaderText.equals(Constants.HYPHEN_CHAR)) {
                odfProps.put("tab_LeaderText" + tabNumber, Constants.HYPHEN);
            } else if (tabLeaderText.equals(Constants.UNDERSCORE_CHAR)) {
                odfProps.put("tab_LeaderText" + tabNumber, Constants.UNDERSCORE);
            } else if (tabLeaderText.equals(Constants.SPACE_CHAR)) {
                odfProps.put("tab_LeaderText" + tabNumber, Constants.NONE);
            }
        }
    }

    /**
     * In LO/AO Tabs are counted from the start of text and the fo:left-margin
     * have to be added. Microsoft Office does not behave this way. For the
     * filter a compatibility flag was added to the settings.xml
     * <config:config-item config:name="TabsRelativeToIndent"
     * config:type="boolean">false</config:config-item>
     */
    static private boolean hasTabsRelativeToIndent(OdfElement element) {
        boolean isTabsRelativeToIndent = false;
        OdfSchemaDocument schemaDoc = (OdfSchemaDocument) ((OdfFileDom) element.getOwnerDocument()).getDocument();
        if (schemaDoc instanceof OdfTextDocument) {
            isTabsRelativeToIndent = ((OdfTextDocument) schemaDoc).hasTabsRelativeToIndent();
        }
        return isTabsRelativeToIndent;
    }

    /**
     * style:position pos Integer 0 Tab stop position in 1/100th mm.
     */
    private static void extractTabulatorPosition(StyleTabStopElement tabStopElement, Map<String, String> odfProps, int tabNumber) {
        String tabPos = tabStopElement.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "position");
        int tabIndent = 0;
        /**
         * >20 years ago someone in the StarOffice Writer team thought it was a
         * brilliant idea to align tabs from the real text start. Therefore to
         * ODF from LO/AO we need to addChild the left-margin to the
         * tab-position, to get the REAL tab-position. If the tabs are relative
         * to indent (margin) we need to addChild the left margin. But there is
         * a compatibility mode for the MS Office formats filter, where this can
         * be disabled:
         * <config:config-item config:name="TabsRelativeToIndent"
         * config:type="boolean">false</config:config-item>
         */
        if (odfProps.containsKey("fo:margin-left") && hasTabsRelativeToIndent(tabStopElement)) {
            // get the variable from the text document
            String propValue = odfProps.get("fo:margin-left");
            if (!propValue.contains(PERCENT)) {
                tabIndent = MapHelper.normalizeLength(propValue);
            }
        }
        if (tabPos.isEmpty()) {
            LOG.severe("There should be only be a length, but it has been: '" + tabPos + "'");
        } else {
            odfProps.put("tab_Pos" + tabNumber, Integer.toString(MapHelper.normalizeLength(tabPos) + tabIndent));
        }
    }

    //convert xmlschema-2 date to double
    public static Double dateToDouble(Object value) {
        Double ret = new Double(0.);
        if (value != null && value instanceof String) {
            // ISO 8601 formatter for date-time without time zone.
            FastDateFormat fdf = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT;
            Date date = null;
            try {
                date = fdf.parse((String) value);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                long diff = cal.getTimeInMillis() + 2209161600000l; //30.12.1899
                ret = diff / 86400000.;
            } catch (ParseException ex) {
                Logger.getLogger(MapHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    //convert xmlschema-2 duration to double

    public static Double timeToDouble(Object value) {
        Double ret = new Double(0.);
        if (value != null && value instanceof String) {
            //PThhHmmMss.sssS
            try {
                String duration = (String) value;
                int length = duration.length();
                double hours = 0;
                double mins = 0;
                double millisecs = 0.;
                if (length >= 11) {
                    int hPos = duration.indexOf('H');
                    int mPos = duration.indexOf('M');

                    hours = Integer.parseInt(duration.substring(2, hPos));
                    mins = Integer.parseInt(duration.substring(hPos + 1, mPos));
                    millisecs = Double.parseDouble(duration.substring(mPos + 1, length - 1));
                    ret = hours / 24. + mins / 1440. + millisecs / 86400.;
                }
            } catch (IllegalArgumentException e) {

            }
        }
        return ret;
    }

    /**
     * get number format attribute via data-style-name attribute from one of the
     * different number format style elements either from office styles or
     * automatic styles names are (implicitly!) unique so it can only be found
     * in one of the two containers
     *
     * @param jsonStyleProperties
     * @param stringProperties
     * @param autoStyle
     * @param autoStyles auto style interface, is allowed to b null
     * @param officeStyles should always be set
     * @return
     */
    public static boolean putNumberFormat(Map<String, Object> jsonStyleProperties, Map<String, Map<String, String>> stringProperties, OdfStyle autoStyle,
            OdfStylesBase autoStyles, OdfStylesBase officeStyles) {
        boolean ret = false;
        if (autoStyle != null && autoStyle.hasAttribute("style:data-style-name")) {
            String dataStyleName = autoStyle.getAttribute("style:data-style-name");
            String formatCode = "";
            DataStyleElement dataStyleBase = null;
            dataStyleBase = officeStyles.getAllDataStyles().get(dataStyleName);
            if (dataStyleBase == null && autoStyles != null) {
                dataStyleBase = autoStyles.getAllDataStyles().get(dataStyleName);
            }
            if (dataStyleBase != null) {
                formatCode = dataStyleBase.getFormat(true);
            }
            if (!formatCode.isEmpty()) {
                ret = true;
                JSONObject jsonCellProps = new JSONObject();
                if (jsonStyleProperties != null) {
                    if (jsonStyleProperties.containsKey("cell")) {
                        jsonCellProps = (JSONObject) jsonStyleProperties.get("cell");
                    }
                    try {
                        jsonCellProps.put("formatCode", formatCode);
                    } catch (JSONException e) {
                    }
                    jsonStyleProperties.put("cell", jsonCellProps);
                } else {
                    Map<String, String> stringCellProps = new HashMap<String, String>();
                    if (stringProperties.containsKey("cell")) {
                        stringCellProps = stringProperties.get("cell");
                    }
                    stringCellProps.put("numberformat_code", formatCode);
                    stringProperties.put("cell", stringCellProps);

                }
            }

        }
        return ret;
    }

    public static String removeQuotedAndColor(String compareCode) {
        while (compareCode.contains("\"")) {
            int firstQuote = compareCode.indexOf("\"");
            if (firstQuote == compareCode.length() - 1) {
                break;
            }
            int secondQuote = compareCode.indexOf("\"", firstQuote + 1);
            if (secondQuote < 0) {
                break;
            }
            String tmp = compareCode.substring(0, firstQuote);
            tmp += compareCode.substring(secondQuote + 1);
            compareCode = tmp;
        }
        int openBracket = compareCode.indexOf("[");
        boolean hasCurrency = false;
        while (openBracket >= 0) {
            int closeBracket = compareCode.indexOf("]", openBracket);
            if (closeBracket > openBracket) {
                String innerText = compareCode.substring(openBracket + 1, closeBracket);
                if (innerText.startsWith("$") && innerText.length() > 1) {
                    hasCurrency = true;
                }
                compareCode = compareCode.substring(0, openBracket) + compareCode.substring(closeBracket + 1);
            }
            openBracket = compareCode.indexOf("[");
        }
        if (hasCurrency) {
            compareCode += "[$]"; //reinsert currency marker
        }
        return compareCode;
    }

    public static Value detectFormatType(String code) {
        String compareCode = code.replaceAll("AM", "xx9999xx");
        compareCode = compareCode.replaceAll("PM", "xx9999xx");
        //remove quoted parts
        compareCode = MapHelper.removeQuotedAndColor(compareCode);
        Value type = OfficeValueTypeAttribute.Value.VOID;
        if (compareCode.contains("@")) {
            type = OfficeValueTypeAttribute.Value.STRING;
        } else if (compareCode.equals("BOOLEAN")) {
            type = OfficeValueTypeAttribute.Value.BOOLEAN;
        } else if (compareCode.contains("[$")) {
            type = OfficeValueTypeAttribute.Value.CURRENCY;
        } else if (compareCode.contains("%")) {
            type = OfficeValueTypeAttribute.Value.PERCENTAGE;
        } else if (compareCode.contains("D") || compareCode.contains("d") || compareCode.contains("M") || compareCode.contains("Y") || compareCode.contains("y")
                || compareCode.contains("WW") || compareCode.contains("ww")) {
            type = OfficeValueTypeAttribute.Value.DATE;
        } else if (compareCode.contains("h") || compareCode.contains("H") || compareCode.contains("m") || compareCode.contains("s") || compareCode.contains("S")
                || compareCode.contains("xx9999xx")) {
            type = OfficeValueTypeAttribute.Value.TIME;
        } else {
            type = OfficeValueTypeAttribute.Value.FLOAT;
        }
        return type;
    }

    public static String findOrCreateDataStyle(String code, long id, OdfFileDom fileDom) {
        OdfDocument odfDocument = (OdfDocument) fileDom.getDocument();
        String ret = "";
        try {
            OdfContentDom contentDom = odfDocument.getContentDom();
            OdfOfficeStyles officeStyles = odfDocument.getStylesDom().getOfficeStyles();
            OdfOfficeAutomaticStyles autoStyles = contentDom.getAutomaticStyles();
            Value type = OfficeValueTypeAttribute.Value.VOID;//.BOOLEAN CURRENCY DATE FLOAT PERCENTAGE STRING TIME
            // if id > -1 then assign an appropriate format and type
            if (id > 0 && id < 164) {
                switch ((int) id) {
                    case 1:
                        code = "0";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 2:
                        code = "0.00";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 3:
                        code = "#,##0";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 4:
                        code = "#,##0.00";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 5:
                        code = "#,##0[$$-409]";
                        type = OfficeValueTypeAttribute.Value.CURRENCY;
                        break;
                    case 6:
                        code = "#,##0[$$-409];[RED]-#,##0[$$-409]";
                        type = OfficeValueTypeAttribute.Value.CURRENCY;
                        break;
                    case 7:
                        code = "#,##0.00[$$-409]";
                        type = OfficeValueTypeAttribute.Value.CURRENCY;
                        break;
                    case 8:
                        code = "#,##0.00[$$-409];[RED]-#,##0.00[$$-409]";
                        type = OfficeValueTypeAttribute.Value.CURRENCY;
                        break;
                    case 9:
                        code = "0%";
                        type = OfficeValueTypeAttribute.Value.PERCENTAGE;
                        break;
                    case 10:
                        code = "0.00%";
                        type = OfficeValueTypeAttribute.Value.PERCENTAGE;
                        break;
                    case 11:
                        code = "0.00E+00";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 12:
                        code = "# ?/?";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 13:
                        code = "# ??/??";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 14:
                        code = "MM/DD/YYYY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 15:
                        code = "D-MMM-YY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 16:
                        code = "D-MMM";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 17:
                        code = "MMM-YY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 18:
                        code = "H:MM AM/PM";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 19:
                        code = "H:MM:SS AM/PM";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 20:
                        code = "H:MM";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 21:
                        code = "H:MM:SS";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 22:
                        code = "M/D/YYYY H:MM";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;//"DATETIME_SYSTEM_SHORT_HHMM,
                    case 23:  //code = "0";                                      type = OfficeValueTypeAttribute.Value.FLOAT; break;
                    case 24:  //code = "0";                                      type = OfficeValueTypeAttribute.Value.FLOAT; break;
                    case 25:  //code = "0";                                      type = OfficeValueTypeAttribute.Value.FLOAT; break;
                    case 26:
                        code = "0";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                        code = "M/D/YYYY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                        code = "HH:MM:SS";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 36:
                        code = "M/D/YYYY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 37:
                        code = "#,##0_);(#,##0)";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 38:
                        code = "#,##0_);[RED](#,##0)";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 39:
                        code = "#,##0.00_);(#,##0.00)";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 40:
                        code = "#,##0.00_);[RED](#,##0.00)";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 41:
                        code = "_-* #,##0 _\u20ac_-;-* #,##0 _\u20ac_-;_-* \"-\" _\u20ac_-;_-@_-";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 42:
                        code = "_-* #,##0 \"\u20ac\"_-;-* #,##0 \"\u20ac\"_-;_-* \"-\" \"\u20ac\"_-;_-@_-";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 43:
                        code = "_-* #,##0.00 _\u20ac_-;-* #,##0.00 _\u20ac_-;_-* \"-\"?? _\u20ac_-;_-@_-";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 44:
                        code = "_-* #,##0.00 \"\u20ac\"_-;-* #,##0.00 \"\u20ac\"_-;_-* \"-\"?? \"\u20ac\"_-;_-@_-";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 45:
                        code = "MM:SS";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 46:
                        code = "[h]:mm:ss";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 47:
                        code = "mm:ss.0";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 48:
                        code = "##0.0E+0";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 49:
                        code = "@";
                        type = OfficeValueTypeAttribute.Value.STRING;
                        break;
                    case 50:
                        code = "M/D/YYYY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                        code = "M/D/YYYY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 59:
                        code = "0";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 60:
                        code = "0.00";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 61:
                        code = "#,##0";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 62:
                        code = "#,##0.00";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 63:
                        code = "#,##0 \"\u20ac\";-#,##0 \"\u20ac\"";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 64:
                        code = "#,##0 \"\u20ac\";[RED]-#,##0 \"\u20ac\"";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 65:
                        code = "#,##0.00 \"\u20ac\";-#,##0.00 \"\u20ac\"";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 66:
                        code = "#,##0.00 \"\u20ac\";[RED]-#,##0.00 \"\u20ac\"";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 67:
                        code = "0%";
                        type = OfficeValueTypeAttribute.Value.PERCENTAGE;
                        break;
                    case 68:
                        code = "0.00%";
                        type = OfficeValueTypeAttribute.Value.PERCENTAGE;
                        break;
                    case 69:
                        code = "0.00E+00";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 70:
                        code = "# ?/?";
                        type = OfficeValueTypeAttribute.Value.FLOAT;
                        break;
                    case 71:  //code = "M/D/YYYY"; type = OfficeValueTypeAttribute.Value.DATE; break;
                    case 72:
                        code = "M/D/YYYY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 73:
                        code = "D-MMM-YY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 74:
                        code = "D-MMM";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 75:
                        code = "MMM-YY";
                        type = OfficeValueTypeAttribute.Value.DATE;
                        break;
                    case 76:
                        code = "HH:MM";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 77:
                        code = "HH:MM:SS";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 78:
                        code = "M/D/YYYY H:MM";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 79:
                        code = "mm:ss";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 80:
                        code = "[h]:mm:ss";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                    case 81:
                        code = "mm:ss.0";
                        type = OfficeValueTypeAttribute.Value.TIME;
                        break;
                }
            }
            if (!code.isEmpty() && (type == OfficeValueTypeAttribute.Value.VOID)) {
                type = detectFormatType(code);
            }
            if (!code.isEmpty() && type != OfficeValueTypeAttribute.Value.VOID) {
                String foundStyleName = null;
                boolean foundInAutoStyles = false;
                HashMap<String, DataStyleElement> officeDataStyles = officeStyles.getAllDataStyles();
                HashMap<String, DataStyleElement> autoDataStyles = autoStyles.getAllDataStyles();
                for (Entry<String, DataStyleElement> autoNumberStyle : autoDataStyles.entrySet()) {
                    if (autoNumberStyle.getValue().getFormat(true).equals(code)) {
                        foundStyleName = autoNumberStyle.getKey();
                        ret = foundStyleName;
                        foundInAutoStyles = true;
                        break;
                    }
                }
                if (!foundInAutoStyles) {
                    for (Entry<String, DataStyleElement> officeNumberStyle : officeDataStyles.entrySet()) {
                        if (officeNumberStyle.getValue().getFormat(true).equals(code)) {
                            foundStyleName = officeNumberStyle.getKey();
                            ret = foundStyleName;
                            break;
                        }
                    }
                }
                if (foundStyleName == null) {
                    //find free name
                    int newIndex = autoDataStyles.size() + officeDataStyles.size();
                    String newDataStyleName = "N" + newIndex;
                    while (autoDataStyles.containsKey(newDataStyleName) || officeDataStyles.containsKey(newDataStyleName)) {
                        newDataStyleName = "N" + ++newIndex;
                    }
                    final DataStyleElement newStyle = autoStyles.createDataStyle(type, code, newDataStyleName);
                    if (id == 14) {
                        newStyle.setAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "number:automatic-order", "true");
                        newStyle.removeAttributeNS(OdfDocumentNamespace.NUMBER.getUri(), "format-source");
                    }
                    ret = newDataStyleName;
                }
            }
        } catch (SAXException e) {
            Logger.getLogger(MapHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException ex) {
            Logger.getLogger(MapHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    static private void fillLocaleMaps() {
        if (localeToLanguageMap == null) {
            localeToLanguageMap = new HashMap<String, Integer>();
            languageToLocaleMap = new HashMap<Integer, String>();

            class StringAndInt {

                public String locale;
                public int msValue;

                public StringAndInt(String s, int v) {
                    locale = s;
                    msValue = v;
                }
            }

            StringAndInt mapping[] = {
                new StringAndInt("af-ZA", 0x436),
                new StringAndInt("sq-AL", 0x41c),
                new StringAndInt("gsw-FR", 0x484),
                new StringAndInt("am-ET", 0x45e),
                new StringAndInt("ar-DZ", 0x1401),
                new StringAndInt("ar-BH", 0x3c01),
                new StringAndInt("ar-EG", 0xc01),
                new StringAndInt("ar-IQ", 0x801),
                new StringAndInt("ar-JO", 0x2c01),
                new StringAndInt("ar-KW", 0x3401),
                new StringAndInt("ar-LB", 0x3001),
                new StringAndInt("ar-LY", 0x1001),
                new StringAndInt("ar-MA", 0x1801),
                new StringAndInt("ar-OM", 0x2001),
                new StringAndInt("ar-QA", 0x4001),
                new StringAndInt("ar-SA", 0x401),
                new StringAndInt("ar-SY", 0x2801),
                new StringAndInt("ar-TN", 0x1c01),
                new StringAndInt("ar-AE", 0x3801),
                new StringAndInt("ar-YE", 0x2401),
                new StringAndInt("ar", 0x1),
                new StringAndInt("hy-AM", 0x42b),
                new StringAndInt("as-IN", 0x44d),
                new StringAndInt("az", 0x2c),
                new StringAndInt("az-cyrillic", 0x82c),
                new StringAndInt("az-AZ", 0x42c),
                new StringAndInt("ba-RU", 0x46d),
                new StringAndInt("eu", 0x42d),
                new StringAndInt("be-BY", 0x423),
                new StringAndInt("bn-IN", 0x445),
                new StringAndInt("bn-BD", 0x845),
                new StringAndInt("bs-BA", 0x141a),
                //            new StringAndInt("", 0x201a),
                new StringAndInt("br-FR", 0x47e),
                new StringAndInt("bg-BG", 0x402),
                new StringAndInt("my-MM", 0x455),
                new StringAndInt("ca-ES", 0x403),
                new StringAndInt("chr-US", 0x45c),
                new StringAndInt("zh", 0x4),
                new StringAndInt("zh-HK", 0xc04),
                new StringAndInt("zh-MO", 0x1404),
                new StringAndInt("zh-CN", 0x804),
                new StringAndInt("zh-SG", 0x1004),
                new StringAndInt("zh-TW", 0x404),
                new StringAndInt("co-FR", 0x483),
                new StringAndInt("hr-HR", 0x41a),
                new StringAndInt("hr-BA", 0x101a),
                new StringAndInt("cs-CZ", 0x405),
                new StringAndInt("da-DK", 0x406),
                new StringAndInt("gbz-AF", 0x48c),
                new StringAndInt("dv-MV", 0x465),
                new StringAndInt("nl-NL", 0x413),
                new StringAndInt("nl-BE", 0x813),
                new StringAndInt("bin-NG", 0x466),
                new StringAndInt("en", 0x9),
                new StringAndInt("en-AU", 0xc09),
                new StringAndInt("en-BZ", 0x2809),
                new StringAndInt("en-CA", 0x1009),
                new StringAndInt("en-BS", 0x2409),
                new StringAndInt("en-IE", 0x1809),
                new StringAndInt("en-HK", 0x3c09),
                new StringAndInt("en-IN", 0x4009),
                new StringAndInt("en-ID", 0x3809),
                new StringAndInt("en-JM", 0x2009),
                new StringAndInt("en-MY", 0x4409),
                new StringAndInt("en-NZ", 0x1409),
                new StringAndInt("en-PH", 0x3409),
                new StringAndInt("en-ZA", 0x1c09),
                new StringAndInt("en-SG", 0x4809),
                new StringAndInt("en-TT", 0x2c09),
                new StringAndInt("en-GB", 0x809),
                new StringAndInt("en-US", 0x409),
                new StringAndInt("en-ZW", 0x3009),
                new StringAndInt("et-EE", 0x425),
                new StringAndInt("fo-FO", 0x438),
                new StringAndInt("fa-IR", 0x429),
                new StringAndInt("fil-PH", 0x464),
                new StringAndInt("fi-FI", 0x40b),
                new StringAndInt("fr-FR", 0x40c),
                new StringAndInt("fr-BE", 0x80c),
                new StringAndInt("fr-CM", 0x2c0c),
                new StringAndInt("fr-CA", 0xc0c),
                new StringAndInt("fr-CI", 0x300c),
                new StringAndInt("fr-HT", 0x3c0c),
                new StringAndInt("fr-LU", 0x140c),
                new StringAndInt("fr-ML", 0x340c),
                new StringAndInt("fr-MC", 0x180c),
                new StringAndInt("fr-MA", 0x380c),
                new StringAndInt("fr", 0xe40c),
                new StringAndInt("fr-RE", 0x200c),
                new StringAndInt("fr-SN", 0x280c),
                new StringAndInt("fr-CH", 0x100c),
                new StringAndInt("fr", 0x1c0c),
                new StringAndInt("fr-CD", 0x240c),
                new StringAndInt("fy-NL", 0x462),
                new StringAndInt("ff-NG", 0x467),
                new StringAndInt("ga-IE", 0x83c),
                new StringAndInt("gd-GB", 0x43c),
                new StringAndInt("gl-ES", 0x456),
                new StringAndInt("ka-GE", 0x437),
                new StringAndInt("de-DE", 0x407),
                new StringAndInt("de-AT", 0xc07),
                new StringAndInt("de-LI", 0x1407),
                new StringAndInt("de-LU", 0x1007),
                new StringAndInt("de-CH", 0x807),
                new StringAndInt("el-GR", 0x408),
                new StringAndInt("gug-PY", 0x474),
                new StringAndInt("gu-IN", 0x447),
                new StringAndInt("ha-NG", 0x468),
                new StringAndInt("haw-US", 0x475),
                new StringAndInt("he-IL", 0x40d),
                new StringAndInt("hi-IN", 0x439),
                new StringAndInt("hu-HU", 0x40e),
                //            new StringAndInt("", 0x469),
                new StringAndInt("is-IS", 0x40f),
                new StringAndInt("ig-NG", 0x470),
                new StringAndInt("id-ID", 0x421),
                //            new StringAndInt("", 0x45d),
                new StringAndInt("iu-CA", 0x85d),
                new StringAndInt("it-IT", 0x410),
                new StringAndInt("it-CH", 0x810),
                new StringAndInt("ja-JP", 0x411),
                new StringAndInt("kl-GL", 0x46f),
                new StringAndInt("kn-IN", 0x44b),
                new StringAndInt("kr-NG", 0x471),
                new StringAndInt("ks", 0x460),
                new StringAndInt("ks-IN", 0x860),
                new StringAndInt("kk-KZ", 0x43f),
                new StringAndInt("km-KH", 0x453),
                new StringAndInt("qut-GT", 0x486),
                new StringAndInt("rw-RW", 0x487),
                new StringAndInt("ky-KG", 0x440),
                new StringAndInt("kok-IN", 0x457),
                new StringAndInt("ko-KR", 0x412),
                new StringAndInt("ko-KR", 0x812),
                new StringAndInt("lo-LA", 0x454),
                new StringAndInt("la-VA", 0x476),
                new StringAndInt("lv-LV", 0x426),
                new StringAndInt("lt-LT", 0x427),
                new StringAndInt("lt-LT", 0x827),
                new StringAndInt("lb-LU", 0x46e),
                new StringAndInt("mk-MK", 0x42f),
                new StringAndInt("ms", 0x3e),
                new StringAndInt("ml-IN", 0x44c),
                new StringAndInt("ms-BN", 0x83e),
                new StringAndInt("ms-MY", 0x43e),
                new StringAndInt("mt-MT", 0x43a),
                new StringAndInt("mni-IN", 0x458),
                new StringAndInt("mi-NZ", 0x481),
                new StringAndInt("arn-CL", 0x47a),
                new StringAndInt("mr-IN", 0x44e),
                new StringAndInt("moh-CA", 0x47c),
                new StringAndInt("mn-MN", 0x450),
                new StringAndInt("mn-MN", 0x850),
                new StringAndInt("ne-NP", 0x461),
                new StringAndInt("ne-IN", 0x861),
                new StringAndInt("no-NO", 0x14),
                new StringAndInt("nb-NO", 0x414),
                new StringAndInt("nn-NO", 0x814),
                new StringAndInt("oc-FR", 0x482),
                new StringAndInt("or-IN", 0x448),
                new StringAndInt("om-ET", 0x472),
                new StringAndInt("pap-AN", 0x479),
                new StringAndInt("ps-AF", 0x463),
                new StringAndInt("pl-PL", 0x415),
                new StringAndInt("pt-PT", 0x816),
                new StringAndInt("pt-BR", 0x416),
                new StringAndInt("pa-IN", 0x446),
                new StringAndInt("lah-PK", 0x846),
                new StringAndInt("qu-BO", 0x46b),
                new StringAndInt("qu-EC", 0x86b),
                new StringAndInt("qu-PE", 0xc6b),
                new StringAndInt("rm-CH", 0x417),
                new StringAndInt("ro-RO", 0x418),
                new StringAndInt("ro-MD", 0x818),
                new StringAndInt("ru-RU", 0x419),
                new StringAndInt("mo-MD", 0x819),
                new StringAndInt("se-NO", 0x43b),
                new StringAndInt("smn-FI", 0x243b),
                new StringAndInt("smj-NO", 0x103b),
                new StringAndInt("smj-SE", 0x143b),
                new StringAndInt("se-FI", 0xc3b),
                new StringAndInt("se-SE", 0x83b),
                new StringAndInt("sms-FI", 0x203b),
                new StringAndInt("sma-NO", 0x183b),
                new StringAndInt("sma-SE", 0x1c3b),
                new StringAndInt("sa-IN", 0x44f),
                new StringAndInt("nso-ZA", 0x46c),
                new StringAndInt("sr", 0x1a),
                new StringAndInt("sr-YU", 0xc1a),
                new StringAndInt("sr-BA", 0x1c1a),
                new StringAndInt("sh-YU", 0x81a),
                new StringAndInt("sh-BA", 0x181a),
                new StringAndInt("sh", 0x7c1a),
                new StringAndInt("st-ZA", 0x430),
                new StringAndInt("sd-IN", 0x459),
                new StringAndInt("sd-PK", 0x859),
                new StringAndInt("si-LK", 0x45b),
                new StringAndInt("sk-SK", 0x41b),
                new StringAndInt("sl-SI", 0x424),
                new StringAndInt("so-SO", 0x477),
                new StringAndInt("hsb-DE", 0x42e),
                new StringAndInt("dsb-DE", 0x82e),
                new StringAndInt("es-ES", 0x40a),
                new StringAndInt("es-AR", 0x2c0a),
                new StringAndInt("es-BO", 0x400a),
                new StringAndInt("es-CL", 0x340a),
                new StringAndInt("es-CO", 0x240a),
                new StringAndInt("es-CR", 0x140a),
                new StringAndInt("es-DO", 0x1c0a),
                new StringAndInt("es-EC", 0x300a),
                new StringAndInt("es-SV", 0x440a),
                new StringAndInt("es-GT", 0x100a),
                new StringAndInt("es-HN", 0x480a),
                new StringAndInt("es", 0xe40a),
                new StringAndInt("es-MX", 0x80a),
                new StringAndInt("es-ES", 0xc0a),
                new StringAndInt("es-NI", 0x4c0a),
                new StringAndInt("es-PA", 0x180a),
                new StringAndInt("es-PY", 0x3c0a),
                new StringAndInt("es-PE", 0x280a),
                new StringAndInt("es-PR", 0x500a),
                new StringAndInt("es-US", 0x540a),
                new StringAndInt("es-UY", 0x380a),
                new StringAndInt("es-VE", 0x200a),
                new StringAndInt("sw-KE", 0x441),
                new StringAndInt("sv-SE", 0x41d),
                new StringAndInt("sv-FI", 0x81d),
                new StringAndInt("syr-TR", 0x45a),
                new StringAndInt("tg-TJ", 0x428),
                //            new StringAndInt("", 0x45f),
                //            new StringAndInt("", 0x85f),
                new StringAndInt("ta-IN", 0x449),
                new StringAndInt("tt-RU", 0x444),
                new StringAndInt("te-IN", 0x44a),
                new StringAndInt("th-TH", 0x41e),
                new StringAndInt("bo-CN", 0x451),
                new StringAndInt("dz-BT", 0x851),
                new StringAndInt("ti-ER", 0x873),
                new StringAndInt("ti-ET", 0x473),
                new StringAndInt("ts-ZA", 0x431),
                new StringAndInt("tn-ZA", 0x432),
                new StringAndInt("tr-TR", 0x41f),
                new StringAndInt("tk-TM", 0x442),
                new StringAndInt("ug-CN", 0x480),
                new StringAndInt("uk-UA", 0x422),
                new StringAndInt("ur", 0x20),
                new StringAndInt("ur-IN", 0x820),
                new StringAndInt("ur-PK", 0x420),
                //            new StringAndInt("", 0x843),
                new StringAndInt("uz-UZ", 0x443),
                new StringAndInt("ve-ZA", 0x433),
                new StringAndInt("vi-VN", 0x42a),
                new StringAndInt("cy-GB", 0x452),
                new StringAndInt("wo-SN", 0x488),
                new StringAndInt("xh-ZA", 0x434),
                new StringAndInt("sah-RU", 0x485),
                new StringAndInt("ii-CN", 0x478),
                new StringAndInt("yi-IL", 0x43d),
                new StringAndInt("yo-NG", 0x46a),
                new StringAndInt("zu-ZA", 0x435),
                null};
            int index = 0;
            while (mapping[index] != null) {
                localeToLanguageMap.put(mapping[index].locale, mapping[index].msValue);
                languageToLocaleMap.put(mapping[index].msValue, mapping[index].locale);
                ++index;
            }
        }
    }

    /**
     * converts a language code from a string and interpretes the number as hex
     * value and returns the a locale or null if either the format is wrong or
     * no locale is known
     */
    static public String getLocaleFromLangCode(String languageCode) {
        if (languageToLocaleMap == null) {
            fillLocaleMaps();
        }
        String ret = "";
        try {
            int languageValue = Integer.parseInt(languageCode, 16);
            ret = languageToLocaleMap.get(languageValue);
        } catch (NumberFormatException e) {
            //no handling required
        }
        return ret;
    }

    /**
     * converts locale information to a hex value string but without a hex
     * marker like '0x' returns null if no value can be found
     */
    static public String getMSLangCode(String language, String country) {
        if (localeToLanguageMap == null) {
            fillLocaleMaps();
        }
        String cmpString = new String(language);
        if (!country.isEmpty()) {
            cmpString += "-" + country;
        }
        Integer entry = localeToLanguageMap.get(cmpString);
        if (entry != null) {
            return Integer.toHexString(entry.intValue());
        }
        return "";
    }

    static public void moveParaToCell(Map<String, Object> allHardFormatting) {
        //move some paragraph properties to cell properties
        if (allHardFormatting != null && allHardFormatting.containsKey("paragraph")) {
            JSONObject paraProps = (JSONObject) allHardFormatting.get("paragraph");
            if (paraProps.has("alignment")) {
                if (!allHardFormatting.containsKey("cell")) {
                    allHardFormatting.put("cell", new JSONObject());
                }
                JSONObject cellProps = (JSONObject) allHardFormatting.get("cell");
                try {
                    cellProps.put("alignHor", paraProps.opt("alignment"));
                    paraProps.remove("alignment");
                } catch (JSONException e) {
                    //no handling required
                }
            }
        }
    }

}
