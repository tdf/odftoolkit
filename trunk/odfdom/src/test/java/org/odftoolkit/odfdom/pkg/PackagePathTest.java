/* 
  Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package org.odftoolkit.odfdom.pkg;


import org.junit.Assert;
import org.junit.Test;

public class PackagePathTest {

	@Test
	public void normalizePath() {
		Assert.assertEquals(OdfPackage.normalizePath("docA1///docA2/.."), "docA1/");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/../docA2"), "docA2");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/../docA2/./."), "docA2/");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/docA2/docA3/../../docA4/"), "docA1/docA4/");
		Assert.assertEquals(OdfPackage.normalizePath("docA1/docA2/docA3/../docA4/../../docA5/"), "docA1/docA5/");
	}

}
