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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

import org.odftoolkit.odfdom.codegen.CodeTemplate.TemplateNode;
import org.odftoolkit.odfdom.codegen.Config.ElementConfig;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Code generator class.
 */
public class CodeGen implements IFunctionSupplier
{
    private Context context;
    private Config config;
    private CodeTemplate template;
    private String targetPath;
    private Schema schema;
	private String head;

    public static void main(String[] args)
    {       
        if( args.length != 4 )
        {
            System.out.println("usage: <schema.rng> <config.xml> <template.xml> <destination folder>");
        }
        else
        {
            String schemaPath = args[0];
            String configPath = args[1];
            String templatePath = args[2];
            String destPath = args[3];

            CodeGen xThis = new CodeGen( destPath );

            if( xThis.parseConfig(configPath) )
            {
                if( xThis.parseSchema(schemaPath) )
                {
                    if( xThis.parseTemplate(templatePath) )
                    {
                        if( xThis.executeTemplate(xThis.template) )
                            System.exit(0);
                    }
                }
            }
        }
        System.exit(1);
    }
    
    public CodeGen( String targetPath )
    {
        this.targetPath = targetPath;
        context = new Context(this);
    }

    public boolean parseConfig( String path )
    {
        try
        {
            config = Config.parse(path);            
            return true;
        }
        catch( Exception ex )
        {
            System.err.println("error: could not load " + path + ", " + ex.toString() );
        }
        return false;
    }
    
    public boolean parseSchema( String path )
    {
        schema = new Schema(config);
        return schema.parseSchema(path);
    }
     
    public boolean parseTemplate( String path )
    {
        try
        {
            template = CodeTemplate.parse(path);            
            return true;
        }
        catch( Exception ex )
        {
            System.err.println("error: could not load configuration, " + ex.toString() );
        }
        return false;
    }
    
    public boolean executeTemplate( CodeTemplate template )
    {
        try
        {
            return executeTemplateNode( template.getRootNode() );
        }
        catch( IOException ex )
        {
            System.err.println("error: " + ex.getMessage() );
            return false;
        }
    }
    
    private boolean executeTemplateNode( TemplateNode node ) throws IOException
    {
//        System.out.println("executeTemplateNode(" + node.getLocalName() + ")");
            
        if( node.getLocalName().equals(TemplateNode.TEMPLATE_ELEMENT ) )
        {
            return executeTemplateChildNodes( node );            
        }
        if( node.getLocalName().equals(TemplateNode.FILE_ELEMENT ) )
        {
            if( !executeFileNode( node ) )
                return false;
        }
        else if( node.getLocalName().equals(TemplateNode.CODE_ELEMENT ) )
        {
            if( !executeCodeNode(node) )
                return false;
        }
        else if( node.getLocalName().equals(TemplateNode.FOREACH_ELEMENT))
        {
            if( !executeForeachNode(node) )
                return false;
        }
        else if( node.getLocalName().equals(TemplateNode.SELECT_ELEMENT) )
        {
            if( !executeSelectNode(node) )
                return false;
        }
        else if( node.getLocalName().equals(TemplateNode.IF_ELEMENT) )
        {
            String test = node.getAttribute(TemplateNode.TEST_ATTRIBUTE);
            if( test == null )
                throw new IOException( new String( "if element needs an attribute 'test'"));
            
            if( ExpressionParser.evaluateBoolean(test, context ) )
            {
                if( !executeTemplateChildNodes(node) )
                    return false;                
            }
            else
            {
                if( !executeTemplateElseNode(node) )
                    return false;
            }
        }
/*        
        else if( node.getLocalName().equals(TemplateNode.IFNEQ_ELEMENT) )
        {
            String p1 = decodeTemplateString( node.getAttribute(("p1")) );
            String p2 = decodeTemplateString( node.getAttribute(("p2")) );

            if( !p1.equals( p2 ) )
            {
                if( !executeTemplateChildNodes(node) )
                    return false;                
            }
            else
            {
                if( !executeTemplateElseNode(node) )
                    return false;                
            }
        }
 */ 
        else if( node.getLocalName().equals(TemplateNode.ELSE_ELEMENT) )
        {
            // skip else
        }
        else if( node.getLocalName().equals(TemplateNode.SET_ELEMENT) )
        {
            Iterator< Entry< String, String > > iter = node.getAttributes();
            while( iter.hasNext() )
            {
                Entry< String, String > attr = iter.next();
                context.setVariable( attr.getKey(), decodeTemplateString( attr.getValue() ) );
            }
        }
        else if( node.getLocalName().equals(TemplateNode.REF_ELEMENT) )
        {
            String name = node.getAttribute(TemplateNode.NAME_ATTRIBUTE);
            if( (name == null) || (name.length() == 0) )
            {
                System.err.println("error: <ref> must have attribute 'name'!");
                return false;
            }
            TemplateNode define = template.getDefine(name);
            
            if( define == null )
            {
                System.err.println("error: <ref> uses unknown define '" + name + "'!");
                return false;
            }
            else
            {
                return executeTemplateChildNodes( define );
            }                               
        }
        //add for selecting chidren elements
        else if( node.getLocalName().equals(TemplateNode.CHILDREN_ELEMENT))
        {
            if( !executeChildrenNode(node) )
                return false;
        }
        else if( node.getLocalName().equals(TemplateNode.SUBATTRIBUTE_ELEMENT))
        {
            if( !executeSubAttributeNode(node) )
                return false;
        }
        else if( node.getLocalName().equals(TemplateNode.GROUP_ELEMENT))
        {
            if( !executeAttributeGroupNode(node) )
                return false;
        }
        else
        {
            System.err.println("error: template uses unknown element <" + node.getLocalName() + ">!");
            return false;
        }
        return true;
    }
    
    private boolean executeTemplateElseNode( TemplateNode parent ) throws IOException
    {
        Iterator< TemplateNode > iter = parent.getChildren();
        while( iter.hasNext() )
        {
            TemplateNode node = iter.next();
            if( node.getLocalName().equals(TemplateNode.ELSE_ELEMENT) )
                return executeTemplateChildNodes(node);
        }
        return true;
    }    
    
    private boolean executeTemplateChildNodes( TemplateNode parent ) throws IOException
    {
        Iterator< TemplateNode > iter = parent.getChildren();
        while( iter.hasNext() )
        {
            if( !executeTemplateNode( iter.next() ) )
                return false;
        }
        return true;
    }

    private boolean executeChildrenNode( TemplateNode node ) throws IOException
    {
    	
        String type = node.getAttribute(TemplateNode.TYPE_ATTRIBUTE);

        String sep = node.getAttribute("seperator");
        if( ((sep != null) && (sep.length() == 0)) || (context.getCurrentFile() == null) )
            sep = null;
        boolean first = true;

        if( type.equals("element") )
        {
            Element element = context.getCurrentElement();
            
            if( element == null )
            {
                System.err.println("error: foreach attribute needs a current element!" );
                return false;
            }
               
            if (element.getSubElements().size() > 0) {

				Iterator<Element> iter = element.getSubElements().iterator();
                
				while (iter.hasNext()) {
					 
					first = printSeperator( sep, first );
					
	                if( !selectElement( node, iter.next(), false ) )                                                    
	                    return false;                
			    }
            }
        }
        else
        {
            System.err.println("error: unknown foreach type " + type );
            return false;
        }
        return true;

    }
    
    private boolean executeSubAttributeNode( TemplateNode node ) throws IOException
    {
    	
    	    
            Element element = context.getCurrentElement();
                        
            if( element == null )
            {
                System.err.println("error: foreach attribute needs a current element!" );
                return false;
            }
                        
            if (element.getSubAttributes().size() > 0) {

				Iterator<Vector<Attribute>> iter = element.getSubAttributes().iterator();
                Iterator <String>  diffIter = element.getDifferentName().iterator();
				while (iter.hasNext()) {
					Vector<Attribute> subAtt = (Vector<Attribute>)iter.next();
					if( !selectSubAttribute( node, element,subAtt , diffIter.next()))                                                  
	                    return false;                
			    }
            }
            
            return true;

    }
    
    private boolean executeAttributeGroupNode( TemplateNode node ) throws IOException
    {
    	
            Element element = context.getCurrentElement();
            
            Vector<Attribute> group = context.getCurrentAttributeGroup();
            
            if( element == null )
            {
                System.err.println("error: foreach attribute needs a current element!" );
                return false;
            }
               
            if (group.size() > 0) {

				Iterator<Attribute> iter = group.iterator();
                
				while (iter.hasNext()) {
					 
	                if( !selectAttribute( node, iter.next()) )                                                    
	                    return false;                
			    }
            }
            
            return true;

    }

    public boolean selectSubAttribute( TemplateNode node, Element element, Vector<Attribute> attributeGroup, String diff) throws IOException
    {
    	context.pushElement(element);
    	context.setCurrentAttributeGroup(attributeGroup);
    	context.setDifferentName(diff);
    	context.pushVariable("diffqname", diff);
//    	System.out.println("different qname "+ diff);
        boolean ret = executeTemplateChildNodes(node);
        context.popVariable("diffqname");
        context.popElement();
        //Context.getCurrentAttributeGroup();
        return ret;
    }
    private boolean printSeperator( String sep, boolean first )
    {
        if( sep != null && !first )
        {
            PrintWriter file = context.getCurrentFile();
            if( file != null )
                file.print(sep);
        }
        return false;
    }
    
    private boolean executeForeachNode( TemplateNode node ) throws IOException
    {
        String type = node.getAttribute(TemplateNode.TYPE_ATTRIBUTE);
        String sep = node.getAttribute("seperator");
        if( ((sep != null) && (sep.length() == 0)) || (context.getCurrentFile() == null) )
            sep = null;

        boolean first = true;
        
        if( type.equals("element") || type.equals("baseelement") )
        {
            boolean baseElements = type.equals("baseelement");
            Iterator< Element > iter = baseElements ? schema.getBaseElements() : schema.getElements();
            while( iter.hasNext() )
            {
                first = printSeperator( sep, first );
                
                if( !selectElement( node, iter.next(), baseElements ) )                                                    
                    return false;                
            }
        }
        else if( type.equals("attribute") )
        {
            Element element = context.getCurrentElement();
            AttributeSet attrSet = context.getCurrentAttributeSet();
            if( element != null )
            {
            	Iterator< Attribute > iter = element.getAttributes();
                while( iter.hasNext() )
                {
                    first = printSeperator( sep, first );               
                    if( !selectAttribute( node, iter.next() ) )
                        return false;
                }
            }else if( attrSet != null ){
            	Iterator< Attribute > iter = attrSet.getAttributes();
            	while( iter.hasNext() )
            	{
            		first = printSeperator( sep, first );
            		if( !selectAttribute( node, iter.next() ))
            			return false;
            	}
            }else{
            	System.err.println("error: foreach attribute needs a current element or a current attributeset!" );
                return false;
            }
        }
        else if( type.equals("attributeset"))
        {
        	Iterator<AttributeSet> iter = schema.getAttributeSets();
        	while( iter.hasNext() )
        	{
        		first = printSeperator( sep, first);
        		if( !selectAttributeSet( node, iter.next() ))
        			return false;
        	}
        }
        else if( type.equals("value") )
        {
            Attribute attr = context.getCurrentAttribute();
            if( attr != null )
            {
            	Iterator< String > iter = attr.getValues();
                while( iter.hasNext() )
                {
                    first = printSeperator( sep, first );
                    
                    context.pushVariable( "value", iter.next() );
                    boolean ret = executeTemplateChildNodes(node);               
                    context.popVariable( "value" );
                    
                    if( !ret )
                        return false;                
                }
        	}else
            {
                System.err.println("error: foreach values needs a current attribute!" );
                return false;
            }
        }else if( type.equals("valueset") || type.equals("defaultvalueset") )
        {
            AttributeSet attrSet = context.getCurrentAttributeSet();
            if( attrSet != null){
            	boolean isValueSetType = type.equals("valueset");
            	Iterator< String > iter = isValueSetType?attrSet.getValues():attrSet.getDefaultValues();
            	while( iter.hasNext() )
                {
                    first = printSeperator( sep, first );
                    
                    context.pushVariable( "value", iter.next() );
                    boolean ret = executeTemplateChildNodes(node);               
                    context.popVariable( "value" );
                    
                    if( !ret )
                        return false;                
                }
        	}else
            {
                System.err.println("error: foreach valueset or defaultvalueset needs a current attributeset!" );
                return false;
            }
        }
        else if( type.equals("namespace") )
        {
            HashMap< String, String > namespaces = schema.getNamespaces();
        	
            Set set = namespaces.entrySet();

            Map.Entry<String, String> [] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);

            Arrays.sort(entries, new Comparator() {
              public int compare(Object arg0, Object arg1) {
                Object key1 = ((Map.Entry) arg0).getKey();
                Object key2 = ((Map.Entry) arg1).getKey();
                return ((Comparable) key1).compareTo(key2);
              }});

 
            for(int i=0;i<entries.length; i++ )
            {
                first = printSeperator( sep, first );

                Entry<String, String> entry = entries[i];
                context.pushVariable("namespaceprefix", entry.getKey());
                context.pushVariable("namespaceuri", entry.getValue());
                
                boolean ret = executeTemplateChildNodes(node);
                context.popVariable("namespaceprefix");
                context.popVariable("namespaceuri");

                if( !ret )
                    return false;                
            }            
        } 
        else
        {
            System.err.println("error: unknown foreach type " + type );
            return false;
        }
        return true;
    }
    
    private boolean executeSelectNode( TemplateNode node ) throws IOException
    {
        String type = node.getAttribute(TemplateNode.TYPE_ATTRIBUTE);
        String name = decodeTemplateString( node.getAttribute(TemplateNode.NAME_ATTRIBUTE) );
        
        if( type.equals("attribute") )
        {
            Element element = context.getCurrentElement();
            if( element == null )
            {
                System.err.println("error: select attribute needs a current element!" );
                return false;
            }
            
            Attribute attr = element.getAttribute(name);
            if( attr == null )
            {
                System.err.println("error: selected attribute \"" + name + "\" not found inside current element!" );
                return false;                
            }
            return selectAttribute( node, attr );
        }else if( type.equals("element") || type.equals("baseelement" ) )
        {
            boolean baseElement = type.equals("baseelement" );
            Element element = schema.getElement( name, baseElement );
            if( element == null )
            {
                System.err.println("error: selected element \"" + name + "\" not found in schema!" );
                return false;                
            }
            
            return selectElement( node, element, baseElement );
        }
        else
        {
            System.err.println("error: unknwon type \"" + type + "\" for select!" );
            return false;                
        }
    }
    
    public boolean selectElement( TemplateNode node, Element element, boolean baseElement ) throws IOException
    {               
        context.pushElement(element);

        String baseName = new String();
        ElementConfig ec = config.getConfigForElement(element.getQName());
        if( (ec != null) && (ec.Base != null) && (ec.Base.length() != 0) )
        {
            if( ec.Base.indexOf( ':' ) == -1 )
                baseName = ec.Base;
            else                    
                baseName = schema.getBaseElement(ec.Base).getQName();
        }

        context.pushVariable( "elementbasename", baseName );

        boolean ret = executeTemplateChildNodes(node);

        context.popVariable( "elementbasename" );
        context.popElement();

        return ret;
    }
    
    public boolean selectAttribute( TemplateNode node, Attribute attr ) throws IOException
    {
        context.pushAttribute(attr);                
        boolean ret = executeTemplateChildNodes(node);
        context.popAttribute();
        return ret;
    }
    
    public boolean selectAttributeSet( TemplateNode node, AttributeSet attrSet ) throws IOException
    {
        context.pushAttributeSet(attrSet);                
        boolean ret = executeTemplateChildNodes(node);
        context.popAttributeSet();
        return ret;
    }
    
    private boolean executeCodeNode( TemplateNode node ) throws IOException
    {
        PrintWriter file = context.getCurrentFile();
        if( file != null )
        {
            file.print( decodeTemplateString( node.Characters ) );
            return true;
        }
        System.out.println("error: a <code> element can only work inside a <file> element!");
        return false;
    }
    
    private boolean executeFileNode( TemplateNode fileNode ) throws IOException
    {
        String path = getPath( decodeTemplateString( fileNode.getAttribute(TemplateNode.PATH_ATTRIBUTE) ) );
        String name = decodeTemplateString( fileNode.getAttribute(TemplateNode.NAME_ATTRIBUTE) );
        String ext = decodeTemplateString( fileNode.getAttribute(TemplateNode.EXTENSION_ATTRIBUTE) );

        PrintWriter file = createFileWriter( path + name + "." + ext );
        if (file == null)
            return false;
        
        context.pushFile( file );
        executeTemplateChildNodes( fileNode );        
        context.popFile();
        file.close();
        return true;
    }
          
    private String getPath( String subpath )
    {
        StringBuffer out = new StringBuffer( targetPath );
        if( out.charAt(out.length()-1) != File.separatorChar )
            out.append( File.separatorChar );
        
        out.append( subpath.replace('.', File.separatorChar) );

        if( out.charAt(out.length()-1) != File.separatorChar )
            out.append( File.separatorChar );
        
        if( File.separatorChar != '/' )
            return out.toString().replace('/', File.separatorChar );
        else
            return out.toString();
    }
    
    private String evaluateExpression( String var ) throws IOException    
    {
        return ExpressionParser.evaluate(var, context);
    }
    
    public String function(String func, Vector<String> params) throws IOException
    {
        int i = -1;
        if( func.equals("toupper") )
        {
            i = 1;
            if( params.size() == i )
                return params.get(0).toUpperCase();
        }
        else if( func.equals("tolower") )
        {
            i = 1;
            if( params.size() == i )
                return params.get(0).toLowerCase();
        }
        else if( func.equals("lowerfirst") )
        {
            i = 1;
			if( params.size() == i ){
				String p = params.get(0).substring(0, 1).toLowerCase();
				return p+ params.get(0).substring(1);
			}			    
        }
        else if( func.equals("prefix" ) )
        {
            i = 1;
            if( params.size() == i )
            {
                String p = params.get(0);
                int pos = p.lastIndexOf(':');
                if( pos == -1 )
                    pos = p.lastIndexOf('.');
                
                if( pos != -1 )
                    return p.substring(0,pos);
                else
                    return new String();
            }
        }
        else if( func.equals("local_name" ) )
        {
            i = 1;
            if( params.size() == i )
            {
                String p = params.get(0);
                int pos = p.lastIndexOf(':');
                if( pos == -1 )
                    pos = p.lastIndexOf('.');
    
                if( pos != -1 )
                    return p.substring(pos+1);
                else
                    return p;
            }
        }
        else if( func.equals("substring-before") )
        {
            i = 2;
            if( params.size() == i )
            {
                int pos = params.get(0).indexOf(params.get(1));
                if( pos == -1 )
                    return params.get(0);
                else if( pos > 0 )
                    return params.get(0).substring(0,pos);
                else
                    return new String();
            }
        }
        else if( func.equals("substring-after") )
        {
            i = 2;
            if( params.size() == i )
            {
                int pos = params.get(0).indexOf(params.get(1));
                if( pos == -1 )
                    return params.get(0);
                else
                    return params.get(0).substring(pos+1);
            }
        }
        else if( func.equals("identifier") )
        {
            i = 1;
            if( params.size() == i )
                return decodeIdentifier( params.get(0) );
        }
        else if( func.equals("endswith") )
        {
            i = 2;
            if( params.size() == i )
            {
                if( params.get(0).endsWith(params.get(1)))
                    return ExpressionParser.TOKEN_TRUE;
                else
                    return ExpressionParser.TOKEN_FALSE;
            }
        }
        else if( func.equals("startswith") )
        {
            i = 2;
            if( params.size() == i )
            {
                if( params.get(0).startsWith(params.get(1)))
                    return ExpressionParser.TOKEN_TRUE;
                else
                    return ExpressionParser.TOKEN_FALSE;
            }
        }
        else if( func.equals("replace") )
        {
            i = 3;
            if( params.size() == i )
            {
                return params.get(2).replaceAll( params.get(0), params.get(1) );
            }
        }
        else if( func.equals("contains") )
        {            
            i = 3;
            if( params.size() == i )
            {
                StringTokenizer tokens = new StringTokenizer(params.get(0), params.get(2) );
                while( tokens.hasMoreTokens() )
                    if( tokens.nextToken().equals(params.get(1)) )
                        return ExpressionParser.TOKEN_TRUE;
                
                return ExpressionParser.TOKEN_FALSE;
            }            
        }
        else if( func.equals("hasdelimiter"))
        {
        	i = 2;
        	if( params.size() == i )
        	{
        		if (params.get(0).contains(params.get(1)) )
        			return ExpressionParser.TOKEN_TRUE;
        		else
        			return ExpressionParser.TOKEN_FALSE;
        	}
        }
        
        if( i == -1 )
            throw new IOException( new String( "unknown function '"+func+"'!") );
        else
            throw new IOException( new String( "function '"+func+"' expects " + i + "arguments"));
    }
    
    private String decodeTemplateString( String in ) throws IOException
    {       
        StringBuffer out = new StringBuffer();
        
        int left = in.length();
        int last = 0, skip = 0;
        String replace = null;
        int i = 0;
        while( left > 0 )
        {
            //String debug = in.substring(i);
            switch( in.charAt(i) )
            {
                case '%':
                {                
                    if( left > 1 ) // can we peek?
                    {
                        switch( in.charAt(i+1) )
                        {
                            case '{':
                                int open = 1;
                                for( skip = i+2; (skip < in.length()) && (open > 0); skip++ )
                                {
                                    if( in.charAt(skip) == '{' )
                                        open++;
                                    else if( in.charAt(skip) == '}' )
                                        open--;
                                    else if( in.charAt(skip) == '"' )
                                    {
                                        // skip strings without checking for '{' and '}'
                                        for( skip = skip+1; (skip < in.length()) && (in.charAt(skip) != '"'); skip++ )
                                        {
                                            if( in.charAt(skip) == '\\' )
                                            {
                                                if( (skip + 1) < in.length() && in.charAt(skip+1) == '"' )
                                                    skip++;
                                            }                                                    
                                        }
                                    }
                                }
                                if( open == 0 )
                                {
                                    String tmp = in.substring( i+2, skip-1 );
                                    replace = evaluateExpression( tmp );
                                }
                                break;

                            case '%':
                                replace = "%";
                                skip = i+1;
                                break;
                        }
                    }
                    break;
                }
/* should be done by xml parser
                case '&':
                {
                    if( left >= 4 )
                    {
                        String enc = in.substring(i,i+4);
                        if( enc.equals("&lt;") )
                        {
                            replace = "<";
                            skip = i+4;
                        }
                        else if( enc.equals("&gt;") )
                        {
                            replace = ">";
                            skip = i+4;
                        }
                        else if( enc.equals("&amp;") )
                        {
                            replace = "&";
                            skip = i+5;
                        }                               
                    }
                    break;
                }
 */ 
            }
                
            if( replace != null )
            {
                if( i > last )
                    out.append( in.substring(last, i ) );
                out.append( replace );
                left -= skip - i;
                i = skip;
                last = i;
                replace = null;
            }
            else
            {
                i++;
                left--;
            }
                        
        }

        if( i > last )
            out.append( in.substring(last, i) );
        
        return out.toString();
    }        
    
    private PrintWriter createFileWriter(  String path )
    {
        System.out.println("generating " + path );
        try
        {
            File file = new File( path );
            if( createRecursiveDir( file.getParentFile() ) )               
            {
                if( file.isDirectory() )
                    return null;

                if( file.isFile() )
                    file.delete();
  
/* for debug purposes, do not overide but create new file
                if( file.isFile() )
                {
                    String tmp = file.getAbsolutePath();
                    int i = 2;
                    
                    do
                    {
                        file = new File( tmp + String.valueOf(i) );
                        i++;
                    }
                    while( file.isFile() );
                }
*/
                return new PrintWriter( file );
            }
        }
        catch( FileNotFoundException ex )
        {
            System.err.println("error: could not create file " + path + ", " + ex.toString() );
        }
        
        return null;
    }
    
    private boolean createRecursiveDir( File file )
    {
        if( (file == null) || file.isFile() )
            return false;
        if( file.isDirectory() )
            return true;

        File parent = file.getParentFile();
        if( parent != null )
            if( !createRecursiveDir( parent ) )
                return false;
            
        return file.mkdir();
    }    
    
    private String decodeIdentifier( String odfname )
    {           
        StringBuffer name = new StringBuffer();
        boolean bFirst = true;
        
        int i = odfname.indexOf(':')+1;
        for( ; i < odfname.length(); i++ )
        {
            if( odfname.charAt(i) == '-' )
            {
                bFirst = true;
                continue;
            }

            Character c = odfname.charAt(i);
            switch( c )
            {
                case '.': c = '_'; break;
            }
            
            if( bFirst )
            {
                name.append( Character.toUpperCase(c));
                bFirst = false;
            }
            else
            {
                //name.append( Character.toLowerCase(c));
            	name.append( c );
            }

        }

        // make sure the identifier does not start with a digit
        if( (name.length() > 0) && Character.isDigit(name.charAt(0) ) )
            name.insert(0, "_" );
        
        return name.toString();
    }

	public void setTemplate(CodeTemplate template) {
		this.template = template;
	}

	public CodeTemplate getTemplate() {
		return template;
	}
}
