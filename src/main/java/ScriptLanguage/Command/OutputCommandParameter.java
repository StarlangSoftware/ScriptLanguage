package ScriptLanguage.Command;

public class OutputCommandParameter extends CommandParameter{
    private String name;

    public OutputCommandParameter(String name, String info, String commandParameterType) {
        super(info, commandParameterType);
        this.name = name;
    }
}
