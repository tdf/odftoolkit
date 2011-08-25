/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009, 2010 IBM. All rights reserved.
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
package org.odftoolkit.simple;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.pkg.MediaType;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;
import org.w3c.dom.Node;

/**
 * This class represents an empty ODF spreadsheet document.
 * 
 */
public class SpreadsheetDocument extends Document {

	private static final String EMPTY_SPREADSHEET_DOCUMENT_PATH = "/OdfSpreadsheetDocument.ods";
	static final Resource EMPTY_SPREADSHEET_DOCUMENT_RESOURCE = new Resource(EMPTY_SPREADSHEET_DOCUMENT_PATH);

	/**
	 * This enum contains all possible media types of SpreadsheetDocument
	 * documents.
	 */
	public enum OdfMediaType implements MediaType {

		SPREADSHEET(Document.OdfMediaType.SPREADSHEET), SPREADSHEET_TEMPLATE(Document.OdfMediaType.SPREADSHEET_TEMPLATE);
		private final Document.OdfMediaType mMediaType;

		OdfMediaType(Document.OdfMediaType mediaType) {
			this.mMediaType = mediaType;
		}

		/**
		 * @return the mediatype of this document
		 */
		public String getMediaTypeString() {
			return mMediaType.getMediaTypeString();
		}

		/**
		 * @return the ODF filesuffix of this document
		 */
		public String getSuffix() {
			return mMediaType.getSuffix();
		}

		/**
		 * 
		 * @param mediaType
		 *            string defining an ODF document
		 * @return the according OdfMediatype encapuslating the given string and
		 *         the suffix
		 */
		public static Document.OdfMediaType getOdfMediaType(String mediaType) {
			return Document.OdfMediaType.getOdfMediaType(mediaType);
		}
	}

	/**
	 * Creates an empty spreadsheet document.
	 * 
	 * @return ODF spreadsheet document based on a default template*
	 * @throws java.lang.Exception
	 *             - if the document could not be created
	 */
	public static SpreadsheetDocument newSpreadsheetDocument() throws Exception {
		return (SpreadsheetDocument) Document.loadTemplate(EMPTY_SPREADSHEET_DOCUMENT_RESOURCE,
				Document.OdfMediaType.SPREADSHEET);
	}

	/**
	 * Creates an empty spreadsheet template.
	 * 
	 * @return ODF spreadsheet template based on a default
	 * @throws java.lang.Exception
	 *             - if the template could not be created
	 */
	public static SpreadsheetDocument newSpreadsheetTemplateDocument() throws Exception {
		SpreadsheetDocument doc = (SpreadsheetDocument) Document.loadTemplate(EMPTY_SPREADSHEET_DOCUMENT_RESOURCE,
				Document.OdfMediaType.SPREADSHEET_TEMPLATE);
		doc.changeMode(OdfMediaType.SPREADSHEET_TEMPLATE);
		return doc;
	}

	/**
	 * To avoid data duplication a new document is only created, if not already
	 * opened. A document is cached by this constructor using the internalpath
	 * as key.
	 */
	protected SpreadsheetDocument(OdfPackage pkg, String internalPath, SpreadsheetDocument.OdfMediaType odfMediaType) {
		super(pkg, internalPath, odfMediaType.mMediaType);
	}

	/**
	 * Creates an SpreadsheetDocument from the OpenDocument provided by a
	 * resource Stream.
	 * 
	 * <p>
	 * Since an InputStream does not provide the arbitrary (non sequentiell)
	 * read access needed by SpreadsheetDocument, the InputStream is cached.
	 * This usually takes more time compared to the other createInternalDocument
	 * methods. An advantage of caching is that there are no problems
	 * overwriting an input file.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF spreadsheet document,
	 * ClassCastException might be thrown.
	 * </p>
	 * 
	 * @param inputStream
	 *            - the InputStream of the ODF spreadsheet document.
	 * @return the spreadsheet document created from the given InputStream
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static SpreadsheetDocument loadDocument(InputStream inputStream) throws Exception {
		return (SpreadsheetDocument) Document.loadDocument(inputStream);
	}

	/**
	 * Loads an SpreadsheetDocument from the provided path.
	 * 
	 * <p>
	 * SpreadsheetDocument relies on the file being available for read access
	 * over the whole lifecycle of SpreadsheetDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF spreadsheet document,
	 * ClassCastException might be thrown.
	 * </p>
	 * 
	 * @param documentPath
	 *            - the path from where the document can be loaded
	 * @return the spreadsheet document from the given path or NULL if the media
	 *         type is not supported by SIMPLE.
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static SpreadsheetDocument loadDocument(String documentPath) throws Exception {
		return (SpreadsheetDocument) Document.loadDocument(documentPath);
	}

	/**
	 * Creates an SpreadsheetDocument from the OpenDocument provided by a File.
	 * 
	 * <p>
	 * SpreadsheetDocument relies on the file being available for read access
	 * over the whole lifecycle of SpreadsheetDocument.
	 * </p>
	 * 
	 * <p>
	 * If the resource stream is not a ODF spreadsheet document,
	 * ClassCastException might be thrown.
	 * </p>
	 * 
	 * @param file
	 *            - a file representing the ODF spreadsheet document.
	 * @return the spreadsheet document created from the given File
	 * @throws java.lang.Exception
	 *             - if the document could not be created.
	 */
	public static SpreadsheetDocument loadDocument(File file) throws Exception {
		return (SpreadsheetDocument) Document.loadDocument(file);
	}

	/**
	 * Get the content root of a spreadsheet document.
	 * 
	 * @return content root, representing the office:spreadsheet tag
	 * @throws Exception
	 *             if the file DOM could not be created.
	 */
	@Override
	public OfficeSpreadsheetElement getContentRoot() throws Exception {
		return super.getContentRoot(OfficeSpreadsheetElement.class);
	}

	/**
	 * Changes the document to the given mediatype. This method can only be used
	 * to convert a document to a related mediatype, e.g. template.
	 * 
	 * @param mediaType
	 *            the related ODF mimetype
	 */
	public void changeMode(OdfMediaType mediaType) {
		setOdfMediaType(mediaType.mMediaType);
	}

	/**
	 * Retrieves sheet by index.
	 * 
	 * @param index
	 *            the index of the retrieved sheet, which starts from 0. If the
	 *            index value is out of range (index >= sheet count or index <
	 *            0), this method would return <code>null</code>.
	 * @since 0.6
	 */
	public Table getSheetByIndex(int index) {
		if (index < 0) {
			return null;
		}
		int count = 0;
		try {
			OfficeSpreadsheetElement spreadsheetElement = getContentRoot();
			Node child = spreadsheetElement.getFirstChild();
			while ((child != null) && (count <= index)) {
				if (child instanceof TableTableElement) {
					if (count == index) {
						return getTableBuilder().getTableInstance((TableTableElement) child);
					} else {
						count++;
					}
				}
				child = child.getNextSibling();
			}
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Retrieves sheet by name.
	 * 
	 * @param name
	 *            the name of the retrieved sheet.
	 * @since 0.6
	 */
	public Table getSheetByName(String name) {
		return getTableByName(name);
	}

	
	/**
	 * Adds a new blank sheet with the specified <code>name</code> to this
	 * document.
	 * 
	 * @param name
	 *            the name of the new sheet.
	 * @return added sheet.
	 * @since 0.6
	 */
	public Table appendSheet(String name) {
		Table newTable = addTable();
		newTable.setTableName(name);
		return newTable;
	}

	/**
	 * Adds a new sheet with data from existing table.
	 * 
	 * @param refTable
	 *            the reference table, which is the data source of the new sheet.
	 * @param name
	 *            the name of the new sheet.
	 * @return added sheet.
	 * @since 0.6
	 */
	public Table appendSheet(Table refTable, String name) {
		TableTableElement refTableElement = refTable.getOdfElement();
		try {
			OdfContentDom contentDom = getContentDom();
			TableTableElement newTableEle = (TableTableElement) (refTableElement.cloneNode(true));
			// not in a same document
			if (refTableElement.getOwnerDocument() != contentDom) {
				Document ownerDocument = refTable.getOwnerDocument();
				copyLinkedRefInBatch(newTableEle, ownerDocument);
				copyForeignStyleRef(newTableEle, ownerDocument);
				newTableEle = (TableTableElement) cloneForeignElement(newTableEle, contentDom, true);
			}
			updateNames(newTableEle);
			updateXMLIds(newTableEle);
			getTableContainerElement().appendChild(newTableEle);
			Table tableInstance = getTableBuilder().getTableInstance(newTableEle);
			tableInstance.setTableName(name);
			return tableInstance;
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Inserts a new blank sheet before the reference index.
	 * 
	 * @param before
	 *            the reference index, which starts from 0. If the index value
	 *            is out of range (index >= sheet count or index < 0), this
	 *            method would return <code>null</code>.
	 * @return inserted sheet.
	 * @since 0.6
	 */
	public Table insertSheet(int before) {
		if (before < 0) {
			return null;
		}
		int count = 0;
		try {
			OfficeSpreadsheetElement spreadsheetElement = getContentRoot();
			Node child = spreadsheetElement.getFirstChild();
			while ((child != null) && (count <= before)) {
				if (child instanceof TableTableElement) {
					if (count == before) {
						Table table = getTableBuilder().newTable();
						getContentRoot().insertBefore(table.getOdfElement(), child);
						return table;
					} else {
						count++;
					}
				}
				child = child.getNextSibling();
			}
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	/**
	 * Inserts a new sheet with data from existing table.
	 * 
	 * @param refTable
	 *            the reference table, which is the data source of the new
	 *            sheet.
	 * @param before
	 *            the reference index, which starts from 0 and new sheet would
	 *            be inserted before it. If the index value is out of range
	 *            (index >= sheet count or index < 0), this method would return
	 *            <code>null</code>.
	 * @return inserted sheet.
	 * @since 0.6
	 */
	public Table insertSheet(Table refTable, int before) {
		if (before < 0) {
			return null;
		}
		int count = 0;
		try {
			OfficeSpreadsheetElement spreadsheetElement = getContentRoot();
			Node child = spreadsheetElement.getFirstChild();
			while ((child != null) && (count <= before)) {
				if (child instanceof TableTableElement) {
					if (count == before) {
						TableTableElement refTableElement = refTable.getOdfElement();
						try {
							OdfContentDom contentDom = getContentDom();
							TableTableElement newTableEle = (TableTableElement) (refTableElement.cloneNode(true));
							//foreign node not in a same document
							if (refTableElement.getOwnerDocument() != contentDom) {
								Document ownerDocument = refTable.getOwnerDocument();
								copyLinkedRefInBatch(newTableEle, ownerDocument);
								copyForeignStyleRef(newTableEle, ownerDocument);
								newTableEle = (TableTableElement) cloneForeignElement(newTableEle, contentDom, true);
							}
							updateNames(newTableEle);
							updateXMLIds(newTableEle);
							newTableEle.setTableNameAttribute(getUniqueSheetName(this));
							getContentRoot().insertBefore(newTableEle, child);
							return getTableBuilder().getTableInstance(newTableEle);
						} catch (Exception e) {
							Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
						}
					} else {
						count++;
					}
				}
				child = child.getNextSibling();
			}
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}

	/**
	 * Removes the sheet in the specified <code>index</code>.
	 * 
	 * @param index
	 *            the index of the removed sheet, which starts from 0. If the
	 *            index value is out of range (index >= sheet count or index <
	 *            0), this method would do nothing.
	 * @since 0.6
	 */
	public void removeSheet(int index) {
		if (index < 0) {
			return;
		}
		int count = 0;
		try {
			OfficeSpreadsheetElement spreadsheetElement = getContentRoot();
			Node child = spreadsheetElement.getFirstChild();
			while ((child != null) && (count <= index)) {
				if (child instanceof TableTableElement) {
					if (count == index) {
						spreadsheetElement.removeChild(child);
						return;
					} else {
						count++;
					}
				}
				child = child.getNextSibling();
			}
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Returns the sheet count of this document.
	 * 
	 * @return the sheet count of this document.
	 * @since 0.6
	 */
	public int getSheetCount() {
		int count = 0;
		try {
			OfficeSpreadsheetElement spreadsheetElement = getContentRoot();
			Node child = spreadsheetElement.getFirstChild();
			while (child != null) {
				if (child instanceof TableTableElement) {
					count++;
				}
				child = child.getNextSibling();
			}
		} catch (Exception e) {
			Logger.getLogger(SpreadsheetDocument.class.getName()).log(Level.SEVERE, null, e);
		}
		return count;
	}
	
	public OdfElement getTableContainerElement() {
		return getTableContainerImpl().getTableContainerElement();
	}
	
	private static String getUniqueSheetName(TableContainer container) {
		List<Table> tableList = container.getTableList();
		boolean notUnique = true;
		String tablename = "Sheet" + (tableList.size() + 1);
		while (notUnique) {
			notUnique = false;
			for (int i = 0; i < tableList.size(); i++) {
				if (tableList.get(i).getTableName() != null) {
					if (tableList.get(i).getTableName().equalsIgnoreCase(tablename)) {
						notUnique = true;
						break;
					}
				}
			}
			if (notUnique) {
				tablename = tablename + Math.round(Math.random() * 10);
			}
		}
		return tablename;
	}
}
