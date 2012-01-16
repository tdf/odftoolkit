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

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelLabelAlignmentElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.text.TextListElement;
import org.odftoolkit.odfdom.dom.element.text.TextListItemElement;
import org.odftoolkit.odfdom.dom.element.text.TextListLevelStyleImageElement;
import org.odftoolkit.odfdom.dom.element.text.TextPElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.pkg.OdfPackage;
import org.odftoolkit.odfdom.pkg.manifest.OdfFileEntry;
import org.odftoolkit.simple.Document;
import org.w3c.dom.Node;

/**
 * ImageDecorator is an implementation of the ListDecorator interface, decorates
 * a given List as image list. Users can set an image as the list item prefix.
 * They also can extend this class and realize their own list and list item
 * style. For example, set a specifies list item with red color.
 * <p>
 * An ImageDecorator can be reused in the same Document.
 * 
 * @since 0.4
 */
public class ImageDecorator implements ListDecorator {

	private static String[] DEFAULT_MARGIN_LEFT_ATTRIBUTES = { "0.741cm", "1.482cm", "2.223cm", "2.963cm", "3.704cm",
			"4.445cm", "5.186cm", "5.927cm", "6.668cm", "7.408cm" };
	private static String DEFAULT_IMAGE_WIDTH = "0.54cm";
	private static String DEFAULT_IMAGE_HEIGHT = "0.54cm";
	private static String DEFAULT_NAME = "Simple_Default_Image_List";
	private static final String SLASH = "/";

	private OdfTextListStyle listStyle;
	private OdfStyle paragraphStyle;
	private OdfOfficeAutomaticStyles styles;

	/**
	 * Constructor with Document and image URI.
	 * 
	 * @param doc
	 *            the Document which this ImageDecorator will be used on.
	 * @param imageUri
	 *            the image location.
	 */
	public ImageDecorator(Document doc, URI imageUri) {
		OdfContentDom contentDocument;
		try {
			contentDocument = doc.getContentDom();
			styles = contentDocument.getAutomaticStyles();
			OdfOfficeStyles documentStyles = doc.getDocumentStyles();
			listStyle = styles.getListStyle(DEFAULT_NAME);
			// create image style
			if (listStyle == null) {
				listStyle = styles.newListStyle();
				String packagePath = null;
				try {
					packagePath = insertImage(doc, imageUri);
				} catch (Exception e) {
					Logger.getLogger(ImageDecorator.class.getName()).log(Level.SEVERE, null, e);
				}
				for (int i = 0; i < 10; i++) {
					TextListLevelStyleImageElement listLevelElement = listStyle
							.newTextListLevelStyleImageElement(i + 1);
					// xlink:href="Pictures/100002010000001700000017B273CC34.png"
					listLevelElement.setXlinkHrefAttribute(packagePath);
					listLevelElement.setXlinkTypeAttribute("simple");
					listLevelElement.setXlinkShowAttribute("embed");
					listLevelElement.setXlinkActuateAttribute("onLoad");
					StyleListLevelPropertiesElement styleListLevelPropertiesElement = listLevelElement
							.newStyleListLevelPropertiesElement();
					styleListLevelPropertiesElement.setTextListLevelPositionAndSpaceModeAttribute("label-alignment");
					styleListLevelPropertiesElement.setStyleVerticalPosAttribute("middle");
					styleListLevelPropertiesElement.setStyleVerticalRelAttribute("line");
					styleListLevelPropertiesElement.setFoWidthAttribute(DEFAULT_IMAGE_WIDTH);
					styleListLevelPropertiesElement.setFoHeightAttribute(DEFAULT_IMAGE_HEIGHT);
					StyleListLevelLabelAlignmentElement styleListLevelLabelAlignmentElement = styleListLevelPropertiesElement
							.newStyleListLevelLabelAlignmentElement("listtab");
					styleListLevelLabelAlignmentElement
							.setTextListTabStopPositionAttribute(DEFAULT_MARGIN_LEFT_ATTRIBUTES[i]);
					styleListLevelLabelAlignmentElement.setFoTextIndentAttribute("-0.741cm");
					styleListLevelLabelAlignmentElement.setFoMarginLeftAttribute(DEFAULT_MARGIN_LEFT_ATTRIBUTES[i]);
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
			Logger.getLogger(ImageDecorator.class.getName()).log(Level.SEVERE, null, e);
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
		return ListType.IMAGE;
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

	private String insertImage(Document doc, URI imageUri) throws Exception {
		String imageRef = null;
		if (!imageUri.isAbsolute()) {
			imageRef = System.getProperty("user.dir") + '/' + imageUri.toString();
		} else {
			imageRef = imageUri.toString();
		}
		String mediaType = OdfFileEntry.getMediaTypeString(imageRef);
		if (imageRef.contains(SLASH)) {
			imageRef = imageRef.substring(imageRef.lastIndexOf(SLASH) + 1, imageRef.length());
		}
		String packagePath = OdfPackage.OdfFile.IMAGE_DIRECTORY.getPath() + SLASH + imageRef;
		packagePath = doc.getDocumentPath() + packagePath;
		doc.getPackage().insert(imageUri, packagePath, mediaType);
		return packagePath;
	}
}
