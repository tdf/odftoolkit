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
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;

/**
 *
 * @author svante.schubertATgmail.com
 */
public class TextHyperlinkSelection extends TextSelection implements Comparable {

    /**
     * Constructor.
     *
     * @param AnchorElement the AnchorElement of the Anchor element.
     * @param startPosition the startPosition of the Anchor element.
     */
    public TextHyperlinkSelection(TextAElement anchorElement, List<Integer> startPosition) {
        mUrl = anchorElement.getXlinkHrefAttribute();
        mSelectionElement = anchorElement;
        mStartPosition = startPosition;
    }

    /**
     * Constructor.
     *
     * @param AnchorElement the AnchorElement of the Anchor element.
     * @param startPosition the startPosition of the Anchor element.
     */
    public TextHyperlinkSelection(TextAElement anchorElement, List<Integer> startPosition, List<Integer> endPosition) {
        mUrl = anchorElement.getXlinkHrefAttribute();
        mSelectionElement = anchorElement;
        mStartPosition = startPosition;
        mEndPosition = endPosition;
    }

    /**
     * Returns the AnchorElement of the Anchor element.
     *
     * @return the AnchorElement of the Anchor element.
     */
    public TextAElement getAElement() {
        return (TextAElement) mSelectionElement;
    }

    /**
     * @param TreeSet of TextSelections, it is assumed there is only one anchor
     * in such set.
     * @return the first overlapping text Element in the given set (ascending
     * searched).
     */
    public TextHyperlinkSelection getOverLappingHyperlinkSelection(TreeSet<TextSelection> set) {
        TextHyperlinkSelection anchor = null;
        Iterator<TextSelection> it = set.iterator();
        TextSelection currentSelection = null;
        while (it.hasNext()) {
            currentSelection = it.next();
            if (currentSelection.mSelectionElement instanceof TextAElement) {
                if (TextSelection.overLapping(this, currentSelection)) {
                    anchor = (TextHyperlinkSelection) currentSelection;
                    break;
                }
            }
        }
        return anchor;
    }

    @Override
    public String toString() {
        return mStartPosition.toString() + "-URL" + mUrl + "-" + mEndPosition.toString() + mSelectionElement.toString() + ((TextAElement) mSelectionElement).getAutomaticStyle().toString();
    }

}
