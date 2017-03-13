/**
 * **********************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ***********************************************************************
 */
package schema2template.example;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.junit.Assert;
import schema2template.example.odf.OdfHelper;

public class ExampleGenerationTest {

	/**
	 * Test: It should be able to generate all examples without a failure.
	 */
	@Test
	public void testAllExampleGenerations() {
		try {
			OdfHelper.main(null);
		} catch (Exception ex) {
			Logger.getLogger(ExampleGenerationTest.class.getName()).log(Level.SEVERE, null, ex);
			Assert.fail(ex.toString());
		}
	}
}
