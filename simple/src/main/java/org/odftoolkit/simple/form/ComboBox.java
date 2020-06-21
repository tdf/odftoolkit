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

package org.odftoolkit.simple.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.element.form.FormComboboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormItemElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.form.FormTypeDefinition.FormListSourceType;
import org.w3c.dom.NodeList;

/**
 * This class represents the form control of Combo Box, provides methods to get/set the form
 * properties and the style formatting of this control.
 *
 * @since 0.8
 */
public class ComboBox extends FormControl {

  private ArrayList<String> entries;

  ComboBox(FormComboboxElement element) {
    this.mElement = element;
    formElement = (FormFormElement) element.getParentNode();
  }

  /**
   * Get an instance of combo box by an instance of FormComboboxElement, while searching the
   * document content to make a bind with the DrawControl which already reference to this check box.
   *
   * @param element - an instance of FormComboboxElement
   * @return an instance of combo box
   */
  public static ComboBox getInstanceOf(FormComboboxElement element) {
    ComboBox combo = new ComboBox(element);
    try {
      combo.loadDrawControl(
          ((Document) ((OdfFileDom) element.getOwnerDocument()).getDocument()).getContentRoot());
    } catch (Exception e) {
      Logger.getLogger(ComboBox.class.getName())
          .log(Level.WARNING, "Cannot load the drawing shape of this combo box.");
    }
    return combo;
  }

  @Override
  FormPropertiesElement getFormPropertiesElementForWrite() {
    if (mFormProperties == null)
      mFormProperties = ((FormComboboxElement) mElement).newFormPropertiesElement();
    return mFormProperties;
  }

  @Override
  public void setControlImplementation(String controlImpl) {
    ((FormComboboxElement) mElement).setFormControlImplementationAttribute(controlImpl);
  }

  @Override
  public String getId() {
    return ((FormComboboxElement) mElement).getFormIdAttribute();
  }

  @Override
  public void setId(String id) {
    ((FormComboboxElement) mElement).setFormIdAttribute(id);
  }

  @Override
  public String getName() {
    return ((FormComboboxElement) mElement).getFormNameAttribute();
  }

  @Override
  public void setName(String name) {
    ((FormComboboxElement) mElement).setFormNameAttribute(name);
  }

  /**
   * Set the visibility of the drop-down list
   *
   * @param isDropDown - specify if the drop-down list is visible
   */
  public void setFormDropdown(boolean isDropDown) {
    ((FormComboboxElement) mElement).setFormDropdownAttribute(isDropDown);
  }

  /**
   * Get the visibility of the drop-down list
   *
   * @return true means the drop-down list is visible; false means invisible
   */
  public boolean getFormDropdown() {
    return ((FormComboboxElement) mElement).getFormDropdownAttribute();
  }

  /**
   * Add a list item to this combo box.
   *
   * @param item - a list item
   */
  public void addItem(String item) {
    if (item == null) return;
    if (entries == null) entries = new ArrayList<String>();
    ((FormComboboxElement) mElement).newFormItemElement().setFormLabelAttribute(item);
    entries.add(item);
  }

  /**
   * Add a group of list items to this combo box
   *
   * @param items -a group of list items
   */
  public void addItems(String[] items) {
    if (items == null || items.length == 0) return;
    for (int i = 0; i < items.length; i++) {
      addItem(items[i]);
    }
  }

  /**
   * Get the list entries if they are initiated through a list of string.
   *
   * @return the list entries
   */
  public ArrayList<String> getEntries() {
    if (entries == null || entries.size() == 0) {
      NodeList items = mElement.getElementsByTagName("form:item");
      if (items != null && items.getLength() > 0) {
        for (int i = 0; i < items.getLength(); i++) {
          if (entries == null) entries = new ArrayList<String>();
          entries.add(((FormItemElement) items.item(i)).getFormLabelAttribute());
        }
        return entries;
      }
    }
    return null;
  }

  /**
   * Set the source type of the data list.
   *
   * @param type - the source type of this list
   */
  public void setListSourceType(FormListSourceType type) {
    ((FormComboboxElement) mElement).setFormListSourceTypeAttribute(type.toString());
  }

  /**
   * Get the source type of the data list.
   *
   * @return the source type of this list
   */
  public FormListSourceType getListSourceType() {
    String value = ((FormComboboxElement) mElement).getFormListSourceTypeAttribute();
    return FormListSourceType.enumValueOf(value);
  }

  /**
   * Set the source of this data list.
   *
   * @param listSource - the source of this data list.
   */
  public void setListSource(String listSource) {
    ((FormComboboxElement) mElement).setFormListSourceAttribute(listSource);
  }

  /**
   * Get the source of this data list.
   *
   * @return the source of this data list.
   */
  public String getListSource() {
    return ((FormComboboxElement) mElement).getFormListSourceAttribute();
  }

  /**
   * Set the data field referenced by this combo box
   *
   * @param dataField - the data field referenced by this combo box
   */
  public void setDataField(String dataField) {
    ((FormComboboxElement) mElement).setFormDataFieldAttribute(dataField);
  }

  /**
   * Get the data field referenced by this combo box
   *
   * @return the data field referenced by this combo box
   */
  public String getDataField() {
    return ((FormComboboxElement) mElement).getFormDataFieldAttribute();
  }

  /**
   * Set the default value of this control, it will be override by current value.
   *
   * @param value - default value
   */
  public void setValue(String defaultValue) {
    ((FormComboboxElement) mElement).setFormValueAttribute(defaultValue);
  }

  /**
   * Get the default value of this control.
   *
   * @return default value
   */
  public String getValue() {
    return ((FormComboboxElement) mElement).getFormValueAttribute();
  }

  /**
   * Set the current value of this control, it override the default value.
   *
   * @param currentValue - current value
   */
  public void setCurrentValue(String currentValue) {
    ((FormComboboxElement) mElement).setFormCurrentValueAttribute(currentValue);
  }

  /**
   * Get the current value of this control, it override the default value.
   *
   * @return current value
   */
  public String getCurrentValue() {
    return ((FormComboboxElement) mElement).getFormCurrentValueAttribute();
  }

  /**
   * Get a simple iterator for combo boxes.
   *
   * @param container - an instance of form where to traverse the combo boxes s
   */
  public static Iterator<FormControl> getSimpleIterator(Form container) {
    return new SimpleComboBoxIterator(container);
  }

  private static class SimpleComboBoxIterator implements Iterator<FormControl> {

    private FormFormElement containerElement;
    private ComboBox nextElement = null;
    private ComboBox tempElement = null;

    private SimpleComboBoxIterator(Form container) {
      containerElement = container.getOdfElement();
    }

    public boolean hasNext() {
      tempElement = findNext(nextElement);
      return (tempElement != null);
    }

    public ComboBox next() {
      if (tempElement != null) {
        nextElement = tempElement;
        tempElement = null;
      } else {
        nextElement = findNext(nextElement);
      }
      if (nextElement == null) {
        return null;
      } else {
        return nextElement;
      }
    }

    public void remove() {
      if (nextElement == null) {
        throw new IllegalStateException("please call next() first.");
      }
      nextElement.remove();
    }

    private ComboBox findNext(ComboBox thisComboBox) {
      FormComboboxElement nextComboBox = null;
      if (thisComboBox == null) {
        nextComboBox = OdfElement.findFirstChildNode(FormComboboxElement.class, containerElement);
      } else {
        nextComboBox =
            OdfElement.findNextChildNode(FormComboboxElement.class, thisComboBox.getOdfElement());
      }

      if (nextComboBox != null) {
        return ComboBox.getInstanceOf(nextComboBox);
      }
      return null;
    }
  }
}
