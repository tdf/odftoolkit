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

package org.odftoolkit.odfdom.codegen;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;

/**
 *
 * @author Christian
 */
public class ExpressionParser
{
    public final static String TOKEN_TRUE = "true";
    public final static String TOKEN_FALSE = "false";
    
    private StreamTokenizer Tokens;
    private Context Context;
    private ExpressionParser( String expression, Context context )
    {
        Tokens = new StreamTokenizer(new StringReader(expression));
        Tokens.resetSyntax();
        Tokens.wordChars('a', 'z');
        Tokens.wordChars('A', 'Z');
        Tokens.wordChars('0', '9');
        Tokens.wordChars('-', '-');
        Tokens.wordChars('.', '.');
        Tokens.wordChars('_', '_');
        Tokens.quoteChar('\'');
        Tokens.quoteChar('"');
        Tokens.whitespaceChars( '\u0000', ' ');
        Tokens.eolIsSignificant(false);
        Tokens.slashStarComments(false);
        Tokens.slashSlashComments(false);
        Context = context;
    }
    
    private final static String TOKEN_OR = "or";
    private final static String TOKEN_AND = "and";
    private final static String TOKEN_NOT = "not";
    
    // ident = "[a-z] | [A-Z] | . | _ ( [a-z] | [A-Z] | . | _ | [0-9] | - )
    // const string = "..." | '...'
    // function := ident ( [ expression { , expression } ] )
    // value-expression ::=
    //                variable
    //              | string
    //              | function
    //              | ( expression )
    //              | value-expression { + value-expression }
    //              | not value-expression
    // secondary-expression ::=
    //                value-expression
    //              | value-expression [ = value-expression ]
    //              | value-expression [ != value-expression ]
    // primary-expression ::=
    //                secondary-expression
    //              | secondary-expression { or secondary-expression }
    // expression ::=
    //                primary-expression
    //              | primary-expression { and primary-expression }
    //              | expression = expression
    //              | expression != expression
    
    
    private String evaluateValueExpression( boolean skip ) throws IOException
    {
        String value = null;
        
        if( Tokens.ttype == '(' )
        {
            if( Tokens.nextToken() == ')' )
                throw new IOException("found empty ()");

            value = evaluateExpression( skip );
            
            if( Tokens.ttype != ')' )
                throw new IOException("missing closing ')' for opening '('");
            
            Tokens.nextToken();
        }
        else if( (Tokens.ttype == '"') || (Tokens.ttype == '\'')  )
        {
            value = Tokens.sval;
            Tokens.nextToken();
        }
        else if( Tokens.ttype == StreamTokenizer.TT_WORD )
        {
            if( Tokens.sval.equals( TOKEN_OR ) || Tokens.sval.equals( TOKEN_AND ) )
            {
                throw new IOException( new String( "keyword '" + Tokens.sval + "' must follow a string expression" ) );
            }
            else if( Tokens.sval.equals( TOKEN_TRUE ) || Tokens.sval.equals( TOKEN_FALSE ) )
            {                
                value = Tokens.sval;
                Tokens.nextToken();
            }
            else if( Tokens.sval.equals( TOKEN_NOT ) )
            {
                Tokens.nextToken();
                return valueToBoolean( evaluateValueExpression( skip ) ) ? TOKEN_FALSE : TOKEN_TRUE;
            }
            else
            {
                String sval = Tokens.sval;
                if( Tokens.nextToken() == '(' ) // function?
                {
                    value = evaluateFunction( sval, skip );
                }
                else
                {
                    if( !skip )
                    {
                        value = Context.getVariable(sval);
                        if( value == null )
                            throw new IOException( new String("unknwon identifier " + sval ) );
                    }
                    else
                    {
                        value = "";
                    }
                }
            }
        }

        if( Tokens.ttype == '+' )
        {
            if( Tokens.nextToken() == StreamTokenizer.TT_EOF )
                throw new IOException( new String("no expression following '+'" ) );
            
            return value + evaluateValueExpression(skip);
        }
        else
        {
            return value;
        }
    }
    
    private String evaluateSecondaryExpression( boolean skip ) throws IOException
    {
        String value = evaluateValueExpression( skip );
        if(Tokens.ttype == '=' )
        {
            Tokens.nextToken();
            String value2 = evaluateValueExpression(skip);        
            return value.equals(value2) ? TOKEN_TRUE : TOKEN_FALSE;
        }
        else if((Tokens.ttype == '!') && (Tokens.nextToken() == '=') )
        {
            Tokens.nextToken();
            String value2 = evaluateValueExpression(skip);
            return value.equals(value2) ? TOKEN_FALSE : TOKEN_TRUE;
        }
        else
        {
            return value;
        }
    }
    
    private String evaluatePrimaryExpression( boolean skip ) throws IOException
    {
        String value = evaluateSecondaryExpression( skip );
        
        while( (Tokens.ttype == StreamTokenizer.TT_WORD) && Tokens.sval.equals(TOKEN_OR) )
        {
            Tokens.nextToken();               
            boolean op1 = valueToBoolean( value);
            String value2 = evaluatePrimaryExpression( skip || op1 );

            value =  op1 || valueToBoolean( value2 ) ? TOKEN_TRUE : TOKEN_FALSE;
        }            
        return value;
    }
    
    private String evaluateExpression( boolean skip ) throws IOException
    {
        String value = evaluatePrimaryExpression( skip );
        
        while( (Tokens.ttype == StreamTokenizer.TT_WORD) && Tokens.sval.equals(TOKEN_AND) )
        {
            Tokens.nextToken();                              
            boolean op1 = valueToBoolean( value);
            String value2 = evaluatePrimaryExpression( skip || !op1 );

            value = op1 && valueToBoolean( value2 ) ? TOKEN_TRUE : TOKEN_FALSE;
        }
        return value;
    }

    private static boolean valueToBoolean( String value )
    {
        return (value != null) && (value.length() != 0) && !value.equals(TOKEN_FALSE);
    }
 
    private String evaluate() throws IOException
    {
        String value;
        if( Tokens.nextToken() != StreamTokenizer.TT_EOF )
        {
            value = evaluateExpression(false);
            if( Tokens.ttype != StreamTokenizer.TT_EOF )
            {
                if( Tokens.ttype == StreamTokenizer.TT_WORD )
                    throw new IOException( new String( "syntax error at word '" + Tokens.sval + "'" ) );
                else
                    throw new IOException( new String( "syntax error at token '" + (char)Tokens.ttype + "'" ) );        
            }
        }
        else
        {
            value = "";
        }
        return value;
    }
    
    public static String evaluate( String expression, Context context ) throws IOException
    {
        return new ExpressionParser( expression, context ).evaluate();            
    }

    public static boolean evaluateBoolean( String expression, Context context ) throws IOException
    {
        return valueToBoolean( new ExpressionParser( expression, context ).evaluate() );
    }    

    private String evaluateFunction(String func, boolean skip) throws IOException
    {
        Vector< String > params = new Vector< String >();
        
        while( Tokens.nextToken() != ')' )
        {
            String p = evaluateExpression(skip);
            
            params.add( p );
            
            if( Tokens.ttype != ',' )
                break;
        }
        
        if( Tokens.ttype != ')' )
            throw new IOException( new String( "missformed parameter sequence for function '" + func + "'") );
        
        // skip ')'
        Tokens.nextToken();
        
        return Context.function( func, params );
    }

}
