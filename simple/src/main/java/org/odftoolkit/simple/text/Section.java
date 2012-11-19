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

package org.odftoolkit.simple.text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.text.TextSectionElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.table.AbstractTableContainer;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;

import sun.misc.BASE64Encoder;

/**
 * This class represents section definition in text document. It provides
 * methods to manipulate section in text document, such as getting/setting
 * section name, moving section and so on.
 * 
 * @since 0.4
 */
public class Section extends Component implements ParagraphContainer,
		TableContainer, ListContainer {

	private ParagraphContainerImpl paragraphContainerImpl;
	private TableContainerImpl tableContainerImpl;
	private ListContainerImpl listContainerImpl;

	private TextSectionElement mSectionElement;
	private Document mDocument;
	private ProtectionKeyDigestProvider protectionKeyDigestProvider;

	private Section(Document doc, TextSectionElement element) {
		mSectionElement = element;
		mDocument = doc;
	}

	/**
	 * Get a section instance by an object of <code>TextSectionElement</code>.
	 * 
	 * @param element
	 *            - an object of <code>TextSectionElement</code>
	 * @return an instance of <code>Section</code> that can represent
	 *         <code>TextSectionElement</code>
	 */
	public static Section getInstance(TextSectionElement element) {
		return new Section((Document) ((OdfFileDom) (element.getOwnerDocument())).getDocument(), element);
	}

	/**
	 * Return the ODF document which this section belongs to.
	 * 
	 * @return - the ODF document which this section belongs to.
	 */
	public Document getOwnerDocument() {
		return mDocument;
	}

	/**
	 * Return the name of this section
	 * 
	 * @return - the name of this section
	 */
	public String getName() {
		return mSectionElement.getTextNameAttribute();
	}

	/**
	 * Set the value of this section name
	 * 
	 * @param name
	 *            - the value of name to be set
	 */
	public void setName(String name) {
		mSectionElement.setTextNameAttribute(name);
	}

	/**
	 * Remove this section from the document.
	 * <p>
	 * All the linked resources which are only linked to this section will be
	 * removed too.
	 * 
	 */
	public void remove() {
		mDocument.removeElementLinkedResource(mSectionElement);
		mSectionElement.getParentNode().removeChild(mSectionElement);
		paragraphContainerImpl = null;
		mSectionElement = null;
		mDocument = null;
	}

	/**
	 * Return an instance of <code>TextSectionElement</code> which represents
	 * this section.
	 * 
	 * @return - an instance of <code>TextSectionElement</code> which represents
	 *         this section
	 */
	public TextSectionElement getOdfElement() {
		return mSectionElement;
	}

	/**
	 * Return whether this section is contained in footer or header.
	 * 
	 * @return - true if this section is contained in footer or header. false if
	 *         this section is not contained in footer or header.
	 */
	boolean isInHeaderFooter() {
		try {
			if (mSectionElement.getOwnerDocument() == mDocument.getStylesDom())
				return true;
		} catch (Exception e) {
			Logger.getLogger(Section.class.getName()).log(Level.SEVERE, "Failed in isInHeaderFooter", e);
		}
		return false;
	}

	/**
	 * Set the value to specify whether the section is protected.
	 * <p>
	 * If this value is set to false, the existing password will be removed at
	 * the same.
	 * 
	 * @param isProtected
	 *            - "true" represents the section cannot be edited through a
	 *            user interface. "false" represents the section is allowed to
	 *            be edited.
	 */
	public void setProtected(boolean isProtected) {
		mSectionElement.setTextProtectedAttribute(isProtected);
		if (!isProtected && getProtectedPassword() != null)
			setProtectedWithPassword(null);
	}

	/**
	 * Return the value of section which specifies whether the section is
	 * protected.
	 * 
	 * @return whether the section is protected.
	 */
	public boolean isProtected() {
		return mSectionElement.getTextProtectedAttribute();
	}

	/**
	 * Set the password which specifies that an authorization is required for
	 * removing the protection of this section.
	 * <p>
	 * If key is empty or null, the attribute of
	 * <code>text:protection-key</code> and
	 * <code>text:protection-key-digest-algorithm</code> will be removed.
	 * <p>
	 * The authentication procedure can be customized by
	 * {@link Section#setProtectionKeyDigestProvider(ProtectionKeyDigestProvider)}
	 * . The default digest algorighom of the protection key is SHA-1:
	 * {@link http://www.w3.org/2000/09/xmldsig#sha1.}
	 * 
	 * @param key
	 *            -the value of the password.
	 * 
	 */
	public void setProtectedWithPassword(String key) {
		if (key != null && key.length() > 0) {
			mSectionElement.setTextProtectionKeyAttribute(generateHashKey(key));
			mSectionElement
					.setTextProtectionKeyDigestAlgorithmAttribute(getDigestAlgorithm());
			setProtected(true);
		} else {
			mSectionElement.removeAttributeNS(OdfDocumentNamespace.TEXT
					.getUri(), "protection-key");
			mSectionElement.removeAttributeNS(OdfDocumentNamespace.TEXT
					.getUri(), "protection-key-digest-algorithm");
		}
	}

	/**
	 * Get the protection key of this section.
	 * 
	 * @return the protection key of this section
	 */
	public String getProtectedPassword() {
		return mSectionElement.getTextProtectionKeyAttribute();
	}

	/**
	 * Get the protection key digest algorithm.
	 * <p>
	 * The default value is http://www.w3.org/2000/09/xmldsig#sha1, if no value
	 * specified.
	 * 
	 * @return an IRI that identifies an authentication procedure for removing a
	 *         protection.
	 */
	public String getProtectionKeyDigestAlgorithm() {
		return mSectionElement.getTextProtectionKeyDigestAlgorithmAttribute();
	}

	private static class SHA1KeyDigest implements ProtectionKeyDigestProvider {

		private static final String KEY_DIGEST_ALGORITHM = "http://www.w3.org/2000/09/xmldsig#sha1";
		private static SHA1KeyDigest provider;

		static SHA1KeyDigest getInstance() {
			if (provider == null)
				provider = new SHA1KeyDigest();
			return provider;
		}

//		@Override
		public String generateHashKey(String passwd) {
			String hashKey = null;
			if (passwd != null && passwd.length() > 0) {
				MessageDigest md;
				try {
					byte[] pwd = new byte[passwd.length() * 2];
					for (int i = 0; i < passwd.length(); i++) {
						pwd[2 * i] = (byte) (passwd.charAt(i) & 0xFF);
						pwd[2 * i + 1] = (byte) (passwd.charAt(i) >> 8);
					}
					md = MessageDigest.getInstance("SHA-1");
					byte[] byteCode = md.digest(pwd);
					BASE64Encoder encoder = new BASE64Encoder();
					hashKey = encoder.encode(byteCode);
				} catch (NoSuchAlgorithmException e) {
					Logger.getLogger(Section.class.getName(),
							"Fail to initiate the digest method.");
				}
			}
			return hashKey;
		}

//		@Override
		public String getProtectionKeyDigestAlgorithm() {
			return KEY_DIGEST_ALGORITHM;
		}

	}

	/**
	 * Set the provider which provides corresponding protection key digest
	 * algorithm.
	 * 
	 * @param provider
	 *            - an instance of a protection key digest algorithm provider
	 */
	public void setProtectionKeyDigestProvider(
			ProtectionKeyDigestProvider provider) {
		protectionKeyDigestProvider = provider;
	}

	/**
	 * Get current used provider which provides corresponding protection key
	 * digest algorithm.
	 * 
	 * @return the current used provider.
	 */
	public ProtectionKeyDigestProvider getProtectionKeyDigestProvier() {
		if (protectionKeyDigestProvider == null) {
			protectionKeyDigestProvider = getDefaultProtectionKeyDigestProvider();
		}
		return protectionKeyDigestProvider;
	}

	/**
	 * Get the default provider which use SHA-1 standard as the protection key
	 * digest algorithm.
	 * 
	 * @return the default protection key digest algorithm.
	 */
	public ProtectionKeyDigestProvider getDefaultProtectionKeyDigestProvider() {
		return SHA1KeyDigest.getInstance();
	}

	private String generateHashKey(String passwd) {
		return getProtectionKeyDigestProvier().generateHashKey(passwd);
	}

	private String getDigestAlgorithm() {
		return getProtectionKeyDigestProvier()
				.getProtectionKeyDigestAlgorithm();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Section))
			return false;
		Section aSection = (Section) obj;
		if (aSection == this)
			return true;
		return aSection.getOdfElement().equals(mSectionElement);
	}

	//****************Paragraph support******************//
	
	public Paragraph addParagraph(String textContent) {
		return getParagraphContainerImpl().addParagraph(textContent);
	}

	public OdfElement getParagraphContainerElement() {
		return getParagraphContainerImpl().getParagraphContainerElement();
	}

	public boolean removeParagraph(Paragraph para) {
		return getParagraphContainerImpl().removeParagraph(para);
	}

	private class ParagraphContainerImpl extends AbstractParagraphContainer {
		public OdfElement getParagraphContainerElement()
		{
			return mSectionElement;
		}
	}
	private ParagraphContainerImpl getParagraphContainerImpl() {
		if (paragraphContainerImpl == null)
			paragraphContainerImpl = new ParagraphContainerImpl();
		return paragraphContainerImpl;
	}

	public Paragraph getParagraphByIndex(int index, boolean isEmptyParagraphSkipped) {
		return getParagraphContainerImpl().getParagraphByIndex(index, isEmptyParagraphSkipped);
	}

	public Paragraph getParagraphByReverseIndex(int reverseIndex, boolean isEmptyParagraphSkipped) {
		return getParagraphContainerImpl().getParagraphByReverseIndex(reverseIndex, isEmptyParagraphSkipped);
	}

	public Iterator<Paragraph> getParagraphIterator() {
		return getParagraphContainerImpl().getParagraphIterator();
	}

	// ****************Table support******************//
	protected TableContainer getTableContainerImpl() {
		if (tableContainerImpl == null) {
			tableContainerImpl = new TableContainerImpl();
		}
		return tableContainerImpl;
	}

	private class TableContainerImpl extends AbstractTableContainer {

		public OdfElement getTableContainerElement() {
			return mSectionElement;
		}
	}

//	@Override
	public Table addTable() {
		return getTableContainerImpl().addTable();
	}

//	@Override
	public Table addTable(int numRows, int numCols) {
		return getTableContainerImpl().addTable(numRows, numCols);
	}

//	@Override
	public TableBuilder getTableBuilder() {
		return getTableContainerImpl().getTableBuilder();
	}

//	@Override
	public Table getTableByName(String name) {
		return getTableContainerImpl().getTableByName(name);
	}

//	@Override
	public OdfElement getTableContainerElement() {
		return getTableContainerImpl().getTableContainerElement();
	}

//	@Override
	public List<Table> getTableList() {
		return getTableContainerImpl().getTableList();
	}

	// ****************List support******************//

	private ListContainerImpl getListContainerImpl() {
		if (listContainerImpl == null) {
			listContainerImpl = new ListContainerImpl();
		}
		return listContainerImpl;
	}

	private class ListContainerImpl extends AbstractListContainer {

		public OdfElement getListContainerElement() {
			return mSectionElement;
		}
	}

//	@Override
	public org.odftoolkit.simple.text.list.List addList() {
		return getListContainerImpl().addList();
	}

//	@Override
	public org.odftoolkit.simple.text.list.List addList(ListDecorator decorator) {
		return getListContainerImpl().addList(decorator);
	}

//	@Override
	public void clearList() {
		getListContainerImpl().clearList();
	}

//	@Override
	public OdfElement getListContainerElement() {
		return getListContainerImpl().getListContainerElement();
	}

//	@Override
	public Iterator<org.odftoolkit.simple.text.list.List> getListIterator() {
		return getListContainerImpl().getListIterator();
	}

//	@Override
	public boolean removeList(org.odftoolkit.simple.text.list.List list) {
		return getListContainerImpl().removeList(list);
	}

}
