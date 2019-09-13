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
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.pkg.OdfAttribute;
import org.odftoolkit.odfdom.pkg.OdfContainerElementBase;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.number.DataStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberCurrencyStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberTimeStyle;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.w3c.dom.Node;

/**
 * Implements shared functions for OdfAutomaticStyles and OdfStyles.
 */
abstract public class OdfStylesBase extends OdfContainerElementBase {

	private static final long serialVersionUID = 1L;

	private HashMap<OdfStyleFamily, HashMap<String, OdfStyle>> mStyles;
	private HashMap<String, OdfTextListStyle> mListStyles;
	private HashMap<String, OdfNumberStyle> mNumberStyles;
	private HashMap<String, OdfNumberDateStyle> mDateStyles;
	private HashMap<String, OdfNumberPercentageStyle> mPercentageStyles;
	private HashMap<String, OdfNumberCurrencyStyle> mCurrencyStyles;
	private HashMap<String, OdfNumberTimeStyle> mTimeStyles;
	private HashMap<String, NumberBooleanStyleElement> mBooleanStyles;
	private HashMap<String, NumberTextStyleElement> mTextStyles;

    private HashMap<String, DataStyleElement> mAllDataStyles = new HashMap<String, DataStyleElement> ();

	public OdfStylesBase(OdfFileDom ownerDoc, OdfName odfName) {
		super(ownerDoc, odfName);
	}

    /**
    *
    * @return a set of all names of the contained 'number' styles (text, date, time, ...)
    */
   public HashMap<String, DataStyleElement> getAllDataStyles() {
       return mAllDataStyles;
   }

   /**
    * Create an ODF style with style name and family
    *
    * @param name  The style name
    * @param family The style family
    * @return  The <code>OdfStyle</code> element
    */
   public OdfStyle newStyle(String name, OdfStyleFamily family) {
	   OdfStyle oldStyle = this.getStyle(name, family);
		if(oldStyle != null){
			this.removeChild(oldStyle);
		}
		OdfFileDom fileDom = ((OdfFileDom) this.ownerDocument);
		OdfStyle newStyle = fileDom.newOdfElement(OdfStyle.class);
		newStyle.setStyleNameAttribute(name);
		newStyle.setStyleFamilyAttribute(family.getName());
		StyleStyleElement firstStyle;
		try {
			firstStyle = getStylesElement(fileDom, StyleStyleElement.class);
			if(firstStyle == null){
				insertBefore(newStyle, firstStyle);
			}else{
				appendChild(newStyle);
			}

		} catch (Exception ex) {
			Logger.getLogger(OdfOfficeStyles.class.getName()).log(Level.SEVERE, null, ex);
		}
		return newStyle;
   	}

   	protected abstract <T extends OdfElement> T getStylesElement(OdfFileDom dom, Class<T> clazz) throws Exception;

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfStyle getStyle(String name, OdfStyleFamily familyType) {
		if (mStyles!=null&&name!=null) {
			HashMap<String, OdfStyle> familyMap = mStyles.get(familyType);
			if (familyMap != null) {
				return familyMap.get(name);
				}
			}
		return null;
	}

	/** Returns an iterator for all <code>OdfStyle</code> elements.
	 *
	 * @return iterator for all <code>OdfStyle</code> elements
	 */
	public Iterable<OdfStyle> getAllStyles() {
		ArrayList<OdfStyle> allStyles = new ArrayList<OdfStyle>();
		if (mStyles != null) {
			for (OdfStyleFamily family : mStyles.keySet()) {
				HashMap<String, OdfStyle> familySet = mStyles.get(family);
				Collection<OdfStyle> familyStyles = familySet.values();
				allStyles.addAll(familyStyles);
			}
		}
		return allStyles;
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfStyle> getStylesForFamily(OdfStyleFamily familyType) {
		if (mStyles != null) {
			HashMap<String, OdfStyle> familyMap = mStyles.get(familyType);
			if (familyMap != null) {
				return familyMap.values();
			}
		}
		return new ArrayList<OdfStyle>();
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfTextListStyle getListStyle(String name) {
		if (mListStyles != null) {
			return mListStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfTextListStyle> getListStyles() {
		if (mListStyles != null) {
			return mListStyles.values();
		} else {
			return new ArrayList<OdfTextListStyle>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfNumberStyle getNumberStyle(String name) {
		if (mNumberStyles != null) {
			return mNumberStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfNumberStyle> getNumberStyles() {
		if (mNumberStyles != null) {
			return mNumberStyles.values();
		} else {
			return new ArrayList<OdfNumberStyle>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfNumberDateStyle getDateStyle(String name) {
		if (mDateStyles != null) {
			return mDateStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfNumberDateStyle> getDateStyles() {
		if (mDateStyles != null) {
			return mDateStyles.values();
		} else {
			return new ArrayList<OdfNumberDateStyle>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfNumberPercentageStyle getPercentageStyle(String name) {
		if (mPercentageStyles != null) {
			return mPercentageStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfNumberPercentageStyle> getPercentageStyles() {
		if (mPercentageStyles != null) {
			return mPercentageStyles.values();
		} else {
			return new ArrayList<OdfNumberPercentageStyle>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfNumberCurrencyStyle getCurrencyStyle(String name) {
		if (mCurrencyStyles != null) {
			return mCurrencyStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfNumberCurrencyStyle> getCurrencyStyles() {
		if (mCurrencyStyles != null) {
			return mCurrencyStyles.values();
		} else {
			return new ArrayList<OdfNumberCurrencyStyle>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public OdfNumberTimeStyle getTimeStyle(String name) {
		if (mTimeStyles != null) {
			return mTimeStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<OdfNumberTimeStyle> getTimeStyles() {
		if (mTimeStyles != null) {
			return mTimeStyles.values();
		} else {
			return new ArrayList<OdfNumberTimeStyle>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public NumberBooleanStyleElement getBooleanStyle(String name) {
		if (mBooleanStyles != null) {
			return mBooleanStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<NumberBooleanStyleElement> getBooleanStyles() {
		if (mBooleanStyles != null) {
			return mBooleanStyles.values();
		} else {
			return new ArrayList<NumberBooleanStyleElement>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	NumberTextStyleElement getTextStyle(String name) {
		if (mTextStyles != null) {
			return mTextStyles.get(name);
		} else {
			return null;
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	public Iterable<NumberTextStyleElement> getTextStyles() {
		if (mTextStyles != null) {
			return mTextStyles.values();
		} else {
			return new ArrayList<NumberTextStyleElement>();
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	@Override
	public void onOdfNodeInserted(OdfElement node, Node refNode) {
        if(node instanceof DataStyleElement ) {
            OdfAttribute name = node.getOdfAttribute(OdfDocumentNamespace.STYLE, "name");
            mAllDataStyles.put(name.getValue(), (DataStyleElement)node);
        }
		if (node instanceof OdfStyle) {
			OdfStyle style = (OdfStyle) node;
			if (mStyles == null) {
				mStyles = new HashMap<OdfStyleFamily, HashMap<String, OdfStyle>>();
			}

			HashMap<String, OdfStyle> familyMap = mStyles.get(style.getFamily());
			if (familyMap == null) {
				familyMap = new HashMap<String, OdfStyle>();
				mStyles.put(style.getFamily(), familyMap);
			}

			familyMap.put(style.getStyleNameAttribute(), style);
		} else if (node instanceof OdfTextListStyle) {
			OdfTextListStyle listStyle = (OdfTextListStyle) node;
			if (mListStyles == null) {
				mListStyles = new HashMap<String, OdfTextListStyle>();
			}

			mListStyles.put(listStyle.getStyleNameAttribute(), listStyle);
		} else if (node instanceof OdfNumberStyle) {
			OdfNumberStyle numberStyle = (OdfNumberStyle) node;

			if (mNumberStyles == null) {
				mNumberStyles = new HashMap<String, OdfNumberStyle>();
			}

			mNumberStyles.put(numberStyle.getStyleNameAttribute(), numberStyle);
		} else if (node instanceof OdfNumberDateStyle) {
			OdfNumberDateStyle dateStyle = (OdfNumberDateStyle) node;

			if (mDateStyles == null) {
				mDateStyles = new HashMap<String, OdfNumberDateStyle>();
			}

			mDateStyles.put(dateStyle.getStyleNameAttribute(), dateStyle);
		} else if (node instanceof OdfNumberPercentageStyle) {
			OdfNumberPercentageStyle percentageStyle = (OdfNumberPercentageStyle) node;

			if (mPercentageStyles == null) {
				mPercentageStyles = new HashMap<String, OdfNumberPercentageStyle>();
			}

			mPercentageStyles.put(percentageStyle.getStyleNameAttribute(), percentageStyle);
		} else if (node instanceof OdfNumberCurrencyStyle) {
			OdfNumberCurrencyStyle currencyStyle = (OdfNumberCurrencyStyle) node;

			if (mCurrencyStyles == null) {
				mCurrencyStyles = new HashMap<String, OdfNumberCurrencyStyle>();
			}

			mCurrencyStyles.put(currencyStyle.getStyleNameAttribute(), currencyStyle);
		} else if (node instanceof OdfNumberTimeStyle) {
			OdfNumberTimeStyle timeStyle = (OdfNumberTimeStyle) node;

			if (mTimeStyles == null) {
				mTimeStyles = new HashMap<String, OdfNumberTimeStyle>();
			}

			mTimeStyles.put(timeStyle.getStyleNameAttribute(), timeStyle);
		} else if (node instanceof NumberBooleanStyleElement) {
			NumberBooleanStyleElement booleanStyle = (NumberBooleanStyleElement) node;

			if (mBooleanStyles == null) {
				mBooleanStyles = new HashMap<String, NumberBooleanStyleElement>();
			}

			mBooleanStyles.put(booleanStyle.getStyleNameAttribute(), booleanStyle);
		} else if (node instanceof NumberTextStyleElement) {
			NumberTextStyleElement textStyle = (NumberTextStyleElement) node;

			if (mTextStyles == null) {
				mTextStyles = new HashMap<String, NumberTextStyleElement>();
			}

			mTextStyles.put(textStyle.getStyleNameAttribute(), textStyle);
		}
	}

	// For documentation see OdfAutomaticStyles or OdfStyles.
	@Override
	public void onOdfNodeRemoved(OdfElement node) {
        if(node instanceof DataStyleElement ) {
            OdfAttribute name = node.getOdfAttribute(OdfDocumentNamespace.STYLE, "name");
            mAllDataStyles.remove(name.getValue());
        }
		if (node instanceof OdfStyle) {
			if (mStyles != null) {
				OdfStyle style = (OdfStyle) node;
				HashMap<String, OdfStyle> familyMap = mStyles.get(style.getFamily());
				if (familyMap != null) {
					familyMap.remove(style.getStyleNameAttribute());
					if (familyMap.isEmpty()) {
						mStyles.remove(style.getFamily());
					}
				}
			}
		} else if (node instanceof OdfTextListStyle) {
			if (mListStyles != null) {
				OdfTextListStyle listStyle = (OdfTextListStyle) node;
				mListStyles.remove(listStyle.getStyleNameAttribute());
			}
		} else if (node instanceof OdfNumberStyle) {
			if (mNumberStyles != null) {
				OdfNumberStyle numberStyle = (OdfNumberStyle) node;
				mNumberStyles.remove(numberStyle.getStyleNameAttribute());
			}
		} else if (node instanceof OdfNumberDateStyle) {
			if (mDateStyles != null) {
				OdfNumberDateStyle dateStyle = (OdfNumberDateStyle) node;
				mDateStyles.remove(dateStyle.getStyleNameAttribute());
			}
		} else if (node instanceof OdfNumberPercentageStyle) {
			if (mPercentageStyles != null) {
				OdfNumberPercentageStyle percentageStyle = (OdfNumberPercentageStyle) node;
				mPercentageStyles.remove(percentageStyle.getStyleNameAttribute());
			}
		} else if (node instanceof OdfNumberCurrencyStyle) {
			if (mCurrencyStyles != null) {
				OdfNumberCurrencyStyle currencyStyle = (OdfNumberCurrencyStyle) node;
				mCurrencyStyles.remove(currencyStyle.getStyleNameAttribute());
			}
		} else if (node instanceof OdfNumberTimeStyle) {
			if (mTimeStyles != null) {
				OdfNumberTimeStyle timeStyle = (OdfNumberTimeStyle) node;
				mTimeStyles.remove(timeStyle.getStyleNameAttribute());
			}
		} else if (node instanceof NumberBooleanStyleElement) {
			if (mBooleanStyles != null) {
				NumberBooleanStyleElement booleanStyle = (NumberBooleanStyleElement) node;
				mBooleanStyles.remove(booleanStyle.getStyleNameAttribute());
			}
		} else if (node instanceof NumberTextStyleElement) {
			if (mTextStyles != null) {
				NumberTextStyleElement textStyle = (NumberTextStyleElement) node;
				mTextStyles.remove(textStyle.getStyleNameAttribute());
			}
		}
	}
}
