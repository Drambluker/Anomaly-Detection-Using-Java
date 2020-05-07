package ru.cma.ml.isolationForest;

import weka.classifiers.Evaluation;
import weka.classifiers.misc.IsolationForest;
import weka.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class IsolationForestTrainer {
  private final int DEFAULT_CAPACITY = 10;

  private IsolationForest classifier;
  private Instances dataset;
  private String modelFile;

  public Instances getDataset() {
    return dataset;
  }

  public IsolationForestTrainer() {
    classifier = new IsolationForest();
    modelFile = null;

    ArrayList<Attribute> atts = new ArrayList<>(2);
    ArrayList<String> classVal = new ArrayList<>(2);
    classVal.add("NORMAL");
    classVal.add("ANOMALY");
    atts.add(new Attribute("data"));
    atts.add(new Attribute("class", classVal));

    dataset = new Instances("TrainingInstances", atts, DEFAULT_CAPACITY);
    dataset.setClassIndex(1);
  }

  public IsolationForestTrainer(int capacity) {
    classifier = new IsolationForest();
    modelFile = null;

    ArrayList<Attribute> atts = new ArrayList<>(2);
    ArrayList<String> classVal = new ArrayList<>(2);
    classVal.add("NORMAL");
    classVal.add("ANOMALY");
    atts.add(new Attribute("data"));
    atts.add(new Attribute("class", classVal));

    dataset = new Instances("TrainingInstances", atts, capacity);
    dataset.setClassIndex(1);
  }

  public IsolationForestTrainer(String outputFile) {
    classifier = new IsolationForest();
    modelFile = outputFile;

    ArrayList<Attribute> atts = new ArrayList<>(2);
    ArrayList<String> classVal = new ArrayList<>(2);
    classVal.add("NORMAL");
    classVal.add("ANOMALY");
    atts.add(new Attribute("data"));
    atts.add(new Attribute("class", classVal));

    dataset = new Instances("TrainingInstances", atts, DEFAULT_CAPACITY);
    dataset.setClassIndex(1);
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

    dataset = new Instances("TrainingInstances", atts, capacity);
    dataset.setClassIndex(1);
  }

  public IsolationForestTrainer(Instances dataset) {
    classifier = new IsolationForest();
    this.dataset = dataset;
    this.dataset.setClassIndex(1);
    modelFile = null;
  }

  public IsolationForestTrainer(Instances dataset, String outputFile) {
    classifier = new IsolationForest();
    this.dataset = dataset;
    this.dataset.setClassIndex(1);
    modelFile = outputFile;
  }

  public void addTrainingInstances(double data, Classification classVal) {
    double[] instanceValue = new double[dataset.numAttributes()];
    instanceValue[0] = data;
    instanceValue[1] = classVal.ordinal();
    dataset.add(new DenseInstance(1.0, instanceValue));
  }

  public void addTrainingInstances(Instance instance) {
    dataset.add(instance);
  }

  public void addTrainingInstances(Collection<? extends Instance> instances) {
    dataset.addAll(instances);
  }

  public void trainModel() throws Exception {
    classifier.buildClassifier(dataset);
  }

  public Evaluation testModel() throws Exception {
    Evaluation test = new Evaluation(dataset);
    test.evaluateModel(classifier, dataset);
    return test;
  }

  public Evaluation crossValidateModel(int numFolds) throws Exception {
    Evaluation eval = new Evaluation(dataset);
    eval.crossValidateModel(classifier, dataset, numFolds, new Random());
    return eval;
  }

  public void showInstances() {
    System.out.println(dataset);
  }

  public void saveModel() throws Exception {
    if (modelFile != null) {
      SerializationHelper.write(modelFile, classifier);
    }
  }

  public void loadModel() throws Exception {
    if (modelFile != null) {
      this.classifier = (IsolationForest) SerializationHelper.read(modelFile);
    }
  }

  public void loadModel(String modelFile) throws Exception {
    this.classifier = (IsolationForest) SerializationHelper.read(modelFile);
    this.modelFile = modelFile;
  }

  public Classification classify(double data) throws Exception {
    double[] instanceValue = new double[dataset.numAttributes()];
    instanceValue[0] = data;

    Instance denseInstance = new DenseInstance(1.0, instanceValue);
    dataset.setClassIndex(1);
    denseInstance.setDataset(dataset);

    double prediction = classifier.classifyInstance(denseInstance);

    return Classification.values()[(int) prediction];
  }

  public Classification classify(Instance instance) throws Exception {
    Instance instanceCopy = (Instance) instance.copy();
    instanceCopy.setDataset(dataset);

    double prediction = classifier.classifyInstance(instanceCopy);

    return Classification.values()[(int) prediction];
  }
}
