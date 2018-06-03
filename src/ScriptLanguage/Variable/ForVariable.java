package ScriptLanguage.Variable;

public class ForVariable extends IntegerVariable{
    private int end;
    private int instructionPointer;

    public ForVariable(String name, int value, int end, int instructionPointer) {
        super(name);
        this.value = value;
        this.end = end;
        this.instructionPointer = instructionPointer;
    }

    public void increment(){
        value++;
    }

    public boolean doesContinue(){
        return value <= end;
    }

    public int getInstructionPointer(){
        return instructionPointer;
    }

}
