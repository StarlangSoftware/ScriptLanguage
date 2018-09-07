package ScriptLanguage.ProgramParameter;

public class OnOffProgramParameter extends ProgramParameter{

    public OnOffProgramParameter(String name, OnOffType value) {
        super(name);
        programParameterType = ProgramParameterType.ON_OFF;
        this.value = value;
    }

    public void setValue(OnOffType value){
        this.value = value;
    }
}
