/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 IBM, Inc. All rights reserved.
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
 */
package org.odftoolkit.doctag;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;



public class OdfLinkTaglet implements Taglet {
	
	 private static final String NAME = "odfLink";

	    /**
	     * Return the name of this custom tag.
	     */
	    public String getName() {
	        return NAME;
	    }
	    
	    /**
	     * @return true since this tag can be used in a field
	     *         doc comment
	     */
	    public boolean inField() {
	        return true;
	    }

	    /**
	     * @return true since this tag can be used in a constructor
	     *         doc comment
	     */
	    public boolean inConstructor() {
	        return true;
	    }
	    
	    /**
	     * @return true since this tag can be used in a method
	     *         doc comment
	     */
	    public boolean inMethod() {
	        return true;
	    }
	    
	    /**
	     * @return true since this tag can be used in an overview
	     *         doc comment
	     */
	    public boolean inOverview() {
	        return true;
	    }

	    /**
	     * @return true since this tag can be used in a package
	     *         doc comment
	     */
	    public boolean inPackage() {
	        return true;
	    }

	    /**
	     * @return true since this 
	     */
	    public boolean inType() {
	        return true;
	    }
	    
	    /**
	     * Will return true since this is an inline tag.
	     * @return true since this is an inline tag.
	     */
	    
	    public boolean isInlineTag() {
	        return true;
	    }
	    
	    /**
	     * Register this Taglet.
	     * @param tagletMap  the map to register this tag to.
	     */
	    public static void register(Map tagletMap) {
	       OdfLinkTaglet tag = new OdfLinkTaglet();
	       Taglet t = (Taglet) tagletMap.get(tag.getName());
	       if (t != null) {
	           tagletMap.remove(tag.getName());
	       }
	       tagletMap.put(tag.getName(), tag);
	    }

	    /**
	     * Given the <code>Tag</code> representation of this custom
	     * tag, return its string representation.
	     * @param tag he <code>Tag</code> representation of this custom tag.
	     */
	    public String toString(Tag tag) {
		    int pos = tag.text().lastIndexOf(":");
			String link = "element-" + tag.text().substring(0, pos) + "_"
					+ tag.text().substring(pos + 1);
			String address = System.getenv("specificationurl");
			return "<a href=\"" + address + "#" + link + "\">" + tag.text()
					+ "</a>";
	    }
	    
	    /**
	     * This method should not be called since arrays of inline tags do not
	     * exist.  Method  should be used to convert this
	     * inline tag to a string.
	     * @param tags the array of <code>Tag</code>s representing of this custom tag.
	     */
	    public String toString(Tag[] tags) {
	        return null;
	    }

}
