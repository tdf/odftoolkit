/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Collection Class for RelaxNG definitions of an Element, Attribute, Value or Datatype.
 *
 * <p>Conventions: <ul><li>PuzzlePiece sorting is done by ns:local tag names as first key and hashCode as second key (see class PuzzlePiece).</li>
 * <li>Since it is a Collection, PuzzlePieceSet is not meant to be used in a Collection. So equals(o) and hashCode() are not overwritten</li>
 * <li>All returned PuzzlePieceSet objects are immutable to protect them against
 * naive usage in velocity templates</li></ul></p>
 */
public class PuzzlePieceSet implements QNamedPuzzleComponent, Collection<PuzzlePiece> {

    private boolean mImmutable = false;
    private SortedSet<PuzzlePiece> mDefinitions;

    public PuzzlePieceSet() {
        mDefinitions = new TreeSet<PuzzlePiece>();
    }

    public PuzzlePieceSet(Collection<PuzzlePiece> c) {
        mDefinitions = new TreeSet<PuzzlePiece>(c);
    }

    private void assertNotImmutable() {
        if (mImmutable) {
            throw new RuntimeException("Attempt to change an immutable DefinitionSet.");
        }
    }

    private void assertNotEmpty(String plannedAction) {
        if (this.size()==0) {
            throw new RuntimeException("Attempt to " + plannedAction + " of empty DefinitionSet ");
        }
    }
    
    private void assertMultiples(String plannedAction) {
        assertNotEmpty(plannedAction);
        PuzzlePiece first = first();
        MSVExpressionType type = first.getType();
        String name = first.getQName();
        for (PuzzlePiece def : this) {
            if (!type.equals(def.getType())) {
                throw new RuntimeException("Attempt to " + plannedAction + " of DefinitionSet consisting of different types of Definition objetcs.");
            }
            String defname = def.getQName();
            if ((name == null && defname != null) || !name.equals(defname)) {
                throw new RuntimeException("Attempt to " + plannedAction + " of DefinitionSet consisting of differently named Definition objetcs.");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PuzzlePieceSet && ((PuzzlePieceSet) o).mDefinitions.equals(mDefinitions)) ? true : false;
    }

    @Override
    public int hashCode() {
        return mDefinitions.hashCode();
    }
    
    private PuzzlePiece first() {
        return this.iterator().next();
    }

    /* Unite Definitions with equal content
     *
     * Returns a Map of the lost Definitions to their survived counterparts
     */
    Map<PuzzlePiece, PuzzlePiece> uniteDefinitionsWithEqualContent() {
        Map<PuzzlePiece, PuzzlePiece> retval = new HashMap<PuzzlePiece, PuzzlePiece>();
        SortedSet<PuzzlePiece> immutableSet = new TreeSet<PuzzlePiece>(this.mDefinitions);
        for (PuzzlePiece def1 : immutableSet) {
            if (!this.mDefinitions.contains(def1)) {
                // if def1 is already removed, we shouldn't process it
                continue;
            }
            for (PuzzlePiece def2 : immutableSet.tailSet(def1)) {
                if (def1 == def2) {
                    // Don't compare def1 to def1
                    continue;
                }
                if (!this.mDefinitions.contains(def2)) {
                    //if def2 is already removed, we shouldn't process it
                    continue;
                }
                if (!def1.getQName().equals(def2.getQName())) {
                    break;
                }
                // Now test for content equality
                if (def1.contentEquals(def2)) {
                    // remove from map
                    this.remove(def2);
                    // map lost to survived counterpart
                    retval.put(def2, def1);
                    // mix parent information
                    def1.getParents().addAll(def2.getParents());
                }
            }
        }
        // Remove deleted multiples in a separate step since in the first step the needed
        // information wasn't present (note that Multiples are no synonym for Definitions with equal content!)
        for (PuzzlePiece def : this.mDefinitions) {
            PuzzlePieceSet immutableDups = new PuzzlePieceSet(def.withMultiples());
            for (PuzzlePiece multiple : immutableDups) {
                if (multiple != def) {
                    if (!this.mDefinitions.contains(multiple)) {
                        def.withMultiples().remove(multiple);
                    }
                }
            }
        }
        return retval;
    }
    
    /**
     * Make PuzzlePieceSet immutable. Cannot be undone.
     *
     * Template Usage: Not for use in templates as all PuzzlePieceSet already have been made immutable.
     */
    public void makeImmutable() {
        mImmutable = true;
    }

    /**
     * <p>Returns new PuzzlePieceSet containing the elements of this PuzzlePieceSet, but restricted to one
     * PuzzlePiece per Name.</p>
     *
     * <p>Template Usage: #foreach ($element in $elements.withoutMultiples())</p>
     *
     * @return new PuzzlePieceSet
     */
    public PuzzlePieceSet withoutMultiples() {
        Map<String, PuzzlePiece> uniqueMap = new HashMap<String, PuzzlePiece>();
        for (PuzzlePiece def : this) {
            uniqueMap.put(def.getQName(), def);
        }
        return new PuzzlePieceSet(uniqueMap.values());
    }

    /**
     * <p>Returns new PuzzlePieceSet containing the elements of this PuzzlePieceSet, but without
     * the elements of the parameter removeAll</p>
     *
     * <p>Template Usage: #set ($non_base_attributes = $element.getAttributes().without($baseclass.getAttributes())</p>
     *
     * @param removeAll QNamedPuzzleComponent which (or which elements) should be removed from the new PuzzlePieceSet
     * @return new PuzzlePieceSet
     */
    public PuzzlePieceSet without(QNamedPuzzleComponent removeAll) {
        PuzzlePieceSet retval = new PuzzlePieceSet(this);
        retval.removeAll(removeAll.getCollection());
        return retval;
    }

    /**
     * Returns new PuzzlePieceSet containing the elements of this PuzzlePieceSet, but only those
     * which have at least one element from the QNamedPuzzleComponent parameter as one of their parent Definitions.
     *
     * <p>Template Usage: Imagine we have one attribute name and we're not interested in the differences
     * between Definitions sharing the same name. We're now printing the
     * resulting allowed attribute values for each parent element name: </p>
     * <code><br />
     * #set ( $oneOrMoreAttributes = $model.getAttribute($atttributename) )<br />
     * ## we want to write information about only _one_ parent per Name...<br />
     * #foreach ($parent in $oneOrMoreAttributes.getParents().withoutMultiples())<br/>
     * - Allowed Values for Parent Element $parent :<br />
     * ## but we want the attribute values displayed which are allowed in _all_ parents with the same Name...<br />
     * #foreach ($value in $oneOrMoreAttributes.byParent($parent.withMultiples()).getValues())<br />
     *    -- "$value" <br />
     * #end<br />
     * #end
     * </code><br />
     *
     * @param parents
     * @return new PuzzlePieceSet
     */
    public PuzzlePieceSet byParent(QNamedPuzzleComponent parents) {
        PuzzlePieceSet retval = new PuzzlePieceSet();
        for (PuzzlePiece def : this) {
            PuzzlePieceSet defparents = def.getParents();
            for (PuzzlePiece parent : parents.getCollection()) {
                if (defparents.contains(parent)) {
                    retval.add(def);
                    break;
                }
            }
        }
        return retval;
    }

    /**
     * Check whether this List contains an Element by this Name
     *
     * @param aDefinitionName
     * @return True if an element by this name exists
     */
    public boolean containsName(String aDefinitionName) {
        for (PuzzlePiece def : this) {
            if (def.getQName().equals(aDefinitionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether this List contains an Element by this Name
     *
     * @param aNamed
     * @return True if an element by this name exists
     */
    public boolean containsName(QNamed aNamed) {
        return containsName(aNamed.getQName());
    }

/*
 * -----------------------------------------------------
 *  Interface QNamed
 * -----------------------------------------------------
 */


    /**
     * Gets the ns:local tag name of the Definitions - provided that this PuzzlePieceSet
     * is not empty and all Definitions share the same tag name. Throws Exception otherwise.
     *
     * @return The tag name
     */
    @Override
    public String getQName() {
        assertMultiples("get name");
        return first().getQName();
    }

    /**
     * Gets the type of the Definitions - provided that this PuzzlePieceSet
     * is not empty and all Definitions have the same type and name. Throws Exception otherwise.
     */
    @Override
    public MSVExpressionType getType() {
        assertMultiples("get type");
        return first().getType();
    }

    /**
     * Determines whether the Definitions can have text - provided that this PuzzlePieceSet
     * is not empty and all Definitions have the same type and name. Throws Exception otherwise.
     */
    @Override
    public boolean canHaveText() {
        assertMultiples("determine text availability");
        for (PuzzlePiece def : this) {
            if (def.canHaveText()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isSingleton(PuzzleComponent child) {
        for (PuzzlePiece def : this) {
            if (!def.isSingleton(child)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getLocalName() {
        return XMLModel.extractLocalname(getQName());
    }

    @Override
    public String getNamespace() {
        return XMLModel.extractNamespace(getQName());
    }

    /**
     * <p>Returns String representation (convenient method for getQName())</p>
     *
     * <p>Template Usage: Just use $aDefinitionSet as you would use a string variable</p>
     */
    @Override
    public String toString() {
        return getQName();
    }

/*
 * -----------------------------------------------------
 *  Interface Collection<PuzzlePiece>
 * -----------------------------------------------------
 */

    @Override
    public boolean add(PuzzlePiece e) {
        assertNotImmutable();
        return mDefinitions.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends PuzzlePiece> c) {
        assertNotImmutable();
        return mDefinitions.addAll(c);
    }

    @Override
    public void clear() {
        assertNotImmutable();
        mDefinitions.clear();
    }

    @Override
    public boolean contains(Object o) {
        return (o instanceof PuzzlePiece) ? mDefinitions.contains((PuzzlePiece) o) : false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return mDefinitions.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return mDefinitions.isEmpty();
    }

    @Override
    public Iterator<PuzzlePiece> iterator() {
        return mDefinitions.iterator();
    }

    @Override
    public boolean remove(Object o) {
        assertNotImmutable();
        return (o instanceof PuzzlePiece) ? mDefinitions.remove((PuzzlePiece) o) : false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        assertNotImmutable();
        return mDefinitions.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        assertNotImmutable();
        return mDefinitions.retainAll(c);
    }

    @Override
    public int size() {
        return mDefinitions.size();
    }

    @Override
    public Object[] toArray() {
        return mDefinitions.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return mDefinitions.toArray(a);
    }

/*
 * -----------------------------------------------------
 *  Interface QNamedPuzzleComponent
 * -----------------------------------------------------
 */

    @Override
    public PuzzlePieceSet getChildElements() {
        PuzzlePieceSet retval = new PuzzlePieceSet();
        for (PuzzlePiece def : this) {
            retval.addAll(def.getChildElements());
        }
        return retval;
    }

    @Override
    public boolean isMandatory(QNamedPuzzleComponent child) {
        for (PuzzlePiece def : this) {
            if (def.isMandatory(child)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<PuzzlePiece> getCollection() {
        return mDefinitions;
    }

    @Override
    public PuzzlePieceSet getAttributes() {
        PuzzlePieceSet retval = new PuzzlePieceSet();
        for (PuzzlePiece def : this) {
            retval.addAll(def.getAttributes());
        }
        return retval;
    }

    @Override
    public PuzzlePieceSet getDatatypes() {
        PuzzlePieceSet retval = new PuzzlePieceSet();
        for (PuzzlePiece def : this) {
            retval.addAll(def.getDatatypes());
        }
        return retval;
    }

    @Override
    public PuzzlePieceSet getParents() {
        PuzzlePieceSet retval = new PuzzlePieceSet();
        for (PuzzlePiece def : this) {
            retval.addAll(def.getParents());
        }
        return retval;
    }


    @Override
    public PuzzlePieceSet getValues() {
        PuzzlePieceSet retval = new PuzzlePieceSet();
        for (PuzzlePiece def : this) {
            retval.addAll(def.getValues());
        }
        return retval;
    }

}
