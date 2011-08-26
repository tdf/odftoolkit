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

package odfxsltrunner;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerException;

/**
 * This class forwards error reports from the XSLT processor to the
 * logger.
 */
class TransformerErrorListener implements javax.xml.transform.ErrorListener {
    
    private Logger m_aLogger;

    /** Creates a new instance of TransforemerErrorListener */
    TransformerErrorListener(Logger aLogger ) {
        m_aLogger = aLogger;
    }
    
    public void warning(TransformerException e) throws TransformerException {
        m_aLogger.logWarning(  e );
    }

    public void fatalError(TransformerException e) throws TransformerException {
        fatalErrorNoException( e );
    }

    public void error(TransformerException e) throws TransformerException {
        m_aLogger.logError( e );
    }

    public void fatalErrorNoException(TransformerException e) {
        m_aLogger.logFatalError(  e );
    }    
}
