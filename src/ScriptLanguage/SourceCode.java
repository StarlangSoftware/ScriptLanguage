package ScriptLanguage;

import ScriptLanguage.Command.ComparisonType;
import ScriptLanguage.Command.ConstructorType;
import ScriptLanguage.Exception.*;
import ScriptLanguage.Variable.ForVariable;
import ScriptLanguage.Variable.IntegerVariable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Scanner;

public class SourceCode {
    private ArrayList<SourceStatement> lines;
    private int instructionPointer = 0;

    public SourceCode(String fileName, ScriptLanguage scriptLanguage) throws CommandNotDefinedException {
        lines = new ArrayList<>();
        try {
            Scanner s = new Scanner(new File(fileName));
            while (s.hasNext()){
                String line = s.nextLine().trim();
                if (!line.startsWith("'")){
                    lines.add(new SourceStatement(line, scriptLanguage));
                }
            }
            s.close();
        } catch (FileNotFoundException e) {
        }
    }

    public void goToEndConstructor(ConstructorType startConstructor, ConstructorType endConstructor){
        int i, howMany = 1;
        for (i = instructionPointer + 1; i < lines.size(); i++){
            ConstructorType constructor = lines.get(i).getConstructor();
            if (constructor != null){
                if (constructor.equals(startConstructor)){
                    howMany++;
                } else {
                    if (constructor.equals(endConstructor)){
                        howMany--;
                        if (howMany == 0){
                            break;
                        }
                    }
                }
            }
        }
        instructionPointer = i;
    }

    public boolean checkConditionalStatement(SymbolTable symbolTable, ComparisonType comparisonType, String parameter1, String parameter2) {
        try{
            Object tokenValue1 = symbolTable.getTokenValue(parameter1);
            Object tokenValue2 = symbolTable.getTokenValue(parameter2);
            if (tokenValue1 instanceof String && tokenValue2 instanceof String){
                String value1 = (String) tokenValue1;
                String value2 = (String) tokenValue2;
                switch (comparisonType){
                    case LESS:
                        return value1.compareTo(value2) < 0;
                    case LESS_THAN_OR_EQUAL:
                        return value1.compareTo(value2) <= 0;
                    case LARGER:
                        return value1.compareTo(value2) > 0;
                    case LARGER_THAN_OR_EQUAL:
                        return value1.compareTo(value2) >= 0;
                    case EQUAL:
                        return value1.compareTo(value2) == 0;
                    case NOT_EQUAL:
                        return value1.compareTo(value2) != 0;
                }
            } else {
                double value1 = 0, value2 = 0;
                if (tokenValue1 instanceof Double){
                    value1 = (Double) tokenValue1;
                } else {
                    if (tokenValue1 instanceof Integer){
                        value1 = (Integer) tokenValue1;
                    }
                }
                if (tokenValue2 instanceof Double){
                    value2 = (Double) tokenValue2;
                } else {
                    if (tokenValue2 instanceof Integer){
                        value2 = (Integer) tokenValue2;
                    }
                }
                switch (comparisonType){
                    case LESS:
                        return value1 < value2;
                    case LESS_THAN_OR_EQUAL:
                        return value1 <= value2;
                    case LARGER:
                        return value1 > value2;
                    case LARGER_THAN_OR_EQUAL:
                        return value1 >= value2;
                    case EQUAL:
                        return value1 == value2;
                    case NOT_EQUAL:
                        return value1 != value2;
                }
            }
        } catch (ArrayDefinitionException e) {
            return false;
        }
        return false;
    }


    public void compile() throws CompileException {
        EnumMap<ConstructorType, Integer> counts = new EnumMap<>(ConstructorType.class);
        for (ConstructorType constructor : ConstructorType.values()){
            counts.put(constructor, 0);
        }
        SymbolTable symbolTable = new SymbolTable(true);
        for (int i = 0; i < lines.size(); i++){
            lines.get(i).compile(counts, symbolTable, i);
        }
        if (counts.get(ConstructorType.IF) > counts.get(ConstructorType.ENDIF)){
            throw new NonMatchConstructorException("If", "EndIf", lines.size());
        }
        if (counts.get(ConstructorType.SWITCH) > counts.get(ConstructorType.ENDSWITCH)){
            throw new NonMatchConstructorException("Switch", "EndSwitch", lines.size());
        }
        if (counts.get(ConstructorType.FOR) > counts.get(ConstructorType.ENDFOR)){
            throw new NonMatchConstructorException("For", "EndFor", lines.size());
        }
        if (counts.get(ConstructorType.WHILE) > counts.get(ConstructorType.ENDWHILE)){
            throw new NonMatchConstructorException("While", "EndWhile", lines.size());
        }
    }

    public void run(){
        ForVariable forVariable;
        IntegerVariable switchVariable = null;
        boolean inCorrectCase = false;
        SourceStatement statement;
        SymbolTable symbolTable;
        Interpreter interpreter = new Interpreter();
        symbolTable = interpreter.getSymbolTable();
        instructionPointer = 0;
        while (instructionPointer < lines.size()){
            statement = lines.get(instructionPointer);
            String commandText = statement.getCommand().getName();
            if (switchVariable != null && !inCorrectCase && !commandText.equals("case") && !commandText.equals("endswitch")){
                instructionPointer++;
                continue;
            }
            switch (commandText){
                case "exit":
                    return;
                case "for":
                    forVariable = new ForVariable(statement.getParameter(0), Integer.parseInt(statement.getParameter(1)), Integer.parseInt(statement.getParameter(2)), instructionPointer);
                    symbolTable.addVariable(forVariable);
                    if (!forVariable.doesContinue()){
                        goToEndConstructor(ConstructorType.FOR, ConstructorType.ENDFOR);
                    }
                    break;
                case "endfor":
                    forVariable = (ForVariable) symbolTable.getVariable(statement.getParameter(0), false);
                    forVariable.increment();
                    if (forVariable.doesContinue()){
                        instructionPointer = forVariable.getInstructionPointer();
                    }
                    break;
                case "switch":
                    switchVariable = (IntegerVariable) symbolTable.getVariable(statement.getParameter(0), true);
                    inCorrectCase = true;
                    break;
                case "endswitch":
                    switchVariable = null;
                    inCorrectCase = false;
                    break;
                case "case":
                    if (switchVariable != null && switchVariable.getValue() == Integer.parseInt(statement.getParameter(0))){
                        inCorrectCase = true;
                    } else {
                        inCorrectCase = false;
                    }
                    break;
                case "if":
                    if (!checkConditionalStatement(symbolTable, statement.getComparisonType(), statement.getParameter(0), statement.getParameter(1))){
                        int howMany = 1, i;
                        for (i = instructionPointer + 1; i < lines.size(); i++){
                            if (lines.get(i).getCommand().getName().equals("if")){
                                howMany++;
                            } else {
                                if (lines.get(i).getCommand().getName().equals("endif")){
                                    howMany--;
                                    if (howMany == 0){
                                        break;
                                    }
                                } else {
                                    if (lines.get(i).getCommand().getName().equals("else") && howMany == 1){
                                        break;
                                    }
                                }
                            }
                        }
                        instructionPointer = i;
                    }
                    break;
                case "endif":
                    break;
                case "else":
                    goToEndConstructor(ConstructorType.IF, ConstructorType.ENDIF);
                    break;
                case "while":
                    if (!checkConditionalStatement(symbolTable, statement.getComparisonType(), statement.getParameter(0), statement.getParameter(1))){
                        goToEndConstructor(ConstructorType.WHILE, ConstructorType.ENDWHILE);
                    }
                    break;
                case "endwhile":
                    int howMany = 1, i;
                    for (i = instructionPointer - 1; i >= 0; i--){
                        if (lines.get(i).getCommand().getName().equals("endwhile")){
                            howMany++;
                        } else {
                            if (lines.get(i).getCommand().getName().equals("while")){
                                howMany--;
                                if (howMany == 0){
                                    break;
                                }
                            }
                        }
                    }
                    instructionPointer = i - 1;
                    break;
                default:
                    interpreter.executeStatement(statement);
                    break;
            }
            instructionPointer++;
        }
    }

}
