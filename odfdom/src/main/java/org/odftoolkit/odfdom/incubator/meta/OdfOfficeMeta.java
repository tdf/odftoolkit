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
import org.odftoolkit.odfdom.dom.element.meta.MetaAutoReloadElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaCreationDateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaDocumentStatisticElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaEditingCyclesElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaEditingDurationElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaGeneratorElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaHyperlinkBehaviourElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaInitialCreatorElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaKeywordElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaPrintDateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaPrintedByElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaTemplateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentMetaElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMetaElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.Duration;

/**
 * <code>OdfOfficeMeta</code> represent the meta data feature in the ODF document.
 * <p>
 * It provides convenient method to get meta data info.
 * 
 * @deprecated As of release 0.8.8, replaced by {@link org.odftoolkit.simple.meta.Meta} in Simple API.
 */
public class OdfOfficeMeta {

	private OfficeMetaElement mOfficeMetaElement;
	private boolean mAutomaticUpdate = true;

	/**
	 * Constructor of <code>OdfOfficeMeta</code> feature.
	 *
	 * @param metaDom	the file DOM element of meta.xml
	 */
	public OdfOfficeMeta(OdfFileDom metaDom) {
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
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaGeneratorElement</code> , See {@odf.element
	 * meta:generator}.
	 * 
	 * @return the generator info of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaGeneratorElement</code> , See {@odf.element
	 * meta:generator}.
	 * 
	 * @param generator	set the specified document generator. NULL will remove the element from the meta.xml.
	 */
	public void setGenerator(String generator) {
		MetaGeneratorElement metaGeneratorEle = OdfElement.findFirstChildNode(
				MetaGeneratorElement.class, mOfficeMetaElement);
		if (generator == null) {
			if (metaGeneratorEle != null) {
				mOfficeMetaElement.removeChild(metaGeneratorEle);
			}
		} else {
			if (metaGeneratorEle == null) {
				metaGeneratorEle = mOfficeMetaElement.newMetaGeneratorElement();
			}
			metaGeneratorEle.setTextContent(generator);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>DcTitleElement</code> , See {@odf.element dc:title}.
	 * 
	 * @return the title of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>DcTitleElement</code> , See {@odf.element dc:title}.
	 * 
	 * @param title set the specified document title. NULL will remove the element from the meta.xml.
	 */
	public void setTitle(String title) {
		DcTitleElement titleEle = OdfElement.findFirstChildNode(
				DcTitleElement.class, mOfficeMetaElement);
		if (title == null) {
			if (titleEle != null) {
				mOfficeMetaElement.removeChild(titleEle);
			}
		} else {
			if (titleEle == null) {
				titleEle = mOfficeMetaElement.newDcTitleElement();
			}
			titleEle.setTextContent(title);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>DcDescriptionElement</code> , See {@odf.element
	 * dc:description}.
	 * 
	 * @return the description of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>DcDescriptionElement</code> , See {@odf.element
	 * dc:description}.
	 * 
	 * @param description set the specified document description. NULL will remove the element from the meta.xml.
	 */
	public void setDescription(String description) {
		DcDescriptionElement descEle = OdfElement.findFirstChildNode(
				DcDescriptionElement.class, mOfficeMetaElement);
		if (description == null) {
			if (descEle != null) {
				mOfficeMetaElement.removeChild(descEle);
			}
		} else {
			if (descEle == null) {
				descEle = mOfficeMetaElement.newDcDescriptionElement();
			}
			descEle.setTextContent(description);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>DcSubjectElement</code> , See {@odf.element
	 * dc:subject}.
	 * 
	 * @return the subject of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>DcSubjectElement</code> , See {@odf.element
	 * dc:subject}.
	 * 
	 * @param subject set the specified document subject. NULL will remove the element from the meta.xml.
	 */
	public void setSubject(String subject) {
		DcSubjectElement subjectEle = OdfElement.findFirstChildNode(
				DcSubjectElement.class, mOfficeMetaElement);
		if (subject == null) {
			if (subjectEle != null) {
				mOfficeMetaElement.removeChild(subjectEle);
			}
		} else {
			if (subjectEle == null) {
				subjectEle = mOfficeMetaElement.newDcSubjectElement();
			}
			subjectEle.setTextContent(subject);
		}
	}

	/**
	 * Receives the list value of the ODFDOM element representation
	 * <code>MetaKeywordElement</code> , See {@odf.element
	 * meta:keyword}.
	 * 
	 * @return the keywords of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the list value of the ODFDOM element representation
	 * <code>MetaKeywordElement</code> , See {@odf.element
	 * meta:keyword}.
	 * 
	 * @param keyList set the specified list of keywords
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
	 * Create child element {@odf.element meta:keyword}.
	 * 
	 * @param keyword	the value of child element {@odf.element
	 *            meta:keyword}.
	 */
	public void addKeyword(String keyword) {
		MetaKeywordElement keywordElement = mOfficeMetaElement.newMetaKeywordElement();
		keywordElement.setTextContent(keyword);
	}

	/**
	 * Receives the list value of the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> , See {@odf.element
	 * meta:user-defined}.
	 * 
	 * @return get the list of user-defined metadata names;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Receives the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> by attribute name, See {@odf.element
	 *  meta:user-defined}.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @return	the <code>MetaUserDefinedElement</code> which is identified by the specified name;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> by attribute name, See {@odf.element
	 *  meta:user-defined}.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @return the value of the user-defined metadata with the specified name;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 */
	public String getUserDefinedDataValue(String name) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			return definedElement.getTextContent();
		}
		return null;
	}

	/**
	 * Receives the data type of the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> by attribute name, See {@odf.element
	 *  meta:user-defined}.
	 * 
	 * @param name	the name of the user-defined metadata
	 * @return the data type of the user-defined metadata with the specified name;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 */
	public String getUserDefinedDataType(String name) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			return definedElement.getMetaValueTypeAttribute();
		}
		return null;
	}

	/**
	 * Remove the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> by attribute name, See {@odf.element
	 *  meta:user-defined}.
	 * 
	 * @param name	the name of the user-defined metadata
	 */
	public void removeUserDefinedDataByName(String name) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			mOfficeMetaElement.removeChild(definedElement);
		}

	}

	/**
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> by attribute name, See {@odf.element
	 *  meta:user-defined}.
	 * 
	 * @param name	the name need to set for the user-defined metadata
	 * @param value  the value need to set for the user-defined metadata
	 */
	public void setUserDefinedDataValue(String name, String value) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			definedElement.setTextContent(value);
		}
	}

	/**
	 * Sets the data type of the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> by attribute name, See {@odf.element
	 *  meta:user-defined}.
	 * 
	 * @param name	the name need to set for the user-defined metadata
	 * @param value  the value need to set for the user-defined metadata
	 */
	public void setUserDefinedDataType(String name, String value) {
		MetaUserDefinedElement definedElement = getUserDefinedElementByAttributeName(name);
		if (definedElement != null) {
			definedElement.setMetaValueTypeAttribute(value);
		}

	}

	/**
	 * Sets the ODFDOM element representation
	 * <code>MetaUserDefinedElement</code> , See {@odf.element
	 * meta:user-defined} if the element with the attribute name exists,then
	 * update;or create a new element if type or value is null,the original will
	 * not be updated.
	 * 
	 * @param name	the name need to set for the user-defined metadata
	 * @param type	the data type need to set for the user-defined metadata
	 * @param value	the value need to set for the user-defined metadata
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
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaInitialCreatorElement</code> , See {@odf.element
	 * meta:initial-creator}.
	 * 
	 * @return get the initial creator of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaInitialCreatorElement</code> , See {@odf.element
	 * meta:initial-creator}.
	 * 
	 * @param initialCreator set the specified initial creator. NULL will remove the element from the meta.xml.
	 */
	public void setInitialCreator(String initialCreator) {
		MetaInitialCreatorElement initialCreatorEle = OdfElement.findFirstChildNode(MetaInitialCreatorElement.class,
				mOfficeMetaElement);
		if (initialCreator == null) {
			if (initialCreatorEle != null) {
				mOfficeMetaElement.removeChild(initialCreatorEle);
			}
		} else {
			if (initialCreatorEle == null) {
				initialCreatorEle = mOfficeMetaElement.newMetaInitialCreatorElement();
			}
			initialCreatorEle.setTextContent(initialCreator);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>DcCreatorElement</code> , See {@odf.element
	 * dc:creator}
	 * 
	 * @return the creator of the current document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>DcCreatorElement</code> , See {@odf.element
	 * dc:creator}.
	 * 
	 * @param creator set the specified creator. NULL will remove the element from the meta.xml.
	 */
	public void setCreator(String creator) {
		DcCreatorElement creatorEle = OdfElement.findFirstChildNode(
				DcCreatorElement.class, mOfficeMetaElement);
		if (creator == null) {
			if (creatorEle != null) {
				mOfficeMetaElement.removeChild(creatorEle);
			}
		} else {
			if (creatorEle == null) {
				creatorEle = mOfficeMetaElement.newDcCreatorElement();
			}
			creatorEle.setTextContent(creator);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaPrintedByElement</code> , See {@odf.element
	 * meta:printed-by}
	 * 
	 * @return the name of the last person who printed the current document;
	 * <p>
	 * <code>null</code>, if element is not set
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaPrintedByElement</code> , See {@odf.element
	 * meta:printed-by}.
	 * 
	 * @param printedBy	the name need to set for the last person who printed the current document. NULL will remove the element from the meta.xml.
	 */
	public void setPrintedBy(String printedBy) {
		MetaPrintedByElement printedByEle = OdfElement.findFirstChildNode(
				MetaPrintedByElement.class, mOfficeMetaElement);
		if (printedBy == null) {
			if (printedByEle != null) {
				mOfficeMetaElement.removeChild(printedByEle);
			}
		} else {
			if (printedByEle == null) {
				printedByEle = mOfficeMetaElement.newMetaPrintedByElement();
			}
			printedByEle.setTextContent(printedBy);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaCreationDateElement</code> , See {@odf.element
	 * meta:creation-date}
	 * 
	 * @return the date and time when the document was created initially;
	 * <p>
	 * <code>null</code>, if element is not set
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaCreationDateElement</code> , See {@odf.element
	 * meta:creation-date}.
	 * 
	 * @param creationDate	the date and time need to set. NULL will remove the element from the meta.xml.
	 */
	public void setCreationDate(Calendar creationDate) {
		MetaCreationDateElement creationDateEle = OdfElement.findFirstChildNode(MetaCreationDateElement.class,
				mOfficeMetaElement);
		if (creationDate == null) {
			if (creationDateEle != null) {
				mOfficeMetaElement.removeChild(creationDateEle);
			}
		} else {
			if (creationDateEle == null) {
				creationDateEle = mOfficeMetaElement.newMetaCreationDateElement();
			}
			creationDateEle.setTextContent(calendarToString(creationDate));
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>DcDateElement</code> , See {@odf.element dc:date}.
	 * 
	 * @return the date and time when the document was last modified;
	 * <p>
	 * <code>null</code>, if the element is not set.
	 */
	public Calendar getDate() {
		DcDateElement dcDateEle = OdfElement.findFirstChildNode(
				DcDateElement.class, mOfficeMetaElement);
		if (dcDateEle != null) {
			return stringToCalendar(dcDateEle.getTextContent());
		}
		return null;
	}

	/**
	 * Sets the value of the ODFDOM element representation
	 * <code>DcDateElement</code> , See {@odf.element dc:date}.
	 * 
	 * @param date	the date and time need to set. NULL will remove the element from the meta.xml.
	 */
	public void setDate(Calendar date) {
		DcDateElement dcDateEle = OdfElement.findFirstChildNode(
				DcDateElement.class, mOfficeMetaElement);
		if (date == null) {
			if (dcDateEle != null) {
				mOfficeMetaElement.removeChild(dcDateEle);
			}
		} else {
			if (dcDateEle == null) {
				dcDateEle = mOfficeMetaElement.newDcDateElement();
			}
			dcDateEle.setTextContent(calendarToString(date));
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaPrintDateElement</code> , See {@odf.element
	 * meta:print-date}.
	 * 
	 * @return the date and time when the document was last printed;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaPrintDateElement</code> , See {@odf.element
	 * meta:print-date}.
	 * 
	 * @param printDate	the date and time need to set. NULL will remove the element from the meta.xml.
	 */
	public void setPrintDate(Calendar printDate) {
		MetaPrintDateElement printDateEle = OdfElement.findFirstChildNode(
				MetaPrintDateElement.class, mOfficeMetaElement);
		if (printDate == null) {
			if (printDateEle != null) {
				mOfficeMetaElement.removeChild(printDateEle);
			}
		} else {
			if (printDateEle == null) {
				printDateEle = mOfficeMetaElement.newMetaPrintDateElement();
			}
			printDateEle.setTextContent(calendarToString(printDate));
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>DcLanguageElement</code> , See {@odf.element
	 * dc:language}.
	 * 
	 * @return the default language of the document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>DcLanguageElement</code> , See {@odf.element
	 * dc:language}.
	 * 
	 * @param language the default language need to set fo the current document. NULL will remove the element from the meta.xml.
	 */
	public void setLanguage(String language) {
		DcLanguageElement languageEle = OdfElement.findFirstChildNode(
				DcLanguageElement.class, mOfficeMetaElement);
		if (language == null) {
			if (languageEle != null) {
				mOfficeMetaElement.removeChild(languageEle);
			}
		} else {
			if (languageEle == null) {
				languageEle = mOfficeMetaElement.newDcLanguageElement();
			}
			languageEle.setTextContent(language);
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaEditingCyclesElement</code> , See {@odf.element
	 * meta:editing-cycles}.
	 * 
	 * @return the number of times that the document has been edited;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaEditingCyclesElement</code> , See {@odf.element
	 * meta:editing-cycles}.
	 * 
	 * @param editingCycles	set the specified edit times. NULL will remove the element from the meta.xml.
	 */
	public void setEditingCycles(Integer editingCycles) {
		MetaEditingCyclesElement editingCyclesEle = OdfElement.findFirstChildNode(MetaEditingCyclesElement.class,
				mOfficeMetaElement);
		if (editingCycles == null) {
			if (editingCyclesEle != null) {
				mOfficeMetaElement.removeChild(editingCyclesEle);
			}
		} else {
			if (editingCyclesEle == null) {
				editingCyclesEle = mOfficeMetaElement.newMetaEditingCyclesElement();
			}
			editingCyclesEle.setTextContent(String.valueOf(editingCycles));
		}
	}

	/**
	 * Receives the value of the ODFDOM element representation
	 * <code>MetaEditingDurationElement</code> , See {@odf.element
	 *  meta:editing-duration}.
	 * 
	 * @return the total time spent editing the document;
	 * <p>
	 * <code>null</code>, if the element is not set.
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
	 * Sets the value of the ODFDOM element representation
	 * <code>MetaEditingDurationElement</code> , See {@odf.element
	 *  meta:editing-duration}.
	 * 
	 * @param editingDuration the time need to set. NULL will remove the element from the meta.xml.
	 */
	public void setEditingDuration(Duration editingDuration) {
		MetaEditingDurationElement editingDurationEle = OdfElement.findFirstChildNode(MetaEditingDurationElement.class,
				mOfficeMetaElement);
		if (editingDuration == null) {
			if (editingDurationEle != null) {
				mOfficeMetaElement.removeChild(editingDurationEle);
			}
		} else {
			if (editingDurationEle == null) {
				editingDurationEle = mOfficeMetaElement.newMetaEditingDurationElement();
			}
			editingDurationEle.setTextContent(editingDuration.toString());
		}
	}

	/**
	 * Receives the sub feature of OdfMetaDocumentStatistic.
	 * 
	 * @return the statistics about the document which can be represented by 
	 * <code>OdfMetaDocumentStatistic</code> feature;
	 * <p>
	 * <code>null</code>, if the feature is not exist.
	 */
	public OdfMetaDocumentStatistic getDocumentStatistic() {
		MetaDocumentStatisticElement element = getDocumentStatisticElement();
		if (element != null) {
			return new OdfMetaDocumentStatistic(element);
		} else {
			return null;
		}
	}

	/**
	 * Receives the OdfMetaHyperlinkBehaviour feature.
	 * 
	 * @return the default behavior of hyperlinks in the current document
	 * which can be represented by <code>OdfMetaHyperlinkBehaviour</code> feature;
	 * <p>
	 * <code>null</code>, if the feature is not exist.
	 */
	public OdfMetaHyperlinkBehaviour getHyperlinkBehaviour() {
		MetaHyperlinkBehaviourElement element = getHyperlinkBehaviourElement();
		if (element != null) {
			return new OdfMetaHyperlinkBehaviour(element);
		} else {
			return null;
		}
	}

	/**
	 * Receives the OdfMetaAutoReload feature.
	 * 
	 * @return the information whether the document is reloaded or replaced by another document
	 * after a certain period of time has elapsed.
	 * <p>
	 * It can be represented by <code>OdfMetaAutoReload</code> feature;
	 * <p>
	 * <code>null</code>, if the feature is not exist.
	 */
	public OdfMetaAutoReload getAutoReload() {
		MetaAutoReloadElement element = getAutoReloadElement();
		if (element != null) {
			return new OdfMetaAutoReload(element);
		} else {
			return null;
		}
	}

	/**
	 * Receives the OdfMetaTemplate feature.
	 * 
	 * @return the information specified the URL for the document that was used to create a document.
	 * <p>
	 * It can be represented by <code>OdfMetaTemplate</code> feature;
	 * <p>
	 * <code>null</code>, if the feature is not exist.
	 */
	public OdfMetaTemplate getTemplate() {
		MetaTemplateElement element = getTemplateElement();
		if (element != null) {
			return new OdfMetaTemplate(element);
		} else {
			return null;
		}
	}

	private MetaDocumentStatisticElement getDocumentStatisticElement() {
		return OdfElement.findFirstChildNode(
				MetaDocumentStatisticElement.class, mOfficeMetaElement);
	}

	private MetaHyperlinkBehaviourElement getHyperlinkBehaviourElement() {
		return OdfElement.findFirstChildNode(
				MetaHyperlinkBehaviourElement.class, mOfficeMetaElement);
	}

	private MetaAutoReloadElement getAutoReloadElement() {
		return OdfElement.findFirstChildNode(MetaAutoReloadElement.class,
				mOfficeMetaElement);
	}

	private MetaTemplateElement getTemplateElement() {
		return OdfElement.findFirstChildNode(MetaTemplateElement.class,
				mOfficeMetaElement);
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

	/**@param enableAutomaticUpdate If the automatic update of metadata is enabled, metadata such as last modified data is set during saving the document.
	The default is <code>true</code>, disabling the default allows to load and save a document without changing any data.
	 */
	public void setAutomaticUpdate(boolean enableAutomaticUpdate) {
		mAutomaticUpdate = enableAutomaticUpdate;
	}

	/**@return If the automatic update of metadata is enabled, metadata such as last modified data is set during saving the document.
	The default is <code>true</code>, disabling the default allows to load and save a document without changing any data. */
	public boolean hasAutomaticUpdate() {
		return mAutomaticUpdate;
	}

	@Override
	public String toString() {
		if (mOfficeMetaElement != null) {
			return mOfficeMetaElement.toString();
		} else {
			return null;
		}
	}
}
