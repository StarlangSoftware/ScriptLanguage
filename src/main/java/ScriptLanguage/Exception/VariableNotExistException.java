package ScriptLanguage.Exception;

public class VariableNotExistException extends CompileException{
    private String name;

    public VariableNotExistException(String name){
        super(0);
        this.name = name;
    }

    public VariableNotExistException(String name, int lineNumber){
        super(lineNumber);
        this.name = name;
    }

    public String toString(){
        return "Variable " + name + " does not exist";
    }
}
