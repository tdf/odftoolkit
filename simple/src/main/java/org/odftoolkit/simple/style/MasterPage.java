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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.style.StylePageLayoutNameAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Color;
import org.odftoolkit.odfdom.type.Percent;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.style.StyleTypeDefinitions.AdjustmentStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.LineStyle;
import org.odftoolkit.simple.style.StyleTypeDefinitions.PrintOrientation;
import org.odftoolkit.simple.style.StyleTypeDefinitions.WritingMode;

/**
 * This class represents the master page style settings. It provides methods to access page layout
 * styles. More functions will be added latter.
 *
 * <p>This class is a corresponding high level class for element "style:master-page". It provides
 * methods to access the attributes and children of "style:master-page".
 *
 * @since 0.8
 */
public class MasterPage {

  /**
   * This class is the style handler for master page. It provides methods to get the readable
   * element and the writable element of a page layout style. It also provides method to get the
   * readable element and the writable element of page layout properties .
   */
  class StyleHandlerImpl {

    private OdfStylePageLayout mWritableStyleElement;
    private OdfStyleBase mStyleElement;
    private PageLayoutProperties mPageLayoutProperties;
    private PageLayoutProperties mWritablePageLayoutProperties;
    private OdfStylableElement mOdfElement; // master page style element
    private Document mDocument;

    /**
     * Constructor of StyleHandlerImpl
     *
     * @param element - the instance of master page element in an ODF document
     */
    public StyleHandlerImpl(OdfStylableElement element) {
      mOdfElement = element;
      mDocument = ((Document) ((OdfFileDom) mOdfElement.getOwnerDocument()).getDocument());
    }

    /**
     * Return the page layout properties definition for this component, only for read function.
     *
     * <p>Null will be returned if there is no style definition.
     *
     * <p>Null will be returned if there is no explicit page layout properties definition for this
     * component.
     *
     * <p>Note if you try to write style properties to the returned object, errors will be met with.
     *
     * @return the page layout properties definition for this component, only for read function
     */
    public PageLayoutProperties getPageLayoutPropertiesForRead() {
      if (mWritablePageLayoutProperties != null) return mWritablePageLayoutProperties;
      else if (mPageLayoutProperties != null) return mPageLayoutProperties;

      OdfStyleBase style = getPageLayoutElementForRead();
      if (style == null) {
        Logger.getLogger(DefaultStyleHandler.class.getName())
            .log(Level.FINE, "No style definition is found!", "");
        return null;
      }
      mPageLayoutProperties = PageLayoutProperties.getPageLayoutProperties(style);
      if (mPageLayoutProperties != null) return mPageLayoutProperties;
      else {
        Logger.getLogger(DefaultStyleHandler.class.getName())
            .log(Level.FINE, "No explicit pageLayout properties definition is found!", "");
        return null;
      }
    }

    /**
     * Return the page layout properties definition for this component, for read and write function.
     *
     * <p>An empty style definition will be created if there is no style definition.
     *
     * <p>An empty page layout properties definition will be created if there is no explicit page
     * layout properties definition.
     *
     * @return the page layout properties definition for this component, for read and write function
     * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
     *     for write.
     */
    public PageLayoutProperties getPageLayoutPropertiesForWrite() throws Exception {
      if (mWritablePageLayoutProperties != null) return mWritablePageLayoutProperties;
      OdfStylePageLayout style = getPageLayoutElementForWrite();
      mWritablePageLayoutProperties = PageLayoutProperties.getOrCreatePageLayoutProperties(style);
      return mWritablePageLayoutProperties;
    }

    /**
     * Return the style element for this component, only for read function. This method will invode
     * <code>getusedStyleName</code> to get the style name, and then find the readable style element
     * by name.
     *
     * <p>Null will be returned if there is no style definition.
     *
     * <p>Note if you try to write style properties to the returned object, errors will be met with.
     *
     * @return the style element
     * @see #getUsedPageLayoutStyleName()
     */
    public OdfStyleBase getPageLayoutElementForRead() {
      // Return current used style
      if (getCurrentUsedStyle() != null) return getCurrentUsedStyle();

      String styleName = getUsedPageLayoutStyleName();
      mStyleElement = getReadableStyleElementByName(styleName);
      return mStyleElement;
    }

    /**
     * Return the style element for this component, for read and write functions. This method will
     * invode <code>getusedStyleName</code> to get the style name, and then find the writable style
     * element by name.
     *
     * <p>An empty style definition will be created if there is no style definition.
     *
     * @return the style element
     * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
     *     for write.
     * @see #getUsedPageLayoutStyleName()
     */
    public OdfStylePageLayout getPageLayoutElementForWrite() throws Exception {
      if (mWritableStyleElement != null) return mWritableStyleElement;

      String styleName = getUsedPageLayoutStyleName();
      mWritableStyleElement = getWritableStyleElementByName(styleName, false);
      return mWritableStyleElement;
    }

    /**
     * Return a readable style element by style name.
     *
     * <p>If the style name is null, the default style will be returned.
     *
     * @param styleName - the style name
     * @return a readable style element
     */
    protected OdfStyleBase getReadableStyleElementByName(String styleName) {

      // TODO: get from default page layout style element
      if (styleName == null || (styleName.equals(""))) ;

      OdfStylePageLayout styleElement = mOdfElement.getAutomaticStyles().getPageLayout(styleName);

      return styleElement;
    }

    /**
     * Return a writable page layout style element by style name.
     *
     * <p>If the style is shared, a copied style element would be returned.
     *
     * <p>If the style name is null, the default style will be copied.
     *
     * @param styleName - the style name
     * @return a writable style element
     * @throws Exception if the corresponding StyleElement cannot be accessed for write.
     */
    protected OdfStylePageLayout getWritableStyleElementByName(String styleName, boolean isShared)
        throws Exception {
      boolean createNew = isShared;
      OdfStylePageLayout pageLayout = null;
      OdfOfficeAutomaticStyles styles = mOdfElement.getAutomaticStyles();
      if (styleName == null || (styleName.equals(""))) {
        createNew = true;
        // TODO: get default page layout style
      } else {
        styles = mOdfElement.getAutomaticStyles();
        pageLayout = styles.getPageLayout(styleName);
        if (pageLayout == null || pageLayout.getStyleUserCount() > 1) {
          createNew = true;
        }
      }
      // if style name is null or this style are used by many users,
      // should create a new one.
      if (createNew) {
        OdfStylePageLayout newPageLayout = null;
        if (pageLayout != null) {
          newPageLayout = (OdfStylePageLayout) pageLayout.cloneNode(true);
        }
        newPageLayout =
            (OdfStylePageLayout)
                mDocument.getStylesDom().newOdfElement(StylePageLayoutElement.class);
        String newname = newUniquePageLayoutName();
        newPageLayout.setStyleNameAttribute(newname);
        styles.appendChild(newPageLayout);
        mOdfElement.setAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "style:page-layout-name", newname);
        newPageLayout.addStyleUser(mOdfElement);
        return newPageLayout;
      }
      return pageLayout;
    }

    private String newUniquePageLayoutName() {
      String unique_name;
      OdfOfficeAutomaticStyles styles = mOdfElement.getAutomaticStyles();
      do {
        unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
      } while (styles.getPageLayout(unique_name) != null);
      return unique_name;
    }

    private OdfStyleBase getCurrentUsedStyle() {
      if (mWritableStyleElement != null) return mWritableStyleElement;
      else return mStyleElement;
    }

    /**
     * Return the referenced style name by this master page.
     *
     * @return - the referenced style name by this master page.
     */
    public String getUsedPageLayoutStyleName() {
      return mOdfElement.getAttribute(StylePageLayoutNameAttribute.ATTRIBUTE_NAME.getQName());
    }
  }

  private StyleMasterPageElement mStyleMasterPageElement;
  private StyleHandlerImpl mStyleHandler;

  /**
   * Constructor of MasterPage
   *
   * @param styleMasterPageElement - the instance of style:master-page element in an ODF document
   */
  MasterPage(StyleMasterPageElement styleMasterPageElement) {
    mStyleMasterPageElement = styleMasterPageElement;
  }

  /**
   * Return a master page according to its name and its document.
   *
   * <p>If there is no existing master page defined by this name in the document, a new master with
   * this name will be created and returned.
   *
   * @param doc - the document to which the master page belongs.
   * @param name - the name of the master page
   * @return a master page
   * @throws Exception if the style DOM cannot be initialized
   */
  public static MasterPage getOrCreateMasterPage(Document doc, String name) throws Exception {
    OdfOfficeMasterStyles officeMasterStyles = doc.getOfficeMasterStyles();
    StyleMasterPageElement master = officeMasterStyles.getMasterPage(name);
    if (master == null) {
      master = doc.getStylesDom().newOdfElement(StyleMasterPageElement.class);
      master.setStyleNameAttribute(name);
      officeMasterStyles.appendChild(master);
    }
    return new MasterPage(master);
  }

  /**
   * Get the style handler of this master page.
   *
   * <p>The style handler is an instance of StyleHandlerImpl
   *
   * @return an instance of StyleHandlerImpl
   * @see StyleHandlerImpl
   */
  StyleHandlerImpl getPageLayoutStyleHandler() {
    if (mStyleHandler != null) return mStyleHandler;
    else {
      mStyleHandler = new StyleHandlerImpl(mStyleMasterPageElement);
      return mStyleHandler;
    }
  }

  /**
   * Get the name of this master page.
   *
   * <p>This represents the attribute <code>style:name</code> of <code>style:master-page</code>.
   *
   * @return the style name of this master page.
   */
  public String getName() {
    return mStyleMasterPageElement.getStyleNameAttribute();
  }

  /**
   * Get the top margin of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @return the top margin (in Millimeter)
   */
  public double getMarginTop() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getMarginTop();
  }

  /**
   * Get the right margin of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @return the right margin (in Millimeter)
   */
  public double getMarginRight() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getMarginRight();
  }

  /**
   * Get the left margin of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @return the left margin (in Millimeter)
   */
  public double getMarginLeft() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getMarginLeft();
  }

  /**
   * Get the bottom margin of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @return the bottom margin (in Millimeter)
   */
  public double getMarginBottom() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getMarginBottom();
  }

  /**
   * Set the margins of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * <p>If the margin size is set to zero, the corresponding margin definition will be removed.
   *
   * @param marginTop the size of the top margin (in Millimeter)
   * @param marginBottom the size of the bottom margin (in Millimeter)
   * @param marginLeft the size of the left margin (in Millimeter)
   * @param marginRight the size of the right margin (in Millimeter)
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setMargins(
      double marginTop, double marginBottom, double marginLeft, double marginRight)
      throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setMarginTop(marginTop);
    properties.setMarginBottom(marginBottom);
    properties.setMarginLeft(marginLeft);
    properties.setMarginRight(marginRight);
  }

  /**
   * Get the page width of the the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @return the size of page width (in Millimeter)
   */
  public double getPageWidth() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getPageWidth();
  }

  /**
   * Set the page width of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * <p>If the size is set to zero, the page width definition will be removed.
   *
   * @param pageWidth the size of the page width (in Millimeter)
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setPageWidth(double pageWidth) throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setPageWidth(pageWidth);
  }

  /**
   * Get the page height of the the <code>PageLayoutProperties</code> referenced by this master
   * page.
   *
   * @return the size of page height (in Millimeter)
   */
  public double getPageHeight() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getPageHeight();
  }

  /**
   * Set the page height of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * <p>If the size is set to zero, the page height definition will be removed.
   *
   * @param pageHeight the size of the page height (in Millimeter)
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setPageHeight(double pageHeight) throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setPageHeight(pageHeight);
  }

  /**
   * Get the number format of the the <code>PageLayoutProperties</code> referenced by this master
   * page.
   *
   * @return the value of number format
   */
  public String getNumberFormat() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getNumberFormat();
  }

  /**
   * Set the number format of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @param format specify the number format. The values of the style:num-format attribute are 1, i,
   *     I, string, an empty string, a or A.
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setNumberFormat(String format) throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setNumberFormat(format);
  }

  /**
   * Get the print orientation of the the <code>PageLayoutProperties</code> referenced by this
   * master page.
   *
   * @return the value of print orientation
   */
  public String getPrintOrientation() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getPrintOrientation();
  }

  /**
   * Set the print orientation of the <code>PageLayoutProperties</code> referenced by this master
   * page.
   *
   * <p>If the parameter <code>orientation</code> is null, the print orientation definition will be
   * removed.
   *
   * @param orientation - the print orientation
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setPrintOrientation(PrintOrientation orientation) throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setPrintOrientation(orientation);
  }

  /**
   * Get the writing mode of the the <code>PageLayoutProperties</code> referenced by this master
   * page.
   *
   * @return the value of writing mode
   */
  public String getWritingMode() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getWritingMode();
  }

  /**
   * Set the writing mode of the <code>PageLayoutProperties</code> referenced by this master page.
   *
   * @param mode - writing mode
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setWritingMode(WritingMode mode) throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setWritingMode(mode);
  }

  /**
   * Get the footnote max height of the the <code>PageLayoutProperties</code> referenced by this
   * master page.
   *
   * @return the value of footnote max height
   */
  public double getFootnoteMaxHeight() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteMaxHeight();
  }

  /**
   * Set the footnote max height of the <code>PageLayoutProperties</code> referenced by this master
   * page.
   *
   * @param height - the max height of a footnote area
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   */
  public void setFootnoteMaxHeight(double height) throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setFootnoteMaxHeight(height);
  }

  /**
   * Get the way in which a footnote separator line aligned to a page, which is set by the <code>
   * PageLayoutProperties</code> referenced by this master page.
   *
   * @return the adjustment of a footnote separator line
   */
  public String getFootnoteSepAdjustment() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepAdjustment();
  }

  /**
   * Get the color of a footnote separator which is set by the <code>PageLayoutProperties</code>
   * referenced by this master page.
   *
   * @return the color of a footnote separator line
   */
  public String getFootnoteSepColor() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepColor();
  }

  /**
   * Get the distance between a footnote separator and the footnote area, which is set by the <code>
   * PageLayoutProperties</code> referenced by this master page.
   *
   * @return the distance after a footnote separator line
   */
  public double getFootnoteSepDistanceAfterSep() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepDistanceAfterSep();
  }

  /**
   * Get the distance between the text area and the footnote separator, which is set by the <code>
   * PageLayoutProperties</code> referenced by this master page.
   *
   * @return the distance before a footnote separator line
   */
  public double getFootnoteSepDistanceBeforeSep() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepDistanceBeforeSep();
  }

  /**
   * Get the line style of a footnote separator which is set by the <code>PageLayoutProperties
   * </code> referenced by this master page.
   *
   * @return the line style of a footnote separator line
   */
  public String getFootnoteSepLineStyle() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepLineStyle();
  }

  /**
   * Get the real width of a footnote separator which is set by the <code>PageLayoutProperties
   * </code> referenced by this master page.
   *
   * @return the real width of a footnote separator line
   */
  public double getFootnoteSepWidth() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepWidth();
  }

  /**
   * Get the thickness of a footnote separator which is set by the <code>PageLayoutProperties</code>
   * referenced by this master page.
   *
   * @return the thickness of a footnote separator line
   */
  public double getFootnoteSepThickness() {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForRead();
    return properties.getFootnoteSepThickness();
  }

  /**
   * Set the foot note separator properties of the <code>PageLayoutProperties</code> referenced by
   * this master page.
   *
   * @throws Exception if the corresponding <code>StylePageLayoutElement</code> cannot be accessed
   *     for write.
   * @see PageLayoutProperties#setFootnoteSepProperties(AdjustmentStyle, Color, double, double,
   *     LineStyle, Percent, double)
   */
  public void setFootnoteSepProperties(
      AdjustmentStyle adjustment,
      Color color,
      double distanceAfterSep,
      double distanceBeforeSep,
      LineStyle lineStyle,
      Percent width,
      double thickness)
      throws Exception {
    PageLayoutProperties properties = getPageLayoutStyleHandler().getPageLayoutPropertiesForWrite();
    properties.setFootnoteSepProperties(
        adjustment, color, distanceAfterSep, distanceBeforeSep, lineStyle, width, thickness);
  }
}
