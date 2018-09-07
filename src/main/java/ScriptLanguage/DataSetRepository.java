package ScriptLanguage;

import Classification.DataSet.DataSet;
import ScriptLanguage.Exception.DataSetAlreadyExistException;
import ScriptLanguage.Exception.DataSetNotExistException;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class DataSetRepository {
    private HashMap<String, DataSet> repository;

    public DataSetRepository(){
        repository = new HashMap<>();
    }

    public void loadDataSets(String directory){
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        for (File file: listOfFiles){
            String fileName = file.getName();
            String dataSetName = fileName.substring(0, fileName.indexOf("."));
            if (repository.containsKey(dataSetName)){
                try {
                    throw new DataSetAlreadyExistException(dataSetName);
                } catch (DataSetAlreadyExistException e) {
                    System.out.println(e.toString());
                }
            }
            repository.put(dataSetName, new DataSet(file));
            System.out.println("DataSet " + dataSetName + " loaded.");
        }
    }

    public void unloadDataSets(){
        repository.clear();
    }

    public DataSet getDataSet(String name) throws DataSetNotExistException {
        if (!repository.containsKey(name)){
            throw new DataSetNotExistException(name);
        }
        return repository.get(name);
    }

    public void updateDataSet(String name, DataSet dataSet){
        repository.put(name, dataSet);
    }

    public String toString(){
        String result = "";
        result = result + String.format("%20s%15s%15s%20s%15s%15s", "DataSet Name", "Sample Size", "Class Count", "Attribute Count", "Discrete", "Continuous") + "\n";
        for (String dataSet : repository.keySet()){
            result = result + repository.get(dataSet).toString(dataSet) + "\n";
        }
        return result;
    }
}
