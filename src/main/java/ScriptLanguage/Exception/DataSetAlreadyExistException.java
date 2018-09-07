package ScriptLanguage.Exception;

public class DataSetAlreadyExistException extends RunTimeException{
    private String dataSetName;

    public DataSetAlreadyExistException(String dataSetName){
        this.dataSetName = dataSetName;
    }

    public String toString(){
        return "DataSet " + dataSetName + " already exists";
    }
}
