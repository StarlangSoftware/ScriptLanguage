package ScriptLanguage;

import ScriptLanguage.Exception.ArrayDimensionLessException;
import ScriptLanguage.Exception.ContinuousValueExpectedException;
import ScriptLanguage.Exception.ExpressionNotEvaluatedException;
import ScriptLanguage.Exception.OperandExpectedException;
import ScriptLanguage.Expression.ExpressionList;
import ScriptLanguage.Variable.ArrayVariable;
import ScriptLanguage.Variable.Variable;
import ScriptLanguage.Variable.VariableType;

import java.util.ArrayList;

public class TestSourceCode {

    public static void testExpression(){
        ArrayList<Integer> sizeList = new ArrayList<>();
        sizeList.add(10);
        sizeList.add(10);
        sizeList.add(10);
        sizeList.add(10);
        SymbolTable symbolTable = new SymbolTable(false);
        symbolTable.addVariable(new ArrayVariable("a", VariableType.INTEGER, sizeList));
        ExpressionList expressionList = new ExpressionList("(2*5)-12.0*5", symbolTable);
        try {
            Variable variable = expressionList.evaluate();
            System.out.println(variable);
        } catch (ContinuousValueExpectedException | ArrayDimensionLessException | ExpressionNotEvaluatedException | OperandExpectedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        Interpreter interpreter = new Interpreter();
        interpreter.runFromStandardInput();
    }
}
