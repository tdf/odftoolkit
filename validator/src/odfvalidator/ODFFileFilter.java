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

package odfvalidator;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

        
public class ODFFileFilter implements FileFilter
{
    private boolean m_bRecursive;
    private Pattern m_aExcludePattern = null;
    
    public ODFFileFilter( boolean bRecursive )
    {
        m_bRecursive = bRecursive;
    }

    public ODFFileFilter( String aExcludeRegExp, boolean bRecursive )
    {
        m_bRecursive = bRecursive;
        if( aExcludeRegExp != null )
            m_aExcludePattern = Pattern.compile(aExcludeRegExp);
    }
    
    
    public boolean accept(File aFile) {
        return !exclude( aFile ) && (aFile.isDirectory() ? m_bRecursive : hasODFExtension( aFile ));
    }
    
    private boolean exclude(File aFile)
    {
        boolean bExclude = false;
        if( m_aExcludePattern != null )
            bExclude = m_aExcludePattern.matcher(aFile.getAbsolutePath()).matches();
        return bExclude;
    }
    
    private boolean hasODFExtension(File aFile)
    {
        String aName = aFile.getName();
        int nIndex = aName.lastIndexOf('.');
        if (nIndex != -1 && aName.length() > nIndex+1)
        {
            String aExt = aName.substring(nIndex+1);
            if( aExt.length() == 3 )
            {
                return aExt.equalsIgnoreCase("odt") ||
                       aExt.equalsIgnoreCase("ods") ||
                       aExt.equalsIgnoreCase("odg") ||
                       aExt.equalsIgnoreCase("odp") ||
                       aExt.equalsIgnoreCase("odf") ||
                       aExt.equalsIgnoreCase("odc") ||
                       aExt.equalsIgnoreCase("odb") ||
                       aExt.equalsIgnoreCase("ott") ||
                       aExt.equalsIgnoreCase("ots") ||
                       aExt.equalsIgnoreCase("otg") ||
                       aExt.equalsIgnoreCase("otp") ||
                       aExt.equalsIgnoreCase("otf") ||
                       aExt.equalsIgnoreCase("otc");
            }
        }
        
        return false;
    }
}
