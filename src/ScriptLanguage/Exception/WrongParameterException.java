package ScriptLanguage.Exception;

public class WrongParameterException extends CompileException{
    private int parameterIndex;

    public WrongParameterException(int parameterIndex){
        super(0);
        this.parameterIndex = parameterIndex;
    }

    public String toString(){
        return "Wrong parameter type in parameter " + parameterIndex;
    }
}
