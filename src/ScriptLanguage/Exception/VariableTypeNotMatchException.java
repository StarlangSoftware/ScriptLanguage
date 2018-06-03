package ScriptLanguage.Exception;

public class VariableTypeNotMatchException extends Exception{
    private String variableType;
    private String variableName;

    public VariableTypeNotMatchException(String variableName, String variableType){
        this.variableName = variableName;
        this.variableType = variableType;
    }

    public String toString(){
        return "Variable " + variableName + " must be of type " + variableType;
    }
}
