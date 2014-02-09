/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.pkg;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.ResourceUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.pkg.rdfa.Util;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 *
 * The package layer described by the ODF 1.2 Package specification is
 * independent of the above ODF XML layer described by the ODF 1.2 XML Schema
 * specification.
 *
 * Still the abstract concept of documents exist in the ODF Package layer.
 */
public class OdfPackageDocument implements Closeable {

	private static final String TWO_DOTS = "..";
	private static final String SLASH = "/";
	private static final String COLON = ":";
	private static final String EMPTY_STRING = "";
	/**
	 * The path of the root document
	 */
	protected static final String ROOT_DOCUMENT_PATH = EMPTY_STRING;
	private Resolver mResolver;
	/**
	 * The ODF package containing the document
	 */
	protected OdfPackage mPackage;
	/**
	 * The internal path to the document relative to the ODF package
	 */
	protected String mDocumentPathInPackage;
	/**
	 * The mediatype of the ODF package document. Note: Not necessarily an ODF
	 * XML mediatype as specified in ODF 1.2 part1
	 */
	protected String mDocumentMediaType;
	private static Templates mRdfFileExtractionTemplate;

	/**
	 * Creates a new OdfPackageDocument.
	 *
	 * @param pkg - the ODF Package that contains the document. A baseURL is
	 * being generated based on its location.
	 * @param internalPath - the directory path within the package from where
	 * the document should be loaded.
	 * @param mediaTypeString - media type of stream. If unknown null can be
	 * used.
	 */
	protected OdfPackageDocument(OdfPackage pkg, String internalPath, String mediaTypeString) {
		if (pkg != null) {
			mPackage = pkg;
			mDocumentPathInPackage = internalPath;
			this.setMediaTypeString(mediaTypeString);
			pkg.cacheDocument(this, internalPath);
		} else {
			throw new IllegalArgumentException("No Package provided for new document!");
		}
	}

	/**
	 * Loads an OdfPackageDocument from the provided path.
	 *
	 * <p>OdfPackageDocument relies on the file being available for read access
	 * over the whole lifecycle of OdfDocument.</p>
	 *
	 * @param documentPath - the path from where the document can be loaded
	 * @return the OpenDocument from the given path or NULL if the media type is
	 * not supported by ODFDOM.
	 * @throws java.lang.Exception - if the document could not be created.
	 */
	public static OdfPackageDocument loadDocument(String documentPath) throws Exception {
		OdfPackage pkg = OdfPackage.loadPackage(documentPath);
		return pkg.loadDocument(ROOT_DOCUMENT_PATH);
	}

	/**
	 * Returns an embedded OdfPackageDocument from the given package path.
	 *
	 * @param documentPath to the document within the package. The path is
	 * relative the current document path.
	 * @return an embedded OdfPackageDocument
	 */
	public OdfPackageDocument loadSubDocument(String documentPath) {
		String internalPath = this.getDocumentPath() + documentPath;
		internalPath = OdfPackage.normalizeDirectoryPath(internalPath);
		return mPackage.loadDocument(internalPath);
	}

	/**
	 * @return the mediatype of this document
	 */
	public String getMediaTypeString() {
		return mDocumentMediaType;
	}

	/**
	 * @param mediaTypeString for the mediatype of this document
	 */
	protected final void setMediaTypeString(String mediaTypeString) {
		mDocumentMediaType = mediaTypeString;
		if (isRootDocument()) {
			mPackage.setMediaTypeString(mediaTypeString);
		}
	}

	/**
	 * Sets the OdfPackage that contains this OdfPackageDocument.
	 *
	 * @param pkg the OdfPackage that contains this OdfPackageDocument
	 */
	void setPackage(OdfPackage pkg) {
		mPackage = pkg;
	}

	/**
	 * Retreives the OdfPackage for this OdfPackageDocument.
	 *
	 * @return the OdfPackage that contains this OdfPackageDocument.
	 */
	public OdfPackage getPackage() {
		return mPackage;
	}

	/**
	 * Set the relative path for an embedded ODF document.
	 *
	 * @param path to directory of the embedded ODF document (relative to ODF
	 * package root).
	 */
	String setDocumentPath(String path) {
		mDocumentPathInPackage = normalizeDocumentPath(path);
		return mDocumentPathInPackage;
	}

	/**
	 * Get the relative path for an embedded ODF document.
	 *
	 * @return path to the directory of the embedded ODF document (relative to
	 * ODF package root).
	 */
	public String getDocumentPath() {
		return mDocumentPathInPackage;
	}

	/**
	 * Removes an embedded ODF document from the ODF Package. All files within
	 * the embedded document directory will be removed.
	 *
	 * @param internDocumentPath path to the directory of the embedded ODF
	 * document (always relative to the package path of the current document).
	 */
	public void removeDocument(String internDocumentPath) {
		mPackage.removeDocument(mDocumentPathInPackage + internDocumentPath);
	}

	/**
	 * @return true if the document is at the root level of the package
	 */
	public boolean isRootDocument() {
		if (getDocumentPath().equals(ROOT_DOCUMENT_PATH)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the given reference is a reference, which points outside the
	 * ODF package Only relative path are allowed with the exception of a single
	 * slash '/' representing the root document.
	 *
	 * @param ref the file reference to be checked
	 * @return true if the reference is an package external reference
	 */
	protected static boolean isExternalReference(String ref) {
		boolean isExternalReference = false;
		// if the reference is a external relative filePath..
		if (ref.startsWith(TWO_DOTS)
				|| // or absolute filePath AND not root document)
				ref.startsWith(SLASH) && !ref.equals(SLASH)
				|| // or absolute IRI
				ref.contains(COLON)) {
			isExternalReference = true;
		}
		return isExternalReference;
	}

	/**
	 * Ensure the document path for is valid and gurantee unique encoding by
	 * normalizing the path.
	 *
	 * @see OdfPackage#normalizeDirectoryPath(java.lang.String)
	 * @param documentPath the destination directory of the document. The path
	 * should end with a '/'.
	 * @return the documentPath after normalization.
	 */
	protected static String normalizeDocumentPath(String documentPath) {
		String dirPath = OdfPackage.normalizeDirectoryPath(documentPath);
		//package path should not start with '/'.
		if (dirPath.startsWith(SLASH) && !dirPath.equals(SLASH)) {
			dirPath = dirPath.substring(1);
		}
		return dirPath;
	}

	/**
	 * Save the document to given path.
	 *
	 * <p>When save the embedded document to a stand alone document, all the
	 * file entries of the embedded document will be copied to a new document
	 * package. If the embedded document is outside of the current document
	 * directory, you have to embed it to the sub directory and refresh the link
	 * of the embedded document. You should reload it from the given path to get
	 * the saved embedded document.
	 *
	 * @param documentPath - the path to the package document
	 * @throws java.lang.Exception if the document could not be saved
	 */
	public void save(String documentPath) throws Exception {
		File f = new File(documentPath);
		save(f);
	}

	/**
	 * Save the document to a given file.
	 *
	 * <p>If the input file has been cached (this is the case when loading from
	 * an InputStream), the input file can be overwritten.</p>
	 *
	 * <p>Otherwise it's allowed to overwrite the input file as long as the same
	 * path name is used that was used for loading (no symbolic link foo2.odt
	 * pointing to the loaded file foo1.odt, no network path X:\foo.odt pointing
	 * to the loaded file D:\foo.odt).</p>
	 *
	 * <p>When saving the embedded document to a stand alone document, all files
	 * of the embedded document will be copied to a new document package. If the
	 * embedded document is outside of the current document directory, you have
	 * to embed it to the sub directory and refresh the link of the embedded
	 * document. You should reload it from the given file to get the saved
	 * embedded document.
	 *
	 * @param file - the file to save the document
	 * @throws java.lang.Exception if the document could not be saved
	 */
	public void save(File file) throws Exception {
		mPackage.save(file);
	}

	/**
	 * Flush the existing DOM to the document to get in advantage of the recent
	 * changes from the DOM
	 */
	protected void flushDoms() {
		mPackage.flushDoms(this);
	}

	/**
	 * Embed an OdfPackageDocument to the current OdfPackageDocument. All the
	 * file entries of child document will be embedded as well to the current
	 * document package.
	 *
	 * @param newDocument the OdfPackageDocument to be embedded.
	 * @param documentPath to the directory the ODF document should be inserted
	 * (relative to the root of this document).
	 */
	public void insertDocument(OdfPackageDocument newDocument, String documentPath) {
		newDocument.flushDoms();
		mPackage.insertDocument(newDocument, mDocumentPathInPackage + documentPath);
	}

	/**
	 * @param internalPath path to the XML file relative to package root
	 * @return the typed DOM of the given file
	 */
	public OdfFileDom getFileDom(String internalPath) throws Exception {
		String normalizeDocumentPath = getDocumentPath();
		if (!isRootDocument()) {
			normalizeDocumentPath = normalizeDocumentPath(normalizeDocumentPath);
		}
		return OdfFileDom.newFileDom(this, normalizeDocumentPath + internalPath);
	}

	/**
	 * Get EntityResolver to be used in XML Parsers which can resolve content
	 * inside the OdfPackage
	 */
	EntityResolver getEntityResolver() {
		if (mResolver == null) {
			mResolver = new Resolver(mPackage);
		}
		return mResolver;
	}

	/**
	 * Get URIResolver to be used in XSL Transformations which can resolve
	 * content inside the OdfPackage
	 */
	URIResolver getURIResolver() {
		if (mResolver == null) {
			mResolver = new Resolver(mPackage);
		}
		return mResolver;
	}

	/**
	 * Close the OdfPackageDocument, its OdfPackage and release all temporary
	 * created data. Acter execution of this method, this class is no longer
	 * usable. Do this as the last action to free resources. Closing an already
	 * closed document has no effect.
	 */
	public void close() {
		mPackage.close();
		// set all member variables explicit to null
		mPackage = null;
	}

	/**
	 * Helper class to receive an ODF document template for new documents from
	 * the environment (ie. from the JAR via classloader)
	 */
	protected static class Resource {

		private String name;

		public Resource(String name) {
			this.name = name;
		}

		public InputStream createInputStream() {
			InputStream in = OdfPackageDocument.class.getResourceAsStream(this.name);
			if (in == null) {
				Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, "Could not find resource: {0}", this.name);
			}
			return in;
		}
	}

	/**
	 * Extracts RDF Metadata triple from XML files
	 * 
	 * @param internalPath path to the XML file relative to package root
	 * @return RDF Metadata through GRDDL XSLT of given XML file
	 */
	public Model getXMLFileMetadata(String internalPath) {
		Model rdfModel = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OdfXMLHelper helper = new OdfXMLHelper();
			if (mRdfFileExtractionTemplate == null) {
				String filePath = "file:" + OdfPackageDocument.class.getClassLoader().getResource("grddl/odf2rdf.xsl").getPath();
				URI uri = new URI(filePath);
				InputSource inputSource = new InputSource(uri.toString());
				mRdfFileExtractionTemplate = TransformerFactory.newInstance().newTemplates(new SAXSource(inputSource));
			}
			helper.transform(this.getPackage(), internalPath, mRdfFileExtractionTemplate, new StreamResult(out));
			String RDFBaseUri = Util.getRDFBaseUri(this.getPackage().getBaseURI(), internalPath);
			rdfModel = ModelFactory.createDefaultModel();
			rdfModel.read(new InputStreamReader(new ByteArrayInputStream(out.toByteArray()), "utf-8"), RDFBaseUri);
			// remove the last SLASH at the end of the RDFBaseUri:
			// test_rdfmeta.odt/ --> test_rdfmeta.odt
			ResourceUtils.renameResource(rdfModel.getResource(RDFBaseUri), RDFBaseUri.substring(0, RDFBaseUri.length() - 1));
		} catch (Exception ex) {
			Logger.getLogger(OdfPackageDocument.class.getName()).log(Level.SEVERE, null, ex);
		}
		return rdfModel;
	}
}
