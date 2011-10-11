/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.common;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfMetaDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMetaElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.text.TextAElement;
import org.odftoolkit.odfdom.dom.element.text.TextTrackedChangesElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.w3c.dom.NodeList;

/**
 * It's a sub class of TextExtractor, which provides a method
 * <code>getText()</code> to return all the text that the user can typically
 * edit in a document, including text in cotent.xml, header and footer in
 * styles.xml, meta data in meta.xml.
 * 
 * <p>
 * This function can be used by search engine, and text analytic operations.
 * </p>
 * 
 * @see org.odftoolkit.odfdom.pkg.OdfElement
 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor
 */
public class EditableTextExtractor extends TextExtractor {

	Document mDocument = null;
	boolean mIsDocumentExtractor = false;

	/**
	 * An instance of EditableTextExtractor will be created to extract the
	 * editable text content in specified document.
	 * 
	 * @param doc
	 *            the document whose text will be extracted.
	 * @return An instance of EditableTextExtractor
	 */
	public static EditableTextExtractor newOdfEditableTextExtractor(Document doc) {
		return new EditableTextExtractor(doc);
	}

	/**
	 * An instance of EditableTextExtractor will be created to extract the
	 * editable text content of an ODF element.
	 * 
	 * @param element
	 *            the ODF element whose text will be extracted.
	 * @return An instance of EditableTextExtractor
	 */
	public static EditableTextExtractor newOdfEditableTextExtractor(OdfElement element) {
		return new EditableTextExtractor(element);
	}

	/**
	 * Return the text content of a element as String
	 * 
	 * @param ele
	 *            the ODF element
	 * @return the text content of the element
	 */
	public static synchronized String getText(OdfElement ele) {
		EditableTextExtractor extractor = newOdfEditableTextExtractor(ele);
		return extractor.getText();
	}

	/**
	 * Return the text content of document as String
	 * 
	 * @param doc
	 *            the document
	 * @return the text content of the document
	 */
	public static synchronized String getText(Document doc) {
		EditableTextExtractor extractor = newOdfEditableTextExtractor(doc);
		return extractor.getText();
	}

	/**
	 * Return the editable text content as a string
	 * 
	 * @return the editable text content as a string
	 */
	@Override
	public String getText() {
		if (mIsDocumentExtractor) {
			return getDocumentText();
		} else {
			visit(mElement);
			return mTextBuilder.toString();
		}
	}

	/**
	 * Constructor with a document as parameter
	 * 
	 * @param doc
	 *            the document whose editable text would be extracted.
	 */
	private EditableTextExtractor(Document doc) {
		super();
		mDocument = doc;
		mIsDocumentExtractor = true;
	}

	/**
	 * Constructor with an ODF element as parameter
	 * 
	 * @param element
	 *            the ODF element whose editable text would be extracted.
	 */
	private EditableTextExtractor(OdfElement element) {
		super(element);
		mIsDocumentExtractor = false;
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of draw:object.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement)
	 */
	@Override
	public void visit(DrawObjectElement element) {
		String embedDocPath = element.getXlinkHrefAttribute();
		Document embedDoc = ((Document) (((OdfContentDom) element.getOwnerDocument()).getDocument()))
				.getEmbeddedDocument(embedDocPath);
		if (embedDoc != null) {
			try {
				mTextBuilder.append(EditableTextExtractor.newOdfEditableTextExtractor(embedDoc).getText());
			} catch (Exception e) {
				Logger.getLogger(EditableTextExtractor.class.getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:tracked-changes.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextTrackedChangesElement)
	 */
	@Override
	public void visit(TextTrackedChangesElement ele) {
		return;
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of text:a.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextAElement)
	 */
	@Override
	public void visit(TextAElement ele) {
		String link = ele.getXlinkHrefAttribute();
		mTextBuilder.append(link);
		appendElementText(ele);
	}

	/**
	 * The end users needn't to care of this method, if you don't want to
	 * override the text content handling strategy of table:table.
	 * 
	 * @see org.odftoolkit.odfdom.dom.DefaultElementVisitor#visit(org.odftoolkit.odfdom.dom.element.text.TextTabElement)
	 */
	@Override
	public void visit(TableTableElement ele) {
		Table table = Table.getInstance(ele);
		List<Row> rowlist = table.getRowList();
		int column = table.getColumnCount();
		for (int i = 0; i < rowlist.size(); i++) {
			Row row = rowlist.get(i);
			for (int j = 0; j < column; j++) {
				mTextBuilder.append(row.getCellByIndex(j).getDisplayText());
				mTextBuilder.append(TabChar);
			}
			mTextBuilder.appendLine();
		}
	}

	private String getDocumentText() {
		StringBuilder builder = new StringBuilder();
		try {
			// Extract text from content.xml
			EditableTextExtractor contentDomExtractor = newOdfEditableTextExtractor(mDocument.getContentRoot());
			builder.append(contentDomExtractor.getText());
			// Extract text from style.xml
			OdfStylesDom styleDom = mDocument.getStylesDom();
			if (styleDom != null) {
				StyleMasterPageElement masterpage = null;
				NodeList list = styleDom.getElementsByTagName("style:master-page");
				if (list.getLength() > 0) {
					masterpage = (StyleMasterPageElement) list.item(0);
				}
				if (masterpage != null) {
					builder.append(newOdfEditableTextExtractor(masterpage).getText());
				}
			}
			// Extract text from meta.xml
			OdfMetaDom metaDom = mDocument.getMetaDom();
			if (metaDom != null) {
				OdfElement root = metaDom.getRootElement();
				OfficeMetaElement officemeta = OdfElement.findFirstChildNode(OfficeMetaElement.class, root);
				if (officemeta != null) {
					builder.append(newOdfEditableTextExtractor(officemeta).getText());
				}
			}
			return builder.toString();
		} catch (Exception e) {
			Logger.getLogger(EditableTextExtractor.class.getName()).severe(e.getMessage());
			return builder.toString();
		}
	}
}
