package ScriptLanguage;

import Classification.Classifier.Classifier;
import Classification.Classifier.DiscreteFeaturesNotAllowed;
import Classification.DataSet.DataSet;
import Classification.DistanceMetric.EuclidianDistance;
import Classification.Experiment.*;
import Classification.Filter.Pca;
import Classification.Model.Svm.KernelType;
import Classification.Parameter.*;
import Classification.Performance.ClassificationAlgorithmExpectedException;
import Classification.Performance.ExperimentPerformance;
import Classification.StatisticalTest.PairedTest;
import Classification.StatisticalTest.StatisticalTestNotApplicable;
import ScriptLanguage.Exception.*;
import ScriptLanguage.Expression.ExpressionList;
import ScriptLanguage.ProgramParameter.OnOffType;
import ScriptLanguage.ProgramParameter.ProgramParameters;
import ScriptLanguage.Variable.*;
import Math.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Interpreter {
    private SymbolTable symbolTable;
    private ScriptLanguage scriptLanguage;
    private ProgramParameters programParameters;
    private DataSetRepository dataSetRepository;

    public Interpreter(){
        scriptLanguage = new ScriptLanguage("language.xml");
        programParameters = new ProgramParameters("parameters.xml");
        symbolTable = new SymbolTable(true);
        dataSetRepository = new DataSetRepository();
    }

    private Parameter prepareClassifierParameters(String classifierName){
        Integer[] hiddenNodes;
        int randomSeed = (Integer)programParameters.getParameterValue("randomSeed");
        switch (classifierName){
            case "dummy":
            case "randomClassifier":
            case "lda":
            case "qda":
            case "naiveBayes":
                return new Parameter(randomSeed);
            case "knn":
                if (programParameters.getParameterValue("distanceMetric").equals("euclidian")){
                    return new KnnParameter(randomSeed, (Integer)programParameters.getParameterValue("neighborCount"), new EuclidianDistance());
                } else {
                    return new KnnParameter(randomSeed, (Integer)programParameters.getParameterValue("neighborCount"), null);
                }
            case "c45":
                return new C45Parameter(randomSeed, true, (Double) programParameters.getParameterValue("crossValidationRatio"));
            case "rocchio":
                if (programParameters.getParameterValue("distanceMetric").equals("euclidian")){
                    return new RocchioParameter(randomSeed, new EuclidianDistance());
                } else {
                    return new RocchioParameter(randomSeed, null);
                }
            case "linearPerceptron":
                return new LinearPerceptronParameter(randomSeed,
                        (Double) programParameters.getParameterValue("learningRate"),
                        (Double) programParameters.getParameterValue("etaDecrease"),
                        (Double) programParameters.getParameterValue("crossValidationRatio"),
                        (Integer)programParameters.getParameterValue("epoch"));
            case "multiLayerPerceptron":
                hiddenNodes = (Integer[])programParameters.getParameterValue("hiddenNodes");
                return new MultiLayerPerceptronParameter(randomSeed,
                        (Double) programParameters.getParameterValue("learningRate"),
                        (Double) programParameters.getParameterValue("etaDecrease"),
                        (Double) programParameters.getParameterValue("crossValidationRatio"),
                        (Integer)programParameters.getParameterValue("epoch"), hiddenNodes[0]);
            case "deepNetwork":
                hiddenNodes = (Integer[])programParameters.getParameterValue("hiddenNodes");
                ArrayList<Integer> hiddens = new ArrayList<>();
                Collections.addAll(hiddens, hiddenNodes);
                return new DeepNetworkParameter(randomSeed,
                        (Double) programParameters.getParameterValue("learningRate"),
                        (Double) programParameters.getParameterValue("etaDecrease"),
                        (Double) programParameters.getParameterValue("crossValidationRatio"),
                        (Integer)programParameters.getParameterValue("epoch"), hiddens);
            case "svm":
                return new SvmParameter(randomSeed,
                        KernelType.valueOf((String) programParameters.getParameterValue("kernelType")),
                        (Integer) programParameters.getParameterValue("polynomDegree"),
                        (Double) programParameters.getParameterValue("svmGamma"),
                        (Double) programParameters.getParameterValue("svmCoef0"),
                        (Double) programParameters.getParameterValue("svmC"));
            case "randomForest":
                return new RandomForestParameter(randomSeed,
                        (Integer) programParameters.getParameterValue("ensembleSize"),
                        (Integer) programParameters.getParameterValue("featureSize"));
            case "bagging":
                return new BaggingParameter(randomSeed,
                        (Integer) programParameters.getParameterValue("ensembleSize"));
            default:
                return null;
        }
    }

    public SymbolTable getSymbolTable(){
        return symbolTable;
    }

    public void executeStatement(SourceStatement statement){
        SourceCode sourceCode;
        DataSet dataSet;
        try {
            String commandText = statement.getCommand().getName();
            switch (commandText) {
                case "time":
                case "separateDataUsed":
                case "displayCode":
                case "displayResult":
                case "displayAccuracy":
                case "hyperParameterTune":
                case "oneTailed":
                    programParameters.setProgramParameter(commandText, OnOffType.valueOf(statement.getParameter(0).toUpperCase()));
                    break;
                case "epoch":
                case "numberOfFolds":
                case "numberOfRuns":
                case "neighborCount":
                case "polynomDegree":
                case "featureSize":
                case "ensembleSize":
                case "randomSeed":
                case "precision":
                    programParameters.setProgramParameter(commandText, Integer.valueOf(statement.getParameter(0)));
                    break;
                case "learningRate":
                case "varianceExplained":
                case "confidenceLevel":
                case "etaDecrease":
                case "svmGamma":
                case "svmCoef0":
                case "svmC":
                case "crossValidationRatio":
                case "testSetPercentage":
                    programParameters.setProgramParameter(commandText, Double.valueOf(statement.getParameter(0)));
                    break;
                case "kernelType":
                case "distanceMetric":
                case "testType":
                case "dimensionReductionType":
                case "output":
                case "dataDirectory":
                    programParameters.setProgramParameter(commandText, statement.getParameter(0));
                    break;
                case "hiddenNodes":
                    Object[] valueList = new Integer[statement.parameterSize()];
                    for (int i = 0; i < statement.parameterSize(); i++) {
                        valueList[i] = Integer.parseInt(statement.getParameter(i));
                    }
                    programParameters.setProgramParameter(commandText, valueList);
                    break;
                case "integer":
                case "real":
                case "string":
                case "file":
                    for (int i = 0; i < statement.parameterSize(); i++){
                        symbolTable.addVariable(VariableType.valueOf(commandText.toUpperCase()), statement.getParameter(i), 0);
                    }
                    break;
                case "variables":
                    symbolTable.print(false);
                    break;
                case "=":
                    Variable variable = symbolTable.checkVariable(statement.getParameter(0), true);
                    switch (variable.getVariableType()){
                        case INTEGER:
                            ExpressionList expression1 = new ExpressionList(statement.getParameter(1), symbolTable);
                            ((IntegerVariable)variable).setValue(((IntegerVariable) expression1.evaluate()).getValue());
                            break;
                        case REAL:
                            ExpressionList expression2 = new ExpressionList(statement.getParameter(1), symbolTable);
                            ((RealVariable)variable).setValue(((RealVariable) expression2.evaluate()).getValue());
                            break;
                        case STRING:
                        case FILE:
                            ((StringVariable)variable).setValue(statement.getParameter(1));
                            break;
                    }
                    break;
                case "++":
                case "inc":
                    performOperation("+", statement.getParameter(0), "1");
                    break;
                case "--":
                case "dec":
                    performOperation("-", statement.getParameter(0), "1");
                    break;
                case "+=":
                case "-=":
                case "*=":
                case "/=":
                case "^=":
                case "$=":
                case "@=":
                    performOperation("" + commandText.charAt(0), statement.getParameter(0), statement.getParameter(1));
                    break;
                case "normal":
                    double z = Double.parseDouble(statement.getParameter(0));
                    symbolTable.setDefaultVariable("out1", Distribution.zNormal(z));
                    break;
                case "normalinv":
                    double probability = Double.parseDouble(statement.getParameter(0));
                    symbolTable.setDefaultVariable("out1", Distribution.zInverse(probability));
                    break;
                case "chi":
                    double criticalValue = Double.parseDouble(statement.getParameter(0));
                    int freedom = Integer.parseInt(statement.getParameter(1));
                    symbolTable.setDefaultVariable("out1", Distribution.chiSquare(criticalValue, freedom));
                    break;
                case "chiinv":
                    probability = Double.parseDouble(statement.getParameter(0));
                    freedom = Integer.parseInt(statement.getParameter(1));
                    symbolTable.setDefaultVariable("out1", Distribution.chiSquareInverse(probability, freedom));
                    break;
                case "f":
                    criticalValue = Double.parseDouble(statement.getParameter(0));
                    int freedom1 = Integer.parseInt(statement.getParameter(1));
                    int freedom2 = Integer.parseInt(statement.getParameter(2));
                    symbolTable.setDefaultVariable("out1", Distribution.fDistribution(criticalValue, freedom1, freedom2));
                    break;
                case "finv":
                    probability = Double.parseDouble(statement.getParameter(0));
                    freedom1 = Integer.parseInt(statement.getParameter(1));
                    freedom2 = Integer.parseInt(statement.getParameter(2));
                    symbolTable.setDefaultVariable("out1", Distribution.fDistributionInverse(probability, freedom1, freedom2));
                    break;
                case "t":
                    criticalValue = Double.parseDouble(statement.getParameter(0));
                    freedom = Integer.parseInt(statement.getParameter(1));
                    symbolTable.setDefaultVariable("out1", Distribution.tDistribution(criticalValue, freedom));
                    break;
                case "tinv":
                    probability = Double.parseDouble(statement.getParameter(0));
                    freedom = Integer.parseInt(statement.getParameter(1));
                    symbolTable.setDefaultVariable("out1", Distribution.tDistributionInverse(probability, freedom));
                    break;
                case "help":
                    scriptLanguage.displayHelp();
                    break;
                case "helpAbout":
                    scriptLanguage.helpAbout(statement.getParameter(0));
                    break;
                case "search":
                    scriptLanguage.searchHelp(statement.getParameter(0));
                    break;
                case "combined5x2F":
                case "pairedt":
                case "paired5x2t":
                case "combined5x2t":
                case "sign":
                    PairedTest pairedTest = (PairedTest) Class.forName("Classification.StatisticalTest." + commandText.substring(0, 1).toUpperCase() + commandText.substring(1)).getConstructor().newInstance();
                    double confidenceLevel = (Double) programParameters.getParameterValue("confidenceLevel");
                    ExperimentPerformance experimentPerformance1 = new ExperimentPerformance(statement.getParameter(0));
                    ExperimentPerformance experimentPerformance2 = new ExperimentPerformance(statement.getParameter(1));
                    symbolTable.setDefaultVariable("out1", pairedTest.compare(experimentPerformance1, experimentPerformance2, confidenceLevel));
                    break;
                case "loadDataSets":
                    dataSetRepository.loadDataSets((String) programParameters.getParameterValue("dataDirectory"));
                    break;
                case "unloadDataSets":
                    dataSetRepository.unloadDataSets();
                    break;
                case "dataSets":
                    System.out.print(dataSetRepository.toString());
                    break;
                case "classCount":
                case "sampleSize":
                case "attributeCount":
                case "discreteAttributeCount":
                case "continuousAttributeCount":
                    dataSet = dataSetRepository.getDataSet(statement.getParameter(0));
                    symbolTable.setDefaultVariable("out1", (Integer) dataSet.getClass().getMethod(commandText, null).invoke(dataSet));
                    break;
                case "classes":
                    dataSet = dataSetRepository.getDataSet(statement.getParameter(0));
                    symbolTable.setDefaultStringVariable("sout1", dataSet.getClasses());
                    break;
                case "dataSet":
                    dataSet = dataSetRepository.getDataSet(statement.getParameter(0));
                    System.out.println(dataSet.info(statement.getParameter(0)));
                    break;
                case "dummy":
                case "randomClassifier":
                case "knn":
                case "c45":
                case "rocchio":
                case "lda":
                case "deepNetwork":
                case "linearPerceptron":
                case "multiLayerPerceptron":
                case "svm":
                case "qda":
                case "naiveBayes":
                case "randomForest":
                case "bagging":
                    Classifier classifier = (Classifier) Class.forName("Classification.Classifier." + commandText.substring(0, 1).toUpperCase() + commandText.substring(1)).getConstructor().newInstance();
                    Parameter parameter = prepareClassifierParameters(commandText);
                    dataSet = dataSetRepository.getDataSet(statement.getParameter(0));
                    Experiment experiment = new Experiment(classifier, parameter, dataSet);
                    MultipleRun multipleRun;
                    switch (statement.getParameter(1)){
                        case "cv":
                            if (((Integer)programParameters.getParameterValue("numberOfRuns")) == 1){
                                multipleRun = new StratifiedKFoldRun(((Integer)programParameters.getParameterValue("numberOfFolds")));
                            } else {
                                multipleRun = new StratifiedMxKFoldRun(((Integer)programParameters.getParameterValue("numberOfRuns")), ((Integer)programParameters.getParameterValue("numberOfFolds")));
                            }
                            break;
                        case "bootstrap":
                            multipleRun = new BootstrapRun(((Integer)programParameters.getParameterValue("numberOfRuns")));
                            break;
                        default:
                            throw new ClassifierRunTypeNotDefinedException(statement.getParameter(1));
                    }
                    ExperimentPerformance experimentPerformance = multipleRun.execute(experiment);
                    String[] performances = new String[experimentPerformance.numberOfExperiments()];
                    for (int i = 0; i < experimentPerformance.numberOfExperiments(); i++){
                        if ((programParameters.getParameterValue("displayAccuracy")).equals(OnOffType.OFF)){
                            performances[i] = "" + experimentPerformance.getErrorRate(i);
                        } else {
                            performances[i] = "" + experimentPerformance.getAccuracy(i);
                        }
                    }
                    symbolTable.setDefaultArrayVariable("aout1", performances);
                    break;
                case "pca":
                    Pca pca = null;
                    dataSet = dataSetRepository.getDataSet(statement.getParameter(0));
                    if (programParameters.getParameterValue("dimensionReductionType").equals("feature")){
                        pca = new Pca(dataSet, (Integer) programParameters.getParameterValue("featureSize"));
                    } else {
                        if (programParameters.getParameterValue("dimensionReductionType").equals("variance")){
                            pca = new Pca(dataSet, (Double) programParameters.getParameterValue("varianceExplained"));
                        }
                    }
                    if (pca != null){
                        pca.train();
                        pca.convert();
                        dataSetRepository.updateDataSet(statement.getParameter(0), dataSet);
                    }
                    break;
                case "loadFile":
                    sourceCode = new SourceCode(statement.getParameter(0), scriptLanguage);
                    sourceCode.run();
                    break;
                case "viewFile":
                    viewFile(statement.getParameter(0));
                    break;
                case "openFile":
                    openFile(statement.getParameter(0), statement.getParameter(1));
                    break;
                case "closeFile":
                    closeFile(statement.getParameter(0));
                    break;
                case "writeFile":
                    writeFile(statement.getParameter(0), statement.getParameter(1));
                    break;
                case "readFile":
                    readFile(statement.getParameter(0), statement.getParameter(1));
                    break;
                case "tokenize":
                    tokenize(statement.getParameter(0), statement.getParameter(1));
                    break;
                case "characterCount":
                    characterCount(statement.getParameter(0), statement.getParameter(1));
                    break;
                case "length":
                    length(statement.getParameter(0));
                    break;
                case "charAt":
                    charAt(statement.getParameter(0), Integer.parseInt(statement.getParameter(1)));
                    break;
                case "compile":
                    sourceCode = new SourceCode(statement.getParameter(0), scriptLanguage);
                    sourceCode.compile();
                    break;
                case "readArrayFile":
                    readArrayFromFile(statement.getParameter(0), statement.getParameter(1), Integer.parseInt(statement.getParameter(2)));
                    break;

            }
        } catch (CompileException | FileNotFoundException | FileTypeNotSupportedException |
                VariableTypeNotMatchException | DivisionByZeroException | OperatorNotSupportedException |
                ClassificationAlgorithmExpectedException | DiscreteFeaturesNotAllowed | InvocationTargetException |
                IllegalAccessException | InstantiationException | NoSuchMethodException | DataSetNotExistException |
                ClassifierRunTypeNotDefinedException | ClassNotFoundException | StatisticalTestNotApplicable |
                ArrayDimensionLessException | ExpressionNotEvaluatedException | OperandExpectedException | ContinuousValueExpectedException e) {
            System.out.println(e.toString());
        }
    }

    public void runFromStandardInput(){
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print("->");
            String userInput = input.nextLine();
            SourceStatement statement;
            try {
                statement = new SourceStatement(userInput, scriptLanguage);
                statement.compile(null, symbolTable, 1);
                if (statement.getCommand().getName().equalsIgnoreCase("exit")){
                    return;
                }
                executeStatement(statement);
            } catch (CompileException e) {
                System.out.println(e.toString());
            }
        }
    }

    private void viewFile(String fileName){
        try {
            Scanner input = new Scanner(new File(fileName));
            while (input.hasNext()){
                System.out.println(input.nextLine());
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void charAt(String variableName, int index) throws VariableNotExistException, VariableTypeNotMatchException {
        symbolTable.setDefaultStringVariable("sout1", ((StringVariable)symbolTable.checkStringVariable(variableName)).charAt(index));
    }

    private void length(String variableName) throws VariableNotExistException, VariableTypeNotMatchException {
        symbolTable.setDefaultVariable("out1", ((StringVariable)symbolTable.checkStringVariable(variableName)).length());
    }

    private void characterCount(String variableName, String characters) throws VariableNotExistException, VariableTypeNotMatchException {
        symbolTable.setDefaultVariable("out1", ((StringVariable)symbolTable.checkStringVariable(variableName)).charCount(characters));
    }

    private void tokenize(String variableName, String separators) throws VariableNotExistException, VariableTypeNotMatchException {
        String[] tokens = ((StringVariable)symbolTable.checkStringVariable(variableName)).split(separators);
        symbolTable.setDefaultArrayVariable("aout1", tokens);
        symbolTable.setDefaultVariable("out1", tokens.length);
    }

    private void readFile(String fileVariableName, String outputVariableName) throws VariableNotExistException, VariableTypeNotMatchException {
        String lineRead = ((FileVariable) symbolTable.checkFileVariable(fileVariableName)).readString();
        ((StringVariable) symbolTable.checkStringVariable(outputVariableName)).setValue(lineRead);
    }

    private void writeFile(String fileVariableName, String lineToBeWritten) throws VariableNotExistException, VariableTypeNotMatchException {
        ((FileVariable) symbolTable.checkFileVariable(fileVariableName)).writeString(lineToBeWritten);
    }

    private void openFile(String fileVariableName, String fileType) throws VariableNotExistException, VariableTypeNotMatchException, FileNotFoundException, FileTypeNotSupportedException {
        FileVariable fileVariable = ((FileVariable) symbolTable.checkFileVariable(fileVariableName));
        if (fileType.equalsIgnoreCase("r")){
            fileVariable.openFile(FileType.READ);
        } else {
            if (fileType.equalsIgnoreCase("w")){
                fileVariable.openFile(FileType.WRITE);
            } else {
                throw new FileTypeNotSupportedException(fileType);
            }
        }
    }

    private void closeFile(String fileVariableName) throws VariableNotExistException, VariableTypeNotMatchException {
        ((FileVariable) symbolTable.checkFileVariable(fileVariableName)).closeFile();
    }

    private void readArrayFromFile(String fileVariableName, String outputArrayVariableName, int size) throws VariableNotExistException, VariableTypeNotMatchException {
        FileVariable fileVariable = (FileVariable) symbolTable.checkFileVariable(fileVariableName);
        ArrayVariable outputArrayVariable = (ArrayVariable) symbolTable.checkArrayVariable(outputArrayVariableName);
        fileVariable.readArray(outputArrayVariable, size);
    }

    private void performOperation(String operator, String variableName1, String variableName2) throws VariableNotExistException, ArrayDefinitionException, DivisionByZeroException, OperatorNotSupportedException {
        Variable variable1 = symbolTable.checkVariable(variableName1, true);
        VariableType variableType1 = variable1.getVariableType();
        if (variableType1.equals(VariableType.INTEGER) || variableType1.equals(VariableType.REAL)){
            double value1, value2;
            if (variableType1.equals(VariableType.INTEGER)){
                value1 = ((IntegerVariable)variable1).getValue();
            } else {
                value1 = ((RealVariable)variable1).getValue();
            }
            if (symbolTable.getTokenValue(variableName2) instanceof Integer){
                value2 = (Integer) symbolTable.getTokenValue(variableName2);
            } else {
                value2 = (double) symbolTable.getTokenValue(variableName2);
            }
            switch (operator){
                case "+":
                    value1 += value2;
                    break;
                case "-":
                    value1 -= value2;
                    break;
                case "*":
                    value1 *= value2;
                    break;
                case "/":
                    if (value2 == 0){
                        throw new DivisionByZeroException(variableName2);
                    }
                    value1 /= value2;
                    break;
                case "^":
                    value1 = Math.pow(value1, value2);
                    break;
                case "$":
                    value1 = Math.pow(value1, 1 / value2);
                    break;
                case "@":
                    if (value2 == 0){
                        throw new DivisionByZeroException(variableName2);
                    }
                    value1 = value1 - value2 * ((int)(value1 / value2));
                    break;
            }
            if (variableType1.equals(VariableType.INTEGER) && variable1 instanceof IntegerVariable){
                ((IntegerVariable)variable1).setValue((int)value1);
            } else {
                if (variable1 instanceof RealVariable){
                    ((RealVariable)variable1).setValue(value1);
                }
            }
        } else {
            throw new OperatorNotSupportedException(operator, variableType1);
        }
    }

}
