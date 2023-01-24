package accessDB;

public class Condition<T, G> {
        private String condition;
        
        /**
         * Creating the condition Column = Value
         * 
         * @param Column
         * @param Value 
         */
        public Condition(String Column, T Value) {
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
        public Condition(String FirstColumn, T FirstValue, String Operator, String SecondColumn, G SecondValue) {
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
        
        private String createCondition(String Column, T Value) {
                if (Value instanceof String) {
                        return Column + " = \'" + Value + "\'";
                }
                return Column + " = " + Value;
        }
        
        private String createCondition(String FirstColumn, T FirstValue, String Operator, String SecondColumn, G SecondValue) throws IncorrectOperatorException{
                if(Operator.equals(accessDB.Operator.NOT)) throw new IncorrectOperatorException("NOT operator is NOT accepted here");
                
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
