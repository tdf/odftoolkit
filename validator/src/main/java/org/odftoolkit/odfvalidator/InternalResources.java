/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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

package org.odftoolkit.odfvalidator;

public class InternalResources {

    static final private String INTERNAL_RESOURCE_PREFIX = "internal:";

    static boolean isInternalResourceIdentifer( String aPath )
    {
        return aPath.startsWith(INTERNAL_RESOURCE_PREFIX);
    }
    
    static String getResourcePath( String aPath )
    {
		String newPath = null;
        if(aPath.startsWith("internal:")){			
			newPath = aPath.substring(INTERNAL_RESOURCE_PREFIX.length());
		}
		if(!newPath.startsWith("/")){		
			newPath = "/".concat(aPath.substring(INTERNAL_RESOURCE_PREFIX.length()));
		}			
		return newPath;
    }
    
    static String createInternalResourceIdentifier( String aPath )
    {
        return INTERNAL_RESOURCE_PREFIX.concat(aPath);
    }
}
