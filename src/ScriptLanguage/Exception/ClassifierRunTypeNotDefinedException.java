package ScriptLanguage.Exception;

public class ClassifierRunTypeNotDefinedException extends RunTimeException{
    private String runType;

    public ClassifierRunTypeNotDefinedException(String runType){
        this.runType = runType;
    }

    public String toString(){
        return "Classifier run type " + runType + " is not defined";
    }
}
