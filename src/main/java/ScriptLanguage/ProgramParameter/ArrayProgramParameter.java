package ScriptLanguage.ProgramParameter;

public class ArrayProgramParameter extends ProgramParameter{
    private ProgramParameterType elementType;

    public ArrayProgramParameter(String name, ProgramParameterType elementType, int size, Object elementValue) {
        super(name);
        this.elementType = elementType;
        programParameterType = ProgramParameterType.ARRAY;
        value = new Object[size];
        for (int i = 0; i < size; i++){
            ((Object[])value)[i] = elementValue;
        }
    }

    public void setValue(Object[] value){
        this.value = value;
    }
}
