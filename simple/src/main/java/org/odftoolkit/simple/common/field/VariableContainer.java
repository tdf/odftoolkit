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

package org.odftoolkit.simple.common.field;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.common.field.VariableField.VariableType;

/**
 * VariableContainer is a container which maintains the declared variables.
 * Variable(s) can be added in this container.
 * 
 * @see VariableField
 * @see org.odftoolkit.simple.TextDocument
 * @see org.odftoolkit.simple.text.Header
 * @see org.odftoolkit.simple.text.Footer
 * 
 * @since 0.5
 */
public interface VariableContainer {

	/**
	 * Get the ODF element which can have variable declare section as child
	 * element directly according to ODF specification. This element will help
	 * to find the position to declare a new variable.
	 * <p>
	 * The variable declare section may be <text:variable-decls>,
	 * <text:user-field-decls> or <text:sequence-decls>.
	 * 
	 * @return container element which can hold variable declare section.
	 */
	public OdfElement getVariableContainerElement();

	/**
	 * Declare a new variable field to this container.
	 * 
	 * @param name
	 *            the unique name of this variable field.
	 * @param type
	 *            the type of this variable field.
	 * 
	 * @return declared variable field.
	 */
	public VariableField declareVariable(String name, VariableType type);

	/**
	 * Return the declared variable by name.
	 * 
	 * @param name
	 *            the unique name of this variable field.
	 * @return the declared variable.
	 */
	public VariableField getVariableFieldByName(String name);

}
