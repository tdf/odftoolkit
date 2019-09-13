/*
 * Copyright 2013 The Apache Software Foundation.
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

import java.util.List;
import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 *
 * @author svante.schubertATgmail.com
 */
public abstract class TextSelection {

    protected List<Integer> mStartPosition;
    protected List<Integer> mEndPosition;
    protected OdfElement mSelectionElement;
    protected String mUrl;

    /**
     * Returns the startPosition of the Anchor element.
     *
     * @return the startPosition of the Anchor element.
     */
    public List<Integer> getStartPosition() {
        return mStartPosition;
    }

    /**
     * Returns the endPosition of the Anchor element.
     *
     * @return the endPosition of the Anchor element.
     */
    public List<Integer> getEndPosition() {
        return mEndPosition;
    }

    /**
     * @param endPosition end position of the Anchor element.
     *
     */
    public void setEndPosition(List<Integer> endPosition) {
        this.mEndPosition = endPosition;
    }

    /**
     * @return the element being used for selecting the text.
     */
    public OdfElement getSelectionElement() {
        return mSelectionElement;
    }

    /**
     * Hyperlinks need to keep their URL even if merged with spans.
     *
     * @return true if a text hyperlink, or a span a hyperlink was merged into
     */
    public boolean hasUrl() {
        return mUrl != null;
    }

    /**
     * @return the hyperlink URL
     */
    public String getURL() {
        return mUrl;
    }

    /**
     * Even on a spanSelection a URL must be able to be set, in case an anchor
     * is merged into it
     */
    public void setURL(String url) {
        mUrl = url;
    }

    /**
     * Test if one of the two given TextSelection is overlapping
     */
    public static boolean overLapping(TextSelection s1, TextSelection s2) {
        boolean isOverlapping;
        // S1 INBETWEEN S2
        // is the start of s1 inbetween s2  positions
        isOverlapping = isInbetween(s1.mStartPosition, s2);

        // is the end of s1 inbetween s2 positions
        if (!isOverlapping) {
            isOverlapping = isInbetween(s1.mEndPosition, s2);
        }
        if (!isOverlapping) {
            // S2 INBETWEEN S1
            // is the start of s2 inbetween s1  positions
            isOverlapping = isInbetween(s2.mStartPosition, s1);

            // is the end of s2 inbetween s1 positions
            if (!isOverlapping) {
                isOverlapping = isInbetween(s2.mEndPosition, s1);
            }
        }

        return isOverlapping;
    }

    /**
     * Tests if the given position is in between the given TextSelection
     */
    private static boolean isInbetween(List<Integer> firstPosition, TextSelection s2) {
        boolean isInbetween = false;
        boolean firstPositionIsAfter2ndStart = false;
        boolean firstPositionIsBefore2ndEnd = false;
        // first position after START position
        if (1 == comparePosition(firstPosition, s2.mStartPosition)) {
            firstPositionIsAfter2ndStart = true;
        }
        // first position before END position
        if (-1 == comparePosition(firstPosition, s2.mEndPosition)) {
            firstPositionIsBefore2ndEnd = true;
        }
        if (firstPositionIsAfter2ndStart && firstPositionIsBefore2ndEnd) {
            isInbetween = true;
        }
        return isInbetween;
    }

    /**
     *
     * int compareTo(T o) Compares this object with the specified object for
     * order. Returns a negative integer, zero, or a positive integer as this
     * object is less than, equal to, or greater than the specified object. The
     * implementor must ensure sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) for
     * all x and y. (This implies that x.compareTo(y) must throw an exception
     * iff y.compareTo(x) throws an exception.)
     *
     * The implementor must also ensure that the relation is transitive:
     * (x.compareTo(y)>0 && y.compareTo(z)>0) implies x.compareTo(z)>0.
     *
     * Finally, the implementor must ensure that x.compareTo(y)==0 implies that
     * sgn(x.compareTo(z)) == sgn(y.compareTo(z)), for all z.
     *
     * It is strongly recommended, but not strictly required that
     * (x.compareTo(y)==0) == (x.equals(y)). Generally speaking, any class that
     * implements the Comparable interface and violates this condition should
     * clearly indicate this fact. The recommended language is "Note: this class
     * has a natural ordering that is inconsistent with equals."
     *
     * In the foregoing description, the notation sgn(expression) designates the
     * mathematical signum function, which is defined to return one of -1, 0, or
     * 1 according to whether the value of expression is negative, zero or
     * positive.
     *
     * Parameters: o - the object to be compared. Returns: a negative integer,
     * zero, or a positive integer as this object is less than, equal to, or
     * greater than the specified object. Throws: ClassCastException - if the
     * specified object's type prevents it from being compared to this object.
     */
    public int compareTo(Object o) {
        TextSelection s2 = (TextSelection) o;

        int result = 0;
        result = comparePosition(this.getStartPosition(), s2.getStartPosition());
        if (result == 0) {
            result = comparePosition(this.getEndPosition(), s2.getEndPosition());
        }
        return result;
    }

    /**
     * @returns 1 if pos1 is after pos2, -1 if pos1 was before pos2 and 0 if
     * both positions are equal
     */
    private static int comparePosition(List<Integer> pos1, List<Integer> pos2) {
        int result = 0;
        int length1 = pos1.size();
        int length2 = pos2.size();
        int i = 0;
        int value1;
        int value2;
        do {
            // check if length is sufficent
            if (length1 > i) {
                value1 = pos1.get(i);
            } else {
                value1 = -1;
            }
            if (length2 > i) {
                value2 = pos2.get(i);
            } else {
                value2 = -1;
            }
            if (value1 > value2) {
                result = 1;
                break;
            } else if (value2 > value1) {
                result = -1;
                break;
            }
            if (value1 == -1 || value2 == -1) {
                break;
            }
            i++;
        } while (true);
        return result;
    }
}
