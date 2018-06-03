package ScriptLanguage.Exception;

public class ListValueNotDefinedException extends RunTimeException{
    private String value;
    private String parameter;

    public ListValueNotDefinedException(String value, String parameter){
        this.value = value;
        this.parameter = parameter;
    }

    public String toString(){
        return "Value " + value + " is not one of the possible values for parameter " + parameter;
    }
}
