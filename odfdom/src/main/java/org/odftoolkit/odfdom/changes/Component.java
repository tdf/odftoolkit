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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dSceneElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCaptionElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCircleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawConnectorElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCustomShapeElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawEllipseElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageThumbnailElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPathElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPolygonElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPolylineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawRectElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawRegularPolygonElement;
import org.odftoolkit.odfdom.dom.element.form.FormConnectionResourceElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationEndElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeChartElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDatabaseElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDrawingElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeImageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterLeftElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderLeftElement;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextAuthorInitialsElement;
import org.odftoolkit.odfdom.dom.element.text.TextBookmarkElement;
import org.odftoolkit.odfdom.dom.element.text.TextHElement;
import org.odftoolkit.odfdom.dom.element.text.TextLineBreakElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListHeaderElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.element.text.TextSElement;
import org.odftoolkit.odfdom.dom.element.text.TextSpanElement;
import org.odftoolkit.odfdom.dom.element.text.TextTabElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The component is a logical modular entity, to abstract from the
 * implementation details of the XML.
 *
 *
 * @author svante.schubertATgmail.com
 */
public class Component {

    private static final Logger LOG = Logger.getLogger(Component.class.getName());
//
//public enum OPERATION {
//    INSERT_PARAGRAPH ("insertParagraph", 2),
//    INSERT_TEXT   ("insertText", 3);
//
//    private final String name;   // function name
//    private final int parameterCount; // number of arguments
//    Operation(String name, int parameterCount) {
//        this.name = name;
//        this.parameterCount = parameterCount;
//    }
//    public String name()   { return parameterCount; }
//    public name parameterCount() { return parameterCount; }
//
//    public double surfaceGravity() {
//        return G * mass / (radius * radius);
//    }
//    public double surfaceWeight(double otherMass) {
//        return otherMass * surfaceGravity();
//    }
//}
    private static final String LIBRE_OFFICE_MS_INTEROP_NAMESPACE = "urn:openoffice:names:experimental:ooo-ms-interop:xmlns:field:1.0";
    private static final String LIBRE_OFFICE_MS_INTEROP_LOCALNAME = "fieldmark";

    /**
     * Tests if the given element is the start of a component
     *
     * @return true if the given element is the root of an ODF component
     */
    public static boolean isComponentRoot(Element element) {
        boolean isComponent = false;
        if (element instanceof OdfElement) {
            isComponent = isComponentRoot(((OdfElement) element).getNamespaceURI(), ((OdfElement) element).getLocalName());
        }
        return isComponent;
    }

    /**
     * Tests if the given element is the start of a component
     *
     * @return true if the given element is the root of an ODF component
     */
    public static boolean isComponentRoot(String uri, String localName) {
        boolean isComponent = false;
        if (uri != null) {
            if (uri.equals(TextPElement.ELEMENT_NAME.getUri())) {
                if (localName.equals(TextPElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                } else if (localName.equals(TextHElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                } else if (localName.equals(TextTabElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                } else if (localName.equals(TextLineBreakElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                }
            } else if (uri.equals(TableTableElement.ELEMENT_NAME.getUri())) {
                if (localName.equals(TableTableElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                } else if (localName.equals(TableTableRowElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                } else if (localName.equals(TableTableCellElement.ELEMENT_NAME.getLocalName())) {
                    isComponent = true;
                }
            } else if (uri.equals(DrawFrameElement.ELEMENT_NAME.getUri()) && localName.equals(DrawFrameElement.ELEMENT_NAME.getLocalName())) {
                isComponent = true;
            } else if (isShapeElement(uri, localName)) {
                isComponent = true;
            } else if ((uri.equals(OfficeAnnotationElement.ELEMENT_NAME.getUri()) && localName.equals(OfficeAnnotationElement.ELEMENT_NAME.getLocalName()))
                    || (uri.equals(OfficeAnnotationEndElement.ELEMENT_NAME.getUri()) && localName.equals(OfficeAnnotationEndElement.ELEMENT_NAME.getLocalName()))) {
                isComponent = true;
            }
            if (isField(uri, localName)) {
                isComponent = true;
            }
        }
        return isComponent;
    }

    /**
     * Tests if the given element is the wrapper around a descendant component
     * root element
     *
     * @return true if the given element is a potential wrapper around an ODF
     * component
     */
    public static boolean isComponentWrapper(Element element) {
        boolean isWrapper = false;
        if (element instanceof OdfElement) {
            isWrapper = isComponentWrapper(((OdfElement) element).getNamespaceURI(), ((OdfElement) element).getLocalName());
        }
        return isWrapper;
    }

    /**
     * Tests if the given element is the wrapper around a descendant component
     * root element
     *
     * @return true if the given element is a potential wrapper around an ODF
     * component
     */
    public static boolean isComponentWrapper(String uri, String localName) {
        boolean isWrapper = false;
        if (uri != null && uri.equals(TextListElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(TextListElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            } else if (localName.equals(TextListItemElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            } else if (localName.equals(TextListHeaderElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            } else if (localName.equals(TextBookmarkElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            }
        } else if (uri != null && uri.equals(TableTableElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(TableTableRowsElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            } else if (localName.equals(TableTableRowGroupElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            } else if (localName.equals(TableTableColumnsElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            } else if (localName.equals(TableTableColumnGroupElement.ELEMENT_NAME.getLocalName())) {
                isWrapper = true;
            }
        }
        return isWrapper;
    }

    //    /** Includes text delimiter */
    //    private boolean isTextElement(String uri, String localName) {
    //        boolean isTextElement = false;
    //        // Check for component root element by combination of URI and localName
    //        if (uri.equals(TextPElement.ELEMENT_NAME.getUri()) && (localName.equals(TextPElement.ELEMENT_NAME.getLocalName())
    //                || localName.equals(TextSpanElement.ELEMENT_NAME.getLocalName())
    //                || localName.equals(TextHElement.ELEMENT_NAME.getLocalName()))) {
    //            isTextElement = true;
    //        }
    //        return isTextElement;
    //    }
    /**
     * Returns true if the Node is an TextPElement or TextHElement. Both are the
     * root elements of text containers. Text container have special handline of
     * whitespace, see
     * http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part1.html#White-space_Characters
     */
    public static boolean isTextComponentRoot(Node textContainer) {
        boolean isTextElement = false;
        if (textContainer instanceof TextPElement || textContainer instanceof TextHElement) {
            isTextElement = true;
        }
        return isTextElement;
    }

    public static boolean isTextComponentRoot(String uri, String localName) {
        boolean isComponent = false;
        if (uri != null && uri.equals(TextPElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(TextPElement.ELEMENT_NAME.getLocalName())) {
                isComponent = true;
            } else if (localName.equals(TextHElement.ELEMENT_NAME.getLocalName())) {
                isComponent = true;
            }
        }
        return isComponent;
    }

    public static boolean isRowComponentRoot(Node textContainer) {
        boolean isRowElement = false;
        if (textContainer instanceof TableTableRowElement) {
            isRowElement = true;
        }
        return isRowElement;
    }

    public static boolean isRowComponentRoot(String uri, String localName) {
        boolean isComponent = false;
        if (uri != null && uri.equals(TableTableRowElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(TableTableRowElement.ELEMENT_NAME.getLocalName())) {
                isComponent = true;
            } else if (localName.equals(TextHElement.ELEMENT_NAME.getLocalName())) {
                isComponent = true;
            }
        }
        return isComponent;
    }

    public static boolean isField(String uri, String localName) {
        boolean isField = false;
        if (uri != null && uri.equals(TextAuthorInitialsElement.ELEMENT_NAME.getUri())) {
            if (localName.equals("author-initials") || localName.equals("author-name") || localName.equals("bookmark-ref") || localName.equals("chapter") || localName.equals("character-count") || localName.equals("conditional-text") || localName.equals("creation-date") || localName.equals("creation-time") || localName.equals("creator") || localName.equals("database-display") || localName.equals("database-name") || localName.equals("database-row-number") || localName.equals("date") || localName.equals("dde-connection") || localName.equals("description") || localName.equals("editing-cycles") || localName.equals("editing-duration") || localName.equals("execute-macro") || localName.equals("expression") || localName.equals("file-name") || localName.equals("hidden-paragraph") || localName.equals("hidden-text") || localName.equals("image-count") || localName.equals("initial-creator") || localName.equals("keywords") || localName.equals("measure") || localName.equals("meta-field") || localName.equals("modification-date") || localName.equals("modification-time") || localName.equals("note-ref") || localName.equals("object-count") || localName.equals("page-continuation") || localName.equals("page-count") || localName.equals("page-number") || localName.equals("page-variable-get") || localName.equals("page-variable-set") || localName.equals("paragraph-count") || localName.equals("placeholder") || localName.equals("print-date") || localName.equals("print-time") || localName.equals("printed-by") || localName.equals("reference-ref") || localName.equals("script") || localName.equals("sender-city") || localName.equals("sender-company") || localName.equals("sender-country") || localName.equals("sender-email") || localName.equals("sender-fax") || localName.equals("sender-firstname") || localName.equals("sender-initials") || localName.equals("sender-lastname") || localName.equals("sender-phone-private") || localName.equals("sender-phone-work") || localName.equals("sender-position") || localName.equals("sender-postal-code") || localName.equals("sender-state-or-province") || localName.equals("sender-street") || localName.equals("sender-title") || localName.equals("sequence-ref") || localName.equals("sequence") || localName.equals("sheet-name") || localName.equals("subject") || localName.equals("table-count") || localName.equals("template-name") || localName.equals("text-input") || localName.equals("time") || localName.equals("title") || localName.equals("user-defined") || localName.equals("user-field-get") || localName.equals("user-field-input") || localName.equals("variable-get") || localName.equals("variable-input") || localName.equals("variable-set") || localName.equals("word-count")) {
                isField = true;
            }
        } else if (uri != null && uri.equals(FormConnectionResourceElement.ELEMENT_NAME.getUri())) {
            if (localName.equals("connection-resource")) {
                isField = true;
            }
        } else if (uri != null && uri.equals(LIBRE_OFFICE_MS_INTEROP_NAMESPACE)) {
            if (localName.equals(LIBRE_OFFICE_MS_INTEROP_LOCALNAME)) {
                isField = true;
            }
        }
        return isField;
    }

    /**
     * Tests if the given element is the start of a document
     *
     * @return true if the given element is the root of an ODF document (e.g.
     * office:text)
     */
    public static boolean isDocumentRoot(String uri, String localName) {
        boolean isRoot = false;
        if (uri.equals(OfficeTextElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(OfficeTextElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            } else if (localName.equals(OfficeSpreadsheetElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            } else if (localName.equals(OfficePresentationElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            } else if (localName.equals(OfficeChartElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            } else if (localName.equals(OfficeDrawingElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            } else if (localName.equals(OfficeImageElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            } else if (localName.equals(OfficeDatabaseElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            }
        } else if (isHeaderRoot(uri, localName) || isFooterRoot(uri, localName)) {
            isRoot = true;
        }
        return isRoot;
    }

    /**
     * Tests if the given element is the start of a header within a page style.
     * The content of a header is equal to the content of a usual ODT text file
     * (ie. <office:text>).
     *
     * @return true if the given element is the root of a header (i.e.
     * style:header)
     */
    public static boolean isHeaderRoot(String uri, String localName) {
        boolean isRoot = false;
        if (uri != null && uri.equals(StyleHeaderElement.ELEMENT_NAME.getUri())) {
            // style:header
            if (localName.equals(StyleHeaderElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            }
        }
        if (uri != null && uri.equals(StyleHeaderLeftElement.ELEMENT_NAME.getUri())) {
            // style:header-left
            if (localName.equals(StyleHeaderLeftElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            }
        }
        if (uri != null && uri.equals(StyleHeaderLeftElement.ELEMENT_NAME.getUri())) {
            // style:header-first
            if (localName.equals("header-first")) {
                isRoot = true;
            }
        }
        return isRoot;
    }

    /**
     * Tests if the given element is the start of a footer within a page style.
     * The content of a footer is equal to the content of a usual ODT text file
     * (ie. <office:text>).
     *
     * @return true if the given element is the root of a footer (i.e.
     * style:footer)
     */
    public static boolean isFooterRoot(String uri, String localName) {
        boolean isRoot = false;

        if (uri != null && uri.equals(StyleFooterElement.ELEMENT_NAME.getUri())) {
            // style:footer
            if (localName.equals(StyleFooterElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            }
        }
        if (uri != null && uri.equals(StyleFooterLeftElement.ELEMENT_NAME.getUri())) {
            // style:footer-left
            if (localName.equals(StyleFooterLeftElement.ELEMENT_NAME.getLocalName())) {
                isRoot = true;
            }
        }
        if (uri != null && uri.equals(StyleFooterLeftElement.ELEMENT_NAME.getUri())) {
            // style:footer-first
            if (localName.equals("footer-first")) {
                isRoot = true;
            }
        }
        return isRoot;
    }

    /**
     * Tests if the given element is a shape element Shapes are in general those
     * with elements with a
     *
     * @svg:width and
     * @text:anchor-type. With the exception of office:annotation, usually
     * viewed aside the document as note. The list of placeholder elements is
     * therefore: dr3d:scene draw:caption draw:circle draw:control
     * draw:custom-shape draw:ellipse draw:page-thumbnail draw:path draw:polygon
     * draw:polyline draw:rect draw:regular-polygon
     * @return true if the given element is the root of an ODF shape element
     */
    public static boolean isShapeElement(String uri, String localName) {
        boolean isShape = false;
        if (uri != null && localName != null) {
            if (uri.equals(DrawRectElement.ELEMENT_NAME.getUri()) && (localName.equals(DrawCaptionElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawCircleElement.ELEMENT_NAME.getLocalName()) || localName.equals(DrawControlElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawCustomShapeElement.ELEMENT_NAME.getLocalName()) || localName.equals(DrawEllipseElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawGElement.ELEMENT_NAME.getLocalName()) || localName.equals(DrawPageThumbnailElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawPathElement.ELEMENT_NAME.getLocalName()) || localName.equals(DrawPolylineElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawPolygonElement.ELEMENT_NAME.getLocalName()) || localName.equals(DrawRectElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawRegularPolygonElement.ELEMENT_NAME.getLocalName()) || localName.equals(DrawLineElement.ELEMENT_NAME.getLocalName())
                    || localName.equals(DrawConnectorElement.ELEMENT_NAME.getLocalName()))) {
                isShape = true;
            }
            if (uri.equals(Dr3dSceneElement.ELEMENT_NAME.getUri())) {
                if (localName.equals(Dr3dSceneElement.ELEMENT_NAME.getLocalName())) {
                    isShape = true;
                }
            }
        }
        return isShape;
    }

    /**
     * Tests if the given element is a whitespace element
     *
     * @return true if the given element is an ODF whitespace element
     */
    public static boolean isWhiteSpaceElement(String uri, String localName) {
        boolean isWhiteSpace = false;
        if (uri.equals(TextSElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(TextSElement.ELEMENT_NAME.getLocalName())) {
                isWhiteSpace = true;
            }
        }
        return isWhiteSpace;
    }

    public static boolean isCoveredComponentRoot(String uri, String localName) {
        boolean isComponent = false;
        if (uri.equals(TableTableElement.ELEMENT_NAME.getUri())) {
            if (localName.equals(TableCoveredTableCellElement.ELEMENT_NAME.getLocalName())) {
                isComponent = true;
            }
        }
        return isComponent;
    }

    /**
     * @return true if the node is a text delimiter element
     */
    public static boolean isTextSelection(Node textSelection) {
        boolean isTextElement = false;
        if (textSelection instanceof TextSpanElement || textSelection instanceof TextAElement) {
            isTextElement = true;
        }
        return isTextElement;
    }

    /**
     * Only being used to create the root of all components, representing the
     * document without a parent element
     */
    public Component(OdfElement componentElement) {
        mRootElement = componentElement;
        componentElement.setComponent(this);
    }

    protected Component(OdfElement componentElement, Component parent) {
        mRootElement = componentElement;
        componentElement.setComponent(this);
        mParent = parent;
    }
    // All component children
    List<Component> mChildren;
    // the root XML element of the component
    public OdfElement mRootElement;
    // the parent component
    private Component mParent;
    private Component mRootComponent;
    /**
     * if a repeated attribute was set at the component. In this case the
     * positioning will change.
     */
    boolean mHasRepeated = false;

    /**
     * Returns the parent component
     */
    public Component getParent() {
        return mParent;
    }

    /**
     * Sometimes (e.g. if the child is a paragraph within list elements). The
     * parent root element of the child component root element will not be
     * directly children. It will be checked if there is a child element or list
     * level 10 has reached.
     */
    public static OdfElement getCorrectStartElementOfChild(OdfElement parentElement, OdfElement existingChildElement) {
        // element usually used for positioning of insertion
        // lists elements are boilerplate between paragraph parent and child component (paragraph),
        OdfElement existingParentElement = (OdfElement) existingChildElement.getParentNode();
        // if the existing component is a paragraph with list properities
        if ((existingParentElement instanceof TextListItemElement || existingParentElement instanceof TextListHeaderElement) && existingChildElement instanceof TextParagraphElementBase) {
            TextParagraphElementBase existingParagraph = (TextParagraphElementBase) existingChildElement;
            JsonOperationConsumer.isolateListParagraph(existingParagraph);
            OdfElement potentialParent = (OdfElement) existingParagraph.getParentNode();
            while (potentialParent != null && !parentElement.equals(potentialParent)) {
                potentialParent = (OdfElement) potentialParent.getParentNode();
                if (parentElement.equals(potentialParent)) {
                    break;
                } else {
                    existingChildElement = potentialParent;
                }
            }
        }
        return existingChildElement;
    }

    public Component getLastChild() {
        Component lastChild = null;
        if (mChildren != null) {
            lastChild = mChildren.get(mChildren.size());
        }
        return lastChild;
    }

    public Document getOwnerDocument() {
        return mRootElement.getOwnerDocument();
    }

    /**
     * @return the child at the given position
     */
    public Node getChildNode(int position) {
        Node rootElement = null;
        Component c = null;
        if (mChildren != null && mChildren.size() > position) {
            c = mChildren.get(position);
        }
        if (c != null) {
            rootElement = c.getRootElement();
        }
        return rootElement;
    }

    public Component get(JSONArray position) {
        return get(position, false, false, 0);
    }

    /**
     * Get descendant component by its relative position to this component.
     * Counting starts with 0.
     *
     * @param position relative position of the desired component relative to the
     * current component
     * @param needParent if true the parent of the given position is returned
     * @param needFollowingSibling if true the next sibling of the given
     * position is returned (exclusive to getPositionsFollowingSibling)
     */
    protected Component get(JSONArray position, boolean needParent, boolean needFollowingSibling, int depth) {
        // check recursion end conditions
        Component c = null;
        final int maxDepth = position.length() - 1;
        // if not the correct depth is reached, go deeper
        if (!needParent && maxDepth > depth || (needParent && (maxDepth - 1 != depth))) {
            try {
                // get from this level the currect component
                Node startNode = getChildNode(position.getInt(depth));
                if (startNode instanceof OdfElement) {
                    c = ((OdfElement) startNode).getComponent();
                }
                // call recursive this method with an additional depth for getting its child
                if (c != null) {
                    c = c.get(position, needParent, needFollowingSibling, depth + 1);
                } else {
                    // we need a special component for table row and cell, to expand if necessary!
                    // take the last existing row/cell and insert as many empty before the last one.
                    // get the position of the last XML and the last XML as well
                    // split up repeated
                    // do not split up covered, but .. hmm... get an empty cell back
                    // shall we track the position on-the-fly via DOM events?
//					OdfElement lastElement = mRootElement.getLastChildElement();
//					Component c2 = lastElement.getComponent();
//					String pos = c2.getPosition(c2);
                    LOG.fine("Component yet missing!");
                }
            } catch (JSONException ex) {
                Logger.getLogger(Component.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                // get the desired child
                if (needFollowingSibling) {
                    Node startNode = getChildNode(position.getInt(depth) + 1);
                    if (startNode instanceof OdfElement) {
                        c = ((OdfElement) startNode).getComponent();
                    }
                } else {
                    Node startNode = getChildNode(position.getInt(depth));
                    if (startNode instanceof OdfElement) {
                        c = ((OdfElement) startNode).getComponent();
                        Component parent = c.getParent();
                        parent.getPosition(c);
                    }
                }
                // call recursive this method with an additional depth for getting its child
                if (c == null) {
                    // we need a special component for table row and cell, to expand if necessary!
                    // take the last existing row/cell and insert as many empty before the last one.
                    // get the position of the last XML and the last XML as well
                    // split up repeated
                    // do not split up covered, but .. hmm... get an empty cell back
                    // shall we track the position on-the-fly via DOM events?
//					OdfElement lastElement = mRootElement.getLastChildElement();
//					Component c2 = lastElement.getComponent();
//					String pos = c2.getPosition(c2);
                    LOG.fine("Component yet missing!");
                }
            } catch (JSONException ex) {
                Logger.getLogger(Component.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return c;
    }

    /**
     * Get next sibling component of the given position. Counting start with 0.
     */
    public Component getNextSiblingOf(JSONArray position) {
        return get(position, false, true, 0);
    }

    /**
     * Get parent component of the given position
     */
    public Component getParentOf(JSONArray position) {
        Component c = null;
        if (position.length() == 1) {
            c = getRootComponent();
        } else {
            c = get(position, true, false, 0);
        }
        return c;
    }

    public Component getRootComponent() {
        if (mRootComponent == null) {
            Component parent = this;
            while (parent != null) {
                mRootComponent = parent;
                parent = parent.getParent();
            }
        }
        return mRootComponent;
    }

    public List<Component> getChildren() {
        return mChildren;
    }

    /**
     * @return the root element of the component
     */
    public OdfElement getRootElement() {
        return mRootElement;
    }

    /**
     * @param odfElement the new root element of the component Used after
     * splitting a list containing paragraphs and assigning the new cloned
     * paragraph elements to the existing components.
     */
    void setRootElement(OdfElement odfElement) {
        mRootElement = odfElement;
    }

    /**
     * Appending a child element to the component
     */
    public Component createChildComponent(OdfElement componentRoot) {
        componentRoot.markAsComponentRoot(true);
        return createChildComponent(-1, this, componentRoot);
    }

    /**
     * Inserts a component at the given position as child
     *
     * @param position of the component, a -1 is going to append the element
     */
    public static Component createChildComponent(int position, Component parentComponent, OdfElement newChildElement) {
        Component c = createComponent(parentComponent, newChildElement);

        if (!(parentComponent instanceof Table || parentComponent instanceof Row || parentComponent instanceof Cell || parentComponent instanceof TextContainer)) {
            addComponent(position, parentComponent, c);
        }

        LOG.log(Level.FINEST, "***\n***  New Component: {0}\n*** {1}\n***", new Object[]{parentComponent.getPosition(c), newChildElement.toString()});
        return c;
    }

    public static Component createComponent(Component parentComponent, OdfElement newChildElement) {
        Component c;
        // Mark the element as component, so for instance an ODF Frame with image can be recognized as component
        newChildElement.markAsComponentRoot(true);

        // if the component is a table container
        if (newChildElement instanceof TableTableElement) {
            c = new Table<Component>(newChildElement, parentComponent);

        } else if (newChildElement instanceof TableTableRowElement) {
            c = new Row<Component>(newChildElement, parentComponent);

        } else if (newChildElement instanceof TableTableCellElement || newChildElement instanceof TableCoveredTableCellElement) {
            c = new Cell<Component>(newChildElement, parentComponent);

        } else if (isTextComponentRoot(newChildElement)) {
            c = new TextContainer<Component>(newChildElement, parentComponent);
            // if the component is a text container (have to deal with text nodes and elements)
        } else if (newChildElement instanceof OfficeAnnotationElement) {
            c = new Annotation(newChildElement, parentComponent);
        } else {
            c = new Component(newChildElement, parentComponent);
        }
        newChildElement.setComponent(c);
        return c;
    }

    /**
     * Adds the given component as new child component. No XML elements are
     * being changed!
     *
     * @param index starting with 0 representing the position of the child, if
     * -1 the new child will be appended
     */
    static void addComponent(int pos, Component parent, Component child) {
        parent.addChild(pos, child);
    }

    /**
     * Adds the given component as new child component. No XML elements are
     * being changed!
     *
     * @param index starting with 0 representing the position of the child, if
     * -1 the new child will be appended
     */
    public void addChild(int index, Component c) {
        if (mChildren == null) {
            if (mChildren == null) {
                mChildren = new LinkedList<Component>();
            }
        }
        if (index >= 0) {
            mChildren.add(index, c);
        } else {
            mChildren.add(c);
        }
    }

    /**
     * Only removes from the component list, not from the DOM
     */
    public Node remove(int position) {
        Node n = null;
        if (mChildren != null) {
            Component c = mChildren.remove(position);
            if (c != null) {
                n = c.getRootElement();
            }
        }
        return n;
    }

    /**
     * Returns the number of child components
     */
    public int size() {
        int size = 0;
        if (mChildren != null) {
            size = mChildren.size();
        }
        return size;
    }

    public void hasRepeated(boolean hasRepeated) {
        mHasRepeated = hasRepeated;
    }

    public boolean hasRepeated() {
        return mHasRepeated;
    }

    /**
     * @return the position as a slash separated string
     */
    protected String getPosition(Component c) {
        String s;
        List<Integer> position;
        int childPos;
        if (c.mParent != null) {
            position = new LinkedList();
            Component parent;
            while ((parent = c.getParent()) != null) {
                childPos = parent.indexOf(c);
//				if (childPos < 0) {
//					childPos = indexOf(c);
//				}
                position.add(childPos);
                c = parent;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = position.size() - 1; i >= 0; i--) {
                sb.append("/").append(position.get(i));
            }
            s = sb.toString();
        } else {
            s = "/";
        }
        return s;
    }

    /**
     * @return the position as a slash separated string
     */
    protected static String getPositionString(Component c) {
        String s;
        List<Integer> position;
        int childPos;
        if (c.mParent != null) {
            position = new LinkedList();
            Component parent;
            while ((parent = c.getParent()) != null) {
                childPos = parent.indexOf(c);
//				if (childPos < 0) {
//					childPos = indexOf(c);
//				}
                position.add(childPos);
                c = parent;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = position.size() - 1; i >= 0; i--) {
                sb.append("/").append(position.get(i));
            }
            s = sb.toString();
        } else {
            s = "/";
        }
        return s;
    }

    /**
     * @returns the position of the child component c or given Node within the
     * parents children list. Returns -1 if it is not a child.
     */
    public int indexOf(Object o) {
        int position = -1;
        // Either use the previous way of creating lists of child components
        if (mChildren != null) {
            position = mChildren.indexOf(o);
        } else {
            // Either use the previous way of creating lists of child components
            Node targetNode = null;
            OdfElement parentNode = null;
            if (o instanceof Component) {
                Component c = ((Component) o);
                targetNode = c.getRootElement();
                parentNode = c.getParent().getRootElement();
            } else if (o instanceof Node) {
                targetNode = (Node) o;
                parentNode = (OdfElement) targetNode.getParentNode();
            }

            if (targetNode != null && targetNode instanceof OdfElement) {
                if (parentNode != null && parentNode.equals(mRootElement)) {
                    position = findPosition(parentNode, (OdfElement) targetNode);
                }
            }
        }
        return position;
    }

    /**
     * Recursive traverse the text container and count the children
     */
    private Integer findPosition(Element parentComponentElement, OdfElement targetNode) {
        int pos = -1;
        // Only start the recursion if the parameters are not null and in an ancestor relationship
        if (parentComponentElement != null && targetNode != null && targetNode.hasAncestor(parentComponentElement)) {
            findChild(0, parentComponentElement, targetNode);
        }
        return pos;
    }

    /**
     * Recursive traverse the text container and count the children
     */
    private Integer findChild(Integer pos, Element parentComponentElement, Node child) {
        // Traverse first horizontal backwards and if no further sibling, traverse up (e.g. for paragraphs within list required)
        if (child != null) {
            if (child instanceof OdfElement) {
                OdfElement childElement = ((OdfElement) child);
                if (childElement.isComponentRoot()) {
                    // if it is a component we found one (even ourselves in the beginning) at one
                    pos += childElement.getRepetition();
                    pos = findChild(pos, parentComponentElement, childElement.getPreviousSibling());
                } else if (Component.isComponentWrapper(childElement)) {
                    pos = findChild(pos, parentComponentElement, childElement.getLastChildElement());
                } else {
                    pos = findChild(pos, parentComponentElement, childElement.getPreviousSibling());
                }
            } else {
                // do the recursion even for text nodes, comments, etc.
                pos = findChild(pos, parentComponentElement, child.getPreviousSibling());
            }
        }
        return pos;
    }

    @Override
    public String toString() {
        String s = "POS:" + getPosition(this);
        if (mRootElement != null) {
            s += mRootElement.getPrefix() + ":" + mRootElement.getLocalName();
        } else {
            s += "NO ROOT ELEMENT!!!";
        }
        return s;
    }

    // These sets should be static initialized and a central place, best generated at the family superclass
    public static Map<String, OdfStylePropertiesSet> getAllStyleGroupingIdProperties(OdfStylableElement styleElement) {
        return getAllStyleGroupingIdProperties(styleElement.getStyleFamily());
    }

    public static Map<String, OdfStylePropertiesSet> getAllStyleGroupingIdProperties(OdfStyleFamily styleFamily) {
        Map<String, OdfStylePropertiesSet> familyProperties = new HashMap<String, OdfStylePropertiesSet>();
        if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
            familyProperties.put("paragraph", OdfStylePropertiesSet.ParagraphProperties);
            familyProperties.put("character", OdfStylePropertiesSet.TextProperties);
        } else if (styleFamily.equals(OdfStyleFamily.Text)) {
            familyProperties.put("character", OdfStylePropertiesSet.TextProperties);
        } else if (styleFamily.equals(OdfStyleFamily.Table)) {
            familyProperties.put("table", OdfStylePropertiesSet.TableProperties);
        } else if (styleFamily.equals(OdfStyleFamily.TableRow)) {
            familyProperties.put("row", OdfStylePropertiesSet.TableRowProperties);
        } else if (styleFamily.equals(OdfStyleFamily.TableCell)) {
            familyProperties.put("cell", OdfStylePropertiesSet.TableCellProperties);
            familyProperties.put("paragraph", OdfStylePropertiesSet.ParagraphProperties);
            familyProperties.put("character", OdfStylePropertiesSet.TextProperties); //changed from text to character
        } else if (styleFamily.equals(OdfStyleFamily.TableColumn)) {
            familyProperties.put("column", OdfStylePropertiesSet.TableColumnProperties);
        } else if (styleFamily.equals(OdfStyleFamily.Section)) {
            familyProperties.put("section", OdfStylePropertiesSet.SectionProperties);
        } else if (styleFamily.equals(OdfStyleFamily.List)) {
            familyProperties.put("list", OdfStylePropertiesSet.ListLevelProperties);
        } else if (styleFamily.equals(OdfStyleFamily.Chart)) {
            familyProperties.put("chart", OdfStylePropertiesSet.ChartProperties);
            familyProperties.put("drawing", OdfStylePropertiesSet.GraphicProperties); //changed from graphic to drawing
            familyProperties.put("paragraph", OdfStylePropertiesSet.ParagraphProperties);
            familyProperties.put("character", OdfStylePropertiesSet.TextProperties); //changed to text from character
        } else if (styleFamily.equals(OdfStyleFamily.Graphic) || styleFamily.equals(OdfStyleFamily.Presentation)) {
            familyProperties.put("drawing", OdfStylePropertiesSet.GraphicProperties); //changed from graphic to drawing
            familyProperties.put("paragraph", OdfStylePropertiesSet.ParagraphProperties);
            familyProperties.put("character", OdfStylePropertiesSet.TextProperties); //changed from text to character
        } else if (styleFamily.equals(OdfStyleFamily.DrawingPage)) {
            familyProperties.put("drawing", OdfStylePropertiesSet.DrawingPageProperties);
        } else if (styleFamily.equals(OdfStyleFamily.Ruby)) {
            familyProperties.put("ruby", OdfStylePropertiesSet.RubyProperties);
        }
        return familyProperties;
    }

    // In the end, this meths should be better moved to the component class
    public static String getFamilyID(OdfStylableElement styleElement) {
        return getFamilyID(styleElement.getStyleFamily());
    }

    // In the end, this meths should be better moved to the component class
    public static String getMainStyleGroupingId(OdfStylableElement styleElement) {
        return Component.getMainStyleGroupingId(styleElement.getStyleFamily());
    }

    // In the end, this meths should be better moved to the component class
    public static String getStyleNamePrefix(OdfStylableElement styleElement) {
        return getStyleNamePrefix(styleElement.getStyleFamily());
    }

    // In the end, this meths should be better moved to the component class
    public static String getMainStyleGroupingId(OdfStyleFamily styleFamily) {
        String familyID = null;
        if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
            familyID = "paragraph";
        } else if (styleFamily.equals(OdfStyleFamily.Text)) {
            familyID = "character";
        } else if (styleFamily.equals(OdfStyleFamily.Table)) {
            familyID = "table";
        } else if (styleFamily.equals(OdfStyleFamily.TableRow)) {
            familyID = "row";
        } else if (styleFamily.equals(OdfStyleFamily.TableCell)) {
            familyID = "cell";
        } else if (styleFamily.equals(OdfStyleFamily.TableColumn)) {
            familyID = "column";
        } else if (styleFamily.equals(OdfStyleFamily.Section)) {
            familyID = "section";
        } else if (styleFamily.equals(OdfStyleFamily.List)) {
            familyID = "list";
        } else if (styleFamily.equals(OdfStyleFamily.Chart)) {
            familyID = "chart";
        } else if (styleFamily.equals(OdfStyleFamily.Graphic) || styleFamily.equals(OdfStyleFamily.Presentation)) {
            familyID = "drawing";
        } else if (styleFamily.equals(OdfStyleFamily.DrawingPage)) {
            familyID = "drawing";
        } else if (styleFamily.equals(OdfStyleFamily.Ruby)) {
            familyID = "ruby";
        }
        return familyID;
    }

    // In the end, this meths should be better moved to the component class
    public static String getStyleNamePrefix(OdfStyleFamily styleFamily) {
        String familyID = null;
        if (styleFamily.equals(OdfStyleFamily.Paragraph) || styleFamily.equals(OdfStyleFamily.Text) || styleFamily.equals(OdfStyleFamily.Section) || styleFamily.equals(OdfStyleFamily.List) || styleFamily.equals(OdfStyleFamily.Ruby)) {
            familyID = "text";
        } else if (styleFamily.equals(OdfStyleFamily.Table) || styleFamily.equals(OdfStyleFamily.TableRow) || styleFamily.equals(OdfStyleFamily.TableCell) || styleFamily.equals(OdfStyleFamily.TableColumn)) {
            familyID = "table";
        } else if (styleFamily.equals(OdfStyleFamily.Chart)) {
            familyID = "chart";
        } else if (styleFamily.equals(OdfStyleFamily.Graphic) || styleFamily.equals(OdfStyleFamily.Presentation) || styleFamily.equals(OdfStyleFamily.DrawingPage)) {
            familyID = "draw";
        } else if (styleFamily.equals(OdfStyleFamily.Presentation)) {
            familyID = "presentation";
        }
        return familyID;
    }

    // In the end, this meths should be better moved to the component class
    public static String getFamilyID(OdfStyleFamily styleFamily) {
        String familyID = null;
        if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
            familyID = "paragraph";
        } else if (styleFamily.equals(OdfStyleFamily.Text)) {
            familyID = "character";
        } else if (styleFamily.equals(OdfStyleFamily.Table)) {
            familyID = "table";
        } else if (styleFamily.equals(OdfStyleFamily.TableRow)) {
            familyID = "row";
        } else if (styleFamily.equals(OdfStyleFamily.TableCell)) {
            familyID = "cell";
        } else if (styleFamily.equals(OdfStyleFamily.TableColumn)) {
            familyID = "column";
        } else if (styleFamily.equals(OdfStyleFamily.Section)) {
            familyID = "section";
        } else if (styleFamily.equals(OdfStyleFamily.List)) {
            familyID = "list";
        } else if (styleFamily.equals(OdfStyleFamily.Chart)) {
            familyID = "chart";
        } else if (styleFamily.equals(OdfStyleFamily.Graphic) || styleFamily.equals(OdfStyleFamily.Presentation)) {
            familyID = "drawing";
        } else if (styleFamily.equals(OdfStyleFamily.DrawingPage)) {
            familyID = "drawing";
        } else if (styleFamily.equals(OdfStyleFamily.Ruby)) {
            familyID = "ruby";
        }
        return familyID;
    }

// In the end, this meths should be better moved to the component class
    public static String getFamilyDisplayName(OdfStyleFamily styleFamily) {
        String familyID = null;
        if (styleFamily.equals(OdfStyleFamily.Paragraph)) {
            familyID = "Paragraph";
        } else if (styleFamily.equals(OdfStyleFamily.Text)) {
            familyID = "Character";
        } else if (styleFamily.equals(OdfStyleFamily.Table)) {
            familyID = "Table";
        } else if (styleFamily.equals(OdfStyleFamily.TableRow)) {
            familyID = "Row";
        } else if (styleFamily.equals(OdfStyleFamily.TableCell)) {
            familyID = "Cell";
        } else if (styleFamily.equals(OdfStyleFamily.TableColumn)) {
            familyID = "Column";
        } else if (styleFamily.equals(OdfStyleFamily.Section)) {
            familyID = "Section";
        } else if (styleFamily.equals(OdfStyleFamily.List)) {
            familyID = "List";
        } else if (styleFamily.equals(OdfStyleFamily.Presentation)) {
            familyID = "Presentation";
        } else if (styleFamily.equals(OdfStyleFamily.Chart)) {
            familyID = "Chart";
        } else if (styleFamily.equals(OdfStyleFamily.Graphic)) {
            familyID = "Graphic";
        } else if (styleFamily.equals(OdfStyleFamily.DrawingPage)) {
            familyID = "Drawing";
        } else if (styleFamily.equals(OdfStyleFamily.Ruby)) {
            familyID = "Ruby";
        }
        return familyID;
    }

    // In the end, this meths should be better moved to the component class
    /**
     * @return styleFamilyValue the <code>String</code> value * * * * * * * of
     * <code>StyleFamilyAttribute</code>,
     */
    public static String getFamilyName(String styleId) {
        String familyID = null;
        if (styleId.equals("paragraph")) {
            familyID = OdfStyleFamily.Paragraph.getName();
        } else if (styleId.equals("character")) {
            familyID = OdfStyleFamily.Text.getName();
        } else if (styleId.equals("table")) {
            familyID = OdfStyleFamily.Table.getName();
        } else if (styleId.equals("row")) {
            familyID = OdfStyleFamily.TableRow.getName();
        } else if (styleId.equals("cell")) {
            familyID = OdfStyleFamily.TableCell.getName();
        } else if (styleId.equals("column")) {
            familyID = OdfStyleFamily.TableColumn.getName();
        } else if (styleId.equals("graphic")) {
            familyID = OdfStyleFamily.Graphic.getName();
        }
        return familyID;
    }

    // In the end, this meths should be better moved to the component class
    /**
     * @return styleFamily the <code>OdfStyleFamily</code> representation * * *
     * of <code>StyleFamilyAttribute</code>,
     */
    public static OdfStyleFamily getFamily(String styleId) {
        OdfStyleFamily family = null;
        if (styleId.equals("paragraph")) {
            family = OdfStyleFamily.Paragraph;
        } else if (styleId.equals("character")) {
            family = OdfStyleFamily.Text;
        } else if (styleId.equals("table")) {
            family = OdfStyleFamily.Table;
        } else if (styleId.equals("row")) {
            family = OdfStyleFamily.TableRow;
        } else if (styleId.equals("cell")) {
            family = OdfStyleFamily.TableCell;
        } else if (styleId.equals("column")) {
            family = OdfStyleFamily.TableColumn;
        } else if (styleId.equals("drawing")) {
            family = OdfStyleFamily.Graphic;
        }
        return family;
    }

    /**
     * A multiple components can be represented by a single XML element
     *
     * @return the number of components the elements represents
     */
    public int repetition() {
        return 1;
    }
}
