/************************************************************************
 *
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
 ************************************************************************/

package org.odftoolkit.odfdom.dom.type;

import org.odftoolkit.odfdom.dom.type.OdfMeasure;
import org.odftoolkit.odfdom.dom.util.OdfUnitConverter;

/** This value type stores either a measure value in 1/100th mm as int or a
 *  percent value as double.
 */
public class OdfLengthOrPercent
{
    private Object Value;
    
    public OdfLengthOrPercent()
    {        
        Value = null;
    }
   
    public void setLength( int length )
    {
        Value = new Integer(length);
    }
    
    public void setPercent( double percent )
    {
        Value = new Double(percent);
    }
    
    public boolean isPercent()
    {
        return (Value != null) && Value instanceof Double;
    }
    
    public int getLength()
    {
        if( isPercent() )
        {
            throw new IllegalArgumentException("I do not have a length value");
        }
        else if( Value != null )
        {
            return ((Integer)Value).intValue();
        }
        else
        {
            return 0;
        }
    }
    
    public double getPercent()
    {
        if( isPercent() )
        {
            return ((Double)Value).doubleValue();
        }
        else
        {
            throw new IllegalArgumentException("I do not have a percent value");
        }
    }
    
    public static String toString( OdfLengthOrPercent val )
    {
        if( val == null )
            throw new IllegalArgumentException( "Illegal length or percent value");
        
        if( val.isPercent() )
            return String.valueOf( val.getPercent() ) + "%";
        else
            return OdfUnitConverter.getMeasureString(val.getLength());
    }

    public static OdfLengthOrPercent valueOf( String value )
    {
        OdfLengthOrPercent ret = new OdfLengthOrPercent();
        int n = value.indexOf( "%" );
	if( n != -1 )
            ret.setPercent( OdfPercent.valueOf(value));
        else
            ret.setLength(OdfMeasure.valueOf(value));
        return ret;
    }
}
