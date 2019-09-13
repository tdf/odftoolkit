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
//ToDo: Move into tooling package?
package org.odftoolkit.odfdom.pkg;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.OdfStylesDom;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

/**
 * Overwriting the DOM methods for element access, whenever a new ODF element was added triggering
 * <code>onOdfNodeInserted</code> and when removed a <code>onOdfNodeRemoved.</code>.
 * Yet not overriding all DOM access methods, esp. not the access on key attributes, eg. @style-name.
 */
abstract public class OdfContainerElementBase extends OdfElement {

	private static final long serialVersionUID = 6944696143015713668L;
	// moved to this class as only used for style handling
	protected OdfPackageDocument mPackageDocument;

	/** Creates a new instance of OdfElement */
	public OdfContainerElementBase(OdfFileDom ownerDocument,
			String namespaceURI,
			String qualifiedName) throws DOMException {
		super(ownerDocument, namespaceURI, qualifiedName);
		if (ownerDocument instanceof OdfContentDom) {
			ownerDocument = (OdfContentDom) ownerDocument;
		} else if (ownerDocument instanceof OdfStylesDom) {
			ownerDocument = (OdfStylesDom) ownerDocument;
		}
		mPackageDocument = ownerDocument.getDocument();
	}

	/** Creates a new instance of OdfElement */
	public OdfContainerElementBase(OdfFileDom ownerDocument,
			OdfName aName) throws DOMException {
		super(ownerDocument, aName.getUri(), aName.getQName());
	}

	/** override this method to get notified about element insertion
	 */
	protected void onOdfNodeInserted(OdfElement node, Node refChild) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/** override this method to get notified about element insertion
	 */
	protected void onOdfNodeRemoved(OdfElement node) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		Node ret = super.insertBefore(newChild, refChild);
		if (newChild instanceof OdfElement) {
			onOdfNodeInserted((OdfElement) newChild, refChild);
		}
		return ret;
	}

	@Override
	public Node removeChild(Node oldChild) throws DOMException {
		Node ret = super.removeChild(oldChild);
		if (oldChild instanceof OdfElement) {
			onOdfNodeRemoved((OdfElement) oldChild);
		}
		return ret;
	}

	@Override
	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		Node ret = super.replaceChild(newChild, oldChild);
		// first REMOVE afterwards ADD, otherwise the removal will take away the insered from the OdfStyleBase properties map
		if (oldChild instanceof OdfElement) {
			onOdfNodeRemoved((OdfElement) oldChild);
		}

		if (newChild instanceof OdfElement) {
			onOdfNodeInserted((OdfElement) newChild, oldChild);
		}
		return ret;
	}
}
