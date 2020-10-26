/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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
package schema2template.model;

/**
 * Unambiguously named (ns:localname) object.
 *
 * <p>Contract: Every object implementing hasQName should overwrite the toString() method and return
 * the QName.
 *
 * <p>Warning: Using this interface does not imply any information about the equals() or hashCode()
 * methods. So for using objects with qualified names in a Collection, you need information from the
 * implementing class.
 */
public interface QNamed {

  /**
   * Get the QName (i.e. namespace:localname )
   *
   * @return full name
   */
  public String getQName();

  /**
   * Get only namespace
   *
   * @return namespace
   */
  public String getNamespace();

  /**
   * Get only localname
   *
   * @return localname
   */
  public String getLocalName();
}
