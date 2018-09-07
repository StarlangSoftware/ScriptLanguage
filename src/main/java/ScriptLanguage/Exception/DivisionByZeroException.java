package ScriptLanguage.Exception;

public class DivisionByZeroException extends RunTimeException{
    private String variableName;

    public DivisionByZeroException(String variableName){
        this.variableName = variableName;
    }

    public String toString(){
        return "Can not be divided by variable " + variableName + " which is zero.";
    }
}
