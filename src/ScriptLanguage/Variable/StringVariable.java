package ScriptLanguage.Variable;

public class StringVariable extends Variable{
    protected String value = null;

    public StringVariable(String name) {
        super(name);
        variableType = VariableType.STRING;
    }

    public StringVariable(String name, String value) {
        super(name);
        variableType = VariableType.STRING;
        this.value = value;
    }

    public String charAt(int index){
        return "" + value.charAt(index);
    }

    public int length(){
        return value.length();
    }

    public int charCount(String characters){
        int count = 0, index = value.indexOf(characters);
        while (index != -1){
            count++;
            if (index  + 1 < value.length()){
                index = value.indexOf(characters, index + 1);
            } else {
                index = -1;
            }
        }
        return count;
    }

    public void setValue(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public boolean equalsValue(String value){
        return this.value.equals(value);
    }

    public String[] split(String separators){
        return value.split(separators);
    }

    public String toString(){
        return name + "->" + value;
    }

}
