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
 * By using this interface you declare that:
 *
 * <ul>
 *   <li>you don't care if a QNamedPuzzleComponent is a Collection of Jigsaw pieces or one single
 *       Jigsaw piece.
 *   <li>you expect one single name. If the QNamedPuzzleComponent is a Collection of definitions,
 *       all definitions have to be equally named. Calling getQName(), toString(), getLocalName() or
 *       getNamespace() on a Collection of differently named definitions will throw a
 *       RuntimeException. For example: A multiple definition of an attribute is frequently used in
 *       an XML schema.
 * </ul>
 */
public interface QNamedPuzzleComponent extends QNamed, PuzzleComponent {

  /**
   * ELEMENT Definition only: Determine solely by child type and name whether child is mandatory.
   *
   * <p>Here's why we're not using the child Definition object(s) for this: An element often has a
   * mandatory attribute, but two (or more) different content definitions for this attribute. This
   * is done by defining this attribute twice and creating a CHOICE between both Definitions. If
   * you'd ask whether one of these definitions is mandatory, you'd always get false as answer as
   * you have the choice between the two definitions. Mostly this is not the answer you're looking
   * for.
   *
   * <p>Contract: If 'this' is a Collection, mandatory means mandatory for one member of 'this'.
   *
   * @param child The child Definition(s) of type ELEMENT or ATTRIBUTE
   * @return true if child is a defined child of this and if it's mandatory. False otherwise.
   */
  public boolean isMandatory(QNamedPuzzleComponent child);
}
