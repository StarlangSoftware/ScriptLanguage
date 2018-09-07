package ScriptLanguage.Exception;

public class ExpressionNotEvaluatedException extends RunTimeException{

    public ExpressionNotEvaluatedException(){

    }

    public String toString(){
        return "Expression can not be evaluated";
    }
}
