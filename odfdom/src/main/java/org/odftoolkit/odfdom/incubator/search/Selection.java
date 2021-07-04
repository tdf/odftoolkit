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

import org.odftoolkit.odfdom.incubator.doc.text.OdfWhitespaceProcessor;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

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

  /**
   * SelectionManager can manage all the selections that are returned to end users. This
   * SelectionManager contains a repository of all selections, and will refresh the status/mIndex of
   * selections after certain operation.
   */
  static class SelectionManager {

    private static Hashtable<OdfElement, Vector<Selection>> repository =
        new Hashtable<OdfElement, Vector<Selection>>();

    /**
     * Register the selection item
     *
     * @param item the selection item
     */
    public static void registerItem(Selection item) {
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
     * Refresh the selections in repository after a item is cut.
     *
     * @param cutItem the cut item
     */
    public static synchronized void refreshAfterCut(Selection cutItem) {
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
     * Refresh the selections in repository after a pastedAtFrontOf operation is called.
     *
     * @param item the pasted item
     * @param positionItem the position item
     */
    public static synchronized void refreshAfterPasteAtFrontOf(
        Selection item, Selection positionItem) {
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
     * Refresh the selections in repository after a pastedAtEndOf operation is called.
     *
     * @param item the pasted item
     * @param positionItem the position item
     */
    public static synchronized void refreshAfterPasteAtEndOf(
        Selection item, Selection positionItem) {
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
     * Remove the selection from repository.
     *
     * @param item selection item
     */
    public static void unregisterItem(Selection item) {
      OdfElement element = item.getElement();
      if (repository.containsKey(element)) {
        Vector<Selection> selections = repository.get(element);
        selections.remove(item);
      }
    }

    /**
     * A direct method to update all the selections contained in a mElement after a certain
     * position.
     *
     * @param containerElement the container mElement
     * @param offset the offset
     * @param positionIndex the mIndex of a certain position
     */
    public synchronized static void refresh(OdfElement containerElement, int offset, int positionIndex) {
        refreshParent(containerElement, offset);
        if (repository.containsKey(containerElement)) {
            Vector<Selection> selections = repository.get(containerElement);
            for (Selection selection : selections) {
                if (selection.getIndex() >= positionIndex) {
                    selection.refresh(offset);
                }
            }
        }
    }

    private static void refreshParent(OdfElement containerElement, int offset) {
        OdfElement parent = getOdfParent(containerElement);
        while (parent != null) {
            if (repository.containsKey(parent)) {
                Vector<Selection> selections = repository.get(parent);
                for (Selection selection : selections) {
                    if (isAfter(selection, containerElement)) {
                        selection.refresh(offset);
                    }
                }
            }
            parent = getOdfParent(parent);
        }
    }

    private static OdfElement getOdfParent(OdfElement element) {
        Node parent = element.getParentNode();
        while (parent != null && !(parent instanceof OdfElement) && parent != parent.getParentNode()) {
            parent = parent.getParentNode();
        }
        return parent instanceof OdfElement ? (OdfElement) parent : null;
    }

    private static boolean isAfter(Selection selection, OdfElement reference) {
        //Assumes that reference is a child of selection.getElement
        final OdfWhitespaceProcessor processor = new OdfWhitespaceProcessor();
        final String text = processor.getText(reference);
        final int idx = processor.getText(selection.getElement()).indexOf(text);
        if (idx == -1 || idx != processor.getText(selection.getElement()).lastIndexOf(text)) {
            //TODO obviously don't do that, need to work with Text nodes perhaps
            throw new IllegalStateException();
        }
        return selection.getIndex() >= idx + text.length();
    }

    private SelectionManager() {}
  }
}
