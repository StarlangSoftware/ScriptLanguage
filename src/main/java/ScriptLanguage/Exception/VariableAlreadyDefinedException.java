package ScriptLanguage.Exception;

public class VariableAlreadyDefinedException extends CompileException{
    private String variableName;

    public VariableAlreadyDefinedException(String variableName, int lineNumber) {
        super(lineNumber);
        this.variableName = variableName;
    }

    public String toString(){
        return "Variable " + variableName + " already defined";
    }
}
