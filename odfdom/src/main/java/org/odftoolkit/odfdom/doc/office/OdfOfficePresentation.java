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

package org.odftoolkit.odfdom.doc.office;

import java.util.ArrayList;
import java.util.Iterator;

import org.odftoolkit.odfdom.OdfElement;
import org.odftoolkit.odfdom.OdfFileDom;
import org.odftoolkit.odfdom.doc.draw.OdfDrawPage;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.w3c.dom.Node;

/**
 * Convenient functionalty for the parent ODF OpenDocument element
 *
 */
public class OdfOfficePresentation extends OfficePresentationElement
{


   
   private ArrayList<OdfDrawPage> mPages;

   public OdfOfficePresentation(OdfFileDom ownerDoc) {
       super(ownerDoc);
   }

   public OdfDrawPage getPageAt(int index) {
       if ((mPages != null) || (mPages.size() <= index)) {
           return mPages.get(index);
       } else {
           return null;
       }
   }

   public int getPageCount() {
       if (mPages != null) {
           return mPages.size();
       } else {
           return 0;
       }
   }

   public OdfDrawPage getPage(String name) {
       if (mPages != null) {
           Iterator<OdfDrawPage> iter = mPages.iterator();
           while (iter.hasNext()) {
               OdfDrawPage page = iter.next();
               if (page.getDrawNameAttribute().equals(name)) {
                   return page;
               }
           }
       }
       return null;
   }

   public Iterator<OdfDrawPage> getPages() {
       if (mPages != null) {
           return mPages.iterator();
       } else {
           return new ArrayList<OdfDrawPage>().iterator();
       }
   }

   /** override this method to get notified about element insertion
    */
   protected void onOdfNodeInserted(OdfElement node, Node refNode) {
       if (node instanceof OdfDrawPage) {
           OdfDrawPage page = (OdfDrawPage) node;

           if (mPages == null) {
               mPages = new ArrayList<OdfDrawPage>();
           } else if (refNode != null) {
               int index = -1;
               OdfDrawPage refPage = findPreviousChildNode(OdfDrawPage.class, node);
               if (refPage != null) {
                   index = mPages.indexOf(refPage);
               }
               mPages.add(index + 1, page);
               return;
           }
           mPages.add(page);
       }
   }

   /** override this method to get notified about element insertion
    */
   protected void onOdfNodeRemoved(OdfElement node) {
       if (node instanceof OdfDrawPage) {
           if (mPages != null) {
               OdfDrawPage page = (OdfDrawPage) node;
               mPages.remove(page);
           }
       }
   }

}
