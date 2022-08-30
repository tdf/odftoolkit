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
package schema2template.grammar;

import java.util.Collection;

/**
 * The first purpose of this interface is to provide a kind of "piece of a puzzle" representing the
 * key definitions of a schema and their relationship:
 *
 * <ul>
 *   <li>Element PuzzlePiece
 *   <li>Attribute PuzzlePiece
 *   <li>Attribute Value PuzzlePiece
 *   <li>Attribute Datatype PuzzlePiece
 * </ul>
 *
 * <p>The second purpose of this interface is to hide the differences between one definition and a
 * Collection of definitions. By this you will be able to use single definitions and collections of
 * definitions as method parameters. The method getCollection() is a helper method for this.
 *
 * <p>By using this interface you declare that:
 *
 * <ul>
 *   <li>you don't care if a PuzzleComponent is a Collection of Jigsaw pieces or one single Jigsaw
 *       piece.
 *   <li>you expect one single name. If the PuzzleComponent is a Collection of definitions, all
 *       definitions have to be equally named. Calling getQName(), toString(), getLocalName() or
 *       getNamespace() on a Collection of differently named definitions will throw a
 *       RuntimeException. For example: A multiple definition of an attribute is frequently used in
 *       an XML schema.
 * </ul>
 *
 * Unambiguously named (ns:localname) object.
 *
 * <p>Contract: Every object implementing hasQName should overwrite the toString() method and return
 * the QName.
 *
 * <p>Warning: Using this interface does not imply any information about the equals() or hashCode()
 * methods. So for using objects with qualified names in a Collection, you need information from the
 * implementing class.
 */
public interface PuzzleComponent {

  /**
   * ELEMENT PuzzlePiece only: Get all child element Definitions
   *
   * @return The child Definitions of this PuzzleComponent
   */
  public PuzzlePieceSet getChildElements();

  /**
   * ELEMENT PuzzlePiece only: Get all attribute Definitions
   *
   * @return The attribute Definitions of this PuzzleComponent
   */
  public PuzzlePieceSet getAttributes();

  /**
   * ATTRIBUTE PuzzlePiece only: Get all datatype Definitions
   *
   * @return The datatype Definitions of this PuzzleComponent
   */
  public PuzzlePieceSet getDatatypes();

  /**
   * ATTRIBUTE PuzzlePiece only: Get all value Definitions
   *
   * @return The constant value Definitions of this PuzzleComponent
   */
  public PuzzlePieceSet getValues();

  /**
   * Get all parent Definitions
   *
   * @return The parent Definitions of this PuzzleComponent
   */
  public PuzzlePieceSet getParents();

  /**
   * Get type of PuzzlePiece [ELEMENT, ATTRIBUTE, VALUE, DATA]
   *
   * @return The type of this PuzzleComponent
   */
  public MSVExpressionType getType();

  /**
   * Determines whether this PuzzleComponent allows a text node as child.
   *
   * @return True if a text node is allowed, false otherwise
   */
  public boolean canHaveText();

  /**
   * Determines whether the child PuzzlePiece(s) is/are singleton(s)
   *
   * <p>Convention: If child is a collection this method returns false if one child element is no
   * singleton. If this is a collection this method returns false if child is no singleton for one
   * element of this.
   *
   * @param child PuzzleComponent child
   * @return True if child is defined as Singleton, falso otherwise.
   */
  public boolean isSingleton(PuzzleComponent child);

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
  public boolean isMandatory(PuzzleComponent child);

  /**
   * Method to treat NamedDefined as a Collection of PuzzlePiece
   *
   * @return Collection of PuzzlePiece objects
   */
  public Collection<PuzzlePiece> getCollection();

  /**
   * Get the QName (i.e. namespace:localname ) or without namespace just the local name as fallback
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
