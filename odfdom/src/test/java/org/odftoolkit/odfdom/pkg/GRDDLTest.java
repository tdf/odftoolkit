/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
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
package org.odftoolkit.odfdom.pkg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.test.ModelTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;
import org.xml.sax.InputSource;

public class GRDDLTest extends ModelTestBase {

  private static final Logger LOG = Logger.getLogger(GRDDLTest.class.getName());
  private static final String SIMPLE_ODT = "test_rdfmeta.odt";

  public GRDDLTest(String name) {
    super(name);
    // TODO: Auto-generated constructor stub
  }

  /**
   * Need help: GRDDLTest.testGRDDL:72 org.xml.sax.SAXParseException; systemId:
   * odfdom/target/test-classes/test_rdfmeta.odt; lineNumber: 4; columnNumber: 11; The prefix
   * "vcard" for element "vcard:fn" is not bound.
   */
  @Test
  public void testGRDDL() throws UnsupportedEncodingException, IOException {
    try {
      OdfXMLHelper helper = new OdfXMLHelper();
      OdfTextDocument odt =
          (OdfTextDocument)
              OdfDocument.loadDocument(ResourceUtilities.getAbsoluteInputPath(SIMPLE_ODT));
      InputSource inputSource =
          new InputSource(
              ResourceUtilities.getURI("grddl" + File.separatorChar + "odf2rdf.xsl").toString());
      Templates multiFileAccessTemplate =
          TransformerFactory.newInstance().newTemplates(new SAXSource(inputSource));
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      helper.transform(
          odt.getPackage(), "content.xml", multiFileAccessTemplate, new StreamResult(out));
      Model m1 = createMemModel();
      // Dumping the DOM to XML file
      //    byte[] bytes = out.toByteArray();
      //    Path path = Paths.get("c:\\test.xml");
      //   Files.write(path, bytes);

      m1.read(
          new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), "utf-8"),
          odt.getPackage().getBaseURI());
      LOG.info("RDF Model:\n" + m1);
      TestCase.assertEquals(5, m1.size());
    } catch (Exception ex) {
      StringWriter sw = new StringWriter();
      ex.printStackTrace(new PrintWriter(sw));
      String message = sw.toString();
      Logger.getLogger(GRDDLTest.class.getName()).log(Level.SEVERE, message, ex);
      sw.close();
      Assert.fail(message);
    }
  }
}
