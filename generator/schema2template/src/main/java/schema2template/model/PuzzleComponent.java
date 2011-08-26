/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package schema2template.model;

import java.util.Collection;

/**
 * <p>The first purpose of this interface is to provide a kind of "piece of a puzzle"
 * representing the key definitions of a schema and their relationship:</p>
 * <ul>
 * <li>Element PuzzlePiece</li>
 * <li>Attribute PuzzlePiece</li>
 * <li>Attribute Value PuzzlePiece</li>
 * <li>Attribute Datatype PuzzlePiece</li>
 * </ul>
 * <p>The second purpose of this interface is to hide the differences between one
 * definition and a Collection of definitions. By this you will be able
 * to use single definitions and collections of definitions as method
 * parameters. The method getCollection() is a helper method for this.</p>
 */
public interface PuzzleComponent {

    /**
     * ELEMENT PuzzlePiece only: Get all child element Definitions
     *
     * @return The child Definitions of this PuzzleComponent
     */
    public PuzzlePieceSet getChildElements();

    /**
     *  ELEMENT PuzzlePiece only: Get all attribute Definitions
     *
     * @return The attribute Definitions of this PuzzleComponent
     */
    public PuzzlePieceSet getAttributes();

    /**
     *  ATTRIBUTE PuzzlePiece only: Get all datatype Definitions
     *
     * @return The datatype Definitions of this PuzzleComponent
     */
    public PuzzlePieceSet getDatatypes();

    /**
     *  ATTRIBUTE PuzzlePiece only: Get all value Definitions
     *
     * @return The constant value Definitions of this PuzzleComponent
     */
    public PuzzlePieceSet getValues();

    /**
     *  Get all parent Definitions
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
     * Convention: If child is a collection this method returns false if one child element is no singleton.
     * If this is a collection this method returns false if child is no singleton for one element of this.
     *
     * @param child PuzzleComponent child
     * @return True if child is defined as Singleton, falso otherwise.
     */
    public boolean isSingleton(PuzzleComponent child);

    /**
     * Method to treat NamedDefined as a Collection of PuzzlePiece
     *
     * @return Collection of PuzzlePiece objects
     */
    public Collection<PuzzlePiece> getCollection();
    
}
