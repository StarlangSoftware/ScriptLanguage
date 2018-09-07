package ScriptLanguage.Exception;

import ScriptLanguage.Variable.VariableType;

public class OperatorNotSupportedException extends RunTimeException{
    private String operator;
    private VariableType variableType;

    public OperatorNotSupportedException(String operator, VariableType variableType){
        this.operator = operator;
        this.variableType = variableType;
    }

    public String toString(){
        return "Variable type " + variableType + " does not support operator " + operator;
    }
}
