package ScriptLanguage.Exception;

public abstract class CompileException extends Exception{
    protected int lineNumber;

    public CompileException(int lineNumber){
        this.lineNumber = lineNumber;
    }
}
