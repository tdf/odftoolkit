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

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ExpressionVisitor;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * Get type of expression
 *
 * <p>Usage example:
 * <em>(MSVExpressionType) myExpression.visit(myMSVExpressionVisitorType)</em></p>
 *
 * <p>Please note that you do not use any method of this class directly!</p>
 */
public class MSVExpressionVisitorType implements ExpressionVisitor {

    @Override
    public MSVExpressionType onAnyString() {
        return MSVExpressionType.STRING;
    }

    @Override
    public MSVExpressionType onAttribute(AttributeExp arg0) {
        return MSVExpressionType.ATTRIBUTE;
    }

    @Override
    public MSVExpressionType onChoice(ChoiceExp arg0) {
        return MSVExpressionType.CHOICE;
    }

    @Override
    public MSVExpressionType onConcur(ConcurExp arg0) {
        return MSVExpressionType.CONCUR;
    }

    @Override
    public MSVExpressionType onData(DataExp arg0) {
        return MSVExpressionType.DATA;
    }

    @Override
    public MSVExpressionType onElement(ElementExp arg0) {
        return MSVExpressionType.ELEMENT;
    }

    @Override
    public MSVExpressionType onEpsilon() {
        return MSVExpressionType.EPSILON;
    }

    @Override
    public MSVExpressionType onInterleave(InterleaveExp arg0) {
        return MSVExpressionType.INTERLEAVE;
    }

    @Override
    public MSVExpressionType onList(ListExp arg0) {
        return MSVExpressionType.LIST;
    }

    @Override
    public MSVExpressionType onMixed(MixedExp arg0) {
        return MSVExpressionType.MIXED;
    }

    @Override
    public MSVExpressionType onNullSet() {
        return MSVExpressionType.NULLSET;
    }

    @Override
    public MSVExpressionType onOneOrMore(OneOrMoreExp arg0) {
        return MSVExpressionType.ONEOREMORE;
    }

    @Override
    public MSVExpressionType onOther(OtherExp arg0) {
        return MSVExpressionType.OTHER;
    }

    @Override
    public MSVExpressionType onRef(ReferenceExp arg0) {
        return MSVExpressionType.REF;
    }

    @Override
    public MSVExpressionType onSequence(SequenceExp arg0) {
        return MSVExpressionType.SEQUENCE;
    }

    @Override
    public MSVExpressionType onValue(ValueExp arg0) {
        return MSVExpressionType.VALUE;
    }

}
