package ru.cma.ml.isolationForest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class IsolationForestTrainerTest {
    IsolationForestTrainer trainer;

    @Before
    public void init() {
        trainer = new IsolationForestTrainer("models/test_isolation_forest.model", 10);
    }

    @Test
    public void testAddTrainingInstance() {
        trainer.addTrainingInstance(-1.1, Classification.NORMAL);
        trainer.addTrainingInstance(0.3, Classification.NORMAL);
        trainer.addTrainingInstance(0.5, Classification.NORMAL);
        trainer.addTrainingInstance(100, Classification.NORMAL);
        assertEquals(trainer.getDataRaw().numInstances(), 4);
    }

    @Test
    public void testClassifyOnExampleFromSklearn() throws Exception {
        trainer.addTrainingInstance(-1.1, Classification.NORMAL);
        trainer.addTrainingInstance(0.3, Classification.NORMAL);
        trainer.addTrainingInstance(0.5, Classification.NORMAL);
        trainer.addTrainingInstance(100.0, Classification.NORMAL);
        trainer.trainModel();
        assertEquals(trainer.classify(0.1), Classification.NORMAL);
        assertEquals(trainer.classify(0), Classification.NORMAL);
        assertEquals(trainer.classify(90), Classification.ANOMALY);
    }
}
