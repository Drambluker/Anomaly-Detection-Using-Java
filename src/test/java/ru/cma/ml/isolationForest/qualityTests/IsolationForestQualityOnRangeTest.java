package ru.cma.ml.isolationForest.qualityTests;

import org.junit.Before;
import org.junit.Test;
import ru.cma.ml.isolationForest.IsolationForestTrainer;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.ArrayList;

public class IsolationForestQualityOnRangeTest {
    Instances dataset;
    IsolationForestTrainer trainer;

    @Before
    public void init() throws Exception {
        ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("datasets/creditcard.csv");
        dataset = dataSource.getDataSet();
    }

    @Test
    public void testQualityOnRange10() throws Exception {
        testQualityOnRange(10);
    }

    @Test
    public void testQualityOnRange100() throws Exception {
        testQualityOnRange(100);
    }

    @Test
    public void testQualityOnRange125() throws Exception {
        testQualityOnRange(125);
    }

    @Test
    public void testQualityOnRange250() throws Exception {
        testQualityOnRange(250);
        testQualityOnRange(500, 750);
        testQualityOnRange(1000, 1250);
        testQualityOnRange(2000, 2250);
        testQualityOnRange(4000, 4250);
        testQualityOnRange(8000, 8250);
        testQualityOnRange(16000, 16250);
        testQualityOnRange(32000, 32250);
        testQualityOnRange(64000, 64250);
        testQualityOnRange(128000, 128250);
        testQualityOnRange(256000, 256250);
    }

    @Test
    public void testQualityOnRange375() throws Exception {
        testQualityOnRange(375);
    }

    @Test
    public void testQualityOnRange500() throws Exception {
        testQualityOnRange(500);
    }

    @Test
    public void testQualityOnRange750() throws Exception {
        testQualityOnRange(750);
    }

    @Test
    public void testQualityOnRange1000() throws Exception {
        testQualityOnRange(1000);
    }

    @Test
    public void testQualityOnRange2000() throws Exception {
        testQualityOnRange(2000);
    }

    @Test
    public void testQualityOnRange10000() throws Exception {
        testQualityOnRange(10000);
    }

    @Test
    public void testQualityOnRange100000() throws Exception {
        testQualityOnRange(100000);
    }

    private void testQualityOnRange(int range) throws Exception {
        testQualityOnRange(0, range);
    }

    private void testQualityOnRange(int start, int end) throws Exception {
        ArrayList<Instance> trainingInstances = new ArrayList<>(end - start);

        for (int i = start; i < end; i++) {
            trainingInstances.add(dataset.instance(i));
        }

        trainer = new IsolationForestTrainer(end - start);
        trainer.addTrainingInstances(trainingInstances);
        trainer.trainModel();
        Evaluation eval = trainer.testModel();
        StringBuilder sb = new StringBuilder();
        sb.append("TEST ON RANGE ").append(start).append("-").append(end).append("\n");
        sb.append("Correct: ").append(eval.correct()).append(" (").append(eval.pctCorrect()).append("%)\n");
        sb.append("Incorrect: ").append(eval.incorrect()).append(" (").append(eval.pctIncorrect()).append("%)\n");
        System.out.println(sb.toString());
    }

    @Test
    public void testQualityOnFullRange() throws Exception {
        trainer = new IsolationForestTrainer(dataset);
        trainer.trainModel();
        Evaluation eval = trainer.testModel();
        StringBuilder sb = new StringBuilder();
        sb.append("TEST ON FULL RANGE\n");
        sb.append("Correct: ").append(eval.correct()).append(" (").append(eval.pctCorrect()).append("%)\n");
        sb.append("Incorrect: ").append(eval.incorrect()).append(" (").append(eval.pctIncorrect()).append("%)\n");
        System.out.println(sb.toString());
    }
}
