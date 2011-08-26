/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
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

package org.odftoolkit.odfdom.codegen;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author cl93746
 */
public class Config extends DefaultHandler
{
    public class ElementConfig
    {
        public String Name;
        public String ReName;
        public String Base;
        public String StyleFamily;
    }
    
    public class AttributeConfig
    {
        public String Name;
        public String Element;
        public String TypeName;            
        public String Rename;
        public String DefaultValue;
    }
    
    public class DataTypeConfig
    {
        public String Name;
        public String ValueType;            
        public String ConversionType;
    }
    
    private HashMap< String, ElementConfig > ElementConfigs;
    private HashMap< String, DataTypeConfig > DataTypes;
    private Vector< AttributeConfig > AttributeConfigs;
    
    private Config()
    {
        ElementConfigs = new HashMap<String,ElementConfig>();
        DataTypes = new HashMap< String, DataTypeConfig >();
        AttributeConfigs = new Vector< AttributeConfig >();
    }
    
    static public Config parse( String url ) throws IOException, SAXException
    {
        Config xThis = new Config();
        InputStream aContent = new FileInputStream( url );
        InputSource aSource = new InputSource( aContent  );
        XMLReader xr = XMLReaderFactory.createXMLReader();
        xr.setContentHandler(xThis);
        xr.setErrorHandler(xThis);
        xr.parse( aSource );
        return xThis;
    }
    
    Iterator<ElementConfig> getElementConfigurations()
    {
        return ElementConfigs.values().iterator();
    }

    public ElementConfig getConfigForElement( String qName )
    {
        return ElementConfigs.get(qName);
    }
    
    Iterator<AttributeConfig> getAttributeConfigurations()
    {
        return AttributeConfigs.iterator();
    }
    

    public DataTypeConfig getDataTypeConfiguration( String name )
    {
        return DataTypes.get(name);
    }
    
    private static String forceString( String str )
    {
        if( str != null )
            return str;
        else
            return new String();
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        if( localName.equals("element") )
        {
            ElementConfig config = new ElementConfig();
            config.Name = forceString( attributes.getValue("name") );
            config.ReName = forceString( attributes.getValue("rename") );
            config.Base = forceString( attributes.getValue("base") );
            config.StyleFamily = forceString( attributes.getValue("family") );            
            ElementConfigs.put( config.Name, config );
        }
        else if( localName.equals("data") || localName.equals("data-type") )
        {
            DataTypeConfig config = new DataTypeConfig();
            config.Name = forceString( attributes.getValue("name") );
            config.ValueType = forceString( attributes.getValue("value-type") );
            config.ConversionType = forceString( attributes.getValue("conversion-type") );
            DataTypes.put( config.Name, config );
        }
        else if( localName.equals("attribute") )
        {
            AttributeConfig config = new AttributeConfig();
            config.Name = forceString( attributes.getValue("name") );
            config.TypeName = forceString( attributes.getValue("type-name") );
            config.Element = forceString( attributes.getValue("element") );
            config.Rename = forceString(  attributes.getValue("rename") );
            config.DefaultValue = forceString(  attributes.getValue("defaultValue") );
            AttributeConfigs.add( config);
        }
    }           
}
