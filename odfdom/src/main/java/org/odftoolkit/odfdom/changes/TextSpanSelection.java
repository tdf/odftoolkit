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

import java.util.List;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;

/** @author svante.schubertATgmail.com */
public class TextSpanSelection extends TextSelection {

  /**
   * Constructor.
   *
   * @param spanElement the spanElement of the Span element.
   * @param startPosition the startPosition of the Span element.
   */
  public TextSpanSelection(TextSpanElement spanElement, List<Integer> startPosition) {
    mSelectionElement = spanElement;
    mStartPosition = startPosition;
  }

  /**
   * Constructor.
   *
   * @param spanElement the spanElement of the Span element.
   * @param startPosition the startPosition of the Span element.
   */
  public TextSpanSelection(
      TextSpanElement spanElement, List<Integer> startPosition, List<Integer> endPosition) {
    mSelectionElement = spanElement;
    mStartPosition = startPosition;
    mEndPosition = endPosition;
  }

  /**
   * Returns the spanElement of the Span element.
   *
   * @return the spanElement of the Span element.
   */
  public TextSpanElement getSpanElement() {
    return (TextSpanElement) mSelectionElement;
  }

  @Override
  public String toString() {
    return mStartPosition
        + "-URL"
        + mUrl
        + "-"
        + mEndPosition
        + mSelectionElement
        + ((TextSpanElement) mSelectionElement).getAutomaticStyle();
  }
}
