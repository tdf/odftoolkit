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
package org.odftoolkit.odfdom.incubator.doc.office;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.odftoolkit.odfdom.changes.MapHelper;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.office.OfficeValueTypeAttribute.Value;
import org.odftoolkit.odfdom.dom.element.OdfStylableElement;
import org.odftoolkit.odfdom.dom.element.number.DataStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencyStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDateStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberPercentageStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTimeStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.text.TextListStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberCurrencyStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStylePageLayout;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.pkg.ElementVisitor;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convenient functionality for the parent ODF OpenDocument element
 *
 */
abstract public class OdfOfficeAutomaticStyles extends OdfStylesBase {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.OFFICE, "automatic-styles");

    private static final long serialVersionUID = -2925910664631016175L;
    // styles that are only in OdfAutomaticStyles
    private HashMap<String, OdfStylePageLayout> mPageLayouts;
    // styles that are common for OdfStyles and OdfAutomaticStyles

    public OdfOfficeAutomaticStyles(OdfFileDom ownerDoc) {
        super(ownerDoc, ELEMENT_NAME);
//        mStylesBaseImpl = new OdfStylesBase();
    }


	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

   /**
     * Create an <code>OdfStyle</code> element with style family
     *
     * @param styleFamily The <code>OdfStyleFamily</code> element
     * @return an <code>OdfStyle</code> element
     */
    public OdfStyle newStyle(OdfStyleFamily styleFamily) {
        OdfFileDom dom = (OdfFileDom) this.ownerDocument;
        OdfStyle newStyle = dom.newOdfElement(OdfStyle.class);
        newStyle.setStyleFamilyAttribute(styleFamily.getName());

        newStyle.setStyleNameAttribute(newUniqueStyleName(styleFamily));
        // <style:style> elements are the first type of elements within the automatic styles parent
        OdfElement firstChild = this.getFirstChildElement();
        // if there another child, add the new <style:style> ahead
        if (firstChild != null) {
            this.insertBefore(newStyle, firstChild);
        } else {
            // otherwise append the <style:style> as first child
            this.appendChild(newStyle);
        }
        return newStyle;
    }

	protected <T extends OdfElement> T getStylesElement(OdfFileDom dom, Class<T> clazz) throws Exception {
		OdfElement stylesRoot = dom.getRootElement();

		OdfOfficeAutomaticStyles contentBody = OdfElement.findFirstChildNode(OdfOfficeAutomaticStyles.class, stylesRoot);
		NodeList childs = contentBody.getChildNodes();
		for (int i = 0;
				i < childs.getLength();
				i++) {
			Node cur = childs.item(i);
			if ((cur != null) && clazz.isInstance(cur)) {
				return (T) cur;
			}
		}
		return null;
	}

    /**
     * Create an <code>OdfTextListStyle</code> element
     *
     * @return an <code>OdfTextListStyle</code> element
     */
    public OdfTextListStyle newListStyle() {
        return newListStyle(newUniqueStyleName(OdfStyleFamily.List));
    }

    /**
     * Create an <code>OdfStylePageLayout</code> element
     *
     * @return an <code>OdfStylePageLayout</code> element
     */
    public OdfStylePageLayout newPageLayout() {
        return newPageLayout(newUniqueStyleName(null));
    }


    /**
     * Create an <code>OdfStylePageLayout</code> element
     *
     * @return an <code>OdfStylePageLayout</code> element
     */
    public OdfStylePageLayout newPageLayout(String pageLayoutName) {
        OdfFileDom dom = (OdfFileDom) this.ownerDocument;
        OdfStylePageLayout newPageLayout = dom.newOdfElement(OdfStylePageLayout.class);

        newPageLayout.setStyleNameAttribute(pageLayoutName);
        // <style:page-layout always last in automatic styles
          // append it to the automatic styles parent
        this.appendChild(newPageLayout);
        return newPageLayout;
    }

    /**
     * Create an <code>OdfTextListStyle</code> element
     *
     * @param listStyleName the name of the new list style
     * @return an <code>OdfTextListStyle</code> element
     */
    public OdfTextListStyle newListStyle(String listStyleName) {
        OdfFileDom dom = (OdfFileDom) this.ownerDocument;
        OdfTextListStyle newStyle = dom.newOdfElement(OdfTextListStyle.class);

        newStyle.setStyleNameAttribute(listStyleName);
        // <text:list-style are always the second after the <style:style> elements
        OdfElement child = this.getFirstChildElement();
        if (child != null) {
            // check if the first element is of <style:style>
            if (child instanceof StyleStyleElement) {
                // search for a following sibling not being a <style:style> element
                while (child != null && child instanceof StyleStyleElement) {
                    child = OdfElement.getNextSiblingElement(child);
                }
            }
            // if such a none <style:style> element exists
            if (child != null) {
                //check if a style by that name already exists
                OdfElement removeChild = child;
                while(removeChild != null)
                {
                    if(removeChild instanceof OdfTextListStyle && ((OdfTextListStyle)removeChild).getStyleNameAttribute().equals(listStyleName))
                    {
                        break;
                    }
                    removeChild = OdfElement.getNextSiblingElement(removeChild);
                }
                // add the list style before of this
                this.insertBefore(newStyle, child);
                if(removeChild != null)
                    this.removeChild(removeChild);
            } else {
                // otherwise add the list style after the <style:style>
                this.appendChild(newStyle);
            }
        } else {
            // add the list style to this element, the empty automatic styles parent
            this.appendChild(newStyle);
        }

        return newStyle;
    }

    /**
     * Returns the <code>OdfStylePageLayout</code> element with the given name.
     *
     * @param name is the name of the page layout
     * @return the page layout or null if there is no such page layout
     */
    public OdfStylePageLayout getPageLayout(String name) {
        if (mPageLayouts != null) {
            return mPageLayouts.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns the <code>OdfStylePageLayout</code> element with the given name.
     *
     * @param name is the name of the page layout
     * @return the page layout
     */
    public OdfStylePageLayout getOrCreatePageLayout(String name) {
        OdfStylePageLayout pageLayout = getPageLayout(name);
        if (pageLayout == null) {
            OdfFileDom dom = (OdfFileDom) this.ownerDocument;
            pageLayout = dom.newOdfElement(OdfStylePageLayout.class);
            pageLayout.setStyleNameAttribute(name);
            this.appendChild(pageLayout);
        }
        return pageLayout;
    }

    @Override
    public void onOdfNodeInserted(OdfElement node, Node refNode) {
        if (node instanceof OdfStylePageLayout) {
            OdfStylePageLayout pageLayout = (OdfStylePageLayout) node;
            if (mPageLayouts == null) {
                mPageLayouts = new HashMap<String, OdfStylePageLayout>();
            }

            mPageLayouts.put(pageLayout.getStyleNameAttribute(), pageLayout);
        } else {
            super.onOdfNodeInserted(node, refNode);
        }
    }

    @Override
    public void onOdfNodeRemoved(OdfElement node) {
        if (node instanceof OdfStylePageLayout) {
            if (mPageLayouts != null) {
                OdfStylePageLayout pageLayout = (OdfStylePageLayout) node;
                mPageLayouts.remove(pageLayout.getStyleNameAttribute());
            }
        } else {
            super.onOdfNodeRemoved(node);
        }
    }

    /**
     * This methods removes all automatic styles that are currently not used by
     * any styleable element. Additionally all duplicate automatic styles will
     * be removed.
     */
    public void optimize() {
        Iterator<OdfStyle> iter = getAllStyles().iterator();
        SortedSet<OdfStyle> stylesSet = new TreeSet<OdfStyle>();
        while (iter.hasNext()) {
            OdfStyle cur = iter.next();

            // skip styles which are not in use:
            if (cur.getStyleUserCount() < 1) {
                continue;
            }

            SortedSet<OdfStyle> tail = stylesSet.tailSet(cur);
            OdfStyle found = tail.size() > 0 ? tail.first() : null;
            if (found != null && found.equals(cur)) {
                // cur already in set. Replace all usages of cur by found:
                Iterator<OdfStylableElement> styleUsersIter = cur.getStyleUsers().iterator();
                ArrayList<OdfStylableElement> styleUsers = new ArrayList<OdfStylableElement>();
                while (styleUsersIter.hasNext()) {
                    styleUsers.add(styleUsersIter.next());
                }
                styleUsersIter = styleUsers.iterator();
                while (styleUsersIter.hasNext()) {
                    OdfStylableElement elem = styleUsersIter.next();
                    OdfStyle autoStyle = elem.getAutomaticStyle();
                    if (autoStyle != null) {
                        elem.setStyleName(found.getStyleNameAttribute());
                    }
                }
            } else {
                stylesSet.add(cur);
            }
        }

        OdfStyle style = OdfElement.findFirstChildNode(OdfStyle.class, this);
        while (style != null) {
            OdfStyle nextStyle = OdfElement.findNextChildNode(OdfStyle.class, style);
            if (style.getStyleUserCount() < 1) {
                this.removeChild(style);
            }

            style = nextStyle;
        }
    }

    /**
     * This method makes the style unique
     *
     * @param referenceStyle The reference <code>OdfStyle</code> element to
     * create a new automatic style
     * @return an <code>OdfStyle</code> element
     */
    public OdfStyle makeStyleUnique(OdfStyle referenceStyle) {
        OdfStyle newStyle = null;

        if (referenceStyle.getOwnerDocument() != this.getOwnerDocument()) {
            // import style from a different dom
            newStyle = (OdfStyle) this.getOwnerDocument().importNode(referenceStyle, true);
        } else {
            // just clone
            newStyle = (OdfStyle) referenceStyle.cloneNode(true);
        }

        newStyle.setStyleNameAttribute(newUniqueStyleName(newStyle.getFamily()));
        appendChild(newStyle);

        return newStyle;
    }

    private String newUniqueStyleName(OdfStyleFamily styleFamily) {
        String unique_name;

        if (styleFamily != null && styleFamily.equals(OdfStyleFamily.List)) {
            do {
                unique_name = String.format("l%06x", (int) (Math.random() * 0xffffff));
            } while (getListStyle(unique_name) != null);
        } else {
            do {
                unique_name = String.format("a%06x", (int) (Math.random() * 0xffffff));
            } while (getStyle(unique_name, styleFamily) != null);
        }
        return unique_name;
    }
    private DataStyleElement createDataStyleElement(Value type, String numberFormatCode, String newDataStyleName) {
    	Value t = type;
    	if(t == OfficeValueTypeAttribute.Value.VOID) {
    		t = MapHelper.detectFormatType(numberFormatCode);
    	}
    	if(t == OfficeValueTypeAttribute.Value.VOID) {
            return null;
        }
        OdfFileDom fileDom = (OdfFileDom) getOwnerDocument();
        DataStyleElement newStyle = null;
        switch(t) {
            case DATE:
                newStyle = new OdfNumberDateStyle(fileDom, numberFormatCode, newDataStyleName);
            break;
            case BOOLEAN:
                newStyle = new NumberBooleanStyleElement(fileDom, newDataStyleName);
            break;
            case CURRENCY:
                newStyle = new OdfNumberCurrencyStyle(fileDom, numberFormatCode, newDataStyleName);
            break;
            case FLOAT:
                newStyle = new OdfNumberStyle(fileDom, numberFormatCode, newDataStyleName);
            break;
            case PERCENTAGE:
                newStyle = new OdfNumberPercentageStyle(fileDom, numberFormatCode, newDataStyleName);
            break;
            case STRING:
                newStyle = new NumberTextStyleElement(fileDom, numberFormatCode, newDataStyleName);
            break;
            case TIME:
                newStyle = new OdfNumberTimeStyle(fileDom, numberFormatCode, newDataStyleName);
            break;
            case VOID:
                // can never happen, already blocked above
            break;
        }
        return newStyle;
    }
    public DataStyleElement createDataStyle(Value type, String numberFormatCode, String newDataStyleName){
        /* conditional formats:
        - default conditions:
            tow parts: "value()>=0";"value()<0"
            three parts: "value()>0";"value()<0";value()==0
            The last one is in general
        */
        ArrayList<String> partArray = new ArrayList<String>();
        //TODO: skip quoted parts - check fails if semicolon is in quotes
        while(numberFormatCode.contains(";")) {
            int partStart = numberFormatCode.lastIndexOf(";", numberFormatCode.length());
            String part = numberFormatCode.substring(partStart + 1);
            partArray.add(0, part);
            numberFormatCode = numberFormatCode.substring(0, partStart);
        }
        partArray.add(0, numberFormatCode);

        // this is the anchor style in case multiple parts are required
        DataStyleElement newStyle = createDataStyleElement(type, partArray.get(partArray.size() - 1), newDataStyleName);
        if(partArray.size() > 1) {
            OdfFileDom fileDom = (OdfFileDom) getOwnerDocument();
            for( int partIndex = 0; partIndex < partArray.size() - 1; ++partIndex) {
                String partStyleName = newDataStyleName + "P" + partIndex;
                String part = partArray.get(partIndex);
                DataStyleElement partStyle = createDataStyleElement(Value.VOID, part, partStyleName );
                //generate an appropriate condition and add this style as sub style and make part style a volatile style
                String condition = "value()";
                // TODO: extract condition if available (e.g. '[>5]')
                condition += partArray.size() == 1  ? ">=0" : partIndex == 0 ? ">0"  : "<0";

                StyleMapElement styleMap = fileDom.newOdfElement(StyleMapElement.class);
                styleMap.setStyleApplyStyleNameAttribute(partStyleName);
                styleMap.setStyleConditionAttribute(condition);
                newStyle.appendChild(styleMap);
                partStyle.setAttributeNS(OdfDocumentNamespace.STYLE.getUri(), "style:volatile", "true");
                appendChild(partStyle);
            }
        }
        return (DataStyleElement)appendChild(newStyle);
    }

	/**
	 * Create child element {@odf.element number:boolean-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:boolean-style}
	 */
	 public NumberBooleanStyleElement newNumberBooleanStyleElement(String styleNameValue) {
		NumberBooleanStyleElement numberBooleanStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberBooleanStyleElement.class);
		numberBooleanStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberBooleanStyle);
		return numberBooleanStyle;
	}

	/**
	 * Create child element {@odf.element number:currency-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:currency-style}
	 */
	 public NumberCurrencyStyleElement newNumberCurrencyStyleElement(String styleNameValue) {
		NumberCurrencyStyleElement numberCurrencyStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberCurrencyStyleElement.class);
		numberCurrencyStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberCurrencyStyle);
		return numberCurrencyStyle;
	}

	/**
	 * Create child element {@odf.element number:date-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:date-style}
	 */
	 public NumberDateStyleElement newNumberDateStyleElement(String styleNameValue) {
		NumberDateStyleElement numberDateStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberDateStyleElement.class);
		numberDateStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberDateStyle);
		return numberDateStyle;
	}

	/**
	 * Create child element {@odf.element number:number-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:number-style}
	 */
	 public NumberNumberStyleElement newNumberNumberStyleElement(String styleNameValue) {
		NumberNumberStyleElement numberNumberStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberNumberStyleElement.class);
		numberNumberStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberNumberStyle);
		return numberNumberStyle;
	}

	/**
	 * Create child element {@odf.element number:percentage-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:percentage-style}
	 */
	 public NumberPercentageStyleElement newNumberPercentageStyleElement(String styleNameValue) {
		NumberPercentageStyleElement numberPercentageStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberPercentageStyleElement.class);
		numberPercentageStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberPercentageStyle);
		return numberPercentageStyle;
	}

	/**
	 * Create child element {@odf.element number:text-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:text-style}
	 */
	 public NumberTextStyleElement newNumberTextStyleElement(String styleNameValue) {
		NumberTextStyleElement numberTextStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberTextStyleElement.class);
		numberTextStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberTextStyle);
		return numberTextStyle;
	}

	/**
	 * Create child element {@odf.element number:time-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element number:time-style}
	 */
	 public NumberTimeStyleElement newNumberTimeStyleElement(String styleNameValue) {
		NumberTimeStyleElement numberTimeStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(NumberTimeStyleElement.class);
		numberTimeStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(numberTimeStyle);
		return numberTimeStyle;
	}

	/**
	 * Create child element {@odf.element style:page-layout}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element style:page-layout}
	 */
	 public StylePageLayoutElement newStylePageLayoutElement(String styleNameValue) {
		StylePageLayoutElement stylePageLayout = ((OdfFileDom) this.ownerDocument).newOdfElement(StylePageLayoutElement.class);
		stylePageLayout.setStyleNameAttribute(styleNameValue);
		this.appendChild(stylePageLayout);
		return stylePageLayout;
	}

	/**
	 * Create child element {@odf.element style:style}.
	 *
	 * @param styleFamilyValue  the <code>String</code> value of <code>StyleFamilyAttribute</code>, see {@odf.attribute  style:family} at specification
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element style:style}
	 */
	 public StyleStyleElement newStyleStyleElement(String styleFamilyValue, String styleNameValue) {
		StyleStyleElement styleStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(StyleStyleElement.class);
		styleStyle.setStyleFamilyAttribute(styleFamilyValue);
		styleStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(styleStyle);
		return styleStyle;
	}

	/**
	 * Create child element {@odf.element text:list-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element text:list-style}
	 */
	 public TextListStyleElement newTextListStyleElement(String styleNameValue) {
		TextListStyleElement textListStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(TextListStyleElement.class);
		textListStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(textListStyle);
		return textListStyle;
	}

	@Override
	public void accept(ElementVisitor visitor) {
		if (visitor instanceof DefaultElementVisitor) {
			DefaultElementVisitor defaultVisitor = (DefaultElementVisitor) visitor;
			defaultVisitor.visit(this);
		} else {
			visitor.visit(this);
		}
	}
}
