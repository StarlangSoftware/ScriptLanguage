package ScriptLanguage.ProgramParameter;

import ScriptLanguage.Exception.ListValueNotDefinedException;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;

public class ProgramParameters {
    private HashMap<String, ProgramParameter> list;

    public ProgramParameters(String fileName){
        Node parseNode, rootNode, nextNode;
        ProgramParameter programParameter = null;
        DOMParser parser = new DOMParser();
        Document doc;
        try {
            parser.parse(fileName);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        list = new HashMap<>();
        doc = parser.getDocument();
        rootNode = doc.getFirstChild();
        parseNode = rootNode.getFirstChild();
        while (parseNode != null){
            if (parseNode.getNodeName().equalsIgnoreCase("parameter")){
                if (parseNode.hasAttributes()){
                    NamedNodeMap attributes = parseNode.getAttributes();
                    String parameterName = attributes.getNamedItem("name").getNodeValue();
                    String parameterType = attributes.getNamedItem("type").getNodeValue();
                    String parameterValue = attributes.getNamedItem("value").getNodeValue();
                    switch (parameterType){
                        case "INTEGER":
                            programParameter = new IntegerProgramParameter(parameterName, Integer.parseInt(parameterValue));
                            break;
                        case "REAL":
                            programParameter = new RealProgramParameter(parameterName, Double.parseDouble(parameterValue));
                            break;
                        case "ONOFF":
                            if (parameterValue.equals("ON")){
                                programParameter = new OnOffProgramParameter(parameterName, OnOffType.ON);
                            } else {
                                programParameter = new OnOffProgramParameter(parameterName, OnOffType.OFF);
                            }
                            break;
                        case "STRING":
                            programParameter = new StringProgramParameter(parameterName, parameterValue);
                            break;
                        case "LIST":
                            programParameter = new ListProgramParameter(parameterName, parameterValue);
                            nextNode = parseNode.getFirstChild();
                            while (nextNode != null){
                                if (nextNode.getNodeName().equalsIgnoreCase("value")){
                                    String listValue = nextNode.getFirstChild().getNodeValue();
                                    ((ListProgramParameter) programParameter).addValue(listValue);
                                }
                                nextNode = nextNode.getNextSibling();
                            }
                            break;
                        case "ARRAY":
                            String subType = attributes.getNamedItem("subtype").getNodeValue();
                            String size = attributes.getNamedItem("size").getNodeValue();
                            switch (subType){
                                case "INTEGER":
                                    programParameter = new ArrayProgramParameter(parameterName, ProgramParameterType.INTEGER, Integer.parseInt(size), Integer.parseInt(parameterValue));
                                    break;
                                case "REAL":
                                    programParameter = new ArrayProgramParameter(parameterName, ProgramParameterType.REAL, Integer.parseInt(size), Double.parseDouble(parameterValue));
                                    break;
                            }
                    }
                    if (programParameter != null){
                        list.put(programParameter.getName(), programParameter);
                    }
                }
            }
            parseNode = parseNode.getNextSibling();
        }
    }

    public void setProgramParameter(String parameterName, Object value){
        ProgramParameter programParameter = list.get(parameterName);
        if (programParameter instanceof ArrayProgramParameter){
            ((ArrayProgramParameter) programParameter).setValue((Object[]) value);
        } else {
            if (programParameter instanceof IntegerProgramParameter){
                ((IntegerProgramParameter) programParameter).setValue((int) value);
            } else {
                if (programParameter instanceof RealProgramParameter){
                    ((RealProgramParameter) programParameter).setValue((double) value);
                } else {
                    if (programParameter instanceof OnOffProgramParameter){
                        ((OnOffProgramParameter) programParameter).setValue((OnOffType) value);
                    } else {
                        if (programParameter instanceof ListProgramParameter){
                            try {
                                ((ListProgramParameter) programParameter).setValue((String) value);
                            } catch (ListValueNotDefinedException e) {
                                System.out.println(e.toString());
                            }
                        } else {
                            if (programParameter instanceof StringProgramParameter){
                                try {
                                    ((StringProgramParameter) programParameter).setValue((String) value);
                                } catch (ListValueNotDefinedException e) {
                                    System.out.println(e.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Object getParameterValue(String parameterName){
        ProgramParameter programParameter = list.get(parameterName);
        return programParameter.getValue();
    }

}
