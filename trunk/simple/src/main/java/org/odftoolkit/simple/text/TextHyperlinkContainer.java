/* 
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.text;

import java.net.URI;
import java.util.Iterator;

/**
 * TextHyperlinkContainer is a container which maintains text hyperlinks.
 * Hyperlinks can be added and removed in this container.
 * 
 * @see TextHyperlink
 * 
 * @since 0.6.5
 */

public interface TextHyperlinkContainer {

	/**
	 * Add a hypertext reference to this hyperlink container.
	 * 
	 * @param linkto
	 *            the hyperlink
	 * @return an instance of TextHyperlink
	 */
	public TextHyperlink applyHyperlink(URI linkto);

	/**
	 * Remove all the hyperlinks in this container element.
	 */
	public void removeHyperlinks();

	/**
	 * Append a hyperlink at the end of the container
	 * 
	 * @param text
	 *            - the text content
	 * @param linkto
	 *            - the URI of this hyperlink
	 * @return an instance of TextHyperlink
	 */
	public TextHyperlink appendHyperlink(String text, URI linkto);

	/**
	 * Get the iterator of hyperlinks within this container element
	 * 
	 * @return the iterator of hyperlinks within this container element
	 */
	public Iterator<TextHyperlink> getHyperlinkIterator();
}
