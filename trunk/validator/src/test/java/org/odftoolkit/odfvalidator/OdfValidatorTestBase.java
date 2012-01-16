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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Ignore;

@Ignore
public class OdfValidatorTestBase {

    public OdfValidatorTestBase() {
    }

    String doValidation(String aFileName, OdfVersion aVersion) throws Exception {
        ODFValidator aValidator = new ODFValidator(null, Logger.LogLevel.INFO, aVersion, true);
        ByteArrayOutputStream aOut = new ByteArrayOutputStream();
        PrintStream aPOut = new PrintStream(aOut);
        InputStream aIn = getClass().getClassLoader().getResourceAsStream(aFileName);
        aValidator.validateStream(aPOut, aIn, aFileName, OdfValidatorMode.VALIDATE, null);
        return aOut.toString();
    }

    String doValidation(String aFileName, OdfVersion aVersion, OdfValidatorMode odfValidatorMode) throws Exception {
        return doValidation(aFileName, aVersion, odfValidatorMode, false);				
	}		
	
    String doValidation(String aFileName, OdfVersion aVersion, OdfValidatorMode odfValidatorMode, boolean htmlOutput) throws Exception {
        ODFValidator aValidator = new ODFValidator(null, Logger.LogLevel.INFO, htmlOutput, aVersion, true);
        ByteArrayOutputStream aOut = new ByteArrayOutputStream();
        PrintStream aPOut = new PrintStream(aOut);
        InputStream aIn = getClass().getClassLoader().getResourceAsStream(aFileName);
		ValidationMessageCollectorErrorFilter filter = new ValidationMessageCollectorErrorFilter();
		try {
			aValidator.validateStream(aPOut, aIn, aFileName, odfValidatorMode, filter);
		} catch (Exception e) {
			e.printStackTrace(aPOut);			
		}
        return aOut.toString();							
	}				
}
