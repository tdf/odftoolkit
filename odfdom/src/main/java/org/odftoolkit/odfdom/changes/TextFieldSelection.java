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
import java.util.List;
import java.util.Map;
import static org.odftoolkit.odfdom.changes.OperationConstants.*;

import org.odftoolkit.odfdom.dom.element.text.TextUserFieldDeclElement;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;

/**
 *
 * @author svante.schubertATgmail.com
 */
public class TextFieldSelection extends TextSelection implements Comparable {

    private String mReplacementText;
    private final Map<String, Object> mAttrs = new HashMap<String, Object>();

    /**
     * Constructor.
     *
     * @param fieldElement the fieldElement of the Field element.
     * @param startPosition the startPosition of the Field element.
     */
    public TextFieldSelection(OdfElement fieldElement, List<Integer> startPosition, OdfOfficeAutomaticStyles autoStyles, Map<String, TextUserFieldDeclElement> userFieldDecls) {
        mSelectionElement = fieldElement;
        mStartPosition = startPosition;

        FieldMap currentMap = FieldMap.fieldMap.get(fieldElement.getLocalName());
        if (currentMap != null) {
            String attributeValue = null;
            if (currentMap.hasFixed() && !(attributeValue = mSelectionElement.getAttribute("text:fixed")).isEmpty()) {
                mAttrs.put("fixed", attributeValue.equals("true") ? "true" : "false");
            }
            if (currentMap.hasDateValue()) {
                String dateValue = mSelectionElement.getAttribute(currentMap.hasTimeStyle() ? "text:time-value" : "text:date-value");
                if (!dateValue.isEmpty()) {
                    mAttrs.put("dateValue", dateValue);
                }
                String dataStyleName = mSelectionElement.getAttribute("style:data-style-name");
                if (!dataStyleName.isEmpty()) {
                    OdfNumberDateStyle dateStyle = autoStyles.getDateStyle(dataStyleName);

                    OdfElement baseStyle = null;
                    if (dateStyle != null) {
                        baseStyle = dateStyle;
                        mAttrs.put("dateFormat", dateStyle.getFormat(true));
                    } else {
                        OdfNumberTimeStyle timeStyle = autoStyles.getTimeStyle(dataStyleName);
                        if (timeStyle != null) {
                            baseStyle = timeStyle;
                            mAttrs.put("dateFormat", timeStyle.getFormat(true));
                        }
                    }
                    if (baseStyle != null) {
                        String language = baseStyle.getAttribute("number:language");
                        String country = baseStyle.getAttribute("number:country");
                        if (!language.isEmpty()) {
                            if (!country.isEmpty()) {
                                language += "-";
                                language += country;
                            }
                            mAttrs.put("locale", language);
                        }
                    }
                }
            }
            if (currentMap.hasdbName() && !(attributeValue = mSelectionElement.getAttribute("text:database-name")).isEmpty()) {
                mAttrs.put("dbName", attributeValue);
            }
            if (currentMap.hasTableType() && !(attributeValue = mSelectionElement.getAttribute("text:table-type")).isEmpty()) {
                mAttrs.put("tableType", attributeValue);
            }
            if (currentMap.hasdbTable() && !(attributeValue = mSelectionElement.getAttribute("text:table-name")).isEmpty()) {
                mAttrs.put("dbTable", attributeValue);
            }
            if (currentMap.hasdbColumn() && !(attributeValue = mSelectionElement.getAttribute("text:column-name")).isEmpty()) {
                mAttrs.put("dbColumn", attributeValue);
            }
            if (currentMap.hasDisplay() && !(attributeValue = mSelectionElement.getAttribute("text:display")).isEmpty()) {
                mAttrs.put("display", attributeValue);
            }
            if (currentMap.hasRefFormat() && !(attributeValue = mSelectionElement.getAttribute("text:reference-format")).isEmpty()) {
                mAttrs.put("refFormat", attributeValue);
            }
            if (currentMap.hasRefName() && !(attributeValue = mSelectionElement.getAttribute("text:ref-name")).isEmpty()) {
                mAttrs.put("refName", attributeValue);
            }
            if (currentMap.hasOutlinelevel() && !(attributeValue = mSelectionElement.getAttribute("text:outline-level")).isEmpty()) {
                mAttrs.put("outlineLevel", attributeValue);
            }
            if (currentMap.hasPageNumFormat() && !(attributeValue = mSelectionElement.getAttribute("style:num-format")).isEmpty()) {
                mAttrs.put("pageNumFormat", attributeValue);
            }
            if (currentMap.hasNumLetterSync() && !(attributeValue = mSelectionElement.getAttribute("style:num-letter-sync")).isEmpty()) {
                mAttrs.put("numLetterSync", attributeValue);
            }
            if (currentMap.hasCondition() && !(attributeValue = mSelectionElement.getAttribute("text:condition")).isEmpty()) {
                mAttrs.put("condition", attributeValue);
            }
            if (currentMap.hasCurrentValue() && !(attributeValue = mSelectionElement.getAttribute("text:current-value")).isEmpty()) {
                mAttrs.put("currentValue", attributeValue);
            }
            if (currentMap.hasFalseValue() && !(attributeValue = mSelectionElement.getAttribute("text:string-value-if-false")).isEmpty()) {
                mAttrs.put("falseValue", attributeValue);
            }
            if (currentMap.hasTrueValue() && !(attributeValue = mSelectionElement.getAttribute("text:string-value-if-true")).isEmpty()) {
                mAttrs.put("trueValue", attributeValue);
            }
            if (currentMap.hasConnectionName() && !(attributeValue = mSelectionElement.getAttribute("text:connection-name")).isEmpty()) {
                mAttrs.put("connectionName", attributeValue);
            }
            if (currentMap.hasDuration() && !(attributeValue = mSelectionElement.getAttribute("text-duration")).isEmpty()) {
                mAttrs.put("duration", attributeValue);
            }
            if (currentMap.hasName() && !(attributeValue = mSelectionElement.getAttribute("text:name")).isEmpty()) {
                mAttrs.put("name", attributeValue);
            }
            if (currentMap.hasBoolValue() && !(attributeValue = mSelectionElement.getAttribute("office:boolean-value")).isEmpty()) {
                mAttrs.put("boolValue", attributeValue);
            }
            if (currentMap.hasCurrency() && !(attributeValue = mSelectionElement.getAttribute("office:currency")).isEmpty()) {
                mAttrs.put("currency", attributeValue);
            }
            if (currentMap.hasStringValue() && !(attributeValue = mSelectionElement.getAttribute("office:value")).isEmpty()) {
                mAttrs.put("stringValue", attributeValue);
            }
            if (currentMap.hasTimeValue() && !(attributeValue = mSelectionElement.getAttribute("text:time-value")).isEmpty()) {
                mAttrs.put("timeValue", attributeValue);
            }
            if (currentMap.hasTValue() && !(attributeValue = mSelectionElement.getAttribute("text:value")).isEmpty()) {
                mAttrs.put("value", attributeValue);
            }
            if (currentMap.hasOValue() && !(attributeValue = mSelectionElement.getAttribute("office:value")).isEmpty()) {
                mAttrs.put("value", attributeValue);
            }
            if (currentMap.hasValueType()) {
                if (fieldElement.getLocalName().equals("user-field-get")) {
                    TextUserFieldDeclElement fieldDecl = userFieldDecls.get(mAttrs.get("name"));
                    if (fieldDecl != null && !(attributeValue = fieldDecl.getAttribute("office:value-type")).isEmpty()) {
                        mAttrs.put("valueType", attributeValue);
                    }
                } else if (!(attributeValue = mSelectionElement.getAttribute("office:value-type")).isEmpty()) {
                    mAttrs.put("valueType", attributeValue);
                }
            }
            if (currentMap.hasFormula() && !(attributeValue = mSelectionElement.getAttribute("text:formula")).isEmpty()) {
                mAttrs.put("formula", attributeValue);
            }
            if (currentMap.hasIsHidden() && !(attributeValue = mSelectionElement.getAttribute("xml:id")).isEmpty()) {
                mAttrs.put("isHidden", attributeValue);
            }
            if (currentMap.hasId() && !(attributeValue = mSelectionElement.getAttribute("xml:id")).isEmpty()) {
                mAttrs.put(OPK_ID, attributeValue);
            }
            if (currentMap.hasDescription() && !(attributeValue = mSelectionElement.getAttribute("text:description")).isEmpty()) {
                mAttrs.put("description", attributeValue);
            }
            if (currentMap.hasActive() && !(attributeValue = mSelectionElement.getAttribute("text:active")).isEmpty()) {
                mAttrs.put("active", attributeValue);
            }
            if (currentMap.hasHref() && !(attributeValue = mSelectionElement.getAttribute("xlink:href")).isEmpty()) {
                mAttrs.put("href", attributeValue);
            }
            if (currentMap.hasPlaceHolderType() && !(attributeValue = mSelectionElement.getAttribute("text:placeholder-type")).isEmpty()) {
                mAttrs.put("placeHolderType", attributeValue);
            }
            if (currentMap.hasKind() && !(attributeValue = mSelectionElement.getAttribute("text:kind")).isEmpty()) {
                mAttrs.put("kind", attributeValue);
            }
            if (currentMap.hasLanguage() && !(attributeValue = mSelectionElement.getAttribute("script:language")).isEmpty()) {
                mAttrs.put("language", attributeValue);
            }
            if (currentMap.hasLinkType() && !(attributeValue = mSelectionElement.getAttribute("xlink:type")).isEmpty()) {
                mAttrs.put("linkType", attributeValue);
            }
            if (currentMap.hasNumFormat() && !(attributeValue = mSelectionElement.getAttribute("style:num-format")).isEmpty()) {
                mAttrs.put("numFormat", attributeValue);
            }
            if (currentMap.hasPageAdjust() && !(attributeValue = mSelectionElement.getAttribute("text:page-adjust")).isEmpty()) {
                mAttrs.put("pageAdjust", attributeValue);
            }
            if (currentMap.hasRowNumber() && !(attributeValue = mSelectionElement.getAttribute("text:row-number")).isEmpty()) {
                mAttrs.put("rowNumber", attributeValue);
            }
        }
    }

    /**
     * Constructor.
     *
     * @param fieldElement the fieldElement of the Field element.
     * @param startPosition the startPosition of the Field element.
     */
    public TextFieldSelection(OdfElement fieldElement, List<Integer> startPosition, String replacementText) {
        mSelectionElement = fieldElement;
        mStartPosition = startPosition;
        mReplacementText = replacementText;
    }

    /**
     * Constructor.
     *
     * @param fieldElement the fieldElement of the Field element.
     * @param startPosition the startPosition of the Field element.
     */
    TextFieldSelection(OdfElement fieldElement, List<Integer> startPosition, List<Integer> endPosition) {
        mSelectionElement = fieldElement;
        mStartPosition = startPosition;
        mEndPosition = endPosition;
    }

    public String getReplacementText() {
        return mReplacementText;
    }

    public Map<String, Object> getAttributes() {
        return mAttrs;
    }

    @Override
    public String toString() {
        return mStartPosition.toString() + "-URL" + mUrl + "-" + mEndPosition.toString() + mSelectionElement.toString();
    }

}
