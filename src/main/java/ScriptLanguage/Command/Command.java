package ScriptLanguage.Command;

import ScriptLanguage.Exception.WrongParameterException;
import ScriptLanguage.SourceStatement;
import ScriptLanguage.SymbolTable;
import ScriptLanguage.Variable.VariableType;

import java.util.ArrayList;

public class Command {
    private String name;
    private String category;
    private ArrayList<InputCommandParameter> inputParameters;
    private ArrayList<OutputCommandParameter> outputParameters;
    private String info;

    public Command(String name, String category, String info){
        this.name = name;
        this.category = category;
        this.info = info;
        inputParameters = new ArrayList<>();
        outputParameters = new ArrayList<>();
    }

    public String getInfo(){
        return info;
    }

    public String getName(){
        return name;
    }

    public void addInputParameter(InputCommandParameter inputCommandParameter){
        inputParameters.add(inputCommandParameter);
    }

    public void addOutputParameter(OutputCommandParameter outputCommandParameter){
        outputParameters.add(outputCommandParameter);
    }

    private void checkForRealNumber(String s, int parameterIndex) throws WrongParameterException {
        try{
            double tmp = Double.parseDouble(s);
        } catch (NumberFormatException e){
            throw new WrongParameterException(parameterIndex);
        }
    }

    private void checkForIntegerNumber(String s, int parameterIndex) throws WrongParameterException {
        try{
            int tmp = Integer.parseInt(s);
        } catch (NumberFormatException e){
            throw new WrongParameterException(parameterIndex);
        }
    }

    public int numberOfMaximumInputParameters(){
        int count = 0;
        for (InputCommandParameter inputCommandParameter : inputParameters){
            switch (inputCommandParameter.commandParameterType){
                case STRING_VALUE:
                case REAL_VALUE:
                case INTEGER_VALUE:
                case ON_OFF_VALUE:
                case STRING_VARIABLE:
                case INTEGER_VARIABLE:
                case REAL_VARIABLE:
                case FILE_VARIABLE:
                case ANY_VARIABLE:
                case ARRAY_VARIABLE:
                case CONTINUOUS_VARIABLE:
                    count++;
                    break;
                case INTEGER_ARRAY_VALUE:
                case STRING_ARRAY_VALUE:
                    return Integer.MAX_VALUE;
            }
        }
        return count;
    }

    public int numberOfInputParameters(){
        return inputParameters.size();
    }

    public void checkParameters(SourceStatement sourceStatement, SymbolTable symbolTable) throws WrongParameterException {
        int i = 0;
        while (i < sourceStatement.parameterSize() && i < inputParameters.size()){
            String parameter = sourceStatement.getParameter(i);
            switch (inputParameters.get(i).commandParameterType){
                case ON_OFF_VALUE:
                    if (!parameter.equalsIgnoreCase("on") && !parameter.equalsIgnoreCase("off")){
                        throw new WrongParameterException(i);
                    }
                    break;
                case REAL_VALUE:
                    checkForRealNumber(parameter, i);
                    break;
                case INTEGER_VALUE:
                    checkForIntegerNumber(parameter, i);
                    break;
                case INTEGER_ARRAY_VALUE:
                    for (int j = i; j < sourceStatement.parameterSize(); j++){
                        parameter = sourceStatement.getParameter(j);
                        checkForIntegerNumber(parameter, i);
                    }
                    break;
                case STRING_VARIABLE:
                    if (!symbolTable.checkParameterType(parameter, VariableType.STRING)){
                        throw new WrongParameterException(i);
                    }
                    break;
                case REAL_VARIABLE:
                    if (!symbolTable.checkParameterType(parameter, VariableType.REAL)){
                        throw new WrongParameterException(i);
                    }
                    break;
                case INTEGER_VARIABLE:
                    if (!symbolTable.checkParameterType(parameter, VariableType.INTEGER)){
                        throw new WrongParameterException(i);
                    }
                    break;
                case FILE_VARIABLE:
                    if (!symbolTable.checkParameterType(parameter, VariableType.FILE)){
                        throw new WrongParameterException(i);
                    }
                    break;
                case CONTINUOUS_VARIABLE:
                    if (!symbolTable.checkParameterType(parameter, VariableType.REAL)
                            && !symbolTable.checkParameterType(parameter, VariableType.INTEGER)){
                        throw new WrongParameterException(i);
                    }
                    break;
                case ANY_VARIABLE:
                    if (!symbolTable.variableExists(parameter)){
                        throw new WrongParameterException(i);
                    }
                    break;
                case COMPARISON_OPERATOR:
                    if (!parameter.equals("<") && !parameter.equals("<=")
                            && !parameter.equals(">") && !parameter.equals(">=")
                            && !parameter.equals("=") && !parameter.equals("==")
                            && !parameter.equals("<>") && !parameter.equals("!=")){
                        throw new WrongParameterException(i);
                    }
                    break;
            }
            i++;
        }
    }

    public String toString(){
        String result = name;
        for (int i = 0; i < inputParameters.size(); i++){
            result = result + " <" + inputParameters.get(i).commandParameterType + ">";
        }
        return result + ": " + info;
    }

}
