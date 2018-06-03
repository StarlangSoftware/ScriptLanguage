package ScriptLanguage.Variable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class FileVariable extends StringVariable{
    private Scanner input;
    private PrintWriter output;
    private FileType fileType;

    public FileVariable(String name) {
        super(name);
        variableType = VariableType.FILE;
    }

    public String readString(){
        return input.nextLine();
    }

    public void writeString(String lineToBeWritten){
        output.println(lineToBeWritten);
    }

    public void openFile(FileType fileType) throws FileNotFoundException {
        this.fileType = fileType;
        switch (fileType){
            case READ:
                input = new Scanner(new File(value));
                break;
            case WRITE:
                output = new PrintWriter(new File(value));
                break;
        }
    }

    public void closeFile(){
        switch (fileType){
            case READ:
                input.close();
                break;
            case WRITE:
                output.close();
                break;
        }
    }

    public void readArray(ArrayVariable outputVariable, int size){
        switch (outputVariable.getElementType()){
            case INTEGER:
                for (int i = 0; i < size; i++){
                    outputVariable.setValue(i, input.nextInt());
                }
                break;
            case REAL:
                for (int i = 0; i < size; i++){
                    outputVariable.setValue(i, input.nextFloat());
                }
                break;
            case STRING:
                for (int i = 0; i < size; i++){
                    outputVariable.setValue(i, input.next());
                }
                break;
        }
    }

}
