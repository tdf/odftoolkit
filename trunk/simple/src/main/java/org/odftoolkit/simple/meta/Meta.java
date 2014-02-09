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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.odftoolkit.odfdom.dom.element.dc.DcCreatorElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDateElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDescriptionElement;
import org.odftoolkit.odfdom.dom.element.dc.DcLanguageElement;
import org.odftoolkit.odfdom.dom.element.dc.DcSubjectElement;
import org.odftoolkit.odfdom.dom.element.dc.DcTitleElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaCreationDateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaDocumentStatisticElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaEditingCyclesElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaEditingDurationElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaGeneratorElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaInitialCreatorElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaKeywordElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaPrintDateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaPrintedByElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentMetaElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMetaElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Duration;

/**
 * <code>Meta</code> represent the meta data feature in the ODF document.
 * <p>
 * It provides convenient method to get meta data info.
 * 
 */
public class Meta {

	private OfficeMetaElement mOfficeMetaElement;

	/**
	 * Constructor of <code>Meta</code> feature.
	 * 
	 * @param metaDom	the file DOM element of meta.xml
	 */
	public Meta(OdfFileDom metaDom) {
		OfficeDocumentMetaElement metaEle = OdfElement.findFirstChildNode(OfficeDocumentMetaElement.class, metaDom);
		mOfficeMetaElement = OdfElement.findFirstChildNode(OfficeMetaElement.class, metaEle);
	}

	/**
	 * Get the instance of <code>OfficeMetaElement</code> which represents this feature.
	 * 
	 * @return the instance of OfficeMetaElement
	 */
	public OfficeMetaElement getOfficeMetaElement() {
		return mOfficeMetaElement;
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaGeneratorElement</code>.
	 * 
	 * @return the generator info of the current document.
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaGeneratorElement.
	 */
	public String getGenerator() {
		MetaGeneratorElement metaGenerator = OdfElement.findFirstChildNode(
				MetaGeneratorElement.class, mOfficeMetaElement);
		if (metaGenerator != null) {
			return metaGenerator.getTextContent();
		}
		return null;

	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaGeneratorElement</code>.
	 * 
	 * @param generator	set the specified document generator.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaGeneratorElement.
	 */
	public void setGenerator(String generator) {
		MetaGeneratorElement metaGenerator = OdfElement.findFirstChildNode(
				MetaGeneratorElement.class, mOfficeMetaElement);
		if (metaGenerator == null) {
			metaGenerator = mOfficeMetaElement.newMetaGeneratorElement();
		}
		metaGenerator.setTextContent(generator);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>DcTitleElement</code>.
	 * 
	 * @return the title of the current document.
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcTitleElement.
	 */
	public String getTitle() {

		DcTitleElement titleEle = OdfElement.findFirstChildNode(
				DcTitleElement.class, mOfficeMetaElement);
		if (titleEle != null) {
			return titleEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>DcTitleElement</code>.
	 * 
	 * @param title set the specified document title
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcTitleElement.
	 */
	public void setTitle(String title) {
		DcTitleElement titleEle = OdfElement.findFirstChildNode(
				DcTitleElement.class, mOfficeMetaElement);
		if (titleEle == null) {
			titleEle = mOfficeMetaElement.newDcTitleElement();
		}
		titleEle.setTextContent(title);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>DcDescriptionElement</code>.
	 * 
	 * @return the description of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcDescriptionElement.
	 */
	public String getDescription() {
		DcDescriptionElement descEle = OdfElement.findFirstChildNode(
				DcDescriptionElement.class, mOfficeMetaElement);
		if (descEle != null) {
			return descEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>DcDescriptionElement</code>.
	 *
	 * @param description set the specified document description 
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcDescriptionElement.
	 */
	public void setDescription(String description) {
		DcDescriptionElement descEle = OdfElement.findFirstChildNode(
				DcDescriptionElement.class, mOfficeMetaElement);
		if (descEle == null) {
			descEle = mOfficeMetaElement.newDcDescriptionElement();
		}
		descEle.setTextContent(description);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>DcSubjectElement</code>.
	 * 
	 * @return the subject of the current document.
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcSubjectElement.
	 */
	public String getSubject() {
		DcSubjectElement subjectEle = OdfElement.findFirstChildNode(
				DcSubjectElement.class, mOfficeMetaElement);
		if (subjectEle != null) {
			return subjectEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>DcSubjectElement</code>.
	 * 
	 * @param subject set the specified document subject.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcSubjectElement.
	 */
	public void setSubject(String subject) {
		DcSubjectElement subjectEle = OdfElement.findFirstChildNode(
				DcSubjectElement.class, mOfficeMetaElement);
		if (subjectEle == null) {
			subjectEle = mOfficeMetaElement.newDcSubjectElement();
		}
		subjectEle.setTextContent(subject);
	}

	/**
	 * Receives the list value of the odf dom element representation
	 * <code>MetaKeywordElement</code>.
	 * 
	 * @return the keywords of the current document.
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaKeywordElement.
	 */
	public List<String> getKeywords() {
		List<String> keywords = new ArrayList<String>();
		MetaKeywordElement keywordEle = OdfElement.findFirstChildNode(
				MetaKeywordElement.class, mOfficeMetaElement);
		if (keywordEle != null) {
			keywords.add(keywordEle.getTextContent());
			MetaKeywordElement keywordNext;
			while ((keywordNext = OdfElement.findNextChildNode(
					MetaKeywordElement.class, keywordEle)) != null) {
				keywords.add(keywordNext.getTextContent());
				keywordEle = keywordNext;
			}
			return keywords;
		} else {
			return null;
		}

	}

	/**
	 * Sets the list value of the odf dom element representation
	 * <code>MetaKeywordElement</code>.
	 * 
	 * @param keyList set the specified list of keywords.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaKeywordElement.
	 */
	public void setKeywords(List<String> keyList) {
		MetaKeywordElement keywordEle = OdfElement.findFirstChildNode(
				MetaKeywordElement.class, mOfficeMetaElement);
		List<MetaKeywordElement> toBeDeleted = new ArrayList<MetaKeywordElement>();
		if (keywordEle != null) {
			MetaKeywordElement keywordTmp;
			toBeDeleted.add(keywordEle);
			while ((keywordTmp = OdfElement.findNextChildNode(
					MetaKeywordElement.class, keywordEle)) != null) {
				keywordEle = keywordTmp;
				toBeDeleted.add(keywordTmp);
			}

		}

		// remove the original
		for (MetaKeywordElement keyele : toBeDeleted) {
			mOfficeMetaElement.removeChild(keyele);
		}
		// add new
		for (int i = 0; i < keyList.size(); i++) {
			MetaKeywordElement keywordElement = mOfficeMetaElement.newMetaKeywordElement();
			keywordElement.setTextContent(keyList.get(i));
		}

	}

	/**
	 * Add the keyword to the current document.
	 * Create child element <code>MetaKeywordElement</code>.
	 * 
	 * @param keyword	the value of child element <code>MetaKeywordElement</code>.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaKeywordElement.
	 */
	public void addKeyword(String keyword) {
		MetaKeywordElement keywordElement = mOfficeMetaElement.newMetaKeywordElement();
		keywordElement.setTextContent(keyword);
	}

	/**
	 * Receives the list value of the odf dom element representation
	 * <code>MetaUserDefinedElement</code>.
	 * 
	 * @return get the list of user-defined metadata names;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public List<String> getUserDefinedDataNames() {
		List<String> definedNames = new ArrayList<String>();
		MetaUserDefinedElement definedEle = OdfElement.findFirstChildNode(
				MetaUserDefinedElement.class, mOfficeMetaElement);
		if (definedEle != null) {

			definedNames.add(definedEle.getMetaNameAttribute());
			MetaUserDefinedElement definedNext;
			while ((definedNext = OdfElement.findNextChildNode(
					MetaUserDefinedElement.class, definedEle)) != null) {

				definedNames.add(definedNext.getMetaNameAttribute());

				definedEle = definedNext;
			}
			return definedNames;
		} else {
			return null;
		}
	}

	/**
	 * Receives the odf dom element representation
	 * <code>MetaUserDefinedElement</code> by attribute name.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @return	the <code>MetaUserDefinedElement</code> which is identified by the specified name;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public MetaUserDefinedElement getUserDefinedElementByAttributeName(
			String name) {
		MetaUserDefinedElement definedEle = OdfElement.findFirstChildNode(
				MetaUserDefinedElement.class, mOfficeMetaElement);
		if (definedEle != null) {
			if (definedEle.getMetaNameAttribute().equals(name)) {
				return definedEle;
			}
			MetaUserDefinedElement definedNext;
			while ((definedNext = OdfElement.findNextChildNode(
					MetaUserDefinedElement.class, definedEle)) != null) {

				if (definedNext.getMetaNameAttribute().equals(name)) {
					return definedNext;
				}

				definedEle = definedNext;
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaUserDefinedElement</code> by attribute name.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @return the value of the user-defined metadata with the specified name;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public String getUserDefinedDataValue(String name) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			return definedElement.getTextContent();
		}
		return null;
	}

	/**
	 * Receives the data type of the odf dom element representation
	 * <code>MetaUserDefinedElement</code> by attribute name.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @return the data type of the user-defined metadata with the specified name;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public String getUserDefinedDataType(String name) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			return definedElement.getMetaValueTypeAttribute();
		}
		return null;
	}

	/**
	 * Remove the odf dom element representation
	 * <code>MetaUserDefinedElement</code> by attribute name.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public void removeUserDefinedDataByName(String name) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			mOfficeMetaElement.removeChild(definedElement);
		}

	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaUserDefinedElement</code> by attribute name.
	 * 
	 * @param name	the name need to set for the user-defined metadata
	 * @param value  the value need to set for the user-defined metadata
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public void setUserDefinedDataValue(String name, String value) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			definedElement.setTextContent(value);
		}

	}

	/**
	 * Sets the data type of the odf dom element representation
	 * <code>MetaUserDefinedElement</code> by attribute name.
	 * 
	 * @param name	the name need to set for the user-defined metadata
	 * @param value  the value need to set for the user-defined metadata
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public void setUserDefinedDataType(String name, String value) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			definedElement.setMetaValueTypeAttribute(value);
		}

	}

	/**
	 * Sets the odf dom element representation
	 * <code>MetaUserDefinedElement</code>, if the element with the attribute name exists,then
	 * update;or create a new element if type or value is null,the original will
	 * not be updated.
	 * 
	 * @param name	the name need to set for the user-defined metadata
	 * @param type	the data type need to set for the user-defined metadata
	 * @param value	the value need to set for the user-defined metadata
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement.
	 */
	public void setUserDefinedData(String name, String type, String value) {
		// test if name exists
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		// if exists, then update,if parameter is null, then don't change
		if (definedElement != null) {
			if (type != null) {
				definedElement.setMetaValueTypeAttribute(type);
			}
			if (value != null) {
				definedElement.setTextContent(value);
			}
		} else {
			// if not exists, add
			definedElement = mOfficeMetaElement.newMetaUserDefinedElement(name,
					type);
			definedElement.setTextContent(value);

		}

	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaInitialCreatorElement</code>.
	 * 
	 * @return get the initial creator of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaInitialCreatorElement.
	 */
	public String getInitialCreator() {
		MetaInitialCreatorElement iniCreatorEle = OdfElement.findFirstChildNode(MetaInitialCreatorElement.class,
				mOfficeMetaElement);
		if (iniCreatorEle != null) {
			return iniCreatorEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaInitialCreatorElement</code>.
	 * 
	 * @param initialCreator set the specified initial creator
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaInitialCreatorElement.
	 */
	public void setInitialCreator(String initialCreator) {
		MetaInitialCreatorElement iniCreatorEle = OdfElement.findFirstChildNode(MetaInitialCreatorElement.class,
				mOfficeMetaElement);
		if (iniCreatorEle == null) {
			iniCreatorEle = mOfficeMetaElement.newMetaInitialCreatorElement();
		}
		iniCreatorEle.setTextContent(initialCreator);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>DcCreatorElement</code>.
	 * 
	 * @return the creator of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcCreatorElement.
	 */
	public String getCreator() {
		DcCreatorElement creatorEle = OdfElement.findFirstChildNode(
				DcCreatorElement.class, mOfficeMetaElement);
		if (creatorEle != null) {
			return creatorEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>DcCreatorElement</code>.
	 * 
	 * @param creator set the specified creator
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcCreatorElement.
	 */
	public void setCreator(String creator) {
		DcCreatorElement creatorEle = OdfElement.findFirstChildNode(
				DcCreatorElement.class, mOfficeMetaElement);
		if (creatorEle == null) {
			creatorEle = mOfficeMetaElement.newDcCreatorElement();
		}
		creatorEle.setTextContent(creator);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaPrintedByElement</code>
	 * 
	 * @return the name of the last person who printed the current document;
	 * <p>
	 * <code>null</code>, if element is not set
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaPrintedByElement.
	 */
	public String getPrintedBy() {
		MetaPrintedByElement printedByEle = OdfElement.findFirstChildNode(
				MetaPrintedByElement.class, mOfficeMetaElement);
		if (printedByEle != null) {
			return printedByEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaPrintedByElement</code>.
	 * 
	 * @param printedBy	the name need to set for the last person who printed the current document
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaPrintedByElement.
	 */
	public void setPrintedBy(String printedBy) {
		MetaPrintedByElement printedByEle = OdfElement.findFirstChildNode(
				MetaPrintedByElement.class, mOfficeMetaElement);
		if (printedByEle == null) {
			printedByEle = mOfficeMetaElement.newMetaPrintedByElement();
		}
		printedByEle.setTextContent(printedBy);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaCreationDateElement</code>
	 * 
	 * @return the date and time when the document was created initially;
	 * <p>
	 * <code>null</code>, if element is not set
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaCreationDateElement.
	 */
	public Calendar getCreationDate() {
		MetaCreationDateElement creationDateEle = OdfElement.findFirstChildNode(MetaCreationDateElement.class,
				mOfficeMetaElement);
		if (creationDateEle != null) {
			return stringToCalendar(creationDateEle.getTextContent());
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaCreationDateElement</code> .
	 * 
	 * @param creationDate	the date and time need to set
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaCreationDateElement.
	 */
	public void setCreationDate(Calendar creationDate) {
		MetaCreationDateElement creationDateEle = OdfElement.findFirstChildNode(MetaCreationDateElement.class,
				mOfficeMetaElement);
		if (creationDateEle == null) {
			creationDateEle = mOfficeMetaElement.newMetaCreationDateElement();
		}
		creationDateEle.setTextContent(calendarToString(creationDate));
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>DcDateElement</code>.
	 * 
	 * @return the date and time when the document was last modified;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcDateElement.
	 */
	public Calendar getDcdate() {
		DcDateElement dcDateEle = OdfElement.findFirstChildNode(
				DcDateElement.class, mOfficeMetaElement);
		if (dcDateEle != null) {
			return stringToCalendar(dcDateEle.getTextContent());
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>DcDateElement</code>.
	 * 
	 * @param dcdate	the date and time need to set
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcDateElement.
	 */
	public void setDcdate(Calendar dcdate) {
		DcDateElement dcDateEle = OdfElement.findFirstChildNode(
				DcDateElement.class, mOfficeMetaElement);
		if (dcDateEle == null) {
			dcDateEle = mOfficeMetaElement.newDcDateElement();
		}
		dcDateEle.setTextContent(calendarToString(dcdate));
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaPrintDateElement</code>.
	 * 
	 * @return the date and time when the document was last printed;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaPrintDateElement
	 */
	public Calendar getPrintDate() {
		MetaPrintDateElement printDateEle = OdfElement.findFirstChildNode(
				MetaPrintDateElement.class, mOfficeMetaElement);
		if (printDateEle != null) {
			return stringToCalendar(printDateEle.getTextContent());
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaPrintDateElement</code>.
	 * 
	 * @param printDate	the date and time need to set
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaPrintDateElement
	 */
	public void setPrintDate(Calendar printDate) {
		MetaPrintDateElement printDateEle = OdfElement.findFirstChildNode(
				MetaPrintDateElement.class, mOfficeMetaElement);
		if (printDateEle == null) {
			printDateEle = mOfficeMetaElement.newMetaPrintDateElement();
		}
		printDateEle.setTextContent(calendarToString(printDate));
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>DcLanguageElement</code>.
	 * 
	 * @return the default language of the document;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcLanguageElement
	 */
	public String getLanguage() {
		DcLanguageElement languageEle = OdfElement.findFirstChildNode(
				DcLanguageElement.class, mOfficeMetaElement);
		if (languageEle != null) {
			return languageEle.getTextContent();
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>DcLanguageElement</code>.
	 * 
	 * @param language the default language need to set fo the current document
	 * @see org.odftoolkit.odfdom.dom.element.meta.DcLanguageElement
	 */
	public void setLanguage(String language) {
		DcLanguageElement languageEle = OdfElement.findFirstChildNode(
				DcLanguageElement.class, mOfficeMetaElement);
		if (languageEle == null) {
			languageEle = mOfficeMetaElement.newDcLanguageElement();
		}
		languageEle.setTextContent(language);
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaEditingCyclesElement</code> .
	 * 
	 * @return the number of times that the document has been edited;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaEditingCyclesElement
	 */
	public Integer getEditingCycles() {
		MetaEditingCyclesElement editingCyclesEle = OdfElement.findFirstChildNode(MetaEditingCyclesElement.class,
				mOfficeMetaElement);
		if (editingCyclesEle != null) {
			return Integer.valueOf(editingCyclesEle.getTextContent());
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaEditingCyclesElement</code> .
	 * 
	 * @param editingCycles	set the specified edit times
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaEditingCyclesElement
	 */
	public void setEditingCycles(Integer editingCycles) {
		MetaEditingCyclesElement editingCyclesEle = OdfElement.findFirstChildNode(MetaEditingCyclesElement.class,
				mOfficeMetaElement);
		if (editingCyclesEle == null) {
			editingCyclesEle = mOfficeMetaElement.newMetaEditingCyclesElement();
		}
		editingCyclesEle.setTextContent(String.valueOf(editingCycles));
	}

	/**
	 * Receives the value of the odf dom element representation
	 * <code>MetaEditingDurationElement</code>.
	 * 
	 * @return the total time spent editing the document;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaEditingDurationElement
	 */
	public Duration getEditingDuration() {
		MetaEditingDurationElement editiingDurationEle = OdfElement.findFirstChildNode(MetaEditingDurationElement.class,
				mOfficeMetaElement);
		if (editiingDurationEle != null) {
			return Duration.valueOf(editiingDurationEle.getTextContent());
		}
		return null;
	}

	/**
	 * Sets the value of the odf dom element representation
	 * <code>MetaEditingDurationElement</code>.
	 * 
	 * @param editingDuration the time need to set
	 * @see org.odftoolkit.odfdom.dom.element.meta.MetaEditingDurationElement
	 */
	public void setEditingDuration(Duration editingDuration) {
		MetaEditingDurationElement editiingDurationEle = OdfElement.findFirstChildNode(MetaEditingDurationElement.class,
				mOfficeMetaElement);
		if (editiingDurationEle == null) {
			editiingDurationEle = mOfficeMetaElement.newMetaEditingDurationElement();
		}
		editiingDurationEle.setTextContent(editingDuration.toString());

	}

	/**
	 * Receives the sub feature of DocumentStatistic.
	 * 
	 * @return the statistics about the document which can be represented by 
	 * <code>DocumentStatistic</code> feature;
	 * <p>
	 * <code>null</code>, if the feature is not exist.
	 * @see org.odftoolkit.odfdom.dom.element.meta.DocumentStatistic
	 */
	public DocumentStatistic getDocumentStatistic() {
		MetaDocumentStatisticElement element = getDocumentStatisticElement();
		if (element != null) {
			return new DocumentStatistic(element);
		} else {
			return null;
		}
	}
	
	private MetaDocumentStatisticElement getDocumentStatisticElement() {
		return OdfElement.findFirstChildNode(
				MetaDocumentStatisticElement.class, mOfficeMetaElement);
	}

	/**
	 * Change valid string of calendar to Calendar type.
	 * 
	 * @param baseDate the string of a calender
	 * @return the object of Calender
	 */
	private Calendar stringToCalendar(String baseDate) {
		// Calendar calendar=new GregorianCalendar();
		Calendar calendar = Calendar.getInstance();
		Date d1 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		try {
			d1 = sdf.parse(baseDate);
		} catch (Exception e) {
			// invalid format or null value in meta.xml
			// d1=new Date();
		}

		calendar.setTime(d1);
		return calendar;
	}

	/**
	 * Convert a <code>Canlender</code> object to <code>String</code> object.
	 * @param calendar an instanceof <code>Canlender</code>
	 * @return the String format(yyyy-MM-dd'T'HH:mm:ss) of Calendar.
	 */
	private String calendarToString(Calendar calendar) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(calendar.getTime());
	}
}
