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
 * distributed under the License is distributed on an "AS IS952" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.odfdom.changes;

import static org.odftoolkit.odfdom.changes.ChangesFileSaxHandler.COMMENT_PREFIX;
import static org.odftoolkit.odfdom.changes.JsonOperationProducer.BLACK;
import static org.odftoolkit.odfdom.changes.OperationConstants.*;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_DEFAULT;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_EVEN;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_FIRST;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_DEFAULT;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_EVEN;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_FIRST;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.attribute.draw.DrawStyleNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.style.StyleRunThroughAttribute;
import org.odftoolkit.odfdom.dom.attribute.table.TableDefaultCellStyleNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.table.TableStyleNameAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextStyleNameAttribute;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.dc.DcCreatorElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDateElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawShapeElementBase;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationEndElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFontFaceDeclsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleChartPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDefaultStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDrawingPagePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderFooterPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelLabelAlignmentElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleRubyPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleSectionPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableRowPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListHeaderElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfGraphicProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.dom.style.props.OdfStyleProperty;
import org.odftoolkit.odfdom.incubator.doc.draw.OdfDrawImage;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeMasterStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.pkg.*;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.odfdom.type.Base64Binary;
import org.odftoolkit.odfdom.type.StyleName;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * ToDo: Is it more flexible to build a different queue for OperationQueue and create an JSON
 * exporter? Can a JSONArray / JSONObject be initialized with an existing queue?
 *
 * @author svante.schubertATgmail.com
 */
public class JsonOperationConsumer {
  private static final Logger LOG = Logger.getLogger(JsonOperationConsumer.class.getName());

  private static JSONObject CELL_WITH_BORDER_ATTRS = null;
  // Mode for column insertion
  private static final String INSERT_BEFORE = "before";
  private static final String INSERT_AFTER = "after";
  private static final String HUNDRED_PERCENT = "100%";

  static {
    try {
      CELL_WITH_BORDER_ATTRS =
          new JSONObject(
              "{\"cell\":{\"padding\":97,\"borderLeft\":{\"width\":2,\"style\":\"solid\",\"color\":{\"value\":\"000000\",\"type\":\"rgb\"}},\"borderBottom\":{\"width\":2,\"style\":\"solid\",\"color\":{\"value\":\"000000\",\"type\":\"rgb\"}},\"borderTop\":{\"width\":2,\"style\":\"solid\",\"color\":{\"value\":\"000000\",\"type\":\"rgb\"}},\"borderRight\":{\"width\":2,\"style\":\"solid\",\"color\":{\"value\":\"000000\",\"type\":\"rgb\"}}}}");
    } catch (JSONException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  public static int applyOperations(CollabTextDocument opsDoc, JSONArray ops) throws Exception {
    LOG.log(Level.FINEST, "*************** INCOMING OPERATIONS **********\n{0}", ops);
    int acceptedOpCount = 0;
    final OdfDocument doc = opsDoc.getDocument();

    // creating styles first, triggering DOM creation
    final OdfStylesDom stylesDom = doc.getStylesDom();
    final OdfContentDom contentDom = doc.getContentDom();

    Component rootComponent = null;
    JSONObject op = null;
    try {
      for (int i = 0; i < ops.length(); i++) {
        opsDoc.setAppliedChangesCount(i);
        op = (JSONObject) ops.get(i);
        String opName = op.getString(OPK_NAME);
        String context = op.optString(OPK_CONTEXT);
        if (context == null || context.isEmpty()) {
          rootComponent = doc.getRootComponent();
        } else {
          String masterPageName = null;
          PageArea pageArea = null;
          OdfElement targetElement = null;
          if (context.startsWith("header")) {
            if (context.startsWith(HEADER_DEFAULT.getPageAreaName())) {
              masterPageName =
                  context.substring(
                      HEADER_DEFAULT.getPageAreaName().length() + 1, context.length());
              pageArea = HEADER_DEFAULT;
            } else if (context.startsWith(HEADER_FIRST.getPageAreaName())) {
              masterPageName =
                  context.substring(HEADER_FIRST.getPageAreaName().length() + 1, context.length());
              pageArea = HEADER_FIRST;
            } else if (context.startsWith(HEADER_EVEN.getPageAreaName())) {
              masterPageName =
                  context.substring(HEADER_EVEN.getPageAreaName().length() + 1, context.length());
              pageArea = HEADER_EVEN;
            }
            targetElement = doc.getRootComponentElement(masterPageName, pageArea, false);
          } else if (context.startsWith("footer")) {
            if (context.startsWith(FOOTER_DEFAULT.getPageAreaName())) {
              masterPageName =
                  context.substring(
                      FOOTER_DEFAULT.getPageAreaName().length() + 1, context.length());
              pageArea = FOOTER_DEFAULT;
            } else if (context.startsWith(FOOTER_FIRST.getPageAreaName())) {
              masterPageName =
                  context.substring(FOOTER_FIRST.getPageAreaName().length() + 1, context.length());
              pageArea = FOOTER_FIRST;
            } else if (context.startsWith(FOOTER_EVEN.getPageAreaName())) {
              masterPageName =
                  context.substring(FOOTER_EVEN.getPageAreaName().length() + 1, context.length());
              pageArea = FOOTER_EVEN;
            }
            targetElement = doc.getRootComponentElement(masterPageName, pageArea, false);
          } else if (context.startsWith(COMMENT_PREFIX)) {
            doc.getRootComponent(); // force loading if not done, yet
            // find comment with the given id
            targetElement =
                opsDoc.getDocument().getAnnotation(context.substring(COMMENT_PREFIX.length()));
          }
          rootComponent = targetElement.getComponent();
          if (rootComponent == null) {
            rootComponent = new Component(targetElement);
          }
        }
        if (opName.equals(OP_PARAGRAPH)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addParagraph(rootComponent, start, attrs);
        } else if (opName.equals(OP_DELETE)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONArray end = decrementAll(op.optJSONArray(OPK_END));
          delete(rootComponent, start, end);
        } else if (opName.equals(OP_TEXT)) { // missing the mapping of whitespaces
          acceptedOpCount++;
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          String text = op.getString("text");
          JsonOperationConsumer.addText(rootComponent, start, attrs, text);
        } else if (opName.equals(OP_TABLE)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          JSONObject sizeExceeded = op.optJSONObject("sizeExceeded");
          addTable(rootComponent, start, attrs, sizeExceeded, null);
        } else if (opName.equals(OP_ROWS)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          int count = op.optInt("count", 1);
          boolean addDefaultCells = op.optBoolean("addDefaultCells");
          int referenceRow = op.optInt("referenceRow", -1);
          addRows(rootComponent, start, attrs, count, addDefaultCells, referenceRow, true);
        } else if (opName.equals(OP_CELLS)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          int count = op.optInt("count", 1);
          addCells(rootComponent, start, attrs, count, null, null, true);
        } else if (opName.equals(OP_COLUMN)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          Integer gridPosition = op.getInt("gridPosition");
          JSONArray tableGrid = op.getJSONArray("tableGrid");
          String insertMode = op.optString("insertMode");
          addColumns(rootComponent, start, tableGrid, gridPosition, insertMode);
        } else if (opName.equals(OP_COLUMNS_DELETE)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          Integer startGrid = op.getInt("startGrid");
          Integer endGrid = op.optInt("endGrid");
          deleteColumns(rootComponent, start, startGrid, endGrid);
        } else if (opName.equalsIgnoreCase(OP_NOTE)) {
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          String id = op.getString(OPK_ID);
          String author = op.optString("author");
          String date = op.optString("date");
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addAnnotation(rootComponent, start, attrs, id, author, date);
        } else if (opName.equalsIgnoreCase(OP_NOTE_SELECTION)) {
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          String id = op.getString(OPK_ID);
          String type = op.optString(OPK_TYPE);
          String position = op.optString(OPK_POSITION);
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addNoteSelection(rootComponent, start, type, position, attrs, id);
        } else if (opName.equalsIgnoreCase(OP_HEADER_FOOTER)) {
          //                        String type = op.getString(OPK_TYPE);
          String id = op.getString(OPK_ID);
          addDeleteHeaderFooter(doc, true, id);
        } else if (opName.equalsIgnoreCase(OP_HEADER_FOOTER_DELETE)) {
          String id = op.getString(OPK_ID);
          addDeleteHeaderFooter(doc, false, id);
        } else if (opName.equalsIgnoreCase(OP_DOCUMENT_LAYOUT)) {
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          modifyPages(doc, attrs);
        } else if (opName.equals(OP_FORMAT)) {
          acceptedOpCount++;
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONArray end = decrementAll(op.optJSONArray(OPK_END));
          JsonOperationConsumer.format(rootComponent, start, end, attrs);
        } else if (opName.equals(OP_PARAGRAPH_MERGE)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          mergeParagraph(rootComponent, start);
        } else if (opName.equals(OP_PARAGRAPH_SPLIT)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          splitParagraph(rootComponent, start);
        } else if (opName.equals(OP_STYLE)) {
          acceptedOpCount++;
          String type = op.getString(OPK_TYPE);
          String styleId = op.getString(OPK_STYLE_ID);
          String styleName = op.optString("styleName");
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          String parent = op.optString("parent");
          Boolean hidden = op.optBoolean("hidden");
          Boolean custom = op.optBoolean("custom");
          addStyles(doc, type, styleId, styleName, attrs, parent, hidden, custom);
        } else if (opName.equals(OP_STYLE_CHANGE)) {
          acceptedOpCount++;
          String type = op.getString(OPK_TYPE);
          String styleId = op.getString(OPK_STYLE_ID);
          String styleName = op.optString("styleName");
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          changeStyle(doc, type, styleId, styleName, attrs);
        } else if (opName.equals(OP_STYLE_DELETE)) {
          acceptedOpCount++;
          String type = op.getString(OPK_TYPE);
          String styleId = op.getString(OPK_STYLE_ID);
          deleteStyle(doc, styleId, type);
        } else if (opName.equals(OP_FONT_DECL)) {
          acceptedOpCount++;
          JSONObject attrs = op.getJSONObject(OPK_ATTRS);
          JSONArray panose1 = attrs.optJSONArray("panose1");
          String panose1Value = null;
          if (panose1 != null) {
            panose1Value = panose1.toString();
          }
          String fontName = op.getString("fontName");
          String[] altNames = (String[]) attrs.opt("altNames");
          String family = attrs.optString("family");
          String familyGeneric = attrs.optString("familyGeneric");
          String pitch = attrs.optString("pitch");
          addFontData(doc, fontName, altNames, family, familyGeneric, pitch, panose1Value);
        } else if (opName.equals(OP_TAB)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addTab(rootComponent, start, attrs);
        } else if (opName.equals(OP_LINE_BREAK)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addLineBreak(rootComponent, start, attrs);
        } else if (opName.equals(OP_DRAWING)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          String type = op.optString(OPK_TYPE);
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addDrawing(rootComponent, start, attrs, type, opsDoc);
        } else if (opName.equals(OP_FIELD)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          String representation = op.optString("representation");
          String type = op.optString(OPK_TYPE);
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          addField(
              rootComponent,
              opsDoc.getDocument().getContentDom(),
              start,
              type,
              representation,
              attrs);
        } else if (opName.equals(OP_FIELD_UPDATE)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          String representation = op.optString("representation");
          String type = op.optString(OPK_TYPE);
          JSONObject attrs = op.optJSONObject(OPK_ATTRS);
          changeField(
              rootComponent,
              opsDoc.getDocument().getContentDom(),
              start,
              type,
              representation,
              attrs);
        } else if (opName.equals(OP_MOVE)) {
          acceptedOpCount++;
          JSONArray start = decrementAll(op.getJSONArray(OPK_START));
          JSONArray to = decrementAll(op.getJSONArray("to"));
          //	JSONArray end = decrementAll(op.optJSONArray(OPK_END));
          move(rootComponent, start, to);
        } else if (opName.equals(OP_LIST_STYLE)) {
          acceptedOpCount++;
          String listStyle = op.optString("listStyleId");
          JSONObject listDefinition = op.optJSONObject("listDefinition");
          addListStyle(listStyle, listDefinition, doc);
        } else if (opName.equals(OP_ERROR)) {
          throw new Exception("ERROR_SIMULATED");
        } else { // unknown operation
          LOG.log(Level.FINEST, "Operation is unknown: {0}", opName);
        }
      }
      opsDoc.setAppliedChangesCount(ops.length());
    } catch (Exception e) {
      LOG.log(
          Level.SEVERE,
          "An error occurred in the operation number {0}{1}",
          new Object[] {acceptedOpCount, 1});
      LOG.severe("The operation was: " + op);
      throw e;
    }
    return acceptedOpCount;
  }

  /**
   * For the demo document "FruitDepot-SeasonalFruits.odt" the tableElement looks like <text:p
   * text:style-name="Text_20_body"/>
   *
   * @param rootComponent high level document structure
   * @param start position of the paragraph starting with 0 and one over the existing is allowed
   * @param attrs ODF attributes to be set on the paragraph
   * @throws IndexOutOfBoundsException - if index is out of range (index < 0 || index > size()). One
   *     over size is allowed to append a paragraph.
   */
  public static void addParagraph(Component rootComponent, JSONArray start, JSONObject attrs)
      throws IndexOutOfBoundsException {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);

    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      int newPosition = start.optInt(start.length() - 1);
      addParagraph(parentComponent, newPosition, attrs);
    }
  }

  /**
   * For the demo document "FruitDepot-SeasonalFruits.odt" the tableElement looks like <text:p
   * text:style-name="Text_20_body"/>
   *
   * @param parentComponent high level document structure
   * @param newPosition position of the paragraph starting with 0 and one over the existing is
   *     allowed
   * @param attrs the new ODF attributes to be set
   * @throws IndexOutOfBoundsException - if index is out of range (index < 0 || index > size()). One
   *     over size is allowed to append a paragraph.
   */
  public static TextParagraphElementBase addParagraph(
      Component parentComponent, int newPosition, JSONObject attrs)
      throws IndexOutOfBoundsException {
    // CREATING NEW ROOT ELEMENT
    OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
    // If there are any list properties there have to be a list inserted, check if preceding- &
    // following-sibling is a list?
    TextParagraphElementBase paragraphBaseElement;
    JSONObject paraProps = null;
    if (attrs != null) {
      paraProps = attrs.optJSONObject("paragraph");
      if (paraProps != null) {
        int outlineLevel = paraProps.optInt("outlineLevel");
        if (outlineLevel != 0) {
          paragraphBaseElement = new TextHElement(xmlDoc);
          ((TextHElement) paragraphBaseElement).setTextOutlineLevelAttribute(outlineLevel);
        } else {
          paragraphBaseElement = new TextPElement(xmlDoc);
        }
      } else {
        paragraphBaseElement = new TextPElement(xmlDoc);
      }
    } else {
      paragraphBaseElement = new TextPElement(xmlDoc);
    }

    // ADDING COMPONENT
    addElementAsComponent(parentComponent, paragraphBaseElement, newPosition);

    // ADDING STYLES TO THE NEW ELEMENT
    StyleStyleElement autoStyle = addStyle(attrs, paragraphBaseElement, xmlDoc);
    if (paraProps != null) {
      if (paraProps.has("listLevel")) {
        if (paraProps.has("listStyleId")) {
          String listStyleId = paraProps.optString("listStyleId");
          if (listStyleId != null && !listStyleId.isEmpty()) {
            autoStyle.setStyleListStyleNameAttribute(listStyleId);
          }
        }
        int listLevel = getListLevel(paragraphBaseElement);
        int newListLevel;
        if (paraProps.isNull("listLevel")) {
          newListLevel = -1;
        } else {
          newListLevel = paraProps.optInt("listLevel", -2);
          // if the list level should be unchanged
          if (newListLevel == -2) {
            // use the current listlevel..
            newListLevel = listLevel;
          }
        }
        // we need to keep the existing top level list attributes, at least the list style
        String currentListStyleName = null;
        TextListElement rootListElement = getListRootElement(paragraphBaseElement);
        if (rootListElement != null) {
          currentListStyleName = rootListElement.getTextStyleNameAttribute();
        }
        setParagraphListProperties(
            paragraphBaseElement, paraProps, xmlDoc, listLevel, newListLevel, currentListStyleName);
      }
    }
    return paragraphBaseElement;
  }

  public static void addTab(Component rootComponent, JSONArray start, JSONObject attrs)
      throws IndexOutOfBoundsException {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);

    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      // CREATING NEW ROOT ELEMENT
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      OdfElement newElement = new TextTabElement(xmlDoc);
      // If there are hard coded styles on the line break, addChild them to a text:span element
      // surrounding the text:line-break
      // At all places of a text:tab a text:span as potential parentComponentElement is allowed:
      // http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#element-text_tab
      if (attrs != null) {
        TextSpanElement newSpanElement = new TextSpanElement(xmlDoc);

        // ADDING STYLES TO THE NEW ELEMENT
        addStyle(attrs, newSpanElement, xmlDoc);
        newSpanElement.appendChild(newElement);
        newElement = newSpanElement;
      }
      try {
        // ADDING COMPONENT
        addElementAsComponent(parentComponent, newElement, start.getInt(start.length() - 1));
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
  }

  public static void addLineBreak(Component rootComponent, JSONArray start, JSONObject attrs)
      throws IndexOutOfBoundsException {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);

    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      // CREATING NEW ROOT ELEMENT
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      OdfElement newElement = new TextLineBreakElement(xmlDoc);
      // If there are hard coded styles on the line break, addChild them to a text:span element
      // surrounding the text:line-break
      // At all places of a text:line-break a text:span as potential parentComponentElement is
      // allowed:
      // http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#element-text_line-break
      if (attrs != null) {
        TextSpanElement newSpanElement = new TextSpanElement(xmlDoc);

        // ADDING STYLES TO THE NEW ELEMENT
        addStyle(attrs, newSpanElement, xmlDoc);
        newSpanElement.appendChild(newElement);
        newElement = newSpanElement;
      }
      try {
        // ADDING COMPONENT
        addElementAsComponent(parentComponent, newElement, start.getInt(start.length() - 1));
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * At the the given newElement from the given ownerDocument the given style changes from attrs are
   * being applied. In addition automatic styles might being created, as ODF does not apply hard
   * styles directly to the element.
   *
   * @param attrs Map with style changes
   * @param newElement the element the style changes will be applied to
   * @param ownerDocument the XML file the element belongs to
   * @return the prior given newElement after all style changes had been applied.
   */
  // ToDo-Clean-Up: Move this to styleable Element
  public static StyleStyleElement addStyle(
      JSONObject attrs, OdfStylableElement newElement, OdfFileDom ownerDocument) {
    StyleStyleElement autoStyle = null;
    // temporary font properties taken into adapter
    OdfDocument doc = ((OdfDocument) ownerDocument.getDocument());

    // if there are style changes
    if (attrs != null) {
      OdfStyleFamily styleFamily = newElement.getStyleFamily();
      boolean hasHardFormatting = hasHardProperties(attrs, styleFamily);
      // IF THERE IS A NEW TEMPLATE STYLE
      if (attrs.has(OPK_STYLE_ID) && !attrs.isNull(OPK_STYLE_ID)) {
        // IF ONLY TEMPLATE STYLE
        String styleName = attrs.optString(OPK_STYLE_ID);
        if (!hasHardFormatting) {
          // Add template style, if there was no hard formatting it will be set directly, otherwise
          // the template style has to reference
          addStyleNameAttribute(newElement, styleFamily, ownerDocument, styleName);
          // IF THERE ARE HARD FORMATTING STYLES AND TEMPLATE STYLES
        } else {
          autoStyle = newElement.getOrCreateUnqiueAutomaticStyle();

          // adding/removing list style reference from style
          modifyListStyleName(attrs, autoStyle);

          // adding the style name
          addStyleNameAttribute(
              newElement, styleFamily, ownerDocument, autoStyle.getStyleNameAttribute());
          autoStyle.setStyleParentStyleNameAttribute(styleName);
          // APPLY HARD FORMATTING PROPERTIES OF ODF FAMILY
          mapProperties(styleFamily, attrs, autoStyle, doc);
        }
      } else { // IF NO NEW TEMPLATE STYLE
        // if Element has no Automatic Styles && styles will not add any --> skip
        String styleName = newElement.getStyleName();
        if (styleName != null) {
          if (styleName.isEmpty() && !hasHardFormatting) {
            // remove the invalid attribute
            newElement.removeAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name");
          } else {
            // if there are automatic styles adjust them
            boolean removeTemplateStyle = attrs.has(OPK_STYLE_ID) && attrs.isNull(OPK_STYLE_ID);

            if (newElement instanceof TableTableColumnElement) {
              // if the template style is being removed
              if (removeTemplateStyle) {
                autoStyle = newElement.getOrCreateUnqiueAutomaticStyle();
                autoStyle.removeAttributeNS(
                    OdfDocumentNamespace.STYLE.getUri(), "parent-style-name");
              }
              if (attrs.has("column")) {
                if (autoStyle == null) {
                  autoStyle = newElement.getOrCreateUnqiueAutomaticStyle();
                }
                // APPLY HARD FORMATTING PROPERTIES OF ODF FAMILY
                mapProperties(styleFamily, attrs, autoStyle, doc);

                // if the template style is being removed
                if (removeTemplateStyle) {
                  addStyleNameAttribute(
                      newElement, styleFamily, ownerDocument, autoStyle.getStyleNameAttribute());
                }
              }
              if (attrs.has("cell") || attrs.has("paragraph") || attrs.has("text")) {
                autoStyle =
                    newElement.getOrCreateUnqiueAutomaticStyle(
                        false, OdfStyleFamily.TableCell);
                // APPLY HARD FORMATTING PROPERTIES OF ODF FAMILY
                mapProperties(OdfStyleFamily.TableCell, attrs, autoStyle, doc);
                // default-cell-style-name
                TableDefaultCellStyleNameAttribute attr =
                    new TableDefaultCellStyleNameAttribute(ownerDocument);
                newElement.setOdfAttribute(attr);
                attr.setValue(autoStyle.getStyleNameAttribute());
              }
            } else {
              autoStyle = newElement.getOrCreateUnqiueAutomaticStyle();
              // adding/removing list style reference from style
              modifyListStyleName(attrs, autoStyle);

              // APPLY HARD FORMATTING PROPERTIES OF ODF FAMILY
              try {
                JSONObject paragraphAttr = null;
                if (newElement instanceof TextPElement
                    && styleFamily.equals(OdfStyleFamily.Paragraph)
                    && attrs.has("paragraph")
                    && ((paragraphAttr = attrs.getJSONObject("paragraph")).has("pageBreakBefore")
                        || paragraphAttr.has("pageBreakAfter"))) {
                  // search for the top table element, stop at top level and at frames
                  TableTableElement tableElement = null;
                  Node parent = newElement.getParentNode();
                  while (parent != null && !(parent instanceof DrawFrameElement)) {
                    if (parent instanceof TableTableElement) {
                      tableElement = (TableTableElement) parent;
                    }
                    parent = parent.getParentNode();
                  }
                  if (tableElement != null) {
                    OdfStyle tableStyle = tableElement.getAutomaticStyle();
                    OdfStylePropertiesBase propsElement =
                        tableStyle.getOrCreatePropertiesElement(
                            OdfStylePropertiesSet.TableProperties);
                    JSONObject paraAttrs = attrs.getJSONObject("paragraph");
                    boolean isBefore = paragraphAttr.has("pageBreakBefore");
                    String attrName = isBefore ? "pageBreakBefore" : "pageBreakAfter";
                    boolean breakValue =
                        !paraAttrs.isNull(attrName) && paraAttrs.getBoolean(attrName);
                    paraAttrs.remove(attrName);
                    if (breakValue) {
                      propsElement.setAttributeNS(
                          OdfDocumentNamespace.FO.getUri(),
                          isBefore ? "fo:break-before" : "fo:break-after",
                          "page");
                      propsElement.removeAttributeNS(
                          OdfDocumentNamespace.FO.getUri(),
                          isBefore ? "break-after" : "break-before");
                    } else {
                      propsElement.removeAttributeNS(
                          OdfDocumentNamespace.FO.getUri(),
                          isBefore ? "break-before" : "break-after");
                    }
                  }
                }
              } catch (JSONException e) {
                // no handling required
              }
              mapProperties(styleFamily, attrs, autoStyle, doc);
              // if the template style is being removed
              if (removeTemplateStyle) {
                autoStyle.removeAttributeNS(
                    OdfDocumentNamespace.STYLE.getUri(), "parent-style-name");
                addStyleNameAttribute(
                    newElement, styleFamily, ownerDocument, autoStyle.getStyleNameAttribute());
              }
            }
          }
        }
      }
      // apply numberFormat - uses existing 'Number' style or create a new one
      JSONObject cellAttrs = attrs.optJSONObject("cell");
      if (cellAttrs != null) {
        String formatCode = cellAttrs.optString("formatCode");
        if (!formatCode.isEmpty()) {
          String dataStyleName =
              MapHelper.findOrCreateDataStyle(
                  formatCode, cellAttrs.optLong("formatId", -1), ownerDocument);
          autoStyle.setAttributeNS(
              OdfDocumentNamespace.STYLE.getUri(), "style:data-style-name", dataStyleName);
        } else {
          autoStyle.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "data-style-name");
        }
      }
    }
    return autoStyle;
  }

  /**
   * Modifying the @style:list-style-name to the style:style of (an automatic) style, when the
   * paragraph properties contain a "listStyleId
   */
  public static void modifyListStyleName(JSONObject attrs, OdfStyleBase autoStyle) {

    // modifying the list style
    String listStyleName;
    if (attrs.has("paragraph")) {
      JSONObject paraProps = attrs.optJSONObject("paragraph");
      listStyleName = paraProps.optString("listStyleId");
      if (listStyleName == null) {
        autoStyle.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "list-style-name");
      } else if (!listStyleName.isEmpty()) {
        autoStyle.setAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "style:list-style-name", listStyleName);
      }
    }
  }

  /**
   * Modifying the @style:list-style-name to the given text:list element, when the paragraph
   * properties contain a "listStyleId
   */
  public static void modifyListStyleName(JSONObject attrs, TextListElement rootListElement) {
    if (rootListElement != null) {
      // modifying the list style
      String listStyleName;
      if (attrs.has("paragraph")) {
        JSONObject paraProps = attrs.optJSONObject("paragraph");
        listStyleName = paraProps.optString("listStyleId");
        if (listStyleName == null) {
          rootListElement.removeAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name");
        } else if (!listStyleName.isEmpty()) {
          rootListElement.setAttributeNS(
              OdfDocumentNamespace.TEXT.getUri(), "text:style-name", listStyleName);
        }
      }
    }
  }

  public static void addStyleNameAttribute(
      OdfElement newElement,
      OdfStyleFamily styleFamily,
      OdfFileDom ownerDocument,
      String styleName) {
    // Add template style, if there was no hard formatting it will be set directly, otherwise the
    // template style has to reference
    String attributeName = null;
    if (styleFamily.getName().equals("paragraph") || styleFamily.getName().equals("text")) {
      TextStyleNameAttribute attr = new TextStyleNameAttribute(ownerDocument);
      newElement.setOdfAttribute(attr);
      attr.setValue(styleName);
      attributeName = "text:style-name";
    } else if (styleFamily.getName().equals("table")
        || styleFamily.getName().equals("table-column")
        || styleFamily.getName().equals("table-row")
        || styleFamily.getName().equals("table-cell")) {
      TableStyleNameAttribute attr = new TableStyleNameAttribute(ownerDocument);
      newElement.setOdfAttribute(attr);
      attr.setValue(styleName);
      attributeName = "table:style-name";
    } else if (styleFamily.getName().equals("graphic")) {
      DrawStyleNameAttribute attr = new DrawStyleNameAttribute(ownerDocument);
      newElement.setOdfAttribute(attr);
      attr.setValue(styleName);
      attributeName = "draw:style-name";
    }
    ErrorHandler errorHandler = ownerDocument.getDocument().getPackage().getErrorHandler();
    if (errorHandler != null && attributeName != null) {
      // Is String from type NCName? == http://www.w3.org/TR/xmlschema-2/#NCName
      if (!StyleName.isValid(styleName)) {
        try {
          errorHandler.error(
              new OdfValidationException(
                  OdfSchemaConstraint.DOCUMENT_XML_INVALID_ATTRIBUTE_VALUE,
                  styleName,
                  attributeName));
        } catch (SAXException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  /**
   * Move the pointed component to its new destination.
   *
   * @param rootComponent the component to be moved
   * @param start the origin of the component to be moved
   * @param to the new destination
   */
  public static void move(Component rootComponent, JSONArray start, JSONArray to) {
    try {
      Component parentSourceComponent = rootComponent.getParentOf(start);
      OdfElement movedNode =
          (OdfElement) parentSourceComponent.remove(start.getInt(start.length() - 1));
      insert(rootComponent, movedNode, to);
    } catch (JSONException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Copy the pointed component to its new destination.
   *
   * @param rootComponent the component to be moved
   * @param start origin of the component to be moved
   * @param to the new destination
   */
  static OdfElement copy(Component rootComponent, JSONArray start, JSONArray to) {
    Component sourceComponent = rootComponent.get(start);
    OdfElement source = (OdfElement) sourceComponent.getRootElement().cloneNode(true);
    insert(rootComponent, source, to);
    return source;
  }

  private static void insert(Component rootComponent, OdfElement rootElement, JSONArray to) {
    if (rootElement != null) {
      try {
        Component parentTargetComponent = rootComponent.getParentOf(to);
        // Text can simply be inserted without taking any parentComponentElement cache/counting into
        // account
        parentTargetComponent.getRootElement().insert(rootElement, to.getInt(to.length() - 1));
        if (rootElement instanceof OdfElement) {
          parentTargetComponent.addChild(to.getInt(to.length() - 1), rootElement.getComponent());
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Deletes a component from the document.
   *
   * @param rootComponent the root of the component tree
   * @param start the position of the component to be deleted /* ToDo: Possible to join the two
   *     delete methods, this and OdfElement delete(int start, int end)??
   */
  private static void deleteComponents(Component rootComponent, JSONArray start, JSONArray end)
      throws IndexOutOfBoundsException {
    Component parentComponent = rootComponent.getParentOf(start);
    OdfElement parentComponentElement = parentComponent.getRootElement();

    // DELETING TEXT
    if (parentComponent instanceof TextContainer) {
      int pos = start.optInt(start.length() - 1);
      ((TextContainer) parentComponent).removeText(pos, pos + 1);
    } else {
      // DELETING COMPONENT
      try {
        int startPos = start.getInt(start.length() - 1);
        int endPos;
        OdfElement targetElement = null;
        int deletionCount = 1;
        if (end != null) {
          endPos = end.getInt(start.length() - 1);
          deletionCount += endPos - startPos;
        } else {
          endPos = startPos;
        }
        // delete from the end to the start, otherwise the count would be influenced..
        while (deletionCount > 0) {
          targetElement = (OdfElement) parentComponent.getChildNode(endPos);
          if (targetElement == null) {
            break;
          }
          int repetition = targetElement.getRepetition();
          if (targetElement instanceof TableTableCellElement) {
            Component tableComponent = parentComponent.getParent();
            TableTableElement tableElement = (TableTableElement) tableComponent.mRootElement;

            // WORK AROUND for "UNDO COLUMN WIDTH" problem
            if (((Table) tableElement.getComponent()).isWidthChangeRequired()) {
              // INSERT COLUMN
              // Returns all TableTableColumn descendants that exist within the tableElement, even
              // within groups, columns and header elements
              OdfTable table = OdfTable.getInstance(tableElement);
              table.removeColumnsByIndex(endPos, deletionCount - 1 + endPos, true);
              ((Table) tableElement.getComponent()).hasChangedWidth();
            }
          }
          if (repetition > 1) {
            if (targetElement instanceof TableTableRowElement) {
              if (deletionCount - repetition > 1) {
                targetElement.removeAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(), "number-rows-repeated");
              } else {
                targetElement.setAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(),
                    "table:number-rows-repeated",
                    String.valueOf(repetition - deletionCount));
              }
            } else if (targetElement instanceof TableTableCellElement) {
              if (deletionCount - repetition > 1) {
                targetElement.removeAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
              } else {
                targetElement.setAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(),
                    "table:number-columns-repeated",
                    String.valueOf(repetition - deletionCount));
              }
            }
          } else {
            OdfElement targetParent = (OdfElement) targetElement.getParentNode();
            if (targetParent.equals(parentComponentElement)) {
              parentComponentElement.removeChild(targetElement);
            } else if (targetParent
                instanceof DrawTextBoxElement) { // text box elements are no 'boilerplate'
              targetParent.removeChild(targetElement);
            } else {
              // if the parentComponentElement component root element is not the
              // parentComponentElement of the child component, there have to be boilerplate
              // elements inbetween that have to take care of
              // the common use case is a paragraph within a list. <text:list> <text:list-item>
              // <text:p/> </text:list-item>< /text:list>. With the last paragraph the list
              // construct is being removed!
              // -> this seems to be wrong. For lists in the body it is a no op while in cells it
              // sets the cell parent component wrongly
              // parentComponentElement.setComponent(rootComponent);
              removeComponentElementAndInbetweenBoilerplate(parentComponentElement, targetElement);
            }
            // already for those and in the future for all components the component removal includes
            // as well an XML removal
            if (!(parentComponent instanceof Table
                || parentComponent instanceof Row
                || parentComponent instanceof Cell)) {
              try {
                parentComponent.remove(start.getInt(start.length() - 1));
              } catch (JSONException ex) {
                LOG.log(Level.SEVERE, null, ex);
              }
            }
            deletionCount--;
            endPos--;
          }
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * If the parent component root element is not the element parent of the child component's root
   * element, there have to be boilerplate elements in-between that might have to be deleted as
   * well, if the last sibling was deleted.
   *
   * @param parentComponentElement The element representing the next higher component
   * @param targetElement the element to be deleted. Bottom-up will be all parents traversed and
   *     deleted in case the original element to be deleted had no other siblings being components.
   */
  private static boolean removeComponentElementAndInbetweenBoilerplate(
      OdfElement parentComponentElement, OdfElement targetElement) {
    OdfElement targetParent = (OdfElement) targetElement.getParentNode();
    if (targetParent instanceof TextListItemElement) {
      OdfElement previousSibling = OdfElement.getPreviousSiblingElement(targetElement);
      if (previousSibling == null) {
        OdfElement nextSibling = OdfElement.getNextSiblingElement(targetElement);
        if (nextSibling != null) {
          // removing the no longer desired content
          targetParent.removeChild(targetElement);

          // moving the previous list-item children to the new list-header
          TextListHeaderElement listHeader =
              new TextListHeaderElement((OdfFileDom) targetParent.getOwnerDocument());
          targetParent.moveChildrenTo(listHeader);

          OdfElement grandParent = (OdfElement) targetParent.getParentNode();
          grandParent.replaceChild(listHeader, targetParent);
          targetParent = listHeader;
        } else {
          targetParent.removeChild(targetElement);
        }
      } else {
        targetParent.removeChild(targetElement);
      }
    } else {
      targetParent.removeChild(targetElement);
    }
    // if the parent component root element is not the parent of the child component, there have to
    // be boilerplate elements inbetween that have to take care of
    return removeComponentElementAndEmptyBoilerplate(
            parentComponentElement, targetParent, targetParent.countDescendantComponents())
        == 0;
  }

  /**
   * If the componentParent component root element is not the element componentParent of the child
   * component's root element, there have to be boilerplate elements in-between that might have to
   * be deleted as well, if the last sibling was deleted.
   *
   * @param componentParent The element representing the next higher component
   * @param targetElement the element to be deleted. Bottom-up will be all parents traversed and
   *     deleted in case the original element to be deleted had no other siblings being components.
   * @param descendantCount the number of descendants of the targetElement
   * @return the number of descendants of the targetParent
   */
  private static int removeComponentElementAndEmptyBoilerplate(
      OdfElement componentParent, OdfElement targetElement, int descendantCount) {
    // if there is boilerplate elements between the targetElement and the componentParent
    OdfElement targetParent = (OdfElement) targetElement.getParentNode();
    // if there is only a single element below (not being the original target)
    if (descendantCount == 0) {
      // if there is still no other component, delete the boilerplate element
      targetParent.removeChild(targetElement);
    }
    if (!targetParent.equals(componentParent)) {
      removeComponentElementAndEmptyBoilerplate(
          componentParent, targetParent, targetParent.countDescendantComponents());
    }
    return descendantCount;
  }

  // ** Only adds text to Headings and Paragraphs */
  public static void addText(
      Component rootComponent, JSONArray start, JSONObject attrs, String newText)
      throws IndexOutOfBoundsException {
    Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent != null && start != null) {
      try {
        addText(
            (TextParagraphElementBase) parentComponent.getRootElement(),
            start.getInt(start.length() - 1),
            attrs,
            newText);
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    } else {
      LOG.log(
          Level.SEVERE,
          "Could not add text as no container (e.g. paragraph) was found at position: {0}",
          start);
    }
  }

  public static void addText(
      TextParagraphElementBase paragraphElementBase, int startPos, JSONObject attrs, String newText)
      throws IndexOutOfBoundsException {

    // ToDo - parse the text spans positions from the beginning, creating array - could do this from
    // the beginning..
    // better not for very long texts it is unnecessary, does not scale
    //        adding the span depths, the span element reference, for every character?
    int endPos = startPos + newText.length() - 1;
    paragraphElementBase.insert(newText, startPos);
    // LO let the value attribute overrule the content, therefore this value have to vanish!
    OdfElement parentElement = (OdfElement) paragraphElementBase.getParentNode();
    if (parentElement instanceof TableTableCellElement) {
      ((TableTableCellElement) parentElement)
          .removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "value");
      ((TableTableCellElement) parentElement)
          .removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "value-type");
    }
    if (attrs != null && attrs.length() > 0) {
      applyStyleOnText(paragraphElementBase, startPos, endPos, attrs);
    }
  }

  /** Reused by insertText and format for Text */
  private static void applyStyleOnText(
      OdfElement parentElement, Integer startPos, Integer endPos, JSONObject attrs) {
    if (parentElement != null) {

      // ToDo - addChild the paragraph text length to the paragraph? Than I need to intercept all
      // text manipulations!!
      // ToDo - parse the text spans positions from the beginning, creating array - could do this
      // from the beginning..
      // better not for very long texts it is unnecessary, does not scale
      //  adding the span depths, the span element reference, for every character?
      // Insert an hyperlink above the span if an URL was included as property
      if (attrs.has("character")) {
        // Remove JSON String encoding for URLs
        JSONObject chars = attrs.optJSONObject("character");
        String url = chars.optString("url");
        if (url != null && !url.isEmpty()) {
          chars.remove(url);
          chars.put("url", url);
          attrs.put("character", chars);
        }
        parentElement.markText(startPos, endPos, attrs);
      }
    }
  }

  /**
   * Analyze the attrs if there is any new style properties given or only existing should be removed
   */
  private static boolean hasHardProperties(JSONObject attrs, OdfStyleFamily styleFamily) {
    int attrsLength = attrs.length();
    if (attrs.has(OPK_STYLE_ID) && !attrs.isNull(OPK_STYLE_ID)) {
      attrsLength--;
    }
    if (attrs.has("changes")) {
      attrsLength--;
    }
    Map<String, OdfStylePropertiesSet> elementProps =
        Component.getAllStyleGroupingIdProperties(styleFamily);
    for (String propertyId : elementProps.keySet()) {
      if (attrs.has(propertyId)) {
        JSONObject newProps = attrs.optJSONObject(propertyId);
        if (newProps != null) {
          int propsLength = newProps.length();
          Iterator<String> keys = newProps.keys();
          String key;
          while (keys.hasNext()) {
            key = keys.next();
            // if there is a deletion of a hard formatting or an URL, which is not being used for
            // <text:span> but an own element (<text:a>)
            if (newProps.has(key) && newProps.isNull(key) || key.equals("url")) {
              propsLength--;
            } else {
              break;
            }
          }
          if (propsLength == 0) {
            attrsLength--;
          } else {
            break;
          }
        } else {
          // remove key, when value is null
          attrs.remove(propertyId);
        }
      }
    }
    return attrsLength != 0;
  }

  private static void mergeParagraph(Component rootComponent, JSONArray start)
      throws IndexOutOfBoundsException {
    Component firstComponent = rootComponent.get(start);
    TextParagraphElementBase firstParagraph = null;
    if (firstComponent != null) {
      firstParagraph = (TextParagraphElementBase) firstComponent.getRootElement();
      if (firstParagraph == null) {
        throw new IndexOutOfBoundsException("There was no component for " + start + " accessible.");
      }
    } else {
      throw new IndexOutOfBoundsException("There was no component for " + start + " accessible.");
    }

    Component secondComponent = rootComponent.getNextSiblingOf(start);
    if (secondComponent != null) {
      TextParagraphElementBase secondParagraph =
          (TextParagraphElementBase) secondComponent.getRootElement();
      if (secondParagraph != null) {
        try {
          secondParagraph.moveChildrenTo(firstParagraph);
          secondParagraph.getParentNode().removeChild(secondParagraph);
          secondComponent.getParent().remove(start.getInt(start.length() - 1) + 1);
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else {
        throw new IndexOutOfBoundsException("There was no sibling for " + start + " accessible.");
      }
    } else {
      LOG.log(Level.SEVERE, "Could not fine second Paragraph to merge. Position: {0}", start);
    }
  }

  public static void delete(Component rootComponent, JSONArray start, JSONArray end)
      throws IndexOutOfBoundsException {
    Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent != null) {
      if (parentComponent instanceof TextContainer) { // text container might
        deleteText(rootComponent, start, end);
      } else {
        deleteComponents(rootComponent, start, end);
      }
    }
  }

  /**
   * Deletes the cells of a single column or multiple columns from a tableElement.
   *
   * @param rootComponent high level document structure
   * @param start Integer[] The logical position of the tableElement whose columns will be removed.
   * @param startGrid Integer Zero-based index of the first column to be removed, according to the
   *     tableGrid attribute of the tableElement.
   * @param endGrid Integer (optional) Zero-based index of the last column to be removed (closed
   *     range). If omitted, only one column will be removed. Note: Each cell that is addressed by
   *     the specified column range will be removed completely. As a side effect, this op changes
   *     the tableGrid attribute of the parentComponentElement tableElement.
   */
  public static void deleteColumns(
      Component rootComponent, JSONArray start, Integer startGrid, Integer endGrid) {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.get(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE,
          "The table parent component of the column should exist at position {0}",
          start);
    } else {
      // CREATING NEW ROOT ELEMENT
      TableTableElement tableElement = (TableTableElement) parentComponent.getRootElement();
      OdfTable table = OdfTable.getInstance(tableElement);

      // WORK AROUND for "UNDO COLUMN WIDTH" problem
      if (!((Table) tableElement.getComponent()).isWidthChangeRequired()) {
        Table.stashColumnWidths(tableElement);
      }
      table.removeColumnsByIndex(startGrid, endGrid - startGrid + 1);

      // WORK AROUND for "UNDO COLUMN WIDTH" problem (see JsonOperationConsumer for further changes)
      if (((Table) tableElement.getComponent()).isWidthChangeRequired()) {
        JsonOperationConsumer.setColumnsWidth(
            tableElement.getComponent(),
            ((Table) tableElement.getComponent()).getPosition(),
            ((Table) tableElement.getComponent()).popTableGrid(),
            true);
      }
    }
  }

  /**
   * Removes a template style from the document
   *
   * @param doc temporary font properties taken into adapter
   */
  public static void deleteStyle(OdfDocument doc, String styleId, String type) {
    final OdfOfficeStyles styles = doc.getDocumentStyles();
    OdfStyle oldStyle = styles.getStyle(styleId, Component.getFamily(type));
    if (oldStyle != null) {
      styles.removeChild(oldStyle);
    } else {
      // check if this is a default style
      final OdfDefaultStyle oldDefaultStyle = styles.getDefaultStyle(Component.getFamily(type));
      if (oldDefaultStyle != null) {
        styles.removeChild(oldDefaultStyle);
      }
    }
  }

  public static void changeStyle(
      OdfDocument doc, String type, String styleId, String styleName, JSONObject attrs) {
    OdfOfficeStyles styles = doc.getDocumentStyles();
    OdfStyleFamily styleFamily = Component.getFamily(type);
    // might be null if not existent
    OdfStyle style = styles.getStyle(styleId, styleFamily);

    if (styleName != null && !styleName.isEmpty()) {
      style.setStyleDisplayNameAttribute(styleName);
    }

    if (attrs != null) {
      if (type.equals("table")) {

        style.removeProperty(
            OdfStyleProperty.get(
                OdfStylePropertiesSet.TableProperties, StyleTablePropertiesElement.ELEMENT_NAME));

        mapProperties(styleFamily, attrs.optJSONObject("wholetable"), style, doc);
      } else {

        style.removeProperty(
            OdfStyleProperty.get(
                OdfStylePropertiesSet.ParagraphProperties,
                StyleParagraphPropertiesElement.ELEMENT_NAME));
        style.removeProperty(
            OdfStyleProperty.get(
                OdfStylePropertiesSet.TextProperties, StyleTextPropertiesElement.ELEMENT_NAME));

        mapProperties(styleFamily, attrs, style, doc);
      }

      // APPLY PARAGRAPH ONLY PROPERTIES
      if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
        handleParaOutline(style, attrs);
      }
    }
  }

  //	/**
  //	 * Adds a new none automatic style to the document. If the style is hidden a
  //	 * default style, otherwise a template style will be added.
  //	 * @param doc  temporary font properties taken into adapter
  //	 */
  public static void addStyles(
      OdfDocument doc,
      String type,
      String styleId,
      String styleName,
      JSONObject attrs,
      String parent,
      Boolean hidden,
      Boolean custom) {

    OdfOfficeStyles styles = null;
    try {
      styles = doc.getStylesDom().getOrCreateOfficeStyles();
    } catch (SAXException | IOException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    if (attrs != null) {
      OdfStyleFamily styleFamily = Component.getFamily(type);
      if (hidden) { // DEFAULT STYLE
        // Removing any existing default style
        OdfDefaultStyle oldDefaultStyle = styles.getDefaultStyle(styleFamily);
        if (oldDefaultStyle != null) {
          styles.removeChild(oldDefaultStyle);
        }
        // Adding new default style
        StyleDefaultStyleElement defaultStyleElement =
            styles.newStyleDefaultStyleElement(styleFamily.getName());
        mapProperties(styleFamily, attrs, defaultStyleElement, doc);
      } else { // TEMPLATE STYLE
        OdfStyle oldStyle = styles.getStyle(styleName, styleFamily);
        if (oldStyle != null) {
          styles.removeChild(oldStyle);
        } else {
          /* Workaround for OOo applications
           * Applications from the OpenOffice family are exchanging any space in their names with an '_20_' string.
           * Unfortunately they are using them equivalent in general, therefore the names 'Heading_20_2' and 'Heading 2' would result and into a name clash */
          if (styleName != null && styleName.contains(" ")) {
            String testName = styleName.replace(" ", "_20_");
            oldStyle = styles.getStyle(testName, styleFamily);
            if (oldStyle != null) {
              styles.removeChild(oldStyle);
            }
          }
        }
        StyleStyleElement style = styles.newStyle(styleId, styleFamily);
        if (parent != null && !parent.isEmpty()) {
          style.setStyleParentStyleNameAttribute(parent);
        }
        if (styleName != null && !styleName.isEmpty()) {
          style.setStyleDisplayNameAttribute(styleName);
        }

        // APPLY FORMATTING PROPERTIES
        if (type.equals("table")) {
          mapProperties(styleFamily, attrs.optJSONObject("wholetable"), style, doc);
        } else {
          mapProperties(styleFamily, attrs, style, doc);
        }

        // APPLY PARAGRAPH ONLY PROPERTIES
        if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
          handleParaOutline(style, attrs);
        }
        if (null != custom && custom) {
          style.setAttribute("custom", "true");
        }
      }
    }
  }

  public static void handleParaOutline(StyleStyleElement style, JSONObject attrs) {
    if (attrs != null) {
      JSONObject props = attrs.optJSONObject("paragraph");
      if (props != null) {
        // Heading Level
        if (props.has("outlineLevel")) {
          if (!props.get("outlineLevel").equals(JSONObject.NULL)) {
            int outlineLevel = props.optInt("outlineLevel");
            if (outlineLevel >= 1) {
              style.setStyleDefaultOutlineLevelAttribute(outlineLevel);
            } else {
              style.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "default-outline-level");
            }
          } else {
            style.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "default-outline-level");
          }
        }

        // Follow-Up Style
        String nextStyleId = props.optString("nextStyleId");
        // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
        if (nextStyleId != null && !nextStyleId.isEmpty()) {
          style.setStyleNextStyleNameAttribute(nextStyleId);
        }
      }
    }
  }

  /**
   * Setting attributes on text, require the text container, otherwise the character itself (as
   * paragraph is not child of paragraph
   */
  public static void format(
      Component rootComponent, JSONArray start, JSONArray end, JSONObject attrs) {
    // Only process the method if all mandatory values are present
    if (rootComponent != null && start != null && attrs != null && attrs.length() != 0) {
      // Parent will not change and have to exist to insert the new component
      // ToDo PERFORMANCE: Sometimes we do have the parentComponentElement, e.g. the table row
      // component earlier, why searching it ALWAYS again?
      final Component parentComponent = rootComponent.getParentOf(start);
      if (parentComponent != null) {
        // the targetComponent might be either text or an element
        int startPos = start.optInt(start.length() - 1);
        Node targetNode;
        if (end == null) {
          targetNode = parentComponent.getChildNode(startPos);
        } else { // if the end is not in same parent component take all the siblings
          int targetPos = -1; // -1 means to the end of the parent element
          // if it is the same parent provide the given number, otherwise -1 for till end
          if (start.length() == end.length()
              && start.optInt(start.length() - 2) == end.optInt(end.length() - 2)) {
            targetPos = end.optInt(end.length() - 1);
          }
          targetNode = parentComponent.getChildNode(startPos, targetPos);
        }
        format(parentComponent.mRootElement, targetNode, start, end, attrs, false);
      } else {
        LOG.log(
            Level.SEVERE,
            "No parent component found to '" + OP_FORMAT + "' from {0} to {1} trying to add {2}",
            new Object[] {start, end, attrs});
      }
    } else {
      LOG.log(
          Level.SEVERE,
          "No parent component found to '" + OP_FORMAT + "' from {0} to {1} trying to add {2}",
          new Object[] {start, end, attrs});
    }
  }

  /**
   * Setting attributes on text, require the text container, otherwise the character itself (as
   * paragraph is not child of paragraph
   *
   * @param formatCompleteLine in case a row/column has cell styles via a default-cell-style
   *     attribute
   */
  // PROBLEM: A column becomes a default cell style only if the complete column is selected!
  static void format(
      OdfElement parentElement,
      Node targetNode,
      JSONArray start,
      JSONArray end,
      JSONObject attrs,
      Boolean formatCompleteLine) {
    if (attrs != null && attrs.length() != 0) {
      if (targetNode != null && targetNode instanceof OdfStylableElement) {
        OdfFileDom xmlDoc = (OdfFileDom) targetNode.getOwnerDocument();
        // Adds the automatic/template style to the target element
        StyleStyleElement autoStyle = null;
        if (targetNode instanceof TextParagraphElementBase) {
          TextParagraphElementBase paragraphBaseElement = (TextParagraphElementBase) targetNode;
          // Check if the paragraph should become a list
          JSONObject paraProps = attrs.optJSONObject("paragraph");
          // adjust list styles and new outline my be triggered by new styleId (template style)
          if (paraProps != null || attrs.has(OPK_STYLE_ID)) {
            boolean isNewList = false;
            boolean hasListLevel = false;
            boolean hasNewListId = false;
            if (paraProps != null) {
              hasListLevel = paraProps.has("listLevel");
              hasNewListId = paraProps.has("listStyleId");
            }
            // The indent of a list is only shown in ODF, if the paragraph do not have an indent
            // itself..
            if (hasListLevel
                || hasNewListId) { // || paraProps.hasAndNotNull("listStyleId") <-- reset of
              // listStyle should not be NULL, but -1 even better 0
              TextListElement rootListElement =
                  JsonOperationConsumer.isolateListParagraph(paragraphBaseElement);
              if (rootListElement != null && hasNewListId) {
                modifyListStyleName(attrs, rootListElement);
              } else {
                isNewList = true;
              }
            }
            if (paraProps != null) {
              if (hasListLevel) { // || paraProps.hasAndNotNull("listStyleId") <-- reset of
                // listStyle should not be NULL, but -1 even better 0
                try {
                  paraProps.put("indentFirstLine", JSONObject.NULL);
                  paraProps.put("marginLeft", JSONObject.NULL);
                } catch (JSONException ex) {
                  LOG.log(Level.SEVERE, null, ex);
                }
                autoStyle = addStyle(attrs, paragraphBaseElement, xmlDoc);

                // get the current list level
                int listLevel = getListLevel(paragraphBaseElement);
                int newListLevel;
                if (paraProps.isNull("listLevel")) {
                  newListLevel = -1;
                  // as there is no list level, remove the list styles
                  if (autoStyle != null) {
                    autoStyle.removeAttributeNS(
                        OdfDocumentNamespace.STYLE.getUri(), "list-style-name");
                  }
                } else {
                  newListLevel = paraProps.optInt("listLevel", -2);
                  // if the list level should be unchanged
                  if (newListLevel == -2) {
                    // use the current listlevel..
                    newListLevel = listLevel;
                  }
                }

                // if the paragraph in the list should be shown..
                boolean listLabelHidden = false;
                if (paraProps.has("listLabelHidden")
                    && !paraProps.get("listLabelHidden").equals(JSONObject.NULL)) {
                  // get the current list level
                  listLabelHidden = paraProps.optBoolean("listLabelHidden");
                }

                // we need to keep the existing top level list attributes, at least the list style
                String currentListStyleName = null;
                TextListElement rootListElement = getListRootElement(paragraphBaseElement);
                if (rootListElement != null) {
                  currentListStyleName = rootListElement.getTextStyleNameAttribute();
                }

                // we need to split the paragraph first to root level, to be certain that it is
                // first in a list-item
                if (paraProps.has("listStyleId")
                    && !paraProps.get("listStyleId").equals(JSONObject.NULL)
                    && !listLabelHidden) {
                  String listStyleId = paraProps.optString("listStyleId");
                  // if (7 == (start.optInt(start.length() - 1)) || listStyleId == "L4") {
                  //	System.out.println("x");
                  // }

                  if (autoStyle != null && listStyleId != null && !listStyleId.isEmpty()) {
                    autoStyle.setStyleListStyleNameAttribute(listStyleId);
                  }
                  // ToDo Optimization: remove the following for list level 0 and those without
                  // preceding/following content!
                  // temporary move the paragraph out of the list to separate both list styles
                  setParagraphListProperties(
                      paragraphBaseElement, paraProps, xmlDoc, listLevel, -1, null);
                  listLevel = -1;
                }
                setParagraphListProperties(
                    paragraphBaseElement,
                    paraProps,
                    xmlDoc,
                    listLevel,
                    newListLevel,
                    currentListStyleName);
              }
              // The indent of a list is only shown in ODF, if the paragraph do not have an indent
              // itself..
              if (isNewList && hasListLevel) {
                TextListElement rootListElement =
                    JsonOperationConsumer.isolateListParagraph(paragraphBaseElement);
                modifyListStyleName(attrs, rootListElement);
              }
            }
            // switch element if outlineLevel is on a paragraph (or vise versa)
            if (attrs.has(OPK_STYLE_ID) || paraProps.has("outlineLevel")) {
              int outlineLevel = -2;
              // take the explicit outline level from the
              if (paraProps != null
                  && paraProps.has("outlineLevel")
                  && !paraProps.get("outlineLevel").equals(JSONObject.NULL)) {
                outlineLevel = paraProps.optInt("outlineLevel");
                // if not the automatic/hard style is overwriting the template/referenced style
              } else if (attrs.has(OPK_STYLE_ID)) { // get the outline level from the styleId
                if (!attrs.isNull(OPK_STYLE_ID)) {
                  String styleId = attrs.optString(OPK_STYLE_ID);
                  if (styleId != null && !styleId.isEmpty()) {
                    // Update the styleId before getting the outline, as the outline level might be
                    // come from the template style (e.g. "heading1" style)
                    if (autoStyle == null) {
                      addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
                    }
                    outlineLevel = getDefaultOutlineLevelFromStyleHierarchy(paragraphBaseElement);
                  }
                }
              }
              // if the <text:p> will e changed to a <text:h> or vice versa, the targetNode will be
              // updated
              targetNode =
                  ensureCorrectParagraphElement(
                      outlineLevel,
                      (TextParagraphElementBase) targetNode,
                      paragraphBaseElement.getComponent());
            }
          }
        } else if (targetNode instanceof TableTableElement) {
          TableTableElement tableElement = (TableTableElement) targetNode;
          autoStyle = addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
          JSONObject tableProps = attrs.optJSONObject("table");
          if (tableProps != null) {
            JSONArray tableGrid = tableProps.optJSONArray("tableGrid");
            if (tableGrid != null) {
              setColumnsWidth(tableElement.getComponent(), start, tableGrid, true);
            }
          }
        } else if (targetNode instanceof TableTableColumnElement) {
          TableTableColumnElement columnElement = (TableTableColumnElement) targetNode;
          if (attrs.has("column")) {
            autoStyle = addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
          }
          if (attrs.has("cell")) {
            // columnElement.
            autoStyle = addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
          }

          JSONObject columnProps = attrs.optJSONObject("column");
          if (columnProps != null) {
            boolean changeToVisible = columnProps.optBoolean("visible", true);
            boolean isVisible = true;
            if (columnElement.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility")) {
              isVisible =
                  Constants.VISIBLE.equals(
                      columnElement.getAttributeNS(
                          OdfDocumentNamespace.TABLE.getUri(), "visibility"));
            }
            if (isVisible && !changeToVisible || !isVisible && changeToVisible) {
              if (changeToVisible) {
                columnElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility");
              } else {
                columnElement.setAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(), "table:visibility", Constants.COLLAPSE);
              }
            }
          }
          if (formatCompleteLine) {
            setDefaultAttribute(columnElement, attrs);
          }
        } else if (targetNode instanceof TableTableRowElement) {
          TableTableRowElement rowElement = (TableTableRowElement) targetNode;
          autoStyle = addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
          JSONObject rowProps = attrs.optJSONObject("row");
          if (rowProps != null) {
            boolean changeToVisible = rowProps.optBoolean("visible", true);
            boolean isVisible = true;
            if (rowElement.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility")) {
              isVisible =
                  Constants.VISIBLE.equals(
                      rowElement.getAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility"));
            }
            if (isVisible && !changeToVisible || !isVisible && changeToVisible) {
              if (changeToVisible) {
                rowElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility");
              } else {
                rowElement.setAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(), "table:visibility", Constants.COLLAPSE);
              }
            }
          }
          // The below would lead into a format of all existing rows due to an OpenOffice issue..
          //                    if (formatCompleteLine) {
          //                        setDefaultAttribute(rowElement, attrs);
          //                    }
        } else if (targetNode instanceof TableTableCellElementBase) {
          TableTableCellElementBase cellElement = (TableTableCellElementBase) targetNode;
          autoStyle = addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
          JSONObject cellProps = attrs.optJSONObject("cell");
          if (cellProps != null) {
            int span = cellProps.optInt("gridSpan", 1);
            if (span > 1) {
              cellElement.setAttributeNS(
                  OdfDocumentNamespace.TABLE.getUri(),
                  "table:number-columns-spanned",
                  Integer.toString(span));
            }
          }
        } else if (targetNode instanceof DrawFrameElement) {
          DrawFrameElement frameElement = (DrawFrameElement) targetNode;
          setFrameProperties(frameElement, attrs);
          NodeList images = frameElement.getElementsByTagName("draw:image");
          if (images.getLength() > 0) {
            DrawImageElement imageElement = (DrawImageElement) images.item(0);
            final JSONObject imageAttrs = attrs.optJSONObject("image");
            if (imageAttrs != null) {
              setImageProperties(imageElement, imageAttrs);
            }
          }
        } else if (targetNode instanceof DrawGElement) {
          Integer gWidth = (Integer) targetNode.getUserData("groupWidth");
          Integer gHeight = (Integer) targetNode.getUserData("groupHeight");
          if (gWidth != null && gHeight != null) {
            int newWidth = -1;
            int newHeight = -1;
            try {
              JSONObject drawAttr = attrs.getJSONObject("drawing");
              if (drawAttr.has("width")) {
                newWidth = attrs.getJSONObject("drawing").getInt("width");
              }
              if (drawAttr.has("height")) {
                newHeight = attrs.getJSONObject("drawing").getInt("height");
              }
            } catch (JSONException e) {
              // no handling required
            }
            double heightFactor = 1;
            double widthFactor = 1;
            if (newHeight > 0 && newHeight != gHeight) {
              heightFactor = newHeight / (double) gHeight;
              targetNode.setUserData("groupHeight", newHeight, null);
            }
            if (newWidth > 0 && newWidth != gWidth) {
              widthFactor = newWidth / (double) gWidth;
              targetNode.setUserData("groupWidth", newHeight, null);
            }
            Node child = targetNode.getFirstChild();
            changeDrawingSizeAndPos(child, heightFactor, widthFactor);
          }
        }
        if (autoStyle == null) {
          addStyle(attrs, (OdfStylableElement) targetNode, xmlDoc);
        }
      } else if (parentElement != null) {
        // If the parentComponentElement is a paragraph/heading, the target can not be a
        // paragraph/heading as they are not allowed nested.
        // Therefore we addChild a span around the text (or target element as draw:frame for image,
        // which is done by MSO15 by default)
        if (attrs.has("character")) {
          int startPos = start.optInt(start.length() - 1);
          int endPos;
          if (end == null) {
            endPos = startPos;
          } else {
            endPos = end.optInt(end.length() - 1);
          }
          // ToDo: if the paragraph's automatic textstyle contains character attributes as well they
          // need to be spanned over the paragraph first
          // while applying a new autoStyle without character properties
          OdfStylableElement paraStylable = (OdfStylableElement) parentElement;
          StyleStyleElement paraAutoStyle = paraStylable.getAutomaticStyle();
          if (paraAutoStyle != null) {
            OdfStylePropertiesBase base =
                paraAutoStyle.getOrCreatePropertiesElement(OdfStylePropertiesSet.TextProperties);
            if (base.getAttributes().getLength() > 0) {

              Map<String, Map<String, String>> allOdfProps =
                new HashMap<>();
              Map<String, OdfStylePropertiesSet> familyPropertyGroups =
                  Component.getAllStyleGroupingIdProperties(OdfStyleFamily.Text);
              MapHelper.getStyleProperties(paraAutoStyle, familyPropertyGroups, allOdfProps);
              Map<String, Object> mappedFormatting =
                  MapHelper.mapStyleProperties(familyPropertyGroups, allOdfProps);
              if (mappedFormatting.containsKey("character")) {
                JSONObject charProps = (JSONObject) mappedFormatting.get("character");
                JSONObject newAttrs = new JSONObject();
                try {
                  newAttrs.put("character", charProps);
                } catch (JSONException e) {
                }
                applyStyleOnText(
                    parentElement,
                    0,
                    parentElement.getComponentRoot().componentSize() - 1,
                    newAttrs);
              }
              StyleStyleElement newAutoStyle = paraStylable.getOrCreateUnqiueAutomaticStyle();
              newAutoStyle.removeChild(
                  newAutoStyle.getOrCreatePropertiesElement(OdfStylePropertiesSet.TextProperties));
            }
          }
          applyStyleOnText(parentElement, startPos, endPos, attrs);
        }
      }
    }
  }

  private static void changeDrawingSizeAndPos(Node child, double heightFactor, double widthFactor) {
    while (child != null) {
      if (child instanceof DrawGElement) {
        changeDrawingSizeAndPos(child.getFirstChild(), heightFactor, widthFactor);
      } else {
        DrawShapeElementBase drawElementBase = (DrawShapeElementBase) child;
        if (heightFactor != 1.) {
          OdfAttribute heightAttr =
              ((DrawShapeElementBase) child).getOdfAttribute(OdfGraphicProperties.Height.getName());
          if (heightAttr != null) {
            int height = MapHelper.normalizeLength(heightAttr.getValue());
            height *= heightFactor;
            heightAttr.setValue(height / 100.0 + "mm");
          }
          OdfAttribute yAttr =
              ((DrawShapeElementBase) child).getOdfAttribute(OdfGraphicProperties.Y.getName());
          if (yAttr != null) {
            int y = MapHelper.normalizeLength(yAttr.getValue());
            y *= heightFactor;
            yAttr.setValue(y / 100.0 + "mm");
          }
        }
        if (widthFactor != 1.) {
          OdfAttribute widthAttr =
              ((DrawShapeElementBase) child).getOdfAttribute(OdfGraphicProperties.Width.getName());
          if (widthAttr != null) {
            int width = MapHelper.normalizeLength(widthAttr.getValue());
            width *= widthFactor;
            widthAttr.setValue(width / 100.0 + "mm");
          }
          OdfAttribute xAttr =
              ((DrawShapeElementBase) child).getOdfAttribute(OdfGraphicProperties.X.getName());
          if (xAttr != null) {
            int x = MapHelper.normalizeLength(xAttr.getValue());
            x *= widthFactor;
            xAttr.setValue(x / 100.0 + "mm");
          }
        }
      }
      child = child.getNextSibling();
    }
  }

  /**
   * Adds a reference of a cell style to the column element (bad ODF design column style should
   * better have cell style families)
   *
   * @param lineElement either the row or the column
   */
  private static void setDefaultAttribute(OdfStylableElement lineElement, JSONObject attrs) {
    JSONObject cellProps = attrs.optJSONObject("cell");
    JSONObject characterProps = attrs.optJSONObject("character");
    if (cellProps != null || characterProps != null) {
      // NOT YET SPECIFIED AS OPERATION:
      // JSONObject characterProps = attrs.optJSONObject("paragraph");
      if (cellProps != null
          && cellProps.equals(JSONObject.NULL)
          && characterProps != null
          && characterProps.equals(JSONObject.NULL)) {
        lineElement.removeAttributeNS(
            OdfDocumentNamespace.TABLE.getUri(), "default-cell-style-name");
      } else {
        String defaultCellTemplateStyleName = null;
        StyleStyleElement autoStyle = null;
        OdfStyle existingDefaultCellStyle = null;

        String defaultCellStyleName =
            lineElement.getAttributeNS(
                OdfDocumentNamespace.TABLE.getUri(), "default-cell-style-name");
        // if there is already a default cell style
        if (defaultCellStyleName != null) {
          OdfOfficeAutomaticStyles autoStyles = lineElement.getOrCreateAutomaticStyles();
          // check if this style exists in the automatic styles
          existingDefaultCellStyle =
              autoStyles.getStyle(defaultCellStyleName, OdfStyleFamily.TableCell);
          if (existingDefaultCellStyle
              == null) { // if not within automatic style, its a template style
            autoStyle =
                lineElement.getOrCreateUnqiueAutomaticStyle(
                    false, OdfStyleFamily.TableCell);
            defaultCellTemplateStyleName = defaultCellStyleName;
          } else {
            autoStyle = existingDefaultCellStyle;
            defaultCellTemplateStyleName =
                existingDefaultCellStyle.getStyleParentStyleNameAttribute();
          }
        } else {
          autoStyle =
              lineElement.getOrCreateUnqiueAutomaticStyle(false, OdfStyleFamily.TableCell);
        }

        if (defaultCellTemplateStyleName != null && !defaultCellTemplateStyleName.isEmpty()) {
          autoStyle.setAttributeNS(
              OdfDocumentNamespace.STYLE.getUri(),
              "style:parent-style-name",
              defaultCellTemplateStyleName);
        }
        mapProperties(
            OdfStyleFamily.TableCell,
            attrs,
            autoStyle,
            (OdfDocument) ((OdfFileDom) lineElement.getOwnerDocument()).getDocument());
        lineElement.setAttributeNS(
            OdfDocumentNamespace.TABLE.getUri(),
            "table:default-cell-style-name",
            autoStyle.getStyleNameAttribute());
      }
    }
  }

  private static int getDefaultOutlineLevelFromStyleHierarchy(
      TextParagraphElementBase paragraphBaseElement) {
    OdfStyle style = paragraphBaseElement.getDocumentStyle();
    int outlineLevel = getStyleOutlineLevel(style);

    if (style != null) {
      // if no outline Level was set, but there is a parentComponentElement
      if (outlineLevel == NO_OUTLINE_LEVEL) {
        OdfStyleBase parentStyle = null;
        style.getParentStyle();
        while (outlineLevel == NO_OUTLINE_LEVEL) {
          outlineLevel = getStyleOutlineLevel(parentStyle);
          if (outlineLevel != NO_OUTLINE_LEVEL) {
            break;
          }
          if (parentStyle instanceof OdfStyle) {
            parentStyle = ((OdfStyle) parentStyle).getParentStyle();
          } else {
            break;
          }
          if (parentStyle == null) {
            break;
          }
        }
      }
    }
    return outlineLevel;
  }

  private static final int NO_OUTLINE_LEVEL = -2;

  private static int getStyleOutlineLevel(OdfStyleBase style) {
    int outlineLevel = NO_OUTLINE_LEVEL;
    if (style != null) {
      String outlineLevelValue;
      if (style.hasAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "default-outline-level")) {
        outlineLevelValue =
            style.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "default-outline-level");
        if (outlineLevelValue != null && !outlineLevelValue.isEmpty()) {
          outlineLevel = Integer.parseInt(outlineLevelValue);
        }
      }
    }
    return outlineLevel;
  }

  /**
   * Exchanges a <text:p> element to a <text:h> when the paragraph receives an outline level. In
   * addition the c is always updated with the latest element reference.
   */
  private static TextParagraphElementBase ensureCorrectParagraphElement(
      int outlineLevel, TextParagraphElementBase rootElement, Component c) {
    TextParagraphElementBase newElement = null;
    if (outlineLevel > -1) {
      // switch to a heading
      if (!(rootElement instanceof TextHElement)) {
        newElement = new TextHElement((OdfFileDom) rootElement.getOwnerDocument());
        // addChild the outline level to the element
        ((TextHElement) newElement).setTextOutlineLevelAttribute(outlineLevel);
      } else {
        ((TextHElement) rootElement).setTextOutlineLevelAttribute(outlineLevel);
      }

      // } else if (outlineLevel == -1) {
      // The above should work, but is not supported by Frontend, issue?
    } else {
      // switch to a paragraph
      if (!(rootElement instanceof TextPElement)) {
        newElement = new TextPElement((OdfFileDom) rootElement.getOwnerDocument());
        // remove the outline level to the element
      }
      rootElement.removeAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "outline-level");
    }
    // if the type have to be changed, clone everything from the old to the new element
    if (newElement != null) {
      OdfElement parent = (OdfElement) rootElement.getParentNode();
      parent.replaceChild(newElement, rootElement);
      // copy/attributes and content
      OdfElement.cloneNode(rootElement, newElement, true);
      c.setRootElement(newElement);
      // adding back reference from element to c
      ((OdfElement) newElement).setComponent(c);
    } else {
      // adding back reference from element to c
      ((OdfElement) rootElement).setComponent(c);
      newElement = rootElement;
    }
    return newElement;
  }

  /**
   * Deletes all hyperlink and template styles from any span/anchor in this area * Extends the
   * recursive delete function, whenever a component was found within a span/anchor it will be moved
   * in front of it. Empty spans/anchor will be deleted!
   *
   * <p>ToDo: On demand cloning? Man traversiert den Baum durch und fuegt alles in ein temporaes
   * Element ein? Oder gleich an den Anfang des Paragraphen? splitParagraph muesste die gleiche
   * Funktionalitaet bekommen. Anchor wird nicht uebernommen!
   */
  private static void setParagraphListProperties(
      TextParagraphElementBase paragraphBaseElement,
      JSONObject paraProps,
      OdfFileDom xmlDoc,
      int listLevel,
      int newListLevel,
      String currentListStyleName) {
    // Taking the component, to update the root XML element at the end
    Component c = paragraphBaseElement.getComponent();
    // Will the listlevel change)
    if (listLevel != newListLevel) {
      // if there has been already a list
      if (listLevel > -1) {
        if (newListLevel > listLevel) {
          // raise the level of lists
          addListLevel(
              paragraphBaseElement,
              newListLevel - listLevel,
              paraProps,
              xmlDoc,
              currentListStyleName);
        } else {
          // lower the level of lists
          removeListLevel(
              paragraphBaseElement, listLevel - newListLevel, paraProps, currentListStyleName);
        }
      } else { // there is no list yet (level plus one as it start counting with 0)
        addListLevel(
            paragraphBaseElement, newListLevel + 1, paraProps, xmlDoc, currentListStyleName);
      }
    }
    // Add list style and xml id to the top most list element
    addTopListElementAttributes(
        getListRootElement(paragraphBaseElement), paraProps, null, currentListStyleName);
    // Updating the XML root element, after (possibly) exchanging it due to addition/less list-item
    // levels
    c.setRootElement(paragraphBaseElement);
  }

  /** ToDo after release: Move this to the paragraph of ODFDOM */
  public static int getListLevel(TextParagraphElementBase paragraphBaseElement) {
    return getListLevel(paragraphBaseElement, -1);
  }

  /** Count the ancestor <text:list> elements to receive the list level */
  private static int getListLevel(OdfElement element, int listLevel) {
    OdfElement parent = (OdfElement) element.getParentNode();
    if (parent instanceof TextListElement) {
      listLevel = getListLevel(parent, listLevel) + 1;
    } else if (parent instanceof TextListItemElement || parent instanceof TextListHeaderElement) {
      listLevel = getListLevel(parent, listLevel);
    }
    return listLevel;
  }

  /**
   * Isolates the paragraph as only paragraph of the list. For instance, required to be able to
   * addChild a component before/behind that paragraph in the DOM.
   *
   * @param paragraphBaseElement the paragraph within a list
   * @return the root text:list element of the list
   */
  public static TextListElement isolateListParagraph(
      TextParagraphElementBase paragraphBaseElement) {
    TextListElement rootListElement = null;
    Element parentElement = (Element) paragraphBaseElement.getParentNode();
    if (parentElement instanceof TextListItemElement
        || parentElement instanceof TextListHeaderElement) {
      // ToDo: To be moved to paragraph of ODFDOM
      int listLevel = JsonOperationConsumer.getListLevel(paragraphBaseElement);

      String listStyleName;
      rootListElement = getListRootElement(paragraphBaseElement);
      if (rootListElement != null) {
        listStyleName = rootListElement.getTextStyleNameAttribute();
        removeListLevel(paragraphBaseElement, listLevel + 1, null, null);
        TextListElement newRootListElement =
            addListLevel(
                paragraphBaseElement,
                listLevel + 1,
                null,
                (OdfFileDom) paragraphBaseElement.getOwnerDocument(),
                listStyleName);
        if (newRootListElement != null) {
          rootListElement = newRootListElement;
        }
      }
    }
    return rootListElement;
  }

  /**
   * Removal of list level starts bottom-up from the lowest list level. The reason is that by
   * component position only the selected paragraph can be found. The paragraph element is nested
   * within all list & list-items and has the deepest element position.
   *
   * <p>If there the paragraphs has preceding and following list content the list has to be split.
   * For this reason it the list will be cloned.
   *
   * <p>As xml:id attributes have to be kept and are not being cloned, the clone will become the
   * latter part and the original list the prior part.
   *
   * <p>From the cloned part all content from the beginning including the marked paragraph are
   * deleted.
   *
   * <p>From the original part all content after the paragraph is being deleted.
   *
   * <p>In addition if the paragraph is not the last, there has to be another clone. In the first
   * clone all following paragraphs are being deleted, in the other last all preceding paragraphs
   * are removed. The same procedure for paragraphs has to be undertaken for text:list-item &
   * text:list-head elements!
   */
  private static void removeListLevel(
      TextParagraphElementBase paragraphBaseElement,
      int levelsToDelete,
      JSONObject paraProps,
      String currentListStyleName) {
    // ToDo: Differentiate between list-header and list-item!!
    if (levelsToDelete > 0) {
      // Checks for preceding paragraphs, lists or list-items before the current list-item
      boolean hasPrecedingListContent =
          OdfElement.getPreviousSiblingElement(paragraphBaseElement) != null
              || OdfElement.getPreviousSiblingElement(paragraphBaseElement.getParentNode()) != null;
      // Checks for following paragraphs, lists or list-items after the current list-item
      boolean hasFollowingListContent =
          OdfElement.getNextSiblingElement(paragraphBaseElement) != null
              || OdfElement.getNextSiblingElement(paragraphBaseElement.getParentNode()) != null;

      // if the paragraph is not the first and nor the last component, get the root of the list to
      // clone the element
      // The parentComponentElement of the paragraph is either a <text:list-item> or
      // <text:list-header>, its parentComponentElement is the desired <text:list> element
      paragraphBaseElement.setAttribute("selectedParagraph", "true");
      OdfElement pContainer = (OdfElement) paragraphBaseElement.getParentNode();
      if (pContainer instanceof TextListHeaderElement) {
        paragraphBaseElement.setAttribute("hiddenParagraph", "true");
      }
      OdfElement lowestListElement = (OdfElement) pContainer.getParentNode();

      TextListElement clonedListWithFollowingContent = null;
      // if there has been content before the paragraph, clone this content
      if (hasPrecedingListContent) {
        if (hasFollowingListContent) {
          // *** CASE 3 ***: Dealing with following list content
          // clone current list for following content
          clonedListWithFollowingContent = (TextListElement) lowestListElement.cloneNode(true);
          Component selectedParagraphComponent = paragraphBaseElement.getComponent();
          int positionOfSelectedParagraph =
              selectedParagraphComponent.getParent().indexOf(selectedParagraphComponent);
          adjustComponentReferences(
              -1,
              positionOfSelectedParagraph,
              selectedParagraphComponent,
              clonedListWithFollowingContent);

          // only the FOLLOWING content is of interest, therefore delete everything BEFORE the
          // selected paragraph
          deleteBeforeAndSelectedParagraph(clonedListWithFollowingContent);
        }
        // *** CASE 1 & 3 ***: handling preceding list content
        // only the PRECEDING content is of interest, therefore delete everything AFTER the selected
        // paragraph (including the paragraph)
        deleteFromSelectedParagraph(lowestListElement);

        boolean listLabelHidden = false;
        // if there is no previous sibling and hidden, we need a list-header
        if (paraProps != null && paraProps.has("listLabelHidden")) {
          listLabelHidden = paraProps.optBoolean("listLabelHidden", false);
        }
        if (listLabelHidden) {
          OdfElement elementForInsertion;
          OdfElement newParentElement;

          // if there is no preceding content, we need to move the paragraph into a list-header
          // element
          if (hasPrecedingListContent) {
            // if the list item is shown the paragraph has to be in a list-item
            newParentElement = (OdfElement) lowestListElement.getParentNode().getParentNode();
            // Create new List Item
            TextListHeaderElement newListHeaderElement =
                new TextListHeaderElement((OdfFileDom) paragraphBaseElement.getOwnerDocument());
            newListHeaderElement.appendChild(paragraphBaseElement);
            elementForInsertion = newListHeaderElement;

          } else {
            // if the list item is hidden it will be placed stand alone (or in a list-header at list
            // beginning)
            newParentElement = (OdfElement) lowestListElement.getParentNode();
            // Create new List Item
            elementForInsertion = paragraphBaseElement;
          }
          OdfElement nextListSiblingElement = OdfElement.getNextSiblingElement(lowestListElement);
          // if we are at the end of the list
          if (nextListSiblingElement == null) {
            newParentElement.appendChild(elementForInsertion);
            if (clonedListWithFollowingContent != null) {
              newParentElement.appendChild(clonedListWithFollowingContent);
            }
          } else { // if we are in the middle of the list
            newParentElement.insertBefore(elementForInsertion, nextListSiblingElement);
            if (clonedListWithFollowingContent != null) {
              newParentElement.insertBefore(clonedListWithFollowingContent, nextListSiblingElement);
            }
          }
        } else {
          boolean isTopLevelList = false;
          OdfElement elementForInsertion;
          OdfElement newParentElement;

          // parentComponentElement is a list element
          // as if the list item is shown the paragraph has to be in a list-item
          OdfElement listItemPrecedingSibling = (OdfElement) lowestListElement.getParentNode();
          OdfElement listItemFollowingSibling;
          if (listItemPrecedingSibling instanceof TextListItemElement
              || listItemPrecedingSibling instanceof TextListHeaderElement) {
            newParentElement = (OdfElement) listItemPrecedingSibling.getParentNode();
            listItemFollowingSibling = OdfElement.getNextSiblingElement(listItemPrecedingSibling);
            // Create new List Item
            TextListItemElement newListItemElement =
                new TextListItemElement((OdfFileDom) paragraphBaseElement.getOwnerDocument());
            newListItemElement.appendChild(paragraphBaseElement);
            elementForInsertion = newListItemElement;
          } else {
            isTopLevelList = true;
            elementForInsertion = paragraphBaseElement;
            newParentElement = listItemPrecedingSibling;
            listItemFollowingSibling = OdfElement.getNextSiblingElement(lowestListElement);
          }

          //					OdfElement nextListSiblingElement =
          // OdfElement.getNextSiblingElement(lowestListElement);
          // if we are at the end of the list
          if (listItemFollowingSibling == null) {
            newParentElement.appendChild(elementForInsertion);
            if (clonedListWithFollowingContent != null) {
              newParentElement.appendChild(clonedListWithFollowingContent);
            }

            if (clonedListWithFollowingContent != null) {
              if (isTopLevelList) {
                // Create new List Item
                newParentElement.appendChild(clonedListWithFollowingContent);

              } else {
                // Create new List Item
                TextListItemElement newListItemElement =
                    new TextListItemElement((OdfFileDom) paragraphBaseElement.getOwnerDocument());
                newListItemElement.appendChild(clonedListWithFollowingContent);
                newParentElement.appendChild(newListItemElement);
              }
            }
          } else { // if we are in the middle of the list
            newParentElement.insertBefore(elementForInsertion, listItemFollowingSibling);
            if (clonedListWithFollowingContent != null) {
              if (isTopLevelList) {
                // Create new List Item
                newParentElement.insertBefore(
                    clonedListWithFollowingContent, listItemFollowingSibling);
              } else {
                // Create new List Item
                TextListItemElement newListItemElement =
                    new TextListItemElement((OdfFileDom) paragraphBaseElement.getOwnerDocument());
                newListItemElement.appendChild(clonedListWithFollowingContent);
                newParentElement.insertBefore(newListItemElement, listItemFollowingSibling);
              }
            }
          }
        }
      } else {
        if (hasFollowingListContent) {
          // *** CASE 2 ***: Ony following list content
          // the list is moved after the paragraph, remove the paragraph out of the list
          deleteBeforeAndSelectedParagraph(lowestListElement);
          Node listParent = lowestListElement.getParentNode();
          listParent.insertBefore(paragraphBaseElement, lowestListElement);
        } else {
          // *** CASE 0 ***: ALONE - the paragraph was alone in the list parentComponentElement,
          // just replace the paragraph with list
          Node listParent = lowestListElement.getParentNode();
          listParent.replaceChild(paragraphBaseElement, lowestListElement);
          lowestListElement = null;
        }
      }

      // **** FIXING STYLES
      // if list element is a top list element (this happens if the list element has not list
      // header/item as parentComponentElement)
      if (lowestListElement != null
          && !(lowestListElement.getParentNode() instanceof TextListItemElement)
          && !(lowestListElement.getParentNode() instanceof TextListHeaderElement)) {
        OdfElement previousSiblingElement = OdfElement.getPreviousSiblingElement(lowestListElement);
        String previousListStyleId = null;
        if (previousSiblingElement instanceof TextListElement) {
          previousListStyleId =
              ((TextListElement) previousSiblingElement).getTextStyleNameAttribute();
        }
        if (currentListStyleName == null) {
          currentListStyleName = ((TextListElement) lowestListElement).getTextStyleNameAttribute();
        }

        // Add list style and xml id to the top most list element
        addTopListElementAttributes(
            getListRootElement(paragraphBaseElement),
            paraProps,
            previousListStyleId,
            currentListStyleName);

        if (clonedListWithFollowingContent != null) {
          // parameter properties have to be zero, as the existing styles should take precedence
          addTopListElementAttributes(
              clonedListWithFollowingContent, null, previousListStyleId, currentListStyleName);
        }
      }
      // removing another pairs of <text:list> and <text:list-item> or <text:list-header>
      removeListLevel(paragraphBaseElement, --levelsToDelete, paraProps, currentListStyleName);
    } else {
      if (paragraphBaseElement.hasAttribute("selectedParagraph")) {
        paragraphBaseElement.removeAttribute("selectedParagraph");
      }
    }
  }

  private static void deleteBeforeAndSelectedParagraph(Element listElement) {
    listContentRemoval(listElement, true);
  }

  private static void deleteFromSelectedParagraph(Element listElement) {
    listContentRemoval(listElement, false);
  }

  /**
   * Loops through elements of a list. There can only be list-item/header children and within those
   * paragraphs. Within one paragraph an attribute was set. According to the given parameters the
   * content before and/or after this paragraph is being deleted
   */
  private static void listContentRemoval(Element listElement, boolean deleteBeforeSelection) {
    // deleteAfterSelection: All content after the selected paragraph in document order is being
    // deleted
    // deleteBeforeSelection: The opposite, only one of both modes exists, the selected paragraph is
    // always deleted
    boolean deleteAfterSelection = !deleteBeforeSelection;
    // searching backwards the list for the correct list item (to not corrupt the node list by
    // removing its content while iterating)
    boolean isAfterSelectedParagraph = false;
    boolean isSelectedParagraph = false;
    NodeList listChildren = ((TextListElement) listElement).getChildNodes();
    for (int i = listChildren.getLength() - 1; i >= 0; i--) {
      Node listItem = listChildren.item(i);
      // check if it is really a list item/header and not for instance a text line break
      if (listItem instanceof Element) {
        NodeList listItemParagraphs = listItem.getChildNodes();
        // as the remove items from the node list we need to count back..
        for (int j = listItemParagraphs.getLength() - 1; j >= 0; j--) {
          Node listItemParagraph = listItemParagraphs.item(j);
          // check if it is really a list item/header and not for instance a text line break
          if (listItemParagraph instanceof OdfElement) {
            // If selected paragraph not found yet
            if (!isAfterSelectedParagraph) {
              // check if the content element is the selected paragraph
              if (((OdfElement) listItemParagraph).hasAttribute("selectedParagraph")) {
                isAfterSelectedParagraph = true;
                isSelectedParagraph = true;
                // the selected character will always be removed
                listItem.removeChild(listItemParagraph);
                // if no deletion before the selection is required..
                if (!deleteBeforeSelection) {
                  // stop here..
                  break;
                }
              } else if (deleteAfterSelection) {
                // IF DESIRED.. delete earlier content
                listItem.removeChild(listItemParagraph);
              }
            } else if (deleteBeforeSelection) {
              // IF DESIRED.. delete LATER content
              listItem.removeChild(listItemParagraph);
            }
          } else if (listItemParagraph instanceof Text) {
            // removing indentation
            listItem.removeChild(listItemParagraph);
          }
        }

        if (isAfterSelectedParagraph && !deleteBeforeSelection) {

          //					// if the last content was removed..
          //					if (listItemParagraphs.getLength() == 0) {
          //						// remove the list itself
          //						Element parentComponentElement = (Element) listItem.getParentNode();
          //						parentComponentElement.removeChild(listItem);
          //					}
          if (!listItem.hasChildNodes()) {
            // remove the list itself
            Element parent = (Element) listItem.getParentNode();
            parent.removeChild(listItem);
          }
          break;
        } else {
          // only do anything if it is not the selected paragraph
          if (!isSelectedParagraph) {
            // ..if we are still after
            if (!isAfterSelectedParagraph) {
              // ..and if the deletion mode is correct..
              if (deleteAfterSelection) {
                // ..delete list item
                listElement.removeChild(listItem);
              } else {
                if (!listItem.hasChildNodes()) {
                  // remove the list itself
                  Element parent = (Element) listItem.getParentNode();
                  parent.removeChild(listItem);
                }
              }
              // else we are before and if the mode is correct..
            } else if (deleteBeforeSelection) {
              // delete list item
              listElement.removeChild(listItem);
            }
          } else {
            isSelectedParagraph = false;
            if (!listItem.hasChildNodes()) {
              // remove the list itself
              Element parent = (Element) listItem.getParentNode();
              parent.removeChild(listItem);
            }
          }
        }
      } else if (listItem instanceof Text) {
        // removing indentation
        listElement.removeChild(listItem);
      }
    }
  }

  /**
   * All paragraph references from the original list to components that occur paragraphs AFTER the
   * selected paragraph have to be moved to the clonedList paragraphs.
   *
   * @param selectedParagraphComponent Within this list, every paragraph holds a reference to its
   *     component and vice versa
   * @param clonedListElement Within this list, NO paragraph holds a reference to its component and
   *     vice versa *
   */
  private static int adjustComponentReferences(
      int currentParagraphPosition,
      int selectedParagraphPosition,
      Component selectedParagraphComponent,
      Element clonedListElement) {
    NodeList listChildren = ((TextListElement) clonedListElement).getChildNodes();
    for (int i = 0; i < listChildren.getLength(); i++) {
      Node listItem = listChildren.item(i);
      // check if it is really a list item/header and not for instance a text line break
      if (listItem instanceof Element) {
        NodeList listItemParagraphs = listItem.getChildNodes();
        // as the remove items from the node list we need to count back..
        for (int j = 0; j < listItemParagraphs.getLength(); j++) {
          Node listItemParagraph = listItemParagraphs.item(j);
          // check if it is really a list item/header and not for instance a text line break
          if (listItemParagraph instanceof OdfElement) {
            // If selected paragraph not found yet
            if (listItemParagraph instanceof TextParagraphElementBase) {
              if (currentParagraphPosition == -1) {
                if (((OdfElement) listItemParagraph).hasAttribute("selectedParagraph")) {
                  currentParagraphPosition = selectedParagraphPosition + 1;
                }
              } else if (currentParagraphPosition > selectedParagraphPosition) {

                //
                //								if(selectedParagraphComponent.mParent == null){
                //									System.out.println("x");
                //								}
                //
                //	if(selectedParagraphComponent.mParent.getChildNode(currentParagraphPosition) ==
                // null){
                //									System.out.println("yy");
                //								}
                // Add components to the paragraphs of the second part of the cloned list
                Component nextComponent =
                    ((OdfElement)
                            selectedParagraphComponent
                                .getParent()
                                .getChildNode(currentParagraphPosition))
                        .getComponent();
                nextComponent.setRootElement((TextParagraphElementBase) listItemParagraph);
                // adding back reference from element to component
                ((TextParagraphElementBase) listItemParagraph).setComponent(nextComponent);
                currentParagraphPosition++;
              }
            } else if (listItemParagraph instanceof TextListElement) {
              currentParagraphPosition =
                  adjustComponentReferences(
                      currentParagraphPosition,
                      selectedParagraphPosition,
                      selectedParagraphComponent,
                      (TextListElement) listItemParagraph);
            }
          }
        }
      }
    }
    return currentParagraphPosition;
  }

  /**
   * In theory the list style might be set on any text:list within the nested list-items/headers,
   * but all known offices (AO/LO/MSO) are consistently using the first style
   */
  private static TextListElement getListRootElement(TextParagraphElementBase paragraphBaseElement) {
    OdfElement root = null;
    OdfElement parent = (OdfElement) paragraphBaseElement.getParentNode();
    while (parent instanceof TextListElement
        || parent instanceof TextListItemElement
        || parent instanceof TextListHeaderElement) {
      root = parent;
      parent = (OdfElement) parent.getParentNode();
    }
    return (TextListElement) root;
  }

  /**
   * If a list already exists, new list level will be added inside of the existing list. In the
   * first step of the recursion only the paragraphBase will be replaced by a list, further list
   * will be added in the other addListLevel method recursive.
   */
  private static TextListElement addListLevel(
      TextParagraphElementBase paragraphBaseElement,
      int levelsToCreate,
      JSONObject paraProps,
      OdfFileDom xmlDoc,
      String currentListStyleName) {
    TextListElement newListElement = null;
    // ToDo: Differentiate between list-header and list-item!!
    if (levelsToCreate > 0) {
      OdfElement parent = (OdfElement) paragraphBaseElement.getParentNode();
      // Create new List
      newListElement = new TextListElement(xmlDoc);

      // if list element is a top list element (this happens if the list element has not list
      // header/item as parentComponentElement)
      if (!(parent instanceof TextListItemElement) && !(parent instanceof TextListHeaderElement)) {
        OdfElement previousSiblingElement =
            OdfElement.getPreviousSiblingElement(paragraphBaseElement);
        String previousListStyleId = null;
        if (previousSiblingElement instanceof TextListElement) {
          previousListStyleId =
              ((TextListElement) previousSiblingElement).getTextStyleNameAttribute();
        }
        // Add list style and xml id to the top most list element
        addTopListElementAttributes(
            newListElement, paraProps, previousListStyleId, currentListStyleName);
      }
      parent.replaceChild(newListElement, paragraphBaseElement);

      levelsToCreate--;
      if (levelsToCreate > 0) {
        // Create new List Item
        TextListItemElement newListItemElement = new TextListItemElement(xmlDoc);
        newListElement.appendChild(newListItemElement);
        newListItemElement.appendChild(paragraphBaseElement);
        addListLevel(paragraphBaseElement, levelsToCreate, paraProps, xmlDoc, currentListStyleName);
      } else {
        boolean listLabelHidden = false;
        // if either the selected paragraph should show no label, or nothing was mentioned and the
        // paragraph had previously no label, was a child of <text:list-heading>
        if (paraProps != null && paraProps.has("listLabelHidden")
            || paragraphBaseElement.hasAttribute("hiddenParagraph")) {
          if (paraProps != null) {
            listLabelHidden = paraProps.optBoolean("listLabelHidden", false);
          } else {
            listLabelHidden = true;
          }
          paragraphBaseElement.removeAttribute("hiddenParagraph");
        }
        if (listLabelHidden) {
          // Create new List Header
          TextListHeaderElement newListHeaderElement = new TextListHeaderElement(xmlDoc);
          newListElement.appendChild(newListHeaderElement);
          newListHeaderElement.appendChild(paragraphBaseElement);
        } else {
          // Create new List Item
          TextListItemElement newListItemElement = new TextListItemElement(xmlDoc);
          newListElement.appendChild(newListItemElement);
          newListItemElement.appendChild(paragraphBaseElement);
        }
      }
    }
    return newListElement;
  }

  /** List styles should currently only changed for the paragraph being selected. */
  private static void addTopListElementAttributes(
      TextListElement rootListElement,
      JSONObject paraProps,
      String previousSiblingListId,
      String currentListId) {
    // Make certain the paragraph is in an list of its own, as only
    if (rootListElement != null) {
      String newListXmlId;
      String newListStyleId;
      if (paraProps != null && paraProps.length() != 0 && paraProps.has("listXmlId")) {
        newListXmlId = paraProps.optString("listXmlId", null);
        if (newListXmlId != null) {
          if (!newListXmlId.isEmpty()) {
            // do not create an empty ID
            rootListElement.setXmlIdAttribute(newListXmlId);
          }
        } else {
          rootListElement.removeAttributeNS(OdfDocumentNamespace.XML.getUri(), "id");
        }
      }
      // If style or styleID provided are different to the existent
      if (paraProps != null && paraProps.length() != 0 && paraProps.has("listStyleId")) {
        newListStyleId = paraProps.optString("listStyleId", null);
        if (newListStyleId != null) {
          if (!newListStyleId.isEmpty()) {
            rootListElement.setTextStyleNameAttribute(newListStyleId);
          } else {
            // reuse the previous list style ID
            if (currentListId == null
                && previousSiblingListId != null
                && !previousSiblingListId.isEmpty()) {
              rootListElement.setTextStyleNameAttribute(newListStyleId);
            }
          }
        } else {
          rootListElement.removeAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name");
        }
      } else {
        if (currentListId == null
            && previousSiblingListId != null
            && !previousSiblingListId.isEmpty()) {
          rootListElement.setTextStyleNameAttribute(previousSiblingListId);
        } else if (currentListId != null && !currentListId.isEmpty()) {
          rootListElement.setTextStyleNameAttribute(currentListId);
        }
      }
      // Similar to MSO 15 ODF output & behavior, set by default the continue numbering to true
      rootListElement.setTextContinueNumberingAttribute(true);
    }
  }

  public static void addListStyle(String listStyleId, JSONObject listDefinition, OdfDocument doc) {
    try {
      // ToDo: Later in case a list was added to a header and footer a styles DOM have to be
      // accessed
      OdfOfficeAutomaticStyles autoStyles = doc.getContentDom().getOrCreateAutomaticStyles();
      OdfTextListStyle listStyle = autoStyles.newListStyle(listStyleId);
      JSONObject listLevelDefinition = null;
      for (int i = 0; i < 9; i++) {
        listLevelDefinition = listDefinition.optJSONObject("listLevel" + i);
        addListDefinition(listStyle, listLevelDefinition, i);
      }
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  /**
   * < text:list-level-style-bullet text:level="1" text:style-name="Bullet_20_Symbols"
   * text:bullet-char="\u2022"> <style:list-level-properties
   * text:list-level-position-and-space-mode="label-alignment"> <style:list-level-label-alignment
   * text:label-followed-by="listtab" text:list-tab-stop-position="1.27cm" fo:text-indent="-0.635cm"
   * fo:margin-left="1.27cm"/> </style:list-level-properties> </text:list-level-style-bullet>
   */
  private static void addListDefinition(
      OdfTextListStyle listStyle, JSONObject listLevelDefinition, int listLevel) {
    if (listLevelDefinition != null) {
      TextListLevelStyleElementBase listLevelStyle = null;

      // numberFormat: One of 'none', 'bullet', 'decimal', 'lowerRoman', 'upperRoman',
      // 'lowerLetter', or 'upperLetter'.
      String numberFormat = listLevelDefinition.optString("numberFormat");
      String levelText = listLevelDefinition.optString("levelText");
      String numPrefix = null;
      String numSuffix = null;
      if (numberFormat.equals("bullet")) {
        // if there is also a suffix appended
        if (levelText.length() > 1) {
          // num-suffix
          numSuffix = levelText.substring(1);
          // bullet-char
          levelText = levelText.substring(0, 1);
          // ToDo: API FIX to split prefix & suffix from bullet char, a single levelText will not be
          // able to round-trip
        }
        String levelPicBulletUri = listLevelDefinition.optString("levelPicBulletUri");
        if (levelText != null) { // if there is no text label, we have to create an image list
          if (levelPicBulletUri != null && !levelPicBulletUri.isEmpty()) {
            listLevelStyle = createListLevelStyleImage(listStyle, listLevel, levelPicBulletUri);
          } else {
            listLevelStyle = listStyle.newTextListLevelStyleBulletElement(levelText, listLevel + 1);
          }
        } else {
          listLevelStyle = createListLevelStyleImage(listStyle, listLevel, levelPicBulletUri);
        }
      } else { // *** NUMBERED LIST ***
        listLevelStyle =
            listStyle.newTextListLevelStyleNumberElement(getNumFormat(numberFormat), listLevel + 1);
        listLevelStyle.setAttributeNS(
            OdfDocumentNamespace.TEXT.getUri(),
            "text:display-levels",
            Integer.toString(countOccurrences(levelText, '%')));

        // if there is prefix
        if (!levelText.startsWith("%")) {
          // num-prefix
          int prefixEnd = levelText.indexOf('%');
          numPrefix = levelText.substring(0, prefixEnd);
          levelText = levelText.substring(prefixEnd);
        }
        // num-suffix
        int suffixStart = levelText.lastIndexOf('%') + 2;
        if (levelText.length() >= suffixStart) {
          numSuffix = levelText.substring(suffixStart);
        }
        int listStartValue = listLevelDefinition.optInt("listStartValue", -1);
        if (listStartValue != -1) {
          listLevelStyle.setAttributeNS(
              OdfDocumentNamespace.TEXT.getUri(),
              "text:start-value",
              Integer.toString(listStartValue));
        }
      }
      if (numPrefix != null && !numPrefix.isEmpty()) {
        listLevelStyle.setAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "style:num-prefix", numPrefix);
      }
      if (numSuffix != null && !numSuffix.isEmpty()) {
        listLevelStyle.setAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "style:num-suffix", numSuffix);
      }
      // Add common list style properties
      addCommonListStyles(listLevelStyle, listLevelDefinition);
    }
  }

  private static TextListLevelStyleElementBase createListLevelStyleImage(
      OdfTextListStyle listStyle, int listLevel, String levelPicBulletUri) {
    TextListLevelStyleElementBase listLevelStyle =
        listStyle.newTextListLevelStyleImageElement(listLevel + 1);

    if (levelPicBulletUri != null && !levelPicBulletUri.isEmpty()) {
      listLevelStyle.setAttributeNS(
          OdfDocumentNamespace.XLINK.getUri(), "xlink:href", levelPicBulletUri);
      TextListLevelStyleImageElement listLevelStyleImage =
          (TextListLevelStyleImageElement) listLevelStyle;
      listLevelStyleImage.setXlinkActuateAttribute("onLoad");
      listLevelStyleImage.setXlinkShowAttribute("embed");
      listLevelStyleImage.setXlinkTypeAttribute("simple");
    }
    return listLevelStyle;
  }

  private static int countOccurrences(String haystack, char needle) {
    int count = 0;
    for (int i = 0; i < haystack.length(); i++) {
      if (haystack.charAt(i) == needle) {
        count++;
      }
    }
    return count;
  }

  /**
   * The <style:list-level-properties> element has the following attributes:
   *
   * <ul>
   *   <li>fo:height
   *   <li>fo:text-align
   *   <li>fo:width
   *   <li>style:font-name
   *   <li>style:vertical-pos
   *   <li>style:vertical-rel
   *   <li>svg:y
   *   <li>text:list-level-position-and-space-mode
   *   <li>text:min-label-distance
   *   <li>text:min-label-width
   *   <li>text:space-before
   * </ul>
   */
  private static void addCommonListStyles(
      TextListLevelStyleElementBase listLevelStyle, JSONObject listLevelDefinition) {
    StyleListLevelPropertiesElement styleListLevelProperties =
        listLevelStyle.newStyleListLevelPropertiesElement();
    addListLabelAlignment(styleListLevelProperties, listLevelDefinition);

    if (listLevelDefinition.has("height")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.FO.getUri(),
          "fo:height",
          listLevelDefinition.optInt("height") / 100 + "mm");
    }
    if (listLevelDefinition.has("textAlign")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.FO.getUri(),
          "fo:text-align",
          listLevelDefinition.optString("textAlign"));
    }
    if (listLevelDefinition.has("width")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.FO.getUri(),
          "fo:width",
          listLevelDefinition.optInt("width") / 100 + "mm");
    }
    if (listLevelDefinition.has("font-name")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.STYLE.getUri(),
          "style:font-name",
          listLevelDefinition.optString("fontName"));
    }
    if (listLevelDefinition.has("vertical-pos")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.STYLE.getUri(),
          "style:vertical-pos",
          listLevelDefinition.optString("verticalPos"));
    }
    if (listLevelDefinition.has("vertical-rel")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.STYLE.getUri(),
          "style:vertical-rel",
          listLevelDefinition.optString("verticalRel"));
    }
    if (listLevelDefinition.has("y")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.SVG.getUri(), "svg:y", listLevelDefinition.optString("y"));
    }
    if (listLevelDefinition.has("listLevelPositionAndSpaceMode")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.TEXT.getUri(),
          "text:list-level-position-and-space-mode",
          listLevelDefinition.optString("listLevelPositionAndSpaceMode"));
    }
    if (listLevelDefinition.has("minLabelDistance")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.TEXT.getUri(),
          "text:min-label-distance",
          listLevelDefinition.optInt("minLabelDistance") / 100 + "mm");
    }
    if (listLevelDefinition.has("minLabelWidth")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.TEXT.getUri(),
          "text:min-label-width",
          listLevelDefinition.optInt("minLabelWidth") / 100 + "mm");
    }
    if (listLevelDefinition.has("spaceBefore")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.TEXT.getUri(),
          "text:space-before",
          listLevelDefinition.optInt("spaceBefore") / 100 + "mm");
    }
  }

  private static void addListLabelAlignment(
      StyleListLevelPropertiesElement styleListLevelProperties, JSONObject listLevelDefinition) {
    if (listLevelDefinition.has("indentLeft") || listLevelDefinition.has("indentFirstLine")) {
      styleListLevelProperties.setAttributeNS(
          OdfDocumentNamespace.TEXT.getUri(),
          "text:list-level-position-and-space-mode",
          "label-alignment");
      String labelFollowedBy = listLevelDefinition.optString("labelFollowedBy", "listtab");
      StyleListLevelLabelAlignmentElement listLevelLabelAlignmentElement =
          styleListLevelProperties.newStyleListLevelLabelAlignmentElement(labelFollowedBy);

      if (listLevelDefinition.has("indentLeft")) {
        int indentLeft = listLevelDefinition.optInt("indentLeft");
        listLevelLabelAlignmentElement.setFoMarginLeftAttribute(indentLeft / 100.0 + "mm");
      }
      if (listLevelDefinition.has("indentFirstLine")) {
        int indentFirstLine = listLevelDefinition.optInt("indentFirstLine");
        listLevelLabelAlignmentElement.setFoTextIndentAttribute(indentFirstLine / 100.0 + "mm");
      }
      if (listLevelDefinition.has("tabStopPosition")) {
        int tabStopPosition = listLevelDefinition.optInt("tabStopPosition");
        listLevelLabelAlignmentElement.setTextListTabStopPositionAttribute(
            tabStopPosition / 100.0 + "mm");
        listLevelLabelAlignmentElement.setTextLabelFollowedByAttribute("listtab");
      }
    }
  }

  /**
   * The style:num-format attribute specifies a numbering sequence. The defined values for the
   * style:num-format attribute are: 1: Hindu-Arabic number sequence starts with 1. a: number
   * sequence of lowercase Modern Latin basic alphabet characters starts with "a". A: number
   * sequence of uppercase Modern Latin basic alphabet characters starts with "A". i: number
   * sequence of lowercase Roman numerals starts with "i". I: number sequence of uppercase Roman
   * numerals start with "I". a value of type string. an empty string: no number sequence displayed.
   * If no value is given, no number sequence is displayed.
   */
  private static String getNumFormat(String numberFormat) {

    String numFormat = ""; // "none" is nothing set
    if (numberFormat.equals("decimal")) {
      numFormat = "1";
    } else if (numberFormat.equals("lowerRoman")) {
      numFormat = "i";
    } else if (numberFormat.equals("upperRoman")) {
      numFormat = "I";
    } else if (numberFormat.equals("lowerLetter")) {
      numFormat = "a";
    } else if (numberFormat.equals("upperLetter")) {
      numFormat = "A";
    }
    return numFormat;
  }

  /**
   * Splits a paragraph into two splitting the text among the two. Special handling for paragraph
   * within lists: In this case the list item will be split!
   */
  public static void splitParagraph(Component rootComponent, JSONArray start)
      throws IndexOutOfBoundsException {
    Component component = rootComponent.getParentOf(start);
    if (component != null) {
      TextParagraphElementBase para = (TextParagraphElementBase) component.getRootElement();
      try {
        TextParagraphElementBase newSecondPara =
            (TextParagraphElementBase) para.split(start.getInt(start.length() - 1));
        Component parentComponent = component.getParent();
        Node paraParent = component.getRootElement().getParentNode();

        // if paragraph within a list (checked on parentComponentElement of list-item or
        // list-header) the split will not only split the paragraph, but as well this item/header
        // BECAUSE: In contrary to ODF in OOXML the list is only a property of a paragraph, there
        // can only be one paragraph within a list (emulating this behavior).
        if (paraParent instanceof TextListItemElement
            || paraParent instanceof TextListHeaderElement) {
          OdfElement listItem = (OdfElement) paraParent;
          // split the list inbetween the old (splitted) and new paragraph, plus one as a new
          // paragraph was added above
          int precedingSiblingsNo = para.countPrecedingSiblingElements() + 1;
          OdfElement secondListPart = listItem.split(precedingSiblingsNo);
          Component.createChildComponent(
              start.getInt(start.length() - 2) + 1,
              parentComponent,
              secondListPart.getFirstChildElement());
        } else {
          Component.createChildComponent(
              start.getInt(start.length() - 2) + 1, parentComponent, newSecondPara);
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    } else {
      LOG.log(Level.SEVERE, "Could not find paragraph at position: {0}", start);
    }
  }

  private static void deleteText(Component rootComponent, JSONArray start, JSONArray end)
      throws IndexOutOfBoundsException {
    Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent != null) {
      // TextParagraphElementBase startParagraph = (TextParagraphElementBase)
      // parentComponent.getRootElement();
      try {
        // ToDo - addChild the paragraph text length to the paragraph? Than I need to intercept all
        // text manipulations!! *ARGH just do-it! :D
        // ToDo - parse the text spans positions from the beginning, creating array - could do this
        // from the beginning..
        // better not for very long texts it is unnecessary, does not scale
        //        adding the span depths, the span element reference, for every character?
        if (parentComponent instanceof TextContainer) {
          int startPos = start.getInt(start.length() - 1);
          int endPos;
          if (end != null) {
            endPos = end.getInt(end.length() - 1);
          } else {
            endPos = startPos;
          }
          ((TextContainer) parentComponent).removeText(startPos, endPos);
          // LO let the value attribute overrule the content, therefore this value have to vanish!
          OdfElement grandParentElement = (OdfElement) parentComponent.mRootElement.getParentNode();
          if (grandParentElement instanceof TableTableCellElement) {
            ((TableTableCellElement) grandParentElement)
                .removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "value");
            ((TableTableCellElement) grandParentElement)
                .removeAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "value-type");
          }
        } else {
          LOG.log(
              Level.SEVERE, "The parent of the text is not a text component: {0}", parentComponent);
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    } else {
      LOG.log(Level.SEVERE, "Could not find paragraph as parent for position: {0}", start);
    }
  }

  private static void addFontData(
      OdfDocument doc,
      String fontName,
      String[] altNames,
      String family,
      String familyGeneric,
      String pitch,
      String panose1) {
    // CREATING CONTENT FONT DECLARATION
    try {
      OdfContentDom contentDom = doc.getContentDom();
      StyleFontFaceElement newElement = new StyleFontFaceElement(contentDom);
      newElement.setStyleNameAttribute(fontName);
      if (family != null && !family.isEmpty()) {
        newElement.setSvgFontFamilyAttribute(family);
      }
      if (familyGeneric != null && !familyGeneric.isEmpty()) {
        newElement.setStyleFontFamilyGenericAttribute(familyGeneric);
      }
      if (pitch != null && !pitch.isEmpty()) {
        newElement.setStyleFontPitchAttribute(pitch);
      }
      if (panose1 != null && !panose1.isEmpty()) {
        if (panose1.contains("[")) {
          panose1 = panose1.substring(1, panose1.length() - 1);
        }
        if (panose1.contains(",")) {
          panose1 = panose1.replace(',', ' ');
        }
        newElement.setSvgPanose1Attribute(panose1);
      }
      addtFontFace(contentDom, newElement);

      OdfStylesDom stylesDom = doc.getStylesDom();
      StyleFontFaceElement newElement2 = (StyleFontFaceElement) newElement.cloneNode(true);
      newElement2 = (StyleFontFaceElement) stylesDom.adoptNode(newElement2);
      addtFontFace(stylesDom, newElement2);

    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  private static void addtFontFace(OdfFileDom fileDom, StyleFontFaceElement fontFaceElement) {
    NodeList fontDecls = fileDom.getElementsByTagName("office:font-face-decls");
    if (fontDecls.getLength() == 0) {
      Element rootElement = fileDom.getRootElement();
      NodeList rootChildren = rootElement.getChildNodes();
      boolean hasInserted = false;
      OfficeFontFaceDeclsElement fontFaceDeclsElement =
          fontFaceDeclsElement = new OfficeFontFaceDeclsElement(fileDom);
      fontFaceDeclsElement.appendChild(fontFaceElement);
      for (int i = 0; i < rootChildren.getLength(); i++) {
        Node currentNode = rootChildren.item(i);
        if (currentNode instanceof Element) {
          String elementName = ((Element) currentNode).getNodeName();
          if (elementName.equals("office:automatic-styles")
              || elementName.equals("office:styles")
              || elementName.equals("office:body")
              || elementName.equals("office:master-styles")) {
            rootElement.insertBefore(fontFaceDeclsElement, currentNode);
            hasInserted = true;
            break;
          }
        } else {
          continue;
        }
      }
      if (!hasInserted) {
        rootElement.appendChild(fontFaceDeclsElement);
      }
    } else {
      OfficeFontFaceDeclsElement fontFaceDeclsElement =
          (OfficeFontFaceDeclsElement) fontDecls.item(0);
      fontFaceDeclsElement.appendChild(fontFaceElement);
    }
  }

  /** Currently all fields are being mapped to a common text field, i.e. <text:text-input> */
  public static void setFieldAttributes(
      OdfElement newFieldElement,
      JSONObject attrs,
      final FieldMap currentMap,
      final OdfContentDom contentDom) {
    if (newFieldElement != null && currentMap != null && attrs != null && attrs.has("field")) {
      try {
        JSONObject fieldAttrs = attrs.getJSONObject("field");
        String styleName = null;
        if (currentMap.hasDateFormat() && fieldAttrs.has("dateFormat")) {
          boolean isTime = currentMap.hasTimeStyle();
          String dateFormat = fieldAttrs.getString("dateFormat");
          OdfOfficeAutomaticStyles autoStyles = contentDom.getOrCreateAutomaticStyles();
          Iterator styleIter = null;
          if (isTime) {
            styleIter = autoStyles.getTimeStyles().iterator();
          } else {
            styleIter = autoStyles.getDateStyles().iterator();
          }

          int newStyleIdx = 0;
          while (styleIter.hasNext()) {
            OdfElement style = (OdfElement) styleIter.next();
            String currentName =
                isTime
                    ? ((OdfNumberTimeStyle) style).getStyleNameAttribute()
                    : ((OdfNumberDateStyle) style).getStyleNameAttribute();
            if (styleName == null
                && (isTime
                    ? ((OdfNumberTimeStyle) style).getFormat(true).equals(dateFormat)
                    : ((OdfNumberDateStyle) style).getFormat(true).equals(dateFormat))) {
              styleName = currentName;
              break;
            }
            if (currentName.substring(0, 1).equals("N")) {
              String numString = currentName.substring(1, currentName.length());
              try {
                int oldIdx = Integer.parseInt(numString);
                newStyleIdx = Math.max(newStyleIdx, oldIdx);
              } catch (NumberFormatException e) {
                LOG.log(Level.INFO, null, e);
              }
            }
          }
          OdfFileDom fileDom = contentDom;
          if (styleName == null) {

            // create new style
            autoStyles.getParentNode();
            styleName = "N" + (newStyleIdx + 1);
            OdfElement newStyle = null;
            if (isTime) {
              newStyle = new OdfNumberTimeStyle(fileDom, dateFormat, styleName);

            } else {
              newStyle = new OdfNumberDateStyle(fileDom, dateFormat, styleName);
            }

            // TODO: what makes an ODF date style not having automatic order?
            newStyle.setAttributeNS(
                OdfDocumentNamespace.NUMBER.getUri(), "number:automatic-order", "true");
            autoStyles.appendChild(newStyle);
          }
          newFieldElement.setAttributeNS(
              OdfDocumentNamespace.STYLE.getUri(), "style:data-style-name", styleName);
        }
        newFieldElement.setAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "style:data-style-name", styleName);
        String textUri = OdfDocumentNamespace.TEXT.getUri();
        String officeUri = OdfDocumentNamespace.OFFICE.getUri();
        String styleUri = OdfDocumentNamespace.STYLE.getUri();
        if (currentMap.hasDateValue() && fieldAttrs.has("dateValue")) {
          newFieldElement.setAttributeNS(
              textUri, "text:date-value", fieldAttrs.getString("dateValue"));
        }
        if (currentMap.hasFixed() && fieldAttrs.has("fixed")) {
          newFieldElement.setAttributeNS(
              textUri, "text:fixed", Boolean.valueOf(fieldAttrs.getBoolean("fixed")).toString());
        }
        if (currentMap.hasdbName() && fieldAttrs.has("dbName")) {
          newFieldElement.setAttributeNS(
              textUri, "text:database-name", fieldAttrs.getString("dbName"));
        }
        if (currentMap.hasTableType() && fieldAttrs.has("typeType")) {
          newFieldElement.setAttributeNS(
              textUri, "text:table-type", fieldAttrs.getString("tableType"));
        }
        if (currentMap.hasdbTable() && fieldAttrs.has("dbTable")) {
          newFieldElement.setAttributeNS(
              textUri, "text:table-name", fieldAttrs.getString("dbTable"));
        }
        if (currentMap.hasdbColumn() && fieldAttrs.has("dbColumn")) {
          newFieldElement.setAttributeNS(
              textUri, "text:column-name", fieldAttrs.getString("dbColumn"));
        }
        if (currentMap.hasDisplay() && fieldAttrs.has("display")) {
          newFieldElement.setAttributeNS(textUri, "text:display", fieldAttrs.getString("display"));
        }
        if (currentMap.hasRefFormat() && fieldAttrs.has("refFormat")) {
          newFieldElement.setAttributeNS(
              textUri, "text:reference-format", fieldAttrs.getString("refFormat"));
        }
        if (currentMap.hasRefName() && fieldAttrs.has("refName")) {
          newFieldElement.setAttributeNS(textUri, "text:ref-name", fieldAttrs.getString("refName"));
        }
        if (currentMap.hasOutlinelevel() && fieldAttrs.has("outlineLevel")) {
          newFieldElement.setAttributeNS(
              textUri, "text:outline-level", fieldAttrs.getString("outlineLevel"));
        }
        if (currentMap.hasPageNumFormat() && fieldAttrs.has("pageNumFormat")) {
          newFieldElement.setAttributeNS(
              styleUri, "style:num-format", fieldAttrs.getString("pageNumFormat"));
        }
        if (currentMap.hasNumLetterSync() && fieldAttrs.has("numLetterSync")) {
          newFieldElement.setAttributeNS(
              styleUri, "style:num-letter-sync", fieldAttrs.getString("numLetterSync"));
        }
        if (currentMap.hasCondition() && fieldAttrs.has("condition")) {
          newFieldElement.setAttributeNS(
              textUri, "text:condition", fieldAttrs.getString("condition"));
        }
        if (currentMap.hasCurrentValue() && fieldAttrs.has("currentValue")) {
          newFieldElement.setAttributeNS(
              textUri, "text:current-value", fieldAttrs.getString("currentValue"));
        }
        if (currentMap.hasFalseValue() && fieldAttrs.has("falseValue")) {
          newFieldElement.setAttributeNS(
              textUri, "text:string-value-if-false", fieldAttrs.getString("falseValue"));
        }
        if (currentMap.hasTrueValue() && fieldAttrs.has("trueValue")) {
          newFieldElement.setAttributeNS(
              textUri, "text:string-value-if-true", fieldAttrs.getString("trueValue"));
        }
        if (currentMap.hasConnectionName() && fieldAttrs.has("connectionName")) {
          newFieldElement.setAttributeNS(
              textUri, "text:connection-name", fieldAttrs.getString("connectionName"));
        }
        if (currentMap.hasDuration() && fieldAttrs.has("duration")) {
          newFieldElement.setAttributeNS(
              textUri, "text-duration", fieldAttrs.getString("duration"));
        }
        if (currentMap.hasName() && fieldAttrs.has(OPK_NAME)) {
          newFieldElement.setAttributeNS(textUri, "text:name", fieldAttrs.getString(OPK_NAME));
        }
        if (currentMap.hasBoolValue() && fieldAttrs.has("boolValue")) {
          newFieldElement.setAttributeNS(
              officeUri, "office:boolean-value", fieldAttrs.getString("boolValue"));
        }
        if (currentMap.hasCurrency() && fieldAttrs.has("currency")) {
          newFieldElement.setAttributeNS(
              officeUri, "office:currency", fieldAttrs.getString("currency"));
        }
        if (currentMap.hasStringValue() && fieldAttrs.has("stringValue")) {
          newFieldElement.setAttributeNS(
              officeUri, "office:value", fieldAttrs.getString("stringValue"));
        }
        if (currentMap.hasTimeValue() && fieldAttrs.has("timeValue")) {
          newFieldElement.setAttributeNS(
              textUri, "text:time-value", fieldAttrs.getString("timeValue"));
        }
        if (currentMap.hasTValue() && fieldAttrs.has("value")) {
          newFieldElement.setAttributeNS(officeUri, "text:value", fieldAttrs.getString("value"));
        }
        if (currentMap.hasOValue() && fieldAttrs.has("value")) {
          newFieldElement.setAttributeNS(officeUri, "office:value", fieldAttrs.getString("value"));
        }
        if (currentMap.hasValueType() && fieldAttrs.has("valueType")) {
          newFieldElement.setAttributeNS(
              officeUri, "office:value-type", fieldAttrs.getString("valueType"));
        }
        if (currentMap.hasFormula() && fieldAttrs.has("formula")) {
          newFieldElement.setAttributeNS(textUri, "text:formula", fieldAttrs.getString("formula"));
        }
        if (currentMap.hasIsHidden() && fieldAttrs.has("isHidden")) {
          newFieldElement.setAttributeNS(textUri, "text:hidden", fieldAttrs.getString("isHidden"));
        }
        if (currentMap.hasId() && fieldAttrs.has(OPK_ID)) {
          newFieldElement.setAttributeNS(
              OdfDocumentNamespace.XML.getUri(), "xml:id", fieldAttrs.getString(OPK_ID));
        }
        if (currentMap.hasDescription() && fieldAttrs.has("description")) {
          newFieldElement.setAttributeNS(
              textUri, "text:description", fieldAttrs.getString("description"));
        }
        if (currentMap.hasActive() && fieldAttrs.has("active")) {
          newFieldElement.setAttributeNS(textUri, "text:active", fieldAttrs.getString("active"));
        }
        if (currentMap.hasHref() && fieldAttrs.has("href")) {
          newFieldElement.setAttributeNS(
              OdfDocumentNamespace.XLINK.getUri(), "xlink:href", fieldAttrs.getString("href"));
        }
        if (currentMap.hasPlaceHolderType() && fieldAttrs.has("placeHolderType")) {
          newFieldElement.setAttributeNS(
              textUri, "text:placeholder-type", fieldAttrs.getString("placeHolderType"));
        }
        if (currentMap.hasKind() && fieldAttrs.has("kind")) {
          newFieldElement.setAttributeNS(textUri, "text:kind", fieldAttrs.getString("kind"));
        }
        if (currentMap.hasLanguage() && fieldAttrs.has("language")) {
          newFieldElement.setAttributeNS(
              OdfDocumentNamespace.SCRIPT.getUri(),
              "script:language",
              fieldAttrs.getString("language"));
        }
        if (currentMap.hasLinkType() && fieldAttrs.has("linkType")) {
          newFieldElement.setAttributeNS(
              OdfDocumentNamespace.XLINK.getUri(), "xlink:type", fieldAttrs.getString("linkType"));
        }
        if (currentMap.hasNumFormat() && fieldAttrs.has("numFormat")) {
          newFieldElement.setAttributeNS(
              styleUri, "style:num-format", fieldAttrs.getString("numFormat"));
        }
        if (currentMap.hasPageAdjust() && fieldAttrs.has("pageAdjust")) {
          newFieldElement.setAttributeNS(
              textUri, "text:page-adjust", fieldAttrs.getString("pageAdjust"));
        }
        if (currentMap.hasRowNumber() && fieldAttrs.has("rowNumber")) {
          newFieldElement.setAttributeNS(
              textUri, "text:row-number", fieldAttrs.getString("rowNumber"));
        }
      } catch (JSONException e) {
        LOG.log(Level.SEVERE, null, e);
      }
    }
  }

  /** Currently all fields are being mapped to a common text field, i.e. <text:text-input> */
  public static void addField(
      Component rootComponent,
      OdfContentDom contentDom,
      JSONArray start,
      String type,
      String representation,
      JSONObject attrs) {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      // CREATING NEW ROOT ELEMENT
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      OdfElement newFieldElement = null;
      FieldMap currentMap = FieldMap.fieldMap.get(type);
      if (currentMap != null) {
        Class<OdfElement> fieldClass;
        try {
          fieldClass = (Class<OdfElement>) Class.forName(currentMap.getClassName());
          if (fieldClass != null) {
            Class[] types = {OdfFileDom.class};
            Constructor<OdfElement> constructor = fieldClass.getConstructor(types);
            newFieldElement = constructor.newInstance(xmlDoc);
          }
        } catch (InstantiationException | ClassNotFoundException | SecurityException | NoSuchMethodException | InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
          LOG.log(Level.SEVERE, null, e);
        }
        setFieldAttributes(newFieldElement, attrs, currentMap, contentDom);
        if (representation != null) {
          Text text = xmlDoc.createTextNode(representation);
          newFieldElement.appendChild(text);
        }
        try {
          // ADDING COMPONENT
          addElementAsComponent(parentComponent, newFieldElement, start.getInt(start.length() - 1));
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  /** Modify field at the given position <text:text-input> */
  public static void changeField(
      Component rootComponent,
      OdfContentDom contentDom,
      JSONArray start,
      String type,
      String representation,
      JSONObject attrs) {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      int targetPos = start.optInt(start.length() - 1);
      Node targetNode = parentComponent.getChildNode(targetPos);
      if (targetNode != null) {
        LOG.log(Level.SEVERE, "node found");
        if (representation != null) {
          targetNode.setTextContent(representation);
        }
        if (type != null) {
          OdfElement element = (OdfElement) targetNode;
          FieldMap currentMap = FieldMap.fieldMap.get(type);
          setFieldAttributes(element, attrs, currentMap, contentDom);
          if (Component.isField(targetNode.getNamespaceURI(), targetNode.getLocalName())) {
            LOG.log(Level.SEVERE, "field found");
          }
        } else if (attrs != null) {
          JsonOperationConsumer.format(rootComponent, start, start, attrs);
        }
      }
    }
  }

  public static void addDrawing(
      Component rootComponent,
      JSONArray start,
      JSONObject attrs,
      String type,
      CollabTextDocument opsDoc)
      throws IndexOutOfBoundsException, JSONException {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      boolean isImageFrame = (type != null && type.equals("image"));
      // CREATING NEW ROOT ELEMENT
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      DrawShapeElementBase frameElement = null;
      DrawImageElement imageElement = null;
      JSONObject drawingProps = null;
      int alternativeNo = -1;
      if (attrs != null) {
        drawingProps = attrs.optJSONObject("drawing");
        if (drawingProps == null) {
          drawingProps = new JSONObject();
          attrs.put("drawing", drawingProps);
        }
        if (!drawingProps.has("inline")) {
          drawingProps.put("inline", true);
        }
        if (drawingProps != null && drawingProps.has("viewAlternative")) {
          try {
            alternativeNo = drawingProps.optInt("viewAlternative");
            frameElement =
                (DrawFrameElement) parentComponent.getChildNode(start.getInt(start.length() - 1));
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (type.equals("group")) {
          frameElement = new DrawGElement(xmlDoc);
        } else {
          frameElement = new DrawFrameElement(xmlDoc);
          if (isImageFrame) {
            final JSONObject imageProps = attrs.optJSONObject("image");
            imageElement = ((DrawFrameElement) frameElement).newDrawImageElement();
            imageElement.setXlinkTypeAttribute("simple");
            imageElement.setXlinkShowAttribute("embed");
            imageElement.setXlinkActuateAttribute("onLoad");
            if (imageProps != null) {
              setImageProperties(imageElement, imageProps);
            }
          }
        }
        if (frameElement != null) {
          setFrameProperties(frameElement, attrs);
        }
        // ADDING STYLES TO THE NEW ELEMENT
        try { // TODO: at the moment only one type of shapes can be inserted - a text frame, later a
          // detection of non-textframes is required here
          if (
          /*type.equals("shape") && */ !attrs.has(OPK_STYLE_ID)) {
            attrs.put(OPK_STYLE_ID, "default");
          }
        } catch (JSONException e) {
        }
        addStyle(attrs, frameElement, xmlDoc);
      }

      // INSERTING THE IMAGE or text frame
      try {
        if (frameElement != null) {
          if (alternativeNo == -1) {
            // ADDING COMPONENT
            addElementAsComponent(parentComponent, frameElement, start.getInt(start.length() - 1));
            DrawTextBoxElement textBoxElement = new DrawTextBoxElement(xmlDoc);
            frameElement.insertBefore(textBoxElement, null);
          } else if (imageElement != null) {
            NodeList frameChildren = frameElement.getChildNodes();
            int imagesFound = 0;
            Node followingImage = null;
            for (int i = 0; i < frameChildren.getLength(); i++) {
              Node node = frameChildren.item(i);
              if (node instanceof DrawImageElement) {
                imagesFound++;
              }
              if (imagesFound > alternativeNo) {
                followingImage = node;
                break;
              }
            }
            // if followingImage is null, the imageElement will be appended (see API)
            frameElement.insertBefore(imageElement, followingImage);
          }
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }

      // INSERTING THE IMAGE TO THE ZIP
      String packagePath = null;
      final JSONObject imageProps = attrs.optJSONObject("image");
      if (imageProps != null) {
        Object imageUrl = imageProps.opt("imageUrl");
        if (imageUrl != null && imageUrl instanceof String) {
          packagePath = (String) imageUrl;
        }
      }
      if (packagePath != null && imageElement != null && imageElement instanceof OdfDrawImage) {
        if (packagePath.contains("uid")) {
          int uidStart = packagePath.indexOf("uid") + 3;
          if (uidStart != 3
              && uidStart < packagePath.length()
              && uidStart < packagePath.indexOf('.')) {
            String uidString = null;
            if (packagePath.contains(".")) {
              uidString = packagePath.substring(uidStart, packagePath.indexOf('.'));
            } else {
              uidString = packagePath.substring(uidStart);
            }
            if (uidString != null) {
              long uid = Long.parseLong(uidString, 16);
              Map<Long, byte[]> resourceMap = opsDoc.getResourceMap();
              if (resourceMap != null) {
                byte[] fileBytes = resourceMap.get(uid);
                if (fileBytes != null) {
                  OdfDrawImage image = (OdfDrawImage) imageElement;
                  try {
                    image.newImage(
                        fileBytes, packagePath, OdfFileEntry.getMediaTypeString(packagePath));
                  } catch (Exception ex) {
                    LOG.log(Level.SEVERE, null, ex);
                  }
                }
              }
            }
          } else {
            LOG.severe("No appropriate resource URL found for picture: " + packagePath);
          }
        }
      }
    }
  }

  private static void setImageProperties(DrawImageElement imageElement, JSONObject imageProps) {
    if (imageProps.has("imageUrl")) {
      String href = imageProps.optString("imageUrl");
      if (href != null && !href.isEmpty()) {
        imageElement.setXlinkHrefAttribute(href);
      }
    } else if (imageProps.has("imageData")) {
      String imageData = imageProps.optString("imageData");
      if (imageData != null && !imageData.isEmpty()) {
        // expected header is
        //		"data:image/png;base64,
        String[] header = imageData.split("base64,");
        String suffix = "png";
        String mediaTypeString = "image/png";
        try {
          mediaTypeString = header[0].substring(header[0].indexOf(":") + 1, header[0].indexOf(";"));
          suffix = header[0].substring(header[0].indexOf("/") + 1, header[0].indexOf(";"));
        } catch (Throwable t) {
          // don't worry if the header is not as expected
          LOG.finer("BASE64 is not as expected '" + header[0] + "', the exception:" + t);
        }
        String fileName = "img_" + new Random().nextInt() + "." + suffix;
        Base64Binary base64Binary = Base64Binary.valueOf(header[1]);
        OdfPackage pkg = ((OdfFileDom) imageElement.getOwnerDocument()).getDocument().getPackage();
        // args: fileBytes, internalPath, mediaTypeString) {
        pkg.insert(base64Binary.getBytes(), "Pictures/" + fileName, mediaTypeString);
        imageElement.setXlinkHrefAttribute("Pictures/" + fileName);
      }
    }
    if (imageProps.has("imageXmlId")) {
      String xmlId = imageProps.optString("imageXmlId");
      if (xmlId != null && !xmlId.isEmpty()) {
        imageElement.setXmlIdAttribute(xmlId);
      }
    }
  }

  private static void setFrameProperties(DrawShapeElementBase frameElement, JSONObject attrs) {
    JSONObject drawingProps = attrs.optJSONObject("drawing");
    if (drawingProps != null) {
      if (drawingProps.has("width")) {
        int width = drawingProps.optInt("width");
        frameElement.setOdfAttributeValue(
            OdfName.newName(OdfDocumentNamespace.SVG, OdfGraphicProperties.Width.toString()),
            width / 100.0 + "mm");
      }
      if (drawingProps.has("height")) {
        int height = drawingProps.optInt("height");
        frameElement.setOdfAttributeValue(
            OdfName.newName(OdfDocumentNamespace.SVG, OdfGraphicProperties.Height.toString()),
            height / 100.0 + "mm");
      }
      if (drawingProps.has(OPK_NAME)) {
        String name = drawingProps.optString(OPK_NAME);
        if (name != null && !name.isEmpty()) {
          frameElement.setDrawNameAttribute(name);
        }
      }
      if (drawingProps.has("anchorHorOffset")) {
        int x = drawingProps.optInt("anchorHorOffset");
        frameElement.setOdfAttributeValue(
            OdfName.newName(OdfDocumentNamespace.SVG, OdfGraphicProperties.X.toString()),
            x / 100.0 + "mm");
      }
      if (drawingProps.has("anchorVertOffset")) {
        int y = drawingProps.optInt("anchorVertOffset");
        frameElement.setOdfAttributeValue(
            OdfName.newName(OdfDocumentNamespace.SVG, OdfGraphicProperties.Y.toString()),
            y / 100.0 + "mm");
      }
      if (drawingProps.has("inline")
          && !drawingProps.get("inline").equals(JSONObject.NULL)
          && drawingProps.optBoolean("inline")) {
        frameElement.setTextAnchorTypeAttribute("as-char");
      } else if (drawingProps.has("anchorHorBase") && drawingProps.has("anchorVertBase")) {
        String anchorHorBase = drawingProps.optString("anchorHorBase");
        String anchorVertBase = drawingProps.optString("anchorVertBase");
        if (anchorHorBase != null && anchorVertBase != null) {
          if (anchorHorBase.equals("page") && anchorVertBase.equals("page")) {
            frameElement.setTextAnchorTypeAttribute("paragraph");

          } else if (anchorHorBase.equals("column") && anchorVertBase.equals("margin")) {
            frameElement.setTextAnchorTypeAttribute("frame");

          } else if (anchorHorBase.equals("column") && anchorVertBase.equals("paragraph")) {
            frameElement.setTextAnchorTypeAttribute("paragraph");
            // apply related default wrapping, if not part of the attributes:
            if (!drawingProps.has("textWrapMode") && !drawingProps.has("textWrapSide")) {
              try {
                drawingProps.put("textWrapMode", "topAndBottom");
              } catch (JSONException e) {
                // no handline required
              }
            }
          } else if (anchorHorBase.equals("character") && anchorVertBase.equals("paragraph")) {
            frameElement.setTextAnchorTypeAttribute("char");
          } else { // the default is "inline" a
            frameElement.setTextAnchorTypeAttribute("as-char");
          }
        }
      } else {
        if (drawingProps.has("anchorHorBase")) {
          String anchorHorBase = drawingProps.optString("anchorHorBase");
          if (anchorHorBase != null) {
            if (anchorHorBase.equals("page")) {
              frameElement.setTextAnchorTypeAttribute("page");

            } else if (anchorHorBase.equals("column")) {
              frameElement.setTextAnchorTypeAttribute("paragraph");

            } else if (anchorHorBase.equals("character")) {
              frameElement.setTextAnchorTypeAttribute("char");
            }
          }
        }
      }
      if (drawingProps.has("anchorLayerOrder")) {
        int anchorLayerOrder = drawingProps.optInt("anchorLayerOrder", 0);
        frameElement.setDrawZIndexAttribute(anchorLayerOrder);
      }
    }
    JSONObject shapeProps = attrs.optJSONObject("shape");
    if (shapeProps != null) {
      if (shapeProps.has("autoResizeHeight") && shapeProps.optBoolean("autoResizeHeight") == true) {
        frameElement.removeAttribute("height");
      }
    }
  }

  /**
   * @param rootComponent high level document structure
   * @param start position of the tableElement starting with 0 and one over the existing is allowed
   * @throws IndexOutOfBoundsException - if index is out of range (index < 0 || index > size()). One
   *     over size is allowed to append a tableElement.
   */
  public static void addTable(
      Component rootComponent,
      JSONArray start,
      JSONObject attrs,
      JSONObject sizeExceeded,
      String tableName)
      throws IndexOutOfBoundsException {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);

    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      OdfStylableElement newTableElement;
      if (sizeExceeded == null) {
        // CREATING NEW ROOT ELEMENT
        newTableElement = new TableTableElement(xmlDoc);
        if (tableName != null && !tableName.isEmpty()) {
          ((TableTableElement) newTableElement).setTableNameAttribute(tableName);
        }
        if (attrs == null) {
          attrs = new JSONObject();
          try {
            attrs.put("visible", true);
          } catch (JSONException e) {
          }
        }
        if (attrs != null) {
          // ADDING STYLES TO THE NEW ELEMENT
          addStyle(attrs, newTableElement, xmlDoc);
          JSONObject tableProps = attrs.optJSONObject("table");
          if (tableProps != null) {
            JSONArray tableGrid = tableProps.optJSONArray("tableGrid");
            if (tableGrid != null) {
              addNewColumns((TableTableElement) newTableElement, tableGrid);
            }
          }
        }
        try {
          // ADDING TABLE COMPONENT
          addElementAsComponent(parentComponent, newTableElement, start.getInt(start.length() - 1));
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else {
        // No tableElement, just writing a paragraph with an error
        newTableElement = new TextPElement(xmlDoc);
        newTableElement.appendChild(xmlDoc.createTextNode("This table was too large!"));
        try {
          // ADDING COMPONENT
          addElementAsComponent(parentComponent, newTableElement, start.getInt(start.length() - 1));
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  /*
      Current implementation only supports the annotation of paragraphs (perhaps other components), by adding the annotation as component into the document beyond the paragraph.
      But the annotation will never allow the annotation of text, as text is a leave and can not have an annotation beyond!
      Or shall style info be beyond a character?

      See content.xml of text-extract.odt

      <text:p text:style-name="P6">ODFDOM <text:span text:style-name="T3">in a </text:span>
          <text:span text:style-name="T3">
              <office:annotation office:name="__Annotation__271_116812866">
                  <dc:creator>Unbekannter Autor</dc:creator>
                  <dc:date>2019-06-17T20:51:24.996000000</dc:date>
                  <text:p text:style-name="P7">
                      <text:span text:style-name="T7">A Note about section!!!</text:span>
                  </text:p>
              </office:annotation>
          </text:span>
          <text:span text:style-name="T3">section</text:span>
          <office:annotation-end office:name="__Annotation__271_116812866"/>

      See content.xml and styles.xml of HeaderFooter.odt for overlapping notes & notes in footer!

  SOLUTION: Create a "target" and annotation refers to target!
   */
  public static void addAnnotation(
      Component rootComponent,
      JSONArray start,
      JSONObject attrs,
      String id,
      String author,
      String date) {
    // TODO: attrs need to be supported
    final Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the comment should exist at position {0}", start);
    } else if (!id.startsWith(COMMENT_PREFIX)) {
      LOG.log(Level.SEVERE, "The comment id should start with " + COMMENT_PREFIX, start);
    } else {
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      OfficeAnnotationElement newCommentElement = new OfficeAnnotationElement(xmlDoc);
      id = id.substring(COMMENT_PREFIX.length());
      newCommentElement.setOfficeNameAttribute(id);
      try {
        // ADDING ANNOTATION COMPONENT
        addElementAsComponent(parentComponent, newCommentElement, start.getInt(start.length() - 1));
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
      if (!author.isEmpty()) {
        DcCreatorElement creatorElement = new DcCreatorElement(xmlDoc);
        creatorElement.setTextContent(author);
        newCommentElement.appendChild(creatorElement);
      }
      if (!date.isEmpty()) {
        DcDateElement dateElement = new DcDateElement(xmlDoc);
        dateElement.setTextContent(date);
        newCommentElement.appendChild(dateElement);
      }
      OdfFileDom fileDom = (OdfFileDom) rootComponent.getOwnerDocument();
      ((OdfDocument) fileDom.getDocument()).addAnnotation(id, (newCommentElement));
    }
  }

  public static void addNoteSelection(
      Component rootComponent,
      JSONArray start,
      String type,
      String position,
      JSONObject attrs,
      String id) {
    final Component parentComponent = rootComponent.getParentOf(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the comment should exist at position {0}", start);
    } else if (!id.startsWith(COMMENT_PREFIX)) {
      LOG.log(Level.SEVERE, "The comment id should start with " + COMMENT_PREFIX, start);
    } else if (!type.equals("comment")) {
      LOG.log(Level.SEVERE, "Only ranges of type 'comment' can be inserted", start);
    } else if (position.equals(OPK_END)) {
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      OfficeAnnotationEndElement newCommentEndElement = new OfficeAnnotationEndElement(xmlDoc);
      id = id.substring(COMMENT_PREFIX.length());
      newCommentEndElement.setOfficeNameAttribute(id);
      try {
        // ADDING ANNOTATION END COMPONENT
        addElementAsComponent(
            parentComponent, newCommentEndElement, start.getInt(start.length() - 1));
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    } else {
      LOG.log(Level.SEVERE, "ODF does not define a comment start range ", start);
    }
  }

  /** Modifys the page layout (ie. 4 margins, width, height) for all master styles */
  public static void modifyPages(OdfDocument doc, JSONObject attrs) {
    try {
      OdfStylesDom stylesDom = doc.getStylesDom();
      if (stylesDom != null && attrs.has("page")) {
        JSONObject pageAttrs = attrs.getJSONObject("page");

        OdfOfficeMasterStyles masterStyles = stylesDom.getOrCreateMasterStyles();
        OdfOfficeAutomaticStyles autoStyles = stylesDom.getOrCreateAutomaticStyles();
        Iterator<StyleMasterPageElement> masterIt = masterStyles.iterator();
        while (masterIt.hasNext()) {
          StyleMasterPageElement masterPage = masterIt.next();
          String pageLayoutName = masterPage.getStylePageLayoutNameAttribute();
          OdfStylePageLayout pageLayout = autoStyles.getPageLayout(pageLayoutName);
          if (pageAttrs.has("marginLeft")) {
            double value = pageAttrs.getDouble("marginLeft");
            pageLayout.setProperty(
                StylePageLayoutPropertiesElement.MarginLeft, Double.toString(value / 100) + "mm");
          }
          if (pageAttrs.has("marginRight")) {
            double value = pageAttrs.getDouble("marginRight");
            pageLayout.setProperty(
                StylePageLayoutPropertiesElement.MarginRight, Double.toString(value / 100) + "mm");
          }
          if (pageAttrs.has("marginTop")) {
            double value = pageAttrs.getDouble("marginTop");
            pageLayout.setProperty(
                StylePageLayoutPropertiesElement.MarginTop, Double.toString(value / 100) + "mm");
          }
          if (pageAttrs.has("marginBottom")) {
            double value = pageAttrs.getDouble("marginBottom");
            pageLayout.setProperty(
                StylePageLayoutPropertiesElement.MarginBottom, Double.toString(value / 100) + "mm");
          }
          if (pageAttrs.has("width")) {
            double value = pageAttrs.getInt("width");
            pageLayout.setProperty(
                StylePageLayoutPropertiesElement.PageWidth, Double.toString(value / 100) + "mm");
          }
          if (pageAttrs.has("height")) {
            double value = pageAttrs.getInt("height");
            pageLayout.setProperty(
                StylePageLayoutPropertiesElement.PageHeight, Double.toString(value / 100) + "mm");
          }
        }
      }
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  public static void addDeleteHeaderFooter(OdfDocument doc, boolean insert, String id) {
    String masterPageName = "";
    PageArea pageArea = null;
    if (id.startsWith("header")) {
      if (id.startsWith(HEADER_DEFAULT.getPageAreaName())) {
        masterPageName = id.substring(HEADER_DEFAULT.getPageAreaName().length() + 1, id.length());
        pageArea = HEADER_DEFAULT;
      } else if (id.startsWith(HEADER_FIRST.getPageAreaName())) {
        masterPageName = id.substring(HEADER_FIRST.getPageAreaName().length() + 1, id.length());
        pageArea = HEADER_FIRST;
      } else if (id.startsWith(HEADER_EVEN.getPageAreaName())) {
        masterPageName = id.substring(HEADER_EVEN.getPageAreaName().length() + 1, id.length());
        pageArea = HEADER_EVEN;
      }
    } else if (id.startsWith("footer")) {
      if (id.startsWith(FOOTER_DEFAULT.getPageAreaName())) {
        masterPageName = id.substring(FOOTER_DEFAULT.getPageAreaName().length() + 1, id.length());
        pageArea = FOOTER_DEFAULT;
      } else if (id.startsWith(FOOTER_FIRST.getPageAreaName())) {
        masterPageName = id.substring(FOOTER_FIRST.getPageAreaName().length() + 1, id.length());
        pageArea = FOOTER_FIRST;
      } else if (id.startsWith(FOOTER_EVEN.getPageAreaName())) {
        masterPageName = id.substring(FOOTER_EVEN.getPageAreaName().length() + 1, id.length());
        pageArea = FOOTER_EVEN;
      }
    }
    if (pageArea != null) {
      if (insert) {
        OdfElement component = doc.getRootComponentElement(masterPageName, pageArea, true);
        if (pageArea == HEADER_EVEN || pageArea == FOOTER_EVEN) {
          String pageLayoutName =
              ((StyleMasterPageElement) component.getParentNode())
                  .getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "page-layout-name");
          if (pageLayoutName != null) {
            OdfStylePageLayout pageLayout =
                ((StyleMasterPageElement) component.getParentNode())
                    .getOrCreateAutomaticStyles()
                    .getOrCreatePageLayout(pageLayoutName);
            String pageUsageAttribute = pageLayout.getStylePageUsageAttribute();
            if (!pageUsageAttribute.isEmpty()
                && (pageUsageAttribute.equals("right") || pageUsageAttribute.equals("left"))) {
              pageLayout.setStylePageUsageAttribute(null);
            }
          }
        }
      } else {
        OdfElement component = doc.getRootComponentElement(masterPageName, pageArea, false);
        if (component != null) {
          Node parent = component.getParentNode();
          parent.removeChild(component);
        }
      }
    } else {
      LOG.log(Level.SEVERE, "Unable to add/delete header/footer for " + id);
    }
  }

  // reusing addColumnAndCellElements, only with a reference that is outside the scope..
  public static void setColumnsWidth(
      Component tableComponent, JSONArray start, JSONArray tableGrid, boolean isTextTable) {
    if (tableGrid != null) {
      // Returns all TableTableColumn descendants that exist within the tableElement, even within
      // groups, columns and header elements
      TableTableElement tableElement = (TableTableElement) tableComponent.getRootElement();
      // WORK AROUND for "UNDO COLUMN WIDTH" problem (see TableTableElement for further changes)
      List<TableTableColumnElement> existingColumnList =
          Table.getTableColumnElements(tableElement, new ArrayList<TableTableColumnElement>());
      int columnCount = 0;
      for (TableTableColumnElement column : existingColumnList) {
        columnCount += column.getRepetition();
      }
      if (tableGrid.length() != columnCount) {
        // reuse the width from later caching
        ((Table) tableElement.getComponent()).pushTableGrid(tableGrid);
        ((Table) tableElement.getComponent()).requireLaterWidthChange(start);
      } else {
        addColumnAndCellElements(
            tableComponent,
            start,
            tableGrid,
            Integer.MAX_VALUE,
            INSERT_BEFORE,
            -1,
            isTextTable,
            existingColumnList,
            true);
      }
    } else {
      LOG.log(
          Level.SEVERE,
          "Missing table grid defining the relative column widths for table component at position {0}");
    }
  }

  /**
   * If the tableElement has an absolute width, the absolute column width is derived from the given
   * tableGrid
   *
   * @return a list of all absolute column widths, derived from the overall tableElement width
   */
  private static List<String> calcAbsoluteColumnWidths(
      TableTableElement tableElement, JSONArray tableGrid) {
    List<String> absColumnWidths = null;
    String tableWidth = tableElement.getProperty(StyleTablePropertiesElement.Width);
    if (tableWidth != null && !tableWidth.isEmpty()) {
      int absTableWidth = MapHelper.normalizeLength(tableWidth);
      double relTableWidth = 0.0;
      int columnCount = tableGrid.length();
      absColumnWidths = new ArrayList(columnCount);
      for (int i = 0; columnCount > i; i++) {
        relTableWidth += tableGrid.optLong(i);
      }
      if (relTableWidth == 0) {
        // should never occur, but being defensive here, before dividing through zero
        relTableWidth = 1;
      }
      double widthProRel = absTableWidth / relTableWidth;
      for (int i = 0; tableGrid.length() > i; i++) {
        double absWidth = Math.round(tableGrid.optInt(i) * widthProRel);
        absColumnWidths.add(absWidth / 100.0 + "mm");
      }
    }
    return absColumnWidths;
  }

  /**
   * Inserts cells for a new column into a tableElement. Copies styles from the existing referenced
   * grid cell and might adjust the width of all new cells.
   *
   * @param rootComponent high level document structure
   * @param start Integer[] The logical position of the tableElement the new column will be inserted
   *     into.
   * @param referenceColumnGridPosition Integer Zero-based column index, according to the tableGrid
   *     attribute of the tableElement.
   * @param tableGrid Integer[] The complete array of relative widths for the entire tableElement,
   *     containing the new entry for the new column. Will be set to the tableGrid attribute of the
   *     tableElement.
   * @param insertMode String (optional) If set to 'before', the new cells will be inserted before
   *     the existing cells, otherwise after the cells. Default is 'before'. Note: As a side effect,
   *     this op changes the tableGrid attribute of the parentComponentElement tableElement.
   */
  public static void addColumns(
      Component rootComponent,
      JSONArray start,
      JSONArray tableGrid,
      Integer referenceColumnGridPosition,
      String insertMode) {
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.get(start);
    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE,
          "The table parent component of the column should exist at position {0}",
          start);
    } else {
      if (tableGrid != null) {
        // WORK AROUND for "UNDO COLUMN WIDTH" problem
        TableTableElement tableElement = (TableTableElement) parentComponent.mRootElement;

        // WORK AROUND for "UNDO COLUMN WIDTH" problem
        if (!((Table) tableElement.getComponent()).isWidthChangeRequired()) {
          Table.stashColumnWidths(tableElement);
        }
        // INSERT COLUMN
        // Returns all TableTableColumn descendants that exist within the tableElement, even within
        // groups, columns and header elements
        List<TableTableColumnElement> existingColumnList =
            Table.getTableColumnElements(
                parentComponent.getRootElement(), new ArrayList<TableTableColumnElement>());
        addColumnAndCellElements(
            parentComponent,
            start,
            tableGrid,
            referenceColumnGridPosition,
            insertMode,
            -1,
            true,
            existingColumnList,
            false);
        // WORK AROUND for "UNDO COLUMN WIDTH" problem (see JsonOperationConsumer for further
        // changes)
        if (((Table) tableElement.getComponent()).isWidthChangeRequired()) {
          JsonOperationConsumer.setColumnsWidth(
              tableElement.getComponent(),
              ((Table) tableElement.getComponent()).getPosition(),
              ((Table) tableElement.getComponent()).popTableGrid(),
              true);
        }
      } else {
        LOG.log(
            Level.SEVERE,
            "Missing table grid defining the relative column widths for table component at position {0}",
            start);
      }
    }
  }

  // The following we should consider for symmetry reasons..
  // private static void addColumns(Component rootComponent, JSONArray start, JSONObject attrs, int
  // count, int referenceColumn, boolean isTextTable) {	}
  private static void addColumnAndCellElements(
      Component tableComponent,
      JSONArray start,
      JSONArray tableGrid,
      Integer referenceColumnGridPosition,
      String insertMode,
      int newLastColumnNo,
      boolean isTextTable,
      List<TableTableColumnElement> existingColumnList,
      boolean columnCreationOnly) {
    TableTableElement tableElement = (TableTableElement) tableComponent.getRootElement();

    // We need to exchange all absolute widths
    // If the tableElement has an absolute width, the absolute column width is derived from the
    // given tableGrid
    List<String> newAbsColumnWidthsGrid = null;
    if (tableGrid != null) {
      newAbsColumnWidthsGrid = calcAbsoluteColumnWidths(tableElement, tableGrid);
    }
    TableTableColumnElement newColumnElement = null;
    int appendColumnCount = newLastColumnNo + 1 - getColumnCount(existingColumnList);
    if (tableGrid != null) {
      Long previousColumnWidth = null;
      Long currentColumnWidth = null;
      int equalColumnsPassed = 0;
      TableTableColumnElement columnElement = null;
      int columnGridNo = 0;
      // COLUMN ITERATION: Find place to insert new column!
      // We are iterating the columns elements from the existingColumnList, receiving the width from
      // the grid
      for (int columnElementNo = 0;
          columnElementNo < existingColumnList.size();
          columnElementNo++, columnGridNo++) {
        // Receive column element
        columnElement = existingColumnList.get(columnElementNo);

        // applies absolute & relative width to column due to LO/AO issue with relative width
        applyWidth(
            columnElement,
            getAbsoluteColumnWidth(newAbsColumnWidthsGrid, columnGridNo),
            tableGrid.optLong(columnGridNo) + "*");
        //	Iterating through columns (there might be repeated)
        //	Breaking repeated columns if
        //		a) new column is within the repeated
        //		b) column given by grid has different follow-up width
        //	We can not join yet as there is no hasSameStyle function for columns
        // if COLUMN element repeated (covers multiple column grid positions)
        // check for every repeated cell if the grid has changed, increment columnGridNo after each
        // repeat..
        int repetition = columnElement.getRepetition();
        for (int repeated = 1; repeated <= repetition; repeated++) {
          if (repeated > 1) {
            columnGridNo++;
          }
          currentColumnWidth = tableGrid.optLong(columnGridNo);
          // split repeated column element if two follow-up column have a different width
          if (previousColumnWidth != null
              && equalColumnsPassed > 0
              && (!currentColumnWidth.equals(previousColumnWidth))) {
            // Remove existing repeated attribute from current column
            if (columnElement.hasAttributeNS(
                OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
              columnElement.removeAttributeNS(
                  OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
            }
            // Create the first part of passed column
            newColumnElement = cloneColumnElement(columnElement);
            OdfElement parent = (OdfElement) columnElement.getParentNode();
            // the new one will be behind, therefore the existing before
            parent.insertBefore(newColumnElement, columnElement);
            // when repeated are left, they can be added as repeated attributed
            if (equalColumnsPassed > 1) {
              newColumnElement.setAttributeNS(
                  OdfDocumentNamespace.TABLE.getUri(),
                  "table:number-columns-repeated",
                  String.valueOf(equalColumnsPassed));
            }
            applyWidth(
                columnElement,
                getAbsoluteColumnWidth(newAbsColumnWidthsGrid, columnGridNo),
                tableGrid.optLong(columnGridNo) + "*");
            equalColumnsPassed = 1;
          } else {
            // columns have the same size
            equalColumnsPassed++;
            // insertion of new column
            if (columnGridNo == referenceColumnGridPosition) {
              equalColumnsPassed++;
              columnGridNo++;
            }
          }
          // the current becomes the previous width
          previousColumnWidth = currentColumnWidth;
        }

        // when repeated are left, they can be added as repeated attributed
        if (equalColumnsPassed > 1) {
          columnElement.setAttributeNS(
              OdfDocumentNamespace.TABLE.getUri(),
              "table:number-columns-repeated",
              String.valueOf(equalColumnsPassed));
        } else {
          if (columnElement.hasAttributeNS(
              OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
            columnElement.removeAttributeNS(
                OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
          }
        }
        equalColumnsPassed = 0;
        previousColumnWidth = null;
      }

      if (columnGridNo == referenceColumnGridPosition) {
        addColumnElement(
            columnElement, insertMode, columnGridNo, newAbsColumnWidthsGrid, tableGrid);
        columnGridNo++;
      }
    }
    if ((appendColumnCount > 0 || newLastColumnNo > 0)
        && !referenceColumnGridPosition.equals(
            Integer
                .MAX_VALUE)) { // else if no existing columns have to be changed, but only further
      // have to be appended
      appendColumnElement(tableElement, existingColumnList, appendColumnCount);
    }
    if (!columnCreationOnly) {
      // A new cell is being inserted at the given position for each row
      if (referenceColumnGridPosition == null) {
        addCellsInRows(
            tableComponent, start, appendColumnCount, insertMode, appendColumnCount, isTextTable);
      } else if (referenceColumnGridPosition
          != Integer.MAX_VALUE) { // MAX VALUE used for setAttribute reusage
        addCellsInRows(
            tableComponent,
            start,
            referenceColumnGridPosition,
            insertMode,
            appendColumnCount,
            isTextTable);
      }
    }
  }

  private static void addColumnElement(
      TableTableColumnElement columnElement,
      String insertMode,
      int columnGridNo,
      List<String> newAbsColumnWidthsGrid,
      JSONArray tableGrid) {
    // Clone the current column (where the cursor stands) as all styles are copied from it
    TableTableColumnElement newColumnElement = cloneColumnElement(columnElement);
    OdfElement parent = (OdfElement) columnElement.getParentNode();
    if (insertMode.equals(INSERT_BEFORE)) {
      parent.insertBefore(newColumnElement, columnElement);
      // increment grid due to the new cell and format the existing cell (pushed back) correctly
      applyWidth(
          columnElement,
          getAbsoluteColumnWidth(newAbsColumnWidthsGrid, columnGridNo),
          tableGrid.optLong(columnGridNo) + "*");
    } else { // new column is after existing columns
      Element nextSibling = OdfElement.getNextSiblingElement(columnElement);
      if (nextSibling != null) {
        parent.insertBefore(newColumnElement, nextSibling);
      } else {
        parent.appendChild(newColumnElement);
      }
      // increment grid due to the new cell and format the new cell correctly
      applyWidth(
          newColumnElement,
          getAbsoluteColumnWidth(newAbsColumnWidthsGrid, columnGridNo),
          tableGrid.optLong(columnGridNo) + "*");
    }
  }

  private static void appendColumnElement(
      TableTableElement tableElement,
      List<TableTableColumnElement> existingColumnList,
      int appendColumnCount) {
    TableTableColumnElement columnElement = existingColumnList.get(existingColumnList.size() - 1);
    TableTableColumnElement newColumnElement =
        new TableTableColumnElement((OdfFileDom) tableElement.getOwnerDocument());
    if (appendColumnCount > 1) {
      newColumnElement.setTableNumberColumnsRepeatedAttribute(appendColumnCount);
    }
    // @tableElement:default-cell-style-name
    if (columnElement.hasAttributeNS(
        OdfDocumentNamespace.TABLE.getUri(), "default-cell-style-name")) {
      newColumnElement.setTableDefaultCellStyleNameAttribute(
          columnElement.getAttributeNS(
              OdfDocumentNamespace.TABLE.getUri(), "default-cell-style-name"));
    }
    OdfElement parent = (OdfElement) columnElement.getParentNode();
    Element nextSibling = OdfElement.getNextSiblingElement(columnElement);
    if (nextSibling != null) {
      parent.insertBefore(newColumnElement, nextSibling);
    } else {
      parent.appendChild(newColumnElement);
    }
  }

  private static int getColumnCount(List<TableTableColumnElement> columnList) {
    int columnCount = 0;
    int columnRepeated = 1;
    for (TableTableColumnElement columnElement : columnList) {
      columnRepeated = 1;
      if (columnElement.hasAttributeNS(
          OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
        columnRepeated =
            Integer.parseInt(
                columnElement.getAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated"));
      }
      columnCount += columnRepeated;
    }
    return columnCount;
  }

  private static void addCellsInRows(
      Component tableComponent,
      JSONArray start,
      Integer referenceColumnGridPosition,
      String insertMode,
      int cellRepetition,
      boolean isTextTable) {
    // A new cell is being inserted at the given position for each row
    if (referenceColumnGridPosition
        != Integer.MAX_VALUE) { // MAX VALUE used for setAttribute reusage
      // GET TARGET POSITION
      // Figure out target grid position for cell/column
      int newColumnTargetGridPosition = -1;
      if (insertMode.equals(INSERT_BEFORE)) {
        newColumnTargetGridPosition = referenceColumnGridPosition;
      } else {
        newColumnTargetGridPosition = referenceColumnGridPosition + 1;
      }
      addNewRowsCells(
          tableComponent,
          start,
          referenceColumnGridPosition,
          newColumnTargetGridPosition,
          cellRepetition,
          isTextTable);
    }
  }

  private static String getAbsoluteColumnWidth(
      List<String> newAbsColumnWidthsGrid, int columnGridNo) {
    String absoluteWidth = null;
    if (newAbsColumnWidthsGrid != null) {
      absoluteWidth = newAbsColumnWidthsGrid.get(columnGridNo);
    }
    return absoluteWidth;
  }

  /** Applies to the given column element the given absolute and relative width */
  private static void applyWidth(
      TableTableColumnElement columnElement, String absoluteWidth, String relativeWidth) {
    StyleStyleElement columnStyleElement = columnElement.getOrCreateUnqiueAutomaticStyle();
    StyleTableColumnPropertiesElement columnPropsElement =
        (StyleTableColumnPropertiesElement)
            columnStyleElement.getOrCreatePropertiesElement(
                OdfStylePropertiesSet.TableColumnProperties);
    if (columnPropsElement == null) {
      columnPropsElement = columnStyleElement.newStyleTableColumnPropertiesElement();
    }
    // There is a problem with relative width in some ODF applications, therefore we need to set the
    // absolute width as well
    if (absoluteWidth != null) {
      columnPropsElement.setAttributeNS(
          OdfDocumentNamespace.STYLE.getUri(), "style:column-width", absoluteWidth);
      // remove the relative with, otherwise LO/AO will ignore the absolute, but unfortunately is
      // buggy with the relative
      columnPropsElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "rel-column-width");
    } else if (relativeWidth != null) {
      // if there is no absolute tableElement width, the relative widths have to be used (or "auto"
      // being resolved)
      columnPropsElement.setAttributeNS(
          OdfDocumentNamespace.STYLE.getUri(), "style:rel-column-width", relativeWidth);
    }
  }

  // The function clones the given column element removing xml:id and repeated attributes
  private static TableTableColumnElement cloneColumnElement(
      TableTableColumnElement origColumnElement) {
    // CLONING EXISTING COLUMN
    TableTableColumnElement newColumnElement =
        (TableTableColumnElement) origColumnElement.cloneNode(true);
    // do not clone an ID, as there can only be one of it in an XML file
    if (newColumnElement.hasAttribute("xml:id")) {
      newColumnElement.removeAttribute("xml:id");
    }

    // skip the iteration over the same column
    if (newColumnElement.hasAttributeNS(
        OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
      newColumnElement.removeAttributeNS(
          OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
    }
    return newColumnElement;
  }

  /** A new cell is being inserted at the given position for each row *** ToBeRemoved*** */
  private static void addNewRowsCells(
      Component tableComponent,
      JSONArray start,
      Integer cellReferencePosition,
      Integer cellTargetPosition,
      int cellRepetition,
      boolean isTextTable) {
    // MULTIPLYING THE CELLS - adding XML & Component
    int currentRowPosition = 0;
    int rowComponentLevel = start.length();
    // Add initial row and cell position
    JSONArray destinationCellPosition = start.put(currentRowPosition).put(cellTargetPosition);
    TableTableCellElement clonedCellElement = null;
    TableTableElement tableElement = (TableTableElement) tableComponent.mRootElement;
    OdfTable table = OdfTable.getInstance(tableElement);
    for (TableTableRowElement rowElement : table.getRowElementList()) {
      Row rowComponent = (Row) rowElement.getComponent();
      // if there is no cell at this position, skip this row
      if (cellReferencePosition == null || cellReferencePosition == -1) {
        clonedCellElement =
            new TableTableCellElement((OdfFileDom) tableComponent.getOwnerDocument());
        cellReferencePosition = -1;
      } else if (rowComponent.getChildNode(cellReferencePosition) != null) {
        // otherwise clone the styles from the referenced cell
        clonedCellElement =
            (TableTableCellElement)
                ((TableTableCellElement) rowComponent.getChildNode(cellReferencePosition))
                    .cloneNode(0);
        // remove any of the following attributes
        clonedCellElement.removeAttributeNS(
            OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated");
        clonedCellElement.removeAttributeNS(
            OdfDocumentNamespace.TABLE.getUri(), "number-columns-spanned");
        clonedCellElement.removeAttribute("xml:id");
      }

      //			// if there is a target position
      //			if ((rowComponent.getChildNode(cellTargetPosition) == null || cellTargetPosition == 1 +
      // cellReferencePosition) && clonedCellElement != null) {
      if (cellRepetition == -1) {
        cellRepetition = cellTargetPosition - cellReferencePosition;
      }
      addCells(
          tableComponent.getRootComponent(),
          destinationCellPosition,
          null,
          cellRepetition,
          rowComponent,
          clonedCellElement,
          isTextTable);
      //			}

      try {
        currentRowPosition += rowComponent.repetition();
        destinationCellPosition.put(rowComponentLevel, currentRowPosition);
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Adding a sequence of columns with repeated attribute to the tableElement and automatic styles
   * with relative column width for the columns
   */
  private static void addNewColumns(TableTableElement table, JSONArray tableGrid) {
    if (tableGrid != null) {
      int columnLength = Integer.MIN_VALUE;
      int previousColumnLength = Integer.MIN_VALUE;
      int repeated = 1;
      int columnCount = tableGrid.length();
      for (int i = 0; i < columnCount; i++) {
        try {
          columnLength = (Integer) tableGrid.get(i);
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
        if (previousColumnLength == columnLength) {
          repeated++;
        } else {
          if (i != 0) {
            // write out column with repeat
            TableTableColumnElement column = table.newTableTableColumnElement();
            if (repeated != 1) {
              column.setTableNumberColumnsRepeatedAttribute(repeated);
            }
            column
                .getOrCreateUnqiueAutomaticStyle()
                .newStyleTableColumnPropertiesElement()
                .setStyleRelColumnWidthAttribute(previousColumnLength + "*");
          }
          previousColumnLength = columnLength;
          repeated = 1;
        }
      }
      TableTableColumnElement column = table.newTableTableColumnElement();
      if (repeated != 1) {
        column.setTableNumberColumnsRepeatedAttribute(repeated);
      }
      column
          .getOrCreateUnqiueAutomaticStyle()
          .newStyleTableColumnPropertiesElement()
          .setStyleRelColumnWidthAttribute(previousColumnLength + "*");
    }
  }

  /**
   * addRows Inserts one or more new rows into a tableElement.
   *
   * @param rootComponent the root of all components
   * @param start The logical position of the new row. The row will be inserted before a row that is
   *     currently located at this position.
   * @param count (optional) The number of rows that will be inserted, default is 1.
   * @param addDefaultCells (optional) If true, empty cells will be inserted into the new row. The
   *     number of inserted cells will be equal to the number of columns in the tableElement, as
   *     specified by the tableGrid attribute of the tableElement. The default is false.
   * @param referenceRow (optional) If specified, the zero-based index of the existing row whose
   *     cells and their attributes will be cloned (but without the cell contents).
   * @param attrs (optional) Initial row attributes. See Table Row Formatting Attributes. Note: The
   *     attributes addDefaultCells and referenceRow are mutually exclusive.
   */
  public static void addRows(
      Component rootComponent,
      JSONArray start,
      JSONObject attrs,
      int count,
      boolean addDefaultCells,
      int referenceRow,
      boolean isTextTable)
      throws IndexOutOfBoundsException, JSONException {
    assert count >= 1 : "Row count is expected to be at least 1";
    // Parent will not change and have to exist to insert the new component
    final Component parentComponent = rootComponent.getParentOf(start);
    Object o = parentComponent.getRootElement();
    // only occurs, when a paragraph was inserted to state that the max. table size was exceeded
    // this is a feature not activated in the default build, but table limitation might assist
    // performance on weak clients
    if (!(o instanceof TableTableElement)) {
      LOG.severe("Table exceeded the maximum Size: " + o);
    }
    TableTableElement tableElement = (TableTableElement) parentComponent.getRootElement();
    OdfTable table = OdfTable.getInstance(tableElement);

    if (parentComponent == null) {
      LOG.log(
          Level.SEVERE, "The parent component of the table should exist at position {0}", start);
    } else {
      TableTableRowElement rowElement;
      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      if (referenceRow > -1) {
        // Get the referenceRow and copy it WITH cells, but without the cell content
        OdfTableRow row = table.getRowByIndex(referenceRow);
        TableTableRowElement originalRow = row.getOdfElement();
        // Once cloned to apply all the hard styles only on the new row
        rowElement = (TableTableRowElement) originalRow.cloneNode(1);
        // do not clone an ID, as there can only be one of it in an XML file
        if (rowElement.hasAttribute("xml:id")) {
          rowElement.removeAttribute("xml:id");
        }
        Integer rowsRepeated = rowElement.getTableNumberRowsRepeatedAttribute();
        if (rowsRepeated != null && rowsRepeated > 1) {
          rowElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-repeated");
        }
      } else {
        // CREATING NEW ROOT ELEMENT
        rowElement = new TableTableRowElement(xmlDoc);
      }

      // ADDING STYLES TO THE NEW ELEMENT
      if (attrs != null) {
        addStyle(attrs, rowElement, xmlDoc);
      }

      // Only addChild cells to a new row, when explicitly requested or reference row had been
      // addressed
      Component newRowComponent =
          addElementAsComponent(parentComponent, rowElement, start.optInt(start.length() - 1));
      if (addDefaultCells) {
        if (isTextTable) {
          addCells(
              rootComponent,
              start,
              CELL_WITH_BORDER_ATTRS,
              table.getColumnCount(),
              newRowComponent,
              null,
              isTextTable);
        } else {
          // if there is no reference row, no cells has been cloned from an existing row and basic
          // cells have to be added
          if (referenceRow <= -1) {
            addCells(
                rootComponent,
                start,
                null,
                table.getColumnCount(),
                newRowComponent,
                null,
                isTextTable);
          }
        }
      }
      if (count > 1) {
        if (isTextTable) {
          // Insert row as often as requested by "count"
          duplicateComponent(newRowComponent, count - 1);
        } else {
          rowElement.setTableNumberRowsRepeatedAttribute(count);
        }
      }
    }
  }

  // ToDo: addCells/Rows/Table are very very SIMILAR and can be condensed!!
  public static void addCells(
      Component rootComponent,
      JSONArray start,
      JSONObject attrs,
      int count,
      Component parentComponent,
      OdfElement newCellElement,
      boolean isTextTable)
      throws IndexOutOfBoundsException {
    // Parent will not change and have to exist to insert the new component
    if (parentComponent == null) {
      parentComponent = rootComponent.getParentOf(start);
    }
    int cellPosition = start.optInt(start.length() - 1);

    if (isTextTable) {
      //
      //		if(parentComponent == null){
      //			// OOStyledTable.odt
      //			// tableOps.odt
      //		}
      //		if (parentComponent == null) {
      //			parentComponent = rootComponent.getParentOf(start);
      //		}

      OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();
      if (newCellElement == null) {
        // Creating a new cell (with border for text documents)
        newCellElement = new TableTableCellElement(xmlDoc);
      }

      // Adding any hard formatting to cell
      if (attrs != null) {
        JsonOperationConsumer.addStyle(attrs, (OdfStylableElement) newCellElement, xmlDoc);
        addCellAttributes(attrs, (TableTableCellElement) newCellElement);
      }

      // ADDING COMPONENT
      Component cellComponent =
          JsonOperationConsumer.addElementAsComponent(
              parentComponent, newCellElement, cellPosition);
      // Insert cell as often as requested by "count"	(minus one as already once created)
      JsonOperationConsumer.duplicateComponent(cellComponent, count - 1);

      Component tableComponent = cellComponent.getParent().getParent();
      TableTableElement tableElement = (TableTableElement) tableComponent.mRootElement;

      // WORK AROUND for "UNDO COLUMN WIDTH" problem
      if (((Table) tableElement.getComponent()).isWidthChangeRequired()) {
        // INSERT COLUMN
        // Returns all TableTableColumn descendants that exist within the tableElement, even within
        // groups, columns and header elements
        List<TableTableColumnElement> existingColumnList =
            Table.getTableColumnElements(tableElement, new ArrayList<TableTableColumnElement>());
        // Column creation only required
        addColumnAndCellElements(
            tableElement.getComponent(),
            start,
            ((Table) tableElement.getComponent()).popTableGrid(),
            cellPosition,
            INSERT_AFTER,
            -1,
            true,
            existingColumnList,
            true);
        ((Table) tableElement.getComponent()).hasChangedWidth();
      }

    } else {
      if (newCellElement == null) {
        OdfFileDom xmlDoc = (OdfFileDom) parentComponent.getOwnerDocument();

        // Creating a new cell (with border for text documents)
        newCellElement = new TableTableCellElement(xmlDoc);
      }
      if (count > 1) {
        newCellElement.setAttributeNS(
            OdfDocumentNamespace.TABLE.getUri(),
            "table:number-columns-repeated",
            String.valueOf(count));
      }

      //			newCellElement.setAttributeNS(OdfDocumentNamespace.TABLE.getUri(),
      // "tableElement:number-columns-repeated", String.valueOf(count));
      //			parentComponent.getRootElement().appendChild(newCellElement);
      // ADDING COMPONENT
      JsonOperationConsumer.addElementAsComponent(parentComponent, newCellElement, cellPosition);
    }
  }

  private static void addCellAttributes(
      JSONObject attrs, TableTableCellElement newCellElement) { // , OdfFileDom ownerDocument
    JSONObject props = (JSONObject) attrs.opt("cell");
    if (props != null && props.length() > 0) {
      if (props.has("gridSpan")) {
        Integer columnSpan = props.optInt("gridSpan");
        newCellElement.setTableNumberColumnsSpannedAttribute(columnSpan);
      }
    }
  }

  /** Clones all elements of the component and created new components for them */
  // ToDo-Clean-Up: Move this to styleable Element
  private static void duplicateComponent(Component c, int count) {
    // if multiple components have to be added, clone them (different handling components with
    // repeated element, e.g. for cells )
    OdfElement newElement = c.mRootElement;
    OdfElement parent = (OdfElement) c.mRootElement.getParentNode();
    for (int i = 0; i < count; i++) {
      newElement = (OdfElement) newElement.cloneNode(true);
      parent.insertBefore(newElement, c.mRootElement);
      addElementAsComponent(c.getParent(), newElement, -1);

      //			NodeList rowChildren = cellElement.getChildNodes();
      //			Node child;
      //			for (int m = 0; m < rowChildren.getLength(); m++) { // ADD CELL ELEMENTS
      //				child = rowChildren.item(m);
      //				if (child instanceof TableTableCellElement) {
      //					// remove all the content from the cell (or covered tableElement cell)
      //					TableTableCellElement cell = (TableTableCellElement) child;
      //					newRowComponent.createChildComponent((OdfElement) cell);
      //				}
      //			}
    }
  }

  /**
   * Adds the newElement to the parentComponent. Creating a component for
   * newElement Repeating the above in the number of the given count Adding
   * components for all children of newElement * Unfortunately sometimes there
   * is trailing boilerplate elements, that HAVE to be at the end. No new
   * components are allowed to be appended, e.g.
   * <table:named-expressions/>
   * </office:spreadsheet>
   *
   */
  // ToDo Move this function to Component/Element or make it obsolete by no longer using component
  // structure
  public static Component addElementAsComponent(
      Component parentComponent, OdfElement newElement, int newPosition) {
    Component newComponent = null;
    if (parentComponent != null) {
      Node existingNode = parentComponent.getChildNode(newPosition);
      Element existingElement = null;
      // CHECK IF AN EXISTING COMPONENT HAVE TO BE MOVED
      // When existing element found and no explicit appending (by providing -1)
      if (existingNode != null && newPosition != -1 && existingNode instanceof OdfElement) {
        // if there is already a component on this position, insert the new component before
        OdfElement existingParentElement = (OdfElement) existingNode.getParentNode();
        // if there is are wrapping list elements around the node
        if (existingParentElement instanceof TextListItemElement
            || existingParentElement instanceof TextListHeaderElement) {
          existingElement =
              Component.getCorrectStartElementOfChild(
                  parentComponent.getRootElement(), (OdfElement) existingNode);
          existingParentElement = parentComponent.getRootElement();
        } else {
          existingElement = (OdfElement) existingNode;
        }
        // PLACING THE NEW COMPONENT ELEMENT INTO THE EXISTING TREE
        existingParentElement.insertBefore(newElement, existingElement);
        newComponent = Component.createChildComponent(newPosition, parentComponent, newElement);
      } else {
        // IF IT IS A TEXT COMPONENT
        if (parentComponent instanceof TextContainer
            && ((TextContainer) parentComponent).getChildNode(newPosition) != null) {
          Element parentElement = parentComponent.getRootElement();
          if (parentElement instanceof OdfElement) {
            ((OdfElement) parentElement).insert(newElement, newPosition);
            newComponent = Component.createChildComponent(newPosition, parentComponent, newElement);
          } else {
            LOG.log(
                Level.WARNING,
                "The parent element {0} is not of type OdfElement. The new element {1} could not be inserted!",
                new Object[] {parentElement.getTagName(), newElement.getTagName()});
          }
        } else { // only possibility left is that it have to be appended with parameter -1
          Element parentElement = parentComponent.getRootElement();
          boolean inserted = false;
          if (parentElement instanceof DrawFrameElement) {
            Node child = parentElement.getFirstChild();
            if (child != null && child instanceof DrawTextBoxElement) {
              child.appendChild(newElement);
              inserted = true;
            }
          }
          if (!inserted) {
            parentElement.appendChild(newElement);
          }
          newComponent = parentComponent.createChildComponent(newElement);
          if (newComponent instanceof Row || newComponent instanceof Cell) {
            OdfElement parent = (OdfElement) newComponent.mRootElement.getParentNode();
            if (newComponent instanceof Row) {

              //							while(!(parentComponentElement instanceof TableTableRowElement)){
              //								parentComponentElement.getParentNode();
              //							}
              // int existingNumber =  parentComponentElement.countDescendantComponents();
              // newElement.setAttributeNS(OdfDocumentNamespace.TABLE.getUri(),
              // "tableElement:tableElement:number-columns-repeated", String.valueOf(newPosition -
              // existingNumber));
              //							newElement.setAttributeNS(OdfDocumentNamespace.TABLE.getUri(),
              // "tableElement:tableElement:number-columns-repeated", String.valueOf(newPosition));
              //						}else{
              while (!(parent instanceof TableTableElement)) {
                parent.getParentNode();
              }
              // minus one as the new row was already added
              int existingNumber = parent.countDescendantComponents() - 1;
              // plus one as the position starts counting with zero
              int newCount = newPosition + 1;
              int rowRepeated = newCount - existingNumber;
              if (rowRepeated > 1) {
                newElement.setAttributeNS(
                    OdfDocumentNamespace.TABLE.getUri(),
                    "table:number-rows-repeated",
                    String.valueOf(rowRepeated));
              }
            }
          }
        }
      }
    } else {
      LOG.severe("The parentComponent should never be null!");
    }
    return newComponent;
  }

  public static void mapProperties(
      OdfStyleFamily styleFamily, JSONObject attrs, OdfStyleBase style, OdfDocument doc) {

    if (attrs != null && styleFamily != null && style != null) {
      if (styleFamily.getName().equals("table-cell")
          && attrs.has("cell")
          && !attrs.get("cell").equals(JSONObject.NULL)) {
        try {
          JSONObject cellObject = attrs.getJSONObject("cell");
          if (cellObject.has("alignHor")) {
            if (!attrs.has("paragraph")) {
              attrs.put("paragraph", new JSONObject());
            }
            JSONObject paraObject = attrs.getJSONObject("paragraph");
            paraObject.put("alignment", cellObject.get("alignHor"));
          }
        } catch (JSONException e) {
        }
      }
      Map<String, OdfStylePropertiesSet> familyProperties =
          Component.getAllStyleGroupingIdProperties(styleFamily);
      Set<String> propTypes = familyProperties.keySet();
      for (String type : propTypes) {

        if (type.equals("character")
            && attrs.has("character")
            && !attrs.get("character").equals(JSONObject.NULL)) {
          JSONObject textProps = (JSONObject) attrs.opt("character");
          if (textProps != null && textProps.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TextProperties);
            mapCharacterProperties(textProps, (StyleTextPropertiesElement) propsElement, doc);
          }
        } else if (type.equals("paragraph")
            && attrs.has("paragraph")
            && !attrs.get("paragraph").equals(JSONObject.NULL)) {
          JSONObject paraProps = (JSONObject) attrs.opt("paragraph");
          if (paraProps != null && paraProps.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.ParagraphProperties);
            mapParagraphProperties(paraProps, (StyleParagraphPropertiesElement) propsElement);
          }
        } else if (type.equals("table")) {
          if (attrs.has("table") && !attrs.get("table").equals(JSONObject.NULL)) {
            JSONObject tableProps = (JSONObject) attrs.opt("table");
            if (tableProps != null && tableProps.length() > 0) {
              OdfStylePropertiesBase propsElement =
                  style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableProperties);
              mapTableProperties(tableProps, (StyleTablePropertiesElement) propsElement);
            }
          } else if (attrs.has(OPK_SHEET) && !attrs.get(OPK_SHEET).equals(JSONObject.NULL)) {
            // currently the sheet are handled different than the tableElement
            JSONObject sheetProps = (JSONObject) attrs.opt(OPK_SHEET);
            if (sheetProps != null && sheetProps.length() > 0) {
              OdfStylePropertiesBase propsElement =
                  style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableProperties);
              mapTableProperties(sheetProps, (StyleTablePropertiesElement) propsElement);
            }
          } else {
            // some default values have to be set (width 100% for MSO15)
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableProperties);
            mapTableProperties(null, (StyleTablePropertiesElement) propsElement);
          }
        } else if (type.equals("row")
            && attrs.has("row")
            && !attrs.get("row").equals(JSONObject.NULL)) {
          JSONObject props = (JSONObject) attrs.opt("row");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableRowProperties);
            mapRowProperties(props, (StyleTableRowPropertiesElement) propsElement);
          }
        } else if (type.equals("cell")
            && attrs.has("cell")
            && !attrs.get("cell").equals(JSONObject.NULL)) {
          JSONObject props = (JSONObject) attrs.opt("cell");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableCellProperties);
            mapCellProperties(props, (StyleTableCellPropertiesElement) propsElement);
          }
        } else if (type.equals("column")
            && attrs.has("column")
            && !attrs.get("column").equals(JSONObject.NULL)) {
          JSONObject props = (JSONObject) attrs.opt("column");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.TableColumnProperties);
            mapColumnProperties(props, (StyleTableColumnPropertiesElement) propsElement);
          }
        } else if (type.equals("list")
            && attrs.has("list")
            && !attrs.get("list").equals(JSONObject.NULL)) {
          JSONObject props = (JSONObject) attrs.opt("list");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.ListLevelProperties);
            mapListProperties(props, (StyleListLevelPropertiesElement) propsElement);
          }
        } else if (type.equals("section")
            && attrs.has("section")
            && !attrs.get("section").equals(JSONObject.NULL)) {
          JSONObject props = (JSONObject) attrs.opt("section");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.SectionProperties);
            mapSectionProperties(props, (StyleSectionPropertiesElement) propsElement);
          }

        } else if (type.equals("drawing") || type.equals("presentation")) {
          if (attrs.has("drawing")
              || attrs.has("shape")
              || attrs.has("line")
              || attrs.has("fill")) {
            JSONObject allDrawingProperties = new JSONObject();
            try {
              String subs[] = {"shape", "drawing"};
              for (String sub : subs) {
                if (attrs.has(sub)) {
                  JSONObject subAttrs = attrs.getJSONObject(sub);
                  Iterator<String> keyIt = subAttrs.keys();
                  while (keyIt.hasNext()) {
                    String key = keyIt.next();
                    allDrawingProperties.put(key, subAttrs.get(key));
                  }
                }
              }
              if (attrs.has("fill")) {
                allDrawingProperties.put("fill", attrs.getJSONObject("fill"));
              }
              if (attrs.has("line")) {
                allDrawingProperties.put("line", attrs.getJSONObject("line"));
              }
            } catch (JSONException e) {
              // no handling required
            }
            if (allDrawingProperties.length() > 0) {
              OdfStyleBase parentStyle = style.getParentStyle();
              OdfStylePropertiesBase propsElement =
                  style.getOrCreatePropertiesElement(OdfStylePropertiesSet.GraphicProperties);
              mapGraphicProperties(
                  allDrawingProperties, (StyleGraphicPropertiesElement) propsElement, parentStyle);
            }
          }

          // ToDo: How to differentiate Graphics from Drawings? ( see condition before - images have
          // only GraphicProperties!)
          //				} else if (type.equals("drawing")) {
          //					JSONObject props = (JSONObject) attrs.opt("drawing");
          //					if (props != null && props.length() > 0) {
          //						OdfStylePropertiesBase propsElement =
          // style.getOrCreatePropertiesElement(OdfStylePropertiesSet.DrawingPageProperties);
          //						mapDrawingProperties(props, (StyleDrawingPagePropertiesElement) propsElement);
          //					}
        } else if (type.equals("chart") || type.equals("chart")) {
          JSONObject props = (JSONObject) attrs.opt("chart");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.ChartProperties);
            mapChartProperties(props, (StyleChartPropertiesElement) propsElement);
          }
        } else if (type.equals("page") || type.equals("page")) {
          JSONObject props = (JSONObject) attrs.opt("page");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.PageLayoutProperties);
            mapPageProperties(props, (StylePageLayoutPropertiesElement) propsElement);
          }
        } else if (type.equals("ruby") || type.equals("ruby")) {
          JSONObject props = (JSONObject) attrs.opt("ruby");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.RubyProperties);
            mapRubyProperties(props, (StyleRubyPropertiesElement) propsElement);
          }
        } else if (type.equals("headerFooter") || type.equals("headerFooter")) {
          JSONObject props = (JSONObject) attrs.opt("headerFooter");
          if (props != null && props.length() > 0) {
            OdfStylePropertiesBase propsElement =
                style.getOrCreatePropertiesElement(OdfStylePropertiesSet.HeaderFooterProperties);
            mapHeaderFooterProperties(props, (StyleHeaderFooterPropertiesElement) propsElement);
          }
        }
      }
    }
  }

  // The latter fontNames Set is a hack for the OX release as the 16 fonts supported do not write
  // out their descriptions
  public static void mapCharacterProperties(
      JSONObject attrs, StyleTextPropertiesElement propertiesElement, OdfDocument doc) {
    if (attrs != null) {

      Object language = null;
      Object noProof = null;

      final Iterator keySetIter = attrs.keySet().iterator();
      while (keySetIter.hasNext()) {
        final String key = (String) keySetIter.next();
        final Object value = attrs.get(key);
        // TODO -- !!!!!!!!!!!!!!!!ROUNDTRIP WITH THESE VALUES!!!!!!!!!!!
        //	<define name="fontWeight">
        //		<choice>
        //			<value>normal</value>
        //			<value>bold</value>
        //			<value>100</value>
        //			<value>200</value>
        //			<value>300</value>
        //			<value>400</value>
        //			<value>500</value>
        //			<value>600</value>
        //			<value>700</value>
        //			<value>800</value>
        //			<value>900</value>
        //		</choice>
        //	</define>
        // fo:font-weight
        if (key.equals("bold")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "font-weight");
          } else {
            Boolean isBold = (Boolean) value;
            if (isBold) {
              propertiesElement.setFoFontWeightAttribute("bold");
            } else {
              propertiesElement.setFoFontWeightAttribute("normal");
            }
          }
        } else if (key.equals("boldAsian")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "font-weight-asian");
          } else {
            Boolean isBold = (Boolean) value;
            if (isBold) {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-weight-asian", "bold");
            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-weight-asian", "normal");
            }
          }
        } else if (key.equals("boldComplex")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "font-weight-complex");
          } else {
            Boolean isBold = (Boolean) value;
            if (isBold) {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-weight-complex", "bold");
            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-weight-complex", "normal");
            }
          }
        } //	<define name="lineStyle">
        //		<choice>
        //			<value>none</value>
        //			<value>solid</value>
        //			<value>dotted</value>
        //			<value>dash</value>
        //			<value>long-dash</value>
        //			<value>dot-dash</value>
        //			<value>dot-dot-dash</value>
        //			<value>wave</value>
        //		</choice>
        //	</define>
        else if (key.equals("underline")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-underline-style");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-underline-width");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-underline-color");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-underline-type");
          } else {
            Boolean isUnderline = (Boolean) value;
            if (isUnderline) {
              propertiesElement.setStyleTextUnderlineStyleAttribute("solid");
            } else {
              propertiesElement.setStyleTextUnderlineStyleAttribute("none");
            }
          }

          //	<define name="fontStyle">
          //		<choice>
          //			<value>normal</value>
          //			<value>italic</value>
          //			<value>oblique</value>
          //		</choice>
          //	</define>
        } else if (key.equals("italic")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "font-style");
          } else {
            Boolean isItalic = (Boolean) value;
            if (isItalic) {
              propertiesElement.setFoFontStyleAttribute("italic");
            } else {
              propertiesElement.setFoFontStyleAttribute("normal");
            }
          }
        } else if (key.equals("italicAsian")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "font-style-asian");
          } else {
            Boolean isItalic = (Boolean) value;
            if (isItalic) {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-style-asian", "italic");

            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-style-asian", "normal");
            }
          }
        } else if (key.equals("italicComplex")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "font-style-complex");
          } else {
            Boolean isItalic = (Boolean) value;
            if (isItalic) {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-style-complex", "italic");
            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-style-complex", "normal");
            }
          }
        } else if (key.equals("color")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "color");
          } else {
            JSONObject color = (JSONObject) value;
            if (color.has(OPK_TYPE) && !color.get(OPK_TYPE).equals(JSONObject.NULL)) {
              String type = color.optString(OPK_TYPE, "");
              if (!type.equals(MapHelper.AUTO)) {
                propertiesElement.setFoColorAttribute(getColor(color, null));
                propertiesElement.removeAttributeNS(
                    OdfDocumentNamespace.STYLE.getUri(), "use-window-font-color");
              } else {
                propertiesElement.setAttributeNS(
                    OdfDocumentNamespace.STYLE.getUri(), "style:use-window-font-color", "true");
              }
            } else { // DEFAULT IS AUTO
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:use-window-font-color", "true");
            }
          }
        } else if (key.equals("fillColor")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "background-color");
          } else {
            JSONObject color = (JSONObject) value;
            propertiesElement.setFoBackgroundColorAttribute(getColor(color, MapHelper.TRANSPARENT));
          }
        } else if (key.equals("fontSize")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "font-size");
          } else {
            propertiesElement.setFoFontSizeAttribute(value + "pt");
          }
        } else if (key.equals("fontSizeAsian")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "font-size-asian");
          } else {
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(),
                "style:font-size-asian",
                value + "pt");
          }
        } else if (key.equals("fontSizeComplex")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "font-size-complex");
          } else {
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(),
                "style:font-size-complex",
                value + "pt");
          }
        } else if (key.equals("fontName")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "font-name");
          } else {
            String fontName = (String) value;
            propertiesElement.setStyleFontNameAttribute(fontName);
            addFontToDocument(fontName, doc);
          }
        } else if (key.equals("fontNameAsian")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "font-name-asian");
          } else {
            String fontName = (String) value;
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "style:font-name-asian", fontName);
          }
        } else if (key.equals("fontNameComplex")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "font-name-complex");
          } else {
            String fontName = (String) value;
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "style:font-name-complex", fontName);
          }
        } else if (key.equals("vertAlign")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-position");
          } else {
            String alignment = (String) value;
            if (alignment.equals("sub")) {
              propertiesElement.setStyleTextPositionAttribute("sub");
            } else if (alignment.equals("super")) {
              propertiesElement.setStyleTextPositionAttribute("super");
            } else { // baseline
              propertiesElement.setStyleTextPositionAttribute("0% 100%");
            }
          }
        } else if (key.equals("strike")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-position");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-color");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-mode");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-style");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-text");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-text-style");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-type");
            propertiesElement.removeAttributeNS(
                OdfDocumentNamespace.STYLE.getUri(), "text-line-through-width");
          } else {
            String strikeType = (String) value;
            if (strikeType.equals("single")) {
              propertiesElement.setStyleTextLineThroughTypeAttribute("single");
              propertiesElement.setStyleTextLineThroughStyleAttribute("solid");
              propertiesElement.setStyleTextLineThroughModeAttribute("continuous");
              propertiesElement.setStyleTextUnderlineModeAttribute("continuous");
              propertiesElement.setStyleTextOverlineModeAttribute("continuous");
            } else if (!strikeType.equals("none")) { // double
              propertiesElement.setStyleTextLineThroughTypeAttribute("double");
              propertiesElement.setStyleTextLineThroughStyleAttribute("solid");
              propertiesElement.setStyleTextLineThroughModeAttribute("continuous");
              propertiesElement.setStyleTextUnderlineModeAttribute("continuous");
              propertiesElement.setStyleTextOverlineModeAttribute("continuous");
            }
          }
        } else if (key.equals("language")) {
          language = value;
        } else if (key.equals("noProof")) {
          noProof = value;
        } else /*
               The defined values for the fo:line-height attribute are:
               * a value of type nonNegativeLength
               * normal: disables the effects of style:line-height-at-least and style:line-spacing.
               * a value of type percent  */
        //				<attribute name="fo:line-height">
        //					<choice>
        //						<value>normal</value>
        //						<ref name="nonNegativeLength"/>
        //						<ref name="percent"/>
        //					</choice>
        //				</attribute>
        // { type: 'percent', value: 100 }
        if (key.equals("letterSpacing")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "letter-spacing");
          } else {
            if (value.equals("normal")) {
              propertiesElement.setFoLetterSpacingAttribute("normal");
            } else {
              propertiesElement.setFoLetterSpacingAttribute(
                  (getSafelyInteger(value)) / 100.0 + "mm");
            }
          }
        } else if (key.equals("url")) {
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
          } else {
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.XLINK.getUri(), "xlink:href", (String) value);
          }
        }
      }
      if (noProof != null || language != null) {
        Object newLanguage = language;
        if ((noProof instanceof Boolean && (Boolean) noProof)
            || ((language instanceof String) && ((String) language).equals("none"))) {
          propertiesElement.setFoLanguageAttribute("zxx");
          propertiesElement.setStyleLanguageAsianAttribute("zxx");
          propertiesElement.setStyleCountryComplexAttribute("zxx");
          propertiesElement.setFoCountryAttribute("none");
          propertiesElement.setStyleCountryAsianAttribute("none");
          propertiesElement.setStyleCountryComplexAttribute("none");
        } else if (newLanguage == null || newLanguage.equals(JSONObject.NULL)) {
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "country");
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "language");
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "country-asian");
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "language-asian");
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "country-complex");
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "language-complex");
        } else {
          String locale = (String) newLanguage;
          if (!locale.isEmpty()) {
            int delimiterPos = locale.indexOf('-');
            if (delimiterPos > -1) {
              propertiesElement.setFoLanguageAttribute(locale.substring(0, delimiterPos));
              propertiesElement.setFoCountryAttribute(
                  locale.substring(delimiterPos + 1, locale.length()));
            } else {
              propertiesElement.setFoLanguageAttribute(locale);
            }
          }
        }
      }
    }
  }

  //	DEFAULT FONTS ADDED TO APACHE OPEN OFFICE
  //		<style:font-face style:name="Andale Mono" svg:font-family="&apos;Andale Mono&apos;"/>
  //		<style:font-face style:name="Mangal1" svg:font-family="Mangal"/>
  //		<style:font-face style:name="Palatino" svg:font-family="Palatino"
  // style:font-family-generic="roman"/>
  //		<style:font-face style:name="Times" svg:font-family="Times"
  // style:font-family-generic="roman"/>
  //		<style:font-face style:name="Helvetica" svg:font-family="Helvetica"
  // style:font-family-generic="swiss"/>
  //		<style:font-face style:name="Consolas" svg:font-family="Consolas"
  // style:font-family-generic="modern" style:font-pitch="fixed"/>
  //		<style:font-face style:name="Courier" svg:font-family="Courier"
  // style:font-family-generic="modern" style:font-pitch="fixed"/>
  //		<style:font-face style:name="Courier New" svg:font-family="&apos;Courier New&apos;"
  // style:font-family-generic="modern" style:font-pitch="fixed"/>
  //		<style:font-face style:name="Book Antiqua" svg:font-family="&apos;Book Antiqua&apos;"
  // style:font-family-generic="roman" style:font-pitch="variable"/>
  //		<style:font-face style:name="Cambria" svg:font-family="Cambria"
  // style:font-family-generic="roman" style:font-pitch="variable"/>
  //		<style:font-face style:name="Georgia" svg:font-family="Georgia"
  // style:font-family-generic="roman" style:font-pitch="variable"/>
  //		<style:font-face style:name="Times New Roman" svg:font-family="&apos;Times New Roman&apos;"
  // style:font-family-generic="roman" style:font-pitch="variable"/>
  //		<style:font-face style:name="Arial" svg:font-family="Arial" style:font-family-generic="swiss"
  // style:font-pitch="variable"/>
  //		<style:font-face style:name="Calibri" svg:font-family="Calibri"
  // style:font-family-generic="swiss" style:font-pitch="variable"/>
  //		<style:font-face style:name="Impact" svg:font-family="Impact"
  // style:font-family-generic="swiss" style:font-pitch="variable"/>
  //		<style:font-face style:name="Tahoma" svg:font-family="Tahoma"
  // style:font-family-generic="swiss" style:font-pitch="variable"/>
  //		<style:font-face style:name="Verdana" svg:font-family="Verdana"
  // style:font-family-generic="swiss" style:font-pitch="variable"/>
  //		<style:font-face style:name="Mangal" svg:font-family="Mangal"
  // style:font-family-generic="system" style:font-pitch="variable"/>
  //		<style:font-face style:name="Microsoft YaHei" svg:font-family="&apos;Microsoft YaHei&apos;"
  // style:font-family-generic="system" style:font-pitch="variable"/>
  //		<style:font-face style:name="SimSun" svg:font-family="SimSun"
  // style:font-family-generic="system" style:font-pitch="variable"/>
  // ADDED FROM MSO 15 export
  /*

  <style:font-face style:name="Cambria" svg:font-family="Cambria" style:font-family-generic="roman" style:font-pitch="variable" svg:panose-1="2 4 5 3 5 4 6 3 2 4"/>
  <style:font-face style:name="MS Mincho" svg:font-family="MS Mincho" style:font-family-generic="modern" style:font-pitch="fixed" svg:panose-1="2 2 6 9 4 2 5 8 3 4"/>
  <style:font-face style:name="Times New Roman" svg:font-family="Times New Roman" style:font-family-generic="roman" style:font-pitch="variable" svg:panose-1="2 2 6 3 5 4 5 2 3 4"/>
  <style:font-face style:name="Calibri" svg:font-family="Calibri" style:font-family-generic="swiss" style:font-pitch="variable" svg:panose-1="2 15 5 2 2 2 4 3 2 4"/>
  <style:font-face style:name="MS Gothic" svg:font-family="MS Gothic" style:font-family-generic="modern" style:font-pitch="fixed" svg:panose-1="2 11 6 9 7 2 5 8 2 4"/>

  */
  private static void addFontToDocument(String fontName, OdfDocument doc) {
    if (doc != null) {

      Set fontNames = doc.getFontNames();
      if (fontName != null && !fontName.isEmpty()) {
        if (!fontNames.contains(fontName)) {
          fontNames.add(fontName);
          if (fontName.equals("Andale Mono")) {
            addFontData(doc, "Andale Mono", null, "Andale Mono", null, null, null);
          } else if (fontName.equals("Arial")) {
            addFontData(doc, "Arial", null, "Arial", "swiss", "variable", null);
          } else if (fontName.equals("Book Antiqua")) {
            addFontData(doc, "Book Antiqua", null, "Book Antiqua", "roman", "variable", null);
          } else if (fontName.equals("Calibri")) {
            addFontData(
                doc, "Calibri", null, "Calibri", "swiss", "variable", "2 15 5 2 2 2 4 3 2 4");
          } else if (fontName.equals("Cambria")) {
            addFontData(
                doc, "Cambria", null, "Cambria", "roman", "variable", "2 4 5 3 5 4 6 3 2 4");
          } else if (fontName.equals("Consolas")) {
            addFontData(doc, "Consolas", null, "Consolas", "modern", "fixed", null);
          } else if (fontName.equals("Courier New")) {
            addFontData(doc, "Courier New", null, "Courier New", "modern", "fixed", null);
          } else if (fontName.equals("Courier")) {
            addFontData(doc, "Courier", null, "Courier", "modern", "fixed", null);
          } else if (fontName.equals("Georgia")) {
            addFontData(doc, "Georgia", null, "Georgia", "roman", "variable", null);
          } else if (fontName.equals("Helvetica")) {
            addFontData(doc, "Helvetica", null, "Helvetica", "swiss", null, null);
          } else if (fontName.equals("Impact")) {
            addFontData(doc, "Impact", null, "Impact", "swiss", "variable", null);
          } else if (fontName.equals("Mangal")) {
            addFontData(doc, "Mangal", null, "Mangal", "system", "variable", null);
          } else if (fontName.equals("Mangal1")) {
            addFontData(doc, "Mangal1", null, "Mangal", null, null, null);
          } else if (fontName.equals("Microsoft YaHei")) {
            addFontData(
                doc, "Microsoft YaHei", null, "Microsoft YaHei", "system", "variable", null);
          } else if (fontName.equals("MS Gothic")) {
            addFontData(
                doc, "MS Gothic", null, "MS Gothic", "modern", "fixed", "2 11 6 9 7 2 5 8 2 4");
          } else if (fontName.equals("MS Mincho")) {
            addFontData(
                doc, "MS Mincho", null, "MS Mincho", "modern", "fixed", "2 2 6 9 4 2 5 8 3 4");
          } else if (fontName.equals("Palatino")) {
            addFontData(doc, "Palatino", null, "Palatino", "roman", null, null);
          } else if (fontName.equals("SimSun")) {
            addFontData(doc, "SimSun", null, "SimSun", "system", "variable", null);
          } else if (fontName.equals("Tahoma")) {
            addFontData(doc, "Tahoma", null, "Tahoma", "swiss", "variable", null);
          } else if (fontName.equals("Times New Roman")) {
            addFontData(
                doc,
                "Times New Roman",
                null,
                "Times New Roman",
                "roman",
                "variable",
                "2 2 6 3 5 4 5 2 3 4");
          } else if (fontName.equals("Times")) {
            addFontData(doc, "Times", null, "Times", "roman", null, null);
          } else if (fontName.equals("Verdana")) {
            addFontData(doc, "Verdana", null, "Verdana", "swiss", "variable", null);
          }
        }
      }
    }
  }

  public static void mapParagraphProperties(
      JSONObject attrs, StyleParagraphPropertiesElement propertiesElement) {

    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        addBorderProperties(key, attrs, propertiesElement, null);
        addPaddingProperties(key, attrs, propertiesElement);
        addMarginProperties(key, attrs, propertiesElement);
        // fo:background-color
        /*
        <attribute name="fo:background-color">
        <choice>
        <value>transparent</value>
        <ref name="color"/>
        </choice>
        </attribute>
        <define name="color">
        <data type="string">
        <param name="pattern">#[0-9a-fA-F]{6}</param>
        </data>
        </define>
        */
        if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else /*
               The defined values for the fo:line-height attribute are:
               * a value of type nonNegativeLength
               * normal: disables the effects of style:line-height-at-least and style:line-spacing.
               * a value of type percent  */
        //				<attribute name="fo:line-height">
        //					<choice>
        //						<value>normal</value>
        //						<ref name="nonNegativeLength"/>
        //						<ref name="percent"/>
        //					</choice>
        //				</attribute>
        // { type: 'percent', value: 100 }
        if (key.equals("lineHeight")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "line-height");
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "line-height-at-least");
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "line-spacing");
            } else {
              JSONObject lineHeight = (JSONObject) value;
              setLineHeight(lineHeight, propertiesElement);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } // One of 'left', 'center', 'right', or 'justify'.
        // start, end, left, right, center or justify.
        else if (key.equals("alignment")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "text-align");
            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "fo:text-align", (String) value);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } // <attribute name="fo:text-indent">
        //	<choice>
        //		<ref name="length"/>
        //		<ref name="percent"/>
        //	</choice>
        // </attribute>
        else if (key.equals("indentFirstLine")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "text-indent");
            } else {
              int indent = getSafelyInteger(value);
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "fo:text-indent", ((indent / 100.0) + "mm"));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } // <attribute name="fo:break-before">
        //	<choice>
        //		<value>auto</value>
        //		<value>column</value>
        //		<value>page</value>
        //	</choice>
        // </attribute>
        else if (key.equals("pageBreakBefore")) {
          try {
            Object value = attrs.get(key);
            if (value != JSONObject.NULL && value.equals(Boolean.TRUE)) {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "fo:break-before", "page");
            } else {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "break-before");
            }
            // there can not be before and after break at the same paragraph
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "break-after");
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } // <attribute name="fo:break-after">
        //	<choice>
        //		<value>auto</value>
        //		<value>column</value>
        //		<value>page</value>
        //	</choice>
        // </attribute>
        else if (key.equals("pageBreakAfter")) {
          try {
            Object value = attrs.get(key);
            if (value != JSONObject.NULL && value.equals(Boolean.TRUE)) {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "fo:break-after", "page");
            } else {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "break-after");
            }
            // there can not be before and after break at the same paragraph
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "break-before");
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("tabStops")) {
          try {
            Object value = attrs.get(key);
            StyleTabStopsElement tabsElement =
                OdfElement.findFirstChildNode(StyleTabStopsElement.class, propertiesElement);
            if (tabsElement != null) {
              propertiesElement.removeChild(tabsElement);
            }
            if (value == null || value.equals(JSONObject.NULL)) {
              // node already removed
            } else {
              JSONArray tabsValue = (JSONArray) value;

              OdfFileDom fileDom = (OdfFileDom) propertiesElement.getOwnerDocument();
              tabsElement = new StyleTabStopsElement(fileDom);
              for (int idx = 0; idx < tabsValue.length(); ++idx) {
                JSONObject tab = tabsValue.getJSONObject(idx);
                StyleTabStopElement tabElement = new StyleTabStopElement(fileDom);
                if (tab.has("pos")) {
                  int tabPos = tab.getInt("pos");
                  tabElement.setStylePositionAttribute((tabPos / 1000F) + "cm");
                }
                if (tab.has("value")) {
                  String tabValue = tab.getString("value");
                  if (tabValue.equals("decimal")) {
                    tabValue = "char";
                  } else if (tabValue.equals("bar")) {
                    tabValue = "left";
                  } else if (tabValue.equals("clear")) {
                    continue; // clear unsupported
                  }
                  tabElement.setStyleTypeAttribute(tabValue); // center, char, left, right
                }
                tabsElement.insertBefore(tabElement, null);
              }

              propertiesElement.insertBefore(tabsElement, null);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapTableProperties(
      JSONObject attrs, StyleTablePropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      boolean isTableWidthGiven = false;
      for (String key : propKeys) {
        // no padding, no border
        addMarginProperties(key, attrs, propertiesElement);
        if (key.equals("width")) {
          Object value = null;
          try {
            value = attrs.get(key);
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }

          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "width");
          } else {
            Object width = value;
            if (width != null && !width.equals(MapHelper.AUTO)) {
              // MSO 15 WORKAROUND..
              isTableWidthGiven = true;
              if (width instanceof Integer) {
                propertiesElement.setStyleWidthAttribute(((Integer) width) / 1000.0 + "cm");
              } else if (width instanceof String) {
                propertiesElement.setStyleWidthAttribute(
                    Integer.parseInt((String) width) / 1000.0 + "cm");
              }
              // LO/AO0 WORKSROUND: if there is a rel width with 100% and an absolute style the
              // rel-width wins :(
              // WIDTH COMES - REL-WIDTH GOES...
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "rel-width");
              // LO/AOO WORKAROUND: width is only recognized by LO/AO, when an alignment is
              // provided. If none now left (or margin equal alignment is set to left!
              if (!propertiesElement.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "align")
                  || propertiesElement
                      .getAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "align")
                      .equalsIgnoreCase("margins")) {
                propertiesElement.setTableAlignAttribute("left");
              }
            }
          }
        } else if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("visible")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "display");
            } else {
              propertiesElement.setTableDisplayAttribute((Boolean) value);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
          // <attribute name="fo:break-before">
          //	<choice>
          //		<value>auto</value>
          //		<value>column</value>
          //		<value>page</value>
          //	</choice>
          // </attribute>
        }
      }
      // MSO 15 WORKAROUND:
      if (!isTableWidthGiven) {
        // by default at least a 100 percent relative width for the table have to be given for MSO15
        propertiesElement.setStyleRelWidthAttribute(HUNDRED_PERCENT);
      }
    } else {
      if (propertiesElement != null) {
        // by default at least a 100 percent relative width for the table have to be given for MSO15
        propertiesElement.setStyleRelWidthAttribute(HUNDRED_PERCENT);
      }
    }
  }

  public static void mapPageProperties(
      JSONObject attrs, StylePageLayoutPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        addBorderProperties(key, attrs, propertiesElement, null);
        addPaddingProperties(key, attrs, propertiesElement);
        addMarginProperties(key, attrs, propertiesElement);
        if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapRowProperties(
      JSONObject attrs, StyleTableRowPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("height")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "row-height");
              // currently we are not differentiating between height and minimum height
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "min-row-height");
            } else {
              int rowHeight = getSafelyInteger(value);
              propertiesElement.setStyleRowHeightAttribute(((rowHeight / 100.0) + "mm"));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("customHeight")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "use-optimal-row-height");
            } else {
              propertiesElement.setStyleUseOptimalRowHeightAttribute(!((Boolean) value));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapCellProperties(
      JSONObject attrs, StyleTableCellPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        // No margin in ODF
        addBorderProperties(key, attrs, propertiesElement, null);
        addPaddingProperties(key, attrs, propertiesElement);
        if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("alignVert")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "vertical-align");
            } else {
              String align = (String) value;
              propertiesElement.setStyleVerticalAlignAttribute(align);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapColumnProperties(
      JSONObject attrs, StyleTableColumnPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        if (key.equals("width")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "column-width");
            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(),
                  "style:column-width",
                  (getSafelyInteger(value) / 100.0) + "mm");
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("customWidth")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "use-optimal-column-width");
            } else {
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(),
                  "style:use-optimal-column-width",
                  value.toString());
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapListProperties(
      JSONObject attrs, StyleListLevelPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        if (key.equals("fontName")) {
          try {
            Object value = attrs.get(key);
            // == null does not work here..
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "font-name");
            } else {
              String fontName = (String) value;
              propertiesElement.setStyleFontNameAttribute(fontName);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("fontNameAsian")) {
          try {
            Object value = attrs.get(key);
            // == null does not work here..
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "font-name-asian");
            } else {
              String fontName = (String) value;
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-name-asian", fontName);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("fontNameComplex")) {
          try {
            Object value = attrs.get(key);
            // == null does not work here..
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "font-name-complex");
            } else {
              String fontName = (String) value;
              propertiesElement.setAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "style:font-name-complex", fontName);
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapSectionProperties(
      JSONObject attrs, StyleSectionPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        // No margin in ODF
        addBorderProperties(key, attrs, propertiesElement, null);
        addPaddingProperties(key, attrs, propertiesElement);
        if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  public static void mapGraphicProperties(
      JSONObject attrs,
      StyleGraphicPropertiesElement propertiesElement,
      OdfStyleBase frameParentStyle) {
    if (attrs != null) {
      boolean isMirroredHorizontalRemoved = false;
      boolean isMirroredHorizontal = false;
      boolean isMirroredVerticalRemoved = false;
      boolean isMirroredVertical = false;
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        addBorderProperties(key, attrs, propertiesElement, frameParentStyle);
        addPaddingProperties(key, attrs, propertiesElement);
        addMarginProperties(key, attrs, propertiesElement);
        if (key.equals("fill")) {
          try {
            JSONObject value = (JSONObject) attrs.get(key);
            boolean flyFrame =
                frameParentStyle != null && !(frameParentStyle instanceof OdfDefaultStyle);
            if (value == null
                || value.equals(JSONObject.NULL)
                || (value.has(OPK_TYPE)
                    && (value.isNull(OPK_TYPE) || value.getString(OPK_TYPE).equals("none")))) {
              if (flyFrame) {
                propertiesElement.removeAttributeNS(
                    OdfDocumentNamespace.FO.getUri(), "background-color");
              } else {
                propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "fill-color");
                propertiesElement.setAttributeNS(
                    OdfDocumentNamespace.DRAW.getUri(), "draw:fill", "none");
              }
            } else {
              String colorAttr = null;
              // sometimes a JSONObject, e.g. color":{OPK_TYPE:"rgb","value":"ff00ff"} within
              //    attrs":{"character":{"color":{OPK_TYPE:"rgb","value":"0000ff"}
              // sometimes a String "fill":{"color":"ffffff"}
              Object color = value.get("color");
              if (color instanceof JSONObject) {
                colorAttr = getColor((JSONObject) color, MapHelper.TRANSPARENT);
              } else {
                colorAttr = (String) color;
              }
              if (flyFrame) {
                propertiesElement.setFoBackgroundColorAttribute(colorAttr);
              } else {
                propertiesElement.setAttributeNS(
                    OdfDocumentNamespace.DRAW.getUri(), "draw:fill-color", colorAttr);
                propertiesElement.setAttributeNS(
                    OdfDocumentNamespace.DRAW.getUri(), "draw:fill", "solid");
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }

        } else if (key.equals("flipH")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              isMirroredHorizontalRemoved = true;
            } else if ((Boolean) value) {
              isMirroredHorizontal = true;
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("flipV")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              isMirroredVerticalRemoved = true;
            } else if ((Boolean) value) {
              isMirroredVertical = true;
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("anchorHorBase")) {
          try {
            Object value = attrs.get(key);
            if (value != null && !value.equals(JSONObject.NULL)) {
              String horBase = (String) value;
              Map<String, String> relMap = new HashMap<>();
              relMap.put("margin", "page-start-margin");
              relMap.put("page", "page");
              relMap.put("column", "paragraph");
              relMap.put("character", "char");
              relMap.put("leftMargin", "page-start-margin");
              relMap.put("rightMargin", "page-end-margin");
              relMap.put(
                  "insideMargin", "page-start-margin"); // TODO: set horizontal-postion:from-inside
              relMap.put(
                  "outsideMargin", "page-start-margin"); // TODO: set horizontal-postion:outside
              String odfValue = relMap.get(horBase);
              if (odfValue != null) {
                propertiesElement.setStyleHorizontalRelAttribute(odfValue);
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("anchorVertBase")) {
          try {
            Object value = attrs.get(key);
            if (value != null && !value.equals(JSONObject.NULL)) {
              String horBase = (String) value;
              Map<String, String> relMap = new HashMap<>();
              relMap.put("margin", "page-content");
              relMap.put("page", "page");
              relMap.put("paragraph", "paragraph");
              relMap.put("line", "line");
              relMap.put("topMargin", "page-content");
              relMap.put("bottomMargin", "page-content");
              String odfValue = relMap.get(horBase);
              if (odfValue != null) {
                propertiesElement.setStyleVerticalRelAttribute(odfValue);
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else /* http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#property-style_wrap
               The style:wrap attribute specifies how text is displayed around a frame or graphic object.
               The defined values for the style:wrap attribute are:
               * biggest: text may wrap around the shape where the difference to the left or right page or column border is largest.
               * Mode=square Side=largest
               * dynamic: text may wrap around both sides of the shape. The space for wrapping is set by the style:wrap-dynamic-threshold attribute. 20.393
               * UNSUPPORTED
               * left: text wraps around the left side of the shape.
               * Mode=square	Side=left
               * none: text does not wrap around the shape.
               * Mode=topAndBottom
               * parallel: text wraps around both sides of the shape.
               * Mode=square	Side=both
               * right: text wraps around the right side of the shape.
               * Mode=square	Side=right
               * run-through: text runs through the shape.
               * Mode=through	Side=both   */
        /*
        OX API: textWrapMode	One of 'none', 'square', 'tight', 'through', or 'topAndBottom'. (IMHO 'none' == 'topAndBottom', but the latter is implemented)
        OX API: textWrapSide	Sides where text wraps around the image (only used if textWrapMode is set to 'square', 'tight', or 'through') (1). One of ['both', 'left', 'right', 'largest'	*/ if (key
            .equals("textWrapMode")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "wrap");
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "wrap-contour");
            } else {
              String textWrapMode = (String) value;
              if (textWrapMode.equals("topAndBottom")) {
                propertiesElement.setStyleWrapAttribute("none");
                propertiesElement.setStyleWrapContourAttribute(false);
              } else {
                String textWrapSide = attrs.optString("textWrapSide");
                if (textWrapSide != null) {
                  if (textWrapMode.equals("square")) {
                    if (textWrapSide.equals("largest")) {
                      propertiesElement.setStyleWrapAttribute("biggest");
                    } else if (textWrapSide.equals("left")) {
                      propertiesElement.setStyleWrapAttribute("left");
                    } else if (textWrapSide.equals("both")) {
                      propertiesElement.setStyleWrapAttribute("parallel");
                    } else if (textWrapSide.equals("right")) {
                      propertiesElement.setStyleWrapAttribute("right");
                    }
                    propertiesElement.setStyleWrapContourAttribute(false);
                  } else if (textWrapMode.equals("through")) {
                    propertiesElement.setStyleWrapAttribute("run-through");
                    propertiesElement.setStyleWrapContourAttribute(false);
                  }
                }
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else /* @style:horizontal-pos:
               The defined values for the style:horizontal-pos attribute are:
               * center: horizontal alignment of a frame should be centered relative to the specified area.
               * anchorHorAlign=center
               * from-inside: on pages with an odd page number the left edge of the specific area is taken as the horizontal alignment of a frame. On pages with an even page number the right edge of the specified area is taken. Attribute svg:x associated with the frame element specifies the horizontal position of the frame from the edge which is taken.
               * UNSUPPORTED
               * from-left: the svg:x attribute associated with the frame element specifies the horizontal position of the frame from the left edge of the specified area.
               * anchorHorAlign=offset
               * inside: on pages with an odd page number the horizontal alignment of a frame is the same as for the attribute value left. On pages with an even page number the horizontal alignment of a frame is the same as for the attribute value right.
               * anchorHorAlign=inside
               * left: horizontal alignment of a frame should be left aligned relative to the specified area.
               * anchorHorAlign=left
               * outside: on pages with an odd page number the horizontal alignment of a frame is the same as for the attribute value right. On pages with an even page number the horizontal alignment of a frame is the same as for the attribute value left.
               * anchorHorAlign=outside
               * right: horizontal alignment of a frame should be right aligned relative to the specified area.
               * anchorHorAlign=right
               If the attribute value is not from-left and not from-inside, the svg:x attribute associated with the frame element is ignored for text documents.						 */
        // OX API:
        // anchorHorAlign: Horizontal anchor position:	One of 'left', 'right', 'center', 'inside',
        // 'outside', or 'offset'.
        // anchorHorOffset: Horizontal position offset (only used if anchorHorAlign is set to
        // 'offset')
        if (key.equals("anchorHorAlign")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "horizontal-pos");
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "horizontal-rel");
            } else {
              String anchorHorAlign = (String) value;
              if (anchorHorAlign.equals("center")) {
                propertiesElement.setStyleHorizontalPosAttribute("center");
              } else if (anchorHorAlign.equals("offset")) {
                propertiesElement.setStyleHorizontalPosAttribute("from-left");
              } else if (anchorHorAlign.equals("left")) {
                propertiesElement.setStyleHorizontalPosAttribute("left");
              } else if (anchorHorAlign.equals("right")) {
                propertiesElement.setStyleHorizontalPosAttribute("right");
              } else if (anchorHorAlign.equals("inside")) {
                propertiesElement.setStyleHorizontalPosAttribute("inside");
              } else if (anchorHorAlign.equals("outside")) {
                propertiesElement.setStyleHorizontalPosAttribute("outside");
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("anchorHorOffset")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x");
            } else {
              int x = getSafelyInteger(value);

              // This is the default in our constellation
              propertiesElement.setSvgXAttribute(x / 100.0 + "mm");
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("anchorVertAlign")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "vertical-pos");
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.STYLE.getUri(), "vertical-rel");
            } else {
              String anchorVertAlign = (String) value;
              if (anchorVertAlign.equals("center")) {
                propertiesElement.setStyleVerticalPosAttribute("center");
              } else if (anchorVertAlign.equals("offset")) {
                propertiesElement.setStyleVerticalPosAttribute("from-top");
              } else if (anchorVertAlign.equals("bottom")) {
                propertiesElement.setStyleVerticalPosAttribute("bottom");
              } else if (anchorVertAlign.equals("top")) {
                propertiesElement.setStyleVerticalPosAttribute("top");
              } else if (anchorVertAlign.equals("inside")) {
                propertiesElement.setStyleVerticalPosAttribute("inside");
              } else if (anchorVertAlign.equals("outside")) {
                propertiesElement.setStyleVerticalPosAttribute("outside");
              }
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("anchorVertOffset")) {
          try {
            Object value = attrs.get(key);
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y");
            } else {
              int y = getSafelyInteger(value);
              // This is the default in our constellation
              propertiesElement.setSvgYAttribute(y / 100.0 + "mm");
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        } else if (key.equals("anchorBehindDoc")) {
          boolean anchorBehindDoc = attrs.optBoolean(key, false);
          StyleRunThroughAttribute.Value attr = null;
          if (anchorBehindDoc) {
            attr = StyleRunThroughAttribute.Value.BACKGROUND;
          } else {
            attr = StyleRunThroughAttribute.Value.FOREGROUND;
          }
          propertiesElement.setStyleRunThroughAttribute(attr.toString());
        }
      }

      //					} else if (propName.contains("svg:x")) {
      //						int x = normalizeLength(odfProps.get("svg:x"));
      //						if(x != 0){
      //							newProps.put("anchorHorOffset", x);
      //						}
      // Two attributes are evaluated via boolean flags:
      if (isMirroredHorizontalRemoved && isMirroredVerticalRemoved) {
        propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "mirror");
      } else if (isMirroredHorizontal && isMirroredVertical) {
        propertiesElement.setStyleMirrorAttribute("horizontal vertical");
      } else if (isMirroredVertical) {
        propertiesElement.setStyleMirrorAttribute("vertical");
      } else if (isMirroredHorizontal) {
        propertiesElement.setStyleMirrorAttribute("horizontal");
      }
    }
  }

  public static void mapChartProperties(
      JSONObject attrs, StyleChartPropertiesElement propertiesElement) {}

  public static void mapDrawingProperties(
      JSONObject attrs, StyleDrawingPagePropertiesElement propertiesElement) {}

  public static void mapRubyProperties(
      JSONObject attrs, StyleRubyPropertiesElement propertiesElement) {}

  public static void mapHeaderFooterProperties(
      JSONObject attrs, StyleHeaderFooterPropertiesElement propertiesElement) {
    if (attrs != null) {
      Set<String> propKeys = attrs.keySet();
      for (String key : propKeys) {
        // No border, padding in ODF
        addMarginProperties(key, attrs, propertiesElement);
        if (key.equals("fillColor")) {
          try {
            Object value = attrs.get(key);
            // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
            if (value == null || value.equals(JSONObject.NULL)) {
              propertiesElement.removeAttributeNS(
                  OdfDocumentNamespace.FO.getUri(), "background-color");
            } else {
              JSONObject color = (JSONObject) value;
              propertiesElement.setFoBackgroundColorAttribute(
                  getColor(color, MapHelper.TRANSPARENT));
            }
          } catch (JSONException ex) {
            LOG.log(Level.SEVERE, null, ex);
          }
        }
      }
    }
  }

  //	fo:border="0.5pt solid #000000"
  // {\"width\":2,
  //  \"style\":\"solid\"
  //  \"color\":{\"value\":\"000000\",\"type\":\"rgb\"}}
  private static String getBorder(JSONObject border) {
    String style = null;
    String borderValue = "";
    if (border.has("style") && !border.get("style").equals(JSONObject.NULL)) {
      //  'none', 'single', 'double', 'dotted', 'dashed', 'outset', or 'inset'
      style = border.optString("style");
      if (style.equals("none")) {
        borderValue = "none";
      } else {
        if (border.has("width") && !border.get("width").equals(JSONObject.NULL)) {
          double width = border.optInt("width") / 100.0;
          borderValue = width + "mm ";
        } else {
          // DEFAULT BORDER: 0.002cm solid #000000"
          borderValue = "0.02mm ";
        }
        if (border.has("style") && !border.get("style").equals(JSONObject.NULL)) {
          //  'none', 'single', 'double', 'dotted', 'dashed', 'outset', or 'inset'
          style = border.optString("style");
          if (style.equals("single")) {
            borderValue = borderValue.concat("solid ");
          } else {
            borderValue = borderValue.concat(style + " ");
          }
        } else {
          // DEFAULT BORDER: 0.002cm solid #000000"
          borderValue = borderValue.concat("solid ");
        }
        if (border.has("color") && !border.get("color").equals(JSONObject.NULL)) {
          JSONObject color = border.optJSONObject("color");
          borderValue = borderValue.concat(getColor(color, BLACK));
        } else {
          // DEFAULT BORDER: 0.002cm solid #000000"
          borderValue = borderValue.concat("#000000");
        }
      }
    }
    return borderValue;
  }

  /** Supporting "normal", "percentage" and a none-negative number */
  private static void setLineHeight(
      JSONObject lineHeight, StyleParagraphPropertiesElement propertiesElement) {
    String lineHeightValue = null;
    try {
      String type = lineHeight.getString(OPK_TYPE);
      if (type.equals("percent")) {
        String value = lineHeight.optString("value");
        if (value != null && !value.isEmpty()) {
          lineHeightValue = value.concat("%");
          propertiesElement.setFoLineHeightAttribute(lineHeightValue);
          propertiesElement.removeAttributeNS(
              OdfDocumentNamespace.STYLE.getUri(), "line-height-at-least");
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "line-spacing");
        }
      } else if (type.equals("fixed")) {
        double value = lineHeight.optInt("value") / 100.0;
        lineHeightValue = value + "mm";
        propertiesElement.setFoLineHeightAttribute(lineHeightValue);
        propertiesElement.removeAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "line-height-at-least");
        propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "line-spacing");
      } else if (type.equals("atLeast")) {
        double value = lineHeight.optInt("value") / 100.0;
        lineHeightValue = value + "mm";
        propertiesElement.setStyleLineHeightAtLeastAttribute(lineHeightValue);
        propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "line-height");
        propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "line-spacing");

      } else if (type.equals("leading")) {
        double value = lineHeight.optInt("value") / 100.0;
        lineHeightValue = value + "mm";
        propertiesElement.setStyleLineSpacingAttribute(lineHeightValue);
        propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "line-height");
        propertiesElement.removeAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "line-height-at-least");
      } else if (type.equals("normal")) {
        lineHeightValue = "normal";
        propertiesElement.removeAttributeNS(
            OdfDocumentNamespace.STYLE.getUri(), "line-height-at-least");
        propertiesElement.removeAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "line-spacing");
      }
    } catch (JSONException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
  }

  public static String getColor(JSONObject color, String autoColor) {
    String colorValue = "";
    try {
      if (color != null) {
        String type = color.getString(OPK_TYPE);
        if (type.equals(MapHelper.AUTO)) {
          colorValue = autoColor;
        } else if (type.equals("rgb")) {
          colorValue = '#' + color.getString("value");
        } else if (color.has("fallbackValue")) {
          // {"color":{OPK_TYPE:"scheme","value":"accent1","transformations":[{OPK_TYPE:"shade","value":74902}],"fallbackValue":"376092"}
          colorValue = '#' + color.getString("fallbackValue");
        } else {
          LOG.warning("Unmappable color: " + color);
        }
      }
    } catch (JSONException ex) {
      // {"value":"text2",OPK_TYPE:"scheme","transformations":[{"value":60000,OPK_TYPE:"tint"}]}
      LOG.log(Level.SEVERE, null, ex);
    }
    return colorValue;
  }

  public static void addBorderProperties(
      String key,
      JSONObject attrs,
      OdfStylePropertiesBase propertiesElement,
      OdfStyleBase frameParentStyle) {
    boolean putBorders = false;
    if (frameParentStyle != null
        && attrs.has("line")
        && !attrs.get("line").equals(JSONObject.NULL)) {
      try {
        boolean isEmptyBorder = true;
        String allBorderString = propertiesElement.getAttribute("fo:border");
        String leftBorderString = propertiesElement.getAttribute("fo:border-left");
        String rightBorderString = propertiesElement.getAttribute("fo:border-right");
        String topBorderString = propertiesElement.getAttribute("fo:border-top");
        String bottomBorderString = propertiesElement.getAttribute("fo:border-bottom");
        boolean useOneBorder = allBorderString.length() > 0;
        if (!useOneBorder) {
          OdfStylePropertiesBase propsElement =
              frameParentStyle.getOrCreatePropertiesElement(
                  OdfStylePropertiesSet.GraphicProperties);
          String styleAllBorderString = propsElement.getAttribute("fo:border");
          if (leftBorderString.length() == 0) {
            leftBorderString = propsElement.getAttribute("fo:border-left");
          }
          if (rightBorderString.length() == 0) {
            rightBorderString = propsElement.getAttribute("fo:border-right");
          }
          if (topBorderString.length() == 0) {
            topBorderString = propsElement.getAttribute("fo:border-top");
          }
          if (bottomBorderString.length() == 0) {
            bottomBorderString = propsElement.getAttribute("fo:border-bottom");
          }
          if (styleAllBorderString.length() > 0
              && leftBorderString.length() == 0
              && rightBorderString.length() == 0
              && topBorderString.length() == 0
              && bottomBorderString.length() == 0) {
            allBorderString = styleAllBorderString;
            useOneBorder = true;
          } else {
            useOneBorder = false;
          }
        }

        JSONObject allBorder = MapHelper.createBorderMap(allBorderString);
        JSONObject oldLeftBorder = MapHelper.createBorderMap(leftBorderString);
        JSONObject oldRightBorder = MapHelper.createBorderMap(rightBorderString);
        JSONObject oldTopBorder = MapHelper.createBorderMap(topBorderString);
        JSONObject oldBottomBorder = MapHelper.createBorderMap(bottomBorderString);
        isEmptyBorder =
            useOneBorder
                ? (!allBorder.has("width") || !allBorder.has("style"))
                : (!oldTopBorder.has("width") || !oldTopBorder.has("style"));

        JSONObject line = attrs.getJSONObject("line");
        JSONObject lineColor = line.optJSONObject("color");
        boolean lineColorIsNull = line.has("color") && line.isNull("color");
        boolean lineTypeIsNull = line.has(OPK_TYPE) && line.isNull(OPK_TYPE);
        boolean lineWidthIsNull = line.has("width") && line.isNull("width");
        boolean lineStyleIsNull = line.has("style") && line.isNull("style");
        if (lineStyleIsNull && lineTypeIsNull && lineWidthIsNull) {
          putBorders = true;
          useOneBorder = true;
          for (String borderKey : allBorder.keySet()) {
            allBorder.remove(borderKey);
          }
        } else {
          String lineType = line.optString(OPK_TYPE);
          String lineStyle = line.optString("style");
          int width = line.optInt("width");
          if (lineColor != null) {
            if (useOneBorder) {
              allBorder.put("color", lineColor);
            } else {
              oldLeftBorder.put("color", lineColor);
              oldRightBorder.put("color", lineColor);
              oldTopBorder.put("color", lineColor);
              oldBottomBorder.put("color", lineColor);
            }
            putBorders = true;
            if (isEmptyBorder) {
              if (lineStyle.isEmpty()) {
                lineStyle = "solid";
              }
              if (width == 0) {
                width = 1;
              }
            }
          } else if (lineColorIsNull) {
            if (useOneBorder) {
              allBorder.remove("color");
            } else {
              oldLeftBorder.remove("color");
              oldRightBorder.remove("color");
              oldTopBorder.remove("color");
              oldBottomBorder.remove("color");
            }
            putBorders = true;
          }
          if (lineType != null
              && lineStyle != null
              && (lineStyle.length() > 0 || (lineType.equals("none")))) {
            // type: none, auto, solid
            // style:               solid,          dotted, dashed, dashDot, dashDotDot
            // border-style: none,   single, double, dotted, dashed, dashDot, dashDotDot
            String newStyle = lineStyle;
            ;
            if (lineType.equals("none")) {
              newStyle = "none";
            } else if (lineStyle.equals("solid")) {
              newStyle = "single";
            }
            if (useOneBorder) {
              allBorder.put("style", newStyle);
            } else {
              oldLeftBorder.put("style", newStyle);
              oldRightBorder.put("style", newStyle);
              oldTopBorder.put("style", newStyle);
              oldBottomBorder.put("style", newStyle);
            }
            if (isEmptyBorder && width == 0 && !newStyle.equals("none")) {
              width = 1;
            }
            putBorders = true;
          }
          if (width > 0) {
            if (useOneBorder) {
              allBorder.put("width", width);
            } else {
              oldLeftBorder.put("width", width);
              oldRightBorder.put("width", width);
              oldTopBorder.put("width", width);
              oldBottomBorder.put("width", width);
            }
            putBorders = true;
          }
        }
        if (putBorders) {
          JSONObject copyAttrs = new JSONObject(attrs);
          copyAttrs.put("borderLeft", useOneBorder ? allBorder : oldLeftBorder);
          copyAttrs.put("borderRight", useOneBorder ? allBorder : oldRightBorder);
          copyAttrs.put("borderTop", useOneBorder ? allBorder : oldTopBorder);
          copyAttrs.put("borderBottom", useOneBorder ? allBorder : oldBottomBorder);
          attrs = copyAttrs;
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "border");
        }
      } catch (JSONException e) {
        // no handling required
      }
    }
    //        if (key.contains("border")) {

    //			if (key.equals("border")) {
    //				try {
    //					Object value = attrs.get(key);
    //					// ToDo: Test with latest JSON Library and adapt accordingly for all occurences
    //					if (value == null || value.equals(JSONObject.NULL)) {
    //						propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "border");
    //					} else {
    //						// {\"width\":2,
    //						// \"style\":\"solid\"
    //						// \"color\":{\"value\":\"000000\",\"type\":\"rgb\"}}
    //						JSONObject border = (JSONObject) value;
    //						propertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(),"fo:border",
    // getBorder(border));
    //					}
    //				} catch (JSONException ex) {
    //					LOG.log(Level.SEVERE, null, ex);
    //				}
    //			} else
    if (putBorders || key.equals("borderLeft")) {
      try {
        Object value = attrs.get("borderLeft");
        // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
        if (value == null || value.equals(JSONObject.NULL)) {
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-left");
        } else {
          // {\"width\":2,
          // \"style\":\"solid\"
          // \"color\":{\"value\":\"000000\",\"type\":\"rgb\"}}
          JSONObject border = (JSONObject) value;
          propertiesElement.setAttributeNS(
              OdfDocumentNamespace.FO.getUri(), "fo:border-left", getBorder(border));
          // oppose to the other border styles, the padding was added to border
          if (border.has("space") && !border.get("space").equals(JSONObject.NULL)) {
            double padding = border.optInt("space") / 100.0;
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-left", padding + "mm");
          }
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    if (putBorders || key.equals("borderRight")) {
      try {
        Object value = attrs.get("borderRight");
        // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
        if (value == null || value.equals(JSONObject.NULL)) {
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-right");
        } else {
          // {\"width\":15,
          // \"style\":\"solid\"
          // \"color\":{\"value\":\"000000\",\"type\":\"rgb\"}} / // \"color\":{\"type\":\"auto\"}}
          // \"space\":\140
          JSONObject border = (JSONObject) value;
          propertiesElement.setAttributeNS(
              OdfDocumentNamespace.FO.getUri(), "fo:border-right", getBorder(border));
          // oppose to the other border styles, the padding was added to border
          if (border.has("space") && !border.get("space").equals(JSONObject.NULL)) {
            double padding = border.optInt("space") / 100.0;
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-right", padding + "mm");
          }
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    if (putBorders || key.equals("borderTop")) {
      try {
        Object value = attrs.get("borderTop");
        // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
        if (value == null || value.equals(JSONObject.NULL)) {
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-top");
        } else {
          // {\"width\":2,
          // \"style\":\"solid\"
          // \"color\":{\"value\":\"000000\",\"type\":\"rgb\"}}
          JSONObject border = (JSONObject) value;
          propertiesElement.setAttributeNS(
              OdfDocumentNamespace.FO.getUri(), "fo:border-top", getBorder(border));
          // oppose to the other border styles, the padding was added to border
          if (border.has("space") && !border.get("space").equals(JSONObject.NULL)) {
            double padding = border.optInt("space") / 100.0;
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-top", padding + "mm");
          }
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    if (putBorders || key.equals("borderBottom")) {
      try {
        Object value = attrs.get("borderBottom");
        // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
        if (value == null || value.equals(JSONObject.NULL)) {
          propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "border-bottom");
        } else {
          // {\"width\":2,
          // \"style\":\"solid\"
          // \"color\":{\"value\":\"000000\",\"type\":\"rgb\"}}
          JSONObject border = (JSONObject) value;
          propertiesElement.setAttributeNS(
              OdfDocumentNamespace.FO.getUri(), "fo:border-bottom", getBorder(border));
          // oppose to the other border styles, the padding was added to border
          if (border.has("space") && !border.get("space").equals(JSONObject.NULL)) {
            double padding = border.optInt("space") / 100.0;
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-bottom", padding + "mm");
          }
        }
      } catch (JSONException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    //        }
  }

  private static void addMarginProperties(
      String key, JSONObject attrs, OdfStylePropertiesBase propertiesElement) {
    if (key.contains("margin") || key.contains("indent")) {

      //			if (key.equals("margin")) {
      //				try {
      //					Object value = attrs.get(key);
      //					// ToDo: Test with latest JSON Library and adapt accordingly for all occurences
      //					if (value == null || value.equals(JSONObject.NULL)) {
      //						propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "margin");
      //					} else {
      //						Integer width = getSafelyInteger(value);
      //						propertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(),"margin", ((width /
      // 100.0) + "mm"));
      //					}
      //				} catch (JSONException ex) {
      //					LOG.log(Level.SEVERE, null, ex);
      //				}
      //			} else
      if (key.equals("marginBottom")) {
        try {
          Object value = attrs.get(key);
          // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "margin-bottom");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:margin-bottom", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else if (key.equals("marginLeft") || key.equals("indentLeft")) {

        // FIX API   			} else if (key.equals("indentLeft")) {
        try {
          Object value = attrs.get(key);
          // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "margin-left");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:margin-left", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else if (key.equals("marginRight") || key.equals("indentRight")) {
        // FIX API   			} else if (key.equals("indentRight")) {
        try {
          Object value = attrs.get(key);
          // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "margin-right");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:margin-right", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else if (key.equals("marginTop")) {
        try {
          Object value = attrs.get(key);
          // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "margin-top");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:margin-top", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  public static void addPaddingProperties(
      String key, JSONObject attrs, OdfStylePropertiesBase propertiesElement) {
    if (key.contains("padding")) {
      //				try {
      //					Object value = attrs.get(key);
      //					// ToDo: Test with latest JSON Library and adapt accordingly for all occurences
      //					if (value == null || value.equals(JSONObject.NULL)) {
      //						propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "padding");
      //					} else {
      //						Integer width = getSafelyInteger(value);
      //						propertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(),"fo:padding",
      // ((width / 100.0) + "mm"));
      //					}
      //				} catch (JSONException ex) {
      //					LOG.log(Level.SEVERE, null, ex);
      //				}
      ////			}
      //			else if (key.equals("padding")) {
      //				try {
      //					Object value = attrs.get(key);
      //					// ToDo: Test with latest JSON Library and adapt accordingly for all occurences
      //					if (value == null || value.equals(JSONObject.NULL)) {
      //						propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "padding");
      //					} else {
      //						Integer width = getSafelyInteger(value);
      //						propertiesElement.setAttributeNS(OdfDocumentNamespace.FO.getUri(),"fo:padding",
      // ((width / 100.0) + "mm"));
      //					}
      //				} catch (JSONException ex) {
      //					LOG.log(Level.SEVERE, null, ex);
      //				}
      //			} else

      if (key.equals("paddingBottom")) {
        try {
          Object value = attrs.get(key);
          // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "padding-bottom");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-bottom", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else if (key.equals("paddingLeft")) {
        try {
          Object value = attrs.get(key);
          // ToDo: Test with latest JSON Library and adapt accordingly for all occurences
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "padding-left");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-left", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else if (key.equals("paddingRight")) {
        try {
          Object value = attrs.get(key);
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "padding-right");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-right", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      } else if (key.equals("paddingTop")) {
        try {
          Object value = attrs.get(key);
          if (value == null || value.equals(JSONObject.NULL)) {
            propertiesElement.removeAttributeNS(OdfDocumentNamespace.FO.getUri(), "padding-top");
          } else {
            int width = getSafelyInteger(value);
            propertiesElement.setAttributeNS(
                OdfDocumentNamespace.FO.getUri(), "fo:padding-top", ((width / 100.0) + "mm"));
          }
        } catch (JSONException ex) {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
  }

  private static int getSafelyInteger(Object value) {
    int i;
    // defensive block in case JSON/Javascript/browser returns a Double
    if (value instanceof Double) {
      i = (int) Math.round((Double) value);
      LOG.log(Level.SEVERE, "The value should be an Integer, not a Double: {0}", value);
    } else if (value instanceof Float) {
      i = Math.round((Float) value);
      LOG.log(Level.SEVERE, "The value should be an Integer, not a Float: {0}", value);
    } else {
      i = (Integer) value;
    }
    return i;
  }

  private static final JSONArray decrementAll(JSONArray position) {
    if (position != null) {
      for (int i = 0; i < position.length(); i++) {
        position.put(i, ((Integer) position.get(i)) - 1);
      }
    }
    return position;
  }
}
