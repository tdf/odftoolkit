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
package org.openoffice.odf.doc;

import java.lang.reflect.Field;
import org.apache.xerces.dom.DocumentImpl;
import org.openoffice.odf.doc.element.OdfElementFactory;
import org.openoffice.odf.doc.element.office.OdfAutomaticStyles;
import org.openoffice.odf.doc.element.office.OdfBody;
import org.openoffice.odf.doc.element.office.OdfMasterStyles;
import org.openoffice.odf.dom.OdfName;
import org.openoffice.odf.dom.element.OdfElement;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class OdfFileDom extends DocumentImpl {
    private String mPackagePath;
    private OdfDocument mOdfDocument;
    
    /**
     * Creates the DOM representation of an XML file of an Odf document.
     * 
     * @param odfDocument   the document the XML files belongs to
     * @param packagePath   the internal package path to the XML file
     */  
    OdfFileDom(OdfDocument odfDocument, String packagePath){
        mOdfDocument = odfDocument;        
        mPackagePath = packagePath;
    }
       
    public OdfDocument getOdfDocument(){
        return mOdfDocument;
    }    
    
    public String getPackagePath(){
        return mPackagePath;
    }     
    
    @Override
    public OdfElement createElementNS(String nsuri, String qname) throws DOMException {
        return createElementNS(OdfName.get(nsuri, qname));
    }    
    
    public OdfElement createElementNS(OdfName name) throws DOMException {
        return OdfElementFactory.createOdfElement(this, name);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends OdfElement> T createOdfElement(Class<T> clazz) {
        try {
            Field fname = clazz.getField("ELEMENT_NAME");
            OdfName name = (OdfName) fname.get(null);
            return (T) createElementNS(name);
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            return null;
        }
    }    
    
    /**
     * @return the style:automatic-styles element of this dom. May return null
     *         if there is not yet such element in this dom.
     * 
     * @see getOrCreateAutomaticStyles()
     * 
     */
    public OdfAutomaticStyles getAutomaticStyles()
    {
        return OdfElement.findFirstChildNode(OdfAutomaticStyles.class, getFirstChild() );
    }
    
    /**
     * @return the style:automatic-styles element of this dom. If it does not
     *         yet exists, a new one is inserted into the dom and returned.
     * 
     */
    public OdfAutomaticStyles getOrCreateAutomaticStyles()
    {
        OdfAutomaticStyles automaticStyles = getAutomaticStyles();
        if( automaticStyles == null )
        {
            automaticStyles = createOdfElement(OdfAutomaticStyles.class);
            
            Node parent = getFirstChild();
            
            // try to insert before body or before master-styles element
            OdfElement sibling = OdfElement.findFirstChildNode(OdfBody.class, parent);
            if( sibling == null )
                sibling = OdfElement.findFirstChildNode(OdfMasterStyles.class, parent);
            
            if( sibling == null )                
                parent.appendChild(automaticStyles);
            else
                parent.insertBefore(automaticStyles, sibling);
        }
        return automaticStyles;
    }
   
    @Override
    public String toString(){
        return ((OdfElement) this.getDocumentElement()).toString();
    }    
}
