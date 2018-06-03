package ScriptLanguage.Exception;

import ScriptLanguage.Expression.NodeType;

public class ContinuousValueExpectedException extends RunTimeException{
    private NodeType nodeType;

    public ContinuousValueExpectedException(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String toString(){
        return "Continuous value expected but " + nodeType + " found.";
    }
}
