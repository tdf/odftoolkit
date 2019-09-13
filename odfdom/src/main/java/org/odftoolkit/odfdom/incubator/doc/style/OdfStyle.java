/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package org.odftoolkit.odfdom.incubator.doc.style;

import java.util.logging.Logger;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.OdfSchemaDocument;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.OdfStylePropertiesBase;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convenient functionality for the parent ODF OpenDocument element
 *
 */
public class OdfStyle extends StyleStyleElement {

	private static final Logger LOG = Logger.getLogger(OdfStyle.class.getName());
	/**
	 *
	 */
	private static final long serialVersionUID = 1114614579014922410L;

	public OdfStyle(OdfFileDom ownerDoc) {
		super(ownerDoc);
	}

	@Override
	public OdfStyleBase getParentStyle() {
		String parent = this.getStyleParentStyleNameAttribute();
        OdfStyleBase parentStyle = null;
		if ((parent != null) && (parent.length() != 0)) {
			parentStyle = ((OdfSchemaDocument) mPackageDocument).getDocumentStyles().getStyle(parent, getFamily());
		} else {
			 OdfOfficeStyles documentStyles = ((OdfSchemaDocument) mPackageDocument).getDocumentStyles();
             if(documentStyles != null){
                 parentStyle = documentStyles.getDefaultStyle(getFamily());
             }
		}
        return parentStyle;
	}

	@Override
	public OdfStyleFamily getFamily() {
		String family = getStyleFamilyAttribute();
		if (family != null) {
			return OdfStyleFamily.valueOf(family);
		} else {
			return null;
		}
	}

	/**
	 * @return true if there is a property other than the style:family,
	 * style:name and style:parent-style-name with a value other than "null", or
	 * a similar attribute on the properties element
	 */
	public boolean hasPropertyAttribute() {
		boolean hasPropertyAttributes = false;
		NodeList children = this.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ((child != null) && OdfStylePropertiesBase.class.isInstance(child)) {
				if (hasPropertyAttributes((OdfElement) child)) {
					hasPropertyAttributes = true;
					break;
				}
			}
		}
		return hasPropertyAttributes;
	}

	/**
	 * If a property element has more than the three default attributes
	 * style:parent-style-name, style:name and style:family, it is considered
	 * not empty!
	 */
	private static boolean hasPropertyAttributes(OdfElement propsElement) {
		boolean hasPropertyAttributes = false;

		NamedNodeMap attrs = propsElement.getAttributes();
		int attrsCount = attrs.getLength();
		for (int i = 0; i < attrsCount; i++) {
			Attr a = (Attr) attrs.item(i);
			if (a.getNamespaceURI().equals(OdfDocumentNamespace.STYLE.getUri())) {
				// none of the below should be in here, as we are looking at teh properties..
				if (!(a.getLocalName().equals("family") || a.getLocalName().equals("name") || a.getLocalName().equals("parent-style-name"))) {
					if (!a.getValue().equals("null")) {
						hasPropertyAttributes = true;
						break;
					}
				}
			} else {
				if (!a.getValue().equals("null")) {
					hasPropertyAttributes = true;
					break;
				}
			}
		}
		return hasPropertyAttributes;
	}

	/**
	 * Two spans having the same range will be provided, only one will remain,
	 * styles are being merged.
	 *
	 * Merges the automatic styles of the two given spans, uses one template
	 * style. The styles from the inner span have the higher priority. The outer
	 * span will be deleted afterwards.
	 * @return the remaining stylableElement of the two
	 */
	//ToDo: Refactoring - the style changs are only dependent on the state of the style-dominant span (refactor with method below)
	public static OdfStylableElement mergeSelectionWithSameRange(OdfStylableElement higherPrioSpan, OdfStylableElement lowerPrioSpan) {
		// only do the merging of both spans, if they really exist
		if (higherPrioSpan != null && lowerPrioSpan != null) {
			boolean innerHasAutomatic = higherPrioSpan.hasAutomaticStyle();
			boolean innerHasTemplate = higherPrioSpan.hasDocumentStyle();
			boolean outerHasAutomatic = lowerPrioSpan.hasAutomaticStyle();
			boolean outerHasTemplate = lowerPrioSpan.hasDocumentStyle();

			OdfOfficeAutomaticStyles autoStyles = lowerPrioSpan.getOrCreateAutomaticStyles();
			if (autoStyles == null) {
				OdfFileDom fileDom = ((OdfFileDom) lowerPrioSpan.getOwnerDocument());
				if (fileDom instanceof OdfContentDom) {
					autoStyles = ((OdfContentDom) fileDom).getOrCreateAutomaticStyles();
				} else {
					autoStyles = ((OdfStylesDom) fileDom).getOrCreateAutomaticStyles();
				}
			}
			OdfStyle innerAutoStyle = higherPrioSpan.getAutomaticStyle();
			OdfStyle outerAutoStyle = lowerPrioSpan.getAutomaticStyle();
			StyleTextPropertiesElement innerPropsElement = null;
			StyleTextPropertiesElement outerPropsElement;
			// in case there is an automatic style, make sure there is a property element
			if (innerAutoStyle != null) {
				innerPropsElement = (StyleTextPropertiesElement) innerAutoStyle.getChildElement(StyleTextPropertiesElement.ELEMENT_NAME.getUri(), StyleTextPropertiesElement.ELEMENT_NAME.getLocalName(), 0);
				if (innerPropsElement != null) {
					NodeList textPropsChildren = innerPropsElement.getChildNodes();
					if (textPropsChildren == null || textPropsChildren.getLength() == 0 && !hasPropertyAttributes(innerPropsElement)) {
						innerHasAutomatic = false;
					}
				} else {
					innerAutoStyle.appendChild(new StyleTextPropertiesElement((OdfFileDom) higherPrioSpan.getOwnerDocument()));
					innerHasAutomatic = false;
				}
			} else {
				innerHasAutomatic = false;
			}
			if (outerAutoStyle != null) {
				outerPropsElement = (StyleTextPropertiesElement) outerAutoStyle.getChildElement(StyleTextPropertiesElement.ELEMENT_NAME.getUri(), StyleTextPropertiesElement.ELEMENT_NAME.getLocalName(), 0);
				if (outerPropsElement != null) {
					NodeList textPropsChildren = outerPropsElement.getChildNodes();
					if (textPropsChildren == null || textPropsChildren.getLength() == 0 && !hasPropertyAttributes(outerPropsElement)) {
						outerHasAutomatic = false;
					}
				} else {
					outerAutoStyle.appendChild(new StyleTextPropertiesElement((OdfFileDom) lowerPrioSpan.getOwnerDocument()));
					outerHasAutomatic = false;
				}
			} else {
				outerHasAutomatic = false;
			}
			// if one of the styles has no style
			if (!innerHasAutomatic && !innerHasTemplate || !outerHasAutomatic && !outerHasTemplate) {
				// if the outer has no style, do nothing as it will be deleted
				if (!outerHasAutomatic && !outerHasTemplate) {
					// KEEP INNER - delete OUTER STYLE & SPAN
					// Done as the deletion is after these conditions
				} else { // the inner has no style, so just reference inner span to the outer style
					// KEEP INNER SPAN (ALWAYS - due to the content)
					// point to the new style with @style:name
					higherPrioSpan.setStyleName(lowerPrioSpan.getStyleName());
				}
			} else if (innerHasAutomatic && !innerHasTemplate) { //*** INNER ONLY AUTO
				if (outerHasAutomatic && !outerHasTemplate) { //*** INNER ONLY AUTO & OUTER ONLY AUTO
					// KEEP OUTER STYLE:
					// override OUTER properties
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(outerAutoStyle);
					StyleTextPropertiesElement newPropsElement = (StyleTextPropertiesElement) newAutoStyle.getChildElement(StyleTextPropertiesElement.ELEMENT_NAME.getUri(), StyleTextPropertiesElement.ELEMENT_NAME.getLocalName(), 0);
					OdfElement.copyAttributes(innerPropsElement, newPropsElement);

					// KEEP INNER SPAN (ALWAYS - due to the content)
					// point to the new style with @style:name
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());

				} else if (!outerHasAutomatic && outerHasTemplate) { //*** INNER ONLY AUTO & OUTER ONLY TEMPLATE
					// KEEP INNER STYLE:
					// overtake OUTER template style parent
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(innerAutoStyle);
					String parentStyle;
					if (outerAutoStyle == null) {
						// if there is no empty automatic style, take the template style name from the span
						parentStyle = lowerPrioSpan.getStyleName();
					} else {
						// if there is an empty automatic style, take the template style name from the style
						parentStyle = outerAutoStyle.getStyleParentStyleNameAttribute();
					}
					newAutoStyle.setStyleParentStyleNameAttribute(parentStyle);
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());
				} else { //*** INNER ONLY AUTO & OUTER BOTH
					// KEEP OUTER STYLE:
					// override OUTER properties
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(outerAutoStyle);
					StyleTextPropertiesElement newPropsElement = (StyleTextPropertiesElement) newAutoStyle.getChildElement(StyleTextPropertiesElement.ELEMENT_NAME.getUri(), StyleTextPropertiesElement.ELEMENT_NAME.getLocalName(), 0);
					OdfElement.copyAttributes(innerPropsElement, newPropsElement);
					// overtake OUTER template style parent
					newAutoStyle.setStyleParentStyleNameAttribute(innerAutoStyle.getStyleParentStyleNameAttribute());

					// overtake OUTER span @style:name
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());
				}
			} else if (!innerHasAutomatic && innerHasTemplate) { //*** INNER ONLY TEMPLATE
				if (outerHasAutomatic && !outerHasTemplate) { //*** INNER ONLY TEMPLATE & OUTER ONLY AUTO
					// KEEP INNER
					// create INNER automatic styles
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(outerAutoStyle);
					newAutoStyle.setStyleParentStyleNameAttribute(higherPrioSpan.getStyleName());
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());

				} else if (!outerHasAutomatic && outerHasTemplate) { //*** INNER ONLY TEMPLATE & OUTER ONLY TEMPLATE
					// KEEP INNER - delete OUTER STYLE & SPAN
					// Done as the deletion of span is after these conditions
				} else { //*** INNER ONLY TEMPLATE & OUTER BOTH
					// KEEP INNER
					// overtake OUTER properties element, by cloning OUTER style
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(outerAutoStyle);
					// overtake INNER template style parent
					newAutoStyle.setStyleParentStyleNameAttribute(higherPrioSpan.getStyleName());
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());
				}
			} else { //*** INNER BOTH
				if (outerHasAutomatic && !outerHasTemplate) { //*** INNER BOTH & OUTER ONLY AUTO
					// KEEP OUTER:
					// overtake OUTER properties element, by cloning OUTER style
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(outerAutoStyle);

					StyleTextPropertiesElement newPropsElement = (StyleTextPropertiesElement) newAutoStyle.getChildElement(StyleTextPropertiesElement.ELEMENT_NAME.getUri(), StyleTextPropertiesElement.ELEMENT_NAME.getLocalName(), 0);
					OdfElement.copyAttributes(innerPropsElement, newPropsElement);
					// overtake INNER template style parent
					newAutoStyle.setStyleParentStyleNameAttribute(innerAutoStyle.getStyleParentStyleNameAttribute());
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());
				} else if (!outerHasAutomatic && outerHasTemplate) { //*** INNER BOTH & OUTER ONLY TEMPLATE
					// KEEP INNER - delete OUTER STYLE & SPAN
					// Done as the deletion is after these conditions
				} else { //*** INNER BOTH & OUTER BOTH
					// KEEP OUTER
					// overtake OUTER properties element, by cloning OUTER style
					OdfStyle newAutoStyle = autoStyles.makeStyleUnique(outerAutoStyle);

					StyleTextPropertiesElement newPropsElement = (StyleTextPropertiesElement) newAutoStyle.getChildElement(StyleTextPropertiesElement.ELEMENT_NAME.getUri(), StyleTextPropertiesElement.ELEMENT_NAME.getLocalName(), 0);
					OdfElement.copyAttributes(innerPropsElement, newPropsElement);
					// overtake INNER template style parent
					newAutoStyle.setStyleParentStyleNameAttribute(innerAutoStyle.getStyleParentStyleNameAttribute());
					higherPrioSpan.setStyleName(newAutoStyle.getStyleNameAttribute());
				}
			}
			OdfElement.removeSingleElement(lowerPrioSpan);
		}
		return higherPrioSpan;
	}

	@Override
	public String toString() {
		return "[name: " + this.getStyleNameAttribute() + " family: " + this.getStyleFamilyAttribute() + "]";
	}
}
