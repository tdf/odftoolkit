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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.element.draw.DrawLayerSetElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHandoutMasterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfName;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Convenient functionality for the parent ODF OpenDocument element
 *
 */
abstract public class OdfOfficeMasterStyles extends OdfStylesBase implements Iterable<StyleMasterPageElement> {

	public static final OdfName ELEMENT_NAME = OdfName.newName(OdfDocumentNamespace.OFFICE, "master-styles");

    private static final long serialVersionUID = 6598785919980862801L;
    private DrawLayerSetElement mLayerSet;
    private StyleHandoutMasterElement mHandoutMaster;
    private HashMap< String, StyleMasterPageElement> mMasterPages;

    public OdfOfficeMasterStyles(OdfFileDom ownerDoc) {
        super(ownerDoc, ELEMENT_NAME);
    }

    @Override
	public OdfName getOdfName() {
		// TODO Auto-generated method stub
		return null;
	}

    public StyleHandoutMasterElement getHandoutMaster() {
        return mHandoutMaster;
    }

    public DrawLayerSetElement getLayerSet() {
        return mLayerSet;
    }

    public StyleMasterPageElement getMasterPage(String name) {
        if (mMasterPages != null) {
            return mMasterPages.get(name);
        } else {
            return null;
        }
    }

	protected <T extends OdfElement> T getStylesElement(OdfFileDom dom, Class<T> clazz) throws Exception {
		OdfElement stylesRoot = dom.getRootElement();

		OdfOfficeMasterStyles contentBody = OdfElement.findFirstChildNode(OdfOfficeMasterStyles.class, stylesRoot);
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

    /** @return the master page with the given style name or creates one*/
    public StyleMasterPageElement getOrCreateMasterPage(String name) {
        StyleMasterPageElement masterPage = getMasterPage(name);
        if (masterPage == null) {
            masterPage = new StyleMasterPageElement((OdfFileDom) this.getOwnerDocument());
            masterPage.setStyleNameAttribute(name);
            this.appendChild(masterPage);
        }
        return masterPage;
    }

    /**  @return all <style:master-page> element children */
    public Map<String, StyleMasterPageElement> getMasterPages() {
        if (mMasterPages != null) {
            return mMasterPages;
        } else {
            return null;
        }
    }

    /**
     * override this method to get notified about element insertion
     */
    @Override
    public void onOdfNodeInserted(OdfElement node, Node refNode) {
        if (node instanceof DrawLayerSetElement) {
            mLayerSet = (DrawLayerSetElement) node;
        } else if (node instanceof StyleHandoutMasterElement) {
            mHandoutMaster = (StyleHandoutMasterElement) node;
        } else if (node instanceof StyleMasterPageElement) {
            StyleMasterPageElement masterPage = (StyleMasterPageElement) node;

            if (mMasterPages == null) {
                mMasterPages = new HashMap< String, StyleMasterPageElement>();
            }
            mMasterPages.put(masterPage.getStyleNameAttribute(), masterPage);
        }
    }

    /**
     * override this method to get notified about element insertion
     */
    @Override
    public void onOdfNodeRemoved(OdfElement node) {
        if (node instanceof DrawLayerSetElement) {
            if (mLayerSet == (DrawLayerSetElement) node) {
                mLayerSet = null;
            }
        } else if (node instanceof StyleHandoutMasterElement) {
            if (mHandoutMaster == (StyleHandoutMasterElement) node) {
                mHandoutMaster = null;
            }
        } else if (node instanceof StyleMasterPageElement) {
            if (mMasterPages != null) {
                StyleMasterPageElement masterPage = (StyleMasterPageElement) node;
                mMasterPages.remove(masterPage.getStyleNameAttribute());
            }
        }
    }

    /**  @return Iterator over the <style:master-page> element children */
    public Iterator<StyleMasterPageElement> iterator() {
        Iterator<StyleMasterPageElement> iter = null;
        Map<String, StyleMasterPageElement> masterPages = this.getMasterPages();
        if(masterPages != null){
            iter = masterPages.values().iterator();
        }
        if(iter == null){
            iter =  Collections.emptyIterator();
        }
        return iter;
    }
}
