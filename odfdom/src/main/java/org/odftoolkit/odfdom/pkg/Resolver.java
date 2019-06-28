/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.pkg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * resolve external entities
 */
class Resolver implements EntityResolver, URIResolver {

	private final OdfPackage mPackage;

	/**
	 * Resolver constructor.
	 */
	public Resolver(OdfPackage pkg) {
		super();
		mPackage = pkg;
	}

	/**
	 * Allow the application to resolve external entities.
	 *
	 * The Parser will call this method before opening any external entity except
	 * the top-level document entity (including the external DTD subset,
	 * external entities referenced within the DTD, and external entities referenced
	 * within the document element): the application may request that the parser
	 * resolve the entity itself, that it use an alternative URI,
	 * or that it use an entirely different input source.
	 */
	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// this deactivates the attempt to load the none existent Math DTD once referenced from OOo files
		if (publicId != null && publicId.startsWith("-//OpenOffice.org//DTD Modified W3C MathML")) {
			return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
		}
		if (systemId != null) {
			// if the entity to be resolved is the base URI return the package
			if ((mPackage.getBaseURI() != null) && systemId.startsWith(mPackage.getBaseURI())) {
				if (systemId.equals(mPackage.getBaseURI())) {
					InputStream in = null;
					try {
						in = mPackage.getInputStream();
					} catch (Exception e) {
						throw new SAXException(e);
					}
					InputSource ins;
					ins = new InputSource(in);

					if (ins == null) {
						return null;
					}
					ins.setSystemId(systemId);
					return ins;
				} else {
					// if the reference points into the package (is larger than the base URI)
					if (systemId.length() > mPackage.getBaseURI().length() + 1) {
						InputStream in = null;
						try {
							String path = systemId.substring(mPackage.getBaseURI().length() + 1);
							in = mPackage.getInputStream(path);
							InputSource ins = new InputSource(in);
							ins.setSystemId(systemId);
							return ins;
						} catch (Exception ex) {
							Logger.getLogger(Resolver.class.getName()).log(Level.SEVERE, null, ex);
						} finally {
							try {
								in.close();
							} catch (IOException ex) {
								Logger.getLogger(Resolver.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					}
					return null;
				}
			} else if (systemId.startsWith("resource:/")) {
				int i = systemId.indexOf('/');
				if ((i > 0) && systemId.length() > i + 1) {
					String res = systemId.substring(i + 1);
					ClassLoader cl = OdfPackage.class.getClassLoader();
					InputStream in = cl.getResourceAsStream(res);
					if (in != null) {
						InputSource ins = new InputSource(in);
						ins.setSystemId(systemId);
						return ins;
					}
				}
				return null;
			} else if (systemId.startsWith("jar:")) {
				try {
					URL url = new URL(systemId);
					JarURLConnection jarConn = (JarURLConnection) url.openConnection();
					InputSource ins = new InputSource(jarConn.getInputStream());
					ins.setSystemId(systemId);
					return ins;
				} catch (MalformedURLException ex) {
					Logger.getLogger(OdfPackage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return null;
	}

	public Source resolve(String href, String base)
			throws TransformerException {
		try {
			URI uri = null;
			if (base != null) {
				URI baseuri = new URI(base);
				uri = baseuri.resolve(href);
			} else {
				uri = new URI(href);
			}

			InputSource ins = null;
			try {
				ins = resolveEntity(null, uri.toString());
			} catch (Exception e) {
				throw new TransformerException(e);
			}
			if (ins == null) {
				return null;
			}
			InputStream in = ins.getByteStream();
			StreamSource src = new StreamSource(in);
			src.setSystemId(uri.toString());
			return src;
		} catch (URISyntaxException use) {
			return null;
		}
	}
}
