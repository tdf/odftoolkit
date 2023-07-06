/**
 * **********************************************************************
 *
 * <p>Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.incubator.search;

import java.util.Hashtable;
import java.util.Vector;
import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * Abstract class Selection describe one of the matched results The selection can be recognized by
 * the container mElement, the start mIndex of the text content of this mElement and the text
 * content.
 */
public abstract class Selection {

  private OdfElement mElement;
  private int mIndex;

  /**
   * get the container mElement of this selection
   *
   * @return the container mElement
   */
  public OdfElement getElement() {
    return mElement;
  }

  /**
   * get the start mIndex of the text content of the container mElement this is only meaningful for
   * TextSelection. other type Selection will return 0.
   *
   * @return the start mIndex of the container mElement
   */
  public int getIndex() {
    return mIndex;
  }

  /**
   * cut the current selection
   *
   * @throws InvalidNavigationException
   */
  public abstract void cut() throws InvalidNavigationException;

  /**
   * paste the current selection at front of the specified position selection
   *
   * @param positionitem the position selection
   * @throws InvalidNavigationException
   */
  public abstract void pasteAtFrontOf(Selection positionitem) throws InvalidNavigationException;

  /**
   * paste the current selection at end of the specified position selection
   *
   * @param positionitem the position selection
   * @throws InvalidNavigationException
   */
  public abstract void pasteAtEndOf(Selection positionitem) throws InvalidNavigationException;

  /**
   * when a selected item has been delete, the selections after this deleted selection should be
   * refresh because these selections mIndex will be changed
   *
   * @param deleteditem the deleted selection
   */
  protected abstract void refreshAfterFrontalDelete(Selection deleteditem);

  /**
   * when a selected item has been inserted, the selection after the inserted item should be refresh
   * because these selections mIndex will be changed
   *
   * @param inserteditem the inserted selection
   */
  protected abstract void refreshAfterFrontalInsert(Selection inserteditem);

  /**
   * A quick method to update the mIndex of this selection
   *
   * @param offset the offset that the mIndex should be added
   */
  protected abstract void refresh(int offset);


}
