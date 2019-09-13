/*
 * Copyright 2012 The lApache Software Foundation.
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
import java.util.Map;

class FieldMap {

    public static final long PROP_FIXED = 1;
    public static final long PROP_DATEVALUE = 1 << 1;
    public static final long PROP_TIMESTYLE = 1 << 2;
    public static final long PROP_DATEFORMAT = 1 << 3;
    public static final long PROP_LOCALE = 1 << 4;
    public static final long PROP_DBNAME = 1 << 5;
    public static final long PROP_DBTABLE = 1 << 6;
    public static final long PROP_TABLETYPE = 1 << 7;
    public static final long PROP_DBCOLUMN = 1 << 8;
    public static final long PROP_DISPLAY = 1 << 9;
    public static final long PROP_REFFORMAT = 1 << 10;
    public static final long PROP_REFNAME = 1 << 11;
    public static final long PROP_OUTLINELEVEL = 1 << 12;
    public static final long PROP_PAGENUMFORMAT = 1 << 13;
    public static final long PROP_NUMLETTERSYNC = 1 << 14;
    public static final long PROP_CONDITION = 1 << 15;
    public static final long PROP_CURRENTVALUE = 1 << 16;
    public static final long PROP_FALSEVALUE = 1 << 17;
    public static final long PROP_TRUEVALUE = 1 << 18;
    public static final long PROP_T_VALUE = 1 << 19; //text:value
    public static final long PROP_CONNECTIONNAME = 1 << 20;
    public static final long PROP_DURATION = 1 << 21;
    public static final long PROP_NAME = 1 << 22;
    public static final long PROP_BOOLVALUE = 1 << 23;
    public static final long PROP_CURRENCY = 1 << 24;
    public static final long PROP_STRINGVALUE = 1 << 25;
    public static final long PROP_TIMEVALUE = 1 << 26;
    public static final long PROP_VALUETYPE = 1 << 27;
    public static final long PROP_FORMULA = 1 << 28;
    public static final long PROP_ISHIDDEN = 1 << 29;
    public static final long PROP_O_VALUE = 1 << 30; //office:value
    public static final long PROP_ID = 1 << 31;
    public static final long PROP_DESCRIPTION = 1 << 32;
    public static final long PROP_ACTIVE = 1 << 33;
    public static final long PROP_HREF = 1 << 34;
    public static final long PROP_PLACEHOLDERTYPE = 1 << 35;
    public static final long PROP_KIND = 1 << 36;
    public static final long PROP_LANGUAGE = 1 << 37;
    public static final long PROP_LINKTYPE = 1 << 38;
    public static final long PROP_NUMFORMAT = 1 << 39;
    public static final long PROP_PAGEADJUST = 1 << 40;
    public static final long PROP_ROWNUMBER = 1 << 41;
//    public static final long  = 1<< 42;

    public String type;
    private final String className;
    private final long propertyFlags;
    public static Map<String, FieldMap> fieldMap = createMap();

    public FieldMap(String t, String c, long flags) {
        type = t;
        className = c;
        propertyFlags = flags;
    }

    public String getClassName() {
        return "org.odftoolkit.odfdom.dom.element.text." + className;
    }

    public boolean hasFixed() {
        return (propertyFlags & PROP_FIXED) != 0;
    }

    public boolean hasDateValue() {
        return (propertyFlags & PROP_DATEVALUE) != 0;
    }

    public boolean hasTimeStyle() {
        return (propertyFlags & PROP_TIMESTYLE) != 0;
    }

    public boolean hasDateFormat() {
        return (propertyFlags & PROP_DATEFORMAT) != 0;
    }

    public boolean hasLocale() {
        return (propertyFlags & PROP_LOCALE) != 0;
    }

    public boolean hasdbName() {
        return (propertyFlags & PROP_DBNAME) != 0;
    }

    public boolean hasdbTable() {
        return (propertyFlags & PROP_DBTABLE) != 0;
    }

    public boolean hasTableType() {
        return (propertyFlags & PROP_TABLETYPE) != 0;
    }

    public boolean hasdbColumn() {
        return (propertyFlags & PROP_DBCOLUMN) != 0;
    }

    public boolean hasDisplay() {
        return (propertyFlags & PROP_DISPLAY) != 0;
    }

    public boolean hasRefFormat() {
        return (propertyFlags & PROP_REFFORMAT) != 0;
    }

    public boolean hasRefName() {
        return (propertyFlags & PROP_REFNAME) != 0;
    }

    public boolean hasOutlinelevel() {
        return (propertyFlags & PROP_OUTLINELEVEL) != 0;
    }

    public boolean hasPageNumFormat() {
        return (propertyFlags & PROP_PAGENUMFORMAT) != 0;
    }

    public boolean hasNumLetterSync() {
        return (propertyFlags & PROP_NUMLETTERSYNC) != 0;
    }

    public boolean hasCondition() {
        return (propertyFlags & PROP_CONDITION) != 0;
    }

    public boolean hasCurrentValue() {
        return (propertyFlags & PROP_CURRENTVALUE) != 0;
    }

    public boolean hasFalseValue() {
        return (propertyFlags & PROP_FALSEVALUE) != 0;
    }

    public boolean hasTrueValue() {
        return (propertyFlags & PROP_TRUEVALUE) != 0;
    }

    public boolean hasConnectionName() {
        return (propertyFlags & PROP_CONNECTIONNAME) != 0;
    }

    public boolean hasDuration() {
        return (propertyFlags & PROP_DURATION) != 0;
    }

    public boolean hasName() {
        return (propertyFlags & PROP_NAME) != 0;
    }

    public boolean hasBoolValue() {
        return (propertyFlags & PROP_BOOLVALUE) != 0;
    }

    public boolean hasCurrency() {
        return (propertyFlags & PROP_CURRENCY) != 0;
    }

    public boolean hasStringValue() {
        return (propertyFlags & PROP_STRINGVALUE) != 0;
    }

    public boolean hasTimeValue() {
        return (propertyFlags & PROP_TIMEVALUE) != 0;
    }

    public boolean hasTValue() {
        return (propertyFlags & PROP_T_VALUE) != 0;
    }

    public boolean hasOValue() {
        return (propertyFlags & PROP_O_VALUE) != 0;
    }

    public boolean hasValueType() {
        return (propertyFlags & PROP_VALUETYPE) != 0;
    }

    public boolean hasFormula() {
        return (propertyFlags & PROP_FORMULA) != 0;
    }

    public boolean hasIsHidden() {
        return (propertyFlags & PROP_ISHIDDEN) != 0;
    }

    public boolean hasId() {
        return (propertyFlags & PROP_ID) != 0;
    }

    public boolean hasDescription() {
        return (propertyFlags & PROP_DESCRIPTION) != 0;
    }

    public boolean hasActive() {
        return (propertyFlags & PROP_ACTIVE) != 0;
    }

    public boolean hasHref() {
        return (propertyFlags & PROP_HREF) != 0;
    }

    public boolean hasPlaceHolderType() {
        return (propertyFlags & PROP_PLACEHOLDERTYPE) != 0;
    }

    public boolean hasKind() {
        return (propertyFlags & PROP_KIND) != 0;
    }

    public boolean hasLanguage() {
        return (propertyFlags & PROP_LANGUAGE) != 0;
    }

    public boolean hasLinkType() {
        return (propertyFlags & PROP_LINKTYPE) != 0;
    }

    public boolean hasNumFormat() {
        return (propertyFlags & PROP_NUMFORMAT) != 0;
    }

    public boolean hasPageAdjust() {
        return (propertyFlags & PROP_PAGEADJUST) != 0;
    }

    public boolean hasRowNumber() {
        return (propertyFlags & PROP_ROWNUMBER) != 0;
    }

    private static Map<String, FieldMap> createMap() {
        Map<String, FieldMap> map = new HashMap<String, FieldMap>();
        map.put("author-initials", new FieldMap("authorinitials", "TextAuthorInitialsElement", PROP_FIXED));
        map.put("author-name", new FieldMap("author-name", "TextAuthorNameElement", PROP_FIXED));
        map.put("bookmark-ref", new FieldMap("bookmark-ref", "TextBookmarkRefElement", PROP_REFFORMAT | PROP_REFNAME));
        map.put("chapter", new FieldMap("chapter", "TextChapterElement", PROP_DISPLAY | PROP_OUTLINELEVEL));
        map.put("character-count", new FieldMap("character-count", "TextCharacterCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("conditional-text", new FieldMap("conditional-text", "TextConditionalTextElement", PROP_CONDITION | PROP_CURRENTVALUE | PROP_FALSEVALUE | PROP_TRUEVALUE));
        map.put("creation-date", new FieldMap("creation-date", "TextCreationDateElement", PROP_FIXED | PROP_DATEVALUE | PROP_DATEFORMAT));
        map.put("creation-time", new FieldMap("creation-time", "TextCreationTimeElement", PROP_FIXED | PROP_DATEVALUE | PROP_DATEFORMAT | PROP_TIMESTYLE));
        map.put("creator", new FieldMap("creator", "TextCreatorElement", PROP_FIXED));
        map.put("database-display", new FieldMap("database-display", "TextDatabaseDisplayElement", PROP_DBTABLE | PROP_DBNAME | PROP_DBCOLUMN));
        map.put("database-name", new FieldMap("database-name", "TextDatabaseNameElement", PROP_DBTABLE | PROP_DBNAME));
        map.put("database-row-number", new FieldMap("database-row-number", "TextDatabaseRowNumberElement", PROP_T_VALUE | PROP_DBTABLE | PROP_DBNAME));
        map.put("database-row-select", new FieldMap("database-row-select", "TextDatabaseRowSelectElement", PROP_CONDITION | PROP_DBTABLE | PROP_DBNAME | PROP_ROWNUMBER | PROP_TABLETYPE));
        map.put("date", new FieldMap("date", "TextDateElement", PROP_FIXED | PROP_DATEVALUE | PROP_DATEFORMAT));
        map.put("dde-connection", new FieldMap("dde-connection", "TextDdeConnectionElement", PROP_CONNECTIONNAME));
        map.put("description", new FieldMap("description", "TextDescriptionElement", PROP_FIXED));
        map.put("editing-cycles", new FieldMap("editing-cycles", "TextEditingCyclesElement", PROP_FIXED));
        map.put("editing-duration", new FieldMap("editing-duration", "TextEditingDurationElement", PROP_FIXED | PROP_DURATION | PROP_DATEFORMAT));
        map.put("execute-macro", new FieldMap("execute-macro", "TextExecuteMacroElement", PROP_NAME));
        map.put("expression", new FieldMap("expression", "TextExpressionElement", PROP_BOOLVALUE | PROP_CURRENCY | PROP_DATEVALUE | PROP_STRINGVALUE | PROP_TIMEVALUE | PROP_O_VALUE | PROP_VALUETYPE | PROP_DATEFORMAT | PROP_DISPLAY | PROP_FORMULA));
        map.put("file-name", new FieldMap("file-name", "TextFileNameElement", PROP_DISPLAY | PROP_FIXED));
        map.put("hidden-paragraph", new FieldMap("hidden-paragraph", "TextHiddenParagraphElement", PROP_CONDITION | PROP_ISHIDDEN));
        map.put("hidden-text", new FieldMap("hidden-text", "TextHiddenTextElement", PROP_CONDITION | PROP_ISHIDDEN | PROP_STRINGVALUE));
        map.put("image-count", new FieldMap("image-count", "TextImageCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("initial-creator", new FieldMap("initial-creator", "TextInitialCreatorElement", PROP_FIXED));
        map.put("keywords", new FieldMap("keywords", "TextKeywordsElement", 0));
        map.put("measure", new FieldMap("measure", "TextMeasureElement", PROP_KIND));
        map.put("meta-field", new FieldMap("meta-field", "TextMetaFieldElement", PROP_DATEFORMAT | PROP_ID));
        map.put("modification-date", new FieldMap("modification-date", "TextModificationDateElement", PROP_FIXED | PROP_DATEVALUE | PROP_DATEFORMAT));
        map.put("modification-time", new FieldMap("modification-time", "TextModificationTimeElement", PROP_FIXED | PROP_DATEVALUE | PROP_DATEFORMAT));
        map.put("note-ref", new FieldMap("note-ref", "TextNoteRefElement", PROP_REFFORMAT | PROP_REFNAME));
        map.put("object-count", new FieldMap("object-count", "TextObjectCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("page-continuation", new FieldMap("page-continuation", "TextPageContinuationElement", 0));
        map.put("page-count", new FieldMap("page-count", "TextPageCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("page-number", new FieldMap("page-number", "TextPageNumberElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("page-variable-get", new FieldMap("page-variable-get", "TextPageVariableGetElement", PROP_NUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("page-variable-set", new FieldMap("page-variable-set", "TextPageVariableSetElement", PROP_PAGEADJUST | PROP_ACTIVE));
        map.put("paragraph-count", new FieldMap("paragraph-count", "TextParagraphCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("placeholder", new FieldMap("placeholder", "TextPlaceholderElement", PROP_DESCRIPTION | PROP_PLACEHOLDERTYPE));
        map.put("print-date", new FieldMap("print-date", "TextPrintDateElement", PROP_FIXED | PROP_DATEVALUE | PROP_DATEFORMAT));
        map.put("print-time", new FieldMap("print-time", "TextPrintTimeElement", PROP_FIXED | PROP_TIMESTYLE | PROP_DATEVALUE | PROP_DATEFORMAT));
        map.put("printed-by", new FieldMap("printed-by", "TextPrintedByElement", PROP_FIXED));
        map.put("reference-ref", new FieldMap("reference-ref", "TextReferenceRefElement", PROP_REFFORMAT | PROP_REFNAME));
        map.put("script", new FieldMap("script", "TextScriptElement", PROP_LANGUAGE | PROP_HREF | PROP_LINKTYPE));
        map.put("sender-city", new FieldMap("sender-city", "TextSenderCityElement", 0));
        map.put("sender-company", new FieldMap("sender-company", "TextSenderCompanyElement", 0));
        map.put("sender-country", new FieldMap("sender-country", "TextSenderCountryElement", 0));
        map.put("sender-email", new FieldMap("sender-email", "TextSenderEmailElement", 0));
        map.put("sender-fax", new FieldMap("sender-fax", "TextSenderFaxElement", 0));
        map.put("sender-firstname", new FieldMap("sender-firstname", "TextSenderFirstnameElement", 0));
        map.put("sender-initials", new FieldMap("sender-initials", "TextSenderInitialsElement", 0));
        map.put("sender-lastname", new FieldMap("sender-lastname", "TextSenderLastnameElement", 0));
        map.put("sender-phone-private", new FieldMap("sender-phone-private", "TextSenderPhonePrivateElement", 0));
        map.put("sender-phone-work", new FieldMap("sender-phone-work", "TextSenderPhoneWorkElement", 0));
        map.put("sender-position", new FieldMap("sender-position", "TextSenderPositionElement", 0));
        map.put("sender-postal-code", new FieldMap("sender-postal-code", "TextSenderPostalCodeElement", 0));
        map.put("sender-state-or-province", new FieldMap("sender-state-or-province", "TextSenderStateOrProvinceElement", 0));
        map.put("sender-street", new FieldMap("sender-street", "TextSenderStreetElement", 0));
        map.put("sender-title", new FieldMap("sender-title", "TextSenderTitleElement", 0));
        map.put("sequence-ref", new FieldMap("sequence-ref", "TextSequenceRefElement", PROP_REFFORMAT | PROP_REFNAME));
        map.put("sequence", new FieldMap("sequence", "TextSequenceElement", PROP_DATEFORMAT | PROP_NUMLETTERSYNC | PROP_FORMULA | PROP_NAME | PROP_REFNAME));
        map.put("sheet-name", new FieldMap("sheet-name", "TextSheetNameElement", 0));
        map.put("subject", new FieldMap("subject", "TextSubjectElement", PROP_FIXED));
        map.put("table-count", new FieldMap("table-count", "TextTableCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));
        map.put("template-name", new FieldMap("template-name", "TextTemplateNameElement", PROP_DISPLAY));
        map.put("text-input", new FieldMap("text-input", "TextTextInputElement", PROP_DESCRIPTION));
        map.put("time", new FieldMap("time", "TextTimeElement", PROP_FIXED | PROP_DATEVALUE | PROP_TIMESTYLE | PROP_DATEFORMAT));
        map.put("title", new FieldMap("title", "TextTitleElement", PROP_FIXED));
        map.put("user-defined", new FieldMap("user-defined", "TextUserDefinedElement", PROP_BOOLVALUE | PROP_CURRENCY | PROP_DATEVALUE | PROP_STRINGVALUE | PROP_TIMEVALUE | PROP_O_VALUE | PROP_DATEFORMAT | PROP_FIXED | PROP_NAME));
        map.put("user-field-get", new FieldMap("user-field-get", "TextUserFieldGetElement", PROP_DATEFORMAT | PROP_DISPLAY | PROP_NAME | PROP_VALUETYPE));
        map.put("user-field-input", new FieldMap("user-field-input", "TextUserFieldInputElement", PROP_DATEFORMAT | PROP_DESCRIPTION | PROP_NAME));
        map.put("variable-get", new FieldMap("variable-get", "TextVariableGetElement", PROP_DATEFORMAT | PROP_DISPLAY));
        map.put("variable-input", new FieldMap("variable-input", "TextVariableInputElement", PROP_BOOLVALUE | PROP_DATEFORMAT | PROP_DISPLAY | PROP_DESCRIPTION | PROP_NAME));
        map.put("variable-set", new FieldMap("variable-set", "TextVariableSetElement", PROP_BOOLVALUE | PROP_CURRENCY | PROP_DATEVALUE | PROP_STRINGVALUE | PROP_TIMEVALUE | PROP_O_VALUE | PROP_VALUETYPE | PROP_DATEFORMAT | PROP_DISPLAY | PROP_FORMULA | PROP_NAME));
        map.put("word-count", new FieldMap("word-count", "TextWordCountElement", PROP_PAGENUMFORMAT | PROP_NUMLETTERSYNC));

        return map;
    }
}
