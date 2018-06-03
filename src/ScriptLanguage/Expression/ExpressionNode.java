package ScriptLanguage.Expression;

import ScriptLanguage.Exception.ContinuousValueExpectedException;
import ScriptLanguage.Variable.*;

public class ExpressionNode {
    private NodeType nodeType = NodeType.STRING;
    private Object value = null;

    public ExpressionNode(String name){
        if (name.equals("+") || name.equals("-")){
            value = name;
            nodeType = NodeType.OPERATOR;
        } else {
            try{
                value = Integer.parseInt(name);
                nodeType = NodeType.INTEGER;
            } catch (NumberFormatException n1){
                try{
                    value = Double.parseDouble(name);
                    nodeType = NodeType.REAL;
                } catch (NumberFormatException n2){
                    value = name;
                    nodeType = NodeType.STRING;
                }
            }
        }
    }

    public ExpressionNode(double value, NodeType nodeType){
        if (nodeType.equals(NodeType.INTEGER)){
            this.value = (int) value;
        } else {
            this.value = value;
        }
        this.nodeType = nodeType;
    }

    public ExpressionNode(char ch, NodeType nodeType){
        this.nodeType = nodeType;
        switch (ch){
            case '+':
                value = OperatorType.PLUS;
                break;
            case '-':
                value = OperatorType.MINUS;
                break;
            case '*':
                value = OperatorType.MULTIPLY;
                break;
            case '/':
                value = OperatorType.DIVIDE;
                break;
            case '^':
                value = OperatorType.POWER;
                break;
            case '@':
                value = OperatorType.MODULUS;
                break;
            case '(':
                value = SeparatorType.OPENING_PARENTHESIS;
                break;
            case ')':
                value = SeparatorType.CLOSING_PARENTHESIS;
                break;
            case '[':
                value = SeparatorType.OPENING_BRACKETS;
                break;
            case ']':
                value = SeparatorType.CLOSING_BRACKETS;
                break;
            case ',':
                value = SeparatorType.COMMA;
                break;
        }
    }

    public ExpressionNode(Variable variable){
        switch (variable.getVariableType()){
            case INTEGER:
                value = ((IntegerVariable) variable).getValue();
                nodeType = NodeType.INTEGER;
                break;
            case REAL:
                value = ((RealVariable) variable).getValue();
                nodeType = NodeType.REAL;
                break;
            case STRING:
            case FILE:
                value = ((StringVariable) variable).getValue();
                nodeType = NodeType.STRING;
                break;
            case ARRAY:
                nodeType = NodeType.ARRAY;
                value = variable;
                break;
        }
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public Object getValue(){
        return value;
    }

    public double getContinuousValue() throws ContinuousValueExpectedException {
        switch (nodeType){
            case INTEGER:
                return (Integer) value;
            case REAL:
                return (Double) value;
        }
        throw new ContinuousValueExpectedException(nodeType);
    }

}
