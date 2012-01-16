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

import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** Test some invalid packages.
 */
public class ValidTest extends OdfValidatorTestBase {

    @Test
    public void validate1() {
        String output = "";
        try {
            String name = "empty.odt";
            output = doValidation(name, OdfVersion.V1_0, OdfValidatorMode.VALIDATE_STRICT, true);						
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
		if(output.contains("Exception")){
			System.out.println("OUTPUT:" + output);
			Assert.fail("An exception occured during validation!");
		}
        Assert.assertTrue(output.contains("<span "));
    }
	
    @Test
	@Ignore
    public void validate2() {
        String output = "";
        try {
            String name = "testValid1.odt";
            output = doValidation(name, null);
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
		if(output.contains("Exception")){
			System.out.println("OUTPUT:" + output);
			Assert.fail("An exception occured during validation!");
		}		
        Assert.assertTrue(output.contains("testValid1.odt:Info:no errors, no warnings"));
    }	

   @Test
   @Ignore
    public void validate3() {
        String output = "";
        try {
            String name = "empty.odt";
            output = doValidation(name, OdfVersion.V1_0, OdfValidatorMode.VALIDATE_STRICT);				
			output = doValidation(name, OdfVersion.V1_1, OdfValidatorMode.VALIDATE);
			output = doValidation(name, OdfVersion.V1_2, null);			
        } catch (Throwable t) {
            t.printStackTrace();
            Assert.fail(t.toString());
        }
		if(output.contains("Exception")){
			System.out.println("OUTPUT:" + output);
			Assert.fail("An exception occured during validation!");
		}
        //Assert.assertTrue(output.contains("dummy.odt:Info:no errors, no warnings"));
    }	
	
}
