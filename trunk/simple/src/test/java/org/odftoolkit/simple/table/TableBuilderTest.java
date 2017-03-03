/*
 * Copyright 2013 The Apache Software Foundation.
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
package org.odftoolkit.simple.table;

import org.junit.Test;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.table.Table.TableBuilder;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Florian Hopf, fhopf@apache.org
 */
public class TableBuilderTest {
    
    @Test
    public void testCreateTable() throws Exception {
        TextDocument textDoc = TextDocument.newTextDocument();
        TableBuilder tableBuilder = textDoc.getTableBuilder();
        Table table = tableBuilder.newTable(4, 3, 2, 1);
        assertEquals(4, table.getRowCount());
        assertEquals(3, table.getColumnCount());
        assertEquals(2, table.getHeaderRowCount());
        assertEquals(1, table.getHeaderColumnCount());
        assertNotNull(table.getCellByPosition(1, 1));
    }
    
    @Test
    public void testAppendRow() throws Exception {
        TextDocument textDoc = TextDocument.newTextDocument();
        TableBuilder tableBuilder = textDoc.getTableBuilder();
        Table table = tableBuilder.newTable(4, 3, 2, 1);
        assertEquals(4, table.getRowCount());
        Row row = table.appendRow();
        assertNotNull(row);
        assertEquals(5, table.getRowCount());
        assertNotNull(table.getCellByPosition(1, 1));
    }
    
    @Test
    public void test363() throws Exception {
        TextDocument document = TextDocument.newTextDocument();
        TableBuilder tableBuilder = document.getTableBuilder();
        Table docBuiltTable1 = tableBuilder.newTable(2, 5, 1, 5);
        assertNotNull(docBuiltTable1.getCellByPosition(1,1));
        Table docBuiltTable2 = tableBuilder.newTable(1, 5, 1, 5);
        docBuiltTable2.appendRow();
        assertNotNull(docBuiltTable2.getCellByPosition(1,1));
    }
}
