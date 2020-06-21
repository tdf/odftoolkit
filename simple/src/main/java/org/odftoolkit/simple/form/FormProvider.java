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

import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;

/**
 * FormProvider provides the methods to create and get form instance in a way that is
 * implementation-independent.
 *
 * @since 0.8
 */
public interface FormProvider {

  /**
   * Create a form in a way that is implementation-independent.
   *
   * @param name -the form name
   * @param parent -the container element of this form
   * @return a form instance
   */
  public Form createForm(String name, OfficeFormsElement parent);

  /**
   * Get a form instance by a <code>FormFormElement</code>
   *
   * @param element - a <code>FormFormElement</code>
   * @return a form instance
   */
  public Form getInstanceOf(FormFormElement element);
}
