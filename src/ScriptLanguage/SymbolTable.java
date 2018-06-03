package ScriptLanguage;

import ScriptLanguage.Exception.*;
import ScriptLanguage.Expression.ExpressionList;
import ScriptLanguage.Variable.*;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private HashMap<String, Variable> variables;

    public SymbolTable(boolean addDefaultVariables){
        variables = new HashMap<>();
        if (addDefaultVariables){
            addDefaultVariables();
        }
    }

    public void addVariable(Variable variable){
        variables.put(variable.getName(), variable);
    }

    public void addVariable(VariableType variableType, String variableName, int lineNumber) throws ArrayDefinitionException {
        if (isArrayVariable(variableName)){
            String arrayVariableName = getArrayVariableName(variableName);
            ArrayList<Integer> arraySize = getArraySize(variableName, lineNumber);
            if (arraySize.size() > 1){
                addVariable(new ArrayVariable(arrayVariableName, variableType, arraySize));
            } else {
                addVariable(new ArrayVariable(arrayVariableName, variableType, arraySize.get(0)));
            }
        } else {
            switch (variableType){
                case INTEGER:
                    addVariable(new IntegerVariable(variableName));
                    break;
                case REAL:
                    addVariable(new RealVariable(variableName));
                    break;
                case FILE:
                    addVariable(new FileVariable(variableName));
                    break;
                case STRING:
                    addVariable(new StringVariable(variableName));
                    break;
            }
        }
    }

    private String getArrayVariableName(String variableName){
        return variableName.substring(0, variableName.indexOf("["));
    }

    public Object getValue(String variableName) throws ArrayDefinitionException {
        Variable variable = getVariable(variableName, false);
        switch (variable.getVariableType()){
            case INTEGER:
                return ((IntegerVariable) variable).getValue();
            case REAL:
                return ((RealVariable) variable).getValue();
            case STRING:
            case FILE:
                return ((StringVariable) variable).getValue();
            case ARRAY:
                ArrayList<Integer> sizeList = getArraySize(variableName, 0);
                return ((ArrayVariable)variable).getValue(sizeList);
        }
        return null;
    }

    public boolean isArrayVariable(String variableName){
        return variableName.contains("[");
    }

    public Variable getVariable(String variableName, boolean returnArrayItem){
        if (isArrayVariable(variableName)){
            if (returnArrayItem){
                try{
                    ArrayList<Integer> sizeList = getArraySize(variableName, 0);
                    return ((ArrayVariable)getVariable(variableName, false)).getVariable(sizeList);
                } catch (ArrayDefinitionException e) {
                    e.printStackTrace();
                }
            } else {
                variableName = getArrayVariableName(variableName);
                return variables.get(variableName);
            }
        }
        return variables.get(variableName);
    }

    public boolean variableExists(String variableName){
        if (isArrayVariable(variableName)){
            variableName = getArrayVariableName(variableName);
        }
        return variables.containsKey(variableName);
    }

    public boolean checkParameterType(String variableName, VariableType variableType){
        Variable variable = getVariable(variableName, false);
        if (variable != null){
            if (variable.getVariableType().equals(VariableType.ARRAY)){
                return variableType.equals(((ArrayVariable)variable).getElementType());
            } else {
                return variableType.equals(variable.getVariableType());
            }
        }
        return false;
    }

    public Variable checkVariable(String variableName, boolean returnArrayItem) throws VariableNotExistException {
        Variable variable = getVariable(variableName, returnArrayItem);
        if (variable == null){
            throw new VariableNotExistException(variableName);
        }
        return variable;
    }

    public Variable checkStringVariable(String variableName) throws VariableNotExistException, VariableTypeNotMatchException {
        Variable variable = checkVariable(variableName, false);
        if (!(variable instanceof StringVariable)){
            throw new VariableTypeNotMatchException(variableName, "String");
        }
        return variable;
    }

    public Variable checkFileVariable(String variableName) throws VariableNotExistException, VariableTypeNotMatchException {
        Variable variable = checkVariable(variableName, false);
        if (!(variable instanceof FileVariable)){
            throw new VariableTypeNotMatchException(variableName, "File");
        }
        return variable;
    }

    public Variable checkArrayVariable(String variableName) throws VariableNotExistException, VariableTypeNotMatchException {
        Variable variable = checkVariable(variableName, false);
        if (!(variable instanceof ArrayVariable)){
            throw new VariableTypeNotMatchException(variableName, "Array");
        }
        return variable;
    }

    public Object getTokenValue(String token) throws ArrayDefinitionException {
        if (variableExists(token)){
            return getValue(token);
        } else try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e1) {
            try {
                return Double.parseDouble(token);
            } catch (NumberFormatException e2) {
                return token;
            }
        }
    }

    public void addVariables(VariableType variableType, ArrayList<String> variableList, int lineNumber) throws VariableAlreadyDefinedException, ArrayDefinitionException {
        for (String variableName : variableList){
            if (variableExists(variableName)) {
                throw new VariableAlreadyDefinedException(variableName, lineNumber);
            }
            addVariable(variableType, variableName, lineNumber);
        }
    }

    private ArrayList<Integer> getArraySize(String definitionText, int lineNumber) throws ArrayDefinitionException {
        ArrayList<Integer> sizeList = new ArrayList<>();
        if (!definitionText.contains("[") || !definitionText.contains("]")){
            throw new ArrayDefinitionException(definitionText, lineNumber);
        }
        String sizeString = definitionText.substring(definitionText.indexOf('[') + 1, definitionText.indexOf(']'));
        String[] tokens = sizeString.split(",");
        for (String token : tokens){
            sizeList.add(Integer.parseInt(token));
        }
        return sizeList;
    }

    private void addDefaultVariable(String prefix, VariableType variableType, int count){
        for (int i = 1; i <= count; i++){
            Variable variable;
            if (variableType.equals(VariableType.STRING)){
                variable = new StringVariable(prefix + i);
            } else {
                variable = new RealVariable(prefix + i);
            }
            variable.setDefault();
            addVariable(variable);
        }
    }

    private void addDefaultArrayVariable(String prefix, VariableType elementType, int count, int elementCount){
        for (int i = 1; i <= count; i++){
            Variable variable = new ArrayVariable(prefix + i, elementType, elementCount);
            variable.setDefault();
            addVariable(variable);
        }
    }

    public void setDefaultStringVariable(String variableName, String value){
        StringVariable variable = (StringVariable) getVariable(variableName, false);
        variable.setValue(value);
    }

    public void setDefaultVariable(String variableName, double value){
        RealVariable variable = (RealVariable) getVariable(variableName, false);
        variable.setValue(value);
    }

    public void setDefaultArrayVariable(String variableName, String[] values){
        ArrayVariable variable = (ArrayVariable) getVariable(variableName, false);
        variable.setValue(values);
    }

    public void addDefaultVariables(){
        addDefaultVariable("out", VariableType.REAL, 10);
        addDefaultVariable("sout", VariableType.STRING, 10);
        addDefaultArrayVariable("aout", VariableType.STRING, 10, 100);
    }

    public void print(boolean printDefault){
        for (Variable variable : variables.values()){
            if (!variable.isDefault() || printDefault){
                System.out.println(variable);
            }
        }
    }

}
