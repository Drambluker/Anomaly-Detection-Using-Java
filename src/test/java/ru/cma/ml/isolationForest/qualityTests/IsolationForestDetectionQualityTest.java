package ru.cma.ml.isolationForest.qualityTests;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import ru.cma.ml.isolationForest.Classification;
import ru.cma.ml.isolationForest.IsolationForestTrainer;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class IsolationForestDetectionQualityTest {
  Instances dataset;
  IsolationForestTrainer trainer;

  @Before
  public void init() throws Exception {
    ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("datasets/creditcard.csv");
    dataset = dataSource.getDataSet();
    dataset.setClassIndex(1);
  }

  @Test
  public void testAnomalyDetectionOnRange250() throws Exception {
    testDetection(Classification.ANOMALY, 250);
  }

  public void testDetection(Classification classification, int range) throws Exception {
    ArrayList<Integer> indexesOfInstances = getIndexesOfInstances(classification);

    int numInstances = indexesOfInstances.size();
    int incorrect = getNumIncorrect(classification, indexesOfInstances, range);
    int correct = numInstances - incorrect;

    StringBuilder sb = new StringBuilder();
    sb.append("Correct: ")
        .append(correct)
        .append("(")
        .append((double) correct / numInstances * 100)
        .append("%)\n");
    sb.append("Incorrect: ")
        .append(incorrect)
        .append("(")
        .append((double) incorrect / numInstances * 100)
        .append("%)\n");

    System.out.println(sb.toString());
  }

  public ArrayList<Integer> getIndexesOfInstances(Classification classification) {
    ArrayList<Integer> indexesOfInstances = new ArrayList<>();

    for (int i = 0; i < dataset.numInstances(); i++) {
      Instance instance = dataset.instance(i);

      if (Double.compare(instance.classValue(), classification.ordinal()) == 0) {
        indexesOfInstances.add(i);
      }
    }

    return indexesOfInstances;
  }

  public int getNumIncorrect(
      Classification classification, ArrayList<Integer> indexesOfInstances, int range)
      throws Exception {
    int incorrect = 0;

    for (int i = 0; i < indexesOfInstances.size(); i++) {
      if (indexesOfInstances.get(i).compareTo(range) >= 0) {
        trainer = new IsolationForestTrainer(range);
        ArrayList<Instance> instances = new ArrayList<>();

        for (int j = indexesOfInstances.get(i) - range; j < indexesOfInstances.get(i); j++) {
          instances.add(dataset.instance(j));
        }

        trainer.addTrainingInstances(instances);
        trainer.trainModel();
        Classification classVal = trainer.classify(dataset.get(indexesOfInstances.get(i)));

        if (classVal != classification) {
          incorrect++;
        }
      }
    }

    return incorrect;
  }
}
