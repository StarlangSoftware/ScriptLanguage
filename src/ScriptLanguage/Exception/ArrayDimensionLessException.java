package ScriptLanguage.Exception;

public class ArrayDimensionLessException extends RunTimeException {
    private String variableName;

    public ArrayDimensionLessException(String variableName){
        this.variableName = variableName;
    }

    public String toString(){
        return "Array " + variableName + "'s dimension is less than expected";
    }
}
