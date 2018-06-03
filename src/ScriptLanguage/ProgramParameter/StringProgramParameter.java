package ScriptLanguage.ProgramParameter;

import ScriptLanguage.Exception.ListValueNotDefinedException;

public class StringProgramParameter extends ProgramParameter{

    public StringProgramParameter(String name, String value) {
        super(name);
        this.value = value;
        programParameterType = ProgramParameterType.STRING;
    }

    public void setValue(String value) throws ListValueNotDefinedException {
        this.value = value;
    }

}
