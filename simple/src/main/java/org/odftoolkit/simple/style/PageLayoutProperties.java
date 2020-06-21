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

import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBorderTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoMarginTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoPageHeightAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoPageWidthAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthBottomAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthLeftAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthRightAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleBorderLineWidthTopAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleColorAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDistanceAfterSepAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleDistanceBeforeSepAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleNumFormatAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StylePrintOrientationAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleRelWidthAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleWidthAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleFootnoteSepElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.odfdom.type.Length;
import org.odftoolkit.odfdom.type.Length.Unit;
import org.odftoolkit.odfdom.type.Percent;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AdjustmentStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.LineStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.WritingMode;

/**
 * This class represents the page layout style settings. It provides methods to access borders,
 * margins,number format, page width, page height and etc. More functions will be added latter.
 *
 * <p>This class is a corresponded high level class for element "style:page-layout-properties". It
 * provides methods to access the attributes and children of "style:page-layout-properties".
 *
 * @since 0.8
 */
public class PageLayoutProperties {
  // fo:background-color

  // fo:border, style:border-line-width
  // fo:border-bottom, style:border-line-width-bottom
  // fo:border-left, style:border-line-width-left
  // fo:border-right, style:border-line-width-right
  // fo:border-top, style:border-line-width-top

  // fo:margin
  // fo:margin-bottom
  // fo:margin-left
  // fo:margin-right
  // fo:margin-top

  // fo:padding
  // fo:padding-bottom
  // fo:padding-left
  // fo:padding-right
  // fo:padding-top

  // fo:page-height
  // fo:page-width

  // style:layout-grid-base-height
  // style:layout-grid-base-width
  // style:layout-grid-color
  // style:layout-grid-display
  // style:layout-grid-lines
  // style:layout-grid-mode
  // style:layout-grid-print
  // style:layout-grid-ruby-below
  // style:layout-grid-ruby-height
  // style:layout-grid-snap-to
  // style:layout-grid-standard-mode

  // style:num-format
  // style:num-letter-sync
  // style:num-prefix
  // style:num-suffix

  // style:print
  // style:print-orientation
  // style:first-page-number
  // style:footnote-max-height
  // style:paper-tray-name
  // style:print-page-order
  // style:register-truth-ref-style-name
  // style:scale-to
  // style:scale-to-pages
  // style:shadow
  // style:table-centering
  // style:writing-mode

  // the default size as used for left-, right-margin and indention
  private static final String DEFAULT_LENGTH = "0in";
  private static final String DEFAULT_PERCENT = "0%";
  StylePageLayoutPropertiesElement mElement;
  StyleFootnoteSepElement mFootnoteSepElement;
  BorderPropertiesImpl mBorderPropertiesHandler;

  /** Create an instance of PageLayoutProperties */
  protected PageLayoutProperties() {}

  /**
   * Create an instance of PageLayoutProperties from an element <style:page-layout-properties>
   *
   * @param pageLayoutProperties - the element of style:page-layout-properties
   */
  protected PageLayoutProperties(StylePageLayoutPropertiesElement pageLayoutProperties) {
    mElement = pageLayoutProperties;
    mFootnoteSepElement =
        (StyleFootnoteSepElement)
            pageLayoutProperties
                .getElementsByTagName(StyleFootnoteSepElement.ELEMENT_NAME.getQName())
                .item(0);
    mBorderPropertiesHandler = new BorderPropertiesImpl(mElement);
  }

  /**
   * Set the border style.
   *
   * <p>The first parameter <code>bordersType</code> describes which borders you want to apply the
   * style to, e.g. up border, bottom border, left border, right border or four borders.
   *
   * <p>The border style information will be removed if the parameter <code>bordersType</code> is
   * NONE.
   *
   * @param bordersType - the type of the borders
   * @param border - the border style description
   */
  public void setBorders(StyleTypeDefinitions.CellBordersType bordersType, Border border) {
    switch (bordersType) {
      case BOTTOM:
        setBottomBorder(border);
        break;
      case LEFT:
        setLeftBorder(border);
        break;
      case RIGHT:
        setRightBorder(border);
        break;
      case TOP:
        setTopBorder(border);
        break;
      case ALL_FOUR:
        setBorder(border);
        break;
      case LEFT_RIGHT:
        setLeftBorder(border);
        setRightBorder(border);
        break;
      case TOP_BOTTOM:
        setTopBorder(border);
        setBottomBorder(border);
        break;
      case NONE:
        mElement.removeAttribute(StyleBorderLineWidthAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(FoBorderAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(StyleBorderLineWidthBottomAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(FoBorderBottomAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(StyleBorderLineWidthTopAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(FoBorderTopAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(StyleBorderLineWidthLeftAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(FoBorderLeftAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(StyleBorderLineWidthRightAttribute.ATTRIBUTE_NAME.getQName());
        mElement.removeAttribute(FoBorderRightAttribute.ATTRIBUTE_NAME.getQName());
    }
  }

  /**
   * Return the border setting for all four borders.
   *
   * <p>Null will be returned if there is no border setting for all four borders.
   *
   * @return the border setting
   */
  public Border getBorder() {
    return mBorderPropertiesHandler.getBorder();
  }

  /**
   * Return the border setting for the top border.
   *
   * <p>Null will be returned if there is no border setting for the top border.
   *
   * @return the border setting
   */
  public Border getTopBorder() {
    return mBorderPropertiesHandler.getTopBorder();
  }

  /**
   * Return the border setting for the left border.
   *
   * <p>Null will be returned if there is no border setting for the left border.
   *
   * @return the border setting
   */
  public Border getLeftBorder() {
    return mBorderPropertiesHandler.getLeftBorder();
  }

  /**
   * Return the border setting for the right border.
   *
   * <p>Null will be returned if there is no border setting for the right border.
   *
   * @return the border setting
   */
  public Border getRightBorder() {
    return mBorderPropertiesHandler.getRightBorder();
  }

  // fo:border-bottom
  // style:border-line-width-bottom
  /**
   * Return the border setting for the bottom border.
   *
   * <p>Null will be returned if there is no border setting for the bottom border.
   *
   * @return the border setting
   */
  public Border getBottomBorder() {
    return mBorderPropertiesHandler.getBottomBorder();
  }

  // fo:border
  // style:border-line-width
  /**
   * Set the border definition for all four borders.
   *
   * <p>If the parameter <code>border</code> is null, the border definition for all four borders
   * will be removed.
   *
   * <p>If the line type in the border definition is NONE, the border definition for all four
   * borders will be removed.
   *
   * @param border - the border setting
   */
  public void setBorder(Border border) {
    mBorderPropertiesHandler.setBorder(border);
  }

  // fo:border-bottom
  // style:border-line-width-bottom
  /**
   * Set the border definition for the bottom border.
   *
   * <p>If the parameter <code>border</code> is null, the border definition for the bottom border
   * will be removed.
   *
   * <p>If the line type in the border definition is NONE, the border definition for the bottom
   * border will be removed.
   *
   * @param border - the border setting
   */
  public void setBottomBorder(Border border) {
    mBorderPropertiesHandler.setBottomBorder(border);
  }

  // fo:border-top
  // style:border-line-width-top
  /**
   * Set the border definition for the top border.
   *
   * <p>If the parameter <code>border</code> is null, the border definition for the top border will
   * be removed.
   *
   * <p>If the line type in the border definition is NONE, the border definition for the top border
   * will be removed.
   *
   * @param border - the border setting
   */
  public void setTopBorder(Border border) {
    mBorderPropertiesHandler.setTopBorder(border);
  }

  // fo:border-left
  // style:border-line-width-left
  /**
   * Set the border definition for the left border.
   *
   * <p>If the parameter <code>border</code> is null, the border definition for the left border will
   * be removed.
   *
   * <p>If the line type in the border definition is NONE, the border definition for the left border
   * will be removed.
   *
   * @param border - the border setting
   */
  public void setLeftBorder(Border border) {
    mBorderPropertiesHandler.setLeftBorder(border);
  }

  // fo:border-right
  // style:border-line-width-right
  /**
   * Set the border definition for the right border.
   *
   * <p>If the parameter <code>border</code> is null, the border definition for the right border
   * will be removed.
   *
   * <p>If the line type in the border definition is NONE, the border definition for the right
   * border will be removed.
   *
   * @param border - the border setting
   */
  public void setRightBorder(Border border) {
    mBorderPropertiesHandler.setRightBorder(border);
  }

  /**
   * Set the left margin of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the left margin definition will be removed.
   *
   * @param marginLeft the size of the left margin (in Millimeter)
   */
  public void setMarginLeft(double marginLeft) {
    if (marginLeft == 0) {
      mElement.removeAttribute(FoMarginLeftAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setFoMarginLeftAttribute(getInchValue(marginLeft));
    }
  }

  /**
   * Returns the provided Millimeter value as Inch value
   *
   * @param value the value to set the attribute value to (in Millimeter)
   */
  private static String getInchValue(double value) {
    // build the string for mm
    final String mmValueString = value + Unit.MILLIMETER.abbr();
    // convert the length to inch
    final String inchValueString = Length.mapToUnit(mmValueString, Unit.INCH);

    // return the value
    return inchValueString;
  }

  /**
   * Get the size of the left margin of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the left margin definition will be removed.
   *
   * @return the size of the left margin (in Millimeter)
   */
  public double getMarginLeft() {
    // get the value
    String valueString = mElement.getFoMarginLeftAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Set the right margin of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the right margin definition will be removed.
   *
   * @param marginRight the size of the right margin (in Millimeter)
   */
  public void setMarginRight(double marginRight) {
    if (marginRight == 0) {
      mElement.removeAttribute(FoMarginRightAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setFoMarginRightAttribute(getInchValue(marginRight));
    }
  }

  /**
   * Get the size of the right margin of this <code>PageLayoutProperties</code>
   *
   * @return the size of the right margin (in Millimeter)
   */
  public double getMarginRight() {
    // get the value
    String valueString = mElement.getFoMarginRightAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Set the top margin of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the top margin definition will be removed.
   *
   * @param marginTop the size of the right margin (in Millimeter)
   */
  public void setMarginTop(double marginTop) {
    if (marginTop == 0) {
      mElement.removeAttribute(FoMarginTopAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setFoMarginTopAttribute(getInchValue(marginTop));
    }
  }

  /**
   * Get the size of the top margin of this <code>PageLayoutProperties</code>
   *
   * @return the size of the top margin (in Millimeter)
   */
  public double getMarginTop() {
    // get the value
    String valueString = mElement.getFoMarginTopAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Set the bottom margin of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the bottom margin definition will be removed.
   *
   * @param marginBottom the size of the bottom margin (in Millimeter)
   */
  public void setMarginBottom(double marginBottom) {
    if (marginBottom == 0) {
      mElement.removeAttribute(FoMarginBottomAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setFoMarginBottomAttribute(getInchValue(marginBottom));
    }
  }

  /**
   * Get the size of the bottom margin of this <code>PageLayoutProperties</code>
   *
   * @return the size of the bottom margin (in Millimeter)
   */
  public double getMarginBottom() {
    // get the value
    String valueString = mElement.getFoMarginBottomAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Get the number format of this <code>PageLayoutProperties</code>
   *
   * @return the number format
   */
  public String getNumberFormat() {
    return mElement.getStyleNumFormatAttribute();
  }

  /**
   * Set the number format of this <code>PageLayoutProperties</code>
   *
   * <p>If the parameter <code>format</code> is null, the definition will be removed.
   *
   * @param format specify the number format. The values of the style:num-format attribute are 1, i,
   *     I, string, an empty string, a or A.
   */
  public void setNumberFormat(String format) {
    if (format == null) {
      mElement.removeAttribute(StyleNumFormatAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setStyleNumFormatAttribute(format);
    }
  }

  /**
   * Get the page width of this <code>PageLayoutProperties</code>
   *
   * @return the size of page width (in Millimeter)
   */
  public double getPageWidth() {
    // get the value
    String valueString = mElement.getFoPageWidthAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Set the page width of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the page width definition will be removed.
   *
   * @param pageWidth the size of the page width (in Millimeter)
   */
  public void setPageWidth(double pageWidth) {
    if (pageWidth == 0) {
      mElement.removeAttribute(FoPageWidthAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setFoPageWidthAttribute(getInchValue(pageWidth));
    }
  }

  /**
   * Get the page height of this <code>PageLayoutProperties</code>
   *
   * @return the size of page height (in Millimeter)
   */
  public double getPageHeight() {
    // get the value
    String valueString = mElement.getFoPageHeightAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Set the page height of this <code>PageLayoutProperties</code>
   *
   * <p>If the size is set to zero, the page height definition will be removed.
   *
   * @param pageHeight the size of the page height (in Millimeter)
   */
  public void setPageHeight(double pageHeight) {
    if (pageHeight == 0) {
      mElement.removeAttribute(FoPageHeightAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setFoPageHeightAttribute(getInchValue(pageHeight));
    }
  }

  /**
   * Get the print orientation of this <code>PageLayoutProperties</code>
   *
   * @return the print orientation
   */
  public String getPrintOrientation() {
    return mElement.getStylePrintOrientationAttribute();
  }

  /**
   * Set the print orientation of this <code>PageLayoutProperties</code>
   *
   * <p>If the parameter <code>orientation</code> is null, the print orientation definition will be
   * removed.
   *
   * @param orientation - the print orientation
   */
  public void setPrintOrientation(StyleTypeDefinitions.PrintOrientation orientation) {
    if (orientation == null) {
      mElement.removeAttribute(StylePrintOrientationAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mElement.setStylePrintOrientationAttribute(orientation.toString());
    }
  }

  /**
   * Get the way in which a footnote separator line is aligned on a page.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, the default value "left" will be
   * returned.
   *
   * @return the adjustment value of footnote separator line.
   */
  public String getFootnoteSepAdjustment() {
    if (mFootnoteSepElement == null) {
      return AdjustmentStyle.LEFT.toString();
    } else {
      return mFootnoteSepElement.getStyleAdjustmentAttribute();
    }
  }

  /**
   * Get the color of footnote separator line.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, the null value will be returned.
   *
   * @return the color of footnote separator line.
   */
  public String getFootnoteSepColor() {
    if (mFootnoteSepElement == null) {
      return null;
    } else {
      return mFootnoteSepElement.getStyleColorAttribute();
    }
  }

  /**
   * Get the distance between a footnote separator line and the footnote text.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, null will be returned.
   *
   * @return the distance after a footnote separator line.
   */
  public double getFootnoteSepDistanceAfterSep() {
    if (mFootnoteSepElement == null) {
      return 0;
    } else {
      String value = mFootnoteSepElement.getStyleDistanceAfterSepAttribute();
      if (value == null) value = DEFAULT_LENGTH;
      return Length.parseDouble(value, Unit.MILLIMETER);
    }
  }

  /**
   * Get the distance between the text area and a footnote separator line.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, null will be returned.
   *
   * @return the distance before a footnote separator line.
   */
  public double getFootnoteSepDistanceBeforeSep() {
    if (mFootnoteSepElement == null) {
      return 0;
    } else {
      String value = mFootnoteSepElement.getStyleDistanceBeforeSepAttribute();
      if (value == null) value = DEFAULT_LENGTH;
      return Length.parseDouble(value, Unit.MILLIMETER);
    }
  }

  /**
   * Get the line style of a footnote separator line.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, the default value "NONE" will be
   * returned.
   *
   * @return the line style of a footnote separator line.
   */
  public String getFootnoteSepLineStyle() {
    if (mFootnoteSepElement == null) {
      return LineStyle.NONE.toString();
    } else {
      return mFootnoteSepElement.getStyleLineStyleAttribute();
    }
  }

  /**
   * Get the width of a footnote separator line.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, the default value 0 will be
   * returned.
   *
   * @return the width a footnote separator line.
   */
  public double getFootnoteSepWidth() {
    if (mFootnoteSepElement == null) {
      return 0;
    } else {
      String value = mFootnoteSepElement.getStyleRelWidthAttribute();
      if (value == null) value = DEFAULT_PERCENT;
      return Percent.valueOf(value).doubleValue();
    }
  }

  /**
   * Get the thickness of a footnote separator line.
   *
   * <p>If there is no <code>style:footnote-sep</code> element, the default value 0 will be
   * returned.
   *
   * @return the thickness a footnote separator line.
   */
  public double getFootnoteSepThickness() {
    if (mFootnoteSepElement == null) {
      return 0;
    } else {
      String value = mFootnoteSepElement.getStyleWidthAttribute();
      if (value == null) value = DEFAULT_LENGTH;
      return Length.parseDouble(value, Unit.MILLIMETER);
    }
  }

  /**
   * Set the formatting of footnote separator of this <code>PageLayoutProperties</code>
   *
   * <p>If the parameter <code>adjustment</code> is null, the adjustment definition will be set as
   * left. <br>
   * If the parameter <code>lineStyle</code> is null, the line style definition will be set as NONE.
   * <br>
   * If other parameters, like <code>color,
   * distanceAfterSep, distanceBeforeSep, width or thickness</code> is null, the corresponding
   * definition will be removed.
   *
   * @param adjustment - specifies how a footnote separator line is aligned on a page.
   * @param color - specifies the color of a column or footnote separator line
   * @param distanceAfterSep - specifies the space between a footnote separator line and the
   *     footnote text.
   * @param distanceBeforeSep - specifies the space between the body text area and a footnote
   *     separator line.
   * @param lineStyle - specifies the style of a footnote separator line.
   * @param width - specifies the length of the footnote separator line as a percentage of the body
   *     text area.
   * @param thickness - specifies the width or thickness of a line.
   */
  public void setFootnoteSepProperties(
      AdjustmentStyle adjustment,
      Color color,
      double distanceAfterSep,
      double distanceBeforeSep,
      StyleTypeDefinitions.LineStyle lineStyle,
      Percent width,
      double thickness) {
    if (mFootnoteSepElement == null) {
      mFootnoteSepElement = mElement.newStyleFootnoteSepElement();
    }

    // style:adjustment
    if (adjustment != null) {
      mFootnoteSepElement.setStyleAdjustmentAttribute(adjustment.toString());
    } else {
      mFootnoteSepElement.setStyleAdjustmentAttribute(AdjustmentStyle.LEFT.toString());
    }

    // style:color
    if (color != null) {
      mFootnoteSepElement.setStyleColorAttribute(color.toString());
    } else {
      mFootnoteSepElement.removeAttribute(StyleColorAttribute.ATTRIBUTE_NAME.getQName());
    }

    // style:distance-after-sep
    if (distanceAfterSep == 0) {
      mFootnoteSepElement.removeAttribute(StyleDistanceAfterSepAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mFootnoteSepElement.setStyleDistanceAfterSepAttribute(getInchValue(distanceAfterSep));
    }

    // style:distance-before-sep
    if (distanceBeforeSep == 0) {
      mFootnoteSepElement.removeAttribute(
          StyleDistanceBeforeSepAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mFootnoteSepElement.setStyleDistanceBeforeSepAttribute(getInchValue(distanceBeforeSep));
    }

    // style:line-style
    if (lineStyle == null) {
      mFootnoteSepElement.setStyleLineStyleAttribute(LineStyle.NONE.toString());
    } else {
      mFootnoteSepElement.setStyleLineStyleAttribute(lineStyle.toString());
    }

    // style:rel-width
    if (width == null) {
      mFootnoteSepElement.removeAttribute(StyleRelWidthAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mFootnoteSepElement.setStyleRelWidthAttribute(width.toString());
    }

    // style:width
    if (thickness == 0) {
      mFootnoteSepElement.removeAttribute(StyleWidthAttribute.ATTRIBUTE_NAME.getQName());
    } else {
      mFootnoteSepElement.setStyleWidthAttribute(getInchValue(thickness));
    }
  }

  /**
   * Set the writing mode of this <code>PageLayoutProperties</code>
   *
   * <p>If the parameter <code>mode</code> is null, the default value of writing node "page" will be
   * set.
   *
   * @param mode - specifies a writing mode.
   */
  public void setWritingMode(WritingMode mode) {
    if (mode == null) {
      mElement.setStyleWritingModeAttribute(WritingMode.PAGE.toString());
    } else {
      mElement.setStyleWritingModeAttribute(mode.toString());
    }
  }

  /**
   * Get the writing mode of this <code>PageLayoutProperties</code>
   *
   * @return the value of writing mode
   */
  public String getWritingMode() {
    return WritingMode.enumValueOf(mElement.getStyleWritingModeAttribute()).toString();
  }

  /**
   * Set the max height of a footnote area on a page.
   *
   * <p>If the value of this attribute is set to 0, there is no limit to the amount of space that
   * the footnote can occupy.
   *
   * @param height - the max height which a footnote area can occupy
   */
  public void setFootnoteMaxHeight(double height) {
    mElement.setStyleFootnoteMaxHeightAttribute(getInchValue(height));
  }

  /**
   * Get the max height of a footnote area on a page.
   *
   * @return the value of max height of footnote area.
   */
  public double getFootnoteMaxHeight() {
    String valueString = mElement.getStyleFootnoteMaxHeightAttribute();
    // check if a value was returned
    if (valueString == null) {
      // if not use the default length
      valueString = DEFAULT_LENGTH;
    }
    // return the converted value
    return Length.parseDouble(valueString, Unit.MILLIMETER);
  }

  /**
   * Return an instance of
   * <code>PageLayoutProperties</p> to represent the "style:page-layout-properties" in a style element.
   * <p>If there is no "style:page-layout-properties" defined in the style element, a new "style:page-layout-properties" element will be created.
   *
   * @param style
   *            - a style element
   * @return an instance of <code>PageLayoutProperties</p>
   */
  public static PageLayoutProperties getOrCreatePageLayoutProperties(OdfStyleBase style) {
    OdfStylePropertiesBase properties =
        style.getOrCreatePropertiesElement(OdfStylePropertiesSet.PageLayoutProperties);
    return new PageLayoutProperties((StylePageLayoutPropertiesElement) properties);
  }

  /**
   * Return an instance of
   * <code>PageLayoutProperties</p> to represent the "style:page-layout-properties" in a style element.
   * <p>If there is no "style:page-layout-properties" defined in the style element, null will be returned.
   *
   * @param style
   *            - a style element
   * @return an instance of <code>PageLayoutProperties</p>;Null if there is no
   *         "style:page-layout-properties" defined
   */
  public static PageLayoutProperties getPageLayoutProperties(OdfStyleBase style) {
    OdfStylePropertiesBase properties =
        style.getPropertiesElement(OdfStylePropertiesSet.PageLayoutProperties);
    if (properties != null)
      return new PageLayoutProperties((StylePageLayoutPropertiesElement) properties);
    else return null;
  }
}
