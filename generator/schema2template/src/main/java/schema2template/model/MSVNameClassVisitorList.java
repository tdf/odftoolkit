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
package schema2template.model;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Returns the Name(s) of an Expression (or more precisely: of its Nameclass) in a List of Strings.
 * A list instead of a single name is necessary as there might be multiple names for a single element definition.
 * For instance in ODF 1.2:
 * <code>
		<element>
			<choice>
				<name>text:reference-ref</name>
				<name>text:bookmark-ref</name>
			</choice>
 </code>

 *
 * <p>Convention: "*" will be returned as the wildcard for "any name"</p>
 *
 * <p>Usage example:
 * <em>(List&lt;String&gt;) myExpression.nameclass.visit(myMSVNameClassVisitorList)</em></p>
 *
 * <p>Please note that you do not use any method of this class directly!</p>
 */
public class MSVNameClassVisitorList implements NameClassVisitor {

    private static NamespaceDictionary nsdict = NamespaceDictionary.getStandardDictionary();

    @Override
    public List<String> onAnyName(AnyNameClass arg0) {
        return single("*");
    }

    @Override
    public List<String> onChoice(ChoiceNameClass arg0) {
        List<String> retval = new ArrayList<String>();
        retval.addAll((List<String>) arg0.nc1.visit(this));
        retval.addAll((List<String>) arg0.nc2.visit(this));
        return retval;
    }

    @Override
    public List<String> onDifference(DifferenceNameClass arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> onNot(NotNameClass arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> onNsName(NamespaceNameClass arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> onSimple(SimpleNameClass arg0) {
        return single(simplify(arg0.namespaceURI, arg0.localName));
    }

    // singleton list
    private List<String> single(String s) {
        return Collections.singletonList(s);
    }

    // (a.name.space.uri, localname) -> ns:localname
    private String simplify(String nsuri, String localname) {
        String shortns = nsdict.getLocalNamespace(nsuri);
        if (shortns == null || shortns.length() == 0) {
            return localname;
        }
        return shortns + ":" + localname;
    }

}
