/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009 Sun Microsystems, Inc. All rights reserved.
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
package schema2template.example.odf;

import org.junit.Test;
import schema2template.TemplateAPICoverageTest;
import schema2template.TemplateAPICoverageTest.MethodSet;
import schema2template.model.QNamed;
import static org.junit.Assert.*;

public class OdfTemplateAPICoverageTest {

    public OdfTemplateAPICoverageTest() {
    }

    /**
     * Test coverage: Test existense of methods made for template usage.
     * If you rename a method, this test will fail to show that all templates
     * using this method have to be adapted.
     */
    @Test
    public void testOdfCoverage() {
        MethodSet methods;
        
        methods = TemplateAPICoverageTest.getMethods(OdfModel.class);
        assertTrue(methods.contains("getDefaultAttributeValue", 2));
        assertTrue(methods.contains("getStyleFamilies", 0));
        assertTrue(methods.contains("getStyleFamilies", 1));
        assertTrue(methods.contains("isStylable", 1));

        methods = TemplateAPICoverageTest.getMethods(SourceCodeModel.class);
        assertTrue(methods.contains("getBaseclass",1));
        assertTrue(methods.contains("getBaseclasses",0));
        assertTrue(methods.contains("getBaseclassOf",1));
        assertTrue(methods.contains("getConversiontype",1));
        assertTrue(methods.contains("getSimpleType",1));
        assertTrue(methods.contains("getValuetype",1));
        assertTrue(methods.contains("getValuetypes",1));

        methods = TemplateAPICoverageTest.getMethods(SourceCodeBaseClass.class);
        assertTrue(methods.contains("isStylable",0));
        assertTrue(methods.contains("getBaseAttributes",0));
        assertTrue(methods.contains("getElements",0));

        // Test inheritance -> so there's no need to test inherited methods
        assertTrue(QNamed.class.isAssignableFrom(SourceCodeBaseClass.class));
    }
}