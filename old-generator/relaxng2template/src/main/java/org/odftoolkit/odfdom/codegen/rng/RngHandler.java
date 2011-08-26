/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
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

package org.odftoolkit.odfdom.codegen.rng;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.odftoolkit.odfdom.codegen.Config;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author cl93746
 */
public class RngHandler extends DefaultHandler
{              
    private HashMap< String, Vector< RngDefine > > Defines;
    private HashMap< String, String > Namespaces;
    private Stack< RngNode > ElementStack;
    private List< RngElement > Elements;
    private HashSet< String > DataTypes;
    private Config Config;
    private String URL;
    
    public class RngFilter extends XMLFilterImpl
    {
        private HashMap< String, String > Namespaces;
        public RngFilter( XMLReader parent, HashMap< String, String > namespaces )
        {
            super( parent );
            Namespaces = namespaces;
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException
        {
            if( prefix.length() != 0 )
                Namespaces.put( prefix, uri );
            super.startPrefixMapping(prefix, uri);
        }                
    };
    
    public RngHandler( Config config, String url )
    {
        Defines = new HashMap< String,  Vector< RngDefine > >();
        ElementStack = new Stack< RngNode >();
        Elements = new Vector< RngElement >();
        DataTypes = new HashSet< String >();
        Namespaces = new HashMap< String, String >();
        Config = config;
        URL = url;
    }
    
    static public RngHandler parse( String url, Config config ) throws IOException, SAXException
    {
        RngHandler xThis = new RngHandler(config, url);
        InputStream aContent = new FileInputStream( url );
        InputSource aSource = new InputSource( aContent  );
        XMLReader xr = xThis.createReader();
        xr.setContentHandler(xThis);
        xr.setErrorHandler(xThis);
        xr.parse( aSource );
        return xThis;
    }
    
    private XMLReader createReader() throws SAXException
    {
        return new RngFilter( XMLReaderFactory.createXMLReader(), Namespaces );
    }

    public Config getConfiguration()
    {
        return Config;
    }
    
    public Iterator<String> getDataTypes()
    {
        return DataTypes.iterator();
    }
    
    public Iterator< RngDefine > getDefines( String name )
    {
        Vector< RngDefine > defines = Defines.get(name);
        if( defines == null )
            defines = new Vector< RngDefine >();
        return defines.iterator();
    }

    public Iterator< RngElement > getElements()
    {
        return Elements.iterator();
    }
            
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        RngNode node;

        // OK, we're doing Namespace processing, since sax implementation does not tell us about it!.
	int length = attributes.getLength();
	for (int i = 0; i < length; i++)
        {
	    String attQName = attributes.getQName(i);

	    if (!attQName.startsWith("xmlns:"))
		continue;
            
            Namespaces.put( attQName.substring(6), attributes.getValue(i));
	}
        
        if( localName.equals( RngDefine.LOCAL_NAME ) )
        {
            RngDefine define = new RngDefine( attributes );
            node = define;

            Vector< RngDefine > defines = Defines.get(define.getName());
            if( defines == null )
            {
                defines = new Vector< RngDefine >();
                Defines.put( define.getName(), defines);
            }

            defines.add( define );            
        }
        else if( localName.equals( RngAttribute.LOCAL_NAME) )
        {
            node = new RngAttribute( this, attributes );
        }
        else if( localName.equals( RngElement.LOCAL_NAME ) )
        {
            RngElement element = new RngElement( this, attributes );
            node = element;
            Elements.add( element );
        }
        else if( localName.equals(RngReference.LOCAL_NAME) )
        {
            node = new RngReference( attributes );
        }
        else if( localName.equals( RngValue.LOCAL_NAME ) )
        {
            node = new RngValue();
        }        
        else if( localName.equals( RngName.LOCAL_NAME ) )
        {
            node = new RngName();
        }
        else if( localName.equals( RngData.LOCAL_NAME ) )
        {
            node = new RngData( attributes );
            DataTypes.add( ((RngData)node).getType() );
        }
        else
        {
            node = new RngNode(localName);
        }
        
        if( !ElementStack.isEmpty() )
            ElementStack.lastElement().addChild(node);
        
        ElementStack.push(node);
        
        if( localName.equals( "include" ) )
        {
            try
            {
                String href = attributes.getValue("href");
                String path = new String();
                File parent = new File( URL ).getParentFile();
                if( parent != null )
                    path = parent.getAbsolutePath() + File.separator;

                path += href;
                System.out.println("include path = " + path );
                InputStream aContent = new FileInputStream( path );
                InputSource aSource = new InputSource( aContent  );
                XMLReader xr = createReader();
                aContent = new FileInputStream(path);
                xr.setContentHandler(this);
                xr.setErrorHandler(this);
                xr.parse( aSource );
            }
            catch( IOException ex )
            {
                throw new SAXException( ex.toString() );
            }
            node = new RngNode(localName);            
        }        
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        if( !ElementStack.isEmpty() )
        {
            RngNode node = ElementStack.lastElement();
            if( node instanceof RngCharacterNode )
            {
                RngCharacterNode value = (RngCharacterNode)node;
                if( value != null )
                {
                    if( value.Value == null )
                    {
                        value.Value = new String( ch, start, length );
                    }
                    else
                    {
                        value.Value += new String( ch, start, length );
                    }
                }            
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        if( ElementStack.isEmpty() || !ElementStack.lastElement().getLocalName().equals(localName) )
            throw new SAXException( new String( "endElement() mismatches with startElement()!" ) );
        
        ElementStack.pop();
    }

    public HashMap<String, String> getNamespaces()
    {
        return Namespaces;
    }
}
