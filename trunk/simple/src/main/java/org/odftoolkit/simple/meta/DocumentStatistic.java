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

package org.odftoolkit.simple.meta;

import org.odftoolkit.odfdom.dom.element.meta.MetaDocumentStatisticElement;

/**
 * <code>DocumentStatistic</code> feature specifies the statistics about
 * the document.
 * 
 */
public class DocumentStatistic {

	private MetaDocumentStatisticElement mDocStatistic;

	/**
	 * Constructor of <code>DocumentStatistic</code> feature.
	 * 
	 * @param docStatistic
	 *            the <code>MetaDocumentStatisticElement</code> represent this
	 *            feature
	 */
	public DocumentStatistic(MetaDocumentStatisticElement docStatistic) {
		this.mDocStatistic = docStatistic;
	}

	/**
	 * Get the instance of <code>MetaDocumentStatisticElement</code> which represents this
	 * feature.
	 * 
	 * @return an instance of <code>MetaDocumentStatisticElement</code>
	 */
	private MetaDocumentStatisticElement getMetaDocumentStatisticElement() {
		return mDocStatistic;
	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaCellCountAttribute</code>.
	 * 
	 * @return the number of table cells contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaCellCountAttribute
	 */
	public Integer getCellCount() {
		return mDocStatistic.getMetaCellCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaCellCountAttribute</code> .
	 * 
	 * @param cellCount	the number of table cells need to be set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaCellCountAttribute
	 *            
	 */
	public void setCellCount(int cellCount) {
		mDocStatistic.setMetaCellCountAttribute(cellCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaCharacterCountAttribute</code> .
	 * 
	 * @return the number of characters contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaCharacterCountAttribute
	 */
	public Integer getCharacterCount() {
		return mDocStatistic.getMetaCharacterCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaCharacterCountAttribute</code>.
	 * 
	 * @param characterCount	the number of characters need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaCharacterCountAttribute
	 */
	public void setCharacterCount(Integer characterCount) {
		mDocStatistic.setMetaCharacterCountAttribute(characterCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaDrawCountAttribute</code> .
	 * 
	 * @return the number of all the graphic related element in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaDrawCountAttribute
	 */
	public Integer getDrawCount() {
		return mDocStatistic.getMetaDrawCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaDrawCountAttribute</code>.
	 * 
	 * @param drawCount	the number of all the graphic related element need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaDrawCountAttribute
	 */
	public void setDrawCount(Integer drawCount) {
		mDocStatistic.setMetaDrawCountAttribute(drawCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaFrameCountAttribute</code>.
	 * 
	 * @return the number of <code><draw:frame></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaFrameCountAttribute
	 */
	public Integer getFrameCount() {
		return mDocStatistic.getMetaFrameCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaFrameCountAttribute</code> .
	 * 
	 * @param frameCount the number of <code><draw:frame></code> element need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaFrameCountAttribute
	 */
	public void setFrameCount(Integer frameCount) {
		mDocStatistic.setMetaFrameCountAttribute(frameCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaImageCountAttribute</code> .
	 * 
	 * @return the number of <code><draw:image></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaImageCountAttribute
	 */
	public Integer getImageCount() {
		return mDocStatistic.getMetaImageCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaImageCountAttribute</code> .
	 * 
	 * @param imageCount the number of <code><draw:image></code> element need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaImageCountAttribute
	 */
	public void setImageCount(Integer imageCount) {
		mDocStatistic.setMetaImageCountAttribute(imageCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaNonWhitespaceCharacterCountAttribute</code>.
	 * 
	 * @return the number of non-whitespace characters contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaNonWhitespaceCharacterCountAttribute
	 */
	public Integer getNonWhitespaceCharacterCount() {
		return mDocStatistic.getMetaNonWhitespaceCharacterCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaNonWhitespaceCharacterCountAttribute</code>.
	 * 
	 * @param nonWhitespaceCharacterCount	the number of non-whitespace characters need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaNonWhitespaceCharacterCountAttribute
	 */
	public void setNonWhitespaceCharacterCount(
			Integer nonWhitespaceCharacterCount) {
		mDocStatistic
				.setMetaNonWhitespaceCharacterCountAttribute(nonWhitespaceCharacterCount);
	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaObjectCountAttribute</code>
	 * 
	 * @return the number of <code><draw:object></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaObjectCountAttribute
	 */
	public Integer getObjectCount() {
		return mDocStatistic.getMetaObjectCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaObjectCountAttribute</code>.
	 * 
	 * @param objectCount the number of <code><draw:object></code> element need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaObjectCountAttribute
	 */
	public void setObjectCount(Integer objectCount) {
		mDocStatistic.setMetaObjectCountAttribute(objectCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaOleObjectCountAttribute</code>.
	 * 
	 * @return the number of <code><draw:object-ole></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaOleObjectCountAttribute
	 */
	public Integer getOleObjectCount() {
		return mDocStatistic.getMetaOleObjectCountAttribute();

	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaOleObjectCountAttribute</code>.
	 * 
	 * @param oleObjectCount the number of <code><draw:object-ole></code> element need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaOleObjectCountAttribute
	 */
	public void setOleObjectCount(Integer oleObjectCount) {
		mDocStatistic.setMetaOleObjectCountAttribute(oleObjectCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaPageCountAttribute</code>.
	 * 
	 * @return the number of page count contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaPageCountAttribute
	 */
	public Integer getPageCount() {
		return mDocStatistic.getMetaPageCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaPageCountAttribute</code>.
	 * 
	 * @param pageCount the number of page count need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaPageCountAttribute
	 */
	public void setPageCount(Integer pageCount) {
		mDocStatistic.setMetaPageCountAttribute(pageCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaParagraphCountAttribute</code>.
	 * 
	 * @return the number of <code><text:p></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaParagraphCountAttribute
	 */
	public Integer getParagraphCount() {
		return mDocStatistic.getMetaParagraphCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaParagraphCountAttribute</code>.
	 * 
	 * @param paragraphCount the number of <code><text:p></code> element need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaParagraphCountAttribute
	 */
	public void setParagraphCount(Integer paragraphCount) {
		mDocStatistic.setMetaParagraphCountAttribute(paragraphCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaRowCountAttribute</code>.
	 * 
	 * @return the number of lines contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaRowCountAttribute
	 */
	public Integer getRowCount() {
		return mDocStatistic.getMetaRowCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaRowCountAttribute</code>.
	 * 
	 * @param rowCount the number of lines need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaRowCountAttribute
	 */
	public void setRowCount(Integer rowCount) {
		mDocStatistic.setMetaRowCountAttribute(rowCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaSentenceCountAttribute</code>.
	 * 
	 * @return the number of sentences contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaSentenceCountAttribute.
	 */
	public Integer getSentenceCount() {
		return mDocStatistic.getMetaSentenceCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaSentenceCountAttribute</code>.
	 * 
	 * @param sentenceCount the number of sentences need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaSentenceCountAttribute.
	 */
	public void setSentenceCount(Integer sentenceCount) {
		mDocStatistic.setMetaSentenceCountAttribute(sentenceCount);
	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaSyllableCountAttribute</code>.
	 * 
	 * @return the number of syllables contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaSyllableCountAttribute.
	 */
	public Integer getSyllableCount() {
		return mDocStatistic.getMetaSyllableCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaSyllableCountAttribute</code>.
	 * 
	 * @param syllableCount the number of syllables need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaSyllableCountAttribute.
	 */
	public void setSyllableCount(Integer syllableCount) {
		mDocStatistic.setMetaSyllableCountAttribute(syllableCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaTableCountAttribute</code>.
	 * 
	 * @return the number of <code><table:table></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaTableCountAttribute.
	 */
	public Integer getTableCount() {
		return mDocStatistic.getMetaTableCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaTableCountAttribute</code>.
	 * 
	 * @param tableCount the number of <code><table:table></code> need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaTableCountAttribute.
	 */
	public void setTableCount(Integer tableCount) {
		mDocStatistic.setMetaTableCountAttribute(tableCount);

	}

	/**
	 * Receives the value of the odf dom attribute representation
	 * <code>MetaWordCountAttribute</code>.
	 * 
	 * @return the number of words contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaWordCountAttribute.
	 */
	public Integer getWordCount() {
		return mDocStatistic.getMetaWordCountAttribute();
	}

	/**
	 * Sets the value of odf dom attribute representation
	 * <code>MetaWordCountAttribute</code>.
	 * 
	 * @param wordCount the number of words need to set
	 * @see org.odftoolkit.odfdom.dom.attribute.meta.MetaWordCountAttribute.
	 */
	public void setWordCount(Integer wordCount) {
		mDocStatistic.setMetaWordCountAttribute(wordCount);

	}

}
