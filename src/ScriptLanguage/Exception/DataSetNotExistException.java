package ScriptLanguage.Exception;

public class DataSetNotExistException extends RunTimeException{
    private String dataSetName;

    public DataSetNotExistException(String dataSetName){
        this.dataSetName = dataSetName;
    }

    public String toString(){
        return "DataSet " + dataSetName + " does not exist";
    }
}
