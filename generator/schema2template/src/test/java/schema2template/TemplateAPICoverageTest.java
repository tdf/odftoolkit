/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2009, 2010 Oracle and/or its affiliates. All rights reserved.
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
package schema2template;

import java.lang.reflect.Method;
import java.util.HashSet;
import org.junit.Test;
import schema2template.model.PuzzleComponent;
import schema2template.model.PuzzlePiece;
import schema2template.model.PuzzlePieceSet;
import schema2template.model.QNameValue;
import schema2template.model.QNamed;
import schema2template.model.QNamedPuzzleComponent;
import schema2template.model.XMLModel;
import static org.junit.Assert.*;

public class TemplateAPICoverageTest {

    public TemplateAPICoverageTest() {
    }

    // Method name and number of input parameters
    public static class MethodWithParameterCount {
        public String methodName;
        public int parameters;

        @Override
        public boolean equals(Object o) {
            if (o instanceof MethodWithParameterCount) {
                MethodWithParameterCount om = ((MethodWithParameterCount) o);
                if (om.methodName.equals(methodName) && om.parameters == parameters) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return methodName.hashCode();
        }
    }


    // Method-HashSet enhanced by convenient search functionality
    public static class MethodSet extends HashSet<MethodWithParameterCount> {

        public boolean contains(String methodName, int parameters) {
            MethodWithParameterCount mwpc = new MethodWithParameterCount();
            mwpc.methodName = methodName;
            mwpc.parameters = parameters;
            return contains(mwpc);
        }

    }


    // get all Methods of a given class
    public static MethodSet getMethods(Class clazz) {
        MethodSet retval = new MethodSet();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            MethodWithParameterCount mwpc = new MethodWithParameterCount();
            mwpc.methodName = method.getName();
            mwpc.parameters = method.getParameterTypes().length;
            retval.add(mwpc);
        }
        return retval;
    }


    /**
     * Test coverage: Test existance of methods made for template usage.
     * If you rename a method, this test will fail to show that all templates
     * using this method have to be adapted.
     *
     * The input parameter count of each method will be tested for additional safety.
     */
    @Test
    public void testCoverage() {
        MethodSet methods;
        
        methods = getMethods(PuzzleComponent.class);
        assertTrue(methods.contains("canHaveText", 0));
        assertTrue(methods.contains("getAttributes", 0));
        assertTrue(methods.contains("getChildElements", 0));
        assertTrue(methods.contains("getCollection", 0));
        assertTrue(methods.contains("getDatatypes", 0));
        assertTrue(methods.contains("getParents", 0));
        assertTrue(methods.contains("getType", 0));
        assertTrue(methods.contains("getValues", 0));
        assertTrue(methods.contains("isSingleton", 1));

        methods = getMethods(QNamed.class);
        assertTrue(methods.contains("getLocalName",0));
        assertTrue(methods.contains("getNamespace",0));
        assertTrue(methods.contains("getQName",0));

        methods = getMethods(XMLModel.class);
        assertTrue(methods.contains("camelCase",1));
        assertTrue(methods.contains("constantCase",1));
        assertTrue(methods.contains("escapeKeyword",1));
        assertTrue(methods.contains("escapeLiteral",1));
        assertTrue(methods.contains("extractLocalname",1));
        assertTrue(methods.contains("extractNamespace",1));
        assertTrue(methods.contains("firstWord",1));
        assertTrue(methods.contains("getAttribute",1));
        assertTrue(methods.contains("getAttribute",2));
        assertTrue(methods.contains("getAttributes",0));
        assertTrue(methods.contains("getElement",1));
        assertTrue(methods.contains("getElement",2));
        assertTrue(methods.contains("getElements",0));
        assertTrue(methods.contains("javaCase",1));
        assertTrue(methods.contains("lastWord",1));

        // Test inheritance -> so there's no need to test inherited methods
        assertTrue(QNamed.class.isAssignableFrom(PuzzlePiece.class));
        assertTrue(QNamed.class.isAssignableFrom(PuzzlePieceSet.class));
        assertTrue(QNamed.class.isAssignableFrom(QNameValue.class));
        assertTrue(QNamed.class.isAssignableFrom(QNamedPuzzleComponent.class));
        assertTrue(PuzzleComponent.class.isAssignableFrom(QNamedPuzzleComponent.class));
        assertTrue(QNamedPuzzleComponent.class.isAssignableFrom(PuzzlePiece.class));
        assertTrue(QNamedPuzzleComponent.class.isAssignableFrom(PuzzlePieceSet.class));
    }
}