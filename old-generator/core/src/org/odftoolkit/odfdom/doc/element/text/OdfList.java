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
package org.odftoolkit.odfdom.doc.element.text;

import org.odftoolkit.odfdom.doc.OdfFileDom;
import org.odftoolkit.odfdom.doc.element.office.OdfAutomaticStyles;
import org.odftoolkit.odfdom.doc.element.office.OdfStyles;
import org.odftoolkit.odfdom.dom.element.text.OdfListElement;
import org.odftoolkit.odfdom.dom.element.text.OdfListLevelStyleElementBase;
import java.util.logging.Logger;
import org.w3c.dom.Node;

public class OdfList extends OdfListElement {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3490592040586665276L;

	/** Creates a new instance of OdfParagraphElementImpl 
     * @param ownerDoc 
     */
    public OdfList(OdfFileDom ownerDoc) {
        super(ownerDoc);
    }
    public OdfListStyle getListStyle() 
    {
        OdfListStyle style = null;
        
        String listName = getStyleName();
        if (listName != null && listName.length() > 0)
        {
            OdfAutomaticStyles autoStyles = ((OdfFileDom)this.ownerDocument).getAutomaticStyles();
            if( autoStyles != null )
                style = autoStyles.getListStyle(listName);
            
            if( style == null )
            {
                OdfStyles styles = mOdfDocument.getDocumentStyles();
                if( styles != null ) {
                    style = styles.getListStyle(listName);
                }
            }
        }
        else
        {
            // if no style is specified at this particular list element, we
            // ask the parent list (if any)
            OdfList parentList = getParentList();
            if (parentList != null) {
                style = parentList.getListStyle();
            }
        }
        
        return style;
    }

    public int getListLevel() {
        int level = 1;
        Node parent = getParentNode();
        while (parent != null) {
            if (parent instanceof OdfListElement) {
                level++;
            }
            parent = parent.getParentNode();
        }
        return level;
    }

    public OdfListLevelStyleElementBase getListLevelStyle() {
        OdfListLevelStyleElementBase odfListLevelStyle = null;
        OdfListStyle style = getListStyle();
        int level = getListLevel();
        if (style != null) {
            odfListLevelStyle = style.getLevel(level);
        } else {
            Logger.getLogger(OdfList.class.getName()).warning("No ListLevelStyle found!");
        }
        return odfListLevelStyle;
    }

    public OdfListStyle getOrCreateLocalListStyle()
    {
        OdfListStyle listStyle = getListStyle();
        if( listStyle == null )
        {
            OdfAutomaticStyles autoStyles = ((OdfFileDom)this.ownerDocument).getOrCreateAutomaticStyles();
            if( autoStyles != null )
                listStyle = autoStyles.createListStyle();
        }
        return listStyle;
    }

    public OdfList getParentList() {
        Node parent = getParentNode();
        while (parent != null) {
            if (parent instanceof OdfList) {
                return (OdfList) parent;
            }
            parent = parent.getParentNode();
        }
        return null;
    }
}
