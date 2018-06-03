package ScriptLanguage.Exception;

public class CaseOutsideOfSwitchException extends CompileException{

    public CaseOutsideOfSwitchException(int lineNumber) {
        super(lineNumber);
    }

    public String toString(){
        return "Case in line " + lineNumber + " is outside of any switch statement";
    }
}
