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

package org.odftoolkit.simple.draw;

import java.util.Iterator;
import java.util.List;

import org.odftoolkit.simple.PresentationDocument;

/**
 * TextboxContainer is a container which maintains TextBox(s) as element(s).
 * TextBox(s) can be added, removed and iterated in this container.
 * 
 * @since 0.5
 */
public interface TextboxContainer extends FrameContainer {

	/**
	 * Add a text box
	 * 
	 * @return the object of text box
	 */
	public Textbox addTextbox();

	/**
	 * Add a text box with a specific size at a specific position
	 * 
	 * @param position
	 *            - the rectangle (position and size) of this text box
	 * @return the object of text box
	 */
	public Textbox addTextbox(FrameRectangle position);

	/**
	 * Remove the text box
	 * 
	 * @param box
	 *            - the instance of text box
	 * @return true if success, false if fails
	 */
	public boolean removeTextbox(Textbox box);

	/**
	 * Return an Iterator of the text objects in this container.
	 * 
	 * @return an Iterator of the text objects in this container
	 */
	public Iterator<Textbox> getTextboxIterator();

	/**
	 * Return a text box whose name is a given value.
	 * 
	 * @param name
	 *            - the name of the text box
	 * @return a text box whose name is a given value
	 */
	public Textbox getTextboxByName(String name);

	/**
	 * This method is only useful in presentation slide.
	 * <p>
	 * This method will return a list of text boxs by the usage in presentation
	 * slides.
	 * 
	 * @param usage
	 *            - the usage description
	 * @return a list of text box Null will be returned if the owner document is
	 *         not a presentation
	 */
	public List<Textbox> getTextboxByUsage(PresentationDocument.PresentationClass usage);

}
