package ScriptLanguage.Variable;

public class RealVariable extends Variable{
    private double value = 0.0;

    public RealVariable(String name) {
        super(name);
        variableType = VariableType.REAL;
    }

    public RealVariable(String name, double value) {
        super(name);
        variableType = VariableType.REAL;
        this.value = value;
    }

    public void setValue(double value){
        this.value = value;
    }

    public double getValue(){
        return value;
    }

    public boolean equalsValue(double value){
        return this.value == value;
    }

    public String toString(){
        return name + "->" + value;
    }

}
