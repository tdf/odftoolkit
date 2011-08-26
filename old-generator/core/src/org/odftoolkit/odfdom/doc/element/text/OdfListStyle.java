
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
import org.odftoolkit.odfdom.dom.element.OdfStyleBase;
import org.odftoolkit.odfdom.dom.element.text.OdfListLevelStyleElementBase;
import org.odftoolkit.odfdom.dom.element.text.OdfListStyleElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.w3c.dom.Node;

/**
 *
 */
public class OdfListStyle extends OdfListStyleElement
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5493176392198676430L;

	public OdfListStyle( OdfFileDom _aOwnerDoc )
    {
        super( _aOwnerDoc );
    }

    /** returns the given level or null if it does not exist
     * 
     * @param level is the level number that should be returned
     * @return an instance of OdfListLevelStyleImageElement,
     *         OdfListLevelStyleBulletElement, OdfListLevelStyleNumberElement or
     *         null.
     */
    public OdfListLevelStyleElementBase getLevel(int level )
    {
        Node levelElement = this.getFirstChild();
        
        while( levelElement != null )
        {
            if( levelElement instanceof OdfListLevelStyleElementBase )
            {
                if( level == 1 ) {
                    return (OdfListLevelStyleElementBase) levelElement;
                }
                else {
                    --level;
                }
                
            }
            levelElement = levelElement.getNextSibling();
        }
        return null;
    }

    /** always returns the given level with the given class. If that level does
     *  not exist or has a different class than it is (re)created.
     * 
     * @param level is the level number that should be returned
     * @param clazz is the class of the level, should be
     *        OdfListLevelStyleImageElement, OdfListLevelStyleBulletElement or
     *        OdfListLevelStyleNumberElement.
     * @return
     *        a list level style with the given level and class
     */
    @SuppressWarnings("unchecked")
    public OdfListLevelStyleElementBase getOrCreateListLevel( int level, Class clazz )
    {
        OdfListLevelStyleElementBase levelStyle = getLevel( level );
        if( (levelStyle != null) && clazz.isInstance(levelStyle) ) {
            return levelStyle;
        }
        
        if( levelStyle != null ) {
            removeChild(levelStyle);
        }
        
        levelStyle = (OdfListLevelStyleElementBase)
                        ((OdfFileDom)this.ownerDocument).createOdfElement(clazz);
        levelStyle.setLevel(level);
        appendChild(levelStyle);
        
        return levelStyle;
    }
    
    @Override
    public OdfStyleFamily getFamily()
    {
        return OdfStyleFamily.List;
    }

    @Override
    public OdfStyleBase getParentStyle()
    {
        return null;
    }
}
