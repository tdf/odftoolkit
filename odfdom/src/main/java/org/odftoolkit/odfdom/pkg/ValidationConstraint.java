/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2010 Oracle and/or its affiliates. All rights reserved.ed.
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
package org.odftoolkit.odfdom.pkg;

/**
 * A <code>ValidationConstraint</code> is used to collect validation conditions and their error
 * messages in case the constraint is not met.
 */
public interface ValidationConstraint {

  /**
   * Creates a localized description of a Constraint. Subclasses may override this method in order
   * to produce a locale-specific message. For subclasses that do not override this method, the
   * default implementation returns the same result as <code>getMessage()</code>.
   *
   * @return The localized description of this constraint.
   */
  public String getLocalizedMessage();

  /**
   * Returns the detail message string of this Constraint.
   *
   * @return the detail message string of this <code>Constraint</code> instance (which may be
   *     <code>null</code>).
   */
  public String getMessage();
}
