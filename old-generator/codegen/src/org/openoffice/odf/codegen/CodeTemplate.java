/*
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
 */

package org.openoffice.odf.codegen;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author cl93746
 */
public class CodeTemplate extends DefaultHandler
{
    public class TemplateNode
    {
        public final static String TEMPLATE_ELEMENT = "template";
        public final static String FOREACH_ELEMENT = "foreach";
        public final static String FILE_ELEMENT = "file";
        public final static String CODE_ELEMENT = "code";
        public final static String IF_ELEMENT = "if";
        public final static String ELSE_ELEMENT = "else";
        public final static String SET_ELEMENT = "set";
        public final static String DEFINE_ELEMENT = "define";
        public final static String REF_ELEMENT = "ref";
        public final static String SELECT_ELEMENT = "select";
        public final static String TYPE_ATTRIBUTE = "type";
        public final static String PATH_ATTRIBUTE = "path";
        public final static String NAME_ATTRIBUTE = "name";
        public final static String VALUE_ATTRIBUTE = "value";
        public final static String TEST_ATTRIBUTE = "test";
        public final static String EXTENSION_ATTRIBUTE = "extensions";
        
        public HashMap< String, String > Attributes;
        public String LocalName;
        public String Characters;
        public Vector< TemplateNode > Children;
        
        public TemplateNode( String localName, Attributes attr )
        {
            LocalName = localName;
            Characters = new String();
            
            Children = new Vector< TemplateNode >();
            Attributes = new HashMap< String, String >();
            
            for( int i = 0; i < attr.getLength(); i++ )
            {
                Attributes.put( attr.getLocalName(i), attr.getValue(i) );
            }
        }

        public String getLocalName() {
            return LocalName;
        }
        
        public String getAttribute( String name )
        {
            String ret = Attributes.get(name);
            if( ret == null )
                ret = new String();
            return ret;
        }
        
        public void addChild( TemplateNode node )
        {
            Children.add(node);
        }
        
        public Iterator< TemplateNode > getChildren()
        {
            return Children.iterator();
        }

        Iterator<Entry<String, String>> getAttributes()
        {
            return Attributes.entrySet().iterator();
        }
    }
               
    private TemplateNode rootNode;
    private Stack< TemplateNode > ElementStack;
    private HashMap< String, TemplateNode > Defines;
    private Locator Locator;

    @Override
    public void setDocumentLocator(Locator locator) 
    {
        Locator = locator;
    }

    public CodeTemplate()
    {
        ElementStack = new Stack< TemplateNode >();
        Defines = new HashMap< String, TemplateNode >();
    }

    public TemplateNode getRootNode() {
        return rootNode;
    }
    
    public TemplateNode getDefine( String name )
    {
        return Defines.get(name);
    }
    
    static public CodeTemplate parse( String path ) throws IOException, SAXException
    {
        CodeTemplate xThis = new CodeTemplate();
        try
        {
            InputStream aContent = new FileInputStream( path );
            InputSource aSource = new InputSource( aContent  );
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(xThis);
            xr.setErrorHandler(xThis);
            xr.parse( aSource );
            return xThis;
        }
        catch( SAXException ex)
        {
            if( xThis.Locator == null )
                throw ex;
            else
                throw new SAXException( new String( ex.getMessage() + " at [" + xThis.Locator.getLineNumber() + ":" + xThis.Locator.getColumnNumber() + "]" ) );
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if( !ElementStack.isEmpty() )
        {
            TemplateNode node = ElementStack.lastElement();
            node.Characters += new String( ch, start, length );
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if( !ElementStack.empty() )
            ElementStack.pop();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        TemplateNode node = new TemplateNode(localName, attributes);

        TemplateNode parent = null;
        if( !ElementStack.empty() )
            parent = ElementStack.lastElement();
               
        if( node.getLocalName().equals( TemplateNode.DEFINE_ELEMENT ) )
        {
            String name = node.getAttribute("name"); 
            if( name.length() == 0 )
                System.err.println("warning: <define> without attribute 'name'!");
            Defines.put( name, node );
        }
        else
        {
            if( parent != null )
                parent.addChild(node);
        
            if( rootNode == null )
                rootNode = node;
        }

        ElementStack.push(node);
    }    
}
