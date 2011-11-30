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

package org.odftoolkit.odfdom.incubator.meta;

import org.odftoolkit.odfdom.dom.element.meta.MetaDocumentStatisticElement;

/**
 * <code>OdfMetaDocumentStatistic</code> feature specifies the statistics about
 * the document.
 * 
 * @deprecated As of release 0.8.8, replaced by {@link org.odftoolkit.simple.meta.DocumentStatistic} in Simple API.
 */
public class OdfMetaDocumentStatistic {

	private MetaDocumentStatisticElement mDocStatistic;

	/**
	 * Constructor of <code>OdfMetaDocumentStatistic</code> feature.
	 * 
	 * @param docStatistic
	 *            the <code>MetaDocumentStatisticElement</code> represent this
	 *            feature
	 */
	public OdfMetaDocumentStatistic(MetaDocumentStatisticElement docStatistic) {
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
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaCellCountAttribute</code> , See {@odf.attribute
	 *  meta:cell-count}.
	 * 
	 * @return the number of table cells contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getCellCount() {
		return mDocStatistic.getMetaCellCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaCellCountAttribute</code> , See {@odf.attribute
	 *  meta:cell-count}.
	 * 
	 * @param cellCount	the number of table cells need to be set
	 *            
	 */
	public void setCellCount(int cellCount) {
		mDocStatistic.setMetaCellCountAttribute(cellCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaCharacterCountAttribute</code> , See {@odf.attribute
	 *  meta:character-count}.
	 * 
	 * @return the number of characters contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getCharacterCount() {
		return mDocStatistic.getMetaCharacterCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaCharacterCountAttribute</code> , See {@odf.attribute
	 *  meta:character-count}.
	 * 
	 * @param characterCount	the number of characters need to set
	 */
	public void setCharacterCount(Integer characterCount) {
		mDocStatistic.setMetaCharacterCountAttribute(characterCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaDrawCountAttribute</code> , See {@odf.attribute
	 *  meta:draw-count}.
	 * 
	 * @return the number of all the graphic related element in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getDrawCount() {
		return mDocStatistic.getMetaDrawCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaDrawCountAttribute</code> , See {@odf.attribute
	 *  meta:draw-count}.
	 * 
	 * @param drawCount	the number of all the graphic related element need to set
	 */
	public void setDrawCount(Integer drawCount) {
		mDocStatistic.setMetaDrawCountAttribute(drawCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaFrameCountAttribute</code> , See {@odf.attribute
	 *  meta:frame-count}.
	 * 
	 * @return the number of <code><draw:frame></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getFrameCount() {
		return mDocStatistic.getMetaFrameCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaFrameCountAttribute</code> , See {@odf.attribute
	 *  meta:frame-count}.
	 * 
	 * @param frameCount the number of <code><draw:frame></code> element need to set
	 */
	public void setFrameCount(Integer frameCount) {
		mDocStatistic.setMetaFrameCountAttribute(frameCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaImageCountAttribute</code> , See {@odf.attribute
	 *  meta:image-count}.
	 * 
	 * @return the number of <code><draw:image></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getImageCount() {
		return mDocStatistic.getMetaImageCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaImageCountAttribute</code> , See {@odf.attribute
	 *  meta:image-count}.
	 * 
	 * @param imageCount the number of <code><draw:image></code> element need to set
	 */
	public void setImageCount(Integer imageCount) {
		mDocStatistic.setMetaImageCountAttribute(imageCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaNonWhitespaceCharacterCountAttribute</code> , See
	 * {@odf.attribute meta:non-whitespace-character-count}.
	 * 
	 * @return the number of non-whitespace characters contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getNonWhitespaceCharacterCount() {
		return mDocStatistic.getMetaNonWhitespaceCharacterCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaNonWhitespaceCharacterCountAttribute</code> , See
	 * {@odf.attribute meta:non-whitespace-character-count}.
	 * 
	 * @param nonWhitespaceCharacterCount	the number of non-whitespace characters need to set
	 */
	public void setNonWhitespaceCharacterCount(
			Integer nonWhitespaceCharacterCount) {
		mDocStatistic
				.setMetaNonWhitespaceCharacterCountAttribute(nonWhitespaceCharacterCount);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaObjectCountAttribute</code> , See {@odf.attribute
	 *  meta:object-count}
	 * 
	 * @return the number of <code><draw:object></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getObjectCount() {
		return mDocStatistic.getMetaObjectCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaObjectCountAttribute</code> , See {@odf.attribute
	 *  meta:object-count}.
	 * 
	 * @param objectCount the number of <code><draw:object></code> element need to set
	 */
	public void setObjectCount(Integer objectCount) {
		mDocStatistic.setMetaObjectCountAttribute(objectCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaOleObjectCountAttribute</code> , See {@odf.attribute
	 *  meta:ole-object-count}.
	 * 
	 * @return the number of <code><draw:object-ole></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getOleObjectCount() {
		return mDocStatistic.getMetaOleObjectCountAttribute();

	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaOleObjectCountAttribute</code> , See {@odf.attribute
	 *  meta:ole-object-count}.
	 * 
	 * @param oleObjectCount the number of <code><draw:object-ole></code> element need to set
	 */
	public void setOleObjectCount(Integer oleObjectCount) {
		mDocStatistic.setMetaOleObjectCountAttribute(oleObjectCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaPageCountAttribute</code> , See {@odf.attribute
	 *  meta:page-count}.
	 * 
	 * @return the number of page count contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getPageCount() {
		return mDocStatistic.getMetaPageCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaPageCountAttribute</code> , See {@odf.attribute
	 *  meta:page-count}.
	 * 
	 * @param pageCount the number of page count need to set
	 */
	public void setPageCount(Integer pageCount) {
		mDocStatistic.setMetaPageCountAttribute(pageCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaParagraphCountAttribute</code> , See {@odf.attribute
	 *  meta:paragraph-count}.
	 * 
	 * @return the number of <code><text:p></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getParagraphCount() {
		return mDocStatistic.getMetaParagraphCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaParagraphCountAttribute</code> , See {@odf.attribute
	 *  meta:paragraph-count}.
	 * 
	 * @param paragraphCount the number of <code><text:p></code> element need to set
	 */
	public void setParagraphCount(Integer paragraphCount) {
		mDocStatistic.setMetaParagraphCountAttribute(paragraphCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaRowCountAttribute</code> , See {@odf.attribute
	 * meta:row-count}.
	 * 
	 * @return the number of lines contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getRowCount() {
		return mDocStatistic.getMetaRowCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaRowCountAttribute</code> , See {@odf.attribute
	 * meta:row-count}.
	 * 
	 * @param rowCount the number of lines need to set
	 */
	public void setRowCount(Integer rowCount) {
		mDocStatistic.setMetaRowCountAttribute(rowCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaSentenceCountAttribute</code> , See {@odf.attribute
	 *  meta:sentence-count}.
	 * 
	 * @return the number of sentences contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getSentenceCount() {
		return mDocStatistic.getMetaSentenceCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaSentenceCountAttribute</code> , See {@odf.attribute
	 *  meta:sentence-count}.
	 * 
	 * @param sentenceCount the number of sentences need to set
	 */
	public void setSentenceCount(Integer sentenceCount) {
		mDocStatistic.setMetaSentenceCountAttribute(sentenceCount);
	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaSyllableCountAttribute</code> , See {@odf.attribute
	 *  meta:syllable-count}.
	 * 
	 * @return the number of syllables contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getSyllableCount() {
		return mDocStatistic.getMetaSyllableCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaSyllableCountAttribute</code> , See {@odf.attribute
	 *  meta:syllable-count}.
	 * 
	 * @param syllableCount the number of syllables need to set
	 */
	public void setSyllableCount(Integer syllableCount) {
		mDocStatistic.setMetaSyllableCountAttribute(syllableCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaTableCountAttribute</code> , See {@odf.attribute
	 *  meta:table-count}.
	 * 
	 * @return the number of <code><table:table></code> element contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getTableCount() {
		return mDocStatistic.getMetaTableCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaTableCountAttribute</code> , See {@odf.attribute
	 *  meta:table-count}.
	 * 
	 * @param tableCount the number of <code><table:table></code> need to set
	 */
	public void setTableCount(Integer tableCount) {
		mDocStatistic.setMetaTableCountAttribute(tableCount);

	}

	/**
	 * Receives the value of the ODFDOM attribute representation
	 * <code>MetaWordCountAttribute</code> , See {@odf.attribute
	 *  meta:word-count}.
	 * 
	 * @return the number of words contained in the current document;
	 * <p>
	 * <code>null</code>, if the attribute is not set and no default value defined.
	 */
	public Integer getWordCount() {
		return mDocStatistic.getMetaWordCountAttribute();
	}

	/**
	 * Sets the value of ODFDOM attribute representation
	 * <code>MetaWordCountAttribute</code> , See {@odf.attribute
	 *  meta:word-count}.
	 * 
	 * @param wordCount the number of words need to set
	 */
	public void setWordCount(Integer wordCount) {
		mDocStatistic.setMetaWordCountAttribute(wordCount);

	}

}
