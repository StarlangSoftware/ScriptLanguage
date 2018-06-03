package ScriptLanguage.Exception;

public class ExtraParametersException extends CompileException{
    private String command;

    public ExtraParametersException(String command, int lineNumber) {
        super(lineNumber);
        this.command = command;
    }

    public String toString(){
        return "Number of parameters of command " + command + " is larger than required in line " + lineNumber;
    }

}
