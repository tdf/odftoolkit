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
package org.odftoolkit.odfdom.incubator.doc.number;

import java.util.List;
import org.odftoolkit.odfdom.dom.element.number.DataStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberPercentageStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.w3c.dom.Node;

/**
 * Convenient functionality for the parent ODF OpenDocument element
 *
 */
public class OdfNumberPercentageStyle extends NumberPercentageStyleElement {

	public OdfNumberPercentageStyle(OdfFileDom ownerDoc) {
		super(ownerDoc);
	}

	/**
	 * Creates a new instance of OdfNumberPercentageStyle.
	 * @param ownerDoc document that this format belongs to
	 * @param format format string for the date/time
	 * @param styleName name of this style
	 */
	public OdfNumberPercentageStyle(OdfFileDom ownerDoc, String format, String styleName) {
		super(ownerDoc);
		this.setStyleNameAttribute(styleName);
		setFormat(format);
	}

	/**
	 * Get the format string that represents this style.
	 * @param with capitals
     * @return the format string
	 */
    @Override
    public String getFormat(boolean caps) {
        String result = "";
        String mappedResult = "";
        Node m = getFirstChild();
        while (m != null) {
            if (m instanceof NumberNumberElement) {
                result += getNumberFormat();
            } else if (m instanceof NumberTextElement) {
                String textcontent = m.getTextContent();
                if (textcontent == null || textcontent.length() == 0) {
                    textcontent = " ";
                }
                result += textcontent;
            } else if (m instanceof StyleTextPropertiesElement) {
                result += getColorFromElement((StyleTextPropertiesElement)m);
            } else if(m instanceof StyleMapElement) {
                mappedResult += getMapping((StyleMapElement)m);
                mappedResult += ";";
            }
            m = m.getNextSibling();
        }
        if(!mappedResult.isEmpty()){
            result = mappedResult + result;
        }
        return result;
    }


	/**
	 * Creates a &lt;number:number-style&gt; element based upon format.
	 * @param format the number format string
	 */
	@Override
    public void setFormat(String format) {
        List<StringToken> tokens = tokenize(format, DataStyleElement.NumberFormatType.FORMAT_PERCENT);
        emitTokens(tokens, DataStyleElement.NumberFormatType.FORMAT_PERCENT);

	}


	/**
	 * Set &lt;style:map&gt; for positive values to the given style name.
	 * @param mapName the style name to map to
	 */
	public void setMapPositive(String mapName) {
		StyleMapElement map = new StyleMapElement((OdfFileDom) this.getOwnerDocument());
		map.setStyleApplyStyleNameAttribute(mapName);
		map.setStyleConditionAttribute("value()>0");
		this.appendChild(map);
	}

	/**
	 * Set &lt;style:map&gt; for negative values to the given style name.
	 * @param mapName the style name to map to
	 */
	public void setMapNegative(String mapName) {
		StyleMapElement map = new StyleMapElement((OdfFileDom) this.getOwnerDocument());
		map.setStyleApplyStyleNameAttribute(mapName);
		map.setStyleConditionAttribute("value()<0");
		this.appendChild(map);
	}
}
