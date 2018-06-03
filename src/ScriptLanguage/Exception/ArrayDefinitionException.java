package ScriptLanguage.Exception;

public class ArrayDefinitionException extends CompileException{
    private String definitionText;

    public ArrayDefinitionException(String definitionText, int lineNumber){
        super(lineNumber);
        this.definitionText = definitionText;
    }

    public String toString(){
        return "Array definition text " + definitionText + " is wrong.";
    }
}
