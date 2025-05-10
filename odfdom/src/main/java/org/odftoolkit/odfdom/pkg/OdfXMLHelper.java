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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.odftoolkit.odfdom.pkg.rdfa.Util;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

class OdfXMLHelper {

  private static final Logger LOG = Logger.getLogger(OdfXMLHelper.class.getName());

  /**
   * create an XMLReader with a Resolver set to parse content in a ODF Package
   *
   * @param pkg the ODF Package
   * @return a SAX XMLReader
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public XMLReader newXMLReader(OdfPackage pkg) throws SAXException, ParserConfigurationException {
    XMLReader xmlReader = pkg.getXMLReader();
    xmlReader.setEntityResolver(pkg.getEntityResolver());
    return xmlReader;
  }

  /**
   * use SAX parser to parse content of package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param contentHandler a SAX Content handler to receive SAX Events
   * @param errorHandler a SAX Error handler to be called on errors during parsing
   * @throws SAXException
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws TransformerConfigurationException
   * @throws TransformerException
   */
  public void parse(
      OdfPackage pkg, String path, ContentHandler contentHandler, ErrorHandler errorHandler)
      throws SAXException, ParserConfigurationException, IOException, IllegalArgumentException,
          TransformerConfigurationException, TransformerException {

    InputStream is = null;
    try {
      is = pkg.getInputStream(path);
      XMLReader reader = newXMLReader(pkg);

      String uri = pkg.getBaseURI() + path;

      if (contentHandler != null) {
        reader.setContentHandler(contentHandler);
      }
      if (errorHandler != null) {
        reader.setErrorHandler(errorHandler);
      }

      InputSource ins = new InputSource(is);
      ins.setSystemId(uri);

      reader.parse(ins);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    } finally {
      try {
        is.close();
      } catch (IOException ex) {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Do XSL-Transformation on content contained in package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templatePath a path to a file in the filesystem containing an XSL Template
   * @param outPath a path in the filesystem for the output of the XSL Transformation
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, String templatePath, String outPath)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {

    transform(pkg, path, new File(templatePath), new File(outPath));
  }

  /**
   * Do XSL-Transformation on content contained in package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templateSource TraX Source of an XSL Transformation Template
   * @param outPath path to an output file for the XSL Transformation
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, Source templateSource, String outPath)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {

    transform(pkg, path, templateSource, new File(outPath));
  }

  /**
   * Do XSL-Transformation on content contained in package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templateSource TraX Source of an XSL Transformation
   * @param out an output File
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, Source templateSource, File out)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {

    transform(pkg, path, templateSource, new StreamResult(out));
  }

  /**
   * Do XSL-Transformation on content contained in package insert result back to package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templatePath path inside the filesystem to an XSL template file
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, String templatePath)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {
    transform(pkg, path, new File(templatePath));
  }

  /**
   * Do XSL-Transformation on content contained in package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param template File containing an XSLT Template
   * @param out File for the XSLT ouput
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, File template, File out)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {

    TransformerFactory transformerfactory = TransformerFactory.newInstance();

    Templates templates = transformerfactory.newTemplates(new StreamSource(template));
    transform(pkg, path, templates, new StreamResult(out));
  }

  /**
   * Do XSL-Transformation on content contained in package insert result back to package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param template a File containing an XSLT Template
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, File template)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {

    TransformerFactory transformerfactory = TransformerFactory.newInstance();

    Templates templates = transformerfactory.newTemplates(new StreamSource(template));
    transform(pkg, path, templates);
  }

  /**
   * Do XSL-Transformation on content contained in package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templateSource TraX Source of an XSLT Template
   * @param result TraX Result of XSL-Tranformation
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, Source templateSource, Result result)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {
    TransformerFactory transformerfactory = TransformerFactory.newInstance();
    transformerfactory.setURIResolver(pkg.getURIResolver());

    Templates templates = transformerfactory.newTemplates(templateSource);
    transform(pkg, path, templates, result);
  }

  /**
   * Does an XSL-Transformation on content contained in package.<br>
   * <br>
   * There are three default parameters provided to the transformation: There are three default
   * parameters provided to the transformation:
   *
   * <ol>
   *   <li><b>sourceURL:</b> the URL of the source directory
   *   <li><b>sourceBaseURL:</b> baseURL of the source file (the package). This URL necessary to
   *       access any content within the package from the XSLT scripts. The relative package path
   *       will concatenated after the 'sourceBaseURL'.
   *   <li><b>targetURL:</b> the URL of the target directory
   *   <li><b>targetBaseURL:</b>the baseURL of the target file
   * </ol>
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templates TraX XSLT Template
   * @param result TraX XSLT Result
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, Templates templates, Result result)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {
    try {

      Source source = null;
      String uri = pkg.getBaseURI() + path;
      Document doc = pkg.getDom(path);
      source = new DOMSource(doc);
      Transformer transformer = templates.newTransformer();
      transformer.setURIResolver(pkg.getURIResolver());

      transformer.setParameter("sourceURL", uri);
      // switch to this, for odf2rdf.xsl grddl transformation
      transformer.setParameter("sourceBaseURL", Util.getRDFBaseUri(pkg.getBaseURI(), path));

      uri = result.getSystemId();
      if (uri != null) {
        transformer.setParameter("targetURL", uri);
        int i = uri.lastIndexOf('/');
        if (i > 0) {
          uri = uri.substring(0, i + 1);
          transformer.setParameter("targetBaseURL", uri);
        }
      }
      DocumentType doctype = doc.getDoctype();
      if (doctype != null) {
        if (doctype.getPublicId() != null) {
          transformer.setParameter("publicType", doctype.getPublicId());
        }
        if (doctype.getSystemId() != null) {
          transformer.setParameter("systemType", doctype.getSystemId());
        }
      }

      transformer.transform(source, result);
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, ex.getMessage(), ex);
    }
  }

  /**
   * Do XSL-Transformation on content contained in package and insert result back to package
   *
   * @param pkg a OdfPackage
   * @param path a path inside the OdfPackage, eg. to a contained content.xml stream
   * @param templates Trax XSLT Template
   * @throws TransformerConfigurationException
   * @throws TransformerException
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws SAXException
   * @throws ParserConfigurationException
   */
  public void transform(OdfPackage pkg, String path, Templates templates)
      throws TransformerConfigurationException, TransformerException, IOException,
          IllegalArgumentException, SAXException, ParserConfigurationException {

    Result result = null;
    ByteArrayOutputStream baos = null;

    //		if (pkg.isDomCached(path)) {
    //			result = new DOMResult();
    //		} else {
    baos = new ByteArrayOutputStream();
    result = new StreamResult(baos);
    //		}

    transform(pkg, path, templates, result);

    //		if (pkg.isDomCached(path)) {
    //			try {
    //				pkg.insert((Document) ((DOMResult) result).getNode(), path, null);
    //			} catch (Exception ex) {
    //				Logger.getLogger(OdfXMLHelper.class.getName()).log(Level.SEVERE, null, ex);
    //			}
    //		} else {
    try {
      byte[] data = baos.toByteArray();
      pkg.insert(data, path, "text/xml");
    } catch (Exception ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    //		}

  }
}
