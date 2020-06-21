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

package org.odftoolkit.simple.draw;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.style.StyleTypeDefinitions;
import org.w3c.dom.DOMException;

/**
 * This class represents control object, a shape that is linked to a control inside an form. It
 * provides methods to get/set control properties, content, and styles.
 *
 * @since 0.8
 */
public class Control extends Component {
  private DrawControlElement mElement;
  private OdfElement containerElement;
  private ControlStyleHandler mStyleHandler;

  public Control(DrawControlElement element) {
    mElement = element;
    containerElement = (OdfElement) element.getParentNode();
  }

  /**
   * Get the container element which contains this control.
   *
   * @return the container element.
   */
  public OdfElement getContainerElement() {
    return containerElement;
  }

  /**
   * Get the instance of DrawControlElement which represents this control.
   *
   * @return the instance of DrawControlElement
   */
  public DrawControlElement getOdfElement() {
    return mElement;
  }

  /**
   * Create an instance of control and and append it at the end of a container element.
   *
   * @param container - the container element
   * @return a control instance
   */
  public static Control newDrawControl(ControlContainer container) {
    OdfElement parent = container.getDrawControlContainerElement();
    OdfFileDom ownerDom = (OdfFileDom) parent.getOwnerDocument();
    DrawControlElement element = ownerDom.newOdfElement(DrawControlElement.class);
    parent.appendChild(element);
    Control control = new Control(element);
    Component.registerComponent(control, element);
    return control;
  }

  /**
   * Get an instance of control according to a DrawControlElement.
   *
   * @param element - an instance of DrawControlElement
   * @return an instance of DrawControlElement
   */
  public static Control getInstanceof(DrawControlElement element) {
    Control control = null;
    control = (Control) Component.getComponentByElement(element);
    if (control != null) return control;

    control = new Control(element);
    Component.registerComponent(control, element);
    return control;
  }

  /**
   * Remove the shape control from the container.
   *
   * <p>The resource is removed if it's only used by this object.
   *
   * @return true if the shape control is successfully removed; false if otherwise.
   */
  public boolean remove() {
    try {
      Document mOwnerDocument = (Document) ((OdfFileDom) mElement.getOwnerDocument()).getDocument();
      mOwnerDocument.removeElementLinkedResource(getOdfElement());
      containerElement.removeChild(mElement);
      return true;
    } catch (DOMException exception) {
      Logger.getLogger(Control.class.getName()).log(Level.WARNING, exception.getMessage());
      return false;
    }
  }

  /**
   * Return the style handler for this control
   *
   * @return the style handler
   */
  public ControlStyleHandler getStyleHandler() {
    if (mStyleHandler == null) mStyleHandler = new ControlStyleHandler(this);
    return mStyleHandler;
  }

  /**
   * Set a control within a form that is linked to this control shape by its ID.
   *
   * @param formControlId - id of a form control
   */
  public void setControl(String formControlId) {
    mElement.setDrawControlAttribute(formControlId);
  }

  /**
   * Set how a form control is bound to a text document. Default position relative and alignment
   * will be set.
   *
   * <p>If the document is not text document, nothing will happen.
   *
   * @param achorType - the point at which a form control is bound to a text document
   */
  public void setAchorType(StyleTypeDefinitions.AnchorType achorType) {
    this.getStyleHandler().setAchorType(achorType);
  }

  /**
   * Set the rectangle used by this control
   *
   * @param rectangle - the rectangle used by this control
   */
  public void setRectangle(FrameRectangle rectangle) {
    if (rectangle.getWidth() > 0) mElement.setSvgWidthAttribute(rectangle.getWidthDesc());
    if (rectangle.getHeight() > 0) mElement.setSvgHeightAttribute(rectangle.getHeigthDesc());
    if (rectangle.getX() > 0) mElement.setSvgXAttribute(rectangle.getXDesc());
    if (rectangle.getY() > 0) mElement.setSvgYAttribute(rectangle.getYDesc());
  }

  /**
   * Return the rectangle used by this control
   *
   * @return - the rectangle used by this control
   */
  public FrameRectangle getRectangle() {
    try {
      FrameRectangle rectange =
          new FrameRectangle(
              mElement.getSvgXAttribute(),
              mElement.getSvgYAttribute(),
              mElement.getSvgWidthAttribute(),
              mElement.getSvgHeightAttribute());
      return rectange;
    } catch (Exception e) {
      Logger.getLogger(Frame.class.getName()).log(Level.FINE, e.getMessage(), e);
      return null;
    }
  }

  /**
   * Set the format string of the input control.
   *
   * <p>This function only works for date, time and percentage, otherwise an {@link
   * java.lang.IllegalArgumentException} will be thrown.
   *
   * <p>For value type percentage, the <code>formatStr</code> must follow the encoding rule of
   * {@link java.text.DecimalFormat <code>java.text.DecimalFormat</code>}. For value type date and
   * time, the <code>formatStr</code> must follow the encoding rule of {@link
   * java.text.SimpleDateFormat <code>java.text.SimpleDateFormat</code>}.
   *
   * @param formatStr -the input need be formatted as this specified format string.
   * @param type - the type that need to be set
   * @throws IllegalArgumentException if <code>formatStr</code> is null or the value type is
   *     supported.
   * @see java.text.SimpleDateFormat
   * @see java.text.DecimalFormat
   */
  public void setFormatString(String formatStr, String type) {
    OfficeValueTypeAttribute.Value typeValue = null;
    typeValue = OfficeValueTypeAttribute.Value.enumValueOf(type);
    if (typeValue == OfficeValueTypeAttribute.Value.DATE) {
      OdfNumberDateStyle dateStyle =
          new OdfNumberDateStyle(
              (OdfFileDom) mElement.getOwnerDocument(), formatStr, getUniqueDateStyleName(), null);
      dateStyle.setNumberLanguageAttribute("en");
      dateStyle.setNumberCountryAttribute("US");
      mElement.getAutomaticStyles().appendChild(dateStyle);
      setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
    } else if (typeValue == OfficeValueTypeAttribute.Value.TIME) {
      OdfNumberTimeStyle timeStyle =
          new OdfNumberTimeStyle(
              (OdfFileDom) mElement.getOwnerDocument(), formatStr, getUniqueDateStyleName());
      mElement.getAutomaticStyles().appendChild(timeStyle);
      setDataDisplayStyleName(timeStyle.getStyleNameAttribute());
    } else if (typeValue == OfficeValueTypeAttribute.Value.PERCENTAGE) {
      OdfNumberPercentageStyle dateStyle =
          new OdfNumberPercentageStyle(
              (OdfFileDom) mElement.getOwnerDocument(), formatStr, getUniquePercentageStyleName());
      mElement.getAutomaticStyles().appendChild(dateStyle);
      setDataDisplayStyleName(dateStyle.getStyleNameAttribute());
    } else {
      throw new IllegalArgumentException("This function doesn't support " + type + "formatting.");
    }
  }

  private String getUniqueDateStyleName() {
    String unique_name;
    OdfOfficeAutomaticStyles styles = mElement.getAutomaticStyles();
    do {
      unique_name = String.format("d%06x", (int) (Math.random() * 0xffffff));
    } while (styles.getDateStyle(unique_name) != null);
    return unique_name;
  }

  private void setDataDisplayStyleName(String name) {
    OdfStyleBase styleElement = getStyleHandler().getStyleElementForWrite();
    if (styleElement != null) {
      styleElement.setOdfAttributeValue(
          OdfName.newName(OdfDocumentNamespace.STYLE, "data-style-name"), name);
    }
  }

  private String getUniquePercentageStyleName() {
    String unique_name;
    OdfOfficeAutomaticStyles styles = mElement.getAutomaticStyles();
    do {
      unique_name = String.format("p%06x", (int) (Math.random() * 0xffffff));
    } while (styles.getDateStyle(unique_name) != null);
    return unique_name;
  }
}
