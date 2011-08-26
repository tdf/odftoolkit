/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
package schema2template;

import com.sun.msv.grammar.Expression;
import java.io.BufferedReader;
import static schema2template.example.odf.OdfHelper.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;
import org.junit.Assert;
import org.junit.Ignore;
import schema2template.example.odf.OdfHelper;
import schema2template.model.MSVExpressionIterator;

public class PuzzlePieceTest {

	private static final Logger LOG = Logger.getLogger(PuzzlePieceTest.class.getName());
	private static final String OUTPUT_DUMP_ODF12 = "target" + File.separator + "odf12-msvtree.dump";
	private static final String OUTPUT_REF_ODF12 = TEST_INPUT_ROOT + File.separator + "odf12-msvtree.ref";


	/**
	 * Test: Use the MSV
	 *
	 * <p>This test uses the ODF example, but it's meant to test the general ability to correctly
	 * extract PuzzlePieces out of a XML schema</p>
	 */
	@Test
	public void testMSVExpressionTree() {
		try {
			Expression odf12Root = OdfHelper.loadSchemaODF12();
			String odf12Dump = MSVExpressionIterator.dumpMSVExpressionTree(odf12Root);
			LOG.info("Writing MSV RelaxNG tree into file: " + OUTPUT_DUMP_ODF12);
			PrintWriter out = new PrintWriter(new FileWriter(OUTPUT_DUMP_ODF12));
			out.print(odf12Dump);
			out.close();
			String odf12Ref = readFileAsString(OUTPUT_REF_ODF12);
			Assert.assertTrue(odf12Ref.equals(odf12Dump));
		} catch (Exception ex) {
			Logger.getLogger(PuzzlePieceTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail(ex.toString());
		}
	}

	/**
	 * Reading a file into a string
     * @param filePath  path of the file to be opened.
     */
    private String readFileAsString(String filePath) throws java.io.IOException {
        StringBuilder fileData = new StringBuilder(2000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

	/**
	 * Test: Create PuzzlePiece elements and attributes with ODF Spec 1.1 (old version, won't be changed, so
	 * it's a good base for a test).
	 *
	 * <p>This test uses the ODF example, but it's meant to test the general ability to correctly
	 * extract PuzzlePieces out of a XML schema</p>
	 */
	@Test
	@Ignore //bug Under some platforms (e.g. Oracle JDK 6b22 on W7 64bit) return changing results let the test fail
	public void testExtractPuzzlePieces() {
		try {
			PuzzlePieceSet allElements = new PuzzlePieceSet();
			PuzzlePieceSet allAttributes = new PuzzlePieceSet();
			PuzzlePiece.extractPuzzlePieces(OdfHelper.loadSchemaODF11(), allElements, allAttributes);
			checkFoundNumber(allElements, ODF11_ELEMENT_NUMBER, "element");
			checkFoundNumber(allAttributes, ODF11_ATTRIBUTE_NUMBER, "attribute");
		} catch (Exception ex) {
			Logger.getLogger(PuzzlePieceTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail(ex.toString());
		}
	}

	/** Routine to compare the expected number of either attributes or elements with the found amount */
	private void checkFoundNumber(PuzzlePieceSet puzzlePieceSet, int expectedAmount, String nodeName) {
		if (expectedAmount == puzzlePieceSet.size()) {
			LOG.log(Level.INFO, "The expected amount of {0}s could be found", nodeName);
			if (DEBUG) {
				int i = 0;
				for (PuzzlePiece piece : puzzlePieceSet) {
					LOG.info(piece.getQName() + " was " + nodeName + " #" + ++i);
				}
				LOG.info("++++++++++++");
			}
		} else {
			String errorMsg = "Instead of " + expectedAmount
					+ " there were " + puzzlePieceSet.size() + " " + nodeName + "s found";
			LOG.severe(errorMsg);
			int i = 0;
			for (PuzzlePiece piece : puzzlePieceSet) {
				LOG.severe(piece.getQName() + " was " + nodeName + " #" + ++i);
			}
			LOG.info("********************");
			Assert.fail(errorMsg);
		}
	}
}
