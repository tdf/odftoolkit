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

package org.odftoolkit.simple.text.list;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleNumberElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.simple.Document;
import org.w3c.dom.Node;

/**
 * NumberDecorator is an implementation of the ListDecorator interface,
 * decorates a given List as number list. User can extend this class and realize
 * their own list and list item style. For example, set a specifies list item
 * with red color.
 * <p>
 * A NumberDecorator can be reused in the same Document.
 * 
 * @since 0.4
 */
public abstract class NumberDecoratorBase implements ListDecorator {

	private static String[] DEFAULT_TEXT_SPACE_BEFORE_ATTRIBUTES = { null, "0.501cm", "1cm", "1.501cm", "2cm",
			"2.501cm", "3.001cm", "3.502cm", "4.001cm", "4.502cm" };
	private static String DEFAULT_TEXT_MIN_LABEL_WIDTH = "0.499cm";
	private static String DEFAULT_FONT_NAME = "Tahoma";
//	protected static String DEFAULT_NUM_FORMAT = "1";
//	protected static String DEFAULT_NUM_SUFFIX = ".";
//	private static String DEFAULT_NAME = "Simple_Custom_Number_List";

	protected OdfTextListStyle listStyle;
	protected OdfStyle paragraphStyle;
	protected OdfOfficeAutomaticStyles styles;

	/**
	 * Constructor with Document.
	 * 
	 * @param doc
	 *            the Document which this NumberDecorator will be used on.
	 * @param styleName style name: (i.e. NumberDecorator:  NumberDecorator.DEFAULT_NAME)                 
	 * @param styleNameIntern intern stylename (i.e. NumberDecorator:  "Numbering_20_Symbols")      
	 * @param numberingFormat the numbering format (i.e. NumberDecorator:  "1", sonsrt "a", "A", "i", "I", ....)      
	 * @param suffix  suffix after the number ("." for 1., 2., ... ")" for a) b) c) etc)
	 * @param prefix  prefix before the number ("(" for (a) (b) (c) etc)
	 */
	protected NumberDecoratorBase(Document doc, String styleName, String styleNameIntern, String numberingFormat, String suffix, String prefix) {
		OdfContentDom contentDocument;
		try {
			contentDocument = doc.getContentDom();
			styles = contentDocument.getAutomaticStyles();
			OdfOfficeStyles documentStyles = doc.getDocumentStyles();
			listStyle = styles.getListStyle(styleName);
			// create number style
			if (listStyle == null) {
				listStyle = styles.newListStyle();
				// <style:style style:name="Numbering_20_Symbols"
				// style:display-name="Numbering Symbols" style:family="text" />
				getOrCreateStyleByName(documentStyles, styles, styleNameIntern, OdfStyleFamily.Text);
				for (int i = 0; i < 10; i++) {
					TextListLevelStyleNumberElement listLevelElement = listStyle.newTextListLevelStyleNumberElement(
						numberingFormat, i + 1);
					// get from default style element
					listLevelElement.setTextStyleNameAttribute(styleNameIntern);
					listLevelElement.setStyleNumSuffixAttribute(suffix);
					listLevelElement.setStyleNumPrefixAttribute(prefix);
					StyleListLevelPropertiesElement styleListLevelPropertiesElement = listLevelElement
							.newStyleListLevelPropertiesElement();
					if (DEFAULT_TEXT_SPACE_BEFORE_ATTRIBUTES[i] != null) {
						styleListLevelPropertiesElement
								.setTextSpaceBeforeAttribute(DEFAULT_TEXT_SPACE_BEFORE_ATTRIBUTES[i]);
					}
					styleListLevelPropertiesElement.setTextMinLabelWidthAttribute(DEFAULT_TEXT_MIN_LABEL_WIDTH);
					StyleTextPropertiesElement styleTextPropertiesElement = listLevelElement
							.newStyleTextPropertiesElement("true");
					styleTextPropertiesElement.setStyleFontNameAttribute(DEFAULT_FONT_NAME);
				}
				// listStyle.setStyleNameAttribute(DEFAULT_NAME);
			}
			// create default paragraph style
			// <style:style style:name="P3" style:family="paragraph"
			// style:parent-style-name="Default_20_Text"
			// style:list-style-name="L1"
			// />
			paragraphStyle = styles.newStyle(OdfStyleFamily.Paragraph);
			// <style:style style:name="Default_20_Text"
			// style:display-name="Default Text" style:family="paragraph"
			// style:class="text" />
			// <style:style style:name="Standard" style:family="paragraph"
			// style:class="text" />
			getOrCreateStyleByName(documentStyles, styles, "Default_20_Text", OdfStyleFamily.Paragraph);
			paragraphStyle.setStyleParentStyleNameAttribute("Default_20_Text");
			paragraphStyle.setStyleListStyleNameAttribute(listStyle.getStyleNameAttribute());
		} catch (Exception e) {
			Logger.getLogger(NumberDecoratorBase.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	public void decorateList(List list) {
		TextListElement listElement = list.getOdfElement();
		listElement.setTextStyleNameAttribute(listStyle.getStyleNameAttribute());
	}

	public void decorateListItem(ListItem item) {
		TextListItemElement listItemElement = item.getOdfElement();
		Node child = listItemElement.getFirstChild();
		while (child != null) {
			if (child instanceof TextPElement) {
				TextPElement pElement = (TextPElement) child;
				pElement.setTextStyleNameAttribute(paragraphStyle.getStyleNameAttribute());
			}
			child = child.getNextSibling();
		}
	}

	public ListType getListType() {
		return ListType.NUMBER;
	}

	private OdfStyle getOrCreateStyleByName(OdfOfficeStyles documentStyles, OdfOfficeAutomaticStyles styles,
			String styleName, OdfStyleFamily styleFamily) {
		OdfStyle odfStyle = documentStyles.getStyle(styleName, styleFamily);
		if (odfStyle == null) {
			styles.getStyle(styleName, styleFamily);
		}
		if (odfStyle == null) {
			odfStyle = styles.newStyle(styleFamily);
			odfStyle.setStyleNameAttribute(styleName);
			odfStyle.setStyleDisplayNameAttribute(styleName);
		}
		return odfStyle;
	}
	
	/**
	 * only for JUnit Test
	 * @return
	 */
	OdfTextListStyle getListStyle()
	{
	    return listStyle;
	}

}
