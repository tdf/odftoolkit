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

import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;

/**
 * FormContainer is a container which maintains Form(s) as element(s).
 * 
 * @since 0.8
 */
public interface FormContainer {

	/**
	 * Get the ODF element <code>OfficeFormsElement</code> which can have
	 * <code>FormFormElement</code> as child element directly according to ODF
	 * specification. This element will help to find the position to insert a
	 * new <code>FormFormElement</code> element.
	 * 
	 * @return the element of office:forms
	 */
	public OfficeFormsElement getFormContainerElement();

	/**
	 * create and add a form into this container.
	 * 
	 * @param name
	 *            - form name, represents attribute <code>form:name</code>
	 * @return an instance of Form
	 */
	public Form createForm(String name);

	/**
	 * Remove the form and its binding shape.
	 * 
	 * @param form
	 *            - the form to be removed
	 * @return true if the form is removed successfully, false if errors happen.
	 */
	public boolean removeForm(Form form);

	/**
	 * Return a form whose name is a given value.
	 * 
	 * @param name
	 *            - the name of the form
	 * @return a form whose name is a given value
	 */
	public Form getFormByName(String name);

	/**
	 * Get an iterator to traverse each form in this container.
	 * 
	 * @return form iterator
	 */
	public Iterator<Form> getFormIterator();

	/**
	 * Get the attribute <code>form:apply-design-mode</code> which specifies
	 * whether forms are presented in editable or non-editable state.
	 * 
	 * @return true if forms in document are presented in editable state;false
	 *         if forms in document are presented in completed state
	 */
	public boolean getApplyDesignMode();

	/**
	 * Get the attribute <code>form:automatic-focus</code> which specifies
	 * whether the consumer loading the document should set the focus to a form
	 * control.
	 * 
	 * @return true if sets the focus to a form control after loading the
	 *         document; false if else.
	 */
	public boolean getAutomaticFocus();

	/**
	 * Set the attribute <code>form:apply-design-mode</code> which specifies
	 * whether forms are presented in editable or non-editable state.
	 * 
	 * @param isDesignMode
	 *            - true if forms in document are presented in editable state;
	 *            false if forms in document are presented in completed state
	 */
	public void setApplyDesignMode(boolean isDesignMode);

	/**
	 * Set the attribute <code>form:automatic-focus</code> which specifies
	 * whether the consumer loading the document should set the focus to a form
	 * control.
	 * 
	 * @param isAutoFocus
	 *            -true if sets the focus to a form control after loading the
	 *            document; false if else.
	 */
	public void setAutomaticFocus(boolean isAutoFocus);

}
