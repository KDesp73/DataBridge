/*
* MIT License
*
* Copyright (c) 2023 Konstantinos Despoinidis

* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/


package kdesp73.madb;

import kdesp73.madb.exceptions.IncorrectOperatorException;

public class Condition {
        private String condition;

        /**
         * Creating the condition Column = Value
         *
         * @param Column
         * @param Value
         */
        public Condition(String Column, Object Value) {
                this.condition = createCondition(Column, Value);
        }

        /**
         * Creating the condition FirstColumn = FirstValue Some_Operator SecondColumn = SecoundValue
         *
         * @param FirstColumn
         * @param FirstValue
         * @param Operator
         * @param SecondColumn
         * @param SecondValue
         */
        public Condition(String FirstColumn, Object FirstValue, String Operator, String SecondColumn, Object SecondValue) {
                this.condition =  createCondition(FirstColumn, FirstValue, Operator, SecondColumn, SecondValue);
        }

        /**
         * Creating the condition Some_Operator Some_Subquery
         *
         * @param Operator
         * @param c
         */
        public Condition(String Operator, Condition c){
                this.condition = createCondition(Operator, c);
        }

        /**
         * Joins two conditions with the selected operator
         *
         * @param c1 First Condition
         * @param Operator Selected Operator
         * @param c2 Second Condition
         */
        public Condition(Condition c1, String Operator, Condition c2){
                this.condition = "(" + c1.getCondition() + ") " + createCondition(Operator, c2);
        }

        private String createCondition(String Column, Object Value) {
                if (Value instanceof String) {
                        return Column + " = \'" + Value + "\'";
                }
                return Column + " = " + Value;
        }

        private String createCondition(String FirstColumn, Object FirstValue, String Operator, String SecondColumn, Object SecondValue) throws IncorrectOperatorException{
                if(Operator.equals(kdesp73.madb.Operator.NOT)) throw new IncorrectOperatorException("NOT operator is NOT accepted here");

                if (FirstValue instanceof String && SecondValue instanceof String) {
                        return FirstColumn + " = \'" + FirstValue + "\' " + Operator + " " + SecondColumn  + " = \'" + SecondValue + "\'";
                }
                else if (FirstValue instanceof String) {
                        return FirstColumn + " = \'" + FirstValue + "\' " + Operator + " " + SecondColumn + " = " + SecondValue;
                }
                else if (SecondValue instanceof String) {
                        return FirstColumn + " = " + FirstValue + " " + Operator + " " + SecondColumn + " = \'" + SecondValue + "\'";
                }

                return FirstColumn + " = " + FirstValue + " " + Operator + " " + SecondColumn + " = " + SecondValue;
        }

        private String createCondition(String Operator, Condition c){
//                if(Operator.equals(accessDB.Operator.AND)) throw new IncorrectOperatorException("AND operator is not accepted here");
//                if(Operator.equals(accessDB.Operator.OR)) throw new IncorrectOperatorException("OR operator is not accepted here");
//                if(Operator.equals(accessDB.Operator.AND_NOT)) throw new IncorrectOperatorException("AND NOT operator is not accepted here");
//                if(Operator.equals(accessDB.Operator.OR_NOT)) throw new IncorrectOperatorException("OR NOT operator is not accepted here");

                return Operator + " (" +c.condition + ")";
        }

        public String getCondition() {
                return condition;
        }
}
