/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.style;

import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBackgroundColorAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBreakAfterAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoBreakBeforeAttribute;
import org.odftoolkit.odfdom.dom.attribute.fo.FoKeepTogetherAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleMinRowHeightAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleRowHeightAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleUseOptimalRowHeightAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;

/** DOM implementation of OpenDocument element {@odf.element style:table-row-properties}. */
public class StyleTableRowPropertiesElement extends OdfStylePropertiesBase {

  public static final OdfName ELEMENT_NAME =
      OdfName.newName(OdfDocumentNamespace.STYLE, "table-row-properties");

  /**
   * Create the instance of <code>StyleTableRowPropertiesElement</code>
   *
   * @param ownerDoc The type is <code>OdfFileDom</code>
   */
  public StyleTableRowPropertiesElement(OdfFileDom ownerDoc) {
    super(ownerDoc, ELEMENT_NAME);
  }

  /**
   * Get the element name
   *
   * @return return <code>OdfName</code> the name of element {@odf.element
   *     style:table-row-properties}.
   */
  public OdfName getOdfName() {
    return ELEMENT_NAME;
  }

  public static final OdfStyleProperty BackgroundColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "background-color"));

  public static final OdfStyleProperty BreakAfter =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "break-after"));

  public static final OdfStyleProperty BreakBefore =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "break-before"));

  public static final OdfStyleProperty KeepTogether =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "keep-together"));

  public static final OdfStyleProperty MinRowHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "min-row-height"));

  public static final OdfStyleProperty RowHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "row-height"));

  public static final OdfStyleProperty UseOptimalRowHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableRowProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "use-optimal-row-height"));

  /**
   * Receives the value of the ODFDOM attribute representation <code>FoBackgroundColorAttribute
   * </code> , See {@odf.attribute fo:background-color}
   *
   * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set
   *     and no default value defined.
   */
  public String getFoBackgroundColorAttribute() {
    FoBackgroundColorAttribute attr =
        (FoBackgroundColorAttribute) getOdfAttribute(OdfDocumentNamespace.FO, "background-color");
    if (attr != null) {
      return String.valueOf(attr.getValue());
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>FoBackgroundColorAttribute</code> , See
   * {@odf.attribute fo:background-color}
   *
   * @param foBackgroundColorValue The type is <code>String</code>
   */
  public void setFoBackgroundColorAttribute(String foBackgroundColorValue) {
    FoBackgroundColorAttribute attr =
        new FoBackgroundColorAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setValue(foBackgroundColorValue);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>FoBreakAfterAttribute</code> ,
   * See {@odf.attribute fo:break-after}
   *
   * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set
   *     and no default value defined.
   */
  public String getFoBreakAfterAttribute() {
    FoBreakAfterAttribute attr =
        (FoBreakAfterAttribute) getOdfAttribute(OdfDocumentNamespace.FO, "break-after");
    if (attr != null) {
      return String.valueOf(attr.getValue());
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>FoBreakAfterAttribute</code> , See
   * {@odf.attribute fo:break-after}
   *
   * @param foBreakAfterValue The type is <code>String</code>
   */
  public void setFoBreakAfterAttribute(String foBreakAfterValue) {
    FoBreakAfterAttribute attr = new FoBreakAfterAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setValue(foBreakAfterValue);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>FoBreakBeforeAttribute</code> ,
   * See {@odf.attribute fo:break-before}
   *
   * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set
   *     and no default value defined.
   */
  public String getFoBreakBeforeAttribute() {
    FoBreakBeforeAttribute attr =
        (FoBreakBeforeAttribute) getOdfAttribute(OdfDocumentNamespace.FO, "break-before");
    if (attr != null) {
      return String.valueOf(attr.getValue());
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>FoBreakBeforeAttribute</code> , See
   * {@odf.attribute fo:break-before}
   *
   * @param foBreakBeforeValue The type is <code>String</code>
   */
  public void setFoBreakBeforeAttribute(String foBreakBeforeValue) {
    FoBreakBeforeAttribute attr = new FoBreakBeforeAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setValue(foBreakBeforeValue);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>FoKeepTogetherAttribute</code>
   * , See {@odf.attribute fo:keep-together}
   *
   * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set
   *     and no default value defined.
   */
  public String getFoKeepTogetherAttribute() {
    FoKeepTogetherAttribute attr =
        (FoKeepTogetherAttribute) getOdfAttribute(OdfDocumentNamespace.FO, "keep-together");
    if (attr != null) {
      return String.valueOf(attr.getValue());
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>FoKeepTogetherAttribute</code> , See
   * {@odf.attribute fo:keep-together}
   *
   * @param foKeepTogetherValue The type is <code>String</code>
   */
  public void setFoKeepTogetherAttribute(String foKeepTogetherValue) {
    FoKeepTogetherAttribute attr = new FoKeepTogetherAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setValue(foKeepTogetherValue);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>StyleMinRowHeightAttribute
   * </code> , See {@odf.attribute style:min-row-height}
   *
   * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set
   *     and no default value defined.
   */
  public String getStyleMinRowHeightAttribute() {
    StyleMinRowHeightAttribute attr =
        (StyleMinRowHeightAttribute) getOdfAttribute(OdfDocumentNamespace.STYLE, "min-row-height");
    if (attr != null) {
      return String.valueOf(attr.getValue());
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>StyleMinRowHeightAttribute</code> , See
   * {@odf.attribute style:min-row-height}
   *
   * @param styleMinRowHeightValue The type is <code>String</code>
   */
  public void setStyleMinRowHeightAttribute(String styleMinRowHeightValue) {
    StyleMinRowHeightAttribute attr =
        new StyleMinRowHeightAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setValue(styleMinRowHeightValue);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>StyleRowHeightAttribute</code>
   * , See {@odf.attribute style:row-height}
   *
   * @return - the <code>String</code> , the value or <code>null</code>, if the attribute is not set
   *     and no default value defined.
   */
  public String getStyleRowHeightAttribute() {
    StyleRowHeightAttribute attr =
        (StyleRowHeightAttribute) getOdfAttribute(OdfDocumentNamespace.STYLE, "row-height");
    if (attr != null) {
      return String.valueOf(attr.getValue());
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>StyleRowHeightAttribute</code> , See
   * {@odf.attribute style:row-height}
   *
   * @param styleRowHeightValue The type is <code>String</code>
   */
  public void setStyleRowHeightAttribute(String styleRowHeightValue) {
    StyleRowHeightAttribute attr = new StyleRowHeightAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setValue(styleRowHeightValue);
  }

  /**
   * Receives the value of the ODFDOM attribute representation <code>
   * StyleUseOptimalRowHeightAttribute</code> , See {@odf.attribute style:use-optimal-row-height}
   *
   * @return - the <code>Boolean</code> , the value or <code>null</code>, if the attribute is not
   *     set and no default value defined.
   */
  public Boolean getStyleUseOptimalRowHeightAttribute() {
    StyleUseOptimalRowHeightAttribute attr =
        (StyleUseOptimalRowHeightAttribute)
            getOdfAttribute(OdfDocumentNamespace.STYLE, "use-optimal-row-height");
    if (attr != null && !attr.getValue().isEmpty()) {
      return attr.booleanValue();
    }
    return null;
  }

  /**
   * Sets the value of ODFDOM attribute representation <code>StyleUseOptimalRowHeightAttribute
   * </code> , See {@odf.attribute style:use-optimal-row-height}
   *
   * @param styleUseOptimalRowHeightValue The type is <code>Boolean</code>
   */
  public void setStyleUseOptimalRowHeightAttribute(Boolean styleUseOptimalRowHeightValue) {
    StyleUseOptimalRowHeightAttribute attr =
        new StyleUseOptimalRowHeightAttribute((OdfFileDom) this.ownerDocument);
    setOdfAttribute(attr);
    attr.setBooleanValue(styleUseOptimalRowHeightValue);
  }

  /**
   * Create child element {@odf.element style:background-image}.
   *
   * <p>Child element was added in ODF 1.2
   *
   * @return the element {@odf.element style:background-image}
   */
  public StyleBackgroundImageElement newStyleBackgroundImageElement() {
    StyleBackgroundImageElement styleBackgroundImage =
        ((OdfFileDom) this.ownerDocument).newOdfElement(StyleBackgroundImageElement.class);
    this.appendChild(styleBackgroundImage);
    return styleBackgroundImage;
  }

  /**
   * Accept an visitor instance to allow the visitor to do some operations. Refer to visitor design
   * pattern to get a better understanding.
   *
   * @param visitor an instance of DefaultElementVisitor
   */
  @Override
  public void accept(ElementVisitor visitor) {
    if (visitor instanceof DefaultElementVisitor) {
      DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
      defaultVisitor.visit(this);
    } else {
      visitor.visit(this);
    }
  }
}
