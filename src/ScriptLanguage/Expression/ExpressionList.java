package ScriptLanguage.Expression;

import ScriptLanguage.Exception.ArrayDimensionLessException;
import ScriptLanguage.Exception.ContinuousValueExpectedException;
import ScriptLanguage.Exception.ExpressionNotEvaluatedException;
import ScriptLanguage.Exception.OperandExpectedException;
import ScriptLanguage.SymbolTable;
import ScriptLanguage.Variable.*;

import java.util.ArrayList;
import java.util.Stack;

public class ExpressionList {
    private ArrayList<ExpressionNode> list;

    public ExpressionList(){
        list = new ArrayList<>();
    }

    public void addExpressionNode(ExpressionNode expressionNode){
        list.add(expressionNode);
    }

    public ExpressionList(String expression, SymbolTable symbolTable){
        final String SEPARATOR_LIST = "()[],";
        final String OPERATOR_LIST = "+-*/^$@";
        int i = 0;
        String current = "";
        list = new ArrayList<>();
        while (i < expression.length()){
            if (SEPARATOR_LIST.contains("" + expression.charAt(i)) || OPERATOR_LIST.contains("" + expression.charAt(i))){
                if (current.length() != 0){
                    if (symbolTable != null && symbolTable.variableExists(current)){
                        list.add(new ExpressionNode(symbolTable.getVariable(current, false)));
                    } else {
                        list.add(new ExpressionNode(current));
                    }
                    current = "";
                }
                if (SEPARATOR_LIST.contains("" + expression.charAt(i))){
                    list.add(new ExpressionNode(expression.charAt(i), NodeType.SEPARATOR));
                } else {
                    list.add(new ExpressionNode(expression.charAt(i), NodeType.OPERATOR));
                }
            } else {
                current = current + expression.charAt(i);
            }
            i++;
        }
        if (current.length() != 0){
            if (symbolTable != null && symbolTable.variableExists(current)){
                list.add(new ExpressionNode(symbolTable.getVariable(current, false)));
            } else {
                list.add(new ExpressionNode(current));
            }
        }
    }

    private ExpressionNode evaluate(ExpressionNode node1, ExpressionNode node2, OperatorType operatorType) throws ContinuousValueExpectedException {
        NodeType resultType = NodeType.REAL;
        double value1 = node1.getContinuousValue();
        double value2 = node2.getContinuousValue();
        if (node1.getNodeType().equals(NodeType.INTEGER) && node2.getNodeType().equals(NodeType.INTEGER)){
            resultType = NodeType.INTEGER;
        }
        switch (operatorType){
            case PLUS:
                return new ExpressionNode(value1 + value2, resultType);
            case MINUS:
                return new ExpressionNode(value1 - value2, resultType);
            case MULTIPLY:
                return new ExpressionNode(value1 * value2, resultType);
            case DIVIDE:
                return new ExpressionNode(value1 / value2, resultType);
            case POWER:
                return new ExpressionNode(Math.pow(value1, value2), resultType);
            case MODULUS:
                return new ExpressionNode(value1 - value2 * ((int)(value1 / value2)), resultType);
        }
        return null;
    }

    private ExpressionList infixToPostfix(){
        ExpressionNode current;
        Stack<ExpressionNode> stack = new Stack<>();
        ExpressionList postfix = new ExpressionList();
        for (ExpressionNode expressionNode : list) {
            current = expressionNode;
            if (current.getNodeType().equals(NodeType.SEPARATOR)) {
                SeparatorType separatorType = (SeparatorType) current.getValue();
                switch (separatorType) {
                    case COMMA:
                        while (!stack.empty()) {
                            if (stack.peek().getValue() instanceof SeparatorType && stack.peek().getValue().equals(SeparatorType.OPENING_BRACKETS)) {
                                break;
                            }
                            postfix.addExpressionNode(stack.pop());
                        }
                        break;
                    case OPENING_PARENTHESIS:
                    case OPENING_BRACKETS:
                        stack.push(current);
                        break;
                    case CLOSING_BRACKETS:
                        while (!stack.empty()) {
                            if (stack.peek().getValue() instanceof SeparatorType && stack.peek().getValue().equals(SeparatorType.OPENING_BRACKETS)) {
                                break;
                            }
                            postfix.addExpressionNode(stack.pop());
                        }
                        stack.pop();
                        postfix.addExpressionNode(stack.pop());
                        break;
                    case CLOSING_PARENTHESIS:
                        while (!stack.empty()) {
                            if (stack.peek().getValue() instanceof SeparatorType && stack.peek().getValue().equals(SeparatorType.OPENING_PARENTHESIS)) {
                                break;
                            }
                            postfix.addExpressionNode(stack.pop());
                        }
                        stack.pop();
                        break;
                }
            } else {
                if (current.getNodeType().equals(NodeType.OPERATOR)) {
                    OperatorType stackOperatorType, currentOperatorType;
                    currentOperatorType = (OperatorType) current.getValue();
                    if (!stack.empty() && stack.peek().getValue() instanceof OperatorType) {
                        stackOperatorType = (OperatorType) stack.peek().getValue();
                    } else {
                        stackOperatorType = null;
                    }
                    while (stackOperatorType != null && higherPrecedence(stackOperatorType, currentOperatorType)) {
                        postfix.addExpressionNode(stack.pop());
                        if (!stack.empty() && stack.peek().getValue() instanceof OperatorType) {
                            stackOperatorType = (OperatorType) stack.peek().getValue();
                        } else {
                            stackOperatorType = null;
                        }
                    }
                    stack.push(current);
                } else {
                    if (current.getNodeType().equals(NodeType.ARRAY)) {
                        stack.push(current);
                    } else {
                        postfix.addExpressionNode(current);
                    }
                }
            }
        }
        while (!stack.isEmpty()){
            postfix.addExpressionNode(stack.pop());
        }
        return postfix;
    }

    public Variable evaluate() throws ExpressionNotEvaluatedException, ContinuousValueExpectedException, ArrayDimensionLessException, OperandExpectedException {
        return infixToPostfix().evaluateExpression();
    }

    private Variable evaluateExpression() throws ArrayDimensionLessException, ExpressionNotEvaluatedException, OperandExpectedException, ContinuousValueExpectedException {
        int index;
        ExpressionNode operand1, operand2, result;
        Stack<ExpressionNode> stack = new Stack<>();
        for (ExpressionNode expressionNode : list){
            if (expressionNode.getNodeType().equals(NodeType.OPERATOR)){
                if (stack.empty()){
                    throw new OperandExpectedException();
                }
                operand2 = stack.pop();
                if (stack.empty()){
                    throw new OperandExpectedException();
                }
                operand1 = stack.pop();
                result = evaluate(operand1, operand2, (OperatorType) expressionNode.getValue());
                stack.push(result);
            } else {
                if (expressionNode.getNodeType().equals(NodeType.ARRAY)){
                    Stack<Integer> indexStack = new Stack<>();
                    Variable variable = (Variable) expressionNode.getValue();
                    String variableName = variable.getName();
                    while (variable.getVariableType().equals(VariableType.ARRAY)){
                        if (stack.empty()){
                            throw new ArrayDimensionLessException(variableName);
                        }
                        operand1 = stack.pop();
                        index = (int) operand1.getValue();
                        indexStack.push(index);
                        variable = ((ArrayVariable) variable).getValue(1);
                    }
                    variable = (Variable) expressionNode.getValue();
                    while (variable.getVariableType().equals(VariableType.ARRAY)){
                        index = indexStack.pop();
                        variable = ((ArrayVariable) variable).getValue(index);
                    }
                    stack.push(new ExpressionNode(variable));
                } else {
                    stack.push(expressionNode);
                }
            }
        }
        if (stack.size() > 1){
            throw new ExpressionNotEvaluatedException();
        }
        result = stack.pop();
        if (result.getNodeType().equals(NodeType.INTEGER)){
            return new IntegerVariable("", (Integer) result.getValue());
        } else {
            if (result.getNodeType().equals(NodeType.REAL)){
                return new RealVariable("", (Double) result.getValue());
            }
        }
        return null;
    }

    private boolean higherPrecedence(OperatorType operatorType1, OperatorType operatorType2){
        if (operatorType1.ordinal() >= operatorType2.ordinal()){
            return true;
        }
        if (operatorType1.equals(OperatorType.PLUS) && operatorType2.equals(OperatorType.MINUS)){
            return true;
        }
        if (operatorType1.equals(OperatorType.MULTIPLY) && operatorType2.equals(OperatorType.DIVIDE)){
            return true;
        }
        return false;
    }
}
