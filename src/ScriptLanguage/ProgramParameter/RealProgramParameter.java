package ScriptLanguage.ProgramParameter;

public class RealProgramParameter extends ProgramParameter{

    public RealProgramParameter(String name, double value) {
        super(name);
        this.value = value;
        programParameterType = ProgramParameterType.REAL;
    }

    public void setValue(double value){
        this.value = value;
    }
}
