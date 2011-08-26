package org.odftoolkit.doctag;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;



public class AttributeNameTaglet implements Taglet {
	
	 private static final String NAME = "attributeName";

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
	       AttributeNameTaglet tag = new AttributeNameTaglet();
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
	        //return "<u>" + tag.text() + "</u>";
		int pos = tag.text().lastIndexOf(":");
		String link = "attribute-"+tag.text().substring(0, pos)+"_"+tag.text().substring(pos+1);
//	    	return "<a href=\"http://odftoolkit.org/downloads/odfdom/OpenDocument-v1.2-draft/OpenDocument-v1.2-cd02.xhtml#"+ link +"\">"+ tag.text()+ "</a>";
		return "<a href=\"../../../../../../../../../src/main/javadoc/OpenDocument-v1.2-draft/OpenDocument-v1.2-draft.xhtml#"+ link +"\">"+ tag.text()+ "</a>";
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
