package ScriptLanguage.Variable;

public class IntegerVariable extends Variable{
    protected int value = 0;

    public IntegerVariable(String name) {
        super(name);
        variableType = VariableType.INTEGER;
    }

    public IntegerVariable(String name, int value) {
        super(name);
        variableType = VariableType.INTEGER;
        this.value = value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public boolean equalsValue(int value){
        return this.value == value;
    }

    public String toString(){
        return name + "->" + value;
    }

}
