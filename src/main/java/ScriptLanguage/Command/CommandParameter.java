package ScriptLanguage.Command;

public class CommandParameter {
    protected String info;
    protected CommandParameterType commandParameterType;

    public CommandParameter(String info, String commandParameterType){
        this.info = info;
        switch (commandParameterType){
            case "stringval":
                this.commandParameterType = CommandParameterType.STRING_VALUE;
                break;
            case "realval":
                this.commandParameterType = CommandParameterType.REAL_VALUE;
                break;
            case "integerval":
                this.commandParameterType = CommandParameterType.INTEGER_VALUE;
                break;
            case "stringvaln":
                this.commandParameterType = CommandParameterType.STRING_ARRAY_VALUE;
                break;
            case "integervaln":
                this.commandParameterType = CommandParameterType.INTEGER_ARRAY_VALUE;
                break;
            case "stringvar":
                this.commandParameterType = CommandParameterType.STRING_VARIABLE;
                break;
            case "realvar":
                this.commandParameterType = CommandParameterType.REAL_VARIABLE;
                break;
            case "integervar":
                this.commandParameterType = CommandParameterType.INTEGER_VARIABLE;
                break;
            case "continuousvar":
                this.commandParameterType = CommandParameterType.CONTINUOUS_VARIABLE;
                break;
            case "filevar":
                this.commandParameterType = CommandParameterType.FILE_VARIABLE;
                break;
            case "arrayvar":
                this.commandParameterType = CommandParameterType.ARRAY_VARIABLE;
                break;
            case "anyvar":
                this.commandParameterType = CommandParameterType.ANY_VARIABLE;
                break;
            case "onoff":
                this.commandParameterType = CommandParameterType.ON_OFF_VALUE;
                break;
            case "compoperator":
                this.commandParameterType = CommandParameterType.COMPARISON_OPERATOR;
                break;
            default:
                System.out.println("Command parameter type " + commandParameterType + " not identified");
                break;
        }
    }
}
