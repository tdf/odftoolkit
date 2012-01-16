
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
import java.util.Iterator;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.dom.element.draw.DrawLayerSetElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHandoutMasterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.w3c.dom.Node;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 */
public class OdfOfficeMasterStyles extends OfficeMasterStylesElement
{
    
	private static final long serialVersionUID = 6598785919980862801L;
	private DrawLayerSetElement mLayerSet;
    private StyleHandoutMasterElement mHandoutMaster;
    private HashMap< String, StyleMasterPageElement > mMasterPages;
    
    public OdfOfficeMasterStyles( OdfFileDom ownerDoc )
    {
        super( ownerDoc );
    }

    public StyleHandoutMasterElement getHandoutMaster()
    {
        return mHandoutMaster;
    }

    public DrawLayerSetElement getLayerSet()
    {
        return mLayerSet;
    }
    
    public StyleMasterPageElement getMasterPage( String name )
    {
        if( mMasterPages != null )
            return mMasterPages.get(name);
        else
            return null;
    }

    public Iterator< StyleMasterPageElement > getMasterPages()
    {
        if( mMasterPages != null )
            return mMasterPages.values().iterator();
        else
            return new ArrayList< StyleMasterPageElement >().iterator();
    }            
    
    /** override this method to get notified about element insertion
     */
    @Override
	protected void onOdfNodeInserted( OdfElement node, Node refNode )
    {
        if( node instanceof DrawLayerSetElement )
        {
            mLayerSet = (DrawLayerSetElement)node;
        }
        else if( node instanceof StyleHandoutMasterElement )
        {
            mHandoutMaster = (StyleHandoutMasterElement)node;
        }
        else if( node instanceof StyleMasterPageElement )
        {
            StyleMasterPageElement masterPage = (StyleMasterPageElement)node;
            
            if( mMasterPages == null )
                mMasterPages = new HashMap< String, StyleMasterPageElement >();
            
            mMasterPages.put( masterPage.getStyleNameAttribute(), masterPage );
        }
    }
            
    /** override this method to get notified about element insertion
     */
    @Override
	protected void onOdfNodeRemoved( OdfElement node )
    {
        if( node instanceof DrawLayerSetElement )
        {
            if( mLayerSet == (DrawLayerSetElement)node )
                mLayerSet = null;
        }
        else if( node instanceof StyleHandoutMasterElement )
        {
            if( mHandoutMaster == (StyleHandoutMasterElement)node )
                mHandoutMaster = null;
        }
        else if( node instanceof StyleMasterPageElement )
        {
            if( mMasterPages != null )
            {
                StyleMasterPageElement masterPage = (StyleMasterPageElement)node;
                mMasterPages.remove( masterPage.getStyleNameAttribute() );            
            }
        }
    }    
}
