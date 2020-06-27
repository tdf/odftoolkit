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
 * A list instead of a single name is necessary as there might be multiple names for a single
 * element definition. For instance in ODF 1.2: <code>
 * <element>
 * <choice>
 * <name>text:reference-ref</name>
 * <name>text:bookmark-ref</name>
 * </choice>
 * </code>
 *
 * <p>Convention: "*" will be returned as the wildcard for "any name"
 *
 * <p>Usage example: <em>(List&lt;String&gt;)
 * myExpression.nameclass.visit(myMSVNameClassVisitorList)</em>
 *
 * <p>Please note that you do not use any method of this class directly!
 */
public class MSVNameClassVisitorList implements NameClassVisitor {

  private static NamespaceDictionary nsdict = NamespaceDictionary.getStandardDictionary();

  public List<String> onAnyName(AnyNameClass arg0) {
    return single("*");
  }

  public List<String> onChoice(ChoiceNameClass arg0) {
    List<String> retval = new ArrayList<String>();
    retval.addAll((List<String>) arg0.nc1.visit(this));
    retval.addAll((List<String>) arg0.nc2.visit(this));
    return retval;
  }

  // ToDo: Temporary workaround, to stop test run, but not correct, yet!!
  // W3C Schema restriction on name have to be given out as more adequate for us!
  public List<String> onDifference(DifferenceNameClass arg0) {
    if (arg0 != null) {
      List<String> l = new ArrayList<String>(2);
      l.add(arg0.nc1.toString());
      l.add(arg0.nc2.toString());
      return l;
    } else {
      return null;
    }
  }

  public List<String> onNot(NotNameClass arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public List<String> onNsName(NamespaceNameClass arg0) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

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
