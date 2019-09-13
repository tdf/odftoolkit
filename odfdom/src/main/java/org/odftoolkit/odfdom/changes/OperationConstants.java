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

import java.io.File;

public interface OperationConstants {

    // *** SUPPORTED USER OPERATIONS ***
    public static final String OP_PARAGRAPH = "addParagraph";
    public static final String OP_PARAGRAPH_SPLIT = "splitParagraph";
    public static final String OP_PARAGRAPH_MERGE = "mergeParagraph";
    public static final String OP_TEXT = "addText";
    public static final String OP_TAB = "addTab";
    public static final String OP_LINE_BREAK = "addLineBreak";
    public static final String OP_STYLE = "addStyle";
    public static final String OP_STYLE_CHANGE = "changeStyle";
    public static final String OP_STYLE_DELETE = "deleteStyle";
    public static final String OP_LIST_STYLE = "addListStyle";
    public static final String OP_FIELD = "addField";
    public static final String OP_FIELD_UPDATE = "updateField"; // only update, no creation, no range yet supported
    public static final String OP_DRAWING = "addDrawing";  // only text frame, no shape
    public static final String OP_DOCUMENT_LAYOUT = "documentLayout";
    public static final String OP_HEADER_FOOTER = "addHeaderFooter"; // 2DO: no editing test
    public static final String OP_HEADER_FOOTER_DELETE = "deleteHeaderFooterContent"; // 2DO: no editing test
    public static final String OP_NOTE = "addNote"; // 2DO: no note on text & a note can not be edited
    public static final String OP_NOTE_SELECTION = "addNoteSelection"; // see above..
    public static final String OP_FONT_DECL = "addFontDecl"; // no binary font data being added

    public static final String OP_TABLE = "addTable";
    public static final String OP_COLUMN = "addColumn";
    public static final String OP_COLUMNS_DELETE = "deleteColumns"; // 2DO: no editing test - pos=Table start,optional end of columns!
    public static final String OP_ROWS = "addRows";
    public static final String OP_CELLS = "addCells";

    public static final String OP_MOVE = "move"; // only single component can be moved
    public static final String OP_FORMAT = "format";
    public static final String OP_DELETE = "delete"; // works also for text

    // Operation only for testing
    public static final String OP_ERROR = "createError";

    /**
     * OPERATION PARAMETER (KEYS)*
     */
    public static final String OPK_OPERATIONS = "changes";
    public static final String OPK_NAME = "name";

    public static final String OPK_START = "start";
    public static final String OPK_END = "end";
    public static final String OPK_POSITION = "position";
    public static final String OPK_ID = "id";
    public static final String OPK_TYPE = "type";
    public static final String OPK_ATTRS = "attrs";
    public static final String OPK_STYLE_ID = "styleId";
    public static final String OPK_CONTEXT = "context";
    public static final String OPK_EDITOR = "editor";
    public static final String OPK_VERSION = "version";
    public static final String OPK_SHEET = "sheet";


    // *** MAXIMUM SIZE OF TABLE SUPPORT *** //
    /**
     * Number of cells, which are being dispatched as JSON until a table is
     * being omitted (replaced by placeholder). 0 represents no max.
     */
    static final Integer MAX_SUPPORTED_CELLS_NUMBER = 0;
    /**
     * Number of columns, which are being dispatched as JSON until a table is
     * being omitted (replaced by placeholder). 0 represents no max.
     */
    static final Integer MAX_SUPPORTED_COLUMNS_NUMBER = 0;
    /**
     * Number of rows, which are being dispatched as JSON until a table is being
     * omitted (replaced by placeholder). 0 represents no max.
     */
    static final Integer MAX_SUPPORTED_ROWS_NUMBER = 0;
    static final String CONFIG_MAX_TABLE_COLUMNS = "maxTableColumns";
    static final String CONFIG_MAX_TABLE_ROWS = "maxTableRows";
    static final String CONFIG_MAX_TABLE_CELLS = "maxTableCells";
    static final String CONFIG_MAX_SHEETS = "maxSheets";
    static final String CONFIG_DEBUG_OPERATIONS = "debugoperations";
    // Internal names to cache operations until considered if a table is too big
    static final String SHAPE = "Shape";
    static final String SHAPE_GROUP = "Group";
    static final String CELLS = "Cells";
    static final String TABLE = "Table";
    static final String EXCEEDEDTABLE = "ExceededTable";
    static final String ROWS = "Rows";
    static final String COLUMNS = "Columns";
    static final String PARAGRAPH = "Paragraph";
    static final String IMAGE = "Image";
    static final String LINE_BREAK = "LineBreak";
    static final String TAB = "Tab";
    static final String TEXT = "Text";
    static final String FIELD = "Field";
    static final String ATTRIBUTES = "Attributes";
    static final String FORMATROWS = "FormatRows";
    static final String FORMATCOLUMNS = "FormatColumns";
    static final String MERGECELLS = "MergeCells";
    static final String COMMENTRANGE = "CommentRange";
    static final String COMMENT = "Comment";

    // OUTPUT DIRECTORY NAME
    static final String OPERATION_OUTPUT_DIR = "operations" + File.separator;
    static final String ODT_SUFFIX = ".odt";
}
