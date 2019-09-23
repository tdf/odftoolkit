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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.odftoolkit.odfdom.changes.OperationConstants.OPERATION_OUTPUT_DIR;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

/**
 * Loads a document with tables and gathers its operations. Gathered operations
 * will be applied to an empty text document. The changed text document will be
 * saved and reloaded. New gathered operations will be compared with the
 * original ones, expected to be identical!
 *
 * @author svanteschubert
 */
public class EditingRegressionTest extends RoundtripTestHelper {

    private static final Logger LOG = Logger.getLogger(EditingRegressionTest.class.getName());

    public EditingRegressionTest() {
    }
    //private static final String OUTPUT_DIRECTORY = "tests-by-edit" + File.separatorChar;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        RoundtripTestHelper.setUpBeforeClass();
    }



    @Test
    @Ignore // Ignore these big performance tests for now
    /**
     * The document can be round-tripped in a single test, but not with all the
     * other files on the disc (memory problem of test or toolkit?)
     */
    public void BigDocTest() {
        final String SOURCE_FILE_NAME_TRUNC = "performance"  + File.separator + "OpenDocument-v1.2-os-part1";
        // create the test directories for the "performance" subdirectory
        new File(ResourceUtilities.getTestInputFolder() + OPERATION_OUTPUT_DIR + "performance").mkdirs();
        new File(ResourceUtilities.getTestOutputFolder() + OPERATION_OUTPUT_DIR + "performance").mkdirs();

        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), null);
    }


    @Test
    /**
     * Creating some JSON by string. Serializing the JSON as string and recreating it. The string should keep the same!
     */
    public void roundtripJSON() {
        final String INPUT_STRING = "2Hello&#x0a; ää http://heise.de \" \t  c:\\path\\dummy";
        // NOTE: The sting is Java encoded/masked so a lot of backslashes appear in the String below, which do not in the output
        final String OUTPUT_STRING = "{\"ops\":[{\"key2\":\"2Hello&#x0a; ää http://heise.de \\\" \\t  c:\\\\path\\\\dummy\"}]}";
        JSONObject o1 = new JSONObject();
        JSONArray a1 = new JSONArray();
        o1.put("ops", a1);
        JSONObject o2 = new JSONObject();
        o2.put("key2", INPUT_STRING);
        a1.put(o2);
        String out1 = o1.toString();
        System.out.println("Serialized JSON1: out1:\n" +  out1);
        ResourceUtilities.saveStringToFile(ResourceUtilities.getTestOutputFile("svante"), out1);

        JSONObject ro1 = new JSONObject(out1);
        String out2 = ro1.toString();
        System.out.println("Serialized JSON2: out2:\n" +  out2);
        ResourceUtilities.saveStringToFile(ResourceUtilities.getTestOutputFile("svanteRoundtrip"), out2);

        String newOps = JsonOperationNormalizer.asString(ro1, Boolean.TRUE);
        System.out.println("Serialized JSON3: norm:\n" +  newOps);
        ResourceUtilities.saveStringToFile(ResourceUtilities.getTestOutputFile("svanteRoundtrip"), newOps);
        Assert.assertTrue(out1.equals(out2));
        Assert.assertTrue(out2.equals(newOps));
        Assert.assertTrue(newOps.toString().equals(OUTPUT_STRING));
    }


    @Test
    /**
     * Adding a table with two rows, deleting the first
     */
    public void rowDeletionTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT with table -copy&paste a table: after saving=> inserted table is lost
     */
    public void copyAndPasteTableTest() {
        final String SOURCE_FILE_NAME_TRUNC = "simple_table";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Table cell component not found, cell content is lost
     */
    public void addingRepeatedRowsForTextTableTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Webeditor Clipboard Docx to ODT: => ERROR: nullorg.json.JSONException:
     * JSONObject["fallbackValue"] not found
     */
    public void copyPasteOOXMLTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }


    @Test
    /**
       String firstEditOperations = "["
                + "{\"text\":\"1\",\"start\":[0,0,2,0,0],\"name\":\"" + OP_TEXT + "\"},"
                + "{\"start\":[0,0,2,0,1],\"name\":\"" + OP_PARAGRAPH_SPLIT + "\"},"
                + "{\"text\":\"2\",\"start\":[0,0,2,1,0],\"name\":\"" + OP_TEXT + "\"},"
                + "{\"start\":[0,0,2,1,1],\"name\":\"" + OP_PARAGRAPH_SPLIT + "\"},"
                /*
             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7">1</text:p>
             </text:list-item>
             <text:list-item>
             <text:p text:style-name="aaaf1f7">2</text:p>
             </text:list-item>
             <text:list-item>
             <text:p text:style-name="aaaf1f7"/>
             </text:list-item>
             </text:list>

                + "{\"start\":[0,0,2,1],\"attrs\":{\"styleId\":null,\"paragraph\":{\"alignment\":null,\"lineHeight\":null,\"fillColor\":null,\"marginTop\":null,\"outlineLevel\":null,\"nextStyleId\":null,\"listStyleId\":null,\"borderBottom\":null,\"listLevel\":null,\"contextualSpacing\":null,\"borderLeft\":null,\"tabStops\":null,\"marginBottom\":null,\"listLabelHidden\":null,\"listStartValue\":null,\"indentRight\":null,\"indentLeft\":null,\"borderTop\":null,\"borderInside\":null,\"indentFirstLine\":null,\"borderRight\":null},\"character\":{\"vertAlign\":null,\"fontName\":null,\"bold\":null,\"strike\":null,\"fillColor\":null,\"color\":null,\"underline\":null,\"italic\":null,\"language\":null,\"fontSize\":null,\"url\":null}},\"name\": \"" + OP_FORMAT + "\"},"

             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7">1</text:p>
             </text:list-item>
             </text:list>
             <text:p text:style-name="a81c9de">2</text:p>
             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7"/>
             </text:list-item>
             </text:list>

                + "{\"text\":\"a\",\"start\":[0,0,2,2,0],\"name\":\"" + OP_TEXT + "\"},"

             * WRONG:
             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7">1</text:p>
             </text:list-item>
             </text:list>
             <text:p text:style-name="a81c9de">a2</text:p>
             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7"/>
             </text:list-item>
             </text:list>


             * RIGHT:
             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7">1</text:p>
             </text:list-item>
             </text:list>
             <text:p text:style-name="a81c9de">2</text:p>
             <text:list text:continue-numbering="true" text:style-name="L1">
             <text:list-item>
             <text:p text:style-name="aaaf1f7">a</text:p>
             </text:list-item>
             </text:list>
                + "]";
     */
    public void listSplitWithinCellTest() {
        final String SOURCE_FILE_NAME_TRUNC = "listsInTable";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Copy&Paste a table: after re-edit, the borderstyle is lost
     */
    public void copyPasteTableTest() {
        final String SOURCE_FILE_NAME_TRUNC = "listsInTable";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Embedding a table within the list of another table
     */
    public void deleteListPropertiesTest() {
        final String SOURCE_FILE_NAME_TRUNC = "listsInTable";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Embedding a table within the list of another table
     */
    public void insertNestedTableTest() {
        final String SOURCE_FILE_NAME_TRUNC = "crazyTable";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    /**
     * ODT - Downloading odt: values are not shown in LO
     */
    @Test
    public void changeTableContentTest() {
        final String SOURCE_FILE_NAME_TRUNC = "repeatedColumns_AO401";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Table: After saving the row sequences are mixed up
     */
    public void insertTableRowTest() {
        final String SOURCE_FILE_NAME_TRUNC = "repeatedColumns_AO401";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Change table width (ODT): after downloading/editing this odt the small
     * column is lost
     */
    public void setTableColumnWidthTest() {
        final String SOURCE_FILE_NAME_TRUNC = "TableWidth";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Insert Row and Column should not show content (no longer cloning XML
     * attribute
     *
     * @office:value)
     */
    public void editComplexTableTest() {
        final String SOURCE_FILE_NAME_TRUNC = "repeatedColumns_AO401";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void complexTableIndexTest() {
        final String SOURCE_FILE_NAME_TRUNC = "tableComplex_DOC_LO41";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Table 1*1 - add column: => java.lang.NullPointerException
     */
    public void insertColumnTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Enhance Line Height by adding leading ("Bleistreifen" from former print)
     * - the space between lines
     */
    public void removeLineHeightLeadingTest() {
        final String SOURCE_FILE_NAME_TRUNC = "lineHeight_AO4-0-1";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Removing one style property, removes all style properties.
     */
    public void lostBackground() {
        final String SOURCE_FILE_NAME_TRUNC = "lostBackground";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Text inserted after a hyperlink always becomes an Hyperlink
     */
    public void hyperlinkExtensionTest() {
        final String SOURCE_FILE_NAME_TRUNC = "hyperlinkSpacesNoUnderline";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODF space XML element that contains more than two spaces and was not
     * completely deleted, returned the removed spaces as remaining element
     */
    public void spaceElementSplitTest() {
        final String SOURCE_FILE_NAME_TRUNC = "hyperlinkSpacesNoUnderline";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODF space XML element that contains more than two spaces and was not
     * completely deleted, returned the removed spaces as remaining element
     */
    public void hyperlinkUnderscoreRemovalTest() {
        final String SOURCE_FILE_NAME_TRUNC = "hyperlinkSpaces";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /*
     * ODT - Roundtrip - numbered list: gap between preview / editor
     */
    public void listRoundtripTest() {
        final String SOURCE_FILE_NAME_TRUNC = "ListRoundtrip";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Foreign ODF elements could not be cloned. Throwable are now caught and
     * rethrown as filterexception to be found by Admin logging -- adding
     * regression test references
     */
    public void foreignElementSplitTest() {
        final String SOURCE_FILE_NAME_TRUNC = "foreignElementSplit";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Preceding nested paragraph in paragraph within unknown content parent was
     * erroneously counted as component
     */
    public void insertCharacterBehindLineShapeWithParagraphTest() {
        final String SOURCE_FILE_NAME_TRUNC = "sample";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Hyperlinks over full unformatted text looses first character.
     */
    public void anchorOverFullUnformattedTextTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Hyperlinks with spaces >1: Hyperlink/First letter is lost after
     * reediting
     */
    public void anchorNoneBreakableTest() {
        final String SOURCE_FILE_NAME_TRUNC = "liste2";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Remove attribute does not works
     */
    public void removeUnderlineTest() {
        final String SOURCE_FILE_NAME_TRUNC = "UNDERLINE";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Remove attribute does not works
     */
    public void removeUnderlineBoldAddColorTest() {
        final String SOURCE_FILE_NAME_TRUNC = "UNDERLINE";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODF toolkit: hyperlink is not properly overwritten with new
     */
    public void hyperlinkOverridenByHyperlinkTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Split Paragraph at end of hyperlink clones hyperlink
     */
    public void listHyperlinkTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - mixed list (numbered/ bullet list): numbering is different in
     * preview /editor
     */
    public void listLevelContinousTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT numbered Lists after saving: Labels and the sublist entries are wrong
     */
    public void listLevelComplexTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT numbered Lists after saving: Labels and the sublist entries are wrong
     */
    public void listMultipleSimularChangeTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     *
     * FIXED: 1) Changing an automatic styles, requires to rename it, if it is
     * being used by others! FIXME: 2) outlineLevel:0 check, copy all attributes
     * and descendent from text:p to text:h and vice versa..
     */
    public void changeParagraphToHeaderTest() {
        final String SOURCE_FILE_NAME_TRUNC = "field";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * List items across the document with the same style do not influence each
     * other in ODF
     */
    public void listWithContinuousNumberingTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Reedit odt with List entries: list entries are displayed on the
     * wrong position
     */
    public void listMixedFirstLevelFullTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Reedit odt with List entries: list entries are displayed on the
     * wrong position
     */
    public void listMixedFirstLevelTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));

        //super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getInputEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Reedit odt with List entries: list entries are displayed on the
     * wrong position
     */
    public void list6LevelTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Create a complex bullet list!
     *
     */
    public void bulletListTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: Changing bullet for one list item changes whole bullet
     */
    public void changeListStyleOfFirstItemTest() {
        final String SOURCE_FILE_NAME_TRUNC = "simple bullet list";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Numbered list: After saving the numbering list contains wrong
     * labels
     */
    public void editSubListTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - Numbered list: After saving the numbering list contains wrong
     * labels
     */
    public void listStyleChangeTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    /**
     * ODT: inserting table after image doubles image
     */
    @Test
    public void insertTableBehindImage() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: Bullets in bullet list not saved
     */
    public void splitListParagraph2() {
        final String SOURCE_FILE_NAME_TRUNC = "simple bullet list 1_pre OX";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - splitParagraph does only split the paragraph and not create a new
     * list item
     */
    public void splitListParagraph() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - bulletlist: placed text behind a bulletlist is lost, after closing
     * the odt
     *
     * The deletion of one of two paragraphs within a list, resulted into the
     * deletion of both, as the routine believed the second was empty after
     * deletion within.
     */
    public void textAfterListTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: insert hyperlink via clipboard not saved
     *
     *
     * The width of an image should be limited to its page width (page width -
     * left&rigth margin) QUESTION: Is the height adapted to keep the ratio?
     */
    public void markHyperlinkAllTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: insert hyperlink via clipboard not saved
     *
     *
     * The width of an image should be limited to its page width (page width -
     * left&rigth margin) QUESTION: Is the height adapted to keep the ratio?
     */
    public void markHyperlinkFirstCharacterTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertLargeImageTest() {
        final String SOURCE_FILE_NAME_TRUNC = "image-attributes";
        final String UID = "d03f7d7218eb";
        final String INTERNAL_IMAGE_PATH = "Pictures/uid" + UID + ".jpg";
        Map<Long, byte[]> resourceMap = new HashMap<Long, byte[]>();
        byte[] imageBytes = null;
        try {
            imageBytes = ResourceUtilities.loadFileAsBytes(ResourceUtilities.getTestInputFile("Herschel-Horsehead-Nebula.jpeg"));
        } catch (IOException ex) {
            Logger.getLogger(EditingRegressionTest.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail(ex.getMessage());
        }
        long uid = Long.parseLong(UID, 16);
        resourceMap.put(uid, imageBytes);
        String savedDocumentPath = super.roundtripRegressionWithResourcesTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()), resourceMap, true);
        OdfPackage pkg = null;
        try {
            // return the absolute path to the test directory
            pkg = OdfPackage.loadPackage(savedDocumentPath);
        } catch (Exception ex) {
            Assert.fail("The saved document '" + savedDocumentPath + "' could not be found!");
            Logger.getLogger(EditingRegressionTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (pkg == null || !pkg.contains(INTERNAL_IMAGE_PATH)) {
            Assert.fail("The image '" + INTERNAL_IMAGE_PATH + "' could not be found in the saved document '" + savedDocumentPath + "'!");
        }
    }

    @Test
    /**
     * ODT: Table borders expected to be on table style (extend ODF)
     */
    public void paddingRoundtripTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: Paragraph style 'List Paragraph' not saved
     */
    public void leftIndentStyleTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: Table border to close to the text
     */
    public void tableBorderTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void simpleList3CopyPasteTest() {
        final String SOURCE_FILE_NAME_TRUNC = "simpleList3";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: insertTab not saved
     */
    public void insertTabTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void simpleListDocTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void loadListStyleResolutionDocFullTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void newListDefinitionTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * When a listLevel is changed on a paragraph the ancestor elements
     * <text:list> and <text:list-item> have to be inserted/deleted.
     */
    public void listLevelTest() {
        final String SOURCE_FILE_NAME_TRUNC = "ListHeading";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * When a listLevel is changed on a paragraph the ancestor elements
     * <text:list> and <text:list-item> have to be inserted/deleted.
     */
    public void listCreationTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Text will be inserted to a paragraph component/element, which has
     * children, but none component child
     */
    public void insertToBoilerplateElementsOnly() {
        final String SOURCE_FILE_NAME_TRUNC = "boilerplate";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Split should delete as well boilerplate from the first part!
     */
    public void splitBeforeBoilerplateContent() {
        final String SOURCE_FILE_NAME_TRUNC = "footnote";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void deleteListByDeletingAllParagraphsFromWithinTest() {
        final String SOURCE_FILE_NAME_TRUNC = "ListStyleResolution";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void TabsInHyperlinkTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void textBeforeEmptyBookmarkTest() {
        final String SOURCE_FILE_NAME_TRUNC = "field";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void whitespaceAroundFieldTest() {
        final String SOURCE_FILE_NAME_TRUNC = "field";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void moveComponentTest() {
        final String SOURCE_FILE_NAME_TRUNC = "image-attributes";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }



    @Test
    /**
     */
    public void exportImageAttributesTest() {
        final String SOURCE_FILE_NAME_TRUNC = "odt-images-linked";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void changeListAlignmentTest() {
        final String SOURCE_FILE_NAME_TRUNC = "images";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void deleteUndoTest() {
        final String SOURCE_FILE_NAME_TRUNC = "odf-fields";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void imageChangeHorizontalAlignTest() {
        final String SOURCE_FILE_NAME_TRUNC = "images";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void columnWidthTest() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_tables";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void hyperlinkInsertionTest() {

        final String SOURCE_FILE_NAME_TRUNC = "hyperlink_destination";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertSingleCharacterTest() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_character_MSO15";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertColumnsWithStyleWithoutXmlId() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_tables_FunnyTable_With_xmlid";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertColumnWithRepeatedCells() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_tables";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertRowAtSecondPosition() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_tables-backgroundTableOnly";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void spanStyleInheritanceTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Split position marks the first component of the second half, therefore
     * Character is Moving the unknown content DANGER: These tests are unable to
     * detect unknown content, like if it is in the first or second half!
     */
    public void markOverUnknownContentPlusSpaceElement() {
        final String SOURCE_FILE_NAME_TRUNC = "DUMMY";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void mergeOverUnknownContent() {
        final String SOURCE_FILE_NAME_TRUNC = "footnote";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void markOverUnknownContent() {
        final String SOURCE_FILE_NAME_TRUNC = "footnote";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void deleteOverUnknownContent() {
        final String SOURCE_FILE_NAME_TRUNC = "footnote";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void hyperlinkTest() {
        final String SOURCE_FILE_NAME_TRUNC = "hyperlink";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void multipleSplitMergeTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void MergeSplitOverUnknownContent() {
        final String SOURCE_FILE_NAME_TRUNC = "footnote";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void splitParagraphAtBeginTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertWithSpace() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_character_MSO15";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     */
    public void editingUsingMultipleSpans() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void insertTextTest() {
        final String SOURCE_FILE_NAME_TRUNC = "ListStyleResolution";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void splitSpaceElementTest() {
        final String SOURCE_FILE_NAME_TRUNC = "compdocfileformat_shortened";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void deletionSpannedColumnTest() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_tables_SMALL";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void deletionColumnTest() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_tables-backgroundTableOnly";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void columnDelete() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    // Verfied: Does it works to delete a hard break within a text container?
    public void editingDeletionTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void editingTextAfterHeadingTestTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void addingNullTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty_as_can_be";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void borderImportTest() {
        final String SOURCE_FILE_NAME_TRUNC = "Tabelle1";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Insert a row and column and undo them again.
     */
    public void deleteRowColumnUndoTest() {
        final String SOURCE_FILE_NAME_TRUNC = "simple-table";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Delete a column and restore it again.
     */
    public void insertColumnUndoTest() {
        final String SOURCE_FILE_NAME_TRUNC = "simple-table-with-lists";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Delete a column and restore it again.
     */
    public void deleteColumnUndoTest() {
        final String SOURCE_FILE_NAME_TRUNC = "coloredTable_MSO15";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Changing the column size of a text table.
     */
    public void changeColumnWidthTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Changing the text flow around an image.
     */
    public void changeImageTextFlowTest() {
        final String SOURCE_FILE_NAME_TRUNC = "imageWithTextFlow";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - changing column width: after closing/re-edit the table is set to
     * 100%
     */
    public void setTableColumnWidthWithRelativeTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }


    /* Align an image to char:
     */
    public void imageAlignParagraph() {
        final String SOURCE_FILE_NAME_TRUNC = "imageAsChar";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    /* Align an image to char:
     */
    public void imageAlignChar() {
        final String SOURCE_FILE_NAME_TRUNC = "imageAsChar";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Removing one style property (font).
     */
    public void deleteHardFormattingFontTest() {
        final String SOURCE_FILE_NAME_TRUNC = "bigFont";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Overriding hyperlinks partly (overlapping)
     */
    public void hyperlinkOverlappingOverridenByHyperlinkTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Adding a link to a part of an existing anchor and nested span.
     */
    public void newHyperlinkOnNestedSpansAndAnchor() {
        final String SOURCE_FILE_NAME_TRUNC = "_multipleSpansNested";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Overriding hyperlinks partly (overlapping)
     */
    public void simpleFormatTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * A text style change over style borders mixes all styles. Formating full
     * text of a paragraph of first part already formatted will mix forma over
     * full selection.
     */
    public void textStyleChangeOverStyleBorders() {
        final String SOURCE_FILE_NAME_TRUNC = "coloredParagraph";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT - changing column width: after closing/re-edit the table is set to
     * 100%
     */
    public void setTableColumnWidthWithAlignMarginTest() {
        final String SOURCE_FILE_NAME_TRUNC = "tabelleAlignMargin";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }


    @Test
    /**
     * Changed bullet symbols are not exported Switching bullet symbols fails in
     * different Level.
     */
    public void switchBulletSymbolsInLevelTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Splitting a paragraph with page break inherited the page break.
     */
    public void splitParagraphWithPageBreak() {
        final String SOURCE_FILE_NAME_TRUNC = "AB pageBreakBefore";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Split ODT paragraph with Page Break is loosing it TestDocument: A and B
     * on two pages First: Adding new paragraph with C behind B on second page
     * Second: Adding page before C, so each letter is on an own page.
     */
    public void pageBreakTest() {
        // A and B on two pages
        final String SOURCE_FILE_NAME_TRUNC = "pageBreakProblem";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Splitting a paragraph with page break inherited the page break.
     */
    public void splitParagraphWithHeadings() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Paragraph formatting/ return/ change/close/re-edit: formatting is wrong
     */
    public void splitParagraphWithHeadings2() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Empty spans are being preserved to keep default character format in a
     * paragraph (both characters have to be bold)
     */
    public void splitParagraphFormatTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * ODT: Resized table: Table size not loaded correctly
     */
    public void tableWidthChangeTest() {
        final String SOURCE_FILE_NAME_TRUNC = "empty";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Bug 35330 - Delete a numbered list/undo/closed/re-edit: => one entry is
     * too much A text:list-item becomes a text:list-header, when there are
     * multiple paragraph in a list and the first paragraph (the one with the
     * list label) is being deleted and some of the following sibling are
     * remaining (without label). WYSIWYG.
     */
    public void insertDeleteListBlockTest() {
        final String SOURCE_FILE_NAME_TRUNC = "ST_Bullets&Numbering2";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * A new list style name could not be set.
     */
    public void changeListDefaultStyleTest() {
        final String SOURCE_FILE_NAME_TRUNC = "listStyleId";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    /**
     * Deleting a column with merged cells within.
     */
    public void deleteColumnWithMergedCellsTest() {
        final String SOURCE_FILE_NAME_TRUNC = "mergedCells";
        super.roundtripRegressionTextTest(SOURCE_FILE_NAME_TRUNC, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }

    @Test
    public void setFontName() {
        final String SOURCE_FILE_NAME_TRUNC = "feature_attributes_character_MSO15";
        super.importOnlyRegressionTest(SOURCE_FILE_NAME_TRUNC, ODT_SUFFIX, getTestMethodName(), getEditingOperations(SOURCE_FILE_NAME_TRUNC, getTestMethodName()));
    }
}
