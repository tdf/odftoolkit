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

import static org.odftoolkit.odfdom.changes.OperationConstants.*; // The names of operations /

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelLabelAlignmentElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleBulletElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleNumberElement;
import org.odftoolkit.odfdom.dom.element.text.TextListStyleElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.type.Length;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// changes

/**
 * ToDo: Is it more flexible to build a different queue for OperationQueue and create an JSON
 * exporter? Can a JSONArray / JSONObject be initialized with an existing queue?
 *
 * @author svante.schubertATgmail.com
 */
public class JsonOperationProducer {

  private static final Logger LOG = Logger.getLogger(JsonOperationProducer.class.getName());

  static final String BLACK = "#000000";
  private static final String ODFDOM_GIT_BRANCH = System.getProperty("odftoolkit.git.branch");
  private static final String ODFDOM_GIT_COMMIT_TIME =
      System.getProperty("odftoolkit.git.commit.time");
  private static final String ODFDOM_GIT_COMMIT_DESCRIBE =
      System.getProperty("odftoolkit.git.commit.id.describe");
  private static final String ODFDOM_GIT_URL =
      System.getProperty("odftoolkit.git.remote.origin.url");

  // line widths constants
  private final JSONArray mOperationQueue = new JSONArray();
  private final JSONObject mOperations = new JSONObject();
  private final JSONObject mDocumentAttributes = new JSONObject();
  /** The maximum empty cell number before starting a new operation */
  /** Every knonwStyle does not have to be read */
  Map knownStyles = new HashMap<String, Boolean>();
  // Added an own map for list styles as it is not 100% certain that the names between styles and
  // list style might be overlapping.
  Map knownListStyles = new HashMap<String, Boolean>();
  /**
   * There is a special style for the replacement table of too large tables, which have to be added
   * only once to a document
   */
  boolean mIsTableExceededStyleAdded = false;

  public JsonOperationProducer() {
    try {
      mDocumentAttributes.put(OPK_NAME, "noOp");
      mOperationQueue.put(mDocumentAttributes);
      mOperations.put(OPK_EDITOR, ODFDOM_GIT_URL);
      mOperations.put(OPK_VERSION, ODFDOM_GIT_COMMIT_DESCRIBE);
      mOperations.put(OPK_VERSION_BRANCH, ODFDOM_GIT_BRANCH);
      mOperations.put(OPK_VERSION_TIME, ODFDOM_GIT_COMMIT_TIME);
      mOperations.put(OPK_OPERATIONS, mOperationQueue);
    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public JSONObject getDocumentOperations() {
    return mOperations;
  }

  /** Used for repeated elements, repeats a set of operations */
  public int getCurrentOperationIndex() {
    return mOperations.length() - 1;
  }

  // -------------------------------------------------------------------------
  /**
   * @param componentType
   * @param start: An array, that contains the number of that paragraph, before which the
   *     new paragraph shall be inserted. Has the last paragraph the number 2, so causes para=3,
   *     that the new paragraph will be inserted at the end. para=4 is not allowed in this case.
   * @param formattingProperties
   * @param context
   */
  public void add(
      String componentType,
      final List<Integer> start,
      final Map<String, Object> formattingProperties,
      String context) {
    // ToDo: Moving styleId into para list -
    // final Map<String, Object> args,

    final JSONObject addComponentObject = new JSONObject();

    try {
      addComponentObject.put(OPK_NAME, "add" + componentType);
      addComponentObject.put(OPK_START, incrementAll(start));
      if (context != null) {
        addComponentObject.put(OPK_CONTEXT, context);
      }
      // ToDo: Moving styleId into para list -
      //			if (args != null) {
      //				for (String arg : args.keySet()) {
      //					newOperation.put(arg, args.get(arg));
      //				}
      //			}
      if (formattingProperties != null && !formattingProperties.isEmpty()) {
        JSONObject attrs = new JSONObject();
        for (String arg : formattingProperties.keySet()) {
          attrs.put(arg, formattingProperties.get(arg));
        }
        addComponentObject.put(OPK_ATTRS, attrs);
      }
      // IN CASE STYLE BECOMES AN ATTRIBUTE
      //			if (args != null || formattingProperties != null && !formattingProperties.isEmpty()) {
      //				JSONObject attrs = new JSONObject();
      //
      //				if (args != null) {
      //					for (String arg : args.keySet()) {
      //						attrs.put(arg, args.get(arg));
      //					}
      //				}
      //				if (formattingProperties != null && !formattingProperties.isEmpty()) {
      //					for (String arg : formattingProperties.keySet()) {
      //						attrs.put(arg, formattingProperties.get(arg));
      //					}
      //					newOperation.put(OPK_ATTRS, attrs);
      //				}
      //			}

      mOperationQueue.put(addComponentObject);
      LOG.log(Level.FINEST, "add" + componentType + " - component:{0}", addComponentObject);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public void addAnnotation(
      final List<Integer> start, String id, String author, String date, String context) {
    final JSONObject newOperation = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_NOTE);
      newOperation.put(OPK_START, incrementAll(start));
      newOperation.put(OPK_ID, id);
      newOperation.put("author", author);
      newOperation.put("date", date);
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_NOTE + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public void addRange(final List<Integer> start, String id, String context) {
    final JSONObject newOperation = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_NOTE_SELECTION);
      newOperation.put(OPK_START, incrementAll(start));
      newOperation.put(OPK_ID, id);
      newOperation.put(OPK_TYPE, "comment");
      newOperation.put(OPK_POSITION, "end");
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_NOTE_SELECTION + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  /**
   * @param start: An array, that contains the number of that paragraph, before which the new
   *     paragraph shall be inserted. Has the last paragraph the number 2, so causes para=3, that
   *     the new paragraph will be inserted at the end. para=4 is not allowed in this case.
   */
  public void formatColumns(
      final List<Integer> start,
      final Map<String, Object> formattingProperties,
      Integer firstColumn,
      Integer lastColumn,
      String context) {
    if (formattingProperties != null && !formattingProperties.isEmpty()) {
      final JSONObject newOperation = new JSONObject();

      try {
        newOperation.put(OPK_NAME, FORMATCOLUMNS);
        newOperation.put(OPK_SHEET, start.get(0));
        newOperation.put(OPK_START, firstColumn);
        if (context != null) {
          newOperation.put(OPK_CONTEXT, context);
        }
        if (lastColumn != null && !firstColumn.equals(lastColumn)) {
          newOperation.put(OPK_END, lastColumn);
        }
        if (formattingProperties != null && !formattingProperties.isEmpty()) {
          JSONObject attrs = new JSONObject();
          for (String arg : formattingProperties.keySet()) {
            attrs.put(arg, formattingProperties.get(arg));
          }
          newOperation.put(OPK_ATTRS, attrs);
        }
        mOperationQueue.put(newOperation);
        LOG.log(Level.FINEST, "changeColumns - component:{0}", newOperation);

      } catch (JSONException e) {
        LOG.log(Level.SEVERE, null, e);
      }
    }
  }

  /**
   * @param start: An array, that contains the number of that paragraph, before which the new
   *     paragraph shall be inserted. Has the last paragraph the number 2, so causes para=3, that
   *     the new paragraph will be inserted at the end. para=4 is not allowed in this case.
   */
  public void formatRows(
      final List<Integer> start,
      final Map<String, Object> formattingProperties,
      Integer firstRow,
      Integer lastRow,
      Integer previousRowRepeated,
      String context) {
    if (formattingProperties != null && !formattingProperties.isEmpty()) {
      final JSONObject newOperation = new JSONObject();

      try {
        newOperation.put(OPK_NAME, "changeRows");
        newOperation.put(OPK_SHEET, start.get(0));
        newOperation.put(OPK_START, firstRow + previousRowRepeated);
        if (context != null) {
          newOperation.put(OPK_CONTEXT, context);
        }
        if (lastRow != null && !firstRow.equals(lastRow)) {
          newOperation.put(OPK_END, lastRow + previousRowRepeated);
        }
        JSONObject attrs = new JSONObject();
        for (String arg : formattingProperties.keySet()) {
          attrs.put(arg, formattingProperties.get(arg));
        }
        newOperation.put(OPK_ATTRS, attrs);

        mOperationQueue.put(newOperation);
        LOG.log(Level.FINEST, "changeRows - component:{0}", newOperation);

      } catch (JSONException e) {
        LOG.log(Level.SEVERE, null, e);
      }
    }
  }

  private static final String NUMBER_FORMAT_CODE = "code";
  private static final String NUMBER_FORMAT_CODE_STANDARD = "Standard";

  /** Maps the ODF office:value-type attribute to the Changes API Text numberFormat property */
  private JSONObject getCellNumberFormat(String valueType) {
    JSONObject cellNumberFormat = new JSONObject();
    try {
      if (valueType == null || valueType.isEmpty()) {
        cellNumberFormat.put(NUMBER_FORMAT_CODE, NUMBER_FORMAT_CODE_STANDARD);
        //			} else if (valueType.equals("1")) {
        //			} else if (valueType.equals("i")) {
        //			} else if (valueType.equals("I")) {
        //			} else if (valueType.equals("a")) {
        //			} else if (valueType.equals("A")) {
      } else {
        cellNumberFormat.put(NUMBER_FORMAT_CODE, NUMBER_FORMAT_CODE_STANDARD);
      }
    } catch (JSONException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    return cellNumberFormat;
  }

  private void addRange(
      int sheet,
      Integer firstRow,
      Integer lastRow,
      int repeatedRowOffset,
      int firstContentCell,
      int horizontalRepetition,
      JSONObject repeatedCell,
      JSONArray singleRow,
      boolean hasHorizontalRepetition) {

    // the row start/end number 0 based, therefore - 1
    int rowStartNo = firstRow + repeatedRowOffset;
    List rangeStart = new LinkedList<Integer>();

    // if there is a repeated row, there will be repeated cells (at least vertical)
    if (hasHorizontalRepetition || lastRow != null && !firstRow.equals(lastRow)) {
      // first the start column position
      rangeStart.add(firstContentCell);
      // second the start row position
      rangeStart.add(rowStartNo);

      List rangeEnd = new LinkedList<Integer>();

      // first the end column position: StartPos of content plus any repetiton (including itself,
      // therefore - 1)
      rangeEnd.add(firstContentCell + horizontalRepetition - 1);
      // second the end row position
      int rowEndNo = lastRow + repeatedRowOffset;
      rangeEnd.add(rowEndNo);

      // create a operation for the given cell range with similar content
      fillCellRange(sheet, rangeStart, rangeEnd, repeatedCell);
    } else {
      // first the start column position
      rangeStart.add(firstContentCell);
      // second the start row position
      rangeStart.add(rowStartNo);

      setCellContents(sheet, rangeStart, singleRow);
    }
  }

  /**
   * Writes a range of the spreadsheet with various values. Will be called only indirectly after a
   * spreadsheet row has checked for optimization.
   *
   * @param sheet Integer The zero-based index of the sheet containing the cell range.
   * @param rangeStart Integer The starting position: where next row number is being written
   * @param spreadsheetRange Object[][] The values and attribute sets to be written into the cell
   *     range. The outer array contains rows of cell contents, and the inner row arrays contain the
   *     cell contents for each single row. The lengths of the inner arrays may be different. Cells
   *     not covered by a row array will not be modified.
   */
  private void setCellContents(Integer sheet, List rangeStart, JSONArray spreadsheetRange) {
    final JSONObject newOperation = new JSONObject();

    try {
      newOperation.put(OPK_NAME, "setCellContents");
      newOperation.put(OPK_SHEET, sheet);
      newOperation.put(OPK_START, rangeStart);
      // Although we only deliver a single row, the range have to be two-dimensional
      newOperation.put("contents", new JSONArray().put(spreadsheetRange));
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, "setCellContents - component:{0}", newOperation);
    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  /** */
  public void mergeCells(List<Integer> position, int columns, int rows) {
    final JSONObject newOperation = new JSONObject();
    LinkedList<Integer> rangeStart = new LinkedList<Integer>();
    rangeStart.add(position.get(2));
    rangeStart.add(position.get(1));
    LinkedList<Integer> rangeEnd = new LinkedList<Integer>();
    rangeEnd.add(position.get(2) + columns - 1);
    rangeEnd.add(position.get(1) + rows - 1);
    try {
      newOperation.put(OPK_NAME, "mergeCells");
      newOperation.put(OPK_SHEET, position.get(0));
      newOperation.put(OPK_START, rangeStart);
      newOperation.put(OPK_END, rangeEnd);
      newOperation.put(OPK_TYPE, "merge");
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, "mergeCells - component:{0}", newOperation);
    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  /**
   * Writes a range of the spreadsheet with various identical/repeating values and attributes
   *
   * <p>Will be called only indirectly after a spreadsheet row has checked for optimization.
   *
   * @param sheet Integer The zero-based index of the sheet containing the cell range.
   * @param start Integer[2] The logical cell position of the upper-left cell in the range.
   * @param end Integer[2] (optional) The logical cell position of the bottom-right cell in the
   *     range. If omitted, the operation addresses a single cell.
   * @param cell (optional) The value used to fill the specified cell range. The value null will
   *     clear the cell range. If omitted, the current values will not change (e.g., to change the
   *     formatting only), except for shared formulas referred by the shared attribute of this
   *     operation. If the parse property is set in the operation, the value must be a string.
   */
  private void fillCellRange(
      int sheet, final List<Integer> start, final List<Integer> end, JSONObject cell) {
    final JSONObject newOperation = new JSONObject();

    try {
      if (cell != null && cell.length() != 0) {
        newOperation.put(OPK_NAME, "fillCellRange");
        newOperation.put(OPK_SHEET, sheet);
        newOperation.put(OPK_START, incrementAll(start));
        if (end != null) {
          newOperation.put(OPK_END, incrementAll(end));
        }
        if (cell.has("value")) {
          newOperation.put("value", cell.get("value"));
        }
        if (cell.has(OPK_ATTRS)) {
          newOperation.put(OPK_ATTRS, cell.get(OPK_ATTRS));
        }
        mOperationQueue.put(newOperation);
        LOG.log(Level.FINEST, "fillCellRange - component:{0}", newOperation);
      }
    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  // -------------------------------------------------------------------------
  /**
   * @param text: text to be inserted into the specified paragraph at the specified position
   * @param start: an array that contains the position, where the new text shall be inserted.
   */
  public void addText(final List<Integer> start, final String text, final String context) {

    final JSONObject newOperation = new JSONObject();

    try {
      newOperation.put("text", text);

      newOperation.put(OPK_START, incrementAll(start));
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      newOperation.put(OPK_NAME, OP_TEXT);
      mOperationQueue.put(newOperation);

      LOG.log(Level.FINEST, newOperation.toString());
    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  // -------------------------------------------------------------------------
  void format(final List<Integer> start, Map<String, Object> attrs, String context) {
    JsonOperationProducer.this.format(start, null, attrs, context);
  }

  // -------------------------------------------------------------------------
  public void format(
      final List<Integer> start,
      final List<Integer> end,
      Map<String, Object> attrs,
      String context) {
    if (attrs != null && attrs.size() > 0) {
      // Not the next position, but the last character to be marked will be referenced
      final JSONObject newOp = new JSONObject();
      List<Integer> lastCharacterPos = new LinkedList<Integer>();
      // text position is usually -1 as we take the first and the last character to be styled
      if (end != null) {
        for (int i = 0; i < end.size(); i++) {
          Integer pos = end.get(i);
          if (i == end.size() - 1) {
            // Special case the span is empty, in this case it shall not be -1
            if (pos != 0) {
              pos--;
            }
          }
          lastCharacterPos.add(pos);
        }
      }

      try {
        newOp.put(OPK_NAME, OP_FORMAT);
        newOp.put(OPK_START, incrementAll(start));
        if (context != null) {
          newOp.put(OPK_CONTEXT, context);
        }
        boolean isValidOperation = true;
        if (end != null) {
          newOp.put(OPK_END, incrementAll(lastCharacterPos));
          if (start.get(start.size() - 1) > lastCharacterPos.get(start.size() - 1)) {
            isValidOperation = false;
            LOG.fine("Neglecting '" + newOp);
          }
        }
        newOp.put(OPK_ATTRS, attrs);
        if (isValidOperation) {
          mOperationQueue.put(newOp);
        }

        LOG.log(Level.FINEST, "New Operation '" + OP_FORMAT + "':" + newOp);

      } catch (JSONException e) {
        LOG.log(Level.SEVERE, null, e);
      }
    }
  }

  public void addImage(
      final List<Integer> start, Map<String, Object> hardFormatations, final String context) {

    final JSONObject newOperation = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_DRAWING);
      newOperation.put(OPK_TYPE, "image");
      newOperation.put(OPK_START, incrementAll(start));
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      if (hardFormatations != null) {
        newOperation.put(OPK_ATTRS, hardFormatations);
      }
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_DRAWING + " (image)" + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public void addShape(
      final List<Integer> start,
      Map<String, Object> hardFormatations,
      final String context,
      boolean isGroup) {

    final JSONObject newOperation = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_DRAWING);
      newOperation.put(OPK_TYPE, isGroup ? "group" : "shape");
      newOperation.put(OPK_START, incrementAll(start));
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      if (hardFormatations != null) {
        newOperation.put(OPK_ATTRS, hardFormatations);
      }
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_DRAWING + " (shape)" + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  /** Special Table color mJsonOperationProducer.addChild("Table", position, props, mTableColor); */
  // -------------------------------------------------------------------------
  /**
   * @param * @param start: An array, that contains the number of that paragraph, before which the
   *     new paragraph shall be inserted. Has the last paragraph the number 2, so causes para=3,
   *     that the new paragraph will be inserted at the end. para=4 is not allowed in this case.
   */
  public void addTable(
      final List<Integer> start,
      Map<String, Object> hardFormatations,
      final List<Integer> tableGrid,
      String tableName,
      final String context) {

    final JSONObject newOperation = new JSONObject();

    try {
      newOperation.put(OPK_NAME, OP_TABLE);
      newOperation.put(OPK_START, incrementAll(start));
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      JSONObject tableAttrs = null;
      if (hardFormatations != null && !hardFormatations.isEmpty()) {
        tableAttrs = (JSONObject) hardFormatations.get("table");
      } else {
        if (hardFormatations == null) {
          hardFormatations = new HashMap<String, Object>();
        }
      }
      if (tableAttrs == null) {
        tableAttrs = new JSONObject();
      }
      if (tableGrid != null) {
        tableAttrs.put("tableGrid", tableGrid);
      }
      hardFormatations.put("table", tableAttrs);
      newOperation.put(OPK_ATTRS, hardFormatations);
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_TABLE + " - component:{0}", newOperation);
    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  private void addTableExceededStyle() {
    try {
      JSONArray operations =
          new JSONArray(
              "[{\"styleName\":\"Exceeded Table Style\",\"styleId\":\"LightShading-Accent1\",\"attrs\":{\"firstRow\":{\"paragraph\":{\"marginBottom\":0,\"lineHeight\":{\"value\":100,\"type\":\"percent\"},\"marginTop\":0},\"cell\":{\"borderInsideVert\":{\"style\":\"none\"},\"borderTop\":{\"style\":\"single\",\"color\":{\"value\":\"accent1\",\"type\":\"scheme\"},\"width\":35},\"borderInsideHor\":{\"style\":\"none\"},\"borderBottom\":{\"style\":\"single\",\"color\":{\"value\":\"accent1\",\"type\":\"scheme\"},\"width\":35},\"borderRight\":{\"style\":\"none\"},\"borderLeft\":{\"style\":\"none\"}},\"character\":{\"bold\":true}},\"lastRow\":{\"paragraph\":{\"marginBottom\":0,\"lineHeight\":{\"value\":100,\"type\":\"percent\"},\"marginTop\":0},\"cell\":{\"borderInsideVert\":{\"style\":\"none\"},\"borderTop\":{\"style\":\"single\",\"color\":{\"value\":\"accent1\",\"type\":\"scheme\"},\"width\":35},\"borderInsideHor\":{\"style\":\"none\"},\"borderBottom\":{\"style\":\"single\",\"color\":{\"value\":\"accent1\",\"type\":\"scheme\"},\"width\":35},\"borderRight\":{\"style\":\"none\"},\"borderLeft\":{\"style\":\"none\"}},\"character\":{\"bold\":true}},\"band1Hor\":{\"cell\":{\"borderInsideVert\":{\"style\":\"none\"},\"fillColor\":{\"value\":\"accent1\",\"type\":\"scheme\",\"transformations\":[{\"value\":24706,\"type\":\"tint\"}]},\"borderInsideHor\":{\"style\":\"none\"},\"borderRight\":{\"style\":\"none\"},\"borderLeft\":{\"style\":\"none\"}}},\"lastCol\":{\"character\":{\"bold\":true}},\"wholeTable\":{\"paragraph\":{\"marginBottom\":0,\"lineHeight\":{\"value\":100,\"type\":\"percent\"}},\"table\":{\"paddingTop\":0,\"borderTop\":{\"style\":\"single\",\"color\":{\"value\":\"accent1\",\"type\":\"scheme\"},\"width\":35},\"borderBottom\":{\"style\":\"single\",\"color\":{\"value\":\"accent1\",\"type\":\"scheme\"},\"width\":35},\"paddingBottom\":0,\"paddingLeft\":190,\"paddingRight\":190},\"character\":{\"color\":{\"value\":\"accent1\",\"type\":\"scheme\",\"transformations\":[{\"value\":74902,\"type\":\"shade\"}]}}},\"band1Vert\":{\"cell\":{\"borderInsideVert\":{\"style\":\"none\"},\"fillColor\":{\"value\":\"accent1\",\"type\":\"scheme\",\"transformations\":[{\"value\":24706,\"type\":\"tint\"}]},\"borderInsideHor\":{\"style\":\"none\"},\"borderRight\":{\"style\":\"none\"},\"borderLeft\":{\"style\":\"none\"}}},\"firstCol\":{\"character\":{\"bold\":true}}},\"parent\":\"TableNormal\",\"uiPriority\":60,\"type\":\"table\",\"name\": \""
                  + OP_STYLE
                  + "\"}]");
      mOperationQueue.put(operations.get(0));

    } catch (JSONException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  public void addExceededTable(
      final List<Integer> start,
      int columns,
      int rows,
      final List<Integer> tableGrid,
      String context) {
    final JSONObject newOperation = new JSONObject();

    if (!mIsTableExceededStyleAdded) {
      // addChild once the table style
      addTableExceededStyle();
      mIsTableExceededStyleAdded = true;
    }

    try {
      newOperation.put(OPK_NAME, OP_TABLE);
      newOperation.put(OPK_START, incrementAll(start));
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      Map<String, Integer> sizeExceeded = new HashMap<String, Integer>();
      sizeExceeded.put("columns", columns);
      sizeExceeded.put("rows", rows);
      newOperation.put("sizeExceeded", sizeExceeded);
      Map<String, Object> hardFormatations = new HashMap<String, Object>();
      JSONObject tableAttrs = new JSONObject();
      //			JSONObject tableAttrs = null;
      //			if (hardFormatations != null && !hardFormatations.isEmpty()) {
      //				tableAttrs = (JSONObject) hardFormatations.get("table");
      //			} else {
      //				if (hardFormatations == null) {
      //					hardFormatations = new HashMap<String, Object>();
      //				}
      //			}
      //			if (tableAttrs == null) {
      //				tableAttrs = new JSONObject();
      //			}
      //			if (tableGrid != null) {
      //				tableAttrs.put("tableGrid", tableGrid);
      //			}
      tableAttrs.put("tableGrid", tableGrid);
      tableAttrs.put("style", "LightShading-Accent1");
      tableAttrs.put("width", "auto");
      List exclude = new ArrayList(3);
      exclude.add("lastRow");
      exclude.add("lastCol");
      exclude.add("bandsVert");
      tableAttrs.put("exclude", exclude);
      hardFormatations.put("table", tableAttrs);
      newOperation.put(OPK_ATTRS, hardFormatations);
      mOperationQueue.put(newOperation);

      LOG.log(Level.FINEST, OP_TABLE + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public void addField(
      final List<Integer> start,
      String fieldType,
      String fieldContent,
      final Map<String, Object> fieldAttributes,
      String context) {
    final JSONObject newOperation = new JSONObject();

    try {
      newOperation.put(OPK_NAME, OP_FIELD);
      newOperation.put(OPK_START, incrementAll(start));
      if (context != null) {
        newOperation.put(OPK_CONTEXT, context);
      }
      newOperation.put(OPK_TYPE, fieldType);
      if (fieldAttributes != null && !fieldAttributes.isEmpty()) {
        JSONObject attrs = new JSONObject();
        JSONObject fieldAttrs = new JSONObject();
        for (Map.Entry<String, Object> entry : fieldAttributes.entrySet()) {
          fieldAttrs.put(entry.getKey(), entry.getValue());
        }

        attrs.put("field", fieldAttrs);
        newOperation.put(OPK_ATTRS, attrs);
      }
      newOperation.put("representation", Objects.requireNonNullElse(fieldContent, ""));
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_FIELD + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  void addAutoFilterColumn(final List<Integer> start, int sheet, final List<String> entries) {
    final JSONObject newOperation = new JSONObject();

    try {
      newOperation.put(OPK_NAME, "changeTableColumn");
      newOperation.put("col", start.get(0));
      newOperation.put(OPK_SHEET, sheet);
      newOperation.put("table", "");
      JSONObject attrs = new JSONObject();
      JSONObject filterAttrs = new JSONObject();
      filterAttrs.put(OPK_TYPE, entries.isEmpty() ? "none" : "discrete");
      if (!entries.isEmpty()) {
        JSONArray entryArray = new JSONArray();
        for (String entry : entries) {
          entryArray.put(entry);
        }
        filterAttrs.put("entries", entryArray);
      }
      attrs.put("filter", filterAttrs);
      newOperation.put(OPK_ATTRS, attrs);
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, "changeTableColumn" + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  /**
   * * addStylesheet name: 'addStylesheet' type: "table" styleId: The identifier of this stylesheet
   * (unique for the corresponding type). stylename: The readable name of this style sheet. attrs:
   * JSONObject, contains formatting attributes as nested JSON objects, keyed by attribute family,
   * as well as other properties specific to a family. Must support the attributes of the main
   * attribute family, may support attributes of other families. See chapter "Style Sheet
   * Properties" below for a list of supported values. parent: (string, optional) The identifier of
   * the parent style sheet that derives formatting attributes to this style sheet (default: no
   * parent). hidden: (boolean, optional) Whether the style sheet is hidden in the user interface
   * (default: false). uipriority: (integer, optional) The priority of the style sheet used to order
   * style sheet in the user interface. The lower the value the higher the priority (default: 0).
   * default: (boolean, optional) Whether this style sheet is the default style sheet of the family.
   * Only one style sheet per family can be the default style sheet (default: false). pooldefault:
   * (boolean, optional) Whether this style sheet is the pool default style sheet. (default: false).
   * OOXML may have on the table default style properties for table Object { type: table properties
   * } '' row Object { type: row properties } '' cell Object { type: cell properties } '' paragraph
   * Object { type: paragraph properties } '' character Object { type: character properties } We
   * need to split this table style into an additional row and cell style and addChild it as parent
   * for those.
   */
  //
  public void addStyleSheet(
      String styleId,
      String familyID,
      String displayName,
      Map<String, Object> componentProps,
      String parentStyle,
      String nextStyleId,
      Integer outlineLevel,
      boolean isDefaultStyle,
      boolean isHidden,
      String custom) {
    // conditionalType: wholetable
    final JSONObject newOperation = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_STYLE);
      if (styleId != null && !styleId.isEmpty()) {
        newOperation.put(OPK_STYLE_ID, styleId);
      }
      newOperation.put(OPK_TYPE, familyID);
      if (displayName != null && !displayName.isEmpty()) {
        newOperation.put("styleName", displayName);
      }
      if (familyID.equals("table")) {
        final JSONObject tableStyleAttrs = new JSONObject();
        tableStyleAttrs.put("wholeTable", componentProps);
        newOperation.put(OPK_ATTRS, tableStyleAttrs);

      } else {
        newOperation.put(OPK_ATTRS, componentProps);
      }
      if (parentStyle != null && !parentStyle.isEmpty()) {
        newOperation.put("parent", parentStyle);
      }
      if (isDefaultStyle) {
        newOperation.put("default", isDefaultStyle);
      }
      if (isHidden) {
        newOperation.put("hidden", isHidden);
      }
      if (outlineLevel != null || nextStyleId != null) {
        JSONObject paraProps;
        if (componentProps.containsKey("paragraph")) {
          paraProps = (JSONObject) componentProps.get("paragraph");
        } else {
          paraProps = new JSONObject();
          componentProps.put("paragraph", paraProps);
        }
        if (outlineLevel != null) {
          paraProps.put("outlineLevel", outlineLevel);
        }
        if (nextStyleId != null && !nextStyleId.isEmpty()) {
          paraProps.put("nextStyleId", nextStyleId);
        }
        componentProps.put("paragraph", paraProps);
        newOperation.put(OPK_ATTRS, componentProps);
      }
      if (null != custom && Boolean.parseBoolean(custom)) {
        newOperation.put("custom", true);
      }
      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_STYLE + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public void addFontData(
      String fontName,
      String[] altNames,
      String family,
      String familyGeneric,
      String pitch,
      String panose1) {
    List<Integer> panose1_Integers = null;
    if (panose1 != null && !panose1.isEmpty()) {
      String[] result = null;
      if (panose1.contains("[")) {
        panose1 = panose1.substring(1, panose1.length() - 1);
      }

      if (panose1.contains(",")) {
        result = panose1.split(",");
      } else {
        result = panose1.split("\\s");
      }
      panose1_Integers = new LinkedList<Integer>();
      for (String token : result) {
        try {
          panose1_Integers.add(Integer.parseInt(token));

        } catch (NumberFormatException e) {
          LOG.log(Level.SEVERE, null, e);
        }
      }
    }
    addFontData(fontName, altNames, family, familyGeneric, pitch, panose1_Integers);
  }

  /**
   * Inserts an extended description for a specific font which an be used for font substitution
   * algorithms. name String 'addFontData'
   *
   * @param fontName The font name.
   * @param altNames String[] NOTE: Will be ignored in ODF. A list of alternate names for the font.
   * @param family String The font family.
   * @param pitch String The font pitch. One of 'fixed', or 'variable'.
   * @param panose1 Integer[10] The font typeface classification number. See
   *     http://en.wikipedia.org/wiki/PANOSE.
   */
  private void addFontData(
      String fontName,
      String[] altNames,
      String family,
      String familyGeneric,
      String pitch,
      List<Integer> panose1) {
    final JSONObject newOperation = new JSONObject();
    final JSONObject attrs = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_FONT_DECL);
      if (fontName != null && !fontName.isEmpty()) {
        newOperation.put("fontName", fontName);
      } else {
        LOG.fine("The font name is mandatory!");
      }
      newOperation.put(OPK_ATTRS, attrs);
      if (family != null && !family.isEmpty()) {
        attrs.put("family", family);
      }
      if (familyGeneric != null && !familyGeneric.isEmpty()) {
        attrs.put("familyGeneric", familyGeneric);
      }
      if (pitch != null && !pitch.isEmpty()) {
        attrs.put("pitch", pitch);
      }
      if (panose1 != null && !panose1.isEmpty()) {
        if (panose1.size() != 10) {
          LOG.fine("Panose1 is not 10 digits long: " + panose1);
        }
        attrs.put("panose1", panose1);
      }

      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_FONT_DECL + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  /**
   * @param componentProps is a JSONObject that has the family ID as key (e.g 'page') and the
   *     properties as JSON object as value
   */
  public void addDocumentData(JSONObject componentProps) {
    // conditionalType: wholetable
    try {
      mDocumentAttributes.put(OPK_ATTRS, componentProps);
      mDocumentAttributes.put(OPK_NAME, OP_DOCUMENT_LAYOUT);
      LOG.log(Level.FINEST, OP_DOCUMENT_LAYOUT + " - component:{0}", mDocumentAttributes);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  private String getComponentPath(List<Integer> pathIntegers) {
    StringBuilder path = new StringBuilder();
    for (Integer pathInteger : pathIntegers) {
      path.append(Constants.PATH_SEPARATOR);
      path.append(pathInteger);
    }
    return path.toString();
  }

  /**
   * ODF List Style inheritance will be resolved:
   *
   * <ol>
   *   <li>The paragraph/heading's list style reference from its closest text:list or text:list-item
   *       ancestor is taken. text:list uses the attribute
   *
   * @text:style-name and text:list-item the attribute
   * @text:style-override respectively.
   *     <li>If no list style was given by any text:list nor text:list-item ancestor the list-style
   *         reference of the paragraph style is being chosen, i.e. the
   * @style:list-style-name attribute within the paragraph style style:style element.
   *     </ol>
   */
  public static String getListStyle(
      ArrayDeque<ParagraphListProperties> listStyleStack, TextParagraphElementBase p) {
    String listStyleId = null;
    Iterator<ParagraphListProperties> listStyles = listStyleStack.descendingIterator();
    // Choose the first style being set
    while (listStyles.hasNext()) {
      ParagraphListProperties paraListStyle = listStyles.next();
      listStyleId = paraListStyle.getListStyleName();
      if (listStyleId != null && !listStyleId.isEmpty()) {
        break;
      }
    }
    // if no style was previous set, use the style on the paragraph
    if (listStyleId == null || listStyleId.isEmpty()) {
      OdfStyleBase style = null;
      if (p.hasAutomaticStyle()) {
        style = p.getAutomaticStyle();
      } else {
        style = p.getDocumentStyle();
      }
      if (style != null) {
        listStyleId = ((OdfStyle) style).getStyleListStyleNameAttribute();
      }
    }
    return listStyleId;
  }

  static Map<String, Object> getAutomaticStyleHierarchyProps(OdfStylableElement styleElement) {
    // Hard formatted properties (automatic styles)
    Map<String, Object> allHardFormatting = null;
    Map<String, Map<String, String>> allOdfProps = null;
    // AUTOMATIC STYLE HANDLING
    if (styleElement.hasAutomaticStyle()) {
      try {
        OdfStyleBase style = styleElement.getAutomaticStyle();

        // all ODF properties
        allOdfProps = new HashMap<String, Map<String, String>>();
        List<OdfStyleBase> parents = new LinkedList<OdfStyleBase>();
        parents.add(style);
        OdfStyleBase parent = style.getParentStyle();
        // if automatic style inheritance is possible
        while (parent != null) {
          Node n = parent.getParentNode();
          // if it is no longer an automatic style (template or default style)
          if (n instanceof OdfOfficeStyles) {
            break;
          }
          parents.add(parent);
          parent = parent.getParentStyle();
        }
        // due to inheritance the top ancestor style have to be propagated first
        boolean numberFormatInserted = false;
        OdfOfficeStyles officeStyles = null;
        OdfOfficeAutomaticStyles automaticStyles = null;
        OdfFileDom fileDom = (OdfFileDom) styleElement.getOwnerDocument();
        OdfDocument doc = (OdfDocument) fileDom.getDocument();
        officeStyles = doc.getStylesDom().getOfficeStyles();
        OdfStylesDom stylesDom = null;
        // get the automatic styles according to the current DOM
        if (fileDom instanceof OdfStylesDom) {
          stylesDom = (OdfStylesDom) fileDom;
          automaticStyles = stylesDom.getAutomaticStyles();
        } else {
          automaticStyles = doc.getContentDom().getAutomaticStyles();
        }
        for (int i = parents.size() - 1; i >= 0; i--) {
          OdfStyleBase styleBase = parents.get(i);
          MapHelper.getStyleProperties(styleBase, styleElement, allOdfProps);
          numberFormatInserted |=
              MapHelper.putNumberFormat(
                  null, allOdfProps, (OdfStyle) styleBase, automaticStyles, officeStyles);
        }
        allHardFormatting = MapHelper.mapStyleProperties(styleElement, allOdfProps);
        if (numberFormatInserted) {
          Map<String, String> cellProps = allOdfProps.get("cell");

          JSONObject jsonCellProps = null;
          if (allHardFormatting.containsKey("cell")) {
            jsonCellProps = (JSONObject) allHardFormatting.get("cell");
          } else {
            jsonCellProps = new JSONObject();
          }
          String formatCode = cellProps.get("numberformat_code");
          jsonCellProps.put("formatCode", formatCode);
          allHardFormatting.put("cell", jsonCellProps);
        }
      } catch (SAXException | IOException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    return allHardFormatting;
  }

  /**
   * Maps the styles of the given stylable ODF element to operations. In case the ODF element uses
   * automatic styles, all styles will be returned as property map. In case the ODF element uses
   * template styles, an operation for each style is being triggered, which not have been triggered
   * by now.
   *
   * @return the mapped automatic style grouped by property set
   */
  public Map<String, Object> getHardStyles(OdfStylableElement styleElement) {
    // Hard formatted properties (automatic styles)
    Map<String, Object> allHardFormatting = null;
    // AUTOMATIC STYLE HANDLING
    if (styleElement.hasAutomaticStyle()) {
      allHardFormatting = getAutomaticStyleHierarchyProps(styleElement);
    }
    return allHardFormatting;
  }

  /**
   * Creates the operation to insert a list style. All template list styles should be already set
   * during initialization.
   */
  public void addListStyle(
      OdfSchemaDocument doc, Map<String, TextListStyleElement> autoListStyles, String styleId) {
    if (styleId != null & !styleId.isEmpty()) {
      if (!knownListStyles.containsKey(styleId)) {
        try {
          // Three locations to check for the list style
          //  - 1 -  Automatic Styles of content.xml
          TextListStyleElement listStyle = autoListStyles.get(styleId);
          if (listStyle != null) {
            JsonOperationProducer.this.addListStyle(listStyle);
          } else {
            //  - 2 -  Automatic Styles of styles.xml
            OdfOfficeAutomaticStyles autoStyles = doc.getStylesDom().getAutomaticStyles();
            if (autoStyles != null) {
              listStyle = autoStyles.getListStyle(styleId);
              if (listStyle != null) {
                JsonOperationProducer.this.addListStyle(listStyle);
              }
            } else {
              //  - 3 -  Template Styles of styles.xml -- third as already checked when initialized
              OdfOfficeStyles templateStyles = doc.getStylesDom().getOfficeStyles();
              if (templateStyles != null) {
                listStyle = templateStyles.getListStyle(styleId);
                if (listStyle != null) {
                  JsonOperationProducer.this.addListStyle(listStyle);
                }
              }
            }
          }
        } catch (Exception ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  /**
   * After checking if the given list style was not declared before as operation call. A new list
   * style will be mapped to an operation.
   *
   * <p>The <text:list-style> element has the following attributes: style:display-name, style:name
   * and text:consecutive-numbering.
   */
  public void addListStyle(TextListStyleElement listStyle) {
    if (listStyle != null) {
      String styleId = listStyle.getStyleNameAttribute();
      if (!knownListStyles.containsKey(styleId)) {
        // addChild the given style to the known styles, so it will not provided again
        knownListStyles.put(styleId, Boolean.TRUE);
        addListStyle(
            listStyle.getStyleNameAttribute(),
            listStyle.getStyleDisplayNameAttribute(),
            listStyle.getTextConsecutiveNumberingAttribute(),
            getListLevelDefinitions(listStyle));
      }
    }
  }

  /** Receives the ten list definition */
  private JSONObject getListLevelDefinitions(TextListStyleElement listStyle) {
    JSONObject listDefinition = new JSONObject(9);
    NodeList listStyleChildren = listStyle.getChildNodes();
    int size = listStyleChildren.getLength();
    for (int i = 0; i < size; i++) {
      Node child = listStyleChildren.item(i);
      if (!(child instanceof Element)) {
        // avoid line breaks, when XML is indented
        continue;
      } else {
        TextListLevelStyleElementBase listLevelStyle = (TextListLevelStyleElementBase) child;
        // Transform mandatory attribute to integer

        String textLevel =
            listLevelStyle.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "level");
        int listLevel = Integer.parseInt(textLevel) - 1;
        try {
          listDefinition.put(
              "listLevel" + listLevel, createListLevelDefinition(listLevelStyle, listLevel));

        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
    return listDefinition;
  }

  /*
  justification		String	 One of 'left', 'right', or 'center'.
  numberFormat		String	 One of 'none', 'bullet', 'decimal', 'lowerRoman', 'upperRoman', 'lowerLetter', or 'upperLetter'.
  levelStart			Integer	 Start index of the level.
  indentLeft			Integer	 Left indent of the numbered paragraph.
  indentFirstLine		Integer	 First line indent, negative values represent hanging indents.
  fontName			String	 Font name, typically used for bullet symbols.
  levelText			String	 Formatting text of the label, can contain bullet symbol or level format like "%1."
  levelRestartValue	Integer	 The number level that resets the current level back to its starting value.
  OLD		paraStyle			String	 Identifier of the paragraph style that the current numbering level shall be applied to.
  NEW		textStyle
  NEW		HARD TEXT PROPERTIES
  levelPicBulletUri	String	 (optional) URI of the bullet picture.
  tabStopPosition		Integer	 Tabulator position, in 1/100 of millimeters.
  OLD		color				Color
  *
  * The <text:list-level-style-bullet> element has the following attributes: style:num-prefix 19.502, style:num-suffix 19.503, text:bullet-char 19.760, text:bullet-relative-size 19.761, text:level 19.828 and text:style-name 19.874.24
  * The <text:list-level-style-number> element has the following attributes: style:num-format 19.500, style:num-letter-sync 19.501, style:num-prefix 19.502, style:num-suffix 19.503, text:display-levels 19.797, text:level 19.828, text:start-value 19.868.4 and text:style-name 19.874.23.
  * The <text:list-level-style-image> element has the following attributes: text:level 19.828, xlink:actuate 19.909, xlink:href 19.910.35, xlink:show 19.911 and xlink:type 19.913.
  */
  private JSONObject createListLevelDefinition(
      TextListLevelStyleElementBase listLevelStyle, int listLevel) throws JSONException {
    JSONObject listLevelDefinition = new JSONObject();

    // NUMBERED LISTS
    if (listLevelStyle instanceof TextListLevelStyleNumberElement) {
      TextListLevelStyleNumberElement listLevelNumberStyle =
          (TextListLevelStyleNumberElement) listLevelStyle;
      listLevelDefinition.put("levelText", getLabel(listLevelNumberStyle, listLevel));
      if (listLevelStyle.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "start-value")) {
        String listStartValue = getListStartValue(listLevelNumberStyle);
        listLevelDefinition.put("listStartValue", Integer.parseInt(listStartValue));
      }
      // There is always the number format set
      listLevelDefinition.put("numberFormat", getNumberFormat(listLevelNumberStyle));

      // BULLET LISTS
    } else if (listLevelStyle instanceof TextListLevelStyleBulletElement) {
      TextListLevelStyleBulletElement listLevelBulletStyle =
          (TextListLevelStyleBulletElement) listLevelStyle;
      listLevelDefinition.put("levelText", getLabel(listLevelBulletStyle, listLevel));
      if (listLevelStyle.hasAttributeNS(
          OdfDocumentNamespace.TEXT.getUri(), "bullet-relative-size")) {
        listLevelDefinition.put(
            "bulletRelativeSize",
            listLevelStyle.getAttributeNS(
                OdfDocumentNamespace.TEXT.getUri(), "bullet-relative-size"));
      }
      listLevelDefinition.put("numberFormat", "bullet");

      // IMAGE LISTS
    } else if (listLevelStyle instanceof TextListLevelStyleImageElement) {
      listLevelDefinition.put(
          "levelPicBulletUri",
          listLevelStyle.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href"));
      listLevelDefinition.put("numberFormat", "bullet");
    }

    // ALL THREE TYPES: number, bullet and image list
    if (listLevelStyle.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name")) {
      listLevelDefinition.put(
          OPK_STYLE_ID,
          listLevelStyle.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name"));
    }

    /*
    The <style:list-level-properties> element has the following attributes:
    fo:height 20.187,
    fo:text-align 20.216.2,
    fo:width 20.222,
    style:font-name 20.269,
    style:vertical-pos 20.387,
    style:vertical-rel 20.388,
    svg:y 20.402.2,
    text:list-level-position-and-space-mode 20.421,
    text:min-label-distance 20.422,
    text:min-label-width 20.423 and
    text:space-before 20.425.
    */
    NodeList listLevelProps =
        listLevelStyle.getElementsByTagNameNS(
            OdfDocumentNamespace.STYLE.getUri(), "list-level-properties");
    if (listLevelProps != null) {
      StyleListLevelPropertiesElement styleListLevelProperties =
          (StyleListLevelPropertiesElement) listLevelProps.item(0);
      if (styleListLevelProperties != null) {
        //  fo:height
        if (styleListLevelProperties.hasAttributeNS(OdfDocumentNamespace.FO.getUri(), "height")) {
          String heightValue =
              styleListLevelProperties.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "height");
          if (heightValue != null) {
            int height = MapHelper.normalizeLength(heightValue);
            listLevelDefinition.put("height", height);
          }
        }

        // fo:text-align
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.FO.getUri(), "text-align")) {
          listLevelDefinition.put(
              "textAlign",
              MapHelper.mapFoTextAlign(
                  styleListLevelProperties.getAttributeNS(
                      OdfDocumentNamespace.FO.getUri(), "text-align")));
        }

        //  fo:width
        if (styleListLevelProperties.hasAttributeNS(OdfDocumentNamespace.FO.getUri(), "width")) {
          String widthValue =
              styleListLevelProperties.getAttributeNS(OdfDocumentNamespace.FO.getUri(), "width");
          if (widthValue != null) {
            int width = MapHelper.normalizeLength(widthValue);
            listLevelDefinition.put("width", width);
          }
        }

        // style:font-name
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "font-name")) {
          listLevelDefinition.put(
              "fontName",
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "font-name"));
        }

        // style:vertical-pos
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "vertical-pos")) {
          listLevelDefinition.put(
              "verticalPos",
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "vertical-pos"));
        }

        // style:vertical-rel
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "vertical-rel")) {
          listLevelDefinition.put(
              "verticalRel",
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "vertical-rel"));
        }

        // svg:y
        if (styleListLevelProperties.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y")) {
          listLevelDefinition.put(
              "y", styleListLevelProperties.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y"));
        }

        // text:list-level-position-and-space-mode
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.TEXT.getUri(), "list-level-position-and-space-mode")) {
          listLevelDefinition.put(
              "listLevelPositionAndSpaceMode",
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.TEXT.getUri(), "list-level-position-and-space-mode"));
        }

        // text:min-label-distance
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.TEXT.getUri(), "min-label-distance")) {
          String minLabelDistanceValue =
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.TEXT.getUri(), "min-label-distance");
          if (minLabelDistanceValue != null && !minLabelDistanceValue.isEmpty()) {
            int minLabelDistance = MapHelper.normalizeLength(minLabelDistanceValue);
            listLevelDefinition.put("minLabelDistance", minLabelDistance);
          }
        }

        // text:min-label-width
        String minLabelWidthValue = null;
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.TEXT.getUri(), "min-label-width")) {
          minLabelWidthValue =
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.TEXT.getUri(), "min-label-width");
          if (minLabelWidthValue != null && !minLabelWidthValue.isEmpty()) {
            int width = MapHelper.normalizeLength(minLabelWidthValue);
            listLevelDefinition.put("minLabelWidth", width);
          }
        }

        // text:space-before
        String spaceBeforeValue = null;
        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.TEXT.getUri(), "space-before")) {
          spaceBeforeValue =
              styleListLevelProperties.getAttributeNS(
                  OdfDocumentNamespace.TEXT.getUri(), "space-before");
          if (spaceBeforeValue != null && !spaceBeforeValue.isEmpty()) {
            int spaceBefore = MapHelper.normalizeLength(spaceBeforeValue);
            listLevelDefinition.put("spaceBefore", spaceBefore);
          }
        }

        // Mapping list XML ODF 1.1 to ODF 1.2: Adding @text:min-label-width & @text:space-before to
        // margin-left
        listLevelDefinition = mapIndent(minLabelWidthValue, spaceBeforeValue, listLevelDefinition);

        if (styleListLevelProperties.hasAttributeNS(
            OdfDocumentNamespace.TEXT.getUri(), "list-level-position-and-space-mode")) {
          if ("label-alignment"
              .equals(
                  styleListLevelProperties.getAttributeNS(
                      OdfDocumentNamespace.TEXT.getUri(), "list-level-position-and-space-mode"))) {
            NodeList nl =
                styleListLevelProperties.getElementsByTagNameNS(
                    OdfDocumentNamespace.STYLE.getUri(), "list-level-label-alignment");
            if (nl != null && nl.getLength() == 1) {
              StyleListLevelLabelAlignmentElement labelAlignmentElement =
                  (StyleListLevelLabelAlignmentElement) nl.item(0);
              String marginLeft =
                  labelAlignmentElement.getAttributeNS(
                      OdfDocumentNamespace.FO.getUri(), "margin-left");
              int margin = 0;
              if (marginLeft != null && !marginLeft.isEmpty()) {
                margin = MapHelper.normalizeLength(marginLeft);
                listLevelDefinition.put("indentLeft", margin);
              } else {
                listLevelDefinition =
                    mapIndent(minLabelWidthValue, spaceBeforeValue, listLevelDefinition);
              }
              String textIndent =
                  labelAlignmentElement.getAttributeNS(
                      OdfDocumentNamespace.FO.getUri(), "text-indent");
              if (textIndent != null && !textIndent.isEmpty()) {
                int indent = MapHelper.normalizeLength(textIndent);
                listLevelDefinition.put("indentFirstLine", indent);
              }

              //			<optional>
              //				<attribute name="text:list-tab-stop-position">
              //					<ref name="length"/>
              //				</attribute>
              //			</optional>
              if (labelAlignmentElement.hasAttributeNS(
                  OdfDocumentNamespace.TEXT.getUri(), "list-tab-stop-position")) {
                String tabPosition =
                    labelAlignmentElement.getAttributeNS(
                        OdfDocumentNamespace.TEXT.getUri(), "list-tab-stop-position");
                if (tabPosition != null && !tabPosition.isEmpty()) {
                  //									if(marginLeft != null && !marginLeft.isEmpty()){
                  //										listLevelDefinition.put("tabStopPosition",
                  // MapHelper.normalizeLength(tabPosition) + margin);
                  //									}else{
                  listLevelDefinition.put(
                      "tabStopPosition", MapHelper.normalizeLength(tabPosition));
                  //									}
                }
              }

              //			<attribute name="text:label-followed-by">
              //				<choice>
              //					<value>listtab</value>
              //					<value>space</value>
              //					<value>nothing</value>
              //				</choice>
              //			</attribute>
              if (labelAlignmentElement.hasAttributeNS(
                  OdfDocumentNamespace.TEXT.getUri(), "label-followed-by")) {
                listLevelDefinition.put(
                    "labelFollowedBy",
                    labelAlignmentElement.getAttributeNS(
                        OdfDocumentNamespace.TEXT.getUri(), "label-followed-by"));
              }
            }
          }
        }
      }
    }
    return listLevelDefinition;
  }

  private static JSONObject mapIndent(
      String minLabelWidthValue, String spaceBeforeValue, JSONObject listLevelDefinition)
      throws JSONException {
    int minLabelWidth = 0;
    boolean isValidMinLabelWidth = Length.isValid(minLabelWidthValue);
    if (isValidMinLabelWidth) {
      minLabelWidth = MapHelper.normalizeLength(minLabelWidthValue);
    }
    int spaceBefore = 0;
    boolean isValidSpaceBefore = Length.isValid(spaceBeforeValue);
    if (isValidSpaceBefore) {
      spaceBefore = MapHelper.normalizeLength(spaceBeforeValue);
    }
    if (isValidMinLabelWidth || isValidSpaceBefore) {
      listLevelDefinition.put("indentLeft", minLabelWidth + spaceBefore);
    }
    return listLevelDefinition;
  }

  /**
   * Handling the attributes of
   *
   * @style:num-format
   *     <p>The style:num-format attribute specifies a numbering sequence. The defined ODF values
   *     for the style:num-format attribute are:
   *     <ul>
   *       <li>1: Hindu-Arabic number sequence starts with 1.
   *       <li>a: number sequence of lowercase Modern Latin basic alphabet characters starts with
   *           "a".
   *       <li>A: number sequence of uppercase Modern Latin basic alphabet characters starts with
   *           "A".
   *       <li>i: number sequence of lowercase Roman numerals starts with "i".
   *       <li>I: number sequence of uppercase Roman numerals start with "I".
   *       <li>a value of type string 18.2. (COMPLEX NUMBERING ie. ASIAN oder ERSTENS..)
   *       <li>an empty string: no number sequence displayed.
   *       <li>If no value is given, no number sequence is displayed.
   *     </ul>
   *     Our API: One of 'none', 'bullet', 'decimal', 'lowerRoman', 'upperRoman', 'lowerLetter', or
   *     'upperLetter'.
   */
  private String getNumberFormat(TextListLevelStyleElementBase listLevelStyle) {
    String numberFormat =
        listLevelStyle.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "num-format");
    String numFormat;
    if (numberFormat == null || numberFormat.isEmpty()) {
      numFormat = "none";
    } else if (numberFormat.equals("1")) {
      numFormat = "decimal";
    } else if (numberFormat.equals("i")) {
      numFormat = "lowerRoman";
    } else if (numberFormat.equals("I")) {
      numFormat = "upperRoman";
    } else if (numberFormat.equals("a")) {
      numFormat = "lowerLetter";
    } else if (numberFormat.equals("A")) {
      numFormat = "upperLetter";
    } else {
      // a value of type string 18.2. (COMPLEX NUMBERING ie. ASIAN oder ERSTENS..)
      numFormat = numberFormat;
    }
    return numFormat;
  }

  /**
   * Handling the attributes of
   *
   * @text:start-value
   */
  private String getListStartValue(TextListLevelStyleElementBase listLevelStyle) {
    return listLevelStyle.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "start-value");
  }

  /**
   * Handling the attributes of
   *
   * @style:num-prefix
   * @text:display-levels
   * @style:num-suffix
   */
  private String getLabel(TextListLevelStyleElementBase listLevelStyle, int listLevel) {
    StringBuilder levelText = new StringBuilder();

    // creating label prefix
    String labelPrefix =
        listLevelStyle.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "num-prefix");
    if (labelPrefix != null && !labelPrefix.isEmpty()) {
      levelText.append(labelPrefix);
    }

    // creating label number
    if (listLevelStyle instanceof TextListLevelStyleNumberElement) {
      String displayLevels =
          listLevelStyle.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "display-levels");
      if (displayLevels != null && !displayLevels.isEmpty()) {
        int showLevels = Integer.parseInt(displayLevels);
        // Creating the label, in ODF always adding the low levelText first, adding each follow up
        // level for display level
        // Custom string with one of the placeholders from '%1' to '%9') for numbered lists.
        for (int i = showLevels; i > 0; i--) {
          levelText.append("%").append(listLevel + 2 - i);
          // Although not commented in the specification a "." is being added to the text level
          if (i != 1) {
            levelText.append('.');
          }
        }
      } else {
        levelText.append("%").append(listLevel + 1);
      }
      // creating label suffix
      String labelSuffix =
          listLevelStyle.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "num-suffix");
      if (labelSuffix != null && !labelSuffix.isEmpty()) {
        levelText.append(labelSuffix);
      }
    } else {
      String bulletChar =
          listLevelStyle.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "bullet-char");
      if (bulletChar != null && !bulletChar.isEmpty()) {
        levelText.append(bulletChar);
      }
    }
    return levelText.toString();
  }
  // addListStyle(knownListStyles.size(), listStyle.getStyleNameAttribute(),
  // listStyle.getStyleDisplayNameAttribute(), listStyle.getTextConsecutiveNumberingAttribute(),
  // getListLevelDefinitions(listStyle));

  private void addListStyle(
      String styleName,
      String displayName,
      boolean hasConsecutiveNumbering,
      JSONObject listDefinition) {
    // conditionalType: wholetable
    final JSONObject newOperation = new JSONObject();
    try {
      newOperation.put(OPK_NAME, OP_LIST_STYLE);
      newOperation.put("listStyleId", styleName);
      //			newOperation.put("displayName", displayName);
      if (hasConsecutiveNumbering) {
        newOperation.put("listUnifiedNumbering", hasConsecutiveNumbering);
      }
      newOperation.put("listDefinition", listDefinition);

      mOperationQueue.put(newOperation);
      LOG.log(Level.FINEST, OP_LIST_STYLE + " - component:{0}", newOperation);

    } catch (JSONException e) {
      LOG.log(Level.SEVERE, null, e);
    }
  }

  public Integer triggerStyleHierarchyOps(
      OdfOfficeStyles officeStyles, OdfStyleFamily styleFamily, OdfStyleBase style) {
    Integer defaultTabStopWidth = null;
    if (style != null) {

      if (!(style instanceof OdfDefaultStyle)) {

        if (!knownStyles.containsKey(((OdfStyle) style).getStyleNameAttribute())) {
          List<OdfStyleBase> parents = new LinkedList<OdfStyleBase>();
          OdfStyleBase parent = style;

          // Collecting hierachy, to go back through the style hierarchy from the end, to be able to
          // neglect empty styles and adjust parent style attribute
          while (parent != null
              && (parent instanceof OdfDefaultStyle
                  || !knownStyles.containsKey(((OdfStyle) parent).getStyleNameAttribute()))) {
            if (parent instanceof OdfDefaultStyle) {
              // Default styles will receive a name and will be referenced by the root style (the
              // default style for the web editor)
              if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
                defaultTabStopWidth =
                    triggerDefaultStyleOp(styleFamily, officeStyles.getDefaultStyle(styleFamily));
              } else {
                triggerDefaultStyleOp(styleFamily, officeStyles.getDefaultStyle(styleFamily));
              }
              // NEXT: there is no style above a default in the style hierarchy
              break;
            } else if (parent != null) {
              parents.add(parent);

              // NEXT: get the next parent style and if the style parent name is the ODFTK_DEFAULT
              // NAME remove it
              Attr parentStyleName =
                  parent.getAttributeNodeNS(
                      OdfDocumentNamespace.STYLE.getUri(), "parent-style-name");
              if (parentStyleName != null
                  && parentStyleName
                      .getValue()
                      .equals(
                          Constants.ODFTK_DEFAULT_STYLE_PREFIX
                              + Component.getFamilyID(styleFamily)
                              + Constants.ODFTK_DEFAULT_STYLE_SUFFIX)) {
                parent.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "parent-style-name");
                triggerDefaultStyleOp(styleFamily, style.getParentStyle());
                break;
              } else {
                parent = parent.getParentStyle();
              }
            }

            // trigger operation only for those style not already existing
            // check if the named style already exists
          }

          String lastWrittenStyleName = null; // Only write out parents with mapped styles
          boolean skippedEmptyParent = false;
          // Intermediate ODF properties
          Map<String, Map<String, String>> allOdfProps = new HashMap<String, Map<String, String>>();
          // The property groups for this component, e.g. cell, paragraph, text for a cell with
          // properties
          Map<String, OdfStylePropertiesSet> familyPropertyGroups =
              Component.getAllStyleGroupingIdProperties(styleFamily);
          // Mapped properties
          Map<String, Object> mappedFormatting = null;

          // addChild named style to operation
          String styleName;

          // due to inheritance the top ancestor style have to be propagated first
          for (int i = parents.size() - 1; i >= 0; i--) {
            style = parents.get(i);

            // get all ODF properties from this style
            MapHelper.getStyleProperties(style, familyPropertyGroups, allOdfProps);
            // mapping the ODF attribute style props to our component properties
            mappedFormatting = MapHelper.mapStyleProperties(familyPropertyGroups, allOdfProps);
            MapHelper.putNumberFormat(mappedFormatting, null, (OdfStyle) style, null, officeStyles);

            styleName = ((OdfStyle) style).getStyleNameAttribute();
            // No OdfStyle, as the parent still might be a default style without name
            OdfStyleBase parentStyle = style.getParentStyle();
            String parentStyleName = null;

            // Default styles do not have a name
            if (parentStyle != null && !(parentStyle instanceof OdfDefaultStyle)) {
              parentStyleName = ((OdfStyle) parentStyle).getStyleNameAttribute();
            }
            String nextStyle = ((OdfStyle) style).getStyleNextStyleNameAttribute();
            Integer outlineLevel = ((OdfStyle) style).getStyleDefaultOutlineLevelAttribute();
            // Do not trigger operations to create empty styles
            if (skippedEmptyParent) {
              parentStyleName = lastWrittenStyleName;
            }

            String custom = style.getAttribute("custom");

            String familyId = Component.getFamilyID(styleFamily);
            if (parentStyleName != null && !parentStyleName.isEmpty()) {
              addStyleSheet(
                  styleName,
                  familyId,
                  ((OdfStyle) style).getStyleDisplayNameAttribute(),
                  mappedFormatting,
                  parentStyleName,
                  nextStyle,
                  outlineLevel,
                  false,
                  false,
                  custom);
            } else {
              // the template style without parent is a root style and being used as a default style
              // in the editor
              // addStyleSheet(styleName, familyId, ((OdfStyle)
              // style).getStyleDisplayNameAttribute(), mappedFormatting, ODFTK_DEFAULT_STYLE_PREFIX
              // + familyId + ODFTK_DEFAULT_STYLE_SUFFIX, nextStyle, outlineLevel, true, false);
              addStyleSheet(
                  styleName,
                  familyId,
                  ((OdfStyle) style).getStyleDisplayNameAttribute(),
                  mappedFormatting,
                  Constants.ODFTK_DEFAULT_STYLE_PREFIX
                      + familyId
                      + Constants.ODFTK_DEFAULT_STYLE_SUFFIX,
                  nextStyle,
                  outlineLevel,
                  false,
                  false,
                  custom);
            }

            lastWrittenStyleName = styleName;
            mappedFormatting.clear();
            allOdfProps.clear();
            // addChild named style to known styles, so it will be only executed once
            knownStyles.put(styleName, Boolean.TRUE);
          }
        }
      } else {
        // DEFAULT STYLE PARENT
        // Default styles will receive a name and will be referenced by the root style (the default
        // style for the web editor)
        if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
          defaultTabStopWidth = triggerDefaultStyleOp(styleFamily, style);
        } else {
          triggerDefaultStyleOp(styleFamily, style);
        }
      }
    }
    return defaultTabStopWidth;
  }

  /**
   * Tests first if the default style was already added to the document, than triggers a
   * addStylesheet operation
   */
  public Integer triggerDefaultStyleOp(OdfStyleFamily styleFamily, OdfStyleBase style) {
    Integer defaultTabStopWidth = null;
    // Intermediate ODF properties
    Map<String, Map<String, String>> allOdfProps = new HashMap<String, Map<String, String>>();
    // The property groups for this component, e.g. cell, paragraph, text for a cell with properties
    Map<String, OdfStylePropertiesSet> familyPropertyGroups =
        Component.getAllStyleGroupingIdProperties(styleFamily);
    // Mapped properties
    Map<String, Object> mappedFormatting = null;

    // addChild named style to operation
    String styleName;

    if (style instanceof OdfDefaultStyle
        && !knownStyles.containsKey(
            Constants.ODFTK_DEFAULT_STYLE_PREFIX
                + Component.getFamilyID(styleFamily)
                + Constants.ODFTK_DEFAULT_STYLE_SUFFIX)) {
      // get all ODF properties from this style
      MapHelper.getStyleProperties(style, familyPropertyGroups, allOdfProps);
      // mapping the ODF attribute style props to our component properties
      mappedFormatting = MapHelper.mapStyleProperties(familyPropertyGroups, allOdfProps);
      // Tabulator default size is an attribute in the default style, will be received from static
      // mapping functions
      if (mappedFormatting.containsKey("paragraph")) {
        JSONObject paraProps = (JSONObject) mappedFormatting.get("paragraph");
        if (paraProps.has("document")) {
          JSONObject documentProps = paraProps.optJSONObject("document");
          defaultTabStopWidth = documentProps.optInt("defaultTabStop");
        }
      }
      String familyId = Component.getFamilyID(styleFamily);
      // Do not trigger operations to create empty styles
      if (!mappedFormatting.isEmpty()) {
        String displayName = "Default " + Component.getFamilyDisplayName(styleFamily) + " Style";
        addStyleSheet(
            Constants.ODFTK_DEFAULT_STYLE_PREFIX + familyId + Constants.ODFTK_DEFAULT_STYLE_SUFFIX,
            familyId,
            displayName,
            mappedFormatting,
            null,
            null,
            null,
            true,
            true,
            null);
      }
      // addChild named style to known styles, so it will be only executed once
      styleName =
          Constants.ODFTK_DEFAULT_STYLE_PREFIX
              + Component.getFamilyID(styleFamily)
              + Constants.ODFTK_DEFAULT_STYLE_SUFFIX;
      knownStyles.put(styleName, Boolean.TRUE);
    }
    return defaultTabStopWidth;
  }

  /** trigger pageStyle operations, only the "standard" page style will be returned */
  public JSONObject addPageProperties(OdfStylesDom stylesDom) {
    JSONObject defaultPageProperties = null;
    // Do NOT create the master styles with getOrCreateMasterStyles()
    // as we might be at the prior office:styles element and office:master-styles would be
    // duplicated
    OfficeMasterStylesElement masterStyles = stylesDom.getMasterStyles();
    if (masterStyles != null) {
      for (StyleMasterPageElement masterPage : masterStyles.getMasterPages().values()) {
        String styleName = masterPage.getStyleNameAttribute();
        /**
         * <style:page-layout style:name="Mpm1"> <style:page-layout-properties
         * fo:margin-bottom="20mm" fo:margin-left="20mm" fo:margin-right="20mm" fo:margin-top="20mm"
         * fo:page-height="297mm" fo:page-width="210.01mm" style:footnote-max-height="0mm"
         * style:num-format="1" style:print-orientation="portrait" style:writing-mode="lr-tb">
         * <style:footnote-sep style:adjustment="left" style:color="#000000"
         * style:distance-after-sep="1.01mm" style:distance-before-sep="1.01mm"
         * style:line-style="solid" style:rel-width="25%" style:width="0.18mm"/>
         * </style:page-layout-properties> <style:header-style> <style:header-footer-properties
         * fo:margin-bottom="4.99mm" fo:margin-left="0mm" fo:margin-right="0mm"
         * fo:min-height="9.98mm" style:dynamic-spacing="false"/> </style:header-style>
         * <style:footer-style> <style:header-footer-properties fo:margin-left="0mm"
         * fo:margin-right="0mm" fo:margin-top="14.99mm" style:dynamic-spacing="false"
         * svg:height="24.99mm"/> </style:footer-style> </style:page-layout>
         */
        if (styleName != null && !styleName.isEmpty()) {
          String pageLayoutName = masterPage.getStylePageLayoutNameAttribute();
          JSONObject pagePropsJson = null;
          if (pageLayoutName != null && !pageLayoutName.isEmpty()) {
            OdfOfficeAutomaticStyles autoStyles = stylesDom.getAutomaticStyles();
            if (autoStyles != null) {
              OdfStylePageLayout pageLayout = autoStyles.getPageLayout(pageLayoutName);
              Map<OdfStyleProperty, String> pageProperties = pageLayout.getStyleProperties();
              Map<String, String> pageProps = transformMap(pageProperties);
              pagePropsJson = MapHelper.mapProperties("page", pageProps);
              try {
                pagePropsJson = pagePropsJson.getJSONObject("page");
              } catch (JSONException e) {
                // no need for handline
              }
              /**
               * SVANTEWHY NO LONGER REQUIRED NodeList pageLayoutChildren =
               * pageLayout.getChildNodes(); if (pageLayoutChildren.getLength() > 1) { for (int i =
               * 0; i < pageLayoutChildren.getLength(); i++) { Node child =
               * pageLayoutChildren.item(i); boolean isHeaderAttribute = child instanceof
               * StyleHeaderStyleElement; if (isHeaderAttribute || child instanceof
               * StyleFooterStyleElement) { StyleHeaderFooterPropertiesElement props =
               * (StyleHeaderFooterPropertiesElement) ((OdfElement)
               * child).getChildElement(StyleHeaderFooterPropertiesElement.ELEMENT_NAME.getUri(),
               * StyleHeaderFooterPropertiesElement.ELEMENT_NAME.getLocalName()); if (props != null)
               * { if(isHeaderAttribute) { String marginHeader = props.getFoMarginBottomAttribute();
               * String minHeightHeader = props.getFoMinHeightAttribute(); String fixedHeightHeader
               * = props.getSvgHeightAttribute(); if(fixedHeightHeader != null) minHeightHeader =
               * fixedHeightHeader; if (marginHeader != null && !marginHeader.isEmpty()) { if
               * (pagePropsJson == null) { pagePropsJson = new JSONObject(); } try { int
               * minHeightNormalized = 0; if (minHeightHeader != null && !minHeightHeader.isEmpty())
               * { minHeightNormalized = MapHelper.normalizeLength(minHeightHeader); }
               * pagePropsJson.put("marginHeader", MapHelper.normalizeLength(marginHeader) +
               * minHeightNormalized); } catch (JSONException ex) {
               * LOG.log(Level.SEVERE, null,
               * ex); } } } else { String marginFooter = props.getFoMarginTopAttribute(); String
               * fixedHeightFooter = props.getSvgHeightAttribute(); String minHeightFooter =
               * props.getFoMinHeightAttribute(); if( fixedHeightFooter != null ) { minHeightFooter
               * = fixedHeightFooter; } if (marginFooter != null && !marginFooter.isEmpty()) { if
               * (pagePropsJson == null) { pagePropsJson = new JSONObject(); } try { int
               * minHeightNormalized = 0; if (minHeightFooter != null && !minHeightFooter.isEmpty())
               * { minHeightNormalized = MapHelper.normalizeLength(minHeightFooter); }
               * pagePropsJson.put("marginFooter", MapHelper.normalizeLength(marginFooter) +
               * minHeightNormalized); } catch (JSONException ex) {
               * LOG.log(Level.SEVERE, null,
               * ex); } } } } } } } SVANTE REQUIRED
               */
              if (pagePropsJson != null && pagePropsJson.length() != 0) {
                if (styleName.equals("Standard") || styleName.equals("MP0")) {
                  defaultPageProperties = pagePropsJson;
                  /**
                   * SVANTE NO LONGER REQUIRED try { //make values fit for the frontend
                   * if(pagePropsJson.has("marginHeader")) { int headerMargin =
                   * pagePropsJson.getInt("marginHeader"); int topMargin =
                   * pagePropsJson.getInt("marginTop"); pagePropsJson.put("marginTop", headerMargin
                   * + topMargin); pagePropsJson.put("marginHeader", topMargin); }
                   * if(pagePropsJson.has("marginFooter")) { int footerMargin =
                   * pagePropsJson.getInt("marginFooter"); int bottomMargin =
                   * pagePropsJson.getInt("marginBottom"); pagePropsJson.put("marginBottom",
                   * footerMargin + bottomMargin); pagePropsJson.put("marginFooter", bottomMargin);
                   * } } catch (JSONException e) { } break; } else { //
                   * documentPropsObject.put("page", pagePropsJson); SVANTE REQUIRED
                   */
                }
              }
            }
          }
        }
      }
    }
    return defaultPageProperties;
  }

  public void addDocumentProperties(
      OdfStylesDom stylesDom, Integer defaultTabStopWidth, JSONObject defaultPageStyles) {
    try {
      JSONObject documentPropsObject = new JSONObject();
      JSONObject docPropsJson = new JSONObject();
      if (defaultTabStopWidth != null) {
        docPropsJson.putOpt("defaultTabStop", defaultTabStopWidth);
      }
      if (defaultPageStyles != null) {
        documentPropsObject.put("page", defaultPageStyles);
      }
      docPropsJson.putOpt("fileFormat", "odf");
      documentPropsObject.put("document", docPropsJson);
      addDocumentData(documentPropsObject);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  // ToDo: Wird noch immer vererbt?!??
  // Ersteinmal wird alles \u00fcberschrieben und vererbt
  // \u00dcberschreiben kann aber auch die gemappten werte, dann w\u00fcrde man ggf. mehrmals
  // umsonst mappen..
  private Map<String, String> transformMap(Map<OdfStyleProperty, String> props) {
    Map<String, String> odfProps = new HashMap<String, String>();
    for (OdfStyleProperty styleProp : props.keySet()) {
      odfProps.put(styleProp.getName().getQName(), props.get(styleProp));
    }
    return odfProps;
  }

  /** Adding a default footer style operation */
  public void addHeaderFooter(String contextName, PageArea pageArea, JSONObject attrs) {
    if (contextName != null) {
      final JSONObject newOperation = new JSONObject(4);
      try {
        newOperation.put(OPK_NAME, OP_HEADER_FOOTER);
        newOperation.put(OPK_ID, contextName);
        newOperation.put(OPK_TYPE, pageArea.getPageAreaName());
        if (attrs != null && attrs.length() != 0) {
          newOperation.put(OPK_ATTRS, attrs);
        }
        mOperationQueue.put(newOperation);
        LOG.log(Level.FINEST, OP_HEADER_FOOTER + " component:{0}", newOperation);
      } catch (JSONException e) {
        LOG.log(Level.SEVERE, null, e);
      }
    }
  }

  /**
   * LO/AO does not interpret clipping as in FO/CSS
   * http://www.w3.org/TR/CSS2/visufx.html#propdef-clip Instead the clip values measure the distance
   * from each border to the start of the viewed area. The clip vales are taking measure from the
   * original size, which is not part of the OFD XML, therefore the image have to be loaded for
   * receiving the size.
   */
  public static void calculateCrops(OdfElement image, String href, JSONObject imageProps) {
    try {
      // ToDo: Although the streams are cached we might cache the clipping for known href, help if
      // images occure more than once
      OdfPackage pkg = ((OdfFileDom) image.getOwnerDocument()).getDocument().getPackage();

      InputStream is = pkg.getInputStream(href);
      if (is != null) {
        BufferedImage bimg = ImageIO.read(is);
        if (bimg != null) {
          double width =
              MapHelper.normalizeLength((bimg.getWidth() / Constants.DOTS_PER_INCH) + "in");
          double height =
              MapHelper.normalizeLength((bimg.getHeight() / Constants.DOTS_PER_INCH) + "in");
          try {
            // 2nd half of absolute fo:clip to relative crop (Changes API) mapping
            if (imageProps.has("cropRight")) {
              Number cropRight = (Number) imageProps.get("cropRight");
              LOG.log(Level.FINEST, "The clipRight is {0}", cropRight);
              if (cropRight != null) {
                if (cropRight.doubleValue() != 0.0) {
                  imageProps.put("cropRight", cropRight.doubleValue() * 100.0 / width);
                  LOG.log(
                      Level.FINEST,
                      "The cropRight is {0}",
                      cropRight.doubleValue() * 100.0 / width);
                } else {
                  // do not set explicitly with 0
                  imageProps.remove("cropRight");
                }
              }
            }
            if (imageProps.has("cropLeft")) {
              Number cropLeft = (Number) imageProps.get("cropLeft");
              LOG.log(Level.FINEST, "The clipLeft is {0}", cropLeft);
              if (cropLeft != null) {
                if (cropLeft.doubleValue() != 0.0) {
                  imageProps.put("cropLeft", cropLeft.doubleValue() * 100.0 / width);
                  LOG.log(
                      Level.FINEST, "The cropLeft is {0}", cropLeft.doubleValue() * 100.0 / width);
                } else {
                  // do not set explicitly with 0
                  imageProps.remove("cropLeft");
                }
              }
            }
            // 2nd half of absolute fo:clip to relative crop (Changes API) mapping
            if (imageProps.has("cropTop")) {
              Number cropTop = (Number) imageProps.get("cropTop");
              LOG.log(Level.FINEST, "The clipTop is {0}", cropTop);
              double d = cropTop.doubleValue();
              if (cropTop != null) {
                if (cropTop.doubleValue() != 0.0) {
                  imageProps.put("cropTop", cropTop.doubleValue() * 100.0 / height);
                  LOG.log(
                      Level.FINEST, "The cropTop is {0}", cropTop.doubleValue() * 100.0 / height);
                } else {
                  // do not set explicitly with 0
                  imageProps.remove("cropTop");
                }
              }
            }
            if (imageProps.has("cropBottom")) {
              Number cropBottom = (Number) imageProps.get("cropBottom");
              LOG.log(Level.FINEST, "The clipBottom is {0}", cropBottom);
              if (cropBottom != null) {
                if (cropBottom.doubleValue() != 0.0) {
                  imageProps.put("cropBottom", cropBottom.doubleValue() * 100.0 / height);
                  LOG.log(
                      Level.FINEST,
                      "The cropBottom is {0}",
                      cropBottom.doubleValue() * 100.0 / height);
                } else {
                  // do not set explicitly with 0
                  imageProps.remove("cropBottom");
                }
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
          LOG.log(Level.FINEST, "Width: {0} Height: {1}", new Object[] {width, height});
        } else {
          LOG.log(Level.WARNING, "The image ''{0}'' could not be loaded!", href);
        }
      } else {
        LOG.log(Level.WARNING, "The image ''{0}'' could not be loaded!", href);
      }
    } catch (IOException ex) {
      LOG.log(Level.SEVERE, "Image could not be found at " + href, ex);
    }
  }

  private static List<Integer> incrementAll(List<Integer> position) {
    if (position != null) {
      position.replaceAll(integer -> integer + 1);
    }
    return position;
  }
}
