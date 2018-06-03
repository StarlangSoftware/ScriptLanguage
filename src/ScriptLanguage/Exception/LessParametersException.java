package ScriptLanguage.Exception;

public class LessParametersException extends CompileException{
    private String command;

    public LessParametersException(String command, int lineNumber) {
        super(lineNumber);
        this.command = command;
    }

    public String toString(){
        return "Number of parameters of command " + command + " is less than required in line " + lineNumber;
    }
}
