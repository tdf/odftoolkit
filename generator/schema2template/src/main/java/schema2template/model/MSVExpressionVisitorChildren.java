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
package schema2template.model;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This visitor visits an Expression and returns a list of child expressions
 *
 * <p>Usage example:
 * <em>(List&lt;Expression&gt;) myExpression.visit(myMSVExpressionVisitorChildren)</em></p>
 *
 * <p>Please note that you do not use any method of this class directly!</p>
 */
public class MSVExpressionVisitorChildren implements ExpressionVisitor {

    private static final List<Expression> empty = Collections.EMPTY_LIST;

    public List<Expression> onAnyString() {
        return empty;
    }

    public List<Expression> onAttribute(AttributeExp exp) {
        return child(exp.exp);
    }

    public List<Expression> onChoice(ChoiceExp exp) {
        return children(exp.children());
    }

    public List<Expression> onConcur(ConcurExp exp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Expression> onData(DataExp exp) {
        return empty;
    }

    public List<Expression> onElement(ElementExp exp) {
        return child(exp.getContentModel());
    }

    public List<Expression> onEpsilon() {
        return empty;
    }

    public List<Expression> onInterleave(InterleaveExp exp) {
        return children(exp.children());
    }

    public List<Expression> onList(ListExp exp) {
        return empty;
    }

    public List<Expression> onMixed(MixedExp exp) {
        return child(exp.exp);
    }

    public List<Expression> onNullSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Expression> onOneOrMore(OneOrMoreExp exp) {
        return child(exp.exp);
    }

    public List<Expression> onOther(OtherExp exp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Expression> onRef(ReferenceExp exp) {
        return child(exp.exp);
    }

    public List<Expression> onSequence(SequenceExp exp) {
        return children(exp.children());
    }

    public List<Expression> onValue(ValueExp exp) {
        return empty;
    }

    private List<Expression> children(Iterator<Expression> i) {
        ArrayList<Expression> list = new ArrayList<Expression>();
        while (i.hasNext()) {
            list.add(i.next());
        }
        return list;
    }

    private List<Expression> child(Expression e) {
        return Collections.singletonList(e);
    }

}
