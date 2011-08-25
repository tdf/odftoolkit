/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2011 IBM. All rights reserved.
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
package org.odftoolkit.simple.text;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.DOMException;

/**
 * AbstractParagraphContainer is an abstract class to present paragraph
 * container. It contains the default implementation to add/get/remove
 * paragraph. Each subclass needs to implement getParagraphContainerElement().
 * 
 * @since 0.5
 */
public abstract class AbstractParagraphContainer implements ParagraphContainer {

	abstract public OdfElement getParagraphContainerElement();

	/**
	 * Add paragraph at the end of the container with specified text content.
	 * 
	 * @param textContent
	 *            - the text content
	 * @return an instance of paragraph
	 */
	public Paragraph addParagraph(String textContent) {
		Paragraph para = Paragraph.newParagraph(this);
		para.setTextContent(textContent);
		return para;
	}

	/**
	 * Remove paragraph from the container
	 * 
	 * @param para
	 *            - the instance of paragraph
	 * @return true if the paragraph is removed successfully, false if errors
	 *         happen.
	 */
	public boolean removeParagraph(Paragraph para) {
		OdfElement containerElement = getParagraphContainerElement();
		try {
			containerElement.removeChild(para.getOdfElement());
		} catch (DOMException exception) {
			Logger.getLogger(AbstractParagraphContainer.class.getName()).log(Level.WARNING, exception.getMessage());
			return false;
		}
		return true;
	}
}
