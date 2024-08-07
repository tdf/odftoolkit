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

/*
 * This file is automatically generated.
 * Don't edit manually.
 */
package org.odftoolkit.odfdom.dom.element.db;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;

/**
 * DOM implementation of OpenDocument element  {@odf.element db:database-description}.
 *
 */
public class DbDatabaseDescriptionElement extends OdfElement {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.DB, "database-description");

	/**
	 * Create the instance of <code>DbDatabaseDescriptionElement</code>
	 *
	 * @param  ownerDoc     The type is <code>OdfFileDom</code>
	 */
	public DbDatabaseDescriptionElement(OdfFileDom ownerDoc) {
		super(ownerDoc, ELEMENT_NAME);
	}

	/**
	 * Get the element name
	 *
	 * @return  return   <code>OdfName</code> the name of element {@odf.element db:database-description}.
	 */
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

	/**
	 * Create child element {@odf.element db:file-based-database}.
	 *
	 * @param dbMediaTypeValue  the <code>String</code> value of <code>DbMediaTypeAttribute</code>, see {@odf.attribute  db:media-type} at specification
	 * @param xlinkHrefValue  the <code>String</code> value of <code>XlinkHrefAttribute</code>, see {@odf.attribute  xlink:href} at specification
	 * @param xlinkTypeValue  the <code>String</code> value of <code>XlinkTypeAttribute</code>, see {@odf.attribute  xlink:type} at specification
	 * Child element was added in ODF 1.2
	 *
	 * @return the element {@odf.element db:file-based-database}
	 */
	 public DbFileBasedDatabaseElement newDbFileBasedDatabaseElement(String dbMediaTypeValue, String xlinkHrefValue, String xlinkTypeValue) {
		DbFileBasedDatabaseElement dbFileBasedDatabase = ((OdfFileDom) this.ownerDocument).newOdfElement(DbFileBasedDatabaseElement.class);
		dbFileBasedDatabase.setDbMediaTypeAttribute(dbMediaTypeValue);
		dbFileBasedDatabase.setXlinkHrefAttribute(xlinkHrefValue);
		dbFileBasedDatabase.setXlinkTypeAttribute(xlinkTypeValue);
		this.appendChild(dbFileBasedDatabase);
		return dbFileBasedDatabase;
	}

	/**
	 * Create child element {@odf.element db:server-database}.
	 *
	 * @param dbTypeValue  the <code>String</code> value of <code>DbTypeAttribute</code>, see {@odf.attribute  db:type} at specification
	 * Child element was added in ODF 1.2
	 *
	 * @return the element {@odf.element db:server-database}
	 */
	 public DbServerDatabaseElement newDbServerDatabaseElement(String dbTypeValue) {
		DbServerDatabaseElement dbServerDatabase = ((OdfFileDom) this.ownerDocument).newOdfElement(DbServerDatabaseElement.class);
		dbServerDatabase.setDbTypeAttribute(dbTypeValue);
		this.appendChild(dbServerDatabase);
		return dbServerDatabase;
	}

  /**
   * Accept an visitor instance to allow the visitor to do some operations. Refer to visitor design
   * pattern to get a better understanding.
   *
   * @param visitor an instance of DefaultElementVisitor
   */
	@Override
	public void accept(ElementVisitor visitor) {
		if (visitor instanceof DefaultElementVisitor) {
			DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
			defaultVisitor.visit(this);
		} else {
			visitor.visit(this);
		}
	}
}
