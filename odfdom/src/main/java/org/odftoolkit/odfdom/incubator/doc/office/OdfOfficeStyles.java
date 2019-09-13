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
package org.odftoolkit.odfdom.incubator.doc.office;

import java.util.ArrayList;
import java.util.HashMap;
import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawFillImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGradientElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawHatchElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMarkerElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawOpacityElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawStrokeDashElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencyStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDateStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberPercentageStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTimeStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDefaultPageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDefaultStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StylePresentationPageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgLinearGradientElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgRadialGradientElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableTemplateElement;
import org.odftoolkit.odfdom.dom.element.text.TextBibliographyConfigurationElement;
import org.odftoolkit.odfdom.dom.element.text.TextLinenumberingConfigurationElement;
import org.odftoolkit.odfdom.dom.element.text.TextListStyleElement;
import org.odftoolkit.odfdom.dom.element.text.TextNotesConfigurationElement;
import org.odftoolkit.odfdom.dom.element.text.TextOutlineStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.style.OdfDefaultStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextOutlineStyle;
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
abstract public class OdfOfficeStyles extends OdfStylesBase {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.OFFICE, "styles");

    private static final long serialVersionUID = 700763983193326060L;

    // styles that are only in OdfOfficeStyles
    private HashMap<OdfStyleFamily, OdfDefaultStyle> mDefaultStyles;
    private HashMap<String, DrawMarkerElement> mMarker;
    private HashMap<String, DrawGradientElement> mGradients;
    private HashMap<String, DrawHatchElement> mHatches;
    private HashMap<String, DrawFillImageElement> mFillImages;
    private OdfTextOutlineStyle mOutlineStyle;

    public OdfOfficeStyles(OdfFileDom ownerDoc) {
        super(ownerDoc, ELEMENT_NAME);
//        mStylesBaseImpl = new OdfStylesBase();
    }

	@Override
	public OdfName getOdfName() {
		return ELEMENT_NAME;
	}

    /**
     * Retrieve or create ODF default style
     *
     * @param family The style family
     * @return The code>OdfDefaultStyle</code> element
     */
    public OdfDefaultStyle getOrCreateDefaultStyle(OdfStyleFamily family) {
        OdfDefaultStyle style = getDefaultStyle(family);
        if (style == null) {
            style = ((OdfFileDom) this.ownerDocument).newOdfElement(OdfDefaultStyle.class);
            style.setStyleFamilyAttribute(family.getName());
            this.appendChild(style);
        }
        return style;
    }

    /**
     * Create ODF TextListStyle
     *
     * @param name  The style name
     * @return The code>OdfTextListStyle</code> element
     */
    public OdfTextListStyle newListStyle(String name) {
        OdfTextListStyle newStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(OdfTextListStyle.class);
        newStyle.setStyleNameAttribute(name);
        this.appendChild(newStyle);
        return newStyle;
    }

    /**
     * Retrieve or create ODF OutlineStyle
     *
     * @return The code>OdfTextOutlineStyle</code> element
     */
    public OdfTextOutlineStyle getOrCreateOutlineStyle() {
        if (mOutlineStyle == null) {
            this.appendChild(((OdfFileDom) this.ownerDocument).newOdfElement(OdfTextOutlineStyle.class));
        }

        return mOutlineStyle;
    }

    /**
     * Returns the <code>OdfTextOutlineStyle</code> element.
     *
     * @return a pointer to the outline stye or null if there is no such element
     */
    public OdfTextOutlineStyle getOutlineStyle() {
        return mOutlineStyle;
    }

    /**
     * Returns the <code>OdfStyleDefaultStyle</code>  element.
     *
     * @param familyType is the family for the default style
     * @return the default style with the given family or null if there is no such default style
     */
    public OdfDefaultStyle getDefaultStyle(OdfStyleFamily familyType) {
        if (mDefaultStyles != null) {
            return mDefaultStyles.get(familyType);
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator for all <code>OdfStyleDefaultStyle</code> elements.
     *
     * @return iterator for all <code>OdfStyleDefaultStyle</code> elements
     */
    public Iterable<OdfDefaultStyle> getDefaultStyles() {
        if (mDefaultStyles != null) {
            return mDefaultStyles.values();
        } else {
            return new ArrayList<OdfDefaultStyle>();
        }
    }

    /**
     * Returns the <code>DrawMarkerElement</code> element with the given name.
     *
     * @param name is the name of the marker
     * @return the marker or null if there is no such marker
     */
    public DrawMarkerElement getMarker(String name) {
        if (mMarker != null) {
            return mMarker.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator for all <code>DrawMarkerElement</code> elements.
     *
     * @return an iterator for all <code>DrawMarkerElement</code> elements
     */
    public Iterable<DrawMarkerElement> getMarker() {
        if (mMarker != null) {
            return mMarker.values();
        } else {
            return new ArrayList<DrawMarkerElement>();
        }
    }

    /**
     * Returns the <code>DrawGradientElement</code> element with the given name.
     *
     * @param name is the name of the gradient
     * @return the gradient or null if there is no such gradient
     */
    public DrawGradientElement getGradient(String name) {
        if (mGradients != null) {
            return mGradients.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator for all <code>DrawGradientElement</code> elements.
     *
     * @return an iterator for all <code>DrawGradientElement</code> elements
     */
    public Iterable<DrawGradientElement> getGradients() {
        if (mGradients != null) {
            return mGradients.values();
        } else {
            return new ArrayList<DrawGradientElement>();
        }
    }

    /**
     * Returns the <code>DrawHatchElement</code> element with the given name.
     *
     * @param name is the name of the hatch
     * @return the hatch or null if there is no such hatch
     */
    public DrawHatchElement getHatch(String name) {
        if (mHatches != null) {
            return mHatches.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator for all <code>DrawHatchElement</code> elements.
     *
     * @return an iterator for all <code>DrawHatchElement</code> elements
     */
    public Iterable<DrawHatchElement> getHatches() {
        if (mHatches != null) {
            return mHatches.values();
        } else {
            return new ArrayList<DrawHatchElement>();
        }
    }

    /**
     * Returns the <code>DrawFillImageElement</code> element with the given name.
     *
     * @param name is the name of the fill image
     * @return the fill image or null if there is no such fill image
     */
    public DrawFillImageElement getFillImage(String name) {
        if (mFillImages != null) {
            return mFillImages.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns an iterator for all <code>DrawFillImageElement</code> elements.
     *
     * @return an iterator for all <code>DrawFillImageElement</code> elements
     */
    public Iterable<DrawFillImageElement> getFillImages() {
        if (mFillImages != null) {
            return mFillImages.values();
        } else {
            return new ArrayList<DrawFillImageElement>();
        }
    }

    @Override
    public void onOdfNodeInserted(OdfElement node, Node refNode) {
        if (node instanceof OdfDefaultStyle) {
            OdfDefaultStyle defaultStyle = (OdfDefaultStyle) node;
            if (mDefaultStyles == null) {
                mDefaultStyles = new HashMap<OdfStyleFamily, OdfDefaultStyle>();
            }

            mDefaultStyles.put(defaultStyle.getFamily(), defaultStyle);
        } else if (node instanceof DrawMarkerElement) {
            DrawMarkerElement marker = (DrawMarkerElement) node;
            if (mMarker == null) {
                mMarker = new HashMap<String, DrawMarkerElement>();
            }

            mMarker.put(marker.getDrawNameAttribute(), marker);
        } else if (node instanceof DrawGradientElement) {
            DrawGradientElement gradient = (DrawGradientElement) node;
            if (mGradients == null) {
                mGradients = new HashMap<String, DrawGradientElement>();
            }

            mGradients.put(gradient.getDrawNameAttribute(), gradient);
        } else if (node instanceof DrawHatchElement) {
            DrawHatchElement hatch = (DrawHatchElement) node;
            if (mHatches == null) {
                mHatches = new HashMap<String, DrawHatchElement>();
            }

            mHatches.put(hatch.getDrawNameAttribute(), hatch);
        } else if (node instanceof DrawFillImageElement) {
            DrawFillImageElement fillImage = (DrawFillImageElement) node;

            if (mFillImages == null) {
                mFillImages = new HashMap<String, DrawFillImageElement>();
            }

            mFillImages.put(fillImage.getDrawNameAttribute(), fillImage);
        } else if (node instanceof OdfTextOutlineStyle) {
            mOutlineStyle = (OdfTextOutlineStyle) node;
        } else {
            super.onOdfNodeInserted(node, refNode);
        }
    }

    @Override
    public void onOdfNodeRemoved(OdfElement node) {
        if (node instanceof OdfDefaultStyle) {
            if (mDefaultStyles != null) {
                OdfDefaultStyle defaultStyle = (OdfDefaultStyle) node;
                mDefaultStyles.remove(defaultStyle.getFamily());
            }
        } else if (node instanceof DrawMarkerElement) {
            if (mMarker != null) {
                DrawMarkerElement marker = (DrawMarkerElement) node;
                mMarker.remove(marker.getDrawNameAttribute());
            }
        } else if (node instanceof DrawGradientElement) {
            if (mGradients != null) {
                DrawGradientElement gradient = (DrawGradientElement) node;
                mGradients.remove(gradient.getDrawNameAttribute());
            }
        } else if (node instanceof DrawHatchElement) {
            if (mHatches != null) {
                DrawHatchElement hatch = (DrawHatchElement) node;
                mHatches.remove(hatch.getDrawNameAttribute());
            }
        } else if (node instanceof DrawFillImageElement) {
            if (mFillImages != null) {
                DrawFillImageElement fillImage = (DrawFillImageElement) node;
                mFillImages.remove(fillImage.getDrawNameAttribute());
            }
        } else if (node instanceof OdfTextOutlineStyle) {
            if (mOutlineStyle == (OdfTextOutlineStyle) node) {
                mOutlineStyle = null;
            }
        } else {
            super.onOdfNodeRemoved(node);
        }
    }

	@SuppressWarnings("unchecked")
	protected <T extends OdfElement> T getStylesElement(OdfFileDom dom, Class<T> clazz)
		throws Exception {

		OdfElement stylesRoot = dom.getRootElement();

		OdfOfficeStyles contentBody = OdfElement.findFirstChildNode(OdfOfficeStyles.class, stylesRoot);
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
	 * Create child element {@odf.element draw:fill-image}.
	 *
	 * @param drawNameValue  the <code>String</code> value of <code>DrawNameAttribute</code>, see {@odf.attribute  draw:name} at specification
	 * @param xlinkHrefValue  the <code>String</code> value of <code>XlinkHrefAttribute</code>, see {@odf.attribute  xlink:href} at specification
	 * @param xlinkTypeValue  the <code>String</code> value of <code>XlinkTypeAttribute</code>, see {@odf.attribute  xlink:type} at specification
	 * @return the element {@odf.element draw:fill-image}
	 */
	 public DrawFillImageElement newDrawFillImageElement(String drawNameValue, String xlinkHrefValue, String xlinkTypeValue) {
		DrawFillImageElement drawFillImage = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawFillImageElement.class);
		drawFillImage.setDrawNameAttribute(drawNameValue);
		drawFillImage.setXlinkHrefAttribute(xlinkHrefValue);
		drawFillImage.setXlinkTypeAttribute(xlinkTypeValue);
		this.appendChild(drawFillImage);
		return drawFillImage;
	}

	/**
	 * Create child element {@odf.element draw:gradient}.
	 *
	 * @param drawStyleValue  the <code>String</code> value of <code>DrawStyleAttribute</code>, see {@odf.attribute  draw:style} at specification
	 * @return the element {@odf.element draw:gradient}
	 */
	 public DrawGradientElement newDrawGradientElement(String drawStyleValue) {
		DrawGradientElement drawGradient = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawGradientElement.class);
		drawGradient.setDrawStyleAttribute(drawStyleValue);
		this.appendChild(drawGradient);
		return drawGradient;
	}

	/**
	 * Create child element {@odf.element draw:hatch}.
	 *
	 * @param drawNameValue  the <code>String</code> value of <code>DrawNameAttribute</code>, see {@odf.attribute  draw:name} at specification
	 * @param drawStyleValue  the <code>String</code> value of <code>DrawStyleAttribute</code>, see {@odf.attribute  draw:style} at specification
	 * @return the element {@odf.element draw:hatch}
	 */
	 public DrawHatchElement newDrawHatchElement(String drawNameValue, String drawStyleValue) {
		DrawHatchElement drawHatch = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawHatchElement.class);
		drawHatch.setDrawNameAttribute(drawNameValue);
		drawHatch.setDrawStyleAttribute(drawStyleValue);
		this.appendChild(drawHatch);
		return drawHatch;
	}

	/**
	 * Create child element {@odf.element draw:marker}.
	 *
	 * @param drawNameValue  the <code>String</code> value of <code>DrawNameAttribute</code>, see {@odf.attribute  draw:name} at specification
	 * @param svgDValue  the <code>String</code> value of <code>SvgDAttribute</code>, see {@odf.attribute  svg:d} at specification
	 * @param svgViewBoxValue  the <code>Integer</code> value of <code>SvgViewBoxAttribute</code>, see {@odf.attribute  svg:viewBox} at specification
	 * @return the element {@odf.element draw:marker}
	 */
	 public DrawMarkerElement newDrawMarkerElement(String drawNameValue, String svgDValue, int svgViewBoxValue) {
		DrawMarkerElement drawMarker = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawMarkerElement.class);
		drawMarker.setDrawNameAttribute(drawNameValue);
		drawMarker.setSvgDAttribute(svgDValue);
		drawMarker.setSvgViewBoxAttribute(svgViewBoxValue);
		this.appendChild(drawMarker);
		return drawMarker;
	}

	/**
	 * Create child element {@odf.element draw:opacity}.
	 *
	 * @param drawStyleValue  the <code>String</code> value of <code>DrawStyleAttribute</code>, see {@odf.attribute  draw:style} at specification
	 * @return the element {@odf.element draw:opacity}
	 */
	 public DrawOpacityElement newDrawOpacityElement(String drawStyleValue) {
		DrawOpacityElement drawOpacity = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawOpacityElement.class);
		drawOpacity.setDrawStyleAttribute(drawStyleValue);
		this.appendChild(drawOpacity);
		return drawOpacity;
	}

	/**
	 * Create child element {@odf.element draw:stroke-dash}.
	 *
	 * @param drawNameValue  the <code>String</code> value of <code>DrawNameAttribute</code>, see {@odf.attribute  draw:name} at specification
	 * @return the element {@odf.element draw:stroke-dash}
	 */
	 public DrawStrokeDashElement newDrawStrokeDashElement(String drawNameValue) {
		DrawStrokeDashElement drawStrokeDash = ((OdfFileDom) this.ownerDocument).newOdfElement(DrawStrokeDashElement.class);
		drawStrokeDash.setDrawNameAttribute(drawNameValue);
		this.appendChild(drawStrokeDash);
		return drawStrokeDash;
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
	 * Create child element {@odf.element style:default-page-layout}.
	 *
	 * Child element is new in Odf 1.2
	 *
	 * @return the element {@odf.element style:default-page-layout}
	 */
	public StyleDefaultPageLayoutElement newStyleDefaultPageLayoutElement() {
		StyleDefaultPageLayoutElement styleDefaultPageLayout = ((OdfFileDom) this.ownerDocument).newOdfElement(StyleDefaultPageLayoutElement.class);
		this.appendChild(styleDefaultPageLayout);
		return styleDefaultPageLayout;
	}

	/**
	 * Create child element {@odf.element style:default-style}.
	 *
	 * @param styleFamilyValue  the <code>String</code> value of <code>StyleFamilyAttribute</code>, see {@odf.attribute  style:family} at specification
	 * @return the element {@odf.element style:default-style}
	 */
	 public StyleDefaultStyleElement newStyleDefaultStyleElement(String styleFamilyValue) {
		StyleDefaultStyleElement styleDefaultStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(StyleDefaultStyleElement.class);
		styleDefaultStyle.setStyleFamilyAttribute(styleFamilyValue);
		this.appendChild(styleDefaultStyle);
		return styleDefaultStyle;
	}

	/**
	 * Create child element {@odf.element style:presentation-page-layout}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element style:presentation-page-layout}
	 */
	 public StylePresentationPageLayoutElement newStylePresentationPageLayoutElement(String styleNameValue) {
		StylePresentationPageLayoutElement stylePresentationPageLayout = ((OdfFileDom) this.ownerDocument).newOdfElement(StylePresentationPageLayoutElement.class);
		stylePresentationPageLayout.setStyleNameAttribute(styleNameValue);
		this.appendChild(stylePresentationPageLayout);
		return stylePresentationPageLayout;
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
	 * Create child element {@odf.element svg:linearGradient}.
	 *
	 * @param drawNameValue  the <code>String</code> value of <code>DrawNameAttribute</code>, see {@odf.attribute  draw:name} at specification
	 * @return the element {@odf.element svg:linearGradient}
	 */
	 public SvgLinearGradientElement newSvgLinearGradientElement(String drawNameValue) {
		SvgLinearGradientElement svgLinearGradient = ((OdfFileDom) this.ownerDocument).newOdfElement(SvgLinearGradientElement.class);
		svgLinearGradient.setDrawNameAttribute(drawNameValue);
		this.appendChild(svgLinearGradient);
		return svgLinearGradient;
	}

	/**
	 * Create child element {@odf.element svg:radialGradient}.
	 *
	 * @param drawNameValue  the <code>String</code> value of <code>DrawNameAttribute</code>, see {@odf.attribute  draw:name} at specification
	 * @return the element {@odf.element svg:radialGradient}
	 */
	 public SvgRadialGradientElement newSvgRadialGradientElement(String drawNameValue) {
		SvgRadialGradientElement svgRadialGradient = ((OdfFileDom) this.ownerDocument).newOdfElement(SvgRadialGradientElement.class);
		svgRadialGradient.setDrawNameAttribute(drawNameValue);
		this.appendChild(svgRadialGradient);
		return svgRadialGradient;
	}

	/**
	 * Create child element {@odf.element table:table-template}.
	 *
	 * @param tableFirstRowEndColumnValue  the <code>String</code> value of <code>TableFirstRowEndColumnAttribute</code>, see {@odf.attribute  table:first-row-end-column} at specification
	 * @param tableFirstRowStartColumnValue  the <code>String</code> value of <code>TableFirstRowStartColumnAttribute</code>, see {@odf.attribute  table:first-row-start-column} at specification
	 * @param tableLastRowEndColumnValue  the <code>String</code> value of <code>TableLastRowEndColumnAttribute</code>, see {@odf.attribute  table:last-row-end-column} at specification
	 * @param tableLastRowStartColumnValue  the <code>String</code> value of <code>TableLastRowStartColumnAttribute</code>, see {@odf.attribute  table:last-row-start-column} at specification
	 * @param tableNameValue  the <code>String</code> value of <code>TableNameAttribute</code>, see {@odf.attribute  table:name} at specification
	 * Child element is new in Odf 1.2
	 *
	 * @return the element {@odf.element table:table-template}
	 */
	 public TableTableTemplateElement newTableTableTemplateElement(String tableFirstRowEndColumnValue, String tableFirstRowStartColumnValue, String tableLastRowEndColumnValue, String tableLastRowStartColumnValue, String tableNameValue) {
		TableTableTemplateElement tableTableTemplate = ((OdfFileDom) this.ownerDocument).newOdfElement(TableTableTemplateElement.class);
		tableTableTemplate.setTableFirstRowEndColumnAttribute(tableFirstRowEndColumnValue);
		tableTableTemplate.setTableFirstRowStartColumnAttribute(tableFirstRowStartColumnValue);
		tableTableTemplate.setTableLastRowEndColumnAttribute(tableLastRowEndColumnValue);
		tableTableTemplate.setTableLastRowStartColumnAttribute(tableLastRowStartColumnValue);
		tableTableTemplate.setTableNameAttribute(tableNameValue);
		this.appendChild(tableTableTemplate);
		return tableTableTemplate;
	}

	/**
	 * Create child element {@odf.element text:bibliography-configuration}.
	 *
	 * @return the element {@odf.element text:bibliography-configuration}
	 */
	public TextBibliographyConfigurationElement newTextBibliographyConfigurationElement() {
		TextBibliographyConfigurationElement textBibliographyConfiguration = ((OdfFileDom) this.ownerDocument).newOdfElement(TextBibliographyConfigurationElement.class);
		this.appendChild(textBibliographyConfiguration);
		return textBibliographyConfiguration;
	}

	/**
	 * Create child element {@odf.element text:linenumbering-configuration}.
	 *
	 * @param styleNumFormatValue  the <code>String</code> value of <code>StyleNumFormatAttribute</code>, see {@odf.attribute  style:num-format} at specification
	 * @return the element {@odf.element text:linenumbering-configuration}
	 */
	 public TextLinenumberingConfigurationElement newTextLinenumberingConfigurationElement(String styleNumFormatValue) {
		TextLinenumberingConfigurationElement textLinenumberingConfiguration = ((OdfFileDom) this.ownerDocument).newOdfElement(TextLinenumberingConfigurationElement.class);
		textLinenumberingConfiguration.setStyleNumFormatAttribute(styleNumFormatValue);
		this.appendChild(textLinenumberingConfiguration);
		return textLinenumberingConfiguration;
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

	/**
	 * Create child element {@odf.element text:notes-configuration}.
	 *
	 * @param styleNumFormatValue  the <code>String</code> value of <code>StyleNumFormatAttribute</code>, see {@odf.attribute  style:num-format} at specification
	 * @param textNoteClassValue  the <code>String</code> value of <code>TextNoteClassAttribute</code>, see {@odf.attribute  text:note-class} at specification
	 * @return the element {@odf.element text:notes-configuration}
	 */
	 public TextNotesConfigurationElement newTextNotesConfigurationElement(String styleNumFormatValue, String textNoteClassValue) {
		TextNotesConfigurationElement textNotesConfiguration = ((OdfFileDom) this.ownerDocument).newOdfElement(TextNotesConfigurationElement.class);
		textNotesConfiguration.setStyleNumFormatAttribute(styleNumFormatValue);
		textNotesConfiguration.setTextNoteClassAttribute(textNoteClassValue);
		this.appendChild(textNotesConfiguration);
		return textNotesConfiguration;
	}

	/**
	 * Create child element {@odf.element text:outline-style}.
	 *
	 * @param styleNameValue  the <code>String</code> value of <code>StyleNameAttribute</code>, see {@odf.attribute  style:name} at specification
	 * @return the element {@odf.element text:outline-style}
	 */
	 public TextOutlineStyleElement newTextOutlineStyleElement(String styleNameValue) {
		TextOutlineStyleElement textOutlineStyle = ((OdfFileDom) this.ownerDocument).newOdfElement(TextOutlineStyleElement.class);
		textOutlineStyle.setStyleNameAttribute(styleNameValue);
		this.appendChild(textOutlineStyle);
		return textOutlineStyle;
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
