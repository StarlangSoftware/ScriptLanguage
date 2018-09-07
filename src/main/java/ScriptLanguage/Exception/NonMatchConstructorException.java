package ScriptLanguage.Exception;

public class NonMatchConstructorException extends CompileException{
    private String constructor, matchConstructor;

    public NonMatchConstructorException(String constructor, String matchConstructor, int lineNumber){
        super(lineNumber);
        this.constructor = constructor;
        this.matchConstructor = matchConstructor;
    }

    public String toString(){
        return "Constructor " + constructor + " does not have a matching " + matchConstructor + " in line " + lineNumber;
    }
}
