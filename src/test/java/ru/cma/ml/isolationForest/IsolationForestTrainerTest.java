package ru.cma.ml.isolationForest;

import org.junit.Before;
import org.junit.Test;

public class IsolationForestTrainerTest {
  IsolationForestTrainer trainer;

  @Before
  public void init() {
    trainer = new IsolationForestTrainer("models/test_isolation_forest.model", 10);
  }

  @Test
  public void testConstructors() {
    IsolationForestTrainer testTrainer = new IsolationForestTrainer();
    org.junit.Assert.assertNotNull(testTrainer.getDataset());
    testTrainer = new IsolationForestTrainer(10);
    org.junit.Assert.assertNotNull(testTrainer.getDataset());
    testTrainer = new IsolationForestTrainer("models/test_isolation_forest.model");
    org.junit.Assert.assertNotNull(testTrainer.getDataset());
    testTrainer = new IsolationForestTrainer("models/test_isolation_forest.model", 10);
    org.junit.Assert.assertNotNull(testTrainer.getDataset());
  }

  @Test
  public void testAddTrainingInstance() {
    trainer.addTrainingInstances(-1.1, Classification.NORMAL);
    trainer.addTrainingInstances(0.3, Classification.NORMAL);
    trainer.addTrainingInstances(0.5, Classification.NORMAL);
    trainer.addTrainingInstances(100, Classification.NORMAL);
    org.junit.Assert.assertEquals(trainer.getDataset().numInstances(), 4);
  }

  @Test
  public void testClassifyOnExampleFromSklearn() throws Exception {
    trainer.addTrainingInstances(-1.1, Classification.NORMAL);
    trainer.addTrainingInstances(0.3, Classification.NORMAL);
    trainer.addTrainingInstances(0.5, Classification.NORMAL);
    trainer.addTrainingInstances(100, Classification.NORMAL);
    trainer.trainModel();
    org.junit.Assert.assertEquals(trainer.classify(0.1), Classification.NORMAL);
    org.junit.Assert.assertEquals(trainer.classify(0), Classification.NORMAL);
    org.junit.Assert.assertEquals(trainer.classify(90), Classification.ANOMALY);
    org.junit.Assert.assertEquals(trainer.classify(100), Classification.ANOMALY);
  }
}
