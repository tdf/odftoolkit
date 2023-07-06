package org.odftoolkit.odfdom.incubator.search;

import java.util.Hashtable;
import java.util.Vector;

import org.odftoolkit.odfdom.pkg.OdfElement;

 /**
   * The SelectionManager can manage all the selections that are returned to end users by a Navigation instance.
   * The SelectionManager contains a repository of all selections, and will refresh the status/mIndex of
   * selections after certain operation.
   */
public class SelectionManagerNeu {

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
    public static synchronized void refresh(
        OdfElement containerElement, int offset, int positionIndex) {
      if (repository.containsKey(containerElement)) {
        Vector<Selection> selections = repository.get(containerElement);
        for (int i = 0; i < selections.size(); i++) {
          if (selections.get(i).getIndex() >= positionIndex) {
            selections.get(i).refresh(offset);
          }
        }
      }
    }

    private SelectionManagerNeu() {}
  }

