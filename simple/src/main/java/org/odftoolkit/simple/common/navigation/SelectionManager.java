/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.simple.common.navigation;

import java.util.Hashtable;
import java.util.Vector;
import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 * SelectionManager can manage all the <code>Selection</code>s that are returned to end users. This
 * SelectionManager contains a repository of all <code>Selection</code>s, and will refresh the
 * status/index of <code>Selection</code>s after certain operation.
 */
public class SelectionManager {

  private Hashtable<OdfElement, Vector<Selection>> repository =
      new Hashtable<OdfElement, Vector<Selection>>();

  /**
   * Register the <code>Selection</code> item.
   *
   * @param item the <code>Selection</code> item
   */
  public void registerItem(Selection item) {
    OdfElement element = item.getElement();
    if (repository.containsKey(element)) {
      Vector<Selection> selections = repository.get(element);
      int i = 0;
      while (i < selections.size()) {
        if (selections.get(i).getIndex() > item.getIndex()) {
          selections.insertElementAt(item, i);
          break;
        }
        i++;
      }
      if (i == selections.size()) {
        selections.add(item);
      }
    } else {
      Vector<Selection> al = new Vector<Selection>();
      al.add(item);
      repository.put(element, al);
    }
  }

  /**
   * Refresh the <code>Selection</code>s in repository after a item is cut.
   *
   * @param cutItem the cut item
   */
  public synchronized void refreshAfterCut(Selection cutItem) {
    // travase the whole sub tree
    OdfElement element = cutItem.getElement();
    if (repository.containsKey(element)) {
      Vector<Selection> selections = repository.get(element);
      for (int i = 0; i < selections.size(); i++) {
        if (selections.get(i).getIndex() > cutItem.getIndex()) {
          selections.get(i).refreshAfterFrontalDelete(cutItem);
        }
      }
    }
  }

  /**
   * Refresh the selections in repository after pastedAtFrontOf operation is called.
   *
   * @param item the pasted item
   * @param positionItem the position item
   */
  public synchronized void refreshAfterPasteAtFrontOf(Selection item, Selection positionItem) {
    // travase the whole sub tree
    OdfElement element = positionItem.getElement();
    if (repository.containsKey(element)) {
      Vector<Selection> selections = repository.get(element);
      for (int i = 0; i < selections.size(); i++) {
        if (selections.get(i).getIndex() >= positionItem.getIndex()) {
          selections.get(i).refreshAfterFrontalInsert(item);
        }
      }
    }
  }

  /**
   * Refresh the <code>Selection</code>s in repository after pastedAtEndOf operation is called.
   *
   * @param item the pasted item
   * @param positionItem the position item
   */
  public synchronized void refreshAfterPasteAtEndOf(Selection item, Selection positionItem) {
    OdfElement element = positionItem.getElement();
    int positionIndex;
    if (positionItem instanceof TextSelection) {
      positionIndex = positionItem.getIndex() + ((TextSelection) positionItem).getText().length();
    } else {
      positionIndex = positionItem.getIndex();
    }
    if (repository.containsKey(element)) {
      Vector<Selection> selections = repository.get(element);
      for (int i = 0; i < selections.size(); i++) {
        if (selections.get(i).getIndex() >= positionIndex) {
          selections.get(i).refreshAfterFrontalInsert(item);
        }
      }
    }
  }

  /**
   * Remove the <code>Selection</code> from repository.
   *
   * @param item <code>Selection</code> item
   */
  public void unregisterItem(Selection item) {
    OdfElement element = item.getElement();
    if (repository.containsKey(element)) {
      Vector<Selection> selections = repository.get(element);
      selections.remove(item);
    }
  }

  /**
   * A direct method to update all the <code>Selection</code>s contained in a element after a
   * certain position.
   *
   * @param containerElement the container element
   * @param offset the offset
   * @param positionIndex the index of a certain position
   */
  public synchronized void refresh(OdfElement containerElement, int offset, int positionIndex) {
    if (repository.containsKey(containerElement)) {
      Vector<Selection> selections = repository.get(containerElement);
      for (int i = 0; i < selections.size(); i++) {
        if (selections.get(i).getIndex() >= positionIndex) {
          selections.get(i).refresh(offset);
        }
      }
    }
  }

  /**
   * Clears the repository being used by the SelectionManager. Must be called at the end of
   * Navigation.
   */
  public void clearRepository() {
    repository.clear();
  }
}
