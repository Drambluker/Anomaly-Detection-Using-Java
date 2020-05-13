package ru.cma.neuralNetwork;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.Dl4jMlpClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.dl4j.CacheMode;
import weka.dl4j.NeuralNetConfiguration;
import weka.dl4j.activations.ActivationReLU;
import weka.dl4j.activations.ActivationSoftmax;
import weka.dl4j.layers.DenseLayer;
import weka.dl4j.layers.OutputLayer;
import weka.dl4j.lossfunctions.LossFMeasure;
import weka.dl4j.updater.Adam;

public class NeuralNetwork
{
    private Dl4jMlpClassifier classifier;
    private Instances dataset;

    public NeuralNetwork()
    {
        ArrayList<Attribute> atts = new ArrayList<>(2);
        ArrayList<String> classVal = new ArrayList<>(2);

        classVal.add("NORMAL");
        classVal.add("ANOMALY");
        atts.add(new Attribute("data"));
        atts.add(new Attribute("class", classVal));
    }

    public void testNeuralNetwork() throws Exception {
        classifier = new Dl4jMlpClassifier();

        classifier.setSeed(1);
        dataset.setClassIndex(dataset.numAttributes() - 1);

        DenseLayer denseLayer = new DenseLayer();
        denseLayer.setNOut(10);
        denseLayer.setActivationFunction(new ActivationReLU());

        OutputLayer outputLayer = new OutputLayer();
        outputLayer.setActivationFunction(new ActivationSoftmax());

        NeuralNetConfiguration nnc = new NeuralNetConfiguration();
        nnc.setUpdater(new Adam());
        nnc.setMiniBatch(false);

        classifier.setLayers(denseLayer, outputLayer);
        classifier.setNeuralNetConfiguration(nnc);

        classifier.setNumEpochs(10);
        classifier.setBatchSize("10000");
        classifier.getInstanceIterator().setTrainBatchSize(10000);
        // Evaluate the network
        Evaluation eval = new Evaluation(dataset);
        int numFolds = 10;
        eval.crossValidateModel(classifier, dataset, numFolds, new Random(1));
    }

    public void trainModel() throws Exception
    {
        testNeuralNetwork();

    }

    public void addTrainingInstances() throws IOException
    {
        dataset = new Instances(new FileReader("src/test/resources/test.arff"));
    }
}
