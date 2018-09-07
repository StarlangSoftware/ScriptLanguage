package ScriptLanguage.Exception;

public class CommandNotDefinedException extends CompileException{
    private String command;

    public CommandNotDefinedException(String command, int lineNumber) {
        super(lineNumber);
        this.command = command;
    }

    public String toString(){
        return "Command " + command + " not defined in line " + lineNumber;
    }
}
