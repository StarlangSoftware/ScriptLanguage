package ScriptLanguage.Variable;

import java.util.ArrayList;

public class ArrayVariable extends Variable{
    private ArrayList<Variable> value;
    private VariableType elementType;

    public ArrayVariable(String name, VariableType elementType, int size){
        super(name);
        variableType = VariableType.ARRAY;
        this.elementType = elementType;
        value = new ArrayList<>();
        for (int i = 0; i < size; i++){
            switch (elementType){
                case INTEGER:
                    value.add(new IntegerVariable(name + "[" + (i + 1) + "]"));
                    break;
                case REAL:
                    value.add(new RealVariable(name + "[" + (i + 1) + "]"));
                    break;
                case FILE:
                    value.add(new FileVariable(name + "[" + (i + 1) + "]"));
                    break;
                case STRING:
                    value.add(new StringVariable(name + "[" + (i + 1) + "]"));
                    break;
            }
        }
    }

    public ArrayVariable(String name, VariableType elementType, ArrayList<Integer> sizeList){
        super(name);
        variableType = VariableType.ARRAY;
        this.elementType = VariableType.ARRAY;
        value = new ArrayList<>();
        int size = sizeList.get(0);
        for (int i = 0; i < size; i++){
            if (sizeList.size() > 2){
                sizeList.remove(0);
                value.add(new ArrayVariable(name + "[" + (i + 1) + "]", elementType, sizeList));
                sizeList.add(0, size);
            } else {
                value.add(new ArrayVariable(name + "[" + (i + 1) + "]", elementType, sizeList.get(1)));
            }
        }
    }

    private void setValues(int start, Object... values){
        for (int i = 0; i < values.length; i++){
            switch (elementType){
                case INTEGER:
                    if (values[i] instanceof Integer){
                        ((IntegerVariable)this.value.get(start + i)).setValue((Integer)values[i]);
                    }
                    break;
                case REAL:
                    if (values[i] instanceof Double){
                        ((RealVariable)this.value.get(start + i)).setValue((Double) values[i]);
                    }
                    break;
                case FILE:
                    if (values[i] instanceof String){
                        ((FileVariable)this.value.get(start + i)).setValue((String) values[i]);
                    }
                    break;
                case STRING:
                    if (values[i] instanceof String){
                        ((StringVariable)this.value.get(start + i)).setValue((String) values[i]);
                    }
                    break;
            }
        }
    }

    public void setValue(int start, Object... values){
        setValues(start, values);
    }

    public void setValue(Object[] values){
        setValues(0, values);
    }

    public void setValue(Object value){
        for (Variable item : this.value) {
            switch (elementType) {
                case INTEGER:
                    if (value instanceof Integer) {
                        ((IntegerVariable) item).setValue((Integer) value);
                    }
                    break;
                case REAL:
                    if (value instanceof Double) {
                        ((RealVariable) item).setValue((Double) value);
                    }
                    break;
                case FILE:
                    if (value instanceof String) {
                        ((FileVariable) item).setValue((String) value);
                    }
                    break;
                case STRING:
                    if (value instanceof String) {
                        ((StringVariable) item).setValue((String) value);
                    }
                    break;
                case ARRAY:
                    if (item instanceof ArrayVariable){
                        ((ArrayVariable) item).setValue(value);
                    }
                    break;
            }
        }
    }

    public Variable getValue(int index){
        return value.get(index - 1);
    }

    public Variable getVariable(ArrayList<Integer> sizeList){
        int index = sizeList.get(0) - 1;
        sizeList.remove(0);
        if (sizeList.size() > 0){
            return ((ArrayVariable) value.get(index)).getVariable(sizeList);
        } else {
            return value.get(index);
        }
    }

    public Object getValue(ArrayList<Integer> sizeList){
        int index = sizeList.get(0) - 1;
        sizeList.remove(0);
        if (sizeList.size() > 0){
            return ((ArrayVariable) value.get(index)).getValue(sizeList);
        } else {
            Variable variable = value.get(index);
            switch (variable.getVariableType()){
                case INTEGER:
                    return ((IntegerVariable)variable).getValue();
                case REAL:
                    return ((RealVariable)variable).getValue();
                case STRING:
                case FILE:
                    return ((StringVariable)variable).getValue();
            }
            return null;
        }
    }

    public VariableType getElementType(){
        if (elementType.equals(VariableType.ARRAY)){
            return ((ArrayVariable) value.get(0)).getElementType();
        }
        return elementType;
    }

    public String toString(){
        String result = "";
        for (Variable variable : value){
            result = result + "\t" + variable.toString() + "\n";
        }
        return result;
    }
}
