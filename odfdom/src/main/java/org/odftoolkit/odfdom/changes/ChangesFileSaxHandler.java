/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0.
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package org.odftoolkit.odfdom.changes;

import static org.odftoolkit.odfdom.changes.OperationConstants.CONFIG_MAX_TABLE_CELLS;
import static org.odftoolkit.odfdom.changes.OperationConstants.CONFIG_MAX_TABLE_COLUMNS;
import static org.odftoolkit.odfdom.changes.OperationConstants.CONFIG_MAX_TABLE_ROWS;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_STYLE_ID;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_DEFAULT;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_EVEN;
import static org.odftoolkit.odfdom.changes.PageArea.FOOTER_FIRST;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_DEFAULT;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_EVEN;
import static org.odftoolkit.odfdom.changes.PageArea.HEADER_FIRST;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfMetaDom;
import org.odftoolkit.odfdom.dom.OdfSchemaConstraint;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.OdfSettingsDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleableShapeElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawConnectorElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMeasureElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawShapeElementBase;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationEndElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderFooterPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListHeaderElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListStyleElement;
import org.odftoolkit.odfdom.dom.element.text.TextNoteCitationElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.dom.element.text.TextUserFieldDeclElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.pkg.OdfAttribute;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.pkg.OdfNamespace;
import org.odftoolkit.odfdom.pkg.OdfValidationException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/** @author svante.schubertATgmail.com */
public class ChangesFileSaxHandler extends org.odftoolkit.odfdom.pkg.OdfFileSaxHandler {

  private static final Logger LOG = Logger.getLogger(ChangesFileSaxHandler.class.getName());
  private static final String ROW_SPAN = "rowSpan";
  // ToDo: Fix API with its 'ugly' property name
  private static final String COLUMN_SPAN = "gridSpan";
  // ODF value types used for cell content
  private static final String LIBRE_OFFICE_MS_INTEROP_NAMESPACE =
      "urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0";
  private static final String LIBRE_OFFICE_MS_INTEROP_TYPE_CHECKBOX =
      "vnd.oasis.opendocument.field.FORMCHECKBOX";
  private static final String LIBRE_OFFICE_MS_INTEROP_CHECKBOX_UNICODE = "\u25A1";
  private static final Integer ONE = 1;

  public static String COMMENT_PREFIX = "cmt";
  // the empty XML file to which nodes will be added
  private OdfFileDom mFileDom;
  private JsonOperationProducer mJsonOperationProducer;
  private Map<String, TextListStyleElement> mAutoListStyles = null;
  private Map<String, TextUserFieldDeclElement> mUserFieldDecls = null;
  /**
   * Represents a stack of TextSpanElement. The text span will be added during startElement(..) with
   * the start address of text span And during endElement(..) the correct span will be returned and
   * the end address can be provided as well.
   */
  private final ArrayDeque<TextSelection> mTextSelectionStack;

  private final StringBuilder mCharsForElement = new StringBuilder();
  // Text for operations will be collected separately to allow to push not at every new delimiter
  // (e.g. span).
  // In addition will be <text:s/> in the output string exchange to spaces
  private final StringBuilder mCharsForOperation = new StringBuilder();
  // Gatheres the start text position for operations
  private boolean mIsCharsBeginning = true;
  List<Integer> mCharsStartPosition = null;
  private int mComponentDepth = -1; // as component depth starts with zero
  // the actual component. Linking each other building the tree view of the document
  private Component mCurrentComponent;
  // the postion of the component, being updated for the operations being generated
  private final LinkedList<Integer> mLastComponentPositions = new LinkedList<Integer>();
  /** DOM is created by default, but is in general not needed */
  private final boolean domCreationEnabled = true;
  //    private final ArrayDeque<ShapeProperties> mShapePropertiesStack;
  // *** TABLE PROPERTIES ***
  // ToDo: Move this table member variables to a special Table related parser/evaluator (Strategy
  // Pattern?)
  // name of the table or spreadsheet
  private String mTableName;
  private TableTableElement mTableElement;
  private List<TableTableColumnElement> mColumns;
  // The relative widths of the columns of a table
  private List<Integer> mColumnRelWidths;

  private int mColumnCount;
  // Required as the component table/draw can only be delayed created,
  // After the first child has been parsed.. And should ONLY ONCE created!
  private boolean isTableNew = false;
  Map<String, Object> mTableHardFormatting = null;
  // *** LIST Properties ***
  // @text:start-value is provided to the first paragraph only
  private int mListStartValue = -1;
  private final ArrayDeque<ParagraphListProperties> mListStyleStack;
  // used to track in a text:h/text:p if currently whitespace is being deleted/trimmed
  private final ArrayDeque<WhitespaceStatus> mWhitespaceStatusStack;
  /**
   * Quick cache to get the correct linked list. Key is the xml:id of the first list. The sequence
   * of all continued lists and usability functions are provided by ContinuedList
   */
  private final Map<String, ContinuedList> mLinkedLists = new HashMap<String, ContinuedList>();
  // *** FOR BLOCKING OPERATIONS
  // the number of elements above the current element during parsing.
  // Required to find out if the correct blocking element for the UI was found
  int mElementDepth = 0;
  // The depth of the element responsible of blocking further operations
  int mBlockingElementDepth = 0;
  boolean mNoOperationsAllowed = false;
  // All following blocking modes have different behavior
  boolean mIsBlockingFrame = false; // itself and children are allowed
  boolean mIsIgnoredElement = false; // not even itself allwed
  boolean mIsBlockingShape = false; // itself allowed
  // RunTimeConfiguration given by the caller of the ODF Adapter
  private int mMaxAllowedColumnCount;
  private int mMaxAllowedRowCount;
  private int mMaxAllowedCellCount;

  /**
   * LO/AOO/Calligra are applying to Hyperlinks the "Internet_20_link" style, without writing out
   * the dependency into XML. Therefore whenever a Hyperlink existists without character style
   * properties, the reference will be set.
   */
  private static final String HYERLINK_DEFAULT_STYLE = "Internet_20_link";

  private boolean mHasHyperlinkTemplateStyle = false;

  /** Properties for the HEADER_DEFAULT and FOOTER_DEFAULT page area. Defining the page layout */
  private String mMasterPageStyleName = null;

  private String mPageLayoutName = null;
  /** ODF attribute on pageLayout */
  private String mPageStyleUsage = null;
  /** indication of being a first page */
  private boolean mHasNextMasterPage = false;

  private JSONObject headerAttrs = null;
  private JSONObject footerAttrs = null;

  /**
   * In the beginning it is only the styleId of the masterPage plus "HeaderDefault" or
   * "FooterDefault"
   */
  private String mContextName = null;

  public static final String CONTEXT_DELIMITER = "_";
  PageArea mPageArea = null;

  /**
   * "footer_default_" "footer_even_" "footer_first_" "header_default_" "header_even_"
   * "header_first_"
   */
  /** The document might be of different types */
  String mMediaType = null;

  /**
   * Required as the order of linked-list is important! All xml:ids of a connected/linked lists are
   * put into a single list. This collection is used to get the correct reference to the xml:id of
   * the preceding list and have to be updated, when linked lists are created, deleted or moved.
   * Only the text:continue-list of a new list will be evaluated
   */
  class ContinuedList {

    private String mListId;
    private List<String> mSortedIds = null;

    public ContinuedList(String precedingListId, String currentListId) {
      if (precedingListId != null && !precedingListId.isEmpty()) {
        mListId = precedingListId;
      } else {
        if (currentListId != null && !currentListId.isEmpty()) {
          mListId = currentListId;
        }
      }
      mSortedIds = new LinkedList<String>();
    }

    public void add(String listId) {
      mSortedIds.add(listId);
    }

    public List<String> getListIds() {
      return mSortedIds;
    }

    public String getListId() {
      return mListId;
    }
  }

  /**
   * Checks if the preceding list is already part of a continued list, otherwise creates a new
   * continued list and adds both ids to it
   */
  ContinuedList newContinuedList(String precedingListId, String currentListId) {
    ContinuedList continuedList;
    if (!mLinkedLists.containsKey(precedingListId)) {
      continuedList = new ContinuedList(precedingListId, currentListId);
      continuedList.add(precedingListId);
      mLinkedLists.put(precedingListId, continuedList);
    } else {
      continuedList = mLinkedLists.get(precedingListId);
    }
    if (currentListId != null && !currentListId.isEmpty()) {
      continuedList.add(currentListId);
      mLinkedLists.put(currentListId, continuedList);
    }
    return continuedList;
  }

  /**
   * Checks if the preceding list is already part of a continued list, otherwise creates a new
   * continued list and adds both id to it
   */
  ContinuedList newContinuedList(String currentListId) {
    ContinuedList continuedList = null;
    if (currentListId != null && !currentListId.isEmpty()) {
      if (!mLinkedLists.containsKey(currentListId)) {
        continuedList = new ContinuedList(null, currentListId);
        mLinkedLists.put(currentListId, continuedList);
      } else {
        continuedList = mLinkedLists.get(currentListId);
      }
    }
    return continuedList;
  }

  /**
   * The whitespace status of a text container (ie. paragraph or heading). Required for whitespace
   * handling
   */
  class WhitespaceStatus {

    WhitespaceStatus(boolean isParagraphIgnored, int depth) {
      mDepth = depth;
      //            mIsParagraphIgnored = isParagraphIgnored;
    }

    int mDepth = -1;

    public int getParagraphDepth() {
      return mDepth;
    }

    boolean mOnlyWhiteSpaceSoFar = true;
    int mFirstSpaceCharPosition = -1;

    public boolean hasOnlyWhiteSpace() {
      return mOnlyWhiteSpaceSoFar;
    }

    public void setOnlyWhiteSpace(boolean onlyWhiteSpace) {
      mOnlyWhiteSpaceSoFar = onlyWhiteSpace;
    }

    /** During parsing the first character of space siblings. -1 if there is no space sibling */
    public int getFirstSpaceCharPosition() {
      return mFirstSpaceCharPosition;
    }

    /** During parsing the first character of space siblings. -1 if there is no space sibling */
    public void setFirstSpaceCharPosition(int currentSpaceCharPosition) {
      mFirstSpaceCharPosition = currentSpaceCharPosition;
    }

    /** @return true if the previous character was a white space character */
    public boolean hasSpaceBefore() {
      return mFirstSpaceCharPosition > -1;
    }
  }

  OdfSchemaDocument mSchemaDoc = null;

  // Candidate Component Mode
  // Some components consist of multiple XML elements.
  // Even some ODF components start with the same
  // 2DO - DRAGON BOOK - Parser Look-ahead does not work with SAX? ;)
  // private boolean isCandidateComponentMode = true;
  public ChangesFileSaxHandler(Node rootNode) throws SAXException {
    super(rootNode);
    // Initialize starting DOM node
    if (rootNode instanceof OdfFileDom) {
      mFileDom = (OdfFileDom) rootNode;
    } else {
      mFileDom = (OdfFileDom) rootNode.getOwnerDocument();
    }
    mCurrentNode = rootNode;

    // *** COMPONENT HANDLING ***
    // Initialize starting Component
    // Make the root of component tree (to be created) accessible via the ODF schema document
    mSchemaDoc = (OdfSchemaDocument) mFileDom.getDocument();
    if (mSchemaDoc != null) {
      // cash the unfinished DOM otherwise, styles.xml might be tried to be parsed again
      if (mFileDom instanceof OdfContentDom) {
        mSchemaDoc.setContentDom((OdfContentDom) mFileDom);
      } else if (mFileDom instanceof OdfStylesDom) {
        mSchemaDoc.setStylesDom((OdfStylesDom) mFileDom);
      } else if (mFileDom instanceof OdfMetaDom) {
        mSchemaDoc.setMetaDom((OdfMetaDom) mFileDom);
      } else if (mFileDom instanceof OdfSettingsDom) {
        mSchemaDoc.setSettingsDom((OdfSettingsDom) mFileDom);
      }
    }

    // The current component is the root component
    mCurrentComponent = null;

    // Getting Configuration
    Map<String, Object> configuration = mSchemaDoc.getPackage().getRunTimeConfiguration();
    mMaxAllowedColumnCount = OperationConstants.MAX_SUPPORTED_COLUMNS_NUMBER;
    mMaxAllowedRowCount = OperationConstants.MAX_SUPPORTED_ROWS_NUMBER;
    mMaxAllowedCellCount = OperationConstants.MAX_SUPPORTED_CELLS_NUMBER;
    mMediaType = mSchemaDoc.getMediaTypeString();

    if (configuration != null) {
      if (configuration.containsKey(CONFIG_MAX_TABLE_COLUMNS)) {
        mMaxAllowedColumnCount = (Integer) configuration.get(CONFIG_MAX_TABLE_COLUMNS);
      }
      if (configuration.containsKey(CONFIG_MAX_TABLE_ROWS)) {
        mMaxAllowedRowCount = (Integer) configuration.get(CONFIG_MAX_TABLE_ROWS);
      }
      if (configuration.containsKey(CONFIG_MAX_TABLE_CELLS)) {
        mMaxAllowedCellCount = (Integer) configuration.get(CONFIG_MAX_TABLE_CELLS);
      }
    }
    LOG.log(Level.FINEST, "mMaxTableColumnCount{0}", mMaxAllowedColumnCount);
    LOG.log(Level.FINEST, "mMaxTableRowCount{0}", mMaxAllowedRowCount);
    LOG.log(Level.FINEST, "mMaxTableCellCount{0}", mMaxAllowedCellCount);

    // Make the Operation Queue to be created accessible via the Schema Document
    mJsonOperationProducer = mSchemaDoc.getJsonOperationQueue();
    if (mJsonOperationProducer == null) {
      // temporary initated here as all the tests are not using the OperationTextDocument
      mJsonOperationProducer = new JsonOperationProducer();
      mSchemaDoc.setJsonOperationQueue(mJsonOperationProducer);
    }

    mAutoListStyles = new HashMap<String, TextListStyleElement>();
    mUserFieldDecls = new HashMap<String, TextUserFieldDeclElement>();

    // Stack to remember/track the nested delimiters not being componenets (spans) open-up by SAX
    // events
    mTextSelectionStack = new ArrayDeque<TextSelection>();
    mListStyleStack = new ArrayDeque<ParagraphListProperties>();
    //        mShapePropertiesStack = new ArrayDeque<ShapeProperties>();
    mWhitespaceStatusStack = new ArrayDeque<WhitespaceStatus>();
  }

  @Override
  public void startDocument() throws SAXException {}

  @Override
  public void endDocument() throws SAXException {}

  /**
   * There are areas that are not allowed to addChild further components beyond. All further
   * operations have to be blocked, but the creation of the DOM tree must not be disturbed.
   */
  private boolean isBlockedSubTree() {
    return mNoOperationsAllowed;
  }

  /**
   * There are areas that are not allowed to addChild further components beyond. All further
   * operations have to be blocked, but the creation of the DOM tree must not be disturbed.
   */
  private boolean checkEndOfBlockedSubTree(String uri, String localName) {

    boolean isBlocked = mNoOperationsAllowed;
    if (mNoOperationsAllowed) {
      isBlocked = isBlockedSubTree(uri, localName, false);
    }
    mElementDepth--;
    return isBlocked;
  }

  private boolean checkStartOfBlockedSubTree(String uri, String localName) {
    mElementDepth++;
    boolean isBlocked = mNoOperationsAllowed;
    if (!mNoOperationsAllowed) {
      isBlocked = isBlockedSubTree(uri, localName, true);
    } else if (mIsBlockingFrame) {
      if (mBlockingElementDepth == mElementDepth - 1 && !localName.equals("table")) {
        isBlocked = false;
      } else {
        isBlocked = true;
      }
    }
    return isBlocked;
  }

  // ToDo: Differentiate  if there is a shapeBlock, ImageBlock or ParagraphBlock
  private boolean isBlockedSubTree(String uri, String localName, boolean isStart) {
    // within a paragraph within a paragraph
    boolean isBlocked = mNoOperationsAllowed;
    boolean isMasterPage =
        uri != null
            && uri.equals(StyleMasterPageElement.ELEMENT_NAME.getUri())
            && localName.equals(StyleMasterPageElement.ELEMENT_NAME.getLocalName());
    if (isStart) {
      // if it is a second text component (ie. text:p or text:h element)
      if (
      /*!mWhitespaceStatusStack.isEmpty() && Component.isTextComponentRoot(uri, localName) || */ OdfElement
              .isIgnoredElement(uri, localName)
          || ((isMasterPage
                  || Component.isHeaderRoot(uri, localName)
                  || Component.isFooterRoot(uri, localName))
              && OdfDocument.OdfMediaType.TEXT.getMediaTypeString() != mMediaType
              && OdfDocument.OdfMediaType.SPREADSHEET.getMediaTypeString() != mMediaType)) {
        isBlocked = true;
        mNoOperationsAllowed = true;
        mIsIgnoredElement = true;
        mBlockingElementDepth = mElementDepth;
        // if it is a <draw:frame>
      }
    } else { // if this is the closing event of an element
      if (mNoOperationsAllowed) {
        if (mBlockingElementDepth == mElementDepth) {
          if (mIsIgnoredElement
                  && (
                  /*!mWhitespaceStatusStack.isEmpty() && Component.isTextComponentRoot(uri, localName) || */ OdfElement
                      .isIgnoredElement(uri, localName))
              || ((isMasterPage
                      || Component.isHeaderRoot(uri, localName)
                      || Component.isFooterRoot(uri, localName))
                  && OdfDocument.OdfMediaType.TEXT.getMediaTypeString() != mMediaType
                  && OdfDocument.OdfMediaType.SPREADSHEET.getMediaTypeString() != mMediaType)) {
            mIsIgnoredElement = false;
            mBlockingElementDepth = -1;
            mNoOperationsAllowed = false;
            isBlocked = true;
            // if it is a <draw:frame>
          }
        } else if (mIsBlockingFrame
            && mBlockingElementDepth == mElementDepth - 1
            && !localName.equals("table")) {
          isBlocked = false;
        }
      } else { // closing will never enabled a blocking
        if (mIsIgnoredElement || mIsBlockingShape) {
          // close this element, but afterwards
          mNoOperationsAllowed = true;
          isBlocked = false;
        } else if (mIsBlockingFrame) {
          if (mBlockingElementDepth == mElementDepth - 1 && !localName.equals("table")) {
            isBlocked = false;
          } else {
            isBlocked = true;
          }
        }
      }
    }
    return isBlocked;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    flushTextAtStart(uri, localName, qName);

    // if there is a specilized handler on the stack, dispatch the event
    OdfElement element = null;
    // ToDo: Should be able to create operations without creating the DOM Tree
    // ToDo: Are there use-cases that text:s still resides in the DOM? if(isWhiteSpaceElement) ??
    // If Paragraph is not being edited, it will be saved as it is..
    if (domCreationEnabled) {
      if (uri.equals(Constants.EMPTY_STRING) || qName.equals(Constants.EMPTY_STRING)) {
        element = mFileDom.createElement(localName);
      } else {
        // == correct: if localName is the same object as qName, there is a default namespace set
        if (localName == qName) {
          element =
              mFileDom.createElementNS(
                  OdfName.getOdfName(OdfNamespace.newNamespace(null, uri), localName));
        } else {
          element = mFileDom.createElementNS(uri, qName);
        }
      }
      addAttributes(element, attributes);
    }
    // if it is the last page bound object then move all the nodes to a temporary location
    if (mComponentDepth < 0
        && m_cachedPageShapes.size() > 0
        && (localName.equals("p") || localName.equals("h") || localName.equals("table"))) {
      // move nodes
      Node bodyNode = mCurrentNode.getParentNode();
      Iterator<ShapeProperties> it = m_cachedPageShapes.iterator();
      while (it.hasNext()) {
        ShapeProperties component = it.next();
        bodyNode.insertBefore(component.mOwnNode, bodyNode.getFirstChild());
      }
      mLastComponentPositions.clear();
    }
    // Font declarations are before the component
    if (element instanceof StyleFontFaceElement) {
      String fontName = element.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "name");
      if (fontName != null && !fontName.isEmpty()) {
        Set<String> fontNames = ((OdfDocument) mSchemaDoc).getFontNames();
        if (!fontNames.contains(fontName)) {
          mJsonOperationProducer.addFontData(
              fontName,
              null,
              element.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "font-family"),
              element.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "font-family-generic"),
              element.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "font-pitch"),
              element.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "panose-1"));
          fontNames.add(fontName);
        }
      }
    }
    if (element instanceof TextListStyleElement) {
      // We need the reference for later gettin the list styles
      TextListStyleElement listStyle = (TextListStyleElement) element;
      String styleName = listStyle.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "name");
      if (styleName != null && !styleName.isEmpty()) {
        mAutoListStyles.put(styleName, listStyle);
      }
    } else if (element instanceof TextUserFieldDeclElement) {
      TextUserFieldDeclElement fieldDecl = (TextUserFieldDeclElement) element;
      mUserFieldDecls.put(fieldDecl.getAttribute("text:name"), fieldDecl);
    }
    if (!checkStartOfBlockedSubTree(uri, localName)) {
      if (Component.isComponentRoot(
          uri, localName)) { // || Component.isCoveredComponentRoot(uri, localName)) {

        // It is not allowed to addChild further components..,
        // within a paragraph within a paragraph
        // ToDo ? -- HashMap with KEY - URL+localname, VALUE - ComponentName
        if (element instanceof TextPElement || element instanceof TextHElement) {
          // Paragraphs that are not child of a known component should be ignored, otherwise the
          // client gets into trouble with nested paragraphs
          boolean isNestedParagraph = false;

          if (!isNestedParagraph) {
            mComponentDepth++;
            TextParagraphElementBase p = (TextParagraphElementBase) element;
            Map<String, Object> hardFormatting = mJsonOperationProducer.getHardStyles(p);
            if (hardFormatting == null) {
              hardFormatting = new HashMap<String, Object>();
            }
            if (element instanceof TextHElement || !mListStyleStack.isEmpty()) {
              if (!hardFormatting.containsKey("paragraph")) {
                // if there are absolute styles, but not the main property set, where the
                // templateStyleId should be placed in
                hardFormatting.put("paragraph", new JSONObject());
              }
              JSONObject paraProps = (JSONObject) hardFormatting.get("paragraph");

              try {
                if (!mListStyleStack.isEmpty()) {
                  paraProps.put("listLevel", mListStyleStack.size() - 1);
                  // Only the first paragraph within a list item should show a label!
                  ParagraphListProperties listProps = mListStyleStack.getLast();
                  if (listProps.hasListLabel()) {
                    listProps.showListLabel(Boolean.FALSE);
                  } else {
                    paraProps.put("listLabelHidden", Boolean.TRUE);
                  }
                  String listId = listProps.getListId();
                  if (listId != null && !listId.isEmpty()) {
                    paraProps.put("listId", listId);
                  }
                  boolean foundListXmlId = false;
                  boolean foundListItemXmlId = false;
                  Iterator<ParagraphListProperties> listPropsIter =
                      mListStyleStack.descendingIterator();
                  while ((!foundListXmlId || !foundListItemXmlId) && listPropsIter.hasNext()) {
                    ParagraphListProperties currentListProp = listPropsIter.next();

                    String listXmlId = currentListProp.getListXmlId();
                    if (!foundListXmlId && listXmlId != null && !listXmlId.isEmpty()) {
                      foundListXmlId = true;
                      paraProps.put("listXmlId", listXmlId);
                    }
                    String listItemXmlId = currentListProp.getListItemXmlId();
                    if (!foundListItemXmlId && listItemXmlId != null && !listItemXmlId.isEmpty()) {
                      foundListItemXmlId = true;
                      paraProps.put("listItemXmlId", listItemXmlId);
                    }
                  }

                  if (listProps.isListStart()) {
                    paraProps.put("listStart", Boolean.TRUE);
                  }
                  String listStyleId = JsonOperationProducer.getListStyle(mListStyleStack, p);
                  if (listStyleId != null && !listStyleId.isEmpty()) {
                    mJsonOperationProducer.addListStyle(mSchemaDoc, mAutoListStyles, listStyleId);
                    paraProps.put("listStyleId", listStyleId);
                  } else {
                    paraProps.put("listStyleId", Constants.ODFTK_DEFAULT_LIST);
                  }
                  if (mListStartValue != -1) {
                    paraProps.put("listStartValue", mListStartValue);
                    mListStartValue = -1;
                  }
                }
                // Add heading outline numbering
                if (element instanceof TextHElement) {
                  Integer outlineLevel = ((TextHElement) p).getTextOutlineLevelAttribute();
                  if (outlineLevel != null) {
                    paraProps.put("outlineLevel", outlineLevel);
                  }
                }
              } catch (JSONException ex) {
                Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
              }
            }

            List<Integer> position = updateComponentPosition();
            OdfStyle templateStyle = p.getDocumentStyle();
            String styleId = null;
            if (templateStyle != null) {
              styleId = templateStyle.getStyleNameAttribute();
              if (styleId != null && !styleId.isEmpty()) {
                hardFormatting.put(OPK_STYLE_ID, styleId);
              }
            }
            mCurrentComponent = mCurrentComponent.createChildComponent(p);
            boolean paragraphOpCreated = false;
            if (!mPageBoundObjectsRelocated && !m_cachedPageShapes.isEmpty()) {
              // first document paragraph might be inside of a table
              boolean isFirstDocumentParagraph =
                  mComponentStack.empty() || mComponentStack.peek() instanceof CachedTable;
              if (isFirstDocumentParagraph && m_cachedPageShapes.size() > 0) {
                cacheOperation(
                    false,
                    OperationConstants.PARAGRAPH,
                    position,
                    false,
                    hardFormatting,
                    mContextName);
                paragraphOpCreated = true;
                Iterator<ShapeProperties> it = m_cachedPageShapes.iterator();
                while (it.hasNext()) {
                  ShapeProperties component = it.next();
                  Component frameComponent = component.getDrawFrameElement().getComponent();
                  Component frameComponentParent = frameComponent.getParent();
                  int framePosition = frameComponentParent.indexOf(frameComponent);
                  frameComponentParent.remove(framePosition);
                  element.appendChild(component.mOwnNode);
                  component.mShapePosition.addAll(0, position);
                  component.createShapeOperation(
                      this,
                      mComponentStack,
                      component.mDescription,
                      component.hasImageSibling()
                          ? ShapeType.ImageShape
                          : component.isGroupShape() ? ShapeType.GroupShape : ShapeType.NormalShape,
                      component.mContext);
                  Iterator<CachedOperation> opIter = component.iterator();
                  while (opIter.hasNext()) {
                    CachedOperation op = opIter.next();
                    List<Integer> start = op.mStart;
                    if (!op.mAbsolutePosition) {
                      if (op.mComponentType.equals(OperationConstants.ATTRIBUTES)) {
                        @SuppressWarnings("unchecked")
                        List<Integer> end = (List<Integer>) op.mComponentProperties[0];
                        // TODO: add _real_ position of the current paragraph (could be in a
                        // table...)
                        end.addAll(0, position);
                      }
                      // TODO: add _real_ position of the current paragraph (could be in a table...)
                      start.addAll(0, position);
                    }
                    cacheOperation(
                        false,
                        op.mComponentType,
                        start,
                        false,
                        op.mHardFormattingProperties,
                        op.mComponentProperties);
                  }
                }
                m_cachedPageShapes.clear();
              }
              mPageBoundObjectsRelocated |= isFirstDocumentParagraph;
            }
            if (!paragraphOpCreated) {
              cacheOperation(
                  false,
                  OperationConstants.PARAGRAPH,
                  position,
                  false,
                  hardFormatting,
                  mContextName);
            }

            // For each new paragraph/heading addChild a new context information for their
            // whitespace, required for normalization
            mWhitespaceStatusStack.add(new WhitespaceStatus(false, mComponentDepth));
            element.markAsComponentRoot(true);
            // ToDo: NEW COMPONENTS - SECTION
            //			} else if (element instanceof TextSectionElement) {
            //                mJsonOperationProducer.addChild("Section", position);
            //                mCurrentComponent = mCurrentComponent.addChild((TextSectionElement)
            // element);
          } else {
            // a nested text component without known component in-between
            // ignore nested paragraph content
            mWhitespaceStatusStack.add(new WhitespaceStatus(true, mComponentDepth));
            element.ignoredComponent(true);
          }
        } else if (element instanceof DrawFrameElement
            || Component.isShapeElement(uri, localName)) {
          OdfElement shape = element;
          Map<String, Object> hardFormatting = null;
          if (element instanceof OdfStyleableShapeElement) {
            hardFormatting = mJsonOperationProducer.getHardStyles((OdfStyleableShapeElement) shape);
          }
          if (hardFormatting == null || !hardFormatting.containsKey("drawing")) {
            // if there are absolute styles, but not the main property set, where the
            // templateStyleId should be placed in
            if (hardFormatting == null) {
              hardFormatting = new HashMap<String, Object>();
            }
            hardFormatting.put("drawing", new JSONObject());
          }
          JSONObject drawingProps = (JSONObject) hardFormatting.get("drawing");
          if (hardFormatting == null || !hardFormatting.containsKey("image")) {
            // if there are absolute styles, but not the main property set, where the
            // templateStyleId should be placed in
            if (hardFormatting == null) {
              hardFormatting = new HashMap<String, Object>();
            }
            hardFormatting.put("image", new JSONObject());
          }
          int anchorHorOffset = 0;
          int anchorVertOffset = 0;
          int anchorLayerOrder = 0;
          int width = 0;
          int height = 0;

          if (shape instanceof DrawShapeElementBase) {
            Integer zIndex = ((DrawShapeElementBase) shape).getDrawZIndexAttribute();
            if (null != zIndex) {
              anchorLayerOrder = zIndex;
            }
          }

          if (element instanceof DrawLineElement
              || element instanceof DrawConnectorElement
              || element instanceof DrawMeasureElement) {
            if (shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y1")
                && shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x1")
                && shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y2")
                && shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x2")) {
              int x1 =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x1"));
              int x2 =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x2"));
              int y1 =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y1"));
              int y2 =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y2"));
              anchorHorOffset = Math.min(x1, x2);
              width = Math.abs(x2 - x1) + 1;
              anchorVertOffset = Math.min(y1, y2);
              height = Math.abs(y2 - y1) + 1;
            }

          } else {

            if (shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "width")) {
              width =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "width"));
            }
            if (shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "height")) {
              height =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "height"));
            }
            if (shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x")) {
              anchorHorOffset =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "x"));
            }
            if (shape.hasAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y")) {
              anchorVertOffset =
                  MapHelper.normalizeLength(
                      shape.getAttributeNS(OdfDocumentNamespace.SVG.getUri(), "y"));
            }
          }
          try {
            if (height != 0) {
              drawingProps.put("height", height);
            }
            if (width != 0) {
              drawingProps.put("width", width);
            }
            if (anchorHorOffset != 0) {
              drawingProps.put("anchorHorOffset", anchorHorOffset);
              drawingProps.put("left", anchorHorOffset);
            }
            if (anchorVertOffset != 0) {
              drawingProps.put("anchorVertOffset", anchorVertOffset);
              drawingProps.put("top", anchorVertOffset);
            }
            if (anchorLayerOrder != 0) {
              drawingProps.put("anchorLayerOrder", anchorLayerOrder);
            }
          } catch (JSONException ex) {
            Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
          }
          if (shape.hasAttributeNS(OdfDocumentNamespace.DRAW.getUri(), "transform")) {
            try {
              String transform =
                  shape.getAttributeNS(OdfDocumentNamespace.DRAW.getUri(), "transform");
              int index = transform.indexOf("translate");
              if (index >= 0) {
                index = transform.indexOf('(', index);
                transform = transform.substring(index, transform.length());
                int separator = transform.indexOf(' ');
                String leftValue = transform.substring(1, separator);
                index = transform.indexOf(')', separator);
                String rightValue = transform.substring(separator + 1, index);
                anchorHorOffset += MapHelper.normalizeLength(leftValue);
                anchorVertOffset += MapHelper.normalizeLength(rightValue);
              }
              if (anchorVertOffset != 0) {
                drawingProps.put("anchorVertOffset", anchorVertOffset);
              }
              if (anchorHorOffset != 0) {
                drawingProps.put("anchorHorOffset", anchorHorOffset);
              }
            } catch (IndexOutOfBoundsException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          // <attribute name="text:anchor-type">
          //	<choice>
          //		<value>page</value>
          //		<value>frame</value>
          //		<value>paragraph</value>
          //		<value>char</value>
          //		<value>as-char</value>
          //	</choice>
          // </attribute>
          /*	API:
          anchorHorBase:  Horizontal anchor mode:		One of 'margin', 'page', 'column', 'character', 'leftMargin', 'rightMargin', 'insideMargin', or 'outsideMargin'.
          /*
          @text:anchor-type: h=anchorHorBase & v=anchorVerBase
          page		=> h=page	v=page
          frame		=> h=column v=margin
          paragraph	=> h=column v=paragraph
          char		=> h=character v=paragraph
          as-char		=> inline & h & v weglassen*/
          if (shape.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "anchor-type")) {
            try {
              String anchorVertBase = null;
              String anchorHorBase = null;
              String anchorType =
                  shape.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "anchor-type");
              if (anchorType.equals("page")) {
                // Changes API: true: image as character, false: floating mode
                drawingProps.put("inline", Boolean.FALSE);
                // page anchor requires page relation
                drawingProps.put("anchorHorBase", "page");
                drawingProps.put("anchorVertBase", "page");
              } else if (anchorType.equals("frame")) {
                // Changes API: true: image as character, false: floating mode
                drawingProps.put("inline", Boolean.FALSE);
                anchorVertBase = "column";
                anchorVertBase = "margin";
              } else if (anchorType.equals("paragraph")) {
                // Changes API: true: image as character, false: floating mode
                drawingProps.put("inline", Boolean.FALSE);
                anchorHorBase = "column";
                anchorVertBase = "paragraph";
              } else if (anchorType.equals("char")) {
                // Changes API: true: image as character, true: floating mode
                drawingProps.put("inline", Boolean.FALSE);
                anchorHorBase = "character";
                anchorVertBase = "paragraph";
              } else if (anchorType.equals("as-char")) {
                // Changes API: true: image as character, false: floating mode
                drawingProps.put("inline", Boolean.TRUE);
              }
              if (anchorVertBase != null && !drawingProps.has("anchorVertBase")) {
                drawingProps.put("anchorVertBase", anchorVertBase);
              }
              if (anchorHorBase != null && !drawingProps.has("anchorHorBase")) {
                drawingProps.put("anchorHorBase", anchorHorBase);
              }
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          hardFormatting.put("drawing", drawingProps);

          mComponentDepth++;
          List<Integer> pos = updateComponentPosition();
          // the delay of the operation was not the solution, as the children would be added fist
          // instead a setAttribute would be more appropriate
          // even if there is a automatic style, only the template style is required
          if (element instanceof OdfStyleableShapeElement) {
            String styleId = ((OdfStyleableShapeElement) shape).getDocumentStyleName();
            if (styleId != null && !styleId.isEmpty()) {
              hardFormatting.put(OPK_STYLE_ID, styleId);
            }
          }
          ShapeProperties shapeProps = new ShapeProperties(pos, hardFormatting);
          // special handling for frames as together with the image child they are a single user
          // component
          if (element instanceof DrawFrameElement) {
            shapeProps.setDrawFrameElement((DrawFrameElement) shape);
            if (!mComponentStack.isEmpty()) {
              final CachedComponent comp = mComponentStack.peek();
              if (comp instanceof ShapeProperties
                  && ((ShapeProperties) comp).getDrawFrameElement() != null) {
                LOG.warning("Feature 'Frame attached to Frame' yet unsupported");
              }
            }
          } else if (element instanceof DrawGElement) {
            shapeProps.setGroupShape();
            element.markAsComponentRoot(true);
          }
          if (mCurrentComponent != null) {
            mComponentStack.push(shapeProps);
            mCurrentComponent = mCurrentComponent.createChildComponent(element);
          }
          //                    mShapePropertiesStack.push(shapeProps);

          // table component (table within a text document or a spreadsheet)
        } else if (element instanceof TableTableElement) {
          mComponentDepth++;
          // The table will be created with column width, after columns are parsed (just before
          // first row!)
          updateComponentPosition();
          // tables are not written out directly, but its operation collected and only flushed
          // if they are not exceeding a maximum size

          isTableNew = true;
          mTableElement = (TableTableElement) element;
          mCurrentComponent = mCurrentComponent.createChildComponent(mTableElement);
          // initialize a new list for the relative column widths
          // ToDo: Receive the styles from the root component
          // ToDo: If I do not want a DOM, do I have to parse the styles and addChild them to
          // component?
          //		Do I have to parse the styles.xml first to get the props as maps (hashmaps)?
          if (mTableElement.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "style-name")) {
            mTableHardFormatting = mJsonOperationProducer.getHardStyles(mTableElement);
            String styleId = mTableElement.getDocumentStyleName();
            if (styleId != null && !styleId.isEmpty()) {
              if (mTableHardFormatting == null) {
                mTableHardFormatting = new HashMap<>();
              }
              mTableHardFormatting.put(OPK_STYLE_ID, styleId);
              //	All ODF styles are hard formatted
              //	JSONObject tableProps = mTableHardFormatting.get("table");
              //	mTableHardFormatting.put("templateStyleId",
              // table.getDocumentStyle().getStyleNameAttribute());
              //	OdfStyle tableStyle = table.getDocumentStyle();
              //	if(tableStyle != null){
              //	mTableDisplayName = tableStyle.getAttributeNS(OdfDocumentNamespace.STYLE.getUri(),
              // "display-name");
            }
          } else {
            mTableHardFormatting = new HashMap<>();
          }
          mTableName = mTableElement.getAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "name");
          mColumnRelWidths = new LinkedList<>();
          element.markAsComponentRoot(true);
        } else if (element instanceof TableTableRowElement) {
          mComponentDepth++;
          if (isTableNew) {
            // In case neiter relative nor absolute table column width were given, all column are
            // equal sized (given rel size of '1')
            mColumnRelWidths = Table.collectColumnWidths(mTableElement, mColumns);
            mColumns.clear();
            if (mColumnRelWidths != null && mColumnRelWidths.isEmpty()) {
              for (int i = 0; i < mColumnCount; i++) {
                mColumnRelWidths.add(ONE);
              }
            }

            // The grid is known after columns had been parsed, updating later to row positino
            List<Integer> tablePosition = new LinkedList<Integer>(mLastComponentPositions);
            cacheTableOperation(
                OperationConstants.TABLE,
                tablePosition,
                mTableHardFormatting,
                mColumnRelWidths,
                mTableName);
            mTableHardFormatting = null;
            isTableNew = false;
            mTableName = null;
            mColumnCount = 0;
            mColumnRelWidths = null;
          }
          List<Integer> position = updateComponentPosition();
          TableTableRowElement row = (TableTableRowElement) element;
          mCurrentComponent = mCurrentComponent.createChildComponent(row);
          // repeatition can cause a different positioning
          int repeatedRows = 1;
          if (row.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-repeated")) {
            repeatedRows =
                Integer.parseInt(
                    row.getAttributeNS(
                        OdfDocumentNamespace.TABLE.getUri(), "number-rows-repeated"));
            mCurrentComponent.hasRepeated(true);
          }
          boolean isVisible = Boolean.TRUE;
          if (row.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility")) {
            isVisible =
                Constants.VISIBLE.equals(
                    row.getAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "visibility"));
          }
          Map<String, Object> hardFormatting = mJsonOperationProducer.getHardStyles(row);
          OdfStyle templateStyle = row.getDocumentStyle();
          String styleId = null;
          if (templateStyle != null) {
            styleId = templateStyle.getStyleNameAttribute();
            if (styleId != null && !styleId.isEmpty()) {
              hardFormatting.put(OPK_STYLE_ID, styleId);
            }
          }
          if (!isVisible) {
            JSONObject rowProps;
            if (hardFormatting == null) {
              // if there are absolute styles, but not the main property set, where the
              // templateStyleId should be placed in
              if (hardFormatting == null) {
                hardFormatting = new HashMap<String, Object>();
              }
            }
            if (!hardFormatting.containsKey("row")) {
              rowProps = new JSONObject();
              hardFormatting.put("row", rowProps);
            } else {
              rowProps = (JSONObject) hardFormatting.get("row");
              if (rowProps == null) {
                rowProps = new JSONObject();
              }
            }
            try {
              rowProps.put("visible", Boolean.FALSE);
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          cacheTableOperation(OperationConstants.ROWS, position, hardFormatting, repeatedRows);
          element.markAsComponentRoot(true);
        } else if (element instanceof TableTableCellElement
            || element instanceof TableCoveredTableCellElement) {
          boolean covered = element instanceof TableCoveredTableCellElement;
          mComponentDepth++;
          TableTableCellElement cell = covered ? null : (TableTableCellElement) element;
          if (cell != null) {
            mCurrentComponent = mCurrentComponent.createChildComponent(cell);
          } else {
            mCurrentComponent = mCurrentComponent.createChildComponent(element);
          }

          CachedTable cachedTableOps = (CachedTable) mComponentStack.peek();
          cachedTableOps.setCellRepetition(1);
          int repetition = 1;
          Map<String, Object> hardFormatting = null;
          if (!covered) {
            hardFormatting = mJsonOperationProducer.getHardStyles(cell);
          }
          // repeatition and covering can cause a different positioning
          // ToDo: To make DOM optional, work on the component instead of the element. Check
          // directly SAX attributes parameter!
          if (element.hasAttributeNS(
              OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
            // cellProps.put("repeatedColumns",
            // cell.getAttributeNS(OdfDocumentNamespace.TABLE.getUri(),
            // "number-columns-repeatedColumns"));
            cachedTableOps.setCellRepetition(
                Integer.parseInt(
                    element.getAttributeNS(
                        OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")));
            repetition =
                Integer.parseInt(
                    element.getAttributeNS(
                        OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated"));
            mCurrentComponent.hasRepeated(true);
          }
          if (cell != null && cell.hasAttributes()) {
            try {
              // if there are absolute styles, but not the main property set, where the
              // templateStyleId should be placed in
              if (hardFormatting == null || !hardFormatting.containsKey("cell")) {
                if (hardFormatting == null) {
                  hardFormatting = new HashMap<String, Object>();
                }
              }
              JSONObject cellProps = (JSONObject) hardFormatting.get("cell");
              if (cellProps == null) {
                cellProps = new JSONObject();
              }
              if (cell.hasAttributeNS(
                  OdfDocumentNamespace.TABLE.getUri(), "number-columns-spanned")) {
                cellProps.put(
                    COLUMN_SPAN,
                    Integer.parseInt(
                        cell.getAttributeNS(
                            OdfDocumentNamespace.TABLE.getUri(), "number-columns-spanned")));
              }
              if (cell.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-rows-spanned")) {
                cellProps.put(
                    ROW_SPAN,
                    Integer.parseInt(
                        cell.getAttributeNS(
                            OdfDocumentNamespace.TABLE.getUri(), "number-rows-spanned")));
              }
              if (cellProps.length() != 0) {
                hardFormatting.put("cell", cellProps);
              }
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          List<Integer> position = updateComponentPosition();
          OdfStyle templateStyle = covered ? null : cell.getDocumentStyle();
          if (templateStyle != null) {
            String styleId = templateStyle.getStyleNameAttribute();
            if (styleId != null && !styleId.isEmpty()) {
              hardFormatting.put(OPK_STYLE_ID, styleId);
            }
          }
          cacheTableOperation(
              OperationConstants.CELLS, position, hardFormatting, mCurrentComponent, repetition);
          element.markAsComponentRoot(true);
        } else if (element instanceof TextLineBreakElement) {
          mComponentDepth++;
          TextLineBreakElement lineBreak = (TextLineBreakElement) element;
          List<Integer> position = updateComponentPosition();
          mCurrentComponent = mCurrentComponent.createChildComponent(lineBreak);
          cacheOperation(false, OperationConstants.LINE_BREAK, position, false, null, null, null);
          element.markAsComponentRoot(true);
        } else if (element instanceof TextTabElement) {
          mComponentDepth++;
          TextTabElement tab = (TextTabElement) element;
          List<Integer> position = updateComponentPosition();
          mCurrentComponent = mCurrentComponent.createChildComponent(tab);
          cacheOperation(false, OperationConstants.TAB, position, false, null, null, null);
          element.markAsComponentRoot(true);
        } else if (Component.isField(uri, localName)) {
          mComponentDepth++;
          List<Integer> position = updateComponentPosition();
          mCurrentComponent = mCurrentComponent.createChildComponent(element);
          TextFieldSelection selection = null;
          if (element.hasAttributeNS(LIBRE_OFFICE_MS_INTEROP_NAMESPACE, "type")
              && element
                  .getAttributeNS(LIBRE_OFFICE_MS_INTEROP_NAMESPACE, "type")
                  .equals(LIBRE_OFFICE_MS_INTEROP_TYPE_CHECKBOX)) {
            selection =
                new TextFieldSelection(element, position, LIBRE_OFFICE_MS_INTEROP_CHECKBOX_UNICODE);
          } else {
            if (mFileDom instanceof OdfContentDom) {
              selection =
                  new TextFieldSelection(
                      element,
                      position,
                      ((OdfContentDom) mFileDom).getAutomaticStyles(),
                      mUserFieldDecls);
            } else {
              selection =
                  new TextFieldSelection(
                      element,
                      position,
                      ((OdfStylesDom) mFileDom).getAutomaticStyles(),
                      mUserFieldDecls);
            }

            // kann auch (OdfStylesDom) sein!
            //                        element.getParentNode();
            //                       TextTimeElement telem = (TextTimeElement)element;
            //                       Map<String, Object> hardFormatting =
            // mJsonOperationProducer.getHardStyles(telem);
          }
          mTextSelectionStack.add(selection);
        } else if (element instanceof OfficeAnnotationElement) {
          ++mComponentDepth;
          if (mIsCharsBeginning) {
            updateTextPosition();
          }
          mCurrentComponent = mCurrentComponent.createChildComponent(element);
          String annotationName = ((OfficeAnnotationElement) element).getOfficeNameAttribute();
          if (annotationName == null) {
            // annotations without range don't have a name attribute
            annotationName = ((OdfDocument) mSchemaDoc).getUniqueAnnotationName();
          }
          CommentComponent commentProps =
              new CommentComponent(mLastComponentPositions, annotationName);
          ((OdfDocument) mSchemaDoc)
              .addAnnotation(annotationName, ((OfficeAnnotationElement) element));

          mComponentStack.push(commentProps);
          element.markAsComponentRoot(true);
        } else if (element instanceof OfficeAnnotationEndElement) {
          mComponentDepth++;
          List<Integer> position = updateComponentPosition();
          String id = COMMENT_PREFIX;
          id += ((OfficeAnnotationEndElement) element).getOfficeNameAttribute();
          cacheOperation(
              false, OperationConstants.COMMENTRANGE, position, false, null, id, mContextName);
          mCurrentComponent = mCurrentComponent.createChildComponent(element);
          element.markAsComponentRoot(true);
        } else {
          mComponentDepth++;
          element.markAsComponentRoot(true);
        }

      } else if (element instanceof TextSpanElement) {
        // Span <text:span> will be triggering an operation after the text content is parsed
        TextSpanSelection selection =
            new TextSpanSelection((TextSpanElement) element, getTextPosition());
        mTextSelectionStack.add(selection);
      } else if (element instanceof TextAElement) {
        TextHyperlinkSelection selection =
            new TextHyperlinkSelection((TextAElement) element, getTextPosition());
        mTextSelectionStack.add(selection);
      } else if (element
          instanceof
          TextSElement) { // IMPROVABLE: Currently no component, as it will be removed anyway and
        // would burden removal from automatic path counting
        mComponentDepth++;
        List<Integer> position = updateComponentPosition();
        if (mIsCharsBeginning) {
          mCharsStartPosition = position;
          mIsCharsBeginning = false;
        }
        // No operation triggering as client knows only space characters. We keep the
        // parsing/mapping to the more performant server
        TextSElement spaces = (TextSElement) element;
        mCurrentComponent = mCurrentComponent.createChildComponent(spaces);
        Integer quantity = spaces.getTextCAttribute();
        if (quantity == null) {
          addText(/*mCachedTableOps, */ "\u0020");
          // mCharsForOperation.append('\u0020');
        } else {
          for (int i = 0; i < quantity; i++) {
            mCharsForOperation.append('\u0020');
          }
          addText(/*mCachedTableOps, */ mCharsForOperation);
        }

      } else if (element instanceof TableTableColumnElement) {
        // Columns can be grouped by <table:table-columns> and <table:table-column-group>, these
        // would addChild metadata to the following columns
        // Column command should be triggered when one of the grouping starts or closes or if the
        // first row arrives
        TableTableColumnElement column = (TableTableColumnElement) element;
        // Adjust Column Count
        mColumnCount++;
        int repeatedColumns = 1;
        if (column.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated")) {
          repeatedColumns =
              Integer.parseInt(
                  column.getAttributeNS(
                      OdfDocumentNamespace.TABLE.getUri(), "number-columns-repeated"));
          if (repeatedColumns > 1) {
            mColumnCount += (repeatedColumns - 1);
          }
        }
        if (mColumns == null) {
          mColumns = new ArrayList<TableTableColumnElement>();
        }
        mColumns.add(column);
      } else if (element instanceof TextListElement) {
        TextListElement list = (TextListElement) element;
        // in case it is a new list
        if (mListStyleStack.isEmpty()) {
          // Add always a style, so it can be popped of the stack EVERY time a list element ends
          ParagraphListProperties paragraphListProps = new ParagraphListProperties();
          paragraphListProps.setListStart(true);

          // There are two continuation mechanisms for lists in ODF.
          // ODF 1.0/1.1 uses @text:continue-numbering using true/false
          // ODF 1.2 added @text:continue-list using an IDRef to an xml:id of another list.
          String continuedListId = list.getTextContinueListAttribute();
          String listXmlId = list.getXmlIdAttribute();
          if (continuedListId != null && !continuedListId.isEmpty()) {
            paragraphListProps.setListId(newContinuedList(continuedListId, listXmlId).getListId());
          } else if (listXmlId != null && !listXmlId.isEmpty()) {
            paragraphListProps.setListId(newContinuedList(listXmlId).getListId());
          }
          if (listXmlId != null && !listXmlId.isEmpty()) {
            paragraphListProps.setListXmlId(listXmlId);
          } else {
            paragraphListProps.setListXmlId(null);
          }
          mListStyleStack.add(paragraphListProps);
        } else {
          // Add always a style, so it can be popped of the stack EVERY time a list element ends
          mListStyleStack.add(new ParagraphListProperties());
        }

        // @text:continue-numbering LATER
        // @text:continue-list LATER
        // @xml-id - LATER
        // @text:style-name is the given list style unless overwritten by a decendent list
        //	Check if the list style was used already in the document,
        //	if not, map the list properties. Check first in auto than in template.
        //	(Due to MSO issue the style might be even in auto in styles.xml - shall I move them back
        // to content?)
        if (list.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name")) {
          String listStyle = list.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-name");
          mListStyleStack.getLast().setListStyleName(listStyle);
        }
      } else if (element instanceof TextListItemElement
          || element instanceof TextListHeaderElement) {
        ParagraphListProperties paragraphListStyle = mListStyleStack.getLast();
        OdfElement listItem = element;
        if (listItem instanceof TextListHeaderElement) {
          // list header never show a label
          paragraphListStyle.showListLabel(false);
        } else {
          // As a new list item starts, the next paragraph needs to provide the list label
          paragraphListStyle.showListLabel(true);
        }
        //	@text:start-value is provided to the first paragraph only
        if (listItem.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "start-value")) {
          mListStartValue =
              Integer.parseInt(
                  listItem.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "start-value"));
        }
        //	@text:style-override overrides within this list item the list style
        if (listItem.hasAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-override")) {
          String styleOverride =
              listItem.getAttributeNS(OdfDocumentNamespace.TEXT.getUri(), "style-override");
          if (styleOverride != null && !styleOverride.isEmpty()) {
            paragraphListStyle.overrideListStyle(styleOverride);
          } else {
            paragraphListStyle.overrideListStyle(null);
          }
        } else {
          paragraphListStyle.overrideListStyle(null);
        }
        //  @xml-id
        String listXmlId = null;
        if (listItem instanceof TextListItemElement) {
          listXmlId = ((TextListItemElement) listItem).getXmlIdAttribute();
        } else if (listItem instanceof TextListHeaderElement) {
          listXmlId = ((TextListHeaderElement) listItem).getXmlIdAttribute();
        }
        if (listXmlId != null && !listXmlId.isEmpty()) {
          mListStyleStack.getLast().setListItemXmlId(listXmlId);
        } else {
          mListStyleStack.getLast().setListItemXmlId(null);
        }
        //		<style:master-page style:name="Standard" style:page-layout-name="Mpm1">
      } else if (element instanceof StyleMasterPageElement) {
        StyleMasterPageElement masterPage = (StyleMasterPageElement) element;
        mMasterPageStyleName = masterPage.getStyleNameAttribute();
        mPageLayoutName = masterPage.getStylePageLayoutNameAttribute();
        footerAttrs = headerAttrs = null;
        if (mPageLayoutName != null) {
          OdfStylesDom stylesDom;
          try {
            stylesDom = mSchemaDoc.getStylesDom();
            OdfOfficeAutomaticStyles autoStyles = stylesDom.getAutomaticStyles();
            if (autoStyles != null) {

              OdfStylePageLayout pageLayout = autoStyles.getPageLayout(mPageLayoutName);
              if (pageLayout != null) {
                mPageStyleUsage = pageLayout.getStylePageUsageAttribute();
                headerAttrs =
                    getHeaderFooterAttrs(
                        (OdfElement)
                            pageLayout.getChildElement(
                                StyleHeaderStyleElement.ELEMENT_NAME.getUri(), "header-style"));
                footerAttrs =
                    getHeaderFooterAttrs(
                        (OdfElement)
                            pageLayout.getChildElement(
                                StyleFooterStyleElement.ELEMENT_NAME.getUri(), "footer-style"));
              }
            }
          } catch (IOException ex) {
            Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        String nextMasterPageStyle = masterPage.getStyleNextStyleNameAttribute();
        if (nextMasterPageStyle != null && !nextMasterPageStyle.isEmpty()) {
          mHasNextMasterPage = true;
        } else {
          mHasNextMasterPage = false;
        }

      } else if (Component.isHeaderRoot(uri, localName)) {
        PageArea pageArea = null;
        if (localName.equals("header")) {
          pageArea = HEADER_DEFAULT;
        } else if (localName.equals("header-left")) {
          pageArea = HEADER_EVEN;
        } else {
          pageArea = HEADER_FIRST;
        }
        mContextName = pageArea.getPageAreaName() + CONTEXT_DELIMITER + mMasterPageStyleName;
        // insert the Header style
        // {"name":"addHeaderFooter","id":"Standard_header_default","type":"header_default"}
        mJsonOperationProducer.addHeaderFooter(mContextName, pageArea, headerAttrs);
      } else if (Component.isFooterRoot(uri, localName)) {
        // insert the Footer style
        PageArea pageArea = null;
        if (localName.equals("footer")) {
          pageArea = FOOTER_DEFAULT;
        } else if (localName.equals("footer-left")) {
          pageArea = FOOTER_EVEN;
        } else {
          pageArea = FOOTER_FIRST;
        }
        mContextName = pageArea.getPageAreaName() + CONTEXT_DELIMITER + mMasterPageStyleName;
        mJsonOperationProducer.addHeaderFooter(mContextName, pageArea, footerAttrs);
      } else if (element instanceof DrawImageElement) {
        DrawImageElement image = (DrawImageElement) element;
        ShapeProperties frameProps = (ShapeProperties) mComponentStack.peek();
        //                ShapeProperties frameProps = mShapePropertiesStack.peekFirst();
        int childNo = frameProps.incrementChildNumber();
        if (childNo == 1) {

          Map<String, Object> hardFormatting = new HashMap<String, Object>();
          hardFormatting.putAll(frameProps.getShapeHardFormatting());
          JSONObject drawingProps = (JSONObject) hardFormatting.get("drawing");
          JSONObject imageProps = (JSONObject) hardFormatting.get("image");
          if (image.hasAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href")) {
            try {
              String href = image.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
              imageProps.put("imageUrl", href);
              // if there is cropping from the frame, we need to do further calculation based on
              // real graphic size
              if (imageProps.has("cropRight")
                  && (imageProps.has("height") || imageProps.has("width"))) {
                JsonOperationProducer.calculateCrops(image, href, imageProps);
              }
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          if (image.hasAttributeNS(OdfDocumentNamespace.XML.getUri(), "id")) {
            try {
              drawingProps.put(
                  "imageXmlId", image.getAttributeNS(OdfDocumentNamespace.XML.getUri(), "id"));
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
          // ToDo: Need test document with child element having office:binary-data with base64
          // content

          DrawFrameElement frameElement = frameProps.getDrawFrameElement();
          frameElement.markAsComponentRoot(true);
          mComponentStack.pop();
          //                    mShapePropertiesStack.pollFirst();
          mComponentStack.push(frameProps);
          //                    mShapePropertiesStack.addFirst(frameProps);
          frameProps.declareImage();
          hardFormatting.put("drawing", drawingProps);
          //					}else {
          //						drawingProps.put("viewAlternative", childNo - 1);
        }

        //				if (!frameProps.hasImageSibling()) {
        //					mComponentDepth++;
        //					frameProps.setFramePosition(updateComponentPosition());
        //					mCurrentComponent = mCurrentComponent.createChildComponent(frameElement);
        //					//			position.set(position.size() - 1, position.get(position.size() - 1) +1);
        //				}
        //				if (childNo == 1) { // DISABLING REPLACEMENT IMAGE FEATURE AS LONG CLIENT DOES NOT
        // SUPPORT IT
        //					 ToDo: Dependencies for frame replacement feature has to be updated in
        // OdfElement.raiseComponentSize() for every Frame child/feature enabled
        //					frameProps.saveShapeProps(frameProps.getShapePosition(), hardFormatting);
        //				}
      } else if (element instanceof DrawTextBoxElement) {
        //            	element.getAttributeNodeNS(namespaceURI, localName);
        //                Map<String, Object> hardFormatting = null;
        //                if (element instanceof OdfStyleableShapeElement) {
        //                    hardFormatting =
        // mJsonOperationProducer.getHardStyles((OdfStyleableShapeElement) element);
        //                }
        //                JSONObject drawingProps = (JSONObject) hardFormatting.get("drawing");
        //            	JSONObject drawingProps = new JSONObject();
        if (!mComponentStack.empty()) {
          ShapeProperties parentShapeProps = (ShapeProperties) mComponentStack.peek();
          JSONObject originalDrawingProps =
              (JSONObject) parentShapeProps.mShapeHardFormatations.get("drawing");
          if (originalDrawingProps != null && !originalDrawingProps.has("height")) {
            try {
              if (!parentShapeProps.mShapeHardFormatations.containsKey("shape")) {
                parentShapeProps.mShapeHardFormatations.put("shape", new JSONObject());
              }
              JSONObject originalShapeProps =
                  (JSONObject) parentShapeProps.mShapeHardFormatations.get("shape");
              originalShapeProps.put("autoResizeHeight", "true");
            } catch (JSONException ex) {
              Logger.getLogger(ChangesFileSaxHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      }

      // but within a shape being a frame, the child elements are still of interest (currently only
      // <draw:image> supported)
      //		} else if (!mShapePropertiesStack.isEmpty() &&
      // mShapePropertiesStack.peekLast().mDrawFrameElement != null) {
      if (Component.isDocumentRoot(uri, localName)
          || Component.isHeaderRoot(uri, localName)
          || Component.isFooterRoot(uri, localName)) {
        // temporary initated here as all the tests are not using the OperationTextDocument
        mCurrentComponent = new Component(element);
        mSchemaDoc.setRootComponent(mCurrentComponent);
        // for every header and footer restart counting
        if (Component.isHeaderRoot(uri, localName) || Component.isFooterRoot(uri, localName)) {
          mLastComponentPositions.clear();
        } else {
          mPageArea = PageArea.BODY;
        }
      }
    } else {
      if (element instanceof OdfElement) {
        element.ignoredComponent(true);
      }
    }

    // add the new element as child & make it the current context node
    mCurrentNode = mCurrentNode.appendChild(element);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    flushTextAtEnd(uri, localName, qName);
    // at the end of a table check if it can be flushed
    if (uri != null
        && localName != null
        && localName.equals(TableTableElement.ELEMENT_NAME.getLocalName())
        && uri.equals(OdfDocumentNamespace.TABLE.getUri())) {
      endTableSizeEvaluation();
    }
    // office:styles only exist in styles.xml
    if (qName.equals("office:styles")) {
      if (mFileDom instanceof OdfStylesDom) {
        Integer defaultTabStopWidth = null;
        JSONObject defaultPageStyles = null;
        OdfStylesDom stylesDom = (OdfStylesDom) mFileDom;
        // reset the position context used for header/footer
        mContextName = null;
        OdfOfficeStyles officeStyles = (OdfOfficeStyles) mCurrentNode;
        if (officeStyles != null) {
          // check if the default hyperlinkstyle do exist
          mHasHyperlinkTemplateStyle =
              officeStyles.getStyle(HYERLINK_DEFAULT_STYLE, OdfStyleFamily.Text) != null;
          final Iterator<OdfStyle> paragraphStyleIter =
              officeStyles.getStylesForFamily(OdfStyleFamily.Paragraph).iterator();
          // The sort is for testing purpose to receive across different JDK an equal result
          Integer _defaultTabStopWidth = null;
          while (paragraphStyleIter.hasNext()) {
            // defaulTableWidth is part of the paragraph default style (optional)
            _defaultTabStopWidth =
                mJsonOperationProducer.triggerStyleHierarchyOps(
                    officeStyles, OdfStyleFamily.Paragraph, paragraphStyleIter.next());
            if (_defaultTabStopWidth != null) {
              defaultTabStopWidth = _defaultTabStopWidth;
            }
          }
          final Iterator<OdfStyle> textStyleIter =
              officeStyles.getStylesForFamily(OdfStyleFamily.Text).iterator();
          while (textStyleIter.hasNext()) {
            mJsonOperationProducer.triggerStyleHierarchyOps(
                officeStyles, OdfStyleFamily.Text, textStyleIter.next());
          }
          final Iterator<OdfStyle> graphicStyleIter =
              officeStyles.getStylesForFamily(OdfStyleFamily.Graphic).iterator();
          while (graphicStyleIter.hasNext()) {
            mJsonOperationProducer.triggerStyleHierarchyOps(
                officeStyles, OdfStyleFamily.Graphic, graphicStyleIter.next());
          }
          // always generate graphic default style
          mJsonOperationProducer.triggerDefaultStyleOp(
              OdfStyleFamily.Graphic, officeStyles.getDefaultStyle(OdfStyleFamily.Graphic));

          //    			for(OdfStyle style : officeStyles.getStylesForFamily(OdfStyleFamily.Table)){
          //    				mJsonOperationProducer.triggerStyleHierarchyOps(officeStyles,
          // OdfStyleFamily.Table, style);
          //    			}
          //    			for(OdfStyle style : officeStyles.getStylesForFamily(OdfStyleFamily.TableRow)){
          //    				mJsonOperationProducer.triggerStyleHierarchyOps(officeStyles,
          // OdfStyleFamily.TableRow, style);
          //    			}
          //    			for(OdfStyle style :
          // officeStyles.getStylesForFamily(OdfStyleFamily.TableColumn)){
          //    				mJsonOperationProducer.triggerStyleHierarchyOps(officeStyles,
          // OdfStyleFamily.TableColumn, style);
          //    			}
          for (OdfStyle style : officeStyles.getStylesForFamily(OdfStyleFamily.TableCell)) {
            mJsonOperationProducer.triggerStyleHierarchyOps(
                officeStyles, OdfStyleFamily.TableCell, style);
            //    				mJsonOperationProducer.triggerStyleHierarchyOps(officeStyles,
            // OdfStyleFamily.TableCell, (OdfStyleBase)
            // officeStyles.getDefaultStyle(OdfStyleFamily.TableCell));
          }
          //    			for(OdfStyle style : officeStyles.getStylesForFamily(OdfStyleFamily.Section)){
          //    				mJsonOperationProducer.triggerStyleHierarchyOps(officeStyles,
          // OdfStyleFamily.Section, style);
          //    			}
          //    			for(OdfStyle style : officeStyles.getStylesForFamily(OdfStyleFamily.List)){
          //    				mJsonOperationProducer.triggerStyleHierarchyOps(officeStyles,
          // OdfStyleFamily.List, style);
          //    			}
          final Iterator<OdfTextListStyle> textListStyleIter =
              officeStyles.getListStyles().iterator();
          while (textListStyleIter.hasNext()) {
            mJsonOperationProducer.addListStyle(textListStyleIter.next());
          }

          // maps page properties, but returns the default page properties
          defaultPageStyles = mJsonOperationProducer.addPageProperties(stylesDom);
          // dispatches default document attributes
          mJsonOperationProducer.addDocumentProperties(
              stylesDom, defaultTabStopWidth, defaultPageStyles);
        } else {
          mJsonOperationProducer.addDocumentProperties(stylesDom, null, null);
        }
      }
    }
    // if we remove the current element, the current node shall not be changed in the end
    boolean selectionNormalization = false;
    // SPECIAL HANDLING FOR DESCRIPTION OF SHAPES: draw:frame as the shape is one of the children,
    // e.g a draw:image child
    if (!checkEndOfBlockedSubTree(
        uri, localName) /*&& !(mContextName != null && localName.equals("annotation-end"))*/) {
      boolean isImageComponent = false;
      if ((uri != null
              && uri.equals(DrawFrameElement.ELEMENT_NAME.getUri())
              && localName.equals(DrawFrameElement.ELEMENT_NAME.getLocalName())
          || Component.isShapeElement(uri, localName))) {
        if (!mComponentStack.empty()) {
          ShapeProperties shapeProps = (ShapeProperties) mComponentStack.pop();
          mComponentDepth--;
          // Check for description of shape/frame about to be closed
          //                ShapeProperties shapeProps = mShapePropertiesStack.removeLast();
          isImageComponent = shapeProps.hasImageSibling();
          NodeList descList =
              mCurrentComponent.mRootElement.getElementsByTagNameNS(
                  OdfDocumentNamespace.SVG.getUri(), SvgDescElement.ELEMENT_NAME.getLocalName());
          String description = null;
          if (descList.getLength() > 0) {
            SvgDescElement desc = (SvgDescElement) descList.item(0);
            Node descText = desc.getFirstChild();
            if (descText != null && descText instanceof Text) {
              description = ((Text) descText).getTextContent();
            }
          }
          // it is root shape if the parent office:text
          shapeProps.createShapeOperation(
              this,
              mComponentStack,
              description,
              isImageComponent
                  ? ShapeType.ImageShape
                  : shapeProps.isGroupShape() ? ShapeType.GroupShape : ShapeType.NormalShape,
              mContextName);
          if (shapeProps.isGroupShape()) {
            mCurrentNode.setUserData(
                "groupWidth",
                new Integer(
                    shapeProps.mHoriOffsetMax
                        - (shapeProps.mHoriOffsetMin == null ? 0 : shapeProps.mHoriOffsetMin)),
                null);
            mCurrentNode.setUserData(
                "groupHeight",
                new Integer(
                    shapeProps.mVertOffsetMax
                        - (shapeProps.mVertOffsetMin == null ? 0 : shapeProps.mVertOffsetMin)),
                null);
          }
          // flush the inner operations of the shape
          Iterator<CachedOperation> opIter = shapeProps.iterator();
          while (opIter.hasNext()) {
            CachedOperation op = opIter.next();
            cacheOperation(
                true,
                op.mComponentType,
                op.mStart,
                false,
                op.mHardFormattingProperties,
                op.mComponentProperties);
          }
          mCurrentComponent = mCurrentComponent.getParent();
        }

        //			} else if (Component.isCoveredComponentRoot(uri, localName)) { // adjust counting for
        // table cells without numbering
        //				//ToDO: Instead to count the covered someone should count the spanning (BUT this is
        // against OOXML cell numbering!)
        //				mComponentDepth--;
      } else if (isSpaceElement(uri, localName)) {
        mComponentDepth--;
        mCurrentComponent = mCurrentComponent.getParent();
        //		} else if (uri != null && uri.equals(DrawImageElement.ELEMENT_NAME.getUri()) &&
        // localName.equals(DrawImageElement.ELEMENT_NAME.getLocalName())) {
        //			mFramePropertiesStack.getFirst().decrementChildNumber();
      } else if (uri != null && Component.isComponentRoot(uri, localName)) {
        //                if (Component.isTextComponentRoot(uri, localName) &&
        // mWhitespaceStatusStack.size() > 0 &&
        // mWhitespaceStatusStack.getLast().mIsParagraphIgnored) {
        /* no ignored paragraphs anymore
        if (Component.isTextComponentRoot(uri, localName) && mWhitespaceStatusStack.size() > 0 && mWhitespaceStatusStack.getLast().mIsParagraphIgnored) {) {
               // do nothing for a ignored paragraph
               mWhitespaceStatusStack.removeLast();
               // SPECIAL HANDLING FOR IMAGE: draw:image (not a component root T- replacement for draw:frame)
           } else */
        if (localName.equals(DrawFrameElement.ELEMENT_NAME.getLocalName())
                && uri.equals(DrawFrameElement.ELEMENT_NAME.getUri())
                && isImageComponent
            || !(localName.equals(DrawFrameElement.ELEMENT_NAME.getLocalName())
                && uri.equals(DrawFrameElement.ELEMENT_NAME.getUri()))) {
          // if the current component is a text container flush spans
          if (Component.isTextComponentRoot(mCurrentNode)) {
            Collection<TextSelection> selections =
                ((TextParagraphElementBase) mCurrentNode).getTextSelections();
            if (selections != null) {
              for (TextSelection s : selections) {
                OdfStylableElement selectionElement = (OdfStylableElement) s.getSelectionElement();
                Map<String, Object> hardFormatting =
                    mJsonOperationProducer.getHardStyles(selectionElement);
                String styleId = null;
                OdfStyle templateStyle = selectionElement.getDocumentStyle();
                if (templateStyle != null) {
                  styleId = templateStyle.getStyleNameAttribute();
                }
                if (s.hasUrl() || styleId != null) {
                  try {
                    JSONObject charProps;
                    if (hardFormatting == null) {
                      // if there are absolute styles, but not the main property set, where the
                      // templateStyleId should be placed in
                      if (hardFormatting == null) {
                        hardFormatting = new HashMap<String, Object>();
                      }
                    }
                    if (s.hasUrl()) {
                      if (!hardFormatting.containsKey("character")) {
                        charProps = new JSONObject();
                        hardFormatting.put("character", charProps);
                      } else {
                        charProps = (JSONObject) hardFormatting.get("character");
                      }
                      charProps.put("url", s.getURL());
                    }
                    if (styleId != null && !styleId.isEmpty()) {
                      hardFormatting.put(OPK_STYLE_ID, styleId);
                    } else {
                      // add the implicit by LO/AOO used hyperlink style
                      if (mHasHyperlinkTemplateStyle) {
                        hardFormatting.put(OPK_STYLE_ID, HYERLINK_DEFAULT_STYLE);
                      }
                    }
                  } catch (JSONException ex) {
                    Logger.getLogger(ChangesFileSaxHandler.class.getName())
                        .log(Level.SEVERE, null, ex);
                  }
                }
                if (hardFormatting != null) {
                  //                                    if (mWithinTable) {
                  cacheOperation(
                      false,
                      OperationConstants.ATTRIBUTES,
                      s.getStartPosition(),
                      false,
                      hardFormatting,
                      s.getEndPosition(),
                      mContextName);
                }
              }
            }
            //					// in this case check if the closing descendent (this element)
            //					// had any none whitespace text and apply if necessary the change
            //					// remove the current whitespace properties from the stack
            //					int depth = mWhitespaceStatusStack.size();
            //					boolean childHasWhiteSpace = true;
            //					boolean parentHasOnlyWhiteSpace;
            // if there is a parent text container
            if (mWhitespaceStatusStack.size() > 0) {
              mWhitespaceStatusStack.removeLast();
              // BEFORE WE DID NOT ALLOWED TO FOLLOWING PARAGRAPHS
              //						// see if the child only had whitespaces
              //						childHasWhiteSpace = mWhitespaceStatusStack.getLast().hasOnlyWhiteSpace();
              //						// switch to parent
              //						mWhitespaceStatusStack.pop();
              //						// see if the parent had only whitespaces
              //						WhitespaceStatus parentWhiteSpaceStatus = mWhitespaceStatusStack.getLast();
              //						parentHasOnlyWhiteSpace = parentWhiteSpaceStatus.hasOnlyWhiteSpace();
              //						// if the parent had only whitespaces, but not the child
              //						if (parentHasOnlyWhiteSpace && !childHasWhiteSpace) {
              //							// remove the only whitespace modus from the parent
              //							parentWhiteSpaceStatus.setOnlyWhiteSpace(childHasWhiteSpace);
              //						}
              //					} else {
              //						// otherwise just end the state collection of this paragraph/heading
              //						mWhitespaceStatusStack.pop();
            }
          }
          // removing the last in the list of positions, when a component is closed
          if (localName.equals("annotation")) {
            CommentComponent commProps = (CommentComponent) mComponentStack.pop();
            if (!commProps.isInHeaderFooter()) {
              String id = COMMENT_PREFIX;
              id += commProps.getCommentName();
              cacheOperation(
                  false,
                  OperationConstants.COMMENT,
                  commProps.getComponentPosition(),
                  false,
                  null,
                  id,
                  commProps.getAuthor(),
                  commProps.getDate(),
                  mContextName);
              int parentPosSize = commProps.getComponentPosition().size();
              for (CachedOperation op : commProps) {
                // TODO: add id as target, remove comments own position from op.mStart;
                CachedOperation newOp = op.clone();
                for (int r = 0; r < parentPosSize; ++r) {
                  newOp.mStart.remove(0);
                }

                ArrayList<Object> componentProperties = new ArrayList<Object>();
                int propIndex = 0;
                while (newOp.mComponentProperties.length > propIndex
                    && newOp.mComponentProperties[propIndex] != null) {
                  if (propIndex == 0
                      && newOp.mComponentType.equals(OperationConstants.ATTRIBUTES)) {
                    @SuppressWarnings("unchecked")
                    List<Integer> endArray = (List<Integer>) newOp.mComponentProperties[propIndex];
                    for (int r = 0; r < parentPosSize; ++r) {
                      endArray.remove(0);
                    }
                    componentProperties.add(endArray);
                  } else if (mContextName == null
                      || !newOp.mComponentProperties[propIndex].equals(mContextName)) {
                    componentProperties.add(newOp.mComponentProperties[propIndex]);
                  }
                  ++propIndex;
                }
                componentProperties.add(id);
                componentProperties.add(null);
                cacheOperation(
                    false,
                    newOp.mComponentType,
                    newOp.mStart,
                    true,
                    newOp.mHardFormattingProperties,
                    componentProperties.toArray());
              }
            }
            //                    } else if (localName.equals("annotation-end")){ //no action
            // required
          }
          if (mCurrentComponent.hasRepeated()) {
            // if it is a cell or row not in spreadsheets all operations back from addCell/addRows
            // need to be repeated with incremented positions (TODO: needs to be recursive! )
            boolean isCell = localName.equals("table-cell");
            boolean isRow = localName.equals("table-row");
            if (isRow || isCell) {
              CachedTable currentTable = (CachedTable) mComponentStack.peek();
              int opSize = currentTable.size();
              int pos = 0;
              for (pos = opSize - 1; pos >= 0; --pos) {
                CachedOperation op = currentTable.get(pos);
                if (op.mComponentType.equals(
                    isRow ? OperationConstants.ROWS : OperationConstants.CELLS)) {
                  break;
                }
              }
              // now we have a start index - add all ops from pos to opSize- 1 again
              // repetition-times with modified position
              if (pos > 0) {
                CachedOperation cellInsertOp = currentTable.get(pos);
                int incrementPos = cellInsertOp.mStart.size() - 1;
                int repetition = (Integer) cellInsertOp.mComponentProperties[isRow ? 0 : 1];
                for (int rep = 0; rep < repetition - 1; ++rep) {
                  for (int opPos = pos; opPos < opSize; ++opPos) {
                    CachedOperation newOp = currentTable.get(opPos).clone();
                    if (newOp.mStart != null) {
                      int oldIndex = newOp.mStart.get(incrementPos);
                      newOp.mStart.set(incrementPos, oldIndex + rep + 1);
                    }
                    cacheOperation(
                        false,
                        newOp.mComponentType,
                        newOp.mStart,
                        false,
                        newOp.mHardFormattingProperties,
                        newOp.mComponentProperties);
                  }
                }
              }
            }
            mLastComponentPositions.set(
                mComponentDepth,
                mLastComponentPositions.get(mComponentDepth) + mCurrentComponent.repetition() - 1);
          }
          mComponentDepth--;
          mCurrentComponent = mCurrentComponent.getParent();
        }
      } // if text delimiter - addChild text
      else if (Component.isTextSelection(mCurrentNode)) {
        // dropping the last (most inner) selection from the stack
        TextSelection textSelection = mTextSelectionStack.pollLast();
        if (textSelection != null) {
          textSelection.setEndPosition(getTextPosition());
          OdfElement root = ((OdfElement) mCurrentNode).getComponentRoot();
          if (Component.isTextComponentRoot(root)) {
            // sometimes when spans share the same text area, they are condensed to one. The
            // remaining one is being returned, or in case of an empty element the parent
            mCurrentNode = ((TextContainingElement) root).appendTextSelection(textSelection);
            // selectionNormalization might delete an element in this case the change of the current
            // node would be an error!
            selectionNormalization = true;
          }
        }
      } else if (uri.equals(OdfDocumentNamespace.TEXT.getUri()) && localName.equals("list-item")) {
        mListStyleStack.getLast().overrideListStyle(null);
      } else if (uri.equals(OdfDocumentNamespace.TEXT.getUri()) && localName.equals("list")) {
        // POP UP NEW LIST STYLE
        mListStyleStack.removeLast();
      } else if (localName.equals("creator")) {
        CachedComponent commProps = mComponentStack.isEmpty() ? null : mComponentStack.peek();
        if (commProps != null && commProps instanceof CommentComponent) {
          ((CommentComponent) commProps).setAuthor(mCurrentNode.getTextContent());
        }
      } else if (localName.equals("date")) {
        CachedComponent commProps = mComponentStack.isEmpty() ? null : mComponentStack.peek();
        if (commProps != null && commProps instanceof CommentComponent) {
          ((CommentComponent) commProps).setDate(mCurrentNode.getTextContent());
        }
      }
    }
    // selectionNormalization might delete an element in this case the change of the current node
    // would be an error!
    if (mCurrentNode != null && !selectionNormalization) {
      // pop to the parent node
      mCurrentNode = mCurrentNode.getParentNode();
    }
  }

  private void addAttributes(Element element, Attributes attributes) {
    String attrQname;
    String attrURL;
    OdfAttribute attr;
    for (int i = 0; i < attributes.getLength(); i++) {
      attrURL = attributes.getURI(i);
      attrQname = attributes.getQName(i);
      // if no namespace exists
      if (attrURL.equals(Constants.EMPTY_STRING) || attrQname.equals(Constants.EMPTY_STRING)) {
        // create attribute without prefix
        attr = mFileDom.createAttribute(attributes.getLocalName(i));
      } else {
        if (attrQname.startsWith("xmlns:")) {
          // in case of xmlns prefix we have to create a new OdfNamespace
          OdfNamespace namespace =
              mFileDom.setNamespace(attributes.getLocalName(i), attributes.getValue(i));
          // if the file Dom is already associated to parsed XML addChild the new namespace to the
          // root element
          Element root = mFileDom.getRootElement();
          if (root == null) {
            root = element;
          }
          root.setAttributeNS(
              "http://www.w3.org/2000/xmlns/",
              "xmlns:" + namespace.getPrefix(),
              namespace.getUri());
        }
        // create all attributes, even namespace attributes
        attr = mFileDom.createAttributeNS(attrURL, attrQname);
      }

      // namespace attributes will not be created and return null
      if (attr != null) {
        element.setAttributeNodeNS(attr);
        try {
          // set Value in the attribute to allow validation in the attribute
          attr.setValue(attributes.getValue(i));
        } // if we detect an attribute with invalid value: remove attribute node
        catch (IllegalArgumentException e) {
          ErrorHandler errorHandler = mFileDom.getDocument().getPackage().getErrorHandler();
          if (errorHandler != null) {
            try {
              errorHandler.error(
                  new OdfValidationException(
                      OdfSchemaConstraint.DOCUMENT_XML_INVALID_ATTRIBUTE_VALUE,
                      attr.getValue(),
                      attr.getPrefix() + ":" + attr.getLocalName()));
            } catch (SAXException ex) {
              Logger.getLogger(StyleStyleElement.class.getName()).log(Level.SEVERE, null, ex);
            }
          } else {
            LOG.severe(
                "ERROR / EXCEPTION DURING XML PARSING: INVALID ATTRIBUTE: '"
                    + attr.getPrefix()
                    + ":"
                    + attr.getLocalName()
                    + "' with value '"
                    + attr.getValue()
                    + "'!");
          }
          element.removeAttributeNode(attr);
        }
      }
    }
  }

  private void flushTextAtStart(String uri, String localName, String qName) {
    flushText(uri, localName, qName, false);
  }

  private void flushTextAtEnd(String uri, String localName, String qName) {
    flushText(uri, localName, qName, true);
  }

  /**
   * Consumers shall collapse white space characters that occur in
   *
   * <ul>
   *   <li>a <text:p> or <text:h> element (so called paragraph elements), and
   *   <li>in their descendant elements, if the OpenDocument schema permits the inclusion of
   *       character data for the element itself and all its ancestor elements up to the paragraph
   *       element.
   * </ul>
   *
   * Collapsing white space characters is defined by the following algorithm: 1)The following
   * [UNICODE] characters are replaced by a " " (U+0020, SPACE) character: \ue570HORIZONTAL
   * TABULATION (U+0009) \ue570CARRIAGE RETURN (U+000D) \ue570LINE FEED (U+000A) 2)The character
   * data of the paragraph element and of all descendant elements for which the OpenDocument schema
   * permits the inclusion of character data for the element itself and all its ancestor elements up
   * to the paragraph element, is concatenated in document order. 3)Leading " " (U+0020, SPACE)
   * characters at the start of the resulting text and trailing SPACE characters at the end of the
   * resulting text are removed. 4)Sequences of " " (U+0020, SPACE) characters are replaced by a
   * single " " (U+0020, SPACE) character.
   */
  private void flushText(String uri, String localName, String qName, boolean isEndOfElement) {
    // check if there is was text found to be added to the element
    if (mCharsForElement.length() > 0) {
      // every text will be kept from the XML file (e.g. indent)
      String newString = mCharsForElement.toString();
      mCharsForElement.setLength(0);
      Text text = mFileDom.createTextNode(newString);
      if (isEndOfElement && Component.isField(uri, localName)) {
        TextSelection textSelection = mTextSelectionStack.pollLast();
        if (!isBlockedSubTree()) {
          // Currently only check-box have an UTF-8 square as replacementText
          TextFieldSelection textFieldSelection = (TextFieldSelection) textSelection;
          String replacementText = textFieldSelection.getReplacementText();
          Map<String, Object> attrMap = textFieldSelection.getAttributes();

          cacheOperation(
              false,
              OperationConstants.FIELD,
              textSelection.getStartPosition(),
              false,
              null,
              localName,
              replacementText != null ? replacementText : newString,
              attrMap,
              mContextName);
        }
        mCurrentNode.appendChild(text);
      } else {
        // if the text is within a text aware component
        if (mCurrentNode instanceof OdfElement) {
          // ToDo: Uncertain what with text should happen that is not within a text component?
          // Neglectable by
          //                    if ((Component.isTextComponentRoot(mCurrentNode) ||
          // Component.isTextComponentRoot(((OdfElement) mCurrentNode).getComponentRoot())) &&
          // !isBlockedSubTree() && mWhitespaceStatusStack.size() > 0 &&
          // !mWhitespaceStatusStack.getLast().isParagraphIgnored() && !(mCurrentNode instanceof
          // TextNoteCitationElement)) {
          if ((Component.isTextComponentRoot(mCurrentNode)
                  || Component.isTextComponentRoot(((OdfElement) mCurrentNode).getComponentRoot()))
              && !isBlockedSubTree()
              && mWhitespaceStatusStack.size() > 0
              && !(mCurrentNode instanceof TextNoteCitationElement)) {
            mComponentDepth++;
            if (mIsCharsBeginning) {
              mCharsStartPosition = updateTextPosition();
              mIsCharsBeginning = false;
            }
            mComponentDepth--;
            // The new charPosition adds the text lenght, but inserted will be without
            // 1) insertion
            addText(/*mCachedTableOps, */ newString);
            // the following would cumulate the text of a paragraph to a single large string
            // mCharsForOperation.append(newString);

            // muss ich rekursiv die gr\u00f6sse nach oben reichen? f\u00fcr jeden none component
            // descendant? K\u00f6nnte ich in OdfElement implemenentieren!
            // \u00fcberschreibe alle addChild/delete Funktionalit\u00e4t!
            // Merge/split/delete Text Funktionalit\u00e4t f\u00fcr alle ELEMENTE? Komponenten
            // m\u00fcssen mit einbezogen werden! Reuse of Recursion -- ACTION KLASSE.operate()
            // aufruf!?!?
            //					OdfElement element = (OdfElement) mCurrentNode;
            //					element.appendChild(text);
          }
          if (isSpaceElement(mCurrentNode)) {
            mCurrentNode.getParentNode().appendChild(text);
          } else {
            mCurrentNode.appendChild(text);
          }
        }
      }
    } else {
      if (isEndOfElement && Component.isField(uri, localName)) {
        TextSelection textSelection = mTextSelectionStack.pollLast();
        if (!isBlockedSubTree()) {
          // Currently only check-box have an UTF-8 square as replacementText
          TextFieldSelection textFieldSelection = (TextFieldSelection) textSelection;
          String replacementText = textFieldSelection.getReplacementText();
          Map<String, Object> attrMap = textFieldSelection.getAttributes();
          if (replacementText == null) {
            replacementText = new String();
          }
          cacheOperation(
              false,
              OperationConstants.FIELD,
              textSelection.getStartPosition(),
              false,
              null,
              localName,
              replacementText,
              attrMap,
              mContextName);
        }
      }
    }
    if (mCharsForOperation.length() > 0
        && Component.isComponentRoot(uri, localName)
        && !isSpaceElement(uri, localName)) {
      addText(/*mCachedTableOps, */ mCharsForOperation);
    }
  }

  private void addText(CharSequence newText) {
    cacheOperation(
        false,
        OperationConstants.TEXT,
        mCharsStartPosition,
        false,
        null,
        newText.toString(),
        mContextName);
    mCharsForOperation.setLength(0);
    mIsCharsBeginning = true;
  }

  static boolean isSpaceElement(Node node) {
    return node instanceof TextSElement;
  }

  static boolean isSpaceElement(String uri, String localName) {
    return uri != null
        && uri.equals(TextSElement.ELEMENT_NAME.getUri())
        && localName.equals(TextSElement.ELEMENT_NAME.getLocalName());
  }

  @Override
  /**
   * http://xerces.apache.org/xerces2-j/faq-sax.html#faq-2 : SAX may deliver contiguous text as
   * multiple calls to characters, for reasons having to do with parser efficiency and input
   * buffering. It is the programmer's responsibility to deal with that appropriately, e.g. by
   * accumulating text until the next non-characters event. This method will finalize the text of an
   * element, by flushing/appending it to the element node. It is called at the beginning of
   * startElement/endElement. In case of startElement the text will be referred to the previous
   * element node (before the new started). In case of endElement the text will be referred to the
   * current element node.
   */
  public void characters(char[] ch, int startPosition, int length) {
    if (mCurrentComponent instanceof TextContainer) {
      // ODF Whitespacehandling
      WhitespaceStatus currentWhiteSpaceStatus = mWhitespaceStatusStack.getLast();
      // Note: The delta between startPosition and endPosition marks the text to be written out
      // startPosition will only be raised to endposition, when characters have to be skipped!
      int endPosition = startPosition;
      int lastPos = startPosition + length;
      boolean previousContentWritten = false;
      char c;
      // Go through all characters found by the parser..
      for (int i = startPosition; i < lastPos; i++) {
        c = ch[i];
        // first part is trimming in the beginning of the element
        if (currentWhiteSpaceStatus.hasOnlyWhiteSpace()) {
          // \t (tabulator = 0x09)
          if (c == '\u0020' // space
              || c == '\t'
              // \r (carriage return = 0x0D)
              || c == '\r'
              // \n (line feed = 0x0A)
              || c == '\n') {
            // skipt this character, keeping the difference between start & end (length) equal
            startPosition++;
            endPosition++;
          } else {
            // first character being found worth to be written
            currentWhiteSpaceStatus.setOnlyWhiteSpace(false);
            endPosition++;
          }
          // second part is about collapsing multiple whitespaces
        } else {
          if (c == '\u0020' // space
              || c == '\t' // \t (tabulator = 0x09)
              // \r (carriage return = 0x0D)
              || c == '\r'
              // \n (line feed = 0x0A)
              || c == '\n') {
            // if we have aleady a preceding whitespace character
            if (currentWhiteSpaceStatus.hasSpaceBefore()) {
              if (!previousContentWritten) {
                // as we have to skip a character in the array, write what we have
                if (endPosition - startPosition > 0) {
                  mCharsForElement.append(ch, startPosition, endPosition - startPosition);
                }
                previousContentWritten = true;
              }
              // NOT including this character
              endPosition++;
              startPosition = endPosition;
            } else {
              currentWhiteSpaceStatus.setFirstSpaceCharPosition(i);
              ch[i] = '\u0020'; // overwrite all
              endPosition++;
            }
          } else {
            if (currentWhiteSpaceStatus.hasSpaceBefore()) {
              currentWhiteSpaceStatus.setFirstSpaceCharPosition(-1);
            }
            endPosition++; // including this character
          }
        }
      }
      if (endPosition - startPosition > 0) {
        mCharsForElement.append(ch, startPosition, endPosition - startPosition);
      }
    } else {
      /*
      * ToDo: The following will be ignored for now:
      In addition, OpenDocument Consumers shall ignore all element children ([RNG] section 5,
      Data Model) of elements defined in this specification that are strings consisting entirely of whitespace characters and
      which do not satisfy a pattern of the OpenDocument schema definition for the element.
      */
      // See
      // http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#a3_18White_Space_Processing_and_EOL_Handling
      mCharsForElement.append(ch, startPosition, length);
    }
  }

  @Override
  public InputSource resolveEntity(String publicId, String systemId)
      throws IOException, SAXException {
    return super.resolveEntity(publicId, systemId);
  }

  /**
   * This method is being called whenever an element being the root element is found, within
   * startElement function. It considers mComponentDepth and the size of the list
   * mLastComponentPositions. It updates the position (ie. list mLastComponentPositions) by adding a
   * child or a sibling. This ONLY works if mComponentDepth has already been updated before this
   * method is being called. Currently at the beginning of startElement, when realizing it is a
   * component root element.
   *
   * <p>The only reason to update the position (mLastComponentPositions) outside of this function is
   * to handle attributes representing repeatedColumns components * (i.e. for cells & rows).
   *
   * @return the path of the current found component as Integer array
   */
  private List<Integer> updateComponentPosition() {
    List<Integer> pos = updatePosition(true);
    return pos;
  }

  /**
   * This method is being called whenever an element being the root element is found, within
   * startElement function. It considers mComponentDepth and the size of the list
   * mLastComponentPositions. It updates the position (ie. list mLastComponentPositions) by adding a
   * child or a sibling. This ONLY works if mComponentDepth has already been updated before this
   * method is being called. Currently at the beginning of startElement, when realizing it is a
   * component root element.
   *
   * <p>The only reason to update the position (mLastComponentPositions) outside of this function is
   * to handle attributes representing repeatedColumns components * (i.e. for cells & rows).
   *
   * @return the path of the current found component as Integer array
   */
  private List<Integer> updateTextPosition() {
    return updatePosition(false);
  }

  /**
   * This method is being called whenever an element being the root element is found, within
   * startElement function. It considers mComponentDepth and the size of the list
   * mLastComponentPositions. It updates the position (ie. list mLastComponentPositions) by adding a
   * child or a sibling. This ONLY works if mComponentDepth has already been updated before this
   * method is being called. Currently at the beginning of startElement, when realizing it is a
   * component root element.
   *
   * <p>The only reason to update the position (mLastComponentPositions) outside of this function is
   * to handle attributes representing repeatedColumns components * (i.e. for cells & rows).
   *
   * @return the path of the current found component as Integer array
   */
  private List<Integer> updatePosition(boolean isComponent) {
    /**
     * Used components being siblings of text, where the position is determined by the text count,
     * not by the previous component number
     */
    // take care of position handling
    // NEW COMPONENT CHILD: If the component Level is deeper than the last position
    // Actually at the first component: componentDepth start with 0, but the size with 1
    if (mComponentDepth == mLastComponentPositions.size()) {
      // addChild a new level
      mLastComponentPositions.add(mComponentDepth, 0);

      // NEW SIBLING: If the depth is "equal" (ie. lower) than addChild a sibling
    } else if (mComponentDepth == mLastComponentPositions.size() - 1) {
      int positionUpdate;
      if (mCurrentComponent instanceof TextContainer) {
        // increment the last position number as a sibling was added
        positionUpdate = mCurrentComponent.size();
      } else if (mCurrentComponent instanceof Cell) {
        positionUpdate = mLastComponentPositions.get(mComponentDepth) + 1;
        //            } else if (mCurrentComponent instanceof Table) {
        //                positionUpdate =mCurrentComponent.size();
      } else if (mCurrentComponent.getRootElement() instanceof OfficeAnnotationEndElement) {
        positionUpdate = mCurrentComponent.getParent().size();
      } else {
        positionUpdate = mLastComponentPositions.get(mComponentDepth) + 1;
      }
      mLastComponentPositions.set(mComponentDepth, positionUpdate);
      // FINISHED COMPONENT - REMOVE DEPTH - If a component was closed and the position needds to be
      // added
    } else if (mComponentDepth < mLastComponentPositions.size() - 1) {
      // remove the last position and addChild a new one
      mLastComponentPositions.removeLast();
      updatePosition(isComponent);
    } else {
      LOG.warning("Houston, we have a problem..");
    }
    // ToDo: Do I need a new LIST for every component? Or may I addChild a position to the
    // component?
    return new LinkedList<Integer>(mLastComponentPositions);
    // ToDo: Below a bad idea?
    // return Collections.unmodifiableList(mLastComponentPositions);
  }

  /** @return the path of the current found component as Integer array */
  private List<Integer> getTextPosition() {
    // The text is one level further down
    mComponentDepth++;
    List<Integer> position = updateTextPosition();
    mComponentDepth--;
    return position;
  }

  // CachedTable mCachedTableOps = null;
  // based on document position the tables and subtables are being flushed after each end
  //    HashMap<List<Integer>, CachedTable> mAllTables = null;
  //    private ArrayList<CachedTable> mTableStack;
  //    boolean mWithinTable = false; //TODO: Detect via type of top element of mComponentStack
  // private int mNumberOfNestedTables = 0;
  Stack<CachedComponent> mComponentStack = new Stack<CachedComponent>();
  // cache objects bound to page that are on top level of the document
  ArrayDeque<ShapeProperties> m_cachedPageShapes = new ArrayDeque<ShapeProperties>();
  boolean mPageBoundObjectsRelocated = false;
  HashMap<String, Integer> mTopLevelTables =
      new HashMap<String, Integer>(); // mapping of spreadsheet index to their names

  @SuppressWarnings("unchecked")
  public void cacheOperation(
      boolean fillCacheOnly,
      String componentType,
      List<Integer> start,
      boolean absolutePosition,
      Map<String, Object> hardFormattingProperties,
      Object... componentProperties) {
    if (mComponentStack.empty()) {
      // send to producer
      if (componentType.equals(OperationConstants.TEXT)) {
        String text = (String) componentProperties[0];
        String context = (String) componentProperties[1];
        mJsonOperationProducer.addText(start, text, context);
      } else if (componentType.equals(OperationConstants.PARAGRAPH)) {
        String context = (String) componentProperties[0];
        mJsonOperationProducer.add(componentType, start, hardFormattingProperties, context);
      } else if (componentType.equals(OperationConstants.TABLE)) {
        List<Integer> tableGrid = (List<Integer>) componentProperties[0];
        String tableName = (String) componentProperties[1];
        String context = (String) componentProperties[2];
        mJsonOperationProducer.addTable(
            start, hardFormattingProperties, tableGrid, tableName, context);
      } else if (componentType.equals(OperationConstants.EXCEEDEDTABLE)) {
        int columns = (Integer) componentProperties[0];
        int rows = (Integer) componentProperties[1];
        List<Integer> tableGrid = (List<Integer>) componentProperties[2];
        String context = (String) componentProperties[3];
        // addExceededTable(final List<Integer> start, int columns, int rows, final List<Integer>
        // tableGrid) {
        mJsonOperationProducer.addExceededTable(start, columns, rows, tableGrid, context);
      } else if (componentType.equals(OperationConstants.ATTRIBUTES)) {
        List<Integer> end = (List<Integer>) componentProperties[0];
        String context = (String) componentProperties[1];
        mJsonOperationProducer.format(start, end, hardFormattingProperties, context);
      } else if (componentType.equals(OperationConstants.FORMATROWS)) {
        Integer firstRow = (Integer) componentProperties[0];
        Integer lastRow = (Integer) componentProperties[1];
        String context = (String) componentProperties[3];
        Integer repeatedRowOffset = (Integer) componentProperties[2];
        mJsonOperationProducer.formatRows(
            start, hardFormattingProperties, firstRow, lastRow, repeatedRowOffset, context);
      } else if (componentType.equals(OperationConstants.FORMATCOLUMNS)) {
        Integer firstColumn = (Integer) componentProperties[0];
        Integer lastColumn = (Integer) componentProperties[1];
        String context = (String) componentProperties[2];
        mJsonOperationProducer.formatColumns(
            start, hardFormattingProperties, firstColumn, lastColumn, context);
      } else if (componentType.equals(OperationConstants.SHAPE)
          || componentType.equals(OperationConstants.SHAPE_GROUP)) {
        String context = (String) componentProperties[0];
        mJsonOperationProducer.addShape(
            start,
            hardFormattingProperties,
            context,
            componentType.equals(OperationConstants.SHAPE_GROUP));
      } else if (componentType.equals(OperationConstants.IMAGE)) {
        String context = (String) componentProperties[0];
        mJsonOperationProducer.addImage(start, hardFormattingProperties, context);
      } else if (componentType.equals(OperationConstants.FIELD)) {
        String fieldType = (String) componentProperties[0];
        String fieldContent = (String) componentProperties[1];
        Map<String, Object> fieldAttributes = (Map<String, Object>) componentProperties[2];
        String context = (String) componentProperties[3];
        mJsonOperationProducer.addField(start, fieldType, fieldContent, fieldAttributes, context);
      } else if (componentType.equals(OperationConstants.COMMENT)) {
        String id = (String) componentProperties[0];
        String author = (String) componentProperties[1];
        String date = (String) componentProperties[2];
        String target = (String) componentProperties[3];
        mJsonOperationProducer.addAnnotation(start, id, author, date, target);
      } else if (componentType.equals(OperationConstants.COMMENTRANGE)) {
        String id = (String) componentProperties[0];
        String target = (String) componentProperties[1];
        mJsonOperationProducer.addRange(start, id, target);
      } else if (componentType.equals(OperationConstants.TAB)) {
        String target = (String) componentProperties[0];
        mJsonOperationProducer.add(
            componentType, start, hardFormattingProperties, target != null ? target : mContextName);
      } else {
        String target = (String) componentProperties[0];
        mJsonOperationProducer.add(
            componentType, start, hardFormattingProperties, target != null ? target : mContextName);
      }
    } else {
      CachedComponent topComponent = mComponentStack.peek();
      if (!fillCacheOnly && topComponent instanceof CachedTable) {
        cacheTableOperation(componentType, start, hardFormattingProperties, componentProperties);
      } else {
        // collect the operations at the CachedComponent
        LinkedList<Integer> position = null;
        if (start != null) {
          position = new LinkedList<>(start);
        }

        topComponent.add(
            new CachedOperation(
                componentType,
                position,
                absolutePosition,
                hardFormattingProperties,
                componentProperties));
      }
    }
  }

  /** Table operation are being cached in one of two caches */
  @SuppressWarnings("unchecked")
  private void /*CachedTable */ cacheTableOperation(
      /*CachedTable currentTable,*/ String componentType,
      List<Integer> start,
      Map<String, Object> hardFormattingProperties,
      Object... componentProperties) {
    LinkedList<Integer> position = null;
    CachedTable currentTable =
        mComponentStack.empty()
            ? null
            : (mComponentStack.peek() instanceof CachedTable)
                ? (CachedTable) mComponentStack.peek()
                : null;
    if (start != null) {
      position = new LinkedList<Integer>(start);
    }
    if (componentType.equals(OperationConstants.CELLS)) { // does not effect a spreadsheet
      currentTable.mCellCount++;
      if (mMaxAllowedCellCount != 0 && currentTable.mCellCount > mMaxAllowedCellCount) {
        currentTable.mIsTooLarge = true;
      }
    } else if (componentType.equals(OperationConstants.ROWS)) { // Counting Rows
      currentTable.mRowCount++; // no repeated in writer
      if (mMaxAllowedRowCount != 0 && currentTable.mRowCount > mMaxAllowedRowCount) {
        currentTable.mIsTooLarge = true;
      }
    } else if (componentType.equals(
        OperationConstants.TABLE)) { // all formats (Text & Spreadsheet atm)
      List<Integer> tableGrid = (List<Integer>) componentProperties[0];
      CachedTable newCachedTable = startTableSizeEvaluation(position, tableGrid);
      if (newCachedTable.mTableGrid != null) {
        if (mMaxAllowedColumnCount != 0
            && newCachedTable.mTableGrid.size() > mMaxAllowedColumnCount) {
          newCachedTable.mIsTooLarge = true;
          // adding the table now, as it would not be below as already too large..
        } else {
          newCachedTable.mColumnCount = newCachedTable.mTableGrid.size();
        }
      }
      // if it is the first root table
      if (currentTable == null) {
        // only for the root table the table itself will be added at the beginning
        currentTable = newCachedTable;
        //                ++currentTable.mNumberOfNestedTables;
        mComponentStack.push(currentTable);
        currentTable.add(
            new CachedInnerTableOperation(
                componentType, position, false, hardFormattingProperties, componentProperties));
      } else { // as subtable the table will be added twice:
        // once for the parent as notifier
        currentTable.addSubTable(newCachedTable, start);
        currentTable.add(
            new CachedInnerTableOperation(
                componentType, position, false, hardFormattingProperties, componentProperties));
        currentTable = newCachedTable;
        // once for the child to create
        currentTable.add(
            new CachedInnerTableOperation(
                componentType, position, false, hardFormattingProperties, componentProperties));
      }
    }
    if (!currentTable.mIsTooLarge
        && !componentType.equals(OperationConstants.TABLE)
        && !componentType.equals(OperationConstants.COLUMNS)) {
      currentTable.add(
          new CachedInnerTableOperation(
              componentType, position, false, hardFormattingProperties, componentProperties));
    }
  }

  /**
   * According to user run-time configuration only tables of a certain size are allowed to be
   * created. Tables exceeding the limit are being shown by a replacement object, otherwise the
   * client performance might not be sufficient.
   */
  private CachedTable startTableSizeEvaluation(List<Integer> position, List<Integer> tableGrid) {
    CachedTable cachedTable = null;

    cachedTable = new CachedTable();

    cachedTable.mTableGrid = tableGrid;
    return cachedTable;
  }

  /**
   * According to user run-time configuration only tables of a certain size are allowed to be
   * created. Tables exceeding the limit are being shown by a replacement object, otherwise the
   * client performance might not be sufficient.
   *
   * <p>As the limit is being checked on sub table level, the complete table have to be parsed
   * before giving green light for any table. On the opposite, if a subtable is already too large,
   * it can be neglected collecting operations for that subtable.
   *
   * @throws SAXException
   */
  private void endTableSizeEvaluation() throws SAXException {

    CachedTable cachedTableOps = (CachedTable) mComponentStack.peek();
    if (cachedTableOps.getSubTableCount() == 0) {
      cachedTableOps.mMostUsedColumnStyle = getMostUsedStyle(cachedTableOps.columnStyleOccurrence);
      cachedTableOps.mMostUsedRowStyle = getMostUsedStyle(cachedTableOps.rowStyleOccurrence);
      mComponentStack.pop();
      flushTableOperations(cachedTableOps, true);
      if (cachedTableOps != null && cachedTableOps.mCachedTableContentOps != null) {
        cachedTableOps.mCachedTableContentOps = null;
        cachedTableOps.lastRowFormatOperation = null;
      }
    } else if (cachedTableOps.getSubTableCount() > 0) {
      // when leaving a table, continue with the parent table
      cachedTableOps.removeSubTable();
    } else { // below zero might appear, when table had started in blocked area
      // TODO: is it really possible to reach this point?
      if (cachedTableOps != null && cachedTableOps.mCachedTableContentOps != null) {
        cachedTableOps.mCachedTableContentOps = null;
        cachedTableOps.lastRowFormatOperation = null;
      }
      mComponentStack.pop();
    }
  }

  @SuppressWarnings("rawtypes")
  private void flushTableOperations(CachedTable currentTable, boolean isStartOfTable)
      throws SAXException {

    boolean putPageBreak = false;
    boolean isBreakBefore = true;
    ListIterator<CachedOperation> cachedOperationIterator = currentTable.listIterator();
    while (cachedOperationIterator.hasNext()) {
      CachedOperation operation = cachedOperationIterator.next();
      if (operation instanceof CachedInnerTableOperation
          && operation.mComponentType.equals(OperationConstants.TABLE)) {
        if (isStartOfTable) {
          isStartOfTable = false;
          if (currentTable.mIsTooLarge) {
            // replacement table
            cacheOperation(
                false,
                OperationConstants.EXCEEDEDTABLE,
                operation.mStart,
                false,
                null,
                ((List) operation.mComponentProperties[0]).size(),
                currentTable.mRowCount,
                operation.mComponentProperties[0],
                mContextName);
            break;
          } else {
            if ((mMaxAllowedRowCount != 0 && currentTable.mRowCount > mMaxAllowedRowCount)
                || (mMaxAllowedColumnCount != 0
                    && currentTable.mColumnCount > mMaxAllowedColumnCount)
                || (mMaxAllowedCellCount != 0 && currentTable.mCellCount > mMaxAllowedCellCount)) {
              // TODO: Exceeded table operation name
              cacheOperation(
                  false,
                  OperationConstants.EXCEEDEDTABLE,
                  operation.mStart,
                  false,
                  null,
                  ((List) operation.mComponentProperties[0]).size(),
                  currentTable.mRowCount,
                  operation.mComponentProperties[0],
                  mContextName);
              break;
            } else {
              // the last parameter are: mColumnRelWidths, mTableName, mIsTableVisible);
              JSONObject tableAttr = null;
              if (operation.mHardFormattingProperties.containsKey("table")
                  && (((tableAttr = (JSONObject) operation.mHardFormattingProperties.get("table"))
                          .has("pageBreakBefore"))
                      || tableAttr.has("pageBreakAfter"))) {
                isBreakBefore = tableAttr.has("pageBreakBefore");
                String breakString = isBreakBefore ? "pageBreakBefore" : "pageBreakAfter";
                boolean breakAttr = tableAttr.getBoolean(breakString);
                if (breakAttr) {
                  putPageBreak = true;
                }
                tableAttr.remove(breakString);
              }
              cacheOperation(
                  false,
                  OperationConstants.TABLE,
                  operation.mStart,
                  false,
                  operation.mHardFormattingProperties,
                  operation.mComponentProperties[0],
                  operation.mComponentProperties[1],
                  mContextName);
            }
          }
        } else {
          flushTableOperations(currentTable.getSubTable(operation.mStart), true);
        }
      } else if (operation.mComponentType.equals(OperationConstants.TEXT)) {
        String context = mContextName;
        if (operation.mComponentProperties.length > 1
            && operation.mComponentProperties[1] != null) {
          context = (String) operation.mComponentProperties[1];
        }
        cacheOperation(
            false,
            operation.mComponentType,
            operation.mStart,
            false,
            null,
            operation.mComponentProperties[0],
            context);
      } else if (operation.mComponentType.equals(OperationConstants.ATTRIBUTES)) {
        String context = mContextName;
        if (operation.mComponentProperties.length > 1
            && operation.mComponentProperties[1] != null) {
          context = (String) operation.mComponentProperties[1];
        }
        cacheOperation(
            false,
            OperationConstants.ATTRIBUTES,
            operation.mStart,
            false,
            operation.mHardFormattingProperties,
            operation.mComponentProperties[0],
            context);
      } else if (operation.mComponentType.equals(OperationConstants.SHAPE)
          || operation.mComponentType.equals(OperationConstants.IMAGE)
          || operation.mComponentType.equals(OperationConstants.SHAPE_GROUP)) {
        cacheOperation(
            false,
            operation.mComponentType,
            operation.mStart,
            false,
            operation.mHardFormattingProperties,
            mContextName);
      } else if (operation.mComponentType.equals(OperationConstants.FIELD)) {
        // TODO: Why do I have to check for map<> casts but not with String casts?
        @SuppressWarnings("unchecked")
        Map<String, Object> attrMap = (Map<String, Object>) operation.mComponentProperties[2];
        cacheOperation(
            false,
            operation.mComponentType,
            operation.mStart,
            false,
            null,
            operation.mComponentProperties[0],
            operation.mComponentProperties[1],
            attrMap,
            mContextName);
      } else if (operation.mComponentType.equals(OperationConstants.TABLE)
          || operation.mComponentType.equals(OperationConstants.COMMENT)
          || operation.mComponentType.equals(OperationConstants.COMMENTRANGE)) {
        cacheOperation(
            false,
            operation.mComponentType,
            operation.mStart,
            false,
            operation.mHardFormattingProperties,
            operation.mComponentProperties);
      } else if (operation.mComponentType.equals(OperationConstants.COMMENT)
          || operation.mComponentType.equals(OperationConstants.COMMENTRANGE)) {
        cacheOperation(
            false,
            operation.mComponentType,
            operation.mStart,
            false,
            operation.mHardFormattingProperties,
            operation.mComponentProperties);
      } else {
        boolean isParagraphOperation =
            operation.mComponentType.equals(OperationConstants.PARAGRAPH);
        if (putPageBreak && isParagraphOperation) {
          JSONObject paraProps = null;
          if (operation.mHardFormattingProperties == null) {
            operation.mHardFormattingProperties = new HashMap<String, Object>();
          }
          if (!operation.mHardFormattingProperties.containsKey("paragraph")) {
            paraProps = new JSONObject();
          } else {
            paraProps = (JSONObject) operation.mHardFormattingProperties.get("paragraph");
          }
          paraProps.put(isBreakBefore ? "pageBreakBefore" : "pageBreakAfter", true);
          operation.mHardFormattingProperties.put("paragraph", paraProps);
          putPageBreak = false;
        }
        String context = mContextName;
        if (isParagraphOperation && operation.mComponentProperties[0] != null) {
          context = (String) operation.mComponentProperties[0];
        }
        cacheOperation(
            false,
            operation.mComponentType,
            operation.mStart,
            false,
            operation.mHardFormattingProperties,
            context);
      }
    }
  }

  private static String getMostUsedStyle(Map<String, Integer> styleOccurrances) {
    String mostUsedStyleName = null;
    if (styleOccurrances != null) {
      Set<Entry<String, Integer>> entrySet = styleOccurrances.entrySet();
      Iterator<Entry<String, Integer>> iter = entrySet.iterator();
      Integer styleOccurance = null;
      Integer styleOccuranceMax = null;
      while (iter.hasNext()) {
        Entry<String, Integer> entry = iter.next();
        styleOccurance = entry.getValue();
        if (styleOccuranceMax == null || styleOccuranceMax < styleOccurance) {
          styleOccuranceMax = styleOccurance;
          mostUsedStyleName = entry.getKey();
        }
      }
      // if there is a most used style
      if (mostUsedStyleName != null) {
        // make sure it is not by coincidence the most single used one..
        if (styleOccurrances.get(mostUsedStyleName) == 1) {
          mostUsedStyleName = null;
        }
      }
    }

    return mostUsedStyleName;
  }

  private JSONObject getHeaderFooterAttrs(OdfElement e) {
    JSONObject attrs = null;
    JSONObject pageAttrs = null;
    if (e != null) {
      final Element p =
          e.getChildElement(
              StyleHeaderFooterPropertiesElement.ELEMENT_NAME.getUri(), "header-footer-properties");
      if (p != null) {
        pageAttrs = new JSONObject(3);
        final String sMinHeight = p.getAttribute("fo:min-height");
        if (!sMinHeight.isEmpty()) {
          pageAttrs.put("minHeight", MapHelper.normalizeLength(sMinHeight));
        }
        final String sHeight = p.getAttribute("svg:height");
        if (!sHeight.isEmpty()) {
          pageAttrs.put("height", MapHelper.normalizeLength(sHeight));
        }
        final String sMarginTop = p.getAttribute("fo:margin-top");
        if (!sMarginTop.isEmpty()) {
          pageAttrs.put("marginTop", MapHelper.normalizeLength(sMarginTop));
        }
        final String sMarginBottom = p.getAttribute("fo:margin-bottom");
        if (!sMarginBottom.isEmpty()) {
          pageAttrs.put("marginBottom", MapHelper.normalizeLength(sMarginBottom));
        }
        final String sMarginLeft = p.getAttribute("fo:margin-left");
        if (!sMarginLeft.isEmpty()) {
          pageAttrs.put("marginLeft", MapHelper.normalizeLength(sMarginLeft));
        }
        final String sMarginRight = p.getAttribute("fo:margin-right");
        if (!sMarginRight.isEmpty()) {
          pageAttrs.put("marginRight", MapHelper.normalizeLength(sMarginRight));
        }
      }
    }
    if (pageAttrs != null && pageAttrs.length() != 0) {
      attrs = new JSONObject(1);
      attrs.put("page", pageAttrs);
    }

    return attrs;
  }

  /**
   * Optimizes the operations of spreadsheet cells neglecting starting/trailing empty cells and for
   * cells with content or style bundling similar cells to single operations.
   *
   * <p>Repeated rows are automatically a range.
   *
   * <p>There are three pointer (variables), that are updated during parsing the spreadsheet:
   * mCurrentCellNo is the actual column number mFirstContentCellNo is mFirstEqualCellNo is set to
   * the first cell to be written, after a cell was written out or an empty precessor
   *
   * <p>ToDo: Refactoring - As soon every component got its own parser, the tableOps. have to be
   * replaced by the Context of the component
   *
   * <p>private CachedTable evaluateSimilarCells(CachedTable tableOps, CachedInnerTableOperation
   * cellOperation, JSONObject currentCell, boolean isRow) { // An Operation will always be
   * triggered in the end of the function boolean triggerOperation = false;
   *
   * <p>// every repeatedColumns row will result into a fillRange operation int
   * previousContentRepetition = 1;
   *
   * <p>// if the previous cells are equal if (tableOps.mFirstEqualCellNo > -1) {
   * previousContentRepetition = tableOps.mCurrentColumnNo - tableOps.mFirstEqualCellNo; }
   *
   * <p>boolean isRepeatedRow = tableOps.mLastRow != null &&
   * !tableOps.mFirstRow.equals(tableOps.mLastRow);
   *
   * <p>// do not trigger the operation if the spreadsheetRow is null and its only member is null if
   * (tableOps.mSheetNo == null && cellOperation != null && cellOperation.mStart != null) { // we
   * have a cell position and require the two above (first parent row, afterwards cell) => already
   * -2 // and an additional - 1 as size of 1 would result in zero position ==> finally -3
   * tableOps.mSheetNo = cellOperation.mStart.get(cellOperation.mStart.size() - 3); } // ** There
   * are four variations for previous/current cell we have to check: // 1) Current Content Cell,
   * Previous Content empty if (currentCell != null && tableOps.mPreviousCell != null) { // if the
   * two cells are NOT the same if (!currentCell.equals(tableOps.mPreviousCell)) { if
   * (previousContentRepetition > MIN_REPEATING_CONTENT_CELLS) { triggerOperation = true; } else {
   * if (tableOps.mCurrentRange == null) { tableOps.mCurrentRange = new JSONArray(); } // Resolving
   * mColumnRepetition, explicitly adding cells to the range for (int i = 0;
   * tableOps.mCurrentColumnNo - tableOps.mFirstEqualCellNo > i; i++) {
   * tableOps.mCurrentRange.put(tableOps.mPreviousCell); } // if the row is being repeated, there
   * are always vertical spans (fill same content multiple times if (tableOps.mLastRow -
   * tableOps.mFirstRow > 0) { triggerOperation = true; } // there is an upcoming fill operation,
   * the previous content has to be flushed if (tableOps.getCellRepetition() >
   * MIN_REPEATING_CONTENT_CELLS) { triggerOperation = true; } tableOps.mFirstEqualCellNo =
   * tableOps.mCurrentColumnNo; } } // 2) Current Content Cell, Previous Empty Cell (never have
   * saved anything) } else if (currentCell != null && tableOps.mPreviousCell == null) { // &&
   * tableOps.mCurrentRange == null tableOps.mFirstEqualCellNo = tableOps.mCurrentColumnNo; if
   * (tableOps.mFirstContentCellNo == -1) { // reset the empty cell counter - if previous was empty
   * tableOps.mFirstContentCellNo = tableOps.mCurrentColumnNo; tableOps.mEmptyCellCount = 0; } else
   * { if (tableOps.getCellRepetition() > MIN_REPEATING_CONTENT_CELLS) { triggerOperation = true; }
   * else { if (tableOps.mCurrentRange == null) { tableOps.mCurrentRange = new JSONArray(); } for
   * (int i = 0; tableOps.mEmptyCellCount > i; i++) { tableOps.mCurrentRange.put(JSONObject.NULL); }
   * tableOps.mEmptyCellCount = 0; } } // 3) Content Cell empty, Previo Cell full } else if
   * (currentCell == null && tableOps.mPreviousCell != null) { tableOps.mEmptyCellCount +=
   * tableOps.getCellRepetition(); // as there had been previously content // check if it was
   * repeating content if (previousContentRepetition > MIN_REPEATING_CONTENT_CELLS) {
   * triggerOperation = true; } else { if (tableOps.mCurrentRange == null) { tableOps.mCurrentRange
   * = new JSONArray(); } // save the previous cell for later compressed output for (int i = 0;
   * tableOps.mCurrentColumnNo - tableOps.mFirstEqualCellNo > i; i++) {
   * tableOps.mCurrentRange.put(tableOps.mPreviousCell); } // if the row is being repeated, there
   * are always vertical spans (fill same content multiple times if (tableOps.mLastRow -
   * tableOps.mFirstRow > 0) { triggerOperation = true; } } tableOps.mFirstEqualCellNo = -1; // if
   * there was previously repeating content cells // 4) Both are null } else if (currentCell == null
   * && tableOps.mPreviousCell == null & !isRow) { // note that an empty cell was passed
   * tableOps.mEmptyCellCount += tableOps.getCellRepetition(); // if this is the first empty cell if
   * (tableOps.mFirstEmptyCell == -1) { // remember when it started tableOps.mFirstEmptyCell =
   * tableOps.mCurrentColumnNo;
   *
   * <p>// else check if the maximum repeated empty cells was reached and existing content has to be
   * dispatched as an operation } else if (tableOps.mFirstContentCellNo != -1 &&
   * MAX_REPEATING_EMPTY_CELLS > tableOps.mEmptyCellCount) { triggerOperation = true; } }
   *
   * <p>// RANGE CREATION: for every row we flush previous content OR if we want to flush for other
   * reasons if (isRow && tableOps.mFirstContentCellNo > -1 || triggerOperation) { // WRITING
   * WHITESPACE TO ROW // if the last cell used content, but there was previous whitespace, the
   * whitespace has to be explicitly set if (tableOps.mEmptyCellCount > 0 && currentCell != null) {
   * for (int i = 0; tableOps.mEmptyCellCount > i; i++) { if (tableOps.mCurrentRange == null) {
   * tableOps.mCurrentRange = new JSONArray(); } tableOps.mCurrentRange.put(JSONObject.NULL); }
   * tableOps.mEmptyCellCount = 0; } // WRITING CELL TO ROW // if content to flush exist and the
   * operation was triggered // OR there is horizontal repeated content // OR there is vertical
   * repeated content if (tableOps.mCurrentRange != null || previousContentRepetition >
   * MIN_REPEATING_CONTENT_CELLS || isRepeatedRow) { if(tableOps.mCurrentRange != null &&
   * !tableOps.mCurrentRange.isEmpty()) { Component rootComponent = mSchemaDoc.getRootComponent();
   * TableTableElement sheet = (TableTableElement)rootComponent.getChildNode(tableOps.mSheetNo); }
   *
   * <p>mJsonOperationProducer.addRange(tableOps.mSheetNo, tableOps.mFirstRow, tableOps.mLastRow,
   * tableOps.mPreviousRepeatedRows, tableOps.mFirstContentCellNo, previousContentRepetition,
   * tableOps.mPreviousCell, tableOps.mCurrentRange, previousContentRepetition >
   * MIN_REPEATING_CONTENT_CELLS); } // if a fill sufficent repeating is now after a content, the
   * previous content was flushed if (tableOps.mFirstEqualCellNo == tableOps.mCurrentColumnNo) { //
   * but still a content and repeating content exits tableOps.mFirstContentCellNo =
   * tableOps.mFirstEqualCellNo; } else { if (currentCell != null) { tableOps.mFirstContentCellNo =
   * tableOps.mCurrentColumnNo; tableOps.mFirstEqualCellNo = tableOps.mCurrentColumnNo; } else {
   * tableOps.mFirstContentCellNo = -1; tableOps.mFirstEqualCellNo = -1; } } tableOps.mCurrentRange
   * = null; } if (!isRow) { // Making the current cell the previous for next round
   * tableOps.mPreviousCell = currentCell; tableOps.mCurrentColumnNo +=
   * tableOps.getCellRepetition(); tableOps.setCellRepetition(1); } else { // after the end of a row
   * reset all values tableOps.mSheetNo = null; tableOps.mCurrentRange = null;
   * tableOps.mPreviousCell = null; tableOps.mFirstContentCellNo = -1; tableOps.mFirstEqualCellNo =
   * -1; tableOps.mCurrentColumnNo = 0; tableOps.mEmptyCellCount = 0; tableOps.mFirstEmptyCell = -1;
   * tableOps.setCellRepetition(1); } return tableOps; }
   *
   * <p>static void stashColumnWidths(TableTableElement tableElement) {
   * List<TableTableColumnElement> existingColumnList = getTableColumnElements(tableElement, new
   * LinkedList<TableTableColumnElement>()); List<Integer> tableColumWidths =
   * OdfFileSaxHandler.collectColumnWidths(tableElement, existingColumnList);
   * tableElement.pushTableGrid(tableColumWidths); }
   *
   * <p>static List<Integer> collectColumnWidths(TableTableElement tableElement,
   * List<TableTableColumnElement> columns) { boolean hasRelColumnWidth = false; boolean
   * hasAbsColumnWidth = false; boolean hasColumnWithoutWidth = false; List<Integer> columnRelWidths
   * = new ArrayList(); for (TableTableColumnElement column : columns) { if
   * (column.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "style-name")) { Length tableWidth
   * = getPropertyLength(StyleTablePropertiesElement.Width, tableElement);
   *
   * <p>int repeatedColumns = 1; if (column.hasAttributeNS(OdfDocumentNamespace.TABLE.getUri(),
   * "number-columns-repeated")) { repeatedColumns =
   * Integer.parseInt(column.getAttributeNS(OdfDocumentNamespace.TABLE.getUri(),
   * "number-columns-repeated")); }
   *
   * <p>String columnRelWidth = getProperty(StyleTableColumnPropertiesElement.RelColumnWidth,
   * column);
   *
   * <p>// it is being assumed, when the columnRelWidth is once set, it is always set if
   * (columnRelWidth != null && !columnRelWidth.isEmpty()) { hasRelColumnWidth = true; if
   * (hasAbsColumnWidth) { LOG.warning("******* BEWARE: Absolute and relative width are not supposed
   * to be mixed!! ***********"); } columnRelWidth = columnRelWidth.substring(0,
   * columnRelWidth.indexOf('*')); Integer relWidth = Integer.parseInt(columnRelWidth); for (int i =
   * 0; i < repeatedColumns; i++) { columnRelWidths.add(relWidth); } } else { // if there is no
   * relative column width if (hasRelColumnWidth) { LOG.warning("******* BEWARE: Absolute and
   * relative width are not supposed to be mixed!! ***********"); }
   *
   * <p>Length columnWidth = getPropertyLength(StyleTableColumnPropertiesElement.ColumnWidth,
   * column); // there can be only table width and .. if (tableWidth != null) { // columnwidth, with
   * a single one missing if (columnWidth != null) { hasAbsColumnWidth = true; int widthFactor =
   * (int) Math.round((columnWidth.getMillimeters() * 100) / tableWidth.getMillimeters()); for (int
   * i = 0; i < repeatedColumns; i++) { columnRelWidths.add(widthFactor); } } else { if
   * (hasColumnWithoutWidth) { LOG.warning("******* BEWARE: Two columns without width and no column
   * width are not expected!! ***********"); } hasColumnWithoutWidth = true; } // if the table is
   * not set, it will always be unset.. } else { if (columnWidth != null) { hasAbsColumnWidth =
   * true; int widthFactor = (int) Math.round((columnWidth.getMicrometer() * 10)); for (int i = 0; i
   * < repeatedColumns; i++) { columnRelWidths.add(widthFactor); } } else { LOG.warning("*******
   * BEWARE: Two columns without width and no column width are not expected!! ***********"); } } } }
   * } return columnRelWidths; } /* Returns all TableTableColumn descendants that exist within the
   * tableElement, even within groups, columns and header elements
   *
   * <p>static List<TableTableColumnElement> getTableColumnElements(Element parent, List columns) {
   * NodeList children = parent.getChildNodes(); for (int i = 0; i < children.getLength(); i++) {
   * Node child = children.item(i); if (child instanceof Element) { if (child instanceof
   * TableTableColumnElement) { columns.add(child); } else if (child instanceof
   * TableTableColumnGroupElement || child instanceof TableTableHeaderColumnsElement || child
   * instanceof TableTableColumnsElement) { columns = getTableColumnElements((Element) child,
   * columns); } else if (child instanceof TableTableRowGroupElement || child instanceof
   * TableTableHeaderRowsElement || child instanceof TableTableRowElement || child instanceof
   * TableTableRowsElement) { break; } } } return columns; }
   */
}
