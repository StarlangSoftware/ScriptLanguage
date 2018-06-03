package ScriptLanguage.Exception;

public class SwitchInsideSwitchException extends CompileException{

    public SwitchInsideSwitchException(int lineNumber){
        super(lineNumber);
    }

    public String toString(){
        return "Switch statement inside switch statement in line " + lineNumber;
    }
}
