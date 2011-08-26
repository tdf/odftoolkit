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
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParserFactory;
import org.junit.Test;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;
import org.junit.Assert;
import org.junit.Ignore;
import schema2template.example.odf.OdfCodegen;

public class PuzzlePieceTest {

	private static final Logger LOG = Logger.getLogger(PuzzlePieceTest.class.getName());
	//ToDo: ODF DETAILS (numbers and schema paths should be system variabels)
	private static final int ODF11_ELEMENT_NUMBER = 507;
	private static final int ODF11_ATTRIBUTE_NUMBER = 840;
	private static final String ODF_RESOURCE_DIR = "target" + File.separator + "classes"
	+ File.separator + "examples" + File.separator + "odf";
	private Expression mRoot;
	private static final Boolean DEBUG = Boolean.FALSE;

	private Expression parseOdfSchema(File rngFile) throws Exception {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);

		Expression root = RELAXNGReader.parse(
				rngFile.getAbsolutePath(),
				factory,
				new com.sun.msv.reader.util.IgnoreController()).getTopLevel();

		if (root == null) {
			throw new Exception("Schema could not be parsed.");
		}
		return root;
	}

	public PuzzlePieceTest() throws Exception {
		mRoot = parseOdfSchema(new File(ODF_RESOURCE_DIR + File.separator + OdfCodegen.ODF11_RNG_FILE_NAME));
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
		PuzzlePieceSet allElements = new PuzzlePieceSet();
		PuzzlePieceSet allAttributes = new PuzzlePieceSet();
		PuzzlePiece.extractPuzzlePieces(mRoot, allElements, allAttributes);
		checkFoundNumber(allElements, ODF11_ELEMENT_NUMBER, "element");
		checkFoundNumber(allAttributes, ODF11_ATTRIBUTE_NUMBER, "attribute");
	}

	/** Routine to compare the expected number of either attributes or elements with the found amount */
	private void checkFoundNumber(PuzzlePieceSet puzzlePieceSet, int expectedAmount, String nodeName) {
		if (expectedAmount == puzzlePieceSet.size()) {
			LOG.log(Level.INFO, "The expected amount of {0}s could be found", nodeName);
			if(DEBUG){
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


