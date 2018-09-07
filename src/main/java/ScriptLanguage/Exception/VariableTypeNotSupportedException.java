package ScriptLanguage.Exception;

import ScriptLanguage.Variable.VariableType;

public class VariableTypeNotSupportedException extends RunTimeException{
    private String operator;
    private VariableType variableType;

    public VariableTypeNotSupportedException(String operator, VariableType variableType){
        this.operator = operator;
        this.variableType = variableType;
    }

    public String toString(){
        return "Variable type " + variableType + " is not supported by operator " + operator;
    }

}
