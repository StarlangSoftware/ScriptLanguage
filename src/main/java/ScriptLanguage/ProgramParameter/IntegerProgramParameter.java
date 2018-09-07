package ScriptLanguage.ProgramParameter;

public class IntegerProgramParameter extends ProgramParameter{

    public IntegerProgramParameter(String name, int value) {
        super(name);
        programParameterType = ProgramParameterType.INTEGER;
        this.value = value;
    }

    public void setValue(int value){
        this.value = value;
    }
}
