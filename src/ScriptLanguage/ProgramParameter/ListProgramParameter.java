package ScriptLanguage.ProgramParameter;

import ScriptLanguage.Exception.ListValueNotDefinedException;

import java.util.ArrayList;

public class ListProgramParameter extends StringProgramParameter{
    private ArrayList<String> valueList;

    public ListProgramParameter(String name, String value) {
        super(name, value);
        programParameterType = ProgramParameterType.LIST;
        valueList = new ArrayList<>();
    }

    public void addValue(String value){
        valueList.add(value);
    }

    public void setValue(String value) throws ListValueNotDefinedException {
        if (!valueList.contains(value)){
            throw new ListValueNotDefinedException(value, name);
        }
        this.value = value;
    }
}
