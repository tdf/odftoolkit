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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.DOMException;

/**
 * AbstractFormContainer is an abstract implementation of the FormContainer interface, with a
 * default implementation for every method defined in FormContainer, except
 * getFormContainerElement(). A subclass must implement the abstract method
 * getFormContainerElement().
 *
 * @since 0.8
 */
public abstract class AbstractFormContainer implements FormContainer {

  private FormProvider provider = null;

  public abstract OfficeFormsElement getFormContainerElement();

  public Form createForm(String name) {
    return getProvider().createForm(name, getFormContainerElement());
  }

  public boolean removeForm(Form form) {
    if (form == null) return true;
    OdfElement containerElement = getFormContainerElement();
    FormFormElement formElement = form.getOdfElement();
    try {
      containerElement.removeChild(formElement);
    } catch (DOMException exception) {
      Logger.getLogger(AbstractFormContainer.class.getName())
          .log(Level.WARNING, exception.getMessage());
      return false;
    }
    return true;
  }

  public Form getFormByName(String name) {
    if (name == null) return null;

    OdfElement container = getFormContainerElement();
    FormFormElement element = OdfElement.findFirstChildNode(FormFormElement.class, container);
    while (element != null) {
      if (name.equals(element.getFormNameAttribute())) {
        Form form = getProvider().getInstanceOf(element);
        return form;
      }
      element = OdfElement.findNextChildNode(FormFormElement.class, element);
    }
    return null;
  }

  public Iterator<Form> getFormIterator() {
    return new SimpleFormIterator(this);
  }

  private class SimpleFormIterator implements Iterator<Form> {

    private OdfElement containerElement;
    private Form nextElement = null;
    private Form tempElement = null;

    private SimpleFormIterator(FormContainer container) {
      containerElement = container.getFormContainerElement();
    }

    public boolean hasNext() {
      tempElement = findNext(nextElement);
      return (tempElement != null);
    }

    public Form next() {
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
      containerElement.removeChild(nextElement.getOdfElement());
    }

    private Form findNext(Form thisForm) {
      FormFormElement nextForm = null;
      if (thisForm == null) {
        nextForm = OdfElement.findFirstChildNode(FormFormElement.class, containerElement);
      } else {
        nextForm = OdfElement.findNextChildNode(FormFormElement.class, thisForm.getOdfElement());
      }

      if (nextForm != null) {
        return getProvider().getInstanceOf(nextForm);
      }
      return null;
    }
  }

  public boolean getApplyDesignMode() {
    return getFormContainerElement().getFormApplyDesignModeAttribute();
  }

  public boolean getAutomaticFocus() {
    return getFormContainerElement().getFormAutomaticFocusAttribute();
  }

  public void setApplyDesignMode(boolean isDesignMode) {
    getFormContainerElement().setFormApplyDesignModeAttribute(isDesignMode);
  }

  public void setAutomaticFocus(boolean isAutoFocus) {
    getFormContainerElement().setFormAutomaticFocusAttribute(isAutoFocus);
  }

  /**
   * Set the form provider of this container.
   *
   * @param provider - which is used to instantiate a form
   */
  public void setProvider(FormProvider provider) {
    this.provider = provider;
  }

  /**
   * Get the form provider of this container.
   *
   * @return provider - a default provider followed by OO definition will be created if users don't
   *     appointed one.
   */
  public FormProvider getProvider() {
    if (provider == null) provider = new OOFormProvider();
    return provider;
  }
}
