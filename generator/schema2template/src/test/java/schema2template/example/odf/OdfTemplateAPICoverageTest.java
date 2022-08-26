/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */
package schema2template.example.odf;

import static org.junit.Assert.*;

import org.junit.Test;
import schema2template.TemplateAPICoverageTest;
import schema2template.TemplateAPICoverageTest.MethodSet;
import schema2template.model.QNamed;

public class OdfTemplateAPICoverageTest {

  public OdfTemplateAPICoverageTest() {}

  /**
   * Test coverage: Test existense of methods made for template usage. If you rename a method, this
   * test will fail to show that all templates using this method have to be adapted.
   *
   * <p>The input parameter count of each method will be tested for additional safety.
   */
  @Test
  public void testOdfRelevantCoverage() {
    MethodSet methods;

    methods = TemplateAPICoverageTest.getMethods(OdfModel.class);
    assertTrue(methods.contains("getDefaultAttributeValue", 2));
    assertTrue(methods.contains("getStyleFamilies", 0));
    assertTrue(methods.contains("getStyleFamilies", 1));
    assertTrue(methods.contains("isStylable", 1));

    methods = TemplateAPICoverageTest.getMethods(SourceCodeModel.class);
    assertTrue(methods.contains("getBaseClass", 1));
    assertTrue(methods.contains("getBaseClasses", 0));
    assertTrue(methods.contains("getBaseClassOf", 1));
    assertTrue(methods.contains("getConversiontype", 1));
    assertTrue(methods.contains("getPrimitiveType", 1));
    assertTrue(methods.contains("getValuetype", 1));
    assertTrue(methods.contains("getValuetypes", 1));

    methods = TemplateAPICoverageTest.getMethods(SourceCodeBaseClass.class);
    assertTrue(methods.contains("isStylable", 0));
    assertTrue(methods.contains("getBaseAttributes", 0));
    assertTrue(methods.contains("getElements", 0));

    // Test inheritance -> so there's no need to test inherited methods
    assertTrue(QNamed.class.isAssignableFrom(SourceCodeBaseClass.class));
  }
}
