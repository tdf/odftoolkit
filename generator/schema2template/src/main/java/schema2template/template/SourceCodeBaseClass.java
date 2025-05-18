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
package schema2template.template;

import java.util.SortedSet;
import java.util.TreeSet;
import schema2template.grammar.PuzzlePiece;
import schema2template.grammar.PuzzlePieceSet;
import schema2template.grammar.XMLModel;

/**
 * This calss encapsulates the name of the java base class, which is a Java super class where shared
 * attributes and elements are being moved to. This class also offers functionality to find the
 * common attributes and elements via getBaseElements() and getBaseAttributes(). The base class
 * feature (its existence) is enabled by adding the attribute "base" one or more XML element named
 * in the grammar-additions.xml The base attributes holds the name of the base class feature is
 * stated like an XML node with a prefix. Like for the elements the prefix will become a subfolder
 * within "org.odftoolkit.odfdom.dom.element" as the base functionality is being placed aside of the
 * element class. The additional attribute "extends" for XML elements (and attributes) defines an
 * additional super class, which will be added to the XML class or if base exists within the base
 * class. The class path of the "extends" have to be a fully qualified Java package name, as the
 * Class can be anywhere. All elements with the same base class have to have the same extends super
 * class as all share these two as parent classes and there is no multiple inheritance in Java.
 *
 * <p>Convention: Unique key is the hash of the name of the baseClass. This hash is used for
 * compareTo(o) equals(o) and hashCode().
 */
public class SourceCodeBaseClass implements Comparable<SourceCodeBaseClass> {

  private SortedSet<PuzzlePiece> mChildElementsOfBaseClass;
  private String mBaseName;

  protected SourceCodeBaseClass(String baseName, SortedSet<PuzzlePiece> childElementsOfBaseClass) {
    mChildElementsOfBaseClass = childElementsOfBaseClass;
    mBaseName = baseName;
  }

  public int compareTo(SourceCodeBaseClass o) {
    return mBaseName.compareTo(o.mBaseName);
  }

  public boolean equals(Object o) {
    return (o instanceof SourceCodeBaseClass
        && ((SourceCodeBaseClass) o).mBaseName.equals(mBaseName));
  }

  public int hashCode() {
    return mBaseName.hashCode();
  }

  public String getLocalName() {
    return XMLModel.extractLocalName(mBaseName);
  }

  public String getQName() {
    return mBaseName;
  }

  public String getNamespacePrefix() {
    return XMLModel.extractNamespacePrefix(mBaseName);
  }

  public String toString() {
    return getQName();
  }

  /**
   * Returns the element Definitions which are subclassing this JavaBaseClass
   *
   * @return subclasses
   */
  public PuzzlePieceSet getChildElementsOfBaseClass() {
    return new PuzzlePieceSet(mChildElementsOfBaseClass);
  }

  /**
   * Returns the attribute Definitions which are shared by all subclasses of this JavaBaseClass
   *
   * @return attributes
   */
  public PuzzlePieceSet getBaseAttributes() {
    SortedSet<PuzzlePiece> attributes =
      new TreeSet<>(mChildElementsOfBaseClass.last().getAttributes());
    for (PuzzlePiece childElement :
        mChildElementsOfBaseClass.headSet(mChildElementsOfBaseClass.last())) {
      attributes.retainAll(childElement.getAttributes());
    }
    return new PuzzlePieceSet(attributes);
  }

  /**
   * Returns the element Definitions which are shared by all subclasses of this JavaBaseClass
   *
   * @return elements
   */
  public PuzzlePieceSet getBaseElements() {
    SortedSet<PuzzlePiece> elements =
            new TreeSet<>(mChildElementsOfBaseClass.last().getChildElements());
    for (PuzzlePiece childElement :
        mChildElementsOfBaseClass.headSet(mChildElementsOfBaseClass.last())) {
      elements.retainAll(childElement.getChildElements());
    }
    return new PuzzlePieceSet(elements);
  }
}
