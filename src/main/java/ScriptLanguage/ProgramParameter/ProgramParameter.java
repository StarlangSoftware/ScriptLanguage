package ScriptLanguage.ProgramParameter;

public abstract class ProgramParameter {
    protected String name;
    protected ProgramParameterType programParameterType;
    protected Object value;

    public ProgramParameter(String name){
        this.name = name;
    }

    public Object getValue(){
        return value;
    }

    public String getName(){
        return name;
    }

}
