package ScriptLanguage;

import ScriptLanguage.Command.Command;
import ScriptLanguage.Command.ComparisonType;
import ScriptLanguage.Command.ConstructorType;
import ScriptLanguage.Exception.*;
import ScriptLanguage.Variable.VariableType;

import java.util.ArrayList;
import java.util.EnumMap;

public class SourceStatement {
    private Command command;
    private String commandText;
    private VariableType variableDeclarationType = null;
    private ConstructorType constructor = null;
    private ComparisonType comparisonType = null;
    private ArrayList<String> parameters;

    public SourceStatement(String text, ScriptLanguage scriptLanguage) throws CommandNotDefinedException {
        String[] tokens = text.split(" |\\t");
        commandText = changeSpecialCharacters(tokens[0]);
        if (!scriptLanguage.containsCommand(commandText)){
            throw new CommandNotDefinedException(commandText, 0);
        }
        command = scriptLanguage.getCommand(commandText);
        for (ConstructorType constructorType : ConstructorType.values()){
            if (commandText.toUpperCase().equals(constructorType.toString())){
                constructor = constructorType;
                break;
            }
        }
        for (VariableType variableType : VariableType.values()){
            if (commandText.toUpperCase().equals(variableType.toString())){
                variableDeclarationType = variableType;
                break;
            }
        }
        parameters = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++){
            parameters.add(changeSpecialCharacters(tokens[i]));
        }
        if (constructor != null && (constructor.equals(ConstructorType.IF) || constructor.equals(ConstructorType.WHILE))){
            comparisonType = getComparisonType(parameters.get(0));
            parameters.remove(0);
        }
    }

    private ComparisonType getComparisonType(String text){
        switch (text){
            case "<":
                return ComparisonType.LESS;
            case "<=":
                return ComparisonType.LESS_THAN_OR_EQUAL;
            case ">":
                return ComparisonType.LARGER;
            case ">=":
                return ComparisonType.LARGER_THAN_OR_EQUAL;
            case "=":
            case "==":
                return ComparisonType.EQUAL;
            case "<>":
            case "!=":
                return ComparisonType.NOT_EQUAL;
            default:
                return ComparisonType.NONE;
        }
    }

    public VariableType getVariableDeclarationType(){
        return variableDeclarationType;
    }

    private String changeSpecialCharacters(String text){
        return text.replace('_', ' ').replace('#', '_');
    }

    public ConstructorType getConstructor(){
        return constructor;
    }

    public ComparisonType getComparisonType(){
        return comparisonType;
    }

    public Command getCommand(){
        return command;
    }

    public String getCommandText(){
        return commandText;
    }

    public String getParameter(int index){
        return parameters.get(index);
    }

    public int parameterSize(){
        return parameters.size();
    }

    private void checkVariableDeclaration(SymbolTable symbolTable, int lineNumber) throws VariableAlreadyDefinedException, ArrayDefinitionException {
        VariableType variableDeclarationType = getVariableDeclarationType();
        if (variableDeclarationType != null){
            for (int j = 0; j < parameterSize(); j++){
                String variableName = getParameter(j);
                if (symbolTable.variableExists(variableName)){
                    throw new VariableAlreadyDefinedException(variableName, lineNumber);
                } else {
                    symbolTable.addVariable(variableDeclarationType, variableName, lineNumber);
                }
            }
        }
    }

    public void compile(EnumMap<ConstructorType, Integer> counts, SymbolTable symbolTable, int lineNumber) throws LessParametersException, ExtraParametersException, WrongParameterException, VariableNotExistException, NonMatchConstructorException, SwitchInsideSwitchException, CaseOutsideOfSwitchException, VariableAlreadyDefinedException, ArrayDefinitionException {
        Command command = getCommand();
        if (parameterSize() < command.numberOfInputParameters()){
            throw new LessParametersException(getCommandText(), lineNumber + 1);
        }
        if (parameterSize() > command.numberOfMaximumInputParameters()){
            throw new ExtraParametersException(getCommandText(), lineNumber + 1);
        }
        command.checkParameters(this, symbolTable);
        checkVariableDeclaration(symbolTable, lineNumber);
        ConstructorType constructor = getConstructor();
        if (counts!= null && constructor != null){
            counts.put(constructor, counts.get(constructor) + 1);
            switch (constructor){
                case ELSE:
                    if (counts.get(ConstructorType.ELSE) > counts.get(ConstructorType.IF)){
                        throw new NonMatchConstructorException("Else", "If", lineNumber + 1);
                    }
                    break;
                case ENDIF:
                    if (counts.get(ConstructorType.ENDIF) > counts.get(ConstructorType.IF)){
                        throw new NonMatchConstructorException("EndIf", "If", lineNumber + 1);
                    }
                    break;
                case SWITCH:
                    if (counts.get(ConstructorType.SWITCH) > counts.get(ConstructorType.ENDSWITCH) + 1){
                        throw new SwitchInsideSwitchException(lineNumber + 1);
                    }
                    break;
                case ENDSWITCH:
                    if (counts.get(ConstructorType.ENDSWITCH) > counts.get(ConstructorType.SWITCH)){
                        throw new NonMatchConstructorException("EndSwitch", "Switch", lineNumber + 1);
                    }
                    break;
                case CASE:
                    if (counts.get(ConstructorType.ENDSWITCH).equals(counts.get(ConstructorType.SWITCH))){
                        throw new CaseOutsideOfSwitchException(lineNumber + 1);
                    }
                    break;
                case ENDFOR:
                    if (counts.get(ConstructorType.ENDFOR) > counts.get(ConstructorType.FOR)){
                        throw new NonMatchConstructorException("EndFor", "For", lineNumber + 1);
                    }
                    break;
                case ENDWHILE:
                    if (counts.get(ConstructorType.ENDWHILE) > counts.get(ConstructorType.WHILE)){
                        throw new NonMatchConstructorException("EndWhile", "While", lineNumber + 1);
                    }
                    break;
            }
        }
    }
}
