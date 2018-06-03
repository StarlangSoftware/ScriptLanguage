package ScriptLanguage;

import ScriptLanguage.Command.Command;
import ScriptLanguage.Command.InputCommandParameter;
import ScriptLanguage.Command.OutputCommandParameter;
import ScriptLanguage.Exception.CommandNotDefinedException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

public class ScriptLanguage {
    private HashMap<String, Command> commandList;

    public ScriptLanguage(String fileName){
        Node categoryNode, rootNode, commandNode, parametersNode, inputParametersNode, outputParametersNode;
        DOMParser parser = new DOMParser();
        Document doc;
        Command currentCommand;
        String currentCategory;
        try {
            parser.parse(fileName);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        commandList = new HashMap<>();
        doc = parser.getDocument();
        rootNode = doc.getFirstChild();
        categoryNode = rootNode.getFirstChild();
        while (categoryNode != null){
            if (categoryNode.getNodeName().equalsIgnoreCase("category") && categoryNode.hasAttributes()){
                NamedNodeMap categoryAttributes = categoryNode.getAttributes();
                currentCategory = categoryAttributes.getNamedItem("name").getNodeValue();
                commandNode = categoryNode.getFirstChild();
                while (commandNode != null){
                    if (commandNode.getNodeName().equalsIgnoreCase("command") && commandNode.hasAttributes()){
                        NamedNodeMap commandAttributes = commandNode.getAttributes();
                        currentCommand = new Command(commandAttributes.getNamedItem("name").getNodeValue(), currentCategory, commandAttributes.getNamedItem("info").getNodeValue());
                        commandList.put(currentCommand.getName(), currentCommand);
                        parametersNode = commandNode.getFirstChild();
                        while (parametersNode != null){
                            if (parametersNode.getNodeName().equalsIgnoreCase("inputparameters")){
                                inputParametersNode = parametersNode.getFirstChild();
                                while (inputParametersNode != null){
                                    if (inputParametersNode.getNodeName().equalsIgnoreCase("inputparameter") && inputParametersNode.hasAttributes()){
                                        NamedNodeMap inputParameterAttributes = inputParametersNode.getAttributes();
                                        InputCommandParameter inputCommandParameter = new InputCommandParameter(inputParameterAttributes.getNamedItem("info").getNodeValue(), inputParameterAttributes.getNamedItem("parametertype").getNodeValue());
                                        currentCommand.addInputParameter(inputCommandParameter);
                                    }
                                    inputParametersNode = inputParametersNode.getNextSibling();
                                }
                            } else {
                                if (parametersNode.getNodeName().equalsIgnoreCase("outputparameters")){
                                    outputParametersNode = parametersNode.getFirstChild();
                                    while (outputParametersNode != null){
                                        if (outputParametersNode.getNodeName().equalsIgnoreCase("outputparameter") && outputParametersNode.hasAttributes()){
                                            NamedNodeMap outputParameterAttributes = outputParametersNode.getAttributes();
                                            OutputCommandParameter outputCommandParameter = new OutputCommandParameter(outputParameterAttributes.getNamedItem("name").getNodeValue(), outputParameterAttributes.getNamedItem("info").getNodeValue(), outputParameterAttributes.getNamedItem("parametertype").getNodeValue());
                                            currentCommand.addOutputParameter(outputCommandParameter);
                                        }
                                        outputParametersNode = outputParametersNode.getNextSibling();
                                    }
                                }
                            }
                            parametersNode = parametersNode.getNextSibling();
                        }
                    }
                    commandNode = commandNode.getNextSibling();
                }
            }
            categoryNode = categoryNode.getNextSibling();
        }
    }

    public boolean containsCommand(String commandText){
        return commandList.containsKey(commandText);
    }

    public void displayHelp(){
        for (Command command : commandList.values()){
            System.out.println(command);
        }
    }

    public void helpAbout(String commandText) throws CommandNotDefinedException {
        if (!commandList.containsKey(commandText)){
            throw new CommandNotDefinedException(commandText, 0);
        }
        System.out.println(getCommand(commandText));
    }

    public void searchHelp(String searchText){
        for (Command command : commandList.values()){
            if (command.getName().contains(searchText) || command.getInfo().contains(searchText)){
                System.out.println(command);
            }
        }
    }

    public Command getCommand(String commandName){
        return commandList.get(commandName);
    }
}
