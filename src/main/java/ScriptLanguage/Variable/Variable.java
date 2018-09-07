package ScriptLanguage.Variable;

public abstract class Variable {
    protected String name;
    protected VariableType variableType;
    protected boolean isDefault = false;

    public Variable(String name){
        this.name = name;
    }

    public void setDefault(){
        isDefault = true;
    }

    public boolean isDefault(){
        return isDefault;
    }

    public VariableType getVariableType(){
        return variableType;
    }

    public String getName(){
        return name;
    }

}
