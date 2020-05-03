package ru.cma.ml.isolationForest;

import weka.classifiers.Evaluation;
import weka.classifiers.misc.IsolationForest;
import weka.core.*;

import java.util.ArrayList;

public class IsolationForestTrainer {
    private IsolationForest classifier;
    private Instances dataRaw;
    private String modelFile;

    public Instances getDataRaw() {
        return dataRaw;
    }

    public IsolationForestTrainer(String outputFile, int capacity) {
        classifier = new IsolationForest();
        modelFile = outputFile;

        ArrayList<Attribute> atts = new ArrayList<>(2);
        ArrayList<String> classVal = new ArrayList<>(2);
        classVal.add("NORMAL");
        classVal.add("ANOMALY");
        atts.add(new Attribute("data"));
        atts.add(new Attribute("class", classVal));

        dataRaw = new Instances("TrainingInstances", atts, capacity);
    }

    public void addTrainingInstance(double data, Classification classVal) {
        double[] instanceValue = new double[dataRaw.numAttributes()];
        instanceValue[0] = data;
        instanceValue[1] = classVal.ordinal();
        dataRaw.add(new DenseInstance(1.0, instanceValue));
        dataRaw.setClassIndex(1);
    }

    public void trainModel() throws Exception {
        classifier.buildClassifier(dataRaw);
    }

    public void testModel() throws Exception {
        Evaluation test = new Evaluation(dataRaw);
        test.evaluateModel(classifier, dataRaw);
        String summary = test.toSummaryString();
        System.out.println(summary);
    }

    public void showInstances() {
        System.out.println(dataRaw);
    }

    public void saveModel() throws Exception {
        SerializationHelper.write(modelFile, classifier);
    }

    public void loadModel() throws Exception {
        this.classifier = (IsolationForest) SerializationHelper.read(modelFile);
    }

    public Classification classify(double data) throws Exception {
        double[] instanceValue = new double[dataRaw.numAttributes()];
        instanceValue[0] = data;

        Instance denseInstance = new DenseInstance(1.0, instanceValue);
        dataRaw.setClassIndex(1);
        denseInstance.setDataset(dataRaw);

        double prediction = classifier.classifyInstance(denseInstance);

        return Classification.values()[(int) prediction];
    }
}
