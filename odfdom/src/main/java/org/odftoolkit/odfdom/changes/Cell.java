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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import static org.odftoolkit.odfdom.changes.JsonOperationConsumer.addStyle;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.number.DataStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.element.text.TextParagraphElementBase;
import org.odftoolkit.odfdom.dom.style.props.OdfStylePropertiesSet;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberCurrencyStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static org.odftoolkit.odfdom.changes.JsonOperationConsumer.addParagraph;
import static org.odftoolkit.odfdom.changes.JsonOperationConsumer.addText;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPK_STYLE_ID;

/**
 * A MultiCoomponent uses a single XML element to represent multiple components.
 * This container can be used for spreadsheet row and cell components using
 * repeated elements via an attribute.
 *
 * @author svante.schubertATgmail.com
 * @param <T>
 */
class Cell<T> extends Component {

    private static final String FORMULA_PREFIX = "of:";

    public Cell(OdfElement componentElement, Component parent) {
        super(componentElement, parent);
    }

    /**
     * A multiple components can be represented by a single XML element
     *
     * @return the number of components the elements represents
     */
    @Override
    public int repetition() {
        return mRootElement.getRepetition();
    }

// CELL ONLY
//	Map<String, Object> mInnerCellStyle = null;
//
//	/** The inner style of a cell will be temporary saved at the cell.
//	 Whenever the cell content is deleted, the style is being merged/applied to the cell style */
//	public Map<String, Object> getInternalCellStyle(){
//		return mInnerCellStyle;
//	}
//
//
//	/** The inner style of a cell will be temporary saved at the cell.
//	 Whenever the cell content is deleted, the style is being merged/applied to the cell style */
//	public void setInternalCellStyle(Map<String, Object> newStyles){
//		mInnerCellStyle = newStyles;
//	}
//
    /**
     * Adds the given component to the root element
     */
    @Override
    public void addChild(int index, Component c) {
        mRootElement.insert(c.getRootElement(), index);
// 2DO: Svante: ARE THE ABOVE AND THE BELOW EQUIVALENT?
//		OdfElement rootElement = c.getRootElement();
//		if (index >= 0) {
//			mRootElement.insertBefore(rootElement, ((OdfElement) mRootElement).receiveNode(index));
//		} else {
//			mRootElement.appendChild(rootElement);
//		}
    }

    /**
     * @return either a text node of size 1 or an element being the root element
     * of a component
     */
    @Override
    public Node getChildNode(int index) {
        return mRootElement.receiveNode(index);

    }

    /**
     * Removes a component from the text element container. Removes either an
     * element representing a component or text node of size 1
     */
    @Override
    public Node remove(int index) {
        Node removedNode = null;
        Node node = this.getChildNode(index);
        if (node != null) {
            removedNode = mRootElement.removeChild(node);
        }
        return removedNode;
    }

    /**
     * All children of the root element will be traversed. If it is a text node
     * the size is added, if it is an element and a component a size of one is
     * added, if it is a marker, for known text marker elements (text:span,
     * text:bookmark) the children are recursive checked
     *
     * @return the number of child components
     */
    @Override
    public int size() {
        return mRootElement.componentSize();
    }

    private static final String FLOAT = "float";
    private static final String STRING = "string";
    private static final String CURRENCY = "currency";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String PERCENTAGE = "percentage";
    private static final String BOOLEAN = "boolean";

    /**
     * Adding cell content: either as formula or paragraph and text content,
     * latter with default styles
     */
    public TableTableCellElement addCellStyleAndContent(Component rootComponent, Object value, JSONObject attrs) {
        // see if the cell is repeated
        TableTableCellElement cell = (TableTableCellElement) this.getRootElement();
        OdfFileDom ownerDoc = (OdfFileDom) rootComponent.getOwnerDocument();
        // save the URL as everyting else will be deleted
        String url = reuseCellHyperlink(cell, attrs);
        boolean setValueType = true;
        boolean isNumberValue = true;
        // exchanges the content if requested
        if (value != null) {
            cell.removeContent();
            // if there is new content..
            if (!value.equals(JSONObject.NULL)) {
                String valueString = value.toString();
                if (valueString.startsWith(Constants.EQUATION)) {
                    // How am I able to set the other values? What is the OOXML solution for this?
                    cell.setAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "office:value-type", FLOAT);
                    cell.setAttributeNS(OdfDocumentNamespace.TABLE.getUri(), "table:formula", FORMULA_PREFIX.concat(valueString));
                } else {
                    // insert a paragraph to store the text within..
                    TextParagraphElementBase newParagraph = addParagraph(this, 0, attrs);
                    if (url != null) {
                        attrs = addUrlToCharacterProps(attrs, url);
                    }
                    // if the formula was masked
                    if (value instanceof String && valueString.startsWith(Constants.APOSTROPHE_AND_EQUATION)) {
                        // cut the first apostrophe
                        valueString = valueString.substring(1);
                    }
                    // addChild the text & removes existing values
                    addText(newParagraph, 0, attrs, valueString);
                    if (value instanceof Integer || value instanceof Double || value instanceof Float) {
                        isNumberValue = true;
                        cell.setAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "office:value", valueString);
                    } else if (value instanceof String) {
                        cell.setAttributeNS(OdfDocumentNamespace.OFFICE.getUri(), "office:value-type", STRING);
                        setValueType = false;
                    }
                }
            }
        }
        if (attrs != null) {
            // Format: Adding Styles to the element
            addStyle(attrs, cell, ownerDoc);
            if (cell.hasChildNodes()) {
                NodeList children = cell.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child instanceof OdfElement) {
                        ((OdfElement) child).markText(0, Integer.MAX_VALUE - 1, attrs);
                    }
                }
            } else {
                if (url != null) {
                    TextPElement containerElement1 = new TextPElement(ownerDoc);
                    TextAElement containerElement2 = new TextAElement(ownerDoc);
                    containerElement2.setXlinkHrefAttribute(url);
                    cell.appendChild(containerElement1);
                    containerElement1.appendChild(containerElement2);
                }
            }
        }
        //value-type has to be set if
        // - a number is set replacing a previous string-type
        // - if a number is changed to a string (already done above ) or vice-versa
        // - if attributes have been set (that contain a number format or a new style)
        //
        if (setValueType) {
            String currentValueType = cell.getOfficeValueTypeAttribute();
            boolean isStringType = currentValueType != null && currentValueType.equals("String");
            boolean changedToNumber = isNumberValue && isStringType;
            boolean numberFormatChanged = false;
            if (attrs != null) {
                JSONObject cellAttrs = attrs.optJSONObject("cell");
                if (cellAttrs != null) {
                    numberFormatChanged = cellAttrs.has("formatCode");
                }
                if (!numberFormatChanged) {
                    String styleId = attrs.optString(OPK_STYLE_ID);
                    numberFormatChanged = styleId != null;
                }
            }
            if (currentValueType == null || changedToNumber || numberFormatChanged) {
                DataStyleElement dataStyle = cell.getOwnDataStyle();
                if (dataStyle != null) {
                    String valueType = "";
                    String currencySymbol = "";
                    if (dataStyle instanceof OdfNumberStyle) {
                        valueType = FLOAT;
                    } else if (dataStyle instanceof OdfNumberCurrencyStyle) {
                        currencySymbol = ((OdfNumberCurrencyStyle) dataStyle).getCurrencySymbolElement().getTextContent();
                        valueType = CURRENCY;
                    } else if (dataStyle instanceof NumberTextStyleElement) {
                        valueType = STRING;
                    } else if (dataStyle instanceof OdfNumberDateStyle) {
                        valueType = DATE;
                    } else if (dataStyle instanceof OdfNumberTimeStyle) {
                        valueType = TIME;
                    } else if (dataStyle instanceof OdfNumberPercentageStyle) {
                        valueType = PERCENTAGE;
                    } else if (dataStyle instanceof NumberBooleanStyleElement) {
                        valueType = BOOLEAN;
                    }
                    if (!valueType.isEmpty()) {
                        cell.setOfficeValueTypeAttribute(valueType);
                        cell.setCalcextValueTypeAttribute(valueType);
                        cell.setOfficeCurrencyAttribute(currencySymbol);
                        //make sure that an appropriate value is available:
                        if (value == null && cell.getOfficeValueAttribute() == null) {
                            String oldDateValue = cell.getOfficeDateValueAttribute();
                            String oldTimeValue = cell.getOfficeTimeValueAttribute();
                            Boolean oldBooleanValue = cell.getOfficeBooleanValueAttribute();
                            Double newValue = null;
                            if (oldDateValue != null) {
                                newValue = new Double(MapHelper.dateToDouble(oldDateValue));
                            } else if (oldTimeValue != null) {
                                newValue = new Double(MapHelper.timeToDouble(oldTimeValue));
                            } else if (oldBooleanValue != null) {
                                newValue = new Double(oldBooleanValue.booleanValue() ? 1 : 0);
                            }

                            if (newValue != null) {
                                cell.setOfficeValueAttribute(newValue);
                            }
                        }

                    }
                }
            }
        }
        return cell;
    }

    /**
     * To be able to reuse existing style on the full table, new cell hyperlinks
     * will be stored in the cell text style properties as @xlink:href attribute
     * and taken back when nothing new is set.
     */
    private static String reuseCellHyperlink(TableTableCellElement cell, JSONObject attrs) {
        String cellURL = null;
        if (attrs != null) { // apply style changes to the cell
            // apply new styles to the cell (modifying not overwriting)
            if (attrs.has("character")) {
                JSONObject charProps = attrs.optJSONObject("character");
                if (charProps != null) {
                    if (charProps.has("url") && !charProps.get("url").equals(JSONObject.NULL)) {
                        cellURL = charProps.optString("url");
                    } else if (charProps.has("url")) {
                        //removeAnchors();
                    }
                }
            }
        }
        // if there is no new hyperlink given, check for an existing cached (in the properties)
        if (cellURL == null || cellURL.isEmpty()) {
            // check if there is still one given at the cell
            OdfStyle autoStyle = cell.getAutomaticStyle();
            if (autoStyle != null) {
                OdfElement textProps = autoStyle.getPropertiesElement(OdfStylePropertiesSet.TextProperties);
                if (textProps != null && textProps.hasAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href")) {
                    cellURL = textProps.getAttributeNS(OdfDocumentNamespace.XLINK.getUri(), "href");
                }
            }
        }
        return cellURL;
    }

    private static JSONObject addUrlToCharacterProps(JSONObject attrs, String cellURL) {
        JSONObject charProps = null;
        if (cellURL != null && !cellURL.isEmpty()) {
            if (attrs == null) {
                attrs = new JSONObject();
            }
            if (!attrs.has("character")) {
                charProps = new JSONObject();
                try {
                    attrs.put("character", charProps);
                } catch (JSONException ex) {
                    Logger.getLogger(JsonOperationConsumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                charProps = attrs.optJSONObject("character");
            }
            try {
                charProps.put("url", cellURL);
            } catch (JSONException ex) {
                Logger.getLogger(JsonOperationConsumer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return attrs;
    }
}
