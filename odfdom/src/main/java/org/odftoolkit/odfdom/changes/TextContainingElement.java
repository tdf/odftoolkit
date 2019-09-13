/*
 * Copyright 2012 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.odfdom.changes;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;

/**
 * This DOM container can be used for all ODF element with an text:style-name.
 *
 * All text handling is capsulated into this class.
 *
 * @author svante.schubertATgmail.com
 */
public abstract class TextContainingElement extends OdfStylableElement {

    private static final Logger LOG = Logger.getLogger(TextContainingElement.class.getName());
    private TreeSet<TextSelection> mSelections = null;

    /**
     * Create the instance of <code>TextParagraphElementBase</code>
     *
     * @param ownerDoc The type is <code>OdfFileDom</code>
     */
    public TextContainingElement(OdfFileDom ownerDoc, OdfName elementName, OdfStyleFamily styleFamily, OdfName styleAttrName) {
        super(ownerDoc, elementName, styleFamily, styleAttrName);
    }

    @Override
    public OdfName getOdfName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * only being used to create the root of all components, representing the
     * document without a parent element
     */
    private class SelectionComparator implements Comparator<TextSelection> {

        @Override
        public int compare(TextSelection o1, TextSelection o2) {
            int firstGreater = o1.compareTo(o2);
            return firstGreater;
        }
    }

    /**
     * @param outSelection as the end position was prior just given for a merge
     * the an outer selection will be added. Earlier added selection as similar
     * positions are therefore called "inner" selections and do have a higher
     * priority (as inner overwrites outer)
     * @return the element that is being kept (sometimes the element of the
     * given selection had to be dismissed, sometimes even the parent, when an
     * empty element was deleted).
     */
    public OdfElement appendTextSelection(TextSelection outerSelection) {
        if (this.mSelections == null) {
            Comparator c = new SelectionComparator();
            mSelections = new TreeSet<TextSelection>(c);
        }
        OdfElement parentElement = (OdfElement) outerSelection.mSelectionElement.getParentNode();

        // empty anchor does not make any sense..
        if ((outerSelection.mSelectionElement instanceof TextSpanElement || outerSelection.mSelectionElement instanceof TextAElement) && outerSelection.mSelectionElement.getChildNodes().getLength() != 0) {

            /* Working NOTE:
             * We need to keep the Anchors, what about the element with URL null, can I remove the anchor right away, or shall I do it here?
             * Adjust upper SAX parsing part
             */
            // there can always be only one <text:a> element in a range
            // Anchor <text:a> will be triggering an operation after the text content is parsed
            if (outerSelection.mSelectionElement instanceof TextAElement) {
                TextAElement outerAnchor = (TextAElement) outerSelection.mSelectionElement;
                String url = ((TextAElement) outerAnchor).getXlinkHrefAttribute();
                TextHyperlinkSelection overlappingInnerSelection = ((TextHyperlinkSelection) outerSelection).getOverLappingHyperlinkSelection(mSelections);
                if (overlappingInnerSelection != null) { // we need to split the old anchor
                    // split the outer element at the end of the inner anchor element (end first, to avoid counting invalidity)
                    outerAnchor.split(overlappingInnerSelection.mEndPosition.get(overlappingInnerSelection.mEndPosition.size() - 1));
                    // split the outer element at the beginning of the inner anchor element
                    OdfElement newMiddlePart = outerAnchor.split(overlappingInnerSelection.mStartPosition.get(overlappingInnerSelection.mStartPosition.size() - 1));
                    // remove the outer anchor element of the middle part (now top element)
                    OdfElement.removeSingleElement(newMiddlePart);
                } else if (url == null || url.isEmpty() || url.equals("null")) {
                    parentElement = (OdfElement) OdfElement.removeSingleElement(outerAnchor);
                } else if (mSelections.contains(outerSelection)) { // if there are is another span/anchor
                    // we need to remove the new outerAnchor from the DOM keeping its children/ancestors
                    SortedSet<TextSelection> equalSet = mSelections.subSet(outerSelection, true, outerSelection, true);
                    if (equalSet.size() < 2) {
                        TextSelection innerSelection;
                        Iterator<TextSelection> it = equalSet.iterator();
                        while (it.hasNext()) {
                            innerSelection = it.next();
                            if (innerSelection.mSelectionElement instanceof TextSpanElement) {
                                if (innerSelection.getURL() == null) {
                                    innerSelection.setURL(url);
                                    parentElement = (OdfElement) outerAnchor.getParentNode();
                                    break; //as there can only be one anchor
                                } else {
                                    // if there is only a span selection, but the URL was already set, remove the new anchor!
                                    parentElement = (OdfElement) OdfElement.removeSingleElement(outerAnchor);
                                    break;
                                }
                            } else if (innerSelection.mSelectionElement instanceof TextAElement) {
                                parentElement = (OdfElement) OdfElement.removeSingleElement(outerAnchor);
                            } else { // field
                                mSelections.add(outerSelection);
                                parentElement = (OdfElement) outerSelection.mSelectionElement.getParentNode();
                            }
                        }
                    } else {
                        // if there are more than two selections (span & anchor) remove the new anchor!)
                        parentElement = (OdfElement) OdfElement.removeSingleElement(outerAnchor);
                    }
                } else {
                    mSelections.add(outerSelection);
                    //parentElement = (OdfElement) outerSelection.mSelectionElement.getParentNode();
                }
                // if there is already a <text:span> at the same position, merge the styles
            } else if (mSelections.contains(outerSelection)) {
                // receives all spans and anchors that already exist for this position
                SortedSet<TextSelection> equalSet = mSelections.subSet(outerSelection, true, outerSelection, true);
                TextSelection innerSelection = null;
                Iterator<TextSelection> it = equalSet.iterator();
                parentElement = (OdfElement) outerSelection.mSelectionElement.getParentNode();
                while (it.hasNext()) {
                    innerSelection = it.next();
                    if (innerSelection.mSelectionElement instanceof TextSpanElement) {
                        OdfStylableElement innerElement = (OdfStylableElement) innerSelection.getSelectionElement();
                        OdfStylableElement outerElement = (OdfStylableElement) outerSelection.getSelectionElement();
                        OdfStyle.mergeSelectionWithSameRange(innerElement, outerElement);
                    } else {
                        String url = innerSelection.getURL();
                        mSelections.remove(innerSelection);
                        outerSelection.setURL(url);
                        mSelections.add(outerSelection);
                    }
                }
            } else {
                mSelections.add(outerSelection);
            }
        } else {
            // EMPTY ELEMENT
            // Remove empty elements, unless it is the last and only text span
//            if (outerSelection.mSelectionElement instanceof TextSpanElement && parentElement.getChildNodes().getLength() != 1) {
            parentElement.removeChild(outerSelection.mSelectionElement);
//            }

        }
        return parentElement;
    }

    public Collection<TextSelection> getTextSelections() {
        return mSelections;
    }
}
