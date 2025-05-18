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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * The SelectionManager can manage all the selections that are returned to end users by a Navigation
 * instance. The SelectionManager contains a repository of all selections, and will refresh the
 * status/mIndex of selections after certain operation.
 */
public class SelectionManager {

  private Map<OdfElement, ArrayList<Selection>> repository = null;

  public SelectionManager() {
    repository = new HashMap<>();
  }

  /**
   * Register the selection item
   *
   * @param item the selection item
   */
  public void registerItem(Selection item) {
    OdfElement element = item.getElement();
    if (repository.containsKey(element)) {
      ArrayList<Selection> selections = repository.get(element);
      int i = 0;
      while (i < selections.size()) {
        if (selections.get(i).getIndex() > item.getIndex()) {
          selections.add(i, item);
          break;
        }
        i++;
      }
      if (i == selections.size()) {
        selections.add(item);
      }
    } else {
      ArrayList<Selection> al = new ArrayList<>();
      al.add(item);
      repository.put(element, al);
    }
  }

  /**
   * Refresh the selections in repository after a item is cut.
   *
   * @param cutItem the cut item
   */
  public void refreshAfterCut(Selection cutItem) {
    // travase the whole sub tree
    OdfElement element = cutItem.getElement();
    if (repository.containsKey(element)) {
      ArrayList<Selection> selections = repository.get(element);
      for (Selection selection : selections) {
        if (selection.getIndex() > cutItem.getIndex()) {
          selection.refreshAfterFrontalDelete(cutItem);
        }
      }
    }
  }

  /**
   * Refresh the selections in repository after a pastedAtFrontOf operation is called.
   *
   * @param item the pasted item
   * @param positionItem the position item
   */
  public void refreshAfterPasteAtFrontOf(Selection item, Selection positionItem) {
    // traverse the whole sub tree
    OdfElement element = positionItem.getElement();
    if (repository.containsKey(element)) {
      ArrayList<Selection> selections = repository.get(element);
      for (Selection selection : selections) {
        if (selection.getIndex() >= positionItem.getIndex()) {
          selection.refreshAfterFrontalInsert(item);
        }
      }
    }
  }

  /**
   * Refresh the selections in repository after a pastedAtEndOf operation is called.
   *
   * @param item the pasted item
   * @param positionItem the position item
   */
  public void refreshAfterPasteAtEndOf(Selection item, Selection positionItem) {
    OdfElement element = positionItem.getElement();
    int positionIndex;

    if (positionItem instanceof TextSelection) {
      positionIndex = positionItem.getIndex() + ((TextSelection) positionItem).getText().length();
    } else {
      positionIndex = positionItem.getIndex();
    }

    if (repository.containsKey(element)) {
      ArrayList<Selection> selections = repository.get(element);
      for (Selection selection : selections) {
        if (selection.getIndex() >= positionIndex) {
          selection.refreshAfterFrontalInsert(item);
        }
      }
    }
  }

  /**
   * Remove the selection from repository.
   *
   * @param item selection item
   */
  public void unregisterItem(Selection item) {
    OdfElement element = item.getElement();
    if (repository.containsKey(element)) {
      ArrayList<Selection> selections = repository.get(element);
      selections.remove(item);
    }
  }

  /**
   * Removes all selections from the SelectionManager.
   */
  public void unregisterAll() {
    repository.clear();
  }

  /**
   * A direct method to update all the selections contained in a mElement after a certain position.
   *
   * @param containerElement the container mElement
   * @param offset the offset
   * @param positionIndex the mIndex of a certain position
   */
  public void refresh(OdfElement containerElement, int offset, int positionIndex) {
    if (repository.containsKey(containerElement)) {
      ArrayList<Selection> selections = repository.get(containerElement);
      for (Selection selection : selections) {
        if (selection.getIndex() >= positionIndex) {
          selection.refresh(offset);
        }
      }
    }
  }
}
