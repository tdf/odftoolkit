/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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
package org.odftoolkit.odfdom.doc.element.office;

import java.util.HashMap;
import java.util.Vector;
import org.odftoolkit.odfdom.doc.element.number.OdfBooleanStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfCurrencyStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfDateStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfNumberStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfPercentageStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfTextStyle;
import org.odftoolkit.odfdom.doc.element.number.OdfTimeStyle;
import org.odftoolkit.odfdom.doc.element.style.OdfStyle;
import org.odftoolkit.odfdom.doc.element.text.OdfListStyle;
import org.odftoolkit.odfdom.dom.element.OdfElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.Node;

/**
 * Implements shared functions for OdfAutomaticStyles and OdfStyles.
 */
class OdfStylesBase {

    private HashMap<OdfStyleFamily, HashMap<String, OdfStyle>> mStyles;
    private HashMap<String, OdfListStyle> mListStyles;
    private HashMap<String, OdfNumberStyle> mNumberStyles;
    private HashMap<String, OdfDateStyle> mDateStyles;
    private HashMap<String, OdfPercentageStyle> mPercentageStyles;
    private HashMap<String, OdfCurrencyStyle> mCurrencyStyles;
    private HashMap<String, OdfTimeStyle> mTimeStyles;
    private HashMap<String, OdfBooleanStyle> mBooleanStyles;
    private HashMap<String, OdfTextStyle> mTextStyles;

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfStyle getStyle(String name, OdfStyleFamily familyType) {
        if (mStyles != null) {
            HashMap<String, OdfStyle> familyMap = mStyles.get(familyType);
            if (familyMap != null) {
                return familyMap.get(name);
            }
        }
        return null;
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfStyle> getStylesForFamily(OdfStyleFamily familyType) {
        if (mStyles != null) {
            HashMap<String, OdfStyle> familyMap = mStyles.get(familyType);
            if (familyMap != null) {
                return familyMap.values();
            }
        }
        return new Vector<OdfStyle>();
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfListStyle getListStyle(String name) {
        if (mListStyles != null) {
            return mListStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfListStyle> getListStyles() {
        if (mListStyles != null) {
            return mListStyles.values();
        } else {
            return new Vector<OdfListStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfNumberStyle getNumberStyle(String name) {
        if (mNumberStyles != null) {
            return mNumberStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfNumberStyle> getNumberStyles() {
        if (mNumberStyles != null) {
            return mNumberStyles.values();
        } else {
            return new Vector<OdfNumberStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfDateStyle getDateStyle(String name) {
        if (mDateStyles != null) {
            return mDateStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfDateStyle> getDateStyles() {
        if (mDateStyles != null) {
            return mDateStyles.values();
        } else {
            return new Vector<OdfDateStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfPercentageStyle getPercentageStyle(String name) {
        if (mPercentageStyles != null) {
            return mPercentageStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfPercentageStyle> getPercentageStyles() {
        if (mPercentageStyles != null) {
            return mPercentageStyles.values();
        } else {
            return new Vector<OdfPercentageStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfCurrencyStyle getCurrencyStyle(String name) {
        if (mCurrencyStyles != null) {
            return mCurrencyStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfCurrencyStyle> getCurrencyStyles() {
        if (mCurrencyStyles != null) {
            return mCurrencyStyles.values();
        } else {
            return new Vector<OdfCurrencyStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfTimeStyle getTimeStyle(String name) {
        if (mTimeStyles != null) {
            return mTimeStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfTimeStyle> getTimeStyles() {
        if (mTimeStyles != null) {
            return mTimeStyles.values();
        } else {
            return new Vector<OdfTimeStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfBooleanStyle getBooleanStyle(String name) {
        if (mBooleanStyles != null) {
            return mBooleanStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfBooleanStyle> getBooleanStyles() {
        if (mBooleanStyles != null) {
            return mBooleanStyles.values();
        } else {
            return new Vector<OdfBooleanStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    OdfTextStyle getTextStyle(String name) {
        if (mTextStyles != null) {
            return mTextStyles.get(name);
        } else {
            return null;
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    Iterable<OdfTextStyle> getTextStyles() {
        if (mTextStyles != null) {
            return mTextStyles.values();
        } else {
            return new Vector<OdfTextStyle>();
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    void onOdfNodeInserted(OdfElement node, Node refNode) {
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

            familyMap.put(style.getName(), style);
        } else if (node instanceof OdfListStyle) {
            OdfListStyle listStyle = (OdfListStyle) node;
            if (mListStyles == null) {
                mListStyles = new HashMap<String, OdfListStyle>();
            }

            mListStyles.put(listStyle.getName(), listStyle);
        } else if (node instanceof OdfNumberStyle) {
            OdfNumberStyle numberStyle = (OdfNumberStyle) node;

            if (mNumberStyles == null) {
                mNumberStyles = new HashMap<String, OdfNumberStyle>();
            }

            mNumberStyles.put(numberStyle.getName(), numberStyle);
        } else if (node instanceof OdfDateStyle) {
            OdfDateStyle dateStyle = (OdfDateStyle) node;

            if (mDateStyles == null) {
                mDateStyles = new HashMap<String, OdfDateStyle>();
            }

            mDateStyles.put(dateStyle.getName(), dateStyle);
        } else if (node instanceof OdfPercentageStyle) {
            OdfPercentageStyle percentageStyle = (OdfPercentageStyle) node;

            if (mPercentageStyles == null) {
                mPercentageStyles = new HashMap<String, OdfPercentageStyle>();
            }

            mPercentageStyles.put(percentageStyle.getName(), percentageStyle);
        } else if (node instanceof OdfCurrencyStyle) {
            OdfCurrencyStyle currencyStyle = (OdfCurrencyStyle) node;

            if (mCurrencyStyles == null) {
                mCurrencyStyles = new HashMap<String, OdfCurrencyStyle>();
            }

            mCurrencyStyles.put(currencyStyle.getName(), currencyStyle);
        } else if (node instanceof OdfTimeStyle) {
            OdfTimeStyle timeStyle = (OdfTimeStyle) node;

            if (mTimeStyles == null) {
                mTimeStyles = new HashMap<String, OdfTimeStyle>();
            }

            mTimeStyles.put(timeStyle.getName(), timeStyle);
        } else if (node instanceof OdfBooleanStyle) {
            OdfBooleanStyle booleanStyle = (OdfBooleanStyle) node;

            if (mBooleanStyles == null) {
                mBooleanStyles = new HashMap<String, OdfBooleanStyle>();
            }

            mBooleanStyles.put(booleanStyle.getName(), booleanStyle);
        } else if (node instanceof OdfTextStyle) {
            OdfTextStyle textStyle = (OdfTextStyle) node;

            if (mTextStyles == null) {
                mTextStyles = new HashMap<String, OdfTextStyle>();
            }

            mTextStyles.put(textStyle.getName(), textStyle);
        }
    }

    // For documentation see OdfAutomaticStyles or OdfStyles.
    void onOdfNodeRemoved(OdfElement node) {
        if (node instanceof OdfStyle) {
            if (mStyles != null) {
                OdfStyle style = (OdfStyle) node;
                HashMap<String, OdfStyle> familyMap = mStyles.get(style.getFamily());
                if (familyMap != null) {
                    familyMap.remove(style.getName());
                    if (familyMap.isEmpty()) {
                        mStyles.remove(style.getFamily());
                    }
                }
            }
        } else if (node instanceof OdfListStyle) {
            if (mListStyles != null) {
                OdfListStyle listStyle = (OdfListStyle) node;
                mListStyles.remove(listStyle.getName());
            }
        } else if (node instanceof OdfNumberStyle) {
            if (mNumberStyles != null) {
                OdfNumberStyle numberStyle = (OdfNumberStyle) node;
                mNumberStyles.remove(numberStyle.getName());
            }
        } else if (node instanceof OdfDateStyle) {
            if (mDateStyles != null) {
                OdfDateStyle dateStyle = (OdfDateStyle) node;
                mDateStyles.remove(dateStyle.getName());
            }
        } else if (node instanceof OdfPercentageStyle) {
            if (mPercentageStyles != null) {
                OdfPercentageStyle percentageStyle = (OdfPercentageStyle) node;
                mPercentageStyles.remove(percentageStyle.getName());
            }
        } else if (node instanceof OdfCurrencyStyle) {
            if (mCurrencyStyles != null) {
                OdfCurrencyStyle currencyStyle = (OdfCurrencyStyle) node;
                mCurrencyStyles.remove(currencyStyle.getName());
            }
        } else if (node instanceof OdfTimeStyle) {
            if (mTimeStyles != null) {
                OdfTimeStyle timeStyle = (OdfTimeStyle) node;
                mTimeStyles.remove(timeStyle.getName());
            }
        } else if (node instanceof OdfBooleanStyle) {
            if (mBooleanStyles != null) {
                OdfBooleanStyle booleanStyle = (OdfBooleanStyle) node;
                mBooleanStyles.remove(booleanStyle.getName());
            }
        } else if (node instanceof OdfTextStyle) {
            if (mTextStyles != null) {
                OdfTextStyle textStyle = (OdfTextStyle) node;
                mTextStyles.remove(textStyle.getName());
            }
        }
    }
}
